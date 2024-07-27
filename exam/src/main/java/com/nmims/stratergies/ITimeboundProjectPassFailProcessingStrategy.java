package com.nmims.stratergies;

import java.util.List;
import java.util.Map;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.TEEResultBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundProjectPassFailProcessingStrategy {
	
	/**
	 * Process pass-fail according to the business requirement for the time-bound student project marks. 
	 * @param eligibleStudentsForProjectPassFail - list of eligible students for pass-fail.
	 * @param Contains currently logged in user id on portal.
	 * @return map of successfully processed and failed to process records lists
	 */
	public Map<String,List<EmbaPassFailBean>> processTimeboundStudentsProjectPassFail (List<TEEResultBean> eligibleStudentsForProjectPassFail, String loggedInUser);

}
