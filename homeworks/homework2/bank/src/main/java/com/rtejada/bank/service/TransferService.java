package com.rtejada.bank.service;

import com.rtejada.bank.exception.InvalidTransactionException;
import com.rtejada.bank.model.Account;
import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.model.Transfer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.rtejada.bank.model.AccountType.CREDIT;
import static com.rtejada.bank.model.AccountType.SAVING;

@Service
public class TransferService {

	private final Map<AccountType, AccountService> accountServiceMap;
	private static final BigDecimal INITIAL_TRANSFER_FEE = BigDecimal.valueOf(1.02);
	private static final BigDecimal TRANSFER_FEE = BigDecimal.valueOf(1.05);
	private static final Map<Long, BigDecimal> TRANSFER_RULES = buildTransferRules();

	public TransferService(CreditAccountService creditAccountService, SaveAccountService saveAccountService) {
		this.accountServiceMap = new HashMap<>();
		accountServiceMap.put(CREDIT, creditAccountService);
		accountServiceMap.put(SAVING, saveAccountService);
	}

	public BigDecimal transfer(Transfer transfer) {
		BigDecimal amountWithFee;

		if (SAVING.equals(transfer.getSourceType()) || SAVING.equals(transfer.getTargetType())) {

			final Account sourceAccount = accountServiceMap.get(transfer.getSourceType()).getAccount(transfer.getSourceId());

			String sourceCpf = sourceAccount.getOwner().getCpf();
			String targetCpf = accountServiceMap.get(transfer.getTargetType()).getAccount(transfer.getTargetId()).getOwner().getCpf();

			if (!sourceCpf.equals(targetCpf)) {
				throw new InvalidTransactionException("Cannot do transfer from/to saving account for different owners");
			}

			if (sourceAccount.getLastTransfer() != null
					&& sourceAccount.getLastTransfer().atStartOfDay().equals(LocalDate.now().atStartOfDay())
					&& (SAVING.equals(transfer.getSourceType()) && CREDIT.equals(transfer.getTargetType()))) {
				amountWithFee = transfer.getAmount().multiply(TRANSFER_RULES.getOrDefault(sourceAccount.getTransferCount(), TRANSFER_FEE));

			} else {
				amountWithFee = transfer.getAmount();
			}

		} else {
			amountWithFee = transfer.getAmount().multiply(TRANSFER_FEE);
		}

		final BigDecimal finalSourceBalance = accountServiceMap
				.get(transfer.getSourceType())
				.withdrawForTransfer(amountWithFee, transfer.getSourceId());

		accountServiceMap
				.get(transfer.getTargetType())
				.depositForTransfer(transfer.getAmount(), transfer.getTargetId());

		return finalSourceBalance;
	}


	private static Map<Long, BigDecimal> buildTransferRules() {
		Map<Long, BigDecimal> transferRules = new HashMap<>();
		transferRules.put(0L, BigDecimal.ONE);
		transferRules.put(1L, BigDecimal.ONE);
		transferRules.put(2L, BigDecimal.ONE);
		transferRules.put(3L, INITIAL_TRANSFER_FEE);

		return transferRules;
	}
}
