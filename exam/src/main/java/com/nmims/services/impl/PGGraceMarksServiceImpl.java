package com.nmims.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.PassFailTransferDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.services.GraceMarksService;
import com.nmims.services.PGGraceMarksService;
import com.nmims.services.PassFailExecutorService;

@Service
public class PGGraceMarksServiceImpl implements PGGraceMarksService {

	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");

	private final String COPY_CASE_STR = "Copy Case";

	@Autowired
	private PassFailTransferDao passFailTransferDao;

	@Autowired
	private StudentMarksDAO studentMarksDao;

	@Autowired
	private PassFailDAO passFailDao;

	@Autowired
	private PassFailExecutorService passFailExecutorService;
	
	@Override
	public List<PGGraceMarksDTO> getValidityEndGraceApplicableRecords(String examYear, String examMonth, int grace) {
		passFailLogger.info("{} {} getValidityEndGraceApplicableRecords Fetching cycle : grace : {}", examMonth,examYear, grace);

		List<PGGraceMarksDTO> records = passFailTransferDao.getValidityEndApplicableRecords(examYear, examMonth,grace);

		passFailLogger.info("{} {} getValidityEndGraceApplicableRecords records found : {} grace : {}", examMonth,examYear, records.size(), grace);

		return records;
	}

	@Override
	public HashMap<String, ArrayList> updateMarksAndReturnMarksRecords(String totalGracemarks, String program,
			String sapid, String consumerType, String resultProcessedYear, String resultProcessedMonth, String prgmStructApplicable) throws NoRecordFoundException {

		passFailLogger.info("applyGraceforValidityEnd (totalGracemarks, program, sapid, consumerType, resultProcessedYear, resultProcessedMonth, prgmStructApplicable) "
						+ "({}, {}, {}, {}, {}, {}, {})",totalGracemarks, program, sapid, consumerType, resultProcessedYear, resultProcessedMonth,prgmStructApplicable);

		HashMap<String, ArrayList> keyMaps = new HashMap<>();

		List<StudentMarksBean> studentMarksDetails = passFailDao.getSingleStudentPassFailMarksData(sapid, program);

		int totalApplicableGrace = Integer.parseInt(totalGracemarks);

		if (studentMarksDetails.isEmpty())
			throw new NoRecordFoundException("Students PassFail marks not found.");

		for (StudentMarksBean bean : studentMarksDetails) {
			passFailLogger.info("applyGraceforValidityEnd (sapid, totalApplicableGrace) ({} , {})", sapid,totalApplicableGrace);

			if (totalApplicableGrace > 0) {
				
				int grace = 50 - (Integer.parseInt(bean.getTotalMarks()));
				int writtenScore = Integer.parseInt(bean.getWritenscore()) + grace;
				int total = Integer.parseInt(bean.getTotalMarks()) + grace;

				passFailLogger.info("applyGraceforValidityEnd (grace, writtenScore, total) ({}, {}, {})", grace, writtenScore, total);

				setGraceMarksAndRemarks(bean, grace, writtenScore, total);

				try {
					studentMarksDao.updateStudentMarks(bean);
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage());
				}

				addMarksRecordsToKeyMaps(program, consumerType, prgmStructApplicable, keyMaps, bean);

				totalApplicableGrace = totalApplicableGrace - grace;
			}

		}
		return keyMaps;
	}

	private void addMarksRecordsToKeyMaps(String program, String consumerType, String prgmStructApplicable,
			HashMap<String, ArrayList> keyMaps, StudentMarksBean bean) {
		
		boolean isBajaj = "Bajaj".equalsIgnoreCase(consumerType);
		boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(prgmStructApplicable) && "DBM".equalsIgnoreCase(program);

		if (isBajaj && !isDBMJul2014) {
			throw new RuntimeException(bean.getSapid() + " " + bean.getSubject() + " isBajaj && !isDBMJul2014 record found.");

		} else {
			keyMaps.putAll(passFailDao.getMarksRecords(bean.getSapid(), bean.getSubject()));
		}
	}

	private void setGraceMarksAndRemarks(StudentMarksBean bean, int grace, int writtenScore, int total) {
		bean.setWritenscore("" + writtenScore);
		bean.setTotal("" + total);
		bean.setGracemarks("" + grace);
		bean.setRemarks("End of Program validity grace given");
	}

	@Override
	public List<PassFailExamBean> fetchAndApplyValidityEndGrace(String examYear, String examMonth) {

		List<GraceMarksService> listOfGraceMarks = Arrays.asList(new PGGraceFourImpl(), new PGGraceTenImpl(), new PGGraceTwelveImpl());

		return listOfGraceMarks.stream().flatMap(grace -> getApplicableStudentsAndApplyGrace(examYear, examMonth, grace).stream())
										.collect(Collectors.toList());
	}

	private List<PassFailExamBean> getApplicableStudentsAndApplyGrace(String examYear, String examMonth, GraceMarksService graceMarks) {
		ArrayList<PassFailExamBean> processNewForPassFail = new ArrayList<PassFailExamBean>();
		try {
			HashMap<String, ArrayList> toProcessPassFailMap = getMarksMapToProcessPassFail(examYear, examMonth, graceMarks);
			
			processNewForPassFail = passFailExecutorService.processNewForPassFail(toProcessPassFailMap);
			
			addRemarksAndExamMonth(examYear, examMonth, processNewForPassFail);
			
			HashMap<String, String> keysBySAPID = passFailDao.getKeysBySAPID();
			
			passFailDao.upsertPassFailRecordsBySAPID(processNewForPassFail, keysBySAPID);
			
		} catch (Exception e) {
			passFailLogger.error("{} {} Error while processing {} marks grace : {}", examMonth, examYear, graceMarks.getGrace(), e.getMessage());
		}

		return processNewForPassFail;
	}

	private void addRemarksAndExamMonth(String examYear, String examMonth,
			ArrayList<PassFailExamBean> processNewForPassFail) {

		processNewForPassFail.forEach(bean -> {
			bean.setRemarks("End of Program validity grace given");
			bean.setResultProcessedMonth(examMonth);
			bean.setResultProcessedYear(examYear);
		});
	}

	private HashMap<String, ArrayList> getMarksMapToProcessPassFail(String examYear, String examMonth, GraceMarksService graceMarks) throws NoRecordFoundException {

		HashMap<String, ArrayList> toProcessPassFailMap = new HashMap<>();

		List<PGGraceMarksDTO> validityEndGraceApplicableRecords = getValidityEndGraceApplicableRecords(examYear, examMonth, graceMarks.getGrace());
		
		validityEndGraceApplicableRecords.forEach(record -> {
			try {

				filterCopyCaseGraceConditionRecords(examYear, examMonth, graceMarks, toProcessPassFailMap, record);

			} catch (Exception e) {
				passFailLogger.error("{} error trying to update marks for record! : {}", record.getSapid(),e.getMessage());
			}
		});
		
		if(toProcessPassFailMap.isEmpty())
			throw new NoRecordFoundException("0 records to process passfail after copy case filter and marks update conditions");
		
		return toProcessPassFailMap;
	}

	private void filterCopyCaseGraceConditionRecords(String examYear, String examMonth, GraceMarksService graceMarks,
			HashMap<String, ArrayList> toProcessPassFailMap, PGGraceMarksDTO record) throws NoRecordFoundException {
		
		if (COPY_CASE_STR.equalsIgnoreCase(record.getRemarks())) {
			// not to give grace marks to copy case students
			passFailLogger.info("{} copy case found!", record.getSapid());
		} else if (graceMarks.satisfiesCondition(record)) {
			//satisfies grace marks conditions defined in the implementations of grace marks interface
			toProcessPassFailMap.putAll(updateMarksAndReturnMarksRecords(record.getGracemarks(),record.getProgram(), record.getSapid(), record.getConsumerType(), 
					examYear, examMonth,record.getPrgmStructApplicable()));
		} else {
			//not copy case records but also didn't satisfy grace marks condtions records
			passFailLogger.info("{} condition not satisfied for {} grace marks, DTO : {}", record.getSapid(),graceMarks.getGrace(), record);
		}
	}

}
