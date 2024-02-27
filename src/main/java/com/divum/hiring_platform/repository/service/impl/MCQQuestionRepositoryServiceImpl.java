package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.dto.McqPaginationDto;
import com.divum.hiring_platform.repository.MCQQuestionRepository;
import com.divum.hiring_platform.repository.service.MCQQuestionRepositoryService;
import com.divum.hiring_platform.util.enums.Difficulty;
import com.divum.hiring_platform.util.enums.QuestionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MCQQuestionRepositoryServiceImpl implements MCQQuestionRepositoryService {
    private final MCQQuestionRepository mcqQuestionRepository;

    public MCQQuestionRepositoryServiceImpl(MCQQuestionRepository mcqQuestionRepository) {
        this.mcqQuestionRepository = mcqQuestionRepository;
    }

    @Override
    public Page<McqPaginationDto> getAllMCQs(Pageable pageable) {
        return mcqQuestionRepository.getAllMCQs(pageable);
    }

    @Override
    public Page<McqPaginationDto> getAllMCQByType(Pageable pageable, List<QuestionCategory> type) {
        return mcqQuestionRepository.getAllMCQByType(pageable, type);
    }

    @Override
    public Page<McqPaginationDto> getAllMCQByDifficulty(Pageable pageable, List<Difficulty> difficulty) {
        return mcqQuestionRepository.getAllMCQByDifficulty(pageable, difficulty);
    }

    @Override
    public Page<McqPaginationDto> getAllMCQByDifficultyAndType(Pageable pageable, List<Difficulty> difficulty, List<QuestionCategory> type) {
        return mcqQuestionRepository.getAllMCQByDifficultyAndType(pageable, difficulty, type);
    }
}