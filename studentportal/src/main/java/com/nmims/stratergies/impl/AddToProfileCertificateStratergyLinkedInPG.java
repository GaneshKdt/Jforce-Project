package com.nmims.stratergies.impl;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.stratergies.AddToProfileCertificateStratergyInterface;

@Service("addToProfileCertificateStratergyLinkedInPG")
public class AddToProfileCertificateStratergyLinkedInPG  implements AddToProfileCertificateStratergyInterface {

	@Value("${LINKED_IN_ORGANIZATION_ID}")
	private String LINKED_IN_ORGANIZATION_ID;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	
	@Override
	public ServiceRequestStudentPortal shareCertificate(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
				HttpHeaders headers =  new HttpHeaders();
				headers.add("Accept", "application/json");
				
					RestTemplate restTemplate = new RestTemplate();	
					
					JSONObject studentObject = new JSONObject();
					
					PassFailBean pf = new PassFailBean();
					pf.setSapid(sr.getSapId()); 
					System.out.println(pf.getSapid());
					   // headers.setContentType(MediaType.APPLICATION_JSON);
						headers.add("Accept", "application/json");
						headers.add("Content-Type", "application/json");
					HttpEntity<PassFailBean> entity = new HttpEntity<PassFailBean>(pf,headers);

					LinkedInAddCertToProfileBean linkedInBean = restTemplate.postForObject(SERVER_PATH + "exam/m/getSingleStudentCertificate", entity ,LinkedInAddCertToProfileBean.class);
		            
					String url = "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME&name="
							+ linkedInBean.getName() 
							+ "&organizationId=" +  LINKED_IN_ORGANIZATION_ID 					
							+ "&issueYear="
							+ linkedInBean.getIssueYear()
							+ "&issueMonth="
							+ linkedInBean.getIssueMonth()
							+ "&certUrl="
							+ linkedInBean.getCertUrl() 
							+ "&certId=" + Integer.toHexString(Integer.parseInt(linkedInBean.getConsumerProgramStructureId())) + linkedInBean.getConsumerProgramStructureId();
					
					//System.out.println(url);
					sr.setReturn_url(url);
					
				return sr;
	}

}
