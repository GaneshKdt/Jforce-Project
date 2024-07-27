package com.nmims.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.nmims.beans.MBAWXPortalExamResultForSubject;
import com.nmims.beans.StudentsTestDetailsStudentPortalBean;
import com.nmims.beans.TimeboundExamBookingBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ISupportService {
	
	public boolean checkStudentUgOrNot(String program,String sem,String month,String year,String sapid);
	
	public void insertUgConsentForm(String optionId,String sapid);
	
	public String getCourseName(String program);
	
	public boolean validateStudentMatrixSalesforceToken(String sapid, String token) throws Exception;
	
	public List<TimeboundExamBookingBean> fetchTimeboundExamBookindRecords(String sapId) throws SQLException;
	
	public List<MBAWXPortalExamResultForSubject> getSubjectWiseStudentMarksRecord(String sapid);
	
	public List<MBAWXPortalExamResultForSubject> getStudentPassFailRecords(String sapid);

	public List<StudentsTestDetailsStudentPortalBean> fetchAllAttemptedIADetails(String sapid) throws SQLException;
	
	public String getToken(String student_No) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException;
}
