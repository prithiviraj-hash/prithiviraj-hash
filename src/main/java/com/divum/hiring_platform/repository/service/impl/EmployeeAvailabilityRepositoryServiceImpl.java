package com.divum.hiring_platform.repository.service.impl;

import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.EmployeeAvailability;
import com.divum.hiring_platform.repository.EmployeeAvailabilityRepository;
import com.divum.hiring_platform.repository.service.EmployeeAvailabilityRepositoryService;
import com.divum.hiring_platform.util.enums.EmployeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeAvailabilityRepositoryServiceImpl implements EmployeeAvailabilityRepositoryService {

    private final EmployeeAvailabilityRepository employeeAvailabilityRepository;
    @Override
    public Employee findEmployeesWhoIsAvailable(Long employeeId, Contest contest, EmployeeType employeeType) {
        return employeeAvailabilityRepository.findEmployeesWhoIsAvailable(employeeId, contest, employeeType);
    }

    @Override
    public void saveAll(List<EmployeeAvailability> employeeAvailabilities) {
        employeeAvailabilityRepository.saveAll(employeeAvailabilities);
    }

    @Override
    public List<EmployeeAvailability> findEmployeeAvailabilitiesByContest(String contestId) {
        return employeeAvailabilityRepository.findEmployeeAvailabilitiesByContest(contestId);
    }
}
