package com.rtejada.bank.v1.dto;

import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotNull;

public class AccountRequest {

	@NotNull
	private String name;

	@CPF
	@NotNull
	private String cpf;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}
