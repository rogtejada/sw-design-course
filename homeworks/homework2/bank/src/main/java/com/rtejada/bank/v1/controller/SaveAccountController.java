package com.rtejada.bank.v1.controller;

import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.service.SaveAccountService;
import com.rtejada.bank.v1.dto.AccountRequest;
import com.rtejada.bank.v1.dto.AccountResponse;
import com.rtejada.bank.v1.dto.StatementResponse;
import com.rtejada.bank.v1.dto.TransactionRequest;
import com.rtejada.bank.v1.mapper.AccountFactory;
import com.rtejada.bank.v1.mapper.AccountMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/save-accounts")
public class SaveAccountController {

	private final SaveAccountService saveAccountService;
	private final AccountMapper accountMapper;
	private final AccountFactory accountFactory;

	public SaveAccountController(SaveAccountService saveAccountService, AccountMapper accountMapper, AccountFactory accountFactory) {
		this.saveAccountService = saveAccountService;
		this.accountMapper = accountMapper;
		this.accountFactory = accountFactory;
	}

	@PostMapping
	public AccountResponse createAccount(@Valid @RequestBody AccountRequest accountRequest) {
		return accountMapper.toResponse(saveAccountService.createAccount(accountFactory.toAccountEntity(accountRequest, AccountType.SAVING)));
	}

	@GetMapping("/{accountId}/balance")
	public BigDecimal getBalance(@PathVariable UUID accountId) {
		return saveAccountService.getBalance(accountId);
	}

	@PostMapping("/{accountId}/deposit")
	public BigDecimal deposit(@PathVariable UUID accountId, @Valid @RequestBody TransactionRequest transactionRequest) {
		return saveAccountService.deposit(transactionRequest.getValue(), accountId);
	}

	@PostMapping("/{accountId}/withdraw")
	public BigDecimal withdraw(@PathVariable UUID accountId, @Valid @RequestBody TransactionRequest transactionRequest) {
		return saveAccountService.withdraw(transactionRequest.getValue(), accountId);
	}

	@GetMapping("/{accountId}/statement")
	public List<StatementResponse> getStatement(@PathVariable UUID accountId) {
		return saveAccountService.getStatement(accountId)
				.stream()
				.map(accountMapper::toResponse)
				.collect(Collectors.toList());
	}
}
