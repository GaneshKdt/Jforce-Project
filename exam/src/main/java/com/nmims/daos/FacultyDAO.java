package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.Page;
import com.nmims.helpers.PaginationHelper;


public class FacultyDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;

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
	public HashMap<String, Integer> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
			hashMap = new HashMap<String, Integer>();
			for (ExamOrderExamBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear(), Integer.valueOf(row.getOrder()));
			}
		}
		return hashMap;
	}

	

	@Transactional(readOnly = true)
	public ArrayList<String> getAllFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' order by firstname, lastname asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, FacultyExamBean> getFacultiesMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, FacultyExamBean> map = new HashMap<>();
		String sql = "SELECT * FROM acads.faculty  ";

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		for (FacultyExamBean facultyBean : facultyList) {
			map.put(facultyBean.getFacultyId(), facultyBean);
		}
		return map;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getFacultyIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> map = new HashMap<>();
		String sql = "SELECT * FROM acads.faculty where active = 'Y' order by firstName, lastName asc ";

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		for (FacultyExamBean facultyBean : facultyList) {
			map.put(facultyBean.getFacultyId(), facultyBean.getFirstName().trim() + " " + facultyBean.getLastName().trim());
		}
		return map;
	}

	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' order by firstname, lastname asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}

	@Transactional(readOnly = false)
	public void insertFaculty(FacultyExamBean faculty) {
		final String sql = "INSERT INTO acads.faculty ( "
				+ " facultyId, firstname, lastname, email, mobile, active, createdBy, createdDate, lastModifiedBy, lastModifiedDate "
				+ ") "
				+ " VALUES( ?,?,?,?,?,'Y',?,sysdate(),?, sysdate()) ";

		jdbcTemplate = new JdbcTemplate(dataSource);


		final FacultyExamBean bean = faculty;
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, bean.getFacultyId());
				ps.setString(2, bean.getFirstName());
				ps.setString(3, bean.getLastName());
				ps.setString(4, bean.getEmail());
				ps.setString(5, bean.getMobile());
				ps.setString(6, bean.getCreatedBy());
				ps.setString(7, bean.getCreatedBy());
		
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//jdbcTemplate.update(psc);
		jdbcTemplate.update(psc, keyHolder);
		
		int id = keyHolder.getKey().intValue();
		
		faculty.setId(id+"");
		
	}

	@Transactional(readOnly = true)
	public FacultyExamBean findByName(String id) {
		String sql = "SELECT * FROM acads.faculty WHERE id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyExamBean faculty = (FacultyExamBean) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(FacultyExamBean.class));

		return faculty;
	}

	@Transactional(readOnly = false)
	public void updateFaculty(FacultyExamBean faculty) {
		String sql = "Update acads.faculty set "

				+ "firstName = ?,"
				+ "lastName = ?,"
				+ "mobile = ?,"
				+ "email = ?"

				+ " where id='"+faculty.getId()+"'";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				faculty.getFirstName(),
				faculty.getLastName(),
				faculty.getMobile(),
				faculty.getEmail()


		});
		
	}

	@Transactional(readOnly = false)
	public void deactivateFaculty(FacultyExamBean faculty) {
		String sql = "Update acads.faculty set "
				+ "active = 'N' "
				+ " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] {faculty.getId() });
		
	}

	@Transactional(readOnly = true)
	public Page<FacultyExamBean> getFacultyPage(int pageNo, int pageSize,	FacultyExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.faculty  where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.faculty  where 1 = 1  ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and facultyId like  ? ";
			countSql = countSql + " and facultyId like  ? ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getFirstName() != null &&   !("".equals(searchBean.getFirstName()))){
			sql = sql + " and firstName like  ? ";
			countSql = countSql + " and firstName like  ? ";
			parameters.add("%"+searchBean.getFirstName()+"%");
		}
		if( searchBean.getLastName() != null &&   !("".equals(searchBean.getLastName()))){
			sql = sql + " and lastName like  ? ";
			countSql = countSql + " and lastName like  ? ";
			parameters.add("%"+searchBean.getLastName()+"%");
		}
		
		
		sql = sql + " order by firstName asc";

		Object[] args = parameters.toArray();

		PaginationHelper<FacultyExamBean> pagingHelper = new PaginationHelper<FacultyExamBean>();
		Page<FacultyExamBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyExamBean.class));

		return page;
	}

	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesWithAssignmentCount(AssignmentFileBean searchBean, String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = null;
		
		if("1".equalsIgnoreCase(level)){
			sql = "select asb.facultyId, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.facultyId = f.facultyId  group by asb.facultyId order by firstname, lastname asc";
		}else if("2".equalsIgnoreCase(level)){
			sql = "select asb.faculty2, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.faculty2 = f.facultyId  group by asb.faculty2 order by firstname, lastname asc";
		}else if("3".equalsIgnoreCase(level)){
			sql = "select asb.faculty3, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.faculty3 = f.facultyId  group by asb.faculty3 order by firstname, lastname asc";
		}
		
		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new Object[]{
				searchBean.getYear(),
				searchBean.getMonth()
		} , new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesWithProjectCount(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = sql = "select psb.facultyId, firstname, lastname ,count(*) as assignmentsAllocated "
				+ " from exam.projectsubmission psb, acads.faculty f where year = ? and month = ? "
				+ " and psb.facultyId = f.facultyId  group by psb.facultyId order by firstname, lastname asc";
		
		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new Object[]{
				searchBean.getYear(),
				searchBean.getMonth()
		} , new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesWithCaseStudyCount(CaseStudyExamBean csBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = sql = "select cs.facultyId, firstname, lastname ,count(*) as assignmentsAllocated "
				+ " from exam.case_study_submission cs, acads.faculty f where batchYear = ? and batchMonth = ? "
				+ " and cs.facultyId = f.facultyId  group by cs.facultyId order by firstname, lastname asc";
		
		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new Object[]{
				csBean.getBatchYear(),
				csBean.getBatchMonth()
		} , new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesForCaseStudyEvaluation() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' and facultyType = 'Executive' order by firstname, lastname asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}

	
	
}
