package com.db.awmd.challenge.service;

import org.springframework.context.annotation.Configuration;


import com.db.awmd.challenge.domain.Account;



@Configuration
public interface NotificationService {

	void notifyAboutTransfer(Account account, String transferDescription);
}
