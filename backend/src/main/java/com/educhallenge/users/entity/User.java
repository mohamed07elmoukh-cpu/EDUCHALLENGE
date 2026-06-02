package com.educhallenge.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.educhallenge.challenges.entity.Challenge;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
		name = "users",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_users_username", columnNames = "username"),
				@UniqueConstraint(name = "uk_users_email", columnNames = "email")
		}
)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'USER'")
	private String role;

	@Column(name = "points_total", nullable = false, columnDefinition = "integer default 0")
	private Integer pointsTotal;

	@Column(nullable = false, columnDefinition = "integer default 1")
	private Integer level;

	@Column(name = "streak_count", nullable = false, columnDefinition = "integer default 0")
	private Integer streakCount;

	@Column(name = "last_activity_date")
	private LocalDate lastActivityDate;

	@OneToMany(mappedBy = "creator")
	private List<Challenge> createdChallenges = new ArrayList<>();

	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getPointsTotal() {
		return pointsTotal;
	}

	public void setPointsTotal(Integer pointsTotal) {
		this.pointsTotal = pointsTotal;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getStreakCount() {
		return streakCount;
	}

	public void setStreakCount(Integer streakCount) {
		this.streakCount = streakCount;
	}

	public LocalDate getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(LocalDate lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public List<Challenge> getCreatedChallenges() {
		return createdChallenges;
	}

	public void setCreatedChallenges(List<Challenge> createdChallenges) {
		this.createdChallenges = createdChallenges;
	}

	@PrePersist
	public void applyDefaults() {
		if (role == null || role.isBlank()) {
			role = "USER";
		}
		if (pointsTotal == null) {
			pointsTotal = 0;
		}
		if (level == null) {
			level = 1;
		}
		if (streakCount == null) {
			streakCount = 0;
		}
	}
}
