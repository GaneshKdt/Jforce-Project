package com.nmims.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ServletConfigAware;

import bookingservice.wsdl.BandwidthOverride;
import bookingservice.wsdl.Conference;
import bookingservice.wsdl.ConferenceType;

import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.ConferenceDAO;
import com.nmims.helpers.ConferenceBookingClient;

public class ConferenceBookingScheduler implements ApplicationContextAware, ServletConfigAware {

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	@Autowired
	private ConferenceDAO conferenceBookingDAO;
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;

	@Override
	public void setServletConfig(ServletConfig config) {
		sc = config;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		act = context;
	}

	//@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void bookTMSConference() {
		List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingConferenceList();

		if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {
			BandwidthOverride bandwidth = new BandwidthOverride();
			bandwidth.setBandwidth("1b/64kbps");
			List<Conference> conferenceWSDLList = new ArrayList<>(pendingConferenceList.size());
			for (SessionDayTimeAcadsBean conferenceBean : pendingConferenceList) {
				Conference conferenceWSDL = new Conference();
				conferenceWSDL.setConferenceId(-1);
				conferenceWSDL.setOwnerId(10);
				conferenceWSDL.setConferenceType(ConferenceType.RESERVATION_ONLY);
				conferenceWSDL.setBandwidth("1b/64kbps");
				conferenceWSDL.setISDNBandwidth(bandwidth);
				conferenceWSDL.setIPBandwidth(bandwidth);

				conferenceWSDL.setTitle(conferenceBean.getSessionName()+"-"+conferenceBean.getSessionName());
				conferenceWSDL.setStartTimeUTC(conferenceBean.getDate()+" "+conferenceBean.getStartTime());
				conferenceWSDL.setEndTimeUTC(conferenceBean.getDate()+" "+conferenceBean.getEndTime());
				
				/*Conference response = conferenceBookingClient.saveConference(conferenceWSDL);
				conferenceBean.setStatus("B");
				conferenceBean.setTmsConfId(response.getConferenceId());
				conferenceBean.setTmsConfLink(response.getWebConferenceAttendeeUri());*/
				conferenceWSDLList.add(conferenceWSDL);
			}

			List<Conference> conferenceResponse = conferenceBookingClient.saveConferences(conferenceWSDLList);
			for (Iterator confIterator = conferenceResponse.iterator(), beanIterator = pendingConferenceList.iterator(); confIterator
					.hasNext() && beanIterator.hasNext();) {
				Conference conference = (Conference) confIterator.next();
				SessionDayTimeAcadsBean conferenceBean = (SessionDayTimeAcadsBean) beanIterator.next();
				conferenceBean.setCiscoStatus("B");
				conferenceBean.setTmsConfId(conference.getConferenceId());
				conferenceBean.setTmsConfLink(conference.getWebConferenceAttendeeUri());
				if( null != conference.getExternalConference() && null != conference.getExternalConference().getWebEx() ) {
					conferenceBean.setMeetingKey(conference.getExternalConference().getWebEx().getMeetingKey());
					conferenceBean.setMeetingPwd(conference.getExternalConference().getWebEx().getMeetingPassword());
					conferenceBean.setJoinUrl(conference.getExternalConference().getWebEx().getJoinMeetingUrl());
					conferenceBean.setHostUrl(conference.getExternalConference().getWebEx().getHostMeetingUrl());
					conferenceBean.setHostKey(conference.getExternalConference().getWebEx().getHostKey());
					if(null != conference.getExternalConference().getWebEx().getTelephony()){
						conferenceBean.setLocalTollNumber(conference.getExternalConference().getWebEx().getTelephony().getLocalCallInTollNumber());
						conferenceBean.setLocalTollFree(conference.getExternalConference().getWebEx().getTelephony().getLocalCallInTollFreeNumber());
						conferenceBean.setGlobalCallNumber(conference.getExternalConference().getWebEx().getTelephony().getGlobalCallInNumberUrl());
						conferenceBean.setPstnDialNumber(conference.getExternalConference().getWebEx().getTelephony().getPstnDialInNumber());
						conferenceBean.setParticipantCode(conference.getExternalConference().getWebEx().getTelephony().getParticipantAccessCode());
					}
					
				}
				
			}

			int[] result = conferenceBookingDAO.updateBookedConference(pendingConferenceList);
		}

	}

}
