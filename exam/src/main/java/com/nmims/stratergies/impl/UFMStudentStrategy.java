package com.nmims.stratergies.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.stratergies.UFMStudentStrategyInterface;

@Service("ufmStudentStrategy")
public class UFMStudentStrategy implements UFMStudentStrategyInterface {

	@Autowired
	UFMNoticeDAO dao;
	
	public static final Logger ufm = LoggerFactory.getLogger("ufm");
	
	//Added by shivam.pandey.EXT
	private static final String DISCONNECT_BELOW_15_MINUTES = "DisconnectBelow15Min";
	//Added by shivam.pandey.EXT
	
	@Override
	public List<UFMNoticeBean> getListOfShowCauseSubjects(String sapid) throws Exception {
		return dao.getListOfUFMMarkedSubjectsForStudent(sapid);
	}
	
	@Override
	public boolean checkIfMarkedForCurrentCycle(String sapid) throws Exception {
		return dao.checkIfStudentMarkedForUFMInCurrentCycle(sapid);
	}
	
	@Override
	public List<UFMNoticeBean> getListOfShowCauseSubjectsForStudentYearMonth(String sapid, String year, String month) throws Exception {
		return dao.getListOfShowCauseSubjectsForStudentYearMonth(sapid, year, month);
	}

	@Override
	public void setStudentResponse(UFMNoticeBean bean) throws Exception {
		if(StringUtils.isBlank(bean.getShowCauseResponse())) {
			throw new Exception("Error! Please enter a valid response!");
		}
		String showCauseResponseDecodedToISO88591 = new String(bean.getShowCauseResponse().getBytes("UTF-8"), "ISO-8859-1");
		String showCauseResponse = showCauseResponseDecodedToISO88591.replace("\r\n", "\n");
		bean.setShowCauseResponse(showCauseResponse);
		ufm.info("Response of:"+bean.getSapid()+","+bean.getSubject()+","+bean.getMonth()+","+bean.getYear()+" is:"+bean.getShowCauseResponse());
		if(bean.getShowCauseResponse().length() > 2000) {
			ufm.info("Error in response length!! Response size is:"+bean.getShowCauseResponse().length());
			throw new Exception("Error! Response submitted is too long!");
		}
		dao.setStudentResponse(bean);
	}

	@Override
	public void setUFMStatus(UFMNoticeBean bean) throws Exception {
		switch(bean.getStage()) {
			case "Show Cause - Awaiting Student Response" : 
				
				if(!DISCONNECT_BELOW_15_MINUTES.equals(bean.getCategory()))
				{
					String showCauseDeadline = bean.getShowCauseDeadline();
					
					if(checkIfCanSubmitResponse(showCauseDeadline)) {
						bean.setStatus("Awaiting Response");
						bean.setCanSubmitResponse("Y");
					} else {
						bean.setStatus("Awaiting Decision");
						bean.setCanSubmitResponse("N");
					}
				}
				else 
				{
					bean.setStatus("Awaiting Decision");
					bean.setCanSubmitResponse("N");
				}
				
				break;
			case "Show Cause - Student Responded" : 
				bean.setStatus("Awaiting Decision");
				bean.setCanSubmitResponse("N");
				break;
			case "Penalty Issued": 
				bean.setStatus("Decision Taken");
				bean.setCanSubmitResponse("N");
				break;
			case "Warning":
				bean.setStatus("Decision Taken");
				bean.setCanSubmitResponse("N");
				break;
			default : throw new Exception("Invalid stage found!");
		}
	}
	
	private boolean checkIfCanSubmitResponse(String showCauseDeadline) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Date deadline = sdf.parse(showCauseDeadline);
		Date now = new Date();
		
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(now);

		Calendar calendarDeadline = Calendar.getInstance();
		calendarDeadline.setTime(deadline);
		return calendarNow.before(calendarDeadline);
	}
}
