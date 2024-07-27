package com.nmims.paymentgateways.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
public class GetRefundStatusTest {

	@Autowired MockMvc mockMvc;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Test public void shouldGetSuccess() throws JsonProcessingException, Exception {
		
		TransactionStatusBean bean = new TransactionStatusBean();
		bean.setRefundId("rfnd_K0fMoZLWMMHzRa");
		
		MvcResult mvcResult = this.mockMvc.perform(post("/student/getRefundStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andDo(print())
				.andExpect(r -> assertEquals(200, r.getResponse().getStatus()))
				.andReturn(); 
		
		TransactionStatusBean result = mapper.readValue(mvcResult.getResponse().getContentAsString(), TransactionStatusBean.class);
		assertEquals("processed", result.getRefundStatus());
		assertEquals(false, bean.isHasError());
	}

	@Test public void shouldGetError() throws JsonProcessingException, Exception {
		
		TransactionStatusBean bean = new TransactionStatusBean();
		bean.setRefundId("aaaaa");
		
		MvcResult mvcResult = this.mockMvc.perform(post("/student/getRefundStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andDo(print())
				.andExpect(r -> assertEquals(200, r.getResponse().getStatus()))
				.andReturn(); 
		
		TransactionStatusBean result = mapper.readValue(mvcResult.getResponse().getContentAsString(), TransactionStatusBean.class);
		assertEquals(true, result.isHasError());
	}
}
