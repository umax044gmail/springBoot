package com.uop.efac.cc.award.Service;

import com.uop.efac.cc.award.dto.Student;
import com.uop.efac.cc.award.dto.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CAHEWAVITHARANAMEMORIALPRIZEService {

    @Autowired
    private ExcelExporter excelExporter;

    public ResponseEntity<String> getCAHewaAward(MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Please Select File To Upload.", HttpStatus.BAD_REQUEST);
        }

        List<Student> studentList = new ArrayList<>();

        Map<String, Double> gradeAndPoint = new HashMap<>();
        gradeAndPoint.put("A", 4.0);
        gradeAndPoint.put("A+", 4.0);
        gradeAndPoint.put("A-", 3.7);
        gradeAndPoint.put("B+", 3.3);
        gradeAndPoint.put("B", 3.3);
        gradeAndPoint.put("B-", 2.7);
        gradeAndPoint.put("C+", 2.3);
        gradeAndPoint.put("C", 2.0);
        gradeAndPoint.put("C-", 1.7);
        gradeAndPoint.put("D+", 1.3);
        gradeAndPoint.put("E", 0.0);
        gradeAndPoint.put("F", 0.0);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            boolean isFirstLine = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                Student student = new Student();

                String[] values = line.split(",");
                if (values.length >= 3) {
                    String indexUpperCase = values[0].trim().toUpperCase();
                    String courseUpperCase = values[1].trim().toUpperCase();
                    String grade = values[2].trim().toUpperCase();

                    //TODO: Please Check This E/ or e
                    if (indexUpperCase.contains("E/")) {
                        Optional<Student> findStudentInList = studentList.stream().filter(s -> s.getRegistrationNumber().equals(indexUpperCase)).findAny();
                        if (findStudentInList.isPresent()) {
                            student = findStudentInList.get();
                            // New Code
                            Map<String, String> resultDuplicate = null;

                            for (Map<String, String> oldResult : student.getResultsMap()) {
                                String oldGrade = oldResult.get(courseUpperCase);
                                if (oldGrade != null) {
                                    Double oldMark = gradeAndPoint.getOrDefault(oldGrade, 0.0);
                                    Double newMark = gradeAndPoint.getOrDefault(grade, 0.0);
                                    if (newMark >= oldMark) {
                                        resultDuplicate = oldResult;
                                        break;
                                    }
                                }
                            }

                            if (resultDuplicate != null) {
                                student.getResultsMap().remove(resultDuplicate);
                            }

                            Map<String, String> newResultMap = new HashMap<>();
                            newResultMap.put(courseUpperCase, grade);
                            student.getResultsMap().add(newResultMap);
                        } else {
                            student.setRegistrationNumber(indexUpperCase);
                            Map<String, String> newResultMap = new HashMap<>();
                            newResultMap.put(courseUpperCase, grade);
                            student.getResultsMap().add(newResultMap);
                        }

                        //Add Student To List
                        if (!findStudentInList.isPresent()) {
                            studentList.add(student);
                        }

                    }
                }
            }
            //Read CSV
            List<Subject> subjects = new ArrayList<>();
            String csvFilePath = new ClassPathResource("Courses and CreditsCE.csv").getFile().getPath();
            BufferedReader readerCredits = new BufferedReader(new FileReader(csvFilePath));
            boolean isFirstLineCredits = true;
            String lineCredit;
            try {
                while ((lineCredit = readerCredits.readLine()) != null) {
                    if (isFirstLineCredits) {
                        isFirstLineCredits = false;
                        continue;
                    }
                    System.out.println(lineCredit);
                    String[] creditValues = lineCredit.split(",");
                    if (creditValues.length >= 3) {
                        String code = creditValues[0].trim().toUpperCase();
                        String credits = creditValues[2].trim().toUpperCase();
                        Subject subject = new Subject();
                        subject.setCode(code);
                        subject.setCredits(Integer.parseInt(credits));
                        subjects.add(subject);
                    }
                }
            } catch (Exception e) {

            }
            // GPA Calculation
            for (Student student : studentList) {
                Double totalGPA = 0.0;
                Integer totalCredits = 0;
                Double averageGPA = 0.0;
                for (Map<String, String> result : student.getResultsMap()) {
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        String code = entry.getKey();
                        String grade = entry.getValue();
                        Double gpa = gradeAndPoint.get(grade);
                        if (gpa == null) {
                            gpa = 0.0;
                        }
                        Optional<Subject> subjectFromCode = subjects.stream().filter(s -> s.getCode().equals(code)).findAny();
                        if (subjectFromCode.isPresent()) {
                            Subject subjectFound = subjectFromCode.get();
                            Integer credits = subjectFound.getCredits();
                            Double gpaSubject = credits * gpa;
                            totalGPA+= gpaSubject;
                            totalCredits+= credits;
                        } else {
                            System.out.println("No subject found for code: " + code);
                        }
                    }
                }
                if(totalCredits > 0) {
                    averageGPA = totalGPA / totalCredits;
                }
                student.setTotalGPA(totalGPA);
                student.setAverageGPA(averageGPA);
                student.setTotalCredits(totalCredits);
            }
            System.out.println("Done");
            // Sorting the list by averageGPA in descending order
            List<Student> sortedStudents = studentList.stream()
                    .sorted((s1, s2) -> Double.compare(s2.getAverageGPA(), s1.getAverageGPA()))
                    .collect(Collectors.toList());

            // Printing the sorted list
            sortedStudents.forEach(student -> System.out.println(
                    "Registration Number: " + student.getRegistrationNumber() +
                            ", Average GPA: " + student.getAverageGPA()));
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to read the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        excelExporter.exportStudentsToExcel(studentList);
        return new ResponseEntity<>("File upload successfully.", HttpStatus.OK);

    }
}
