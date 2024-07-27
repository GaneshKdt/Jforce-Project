package com.nmims.paymentgateways.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.interfaces.ProcessWebhookInterface;

@Component
public class WebhookFactoryInterfaceImpl implements WebhookFactoryInterface {

//	@Autowired
//	@Qualifier(value = "processWebhookInterfacePayTm")
//	private ProcessWebhookInterface processWebhookInterfacePayTm;
//
//	@Autowired
//	@Qualifier(value = "processWebhookInterfaceBilldesk")
//	private ProcessWebhookInterface processWebhookInterfaceBilldesk;
//
//	@Autowired
//	@Qualifier(value = "processWebhookInterfaceRazorpay")
//	private ProcessWebhookInterface processWebhookInterfaceRazorpay;

	@Autowired
	private Map<String, ProcessWebhookInterface> productMap;

	@Override
	public ProcessWebhookInterface getProductType(WebhookEnum type) {


//		switch (type) {
//		case PAYTM:
//			processWebhookInterface = processWebhookInterfacePayTm;
//
//		case RAZORPAY:
//			processWebhookInterface = processWebhookInterfaceRazorpay;
//
//		case BILLDESK:
//			processWebhookInterface = processWebhookInterfaceBilldesk;
//		}
//		;
		return productMap.get(type.getValue());
	}

}
