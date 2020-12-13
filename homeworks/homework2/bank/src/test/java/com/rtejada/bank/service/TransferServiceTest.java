package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.model.Owner;
import com.rtejada.bank.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransferServiceTest {


	private SaveAccountService saveAccountServiceMock;
	private CreditAccountService creditAccountServiceMock;
	private TransferService transferService;

	@BeforeEach
	public void setUp() {
		saveAccountServiceMock = mock(SaveAccountService.class);
		creditAccountServiceMock = mock(CreditAccountService.class);
		transferService = new TransferService(creditAccountServiceMock, saveAccountServiceMock);
	}

	@Test
	public void shouldTransferSuccessfullyFromCreditToCredit() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.CREDIT);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.CREDIT);
		transfer.setAmount(BigDecimal.valueOf(100));

		ArgumentCaptor<BigDecimal> withdrawAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> sourceAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> withdrawTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.withdrawForTransfer(withdrawAmountCaptor.capture(), sourceAccountIdCaptor.capture(), withdrawTransferTimeCaptor.capture())).thenReturn(BigDecimal.TEN);

		ArgumentCaptor<BigDecimal> depositAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> targetAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> depositTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.depositForTransfer(depositAmountCaptor.capture(), targetAccountIdCaptor.capture(), depositTransferTimeCaptor.capture())).thenReturn(BigDecimal.valueOf(100));

		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		final BigDecimal withdrawAmount = withdrawAmountCaptor.getValue();
		final UUID sourceAccountId = sourceAccountIdCaptor.getValue();
		final LocalDateTime withdrawTransferTime = withdrawTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.05)), withdrawAmount);
		assertEquals(transfer.getSourceId(), sourceAccountId);

		final BigDecimal depositAmount = depositAmountCaptor.getValue();
		final UUID targetAccountId = targetAccountIdCaptor.getValue();
		final LocalDateTime depositTransferTime = depositTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), depositAmount);
		assertEquals(transfer.getTargetId(), targetAccountId);

		assertEquals(withdrawTransferTime, depositTransferTime);

		verify(creditAccountServiceMock, times(1)).withdrawForTransfer(withdrawAmount, sourceAccountId, withdrawTransferTime);
		verify(creditAccountServiceMock, times(1)).depositForTransfer(depositAmount, targetAccountId, depositTransferTime);
	}

	@Test
	public void shouldTransferSuccessfullyFromCreditToSaving() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.CREDIT);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.SAVING);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);

		Account targetAccount = new Account();
		targetAccount.setOwner(owner);

		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		ArgumentCaptor<BigDecimal> withdrawAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> sourceAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> withdrawTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.withdrawForTransfer(withdrawAmountCaptor.capture(), sourceAccountIdCaptor.capture(), withdrawTransferTimeCaptor.capture())).thenReturn(BigDecimal.TEN);

		ArgumentCaptor<BigDecimal> depositAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> targetAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> depositTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(saveAccountServiceMock.depositForTransfer(depositAmountCaptor.capture(), targetAccountIdCaptor.capture(), depositTransferTimeCaptor.capture())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		final BigDecimal withdrawAmount = withdrawAmountCaptor.getValue();
		final UUID sourceAccountId = sourceAccountIdCaptor.getValue();
		final LocalDateTime withdrawTransferTime = withdrawTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), withdrawAmount);
		assertEquals(transfer.getSourceId(), sourceAccountId);

		final BigDecimal depositAmount = depositAmountCaptor.getValue();
		final UUID targetAccountId = targetAccountIdCaptor.getValue();
		final LocalDateTime depositTransferTime = depositTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), depositAmount);
		assertEquals(transfer.getTargetId(), targetAccountId);

		assertEquals(withdrawTransferTime, depositTransferTime);

		verify(creditAccountServiceMock, times(1)).withdrawForTransfer(withdrawAmount, sourceAccountId, withdrawTransferTime);
		verify(saveAccountServiceMock, times(1)).depositForTransfer(depositAmount, targetAccountId, depositTransferTime);
	}

	@Test
	public void shouldTransferSuccessfullyFromSavingToCreditNoFee() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.SAVING);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.CREDIT);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);

		Account targetAccount = new Account();
		targetAccount.setOwner(owner);

		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		ArgumentCaptor<BigDecimal> withdrawAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> sourceAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> withdrawTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(saveAccountServiceMock.withdrawForTransfer(withdrawAmountCaptor.capture(), sourceAccountIdCaptor.capture(), withdrawTransferTimeCaptor.capture())).thenReturn(BigDecimal.TEN);

		ArgumentCaptor<BigDecimal> depositAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> targetAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> depositTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.depositForTransfer(depositAmountCaptor.capture(), targetAccountIdCaptor.capture(), depositTransferTimeCaptor.capture())).thenReturn(BigDecimal.valueOf(100));

		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		final BigDecimal withdrawAmount = withdrawAmountCaptor.getValue();
		final UUID sourceAccountId = sourceAccountIdCaptor.getValue();
		final LocalDateTime withdrawTransferTime = withdrawTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), withdrawAmount);
		assertEquals(transfer.getSourceId(), sourceAccountId);

		final BigDecimal depositAmount = depositAmountCaptor.getValue();
		final UUID targetAccountId = targetAccountIdCaptor.getValue();
		final LocalDateTime depositTransferTime = depositTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), depositAmount);
		assertEquals(transfer.getTargetId(), targetAccountId);

		assertEquals(withdrawTransferTime, depositTransferTime);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(withdrawAmount, sourceAccountId, withdrawTransferTime);
		verify(creditAccountServiceMock, times(1)).depositForTransfer(depositAmount, targetAccountId, depositTransferTime);
	}

	@Test
	public void shouldTransferSuccessfullyFromSavingToCreditInitialFee() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.SAVING);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.CREDIT);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);
		sourceAccount.setTransferCount(3L);
		sourceAccount.setLastTransfer(LocalDate.now());

		Account targetAccount = new Account();
		targetAccount.setOwner(owner);

		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		ArgumentCaptor<BigDecimal> withdrawAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> sourceAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> withdrawTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(saveAccountServiceMock.withdrawForTransfer(withdrawAmountCaptor.capture(), sourceAccountIdCaptor.capture(), withdrawTransferTimeCaptor.capture())).thenReturn(BigDecimal.TEN);

		ArgumentCaptor<BigDecimal> depositAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> targetAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> depositTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.depositForTransfer(depositAmountCaptor.capture(), targetAccountIdCaptor.capture(), depositTransferTimeCaptor.capture())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		final BigDecimal withdrawAmount = withdrawAmountCaptor.getValue();
		final UUID sourceAccountId = sourceAccountIdCaptor.getValue();
		final LocalDateTime withdrawTransferTime = withdrawTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.02)), withdrawAmount);
		assertEquals(transfer.getSourceId(), sourceAccountId);

		final BigDecimal depositAmount = depositAmountCaptor.getValue();
		final UUID targetAccountId = targetAccountIdCaptor.getValue();
		final LocalDateTime depositTransferTime = depositTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), depositAmount);
		assertEquals(transfer.getTargetId(), targetAccountId);

		assertEquals(withdrawTransferTime, depositTransferTime);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(withdrawAmount, sourceAccountId, withdrawTransferTime);
		verify(creditAccountServiceMock, times(1)).depositForTransfer(depositAmount, targetAccountId, depositTransferTime);
	}

	@Test
	public void shouldTransferSuccessfullyFromSavingToCreditFinalFee() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.SAVING);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.CREDIT);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);
		sourceAccount.setTransferCount(4L);
		sourceAccount.setLastTransfer(LocalDate.now());

		Account targetAccount = new Account();
		targetAccount.setOwner(owner);

		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		ArgumentCaptor<BigDecimal> withdrawAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> sourceAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> withdrawTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(saveAccountServiceMock.withdrawForTransfer(withdrawAmountCaptor.capture(), sourceAccountIdCaptor.capture(), withdrawTransferTimeCaptor.capture())).thenReturn(BigDecimal.TEN);

		ArgumentCaptor<BigDecimal> depositAmountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
		ArgumentCaptor<UUID> targetAccountIdCaptor = ArgumentCaptor.forClass(UUID.class);
		ArgumentCaptor<LocalDateTime> depositTransferTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		when(creditAccountServiceMock.depositForTransfer(depositAmountCaptor.capture(), targetAccountIdCaptor.capture(), depositTransferTimeCaptor.capture())).thenReturn(BigDecimal.valueOf(100));



		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		final BigDecimal withdrawAmount = withdrawAmountCaptor.getValue();
		final UUID sourceAccountId = sourceAccountIdCaptor.getValue();
		final LocalDateTime withdrawTransferTime = withdrawTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.05)), withdrawAmount);
		assertEquals(transfer.getSourceId(), sourceAccountId);

		final BigDecimal depositAmount = depositAmountCaptor.getValue();
		final UUID targetAccountId = targetAccountIdCaptor.getValue();
		final LocalDateTime depositTransferTime = depositTransferTimeCaptor.getValue();

		assertEquals(BigDecimal.valueOf(100), depositAmount);
		assertEquals(transfer.getTargetId(), targetAccountId);

		assertEquals(withdrawTransferTime, depositTransferTime);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(withdrawAmount, sourceAccountId, withdrawTransferTime);
		verify(creditAccountServiceMock, times(1)).depositForTransfer(depositAmount, targetAccountId, depositTransferTime);
	}


	@Test
	public void shouldNotAllowTransferFromSavingAccountFromDifferentOwners() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.SAVING);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.CREDIT);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);
		Account targetAccount = new Account();
		Owner targetOwner = new Owner();
		owner.setCpf("0023113123");
		owner.setName("John");
		targetAccount.setOwner(targetOwner);

		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		assertThrows(
				InvalidTransactionException.class,
				() -> transferService.transfer(transfer));
	}


	@Test
	public void shouldNotAllowTransferToSavingAccountFromDifferentOwners() {
		Transfer transfer = new Transfer();
		transfer.setSourceId(UUID.randomUUID());
		transfer.setSourceType(AccountType.CREDIT);
		transfer.setTargetId(UUID.randomUUID());
		transfer.setTargetType(AccountType.SAVING);
		transfer.setAmount(BigDecimal.valueOf(100));

		Owner owner = new Owner();
		owner.setCpf("12343201232");
		owner.setName("John");

		Account sourceAccount = new Account();
		sourceAccount.setOwner(owner);

		Account targetAccount = new Account();
		Owner targetOwner = new Owner();
		owner.setCpf("0023113123");
		owner.setName("John");
		targetAccount.setOwner(targetOwner);

		when(creditAccountServiceMock.getAccount(any())).thenReturn(Optional.of(sourceAccount));
		when(saveAccountServiceMock.getAccount(any())).thenReturn(Optional.of(targetAccount));

		assertThrows(
				InvalidTransactionException.class,
				() -> transferService.transfer(transfer));
	}
}
