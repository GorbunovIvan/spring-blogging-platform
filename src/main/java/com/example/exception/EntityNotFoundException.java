package com.example.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, long id) {
        super(String.format("%s with id '%d' is not found", entityName, id));
    }
}
