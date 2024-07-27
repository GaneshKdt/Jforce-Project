package com.nmims.stratergies.impl;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.controllers.BaseController;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.PaymentHelper;
import com.nmims.stratergies.ProceedToGatewaySRStratergyInterface;

@Service("proceedToGatewaySRStratergy")
public class ProceedToGatewaySRStratergy extends BaseController implements ProceedToGatewaySRStratergyInterface{

	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; 
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	@Value("${V3URL}") 
	private String V3URL;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH; 
	@Value("${SR_RETURN_URL_MOBILE}")
	private String SR_RETURN_URL_MOBILE;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	PaymentHelper paymentHelper;
	
	private final int SECOND_MARKSHEET_FEE_PER_SUBJECT = 500;
	
	@Override
	public ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, String sapId, String serviceRequestId, String paymentOptionName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("sapID:--"+sapId);
		ModelMap model = new ModelMap();
				StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
				
				ServiceRequestStudentPortal service = serviceRequestDao.findById(serviceRequestId);
				String requestId = service.getTrackId();
				
				ArrayList<ServiceRequestStudentPortal> srList = serviceRequestDao.findByTrackId(requestId);
				if(service.getServiceRequestType().equalsIgnoreCase("Issuance of Marksheet") || 
						service.getServiceRequestType().equalsIgnoreCase("Issuance of Gradesheet")	) {
				        try {
							service.setMarksheetDetailRecord1(srList.get(0).getYear()+ "|" +srList.get(0).getMonth()+ "|" + srList.get(0).getSem()+ "|" + srList.get(0).getId());
							service.setMarksheetDetailRecord2(srList.get(1).getYear()+ "|" +srList.get(1).getMonth()+ "|" + srList.get(1).getSem()+ "|" + srList.get(1).getId());
							service.setMarksheetDetailRecord3(srList.get(2).getYear()+ "|" +srList.get(2).getMonth()+ "|" + srList.get(2).getSem()+ "|" + srList.get(2).getId());
							service.setMarksheetDetailRecord4(srList.get(3).getYear()+ "|" +srList.get(3).getMonth()+ "|" + srList.get(3).getSem()+ "|" + srList.get(3).getId());
							service.setMarksheetDetailRecord5(srList.get(4).getYear()+ "|" +srList.get(4).getMonth()+ "|" + srList.get(4).getSem()+ "|" + srList.get(4).getId());
							service.setMarksheetDetailRecord6(srList.get(5).getYear()+ "|" +srList.get(5).getMonth()+ "|" + srList.get(5).getSem()+ "|" + srList.get(5).getId());
							service.setMarksheetDetailRecord7(srList.get(6).getYear()+ "|" +srList.get(6).getMonth()+ "|" + srList.get(6).getSem()+ "|" + srList.get(6).getId());
							service.setMarksheetDetailRecord8(srList.get(7).getYear()+ "|" +srList.get(7).getMonth()+ "|" + srList.get(7).getSem()+ "|" + srList.get(7).getId());

				        
				        } catch (Exception e) {  
							
						}
					}	 
			
				service.setIsMobile(true);
				service.setProductType(request.getSession().getAttribute("productType").toString());
				//service.setProductType("MBAWX");
				System.out.println("==================sr:-"+service);
				
				request.getSession().setAttribute("userId",sapId);
				request.getSession().setAttribute("trackId",requestId);
				request.getSession().setAttribute("amount", service.getAmount());
				request.getSession().setAttribute("device", service.getDevice());
				request.getSession().setAttribute("sr",service);
				String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
				student.setProgramForHeader(programForHeader);
				request.getSession().setAttribute("student_studentportal",student); 
				//////////////////////////////////////////////////////////
				// logic for fee receipt start
				if("Courier".equals(service.getModeOfDispatch()) && service.getModeOfDispatch()!=null){
					service.setCourierAmount(100+"");
				}else{ 
					service.setCourierAmount(0+"");   
				}
				boolean isCertificate = student.isCertificateStudent();
				String courierAmount = isCertificate ? generateAmountBasedOnCriteria(service.getCourierAmount(),"GST") : service.getCourierAmount();
				request.getSession().setAttribute("courierAmount", courierAmount);
								
				if(service.getServiceRequestType().equalsIgnoreCase("Issuance of Marksheet") || 
						service.getServiceRequestType().equalsIgnoreCase("Issuance of Gradesheet")	) {
				
						
						ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(service,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
						//System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
		//				System.out.println(courierAmount);
		//				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
						request.getSession().setAttribute("marksheetDetailAndAmountToBePaidList", marksheetDetailAndAmountToBePaidList);
				}
				//logic for fee receipt end
				////////////////////////////////////////////////////////
				
				if("http://localhost:8080/".equals(SERVER_PATH)) {
					response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none;");
					response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
				} else {
					response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
					response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
				}
				
				
				//System.out.println("======>>>> request.getParameter(\"paymentOption\") : " + paymentOptionName);
				if(paymentOptionName.equals("hdfc")) {
					request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);
					request.getSession().setAttribute("paymentOption","hdfc");
					fillPaymentParametersInMap(model, student, service);
					return new ModelAndView(new RedirectView("/studentportal/pay"), model);
				}
				
				String paymentOption = paymentOptionName; 
				//System.out.println("-----===============>>>>>> paymentOption : " + paymentOption);

				ModelAndView mv = new ModelAndView("jsp/"+paymentOption + "Pay");	//set payment jsp file name;


				//PaymentHelper paymentHelper = new PaymentHelper();
				String checkSum = paymentHelper.generateCommonCheckSum(service, student, requestId,paymentOption);
				//System.out.println("checksum : " + checkSum); 
				if(checkSum != "true") {	
					//setError(request, "Error: " + checkSum);
					 return new ModelAndView("redirect:" + "/m/paymentResponse?status=error&message=Error: " + checkSum);
					//return checkMarksheetHistory(service, request);
				}
				System.out.println("===============>>>>>>>>>>>> payment Option : " + paymentOption);
				
				
				request.getSession().setAttribute("paymentOption",paymentOption);
				//System.out.println("SR Request");

				//System.out.println(request.getSession().getAttribute("sr"));
				
//				String sapid = (String) request.getSession().getAttribute("userId");
//				String trackId = (String) request.getSession().getAttribute("trackId");
//				ServiceRequest sr = (ServiceRequest) request.getSession().getAttribute("sr");
				
				
				mv = paymentHelper.setCommonModelData(mv, service, student, requestId,paymentOption);
//				mv.addObject("CALLBACK_URL",SR_RETURN_URL_MOBILE);
				return mv;
	}
	
	public void fillPaymentParametersInMap(ModelMap model, StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String address = student.getAddress();
		//System.out.println("Address Before Substring-->" + address);

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		//System.out.println("Address after Substring-->" + address);
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", sr.getTrackId());
		model.addAttribute("amount",sr.getAmount());
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", sr.getServiceRequestType() + ":" + student.getSapid());// This
		model.addAttribute("orderId",sr.getOrderId());																							// should
																									// be
																									// used
																									// in
																									// response
		if(sr.getIsMobile()) {
			model.addAttribute("return_url", SR_RETURN_URL_MOBILE);
		}else {
			model.addAttribute("return_url", SR_RETURN_URL);	
		}
		model.addAttribute("name", student.getFirstName() + " " + student.getLastName());
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", sr.getSapId());
	}

}
