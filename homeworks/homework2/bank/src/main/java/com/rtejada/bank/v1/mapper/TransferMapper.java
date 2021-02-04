package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.Transfer;
import com.rtejada.bank.v1.dto.TransferRequest;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

	public Transfer toEntity(TransferRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Cannot map null transfer request");
		}

		Transfer transfer = new Transfer();
		transfer.setAmount(request.getAmount());
		transfer.setSourceType(request.getSourceType());
		transfer.setSourceId(request.getSourceId());
		transfer.setTargetType(request.getTargetType());
		transfer.setTargetId(request.getTargetId());
		return transfer;
	}
}
