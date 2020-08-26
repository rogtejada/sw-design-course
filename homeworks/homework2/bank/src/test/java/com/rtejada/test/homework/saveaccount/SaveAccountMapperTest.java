package com.rtejada.test.homework.saveaccount;

import com.rtejada.test.homework.owner.Owner;
import com.rtejada.test.homework.saveaccount.v1.SaveAccountMapper;
import com.rtejada.test.homework.saveaccount.v1.SaveAccountRequest;
import com.rtejada.test.homework.saveaccount.v1.SaveAccountResponse;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SaveAccountMapperTest {

	private SaveAccountMapper accountMapper;

	@Before
	public void setUp() {
		accountMapper = new SaveAccountMapper();
	}

	@Test
	public void shouldMapToResponseSuccessfully() {
		SaveAccount account = new SaveAccount();
		account.setBalance(BigDecimal.valueOf(100));
		account.setId(UUID.randomUUID());

		Owner owner = new Owner();
		owner.setName("Rodrigo");
		owner.setLastName("Tejada");
		owner.setBirthDate(LocalDate.now());
		owner.setCpf("04215411050");

		account.setOwner(owner);

		SaveAccountResponse result = accountMapper.toResponse(account);

		assertEquals(account.getBalance(), result.getBalance());
		assertEquals(account.getId(), result.getId());
		assertEquals(account.getOwner().getName(), result.getOwner().getName());
		assertEquals(account.getOwner().getLastName(), result.getOwner().getLastName());
		assertEquals(account.getOwner().getBirthDate(), result.getOwner().getBirthDate());
		assertEquals(account.getOwner().getCpf(), result.getOwner().getCpf());
	}

	@Test
	public void shouldMapToEntitySuccessfully() {
		SaveAccountRequest accountRequest = new SaveAccountRequest();
		accountRequest.setBirthDate(LocalDate.now());
		accountRequest.setCpf("04215411050");
		accountRequest.setName("Rodrigo");
		accountRequest.setLastName("Tejada");

		SaveAccount result = accountMapper.toEntity(accountRequest);

		assertEquals(accountRequest.getName(), result.getOwner().getName());
		assertEquals(accountRequest.getLastName(), result.getOwner().getLastName());
		assertEquals(accountRequest.getCpf(), result.getOwner().getCpf());
		assertEquals(accountRequest.getBirthDate(), result.getOwner().getBirthDate());
		assertEquals(BigDecimal.ZERO, result.getBalance());
		assertEquals(0L, result.getTransferCount(), 0);
	}
}
