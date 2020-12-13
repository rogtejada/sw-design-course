package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.Statement;
import com.rtejada.bank.v1.dto.AccountResponse;
import com.rtejada.bank.v1.dto.OwnerResponse;
import com.rtejada.bank.v1.dto.StatementResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

	public AccountResponse toResponse(Account entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Cannot map null account");
		}
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setId(entity.getId());
		accountResponse.setAccountType(entity.getAccountType());

		if (entity.getOwner() == null) {
			throw new IllegalArgumentException("Cannot map entity without owner");
		}

		OwnerResponse ownerResponse = new OwnerResponse();
		ownerResponse.setCpf(entity.getOwner().getCpf());
		ownerResponse.setName(entity.getOwner().getName());

		accountResponse.setOwner(ownerResponse);

		return accountResponse;
	}

	public StatementResponse toResponse(Statement statement) {
		if (statement == null) {
			throw new IllegalArgumentException("Cannot map null statement");
		}

		StatementResponse statementResponse = new StatementResponse();
		statementResponse.setDate(statement.getDate());
		statementResponse.setTransaction(statement.getTransaction());
		statementResponse.setValue(statement.getValue());
		return statementResponse;
	}
}
