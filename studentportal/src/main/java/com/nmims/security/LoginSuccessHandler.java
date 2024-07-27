//package com.nmims.security;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import com.nmims.beans.CaptchaResponse;
//import com.nmims.daos.LDAPDao;
//
//@Component
//public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
//
//	@Autowired
//	LDAPDao ldapDao ;
//	
//	@Value("${WEB_RECAPTCHA_SECRET_KEY}")
//	private String WEB_RECAPTCHA_SECRET_KEY;
//	
//	@Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//    		//FilterChain chain,
//            Authentication authentication) throws IOException, ServletException {
//    	System.out.println("IN onAuthenticationSuccess() : ");
//    	boolean isCaptchaValidate = isValidCaptcha(request, response, request.getParameter("g-recaptcha-response"));
//    	
//    	if (isCaptchaValidate) {
//    		getRedirectStrategy().sendRedirect(request, response, "/authenticate");
//		}else {
//			getRedirectStrategy().sendRedirect(request, response, "/login");
//		}
//    		
//        
//    
//    }
//	
//	public boolean isValidCaptcha(HttpServletRequest request, HttpServletResponse response, String captcha) {
//		String url= "https://www.google.com/recaptcha/api/siteverify";
//		String params = "?secret=" + WEB_RECAPTCHA_SECRET_KEY + "&response=" + captcha;
//		String completeUrl = url + params;
//		
//		System.out.println("completeUrl "+completeUrl);
//		RestTemplate restTemplate = new RestTemplate();
//		CaptchaResponse captchaResponse = restTemplate.postForObject(completeUrl, null, CaptchaResponse.class);
//		System.out.println("response "+response);
//		System.out.println("response.isSuccess() "+captchaResponse.isSuccess());
//		return captchaResponse.isSuccess();
//	}
//}
