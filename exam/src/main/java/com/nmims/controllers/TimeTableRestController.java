package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimeTableBeanAPIResponse;
import com.nmims.beans.TimetableBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.StudentMarksDAO;

@RestController
@RequestMapping("m")
public class TimeTableRestController  extends BaseController {
	
	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	/*
	 * @Value("#{'${CORPORATE_CENTERS}'.split(',')}") private List<String>
	 * corporateCenterList;
	 */
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 
	private final int pageSize = 20;
	private static final Logger logger = LoggerFactory.getLogger(TimeTableRestController.class);
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> timeTableYearList; 
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Jan","Feb","Mar","Apr","Jun","Jul","Sep","Dec")); 

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();
	private HashMap<String, StudentExamBean> sapidStudentMap = new HashMap<>();
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();	
		
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		sapidStudentMap = null;
		getAllValidStudents();
		
		programAndProgramStructureAndSubjectsMap = null;
		getProgramSubjectMappingMap();
		
		return null;
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		
		return 	this.programSubjectMappingList;
	}

	public List<String> getAllCorporateNamesList(){
			List<String> corporateList= studentMarksDAO.getAllCorporateNames();
		return 	corporateList;
	}
	
	public HashMap<String, StudentExamBean> getAllValidStudents(){
		if(this.sapidStudentMap == null || this.sapidStudentMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<StudentExamBean> allValidStudent = eDao.getAllvalidStudents();
			sapidStudentMap = new HashMap<>();
			
			for (int i = 0; i < allValidStudent.size(); i++) {
				sapidStudentMap.put(allValidStudent.get(i).getSapid(), allValidStudent.get(i));
			}
		}
		
		return sapidStudentMap;
	}
	
	public HashMap<String, ArrayList<String>> getProgramSubjectMappingMap(){
		if(this.programAndProgramStructureAndSubjectsMap == null || this.programAndProgramStructureAndSubjectsMap.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = eDao.getProgramSubjectMappingList();

			programAndProgramStructureAndSubjectsMap = new HashMap<>();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);
				String program = bean.getProgram();
				String programStrucutre = bean.getPrgmStructApplicable();
				String key = program + "|" + programStrucutre;

				if(programAndProgramStructureAndSubjectsMap.containsKey(key)){
					ArrayList<String> subjectsUnderProgram = programAndProgramStructureAndSubjectsMap.get(key);
					subjectsUnderProgram.add(bean.getSubject());
				}else{
					ArrayList<String> subjectsUnderProgram = new ArrayList<>();
					subjectsUnderProgram.add(bean.getSubject());
					programAndProgramStructureAndSubjectsMap.put(key,subjectsUnderProgram);
				}

			}


		}
		return programAndProgramStructureAndSubjectsMap;
	}
	
	
	@PostMapping(path = "/studentTimeTable" , consumes = "application/json", produces = "application/json")
	public ResponseEntity<TimeTableBeanAPIResponse> mstudentTimeTable(@RequestBody StudentExamBean postInput) {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json"); 
	    
	    StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean isCorporate = false;

		HashMap<String,String> getCorporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();
		if(getCorporateCenterUserMapping.containsKey(postInput.getSapid())){
			isCorporate = true;
		}
		String mostRecentTimetablePeriod; // = dao.getMostRecentTimeTablePeriod();
		List<TimetableBean> timeTableList = dao.getStudentTimetableList(postInput,isCorporate);
		TimeTableBeanAPIResponse response = new TimeTableBeanAPIResponse();
		response.settimeTableList(timeTableList);
		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
		
		String examYear = "";
		String examMonth = "";
		
		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);
			examYear = bean.getExamYear();
			examMonth = bean.getExamMonth();
			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}
		
		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		response.setmostRecentTimetablePeriod(mostRecentTimetablePeriod);
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
		response.settreeMap(treeMap);		
		return new ResponseEntity(response, headers, HttpStatus.OK);
	}

}
