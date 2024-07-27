package com.nmims.services.impl;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;

import com.nmims.beans.BulkTranscriptGenerationBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.StudentExamBean;

public interface TranscriptGenerationServiceInterface {
	public List<String> convertCommaSepratedToList(String commaSeprated);
	
	
	public String generateCommaSepratedErrorList(List<String> allList,List<String> errorList);
	
	public String generateSingleTranscript(StudentExamBean bean) throws Exception;
	
	public List<String> addingSuccessSridsInList(List<String> listOfSrId) throws Exception;
	
	public BulkTranscriptGenerationBean generateBulkTranscript(StudentExamBean Bean) throws Exception;
	
	public void checkForServiceRequestIdPresent(String serviceRequestIdList) throws Exception ;
	
	
	
	public List<MarksheetBean> checkDataOnSrId(String successList) throws Exception;

}