package com.rtejada.test.homework.owner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OwnerServiceTest {

	private OwnerService ownerService;
	private OwnerRepository ownerRepository;

	@Before
	public void setUp() {
		ownerRepository = Mockito.mock(OwnerRepository.class);
		ownerService = new OwnerService(ownerRepository);
	}

	@Test
	public void shouldFindOwnerSuccessfully() {
		Owner owner = new Owner();
		owner.setName("Rodrigo");
		owner.setLastName("Tejada");
		owner.setCpf("03215411050");
		owner.setBirthDate(LocalDate.now());

		when(ownerRepository.findByCpf(any())).thenReturn(Optional.of(owner));

		Optional<Owner> optionalResult = ownerService.findByCpf(owner.getCpf());

		verify(ownerRepository).findByCpf(owner.getCpf());
		assertTrue(optionalResult.isPresent());

		Owner result = optionalResult.get();
		assertEquals(owner.getName(), result.getName());
		assertEquals(owner.getLastName(), result.getLastName());
		assertEquals(owner.getCpf(), result.getCpf());
		assertEquals(owner.getBirthDate(), result.getBirthDate());
	}

	@Test
	public void shouldReturnEmptyOwnerSuccessfully() {
		when(ownerRepository.findByCpf(any())).thenReturn(Optional.empty());

		String cpf = "04215411050";
		Optional<Owner> optionalResult = ownerService.findByCpf(cpf);

		verify(ownerRepository).findByCpf(cpf);
		assertFalse(optionalResult.isPresent());
	}
}
