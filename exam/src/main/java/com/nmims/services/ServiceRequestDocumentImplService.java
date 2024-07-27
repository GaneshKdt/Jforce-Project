package com.nmims.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestBean;
import com.nmims.daos.ServiceRequestDAO;

@Service("serviceRequestDocumentService")
public class ServiceRequestDocumentImplService implements ServiceRequestDocumentService{
	
	@Autowired
	ServiceRequestDAO serviceRequestDao;
	
	@Value("${SR_FILES_S3_PATH}")
	private String SR_FILES_S3_PATH;
	
	
	
	@Override
	public List<ServiceRequestBean> getMySRDocumentsFromSapId(String sapid) throws Exception {
		
		List<ServiceRequestBean> listOfSRDocumentsBasedOnSapid = new ArrayList<>();
       
    	listOfSRDocumentsBasedOnSapid = serviceRequestDao.getGeneratedSrDocuments(sapid);
    	listOfSRDocumentsBasedOnSapid.stream().forEach(list -> {
    		list.setFilePath(SR_FILES_S3_PATH + list.getFilePath());
    	});
    	
		return listOfSRDocumentsBasedOnSapid;
	}
}
