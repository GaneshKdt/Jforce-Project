package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.services.OpenBadgesService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/m")
public class OpenBadgesRESTController {
	
	@Autowired
	private OpenBadgesService openBadgesService;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	
	@Value("${LINKED_IN_CLIENT_SECRET}")
	private String LINKED_IN_CLIENT_SECRET;
	
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;
	
	@Value("${LINKED_IN_IMAGE_POST_REDIRECT_URI}")
	private String LINKED_IN_IMAGE_POST_REDIRECT_URI;
//	Mobile API START
	
	@PostMapping(value = "/myBadges")
	public ResponseEntity<OpenBadgesUsersBean> getBadgesForStudentMobile( @RequestBody OpenBadgesUsersBean usersBean ) {
		OpenBadgesUsersBean response =  new OpenBadgesUsersBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response = openBadgesService.getMyBadgeList(usersBean.getSapid(), usersBean.getConsumerProgramStructureId());
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/badgeDetails")
	public ResponseEntity<OpenBadgesIssuedBean> getBadgeDetailsMobile( @RequestBody OpenBadgesUsersBean usersBean ) {
		OpenBadgesIssuedBean response =  new OpenBadgesIssuedBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response = openBadgesService.getBadgesDetails(usersBean.getUniquehash(), usersBean.getBadgeId(), usersBean.getSapid(), usersBean.getAwardedAt());
			
			LinkedInAddCertToProfileBean linkedInCredentials = new LinkedInAddCertToProfileBean();
			
			linkedInCredentials.setCode(LINKED_IN_CODE);
			linkedInCredentials.setClient_id(LINKED_IN_CLIENT_ID);
			linkedInCredentials.setClient_secret(LINKED_IN_CLIENT_SECRET);
			linkedInCredentials.setScope(LINKED_IN_SCOPE);
			linkedInCredentials.setRedirect_uri(SERVER_PATH + LINKED_IN_IMAGE_POST_REDIRECT_URI);
			
			response.setLinkedInCredentials(linkedInCredentials);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value = "/claimedMyBadge")
	public ResponseEntity<Integer> claimedMyBadgeMobile( @RequestBody OpenBadgesIssuedBean issuedBean ) {
		Integer response = 0 ;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			 openBadgesService.claimedMyBadge(issuedBean.getUniquehash());
			 response = 1; 
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/reClaimedRevokedMyBadge")
	public ResponseEntity<Integer> reClaimedRevokedMyBadgeMobile( @RequestBody OpenBadgesIssuedBean issuedBean ) {
		Integer response = 0 ;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			 openBadgesService.reclaimedRevokedMyBadge(issuedBean.getUniquehash());
			 response = 1; 
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/revokedMyBadge")
	public ResponseEntity<Integer> revokedMyBadgeMobile( @RequestBody OpenBadgesIssuedBean issuedBean ) {
		Integer response =  0;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			 openBadgesService.revokedMyBadge(issuedBean.getUniquehash());
			 response = 1;
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	Mobile API END
	
	
}
