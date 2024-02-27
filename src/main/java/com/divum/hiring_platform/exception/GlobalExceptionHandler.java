package com.divum.hiring_platform.exception;


import com.divum.hiring_platform.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseDto> errorHandle(DataNotFoundException e)
    {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setObject(null);
        responseDto.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);//ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
