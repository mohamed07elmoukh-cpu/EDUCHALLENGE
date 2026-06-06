package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.ChallengeLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeLikeRepository extends JpaRepository<ChallengeLike, Long> {

	long countByChallenge_Id(Long challengeId);

	boolean existsByChallenge_IdAndUser_Id(Long challengeId, Long userId);

	Optional<ChallengeLike> findByChallenge_IdAndUser_Id(Long challengeId, Long userId);
}
