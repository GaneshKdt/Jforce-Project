package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.SyllabusBean;

public class SyllabusDAO {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSubject() throws Exception{
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    subjectname "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping scm "
				+ "    INNER JOIN  "
				+ "    exam.mdm_subjectcode sc on scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    consumerProgramStructureId IN (111, 151) "
				+ "GROUP BY subjectname ";
		
		ArrayList<SyllabusBean> subjects = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return subjects;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSubjectCode() throws Exception{
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    sc.subjectcode, sc.subjectname "
				+ "FROM "
				+ "    exam.mdm_subjectcode sc "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId "
				+ "WHERE "
				+ "    consumerProgramStructureId IN (111, 151) "
				+ "GROUP BY subjectcode "
				+ "ORDER BY subjectname ";
		
		ArrayList<SyllabusBean> subjects = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return subjects;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> geSubjectCodeMappingForSubjectCode(SyllabusBean bean) throws Exception{
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    sc.subjectname, sc.subjectcode, scm.id as subjectCodeMappingId, scm.sem "
				+ "FROM "
				+ "    exam.mdm_subjectcode sc "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId "
				+ "WHERE "
				+ "    sc.subjectcode=? "
				+ "ORDER BY subjectname; ";
		
		ArrayList<SyllabusBean> subjects = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new Object[] { bean.getSubjectcode() }, 
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return subjects;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSemesterForSubject(SyllabusBean bean) throws Exception{
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    sem "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping scm "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    consumerProgramStructureId IN (111, 151) "
				+ "        AND subjectname = ? "
				+ "ORDER BY sem";
		
		ArrayList<SyllabusBean> semester = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new Object[] {bean.getSubjectname()}, 
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return semester;
		
	}

	@Transactional(readOnly = false)
	public long insertSyllabus(final SyllabusBean bean) throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		final String sql = "INSERT INTO `acads`.`syllabus` " + 
				"(`subjectCodeMappingId`,`chapter`, `title`,`topic`, `outcomes`, `pedagogicalTool`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) " + 
				"VALUES " + 
				"(?,?,?,?,?,?,?,sysdate(),?,sysdate())";
		
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					statement.setLong(1, bean.getSubjectCodeMappingId());			
					statement.setString(2, bean.getChapter());
					statement.setString(3, bean.getTitle());
					statement.setString(4, bean.getTopic());
					statement.setString(5, bean.getOutcomes());
					statement.setString(6, bean.getPedagogicalTool());
					statement.setString(7, bean.getCreatedBy());
					statement.setString(8, bean.getLastModifiedBy());
		        return statement;
		    }
		}, holder);
		
		final long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}

	@Transactional(readOnly = true)
	private int getSubjectCodeMappingId(SyllabusBean bean) throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    scm.id "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping scm "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    consumerProgramStructureId IN (111, 151) "
				+ "        AND subjectname = ? "
				+ "        AND sem = ?";
		
		int subjectCodeMappingId = jdbcTemplate.queryForObject(sql, new Object[] {bean.getSubjectname(), bean.getSem()}, Integer.class);
		
		return subjectCodeMappingId;
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSyllabusForSessionPlanId(Long id) throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    acads.sessionplanid_timeboundid_mapping sptm " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON sptm.timeboundId = ssc.id " + 
				"        INNER JOIN " + 
				"    acads.syllabus s ON ssc.prgm_sem_subj_id = s.subjectCodeMappingId " + 
				"WHERE " + 
				"    sessionPlanId = ? "+
				"ORDER BY id";
		
		ArrayList<SyllabusBean> syllabus = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new Object[] {id},
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return syllabus;
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSyllabusForSessionPlanModuleId(Long id) throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    acads.sessionplan_module spm " + 
				"        INNER JOIN " + 
				"    acads.sessionplanid_timeboundid_mapping sptm ON spm.sessionPlanId = sptm.sessionPlanId " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON sptm.timeboundId = ssc.id " + 
				"        INNER JOIN " + 
				"    acads.syllabus s ON ssc.prgm_sem_subj_id = s.subjectCodeMappingId " + 
				"WHERE " + 
				"    spm.id = ? " + 
				"ORDER BY s.id";
		
		ArrayList<SyllabusBean> syllabus = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new Object[] {id},
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		
		return syllabus;
	}

	@Transactional(readOnly = true)
	public int getSubjectCodeForMapping(SessionPlanModuleBean bean) throws Exception {
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    subjectCodeId "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping "
				+ "WHERE "
				+ "    subjectCodeId IN (SELECT  "
				+ "            id "
				+ "        FROM "
				+ "            exam.mdm_subjectcode "
				+ "        WHERE "
				+ "            subjectname = (SELECT  "
				+ "                    subject "
				+ "                FROM "
				+ "                    acads.sessionplan "
				+ "                WHERE "
				+ "                    id = ?)) "
				+ "        AND consumerProgramStructureId IN (111, 151) "
				+ "GROUP BY subjectCodeId";
		
		int subjectCodeId = jdbcTemplate.queryForObject(sql, new Object[] {bean.getSessionPlanId()}, Integer.class);
		
		return subjectCodeId;
	}
	

	public String saveSessionPlanModuleMapping( SessionPlanModuleBean bean) {

		String response = addNewMappingForSyllabusSessionPlanModule(bean);
		return response;

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSyllabusSessionPlanModuleMapping(SessionPlanModuleBean module) throws Exception{
		
		deleteExistingMapping(module);
		
		ArrayList<String> chapterList = module.getChapters();
		for(int i = 0; i<chapterList.size(); i++) {
			
			module.setSyllabusId(Long.parseLong(chapterList.get(i)));	//syllabusId, we have sessionPlanModuleId as Id set when updating 
			module.setSubjectCodeId(getSubjectCodeForMapping( module ));
			addNewMappingForSyllabusSessionPlanModule(module);
			
		}
		
	}
	
	private void deleteExistingMapping(SessionPlanModuleBean module) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "DELETE FROM acads.syllabus_sessionplanmodule_mapping " + 
				"WHERE " + 
				"    sessionPlanModuleId = ?";
		
		jdbcTemplate.update(sql, new Object[] { module.getId() });
	}
	
	private String addNewMappingForSyllabusSessionPlanModule(final SessionPlanModuleBean module) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO acads.syllabus_sessionplanmodule_mapping "+
				"( `syllabusId`,`sessionPlanModuleId` ) VALUES (?,?)";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					statement.setLong(1, module.getSyllabusId());
					statement.setLong(2, module.getId());

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
	public ArrayList<SyllabusBean> getSyllabusForPSS(SyllabusBean bean) throws Exception{
		
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    s.id, s.subjectCodeMappingId, s.chapter, s.title, s.topic, s.outcomes, s.pedagogicalTool, "
				+ "    scm.consumerProgramStructureId, scm.sem, sc.subjectname "
				+ "FROM "
				+ "    acads.syllabus s "
				+ "        INNER JOIN "
				+ "	exam.mdm_subjectcode_mapping scm ON s.subjectCodeMappingId = scm.id "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    s.subjectCodeMappingId = ?";
		
		 syllabus = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql, new Object[] { bean.getSubjectCodeMappingId() },
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		return syllabus;
		
	}

	@Transactional(readOnly = true)
	public SyllabusBean getSyllabusForId(SyllabusBean bean) throws Exception{
		
		SyllabusBean syllabus = new SyllabusBean();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    s.id, s.subjectCodeMappingId, s.chapter, s.title, s.topic, s.outcomes, s.pedagogicalTool, "
				+ "    scm.consumerProgramStructureId, scm.sem, sc.subjectname "
				+ "FROM "
				+ "    acads.syllabus s "
				+ "        INNER JOIN "
				+ "	exam.mdm_subjectcode_mapping scm ON s.subjectCodeMappingId = scm.id "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    s.id = ? ";
		
		 syllabus = jdbcTemplate.queryForObject(sql, new Object[] { bean.getId() },
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		return syllabus;
	}

	@Transactional(readOnly = true)
	public ArrayList<SyllabusBean> getSubjectForSyllabus() throws Exception{
		
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    acads.syllabus s "
				+ "		INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON s.subjectCodeMappingId = scm.id "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    s.subjectCodeMappingId = scm.id "
				+ "GROUP BY subjectCodeMappingId ";
		
		 syllabus = (ArrayList<SyllabusBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<>(SyllabusBean.class));
		return syllabus;
		
	}

	@Transactional(readOnly = false)
	public int deleteSyllabus(SyllabusBean bean) throws Exception{
		
		int syllabus;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "DELETE FROM `acads`.`syllabus` "
				+ "WHERE subjectCodeMappingId = ?";
		
		 syllabus = jdbcTemplate.update(sql, new Object[] { bean.getSubjectCodeMappingId() } );
		return syllabus;
		
	}

	@Transactional(readOnly = false)
	public int updateSyllabus(SyllabusBean bean) throws Exception{
		
		int result;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "UPDATE `acads`.`syllabus` "
				+ "SET "
				+ "`chapter` = ?, "
				+ "`title` = ?, "
				+ "`topic` = ?, "
				+ "`outcomes` = ?, "
				+ "`pedagogicalTool` = ?, "
				+ "`lastModifiedBy` = ?, "
				+ "`lastModifiedDate` = sysdate() "
				+ "WHERE `id` =?";
		
		result = jdbcTemplate.update(sql, new Object[] { bean.getChapter(), bean.getTitle(), bean.getTopic(), 
				 bean.getOutcomes(), bean.getPedagogicalTool(), bean.getLastModifiedBy(), bean.getId() } );
		return result;
		
	}	

	@Transactional(readOnly = false)
	public int deleteSyllabusDetails(SyllabusBean bean) throws Exception{
		
		int restult;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "DELETE FROM `acads`.`syllabus` "
				+ "WHERE id = ?";
		
		restult = jdbcTemplate.update(sql, new Object[] { bean.getId() } );
		return restult;
		
	}
	
}
