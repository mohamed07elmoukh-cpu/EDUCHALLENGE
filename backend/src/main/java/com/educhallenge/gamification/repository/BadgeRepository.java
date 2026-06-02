package com.educhallenge.gamification.repository;

import com.educhallenge.gamification.entity.Badge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

	Optional<Badge> findBySlug(String slug);

	List<Badge> findAllByOrderByConditionValueAscNameAsc();
}
