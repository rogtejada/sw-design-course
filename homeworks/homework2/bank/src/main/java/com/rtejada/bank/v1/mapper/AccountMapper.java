package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.Owner;
import com.rtejada.bank.model.Statement;
import com.rtejada.bank.v1.dto.AccountRequest;
import com.rtejada.bank.v1.dto.AccountResponse;
import com.rtejada.bank.v1.dto.OwnerResponse;
import com.rtejada.bank.v1.dto.StatementResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

	public Account toEntity(AccountRequest request) {
		Owner owner = new Owner();
		owner.setName(request.getName());
		owner.setCpf(request.getCpf());
		Account account = new Account();
		account.setOwner(owner);

		return account;
	}

	public AccountResponse toResponse(Account entity) {
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setId(entity.getId());
		accountResponse.setAccountType(entity.getAccountType());

		OwnerResponse ownerResponse = new OwnerResponse();
		ownerResponse.setCpf(entity.getOwner().getCpf());
		ownerResponse.setName(entity.getOwner().getName());

		accountResponse.setOwner(ownerResponse);

		return accountResponse;
	}

	public StatementResponse toResponse(Statement statement) {
		StatementResponse statementResponse = new StatementResponse();
		statementResponse.setDate(statement.getDate());
		statementResponse.setTransaction(statement.getTransaction());
		statementResponse.setValue(statement.getValue());
		return statementResponse;
	}
}
