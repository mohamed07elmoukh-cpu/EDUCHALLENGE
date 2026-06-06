package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.ChallengeAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeAttemptRepository extends JpaRepository<ChallengeAttempt, Long> {

	interface AdminChallengeAttemptProjection {
		Long getAttemptId();
		Long getChallengeId();
		Long getParticipantId();
		String getParticipantUsername();
		String getParticipantEmail();
		String getStatus();
		Integer getScore();
		java.time.LocalDateTime getStartedAt();
		java.time.LocalDateTime getCompletedAt();
		Long getDurationSeconds();
	}

	interface AdminChallengeParticipantLeaderboardProjection {
		Integer getRank();
		Long getParticipantId();
		String getParticipantUsername();
		String getParticipantEmail();
		Integer getBestScore();
		Long getCompletedAttempts();
		Long getFastestCompletionSeconds();
		java.time.LocalDateTime getLastCompletedAt();
	}

	boolean existsByUser_IdAndChallenge_IdAndStatusIgnoreCase(Long userId, Long challengeId, String status);

	boolean existsByUser_IdAndChallenge_IdAndStatusIgnoreCaseAndIdLessThan(
			Long userId,
			Long challengeId,
			String status,
			Long id
	);

	@Query("""
			select count(distinct ca.challenge.id)
			from ChallengeAttempt ca
			where ca.user.id = :userId
			  and upper(ca.status) = 'COMPLETED'
			""")
	long countDistinctCompletedChallengesByUserId(@Param("userId") Long userId);

	@Query("""
			select count(ca)
			from ChallengeAttempt ca
			where ca.user.id = :userId
			  and upper(ca.status) = 'COMPLETED'
			""")
	long countCompletedAttemptsByUserId(@Param("userId") Long userId);

	@EntityGraph(attributePaths = {"challenge", "challenge.steps"})
	List<ChallengeAttempt> findTop6ByUser_IdAndStatusIgnoreCaseOrderByCompletedAtDesc(Long userId, String status);

	@Query(
			value = """
					select
						ca.id as attemptId,
						ca.challenge_id as challengeId,
						u.id as participantId,
						u.username as participantUsername,
						u.email as participantEmail,
						ca.status as status,
						coalesce(ca.score, 0) as score,
						ca.started_at as startedAt,
						ca.completed_at as completedAt,
						case
							when ca.completed_at is not null and ca.started_at is not null
								then greatest(0, extract(epoch from (ca.completed_at - ca.started_at))::bigint)
							else null
						end as durationSeconds
					from challenge_attempts ca
					join users u on u.id = ca.user_id
					where ca.challenge_id = :challengeId
					order by coalesce(ca.completed_at, ca.started_at) desc, ca.id desc
					""",
			nativeQuery = true
	)
	List<AdminChallengeAttemptProjection> findAdminAttemptsByChallengeId(@Param("challengeId") Long challengeId);

	@Query(
			value = """
					with participant_scores as (
						select
							u.id as participantId,
							u.username as participantUsername,
							u.email as participantEmail,
							max(coalesce(ca.score, 0))::integer as bestScore,
							count(*) filter (where upper(ca.status) = 'COMPLETED')::bigint as completedAttempts,
							min(
								case
									when upper(ca.status) = 'COMPLETED'
									 and ca.completed_at is not null
									 and ca.started_at is not null
										then greatest(0, extract(epoch from (ca.completed_at - ca.started_at))::bigint)
									else null
								end
							) as fastestCompletionSeconds,
							max(ca.completed_at) filter (where upper(ca.status) = 'COMPLETED') as lastCompletedAt
						from challenge_attempts ca
						join users u on u.id = ca.user_id
						where ca.challenge_id = :challengeId
						group by u.id, u.username, u.email
					),
					ranked as (
						select
							participant_scores.*,
							dense_rank() over (
								order by
									coalesce(bestScore, 0) desc,
									coalesce(fastestCompletionSeconds, 9223372036854775807) asc,
									lower(participantUsername) asc
							) as rank
						from participant_scores
					)
					select
						ranked.rank as rank,
						ranked.participantId as participantId,
						ranked.participantUsername as participantUsername,
						ranked.participantEmail as participantEmail,
						coalesce(ranked.bestScore, 0) as bestScore,
						coalesce(ranked.completedAttempts, 0) as completedAttempts,
						ranked.fastestCompletionSeconds as fastestCompletionSeconds,
						ranked.lastCompletedAt as lastCompletedAt
					from ranked
					order by ranked.rank asc, lower(ranked.participantUsername) asc
					""",
			nativeQuery = true
	)
	List<AdminChallengeParticipantLeaderboardProjection> findAdminLeaderboardByChallengeId(@Param("challengeId") Long challengeId);
}
