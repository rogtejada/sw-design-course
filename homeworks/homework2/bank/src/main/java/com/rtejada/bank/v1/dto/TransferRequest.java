package com.rtejada.bank.v1.dto;

import com.rtejada.bank.model.AccountType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class TransferRequest {

	@NotNull
	private UUID sourceId;

	@NotNull
	private AccountType sourceType;

	@NotNull
	private UUID targetId;

	@NotNull
	private AccountType targetType;

	@NotNull
	private BigDecimal amount;

	public UUID getSourceId() {
		return sourceId;
	}

	public void setSourceId(UUID sourceId) {
		this.sourceId = sourceId;
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
