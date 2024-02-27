package com.divum.hiring_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAssignDTO {

    private String round;
    private String id;
    private String contestName;
    private Integer roundsNumber;
    private List<InterviewAssignContestant> contestants;
    private List<InterviewAssignEmployee> employees;
}
