package com.nmims.paymentgateways.bean;

public class PaymentOptionsBean {

	private String name;
	private String id;
	private String image;
	private String exambookingActive;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExambookingActive() {
		return exambookingActive;
	}

	public void setExambookingActive(String exambookingActive) {
		this.exambookingActive = exambookingActive;
	}

	@Override
	public String toString() {
		return "PaymentOptionsBean [name=" + name + ", id=" + id + ", image=" + image + ", exambookingActive="
				+ exambookingActive + "]";
	}

}
