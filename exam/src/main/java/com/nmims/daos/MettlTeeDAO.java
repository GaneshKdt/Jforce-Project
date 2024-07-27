package com.nmims.daos;


import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.beans.MettlHookResponseBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.ExamBookingTransactionBean;

@Repository("mettlTeeDAO")
public class MettlTeeDAO extends BaseDAO{

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getExamBookingByMettlResponseInfoHistory(MettlHookResponseBean mettlHookResponseBean) {	
		try {	
			jdbcTemplate = new JdbcTemplate(dataSource);	
			//Get list of failed subjects	
				
			String sql = ""	
				+ " SELECT * FROM `exam`.`exams_pg_scheduleinfo_history_mettl` "	
				+ " WHERE `acessKey` = ? AND `emailId` = ? ";	
				
			MettlSSOInfoBean booking = jdbcTemplate.queryForObject(	
				sql, 	
				new Object[]{ 	
					mettlHookResponseBean.getInvitation_key(), mettlHookResponseBean.getEmail()	
				},	
				new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)	
			);	
			return booking;	
		}	
		catch (Exception e) {	
			// TODO: handle exception	
			e.printStackTrace();	
			return null;	
		}	
	}
	
	public void updateExamAttendanceStatus(String lastModifiedBy, String status, MettlSSOInfoBean booking,String finish_node,String request) {
 		jdbcTemplate = new JdbcTemplate(dataSource);
 		String sql = ""
 				 + " UPDATE `exam`.`exams_pg_scheduleinfo_mettl` "
 								+ " SET `testTaken` = ?,`startJsonResponse` = ?"
 								+ " WHERE acessKey =? and sapid=?";
 						jdbcTemplate.update(sql, new Object[] { 
 								status,request ,booking.getAcessKey(),booking.getSapid()
 						});
	}
	

	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getExamStatus(ExamBookingTransactionBean examBookingTransactionBean) {	
		try {	
			jdbcTemplate = new JdbcTemplate(dataSource);	
			//Get list of failed subjects	
				
//			String sql = ""	
//					+ " select * from ( SELECT * FROM `exam`.`exams_pg_scheduleinfo_mettl` UNION ALL SELECT * FROM `exam`.`exams_pg_scheduleinfo_history_mettl`) as examStatus"	
//					+ " WHERE `sapid` = ? and `subject` = ? and `examStartDateTime` = ? limit 1";	
			
			String sql = ""	
					+ " select * from ( SELECT * FROM `exam`.`exams_pg_scheduleinfo_mettl` ) as examStatus"	
					+ " WHERE `sapid` = ? and `subject` = ? and `examStartDateTime` = ? limit 1";	
			
			List<ExamBookingTransactionBean> examBookingTransactionBeans = jdbcTemplate.query(	
				sql, 	
				new Object[]{ 	
					examBookingTransactionBean.getSapid(),	
					examBookingTransactionBean.getSubject(),	
					examBookingTransactionBean.getExamDate() + " " + examBookingTransactionBean.getExamTime()	
				},	
				new BeanPropertyRowMapper<ExamBookingTransactionBean>(ExamBookingTransactionBean.class)	
			);	
			return examBookingTransactionBeans;	
		}	
		catch (Exception e) {	
			// TODO: handle exception	
			e.printStackTrace();	
			return null;	
		}	
	}
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getExamBookingForStudent(MettlSSOInfoBean input) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
			+ " SELECT * FROM `exam`.`exambooking_mettl_schedule_info` "
			+ " WHERE `subject` = ? AND `sapid` = ? AND `month` = ? AND `year` = ? ";
		
		MettlSSOInfoBean booking = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				input.getSubject(), input.getSapid(), input.getMonth(), input.getYear()
			},
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
		
		return booking;
	}
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getExamBookingInfoForStudent(MettlSSOInfoBean input) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
			+ " SELECT *, CONCAT(`examDate`,' ', `examTime`) AS `examStartDateTime` "
			+ " FROM `exam`.`exambookings` "
			+ " WHERE `subject` = ? AND `sapid` = ? AND `month` = ? AND `year` = ? AND `booked` = 'Y' ";
		
		MettlSSOInfoBean booking = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				input.getSubject(), input.getSapid(), input.getMonth(), input.getYear()
			},
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
		
		return booking;
	}
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getScheduleInfoForStudent(MettlSSOInfoBean input) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
			+ " SELECT * FROM `exam`.`exams_pg_scheduleinfo_mettl` "
			+ " WHERE `subject` = ? AND `sapid` = ? AND `month` = ? AND `year` = ? ";
		
		MettlSSOInfoBean booking = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				input.getSubject(), input.getSapid(), input.getMonth(), input.getYear()
			},
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
		
		return booking;
	}

	@Transactional(readOnly = true)
	public String getExamCenterGoogleMapUrl(String examCenterName, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT  " + 
				"    googleMapUrl " + 
				"FROM " + 
				"    exam.examcenter " + 
				"WHERE " + 
				"    examCenterName = ? " + 
				"        AND year = ? " + 
				"        AND month = ? ";
		
		String googleMapUrl = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ 
						examCenterName, year, month
				},
				new SingleColumnRowMapper<String>(String.class)
				);
		
		return googleMapUrl;
	}

	@Transactional(readOnly = true)
	public MettlSSOInfoBean getExamCenterGoogleMapUrl(Integer centerId, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
				+ " SELECT  " + 
				"    examCenterName, googleMapUrl " + 
				"FROM " + 
				"    exam.examcenter " + 
				"WHERE " + 
				"    centerId = ? " + 
				"        AND year = ? " + 
				"        AND month = ? ";
		
		MettlSSOInfoBean bean = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ 
						centerId, year, month
				},
				new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
				);
		
		return bean;
	}
	
	@Transactional(readOnly = true)
	public List<MettlSSOInfoBean> getTestExamBookingBean(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
				+ " SELECT * FROM `exam`.`exams_pg_scheduleinfo_mettl` "
				+ " WHERE `sapid` = ? ";

		return jdbcTemplate.query(
			sql, new Object[] {sapid}, 
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<ExamCenterSlotMappingBean> getListOfUpcomingExamsForScheduler() {

		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = " "
//				+ " SELECT * FROM `exam`.`examcenter_slot_mapping` "
//				/* added extra time just in case */
//				+ " WHERE TIME_TO_SEC(TIMEDIFF( CAST(CONCAT(date, ' ', starttime) AS DATETIME),SYSDATE())) < 9000 "
//				+ " AND TIME_TO_SEC(TIMEDIFF( CAST(CONCAT(date, ' ', starttime) AS DATETIME),SYSDATE())) > 0 ";
		
		String sql = "SELECT " + 
				"    map.* " + 
				"FROM " + 
				"    `exam`.`examcenter_slot_mapping` map " + 
				"        INNER JOIN " + 
				"    exam.examcenter c ON c.centerId = map.examcenterId " + 
				"WHERE " + 
				"    TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(map.date, ' ', map.starttime) AS DATETIME), " + 
				"                    SYSDATE())) < 9000 " + 
				"        AND TIME_TO_SEC(TIMEDIFF(CAST(CONCAT(map.date, ' ', map.starttime) AS DATETIME), " + 
				"                    SYSDATE())) > 0 " + 
				"        AND c.examCenterName = 'AT MY LOCATION' ";

		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<ExamCenterSlotMappingBean>(ExamCenterSlotMappingBean.class)
		);
	
	}
	
	@Transactional(readOnly = true)
	public List<MettlSSOInfoBean> getBookingsForSlot(ExamCenterSlotMappingBean slot) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
				+ " SELECT * "
				+ " FROM `exam`.`exams_pg_scheduleinfo_mettl` "
				+ " WHERE `examStartDateTime` = ? ";

		return jdbcTemplate.query(
			sql,
			new Object[] {
				slot.getDate() + " " + slot.getStarttime()
			},
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getExamBookingByMettlResponseInfo(MettlHookResponseBean mettlHookResponseBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT * FROM `exam`.`exams_pg_scheduleinfo_mettl` "
			+ " WHERE `acessKey` = ? AND `emailid` = ? ";
		
		MettlSSOInfoBean booking = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				mettlHookResponseBean.getInvitation_key(), mettlHookResponseBean.getEmail()
			},
			new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
		);
		return booking;
	}
	

	public void updateResumeExamAttendanceStatus(String lastModifiedBy, String status, MettlSSOInfoBean booking,String finish_node,String request) {	
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql = ""	
				+ " UPDATE `exam`.`exams_pg_scheduleinfo_mettl` "	
				+ " SET `testTaken` = ?,`resumeJsonResponse` = ?"	
				+ " WHERE acessKey =? and sapid=?";	
		jdbcTemplate.update(sql, new Object[] { 	
				status,request ,booking.getAcessKey(),booking.getSapid()	
		});	
	}
	

	public void updateGradedExamAttendanceStatus(String lastModifiedBy, String status, MettlSSOInfoBean booking,String finish_node,String request) {	
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql = ""	
				+ " UPDATE `exam`.`exams_pg_scheduleinfo_mettl` "	
				+ " SET `gradedJsonResponse` = ?"	
				+ " WHERE acessKey =? and sapid=?";	
		jdbcTemplate.update(sql, new Object[] { 	
				request ,booking.getAcessKey(),booking.getSapid()	
		});	
	}
	

	public void updateEndExamAttendanceStatus(String lastModifiedBy, String status, MettlSSOInfoBean booking,String finish_node,String request) {	
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql = ""	
				+ " UPDATE `exam`.`exams_pg_scheduleinfo_mettl` "	
				+ " SET `testTaken` = ?,`finishedJsonResponse` = ?,`finish_node` = ?"	
				+ " WHERE acessKey =? and sapid=?";	
		jdbcTemplate.update(sql, new Object[] { 	
				status,request, finish_node,booking.getAcessKey(),booking.getSapid()	
		});	
	}
	
	@Transactional(readOnly = false)
	public String getExamStatusCount(String startDateTime,String testTaken) {	
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql;	
		if(testTaken == null) {	
			sql = "SELECT count(*) as total FROM exam.exams_pg_scheduleinfo_mettl where examStartDateTime = ? and testTaken IS NULL;";	
			return (String) jdbcTemplate.queryForObject(sql, new Object[] { 	
					startDateTime	
				},String.class);	
		}	
		else {	
			sql = "SELECT count(*) as total FROM exam.exams_pg_scheduleinfo_mettl where examStartDateTime = ? and testTaken=?;";	
			return (String) jdbcTemplate.queryForObject(sql, new Object[] { 	
					startDateTime,testTaken	
				},String.class);	
		}	
			
	}
	
	@Transactional(readOnly = true)
	public List<MettlSSOInfoBean> getPGScheduleData(String fromdate,String todate){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.exams_pg_scheduleinfo_mettl where examStartDateTime >= ? and examStartDateTime <= ?";
			return jdbcTemplate.query(sql,new Object[] {fromdate,todate},new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return new ArrayList<MettlSSOInfoBean>();
		}
	}
	
	public boolean moveDataToHistory() {	
		try {	
			jdbcTemplate = new JdbcTemplate(dataSource);	
			String sql = "insert into exam.exams_pg_scheduleinfo_history_mettl select * from exam.exams_pg_scheduleinfo_mettl";	
			jdbcTemplate.execute(sql);	
			return true;	
		}	
		catch (Exception e) {	
			// TODO: handle exception	
			return false;	
		}	
	}
	
	public boolean insertIntoPGScheduleInfo(MettlSSOInfoBean bean) {	
		jdbcTemplate = new JdbcTemplate(dataSource);	
		try {	
			String sql = "insert into exam.exams_pg_scheduleinfo_mettl(`subject`,`year`,`month`,`trackId`,`sapid`,`testTaken`,`firstname`,`lastname`,`emailId`,`examStartDateTime`,`examEndDateTime`,`accessStartDateTime`,`accessEndDateTime`,`sifySubjectCode`,`scheduleName`,`acessKey`,`joinURL`,`createdBy`,`createdDateTime`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY "	
					+ "    UPDATE `firstname` = ?,`lastname` = ?,`emailId` = ?";	
			jdbcTemplate.update(sql,new Object[] {	
					bean.getSubject(),	
					bean.getYear(),	
					bean.getMonth(),	
					bean.getTrackId(),	
					bean.getSapid(),	
					bean.getTestTaken(),	
					bean.getFirstname(),	
					bean.getLastname(),	
					bean.getEmailId(),	
					bean.getExamStartDateTime(),	
					bean.getExamEndDateTime(),	
					bean.getAccessStartDateTime(),	
					bean.getAccessEndDateTime(),	
					bean.getSifySubjectCode(),	
					bean.getScheduleName(),	
					bean.getAcessKey(),	
					bean.getJoinURL(),	
					bean.getCreatedBy(),	
					bean.getCreatedDateTime(),	
					//update logic	
					bean.getFirstname(),	
					bean.getLastname(),	
					bean.getEmailId()	
			});	
			return true;	
		}	
		catch (Exception e) {	
			// TODO: handle exception	
			e.printStackTrace();	
			return false;	
		}	
	}
	
	@Transactional(readOnly = false)
	public boolean createPGAssessment(String name,String sifyCode,int assessmentId, int duration) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			
			String sql = "INSERT INTO `exam`.`pg_assessment` (`assessmentId`, `name`, `sifyCode`,`duration`) VALUES ( ?, ?, ?, ?) ";
			
			int updated = jdbcTemplate.update(sql,new Object[] {
					assessmentId,
					name,
					sifyCode,
					duration
			});
			
			return updated  > 0;
	}

	public List<ExamScheduleinfoBean> getRegularStudentByExamStartTime() throws Exception{
		// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql= "	SELECT																			" + 
					"	sapid,subject,timebound_id,emailId,examStartDateTime,schedule_id,assessmentName	" +
					"	FROM																			" +
				    "	exam.exams_scheduleinfo_mettl 													" + 
					" 	WHERE TIME_TO_SEC(TIMEDIFF(CAST(examStartDateTime AS DATETIME),					" + 
					"				                    SYSDATE())) < 9000  							" + 
					"   AND TIME_TO_SEC(TIMEDIFF(CAST(examStartDateTime AS DATETIME),   				" + 
					"				                   SYSDATE())) > 5400  								" + 
					"   AND max_score != '100' 															" ; 
//				" AND (sapid , timebound_id) not  " + 
//				" IN ( SELECT sapid, timebound_id FROM exam.exams_scheduleinfo_mettl where testTaken='Attempted' GROUP BY sapid , timebound_id HAVING COUNT(testTaken) > 0) ";
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamScheduleinfoBean>(ExamScheduleinfoBean.class));
	}
	
	public List<ExamScheduleinfoBean> getRegularLastExamAttemptdStudent() throws Exception{
		// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);	
		String sql= "	SELECT 						    " + 
					"   sapid, timebound_id 			" + 
					"	FROM 							" + 
					"	exam.exams_scheduleinfo_mettl	" + 
					"	WHERE						    " + 
					"   testTaken = 'Attempted'		    " + 
					"	GROUP BY sapid , timebound_id   " + 
					"	HAVING COUNT(testTaken) > 0	   	";
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamScheduleinfoBean>(ExamScheduleinfoBean.class));
	}
	
	

	public List<ExamScheduleinfoBean> getResitStudentByExamStartTime() throws Exception{
		// TODO Auto-generated method stub
		jdbcTemplate = new JdbcTemplate(dataSource);	
		
		String sql= "	 SELECT  																											" + 
					"    esm.sapid, esm.subject, esm.timebound_id, esm.emailId, esm.examStartDateTime, esm.schedule_id, esm.assessmentName	" + 
					"	 FROM 																												" + 
					"    exam.exams_scheduleinfo_mettl esm								 			   			 	  					    " + 
					"	 WHERE 																												" + 
					"    TIME_TO_SEC(TIMEDIFF(CAST(esm.examStartDateTime AS DATETIME), 														" + 
					"                    SYSDATE())) < 9000								    												" + 
					"    AND TIME_TO_SEC(TIMEDIFF(CAST(esm.examStartDateTime AS DATETIME),  												" + 
					"                    SYSDATE())) > 5400								    												" + 
					"    AND esm.max_score = '100'										    												";
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamScheduleinfoBean>(ExamScheduleinfoBean.class));
	}
}
