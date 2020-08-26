package com.rtejada.test.homework.owner;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends CrudRepository<Owner, UUID> {


	Optional<Owner> findByCpf(String cpf);
}
