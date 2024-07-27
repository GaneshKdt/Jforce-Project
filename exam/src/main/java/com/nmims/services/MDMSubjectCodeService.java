/**
 * 
 */
package com.nmims.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.MDMSubjectCodeBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.controllers.MDMSubjectCodeController;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.MDMSubjectCodeDAO;
import com.nmims.helpers.MDMExcelHelper;

/**
 * @author vil_m
 *
 */

@Service("mdmSubjectCodeService")
public class MDMSubjectCodeService {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	DashboardDAO dashboardDao;

	@Autowired
	AssignmentsDAO asignmentsDAO;

	@Autowired
	MDMSubjectCodeDAO mdmSubjectCodeDAO;
	
	@Autowired
	MDMExcelHelper mdmExcelHelper;
	
	public static final String KEY_ERROR = "error";
	public static final String KEY_Y = "Y";
	public static final String KEY_N = "N";
	public static final String KEY_NA = "NA";
	public static final String V_YES = "Yes";
	public static final String V_NO = "No";
	public static final String V_NA = "Not Applicable";
	public static final String V_BEST = "Best";
	public static final String V_LATEST = "Latest";
	private static final String KEY_SUCCESS = "success";
	
	public static final Logger logger = LoggerFactory.getLogger(MDMSubjectCodeService.class);
	
	public static String toString(Integer arg) {
		return String.valueOf(arg);
	}
	
	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
	
	public static boolean areEqualString(String str1, String str2) {
		return str1.equalsIgnoreCase(str2);
	}
	
	public static String changeToYN(String arg) {
		String r = null;
		if(areEqualString(arg, V_YES)) {
			r = KEY_Y;
		} else if(areEqualString(arg, V_NO)) {
			r = KEY_N;
		}
		return r;
	}
	
	public static String changeToNumberString(String arg) {
		String r = "";
		if(areEqualString(arg, V_YES)) {
			r = "1";
		} else if(areEqualString(arg, V_NO)) {
			r = "0";
		}
		return r;
	}
	
	public static String changeForStudentType(String arg) {
		String r = null;
		if(areEqualString(arg, V_YES)) {
			r = KEY_Y;
		} else if(areEqualString(arg, V_NO)) {
			r = KEY_N;
		}
		return r;
	}
	
	public static String changeForModel(String arg) {
		String r = null;
		if(areEqualString(arg, V_NA)) {
			r = KEY_NA;
		} else if(areEqualString(arg, KEY_NA)) {
			r = KEY_NA;
		} else if(areEqualString(arg, V_BEST)) {
			r = V_BEST;
		} else if(areEqualString(arg, V_LATEST)) {
			r = V_LATEST;
		}
		return r;
	}
	
	public String validateFileExtension(String fileName) {
		String fileExtension = "";
		logger.info("MDMSubjectCodeService : validateFileExtension : " + fileName);
		fileExtension = mdmExcelHelper.validateFileExtension(fileName);
		return fileExtension;
	}
	
	public boolean processFile(MDMSubjectCodeBean bean, String userId) {
		Boolean isProcessed = Boolean.FALSE;
		MDMSubjectCodeBean obj = null;
		String temp = null;
		List<MDMSubjectCodeBean> list = null;
		try {
			list = mdmExcelHelper.processMDMSubjectCodeFile(bean);
			for(int w = 0; w < list.size(); w++) {
				obj = list.get(w);
				if(isBlank(obj.getSubjectcode())) {
					addError(bean, "Upload file, Empty SubjectCode : " + obj.getSubjectcode() + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				}
				if(!MDMSubjectCodeController.applyRegExp(MDMSubjectCodeController.ALPHANUMERIC_REGEX, obj.getSubjectcode())) {
					addError(bean, "Upload file, Special Characters not allowed in SubjectCode : " + obj.getSubjectcode() + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				}
				if(this.checkIfDuplicateMDMSubjectCode(obj.getSubjectcode())) {
					addError(bean, "Upload file, Duplicate SubjectCode : " + obj.getSubjectcode() + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				}
				if(isBlank(obj.getSubjectname())) {
					addError(bean, "Upload file, Empty Subject Name : " + obj.getSubjectname() + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				}
				temp = obj.getActive();
				if(isBlank(temp)) {
					addError(bean, "Upload file, Empty Active Status : " + temp + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setActive(changeToYN(temp));
				}
				temp = obj.getIsProject();
				if(isBlank(temp)) {
					addError(bean, "Upload file, Empty IsProject : " + temp + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setIsProject(changeToNumberString(temp));
				}
				
				temp = obj.getStudentType();
				if(!isBlank(temp)) {
					obj.setStudentType(temp);
				}
				
				if(null == obj.getSessionTime() || Character.isDigit(obj.getSessionTime())) {
					addError(bean, "Upload file, Invalid Session Time : " + obj.getSessionTime() + " at Sr.No : " + (w+1));
					isProcessed = Boolean.FALSE;
					break;
				}
				
				obj.setCreatedBy(userId);
				obj.setLastModifiedBy(userId);
				isProcessed = Boolean.TRUE;
			}
			if(isProcessed) {
				isProcessed = batchSaveMDMSubjectCodeData(list, userId);
			}
		} catch(IOException excepIO) {
			bean.setStatus(KEY_ERROR);
			bean.setMessage(excepIO.getMessage());
		} finally {
			if(!isProcessed) {
				if(isBlank(bean.getStatus())) {
					addError(bean, "Upload File, Error saving data.");
				}
			}
			if(null != list) list.clear();
		}
		return isProcessed;
	}
	
	public void addError(MDMSubjectCodeBean obj, String message) {
		obj.setStatus(KEY_ERROR);
		obj.setMessage(message);
		logger.error("MDMSubjectCodeService : addError(MDMSubjectCodeBean) : " + message);
	}
	
	protected boolean batchSaveMDMSubjectCodeData(List<MDMSubjectCodeBean> list, String userId) {
		logger.info("MDMSubjectCodeService : batchSaveMDMSubjectCodeData");
		boolean isSuccess = Boolean.FALSE;
		MDMSubjectCodeBean mdmSubjectCodeBean = null;
		try {
			for(int d = 0; d < list.size(); d++) {
				mdmSubjectCodeBean = list.get(d);
				populateSpecialization(1, mdmSubjectCodeBean);
				mdmSubjectCodeBean.setCreatedBy(userId);
				mdmSubjectCodeBean.setLastModifiedBy(userId);
				//isSuccess = mdmSubjectCodeDAO.saveMDMSubjectCodeData(mdmSubjectCodeBean);
				//if(!isSuccess) {
					//break;
				//}
			}
			isSuccess = mdmSubjectCodeDAO.batchSaveMDMSubjectCodeData(list);
		}  catch (Exception e) {
			logger.error("MDMSubjectCodeService : batchSaveMDMSubjectCodeData : " + e.getMessage());
			
			throw e;
		}
		return isSuccess;
	}
	
	public Map<String,Object> prepareMDMSubjectCode() {
		logger.info("MDMSubjectCodeService : prepareMDMSubjectCode");
		Map<String,Object> dataPrepared = null;
		List<String> specializationTypeNameList = null;
		List<MDMSubjectCodeBean> subjectCodeList = null;
		
		subjectCodeList = fetchMDMSubjectCodeList();
		
		specializationTypeNameList = fetchSpecializationTypeNameList();
		
		dataPrepared = new LinkedHashMap<String, Object>();
		dataPrepared.put("subjectCodeList", subjectCodeList);
		dataPrepared.put("specializationTypeNameList", specializationTypeNameList);
		
		return dataPrepared;
	}
	
	public boolean saveMDMSubjectCode(MDMSubjectCodeBean mdmSubjectCodeBean, String userId) {
		logger.info("MDMSubjectCodeService : saveMDMSubjectCode");
		boolean isSuccess = Boolean.FALSE;
		try {
			populateSpecialization(1, mdmSubjectCodeBean);
			mdmSubjectCodeBean.setCreatedBy(userId);
			mdmSubjectCodeBean.setLastModifiedBy(userId);
			isSuccess = mdmSubjectCodeDAO.saveMDMSubjectCodeData(mdmSubjectCodeBean);
		}  catch (Exception e) {
			logger.error("MDMSubjectCodeService : saveMDMSubjectCode : " + e.getMessage());
			

			mdmSubjectCodeBean.setStatus(KEY_ERROR);
			mdmSubjectCodeBean.setMessage(e.getMessage());
		}
		return isSuccess;
	}
	
	public boolean checkIfDuplicateMDMSubjectCode(String subjectCode) {
		logger.info("MDMSubjectCodeService : checkIfDuplicateMDMSubjectCode");
		boolean isDuplicate = mdmSubjectCodeDAO.checkIfDuplicateMDMSubjectCode(subjectCode);
		return isDuplicate;
	}
	
	public boolean updateMDMSubjectCode(MDMSubjectCodeBean bean) {
		logger.info("MDMSubjectCodeService : updateMDMSubjectCode");
		boolean isSuccess = Boolean.FALSE;
		try {
			populateSpecialization(1, bean);
			isSuccess = mdmSubjectCodeDAO.updateMDMSubjectCodeData(bean);
		} catch (Exception e) {
			logger.error("MDMSubjectCodeService : updateMDMSubjectCode : " + e.getMessage());
			
			
			bean.setStatus(KEY_ERROR);
			bean.setMessage(e.getMessage());
		}
		return isSuccess;
	}
	
	public Integer deleteMDMSubjectCode(MDMSubjectCodeBean bean) {
		logger.info("MDMSubjectCodeService : deleteMDMSubjectCode");
		Integer rowsAffected = -1;
		try {
			if(null != bean) {
				rowsAffected = mdmSubjectCodeDAO.deleteMDMSubjectCode(bean);
			}
		}  catch (Exception e) {
			logger.error("MDMSubjectCodeService : deleteMDMSubjectCode : " + e.getMessage());
			

			bean.setStatus(KEY_ERROR);
			bean.setMessage(e.getMessage());
		}
		return rowsAffected;
	}
	
	public void populateSpecialization(int mode, List<MDMSubjectCodeBean> subjectCodeList) {
		List<ProgramExamBean> listProgramBean = null;
		listProgramBean = dashboardDao.getSpecializationtypeList();
		if (null != listProgramBean && !listProgramBean.isEmpty()) {
			for (MDMSubjectCodeBean mdmSubjectCodeBean : subjectCodeList) {
				for (ProgramExamBean pb : listProgramBean) {
					if (compareSpecialization(pb, mode, mdmSubjectCodeBean)) {
						break;// go to next bean.
					}
				}
			}
		}
	}

	public void populateSpecialization(int mode, MDMSubjectCodeBean mdmSubjectCodeBean) {
		List<ProgramExamBean> listProgramBean = null;
		listProgramBean = dashboardDao.getSpecializationtypeList();
		if (null != listProgramBean && !listProgramBean.isEmpty()) {
			for (ProgramExamBean pb : listProgramBean) {
				if (compareSpecialization(pb, mode, mdmSubjectCodeBean)) {
					break;// exit from loop.
				}
			}
		}
	}

	protected boolean compareSpecialization(ProgramExamBean pb, int mode, MDMSubjectCodeBean mdmSubjectCodeBean) {
		boolean isMatch = Boolean.FALSE;
		if (1 == mode) {
			if (pb.getSpecializationType().equalsIgnoreCase(mdmSubjectCodeBean.getSpecializationType())) {
				mdmSubjectCodeBean.setSpecializationType(pb.getId());
				isMatch = Boolean.TRUE;
			} /*else if ("No Specialization".equalsIgnoreCase(mdmSubjectCodeBean.getSpecializationType())) {
				mdmSubjectCodeBean.setSpecializationType("0");
				isMatch = Boolean.TRUE;
			}*/
		} else if (2 == mode) {
			if (pb.getId().equals(mdmSubjectCodeBean.getSpecializationType())) {
				mdmSubjectCodeBean.setSpecializationType(pb.getSpecializationType());
				isMatch = Boolean.TRUE;
			} /*else if ("0".equals(mdmSubjectCodeBean.getSpecializationType())) {
				mdmSubjectCodeBean.setSpecializationType("No Specialization");
				isMatch = Boolean.TRUE;
			}*/
		}
		return isMatch;
	}
	
	protected List<String> fetchSpecializationTypeNameList() {
		ArrayList<String> specializationTypeNameList = null;
		specializationTypeNameList = dashboardDao.getSpecializationtypeNameList();
		logger.info("MDMSubjectCodeService : fetchSpecializationTypeNameList : " + specializationTypeNameList);
		return specializationTypeNameList;
	}
	
	/**
	 * Fetch all MDMSubjectCode(s).
	 * @param columnRequired 1 for all columns, 0 for two columns.
	 * @return
	 */
	public List<MDMSubjectCodeBean> fetchMDMSubjectCodeList() {
		List<MDMSubjectCodeBean> subjectCodeList = null;
		logger.info("MDMSubjectCodeService : fetchMDMSubjectCodeList");
		subjectCodeList = (ArrayList<MDMSubjectCodeBean>) mdmSubjectCodeDAO.fetchMDMSubjectCodeList(1);
		populateSpecialization(2, subjectCodeList);
		return subjectCodeList;
	}
	
	/**
	 * For the consumerType, select all applicable ProgramStructure(s).
	 * @param consumerTypeId
	 * @return
	 */
	public List<ConsumerProgramStructureExam> fetchPrgmStruc_By_ConsumerType(String consumerTypeId) {
		logger.info("MDMSubjectCodeService : fetchPrgmStruc_By_ConsumerType");
		List<ConsumerProgramStructureExam> cpsList = null;
		cpsList = mdmSubjectCodeDAO.fetchPrgmStruc_By_ConsumerType(consumerTypeId);
		return cpsList;
	}
	
	/**
	 * For the program structure and consumerType, select all applicable Program(s).
	 * @param programStructureId
	 * @param consumerTypeId
	 * @return
	 */
	public List<ConsumerProgramStructureExam> fetchProgram_By_PrgmStruc_ConsumerType(String programStructureId, String consumerTypeId) {
		logger.info("MDMSubjectCodeService : fetchProgram_By_PrgmStruc_ConsumerType");
		List<ConsumerProgramStructureExam> cpsList = null;
		cpsList = mdmSubjectCodeDAO.fetchProgram_By_PrgmStruc_ConsumerType(programStructureId, consumerTypeId);
		return cpsList;
	}
	
	/**
	 * Fetch all MDMSubjectCode(s).
	 * @param columnRequired 1 for all columns, 0 for two columns.
	 * @return
	 */
	protected List<MDMSubjectCodeBean> fetchMDMSubjectCodeList(int columnRequired) {
		logger.info("MDMSubjectCodeService : fetchMDMSubjectCodeList");
		List<MDMSubjectCodeBean> subjectCodeList = null;
		subjectCodeList = (ArrayList<MDMSubjectCodeBean>) mdmSubjectCodeDAO.fetchMDMSubjectCodeList(columnRequired);
		return subjectCodeList;
	}
	
	/**
	 * Fetch List of Consumer Type.
	 * @return
	 */
	protected List<ConsumerProgramStructureExam> fetchConsumerTypeList() {
		logger.info("MDMSubjectCodeService : fetchConsumerTypeList");
		List<ConsumerProgramStructureExam> listCPS = null;
		listCPS = asignmentsDAO.getConsumerTypeList();
		return listCPS;
	}

	public boolean saveMDMSubjectCodeMappingList(final List<MDMSubjectCodeMappingBean> list, final String userId) {
		logger.info("MDMSubjectCodeService : saveMDMSubjectCodeMappingList");
		boolean isSuccess = Boolean.FALSE;
		MDMSubjectCodeMappingBean localBean = null;
		for(int v = 0; v < list.size(); v++) {
			localBean = list.get(v);
			localBean.setCreatedBy(userId);
			isSuccess = mdmSubjectCodeDAO.saveMDMSubjectCodeMappingData(localBean);
			if(!isSuccess) {
				logger.error("MDMSubjectCodeService : saveMDMSubjectCodeMappingList : " + localBean.getMessage());
				break;
			}
		}
		return isSuccess;
	}
	
	public boolean processFile(MDMSubjectCodeMappingBean bean, String userId) {
		logger.info("MDMSubjectCodeService : processFile");
		Boolean isProcessed = Boolean.FALSE;
		Boolean isMatch = Boolean.FALSE;
		MDMSubjectCodeMappingBean obj = null;
		MDMSubjectCodeBean mdmSubjCodeBean = null;
		String temp = null;
		String consumerTypeId = null;
		String programStructureId = null;
		String programId = null;
		List<ConsumerProgramStructureExam> listConsumerType = null;
		List<ConsumerProgramStructureExam> listCPS = null;
		List<MDMSubjectCodeMappingBean> list = null;
		List<MDMSubjectCodeBean> subjectCodeList = null;
		ConsumerProgramStructureExam objCPS = null;
		Set<String> semesterSet = null;
		try {
			semesterSet = new HashSet<String>();
			semesterSet.add("1");
			semesterSet.add("2");
			semesterSet.add("3");
			semesterSet.add("4");
			semesterSet.add("5");
			semesterSet.add("6");
			semesterSet.add("7");
			semesterSet.add("8");
			semesterSet.add("9");
			semesterSet.add("10");
			
			listConsumerType = fetchConsumerTypeList();
			subjectCodeList = fetchMDMSubjectCodeList(0);

			list = mdmExcelHelper.processMDMSubjectCodeMappingFile(bean);
			for(int w = 0; w < list.size(); w++) {
				obj = list.get(w);
				if(isBlank(obj.getConsumerTypeId())) {
					addError(obj, ("Upload file, Empty ConsumerType : " + obj.getConsumerTypeId() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					for(int h = 0; h < listConsumerType.size(); h++) {
						if(areEqualString(obj.getConsumerTypeId(), listConsumerType.get(h).getName())) {
							consumerTypeId = listConsumerType.get(h).getId();
							obj.setConsumerType(consumerTypeId);
							isMatch = Boolean.TRUE;
							break;
						}
					}
				}
				if(!isMatch) {
					addError(obj, ("Upload file, Invalid ConsumerType : " + obj.getConsumerTypeId() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				isMatch = Boolean.FALSE;
				
				listCPS = fetchPrgmStruc_By_ConsumerType(consumerTypeId);
				for(int i = 0; i < listCPS.size(); i++) {
					objCPS = listCPS.get(i);
					if(areEqualString(obj.getProgramStructureId(), objCPS.getProgram_structure())) {
						programStructureId = objCPS.getProgramStructureId();
						obj.setPrgmStructApplicable(programStructureId);
						isMatch = Boolean.TRUE;
						break;
					}
				}
				listCPS.clear();
				if(!isMatch) {
					addError(obj, ("Upload file, Invalid Program Structure : " + obj.getProgramStructureId() + ", as per Consumer Type : " + obj.getConsumerTypeId() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				isMatch = Boolean.FALSE;
				
				listCPS = fetchProgram_By_PrgmStruc_ConsumerType(programStructureId, consumerTypeId);
				for(int j = 0; j < listCPS.size(); j++) {
					objCPS = listCPS.get(j);
					if(areEqualString(obj.getProgramId(), objCPS.getCode())) {
						programId = objCPS.getProgramId();
						obj.setProgram(programId);
						isMatch = Boolean.TRUE;
						break;
					}
				}
				listCPS.clear();
				if(!isMatch) {
					addError(obj, ("Upload file, Invalid Program : " + obj.getProgramId() + ", as per ProgramStructure, Consumer Type : " + obj.getProgramStructureId() + "," +obj.getConsumerTypeId() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				isMatch = Boolean.FALSE;
				
				for(int k = 0; k < subjectCodeList.size(); k++) {
					mdmSubjCodeBean = subjectCodeList.get(k);
					if(areEqualString(mdmSubjCodeBean.getSubjectcode(), obj.getSubjectCodeId())) {
						obj.setSubjectCodeId(toString(mdmSubjCodeBean.getId()));
						isMatch = Boolean.TRUE;
						break;
					}
				}
				//subjectCodeList.clear();
				if(!isMatch) {
					addError(obj, ("Upload file, Invalid SubjectCode : " + obj.getSubjectCodeId() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				isMatch = Boolean.FALSE;
				
				if(null == obj.getSem()) {
					addError(obj, ("Upload file, Invalid Semester : " + obj.getSem() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else if(!(semesterSet.contains(obj.getSem()))) {
					addError(obj, ("Upload file, Wrong Semester : " + obj.getSem() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				
				if(null == obj.getPassScore()) {
					addError(obj, ("Upload file, Invalid Program Pass Score : " + obj.getPassScore() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				if(null == obj.getMaxGraceMarks()) {
					addError(obj, ("Upload file, Invalid Max Grace Marks : " + obj.getMaxGraceMarks() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				if(null == obj.getSifySubjectCode()) {
					addError(bean, ("Upload file, Invalid SifySubjectCode : " + obj.getSifySubjectCode() + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				}
				temp = obj.getActive();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty ActiveStatus : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setActive(changeToYN(temp));
				}
				temp = obj.getHasIA();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty HasIA : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setHasIA(changeToYN(temp));
				}
				temp = obj.getHasTest();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty HasTest : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setHasTest(changeToYN(temp));
				}
				temp = obj.getHasAssignment();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty HasAssignment : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setHasAssignment(changeToYN(temp));
				}
				temp = obj.getAssignmentNeededBeforeWritten();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty AssignmentNeededBeforeWritten : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setAssignmentNeededBeforeWritten(changeToYN(temp));
				}
				temp = obj.getCreateCaseForQuery();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty CreateCaseForQuery : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setCreateCaseForQuery(changeToYN(temp));
				}
				temp = obj.getAssignQueryToFaculty();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty AssignQueryToFaculty : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setAssignQueryToFaculty(changeToYN(temp));
				}
				temp = obj.getIsGraceApplicable();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty IsGraceApplicable : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setIsGraceApplicable(changeToYN(temp));
				}
				temp = obj.getAssignmentScoreModel();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty AssignmentScoreModel : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setAssignmentScoreModel(changeForModel(temp));
				}
				temp = obj.getWrittenScoreModel();
				if(isBlank(temp)) {
					addError(obj, ("Upload file, Empty WrittenScoreModel : " + temp + " at Sr.No : " + (w+1)));
					isProcessed = Boolean.FALSE;
					break;
				} else {
					obj.setWrittenScoreModel(changeForModel(temp));
				}
				obj.setCreatedBy(userId);
				obj.setLastModifiedBy(userId);
				isProcessed = Boolean.TRUE;
			}
			if(isProcessed) {
				isProcessed = saveMDMSubjectCodeMappingList(list, userId);
			}
		} catch(IOException excepIO) {
			bean.setStatus(KEY_ERROR);
			bean.setMessage(excepIO.getMessage());
			isProcessed = Boolean.FALSE;
		} finally {
			if(!isProcessed) {
				if(null != list) {
					for(int x = 0; x < list.size(); x++) {
						obj = list.get(x);
						if(!isBlank(obj.getStatus())) {
							bean.setStatus(obj.getStatus());
							bean.setMessage("Invalid (" + obj.getConsumerTypeId() + "," +  obj.getProgramStructureId() + "," + obj.getProgramId() + ") " + obj.getMessage());
							break;
						}
					}
				}
			}
			if(null != subjectCodeList) subjectCodeList.clear();
			if(null != listCPS) listCPS.clear();
			if(null != list) list.clear();
			if(null != listConsumerType) listConsumerType.clear();
			if(null != semesterSet) semesterSet.clear();
			obj = null;
			mdmSubjCodeBean = null;
			temp = null;
			consumerTypeId = null;
			programStructureId = null;
			programId = null;
			listConsumerType = null;
			listCPS = null;
			list = null;
			subjectCodeList = null;
			objCPS = null;
			semesterSet = null;
		}
		return isProcessed;
	}
	
	public void addError(MDMSubjectCodeMappingBean obj, String message) {
		obj.setStatus(KEY_ERROR);
		obj.setMessage(message);
		logger.error("MDMSubjectCodeService : addError(MDMSubjectCodeMappingBean) : " + message);
	}
	
	public boolean saveMDMSubjectCodeMapping(final MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean, final String userId) {
		logger.info("MDMSubjectCodeService : saveMDMSubjectCodeMapping");
		boolean isSuccess = Boolean.FALSE;
		try {
			mdmSubjectCodeMappingBean.setCreatedBy(userId);
			isSuccess = mdmSubjectCodeDAO.saveMDMSubjectCodeMappingData(mdmSubjectCodeMappingBean);
		} catch (Exception e) {
			logger.error("MDMSubjectCodeService : saveMDMSubjectCodeMapping : " + e.getMessage());
			
			
			mdmSubjectCodeMappingBean.setStatus(KEY_ERROR);
			mdmSubjectCodeMappingBean.setMessage(e.getMessage());// "Entries Not inserted Correctly"
		}
		return isSuccess;
	}
	
	public Map<String,Object> prepareMDMSubjectCodeMapping() {
		logger.info("MDMSubjectCodeService : prepareMDMSubjectCodeMapping");
		StringBuffer strBuf = null;
		Map<String,Object> dataPrepared = null;
		Map<String,String> consumerTypeMap = null;
		Map<Integer,String> subjCodeMap = null;
		List<ConsumerProgramStructureExam> listConsumerType = null;
		List<MDMSubjectCodeBean> subjectCodeList = null;
		List<MDMSubjectCodeMappingBean> subjectCodeMappingList = null;
		
		listConsumerType = fetchConsumerTypeList();
		consumerTypeMap = new LinkedHashMap<String, String>();
		for(ConsumerProgramStructureExam bean : listConsumerType) {
			consumerTypeMap.put(bean.getId(), bean.getName());
		}
		
		subjectCodeMappingList = fetchMDMSubjectCodeMappingList(); //mdmSubjectCodeDAO.fetchMDMSubjectCodeMappingList();

		subjectCodeList = fetchMDMSubjectCodeList(0);
		//strBuf = new StringBuffer();
		//strBuf.append("'{");
		subjCodeMap = new LinkedHashMap<Integer, String>();
		for(MDMSubjectCodeBean bean : subjectCodeList) {
			subjCodeMap.put(bean.getId(), bean.getSubjectcode());
			//strBuf.append("\"").append(bean.getId()).append("\":\"").append(bean.getSubjectcode()).append("\",");
		}
		///strBuf.deleteCharAt(strBuf.length() - 1);//remove the last comma
		//strBuf.append("}'");
		
		dataPrepared = new LinkedHashMap<String, Object>();
		dataPrepared.put("consumerTypeMap", consumerTypeMap);
		dataPrepared.put("subjectCodeMappingList", subjectCodeMappingList);
		dataPrepared.put("subjCodeMap", subjCodeMap);
		//dataPrepared.put("subjCodeString", strBuf.toString());
		
		MDMSubjectCodeDAO.emptyStringBuffer(strBuf);
		return dataPrepared;
	}
	
	public Integer deleteMDMSubjectCodeMapping(MDMSubjectCodeMappingBean bean) {
		logger.info("MDMSubjectCodeService : deleteMDMSubjectCodeMapping");
		Integer rowsAffected = -1;
		try {
			if(null != bean) {
				rowsAffected = mdmSubjectCodeDAO.deleteMDMSubjectCodeMapping(bean);
			}
		}  catch (Exception e) {
			logger.error("MDMSubjectCodeService : deleteMDMSubjectCodeMapping : " + e.getMessage());
			

			bean.setStatus(KEY_ERROR);
			bean.setMessage(e.getMessage());
		}
		return rowsAffected;
	}
	
	public Integer updateMDMSubjectCodeMapping(final MDMSubjectCodeMappingBean bean) {
		logger.info("MDMSubjectCodeService : updateMDMSubjectCodeMapping");
		Integer rowsAffected = -1;
		try {
			if(null != bean) {
				rowsAffected = mdmSubjectCodeDAO.updateMDMSubjectCodeMapping(bean);
			}
		}  catch (Exception e) {
			logger.error("MDMSubjectCodeService : updateMDMSubjectCodeMapping : " + e.getMessage());
			bean.setStatus(KEY_ERROR);
			bean.setMessage(e.getMessage());
		}
		return rowsAffected;
	}
	
	/**
	 * Fetch all MDMSubjectCodeMapping(s).
	 * @return
	 */
	public List<MDMSubjectCodeMappingBean> fetchMDMSubjectCodeMappingList() {
		logger.info("MDMSubjectCodeService : fetchMDMSubjectCodeMappingList");
		List<MDMSubjectCodeMappingBean> subjectCodeMappingList = null;
		subjectCodeMappingList = mdmSubjectCodeDAO.fetchMDMSubjectCodeMappingList();
		return subjectCodeMappingList;
	}
	
	public String fetchConsumerType(String consumerTypeId) {
		logger.info("MDMSubjectCodeService : fetchConsumerType");
		String consumerType = null;
		List<ConsumerProgramStructureExam> listConsumerType = fetchConsumerTypeList();
		for (ConsumerProgramStructureExam bean : listConsumerType) {
			if (consumerTypeId.equals(bean.getId())) {
				consumerType = bean.getName();
				break;
			}
		}
		listConsumerType.clear();
		return consumerType;
	}

	public String fetchProgramStructure(String consumerTypeId, String programStructureId) {
		logger.info("MDMSubjectCodeService : fetchProgramStructure");
		String programStructure = null;
		List<ConsumerProgramStructureExam> cpsList = null;
		cpsList = this.fetchPrgmStruc_By_ConsumerType(consumerTypeId);
		for (ConsumerProgramStructureExam cps : cpsList) {
			if (programStructureId.equals(cps.getProgramStructureId())) {
				programStructure = cps.getProgram_structure();
				break;
			}
		}
		cpsList.clear();
		return programStructure;
	}

	public String fetchProgram(String consumerTypeId, String programStructureId, String programId) {
		logger.info("MDMSubjectCodeService : fetchProgram");
		String program = null;
		List<ConsumerProgramStructureExam> cpsList = null;
		cpsList = this.fetchProgram_By_PrgmStruc_ConsumerType(programStructureId, consumerTypeId);
		for (ConsumerProgramStructureExam cps : cpsList) {
			if (programId.equals(cps.getProgramId())) {
				program = cps.getCode();
				break;
			}
		}
		cpsList.clear();
		return program;
	}

	public MDMSubjectCodeBean fetchMDMSubjectCode(String subjectCodeId) {
		logger.info("MDMSubjectCodeService : fetchMDMSubjectCode");
		MDMSubjectCodeBean outBean = null;
		List<MDMSubjectCodeBean> subjectCodeList = null;
		int newDataId = -1;
		//logger.info("MDMSubjectCodeService : Compare : "+subjectCodeId);
		Integer dataId = MDMSubjectCodeDAO.toInteger(subjectCodeId);
		newDataId = dataId.intValue();
		subjectCodeList = fetchMDMSubjectCodeList();
		for (MDMSubjectCodeBean bean : subjectCodeList) {
			//logger.info("MDMSubjectCodeService : Compare To : "+bean.getId() + " Compare : "+dataId);
			if(newDataId == bean.getId().intValue()) {
				outBean = bean;
				break;
			}
		}
		subjectCodeList.clear();
		return outBean;
	}
	
	public MDMSubjectCodeMappingBean fetchMDMSubjectCodeMapping(Integer subjectCodeMappingId) {
		logger.info("MDMSubjectCodeService : fetchMDMSubjectCodeMapping");
		MDMSubjectCodeMappingBean outBean = null;
		List<MDMSubjectCodeMappingBean> subjectCodeMappingList = null;
		subjectCodeMappingList = fetchMDMSubjectCodeMappingList();
		for(MDMSubjectCodeMappingBean bean : subjectCodeMappingList) {
			if(subjectCodeMappingId.intValue() == bean.getId().intValue()) {
				outBean = bean;
				break;
			}
		}
		subjectCodeMappingList.clear();
		return outBean;
	}
	
	
	public int deletePssId(MDMSubjectCodeMappingBean bean) {
		Integer rowsAffected = -1;
		boolean isSuccess = Boolean.FALSE;
		mdmSubjectCodeDAO.start_Transaction_U_PR("deletePssId");
		try {
			if(null != bean) {
				MDMSubjectCodeMappingBean newDataBean = fetchMDMSubjectCodeMapping(bean.getId());
				MDMSubjectCodeBean dataBean = fetchMDMSubjectCode(newDataBean.getSubjectCodeId());
				
				mdmSubjectCodeDAO.deleteProgramSubject(newDataBean.getPrgmStructApplicable(),newDataBean.getProgram(),newDataBean.getSem(),dataBean.getSubjectname());//delete from program subject table
		
				mdmSubjectCodeDAO.deleteProgramSemSubject(bean.getId());//delete from program sem subject table
				
				rowsAffected = mdmSubjectCodeDAO.deleteInMdm(bean.getId()); //delete in mdm table
				isSuccess = Boolean.TRUE;
				mdmSubjectCodeDAO.end_Transaction(isSuccess);
				
				bean.setStatus(KEY_SUCCESS);
				bean.setMessage("Total Rows Deleted : "+ rowsAffected);
			}
		}  catch (Exception e) {
			mdmSubjectCodeDAO.end_Transaction(Boolean.FALSE);
		
			logger.info("MDMSubjectCodeService : deleteMDMSubjectCodeMapping for bean: " + bean.toString());
			logger.error("MDMSubjectCodeService : deleteMDMSubjectCodeMapping Error : " + e.getMessage());
			bean.setStatus(KEY_ERROR);
			bean.setMessage("Not Allowed to delete this id as it has references.");
		}
		return rowsAffected;
	}
	

}
