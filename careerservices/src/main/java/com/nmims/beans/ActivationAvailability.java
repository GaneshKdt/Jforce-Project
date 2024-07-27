package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class ActivationAvailability  implements Serializable{

	private Date availablilityDate;
	private int additionalAvailabile;
	public Date getAvailablilityDate() {
		return availablilityDate;
	}
	public void setAvailablilityDate(Date availablilityDate) {
		this.availablilityDate = availablilityDate;
	}
	public int getAdditionalAvailabile() {
		return additionalAvailabile;
	}
	public void setAdditionalAvailabile(int additionalAvailabile) {
		this.additionalAvailabile = additionalAvailabile;
	}
	
}
