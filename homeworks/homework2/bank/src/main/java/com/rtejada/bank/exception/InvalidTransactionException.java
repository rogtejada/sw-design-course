package com.rtejada.bank.exception;

public class InvalidTransactionException extends RuntimeException {
	public InvalidTransactionException(String s) {
		super(s);
	}
}
