package com.rtejada.bank.exception;

import java.util.UUID;

public class InvalidAccountException extends RuntimeException {
	public InvalidAccountException(UUID accountID) {
		super("Account not found: " + accountID);
	}
}
