package com.divum.hiring_platform.api;

import com.divum.hiring_platform.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/v1/result/code")
public interface CodeResultApi {
    @GetMapping("/{codingResultId}")
    ResponseEntity<ResponseDto> fetchResult(@PathVariable String codingResultId);

}
