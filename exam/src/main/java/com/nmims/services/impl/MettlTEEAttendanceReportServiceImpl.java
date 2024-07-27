package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.MettlTEEAttendanceReportBean;
import com.nmims.daos.MettlTEEAttendanceReportDAO;
import com.nmims.services.MettlTEEAttendanceReportService;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Service
public class MettlTEEAttendanceReportServiceImpl implements MettlTEEAttendanceReportService{
	
	/*Variable*/
	@Autowired
	private MettlTEEAttendanceReportDAO teeAttendanceDAO;
	
	
	/*Implemented Methods*/
	@Override
	public List<MettlTEEAttendanceReportBean> getTEEAttendanceReportByCycle(MettlTEEAttendanceReportBean searchBean) throws Exception 
	{
		//To get the data of exam.pg_scheduleinfo_mettl by cycle wise
		List<MettlTEEAttendanceReportBean> teeAttendanceReportByCycle = teeAttendanceDAO.getTEEAttendanceReportByCycle(searchBean.getYear(), searchBean.getMonth());

		//If attendance report not found for searched cycle
		if(teeAttendanceReportByCycle.isEmpty())
		{
			return null;
		}
		
		//To get mapped sem and program list, key as "sapid+subject+examDate+examTime" and value as bean
		Map<String, MettlTEEAttendanceReportBean> mapSemAndProg = getMappedSemAndProg(searchBean.getYear(),searchBean.getMonth(),
				searchBean.getSem(),searchBean.getProgramCode());
		
		//To get mapped IC and LC
		Map<String, MettlTEEAttendanceReportBean> mapICAndLC = getMappedICAndLC();
		
		//To set the TEE attendance report properties
		List<MettlTEEAttendanceReportBean> teeAttendanceReport = setTEEAttendanceProperties(teeAttendanceReportByCycle,mapICAndLC,mapSemAndProg);

		//Return the TEE attendance report
		return teeAttendanceReport;
	}
	
	@Override
	public List<MettlTEEAttendanceReportBean> getAllProgramList() throws Exception {
		//To get all program list
		List<MettlTEEAttendanceReportBean> allProgramList = teeAttendanceDAO.getAllProgramList();
		
		return allProgramList;
	}
	
	
	
	/*Methods*/
	//To Get Mapped Sem And Program List
	public Map<String, MettlTEEAttendanceReportBean> getMappedSemAndProg(String year, String month, 
			Integer sem, String program) throws Exception
	{
		List<MettlTEEAttendanceReportBean> semAndProgramList = teeAttendanceDAO.getSemAndProgramList(year, month);
	
		if(sem != null)
			semAndProgramList = sortSemWiseList(semAndProgramList,sem);
	
		if(!program.isEmpty())
			semAndProgramList = sortProgramWiseList(semAndProgramList,program);
		
		//Converting list to map
		Map<String, MettlTEEAttendanceReportBean> mapSemAndProgList = semAndProgramList.stream().collect(Collectors.toMap(bean ->  bean.getSapid()+bean.getSubject()+bean.getExamDate()+bean.getExamTime(), bean -> bean,(oldEntry, newEntry) -> newEntry));
		
		return mapSemAndProgList;
	}
	
	//To Get Mapped IC And LC List
	public Map<String, MettlTEEAttendanceReportBean> getMappedICAndLC() throws Exception
	{
		List<MettlTEEAttendanceReportBean> icAndCenterCodeList = teeAttendanceDAO.getICAndCenterCode();
		List<MettlTEEAttendanceReportBean> allLCList = teeAttendanceDAO.getAllLCList();
		
		Map<String, MettlTEEAttendanceReportBean> lcMap = allLCList.stream().collect(Collectors.toMap(MettlTEEAttendanceReportBean :: getCenterCode, bean -> bean,(oldEntry, newEntry) -> newEntry));
		
		icAndCenterCodeList.stream().forEach(bean -> {
				if(lcMap.containsKey(bean.getCenterCode()))
				{
					MettlTEEAttendanceReportBean extractLC = lcMap.get(bean.getCenterCode());
					bean.setLc(extractLC.getLc());
				}
			});
		
		//Converting list to map to IC list, key as "sapid" and value as bean
		Map<String, MettlTEEAttendanceReportBean> mapICAndLC = icAndCenterCodeList.stream().collect(Collectors.toMap(MettlTEEAttendanceReportBean :: getSapid, bean -> bean,(oldEntry, newEntry) -> newEntry));
		
		return mapICAndLC;
	}
	
	//To Set The TEE Attendance Report Properties(IC, LC, Exam Status, Program and Sem), And Sort the Report Sem and Program Wise
	public List<MettlTEEAttendanceReportBean> setTEEAttendanceProperties(
			List<MettlTEEAttendanceReportBean> teeAttendanceReportByCycle,
			Map<String, MettlTEEAttendanceReportBean> mapICAndLC,
			Map<String, MettlTEEAttendanceReportBean> mapSemAndProg)
	{
		//Declared a list to store TEE attendance report
		List<MettlTEEAttendanceReportBean> teeAttednnaceReport = new ArrayList<>();

		//Iterating teeAttendanceReportByCycle
		teeAttendanceReportByCycle.forEach(bean -> {
			
			//To set IC and LC - START
			if(mapICAndLC.containsKey(bean.getSapid()))
			{
				//If matched then extract the key
				MettlTEEAttendanceReportBean extractStudentIC = mapICAndLC.get(bean.getSapid());
				//Set the IC into bean
				bean.setIc(extractStudentIC.getIc());
				//Set the LC into bean
				bean.setLc(extractStudentIC.getLc());
			}
			//To set IC and LC - END
			
			
			//To set Exam Status(Present or Absent) - START
			if(bean.getTestTaken() == null)
				//Marked exmaStatus as "Absent"
				bean.setExamStatus("Absent");
			//Or student Test Taken are not Null or students has started or Ended the mettl portal then
			else
				//Marked examStatus as "Present"
				bean.setExamStatus("Present"); 
			//To set Exam Status(Present or Absent) - END
			
			
			//To set sem and program - START
			if(mapSemAndProg.containsKey(bean.getSapid()+bean.getSubject()+bean.getExamDate()+bean.getExamTime()))
			{
				//Extract the mapped sem list
				MettlTEEAttendanceReportBean extractSemAndProg = mapSemAndProg.get(bean.getSapid()+bean.getSubject()+bean.getExamDate()+bean.getExamTime());
				
				//Add the sem and program into bean
				bean.setSem(extractSemAndProg.getSem());
				bean.setProgramCode(extractSemAndProg.getProgramCode());
				
				//Add bean into teeEEAttendanceReport List
				teeAttednnaceReport.add(bean);
			}
			//To set sem and program - END
		});
		
		//Return the teeAttendanceReport List
		return teeAttednnaceReport;
	}
	
	//To Sort the List Sem Wise
	public List<MettlTEEAttendanceReportBean> sortSemWiseList(List<MettlTEEAttendanceReportBean> semAndProgramList,
			Integer searchedSem)
	{
		List<MettlTEEAttendanceReportBean> sortedSemWiseList = new ArrayList<>();
		semAndProgramList.stream().forEach(bean -> {
			if(searchedSem == bean.getSem())
			{
				sortedSemWiseList.add(bean);
			}
		});
		return sortedSemWiseList;
	}
	
	//To Sort the List Program Wise
	public List<MettlTEEAttendanceReportBean> sortProgramWiseList(List<MettlTEEAttendanceReportBean> semAndProgramList,
			String searchedProgram)
	{
		List<MettlTEEAttendanceReportBean> sortedProgramWiseList = new ArrayList<>();
		semAndProgramList.stream().forEach(bean -> {
			if(searchedProgram.equals(bean.getProgramCode()))
			{
				sortedProgramWiseList.add(bean);
			}
		});
		return sortedProgramWiseList;
	}
}
