package com.nmims.daos;



import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.dto.DissertationResultProcessingDTO;


@Component
public class DissertationQ7DAO extends BaseDAO implements DissertationResultProcessingDaoInterface{


	@Autowired
	ExamsAssessmentsDAO examAssementsDao;
	
	@Autowired
	MDMSubjectCodeDAO mdmSubjectCodeDao;
	
	@Autowired
	DashboardDAO dashBoardDao;
	
	@Autowired
	TestDAO testDao;
	
	
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
	
	@Override
	public List<TEEResultBean> getConsumerAndSubjectId(String subject, int sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="select consumerProgramStructureId,id as prgm_sem_subj_id from exam.program_sem_subject "
				+ "where subject =? and sem = ?";
		return  jdbcTemplate.query(sql, new Object[] {subject,sem}, new BeanPropertyRowMapper<>(TEEResultBean.class));
	}
	
	@Override
	public List<BatchExamBean> getBatchList(String consumerProgramStructureId, int sem) {
		List<BatchExamBean>  batchList = new ArrayList<>();
		 batchList = examAssementsDao.getBatchesList(consumerProgramStructureId,sem);
		 return batchList;
		 
	}
	


	@Override
	public List<TimeBoundUserMapping> getMappedStudent(String timeBound) {
	
		return dashBoardDao.getMappedStudent(timeBound);
	}

	@Override
	public String getStudentSubjectConfig(String timeboundId) {
		return dashBoardDao.getStudentSubjectConfig(timeboundId);
	}

	@Override
	public DissertationResultProcessingDTO getProgram(String subjectId) {
		return dashBoardDao.getProgram(subjectId);
	}

	@Override
	public List<DissertationResultProcessingDTO> getTestScores(String sapid,String commaSepratedTestIds) {
		
		return testDao.getTestScoreForIA(sapid,commaSepratedTestIds);
	}

	@Override
	public List<Integer> getTestIds(String refId) {
		
		return testDao.getTestIds(refId);
	}
	
	@Override
	public List<Integer> getSessionIds(int sessionPlanId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" select id from acads.sessionplan_module where topic <> 'Generic Module For Session Plan ' and sessionPlanId = ?";
		return jdbcTemplate.queryForList(sql,new Object[] {sessionPlanId},Integer.class);
		 
	}

	@Override
	public int getSessionPlanId(int timeBoundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="select sessionPlanId from acads.sessionplanid_timeboundid_mapping where timeboundId = ?";
		return jdbcTemplate.queryForObject(sql,new Object[] {timeBoundId},Integer.class);
		
	}

	@Override
	public List<TestExamBean> getExamTest(String commaSepratedTestIds) {
		return testDao.getExamTest(commaSepratedTestIds);
	}

	@Override
	@Transactional(readOnly = false)
	public int upsertMarks(List<DissertationResultBean> insertMarksData) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] insertCount = null;
		String sql="Insert into exam.mscaiml_md_q7_marks "+"(sapid,"
				+ "timebound_id," + "prgm_sem_subj_id,"
				+ "component_a_score,"+"component_a_max_score,"+"component_b_score,"
						+ "component_b_max_score,"+"processed,"+"component_a_status,"
				+ "component_b_status,"+"createdBy,"+"createdDate,"+"lastModifiedBy,"+"lastModifiedDate)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ " ON DUPLICATE KEY UPDATE "
				+ " component_a_score = ? , "
				+" component_b_score = ? , "
				+ " component_a_status = ? , "
				+" component_b_status = ? , "
				+" processed = 'N' , "
				+ " lastModifiedBy = ?  , "
				+ " lastModifiedDate = sysdate() ";
		
		
		insertCount =	jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = insertMarksData.get(i);
				
				ps.setLong(1, bean.getSapid());
				ps.setInt(2, bean.getTimeBoundId());
				ps.setInt(3, bean.getPrgm_sem_subj_id());
				ps.setDouble(4, bean.getComponent_a_score());
				ps.setInt(5, bean.getComponent_a_max_score());
				ps.setDouble(6, bean.getComponent_b_score());
				ps.setInt(7, bean.getComponent_b_max_score());
				ps.setString(8, "N");
				ps.setString(9, bean.getComponent_a_status());
				ps.setString(10, bean.getComponent_b_status());
				ps.setString(11, bean.getCreatedBy());
				ps.setString(12, bean.getLastModifiedBy());
				ps.setDouble(13, bean.getComponent_a_score());
				ps.setDouble(14, bean.getComponent_b_score());
				ps.setString(15, bean.getComponent_a_status());
				ps.setString(16, bean.getComponent_b_status());
				ps.setString(17, bean.getLastModifiedBy());
			}
			
			@Override
			public int getBatchSize() {
				
				return insertMarksData.size();
			}
		});
	
	
		return insertCount.length;
	}

	@Override
	public List<DissertationResultBean> getNotProcessedList(String timeBoundId) {

		jdbcTemplate =  new JdbcTemplate(dataSource);
		
		String sql = "select timebound_id as timeBoundId ,prgm_sem_subj_id,sapid,component_a_score,component_b_score,component_a_max_score,"
				+ "component_b_max_score,component_a_status,component_b_status "
				+ "from exam.mscaiml_md_q7_marks where processed = 'N' and timebound_id =?";
		return jdbcTemplate.query(sql,new Object[] {timeBoundId},new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int upsertPassFailStaging(List<DissertationResultBean> upsertList) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		int[] insertCount = null;
		String sql = "Insert into mscaiml_md_q7_passfail_staging"+"(timeboundId,"
				+ "sapid,"+"prgm_sem_subj_id,"+"component_a_score,"+"component_b_score,"+"isPass,"+"failReason,"
				+"isResultLive, "+" createdBy,"+"createdDate, "+" lastModifiedBy,"+"lastModifiedDate,"+"component_a_status,"
				+"component_b_status,"+"grade,"+"component_a_max_score,"+"component_b_max_score)"
				+"VALUES(?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?)"
				+ " ON DUPLICATE KEY UPDATE "
				+ " component_a_score = ? , "
				+" component_b_score = ? , "
				+ " component_a_status = ? , "
				+" component_b_status = ? , "
				+ " lastModifiedBy = ?  , "
				+ " lastModifiedDate = sysdate() ,"
				+ "isPass = ?,"
				+ "failReason = ?,"
				+ "grade = ?,"
				+ "gradePoints =?";
		
		insertCount = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean resultBean = upsertList.get(i);
				ps.setInt(1, resultBean.getTimeBoundId());
				ps.setLong(2, resultBean.getSapid());
				ps.setInt(3, resultBean.getPrgm_sem_subj_id());
				ps.setDouble(4, resultBean.getComponent_a_score());
				ps.setDouble(5, resultBean.getComponent_b_score());
				ps.setString(6, resultBean.getIsPass());
				ps.setString(7, resultBean.getFailReason());
				ps.setString(8, "N");
				ps.setString(9,resultBean.getCreatedBy());
				ps.setString(10, resultBean.getLastModifiedBy());
				ps.setString(11, resultBean.getComponent_a_status());
				ps.setString(12, resultBean.getComponent_b_status());
				ps.setString(13, resultBean.getGrade());
				ps.setInt(14, resultBean.getComponent_a_max_score());
				ps.setInt(15, resultBean.getComponent_b_max_score());
				ps.setDouble(16, resultBean.getComponent_a_score());
				ps.setDouble(17, resultBean.getComponent_b_score());
				ps.setString(18, resultBean.getComponent_a_status());
				ps.setString(19, resultBean.getComponent_b_status());
				ps.setString(20, resultBean.getLastModifiedBy());
				ps.setString(21, resultBean.getIsPass());
				ps.setString(22, resultBean.getFailReason());
				ps.setString(23, resultBean.getGrade());
				ps.setFloat(24, resultBean.getGradePoints());
				
				
			}
			
			@Override
			public int getBatchSize() {
				return upsertList.size();
			}
			
		});
		return insertCount.length;
	}

	@Override
	public List<DissertationResultBean> getStagedDissertationList(String timeBoundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="select timeboundId,sapid,prgm_sem_subj_id,component_a_score,component_b_score,isPass,failReason,isResultLive,  createdBy,createdDate, gradePoints," + 
				"lastModifiedBy,lastModifiedDate,component_a_status," + 
				"component_b_status,grade,component_a_max_score,component_b_max_score from  exam.mscaiml_md_q7_passfail_staging "
				+ "where timeboundId = ?";
		
		return jdbcTemplate.query(sql,new Object[] {timeBoundId}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int transferrToPassFailQ7(List<DissertationResultBean> transferList) {
		jdbcTemplate  = new JdbcTemplate(dataSource);
		int[] insertCount = null;
		String sql = "Insert into exam.mscaiml_md_q7_passfail"+"(timeboundId,"
				+ "sapid,"+"prgm_sem_subj_id,"+"component_a_score,"+"component_b_score,"+"isPass,"+"failReason,"
				+"isResultLive, "+" createdBy,"+"createdDate, "+" lastModifiedBy,"+"lastModifiedDate,"+"component_a_status,"
				+"component_b_status,"+"grade,"+"component_a_max_score,"+"component_b_max_score,"+"gradePoints)"
				+"VALUES(?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?,?)"
				+ " ON DUPLICATE KEY UPDATE "
				+ " component_a_score = ? , "
				+" component_b_score = ? , "
				+ " component_a_status = ? , "
				+" component_b_status = ? , "
				+ " lastModifiedBy = ?  , "
				+ " lastModifiedDate = sysdate(), "
				+ "grade = ?,"
				+ "gradePoints =?,"
				+"isResultLive = 'N',"
				+"isPass = ?,"  
				+"failReason = ?";
		
		insertCount =	jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean resultBean = transferList.get(i);
				ps.setInt(1, resultBean.getTimeBoundId());
				ps.setLong(2, resultBean.getSapid());
				ps.setInt(3, resultBean.getPrgm_sem_subj_id());
				ps.setDouble(4, resultBean.getComponent_a_score());
				ps.setDouble(5, resultBean.getComponent_b_score());
				ps.setString(6, resultBean.getIsPass());
				ps.setString(7, resultBean.getFailReason());
				ps.setString(8, "N");
				ps.setString(9,resultBean.getCreatedBy());
				ps.setString(10, resultBean.getLastModifiedBy());
				ps.setString(11, resultBean.getComponent_a_status());
				ps.setString(12, resultBean.getComponent_b_status());
				ps.setString(13, resultBean.getGrade());
				ps.setInt(14, resultBean.getComponent_a_max_score());
				ps.setInt(15, resultBean.getComponent_b_max_score());
				ps.setFloat(16, resultBean.getGradePoints());
				ps.setDouble(17, resultBean.getComponent_a_score());
				ps.setDouble(18, resultBean.getComponent_b_score());
				ps.setString(19, resultBean.getComponent_a_status());
				ps.setString(20, resultBean.getComponent_b_status());
				ps.setString(21, resultBean.getLastModifiedBy());
				ps.setString(22, resultBean.getGrade());
				ps.setFloat(23, resultBean.getGradePoints());
				ps.setString(24, resultBean.getIsPass());
				ps.setString(25, resultBean.getFailReason());
				
				
			}
			
			@Override
			public int getBatchSize() {
				return transferList.size();
			}
		});
		
		
		return insertCount.length;
	}

	@Override
	public int deleteStagingData(String timeBoundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="delete from exam.mscaiml_md_q7_passfail_staging where timeboundId = ?";
		return  jdbcTemplate.update(sql, new Object[] {timeBoundId});
	}

	@Override
	@Transactional(readOnly = false)
	public int updateUpsertList(List<DissertationResultBean> transferListForStaging) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		
		String sql ="update exam.mscaiml_md_q7_marks set processed = 'Y' where sapid = ? and timebound_id = ?";
		
		//jdbcTemplate.update(sql,new Object[] {transferListForStaging.get(0).getSapid(),transferListForStaging.get(0).getTimeBoundId()});
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = transferListForStaging.get(i);
		
				ps.setLong(1, bean.getSapid());
				ps.setInt(2, bean.getTimeBoundId());
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return transferListForStaging.size();
			}
		});
		return transferListForStaging.size();
	}

	@Override
	public int getTimboundDetails(String sapid, String timeboundId) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		
		return dashBoardDao.getTimboundDetails(sapid,timeboundId);
	}

	@Override
	public DissertationResultBean getDissertationResult(String sapid, String timeboundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "  select sapid,timeboundId,component_a_score,component_b_score,isPass,"
				+ " component_a_status,component_b_status,grade,failReason,isResultLive ,component_a_max_score,component_b_max_score "
				+ " from exam.mscaiml_md_q7_passfail where sapid = ?  and timeboundId =? and isResultLive = 'Y'" ;
				
		return (DissertationResultBean) jdbcTemplate.queryForObject(sql,new Object[] {sapid,timeboundId},new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int makeResultLive(String timebound_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.mscaiml_md_q7_passfail set isResultLive ='Y' where timeboundId = ?";
		return jdbcTemplate.update(sql,new Object[] {timebound_id});
	}

	@Override
	public int upsertGradeInPassFailStaging(List<DissertationResultBean> processListForGrade) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		
		int[] updateCount = null;
		
		String sql = "update exam.mscaiml_md_q7_passfail_staging set grade = ? , gradePoints = ?,lastModifiedBy = ?, "
				+ "lastModifiedDate = sysdate() where sapid = ? and timeboundId = ?";
		
		updateCount = jdbcTemplate.batchUpdate(sql ,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = processListForGrade.get(i);
				ps.setString(1, bean.getGrade());
				ps.setFloat(2, bean.getGradePoints());
				ps.setString(3, bean.getLastModifiedBy());
				ps.setLong(4, bean.getSapid());
				ps.setInt(5, bean.getTimeBoundId());
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return processListForGrade.size();
			}
		});
		return updateCount.length;
	}
	
	@Override
	public List<EmbaGradePointBean> getAllGrades() {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql =  "select grade,points,marksFrom,marksTill from exam.mba_gradepoint";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EmbaGradePointBean.class));
	}

	@Override
	public DissertationResultBean getPassFail(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "  select sapid,timeboundId,component_a_score,component_b_score,isPass,"
				+ " component_a_status,component_b_status,grade,failReason,isResultLive ,component_a_max_score,component_b_max_score,"
				+ "gradePoints,prgm_sem_subj_id "
				+ " from exam.mscaiml_md_q7_passfail where sapid = ?" ;
				
		return (DissertationResultBean) jdbcTemplate.queryForObject(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int checkSapidExist(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from mscaiml_md_q7_passfail where sapid = ?";
		
		return jdbcTemplate.queryForObject(sql,new Object[] {sapid},Integer.class);
		
	}

	@Override
	public DissertationResultProcessingDTO getSubjectName(int subjectIdForQ7) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,subject from exam.program_sem_subject where id = ?";
		
		return jdbcTemplate.queryForObject(sql,new Object[] {subjectIdForQ7},new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class)); 
	}

	@Override
	public List<Integer> getTimeBoundUser(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select timebound_subject_config_id  from lti.timebound_user_mapping where userId = ?";
		return jdbcTemplate.queryForList(sql,new Object[] {sapid},Integer.class); 
	}

	@Override
	public List<DissertationResultProcessingDTO> getTimeBounds(String commaSepratedTimeBoundIds) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select id,examYear,examMonth from lti.student_subject_config where id in ("+commaSepratedTimeBoundIds+")";
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class)); 
	}
}
