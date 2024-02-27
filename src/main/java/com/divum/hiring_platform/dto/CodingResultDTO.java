package com.divum.hiring_platform.dto;


import com.divum.hiring_platform.entity.CodingQuestionObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodingResultDTO {

    private String id;
    private String contestId;
    private String roundId;
    private String userId;
    private List<CodingQuestionObject> question;
    private double totalMarks;

}
