package com.rtejada.bank.v1.controller;

import com.rtejada.bank.service.CreditAccountService;
import com.rtejada.bank.v1.dto.AccountRequest;
import com.rtejada.bank.v1.dto.AccountResponse;
import com.rtejada.bank.v1.dto.StatementResponse;
import com.rtejada.bank.v1.dto.TransactionRequest;
import com.rtejada.bank.v1.mapper.AccountMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/credit-accounts")
public class CreditAccountController {

	private final CreditAccountService creditAccountService;
	private final AccountMapper accountMapper;

	public CreditAccountController(CreditAccountService creditAccountService, AccountMapper accountMapper) {
		this.creditAccountService = creditAccountService;
		this.accountMapper = accountMapper;
	}

	@PostMapping
	public AccountResponse createAccount(@RequestBody AccountRequest accountRequest) {
		return accountMapper.toResponse(creditAccountService.createAccount(accountMapper.toEntity(accountRequest)));
	}

	@GetMapping("/{accountId}/balance")
	public BigDecimal getBalance(@PathVariable UUID accountId) {
		return creditAccountService.getBalance(accountId);
	}

	@PostMapping("/{accountId}/deposit")
	public BigDecimal deposit(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest) {
		return creditAccountService.deposit(transactionRequest.getValue(), accountId);
	}

	@PostMapping("/{accountId}/withdraw")
	public BigDecimal withdraw(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest) {
		return creditAccountService.withdraw(transactionRequest.getValue(), accountId);
	}

	@GetMapping("/{accountId}/statement")
	public List<StatementResponse> getStatement(@PathVariable UUID accountId) {
		return creditAccountService.getStatement(accountId)
				.stream()
				.map(accountMapper::toResponse)
				.collect(Collectors.toList());
	}
}
