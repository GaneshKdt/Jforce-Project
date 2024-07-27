/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.RemarksGradeResultsBean;

/**
 * @author vil_m
 *
 */
public interface RemarkResultsDisplayStrategyInterface {
	
	public List<RemarksGradeResultsBean> fetchStudentsResult(final String sapid,
			final Integer resultLive, final String status, final Integer statusPassFail,
			final String activeFlag);
	
	public boolean checkResultsAvailable(final String sapid, final Integer resultLive,
			final String status, final Integer statusPassFail, final Integer processedFlag, final String activeFlag);
}
