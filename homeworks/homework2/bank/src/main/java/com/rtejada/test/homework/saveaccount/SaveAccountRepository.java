package com.rtejada.test.homework.saveaccount;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SaveAccountRepository extends CrudRepository<SaveAccount, UUID> {

	Optional<SaveAccount> findByIdAndOwnerId(UUID id, UUID ownerId);

	void deleteByIdAndOwnerId(UUID id, UUID ownerId);
}
