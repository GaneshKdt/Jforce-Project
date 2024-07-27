package com.nmims.paymentgateways.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.strategies.ProcessTransactionBilldeskStrategies;
import com.nmims.paymentgateways.strategies.ProcessTransactionPaytmStrategies;
import com.nmims.paymentgateways.strategies.ProcessTransactionRazorpayStrategies;

@Service(value = "processWebhookInterfaceBilldesk")
public class ProcessWebhookTransactionImplBilldesk implements ProcessWebhookInterface {

	@Autowired
	private ProcessTransactionBilldeskStrategies processTransactionBilldeskStrategies;

	@Override
	public String processWebhooktransaction(PayloadWrapper wrapper, WebhookEnum implementation) {
		// TODO Auto-generated method stub
		return processTransactionBilldeskStrategies.processWebhookTransaction(wrapper, implementation);
	}

}
