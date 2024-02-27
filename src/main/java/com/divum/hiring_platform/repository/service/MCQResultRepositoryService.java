package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.MCQResult;

import java.util.List;
import java.util.Optional;

public interface MCQResultRepositoryService {

    Optional<MCQResult> findByUserIdAndRoundId(String userId, String roundId);
    int countByRoundId(String roundId);

    List<MCQResult> findMCQResultsByRoundId(String id);
}
