package com.divum.hiring_platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Document(collection = "codingResult")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodingResult {

    @Id
    private String id;
    private String contestId;
    private String roundId;
    private String userId;
    private List<CodingQuestionObject> question;
    private double totalMarks;

}