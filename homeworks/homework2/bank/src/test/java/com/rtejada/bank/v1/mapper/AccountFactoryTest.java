package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.v1.dto.AccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountFactoryTest {

	private AccountFactory accountFactory;

	@BeforeEach
	public void setUp() {
		accountFactory = new AccountFactory();
	}

	@Test
	public void shouldBuildCreditAccount() {
		AccountRequest request = new AccountRequest();
		request.setCpf("04215411050");
		request.setName("john");

		final Account account = accountFactory.toAccountEntity(request, AccountType.CREDIT);

		assertEquals(account.getOwner().getCpf(), request.getCpf());
		assertEquals(account.getOwner().getName(), request.getName());
		assertEquals(AccountType.CREDIT, account.getAccountType());
	}

	@Test
	public void shouldBuildSavingAccount() {
		AccountRequest request = new AccountRequest();
		request.setCpf("04215411050");
		request.setName("john");

		final Account account = accountFactory.toAccountEntity(request, AccountType.SAVING);

		assertEquals(account.getOwner().getCpf(), request.getCpf());
		assertEquals(account.getOwner().getName(), request.getName());
		assertEquals(AccountType.SAVING, account.getAccountType());
	}

	@Test
	public void shouldNotAllowCreateNullAccount() {
		assertThrows(
				IllegalArgumentException.class,
				() -> accountFactory.toAccountEntity(null, AccountType.CREDIT));
	}

	@Test
	public void shouldNotAllowCreateAccountWithoutType() {
		AccountRequest request = new AccountRequest();
		request.setCpf("04215411050");
		request.setName("john");

		assertThrows(
				IllegalArgumentException.class,
				() -> accountFactory.toAccountEntity(request, null));
	}
}
