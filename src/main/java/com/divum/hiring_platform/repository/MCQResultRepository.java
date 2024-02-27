package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.MCQResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MCQResultRepository extends MongoRepository<MCQResult, String> {


    Optional<MCQResult> findByUserIdAndRoundId(String userId, String roundId);
    List<MCQResult> findMCQResultsByRoundId(String roundId);


    int countByRoundId(String roundId);
}
