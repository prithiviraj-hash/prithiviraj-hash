package com.divum.hiring_platform.dto;

import com.divum.hiring_platform.entity.McqImageUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionsMcq {
    private String questionId;
    private List<McqImageUrl> imageUrl;
    private String question;
    private List<String> options;
    private String difficulty;
    private String type;
}
