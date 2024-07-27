package com.nmims.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundProjectPassFailService {
	
	/**
	 * This method is used to to get time-bound students project marks based on subjectName and timeboundId.
	 * @param subjectName - Contains the subject name for which marks has to get.
	 * @param timeboundId - Contains id for which subject delivery happens for limited period. 
	 * @return	the list of time-bound students projects marks.
	 */
	public List<TEEResultBean> getTimeboundStudentProjectMarks(String subjectName, Integer timeboundId);
	
	/**
	 * This method is used to insert or update the given students project marks. 
	 * @param studentMarksList - Contains students project marks
	 * @return It returns the list of errors.
	 */
	public List<String> upsertTimeboundStudentProjectMarks(List<TEEResultBean> studentMarksList);
	
	/**
	 * This method is used to get the program sem subject id based on the sapId and timeboundId.
	 * @param sapId - student number e.g 77777777778
	 * @param timeboundId - Contains id for which subject delivery happens for limited period. 
	 * @return program sem subject id
	 */
	public Optional<Integer> getTimeboundSubjectPSSId(String sapId, String timeboundId);
	
	/**
	 * Get time-bound eligible students project marks records for processing the project pass-fail. 
	 * @param timeboundId - Contains id for which subject delivery happens for limited period.
	 * @return the list of student project marks record. 
	 */
	public List<TEEResultBean> getEligibleStudentsForProjectPassFail(String timeboundId);
	
	/**
	 * Process pass-fail according to the business requirement for the time-bound student project marks. 
	 * @param eligibleStudentsForProjectPassFail - list of eligible students for pass-fail.
	 * @param Contains currently logged in user id on portal.
	 * @return map of successfully processed and failed to process records lists
	 */
	public Map<String,List<EmbaPassFailBean>> processTimeboundStudentsProjectPassFail (List<TEEResultBean> eligibleStudentsForProjectPassFail, String loggedInUser);
	
	/**
	 * This method is used to insert or update the given students project pass-fail records. 
	 * @param studentPassFailList - list of processed pass-fail records.
	 * @return It returns the list of errors during the insertion or updating.
	 */
	public List<String> upsertTimeboundStudentProjectPassFail(List<EmbaPassFailBean> studentPassFailList);
}
