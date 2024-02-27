package com.divum.hiring_platform.dto;


import com.divum.hiring_platform.util.enums.ContestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EssentialContestDetails {

    private String contestId;
    private String startDate;
    private String endDate;
    private String name;
    private ContestStatus contestStatus;
}
