package com.nmims.services;

import java.util.List;
import com.nmims.beans.MettlTEEAttendanceReportBean;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public interface MettlTEEAttendanceReportService {
	//To Get TEE Attendance Report By Cycle wise and Program,Sem Wise If Any
	public List<MettlTEEAttendanceReportBean> getTEEAttendanceReportByCycle(MettlTEEAttendanceReportBean bean)throws Exception;
	//To Get All Program List
	public List<MettlTEEAttendanceReportBean> getAllProgramList()throws Exception;
}
