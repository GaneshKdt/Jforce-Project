package com.nmims.stratergies.impl;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
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

@Service("shareAsPostLinkedInCertificateStrategyMBAWX")
public class ShareAsPostLinkedInCertificateStrategyMBAWX {
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

	public ServiceRequestStudentPortal shareAsPostLinkedInCertifiacteStrategy(ServiceRequestStudentPortal sr) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		PassFailBean pf = new PassFailBean();
		pf.setSapid(sr.getSapId());

		LinkedInAddCertToProfileBean linkedInAddCertToProfileBean = serviceRequestDao.getLinkedInProfile(sr.getSapId());

		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		HttpEntity<PassFailBean> entity4 = new HttpEntity<PassFailBean>(pf, headers);

		LinkedInAddCertToProfileBean linkedInBean = restTemplate.postForObject(
			SERVER_PATH + "exam/m/getSingleStudentCertificateMBAWX", entity4, LinkedInAddCertToProfileBean.class);

		String shareText = "In my quest to become a better professional, I have successfully completed a program from NMIMS Global Access School for Continuing Education - India\'s Largest Ed-Tech University #NMIMSGlobalAccess #ContinuingEducation";

		URL url = new URL(linkedInBean.getCertUrl());

		String cert_path = CERTIFICATES_PATH + FilenameUtils.getName(url.getPath());

		Path targetPath = new File(CERTIFICATES_PATH + File.separator + FilenameUtils.getName(url.getPath())).toPath();
		Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
		linkedInManager.registerUploadPDF(linkedInAddCertToProfileBean.getSapId(),
			linkedInAddCertToProfileBean.getPersonId(), cert_path,
			linkedInAddCertToProfileBean.getAccess_token(), shareText);

		try {
			File fileToDelete = new File(cert_path);
			fileToDelete.delete();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		sr.setReturn_url(SERVER_PATH+"studentportal/response?success="+true+"&productType=MBAWX");
	
		return sr;

	}

}
