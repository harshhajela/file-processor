package com.example.fileprocessor;

import com.example.fileprocessor.service.FileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FileProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProcessorApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FileService fileService) {
        return (args) -> {
            fileService.deleteAll();
            fileService.init();
        };
    }
}
