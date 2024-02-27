package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestResult {

    private String contestName;
    private Integer participantCount;
    private String contestDate;
    private Integer totalRoundCount;
    private List<RoundList> roundLists;
}
