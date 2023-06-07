package com.db.awmd.challenge.repository;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.NotificationService;

import lombok.Data;

import com.db.awmd.challenge.domain.Account;



import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
@Data
public class TransfersRepositoryInMemory implements TransfersRepository{

	private final Map<String, Transfer> transfers = new ConcurrentHashMap<>();
	private final Map<String, Account> accounts = new ConcurrentHashMap<>();
	private java.lang.String message;
	private Account account;

	@Override
	public void createTransfer(Transfer transfer) {
	    Transfer previousTransfer = transfers.putIfAbsent(transfer.getTransferFromAccountId(), transfer);
		this.message=transfer.getTransferFromAccountId() + " transferred "+ 
						transfer.getAmountTransferred() +" To "+ transfer.getTransferToAccountId();
	  }
	

	@Override
	public Transfer getTransfer(String transferFromAccountId) {
		// TODO 
		return transfers.get(transferFromAccountId);
		
	}

	@Override
	public void clearTransfers() {
		// TODO 
		transfers.clear();
		
	}

	@Override
	public void updateTransfer(Transfer transfer) {
		// TODO 
		
	}




	@Override
	public void updateAccountAfterTransfer(Transfer transfer) {
		account.setBalance(account.getBalance().subtract(transfer.getAmountTransferred()));
		// TODO 
		
	}


}

