package com.nmims.services;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EmailSmsStudentBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.SubjectCodeBatchBean;
import com.nmims.daos.EmailMessageDAO;
import com.nmims.daos.PortalDao;

@Service("emailSMSService")
public class EmailSMSServiceImpl implements IEmailSMSService {
	@Autowired
	PortalDao portalDAO;
	
	@Autowired
	EmailMessageDAO emailDao;
	
	/*
	 *@param	sapId 
	 *@return	List<MailBean> 	:returning all archived mails communications of particular user
	 *@throws	Exception 		: If any exception occurs it will throws to caller
	 *@date 	Feb 13, 2021 */
	@Override
	@Transactional
	public List<MailStudentPortalBean> fetchArchivedEmailCommunication(String sapId) throws Exception {
		List<MailStudentPortalBean> emailCommunicationList=null;
		
		//use portalDAO and get all archived mails communications
		emailCommunicationList= portalDAO.getArchivedEmailCommunication(sapId);
		
		//return archived mails list
		return emailCommunicationList;
	}
	
	/*
	 *@param	mailTemplateId 
	 *@return	MailBean 	:returning a archived mail communication details
	 *@throws	Exception 	: If any exception occurs it will throws to caller
	 *@date 	Feb 15, 2021 */
	@Override
	public MailStudentPortalBean fetchSingleArchiveEmail(String mailTemplateId) throws Exception {
		MailStudentPortalBean mailBean=null;
		
		//Use portalDAO and get single mail details based on mailTempleteId
		mailBean=portalDAO.getSingleArchiveMail(mailTemplateId);
		
		//return bean having mail details
		return mailBean;
	}
	
	@Override
	public ArrayList<StudentStudentPortalBean> getStudentsListByCriteria(EmailSmsStudentBean student) throws Exception {
		ArrayList<StudentStudentPortalBean> studentsList = new ArrayList<StudentStudentPortalBean>();
		if(student.getUploadtype().equals("subjecttype"))
		{
			ArrayList<String> sapids =emailDao.getSapidsBySubjectCodeList(student.getSubjectCodeId(),student.getAcadMonth(),student.getAcadYear());
		
			if(sapids.size() > 0 ) {
			studentsList= emailDao.getStudentListsBySapids(student.getEnrollmentMonth(),student.getEnrollmentYear(),student.getPrgmStructApplicable(),sapids);
			}
		}else if(student.getUploadtype().equals("batchtype"))
		{
			studentsList= emailDao.getStudentListsBytimeBoundIds(student.getEnrollmentMonth(),student.getEnrollmentYear(),student.getPrgmStructApplicable(),student.getTimeboundId());
		}else if(student.getUploadtype().equals("programtype")){
			studentsList =  portalDAO.getStudentsListByCriteria(student.getEnrollmentMonth(),student.getEnrollmentYear(),student.getAcadMonth(),student.getAcadYear(),student.getSem(),student.getPrgmStructApplicable(),student.getProgram());
		}
		
		ArrayList<StudentStudentPortalBean> studentsListForSMS = new ArrayList<StudentStudentPortalBean>();
		
		ArrayList<String> validStudentsList = portalDAO.getAllValidStudents();
		for (StudentStudentPortalBean studentBean : studentsList) {
			if(validStudentsList.contains(studentBean.getSapid())){
				studentsListForSMS.add(studentBean);
			}
		}
	
		return studentsListForSMS;
	}

	@Override
	public List<SubjectCodeBatchBean> getSubjectcodeLists() throws Exception {
		// TODO Auto-generated method stub
		return 	 emailDao.getSubjectcodeLists();
	}

	@Override
	public List<SubjectCodeBatchBean> getBatchDetails() throws Exception {
		// TODO Auto-generated method stub
		return  emailDao.getBatchDetails();
	}

}
