package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.CodingQuestion;

import java.util.List;

public interface RoundsAndQuestionRepositoryService {
    List<CodingQuestion> findByRoundId(String roundId);
}