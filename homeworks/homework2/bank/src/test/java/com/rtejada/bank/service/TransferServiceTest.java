package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.model.Owner;
import com.rtejada.bank.model.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
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

		when(creditAccountServiceMock.withdrawForTransfer(any(), any())).thenReturn(BigDecimal.TEN);
		when(creditAccountServiceMock.depositForTransfer(any(), any())).thenReturn(BigDecimal.valueOf(100));

		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		verify(creditAccountServiceMock, times(1)).withdrawForTransfer(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.05)), transfer.getSourceId());
		verify(creditAccountServiceMock, times(1)).depositForTransfer(BigDecimal.valueOf(100), transfer.getTargetId());
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

		when(creditAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(saveAccountServiceMock.getAccount(any())).thenReturn(targetAccount);
		when(creditAccountServiceMock.withdrawForTransfer(any(), any())).thenReturn(BigDecimal.TEN);
		when(saveAccountServiceMock.depositForTransfer(any(), any())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		verify(creditAccountServiceMock, times(1)).withdrawForTransfer(BigDecimal.valueOf(100), transfer.getSourceId());
		verify(saveAccountServiceMock, times(1)).depositForTransfer(BigDecimal.valueOf(100), transfer.getTargetId());
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

		when(saveAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(creditAccountServiceMock.getAccount(any())).thenReturn(targetAccount);
		when(saveAccountServiceMock.withdrawForTransfer(any(), any())).thenReturn(BigDecimal.TEN);
		when(creditAccountServiceMock.depositForTransfer(any(), any())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(BigDecimal.valueOf(100), transfer.getSourceId());
		verify(creditAccountServiceMock, times(1)).depositForTransfer(BigDecimal.valueOf(100), transfer.getTargetId());
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

		when(saveAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(creditAccountServiceMock.getAccount(any())).thenReturn(targetAccount);
		when(saveAccountServiceMock.withdrawForTransfer(any(), any())).thenReturn(BigDecimal.TEN);
		when(creditAccountServiceMock.depositForTransfer(any(), any())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.02)), transfer.getSourceId());
		verify(creditAccountServiceMock, times(1)).depositForTransfer(BigDecimal.valueOf(100), transfer.getTargetId());
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

		when(saveAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(creditAccountServiceMock.getAccount(any())).thenReturn(targetAccount);
		when(saveAccountServiceMock.withdrawForTransfer(any(), any())).thenReturn(BigDecimal.TEN);
		when(creditAccountServiceMock.depositForTransfer(any(), any())).thenReturn(BigDecimal.valueOf(100));


		final BigDecimal result = transferService.transfer(transfer);

		assertEquals(BigDecimal.TEN, result);

		verify(saveAccountServiceMock, times(1)).withdrawForTransfer(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(1.05)), transfer.getSourceId());
		verify(creditAccountServiceMock, times(1)).depositForTransfer(BigDecimal.valueOf(100), transfer.getTargetId());
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

		when(saveAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(creditAccountServiceMock.getAccount(any())).thenReturn(targetAccount);

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

		when(creditAccountServiceMock.getAccount(any())).thenReturn(sourceAccount);
		when(saveAccountServiceMock.getAccount(any())).thenReturn(targetAccount);

		assertThrows(
				InvalidTransactionException.class,
				() -> transferService.transfer(transfer));
	}
}
