package com.nmims.beans;


public class ShipmentRequest {

	private ShipperDetails shipper;
	private ReceiverDetails receiver;
	private ProductDetails productDetails;
	public ProductDetails getProductDetails() {
		return productDetails;
	}
	public void setProductDetails(ProductDetails productDetails) {
		this.productDetails = productDetails;
	}
	public ShipperDetails getShipper() {
		return shipper;
	}
	public void setShipper(ShipperDetails shipper) {
		this.shipper = shipper;
	}
	public ReceiverDetails getReceiver() {
		return receiver;
	}
	public void setReceiver(ReceiverDetails receiver) {
		this.receiver = receiver;
	}
}
