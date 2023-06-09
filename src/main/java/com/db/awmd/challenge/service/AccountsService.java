package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.OverDraftException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.variables.ErrorCodes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsService implements NotificationService {

	@Getter
	@Setter
	private AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public void createTransfer(Transfer transfer) {

		/* Just logging the amount money transferred */
		log.info("log -- Here is amount of money transferred: " + transfer.getAmountTransferred());

		Account accountFrom = this.accountsRepository.getAccount(transfer.getTransferFromAccountId());
//			.orElseThrow(() -> new AccountNotExistException("Account with id: " + transfer.getAccountFromId() + " does not exist.", ErrorCodes.ACCOUNT_ERROR));

		Account accountTo = this.accountsRepository.getAccount(transfer.getTransferToAccountId());
//				.orElseThrow(() -> new AccountNotExistException("Account with id: " + transfer.getAccountFromId() + " does not exist.", ErrorCodes.ACCOUNT_ERROR));

		/* Checking if there is enough money to transfer */
		if (accountFrom.getBalance().compareTo(transfer.getAmountTransferred()) < 0) {
			String errorMessage = "Account with id: " + accountFrom.getAccountId()
					+ " does not have enough balance to transfer.";
			log.info(errorMessage);
			throw new OverDraftException(
					"Account with id: " + accountFrom.getAccountId() + " does not have enough balance to transfer.",
					ErrorCodes.ACCOUNT_ERROR);
		}

//		FROM: Updating the balance of the first account giving money $
		BigDecimal temp1 = accountFrom.getBalance();
		accountFrom.setBalance(temp1.subtract(transfer.getAmountTransferred()));
		/* sending a notification to the sending account */
		String msg1 = "id of the account money transferred To: " + transfer.getTransferToAccountId()
				+ " Amount transferred: {}." + temp1;
		this.notifyAboutTransfer(accountFrom, msg1);
		log.info("log -- Balance After Transation for Account id: {} is {} euri.", accountFrom.getAccountId(),
				accountFrom.getBalance(), " - ", accountFrom.getBalance());

//		TO: Updating the balance of the second account receiving money $
		BigDecimal temp2 = accountTo.getBalance();
		accountTo.setBalance(temp2.add(transfer.getAmountTransferred()));
		/* sending a notification to the receiving account */
		String msg2 = "id of the account money transferred From: " + transfer.getTransferFromAccountId()
				+ " Amount transferred: {}." + temp2;
		this.notifyAboutTransfer(accountFrom, msg2);
		log.info("log -- Balance After Transation for Account id: {} is {} euri.", accountTo.getAccountId(),
				accountTo.getBalance(), " - ", accountTo.getBalance());

	}

	@Override
	public void notifyAboutTransfer(Account account, String transferDescription) {
		// TOBE implemented as it is assumed another colleague would implement it
	}
}
