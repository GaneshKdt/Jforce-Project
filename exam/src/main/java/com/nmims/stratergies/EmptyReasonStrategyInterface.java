package com.nmims.stratergies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;

public interface EmptyReasonStrategyInterface {

	ArrayList<LostFocusLogExamBean> performEmptyReasonCheck( ArrayList<LostFocusLogExamBean> studentListForCopyCase, 
			HashMap<String, List<NetworkLogsExamBean>> networkLogList, HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs);
	
	Boolean perfromAnswerUpdatedAfterLostFocusCheck( LostFocusLogExamBean bean, 
			HashMap<String, List<NetworkLogsExamBean>> networkLogList, 
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs, String testEndedStatus );
	
}
