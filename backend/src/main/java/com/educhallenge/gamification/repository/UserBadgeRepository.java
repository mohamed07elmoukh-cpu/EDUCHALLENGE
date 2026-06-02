package com.educhallenge.gamification.repository;

import com.educhallenge.gamification.entity.UserBadge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	@EntityGraph(attributePaths = {"badge"})
	List<UserBadge> findByUser_IdOrderByEarnedAtDesc(Long userId);

	boolean existsByUser_IdAndBadge_Id(Long userId, Long badgeId);

	@EntityGraph(attributePaths = {"badge"})
	Optional<UserBadge> findTopByUser_IdOrderByEarnedAtDesc(Long userId);

	@EntityGraph(attributePaths = {"badge", "user"})
	List<UserBadge> findByUser_IdInOrderByEarnedAtDesc(List<Long> userIds);
}
