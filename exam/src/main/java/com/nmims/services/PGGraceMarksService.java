package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nmims.beans.PassFailExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.dto.PGGraceMarksDTO;
import com.nmims.exceptions.NoRecordFoundException;

/**
 * PG Grace marks service interface to apply TEE grace marks
 * 
 * @author Swarup Singh Rajpurohit
 */
public interface PGGraceMarksService {

	/**
	 * Fetches Validity end grace applicable students from passfail.
	 * 
	 * @param examYear
	 * @param examMonth
	 * @param grace
	 * @return Validity End Eligible Records
	 */
	List<PGGraceMarksDTO> getValidityEndGraceApplicableRecords(String examYear, String examMonth, int grace);

	/**
	 * Based on Input Params </br>
	 * 1)fetches subjects and applies validity end grace marks to required subjects.</br>
	 * 2)adds grace marks </br>
	 * 3)updates marks of students </br>
	 * 4)runs processNew for all subjects </br>
	 * 5)adds to the hashmap and returns to update passfail. </br>
	 * 
	 * @param totalGracemarks
	 * @param program
	 * @param sapid
	 * @param studentType
	 * @param resultProcessedYear
	 * @param resultProcessedMonth
	 * @param prgmStructApplicable
	 * @return passfail records
	 * @throws NoRecordFoundException 
	 */
	HashMap<String, ArrayList> updateMarksAndReturnMarksRecords(String totalGracemarks, String program,
			String sapid, String studentType, String resultProcessedYear, String resultProcessedMonth,
			String prgmStructApplicable) throws NoRecordFoundException;

	/**
	 * Fetches and Applies grace marks, internally uses
	 * {@link PGGraceMarksService#getValidityEndGraceApplicableRecords(String, String, int)
	 * getValidityEndGraceApplicableRecords},
	 * {@link PGGraceMarksService#updateMarksAndReturnMarksRecords(String, String, String, String, String, String, String)
	 * updateMarksAndRetrieveToPassFailRecords} and
	 * {@link PassFailDAO#upsertPassFailRecordsBySAPID(ArrayList, HashMap)
	 * upsertPassFailRecordsBySAPID}
	 * 
	 * @param examYear
	 * @param examMonth
	 * @return list of validity end applied records
	 */
	List<PassFailExamBean> fetchAndApplyValidityEndGrace(String examYear, String examMonth);

}
