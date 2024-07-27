package com.nmims.daos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import com.nmims.beans.FacultyReallocationBean;

@Component
public interface FacultyReallocationDao 
{
	public ArrayList<FacultyReallocationBean> getAllFacultyWithNameAndId()throws SQLException;
	public ArrayList<FacultyReallocationBean> getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws SQLException;
	public HashMap<String,FacultyReallocationBean> getProjectsNotEvaluatedToFacultyByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws SQLException;
	public int reallocateProjectsToFaculty(String fromFacultyId, String toFacultyId, String year, String month, List<String> sapids, String user)throws Exception;
	public List<String> getSapidsByFacultyIdAndYearAndMonth(String facultyId, String year, String month)throws Exception;
	public List<FacultyReallocationBean> getMasterKeysDetailList(List<String> sapids)throws Exception;
	public List<FacultyReallocationBean> getAllProgramDetails()throws Exception;
}