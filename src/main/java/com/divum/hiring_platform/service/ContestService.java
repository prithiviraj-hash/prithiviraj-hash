package com.divum.hiring_platform.service;


import com.divum.hiring_platform.dto.*;
import com.divum.hiring_platform.entity.Category;
import com.divum.hiring_platform.entity.Contest;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContestService {


    ResponseEntity<ResponseDto> createContest(Contest contest) throws MessagingException;

    ResponseEntity<ResponseDto> updateContest(String contestId, Contest contest);

    ResponseEntity<ResponseDto> assignUser(String contestId, List<UserDto> users) throws MessagingException;

    ResponseEntity<ResponseDto> getAllCategory();

    ResponseEntity<ResponseDto> deleteContest(String contestId);

    ResponseEntity<ResponseDto> createCategory(Category category);

    ResponseEntity<ResponseDto> getAllContest();

    ResponseEntity<ResponseDto> getContest(String contestId);

    ResponseEntity<ResponseDto> getLiveRoundsForFilter();

    ResponseEntity<ResponseDto> getUsers(String type, String contestId, String roundId, Integer passmark);

    ResponseEntity<ResponseDto> getContestantAndEmployeeDetails(String roundId, Integer passMark);

    ResponseEntity<ResponseDto> getRoundsForHRAssign(String request);

    ResponseEntity<ResponseDto> sendEmailToTheEmployeesAboutTheInterview(String roundId, List<Long> employeeIds);

    ResponseEntity<ResponseDto> assignQuestion(String contestId) throws MessagingException;

    ResponseEntity<ResponseDto> generateInterviewSchedule(String roundId, InterviewScheduleGenerateDTO scheduleGenerateDTO);

    ResponseEntity<ResponseDto> updateTheInterviewTiming(String roundId, List<InterviewScheduleResponseDTO> interviewScheduleResponseDTO);

    ResponseEntity<ResponseDto> getRequest(Long requestId, String choice);

    ResponseEntity<ResponseDto> updateInterview(Long requestId, String decision, EmployeeAndTime employeeAndTime) throws MessagingException;

    ResponseEntity<ResponseDto> finalResult(String contestId, String roundId);
}
