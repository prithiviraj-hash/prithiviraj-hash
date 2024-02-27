package com.divum.hiring_platform.dto;

import com.divum.hiring_platform.util.enums.EmployeeType;
import com.divum.hiring_platform.util.enums.Role;
import com.divum.hiring_platform.util.enums.Stack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private EmployeeType employeeType;
    private Stack stack;
    private int yearsOfExperience;
}
