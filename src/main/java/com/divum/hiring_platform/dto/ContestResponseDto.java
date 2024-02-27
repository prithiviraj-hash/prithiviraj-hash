package com.divum.hiring_platform.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContestResponseDto {

    private String id;
    private String name;
    private String startTime;
    private String endTime;
}
