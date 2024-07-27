package com.nmims.daos;

import java.util.List;

import com.nmims.beans.MettlTEEAttendanceReportBean;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface MettlTEEAttendanceReportDAO {
	
	//To Get All Programs List
	public List<MettlTEEAttendanceReportBean> getAllProgramList()throws Exception;
		
	//To get TEE Attendance Report By Cycle Wise(Exam Year and Exam Month)
	public List<MettlTEEAttendanceReportBean> getTEEAttendanceReportByCycle(String examYear, String examMonth)throws Exception;
	
	//To Get Sem And Program From exam.exambookings Table
	public List<MettlTEEAttendanceReportBean> getSemAndProgramList(String year, String month) throws Exception;
	
	//To IC And Center Code List
	public List<MettlTEEAttendanceReportBean> getICAndCenterCode() throws Exception;
	
	//To Get All LC List
	public List<MettlTEEAttendanceReportBean> getAllLCList() throws Exception;
}
