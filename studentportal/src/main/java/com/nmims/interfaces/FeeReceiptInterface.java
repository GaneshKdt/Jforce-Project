package com.nmims.interfaces;

import java.util.ArrayList;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface FeeReceiptInterface {
	public abstract void createSrFeeReceipt(String FEE_RECEIPT_PATH, ServiceRequestStudentPortal sr, StudentStudentPortalBean student, String courierAmount, ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList) throws Exception; 
}
