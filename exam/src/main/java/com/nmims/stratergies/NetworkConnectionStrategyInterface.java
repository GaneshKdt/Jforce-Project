package com.nmims.stratergies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;

public interface NetworkConnectionStrategyInterface {

	ArrayList<LostFocusLogExamBean> performNetworkConnectionCheck( ArrayList<LostFocusLogExamBean> studentListForCopyCase, 
			List<ErrorAnalyticsBean> errorAnalytics, HashMap<String, List<NetworkLogsExamBean>> networkLogList, 
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs );
	
	Boolean perfromAnswerUpdatedAfterLostFocusCheck( LostFocusLogExamBean bean, 
			HashMap<String, List<NetworkLogsExamBean>> networkLogList, 
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs, String testEndedStatus );
	
	Boolean perfromDurationCheckForIndividualStudentLogs( HashMap<String, ArrayList<LostFocusLogExamBean>> individualLogs, 
			LostFocusLogExamBean accumulateLostFocus, int durationCheck);
	
	Boolean checkErrorLogForStudentAndTest( List<ErrorAnalyticsBean> errorAnalytics, LostFocusLogExamBean lostFocus,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs );
	
}
