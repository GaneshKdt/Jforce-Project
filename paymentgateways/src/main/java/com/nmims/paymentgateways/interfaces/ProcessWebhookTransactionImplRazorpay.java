package com.nmims.paymentgateways.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.strategies.ProcessTransactionRazorpayStrategies;

@Service(value = "processWebhookInterfaceRazorpay")
public class ProcessWebhookTransactionImplRazorpay implements ProcessWebhookInterface {

	@Autowired
	private ProcessTransactionRazorpayStrategies processTransactionRazorpayStrategies;

	@Override
	public String processWebhooktransaction(PayloadWrapper wrapper, WebhookEnum implementation) {
		return processTransactionRazorpayStrategies.processWebhookTransaction(wrapper, implementation);
	}

}
