/**
 * 
 */
package com.nmims.interfaces;

import java.util.List;

import com.nmims.beans.RemarksGradeResultsDTO;

/**
 * @author vil_m
 *
 */
public interface GradingTypeResultsServiceInterface {
	
	public List<RemarksGradeResultsDTO> fetchStudentsResult(String sapid,
			Integer resultLive, String status, Integer statusPassFail, String activeFlag);
	
	public boolean checkResultsAvailable(String sapid, Integer resultLive,
			String status, Integer statusPassFail, Integer processedFlag, String activeFlag);
	
}
