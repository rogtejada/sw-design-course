package com.rtejada.bank.v1.dto;

import java.math.BigDecimal;

public class TransactionRequest {

	private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
