package com.uop.efac.cc.award.Service;

import com.uop.efac.cc.award.dto.Student;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExporter {
    public static void exportStudentsToExcel(List<Student> students) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Registration Number");
            headerRow.createCell(1).setCellValue("Course Code");
            headerRow.createCell(2).setCellValue("Grade");

            // Create data rows
            int rowNum = 1;
            for (Student student : students) {
                for (Map<String, String> result : student.getResultsMap()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(student.getRegistrationNumber());
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        row.createCell(1).setCellValue(entry.getKey());
                        row.createCell(2).setCellValue(entry.getValue());
                    }
                }
            }

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream("D:\\Award\\award\\src\\mai\\java\\com\\uop\\efac\\cc\\award\\Excel.xlsx");
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
