package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.DivisionBean;
import com.nmims.beans.StudentDivisionMappingBean;

@Component
public class DivisionDetailsDao extends BaseDAO {

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

	@Transactional(readOnly = false)
	public String insertMBAWXDivisionDeatils(DivisionBean bean) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String status = "Success";
		String sql = " INSERT INTO lti.mbawx_divisions(year, month, divisionName, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				+ " VALUES (?,?,?,?,sysdate(),?,sysdate())";

		int num = jdbcTemplate.update(sql, new Object[] { bean.getYear(), bean.getMonth(), bean.getDivisionName(),
				bean.getCreatedBy(), bean.getCreatedBy() });
		if (num < 0) {
			status = "Error";
		}
		return status;
	}
	@Transactional(readOnly = true)
	public List<DivisionBean> getExistingDivisionDetails() throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM lti.mbawx_divisions";
		List<DivisionBean> query = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper<>(DivisionBean.class));
		return query;
	}
	@Transactional(readOnly = true)
	public Boolean duplicateStudentEntriesCheck(String sapId, String divisionId) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT count(*) FROM lti.student_division_mapping where sapId=? and divisionId=?";
		int count=(int)jdbcTemplate.queryForObject(sql, new Object[] { sapId, divisionId},Integer.class);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}
	@Transactional(readOnly = false)
	public int insertStudentToDivisionMappingBean(ArrayList<StudentDivisionMappingBean> listOfStudent,
			String createdBy) throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO lti.student_division_mapping(sapId, divisionId, "
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				+ " VALUES (?,?,?,sysdate(),?,sysdate())";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				StudentDivisionMappingBean bean =listOfStudent.get(i);
				ps.setString(1,bean.getSapId());
				ps.setString(2,bean.getDivisionId());
				ps.setString(3,bean.getCreatedBy());
				ps.setString(4,bean.getCreatedBy());
				
			}
			
			@Override
			public int getBatchSize() {
				return listOfStudent.size();
			}
		});
		
		return batchUpdate.length;
	}
	@Transactional(readOnly = true)
	public List<StudentDivisionMappingBean>getListOfDivisionStudent(String divisionId)  throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="SELECT id,sapId,divisionId FROM lti.student_division_mapping where divisionId=?;";
		List<StudentDivisionMappingBean> list = jdbcTemplate.query(sql,new Object[] {divisionId},new BeanPropertyRowMapper<>(StudentDivisionMappingBean.class));
		return list;
	}
	
	
	@Transactional(readOnly = true)
	public List<String> getListOfStudentByYear(String year){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select sapid from exam.students where enrollmentYear=? ";
		return jdbcTemplate.query(sql,new Object[] {year},new SingleColumnRowMapper<>(String.class));
		
	}
	
	
	
}