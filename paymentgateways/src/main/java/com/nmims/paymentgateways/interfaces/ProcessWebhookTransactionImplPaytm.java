package com.nmims.paymentgateways.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.strategies.ProcessTransactionPaytmStrategies;

@Service(value = "processWebhookInterfacePayTm")
public class ProcessWebhookTransactionImplPaytm implements ProcessWebhookInterface{
	
	@Autowired
	private ProcessTransactionPaytmStrategies processTransactionPaytmStrategies;

	@Override
	public String processWebhooktransaction(PayloadWrapper wrapper, WebhookEnum implementation) {
		
		return processTransactionPaytmStrategies.processWebhookTransaction(wrapper, implementation);
	}

}
