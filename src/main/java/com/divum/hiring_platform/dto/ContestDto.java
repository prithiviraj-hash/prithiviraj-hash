package com.divum.hiring_platform.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestDto {

    private String id;

    @FutureOrPresent(message = "Start time must be in the present or future")
    private LocalDateTime startTime;

    @FutureOrPresent(message = "End time must be in the present or future")
    private LocalDateTime endTime;

    @Positive(message = "The passmark must be greater than zero")
    private int passMark;

    private List<PartTypeDto> type;

    @NotNull(message = "Contest name cannot be null")
    private String name;

    private List<RoundDTO> rounds;

}
