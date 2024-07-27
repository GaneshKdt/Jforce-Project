package com.nmims.helpers;

import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.PaymentDetails;
import com.paytm.pg.merchant.CheckSumServiceHelper;


@Component
public class CheckSumHelper {

	
	@Value("${SFDC_CHECKSUM_MERCHANT_KEY}")
	private String MERCHANT_KEY;

	private String paytmChecksum = null;

	private static final Logger logger = LoggerFactory.getLogger(CheckSumHelper.class);
 
	public void verifyChecksum(HttpServletRequest requestParams, PaymentDetails paymentResponse) {
		verifyPaytmChecksum(requestParams, paymentResponse);
		return;
	}
	
	
	// Create a tree map from the form post param
	private void verifyPaytmChecksum(HttpServletRequest request, PaymentDetails paymentResponse) {
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		// Request is HttpServletRequest
		for (Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
		    if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
		        paytmChecksum = requestParamsEntry.getValue()[0];
		    }else {
		        paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
		    }
		}
		// Call the method for verification
		boolean isValidChecksum;
		try {
			isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(MERCHANT_KEY, paytmParams, paytmChecksum);// If isValidChecksum is false, then checksum is not valid
			if(isValidChecksum){
				paymentResponse.setCheckSumStatus(true);
				return;
			}else {
				paymentResponse.setReasonForFail("Checksum validation failed");
				paymentResponse.setMessage("Data mismatch!");
				return;
			}
		} catch (Exception e) {
			paymentResponse.setReasonForFail("Checksum validation failed");
			paymentResponse.setMessage("Data mismatch!");
			logger.info("exception : "+e.getMessage());
		}
		return;
	}

}
