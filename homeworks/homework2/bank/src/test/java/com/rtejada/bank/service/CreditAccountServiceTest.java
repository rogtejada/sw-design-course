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

public class CreditAccountServiceTest {

	private CreditAccountService creditAccountService;

	@BeforeEach
	public void setUp() {
		creditAccountService = new CreditAccountService();
	}

	@Test
	public void shouldCreateAccount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(BigDecimal.ZERO);
		account.setOwner(buildOwner());

		Account result = creditAccountService.createAccount(account);

		assertNotNull(result.getId());
		assertEquals(BigDecimal.ZERO, account.getBalance());
		assertTrue(account.getStatementList().isEmpty());
	}

	@Test
	public void shouldAllowCreateAccountWithNullBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account result = creditAccountService.createAccount(account);

		assertNotNull(result.getId());
		assertEquals(BigDecimal.ZERO, account.getBalance());
		assertTrue(account.getStatementList().isEmpty());
	}

	@Test
	public void shouldNotAllowInvalidAccountType() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN);

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithNegativeBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN.negate());

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithStatements() {
		final Account account =  new Account();
		account.setAccountType(AccountType.SAVING);
		account.setBalance(BigDecimal.TEN.negate());
		account.addStatement(new Statement(LocalDateTime.now(), BigDecimal.TEN, Transaction.TRANSFER));

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwner() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(null);

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwnerCpf() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);

		Owner owner = new Owner();
		owner.setName("jonas");
		owner.setCpf(null);

		account.setOwner(owner);

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldNotAllowAccountWithoutOwnerName() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);

		Owner owner = new Owner();
		owner.setName(null);
		owner.setCpf("03214231230");

		account.setOwner(owner);

		assertThrows(
				IllegalArgumentException.class,
				() -> creditAccountService.createAccount(account));
	}

	@Test
	public void shouldGetBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		BigDecimal result = creditAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);
	}

	@Test
	public void shouldGetBalanceEvenWhenAccountIsEmpty() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);

		BigDecimal result = creditAccountService.getBalance(accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldNotAllowGetBalanceFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.getBalance(UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowGetBalanceFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.getBalance(null));
	}

	@Test
	public void shouldDepositSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		BigDecimal result = creditAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);
	}

	@Test
	public void shouldDepositSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = creditAccountService.createAccount(account);
		BigDecimal result = creditAccountService.deposit(hugeAmount, accountCreated.getId());

		assertEquals(hugeAmount, result);
	}

	@Test
	public void shouldNotAllowDepositNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.deposit(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositNoValue() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.deposit(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.deposit(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowDepositForNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.deposit(BigDecimal.TEN, null));
	}

	@Test
	public void shouldDepositForTransferSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		BigDecimal result = creditAccountService.depositForTransfer(BigDecimal.TEN, accountCreated.getId());

		assertEquals(BigDecimal.TEN, result);

		final Account accountResult = creditAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldDepositForTransferSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = creditAccountService.createAccount(account);
		BigDecimal result = creditAccountService.depositForTransfer(hugeAmount, accountCreated.getId());

		assertEquals(hugeAmount, result);

		final Account accountResult = creditAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldNotAllowDepositForTransferNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.depositForTransfer(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForTransferNoValue() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.depositForTransfer(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowDepositForTransferForInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.depositForTransfer(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowDepositForTransferForNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.depositForTransfer(BigDecimal.TEN, null));
	}

	@Test
	public void shouldWithdrawSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		assertEquals(BigDecimal.valueOf(900), result);
	}

	@Test
	public void shouldWithdrawWithInitialFeeSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		final BigDecimal result = creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		assertEquals(BigDecimal.valueOf(598), result.stripTrailingZeros());
	}

	@Test
	public void shouldWithdrawWithFinalFeeSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());
		final BigDecimal result = creditAccountService.withdraw(BigDecimal.valueOf(100), accountCreated.getId());

		assertEquals(BigDecimal.valueOf(493), result.stripTrailingZeros());
	}

	@Test
	public void shouldWithdrawAllMoneySuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = creditAccountService.withdraw(BigDecimal.valueOf(1000), accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldWithdrawSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(hugeAmount, accountCreated.getId());

		BigDecimal result = creditAccountService.withdraw(hugeAmount, accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldNotAllowWithdrawNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdraw(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawNoAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdraw(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.withdraw(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowWithdrawFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.withdraw(BigDecimal.TEN, null));
	}

	@Test
	public void shouldNotAllowWithdrawMoreThanCurrentBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdraw(BigDecimal.valueOf(5000), accountCreated.getId()));
	}

	@Test
	public void shouldWithdrawForTransferSuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = creditAccountService.withdrawForTransfer(BigDecimal.valueOf(100), accountCreated.getId());

		assertEquals(BigDecimal.valueOf(900), result);

		final Account accountResult = creditAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldWithdrawForTransferAllMoneySuccessfully() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		BigDecimal result = creditAccountService.withdrawForTransfer(BigDecimal.valueOf(1000), accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);
	}

	@Test
	public void shouldWithdrawForTransferSuccessfullyHugeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(hugeAmount, accountCreated.getId());

		BigDecimal result = creditAccountService.withdrawForTransfer(hugeAmount, accountCreated.getId());

		assertEquals(BigDecimal.ZERO, result);

		final Account accountResult = creditAccountService.getAccount(accountCreated.getId());

		assertEquals(Transaction.TRANSFER, accountResult.getStatementList().get(accountResult.getStatementList().size() - 1).getTransaction());
	}

	@Test
	public void shouldNotAllowWithdrawForTransferNegativeAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdrawForTransfer(BigDecimal.TEN.negate(), accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferNoAmount() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdrawForTransfer(BigDecimal.ZERO, accountCreated.getId()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferFromInvalidAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.withdrawForTransfer(BigDecimal.TEN, UUID.randomUUID()));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferFromNullAccount() {
		assertThrows(
				InvalidAccountException.class, () -> creditAccountService.withdrawForTransfer(BigDecimal.TEN, null));
	}

	@Test
	public void shouldNotAllowWithdrawForTransferMoreThanCurrentBalance() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.valueOf(1000), accountCreated.getId());

		assertThrows(
				InvalidTransactionException.class, () -> creditAccountService.withdrawForTransfer(BigDecimal.valueOf(5000), accountCreated.getId()));
	}


	@Test
	public void shouldGetEmptyStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);

		final List<Statement> result = creditAccountService.getStatement(accountCreated.getId());

		assertTrue(result.isEmpty());
	}

	@Test
	public void shouldGetOneDepositStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());
		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.TEN, accountCreated.getId());

		final List<Statement> result = creditAccountService.getStatement(accountCreated.getId());

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(Transaction.DEPOSIT, result.get(0).getTransaction());
		assertEquals(BigDecimal.TEN, result.get(0).getValue());
	}

	@Test
	public void shouldGetOneDepositAndWithdrawStatement() {
		final Account account =  new Account();
		account.setAccountType(AccountType.CREDIT);
		account.setBalance(null);
		account.setOwner(buildOwner());

		Account accountCreated = creditAccountService.createAccount(account);
		creditAccountService.deposit(BigDecimal.TEN, accountCreated.getId());
		creditAccountService.withdraw(BigDecimal.TEN, accountCreated.getId());

		final List<Statement> result = creditAccountService.getStatement(accountCreated.getId());

		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
		assertEquals(Transaction.DEPOSIT, result.get(0).getTransaction());
		assertEquals(BigDecimal.TEN, result.get(0).getValue());
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
