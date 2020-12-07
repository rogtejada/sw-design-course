package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidAccountException;
import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rtejada.bank.model.Transaction.WITHDRAW;

@Service
public class CreditAccountService implements AccountService {

	private static final BigDecimal INITIAL_WITHDRAW_FEE = BigDecimal.valueOf(1.02);
	private static final BigDecimal FINAL_WITHDRAW_FEE = BigDecimal.valueOf(1.05);
	private static final Map<Long, BigDecimal> WITHDRAW_RULES = buildWithdrawRules();
	private final Map<UUID, Account> accounts;

	public CreditAccountService() {
		this.accounts = new HashMap<>();
	}

	public Account createAccount(final Account account) {
		if (!AccountType.CREDIT.equals(account.getAccountType())) {
			throw new IllegalArgumentException("Invalid account type");
		}

		if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new IllegalArgumentException("Cannot create account with balance:" + account.getBalance());
		}

		if (!account.getStatement().isEmpty()) {
			throw new IllegalArgumentException("Cannot create account with statements");
		}

		if (account.getOwner() == null || account.getOwner().getCpf() == null || account.getOwner().getName() == null) {
			throw new IllegalArgumentException("Cannot create account without owner");
		}

		account.setId(UUID.randomUUID());
		account.setBalance(BigDecimal.ZERO);
		accounts.put(account.getId(), account);
		return account;
	}

	public Account getAccount(final UUID accountId) {
		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		return account;
	}

	public BigDecimal getBalance(final UUID accountId) {
		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		return account.getBalance();
	}

	public BigDecimal deposit(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		account.setBalance(account.getBalance().add(amount));
		account.addStatement(new Statement(LocalDateTime.now(), amount, Transaction.DEPOSIT));

		return account.getBalance();
	}

	public BigDecimal depositForTransfer(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		account.setBalance(account.getBalance().add(amount));
		account.addStatement(new Statement(LocalDateTime.now(), amount, Transaction.TRANSFER));

		return account.getBalance();
	}

	public BigDecimal withdraw(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		BigDecimal finalBalance;

		if (account.getLastWithdraw() != null && account.getLastWithdraw().atStartOfDay().equals(LocalDate.now().atStartOfDay())) {
			finalBalance = account.getBalance().subtract(amount.multiply(WITHDRAW_RULES.getOrDefault(account.getWithdrawCount(), FINAL_WITHDRAW_FEE)));
			account.setWithdrawCount(account.getWithdrawCount() + 1);

		} else {
			account.setWithdrawCount(1L);
			finalBalance = account.getBalance().subtract(amount);
		}

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than current balance");
		}

		account.setBalance(finalBalance);
		account.setLastWithdraw(LocalDate.now());
		account.addStatement(new Statement(LocalDateTime.now(), amount.negate(), WITHDRAW));

		return account.getBalance();
	}

	public BigDecimal withdrawForTransfer(final BigDecimal amount, final UUID accountId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}

		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		final BigDecimal finalBalance = account.getBalance().subtract(amount);

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than current balance");
		}

		account.setBalance(finalBalance);
		account.setLastTransaction(LocalDateTime.now());
		account.setLastTransfer(LocalDate.now());
		account.setTransferCount(account.getTransferCount() == null ? 1L : account.getTransferCount() + 1);
		account.addStatement(new Statement(LocalDateTime.now(), amount.negate(), Transaction.TRANSFER));

		return account.getBalance();
	}

	public List<Statement> getStatement(final UUID accountId) {
		final Account account = accounts.get(accountId);

		if (account == null) {
			throw new InvalidAccountException(accountId);
		}

		return account.getStatement();
	}

	private static Map<Long, BigDecimal> buildWithdrawRules() {
		Map<Long, BigDecimal> withdrawRules = new HashMap<>();
		withdrawRules.put(0L, BigDecimal.ONE);
		withdrawRules.put(1L, BigDecimal.ONE);
		withdrawRules.put(2L, BigDecimal.ONE);
		withdrawRules.put(3L, INITIAL_WITHDRAW_FEE);

		return withdrawRules;
	}
}
