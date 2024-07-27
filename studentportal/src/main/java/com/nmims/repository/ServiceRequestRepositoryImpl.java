package com.nmims.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.naming.InvalidNameException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.stereotype.Repository;

import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;

/**
 * Implementation of the Service Request Repository Interface
 * @author Raynal Dcunha
 */
@Repository
public class ServiceRequestRepositoryImpl implements ServiceRequestRepository {
	private final ServiceRequestDao serviceRequestDao;
	private final StudentDAO studentDAO;
	private final LDAPDao ldapDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceRequestRepositoryImpl.class);
	
	@Autowired
	public ServiceRequestRepositoryImpl(ServiceRequestDao serviceRequestDao, StudentDAO studentDAO, LDAPDao ldapDao) {
		Objects.requireNonNull(serviceRequestDao);			//Fail-fast approach, field is guaranteed be non-null.
		Objects.requireNonNull(studentDAO);
		Objects.requireNonNull(ldapDao);
		this.serviceRequestDao = serviceRequestDao;
		this.studentDAO = studentDAO;
		this.ldapDao = ldapDao;
	}
	
	@Override
	public Map<String, Object> studentFatherMotherHusbandName(Long sapid) {
		return studentDAO.getStudentFatherMotherHusbandName(sapid);
	}
	
	@Override
	public Map<String, Object> studentEmailIdMobileNo(Long sapid) {
		return studentDAO.getStudentEmailIdMobileNo(sapid);
	}

	@Override
	public Map<String, Object> studentPersonalDetails(Long sapid) {
		return studentDAO.getStudentPersonalDetails(sapid);
	}

	@Override
	public Map<String, Object> studentEnrollmentYearMonth(Long sapid) {
		return studentDAO.getStudentEnrollmentYearMonth(sapid);
	}
	
	@Override
	public Long insertFreeServiceRequestRecord(ServiceRequestStudentPortal serviceRequest) {
		return serviceRequestDao.insertFreeServiceRequest(serviceRequest.getServiceRequestType(), serviceRequest.getSapId(), serviceRequest.getAmount(), 
														serviceRequest.getTranStatus(), serviceRequest.getRequestStatus(), serviceRequest.getDescription(), 
														serviceRequest.getCreatedBy(), serviceRequest.getLastModifiedBy(), serviceRequest.getCategory(), 
														serviceRequest.getHasDocuments(), serviceRequest.getModeOfDispatch(), serviceRequest.getDevice());
	}
	
	@Override
	public Long inserServiceRequestDocumentRecord(ServiceRequestDocumentBean srDocument) {
		serviceRequestDao.insertServiceRequestDocument(srDocument);
		return srDocument.getId();
	}

	@Override
	public Long updateSrRecordDocumentStatus(Long serviceRequestId, String hasDocuments, String user) {
		Long count = serviceRequestDao.updateSrHasDocumentsFlag(serviceRequestId, hasDocuments, user);
		ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(serviceRequestId);
		serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
		return count;
	}
	
	@Override
	public Integer studentEmailIdUpdate(Long sapid, String emailId, String user) {
		return studentDAO.updateStudentEmailId(sapid, emailId, user);
	}
	
	@Override
	public Integer studentMobileNoUpdate(Long sapid, String mobile, String user) {
		return studentDAO.updateStudentMobileNo(sapid, mobile, user);
	}
	
	@Override
	public Integer studentFatherNameUpdate(Long sapid, String fatherName, String user) {
		return serviceRequestDao.updateStudentFatherName(sapid, fatherName, user);
	}

	@Override
	public Integer studentMotherNameUpdate(Long sapid, String motherName, String user) {
		return serviceRequestDao.updateStudentMotherName(sapid, motherName, user);
	}

	@Override
	public Integer studentSpouseNameUpdate(Long sapid, String spouseName, String user) {
		return serviceRequestDao.updateStudentSpouseName(sapid, spouseName, user);
	}
	
	@Override
	public List<String> srDescriptionList(Long sapid, String srType) {
		return serviceRequestDao.getNotClosedCancelledFailedSrDescriptionList(sapid, srType);
	}

	@Override
	public List<ServiceRequestStudentPortal> serviceRequestTypeSapidList(List<String> serviceRequestIdList) {
		try {
			return serviceRequestDao.getServiceRequestTypeSapIdBySrIds(serviceRequestIdList);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("EmptyResultDataAccess Exception while retrieving Type and SapId of serviceRequestIds: {}", serviceRequestIdList);
			
			return new ArrayList<>();			//Empty result List returned in case of no records found.
		}
	}

	@Override
	public String getStudentEmailAttributeLdap(Long sapid) {
		try {
			return ldapDao.getMailLdapAttribute(String.valueOf(sapid));
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while fetching mail attribute in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object of Student with Sapid: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching mail attribute of Student {} in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to search and retrieve Student mail attribute from LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}

	@Override
	public String getStudentMobileAttributeLdap(Long sapid) {
		try {
			return ldapDao.getMobileLdapAttribute(String.valueOf(sapid));
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while fetching mobile attribute in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object of Student with Sapid: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching mobile attribute of Student {} in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to search and retrieve Student mobile attribute from LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}

	@Override
	public void updateStudentMailAttributeLdap(Long sapid, String attributeValue) {
		try {
			ldapDao.updateMailLdapAttribute(String.valueOf(sapid), attributeValue);
			return;
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while modifying mail attribute in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object for modifying mail attribute of Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while modifying mail attribute of Student {} in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to modify Student mail attribute in LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}
	
	@Override
	public void updateStudentMobileAttributeLdap(Long sapid, String attributeValue) {
		try {
			ldapDao.updateMobileLdapAttribute(String.valueOf(sapid), attributeValue);
			return;
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while modifying mobile attribute in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object for modifying mobile attribute of Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while modifying mobile attribute of Student {} in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to modify Student mobile attribute in LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}

	@Override
	public Integer updateProgramStatusForDeregisteredStudent(Long sapid, String userId,StudentStudentPortalBean newMappedProgram) {
		return studentDAO.updateStudentProgramStatusOnExitApproval(sapid, userId,newMappedProgram);
	}

	@Override
	public Integer updateProgramStatusAsActiveForDeregistrationCancellation(Long sapid, String userId) {
		String programStatus = studentDAO.getStudentProgramStatus(sapid);		//Obtain the current programStatus of Student
		
		if(Objects.nonNull(programStatus))		//If programStatus is not Active (null), then run the update query
			return studentDAO.updateActiveProgramStatus(sapid, userId);
		
		return 0;		//return 0 as no data updated
	}
}
