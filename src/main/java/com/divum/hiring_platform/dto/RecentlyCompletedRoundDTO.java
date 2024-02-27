package com.divum.hiring_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentlyCompletedRoundDTO {

    private String roundId;
    private String contestName;
    private String roundCompletedTime;
    private int RoundNumber;
    private String status;
}
