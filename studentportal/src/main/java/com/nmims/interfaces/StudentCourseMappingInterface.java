package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.beans.StudentStudentPortalBean;

public interface StudentCourseMappingInterface 
{
		
	int insertInStudentCourseTable(String sapid,boolean sfdcCall) throws Exception ;
	
	ArrayList<String> getCurrentCycleSujects(String sapid,String masterkey,String sem,boolean sfdcCall) throws Exception ;
	
	ArrayList<String> getFailSubjectsNamesForAStudent(String sapid);
	
	ArrayList<String> getPassSubjectsNamesForAStudent(String sapid);

}
