package com.rtejada.test.homework.account;

import com.rtejada.test.homework.owner.Owner;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;

	@Column(name="balance")
	private BigDecimal balance;

	@Column(name="last_withdraw_date")
	private LocalDate lastWithdraw;

	@Column(name="withdraw_count")
	private Long withdrawCount;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "owner_id", referencedColumnName = "id")
	private Owner owner;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public LocalDate getLastWithdraw() {
		return lastWithdraw;
	}

	public void setLastWithdraw(LocalDate lastWithdraw) {
		this.lastWithdraw = lastWithdraw;
	}

	public Long getWithdrawCount() {
		return withdrawCount;
	}

	public void setWithdrawCount(Long withdrawCount) {
		this.withdrawCount = withdrawCount;
	}
}
