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

@Entity
@Table(
		name = "step_options",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_step_options_step_text", columnNames = {"step_id", "option_text"})
		}
)
public class StepOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "step_id", nullable = false)
	private ChallengeStep step;

	@Column(name = "option_text", nullable = false, length = 255)
	private String optionText;

	@Column(name = "is_correct", nullable = false)
	private Boolean isCorrect;

	public StepOption() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChallengeStep getStep() {
		return step;
	}

	public void setStep(ChallengeStep step) {
		this.step = step;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean correct) {
		isCorrect = correct;
	}

	@PrePersist
	public void applyDefaults() {
		if (isCorrect == null) {
			isCorrect = false;
		}
	}
}
