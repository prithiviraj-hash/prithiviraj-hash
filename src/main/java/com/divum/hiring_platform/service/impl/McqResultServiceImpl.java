package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.PartResponseDto;
import com.divum.hiring_platform.dto.PartWiseMark;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.entity.MCQResult;
import com.divum.hiring_platform.entity.PartWiseResponse;
import com.divum.hiring_platform.entity.Rounds;
import com.divum.hiring_platform.entity.UserResponse;
import com.divum.hiring_platform.exception.DataNotFoundException;
import com.divum.hiring_platform.repository.MCQResultRepository;
import com.divum.hiring_platform.repository.McqResultUserRepository;
import com.divum.hiring_platform.repository.RoundsRepository;
import com.divum.hiring_platform.repository.service.MCQResultRepositoryService;
import com.divum.hiring_platform.repository.service.McqResultUserRepositoryService;
import com.divum.hiring_platform.repository.service.OptionsRepositoryService;
import com.divum.hiring_platform.repository.service.RoundsRepositoryService;
import com.divum.hiring_platform.service.McqResultService;
import com.divum.hiring_platform.util.McqPartWiseResponseService;
import com.divum.hiring_platform.util.enums.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
public class McqResultServiceImpl implements McqResultService {

    private final MCQResultRepository mcqResultRepository;
    private final RoundsRepository roundsRepository;
    private final MCQResultRepositoryService mcqResultRepositoryService;
    private final McqPartWiseResponseService mcqPartWiseResponseService;

    Map<String,List<String>> answer = new HashMap<>();
    List<PartWiseResponse> mcqdata = null;
    List<String> obj = null;
    List<String> ansObj = null;
    double easy = 0;
    double easyCorrect = 0;
    int correctCount = 0;
    double medium = 0;
    double mediumCorrect = 0;
    double hard = 0;
    double hardCorrect = 0;
    boolean equal = false;

    boolean getAnswer = false;

    private final McqResultUserRepositoryService mcqResultUserRepositoryService;

    private final OptionsRepositoryService optionsRepositoryService;

    private final McqResultUserRepository mcqResultUserRepository;

    private final RoundsRepositoryService roundsRepositoryService;


    /////////////////////////////////////////// FETCHING ANSWERS IN MEMORY //////////////////////////////////////////////

    void fetchAnswers()
    {
        List<Object[]> correctAnswer = optionsRepositoryService.getQuestionIdAndAnswers();
        for(Object[] options : correctAnswer)
        {
            String id = (String) options[0];
            String answers = (String) options[1];
            answer.putIfAbsent(id,new ArrayList<>());
            answer.get(id).add(answers);
        }
    }

    /////////////////////////////////////////// MCQ VALIDATION API ////////////////////////////////////////////////////

    public ResponseEntity<ResponseDto> update(String id)
    {
        if(!getAnswer)
        {
            fetchAnswers();
            getAnswer = true;
        }
        MCQResult data = mcqResultUserRepositoryService.findByid(id);
        if(data == null) {
            throw new DataNotFoundException("RESULT NOT FOUND WITH PROVIDED ID");
        }
        String roundId = data.getRoundId();
        int passPercentage = roundsRepositoryService.passPercentage(roundId);
        mcqdata = data.getSavedMcq();
        obj = null;
        ansObj = null;
        List<PartWiseMark> mark = data.getPartWiseMarks();
        double total = 0;
        int partCount = 0;
        int totalCorrect = 0;

        for(PartWiseResponse mcqans : mcqdata)
        {
            String category;
            easy = 0;
            easyCorrect = 0;
            correctCount = 0;
            medium = 0;
            mediumCorrect = 0;
            hard = 0;
            hardCorrect = 0;
            equal = false;

            category = mcqans.getCategory();
            partCount ++;

            for(UserResponse userResponse : mcqans.getUserResponse())
            {
                mark(userResponse);
            }

            int correctAnswer = correctCount;
            easy = easy * 1;
            medium = medium * 2;
            hard = hard * 3;

            double easyUpdate = 0;
            double mediumUpdate = 0;
            double hardUpdate = 0;

            total = getTotalinfo(easy ,medium, hard, total, easyCorrect, mediumCorrect, hardCorrect);

            double[] difficulties = {easy, medium, hard};
            double[] updates = {easyUpdate, mediumUpdate, hardUpdate};
            double[] correctCounts = {easyCorrect, mediumCorrect, hardCorrect};


            totalCorrect += (int) (easyCorrect + mediumCorrect + hardCorrect);

            updates = updateDifficulity(updates, difficulties, correctCounts);

            markUpdate(mark ,category  , data , updates ,correctAnswer, totalCorrect);

        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setGroupingUsed(false);
        float setPercentage = 0;
        if(partCount != 0)
        {
            setPercentage = (float) Double.parseDouble(df.format(total / partCount));
        }

        data.setTotalPercentage(setPercentage);

        if(data.getTotalPercentage() >= passPercentage)
        {
            data.setResult(Result.PASS);
        }
        else {
            data.setResult(Result.FAIL);
        }

        mcqResultUserRepository.save(data);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("EVALUATION IS SUCCESSFULLY DONE");
        responseDto.setObject(data);
        return ResponseEntity.ok(responseDto);
    }

    double[] updateDifficulity(double[] updates, double[] difficulties, double[] correctCounts)
    {
        for (int i = 0; i < difficulties.length; i++) {
            double divisor = difficulties[i];
            if (divisor != 0.0) {
                updates[i] = correctCounts[i] / divisor * 100;
            }
        }
        return updates;
    }
    void markUpdate(List<PartWiseMark> mark,String category, MCQResult data , double[] updates, int correctAnswer , int totalCorrect)
    {
        Map<String , Double> setvalues ;
        for(PartWiseMark updateMark : mark)
        {
            if(updateMark.getPart().compareTo(category) == 0)
            {
                setvalues = updateMark.getDifficultyWiseMarks();
                setvalues.put("EASY",updates[0]);
                setvalues.put("MEDIUM",updates[1]);
                setvalues.put("HARD",updates[2]);
                updateMark.setCorrectAnswerCount(correctAnswer);
            }
            data.setTotalMarks(totalCorrect);
            mcqResultUserRepository.save(data);
        }
    }

    double getTotalinfo(double easy, double medium, double hard, double total, double easyCorrect , double mediumCorrect, double hardCorrect) {
        if (easy + medium + hard != 0) {
            total += (easyCorrect + mediumCorrect + hardCorrect) / (easy + medium + hard) * 100;
        }
        return total;
    }

    void mark(UserResponse userResponse) {
        String difficulty = userResponse.getDifficulty().toUpperCase();
        obj = userResponse.getChosenAnswer();
        String questionId = userResponse.getQuestionId();
        ansObj = answer.get(questionId);

        switch (difficulty) {
            case "EASY":
                easy++;
                checkAnswer(userResponse, obj, ansObj, 1);
                break;
            case "MEDIUM":
                medium++;
                checkAnswer(userResponse, obj, ansObj, 2);
                break;
            case "HARD":
                hard++;
                checkAnswer(userResponse, obj, ansObj, 3);
                break;
            default:
                break;
        }
    }

    private void checkAnswer(UserResponse userResponse, List<String> obj, Object ansObj, int score) {
        boolean isCorrect = obj.equals(ansObj);
        userResponse.setIsCorrect(isCorrect ? TRUE : FALSE);
        if (isCorrect) {
            correctCount++;
            if (score == 1) {
                easyCorrect++;
            } else if (score == 2) {
                mediumCorrect += 2;
            } else {
                hardCorrect += 3;
            }
        }
    }

    @Override
    public ResponseEntity<ResponseDto> partWiseResponseMcq(String userId, String roundId, PartResponseDto partResponseDto, String status) {
        Optional<MCQResult> mcqResult = mcqResultRepositoryService.findByUserIdAndRoundId(userId, roundId);


            if (mcqResult.isPresent()) {
                PartWiseResponse partWiseResponses = mcqPartWiseResponseService.mcqPartWiseResponse(partResponseDto);

                List<PartWiseResponse> partWiseResponses1=new ArrayList<>();
                partWiseResponses1.add(partWiseResponses);
                mcqResult.get().setSavedMcq(partWiseResponses1);
                mcqResultRepository.save(mcqResult.get());
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("updated", mcqResultRepository.findAll()));
            } else {
                Optional<Rounds> round = roundsRepository.findById(roundId);
                if (round.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto("there is no round with this id " + roundId, null));
                } else {
                    MCQResult mcqResult1 = new MCQResult();
                    mcqResult1.setUserId(userId);
                    mcqResult1.setContestId(round.get().getContest().getContestId());
                    mcqResult1.setRoundId(roundId);

                    PartWiseResponse partWiseResponses =mcqPartWiseResponseService.mcqPartWiseResponse(partResponseDto) ;
                    List<PartWiseResponse> partWiseResponse=new ArrayList<>();
                    partWiseResponse.add(partWiseResponses);
                    mcqResult1.setSavedMcq(partWiseResponse);

                    mcqResultRepository.save(mcqResult1);
                    return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("mcq user response saved", mcqResult1));
                }
            }
        }

}

