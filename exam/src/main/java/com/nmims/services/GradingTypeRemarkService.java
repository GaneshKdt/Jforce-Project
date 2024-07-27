/**
 * 
 */
package com.nmims.services;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nmims.beans.RemarksGradeBean;
import com.nmims.interfaces.GradingTypeServiceInterface;
import com.nmims.stratergies.FileUploadStrategyInterface;
import com.nmims.stratergies.PassFailStrategyInterface;
import com.nmims.stratergies.PassFailTransferStrategyInterface;
import com.nmims.stratergies.ResultsDisplayStrategyInterface;
import com.nmims.stratergies.StudentStatusStrategyInterface;
import com.nmims.stratergies.AbsoluteGradingStrategyInterface;
import com.nmims.stratergies.FileDisplayStrategyInterface;

/**
 * @author vil_m
 *
 */

@Service("gradingTypeRemarkService")
public class GradingTypeRemarkService implements GradingTypeServiceInterface {
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Autowired
	@Qualifier("fileUploadStrategy")
	private FileUploadStrategyInterface fileUploadStrategy;//FileUpload
	
	@Autowired
	@Qualifier("fileDisplayStrategy")
	private FileDisplayStrategyInterface fileDisplayStrategy;
	
	@Autowired
	@Qualifier("studentStatusStrategy")
	private StudentStatusStrategyInterface studentStatusStrategy;
	
	@Autowired
	@Qualifier("passFailStrategy")
	private PassFailStrategyInterface passFailStrategy;
	
	@Autowired
	@Qualifier("absoluteGradingStrategy")
	private AbsoluteGradingStrategyInterface absoluteGradingStrategy;
	
	@Autowired
	@Qualifier("passFailTransferStrategy")
	private PassFailTransferStrategyInterface passFailTransferStrategy;
	
	@Autowired
	@Qualifier("resultsDisplayStrategy")
	private ResultsDisplayStrategyInterface resultsDisplayStrategy;
	
	public List<RemarksGradeBean> uploadMarksExcelFile(RemarksGradeBean databean, String userId) {
		logger.info("Entering GradingTypeRemarkService : uploadMarksExcelFile");
		List<RemarksGradeBean> list = null;
		list = fileUploadStrategy.processMarksExcelFile(databean, userId);
		logger.info("Exiting GradingTypeRemarkService : uploadMarksExcelFile");
		return list;
	}
	
	public List<RemarksGradeBean> displayMarksExcelFile(RemarksGradeBean databean) {
		logger.info("Entering GradingTypeRemarkService : displayMarksExcelFile");
		List<RemarksGradeBean> list = null;
		list = fileDisplayStrategy.displayFileMarks(databean);
		logger.info("Exiting GradingTypeRemarkService : displayMarksExcelFile");
		return list;
	}
	
	@Override
	public List<RemarksGradeBean> searchForAbsentStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkService : searchForAbsentStudents");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.searchForAbsentStudents(bean);
		return list1;
	}

	@Override
	public RemarksGradeBean moveAbsentStudents(RemarksGradeBean bean, String userId) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkService : moveAbsentStudents");
		RemarksGradeBean remarksGradeBean = null;
		remarksGradeBean = studentStatusStrategy.moveAbsentStudents(bean, userId);
		return remarksGradeBean;
	}
	
	public List<RemarksGradeBean> searchForCopyCaseStudents(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : searchForCopyCaseStudents");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.searchForCopyCaseStudents(bean);
		return list1;
	}
	
	public RemarksGradeBean moveCopyCaseStudents(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : moveCopyCaseStudents");
		RemarksGradeBean remarksGradeBean = null;
		remarksGradeBean = studentStatusStrategy.moveCopyCaseStudents(bean, userId);
		return remarksGradeBean;
	}
	
	public List<RemarksGradeBean> downloadAbsentStudents(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : downloadAbsentStudents");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.downloadAbsentStudents(bean);
		logger.info("Exiting GradingTypeRemarkService : downloadAbsentStudents");
		return list1;
	}

	@Override
	public List<RemarksGradeBean> downloadCopyCaseStudents(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkService : downloadCopyCaseStudents");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.downloadCopyCaseStudents(bean);
		logger.info("Exiting GradingTypeRemarkService : downloadCopyCaseStudents");
		return list1;
	}
	
	public List<RemarksGradeBean> searchForStudentMarksStatus(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : searchForStudentMarksStatus");
		List<RemarksGradeBean> list = null;
		list = studentStatusStrategy.searchForStudentMarksStatus(bean);
		logger.info("Exiting GradingTypeRemarkService : searchForStudentMarksStatus");
		return list;
	}
	
	public Boolean updateForStudentMarksStatus(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : updateForStudentMarksStatus");
		Boolean isUpdated = Boolean.FALSE;
		isUpdated = studentStatusStrategy.updateForStudentMarksStatus(bean, userId);
		logger.info("Exiting GradingTypeRemarkService : updateForStudentMarksStatus");
		return isUpdated;
	}
	
	public Map<String, Integer> fetchStudentSummary(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : fetchStudentSummary");
		Map<String, Integer> summaryMap = null;
		summaryMap = passFailStrategy.fetchSummary(bean, PassFailStrategyInterface.EXAM_MODE_ONLINE);
		logger.info("Exiting GradingTypeRemarkService : fetchStudentSummary");
		return summaryMap;
	}
	
	public Map<String, Integer> processStudentSummary(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : processStudentSummary");
		Map<String, Integer> summaryMap = null;
		summaryMap = passFailStrategy.processSummary(bean, PassFailStrategyInterface.EXAM_MODE_ONLINE, userId);
		logger.info("Exiting GradingTypeRemarkService : processStudentSummary");
		return summaryMap;
	}
	
	public Integer searchProcessStudentForGrade(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : searchProcessStudentForGrade");
		Integer totalRows = null;
		totalRows = absoluteGradingStrategy.searchProcessStudentForGrade(bean, userId);
		logger.info("Exiting GradingTypeRemarkService : searchProcessStudentForGrade");
		return totalRows;
	}
	
	public List<RemarksGradeBean> searchStudentsForTransfer(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : searchStudentsForTransfer");
		List<RemarksGradeBean> list1 = null;
		list1 = passFailTransferStrategy.searchStudentsForTransfer(bean);
		logger.info("Exiting GradingTypeRemarkService : searchStudentsForTransfer");
		return list1;
	}
	
	public List<RemarksGradeBean> downloadStudentsForTransfer(RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : downloadStudentsForTransfer");
		List<RemarksGradeBean> list1 = null;
		list1 = passFailTransferStrategy.downloadStudentsForTransfer(bean);
		logger.info("Exiting GradingTypeRemarkService : downloadStudentsForTransfer");
		return list1;
	}

	public Integer searchTransferStudents(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : searchTransferStudents");
		Integer rowsSaved = 0;
		rowsSaved = passFailTransferStrategy.searchTransferStudents(bean, userId);
		logger.info("Exiting GradingTypeRemarkService : searchTransferStudents");
		return rowsSaved;
	}
	
	public Boolean changeResultsLiveState(RemarksGradeBean bean, String userId) {
		logger.info("Entering GradingTypeRemarkService : changeResultsLiveState");
		Boolean isSuccess = Boolean.FALSE;
		isSuccess = resultsDisplayStrategy.changeResultsLiveState(bean, userId);
		logger.info("Exiting GradingTypeRemarkService : changeResultsLiveState");
		return isSuccess;
	}
	
	public List<RemarksGradeBean> searchResultsAsPassFailReport(RemarksGradeBean bean, Boolean countRequired) {
		logger.info("Entering GradingTypeRemarkService : searchResultsAsPassFailReport");
		List<RemarksGradeBean> list1 = null;
		list1 = resultsDisplayStrategy.searchResultsAsPassFailReport(bean, countRequired);
		logger.info("Exiting GradingTypeRemarkService : searchResultsAsPassFailReport");
		return list1;
	}
	
	public List<RemarksGradeBean> downloadResultsAsPassFailReport(final RemarksGradeBean bean) {
		logger.info("Entering GradingTypeRemarkService : downloadResultsAsPassFailReport");
		List<RemarksGradeBean> list1 = null;
		list1 = resultsDisplayStrategy.downloadResultsAsPassFailReport(bean);
		logger.info("Exiting GradingTypeRemarkService : downloadResultsAsPassFailReport");
		return list1;
	}

	@Override
	public List<RemarksGradeBean> searchStudentsForAssignments(final RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkService : searchStudentsForAssignments");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.searchStudentsForAssignments(bean);
		return list1;
	}

	@Override
	public List<RemarksGradeBean> downloadStudentsForAssignments(RemarksGradeBean bean) {
		// TODO Auto-generated method stub
		logger.info("Entering GradingTypeRemarkService : downloadStudentsForAssignments");
		List<RemarksGradeBean> list1 = null;
		list1 = studentStatusStrategy.downloadStudentsForAssignments(bean);
		return list1;
	}
}
