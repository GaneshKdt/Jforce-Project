package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.dto.SrAdminUpdateDto;
import com.nmims.repository.ServiceRequestRepository;

@Service
public class MassUpdateSRStatusService {
	
	@Autowired
	ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	ServiceRequestService serviceRequestService;
	
	private static final Logger logger = LoggerFactory.getLogger(MassUpdateSRStatusService.class);

	
	private static final List<String> serviceRequestStatusList = Arrays.asList("Cancelled", "Closed", "In Progress", "Payment Failed", "Payment Pending", "Submitted");

	/**
	 * Mass update Service Request records with provided status and cancellation reason for the given Service Request IDs.
	 * The provided fields are validated and each Service Request record is updated accordingly.
	 * A response Map is returned which contains the amount of Service Request IDs updated and error message for invalid/failed Service Request IDs.
	 * @param serviceRequestIdList - List of Service Request IDs
	 * @param requestStatus - Service Request status to be applied
	 * @param cancellationReason - cancellation reason (applicable for Cancelled Service Request records)
	 * @param userId - id of the Admin user
	 * @return Map containing the response message
	 */
	public Map<String, String> massUpdateSR(List<String> serviceRequestIdList, String requestStatus, String cancellationReason, String userId) {
		int updatedRecordsCount = 0;											//Counter which increments on every Service Request record updated
		StringBuilder errorMessage = new StringBuilder();
		
		//Fields of Mass Update Service Request validated
		massUpdateSRFieldChecks(serviceRequestIdList, requestStatus, cancellationReason, userId);
		logger.info("Mass update Service Request status: {} with cancellationReason: {}, initiated by user: {}, for {} Service Request IDs: {}",
				requestStatus, cancellationReason, userId, serviceRequestIdList.size(), serviceRequestIdList);
	
		//Records of Service Request IDs fetched from database and stored in a Map of key (serviceRequestId) - value (corresponding Service Request record data) pairs
		List<ServiceRequestStudentPortal> serviceRequestList = serviceRequestRepository.serviceRequestTypeSapidList(serviceRequestIdList);
		System.out.println("serviceRequestList : "+ serviceRequestList);
		Map<String, SrAdminUpdateDto> serviceRequestMap = serviceRequestList.stream()
																			.map((serviceRequestBean) -> createSrAdminUpdateDto(serviceRequestBean.getId(), serviceRequestBean.getServiceRequestType(), 
																																serviceRequestBean.getSapId(), requestStatus, cancellationReason))
																			.collect(Collectors.toMap((serviceRequest) -> String.valueOf(serviceRequest.getId()), Function.identity()));
		Set<String> validServiceRequestIdSet = serviceRequestMap.keySet();
		logger.info("Service Request records fetched for IDs: {}", validServiceRequestIdSet);
		
		//Service Request IDs which are not present in the Set retrieved from database are stored as invalid Service Request IDs
		Set<String> invalidServiceRequestIds = dataDifferenceSet(serviceRequestIdList, validServiceRequestIdSet);
		logger.info("Invalid Service Request IDs provided by user {}: {}", userId, invalidServiceRequestIds);
		if(!invalidServiceRequestIds.isEmpty())
			errorMessage.append("Invalid Service Request IDs: " + invalidServiceRequestIds.toString() + "\n");
		List<String> errorSrId = new ArrayList<String>();
		//each Service Request ID is iterated and updateServiceRequestStatusAndReason() method is called
		for(String serviceRequestId: validServiceRequestIdSet) {
			try {
				serviceRequestService.updateServiceRequestStatusAndReason(serviceRequestMap.get(serviceRequestId), userId);
				updatedRecordsCount++;
			}
	        catch(Exception ex) {
//	        	ex.printStackTrace();
				logger.error("Error while trying to update status: {} for Service Request ID: {}, Exception thrown: ", requestStatus, serviceRequestId, ex);
				errorSrId.add(serviceRequestId);
			}
		}
		if(!errorSrId.isEmpty())
			errorMessage.append("Error updating Service Request ID: ").append(errorSrId).append("\n");
		
		//A response Map is created with required data
		Map<String, String> responseMap = new HashMap<>();
		responseMap.put("status", (invalidServiceRequestIds.isEmpty() && updatedRecordsCount == validServiceRequestIdSet.size()) ? "success" : "error");
		responseMap.put("successCount", String.valueOf(updatedRecordsCount));			
		responseMap.put("errorMessage", errorMessage.toString());
		return responseMap;
	}
	
	
	/**
	 * List Data which is not present in Set is returned as a Set.
	 * @param dataList - List containing the data which is to be evaluated
	 * @param dataSet - Set containing data which is used as a Comparator
	 * @return Set containing the non-matching data
	 */
	private static Set<String> dataDifferenceSet(List<String> dataList, Set<String> dataSet) {
		return dataList.stream()
						.filter(data -> !dataSet.contains(data))
						.collect(Collectors.toSet());
	}
	
	/**
	 * The fields required for Mass Update Service Request is validated.
	 * @param serviceRequestIdList - List containing Service Request IDs
	 * @param requestStatus - Service Request status
	 * @param cancellationReason - reason for Service Request cancellation
	 * @param userId - id of the Admin user
	 */
	private void massUpdateSRFieldChecks(List<String> serviceRequestIdList, String requestStatus, String cancellationReason, String userId) {
		if(CollectionUtils.isEmpty(serviceRequestIdList))
			throw new IllegalArgumentException("No Service Request IDs provided!");
		
		if(!serviceRequestStatusList.contains(requestStatus))
			throw new IllegalArgumentException("Invalid Status selected.");
		
		if("Cancelled".equals(requestStatus) && StringUtils.isBlank(cancellationReason))
			throw new IllegalArgumentException("Cancellation Reason not provided.");
		
		if(StringUtils.isBlank(userId))
			throw new IllegalArgumentException("Unable to detect User ID. Please try again!");
	}
	/**
	 * A SrAdminUpdateDto DTO is created with the provided fields.
	 * @param serviceRequestId - ID of the Service Request
	 * @param serviceRequestType - type of the Service Request
	 * @param sapId - sapid of the student who raised the Service Request
	 * @param requestStatus - status of the Service Request
	 * @param cancellationReason - reason for the Service Request cancellation (applicable for Cancelled Service Request records)
	 * @return DTO containing the provided fields
	 */
	private static SrAdminUpdateDto createSrAdminUpdateDto(Long serviceRequestId, String serviceRequestType, String sapId, String requestStatus, String cancellationReason) {
		SrAdminUpdateDto srAdminUpdateDto = new SrAdminUpdateDto();
		srAdminUpdateDto.setId(serviceRequestId);
		srAdminUpdateDto.setServiceRequestType(serviceRequestType);
		srAdminUpdateDto.setSapid(sapId);
		srAdminUpdateDto.setRequestStatus(requestStatus);
		srAdminUpdateDto.setCancellationReason(cancellationReason);
		
		return srAdminUpdateDto;
	}
	
	
}
