package com.nmims.daos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConfigurationStudentPortal;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ReRegistrationStudentPortalBean;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.QueryResult;
import com.sforce.ws.ConnectorConfig;

public class StudentInfoCheckDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH; 
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	public void setDataSource(DataSource dataSource) {
	      this.dataSource = dataSource;
	      this.jdbcTemplate = new JdbcTemplate(dataSource);
	   }
	
	
	/*
	 * Get list of student how have 
	 * program,validityEndMonth,validityEndYear,PrgmStructApplicable,enrollmentMonth,enrollmentYear
	 * any of value be null or empty
	 * 
	 * Param @
	 * 
	 * Return @
	 * List of student with missing column name 
	 * */
	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getStudentWithNullDataRow() {
	
		String SQL = "SELECT distinct sapid,validityEndMonth,validityEndYear,PrgmStructApplicable,enrollmentMonth,enrollmentYear,program,firstName,lastName,emailId,mobile,city FROM exam.students as s where (s.validityEndMonth is NULL or s.validityEndMonth = '') or (s.validityEndYear is NULL or s.validityEndYear = '') or (s.PrgmStructApplicable is NULL or s.PrgmStructApplicable = '') or (s.enrollmentMonth is NULL or s.enrollmentMonth = '') or (s.enrollmentYear is NULL or s.enrollmentYear = '') or (s.program is NULL or s.program = '') or (s.firstName is NULL or s.firstName = '') or (s.lastName is NULL or s.lastName = '') or (s.emailId is NULL or s.emailId = '') or (s.mobile is NULL or s.mobile = '') or (s.city is NULL or s.city = '');";
		return (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(SQL, new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
	}
	
	/*
	 * Count of missing student data
	 * */
	@Transactional(readOnly = true)
	public int getEmptyDataCount() {
		String SQL = "SELECT count(distinct sapid) as count FROM exam.students as s where (s.validityEndMonth is NULL or s.validityEndMonth = '') or (s.validityEndYear is NULL or s.validityEndYear = '') or (s.PrgmStructApplicable is NULL or s.PrgmStructApplicable = '') or (s.enrollmentMonth is NULL or s.enrollmentMonth = '') or (s.enrollmentYear is NULL or s.enrollmentYear = '') or (s.program is NULL or s.program = '') or (s.firstName is NULL or s.firstName = '') or (s.lastName is NULL or s.lastName = '') or (s.emailId is NULL or s.emailId = '') or (s.mobile is NULL or s.mobile = '') or (s.city is NULL or s.city = '') ;";
		return jdbcTemplate.queryForObject(SQL,Integer.class);
	}
	
	/*
	 * Count number of Student Offline Booked seat
	 * */
	@Transactional(readOnly = true)
	public int getStudentOfflineBookedSeatCount(String month,int year) {
		String SQL = "SELECT count(sapid) FROM exam.exambookings where examMode = 'Offlne' and booked = 'Y' and year = ? and month = ?;";
		return jdbcTemplate.queryForObject(SQL,new Object[] {year,month},Integer.class);
	}
	
	
	/*
	 * Count number of Student Online Booked seat
	 * */
	@Transactional(readOnly = true)
	public int getStudentOnlineBookedSeatCount(String month,int year) {
		String SQL = "SELECT count(sapid) FROM exam.exambookings where examMode = 'Online' and booked = 'Y' and year = ? and month = ?;";
		return jdbcTemplate.queryForObject(SQL,new Object[] {year,month},Integer.class);
	}
	
	/*
	 * Count number of Student Online Release seat
	 * */
	@Transactional(readOnly = true)
	public int getStudentOnlineReleaseSeatCount(String month,int year) {
		String SQL = "SELECT count(sapid) FROM exam.exambookings where examMode = 'Online' and booked = 'RL' and year = ? and month = ?;";
		return jdbcTemplate.queryForObject(SQL,new Object[] {year,month},Integer.class);
	}
	
	/*
	 * Count number of Student Offline Release seat
	 * */
	@Transactional(readOnly = true)
	public int getStudentOfflineReleaseSeatCount(String month,int year) {
		String SQL = "SELECT count(sapid) FROM exam.exambookings where examMode = 'Offline' and booked = 'RL' and year = ? and month = ?;";
		return jdbcTemplate.queryForObject(SQL,new Object[] {year,month},Integer.class);
	}
	
	
	/*
	 * count student booked twice
	 * */
	@Transactional(readOnly = true)
	public int getStudentBooktwice(String month,int year) {
		String SQL = "select count(repeated) as count from( select count(sapid) as repeated from exam.exambookings where booked = 'Y' and year = ? and month = ? group by sapid,subject,year,month,booked having repeated > 1 ) as s;";
		return jdbcTemplate.queryForObject(SQL,new Object[] {year,month},Integer.class);
	}
	
	/*
	 * count studentMissing Mapping Count
	 * */
	@Transactional(readOnly = true)
	public int getSubjectMissingMapCount() {
		String SQL = "SELECT COUNT(program) FROM exam.program_subject where sifySubjectCode = 0 and active = 'Y'  and subject NOT IN ('Project', 'Module 4 - Project');";
		return jdbcTemplate.queryForObject(SQL,Integer.class);
	}
	
	/*
	 * List of student Missing Mapping
	 * */
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getSubjectMissingMap() {
		String SQL = "SELECT program,subject,sem,prgmStructApplicable FROM exam.program_subject where sifySubjectCode = 0 and active = 'Y'  and subject NOT IN ('Project', 'Module 4 - Project');";
		return (ArrayList<ProgramSubjectMappingStudentPortalBean>) jdbcTemplate.query(SQL, new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class));
	}
	
	/*
	 * return Current ExamRegistration Date and time 
	 * */
	@Transactional(readOnly = true)
	public HashMap<String, String> getCurrentYearAndMonth(){
		String SQL = "SELECT * FROM exam.configuration where configurationType = 'Exam Registration' limit 1;";
		ConfigurationStudentPortal configuration = jdbcTemplate.queryForObject(SQL,new BeanPropertyRowMapper<ConfigurationStudentPortal>(ConfigurationStudentPortal.class));
		Map<String, String> YearAndMonth = new HashMap<String,String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String StartDate = configuration.getStartTime();
        String EndDate = configuration.getEndTime();
        String ExtendedEndDate = configuration.getExtendEndTime();
        
        String month;
        String year;
        try {
        	Date StartDate2 = format.parse(StartDate);
        	Date ExEndDate2 = format.parse(ExtendedEndDate);
        	Date EndDate2 = format.parse(EndDate);
        	SimpleDateFormat format2 = new SimpleDateFormat("MMM");
        	SimpleDateFormat format3 = new SimpleDateFormat("yyyy");
        	year = format3.format(StartDate2);
        	StartDate = format2.format(StartDate2);
        	EndDate = format2.format(EndDate2);
        	ExtendedEndDate = format2.format(ExEndDate2);
        	if(ExEndDate2.after(EndDate2)) {
        		//System.out.println("ExEndDate is greater than EndDate");
        		EndDate = ExtendedEndDate;
        	}
        	month = StartDate;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
		YearAndMonth.put("year", year);
		YearAndMonth.put("month", month);
		return (HashMap<String, String>) YearAndMonth;
	}
	
	
	@Transactional(readOnly = false)
	public void updateStudentPasswordStatus(String sapid) {
		String sql = "update exam.students set changedPassword = 'Y', lastModifiedBy = ? , lastModifiedPasswordDate = sysdate(), lastModifiedDate = sysdate() where sapid = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
		jdbcTemplate.update(sql, new Object[] { 
				sapid,sapid
		});
		}catch(Exception e){
			System.out.println("unable to update changedPassword flag");
		}

	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getstudentData(String sapId) {
		StudentStudentPortalBean student= new StudentStudentPortalBean();
		try {
			String sql = "select * from exam.students as s where s.sapid = ?";
			student =  jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class), sapId);
		} catch (DataAccessException e) {}
		return student;
	}
	
	@Transactional(readOnly = true)
	public boolean ifStudentAlreadyRegisteredForNextSem(String sapid,ReRegistrationStudentPortalBean activeRegistration) {
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
			//e.printStackTrace();
		} 
		return registered;
 	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getSingleSemRegistrationData(String sapId,String sem) {
		StudentStudentPortalBean student= new StudentStudentPortalBean();
		try {
			String sql = "select month as acadMonth,year as acadYear,sem from exam.registration  where sapid = ? and sem=?  limit 1";
			student =  jdbcTemplate.queryForObject(sql,new Object[] {sapId,sem} ,new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
		} catch (DataAccessException e) {}
		return student;
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SchedulerApisBean> getSalesforceSyncApiList() {
		ArrayList<SchedulerApisBean> bean= new ArrayList<SchedulerApisBean>();
		try {
			String sql = "select * from portal.scheduler_apis  ";
			bean =  (ArrayList<SchedulerApisBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(SchedulerApisBean.class));
		
		} catch (DataAccessException e) {//e.printStackTrace();
		}
		return bean;
		
	}
	
	@Transactional(readOnly = false)
	public void updateLastSyncedTime(SchedulerApisBean bean) {
		String sql = "update portal.scheduler_apis set lastSync = NOW(),error=? where syncType = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
		jdbcTemplate.update(sql, new Object[] { 
				bean.getError(),
				bean.getSyncType() 
		});
		}catch(Exception e){
			System.out.println("unable to update  ");
		} 
	}
	
	@Transactional(readOnly = true)
	public int getQpPendingToApprove() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT count(*) "
			+ " FROM  `exam`.`assignment_faculty_qp_creation_mapping`"
			+ " where `approve`='N' and reviewStatus='Y'   ";
		int count=0;
		try {
			count = jdbcTemplate.queryForObject(sql,new Object[] {} ,Integer.class);
		} catch (Exception e) {}
		return count;	  
	}
}
