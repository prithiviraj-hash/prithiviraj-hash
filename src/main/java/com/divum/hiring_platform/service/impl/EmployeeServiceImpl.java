package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.*;
import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.entity.Employee;
import com.divum.hiring_platform.entity.EmployeeAndContest;
import com.divum.hiring_platform.entity.EmployeeAvailability;
import com.divum.hiring_platform.repository.ContestRepository;
import com.divum.hiring_platform.repository.EmployeeAvailabilityRepository;
import com.divum.hiring_platform.repository.service.EmployeeRepositoryService;
import com.divum.hiring_platform.service.EmployeeService;
import com.divum.hiring_platform.util.EmailSender;
import com.divum.hiring_platform.util.JwtUtil;
import com.divum.hiring_platform.util.enums.EmployeeResponse;
import com.divum.hiring_platform.util.enums.EmployeeType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {


    private final JwtUtil jwtUtil;
    private final ContestRepository contestRepository;
    private final EmployeeAvailabilityRepository employeeAvailabilityRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepositoryService employeeRepositoryService;

    @Override
    public ResponseEntity<ResponseDto> addEmployee(Employee employee) {
        employeeRepositoryService.save(employee);
        return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("employee is successfully added",employee));
    }

    @Override
    public ResponseEntity<ResponseDto> getEmployee(Long id) {
        Employee employee = employeeRepositoryService.findEmployeeByEmployeeId(id);
        if(employee != null) {
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("here is the employee",employee));
        }else{
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("there is no employee with this id "+id,null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateEmployee(Long id, EmployeeDto employeeDto) {
        Optional<Employee> employee = Optional.ofNullable(employeeRepositoryService.findEmployeeByEmployeeId(id));

        if(employee.isPresent()) {
            Employee employee1=employee.get();
            employee1.setEmail(employeeDto.getEmail());
            employee1.setEmployeeType(employeeDto.getEmployeeType());
            employee1.setFirstName(employeeDto.getFirstName());
            employee1.setLastName(employeeDto.getLastName());
            employee1.setRole(employeeDto.getRole());
            employee1.setPassword(employeeDto.getPassword());
            employee1.setStack(employeeDto.getStack());
            employee1.setYearsOfExperience(employeeDto.getYearsOfExperience());
            employeeRepositoryService.save(employee1);
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("employee data is updated",null));
        }else {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("no employee with this id "+id,null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> deleteEmployee(Long id) {
        Optional<Employee> employee= Optional.ofNullable(employeeRepositoryService.findEmployeeByEmployeeId(id));
        if(employee.isPresent()){
            employeeRepositoryService.deleteById(id);
            return  ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("employee is deleted",null));
        }else{
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("no employee with this id "+id,null));


        }

    }

    @Override
    public ResponseEntity<ResponseDto> getAllEmployees(Pageable pageable, EmployeeType type) {
        if(type==null){
            Page<EmployeePaginationDto> employees = employeeRepositoryService.getAllEmployees(pageable);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all employees",employees));
        }else{
            Page<EmployeePaginationDto> employees = employeeRepositoryService.getAllEmployeesByType(pageable,type);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all employees by type",employees));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> setResponse(String token) throws MessagingException {
        EmployeeResponseDTO employeeResponse;
        try {
            employeeResponse = jwtUtil.extractEmployeeResponse(token);
            if(!jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto("Token expired contact your admin", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto("Not a valid token", null));
        }
        Contest contest = null;
        Employee employee = null;
        try {
            contest = contestRepository.findById(employeeResponse.getContestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contest with the id " + employeeResponse.getContestId() + "not found"));
        } catch (Exception e) {
            resourceNotFound(e.getMessage());
        }
        try {
            employee = employeeRepositoryService.findById(employeeResponse.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with the id " + employeeResponse.getEmployeeId() + "not found"));
        } catch (Exception e) {
            resourceNotFound(e.getMessage());
        }
        EmployeeAvailability employeeAvailability = employeeAvailabilityRepository.findEmployeeAvailabilityByEmployeeAndContest(new EmployeeAndContest(employee, contest));
        if(employeeResponse.getDecision().equals("ACCEPT")) {
            employeeAvailability.setResponse(EmployeeResponse.AVAILABLE);
        } else {
            employeeAvailability.setResponse(EmployeeResponse.NOT_AVAILABLE);
        }
        employeeAvailabilityRepository.save(employeeAvailability);
        emailSender.sendConfirmationMail(employeeAvailability);
        return ResponseEntity.ok(new ResponseDto("Email send to the employees", null));
    }

    @Override
    public ResponseEntity<ResponseDto> forgotPassword(String emailId, String method, Password password) {
        Employee employee = employeeRepositoryService.findEmployeeByEmail(emailId);

        if(employee == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(emailId + " Not found", null));
        }
        assert employee != null;
        if(method.equals("FORGET_PASSWORD")) {
            String token = jwtUtil.generateForgotPasswordToken(employee.getEmployeeId());
            try {
                emailSender.sendEmailToTheEmployeeToResetThePassword(employee, token);
                return ResponseEntity.ok(new ResponseDto("Email has been sent to the user with password reset link", null));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(e.getMessage(), null));
            }
        } else if (method.equals("CHANGE_PASSWORD")) {
            String oldPassword = password.getOldPassword();
            if(passwordEncoder.matches(oldPassword, employee.getPassword())) {
                employee.setPassword(passwordEncoder.encode(password.getNewPassword()));
                employeeRepositoryService.save(employee);
                try {
                    emailSender.sendEmailToTheEmployeeAboutPasswordChange(employee);
                } catch (MessagingException e) {
                    return ResponseEntity.ok(new ResponseDto("Password has been changed", e.getMessage()));
                }
                return ResponseEntity.ok(new ResponseDto("Password has been changed", null));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto("You password does not match", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Not a valid request", null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> employeePasswordReset(String token, Password password) {
        Long id;
        try {
            id = Long.valueOf(jwtUtil.extractEmployeeId(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDto("Not a valid token", null));
        }
        Employee employee = employeeRepositoryService.findEmployeeByEmployeeId(id);
        employee.setPassword(passwordEncoder.encode(password.getNewPassword()));
        employeeRepositoryService.save(employee);
        try {
            emailSender.sendEmailToTheEmployeeAboutPasswordChange(employee);
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDto("Password has been updated", e.getMessage()));
        }
        return ResponseEntity.ok(new ResponseDto("Password has been updated", null));
    }

    public void resourceNotFound(String message){
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(message, null));
    }


}
