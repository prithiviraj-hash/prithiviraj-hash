package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.CodingResult;

import java.util.List;


public interface CodingResultRepositoryService {
    int countByRoundId(String roundId);

    List<CodingResult> findCodingResultsByRoundId(String id);
}
