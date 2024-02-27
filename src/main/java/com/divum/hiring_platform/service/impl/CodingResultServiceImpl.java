package com.divum.hiring_platform.service.impl;

import com.divum.hiring_platform.dto.CodingResultDTO;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.entity.CodingResult;
import com.divum.hiring_platform.exception.DataNotFoundException;
import com.divum.hiring_platform.repository.CodingResultRepository;
import com.divum.hiring_platform.service.CodingResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodingResultServiceImpl implements CodingResultService {

    private final CodingResultRepository codingResultRepository;

    @Override
    public ResponseEntity<ResponseDto> fetch(String id) {
        Optional<CodingResult> codingResult = codingResultRepository.findById(id);
        if (codingResult.isEmpty()) {
            throw new DataNotFoundException("DATA NOT FOUND");
        }
        CodingResult codeResult = codingResult.get();
        CodingResultDTO codingResultDTO = CodingResultDTO.builder()
                .contestId(codeResult.getContestId())
                .id(codeResult.getId())
                .roundId(codeResult.getRoundId())
                .userId(codeResult.getUserId())
                .question(codeResult.getQuestion())
                .totalMarks(codeResult.getTotalMarks())
                .build();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("CODING RESULTS");
        responseDto.setObject(codingResultDTO);
            return ResponseEntity.ok(responseDto);
    }
}