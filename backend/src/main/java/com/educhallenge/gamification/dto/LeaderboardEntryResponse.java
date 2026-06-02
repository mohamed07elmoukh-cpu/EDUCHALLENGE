package com.educhallenge.gamification.dto;

public class LeaderboardEntryResponse {

	private Long id;
	private Integer rank;
	private String username;
	private Integer points;
	private String level;
	private String badge;

	public LeaderboardEntryResponse() {
	}

	public LeaderboardEntryResponse(Long id, Integer rank, String username, Integer points, String level, String badge) {
		this.id = id;
		this.rank = rank;
		this.username = username;
		this.points = points;
		this.level = level;
		this.badge = badge;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}
}
