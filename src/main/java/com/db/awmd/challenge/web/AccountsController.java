package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransfersService;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  private final TransfersService transfersService;

  @Autowired
  public AccountsController(AccountsService accountsService, TransfersService transfersService) {
    this.accountsService = accountsService;
	this.transfersService = transfersService;
  }
  
  
  
  //For the accounts

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  
  
  //update one single account
  
  @PutMapping(path = "/updateAccount", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> updateAccount(@RequestBody @Valid Account account) {
    log.info("Updating account {}", account);

    this.accountsService.updateAccount(account);
    

    return new ResponseEntity<>(HttpStatus.CREATED);
  }
 
  
  
  
  // For the transfers
  
  @PostMapping(path = "/createTransfer", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createTransfer(@RequestBody @Valid Transfer transfer) {
	this.transfersService.createTransfer(transfer);
	log.info("Creating transfer {}", transfer);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
  
  @GetMapping(path = "createdTransfer/{transferFromAccountId}")
  public Transfer getTransfer(@PathVariable String transferFromAccountId) {
    log.info("Retrieving transfer for id {}", transferFromAccountId);
    return this.transfersService.getTransfer(transferFromAccountId);
  }
  

}
