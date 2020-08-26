package com.rtejada.test.homework.account;

import com.rtejada.test.homework.InvalidAccountException;
import com.rtejada.test.homework.InvalidTransactionException;
import com.rtejada.test.homework.owner.OwnerService;
import com.rtejada.test.homework.saveaccount.SaveAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
	private static final BigDecimal TRANSFER_FEE = BigDecimal.valueOf(1.05);
	private static final BigDecimal INITIAL_WITHDRAW_FEE = BigDecimal.valueOf(1.02);
	private static final BigDecimal FINAL_WITHDRAW_FEE = BigDecimal.valueOf(1.05);
	private static final HashMap<Long, BigDecimal> WITHDRAW_RULES = new HashMap<Long, BigDecimal>()
	{{
		put(0L, BigDecimal.ONE);
		put(1L, BigDecimal.ONE);
		put(2L, BigDecimal.ONE);
		put(3L, BigDecimal.ONE);
		put(4L, INITIAL_WITHDRAW_FEE);
	}};

	private final AccountRepository accountRepository;
	private final SaveAccountService saveAccountService;
	private final OwnerService ownerService;

	public AccountService(AccountRepository accountRepository, SaveAccountService saveAccountService, OwnerService ownerService) {
		this.accountRepository = accountRepository;
		this.saveAccountService = saveAccountService;
		this.ownerService = ownerService;
	}

	public Account createAccount(Account account) {
		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new InvalidAccountException("Cannot create account with balance:" + account.getBalance());
		}

		ownerService.findByCpf(account.getOwner().getCpf()).ifPresent(account::setOwner);

		return accountRepository.save(account);
	}

	public Optional<Account> getAccount(UUID accountID, UUID ownerId) {
		return accountRepository.findByIdAndOwnerId(accountID, ownerId);
	}

	public void deleteAccount(UUID accountId, UUID ownerId) {
		Account account = getAccount(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found with id:" + accountId));
		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new InvalidTransactionException("Cannot delete account with balance:" + account.getBalance());
		}
		accountRepository.deleteById(accountId);
	}

	public BigDecimal deposit(BigDecimal amount, UUID accountId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}
		Account account = getAccount(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found with id:" + accountId));
		BigDecimal finalBalance = account.getBalance().add(amount);
		account.setBalance(finalBalance);
		accountRepository.save(account);
		return finalBalance;
	}

	public BigDecimal withdraw(BigDecimal amount, UUID accountId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot withdraw negative value");
		}
		Account account = getAccount(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found with id:" + accountId));
		BigDecimal finalBalance;
		if (account.getLastWithdraw() != null && account.getLastWithdraw().atStartOfDay().equals(LocalDate.now().atStartOfDay())) {
			finalBalance = account.getBalance().subtract(amount.multiply(WITHDRAW_RULES.getOrDefault(account.getWithdrawCount(), FINAL_WITHDRAW_FEE)));
			account.setWithdrawCount(account.getWithdrawCount() + 1);
		} else {
			account.setWithdrawCount(1L);
			finalBalance = account.getBalance().subtract(amount);
		}

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than your current account balance");
		}
		account.setBalance(finalBalance);
		account.setLastWithdraw(LocalDate.now());
		accountRepository.save(account);
		return finalBalance;
	}

	@Transactional
	public BigDecimal transfer(BigDecimal amount, UUID sourceId, UUID targetId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot transfer negative value");
		}
		Account sourceAccount = getAccount(sourceId, ownerId).orElseThrow(() -> new InvalidAccountException("Source account not found with id:" + sourceId));
		BigDecimal finalBalance = sourceAccount.getBalance().subtract(amount.multiply(TRANSFER_FEE));
		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot transfer more than your current balance");
		}
		Account targetAccount = accountRepository.findById(targetId).orElseThrow(() -> new InvalidAccountException("Target account not found with id:" + targetId));
		sourceAccount.setBalance(finalBalance);
		targetAccount.setBalance(targetAccount.getBalance().add(amount));
		accountRepository.save(sourceAccount);
		accountRepository.save(targetAccount);
		return sourceAccount.getBalance();
	}

	@Transactional
	public BigDecimal transferToSaving(BigDecimal value, UUID sourceId, UUID targetId, UUID ownerId) {
		if (value.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot transfer negative value");
		}
		Account sourceAccount = getAccount(sourceId, ownerId).orElseThrow(() -> new InvalidAccountException("Source account not found with id:" + sourceId));
		BigDecimal finalBalance = sourceAccount.getBalance().subtract(value);
		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot transfer more than your current balance");
		}
		sourceAccount.setBalance(finalBalance);
		saveAccountService.deposit(value, targetId, ownerId);
		return accountRepository.save(sourceAccount).getBalance();
	}
}
