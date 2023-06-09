package com.db.awmd.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

// This service can even be eliminated 
// Because now every transaction is handled by AccountsService.java
@Service
public class TransfersService {

	@Getter
	@Setter
	private AccountsRepository accountsRepository;

	@Autowired
	public TransfersService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

}
