package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.EmailTask;
import com.divum.hiring_platform.entity.Rounds;

import java.util.List;

public interface EmailTaskRepositoryService {
    List<EmailTask> findAllByRounds(Rounds existingRound);

    void saveAll(List<EmailTask> emailTasks);
}

