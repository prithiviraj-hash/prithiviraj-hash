package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.CodingQuestion;
import com.divum.hiring_platform.repository.RoundsAndQuestionRepository;
import com.divum.hiring_platform.repository.service.RoundsAndQuestionRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundAndQuestionRepositoryServiceImpl implements RoundsAndQuestionRepositoryService {
    private final RoundsAndQuestionRepository roundsAndQuestionRepository;

    @Override
    public List<CodingQuestion> findByRoundId(String roundId) {
        return roundsAndQuestionRepository.findByRoundId(roundId);
    }
}