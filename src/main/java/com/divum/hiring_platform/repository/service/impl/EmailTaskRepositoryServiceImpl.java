package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.EmailTask;
import com.divum.hiring_platform.entity.Rounds;
import com.divum.hiring_platform.repository.EmailTaskRepository;
import com.divum.hiring_platform.repository.service.EmailTaskRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailTaskRepositoryServiceImpl implements EmailTaskRepositoryService {

    private final EmailTaskRepository emailTaskRepository;

    @Override
    public List<EmailTask> findAllByRounds(Rounds existingRound) {
        return emailTaskRepository.findAllByRounds(existingRound);
    }

    @Override
    public void saveAll(List<EmailTask> emailTasks) {
        emailTaskRepository.saveAll(emailTasks);
    }

}
