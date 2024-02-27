package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeSubmissionDto {

    private String questionId;
    private String input;
    private String code;
    private String language;

}
