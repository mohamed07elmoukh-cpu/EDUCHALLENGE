package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.Challenge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

	interface MyChallengeProjection {
		Long getId();
		String getTitle();
		String getDescription();
		String getCategory();
		String getDifficulty();
		Integer getPointsReward();
		Long getQuestionsCount();
		Long getParticipantsCount();
		Long getAttemptsCount();
		String getStatus();
		java.time.LocalDateTime getCreatedAt();
	}

	interface AdminChallengeManagementProjection {
		Long getId();
		String getTitle();
		String getCategory();
		String getDifficulty();
		Integer getPointsReward();
		Long getCreatorId();
		String getCreatorUsername();
		String getVisibility();
		Boolean getIsActive();
		java.time.LocalDateTime getCreatedAt();
		Long getStepCount();
		Long getTotalAttempts();
		Long getUniqueParticipants();
		java.math.BigDecimal getAverageScore();
		Long getCompletionCount();
	}

	interface AdminChallengeStatsProjection {
		Long getChallengeId();
		Long getTotalAttempts();
		Long getUniqueParticipants();
		java.math.BigDecimal getAverageScore();
		Long getCompletionCount();
	}

	@EntityGraph(attributePaths = {"creator"})
	List<Challenge> findByVisibilityAndIsActiveTrueOrderByCreatedAtDesc(String visibility);

	@Query("""
			select distinct c
			from Challenge c
			left join fetch c.creator
			where c.isActive = true
			  and (
			    upper(c.visibility) = 'PUBLIC'
			    or c.creator.id = :userId
			    or (c.creator is null and lower(c.creatorUsername) = lower(:username))
			  )
			order by c.createdAt desc
			""")
	List<Challenge> findVisibleChallengesForUser(
			@Param("userId") Long userId,
			@Param("username") String username
	);

	@Query(
			value = """
					select
						c.id as id,
						c.title as title,
						c.description as description,
						c.category as category,
						c.difficulty as difficulty,
						c.points_reward as pointsReward,
						count(distinct cs.id) as questionsCount,
						count(distinct ca.user_id) as participantsCount,
						count(distinct ca.id) as attemptsCount,
						case
							when coalesce(c.is_active, true) = false then 'ARCHIVED'
							when upper(coalesce(c.visibility, 'PUBLIC')) = 'PRIVATE' then 'DRAFT'
							else 'ACTIVE'
						end as status,
						c.created_at as createdAt
					from challenges c
					left join challenge_steps cs on cs.challenge_id = c.id
					left join challenge_attempts ca on ca.challenge_id = c.id
					where c.creator_id = :userId
					   or (c.creator_id is null and lower(c.creator_username) = lower(:username))
					group by c.id, c.title, c.description, c.category, c.difficulty, c.points_reward, c.is_active, c.visibility, c.created_at
					order by c.created_at desc
					""",
			nativeQuery = true
	)
	List<MyChallengeProjection> findMyCreatedChallenges(
			@Param("userId") Long userId,
			@Param("username") String username
	);

	@Query(
			value = """
					select
						c.id as id,
						c.title as title,
						c.category as category,
						c.difficulty as difficulty,
						c.points_reward as pointsReward,
						c.creator_id as creatorId,
						c.creator_username as creatorUsername,
						c.visibility as visibility,
						c.is_active as isActive,
						c.created_at as createdAt,
						(select count(*) from challenge_steps cs where cs.challenge_id = c.id) as stepCount,
						(select count(*) from challenge_attempts ca where ca.challenge_id = c.id) as totalAttempts,
						(select count(distinct ca.user_id) from challenge_attempts ca where ca.challenge_id = c.id) as uniqueParticipants,
						(select coalesce(avg(ca.score), 0) from challenge_attempts ca where ca.challenge_id = c.id) as averageScore,
						(select count(*) from challenge_attempts ca where ca.challenge_id = c.id and upper(ca.status) = 'COMPLETED') as completionCount
					from challenges c
					order by c.created_at desc
					""",
			nativeQuery = true
	)
	List<AdminChallengeManagementProjection> findAllForAdminManagement();

	@Query(
			value = """
					select
						c.id as challengeId,
						(select count(*) from challenge_attempts ca where ca.challenge_id = c.id) as totalAttempts,
						(select count(distinct ca.user_id) from challenge_attempts ca where ca.challenge_id = c.id) as uniqueParticipants,
						(select coalesce(avg(ca.score), 0) from challenge_attempts ca where ca.challenge_id = c.id) as averageScore,
						(select count(*) from challenge_attempts ca where ca.challenge_id = c.id and upper(ca.status) = 'COMPLETED') as completionCount
					from challenges c
					where c.id = :challengeId
					""",
			nativeQuery = true
	)
	Optional<AdminChallengeStatsProjection> findAdminStatsByChallengeId(@Param("challengeId") Long challengeId);

	@Query("""
			select distinct c
			from Challenge c
			left join fetch c.creator
			left join fetch c.steps s
			where c.id = :id
			""")
	Optional<Challenge> findDetailedById(@Param("id") Long id);
}
