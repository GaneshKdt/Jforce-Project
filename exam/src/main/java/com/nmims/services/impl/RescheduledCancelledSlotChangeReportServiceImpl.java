package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import com.google.common.base.Throwables;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.RescheduledCancelledSlotChangeReportBean;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.RescheduledCancelledSlotChangeReportDAO;
import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.services.RescheduledCancelledSlotChangeReportService;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */

@Service
@EnableAsync
public class RescheduledCancelledSlotChangeReportServiceImpl implements RescheduledCancelledSlotChangeReportService{
	
	/*Variables*/
	@Autowired
	private RescheduledCancelledSlotChangeReportDAO dao;
	@Autowired
	private ExamCenterDAO examCenterDAO;
	
	public static final Logger exambookingAuditlogger = LoggerFactory.getLogger("examBookingAudit");
	
	private final String SEAT_CANCELLED_WITH_REFUND = "Cancellation With Refund";
	private final String SEAT_CANCELLED_WITHOUT_REFUND = "Cancellation Without Refund";
	
	
	
	/*Implemented Methods*/
	@Override
	public List<RescheduledCancelledSlotChangeReportBean> getAllReleasedAndCancelledList() throws Exception 
	{
		//Declared to store all RL and CL sorted list
		List<RescheduledCancelledSlotChangeReportBean> sortedRlAndClList = new ArrayList<>();
		
		//Declared to add all RL and CL list into one list
		List<RescheduledCancelledSlotChangeReportBean> allRLAndCLList = new ArrayList<>();
		
		//To get all RL list
		List<RescheduledCancelledSlotChangeReportBean> allRlList = dao.getAllRLList();
		
		//To get all CL list
		List<RescheduledCancelledSlotChangeReportBean> allClList = dao.getAllCLList();
		
		//To add RL and CL list into one list i.e addedRLAndCLList
		allRLAndCLList.addAll(allRlList);
		allRLAndCLList.addAll(allClList);

		//To get mapped allRLAndCLList, key as "sapid" and value as list of bean related to their sapid
		HashMap<String, LinkedList<RescheduledCancelledSlotChangeReportBean>> mappedRLAndCLList = getMappedRLAndCLList(allRLAndCLList);

		//To get all mapped IC and LC of students, key as "sapid" and value as bean that include(ic, centerCode and sapid)
		Map<String, RescheduledCancelledSlotChangeReportBean> mappedICAndLC = getMappedICAndLC();

		//To get most recent marked RL or CL data list for every sapid and subjects
		sortedRlAndClList = getSortedRlAndClList(mappedRLAndCLList,mappedICAndLC);
			
		//To return sorted RL and CL list
		return sortedRlAndClList;
	}
	
	@Async
	@Override
	public void asyncInsertExamBookingAudit(List<ExamBookingTransactionBean> toReleaseSubjectList, 
			String releasedStatus, String createdBy) throws Exception 
	{
		try
		{
			exambookingAuditlogger.info("Column that will be use in insert operation on exambooking_audit table: "+"Released Status="+releasedStatus+", Created By="+createdBy
					+"  All Subject Response:"+toReleaseSubjectList);
			
			//To insert RL Details in exambookings_audit table
			int insertRowCount = dao.batchInsertExamBookingAudit(toReleaseSubjectList, releasedStatus, createdBy);
			
			exambookingAuditlogger.info("Number of Record Inserted Succesfully By Student: "+insertRowCount);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			exambookingAuditlogger.error("Error in Inserting Released Data In exambookin_audit Table By Student: "+Throwables.getStackTraceAsString(e));
		}
	}
	
	@Override
	public List<ExamBookingTransactionBean> getConfirmedOrReleasedBooking(ExamBookingTransactionDTO bookingDTO) {
		
		ExamBookingTransactionBean booking = new ExamBookingTransactionBean();
		
		//Convert DTO to BO class object.
		BeanUtils.copyProperties(bookingDTO, booking);
		
		ArrayList<ExamBookingTransactionBean> confirmedOrReleasedBooking = examCenterDAO.getConfirmedOrReleasedBooking(booking);
		
		return confirmedOrReleasedBooking;
	}
	
	
	
	/*Service Methods*/
	public HashMap<String, LinkedList<RescheduledCancelledSlotChangeReportBean>> getMappedRLAndCLList(
			List<RescheduledCancelledSlotChangeReportBean> allRlAndClList)
	{
		//Declared to store mapped RC and LC list by sapid wise
		HashMap<String, LinkedList<RescheduledCancelledSlotChangeReportBean>> mappedAllRlAndClList = new HashMap<>();
		
		//Iterate allRlAndClList
		allRlAndClList.stream().forEach(bean -> {
			//If map contain this sapid, then just append this new records of this sapid in their value
			if(mappedAllRlAndClList.containsKey(bean.getSapid()))
			{
				LinkedList<RescheduledCancelledSlotChangeReportBean> extractRlAndClList = mappedAllRlAndClList.get(bean.getSapid());
				extractRlAndClList.add(bean);
				mappedAllRlAndClList.put(bean.getSapid(), extractRlAndClList);
			}
			//If map not contain this sapid then, just add this new record of this sapid in the map
			else
			{
				LinkedList<RescheduledCancelledSlotChangeReportBean> tempList = new LinkedList<>();
				tempList.add(bean);
				mappedAllRlAndClList.put(bean.getSapid(), tempList);
			}
		});
		
		//Return the map that store all the released and cancelled data by sapid wise
		return mappedAllRlAndClList;
	}
	
	public List<RescheduledCancelledSlotChangeReportBean> getSortedRlAndClList(
			HashMap<String, LinkedList<RescheduledCancelledSlotChangeReportBean>> mappedRlAndClList,
			Map<String, RescheduledCancelledSlotChangeReportBean> mappedICAndLC)
	{
		//Created a list that store most recent released or cancelled data for every sapid and subjects
		List<RescheduledCancelledSlotChangeReportBean> sortedRlAndClList = new ArrayList<>();
		
		//Iterate mappedRlAndClList 
		for(Map.Entry<String, LinkedList<RescheduledCancelledSlotChangeReportBean>> entry : mappedRlAndClList.entrySet())
		{
			//Extract value from HashMap and store in linked list as extractRlAndClList
			LinkedList<RescheduledCancelledSlotChangeReportBean> extractRlAndClList = entry.getValue();
			
			//Sort the above list by createdDate in descending order
			List<RescheduledCancelledSlotChangeReportBean> sortedList = extractRlAndClList.stream()
	                  .sorted(Comparator.comparing(RescheduledCancelledSlotChangeReportBean::getCreatedDate).reversed())
	                  .collect(Collectors.toList());
			
			//Created a list to store unique subject and check subject will not repeat
			List<String> checkSubject = new ArrayList<>();
			
			//Itearte sorted list by createdDate wise
			sortedList.stream().forEach(bean -> {
				//Check if checkSubject list have already store the subject then just filter out it else execute if
				if(!checkSubject.contains(bean.getSubject()))
				{
					//Extract mappedICAndLC HashMap and 
					RescheduledCancelledSlotChangeReportBean extractICAndLC = mappedICAndLC.get(bean.getSapid());
					
					//If sapid mapped then set IC and LC in bean
					if(mappedICAndLC.containsKey(bean.getSapid()))
					{
						bean.setIc(extractICAndLC.getIc());
						bean.setLc(extractICAndLC.getLc());
					}
					
					//If tranStatus seat are cancelled then add cancel status "Cancel with/without refund"
					if(SEAT_CANCELLED_WITH_REFUND.equals(bean.getTranStatus()))
						bean.setCancelStatus(SEAT_CANCELLED_WITH_REFUND);
					else if(SEAT_CANCELLED_WITHOUT_REFUND.equals(bean.getTranStatus()))
						bean.setCancelStatus(SEAT_CANCELLED_WITHOUT_REFUND);
					
					//Add bean in list of report for RL And CL
					sortedRlAndClList.add(bean);
					//Add the current iteration of subject in the list so that it can't repeat in next iteration
					checkSubject.add(bean.getSubject());
				}
			});
		}
		
		//Return the sorted list
		return sortedRlAndClList;
	}
	
	public Map<String,RescheduledCancelledSlotChangeReportBean> getMappedICAndLC() throws Exception
	{
		//To get all IC List with centerCode
		List<RescheduledCancelledSlotChangeReportBean> allICList = dao.getAllICList();

		//To get all LC list with centerCode
		List<RescheduledCancelledSlotChangeReportBean> allLCList = dao.getAllLCList();

		//Mapped LC list, key as "centerCode" and LC as value
		Map<String, RescheduledCancelledSlotChangeReportBean> mappedLC = allLCList.stream().collect(Collectors.toMap(RescheduledCancelledSlotChangeReportBean :: getCenterCode, bean -> bean,(oldEntry, newEntry) -> newEntry));
		
		//Iterate IC list
		allICList.stream().forEach(bean -> {
			//If LC centerCode matched with IC centerCode
			if(mappedLC.containsKey(bean.getCenterCode()))
			{
				//Extract the mapped LC
				RescheduledCancelledSlotChangeReportBean extractLC = mappedLC.get(bean.getCenterCode());
				//Set the LC in IC list from extracted LC
				bean.setLc(extractLC.getLc());
			}
		});
		
		//Converting list to map to IC list, key as "sapid" and value as bean
		Map<String, RescheduledCancelledSlotChangeReportBean> mapICAndLC = allICList.stream().collect(Collectors.toMap(RescheduledCancelledSlotChangeReportBean :: getSapid, bean -> bean,(oldEntry, newEntry) -> newEntry));
		
		//Return mapped IC and LC list
		return mapICAndLC;
	}
}
