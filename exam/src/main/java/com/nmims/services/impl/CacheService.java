package com.nmims.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.ICacheService;

@Service("cacheService")
public class CacheService implements ICacheService{
	
	@Autowired
	private ExamCenterDAO examCenterDAO;
	
	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	@Autowired
	private ExamBookingDAO examBookingDAO;
	
	private Map<String,String> corporateCenterUserMapping;
	
	private Map<String,String> corporateExamCenterIdNameMap;
	
	private Map<String, String> examCenterIdNameMap;
	
	private Map<String,String> programCodeNameMap;
	
	private List<String> subjectList;
	
	private List<String> programList;
	
	private List<ProgramSubjectMappingExamBean> programSubjectMappingList;
	
	private boolean refreshCache;
	
	@Override
	public void refreshCache() throws Exception{
		/*refreshCache = true;
		getStudentFreeSubjectsMap();
		
		refreshCache = true;
		getProgramSubjectMappingList(); Done
		
		refreshCache = true;
		getProgramMap(); - Done
		
		refreshCache = true;
		getExamCenterIdNameHashMap();*/
		
		refreshCache = true;
		this.getCorporateCenterUserMapping();
		
		refreshCache = true;
		this.getExamCenterIdNameMap();
		
		refreshCache = true;
		this.getCorporateExamCenterIdNameMap(); 
		
		/*refreshCache = true;
		getExemptStudentList();
		
		refreshCache = true;
		getSubjectList(); Done
		
		refreshCache = true;
		getProgramList(); Done */
		
	}
	
	@Override
	public Map<String, String> getExamCenterIdNameMap(){
		if(this.examCenterIdNameMap == null || examCenterIdNameMap.size() == 0 || refreshCache){
			this.examCenterIdNameMap = examCenterDAO.getExamCenterIdNameMap();
			refreshCache = false;
		}
		return this.examCenterIdNameMap;
	}
	
	@Override
	public Map<String, String> getCorporateCenterUserMapping() throws Exception{
		if(this.corporateCenterUserMapping == null || this.corporateCenterUserMapping.size() == 0 || refreshCache){
			this.corporateCenterUserMapping = examCenterDAO.getCorporateCenterUserMapping();
			refreshCache = false;
		}

		return this.corporateCenterUserMapping;
	}

	@Override
	public Map<String, String> getCorporateExamCenterIdNameMap() throws Exception {
		if(this.corporateExamCenterIdNameMap == null || this.corporateExamCenterIdNameMap.size() == 0 || refreshCache) {
			this.corporateExamCenterIdNameMap = examCenterDAO.getCorporateExamCenterIdNameMap();
			refreshCache = false;
		}
		return this.corporateExamCenterIdNameMap;
	}
	
	@Override
	public Map<String, String> getProgramMap() throws Exception{
		if(this.programCodeNameMap == null || this.programCodeNameMap.size() == 0 || refreshCache){
			this.programCodeNameMap = studentMarksDAO.getProgramDetails();
			refreshCache = false;
		}
		return programCodeNameMap;
	}
	
	@Override
	public List<String> getSubjectList() throws Exception{
		if(this.subjectList == null || refreshCache){
			this.subjectList = studentMarksDAO.getAllSubjects();
			refreshCache = false;
		}
		return subjectList;
	}
	
	@Override
	public List<String> getProgramList() throws Exception{
		if(this.programList == null || refreshCache){
			this.programList = studentMarksDAO.getAllPrograms();
			refreshCache = false;
		}
		return programList;
	}
	
	@Override
	public List<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0 || refreshCache){
			this.programSubjectMappingList = examBookingDAO.getProgramSubjectMappingList();
			refreshCache = false;
		}
		return programSubjectMappingList;
	}
}
