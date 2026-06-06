package com.educhallenge.challenges.service;

import com.educhallenge.challenges.dto.ChallengeDetailsResponse;
import com.educhallenge.challenges.dto.ChallengeCommentResponse;
import com.educhallenge.challenges.dto.ChallengeAttemptResultResponse;
import com.educhallenge.challenges.dto.ChallengeResponse;
import com.educhallenge.challenges.dto.ChallengeSocialSummaryResponse;
import com.educhallenge.challenges.dto.CreateChallengeCommentRequest;
import com.educhallenge.challenges.dto.CreateChallengeRequest;
import com.educhallenge.challenges.dto.MyChallengeResponse;
import com.educhallenge.challenges.dto.SubmitChallengeAttemptRequest;
import com.educhallenge.challenges.entity.Challenge;
import com.educhallenge.challenges.entity.ChallengeAttempt;
import com.educhallenge.challenges.entity.ChallengeStep;
import com.educhallenge.challenges.entity.AttemptAnswer;
import com.educhallenge.challenges.entity.ChallengeComment;
import com.educhallenge.challenges.entity.ChallengeLike;
import com.educhallenge.challenges.entity.ChallengeShare;
import com.educhallenge.challenges.entity.SavedChallenge;
import com.educhallenge.challenges.entity.StepOption;
import com.educhallenge.challenges.repository.ChallengeAttemptRepository;
import com.educhallenge.challenges.repository.ChallengeCommentRepository;
import com.educhallenge.challenges.repository.ChallengeLikeRepository;
import com.educhallenge.challenges.repository.ChallengeRepository;
import com.educhallenge.challenges.repository.ChallengeShareRepository;
import com.educhallenge.challenges.repository.SavedChallengeRepository;
import com.educhallenge.gamification.service.GamificationService;
import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ChallengeService {

	private static final List<String> ALLOWED_DIFFICULTIES = List.of("EASY", "MEDIUM", "HARD");
	private static final List<String> ALLOWED_VISIBILITIES = List.of("PUBLIC", "PRIVATE");

	private final ChallengeRepository challengeRepository;
	private final ChallengeAttemptRepository challengeAttemptRepository;
	private final ChallengeLikeRepository challengeLikeRepository;
	private final SavedChallengeRepository savedChallengeRepository;
	private final ChallengeShareRepository challengeShareRepository;
	private final ChallengeCommentRepository challengeCommentRepository;
	private final UserRepository userRepository;
	private final GamificationService gamificationService;

	public ChallengeService(
			ChallengeRepository challengeRepository,
			ChallengeAttemptRepository challengeAttemptRepository,
			ChallengeLikeRepository challengeLikeRepository,
			SavedChallengeRepository savedChallengeRepository,
			ChallengeShareRepository challengeShareRepository,
			ChallengeCommentRepository challengeCommentRepository,
			UserRepository userRepository,
			GamificationService gamificationService
	) {
		this.challengeRepository = challengeRepository;
		this.challengeAttemptRepository = challengeAttemptRepository;
		this.challengeLikeRepository = challengeLikeRepository;
		this.savedChallengeRepository = savedChallengeRepository;
		this.challengeShareRepository = challengeShareRepository;
		this.challengeCommentRepository = challengeCommentRepository;
		this.userRepository = userRepository;
		this.gamificationService = gamificationService;
	}

	public ChallengeDetailsResponse createChallenge(CreateChallengeRequest request, User currentUser) {
		validateQuestions(request.getQuestions());
		User managedCurrentUser = getManagedUser(currentUser);

		Challenge challenge = new Challenge();
		applyChallengeMetadata(challenge, request, managedCurrentUser, managedCurrentUser.getUsername(), true);
		challenge.setVisibility("PUBLIC");
		challenge.setSteps(buildSteps(request.getQuestions()));

		Challenge savedChallenge = challengeRepository.save(challenge);
		return mapToDetailsResponse(savedChallenge, true, managedCurrentUser);
	}

	@Transactional(readOnly = true)
	public List<ChallengeResponse> getPublicChallenges(User currentUser) {
		return challengeRepository.findVisibleChallengesForUser(currentUser.getId(), currentUser.getUsername())
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<MyChallengeResponse> getMyCreatedChallenges(User currentUser) {
		return challengeRepository.findMyCreatedChallenges(currentUser.getId(), currentUser.getUsername())
				.stream()
				.map(challenge -> new MyChallengeResponse(
						challenge.getId(),
						challenge.getTitle(),
						challenge.getDescription(),
						challenge.getCategory(),
						challenge.getDifficulty(),
						challenge.getPointsReward(),
						challenge.getQuestionsCount(),
						challenge.getParticipantsCount(),
						challenge.getAttemptsCount(),
						challenge.getStatus(),
						challenge.getCreatedAt()
				))
				.toList();
	}

	@Transactional(readOnly = true)
	public ChallengeDetailsResponse getChallengeById(Long id, User currentUser) {
		Challenge challenge = getDetailedChallenge(id);

		if (!canViewChallenge(currentUser, challenge)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this challenge");
		}

		return mapToDetailsResponse(challenge, canManageChallenge(currentUser, challenge), currentUser);
	}

	public ChallengeAttemptResultResponse submitChallengeAttempt(
			Long challengeId,
			SubmitChallengeAttemptRequest request,
			User currentUser
	) {
		User managedCurrentUser = getManagedUser(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);

		if (!canViewChallenge(managedCurrentUser, challenge)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to attempt this challenge");
		}

		if (challenge.getSteps() == null || challenge.getSteps().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This challenge does not contain any questions yet");
		}

		AttemptEvaluation evaluation = evaluateAttemptAnswers(request.getAnswers(), challenge.getSteps());
		boolean firstCompletion = !challengeAttemptRepository.existsByUser_IdAndChallenge_IdAndStatusIgnoreCase(
				managedCurrentUser.getId(),
				challengeId,
				"COMPLETED"
		);

		ChallengeAttempt attempt = new ChallengeAttempt();
		attempt.setUser(managedCurrentUser);
		attempt.setChallenge(challenge);
		attempt.setStatus("COMPLETED");
		attempt.setScore(evaluation.earnedScore());
		attempt.setCompletedAt(LocalDateTime.now());
		attempt.setAnswers(evaluation.answers());

		ChallengeAttempt savedAttempt = challengeAttemptRepository.save(attempt);
		GamificationService.ChallengeGamificationUpdate gamificationUpdate = gamificationService.applyChallengeCompletion(
				managedCurrentUser,
				challenge,
				savedAttempt,
				evaluation.totalQuestions(),
				evaluation.correctAnswers(),
				evaluation.earnedScore(),
				evaluation.maxScore(),
				firstCompletion
		);

		return new ChallengeAttemptResultResponse(
				savedAttempt.getId(),
				challenge.getId(),
				challenge.getTitle(),
				savedAttempt.getStatus(),
				firstCompletion,
				evaluation.totalQuestions(),
				evaluation.correctAnswers(),
				evaluation.earnedScore(),
				evaluation.maxScore(),
				gamificationUpdate.awardedPoints(),
				gamificationUpdate.totalPoints(),
				gamificationUpdate.level(),
				gamificationUpdate.currentStreak(),
				gamificationUpdate.currentRank(),
				savedAttempt.getCompletedAt(),
				gamificationUpdate.unlockedBadges(),
				gamificationUpdate.notifications(),
				evaluation.answerResults()
		);
	}

	public ChallengeSocialSummaryResponse toggleChallengeLike(Long challengeId, User currentUser) {
		User managedCurrentUser = getManagedUser(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);
		assertCanViewChallenge(managedCurrentUser, challenge);

		challengeLikeRepository.findByChallenge_IdAndUser_Id(challengeId, managedCurrentUser.getId())
				.ifPresentOrElse(
						challengeLikeRepository::delete,
						() -> {
							ChallengeLike challengeLike = new ChallengeLike();
							challengeLike.setChallenge(challenge);
							challengeLike.setUser(managedCurrentUser);
							challengeLikeRepository.save(challengeLike);
						}
				);

		return buildSocialSummary(challengeId, managedCurrentUser);
	}

	public ChallengeSocialSummaryResponse toggleSavedChallenge(Long challengeId, User currentUser) {
		User managedCurrentUser = getManagedUser(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);
		assertCanViewChallenge(managedCurrentUser, challenge);

		savedChallengeRepository.findByChallenge_IdAndUser_Id(challengeId, managedCurrentUser.getId())
				.ifPresentOrElse(
						savedChallengeRepository::delete,
						() -> {
							SavedChallenge savedChallenge = new SavedChallenge();
							savedChallenge.setChallenge(challenge);
							savedChallenge.setUser(managedCurrentUser);
							savedChallengeRepository.save(savedChallenge);
						}
				);

		return buildSocialSummary(challengeId, managedCurrentUser);
	}

	public ChallengeSocialSummaryResponse registerChallengeShare(Long challengeId, User currentUser) {
		User managedCurrentUser = getManagedUser(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);
		assertCanViewChallenge(managedCurrentUser, challenge);

		ChallengeShare challengeShare = new ChallengeShare();
		challengeShare.setChallenge(challenge);
		challengeShare.setUser(managedCurrentUser);
		challengeShareRepository.save(challengeShare);

		return buildSocialSummary(challengeId, managedCurrentUser);
	}

	public ChallengeCommentResponse addChallengeComment(
			Long challengeId,
			CreateChallengeCommentRequest request,
			User currentUser
	) {
		User managedCurrentUser = getManagedUser(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);
		assertCanViewChallenge(managedCurrentUser, challenge);

		if (!hasCompletedChallenge(managedCurrentUser.getId(), challengeId)) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN,
					"Complete the challenge before posting a comment"
			);
		}

		ChallengeComment comment = new ChallengeComment();
		comment.setChallenge(challenge);
		comment.setUser(managedCurrentUser);
		comment.setContent(request.getContent().trim());

		ChallengeComment savedComment = challengeCommentRepository.save(comment);
		return mapToCommentResponse(savedComment);
	}

	public ChallengeDetailsResponse updateChallenge(Long id, CreateChallengeRequest request, User currentUser) {
		validateQuestions(request.getQuestions());

		Challenge challenge = getDetailedChallenge(id);
		assertCanManageChallenge(currentUser, challenge);
		List<ChallengeStep> replacementSteps = buildSteps(request.getQuestions());
		challenge.clearSteps();
		challengeRepository.saveAndFlush(challenge);
		applyChallengeMetadata(
				challenge,
				request,
				challenge.getCreator(),
				challenge.getCreatorUsername(),
				false
		);
		challenge.setSteps(replacementSteps);

		Challenge updatedChallenge = challengeRepository.save(challenge);
		return mapToDetailsResponse(updatedChallenge, true, getManagedUser(currentUser));
	}

	public void deleteChallenge(Long id, User currentUser) {
		Challenge challenge = getDetailedChallenge(id);
		assertCanManageChallenge(currentUser, challenge);
		challengeRepository.delete(challenge);
	}

	private void applyChallengeMetadata(
			Challenge challenge,
			CreateChallengeRequest request,
			User creator,
			String creatorUsername,
			boolean defaultPublicVisibility
	) {
		challenge.setTitle(request.getTitle().trim());
		challenge.setDescription(request.getDescription().trim());
		challenge.setCategory(request.getCategory().trim());
		challenge.setDifficulty(normalizeDifficulty(request.getDifficulty()));
		challenge.setPointsReward(request.getPointsReward());
		challenge.setCreator(creator);
		challenge.setCreatorUsername(creator != null ? creator.getUsername() : creatorUsername);
		challenge.setVisibility(resolveVisibility(request.getVisibility(), challenge.getVisibility(), defaultPublicVisibility));
	}

	private List<ChallengeStep> buildSteps(List<CreateChallengeRequest.QuestionRequest> questions) {
		List<ChallengeStep> steps = new ArrayList<>();
		int order = 1;

		for (CreateChallengeRequest.QuestionRequest questionRequest : questions) {
			ChallengeStep step = new ChallengeStep();
			step.setQuestionText(questionRequest.getQuestionText().trim());
			step.setStepText(questionRequest.getQuestionText().trim());
			step.setStepOrder(order++);
			step.setPoints(questionRequest.getPoints() == null ? 1 : questionRequest.getPoints());

			for (CreateChallengeRequest.OptionRequest optionRequest : questionRequest.getOptions()) {
				StepOption option = new StepOption();
				option.setOptionText(optionRequest.getOptionText().trim());
				option.setIsCorrect(optionRequest.getIsCorrect());
				step.addOption(option);
			}

			steps.add(step);
		}

		return steps;
	}

	private void validateQuestions(List<CreateChallengeRequest.QuestionRequest> questions) {
		if (questions == null || questions.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge must contain at least one question");
		}

		for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
			CreateChallengeRequest.QuestionRequest question = questions.get(questionIndex);
			List<CreateChallengeRequest.OptionRequest> options = question.getOptions();

			if (options == null || options.size() < 2) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"Question " + (questionIndex + 1) + " must contain at least 2 options"
				);
			}

			long correctOptions = options.stream()
					.filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
					.count();

			if (correctOptions != 1) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"Question " + (questionIndex + 1) + " must contain exactly 1 correct answer"
				);
			}
		}
	}

	private String normalizeDifficulty(String difficulty) {
		String normalizedDifficulty = difficulty.trim().toUpperCase(Locale.ROOT);

		if (!ALLOWED_DIFFICULTIES.contains(normalizedDifficulty)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Difficulty must be one of: EASY, MEDIUM, HARD"
			);
		}

		return normalizedDifficulty;
	}

	private String resolveVisibility(String visibility, String fallbackVisibility, boolean defaultPublicVisibility) {
		String candidateVisibility = visibility;

		if (candidateVisibility == null || candidateVisibility.isBlank()) {
			if (fallbackVisibility != null && !fallbackVisibility.isBlank()) {
				candidateVisibility = fallbackVisibility;
			} else if (defaultPublicVisibility) {
				candidateVisibility = "PUBLIC";
			}
		}

		if (candidateVisibility == null || candidateVisibility.isBlank()) {
			candidateVisibility = "PUBLIC";
		}

		String normalizedVisibility = candidateVisibility.trim().toUpperCase(Locale.ROOT);

		if (!ALLOWED_VISIBILITIES.contains(normalizedVisibility)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Visibility must be one of: PUBLIC, PRIVATE"
			);
		}

		return normalizedVisibility;
	}

	private Challenge getDetailedChallenge(Long id) {
		return challengeRepository.findDetailedById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
	}

	private AttemptEvaluation evaluateAttemptAnswers(
			List<SubmitChallengeAttemptRequest.AnswerRequest> answers,
			List<ChallengeStep> steps
	) {
		if (answers == null || answers.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please answer all questions before submitting");
		}

		Map<Long, ChallengeStep> stepsById = new HashMap<>();
		int maxScore = 0;

		for (ChallengeStep step : steps) {
			stepsById.put(step.getId(), step);
			maxScore += safeStepPoints(step.getPoints());
		}

		if (answers.size() != stepsById.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please answer every question before submitting");
		}

		Set<Long> answeredStepIds = new HashSet<>();
		List<AttemptAnswer> attemptAnswers = new ArrayList<>();
		List<ChallengeAttemptResultResponse.AnswerResultResponse> answerResults = new ArrayList<>();
		int correctAnswers = 0;
		int earnedScore = 0;

		for (SubmitChallengeAttemptRequest.AnswerRequest answerRequest : answers) {
			Long stepId = answerRequest.getStepId();

			if (!answeredStepIds.add(stepId)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each question can only be answered once");
			}

			ChallengeStep step = stepsById.get(stepId);

			if (step == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One of the submitted questions does not belong to this challenge");
			}

			StepOption selectedOption = findSelectedOption(step, answerRequest.getSelectedOptionId());
			List<StepOption> correctOptions = findCorrectOptions(step);
			boolean isCorrect = Boolean.TRUE.equals(selectedOption.getIsCorrect());
			int stepPoints = safeStepPoints(step.getPoints());

			if (isCorrect) {
				correctAnswers++;
				earnedScore += stepPoints;
			}

			AttemptAnswer attemptAnswer = new AttemptAnswer();
			attemptAnswer.setStep(step);
			attemptAnswer.setSelectedOption(selectedOption);
			attemptAnswer.setIsCorrect(isCorrect);
			attemptAnswers.add(attemptAnswer);

			answerResults.add(new ChallengeAttemptResultResponse.AnswerResultResponse(
					step.getId(),
					step.getStepOrder(),
					step.getQuestionText(),
					selectedOption.getId(),
					resolveReferenceCorrectOptionId(selectedOption, correctOptions),
					isCorrect,
					stepPoints
			));
		}

		if (answeredStepIds.size() != stepsById.size()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please answer every question before submitting");
		}

		answerResults.sort(Comparator.comparing(
				ChallengeAttemptResultResponse.AnswerResultResponse::getStepOrder,
				Comparator.nullsLast(Integer::compareTo)
		));

		return new AttemptEvaluation(
				attemptAnswers,
				answerResults,
				stepsById.size(),
				correctAnswers,
				earnedScore,
				maxScore
		);
	}

	private StepOption findSelectedOption(ChallengeStep step, Long selectedOptionId) {
		return step.getOptions()
				.stream()
				.filter(option -> option.getId().equals(selectedOptionId))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"One of the selected answers does not belong to the requested question"
				));
	}

	private List<StepOption> findCorrectOptions(ChallengeStep step) {
		List<StepOption> correctOptions = step.getOptions()
				.stream()
				.filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
				.toList();

		if (correctOptions.isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Challenge data is invalid because one question has no correct answer configured"
			);
		}

		return correctOptions;
	}

	private Long resolveReferenceCorrectOptionId(StepOption selectedOption, List<StepOption> correctOptions) {
		if (Boolean.TRUE.equals(selectedOption.getIsCorrect())) {
			return selectedOption.getId();
		}

		return correctOptions.getFirst().getId();
	}

	private int safeStepPoints(Integer stepPoints) {
		return stepPoints == null || stepPoints < 1 ? 1 : stepPoints;
	}

	private User getManagedUser(User currentUser) {
		if (currentUser.getId() != null) {
			return userRepository.findById(currentUser.getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
		}

		if (currentUser.getEmail() != null) {
			return userRepository.findByEmailIgnoreCase(currentUser.getEmail())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
		}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found");
	}

	private boolean canViewChallenge(User currentUser, Challenge challenge) {
		return isPubliclyVisible(challenge) || canManageChallenge(currentUser, challenge);
	}

	private void assertCanViewChallenge(User currentUser, Challenge challenge) {
		if (!canViewChallenge(currentUser, challenge)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this challenge");
		}
	}

	private boolean isPubliclyVisible(Challenge challenge) {
		return Boolean.TRUE.equals(challenge.getIsActive())
				&& "PUBLIC".equalsIgnoreCase(challenge.getVisibility());
	}

	private boolean canManageChallenge(User currentUser, Challenge challenge) {
		if (isAdmin(currentUser)) {
			return true;
		}

		if (challenge.getCreator() != null && challenge.getCreator().getId() != null) {
			return challenge.getCreator().getId().equals(currentUser.getId());
		}

		return challenge.getCreatorUsername() != null
				&& challenge.getCreatorUsername().equalsIgnoreCase(currentUser.getUsername());
	}

	private void assertCanManageChallenge(User currentUser, Challenge challenge) {
		if (!canManageChallenge(currentUser, challenge)) {
			throw new ResponseStatusException(
					HttpStatus.FORBIDDEN,
					"Only the creator of the challenge or an admin can modify or delete it"
			);
		}
	}

	private boolean isAdmin(User user) {
		return user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().trim());
	}

	private ChallengeResponse mapToResponse(Challenge challenge) {
		return new ChallengeResponse(
				challenge.getId(),
				challenge.getTitle(),
				challenge.getDescription(),
				challenge.getCategory(),
				challenge.getDifficulty(),
				challenge.getPointsReward(),
				challenge.getCreatedAt(),
				resolveCreatorUsername(challenge),
				challenge.getVisibility(),
				challenge.getIsActive()
		);
	}

	private ChallengeDetailsResponse mapToDetailsResponse(Challenge challenge, boolean includeCorrectAnswers, User currentUser) {
		List<ChallengeDetailsResponse.QuestionResponse> questions = challenge.getSteps()
				.stream()
				.map(step -> new ChallengeDetailsResponse.QuestionResponse(
						step.getId(),
						step.getQuestionText(),
						step.getStepOrder(),
						step.getPoints(),
						step.getOptions()
								.stream()
								.map(option -> new ChallengeDetailsResponse.OptionResponse(
										option.getId(),
										option.getOptionText(),
										includeCorrectAnswers ? option.getIsCorrect() : null
								))
								.toList()
				))
				.toList();

		List<ChallengeCommentResponse> comments = challengeCommentRepository.findByChallenge_IdOrderByCreatedAtDesc(challenge.getId())
				.stream()
				.map(this::mapToCommentResponse)
				.toList();

		ChallengeSocialSummaryResponse social = buildSocialSummary(challenge.getId(), currentUser);

		return new ChallengeDetailsResponse(
				challenge.getId(),
				challenge.getTitle(),
				challenge.getDescription(),
				challenge.getCategory(),
				challenge.getDifficulty(),
				challenge.getPointsReward(),
				challenge.getCreator() != null ? challenge.getCreator().getId() : null,
				resolveCreatorUsername(challenge),
				challenge.getVisibility(),
				challenge.getIsActive(),
				challenge.getCreatedAt(),
				social,
				comments,
				questions
		);
	}

	private ChallengeSocialSummaryResponse buildSocialSummary(Long challengeId, User currentUser) {
		Long currentUserId = currentUser != null ? currentUser.getId() : null;
		boolean completedByCurrentUser = currentUserId != null && hasCompletedChallenge(currentUserId, challengeId);

		return new ChallengeSocialSummaryResponse(
				challengeLikeRepository.countByChallenge_Id(challengeId),
				savedChallengeRepository.countByChallenge_Id(challengeId),
				challengeShareRepository.countByChallenge_Id(challengeId),
				challengeCommentRepository.countByChallenge_Id(challengeId),
				currentUserId != null && challengeLikeRepository.existsByChallenge_IdAndUser_Id(challengeId, currentUserId),
				currentUserId != null && savedChallengeRepository.existsByChallenge_IdAndUser_Id(challengeId, currentUserId),
				completedByCurrentUser,
				completedByCurrentUser
		);
	}

	private boolean hasCompletedChallenge(Long userId, Long challengeId) {
		return challengeAttemptRepository.existsByUser_IdAndChallenge_IdAndStatusIgnoreCase(userId, challengeId, "COMPLETED");
	}

	private ChallengeCommentResponse mapToCommentResponse(ChallengeComment comment) {
		return new ChallengeCommentResponse(
				comment.getId(),
				comment.getUser() != null ? comment.getUser().getId() : null,
				comment.getUser() != null ? comment.getUser().getUsername() : null,
				comment.getUser() != null ? comment.getUser().getEmail() : null,
				comment.getContent(),
				comment.getCreatedAt(),
				comment.getUpdatedAt()
		);
	}

	private String resolveCreatorUsername(Challenge challenge) {
		if (challenge.getCreator() != null && challenge.getCreator().getUsername() != null) {
			return challenge.getCreator().getUsername();
		}

		return challenge.getCreatorUsername();
	}

	private record AttemptEvaluation(
			List<AttemptAnswer> answers,
			List<ChallengeAttemptResultResponse.AnswerResultResponse> answerResults,
			int totalQuestions,
			int correctAnswers,
			int earnedScore,
			int maxScore
	) {
	}
}
