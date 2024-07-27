package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.controllers.HomeController;
import com.nmims.controllers.ServiceRequestController;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.factory.CertificateFactory;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.ServiceRequestService;
import com.nmims.stratergies.SaveFinalCertificateSRPostPaymentInterface;

@Service("saveFinalCertificateSRPostPayment")
public class SaveFinalCertificateSRPostPayment  implements SaveFinalCertificateSRPostPaymentInterface{
	
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	HomeController homeController;
	
	@Autowired
	CertificateFactory certificateFactory;
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceRequestController.class);
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;


	private static final String HttpServletRequest = null;
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	@Autowired
	ServiceRequestService servReqServ;
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${V3URL}") 
	private String V3URL;
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	@Value("${SR_RETURN_URL_MOBILE}")
	private String SR_RETURN_URL_MOBILE;
	@Value("${ADHOC_PAYMENT_RETURN_URL}") // AdhocpaymentReturn Url
	private String ADHOC_PAYMENT_RETURN_URL;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	
	
	@Value("${PENDING_PAYMENT_RETURN_URL}") // Pending payment URL
	private String PENDING_PAYMENT_RETURN_URL;
	
	
	@Value("${SERVICE_TAX_RULE}")
	private String SERVICE_TAX_RULE; 

	
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;

	
	private final int REVALUATION_FEE_PER_SUBJECT = 1000;
	private final int DUPLICATE_CERTIFICATE_FEE = 1000;
	private final int PHOTOCOPY_FEE_PER_SUBJECT = 500;
	private final int SECOND_MARKSHEET_FEE_PER_SUBJECT = 500;
	private final int SECOND_DIPLOMA_FEE = 1000;
	private final int TRANSCRIPT_FEE = 1000;
	private final int EXTRA_TRANSCRIPT_FEE = 300;
	private final long MAX_FILE_SIZE = 5242880;
	
	//flag to check data inserted into serviceRequest or not
	private boolean ServiceRequestFlag = false;

	ArrayList<String> requestTypes = new ArrayList<>();

	private ArrayList<String> paymentTypeList =new ArrayList<String>(
			Arrays.asList("Exam Registration","PCP Booking","Service Request"));
	
	private ArrayList<String> refundPaymentTypeList =new ArrayList<String>(
			Arrays.asList("Exam Registration","Service Request","PCP Booking","Assignment Fees", "Exam Registration MBA - WX", "Exam Registration MBA - X"));
	
	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019","2020"));

	@Override
	public void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		//Steps to be done after payment is done for Certificate
			// This also gets called from ServiceRequestPaymentScheduler via
			// handlePostPaymentAction method
			serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
			// how many times
			// same request was
			// made so that next
			// time they can be
			// charged
		}
}
