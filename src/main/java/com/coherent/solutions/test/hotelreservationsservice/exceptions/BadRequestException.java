package com.coherent.solutions.test.hotelreservationsservice.exceptions;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }

}
