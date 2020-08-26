package com.rtejada.test.homework.account.v1;

import com.rtejada.test.homework.account.Account;
import com.rtejada.test.homework.owner.Owner;
import com.rtejada.test.homework.owner.v1.OwnerResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper {

	public Account toEntity(AccountRequest accountRequest) {
		Account account = new Account();
		account.setBalance(BigDecimal.ZERO);
		account.setWithdrawCount(0L);

		Owner owner = new Owner();
		owner.setCpf(accountRequest.getCpf());
		owner.setBirthDate(accountRequest.getBirthDate());
		owner.setLastName(accountRequest.getLastName());
		owner.setName(accountRequest.getName());

		account.setOwner(owner);

		return account;
	}

	public AccountResponse toResponse(Account account) {
		AccountResponse accountResponse = new AccountResponse();
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
