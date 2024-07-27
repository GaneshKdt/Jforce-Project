package com.nmims.enums;

public enum StudentPaymentStage {	
	REGISTRATION_DONE("Registration Done"),
	PENDING_PAYMENT("Pending Payment"),
	PAYMENT_MADE("Payment Made"),
	REGISTRATION_FEE_DISAPPROVED_BY_FINANCE("Registration Fee Disapproved By Finance"),
	CLOSED_LOST("Closed Lost"),
	EMERSON_STUDENT("Emerson Student"),
	CLOSED_WON("Closed Won"),
	CLOSED("Closed"),
	CLOSED_WON_RE_REGISTRATION_PENDING("Closed Won - Re-registration Pending"),
	DE_REGISTERED("De-Registered"),
	RE_REGISTRATION_PENDING("Re-Registration Pending"),
	PENDING_PAYMENT_LOAN("Pending Payment-Loan");


	private StudentPaymentStage(String studentPaymentStage) {
		this.studentPaymentStage = studentPaymentStage;
	}	
	
	private final String studentPaymentStage;
	
	public String getStudentPaymentStage() {
		return studentPaymentStage;
	}
}
