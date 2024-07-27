package com.nmims.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.PaymentGatewayTransactionBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBean;
import com.nmims.beans.VivaSlotBookingConfigBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.helpers.LevelBasedProjectHelper;

@Component
public class LevelBasedProjectService {

	@Autowired
	LevelBasedProjectHelper levelBasedProjectHelper;

	@Autowired
	ProjectTitleDAO projectTitleDAO;
	
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO;
	
	@Autowired
	ApplicationContext act;
	
	public void excelUpload(HttpServletRequest request,LevelBasedProjectBean levelBasedProjectBean) {
		try {
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public void viewVivaSlotBooking(StudentExamBean studentBean) {
		 
	}
	
	public List<VivaSlotBean> getSlotDateTimeByDate(String date) {
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		return levelBasedProjectDAO.getVivaSlotsDateTimeByDate(eDao.getLiveExamYear(), eDao.getLiveExamMonth(), date);
	}
	
	public ProjectConfiguration getProjectConfigurationForSubjectId(String examMonth, String examYear, String programSemSubjId) {
		return projectTitleDAO.getSingleLevelBasedProjectConfiguration(examYear, examMonth, programSemSubjId);
	}
	
	public boolean checkIfProgramSemSubjectIdApplicableForStudent(String sapid, String subject) {
		return projectTitleDAO.checkIfProgramSemSubjectIdApplicableForStudent(sapid, subject);
	}
	
	public String getProgramSemSubjectIdForStudent(String sapid, String subject) {
		return projectTitleDAO.getProgramSemSubjectIdForStudent(sapid, subject);
	}

	public List<ProjectModuleExtensionBean> getProjectExtensionsListForStudent(String examMonth, String examYear, String sapid, String programSemSubjId) {
		return projectTitleDAO.getProjectExtensionsListForStudent(examYear, examMonth, sapid, programSemSubjId);
	}


	public LevelBasedSOPConfigBean getSOPConfiguration(String examYear, String examMonth, String programSemSubjId) {
		return projectTitleDAO.getSOPConfiguration(examYear, examMonth, programSemSubjId);
	}


	public UploadProjectSOPBean getSOPSubmissionForStudent(String examYear, String examMonth, String sapid) {
		return projectTitleDAO.getSOPCount(examYear, examMonth, sapid);
	}

	public boolean getSOPSubmissionStatusForStudent(String examYear, String examMonth, String sapid) {
		return projectTitleDAO.getSOPSubmissionStatusForStudent(examYear, examMonth, sapid);
	}
	
	public boolean getSOPSubmissionStatusForStudentV2(String examYear, String examMonth, String sapid) {
		return projectTitleDAO.getSOPSubmissionStatusForStudentV2(examYear, examMonth, sapid);
	}


	public String getSOPGuideNameForStudent(String examYear, String examMonth, String sapid, String subject) {
		FacultyExamBean faculty = projectTitleDAO.getSOPGuideNameForStudent(examYear, examMonth, sapid, subject);
		return faculty.getFirstName() + " " + faculty.getLastName();
	}


	public LevelBasedSynopsisConfigBean getSynopsisConfiguration(String examYear, String examMonth,
			String programSemSubjectId) {
		return projectTitleDAO.getSynopsisConfiguration(examYear, examMonth, programSemSubjectId);
	}


	public UploadProjectSynopsisBean getSynopsisSubmissionForStudent(String examYear, String examMonth, String sapid) {
		return projectTitleDAO.getSynopsisCount(examYear, examMonth, sapid);
	}
	public boolean getSynopsisSubmissionStatusForStudent(String examYear, String examMonth, String sapid) {
		return projectTitleDAO.getSynopsisSubmissionStatusForStudent(examYear, examMonth, sapid);
	}
	

	//For Checking Synopsis end date of SOP

	////Download Eligible List for PD-WM Module 4 report
	
	public List<StudentExamBean> getProjectApplicableStudents(String CPSID,String sem) {
				return projectTitleDAO.getProjectApplicableStudents(CPSID,sem);
	}
	
	public StudentExamBean getProjectConfigurationMonthYear() {
		return projectTitleDAO.getProjectConfigurationMonthYear();
	}
	
	//Check if student is valid or not
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	public boolean isStudentValid(String sapid,String ValidityEndMonth,String ValidityEndYear) throws ParseException {
		String date = "";
		if(sapid.startsWith("77")){
			String validityEndMonthStr = ValidityEndMonth;
			int validityEndYear = Integer.parseInt(ValidityEndYear);
			Date lastAllowedAcccessDate = null;
			int validityEndMonth = 0;
			if("Jun".equals(validityEndMonthStr)){
				validityEndMonth = 6;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Dec".equals(validityEndMonthStr)){
				validityEndMonth = 12;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Sep".equals(validityEndMonthStr)){
				validityEndMonth = 9;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Apr".equals(validityEndMonthStr)){
				validityEndMonth = 4;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Oct".equals(validityEndMonthStr)){
				validityEndMonth = 10;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Aug".equals(validityEndMonthStr)){
				validityEndMonth = 8;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Feb".equals(validityEndMonthStr)){
				validityEndMonth = 2;
				date = validityEndYear + "/" + validityEndMonth + "/" + "28";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Mar".equals(validityEndMonthStr)){
				validityEndMonth = 3;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jan".equals(validityEndMonthStr)){
				validityEndMonth = 1;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("May".equals(validityEndMonthStr)){
				validityEndMonth = 5;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jul".equals(validityEndMonthStr)){
				validityEndMonth = 7;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}

			Calendar now = Calendar.getInstance();
			int currentExamYear = now.get(Calendar.YEAR);
			int currentExamMonth = (now.get(Calendar.MONTH) + 1);

			if(currentExamYear < validityEndYear  ){
				return true;
			}else if(currentExamYear == validityEndYear && currentExamMonth <= validityEndMonth){
				return true;
			}else{
				Date currentDate = new Date();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(lastAllowedAcccessDate);
					
				if(currentDate.before(cal.getTime())){
					return true;
				}else {
					return false;
				}
			}
			}else{
				return true;
			}
	}
	
	public boolean checkIfStudentPassProject(String sapid,String subject,String isPass) {
		return projectTitleDAO.checkIfStudentPassProject(sapid,subject,isPass);
	}
	//Download Eligible List for PD-WM Module 4 report
	
	////Download SOP & Synopsis Submission Report
	public List<UploadProjectSOPBean> getSOPSubmissionList(String month,String year) {
		return projectTitleDAO.getSOPSubmissionList(month,year);
	}
	
	public List<PaymentGatewayTransactionBean> getSOPorSynopsisTransactionList(List<String> trackIDList){
		return projectTitleDAO.getSOPorSynopsisTransactionList(trackIDList);
	}
	
	public List<UploadProjectSynopsisBean> getSynopsisSubmissionList(String month,String year) {
		return projectTitleDAO.getSynopsisSubmissionList(month,year);
	}
	//Download SOP & Synopsis Submission Report
	
	////Download Synopsis Evaluated Score Report
	public List<UploadProjectSynopsisBean> getSynopsisEvaluatedScoreList(String month,String year,String evaluated) {
		return projectTitleDAO.getSynopsisEvaluatedScoreList(month,year,evaluated);
	}
	
	public String getCPSID(String sapid) {
		return projectTitleDAO.getCPSID(sapid);
	}
	public List<String> getPSSIDList(String CPSID) {
		return projectTitleDAO.getPSSIDList(CPSID);
	}
	
	public String getProgramSemSubjectId(String month, String year, String pssIDList){
		return projectTitleDAO.getProgramSemSubjectId(month,year,pssIDList);
	}
	
	public ProjectModuleExtensionBean getProjectExtensionsEndDateModuleType(String examMonth, String examYear, String sapid, String moduleType) {
		return projectTitleDAO.getProjectExtensionsEndDateModuleType(examMonth,examYear,sapid,moduleType);
	}
	//Download Synopsis Evaluated Score Report

	public VivaSlotBookingConfigBean getVivaConfiguration(String examYear, String examMonth, String programSemSubjId) {
		return projectTitleDAO.getSingleLevelBasedVivaConfiguration(examYear, examMonth, programSemSubjId);
	
	}


	public boolean isResitCycleMonth(String examMonth) {
		
		List<String> resitCycleMonth = new ArrayList(Arrays.asList("Apr","Sep"));
		
		if(resitCycleMonth.contains(examMonth)) {
			return true;
		}
		return false;
	}


	public LevelBasedProjectBean getRecentStudentGuideMapping(String sapid) {
		LevelBasedProjectBean recentMappingBean = null;
		try {
			recentMappingBean = levelBasedProjectDAO.getRecentStudentGuideMapping(sapid);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return recentMappingBean;
	}


	public ProjectConfiguration getSingleLevelBasedProjectConfiguration(String examYear, String examMonth,
			String pssId) {
		return projectTitleDAO.getSingleLevelBasedProjectConfiguration(examYear, examMonth, pssId);
	}
}
