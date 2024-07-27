package com.nmims.services;

import java.util.List;

import com.nmims.beans.ServiceRequestBean;

public interface ServiceRequestDocumentService {
	
	public List<ServiceRequestBean> getMySRDocumentsFromSapId(String sapid)throws Exception;
}
