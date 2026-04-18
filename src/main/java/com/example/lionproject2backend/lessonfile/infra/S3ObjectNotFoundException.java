package com.example.lionproject2backend.lessonfile.infra;

public class S3ObjectNotFoundException extends RuntimeException {

    public S3ObjectNotFoundException(String key) {
        super("S3 object not found: " + key);
    }
}
