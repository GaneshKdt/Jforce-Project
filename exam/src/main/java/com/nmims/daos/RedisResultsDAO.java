package com.nmims.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.RedisPassFailBean;
import com.nmims.beans.RedisStudentBean;
import com.nmims.beans.RedisStudentMarksBean;
import com.nmims.beans.StudentMarksBean;


public class RedisResultsDAO extends BaseDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public static final String PGM_CODE_1 = "C-SEM";
	public static final String PGM_CODE_2 = "C-SMM";
	public static final String PGM_CODE_3 = "C-DMA";
	public static final String PGM_CODE_4 = "C-SEM & DMA";
	public static final String PGM_CODE_5 = "C-SMM & DMA";
	public static final String PGM_CODE_6 = "C-SEM & SMM";
	public static final String PGM_CODE_7 = "PDDM";
	public static final String PGM_CODE_8 = "PC-DM";
	public static final String PGM_CODE_9 = "MBA - WX";
	public static final String PGM_CODE_10 = "MBA - X";
	public static final String PGM_CODE_11 = "M.Sc. (AI & ML Ops)";
	public static final String PGM_CODE_12 = "M.Sc. (AI) - DO";
	public static final String PGM_CODE_13 = "M.Sc. (AI) - DL";
	
	/**
	 * REDIS Store results only for Non MBA-WX, Non MBA-X, Non M.Sc and others. Also
	 * has seperate marks and passfail tables.  
	 */
	public static final ArrayList<String> EXCLUDE_PROGRAM_TEE_LIST = new ArrayList<String>(
			Arrays.asList(PGM_CODE_1, PGM_CODE_2, PGM_CODE_3, PGM_CODE_4, PGM_CODE_5, PGM_CODE_6, PGM_CODE_7,
					PGM_CODE_8, PGM_CODE_9, PGM_CODE_10, PGM_CODE_11, PGM_CODE_12, PGM_CODE_13));
	

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
	public Map<String, List<RedisPassFailBean>> getAllPassFailMarksMapByRegYear(String regYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		Map<String, List<RedisPassFailBean>> map = new HashMap<String, List<RedisPassFailBean>>(); 
		
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`passfail` "
			+ " WHERE `sapid` IN ( "
				+ " SELECT `sapid` "
				+ " FROM `exam`.`students` "
				+ " WHERE `enrollmentYear` = ? "
			+ " ) ";
		List<RedisPassFailBean> list = jdbcTemplate.query(
				sql, 
				new Object[] { regYear },
				new BeanPropertyRowMapper<RedisPassFailBean>(RedisPassFailBean.class)
			);

			for (RedisPassFailBean bean : list) {
				List<RedisPassFailBean> currList = new ArrayList<RedisPassFailBean>();
				if(map.containsKey(bean.getSapid())) {
					currList = map.get(bean.getSapid());
				}
				currList.add(bean);
				map.put(bean.getSapid(), currList);
			}
			
			return map;
	}
	
	@Transactional(readOnly = true)
	public Map<String, List<RedisStudentMarksBean>> getAllStudentMarksMapByRegYear(String regYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`marks` "
			+ " WHERE `sapid` IN ( "
				+ " SELECT `sapid` "
				+ " FROM `exam`.`students` "
				+ " WHERE `enrollmentYear` = ? "
			+ " ) "
			+ " AND `processed` = 'Y'";	
		
		Map<String, List<RedisStudentMarksBean>> map = new HashMap<String, List<RedisStudentMarksBean>>();
		List<RedisStudentMarksBean> list = jdbcTemplate.query(
			sql, 
			new Object[] { regYear },
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);

		for (RedisStudentMarksBean bean : list) {
			List<RedisStudentMarksBean> currList = new ArrayList<RedisStudentMarksBean>();
			if(map.containsKey(bean.getSapid())) {
				currList = map.get(bean.getSapid());
			}
			currList.add(bean);
			map.put(bean.getSapid(), currList);
		}
		
		return map;
	}

	@Transactional(readOnly = true)
	public Map<String, List<RedisStudentMarksBean>> getAllStudentsMostRecentAssignmentMarks(String examYear, String examMonth, String enrollmentYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	 	String sql = ""
	 			+ " select a.*, c.reason, c.markedForRevaluation, c.revaluationRemarks , c.revaluationScore from exam.marks a, exam.assignmentsubmission c "
				+ " where 1"
				+ " and a.year = ? and a.month = ? "
				+ " and a.year = c.year and a.month = c.month and a.sapid = c.sapid and a.subject = c.subject "
				+ " and a.processed = 'Y' "
				+ " and a.sapid IN ( "
					+ " SELECT `sapid` "
					+ " FROM `exam`.`students` "
					+ " WHERE `enrollmentYear` = ? "
				+ " ) "
				+ " order by sem, subject asc";
	 	
		List<RedisStudentMarksBean> list = jdbcTemplate.query(
			sql,
			new Object[]{ examYear, examMonth, enrollmentYear },
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);


		Map<String, List<RedisStudentMarksBean>> map = new HashMap<String, List<RedisStudentMarksBean>>();
		for (RedisStudentMarksBean bean : list) {
			List<RedisStudentMarksBean> currList = new ArrayList<RedisStudentMarksBean>();
			if(map.containsKey(bean.getSapid())) {
				currList = map.get(bean.getSapid());
			}
			currList.add(bean);
			map.put(bean.getSapid(), currList);
		}
		
		return map;
	}
	
	@Transactional(readOnly = true)
	public Map<String, List<RedisStudentMarksBean>> getAllCurrentCycleMarksMapByRegYear(String year, String month, String regYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
		+ "SELECT "
			+ " `m`.*, "
			+ " ROUND((`om`.`part1marks` + `om`.`part2marks` + `om`.`part3marks`), 2) AS `mcq`, "
			+ " ROUND(`om`.`part4marks`, 2) AS `part4marks`, "
			+ " `om`.`roundedTotal` "
		+ " FROM `exam`.`marks` `m` "
		+ " LEFT JOIN `exam`.`online_marks` `om` "
			+ " ON `om`.`sapid` = `m`.`sapid` "
			+ " AND `om`.`month` = `m`.`month` "
			+ " AND `om`.`year` = `m`.`year` "
			+ " AND `om`.`subject` = `m`.`subject` "
		+ " WHERE 1 "
		+ " AND `m`.`year` = ? AND `m`.`month` = ? "
		+ " AND `m`.`processed` = 'Y' "
		+ " AND `m`.`sapid` IN ( "
			+ " SELECT `sapid` "
			+ " FROM `exam`.`students` "
			+ " WHERE `enrollmentYear` = ? "
		+ " ) ";

		List<RedisStudentMarksBean> list = jdbcTemplate.query(
			sql, 
			new Object[] { year, month, regYear }, 
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);

		Map<String, List<RedisStudentMarksBean>> map = new HashMap<String, List<RedisStudentMarksBean>>(); 
		for (RedisStudentMarksBean bean : list) {
			List<RedisStudentMarksBean> currList = new ArrayList<RedisStudentMarksBean>();
			if(map.containsKey(bean.getSapid())) {
				currList = map.get(bean.getSapid());
			}
			currList.add(bean);
			map.put(bean.getSapid(), currList);
		}
		
		return map;
	}

	@Transactional(readOnly = true)
	public Map<String, ExamOrderExamBean> getAllExamOrderMap(){
		final String sql = " Select * from examorder";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));
		Map<String, ExamOrderExamBean> map = new HashMap<String, ExamOrderExamBean>();
		for (ExamOrderExamBean row : rows) {
			map.put(row.getMonth()+row.getYear(), row);
		}
		return map ;
	}

	@Transactional(readOnly = true)
	public HashMap<String, RedisStudentBean> getAllStudentsByRegYear(String regYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String excludePrograms = EXCLUDE_PROGRAM_TEE_LIST.stream().filter(e -> Boolean.TRUE).map(e -> ("\'" + e +"\'")).collect(Collectors.joining(", "));
		
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`students` "
			+ " WHERE `enrollmentYear` = ? "
			+ " AND `program` NOT IN (?)";
		
		List<RedisStudentBean> students = jdbcTemplate.query(
			sql, 
			new Object[] { regYear, excludePrograms },
			new BeanPropertyRowMapper<RedisStudentBean>(RedisStudentBean.class)
		);
		
		HashMap<String, RedisStudentBean> studentsMap = new HashMap<String, RedisStudentBean>();
		for (RedisStudentBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}
		
		return studentsMap;
	}

	@Transactional(readOnly = true)
	public List<RedisPassFailBean> getPassFailMarksForSapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`passfail` "
			+ " WHERE `sapid` = ? ";
		return jdbcTemplate.query(
			sql, 
			new Object[] { sapid },
			new BeanPropertyRowMapper<RedisPassFailBean>(RedisPassFailBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<RedisStudentMarksBean> getStudentMarksForSapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`marks` "
			+ " WHERE `sapid` = ? ";

		return jdbcTemplate.query(
			sql, 
			new Object[] { sapid },
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<RedisStudentMarksBean> getCurrentCycleMarksForSapid(String year, String month, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = ""
		+ "SELECT "
			+ " `m`.*, "
			+ " ROUND((`om`.`part1marks` + `om`.`part2marks` + `om`.`part3marks`), 2) AS `mcq`, "
			+ " ROUND(`om`.`part4marks`, 2) AS `part4marks`, "
			+ " `om`.`roundedTotal` "
		+ " FROM `exam`.`marks` `m` "
		+ " LEFT JOIN `exam`.`online_marks` `om` "
			+ " ON `om`.`sapid` = `m`.`sapid` "
			+ " AND `om`.`month` = `m`.`month` "
			+ " AND `om`.`year` = `m`.`year` "
			+ " AND `om`.`subject` = `m`.`subject` "
		+ " WHERE 1 "
		+ " AND `m`.`year` = ? AND `m`.`month` = ? "
		+ " AND `m`.`sapid` = ? ";

		return jdbcTemplate.query(
			sql, 
			new Object[] { year, month, sapid }, 
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public RedisStudentBean getStudentBySapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * "
			+ " FROM `exam`.`students` "
			+ " WHERE `sapid` = ? ";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] { sapid },
			new BeanPropertyRowMapper<RedisStudentBean>(RedisStudentBean.class)
		);
	}

	@Transactional(readOnly = true)
	public List<RedisStudentMarksBean> getAStudentsMostRecentAssignmentMarks(String examYear, String examMonth, String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	 	String sql = ""
	 			+ " select a.*, c.reason, c.markedForRevaluation, c.revaluationRemarks , c.revaluationScore from exam.marks a, exam.assignmentsubmission c "
				+ " where 1"
				+ " and a.sapid = ? and a.year = ? and a.month = ? "
				+ " and a.year = c.year and a.month = c.month and a.sapid = c.sapid and a.subject = c.subject "
				+ " order by sem, subject asc";
	 	
		return jdbcTemplate.query(
			sql,
			new Object[]{ sapid, examYear, examMonth }, 
			new BeanPropertyRowMapper<RedisStudentMarksBean>(RedisStudentMarksBean.class)
		);
	}
}
