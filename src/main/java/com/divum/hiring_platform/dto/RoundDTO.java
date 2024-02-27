package com.divum.hiring_platform.dto;


import com.divum.hiring_platform.util.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundDTO {

    private String id;

    private RoundType roundType;

    private List<PartDto> parts;

    private List<InterviewRoundDTO> interviews;

    private int roundNumber;

    private int pass;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
