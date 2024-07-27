/**
 * 
 */
package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public interface ResultsDisplayStrategyInterface {

	public Boolean changeResultsLiveState(RemarksGradeBean bean, String userId);
	
	public List<RemarksGradeBean> searchResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean, final Boolean countRequired);
	
	public List<RemarksGradeBean> downloadResultsAsPassFailReport(final RemarksGradeBean remarksGradeBean);
}
