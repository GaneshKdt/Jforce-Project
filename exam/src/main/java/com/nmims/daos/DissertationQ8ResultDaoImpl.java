package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationQ8ResultDaoInterface;

@Component
public class DissertationQ8ResultDaoImpl extends BaseDAO implements DissertationQ8ResultDaoInterface {
	
	@Autowired
	DissertationQ7DAO dissertationQ7Dao;
	

	
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
		return dissertationQ7Dao.getConsumerAndSubjectId(subject, sem);
	}

	@Override
	public List<BatchExamBean> getBatchList(String consumerProgramStructureId, int sem) {
		// TODO Auto-generated method stub
		return dissertationQ7Dao.getBatchList(consumerProgramStructureId, sem);
	}

	@Override
	public int upsertIntoMarks(List<DissertationResultBean> upsertList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] insertCount = null;
		String sql = "Insert into exam.mscaiml_md_q8_marks(sapid,"
				+" timebound_id, prgm_sem_subj_id, component_c_score, component_c_max_score,component_c_status,processed,"
				+ " createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
				+" VALUES(?,?,?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ "ON DUPLICATE KEY UPDATE "
				+ "component_c_score = ?,"
				+ "component_c_status = ?, "
				+ "processed = 'N' ,"
				+ "lastModifiedBy = ?,"
				+ "lastModifiedDate = sysdate()";
		
		insertCount =	jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean =  upsertList.get(i);
				ps.setLong(1, bean.getSapid());
				ps.setInt(2, bean.getTimeBoundId());
				ps.setInt(3, bean.getPrgm_sem_subj_id());
				ps.setDouble(4, bean.getComponent_c_score());
				ps.setInt(5, bean.getComponent_c_max_score());
				ps.setString(6, bean.getComponent_c_status());
				ps.setString(7, "N");
				ps.setString(8, bean.getCreatedBy());
				ps.setString(9, bean.getLastModifiedBy());
				ps.setDouble(10, bean.getComponent_c_score());
				ps.setString(11, bean.getComponent_c_status());
				ps.setString(12, bean.getLastModifiedBy());
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return upsertList.size();
			}
		});
				
		return insertCount.length;
	}

	@Override
	public List<DissertationResultBean> getQ8MarksList(String timebound_id) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql ="select sapid,prgm_sem_subj_id,component_c_score,component_c_max_score,component_c_status,"
				+ "timebound_id as timeBoundId from exam.mscaiml_md_q8_marks where timebound_id = ? and processed = 'N' ";
		
		return jdbcTemplate.query(sql,new Object[] {timebound_id}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	

	@Override
	public int upsertMarkstoStaging(List<DissertationResultBean> finalUpsertListForStaging) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] insertCount =  null;
		String sql = "Insert into exam.mscaiml_md_q8_passfail_staging(timeboundId,prgm_sem_subj_id,sapid,component_c_score,"
				+ "component_c_max_score,total,"
				+ "graceMarks,isPass,failReason,"
				+ "isResultLive,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)"
				+ "ON DUPLICATE KEY UPDATE "
				+ "component_c_score = ?,"
				+ "total = ?,"
				+ "status = ?,"
				+ "isPass = ? ,"
				+ "failReason = ?,"
				+ "lastModifiedBy = ?,"
				+ "lastModifiedDate = sysdate()";
			
		
		
		insertCount =	 jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			 @Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DissertationResultBean bean = finalUpsertListForStaging.get(i);
					ps.setInt(1, bean.getTimeBoundId());
					ps.setInt(2, bean.getPrgm_sem_subj_id());
					ps.setLong(3, bean.getSapid());
					ps.setDouble(4, bean.getComponent_c_score());
					ps.setInt(5, bean.getComponent_c_max_score());
					ps.setInt(6, bean.getTotal());
					ps.setInt(7, bean.getGraceMarks());
					ps.setString(8, bean.getIsPass());
					ps.setString(9, bean.getFailReason());
					ps.setString(10, "N");
					ps.setString(11, bean.getCreatedBy());
					ps.setString(12, bean.getLastModifiedBy());
					ps.setString(13, bean.getComponent_c_status());
					
					ps.setDouble(14, bean.getComponent_c_score());
					ps.setInt(15, bean.getTotal());
					ps.setString(16, bean.getComponent_c_status());
					ps.setString(17, bean.getIsPass());
					ps.setString(18, bean.getFailReason());
					ps.setString(19, bean.getLastModifiedBy());
					
				}
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return finalUpsertListForStaging.size();
			}
		});
	
		 return insertCount.length;
	}

	@Override
	public List<DissertationResultBean> getGraceApplicabeStudent(String timebound_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select timeboundId,prgm_sem_subj_id,sapid,component_c_score,"
				+ "component_c_max_score,status as component_c_status,graceMarks,total,isPass,failReason "
				+ "from exam.mscaiml_md_q8_passfail_staging where timeboundId = ? and isPass = 'N' ;";
		
		return jdbcTemplate.query(sql, new Object[] {timebound_id}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int upsertGraceList(List<DissertationResultBean> processedGrace) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "Update exam.mscaiml_md_q8_passfail_staging set component_c_score = ? , isPass =  ? ,"
					+ " graceMarks = ?,total = ?, failReason= ?  where sapid = ?  and timeboundId = ?;";
			
			
			 jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					DissertationResultBean bean = processedGrace.get(i);
					ps.setDouble(1, bean.getComponent_c_score());
					ps.setString(2, bean.getIsPass());
					ps.setInt(3, bean.getGraceMarks());
					ps.setInt(4, bean.getTotal());
					ps.setString(5, bean.getFailReason());
					ps.setLong(6, bean.getSapid());
					ps.setInt(7, bean.getTimeBoundId());
					
					
				}
				
				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return processedGrace.size();
				}
			});
			}catch(Exception e ) {
				e.printStackTrace();
			}
			 return processedGrace.size();
		}

	@Override
	public List<DissertationResultBean> getPassFailStaging(String timebound_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select timeboundId,prgm_sem_subj_id,sapid,component_c_score,component_c_max_score,status as component_c_status,"
				+ "total,isPass,failReason,createdBy,lastModifiedBy,graceMarks,grade,gradePoints "
				+ "from exam.mscaiml_md_q8_passfail_staging where timeboundId = ? ";
		return jdbcTemplate.query(sql, new Object[] {timebound_id}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
	}

	@Override
	public int upsertIntoPassFail(List<DissertationResultBean> passfailList) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		int[] insertCount = null;
		String sql = "Insert into exam.mscaiml_md_q8_passfail(timeboundId,prgm_sem_subj_id,sapid,component_c_score,"
				+ "component_c_max_score,total,graceMarks,isPass,failReason,"
				+ "isResultLive,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status,grade,gradePoints) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?)"
				+ "ON DUPLICATE KEY UPDATE "
				+ "component_c_score = ?,"
				+ "status = ?,"
				+ "total = ?,"
				+ "isPass = ? ,"
				+ "failReason = ?,"
				+ "lastModifiedBy = ?,"
				+ "lastModifiedDate = sysdate(),"
				+"graceMarks = ?, "
				+ "grade = ?,"
				+ "gradePoints =?,"
				+ "isResultLive = 'N'";
		
		
		insertCount = 	 jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = passfailList.get(i);
				ps.setInt(1, bean.getTimeBoundId());
				ps.setInt(2, bean.getPrgm_sem_subj_id());
				ps.setLong(3, bean.getSapid());
				ps.setDouble(4, bean.getComponent_c_score());
				ps.setInt(5, bean.getComponent_c_max_score());
				ps.setInt(6, bean.getTotal());
				ps.setInt(7, bean.getGraceMarks());
				ps.setString(8, bean.getIsPass());
				ps.setString(9, bean.getFailReason());
				ps.setString(10, "N");
				ps.setString(11, bean.getCreatedBy());
				ps.setString(12, bean.getLastModifiedBy());
				ps.setString(13, bean.getComponent_c_status());
				ps.setString(14, bean.getGrade());
				ps.setFloat(15, bean.getGradePoints());
				ps.setDouble(16, bean.getComponent_c_score());
				ps.setString(17, bean.getComponent_c_status());
				ps.setInt(18, bean.getTotal());
				ps.setString(19, bean.getIsPass());
				ps.setString(20, bean.getFailReason());
				ps.setString(21, bean.getLastModifiedBy());
				ps.setInt(22, bean.getGraceMarks());
				ps.setString(23, bean.getGrade());
				ps.setFloat(24, bean.getGradePoints());
				
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return passfailList.size();
			}
		});
		return insertCount.length;
	}

	@Override
	public int deleteFromStaging(String timebound_id) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql = "Delete from exam.mscaiml_md_q8_passfail_staging where timeboundId  = ?";
		
		return jdbcTemplate.update(sql, new Object[] {timebound_id});
	}

	@Override
	public int makeLive(String timebound_id) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		String sql = "update exam.mscaiml_md_q8_passfail set isResultLive = 'Y' where timeboundId  = ?";
		
		return  jdbcTemplate.update(sql, new Object[] {timebound_id});
	}

	@Override
	public int updateMarksProccessed(List<DissertationResultBean> finalUpsertListForStaging) {
		jdbcTemplate =  new JdbcTemplate(dataSource);
		int[] updateCount =null;
		String sql = "update exam.mscaiml_md_q8_marks set processed = 'Y' where timebound_id  = ? and sapid = ?";
		updateCount = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = finalUpsertListForStaging.get(i);
				ps.setInt(1, bean.getTimeBoundId());
				ps.setLong(2, bean.getSapid());
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return finalUpsertListForStaging.size();
			}
		});
		return updateCount.length;
	}

	@Override
	public DissertationResultBean getDissertationResult(String sapid, String timeboundId) {
		
		jdbcTemplate =  new JdbcTemplate(dataSource);
		
		String sql = "select timeboundId,prgm_sem_subj_id,sapid,component_c_score,component_c_max_score,"
				+ "status as component_c_status,graceMarks,total,isPass,isResultLive "
				+ "from exam.mscaiml_md_q8_passfail where sapid = ?  and timeboundId = ? and isResultLive = 'Y'";
		return (DissertationResultBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,timeboundId}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
		
		}

	@Override
	public List<EmbaGradePointBean> getAllGrade() {
		return dissertationQ7Dao.getAllGrades();
	}
	
	@Override
	public int upsertGrade(List<DissertationResultBean> upsertList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.mscaiml_md_q8_passfail_staging set grade = ? ,gradePoints = ?,lastModifiedBy =? "
				+ ",lastModifiedDate=sysdate() where timeboundId  = ? and sapid = ?";
		
		int[] updateCount = null;
		updateCount = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DissertationResultBean bean = upsertList.get(i);
				ps.setString(1, bean.getGrade());
				ps.setFloat(2, bean.getGradePoints());
				ps.setString(3, bean.getLastModifiedBy());
				ps.setInt(4, bean.getTimeBoundId());
				ps.setLong(5, bean.getSapid());
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return upsertList.size();
			}
		});
		return updateCount.length;
	}

	public DissertationResultBean getPassFail(String sapid) {
			jdbcTemplate =  new JdbcTemplate(dataSource);
		
		String sql = "select timeboundId,prgm_sem_subj_id,sapid,component_c_score,component_c_max_score,"
				+ "status as component_c_status,graceMarks,total,isPass,isResultLive,grade,gradePoints,prgm_sem_subj_id "
				+ "from exam.mscaiml_md_q8_passfail where sapid = ?";
		return (DissertationResultBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid}, new BeanPropertyRowMapper<>(DissertationResultBean.class));
		
		}

	public int checkSapidExist(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from mscaiml_md_q8_passfail where sapid = ?";
		
		return jdbcTemplate.queryForObject(sql,new Object[] {sapid},Integer.class);
	}

	public DissertationResultProcessingDTO getRegistration(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,sapid,month as acadMonth,year as acadYear from exam.registration where sapid =? "
				+ "and sem = (select max(sem) from exam.registration where sapid =?);";
		
		return jdbcTemplate.queryForObject(sql,new Object[] {sapid,sapid},new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class));
	}

	public List<DissertationResultProcessingDTO> getTimeBound(int pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select id,examYear,examMonth,acadMonth,acadYear from lti.student_subject_config where prgm_sem_subj_id = ?;";
		
		return jdbcTemplate.query(sql,new Object[] {pssId},new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class));
	}

	public List<DissertationResultProcessingDTO> getTimbound(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select timebound_subject_config_id as id,userId as sapid from lti.timebound_user_mapping where userId = ?";
		
		return jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(DissertationResultProcessingDTO.class));
	}

}
