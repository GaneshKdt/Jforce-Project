package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.PaymentGatewayTransactionBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.ProjectTitleConfig;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBookingConfigBean;


@Component
public class ProjectTitleDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;


	@Autowired
	StudentMarksDAO marksDao;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	@Transactional(readOnly = true)
	public String getProgramById(String programId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `name` FROM `exam`.`program` WHERE `id` = ? ";
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { programId },
            String.class
	    );
	}
	@Transactional(readOnly = true)
	public String getProgramStrucutreById(String programStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `program_structure` FROM `program_structure` WHERE `id` = ? ";
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { programStructureId },
            String.class
	    );
	}
	@Transactional(readOnly = true)
	public ProjectTitle getSingleProjectTitle(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pt`.*, "
				+ " `pss`.`subject` AS `subject`, "
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_titles` `pt` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`prgm_sem_subj_id` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `pt`.`id` = ? ";
		ProjectTitle title = jdbcTemplate.queryForObject(
	            sql, 
	            new Object[] { id }, 
	            new BeanPropertyRowMapper<ProjectTitle>(ProjectTitle.class)
	    );
		return title;
	}
	@Transactional(readOnly = false)
	public String updateProjectTopic(ProjectTitle title) {
		String sql = ""
				+ " UPDATE `exam`.`project_titles` "
				+ " SET `active` = ?, `title` = ?, `updatedBy` = ? "
				+ " WHERE `id` = ?; ";

		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					title.getActive(), title.getTitle(), title.getUpdatedBy(), title.getId()
				}
			);
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}

	}
	@Transactional(readOnly = false)
	public String deleteProjectTitle(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " DELETE FROM `exam`.`project_titles` WHERE ID = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { id }
			);
			return "Successfully deleted";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}
	@Transactional(readOnly = false)
	public String toggleActiveProjectTitle(String id, String active) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " UPDATE `exam`.`project_titles` SET `active` = ? WHERE `id` = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { active, id }
			);
			return "Successfully toggled inactive";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}
	@Transactional(readOnly = true)
	public List<ProjectTitle> getAllProjectTitles() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pt`.*, "

				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_titles` `pt` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`prgm_sem_subj_id` "
			+ " LEFT JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " LEFT JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " LEFT JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` ";
		List<ProjectTitle> titles = jdbcTemplate.query(
	            sql, 
	            new BeanPropertyRowMapper<ProjectTitle>(ProjectTitle.class)
	    );
		return titles;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfPSSIdExists(String consumerType, String programCode, String programStructure, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`consumer_program_structure` `cps` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`consumerProgramStructureId` = `cps`.`id`"
			+ " WHERE "
				+ " `consumerTypeId` = ? "
			+ " AND `programId` = ? "
			+ " AND `programStructureId` = ? "
			+ " AND `pss`.`subject` = ?";
		int count = jdbcTemplate.queryForObject(
	            sql, 
	            new Object[] { consumerType, programCode, programStructure, subject }, 
	            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfPSSIdExistsByMasterKey(String consumerProgramStructureId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`consumer_program_structure` `cps` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`consumerProgramStructureId` = `cps`.`id`"
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `pss`.`subject` = ?";
		int count = jdbcTemplate.queryForObject(
	            sql, 
	            new Object[] { consumerProgramStructureId, subject }, 
	            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = false)
	public void saveProjectTitle(ProjectTitle title) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`project_titles` ( "
					+ " `prgm_sem_subj_id`, `title`, `active`, "
					+ " `createdBy`, `createdDate`, "
					+ " `updatedBy`, `updatedDate` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, sysdate(), "
					+ " ?, sysdate() "
				+ " ); ";

		jdbcTemplate.update(sql, new Object[] { 
			title.getPrgm_sem_subj_id(), title.getTitle(), title.getActive(),
			title.getCreatedBy(), 
			title.getUpdatedBy()
		});

	}
	@Transactional(readOnly = true)
	public boolean getProjectTitleStatus(AssignmentFileBean assignmentFile, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT count(*) FROM `exam`.`project_title_student_mapping` WHERE `examYear` = ? AND `examMonth` = ? and `sapid` = ? ";


			int count = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{
					assignmentFile.getYear(), assignmentFile.getMonth(), sapId
				}, 
				Integer.class
			);

			return count > 0;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	} 
	@Transactional(readOnly = true)
	public boolean getProjectTitleList(AssignmentFileBean assignmentFile, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT count(*) FROM `exam`.`project_titles`"
					+ " WHERE `examYear` = ? AND `examMonth` = ? and `sapid` = ? ";


			int count = jdbcTemplate.queryForObject(
				sql, 
				new Object[]{
					assignmentFile.getYear(), assignmentFile.getMonth(), sapId
				}, 
				Integer.class
			);

			return count > 0;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	}
	@Transactional(readOnly = true)
	public List<ProjectTitle> getProjectTitleListForStudent(String year, String month, String consumerProgramStructureId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = ""
			+ " SELECT "
				+ " `pt`.*, "

				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_titles` `pt` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`prgm_sem_subj_id` "
			+ " LEFT JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " LEFT JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " LEFT JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `pt`.`active` = 'Y' AND `consumerProgramStructureId` = ? AND `subject` = ? ";

			List<ProjectTitle> list = jdbcTemplate.query(
				sql, 
				new Object[]{
					consumerProgramStructureId, subject
				}, 
				new BeanPropertyRowMapper<ProjectTitle>(ProjectTitle.class)
			);

			return list;
		} catch (Exception e) {
			
		}
		return new ArrayList<ProjectTitle>();

	}
	@Transactional(readOnly = false)
	public void saveProjectTitleSelectionForStudent(ProjectTitle title) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`project_title_student_mapping` ( "
					+ " `sapid`, `titleId`, `examYear`, "
					+ " `examMonth`, `createdBy`, `updatedBy` "
				+ " ) VALUES  ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ? "
				+ " ); ";

		jdbcTemplate.update(sql, new Object[] { 
			title.getSapid(), title.getTitleId(), title.getExamYear(),
			title.getExamMonth(), title.getCreatedBy(), title.getUpdatedBy()
		});

	}
	@Transactional(readOnly = true)
	public String getProjectTitleForStudent(AssignmentFileBean assignmentFile, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = ""
				+ " SELECT `title` "
				+ " FROM `exam`.`project_title_student_mapping` `ptsm` "
				+ " INNER JOIN `exam`.`project_titles` `pt` ON `pt`.`id` = `ptsm`.`titleId` "
				+ " WHERE `ptsm`.`examYear` = ? AND `ptsm`.`examMonth` = ? and `ptsm`.`sapid` = ? ";


			return jdbcTemplate.queryForObject(
				sql, 
				new Object[]{
					assignmentFile.getYear(), assignmentFile.getMonth(), sapId
				}, 
				String.class
			);
		} catch (Exception e) {
			
		}
		return null;
	}
	@Transactional(readOnly = true)
	public boolean checkIfProgramSemSubjectIdExists(ProjectConfiguration bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`program_sem_subject` `pss` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `ct`.`name` = ? "
			+ " AND `code` = ? "
			+ " AND `program_structure` = ? "
			+ " AND `pss`.`subject` = ? ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { bean.getConsumerType(), bean.getProgramCode(), bean.getProgramStructure(), bean.getSubject() }, 
            Integer.class
	    );
		
		return count > 0;
	}
	
	@Transactional(readOnly = true)
	public String getProgramSemSubjectId(String consumerType, String programCode, String programStructure, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pss`.`id` "
			+ " FROM `exam`.`program_sem_subject` `pss` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `ct`.`name` = ? "
			+ " AND `code` = ? "
			+ " AND `program_structure` = ? "
			+ " AND `pss`.`subject` = ? ";
		String cpsId = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { consumerType, programCode, programStructure, subject },
            String.class
	    );
		return cpsId;
	}
	@Transactional(readOnly = true)
	public String getProgramSemSubjectIdByKeys(String consumerTypeId, String programId, String programStructureId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pss`.`id` "
			+ " FROM `exam`.`program_sem_subject` `pss` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `ct`.`id` = ? "
			+ " AND `p`.`id` = ? "
			+ " AND `ps`.`id` = ? "
			+ " AND `pss`.`subject` = ? "; 
		String cpsId = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { consumerTypeId, programId, programStructureId, subject },
            String.class
	    );
		return cpsId;
	}
	@Transactional(readOnly = true)
	public String getProgramSemSubjectIdByMasterKey(String masterKey, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `id` "
			+ " FROM `exam`.`program_sem_subject` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `subject` = ? ";
		String cpsId = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { masterKey, subject },
            String.class
	    );
		return cpsId;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfSubjectActive(String programSemSubjectId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`program_sem_subject` `pss` "
			
			+ " WHERE `id` = ? "
			+ " AND `active` = 'Y' ";
		int count = jdbcTemplate.queryForObject(
            sql, 
            new Object[] { programSemSubjectId }, 
            Integer.class
	    );
		return count > 0;
	}
	@Transactional(readOnly = true)
	public List<ProjectConfiguration> getAllProjectConfigurations() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pc`.*, "

				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_configuration` `pc` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pc`.`programSemSubjId` "
			+ " LEFT JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " LEFT JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " LEFT JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` ";
		
		return jdbcTemplate.query(
            sql, 
            new BeanPropertyRowMapper<ProjectConfiguration>(ProjectConfiguration.class)
	    );
	}
	
	
	@Transactional(readOnly = false)
	public void saveProjectConfiguration(ProjectConfiguration configuration) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`project_configuration` ( "
					+ " `examYear`, `examMonth`, `programSemSubjId`, "
					+ " `hasTitle`, `hasSOP`, `hasViva`, "
					+ " `hasSynopsis`, `hasSubmission`, "
					+ " `createdBy`, `lastModifiedBy`"
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " ?, ? "
				+ " ); ";

		jdbcTemplate.update(sql, new Object[] { 
			configuration.getExamYear(), configuration.getExamMonth(), configuration.getProgramSemSubjId(),
			configuration.getHasTitle(), configuration.getHasSOP(), configuration.getHasViva(),
			configuration.getHasSynopsis(), configuration.getHasSubmission(), 
			configuration.getCreatedBy(), configuration.getUpdatedBy()
		});

	}
	
	@Transactional(readOnly = true)
	public ProjectConfiguration getSingleProjectConfiguration(ProjectConfiguration configuration) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pc`.*, "

				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_configuration` `pc` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pc`.`programSemSubjId` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `pc`.`id` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { configuration.getId() },
            new BeanPropertyRowMapper<ProjectConfiguration>(ProjectConfiguration.class)
	    );
	}
	@Transactional(readOnly = false)	
	public String updateProjectConfiguration(ProjectConfiguration configuration) {
		String sql = ""
				+ " UPDATE `exam`.`project_configuration` "
				+ " SET `hasTitle` = ?, `hasSOP` = ?, `hasViva` = ?, "
				+ " `hasSynopsis` = ?, `hasSubmission` = ?, `lastModifiedBy` = ? "
				+ " WHERE `id` = ?; ";

		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					configuration.getHasTitle(), configuration.getHasSOP(), configuration.getHasViva(), 
					configuration.getHasSynopsis(), configuration.getHasSubmission(), configuration.getUpdatedBy(),
					configuration.getId()
				}
			);
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}

	}
	
	@Transactional(readOnly = true)
	public List<ProjectModuleExtensionBean> getProjectModuleExtensionList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pm`.*, `s`.`firstname`, `s`.`lastname`, `pss`.`subject` "
			+ " FROM `exam`.`project_module_extension` `pm` "
			+ " INNER JOIN `exam`.`students` `s` ON `s`.`sapid` = `pm`.`sapid` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pm`.`programSemSubjId` ";
		
		return jdbcTemplate.query(
            sql, 
            new BeanPropertyRowMapper<ProjectModuleExtensionBean>(ProjectModuleExtensionBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public ProjectModuleExtensionBean getSingleProjectModuleExtension(ProjectModuleExtensionBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pm`.*, `s`.`firstname`, `s`.`lastname`, `pss`.`subject` "
			+ " FROM `exam`.`project_module_extension` `pm` "
			+ " INNER JOIN `exam`.`students` `s` ON `s`.`sapid` = `pm`.`sapid` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pm`.`programSemSubjId` "
			+ " WHERE `pm`.`id` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { bean.getId() },
            new BeanPropertyRowMapper<ProjectModuleExtensionBean>(ProjectModuleExtensionBean.class)
	    );
	}
	@Transactional(readOnly = false)
	public String insertProjectExtension(ProjectModuleExtensionBean inputBean) {
		String sql = ""
				+ " INSERT INTO `exam`.`project_module_extension` ( "
					+ " `examYear`, `examMonth`, `sapid`, "
					+ " `programSemSubjId`, `moduleType`, `endDate`, "
					+ " `createdBy`, `updatedBy` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ? "
				+ " ); ";

		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					inputBean.getExamYear(), inputBean.getExamMonth(), inputBean.getSapid(), 
					inputBean.getProgramSemSubjId(), inputBean.getModuleType(), inputBean.getEndDate(),
					inputBean.getCreatedBy(), inputBean.getUpdatedBy()
				}
			);
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}
	}
	@Transactional(readOnly = true)
	public String getProgramSemSubjectIdForStudent(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `pss`.`id` "
			+ " FROM `exam`.`students` `s` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`consumerProgramStructureId` = `s`.`consumerProgramStructureId`"
			+ " WHERE `sapid` = ? "
			+ " AND `subject` = ? ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { sapid, subject }, 
            String.class
	    );
	}
	@Transactional(readOnly = false)
	public String deleteProjectConfiguration(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " DELETE FROM `exam`.`project_configuration` WHERE ID = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { id }
			);
			return "Successfully deleted";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}
	@Transactional(readOnly = false)
	public String deleteProjectModuleExtension(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " DELETE FROM `exam`.`project_module_extension` WHERE ID = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { id }
			);
			return "Successfully deleted";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}
	@Transactional(readOnly = false)
	public String updateProjectModuleExtension(ProjectModuleExtensionBean bean) {
		String sql = ""
				+ " UPDATE `exam`.`project_module_extension` "
				+ " SET `moduleType` = ?, `endDate` = ?, `updatedBy` = ? "
				+ " WHERE `id` = ?; " ;

		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					bean.getModuleType(), bean.getEndDate(), bean.getUpdatedBy(), 
					bean.getId()
				}
			);
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}

	}
	@Transactional(readOnly = false)
	public void saveProjectTitleConfig(ProjectTitleConfig title) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `exam`.`project_title_config` ( "
					+ " `examYear`, `examMonth`, `programSemSubjId`, "
					+ " `active`, `start_date`, `end_date`, "
					+ " `createdBy`, `createdDate`, "
					+ " `updatedBy`, `updatedDate` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, sysdate(), "
					+ " ?, sysdate() "
				+ " ) ON DUPLICATE KEY UPDATE "
				+ " `start_date` = ?, "
				+ " `end_date` = ? ";

		jdbcTemplate.update(sql, new Object[] { 
			title.getExamYear(),  title.getExamMonth(), title.getProgramSemSubjId(),
			title.getActive(), title.getStart_date(), title.getEnd_date(), 
			title.getCreatedBy(), title.getUpdatedBy(), title.getStart_date(), title.getEnd_date()
		});

	}
	@Transactional(readOnly = true)
	public String getProgramByConsumerProgramStructureId(String cpsId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `p`.`name` "
			+ " FROM `exam`.`consumer_program_structure` `cps` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " WHERE `cps`.`id` = ? ";
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { cpsId },
            String.class
	    );
	}
	@Transactional(readOnly = true)
	public String getProgramStrucutreByConsumerProgramStructureId(String cpsId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `ps`.`program_structure` "
			+ " FROM `exam`.`consumer_program_structure` `cps` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " WHERE `cps`.`id` = ? ";
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { cpsId },
            String.class
	    );
	}
	@Transactional(readOnly = true)
	public List<ProjectTitleConfig> getAllProjectTitleConfig() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pt`.*, "

				+ " `pss`.`subject` AS `subject`, "
				
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
			+ " FROM `exam`.`project_title_config` `pt` "
			+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`programSemSubjId` "
			+ " LEFT JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " LEFT JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " LEFT JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " LEFT JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` ";
		return jdbcTemplate.query(
	            sql, 
	            new BeanPropertyRowMapper<ProjectTitleConfig>(ProjectTitleConfig.class)
	    );
	}
	@Transactional(readOnly = true)
	public ProjectTitleConfig getSingleProjectTitleConfig(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " `pt`.*, "
				+ " `pss`.`subject` AS `subject`, "
				+ " `p`.`id` AS `programId`, "
				+ " `p`.`name` AS `programName`, "
				+ " `p`.`code` AS `programCode`, "
				
				+ " `ct`.`id` AS `consumerTypeId`, "
				+ " `ct`.`name` AS `consumerType`, "
				
				+ " `ps`.`id` AS `programStructureId`, "
				+ " `ps`.`program_structure` AS `programStructure` "
				+ " FROM `exam`.`project_title_config` `pt` "
				+ " LEFT JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `pt`.`programSemSubjId` "
			+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
			+ " INNER JOIN `exam`.`program` `p` ON `cps`.`programId` = `p`.`id` "
			+ " INNER JOIN `exam`.`program_structure` `ps` ON `cps`.`programStructureId` = `ps`.`id` "
			+ " INNER JOIN `exam`.`consumer_type` `ct` ON `cps`.`consumerTypeId` = `ct`.`id` "
			+ " WHERE `pt`.`id` = ? ";
		return jdbcTemplate.queryForObject(
	            sql, 
	            new Object[] { id }, 
	            new BeanPropertyRowMapper<ProjectTitleConfig>(ProjectTitleConfig.class)
	    );
	}
	@Transactional(readOnly = false)
	public String toggleActiveProjectTitleConfig(String id, String active) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " UPDATE `exam`.`project_title_config` SET `active` = ? WHERE `id` = ? ";
		try {
			jdbcTemplate.update(
				sql,
				new Object[] { active, id }
			);
			return "Successfully toggled inactive";
		} catch (Exception e) {
			
			return e.getMessage();
		}
	}
	@Transactional(readOnly = false)
	public String updateProjectTitleConfig(ProjectTitleConfig title) {
		String sql = ""
				+ " UPDATE `exam`.`project_title_config` "
				+ " SET `active` = ?, `start_date` = ?, `end_date` = ?, `updatedBy` = ? "
				+ " WHERE `id` = ?; ";

		try {
			jdbcTemplate.update(
				sql,
				new Object[] {
					title.getActive(), title.getStart_date(), title.getEnd_date(), title.getUpdatedBy(), title.getId()
				}
			);
			return null;
		}catch (Exception e) {
			return e.getMessage();
		}

	}
	@Transactional(readOnly = true)
	public ProjectConfiguration getSingleLevelBasedProjectConfiguration(String examYear, String examMonth, String programSemSubjId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`project_configuration` `pc` "
			+ " WHERE `pc`.`examYear` = ? AND `pc`.`examMonth` = ? AND `pc`.`programSemSubjId` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, programSemSubjId },
            new BeanPropertyRowMapper<ProjectConfiguration>(ProjectConfiguration.class)
	    );
	}
	@Transactional(readOnly = true)
	public List<ProjectModuleExtensionBean> getProjectExtensionsListForStudent(String examYear, String examMonth,
			String sapid, String programSemSubjId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`project_module_extension` `pme` "
			+ " WHERE `pme`.`examYear` = ? AND `pme`.`examMonth` = ? AND `pme`.`sapid` = ? AND `pme`.`programSemSubjId` = ? ";
		
		return jdbcTemplate.query(
            sql, 
            new Object[] { examYear, examMonth, sapid, programSemSubjId },
            new BeanPropertyRowMapper<ProjectModuleExtensionBean>(ProjectModuleExtensionBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public LevelBasedSOPConfigBean getSOPConfiguration(String examYear, String examMonth, String pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`level_based_sop_config` `sopc` "
			+ " WHERE `sopc`.`year` = ? AND `sopc`.`month` = ? AND `sopc`.`program_sem_subject_id` = ? ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, pssId },
            new BeanPropertyRowMapper<LevelBasedSOPConfigBean>(LevelBasedSOPConfigBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public UploadProjectSOPBean getSOPCount(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`student_sop_submissions` `sops` "
			+ " WHERE `sops`.`year` = ? AND `sops`.`month` = ? AND `sops`.`sapid` = ? limit 1";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, sapid },
            new BeanPropertyRowMapper<UploadProjectSOPBean>(UploadProjectSOPBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public boolean getSOPSubmissionStatusForStudent(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`student_sop_submissions` `sops` "
			+ " WHERE `sops`.`year` = ? AND `sops`.`month` = ? AND `sops`.`sapid` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, sapid },
            Integer.class
	    ) > 0;
	}
	@Transactional(readOnly = true)
	public boolean getSOPSubmissionStatusForStudentV2(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`student_sop_submissions` `sops` "
			+ " WHERE `sops`.`year` = ? AND `sops`.`month` = ? AND `sops`.`sapid` = ? and status in (\"Submitted\",\"Rejected\",\"Approved\")";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, sapid },
            Integer.class
	    ) > 0;
	}
	@Transactional(readOnly = true)
	public FacultyExamBean getSOPGuideNameForStudent(String examYear, String examMonth, String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `f`.* "
			+ " FROM `exam`.`student_guide_mapping` `sgm` "
			+ " INNER JOIN `acads`.`faculty` `f` ON `f`.`facultyId` = `sgm`.`facultyId` "
			+ " WHERE `sgm`.`year` = ? AND `sgm`.`month`= ? AND `sgm`.`sapid` = ? AND `sgm`.`subject` = ?";
		
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { examYear, examMonth, sapid, subject },
            new BeanPropertyRowMapper<FacultyExamBean>(FacultyExamBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public FacultyExamBean getSynopsisGuideNameForStudent(String examYear, String examMonth, String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `f`.* "
			+ " FROM `exam`.`student_guide_mapping` `sgm` "
			+ " INNER JOIN `acads`.`faculty` `f` ON `f`.`facultyId` = `sgm`.`facultyId` "
			+ " WHERE `sgm`.`year` = ? AND `sgm`.`month`= ? AND `sgm`.`sapid` = ? AND `sgm`.`subject` = ?";
		
		return jdbcTemplate.queryForObject(
            sql,
            new Object[] { examYear, examMonth, sapid, subject },
            new BeanPropertyRowMapper<FacultyExamBean>(FacultyExamBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public LevelBasedSynopsisConfigBean getSynopsisConfiguration(String examYear, String examMonth,
			String programSemSubjectId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`level_based_synopsis_config` `sopc` "
			+ " WHERE `sopc`.`year` = ? AND `sopc`.`month` = ? AND `sopc`.`program_sem_subject_id` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, programSemSubjectId },
            new BeanPropertyRowMapper<LevelBasedSynopsisConfigBean>(LevelBasedSynopsisConfigBean.class)
	    );
	}

	@Transactional(readOnly = true)
	public UploadProjectSynopsisBean getSynopsisCount(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`student_synopsis_submissions` `sops` "
			+ " WHERE `sops`.`year` = ? AND `sops`.`month` = ? AND `sops`.`sapid` = ? limit 1 ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, sapid },
            new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class)
	    );
	}
	@Transactional(readOnly = true)
	public boolean getSynopsisSubmissionStatusForStudent(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`student_synopsis_submissions` `sops` "
			+ " WHERE `sops`.`year` = ? AND `sops`.`month` = ? AND `sops`.`sapid` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, sapid },
            Integer.class
	    ) > 0;
	}
	@Transactional(readOnly = true)
	public boolean checkIfProgramSemSubjectIdApplicableForStudent(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`students` `s` "
			+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`consumerProgramStructureId` = `s`.`consumerProgramStructureId`"
			+ " WHERE `sapid` = ? "
			+ " AND `subject` = ? "
			+ " AND `active` = 'Y' ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { sapid, subject }, 
            Integer.class
	    ) > 0;
	}
	
	////Download Eligible List for PD-WM Module 4 report
	@Transactional(readOnly = true)
	public List<StudentExamBean> getProjectApplicableStudents(String consumerProgramStructureId,String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"    MAX(r.sem) AS sem," + 
				"    r.sapid," + 
				"    r.month," +
				"    r.year," +
				"    s.firstName," + 
				"    s.lastname," + 
				"    s.emailId," + 
				"    s.mobile," + 
				"    s.program," + 
				"    s.enrollmentMonth," + 
				"    s.enrollmentYear," + 
				"    s.validityEndMonth," + 
				"    s.validityEndYear," + 
				"    s.centerCode " + 
				"FROM" + 
				"    exam.registration r" + 
				"        INNER JOIN" + 
				"    exam.students s ON s.sapid = r.sapid " + 
				"WHERE" + 
				"    r.consumerProgramStructureId = ?" +
				"        AND r.sem >= ? " + 
				"GROUP BY sapid";
		return (List<StudentExamBean>) jdbcTemplate.query(sql, new Object[]{consumerProgramStructureId,sem},new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
	    
	}
	
	
	@Transactional(readOnly = true)
	public boolean checkIfStudentPassProject(String sapid,String subject,String isPass) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from `exam`.`passfail`  where  `sapid` = ? and `subject` = ? and `isPass` = ?  ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] {sapid,subject,isPass},
            Integer.class
	    ) > 0;
	}
	//Download Eligible List for PD-WM Module 4 report
	
	////Download SOP & Synopsis Submission Report
	@Transactional(readOnly = true)
	public List<UploadProjectSOPBean> getSOPSubmissionList(String month,String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from `exam`.`student_sop_submissions` where  `month` = ? and `year` = ? ";
		return (List<UploadProjectSOPBean>) jdbcTemplate.query(sql, new Object[]{month,year},new BeanPropertyRowMapper<UploadProjectSOPBean>(UploadProjectSOPBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<PaymentGatewayTransactionBean> getSOPorSynopsisTransactionList(List<String> trackIDList) {
		
		List<PaymentGatewayTransactionBean> listOfSOPorSynopsisTransaction = new ArrayList<PaymentGatewayTransactionBean>();
		NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		//Create MapSqlParameterSource object
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql = new StringBuilder("SELECT `track_id`,`sapid`,`amount`,`transaction_status`,`transaction_id`,`response_payment_method`,`payment_option`,`bank_name`,`created_at` FROM `payment_gateway`.`transaction` where `track_id` in (:trackIDList) ").toString();
		
		//Adding parameters in SQL parameter map.
		queryParams.addValue("trackIDList", trackIDList);
		try {
			listOfSOPorSynopsisTransaction = (List<PaymentGatewayTransactionBean>) namedJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper<PaymentGatewayTransactionBean>(PaymentGatewayTransactionBean.class));
		} catch (Exception e) {
			
		}
		return listOfSOPorSynopsisTransaction;
	}
	
	@Transactional(readOnly = true)
	public List<UploadProjectSynopsisBean> getSynopsisSubmissionList(String month,String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from `exam`.`student_synopsis_submissions` where  `month` = ? and `year` = ? ";
		return (List<UploadProjectSynopsisBean>) jdbcTemplate.query(sql, new Object[]{month,year},new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class));
	}
	//Download SOP & Synopsis Submitted and Transaction Report
	
	//Download Synopsis Evaluated Score Report Form
	@Transactional(readOnly = true)
	public List<UploadProjectSynopsisBean> getSynopsisEvaluatedScoreList(String month,String year,String evaluated) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from `exam`.`student_synopsis_submissions` where `month` = ? and `year` = ? and `evaluated` = ? ";
		return (List<UploadProjectSynopsisBean>) jdbcTemplate.query(sql, new Object[]{month,year,evaluated},new BeanPropertyRowMapper<UploadProjectSynopsisBean>(UploadProjectSynopsisBean.class));

	}
	
	@Transactional(readOnly = true)
	public String getCPSID(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " select `consumerProgramStructureId` from `exam`.`registration` where `sem` = (select max(`sem`) from `exam`.`registration` where `sapid` = ?) and `sapid` = ? ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { sapid,sapid },
            String.class
	    );
	}
	
	@Transactional(readOnly = true)
	public List<String> getPSSIDList(String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select `id` from `exam`.`program_sem_subject` where `consumerProgramStructureId` = ?";
		return jdbcTemplate.queryForList(sql, new Object[]{consumerProgramStructureId}, String.class);
	}
	
	@Transactional(readOnly = true)
	public String getProgramSemSubjectId(String month, String year, String pssIDList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `program_sem_subject_id` FROM `exam`.`level_based_synopsis_config` where `month` = ? and `year` = ? and `program_sem_subject_id` in (" + pssIDList + ") ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { month, year },
            String.class
	    );
	}
	
	@Transactional(readOnly = true)
	public ProjectModuleExtensionBean getProjectExtensionsEndDateModuleType(String examMonth, String examYear, String sapid, String moduleType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select max(`endDate`) as `endDate` FROM `exam`.`project_module_extension` where `examMonth` = ? and `examYear` = ? and `sapid` = ? and `moduleType` = ? ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examMonth,examYear,sapid,moduleType },
            new BeanPropertyRowMapper<ProjectModuleExtensionBean>(ProjectModuleExtensionBean.class)
	    );
	}

	@Transactional(readOnly = true)
	public VivaSlotBookingConfigBean getSingleLevelBasedVivaConfiguration(String examYear, String examMonth,
			String programSemSubjId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`viva_slots_config` `vc` "
			+ " WHERE `vc`.`year` = ? AND `vc`.`month` = ? AND `vc`.`program_sem_subject_id` = ? ";
		
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] { examYear, examMonth, programSemSubjId },
            new BeanPropertyRowMapper<VivaSlotBookingConfigBean>(VivaSlotBookingConfigBean.class)
	    );
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getProjectConfigurationMonthYear() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select YEAR(MAX(STR_TO_DATE(concat('01',examMonth, examYear), '%d%b%Y') )) as year," + 
					" DATE_FORMAT(MAX(STR_TO_DATE(concat('01',examMonth, examYear), '%d%b%Y') ),'%b') as month" + 
					" from exam.project_configuration ";
		return jdbcTemplate.queryForObject(
            sql, 
            new Object[] {},
            new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
	    );
	}
	
	
	
}