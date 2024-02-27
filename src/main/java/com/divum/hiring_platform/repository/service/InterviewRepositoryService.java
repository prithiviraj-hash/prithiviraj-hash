package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.Interview;

import java.util.List;
import java.util.Optional;

public interface InterviewRepositoryService {
    List<Interview> findInterviewsByRoundsId(String roundId);

    void saveAll(List<Interview> interviews);

    Optional<Interview> findById(String interviewId);

    void save(Interview interview);

    List<Interview> findInterviewsByEmployee(Employee employee);

    int countByRoundsId(String roundId);
}
