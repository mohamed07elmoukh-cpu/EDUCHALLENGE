package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.SavedChallenge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedChallengeRepository extends JpaRepository<SavedChallenge, Long> {

	long countByChallenge_Id(Long challengeId);

	boolean existsByChallenge_IdAndUser_Id(Long challengeId, Long userId);

	Optional<SavedChallenge> findByChallenge_IdAndUser_Id(Long challengeId, Long userId);
}
