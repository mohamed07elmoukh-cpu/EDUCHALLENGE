package com.educhallenge.gamification.dto;

import java.util.List;

public class LeaderboardResponse {

	private List<LeaderboardEntryResponse> entries;
	private Integer currentUserRank;

	public LeaderboardResponse() {
	}

	public LeaderboardResponse(List<LeaderboardEntryResponse> entries, Integer currentUserRank) {
		this.entries = entries;
		this.currentUserRank = currentUserRank;
	}

	public List<LeaderboardEntryResponse> getEntries() {
		return entries;
	}

	public void setEntries(List<LeaderboardEntryResponse> entries) {
		this.entries = entries;
	}

	public Integer getCurrentUserRank() {
		return currentUserRank;
	}

	public void setCurrentUserRank(Integer currentUserRank) {
		this.currentUserRank = currentUserRank;
	}
}
