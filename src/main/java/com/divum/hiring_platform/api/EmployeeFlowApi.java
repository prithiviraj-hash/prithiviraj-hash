package com.divum.hiring_platform.api;

import com.divum.hiring_platform.dto.FeedBackAndResultDto;
import com.divum.hiring_platform.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/employee")
public interface EmployeeFlowApi {

    @PostMapping("/feed/{interviewId}")
    ResponseEntity<ResponseDto> addFeedBackAndResult(@PathVariable String interviewId, @RequestBody FeedBackAndResultDto feedBackAndResultDto);

}
