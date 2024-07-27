package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import com.nmims.beans.StudentStudentPortalBean;

public interface CustomCourseWaiverService {

	public abstract void getWaivedInSubject(StudentStudentPortalBean student, ArrayList<String> subjects,
			HashMap<String, String> studentSemMapping);

	public abstract void getWaivedOffSubject(StudentStudentPortalBean student, ArrayList<String> waivedOffSubjects);

	public abstract boolean checkSapidExist(String sapid);

}
