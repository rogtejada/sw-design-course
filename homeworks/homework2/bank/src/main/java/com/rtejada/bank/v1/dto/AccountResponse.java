package com.rtejada.bank.v1.dto;

import com.rtejada.bank.model.AccountType;

import java.util.UUID;

public class AccountResponse {

	private UUID id;
	private AccountType accountType;
	private OwnerResponse owner;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public OwnerResponse getOwner() {
		return owner;
	}

	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}
}
