package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.model.Owner;
import com.rtejada.bank.v1.dto.AccountRequest;
import org.springframework.stereotype.Component;

@Component
public class AccountFactory {

	public Account toAccountEntity(AccountRequest request, AccountType accountType) {
		if (request == null) {
			throw new IllegalArgumentException("Cannot create null account");
		}

		if (accountType == null) {
			throw new IllegalArgumentException("Cannot create account without type");
		}

		Owner owner = new Owner();
		owner.setName(request.getName());
		owner.setCpf(request.getCpf());
		Account account = new Account();
		account.setAccountType(accountType);
		account.setOwner(owner);

		return account;
	}
}
