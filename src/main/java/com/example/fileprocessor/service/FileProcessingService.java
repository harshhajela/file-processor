package com.example.fileprocessor.service;

import com.example.fileprocessor.config.StorageConfiguration;
import com.example.fileprocessor.exception.FileDirectoryNotInitialized;
import com.example.fileprocessor.exception.FileStorageException;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService implements FileService {

    private final StorageConfiguration storageConfiguration;
    private final Path root = Paths.get("files");

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageConfiguration.getUploadPath()));
            Files.createDirectories(Paths.get(storageConfiguration.getOutputPath()));
        } catch (IOException e) {
            throw new FileDirectoryNotInitialized("Could not initialize storage", e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public void uploadFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Cannot save empty file");
            }
            Path destination = Paths.get(storageConfiguration.getUploadPath())
                    .resolve(file.getOriginalFilename())
                    .normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destination);
            log.info("File {} uploaded successfully", file.getOriginalFilename());
        } catch (IOException e) {
            throw new FileStorageException("Store exception", e);
        }
    }

    @Override
    public void processFile(String fileName) throws MalformedURLException {
        Path file = Paths.get(storageConfiguration.getUploadPath()).resolve(fileName);
        Resource resource = new UrlResource(file.toUri());
        List<JsonEntry> fileContent = new ArrayList<>();

        if (resource.exists() || resource.isReadable()) {
            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    JsonEntry jsonEntry = parseLine(line);
                    fileContent.add(jsonEntry);
                }
            } catch (IOException e) {
                throw new FileStorageException("Cannot read file", e);
            }
            createJsonFile(fileContent);
            log.info("Processed file successfully");
        } else {
            throw new FileStorageException("Cannot read file");
        }
    }

    @Override
    public Resource loadOutputFile() {
        try {
            Path file = Paths.get(storageConfiguration.getOutputPath())
                    .resolve(storageConfiguration.getOutputFileName());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Could not read the output file");
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Error", e);
        }
    }

    private void createJsonFile(List<JsonEntry> fileContent) {
        Path outputPath = Paths.get(storageConfiguration.getOutputPath())
                .resolve(storageConfiguration.getOutputFileName());
        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            Gson gson = new Gson();
            String json = gson.toJson(fileContent);
            writer.write(json);
            log.info("JSON file created successfully at: {}", outputPath);
        } catch (IOException e) {
            // Handle the exception
            throw new FileStorageException("Cannot write to json file", e);
        }

    }

    private JsonEntry parseLine(String line) {
        String[] parts = line.split("\\|");
        String name = parts[2];
        String transport = parts[4];
        String topSpeed = parts[6];

        return new JsonEntry(name, transport, topSpeed);
    }
}
