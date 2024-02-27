package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewAssignContestant {

    private String name;
    private String email;
    private double score;
    private String feedback;
}
