package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Options;
import com.divum.hiring_platform.repository.OptionsRepository;
//import com.divum.hiring_platform.repository.service.OptionsRepositoryService;
import com.divum.hiring_platform.repository.service.OptionsRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionsRepositoryServiceImpl implements OptionsRepositoryService {

    private final OptionsRepository optionsRepository;

    @Override
    public List<String> getOption(String questionId) {
             List<String> options = optionsRepository.getOption(questionId);
             return options;
    }

    @Override
    public List<Object[]> getQuestionIdAndAnswers() {
        return optionsRepository.getQuestionIdAndAnswers();
    }


}
