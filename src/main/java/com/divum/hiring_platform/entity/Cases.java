package com.divum.hiring_platform.entity;

import com.divum.hiring_platform.util.enums.CasesType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cases")
public class Cases {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long caseId;

    private String input;
    private String output;

    @Enumerated(EnumType.STRING)
    private CasesType casesType;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private CodingQuestion codingQuestion;

    @Override
    public String toString() {
        return "Cases{" +
                "caseId=" + caseId +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", casesType=" + casesType +
                ", codingQuestion=" + codingQuestion.getQuestionId() +
                '}';
    }
}
