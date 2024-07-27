package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBean;
import com.nmims.beans.VivaSlotBookingConfigBean;

@Component
public class LevelBasedProjectDAO extends BaseDAO {
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
	public DataSource getDataSource() {
		return this.dataSource ;
	}
	
	@Transactional(readOnly = false)
	public void insertIntoStudentMapping(LevelBasedProjectBean levelBasedProjectBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
					+ " INSERT INTO `exam`.`student_guide_mapping` ( "
						+ " `sapId`, `facultyId`, `subject`, "
						+ " `year`, `month`, "
						+ " `lastModifiedBy`, `createdBy`, "
						+ " `createdDate`, `lastModifiedDate` "
					+ " ) VALUES ( "
						+ " ?, ?, ?, "
						+ " ?, ?, "
						+ " ?, ?, "
						+ " sysdate(), sysdate() "
					+ " ) ";
			jdbcTemplate.update(
				sql,
				new Object[] {
					levelBasedProjectBean.getSapId(), levelBasedProjectBean.getFacultyId(), levelBasedProjectBean.getSubject(),
					levelBasedProjectBean.getYear(), levelBasedProjectBean.getMonth(),
					levelBasedProjectBean.getLastModifiedBy(), levelBasedProjectBean.getCreatedBy()
				}
			);
	}
	
	@Transactional(readOnly = false)
	public void upsertIntoStudentMapping(LevelBasedProjectBean levelBasedProjectBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`student_guide_mapping` ( "
					+ " `sapId`, `facultyId`, `subject`, "
					+ " `year`, `month`, "
					+ " `lastModifiedBy`, `createdBy`, "
					+ " `createdDate`, `lastModifiedDate` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " ?, ?, "
					+ " sysdate(), sysdate() "
				+ " ) "
				+ " ON DUPLICATE KEY UPDATE  "
				+ " `facultyId` = ?";
		jdbcTemplate.update(
			sql,
			new Object[] {
				levelBasedProjectBean.getSapId(), levelBasedProjectBean.getFacultyId(), levelBasedProjectBean.getSubject(),
				levelBasedProjectBean.getYear(), levelBasedProjectBean.getMonth(),
				levelBasedProjectBean.getLastModifiedBy(), levelBasedProjectBean.getCreatedBy(),
				
				//on update
				levelBasedProjectBean.getFacultyId()
			}
		);
	}
	
	@Transactional(readOnly = true)
	public List<LevelBasedProjectBean> getAllStudentGuideMapping(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select s.*,CONCAT(f.firstName,f.lastName) as faculty from `exam`.`student_guide_mapping` s,`acads`.`faculty` f where f.facultyId = s.facultyId";
			return  jdbcTemplate.query(sql, new BeanPropertyRowMapper<LevelBasedProjectBean>(LevelBasedProjectBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public LevelBasedProjectBean getStudentGuide(String sapid,String year,String month) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM exam.student_guide_mapping where sapid = ? and year=? and month=? ;";
			return (LevelBasedProjectBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,year,month},new BeanPropertyRowMapper(LevelBasedProjectBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public String updateSOPTransactionStatus(UploadProjectSOPBean uploadProjectSOPBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update exam.student_sop_submissions set payment_status = ?,status=? where  track_id=? and sapid = ?";
			int status = jdbcTemplate.update(sql,new Object[] {uploadProjectSOPBean.getPayment_status(),uploadProjectSOPBean.getStatus(),uploadProjectSOPBean.getTrack_id(),uploadProjectSOPBean.getSapId()});
			if(status > 0) {
				return "true";
			}
			return "Failed to update payment status";
		}
		catch (Exception e) {
			// TODO: handle exception
			return "Error: " + e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String updateSynopsisTransactionStatus(UploadProjectSynopsisBean uploadProjectSynopsisBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update exam.student_synopsis_submissions set payment_status = ?,status=? where year = ? and month=? and track_id=?";
			int status = jdbcTemplate.update(sql,new Object[] {uploadProjectSynopsisBean.getPayment_status(),uploadProjectSynopsisBean.getStatus(),uploadProjectSynopsisBean.getYear(),uploadProjectSynopsisBean.getMonth(),uploadProjectSynopsisBean.getTrack_id()});
			if(status > 0) {
				return "true";
			}
			return "Failed to update payment status";
		}
		catch (Exception e) {
			// TODO: handle exception
			return "Error: " + e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public boolean deleteStudentGuideMapping(LevelBasedProjectBean levelBasedProjectBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "delete from `exam`.`student_guide_mapping` where sapid = ?";
			int status = jdbcTemplate.update(sql,new Object[] {levelBasedProjectBean.getSapId()}); 
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
	public UploadProjectSOPBean getLastSubmittedSOP(UploadProjectSOPBean uploadProjectSOPBean,List<String> SOP_STATUS) {
		String sql = ""
				+ " SELECT * "
				+ " FROM `exam`.`student_sop_submissions` "
				+ " WHERE `sapid` = ? "
				+ " AND `year` = ? "
				+ " AND `month` = ? "
				+ " AND `status` IN ('"+ SOP_STATUS.get(2) +"','"+ SOP_STATUS.get(3) +"','"+ SOP_STATUS.get(4) +"') "
				+ " order by id desc limit 1";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				uploadProjectSOPBean.getSapId(), uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth()
			},
			new BeanPropertyRowMapper<UploadProjectSOPBean>(UploadProjectSOPBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public boolean getSOPSubmissionCount(UploadProjectSOPBean uploadProjectSOPBean,List<String> SOP_STATUS) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`student_sop_submissions` "
			+ " WHERE `sapid` = ? "
			+ " AND `year` = ? "
			+ " AND `month` = ? "
			+ " AND `status` IN ('"+ SOP_STATUS.get(2) +"','"+ SOP_STATUS.get(3) +"','"+ SOP_STATUS.get(4) +"') "
			+ " order by id desc limit 1";

		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				uploadProjectSOPBean.getSapId(), uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth()
			},
			Integer.class
		) > 0;
	}

	@Transactional(readOnly = true)
	public boolean checkSynopsisSubmissionCount(UploadProjectSynopsisBean uploadProjectSynopsisBean, List<String> SOP_STATUS) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT count(*) "
				+ " FROM `exam`.`student_synopsis_submissions` "
				+ " WHERE `sapid` = ? "
				+ " AND `year` = ?"
				+ " AND `month` = ? "
				+ " AND `status` IN ('"+ SOP_STATUS.get(2) +"','"+ SOP_STATUS.get(3) +"','"+ SOP_STATUS.get(4) +"')"
				+ " ORDER BY `id` DESC LIMIT 1";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] {
				uploadProjectSynopsisBean.getSapid(), uploadProjectSynopsisBean.getYear(), uploadProjectSynopsisBean.getMonth()
			},
			Integer.class
		) > 0;
	}

	@Transactional(readOnly = true)
	public UploadProjectSynopsisBean getLastSubmittedSynopsis(UploadProjectSynopsisBean uploadProjectSynopsisBean, List<String> SOP_STATUS) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
					+ " SELECT * "
					+ " FROM `exam`.`student_synopsis_submissions` "
					+ " WHERE `sapid` = ? "
					+ " AND `year` = ?"
					+ " AND `month` = ? "
					+ " AND `status` IN ('"+ SOP_STATUS.get(2) +"','"+ SOP_STATUS.get(3) +"','"+ SOP_STATUS.get(4) +"')";
			return jdbcTemplate.queryForObject(
				sql, 
				new Object[] {
					uploadProjectSynopsisBean.getSapid(), uploadProjectSynopsisBean.getYear(), uploadProjectSynopsisBean.getMonth()
				},
				new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class)
			);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	
	@Transactional(readOnly = false)
	public boolean insertSOPRecord(UploadProjectSOPBean uploadProjectSOPBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`student_sop_submissions` ( "
				+ " `year`, `month`, `sapid`, "
				+ " `filePath`, `previewPath`, `facultyId`, "
				+ " `status`, `payment_status`, `track_id`, "
				+ " `attempt`, `created_by`, `updated_by` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ? "
			+ " ) "
			+ " ON DUPLICATE KEY UPDATE  "
			+ " `attempt` = (`attempt` + 1), "
			+ " `filePath` = ?, "
			+ " `previewPath` = ?, "
			+ " `status` = ? ";
			//+ " `reason` = null, "
			//+ " `payment_status` = ?, "
			//+ " `track_id` = ? ";
		int status = jdbcTemplate.update(
			sql,
			new Object[] {
				uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth(), uploadProjectSOPBean.getSapId(),
				uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(), uploadProjectSOPBean.getFacultyId(), 
				uploadProjectSOPBean.getStatus(), uploadProjectSOPBean.getPayment_status(), uploadProjectSOPBean.getTrack_id(),
				uploadProjectSOPBean.getAttempt(),uploadProjectSOPBean.getCreated_by(), uploadProjectSOPBean.getUpdated_by(),
				
				uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(),
				uploadProjectSOPBean.getStatus()
			}
		);
		if(status == 1) {
			return true;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public boolean upsertPaymentSOPRecord(UploadProjectSOPBean uploadProjectSOPBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`student_sop_submissions` ( "
				+ " `year`, `month`, `sapid`, "
				+ " `filePath`, `previewPath`, `facultyId`, "
				+ " `status`, `payment_status`, `track_id`, "
				+ " `attempt`, `created_by`, `updated_by` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ? "
			+ " ) "
			+ " ON DUPLICATE KEY UPDATE  "
			+ " `attempt` = ?, "
			+ " `filePath` = ?, "
			+ " `previewPath` = ?, "
			+ " `status` = ?, "
			+ " `reason` = null, "
			+ " `payment_status` = ?, "
			+ " `track_id` = ? ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth(), uploadProjectSOPBean.getSapId(),
				uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(), uploadProjectSOPBean.getFacultyId(), 
				uploadProjectSOPBean.getStatus(), uploadProjectSOPBean.getPayment_status(), uploadProjectSOPBean.getTrack_id(),
				uploadProjectSOPBean.getAttempt(),uploadProjectSOPBean.getCreated_by(), uploadProjectSOPBean.getUpdated_by(),
				
				uploadProjectSOPBean.getAttempt(),uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(),
				uploadProjectSOPBean.getStatus(),uploadProjectSOPBean.getPayment_status(),uploadProjectSOPBean.getTrack_id()
			}
		);
		/*
		if(status == 1) {
			return true;
		}
		return false;*/
		return true;
	}
	
	@Transactional(readOnly = false)
	public boolean insertSOPRecordToHistory(UploadProjectSOPBean uploadProjectSOPBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
				+ " INSERT INTO `exam`.`student_sop_submissions_history` ( "
					+ " `year`, `month`, `sapid`, `subject`, "
					+ " `filePath`, `previewPath`, `facultyId`, "
					+ " `status`, `payment_status`, `track_id`, "
					+ " `created_by`, `updated_by` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ? "
				+ " ) ";
			int status = jdbcTemplate.update(
				sql,
				new Object[] {
					uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth(), uploadProjectSOPBean.getSapId(), uploadProjectSOPBean.getSubject(),
					uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(), uploadProjectSOPBean.getFacultyId(), 
					uploadProjectSOPBean.getStatus(), uploadProjectSOPBean.getPayment_status(), uploadProjectSOPBean.getTrack_id(),
					uploadProjectSOPBean.getCreated_by(), uploadProjectSOPBean.getUpdated_by()
				}
			); 
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
	
	@Transactional(readOnly = false)
	public void insertSynopsisRecord(UploadProjectSynopsisBean uploadProjectSynopsisBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`student_synopsis_submissions` ( "
				+ " `year`, `month`, `sapid`, "
				+ " `filePath`, `previewPath`, `facultyId`, "
				+ " `status`, `payment_status`, `track_id`, "
				+ " `attempt`, `created_by`, `updated_by` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ? "
			+ " ) "
			+ " ON DUPLICATE KEY UPDATE  "
			+ " `attempt` = (`attempt` + 1), "
			+ " `filePath` = ?, "
			+ " `previewPath` = ?, "
			+ " `status` = ?, "
			+ " `payment_status` = ?, "
			+ " `track_id` = ? ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				uploadProjectSynopsisBean.getYear(), uploadProjectSynopsisBean.getMonth(), uploadProjectSynopsisBean.getSapid(),
				uploadProjectSynopsisBean.getFilePath(), uploadProjectSynopsisBean.getPreviewPath(), uploadProjectSynopsisBean.getFacultyId(), 
				uploadProjectSynopsisBean.getStatus(), uploadProjectSynopsisBean.getPayment_status(), uploadProjectSynopsisBean.getTrack_id(),
				uploadProjectSynopsisBean.getAttempt(), uploadProjectSynopsisBean.getCreated_by(), uploadProjectSynopsisBean.getUpdated_by(),
				
				uploadProjectSynopsisBean.getFilePath(), uploadProjectSynopsisBean.getPreviewPath(),
				uploadProjectSynopsisBean.getStatus(), uploadProjectSynopsisBean.getPayment_status(), uploadProjectSynopsisBean.getTrack_id()
			}
		);
	}
	
	@Transactional(readOnly = false)
	public boolean insertSynopsisRecordToHistory(UploadProjectSynopsisBean uploadProjectSOPBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
				+ " INSERT INTO `exam`.`student_synopsis_submissions_history` ( "
					+ " `year`, `month`, `sapid`, `subject`, "
					+ " `filePath`, `previewPath`, `facultyId`, "
					+ " `status`, `payment_status`, `track_id`, "
					+ " `created_by`, `updated_by` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ? "
				+ " ) ";
			int status = jdbcTemplate.update(
				sql,
				new Object[] {
					uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth(), uploadProjectSOPBean.getSapid(), uploadProjectSOPBean.getSubject(),
					uploadProjectSOPBean.getFilePath(), uploadProjectSOPBean.getPreviewPath(), uploadProjectSOPBean.getFacultyId(), 
					uploadProjectSOPBean.getStatus(), uploadProjectSOPBean.getPayment_status(), uploadProjectSOPBean.getTrack_id(),
					uploadProjectSOPBean.getCreated_by(), uploadProjectSOPBean.getUpdated_by()
				}
			); 
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
	public List<UploadProjectSOPBean> getSubmittedSOPWithGuidId(String guidId){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_sop_submissions` where facultyId = ?";
			return jdbcTemplate.query(sql,new Object[] {guidId},new BeanPropertyRowMapper<UploadProjectSOPBean>(UploadProjectSOPBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public UploadProjectSOPBean getSOPByTrackId(String trackId){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_sop_submissions` where track_id = ?";
			return jdbcTemplate.queryForObject(sql,new Object[] {trackId},new BeanPropertyRowMapper<UploadProjectSOPBean>(UploadProjectSOPBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<UploadProjectSynopsisBean> getSubmittedSynopsisWithGuidId(String guidId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_synopsis_submissions` where facultyId = ?";
			return jdbcTemplate.query(sql,new Object[] {guidId},new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class));
	}
	
	@Transactional(readOnly = true)
	public UploadProjectSOPBean getStudentSOPSubmissionBySapId(String month, String year, String sapid) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_sop_submissions` where month = ? and year = ? and sapId = ? and payment_status in ('No Charges','Payment Successfull') limit 1";
			return (UploadProjectSOPBean) jdbcTemplate.queryForObject(sql, new Object[] {month,year,sapid},new BeanPropertyRowMapper(UploadProjectSOPBean.class));
			//return jdbcTemplate.queryForObject(sql,new Object[] {sapid}, String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean updateSubmittedSOP(UploadProjectSOPBean uploadProjectSOPBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update `exam`.`student_sop_submissions` set status = ?,reason=? where sapId=? and facultyId=? and year=? and month=?";
			int result = jdbcTemplate.update(sql,new Object[] {uploadProjectSOPBean.getStatus(),uploadProjectSOPBean.getReason(),uploadProjectSOPBean.getSapId(),uploadProjectSOPBean.getFacultyId(),uploadProjectSOPBean.getYear(),uploadProjectSOPBean.getMonth()});
			if(result == 1) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public void updateSubmittedSynopsis(UploadProjectSynopsisBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update `exam`.`student_synopsis_submissions` set score = ?, reason=?,evaluationDate=?,evaluated=?,evaluationCount=?,updated_by=? where id=? and facultyId=? and sapId=?";

		jdbcTemplate.update(
			sql,
			new Object[] {
				bean.getScore(), bean.getReason(), bean.getEvaluationDate(),bean.getEvaluated(),bean.getEvaluationCount(),bean.getUpdated_by(),
				bean.getId(), bean.getFacultyId(), bean.getSapid()
			}
		);
	}
	
	@Transactional(readOnly = true)
	public UploadProjectSOPBean viewSubmittedSOP(UploadProjectSOPBean uploadProjectSOPBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_sop_submissions` where sapid=? and year=? and month=? limit 1";
			return (UploadProjectSOPBean) jdbcTemplate.queryForObject(sql, new Object[] {uploadProjectSOPBean.getSapId(),uploadProjectSOPBean.getYear(),uploadProjectSOPBean.getMonth()},new BeanPropertyRowMapper(UploadProjectSOPBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public UploadProjectSynopsisBean viewSubmittedSynopsis(UploadProjectSynopsisBean uploadProjectSynopsisBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`student_synopsis_submissions` where sapid = ? and month = ? and year = ? limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {uploadProjectSynopsisBean.getSapid(),uploadProjectSynopsisBean.getMonth(),uploadProjectSynopsisBean.getYear()},new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<LevelBasedSOPConfigBean> getLiveSOPConfigurationList(){
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
				+ "SELECT "
					+ " `pss`.`subject` AS `subject`, "
					+ " `c_t`.`name` AS `consumer_type`, "
					+ " `p`.`code` AS `program`, "
					+ " `p_s`.`program_structure` AS `program_structure`, "
					+ " `l_b_s`.* "
				+ " FROM `exam`.`level_based_sop_config` `l_b_s` "
				+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `l_b_s`.`program_sem_subject_id` "
				+ " LEFT JOIN `exam`.`consumer_program_structure` `c_p_s` ON `c_p_s`.`id` = `pss`.`consumerProgramStructureId` "
				+ " LEFT JOIN `exam`.`consumer_type` `c_t` ON `c_t`.`id` = `c_p_s`.`consumerTypeId` "
				+ " LEFT JOIN `exam`.`program` `p` ON `p`.`id` = `c_p_s`.`programId` "
				+ " LEFT JOIN `exam`.`program_structure` `p_s` ON `p_s`.`id` = `c_p_s`.`programStructureId`;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<LevelBasedSOPConfigBean>(LevelBasedSOPConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<LevelBasedSynopsisConfigBean> getLiveSynopsisConfigurationList(){
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT c_t.name AS consumer_type, p.code AS program, p_s.program_structure AS program_structure, l_b_s.* FROM exam.level_based_synopsis_config l_b_s INNER JOIN exam.program_sem_subject p_s_s ON p_s_s.id = l_b_s.program_sem_subject_id INNER JOIN exam.consumer_program_structure c_p_s ON c_p_s.id = p_s_s.consumerProgramStructureId INNER JOIN exam.consumer_type c_t ON c_t.id = c_p_s.consumerTypeId INNER JOIN exam.program p ON p.id = c_p_s.programId INNER JOIN exam.program_structure p_s ON p_s.id = c_p_s.programStructureId;";
			return jdbcTemplate.query(sql, new BeanPropertyRowMapper<LevelBasedSynopsisConfigBean>(LevelBasedSynopsisConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public LevelBasedSOPConfigBean getConfigBeanBasedOnMasterKey(String masterKey,String year,String month) {
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.level_based_sop_config where year = ? and month = ? and consumer_program_structure_id = ? and live = 'Y'";
			return (LevelBasedSOPConfigBean) jdbcTemplate.queryForObject(sql, new Object[] {year,month,masterKey},new BeanPropertyRowMapper(LevelBasedSOPConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public LevelBasedSynopsisConfigBean getSynopsisConfigBeanBasedOnMasterKey(String pss_id,String year,String month) {
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.level_based_synopsis_config where year = ? and month = ? and program_sem_subject_id = ? limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {year,month,pss_id},new BeanPropertyRowMapper<LevelBasedSynopsisConfigBean>(LevelBasedSynopsisConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<LevelBasedSynopsisConfigBean> getLevelBasedSynopsisConfigBasedOnFilter(LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean){
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select l_s_c.*,c_t.name as consumer_type,p.code as program,p_s.program_structure from exam.level_based_synopsis_config l_s_c inner join exam.program_sem_subject p_s_s on p_s_s.id = l_s_c.program_sem_subject_id inner join exam.consumer_program_structure c_p_s ON c_p_s.id = p_s_s.consumerProgramStructureId INNER JOIN exam.consumer_type c_t ON c_t.id = c_p_s.consumerTypeId INNER JOIN exam.program p ON p.id = c_p_s.programId INNER JOIN exam.program_structure p_s ON p_s.id = c_p_s.programStructureId where l_s_c.year=? and l_s_c.month=? and l_s_c.program_sem_subject_id in ("+ levelBasedSynopsisConfigBean.getProgram_sem_subject_ids() +");";
			return (ArrayList<LevelBasedSynopsisConfigBean>) jdbcTemplate.query(sql, new Object[] {levelBasedSynopsisConfigBean.getYear(),levelBasedSynopsisConfigBean.getMonth()},new BeanPropertyRowMapper<LevelBasedSynopsisConfigBean>(LevelBasedSynopsisConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<LevelBasedSynopsisConfigBean> getLevelBasedSynopsisConfigList(){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select l_s_c.*,c_t.name as consumer_type,p.code as program,p_s.program_structure from exam.level_based_synopsis_config l_s_c inner join exam.program_sem_subject p_s_s on p_s_s.id = l_s_c.program_sem_subject_id inner join exam.consumer_program_structure c_p_s ON c_p_s.id = p_s_s.consumerProgramStructureId INNER JOIN exam.consumer_type c_t ON c_t.id = c_p_s.consumerTypeId INNER JOIN exam.program p ON p.id = c_p_s.programId INNER JOIN exam.program_structure p_s ON p_s.id = c_p_s.programStructureId";
			return jdbcTemplate.query(
				sql, 
				new BeanPropertyRowMapper<LevelBasedSynopsisConfigBean>(LevelBasedSynopsisConfigBean.class)
			);
		} catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public String bulkUpdateSynopsisByYearMonthAndPssId(LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update exam.level_based_synopsis_config set ";
			String comma = "";
			if(levelBasedSynopsisConfigBean.getStart_date() != null && !"".equalsIgnoreCase(levelBasedSynopsisConfigBean.getStart_date())) {
				sql = sql + "start_date='" + levelBasedSynopsisConfigBean.getStart_date() + "'";
				comma = ",";
			}
			if(levelBasedSynopsisConfigBean.getEnd_date() != null && !"".equalsIgnoreCase(levelBasedSynopsisConfigBean.getEnd_date())) {
				sql = sql + comma + "end_date='" + levelBasedSynopsisConfigBean.getEnd_date() + "'";
				comma = ",";
			}
			sql = sql + comma + "max_attempt = '" + levelBasedSynopsisConfigBean.getMax_attempt() + "'";
			comma = ",";
			if(levelBasedSynopsisConfigBean.getPayment_amount() != null && !"".equalsIgnoreCase(levelBasedSynopsisConfigBean.getPayment_amount())) {
				sql = sql + comma + "payment_amount='" + levelBasedSynopsisConfigBean.getPayment_amount() + "'";
				comma = ",";
			}
			sql = sql + " where year=? and month=? and program_sem_subject_id in ("+ levelBasedSynopsisConfigBean.getProgram_sem_subject_ids() +")";
			jdbcTemplate.update(sql,new Object[] {levelBasedSynopsisConfigBean.getYear(),levelBasedSynopsisConfigBean.getMonth()});
			return "true";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String deleteSynopsisByYearMonthAndPssId(LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "delete from exam.level_based_synopsis_config where year=? and month=? and program_sem_subject_id=?";
			jdbcTemplate.update(sql,new Object[] {levelBasedSynopsisConfigBean.getYear(),levelBasedSynopsisConfigBean.getMonth(),levelBasedSynopsisConfigBean.getProgram_sem_subject_id()});
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public void insertIntoConfig(LevelBasedSOPConfigBean levelBasedSOPConfigBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`level_based_sop_config` ( "
					+ " `year`, `month`, `program_sem_subject_id`, "
					+ " `live`, `max_attempt`, `payment_applicable`, "
					+ " `payment_amount`, `start_date`, `end_date`, "
					+ " `createdBy`, `lastModifiedBy`, "
					+ " `createdDate`, `lastModifiedDate` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " sysdate(), sysdate() "
				+ " ) ON DUPLICATE KEY UPDATE"
					+ " `live` = ?, "
					+ " `max_attempt` = ?, "
					+ " `payment_applicable` = ?, "
					+ " `payment_amount` = ?, "
					+ " `start_date` = ?, "
					+ " `end_date` = ?, "
					+ " `lastModifiedBy` = ?, "
					+ " `lastModifiedDate` = sysdate() ";
		jdbcTemplate.update(
			sql, 
			new Object[] {
				levelBasedSOPConfigBean.getYear(), levelBasedSOPConfigBean.getMonth(), levelBasedSOPConfigBean.getProgram_sem_subject_id(),
				levelBasedSOPConfigBean.getLive(), levelBasedSOPConfigBean.getMax_attempt(), levelBasedSOPConfigBean.getPayment_applicable(), 
				levelBasedSOPConfigBean.getPayment_amount(), levelBasedSOPConfigBean.getStart_date(), levelBasedSOPConfigBean.getEnd_date(), 
				levelBasedSOPConfigBean.getCreatedBy(), levelBasedSOPConfigBean.getLastModifiedBy(), 
				levelBasedSOPConfigBean.getLive(),
				levelBasedSOPConfigBean.getMax_attempt(),
				levelBasedSOPConfigBean.getPayment_applicable(), 
				levelBasedSOPConfigBean.getPayment_amount(),
				levelBasedSOPConfigBean.getStart_date(),
				levelBasedSOPConfigBean.getEnd_date(),
				levelBasedSOPConfigBean.getLastModifiedBy()
			}
		);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer>  getProgramStructureIdBasedOnMasterKeyAndSubject(String programId,String programStructureId, String consumerTypeId, String subject){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql =  "SELECT p_s_s.id FROM exam.consumer_program_structure as c_p_s, "
					+ "exam.program_sem_subject as p_s_s "
					+ "where c_p_s.programId in ("+ programId +") "
					+ "and c_p_s.programStructureId in ("+ programStructureId +") "
					+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
					+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ "and p_s_s.subject=?";

			return (ArrayList<Integer>) jdbcTemplate.query(
					sql, new Object[] {subject},  new SingleColumnRowMapper<Integer>(
							Integer.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,ArrayList<LevelBasedSynopsisConfigBean>> insertIntoSynopsisConfig(ArrayList<Integer> pss_ids,LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean){
		try {
			ArrayList<LevelBasedSynopsisConfigBean> levelBasedSOPConfigBeanErrorList = new ArrayList<LevelBasedSynopsisConfigBean>();
			ArrayList<LevelBasedSynopsisConfigBean> levelBasedSOPConfigBeanSuccessList = new ArrayList<LevelBasedSynopsisConfigBean>();
			HashMap<String,ArrayList<LevelBasedSynopsisConfigBean>> responseHashMap = new HashMap<String, ArrayList<LevelBasedSynopsisConfigBean>>();
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = ""
					+ " INSERT INTO `exam`.`level_based_synopsis_config` ( "
						+ " `year`, `month`, `program_sem_subject_id`, `live`, "
						+ " `max_attempt`, `payment_applicable`, `payment_amount`, "
						+ " `start_date`, `end_date`, `question_filePath`, "
						+ " `question_previewPath`, `created_by`, `updated_by` "
					+ " ) VALUES ( "
						+ " ?, ?, ?, ?, "
						+ " ?, ?, ?, "
						+ " ?, ?, ?, "
						+ " ?, ?, ? "
					+ " ) ON DUPLICATE KEY UPDATE "
					+ " `live` = ?, "
					+ " `max_attempt` = ?, "
					+ " `payment_applicable` = ?, "
					+ " `payment_amount` = ?, "
					+ " `start_date` = ?, "
					+ " `end_date` = ?, "
					+ " `question_filePath` = ?, "
					+ " `question_previewPath` = ?, "
					+ " `updated_by` = ? ";
			for (int i = 0; i < pss_ids.size(); i++) {
				try {
					levelBasedSynopsisConfigBean.setProgram_sem_subject_id(pss_ids.get(i));
					Object[] parameters = new Object[] {
						levelBasedSynopsisConfigBean.getYear(), levelBasedSynopsisConfigBean.getMonth(), levelBasedSynopsisConfigBean.getProgram_sem_subject_id(), levelBasedSynopsisConfigBean.getLive(), 
						levelBasedSynopsisConfigBean.getMax_attempt(), levelBasedSynopsisConfigBean.getPayment_applicable(), levelBasedSynopsisConfigBean.getPayment_amount(), 
						levelBasedSynopsisConfigBean.getStart_date(), levelBasedSynopsisConfigBean.getEnd_date(), levelBasedSynopsisConfigBean.getQuestion_filePath(), 
						levelBasedSynopsisConfigBean.getQuestion_previewPath(), levelBasedSynopsisConfigBean.getCreated_by(), levelBasedSynopsisConfigBean.getUpdated_by(),

						levelBasedSynopsisConfigBean.getLive(),
						levelBasedSynopsisConfigBean.getMax_attempt(),
						levelBasedSynopsisConfigBean.getPayment_applicable(),
						levelBasedSynopsisConfigBean.getPayment_amount(),
						levelBasedSynopsisConfigBean.getStart_date(),
						levelBasedSynopsisConfigBean.getEnd_date(),
						levelBasedSynopsisConfigBean.getQuestion_filePath(),
						levelBasedSynopsisConfigBean.getQuestion_previewPath(),
						levelBasedSynopsisConfigBean.getUpdated_by()
					};
					int status = jdbcTemplate.update(
						sql, 
						parameters
					);
					if(status <= 0)
					{
						levelBasedSOPConfigBeanErrorList.add(levelBasedSynopsisConfigBean);
					}else {
						levelBasedSOPConfigBeanSuccessList.add(levelBasedSynopsisConfigBean);
					}
				}
				catch (Exception e) {
					
					levelBasedSOPConfigBeanErrorList.add(levelBasedSynopsisConfigBean);
				}
			}
			responseHashMap.put("success", levelBasedSOPConfigBeanSuccessList);
			responseHashMap.put("error", levelBasedSOPConfigBeanErrorList);
			return responseHashMap;
		}	
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public VivaSlotBookingConfigBean getVivaSlotBookingConfig(String pss_id,String year,String month) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from `exam`.`viva_slots_config` where year = ? and month=? and program_sem_subject_id = ? and start_date <= sysdate() and end_date >= sysdate() limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {"2021","Dec",pss_id},new BeanPropertyRowMapper<VivaSlotBookingConfigBean>(VivaSlotBookingConfigBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VivaSlotBean> getVivaSlots(String year,String month){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select v_s.*,((capacity - booked)) as remaining from ( select v_s.*,(select count(*) from exam.viva_slot_booking where viva_slots_id = v_s.id and booked = 'Y' or payment_status = 'initiated') as booked from exam.viva_slots v_s where v_s.year=? and v_s.month=?) v_s where (capacity - booked) > 0 group by v_s.`date`;";
			return jdbcTemplate.query(sql, new Object[] {year,month},new BeanPropertyRowMapper<VivaSlotBean>(VivaSlotBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public VivaSlotBean getVivaSlotDateTimeById(String id){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select v_s.*,(v_s.capacity - (select count(*) as total from exam.viva_slot_booking where viva_slots_id = v_s.id and booked='Y' or payment_status='Initiated')) as remaining from exam.viva_slots v_s where id=? limit 1;";
			return jdbcTemplate.queryForObject(sql, new Object[] {id},new BeanPropertyRowMapper<VivaSlotBean>(VivaSlotBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean createVivaSlotBooking(VivaSlotBean vivaSlotBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "insert into `exam`.`viva_slot_booking`(`year`,`month`,`sapid`,`viva_slots_id`,`booked`,`payment_status`,`track_id`,`created_by`,`updated_by`) values(?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql,new Object[] {
					vivaSlotBean.getYear(),
					vivaSlotBean.getMonth(),
					vivaSlotBean.getSapid(),
					vivaSlotBean.getViva_slots_id(),
					vivaSlotBean.getBooked(),
					vivaSlotBean.getPayment_status(),
					vivaSlotBean.getTrack_id(),
					vivaSlotBean.getCreated_by(),
					vivaSlotBean.getUpdated_by()
				});
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<VivaSlotBean> getVivaSlotsDateTimeByDate(String year,String month,String date){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select v_s.*,((capacity - booked)) as remaining from ( select v_s.*,(select count(*) from exam.viva_slot_booking where viva_slots_id = v_s.id and booked = 'Y' or payment_status = 'initiated') as booked from exam.viva_slots v_s where v_s.year=? and v_s.month=?) v_s where (capacity - booked) > 0 and date = ?";
			return jdbcTemplate.query(sql, new Object[] {year,month,date},new BeanPropertyRowMapper<VivaSlotBean>(VivaSlotBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public int getProgramSemSubjectId(String subject,String consumerProgramStructureId) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT id FROM exam.program_sem_subject where subject = ? and consumerProgramStructureId = ? and active='Y'";
			return jdbcTemplate.queryForObject(sql, new Object[] {subject,consumerProgramStructureId},Integer.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return 0;
		}
	}
	
	@Transactional(readOnly = true)
	public VivaSlotBean vivaSlotAlreadyBooked(String sapid,String year,String month) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select v_s.date,v_s.start_time,v_s.end_time,v_s_b.* from exam.viva_slot_booking v_s_b inner join exam.viva_slots v_s on v_s.id = v_s_b.viva_slots_id where v_s_b.sapid=? and v_s_b.year=? and v_s_b.month=? and v_s_b.booked='Y' limit 1";
			return jdbcTemplate.queryForObject(sql, new Object[] {sapid,"2021","Dec"},new BeanPropertyRowMapper<VivaSlotBean>(VivaSlotBean.class));
		}
		catch(EmptyResultDataAccessException e) {
			return new VivaSlotBean(); 
		}
		catch (IncorrectResultSizeDataAccessException e) {
			// TODO: handle exception
			return new VivaSlotBean(); 
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean deleteStudentGuideMappingForACycle(LevelBasedProjectBean levelBasedProjectBean) {
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "delete from `exam`.`student_guide_mapping` where year = ? and month=?"
					+ " and facultyId=? and sapId=?";
			
			int status = jdbcTemplate.update(sql,new Object[] {
					levelBasedProjectBean.getYear(),
					levelBasedProjectBean.getMonth(),
					levelBasedProjectBean.getFacultyId(),
					levelBasedProjectBean.getSapId()
					
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

	public LevelBasedProjectBean getRecentStudentGuideMapping(String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		LevelBasedProjectBean bean = new LevelBasedProjectBean();
		
//		try { 
			String sql = "select g.* from exam.student_guide_mapping g where sapid=? order by lastModifiedDate desc limit 1 ";
			bean =  jdbcTemplate.queryForObject(sql, new Object[] {sapid}, new BeanPropertyRowMapper<LevelBasedProjectBean>(LevelBasedProjectBean.class));
		    return bean;
//		}
//		catch (Exception e) { 
//			e.printStackTrace();
//		}
//		return bean;
	}
}
