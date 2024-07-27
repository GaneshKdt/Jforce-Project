package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.StudentMarksBean;

@Repository("resultDAO")
public class ResultDAO extends BaseDAO {
	
	
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
	
	@Transactional(readOnly = true) 
	public List<StudentMarksBean> getAStudentsMarksForSubject(String sapid,String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.marks a, exam.examorder b where 1 = 1 and a.month = b.month and a.year = b.year and "
				+ " b.live = 'Y' and a.sapid = ? and a.subject = ? order by a.sem  asc";
		List<StudentMarksBean> allStudentsMarksList;
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();

		try {
			allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{sapid,subject}, new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
	
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentRevalWithYearAndMonth(sapid,subject);
		//Iterating through records and making the written score pending since the Revaluation Result is not declared//
		for(StudentMarksBean studentBean : allStudentsMarksList){
			//System.out.println("-------->>>>> loop : " + studentBean.getSubject());
			String key = studentBean.getSubject()+studentBean.getYear()+studentBean.getMonth();
			if("Y".equals(studentBean.getMarkedForRevaluation()) && !"Y".equals(studentBean.getRevaulationResultDeclared())){
				studentBean.setWritenscore("Pending For Reval");
			}
			if(subjectsPendingForAssignmentReval.contains(key)){
				studentBean.setAssignmentscore("Pending For Reval");
			}
			studentsMarksList.add(studentBean);
		}
		return studentsMarksList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return studentsMarksList;

		}
	}
	
	@Transactional(readOnly = true)
	public List<String> getSubjectsPendingForAssigmentRevalWithYearAndMonth(String sapid,String subject){
		String sql = "SELECT CONCAT(subject,year,month) FROM exam.assignmentsubmission where markedForRevaluation ='Y' and revaulationResultDeclared ='N' and sapid = ? and subject = ? ";
		List<String> subjectList = new ArrayList<String>();
		try {
			subjectList = jdbcTemplate.query(sql,new Object[]{sapid,subject},new SingleColumnRowMapper<String>(String.class));
			return subjectList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return subjectList;
		}
		
	}
	
	
	
}
