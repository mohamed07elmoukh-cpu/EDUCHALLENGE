package com.educhallenge.challenges.service;

import com.educhallenge.backend.exception.BusinessRuleViolationException;
import com.educhallenge.backend.exception.ResourceNotFoundException;
import com.educhallenge.backend.security.AdminAuthorizationService;
import com.educhallenge.challenges.dto.admin.AdminChallengeAttemptResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeCreateRequest;
import com.educhallenge.challenges.dto.admin.AdminChallengeDetailsResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeListItemResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeOptionCreateRequest;
import com.educhallenge.challenges.dto.admin.AdminChallengeOptionResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeOptionUpdateRequest;
import com.educhallenge.challenges.dto.admin.AdminChallengeParticipantLeaderboardEntryResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeStatsResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeStepCreateRequest;
import com.educhallenge.challenges.dto.admin.AdminChallengeStepResponse;
import com.educhallenge.challenges.dto.admin.AdminChallengeStepUpdateRequest;
import com.educhallenge.challenges.dto.admin.AdminChallengeUpdateRequest;
import com.educhallenge.challenges.entity.Challenge;
import com.educhallenge.challenges.entity.ChallengeStep;
import com.educhallenge.challenges.entity.StepOption;
import com.educhallenge.challenges.repository.ChallengeAttemptRepository;
import com.educhallenge.challenges.repository.ChallengeRepository;
import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminChallengeService {

	private static final List<String> ALLOWED_DIFFICULTIES = List.of("EASY", "MEDIUM", "HARD");
	private static final List<String> ALLOWED_VISIBILITIES = List.of("PUBLIC", "PRIVATE");

	private final ChallengeRepository challengeRepository;
	private final ChallengeAttemptRepository challengeAttemptRepository;
	private final UserRepository userRepository;
	private final AdminAuthorizationService adminAuthorizationService;

	public AdminChallengeService(
			ChallengeRepository challengeRepository,
			ChallengeAttemptRepository challengeAttemptRepository,
			UserRepository userRepository,
			AdminAuthorizationService adminAuthorizationService
	) {
		this.challengeRepository = challengeRepository;
		this.challengeAttemptRepository = challengeAttemptRepository;
		this.userRepository = userRepository;
		this.adminAuthorizationService = adminAuthorizationService;
	}

	public AdminChallengeDetailsResponse createChallenge(AdminChallengeCreateRequest request, User currentUser) {
		User managedAdmin = getManagedAdmin(currentUser);

		Challenge challenge = new Challenge();
		challenge.setTitle(request.getTitle().trim());
		challenge.setDescription(request.getDescription().trim());
		challenge.setCategory(request.getCategory().trim());
		challenge.setDifficulty(normalizeDifficulty(request.getDifficulty()));
		challenge.setPointsReward(request.getPointsReward());
		challenge.setVisibility(resolveVisibility(request.getVisibility(), null));
		challenge.setIsActive(resolveActive(request.getIsActive(), true));
		challenge.setCreator(managedAdmin);
		challenge.setCreatorUsername(managedAdmin.getUsername());

		Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);
		return mapToDetailsResponse(getDetailedChallenge(savedChallenge.getId()));
	}

	@Transactional(readOnly = true)
	public List<AdminChallengeListItemResponse> listChallenges(User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);

		return challengeRepository.findAllForAdminManagement()
				.stream()
				.map(projection -> new AdminChallengeListItemResponse(
						projection.getId(),
						projection.getTitle(),
						projection.getCategory(),
						projection.getDifficulty(),
						projection.getPointsReward(),
						projection.getCreatorId(),
						projection.getCreatorUsername(),
						projection.getVisibility(),
						projection.getIsActive(),
						projection.getCreatedAt(),
						nullSafeLong(projection.getStepCount()),
						nullSafeLong(projection.getTotalAttempts()),
						nullSafeLong(projection.getUniqueParticipants()),
						normalizeAverageScore(projection.getAverageScore()),
						nullSafeLong(projection.getCompletionCount())
				))
				.toList();
	}

	@Transactional(readOnly = true)
	public AdminChallengeDetailsResponse getChallengeDetails(Long challengeId, User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);
		return mapToDetailsResponse(getDetailedChallenge(challengeId));
	}

	@Transactional(readOnly = true)
	public AdminChallengeStatsResponse getChallengeStats(Long challengeId, User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);
		assertChallengeExists(challengeId);
		return mapToStatsResponse(challengeId);
	}

	@Transactional(readOnly = true)
	public List<AdminChallengeAttemptResponse> getChallengeAttempts(Long challengeId, User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);
		assertChallengeExists(challengeId);

		return challengeAttemptRepository.findAdminAttemptsByChallengeId(challengeId)
				.stream()
				.map(projection -> new AdminChallengeAttemptResponse(
						projection.getAttemptId(),
						projection.getChallengeId(),
						projection.getParticipantId(),
						projection.getParticipantUsername(),
						projection.getParticipantEmail(),
						projection.getStatus(),
						projection.getScore(),
						projection.getStartedAt(),
						projection.getCompletedAt(),
						projection.getDurationSeconds()
				))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<AdminChallengeParticipantLeaderboardEntryResponse> getChallengeLeaderboard(Long challengeId, User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);
		assertChallengeExists(challengeId);

		return challengeAttemptRepository.findAdminLeaderboardByChallengeId(challengeId)
				.stream()
				.map(projection -> new AdminChallengeParticipantLeaderboardEntryResponse(
						projection.getRank(),
						projection.getParticipantId(),
						projection.getParticipantUsername(),
						projection.getParticipantEmail(),
						projection.getBestScore(),
						nullSafeLong(projection.getCompletedAttempts()),
						projection.getFastestCompletionSeconds(),
						projection.getLastCompletedAt()
				))
				.toList();
	}

	public AdminChallengeDetailsResponse updateChallenge(
			Long challengeId,
			AdminChallengeUpdateRequest request,
			User currentUser
	) {
		adminAuthorizationService.assertAdmin(currentUser);

		Challenge challenge = getDetailedChallenge(challengeId);
		challenge.setTitle(request.getTitle().trim());
		challenge.setDescription(request.getDescription().trim());
		challenge.setCategory(request.getCategory().trim());
		challenge.setDifficulty(normalizeDifficulty(request.getDifficulty()));
		challenge.setPointsReward(request.getPointsReward());
		challenge.setVisibility(resolveVisibility(request.getVisibility(), challenge.getVisibility()));
		challenge.setIsActive(resolveActive(request.getIsActive(), Boolean.TRUE.equals(challenge.getIsActive())));

		Challenge updatedChallenge = challengeRepository.saveAndFlush(challenge);
		return mapToDetailsResponse(updatedChallenge);
	}

	public AdminChallengeStepResponse addStep(
			Long challengeId,
			AdminChallengeStepCreateRequest request,
			User currentUser
	) {
		adminAuthorizationService.assertAdmin(currentUser);
		validateCreateStepRequest(request);

		Challenge challenge = getDetailedChallenge(challengeId);
		ChallengeStep newStep = new ChallengeStep();
		newStep.setQuestionText(request.getQuestionText().trim());
		newStep.setStepText(request.getQuestionText().trim());
		newStep.setPoints(request.getPoints());

		for (AdminChallengeOptionCreateRequest optionRequest : request.getOptions()) {
			StepOption option = new StepOption();
			option.setOptionText(optionRequest.getOptionText().trim());
			option.setIsCorrect(optionRequest.getIsCorrect());
			newStep.addOption(option);
		}

		challenge.addStep(newStep);
		reorderSteps(challenge, newStep, request.getStepOrder());

		Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);
		ChallengeStep savedStep = findStep(savedChallenge, newStep.getId());
		return mapStepResponse(savedChallenge, savedStep);
	}

	public AdminChallengeStepResponse updateStep(
			Long challengeId,
			Long stepId,
			AdminChallengeStepUpdateRequest request,
			User currentUser
	) {
		adminAuthorizationService.assertAdmin(currentUser);
		Challenge challenge = getDetailedChallenge(challengeId);
		ChallengeStep step = findStep(challenge, stepId);

		step.setQuestionText(request.getQuestionText().trim());
		step.setStepText(request.getQuestionText().trim());
		step.setPoints(request.getPoints());
		reorderSteps(challenge, step, request.getStepOrder());

		Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);
		return mapStepResponse(savedChallenge, findStep(savedChallenge, stepId));
	}

	public AdminChallengeOptionResponse addOption(
			Long challengeId,
			Long stepId,
			AdminChallengeOptionCreateRequest request,
			User currentUser
	) {
		adminAuthorizationService.assertAdmin(currentUser);

		Challenge challenge = getDetailedChallenge(challengeId);
		ChallengeStep step = findStep(challenge, stepId);
		validateOptionTextUniqueness(step, request.getOptionText(), null);

		StepOption option = new StepOption();
		option.setOptionText(request.getOptionText().trim());
		option.setIsCorrect(request.getIsCorrect());
		step.addOption(option);
		validateAtLeastOneCorrectOption(step);

		Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);
		ChallengeStep savedStep = findStep(savedChallenge, stepId);
		StepOption savedOption = savedStep.getOptions()
				.stream()
				.filter(candidate -> Objects.equals(candidate.getId(), option.getId()))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Option not found after creation"));

		return mapOptionResponse(savedStep, savedOption);
	}

	public AdminChallengeOptionResponse updateOption(
			Long challengeId,
			Long stepId,
			Long optionId,
			AdminChallengeOptionUpdateRequest request,
			User currentUser
	) {
		adminAuthorizationService.assertAdmin(currentUser);

		Challenge challenge = getDetailedChallenge(challengeId);
		ChallengeStep step = findStep(challenge, stepId);
		StepOption option = findOption(step, optionId);

		validateOptionTextUniqueness(step, request.getOptionText(), optionId);
		option.setOptionText(request.getOptionText().trim());
		option.setIsCorrect(request.getIsCorrect());
		validateAtLeastOneCorrectOption(step);

		Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);
		ChallengeStep savedStep = findStep(savedChallenge, stepId);
		return mapOptionResponse(savedStep, findOption(savedStep, optionId));
	}

	public void deactivateChallenge(Long challengeId, User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);

		Challenge challenge = getDetailedChallenge(challengeId);
		challenge.setIsActive(false);
		challengeRepository.saveAndFlush(challenge);
	}

	private User getManagedAdmin(User currentUser) {
		adminAuthorizationService.assertAdmin(currentUser);

		if (currentUser.getId() != null) {
			return userRepository.findById(currentUser.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Authenticated admin user not found"));
		}

		if (currentUser.getEmail() != null) {
			return userRepository.findByEmailIgnoreCase(currentUser.getEmail())
					.orElseThrow(() -> new ResourceNotFoundException("Authenticated admin user not found"));
		}

		throw new ResourceNotFoundException("Authenticated admin user not found");
	}

	private void validateCreateStepRequest(AdminChallengeStepCreateRequest request) {
		validateOptionRequests(request.getOptions());
	}

	private void validateOptionRequests(List<AdminChallengeOptionCreateRequest> options) {
		if (options == null || options.size() < 2) {
			throw new BusinessRuleViolationException("Each step must contain at least 2 options");
		}

		long correctOptions = options.stream()
				.filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
				.count();

		if (correctOptions < 1) {
			throw new BusinessRuleViolationException("Each step must contain at least 1 correct option");
		}

		List<String> normalizedTexts = options.stream()
				.map(AdminChallengeOptionCreateRequest::getOptionText)
				.map(this::normalizeText)
				.toList();

		if (normalizedTexts.stream().distinct().count() != normalizedTexts.size()) {
			throw new BusinessRuleViolationException("Option texts must be unique within the same step");
		}
	}

	private void validateAtLeastOneCorrectOption(ChallengeStep step) {
		boolean hasCorrectOption = step.getOptions()
				.stream()
				.anyMatch(option -> Boolean.TRUE.equals(option.getIsCorrect()));

		if (!hasCorrectOption) {
			throw new BusinessRuleViolationException("A QCM step must contain at least 1 correct option");
		}
	}

	private void validateOptionTextUniqueness(ChallengeStep step, String optionText, Long excludedOptionId) {
		String normalizedCandidate = normalizeText(optionText);
		boolean alreadyExists = step.getOptions()
				.stream()
				.filter(option -> excludedOptionId == null || !Objects.equals(option.getId(), excludedOptionId))
				.map(StepOption::getOptionText)
				.map(this::normalizeText)
				.anyMatch(normalizedCandidate::equals);

		if (alreadyExists) {
			throw new BusinessRuleViolationException("Option texts must be unique within the same step");
		}
	}

	private void reorderSteps(Challenge challenge, ChallengeStep targetStep, Integer requestedOrder) {
		List<ChallengeStep> orderedSteps = challenge.getSteps()
				.stream()
				.filter(step -> !sameStep(step, targetStep))
				.sorted(Comparator.comparing(this::safeStepOrder))
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

		int normalizedOrder = normalizeStepOrder(requestedOrder, orderedSteps.size() + 1);
		orderedSteps.add(normalizedOrder - 1, targetStep);

		int temporaryOrderBase = 1000 + orderedSteps.size();
		for (int index = 0; index < orderedSteps.size(); index++) {
			orderedSteps.get(index).setStepOrder(temporaryOrderBase + index);
		}

		challengeRepository.saveAndFlush(challenge);

		for (int index = 0; index < orderedSteps.size(); index++) {
			orderedSteps.get(index).setStepOrder(index + 1);
		}
	}

	private int normalizeStepOrder(Integer requestedOrder, int maxOrder) {
		if (requestedOrder == null || requestedOrder < 1) {
			return 1;
		}

		return Math.min(requestedOrder, maxOrder);
	}

	private int safeStepOrder(ChallengeStep step) {
		return step.getStepOrder() == null || step.getStepOrder() < 1 ? Integer.MAX_VALUE : step.getStepOrder();
	}

	private boolean sameStep(ChallengeStep left, ChallengeStep right) {
		if (left == right) {
			return true;
		}

		return left != null
				&& right != null
				&& left.getId() != null
				&& right.getId() != null
				&& left.getId().equals(right.getId());
	}

	private Challenge getDetailedChallenge(Long challengeId) {
		return challengeRepository.findDetailedById(challengeId)
				.orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
	}

	private void assertChallengeExists(Long challengeId) {
		if (!challengeRepository.existsById(challengeId)) {
			throw new ResourceNotFoundException("Challenge not found");
		}
	}

	private ChallengeStep findStep(Challenge challenge, Long stepId) {
		return challenge.getSteps()
				.stream()
				.filter(step -> Objects.equals(step.getId(), stepId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Challenge step not found"));
	}

	private StepOption findOption(ChallengeStep step, Long optionId) {
		return step.getOptions()
				.stream()
				.filter(option -> Objects.equals(option.getId(), optionId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Step option not found"));
	}

	private String normalizeDifficulty(String difficulty) {
		String normalizedDifficulty = normalizeText(difficulty).toUpperCase(Locale.ROOT);

		if (!ALLOWED_DIFFICULTIES.contains(normalizedDifficulty)) {
			throw new BusinessRuleViolationException("Difficulty must be one of: EASY, MEDIUM, HARD");
		}

		return normalizedDifficulty;
	}

	private String resolveVisibility(String visibility, String fallbackVisibility) {
		String candidateVisibility = visibility;

		if (candidateVisibility == null || candidateVisibility.isBlank()) {
			candidateVisibility = fallbackVisibility;
		}

		if (candidateVisibility == null || candidateVisibility.isBlank()) {
			candidateVisibility = "PUBLIC";
		}

		String normalizedVisibility = candidateVisibility.trim().toUpperCase(Locale.ROOT);

		if (!ALLOWED_VISIBILITIES.contains(normalizedVisibility)) {
			throw new BusinessRuleViolationException("Visibility must be one of: PUBLIC, PRIVATE");
		}

		return normalizedVisibility;
	}

	private boolean resolveActive(Boolean requestedIsActive, boolean fallbackValue) {
		return requestedIsActive != null ? requestedIsActive : fallbackValue;
	}

	private String normalizeText(String value) {
		return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
	}

	private AdminChallengeDetailsResponse mapToDetailsResponse(Challenge challenge) {
		List<AdminChallengeStepResponse> steps = challenge.getSteps()
				.stream()
				.sorted(Comparator.comparing(this::safeStepOrder))
				.map(step -> mapStepResponse(challenge, step))
				.toList();

		return new AdminChallengeDetailsResponse(
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
				steps.size(),
				mapToStatsResponse(challenge.getId()),
				steps
		);
	}

	private AdminChallengeStepResponse mapStepResponse(Challenge challenge, ChallengeStep step) {
		List<AdminChallengeOptionResponse> options = step.getOptions()
				.stream()
				.sorted(Comparator.comparing(StepOption::getId, Comparator.nullsLast(Long::compareTo)))
				.map(option -> mapOptionResponse(step, option))
				.toList();

		return new AdminChallengeStepResponse(
				step.getId(),
				challenge.getId(),
				step.getQuestionText(),
				step.getStepOrder(),
				step.getPoints(),
				options.size(),
				options
		);
	}

	private AdminChallengeOptionResponse mapOptionResponse(ChallengeStep step, StepOption option) {
		return new AdminChallengeOptionResponse(
				option.getId(),
				step.getId(),
				option.getOptionText(),
				option.getIsCorrect()
		);
	}

	private AdminChallengeStatsResponse mapToStatsResponse(Long challengeId) {
		ChallengeRepository.AdminChallengeStatsProjection projection = challengeRepository.findAdminStatsByChallengeId(challengeId)
				.orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

		return new AdminChallengeStatsResponse(
				projection.getChallengeId(),
				nullSafeLong(projection.getTotalAttempts()),
				nullSafeLong(projection.getUniqueParticipants()),
				normalizeAverageScore(projection.getAverageScore()),
				nullSafeLong(projection.getCompletionCount())
		);
	}

	private BigDecimal normalizeAverageScore(BigDecimal averageScore) {
		return averageScore == null ? BigDecimal.ZERO : averageScore.stripTrailingZeros();
	}

	private long nullSafeLong(Long value) {
		return value == null ? 0L : value;
	}

	private String resolveCreatorUsername(Challenge challenge) {
		if (challenge.getCreator() != null && challenge.getCreator().getUsername() != null) {
			return challenge.getCreator().getUsername();
		}

		return challenge.getCreatorUsername();
	}
}
