package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidAccountException;
import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.rtejada.bank.model.Transaction.INCOME;

@Service
public class SaveAccountService implements AccountService {

	private final Map<UUID, Account> accounts;
	private static final BigDecimal WITHDRAW_FEE = BigDecimal.valueOf(1.02);
	private static final BigDecimal INCOME_FEE = BigDecimal.valueOf(0.22);

	public SaveAccountService() {
		this.accounts = new HashMap<>();
	}

	public Account createAccount(final Account account) {
		if (!AccountType.SAVING.equals(account.getAccountType())) {
			throw new IllegalArgumentException("Invalid account type");
		}

		if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new IllegalArgumentException("Cannot create account with balance:" + account.getBalance());
		}

		if (!account.getStatementList().isEmpty()) {
			throw new IllegalArgumentException("Cannot create account with statements");
		}

		if (account.getOwner() == null || account.getOwner().getCpf() == null || account.getOwner().getName() == null) {
			throw new IllegalArgumentException("Cannot create account without owner");
		}

		account.setBalance(BigDecimal.ZERO);
		account.setId(UUID.randomUUID());
		account.setLastTransaction(LocalDateTime.now());
		accounts.put(account.getId(), account);

		return account;
	}

	public Optional<Account> getAccount(final UUID accountId) {
		return Optional.ofNullable(accounts.get(accountId));
	}

	public Optional<BigDecimal> getBalance(final UUID accountId) {
		final Account account = getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId));

		final Saving savingResult = calculateIncome(account);
		account.setBalance(savingResult.getTotal());
		account.addStatements(savingResult.getSavingsStatements());
		account.setLastTransaction(savingResult.getLastTransaction());

		return Optional.ofNullable(account.getBalance());
	}

	public BigDecimal deposit(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId));

		final LocalDateTime now = LocalDateTime.now();

		account.setBalance(getBalance(accountId).orElseThrow(() -> new InvalidAccountException(accountId)).add(amount));
		account.setLastTransaction(now);
		account.addStatement(new Statement(now, amount, Transaction.DEPOSIT));

		return account.getBalance();
	}

	public BigDecimal depositForTransfer(final BigDecimal amount, final UUID accountId, final LocalDateTime transferTime) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId));

		account.setBalance(account.getBalance().add(amount));
		account.addStatement(new Statement(transferTime, amount, Transaction.TRANSFER));

		return account.getBalance();
	}


	public BigDecimal withdraw(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId));

		final BigDecimal finalBalance =
				getBalance(accountId)
						.orElseThrow(() -> new InvalidAccountException(accountId))
						.subtract(amount.multiply(WITHDRAW_FEE));

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than current balance");
		}

		final LocalDateTime now = LocalDateTime.now();

		account.setBalance(finalBalance);
		account.setLastTransaction(now);
		account.addStatement(new Statement(now, amount.negate(), Transaction.WITHDRAW));

		return account.getBalance();
	}

	public BigDecimal withdrawForTransfer(final BigDecimal amount, final UUID accountId, final LocalDateTime transferTime) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId));

		final BigDecimal finalBalance = getBalance(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId))
				.subtract(amount);

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than current balance");
		}

		account.setBalance(finalBalance);
		account.setLastTransaction(transferTime);
		account.setLastTransfer(transferTime.toLocalDate());
		account.setTransferCount(account.getTransferCount() == null ? 1L : account.getTransferCount() + 1);
		account.addStatement(new Statement(transferTime, amount.negate(), Transaction.TRANSFER));

		return account.getBalance();
	}

	public List<Statement> getStatement(final UUID accountId) {
		return getAccount(accountId)
				.orElseThrow(() -> new InvalidAccountException(accountId))
				.getStatementList();
	}

	private Saving calculateIncome(final Account account) {
		LocalDateTime lastTransaction = account.getLastTransaction();
		final long minutes = Duration.between(lastTransaction, LocalDateTime.now()).toMinutes();

		final List<Statement> statement = new ArrayList<>();
		BigDecimal currentAmount = account.getBalance();

		for (int i = 0; i < minutes; i++) {
			BigDecimal incomeAmount = currentAmount.multiply(INCOME_FEE);
			currentAmount = currentAmount.add(incomeAmount);
			lastTransaction = lastTransaction.plusMinutes(1);
			Statement income = new Statement(lastTransaction, incomeAmount, INCOME);
			statement.add(income);
		}

		return new Saving(currentAmount, lastTransaction, statement);
	}
}
