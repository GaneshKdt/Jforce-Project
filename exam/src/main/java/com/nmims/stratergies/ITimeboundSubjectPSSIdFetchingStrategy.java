package com.nmims.stratergies;

import java.util.Optional;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ITimeboundSubjectPSSIdFetchingStrategy {
	
	/**
	 * This method is used to get the program sem subject id based on the sapId and timeboundId.
	 * @param sapId - student number e.g 77777777778
	 * @param timeboundId - Contains id for which subject delivery happens for limited period. 
	 * @return program sem subject id
	 */
	public Optional<Integer> getTimeboundSubjectPSSId(String sapId, String timeboundId);
}
