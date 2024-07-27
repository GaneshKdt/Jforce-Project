package com.nmims.paymentgateways.interfaces;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.enums.WebhookEnum;

public interface ProcessWebhookInterface {
	String processWebhooktransaction(PayloadWrapper wrapper, WebhookEnum implementation);
}
