package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.TransfersRepository;


import lombok.Getter;

import org.springframework.stereotype.Service;

@Service
public class TransfersService {
	@Getter
	  private final TransfersRepository transfersRepository;
	
	  @Autowired
	  public TransfersService(TransfersRepository transfersRepository) {
	    this.transfersRepository = transfersRepository;
	  }

	  public void createTransfer(Transfer transfer) {
	    this.transfersRepository.createTransfer(transfer);
	  }

	  public Transfer getTransfer(String transferFromAccountId) {
	    return this.transfersRepository.getTransfer(transferFromAccountId);
	  }

	  public void updateTransfer(Transfer transfer) {
		this.transfersRepository.updateTransfer(transfer);
		// TODO 
		
	  }
	  public void updateAccountAfterTransfer(Transfer transfer) {
			this.transfersRepository.updateTransfer(transfer);
			// TODO 
			
		  }
}
