package com.nmims.stratergies.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;
import com.nmims.stratergies.EmptyReasonStrategyInterface;

@Service
public class EmptyReasonStrategy implements EmptyReasonStrategyInterface {

	final int durationCheckForEmptyReason = 60;
	final int durationCheckForAnswerUpdate = 5;
	
	@Override
	public ArrayList<LostFocusLogExamBean> performEmptyReasonCheck(ArrayList<LostFocusLogExamBean> studentListForCopyCase,
			HashMap<String, List<NetworkLogsExamBean>> networkLogList,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs) {

		ArrayList<LostFocusLogExamBean> studentToMarkCopyCase = new ArrayList<LostFocusLogExamBean>();

		for(LostFocusLogExamBean bean : studentListForCopyCase) {

			Boolean answerUpdated = perfromAnswerUpdatedAfterLostFocusCheck( bean, networkLogList, individualStudentLostFocusLogs,
					bean.getTestEndedStatus() );

			if( StringUtils.isBlank( bean.getReason()) && answerUpdated && 
					bean.getTimeAwayInSecs().compareTo(BigInteger.valueOf(durationCheckForEmptyReason)) > 0) {

				if( !studentToMarkCopyCase.contains(bean) ) {
					studentToMarkCopyCase.add( bean );
				}
			}
		}

		return studentToMarkCopyCase;
		
	}
	
	@Override
	public Boolean perfromAnswerUpdatedAfterLostFocusCheck(LostFocusLogExamBean bean,
			HashMap<String, List<NetworkLogsExamBean>> networkLogList,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs, String testEndedStatus) {
		
		List<NetworkLogsExamBean> networkLog = networkLogList.get( bean.getSapid() );
		Calendar networkLogInstance = Calendar.getInstance();
		Boolean status = false;
		ArrayList<LostFocusLogExamBean> lostFocusListForSapid = individualStudentLostFocusLogs.get(  bean.getSapid() );
		
		Pattern networkLogsPattern = Pattern.compile("(?=.*saveDQAnswerInCache|addStudentsQuestionResponse)", Pattern.CASE_INSENSITIVE );
		
		networkLogLoop:
		for( NetworkLogsExamBean networkLogs : networkLog) {
			
			if( networkLogsPattern.matcher( networkLogs.getName() ).find() ) {
				
				/* time at which network logs were captured */
				networkLogInstance.setTimeInMillis( Long.parseLong( networkLogs.getCreated_at() ) );

				for( LostFocusLogExamBean lostFocusBean : lostFocusListForSapid ) {

					if( networkLogInstance.getTime().after( lostFocusBean.getCreatedDate() ) && !"Auto Submitted".equals(testEndedStatus) &&
							bean.getTimeAwayInSecs().compareTo(BigInteger.valueOf(durationCheckForAnswerUpdate)) > 0 ) {
						
						status = true;
						break networkLogLoop;
						
					}
					
				}
			}
		}
		
		return status;
		
	}
	
}
