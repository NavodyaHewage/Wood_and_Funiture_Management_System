package com.group_project.wfms_backend.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}// use define exaception insatead of nullpoint exception