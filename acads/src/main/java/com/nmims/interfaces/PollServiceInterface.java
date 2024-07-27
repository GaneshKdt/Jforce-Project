package com.nmims.interfaces;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionPollReportBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsResultsBean;


public interface PollServiceInterface
{
	WebinarPollsResultsBean getWebinarPollsResults(String meetingKey,String userId);
	
    HashMap<String,String> createWebinarPoll(String webinarId,WebinarPollsBean webinarPollsBean);
	
	List<WebinarPollsBean> getSessionPolls(String meetingKey);
	
	HashMap<String,String> updateWebinarPoll(String webinarId,WebinarPollsBean webinarPollsBean) throws Exception;
	
	HashMap<String,String> deleteWebinarPoll(String webinarId, String pollId);
	
	SessionDayTimeAcadsBean findScheduledSessionById(String sessionId);
	
	public abstract ArrayList<String> getSubjectCodeLists(String userId,String month,String year);
	
	public PageAcads<SessionPollReportBean> getSessionPollReport(int pageNo, int pageSize, SessionPollReportBean searchBean);

	
}

