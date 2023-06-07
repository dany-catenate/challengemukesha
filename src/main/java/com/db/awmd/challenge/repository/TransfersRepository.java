package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Transfer;
public interface TransfersRepository {
	 void createTransfer(Transfer transfer);

	 Transfer getTransfer(String accountFromId);

	 void clearTransfers();

	 void updateTransfer(Transfer transfer);

	void updateAccountAfterTransfer(Transfer transfer);
}

