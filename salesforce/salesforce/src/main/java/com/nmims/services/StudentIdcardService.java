package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.helpers.IdCardHelper;
import com.nmims.interfaces.StudentIdCardInterface;
import com.nmims.listeners.SalesforceSyncScheduler;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;

@Service
public class StudentIdcardService implements StudentIdCardInterface {
	
	
	@Autowired
	IdCardHelper idCardHelper;
	
	@Value("${ID_CARD_LINK}") 
	private String ID_CARD_LINK;
	
	@Autowired
	StudentZoneDao studentZoneDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(SalesforceSyncScheduler.class);

	@Override
	public ArrayList<StudentBean> generateIdCardForStudent(StudentBean student) {
		ArrayList<StudentBean> errorList = new ArrayList<>();
		HashMap<String, String> mapOfResponse = new HashMap<String, String>();
		try {
			logger.info("Student for Id card : " + student.getSapid());
			logger.info("Student's Account Id : " + student.getAccountId());

			// For Future registration if studentDetails are blank
			if (StringUtils.isBlank(student.getImageUrl())) {
				StudentBean studentNew = studentZoneDAO.getSingleStudentsData(student.getSapid());
				studentNew.setProgram(student.getProgram());
				studentNew.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
				studentNew.setYear(student.getYear());
				studentNew.setMonth(student.getMonth());
				studentNew.setSem(student.getSem());

				mapOfResponse = idCardHelper.generateIdCardURL(studentNew);
				if ("error".equalsIgnoreCase(mapOfResponse.get("status"))) {
					logger.error(" Error while generating ID Card: " + mapOfResponse.get("response"));
					student.setErrorMessage("Error while creating Id card");
					errorList.add(student);
				}
			} else {
				mapOfResponse = idCardHelper.generateIdCardURL(student);
				if ("error".equalsIgnoreCase(mapOfResponse.get("status"))) {
					logger.error(" Error while generating ID Card: " + mapOfResponse.get("response"));
					student.setErrorMessage("Error while creating Id card");
					errorList.add(student);
				}
			}
		} catch (Exception e) {
			student.setErrorMessage(e.getMessage());
			errorList.add(student);
			logger.error("Error while generating Id card: " + e.getMessage());
		}
		return errorList;
	}
	
	
}
