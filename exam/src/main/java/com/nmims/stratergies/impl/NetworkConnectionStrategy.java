package com.nmims.stratergies.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.NetworkLogsExamBean;
import com.nmims.stratergies.NetworkConnectionStrategyInterface;

@Service
public class NetworkConnectionStrategy implements NetworkConnectionStrategyInterface {

	final int durationCheckForNetworkConnection = 60;
	final int durationCheckForAnswerUpdate = 5;
	
	@Override
	public ArrayList<LostFocusLogExamBean> performNetworkConnectionCheck(ArrayList<LostFocusLogExamBean> studentListForCopyCase,
			List<ErrorAnalyticsBean> errorAnalytics, HashMap<String, List<NetworkLogsExamBean>> networkLogList,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs) {

		ArrayList<LostFocusLogExamBean> studentToMarkCopyCase = new ArrayList<LostFocusLogExamBean>();
		Pattern networkPattern = Pattern.compile("(?=.*connect|net|load)", Pattern.CASE_INSENSITIVE );
		Pattern errorPattern = Pattern.compile("(?=.*wasOfflineAt|readyState)", Pattern.CASE_INSENSITIVE );

		for(LostFocusLogExamBean bean : studentListForCopyCase) {

			if( !StringUtils.isBlank( bean.getReason() )) {
				
				if( networkPattern.matcher( bean.getReason() ).find() ) {

					Boolean answerUpdated = perfromAnswerUpdatedAfterLostFocusCheck( bean, networkLogList, individualStudentLostFocusLogs,
							bean.getTestEndedStatus());
					
					Boolean durationCheck = perfromDurationCheckForIndividualStudentLogs( individualStudentLostFocusLogs, bean, 
							durationCheckForNetworkConnection );
					
					if( checkErrorLogForStudentAndTest( errorAnalytics, bean, individualStudentLostFocusLogs ) ) {

						for( ErrorAnalyticsBean erroBean : errorAnalytics ) {
	
							if( bean.getSapid().equals( erroBean.getSapid() ) ) {
	
								if( !errorPattern.matcher( erroBean.getStackTrace() ).find() && answerUpdated ) {

									if( !studentToMarkCopyCase.contains(bean) ) {
										studentToMarkCopyCase.add( bean );
									}
	
								}else if ( errorPattern.matcher( erroBean.getStackTrace() ).find() && answerUpdated && durationCheck ) {

									if( !studentToMarkCopyCase.contains(bean) ) {
										studentToMarkCopyCase.add( bean );
									}
									
								}
							}
						}
						
					}else if ( durationCheck ){

						if( !studentToMarkCopyCase.contains(bean) && answerUpdated) {
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

	@Override
	public Boolean checkErrorLogForStudentAndTest(List<ErrorAnalyticsBean> errorAnalytics, LostFocusLogExamBean lostFocus,
			HashMap<String, ArrayList<LostFocusLogExamBean>> individualStudentLostFocusLogs) {

		Calendar beforeTime = Calendar.getInstance();
		Calendar afterTime = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date date = new Date(); 
		ArrayList<LostFocusLogExamBean> lostFocusList = individualStudentLostFocusLogs.get( lostFocus.getSapid() );
		
		for(ErrorAnalyticsBean error : errorAnalytics) {

			for( LostFocusLogExamBean lostFocusBean: lostFocusList) {
				
				try {
					date = formater.parse( error.getCreatedOn() );
				} catch (ParseException e) {
					
				}
				
				beforeTime.setTime(date);
				beforeTime.add(Calendar.MINUTE, -1);
				
				afterTime.setTime(date);
				afterTime.add(Calendar.MINUTE, 1);
				
				if( error.getSapid().equals( lostFocusBean.getSapid() ) &&
						lostFocusBean.getCreatedDate().after( beforeTime.getTime() ) && 
						lostFocusBean.getCreatedDate().before( afterTime.getTime() )) {
					
					return true;
					
				}
			}
	    }
		
	    return false;
	    
	}

}
