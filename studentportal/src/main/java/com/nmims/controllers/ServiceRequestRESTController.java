package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.EBonafidePDFContentRequestBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.FeedbackBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ServiceRequestCustomPDFContentBean;
import com.nmims.beans.ServiceRequestResponse;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.dto.SpecialNeedStudent;
import com.nmims.dto.SrAdminUpdateDto;
import com.nmims.dto.StudentSrDTO;
import com.nmims.exception.RecordNotExistException;
import com.nmims.factory.CertificateFactory;
import com.nmims.factory.ChangeInSpecializationFactory;
import com.nmims.factory.SemDeregisterFactory;
import com.nmims.factory.TranscriptFactory;
import com.nmims.helpers.PaymentHelper;
import com.nmims.interfaces.CertificateInterface;
import com.nmims.interfaces.ChangeInSpecializationServiceInterface;
import com.nmims.interfaces.SemDeregisterInterface;
import com.nmims.interfaces.TranscriptServiceInterface;
import com.nmims.services.ServiceRequestService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/m")
public class ServiceRequestRESTController {
	@Autowired
	ServiceRequestService servReqServ;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;

	@Autowired
	CertificateFactory certificateFactory;
	
	@Autowired 
	TranscriptFactory transcriptFactory;
	
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	SemDeregisterFactory semDeregisterFactory;
	
	@Autowired 
	ChangeInSpecializationFactory specializationFactory;
	private final int SECOND_MARKSHEET_FEE_PER_SUBJECT = 500;

	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	
	@Autowired
	ApplicationContext act;
	
	ArrayList<String> requestTypes = new ArrayList<>();
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceRequestRESTController.class);
	private static final Logger loggerForFCSR = LoggerFactory.getLogger("service_request");
	private static final Logger loggerForTranscript = LoggerFactory.getLogger("transcript_sr");
	
	
	  @ModelAttribute("requestTypes")
      public ArrayList<String> getRequestTypes(HttpServletRequest request,String sapid) {
               //String sapid = (String) request.getSession().getAttribute("userId"); 
		  		// don't use session inside theses function,because mobile app using these function
               this.requestTypes = serviceRequestDao.getActiveSRTypes();
               this.requestTypes.addAll(serviceRequestDao.getSRTypesForExtendedTimeStudents(sapid)); //added on 6/2/2018 to allow students to raise SR after last date.
               StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
               if (student != null) {
                         if("Jul2017".equals(student.getPrgmStructApplicable())){
                                  requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                                  requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                                  requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                         } else if (Arrays.asList("ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM").contains(student.getProgram())) {
                             requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                             requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                             requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                         } else if ("Online".equals(student.getExamMode())) {
                                  //requestTypes.remove(ServiceRequest.TEE_REVALUATION);
                                   requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
                                   requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
                                   if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {

                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE);
                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET);
                                       requestTypes.remove(ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT);
                                   }
                         } 
                         else
                         {         requestTypes.remove(ServiceRequestStudentPortal.TEE_REVALUATION);
                                  // requestTypes.remove(ServiceRequest.ASSIGNMENT_REVALUATION);
                         }
               }
               
               return this.requestTypes;
      }



	   @PostMapping(path = "/passedSemList", consumes = "application/json")

			public ResponseEntity<ArrayList<PassFailBean>> MpassedSemList(HttpServletRequest request,
					@RequestBody StudentStudentPortalBean studentBean
					)throws Exception {
				
				ArrayList<PassFailBean> yearMonthList= new ArrayList<>();
				try {
					StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(studentBean.getSapid()); 
					yearMonthList = servReqServ.getPassedYearMonthList(student);
				} catch (Exception e) {
					//e.printStackTrace();
				} 
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				
				return new ResponseEntity<ArrayList<PassFailBean>>(yearMonthList,headers, HttpStatus.OK);
	   }
	   

		@PostMapping(path = "/checkMarksheetHistory")
		public ResponseEntity<ServiceRequestStudentPortal> McheckMarksheetHistory(HttpServletRequest request,
				@RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String serviceRequestType = sr.getServiceRequestType();
			if(serviceRequestType.equalsIgnoreCase("Issuance of Gradesheet")) {
				sr.setError("Could not Raise this Service Request");
				return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			}
			ServiceRequestStudentPortal response =  servReqServ.checkMarksheetHistory(sr,request);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		 @PostMapping(path = "/confirmMarksheetRequest", consumes = "application/json")
			public ResponseEntity<ServiceRequestStudentPortal> MconfirmMarksheetRequest(HttpServletRequest request,
						@RequestBody ServiceRequestStudentPortal sr)throws Exception {
					HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Type", "application/json");
					ServiceRequestStudentPortal response =  servReqServ.confirmMarksheetRequest(sr);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			} 
		

		@PostMapping(path = "/saveMarksheetAndPayment", headers = "content-type=multipart/form-data")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveMarksheetAndPayment(HttpServletRequest request,
				 ServiceRequestStudentPortal sr,  MultipartFile firCopy,
				 MultipartFile indemnityBond)throws Exception {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			 JSONArray jsonArray = new JSONArray(sr.getMarksheetDetailAndAmountToBePaidListAsString());
			 
			   List<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidLists = new ArrayList<>();
			    for (int i = 0; i < jsonArray.length(); i++) {
			        ServiceRequestStudentPortal marksheetDetailAndAmountToBePaidList = new Gson().fromJson(jsonArray.get(i).toString(), ServiceRequestStudentPortal.class);
			        marksheetDetailAndAmountToBePaidLists.add(marksheetDetailAndAmountToBePaidList);
			    }
			    sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidLists);
			    
			String sapid = sr.getSapId();
			String a = sr.getMarksheetDetailAndAmountToBePaidListAsString();
			//StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
			
			
			//added by tushar
			//amount mismatch validation start
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
			//boolean isCertificate = isStudentOfCertificate(student.getProgram());
			boolean isCertificate = student.isCertificateStudent();
			
			ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
			
			boolean isMisMatch = false;
			for(int i=0; i<marksheetDetailAndAmountToBePaidList.size();i++) {
				if(!marksheetDetailAndAmountToBePaidList.get(i).getAmount().equals(sr.getMarksheetDetailAndAmountToBePaidList().get(i).getAmount()) ) {
					//sr.setId(service.getId());
					isMisMatch = true;
				}
			}
			if(isMisMatch) {
				sr.setId((long) 0);
				sr.setErrorMessage("Invalid Amount, Please try again after some time.");
				String paymentUrl = "/m/paymentResponse?status=error&message=Invalid Amount, Please try again after some time.";
				sr.setPaymentUrl(paymentUrl);
				sr.setFirCopy(null);
				sr.setIndemnityBond(null);
				return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			}else {
				ServiceRequestStudentPortal service =  servReqServ.saveMarksheetAndPayment(sr,firCopy,indemnityBond);
				
				sr.setId(service.getId());
				String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
				service.setPaymentUrl(paymentUrl);
				sr.setPaymentUrl(paymentUrl);
				//String requestId = service.getTrackId();
				sr.setFirCopy(null);
				sr.setIndemnityBond(null);
				return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			}
		}
		

		@PostMapping(path = "/saveMarksheetAndPaymentWithoutFile", consumes="application/json", produces="application/json")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveMarksheetAndPayment2(HttpServletRequest request,
				@RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			    MultipartFile firCopy = null;
			    MultipartFile indemnityBond = null;
			String sapid = sr.getSapId();
			//StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
			
			//added by tushar
			//amount mismatch validation start
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
			//boolean isCertificate = isStudentOfCertificate(student.getProgram());
			boolean isCertificate = student.isCertificateStudent();
			
			ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
			
			boolean isMisMatch = false;
			for(int i=0; i<marksheetDetailAndAmountToBePaidList.size();i++) {
				if(!marksheetDetailAndAmountToBePaidList.get(i).getAmount().equals(sr.getMarksheetDetailAndAmountToBePaidList().get(i).getAmount()) ) {
					//sr.setId(service.getId());
					isMisMatch = true;
				}
			}
			if(isMisMatch) {
				sr.setId((long) 0);
				sr.setErrorMessage("Invalid Amount, Please try again after some time.");
				String paymentUrl = "/m/paymentResponse?status=error&message=Invalid Amount, Please try again after some time.";
				sr.setPaymentUrl(paymentUrl);
				return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			}else {
				ServiceRequestStudentPortal service =  servReqServ.saveMarksheetAndPayment(sr,firCopy,indemnityBond);
				
				
				
				sr.setId(service.getId());
				String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
				service.setPaymentUrl(paymentUrl);
				sr.setPaymentUrl(paymentUrl);
//				service.setMarksheetDetailAndAmountToBePaidList((ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList"));
				return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			}
			//amount mismatch validation end
	
		}
		

	
	
	////////////////////////////////////////////////////////////////////////////////////////
	//mappings for sr issuance of marksheet end
	///////////////////////////////////////////////////////////////////////////////////////
		
		
	////////////////////////////////////////////////////////////////////////////////////////
	//mappings for issuance of transcript start
	///////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		

	@PostMapping(path = "/saveTranscriptRequest" )

	public ResponseEntity<ServiceRequestStudentPortal> MsaveTranscriptRequest(MultipartFile sscMarksheet,
			@RequestBody ServiceRequestStudentPortal sr )throws Exception {
	  
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		
		

		try {
			TranscriptServiceInterface transcript = transcriptFactory.getProductType(TranscriptFactory.ProductType.valueOf(sr.getProductType()));
			sr = transcript.createServiceRequest(sr);	
			String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
			sr.setPaymentUrl(paymentUrl);
			loggerForFCSR.info("saveTranscriptRequest success detail : sapid="+sr.getSapId()+" paymentOptionName="+sr.getPaymentOption()+" serviceRequestId="+sr.getId() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			loggerForFCSR.error("saveTranscriptRequest fail detail : sapid="+sr.getSapId()+" paymentOptionName="+sr.getPaymentOption()+" errorMessage="+e.getMessage() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
			sr.setError(e.getMessage());
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
		}
		
		}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//mappings for issuance of transcript end
	///////////////////////////////////////////////////////////////////////////////////////
	
//	@PostMapping(value = "/saveFinalCertificateRequestWithFile", headers = "content-type=multipart/form-data")
//	public ResponseEntity<ServiceRequest> msaveFinalCertificateRequestWithFile(HttpServletRequest request,
//				ServiceRequest sr,MultipartFile nameOnCertificateDoc)throws Exception {
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		ServiceRequest response = servReqServ.saveFinalCertificateRequest(sr,nameOnCertificateDoc);
//		if(response.getError() !=null) {
//		     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//	       }
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//	}
	

	@PostMapping(path = "/saveFinalCertificateAndPaymentWithfile", headers = "content-type=multipart/form-data" )
	public ResponseEntity<ServiceRequestStudentPortal> MsaveFinalCertificateAndPaymentWithfile(HttpServletRequest request,
			ServiceRequestStudentPortal sr)throws Exception {
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		//System.out.println("indemnityBond "+indemnityBond.toString());
		//System.out.println("firCopy "+firCopy.toString());
		ServiceRequestStudentPortal response = new ServiceRequestStudentPortal();
		try {
		response = servReqServ.saveFinalCertificateAndPayment(sr,sr.getIndemnityBond(),sr.getFirCopy(),sr.getAffidavit());
		
		sr.setId(response.getId());
		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
		response.setPaymentUrl(paymentUrl);
		sr.setPaymentUrl(paymentUrl);
		sr.setFirCopy(null);
		sr.setIndemnityBond(null);
		sr.setAffidavit(null);
		loggerForFCSR.info("saveFinalCertificateAndPaymentWithfile success detail : sapid="+sr.getSapId()+" HusbandName="+sr.getAdditionalInfo1()+" paymentOptionName="+sr.getPaymentOption()+" serviceRequestType="+sr.getServiceRequestType() +" serviceRequestId="+sr.getId() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
		} catch (Exception e) {
			loggerForFCSR.error("saveFinalCertificateAndPaymentWithfile fail detail : sapid="+sr.getSapId()+" paymentOptionName="+sr.getPaymentOption()+" serviceRequestType="+sr.getServiceRequestType() +" errorMessage="+e.getMessage() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
			sr.setError(e.getMessage());
		}
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	}
	

	@PostMapping(path = "/checkFinalCertificateEligibility")
	public ResponseEntity<ServiceRequestStudentPortal> mCheckFinalCertificateEligibility(
			@RequestBody ServiceRequestStudentPortal sr){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType()));
		
		try {
			
			sr = certificate.checkFinalCertificateEligibility(sr);
			loggerForFCSR.info("checkFinalCertificateEligibility success detail : sapid="+sr.getSapId());
			
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		} catch (Exception e) {
			loggerForFCSR.error("checkFinalCertificateEligibility fail detail : sapid="+sr.getSapId()+" Error message "+e.getMessage());
			sr.setError(e.getMessage());
			//e.printStackTrace();
			return  new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		}
	}
	

	@PostMapping(path="checkSemDeregisterEligibility")
	public ResponseEntity<ServiceRequestStudentPortal> mCheckSemDeregisterEligibility(@RequestBody ServiceRequestStudentPortal sr){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SemDeregisterInterface semDeregister = semDeregisterFactory.getProductType(SemDeregisterFactory.ProductType.valueOf(sr.getProductType()));
	
		try {
			sr = semDeregister.checkSemDeregisterEligibility(sr);
			return new ResponseEntity<>(sr,headers,HttpStatus.OK);
		} catch (Exception e)
		{		sr.setError(e.getMessage());
		//e.printStackTrace();
		return new ResponseEntity<ServiceRequestStudentPortal>(sr,headers, HttpStatus.OK);
		}
	}
	

	@PostMapping(path = "/saveFinalCertificateRequest")
	public ResponseEntity<ServiceRequestStudentPortal> msaveFinalCertificateRequest(@RequestBody ServiceRequestStudentPortal sr)throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType()));
		try {
			sr = certificate.saveFinalCertificateRequest(sr);
			return new ResponseEntity<ServiceRequestStudentPortal>(sr,headers, HttpStatus.OK);
		} catch(Exception e) {
			sr.setError(e.getMessage());
			return new ResponseEntity<ServiceRequestStudentPortal>(sr, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	       }
	}
	

	
	@PostMapping(path = "/saveFinalCertificateAndPayment" ,headers = "content-type=application/json")
    public ResponseEntity<ServiceRequestStudentPortal> msaveFinalCertificateAndPayment(@RequestBody ServiceRequestStudentPortal sr) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			//System.out.println("call save FC");
			CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType()));
			//System.out.println("get fc interface ");
			sr = certificate.saveFinalCertificateAndPayment(sr);
			//System.out.println("Save FC ");
			sr.setId(sr.getId());
			String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId() + "&productType=" + sr.getProductType();

			sr.setPaymentUrl(paymentUrl);
			loggerForFCSR.info("saveFinalCertificateAndPayment success detail : sapid="+sr.getSapId()+" paymentOptionName="+sr.getPaymentOption()+" serviceRequestType="+sr.getServiceRequestType() +" serviceRequestId="+sr.getId() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
			return new ResponseEntity<ServiceRequestStudentPortal>(sr,headers, HttpStatus.OK);
		} catch (Exception e) { 
			// TODO Auto-generated catch block
			//e.printStackTrace();
			sr.setError(e.getMessage());
			loggerForFCSR.error("saveFinalCertificateAndPayment fail detail : sapid="+sr.getSapId()+" paymentOptionName="+sr.getPaymentOption()+" serviceRequestType="+sr.getServiceRequestType() +" errorMessage="+e.getMessage() + " productType=" + sr.getProductType()+ " amount=" + sr.getAmount());
			return new ResponseEntity<ServiceRequestStudentPortal>(sr, headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	

	@RequestMapping(path = "/proceedToPaymentGatewaySr",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, HttpServletRequest request,String sapId,String serviceRequestId,String paymentOptionName, String productType, HttpServletResponse response) {
		logger.info("Inside  Payment");
		CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(productType));
		try {
			request.getSession().setAttribute("productType",productType);
			return certificate.proceedToPaymentGatewaySr(sr, sapId, serviceRequestId, paymentOptionName, request, response);
		} catch (Exception e) {
			logger.info("Error Found While Payment"+" "+e);
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}

	@PostMapping(path = "/ServiceRequestFee")
	public ResponseEntity<ServiceRequestStudentPortal> mServiceRequestFee(@RequestBody ServiceRequestStudentPortal sr)throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType()));

		try {
			sr = certificate.serviceRequestFee(sr);
			
			return new ResponseEntity<ServiceRequestStudentPortal>(sr,headers, HttpStatus.OK);
		
		} catch(Exception e) {
			sr.setError(e.getMessage());
			return new ResponseEntity<ServiceRequestStudentPortal>(sr, headers, HttpStatus.INTERNAL_SERVER_ERROR);	
		}

	}	
	


	

    @PostMapping(path="/CheckFinalCertificateCount")
    public Integer CheckFinalCertificateCount(@RequestBody ServiceRequestStudentPortal sr) {
    	Integer count = null;
    	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
try {
	count = certificate.checkFinalCertificateCount(sr);
	return count;

}catch(Exception e){
	return count;

    }
    }
    

    @RequestMapping(path="/addCredentialLinkedIn", method= {RequestMethod.POST})
    public ServiceRequestStudentPortal addCredentialLinkedIn(@RequestBody ServiceRequestStudentPortal sr) {
    	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 
    	try {
			sr = certificate.addCredentialLinkedIn(sr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return sr;
    	
    }
    
    
//	private ServiceRequest MfillPaymentParametersInMap( StudentBean student, ServiceRequest sr) {
//
//		String address = student.getAddress();
//
//		if (address == null || address.trim().length() == 0) {
//			address = "Not Available";
//		} else if (address.length() > 200) {
//			address = address.substring(0, 200);
//		}
//		String city = student.getCity();
//		if (city == null || city.trim().length() == 0) {
//			city = "Not Available";
//		}
//
//		String pin = student.getPin();
//		if (pin == null || pin.trim().length() == 0) {
//			pin = "400000";
//		}
//
//		String mobile = student.getMobile();
//		if (mobile == null || mobile.trim().length() == 0) {
//			mobile = "0000000000";
//		}
//
//		String emailId = student.getEmailId();
//		if (emailId == null || emailId.trim().length() == 0) {
//			emailId = "notavailable@email.com";
//		}
//		sr.setChannel("10");
//		sr.setAccount_id(ACCOUNT_ID);
//		sr.setMode("LIVE");
//		sr.setCurrency("INR");
//		sr.setCurrency_code("INR");
//		sr.setReference_no(sr.getTrackId());
//		sr.setDescription(sr.getServiceRequestType() + ":" + student.getSapid());
//		//model.addAttribute("channel", "10");
//		//model.addAttribute("account_id", ACCOUNT_ID);
///*		model.addAttribute("reference_no", sr.getTrackId());
//		model.addAttribute("amount",sr.getAmount());*/
///*		model.addAttribute("mode", "LIVE");
//		model.addAttribute("currency", "INR");
//		model.addAttribute("currency_code", "INR");*/
//		//model.addAttribute("description", sr.getServiceRequestType() + ":" + student.getSapid());// This
//																									// should
//																									// be
//																									// used
//		sr.setReturn_url(SR_RETURN_URL);																							// in
//		sr.setFinalName(student.getFirstName() + " " + student.getLastName());	
//		sr.setPostalAddress(address);
//		sr.setMobile(mobile);
//		sr.setEmailId(emailId);
//		sr.setAlgo("MD5");
//		sr.setV3URL(V3URL);
//		// response
//		return sr;
//
//	}
	
	private String saveAllTransactionDetails(HttpServletRequest request) {

		try {
			//PaymentHelper payment = new PaymentHelper();
			ServiceRequestStudentPortal bean = paymentHelper.CreateResponseBean(request);
			if(bean != null) {
				if(bean.getTrackId().equalsIgnoreCase(bean.getMerchantRefNo())) {
					serviceRequestDao.insertOnlineTransaction(bean);
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
	

	@PostMapping(value = "/getDispatches")
	public ResponseEntity<HashMap<String, String>> mgetDispatches(@RequestBody StudentStudentPortalBean input) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		HashMap<String, String> mapOfDispatchParameters = new HashMap<String, String>();
		mapOfDispatchParameters = servReqServ.getDispatches(input.getSapid());
		
		if (mapOfDispatchParameters.isEmpty()) {
			HashMap<String, String> errorResponse = new HashMap<String, String>();
			errorResponse.put("error", "true");
			errorResponse.put("errorMessage", "No Dispatches Made.");
			
			return new ResponseEntity<HashMap<String, String>>(errorResponse, headers, HttpStatus.OK);

		}
	
		return new ResponseEntity<HashMap<String, String>>(mapOfDispatchParameters,headers,HttpStatus.OK);

	}

	
////////////////////////////////////////////////////////////////////////////////////////
//mappings for sr issuance of bonafide start
///////////////////////////////////////////////////////////////////////////////////////


		@PostMapping(path = "/saveBonafideRequest")
		public ResponseEntity<ServiceRequestStudentPortal> saveBonafideRequest(HttpServletRequest request,
				@RequestBody ServiceRequestStudentPortal sr)throws Exception 
		{

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			

			ServiceRequestStudentPortal response = servReqServ.saveBonafideRequest(sr,sr.getPurpose());

			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}



		@PostMapping(path = "/saveBonafideRequestAndProceedToPay")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveBonafideRequestAndProceedToPay(HttpServletRequest request,
					@RequestBody ServiceRequestStudentPortal sr)throws Exception
		{
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String purpose = sr.getPurpose();
			sr.setAdditionalInfo1(purpose);
			ServiceRequestStudentPortal response = servReqServ.SaveBonafideRequestAndProceedToPay(sr,request);

			//String paymentUrl = "proceedToPaymentGatewaySr?sapId="+response.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId();
			//response.setPaymentUrl(paymentUrl);

			String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
			sr.setPaymentUrl(paymentUrl);
			//return new ResponseEntity<>(response,headers, HttpStatus.OK);

			return new ResponseEntity<ServiceRequestStudentPortal>(sr,headers, HttpStatus.OK);


		}

////////////////////////////////////////////////////////////////////////////////////////
//mappings for sr issuance of bonafide end
///////////////////////////////////////////////////////////////////////////////////////
		
		
		@PostMapping(path = "/getStudentSRList",consumes = "application/json", produces = "application/json")
		public ResponseEntity<ArrayList<ServiceRequestStudentPortal>> getStudentSRList(HttpServletRequest request,@RequestBody StudentStudentPortalBean student){
			//student.setSapid(request.getParameter("sapid"));
			String status = request.getParameter("status");
			ArrayList<ServiceRequestStudentPortal> srList = new ArrayList<ServiceRequestStudentPortal>();
			if(status.equalsIgnoreCase("pending")) {
				srList = serviceRequestDao.getStudentsPendingSR(student.getSapid());
			}
			else if(status.equalsIgnoreCase("closed")) {
				srList = serviceRequestDao.getStudentsClosedSR(student.getSapid());
			}
			for(ServiceRequestStudentPortal bean : srList) {
				if(bean.getHasDocuments().equalsIgnoreCase("Y")) {
					bean.setDocuments(serviceRequestDao.getDocuments(bean.getId()));
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			return new ResponseEntity<ArrayList<ServiceRequestStudentPortal>>(srList,headers,HttpStatus.OK);
		}
		
		
		
		@PostMapping(path = "/getActiveSRList", headers = "content-type=multipart/form-data")
		public ResponseEntity<ArrayList<String>> getActiveSRList(HttpServletRequest request,StudentStudentPortalBean student){
			ArrayList<String> srList = getRequestTypes(request,student.getSapid());
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			return new ResponseEntity<ArrayList<String>>(srList,headers,HttpStatus.OK);
		}
		
		
		@RequestMapping(path = "/srFeeResponse", method = { RequestMethod.GET, RequestMethod.POST })
		public void mSrFeeResponse(HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
				Model m) throws IOException {

			ServiceRequestStudentPortal sr = paymentHelper.CreateResponseBean(request);
			request.getSession().setAttribute("amount", sr.getAmount());
			
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
	    	CertificateInterface certificate = certificateFactory.getProductType(CertificateFactory.ProductType.valueOf(sr.getProductType())); 

	    	try {
				sr = certificate.srFeeResponse(sr, student, request,respnse, model,m);
				respnse.sendRedirect(sr.getReturn_url());
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				respnse.sendRedirect("ssoservices/mbax/sRPaymentSuccess?status=error&message="+e.getMessage());
				return;
			}
			
		


		}
		
		
//		@CrossOrigin(origins = "http://localhost:3000")
		@PostMapping(path = "/saveMarksheetRequest",consumes = "application/json")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveMarksheetRequest(HttpServletRequest request,@RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response =  servReqServ.saveMarksheetRequest(sr,request);
			
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		
//		@CrossOrigin(origins = "http://localhost:3000")
		@PostMapping(path = "/saveMarksheetAndPaymentForMBAWX", consumes="application/json", produces="application/json")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveMarksheetAndPaymentForMBAWX(HttpServletRequest request,
				 @RequestBody ServiceRequestStudentPortal sr)throws Exception {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			ServiceRequestStudentPortal response =  servReqServ.saveMarksheetAndPaymentForMBAWX(sr,request);
			
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			
		}
		
//		@CrossOrigin(origins = "http://localhost:3000")
		@PostMapping(path = "/saveMarksheetAndPaymentDocsForMBAWX", headers = "content-type=multipart/form-data")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveMarksheetAndPaymentDocsForMBAWX(HttpServletRequest request,
				 ServiceRequestStudentPortal sr,  MultipartFile firCopy,
				 MultipartFile indemnityBond, @RequestParam Integer listSize )throws Exception {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			
			ServiceRequestStudentPortal response =  servReqServ.saveMarksheetAndPaymentDocsForMBAWX(sr,request,firCopy,indemnityBond,listSize);
			
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			
		}
		
		
		@PostMapping(path = "/saveChangeInICard", headers = "content-type=multipart/form-data" )
		public ResponseEntity<ServiceRequestStudentPortal> MsaveChangeInICard(HttpServletRequest request,
				MultipartFile changeInIDDoc,ServiceRequestStudentPortal sr,@RequestParam Map<String,String> mapOfInputs
					 )throws Exception {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			String sapId= sr.getSapId();
			sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + ": First Name:" + mapOfInputs.get("firstName")
			+ ", Last Name: " + mapOfInputs.get("lastName"));
			
			ServiceRequestStudentPortal response = servReqServ.saveNewICard(sr,changeInIDDoc,request);
			if(response.getError() !=null) {
			     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		       }
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		
		@PostMapping(path = "/saveCorrectDOB", headers = "content-type=multipart/form-data" )
		public ResponseEntity<ServiceRequestStudentPortal> MsaveCorrectDOB(HttpServletRequest request,
					MultipartFile sscMarksheet,ServiceRequestStudentPortal sr )throws Exception {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response =  servReqServ.saveDOB(sr,sscMarksheet);
			if(response.getError() !=null) {
			     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		       }
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		
		@PostMapping(path="/saveSpecialNeedSR",headers="content-type=multipart/form-data")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveSpecialNeedsSR(MultipartFile medical,ServiceRequestStudentPortal sr) throws Exception
		{
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId()); 
			ServiceRequestStudentPortal response =servReqServ.saveSpecialNeedSR(sr,medical,student);
			if(response.getError() !=null) {
			     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		       }
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		
		
		@PostMapping(path = "/saveChangeInName", headers = "content-type=multipart/form-data")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveChangeInName(ServiceRequestStudentPortal sr,
				MultipartFile changeInNameDoc, HttpServletRequest request) {
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			sr.setIssued("N");
			sr.setFirstName(request.getParameter("firstName"));
			sr.setLastName(request.getParameter("lastName"));
			sr.setMiddleName(request.getParameter("middleName"));
			// sapid = "77214002240"; 
			ServiceRequestStudentPortal response = servReqServ.saveChangeInName(sr,changeInNameDoc);
			if(response.getError() !=null) {
			     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		       }
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		
		@GetMapping(path = "/paymentResponse")
		public ModelAndView paymentResponse(HttpServletRequest request, HttpServletResponse response,String status,String message) {
		
//			request.setAttribute("status", status);
//			request.setAttribute("message", message);
			
			return new ModelAndView("jsp/paymentResponse");
		}
		
		
		
	    // Mobile Api Save Feedback
	    @PostMapping(path = "/saveFeedback", consumes="application/json", produces="application/json")
	    public ResponseEntity<Map<String,String>> msaveFeedback(@RequestBody FeedbackBean fb,  @RequestParam("sapid") String sapid){
	         HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/json");
	         Map<String,String> response = new HashMap<String,String>(); 
	         StudentStudentPortalBean studentFeedback = new  StudentStudentPortalBean();
	         studentFeedback.setSapid(sapid);
	         try {    
	         serviceRequestDao.saveFeedBack(fb, studentFeedback);
	         }catch(Exception e){
	               response.put("error","true");
	               response.put("errorMessage", "Error in posting.");
	         }
	         response.put("success", "true");
	         response.put("successMessage", "Thank you for your valuable Suggestion");
	         return new ResponseEntity<>(response,headers,HttpStatus.OK);
	    }
	    
	    
	    
	    @PostMapping(path = "/saveChangeInPhotograph", headers = "content-type=multipart/form-data" )
		public ResponseEntity<ServiceRequestStudentPortal> MsaveChangeInPhotograph(HttpServletRequest request,
					ServiceRequestStudentPortal sr, MultipartFile changeInPhotographDoc,MultipartFile changeInPhotographProofDoc)throws Exception {
	    	HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response = servReqServ.savePhograph(sr,changeInPhotographDoc,changeInPhotographProofDoc);
			if(response.getError() !=null) {
			     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		       } 
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
	    
	    
	    @PostMapping(path="/getCheckSumForPaytm")
	    public @ResponseBody HashMap<String, String> getCheckSumForPaytm(@RequestBody ServiceRequestStudentPortal sr){
	    	
	    	if(sr == null) {
	    		HashMap<String, String> errorResponse = new HashMap<>();
	    		errorResponse.put("status", "500");
	    		errorResponse.put("message","Invalid Request Param formate found");
	    		return errorResponse;
	    	}
	    	
	    	if(sr.getSapId() == null) {
	    		HashMap<String, String> errorResponse = new HashMap<>();
	    		errorResponse.put("status", "500");
	    		errorResponse.put("message","Invalid Request Param SapId found");
	    		return errorResponse;
	    	}
	    	
	    	//PaymentHelper paymentHelper = new PaymentHelper();
	    	StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
			StudentStudentPortalBean student = studentDao.getstudentData(sr.getSapId());
	    	paymentHelper.generateCheckSum(sr, student, "");
	    	HashMap<String, String> successResponse = new HashMap<>();
	    	successResponse.put("status", "200");
	    	successResponse.put("checkSum",sr.getSecureHash());
	    	return successResponse;
	    }

	    
	    
//	    service request: program de-registration
	    @PostMapping(path = "/saveProgramDeRegistration", consumes="application/json")
	   	public ResponseEntity<ServiceRequestStudentPortal> MsaveProgramDeRegistration(HttpServletRequest request,@RequestBody ServiceRequestStudentPortal sr)
	   		throws Exception {
	       	HttpHeaders headers = new HttpHeaders();
	   		headers.add("Content-Type", "application/json");
	   		ServiceRequestStudentPortal response = servReqServ.saveProgramDeRegistration(sr);
	   		if(response.getError() !=null) {
	   		     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	   	       } 
	   		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	   	}
	    
//	    service request : Issuance Of Marksheet for MBA WX
		@PostMapping(path = "/checkMarksheetHistoryForMBAWX" )
		public ResponseEntity<ServiceRequestStudentPortal> McheckMarksheetHistoryForMBAWX(HttpServletRequest request,
				@RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response =  servReqServ.checkMarksheetHistoryForMBAWX(sr,request);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
	//  service request : Issuance Of Marksheet for MBA WX
		@PostMapping(path = "/checkMarksheetHistoryForMBAX" )
		public ResponseEntity<ServiceRequestStudentPortal> McheckMarksheetHistoryForMBAX(HttpServletRequest request,
				@RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response =  servReqServ.checkMarksheetHistoryForMBAX(sr,request);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		
	//  service request : Issuance Of Marksheet for MBA WX
		@PostMapping(path = "/getStudentSubjectRepeatStatusMBAWX")
		public ResponseEntity<ServiceRequestStudentPortal> MgetStudentSubjectRepeatStatusMBAWX(HttpServletRequest request, @RequestBody ServiceRequestStudentPortal sr)throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			ServiceRequestStudentPortal response =  servReqServ.getSubjectRepeatStatusForStudent(sr.getSapId());
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		
		
		@PostMapping(path = "/saveSubjectRegistrationSRPaymentForMBAWX", consumes="application/json", produces="application/json")
		public ResponseEntity<ServiceRequestStudentPortal> MsaveSubjectRegistrationSRPaymentForMBAWX(HttpServletRequest request, HttpServletResponse response, @RequestBody ServiceRequestStudentPortal sr)throws Exception {

			if("https://localhost/".equals(SERVER_PATH)) {
				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none;");
				response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
			} else {
				response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
				response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
			}
			ServiceRequestStudentPortal srResponse =  new ServiceRequestStudentPortal();
			try {
				srResponse =  servReqServ.saveSubjectRegistrationSRPaymentForMBAWX(sr);
				srResponse.setError("false");
			}catch (Exception e) {
				srResponse.setError("true");
				srResponse.setErrorMessage("Error Initiating Service Request!");
			}
			
			return new ResponseEntity<>(srResponse, HttpStatus.OK);
			
		}
		
		@PostMapping(path = "/findServiceRequest", consumes="application/json", produces="application/json")
		public ResponseEntity<ServiceRequestResponse> findServiceRequest(HttpServletRequest request, @RequestBody ServiceRequestStudentPortal sr)throws Exception {

			ServiceRequestResponse response =  servReqServ.getSRStatus(sr);
			
			
			
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		}
		
		@PostMapping(path = "/issuanceOfBonafideCertificateForm")	
		public ResponseEntity<ServiceRequestStudentPortal> MissuanceOfBonafideCertificateForm(HttpServletRequest request,	
				@RequestBody ServiceRequestStudentPortal sr)throws Exception {	
			HttpHeaders headers = new HttpHeaders();	
			headers.add("Content-Type", "application/json");	
			ServiceRequestStudentPortal response = servReqServ.issueBonafide(sr,request);	
				
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
				
		}

	/**
	 * Update the Request Status And Cancellation Reason for a particular Service Request 
	 * in service_request & service_request_history table
	 * @param srAdminUpdateDto - bean containing the required fields of ServiceRequestBean
	 * @param request - HttpServletRequest to get the session attributes
	 * @return - status code
	 */
	@PostMapping(value="/admin/saveRequestStatusAndReason")
	public ResponseEntity<?> saveRequestStatusAndReason(@RequestBody SrAdminUpdateDto srAdminUpdateDto, HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		try {
			servReqServ.updateServiceRequestStatusAndReason(srAdminUpdateDto, userId);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch(RecordNotExistException re) {
			logger.info("Failed to update Service Request Status And Cancellation Reason, due to " + re.toString());
			return new ResponseEntity<>(re.getMessage(),HttpStatus.NOT_FOUND);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.info("Failed to update Service Request Status And Cancellation Reason, due to " + ex.toString());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@RequestMapping(value = "/saveChangeInSpecializationRequest", method = RequestMethod.POST )
	public ResponseEntity<ServiceRequestStudentPortal> saveChangeInSpecializationRequest(HttpServletRequest request, MultipartFile sscMarksheet,
		@RequestBody ServiceRequestStudentPortal sr )throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		try {
			
			ChangeInSpecializationServiceInterface changeInSpecialization = specializationFactory.getProductType(ChangeInSpecializationFactory.ProductType.valueOf(sr.getProductType()));
			sr = changeInSpecialization.createServiceRequest(sr);	
			
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId()); 
			String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
			sr.setPaymentUrl(paymentUrl);

			request.getSession().setAttribute("sr", sr);
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("student_studentportal", student);
			
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		} catch (Exception e) {
			
		// TODO Auto-generated catch block
			//e.printStackTrace();
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		}

	} 
	@PostMapping(value = "/getSrTypes")
	public ResponseEntity<ServiceRequestResponse> getSrTypes(@RequestBody ServiceRequestStudentPortal sr)throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestResponse response = new ServiceRequestResponse(); 
		try {
			ArrayList<String> srTypes =  servReqServ.getSrRequestTypes(sr.getSapId());
			//srTypes.remove("Issuance of Gradesheet");
			response.setSrTypes(srTypes); 
			return new ResponseEntity<ServiceRequestResponse>(response,headers, HttpStatus.OK);
			
		} catch(Exception e) {
			e.printStackTrace();
			sr.setError(e.getMessage());
			return new ResponseEntity<ServiceRequestResponse>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
	

	@RequestMapping(value = "/saveDuplicateStudyKit", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceRequestStudentPortal> MsaveDuplicateStudyKit(@RequestBody ServiceRequestStudentPortal sr,HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
 
		ServiceRequestStudentPortal response = null;
		try {
			response = servReqServ.saveDuplicateStudyKit(sr);
			String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
			sr.setPaymentUrl(paymentUrl);
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(response,headers, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
		

	@PostMapping(path = "/saveSingleBook")
	public ResponseEntity<ServiceRequestStudentPortal> MsaveSingleBook(HttpServletRequest request,
			@RequestBody ServiceRequestStudentPortal sr)throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ServiceRequestStudentPortal response = servReqServ.saveSingleBook(sr);
		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sr.getSapId()+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
		sr.setPaymentUrl(paymentUrl);
		
		return new ResponseEntity<>(sr,headers, HttpStatus.OK);		
		
	}


	 

////////////////////////////////////////////////////////////////////////////////////////
//mappings for sr program withdrawal start
///////////////////////////////////////////////////////////////////////////////////////

	@PostMapping(value = "/saveProgramWithdrawal")
	public ResponseEntity<ServiceRequestStudentPortal> saveProgramWithdrawal(@RequestBody ServiceRequestStudentPortal sr)throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestStudentPortal response = new ServiceRequestStudentPortal();
		try {
			
			response =  servReqServ.saveProgramWithdrawal(sr);
			if(response.getError()!="true" && sr.getSapId()!=null) {
				//update student table
				response.setError("false");
				servReqServ.updateStudentProgramStatus(sr.getSapId()); 

			}

			return new ResponseEntity<ServiceRequestStudentPortal>(response,headers, HttpStatus.OK);

		} catch(Exception e) {
			
			sr.setError(e.getMessage());
			return new ResponseEntity<ServiceRequestStudentPortal>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);	
		}

	}	


	@PostMapping(value = "/checkIfStudentApplicableForWithdrawal")
	public ResponseEntity<String> checkIfStudentApplicableForWithdrawal(@RequestBody StudentStudentPortalBean student)throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {

			
			boolean flag = servReqServ.checkIfStudentApplicableForWithdrawal(student);

			if(flag) {
				return new ResponseEntity<String>("",headers, HttpStatus.OK);
			} 

			return new ResponseEntity<String>("Error!! Couldn't raise Service request",headers, HttpStatus.OK);

		} catch(Exception e) {
			return new ResponseEntity<String>("Error!! Couldn't raise Service request "+e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);	
		}

	}	

////////////////////////////////////////////////////////////////////////////////////////
//mappings for sr program withdrawal end
///////////////////////////////////////////////////////////////////////////////////////
	
  
    
	@PostMapping(value = "/checkIfStudentApplicableForExitProgram")
	public ResponseEntity<String> checkIfStudentApplicableForExitProgram(@RequestBody StudentStudentPortalBean student)throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestStudentPortal serReq = new ServiceRequestStudentPortal();
		String errorReasonmsg="";
		try {
			serReq=servReqServ.checkIfStudentIsApplicableForExitPrograms(student);
		    if(serReq.getIsCertificate()) {
		    	return new ResponseEntity<String>("",headers, HttpStatus.OK);
		    } 
		    if(!StringUtils.isBlank(serReq.getError())) {
		    	errorReasonmsg=serReq.getError();
		    }
		    serReq.setError("Sorry! You are not Eligible to raise SR for Exit Program "+errorReasonmsg);  
		    
		}catch(NoSuchElementException n) {
			serReq.setError("Sorry! You are not Eligible to raise SR for Exit Program.");	
		}catch(Exception e) {
			logger.error("Error in proceeding request for SAP ID: "+student.getSapid()+ "ERROR: " +e.getMessage());
			serReq.setError("Error in proceeding request "+e.getMessage());	
		}
		return new ResponseEntity<String>(serReq.getError(), headers, HttpStatus.OK);
	}	


	@PostMapping(value = "/saveExitProgram")
	public ResponseEntity<ServiceRequestStudentPortal> saveExitProgram(@RequestBody ServiceRequestStudentPortal sr)throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestStudentPortal serReq = new ServiceRequestStudentPortal();
		try {
			serReq = servReqServ.saveExitProgram(sr);
			
			  if(serReq.getError()!="true" && serReq.getSapId()!=null) {
		   			//update student table
		   	   		int count = servReqServ.updateStudentProgramStatus(serReq.getSapId()); 
		   	   		if(count>0) {
		   	   			serReq.setSuccessMessage( "Success!! Service request raised ");
		   	   		}
		   		}else {
		   			serReq.setError(sr.getErrorMessage());  
		   	    } 
			  
		} catch(Exception e) {
			serReq.setError("Error in proceeding request "+e.getMessage());	
			
		}
		return new ResponseEntity<ServiceRequestStudentPortal>(serReq,headers, HttpStatus.OK);
	}
	
	/** 
	  * Collects the details required to show in contents of certificate when admin preview the pdf
	  * @param sapid of the student
	  * @param reason of the SR request
	  * @param srid of the SR request
	  * @param status of the Sr request
	  * @param request HttpServletRequest
	  * @param response HttpServletResponse
	  */
	@PostMapping(value = "/previewEBonafidePDF", produces="application/json")
	public ResponseEntity<StudentSrDTO> previewEBonafidePDF(@RequestParam(value="sapid")  String sapId,@RequestParam(value="reason") String reason, 
		@RequestParam(value="srid") Long srId, @RequestParam(value="status") String status,
		HttpServletRequest request, HttpServletResponse response) { 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StudentSrDTO srDto = new StudentSrDTO();
		try {
			if("Closed".equals(status)) {
				srDto = servReqServ.eBonafidePDFDetails(sapId, reason, srId);
			}
			else {
				srDto = servReqServ.eBonafidePDFContent(sapId, reason, srId);
			}
			return new ResponseEntity<>(srDto, HttpStatus.OK);
		}catch (Exception ex) {
			logger.error("error in getting showPDF details for srId {} sapid {} due to ", srId, sapId, ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Student' FatherName, MotherName and SpouseName (Husband Name) details are fetched and returned as a Map of key-value pairs.
	 * @param sapid - studentNo of the Student
	 * @return Map containing the fatherName, motherName, spouseName details stored in key-value pairs
	 */
	@GetMapping(value="/student/getCurrentFatherMotherHusbandName/{sapid}")
	public ResponseEntity<Map<String, Object>> getCurrentFatherMotherHusbandName(@PathVariable Long sapid) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			logger.info("Fetching stored Father, Mother and Spouse Name details of Student: {}", sapid);
			Map<String, Object> studentFatherMotherHusbandNameMap = servReqServ.studentFatherMotherHusbandName(sapid);
			logger.info("Successfully fetched student' Father, Mother and Spouse Name details: {}", studentFatherMotherHusbandNameMap.toString());
			responseMap.put("status", "success");
			responseMap.put("details", studentFatherMotherHusbandNameMap);
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
//			ex.printStackTrace();
			responseMap.put("status", "error");
			responseMap.put("message", ex.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching details for Student: {}, Exception thrown: {}", sapid, ex.toString());
			responseMap.put("status", "error");
			responseMap.put("message", "Error while fetching Student details.");
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Student' EmailId and MobileNo details are fetched and returned as a Map of key-value pairs.
	 * @param sapid - studentNo of the Student
	 * @return Map containing the emailId and MobileNo details stored in key-value pairs
	 */
	@GetMapping(value="/student/getCurrentEmailIdMobileNo/{sapid}")
	public ResponseEntity<Map<String, Object>> getCurrentEmailIdMobileNo(@PathVariable Long sapid) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			logger.info("Fetching stored EmailId and MobileNo details of Student: {}", sapid);
			Map<String, Object> studentEmailMobileMap = servReqServ.studentEmailIdMobileNo(sapid);
			logger.info("Successfully fetched student' EmailId and MobileNo details: {}", studentEmailMobileMap.toString());
			responseMap.put("status", "success");
			responseMap.put("details", studentEmailMobileMap);
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
	//		ex.printStackTrace();
			responseMap.put("status", "error");
			responseMap.put("message", ex.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
	//		ex.printStackTrace();
			logger.error("Error while fetching details for Student: {}, Exception thrown: {}", sapid, ex.toString());
			responseMap.put("status", "error");
			responseMap.put("message", "Error while fetching Student details.");
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Checks if the student is eligible to raise the Change Details Service Request. 
	 * If the student had previously raised a Change Details Service Request to update a particular detail, 
	 * and the Service Request status of that SR is not in Closed or Cancelled state, 
	 * the student is not eligible to raise the Service Request to update that particular detail, as it is still in progress.
	 * @param sapid - studentNo of the Student
	 * @param detailType - type of detail that is to be updated
	 * @return boolean value indicating if the student is eligible or not
	 */
	@GetMapping(value="/student/checkEligibilityChangeDetailsSR")
	public ResponseEntity<Boolean> checkEligibilityChangeDetailsSR(@RequestParam Long sapid, @RequestParam String detailType) {
		try {
			logger.info("Checking eligibility of Student: {} to raise the Change Details Service Request for Change in {}", sapid, detailType);
			boolean isEligible = servReqServ.checkStudentContactDetailsSrEligibility(sapid, detailType);
			logger.info("Eligibility of Student: {} returned as {} to raise the Change in {} SR", sapid, isEligible, detailType);
			return new ResponseEntity<>(isEligible, HttpStatus.OK);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.error("Error while checking eligibility of Student: {} to raise the Change Details Service Request for Change in {}", sapid, detailType);
			return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/getCurrentFatherMotherHusbandName")
	public ResponseEntity<String> getCurrentFatherMotherHusbandNameMobile(HttpServletRequest request,
			@RequestBody ChangeDetailsSRDto sr)throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		String studentFatherMotherHusbandNameJsonString = "";
		try {
			Map<String, Object> studentFatherMotherHusbandNameMap = servReqServ.studentFatherMotherHusbandName(sr.getSapid());
			studentFatherMotherHusbandNameJsonString = ObjectConverter.mapToJson(studentFatherMotherHusbandNameMap);
		}
		catch(Exception ex) {
			Map<String, Object> studentFatherMotherSpouseFirstNameMap = new HashMap<>();
			studentFatherMotherSpouseFirstNameMap.put("error", true);
			studentFatherMotherSpouseFirstNameMap.put("errorMessage", "Error occured while fetching user details, Please Try Again");
			studentFatherMotherHusbandNameJsonString = ObjectConverter.mapToJson(studentFatherMotherSpouseFirstNameMap);	
		}
		return new ResponseEntity<>(studentFatherMotherHusbandNameJsonString,headers, HttpStatus.OK);
	}
	
	
	@PostMapping(path = "/changeFatherMotherSpouseNameSR", headers = "content-type=multipart/form-data" )
	public  ResponseEntity<ServiceRequestStudentPortal> changeFatherMotherSpouseNameSRmobile(HttpServletRequest request, @ModelAttribute ChangeDetailsSRDto srDto, MultipartFile supportingDocument) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestStudentPortal serviceRequestBean = new ServiceRequestStudentPortal();
		try {
			srDto.setSupportingDocument(supportingDocument);
			serviceRequestBean =  servReqServ.changeFatherMotherSpouseName(srDto);
			return new ResponseEntity<ServiceRequestStudentPortal>(serviceRequestBean,headers, HttpStatus.OK);
		}
		catch(Exception ex) {
			serviceRequestBean.setError("Error in raising Service Request. Please try again!");
			serviceRequestBean.setErrorMessage("Error in raising Service Request. Please try again!");
			return new ResponseEntity<ServiceRequestStudentPortal>(serviceRequestBean,headers, HttpStatus.OK);
		}
	}
	
	@PostMapping(value="/changeInContactDetailsSR")
	public ResponseEntity<ServiceRequestStudentPortal> changeInContactDetailsSR(@RequestBody ChangeDetailsSRDto srDto) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			logger.info("Validating and storing the Change Details Service Request raised by the student: {}", srDto.toString());
			ServiceRequestStudentPortal response =  servReqServ.changeContactDetails(srDto);
			logger.info("Change Details Service Request successfully raised of Student: {}, with Service Request ID: {}", response.getSapId(), response.getId());

			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			ServiceRequestStudentPortal response = new ServiceRequestStudentPortal();
			//Add Error Message to display to the user
			String errorMessage = ex.getMessage();		//IllegalArgumentException thrown on BindingResult error
			response.setError(errorMessage);
			logger.error("Error Message shown to user: {} Exception thrown: {}", errorMessage, ex.toString());
			
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//  service request : Issuance Of Marksheet for MBA WX
	@PostMapping(path = "/getStudentSubjectRepeatStatusMBAX")
	public ResponseEntity<ServiceRequestStudentPortal> MgetStudentSubjectRepeatStatusMBAX(HttpServletRequest request, @RequestBody ServiceRequestStudentPortal sr)throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ServiceRequestStudentPortal response =  servReqServ.getSubjectRepeatStatusForStudent(sr.getSapId());
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/saveSubjectRegistrationSRPaymentForMBAX", consumes="application/json", produces="application/json")
	public ResponseEntity<ServiceRequestStudentPortal> MsaveSubjectRegistrationSRPaymentForMBAX(HttpServletRequest request, HttpServletResponse response, @RequestBody ServiceRequestStudentPortal sr)throws Exception {

		/*if("https://localhost/".equals(SERVER_PATH)) {
			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none;");
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		} else {
			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		}*/
		ServiceRequestStudentPortal srResponse =  new ServiceRequestStudentPortal();
		try {
			srResponse =  servReqServ.saveSubjectRegistrationSRPaymentForMBAWX(sr);
			srResponse.setError("false");
		}catch (Exception e) {
			srResponse.setError("true");
			srResponse.setErrorMessage("Error Initiating Service Request!");
		}
		
		return new ResponseEntity<>(srResponse, HttpStatus.OK);
		
	}
	
	@PostMapping(value="/checkEligibilitySpecialNeedSR", consumes="application/json", produces="application/json")
	public ResponseEntity<ServiceRequestResponse> checkEligibilitySpecialNeedSR(HttpServletRequest request, @RequestBody ServiceRequestStudentPortal sr) {
		ServiceRequestResponse response = new ServiceRequestResponse();

		try {
			List<ServiceRequestStudentPortal> ListofSR=serviceRequestDao.getSRBySapIdandtype(sr.getSapId(), sr.getServiceRequestType());
			logger.info("Checking eligibility of Student: {} to raise the special need Service Request", sr.getSapId());
			if(ListofSR.size()>0){
 				for (ServiceRequestStudentPortal SR : ListofSR) {
 					if(SR.getRequestStatus().equals("Cancelled"))
 					{
 						response.setError("false");
 					}
 					else if(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED.equals(SR.getRequestStatus())){
 						response.setError("true");
 						response.setErrorMessage("Already submitted Special Need SR. Please Wait for Approval");
 					}else if(ServiceRequestStudentPortal.REQUEST_STATUS_CLOSED.equals(SR.getRequestStatus())){
 						response.setError("true");
 						response.setErrorMessage("Your SR for Special Need is already approved");
 					}else{
 						response.setError("false");
 					}
 				}
 			}else {
 				response.setError("false");
 			}
			return new ResponseEntity<ServiceRequestResponse>(response, HttpStatus.OK);
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			logger.error("Error while checking eligibility of Student: {} to raise the special need Service Request", sr.getSapId());
			response.setError("true");
			response.setErrorMessage(ex.getMessage());
			return new ResponseEntity<ServiceRequestResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/checkTranscriptEligibility")
	public ResponseEntity<ServiceRequestStudentPortal> mCheckTranscriptCertificateEligibility(
			@RequestBody ServiceRequestStudentPortal sr){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
	
		TranscriptServiceInterface transcript = transcriptFactory.getProductType(TranscriptFactory.ProductType.valueOf(sr.getProductType()));

		
		try {
			
		
			sr = transcript.checkEligibility(sr);
			
			loggerForTranscript.info("checkTrasncriptEligibility success detail : sapid="+sr.getSapId());
			
			return new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		} catch (Exception e) {
			loggerForTranscript.error("checkTranscriptEligibility fail detail : sapid="+sr.getSapId()+" Error message "+e.getMessage());
			sr.setError(e.getMessage());
			return  new ResponseEntity<>(sr,headers, HttpStatus.OK);
			
		}
	}
	@GetMapping("/getApprovedStudentList")
	public ResponseEntity<SpecialNeedStudent> getApprovedStudentList() {
		SpecialNeedStudent student = new SpecialNeedStudent();
		ArrayList<String> list = new ArrayList<String>();
		try {
			list = servReqServ.getApprovedStudentList("Special Needs SR", "Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		student.setListOfSapid(list);
		return new ResponseEntity<SpecialNeedStudent>(student, headers, HttpStatus.OK);
	}
	
	@PostMapping(path="/saveScribeSR",headers="content-type=multipart/form-data")
	public ResponseEntity<ServiceRequestStudentPortal> MsaveScribeSR(MultipartFile resume,ServiceRequestStudentPortal sr) throws Exception
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
		ServiceRequestStudentPortal response =servReqServ.saveScribeSR(sr,resume,student);
		if(response.getError() !=null) {
		     return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		return new ResponseEntity<>(response,headers,HttpStatus.OK);
	}
	
	@PostMapping(value="/checkEligibilityScribe", consumes="application/json", produces="application/json")
	public ResponseEntity<ServiceRequestResponse> checkEligibilityScribe(HttpServletRequest request, @RequestBody ServiceRequestStudentPortal sr) {
		ServiceRequestResponse response = new ServiceRequestResponse();
		List<ServiceRequestStudentPortal> ScribeResponse=new ArrayList<ServiceRequestStudentPortal>();
		try {
		String exammonth="";
			List<ServiceRequestStudentPortal> spceialNeedsSRList=serviceRequestDao.getSRBySapIdandtype(sr.getSapId(), "Special Needs SR"); 
			if(spceialNeedsSRList.size()>0){
				for (ServiceRequestStudentPortal SR : spceialNeedsSRList) {
					if(!ServiceRequestStudentPortal.REQUEST_STATUS_CLOSED.equalsIgnoreCase(SR.getRequestStatus())){
 						response.setError("true");
 						response.setErrorMessage("Your SR for Special Need is Not approved.");
					}else{
						String examdetails=serviceRequestDao.getExamMonthByAcadMonth(CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR);
						int listofresitsubject=serviceRequestDao.getpassfailstatus(sr.getSapId());
						if(listofresitsubject>0) {
							exammonth=servReqServ.returnmonthforResitExam();
						}else {
							exammonth=examdetails;
						}
						List<ServiceRequestStudentPortal> checkalreadyregistered=serviceRequestDao.getSRByStudentDetails(sr.getSapId(),sr.getServiceRequestType(), exammonth, CURRENT_ACAD_YEAR);
						if(checkalreadyregistered.size()>0){
				 				for (ServiceRequestStudentPortal SR1 : checkalreadyregistered) {
				 					if(ServiceRequestStudentPortal.REQUEST_STATUS_IN_PROGRESS.equalsIgnoreCase(SR1.getRequestStatus())){
				 						response.setError("true");
				 						response.setErrorMessage("Your SR for Scribe for Term End Exam is In Progress.");
				 					}else if(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED.equals(SR1.getRequestStatus())){
				 						response.setError("true");
				 						response.setErrorMessage("Already submitted Scribe for Term End Exam SR. Please Wait for Approval.");
				 					}else{
				 						response.setError("true");
				 						response.setErrorMessage("Your SR for Scribe for Term End Exam is already approved.");
				 					}
				 				}
				 		}else {
						 	response.setError("false");
						 	sr.setMonth(exammonth);
						 	sr.setYear(CURRENT_ACAD_YEAR);
						 	ScribeResponse.add(sr);
						 	response.setResponse(ScribeResponse);
						 }
					}
				}	
			}else {
				response.setError("true");
				response.setErrorMessage("Please get approved Special Needs SR.");
			}
			return new ResponseEntity<ServiceRequestResponse>(response, HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<ServiceRequestResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/saveEBonafidePDFContent", produces="application/json")
	public ResponseEntity<StudentSrDTO> saveEBonafidePDFContent(@RequestBody EBonafidePDFContentRequestBean bean, 
		HttpServletRequest request, HttpServletResponse response) { 
		
		String userId = (String) request.getSession().getAttribute("userId");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			servReqServ.saveEBonafideContent(bean, userId);
			return new ResponseEntity<>(new StudentSrDTO(), headers, HttpStatus.OK);
		} 
		catch (Exception ex) {
			logger.error("Failed to save eBonafide content for srId : {}, due to ", bean.getCustomPDFContent().get(0).getServiceRequestId(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
