package com.nmims.helpers;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nmims.beans.PaymentDetails;
import com.nmims.beans.PaymentGatewayConstants;
import com.nmims.beans.PaymentInitiationModelBean;
import com.nmims.beans.SalesForceAccessToken;
import com.nmims.beans.SalesForceAccessTokenRequest;
import com.nmims.beans.SalesForceAccessTokenResponse;
import com.nmims.beans.SalesForceCreatePackage;
import com.nmims.beans.SalesForceCreatePackageRequest;
import com.nmims.beans.SalesForceCreatePackageResponse;
import com.nmims.beans.SalesForceGetPackagesResponse;
import com.nmims.beans.SalesForceGetRequest;
import com.nmims.beans.SalesForcePackage;
import com.nmims.beans.SalesForcePackageList;
import com.nmims.beans.SalesForceSubmitRegistration;
import com.nmims.beans.SalesForceSubmitRegistrationRequest;
import com.nmims.beans.SalesForceSubmitRegistrationResponse;
import com.nmims.beans.SalesForceSubmitRegistrationResponseObject;

@Component
public class SalesForceHelper_Packages extends RestCallHelper{


	@Autowired
	CheckSumHelper checkSumHelper;
	
	@Value( "${SFDC_AUTH_TOKEN}" )
	private String SFDC_AUTH_TOKEN;
	
	@Value( "${SFDC_BASE_URL}" )
	private String SFDC_BASE_URL;	
	
	@Value( "${SFDC_GET_PACKAGES}" )
	private String SFDC_GET_PACKAGES;
	
	@Value( "${SFDC_SUBMIT_REGISTRATION}" )
	private String SFDC_SUBMIT_REGISTRATION;

	@Value( "${SFDC_CHECKSUM_MERCHANT_KEY}" )
	private String MERCHANT_KEY;

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${SFDC_CREATE_PACKAGE}" )
	private String SFDC_CREATE_PACKAGE;
	
	@Value( "${SFDC_TOKEN_EMAIL}" )
	private String SFDC_TOKEN_EMAIL;

	@Value( "${SFDC_TOKEN_PASSWORD}" )
	private String SFDC_TOKEN_PASSWORD;
	
	Gson gson = new Gson();
	DataValidationHelpers validationHelpers = new DataValidationHelpers();

	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
 
	/*
	 * 	Get payment url from salesforce
	 */
		public void getPaymentURL(PaymentInitiationModelBean paymentInitiationModelBean) {
			
			//get token from salesforce.			
			String token = getAuthToken();
			if(token.equals("No Token")) {
				paymentInitiationModelBean.setErrorMessage("Error reaching payment gateway!");
				return;
			}
			generatePaymentURLFromSalesForce(paymentInitiationModelBean, token);
			return;
		}
	
		private void generatePaymentURLFromSalesForce(PaymentInitiationModelBean paymentInitiationModelBean, String token) {
			
				//if the token generation was successful, start forming the request
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				
				SalesForceSubmitRegistration salesForceSubmitRegistration = new SalesForceSubmitRegistration();
				
				//set the "request" parameter
				SalesForceSubmitRegistrationRequest salesForceSubmitRegistrationRequest = new SalesForceSubmitRegistrationRequest();
				if(paymentInitiationModelBean.getSource().equals(PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL_WEB)) {
					salesForceSubmitRegistrationRequest.setSuccessURL(SERVER_PATH + "careerservices/paymentResponseSuccess");
					salesForceSubmitRegistrationRequest.setFailureURL(SERVER_PATH + "careerservices/paymentResponseFailure");
				}else {
					salesForceSubmitRegistrationRequest.setSuccessURL(SERVER_PATH + "careerservices/paymentResponseSuccessMobile");
					salesForceSubmitRegistrationRequest.setFailureURL(SERVER_PATH + "careerservices/paymentResponseFailureMobile");
				}
				salesForceSubmitRegistrationRequest.setPackageId(paymentInitiationModelBean.getSalesForcePackageId());
				salesForceSubmitRegistrationRequest.setPaymentInitializationId(paymentInitiationModelBean.getPaymentInitializedId());

				//form the request
				salesForceSubmitRegistration.setAuthToken(token);
				salesForceSubmitRegistration.setSapId(paymentInitiationModelBean.getSapid());
				salesForceSubmitRegistration.setRequest(salesForceSubmitRegistrationRequest);

				//catch errors
				try {
					ResponseEntity<String> submitRegistrationResponseEntity = getResponse(gson.toJson(salesForceSubmitRegistration), SFDC_BASE_URL + SFDC_SUBMIT_REGISTRATION);
					
					//if response code says no error, continue
					if (submitRegistrationResponseEntity.getStatusCode() == HttpStatus.OK) {
					
						SalesForceSubmitRegistrationResponse submitRegistrationResponse = gson.fromJson(submitRegistrationResponseEntity.getBody(), SalesForceSubmitRegistrationResponse.class);
						
						if(submitRegistrationResponse.getErrKey() != null) {
							if(submitRegistrationResponse.getErrKey().equals("APEX ERROR")) {
								paymentInitiationModelBean.setErrorMessage("Apex Error!");
								return;
							}
						}

						if(submitRegistrationResponse.getErrorCode() != null && submitRegistrationResponse.getErrorCode().equals("422")) {
							paymentInitiationModelBean.setErrorMessage("Invalid sapid!");
							return;
						}
						
						//cast response packages to an array
						SalesForceSubmitRegistrationResponseObject response = submitRegistrationResponse.getResponse();
						if(response.getStatus() != null && response.getPaymentUrl() != null && response.getStatus().equals("success")) {
							//redirect user to the generated payment url
							paymentInitiationModelBean.setSalesForceURL(response.getPaymentUrl());
							paymentInitiationModelBean.setPaymentInitializedId(response.getPaymentInitializationId());
							paymentInitiationModelBean.setCanStartPayment(true);
							return;
						}
						
					}
				}catch(Exception e){
					paymentInitiationModelBean.setErrorMessage("Exception: " + e);
					logger.info("exception : "+e.getMessage());
					return;
				}
			
			return;
		}
	
	/*
	 * 	Get the auth token from salesforce
	 */
		private String getAuthToken() {
			SalesForceAccessTokenRequest request = new SalesForceAccessTokenRequest();
			request.setEmail(SFDC_TOKEN_EMAIL);
			request.setPassword(SFDC_TOKEN_PASSWORD);
			request.setRequest("auth_token");
			request.setSource("Career Services");
	
			// send request and parse result
			ResponseEntity<String> tokenResponseEntity = getResponse(gson.toJson(request) , SFDC_BASE_URL + SFDC_AUTH_TOKEN);
			
			String returnValue = "No Token";
			if (tokenResponseEntity.getStatusCode() == HttpStatus.OK) {
				try{
					SalesForceAccessTokenResponse tokenResponse = gson.fromJson(tokenResponseEntity.getBody(), SalesForceAccessTokenResponse.class);
					//cast response packages to an array
	
					if(!validationHelpers.checkIfStringEmptyOrNull(tokenResponse.getErrKey())) {
						if(tokenResponse.getErrKey().equals("APEX ERROR")) {
							return "No Token";
						}
					}
					
					SalesForceAccessToken response = tokenResponse.getResponse();
					
					if(!validationHelpers.checkIfStringEmptyOrNull(response.getStatus())
							&& !validationHelpers.checkIfStringEmptyOrNull(response.getToken())
							&& response.getStatus().equals("success")) {
						returnValue = response.getToken();
					}
				}catch(HttpServerErrorException serverError) {
					logger.info(serverError.getMessage());
				}catch(HttpClientErrorException clientError) {
					logger.info(clientError.getMessage());
				}catch(UnknownHttpStatusCodeException unk) {
					logger.info(unk.getMessage());
				}
			}
			return returnValue;
		}

		
/*
 * 	-------------- Payment Success Response --------------
 */
	
	/*
	 * 	Checks the payment response from salesforce
	 */
		public void managePaymentResponse(HttpServletRequest requestParams, PaymentDetails paymentResponse) {
			checkSuccessResponse(requestParams, paymentResponse);
			return;
		}	
		/*
		 * 	Check if the success response is true
		 */
			private void checkSuccessResponse(HttpServletRequest requestParams, PaymentDetails paymentResponse) {
				checkSumHelper.verifyChecksum(requestParams, paymentResponse);
				getPaymentDetails(requestParams, paymentResponse);
				String paymentStatus = paymentResponse.getStatus();
				if(!(paymentStatus != null && (paymentStatus.equalsIgnoreCase(PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_APPROVED) || paymentStatus.equalsIgnoreCase(PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_MADE)))) {
					paymentResponse.setCheckSumStatus(false);
					paymentResponse.setReasonForFail("Invalid Response");
					paymentResponse.setMessage("Data mismatch!");
				}
				return;
			}
			
		public void getPaymentDetails(HttpServletRequest requestParams, PaymentDetails paymentResponse) {
			//Add all request parameters to object	
			for (Entry<String, String[]> requestParamsEntry : requestParams.getParameterMap().entrySet()) {
				String key = requestParamsEntry.getKey();
				String value = requestParamsEntry.getValue()[0];

				switch(key) {
					case "packageId": 
						paymentResponse.setPackageId(value);
						break;
					case "message": 
						paymentResponse.setMessage(value);
						break;
					case "paymentTrackId": 
						paymentResponse.setMerchantTrackId(value);
						break;
					case "sapid": 
						paymentResponse.setSapid(value);
						break;
					case "status": 
						paymentResponse.setStatus(value);
						break;
					case "paymentInitializationId": 
						paymentResponse.setPaymentInitializationId(value);
						break;
					case "CHECKSUMHASH": 
						paymentResponse.setCheckSumHash(value);
						break;
				}
			}
		}
		
/*
 * 	------------------------ END -------------------------		
 */

/*
 * 	-------------- Get package prices --------------
 */
	public List<SalesForcePackage> getPackagePrices(){

		SalesForceGetRequest request = new SalesForceGetRequest();
		request.setAuthToken(getAuthToken());
		request.setRequest("packages");
		
		// send request to salesforce
		ResponseEntity<String> salesForceResponse = getResponse(gson.toJson(request), SFDC_BASE_URL + SFDC_GET_PACKAGES);

		if (salesForceResponse.getStatusCode() == HttpStatus.OK) {
			
			String jsonResponse = salesForceResponse.getBody();
			SalesForceGetPackagesResponse salesForceGetPackagesResponse = gson.fromJson(jsonResponse, SalesForceGetPackagesResponse.class);
			
			SalesForcePackageList packageList = salesForceGetPackagesResponse.getPackages();
			
			return packageList.getPackageList();
		} else if (salesForceResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			logger.info("Unauthorized access token");
		}
		return null;
	}
/*
 * 	----------------------- END --------------------------
 */
	
/*
 * 	------------- Create package on salesforce -------------
 */

	public String addPackageToSalesForce(String packageName, String type){
		return createPackageOnSalesForce(packageName, type);
	}
	private String createPackageOnSalesForce(String packageName, String type){

		SalesForceCreatePackage request = new SalesForceCreatePackage();
		request.setAuthToken(getAuthToken());
		
		SalesForceCreatePackageRequest createPackageRequest = new SalesForceCreatePackageRequest();
		createPackageRequest.setPackageName(packageName);
		createPackageRequest.setType(type);
		request.setRequest(createPackageRequest);

		ResponseEntity<String> salesForceResponse = getResponse(gson.toJson(request), SFDC_BASE_URL + SFDC_CREATE_PACKAGE);

		if (salesForceResponse.getStatusCode() == HttpStatus.OK) {
			
			String jsonResponse = salesForceResponse.getBody();
			SalesForceCreatePackageResponse createPackageResponse = gson.fromJson(jsonResponse, SalesForceCreatePackageResponse.class);
			
			if(createPackageResponse.getErrKey() != null && createPackageResponse.getErrKey().equals("APEX ERROR")) {
				return null;
			}

			if(
					!validationHelpers.checkIfStringEmptyOrNull(createPackageResponse.getErrorCode()) &&
					!validationHelpers.checkIfStringEmptyOrNull(createPackageResponse.getMessage()) &&
					createPackageResponse.getMessage().equalsIgnoreCase("ok")
			) {
				
				return createPackageResponse.getResponse().getPackages().getPackageId();
			}
			return null;
		} else if (salesForceResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			logger.info("Unauthorized access token");
		}
		return null;
	}
		
/*
 * 	----------------- END ----------------------
 */
		
}
