package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.CodingResult;
import com.divum.hiring_platform.repository.CodingResultRepository;
import com.divum.hiring_platform.repository.service.CodingResultRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodingResultRepositoryServiceImpl implements CodingResultRepositoryService {


    private final CodingResultRepository codingResultRepository;
    @Override
    public int countByRoundId(String roundId) {
        return codingResultRepository.countByRoundId(roundId);
    }

    @Override
    public List<CodingResult> findCodingResultsByRoundId(String id) {
        return codingResultRepository.findCodingResultsByRoundId(id);
    }
}
