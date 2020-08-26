package com.rtejada.test.homework.account.v1;


import com.rtejada.test.homework.account.AccountService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

	private final AccountService accountService;
	private final AccountMapper accountMapper;

	public AccountController(AccountService accountService, AccountMapper accountMapper) {
		this.accountService = accountService;
		this.accountMapper = accountMapper;
	}

	@PostMapping
	public AccountResponse createAccount(@RequestBody AccountRequest accountRequest) {
		return accountMapper.toResponse(accountService.createAccount(accountMapper.toEntity(accountRequest)));
	}

	@GetMapping("/{accountId}")
	public AccountResponse getAccount(@PathVariable UUID accountId, @RequestParam UUID ownerId) {
		return accountMapper.toResponse(accountService.getAccount(accountId, ownerId).orElseThrow(() -> new RuntimeException("error")));
	}

	@DeleteMapping("/{accountId}")
	public void deleteAccount(@PathVariable UUID accountId, @RequestParam UUID ownerId) {
		accountService.deleteAccount(accountId, ownerId);
	}

	@PostMapping("/{accountId}/deposit")
	public BigDecimal deposit(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return accountService.deposit(transactionRequest.getValue(), accountId, ownerId);
	}

	@GetMapping("/{accountId}/withdraw")
	public BigDecimal withdraw(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return accountService.withdraw(transactionRequest.getValue(), accountId, ownerId);
	}

	@PostMapping("/{sourceId}/transfer/{targetId}")
	public BigDecimal transfer(@PathVariable UUID sourceId, @PathVariable UUID targetId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return accountService.transfer(transactionRequest.getValue(), sourceId, targetId, ownerId);
	}

	@PostMapping("/{sourceId}/save/{targetId}")
	public BigDecimal save(@PathVariable UUID sourceId, @PathVariable UUID targetId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return accountService.transferToSaving(transactionRequest.getValue(), sourceId, targetId, ownerId);
	}
}
