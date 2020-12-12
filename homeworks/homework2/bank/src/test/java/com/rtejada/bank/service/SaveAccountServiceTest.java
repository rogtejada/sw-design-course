package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidAccountException;
import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SaveAccountServiceTest {

	private SaveAccountService saveAccountService;

	@BeforeEach
	public void setUp() {
		saveAccountService = new SaveAccountService();
	}

	@Test
	public void shouldCreateAccount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.ZERO);
		account.setOwner(buildOwner());

		Account result = saveAccountService.createAccount(account);

		assertNotNull(result.getId());
		assertEquals(BigDecimal.ZERO, account.getBalance());
		assertTrue(account.getStatementList().isEmpty());
	}

	@Test
	public void shouldAllowCreateAccountWithNullBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account result = saveAccountService.createAccount(account);

		assertNotNull(result.getId());
		assertEquals(BigDecimal.ZERO, account.getBalance());
		assertTrue(account.getStatementList().isEmpty());
	}

	@Test
	public void shouldNotAllowInvalidAccountType() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN);

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithNegativeBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN.negate());

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithStatements() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN.negate());
		account.addStatement(new Statement(LocalDateTime.now(), BigDecimal.TEN, Transaction.TRANSFER));

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwner() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(null);

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwnerCpf() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);

		Owner owner = new Owner();
		owner.setName("jonas");
		owner.setCpf(null);

		account.setOwner(owner);

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwnerName() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);

		Owner owner = new Owner();
		owner.setName(null);
		owner.setCpf("03214231230");

		account.setOwner(owner);

		assertThrows(
				IllegalArgumentException.class,
				() -> saveAccountService.createAccount(account));
	}

	@Test
	public void shouldGetBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		BigDecimal result = saveAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);
	}

	@Test
	public void shouldGetBalanceWithIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(1L));
		BigDecimal result = saveAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(1.22)), result);
	}

	@Test
	public void shouldGetBalanceWithCompoundIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(2L));
		BigDecimal result = saveAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(1.22)).multiply(BigDecimal.valueOf(1.22)), result);
	}

	@Test
	public void shouldGetBalanceEvenWhenAccountIsEmpty() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);

		BigDecimal result = saveAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldNotAllowGetBalanceFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.getBalance(UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowGetBalanceFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.getBalance(null));
	}

	@Test
	public void shouldDepositSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		BigDecimal result = saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);
	}

	@Test
	public void shouldDepositSuccessfullyWithIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		accountCreated.setBalance(BigDecimal.TEN);
		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(1L));
		BigDecimal result = saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(1.22)).add(BigDecimal.TEN), result);
	}

	@Test
	public void shouldDepositSuccessfullyWithCompoundIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		accountCreated.setBalance(BigDecimal.TEN);
		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(2L));
		BigDecimal result = saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(1.22)).multiply(BigDecimal.valueOf(1.22)).add(BigDecimal.TEN), result);
	}

	@Test
	public void shouldDepositSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = saveAccountService.createAccount(account);
		BigDecimal result = saveAccountService.deposit(hugeAmount, accountCreated.getId());

		assertEquals(hugeAmount, result);
	}

	@Test
	public void shouldNotAllowDepositNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.deposit(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositNoValue() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.deposit(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.deposit(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowDepositForNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.deposit(BigDecimal.TEN, null));
	}

	@Test
	public void shouldDepositForTransferSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		BigDecimal result = saveAccountService.depositForTransfer(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);

		final Account accountResult = saveAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldDepositForTransferSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = saveAccountService.createAccount(account);
		BigDecimal result = saveAccountService.depositForTransfer(hugeAmount, accountCreated.getId());

		assertEquals(hugeAmount, result);

		final Account accountResult = saveAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldNotAllowDepositForTransferNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.depositForTransfer(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForTransferNoValue() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.depositForTransfer(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForTransferForInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.depositForTransfer(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowDepositForTransferForNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.depositForTransfer(BigDecimal.TEN, null));
	}

	@Test
	public void shouldWithdrawSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = saveAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		assertEquals(BigDecimal.valueOf(898), result.stripTrailingZeros());
	}

	@Test
	public void shouldWithdrawSuccessfullyWithIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(1L));
		BigDecimal result = saveAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		BigDecimal expectedResult = BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(1.22)).subtract(BigDecimal.valueOf(102));
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldWithdrawSuccessfullyWithCompoundIncome() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(2L));
		BigDecimal result = saveAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		BigDecimal expectedResult = BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(1.22)).multiply(BigDecimal.valueOf(1.22)).subtract(BigDecimal.valueOf(102));
		assertEquals(expectedResult, result);
	}

	@Test
	public void shouldWithdrawAllMoneySuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1020), accountCreated.getId());

		BigDecimal result = saveAccountService.withdraw(BigDecimal.valueOf(1000), accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result.stripTrailingZeros());
	}

	@Test
	public void shouldNotAllowWithdrawNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdraw(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawNoAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdraw(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.withdraw(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowWithdrawFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.withdraw(BigDecimal.TEN, null));
	}

	@Test
	public void shouldNotAllowWithdrawMoreThanCurrentBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdraw(BigDecimal.valueOf(5000), accountCreated.getId()));
	}

	@Test
	public void shouldWithdrawForTransferSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		accountCreated.setLastTransaction(LocalDateTime.now().minusMinutes(1L));
		BigDecimal result = saveAccountService.withdrawForTransfer(BigDecimal.valueOf(100), accountCreated.getId());

		BigDecimal expectedResult = BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(1.22)).subtract(BigDecimal.valueOf(100));
		assertEquals(expectedResult, result);

		final Account accountResult = saveAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldWithdrawForTransferAllMoneySuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = saveAccountService.withdrawForTransfer(BigDecimal.valueOf(1000), accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldWithdrawForTransferSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(hugeAmount, accountCreated.getId());

		BigDecimal result = saveAccountService.withdrawForTransfer(hugeAmount, accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);

		final Account accountResult = saveAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldNotAllowWithdrawForTransferNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdrawForTransfer(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferNoAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdrawForTransfer(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.withdrawForTransfer(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> saveAccountService.withdrawForTransfer(BigDecimal.TEN, null));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferMoreThanCurrentBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> saveAccountService.withdrawForTransfer(BigDecimal.valueOf(5000), accountCreated.getId()));
	}

	@Test
	public void shouldGetEmptyStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);

		final List<Statement> result = saveAccountService.getStatement(accountCreated.getId());

		assertTrue(result.isEmpty());
	}

	@Test
	public void shouldGetOneDepositStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		final List<Statement> result = saveAccountService.getStatement(accountCreated.getId());

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(Transaction.DEPOSIT, result.get(0).getTransaction());
		assertEquals(BigDecimal.TEN, result.get(0).getValue());
	}

	@Test
	public void shouldGetOneDepositAndWithdrawStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = saveAccountService.createAccount(account);
		saveAccountService.deposit(BigDecimal.valueOf(12), accountCreated.getId());
		saveAccountService.withdraw(BigDecimal.TEN, accountCreated.getId());

		final List<Statement> result = saveAccountService.getStatement(accountCreated.getId());

		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(Transaction.DEPOSIT, result.get(0).getTransaction());
		assertEquals(BigDecimal.valueOf(12), result.get(0).getValue());
		assertEquals(Transaction.WITHDRAW, result.get(1).getTransaction());
		assertEquals(BigDecimal.TEN.negate(), result.get(1).getValue());
	}

	private Owner buildOwner() {
		Owner owner = new Owner();
		owner.setCpf("12345678910");
		owner.setName("joao");

		return owner;
	}
}
