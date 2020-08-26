package com.rtejada.test.homework.account.v1;

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
