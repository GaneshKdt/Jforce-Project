package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundEligibleStudentsForProjectPassFailStrategy {

	/**
	 * Get time-bound eligible students project marks records for processing the project pass-fail. 
	 * @param timeboundId - Contains id for which subject delivery happens for limited period.
	 * @return the list of student project marks record. 
	 */
	List<TEEResultBean> getEligibleStudentsForProjectPassFail(String timeboundId);
}
