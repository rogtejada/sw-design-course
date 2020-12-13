package com.rtejada.bank.service;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.Statement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

	Account createAccount(final Account account);

	Optional<Account> getAccount(final UUID accountId);

	BigDecimal getBalance(final UUID accountId);

	BigDecimal deposit(final BigDecimal amount, final UUID accountId);

	BigDecimal depositForTransfer(final BigDecimal amount, final UUID accountId, final LocalDateTime transferTime);

	BigDecimal withdraw(final BigDecimal amount, final UUID accountId);

	BigDecimal withdrawForTransfer(final BigDecimal amount, final UUID accountId, final LocalDateTime transferTime);

	List<Statement> getStatement(final UUID accountId);
}
