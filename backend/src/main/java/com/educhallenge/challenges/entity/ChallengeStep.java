package com.educhallenge.challenges.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
		name = "challenge_steps",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_challenge_steps_challenge_order", columnNames = {"challenge_id", "step_order"})
		}
)
@SequenceGenerator(name = "challengeStepsSeq", sequenceName = "challenge_steps_id_seq", allocationSize = 1)
public class ChallengeStep {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "challengeStepsSeq")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	@Column(name = "question_text", nullable = false, columnDefinition = "text")
	private String questionText;

	@Column(name = "step_text", nullable = false, length = 500)
	private String stepText;

	@Column(name = "step_order", nullable = false)
	private Integer stepOrder;

	@Column(nullable = false)
	private Integer points;

	@OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private List<StepOption> options = new ArrayList<>();

	public ChallengeStep() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Challenge getChallenge() {
		return challenge;
	}

	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getStepText() {
		return stepText;
	}

	public void setStepText(String stepText) {
		this.stepText = stepText;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public List<StepOption> getOptions() {
		return options;
	}

	public void setOptions(List<StepOption> options) {
		this.options.clear();

		if (options == null) {
			return;
		}

		for (StepOption option : options) {
			addOption(option);
		}
	}

	public void addOption(StepOption option) {
		option.setStep(this);
		this.options.add(option);
	}

	@PostLoad
	public void synchronizeQuestionTextAfterLoad() {
		if ((questionText == null || questionText.isBlank()) && stepText != null) {
			questionText = stepText;
		}
	}

	@PrePersist
	@PreUpdate
	public void synchronizeLegacyStepText() {
		if ((questionText == null || questionText.isBlank()) && stepText != null) {
			questionText = stepText;
		}
		if ((stepText == null || stepText.isBlank()) && questionText != null) {
			stepText = questionText;
		}
		if (points == null) {
			points = 1;
		}
	}
}
