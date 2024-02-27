package com.divum.hiring_platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_session")
public class EmployeeSession {

    @Id
    private String sessionId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String userId;
    private String uniqueId;
}
