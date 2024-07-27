package com.nmims.paymentgateways.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.api.Assertions;
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

@SpringBootTest
@AutoConfigureMockMvc
public class ProcessTransactionTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TransactionService service;
	
	@Autowired
	PaymentOptionsDAO options;
	
	@Autowired
	JdbcTemplate template;
	
	@Test
	public void shouldGetPaytmPage() throws Exception {
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
		body.add("payment_option", "paytm");
		
		MvcResult mvcResult =  this.mockMvc.perform(post("/student/processTransaction")
				.contentType(MediaType.APPLICATION_JSON)
				.params(body)
				.characterEncoding("utf-8")
				.accept(MediaType.TEXT_HTML))
				.andDo(print())
				.andExpect(view()
				.name("paytmPay"))
				.andReturn()
				;
	}
	
	@Test
	public void shouldGetRazorpayPage() throws Exception {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		String track_id = String.valueOf(System.currentTimeMillis());
		body.add("track_id", track_id);
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
		body.add("payment_option", "razorpay");
		
		MvcResult mvcResult =  this.mockMvc.perform(post("/student/processTransaction")
				.contentType(MediaType.APPLICATION_JSON)
				.params(body)
				.characterEncoding("utf-8")
				.accept(MediaType.TEXT_HTML))
				.andDo(print())
				.andExpect(result -> assertEquals(result.getModelAndView().getViewName(), "razorpayPay"))
				.andExpect(result -> assertEquals(result.getModelAndView().getModel().get("amount"), "40000"))
				.andExpect(result -> assertEquals(result.getModelAndView().getModel().get("first_name"), "Swarup"))
				.andReturn()
				;
	}
}
