/**
 * 
 */
package com.nmims.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentSessionCoursesBean;
import com.nmims.dto.StudentSessionCoursesDTO;
import com.nmims.interfaces.StudentSessionCoursesClientServiceInterface;
import com.nmims.interfaces.StudentSessionCoursesFactoryInterface;
import com.nmims.interfaces.StudentSessionCoursesServiceInterface;

/**
 * @author vil_m
 *
 */
@Service("studentSessionCoursesClientService")
public class StudentSessionCoursesClientService implements StudentSessionCoursesClientServiceInterface {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesClientService.class);
	
	@Autowired
	protected StudentSessionCoursesFactoryInterface studentSessionCoursesFactory;

	protected StudentSessionCoursesServiceInterface studentSessionCoursesService;

	public StudentSessionCoursesDTO create(StudentSessionCoursesDTO dtoObj, String userId) {
		logger.info("Entering StudentSessionCoursesClientService : create");
		StudentSessionCoursesBean bean = null;
		StudentSessionCoursesBean returnBean = null;
		StudentSessionCoursesDTO returnDTOObj = null;
		String month = null;
		
		try {
			studentSessionCoursesService = studentSessionCoursesFactory
					.getStudentSessionCoursesService(StudentSessionCoursesFactoryInterface.PRODUCT_PG);
			
			month = dtoObj.getAcadMonth();
			month = month.substring(0, 3);
			bean = new StudentSessionCoursesBean(dtoObj.getSapId(), dtoObj.getAcadYear(), month, dtoObj.getCourseIds(), StudentSessionCoursesBean.ROLE);
			bean.setCreatedBy(userId);
			bean.setLastModifiedBy(userId);
			logger.info("StudentSessionCoursesClientService : create : SapId : "+dtoObj.getSapId());
			
			returnBean = studentSessionCoursesService.create(bean);
			
			returnDTOObj = new StudentSessionCoursesDTO(returnBean.getSapId(), returnBean.getAcadYear(), returnBean.getAcadMonth(), returnBean.getCourseIds());
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_SUCCESS);
			returnDTOObj.setMessage("Success.");
		} catch(Exception ex) {
			if(null == returnDTOObj) {
				returnDTOObj = new StudentSessionCoursesDTO(dtoObj.getSapId(), dtoObj.getAcadYear(), dtoObj.getAcadMonth(), dtoObj.getCourseIds());
			}
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_ERROR);
			returnDTOObj.setMessage("Error : "+ ex.getMessage());
			logger.error("StudentSessionCoursesClientService : create : ERROR : "+ex.getMessage());
		} finally {
			bean = null;
			returnBean = null;
		}
		return returnDTOObj;
	}

	@Override
	public StudentSessionCoursesDTO read(StudentSessionCoursesDTO dtoObj) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesClientService : read");
		StudentSessionCoursesBean bean = null;
		StudentSessionCoursesBean returnBean = null;
		StudentSessionCoursesDTO returnDTOObj = null;
		
		try {
			studentSessionCoursesService = studentSessionCoursesFactory
					.getStudentSessionCoursesService(StudentSessionCoursesFactoryInterface.PRODUCT_PG);
			
			bean = new StudentSessionCoursesBean();
			bean.setSapId(dtoObj.getSapId());
			logger.info("StudentSessionCoursesClientService : read : SapId : "+dtoObj.getSapId());
			
			returnBean = studentSessionCoursesService.read(bean);
			
			if(null != returnBean) {
				returnDTOObj = new StudentSessionCoursesDTO(returnBean.getSapId(), returnBean.getAcadYear(), returnBean.getAcadMonth(), returnBean.getCourseIds());
				returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_SUCCESS);
				returnDTOObj.setMessage("Success.");
			} else {
				returnDTOObj = new StudentSessionCoursesDTO();
				returnDTOObj.setSapId(dtoObj.getSapId());
				returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_SUCCESS);
				returnDTOObj.setMessage("Success : Total Rows found : 0");
			}
		} catch(Exception ex) {
			if(null == returnDTOObj) {
				returnDTOObj = new StudentSessionCoursesDTO();
				returnDTOObj.setSapId(dtoObj.getSapId());
			}
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_ERROR);
			returnDTOObj.setMessage("Error : "+ ex.getMessage());
			logger.error("StudentSessionCoursesClientService : read : ERROR : "+ex.getMessage());
		} finally {
			bean = null;
			returnBean = null;
		}
		return returnDTOObj;
	}

	@Override
	public StudentSessionCoursesDTO delete(StudentSessionCoursesDTO dtoObj) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesClientService : delete");
		StudentSessionCoursesBean bean = null;
		int rowsDeleted = -1;
		StudentSessionCoursesDTO returnDTOObj = null;
		String month = null;
		
		try {
			studentSessionCoursesService = studentSessionCoursesFactory
					.getStudentSessionCoursesService(StudentSessionCoursesFactoryInterface.PRODUCT_PG);
			
			//bean = new StudentSessionCoursesBean();
			//bean.setSapId(dtoObj.getSapId());
			month = dtoObj.getAcadMonth();
			month = month.substring(0, 3);
			bean = new StudentSessionCoursesBean(dtoObj.getSapId(), dtoObj.getAcadYear(), month, null, null);
			logger.info("StudentSessionCoursesClientService : delete : (SapId, Course Id, Acad Year, Acad Month) : ("
					+ dtoObj.getSapId() + "," + dtoObj.getCourseIds() + "," + dtoObj.getAcadYear() + "," + month + ")");
			
			rowsDeleted = studentSessionCoursesService.delete(bean);
			logger.info("StudentSessionCoursesClientService : delete : Total Rows deleted : "+rowsDeleted);
			
			returnDTOObj = new StudentSessionCoursesDTO();
			returnDTOObj.setSapId(dtoObj.getSapId());
			//returnDTOObj.setCourseIds(dtoObj.getCourseIds());
			returnDTOObj.setAcadYear(dtoObj.getAcadYear());
			returnDTOObj.setAcadMonth(dtoObj.getAcadMonth());
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_SUCCESS);
			returnDTOObj.setMessage("Success : Total Rows deleted : "+rowsDeleted);
		} catch(Exception ex) {
			if(null == returnDTOObj) {
				returnDTOObj = new StudentSessionCoursesDTO();
				returnDTOObj.setSapId(dtoObj.getSapId());
				//returnDTOObj.setCourseIds(dtoObj.getCourseIds());
				returnDTOObj.setAcadYear(dtoObj.getAcadYear());
				returnDTOObj.setAcadMonth(dtoObj.getAcadMonth());
			}
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_ERROR);
			returnDTOObj.setMessage("Error : "+ ex.getMessage());
			logger.error("StudentSessionCoursesClientService : delete : ERROR : "+ex.getMessage());
		} finally {
			bean = null;
		}
		return returnDTOObj;
	}

	@Override
	public StudentSessionCoursesDTO update(StudentSessionCoursesDTO dtoObj, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering StudentSessionCoursesClientService : update");
		StudentSessionCoursesBean bean = null;
		StudentSessionCoursesBean returnBean = null;
		StudentSessionCoursesDTO returnDTOObj = null;
		String month = null;
		
		try {
			studentSessionCoursesService = studentSessionCoursesFactory
					.getStudentSessionCoursesService(StudentSessionCoursesFactoryInterface.PRODUCT_PG);
			
			month = dtoObj.getAcadMonth();
			month = month.substring(0, 3);
			bean = new StudentSessionCoursesBean(dtoObj.getSapId(), dtoObj.getAcadYear(), month, dtoObj.getCourseIds(), StudentSessionCoursesBean.ROLE);
			bean.setLastModifiedBy(userId);
			logger.info("StudentSessionCoursesClientService : update : SapId : "+dtoObj.getSapId());
			
			returnBean = studentSessionCoursesService.update(bean);
			
			returnDTOObj = new StudentSessionCoursesDTO(returnBean.getSapId(), returnBean.getAcadYear(), returnBean.getAcadMonth(), returnBean.getCourseIds());
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_SUCCESS);
			returnDTOObj.setMessage("Success.");
		} catch(Exception ex) {
			if(null == returnDTOObj) {
				returnDTOObj = new StudentSessionCoursesDTO(dtoObj.getSapId(), dtoObj.getAcadYear(), dtoObj.getAcadMonth(), dtoObj.getCourseIds());
			}
			returnDTOObj.setStatus(StudentSessionCoursesBean.KEY_ERROR);
			returnDTOObj.setMessage("Error : "+ ex.getMessage());
			logger.error("StudentSessionCoursesClientService : update : ERROR : "+ex.getMessage());
		} finally {
			bean = null;
			returnBean = null;
		}
		return returnDTOObj;
	}
}
