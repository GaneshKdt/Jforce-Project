package com.nmims.paymentgateways.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.gson.JsonObject;

@SpringBootTest
public class TransactionStatusTest {

	@Autowired
	private RazorpayHelper helper;

	@Test
	public void shouldGetSuccess() {
		JsonObject transactionEntity = helper.getTransactionStatus("165916352163720872");
		assertThat(transactionEntity.get("status").toString()).isEqualTo("\"captured\"");
		assertNotNull(transactionEntity);
	}

	@Test
	public void shouldGetFailed() {
		JsonObject entity = helper.getTransactionStatus("165916406849168424");
		assertThat(entity.get("status").toString()).isEqualTo("\"failed\"");
		assertNotNull(entity);
	}

	@Test
	public void shouldGetNull() {
		JsonObject entity = helper.getTransactionStatus("1659178702292");
		assertNull(entity);
	}
}
