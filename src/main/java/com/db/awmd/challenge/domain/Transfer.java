package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Transfer {

	@NotNull
	@NotEmpty
	private final String transferFromAccountId;

	@NotNull
	@NotEmpty
	private final String transferToAccountId;

	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	private BigDecimal amountTransferred;

	public Transfer(String transferToAccountId, String transferFromAccountId) {
		this.transferFromAccountId = transferFromAccountId;
		this.transferToAccountId = transferToAccountId;
		this.amountTransferred = BigDecimal.ZERO;
	}

	@JsonCreator
	public Transfer(@JsonProperty("transferFromAccountId") String transferFromAccountId,
			@JsonProperty("transferToAccountId") String transferToAccountId,
			@JsonProperty("amountTransferred") BigDecimal amountTransferred) {
		this.transferFromAccountId = transferFromAccountId;
		this.transferToAccountId = transferToAccountId;
		this.amountTransferred = amountTransferred;

	}

	// to be removed
//	  @JsonIgnore
//	  private BigDecimal balanceAfterTransfer;
//	  @JsonIgnore
//	  public void setBalanceAfterTransfer(BigDecimal balanceAfterTransfer) {
//			this.balanceAfterTransfer = balanceAfterTransfer;			
//		}
}
