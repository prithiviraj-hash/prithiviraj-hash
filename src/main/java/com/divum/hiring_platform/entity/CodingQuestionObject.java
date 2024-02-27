package com.divum.hiring_platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodingQuestionObject {

    private long questionId;
    private String code;
    private String language;
    private List<TestCasesObject> testCases;
    private double score;

}
