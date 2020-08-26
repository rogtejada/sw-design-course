package com.rtejada.test.homework.saveaccount;

import com.rtejada.test.homework.InvalidAccountException;
import com.rtejada.test.homework.InvalidTransactionException;
import com.rtejada.test.homework.account.AccountService;
import com.rtejada.test.homework.owner.Owner;
import com.rtejada.test.homework.owner.OwnerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SaveAccountServiceTest {

	private static final BigDecimal SAVING_BONUS = BigDecimal.valueOf(0.22);


	private SaveAccountRepository saveAccountRepository;
	private SaveAccountService saveAccountService;
	private OwnerService ownerService;
	private AccountService accountService;

	@Before
	public void setUp() {
		saveAccountRepository = Mockito.mock(SaveAccountRepository.class);
		ownerService = Mockito.mock(OwnerService.class);
		accountService = Mockito.mock(AccountService.class);
		saveAccountService = new SaveAccountService(saveAccountRepository, accountService, ownerService);
	}

	@Test
	public void shouldCreateAccountSuccessfully() {
		SaveAccount saveAccount = buildSaveAccount();

		when(ownerService.findByCpf(any())).thenReturn(Optional.empty());

		saveAccountService.createAccount(saveAccount);

		verify(ownerService).findByCpf(saveAccount.getOwner().getCpf());
		verify(saveAccountRepository).save(saveAccount);
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowCreationOfAccountWitNegativeBalance() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.valueOf(100));
		saveAccountService.createAccount(saveAccount);
	}


	@Test
	public void shouldCreateAccountLinkedToOwnerSuccessfully() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.getOwner().setCpf("12345678910");

		Owner ownerFromDb = new Owner();
		ownerFromDb.setName("Joao");
		ownerFromDb.setLastName("Silva");
		ownerFromDb.setCpf("12345678910");
		ownerFromDb.setBirthDate(LocalDate.now());

		when(ownerService.findByCpf(any())).thenReturn(Optional.of(ownerFromDb));
		when(saveAccountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		SaveAccount result = saveAccountService.createAccount(saveAccount);

		verify(ownerService).findByCpf(saveAccount.getOwner().getCpf());
		verify(saveAccountRepository).save(saveAccount);
		assertEquals(result.getOwner().getName(), ownerFromDb.getName());
		assertEquals(result.getOwner().getLastName(), ownerFromDb.getLastName());
		assertEquals(result.getOwner().getCpf(), ownerFromDb.getCpf());
	}

	@Test
	public void shouldGetAccountSuccessfullyWithIncome() {
		SaveAccount saveAccount = buildSaveAccount();
		BigDecimal initialBalance = BigDecimal.valueOf(10);
		saveAccount.setBalance(initialBalance);
		LocalDateTime lastTransaction = LocalDate.now().atStartOfDay();
		saveAccount.setLastTransaction(lastTransaction);

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(accountId, ownerId)).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		SaveAccount result = saveAccountService.getAccount(accountId, ownerId);

		BigDecimal expectedBalance = generateExpectedBalance(initialBalance, lastTransaction);
		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(saveAccountRepository).save(saveAccount);
		assertEquals(expectedBalance, result.getBalance());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowGetInvalidAccount() {
		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(accountId, ownerId)).thenReturn(Optional.empty());

		saveAccountService.getAccount(accountId, ownerId);
	}

	@Test
	public void shouldDeleteAccountSuccessfully() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.ZERO);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		saveAccountService.deleteAccount(accountId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(saveAccountRepository).save(saveAccount);
		verify(saveAccountRepository).deleteByIdAndOwnerId(accountId, ownerId);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDeleteAccountWithBalance() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.TEN);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		saveAccountService.deleteAccount(accountId, ownerId);
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowDeleteInvalidAccount() {
		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		saveAccountService.deleteAccount(accountId, ownerId);
	}

	@Test
	public void shouldAllowDepositSuccessfully() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.ZERO);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.deposit(BigDecimal.TEN, accountId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(BigDecimal.TEN.stripTrailingZeros(), result.stripTrailingZeros());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDepositNegativeAmount() {
		saveAccountService.deposit(BigDecimal.valueOf(-10), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowDepositInInvalidAccount() {
		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		saveAccountService.deposit(BigDecimal.TEN, accountId, ownerId);
	}

	@Test
	public void shouldAllowDepositSuccessfullyAndCalculateIncome() {
		SaveAccount saveAccount = buildSaveAccount();
		BigDecimal initialBalance = BigDecimal.TEN;
		saveAccount.setBalance(initialBalance);
		LocalDateTime lastTransaction = LocalDate.now().atStartOfDay();
		saveAccount.setLastTransaction(lastTransaction);
		BigDecimal depositAmount = BigDecimal.valueOf(100);

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.deposit(depositAmount, accountId, ownerId);

		BigDecimal expectedBalance = generateExpectedBalance(initialBalance, lastTransaction);
		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(expectedBalance.add(depositAmount), result);
	}

	@Test
	public void shouldWithdrawSuccessfully() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setLastTransaction(LocalDateTime.now());
		saveAccount.setBalance(BigDecimal.valueOf(1000));

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.withdraw(BigDecimal.valueOf(500), accountId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(BigDecimal.valueOf(490).stripTrailingZeros(), result.stripTrailingZeros());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowWithdrawNegativeAmount() {
		saveAccountService.withdraw(BigDecimal.valueOf(-500), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowWithdrawFromInvalidAccount() {
		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		saveAccountService.withdraw(BigDecimal.valueOf(500), accountId, ownerId);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowWithdrawMoreThanBalance() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setLastTransaction(LocalDateTime.now());
		saveAccount.setBalance(BigDecimal.valueOf(1000));

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		saveAccountService.withdraw(BigDecimal.valueOf(1000), accountId, ownerId);
	}

	@Test
	public void shouldTransferMoneySuccessfullyWithNoFee() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.valueOf(1000));
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(accountService.deposit(any(), any(), any())).thenReturn(BigDecimal.valueOf(500));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.transferForAccount(BigDecimal.valueOf(500), accountId, targetId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountService).deposit(BigDecimal.valueOf(500), targetId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(BigDecimal.valueOf(500).stripTrailingZeros(), result.stripTrailingZeros());
	}

	@Test
	public void shouldTransferMoneySuccessfullyWithInitialFee() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.valueOf(1000));
		saveAccount.setLastTransfer(LocalDate.now());
		saveAccount.setTransferCount(4L);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(accountService.deposit(any(), any(), any())).thenReturn(BigDecimal.valueOf(500));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.transferForAccount(BigDecimal.valueOf(500), accountId, targetId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountService).deposit(BigDecimal.valueOf(500), targetId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(BigDecimal.valueOf(490).stripTrailingZeros(), result.stripTrailingZeros());
	}

	@Test
	public void shouldTransferMoneySuccessfullyWithFinalFee() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.valueOf(1000));
		saveAccount.setLastTransfer(LocalDate.now());
		saveAccount.setTransferCount(5L);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(accountService.deposit(any(), any(), any())).thenReturn(BigDecimal.valueOf(500));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal result = saveAccountService.transferForAccount(BigDecimal.valueOf(500), accountId, targetId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountService).deposit(BigDecimal.valueOf(500), targetId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(BigDecimal.valueOf(475), result.stripTrailingZeros());
	}

	@Test
	public void shouldApplyIncomeBeforeTransfer() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setLastTransfer(LocalDate.now());
		saveAccount.setTransferCount(0L);

		BigDecimal initialBalance = BigDecimal.valueOf(500);
		saveAccount.setBalance(initialBalance);
		LocalDateTime lastTransaction = LocalDate.now().atStartOfDay();
		saveAccount.setLastTransaction(lastTransaction);
		BigDecimal expectedIncome = generateExpectedBalance(initialBalance, lastTransaction);

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));
		when(accountService.deposit(any(), any(), any())).thenReturn(BigDecimal.valueOf(500));
		when(saveAccountRepository.save(any())).thenAnswer(result -> result.getArguments()[0]);

		BigDecimal transferValue = BigDecimal.valueOf(500);
		BigDecimal result = saveAccountService.transferForAccount(transferValue, accountId, targetId, ownerId);

		verify(saveAccountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountService).deposit(transferValue, targetId, ownerId);
		verify(saveAccountRepository).save(saveAccount);

		assertEquals(expectedIncome.subtract(transferValue), result);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferNegativeMoney() {
		saveAccountService.transferForAccount(BigDecimal.valueOf(-500), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowFromInvalidAccount() {
		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		saveAccountService.transferForAccount(BigDecimal.valueOf(500), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferMoreThanCurrentBalance() {
		SaveAccount saveAccount = buildSaveAccount();
		saveAccount.setBalance(BigDecimal.valueOf(1000));
		saveAccount.setLastTransfer(LocalDate.now());
		saveAccount.setTransferCount(5L);
		saveAccount.setLastTransaction(LocalDateTime.now());

		UUID ownerId = UUID.randomUUID();
		UUID accountId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();

		when(saveAccountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(saveAccount));

		saveAccountService.transferForAccount(BigDecimal.valueOf(2000), accountId, targetId, ownerId);
	}

	@Test
	public void transferMethodShouldBeTransactional() throws NoSuchMethodException {
		Assert.assertNotNull(saveAccountService.getClass()
				.getMethod("transferForAccount", BigDecimal.class, UUID.class, UUID.class, UUID.class)
				.getAnnotation(Transactional.class));
	}

	private BigDecimal generateExpectedBalance(BigDecimal initialBalance, LocalDateTime lastTransaction) {
		return SAVING_BONUS
				.multiply(initialBalance)
				.multiply(BigDecimal.valueOf(Duration.between(lastTransaction, LocalDateTime.now()).toMinutes()))
				.add(initialBalance);
	}

	private SaveAccount buildSaveAccount() {
		SaveAccount saveAccount = new SaveAccount();
		saveAccount.setId(UUID.randomUUID());
		saveAccount.setBalance(BigDecimal.ZERO);
		saveAccount.setTransferCount(0L);

		Owner owner = new Owner();
		owner.setCpf("04215411050");
		owner.setName("Rodrigo");
		owner.setLastName("Tejada");
		owner.setBirthDate(LocalDate.now());

		saveAccount.setOwner(owner);

		return saveAccount;
	}
}
