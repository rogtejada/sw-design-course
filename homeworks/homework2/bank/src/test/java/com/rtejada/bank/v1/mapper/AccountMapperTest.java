package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.*;
import com.rtejada.bank.v1.dto.AccountResponse;
import com.rtejada.bank.v1.dto.StatementResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountMapperTest {

	private AccountMapper accountMapper;

	@BeforeEach
	public void setUp() {
		accountMapper = new AccountMapper();
	}

	@Test
	public void shouldMapToResponse() {
		Account account = new Account();
		account.setId(UUID.randomUUID());
		account.setAccountType(AccountType.CREDIT);

		Owner owner = new Owner();
		owner.setCpf("12345602312");
		owner.setName("test");

		account.setOwner(owner);

		final AccountResponse result = accountMapper.toResponse(account);

		assertEquals(account.getId(), result.getId());
		assertEquals(account.getAccountType(), result.getAccountType());
		assertEquals(account.getOwner().getName(), result.getOwner().getName());
		assertEquals(account.getOwner().getCpf(), result.getOwner().getCpf());
	}

	@Test
	public void shouldMapNullAccount() {
		assertNull(accountMapper.toResponse((Account) null));
	}

	@Test
	public void shouldMapAccountWithoutOwner() {
		Account account = new Account();
		account.setId(UUID.randomUUID());
		account.setAccountType(AccountType.CREDIT);

		account.setOwner(null);

		final AccountResponse result = accountMapper.toResponse(account);

		assertEquals(account.getId(), result.getId());
		assertEquals(account.getAccountType(), result.getAccountType());
		assertNull(result.getOwner());
	}

	@Test
	public void shouldMapToStatementResponse() {
		Statement statement = new Statement(LocalDateTime.of(2000, Month.APRIL, 20, 10, 10), BigDecimal.TEN, Transaction.DEPOSIT);

		final StatementResponse result = accountMapper.toResponse(statement);

		assertEquals(statement.getDate(), result.getDate());
		assertEquals(statement.getValue(), result.getValue());
		assertEquals(statement.getTransaction(), result.getTransaction());
	}

	@Test
	public void shouldNotAllowMapNullStatement() {
		assertThrows(
				IllegalArgumentException.class,
				() ->  accountMapper.toResponse((Statement) null));
	}
}
