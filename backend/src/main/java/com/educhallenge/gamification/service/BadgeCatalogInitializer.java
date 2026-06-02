package com.educhallenge.gamification.service;

import com.educhallenge.gamification.entity.Badge;
import com.educhallenge.gamification.repository.BadgeRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BadgeCatalogInitializer implements ApplicationRunner {

	private final BadgeRepository badgeRepository;

	public BadgeCatalogInitializer(BadgeRepository badgeRepository) {
		this.badgeRepository = badgeRepository;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		List<BadgeDefinition> catalog = List.of(
				new BadgeDefinition(
						"first-challenge-completed",
						"First Challenge Completed",
						"Finish your very first challenge on EduChallenge.",
						"challenge",
						"CHALLENGES_COMPLETED",
						1,
						"bronze",
						"flag"
				),
				new BadgeDefinition(
						"quick-solver",
						"Quick Solver",
						"Complete a challenge with a perfect score on the first finished attempt.",
						"speed",
						"FIRST_TRY_PERFECT",
						1,
						"silver",
						"bolt"
				),
				new BadgeDefinition(
						"perfect-score",
						"Perfect Score",
						"Reach a perfect score on any challenge.",
						"mastery",
						"PERFECT_SCORE",
						1,
						"gold",
						"star"
				),
				new BadgeDefinition(
						"100-points-reached",
						"100 Points Reached",
						"Accumulate at least 100 points across completed challenges.",
						"milestone",
						"POINTS_REACHED",
						100,
						"gold",
						"rocket"
				),
				new BadgeDefinition(
						"5-challenges-completed",
						"5 Challenges Completed",
						"Complete five different challenges to prove your consistency.",
						"challenge",
						"CHALLENGES_COMPLETED",
						5,
						"silver",
						"trophy"
				)
		);

		for (BadgeDefinition definition : catalog) {
			Badge badge = badgeRepository.findBySlug(definition.slug()).orElseGet(Badge::new);
			badge.setSlug(definition.slug());
			badge.setName(definition.name());
			badge.setDescription(definition.description());
			badge.setBadgeType(definition.badgeType());
			badge.setConditionType(definition.conditionType());
			badge.setConditionValue(definition.conditionValue());
			badge.setTone(definition.tone());
			badge.setIconName(definition.iconName());
			badgeRepository.save(badge);
		}
	}

	private record BadgeDefinition(
			String slug,
			String name,
			String description,
			String badgeType,
			String conditionType,
			Integer conditionValue,
			String tone,
			String iconName
	) {
	}
}
