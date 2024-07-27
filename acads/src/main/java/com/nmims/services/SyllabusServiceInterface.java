package com.nmims.services;

import java.util.ArrayList;

import com.nmims.beans.SyllabusBean;

public interface SyllabusServiceInterface {
	
	public abstract Long createSyllabus(SyllabusBean bean) throws Exception;
	
	public abstract SyllabusBean createSyllabusBulk(SyllabusBean bean) throws Exception;
	
	public abstract ArrayList<SyllabusBean> readSyllabusForPSS(SyllabusBean bean) throws Exception;
	
	public abstract SyllabusBean readSyllabusForSyllabusId(SyllabusBean bean) throws Exception;
	
	public abstract int updateSyllabus(SyllabusBean bean) throws Exception;
	
	public abstract int deleteSyllabus(SyllabusBean bean) throws Exception;

	public abstract int deleteSyllabusDetails(SyllabusBean bean) throws Exception;
	
	public abstract ArrayList<SyllabusBean> getSubject() throws Exception;
}
