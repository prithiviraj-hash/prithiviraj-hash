package com.divum.hiring_platform.repository;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.EmployeeAndContest;
import com.divum.hiring_platform.entity.EmployeeAvailability;
import com.divum.hiring_platform.util.enums.EmployeeResponse;
import com.divum.hiring_platform.util.enums.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeAvailabilityRepository extends JpaRepository<EmployeeAvailability, Long> {
    List<EmployeeAvailability> findByResponse(EmployeeResponse response);

    @Query("SELECT employee FROM EmployeeAvailability employee WHERE employee.employeeAndContest.contest.contestId = ?1")
    List<EmployeeAvailability> findEmployeeAvailabilitiesByContest(String contestId);

    @Query("SELECT r FROM EmployeeAvailability r WHERE r.employeeAndContest =?1")
    EmployeeAvailability findEmployeeAvailabilityByEmployeeAndContest(EmployeeAndContest employeeAndContest);

    @Query("SELECT availability.employeeAndContest.employee FROM EmployeeAvailability availability WHERE availability.employeeAndContest.employee.employeeId =?1 AND  availability.employeeAndContest.contest =?2 AND availability.employeeAndContest.employee.employeeType =?3")
    Employee findEmployeesWhoIsAvailable(Long employeeId, Contest contest, EmployeeType employeeType);
}

