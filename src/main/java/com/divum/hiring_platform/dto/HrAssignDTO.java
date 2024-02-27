package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HrAssignDTO {

    private String contestName;
    private Integer roundNumber;
    private String status;
    private String roundType;
    private String roundId;
    private String contestDate;
}
