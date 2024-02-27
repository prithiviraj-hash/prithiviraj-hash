package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.User;
import com.divum.hiring_platform.repository.ContestRepository;
import com.divum.hiring_platform.repository.service.ContestRepositoryService;
import com.divum.hiring_platform.util.enums.ContestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestRepositoryServiceImpl implements ContestRepositoryService {

    private final ContestRepository contestRepository;


    @Override
    public Contest findContestByContestId(String contestId) {
        return contestRepository.findContestByContestId(contestId);
    }

    @Override
    public void save(Contest contest) {
        contestRepository.save(contest);
    }
    public List<User> findUsersAssignedToTheContest(String contestId) {
        return contestRepository.findUsersAssignedToTheContest(contestId);
    }

    @Override
    public List<User> findPassedStudents(Contest contest) {
        return contestRepository.findPassedStudents(contest);
    }

    @Override
    public List<Contest> findContestsByContestStatus(ContestStatus contestStatus) {
        return contestRepository.findContestsByContestStatus(contestStatus);
    }

    @Override
    public void deleteByContestId(String contestId) {
        contestRepository.deleteByContestId(contestId);
    }

    @Override
    public List<Contest> findAll() {
        return contestRepository.findAll();
    }


}
