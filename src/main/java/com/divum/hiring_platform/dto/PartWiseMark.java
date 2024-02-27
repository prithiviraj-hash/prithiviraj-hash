package com.divum.hiring_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartWiseMark {

    private String part;
    private Map<String, Double> difficultyWiseMarks;
    private int correctAnswerCount;

    @Override
    public String toString() {
        return "PartWiseMark{" +
                "part='" + part + '\'' +
                ", difficultyWiseMarks=" + difficultyWiseMarks +
                ", correctAnswerCount=" + correctAnswerCount +
                '}';
    }
}
