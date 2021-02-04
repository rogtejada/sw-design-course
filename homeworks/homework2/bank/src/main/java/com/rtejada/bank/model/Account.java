package com.rtejada.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Account {
	private UUID id;
	private BigDecimal balance;
	private AccountType accountType;
	private List<Statement> statementList;
	private Owner owner;
	private LocalDateTime lastTransaction;
	private LocalDate lastWithdraw;
	private Long withdrawCount;
	private LocalDate lastTransfer;
	private Long transferCount;

	public Account() {
		this.statementList = new ArrayList<>();
	}

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

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public List<Statement> getStatementList() {
		return Collections.unmodifiableList(statementList);
	}

	public void addStatements(final List<Statement> statements) {
		this.statementList.addAll(statements);
	}

	public void addStatement(final Statement statement) {
		this.statementList.add(statement);
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public LocalDateTime getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(LocalDateTime lastTransaction) {
		this.lastTransaction = lastTransaction;
	}

	public void setStatementList(List<Statement> statementList) {
		this.statementList = statementList;
	}

	public LocalDate getLastWithdraw() {
		return lastWithdraw;
	}

	public void setLastWithdraw(LocalDate lastWithdraw) {
		this.lastWithdraw = lastWithdraw;
	}

	public Long getWithdrawCount() {
		return withdrawCount;
	}

	public void setWithdrawCount(Long withdrawCount) {
		this.withdrawCount = withdrawCount;
	}

	public LocalDate getLastTransfer() {
		return lastTransfer;
	}

	public void setLastTransfer(LocalDate lastTransfer) {
		this.lastTransfer = lastTransfer;
	}

	public Long getTransferCount() {
		return transferCount;
	}

	public void setTransferCount(Long transferCount) {
		this.transferCount = transferCount;
	}
}
