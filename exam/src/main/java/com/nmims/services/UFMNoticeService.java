package com.nmims.services;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.nmims.beans.Page;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMIncidentExcelBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.beans.UFMResponseBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.stratergies.UFMActionStrategyInterface;
import com.nmims.stratergies.UFMDocumentGeneratorStrategyInterface;
import com.nmims.stratergies.UFMShowCauseStrategyInterface;
import com.nmims.stratergies.impl.UFMActionStrategy;
import com.nmims.stratergies.impl.UFMDocumentGeneratorStrategy;
import com.nmims.stratergies.impl.UFMMarkedStudentsListStrategy;
import com.nmims.stratergies.impl.UFMShowCauseStrategy;
import com.nmims.stratergies.impl.UFMStudentStrategy;

@Service
public class UFMNoticeService {

	@Autowired
	UFMShowCauseStrategy ufmShowCauseStrategy;
	
	@Autowired
	UFMShowCauseStrategyInterface ufmShowCauseStrategyI;
	
	@Autowired
	UFMMarkedStudentsListStrategy ufmMarkedStudentsListStrategy;
	
	@Autowired
	UFMDocumentGeneratorStrategy ufmDocumentGeneratorStrategy;
	
	@Autowired
	UFMDocumentGeneratorStrategyInterface ufmDocumentGeneratorStrategyI;
	
	@Autowired
	UFMActionStrategy ufmActionStrategy;
	
	@Autowired
	UFMActionStrategyInterface ufmActionStrategyI;
	
	@Autowired
	UFMStudentStrategy ufmStudentStrategy;
	
	@Autowired
	UFMNoticeDAO ufmNoticeDAO;
	
	@Autowired
	ApplicationContext act;
	private static final String blankCellErrorMessage = "Blank Cells Found <br>";
	public static final Logger ufm = LoggerFactory.getLogger("ufm");

	public Page<UFMNoticeBean> getListOfStudentsMarkedForUFM(UFMNoticeBean bean, int pageNo, int pageSize) throws Exception {
		return ufmMarkedStudentsListStrategy.getListOfStudentsMarked(bean,pageNo,pageSize);
	}
	
	public void performUploadUFMNoticeFiles(UFMNoticeBean inputBean, List<UFMNoticeBean> successList,
			List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			
			
			List<UFMNoticeBean> uploadedList = ufmShowCauseStrategy.readShowCauseList(inputBean);
			
			UFMIncidentExcelBean readIncidentExcel = readExcelAndFillDataInBean(inputBean.getIncidentUploadFileData());
			
			if(!(StringUtils.isBlank(readIncidentExcel.getErrorMessage()))) {
				throw new RuntimeException(readIncidentExcel.getErrorMessage());
			}
//			Set<UFMNoticeBean> UFMuploadSet=new HashSet<>(uploadedList);
//			
//			Set<UFMIncidentBean> IncidentUploadSet = new HashSet<>(readIncidentExcel.getSuccessList());
//			
//			List<String> ufmNotMatchedRecords = UFMuploadSet.stream().filter(k -> IncidentUploadSet.contains(k))
//					.map(k -> k.getSapid() + " - " + k.getSubject()).collect(Collectors.toList());
//			
//			
//			List<String> incidentNotMatchedRecords = IncidentUploadSet.stream()
//					.filter(k -> IncidentUploadSet.contains(k)).map(k -> k.getSapid() + " - " + k.getSubject())
//					.collect(Collectors.toList());
			
			
			Set<String> ufmTempSet =uploadedList
					.stream()
					.map(bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim())
					.collect(Collectors.toSet());
					

			Set<String> incidentTempSet =readIncidentExcel.getSuccessList()
					.stream()
					.map(bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim())
					.collect(Collectors.toSet());
			
			Set<String> ufmSet = ufmTempSet.stream().filter(k -> !incidentTempSet.contains(k)).collect(Collectors.toSet());
			Set<String> incidentSet = incidentTempSet.stream().filter(k -> !ufmTempSet.contains(k)).collect(Collectors.toSet());
					
					
					
//					
//			Map<String, UFMNoticeBean> uploadedUFMMap = uploadedList
//					.stream()
//					.collect(Collectors.toMap(
//					bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim(), Function.identity(), (a, b) -> a, LinkedHashMap::new));
//			
//			
//			Map<String, UFMIncidentBean> uploadIncidentMap = readIncidentExcel.getSuccessList()
//				    .stream()
//				    .collect(Collectors.toMap(bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim(), Function.identity(), (a, b) -> a, LinkedHashMap::new));
//			
//			List<String> uploadUFMList = getUncommonKey(uploadedUFMMap,uploadIncidentMap);
//			
//			List<String> uploadIncidentList = getUncommonKeyIncident(uploadedUFMMap,uploadIncidentMap);
//			
			Map<String, Set<String>> errorMap = new HashMap<>();
			
			System.out.println("this is the ufm set "+ufmSet.size() );
			if (ufmSet.size() > 0) {

//				List<String> notMatchedRecords = UFMuploadSet.stream().map(k -> k.getSapid() + " - " +k.getSubject() ).collect(Collectors.toList());
				errorMap.put("Records Missing In Incident Excel", ufmSet);
//				throw new RuntimeException("Records Missing In Incident Excel " + );
				
			}
			if(incidentSet.size()>0) {
//				List<String> incidentUploadNotMatchedRecords = IncidentUploadSet.stream().map(k -> k.getSapid() +" - "+ k.getSubject()).collect(Collectors.toList());
				errorMap.put("Records Missing in UFM Show Cause Excel", incidentSet);
//				throw new RuntimeException("Records Missing in UFM Show Cause Excel "+incidentUploadNotMatchedRecords);
			}
			if (errorMap.size()>0) {
				throw new RuntimeException(generateErrorMessage(errorMap));
			}

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmShowCauseStrategy.checkUploadedUFMBean(ufmNoticeBean);
				if (!StringUtils.isBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						int studentCount= ufmNoticeDAO.validateUFMStudentRecord(ufmNoticeBean.getSapid(), ufmNoticeBean.getSubject(), ufmNoticeBean.getCategory(), ufmNoticeBean.getYear(), ufmNoticeBean.getMonth());
						if(studentCount>0) {
							throw new Exception("Record Already Exists !");
						}
						ufmShowCauseStrategy.upsertShowCause(ufmNoticeBean);
						UFMNoticeBean bean = ufmDocumentGeneratorStrategy.getUpdatedUFMBean(ufmNoticeBean);
						successList.add(bean);
					} catch (Exception e) {
						ufm.info("Error adding UFM Record! for sapid " + ufmNoticeBean.getSapid() + "is:"
								+ e.getMessage());
						ufmNoticeBean.setError("Error adding UFM Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			try {
				int rowsUpdatedForIncidentDetails = getIdFromStudentDetailsAndInsertIntoIncident(readIncidentExcel.getSuccessList(),
						inputBean, successList,errorList);
				inputBean.setIncidentRowUpdated(rowsUpdatedForIncidentDetails);
			} catch (Exception e) {
//				e.printStackTrace();
				ufm.info("Error Updating Incident Excel Data   " + e);
				throw new RuntimeException("Error Updating Incident Excel Data");
			}

			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);

			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {

				try {

					List<UFMIncidentBean> incidentDetails = ufmShowCauseStrategy.getIncidentDetails(
							ufmNoticeBean.getSapid(), ufmNoticeBean.getSubjectsList(), inputBean.getYear(),
							inputBean.getMonth(), inputBean.getCategory());
					setSubjectsInIncidentBean(incidentDetails,ufmNoticeBean.getSubjectsList());
					ufmDocumentGeneratorStrategy.createUpdatedPDFDocument(ufmNoticeBean, incidentDetails);
					sendShowCauseNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :" + ufmNoticeBean.getSapid() + " is:" + e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			inputBean.setError(e.getMessage());
			ufm.info("Exception is:" + e.getMessage());
		}
	}

	private String generateErrorMessage(Map<String, Set<String>> errorMap) {
		StringBuffer buffer = new StringBuffer();
		errorMap.forEach((k, v) -> buffer.append(k + " : " + String.join(",", v) + "<br>"));
		return buffer.toString();
	}

	public void performUploadUFMActionFiles(UFMNoticeBean bean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			List<UFMNoticeBean> uploadedList = ufmActionStrategy.readShowCauseList(bean);

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmActionStrategy.checkUploadedUFMBean(ufmNoticeBean);
				if(!StringUtils.isBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmActionStrategy.updateUFMActionRecord(ufmNoticeBean);

						UFMNoticeBean bean2 = ufmDocumentGeneratorStrategy.getUpdatedUFMBean(ufmNoticeBean);
						successList.add(bean2);
					} catch (Exception e) {
						ufm.info("Error adding UFM Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding UFM Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			
			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					List<UFMIncidentBean> incidentDetails = ufmShowCauseStrategy.getIncidentDetails(
							ufmNoticeBean.getSapid(), ufmNoticeBean.getSubjectsList(), ufmNoticeBean.getYear(),
							ufmNoticeBean.getMonth(), ufmNoticeBean.getCategory());
					ufm.info("---------IncidentDetails : {}",incidentDetails);
					ufmDocumentGeneratorStrategy.createUpdatedPDFDocument(ufmNoticeBean,incidentDetails);
					sendDecisionNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	

	public List<UFMNoticeBean> groupSubjectsByCommonStatusAndSapids(List<UFMNoticeBean> subjects) {
		List<UFMNoticeBean> groupedSubjects = new ArrayList<UFMNoticeBean>();
		for (UFMNoticeBean subjectNoticeBean : subjects) {
			boolean found = false;
			for (UFMNoticeBean groupedNoticeBean : groupedSubjects) {
				if(
					subjectNoticeBean.getYear().equals(groupedNoticeBean.getYear()) &&
					subjectNoticeBean.getMonth().equals(groupedNoticeBean.getMonth()) && 
					subjectNoticeBean.getStage().equals(groupedNoticeBean.getStage()) && 
					subjectNoticeBean.getUfmMarkReason().equals(groupedNoticeBean.getUfmMarkReason()) && 
					subjectNoticeBean.getSapid().equals(groupedNoticeBean.getSapid())
				) {
					groupedNoticeBean.getSubjectsList().add(subjectNoticeBean);
					found = true;
					break;
				}
			}
			if(!found) {
				UFMNoticeBean bean = new UFMNoticeBean();
				bean.setSapid(subjectNoticeBean.getSapid());
				bean.setYear(subjectNoticeBean.getYear());
				bean.setMonth(subjectNoticeBean.getMonth());
				bean.setStage(subjectNoticeBean.getStage());
				bean.setActive(subjectNoticeBean.getActive());
				bean.setUfmMarkReason(subjectNoticeBean.getUfmMarkReason());
				bean.setShowCauseGenerationDate(subjectNoticeBean.getShowCauseGenerationDate());
				bean.setShowCauseDeadline(subjectNoticeBean.getShowCauseDeadline());
				bean.setShowCauseResponse(subjectNoticeBean.getShowCauseResponse());
				bean.setShowCauseSubmissionDate(subjectNoticeBean.getShowCauseSubmissionDate());
				bean.setShowCauseNoticeURL(subjectNoticeBean.getShowCauseNoticeURL());
				bean.setDecisionNoticeURL(subjectNoticeBean.getDecisionNoticeURL());
				bean.setFirstName(subjectNoticeBean.getFirstName());
				bean.setLastName(subjectNoticeBean.getLastName());
				bean.setLcName(subjectNoticeBean.getLcName());
				bean.setProgram(subjectNoticeBean.getProgram());
				bean.setProgramStructure(subjectNoticeBean.getProgramStructure());
				bean.setConsumerType(subjectNoticeBean.getConsumerType());
				bean.setConsumerProgramStructureId(subjectNoticeBean.getConsumerProgramStructureId());
				bean.setEmailId(subjectNoticeBean.getEmailId());
				bean.setError(subjectNoticeBean.getError());
				bean.setCategory(subjectNoticeBean.getCategory());
				List<UFMNoticeBean> subjectsList = new ArrayList<UFMNoticeBean>();
				subjectsList.add(subjectNoticeBean);
				bean.setSubjectsList(subjectsList);
				bean.setExamDate(subjectNoticeBean.getExamDate());
				
				groupedSubjects.add(bean);
			}
		}
		return groupedSubjects;
	}
	
	public List<UFMNoticeBean> getUFMListForStudent(String sapid) {
		try {
			return ufmStudentStrategy.getListOfShowCauseSubjects(sapid);
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
			return null;
		}
	}
	
	public List<UFMNoticeBean> getUFMListForStudentYearMonth(String sapid, String year, String month) {
		try {
			return ufmStudentStrategy.getListOfShowCauseSubjectsForStudentYearMonth(sapid, year, month);
		} catch (Exception e) {
			
			return null;
		}
	}

	public String addStudentResponse(UFMNoticeBean bean) {
		try {
			ufmStudentStrategy.setStudentResponse(bean);
			return null;
		} catch (Exception e) {
			ufm.info("Exception for sapid "+bean.getSapid()+" while adding student response is:"+e.getMessage());
			return e.getMessage();
		}
	}
	
	public UFMResponseBean getUfmResponseBean(String sapid) {
		UFMResponseBean response = new UFMResponseBean();
		try{
			List<UFMNoticeBean> subjects = getUFMListForStudent(sapid);
//			List<UFMNoticeBean> noticeBeans = groupSubjectsByCommonStatusAndSapids(subjects);
			
			for (UFMNoticeBean notice : subjects) {
				ufmStudentStrategy.setUFMStatus(notice);
			}
			
			response.setMarkedForCurrentCycle(checkIfMarkedForCurrentCycle(sapid));
			response.setStatus("success");
			response.setNotices(subjects);
		} catch(Exception e) {
			ufm.info("Exception for sapid "+sapid+" is:"+e.getMessage());
			response.setStatus("error");
			response.setErrorMessage(e.getMessage());
			
		}
		return response;
	}
	
	public boolean checkIfMarkedForCurrentCycle(String sapid) throws Exception {
		return ufmStudentStrategy.checkIfMarkedForCurrentCycle(sapid);
	}
	
	public void sendShowCauseNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendUFMShowCauseMailer(bean);
	}
	
	public void sendDecisionNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendUFMDecisionMailer(bean);
	}
	
	public List<StudentMarksBean> getPendingRIARecords(String year, String month, String status) throws Exception{
		List<StudentMarksBean> list = new ArrayList<>(); 
		switch(status){
		case "RIA":
			list = ufmNoticeDAO.getPendingRIARecords(year, month);
			break;
		case "NV":
			list = ufmNoticeDAO.getPendingNVRecords(year, month);
			break;
		case "SCORED":
			list = ufmNoticeDAO.getPendingScoredRecords(year, month);
			break;
		}
		list = validateMultipleCategory(list);
		 return list;
	}
	
	public List<StudentMarksBean> validateMultipleCategory(List<StudentMarksBean> list)
	{
		List<StudentMarksBean> updatedList = new ArrayList<>();
		HashMap<String,StudentMarksBean> ufmKeys = new HashMap<>();
		list.stream().forEach(bean -> {
			if(!ufmKeys.containsKey(bean.getSapid()+"-"+bean.getSubject()+"-"+bean.getYear()+"-"+bean.getMonth()))
			{
				updatedList.add(bean);
				ufmKeys.put(bean.getSapid()+"-"+bean.getSubject()+"-"+bean.getYear()+"-"+bean.getMonth(), bean);
			}
		});
		return updatedList;
	}
	
	public HashMap<String, Integer> updateRIANVStatus(List<StudentMarksBean> list, String status, String userId) throws Exception{
		HashMap<String, Integer> result = new HashMap<>();
		int successCount = 0;
		int errorCount = 0;
		
		switch(status){
		case "RIA":
			int[] resultRIA = ufmNoticeDAO.applyRIA(list, userId);
			 List<Integer> resultRIAList = Arrays.stream(resultRIA).boxed().collect(Collectors.toList());
			 successCount = Collections.frequency(resultRIAList, 1);
			 errorCount = Collections.frequency(resultRIAList, 0);
			 result.put("successCount", successCount);
			 result.put("errorCount", errorCount);
			 
			 break;
		case "NV":
			 int[] resultNV = ufmNoticeDAO.applyNV(list, userId);
			 List<Integer> resultNVList = Arrays.stream(resultNV).boxed().collect(Collectors.toList());
			 successCount = Collections.frequency(resultNVList, 1);
			 errorCount = Collections.frequency(resultNVList, 0);
			 result.put("successCount", successCount);
			 result.put("errorCount", errorCount);
			 
			break;
		case "SCORED":
			int[] resultSCORED = ufmNoticeDAO.applyScored(list, userId);
			 List<Integer> resultSCOREDList = Arrays.stream(resultSCORED).boxed().collect(Collectors.toList());
			 successCount = Collections.frequency(resultSCOREDList, 1);
			 errorCount = Collections.frequency(resultSCOREDList, 0);
			 result.put("successCount", successCount);
			 result.put("errorCount", errorCount);
			break;
		}
		
		return result;
	}
	
	
	
	/**
	 * shivam.pandey.EXT COC - START
	 */
	//To Upload Show Cause File Into Database(exam.ufm_students)
	public void performUploadCOCNoticeFiles(UFMNoticeBean inputBean, List<UFMNoticeBean> successList,
			List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		
		try {
			
			ExcelHelper readFile = new ExcelHelper();
			
			List<UFMNoticeBean> uploadedList = readFile.readShowCauseList(inputBean);
			
			UFMIncidentExcelBean readIncidentExcel = readExcelAndFillDataInBean(
					inputBean.getIncidentUploadFileData());
			ufm.info(" ---------- Records for Incident Upload Size : {} ",readIncidentExcel.getSuccessList().size());

			if(!(StringUtils.isBlank(readIncidentExcel.getErrorMessage()))) {
				throw new RuntimeException(readIncidentExcel.getErrorMessage());
			}
	

			Set<String> ufmTempSet =uploadedList
					.stream()
					.map(bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim())
					.collect(Collectors.toSet());
					

			Set<String> incidentTempSet =readIncidentExcel.getSuccessList()
					.stream()
					.map(bean -> bean.getSapid().trim() + "-" + bean.getSubject().trim())
					.collect(Collectors.toSet());
			
			Set<String> ufmSet = ufmTempSet.stream().filter(k -> !incidentTempSet.contains(k)).collect(Collectors.toSet());
			Set<String> incidentSet = incidentTempSet.stream().filter(k -> !ufmTempSet.contains(k)).collect(Collectors.toSet());
					
//				Map<String, UFMNoticeBean> uploadedUFMMap = uploadedList.stream().collect(Collectors.toMap(
//						bean -> bean.getSapid() + "-" + bean.getSubject(), Function.identity(), (a, b) -> a, LinkedHashMap::new));
//				
//				
//				Map<String, UFMIncidentBean> uploadIncidentMap = readIncidentExcel.getSuccessList()
//					    .stream()
//					    .collect(Collectors.toMap(bean -> bean.getSapid() + "-" + bean.getSubject(), Function.identity(), (a, b) -> a, LinkedHashMap::new));
//
//				
//				
//				List<String> uploadUFMList = getUncommonKey(uploadedUFMMap,uploadIncidentMap);
				
				ufm.info(" ---------- Records Not In UFM : {} ",ufmSet.size());
				
//				List<String> uploadIncidentList = getUncommonKeyIncident(uploadedUFMMap,uploadIncidentMap);
				
				ufm.info(" ---------- Records Not In Incident : {} ",incidentSet.size());
				Map<String, Set<String>> errorMap = new HashMap<>();
				
				System.out.println("this is the ufm set "+ufmSet.size() );
				if (ufmSet.size() > 0) {

//					List<String> notMatchedRecords = UFMuploadSet.stream().map(k -> k.getSapid() + " - " +k.getSubject() ).collect(Collectors.toList());
					errorMap.put("Records Missing In Incident Excel", ufmSet);
//					throw new RuntimeException("Records Missing In Incident Excel " + );
					
				}
				if(incidentSet.size()>0) {
//					List<String> incidentUploadNotMatchedRecords = IncidentUploadSet.stream().map(k -> k.getSapid() +" - "+ k.getSubject()).collect(Collectors.toList());
					errorMap.put("Records Missing in UFM Show Cause Excel", incidentSet);
//					throw new RuntimeException("Records Missing in UFM Show Cause Excel "+incidentUploadNotMatchedRecords);
				}
				if (errorMap.size()>0) {
					throw new RuntimeException(generateErrorMessage(errorMap));
				}

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmShowCauseStrategy.checkUploadedCOCShowCauseFile(ufmNoticeBean);
				if (StringUtils.isNotBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						int studentCount = ufmNoticeDAO.validateUFMStudentRecord(ufmNoticeBean.getSapid(),
								ufmNoticeBean.getSubject(), ufmNoticeBean.getCategory(), ufmNoticeBean.getYear(),
								ufmNoticeBean.getMonth());
						if (studentCount > 0) {
							throw new Exception("Record Already Exists !");
						}
						ufmShowCauseStrategyI.upsertCOCShowCause(ufmNoticeBean);
						UFMNoticeBean bean = ufmDocumentGeneratorStrategyI.getUpdatedCOCUFMBean(ufmNoticeBean);
						successList.add(bean);
					} catch (Exception e) {
//						e.printStackTrace();
						ufm.info("Error adding UFM Record! for sapid " + ufmNoticeBean.getSapid() + "is:"
								+ e.getMessage());
						ufmNoticeBean.setError("Error adding UFM Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			try {
				int rowsUpdatedForIncidentDetails = getIdFromStudentDetailsAndInsertIntoIncident(readIncidentExcel.getSuccessList(),
						inputBean, successList,errorList);
				
				inputBean.setIncidentRowUpdated(rowsUpdatedForIncidentDetails);
				
			} catch (Exception e) {
//				e.printStackTrace();
				ufm.info("Error Updating Incident Excel Data" + e);
				
				throw new RuntimeException("Error Updating Incident Excel Data");
				
			}
			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					List<UFMIncidentBean> incidentDetails = ufmShowCauseStrategy.getIncidentDetails(
							ufmNoticeBean.getSapid(), ufmNoticeBean.getSubjectsList(), inputBean.getYear(),
							inputBean.getMonth(), inputBean.getCategory());
					setSubjectsInIncidentBean(incidentDetails,ufmNoticeBean.getSubjectsList());
					ufmDocumentGeneratorStrategyI.createUpdatedCOCPDFDocument(ufmNoticeBean, incidentDetails);
					sendCOCShowCauseNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :" + ufmNoticeBean.getSapid() + " is:" + e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			inputBean.setError(e.getMessage());
			ufm.info("Exception is:" + e.getMessage());
		}
	}
	
	private void setSubjectsInIncidentBean(List<UFMIncidentBean> incidentDetails, List<UFMNoticeBean> subjectsList) {
		subjectsList.forEach(bean -> {
		    incidentDetails.stream()
		                   .filter(incidentBean -> bean.getId() == incidentBean.getId())
		                   .forEach(incidentBean -> {
		                       incidentBean.setSubject(bean.getSubject());
		                       incidentBean.setExamDate(bean.getExamDate());
		                   });
		});
	}

	private List<String> getUncommonKey(Map<String, UFMNoticeBean> mapOne,
			Map<String, UFMIncidentBean> mapTwo) {
		return mapOne.entrySet().stream()
//				.peek(System.out::println)
				.filter(k -> !mapTwo.containsKey(k.getKey()))
//				.peek(System.out::println)
				.map(k -> k.getKey())
				.collect(Collectors.toList());
	}
	
	
	
	private List<String> getUncommonKeyIncident(Map<String, UFMNoticeBean> mapTwo ,Map<String, UFMIncidentBean> mapOne) {
		return mapOne.entrySet()
				.stream()
//				.peek(System.out::println)
				.filter(k -> !mapTwo.containsKey(k.getKey()))
//				.peek(System.out::println)
				.map(k -> k.getKey())
				.collect(Collectors.toList());

	}

	//To Upload Action File for Update Stage Of "exam.ufm_students" Table From "Show Cause" to "Penalty" or "Warning"
	public void performUploadCOCActionFiles(UFMNoticeBean bean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			ExcelHelper readFile = new ExcelHelper();
			List<UFMNoticeBean> uploadedList = readFile.readActionFileList(bean);

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmActionStrategyI.checkUploadedCOCShowCauseFile(ufmNoticeBean);
				if(!StringUtils.isBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmActionStrategyI.updateCOCActionRecord(ufmNoticeBean);
						UFMNoticeBean bean2 = ufmDocumentGeneratorStrategyI.getUpdatedCOCUFMBean(ufmNoticeBean);
						successList.add(bean2);
					} catch (Exception e) {
						ufm.info("Error adding COC Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding COC Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					List<UFMIncidentBean> incidentDetails = ufmShowCauseStrategy.getIncidentDetails(
							ufmNoticeBean.getSapid(), ufmNoticeBean.getSubjectsList(), bean.getYear(),
							bean.getMonth(), bean.getCategory());
					ufmDocumentGeneratorStrategyI.createUpdatedCOCPDFDocument(ufmNoticeBean,incidentDetails);
					sendCOCDecisionNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	
	//To Generate COC Show Cause File Upload MAIL
	public void sendCOCDecisionNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendCOCDecisionMailer(bean);
	}
	
	//To Generate COC Action File Upload MAIL
	public void sendCOCShowCauseNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendCOCShowCauseMailer(bean);
	}
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	//To Upload Show Cause File Into Database(exam.ufm_students)
	public void performUploadDisconnectAboveNoticeFiles(UFMNoticeBean inputBean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			ExcelHelper readFile = new ExcelHelper();
			List<UFMNoticeBean> uploadedList = readFile.readShowCauseList(inputBean);
			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmShowCauseStrategyI.checkUploadedDisconnectAboveShowCauseFile(ufmNoticeBean);
				if(StringUtils.isNotBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmShowCauseStrategyI.upsertDisconnectAboveShowCause(ufmNoticeBean);
						UFMNoticeBean bean = ufmDocumentGeneratorStrategyI.getUpdatedDisconnectAboveUFMBean(ufmNoticeBean);
						successList.add(bean);
					} catch (Exception e) {
						ufm.info("Error adding UFM Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding UFM Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}

			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					ufmDocumentGeneratorStrategyI.createUpdatedDisconnectAbovePDFDocument(ufmNoticeBean);
					sendDisconnectAboveShowCauseNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	
	//To Upload Action File for Update Stage Of "exam.ufm_students" Table From "Show Cause" to "Penalty" or "Warning"
	public void performUploadDisconnectAboveActionFiles(UFMNoticeBean bean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			ExcelHelper readFile = new ExcelHelper();
			List<UFMNoticeBean> uploadedList = readFile.readActionFileList(bean);

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmActionStrategyI.checkUploadedDisconnectShowCauseFile(ufmNoticeBean);
				if(!StringUtils.isBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmActionStrategyI.updateDisconnectActionRecord(ufmNoticeBean);
						UFMNoticeBean bean2 = ufmDocumentGeneratorStrategyI.getUpdatedDisconnectAboveUFMBean(ufmNoticeBean);
						successList.add(bean2);
					} catch (Exception e) {
						ufm.info("Error adding COC Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding COC Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			
			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					ufmDocumentGeneratorStrategyI.createUpdatedDisconnectAbovePDFDocument(ufmNoticeBean);
					sendDisconnectAboveDecisionNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (SQLIntegrityConstraintViolationException e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	
	//To Generate Disconnect Show Cause File Upload MAIL
	public void sendDisconnectAboveDecisionNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendDisconnectAboveDecisionMailer(bean);
	}
	
	//To Generate Disconnect Action File Upload MAIL
	public void sendDisconnectAboveShowCauseNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendDisconnectAboveShowCauseMailer(bean);
	}
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	//To Upload Show Cause File Into Database(exam.ufm_students)
	public void performUploadDisconnectBelowNoticeFiles(UFMNoticeBean inputBean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			ExcelHelper readFile = new ExcelHelper();
			List<UFMNoticeBean> uploadedList = readFile.readShowCauseList(inputBean);
			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmShowCauseStrategyI.checkUploadedDisconnectBelowShowCauseFile(ufmNoticeBean);
				if(StringUtils.isNotBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmShowCauseStrategyI.upsertDisconnectBelowShowCause(ufmNoticeBean);
						UFMNoticeBean bean = ufmDocumentGeneratorStrategyI.getUpdatedDisconnectBelowUFMBean(ufmNoticeBean);
						successList.add(bean);
					} catch (Exception e) {
						ufm.info("Error adding UFM Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding UFM Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}

			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					//ufmDocumentGeneratorStrategyI.createUpdatedDisconnectBelowPDFDocument(ufmNoticeBean); - temporary comment
					sendDisconnectBelowShowCauseNoticeMail(ufmNoticeBean);
					//successListDocuments.add(ufmNoticeBean); - temporary comment
				} catch (Exception e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	
	//To Upload Action File for Update Stage Of "exam.ufm_students" Table From "Show Cause" to "Penalty" or "Warning"
	public void performUploadDisconnectBelowActionFiles(UFMNoticeBean bean, List<UFMNoticeBean> successList, List<UFMNoticeBean> successListDocuments, List<UFMNoticeBean> errorList) {
		try {
			ExcelHelper readFile = new ExcelHelper();
			List<UFMNoticeBean> uploadedList = readFile.readActionFileList(bean);

			for (UFMNoticeBean ufmNoticeBean : uploadedList) {
				ufmActionStrategyI.checkUploadedDisconnectShowCauseFile(ufmNoticeBean);
				if(!StringUtils.isBlank(ufmNoticeBean.getError())) {
					errorList.add(ufmNoticeBean);
				} else {
					try {
						ufmActionStrategyI.updateDisconnectActionRecord(ufmNoticeBean);
						UFMNoticeBean bean2 = ufmDocumentGeneratorStrategyI.getUpdatedDisconnectBelowUFMBean(ufmNoticeBean);
						successList.add(bean2);
					} catch (Exception e) {
						ufm.info("Error adding COC Record! for sapid "+ufmNoticeBean.getSapid()+"is:"+e.getMessage());
						ufmNoticeBean.setError("Error adding COC Record! Error : " + e.getMessage());
						errorList.add(ufmNoticeBean);
					}
				}
			}
			
			List<UFMNoticeBean> documentsToGenerate = groupSubjectsByCommonStatusAndSapids(successList);
			for (UFMNoticeBean ufmNoticeBean : documentsToGenerate) {
				try {
					ufmDocumentGeneratorStrategyI.createUpdatedDisconnectBelowPDFDocument(ufmNoticeBean);
					sendDisconnectBelowDecisionNoticeMail(ufmNoticeBean);
					successListDocuments.add(ufmNoticeBean);
				} catch (Exception e) {
					ufm.info("Exception for sapid :"+ufmNoticeBean.getSapid()+" is:"+e.getMessage());
					ufmNoticeBean.setError("Duplicate Record Found!");
					errorList.add(ufmNoticeBean);
				}
			}
		} catch (Exception e) {
			ufm.info("Exception is:"+e.getMessage());
		}
	}
	
	//To Generate Disconnect Show Cause File Upload MAIL
	public void sendDisconnectBelowDecisionNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendDisconnectBelowDecisionMailer(bean);
	}

	public UFMIncidentExcelBean readExcelAndFillDataInBean(CommonsMultipartFile fileData) throws Exception {
		if (!(fileData.getOriginalFilename().toLowerCase().endsWith("xls")||fileData.getOriginalFilename().toLowerCase().endsWith("xlsx"))){
			throw new IllegalArgumentException("Received file does not have a standard excel extension.");
		}
			
		UFMIncidentExcelBean incidentList = new UFMIncidentExcelBean();
		ArrayList<UFMIncidentBean> incidentSuccessList = new ArrayList<UFMIncidentBean>();
		ArrayList<UFMIncidentBean> incidentErrorList = new ArrayList<UFMIncidentBean>();
		XSSFWorkbook workBook;
		final int SAPID = 0;
		final int SUBJECT = 1;
		final int INCIDENT = 2;
		final int TIMESTAMP = 3;
		final int VIDEONUMBER = 4;
		 
		String errorBlankColumnMessage = new String();
		workBook = new XSSFWorkbook(fileData.getInputStream());
		int emptyRowNum = 0;
		int emptyColumnNum = 0;
		XSSFSheet workSheet = workBook.getSheetAt(0);
		int rowsForIteration = workSheet.getPhysicalNumberOfRows();
		XSSFRow headerRow  = workSheet.getRow(0);
		if(headerRow == null) {
			throw new Exception("Blank Incident Excel Found ");
		}
		boolean isExcelValid = validateRowName(headerRow,INCIDENT,VIDEONUMBER);
		if(!isExcelValid) {
			throw new Exception("Incident Excel Not In Valid Format");
		}
		
		
		for (int i = 1; i < rowsForIteration; i++) {
			String errorColumnMessage = new String();

			UFMIncidentBean ufmincident = new UFMIncidentBean();
			Row sheetRow = workSheet.getRow(i);
			List<String> allvalues = new ArrayList<String>();
			
			XSSFRow row = workSheet.getRow(i);
			if (row == null) {
				rowsForIteration++;
				continue;
			}
			;

			ufm.info("--------  Total Rows : {}  ---------", workSheet.getPhysicalNumberOfRows());

			row.getCell(SAPID, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
			String sapid = row.getCell(SAPID).getStringCellValue();
			Cell sapidCellNumber = sheetRow.getCell(SAPID);
			if (StringUtils.isBlank(sapid)) {
				emptyRowNum = sapidCellNumber.getRowIndex() + 1;
				emptyColumnNum = sapidCellNumber.getColumnIndex() + 1;
				errorColumnMessage = "Error In Sapid Column Row In Incident Excel : Row  " + emptyRowNum + " Column : "
						+ emptyColumnNum + "   <br>";

			}
			ufm.info("----------------  Sapid : {} ----------------", sapid);
			allvalues.add(sapid);

			row.getCell(SUBJECT, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
			String subject = row.getCell(SUBJECT).getStringCellValue();
			Cell subjectCellNumber = sheetRow.getCell(SUBJECT);
			if (StringUtils.isBlank(subject)) {
				emptyRowNum = subjectCellNumber.getRowIndex() + 1;
				emptyColumnNum = subjectCellNumber.getColumnIndex() + 1;
				errorColumnMessage = errorColumnMessage + "Error In Subject Column Row In Incident Excel : Row "
						+ emptyRowNum + " Column : " + emptyColumnNum + "   <br>";

			}
			ufm.info("----------------  Subject : {} ----------------", subject);

			allvalues.add(subject);

			row.getCell(INCIDENT, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
			String incident = row.getCell(INCIDENT).getStringCellValue();
			Cell incidentCellNumber = sheetRow.getCell(INCIDENT);
			if (StringUtils.isBlank(incident)) {
				emptyRowNum = incidentCellNumber.getRowIndex() + 1;
				emptyColumnNum = incidentCellNumber.getColumnIndex() + 1;
				errorColumnMessage = errorColumnMessage + "Error In Incident Column Row In Incident Excel : Row "
						+ emptyRowNum + " Column : " + emptyColumnNum + "   <br>";

			}

			ufm.info("---------------- Incident : {} ----------------", incident);
			allvalues.add(incident);

			String timeStampDateStr = new String();
			Date timeStampDate = new Date();

			Cell getTimeStampCell = row.getCell(TIMESTAMP, row.CREATE_NULL_AS_BLANK);
			if (getTimeStampCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(getTimeStampCell)) {
					timeStampDate = getTimeStampCell.getDateCellValue();
				} else {
					timeStampDate = HSSFDateUtil.getJavaDate(getTimeStampCell.getNumericCellValue());
				}
				SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
				timeStampDateStr = sdfTime.format(timeStampDate);

			} else {
				getTimeStampCell.setCellType(Cell.CELL_TYPE_STRING);
				timeStampDateStr = getTimeStampCell.getStringCellValue();
			}
			Cell timeCellNumber = sheetRow.getCell(TIMESTAMP);
			if (StringUtils.isBlank(timeStampDateStr)) {
				emptyRowNum = timeCellNumber.getRowIndex() + 1;
				emptyColumnNum = timeCellNumber.getColumnIndex() + 1;
				errorColumnMessage = errorColumnMessage + "Error In TimeStamp Column Row In Incident Excel : Row "
						+ emptyRowNum + " Column : " + emptyColumnNum + "   <br>";
			}

			ufm.info("---------------- Time Stamp : {} ----------------", timeStampDateStr);
			allvalues.add(timeStampDateStr);

			row.getCell(VIDEONUMBER, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
			String videoNumber = row.getCell(VIDEONUMBER).getStringCellValue();
			Cell videoCellNumber = sheetRow.getCell(VIDEONUMBER);
			if (StringUtils.isBlank(videoNumber)) {
				emptyRowNum = videoCellNumber.getRowIndex() + 1;
				emptyColumnNum = videoCellNumber.getColumnIndex() + 1;
				errorColumnMessage = errorColumnMessage + "Error In VideoNumber Column Row In Incident Excel : Row "
						+ emptyRowNum + " Column : " + emptyColumnNum + "   <br>";

			}

			ufm.info("----------------  videoNumber : {} ----------------", videoNumber);
			allvalues.add(videoNumber);

			ufmincident.setSapid(sapid);

			ufmincident.setSubject(subject);

			ufmincident.setIncident(incident);

			ufmincident.setTime_Stamp(timeStampDateStr);

			ufmincident.setVideo_Number(videoNumber);
			if (!StringUtils.isBlank(errorColumnMessage)) {
				incidentErrorList.add(ufmincident);
				errorBlankColumnMessage += errorColumnMessage;
			} else {
				incidentSuccessList.add(ufmincident);
			}
		}
		incidentList.setErrorList(incidentErrorList);
		incidentList.setSuccessList(incidentSuccessList);
		incidentList.setErrorMessage(errorBlankColumnMessage);
		return incidentList;
	}
	
	public int getIdFromStudentDetailsAndInsertIntoIncident(List<UFMIncidentBean> incidentDetails,
			UFMNoticeBean attributeBean, List<UFMNoticeBean> successList, List<UFMNoticeBean> errorList) {
		Map<String,Integer> keyIdMap = generateKeyIdMap(successList);
		System.out.println("keyIdMap : "+ keyIdMap.size());
		System.out.println("successList.size() : "+successList.size());
		
		ufm.info(" Incident Details Size {}", incidentDetails.size());
		
		
		incidentDetails.stream()
//	    .peek(incidentbean -> noticeBean.setSapid(incidentbean.getSapid()))
	    .forEach(incidentbean -> 
	    
	    {
			try {
				incidentbean.setUfm_student_id(keyIdMap.get(
				    incidentbean.getSapid() + incidentbean.getSubject() +
				    attributeBean.getYear() + attributeBean.getMonth() + attributeBean.getCategory()));
			} catch (Exception e1) {
				UFMNoticeBean noticeBean = new UFMNoticeBean();
				noticeBean.setSapid(incidentbean.getSapid());
				noticeBean.setSubject(incidentbean.getSubject());
				
				
			}
		});

		List<Integer> sumOfupdatedRow = new ArrayList<Integer>();
		for (UFMIncidentBean incidentBean : incidentDetails) {
			try {

				int Insertion = ufmNoticeDAO.insertIntoIncidentDetails(incidentBean);
				sumOfupdatedRow.add(Insertion);

			} catch (Exception e) {
//				e.printStackTrace();
//				errorList.add(noticeBean);
//				ufm.info(" Error in Inserting the row {}", noticeBean);
			}
		}
		ufm.info("Number Of rows Updated {}", sumOfupdatedRow.size());
		return sumOfupdatedRow.size();
	}
	
	//To Generate Disconnect Action File Upload MAIL
	public void sendDisconnectBelowShowCauseNoticeMail(UFMNoticeBean bean) throws Exception {
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendDisconnectBelowShowCauseMailer(bean);
	}
	
	public boolean checkForNull(List<String> stringList) {
		ufm.info("String List Size : {}", stringList.size());
		for (String singleString: stringList) {
			if(StringUtils.isBlank(singleString)) {
				return true;
			}
		}
		return false;
	}
	public Set<String> generateKeyOfIncidentDetails(List<UFMIncidentBean> studentIncidentDetails,UFMNoticeBean attributeBean ){
		Set<String> key= new HashSet<String>();
		for(UFMIncidentBean bean : studentIncidentDetails) {
			String singleKey =(new StringBuilder().append( bean.getSapid()).append(bean.getSubject()).append(attributeBean.getYear()).append(attributeBean.getMonth()).append(attributeBean.getCategory())).toString();//sapid,subject,year,month,category
			key.add(singleKey);
		}
		return key;
	}

	private Map<String,Integer> generateKeyIdMap(List<UFMNoticeBean>successList ){    
		return successList.stream()
		        .collect(Collectors.toMap(
		                bean -> new StringBuilder()
		                        .append(bean.getSapid())
		                        .append(bean.getSubject())
		                        .append(bean.getYear())
		                        .append(bean.getMonth())
		                        .append(bean.getCategory())
		                        .toString(),
		                UFMNoticeBean::getId,
		                (id1, id2) -> {
//		                	System.err.println(" repeated ids : " + id1);
		                	return id1;
		                },
		                LinkedHashMap::new));
		
	}
	
	
	public List<UFMNoticeBean> getIncidentDetailsForReport(UFMNoticeBean inputBean){
		List<UFMNoticeBean> getUFMStudentDetails = ufmNoticeDAO.getUFMStudentDetailsForReport(inputBean.getYear(),inputBean.getMonth(),inputBean.getCategory());
		List<Integer> listOfId = getUFMStudentDetails.stream().map(UFMNoticeBean :: getId).collect(Collectors.toList());
		if(listOfId ==null || listOfId.isEmpty()) {
			throw new RuntimeException("No Record Found");
		}

		ArrayList<UFMIncidentBean> getUFMIncidentDetails = ufmNoticeDAO.getincidentDetailsForReport(listOfId);
		
		Map<Integer,ArrayList<UFMIncidentBean>> IncidentMap = getMapFromUFMIncidentList(getUFMIncidentDetails);
		for(UFMNoticeBean studBean :getUFMStudentDetails) {
			studBean.setIncidentBean(IncidentMap.get(studBean.getId()));
}
		
		return getUFMStudentDetails;
	}

	private Map<Integer,ArrayList<UFMIncidentBean> > getMapFromUFMIncidentList(ArrayList<UFMIncidentBean> list){
		Map<Integer,ArrayList<UFMIncidentBean>> IncidentMap = new HashMap<Integer, ArrayList<UFMIncidentBean>>();

		for(UFMIncidentBean bean : list) {
			if(IncidentMap.containsKey(bean.getId())) {
				ArrayList<UFMIncidentBean> listOfIncident =
				IncidentMap.get(bean.getId());
				listOfIncident.add(bean);
				IncidentMap.put(bean.getId(),listOfIncident );
			}else {
				ArrayList<UFMIncidentBean> listOfIncident = new ArrayList<UFMIncidentBean>();
				listOfIncident.add(bean);
				IncidentMap.put(bean.getId(),listOfIncident );
			}
			
		}
		return IncidentMap;
	}
	public boolean validateRowName(XSSFRow row, int INCIDENT, int VIDEONUMBER){
		row.getCell(INCIDENT, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		String Incident= row.getCell(INCIDENT).getStringCellValue();
		row.getCell(VIDEONUMBER, row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		String video= row.getCell(VIDEONUMBER).getStringCellValue();
		if(Incident.equalsIgnoreCase("Inicident")) {
			return true;
			}
		if(video.equalsIgnoreCase("Video Number")) {
			return true;
		}
		return false;
	}

	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
}