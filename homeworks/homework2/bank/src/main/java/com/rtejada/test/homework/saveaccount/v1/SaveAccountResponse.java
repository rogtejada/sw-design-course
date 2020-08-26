package com.rtejada.test.homework.saveaccount.v1;

import com.rtejada.test.homework.owner.v1.OwnerResponse;

import java.math.BigDecimal;
import java.util.UUID;

public class SaveAccountResponse {

	private UUID id;
	private BigDecimal balance;
	private OwnerResponse owner;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public OwnerResponse getOwner() {
		return owner;
	}

	public void setOwner(OwnerResponse owner) {
		this.owner = owner;
	}
}
