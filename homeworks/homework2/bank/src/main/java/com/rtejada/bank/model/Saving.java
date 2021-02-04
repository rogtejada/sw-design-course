package com.rtejada.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Saving {

	private BigDecimal total;
	private LocalDateTime lastTransaction;
	private List<Statement> savingsStatements;

	public Saving(BigDecimal total, LocalDateTime lastTransaction, List<Statement> savingsStatements) {
		this.total = total;
		this.lastTransaction = lastTransaction;
		this.savingsStatements = savingsStatements;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<Statement> getSavingsStatements() {
		return savingsStatements;
	}

	public void setSavingsStatements(List<Statement> savingsStatements) {
		this.savingsStatements = savingsStatements;
	}

	public LocalDateTime getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(LocalDateTime lastTransaction) {
		this.lastTransaction = lastTransaction;
	}
}
