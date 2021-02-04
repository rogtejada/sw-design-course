package com.rtejada.bank.v1.dto;

import com.rtejada.bank.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StatementResponse {

	private LocalDateTime date;
	private BigDecimal value;
	private Transaction transaction;

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
}
