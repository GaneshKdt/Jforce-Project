package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.factory.FeeReceiptFactory;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.interfaces.FeeReceiptInterface;
import com.nmims.stratergies.SRFeeResponseStratergyInterface;

@Service("sRFeeResponse")
public class SRFeeResponse implements SRFeeResponseStratergyInterface{
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private MailSender mailer;
	
	@Value("${FEE_RECEIPT_PATH}") 
	private String FEE_RECEIPT_PATH;
	
	@Autowired
	FeeReceiptFactory feeReceiptFactory ;
	
	@Autowired 
	CreatePDF pdfHelper;


		@Override
		public ServiceRequestStudentPortal srFeeResponse(ServiceRequestStudentPortal sr, StudentStudentPortalBean student, HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
				Model m) throws Exception {
			// TODO Auto-generated method stub
			String msg;

			//System.out.println("=========> request.getParameter(\"CHECKSUMHASH\") " + request.getParameter("CHECKSUMHASH") + " request.getParameter(\"MID\") " + request.getParameter("MID") + " request.getParameter(\"RESPMSG\") : " + request.getParameter("RESPMSG") + " | request.getParameter(\"RESPCODE\")" + request.getParameter("RESPCODE"));
			//System.out.println(request.getParameter("status"));
		
			try {
				//PaymentHelper payment = new PaymentHelper();
				if(sr != null) {
					if(sr.getTrackId().equalsIgnoreCase(sr.getMerchantRefNo())) {
						serviceRequestDao.insertOnlineTransaction(sr);
						//System.out.println("Saved transaction details");
						msg = "success";
					}else {
						msg=  "Error: Invalid trackId found";
					}
					
				}else {
					msg = "Error: Invalid Payment Gateway Selected";
				}

			} catch (Exception e) {
				//e.printStackTrace();
				msg = "Error: " + e.getMessage();
			}
			
			//TODO add error handling
			if(msg!="success") {
				 
				if("MobileApp".equalsIgnoreCase(sr.getDevice())) {
					sr.setReturn_url(SERVER_PATH + "studentportal/m/paymentResponse?status=error&message="+msg);
					return sr;
				}else {
					if(StringUtils.equals("119",student.getConsumerProgramStructureId()) || "126".equals(student.getConsumerProgramStructureId()) ) {
						sr.setReturn_url(SERVER_PATH   + "ssoservices/mbax/sRPaymentSuccess?status=error&message="+msg);
						return sr;
					}else {
						sr.setReturn_url(SERVER_PATH + "timeline/sRPaymentSuccess?status=error&message="+msg);
						return sr;
					}
				}
				 
			}
			//String sapid = (String) request.getSession().getAttribute("userId");
			String trackId = (String) request.getSession().getAttribute("trackId");
			
			//ServiceRequest paymentResponseBean = (ServiceRequest) request.getSession().getAttribute("paymentResponseBean");
			//ArrayList<ServiceRequest> listOfServiceRequestInserted = null;
			///listOfServiceRequestInserted = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
			
			//System.out.println("=========>>>>>>>>>>>0<<<<<<<<<<<=========");
			//System.out.println("=========>>>>>>>>>>>1<<<<<<<<<<<=========");
			//System.out.println(sapid);
			//System.out.println("=========>>>>>>>>>>>2<<<<<<<<<<<=========");
			//System.out.println(trackId);
			//System.out.println("=========>>>>>>>>>>>3<<<<<<<<<<<=========");
			//System.out.println(sr);
			//System.out.println("=========>>>>>>>>>>>4<<<<<<<<<<<=========");
			//System.out.println(paymentResponseBean);
			//System.out.println("=========>>>>>>>>>>>5<<<<<<<<<<<=========");
			//System.out.println(listOfServiceRequestInserted);
			//System.out.println("=========>>>>>>>>>>>6<<<<<<<<<<<=========");
			//System.out.println("=========>>>>>>>>>>>7<<<<<<<<<<<=========");
			
			String errorMessage = paymentHelper.checkErrorInPayment(request);
			//System.out.println("error msg==>>????????????");
			//System.out.println(errorMessage);
			if (errorMessage != null) {
				//System.out.println("============Eror========"+errorMessage);
			//	return sendBackToServiceRequestPage(request, errorMessage, m);
				if("MobileApp".equalsIgnoreCase(sr.getDevice())) {
					sr.setReturn_url("/m/paymentResponse?status=error&message="+errorMessage);
					return sr;
				}else {
					if(StringUtils.equals("119",student.getConsumerProgramStructureId()) || "126".equals(student.getConsumerProgramStructureId()) ) {
						sr.setReturn_url(SERVER_PATH + "ssoservices/mbax/sRPaymentSuccess?status=error&message="+errorMessage+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId());
						 return sr;
					}else {
						sr.setReturn_url(SERVER_PATH + "timeline/sRPaymentSuccess?status=error&message="+errorMessage+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId());
						 return sr;
					}
				}
			} else {
				//System.out.println("============success========");
				msaveSuccessfulTransaction(request, respnse, model);
			}
			
			if(request.getAttribute("success")=="true") {
				//System.out.println(request.getAttribute("successMessage"));
				if("MobileApp".equalsIgnoreCase(sr.getDevice())) {
					sr.setReturn_url(SERVER_PATH + "studentportal/m/paymentResponse?status=success&message=Successfully payment completed");
					return sr;
				}else {
					if(StringUtils.equals("119",student.getConsumerProgramStructureId()) || "126".equals(student.getConsumerProgramStructureId()) ) {
						sr.setReturn_url(SERVER_PATH + "ssoservices/mbax/sRPaymentSuccess?status=success&message="+request.getAttribute("successMessage")+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId());
						 return sr ;
						
					}else {
						 sr.setReturn_url(SERVER_PATH + "timeline/sRPaymentSuccess?status=success&message="+request.getAttribute("successMessage")+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId()); 
						 return sr ;

					}
				}
//				mv.addObject("responseType","success");
//				mv.addObject("response",request.getAttribute("successMessage"));
//				return mv;		
			}else {
				if("MobileApp".equalsIgnoreCase(sr.getDevice())) {
					
					sr.setReturn_url("redirect:" + "/m/paymentResponse?status=error&message="+errorMessage);
					return sr;
				}else {
					if(StringUtils.equals("119",student.getConsumerProgramStructureId()) || "126".equals(student.getConsumerProgramStructureId()) ) {
						
						sr.setReturn_url(SERVER_PATH + "ssoservices/mbax/sRPaymentSuccess?status=error&message="+errorMessage+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId());
						
						return sr ;

					}else {
						
						sr.setReturn_url(SERVER_PATH + "timeline/sRPaymentSuccess?status=error&message="+errorMessage+"&trackId="+trackId + "&reqType=" + sr.getServiceRequestType() + "&description="+ sr.getDescription() + "&error="+ sr.getError() +  "&id=" + sr.getId());
						return sr; 
					}
				}
//				mv.addObject("responseType","error");
//				mv.addObject("response",errorMessage);
//				return mv;			
			}
		}
	
	

public void msaveSuccessfulTransaction(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
	 
	  
	String sapid = (String) request.getSession().getAttribute("userId");
	String trackId = (String) request.getSession().getAttribute("trackId");
	ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("sr");
	//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
	//System.out.println(sr.toString());
	ServiceRequestStudentPortal paymentResponseBean = (ServiceRequestStudentPortal) request.getSession().getAttribute("paymentResponseBean");
	ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = null;
	
	//System.out.println("--->>"+sr.getServiceRequestType());
	if("Issuance of Marksheet".equalsIgnoreCase(sr.getServiceRequestType()) || "Issuance of Gradesheet".equalsIgnoreCase(sr.getServiceRequestType())){
		//System.out.println("inside if");
		listOfServiceRequestInserted = serviceRequestDao.findByTrackId(trackId);
		//System.out.println("listOfServiceRequestInserted----"+listOfServiceRequestInserted.toString());
		//System.out.println(listOfServiceRequestInserted.size());
		//listOfServiceRequestInserted = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
		
		
		if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0){
			//System.out.println("inside if1");
			StringBuilder serviceRequestIdList = new StringBuilder();
			//System.out.println("inside if2");
			StringBuilder descriptionList = new StringBuilder();
			//System.out.println("inside if3");
			for(ServiceRequestStudentPortal bean : listOfServiceRequestInserted){
				//System.out.println("inside if4");
				serviceRequestIdList.append(bean.getId()).append(",");
				descriptionList.append(bean.getDescription()).append(",");
			}
			//System.out.println("inside if5");
			sr.setSrIdList(serviceRequestIdList.toString().substring(0,serviceRequestIdList.toString().length()-1));
			//System.out.println("inside if6");
			sr.setDescriptionList(descriptionList.toString().substring(0, descriptionList.toString().length()-1));
		}
			
	} else {
		//System.out.println("inside else-----------");
	}
	
	//System.out.println("sr------->>>>>"+sr);
	//modelnView.addObject("sr", sr);
	//If else loop for storing old assignment/written score depending on the reval subjects opted for 
	if("Assignment Revaluation".equals(sr.getServiceRequestType())){
		ArrayList<StudentMarksBean> studentAssignmentMarksList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentAssignmentMarksList");
		serviceRequestDao.updateStudentAssignmentMarks(studentAssignmentMarksList);
	}else if("Revaluation of Term End Exam Marks".equals(sr.getServiceRequestType())){
		ArrayList<StudentMarksBean> studentWrittenScoreMarkList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("studentWrittenScoreMarks");
		serviceRequestDao.updateStudentWrittenMarks(studentWrittenScoreMarkList);
	}
	try { 
		paymentResponseBean.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);

		serviceRequestDao.updateSRTransactionDetails(paymentResponseBean);
		
		ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(paymentResponseBean.getId());
		serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
		
		//////////////////////////////////////////////////////////////
		// logic for fee receipt start
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList= new ArrayList<ServiceRequestStudentPortal>();
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal"); 
		if(sr.getServiceRequestType().equalsIgnoreCase("Issuance of Marksheet") || 
				sr.getServiceRequestType().equalsIgnoreCase("Issuance of Gradesheet")	) {
		        marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequestStudentPortal>) request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
				System.out.println(marksheetDetailAndAmountToBePaidList);
				 
		}
		String courierAmount = (String) request.getSession().getAttribute("courierAmount"); 
		System.out.println("courierAmount:"+courierAmount);
		//added for fee receipt
		
		//old logic
		//pdfHelper.createSrFeeReceipt(FEE_RECEIPT_PATH,sr,student,courierAmount,marksheetDetailAndAmountToBePaidList);
		
		//new logic
		FeeReceiptInterface feeReceipt = feeReceiptFactory.getSRType(FeeReceiptFactory.ProductType.valueOf(sr.getServiceRequestType().trim().toUpperCase().replaceAll("-","").replaceAll(" ", "").replaceAll("\\.", "") )); 
        feeReceipt.createSrFeeReceipt(FEE_RECEIPT_PATH,sr,student,courierAmount,marksheetDetailAndAmountToBePaidList);
		   
		
		// logic for fee receipt end
		//////////////////////////////////////////////////////////////
		
		
		//System.out.println("4845");
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Service Request created successfully");

//		StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
		//System.out.println("4850");
		if(listOfServiceRequestInserted!=null && listOfServiceRequestInserted.size()>0 ){
			for(ServiceRequestStudentPortal serviceBean : listOfServiceRequestInserted){
				handlePostPaymentAction(student, serviceBean);
			}
			//System.out.println(listOfServiceRequestInserted);
			//System.out.println(listOfServiceRequestInserted.size());
			//modelnView.addObject("listOfServiceRequestInserted", listOfServiceRequestInserted);
			//modelnView.addObject("rowCount", listOfServiceRequestInserted.size());
		}else{
			//System.out.println("else============");
			//modelnView.addObject("listOfServiceRequestInserted", null);
			//modelnView.addObject("rowCount",0);
			handlePostPaymentAction(student, sr);
		}
		
		

	} catch (Exception e) {
		//e.printStackTrace();
	}
	//return modelnView;
}

	
private String saveAllTransactionDetails(HttpServletRequest request) {

	try {
		//PaymentHelper payment = new PaymentHelper();
		ServiceRequestStudentPortal bean = paymentHelper.CreateResponseBean(request);
		if(bean != null) {
			if(bean.getTrackId().equalsIgnoreCase(bean.getMerchantRefNo())) {
				serviceRequestDao.insertOnlineTransaction(bean);
				//System.out.println("Saved transaction details");
				return "success";
			}else {
				return "Error: Invalid trackId found";
			}
			
		}else {
			return "Error: Invalid Payment Gateway Selected";
		}

	} catch (Exception e) {
		//e.printStackTrace();
		return "Error: " + e.getMessage();
	}
}

public void handlePostPaymentAction(StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {
	//System.out.println("handlePostPaymentAction started");
	
	String serviceRequestType = sr.getServiceRequestType();
	if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType) || "Issuance of Gradesheet".equals(serviceRequestType) ) {
		saveMarksheetRequestPostPayment(sr);
	} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)) {
		saveFinalCertificateRequestPostPayment(sr);
	} 
		
	mailer.sendSREmail(sr, student);

}


private void saveMarksheetRequestPostPayment( ServiceRequestStudentPortal sr) {
	// This also gets called from ServiceRequestPaymentScheduler via
	// handlePostPaymentAction method
	ArrayList<String> postPaymentData = new ArrayList<String>(
			Arrays.asList(sr.getInformationForPostPayment().split("~")));
	String sapId = postPaymentData.get(0);
	String year = postPaymentData.get(1);
	String month = postPaymentData.get(2);
	String sem = postPaymentData.get(3);

	sr.setYear(year);
	sr.setMonth(month);
	sr.setSem(sem);
	
	serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
	
	
	// how many times
	// same request was
	// made so that next
	// time they can be
	// charged
}
//Steps to be done after payment is done for Certificate
	private void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr) {
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
