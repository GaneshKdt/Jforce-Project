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
import com.nmims.stratergies.MiscellaneousReasonStrategyInterface;

@Service
public class MiscellaneousReasonStrategy implements MiscellaneousReasonStrategyInterface {

	final int durationCheckForMiscellaneous = 30;
	final int durationCheckForAnswerUpdate = 5;
	
	@Override
	public ArrayList<LostFocusLogExamBean> performMiscellaneousCheck(ArrayList<LostFocusLogExamBean> studentListForCopyCase,
			HashMap<String, List<NetworkLogsExamBean>> networkLogList,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs) {

		ArrayList<LostFocusLogExamBean> studentToMarkCopyCase = new ArrayList<LostFocusLogExamBean>();
		Pattern miscellaneousPattern = Pattern.compile("(?=.*underline|mistake|pop|message|keyboard|call|click|warning|mouse|shortcut|minimize)", 
				Pattern.CASE_INSENSITIVE );
		
		for(LostFocusLogExamBean bean : studentListForCopyCase) {

			if( !StringUtils.isBlank( bean.getReason() )) {

				
				if( miscellaneousPattern.matcher( bean.getReason() ).find() ) {

					Boolean answerUpdated = perfromAnswerUpdatedAfterLostFocusCheck( bean, networkLogList, individualStudentLostFocusLogs,
							bean.getTestEndedStatus());
					Boolean durationCheck = perfromDurationCheckForIndividualStudentLogs( individualStudentLostFocusLogs, bean, 
							durationCheckForMiscellaneous );

					if( durationCheck && answerUpdated  ) {

						if( !studentToMarkCopyCase.contains(bean) ) {
							studentToMarkCopyCase.add( bean );
						}
					}
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

	@Override
	public Boolean perfromDurationCheckForIndividualStudentLogs(
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualLogs, LostFocusLogExamBean accumulateLostFocus,
			int durationCheck) {

		ArrayList<LostFocusLogExamBean> logList = individualLogs.get( accumulateLostFocus.getSapid() );
		
		for( LostFocusLogExamBean bean : logList ) {
			
			if( bean.getTimeAwayInSecs().compareTo(BigInteger.valueOf( durationCheck )) > 0 )
				return true;
			
		}
		
		return false;
		
	}

	
}
