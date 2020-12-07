package com.rtejada.bank.v1.controller;

import com.rtejada.bank.service.TransferService;
import com.rtejada.bank.v1.dto.TransferRequest;
import com.rtejada.bank.v1.mapper.TransferMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/transfers")
public class TransferController {

	private final TransferService transferService;
	private final TransferMapper mapper;

	public TransferController(TransferService transferService, TransferMapper mapper) {
		this.transferService = transferService;
		this.mapper = mapper;
	}

	@PostMapping()
	public BigDecimal transfer(@RequestBody TransferRequest transferRequest) {
		return transferService.transfer(mapper.toEntity(transferRequest));
	}
}
