package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.MultipleChoiceQuestion;

import java.util.List;

public interface RoundsAndMcqQuestionRepositoryService {
    List<MultipleChoiceQuestion> findByRoundId(String roundId);
}
