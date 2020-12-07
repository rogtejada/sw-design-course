package com.rtejada.bank.v1.mapper;

import com.rtejada.bank.model.AccountType;
import com.rtejada.bank.model.Transfer;
import com.rtejada.bank.v1.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferMapperTest {

	private TransferMapper transferMapper;

	@BeforeEach
	public void setUp() {
		transferMapper = new TransferMapper();
	}

	@Test
	public void shouldMapToEntity() {
		TransferRequest request = new TransferRequest();
		request.setAmount(BigDecimal.TEN);
		request.setSourceId(UUID.randomUUID());
		request.setSourceType(AccountType.CREDIT);
		request.setTargetId(UUID.randomUUID());
		request.setTargetType(AccountType.SAVING);

		final Transfer result = transferMapper.toEntity(request);

		assertEquals(request.getAmount(), result.getAmount());
		assertEquals(request.getSourceId(), result.getSourceId());
		assertEquals(request.getSourceType(), result.getSourceType());
		assertEquals(request.getTargetId(), result.getTargetId());
		assertEquals(request.getTargetType(), result.getTargetType());
	}
}
