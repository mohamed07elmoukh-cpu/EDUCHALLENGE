package com.educhallenge.users.repository;

import com.educhallenge.users.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	interface LeaderboardProjection {
		Long getId();
		String getUsername();
		Integer getPointsTotal();
		Integer getLevel();
		Integer getRankPosition();
	}

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUsername(String username);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByUsernameIgnoreCase(String username);

	@Query(
			value = """
					with ranked as (
						select
							u.id as id,
							u.username as username,
							coalesce(u.points_total, 0) as points_total,
							coalesce(u.level, 1) as level,
							dense_rank() over (
								order by coalesce(u.points_total, 0) desc, coalesce(u.level, 1) desc, lower(u.username) asc
							) as rank_position
						from users u
					)
					select
						ranked.id as id,
						ranked.username as username,
						ranked.points_total as pointsTotal,
						ranked.level as level,
						ranked.rank_position as rankPosition
					from ranked
					order by ranked.rank_position asc, lower(ranked.username) asc
					limit :limit
					""",
			nativeQuery = true
	)
	List<LeaderboardProjection> findLeaderboardEntries(@Param("limit") int limit);

	@Query(
			value = """
					with ranked as (
						select
							u.id as id,
							dense_rank() over (
								order by coalesce(u.points_total, 0) desc, coalesce(u.level, 1) desc, lower(u.username) asc
							) as rank_position
						from users u
					)
					select ranked.rank_position
					from ranked
					where ranked.id = :userId
					""",
			nativeQuery = true
	)
	Integer findRankPositionByUserId(@Param("userId") Long userId);
}
