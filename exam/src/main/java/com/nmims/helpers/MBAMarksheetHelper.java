package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXExamResultsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.services.MBAResultMonthYearComparatorService;
import com.nmims.util.SubjectsCerditsConfiguration;

@Component
public class MBAMarksheetHelper {

	@Autowired
	ApplicationContext act;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;
	
	@Autowired
	private MBAWXExamResultsDAO mbawxResultsDao;
	
	@Autowired
	DissertationGradesheet_TranscriptService transcriptService;
	
	private final static int IA_MASTER_DISSERTATION_SEM_FOR_Q7 = 7;
	
	private final static int IA_MASTER_DISSERTATION_SEM_FOR_Q8 = 8;
	
	private final static int IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static List<Integer> ONE_TO_THREE_SEM = Arrays.asList(1,2,3);
	
	private final static List<Integer> FOUR_AND_FIVE_SEM = Arrays.asList(4,5);
	
	private final static List<Integer> SIX_TO_EIGHT_SEM = Arrays.asList(6,7,8);
	
	private final static String MBAWX_JUL22_MASTERKEY = "160";
	
	
	private static final Logger trasncript_logger = LoggerFactory.getLogger("trasncript");
	public MBATranscriptBean getTranscriptBeanForSapid(String sapid) {

		MBATranscriptBean transcriptBean = new MBATranscriptBean();
		transcriptBean.setSapid(sapid);
		transcriptBean.setLogoRequired("Y");
		
		
		// Get the students details
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = dao.getSingleStudentsData(transcriptBean.getSapid());		
		transcriptBean.setStudent(student);
		
		/*
		 * 
		 * commented out by harsh
		 * specialization was not ordered alphabetically, needed fix 
		 * based on card 9897
		 * List<String> studentSpecializations = mbawxResultsDao.getStudentSpecialisations(sapid);
		 * transcriptBean.setSpecialisation(String.join(" | ", studentSpecializations)); 
		 * */
		String studentSpecializations = mbawxResultsDao.getStudentSpecialisations(sapid);
		transcriptBean.setSpecialisation(studentSpecializations);
		
		// Get marksheet Bean For each of the semsesters
		Map<Integer, MBAMarksheetBean> marksheetList = new HashMap<Integer, MBAMarksheetBean>();
		
		try {
		Map<String, Integer> semTotalSubjectsMap = getSemAndSubjectMapForStudent(sapid);
		
		for(int index = 1; index <= semTotalSubjectsMap.size(); index++) {
			int totalSubjectsInSem = semTotalSubjectsMap.get(index + "");
			
		
		if(( 3==index || 4==index) && ( "151".equals(student.getConsumerProgramStructureId()) ||  "111".equals(student.getConsumerProgramStructureId()))) {
			totalSubjectsInSem = 5;
		} else if (5==index && ( "151".equals(student.getConsumerProgramStructureId()) ||  "111".equals(student.getConsumerProgramStructureId())) ) {
				totalSubjectsInSem = "151".equals(student.getConsumerProgramStructureId()) ? 3 : 1;
		}  else if (4 ==index && ( "131".equals(student.getConsumerProgramStructureId())) ) {
			totalSubjectsInSem = 3;
		}
		else if (5==index && ( "131".equals(student.getConsumerProgramStructureId())) ) {
			totalSubjectsInSem = 4;
		}
		else if(ONE_TO_THREE_SEM.contains(index) && MBAWX_JUL22_MASTERKEY.equals(student.getConsumerProgramStructureId())) {
			totalSubjectsInSem = 4;
		}
		else if(FOUR_AND_FIVE_SEM.contains(index) && MBAWX_JUL22_MASTERKEY.equals(student.getConsumerProgramStructureId())) {
			totalSubjectsInSem = 5;
		}
		else if(SIX_TO_EIGHT_SEM.contains(index) && MBAWX_JUL22_MASTERKEY.equals(student.getConsumerProgramStructureId())) {
			totalSubjectsInSem = 1;
		}
		MBAMarksheetBean marksheetBean = getMarksheetBeanForStudentForTerm(student, index, totalSubjectsInSem);
		
		marksheetList.put(index, marksheetBean);
	}
		transcriptBean.setSemSubjectList(marksheetList);
		transcriptBean.setLogoRequired("Y");
		
		if(StringUtils.isBlank(marksheetList.get(semTotalSubjectsMap.size()).getClearExamMonth()) || StringUtils.isBlank(marksheetList.get(semTotalSubjectsMap.size()).getClearExamYear())) {
			transcriptBean.setPassYearMonth("Pursuing");
		} else {
			transcriptBean.setPassYearMonth(marksheetList.get(semTotalSubjectsMap.size()).getClearExamMonth() + "-" + marksheetList.get(semTotalSubjectsMap.size()).getClearExamYear() );
		}
		}catch (Exception e) {
			trasncript_logger.error("Error for creating transcriptBean " +  e.getMessage() +"for sem "+ student.getSem());
		}
		return transcriptBean;
	}
	
	private MBAMarksheetBean getMarksheetBeanForStudentForTerm(StudentExamBean student, int term, int totalSubjectsInSem)throws Exception {

		MBAMarksheetBean studentMarks = new MBAMarksheetBean();
	
		MBAPassFailBean passFail  =  new MBAPassFailBean();
		studentMarks.setSapid(student.getSapid());
		studentMarks.setProgram(student.getProgram());
		studentMarks.setTerm("" + term);
		studentMarks.setConsumerProgramStructureId(student.getConsumerProgramStructureId());

		List<MBAPassFailBean> passFailDataList = mbawxResultsDao.getPassFailBySapidForTerm(student.getSapid(), term);
		
		
		if(IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
			if(IA_MASTER_DISSERTATION_SEM_FOR_Q7 == term) {
				//getting passfail for Q7
				passFail =  transcriptService.getPassFailForQ7Transcript(student.getSapid());
			
				if(null != passFail && !StringUtils.isEmpty(passFail.getGrade())) {
				 //passFail.setSubject(IA_MASTER_DISSERTATION_SUBJECT_FOR_Q7);
				 passFail.setTerm(String.valueOf(term));
				 passFailDataList.add(passFail);
			 }
			}
			
			if(IA_MASTER_DISSERTATION_SEM_FOR_Q8 == term) {
				
				//getting passfailFor Q8
				passFail =  transcriptService.getPassFailForQ8Transcript(student.getSapid());
				
				if(null!=passFail && !StringUtils.isEmpty(passFail.getGrade())) {
					//passFail.setSubject(IA_MASTER_DISSERTATION_SUBJECT_FOR_Q8);
					passFail.setTerm(String.valueOf(term));
					passFailDataList.add(passFail);
				}
			}
			
		}
		studentMarks.setMarksList(passFailDataList);
		
		int numberOfSubjectsAppearedInThisSem = passFailDataList.size();
		
		if(numberOfSubjectsAppearedInThisSem < totalSubjectsInSem) {
			studentMarks.setAppearedForTerm(false);
			studentMarks.setTermCleared(false);
		} else {
			studentMarks.setAppearedForTerm(true);
			// Calculate GPA and CGPA only if student has appeared for all subjects in this term 
			setGPAForTermMarksheet(studentMarks);
			
			// CGPA requires marks for all previous semesters
			List<MBAPassFailBean> passFailDataListAllSem = mbawxResultsDao.getPassFailForCGPACalculation(student.getSapid(), term);
			if(IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
				if(IA_MASTER_DISSERTATION_SEM_FOR_Q7 ==term) {
					
					//grade should not be null, grade calculation is mandatory for showing gradeSheet
					if(null!=passFail && !StringUtils.isEmpty(passFail.getGrade())) {
						passFailDataListAllSem.add(passFail);
					}
				}
				if(IA_MASTER_DISSERTATION_SEM_FOR_Q8 == term) {
				//getting Q7 details in q8 for CGPA calculation
					MBAPassFailBean	passFailForQ7 =  transcriptService.getPassFailForQ7Transcript(student.getSapid());
					
				//grade should not be null, grade calculation is mandatory for showing gradeSheet
				if(null!=passFail && !StringUtils.isEmpty(passFail.getGrade())) {
					passFailForQ7.setTerm(String.valueOf(IA_MASTER_DISSERTATION_SEM_FOR_Q7));
					passFailDataListAllSem.add(passFailForQ7);
					passFailDataListAllSem.add(passFail);
				}
			}
			}
			
			setCGPAForTermMarksheet(studentMarks, passFailDataListAllSem);
			
			// Term remark is calculated by GPA obtained
			setTermRemark(studentMarks);
		}
//		if(term == 5) {
		setExamMonthYearForMarksheet(studentMarks);	
//		}

		return studentMarks;
	}
	
	private void setExamMonthYearForMarksheet(MBAMarksheetBean studentMarks)throws Exception {
		// Clear Month/Year only to be added if the sem is clear
		if(studentMarks.getMarksList() != null && studentMarks.getMarksList().size() > 0) {
			// Loop through all exams for this term.
			MBAPassFailBean marksBeanForLatestExamYearMonth;
			List<MBAPassFailBean> yearMonthList = new ArrayList<MBAPassFailBean>();
			for (MBAPassFailBean subjectResult : studentMarks.getMarksList()) {
				if("Y".equals(subjectResult.getIsPass())) {
					yearMonthList.add(subjectResult);
				}
			}
			MBAResultMonthYearComparatorService comparator = new MBAResultMonthYearComparatorService();
			yearMonthList.sort(comparator);

			Collections.reverse(yearMonthList);
			if(yearMonthList.size() > 0) {
				marksBeanForLatestExamYearMonth = yearMonthList.get(0);
				if(studentMarks.isTermCleared()) {
					studentMarks.setClearExamMonth(marksBeanForLatestExamYearMonth.getExamMonth());
					studentMarks.setClearExamYear(marksBeanForLatestExamYearMonth.getExamYear());
				}
			}
		}
	}

	private void setGPAForTermMarksheet(MBAMarksheetBean marksheet)throws Exception {

		// GPA is only for the CURRENT semester 
		// GPA = (Sum of credits obtained) / (Total credits)
		// Credits Obtained for a subject = (Grade Point for subject) * (Number Of Credits For Subject)
		
		double totalCredits = 0.0;
		double totalCreditsObtained = 0.0;
		for (MBAPassFailBean subjectResult : marksheet.getMarksList()) {	
			float subjectGradePoint = Float.parseFloat(subjectResult.getPoints());
			double subjectCredits = 4.0;
			if(subjectResult.getTerm().equals("5") && "1883".equals(subjectResult.getPrgm_sem_subj_id())) {
				subjectCredits = 20.0;
			}else if("2351".equals(subjectResult.getPrgm_sem_subj_id())) {
				subjectCredits = 12.0;
			}else if("160".equals(marksheet.getConsumerProgramStructureId())){
				subjectCredits=SubjectsCerditsConfiguration.getMBAWXSubjectCredits(Integer.parseInt(subjectResult.getPrgm_sem_subj_id()));
			}else if("158".equals(marksheet.getConsumerProgramStructureId()) ||"155".equals(marksheet.getConsumerProgramStructureId()) ||"154".equals(marksheet.getConsumerProgramStructureId())) {
				subjectCredits=SubjectsCerditsConfiguration.getMSCAISubjectCredits(subjectResult.getSubject());
			}else if ("131".equals(marksheet.getConsumerProgramStructureId())) {
				subjectCredits=SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(subjectResult.getSubject());
			}
			subjectResult.setCredits("" + subjectCredits);
		
			double creditsObtained = subjectGradePoint * subjectCredits;
			totalCreditsObtained = totalCreditsObtained + creditsObtained;
			totalCredits = totalCredits + subjectCredits;
		}
		double termGPA = totalCreditsObtained / totalCredits;
		marksheet.setGpa(String.format ("%,.2f", termGPA)+"");
	}


	private void setCGPAForTermMarksheet(MBAMarksheetBean marksheet, List<MBAPassFailBean> passFailDataListAllSem)throws Exception {

		// CGPA is only for the current and all previous semesters
		
		// CGPA = (Sum of credits obtained) / (Total credits)
		// Credits Obtained for a subject = (Grade Point for subject) * (Number Of Credits For Subject)
		// Ex. For sem 3, the CGPA would be (Sum of credits obtained in sem 1, 2 and 3) / (Total credits in sem 1, 2 and 3) 
		
		double totalCredits = 0.0;
		double totalCreditsObtained = 0.0;
		for (MBAPassFailBean subjectResult : passFailDataListAllSem) {
			double subjectCredits = 4.0;
			if(subjectResult.getTerm().equals("5") && "1883".equals(subjectResult.getPrgm_sem_subj_id())) {
				subjectCredits = 20.0;
			}else if("2351".equals(subjectResult.getPrgm_sem_subj_id())) {
				subjectCredits = 12.0;
			}else if("160".equals(marksheet.getConsumerProgramStructureId())) {
				subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(Integer.parseInt(subjectResult.getPrgm_sem_subj_id()));
			}else if("158".equals(marksheet.getConsumerProgramStructureId()) ||"155".equals(marksheet.getConsumerProgramStructureId()) ||"154".equals(marksheet.getConsumerProgramStructureId())) {
				subjectCredits=SubjectsCerditsConfiguration.getMSCAISubjectCredits(subjectResult.getSubject());
			}else if ("131".equals(marksheet.getConsumerProgramStructureId())) {
				subjectCredits=SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(subjectResult.getSubject());
			}
			subjectResult.setCredits("" + subjectCredits);
			double subjectGradePoint = Float.parseFloat(subjectResult.getPoints());
			
			double creditsObtained = subjectGradePoint * subjectCredits;
			totalCreditsObtained = totalCreditsObtained + creditsObtained;
			totalCredits = totalCredits + subjectCredits;
		}	
		double termCGPA = totalCreditsObtained / totalCredits;
		marksheet.setCgpa(String.format ("%,.2f", termCGPA)+"");
	}

	private void setTermRemark(MBAMarksheetBean marksheet) throws Exception{

		int numberOfSubjects = marksheet.getMarksList().size();
		
		int passCount = 0;
		for (MBAPassFailBean subjectResult : marksheet.getMarksList()) {
			if(subjectResult.getIsPass().equals("Y")) {
				passCount ++;
			}
		}
		double gpa = Float.parseFloat(marksheet.getGpa());
		String remark = "";
		
		if(numberOfSubjects == passCount) {
			marksheet.setTermCleared(true);
			if(gpa >= 3.5) {
				remark = "PASS WITH DISTINCTION";
			} else {
				remark = "PASS";
			}
		} else {
			marksheet.setTermCleared(false);
			remark = "FAIL";
		}
		
		marksheet.setRemark(remark);
	}
	
	private Map<String, Integer> getSemAndSubjectMapForStudent(String sapid) throws Exception {
		Map<String, Integer> semSubjectCountMap = new HashMap<String, Integer>();

		// get all subjects and sort them by sem
		List<ProgramSubjectMappingExamBean> subjectMappingBeans = mbawxResultsDao.getAllProgramSemSubjectsStudent(sapid);
		for (ProgramSubjectMappingExamBean programSubjectMappingBean : subjectMappingBeans) {
			String sem = programSubjectMappingBean.getSem();
			int totalSubjectsForSem = semSubjectCountMap.containsKey(sem) ? semSubjectCountMap.get(sem) : 0;

			totalSubjectsForSem ++;
			semSubjectCountMap.put(sem, totalSubjectsForSem);
		}
		return semSubjectCountMap;
	}	
}
