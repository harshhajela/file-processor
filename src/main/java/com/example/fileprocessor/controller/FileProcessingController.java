package com.example.fileprocessor.controller;

import com.example.fileprocessor.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/v1/file")
@RequiredArgsConstructor
public class FileProcessingController {

    private final FileProcessingService fileProcessingService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Resource> processFile(@RequestPart("file") MultipartFile file) throws MalformedURLException {
        //upload file
        fileProcessingService.uploadFile(file);
        //process file to outcome
        fileProcessingService.processFile(file.getOriginalFilename());
        //load the output file and return
        Resource resource = fileProcessingService.loadOutputFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
