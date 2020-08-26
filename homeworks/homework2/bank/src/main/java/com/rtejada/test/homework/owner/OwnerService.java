package com.rtejada.test.homework.owner;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnerService {

	private final OwnerRepository ownerRepository;

	public OwnerService(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	public Optional<Owner> findByCpf(String cpf) {
		return ownerRepository.findByCpf(cpf);
	}
}

