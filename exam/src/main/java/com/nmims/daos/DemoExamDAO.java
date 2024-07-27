package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.DemoExamBean;
import com.nmims.beans.MbaWxDemoExamAttendanceBean;
import com.nmims.beans.MbaWxDemoExamKeysBean;
import com.nmims.beans.MbaWxDemoExamScheduleDetailBean;
import com.nmims.beans.StudentExamBean;

@Repository("demoExamDAO")
public class DemoExamDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	public static final Logger demoExamMbaWXlogger = LoggerFactory.getLogger("demoExamCreationMbaWX");
	
	public boolean insertDemoExam(DemoExamBean demoExamBean) {
		try {
			String sql_select = "select count(subject) from `exam`.`demoexam_keys` where subject = ? ";
			String sql_update = "update `exam`.`demoexam_keys` set link=? where subject=?";
			String sql_insert = "insert into `exam`.`demoexam_keys`(subject,link,lastmodified_by) values(?,?,?)";
			int count = jdbcTemplate.queryForObject(sql_select,new Object[] {demoExamBean.getSubject()},Integer.class);
			int status = 0;
			if(count > 0) {
				status = jdbcTemplate.update(sql_update, new Object[] {
					demoExamBean.getLink(),
					demoExamBean.getSubject()
				});
			}else {
				status = jdbcTemplate.update(sql_insert, new Object[] {
					demoExamBean.getSubject(),
					demoExamBean.getLink(),
					demoExamBean.getLastmodified_by()
				});
			}
			
			if(status == 1) {
				
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean checkInsideProgramSemSubject(DemoExamBean demoExamBean) {
		try {
			String sql = "select subject from exam.program_sem_subject where subject = ? and sifySubjectCode = ?";
			List<String> subjectList = jdbcTemplate.query(sql, new Object[] {
				demoExamBean.getSubject(),
				demoExamBean.getSubject_code()
			},new SingleColumnRowMapper<String>(String.class));
			if(subjectList.size() > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	
	@Transactional(readOnly = true)
	public List<String> getSubjectList(){
		try{
			String sql = "SELECT distinct subject FROM exam.program_subject where subject not in (select subject from exam.demoexam_keys);";
			return jdbcTemplate.query(sql,new SingleColumnRowMapper<String>(String.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return new ArrayList<String>();
		}
	}
	
	/*
	 @Transactional(readOnly = true)
	 public String getAttendStudentCount(String authorizedCenterCodes) {
		try {
			String sql = "select count(*) as total from (SELECT s.sapid,s.firstName,s.lastName,s.mobile,s.emailId,count(*) as total FROM exam.demoexam_attendance d_a inner join exam.students s on s.sapid = d_a.sapid where ";
			if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
				sql = sql + " s.centerCode in (" + authorizedCenterCodes + ") and ";
			}
			sql = sql + " markAttend = 'Y' group by d_a.sapid) s";
			return jdbcTemplate.queryForObject(sql, String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}*/
	
	
	/*
	 @Transactional(readOnly = true)
	 public List<StudentBean> getAttendStudent(String authorizedCenterCodes) {
		try {
			String sql = "SELECT s.sapid,s.firstName,s.lastName,s.mobile,s.emailId,count(*) as total FROM exam.demoexam_attendance d_a inner join exam.students s on s.sapid = d_a.sapid where ";
			if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
				sql = sql + " s.centerCode in (" + authorizedCenterCodes + ") and ";
			}
			sql = sql + " markAttend = 'Y' group by d_a.sapid";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentBean>(StudentBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}*/
	
	/*
	 @Transactional(readOnly = true) 
	 public String getNotAttendStudentCount(String authorizedCenterCodes,String year,String month) {
		try {
			String sql = "select count(*) as total from "
					+ "( SELECT eb.sapid, s.firstName, s.lastName, s.emailId "
					+ "FROM exam.exambookings eb INNER JOIN exam.students s "
					+ "ON s.sapid = eb.sapid LEFT JOIN exam.demoexam_attendance da "
					+ "ON eb.sapid = da.sapid WHERE (markAttend = 'N' OR markAttend IS NULL) "
					+ "AND eb.year =? AND eb.month=? AND eb.booked = 'Y' AND "
					+ "eb.centerId <> -1 AND eb.subject not IN "
					+ "('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', "
					+ "'Simulation: Mimic Social') ";
			
				if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
					sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				}
				sql = sql + " GROUP BY eb.sapid ) as sapid;";
			return jdbcTemplate.queryForObject(sql,new Object[] {year,month}, String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}*/
	
	@Transactional(readOnly = true)
	public List<String> getAttendStudentDemoExamAttendanceSapids() {
		try {
			String sql = "SELECT sapid FROM exam.demoexam_attendance d_a where markAttend = 'Y'";
			return jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	

	@Transactional(readOnly = true)
	public List<DemoExamBean> getAttendStudentDemoExamAttendanceSapidsAndLatestDateTime() throws Exception{
		
			String sql = "    SELECT  " + 
					"    sapid, " + 
					"    MAX(created_at) AS latestAttemptDateTime, " + 
					"    COUNT(sapid) AS count " + 
					"FROM " + 
					"    exam.demoexam_attendance d_a " + 
					"WHERE " + 
					"    markAttend = 'Y' " + 
					"GROUP BY sapid";
			
			List<DemoExamBean> listOfAttendStudentDemoExamAttendanceSapidsAndLatestDateTime = jdbcTemplate.query(sql, new BeanPropertyRowMapper<DemoExamBean>(DemoExamBean.class));
			return listOfAttendStudentDemoExamAttendanceSapidsAndLatestDateTime;			
	}
	
	@Transactional(readOnly = true)
	public List<StudentExamBean> getCurrentCycleExamBookedStudentList(String authorizedCenterCodes,String year,String month){
		try {
			String sql = "SELECT eb.sapid, s.firstName, s.lastName, s.emailId,s.mobile,s.centerCode FROM exam.exambookings eb INNER JOIN exam.students s ON s.sapid = eb.sapid WHERE eb.year = ? AND eb.month = ? AND eb.booked = 'Y' AND eb.centerId <> - 1 AND eb.subject NOT IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') ";
			if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
				sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			}
			sql = sql + " GROUP BY eb.sapid;";
			return jdbcTemplate.query(sql,new Object[] {year,month}, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	
	/*
	 @Transactional(readOnly = true) 
	 public List<StudentBean> getNotAttendStudent(String authorizedCenterCodes,String year,String month) {
		try {
			String sql = "SELECT eb.sapid, s.firstName, s.lastName, s.emailId,s.mobile "
					+ "FROM exam.exambookings eb INNER JOIN exam.students s "
					+ "ON s.sapid = eb.sapid LEFT JOIN exam.demoexam_attendance da "
					+ "ON eb.sapid = da.sapid WHERE "
					+ "(markAttend = 'N' OR markAttend IS NULL) AND eb.year =? AND "
					+ "eb.month=? AND eb.booked = 'Y' AND eb.centerId <> -1 AND "
					+ "eb.subject not IN "
					+ "('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', "
					+ "'Simulation: Mimic Social') ";
			if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
				sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			}
			sql = sql + " GROUP BY eb.sapid";
			return jdbcTemplate.query(sql,new Object[] {year,month}, new BeanPropertyRowMapper<StudentBean>(StudentBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}*/
	
	@Transactional(readOnly = true)
	public List<DemoExamBean> getDemoExamList() {
		try {
			String sql = "select * from `exam`.`demoexam_keys` where active = 1";
			return (List<DemoExamBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<DemoExamBean>(DemoExamBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return new ArrayList<DemoExamBean>();
		}
	}
	
	public boolean updateDemoExam(DemoExamBean demoExamBean) {
		try {
			String sql = "update `exam`.`demoexam_keys` set subject=?,link=?,lastmodified_by=? where id=?";
			int status = jdbcTemplate.update(sql, new Object[] {
				demoExamBean.getSubject(),
				demoExamBean.getLink(),
				demoExamBean.getLastmodified_by(),
				demoExamBean.getId()
			});
			if(status == 1) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	public boolean deleteDemoExam(DemoExamBean demoExamBean) {
		try {
			String sql = "delete from `exam`.`demoexam_keys` where id=?";
			int status = jdbcTemplate.update(sql, new Object[] {
					demoExamBean.getId()
			});
			if(status == 1) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	public boolean createExamAttendance(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "insert into `exam`.`demoexam_attendance`(`sapid`,`demoExamId`,`accessKey`) values(?,?,?)";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getDemoExamId(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	public boolean updateStartExamAttendance(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.demoexam_attendance set startedTime=? where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getStartedTime(),demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	public boolean updateEndExamAttendance(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.demoexam_attendance set endTime=?, markAttend=? where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getEndTime(),demoExamAttendanceBean.getMarkAttend(),demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	public boolean updateEndExamAttendanceByBatchJob(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.demoexam_attendance set status=?, endTime=?, markAttend=? where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getStatus(), demoExamAttendanceBean.getEndTime(),demoExamAttendanceBean.getMarkAttend(),demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public DemoExamAttendanceBean getAttendanceData(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "select * from `exam`.`demoexam_attendance` where sapid=? and accessKey=?";
			return (DemoExamAttendanceBean) jdbcTemplate.queryForObject(sql, new Object[] {demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()},DemoExamAttendanceBean.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DemoExamAttendanceBean> getPendingAttendanceData() {
		try {
			String sql = "select s.emailId,d.* from `exam`.`demoexam_attendance` d left join (select * from `exam`.`students` s where programStatus IS NULL or programStatus = '' ) s on s.sapid = d.sapid where d.markAttend IS NULL and d.created_at > '2020-11-01 00:00:00';";
			return (ArrayList<DemoExamAttendanceBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<DemoExamAttendanceBean>(DemoExamAttendanceBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<String> getAttendanceList(String sapId) {
		try {
			String sql = "select * from (SELECT demoExamId FROM `exam`.`demoexam_attendance_history` where sapid=? and markAttend='Y' union all select demoExamId from `exam`.`demoexam_attendance` where sapid=? and markAttend='Y') as demoExam group by demoExamId;";
			return jdbcTemplate.query(sql, new Object[] {sapId,sapId},new SingleColumnRowMapper(String.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getStudentByEmailId(String emailId) {
		try {
			String sql = "select * from exam.students where emailId = ? order by createdDate desc limit 1";
			return (StudentExamBean) jdbcTemplate.queryForObject(sql,new Object[] {emailId}, new BeanPropertyRowMapper(StudentExamBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<DemoExamBean> getCurrentCycleExamBookedStudentsList(String authorizedCenterCodes,String year,String month)throws Exception{
		
			String sql = "SELECT  " + 
					"    eb.sapid, " + 
					"    s.firstName, " + 
					"    s.lastName, " + 
					"    s.emailId, " + 
					"    s.mobile, " + 
					"    s.centerCode " + 
					"FROM " + 
					"    exam.exambookings eb " + 
					"        INNER JOIN " + 
					"    exam.students s ON s.sapid = eb.sapid " + 
					"WHERE " + 
					"    eb.year = ? AND eb.month = ? " + 
					"        AND eb.booked = 'Y' " + 
					"        AND eb.centerId <> - 1 " + 
					"        AND eb.subject NOT IN ('Project' , 'Module 4 - Project', " + 
					"        'Simulation: Mimic Pro', " + 
					"        'Simulation: Mimic Social')";
			if (authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())) {
				sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			}
			sql = sql + " GROUP BY eb.sapid;";
			List<DemoExamBean> listOfCurrentCycleexamBookingStudent = jdbcTemplate.query(sql,new Object[] {year,month}, new BeanPropertyRowMapper<DemoExamBean>(DemoExamBean.class));
			return listOfCurrentCycleexamBookingStudent;
		
	}
	
	
	
	@Transactional(readOnly = true)
	public List<MbaWxDemoExamAttendanceBean> getDemoExamAttendanceRecords(String sapid) {
		List<MbaWxDemoExamAttendanceBean> attendanceList = new ArrayList<MbaWxDemoExamAttendanceBean>();
		String sql="select * from exam.mba_wx_demoexam_attendance where sapid= ? and markAttend='Y' ";
		attendanceList = jdbcTemplate.query(sql, new Object[] {sapid},new BeanPropertyRowMapper<MbaWxDemoExamAttendanceBean>(MbaWxDemoExamAttendanceBean.class));
		return attendanceList;
	}
	
	@Transactional(readOnly = true)
	public List<MbaWxDemoExamKeysBean> getDemoExamKeyRecords(String acadYear,String acadMonth,String program) {
		List<MbaWxDemoExamKeysBean> demoExamKeyList = new ArrayList<MbaWxDemoExamKeysBean>();
		String sql="SELECT * FROM exam.mba_wx_demoexam_keys where acadYear = ? AND acadMonth = ? AND program = ? order by id ";
		demoExamKeyList = jdbcTemplate.query(sql, new Object[] {acadYear,acadMonth,program},new BeanPropertyRowMapper<MbaWxDemoExamKeysBean>(MbaWxDemoExamKeysBean.class));
		return demoExamKeyList;
	}
	
	@Transactional(readOnly = true)
	public MbaWxDemoExamScheduleDetailBean  getStudentDetails(String sapid) {
		MbaWxDemoExamScheduleDetailBean student = new MbaWxDemoExamScheduleDetailBean();
		String sql="select * from exam.students st where st.sapid= ? and st.sem=(select max(sem) from exam.students stud where stud.sapid=st.sapid)";
		student = jdbcTemplate.queryForObject(sql,new Object[] {sapid}, new BeanPropertyRowMapper<MbaWxDemoExamScheduleDetailBean>(MbaWxDemoExamScheduleDetailBean.class));
		return student;
	}
	
	@Transactional(readOnly = false)
	public MbaWxDemoExamAttendanceBean insertIntoDemoExamKeys(MbaWxDemoExamScheduleDetailBean dbBean) {
		MbaWxDemoExamAttendanceBean bean = new MbaWxDemoExamAttendanceBean();
		jdbcTemplate = new JdbcTemplate(dataSource); 
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			final String sql = "insert into exam.mba_wx_demoexam_keys (subject, accessKey, link, acadYear, acadMonth, program, lastmodified_by, createdBy, createdTime, lastModifiedTime) "
					   + " values (?,?,?,?,?,?,?,?,sysdate(),sysdate())";
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					statement.setString(1, dbBean.getScheduleName());
					statement.setString(2, dbBean.getScheduleAccessKey());
					statement.setString(3, dbBean.getScheduleAccessUrl());
					statement.setString(4, dbBean.getAcadYear());
					statement.setString(5, dbBean.getAcadMonth());
					statement.setString(6, dbBean.getProgram());
					statement.setString(7, dbBean.getSapid());
					statement.setString(8, dbBean.getSapid());
					
					return statement;
				}
			},keyHolder);
			
			bean.setId(String.valueOf(keyHolder.getKey().toString()));
			bean.setMessage("success");
		}catch(Exception e) {
			//e.printStackTrace();
			demoExamMbaWXlogger.info("Exception:"+e);
			bean.setMessage(e.getMessage()+"");
		}
		return bean;
		
	}
	
	@Transactional(readOnly = false)
	public void createAttendanceForDemoExamMBAWX(MbaWxDemoExamScheduleDetailBean scheduleAndSapidDetail) {
		String sql = "insert into exam.mba_wx_demoexam_attendance (sapid, demoExamId, accessKey,created_at, updated_at, createdBy, lastModifiedBy) "
				 + " values (?,?,?,sysdate(),sysdate(),?,?)  ON DUPLICATE KEY UPDATE demoExamId = ? ,createdBy = ? , lastModifiedBy = ?, updated_at=sysdate() ";
		jdbcTemplate.update(sql,new Object[] {scheduleAndSapidDetail.getSapid(),scheduleAndSapidDetail.getScheduleId(),scheduleAndSapidDetail.getScheduleAccessKey(),scheduleAndSapidDetail.getSapid(),scheduleAndSapidDetail.getSapid(),scheduleAndSapidDetail.getScheduleId(),scheduleAndSapidDetail.getSapid(),scheduleAndSapidDetail.getSapid()});
	}
	
	@Transactional(readOnly = false)
	public boolean updateStartExamAttendanceMbaWX(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.mba_wx_demoexam_attendance set startedTime=sysdate(),updated_at=sysdate() where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			demoExamMbaWXlogger.info("Exception:"+e);
			throw e;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean updateEndExamAttendanceMbaWX(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.mba_wx_demoexam_attendance set endTime=sysdate(),updated_at=sysdate(), markAttend=? where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getMarkAttend(),demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			demoExamMbaWXlogger.info("Exception:"+e);
			throw e;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DemoExamAttendanceBean> getPendingAttendanceDataMbaWX(ArrayList<String> sapidList) {
		ArrayList<DemoExamAttendanceBean> demoExamPendingList = new ArrayList<DemoExamAttendanceBean>();
		try {
			String sapid="";
			String commaSeparatedsapid="";
			for(int i=0;i<=sapidList.size()-2;i++)
			{
				sapid=sapidList.get(i);
				commaSeparatedsapid = commaSeparatedsapid + sapid + ",";
			}
			commaSeparatedsapid = commaSeparatedsapid + sapidList.get(sapidList.size()-1);
			
			String sql = "SELECT" + 
					"   accessKey,sapid" + 
					"	FROM" + 
					"   exam.mba_wx_demoexam_attendance" + 
					"	WHERE" + 
					"   markAttend IS NULL "+
					" 	AND sapid in("+commaSeparatedsapid+")";
			demoExamPendingList = (ArrayList<DemoExamAttendanceBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<DemoExamAttendanceBean>(DemoExamAttendanceBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			demoExamMbaWXlogger.info("Exception:"+e);
			throw e;
		}
		return demoExamPendingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DemoExamAttendanceBean> getStudentsByProgramMbaWX(ArrayList<Integer> consumerProgramStructureIdList) {
		ArrayList<DemoExamAttendanceBean> demoExamStudentList = new ArrayList<DemoExamAttendanceBean>();
		try {
			
			String cpsid="";
			String commaSeparatedCPSid="";
			for(int i=0;i<=consumerProgramStructureIdList.size()-2;i++)
			{
				cpsid=Integer.toString(consumerProgramStructureIdList.get(i));
				commaSeparatedCPSid = commaSeparatedCPSid + cpsid + ",";
			}
			commaSeparatedCPSid = commaSeparatedCPSid + consumerProgramStructureIdList.get(consumerProgramStructureIdList.size()-1);
			
			String sql = "SELECT" + 
					"    emailId,sapid" + 
					"	FROM" + 
					"    exam.students" + 
					"	WHERE" + 
					"   (programStatus IS NULL OR programStatus = '') AND consumerProgramStructureId in ("+commaSeparatedCPSid+")";
			demoExamStudentList = (ArrayList<DemoExamAttendanceBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<DemoExamAttendanceBean>(DemoExamAttendanceBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			demoExamMbaWXlogger.info("Exception:"+e);
			throw e;
		}
		return demoExamStudentList;
	}
	
	@Transactional(readOnly = false)
	public boolean updateEndExamAttendanceByBatchJobMbaWX(DemoExamAttendanceBean demoExamAttendanceBean) {
		try {
			String sql = "update exam.mba_wx_demoexam_attendance set status=?, endTime=?, markAttend=?,updated_at=sysdate() where sapid=? and accessKey=?";
			jdbcTemplate.update(sql,new Object[] {demoExamAttendanceBean.getStatus(), demoExamAttendanceBean.getEndTime(),demoExamAttendanceBean.getMarkAttend(),demoExamAttendanceBean.getSapid(),demoExamAttendanceBean.getAccessKey()});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			demoExamMbaWXlogger.info("Exception for student:"+demoExamAttendanceBean.getSapid()+"-"+demoExamAttendanceBean.getEmailId()+"-"+e);
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<DemoExamAttendanceBean> getDemoExamStatusForMbaWXStudent(String sapid) {
		String sql="select sapid,demoExamId,accessKey,startedTime,endTime,markAttend from exam.mba_wx_demoexam_attendance where sapid= ? ";
		return (ArrayList<DemoExamAttendanceBean>)jdbcTemplate.query(sql, new Object[] {sapid},new BeanPropertyRowMapper<DemoExamAttendanceBean>(DemoExamAttendanceBean.class));
	}
	
}
