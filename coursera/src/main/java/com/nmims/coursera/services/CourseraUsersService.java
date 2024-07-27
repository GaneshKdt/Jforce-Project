package com.nmims.coursera.services;

import java.time.LocalDateTime;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.beans.StudentCourseraMappingBean;
import com.nmims.coursera.dao.CourseraUsersDao;
import com.nmims.coursera.dto.CourseraSyncDto;
import com.nmims.coursera.dto.StudentCourseraDto;
import com.nmims.coursera.interfaces.CourseraServiceInterface;

@Service("CourseraUsersService")
public class CourseraUsersService implements CourseraServiceInterface {
	
	private static final Logger logger = LoggerFactory.getLogger("coursera_sync_trigger");

	@Autowired
	CourseraUsersDao courseraUsersDao;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;

	@Transactional
	@Override
	public StudentCourseraBean getSingleStudentsData(String sapid) {
		return courseraUsersDao.getSingleStudentsData(sapid);
	}

	@Transactional
	@Override
	public String getStudentlearnerURL(String sapid) {
		
		String learnerURL = "";
		
		if (ENVIRONMENT.equalsIgnoreCase("PROD")){
			learnerURL = courseraUsersDao.getStudentlearnerURL(sapid);
		}else {
			//Added static for Testing Environment
			learnerURL = "https://coursera.org/programs/sso-api-testing-nc9yb";
		}
		
		//Appended learnerURL for Coursera SSO Login
		if (!StringUtils.isBlank(learnerURL)) {
			learnerURL = learnerURL + "?attemptSSOLogin=true";
		}
		
		return learnerURL;
		
	}

	@Transactional
	@Override
	public CourseraSyncDto create(CourseraSyncDto dtoObj) {
		CourseraSyncDto returnDto = new CourseraSyncDto();
		try {
			String coursera_program_id = courseraUsersDao.getCourseraProgramIdBySapid(dtoObj.getSapId());
				try {
					courseraUsersDao.insertCourseraEntry(dtoObj.getSapId(), "Salesforce", coursera_program_id);
					returnDto.setStatus("success");
					returnDto.setSapId(dtoObj.getSapId());
					returnDto.setMessage("Student has been added successfully!");
					logger.info("SapId -- "+dtoObj.getSapId() +" added successfully!");
					return returnDto;
				} catch (PersistenceException e) {
					returnDto.setStatus("success");
					returnDto.setSapId(dtoObj.getSapId());
					returnDto.setMessage("Student already present in database!");
					logger.error("SapId -- "+dtoObj.getSapId() +" already present in database! "+e.getMessage());
					return returnDto;
				} catch (Exception e) {
					returnDto.setStatus("error");
					returnDto.setSapId(dtoObj.getSapId());
					returnDto.setMessage("Error in saving student!");
					logger.error("SapId -- "+dtoObj.getSapId() +" Error in saving student! "+e.getMessage());
					return returnDto;
				}
		}catch (Exception e) {
			returnDto.setStatus("error");
			returnDto.setSapId(dtoObj.getSapId());
			returnDto.setMessage("Coursera is not applicable for the student program!");
			logger.info("SapId -- "+dtoObj.getSapId() +" Coursera is not applicable for the student program!");
			return returnDto;
		}
	}
	
	@Transactional
	@Override
	public StudentCourseraDto checkForStudentOptedForCoursera(StudentCourseraMappingBean dtoObj) {
		StudentCourseraDto returnDto = new StudentCourseraDto();
		int count = courseraUsersDao.checkForCourseraAvailibilityForMasterKey(dtoObj.getConsumer_program_structure_id());
		if (count > 0) {
			try {
				returnDto.setApplicable(true);

				String learnerURL = courseraUsersDao.getStudentlearnerURLByCounsumerProgramStructureId(dtoObj.getConsumer_program_structure_id());
				returnDto.setLearnerURL(learnerURL);

				StudentCourseraMappingBean bean = courseraUsersDao.checkStudentOptedForCoursera(dtoObj.getSapId());

				boolean isBefore = false;
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime expiryDate = bean.getExpiryDate();
				isBefore = now.isBefore(expiryDate);
				if (isBefore) {
					returnDto.setPaid(true);
				} else {
					returnDto.setPaid(false);
				}
			} catch (Exception e) {
				returnDto.setPaid(false);
			}

		} else {
			returnDto.setApplicable(false);
		}

		return returnDto;
	}
}
