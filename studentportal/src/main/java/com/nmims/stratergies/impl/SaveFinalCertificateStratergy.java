package com.nmims.stratergies.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.MailSender;
import com.nmims.stratergies.SaveFinalCertificateInterface;


@Service("saveFinalCertificateStratergy")
public class SaveFinalCertificateStratergy implements SaveFinalCertificateInterface {

	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired 
	private MailSender mailer;
	
	@Override
	public ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal sr)
			throws Exception {
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();
				sr.setIssued("N");
				sr.setModeOfDispatch("LC");
				////System.out.println("Name Of Certifcate Document-->" + nameOnCertificateDoc.getOriginalFilename());
				////System.out.println("Address = " + sr.getPostalAddress());
				String sapid = sr.getSapId();
				StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
				sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
				sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
				sr.setCategory("Exam");
				sr.setSapId(sapid);
				sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
				sr.setCreatedBy(sapid);
				sr.setLastModifiedBy(sapid);
				sr.setSrAttribute("");
				sr.setMonth(new SimpleDateFormat("MMM").format(cal.getTime()));
				sr.setYear(new SimpleDateFormat("YYYY").format(cal.getTime()));
				ArrayList<ServiceRequestStudentPortal> listOfServiceRequest = new ArrayList<ServiceRequestStudentPortal>();
				listOfServiceRequest.add(sr);
				serviceRequestDao.insertServiceRequest(sr);
				serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
				
				mailer.sendSREmail(sr, student);
				
				
				return sr;
	}
}
