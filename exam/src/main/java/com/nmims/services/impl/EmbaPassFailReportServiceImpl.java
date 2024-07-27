package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.DissertationResultDTO;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.Q7Q8DissertationResultBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.dto.TEEResultDTO;
import com.nmims.interfaces.IEmbaPassFailReportService;

@Service
public class EmbaPassFailReportServiceImpl implements IEmbaPassFailReportService{
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	public static final Logger logger =LoggerFactory.getLogger(EmbaPassFailReportServiceImpl.class);

	@Override
	public ArrayList<EmbaPassFailBean> searchEmbaMarks(TEEResultDTO resultBean, String authorizedCodes) throws Exception{
		// TODO Auto-generated method stub
		String masterKeys=null;
		if (!StringUtils.isBlank(resultBean.getConsumerTypeId()) && !StringUtils.isBlank(resultBean.getProgramStructureId()) && !StringUtils.isBlank(resultBean.getProgramId()) ) {
		List<String> masterKeyList=examsAssessmentsDAO.getMsterKey(resultBean.getConsumerTypeId(), resultBean.getProgramStructureId(), resultBean.getProgramId());
		StringJoiner joiner = new StringJoiner(",");
		if (!masterKeyList.isEmpty()) {
			for (String value : masterKeyList) {
				joiner.add("'" + value + "'");
			}
		}
		
         masterKeys = joiner.toString();
		}

		return examsAssessmentsDAO.getPassFailResultForReport(resultBean, authorizedCodes ,masterKeys);
	}

	@Override
	public Map<String, ArrayList<ConsumerProgramStructureExam>> getConsumerTypeByExamYearMonth(List<String> masterKeyList) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<ConsumerProgramStructureExam> masterKeyDetailList=examsAssessmentsDAO.getMsterKeyDetailsByid(masterKeyList);
		Set<String> programIdList=new HashSet<String>();
		Set<String> ConsumerTypeIdList=new HashSet<String>();
		Set<String> ProgramStructureIdList=new HashSet<String>();
		
		masterKeyDetailList.stream().forEach(bean->{
			programIdList.add(bean.getProgramId());
			ConsumerTypeIdList.add(bean.getConsumerTypeId());
			ProgramStructureIdList.add(bean.getProgramStructureId());
		});
		
	
		Map<String,ArrayList<ConsumerProgramStructureExam>> serachMap=new HashMap<String, ArrayList<ConsumerProgramStructureExam>>();
		ArrayList<ConsumerProgramStructureExam> consumerTypeList=new ArrayList<ConsumerProgramStructureExam>();
		ArrayList<ConsumerProgramStructureExam> programStructurList=new ArrayList<ConsumerProgramStructureExam>();
		ArrayList<ConsumerProgramStructureExam> programList=new ArrayList<ConsumerProgramStructureExam>();
		try {
		 consumerTypeList = examsAssessmentsDAO.getConsumerTypeid(ConsumerTypeIdList);
		 programStructurList = examsAssessmentsDAO.getProgramStructurByid(ProgramStructureIdList);
		 programList = examsAssessmentsDAO.getProgramByid(programIdList);
		}
		catch (Exception e) {
			throw e;
		}
		
		serachMap.put("consumerTypeList", consumerTypeList);
		serachMap.put("programStructurList", programStructurList);
		serachMap.put("programList", programList);
		
		return serachMap;
	}

	@Override
	public List<String> getMsterKeysByBatch(ConsumerProgramStructureExam consumerProgramStructure) throws Exception{
		// TODO Auto-generated method stub
		List<String> masterKeyList=examsAssessmentsDAO.getMsterKeysByBatch(consumerProgramStructure);
		List<String> uniqueMasterKeyList=masterKeyList.stream().distinct().collect(Collectors.toList());
		return uniqueMasterKeyList;
	}

	@Override
	public ArrayList<ConsumerProgramStructureExam> getBatchesByMsterKey (ConsumerProgramStructureExam resultBean) throws Exception{
		// TODO Auto-generated method stub
		List<String> masterKeyList=examsAssessmentsDAO.getMsterKey(resultBean.getConsumerTypeId(), resultBean.getProgramStructureId(), resultBean.getProgramId());
		List<String> uniqueMasterKeyList=masterKeyList.stream().distinct().collect(Collectors.toList());
		StringJoiner joiner = new StringJoiner(",");
		if (!uniqueMasterKeyList.isEmpty()) {
			for (String value : masterKeyList) {
				joiner.add("'" + value + "'");
			}
		}
		
        String masterKeys = joiner.toString();
		return examsAssessmentsDAO.getBatchesByMsterKey(resultBean,masterKeys);
	}
	
	

	@Override
	public Q7Q8DissertationResultBean getDissertionReport(TEEResultDTO resultBean, String authCode)
			throws Exception {
		
		if(StringUtils.isNotEmpty(resultBean.getSapid()))
		return getPassFailReporttBySapId(resultBean.getSapid());
		
		Q7Q8DissertationResultBean finalResult=new Q7Q8DissertationResultBean();
		//get pss ids from pss table
		ArrayList<ProgramExamBean> pssList=examsAssessmentsDAO.getPSSBySubjectForQ7Q8();
		ArrayList<String> masterKeyList=new ArrayList<String>();
		List<String> userInputmasterKeyList=new ArrayList<String>();
		boolean checkMasterKey=false;
		List<String> Q7PssList=new ArrayList<String>();
		List<String> Q8PssList=new ArrayList<String>();
		
		//get master keys from consumer_program_structure table based on user inputs
		userInputmasterKeyList=examsAssessmentsDAO.getMsterKey(resultBean.getConsumerTypeId(), resultBean.getProgramStructureId(), resultBean.getProgramId());
		// convert list of pss id into single string with '
		StringJoiner joiner = new StringJoiner(",");
		if (!pssList.isEmpty()) {
			for (ProgramExamBean value : pssList) {
				joiner.add("'" + value.getId() + "'");
				if(!(masterKeyList.contains(value.getConsumerProgramStructureId())))
				masterKeyList.add(value.getConsumerProgramStructureId());
				
				if(value.getName().equalsIgnoreCase("Masters Dissertation Part - I"))
					Q7PssList.add(value.getId());
				
				if(value.getName().equalsIgnoreCase("Masters Dissertation Part - II"))
					Q8PssList.add(value.getId());
			}
		}
		
		
		//check in user selected master key is it contains the subject-Masters Dissertation master key 
		checkMasterKey = masterKeyList.stream().anyMatch(userInputmasterKeyList::contains);
		
		
		//check the user selected batch is contains subject-Masters Dissertation pss or not
		List<String> stringList=new ArrayList<String>();
		if (resultBean.getBatchId() != null)
		stringList =  Arrays.stream(resultBean.getBatchId().split(",")).filter(s -> s != null && !s.isEmpty()).collect(Collectors.toList());
		if(stringList.size()==1) {
			List<String> userBatchSelectedPssIdList=examsAssessmentsDAO.getPssIdByBatchId(stringList.get(0));
//			checkMasterKey = Q7PssList.stream().anyMatch(userBatchSelectedPssIdList::contains);
//			checkMasterKey = Q8PssList.stream().anyMatch(userBatchSelectedPssIdList::contains);
			checkMasterKey = Stream.concat(Q7PssList.stream(), Q8PssList.stream()).anyMatch(userBatchSelectedPssIdList::contains);

		}
		
		
		//check the user selected pss is contains subject-Masters Dissertation pss or not
		if(StringUtils.isNotEmpty(resultBean.getTimebound_id())) {
			String userSelectedPssId=examsAssessmentsDAO.getPssIdByTimeBoundId(resultBean.getTimebound_id());			
			checkMasterKey = Q7PssList.contains(userSelectedPssId) || Q8PssList.contains(userSelectedPssId);
		}
		
		//if Masters Dissertation subject is contains in selected user inputs
		if(checkMasterKey) {
		String pssIds = joiner.toString();
		ArrayList<StudentSubjectConfigExamBean> timeboundIdList=examsAssessmentsDAO.getTimeboubdIdByPSSAndBatch(pssIds, resultBean.getBatchId(), resultBean.getExamMonth(), resultBean.getExamYear());
		Map<String,Integer> timeboundIdBatchIdMap=new HashMap<String, Integer>();
		
		List<String> q7timeboundIdList=new ArrayList<String>();
		List<String> q8timeboundIdList=new ArrayList<String>();
		ArrayList<DissertationResultDTO> q7ReportList=new ArrayList<DissertationResultDTO>();
		ArrayList<DissertationResultDTO> q8ReportList=new ArrayList<DissertationResultDTO>();
		timeboundIdList.forEach(StudentSubjectConfigExamBean->{
			timeboundIdBatchIdMap.put(StudentSubjectConfigExamBean.getId(), StudentSubjectConfigExamBean.getBatchId());
			if(Q7PssList.contains(StudentSubjectConfigExamBean.getPrgm_sem_subj_id())) {
				q7timeboundIdList.add(StudentSubjectConfigExamBean.getId());
			}
			else if(Q8PssList.contains(StudentSubjectConfigExamBean.getPrgm_sem_subj_id())) {
				q8timeboundIdList.add(StudentSubjectConfigExamBean.getId());
			}
			
		});
		
		//get pass fail record from q7 table
		if(!(q7timeboundIdList.isEmpty()) && q7timeboundIdList!=null) {
			q7ReportList=examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(q7timeboundIdList,resultBean.getSapid());
		}
		//get pass fail record from q7 table
		if(!(q8timeboundIdList.isEmpty()) && q8timeboundIdList!=null) {
			q8ReportList=examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(q8timeboundIdList,resultBean.getSapid());
		}
		//get distinct sapid list to get student information student table
		List<Long> sapidsQ7List = q7ReportList.stream().map(x -> x.getSapid()).distinct().collect(Collectors.toList());
		List<Long> sapidsQ8List = q8ReportList.stream().map(x -> x.getSapid()).distinct().collect(Collectors.toList());
		List<Long> sapidsQ7Q8List=new ArrayList<Long>(sapidsQ7List);
		sapidsQ7Q8List.addAll(sapidsQ8List);
		//if data not present in db
		if(sapidsQ7Q8List.isEmpty())
			return new Q7Q8DissertationResultBean();
		
		List<DissertationResultDTO> q7q8studentIfoList=examsAssessmentsDAO.getStudentInfoBySapId(sapidsQ7Q8List);
		
		
		Map<Long,DissertationResultDTO> q8q7studentInfoMap=q7q8studentIfoList.stream().collect(Collectors.toMap(DissertationResultDTO :: getSapid, Function.identity()));
		List<Integer> batchIds = timeboundIdBatchIdMap.keySet().stream().map(timeboundId->timeboundIdBatchIdMap.get(timeboundId)).collect(Collectors.toList());
		Map<String,String> batchIdAndBatchNameMap = examsAssessmentsDAO.getBatchIdAndBatchNameMap(batchIds);
		Map<String,String> pssIdSubjectMap = pssList.stream().collect(Collectors.toMap(key->key.getId(), value->value.getName()));
		
		q7ReportList=getReportInfo(q7ReportList,pssIdSubjectMap,q8q7studentInfoMap,batchIdAndBatchNameMap,timeboundIdBatchIdMap);
		q8ReportList=getReportInfo(q8ReportList,pssIdSubjectMap,q8q7studentInfoMap,batchIdAndBatchNameMap,timeboundIdBatchIdMap);
		
		logger.info(" {} getDissertionReport() :: q7ReportList"+q7ReportList);
		logger.info("{} getDissertionReport() :: q8ReportList"+q8ReportList);
		//set all q7 abd q8 report list in final bean
		finalResult.setQ7ResultList(q7ReportList);
		finalResult.setQ8ResultList(q8ReportList);
		
		}
		return finalResult;
	}
	
	
	private static ArrayList<DissertationResultDTO> getReportInfo( ArrayList<DissertationResultDTO> q7Q8ReportList,Map<String,String> pssIdSubjectMap,
			Map<Long,DissertationResultDTO> studentInfoMap,Map<String,String> batchIdAndBatchNameMap,Map<String,Integer> timeboundIdBatchIdMap){
		
		q7Q8ReportList.stream().forEach(reportBean->{
			DissertationResultDTO student=studentInfoMap.get(reportBean.getSapid());
			reportBean.setFirstName(student.getFirstName());
			reportBean.setLastName(student.getLastName());
			reportBean.setProgram(student.getProgram());
			reportBean.setCenterCode(student.getCenterCode());
			reportBean.setCenterName(student.getCenterName());
			String batchNameSem = batchIdAndBatchNameMap.get(String.valueOf(timeboundIdBatchIdMap.get(String.valueOf(reportBean.getTimeBoundId()))));
			reportBean.setSem(batchNameSem.substring(batchNameSem.indexOf("~")+1, batchNameSem.length()));
			reportBean.setBatchName(batchNameSem.substring(0,batchNameSem.indexOf("~")));
			reportBean.setSubject(pssIdSubjectMap.get(String.valueOf(reportBean.getPrgm_sem_subj_id())));
		});
		
		return q7Q8ReportList;
	}
	
	private  Q7Q8DissertationResultBean getPassFailReporttBySapId(String sapid) throws Exception{
		//pass fail report
		Q7Q8DissertationResultBean finalReportBean=new Q7Q8DissertationResultBean();
		ArrayList<DissertationResultDTO> q7ReportList=new ArrayList<DissertationResultDTO>();
		ArrayList<DissertationResultDTO> q8ReportList=new ArrayList<DissertationResultDTO>();
		q7ReportList=examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(new ArrayList<String>(),sapid);
		q8ReportList=examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(new ArrayList<String>(),sapid);
		ArrayList<DissertationResultDTO> ReportList=new ArrayList<DissertationResultDTO>(q7ReportList);
		ReportList.addAll(q8ReportList);
		
		//return if student pass fail record not found
		if(ReportList.isEmpty())
			return new Q7Q8DissertationResultBean();
		
		List<StudentSubjectConfigExamBean> timeBoundIdList=examsAssessmentsDAO.getBatchIdByTimeBoundId(ReportList.stream().map(DissertationResultDTO :: getTimeBoundId).collect(Collectors.toList()));
		Map<String,String> timeBoundIdMap=timeBoundIdList.stream().collect(Collectors.toMap(key->key.getId(), value->String.valueOf(value.getBatchId())));
		
		
		List<Integer> batchIdList=timeBoundIdMap.keySet().stream().map(timeBoundId->Integer.parseInt(timeBoundIdMap.get(timeBoundId))).collect(Collectors.toList());
		Map<String,DissertationResultDTO> batchInfoMap=examsAssessmentsDAO.getBatchIdAndBatchNameBeanMap(batchIdList);
		
		ArrayList<ProgramExamBean> q7q8SubjectInfo=examsAssessmentsDAO.getPSSBySubjectForQ7Q8();
		Map<String, String> pssIdAndSubjectSemMap=q7q8SubjectInfo.stream().collect(Collectors.toMap(key->key.getId(), value->value.getName()));
		
		List<Long> sapidList=new ArrayList<Long>();
		sapidList.add(Long.parseLong(sapid));
		
		List<DissertationResultDTO> studentInfo=examsAssessmentsDAO.getStudentInfoBySapId(sapidList);
		
		q7ReportList.stream().forEach(reportBean->{
			DissertationResultDTO student=studentInfo.get(0);
			reportBean.setFirstName(student.getFirstName());
			reportBean.setLastName(student.getLastName());
			reportBean.setProgram(student.getProgram());
			reportBean.setCenterCode(student.getCenterCode());
			reportBean.setCenterName(student.getCenterName());
			reportBean.setSem(batchInfoMap.get(timeBoundIdMap.get(String.valueOf(reportBean.getTimeBoundId()))).getSem());
			reportBean.setBatchName(batchInfoMap.get(timeBoundIdMap.get(String.valueOf(reportBean.getTimeBoundId()))).getBatchName());
			reportBean.setSubject(pssIdAndSubjectSemMap.get(String.valueOf(reportBean.getPrgm_sem_subj_id())));
			
		});
		
		q8ReportList.stream().forEach(reportBean->{
			DissertationResultDTO student=studentInfo.get(0);
			reportBean.setFirstName(student.getFirstName());
			reportBean.setLastName(student.getLastName());
			reportBean.setProgram(student.getProgram());
			reportBean.setCenterCode(student.getCenterCode());
			reportBean.setCenterName(student.getCenterName());
			reportBean.setSem(batchInfoMap.get(timeBoundIdMap.get(String.valueOf(reportBean.getTimeBoundId()))).getSem());
			reportBean.setBatchName(batchInfoMap.get(timeBoundIdMap.get(String.valueOf(reportBean.getTimeBoundId()))).getBatchName());
			reportBean.setSubject(pssIdAndSubjectSemMap.get(String.valueOf(reportBean.getPrgm_sem_subj_id())));
			
		});
		
		logger.info(" {} getPassFailReporttBySapId() :: q7ReportList"+q7ReportList);
		logger.info("{} getPassFailReporttBySapId() :: q8ReportList"+q8ReportList);
		finalReportBean.setQ7ResultList(q7ReportList);
		finalReportBean.setQ8ResultList(q8ReportList);
		
		return finalReportBean;
	}
	
	

}
