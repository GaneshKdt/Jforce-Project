package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.nmims.beans.DivisionBean;
import com.nmims.beans.StudentDivisionMappingBean;

@Service
public interface DivisionService {
	String insertDivisionDetails(DivisionBean bean) throws Exception;

	List<DivisionBean> getExistingDivisionList() throws Exception;

	Boolean duplicateStudentEntriesCheck(String sapId, String divisionId) throws Exception;

	int insertStudentToDivisionMappingBean(ArrayList<StudentDivisionMappingBean> listOfStudent, String createdBy)
			throws Exception;
	
	List<StudentDivisionMappingBean>getListOfExistingStudent(String divisionId)throws Exception;
	
	
	List<String>getListOfStudentByYear(String year);
	
}