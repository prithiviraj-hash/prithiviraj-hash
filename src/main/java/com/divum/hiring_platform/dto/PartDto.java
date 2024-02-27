package com.divum.hiring_platform.dto;


import com.divum.hiring_platform.util.enums.QuestionCategory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartDto {

    private QuestionCategory category;

    @PositiveOrZero(message = "The easy count should not be negative")
    private int easy;

    @PositiveOrZero(message = "The medium count should not be negative")
    private int medium;

    @PositiveOrZero(message = "The hard count should not be negative")
    private int hard;

    @Positive(message = "The assigned should be greater than zero")
    private Duration assignedTime;
}
