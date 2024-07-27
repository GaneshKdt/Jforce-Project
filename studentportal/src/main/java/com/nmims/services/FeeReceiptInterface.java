package com.nmims.services;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface FeeReceiptInterface {
	public abstract ServiceRequestStudentPortal createPDF(ServiceRequestStudentPortal sr) throws Exception; 
}
