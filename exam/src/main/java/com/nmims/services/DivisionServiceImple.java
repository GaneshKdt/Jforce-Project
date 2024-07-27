package com.nmims.services;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.DivisionBean;
import com.nmims.beans.StudentDivisionMappingBean;
import com.nmims.daos.DivisionDetailsDao;
import com.nmims.interfaces.DivisionService;
@Service
public class DivisionServiceImple implements DivisionService{
	@Autowired
	DivisionDetailsDao divisionDetailsDao;
	
	@Override
	public String insertDivisionDetails(DivisionBean bean) throws Exception {
		String insertMBAWXDivisionDeatils = divisionDetailsDao.insertMBAWXDivisionDeatils(bean);
		return insertMBAWXDivisionDeatils;
	}
	@Override
	public List<DivisionBean> getExistingDivisionList() throws Exception {
		List<DivisionBean> existingDivisionDetails = new ArrayList<DivisionBean>();
		existingDivisionDetails=divisionDetailsDao.getExistingDivisionDetails();
		return existingDivisionDetails;
	}
	@Override
	public Boolean duplicateStudentEntriesCheck(String sapId, String divisionId) throws Exception {
		return  divisionDetailsDao.duplicateStudentEntriesCheck(sapId,divisionId);
	}
	@Override
	public int insertStudentToDivisionMappingBean(ArrayList<StudentDivisionMappingBean> listOfStudent,
			String createdBy) throws Exception {
		return divisionDetailsDao.insertStudentToDivisionMappingBean(listOfStudent,createdBy);
		
	}
	@Override
	public List<StudentDivisionMappingBean> getListOfExistingStudent(String divisionId) throws Exception {
		return divisionDetailsDao.getListOfDivisionStudent(divisionId);
	}
	
	@Override
	public List<String> getListOfStudentByYear(String year) {
		return divisionDetailsDao.getListOfStudentByYear(year);
	}
}