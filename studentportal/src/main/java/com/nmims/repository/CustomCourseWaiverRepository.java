package com.nmims.repository;

import java.util.List;
import java.util.Set;

import com.nmims.beans.CustomCourseWaiverDTO;

public interface CustomCourseWaiverRepository {

	public int getStudentCurrentSem(String sapid);
	
	public List<Integer> getWaivedInPss(String sapid, int sem);

	public List<CustomCourseWaiverDTO> getSubjectCodeId(List<Integer> waivedInPssId);

	public List<CustomCourseWaiverDTO> getSubjectName(Set<Integer> subjectCodeIds);

	public List<Integer> getWaivedOffPss(String sapid);

	public int checkSapidExist(String sapid);

	

}
