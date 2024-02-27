package com.divum.hiring_platform.dto;

import com.divum.hiring_platform.util.enums.EmployeeType;
import com.divum.hiring_platform.util.enums.Stack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePaginationDto {
    private Long employeeId;
    private String email;
    private String firstName;
    private String lastName;
    private EmployeeType employeeType;

    @Override
    public String toString() {
        return "EmployeePaginationDto{" +
                "employeeId=" + employeeId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeType=" + employeeType +
                ", stack=" + stack +
                ", yearsOfExperience=" + yearsOfExperience +
                '}';
    }

    private Stack stack;
    private int yearsOfExperience;
}
