package com.nmims.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.daos.MassUploadTrackingSRDAO;
import com.nmims.dto.SrAdminUpdateDto;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.interfaces.MassUploadTrackingSRInterface;

@Service
public class MassUploadTrackingSRService implements MassUploadTrackingSRInterface{
	
	@Value("${SR_TRACKING_LIST}")
	private List<String> SR_TRACKING_LIST;
	
	@Value("${COURIER_NAME}")
	private List<String> COURIER_NAME;
	
	private static final Logger logger = LoggerFactory.getLogger(MassUploadTrackingSRService.class);
	private static final String YES="Y";
	
	@Autowired
	MassUploadTrackingSRDAO massUploadTrackingSRDAO;
	
	@Autowired
	ServiceRequestService serviceRequest;
	
	@Autowired
	MailSender mailSender;
	
    @Override
	public MassUploadTrackingSRBean saveSrExcelRecord(HttpServletRequest request, MultipartFile file) throws Exception {
    	MassUploadTrackingSRBean mssgBean = new MassUploadTrackingSRBean();
    	try {
    		String userId = (String) request.getSession().getAttribute("userId");
        	ExcelHelper excelHelper = new ExcelHelper();
        	
        	//reading records from the excel file to bean
        	MassUploadTrackingSRBean trackingBean =  excelHelper.readSRTrackingExcel(file);
        	List<MassUploadTrackingSRBean> successList = trackingBean.getSuccessList();
    		List<MassUploadTrackingSRBean> errorList = trackingBean.getErrorList();
    		
			if(errorList.size() > 0) 
				return getErrorMessage(errorList);
    		
    		//validation for tracking list
    		errorList = validateTrackingList(successList, errorList);
    		
    		if(errorList.size() > 0) 
				return getErrorMessage(errorList);
    		
    		//setting user id to each success list for createdBy and lastModifiedBy field
    		successList.forEach(bean->{bean.setCreatedBy(userId);bean.setLastModifiedBy(userId);});
    		
    		//saving the records in servicerequest_trackingrecords table and updating service request status to closed
    		mssgBean = saveAndClosedTrackingSR(userId, successList, errorList);
    	}
    	catch (Exception e) {
    		logger.info("{} due to {}", e.getMessage(), e);
			throw new IllegalArgumentException(e.getMessage());
		}
		return mssgBean;
	}

	private List<MassUploadTrackingSRBean> validateTrackingList(List<MassUploadTrackingSRBean> successList, List<MassUploadTrackingSRBean> errorList) throws Exception {
		List<MassUploadTrackingSRBean> tempErrorList = new ArrayList<MassUploadTrackingSRBean>(); 
		try {
			for(MassUploadTrackingSRBean bean : successList) {
				Integer serviceRequestId = bean.getServiceRequestId();
				String courierName = bean.getCourierName();
				Integer row = bean.getRow();
				try {
					if(!massUploadTrackingSRDAO.isSRValid(serviceRequestId,SR_TRACKING_LIST)) {
						tempErrorList.add(getTrackingErrorBean("Invalid serviceRequestId on row no : "+row));
					}
					if(!COURIER_NAME.contains(courierName)) {
						tempErrorList.add(getTrackingErrorBean("Invalid courierName on row no : "+row));
					}
				}
				catch (Exception e) {
					tempErrorList.add(getTrackingErrorBean("Incorrect records size or Data is invalid on row no : "+row));
					continue;
				}
			} 
		}
		catch (Exception e) {
			logger.info("Error in validating tracking records due to {}", e);
		}
		return tempErrorList;
	}
	
	@Transactional
	private MassUploadTrackingSRBean saveAndClosedTrackingSR(String userId, List<MassUploadTrackingSRBean> successList, List<MassUploadTrackingSRBean> errorList) throws Exception {
    	MassUploadTrackingSRBean  mssgBean = new MassUploadTrackingSRBean();
    	try {
			massUploadTrackingSRDAO.saveSrExcelRecord(successList);
			
			mssgBean = closedSRStatus(userId, successList, errorList);
		} 
    	catch (SQLException se) {
			logger.info("Failed to upload tracking records into servicerequest_trackingRecords table due to {}", se);
			throw new IllegalArgumentException("Failed to uplaod tracking records for serviceRequestId : "+ successList.stream()
			.map(bean -> String.valueOf(bean.getServiceRequestId()))
			.collect(Collectors.joining(",")));
		}
    	catch (Exception e) {
			logger.info("{} due to {}", e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
    	return mssgBean;
	}
	
	private MassUploadTrackingSRBean closedSRStatus(String userId, List<MassUploadTrackingSRBean> successList, List<MassUploadTrackingSRBean> errorList) throws Exception {
    	Integer SRClosedCount = 0;
    	Map<Integer, String> mapOfSrIdAndSapId = new HashMap<Integer, String>();
    	List<MassUploadTrackingSRBean> tempErrorList = new ArrayList<MassUploadTrackingSRBean>();
    	try {
    		List<String> srIdList = successList.stream().map(bean -> String.valueOf(bean.getServiceRequestId())).collect(Collectors.toList());
    		mapOfSrIdAndSapId  = massUploadTrackingSRDAO.getMapOfSrIdAndSapId(srIdList);
    		
    		for(MassUploadTrackingSRBean bean: successList) {
    			SrAdminUpdateDto srAdminUpdateDto = new SrAdminUpdateDto();
    			
    			srAdminUpdateDto.setId((long)bean.getServiceRequestId());
    			srAdminUpdateDto.setRequestStatus("Closed");
    			srAdminUpdateDto.setSapid(mapOfSrIdAndSapId.get(bean.getServiceRequestId()));
    			srAdminUpdateDto.setServiceRequestType(bean.getServiceRequestType());
    			try {
    				serviceRequest.updateServiceRequestStatusAndReason(srAdminUpdateDto, userId);
    				SRClosedCount++;
    			}
    			catch(Exception e) {
    				tempErrorList.add(getTrackingErrorBean("Error in closing SR for serviceRequestId : "+bean.getServiceRequestId()));
    				continue;
    			}
    		}
		} catch (Exception e) {
			logger.info("Fail to close service request due to {}", e);
			String srIds = successList.stream().map(bean -> String.valueOf(bean.getServiceRequestId())).collect(Collectors.joining(","));
			
			tempErrorList.add(getTrackingErrorBean("Error in closing SR for serviceRequestId : "+srIds));
		}
    	return getTrackingMessage(tempErrorList, successList, SRClosedCount);
	}
	
	private MassUploadTrackingSRBean getErrorMessage(List<MassUploadTrackingSRBean> errorList) {
		MassUploadTrackingSRBean errorBean = new MassUploadTrackingSRBean();
		List<MassUploadTrackingSRBean> tempList = new ArrayList<MassUploadTrackingSRBean>();
		
		String errorMssg = "";
    	errorMssg += errorList.stream().map(bean -> bean.getErrorMessage()).collect(Collectors.joining(",<br>"));
    	
    	errorBean.setErrorMessage(errorMssg);
    	tempList.add(errorBean);
    	errorBean.setErrorList(tempList);
    	
		return errorBean;
	}
	
    private MassUploadTrackingSRBean getSuccessMessage(List<MassUploadTrackingSRBean> successList, Integer srClosedCount) {
    	MassUploadTrackingSRBean tempSuccessBean = new MassUploadTrackingSRBean();
    	MassUploadTrackingSRBean successBean = new MassUploadTrackingSRBean();
    	List<MassUploadTrackingSRBean> tempSuccessList = new ArrayList<MassUploadTrackingSRBean>();
    	
    	Integer successCount = successList.size();
    	String srUploadMssg = "Successfully uploaded "+successCount+" records.";
    	String srClosedMssg = "Successfully closed "+srClosedCount+" records.";
    	String successMssg = "";
    	
    	if(srClosedCount > 0) {
    		successMssg += srUploadMssg+"<br>"+srClosedMssg;
    	}
    	else {
    		successMssg += srUploadMssg;
    	}
    	tempSuccessBean.setSuccessMessage(successMssg);
    	tempSuccessList.add(tempSuccessBean);
    	successBean.setSuccessList(tempSuccessList);
    	
    	return successBean;
    }
    
	private MassUploadTrackingSRBean getTrackingMessage(List<MassUploadTrackingSRBean> tempErrorList,
			List<MassUploadTrackingSRBean> successList, Integer sRClosedCount) {
		MassUploadTrackingSRBean mssgBean = new MassUploadTrackingSRBean();
		MassUploadTrackingSRBean errorBean = new MassUploadTrackingSRBean();
		MassUploadTrackingSRBean successBean = new MassUploadTrackingSRBean();
		
		//getting error tracking message
    	if(tempErrorList.size() > 0) {
    		errorBean = getErrorMessage(tempErrorList);
    		mssgBean.setErrorList(errorBean.getErrorList());
    		mssgBean.getErrorList().get(0).setErrorMessage(mssgBean.getErrorList().get(0).getErrorMessage()+"<br>Please close above srIds manually.");
    	}
    	
    	//getting success tracking message
    	successBean = getSuccessMessage(successList, sRClosedCount);
    	mssgBean.setSuccessList(successBean.getSuccessList());
    	
		return mssgBean;
	}
	
	private MassUploadTrackingSRBean getTrackingErrorBean(String errorMessage) {
		MassUploadTrackingSRBean errorBean = new MassUploadTrackingSRBean();
		errorBean.setErrorMessage(errorMessage);
		
		return errorBean;
	}
    
	@Override
	public List<MassUploadTrackingSRBean> getSearchTrackingRecords(MassUploadTrackingSRBean searchBean) {
		List<MassUploadTrackingSRBean>  mapSearchDetailsToList = new ArrayList<MassUploadTrackingSRBean>();
		try {
			//Get the tracking details list from servierequest_trackingrecords table
			List<MassUploadTrackingSRBean> trackingDetailsList =  massUploadTrackingSRDAO.getTrackingDetailsList(searchBean);
			
			//Get the map of srId and tracking details
			Map<Integer, MassUploadTrackingSRBean> mapOfSrIdAndTrackingDetails = getMapOfTrackingDetails(trackingDetailsList);
			
			if(!mapOfSrIdAndTrackingDetails.isEmpty()) 
			{
				List<Integer> srIdList = new ArrayList<Integer>(mapOfSrIdAndTrackingDetails.keySet());
				
				//Get list of srId and sapId, serviceRequestId from service_request table
				List<MassUploadTrackingSRBean> sRDetailsList = massUploadTrackingSRDAO.getSRDetailsList(srIdList);
				
				//Get map of srId and SR details
				Map<Integer, MassUploadTrackingSRBean> mapOfSrIdAndSRDetails = getMapOfSrIdAndSRDetails(sRDetailsList);
				
				List<String> sapIdList = mapOfSrIdAndSRDetails.values().stream().map(bean2 -> bean2.getSapId()).collect(Collectors.toList());
				
				//Get the list of sapId and studentName from students table
				List<MassUploadTrackingSRBean> StudentDetailsList = massUploadTrackingSRDAO.getStudentDetailsList(sapIdList);
				
				//Get the map of sapId and student details 
				Map<String, String> mapOfSapIdAndStudentName = getMapOfSapIdAndStudentName(StudentDetailsList);
				
				mapSearchDetailsToList = mapSearchDetailsToList(mapOfSrIdAndTrackingDetails, mapOfSrIdAndSRDetails, mapOfSapIdAndStudentName, searchBean);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			logger.info("Error in creating map of Search SR Records due to {} ", e);
		}
		return mapSearchDetailsToList;
	}

	private Map<Integer, MassUploadTrackingSRBean> getMapOfTrackingDetails(
			List<MassUploadTrackingSRBean> trackingDetailsList) {
		
		return trackingDetailsList.stream()
				.collect(Collectors.toMap(MassUploadTrackingSRBean :: getServiceRequestId, Function.identity()));
	}
	
	private Map<Integer, MassUploadTrackingSRBean> getMapOfSrIdAndSRDetails(
			List<MassUploadTrackingSRBean> sRDetailsList) {
		
		return sRDetailsList.stream().
				collect(Collectors.toMap(MassUploadTrackingSRBean :: getServiceRequestId, Function.identity()));	
	}
	
	private Map<String, String> getMapOfSapIdAndStudentName(List<MassUploadTrackingSRBean> studentDetailsList) {
		
		return studentDetailsList.stream().
				collect(Collectors.toMap(MassUploadTrackingSRBean :: getSapId, MassUploadTrackingSRBean :: getStudentName));
	}

	private List<MassUploadTrackingSRBean> mapSearchDetailsToList(Map<Integer, MassUploadTrackingSRBean> mapOfSrIdAndTrackingDetails, 
	Map<Integer, MassUploadTrackingSRBean> mapOfSrIdAndSRDetails, Map<String, String> mapOfSapIdAndStudentName, MassUploadTrackingSRBean searchBean) {
		List<MassUploadTrackingSRBean> searchTrackingList = new ArrayList<MassUploadTrackingSRBean>();
		try {
			for(Integer srId : mapOfSrIdAndSRDetails.keySet()) {
				MassUploadTrackingSRBean tempBean = new MassUploadTrackingSRBean();
				
				//filters the entry based on serviceRequestType present in searchBean
				if(StringUtils.isNotBlank(searchBean.getServiceRequestType()))
					if(!searchBean.getServiceRequestType().equals(mapOfSrIdAndSRDetails.get(srId).getServiceRequestType()))
						continue;
				
				tempBean.setServiceRequestId(srId);
				tempBean.setSapId(mapOfSrIdAndSRDetails.get(srId).getSapId());
				tempBean.setServiceRequestType(mapOfSrIdAndSRDetails.get(srId).getServiceRequestType());
				tempBean.setTrackId(mapOfSrIdAndTrackingDetails.get(srId).getTrackId());
				tempBean.setCourierName(mapOfSrIdAndTrackingDetails.get(srId).getCourierName());
				tempBean.setUrl(mapOfSrIdAndTrackingDetails.get(srId).getUrl());
				tempBean.setStudentName(mapOfSapIdAndStudentName.get(tempBean.getSapId()));
				
				searchTrackingList.add(tempBean);
			}
		} catch (Exception e) {
			logger.info("Error in mapping search details of Search SR Records to bean due to {} ", e);
		}
		return searchTrackingList;
	}

	@Override
	public boolean deleteMassUploadTrackingSRBySrId(Integer srId) {
		Integer deleteSRCount = massUploadTrackingSRDAO.deleteMassUploadTrackingBySrId(srId);
		if(deleteSRCount > 0) {
			logger.info("{} Tracking records deleted successfully for serviceRequestId : {}", deleteSRCount, srId);
			return true;
		}
		else {
			logger.info("Failed to delete Tracking record for serviceRequestId : {} ");
			return false;
		}
	}

	@Override
	public boolean updateMassUploadTrackingSR(HttpServletRequest request,MassUploadTrackingSRBean massUploadTrackingSRBean) {
		
		String userId = (String) request.getSession().getAttribute("userId");
		massUploadTrackingSRBean.setLastModifiedBy(userId);
		
		Integer updatedTrackingSRCount = massUploadTrackingSRDAO.updateMassUploadSR(massUploadTrackingSRBean);
		if(updatedTrackingSRCount > 0) {
			logger.info("{} Tracking records saved successfully for serviceRequestId : {}", updatedTrackingSRCount, massUploadTrackingSRBean.getServiceRequestId());
			return true;
		}
		else {
			logger.info("Failed to update records for serviceRequestId : {}", massUploadTrackingSRBean.getServiceRequestId());
			return false;
		}
	}

	@Override
	public MassUploadTrackingSRBean getMassUploadTrackingBySRId(Integer srId) {
		
		return massUploadTrackingSRDAO.getMassUploadTrackingBySRId(srId);
	}

	@Override
	public void notifyStudentForTrackingDetails() {
		
		//get list of tracking details of students whose mail status is 'N' 
		List<MassUploadTrackingSRBean> listSendEmailNotification = getListSendEmailNotification();
		
		if(listSendEmailNotification.size() > 0) {
			
			//send tracking notification to the users mail
			mailSender.sendTrackingNotificationToStudents(listSendEmailNotification);
			
			List<Integer> srIdList = listSendEmailNotification.stream().map(bean->bean.getServiceRequestId()).collect(Collectors.toList());
			
			//update mail status to 'Y' in servicerequest_trackingrecords table
			updateTrackingMailStatus(YES,srIdList);
		}
	}
	
	private List<MassUploadTrackingSRBean> getListSendEmailNotification() {
		
		List<MassUploadTrackingSRBean> listSendEmailNotification = massUploadTrackingSRDAO.getListSendEmailNotification();
		
		listSendEmailNotification.forEach(bean -> {
			
			if(bean.getAmount() == null)
				bean.setAmount(0);
			
			if("Issuance of Marksheet".equalsIgnoreCase(bean.getServiceRequestType()) && bean.getAmount() >= 1000)
				bean.setServiceRequestType("Duplicate Marksheet");
			
			if("Issuance of Gradesheet".equalsIgnoreCase(bean.getServiceRequestType()) && bean.getAmount() >= 500)
				bean.setServiceRequestType("Duplicate Gradesheet");
		});
		return listSendEmailNotification;
	}

	private void updateTrackingMailStatus(String status, List<Integer> srIdList) {
		
		Integer updatedMailStatusCount = massUploadTrackingSRDAO.updateTrackingMailStatus(status, srIdList);
		logger.info("mailStatus updated for {} Tracking records successfully", updatedMailStatusCount);
	}
}
