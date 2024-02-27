package com.divum.hiring_platform.util;

import com.divum.hiring_platform.dto.EmailStructure;
import com.divum.hiring_platform.dto.EmployeeInterviewScheduleMail;
import com.divum.hiring_platform.entity.*;
import com.divum.hiring_platform.repository.ContestRepository;
import com.divum.hiring_platform.repository.UserRepository;
import com.divum.hiring_platform.util.enums.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class EmailSender {

    public static final String UTF_8_ENCODING = "UTF-8";
    @Value("${email.sender}")
    public String senderEmail;
    @Value("${email.link.employee-availability}")
    private String employeeAvailabilityLink;
    @Value("${email.template.welcome-email}")
    private String welcomeMail;
    @Value("${email.template.first-round-mail}")
    private String roundMail;
    @Value("${email.template.result-mail}")
    private String resultMail;
    @Value("${email.template.response-mail}")
    private String employeeResponseMail;
    @Value("${email.template.employee-interview-notification}")
    private String employeeInterviewNotification;
    @Value("${email.template.interview-reschedule-contestant}")
    private String interviewRescheduleNotificationToContestant;
    @Value("${email.link.password-reset}")
    private String passwordResetBaseLink;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final JwtUtil jwtUtil;
    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void sendEmailToTheContestantAboutTheRound(EmailTask emailTask) throws MessagingException {
        Rounds round = emailTask.getRounds();
        Contest contest = round.getContest();
        List<User> users;
        boolean isWelcomeEmail = false;
        boolean isFirstRoundMail = false;
        boolean hasPasswordChanged = false;
        if(round.getStartTime().isEqual(emailTask.getTaskTime().plusHours(60))) {
            isWelcomeEmail = true;
            users = contestRepository.findUsersAssignedToTheContest(contest.getContestId());
        } else if (round.getStartTime().isEqual(emailTask.getTaskTime().plusMinutes(30))){
            isFirstRoundMail = true;
            users = contestRepository.findUsersAssignedToTheContest(contest.getContestId());
        } else {
            users = contestRepository.findPassedStudents(contest);
        }
        EmailStructure emailStructure = new EmailStructure();
        emailStructure.setSender(senderEmail);
        EmailStructure adminResultNotification = new EmailStructure();
        adminResultNotification.setSender(senderEmail);
        adminResultNotification.setReceiver(senderEmail);
        for(User user : users) {
            Context context;
            String text;
            if(isWelcomeEmail) {
                context = getContextForWelcomeMail(user.getName(), user.getEmail(), user.getUserId(), contest.getRounds().get(0).getStartTime());
                text = templateEngine.process(welcomeMail, context);
                emailStructure.setSubject("Assessment Details");
            } else if (isFirstRoundMail){
                hasPasswordChanged = !passwordEncoder.matches(user.getUserId(), user.getPassword());
                context = getContextForRoundEmail(hasPasswordChanged, round);
                text  = templateEngine.process(roundMail, context);
                emailStructure.setSubject("Assessment Details");
                user.setPassed(true);
            } else {
                Rounds nextRound = Objects.requireNonNull(getNextRound(round));
                context = getContextForResultEmail(round, nextRound, user);
                text = templateEngine.process(resultMail, context);
                emailStructure.setSubject("Round result");
                user.setPassed(true);
            }
            emailStructure.setReceiver(user.getEmail());
            emailStructure.setText(text);
            sendEmail(emailStructure);
        }
        if(isWelcomeEmail) {
            contest.setContestStatus(ContestStatus.CURRENT);
        }
        userRepository.saveAll(users);
        contestRepository.save(contest);
    }

    public Rounds getNextRound(Rounds currentRound) {
        int nextRoundNumber = 1 + currentRound.getRoundNumber();
        for(Rounds round: currentRound.getContest().getRounds()) {
            if(round.getRoundNumber() == nextRoundNumber) return round;
        }
        return null;
    }

    public Context getContextForResultEmail(Rounds round, Rounds nextRound, User user) {
        boolean isNextRoundIsInterview = nextRound != null && (nextRound.getRoundType() == RoundType.PERSONAL_INTERVIEW || nextRound.getRoundType() == RoundType.TECHNICAL_INTERVIEW);
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("isPassed", user.isPassed());
        if(!isNextRoundIsInterview && nextRound != null) {
            context.setVariable("startDate", dateFormatter(nextRound.getStartTime()));
            context.setVariable("startTime", timeFormatter(nextRound.getStartTime()));
            context.setVariable("endDate", timeFormatter(nextRound.getEndTime()));
            context.setVariable("endTime", timeFormatter(nextRound.getEndTime()));
        }
        context.setVariable("isNextRoundIsInterview", isNextRoundIsInterview);
        String categories = getCategoryString(round.getParts());
        context.setVariable("categories", categories);
        return  context;
    }
    public String getCategoryString(List<Part> parts) {
        StringBuilder categoryBuilder = new StringBuilder();
        for(Part part : parts) {
            categoryBuilder.append(part.getCategory().getQuestionCategory().toString()).append(", ");
            if(!categoryBuilder.isEmpty()) {
                categoryBuilder.setLength(categoryBuilder.length() - 2);
            }
        }
        return categoryBuilder.toString();
    }


    private Context getContextForRoundEmail(boolean hasPasswordChanged, Rounds round) {
        Context context = new Context();
        String startDate = dateFormatter(round.getStartTime());
        String startTime = timeFormatter(round.getStartTime());

        String endDate = dateFormatter(round.getEndTime());
        String endTime = timeFormatter(round.getEndTime());
        context.setVariable("startDate", startDate);
        context.setVariable("startTime", startTime);
        context.setVariable("endDate", endDate);
        context.setVariable("endTime", endTime);
        context.setVariable("hasChangedPassword", hasPasswordChanged);
        String categories = getCategoryString(round.getParts());
        context.setVariable("categories", categories);
        return context;
    }

    public Context getContextForWelcomeMail(String name, @jakarta.validation.constraints.Email String email, String password, LocalDateTime dateTime) {
        Context context = new Context();
        context.setVariable("startTime",dateTime);
        context.setVariable("name", name);
        context.setVariable("username", email);
        context.setVariable("defaultPassword", password);
        return context;
    }
    private String dateFormatter(LocalDateTime localDateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(dateFormatter);
    }

    private String timeFormatter(LocalDateTime localDateTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return localDateTime.format(timeFormatter);
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }
    public void sendEmail(EmailStructure emailStructure) throws MessagingException {
        MimeMessage message = getMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
        helper.setPriority(1);
        helper.setTo(emailStructure.getReceiver());
        helper.setSubject(emailStructure.getSubject());
        helper.setFrom(emailStructure.getSender());
        helper.setText(emailStructure.getText(), true);
        javaMailSender.send(message);
    }

    public EmailStructure questionAssignResponseMail(Contest contest, int questionCount, int actualQuestionCount) {
        EmailStructure adminResponseMail = new EmailStructure();
        adminResponseMail.setSender(senderEmail);
        adminResponseMail.setReceiver(senderEmail);
        if(questionCount == actualQuestionCount) {
            adminResponseMail.setSubject("Questions has been assigned");
            adminResponseMail.setText("The question for the contest " + contest.getName() + " has been assigned");
        } else {
            adminResponseMail.setSubject("Error while assigning question");
            adminResponseMail.setText("Error occurred while assigning the question for the contest");
        }
        return adminResponseMail;
    }

    public void sendEmailToTheAdminAboutTheError(EmailTask emailTask, String emailException) throws MessagingException {
        EmailStructure emailStructure = new EmailStructure();
        emailStructure.setReceiver(senderEmail);
        emailStructure.setSender(senderEmail);
        emailStructure.setSubject("Notification mail failed");
        emailStructure.setText("Error occurred while sending mail to the contestants for the contest " + emailTask.getRounds().getContest().getName() + " for round " + emailTask.getRounds().getRoundNumber() + "\n The error is " + emailException);
        sendEmail(emailStructure);
        emailTask.setTaskStatus(TaskStatus.ADMIN_NOTIFIED);
    }

    public void notifyAboutOneOnOne(List<EmployeeAvailability> availabilities) throws MessagingException {
        for(EmployeeAvailability availability : availabilities) {
            EmailStructure emailStructure = new EmailStructure();
            emailStructure.setReceiver(availability.getEmployeeAndContest().getEmployee().getEmail());
            emailStructure.setSubject("Availability Inquiry: One-on-One Round with Contestants");
            emailStructure.setSender(senderEmail);
            Context context = getContestForEmployeeOneOnOneRequest(availability);
            String text = templateEngine.process(employeeResponseMail, context);
            emailStructure.setText(text);
            sendEmail(emailStructure);
        }
    }

    private Context getContestForEmployeeOneOnOneRequest(EmployeeAvailability availability) {
        Context context = new Context();
        String name = availability.getEmployeeAndContest().getEmployee().getFirstName() + " " + availability.getEmployeeAndContest().getEmployee().getLastName();
        context.setVariable("employeeName", name);
        String acceptToken = jwtUtil.createEmployeeAvailabilityToken(availability, "ACCEPT");
        String rejectToken = jwtUtil.createEmployeeAvailabilityToken(availability, "REJECT");
        context.setVariable("acceptLink", employeeAvailabilityLink + "?token=" + acceptToken);
        context.setVariable("rejectLink", employeeAvailabilityLink + "?token=" + rejectToken);
        return context;
    }

    public void sendConfirmationMail(EmployeeAvailability employeeAvailability) throws MessagingException {
        EmailStructure emailStructure = new EmailStructure();
        emailStructure.setSender(senderEmail);
        String name = employeeAvailability.getEmployeeAndContest().getEmployee().getFirstName() + " " + employeeAvailability.getEmployeeAndContest().getEmployee().getLastName();
        emailStructure.setReceiver(employeeAvailability.getEmployeeAndContest().getEmployee().getEmail());
        if(employeeAvailability.getResponse() == EmployeeResponse.AVAILABLE){
            emailStructure.setSubject("Confirmation of Your Response");
            emailStructure.setText("Dear " + name + ",\n\nYour response to the one-on-one interview availability has been confirmed.\n\nContest Name: "+ employeeAvailability.getEmployeeAndContest().getContest().getName() + "\nDecision: Accepted\n\nThank you for your confirmation.\n\nBest regards,\nDIVUM");
        } else {
            emailStructure.setSender("Confirmation of Your Response");
            emailStructure.setSubject("Dear " + name + ",\n\nYour response to the one-on-one interview availability has been confirmed.\n\nContest Name: " + employeeAvailability.getEmployeeAndContest().getContest().getName() + "\nDecision: Rejected\n\nThank you for your confirmation.\n\nBest regards,\nDIVUM");
        }
        sendEmail(emailStructure);
    }


    public void sendEmailAboutTheInterview(List<EmployeeInterviewScheduleMail> mails) {
        for (EmployeeInterviewScheduleMail mail : mails) {
            executorService.submit(() -> {
                try {
                    EmailStructure employeeMail = new EmailStructure();
                    employeeMail.setSender(senderEmail);
                    employeeMail.setSubject("Interview Schedule Notification");
                    Context context = getEmployeeNotificationContext(mail);
                    String text = templateEngine.process(employeeInterviewNotification, context);
                    employeeMail.setText(text);
                    employeeMail.setReceiver(mail.getEmployee().getEmail());
                    sendEmail(employeeMail);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private Context getEmployeeNotificationContext(EmployeeInterviewScheduleMail mail) {
        Context context = new Context();
        String name = mail.getEmployee().getFirstName() + " " + mail.getEmployee().getLastName();
        context.setVariable("name", name);

        List<Map<String, String>> intervieweeDetailsList = new ArrayList<>();
        for (Map.Entry<User, LocalDateTime> entry : mail.getUsersAndInterviewTime().entrySet()) {
            Map<String, String> intervieweeDetails = new HashMap<>();
            User user = entry.getKey();
            LocalDateTime interviewTime = entry.getValue();
            String intervieweeName = user.getName();
            String formattedInterviewTime = interviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            intervieweeDetails.put("name", intervieweeName);
            intervieweeDetails.put("interviewTime", formattedInterviewTime);
            intervieweeDetailsList.add(intervieweeDetails);
        }
        Comparator<Map<String, String>> dateTimeComparator = (map1, map2) -> {
            LocalDateTime dateTime1 = LocalDateTime.parse(map1.get("interviewTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime dateTime2 = LocalDateTime.parse(map2.get("interviewTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return dateTime1.compareTo(dateTime2);
        };
        Collections.sort(intervieweeDetailsList, dateTimeComparator);
        context.setVariable("intervieweeDetailsList", intervieweeDetailsList);

        return context;
    }

    public void sendEmailToTheContestantAndEmployeeAboutTheReschedule(Interview interview, InterviewRequestType requestType) throws MessagingException {
        EmailStructure contestantEmail = new EmailStructure();
        EmailStructure employeeEmail = new EmailStructure();

        contestantEmail.setSender(senderEmail);
        employeeEmail.setSender(senderEmail);

        contestantEmail.setSubject("Interview Rescheduled");
        employeeEmail.setSubject("Interview Rescheduled");

        contestantEmail.setReceiver(interview.getUser().getEmail());
        employeeEmail.setReceiver(interview.getEmployee().getEmail());

        Context contestantContext = new Context();
        contestantContext.setVariable("interviewTime", interview.getInterviewTime().format(DateTimeFormatter.ofPattern("dd-MM-yy : HH:mm")));
        contestantContext.setVariable("recipientType", "contestant");
        String contestantText = templateEngine.process(interviewRescheduleNotificationToContestant, contestantContext);
        Context employeeContext = new Context();
        employeeContext.setVariable("interviewTime", interview.getInterviewTime().format(DateTimeFormatter.ofPattern("dd-MM-yy : HH:mm")));
        employeeContext.setVariable("recipientType", "employee");
        String employeeText = templateEngine.process(interviewRescheduleNotificationToContestant, employeeContext);

        contestantEmail.setText(contestantText);
        employeeEmail.setText(employeeText);

        sendEmail(employeeEmail);
        sendEmail(contestantEmail);
    }

    public void sendEmailToTheEmployeeToResetThePassword(Employee employee, String token) throws MessagingException {
        EmailStructure emailStructure = new EmailStructure();
        emailStructure.setSubject("Password reset");
        emailStructure.setSender(senderEmail);
        emailStructure.setReceiver(employee.getEmail());
        emailStructure.setText("You can reset the password using below link \n " + passwordResetBaseLink + "?token=" + token);
        sendEmail(emailStructure);
    }

    public void sendEmailToTheEmployeeAboutPasswordChange(Employee employee) throws MessagingException {
        EmailStructure emailStructure = new EmailStructure();
        emailStructure.setReceiver(employee.getEmail());
        emailStructure.setSubject("Password reset : Successful");
        emailStructure.setSender(senderEmail);
        emailStructure.setText("You password has been updated");
        sendEmail(emailStructure);
    }

    public void sendEmailToEmployeeAboutRejection(InterviewRequest interviewRequest) {

    }
}
