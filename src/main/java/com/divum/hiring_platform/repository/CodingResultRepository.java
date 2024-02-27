package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.CodingResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingResultRepository extends MongoRepository<CodingResult, String> {
    List<CodingResult> findCodingResultsByRoundId(String roundId);

    int countByRoundId(String roundId);
}