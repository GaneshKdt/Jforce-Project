package com.nmims.beans;

public class ProductDetails {
	private String paymentMode;
	private String quantity;
	private String totalAmount;
	private String codAmount;
	private String ProductDescription;
	private String shipmentHeight; 
	private String shipmentLength;
	private String shipmentWidth;
	private String orderDate;
	private String trackingId;
	private String dispatchOrderId;
	private String shippingMode;
	private String statusOfDispatch;
	private String customerPin;
	private String originPin;
	
	
	public String getOriginPin() {
		return originPin;
	}
	public void setOriginPin(String originPin) {
		this.originPin = originPin;
	}
	public String getCustomerPin() {
		return customerPin;
	}
	public void setCustomerPin(String customerPin) {
		this.customerPin = customerPin;
	}
	public String getStatusOfDispatch() {
		return statusOfDispatch;
	}
	public void setStatusOfDispatch(String statusOfDispatch) {
		this.statusOfDispatch = statusOfDispatch;
	}
	public String getShippingMode() {
		return shippingMode;
	}
	public void setShippingMode(String shippingMode) {
		this.shippingMode = shippingMode;
	}
	public String getDispatchOrderId() {
		return dispatchOrderId;
	}
	public void setDispatchOrderId(String dispatchOrderId) {
		this.dispatchOrderId = dispatchOrderId;
	}
	public String getTrackingId() {
		return trackingId;
	}
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCodAmount() {
		return codAmount;
	}
	public void setCodAmount(String codAmount) {
		this.codAmount = codAmount;
	}
	public String getProductDescription() {
		return ProductDescription;
	}
	public void setProductDescription(String productDescription) {
		ProductDescription = productDescription;
	}
	public String getShipmentHeight() {
		return shipmentHeight;
	}
	public void setShipmentHeight(String shipmentHeight) {
		this.shipmentHeight = shipmentHeight;
	}
	public String getShipmentLength() {
		return shipmentLength;
	}
	public void setShipmentLength(String shipmentLength) {
		this.shipmentLength = shipmentLength;
	}
	public String getShipmentWidth() {
		return shipmentWidth;
	}
	public void setShipmentWidth(String shipmentWidth) {
		this.shipmentWidth = shipmentWidth;
	}
	@Override
	public String toString() {
		return "ProductDetails [paymentMode=" + paymentMode + ", quantity=" + quantity + ", totalAmount=" + totalAmount
				+ ", codAmount=" + codAmount + ", ProductDescription=" + ProductDescription + ", shipmentHeight="
				+ shipmentHeight + ", shipmentLength=" + shipmentLength + ", shipmentWidth=" + shipmentWidth
				+ ", orderDate=" + orderDate + ", trackingId=" + trackingId + ", dispatchOrderId=" + dispatchOrderId
				+ ", shippingMode=" + shippingMode + ", statusOfDispatch=" + statusOfDispatch + ", customerPin="
				+ customerPin + ", originPin=" + originPin + "]";
	}
	
	
}
