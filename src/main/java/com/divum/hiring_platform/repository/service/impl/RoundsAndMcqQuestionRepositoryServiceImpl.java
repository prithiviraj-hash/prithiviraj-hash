package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.MultipleChoiceQuestion;
import com.divum.hiring_platform.repository.RoundAndMcqQuestionRepository;
import com.divum.hiring_platform.repository.service.RoundsAndMcqQuestionRepositoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundsAndMcqQuestionRepositoryServiceImpl implements RoundsAndMcqQuestionRepositoryService {
    private final RoundAndMcqQuestionRepository roundsAndMcqQuestionRepository;

    public RoundsAndMcqQuestionRepositoryServiceImpl(RoundAndMcqQuestionRepository roundsAndMcqQuestionRepository) {
        this.roundsAndMcqQuestionRepository = roundsAndMcqQuestionRepository;
    }

    @Override
    public List<MultipleChoiceQuestion> findByRoundId(String roundId) {
        return roundsAndMcqQuestionRepository.findByRoundId(roundId);
    }
}
