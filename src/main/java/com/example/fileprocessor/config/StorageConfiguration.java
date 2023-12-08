package com.example.fileprocessor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("file")
public class StorageConfiguration {

    private String uploadPath;
    private String outputPath;
    private String outputFileName;
    private List<String> inputStructure;
    private List<String> outputStructure;

}
