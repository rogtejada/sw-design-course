package com.rtejada.test.homework.saveaccount;

import com.rtejada.test.homework.InvalidAccountException;
import com.rtejada.test.homework.InvalidTransactionException;
import com.rtejada.test.homework.account.AccountService;
import com.rtejada.test.homework.owner.OwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Service
public class SaveAccountService {
	private static final BigDecimal INITIAL_TRANSFER_FEE = BigDecimal.valueOf(1.02);
	private static final BigDecimal TRANSFER_FEE = BigDecimal.valueOf(1.05);
	private static final BigDecimal SAVING_BONUS = BigDecimal.valueOf(0.22);
	private static final BigDecimal WITHDRAW_FEE = BigDecimal.valueOf(1.02);
	private static final HashMap<Long, BigDecimal> TRANSFER_RULES = new HashMap<Long, BigDecimal>()
	{{
		put(0L, BigDecimal.ONE);
		put(1L, BigDecimal.ONE);
		put(2L, BigDecimal.ONE);
		put(3L, BigDecimal.ONE);
		put(4L, INITIAL_TRANSFER_FEE);
	}};

	private final SaveAccountRepository saveAccountRepository;
	private final AccountService accountService;
	private final OwnerService ownerService;

	public SaveAccountService(SaveAccountRepository saveAccountRepository, AccountService accountService, OwnerService ownerService) {
		this.saveAccountRepository = saveAccountRepository;
		this.accountService = accountService;
		this.ownerService = ownerService;
	}

	public SaveAccount createAccount(SaveAccount account) {
		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new InvalidAccountException("Cannot create account with balance");
		}
		ownerService.findByCpf(account.getOwner().getCpf()).ifPresent(account::setOwner);
		return saveAccountRepository.save(account);
	}

	public SaveAccount getAccount(UUID accountId, UUID ownerId) {
		SaveAccount account = saveAccountRepository.findByIdAndOwnerId(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found"));
		account.setBalance(applyIncome(account.getBalance(), account.getLastTransaction()));
		account.setLastTransaction(LocalDateTime.now());
		return saveAccountRepository.save(account);
	}

	public void deleteAccount(UUID accountId, UUID ownerId) {
		SaveAccount account = getAccount(accountId, ownerId);
		if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
			throw new InvalidTransactionException("Cannot delete account with balance:" + account.getBalance());
		}
		saveAccountRepository.deleteByIdAndOwnerId(accountId, ownerId);
	}

	public BigDecimal deposit(BigDecimal amount, UUID accountId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot deposit negative value");
		}
		SaveAccount account = saveAccountRepository.findByIdAndOwnerId(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found"));
		account.setBalance(applyIncome(account.getBalance(), account.getLastTransaction()));
		account.setLastTransaction(LocalDateTime.now());
		account.setBalance(account.getBalance().add(amount));
		return saveAccountRepository.save(account).getBalance();
	}

	public BigDecimal withdraw(BigDecimal amount, UUID accountId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot withdraw negative value");
		}
		SaveAccount account = saveAccountRepository.findByIdAndOwnerId(accountId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found"));
		account.setBalance(applyIncome(account.getBalance(), account.getLastTransaction()));
		account.setLastTransaction(LocalDateTime.now());

		BigDecimal finalBalance = account.getBalance().subtract(amount.multiply(WITHDRAW_FEE));
		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than your current account balance");
		}
		account.setBalance(finalBalance);
		return saveAccountRepository.save(account).getBalance();
	}

	@Transactional
	public BigDecimal transferForAccount(BigDecimal amount, UUID sourceId, UUID targetId, UUID ownerId) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidTransactionException("Cannot transfer negative value");
		}
		SaveAccount sourceAccount = saveAccountRepository.findByIdAndOwnerId(sourceId, ownerId).orElseThrow(() -> new InvalidAccountException("Account not found"));
		sourceAccount.setBalance(applyIncome(sourceAccount.getBalance(), sourceAccount.getLastTransaction()));
		sourceAccount.setLastTransaction(LocalDateTime.now());

		BigDecimal finalBalance;
		if (sourceAccount.getLastTransfer() != null
				&& sourceAccount.getLastTransfer().atStartOfDay().equals(LocalDate.now().atStartOfDay())) {
			finalBalance = sourceAccount.getBalance().subtract(amount.multiply(TRANSFER_RULES.getOrDefault(sourceAccount.getTransferCount(), TRANSFER_FEE)));
			sourceAccount.setTransferCount(sourceAccount.getTransferCount() + 1);
		} else {
			sourceAccount.setTransferCount(1L);
			finalBalance = sourceAccount.getBalance().subtract(amount);
		}

		if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidTransactionException("Cannot withdraw more than your current account balance");
		}

		sourceAccount.setBalance(finalBalance);
		accountService.deposit(amount, targetId, ownerId);
		return saveAccountRepository.save(sourceAccount).getBalance();
	}

	private BigDecimal applyIncome(BigDecimal amount, LocalDateTime lastTransaction){
		long minutes = Duration.between(lastTransaction, LocalDateTime.now()).toMinutes();
		return amount.multiply(SAVING_BONUS).multiply(BigDecimal.valueOf(minutes)).add(amount);
	}
}
