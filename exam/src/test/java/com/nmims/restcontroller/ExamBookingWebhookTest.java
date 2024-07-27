package com.nmims.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ExamBookingWebhookTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	ExamBookingDAO ebDao;
	
	@MockBean
	ExamCenterDAO ecDao;

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void uponGettingInvalidPayload() throws Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("unhandled payment status");

		MvcResult mvcResult = this.mvc.perform(post("/m/examGatewayResponse")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andReturn();

		assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo("INVALID REQUEST");
		
	}

	@Test
	public void uponGettingExamBookingNotLive() throws JsonProcessingException, Exception {

		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Successfull");
		bean.setSapid("not null");
		bean.setTrack_id("not null");

		when(ebDao.isConfigurationLive(anyString())).thenReturn(false);

		this.mvc.perform(post("/m/examGatewayResponse").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("Exam registration is not live so sending back payload"));
	}

	@Test
	public void getErrorOnsapIdNotFound() throws JsonProcessingException, Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Successfull");
		bean.setSapid("test");
		bean.setTrack_id("test_track");

		when(ebDao.isConfigurationLive(anyString())).thenReturn(true);
		when(ebDao.getAllBookingsBySapId("test")).thenReturn(new ArrayList<>(Arrays.asList()));

		this.mvc.perform(post("/m/examGatewayResponse")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("No transactions found for sapid : "));
	}

	@Test
	public void getErrorOnNoTrackIdFound() throws JsonProcessingException, Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Successfull");
		bean.setSapid("test");
		bean.setTrack_id("test_track");

		when(ebDao.isConfigurationLive(anyString())).thenReturn(true);
		when(ebDao.getAllBookingsBySapId("test")).thenReturn(returnListWithDifferentTrackId());

		this.mvc.perform(post("/m/examGatewayResponse").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("No records for track id : "));

	}

	@Test
	public void alreadyMarkedAsSuccessful() throws JsonProcessingException, Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Successfull");
		bean.setSapid("test");
		bean.setTrack_id("test_track");

		when(ebDao.isConfigurationLive(anyString())).thenReturn(true);
		when(ebDao.getAllBookingsBySapId("test")).thenReturn(returnListWithSuccessfulBooked());

		this.mvc.perform(post("/m/examGatewayResponse")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("Payments already updated for track id : "));

	}
	
	@Test
	public void alreadyMarkedAsFailed() throws JsonProcessingException, Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Failed");
		bean.setSapid("test");
		bean.setTrack_id("test_track");
		
		when(ebDao.isConfigurationLive(anyString())).thenReturn(true);
		when(ebDao.getAllBookingsBySapId("test")).thenReturn(returnListWithFailed());
		
		this.mvc.perform(post("/m/examGatewayResponse")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("Payments already updated for track id : "));
		
	}
	
	@Test
	public void markTransactionAsFailed() throws JsonProcessingException, Exception {
		TransactionsBean bean = new TransactionsBean();
		bean.setTransaction_status("Payment Failed");
		bean.setSapid("test");
		bean.setTrack_id("test_track");
		
		when(ebDao.isConfigurationLive(anyString())).thenReturn(true);
		when(ebDao.getAllBookingsBySapId("test")).thenReturn(returnInitiatedTransactions());
		
		this.mvc.perform(post("/m/examGatewayResponse")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(bean)))
				.andExpect(result -> assertThat(result.getResponse().getContentAsString())
				.startsWith("table updated as failed for track id : "));
		
		ExamBookingTransactionBean examBooking = failedExamBookingBean();
		
		verify(ebDao).markTransactionsFailed(examBooking);
	}
	
	private ExamBookingTransactionBean failedExamBookingBean() {
		ExamBookingTransactionBean examBooking = new ExamBookingTransactionBean();
		return null;
	}

	private List<ExamBookingTransactionBean> returnInitiatedTransactions() {
		List<ExamBookingTransactionBean> bookingTransactionBeans = new ArrayList<>();
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		bean.setTrackId("test_track");
		bean.setBooked("N");
		bean.setTranStatus("Online Payment Initiated");
		bookingTransactionBeans.add(bean);
		return bookingTransactionBeans;
	}

	private List<ExamBookingTransactionBean> returnListWithFailed() {
		List<ExamBookingTransactionBean> bookingTransactionBeans = new ArrayList<>();
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		bean.setTrackId("test_track");
		bean.setBooked("N");
		bean.setTranStatus("Transaction Failed");
		bookingTransactionBeans.add(bean);
		return bookingTransactionBeans;
	}

	private List<ExamBookingTransactionBean> returnListWithSuccessfulBooked() {
		List<ExamBookingTransactionBean> bookingTransactionBeans = new ArrayList<>();
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		bean.setTrackId("test_track");
		bean.setBooked("Y");
		bookingTransactionBeans.add(bean);
		return bookingTransactionBeans;
	}

	private List<ExamBookingTransactionBean> returnListWithDifferentTrackId() {
		List<ExamBookingTransactionBean> bookingTransactionBeans = new ArrayList<>();
		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
		bean.setTrackId("not_test_track_id");
		bookingTransactionBeans.add(bean);
		return bookingTransactionBeans;
	}
}
