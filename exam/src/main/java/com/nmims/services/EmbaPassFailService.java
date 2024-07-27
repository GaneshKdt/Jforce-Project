package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.util.ContentUtil;
import com.nmims.util.StringUtility;

@Service("epfService")
public class EmbaPassFailService {
	//Certificate programs consumerProgramStructureIds list. 
	public static final List<Integer> NON_GRADED_MASTER_KEY_LIST = (List<Integer>) Arrays.asList(142,143,144,145,146,147,148,149);

	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	DissertationGradesheet_TranscriptService gradSheetService;
	
	@Value("${PDDM_PROGRAMS_LIST}")
	private String PDDM_PROGRAMS_LIST;
	
	@Value("${RESULT_PROCESSNG_MBA_WX_EXAM_MONTH}")
	private String RESULT_PROCESSNG_MBA_WX_EXAM_MONTH;
	
	@Value("${RESULT_PROCESSNG_MBA_WX_EXAM_YEAR}")
	private String RESULT_PROCESSNG_MBA_WX_EXAM_YEAR;
	
	@Value("${RESULT_PROCESSNG_PDDM_EXAM_MONTH}")
	private String RESULT_PROCESSNG_PDDM_EXAM_MONTH;
	
	@Value("${RESULT_PROCESSNG_PDDM_EXAM_YEAR}")
	private String RESULT_PROCESSNG_PDDM_EXAM_YEAR;
	
	private final static int IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static int IA_MASTER_DISSERTATION_SUBJECT_ID_FOR_Q7 = 1990;
	
	private final static int IA_MASTER_DISSERTATION_SUBJECT_ID_FOR_Q8 = 1991;
	
	private final static int IA_MASTER_DISSERTATION_SEM_FOR_Q7 = 7;
	
	private final static int IA_MASTER_DISSERTATION_SEM_FOR_Q8 = 8;
	
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	
	public ArrayList<EmbaPassFailBean> getClearedSemForStudent(String sapid){
	
		ArrayList<EmbaPassFailBean> subjectsToClearSem = new ArrayList<>();
		ArrayList<String> sems = new ArrayList<>();
		ArrayList<EmbaPassFailBean> passFailDataByTimeboundIdAndSapid = new ArrayList<>();
		String timeBoundIdList = "";
		String examYear = "",examMonth = "";
		ArrayList<EmbaPassFailBean> clearedSems = new ArrayList<>();
		
		sems = examsAssessmentsDAO.getSemsFromRegistration(sapid);
		
		StudentExamBean students = examsAssessmentsDAO.getSingleStudentsData(sapid);
		
		
		for(int i = 0 ;i < sems.size() ; i++) {
			 String old_sem =  sems.get(i);
//if(sems.get(i).equalsIgnoreCase("3")) {
//	sems.set(i, "4");
//}
			subjectsToClearSem = examsAssessmentsDAO.getSubjectToClearBySem(sems.get(i),sapid);
			
			if (IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(students.getConsumerProgramStructureId())) {
				try {
					if (IA_MASTER_DISSERTATION_SEM_FOR_Q7 == Integer.parseInt(old_sem)) {
						DissertationResultBean passFail = gradSheetService.getPassFail(sapid);
						if (null != passFail) {
						
							List<Integer> timeBoundIds = gradSheetService.getTimeboundUser(sapid);
							String commaSepratedTimeBoundIds = StringUtility.generateCommaSeprateStringByInteger(timeBoundIds);

							List<DissertationResultProcessingDTO> timeBound = gradSheetService.getTimeBounds(commaSepratedTimeBoundIds);
							DissertationResultProcessingDTO subjects = gradSheetService.getSubjectDetails(IA_MASTER_DISSERTATION_SUBJECT_ID_FOR_Q7);

							EmbaPassFailBean subjectsToClearSemForQ7 = gradSheetService.filterSubjectToClear(passFail,timeBound, subjects);
						
							subjectsToClearSem.add(subjectsToClearSemForQ7);
							
						}
					}
				} catch (Exception e) {
					dissertationLogs.info("Error while getting subject to clear data from Q7 passFail"+ " "+e);
				}
				
				try {
					if (IA_MASTER_DISSERTATION_SEM_FOR_Q8 == Integer.parseInt(old_sem)) {
						DissertationResultBean passFail = gradSheetService.getPassFailQ8(sapid);
						if (null != passFail) {
							
							List<Integer> timeBoundIds = gradSheetService.getTimeboundUser(sapid);
							String commaSepratedTimeBoundIds = StringUtility
									.generateCommaSeprateStringByInteger(timeBoundIds);

							List<DissertationResultProcessingDTO> timeBound = gradSheetService
									.getTimeBounds(commaSepratedTimeBoundIds);

							DissertationResultProcessingDTO subjects = gradSheetService
									.getSubjectDetails(IA_MASTER_DISSERTATION_SUBJECT_ID_FOR_Q8);

							EmbaPassFailBean subjectsToClearSemForQ8 = gradSheetService.filterSubjectToClear(passFail,
									timeBound, subjects);

							subjectsToClearSem.add(subjectsToClearSemForQ8);
							
						}
					}

				} catch (Exception e) {
					dissertationLogs.info("Error while getting subject to clear data from Q8 passFail"+ " "+e);
				}
			}
			// in case student has given no exam in the current cycle.
			if(subjectsToClearSem.size() == 0) {
				continue;
			}
			if(examYear == "") {
				examYear = subjectsToClearSem.get(0).getExamYear();
			}
			if(examMonth == "") {
				examMonth = subjectsToClearSem.get(0).getExamMonth();
			}
			sems.set(i, old_sem);
			
			for(int j = 0;j < subjectsToClearSem.size(); j++) {

				
				timeBoundIdList = timeBoundIdList + subjectsToClearSem.get(j).getId() + ",";
				
				if(IA_MASTER_DISSERTATION_SEM_FOR_Q8  == Integer.parseInt(old_sem)) {
				
					EmbaPassFailBean passFailForQ8 = gradSheetService.getPassFailForQ8(sapid);
					passFailDataByTimeboundIdAndSapid = new ArrayList<EmbaPassFailBean>();
					if(null!=passFailForQ8 && "Y".equalsIgnoreCase(passFailForQ8.getIsPass())
							&& !StringUtils.isEmpty(passFailForQ8.getGrade())
							&& !StringUtils.isEmpty(passFailForQ8.getPoints()))  {
						passFailDataByTimeboundIdAndSapid.add(passFailForQ8);
					}
				}else {
					passFailDataByTimeboundIdAndSapid = examsAssessmentsDAO.getPassFailDataByTimeboundIdAndSapid(timeBoundIdList,sapid,old_sem);
					boolean passFailStatus = passFailDataByTimeboundIdAndSapid.stream()
							.filter(sem -> IA_MASTER_DISSERTATION_SEM_FOR_Q7 == Integer.parseInt(sem.getSem()))
							.findFirst().isPresent();
					if (passFailStatus) {
						if (IA_MASTER_DISSERTATION_SEM_FOR_Q7 == Integer.parseInt(old_sem)) {
							EmbaPassFailBean passFailForQ7 = gradSheetService.getPassFailForQ7(sapid);
							if (null != passFailForQ7 && "Y".equalsIgnoreCase(passFailForQ7.getIsPass())) {
									passFailDataByTimeboundIdAndSapid.add(passFailForQ7);
								} 
							}
						}
					}
				
//				
			
				
				 int subject_to_clear = subjectsToClearSem.size();
				 
				 if("158".equals(students.getConsumerProgramStructureId()) ) {
					 subject_to_clear = 4;
				 }
				 else if("3".equalsIgnoreCase(old_sem) || "4".equalsIgnoreCase(old_sem)) {
					 subject_to_clear = 5;
				 }
				if(subject_to_clear == passFailDataByTimeboundIdAndSapid.size()) {	
					EmbaPassFailBean pf = new EmbaPassFailBean();
					pf.setExamYear(examYear);
					pf.setExamMonth(examMonth);
					pf.setSem(sems.get(i));
					clearedSems.add(pf);
					break;
				}
				
			}
			examYear = "";
			examMonth = "";
			timeBoundIdList = "";
			
		}
		
		
		
		return clearedSems;
	}
	
	public ArrayList<EmbaPassFailBean> fetchClearedSemForAStudent(String sapid){
		ArrayList<EmbaPassFailBean> subjectsToClearSem = new ArrayList<>();
		ArrayList<String> sems = new ArrayList<>();
		ArrayList<EmbaPassFailBean> passFailDataByTimeboundIdAndSapid = new ArrayList<>();
		String timeBoundIdList = "";
		String examYear = "",examMonth = "";
		ArrayList<EmbaPassFailBean> clearedSems = new ArrayList<>();
		
		sems = examsAssessmentsDAO.getSemsFromRegistration(sapid);
		
		for(int i = 0 ;i < sems.size() ; i++) {
			 String old_sem =  sems.get(i);
			 
				subjectsToClearSem = examsAssessmentsDAO.getSubjectToClearBySem(sems.get(i), sapid,
						ContentUtil.frameINClauseString(NON_GRADED_MASTER_KEY_LIST));
			
			// in case student has given no exam in the current cycle.
			if(subjectsToClearSem.size() == 0) {
				continue;
			}
			if(examYear == "") {
				examYear = subjectsToClearSem.get(0).getExamYear();
			}
			if(examMonth == "") {
				examMonth = subjectsToClearSem.get(0).getExamMonth();
			}
			sems.set(i, old_sem);
			
			for(int j = 0;j < subjectsToClearSem.size(); j++) {
				
				timeBoundIdList = timeBoundIdList + subjectsToClearSem.get(j).getId() + ",";
				passFailDataByTimeboundIdAndSapid = examsAssessmentsDAO.getNonGradePassFailDataByTimeboundIdAndSapid(timeBoundIdList,sapid,old_sem);
				
				 int subject_to_clear = subjectsToClearSem.size();
				 
				if(subject_to_clear == passFailDataByTimeboundIdAndSapid.size()) {	
					EmbaPassFailBean pf = new EmbaPassFailBean();
					pf.setExamYear(examYear);
					pf.setExamMonth(examMonth);
					pf.setSem(sems.get(i));
					clearedSems.add(pf);
					break;
				}
			}
			examYear = "";
			examMonth = "";
			timeBoundIdList = "";
		}
		return clearedSems;
	}
	
	
	public List<BatchExamBean> getBatchesList(String programType) throws Exception{
		List<Integer> consumerProgramStructureId = new ArrayList<Integer>();
		switch (programType) {
		case "MBA - WX":
			consumerProgramStructureId.add(111);
			consumerProgramStructureId.add(151);
			consumerProgramStructureId.add(160); 
			return examsAssessmentsDAO.getBatchesList(consumerProgramStructureId);
		case "M.Sc. (AI & ML Ops)":
			consumerProgramStructureId.add(131);
			return examsAssessmentsDAO.getBatchesList(consumerProgramStructureId);
		case "M.Sc. (AI)":	
			consumerProgramStructureId.add(158);
			return examsAssessmentsDAO.getBatchesList(consumerProgramStructureId);
		case "Modular PD-DM":
			consumerProgramStructureId.add(148);
			consumerProgramStructureId.add(144);
			consumerProgramStructureId.add(149);
			consumerProgramStructureId.add(142);
			consumerProgramStructureId.add(143);
			consumerProgramStructureId.add(147);
			consumerProgramStructureId.add(145);
			consumerProgramStructureId.add(146);
			return examsAssessmentsDAO.getBatchesList(consumerProgramStructureId);
		case "MBA - X":
			consumerProgramStructureId.add(119);
			consumerProgramStructureId.add(126);
			consumerProgramStructureId.add(162); 
			return examsAssessmentsDAO.getBatchesList(consumerProgramStructureId);
		default : 
			return examsAssessmentsDAO.getBatchesListForMbaWx();
		}
		
		
	}
	
	public ArrayList<TEEResultBean> getIAComponentOnlyEligibleStudentsForPassFail(String timeboundId) {
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
		try {
			eligibleList = examsAssessmentsDAO.getIAComponentOnlyEligibleStudentsForPassFail(Integer.parseInt(timeboundId));
			return eligibleList;
		}catch (Exception e) {
			// TODO: handle exception
			
			return eligibleList;
		}
	}
	
	public int passFailResultsLiveForIAComponentSubject(String timeboundId) throws Exception{
			return examsAssessmentsDAO.passFailResultsLiveForIAComponentSubject(Integer.parseInt(timeboundId));
		
	}

	public ArrayList<BatchExamBean> getActiveBatchList(String  programType, String acadMonth, String acadYear){
		
		List<String> programCode = programType.equalsIgnoreCase("Modular PD-DM") ? Stream.of(PDDM_PROGRAMS_LIST.split(",", -1)).collect(Collectors.toList()) : Arrays.asList(programType);
		List<BatchExamBean> batchList=examsAssessmentsDAO.getBatchesForAcadMonthYear(acadMonth, acadYear);
		List<ConsumerProgramStructureExam> cpsList = examsAssessmentsDAO.getConsumerProgramStructureId();
		List<ProgramsBean> programList = examsAssessmentsDAO.getProgramsList(programCode);
		return (ArrayList<BatchExamBean>)batchList.stream().filter(batch -> cpsList.stream()
				.filter(masterKey -> programList.stream().anyMatch(program -> program.getId().equalsIgnoreCase(masterKey.getProgramId())))
				.anyMatch(masterKey -> masterKey.getId().equalsIgnoreCase(batch.getConsumerProgramStructureId())))
				.collect(Collectors.toList()); //Using streams to get batches for a specific program type and acad year and acad month and filter out only those batches
		
	}
}
