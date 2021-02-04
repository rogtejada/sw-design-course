package com.rtejada.bank.v1.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {

	@NotNull
	private BigDecimal value;

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
