package com.nmims.services;

import java.util.List;
import java.util.ArrayList;

import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.SubjectCodeBatchBean;
import com.nmims.beans.EmailSmsStudentBean;

public interface IEmailSMSService {
	//To retrieve archived mails communication including current current cycle
	public List<MailStudentPortalBean> fetchArchivedEmailCommunication(String sapId) throws Exception;
	
	//Fetch single archive mail
	public MailStudentPortalBean fetchSingleArchiveEmail(String mailTemplateId) throws Exception;
	
	ArrayList<StudentStudentPortalBean> getStudentsListByCriteria(EmailSmsStudentBean student) throws Exception;
	
	public List<SubjectCodeBatchBean> getSubjectcodeLists() throws Exception;
	
	public List<SubjectCodeBatchBean> getBatchDetails() throws Exception;

}
