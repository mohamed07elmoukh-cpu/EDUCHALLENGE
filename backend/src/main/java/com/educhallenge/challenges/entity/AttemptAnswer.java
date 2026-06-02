package com.educhallenge.challenges.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
		name = "attempt_answers",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_attempt_answers_attempt_step", columnNames = {"attempt_id", "step_id"})
		}
)
public class AttemptAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attempt_id", nullable = false)
	private ChallengeAttempt attempt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "step_id", nullable = false)
	private ChallengeStep step;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "selected_option_id", nullable = false)
	private StepOption selectedOption;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	@Column(name = "answered_at", nullable = false)
	private LocalDateTime answeredAt;

	public AttemptAnswer() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChallengeAttempt getAttempt() {
		return attempt;
	}

	public void setAttempt(ChallengeAttempt attempt) {
		this.attempt = attempt;
	}

	public ChallengeStep getStep() {
		return step;
	}

	public void setStep(ChallengeStep step) {
		this.step = step;
	}

	public StepOption getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(StepOption selectedOption) {
		this.selectedOption = selectedOption;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean correct) {
		isCorrect = correct;
	}

	public LocalDateTime getAnsweredAt() {
		return answeredAt;
	}

	public void setAnsweredAt(LocalDateTime answeredAt) {
		this.answeredAt = answeredAt;
	}

	@PrePersist
	public void applyDefaults() {
		if (answeredAt == null) {
			answeredAt = LocalDateTime.now();
		}
		if (isCorrect == null) {
			isCorrect = false;
		}
	}
}
