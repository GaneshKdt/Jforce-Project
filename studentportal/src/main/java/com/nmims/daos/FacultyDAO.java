package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FacultyStudentPortalBean;


@Repository("facultyDAO")
public class FacultyDAO extends BaseDAO{
	
	
	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
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
	public FacultyStudentPortalBean findfacultyByFacultyId(String facultyId) {
		String sql = "SELECT * FROM acads.faculty WHERE facultyId = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyStudentPortalBean faculty = (FacultyStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, new BeanPropertyRowMapper(FacultyStudentPortalBean.class));

		return faculty;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyStudentPortalBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";
		
		ArrayList<FacultyStudentPortalBean> facultyNameAndIdList  = (ArrayList<FacultyStudentPortalBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(FacultyStudentPortalBean.class));
		
		return facultyNameAndIdList;
	}
	
}
