package com.divum.hiring_platform.dto;

import com.divum.hiring_platform.util.enums.Difficulty;
import com.divum.hiring_platform.util.enums.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqPaginationDto {
    private String questionId;
    private String question;
    private QuestionCategory category;
    private Difficulty difficulty;
}
