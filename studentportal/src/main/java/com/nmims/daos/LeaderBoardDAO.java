package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.boot.autoconfigure.session.SessionProperties.Jdbc;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.nmims.beans.MBAPassFailResponseBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.SubjectCodeBatchBean;

public class LeaderBoardDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public String group_concat_max_len;

	private static final Logger rankLogger = LoggerFactory.getLogger("rankDenormalization");

	public List<StudentRankBean> getCycleWiseConfigurationForRankDenormalization(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    r.program, "
				+ "    r.sem, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    (p.noOfSubjectsToClearSem * 100) AS subjectTotal "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.programs p ON p.program = r.program "
				+ "        AND p.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "        AND p.active = 'Y' "
				+ "WHERE "
				+ "    eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL "
				+ "GROUP BY sem, program, consumerProgramStructureId, month,year; ";

		List<StudentRankBean> rankConfigList = (ArrayList<StudentRankBean>)jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rankConfigList;
		
	}
	
	public List<StudentRankBean> getSubjectWiseConfigurationForRankDenormalization(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    r.program, "
				+ "    r.sem, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    sc.subjectname AS subject, "
				+ "    scm.id AS subjectcodeMappingId "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON r.consumerProgramStructureId = scm.consumerProgramStructureId "
				+ "        AND r.sem = scm.sem "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL "
				+ "GROUP BY sem, program, consumerProgramStructureId, month, year, subject;";

		List<StudentRankBean> rankSubjectConfigList = (ArrayList<StudentRankBean>)jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rankSubjectConfigList;
	}

	public List<StudentRankBean> getStudentRegistration( String sapid ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    sapid, sem, program, month, year, consumerProgramStructureId "
				+ "FROM "
				+ "    exam.registration "
				+ "WHERE "
				+ "    sapid = ?;";
		
		List<StudentRankBean> registrations = jdbcTemplate.query(sql, new Object[]{ sapid }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return registrations;
		
	}

	public StudentRankBean getRegistrationDetailsForSem( String sapid, String sem ) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    sapid, sem, program, month, year, consumerProgramStructureId "
				+ "FROM "
				+ "    exam.registration "
				+ "WHERE "
				+ "    sapid = ? "
				+ "	   AND sem = ?;";
		
		StudentRankBean registration = jdbcTemplate.queryForObject(sql, new Object[]{ sapid, sem }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return registration;
		
	}

	public List<StudentRankBean> getLiveExamOrder() throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    acadMonth, month, year "
				+ "FROM "
				+ "    exam.examorder "
				+ "WHERE "
				+ "    live = 'Y'";
		
		List<StudentRankBean> examorder = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(StudentRankBean.class));

		return examorder;
		
	}

	public List<StudentRankBean> getProgramDetails() throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    program, "
				+ "    consumerProgramStructureId, "
				+ "    noOfSubjectsToClearSem * 100 AS subjectsCount "
				+ "FROM "
				+ "    exam.programs "
				+ "WHERE "
				+ "    active = 'Y'";
		
		List<StudentRankBean> programDetails = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(StudentRankBean.class));

		return programDetails;
		
	}

	public List<StudentRankBean> getSubjectDetails() throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    consumerProgramStructureId, "
				+ "    sem, "
				+ "    sc.subjectname AS subject, "
				+ "    scm.id AS subjectcodeMappingId "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping scm "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id";
		
		List<StudentRankBean> programDetails = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(StudentRankBean.class));

		return programDetails;
		
	}

	public List<StudentRankBean> getCycleWiseRankConfigList(String sapId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    r.sapid, " + 
				"    r.sem, " + 
				"    r.program, " + 
				"    r.consumerProgramStructureId, " + 
				"    eo.month, " + 
				"    eo.year, " + 
				"    (p.noOfSubjectsToClearSem * 100) AS subjectsCount " + 
				" FROM " + 
				"    exam.registration r " + 
				"        INNER JOIN " + 
				"    exam.examorder eo ON r.month = eo.acadMonth " + 
				"        AND r.year = eo.year " + 
				"        INNER JOIN " + 
				"    exam.programs p ON p.program = r.program " + 
				"        AND p.consumerProgramStructureId = r.consumerProgramStructureId " + 
				"        AND p.active = 'Y' " + 
				"WHERE " + 
				"    r.sapid = ? " + 
				"        AND eo.order = (SELECT  " + 
				"            MAX(eo.order) " + 
				"        FROM " + 
				"            exam.examorder eo " + 
				"        WHERE " + 
				"            r.month = eo.acadMonth " + 
				"                AND r.year = eo.year) " + 
				"        AND eo.live = 'Y' " + 
				"        AND r.consumerProgramStructureId IS NOT NULL " + 
				"ORDER BY r.lastModifiedDate";

		List<StudentRankBean> rankConfigList = jdbcTemplate.query(sql, new Object[]{sapId}, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rankConfigList;
		
	}
	
	public List<StudentRankBean> getSubjectWiseRankConfigList(String sapId) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT "
				+ "    r.sapid, "
				+ "    r.sem, "
				+ "    r.program, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    sc.subjectname AS subject, "
				+ "    scm.id AS subjectcodeMappingId "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON r.consumerProgramStructureId = scm.consumerProgramStructureId "
				+ "        AND r.sem = scm.sem "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    r.sapid = ? "
				+ "        AND eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL "
				+ "ORDER BY r.lastModifiedDate";

		List<StudentRankBean> rankSubjectConfigList = jdbcTemplate.query(sql, new Object[]{sapId}, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rankSubjectConfigList;
		
	}

	public StudentStudentPortalBean getStudentDetailsForRank(StudentStudentPortalBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    r.sapid, "
				+ "    r.sem, "
				+ "    r.program, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    (p.noOfSubjectsToClearSem * 100) AS subjectsCount "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.programs p ON p.program = r.program "
				+ "        AND p.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "        AND p.active = 'Y' "
				+ "WHERE "
				+ "    r.sapid = ? AND r.sem = ? "
				+ "        AND eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL";
		
		bean = jdbcTemplate.queryForObject(query, new Object[] { bean.getSapid(), bean.getSem() }, 
				new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
		
		return bean;
		
	}

	public List<StudentRankBean> getTopFiveCycleWiseRank( String masterKey, String program, String year, String month, 
			String sem ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentRankBean> topFiveRankList  = new ArrayList<>();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.cycle_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "HAVING `rank` < 6 "
				+ "ORDER BY `rank`, `name`;";
		
		topFiveRankList = jdbcTemplate.query( query , new Object[]{ masterKey, program, year, month, sem }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return topFiveRankList;
		
	}

	public List<StudentRankBean> getTopFiveCycleWiseRankForLinkedIn( String masterKey, String program, String year, String month, 
			String sem ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentRankBean> topFiveRankList  = new ArrayList<>();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.cycle_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "GROUP BY `rank` "
				+ "HAVING `rank` < 6 "
				+ "ORDER BY `rank`, `name`;";
		
		topFiveRankList = jdbcTemplate.query( query , new Object[]{ masterKey, program, year, month, sem }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return topFiveRankList;
		
	}
	
	public StudentRankBean getCycleWiseRankForStudent( String masterKey, String program, String year, String month, 
			String sem, String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentRankBean studentsRank  = new StudentRankBean();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.cycle_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "        AND sapid = ? "
				+ "ORDER BY `rank`, `name`;";
		
		studentsRank = jdbcTemplate.queryForObject( query , new Object[]{ masterKey, program, year, month, sem, sapid }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return studentsRank;
		
	}
	
	public List<StudentRankBean> getTopFiveSubjectWiseRank( String masterKey, String program, String year, String month,
			String sem, String subject ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentRankBean> topFiveRankList  = new ArrayList<>();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.subject_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "        AND subject = ? "
				+ "HAVING `rank` < 6 "
				+ "ORDER BY `rank` , `name`;";
		
		topFiveRankList = jdbcTemplate.query( query , new Object[]{ masterKey, program, 
				year, month, sem, subject }, new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return topFiveRankList;
		
	}

	public List<StudentRankBean> getTopFiveSubjectWiseRankForLinkedIn( String masterKey, String program, String year, String month,
			String sem, String subject ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentRankBean> topFiveRankList  = new ArrayList<>();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.subject_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "        AND subject = ? "
				+ "GROUP BY `rank` "
				+ "HAVING `rank` < 6 "
				+ "ORDER BY `rank` , `name`;";
		
		topFiveRankList = jdbcTemplate.query( query , new Object[]{ masterKey, program, 
				year, month, sem, subject }, new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return topFiveRankList;
		
	}
	
	public StudentRankBean getSubjectWiseRankForStudent( String masterKey, String program, String year, String month, 
			String sem, String subject, String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentRankBean studentsRank  = new StudentRankBean();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.subject_wise_rank "
				+ "WHERE "
				+ "    masterKey = ? AND program = ? "
				+ "        AND year = ? "
				+ "        AND month = ? "
				+ "        AND sem = ? "
				+ "        AND subject = ? "
				+ "        AND sapid = ? "
				+ "ORDER BY `rank` , `name`;";
		
		studentsRank = jdbcTemplate.queryForObject( query , new Object[]{ masterKey, program, 
				year, month, sem, subject, sapid }, new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return studentsRank;
		
	}

	public List<StudentRankBean> getCycleWiseConfigurationToMigrateRank( String examMonth, String examYear ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    r.program, "
				+ "    r.sem, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    (p.noOfSubjectsToClearSem * 100) AS subjectTotal "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.programs p ON p.program = r.program "
				+ "        AND p.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "        AND p.active = 'Y' "
				+ "WHERE "
				+ "    eo.month = ? AND eo.year = ? "
				+ "        AND eo.live = 'Y' "
				+ "GROUP BY sem , program , consumerProgramStructureId , eo.month , eo.year";

		List<StudentRankBean> rankConfigList = (ArrayList<StudentRankBean>)jdbcTemplate.query(sql, new Object[] { examMonth, examYear},
				new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return rankConfigList;
		
	}

	public ArrayList<StudentRankBean> getCycleWiseRankDetails( StudentRankBean bean ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT "
				+ "    sapid, name, studentImage, consumerProgramStructureId, program, year, "
				+ "    month, sem, total, outOfMarks, CONCAT(`rank`, '/', @outOf) AS `rank` "
				+ "FROM "
				+ "    (SELECT  "
				+ "        *, "
				+ "            @curRank:=IF(@prev = total, @curRank, @curRank + 1) AS `rank`, "
				+ "            @prev AS previous, "
				+ "            @prev:=total, "
				+ "            @outOf:=@curRank "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        pf.sapid, "
				+ "            CONCAT(firstName, ' ', lastName) AS name, "
				+ "            CASE "
				+ "                WHEN imageUrl IS NULL THEN 'dummyURL' "
				+ "                ELSE imageUrl "
				+ "            END AS studentImage, "
				+ "            pss.consumerProgramStructureId, "
				+ "            r.program, "
				+ "            pf.writtenYear AS year, "
				+ "            pf.writtenMonth AS month, "
				+ "            pf.sem, "
				+ "            SUM(CAST(total AS SIGNED)) AS total, "
				+ "            COUNT(pss.subject) * 100 AS outOfMarks "
				+ "    FROM "
				+ "        (SELECT @curRank:=0) r, (SELECT @prev:=NULL) AS init, exam.passfail pf "
				+ "    INNER JOIN exam.students s ON pf.sapid = s.sapid "
				+ "    INNER JOIN exam.registration r ON pf.sapid = r.sapid AND pf.sem = r.sem "
				+ "    INNER JOIN exam.program_sem_subject pss ON pss.subject = pf.subject "
				+ "        AND pss.sem = pf.sem  AND pss.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "    WHERE "
				+ "        pf.sem = ? AND pf.writtenMonth = ? "
				+ "            AND pf.writtenYear = ? "
				+ "            AND r.program = ? "
				+ "            AND r.consumerProgramStructureId = ? "
				+ "            AND isPass = 'Y' "
				+ "    GROUP BY sapid "
				+ "    ORDER BY total DESC) AS `details`) AS `cycle_wise_rank`"
				+ "HAVING outOfMarks = ?;";

		ArrayList<StudentRankBean> rank = ( ArrayList<StudentRankBean> )jdbcTemplate.query(sql, new Object[]{ bean.getSem(), 
				bean.getMonth(), bean.getYear(), bean.getProgram(), bean.getConsumerProgramStructureId(), 
				bean.getSubjectTotal()}, new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rank;
		
	}
	
	public void insertCycleWiseRankDetails( final ArrayList<StudentRankBean> cycleWiseRankList ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String query = "INSERT INTO `exam`.`cycle_wise_rank` "
				+ "(`sapid`,`name`,`studentImage`,`masterKey`,`program`,`year`,`month`,`sem`,`total`,`outOfMarks`, `rank`) "
				+ "VALUES "
				+ "( ?,?,?,?,?,?,?,?,?,?,? );";


		int[] batchInsertCycleWiseRank = jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString( 1, cycleWiseRankList.get(i).getSapid() );
				ps.setString( 2, cycleWiseRankList.get(i).getName() );
				ps.setString( 3, cycleWiseRankList.get(i).getStudentImage() );
				ps.setString( 4, cycleWiseRankList.get(i).getConsumerProgramStructureId() );
				ps.setString( 5, cycleWiseRankList.get(i).getProgram() );
				ps.setString( 6, cycleWiseRankList.get(i).getYear() );
				ps.setString( 7, cycleWiseRankList.get(i).getMonth() );
				ps.setString( 8, cycleWiseRankList.get(i).getSem() );
				ps.setString( 9, cycleWiseRankList.get(i).getTotal() );
				ps.setString( 10, cycleWiseRankList.get(i).getOutOfMarks() );
				ps.setString( 11, cycleWiseRankList.get(i).getRank() );


			}

			@Override
			public int getBatchSize() {
				return cycleWiseRankList.size();
			}
		});

		return;
	}
	
	public List<StudentRankBean> getSubjectWiseConfigurationToMigrateRank( String examMonth, String examYear ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    r.program, "
				+ "    r.sem, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    sc.subjectname AS subject, "
				+ "    scm.id AS subjectcodeMappingId "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode_mapping scm ON r.consumerProgramStructureId = scm.consumerProgramStructureId "
				+ "        AND r.sem = scm.sem "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "     eo.month = ? AND eo.year = ? "
				+ "        AND eo.live = 'Y' "
				+ "GROUP BY sem , program , consumerProgramStructureId , month , year , subject;";

		List<StudentRankBean> rankSubjectConfigList = (ArrayList<StudentRankBean>)jdbcTemplate.query(sql, new Object[] { examMonth, examYear},
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rankSubjectConfigList;
	}

	public ArrayList<StudentRankBean> getSubjectWiseRankDetails( StudentRankBean bean ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT "
				+ "    sapid, `name`, studentImage, consumerProgramStructureId, program, year, "
				+ "    month, sem, total, `subject`, CONCAT(`rank`, '/', @outOf) AS `rank`, outOfMarks "
				+ "FROM "
				+ "    (SELECT  "
				+ "        *, "
				+ "            @curRank:=IF(@prev = total, @curRank, @curRank + 1) AS `rank`, "
				+ "            @prev AS previous, "
				+ "            @prev:=total, "
				+ "            @outOf:=@curRank "
				+ "    FROM "
				+ "        (SELECT  "
				+ "        pf.sapid, "
				+ "            CONCAT(firstName, ' ', lastName) AS name, "
				+ "            CASE "
				+ "                WHEN imageUrl IS NULL THEN 'dummyURL' "
				+ "                ELSE imageUrl "
				+ "            END AS studentImage, "
				+ "            r.consumerProgramStructureId, "
				+ "            r.program, "
				+ "            pf.writtenYear AS year, "
				+ "            pf.writtenMonth AS month, "
				+ "            pf.sem, "
				+ "            pf.subject, "
				+ "            CAST(total AS SIGNED) AS total, "
				+ "            outOfMarks * 100 AS outOfMarks "
				+ "    FROM "
				+ "        (SELECT @curRank:=0) r, (SELECT @prev:=NULL) AS init, exam.passfail pf "
				+ "    INNER JOIN exam.students s ON pf.sapid = s.sapid "
				+ "    INNER JOIN exam.registration r ON pf.sapid = r.sapid AND pf.sem = r.sem, (SELECT  "
				+ "        COUNT(DISTINCT subject) AS outOfMarks "
				+ "    FROM "
				+ "        exam.passfail pf "
				+ "    INNER JOIN exam.registration r ON pf.sapid = r.sapid AND pf.sem = r.sem "
				+ "    WHERE "
				+ "        pf.sem = ? AND pf.writtenMonth = ? "
				+ "            AND pf.writtenYear = ? "
				+ "            AND r.program = ? "
				+ "            AND pf.subject = ?) AS outOfMarks "
				+ "    WHERE "
				+ "        pf.sem = ? AND pf.writtenMonth = ? "
				+ "            AND pf.writtenYear = ? "
				+ "            AND r.program = ? "
				+ "            AND pf.subject = ? "
				+ "            AND r.consumerProgramStructureId = ? "
				+ "            AND isPass = 'Y' "
				+ "    ORDER BY total DESC) AS `details`) AS `subject_wise_rank`;";

		ArrayList<StudentRankBean> rank = ( ArrayList<StudentRankBean> )jdbcTemplate.query(sql, new Object[]{  bean.getSem(), 
				bean.getMonth(), bean.getYear(), bean.getProgram(),  bean.getSubject(), bean.getSem(), 
				bean.getMonth(), bean.getYear(), bean.getProgram(),  bean.getSubject(), bean.getConsumerProgramStructureId() }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return rank;
		
	}
	
	public void insertSubjectWiseRankDetails( final ArrayList<StudentRankBean> subjectWiseRankList ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String query = "INSERT INTO `exam`.`subject_wise_rank` "
				+ "(`sapid`,`name`,`studentImage`,`masterKey`,`program`,`year`,`month`,`sem`,`subject`,`total`,`outOfMarks`, `rank`) "
				+ "VALUES "
				+ "( ?,?,?,?,?,?,?,?,?,?,?,? );";

		int[] batchInsertSubjectWiseRank = jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString( 1, subjectWiseRankList.get(i).getSapid() );
				ps.setString( 2, subjectWiseRankList.get(i).getName() );
				ps.setString( 3, subjectWiseRankList.get(i).getStudentImage() );
				ps.setString( 4, subjectWiseRankList.get(i).getConsumerProgramStructureId() );
				ps.setString( 5, subjectWiseRankList.get(i).getProgram() );
				ps.setString( 6, subjectWiseRankList.get(i).getYear() );
				ps.setString( 7, subjectWiseRankList.get(i).getMonth() );
				ps.setString( 8, subjectWiseRankList.get(i).getSem() );
				ps.setString( 9, subjectWiseRankList.get(i).getSubject() );
				ps.setString( 10, subjectWiseRankList.get(i).getTotal() );
				ps.setString( 11, subjectWiseRankList.get(i).getOutOfMarks() );
				ps.setString( 12, subjectWiseRankList.get(i).getRank() );
				
			}

			@Override
			public int getBatchSize() {
				return subjectWiseRankList.size();
			}
		});

		return;

	}

	public StudentRankBean getStudentDetailsForSharingRank( String sapid ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  "
				+ "    CONCAT(firstName, ' ', lastName) AS `name`, "
				+ "    imageUrl AS studentImage "
				+ "FROM "
				+ "    exam.students "
				+ "WHERE "
				+ "    sapid = ?";

		StudentRankBean studenDetails = jdbcTemplate.queryForObject(sql, new Object[]{ sapid }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));

		return studenDetails;
		
	}

	public ArrayList<StudentStudentPortalBean> getHomepageRankConfig(StudentStudentPortalBean bean) throws Exception{
		
		ArrayList<StudentStudentPortalBean> homepageRankDetails = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    r.sapid, "
				+ "    r.sem, "
				+ "    r.program, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    (p.noOfSubjectsToClearSem * 100) AS subjectsCount "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.programs p ON p.program = r.program "
				+ "        AND p.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "        AND p.active = 'Y' "
				+ "WHERE "
				+ "    r.sapid = ? "
				+ "        AND eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL ";
		
		homepageRankDetails = (ArrayList<StudentStudentPortalBean>) jdbcTemplate.query(query, new Object[] { bean.getSapid() }, 
				new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
		
		return homepageRankDetails;
		
	}

	public List<StudentRankBean> getStudentRankForHomepage( String sapid ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentRankBean> studentsRank  = new ArrayList<>();
		
		String query = "SELECT  "
				+ "    * "
				+ "FROM "
				+ "    exam.cycle_wise_rank "
				+ "WHERE "
				+ "    sapid = ? "
				+ "ORDER BY `sem`";
		
		studentsRank = jdbcTemplate.query( query , new Object[]{ sapid }, 
				new BeanPropertyRowMapper<>(StudentRankBean.class));
		
		return studentsRank;
		
	}
	
	public List<String> getBBAElectiveSubjectListByPssidAndSapid(List<String> pssIdList, String sapid)throws Exception
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = ""
				+ "SELECT  " 
				+ "    programSemSubjectId AS subjectcodeMappingId " 
				+ "FROM " 
				+ "    exam.student_current_subject " 
				+ "WHERE " 
				+ "    sapid = :sapid "
				+ "AND programSemSubjectId IN (:pssIdList)";
		
		parameters.addValue("sapid", sapid);
		parameters.addValue("pssIdList", pssIdList);
		
		List<String> bbaElectivePssIdList = namedParameterJdbcTemplate.query(query, parameters, new SingleColumnRowMapper<String>(String.class));
		
		return bbaElectivePssIdList;
	}

	public Map<String,Object> getSapidFromRegistrationMap(String month,String year,List<String> masterkey)throws Exception
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		Map<String,Object> studentMap = new HashMap<>();
		String query = "select sapid,sem,consumerProgramStructureId as masterkey from exam.registration where month=:month and year=:year and consumerProgramStructureId in (:consumerProgramStructureIds) group by sem,sapid order by sem";
		
		parameters.addValue("month", month);
		parameters.addValue("year", year);
		parameters.addValue("consumerProgramStructureIds", masterkey);
		List<MBAPassFailResponseBean> semMasterkeyToSapidList = namedParameterJdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		Map<String, List<String>> semMasterkeyToSapidsMap = semMasterkeyToSapidList.stream()
			    .collect(Collectors.groupingBy(obj -> obj.getMasterkey() + "/" + obj.getSem(),
			        Collectors.mapping(obj -> obj.getSapid(), Collectors.toList())));
		studentMap.put("semMasterkeyToSapidsMap", semMasterkeyToSapidsMap);
		studentMap.put("sapIdList", semMasterkeyToSapidList.stream().map(bean->bean.getSapid()).collect(Collectors.toList()));
		return studentMap;
	}
	
	public void insertSubjectWiseRankDetailsForTimebound(List<MBAPassFailResponseBean> list, String subject,
			String month, String year, Map<String, String> sapIdSemMapFromSFDC,
			Map<String, StudentStudentPortalBean> studentMap, Map<String, String> sapIdSemMapFromDB,
			Map<String, String> pssIdSemMap, Map<String, String> timeboundIdPssIdMap) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String query = "INSERT INTO `exam`.`subject_wise_rank_timebound` "
				+ "(`sapid`,`timeboundId`,`total`,`outOfMarks`,`rank`,`sem`,`subject`,`name`,`imageUrl`,`month`,`year`,`masterkey`,`createdBy`,`createdDate`) "
				+ "VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate()) ON DUPLICATE KEY UPDATE lastModifiedDate=sysdate();";
		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, list.get(i).getSapid());
				ps.setString(2, list.get(i).getTimeboundId());
				ps.setInt(3, list.get(i).getTotal());
				ps.setString(4, "100");
				ps.setString(5, list.get(i).getRank());
				if (sapIdSemMapFromDB.keySet().contains(list.get(i).getSapid())) {
					ps.setString(6, sapIdSemMapFromDB.get(list.get(i).getSapid()));
				} else if (sapIdSemMapFromSFDC.keySet().contains(list.get(i).getSapid())) {
					ps.setString(6, String.valueOf(sapIdSemMapFromSFDC.get(list.get(i).getSapid())));
				} else {
					rankLogger.info(" Sem is Null for input month : " + month + " , year : " + year
							+ " and iterating sapid : " + list.get(i).getSapid() + " { sapid : "
							+ list.get(i).getSapid() + " , timeboundId : " + list.get(i).getTimeboundId()
							+ " , subject : " + subject + " , sem : "
							+ pssIdSemMap.get(timeboundIdPssIdMap.get(list.get(i).getTimeboundId())) + " } ");
					ps.setString(6, pssIdSemMap.get(timeboundIdPssIdMap.get(list.get(i).getTimeboundId())));
				}
				ps.setString(7, subject);

				String firstName = studentMap.get(list.get(i).getSapid()).getFirstName();
				String lastName = studentMap.get(list.get(i).getSapid()).getLastName();

				String fullName = (StringUtils.isBlank(firstName) ? "Null" : firstName) + " "
						+ (StringUtils.isBlank(lastName) ? "Null" : lastName);

				ps.setString(8, fullName);

				ps.setString(9, studentMap.get(list.get(i).getSapid()).getImageUrl());
				ps.setString(10, month);
				ps.setString(11, year);
				ps.setString(12, studentMap.get(list.get(i).getSapid()).getConsumerProgramStructureId());
				ps.setString(13, "Manual");
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		return;
	}

	public Map<String, Object> getStudentSubjectWiseRankForTimebound(String sapid) {
		String sql = "select swrt.sapid,swrt.timeboundId,swrt.total,swrt.outOfMarks,swrt.rank,swrt.sem,swrt.subject,swrt.month,swrt.year from exam.subject_wise_rank_timebound swrt where sapid=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBAPassFailResponseBean> studentdetails = (ArrayList<MBAPassFailResponseBean>) jdbcTemplate.query(sql,
				new Object[] { sapid },
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		Map<String, Object> response = new HashMap<>();
		response.put("rankMap", studentdetails.stream()
				.collect(Collectors.toMap(MBAPassFailResponseBean::getTimeboundId, Function.identity())));
		response.put("rankList", studentdetails);
		return response;
	}


	public Map<String, MBAPassFailResponseBean> getRankIdsForTimeboundForAllStudents(List<String> timeboundIds) {
		String sql = "select swrt.timeboundId,group_concat(swrt.id) as rankIds,swrt.subject,swrt.sem,swrt.month,swrt.year,swrt.masterkey from exam.subject_wise_rank_timebound swrt where swrt.timeboundId in (:timeboundIds) and SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5 and swrt.rank>0 group by swrt.timeboundId";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("timeboundIds", timeboundIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(sql, parameters,

				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return returnList.stream()
				.collect(Collectors.toMap(MBAPassFailResponseBean::getTimeboundId, Function.identity()));
	}

	public Map<String, MBAPassFailResponseBean> getAllSubjectRankForTimeboundStudents(List<String> rankIds) {
		String sql = "select * from exam.subject_wise_rank_timebound swrt where swrt.id in (:ids) and SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5 and swrt.rank>0;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", rankIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(sql, parameters,

				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return returnList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getId, Function.identity()));
	}
	
	public Map<String,Object> getStudentDetailsUsingSapIds(List<String> sapIds){
		String sql="select sapid,firstName,lastName,imageUrl,consumerProgramStructureId from exam.students where sapid in (:sapids);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapids", sapIds);
		List<StudentStudentPortalBean> returnList = namedParameterJdbcTemplate.query(
				sql,
			    parameters,
			    
			  new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class)
					  );
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put("studentMap", returnList.stream().collect(Collectors.toMap(StudentStudentPortalBean::getSapid, Function.identity())));
		responseMap.put("studentList", returnList);
		return responseMap;
	}
	
	public Map<String, MBAPassFailResponseBean> getSemPssIdsCountMap(Set<String> sem,String masterkey){
		String sql="select sem,count(id) as pssIdCount,group_concat(id) as pssIds,consumerProgramStructureId as masterkey from exam.mdm_subjectcode_mapping where sem in (:sem) and consumerProgramStructureId in (:masterKeys) and passScore>0 group by sem,consumerProgramStructureId order by sem;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("masterKeys",masterkey);
		parameters.addValue("sem", sem.stream().map(key->key.substring(key.indexOf("/")+1, key.length())).collect(Collectors.toList()));
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
				sql,
				parameters,
				
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
				);
		return returnList.stream().collect(Collectors.toMap(bean -> bean.getMasterkey() + "/" + bean.getSem(), Function.identity()));
	}
	public List<String> getSemTimeboundIdList(List<String> pssIds,String month,String year){
		String sql="select id from lti.student_subject_config where prgm_sem_subj_id in (:pssIds) and acadMonth in (:acadMonth) and acadYear in (:acadYear);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("pssIds", pssIds);
		parameters.addValue("acadMonth", month);
		parameters.addValue("acadYear", year);
		List<String> returnList = namedParameterJdbcTemplate.queryForList(sql, parameters,String.class);
		return returnList;
	}
	public List<String> getSemTimeboundIdList(String month,String year){
		String sql="select id from lti.student_subject_config where acadMonth in (:acadMonth) and acadYear in (:acadYear);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("acadMonth", month);
		parameters.addValue("acadYear", year);
		List<String> returnList = namedParameterJdbcTemplate.queryForList(sql, parameters,String.class);
		return returnList;
	}
	public List<MBAPassFailResponseBean> getMarksDetailsTimeboundIdList(List<String> timeboundIds, List<String> sapIds) {
	    String sql = "SELECT sapid, iaScore, teeScore, graceMarks, prgm_sem_subj_id AS pssId " +
	                 "FROM exam.mba_passfail " +
	                 "WHERE timeboundId IN (:timeboundIds) " +
	                 "AND sapid IN (:sapIds) " +
	                 "AND isResultLive = 'Y' " +
	                 "AND isPass = 'Y' " +
	                 "AND iaScore is not null " +
	                 "GROUP BY sapid, timeboundId";

	    MapSqlParameterSource parameters = new MapSqlParameterSource();
	    parameters.addValue("timeboundIds", timeboundIds);
	    parameters.addValue("sapIds", sapIds);

	    List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
	        sql,
	        parameters,
	        new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class)
	    );

	    return returnList;
	}
	public List<String> getPassStudentSapIds(List<String> timeboundIds, List<String> sapIds,int subjectCount) {
		String sql = "select sapid from exam.mba_passfail where timeboundId in (:timeboundIds) and iaScore is not null and isPass='Y' and sapid in (:sapIds) group by sapid having count(sapid)=:subjectCount;";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("timeboundIds", timeboundIds);
		parameters.addValue("sapIds", sapIds);
		parameters.addValue("subjectCount", subjectCount);
		
		List<String> returnList = namedParameterJdbcTemplate.queryForList(
				sql,
				parameters,
				String.class
				);
		
		return returnList;
	}
	public List<String> getFailedStudentSapIds(List<String> timeboundIds, List<String> sapIds,List<String> passStudentSapIds,int subjectCount) {
		String sql = "select sapid from exam.mba_passfail where timeboundId in (:timeboundIds) and iaScore is not null and sapid in (:sapIds) ";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		if(passStudentSapIds.size()>0) {
			sql+=" and sapid not in (:passStudentSapIds) ";
			parameters.addValue("passStudentSapIds", passStudentSapIds);
		}
		sql+=" group by sapid having count(sapid)<=:subjectCount;";
		parameters.addValue("timeboundIds", timeboundIds);
		parameters.addValue("sapIds", sapIds);
		parameters.addValue("subjectCount", subjectCount);
		List<String> returnList = namedParameterJdbcTemplate.queryForList(
				sql,
				parameters,
				String.class
				);
		
		return returnList;
	}
	public Map<String,MBAPassFailResponseBean> getSapIdPssIdCountMap(List<String> timeboundIds,List<String> sapIds){
		String sql="select sapid,count(prgm_sem_subj_id) as pssIdCount from exam.mba_passfail where timeboundId in (:timeboundIds) and sapid in (:sapIds) and isResultLive='Y' group by sapid;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("timeboundIds", timeboundIds);
		parameters.addValue("sapIds", sapIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(sql, parameters,new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		return returnList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getSapid, Function.identity()));
	}
	public void insertCycleWiseRankDetailsForTimebound(List<MBAPassFailResponseBean> marksList,List<Integer> ranks,int noOfStudents) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		final String query = "INSERT INTO `exam`.`cycle_wise_rank_timebound` "
				+ "(`sapid`,`total`,`outOfMarks`,`rank`,`sem`,`name`,`imageUrl`,`month`,`year`,`masterkey`,`createdDate`,`createdBy`) "
				+ "VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,sysdate(),?) ON DUPLICATE KEY UPDATE lastModifiedDate=sysdate();";


		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString( 1,marksList.get(i).getSapid());
				ps.setInt( 2,marksList.get(i).getTotal());
				ps.setString( 3,marksList.get(i).getOutOfMarks());
				if(marksList.get(i).getRank()==null) {
					ps.setString( 4,ranks.indexOf(marksList.get(i).getTotal())+1+"/"+noOfStudents);
				}else {
					ps.setString( 4,marksList.get(i).getRank());
				}
				ps.setString( 5,marksList.get(i).getSem());
				ps.setString( 6,  marksList.get(i).getName());
				ps.setString( 7, marksList.get(i).getImageUrl());
				ps.setString( 8, marksList.get(i).getMonth());
				ps.setString( 9,  marksList.get(i).getYear());
				ps.setString( 10,  marksList.get(i).getMasterkey());
				ps.setString( 11, "Manual");
			}

			@Override
			public int getBatchSize() {
				return marksList.size();
			}
		});

		return;
}
	public int checkRankForTimeboundIds(Set<String> timeboundIds) {
		final String sql = "select count(*) from exam.subject_wise_rank_timebound where timeboundId in (:timeboundIds);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("timeboundIds", timeboundIds);
		return namedParameterJdbcTemplate.queryForObject(sql,mapSource,Integer.class);
	}
	
	public int checkRankForMonthYear(String month,String year) {
		final String sql = "select count(*) from exam.cycle_wise_rank_timebound where month=:month and year=:year";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("month", month);
		mapSource.addValue("year", year);
		
		try {
			return namedParameterJdbcTemplate.queryForObject(sql,mapSource,Integer.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}
	}
	public List<MBAPassFailResponseBean> getPassFailDetailsForMBAWX(Set<String> sapIds,List<String> timeboundIds) {
		final String sql = "SELECT sapid, iaScore, teeScore, graceMarks, prgm_sem_subj_id AS pssId,timeboundId FROM exam.mba_passfail where sapId in (:sapIds) and timeboundId in (:timeboundIds) and isPass='Y' and isResultLive='Y';";
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sapIds", sapIds);
		mapSource.addValue("timeboundIds", timeboundIds);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		List<MBAPassFailResponseBean> list = namedParameterJdbcTemplate.query(sql,mapSource, new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		return list;
	}
	
	public List<MBAPassFailResponseBean> getStudentCycleWiseRankForTimebound(String sapid){
		String sql="select swrt.sapid,swrt.total,swrt.outOfMarks,swrt.rank,swrt.sem,swrt.masterkey,swrt.month,swrt.year from exam.cycle_wise_rank_timebound swrt where sapid=?;";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBAPassFailResponseBean> marksDetails = (ArrayList<MBAPassFailResponseBean>)jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return marksDetails;
	}
	
	public Map<String,MBAPassFailResponseBean> getRankIdsForTimeboundForAllStudents(Set<String> timeboundIds){
		String sql="select swrt.timeboundId,group_concat(swrt.id) as rankIds from exam.subject_wise_rank_timebound swrt where swrt.timeboundId in (:timeboundIds) and SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5 group by swrt.timeboundId;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("timeboundIds", timeboundIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
				sql,
			    parameters,
			    
			  new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
					  );
		return returnList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getTimeboundId, Function.identity()));
	}
	
	public Map<String,MBAPassFailResponseBean> getAllCycleWiseRankForTimeboundStudents(List<String> rankIds){
		String sql="select * from exam.cycle_wise_rank_timebound swrt where id in (:rankIds) and SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5 and swrt.rank>0;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("rankIds", rankIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
				sql,
			    parameters,
			    
			  new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
					  );
		return returnList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getId, Function.identity()));
	}
	public Map<String,MBAPassFailResponseBean> getAllSWiseRankForTimeboundStudents(List<String> rankIds){
		String sql="select * from exam.cycle_wise_rank_timebound swrt where id in (:rankIds) and SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("rankIds", rankIds);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
				sql,
				parameters,
				
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
				);
		return returnList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getId, Function.identity()));
	}
	public List<MBAPassFailResponseBean> getStudentDetailsFromRegistration(String sapid){
		String sql="select sapid,month,year,sem,consumerProgramStructureId as masterkey from exam.registration where sapid=:sapid group by sem;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapid", sapid);
		List<MBAPassFailResponseBean> returnList = namedParameterJdbcTemplate.query(
				sql,
				parameters,
				
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
				);
		return returnList;
	}

	public Map<String,SubjectCodeBatchBean> getTimeboundDetails()throws Exception{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "select id as timeboundId,acadMonth,acadYear from lti.student_subject_config";
		
		List<SubjectCodeBatchBean> resultList = namedParameterJdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<SubjectCodeBatchBean>(SubjectCodeBatchBean.class));
		return resultList.stream().collect(Collectors.toMap(SubjectCodeBatchBean::getTimeboundId,Function.identity()));
	}
	
	public Map<String,String> getPSSIdsFromSubjectCodeMappingBacklog(List<String> masterkeys)throws Exception
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		  // Create a new HashMap to store the results
        Map<String,String> resultMap = new HashMap<>();
		
		String query = "SELECT msm.consumerProgramStructureId,GROUP_CONCAT(msm.id) AS pssids,msm.sem FROM exam.mdm_subjectcode_mapping msm WHERE msm.consumerProgramStructureId IN (:masterkeys) GROUP BY msm.consumerProgramStructureId,sem;";

		parameters.addValue("masterkeys", masterkeys);
		
		List<Map<String, Object>> resultList = namedParameterJdbcTemplate.queryForList(query, parameters);
		
		
		for (Map<String, Object> row : resultList) {
			Integer consumerProgramStructureId = (Integer) row.get("consumerProgramStructureId");
		    String pssIds  = (String) row.get("pssids");
		    resultMap.put(String.valueOf(consumerProgramStructureId)+"/"+row.get("sem"), pssIds);
        }

        return resultMap;
	}
	
	public List<String> getSapIds(List<String> timeboundIdsList,int count,List<String> sapIds) throws Exception {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		parameters.addValue("timeboundIds", timeboundIdsList);
		parameters.addValue("sapIds", sapIds);
		parameters.addValue("count", count);
		
		String query = "select userId from lti.timebound_user_mapping where role='Student' and timebound_subject_config_id in (:timeboundIds) and userId in (:sapIds) group by userId having count(timebound_subject_config_id)=:count;";
		
		List<String> timeboundIds = namedParameterJdbcTemplate.queryForList(query, parameters,String.class);
		
		return timeboundIds;
	}
	
	public List<String> getFailedStudentsSapIds(List<String> timeboundIdsList,int count) throws Exception {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		parameters.addValue("timeboundIds", timeboundIdsList);
		parameters.addValue("count", count);
		
		String query = "select userId from lti.timebound_user_mapping where role='Student' and timebound_subject_config_id in (:timeboundIds) group by userId having count(timebound_subject_config_id)!=:count;";
		
		List<String> timeboundIds = namedParameterJdbcTemplate.queryForList(query, parameters,String.class);
		
		return timeboundIds;
	}
	
	public List<SubjectCodeBatchBean> getTimeboundDetails(List<String> ids) throws Exception {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		parameters.addValue("ids", ids);
		
		String query = "select id as timeboundId,acadMonth,acadYear from lti.student_subject_config where id in (:ids);";
		
		List<SubjectCodeBatchBean> timeboundDetails = namedParameterJdbcTemplate.query(query, parameters,new BeanPropertyRowMapper<SubjectCodeBatchBean>(SubjectCodeBatchBean.class));
		
		return timeboundDetails;
	}
	
	public int checkAlreadyLiveSubjectWiseRank(String month,String year) {
		String sql = "select count(*) from exam.subject_wise_rank_timebound where month=:month and year=:year ";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("month",month);
		mapSource.addValue("year",year);
		int count = namedParameterJdbcTemplate.queryForObject(sql,mapSource,Integer.class);
		return count;
	}
	
	public Map<String,Object> getSapidFromRegistration(String month,String year,List<String> masterkeys) throws Exception {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		String query = "select sapid,month,year,sem,consumerProgramStructureId from exam.registration where month=:month and year=:year and consumerProgramStructureId in (:consumerProgramStructureIds) group by sem,consumerProgramStructureid,sapid order by sem";
		
		parameters.addValue("month", month);
		parameters.addValue("year", year);
		parameters.addValue("consumerProgramStructureIds", masterkeys);
		
		List<StudentStudentPortalBean> registraionList = namedParameterJdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
		
		Map<String,Object> returnMap = new HashMap<>();
		Map<String,List<String>> masterkeySemSapIdsmap = new HashMap<>();
		for(StudentStudentPortalBean bean:registraionList) {
			registraionList.stream().filter(key->bean.getConsumerProgramStructureId().equalsIgnoreCase(key.getConsumerProgramStructureId())&&bean.getSem().equalsIgnoreCase(key.getSem())).collect(Collectors.toList());
			if(!masterkeySemSapIdsmap.keySet().contains(bean.getConsumerProgramStructureId()+"/"+bean.getSem())) {
				masterkeySemSapIdsmap.put(bean.getConsumerProgramStructureId()+"/"+bean.getSem(),registraionList.stream().filter(key->bean.getConsumerProgramStructureId().equalsIgnoreCase(key.getConsumerProgramStructureId())&&bean.getSem().equalsIgnoreCase(key.getSem())).collect(Collectors.toList()).stream().map(value->value.getSapid()).collect(Collectors.toList()));
			}
		}
		returnMap.put("masterkeySemSapIdMap",masterkeySemSapIdsmap);
		returnMap.put("sapIdsList",registraionList.stream().map(bean->bean.getSapid()).distinct().collect(Collectors.toList()));
		return returnMap;
	}
	
	public Map<String,SubjectCodeBatchBean> getTimeboundDetails(String month,String year)throws Exception {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "select id as timeboundId,acadMonth,acadYear,prgm_sem_subj_id as programSemSubjectId from lti.student_subject_config where acadMonth=:acadMonth and acadYear=:acadYear";
		parameters.addValue("acadMonth", month);
		parameters.addValue("acadYear", year);
		List<SubjectCodeBatchBean> resultList = namedParameterJdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<SubjectCodeBatchBean>(SubjectCodeBatchBean.class));
		return resultList.stream().collect(Collectors.toMap(SubjectCodeBatchBean::getTimeboundId,Function.identity()));
	}

	public Map<String,MBAPassFailResponseBean> getTimeboundIdsList(List<String> acadMonthYearList)throws Exception
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String query = "select id as timeboundId,acadMonth as month,acadYear as year,prgm_sem_subj_id as pssId from lti.student_subject_config where concat(acadMonth,'/',acadYear) in (:acadMonthYearList)";
		parameters.addValue("acadMonthYearList", acadMonthYearList);
		List<MBAPassFailResponseBean> resultList = namedParameterJdbcTemplate.query(query, parameters,new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		return resultList.stream().collect(Collectors.toMap(MBAPassFailResponseBean::getTimeboundId, Function.identity()));
	}
	
	public List<String> getTimeboundIdsList(List<String> sapIds,Set<String> timeboundIds) {
		String sql = "select distinct(timebound_subject_config_id) as timeboundId from lti.timebound_user_mapping where userId in (:sapIds) and role='Student' and timebound_subject_config_id in (:timeboundId);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sapIds",sapIds);
		mapSource.addValue("timeboundId",timeboundIds);
		List<String> timeboundIdsList = namedParameterJdbcTemplate.queryForList(sql,mapSource,String.class);
		return timeboundIdsList;
	}

	public Map<String,List<MBAPassFailResponseBean>> getTimeboundIdMarksMap(String masterkeysem,List<String> timeboundIds,List<String> sapIds){
		String sql = "select sapid,iaScore,teeScore,graceMarks,timeboundId from exam.mba_passfail where timeboundId in (:timeboundIds) and isPass='Y' and isResultLive='Y' and sapId in (:sapIds) and iaScore is not null group by timeboundId,sapId;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("timeboundIds",timeboundIds);
		mapSource.addValue("sapIds",sapIds);
		List<MBAPassFailResponseBean> timeboundIdsMarksList = namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		Map<String,List<MBAPassFailResponseBean>> timeboundIdsMarksMap =  new HashMap<>();
		Set<String> timeboundIdsSet = new HashSet<>(timeboundIds);
		for(String timeboundId : timeboundIdsSet) {
			if(!timeboundIdsMarksMap.keySet().contains(masterkeysem+"/"+timeboundId)) {
				timeboundIdsMarksMap.put(masterkeysem+"/"+timeboundId, timeboundIdsMarksList.stream().filter(key->key.getTimeboundId().equalsIgnoreCase(timeboundId)).distinct().collect(Collectors.toList()));
			}
		}
		return timeboundIdsMarksMap;
	}
	
	
	public List<MBAPassFailResponseBean> getMarksList(String masterkeysem,List<String> timeboundIds,List<String> sapIds) {
		String sql = "select sapid,iaScore,teeScore,graceMarks,timeboundId from exam.mba_passfail where timeboundId in (:timeboundIds) and isPass='Y' and isResultLive='Y' and sapId in (:sapIds) and iaScore is not null group by timeboundId,sapId;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("timeboundIds",timeboundIds);
		mapSource.addValue("sapIds",sapIds);
		List<MBAPassFailResponseBean> marksList = namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return marksList;
	}
	
	public List<String> getAcadMonthYearList() {
		String sql = "select concat(month,'/',year) from exam.subject_wise_rank_timebound group by month,year;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		List<String> list = namedParameterJdbcTemplate.queryForList(sql, mapSource, String.class);
		return list;
	}

	public List<String> getSapIdAndSubjectList(String month, String year) {
		String sql = "select sapid,informationForPostPayment as subject from portal.service_request where serviceRequestType in ('Subject Repeat','Subject Repeat M.Sc. AI and ML Ops','Subject Repeat MBA - WX') and  tranStatus='Payment Successful' and month=:month and year=:year;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("month", month);
		mapSource.addValue("year", year);
		List<MBAPassFailResponseBean> list = namedParameterJdbcTemplate.query(sql, mapSource,
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return list.stream().map(bean -> bean.getSapid() + "/" + bean.getSubject()).collect(Collectors.toList());
	}

	public List<MBAPassFailResponseBean> getSapIdAndSubjectList(String sapid) {
		String sql = "select sapid,informationForPostPayment as subject,month,year,sem from portal.service_request where serviceRequestType in ('Subject Repeat','Subject Repeat M.Sc. AI and ML Ops','Subject Repeat MBA - WX') and  tranStatus='Payment Successful' and sapid=:sapid;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sapid", sapid);
		List<MBAPassFailResponseBean> list = namedParameterJdbcTemplate.query(sql, mapSource,
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class));
		return list;
	}

	public Map<String, String> getTimeboundIdAndPssIdMap(String month, String year,List<String> pssIds) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("month", month);
		parameters.addValue("year",year);
		parameters.addValue("pssIds",pssIds);
		String sql = "select id as timeboundId,prgm_sem_subj_id as pssId from lti.student_subject_config where acadMonth=:month and acadYear=:year and prgm_sem_subj_id not in (:pssIds)";
		List<MBAPassFailResponseBean> list = namedParameterJdbcTemplate.query(sql,parameters,
				new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		return list.stream().collect(Collectors.toMap(key -> key.getTimeboundId(), value -> value.getPssId()));
	}

	public List<String> getSapIdsFromTUM(String timeboundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select userId from lti.timebound_user_mapping where timebound_subject_config_id=? and role='Student'";
		return jdbcTemplate.queryForList(sql, new Object[] { timeboundId }, String.class);

	}

	public Map<String, Object> getPssIdDetailsMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select id as pssId,subject,sem from exam.program_sem_subject";
		List<MBAPassFailResponseBean> list = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("pssIdSemMap",
				list.stream().collect(Collectors.toMap(key -> key.getPssId(), value -> value.getSem())));
		responseMap.put("pssIdSubjectMap",
				list.stream().collect(Collectors.toMap(key -> key.getPssId(), value -> value.getSubject())));
		return responseMap;
	}

	public List<MBAPassFailResponseBean> getPassFailDetails(String timeboundId, List<String> sapIds) {
		String sql = "select sapid,iaScore,teeScore,graceMarks,timeboundId,prgm_sem_subj_id as pssId,isPass from exam.mba_passfail where sapid in (:sapIds) and timeboundId=:timeboundId;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapIds", sapIds);
		parameters.addValue("timeboundId", timeboundId);
		return namedParameterJdbcTemplate.query(sql, parameters,
				new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
	}

	public Map<String, String> getSapIdSemMap(String month, String year, List<String> masterkeys) {
		String sql = "select sapid,sem from exam.registration where month=:month and year=:year and consumerProgramStructureId in (:masterkeys);";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("masterkeys", masterkeys);
		parameters.addValue("month", month);
		parameters.addValue("year", year);
		List<MBAPassFailResponseBean> list = namedParameterJdbcTemplate.query(sql, parameters,
				new BeanPropertyRowMapper<>(MBAPassFailResponseBean.class));
		return list.stream().collect(Collectors.toMap(key -> key.getSapid(), value -> value.getSem()));
	}

	public List<String> getPassScore0SubjectList() {
		String sql = "select id from exam.program_sem_subject where passScore=0;";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForList(sql, String.class);
	}
	
	public List<String> getBatchIds(String month,String year,List<String> masterKeys){
		String sql = "select id from exam.batch where acadMonth=:month and acadYear=:year and consumerProgramStructureId in (:masterKeys);";
	    namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    MapSqlParameterSource mapSource = new MapSqlParameterSource();
	    mapSource.addValue("masterKeys", masterKeys);
	    mapSource.addValue("month", month);
	    mapSource.addValue("year", year);
	    List<String> list = namedParameterJdbcTemplate.queryForList(sql, mapSource,String.class);
	    return list;
	}
	
	public List<String> getRankLiveMonthYearList(){
		String sql = "select concat(month,'/',year) from exam.cycle_wise_rank_timebound group by month,year";
		jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForList(sql,String.class);
	}
	
	public List<String> getTimeboundIdsUsingBatchIds(String month,String year,List<String> batchIds,List<String> prgm_sem_subj_id){
		String sql = "select id from lti.student_subject_config where batchId in (:batchIds) and acadMonth=:month and acadYear=:year ";
	    namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	    MapSqlParameterSource mapSource = new MapSqlParameterSource();
	    if(prgm_sem_subj_id.size()>0) {
	    	sql+="and prgm_sem_subj_id not in (:prgm_sem_subj_id);";
	    	mapSource.addValue("prgm_sem_subj_id", prgm_sem_subj_id);
	    }
	    mapSource.addValue("batchIds", batchIds);
	    mapSource.addValue("month", month);
	    mapSource.addValue("year", year);
	    List<String> list = namedParameterJdbcTemplate.queryForList(sql, mapSource,String.class);
	    return list;
	}
	
	public List<MBAPassFailResponseBean> getRankIdsForTimeboundForAllStudents(String masterkey,String month,String year,String sem){
		String sql="select sem,month,year,masterkey,group_concat(swrt.id) as rankIds from exam.cycle_wise_rank_timebound swrt where SUBSTRING(swrt.rank, 1, POSITION('/' IN swrt.rank)) <=5 and swrt.rank>0 and masterkey =:masterkey and month=:month and year=:year and sem =:sem group by sem,masterkey,month,year;";
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("masterkey", masterkey);
		parameters.addValue("month", month);
		parameters.addValue("year", year);
		parameters.addValue("sem", sem);
		List<MBAPassFailResponseBean> returnBean = namedParameterJdbcTemplate.query(
				sql,
				parameters,
				new BeanPropertyRowMapper<MBAPassFailResponseBean>(MBAPassFailResponseBean.class)
				);
		return returnBean;
	}
	
}
