package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.MCQResult;
import com.divum.hiring_platform.repository.McqResultUserRepository;
import com.divum.hiring_platform.repository.service.McqResultUserRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class McqResultUserRepositoryServiceImpl implements McqResultUserRepositoryService {

    private final McqResultUserRepository mcqResultUserRepository;

    @Override
    public MCQResult findByid(String resultId) {
        return mcqResultUserRepository.findByid(resultId);
    }

//    @Override
//    public String save(MCQResult entity) {
//        mcqResultUserRepository.save(entity);
//        return "Data Inserted";
//    }
    @Override
    public List<MCQResult> findAll() {
        return mcqResultUserRepository.findAll();
    }

}
