package com.fatec.donation.jwt;

public class InvalidTokenException extends RuntimeException{public InvalidTokenException(String message) {
    super(message);
}
}
