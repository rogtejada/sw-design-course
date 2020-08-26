package com.rtejada.test.homework.owner;

import com.rtejada.test.homework.account.Account;
import com.rtejada.test.homework.saveaccount.SaveAccount;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table( name="owner")
public class Owner {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;

	@Column(name="name")
	private String name;

	@Column(name="last_name")
	private String lastName;

	@Column(name="cpf")
	private String cpf;

	@Column(name="birth_date")
	private LocalDate birthDate;

	@OneToOne(mappedBy = "owner")
	private Account account;

	@OneToOne(mappedBy = "owner")
	private SaveAccount saveAccount;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
