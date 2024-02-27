package com.divum.hiring_platform.controller;


import com.divum.hiring_platform.api.ContestApi;
import com.divum.hiring_platform.dto.*;
import com.divum.hiring_platform.entity.Category;
import com.divum.hiring_platform.entity.Contest;
import com.divum.hiring_platform.service.ContestService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ContestController implements ContestApi {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @Override
    public ResponseEntity<ResponseDto> createContest(Contest contest) throws MessagingException {
        return contestService.createContest(contest);
    }

    @Override
    public ResponseEntity<ResponseDto> assignQuestionToTheContest(String contestId) throws MessagingException {
        return contestService.assignQuestion(contestId);
    }

    @Override
    public ResponseEntity<ResponseDto> updateContest(String contestId, Contest contest) {
        return contestService.updateContest(contestId, contest);
    }

    @Override
    public ResponseEntity<ResponseDto> assignUsers(String contestId, List<UserDto> users) throws MessagingException {
        return contestService.assignUser(contestId, users);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllCategory() {
        return contestService.getAllCategory();
    }

    @Override
    public ResponseEntity<ResponseDto> deleteContest(String contestId) {
        return contestService.deleteContest(contestId);
    }

    @Override
    public ResponseEntity<ResponseDto> createCategory(Category category) {
        return contestService.createCategory(category);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllContest() {
        return contestService.getAllContest();
    }

    @Override
    public ResponseEntity<ResponseDto> getContest(String contestId) {
        return contestService.getContest(contestId);
    }

    @Override
    public ResponseEntity<ResponseDto> getLiveRounds() {
        return contestService.getLiveRoundsForFilter();
    }

    @Override
    public ResponseEntity<ResponseDto> getUsers(String type,String contestId, String roundId, Integer passmark) {
        return contestService.getUsers(type, contestId, roundId, passmark);
    }

    @Override
    public ResponseEntity<ResponseDto> getContestantAndEmployeeDetails(String roundId, Integer passMark) {
        return contestService.getContestantAndEmployeeDetails(roundId, passMark);
    }

    @Override
    public ResponseEntity<ResponseDto> getRoundsForHRAssign(String request) {
        return contestService.getRoundsForHRAssign(request);
    }

    @Override
    public ResponseEntity<ResponseDto> sendEmailToTheEmployeesAboutTheInterview(String roundId, List<Long> employeeIds) {
        return contestService.sendEmailToTheEmployeesAboutTheInterview(roundId, employeeIds);
    }

    @Override
    public ResponseEntity<ResponseDto> generateInterviewSchedule(String roundId,InterviewScheduleGenerateDTO scheduleGenerateDTO) {
        return contestService.generateInterviewSchedule(roundId, scheduleGenerateDTO);
    }

    @Override
    public ResponseEntity<ResponseDto> updateTheInterviewTiming(String roundId, List<InterviewScheduleResponseDTO> interviewScheduleResponseDTO) {
        return contestService.updateTheInterviewTiming(roundId, interviewScheduleResponseDTO);
    }

    @Override
    public ResponseEntity<ResponseDto> getRequest(Long requestId,String choice) {
        return contestService.getRequest(requestId, choice);
    }

    @Override
    public ResponseEntity<ResponseDto> updateInterview(Long requestId, String decision, EmployeeAndTime employeeAndTime) throws MessagingException {
        return contestService.updateInterview(requestId, decision, employeeAndTime);
    }

    @Override
    public ResponseEntity<ResponseDto> finalResult(String contestId, String roundId) {
        return contestService.finalResult(contestId, roundId);
    }

}
