package com.divum.hiring_platform.entity;

import com.divum.hiring_platform.util.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "coding_question")
public class CodingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private Long questionId;

    @Column(length = 1000)
    private String question;

    @Column(length = 1000)
    private String constraints;

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("codingQuestion")
    private List<CodingImageUrl> imageUrl;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("codingQuestion")
    private List<Cases> casesList;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(length = 1000)
    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("codingQuestion")
    private List<StaticCode> staticCodes;

    @Column(length = 1000)
    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("codingQuestion")
    private List<FunctionCode> functionCodes;

    @Override
    public String toString() {
        return "CodingQuestion{" +
                "questionId=" + questionId +
                ", question='" + question + '\'' +
                ", imageUrl=" + imageUrl +
                ", category=" + category +
                ", casesList=" + casesList +
                ", difficulty=" + difficulty +
                ", staticCodes=" + staticCodes +
                ", functionCodes=" + functionCodes +
                '}';
    }
}