package com.divum.hiring_platform.util;

import com.divum.hiring_platform.entity.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserExcelUploadService {
    public  boolean isValidExcelFile(MultipartFile file) {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType());
    }


    public List<User> addUser(InputStream inputStream) throws IOException {
        List<User> users = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheet("User");
        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex > 101)
                break;
            if (row.getRowNum() != 0) {

                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;


                User user = new User();
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();

                    switch (cellIndex) {

                        case 0:
                            String name = cell.toString();
                            user.setName(name);
                            break;
                        case 1:
                            String email = cell.toString();
                            user.setEmail(email);
                            break;
                        case 2:
                            String college = cell.toString();
                            user.setCollegeName(college);
                            break;

                        default:
                            break;
                    }
                    cellIndex++;
                }

                users.add(user);
                rowIndex++;
            }
        }
        return users;
    }
}
