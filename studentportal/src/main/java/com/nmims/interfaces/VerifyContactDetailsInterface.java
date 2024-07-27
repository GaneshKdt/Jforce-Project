package com.nmims.interfaces;

import com.nmims.beans.VerifyContactDetailsBean;
import com.nmims.dto.ChangeDetailsSRDto;
/**
 * To verify the contact Details Provided by Student
 * @author shivam sangale
 *
 */
public interface VerifyContactDetailsInterface {
	public VerifyContactDetailsBean verifyDetails(VerifyContactDetailsBean verifyBean);
	
	
	public VerifyContactDetailsBean sendOtpAndInsertIntoTable(VerifyContactDetailsBean verifyBean);
	
	
	
}
