package com.educhallenge.challenges.controller;

import com.educhallenge.backend.security.CurrentUser;
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
import com.educhallenge.challenges.service.AdminChallengeService;
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
@RequestMapping("/api/admin/challenges")
public class AdminChallengeController {

	private final AdminChallengeService adminChallengeService;

	public AdminChallengeController(AdminChallengeService adminChallengeService) {
		this.adminChallengeService = adminChallengeService;
	}

	@PostMapping
	public ResponseEntity<AdminChallengeDetailsResponse> createChallenge(
			@Valid @RequestBody AdminChallengeCreateRequest request,
			@CurrentUser User currentUser
	) {
		AdminChallengeDetailsResponse response = adminChallengeService.createChallenge(request, currentUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<AdminChallengeListItemResponse>> listChallenges(@CurrentUser User currentUser) {
		return ResponseEntity.ok(adminChallengeService.listChallenges(currentUser));
	}

	@GetMapping("/{challengeId}")
	public ResponseEntity<AdminChallengeDetailsResponse> getChallengeDetails(
			@PathVariable Long challengeId,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.getChallengeDetails(challengeId, currentUser));
	}

	@GetMapping("/{challengeId}/stats")
	public ResponseEntity<AdminChallengeStatsResponse> getChallengeStats(
			@PathVariable Long challengeId,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.getChallengeStats(challengeId, currentUser));
	}

	@GetMapping("/{challengeId}/attempts")
	public ResponseEntity<List<AdminChallengeAttemptResponse>> getChallengeAttempts(
			@PathVariable Long challengeId,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.getChallengeAttempts(challengeId, currentUser));
	}

	@GetMapping("/{challengeId}/leaderboard")
	public ResponseEntity<List<AdminChallengeParticipantLeaderboardEntryResponse>> getChallengeLeaderboard(
			@PathVariable Long challengeId,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.getChallengeLeaderboard(challengeId, currentUser));
	}

	@PutMapping("/{challengeId}")
	public ResponseEntity<AdminChallengeDetailsResponse> updateChallenge(
			@PathVariable Long challengeId,
			@Valid @RequestBody AdminChallengeUpdateRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.updateChallenge(challengeId, request, currentUser));
	}

	@PostMapping("/{challengeId}/steps")
	public ResponseEntity<AdminChallengeStepResponse> addStep(
			@PathVariable Long challengeId,
			@Valid @RequestBody AdminChallengeStepCreateRequest request,
			@CurrentUser User currentUser
	) {
		AdminChallengeStepResponse response = adminChallengeService.addStep(challengeId, request, currentUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{challengeId}/steps/{stepId}")
	public ResponseEntity<AdminChallengeStepResponse> updateStep(
			@PathVariable Long challengeId,
			@PathVariable Long stepId,
			@Valid @RequestBody AdminChallengeStepUpdateRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.updateStep(challengeId, stepId, request, currentUser));
	}

	@PostMapping("/{challengeId}/steps/{stepId}/options")
	public ResponseEntity<AdminChallengeOptionResponse> addOption(
			@PathVariable Long challengeId,
			@PathVariable Long stepId,
			@Valid @RequestBody AdminChallengeOptionCreateRequest request,
			@CurrentUser User currentUser
	) {
		AdminChallengeOptionResponse response = adminChallengeService.addOption(challengeId, stepId, request, currentUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{challengeId}/steps/{stepId}/options/{optionId}")
	public ResponseEntity<AdminChallengeOptionResponse> updateOption(
			@PathVariable Long challengeId,
			@PathVariable Long stepId,
			@PathVariable Long optionId,
			@Valid @RequestBody AdminChallengeOptionUpdateRequest request,
			@CurrentUser User currentUser
	) {
		return ResponseEntity.ok(adminChallengeService.updateOption(challengeId, stepId, optionId, request, currentUser));
	}

	@DeleteMapping("/{challengeId}")
	public ResponseEntity<Void> deactivateChallenge(
			@PathVariable Long challengeId,
			@CurrentUser User currentUser
	) {
		adminChallengeService.deactivateChallenge(challengeId, currentUser);
		return ResponseEntity.noContent().build();
	}
}
