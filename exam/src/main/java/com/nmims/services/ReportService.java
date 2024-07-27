package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UGConsentExcelReportBean;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.StudentMarksDAO;

@Service("reportService")
public class ReportService {

	@Autowired
	ReportsDAO reportDao;
	
	@Autowired
	StudentMarksDAO studentMarkDao;
	
	private HashMap<String, CenterExamBean> centersMap = null; 
	
	@Autowired
	StudentService studentService;
	
	private final static String sem = "1";
	private final static String month = "Jul";
	private final static String year = "2022";
	private final static String LC = "No LC";
	private final static String IC = "No IC";
	
	static final Map<String, String> programCourseDetail = ImmutableMap.of(
		    "B.Com", "Certificate Program in General Management",
		  "BBA" , "Certificate in Business Administration",
		  "BBA-BA","Certificate in Business Administration");
	
	static final Map<String, String> ugOptionDetail = ImmutableMap.of(
		    "1", "Opt for the 6-month #course. At the end of the 6-month cycle, opt for lateral admission/program upgrade to Semester 2 of #program in January 2023 academic cycle. This option will ensure continuity and overall duration of the program remains as is.",
		  "2" , "Opt out of the current academic cycle completely & request for transfer of admission to the next admission intake (January 2023) for #program program. This means your program start date will be January 2023.",
		  "3","Opt for 6-month #course only until its completion and then have the freedom of decision for your next steps",
		  "4","Admission Cancellation You may cancel your admission and get a full refund of the fee paid."
		);

	public HashMap<String, CenterExamBean> getCentersMap(){
		ArrayList<CenterExamBean> centers = studentMarkDao.getAllCenters();
		centersMap = new HashMap<>();
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean cBean = centers.get(i);
			centersMap.put(cBean.getCenterCode(), cBean);
		}
		return centersMap;
	}
	
	/**
	 * 
	 * @param ugStudent (type - UGConsentExcelReportBean)
	 * @return - Ug student which have submitted the consent form , details will be extracted on the basis of program
	 */
	
	public ArrayList<UGConsentExcelReportBean> getUgConsentStudentSubmittedDetails(UGConsentExcelReportBean ugStudent, String authorizedCenterCodes){
		//Get the Student list which has given ugc consent form
		ArrayList<UGConsentExcelReportBean> studentList = reportDao.getugcStudentDetailedList(ugStudent.getProgram(),authorizedCenterCodes);
		if(studentList.size() > 0) {
			HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
			//Get the student's personsal Details 
			for (UGConsentExcelReportBean studentBean : studentList) {
					studentBean.setOption(ugOptionDetail.get(studentBean.getConsent_option()).replaceAll("#course", programCourseDetail.get(studentBean.getProgram())));
					studentBean.setOption(studentBean.getOption().replaceAll("#program", studentBean.getProgram()));
					studentBean.setConsent_optionid(studentBean.getConsent_option());
					studentBean.setLcName(centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getLc() : LC);
					studentBean.setInformation_center( centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getCenterName()  : IC);
			}
		}
		return studentList;
	}
	
	/**
	 * 
	 * @param ugStudent (type - UGConsentExcelReportBean)
	 * @return - Ug student which have not submitted the consent form , details will be extracted on the basis of program
	 */
	
	public ArrayList<UGConsentExcelReportBean> getUgConsentStudentPendingDetails(UGConsentExcelReportBean ugStudent, String authorizedCenterCodes){
		ArrayList<UGConsentExcelReportBean> studentList = null;
		//Get the Student list which has given ugc consent form
		ArrayList<String> ugStudents  = reportDao.getugcStudents();
		if(ugStudents.size() > 0) {
			ArrayList<String> notsubmittedugStudent = reportDao.getugcStudentPendingList(ugStudents,ugStudent.getProgram(),sem,month,year);
					
			if(notsubmittedugStudent != null && notsubmittedugStudent.size() > 0){
			//Get the student's personsal Details 
			studentList = reportDao.getStudentsDetails(notsubmittedugStudent,authorizedCenterCodes);
			HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
				for (UGConsentExcelReportBean studentBean : studentList) {
						studentBean.setLcName(centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getLc() : LC);
						studentBean.setInformation_center( centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getCenterName()  : IC);
				}
			}
		}
		return studentList;
	}
	
	public List<PassFailExamBean> getStudentPassList(StudentMarksBean studentMarks, String programType,
			int numberOfSubjectsToPass, String programStructure, boolean isLateral, String authorizedCenterCodes){
		List<PassFailExamBean> updatedList = new ArrayList<PassFailExamBean>();
		HashMap<String,StudentExamBean> studentlist = reportDao.getLateralStudentsByProgram(programType,authorizedCenterCodes,studentMarks.getSapid(),isLateral,programStructure);
	
		if(studentlist.size() > 0) {
		List<String> sapids = new ArrayList<>(studentlist.keySet());
		
		List<PassFailExamBean> studentSubjectList = reportDao.getPassSubjectDetails(sapids, studentMarks.getMonth(), studentMarks.getYear());

		studentSubjectList.stream()
                .forEach(userBean -> {
                	
                	try {
                	List<String> passedSubjects = Arrays.asList(userBean.getSubject().split("\",\""));
      
                	int noOfSubjectsCleared = passedSubjects.size();
                	int totalSubjectsCleared = 0;
                	if(isLateral) {
                		List<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(studentlist.get(userBean.getSapid()));
                		List<String> finalwaivedOffSubjects = studentService.mWaivedOffSubjectsForProgramComplete(studentlist.get(userBean.getSapid()),waivedOffSubjects,passedSubjects);
                		totalSubjectsCleared = noOfSubjectsCleared + finalwaivedOffSubjects.size();
                	}
                	else
                		totalSubjectsCleared = noOfSubjectsCleared;
             
                 	if(numberOfSubjectsToPass == totalSubjectsCleared) {
                 		userBean.setGender(studentlist.get(userBean.getSapid()).getGender());
                 		userBean.setProgram(studentlist.get(userBean.getSapid()).getProgram());
                 		updatedList.add(userBean);
                 	}
                	}catch(Exception e) {}
                });
		}
		return updatedList;
		
	}
}
