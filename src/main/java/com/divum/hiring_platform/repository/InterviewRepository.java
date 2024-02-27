package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findInterviewsByRoundsId(String id);

    List<Interview> findInterviewsByEmployee(Employee employee);

    int countByRoundsId(String roundId);
}
