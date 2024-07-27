package com.nmims.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class NoSlotBookingDto implements Serializable {
	@NotBlank(message="Student No. cannot be empty")
	private String sapid;
	@NotNull(message="Timebound ID cannot be empty")
	private Long timeboundId;
	@NotBlank(message="Type cannot be empty")
	private String type;
	private String amount;
	@NotBlank(message="Payment option cannot be empty")
	private String paymentOption;
	private String source;
	
	public String getSapid() {
		return sapid;
	}
	
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	
	public Long getTimeboundId() {
		return timeboundId;
	}
	
	public void setTimeboundId(Long timeboundId) {
		this.timeboundId = timeboundId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getPaymentOption() {
		return paymentOption;
	}
	
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public String toString() {
		return "NoSlotBookingDto [sapid=" + sapid + ", timeboundId=" + timeboundId + ", type=" + type + ", amount="
				+ amount + ", paymentOption=" + paymentOption + ", source=" + source + "]";
	}
}
