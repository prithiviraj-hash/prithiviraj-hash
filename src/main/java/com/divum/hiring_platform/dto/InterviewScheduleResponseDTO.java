package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewScheduleResponseDTO {

    private String interviewId;
    private String interviewer;
    private String interviewee;
    private String collageName;
    private String interviewDate;
    private String interviewTime;
}
