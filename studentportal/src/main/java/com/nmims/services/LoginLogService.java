package com.nmims.services;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nmims.beans.LoginLogBean;
import com.nmims.daos.LoginLogDAO;

@Service
public class LoginLogService {

	@Autowired
	LoginLogDAO loginDAO;

	private static final Logger logger = LoggerFactory.getLogger("login");
	
	@Async
	public void insertLoginDetails( String userId, HttpServletRequest request ) {

		Date before = new Date();
		
		LoginLogBean loginDetails = new LoginLogBean();
		
		try {

			loginDetails.setSapid( userId );
			loginDetails.setIpAddress( getClientIp(request) ) ;
			loginDetails.setOperatingSystem( getClientOs(request) );
			loginDetails.setBrowserDetails( getClientBrowserDetails(request) );
			
			loginDAO.insertLoginDetails(loginDetails);
			
		} catch (Exception e) {

			logger.info("in insertLoginDetails : got exception : "+e.getMessage() );
			
		}

		Date after = new Date();
		logger.info("in insertLoginDetails got sapid : "+userId+ 
				" time taken for method execution : "+ (after.getTime() - before.getTime() ) );
		
	}

	public LoginLogBean getLoginLogs( String sapid ) {

		Date before = new Date();
		
		LoginLogBean bean = new LoginLogBean();

		try {
			bean = loginDAO.getMailerTriggredOn( sapid );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in getMailerTriggredOn fetching details for sapid : "+sapid + " got error : "+e.getMessage());
		}

		try {
			bean = loginDAO.getLoginLogs( bean );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in getLastLoginLogs fetching details for sapid : "+sapid + " got error : "+e.getMessage());
		}
		
		Date after = new Date();
		logger.info("in getLoginLogs fetching details for sapid : " + sapid + 
				" time taken for method execution : "+ (after.getTime() - before.getTime() ) );
		
		return bean;
		
	}
	private static String getClientIp(HttpServletRequest request) {
		
		String remoteAddr = "";
		
		remoteAddr = request.getHeader("X-FORWARDED-FOR");
		if (remoteAddr == null || "".equals(remoteAddr)) {
			remoteAddr = request.getRemoteAddr();
		}
		
		return remoteAddr;
		
	}

	public static String getClientOs(HttpServletRequest request) {
		
		String  browserDetails  =   request.getHeader("User-Agent");
		String  userAgent       =   browserDetails;
		String os = "";

		if (userAgent.toLowerCase().indexOf("windows") >= 0 ) {
			os = "Windows";
		} else if(userAgent.toLowerCase().indexOf("mac") >= 0) {
			os = "Mac";
		} else if(userAgent.toLowerCase().indexOf("x11") >= 0) {
			os = "Unix";
		} else if(userAgent.toLowerCase().indexOf("android") >= 0) {
			os = "Android";
		} else if(userAgent.toLowerCase().indexOf("iphone") >= 0) {
			os = "IPhone";
		}else{
			os = "UnKnown, More-Info: "+userAgent;
		}

		return os;
		
	}

	public static String getClientBrowserDetails(HttpServletRequest request) {

		String  browserDetails  =   request.getHeader("User-Agent");
		String  userAgent       =   browserDetails;
		String  user            =   userAgent.toLowerCase();

		String browser = "";

		if (user.contains("msie")) {
			String substring=userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
			browser=substring.split(" ")[0].replace("MSIE", "IE")+"-"+substring.split(" ")[1];
		} else if (user.contains("safari") && user.contains("version")) {
			browser=(userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
		}else if ( user.contains("opr") || user.contains("opera")) {
			if(user.contains("opera"))
				browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]+"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
			else if(user.contains("opr"))
				browser=((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
		} else if (user.contains("chrome")) {
			browser=(userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
		} else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)  || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1) ) {
			//browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
			browser = "Netscape-?";

		} else if (user.contains("firefox")) {
			browser=(userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
		} else if(user.contains("rv")) {
			browser="IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
		} else if(user.contains("rv:11.0")) {
			String substring=userAgent.substring(userAgent.indexOf("rv")).split("\\)")[0];
			browser=substring.split(":")[0].replace("rv", "IE")+"-"+substring.split(":")[1];

		}else {
			browser = "UnKnown, More-Info: "+userAgent;
		}

		return browser;
	}

}
