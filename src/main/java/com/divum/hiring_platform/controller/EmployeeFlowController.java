package com.divum.hiring_platform.controller;

import com.divum.hiring_platform.api.EmployeeFlowApi;
import com.divum.hiring_platform.dto.FeedBackAndResultDto;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.service.EmployeeFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmployeeFlowController implements EmployeeFlowApi {

    private final EmployeeFlowService employeeFlowService;

    @Override
    public ResponseEntity<ResponseDto> addFeedBackAndResult( String interviewId,FeedBackAndResultDto feedBackAndResultDto) {
        return employeeFlowService.addFeedBackAndResult(interviewId,feedBackAndResultDto);
    }
}
