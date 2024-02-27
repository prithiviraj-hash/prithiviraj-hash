package com.divum.hiring_platform.repository.service;

import com.divum.hiring_platform.dto.EmployeePaginationDto;
import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.util.enums.EmployeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface EmployeeRepositoryService {

    Page<EmployeePaginationDto> getAllEmployeesByType(Pageable pageable,EmployeeType type);

    Page<EmployeePaginationDto> getAllEmployees(Pageable pageable);

    Employee findEmployeeByEmployeeId(Long employeeId);

    List<Employee> findEmployeesByEmployeeType(EmployeeType employeeType);

    void save(Employee employee1);

    void deleteById(Long id);

    Employee findEmployeeByEmail(String emailId);

    Optional<Employee> findById(Long employeeId);
}
