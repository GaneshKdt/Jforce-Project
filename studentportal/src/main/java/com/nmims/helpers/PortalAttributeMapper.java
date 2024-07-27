package com.nmims.helpers;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.ELearnResourcesStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.dto.LoginSSODto;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class PortalAttributeMapper 
{
	//@Value( "${SERVER_PATH}" )
	private static String SERVER_PATH = "http://localhost:8080/";
	
	public static void populateForAdminStudentSSOLoader(HttpServletRequest request) {
		
		LoginSSODto detail = new LoginSSODto();
		String encryptedSapId =  "";
		boolean loadCS = false;
	//	String str = "";
		
		try{
			detail.setUserId((String)request.getSession().getAttribute("userId"));
			detail.setStudent((StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal"));
			detail.setAnnouncements((ArrayList<AnnouncementStudentPortalBean>)request.getSession().getAttribute("announcementsPortal"));
			detail.setUserBean((UserAuthorizationStudentPortalBean)request.getSession().getAttribute("userAuthorization_studentportal"));
			detail.setPersonDetails((PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal"));
			detail.setApplicableSubjects((HashMap<String, String>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal"));
			detail.setRegData((StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal"));
			detail.setHarvard((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("harvard_details"));
			detail.setStukent((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("stukent_details"));
			detail.setValidityExpired((String)request.getSession().getAttribute("validityExpired"));
			detail.setIsLoginAsLead((String)request.getSession().getAttribute("isLoginAsLead"));
			if(detail.getStudent() != null){
				detail.setCurrentOrder((double)request.getSession().getAttribute("current_order"));
				detail.setMaxOrderWhereContentLive((double)request.getSession().getAttribute("acadContentLiveOrder"));
				detail.setRegOrder((double)request.getSession().getAttribute("reg_order"));
				detail.setAcadSessionLiveOrder((double)request.getSession().getAttribute("acadSessionLiveOrder"));
				detail.setCurrentSemPSSId((List<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal"));
				detail.setEarlyAccess((String)request.getSession().getAttribute("earlyAccess"));
				detail.setLiveSessionPssIdAccess((List<Integer>)request.getSession().getAttribute("liveSessionPssIdAccess_studentportal"));
				detail.setFeatureViseAccess((Map<String, Boolean>)request.getSession().getAttribute("CSFeatureAccess"));
				detail.setConsumerProgramStructureHasCSAccess((boolean)request.getSession().getAttribute("consumerProgramStructureHasCSAccess"));
				detail.setCourseraAccess((boolean)request.getSession().getAttribute("courseraAccess"));
				}else{
		            detail.setCsAdmin((Map<String, Boolean>)request.getSession().getAttribute("csAdmin"));
		    }
		
		}catch(Exception e) {
			//e.printStackTrace();
		}
		try {
		boolean isLead =  Boolean.parseBoolean( request.getSession().getAttribute("isLoginAsLead").toString()  );
		if(isLead)
			encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)request.getSession().getAttribute("emailId"))); 
		else
			encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)request.getSession().getAttribute("userId"))); 
		
		//load CS set variable
		
		if(request.getSession().getAttribute("consumerProgramStructureHasCSAccess") != null && (boolean) request.getSession().getAttribute("consumerProgramStructureHasCSAccess") ){
			loadCS = true;
		} else {
			// check for admin user
			boolean isExternallyAffiliatedForProducts = false;
			boolean isCSSpeaker = false;
			boolean isCSAdmin = false;
			boolean isCSSessionsAdmin = false;
			boolean isCSProductsAdmin = false;
			
			if(request.getSession().getAttribute("isCSSpeaker") != null){
				isCSSpeaker = (boolean) request.getSession().getAttribute("isCSSpeaker");
			}
			if(request.getSession().getAttribute("isCSAdmin") != null){
				isCSAdmin = (boolean) request.getSession().getAttribute("isCSAdmin");
			}
			if(request.getSession().getAttribute("isCSProductsAdmin") != null){
				isCSProductsAdmin = (boolean) request.getSession().getAttribute("isCSProductsAdmin");
			}
			if(request.getSession().getAttribute("isCSSessionsAdmin") != null){
				isCSSessionsAdmin = (boolean) request.getSession().getAttribute("isCSSessionsAdmin");
			}
			if(request.getSession().getAttribute("isExternallyAffiliatedForProducts") != null){
				isExternallyAffiliatedForProducts = (boolean) request.getSession().getAttribute("isExternallyAffiliatedForProducts");
			}
			loadCS = isCSSpeaker || isCSAdmin || isCSProductsAdmin || isCSSessionsAdmin || isExternallyAffiliatedForProducts;
		}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
		request.setAttribute("loadCS", loadCS );
		request.setAttribute("str", detail);
		String ltiAppSSOUrl = SERVER_PATH + "ltidemo/loginforSSO?uid="+encryptedSapId;
		String csAppSSOUrl = SERVER_PATH + "careerservices/loginforSSO?uid="+encryptedSapId;
		request.setAttribute("ltiAppSSOUrl", ltiAppSSOUrl);
		request.setAttribute("csAppSSOUrl", csAppSSOUrl );
		request.setAttribute("encryptedSapId", encryptedSapId );
		request.setAttribute("encryptedSapId", encryptedSapId );
		
	}

}
