package com.rtejada.test.homework.saveaccount.v1;

import com.rtejada.test.homework.owner.v1.OwnerResponse;
import com.rtejada.test.homework.saveaccount.SaveAccount;
import com.rtejada.test.homework.owner.Owner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class SaveAccountMapper {

	public SaveAccount toEntity(SaveAccountRequest accountRequest) {
		SaveAccount account = new SaveAccount();
		account.setBalance(BigDecimal.ZERO);
		account.setTransferCount(0L);
		account.setLastTransaction(LocalDateTime.now());

		Owner owner = new Owner();
		owner.setCpf(accountRequest.getCpf());
		owner.setBirthDate(accountRequest.getBirthDate());
		owner.setLastName(accountRequest.getLastName());
		owner.setName(accountRequest.getName());

		account.setOwner(owner);

		return account;
	}

	public SaveAccountResponse toResponse(SaveAccount account) {
		SaveAccountResponse accountResponse = new SaveAccountResponse();
		accountResponse.setBalance(account.getBalance());
		accountResponse.setId(account.getId());

		OwnerResponse ownerResponse = new OwnerResponse();
		ownerResponse.setName(account.getOwner().getName());
		ownerResponse.setLastName(account.getOwner().getLastName());
		ownerResponse.setBirthDate(account.getOwner().getBirthDate());
		ownerResponse.setCpf(account.getOwner().getCpf());

		accountResponse.setOwner(ownerResponse);

		return accountResponse;
	}
}
