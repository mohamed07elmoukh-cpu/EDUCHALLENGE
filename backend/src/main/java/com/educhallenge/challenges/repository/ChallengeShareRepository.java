package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.ChallengeShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeShareRepository extends JpaRepository<ChallengeShare, Long> {

	long countByChallenge_Id(Long challengeId);
}
