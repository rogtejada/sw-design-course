package com.rtejada.test.homework.saveaccount;

import com.rtejada.test.homework.owner.Owner;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name="save_account")
public class SaveAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private UUID id;

	@Column(name="balance")
	private BigDecimal balance;

	@Column(name="last_transfer_date")
	private LocalDate lastTransfer;

	@Column(name="transfer_count")
	private Long transferCount;

	@Column(name="last_transaction")
	private LocalDateTime lastTransaction;

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

	public LocalDateTime getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(LocalDateTime lastDeposit) {
		this.lastTransaction = lastDeposit;
	}

	public LocalDate getLastTransfer() {
		return lastTransfer;
	}

	public void setLastTransfer(LocalDate lastWithdraw) {
		this.lastTransfer = lastWithdraw;
	}

	public Long getTransferCount() {
		return transferCount;
	}

	public void setTransferCount(Long withdrawCount) {
		this.transferCount = withdrawCount;
	}
}
