package com.educhallenge.gamification.controller;

import com.educhallenge.backend.security.CurrentUser;
import com.educhallenge.gamification.dto.BadgeResponse;
import com.educhallenge.gamification.dto.GamificationMeResponse;
import com.educhallenge.gamification.dto.LeaderboardResponse;
import com.educhallenge.gamification.service.GamificationService;
import com.educhallenge.users.entity.User;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GamificationController {

	private final GamificationService gamificationService;

	public GamificationController(GamificationService gamificationService) {
		this.gamificationService = gamificationService;
	}

	@GetMapping("/gamification/me")
	public ResponseEntity<GamificationMeResponse> getMyGamification(@CurrentUser User currentUser) {
		return ResponseEntity.ok(gamificationService.getMyGamification(currentUser));
	}

	@GetMapping("/leaderboard")
	public ResponseEntity<LeaderboardResponse> getLeaderboard(@CurrentUser User currentUser) {
		return ResponseEntity.ok(gamificationService.getLeaderboard(currentUser));
	}

	@GetMapping("/badges")
	public ResponseEntity<List<BadgeResponse>> getBadges(@CurrentUser User currentUser) {
		return ResponseEntity.ok(gamificationService.getAvailableBadges(currentUser));
	}

	@GetMapping("/users/me/badges")
	public ResponseEntity<List<BadgeResponse>> getMyBadges(@CurrentUser User currentUser) {
		return ResponseEntity.ok(gamificationService.getMyEarnedBadges(currentUser));
	}
}
