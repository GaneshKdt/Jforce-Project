package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.ExecutiveExamCenter;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;

public class ExecutiveConfigurationDao extends ExecutiveBaseDao{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	
	//Added by Steffi for ExecutiveConfiguration on 4th Jan
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Transactional(readOnly = true)
		public ArrayList<String> getAllProgramStructures() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT distinct prgmStructApplicable FROM exam.program_subject";
			ArrayList<String> programStructList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return programStructList;

		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Transactional(readOnly = true)
		public List<ExecutiveExamOrderBean> getExecutiveExamOrderList(){

			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql= "select * from exam.executive_examorder  order  by executive_examorder.order asc";
			List<ExecutiveExamOrderBean> examOrderList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));
			return examOrderList;
		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public void updateExamOrderTimetableStats(ExecutiveExamOrderBean exam){
			String sql = "insert into exam.executive_examorder (timeTableLive, year, month, acadMonth, acadYear) "
					+ " values (? , ? , ? , ? , ? )   "
					+ " on duplicate key "
					+ " update timeTableLive=? ";
					/*+ "where year =? and month = ? and acadMonth = ? and acadYear = ? ";*/

			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] { 
					exam.getTimeTableLive(),
					exam.getYear(),
					exam.getMonth(),
					exam.getAcadMonth(),
					exam.getAcadYear(),
					exam.getTimeTableLive()/*,
					exam.getYear(),
					exam.getMonth(),
					exam.getAcadMonth(),
					exam.getAcadYear()*/
			});
		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public void updateExamOrderRegistrationStats(ExecutiveExamOrderBean exam){
			String sql = "Update exam.executive_examorder set "
					+ " acadYear=?,"
					+ " acadMonth=?,"
					+ " registrationStartDate=?,"
					+ " registrationEndDate=? "
					+ " where year =? and month = ? "
					+ " and acadYear=? "
					+ " and acadMonth=?";

			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] { 
					exam.getAcadYear(),
					exam.getAcadMonth(),
					exam.getRegistrationStartDate(),
					exam.getRegistrationEndDate(),
					exam.getYear(),
					exam.getMonth(),
					exam.getAcadYear(),
					exam.getAcadMonth()
			});
		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public void updateExamOrderHallticketStats(ExecutiveExamOrderBean exam){
			String sql = "Update exam.executive_examorder set "
					+ " hallTicketStartDate=?,"
					+ " hallTicketEndDate=? "
					+ " where year =? and month = ? and acadYear=? and acadMonth=?";

			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] { 
					exam.getHallTicketStartDate(),
					exam.getHallTicketEndDate(),
					exam.getYear(),
					exam.getMonth(),
					exam.getAcadYear(),
					exam.getAcadMonth()
			});
		}
		
		//update result live n date start
		public void updateExamOrderResultStats(ExecutiveExamOrderBean exam){
			String sql = "Update exam.executive_examorder set "
					+ " resultLive=?,"
					+ " resultDeclareDate=? "
					+ " where year =? and month = ? and acadYear=? and acadMonth=?";

			jdbcTemplate = new JdbcTemplate(dataSource);

			jdbcTemplate.update(sql, new Object[] { 
					exam.getResultLive(),
					exam.getResultDeclareDate(),
					exam.getYear(),
					exam.getMonth(),
					exam.getAcadYear(),
					exam.getAcadMonth()
			});
		}
		//update result live n date end 
		
		@Transactional(readOnly = true)
		public List<StudentMarksBean> getActiveRegistrations(String sapid) {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration where sapid = ? "
					+ " and STR_TO_DATE(concat('01-',month,'-',year), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y') ";
			List<StudentMarksBean> registrationList = new ArrayList<>();
			try{
				registrationList = jdbcTemplate.query(sql, new Object[]{sapid,"01-"+"Jan"  + "-"+ "2018"},new BeanPropertyRowMapper(StudentMarksBean.class));
			}catch(Exception e){
				
			}
			return registrationList;
		}

		@Transactional(readOnly = true)
		public List<TimetableBean> getTimetableListForGivenSubjects(ArrayList<String> subjects,StudentExamBean student) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM exam.sas_timetable a, exam.executive_examorder b " + 
					"					  where  a.examyear = b.year and  a.examMonth = b.month and " + 
					"					  b.order = (Select max(executive_examorder.order) from exam.executive_examorder where timeTableLive='Y') " + 
					"					  and a.PrgmStructApplicable = ? " + 
					"					  order by a.subject, a.date, a.starttime  asc";
			List<TimetableBean> tempTimeTableList = jdbcTemplate.query(sql, new Object[]{student.getPrgmStructApplicable()},new BeanPropertyRowMapper(TimetableBean.class));
			List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
			if(tempTimeTableList != null && tempTimeTableList.size() > 0){
				for (int i = 0; i < tempTimeTableList.size(); i++) {
					TimetableBean bean = tempTimeTableList.get(i);
					if(subjects.contains(bean.getSubject())){
						timeTableList.add(bean);
					}
				}
			}
			return tempTimeTableList;
		}
		
		@Transactional(readOnly = true)
		public HashMap<String, String> getCenterMappings() {
			HashMap<String, String> CenterUserMapping = new HashMap<String, String>();
			String sql = " SELECT * FROM exam.executive_exam_center_slot_mapping ecu,exam.executive_examorder ee where ecu.year = ee.year and ecu.month = ee.month " + 
					"				  and ee.order = (SELECT max(executive_examorder.order) FROM exam.executive_examorder where timeTableLive = 'Y')";
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<ExamCenterBean> listOfCorporateUserMappings = (ArrayList<ExamCenterBean>) jdbcTemplate
					.query(sql, new BeanPropertyRowMapper(ExamCenterBean.class));
			for (ExamCenterBean exam : listOfCorporateUserMappings) {
				CenterUserMapping.put(exam.getSapid(), exam.getCenterId());
			}
			return CenterUserMapping;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<ExecutiveBean> getExamCenterDetails() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.executive_examcenter e where e.capacity " + 
					" IN " + 
					" (select s.capacity from exam.executive_exam_center_slot_mapping s where s.capacity>s.booked)";  
			ArrayList<ExecutiveBean> examCenterDetails = (ArrayList<ExecutiveBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExecutiveBean.class));
			return examCenterDetails;

		}
		
		@Transactional(readOnly = true)
		public ArrayList<ExecutiveBean> getTimeTableDetails(String prgmStructApplicable,String program ) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.sas_timetable t, exam.executive_examorder e where t.PrgmStructApplicable=? and " + 
					" t.program=? and e.year=t.examYear and e.month=t.examMonth and e.live='Y' ";  
			ArrayList<ExecutiveBean> timeTableDetails = (ArrayList<ExecutiveBean>) jdbcTemplate.query(sql,new Object[] {prgmStructApplicable,program}, new BeanPropertyRowMapper(ExecutiveBean.class));
			return timeTableDetails;

		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getCityList() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select s.city from exam.executive_examcenter s ";
			ArrayList<String> cityList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return cityList;

		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getTimeSlotList() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select startTime from exam.executive_exam_center_slot_mapping t ";
			ArrayList<String> timeSlotList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return timeSlotList;

		}
		
		public long saveCenterDetails(final ExecutiveBean executiveBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement("INSERT INTO exam.executive_exam_bookings"
				        		+ " (sapId, booked, subject, programStructApplicable ,"
				        		+ " program, sem) "
				        		+ " VALUES(?,'Y',?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
				        statement.setString(1, executiveBean.getSapid());
				        statement.setString(2, executiveBean.getSubject());
				        statement.setString(3, executiveBean.getPrgmStructApplicable());
				        statement.setString(4, executiveBean.getProgram());
				        //statement.setInt(5, executiveBean.getSem());
				        return statement;
				    }
				}, holder);

				long primaryKey = holder.getKey().longValue();

				return primaryKey;
			} catch (DataAccessException e) {
				
				return 0;
			}
		}
		
		@Transactional(readOnly = true)
		public ArrayList<String> getBookingStatus(String sapId,String program ,String programStructApplicable ) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select e.booked from exam.executive_exam_bookings e where " + 
					"e.sapId=? and e.programStructApplicable=? and " + 
					"e.program=? ";
			ArrayList<String> bookingStatusList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {sapId,programStructApplicable,program},
					new SingleColumnRowMapper(String.class));
			return bookingStatusList;

		}
		
		@Transactional(readOnly = true)
		public boolean isConfigurationLive(String configurationType) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select count(*) from exam.sas_configuration where"
					+ " configurationType = ? "
					+ "and startTime <= sysdate() and endTime >= sysdate()"
					+ " and 'Y'= (select eo.live from exam.executive_examorder eo "
					+ "				where eo.order= (select max(eeo.order) from exam.executive_examorder eeo) ) ";
					
			
			int noOfRows = 0;
			try {
				noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{configurationType},Integer.class);
			} catch (Exception e) {
				
			}

			if(noOfRows > 0){
				return true;
			}else{
				return false;
			}
		}
		
		
	//Exam booked pdf start--->	
	@Transactional(readOnly = true)
	public	ArrayList<ExecutiveBean> getExecutiveExamsBookedDetails(String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.executive_exam_bookings "
				+ " where sapid = ? "
				+ " and year = (select year from exam.executive_examorder eeo where eeo.order = (select max(eeo.order) from exam.executive_examorder) )  "
				+ " and month = (select month from exam.executive_examorder eeo where eeo.order = (select max(eeo.order) from exam.executive_examorder) ) ";
		
		ArrayList<ExecutiveBean> examBookedList = null;
		return examBookedList;
	}
	//Exam booked pdf end--->	
	
	@Transactional(readOnly = true)
	public	List<ExamBookingTransactionBean> getAllExamBookingsByYearMonth(ExamBookingTransactionBean searchBean, String authorizedCenterCodes){
		jdbcTemplate = new JdbcTemplate(dataSource);
			
		String sql = "select eeb.year, eeb.month, "
				+ "			s.sapid, s.firstName, s.lastName, s.program, s.emailId, s.mobile, s.altPhone,s.enrollmentYear,s.enrollmentMonth, "
				+ "			eeb.sem, eeb.subject, eeb.examDate, eeb.examTime, addtime(eeb.examTime,sec_to_time(90*60)) as examEndTime, "
				+ "			ec.centerId, ec.examCenterName as centerName, ec.city, eeb.createdDate, eeb.booked"
				+ " 	from exam.executive_exam_bookings eeb, "
				+ "			 exam.students s, "
				+ "			 exam.executive_examcenter ec " 
				+ " where eeb.booked = 'Y' "
				+ " and eeb.year = ?  "
				+ " and eeb.month = ? "
				+ " and s.enrollmentYear = ?  "
				+ " and s.enrollmentMonth = ? "
				+ " and s.sapid = eeb.sapid "
				+ " and eeb.centerId = ec.centerId "  ;
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		List<ExamBookingTransactionBean> examBookedList = null;
		
		try {
			examBookedList = jdbcTemplate.query(sql,
												new Object[] {searchBean.getYear(), searchBean.getMonth(),searchBean.getEnrollmentYear(),searchBean.getEnrollmentMonth()},
												new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		
		return examBookedList;
	}
	
	@Transactional(readOnly = true)
	public	List<ExecutiveExamCenter> getAllExamCenterSlotsCapacityByYearMonth(ExecutiveExamCenter searchBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select ec.batchYear,ec.batchMonth, ec.year, ec.month, ec.centerId, ec.examCenterName, ec.city, ec.address, "
				+ "			sm.date, sm.startTime as starttime, sm.capacity, sm.booked, sm.onHold,"
				+ "		    (sm.capacity - COALESCE(sm.booked, 0)  - (COALESCE(sm.onHold , 0))) available  " 
				+ " 	from  "
				+ "			 exam.executive_exam_center_slot_mapping sm, "
				+ "			 exam.executive_examcenter ec "
				+ " where "
				+ "  ec.year = ?  "
				+ " and ec.month = ? " 
				+ " and ec.batchYear = ?  "
				+ " and ec.batchMonth = ? " 
				+ " and sm.examcenterId = ec.centerId ";
		
		List<ExecutiveExamCenter> capacityList = null;
		
		try {
			capacityList = jdbcTemplate.query(sql,
												new Object[] {searchBean.getYear(), searchBean.getMonth(),searchBean.getBatchYear(),searchBean.getBatchMonth()},
												new BeanPropertyRowMapper(ExecutiveExamCenter.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		
		return capacityList;
	}
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public ArrayList<ExecutiveExamOrderBean> getExecutiveSubjectSetUp( String acadYear,String acadMonth,String examYear,String examMonth,String prgmStructApplicable,String program,String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.live_exam_subjects where 1=1 ";
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		if(!StringUtils.isBlank(acadYear)){
			sql+= " and acadYear = ?";
			parameters.add(acadYear);
		}
		if(!StringUtils.isBlank(acadMonth)){
			sql+= " and acadMonth = ?";
			parameters.add(acadMonth);
		}
		if(!StringUtils.isBlank(examYear)){
			sql+= " and examYear = ?";
			parameters.add(examYear);
		}
		if(!StringUtils.isBlank(examMonth)){
			sql+= " and examMonth = ?";
			parameters.add(examMonth);
		}
		if(!StringUtils.isBlank(prgmStructApplicable)){
			sql+= " and prgmStructApplicable = ?";
			parameters.add(prgmStructApplicable);
		}
		if(!StringUtils.isBlank(program)){
			sql+= " and program = ?";
			parameters.add(program);
		}
		if(!StringUtils.isBlank(subject)){
			sql+= " and subject = ?";
			parameters.add(subject);
		}
		Object[] args = parameters.toArray();
		ArrayList<ExecutiveExamOrderBean> liveSubjects = (ArrayList<ExecutiveExamOrderBean>) 
				jdbcTemplate.query(sql,args,
						 new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));
		return liveSubjects;

	}
	


}
