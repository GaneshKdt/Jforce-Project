package com.nmims.interfaces;

import java.util.HashMap;

import com.nmims.beans.StudentStudentPortalBean;

public interface IdCardServiceInterface {
	
	public HashMap<String, String> createIdCardPdf(StudentStudentPortalBean student);
	public String createIdCardByBatchJob(String enrollmentMonth, String enrollmentYear);
	public String createIdCardBySapId(String sapId);
	public void updateIdCard(StudentStudentPortalBean student);

}
