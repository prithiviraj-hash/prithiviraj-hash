package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.QuestionsMcq;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.dto.UserMcq;
import com.divum.hiring_platform.dto.UserMcqDto;
import com.divum.hiring_platform.entity.MultipleChoiceQuestion;
import com.divum.hiring_platform.entity.Options;
import com.divum.hiring_platform.entity.Part;
import com.divum.hiring_platform.repository.service.PartRepositoryService;
import com.divum.hiring_platform.repository.service.RoundsAndMcqQuestionRepositoryService;
import com.divum.hiring_platform.repository.service.RoundsAndQuestionRepositoryService;
import com.divum.hiring_platform.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final RoundsAndMcqQuestionRepositoryService roundsAndMcqQuestionRepositoryService;
    private final RoundsAndQuestionRepositoryService roundsAndQuestionRepositoryService;
    private final PartRepositoryService partRepositoryService;
    @Override
    public ResponseEntity<ResponseDto> getRoundQuestionMcq(String roundId) {
        UserMcq userMcq = new UserMcq();
        List<MultipleChoiceQuestion> multipleChoiceQuestions = roundsAndMcqQuestionRepositoryService.findByRoundId(roundId);
        List<UserMcqDto> userMcqDto = new ArrayList<>();

        for (Part part : partRepositoryService.findAllByRounds_Id(roundId)) {
            UserMcqDto userMcqDto1 = new UserMcqDto();
            userMcqDto1.setCategory(String.valueOf(part.getCategory().getQuestionCategory()));

            List<QuestionsMcq> questionsMcqS = new ArrayList<>();
            for (MultipleChoiceQuestion multipleChoiceQuestion : multipleChoiceQuestions) {
                if (part.getCategory().equals(multipleChoiceQuestion.getCategory())) {
                    QuestionsMcq questionsMcq = mapToQuestionsMcq(multipleChoiceQuestion);
                    questionsMcqS.add(questionsMcq);
                }
            }
            userMcqDto1.setQuestionsMcqS(questionsMcqS);
            userMcqDto.add(userMcqDto1);
        }

        userMcq.setRoundType("MCQ");
        userMcq.setParts(userMcqDto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Here is the mcq assigned to this round", userMcq));
    }

    private QuestionsMcq mapToQuestionsMcq(MultipleChoiceQuestion multipleChoiceQuestion) {
        QuestionsMcq questionsMcq = new QuestionsMcq();
        questionsMcq.setQuestionId(multipleChoiceQuestion.getQuestionId());
        questionsMcq.setImageUrl(multipleChoiceQuestion.getImageUrl());
        questionsMcq.setQuestion(multipleChoiceQuestion.getQuestion());

        List<String> options = new ArrayList<>();
        int correctOptionsCount = 0;
        for (Options option : multipleChoiceQuestion.getOptions()) {
            options.add(String.valueOf(option.getOption()));
            if (option.isCorrect()) {
                correctOptionsCount++;
            }
        }
        questionsMcq.setType(correctOptionsCount > 1 ? "MULTIPLE" : "SINGLE");
        questionsMcq.setOptions(options);
        questionsMcq.setDifficulty(String.valueOf(multipleChoiceQuestion.getDifficulty()));

        return questionsMcq;
    }


}
