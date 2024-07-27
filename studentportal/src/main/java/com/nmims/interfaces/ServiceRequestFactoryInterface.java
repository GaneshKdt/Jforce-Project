package com.nmims.interfaces;

import com.nmims.abstracts.ServiceRequestAbstract;
import com.nmims.enums.ServiceRequestTypeEnum;

/**
 * Interface created containing method for the instantiation of the required Service Request Implementation.
 * @author Raynal Dcunha
 */
public interface ServiceRequestFactoryInterface {
	/**
	 * Returns the implementation (subclass) based upon the ServiceRequestTypeEnum element passed.
	 * @param serviceRequestType - ServiceRequestTypeEnum element
	 * @return ServiceRequest implementation (subclass)
	 */
	ServiceRequestAbstract createServiceRequest(ServiceRequestTypeEnum serviceRequestType);
}
