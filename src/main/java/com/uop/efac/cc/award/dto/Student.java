package com.uop.efac.cc.award.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student {
    private String registrationNumber;
    private Double totalGPA = 0.0;
    private Double averageGPA = 0.0;
    private Integer totalCredits = 0;
    private List<Map<String, String>> resultsMap = new ArrayList<>();

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<Map<String, String>> getResultsMap() {
        return resultsMap;
    }

    public void setResultsMap(List<Map<String, String>> resultsMap) {
        this.resultsMap = resultsMap;
    }

    public Double getTotalGPA() {
        return totalGPA;
    }

    public void setTotalGPA(Double totalGPA) {
        this.totalGPA = totalGPA;
    }

    public Double getAverageGPA() {
        return averageGPA;
    }

    public void setAverageGPA(Double averageGPA) {
        this.averageGPA = averageGPA;
    }

    public Integer getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Integer totalCredits) {
        this.totalCredits = totalCredits;
    }
}
