/**
 * 
 */
package com.nmims.stratergies.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.RemarksGradeDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.GradingExcelHelper;
import com.nmims.helpers.HibernateValidatorHelper;
import com.nmims.stratergies.FileUploadStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("fileUploadStrategy")
public class FileUploadStrategy implements FileUploadStrategyInterface {
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Autowired
	private GradingExcelHelper gradingExcelHelper;
	
	@Autowired
	private RemarksGradeDAO remarksGradeDAO;
	
	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	public List<RemarksGradeBean> processMarksExcelFile(RemarksGradeBean databean, String userId) {
		logger.info("Entering FileUploadStrategy : processMarksExcelFile");
		boolean isError = Boolean.FALSE;
		String message = null; 
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> list = null;
		List<RemarksGradeBean> errorList = null;
		String fileExtension = null;
		Boolean isSave = Boolean.TRUE;
		Boolean isValid = Boolean.TRUE;
		CommonsMultipartFile fileData = null;
		String examYear = null;
		String examMonth = null;
		
		try {
			fileData = databean.getFileData();
			examYear = databean.getYear();
			examMonth = databean.getMonth();
			
			logger.info("FileUploadStrategy : processMarksExcelFile : Reading File : "+fileData.getOriginalFilename());
			fileExtension = gradingExcelHelper.validateFileExtension(fileData.getOriginalFilename());
			if(isBlank(fileExtension)) {
				isError = Boolean.TRUE;
				message = "Invalid File Extension. File must be .xls or .xlsx";
				logger.error("FileUploadStrategy : processMarksExcelFile : Reading File : Error : "+message);
			} else {
				list = gradingExcelHelper.processRemarkGradingUGMarksFile(fileData);
				logger.info("FileUploadStrategy : processMarksExcelFile : Finished, Reading File");
				
				errorList = validateMarksExcelFile(list);
				message = "";
				if(null != errorList && errorList.size() > 0) {
					isValid = Boolean.FALSE;
					for(int d = 0; d < errorList.size(); d++) {
						message += errorList.get(d).getMessage();
						message += "\n";
					}
				}
				if(isValid) {
					logger.info("FileUploadStrategy : processMarksExcelFile : Read File successfully, Rows to be Saved : "+list.size());
					for(int n = 0; n < list.size(); n++) {
						list.get(n).setStatus(RemarksGradeBean.ATTEMPTED);
						list.get(n).setProcessed(RemarksGradeBean.NOTPROCESSED);
						list.get(n).setResultLive(RemarksGradeBean.RESULT_NOT_LIVE);
						list.get(n).setActive(RemarksGradeBean.ROWS_NOT_DELETED);
					}
					isSave = saveMarksExcelFile(list, examYear, examMonth, userId);
					if(isSave) {
						isError = Boolean.FALSE;
						message = "File Uploaded Successfully. (Rows,"+list.size()+")";
					} else {
						isError = Boolean.TRUE;
						for(int t = 0; t < list.size(); t++) {
							message += list.get(t).getMessage();
							message += "\n";
						}
						logger.info("FileUploadStrategy : processMarksExcelFile : Failure, while saving Rows");
					}
				} else {
					isError = Boolean.TRUE;
				}
			}
		} catch (IOException e) {
			isError = Boolean.TRUE;
			message = e.getMessage();
		} catch (Exception ex) {
			isError = Boolean.TRUE;
			message = ex.getMessage();
		} finally {
			errorList = new ArrayList<RemarksGradeBean>();
			bean = new RemarksGradeBean();
			bean.setMessage(message);
			errorList.add(bean);
			if(isError) {
				bean.setStatus(RemarksGradeBean.KEY_ERROR);
				logger.error("FileUploadStrategy : processMarksExcelFile : Reading File : Error : "+message);
			} else {
				bean.setStatus(RemarksGradeBean.KEY_SUCCESS);
				logger.info("FileUploadStrategy : processMarksExcelFile : Reading File : Success : "+message);
			}
		}
		return errorList;
	}
	
	public List<RemarksGradeBean> validateMarksExcelFile(List<RemarksGradeBean> list) {
		logger.info("Entering FileUploadStrategy : validateMarksExcelFile");
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> retList = null;
		Set<ConstraintViolation<RemarksGradeBean>> setConstraintViolations = null;
		Iterator<ConstraintViolation<RemarksGradeBean>> iterObj = null;
		Validator validator = null;
		boolean isProceed = Boolean.TRUE;
		List<String> subjectList = null;
		StringBuilder strBuil = null;
		if(null != list) {
			if(list.size() == 1) {
				//If error while reading from EXCEL file
				if(RemarksGradeBean.KEY_ERROR.equalsIgnoreCase(list.get(0).getStatus())) {
					retList = list;
					isProceed = Boolean.FALSE;
				}
			}
			if(isProceed) {
				subjectList = studentMarksDAO.getAllSubjects();
				//If error in data in column of EXCEL file
				retList = new ArrayList<RemarksGradeBean>();
				validator = HibernateValidatorHelper.getValidator();
				for (int d = 0; d < list.size(); d++) {
					setConstraintViolations = validator.validate(list.get(d));
	
					if (setConstraintViolations.size() > 0) {
						iterObj = setConstraintViolations.iterator();
						while (iterObj.hasNext()) {
							if (null == strBuil) {
								strBuil = new StringBuilder();
							}
							strBuil.append("|").append(iterObj.next().getMessage());
						}
					}
					
					//Are Subjects valid
					if(!subjectList.contains(list.get(d).getSubject())) {
						if (null == strBuil) {
							strBuil = new StringBuilder();
						}
						strBuil.append("|").append("Invalid Subject (Sapid,Subject) : ("+list.get(d).getSapid()+","+list.get(d).getSubject()+")");
					}
					
					if (null != strBuil) {
						if (null == bean) {
							bean = new RemarksGradeBean();
							bean.setStatus(RemarksGradeBean.KEY_ERROR);
						}
						bean.setMessage("Row " + (d + 1) + " -> " + strBuil.toString());
						retList.add(bean);
						emptyStringBuilder(strBuil);
					}
					
					strBuil = null;
					bean = null;
				}
			}
		}
		return retList;
	}
	
	public boolean saveMarksExcelFile(final List<RemarksGradeBean> list, final String examYear, final String examMonth,
			final String userId) {
		logger.info("Entering FileUploadStrategy : saveMarksExcelFile");
		String msg = null;
		boolean isSuccess = Boolean.TRUE;
		RemarksGradeBean bean = null;

		remarksGradeDAO.fetchProgramSemSubjectId(list);
		for (int f = 0; f < list.size(); f++) {
			bean = list.get(f);
			if (null == bean.getProgramSemSubjectId()) {
				bean.setStatus(RemarksGradeBean.KEY_ERROR);
				msg = "Error : ProgramSemSubjectId (Row,SapId,Subject,Program) (" + (f + 1) + "," +  bean.getSapid()
						+ "," + bean.getSubject() + "," + bean.getProgram() + ")";
				bean.setMessage(msg);
				isSuccess = Boolean.FALSE;
			}
			msg = null;
		}
		if (isSuccess) {
			isSuccess = remarksGradeDAO.saveMarks(list, examYear, examMonth, userId);
		}
		return isSuccess;
	}
	
	public static void emptyStringBuilder(StringBuilder strBuil) {
		if(null != strBuil) {
			strBuil.delete(0, strBuil.length() - 1);
		}
	}
	
	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
}
