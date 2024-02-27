package com.divum.hiring_platform.util;

import com.divum.hiring_platform.dto.PartResponseDto;
import com.divum.hiring_platform.dto.PartWiseResponseDto;
import com.divum.hiring_platform.dto.SingleResponse;
import com.divum.hiring_platform.entity.PartWiseResponse;
import com.divum.hiring_platform.entity.UserResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class McqPartWiseResponseService {
    public PartWiseResponse mcqPartWiseResponse(PartResponseDto partResponseDto) {
        PartWiseResponse partWiseResponse = new PartWiseResponse();
        for (PartWiseResponseDto partWiseResponseDto : partResponseDto.getPartWiseResponseDtoList()) {
            partWiseResponse.setCategory(partWiseResponseDto.getCategory());
            List<UserResponse> userResponses = new ArrayList<>();
            for (SingleResponse singleResponse : partWiseResponseDto.getUserResponse()) {
                UserResponse userResponse = new UserResponse();
                List<String> chosenAnswers = new ArrayList<>(singleResponse.getChosenAnswer());
                userResponse.setChosenAnswer(chosenAnswers);
                userResponse.setQuestionId(singleResponse.getQuestionId());
                userResponse.setIsCorrect(Boolean.FALSE);
                userResponses.add(userResponse);
                userResponse.setDifficulty(singleResponse.getDifficulty());
            }
            partWiseResponse.setUserResponse(userResponses);
        }
        return partWiseResponse;
    }
}
