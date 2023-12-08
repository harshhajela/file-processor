package com.example.fileprocessor.exception;

public class FileDirectoryNotInitialized extends RuntimeException {
    public FileDirectoryNotInitialized(String message, Throwable cause) {
        super(message, cause);
    }
}
