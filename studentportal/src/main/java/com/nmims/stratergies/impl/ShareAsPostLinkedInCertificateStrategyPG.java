package com.nmims.stratergies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.LinkedInManager;
import com.nmims.stratergies.ShareAsPostCertificateStratergyInterface;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

@Service("shareAsPostLinkedInCertificateStrategyPG")
public class ShareAsPostLinkedInCertificateStrategyPG implements ShareAsPostCertificateStratergyInterface {

	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	@Value("${LINKED_IN_CLIENT_SECRET}")
	private String LINKED_IN_CLIENT_SECRET;
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;
	@Value("${LINKED_IN_REDIRECT_URI}")
	private String LINKED_IN_REDIRECT_URI;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;

	@Autowired
	LinkedInManager linkedInManager;

	@Autowired
	private ServiceRequestDao serviceRequestDao;

	public ServiceRequestStudentPortal shareAsPostLinkedInCertifiacteStrategy(ServiceRequestStudentPortal sr) {

		/*
		 *This sample code will make a request to LinkedIn's API to retrieve and print
		 *out some
		 *basic profile information for the user whose access token you provide.
		 */
		
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		PassFailBean pf = new PassFailBean();
		pf.setSapid(sr.getSapId());

		LinkedInAddCertToProfileBean linkedInAddCertToProfileBean = serviceRequestDao
				.getLinkedInProfile(sr.getSapId());
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		HttpEntity<PassFailBean> entity4 = new HttpEntity<PassFailBean>(pf, headers);

		LinkedInAddCertToProfileBean linkedInBean = restTemplate.postForObject(
				SERVER_PATH + "exam/m/getSingleStudentCertificate", entity4, LinkedInAddCertToProfileBean.class);

		String shareText = "In my quest to become a better professional, I have successfully completed a program from "
				+ "NMIMS Global Access School for Continuing Education - India\'s Largest Ed-Tech University "
				+ "#NMIMSGlobalAccess #ContinuingEducation";

		
		try {
			linkedInManager.registerUploadPDF_V2( linkedInAddCertToProfileBean.getSapId(),
					linkedInAddCertToProfileBean.getPersonId(), linkedInBean.getCertUrl(),
					linkedInAddCertToProfileBean.getAccess_token(), shareText );
		} catch ( Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}

		sr.setReturn_url(SERVER_PATH+"studentportal/response?success="+true+"&productType=PG");
		
		return sr;
	}

}