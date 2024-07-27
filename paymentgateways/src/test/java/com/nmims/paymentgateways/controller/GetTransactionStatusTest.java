package com.nmims.paymentgateways.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.paymentgateways.bean.TransactionStatusBean;

@SpringBootTest
@AutoConfigureMockMvc
public class GetTransactionStatusTest {

	@Autowired MockMvc mockMvc;
	
	private ObjectMapper mapper = new  ObjectMapper();
	
	@Test public void shouldGetSuccess() throws Exception {
		
		TransactionStatusBean bean = new TransactionStatusBean();
		bean.setTrackId("166027943947731932");
		MvcResult result =  this.mockMvc.perform(post("/student/getTransactionStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andDo(print())
				.andExpect( r -> assertEquals(r.getResponse().getStatus() , 200))
				.andReturn()
				;
		TransactionStatusBean resultBean =  mapper.readValue(result.getResponse().getContentAsString(), TransactionStatusBean.class);
		assertEquals(resultBean.getMerchantRefNo(),bean.getTrackId());
		assertEquals(resultBean.getOrderStatus(), "paid");
		assertEquals(resultBean.isHasError(), false);
		assertEquals(resultBean.getAmount(), "500");		
	}
	
	@Test public void shouldGetFail() throws JsonProcessingException, Exception {
		TransactionStatusBean bean = new TransactionStatusBean();
		bean.setTrackId("166028401568567116");
		
		MvcResult result =  this.mockMvc.perform(post("/student/getTransactionStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andDo(print())
				.andExpect( r -> assertEquals(r.getResponse().getStatus() , 200))
				.andReturn()
				;
		TransactionStatusBean resultBean =  mapper.readValue(result.getResponse().getContentAsString(), TransactionStatusBean.class);
		assertEquals(resultBean.getOrderStatus(), "attempted");
		assertEquals(resultBean.isHasError(), true);
	}
	
	@Test public void shouldGetError() throws JsonProcessingException, Exception {
		TransactionStatusBean bean = new TransactionStatusBean();
		bean.setTrackId("1234564");
		
		MvcResult result =  this.mockMvc.perform(post("/student/getTransactionStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andDo(print())
				.andExpect( r -> assertEquals(r.getResponse().getStatus() , 200))
				.andReturn()
				;
		TransactionStatusBean resultBean =  mapper.readValue(result.getResponse().getContentAsString(), TransactionStatusBean.class);
		assertEquals(resultBean.isHasError(), true);
	}
}
