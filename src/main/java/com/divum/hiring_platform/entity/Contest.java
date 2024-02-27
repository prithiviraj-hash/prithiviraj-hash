package com.divum.hiring_platform.entity;


import com.divum.hiring_platform.util.enums.ContestStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@Table(name = "contest")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String contestId;

    private String name;

    @Enumerated(EnumType.STRING)
    private ContestStatus contestStatus;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("contest")
    private List<Rounds> rounds;

    @ManyToMany(mappedBy = "contest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("contest")
    private Set<Employee> employees;

    @Override
    public String toString() {
        return "Contest{" +
                "contestId='" + contestId + '\'' +
                ", name='" + name + '\'' +
                ", contestStatus=" + contestStatus +
                ", rounds=" + rounds +
                ", employees=" + employees +
                '}';
    }

}
