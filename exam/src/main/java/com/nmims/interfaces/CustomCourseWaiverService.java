package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.StudentExamBean;
import com.nmims.dto.CustomCourseWaiverDTO;


public interface CustomCourseWaiverService {

	public abstract void getWaivedInSubject(StudentExamBean student, ArrayList<String> subjects,
			HashMap<String, String> studentSemMapping);

	public abstract void getWaivedOffSubject(StudentExamBean student, ArrayList<String> waivedOffSubjects);

	public abstract boolean checkSapidExist(String sapid);

	public abstract Map<String, List<?>> getApplicableSubject(Long sapid);

	public abstract int saveWaivedInSubject(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser) throws Exception ;

	public abstract int saveWaivedOffSubject(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser);

	public abstract Map<String,Integer> upsertInCourseWaiver(List<CustomCourseWaiverDTO> waiverList,String loggedUser);

	public abstract void upsertInStudentCurrentSubject(long sapid);

	public abstract void upsertInStudentCourseMapping(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser);

	
}
