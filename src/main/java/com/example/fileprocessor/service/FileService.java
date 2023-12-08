package com.example.fileprocessor.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface FileService {

    void init();

    void deleteAll();

    void uploadFile(MultipartFile file);

    void processFile(String fileName) throws MalformedURLException;

    Resource loadOutputFile();
}
