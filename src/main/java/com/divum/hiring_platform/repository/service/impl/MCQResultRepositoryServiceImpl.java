package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.MCQResult;
import com.divum.hiring_platform.repository.MCQResultRepository;
import com.divum.hiring_platform.repository.service.MCQResultRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MCQResultRepositoryServiceImpl implements MCQResultRepositoryService{
    private final MCQResultRepository mcqResultRepository;

    public MCQResultRepositoryServiceImpl(MCQResultRepository mcqResultRepository) {
        this.mcqResultRepository = mcqResultRepository;
    }

    @Override
    public Optional<MCQResult> findByUserIdAndRoundId(String userId, String roundId) {
        return mcqResultRepository.findByUserIdAndRoundId(userId, roundId);
    }

    @Override
    public int countByRoundId(String roundId) {
        return mcqResultRepository.countByRoundId(roundId);
    }

    @Override
    public List<MCQResult> findMCQResultsByRoundId(String id) {
        return mcqResultRepository.findMCQResultsByRoundId(id);
    }
}
