package com.nmims.daos;

import java.sql.SQLException;
import java.util.List;

import com.nmims.beans.TimeboundExamBookingBean;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
public interface ISupportDAO {
	
	public void addUgConsentOption(String optionId,String sapid);
	
	public int checkStudentHasGivenConsent(String sapid);
	
	public List<TimeboundExamBookingBean> getExamBookingDetails(String sapId) throws SQLException;
	
}
