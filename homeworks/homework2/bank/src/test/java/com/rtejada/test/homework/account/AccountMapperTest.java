package com.rtejada.test.homework.account;

import com.rtejada.test.homework.account.v1.AccountMapper;
import com.rtejada.test.homework.account.v1.AccountRequest;
import com.rtejada.test.homework.account.v1.AccountResponse;
import com.rtejada.test.homework.owner.Owner;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AccountMapperTest {

	private AccountMapper accountMapper;


	@Before
	public void setUp() {
		accountMapper = new AccountMapper();
	}

	@Test
	public void shouldMapToResponseSuccessfully() {
		Account account = new Account();
		account.setBalance(BigDecimal.valueOf(100));
		account.setId(UUID.randomUUID());

		Owner owner = new Owner();
		owner.setName("Rodrigo");
		owner.setLastName("Tejada");
		owner.setBirthDate(LocalDate.now());
		owner.setCpf("04215411050");

		account.setOwner(owner);

		AccountResponse result = accountMapper.toResponse(account);

		assertEquals(account.getBalance(), result.getBalance());
		assertEquals(account.getId(), result.getId());
		assertEquals(account.getOwner().getName(), result.getOwner().getName());
		assertEquals(account.getOwner().getLastName(), result.getOwner().getLastName());
		assertEquals(account.getOwner().getBirthDate(), result.getOwner().getBirthDate());
		assertEquals(account.getOwner().getCpf(), result.getOwner().getCpf());
	}

	@Test
	public void shouldMapToEntitySuccessfully() {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setBirthDate(LocalDate.now());
		accountRequest.setCpf("04215411050");
		accountRequest.setName("Rodrigo");
		accountRequest.setLastName("Tejada");

		Account result = accountMapper.toEntity(accountRequest);

		assertEquals(accountRequest.getName(), result.getOwner().getName());
		assertEquals(accountRequest.getLastName(), result.getOwner().getLastName());
		assertEquals(accountRequest.getCpf(), result.getOwner().getCpf());
		assertEquals(accountRequest.getBirthDate(), result.getOwner().getBirthDate());
		assertEquals(BigDecimal.ZERO, result.getBalance());
		assertEquals(0L, result.getWithdrawCount(), 0);
	}
}
