package com.db.awmd.challenge.exception;

public class OverDraftException extends BusinessException {
	public OverDraftException(String message, String errorCode) {
		super(message, errorCode);
	}
}
