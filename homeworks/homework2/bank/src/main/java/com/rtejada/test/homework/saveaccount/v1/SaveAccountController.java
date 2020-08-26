package com.rtejada.test.homework.saveaccount.v1;

import com.rtejada.test.homework.saveaccount.SaveAccountService;
import com.rtejada.test.homework.account.v1.TransactionRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/v1/savings")
public class SaveAccountController {

	private final SaveAccountService saveAccountService;
	private final SaveAccountMapper saveAccountMapper;

	public SaveAccountController(SaveAccountService saveAccountService, SaveAccountMapper saveAccountMapper) {
		this.saveAccountService = saveAccountService;
		this.saveAccountMapper = saveAccountMapper;
	}

	@PostMapping
	public SaveAccountResponse createAccount(@RequestBody SaveAccountRequest accountRequest) {
		return saveAccountMapper.toResponse(saveAccountService.createAccount(saveAccountMapper.toEntity(accountRequest)));
	}

	@GetMapping("/{accountId}")
	public SaveAccountResponse getAccount(@PathVariable UUID accountId, @RequestParam UUID ownerId) {
		return saveAccountMapper.toResponse(saveAccountService.getAccount(accountId, ownerId));
	}

	@DeleteMapping("/{accountId}")
	public void deleteAccount(@PathVariable UUID accountId, @RequestParam UUID ownerId) {
		saveAccountService.deleteAccount(accountId, ownerId);
	}

	@GetMapping("/{accountId}/withdraw")
	public BigDecimal withdraw(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return saveAccountService.withdraw(transactionRequest.getValue(), accountId, ownerId);
	}

	@PostMapping("/{accountId}/deposit")
	public BigDecimal deposit(@PathVariable UUID accountId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return saveAccountService.deposit(transactionRequest.getValue(), accountId, ownerId);
	}

	@PostMapping("/{accountId}/transfer/{targetId}")
	public BigDecimal deposit(@PathVariable UUID accountId, @PathVariable UUID targetId, @RequestBody TransactionRequest transactionRequest, @RequestParam UUID ownerId) {
		return saveAccountService.transferForAccount(transactionRequest.getValue(), accountId, targetId, ownerId);
	}
}
