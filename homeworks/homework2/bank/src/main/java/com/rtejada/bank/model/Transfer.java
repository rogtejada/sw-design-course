package com.rtejada.bank.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Transfer {
	private UUID sourceId;
	private AccountType sourceType;
	private UUID targetId;
	private AccountType targetType;
	private BigDecimal amount;

	public UUID getSourceId() {
		return sourceId;
	}

	public void setSourceId(UUID sourceNumber) {
		this.sourceId = sourceNumber;
	}

	public AccountType getSourceType() {
		return sourceType;
	}

	public void setSourceType(AccountType sourceType) {
		this.sourceType = sourceType;
	}

	public UUID getTargetId() {
		return targetId;
	}

	public void setTargetId(UUID targetId) {
		this.targetId = targetId;
	}

	public AccountType getTargetType() {
		return targetType;
	}

	public void setTargetType(AccountType targetType) {
		this.targetType = targetType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
