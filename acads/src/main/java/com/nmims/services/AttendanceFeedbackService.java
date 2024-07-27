package com.nmims.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionCountBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.util.ContentUtil;

@Service
public class AttendanceFeedbackService {

	@Autowired
	private AttendanceFeedbackDAO attendanceFeedbackDAO;
	

	private ArrayList<String> commmonSessionList = new ArrayList<String>(Arrays.asList("Orientation", "Assignment"));
	
	public PageAcads<SessionAttendanceFeedbackAcads> getAttendanceDetails(SessionAttendanceFeedbackAcads searchBean,  String CURRENT_ACAD_YEAR,  String CURRENT_ACAD_MONTH, String centerCode) {
		PageAcads<SessionAttendanceFeedbackAcads> page=new PageAcads<>();

		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
				|| (searchBean.getYear().equalsIgnoreCase("2022") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr") || searchBean.getMonth().equalsIgnoreCase("Oct")))
				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr") || searchBean.getMonth().equalsIgnoreCase("Oct")))
				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Apr") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
			page = attendanceFeedbackDAO.getAttendance(1, Integer.MAX_VALUE, searchBean, centerCode);
		}else {
			page = attendanceFeedbackDAO.getAttendanceFromHistory(1, Integer.MAX_VALUE, searchBean, centerCode);
		}
		return page;
	}
	
	public List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverage(SessionAttendanceFeedbackAcads searchBean){
		List<SessionAttendanceFeedbackAcads>	facultyFeedbackList=attendanceFeedbackDAO.getSubjectFacultyWiseAverage(searchBean);
		return	facultyFeedbackList;
	}
	
	public List<SessionAttendanceFeedbackAcads> getListOfSubjectFacultyWiseFeedbackAverage(SessionAttendanceFeedbackAcads searchBean, String CURRENT_ACAD_YEAR, String CURRENT_ACAD_MONTH, HttpServletRequest request){
		
		List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverageList = new ArrayList<SessionAttendanceFeedbackAcads>();
	
		// changing month for Oct & Apr cycle
		searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
		searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
		try {
			String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			Date historyDate;

			date = formatter.parse(acadDateFormat);
			historyDate = formatter.parse("2020-07-01");

			if (date.compareTo(historyDate) >= 0) {
				getSubjectFacultyWiseAverageList = attendanceFeedbackDAO.getSubjectFacultyWiseAverage(searchBean);

			} else {
				getSubjectFacultyWiseAverageList = attendanceFeedbackDAO
						.getSubjectFacultyWiseAverageFromHistory(searchBean);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return getSubjectFacultyWiseAverageList;
	}
	
	public HashMap<String, SessionAttendanceFeedbackAcads> getMapOfSubjectFacultySessionWiseAverage(SessionAttendanceFeedbackAcads searchBean, String CURRENT_ACAD_YEAR, String CURRENT_ACAD_MONTH, HttpServletRequest request){
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = new HashMap<String, SessionAttendanceFeedbackAcads>();
		
		// changing month for Oct & Apr cycle
		searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
		searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
		try {
			String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			Date historyDate;

			date = formatter.parse(acadDateFormat);
			historyDate = formatter.parse("2020-07-01");

			if (date.compareTo(historyDate) >= 0) {
				mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO
						.getMapOfSubjectFacultySessionWiseAverage(searchBean, request);
			} else {
				mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO
						.getMapOfSubjectFacultySessionWiseAverageFromHistory(searchBean, request);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return mapOfSubjectFacultySessionWiseAverage;
	}
	

	public HashMap<String, SessionAttendanceFeedbackAcads> getMapOfSessionIdAndFeedBackBean(SessionAttendanceFeedbackAcads searchBean, String CURRENT_ACAD_YEAR, String CURRENT_ACAD_MONTH, HttpServletRequest request){
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionIdAndFeedBackBean = new HashMap<String, SessionAttendanceFeedbackAcads>();

		// changing month for Oct & Apr cycle
		searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
		searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
		try {
			String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			Date historyDate;

			date = formatter.parse(acadDateFormat);
			historyDate = formatter.parse("2020-07-01");

			if (date.compareTo(historyDate) >= 0) {
				mapOfSessionIdAndFeedBackBean = attendanceFeedbackDAO.getMapOfSessionIdAndFeedBackBean();
			} else {
				mapOfSessionIdAndFeedBackBean = attendanceFeedbackDAO.getMapOfSessionIdAndFeedBackBean();
			}
		} catch (Exception e) {
			// TODO: handle exception

		}
		return mapOfSessionIdAndFeedBackBean;
	}

	

	public HashMap<String, SessionAttendanceFeedbackAcads> getMapOfSessionWiseAttendaceCount(SessionAttendanceFeedbackAcads searchBean, ArrayList<SessionAttendanceFeedbackAcads> sessionList,  ArrayList<String> liveSessionMasterKeyList){

		ArrayList<SessionAttendanceFeedbackAcads>  sessionIdFilteredList =new ArrayList<>();
		sessionIdFilteredList=removeDuplicateSessionId(sessionList);
		
		//Map of students applicable and attended students count for session
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionWiseAttendaceCount=new HashMap<>();

		
		//map Of Subject Sem And MasterKey
		HashMap<String, Integer> mapOfSubjectSemAndMasterKey=new HashMap<>();
		
		//map of sessionId and Attendance Count
		HashMap<String, Integer> mapOfSessionIdAndAttendaceCount=new HashMap<>();
		
		//map Of PG students Count Before July21 For MasterKey And Sem
		HashMap<String, Integer> mapOfPGCountBeforeJuly21ForMasterKeyAndSem=new HashMap<>();
		
		//map Of NonPG students Count For MasterKey And Sem
		HashMap<String, Integer> mapOfNonPGCountBeforeForMasterKeyAndSem=new HashMap<>();
		
		try {
		mapOfSubjectSemAndMasterKey=attendanceFeedbackDAO.getMapOfSubjectSemAndMasterKey();
		mapOfSessionIdAndAttendaceCount=attendanceFeedbackDAO.getMapOfSessionIdAndAttendaceCount(searchBean.getYear(), searchBean.getMonth());
		mapOfPGCountBeforeJuly21ForMasterKeyAndSem=attendanceFeedbackDAO.getMapOfPGCountBeforeJul21ForMasterKeyAndSem(searchBean.getYear(), searchBean.getMonth());
		mapOfNonPGCountBeforeForMasterKeyAndSem=attendanceFeedbackDAO.getMapOfNonPGCountBeforeJul21ForMasterKeyAndSem(searchBean.getYear(), searchBean.getMonth(),liveSessionMasterKeyList);

		/* Iterating all sessionList */
		for(SessionAttendanceFeedbackAcads feedback:sessionIdFilteredList) {		
			int applicableStudentsCountForSession=0;
			int attendedStudentsCountForSession=0;

			if(commmonSessionList.contains(feedback.getSubject())) {
				//for common session students count

				String programs=feedback.getProgramList();
				ArrayList<String> programsList = new ArrayList<String>(Arrays.asList(programs.split(",")));
				
				//applicable count of student for session
				applicableStudentsCountForSession=attendanceFeedbackDAO.getCommonSessionApplicableStudentsCount(programsList, feedback.getSem(), searchBean.getYear(), searchBean.getMonth());
				//attendance count for session
				attendedStudentsCountForSession=mapOfSessionIdAndAttendaceCount.get(feedback.getSessionId());
				
				SessionAttendanceFeedbackAcads bean1=new SessionAttendanceFeedbackAcads();
				bean1.setApplicableStudentsForSession(applicableStudentsCountForSession);
				bean1.setAttendedStudentForSession(attendedStudentsCountForSession);
				mapOfSessionWiseAttendaceCount.put(feedback.getSessionId(), bean1);
				
			}else {
			//list Of Sem For MasterKeys
			ArrayList<SessionCountBean> listOfSemForMasterKeys=new ArrayList<>();
			
			//list of masterkeys applicable for sessionId
			ArrayList<String> masterkeyListForSession=attendanceFeedbackDAO.getMasterKeyListForSessionId(feedback.getSessionId());


			int countOfRegisteredTimeBoundStudentForSession=0;
			int countOfRegisteredPGStudentForSessionAfterJul21=0;
			int countOfRegisteredPGStudentForSessionBeforeJul21=0;
			int countOfRegisteredNonPGStudentForSession=0;

			if("Y".equals(searchBean.getHasModuleId())) {
				//for MBA-WX students
				
				countOfRegisteredTimeBoundStudentForSession=attendanceFeedbackDAO.getRegisteredTimeBoundStudentForSession(feedback.getSessionId());
				
				//applicable count of student for session
				applicableStudentsCountForSession=countOfRegisteredTimeBoundStudentForSession;
				//attendance count for session
				attendedStudentsCountForSession=mapOfSessionIdAndAttendaceCount.get(feedback.getSessionId());
				
				}else if ("N".equals(searchBean.getHasModuleId())) { //all students except MBA-WX
					
					//for PGStudentAndNnPGStudentsBeoreJul21
					for(String consumerProgramStructureId:masterkeyListForSession) {
							int sem=0;
							if(mapOfSubjectSemAndMasterKey.containsKey(consumerProgramStructureId+"-"+feedback.getSubject())) {
								sem=mapOfSubjectSemAndMasterKey.get(consumerProgramStructureId+"-"+feedback.getSubject());
								}
							SessionCountBean bean=new SessionCountBean();
							bean.setSem(sem);
							bean.setConsumerProgramStructureId(consumerProgramStructureId);
							listOfSemForMasterKeys.add(bean);
						}
						
						//Student count for non PG and PG students before Jul21
						for(SessionCountBean bean: listOfSemForMasterKeys) {
							int pgCountForMasterKeyAndSem=0;
							int nonPGCountForMasterKeyAndSem=0;
							
							if(mapOfPGCountBeforeJuly21ForMasterKeyAndSem.containsKey(bean.getConsumerProgramStructureId()+"-"+bean.getSem())) {
								pgCountForMasterKeyAndSem=mapOfPGCountBeforeJuly21ForMasterKeyAndSem.get(bean.getConsumerProgramStructureId()+"-"+bean.getSem());
							}
							
							if(mapOfNonPGCountBeforeForMasterKeyAndSem.containsKey(bean.getConsumerProgramStructureId()+"-"+bean.getSem())) {
								nonPGCountForMasterKeyAndSem=mapOfNonPGCountBeforeForMasterKeyAndSem.get(bean.getConsumerProgramStructureId()+"-"+bean.getSem());
							}
							countOfRegisteredPGStudentForSessionBeforeJul21+=pgCountForMasterKeyAndSem;
							countOfRegisteredNonPGStudentForSession+=nonPGCountForMasterKeyAndSem;
						}
						//for PGSTudentAndNnPGStudentsBeoreJul21 end
					
					countOfRegisteredPGStudentForSessionAfterJul21=attendanceFeedbackDAO.getRegisteredPGStudentForSessionAfterJul21(feedback.getSessionId(), searchBean.getMonth(), searchBean.getYear());
					
					//applicable count of student for session
					applicableStudentsCountForSession=countOfRegisteredPGStudentForSessionAfterJul21+countOfRegisteredPGStudentForSessionBeforeJul21+countOfRegisteredNonPGStudentForSession;
					
					//attendance count for session
					attendedStudentsCountForSession=mapOfSessionIdAndAttendaceCount.get(feedback.getSessionId());
					
				}else { //for all students and 
					//TimeBound students count
					countOfRegisteredTimeBoundStudentForSession=attendanceFeedbackDAO.getRegisteredTimeBoundStudentForSession(feedback.getSessionId());
					
					//students count after Jul21
					countOfRegisteredPGStudentForSessionAfterJul21=attendanceFeedbackDAO.getRegisteredPGStudentForSessionAfterJul21(feedback.getSessionId(), searchBean.getMonth(), searchBean.getYear());
					
					//for PGStudentAndNnPGStudentsBeoreJul21
					for(String consumerProgramStructureId:masterkeyListForSession) {
							int sem=0;
							if(mapOfSubjectSemAndMasterKey.containsKey(consumerProgramStructureId+"-"+feedback.getSubject())) {
								sem=mapOfSubjectSemAndMasterKey.get(consumerProgramStructureId+"-"+feedback.getSubject());
							}

							SessionCountBean bean=new SessionCountBean();
							bean.setSem(sem);
							bean.setConsumerProgramStructureId(consumerProgramStructureId);
							listOfSemForMasterKeys.add(bean);
						}
						
						//Student count for non PG and PG students before Jul21
						for(SessionCountBean bean: listOfSemForMasterKeys) {
							int pgCountForMasterKeyAndSem=0;
							int nonPGCountForMasterKeyAndSem=0;
							
							if(mapOfPGCountBeforeJuly21ForMasterKeyAndSem.containsKey(bean.getConsumerProgramStructureId()+"-"+bean.getSem())) {
								pgCountForMasterKeyAndSem=mapOfPGCountBeforeJuly21ForMasterKeyAndSem.get(bean.getConsumerProgramStructureId()+"-"+bean.getSem());
							}
							
							if(mapOfNonPGCountBeforeForMasterKeyAndSem.containsKey(bean.getConsumerProgramStructureId()+"-"+bean.getSem())) {
								nonPGCountForMasterKeyAndSem=mapOfNonPGCountBeforeForMasterKeyAndSem.get(bean.getConsumerProgramStructureId()+"-"+bean.getSem());
							}
							countOfRegisteredPGStudentForSessionBeforeJul21+=pgCountForMasterKeyAndSem;
							countOfRegisteredNonPGStudentForSession+=nonPGCountForMasterKeyAndSem;
						}
						//for PGSTudentAndNnPGStudentsBeoreJul21 end
					
					//applicable count of student for session
					applicableStudentsCountForSession=countOfRegisteredPGStudentForSessionAfterJul21+countOfRegisteredPGStudentForSessionBeforeJul21+countOfRegisteredNonPGStudentForSession+countOfRegisteredTimeBoundStudentForSession;
					//attendance count for session
					attendedStudentsCountForSession=mapOfSessionIdAndAttendaceCount.get(feedback.getSessionId());	
				}
					

			SessionAttendanceFeedbackAcads bean1=new SessionAttendanceFeedbackAcads();
			bean1.setApplicableStudentsForSession(applicableStudentsCountForSession);
			bean1.setAttendedStudentForSession(attendedStudentsCountForSession);
			
			mapOfSessionWiseAttendaceCount.put(feedback.getSessionId(), bean1);
			}

		}
		}catch (Exception e) {
			// TODO: handle exception
			  
		}
		
		/* Iterating all sessionList End */
		return mapOfSessionWiseAttendaceCount;
	}
	

	public ArrayList<SessionAttendanceFeedbackAcads> removeDuplicateSessionId(ArrayList<SessionAttendanceFeedbackAcads> list){
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionBean=new HashMap<>();
		ArrayList<SessionAttendanceFeedbackAcads> filteredSessionIdList = new ArrayList<>();

		for(SessionAttendanceFeedbackAcads bean:list) {
			mapOfSessionBean.put(bean.getSessionId(), bean);

		}
		
		filteredSessionIdList = new ArrayList<SessionAttendanceFeedbackAcads>(mapOfSessionBean.values());

        return filteredSessionIdList;
	}
	
	private String joinCommaSeperated(List<String> namesList) {
	    return String.join(",", namesList
	            .stream()
	            .map(name -> ("'" + name + "'"))
	            .collect(Collectors.toList()));
	}
	
	public ArrayList<SessionAttendanceFeedbackAcads> getAttendanceDetailsNew(SessionAttendanceFeedbackAcads searchBean,  String authorizedCenterCodes) {
		
		ArrayList<SessionAttendanceFeedbackAcads> listOfCombinedDataList=new ArrayList<SessionAttendanceFeedbackAcads>();
		//changing month for Oct & Apr cycle
		searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
		searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));
		
		String acadDateFormat=ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date;
			date = formatter.parse(acadDateFormat);
			Date historyDate=formatter.parse("2020-07-01");
		
		
		if (date.compareTo(historyDate) >= 0){
			//From Current Tables
			HashMap<String,FacultyAcadsBean> mapOfFacultyBean=attendanceFeedbackDAO.getMapOfFacultyDetails();
			HashMap<String,StudentAcadsBean> mapOfStudentBean=attendanceFeedbackDAO.getMapOfStudentDetails(searchBean.getYear(), searchBean.getMonth(), authorizedCenterCodes);
			//HashMap<String,SessionBean> mapOfSessionBean=attendanceFeedbackDAO.getMapOfSesionDetails(searchBean.getYear(), searchBean.getMonth());
			
			if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth())){
			searchBean.setMonth(searchBean.getMonth().replace("Oct", "Jul"));
			searchBean.setMonth(searchBean.getMonth().replace("Apr", "Jan"));

			ArrayList<SessionAttendanceFeedbackAcads> listOfSessionAttendanceFeedback = attendanceFeedbackDAO.getListSessionAttendaceDetailsForYearMonth(searchBean, acadDateFormat);
			
			for(SessionAttendanceFeedbackAcads obj1: listOfSessionAttendanceFeedback) {
				FacultyAcadsBean facultyBean=mapOfFacultyBean.get(obj1.getFacultyId());
				StudentAcadsBean studentBean=mapOfStudentBean.get(obj1.getSapId());
				
				try {
					obj1.setFacultyFirstName(facultyBean.getFirstName());
					obj1.setFacultyLastName(facultyBean.getLastName());
					obj1.setFirstName(studentBean.getFirstName());
					obj1.setLastName(studentBean.getLastName());
					obj1.setYear(studentBean.getYear());
					obj1.setMonth(studentBean.getMonth());
					obj1.setProgram(studentBean.getProgram());
					obj1.setSem(studentBean.getSem());
					listOfCombinedDataList.add(obj1);
				}catch (Exception e) {
					  
				}
			}
		}
			
		}else {
			//From History Table
			HashMap<String,FacultyAcadsBean> mapOfFacultyBean = attendanceFeedbackDAO.getMapOfFacultyDetails();
			HashMap<String,StudentAcadsBean> mapOfStudentBean=attendanceFeedbackDAO.getMapOfStudentDetails(searchBean.getYear(), searchBean.getMonth(), authorizedCenterCodes);
			
			if(!StringUtils.isBlank(searchBean.getYear()) && !StringUtils.isBlank(searchBean.getMonth())){
		
				ArrayList<SessionAttendanceFeedbackAcads> listOfSessionAttendanceFeedback=attendanceFeedbackDAO.getListSessionAttendaceDetailsFromHistory(searchBean, acadDateFormat);
			
				for(SessionAttendanceFeedbackAcads obj1: listOfSessionAttendanceFeedback) {
					FacultyAcadsBean facultyBean=mapOfFacultyBean.get(obj1.getFacultyId());
					StudentAcadsBean studentBean=mapOfStudentBean.get(obj1.getSapId());
					
					try {
						obj1.setFacultyFirstName(facultyBean.getFirstName());
						obj1.setFacultyLastName(facultyBean.getLastName());
						obj1.setFirstName(studentBean.getFirstName());
						obj1.setLastName(studentBean.getLastName());
						obj1.setYear(studentBean.getYear());
						obj1.setMonth(studentBean.getMonth());
						obj1.setProgram(studentBean.getProgram());
						obj1.setProgram(studentBean.getConsumerType());
						obj1.setSem(studentBean.getSem());
						listOfCombinedDataList.add(obj1);
					}catch (Exception e) {
						  
					}
				}
			}
		}
		
		}catch (Exception e) {
			// TODO: handle exception
		}
		return listOfCombinedDataList;
	}
	
	//post Session feedback
	public SessionAttendanceFeedbackAcads getPostSessionFeedback(String sapId,String sessionId, String acadDateFormat) throws Exception {
		
		SessionAttendanceFeedbackAcads pendingFeedback = new SessionAttendanceFeedbackAcads();
			pendingFeedback = attendanceFeedbackDAO.getPostSessionFeedback(sapId, sessionId, acadDateFormat);
			return pendingFeedback;
	}
	
	//save post session feed
	public void saveFeedback(SessionAttendanceFeedbackAcads feedback) throws Exception{
		attendanceFeedbackDAO.saveFeedback(feedback);
	}
}
