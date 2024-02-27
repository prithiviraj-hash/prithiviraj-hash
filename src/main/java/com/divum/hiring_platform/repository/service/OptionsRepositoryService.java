package com.divum.hiring_platform.repository.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptionsRepositoryService {

    List<String> getOption(String questionId);

    List<Object[]> getQuestionIdAndAnswers();

}
