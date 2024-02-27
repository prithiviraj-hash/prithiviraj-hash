package com.divum.hiring_platform.dto;

import com.divum.hiring_platform.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McqDto {
    private String question;
    private List<String> options;
    private List<String> imageUrl;
    private List<String> correctOption;
    private String difficulty;
    private String category;
}
