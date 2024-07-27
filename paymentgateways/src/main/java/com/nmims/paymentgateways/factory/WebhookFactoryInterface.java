package com.nmims.paymentgateways.factory;

import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.interfaces.ProcessWebhookInterface;

public interface WebhookFactoryInterface {
	public abstract ProcessWebhookInterface getProductType(WebhookEnum type);
}
