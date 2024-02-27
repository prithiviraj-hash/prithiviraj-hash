package com.divum.hiring_platform.controller;

import com.divum.hiring_platform.api.ExcelApi;
import com.divum.hiring_platform.dto.ResponseDto;
import com.divum.hiring_platform.service.ExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ExcelController implements ExcelApi {
    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Override
    public ResponseEntity<ResponseDto> excelUpload(MultipartFile file, String uploadType) throws IOException {
        return excelService.excelUpload(file,uploadType);
    }
}
