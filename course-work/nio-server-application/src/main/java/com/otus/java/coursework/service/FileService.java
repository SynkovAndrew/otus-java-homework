package com.otus.java.coursework.service;

import com.otus.java.coursework.dto.BaseDTO;

public interface FileService {
    void writeToFile(String fileName, BaseDTO dto);
}
