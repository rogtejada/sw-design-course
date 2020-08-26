package com.rtejada.test.homework.account;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends CrudRepository<Account, UUID> {

	Optional<Account> findByIdAndOwnerId(UUID id, UUID ownerId);
}
