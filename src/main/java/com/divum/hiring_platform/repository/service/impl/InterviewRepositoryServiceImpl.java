package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.Interview;
import com.divum.hiring_platform.repository.InterviewRepository;
import com.divum.hiring_platform.repository.service.InterviewRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewRepositoryServiceImpl implements InterviewRepositoryService {

    private final InterviewRepository interviewRepository;
    @Override
    public List<Interview> findInterviewsByRoundsId(String roundId) {
        return interviewRepository.findInterviewsByRoundsId(roundId);
    }

    @Override
    public void saveAll(List<Interview> interviews) {
        interviewRepository.saveAll(interviews);
    }

    @Override
    public Optional<Interview> findById(String interviewId) {
        return interviewRepository.findById(interviewId);
    }

    @Override
    public void save(Interview interview) {
        interviewRepository.save(interview);
    }

    @Override
    public List<Interview> findInterviewsByEmployee(Employee employee) {
        return interviewRepository.findInterviewsByEmployee(employee);
    }

    @Override
    public int countByRoundsId(String roundId) {
        return interviewRepository.countByRoundsId(roundId);
    }
}
