package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

	public BusinessException(String message, String errorCode) {
		super(message);
	}

	public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
		super(message);
	}

}