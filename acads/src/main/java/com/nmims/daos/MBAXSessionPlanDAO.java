/**
 * 
 */
package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConsumerProgramStructureAcadsBean;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.Post;
import com.nmims.beans.SessionPlanBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;

/**
 * @author Pranit.S
 *
 */
public class MBAXSessionPlanDAO extends BaseDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	/* Code for masterkey config start */
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcadsBean> getConsumerTypeBeanList() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureAcadsBean> ConsumerType = null;
		String sql = "SELECT id,name FROM exam.consumer_type";

		try {
			ConsumerType = (ArrayList<ConsumerProgramStructureAcadsBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureAcadsBean.class));

		} catch (Exception e) {
			  
			return null;
		}

		return ConsumerType;

	}

	@Transactional(readOnly = true)
	public Map<String, String> getProgramStructureIdNameMap() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureAcadsBean> programStructureList = null;

		Map<String, String> programStructureIdNameMap = new HashMap<>();

		String sql = "SELECT id,program_structure as name FROM exam.program_structure";

		try {
			programStructureList = (ArrayList<ConsumerProgramStructureAcadsBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureAcadsBean.class));
			for (ConsumerProgramStructureAcadsBean c : programStructureList) {
				programStructureIdNameMap.put(c.getId(), c.getName());
			}

		} catch (Exception e) {
			  
		}
		return programStructureIdNameMap;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getProgramIdNameMap() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureAcadsBean> programList = null;

		Map<String, String> programIdNameMap = new HashMap<>();

		String sql = "SELECT id,code as name FROM exam.program";

		try {
			programList = (ArrayList<ConsumerProgramStructureAcadsBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureAcadsBean.class));
			for (ConsumerProgramStructureAcadsBean c : programList) {
				programIdNameMap.put(c.getId(), c.getName());
			}

		} catch (Exception e) {

			  
		}

		return programIdNameMap;

	}

	/* Code for masterkey config end */

	/*
	 * public String saveSessionPlan(final SessionPlanBean bean) { jdbcTemplate =
	 * new JdbcTemplate(dataSource); GeneratedKeyHolder holder = new
	 * GeneratedKeyHolder(); final String sql = "INSERT INTO acads.upgrad_sessionplan " +
	 * " (title, subject, month, year, createdBy," +
	 * "  createdDate, lastModifiedBy, lastModifiedDate, consumerTypeId, programStructureId,"
	 * +
	 * "  programId, noOfClassroomSessions, noOf_Practical_Group_Work, noOfAssessments, continuousEvaluationPercentage,"
	 * +
	 * "  tEEPercentage, courseRationale, objectives, learningOutcomes, prerequisites,"
	 * + "  pedagogy, textbook, journals, links) " + " VALUES (?,?,?,?,?," +
	 * "			sysdate(),?,sysdate(),?,?," + "			?,?,?,?,?," +
	 * "			?,?,?,?,?," + "			?,?,?,?)"; try { jdbcTemplate.update(new
	 * PreparedStatementCreator() {
	 * 
	 * @Override public PreparedStatement createPreparedStatement(Connection con)
	 * throws SQLException { PreparedStatement statement = con.prepareStatement(sql,
	 * Statement.RETURN_GENERATED_KEYS); statement.setString(1,bean.getTitle());
	 * statement.setString(2,bean.getSubject());
	 * statement.setString(3,bean.getMonth()); statement.setInt(4,bean.getYear());
	 * statement.setString(5,bean.getCreatedBy());
	 * 
	 * statement.setString(6,bean.getLastModifiedBy());
	 * statement.setString(7,bean.getConsumerTypeId());
	 * statement.setString(8,bean.getProgramStructureId());
	 * 
	 * statement.setString(9,bean.getProgramId());
	 * statement.setInt(10,bean.getNoOfClassroomSessions());
	 * statement.setInt(11,bean.getNoOf_Practical_Group_Work());
	 * statement.setInt(12,bean.getNoOfAssessments());
	 * statement.setInt(13,bean.getContinuousEvaluationPercentage());
	 * 
	 * statement.setInt(14,bean.gettEEPercentage());
	 * statement.setString(15,bean.getCourseRationale());
	 * statement.setString(16,bean.getObjectives());
	 * statement.setString(17,bean.getLearningOutcomes());
	 * statement.setString(18,bean.getPrerequisites());
	 * 
	 * statement.setString(19,bean.getPedagogy());
	 * statement.setString(20,bean.getTextbook());
	 * statement.setString(21,bean.getJournals());
	 * statement.setString(22,bean.getLinks());
	 * 
	 * return statement; } }, holder); long primaryKey =
	 * holder.getKey().longValue();
	 * return
	 * primaryKey+""; } catch (Exception e) {    return
	 * "Error in saveSessionPlan : "+e.getMessage(); }
	 * 
	 * 
	 * 
	 * }
	 * 
	 */

	@Transactional(readOnly = false)
	public String saveSessionPlan(final SessionPlanBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.upgrad_sessionplan " + " (title, subject, month, year, createdBy,"
				+ "  createdDate, lastModifiedBy, lastModifiedDate, consumerTypeId, programStructureId,"
				+ "  programId, noOfClassroomSessions, noOf_Practical_Group_Work, noOfAssessments, continuousEvaluationPercentage,"
				+ "  tEEPercentage, courseRationale, objectives, learningOutcomes, prerequisites,"
				+ "  pedagogy, textbook, journals, links) " + " VALUES (?,?,?,?,?,"
				+ "			sysdate(),?,sysdate(),?,?," + "			?,?,?,?,?," + "			?,?,?,?,?,"
				+ "			?,?,?,?)";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, bean.getTitle());
					statement.setString(2, bean.getSubject());
					statement.setString(3, bean.getMonth());
					statement.setInt(4, bean.getYear());
					statement.setString(5, bean.getCreatedBy());

					statement.setString(6, bean.getLastModifiedBy());
					statement.setString(7, bean.getConsumerTypeId());
					statement.setString(8, bean.getProgramStructureId());

					statement.setString(9, bean.getProgramId());
					statement.setInt(10, bean.getNoOfClassroomSessions());
					statement.setInt(11, bean.getNoOf_Practical_Group_Work());
					statement.setInt(12, bean.getNoOfAssessments());
					statement.setInt(13, bean.getContinuousEvaluationPercentage());

					statement.setInt(14, bean.gettEEPercentage());
					statement.setString(15, bean.getCourseRationale());
					statement.setString(16, bean.getObjectives());
					statement.setString(17, bean.getLearningOutcomes());
					statement.setString(18, bean.getPrerequisites());

					statement.setString(19, bean.getPedagogy());
					statement.setString(20, bean.getTextbook());
					statement.setString(21, bean.getJournals());
					statement.setString(22, bean.getLinks());

					return statement;
				}
			}, holder);
			long primaryKey = holder.getKey().longValue();

			return primaryKey + "";
		} catch (Exception e) {
			  
			return "Error in saveSessionPlan : " + e.getMessage();
		}

	}

	@Transactional(readOnly = true)
	public ArrayList<String> getProgramSemSubjectIdsBySubjectNProgramConfig(String programId, String programStructureId,
			String consumerTypeId, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT p_s_s.id " + " FROM exam.consumer_program_structure as c_p_s, "
				+ "		 exam.program_sem_subject as p_s_s " + "where c_p_s.programId in (" + programId + ") "
				+ "and c_p_s.programStructureId in (" + programStructureId + ") " + "and c_p_s.consumerTypeId in ("
				+ consumerTypeId + ") " + "and c_p_s.id = p_s_s.consumerProgramStructureId " + "and p_s_s.subject=?";

		ArrayList<String> programSemSubjectIds = new ArrayList<>();
		try {
			programSemSubjectIds = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] { subject },
					new SingleColumnRowMapper(String.class));
		} catch (DataAccessException e) {
			  
		}

		return programSemSubjectIds;
	}

	@Transactional(readOnly = false)
	public String batchInsertSessionPlanIdProgramSemSubjectIdMappings(final SessionPlanBean bean,
			final ArrayList<String> programSemSubjectIds) {
		try {
			String sql = " INSERT INTO acads.upgrad_sessionplanid_programsemsubjectid_mapping(sessionPlanId, programSemSubjectId,createdBy,createdDate) "
					+ " VALUES (?,?,?,sysdate())";

			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setLong(1, bean.getId());
					ps.setString(2, programSemSubjectIds.get(i));
					ps.setString(3, bean.getLastModifiedBy());

				}

				@Override
				public int getBatchSize() {
					return programSemSubjectIds.size();
				}
			});
			return "";
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			return "Error in batchInsertSessionPlanIdProgramSemSubjectIdMappings : " + e.getMessage();
		}

	}

	@Transactional(readOnly = false)
	public String batchInsertSessionPlanIdTimeBoundIdMappings(final SessionPlanBean bean,
			final List<Long> timeboundIds) {
		try {
			String sql = " INSERT INTO acads.upgrad_sessionplanid_timeboundid_mapping"
					+ "	(sessionPlanId, timeboundId,createdBy,createdDate) " + " VALUES (?,?,?,sysdate())";

			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setLong(1, bean.getId());
					ps.setLong(2, timeboundIds.get(i));
					ps.setString(3, bean.getLastModifiedBy());

				}

				@Override
				public int getBatchSize() {
					return timeboundIds.size();
				}
			});
			return "";
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
			return "Error in batchInsertSessionPlanIdTimeBoundIdMappings : " + e.getMessage();
		}

	}

	@Transactional(readOnly = true)
	public List<SessionPlanBean> getAllSessionPlans() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Edited to find the timebound ID, get batchId then finally find out the batch name
		String sql = ""
				+ "SELECT "
				+ "`sp`.*, "
				+ "`batch`.`name` AS `batchName` "
				+ "FROM "
				+ "`acads`.`upgrad_sessionplan` `sp` "
				+ "LEFT JOIN "
				+ "`acads`.`upgrad_sessionplanid_timeboundid_mapping` `sptm` "
				+ "ON "
				+ "`sp`.`id` = `sptm`.`sessionPlanId` "
				+ "LEFT JOIN "
				+ "`lti`.`student_subject_config` `ssc` "
				+ "ON "
				+ "`ssc`.`id` = `sptm`.`timeboundId` "
				+ "LEFT JOIN "
				+ "`exam`.`batch` `batch` "
				+ "ON "
				+ "`batch`.`id` = `ssc`.`batchId` "
				+ "ORDER BY `sp`.`id` DESC";
		List<SessionPlanBean> list = new ArrayList<>();
		try {
			list = (List<SessionPlanBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionPlanBean>(SessionPlanBean.class));
		} catch (Exception e) {
			 e.printStackTrace(); 
		}
		System.out.println(" getAllSessionPlans() : "+list.size());
		
		return list;
	}

	@Transactional(readOnly = true)
	public SessionPlanBean getSessionPlanById(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.upgrad_sessionplan where id = ?";
		try {
			SessionPlanBean bean = (SessionPlanBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
					new BeanPropertyRowMapper(SessionPlanBean.class));

			return bean;
		} catch (Exception e) {

			  
			return null;
		}
	}

	/*
	 * acads.upgrad_sessionplan id, title, subject, month, year, createdBy, createdDate,
	 * lastModifiedBy, lastModifiedDate, consumerTypeId, programStructureId,
	 * programId, noOfClassroomSessions, noOf_Practical_Group_Work, noOfAssessments,
	 * continuousEvaluationPercentage, tEEPercentage, courseRationale, objectives,
	 * learningOutcomes, prerequisites, pedagogy, textbook, journals, links
	 */
	
	@Transactional(readOnly = false)
	public String updateSessionPlan(SessionPlanBean formBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.upgrad_sessionplan set " + " title=?, " + " subject=?," + " month=?," + " year=?,"
				+ " lastModifiedBy=?," + " lastModifiedDate=sysdate(), " + " consumerTypeId=?,"
				+ " programStructureId=?," + " programId=?," + " noOfClassroomSessions=?,"
				+ " noOf_Practical_Group_Work=?," + " noOfAssessments=?," + " continuousEvaluationPercentage=?,"
				+ " tEEPercentage=?," + " courseRationale=?," + " objectives=?," + " learningOutcomes=?,"
				+ " prerequisites=?," + " textbook=?," + " journals=?," + " links=?, pedagogy=?"

				+ " where id=?";
		try {
			jdbcTemplate.update(sql,
					new Object[] { formBean.getTitle(), formBean.getSubject(), formBean.getMonth(), formBean.getYear(),
							formBean.getLastModifiedBy(), formBean.getConsumerTypeId(),
							formBean.getProgramStructureId(), formBean.getProgramId(),
							formBean.getNoOfClassroomSessions(), formBean.getNoOf_Practical_Group_Work(),
							formBean.getNoOfAssessments(), formBean.getContinuousEvaluationPercentage(),
							formBean.gettEEPercentage(), formBean.getCourseRationale(), formBean.getObjectives(),
							formBean.getLearningOutcomes(), formBean.getPrerequisites(), formBean.getTextbook(),
							formBean.getJournals(), formBean.getLinks(), formBean.getPedagogy(),

							formBean.getId() });

			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  

			return "Error in update, " + e.getMessage();
		}
	}

	/*
	 * id, sessionPlanId, sessionModuleNo, topic, outcomes, pedagogicalTool,
	 * chapter, createdBy, lastModifiedBy, createdDate, lastModifiedDate
	 */

	@Transactional(readOnly = false)
	public String updateSessionPlanModule(SessionPlanModuleBean formBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update acads.upgrad_sessionplan_module set " + "  sessionModuleNo=?, "
				+ "topic=?, outcomes=?, pedagogicalTool=?," + " chapter=?,  lastModifiedBy=?,"
				+ "  lastModifiedDate=sysdate() "

				+ " where id=?";
		try {
			jdbcTemplate.update(sql,
					new Object[] { formBean.getSessionModuleNo(), formBean.getTopic(), formBean.getOutcomes(),
							formBean.getPedagogicalTool(), formBean.getChapter(), formBean.getLastModifiedBy(),

							formBean.getId() });

			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  

			return "Error in updateSessionPlanModule, " + e.getMessage();
		}
	}

	@Transactional(readOnly = false)
	public int deleteSessionPlanById(Long id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.upgrad_sessionplan " + "	 where id=?  ";
		try {
			int deleted = deleteSessionPlanIdTimeboundIdMappingBySessionPlanId(id);

			if (deleted < 1) {
				return 0;
			}

			row = jdbcTemplate.update(sql, new Object[] { id });

		} catch (Exception e) {
			  
		}
		return row;

	}

	@Transactional(readOnly = false)
	public int deleteSessionPlanIdProgramSemSubjectIdMappingById(Long id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.upgrad_sessionplanid_programsemsubjectid_mapping " + "	 where sessionPlanId=?  ";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
		} catch (Exception e) {
			  
		}
		return row;

	}

	@Transactional(readOnly = false)
	public int deleteSessionPlanIdTimeboundIdMappingBySessionPlanId(Long id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.upgrad_sessionplanid_timeboundid_mapping " + "	 where sessionPlanId=?  ";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
		} catch (Exception e) {
			  
		}
		return row;

	}

	@Transactional(readOnly = false)
	public String saveSessionPlanModule(final SessionPlanModuleBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.upgrad_sessionplan_module "
				+ " ( sessionPlanId, topic, outcomes, pedagogicalTool, chapter, "
				+ "	  createdBy, lastModifiedBy, createdDate, lastModifiedDate,sessionModuleNo) "
				+ " VALUES (?,?,?,?,?,?,?,sysdate(),sysdate(),? )";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					statement.setLong(1, bean.getSessionPlanId());
					statement.setString(2, bean.getTopic());
					statement.setString(3, bean.getOutcomes());
					statement.setString(4, bean.getPedagogicalTool());
					statement.setString(5, bean.getChapter());

					statement.setString(6, bean.getCreatedBy());
					statement.setString(7, bean.getLastModifiedBy());
					statement.setLong(8, bean.getSessionModuleNo());

					return statement;
				}
			}, holder);
			long primaryKey = holder.getKey().longValue();
			return primaryKey + "";
		} catch (Exception e) {
			  
			return "Error in saveSessionPlanModule : " + e.getMessage();
		}

	}

	@Transactional(readOnly = true)
	public List<SessionPlanModuleBean> getAllSessionPlanModulesBySessionPlanId(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//old query	String sql = "SELECT * FROM acads.upgrad_sessionplan_module where sessionPlanId = ?  order by sessionModuleNo ";
		String sql = "SELECT  " + 
				"    sm.*, COALESCE(s.sessionname,'NA') AS sessionName " + 
				"FROM " + 
				"    acads.upgrad_sessionplan_module sm " + 
				"        LEFT JOIN " + 
				"    acads.sessions s ON s.moduleid = sm.id " + 
				"WHERE " + 
				"    sessionPlanId = ? " + 
				"ORDER BY sessionModuleNo";
		List<SessionPlanModuleBean> list = new ArrayList<>();
		try {			
			list = (List<SessionPlanModuleBean>) jdbcTemplate.query(sql, new Object[] { id },
					new BeanPropertyRowMapper(SessionPlanModuleBean.class));

		} catch (Exception e) {
			  
		}
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<SessionPlanModuleBean> getAllSessionPlanModulesListBySessionPlanId(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//old query	String sql = "SELECT * FROM acads.upgrad_sessionplan_module where sessionPlanId = ?  order by sessionModuleNo ";
		/*
		String sql = "SELECT sm.*, s.*, t.*, sp.*, stm.*, ssc.*, b.*, " + 
				"				    sm.sessionPlanId as moduleSessionPlanId,ct.name as corporateName,sp.month AS sessionplanMonth,sp.year AS sessionplanYear,sp.createdBy AS sessionplanCreatedBy,sp.lastModifiedBy AS sessionplanLastModifiedBy,   " + 
				"				    sp.subject AS sessionplanSubject,ssc.examYear,ssc.examMonth,b.name as batchName,   " + 
				"				    tum.userId as timebondFacultyId , COALESCE(s.id,0) as sessionId ,sm.id AS sessionplan_module_id ,t.id as testId,t.endDate as testEndDate, ssm.consumerProgramStructureId as consumerProgramStructureId " + 
				"				  FROM        " + 
				"				      acads.upgrad_sessionplan_module sm        " + 
				"				          LEFT JOIN        " + 
				"				      acads.sessions s ON s.moduleid = sm.id        " + 
				"						  LEFT JOIN " +
				"					  acads.session_subject_mapping ssm on s.id = ssm.sessionId " +
			    "						  LEFT JOIN " +
		        "				      exam.upgrad_test t on sm.id=t.referenceId" + 
				"				          INNER JOIN        " + 
				"				      acads.upgrad_sessionplan sp ON sp.id = sm.sessionPlanId        " + 
				"				          INNER JOIN        " + 
				"				      acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = sm.sessionPlanId        " + 
				"				          INNER JOIN        " +
				"				      lti.student_subject_config ssc ON ssc.id=stm.timeboundId        " +
				"				          INNER JOIN        " +
				"				      exam.batch b  ON   ssc.batchId=b.id     " +"				          INNER JOIN        " + 
				"				      (SELECT * FROM lti.timebound_user_mapping where role = 'Faculty' and id in (SELECT max(id) FROM lti.timebound_user_mapping where role = 'Faculty' group by timebound_subject_config_id) )  tum  ON tum.timebound_subject_config_id = stm.timeboundId " + 
				"						INNER JOIN " + 
				"						exam.consumer_type ct ON sp.consumerTypeId = ct.id   " + 
				"				  WHERE        " +    
				"				      sm.sessionPlanId = ? and tum.role = 'Faculty' GROUP BY sm.sessionModuleNo  ORDER BY sm.sessionModuleNo  ;";
		*/
		String sql = "SELECT sm.*,  t.*, sp.*, stm.*, ssc.*, b.*, 'NGASCE00021' as timebondFacultyId,  " + 
				"								    sm.sessionPlanId as moduleSessionPlanId," + 
				//"                                    #ct.name as corporateName," + 
				"                                    sp.month AS sessionplanMonth,sp.year AS sessionplanYear," + 
				"                                    sp.createdBy AS sessionplanCreatedBy,sp.lastModifiedBy AS sessionplanLastModifiedBy," + 
				"                                    " + 
				"								    sp.subject AS sessionplanSubject,ssc.examYear,ssc.examMonth,b.name as batchName,     " + 
				//"								    #tum.userId as timebondFacultyId ," + 
				"                                    sm.id AS sessionplan_module_id ,t.id as testId," + 
				"                                    t.endDate as testEndDate " + 
				"								  FROM          " + 
				"								      acads.upgrad_sessionplan_module sm          " + 
				"								          LEFT JOIN          " + 
				//"								      #acads.sessions s ON s.moduleid = sm.id          " + 
				//"										  #LEFT JOIN  " + 
				//"									  #acads.session_subject_mapping ssm on s.id = ssm.sessionId  " + 
				//"			    						  #LEFT JOIN  " + 
				"		        				      exam.upgrad_test t on sm.id=t.referenceId  " + 
				"								          INNER JOIN          " + 
				"								      acads.upgrad_sessionplan sp ON sp.id = sm.sessionPlanId          " + 
				"								          INNER JOIN          " + 
				"								      acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = sm.sessionPlanId          " + 
				"								          INNER JOIN         " + 
				"								      lti.student_subject_config ssc ON ssc.id=stm.timeboundId         " + 
				"								          INNER JOIN         " + 
				"								      exam.batch b  ON   ssc.batchId=b.id " + 
				//"                                      #INNER JOIN          " + 
				//"								      #(SELECT * FROM lti.timebound_user_mapping where role = 'Faculty' and id in (SELECT max(id) FROM lti.timebound_user_mapping where role = 'Faculty' group by timebound_subject_config_id) )  tum  ON tum.timebound_subject_config_id = stm.timeboundId   " + 
				//"										#INNER JOIN   " + 
				//"										#exam.consumer_type ct ON sp.consumerTypeId = ct.id     " + 
				"								  WHERE             " + 
				"								      sm.sessionPlanId = ? ORDER BY sm.id  ;";
		List<SessionPlanModuleBean> list = new ArrayList<>();
		try {			
			list = (List<SessionPlanModuleBean>) jdbcTemplate.query(sql, new Object[] {id}, new BeanPropertyRowMapper(SessionPlanModuleBean.class));

		} catch (Exception e) {
			  e.printStackTrace();
		}
		System.out.println(" getAllSessionPlanModulesListBySessionPlanId : "+list.size());
		return list;
	}

	@Transactional(readOnly = true)
	public List<SessionPlanModuleBean> getAllSessionPlanModulesBySessionPlanIdAndSapid(Long id, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " "
				+ "SELECT "
				+ " `spm`.* , "
				/* Get true or false for session attended */
				+ " ( "
					+ " CASE "
						 + " WHEN `saf`.`sessionAttended` = 'Y' THEN true "
						 + " ELSE false "
					+ " END "
				+ " ) as `sessionAttended`, "
					
				/* Get true or false for test attended */
				+ " ( "
					 + " CASE "
							+ " WHEN isnull(`tstd`.`score`) THEN false "
							+ " ELSE true "
					 + " END "
				+ " ) as `testAttended`, "
					 
				/* Check if session is over */
				+ " ( "
					+ " CASE "
						+ " WHEN (TIMESTAMP(`sessions`.`sessionDate`,`sessions`.`sessionEndTime`) <= NOW()) THEN true "
						+ " ELSE false "
					+ " END "
				+ " ) AS `sessionOver`, "
					
				/* Check if test is over */
				+ " ( "
					+ " CASE "
						+ " WHEN (TIMESTAMP(`test`.`testEndDate`) <= NOW()) THEN true "
						+ " ELSE false "
					+ " END "
				+ " ) AS `testOver`, "
					
				/* Test details needed */
				+ " `test`.`testId`, "
				+ " `test`.`testName`, "
				+ " `test`.`testStartDate`, "
				+ " `test`.`testEndDate`, "
				+ " `test`.`showResultsToStudents`, "
				+ " COALESCE(`tstd`.`score`, 0) AS `testScoreObtained`, "

				/* Session details needed */
				+ " `sessions`.`sessionId` "
				
				+ " FROM "
					 + " `acads`.`upgrad_sessionplan_module` `spm` "
				
				+ " LEFT JOIN "
					 + " ( "
							+ " SELECT "
								+ " `id` as `sessionId`, "
								+ " `date` as `sessionDate`, "
								+ " `endTime` as `sessionEndTime`, "
								+ " `moduleid` "
							+ " FROM "
								+ " `acads`.`sessions` "
							+ " WHERE "
								+ " `hasModuleId` = 'Y' "
					 + " ) `sessions` "
				+ " ON "
					 + " `sessions`.`moduleid` = `spm`.`id` "
				+ " LEFT JOIN "
					 + " ( "
							+ " SELECT "
								+ " `sessionId`, `attended` AS `sessionAttended` "
							+ " FROM "
								+ " `acads`.`session_attendance_feedback` "
							+ " WHERE "
								+ " `sapid` = ? "
					 + " ) `saf` "
				+ " ON "
					 + " `saf`.`sessionId` = `sessions`.`sessionId` "

				+ " LEFT JOIN "
				+ " ( "
					 + " SELECT "
							+ " `t`.`id` AS `testId`, "
							+ " `t`.`testName` AS `testName`, "
							+ " `t`.`startDate` AS `testStartDate`, "
							+ " `t`.`endDate` AS `testEndDate`, "
							+ " `t`.`showResultsToStudents` AS `showResultsToStudents`, "
							+ " `tcm`.`referenceId` AS `sessionPlanId` "
					 + " FROM "
					 + " `exam`.`test` `t` "

						/* Get only live tests */
					 + " INNER JOIN "
							+ " `exam`.`test_testid_configuration_mapping` `tcm` "
					 + " ON "
							+ " `t`.`id` = `tcm`.`testId` "

						/* Get only live tests */
					 + " INNER JOIN "
							+ " `exam`.`test_live_settings` `tls` "
					 + " ON "
							+ " `tls`.`referenceId` = `tcm`.`referenceId` "

						/* Get only live tests for modules */
					 + " WHERE "
							+ " `tls`.`liveType` = 'Regular' "
						 + " AND "
							+ "`tls`.`applicableType` = 'module' "
				+ " ) `test` "

				+ " ON "
				+ " `test`.`sessionPlanId` = `spm`.`id` "
	 
				+ " LEFT JOIN "
				+ " ( "
					 + " SELECT "
							+ " `score`,`testId` "
					 + " FROM "
							+ " `exam`.`test_student_testdetails` "
					 + " WHERE "
							+ " `sapid` = ? "
				+ " ) `tstd` "
				+ " ON "
				+ " `tstd`.`testId` = `test`.`testId` "
				 
				+ " WHERE "
					+ " `spm`.`sessionPlanId` = ? "

				+ " GROUP BY `spm`.`id` "
				+ " ORDER BY `spm`.`sessionModuleNo` ";
		List<SessionPlanModuleBean> list = new ArrayList<>();
		try {
			list = (List<SessionPlanModuleBean>) jdbcTemplate.query(
					sql, 
					new Object[] { 
						sapid, 
						sapid, 
						id 
					},
					new BeanPropertyRowMapper<SessionPlanModuleBean>(SessionPlanModuleBean.class));

		} catch (Exception e) {
			  
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public int deleteSessionPlanModuleById(Long id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int row = 0;
		String sql = "delete from acads.upgrad_sessionplan_module " + "	 where id=?  ";
		try {
			row = jdbcTemplate.update(sql, new Object[] { id });
		} catch (Exception e) {
			  
		}
		return row;

	}

	@Transactional(readOnly = true)
	public SessionPlanBean getSessionPlanBySapidNMasterKey(String sapid, String subject) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "    sp.* " + "FROM " + "    acads.upgrad_sessionplan sp, "
				+ "    acads.upgrad_sessionplanid_programsemsubjectid_mapping spm, " + "    exam.students s, "
				+ "    exam.program_sem_subject pss " + "where  " + "	sp.id = spm.sessionPlanId "
				+ "    and spm.programSemSubjectId = pss.id "
				+ "    and s.consumerProgramStructureId = pss.consumerProgramStructureId " + "    and pss.subject = ? "
				+ "    and s.sapid = ? " + "     " + "    group by sp.id";
		try {
			SessionPlanBean bean = (SessionPlanBean) jdbcTemplate.queryForObject(sql, new Object[] { subject, sapid },
					new BeanPropertyRowMapper(SessionPlanBean.class));


			return bean;
		} catch (Exception e) {
			  
			return null;
		}

	}

	//@Transactional(readOnly = false)
	public SessionPlanModuleBean getSessionPlanModuleById(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.upgrad_sessionplan_module where id = ?";
		try {
			SessionPlanModuleBean bean = (SessionPlanModuleBean) jdbcTemplate.queryForObject(sql, new Object[] { id },
					new BeanPropertyRowMapper(SessionPlanModuleBean.class));

			return bean;
		} catch (Exception e) {
			  
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAllVideoContentListBySesionPlanModuleId(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = new ArrayList<>();
		String sql = "select vc.*, concat('Prof. ',f.firstName,' ',f.lastName) as facultyName "
				+ "	 from acads.video_content vc inner join acads.faculty f on f.facultyId=vc.facultyId "
				+ "  where sessionPlanModuleId = ? " + "  group by vc.id "
				+ "  Order By vc.id desc";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForSessionPlanModule(Long id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT c.* FROM acads.content c where sessionPlanModuleId = ? order by id desc ";

		List<ContentAcadsBean> contents = new ArrayList<>();
		try {
			contents = jdbcTemplate.query(sql, new Object[] { id }, new BeanPropertyRowMapper(ContentAcadsBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}

		return contents;
	}

	@Transactional(readOnly = true)
	public SessionPlanBean getSessionPlanDetailsBeanByProgramSemSubjectId(Long programSemSubjectId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "    sp.* " + "FROM " + "    acads.upgrad_sessionplan sp, "
				+ "    acads.upgrad_sessionplanid_programsemsubjectid_mapping spm " + "     " + "     " + "where  "
				+ "	sp.id = spm.sessionPlanId " + "    and spm.programSemSubjectId = ? " + "     "
				+ "    group by sp.id limit 1 ";
		try {
			SessionPlanBean bean = (SessionPlanBean) jdbcTemplate.queryForObject(sql,
					new Object[] { programSemSubjectId }, new BeanPropertyRowMapper(SessionPlanBean.class));
			return bean;
		} catch (Exception e) {
			  
			return null;
		}

	}

	@Transactional(readOnly = true)
	public SessionPlanBean getSessionPlanDetailsBeanByTimeboundId(Long timeboundId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + "    sp.* " + "FROM " + "    acads.upgrad_sessionplan sp, "
				+ "    acads.upgrad_sessionplanid_timeboundid_mapping stm " + "where  " + "	sp.id = stm.sessionPlanId "
				+ "    and stm.timeboundId = ? " + " group by sp.id limit 1 ";
		try {
			SessionPlanBean bean = (SessionPlanBean) jdbcTemplate.queryForObject(sql, new Object[] { timeboundId },
					new BeanPropertyRowMapper(SessionPlanBean.class));
			return bean;
		} catch (Exception e) {
			  
			return null;
		}

	}

	@Transactional(readOnly = true)
	public List<Post> getPostByModuleId(String moduleId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM lti.post where session_plan_module_id = ? ";
		List<Post> listOfPost = new ArrayList<>();
		try {
			listOfPost = jdbcTemplate.query(sql, new Object[] { moduleId }, new BeanPropertyRowMapper(Post.class));
		} catch (Exception e) {
			  
		}

		return listOfPost;

	}

	/*commented on 26-06-2019
	 * public void deleteSessionByModuleId(Long id) {
	 * 
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource); int row=0; String
	 * sql="delete from acads.upgrad_sessionplan_module " + "	 where id=?  "; try { row =
	 * jdbcTemplate.update(sql, new Object[] {id});
	 * } catch (Exception e) {    } return row;
	 * 
	 * 
	 * }
	 * 
	 * public ArrayList<String> selectSessionIdByModuleId(Long id) {
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource); int row=0; String
	 * sql="select sessionId acads.upgrad_sessionplan_module " + "	 where id=?  "; try {
	 * row = jdbcTemplate.update(sql, new Object[] {id});
	 *  } catch (Exception e) {    } return row;
	 * 
	 * }
	 */
	
	@Transactional(readOnly = false)
	public long insertUpgradModuleDetails(final SessionPlanModuleBean moduleBean) {
		
		try {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			//for(SessionPlanModuleBean moduleBean:moduleDetails) {
				if(StringUtils.isBlank(""+moduleBean.getSessionModuleNo())  || StringUtils.isBlank(""+moduleBean.getSessionPlanId()) || StringUtils.isBlank(""+moduleBean.getTopic())  
						|| moduleBean.getSessionModuleNo()==null ||moduleBean.getSessionPlanId()==null ||moduleBean.getTopic() ==null) {
					return 0;
				}
				final	String sql = "INSERT INTO `acads`.`upgrad_sessionplan_module` " + 
						"(`sessionPlanId`, " + 
						"`topic`, " + 
						"`outcomes`, " + 
						"`pedagogicalTool`, " + 
						"`chapter`, " + 
						"`sessionModuleNo`, " + 
						"`createdBy`, " + 
						"`createdDate`, " + 
						"`lastModifiedBy`, " + 
						"`lastModifiedDate`) " + 
						"VALUES " + 
						"(?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"?, " + 
						"'Upgrad', " + 
						"sysdate(), " + 
						"'Upgrad', " + 
						"sysdate()) " + 
						" on duplicate key update "+
						"`topic` =?, " + 
						"`outcomes`=?, " + 
						"`pedagogicalTool`=?, " + 
						"`chapter`=?, " + 
						"`lastModifiedBy`='Upgrad', " + 
						"`lastModifiedDate`=sysdate() "  ;
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							 statement.setLong(1, moduleBean.getSessionPlanId());
						        statement.setString(2, moduleBean.getTopic());
						        statement.setString(3, moduleBean.getOutcomes());
						        statement.setString(4, moduleBean.getPedagogicalTool()); 
						        statement.setString(5, moduleBean.getChapter()); 
						        statement.setLong(6, moduleBean.getSessionModuleNo()); 
						        
						        statement.setString(7, moduleBean.getTopic());
						        statement.setString(8, moduleBean.getOutcomes());
						        statement.setString(9, moduleBean.getPedagogicalTool()); 
						        statement.setString(10, moduleBean.getChapter()); 
						        return statement;
				    }
				}, holder);
				///final long primaryKey = holder.getKey().longValue();
				
				
				//TestBean testToshow = getTestById(primaryKey) ;
				
				
				
				
				return 1;
		//	}
			
		}catch (Exception e) {
			  
			return 0;
		}
	}

//	public boolean checkIfBookmarked(String sapId, String contentId){
//		jdbcTemplate = new JdbcTemplate(dataSource);
//
//		String sql = "select count(*) from bookmarks.content_bookmarks cb where cb.sapid=? and cb.content_id=?";
//
//		int count = (int)jdbcTemplate.queryForObject(sql, new Object[]{sapId, contentId}, new SingleColumnRowMapper(Integer.class));
//		if(count==0){
//			return false;
//		} else{
//			return true;
//		}
//	}

	@Transactional(readOnly = true)
	public List<VideoContentAcadsBean> getAllVideoContentListBySesionPlanModuleIdAndSapId(Long id, String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<VideoContentAcadsBean> VideoContentsList = new ArrayList<>();
		String sql = "select vc.*, concat('Prof. ',f.firstName,' ',f.lastName) as facultyName, f.imgUrl, cb.bookmarked "
				+ "	 from acads.video_content vc inner join acads.faculty f on f.facultyId=vc.facultyId "
				+ "  left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=vc.id "
				+ "  where sessionPlanModuleId = ? " + "  group by vc.id "
				+ "  Order By vc.id desc";
		try {
			VideoContentsList = (List<VideoContentAcadsBean>) jdbcTemplate.query(sql, new Object[] { sapId, id },
					new BeanPropertyRowMapper(VideoContentAcadsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return VideoContentsList;
	}

	@Transactional(readOnly = true)
	public List<ContentAcadsBean> getContentsForSessionPlanModuleAndSapId(Long id, String sapId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT c.*, cb.bookmarked FROM acads.content c "
				+ " left join bookmarks.content_bookmarks cb on cb.sapid=? and cb.content_id=c.id "
				+ " where sessionPlanModuleId = ? order by id desc ";

		List<ContentAcadsBean> contents = new ArrayList<>();
		try {
			contents = jdbcTemplate.query(sql, new Object[] { sapId, id }, new BeanPropertyRowMapper(ContentAcadsBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}

		return contents;
	}
}
