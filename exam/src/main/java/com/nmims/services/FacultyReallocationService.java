package com.nmims.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.nmims.beans.FacultyReallocationBean;

public interface FacultyReallocationService
{
	public ArrayList<FacultyReallocationBean> getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws IOException,NullPointerException,SQLException;
	public HashMap<String,FacultyReallocationBean> getProjectsNotEvaluatedByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws IOException,NullPointerException,SQLException;
	public int reallocateProjectsToFaculty(FacultyReallocationBean bean)throws Exception;
	public ArrayList<FacultyReallocationBean> getSearchedFacultyList(HashMap<String, FacultyReallocationBean> mapRealocatedCount, ArrayList<FacultyReallocationBean> allocatedCount, Map<String, FacultyReallocationBean> mapfacultyList);
	public List<FacultyReallocationBean> getStudentsByFacultyIdAndYearAndMonth(String facultyId, String year, String month)throws Exception;
}
