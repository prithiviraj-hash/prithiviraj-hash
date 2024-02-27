package com.divum.hiring_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestDetails {
    private String contestName;
    private Integer roundNumber;
    private String roundType;
    private String interviewTime;
    private String interviewDate;
}
