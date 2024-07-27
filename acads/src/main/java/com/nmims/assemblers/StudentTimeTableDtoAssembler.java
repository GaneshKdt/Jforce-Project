package com.nmims.assemblers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeToken;

import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.TimeTableDAO;
import com.nmims.dto.StudentTimeTableDto;



public class StudentTimeTableDtoAssembler {
	
	public static ArrayList<StudentTimeTableDto> transferSessionBeanToDto(ArrayList<SessionDayTimeAcadsBean> sessionList){
		ArrayList<StudentTimeTableDto> scheduledSessionList = new ArrayList<StudentTimeTableDto>();
		
//		ModelMapper modelMapper = new ModelMapper();
//		 java.lang.reflect.Type targetListType = new TypeToken<List<StudentTimeTableDto>>() {}.getType();
//		 scheduledSessionList.addAll(modelMapper.map(sessionList, targetListType));
		try {
			sessionList.forEach(session -> {
				StudentTimeTableDto sessionDto = new StudentTimeTableDto();
				sessionDto.setId(session.getId());
				sessionDto.setPrgmSemSubId(session.getPrgmSemSubId());
				sessionDto.setDate(session.getDate());
				sessionDto.setStartTime(session.getStartTime());
				sessionDto.setDay(session.getDay());
				sessionDto.setSubject(session.getSubject());
				sessionDto.setSessionName(session.getSessionName());
				sessionDto.setMonth(session.getMonth());
				sessionDto.setYear(session.getYear());
				sessionDto.setFacultyId(session.getFacultyId());
				sessionDto.setEndTime(session.getEndTime());
				sessionDto.setTrack(session.getTrack());
				sessionDto.setFirstName(session.getFirstName());
				sessionDto.setLastName(session.getLastName());
				sessionDto.setIsCancelled(session.getIsCancelled());
				sessionDto.setReasonForCancellation(session.getReasonForCancellation());
				sessionDto.setFacultyName(session.getFacultyName());
				scheduledSessionList.add(sessionDto);
			});			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return scheduledSessionList;
	}
	
	public static ArrayList<StudentTimeTableDto> transferSessionTimelineBeanToDto(ArrayList<SessionDayTimeAcadsBean> sessionList,String sapid,TimeTableDAO tDao){
		ArrayList<StudentTimeTableDto> scheduledSessionList = new ArrayList<StudentTimeTableDto>();
		HashMap<String, String> getStudentSessionMap = tDao.getAttendanceForSessionMap(sapid);
		try {
			sessionList.forEach(session -> {
				StudentTimeTableDto sessionDto = new StudentTimeTableDto();
				sessionDto.setId(session.getId());
				sessionDto.setPrgmSemSubId(session.getPrgmSemSubId());
				sessionDto.setDate(session.getDate());
				sessionDto.setStartTime(session.getStartTime());
				sessionDto.setDay(session.getDay());
				sessionDto.setSubject(session.getSubject());
				sessionDto.setSessionName(session.getSessionName());
				sessionDto.setMonth(session.getMonth());
				sessionDto.setYear(session.getYear());
				sessionDto.setFacultyId(session.getFacultyId());
				sessionDto.setEndTime(session.getEndTime());
				sessionDto.setTrack(session.getTrack());
				sessionDto.setFirstName(session.getFirstName());
				sessionDto.setLastName(session.getLastName());
				sessionDto.setIsCancelled(session.getIsCancelled());
				sessionDto.setReasonForCancellation(session.getReasonForCancellation());
				sessionDto.setFacultyName(session.getFacultyName());
				String key = sapid + " - "+session.getId();
				if (getStudentSessionMap.containsKey(key)) {
					sessionDto.setAttended("Yes");
				}else{
					sessionDto.setAttended("No");
				}
				scheduledSessionList.add(sessionDto);
			});			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return scheduledSessionList;
	}
}