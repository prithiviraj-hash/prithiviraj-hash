package com.divum.hiring_platform.util;

import com.divum.hiring_platform.dto.EmailStructure;
import com.divum.hiring_platform.entity.*;
import com.divum.hiring_platform.repository.*;
import com.divum.hiring_platform.util.enums.Difficulty;
import com.divum.hiring_platform.util.enums.RoundType;
import com.divum.hiring_platform.util.enums.TaskStatus;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class Scheduler {

    private final RoundAndMcqQuestionRepository roundAndMcqQuestionRepository;
    private final MCQQuestionRepository mcqQuestionRepository;
    private final CodingQuestionRepository codingQuestionRepository;
    private final RoundsAndQuestionRepository roundsAndCodingQuestionRepository;
    private final EmailTaskRepository emailTaskRepository;
    private final EmailSender emailSender;

    @Scheduled(fixedRate = 1000)
    public void schedule() throws MessagingException {
        List<EmailTask> emailTasks = emailTaskRepository.findEmailTasksByTaskTimeAfterAndTaskTimeBefore(LocalDateTime.now(), LocalDateTime.now().plusSeconds(5));
        for(EmailTask emailTask : emailTasks) {
            String emailException = null;
            if(emailTask.getTaskStatus() == TaskStatus.PENDING) {
                try {
                    emailSender.sendEmailToTheContestantAboutTheRound(emailTask);
                    emailTask.setTaskStatus(TaskStatus.SUCCESS);
                } catch (Exception e) {
                    emailTask.setTaskStatus(TaskStatus.RETRY);
                }
            }
            if(emailTask.getTaskStatus() == TaskStatus.RETRY) {
                try {
                    emailSender.sendEmailToTheContestantAboutTheRound(emailTask);
                } catch (Exception e) {
                    emailException = e.getMessage();
                    emailTask.setTaskStatus(TaskStatus.FAILED);
                }
            }
            if(emailTask.getTaskStatus() == TaskStatus.FAILED) {
                emailSender.sendEmailToTheAdminAboutTheError(emailTask, emailException);
            }
            emailTaskRepository.save(emailTask);
        }
    }

    public void assignQuestion(Contest contest) throws MessagingException {
        int questionCount = 0;
        int actualQuestionCount = 0;
        for (Rounds rounds : contest.getRounds()) {
            if (rounds.getRoundType() == RoundType.MCQ) {
                questionCount += assignMcqQuestions(rounds, contest);
            } else if (rounds.getRoundType() == RoundType.CODING) {
                questionCount += assignCodingQuestions(rounds, contest);
            }
            actualQuestionCount = roundsAndCodingQuestionRepository.getQuestionCount(contest);
        }
        EmailStructure adminResponseMail = emailSender.questionAssignResponseMail(contest, questionCount, actualQuestionCount);
        emailSender.sendEmail(adminResponseMail);
    }

    private int assignMcqQuestions(Rounds rounds, Contest contest) {
        int questionCount = 0;
        for (int i = 0; i < rounds.getParts().size(); i++) {
            questionCount += assignMcqQuestionsToPart(rounds.getParts().get(i), contest);
        }
        return questionCount;
    }

    private int assignCodingQuestions(Rounds rounds, Contest contest) {
        int questionCount = 0;
        for (Part part : rounds.getParts()) {
            questionCount += assignCodingQuestion(part, contest);
        }
        return questionCount;
    }

    private int assignMcqQuestionsToPart(Part part, Contest contest) {
        List<MultipleChoiceQuestion> questions = new ArrayList<>();
        Map<Difficulty, Integer> difficultyIntegerMap = getDifficultyIntegerMap(part);
        int categoryId = part.getCategory().getCategoryId();

        for (Map.Entry<Difficulty, Integer> entry : difficultyIntegerMap.entrySet()) {
            Difficulty difficulty = entry.getKey();
            int requiredCount = entry.getValue();

            while (requiredCount > 0) {
                MultipleChoiceQuestion question = fetchQuestion(categoryId, difficulty, contest);
                if (question == null) {
//                    emailSender.questionAssignResponseMail(contest, )
                    break;
                }
                if (!questions.contains(question)) {
                    questions.add(question);
                    requiredCount--;
                }
            }
        }

        List<RoundAndMcqQuestion> roundAndMcqQuestions = new ArrayList<>();
        saveMcqQuestion(questions, part.getRounds(), contest, roundAndMcqQuestions);
        roundAndMcqQuestionRepository.saveAll(roundAndMcqQuestions);
        return roundAndMcqQuestions.size();
    }

    private static Map<Difficulty, Integer> getDifficultyIntegerMap(Part part) {
        Map<Difficulty, Integer> difficultyIntegerMap = new EnumMap<>(Difficulty.class);
        difficultyIntegerMap.put(Difficulty.EASY, part.getEasy());
        difficultyIntegerMap.put(Difficulty.MEDIUM, part.getMedium());
        difficultyIntegerMap.put(Difficulty.HARD, part.getHard());
        return difficultyIntegerMap;
    }

    private MultipleChoiceQuestion fetchQuestion(int categoryId, Difficulty difficulty, Contest contest) {
        return mcqQuestionRepository.getRandomQuestion(categoryId, difficulty, contest, 1);
    }


    private void saveMcqQuestion(List<MultipleChoiceQuestion> questions, Rounds rounds, Contest contest, List<RoundAndMcqQuestion> roundAndMcqQuestions) {
        for(MultipleChoiceQuestion question : questions) {
            RoundAndMcqQuestion roundAndMcqQuestion = new RoundAndMcqQuestion();
            roundAndMcqQuestion.setContestAndMcq(new ContestAndMcq(contest, question));
            roundAndMcqQuestion.setRounds(rounds);
            roundAndMcqQuestions.add(roundAndMcqQuestion);
        }
    }

    public int assignCodingQuestion(Part part, Contest contest) {
        List<CodingQuestion> questions = new ArrayList<>();
        Map<Difficulty, Integer> difficultyIntegerMap = getDifficultyIntegerMap(part);
        int categoryId = part.getCategory().getCategoryId();

        for (Map.Entry<Difficulty, Integer> entry : difficultyIntegerMap.entrySet()) {
            Difficulty difficulty = entry.getKey();
            int requiredCount = entry.getValue();

            while (requiredCount > 0) {
                CodingQuestion question = fetchCodingQuestion(categoryId, difficulty, contest);
                if (question == null) {
                    System.out.println("Not enough questions available for difficulty: " + difficulty);
                    break;
                }
                if (!questions.contains(question)) {
                    questions.add(question);
                    requiredCount--;
                }
            }
        }

        List<RoundAndCodingQuestion> roundAndMcqQuestions = new ArrayList<>();
        saveCodingQuestion(questions, part.getRounds(), contest, roundAndMcqQuestions);
        roundsAndCodingQuestionRepository.saveAll(roundAndMcqQuestions);
        return roundAndMcqQuestions.size();
    }

    private void saveCodingQuestion(List<CodingQuestion> questions, Rounds rounds, Contest contest, List<RoundAndCodingQuestion> roundAndCodingQuestions) {
        for(CodingQuestion question : questions) {
            RoundAndCodingQuestion roundAndCodingQuestion = new RoundAndCodingQuestion();
            roundAndCodingQuestion.setContestAndCoding(new ContestAndCoding(contest, question));
            roundAndCodingQuestion.setRounds(rounds);
            roundAndCodingQuestions.add(roundAndCodingQuestion);
        }
    }

    private CodingQuestion fetchCodingQuestion(int categoryId, Difficulty difficulty, Contest contest) {
        return codingQuestionRepository.getRandomQuestion(categoryId, difficulty, contest, 1);
    }
}

