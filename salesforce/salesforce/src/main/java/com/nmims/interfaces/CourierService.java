package com.nmims.interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.DispatchedDocumentMergeResponseBean;
import com.nmims.beans.FedExMergerBean;
import com.nmims.helpers.DelhiveryManager;
import com.sforce.ws.ConnectionException;

public interface CourierService {

	public ResponseEntity<String> checkPincode(String pincode,String shippingMode)throws IOException; 
	public ResponseEntity<String> getTrackingId(HttpServletRequest request) throws IOException;
	public ResponseEntity<String> getBulkTrackingId(HttpServletRequest request) throws IOException;
	public ResponseEntity<String> trackOrder(String trackingId,String shippingMode) throws IOException;
	public ResponseEntity<String> editOrder() throws IOException;
	public ResponseEntity<String> cancelOrder() throws IOException;
	public ResponseEntity<String> pickupRequestCreation() throws IOException;
	public String orderCreationFromDelhivery(String dispatchOrderId,int retryCount);
	public String orderCreation(String dispatchOrderId);
	public ResponseEntity<String> shippingChargesCalculation(String mode,String weight,String origin_pin,String destination_pin,String status) throws IOException;
	public ResponseEntity<String> slipCreation(String trackingId,String shippingMode) throws IOException;
	public String documentsMergeForDelhivery(HttpServletResponse response,String commaSepratedIds,int retryCount);
	public String pdfCreationForDelhivery(String orderIds);
	public DispatchedDocumentMergeResponseBean dispatchOrderDocumnetMerge(List<FedExMergerBean> bean);
	public DispatchedDocumentMergeResponseBean createFileFromExcelRecords(MultipartFile file);
	public void sendDeliveredInitiatedSMS(String mobileNumber, String message);
}
