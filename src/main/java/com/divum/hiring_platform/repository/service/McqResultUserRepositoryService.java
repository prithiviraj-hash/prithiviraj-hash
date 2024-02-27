package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.MCQResult;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface McqResultUserRepositoryService {
    MCQResult findByid(String resultId);
    public List<MCQResult> findAll();

}
