package com.example.OrangeToolzProject.controller;

import com.example.OrangeToolzProject.service.MessageResponse;
import com.example.OrangeToolzProject.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {
    @Autowired
    UserServices userServices;
    @PostMapping("/csv-upload")
    public ResponseEntity<MessageResponse> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        MessageResponse newRoute = userServices.uploadCSV(file);
        return new ResponseEntity<>(newRoute, HttpStatus.CREATED);

    }
}
