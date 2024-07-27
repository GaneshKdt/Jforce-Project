package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundStudentProjectMarksFetchingStrategy {
	
	/**
	 * This method is used to to get time-bound students project marks based subjectName and timeboundId.
	 * @param subjectName - Contains the subject name for which marks has to get.
	 * @param timeboundId - Contains id for which subject  happens for limited mount of time. 
	 * @return	the list of time-bound students projects marks.
	 */
	List<TEEResultBean> getTimeboundStudentProjectMarks(String subjectName, Integer timeboundId);
}
