package com.nmims.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.abstracts.ServiceRequestAbstract;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.interfaces.ServiceRequestFactoryInterface;

/**
 * A Factory class through which an Implementation (subclass) of the ServiceRequestAbstract can be instantiated.
 * All the implementations (subclasses) of the ServiceRequestAbstract are stored in a Set,
 * this set is then iterated and each implementation is stored as a value in a HashMap, 
 * with it's corresponding ServiceRequestTypeEnum as the key.
 * 
 * A particular implementation (subclass) can then be retrieved with the help of the ServiceRequestTypeEnum key.
 * @author Raynal Dcunha
 */
@Component
public class ServiceRequestFactory implements ServiceRequestFactoryInterface {
	private static Map<ServiceRequestTypeEnum, ServiceRequestAbstract> serviceRequestMap;
	
	@Autowired
	public ServiceRequestFactory(Set<ServiceRequestAbstract> serviceRequestSet) {
		createServiceRequestMap(serviceRequestSet);
	}
	
	@Override
	public ServiceRequestAbstract createServiceRequest(ServiceRequestTypeEnum serviceRequestType) {
		return serviceRequestMap.get(serviceRequestType);
	}
	
	/**
	 * Creates a HashMap contating the Implementations (subclasses) of the ServiceRequestAbstract, 
	 * with ServiceRequestTypeEnum element as their respective keys.
	 * @param serviceRequestSet - Set containing the ServiceRequest Implementations (subclasses)
	 */
	private void createServiceRequestMap(Set<ServiceRequestAbstract> serviceRequestSet) {
		serviceRequestMap = new HashMap<>();
		serviceRequestSet.forEach(serviceRequest -> serviceRequestMap.put(serviceRequest.getServiceRequestType(), serviceRequest));
	}
}
