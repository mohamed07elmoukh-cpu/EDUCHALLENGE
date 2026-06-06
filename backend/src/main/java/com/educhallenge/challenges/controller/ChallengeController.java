package com.educhallenge.challenges.controller;

import com.educhallenge.backend.security.CurrentUser;
import com.educhallenge.challenges.dto.ChallengeCommentResponse;
import com.educhallenge.challenges.dto.ChallengeDetailsResponse;
import com.educhallenge.challenges.dto.ChallengeAttemptResultResponse;
import com.educhallenge.challenges.dto.ChallengeResponse;
import com.educhallenge.challenges.dto.ChallengeSocialSummaryResponse;
import com.educhallenge.challenges.dto.CreateChallengeCommentRequest;
import com.educhallenge.challenges.dto.CreateChallengeRequest;
import com.educhallenge.challenges.dto.MyChallengeResponse;
import com.educhallenge.challenges.dto.SubmitChallengeAttemptRequest;
import com.educhallenge.challenges.service.ChallengeService;
import com.educhallenge.users.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

	private final ChallengeService challengeService;

	public ChallengeController(ChallengeService challengeService) {
		this.challengeService = challengeService;
	}

	@PostMapping
	public ResponseEntity<ChallengeDetailsResponse> createChallenge(
			@Valid @RequestBody CreateChallengeRequest request,
			@CurrentUser User currentUser
	) {
		ChallengeDetailsResponse response = challengeService.createChallenge(request, currentUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<ChallengeResponse>> getAllChallenges(@CurrentUser User currentUser) {
		return ResponseEntity.ok(challengeService.getPublicChallenges(currentUser));
	}

	@GetMapping("/my-created")
	public ResponseEntity<List<MyChallengeResponse>> getMyCreatedChallenges(@CurrentUser User currentUser) {
		return ResponseEntity.ok(challengeService.getMyCreatedChallenges(currentUser));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ChallengeDetailsResponse> getChallengeById(
			@PathVariable Long id,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.getChallengeById(id, currentUser));
	}

	@PostMapping("/{id}/attempts")
	public ResponseEntity<ChallengeAttemptResultResponse> submitChallengeAttempt(
			@PathVariable Long id,
			@Valid @RequestBody SubmitChallengeAttemptRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.submitChallengeAttempt(id, request, currentUser));
	}

	@PostMapping("/{id}/likes/toggle")
	public ResponseEntity<ChallengeSocialSummaryResponse> toggleChallengeLike(
			@PathVariable Long id,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.toggleChallengeLike(id, currentUser));
	}

	@PostMapping("/{id}/saves/toggle")
	public ResponseEntity<ChallengeSocialSummaryResponse> toggleSavedChallenge(
			@PathVariable Long id,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.toggleSavedChallenge(id, currentUser));
	}

	@PostMapping("/{id}/shares")
	public ResponseEntity<ChallengeSocialSummaryResponse> registerChallengeShare(
			@PathVariable Long id,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.registerChallengeShare(id, currentUser));
	}

	@PostMapping("/{id}/comments")
	public ResponseEntity<ChallengeCommentResponse> addChallengeComment(
			@PathVariable Long id,
			@Valid @RequestBody CreateChallengeCommentRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.addChallengeComment(id, request, currentUser));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ChallengeDetailsResponse> updateChallenge(
			@PathVariable Long id,
			@Valid @RequestBody CreateChallengeRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(challengeService.updateChallenge(id, request, currentUser));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteChallenge(
			@PathVariable Long id,
			@CurrentUser User currentUser
	) {
		challengeService.deleteChallenge(id, currentUser);
		return ResponseEntity.noContent().build();
	}
}
