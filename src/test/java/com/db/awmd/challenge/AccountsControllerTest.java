package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void createAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-123");
		assertThat(account.getAccountId()).isEqualTo("Id-123");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createDuplicateAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"balance\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBalance() throws Exception {
		this.mockMvc.perform(
				post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"Id-123\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBody() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNegativeBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountEmptyAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void getAccount() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
	}

	@Test
	public void createTransfer() throws Exception {
		// creating the first account
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		Account account1BeforeTransfer = accountsService.getAccount("Id-123");
		final BigDecimal amount1BeforeTransfer = account1BeforeTransfer.getBalance();

		// creating the second account
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-124\",\"balance\":1000}")).andExpect(status().isCreated());
		Account account2BeforeTransfer = accountsService.getAccount("Id-124");
		final BigDecimal amount2BeforeTransfer = account2BeforeTransfer.getBalance();

		// run the API for money transactions
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "\"transferToAccountId\":\"Id-124\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isAccepted())
		.andExpect(content().string("The process of the transaction has been completed with success."));

		// amount transferred
		BigDecimal amountTransferred = new BigDecimal("100");
		
		// check if the money was removed from the first account
		Account account1AfterTransfer = this.accountsService.getAccountsRepository().getAccount("Id-123");
		assertThat(account1AfterTransfer.getBalance()).isEqualByComparingTo(amount1BeforeTransfer.subtract(amountTransferred));

		// check if the money was added to the second account
		Account account2AfterTransfer = this.accountsService.getAccountsRepository().getAccount("Id-124");
		assertThat(account2AfterTransfer.getBalance()).isEqualByComparingTo(amount2BeforeTransfer.add(amountTransferred));
		
		// making a transaction of money FROM a non-existing account
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-1230000\","
						+ "\"transferToAccountId\":\"Id-124\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isBadRequest());			
		
		// making a transaction of money TO a non-existing account
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "\"transferToAccountId\":\"Id-1240000\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isBadRequest());
	

		// Overdrafts: trying to transfer more money the available from the original account 
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "\"transferToAccountId\":\"Id-124\","
						+ "\"amountTransferred\":10000}"))
		.andExpect(status().isBadRequest())
		.andExpect(content().string("Account with id: " + "Id-123" + " does not have enough balance to transfer."));
		
		// with amount to transfer less than zero 
				this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
						.content("{\"transferFromAccountId\":\"Id-123\","
								+ "\"transferToAccountId\":\"Id-124\","
								+ "\"amountTransferred\":-100}"))
				.andExpect(status().isBadRequest());
				
		// making a transfer with the same accountId FROM as the accountId TO 
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "{\"transferToAccountId\":\"Id-123\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isBadRequest());	
	
	}
	
	@Test
	public void createTransferWithoutAccountIdFrom() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferToAccountId\":\"Id-124\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void createTransferWithoutAccountIdTo() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "\"amountTransferred\":100}"))
		.andExpect(status().isBadRequest());
		}
	
	@Test
	public void createTransferWithoutAmountToTransfer() throws Exception {
		this.mockMvc.perform(post("/v1/accounts/transactions").contentType(MediaType.APPLICATION_JSON)
				.content("{\"transferFromAccountId\":\"Id-123\","
						+ "\"transferToAccountId\":\"Id-1240000\"}"))
		.andExpect(status().isBadRequest());
	}

}
