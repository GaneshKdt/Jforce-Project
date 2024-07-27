package com.nmims.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingCancelBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.ReportsDAO;
import com.nmims.services.IReportsService;

/**
 * 
 * @author Manasi_T
 *
 */

@Service("reportsService")
public class ReportsService implements IReportsService {
	
	@Autowired
	private ReportsDAO reportsDAO;

	@Override
	public List<ExamCenterSlotMappingBean> getexamCenterCapacityReport(StudentMarksBean studentMarks) throws Exception {
		long start = System.currentTimeMillis();

		List<ExamCenterSlotMappingBean> allCapacityRecords = new ArrayList<ExamCenterSlotMappingBean>();
		List<ExamCenterSlotMappingBean> centerBookingsList = new ArrayList<ExamCenterSlotMappingBean>();
		List<ExamCenterSlotMappingBean> corporateCenterBookingsList = new ArrayList<ExamCenterSlotMappingBean>();
		
		//get the list of retail center booking by centerId-examDate-examTime
		centerBookingsList = reportsDAO.getCenterBookings(studentMarks,false);
		//getting list of corporate center booking based on centerID-examDate-examTime
		corporateCenterBookingsList = reportsDAO.getCenterBookings(studentMarks,true);
		System.out.print("CenterBookings"+reportsDAO.getCenterBookings(studentMarks,true));
		
		allCapacityRecords.addAll(centerBookingsList);
		allCapacityRecords.addAll(corporateCenterBookingsList);
		
		// converting Date and time into day
		allCapacityRecords.forEach((examCenterSlotMappingBean)->{
			try {
				examCenterSlotMappingBean.setDay(
						LocalDate.parse(examCenterSlotMappingBean.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
								.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US));
			
				
			
			}catch(Exception e) {
				//e.printStackTrace();
			}
			
		});
		long elapsedTime = System.currentTimeMillis() -  start;
		 System.out.println("ServicesProfiler.profile(): Method execution time: " + elapsedTime + " milliseconds.");
		 System.out.println("in method getexamCenterCapacityReport");
		return allCapacityRecords;
	}

	@Override
	public List<ExamBookingCancelBean> getExamBookingCanceledListReport(ExamBookingCancelBean searchBean) throws Exception {
		List<ExamBookingCancelBean> canceledBookingList=new ArrayList<ExamBookingCancelBean>();
		List<ExamBookingCancelBean> cancelAdditionalList=new ArrayList<ExamBookingCancelBean>();
		canceledBookingList = reportsDAO.getCancelledExamBookings(searchBean.getYear(),searchBean.getMonth());
		if(canceledBookingList != null && canceledBookingList.size() > 0)
		{
			//Extracting sapid from cancel list
			List<String> sapids = canceledBookingList.stream().map(ExamBookingCancelBean::getSapid)
	        .collect(Collectors.toList());
			//Get the student details by sapid wise
			List<ExamBookingCancelBean> cancelStudentList = reportsDAO.getCancelStudentBySapid(sapids);
			//Converting student list to map
			Map<String, ExamBookingCancelBean> cancelStudentMap = cancelStudentList.stream()
					.collect(Collectors.toMap(bean ->  bean.getSapid(), bean -> bean,(oldEntry, newEntry) -> newEntry));
			//Get all center details
			ArrayList<CenterExamBean> allCenters = reportsDAO.getAllCenters();
			//Converting center list to map
			Map<String, CenterExamBean> allCentersMap = allCenters.stream()
					.collect(Collectors.toMap(bean ->  bean.getCenterCode(), bean -> bean,(oldEntry, newEntry) -> newEntry));
			//Include student and center details in cancellation report list
			cancelAdditionalList = includeAdditionalDetails(canceledBookingList,cancelStudentMap,allCentersMap);
			//return the included list 
		}
		return cancelAdditionalList;
	}

	public List<ExamBookingCancelBean> includeAdditionalDetails(List<ExamBookingCancelBean> cancelStudentList,
			Map<String, ExamBookingCancelBean> cancelStudentMap, Map<String, CenterExamBean> allCentersMap)
	{
		cancelStudentList.stream().forEach(bean -> {
			if(cancelStudentMap.containsKey(bean.getSapid()))
			{
				ExamBookingCancelBean cancelStudentExtracted = cancelStudentMap.get(bean.getSapid());
				bean.setFirstName(cancelStudentExtracted.getFirstName());
				bean.setLastName(cancelStudentExtracted.getLastName());
				bean.setCenterCode(cancelStudentExtracted.getCenterCode());
				bean.setCenterName(cancelStudentExtracted.getCenterName());
				bean.setValidityEndYear(cancelStudentExtracted.getValidityEndYear());
				bean.setValidityEndMonth(cancelStudentExtracted.getValidityEndMonth());
				bean.setEnrollmentMonth(cancelStudentExtracted.getEnrollmentMonth());
				bean.setEnrollmentYear(cancelStudentExtracted.getEnrollmentYear());
				bean.setEmailId(cancelStudentExtracted.getEmailId());
				bean.setMobile(cancelStudentExtracted.getMobile());
			}
			if(allCentersMap.containsKey(bean.getCenterCode()))
				bean.setLc(allCentersMap.get(bean.getCenterCode()).getLc());
		});
		return cancelStudentList;
	}

}
