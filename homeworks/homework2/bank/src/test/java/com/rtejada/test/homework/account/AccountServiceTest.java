package com.rtejada.test.homework.account;

import com.rtejada.test.homework.InvalidAccountException;
import com.rtejada.test.homework.InvalidTransactionException;
import com.rtejada.test.homework.saveaccount.SaveAccountService;
import com.rtejada.test.homework.owner.Owner;
import com.rtejada.test.homework.owner.OwnerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

	private AccountRepository accountRepository;
	private AccountService accountService;
	private OwnerService ownerService;
	private SaveAccountService saveAccountService;

	@Before
	public void setUp() {
		accountRepository = Mockito.mock(AccountRepository.class);
		ownerService = Mockito.mock(OwnerService.class);
		saveAccountService = Mockito.mock(SaveAccountService.class);
		accountService = new AccountService(accountRepository, saveAccountService, ownerService);
	}

	@Test
	public void shouldCreteAccount() {
		Account account = buildAccount();

		when(accountRepository.save(any())).thenAnswer((result) ->  result.getArguments()[0]);

		Account savedAccount = accountService.createAccount(account);

		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.ZERO, savedAccount.getBalance());
		Assert.assertEquals(account.getOwner().getName(), savedAccount.getOwner().getName());
		Assert.assertEquals(account.getOwner().getLastName(), savedAccount.getOwner().getLastName());
		Assert.assertEquals(account.getOwner().getBirthDate(), savedAccount.getOwner().getBirthDate());
		Assert.assertEquals(account.getOwner().getCpf(), savedAccount.getOwner().getCpf());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowAccountCreationWithPositiveBalance() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(10));

		accountService.createAccount(account);
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowAccountCreationWithNegativeBalance() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(-10));

		accountService.createAccount(account);
	}

	@Test
	public void shouldReturnAccount() {
		Account account = buildAccount();
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));

		Optional<Account> result = accountService.getAccount(accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(account, result.get());
	}

	@Test
	public void shouldReturnEmptyAccount() {
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		Optional<Account> account = accountService.getAccount(accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		Assert.assertFalse(account.isPresent());
	}

	@Test
	public void shouldDeleteAccount() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.ZERO);

		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		doNothing().when(accountRepository).deleteById(any());
		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));

		accountService.deleteAccount(accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).deleteById(accountId);
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowDeleteNonExistentAccount() {
		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		accountService.deleteAccount(UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDeleteAccountWithPositiveBalance() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(100));

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));

		accountService.deleteAccount(UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDeleteAccountWithNegativeBalance() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(-100));

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));

		accountService.deleteAccount(UUID.randomUUID(), UUID.randomUUID());
	}

	@Test
	public void shouldDepositSuccessfully(){
		Account account = buildAccount();
		account.setBalance(BigDecimal.ZERO);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.deposit(BigDecimal.valueOf(1000), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(1000), result);
	}

	@Test
	public void shouldDepositSuccessfullyWhenInitialBalanceIsNegative(){
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(-500));
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.deposit(BigDecimal.valueOf(1000), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(500), result);
	}

	@Test
	public void shouldDepositSuccessfullyWhenFinalBalanceIsNegative(){
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(-500));
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.deposit(BigDecimal.valueOf(200), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(-300), result);
	}

	@Test
	public void shouldAllowDepositOfHugeAmount(){
		Account account = buildAccount();
		account.setBalance(BigDecimal.ZERO);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		BigDecimal expectedValue = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.deposit(expectedValue, accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(expectedValue, result);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDepositNegativeAmounts() {
		accountService.deposit(BigDecimal.valueOf(-100), UUID.randomUUID(), UUID.randomUUID());
	}


	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowDepositEmptyAmounts() {
		accountService.deposit(BigDecimal.ZERO, UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowDepositInInvalidAccount() {
		when(accountRepository.findById(any())).thenReturn(Optional.empty());

		accountService.deposit(BigDecimal.valueOf(200), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test
	public void shouldWithdrawMoneySuccessfully() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(700), result);
	}

	@Test
	public void shouldWithdrawAllMoneySuccessfully() {
		Account account = buildAccount();
		account.setWithdrawCount(1L);
		Long initialCount = 1L;
		account.setBalance(BigDecimal.valueOf(1000));
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(1000), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.ZERO, result);
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(initialCount + 1, account.getWithdrawCount(), 0);
	}

	@Test
	public void shouldWithdrawHugeAmountSuccessfully() {
		Account account = buildAccount();
		Long initialCount = account.getWithdrawCount();
		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));
		account.setBalance(hugeAmount);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(hugeAmount, accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.ZERO, result);
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(initialCount + 1, account.getWithdrawCount(), 0);
	}

	@Test
	public void shouldWithdrawMoneySuccessfullyWithoutFee() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		account.setLastWithdraw(LocalDate.now());
		account.setWithdrawCount(3L);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(700), result);
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(4L, account.getWithdrawCount(), 0);
	}

	@Test
	public void shouldWithdrawMoneySuccessfullyWithoutFeeEvenWhenCountIsHigh() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		account.setLastWithdraw(LocalDate.of(1903, 10, 10));
		account.setWithdrawCount(3L);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(700), result);
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(1, account.getWithdrawCount(), 0);
	}


	@Test
	public void shouldWithdrawMoneySuccessfullyWithoutFeeWhenNullWithdrawDate() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		account.setLastWithdraw(null);
		account.setWithdrawCount(3L);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(700), result);
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(1, account.getWithdrawCount(), 0);
	}

	@Test
	public void shouldWithdrawMoneySuccessfullyWithFinalFee() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		account.setLastWithdraw(LocalDate.now());
		account.setWithdrawCount(5L);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(685), result.stripTrailingZeros());
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(6, account.getWithdrawCount(), 0);
	}

	@Test
	public void shouldWithdrawMoneySuccessfullyWithFinalFeeWhenCountIsHigher() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		account.setLastWithdraw(LocalDate.now());
		account.setWithdrawCount(10L);
		UUID accountId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.withdraw(BigDecimal.valueOf(300), accountId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(accountId, ownerId);
		verify(accountRepository).save(account);
		Assert.assertEquals(BigDecimal.valueOf(685), result.stripTrailingZeros());
		Assert.assertTrue(account.getLastWithdraw().atStartOfDay().isEqual(LocalDate.now().atStartOfDay()));
		Assert.assertEquals(11L, account.getWithdrawCount(), 0);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowWithdrawNegativeAmount() {
		accountService.withdraw(BigDecimal.valueOf(-1000), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowWithdrawEmptyAmount() {
		accountService.withdraw(BigDecimal.ZERO, UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowWithdrawFromInvalidAccount() {
		when(accountRepository.findById(any())).thenReturn(Optional.empty());

		accountService.withdraw(BigDecimal.valueOf(1000), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowWithdrawMoreThanCurrentBalance() {
		Account account = buildAccount();
		account.setBalance(BigDecimal.valueOf(1000));
		UUID accountId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.of(account));

		accountService.withdraw(BigDecimal.valueOf(2000), accountId, UUID.randomUUID());
	}

	@Test
	public void shouldTransferSuccessfully() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(1000));

		Account targetAcc = buildAccount();
		targetAcc.setBalance(BigDecimal.valueOf(1000));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(accountRepository.findById(targetId)).thenReturn(Optional.of(targetAcc));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.transfer(BigDecimal.valueOf(500), sourceId, targetId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(sourceId, ownerId);
		verify(accountRepository).findById(targetId);
		verify(accountRepository).save(sourceAcc);
		verify(accountRepository).save(targetAcc);
		Assert.assertEquals(BigDecimal.valueOf(475), result.stripTrailingZeros());
		Assert.assertEquals(BigDecimal.valueOf(1500), targetAcc.getBalance());
	}


	@Test
	public void shouldAllowTransferAllMoney() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(1050));

		Account targetAcc = buildAccount();
		targetAcc.setBalance(BigDecimal.valueOf(1000));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();


		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(accountRepository.findById(targetId)).thenReturn(Optional.of(targetAcc));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.transfer(BigDecimal.valueOf(1000), sourceId, targetId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(sourceId, ownerId);
		verify(accountRepository).findById(targetId);
		verify(accountRepository).save(sourceAcc);
		verify(accountRepository).save(targetAcc);
		Assert.assertEquals(BigDecimal.ZERO, result.stripTrailingZeros());
		Assert.assertEquals(BigDecimal.valueOf(2000), targetAcc.getBalance());
	}

	@Test
	public void shouldAllowTransferHugeAmount() {
		Account sourceAcc = buildAccount();
		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));
		sourceAcc.setBalance(hugeAmount.add(hugeAmount));
		BigDecimal remainingAfterFee = hugeAmount.multiply(BigDecimal.valueOf(0.95)).stripTrailingZeros();

		Account targetAcc = buildAccount();
		targetAcc.setBalance(BigDecimal.ZERO);

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(accountRepository.findById(targetId)).thenReturn(Optional.of(targetAcc));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.transfer(hugeAmount, sourceId, targetId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(sourceId, ownerId);
		verify(accountRepository).findById(targetId);
		verify(accountRepository).save(sourceAcc);
		verify(accountRepository).save(targetAcc);
		Assert.assertEquals(remainingAfterFee, result.stripTrailingZeros());
		Assert.assertEquals(hugeAmount, targetAcc.getBalance());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferNegativeAmount() {
		accountService.transfer(BigDecimal.valueOf(-500), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferEmptyAmount() {
		accountService.transfer(BigDecimal.ZERO, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowTransferFromInvalidAccount() {
		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.empty());
		accountService.transfer(BigDecimal.valueOf(500), sourceId, targetId, ownerId);
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowTransferToInvalidAccount() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(1000));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(accountRepository.findById(targetId)).thenReturn(Optional.empty());
		accountService.transfer(BigDecimal.valueOf(500), sourceId, targetId, ownerId);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferMoreThanSourceAccountBalance() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(500));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		accountService.transfer(BigDecimal.valueOf(1000), sourceId, targetId, ownerId);
	}


	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferWhenThereIsNoMoneyToPayFee() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(500));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		accountService.transfer(BigDecimal.valueOf(500), sourceId, targetId, ownerId);
	}

	@Test
	public void transferMethodShouldBeTransactional() throws NoSuchMethodException {
		Assert.assertNotNull(accountService.getClass()
				.getMethod("transfer", BigDecimal.class, UUID.class, UUID.class, UUID.class)
				.getAnnotation(Transactional.class));
	}

	@Test
	public void shouldTransferToSavingsSuccessfully() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(500));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		BigDecimal transactionAmount = new BigDecimal(100);

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(saveAccountService.deposit(any(), any(), any())).thenReturn(new BigDecimal(100));
		when(accountRepository.save(any())).thenAnswer((result) ->  result.getArguments()[0]);

		BigDecimal result = accountService.transferToSaving(BigDecimal.valueOf(100), sourceId, targetId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(sourceId, ownerId);
		verify(accountRepository).save(sourceAcc);
		verify(saveAccountService).deposit(transactionAmount, targetId, ownerId);
		Assert.assertEquals(BigDecimal.valueOf(400), result);
	}

	@Test
	public void shouldTransferToSavingsHugeAmountSuccessfully() {
		Account sourceAcc = buildAccount();
		BigDecimal hugeAmount = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));
		sourceAcc.setBalance(hugeAmount);

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));
		when(saveAccountService.deposit(any(), any(), any())).thenReturn(new BigDecimal(100));
		when(accountRepository.save(any())).thenAnswer((result) -> result.getArguments()[0]);

		BigDecimal result = accountService.transferToSaving(hugeAmount, sourceId, targetId, ownerId);

		verify(accountRepository).findByIdAndOwnerId(sourceId, ownerId);
		verify(accountRepository).save(sourceAcc);
		verify(saveAccountService).deposit(hugeAmount, targetId, ownerId);
		Assert.assertEquals(BigDecimal.ZERO, result);
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferToSavingsNegativeValue() {
		accountService.transferToSaving(BigDecimal.valueOf(-100), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidAccountException.class)
	public void shouldNotAllowTransferToSavingsForInvalidAccount() {
		when(accountRepository.findByIdAndOwnerId(any(), any())).thenReturn(Optional.empty());

		accountService.transferToSaving(BigDecimal.valueOf(100), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
	}

	@Test(expected = InvalidTransactionException.class)
	public void shouldNotAllowTransferToSavingsMoreThanCurrentBalance() {
		Account sourceAcc = buildAccount();
		sourceAcc.setBalance(BigDecimal.valueOf(200));

		UUID sourceId = UUID.randomUUID();
		UUID targetId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		when(accountRepository.findByIdAndOwnerId(sourceId, ownerId)).thenReturn(Optional.of(sourceAcc));

		accountService.transferToSaving( new BigDecimal(400), sourceId, targetId, ownerId);
	}

	@Test
	public void transferToSavingMethodShouldBeTransactional() throws NoSuchMethodException {
		Assert.assertNotNull(accountService.getClass()
				.getMethod("transferToSaving", BigDecimal.class, UUID.class, UUID.class, UUID.class)
				.getAnnotation(Transactional.class));
	}

	private Account buildAccount() {
		Account account = new Account();
		account.setBalance(BigDecimal.ZERO);
		account.setLastWithdraw(LocalDate.now());
		account.setWithdrawCount(1L);

		Owner owner = new Owner();
		owner.setName("rodrigo");
		owner.setLastName("tejada");
		owner.setBirthDate(LocalDate.now());
		owner.setCpf("12345678910");

		account.setOwner(owner);

		return account;
	}
}
