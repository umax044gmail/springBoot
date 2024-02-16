package com.uop.efac.cc.award.Service;

import com.uop.efac.cc.award.dto.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CAHEWAVITHARANAMEMORIALService {
    public ResponseEntity<String> getCAHewAward(MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        List<Student> studentList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            boolean isFirstLine = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                //Print Each Line of The CSV File To The Console
                System.out.println(line);

                Student student = new Student();
                String[] values = line.split(",");

                if (values.length >= 3) {

                    String indexUpperCase = values[0].trim().toUpperCase();
                    String courseUpperCase = values[1].trim().toUpperCase();
                    String result = values[2].trim().toUpperCase();

                    //TODO: Please check this E/ or e
                    if (indexUpperCase.contains("E/")) {
                        Optional<Student> findStudentInList = studentList.stream().filter(s -> s.getRegistrationNumber().equals(indexUpperCase)).findAny();
                        if (findStudentInList.isPresent()) {
                            student = findStudentInList.get();
                            Map<String, String> newResultMap = new HashMap<>();
                            newResultMap.put(courseUpperCase, result);
                            student.getResultsMap().add(newResultMap);
                        } else {
                            student.setRegistrationNumber(indexUpperCase);
                            Map<String, String> newResultMap = new HashMap<>();
                            newResultMap.put(courseUpperCase, result);
                            student.getResultsMap().add(newResultMap);
                        }
                    }
                }
                // Add Student to list
                if (!student.getResultsMap().isEmpty()) {
                    studentList.add(student);
                }

            }
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to read the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("File upload successfully.", HttpStatus.OK);
    }

}
