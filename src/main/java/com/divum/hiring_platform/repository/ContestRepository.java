package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.User;
import com.divum.hiring_platform.util.enums.ContestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, String> {
    Contest findContestByContestId(String contestId);


    @Query("SELECT u FROM User u JOIN u.contest c WHERE c.contestId = ?1")
    List<User> findUsersAssignedToTheContest(String contestId);

    @Query("SELECT user FROM User user JOIN user.contest c WHERE c = :contest AND user.isPassed = true")
    List<User> findPassedStudents(Contest contest);

    List<Contest> findContestsByContestStatus(ContestStatus contestStatus);
    void deleteByContestId(String contestId);
}
