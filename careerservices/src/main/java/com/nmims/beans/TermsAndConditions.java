package com.nmims.beans;

import java.io.Serializable;

public class TermsAndConditions implements Serializable {
	
	private String general = "<div style='color: black;'>"
			+ "<ul style='padding: 5px'>"
				+ "<li>"
					+ "Students have to disclose the desired information in all earnestness. "
					+ "A student will have to submit all relevant documentary proofs. In case of "
					+ "any information falsified or omitted, will lead to cancellation of the service "
					+ "with no refund of fees paid."
				+ "</li>"
				+ "<li>"
					+ "Once a student chooses a Career Service program, he/she cannot change the program. "
					+ "However, a student can additionally enroll for another program by paying applicable fee."
				+ "</li>"
				+ "<li>"
					+ "In case a student fails to appear for a scheduled session/activity, it shall be considered"
					+ " as completed and will not be repeated."
				+ "</li>"
				+ "<li>"
					+ "In case the student wants to discontinue from the program, he/she needs to inform NMIMS "
					+ "in all earnestness."
				+ "</li>"
			+ "</ul>"
			+ "</div>";
	private String refundPolicy = "<div style='color: black;'>"
			+ "<ul style='padding: 5px'>"
				+ "<li>"
					+ "Students can claim refund within 10 days of the payment made for the program which s/he has enrolled for"
				+ "</li>"
				+ "<li>"
					+ "Administrative Charges of Rs.1500 will be deducted"
				+ "</li>"
			+ "</ul>"
			+ "</div>";
	public String getGeneral() {
		return "<div style='color: black;'>" + general + "</div>";
	}
	public void setGeneral(String general) {
		this.general = general;
	}
	public String getRefundPolicy() {
		return  refundPolicy ;
	}
	public void setRefundPolicy(String refundPolicy) {
		this.refundPolicy = refundPolicy;
	}
}
