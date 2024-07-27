package com.nmims.beans;

import java.io.Serializable;

public class PaymentGatewayConstants  implements Serializable{

	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_MADE = "Payment Made";
	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_APPROVED = "Payment Approved";
	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_DISAPPROVED = "Payment Disapproved";
	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_TRANSACTION_FAILED = "Transaction Failed";
	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_INITIATED = "Payment Initiated";
	public static final String PAYMENT_SALESFORCE_STATUS_PAYMENT_STARTED = "Went to SFDC";

	public static final String PAYMENT_SALESFORCE_STATUS_ERROR = "Portal Error";
	public static final String PAYMENT_SALESFORCE_STATUS_INITIATION_FAILED = "Transaction Initiation Failed";

	public static final String PAYMENT_SOURCE_PORTAL_WEB = "Payment Gateway - Web";
	public static final String PAYMENT_SOURCE_PORTAL = "Payment Gateway";
	public static final String PAYMENT_SOURCE_PORTAL_MOBILE = "Payment Gateway - Mobile";
	public static final String PAYMENT_SOURCE_SFDC = "Salesforce";
	public static final String PAYMENT_SOURCE_SCHEDULER = "Approved Payments Scheduler";
	public static final String PAYMENT_SOURCE_API = "API Call";
	
	public static final String LEAD_MERCHANT_ID = "Lead";
	
}
