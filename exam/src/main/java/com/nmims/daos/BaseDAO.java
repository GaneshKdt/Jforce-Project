package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.MBAWXConfigurationBean;
import com.nmims.beans.ReRegistrationBean;

public abstract class BaseDAO {

	protected DataSource baseDataSource;
	private JdbcTemplate jdbcTemplate;
	private List<ExamOrderExamBean> liveFlagList = null;

	private String liveExamMonth = null;
	private String liveExamYear = null;
	private String liveAcadConentMonth = null;
	private String liveAcadConentYear = null;
	private String liveAcadSessionMonth = null;
	private String liveAcadSessionYear = null;

	private String liveOnlineExamResultMonth = null;
	private String liveOnlineExamResultYear = null;

	private String liveOfflineExamResultMonth = null;
	private String liveOfflineExamResultYear = null;

	private String liveAssignmentMonth = null;
	private String liveAssignmentYear = null;

	private String liveResitAssignmentMonth = null;
	private String liveResitAssignmentYear = null;
	
	private String liveRegularTestMonth = null;
	private Integer liveRegularTestYear = null;

	private String liveResitTestMonth = null;
	private Integer liveResitTestYear = null;

	
	private String liveAssignmentMarksMonth = null;
	private String liveAssignmentMarksYear = null;
	
	private String liveProjectExamMonth = null;
	private String liveProjectExamYear = null;
	
	private static boolean isExtendedExamRegistrationLiveForRealTime=false;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH; 
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value("${REAL_TIME_REGISTRATION_WINDOW}")
	private String REAL_TIME_REGISTRATION_WINDOW;
	
	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	public abstract void setBaseDataSource();

	/*
	 * public BaseDAO(){ 
	 * getLiveFlagDetails(); }
	 */

	public String refreshLiveFlagSettings() {
		String methodStatus = null;
		try {
			getLiveFlagDetails(true);
			methodStatus = "Cache Refresh Successfully";
		} catch (Exception e) {
			methodStatus = e.getMessage();
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return methodStatus;
	}

	public List<ExamOrderExamBean> getLiveFlagDetails(boolean refresh) {
			// Query only once, and refresh later when settings are changed
			if (this.liveFlagList == null || this.liveFlagList.size() == 0 || refresh == true) {
				final String sql = " Select * from exam.examorder order by examorder.order asc";
			jdbcTemplate = new JdbcTemplate(baseDataSource);
				this.liveFlagList = (ArrayList<ExamOrderExamBean>) jdbcTemplate.query(sql,
						new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));

				for (ExamOrderExamBean bean : liveFlagList) {
					if ("Y".equalsIgnoreCase(bean.getAcadSessionLive())) {
						liveAcadSessionMonth = bean.getAcadMonth();
						liveAcadSessionYear = bean.getYear();
					}

					if ("Y".equalsIgnoreCase(bean.getAcadContentLive())) {
						liveAcadConentMonth = bean.getAcadMonth();
						liveAcadConentYear = bean.getYear();
					}

					if ("Y".equalsIgnoreCase(bean.getTimeTableLive())) {
						liveExamMonth = bean.getMonth();
						liveExamYear = bean.getYear();
					}

					if ("Y".equalsIgnoreCase(bean.getLive())) {
						liveOnlineExamResultMonth = bean.getMonth();
						liveOnlineExamResultYear = bean.getYear();
					}

					if ("Y".equalsIgnoreCase(bean.getOflineResultslive())) {
						liveOfflineExamResultMonth = bean.getMonth();
						liveOfflineExamResultYear = bean.getYear();
					}
					/**
					 * Changes made after login
					 * */
					/*if ("Y".equalsIgnoreCase(bean.getAssignmentLive())) {
						liveAssignmentMonth = bean.getAcadMonth();
						liveAssignmentYear = bean.getYear();
					}

					if ("Y".equalsIgnoreCase(bean.getResitAssignmentLive())) {
						liveResitAssignmentMonth = bean.getMonth();
						liveResitAssignmentYear = bean.getYear();
					}*/

					if ("Y".equalsIgnoreCase(bean.getAssignmentMarksLive())) {
						liveAssignmentMarksMonth = bean.getMonth();
						liveAssignmentMarksYear = bean.getYear();
					}
					if ("Y".equalsIgnoreCase(bean.getProjectSubmissionLive())) {
						liveProjectExamMonth = bean.getMonth();
						liveProjectExamYear = bean.getYear();
					}
				}
			isExtendedExamRegistrationLiveForRealTime = checkisExtendedExamRegistrationLiveForRealTime("Exam Registration");
			logger.info("isExtendedExamRegistrationLiveForRealTime:"+isExtendedExamRegistrationLiveForRealTime);
			//isExtendedExamRegistrationLiveForRealTime = checkRealTimeRegistrationWindow();
		}

		return this.liveFlagList;
	}
	
//	public boolean checkRealTimeRegistrationWindow()
//	{
//		boolean flag=false;
//		try {
//			if(REAL_TIME_REGISTRATION_WINDOW.equalsIgnoreCase("Active")) {
//				flag=true;
//			}
//		}
//		catch(Exception e) {
//			
//		}
//		return flag;
//	}
	
	public boolean checkisExtendedExamRegistrationLiveForRealTime(String configurationType)
	{
		int noOfRows=0;
		try
		{
			String sql="select count(*) from exam.configuration where configurationType = ? and realTimeRegistration = 'Y' ";
			noOfRows=(int)jdbcTemplate.queryForObject(sql, new Object[] {configurationType}, Integer.class);
		}
		catch(Exception e){
			logger.info("Exception is:"+e);
		}
		
		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}

	public String getCommaSepareatedSubjects(List<ExamBookingTransactionBean> bookingList) {
		String subjectCommaSeparated = "";
		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			if (i == 0) {
				subjectCommaSeparated = "'" + bean.getSubject().replace("'", "\\'") + "'";
			} else {
				subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'", "\\'") + "'";
			}
		}

		return subjectCommaSeparated;
	}
	
	
	
	public boolean getIsExtendedExamRegistrationLiveForRealTime() {
		return isExtendedExamRegistrationLiveForRealTime;
	}

	public void setExtendedExamRegistrationLiveForRealTime(boolean isExtendedExamRegistrationLiveForRealTime) {
		this.isExtendedExamRegistrationLiveForRealTime = isExtendedExamRegistrationLiveForRealTime;
	}

	/**
	 * @return the liveRegularTestMonth
	 */
	public String getLiveRegularTestMonth() {
		return liveRegularTestMonth;
	}

	/**
	 * @param liveRegularTestMonth the liveRegularTestMonth to set
	 */
	public void setLiveRegularTestMonth(String liveRegularTestMonth) {
		this.liveRegularTestMonth = liveRegularTestMonth;
	}

	/**
	 * @return the liveRegularTestYear
	 */
	public Integer getLiveRegularTestYear() {
		return liveRegularTestYear;
	}

	/**
	 * @param liveRegularTestYear the liveRegularTestYear to set
	 */
	public void setLiveRegularTestYear(Integer liveRegularTestYear) {
		this.liveRegularTestYear = liveRegularTestYear;
	}

	/**
	 * @return the liveResitTestMonth
	 */
	public String getLiveResitTestMonth() {
		return liveResitTestMonth;
	}

	/**
	 * @param liveResitTestMonth the liveResitTestMonth to set
	 */
	public void setLiveResitTestMonth(String liveResitTestMonth) {
		this.liveResitTestMonth = liveResitTestMonth;
	}

	/**
	 * @return the liveResitTestYear
	 */
	public Integer getLiveResitTestYear() {
		return liveResitTestYear;
	}

	/**
	 * @param liveResitTestYear the liveResitTestYear to set
	 */
	public void setLiveResitTestYear(Integer liveResitTestYear) {
		this.liveResitTestYear = liveResitTestYear;
	}

	public String getLiveExamMonth() {
		return liveExamMonth;
	}

	public void setLiveExamMonth(String liveExamMonth) {
		this.liveExamMonth = liveExamMonth;
	}

	public String getLiveExamYear() {
		return liveExamYear;
	}

	public void setLiveExamYear(String liveExamYear) {
		this.liveExamYear = liveExamYear;
	}

	public String getLiveAcadConentMonth() {
		return liveAcadConentMonth;
	}

	public void setLiveAcadConentMonth(String liveAcadConentMonth) {
		this.liveAcadConentMonth = liveAcadConentMonth;
	}

	public String getLiveAcadConentYear() {
		return liveAcadConentYear;
	}

	public void setLiveAcadConentYear(String liveAcadConentYear) {
		this.liveAcadConentYear = liveAcadConentYear;
	}

	public String getLiveAcadSessionMonth() {
		return liveAcadSessionMonth;
	}

	public void setLiveAcadSessionMonth(String liveAcadSessionMonth) {
		this.liveAcadSessionMonth = liveAcadSessionMonth;
	}

	public String getLiveAcadSessionYear() {
		return liveAcadSessionYear;
	}

	public void setLiveAcadSessionYear(String liveAcadSessionYear) {
		this.liveAcadSessionYear = liveAcadSessionYear;
	}

	public String getLiveOnlineExamResultMonth() {
		return liveOnlineExamResultMonth;
	}

	public void setLiveOnlineExamResultMonth(String liveOnlineExamResultMonth) {
		this.liveOnlineExamResultMonth = liveOnlineExamResultMonth;
	}

	public String getLiveOnlineExamResultYear() {
		return liveOnlineExamResultYear;
	}

	public void setLiveOnlineExamResultYear(String liveOnlineExamResultYear) {
		this.liveOnlineExamResultYear = liveOnlineExamResultYear;
	}

	public String getLiveOfflineExamResultMonth() {
		return liveOfflineExamResultMonth;
	}

	public void setLiveOfflineExamResultMonth(String liveOfflineExamResultMonth) {
		this.liveOfflineExamResultMonth = liveOfflineExamResultMonth;
	}

	public String getLiveOfflineExamResultYear() {
		return liveOfflineExamResultYear;
	}

	public void setLiveOfflineExamResultYear(String liveOfflineExamResultYear) {
		this.liveOfflineExamResultYear = liveOfflineExamResultYear;
	}

	public String getLiveAssignmentMonth() {
		return liveAssignmentMonth;
	}

	public void setLiveAssignmentMonth(String liveAssignmentMonth) {
		this.liveAssignmentMonth = liveAssignmentMonth;
	}

	public String getLiveAssignmentYear() {
		return liveAssignmentYear;
	}

	public void setLiveAssignmentYear(String liveAssignmentYear) {
		this.liveAssignmentYear = liveAssignmentYear;
	}

	public String getLiveResitAssignmentMonth() {
		return liveResitAssignmentMonth;
	}

	public void setLiveResitAssignmentMonth(String liveResitAssignmentMonth) {
		this.liveResitAssignmentMonth = liveResitAssignmentMonth;
	}

	public String getLiveResitAssignmentYear() {
		return liveResitAssignmentYear;
	}

	public void setLiveResitAssignmentYear(String liveResitAssignmentYear) {
		this.liveResitAssignmentYear = liveResitAssignmentYear;
	}

	public String getLiveAssignmentMarksMonth() {
		return liveAssignmentMarksMonth;
	}

	public void setLiveAssignmentMarksMonth(String liveAssignmentMarksMonth) {
		this.liveAssignmentMarksMonth = liveAssignmentMarksMonth;
	}

	public String getLiveAssignmentMarksYear() {
		return liveAssignmentMarksYear;
	}

	public void setLiveAssignmentMarksYear(String liveAssignmentMarksYear) {
		this.liveAssignmentMarksYear = liveAssignmentMarksYear;
	}

	public String getLiveProjectExamYear() {
		return liveProjectExamYear;
	}

	public void setLiveProjectExamYear(String liveProjectExamYear) {
		this.liveProjectExamYear = liveProjectExamYear;
	}

	public String getLiveProjectExamMonth() {
		return liveProjectExamMonth;
	}

	public void setLiveProjectExamMonth(String liveProjectExamMonth) {
		this.liveProjectExamMonth = liveProjectExamMonth;
	}
	public boolean ifStudentAlreadyRegisteredForNextSem(String sapid,ReRegistrationBean activeRegistration) {
		boolean registered = true;
		try {
			String sql =  " SELECT count(*) "
				+ " FROM  `exam`.`registration`"
				+ " where `sapid`=? and month=? and year=?   ";
			  int count = jdbcTemplate.queryForObject(sql,new Object[] {sapid,activeRegistration.getAcadMonth(),activeRegistration.getAcadYear()} ,Integer.class);
			  
			 String sql1 =  " SELECT count(*) "
					    	+ " FROM  exam.registration_staging_future_records"
					    	+ " where `sapid`=? and month=? and year=?   ";
			 int count1 = jdbcTemplate.queryForObject(sql,new Object[] {sapid,activeRegistration.getAcadMonth(),activeRegistration.getAcadYear()} ,Integer.class);
			if(count==0 && count1==0) {
				registered=false;
			}
		} catch (DataAccessException e) {
			
		} 
		return registered;
 	}


}
