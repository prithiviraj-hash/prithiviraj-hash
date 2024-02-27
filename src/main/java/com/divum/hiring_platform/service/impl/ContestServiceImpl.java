package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.*;
import com.divum.hiring_platform.entity.*;
import com.divum.hiring_platform.repository.service.*;
import com.divum.hiring_platform.service.ContestService;
import com.divum.hiring_platform.util.EmailSender;
import com.divum.hiring_platform.util.Scheduler;
import com.divum.hiring_platform.util.enums.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    @Value("${email.task.round.first.welcome}")
    private String welcomeEmailTimeGap;

    @Value("${email.task.round.first.start}")
    private String firstRoundStartTimeGap;

    @Value("${email.task.round.result}")
    private String resultMail;
    private static final String RESCHEDULE = "RESCHEDULE";
    private static final String PERSONAL = "Personal";
    private static final String TECHNICAL = "Technical";
    private static final String ROUND_NOT_FOUND = "Round not found";
    private static final String CONTEST_NOT_FOUND = "Contest not found";
    private static final String DD_MM_YY= "dd-MM-yy";
    private static final String HH_MM = "HH:mm";
    private static final String H_MMA = "h:mma";

    private final ContestRepositoryService contestRepositoryService;
    private final CategoryRepositoryService categoryRepositoryService;
    private final UserRepositoryService userRepositoryService;
    private final PasswordEncoder passwordEncoder;
    private final EmailTaskRepositoryService emailTaskRepositoryService;
    private final RoundsRepositoryService roundsRepositoryService;
    private final MCQResultRepositoryService mcqResultRepositoryService;
    private final CodingResultRepositoryService codingResultRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final EmployeeAvailabilityRepositoryService employeeAvailabilityRepositoryService;
    private final InterviewRepositoryService interviewRepositoryService;
    private final EmailSender emailSender;
    private final Scheduler scheduler;
    private final InterviewRequestRepositoryService interviewRequestRepositoryService;



    @Override
    public ResponseEntity<ResponseDto> createContest(Contest contest) {
        ResponseEntity<ResponseDto> responseEntity = createNewContest(contest);
        if (responseEntity != null) {
            return responseEntity;
        }
        contestRepositoryService.save(contest);
        generateEmailTask(contest.getRounds());
        try {
            assignQuestion(contest.getContestId());
        } catch (MessagingException e) {
            return ResponseEntity.ok(new ResponseDto("Questions has been assigned", null));
        }
        return ResponseEntity.ok(new ResponseDto("The contest created successfully", contest.getContestId()));
    }

    @Override
    public ResponseEntity<ResponseDto> updateContest(String contestId, Contest contest) {
        Contest existingContest = contestRepositoryService.findContestByContestId(contestId);
        if(contest == null) {
            return resourceNotFound(CONTEST_NOT_FOUND);
        }
        existingContest.setName(contest.getName());
        List<Rounds> existingContestRounds = existingContest.getRounds();
        ResponseEntity<ResponseDto> responseEntity = null;
        try {
            responseEntity = updateRounds(existingContest, contest.getRounds());
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseDto(e.getMessage(), null));
        }
        contestRepositoryService.save(existingContest);
        updateEmailTask(contest.getRounds(), existingContestRounds);

        if (responseEntity != null) {
            return responseEntity;
        }
        return ResponseEntity.ok().build();
    }

    public void updateEmailTask(List<Rounds> updatedRounds, List<Rounds> existingContestRounds) {
        for (Rounds updatedRound : updatedRounds) {
            for (Rounds existingRound : existingContestRounds) {
                if (existingRound.getId().equals(updatedRound.getId())) {
                    updateEmailTasksForRound(updatedRound, existingRound);
                    break;
                }
            }
        }
    }

    private void updateEmailTasksForRound(Rounds updatedRound, Rounds existingRound) {
        List<EmailTask> emailTasks = emailTaskRepositoryService.findAllByRounds(existingRound);

        for (EmailTask emailTask : emailTasks) {
            if (emailTask.getTaskTime().isEqual(existingRound.getStartTime().minusHours(Long.parseLong(welcomeEmailTimeGap)))) {
                updateEmailTaskTime(emailTask, updatedRound.getStartTime().minusHours(Long.parseLong(welcomeEmailTimeGap)));
            } else if (emailTask.getTaskTime().isEqual(existingRound.getStartTime().minusMinutes(Long.parseLong(firstRoundStartTimeGap)))) {
                updateEmailTaskTime(emailTask, updatedRound.getStartTime().minusMinutes(Long.parseLong(firstRoundStartTimeGap)));
            } else {
                updateEmailTaskTime(emailTask, updatedRound.getEndTime().plusMinutes(Long.parseLong(resultMail)));
            }
        }
        emailTaskRepositoryService.saveAll(emailTasks);
    }

    private void updateEmailTaskTime(EmailTask emailTask, LocalDateTime newTaskTime) {
        emailTask.setTaskTime(newTaskTime);
    }

    @Override
    public ResponseEntity<ResponseDto> assignUser(String contestId, List<UserDto> users) {
        Contest contest;
        try {
            contest = contestRepositoryService.findContestByContestId(contestId);
            if(contest == null) {
                throw new ResourceNotFoundException(CONTEST_NOT_FOUND);
            }
        } catch (Exception e) {
            return resourceNotFound(e.getMessage());
        }
        return assignUserUsingObjects(contest, users);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllCategory() {
        return ResponseEntity.ok(new ResponseDto("The available categories", categoryRepositoryService.findAll()));
    }

    @Override
    public ResponseEntity<ResponseDto> deleteContest(String contestId) {
        try {
            Contest contest = contestRepositoryService.findContestByContestId(contestId);
            if(contest == null) {
                throw new ResourceNotFoundException(CONTEST_NOT_FOUND);
            }
            List<User> users = contestRepositoryService.findUsersAssignedToTheContest(contestId);
            for (User user : users) {
                Set<Contest> contests = user.getContest();
                contests.removeIf(c -> c.getContestId().equals(contestId));
            }
            contestRepositoryService.save(contest);
            contestRepositoryService.deleteByContestId(contestId);
            return ResponseEntity.ok(new ResponseDto("Contest with ID " + contestId + " deleted successfully.", null));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto("An error occurred while deleting the contest.", null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> createCategory(Category category) {
        try {
            categoryRepositoryService.save(category);
            return ResponseEntity.ok(new ResponseDto("Category added successfully", category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getAllContest() {
        List<Contest> contests = contestRepositoryService.findAll();
        List<EssentialContestDetails> contestDetails = new ArrayList<>();
        for(Contest contest : contests) {
            EssentialContestDetails details = new EssentialContestDetails();
            details.setContestId(contest.getContestId());
            details.setName(contest.getName());
            details.setContestStatus(contest.getContestStatus());
            details.setStartDate(contest.getRounds().get(0).getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yy")));
            if(contest.getContestStatus() == ContestStatus.COMPLETED){
                details.setEndDate(contest.getRounds().get(contest.getRounds().size() - 1).getEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            contestDetails.add(details);
        }
        return ResponseEntity.ok(new ResponseDto("Contest log", contestDetails));
    }

    @Override
    public ResponseEntity<ResponseDto> getContest(String contestId) {
        Contest contest = contestRepositoryService.findContestByContestId(contestId);
        if (contest != null) {
            return ResponseEntity.ok(new ResponseDto("The contest with the id " + contestId, contest));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getLiveRoundsForFilter() {
        List<RecentlyCompletedRoundDTO> recentlyCompletedRounds = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();

        List<Contest> contestList = contestRepositoryService.findContestsByContestStatus(ContestStatus.CURRENT);
        for (Contest contest : contestList) {
            Rounds recentlyCompletedRound = findRecentlyCompletedRound(contest, currentTime);
            if (recentlyCompletedRound != null) {
                RecentlyCompletedRoundDTO dto = createRecentlyCompletedRoundDTO(contest, recentlyCompletedRound);
                recentlyCompletedRounds.add(dto);
            }
        }

        return ResponseEntity.ok(new ResponseDto("Recently completed rounds are ", recentlyCompletedRounds));
    }

    private Rounds findRecentlyCompletedRound(Contest contest, LocalDateTime currentTime) {
        for (Rounds round : contest.getRounds()) {
            if (isRecentlyCompleted(round, currentTime)) {
                return round;
            }
        }
        return null;
    }

    private boolean isRecentlyCompleted(Rounds round, LocalDateTime currentTime) {
        return round.getRoundType() == RoundType.CODING || round.getRoundType() == RoundType.MCQ &&
                round.getEndTime().isBefore(currentTime) && round.getEndTime().isBefore(currentTime.plusMinutes(30));
    }

    private RecentlyCompletedRoundDTO createRecentlyCompletedRoundDTO(Contest contest, Rounds round) {
        RecentlyCompletedRoundDTO dto = new RecentlyCompletedRoundDTO();
        dto.setRoundId(round.getId());
        dto.setContestName(contest.getName());
        dto.setRoundNumber(round.getRoundNumber());
        dto.setRoundCompletedTime(round.getEndTime().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")));
        dto.setStatus(round.getEndTime().isBefore(LocalDateTime.now().minusMinutes(30)) ? "Published" : "Pending");
        return dto;
    }


    @Override
    public ResponseEntity<ResponseDto> getUsers(String type, String contestId, String roundId, Integer passmark) {
        return switch (type) {
            case "ROUND_FILTER" -> getUsersForRoundFilter(roundId, passmark);
            case "CONTEST_RESULT" -> getUsersForFinalResult(contestId);

            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Error occurred while fetching details", null));
        };
    }

    @Override
    public ResponseEntity<ResponseDto> getContestantAndEmployeeDetails(String roundId, Integer passMark) {
        Rounds rounds;
        try {
            rounds = roundsRepositoryService.findById(roundId).orElseThrow(() -> new ResourceNotFoundException(CONTEST_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        if(rounds.getStartTime() == null) {
            Rounds previousRound = getPreviousRound(rounds.getContest(), rounds);
            if(previousRound != null) {
                if(passMark != null) {
                    previousRound.setPass(passMark);
                    roundsRepositoryService.save(previousRound);
                }
                return getDetailsToConductInterview(rounds, previousRound);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("There are no previous round for this interview", null));
            }
        }
        else {
            return getLiveInterviewRound(roundId);
        }
    }

    private ResponseEntity<ResponseDto> getLiveInterviewRound(String roundId) {
        List<Interview> interviews = interviewRepositoryService.findInterviewsByRoundsId(roundId);
        try {
            if(interviews == null) {
                throw new ResourceNotFoundException("No interview with this round");
            }
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        List<InterviewScheduleResponseDTO> response = buildInterviewResponse(interviews);
        return ResponseEntity.ok(new ResponseDto("Scheduled Interview list", response));
    }

    @Override
    public ResponseEntity<ResponseDto> getRoundsForHRAssign(String request) {
        List<Contest> contests = contestRepositoryService.findContestsByContestStatus(ContestStatus.CURRENT);
        if (contests == null) {
            return ResponseEntity.ok(new ResponseDto("0 live contest found", null));
        }
        if(request.equals("INTERVIEW")) {
            return getInterviewDetails(contests);
        } else if (request.equals(RESCHEDULE)) {
            return getRescheduleRequest(contests);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Not a valid request", null));
    }

    private ResponseEntity<ResponseDto> getRescheduleRequest(List<Contest> contests) {
        List<Rounds> rounds = new ArrayList<>();
        for (Contest contest : contests) {
            rounds.addAll(contest.getRounds());
        }
        rounds.removeIf(round -> round.getRoundType() == RoundType.MCQ || round.getRoundType() == RoundType.CODING);
        List<InterviewRequest> requests = new ArrayList<>();
        for(Rounds round : rounds) {
            requests.addAll(interviewRequestRepositoryService.findCurrentInterviewRequest(round.getId()));
        }
        List<InterviewRequestLog> requestLogs = new ArrayList<>();

        for(InterviewRequest request : requests) {

            InterviewRequestLog requestLog = new InterviewRequestLog();
            Rounds interviewRound = request.getInterview().getRounds();
            String roundType = getInterviewType(interviewRound.getRoundType());
            String round = interviewRound.getRoundNumber() + " - " + roundType;
            requestLog.setRound(round);
            requestLog.setIntervieweeName(request.getInterview().getUser().getName());
            requestLog.setId(request.getId());
            requestLog.setContestName(interviewRound.getContest().getName());
            requestLog.setEmployeeName(request.getInterview().getEmployee().getFirstName() + " " + request.getInterview().getEmployee().getLastName());
            requestLog.setRequestType(String.valueOf(request.getInterviewRequestType()));
            requestLog.setStatus(String.valueOf(request.getInterviewRequestStatus()));
            requestLog.setReason(request.getReason());
            requestLog.setAssignedTime(request.getInterview().getInterviewTime().format(DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm")));
            requestLog.setPreferredTime(request.getPreferredTime().format(DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm")));
            requestLogs.add(requestLog);
        }

        return ResponseEntity.ok(new ResponseDto("Reschedule requests", requestLogs));
    }

    private ResponseEntity<ResponseDto> getInterviewDetails(List<Contest> contests) {
        List<HrAssignDTO> assignDTOList = new ArrayList<>();
        for (Contest contest : contests) {
            List<Rounds> rounds = contest.getRounds();
            LocalDateTime contestStartTime = findContestStartTime(rounds);

            for (Rounds round : rounds) {
                if (isInterviewRoundWithoutEndTime(round)) {
                    assert contestStartTime != null;
                    HrAssignDTO assignDTO = createHrAssignDTO(contest, round, contestStartTime);
                    assignDTOList.add(assignDTO);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("current/upcoming interview rounds", assignDTOList));
    }

    private LocalDateTime findContestStartTime(List<Rounds> rounds) {
        for (Rounds round : rounds) {
            if (round.getRoundNumber() == 1) {
                return round.getStartTime();
            }
        }
        return null;
    }

    private boolean isInterviewRoundWithoutEndTime(Rounds round) {
        return (round.getRoundType() == RoundType.PERSONAL_INTERVIEW || round.getRoundType() == RoundType.TECHNICAL_INTERVIEW)
                && round.getEndTime() == null;
    }

    private HrAssignDTO createHrAssignDTO(Contest contest, Rounds round, LocalDateTime contestStartTime) {
        HrAssignDTO assignDTO = new HrAssignDTO();
        assignDTO.setContestName(contest.getName());
        assignDTO.setStatus(round.getStartTime() == null ? "Pending" : "Assigned");
        assignDTO.setRoundNumber(round.getRoundNumber());
        assert contestStartTime != null;
        assignDTO.setRoundType(getInterviewType(round.getRoundType()));
        assignDTO.setContestDate(contestStartTime.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")));
        assignDTO.setRoundId(round.getId());
        return assignDTO;
    }


    @Override
    public ResponseEntity<ResponseDto> sendEmailToTheEmployeesAboutTheInterview(String roundId, List<Long> employeeIds) {
        Rounds rounds;
        try {
            rounds = roundsRepositoryService.findById(roundId)
                    .orElseThrow(() -> new ResourceNotFoundException(ROUND_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        try {
            List<Employee> employees = new ArrayList<>();
            Contest contest = rounds.getContest();
            for(Long employeeId : employeeIds) {
                employees.add(employeeRepositoryService.findEmployeeByEmployeeId(employeeId));
            }
            List<EmployeeAvailability> employeeAvailabilities = new ArrayList<>();
            for(Employee employee : employees) {
                EmployeeAvailability employeeAvailability = new EmployeeAvailability();
                employeeAvailability.setEmployeeAndContest(new EmployeeAndContest(employee, contest));
                employeeAvailability.setResponse(EmployeeResponse.PENDING);
                employeeAvailabilities.add(employeeAvailability);
            }
            emailSender.notifyAboutOneOnOne(employeeAvailabilities);
            employeeAvailabilityRepositoryService.saveAll(employeeAvailabilities);
            return ResponseEntity.ok(new ResponseDto("Email sent to the employees ", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDto(e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> assignQuestion(String contestId) throws MessagingException {
        Contest contest;
        try {
            contest = contestRepositoryService.findContestByContestId(contestId);
            if(contest == null) {
                throw new ResourceNotFoundException(CONTEST_NOT_FOUND);
            }
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        scheduler.assignQuestion(contest);
        return ResponseEntity.ok(new ResponseDto("The question has been assigned to the contest", null));
    }

    @Override
    public ResponseEntity<ResponseDto> generateInterviewSchedule(String roundId, InterviewScheduleGenerateDTO scheduleGenerateDTO) {
        Rounds rounds;
        try {
            rounds = roundsRepositoryService.findById(roundId).
                    orElseThrow(() -> new ResourceNotFoundException(ROUND_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        rounds.setStartTime(scheduleGenerateDTO.getStartTime());
        Contest contest = rounds.getContest();
        EmployeeType employeeType;
        if(RoundType.PERSONAL_INTERVIEW == rounds.getRoundType()) {
            employeeType = EmployeeType.PERSONAL_HR;
        } else {
            employeeType = EmployeeType.TECHNICAL_HR;
        }
        List<Employee> employees = new ArrayList<>();
        for(Long employeeId : scheduleGenerateDTO.getEmployeeId()) {
            Employee employee = employeeAvailabilityRepositoryService.findEmployeesWhoIsAvailable(employeeId, contest, employeeType);
            employees.add(employee);
        }
        List<User> users = contestRepositoryService.findPassedStudents(contest);
        List<Interview> interviews = generateInterviews(rounds.getStartTime(), scheduleGenerateDTO.getDuration(), users, employees, rounds);
        interviewRepositoryService.saveAll(interviews);
        List<InterviewScheduleResponseDTO> response = buildInterviewResponse(interviews);
        return ResponseEntity.ok(new ResponseDto("Interview assigned", response));
    }

    @Override
    public ResponseEntity<ResponseDto> updateTheInterviewTiming(String roundId, List<InterviewScheduleResponseDTO> interviewScheduleResponseDTO) {
        List<InterviewScheduleResponseDTO> updated = new ArrayList<>();
        for(InterviewScheduleResponseDTO response : interviewScheduleResponseDTO) {
            Interview interview = null;
            try {
                interview = interviewRepositoryService.findById(response.getInterviewId())
                        .orElseThrow(() -> new ResourceNotFoundException("Interview with id " + response.getInterviewId() + " found"));
            } catch (ResourceNotFoundException e) {
                resourceNotFound(e.getMessage());
            }
            try {
                assert interview != null;
                String interviewDateStr = response.getInterviewDate();
                String interviewTimeStr = response.getInterviewTime();

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DD_MM_YY);
                LocalDate interviewDate = LocalDate.parse(interviewDateStr, dateFormatter);

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(HH_MM);
                LocalTime interviewTime = LocalTime.parse(interviewTimeStr, timeFormatter);

                LocalDateTime interviewDateTime = interviewDate.atTime(interviewTime);
                interview.setInterviewTime(interviewDateTime);
                interviewRepositoryService.save(interview);

                response.setInterviewTime(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(DD_MM_YY)));
                response.setInterviewDate(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(HH_MM)));
                updated.add(response);
                ResponseEntity<ResponseDto> entity = sendMail(interview);
                if(entity != null) {
                    return entity;
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("The date and time is not in the required format", null));
            }
        }
        return ResponseEntity.ok(new ResponseDto("Interview timings updated", updated));
    }

    @Override
    public ResponseEntity<ResponseDto> getRequest(Long requestId, String choice) {
        InterviewRequest rescheduleRequest;
        try {
            rescheduleRequest = interviewRequestRepositoryService.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("No request found with id " + requestId));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        IndividualInterviewRequest request = new IndividualInterviewRequest();
        ContestDetails contestDetails = new ContestDetails();
        Rounds rounds = rescheduleRequest.getInterview().getRounds();
        contestDetails.setContestName(rounds.getContest().getName());
        contestDetails.setRoundNumber(rounds.getRoundNumber());
        contestDetails.setInterviewDate(rescheduleRequest.getInterview().getInterviewTime().format(DateTimeFormatter.ofPattern(DD_MM_YY)));
        contestDetails.setInterviewTime(rescheduleRequest.getInterview().getInterviewTime().format(DateTimeFormatter.ofPattern(H_MMA)));
        contestDetails.setRoundType(getInterviewType(rounds.getRoundType()));
        request.setContestDetails(contestDetails);

        if(choice.equals(RESCHEDULE)) {
            EmployeeDetails employeeDetails = new EmployeeDetails();
            String name = rescheduleRequest.getInterview().getEmployee().getFirstName() + " " + rescheduleRequest.getInterview().getEmployee().getLastName();
            employeeDetails.setName(name);
            employeeDetails.setPreferredTime(rescheduleRequest.getPreferredTime().format(DateTimeFormatter.ofPattern(H_MMA)));
            employeeDetails.setPreferredDate(rescheduleRequest.getPreferredTime().format(DateTimeFormatter.ofPattern(DD_MM_YY)));
            request.setEmployeeDetails(employeeDetails);

            List<Interview> interviews = interviewRepositoryService.findInterviewsByEmployee(rescheduleRequest.getInterview().getEmployee());
            List<ScheduleDetails> scheduleDetails = new ArrayList<>();
            for(Interview interview : interviews) {
                ScheduleDetails scheduleDetail = new ScheduleDetails();
                scheduleDetail.setIntervieweeName(interview.getUser().getName());
                scheduleDetail.setDate(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(DD_MM_YY)));
                scheduleDetail.setTime(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(H_MMA)));
                scheduleDetails.add(scheduleDetail);
            }
            request.setScheduleDetails(scheduleDetails);
        } else if (choice.equals("REASSIGN")) {
            List<InterviewAssignEmployee> employees = getEmployeeForInterview(rounds);
            request.setEmployees(employees);
        }

        return ResponseEntity.ok(new ResponseDto("interview request details", request));
    }

    private String getInterviewType(RoundType roundType) {
        return roundType == RoundType.PERSONAL_INTERVIEW ? PERSONAL : TECHNICAL;
    }

    @Override
    public ResponseEntity<ResponseDto> updateInterview(Long requestId, String decision, EmployeeAndTime employeeAndTime) throws MessagingException {
        InterviewRequest interviewRequest;
        try {
            interviewRequest = interviewRequestRepositoryService.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        Interview interview = interviewRequest.getInterview();
        switch (decision) {
            case RESCHEDULE -> {
                interview.setInterviewTime(employeeAndTime.getInterviewTime());
                interviewRequest.setInterviewRequestStatus(InterviewRequestStatus.RESCHEDULED);
                //send email to the contestant and the employee about reschedule

                emailSender.sendEmailToTheContestantAndEmployeeAboutTheReschedule(interview, interviewRequest.getInterviewRequestType());
            }
            case "REASSIGN" -> {
                Employee employee = employeeRepositoryService.findEmployeeByEmployeeId(employeeAndTime.getEmployeeId());
                interview.setEmployee(employee);
                interviewRequest.setInterviewRequestStatus(InterviewRequestStatus.REASSIGNED);
                //send email to the employee about reassign
            }
            case "REJECTED" -> {
                interviewRequest.setInterviewRequestStatus(InterviewRequestStatus.REJECTED);
                //send mail to the employee
                emailSender.sendEmailToEmployeeAboutRejection(interviewRequest);
            }

            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Not a valid decision", null));
            }
        }
        interviewRequestRepositoryService.save(interviewRequest);
        interviewRepositoryService.save(interview);
        return ResponseEntity.ok(new ResponseDto("Interview updated", null));
    }

    @Override
    public ResponseEntity<ResponseDto> finalResult(String contestId, String roundId) {
        Contest contest;
        try {
            contest = contestRepositoryService.findContestByContestId(contestId);
            if(contest == null) {
                throw new ResourceNotFoundException(CONTEST_NOT_FOUND);
            }
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        List<Rounds> rounds = contest.getRounds();
        if(roundId == null) {
            return getPrimaryRoundResultDetails(rounds, contest);
        } else {
            return getRoundWiseResult(roundId);
        }
    }

    private ResponseEntity<ResponseDto> getRoundWiseResult(String roundId) {
        Rounds round;
        try {
            round = roundsRepositoryService.findById(roundId)
                    .orElseThrow(() -> new ResourceNotFoundException(ROUND_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }
        int count = 0;
        RoundResult result = new RoundResult();
        result.setPassPercentage(round.getPass());
        result.setInterviewResults(null);
        List<PartWiseMarkAllocation> markAllocations = new ArrayList<>();
        List<InterviewResultDetails> interviewResults = new ArrayList<>();
        switch (round.getRoundType()) {
            case MCQ -> {
                result.setRoundType("MCQ");
                count = mcqResultRepositoryService.countByRoundId(roundId);
                result.setParticipantsCount(count);
                getPartWiserMark(round, markAllocations);
                result.setPassPercentage(round.getPass());
                result.setPartWiseMarkAllocations(markAllocations);
            }
            case CODING -> {
                result.setRoundType("Coding");
                count = codingResultRepositoryService.countByRoundId(roundId);
                result.setParticipantsCount(count);
                getPartWiserMark(round, markAllocations);
                result.setPassPercentage(round.getPass());
                result.setPartWiseMarkAllocations(markAllocations);
            }
            case TECHNICAL_INTERVIEW -> {
                result.setRoundType("Technical Interview");
                count = interviewRepositoryService.countByRoundsId(roundId);
                result.setParticipantsCount(count);
                result.setPassPercentage(null);
                result.setPartWiseMarkAllocations(null);
                getInterviewResult(round, interviewResults);
                result.setInterviewResults(interviewResults);
            }
            case PERSONAL_INTERVIEW -> {
                result.setRoundType("Personal Interview");
                count = interviewRepositoryService.countByRoundsId(roundId);
                result.setParticipantsCount(count);
                result.setPassPercentage(null);
                result.setPartWiseMarkAllocations(null);
                getInterviewResult(round, interviewResults);
                result.setInterviewResults(interviewResults);
            }
        }
        if(round.getEndTime() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("The round is not ended",null));
        }
        LocalDateTime startTime = round.getStartTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM,dd,yy-hh:mm");
        String startingTime = startTime.format(formatter);
        String endingTime = round.getEndTime().format(formatter);

        result.setRoundRange(startingTime + " -- " + endingTime);
        result.setRoundNumber(String.valueOf(round.getRoundNumber()));
        return ResponseEntity.ok(new ResponseDto("Round wise result", result));
    }

    private void getInterviewResult(Rounds round, List<InterviewResultDetails> interviewResults) {
        List<Interview> interviews = interviewRepositoryService.findInterviewsByRoundsId(round.getId());
        for(Interview interview : interviews) {
            InterviewResultDetails interviewResult = new InterviewResultDetails();
            interviewResult.setContestant(interview.getUser().getName());
            interviewResult.setEmployee(interview.getEmployee().getFirstName() + " " + interview.getEmployee().getLastName());
            interviewResult.setCollageName(interview.getUser().getName());
            interviewResult.setEmail(interview.getUser().getEmail());
            interviewResults.add(interviewResult);
        }
    }

    private void getPartWiserMark(Rounds round, List<PartWiseMarkAllocation> markAllocations) {
        for(Part part : round.getParts()) {
            PartWiseMarkAllocation markAllocation = new PartWiseMarkAllocation();
            markAllocation.setAllocatedTime(part.getAssignedTime());
            markAllocation.setPart(part.getCategory().getQuestionCategory().toString().split("_")[0]);
            List<DifficultyAndCount> difficultyAndCountList = new ArrayList<>();
            getDifficultyWiseMarkAndTime(difficultyAndCountList, part.getEasy(), "Easy");
            getDifficultyWiseMarkAndTime(difficultyAndCountList, part.getMedium(), "Medium");
            getDifficultyWiseMarkAndTime(difficultyAndCountList, part.getHard(), "Hard");
            markAllocation.setDifficultyAndCountList(difficultyAndCountList);
            markAllocations.add(markAllocation);
        }
    }

    private void getDifficultyWiseMarkAndTime(List<DifficultyAndCount> difficultyAndCountList, int count, String difficulty) {
        DifficultyAndCount difficultyAndCount = new DifficultyAndCount(difficulty, count);
        difficultyAndCountList.add(difficultyAndCount);
    }

    private ResponseEntity<ResponseDto> getPrimaryRoundResultDetails(List<Rounds> rounds, Contest contest) {
        ContestResult result = new ContestResult();

        Rounds firstRound = rounds.stream().filter(r -> r.getRoundNumber() == 1).findFirst().orElse(null);
        Rounds lastRound = rounds.stream().filter(r -> r.getRoundNumber() == rounds.size() - 1).findFirst().orElse(null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM,dd,yyyy");
        if(firstRound == null || lastRound == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDto("Error occurred while fetching rounds", null));
        }
        String formattedStartDate = firstRound.getStartTime().format(formatter);
        String formattedEndDate = lastRound.getEndTime().format(formatter);
        result.setContestDate(formattedStartDate + " - " + formattedEndDate);
        result.setContestName(contest.getName());
        Long count = userRepositoryService.countUsersByContest(contest);
        result.setParticipantCount(count.intValue());
        Integer roundCount = 0;
        List<RoundList> roundLists = new ArrayList<>();
        for(Rounds round : contest.getRounds()) {
            roundCount++;
            RoundList roundList = new RoundList();
            roundList.setRound("Round " + round.getRoundNumber());
            String roundType = round.getRoundType() == RoundType.MCQ || round.getRoundType() == RoundType.CODING ? "Test" : "Interview" ;
            roundList.setRoundType(roundType);
            roundList.setRoundId(round.getId());
            roundLists.add(roundList);
        }
        result.setTotalRoundCount(roundCount);
        result.setRoundLists(roundLists);
        return ResponseEntity.ok(new ResponseDto("Contest result", result));
    }


    private ResponseEntity<ResponseDto> sendMail(Interview interview) {
        try {
            sendEmailAboutTheInterview(interview.getRounds().getId());
            return null;
        } catch (Exception e) {
            return resourceNotFound(e.getMessage());
        }
    }

    private void sendEmailAboutTheInterview(String roundId) {
        List<Interview> interviews = interviewRepositoryService.findInterviewsByRoundsId(roundId);
        Map<Employee, Map<User, LocalDateTime>> employeeInterviewMap = new HashMap<>();

        for (Interview interview : interviews) {
            Employee employee = interview.getEmployee();
            User user = interview.getUser();
            LocalDateTime interviewTime = interview.getInterviewTime();
            employeeInterviewMap.computeIfAbsent(employee, k -> new HashMap<>());
            employeeInterviewMap.get(employee).put(user, interviewTime);
        }

        List<EmployeeInterviewScheduleMail> mails = new ArrayList<>();
        for (Map.Entry<Employee, Map<User, LocalDateTime>> entry : employeeInterviewMap.entrySet()) {
            Employee employee = entry.getKey();
            Map<User, LocalDateTime> userInterviewMap = entry.getValue();
            EmployeeInterviewScheduleMail mail = new EmployeeInterviewScheduleMail(employee, userInterviewMap);
            mails.add(mail);
        }
        emailSender.sendEmailAboutTheInterview(mails);
    }


    private List<InterviewScheduleResponseDTO> buildInterviewResponse(List<Interview> interviews) {
        List<InterviewScheduleResponseDTO> responseDTOList = new ArrayList<>();
        for(Interview interview : interviews) {
            InterviewScheduleResponseDTO response = new InterviewScheduleResponseDTO();
            response.setInterviewId(interview.getInterviewId());
            String name = interview.getEmployee().getFirstName() + " " + interview.getEmployee().getLastName();
            response.setInterviewer(name);
            response.setInterviewDate(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(DD_MM_YY)));
            response.setInterviewTime(interview.getInterviewTime().format(DateTimeFormatter.ofPattern(HH_MM)));
            response.setInterviewee(interview.getUser().getName());
            response.setCollageName(interview.getUser().getCollegeName());
            responseDTOList.add(response);
        }
        return responseDTOList;
    }

    public List<Interview> generateInterviews(LocalDateTime startTime, int durationMinutes, List<User> users, List<Employee> employees, Rounds round) {
        List<Interview> interviews = new ArrayList<>();
        if (users.isEmpty() || employees.isEmpty()) {
            return interviews;
        }

        int totalUsers = users.size();
        int totalEmployees = employees.size();
        int usersPerEmployee = totalUsers / totalEmployees;

        int remainingUsers = totalUsers % totalEmployees;

        int[] usersAssignedPerEmployee = new int[totalEmployees];
        Arrays.fill(usersAssignedPerEmployee, usersPerEmployee);

        for (int i = 0; i < remainingUsers; i++) {
            usersAssignedPerEmployee[i]++;
        }

        LocalDateTime interviewStartTime = startTime;
        Employee previousEmployee = null;
        for (int i = 0; i < totalEmployees; i++) {
            Employee employee = employees.get(i);
            if(previousEmployee != null && previousEmployee != employee) {
                interviewStartTime = startTime;
            }
            Iterator<User> userIterator = users.iterator();
            for (int j = 0; j < usersAssignedPerEmployee[i] && userIterator.hasNext(); j++) {
                User user = userIterator.next();
                userIterator.remove();

                Interview interview = new Interview();
                interview.setInterviewTime(interviewStartTime);
                interview.setUser(user);
                interview.setEmployee(employee);
                interview.setRounds(round);
                interviews.add(interview);
                interviewStartTime = interviewStartTime.plusMinutes(durationMinutes);
                previousEmployee = employee;
            }

        }
        return interviews;
    }




    private ResponseEntity<ResponseDto> getDetailsToConductInterview(Rounds currentRound, Rounds previousRound) {
        try {
            Contest contest = currentRound.getContest();
            InterviewAssignDTO assignDTO = new InterviewAssignDTO();
            assignDTO.setId(currentRound.getId());
            assignDTO.setContestName(contest.getName());
            assignDTO.setRoundsNumber(currentRound.getRoundNumber());
            if(currentRound.getRoundType() == RoundType.PERSONAL_INTERVIEW) {
                assignDTO.setRound(PERSONAL);
            } else if (currentRound.getRoundType() == RoundType.TECHNICAL_INTERVIEW) {
                assignDTO.setRound(TECHNICAL);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Not a valid round", null));
            }
            assert previousRound != null;
            if(previousRound.getRoundType() == RoundType.MCQ || previousRound.getRoundType() == RoundType.CODING) {
                getPreviousRoundResult(previousRound, assignDTO);
            } else {
                getPreviousInterviewRoundResult(previousRound, assignDTO);
            }
            List<InterviewAssignEmployee> employees = getEmployeeForInterview(currentRound);
            assignDTO.setEmployees(employees);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Previous rounds passed contestants and employee details", assignDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(e.getMessage(), null));
        }
    }

    private void getPreviousInterviewRoundResult(Rounds previousRound, InterviewAssignDTO assignDTO) {
        List<Interview> interviews = interviewRepositoryService.findInterviewsByRoundsId(previousRound.getId());
        List<InterviewAssignContestant> contestants = new ArrayList<>();
        for(Interview interview : interviews) {
            User user = interview.getUser();
            if(interview.getInterviewResult() == InterviewResult.SELECTED || interview.getInterviewResult() == InterviewResult.CAN_BE_CONSIDERATE) {
                InterviewAssignContestant contestant = new InterviewAssignContestant();
                contestant.setName(user.getName());
                contestant.setEmail(user.getEmail());
                contestant.setFeedback(interview.getFeedBack());
                contestants.add(contestant);
                user.setPassed(true);
            } else {
                user.setPassed(false);
            }
            userRepositoryService.save(user);
        }
        assignDTO.setContestants(contestants);
    }

    private List<InterviewAssignEmployee> getEmployeeForInterview(Rounds currentRound) {
        EmployeeType employeeType = currentRound.getRoundType() == RoundType.TECHNICAL_INTERVIEW ? EmployeeType.TECHNICAL_HR : EmployeeType.PERSONAL_HR;
        List<Employee> employees = employeeRepositoryService.findEmployeesByEmployeeType(employeeType);
        employees.removeIf(employee -> employee.getRole() == Role.ADMIN);
        List<EmployeeAvailability> employeeAndContests = employeeAvailabilityRepositoryService.findEmployeeAvailabilitiesByContest(currentRound.getContest().getContestId());
        Map<Employee, EmployeeResponse> employeeAndDecision = new HashMap<>();
        List<InterviewAssignEmployee> interviewAssignEmployees = new ArrayList<>();
        for(EmployeeAvailability employeeAvailability: employeeAndContests) {
            employeeAndDecision.put(employeeAvailability.getEmployeeAndContest().getEmployee(), employeeAvailability.getResponse());
        }
        for(Employee employee : employees) {
            InterviewAssignEmployee assignEmployee = new InterviewAssignEmployee();
            assignEmployee.setId(employee.getEmployeeId());
            assignEmployee.setName(employee.getFirstName() + " " + employee.getLastName());
            assignEmployee.setEmail(employee.getEmail());
            if(employeeAndDecision.containsKey(employee)) {
                assignEmployee.setStatus(String.valueOf(employeeAndDecision.get(employee)));
            } else {
                assignEmployee.setStatus("Not assigned");
            }
            interviewAssignEmployees.add(assignEmployee);
        }
        return interviewAssignEmployees;
    }

    private void getPreviousRoundResult(Rounds previousRound, InterviewAssignDTO assignDTO) {
        List<InterviewAssignContestant> contestants = new ArrayList<>();
        if(previousRound.getRoundType() == RoundType.MCQ) {
            List<MCQResult> mcqResults = mcqResultRepositoryService.findMCQResultsByRoundId(previousRound.getId());
            for(MCQResult mcqResult : mcqResults) {
                InterviewAssignContestant contestant = new InterviewAssignContestant();
                User user = userRepositoryService.findUserByUserId(mcqResult.getUserId());
                contestant.setEmail(user.getEmail());
                contestant.setName(user.getName());
                contestant.setScore(mcqResult.getTotalPercentage());
                contestants.add(contestant);
            }
        } else {
            List<CodingResult> results = codingResultRepositoryService.findCodingResultsByRoundId(previousRound.getId());
            for(CodingResult result : results) {
                InterviewAssignContestant contestant = new InterviewAssignContestant();
                User user = userRepositoryService.findUserByUserId(result.getUserId());
                contestant.setName(user.getName());
                contestant.setEmail(user.getEmail());
                contestant.setScore(result.getTotalMarks());
                contestants.add(contestant);
            }
        }
        assignDTO.setContestants(contestants);
    }

    private Rounds getPreviousRound(Contest contest, Rounds currentRound) {
        for(Rounds round : contest.getRounds()) {
            if(currentRound.getRoundNumber() == round.getRoundNumber() + 1) {
                return round;
            }
        }
        return null;
    }

    private ResponseEntity<ResponseDto> getUsersForFinalResult(String contestId) {
        return null;
    }

    private ResponseEntity<ResponseDto> getUsersForRoundFilter(String roundId, Integer passmark) {
        try {
            Rounds round = roundsRepositoryService.findById(roundId).
                    orElseThrow(() -> new ResourceNotFoundException(ROUND_NOT_FOUND));

            if(passmark != null) {
                round.setPass(passmark);
            }

            List<MCQResult> mcqResults = mcqResultRepositoryService.findMCQResultsByRoundId(roundId);
            RoundFilterContestantDTO filterContestantDTO = new RoundFilterContestantDTO();
            filterContestantDTO.setContestName(round.getContest().getName());
            filterContestantDTO.setRoundType(String.valueOf(round.getRoundType()));
            filterContestantDTO.setRoundNumber(round.getRoundNumber());

            List<Contestants> contestants = new ArrayList<>();

            for(MCQResult mcqResult : mcqResults) {
                User user = userRepositoryService.findUserByUserId(mcqResult.getUserId());
                if(mcqResult.getTotalPercentage() >= round.getPass()) {
                    buildContestants(mcqResult, user, contestants);
                    user.setPassed(true);
                } else {
                    user.setPassed(false);
                }
                userRepositoryService.save(user);
            }
            roundsRepositoryService.save(round);
            filterContestantDTO.setContestantLists(contestants);
            return ResponseEntity.ok(new ResponseDto("Listed contestants for the round", filterContestantDTO));
        } catch (ResourceNotFoundException e) {
            return resourceNotFound(e.getMessage());
        }

    }

    private static void buildContestants(MCQResult mcqResult, User user, List<Contestants> contestants) {
        Contestants contestant = new Contestants();
        contestant.setEmail(user.getEmail());
        contestant.setScore(mcqResult.getTotalPercentage());
        contestant.setName(user.getName());
        contestant.setCollegeName(user.getCollegeName());
        contestants.add(contestant);
    }


    public ResponseEntity<ResponseDto> assignUserUsingObjects(Contest contest, List<UserDto> userDTOs) {
        List<String> errorList = new ArrayList<>();
        for (UserDto userDto : userDTOs) {
            if (isExistingUser(userDto.getEmail())) {
                assignUserToTheContest(userDto.getEmail(), contest, errorList);
            } else {
                User newUser = createUserFromDto(userDto, contest, passwordEncoder);
                userRepositoryService.save(newUser);
            }
        }
        if (!errorList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("The users with the following email id are already in a live or upcoming contest", errorList));
        }

        return ResponseEntity.ok(new ResponseDto("Users are assigned to the contest", null));
    }

    public boolean isExistingUser(String email) {
        return userRepositoryService.existsUserByEmail(email);
    }

    public void assignUserToTheContest(String email, Contest contest, List<String> errorList) {
        User user = userRepositoryService.findUserByEmail(email);

        Set<Contest> contestList = userRepositoryService.getParticipatedContest(user.getUserId());
        for (Contest enrolledContest : contestList) {
            if (enrolledContest.getContestStatus() == ContestStatus.CURRENT || enrolledContest.getContestStatus() == ContestStatus.UPCOMING) {
                errorList.add(user.getEmail());
                return;
            }
        }
        contestList.add(contest);
        user.setContest(contestList);
        userRepositoryService.save(user);
    }

    private static User createUserFromDto(UserDto userDto, Contest contest, PasswordEncoder passwordEncoder) {
        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());
        newUser.setCollegeName(userDto.getCollege());
        newUser.setPassword(passwordEncoder.encode(newUser.getUserId()));
        newUser.setPassed(false);
        Set<Contest> contestList = new HashSet<>();
        contestList.add(contest);
        newUser.setContest(contestList);
        return newUser;
    }

    public ResponseEntity<ResponseDto> updateRounds(Contest existingContest, List<Rounds> updatedRounds) {
        List<Rounds> existingRounds = existingContest.getRounds();
        List<Rounds> finalUpdatedRounds = new ArrayList<>();
        Rounds previousRound = null;
        ResponseEntity<ResponseDto> responseEntity;
        for (Rounds updatedRoundDTO : updatedRounds) {
            Rounds existingRound = findOrCreateRound(updatedRoundDTO, existingRounds);

            if (previousRound != null && isSuitableRoundType(updatedRoundDTO.getRoundType())) {
                LocalDateTime previousRoundEndTime = previousRound.getEndTime();
                LocalDateTime currentRoundStartTime = updatedRoundDTO.getStartTime();
                if (currentRoundStartTime != null && previousRoundEndTime != null &&
                        previousRoundEndTime.plusHours(1).isAfter(currentRoundStartTime)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("Failed to update round: There is not enough time between the end of the previous round and the start of the current round.", existingContest));
                }
            }
            responseEntity = updateRound(existingRound, updatedRoundDTO);
            if (responseEntity != null) {
                return responseEntity;
            }
            existingRound.setContest(existingContest);
            finalUpdatedRounds.add(existingRound);
            previousRound = existingRound;
        }

        existingContest.setRounds(finalUpdatedRounds);
        return null;
    }

    public ResponseEntity<ResponseDto> createNewContest(Contest contest) {
        contest.setContestStatus(ContestStatus.UPCOMING);
        List<Rounds> rounds = contest.getRounds();
        rounds.sort(Comparator.comparingInt(Rounds::getRoundNumber));

        Rounds previousRound = null;
        for (Rounds round : rounds) {
            round.setContest(contest);
            if (!isValidRoundType(round)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDto("Not a valid round " , null));
            }
            if (calculateRoundDuration(round) && (round.getRoundType() == RoundType.CODING || round.getRoundType() == RoundType.MCQ)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDto("Round " + round.getRoundNumber() + " duration exceeds the end time", null));
            }
            if(previousRound != null && (previousRound.getRoundType() == RoundType.MCQ || previousRound.getRoundType() == RoundType.CODING)) {
                ResponseEntity<ResponseDto> responseEntity = validateRoundTiming(previousRound, round);
                if (responseEntity != null) {
                    return responseEntity;
                }
            }
            previousRound = round;
        }
        return null;
    }

    private boolean isValidRoundType(Rounds round) {
        return round.getRoundType() == RoundType.MCQ || round.getRoundType() == RoundType.CODING
                || round.getRoundType() == RoundType.PERSONAL_INTERVIEW || round.getRoundType() == RoundType.TECHNICAL_INTERVIEW;
    }

    private boolean calculateRoundDuration(Rounds round) {
        if(isTestRoundType(round)) {
            int roundDuration = 0;
            for (Part part : round.getParts()) {
                roundDuration += part.getAssignedTime();
                part.setRounds(round);
            }
            return !round.getStartTime().plusMinutes(roundDuration).isBefore(round.getEndTime());
        } else {
            return true;
        }
    }

    private ResponseEntity<ResponseDto> validateRoundTiming(Rounds previousRound, Rounds round) {
        if ((previousRound != null) && isTestRoundType(round) && (previousRound.getEndTime().isAfter(round.getStartTime().minusHours(1)))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto("There is not enough time between round " + previousRound.getRoundNumber() + " and " + round.getRoundNumber(), null));

        }
        return null;
    }

    private boolean isTestRoundType(Rounds round) {
        return round.getRoundType() == RoundType.CODING || round.getRoundType() == RoundType.MCQ;
    }

    private boolean isSuitableRoundType(RoundType roundType) {
        return (roundType == RoundType.MCQ) || (roundType == RoundType.CODING);
    }

    private ResponseEntity<ResponseDto> getResponseEntity(Rounds round, Part part) {
        QuestionCategory partCategory = part.getCategory().getQuestionCategory();
        boolean isValidCategory = (round.getRoundType() == RoundType.MCQ && isValidMCQCategory(partCategory)) ||
                (round.getRoundType() == RoundType.CODING && isValidCodingCategory(partCategory));
        if (!isValidCategory) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(partCategory + " is not suitable for " + round.getRoundType() + " round", null));
        }
        return null;
    }

    private boolean isValidMCQCategory(QuestionCategory category) {
        return category == QuestionCategory.APTITUDE_MCQ || category == QuestionCategory.LOGICAL_MCQ ||
                category == QuestionCategory.VERBAL_MCQ || category == QuestionCategory.TECHNICAL_MCQ;
    }

    private boolean isValidCodingCategory(QuestionCategory category) {
        return category == QuestionCategory.ALGORITHMS_CODING || category == QuestionCategory.PATTERN_CODING ||
                category == QuestionCategory.STRINGS_CODING || category == QuestionCategory.DATA_STRUCTURE_CODING ||
                category == QuestionCategory.MATHEMATICS_CODING;
    }

    private Rounds findOrCreateRound(Rounds updatedRound, List<Rounds> existingRound) {
        if (updatedRound.getId() != null) {
            return existingRound.stream()
                    .filter(rounds -> rounds.getId().equals(updatedRound.getId()))
                    .findFirst()
                    .orElse(new Rounds());
        } else {
            return new Rounds();
        }
    }

    private ResponseEntity<ResponseDto> updateRound(Rounds round, Rounds updatedRound) {
        round.setStartTime(updatedRound.getStartTime());
        round.setEndTime(updatedRound.getEndTime());
        round.setPass(updatedRound.getPass());
        round.setContest(round.getContest());
        round.setRoundType(updatedRound.getRoundType());

        if (updatedRound.getParts() != null) {
            List<Part> existingParts = round.getParts();
            List<Part> finalUpdatedParts = new ArrayList<>();
            for (Part part : updatedRound.getParts()) {
                ResponseEntity<ResponseDto> responseEntity = getResponseEntity(round, part);
                if (responseEntity != null) return responseEntity;
                Part fetchedPart = findOrCreatePart(existingParts, part);
                updatePart(fetchedPart, part);
                fetchedPart.setRounds(round);
                finalUpdatedParts.add(fetchedPart);
            }
            round.setParts(finalUpdatedParts);
        }

        if (calculateRoundDuration(round)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("The end time of the round " + round.getRoundNumber() + " exceeds the end time", null));
        }
        return null;
    }

    private Part findOrCreatePart(List<Part> existingParts, Part part) {
        if (existingParts == null) {
            return new Part();
        }
        return existingParts.stream()
                .filter(p -> p.getId().equals(part.getId()))
                .findFirst()
                .orElse(new Part());
    }

    private void updatePart(Part part, Part updated) {
        part.setEasy(updated.getEasy());
        part.setMedium(updated.getMedium());
        part.setHard(updated.getHard());
        part.setAssignedTime(updated.getAssignedTime());
        part.setCategory(updated.getCategory());
    }


    public void generateEmailTask(List<Rounds> rounds) {
        List<EmailTask> emailTasks = new ArrayList<>();
        for (Rounds round : rounds) {
            if (round.getRoundType() != RoundType.PERSONAL_INTERVIEW && round.getRoundType() != RoundType.TECHNICAL_INTERVIEW) {
                if (round.getRoundNumber() == 1) {
                    createEmailTask(round, round.getStartTime().minusHours(Long.parseLong(welcomeEmailTimeGap)), emailTasks);
                    createEmailTask(round, round.getStartTime().minusMinutes(Long.parseLong(firstRoundStartTimeGap)), emailTasks);
                }
                createEmailTask(round, round.getEndTime().plusSeconds(Long.parseLong(resultMail)), emailTasks);
            }
        }
        emailTaskRepositoryService.saveAll(emailTasks);
    }

    public void createEmailTask(Rounds round, LocalDateTime taskTime, List<EmailTask> emailTasks) {
        EmailTask emailTask = new EmailTask();
        emailTask.setRounds(round);
        emailTask.setTaskStatus(TaskStatus.PENDING);
        emailTask.setTaskTime(taskTime);
        emailTasks.add(emailTask);
    }

    public ResponseEntity<ResponseDto> resourceNotFound(String message){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto(message, null));
    }
}
