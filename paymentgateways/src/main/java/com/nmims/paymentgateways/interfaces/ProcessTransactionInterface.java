package com.nmims.paymentgateways.interfaces;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.enums.WebhookEnum;

/**
 * Inteface to define a set of rule for webhook from payment gateways like Paytm
 * and Razorpay
 * 
 * @author Swarup Singh Rajpurohit
 * @since Paytm Wehbook Integration
 */
public interface ProcessTransactionInterface {

	/**
	 * Uses to get track id from payload return type is track id itself,if not found
	 * error is thrown and handled accordingly
	 * 
	 * @return track Id (String)
	 */
	String getTrackId();

	/**
	 * Populates request bean for API when transaction was successful
	 * 
	 * @param TransactionsBean
	 */
	boolean populateBeanForSuccessfulTransaction(TransactionsBean bean);

	/**
	 * Populates request bean for API when transaction was failed
	 * 
	 * @param TransactionsBean
	 */
	boolean populateBeanForFailedTransaction(TransactionsBean bean);

	/**
	 * Sends transaction details to modules which is common among all
	 * implementations so setting it as default
	 * 
	 * @param TransactionsBean transactionsBean
	 * @param String           url
	 * @return String reponse from API
	 */
	default String postForTransactionUpdate(TransactionsBean transactionsBean, String url) {
		RestTemplate restTemplate = new RestTemplate();
		String returnString = null;
		try {
			HttpEntity<TransactionsBean> requestBody = new HttpEntity<>(transactionsBean);

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestBody,
					String.class);

			if (responseEntity.hasBody()) {
				returnString = responseEntity.getBody();
			} else {
				returnString = "Invalid resonse received for track id : " + transactionsBean.getTrack_id();
			}

		} catch (Exception e) {
			returnString = "Error while processing webhook for track id : " + transactionsBean.getTrack_id() + " : "
					+ e;
		}
		return returnString;
	}

	/**
	 * Returns payment success status
	 * 
	 * @return boolean
	 */
	boolean isPaymentSuccess();

	/**
	 * Checks error in payload and transaction in general while comparing data with
	 * data we have in database to ensure data integrity
	 * 
	 * @param bean
	 * @param payloadTrackId
	 * @return boolean
	 */
	void checkErrorInPayload(TransactionsBean bean, String payloadTrackId);

	String processWebhookTransaction(PayloadWrapper wrapper, WebhookEnum implementation);
}
