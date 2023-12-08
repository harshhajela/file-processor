package com.example.fileprocessor.controller;

import com.example.fileprocessor.FileProcessorApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileProcessingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldUploadProcessAndDownloadFile() throws IOException {

        ClassPathResource resource = new ClassPathResource("testInputFile.txt", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", resource);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/v1/file", map, String.class, "ExpectedOutcome.json");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"OutcomeFile.json\"");

        Path expectedFilePath = Paths.get("src/test/resources/com/example/fileprocessor/controller/ExpectedOutcome.json");
        String expectedJson = Files.readString(expectedFilePath, StandardCharsets.UTF_8);

        assertThat(response.getBody()).isEqualTo(expectedJson);

    }

}