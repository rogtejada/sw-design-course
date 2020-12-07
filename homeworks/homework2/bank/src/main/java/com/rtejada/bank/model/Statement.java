package com.rtejada.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Statement {

	private LocalDateTime date;
	private BigDecimal value;
	private Transaction transaction;

	public Statement(LocalDateTime date, BigDecimal value, Transaction transaction) {
		this.date = date;
		this.value = value;
		this.transaction = transaction;
	}

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
