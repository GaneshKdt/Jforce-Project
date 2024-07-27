package com.nmims.paymentgateways.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.nmims.paymentgateways.dao.PaymentOptionsDAO;
import com.nmims.paymentgateways.service.TransactionService;

//@WebMvcTest(controllers = TransactionController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class GatewayStageTransactionTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TransactionService service;
	
	@Autowired
	PaymentOptionsDAO options;
	
	@Autowired
	JdbcTemplate template;

	
	@DisplayName("Should get me payment page")
	@Test
	public void shouldGetPaymentOption() throws Exception {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("track_id", String.valueOf(System.currentTimeMillis()));
		body.add("response_method", "post");
		body.add("sapid", "77777777771");
		body.add("type", "adhoc");
		body.add("amount", "400");
		body.add("description", "monnnies");
		body.add("source", "web");
		body.add("portal_return_url", "localhost:8080");
		body.add("created_by", "77777777771");
		body.add("updated_by", "77777777771");
		body.add("mobile", "9137123692");
		body.add("email_id", "something@123");
		body.add("first_name", "Swarup");
		
		MvcResult mvcResult =  this.mockMvc.perform(post("/student/selectGatewayStageTransaction")
		.contentType(MediaType.APPLICATION_JSON)
		.params(body)
		.characterEncoding("utf-8")
		.accept(MediaType.TEXT_HTML))
		.andDo(print())
		.andExpect(view()
		.name("payment"))
		.andReturn()
		;
	}
	
	
	@Test
	public void shouldNotGetPaymentOption() throws Exception {
		
		MvcResult mvcResult =  this.mockMvc.perform(post("/student/selectGatewayStageTransaction")
				.contentType(MediaType.APPLICATION_JSON)
				.param("track_id","3213123")
				.characterEncoding("utf-8")
				.accept(MediaType.TEXT_HTML))
				.andDo(print())
				.andExpect(result -> assertNotEquals(result.getModelAndView().getViewName(), "payment"))
				.andDo(print())
				.andReturn()
				;
	}
}