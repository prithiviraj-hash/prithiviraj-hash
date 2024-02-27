package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.entity.MultipleChoiceQuestion;
import com.divum.hiring_platform.entity.User;
import com.divum.hiring_platform.repository.MCQQuestionRepository;
import com.divum.hiring_platform.service.ExcelService;
import com.divum.hiring_platform.util.McqExcelUploadService;
import com.divum.hiring_platform.util.UserExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    private final MCQQuestionRepository mcqQuestionRepository;
    private final McqExcelUploadService mcqExcelUploadService;
    private final UserExcelUploadService userExcelUploadService;




    @Override
    public ResponseEntity<ResponseDto> excelUpload(MultipartFile file, String uploadType) throws IOException {
        if(uploadType.equals("MCQ")){
            if(mcqExcelUploadService.isValidExcelFile(file)){
                List<MultipleChoiceQuestion> mcqQuestions =mcqExcelUploadService.getMcqQuestions(file.getInputStream());
                for(MultipleChoiceQuestion mcq:mcqQuestions){
                    mcq.setQuestionId(UUID.randomUUID().toString());
                    mcqQuestionRepository.save(mcq);
                }
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("questions added",null));

            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDto("questions not found",null));

            }
        }else if(uploadType.equals("USER") && (userExcelUploadService.isValidExcelFile(file))){
                List<User> users =userExcelUploadService.addUser(file.getInputStream());
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("questions added",users));


        }
        return null;
    }
}
