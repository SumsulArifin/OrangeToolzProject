package com.example.OrangeToolzProject.service;

import com.example.OrangeToolzProject.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component

public interface UserServices {
    public MessageResponse uploadCSV(MultipartFile file);

    public List<User> getAll();


}
