package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualInterviewRequest {

    private ContestDetails contestDetails;
    private EmployeeDetails employeeDetails;
    private List<ScheduleDetails> scheduleDetails;
    private List<InterviewAssignEmployee> employees;
}
