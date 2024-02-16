package com.uop.efac.cc.award.controller;

import com.uop.efac.cc.award.Service.CAHEWAVITHARANAMEMORIALPRIZEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
public class UploadCSVController {

    @Autowired
    private CAHEWAVITHARANAMEMORIALPRIZEService cahewavitharanamemorialprizeService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        return cahewavitharanamemorialprizeService.getCAHewaAward(file);
    }

}
