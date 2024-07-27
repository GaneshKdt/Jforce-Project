package com.nmims.daos;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.MBAScheduleInfoBean;

import com.nmims.beans.MettlRegisterCandidateBeanMBAWX;

@Repository("mbaWXTeeDAO")
public class MBAWXTeeDAO extends BaseDAO{

	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(MBAWXTeeDAO.class);

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public MBAScheduleInfoBean getScheduleInfo(MBAScheduleInfoBean inputInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT "
				+ " UNIX_TIMESTAMP(`s`.`exam_start_date_time`) AS `startTimestamp`, "
				+ " UNIX_TIMESTAMP(COALESCE(`s`.`reporting_start_date_time`,`s`.`exam_start_date_time`)) AS `reportingStartTimeStamp`, "
				+ " UNIX_TIMESTAMP(`s`.`exam_end_date_time`) AS `endTimestamp`, "
				+ " `pss`.`subject`, "
				+ " `s`.`timebound_id` AS `timeboundId`, "
				+ " `s`.`schedule_id` AS `scheduleId`, "
				+ " `s`.`max_score` AS `maxMarks`, "
				+ " `a`.`name` AS `testName`, "
				+ " `a`.`duration`  "
			+ " FROM `exam`.`exams_schedule` `s` "
				
			+ " LEFT JOIN `exam`.`exams_assessments` `a` "
			+ " ON `a`.`id` = `s`.`assessments_id` "

			+ " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			+ " ON `ssc`.`id` = `s`.`timebound_id` "

			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` "
			+ " ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "
			
			+ " WHERE `s`.`timebound_id` = ? AND `s`.`schedule_id` = ? ";
		

		MBAScheduleInfoBean scheduleInfo = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				inputInfo.getTimeboundId(), inputInfo.getScheduleId()
			},
			new BeanPropertyRowMapper<MBAScheduleInfoBean>(MBAScheduleInfoBean.class)
		);
		scheduleInfo.setSapid(inputInfo.getSapid());
		return scheduleInfo;
	}
	
	@Transactional(readOnly = true)
	public MBAScheduleInfoBean getScheduleInfoFromTempTable(MBAScheduleInfoBean inputInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		MBAScheduleInfoBean scheduleInfo = new MBAScheduleInfoBean();
		try
		{
			String sql = ""+
				"SELECT " + 
				"UNIX_TIMESTAMP(sm.accessStartDateTime) AS `startTimestamp`, "+
				"UNIX_TIMESTAMP(COALESCE(sm.reporting_start_date_time,sm.accessStartDateTime)) AS `reportingStartTimeStamp`, " + 
				"UNIX_TIMESTAMP(sm.accessEndDateTime) AS `endTimestamp`, " + 
				"sm.subject, " + 
				"sm.timebound_id AS `timeboundId`, " + 
				"sm.schedule_id AS `scheduleId`, " + 
				"sm.max_score AS `maxMarks`, " + 
				"sm.assessmentName AS `testName`, " + 
				"sm.assessmentDuration AS `duration` " + 
				"from exam.exams_scheduleinfo_mettl sm " + 
				"WHERE sm.timebound_id = ? AND sm.schedule_id = ? AND sm.sapid = ? ";
	
			scheduleInfo = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ 
					inputInfo.getTimeboundId(), inputInfo.getScheduleId(), inputInfo.getSapid()
				},
				new BeanPropertyRowMapper<MBAScheduleInfoBean>(MBAScheduleInfoBean.class)
			);
			scheduleInfo.setSapid(inputInfo.getSapid());
			return scheduleInfo;
		}
		catch(Exception e)
		{
			return scheduleInfo;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamTakenByStudent(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`tee_marks` "
			+ " WHERE `timebound_id` = ? AND `sapid` = ? AND `status` in ('Attempted', 'RIA', 'AB', 'NV', 'CC')";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
					scheduleInfo.getTimeboundId(), scheduleInfo.getSapid() 
			},
			Integer.class
		);
		return count == 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamTakenByStudentFromTempTable(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql =  "SELECT count(*)"
				  +" FROM  exam.exams_scheduleinfo_mettl "
				  +" WHERE timebound_id = ? AND sapid = ? and testTaken='Attempted' ";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
					scheduleInfo.getTimeboundId(), scheduleInfo.getSapid()
			},
			Integer.class
		);
		return count == 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamApplicableForStudent(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `lti`.`timebound_user_mapping` "
			+ " WHERE `timebound_subject_config_id` = ? AND `userId` = ? ";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				scheduleInfo.getTimeboundId(), scheduleInfo.getSapid() 
			},
			Integer.class
		);
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfExamBookedByStudent(MBAScheduleInfoBean scheduleInfo) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Get list of failed subjects
		
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`mba_wx_bookings` "
			+ " WHERE `timeboundId` = ? AND `sapid` = ? AND `bookingStatus` = 'Y'";
		

		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ 
				scheduleInfo.getTimeboundId(), scheduleInfo.getSapid() 
			},
			Integer.class
		);
		return count > 0;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getMbaWxStudentsExamDataOnExamTime(String examType,String examTime,String sapid) {
		ArrayList<MettlRegisterCandidateBeanMBAWX> userList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> params = new ArrayList<Object>();
		String sql = "select subject, timebound_id, sapid, firstname, lastname, emailId, examStartDateTime, schedule_id, acessKey, joinURL, max_score, portalStatus, mettlStatus from exam.exams_scheduleinfo_mettl where examStartDateTime like '"+examTime+"%'";

		sql = examType.equalsIgnoreCase("100") ? (sql + " and max_score='100'") : (sql + " and max_score<>'100'");
		if(sapid!=null && !sapid.isEmpty()){
			sql = sql + " and sapid = ? ";
			params.add(sapid);
		}
		Object args[] = params.toArray();
		userList = (ArrayList<MettlRegisterCandidateBeanMBAWX>)jdbcTemplate.query(sql,args,new BeanPropertyRowMapper<MettlRegisterCandidateBeanMBAWX>(MettlRegisterCandidateBeanMBAWX.class));

		return userList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getExamStatusMbaWx(String sapid,String scheduleId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<MettlRegisterCandidateBeanMBAWX> mettlRegisterCandidateBeanMBAWX = new ArrayList<MettlRegisterCandidateBeanMBAWX>();
		String sql="select sapid,firstname,lastname,mettlStatus as testTaken,startJsonResponse,finishedJsonResponse,finish_mode,gradedJsonResponse,resumeJsonResponse "
				+ " from exam.exams_scheduleinfo_mettl where sapid = ? and schedule_id = ? ";
		mettlRegisterCandidateBeanMBAWX = (ArrayList<MettlRegisterCandidateBeanMBAWX>)jdbcTemplate.query(sql, new Object[] {sapid,scheduleId},new BeanPropertyRowMapper<MettlRegisterCandidateBeanMBAWX>(MettlRegisterCandidateBeanMBAWX.class));
		return mettlRegisterCandidateBeanMBAWX;
	}
		
	@Transactional(readOnly = false)
	public void updateExamStartStatusMbaWx(String status, String startJsonResponse, String accessKey, String emailId, String modifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.exams_scheduleinfo_mettl set mettlStatus = ? , startJsonResponse = ? , modifiedBy = ? , modifiedDateTime = sysdate() where emailId = ? and acessKey = ? ";
		jdbcTemplate.update(sql, new Object[] {status,startJsonResponse,modifiedBy,emailId,accessKey});
	}
	
	@Transactional(readOnly = false)
	public void updateExamEndStatusMbaWx(String status, String finishedJsonResponse, String finishMode, String accessKey, String emailId, String modifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.exams_scheduleinfo_mettl set testTaken='Attempted' , mettlStatus = ? , finishedJsonResponse = ? , finish_mode = ? , modifiedBy= ? , modifiedDateTime = sysdate() where emailId = ? and acessKey = ? ";
		jdbcTemplate.update(sql, new Object[] {status,finishedJsonResponse,finishMode,modifiedBy,emailId,accessKey});
	}
	
	@Transactional(readOnly = false)
	public void updateExamResumeStatusMbaWx(String resumeJsonResponse, String accessKey, String emailId, String modifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.exams_scheduleinfo_mettl set resumeJsonResponse = ? , modifiedBy= ? , modifiedDateTime = sysdate() where emailId= ? and acessKey= ? ";
		jdbcTemplate.update(sql, new Object[] {resumeJsonResponse,modifiedBy,emailId,accessKey});
	}
	
	@Transactional(readOnly = false)
	public void updateExamGradedStatusMbaWx(String gradedJsonResponse, String accessKey, String emailId, String modifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.exams_scheduleinfo_mettl set gradedJsonResponse = ? , modifiedBy= ? , modifiedDateTime = sysdate() where emailId= ? and acessKey= ?";
		jdbcTemplate.update(sql, new Object[] {gradedJsonResponse,modifiedBy,emailId,accessKey});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getStudentDataForDashboardMBAWX(String todayDate, String examType) {
		ArrayList<MettlRegisterCandidateBeanMBAWX> userList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid,emailId,acessKey,examStartDateTime,timebound_id,max_score,mettlStatus,portalStatus from exam.exams_scheduleinfo_mettl where examStartDateTime like '"+todayDate+"%'";

		if(examType.equalsIgnoreCase("100"))
			sql = sql + " and max_score='100'";
		else
			sql = sql + " and max_score<>'100'";

		userList = (ArrayList<MettlRegisterCandidateBeanMBAWX>)jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper<MettlRegisterCandidateBeanMBAWX>(MettlRegisterCandidateBeanMBAWX.class));

		return userList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getBatchDataForDashboardMBAWX(ArrayList<Integer> consumerProgramStructureIdList) {
		ArrayList<String> batchList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String commaSeparatedCPSid=consumerProgramStructureIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
		String sql = "select id from exam.batch where consumerProgramStructureId in ("+commaSeparatedCPSid+")";
		batchList = (ArrayList<String>)jdbcTemplate.query(sql,new SingleColumnRowMapper<String>(String.class));
		return batchList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getTimeBoundDataForDashboardMBAWX() {
		ArrayList<BatchExamBean> timeboundList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select id,batchId from lti.student_subject_config";
		timeboundList=(ArrayList<BatchExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class));
		return timeboundList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<MettlRegisterCandidateBeanMBAWX> getAttemptedDataForDashboardMBAWX(String examTime) {
		ArrayList<MettlRegisterCandidateBeanMBAWX> attemptedUserList = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select sapid,timebound_id from exam.exams_scheduleinfo_mettl where testTaken='Attempted' and examStartDateTime <> ? ";
		attemptedUserList=(ArrayList<MettlRegisterCandidateBeanMBAWX>)jdbcTemplate.query(sql,new Object[] {examTime}, new BeanPropertyRowMapper<MettlRegisterCandidateBeanMBAWX>(MettlRegisterCandidateBeanMBAWX.class));
		return attemptedUserList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getExamTimeForDashboardMBAWX(String examDate) {
		ArrayList<String> examTimeList = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select examStartDateTime from exam.exams_scheduleinfo_mettl where max_score='100' and examStartDateTime like '"+examDate+"%' group by examStartDateTime";
		examTimeList =(ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper<>(String.class));
		return examTimeList;
	}
}
