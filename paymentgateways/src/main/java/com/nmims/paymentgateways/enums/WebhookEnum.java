package com.nmims.paymentgateways.enums;

/**
 * ENUM to help get implementations throughout the webhook factory lifecycle
 * 
 * @author Swarup Singh Rajpurohit
 * @since Paytm webhook integration Oct 2022
 */
public enum WebhookEnum {
	PAYTM("processWebhookInterfacePayTm"), RAZORPAY("processWebhookInterfaceRazorpay"), PAYTM_WEBHOOK("paytm_webhook"),
	RAZORPAY_WEBHOOK("razorpay_webhook"), BILLDESK("processWebhookInterfaceBilldesk");

	private final String value;

	private WebhookEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
