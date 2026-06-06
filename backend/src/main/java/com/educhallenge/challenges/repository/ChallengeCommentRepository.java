package com.educhallenge.challenges.repository;

import com.educhallenge.challenges.entity.ChallengeComment;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeCommentRepository extends JpaRepository<ChallengeComment, Long> {

	@EntityGraph(attributePaths = {"user"})
	List<ChallengeComment> findByChallenge_IdOrderByCreatedAtDesc(Long challengeId);

	long countByChallenge_Id(Long challengeId);
}
