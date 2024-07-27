package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.nmims.abstracts.ServiceRequestAbstract;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.MailSender;
import com.nmims.repository.ServiceRequestRepository;

/**
 * A Service Layer which contains methods for the Change in Contact Details Service Request.
 * @author Raynal Dcunha
 */
@Service
public class ChangeContactDetailsSRService extends ServiceRequestAbstract {
	private static final ServiceRequestTypeEnum SERVICE_REQUEST_TYPE = ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS;
	private static final Integer TAT = 3;
	
	private final ServiceRequestRepository serviceRequestRepository;
	private final MailSender mailSender;
	
	private static final Logger logger = LoggerFactory.getLogger(ChangeContactDetailsSRService.class);
	
	@Autowired
	public ChangeContactDetailsSRService(ServiceRequestRepository serviceRequestRepository, MailSender mailSender, AWSHelper awsHelper) {
		super(serviceRequestRepository, awsHelper);
		Objects.requireNonNull(serviceRequestRepository);			//Fail-fast approach, field is guaranteed be non-null.
		Objects.requireNonNull(mailSender);
		this.serviceRequestRepository = serviceRequestRepository;
		this.mailSender = mailSender;
	}
	
	@Override
	public ServiceRequestTypeEnum getServiceRequestType() {
		return SERVICE_REQUEST_TYPE;
	}
	
	/**
	 * Gets a list of Service Request description having the particular type and status as not Closed or Cancelled 
	 * @param sapid - studentNo of the Student
	 * @return List of String containing the Service Request description
	 */
	public List<String> getOpenSrDescriptionList(Long sapid) {
		try {
			return serviceRequestRepository.srDescriptionList(sapid, getServiceRequestType().getValue());
		}
		catch(EmptyResultDataAccessException ex) {
//			ex.printStackTrace();
			logger.error("No Open SR records of Service Request Type: {} found for Student: {}", getServiceRequestType().getValue());
			return new ArrayList<>();
		}
	}
	
	@Override
	public ServiceRequestStudentPortal createServiceRequestBean(Long sapid, ServiceRequestTypeEnum srType, 
																String description, String device) {
		device = DEVICE_MOBILEAPP.equalsIgnoreCase(device) ? DEVICE_MOBILEAPP : DEVICE_WEBAPP;		//Use value from constants to avoid irregular values;
		
		ServiceRequestStudentPortal serviceRequest = new ServiceRequestStudentPortal();
		serviceRequest.setServiceRequestType(srType.getValue());
		serviceRequest.setSapId(String.valueOf(sapid));
		serviceRequest.setAmount(AMOUNT_ZERO);
		serviceRequest.setTranStatus(TRAN_STATUS_FREE);
		serviceRequest.setRequestStatus(REQUEST_STATUS_SUBMITTED);
		serviceRequest.setDescription(description);
		serviceRequest.setCreatedBy(String.valueOf(sapid));
		serviceRequest.setLastModifiedBy(String.valueOf(sapid));
		serviceRequest.setCategory(CATEGORY_ADMISSION);
		serviceRequest.setHasDocuments(HAS_DOCUMENTS_NO);
		serviceRequest.setDevice(device);
		return serviceRequest;
	}

	@Override
	public void sendSuccessfulSrRequestMail(ServiceRequestStudentPortal serviceRequest, String studentSapid,
											String studentFirstName, String studentLastName, String studentEmailId) {
		String additionalConfirmationContent = "Service Request Number " + serviceRequest.getId() + " has been created, "
											+ "and will be closed in " + TAT + " working days subject to compliance on required documentations. <br>";
		
		mailSender.sendSRConfirmationEmail(serviceRequest.getId(), serviceRequest.getServiceRequestType(), serviceRequest.getDescription(), Integer.valueOf(serviceRequest.getAmount()), 
											additionalConfirmationContent, Long.valueOf(studentSapid), studentFirstName, studentLastName, studentEmailId);
	}
	
	@Override
	public void sendSuccessfulSrCreatedMail(ServiceRequestStudentPortal serviceRequest, String studentSapid,
											String studentFirstName, String studentLastName) {
		try {
			InternetAddress[] recipientInternetAddress = convertEmailAddressToInternetAddressArray("ngasce@nmims.edu", "ngasce.admission@nmims.edu", "jforce.solution@gmail.com");
			
			mailSender.sendSrCreatedMail(serviceRequest.getId(), serviceRequest.getServiceRequestType(), serviceRequest.getDescription(), TAT, 
										recipientInternetAddress, "", Long.valueOf(studentSapid), studentFirstName, studentLastName);
		}
		catch(AddressException ex) {
			logger.error("Unable to send the Service Request creation mail. Address Exception thrown while converting the recipient email address to Internet Address!");
		}
	}
}
