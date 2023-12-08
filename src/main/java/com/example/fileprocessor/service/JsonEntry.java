package com.example.fileprocessor.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonEntry {
    private String name;
    private String transport;
    private String topSpeed;
}
