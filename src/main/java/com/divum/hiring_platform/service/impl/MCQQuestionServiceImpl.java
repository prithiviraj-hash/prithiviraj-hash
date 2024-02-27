package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.McqDto;
import com.divum.hiring_platform.dto.McqPaginationDto;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.entity.Category;
import com.divum.hiring_platform.entity.McqImageUrl;
import com.divum.hiring_platform.entity.MultipleChoiceQuestion;
import com.divum.hiring_platform.entity.Options;
import com.divum.hiring_platform.repository.MCQQuestionRepository;
import com.divum.hiring_platform.repository.service.CategoryRepositoryService;
import com.divum.hiring_platform.repository.service.MCQQuestionRepositoryService;
import com.divum.hiring_platform.service.MCQQuestionService;
import com.divum.hiring_platform.util.enums.Difficulty;
import com.divum.hiring_platform.util.enums.QuestionCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MCQQuestionServiceImpl implements MCQQuestionService {
    private final CategoryRepositoryService categoryRepositoryService;
    private final MCQQuestionRepository mcqQuestionRepository;
    private final MCQQuestionRepositoryService mcqQuestionRepositoryService;




    @Override
    public ResponseEntity<ResponseDto> getQuestion(String questionId) {
        
            Optional<MultipleChoiceQuestion> multipleChoiceQuestion=mcqQuestionRepository.findById(questionId);
            if(multipleChoiceQuestion.isPresent())
             return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("The question with the id ", multipleChoiceQuestion));
            else 
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("there is no question with this id "+questionId, null));
        }




    @Override
    public ResponseEntity<ResponseDto> updateQuestion(String questionId,MultipleChoiceQuestion multipleChoiceQuestion) {

            Optional<MultipleChoiceQuestion> multipleChoiceQuestion1=mcqQuestionRepository.findById(questionId);
        if(multipleChoiceQuestion1.isPresent()) {
           if(multipleChoiceQuestion.getQuestion()!=null) multipleChoiceQuestion1.get().setQuestion(multipleChoiceQuestion.getQuestion());
           if(multipleChoiceQuestion.getQuestion()!=null) multipleChoiceQuestion1.get().setCategory(multipleChoiceQuestion.getCategory());
           if(multipleChoiceQuestion.getQuestion()!=null) multipleChoiceQuestion1.get().setDifficulty(multipleChoiceQuestion.getDifficulty());
           if(multipleChoiceQuestion.getQuestion()!=null) multipleChoiceQuestion1.get().setOptions(multipleChoiceQuestion.getOptions());
           if(multipleChoiceQuestion.getQuestion()!=null) multipleChoiceQuestion1.get().setImageUrl(multipleChoiceQuestion.getImageUrl());

           mcqQuestionRepository.save(multipleChoiceQuestion1.get());
           return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("question updated", multipleChoiceQuestion1));
        }else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("There is no question with this id "+questionId,null));

        }

    @Override
    public ResponseEntity<ResponseDto> deleteQuestion(String questionId) {
        Optional<MultipleChoiceQuestion> multipleChoiceQuestion1=mcqQuestionRepository.findById(questionId);
        if(multipleChoiceQuestion1.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("There is no question with this id "+questionId,null));
        }else {
            mcqQuestionRepository.deleteById(multipleChoiceQuestion1.get().getQuestionId());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("question deleted",null));

        }
    }



    @Override
    public ResponseEntity<ResponseDto> addQuestion(McqDto mcqDto) {
        if(mcqDto != null) {
            MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
            multipleChoiceQuestion.setQuestionId(UUID.randomUUID().toString());
            multipleChoiceQuestion.setQuestion(mcqDto.getQuestion());
            multipleChoiceQuestion.setDifficulty(Difficulty.valueOf(mcqDto.getDifficulty()));
            Category category = categoryRepositoryService.findCategoryByCategory(mcqDto.getCategory()) ;
            multipleChoiceQuestion.setCategory(category);
            List<McqImageUrl> images = new ArrayList<>();
            for (String image : mcqDto.getImageUrl()) {
                McqImageUrl mcqImageUrl = new McqImageUrl();
                mcqImageUrl.setId(UUID.randomUUID().toString());
                mcqImageUrl.setImageUrl(image);
                mcqImageUrl.setMultipleChoiceQuestion(multipleChoiceQuestion);
                images.add(mcqImageUrl);
            }
            multipleChoiceQuestion.setImageUrl(images);
            List<Options> options = new ArrayList<>();
            for (String option : mcqDto.getOptions()) {
                Options options1 = new Options();
                for (String correctOption : mcqDto.getCorrectOption()) {
                    options1.setMultipleChoiceQuestion(multipleChoiceQuestion);
                    options1.setOption(option);
                    options1.setCorrect(option.equals(correctOption));
                }
                options.add(options1);
            }
            multipleChoiceQuestion.setOptions(options);
            mcqQuestionRepository.save(multipleChoiceQuestion);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("question added", category));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("there is no object", null));
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getAll(Pageable pageable,List<QuestionCategory> type, List<Difficulty> difficulty) {
        if(type==null&&difficulty==null) {
            Page<McqPaginationDto> multipleChoiceQuestions = mcqQuestionRepositoryService.getAllMCQs(pageable);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all questions",multipleChoiceQuestions));
        }
        else if(type!=null&&difficulty==null){
            Page<McqPaginationDto> multipleChoiceQuestions = mcqQuestionRepositoryService.getAllMCQByType(pageable, type);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all questions with category " + type, multipleChoiceQuestions));
        }else if(type==null){
            Page<McqPaginationDto> multipleChoiceQuestions = mcqQuestionRepositoryService.getAllMCQByDifficulty(pageable, difficulty);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all questions with difficulty " + difficulty, multipleChoiceQuestions));
        }else{
            Page<McqPaginationDto> multipleChoiceQuestions = mcqQuestionRepositoryService.getAllMCQByDifficultyAndType(pageable, difficulty,type);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("all questions with category " + type +" and difficulty "+difficulty,multipleChoiceQuestions));
        }

    }
    }
