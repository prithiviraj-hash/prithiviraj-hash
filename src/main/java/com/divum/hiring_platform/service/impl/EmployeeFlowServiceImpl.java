package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.FeedBackAndResultDto;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.entity.Interview;
import com.divum.hiring_platform.repository.InterviewRepository;
import com.divum.hiring_platform.service.EmployeeFlowService;
import com.divum.hiring_platform.util.enums.InterviewResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeFlowServiceImpl implements EmployeeFlowService {
    private final InterviewRepository interviewRepository;
    @Override
    public ResponseEntity<ResponseDto> addFeedBackAndResult(String interviewId,FeedBackAndResultDto feedBackAndResultDto) {
        Optional<Interview> interview=interviewRepository.findById(interviewId);
        if(interview.isPresent()) {
            Interview interview1 = interview.get();
            interview1.setFeedBack(feedBackAndResultDto.getFeedback());
            interview1.setInterviewResult(InterviewResult.valueOf(feedBackAndResultDto.getInterviewResult()));
            interviewRepository.save(interview1);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("feedback saved", interview1));
        }else{
            return null;
        }
    }
}
