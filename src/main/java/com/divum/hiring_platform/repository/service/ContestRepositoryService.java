package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.User;
import com.divum.hiring_platform.util.enums.ContestStatus;

import java.util.List;

public interface ContestRepositoryService {

    Contest findContestByContestId(String contestId);
    List<User> findUsersAssignedToTheContest(String contestId);
    List<User> findPassedStudents(Contest contest);
    List<Contest> findContestsByContestStatus(ContestStatus contestStatus);
    void deleteByContestId(String contestId);
    List<Contest> findAll();
    void save(Contest contest);
}
