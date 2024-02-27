package com.divum.hiring_platform.dto;


import com.divum.hiring_platform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListAndMapObjectDTO {

    private List<User> users;
    private Map<String, String> emailAndError;
}
