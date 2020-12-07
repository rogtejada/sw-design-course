package com.rtejada.bank.service;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.Statement;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {

	Account createAccount(Account account);

	Account getAccount(final UUID accountId);

	BigDecimal getBalance(UUID accountId);

	BigDecimal deposit(BigDecimal amount, UUID accountId);

	BigDecimal depositForTransfer(BigDecimal amount, UUID accountId);

	BigDecimal withdraw(BigDecimal amount, UUID accountId);

	BigDecimal withdrawForTransfer(final BigDecimal amount, final UUID accountId);

	List<Statement> getStatement(UUID accountId);
}
