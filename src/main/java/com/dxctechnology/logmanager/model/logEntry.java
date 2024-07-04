package com.dxctechnology.logmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Data
@NoArgsConstructor

@Document(collection = "logEntry")
public class logEntry {
    @Id
    private String id = UUID.randomUUID().toString();
    private Date timestamp;
    private String label;
    private String info;
    private String message;
    private String service;
    private String method_name;
    private int frequency;


    public logEntry( Timestamp timestamp, String label, String info, String message, String service, String method_name, int frequency) {
        this.timestamp = timestamp;
        this.label = label;
        this.info = info;
        this.message = message;
        this.service = service;
        this.method_name = method_name;
        this.frequency = frequency;
    }
}

