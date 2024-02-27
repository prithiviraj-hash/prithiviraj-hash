package com.divum.hiring_platform.service;

import com.divum.hiring_platform.dto.FeedBackAndResultDto;
import com.divum.hiring_platform.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface EmployeeFlowService {
    ResponseEntity<ResponseDto> addFeedBackAndResult( String interviewId,FeedBackAndResultDto feedBackAndResultDto);
}
