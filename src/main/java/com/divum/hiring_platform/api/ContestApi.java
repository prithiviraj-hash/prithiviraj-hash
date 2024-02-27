package com.divum.hiring_platform.api;


import com.divum.hiring_platform.dto.*;
import com.divum.hiring_platform.entity.Category;
import com.divum.hiring_platform.entity.Contest;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contest")
public interface ContestApi {

    @PostMapping
    ResponseEntity<ResponseDto> createContest(@RequestBody Contest contest) throws MessagingException;
    @PostMapping("/{contestId}")
    ResponseEntity<ResponseDto> assignQuestionToTheContest(@PathVariable String contestId) throws MessagingException;

    @PutMapping("/{contestId}")
    ResponseEntity<ResponseDto> updateContest(@PathVariable String contestId, @RequestBody Contest contest);

    @PostMapping("{contestId}/users")
    ResponseEntity<ResponseDto> assignUsers(@PathVariable String contestId, @RequestBody List<UserDto> users) throws MessagingException;

    @GetMapping("/category")
    ResponseEntity<ResponseDto> getAllCategory();

    @DeleteMapping("/{contestId}")
    ResponseEntity<ResponseDto> deleteContest(@PathVariable String contestId);

    @PostMapping("/category")
    ResponseEntity<ResponseDto> createCategory(@RequestBody Category category);

    @GetMapping
    ResponseEntity<ResponseDto> getAllContest();

    @GetMapping("/{contestId}")
    ResponseEntity<ResponseDto> getContest(@PathVariable String contestId);

    @GetMapping("/live-rounds")
    ResponseEntity<ResponseDto> getLiveRounds();

    @GetMapping("/user")
    ResponseEntity<ResponseDto> getUsers(@RequestParam("type") String type,
                               @RequestPart(value = "contestId", required = false) String contestId,
                               @RequestPart(value = "roundId", required = false) String roundId,
                               @RequestPart(value = "passMark", required = false) Integer passMark
    );
    @GetMapping("/round/{roundId}/interview")
    ResponseEntity<ResponseDto> getContestantAndEmployeeDetails(@PathVariable String roundId, @RequestPart(value = "passMark", required = false) Integer passMark);
    @GetMapping("/round/interview/live")
    ResponseEntity<ResponseDto> getRoundsForHRAssign(@RequestParam(value = "request") String request);
    @PostMapping("/round/{roundId}/interview")
    ResponseEntity<ResponseDto> sendEmailToTheEmployeesAboutTheInterview(@PathVariable String roundId, @RequestBody List<Long> employeeIds);
    @PostMapping("/round/{roundId}/interview/schedule")
    ResponseEntity<ResponseDto> generateInterviewSchedule(@PathVariable String roundId, @RequestBody InterviewScheduleGenerateDTO scheduleGenerateDTO);
    @PutMapping("/round/{roundId}/interview/schedule")
    ResponseEntity<ResponseDto> updateTheInterviewTiming(@PathVariable String roundId, @RequestBody(required = false) List<InterviewScheduleResponseDTO> interviewScheduleResponseDTO);
    @GetMapping("/round/interview/reschedule/{requestId}")
    ResponseEntity<ResponseDto> getRequest(@PathVariable Long requestId, @RequestPart(value = "choice") String choice);
    @PutMapping("/round/interview/reschedule/{requestId}")
    ResponseEntity<ResponseDto> updateInterview(@PathVariable Long requestId, @RequestPart(value = "decision") String decision, @RequestBody EmployeeAndTime employeeAndTime) throws MessagingException;

    @GetMapping("/{contestId}/result")
    ResponseEntity<ResponseDto> finalResult(@PathVariable String contestId, @RequestPart(value = "roundId", required = false) String roundId);

}