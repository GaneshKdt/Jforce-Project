package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.CaseStudyStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;

public class CaseStudyDao {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	@Transactional(readOnly = true)
	public List<CaseStudyStudentPortalBean> getCaseStudyList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<CaseStudyStudentPortalBean> caseStudyFiles = null;
	try{	
		String sql = "SELECT * FROM portal.case_study cs,exam.examorder eo where cs.month = eo.month and cs.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where  assignmentLive = 'Y') ";
		caseStudyFiles = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(CaseStudyStudentPortalBean.class));
		//System.out.println("caseStudyFiles = "+caseStudyFiles);
	} catch (Exception e) {
		// TODO: handle exception
	}
	return caseStudyFiles;
	}
	
	@Transactional(readOnly = true)
	public CaseStudyStudentPortalBean getCaseStudyStatusForASubject(String subject,
			String sapId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT * FROM exam.assignmentsubmission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and subject = ? and a.sapid = ? ";

			////System.out.println("SQL = " + sql);
			CaseStudyStudentPortalBean assignmentFile = (CaseStudyStudentPortalBean) jdbcTemplate
					.queryForObject(sql, new Object[] { subject, sapId },
							new BeanPropertyRowMapper(CaseStudyStudentPortalBean.class));

			return assignmentFile;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}
	
	@Transactional(readOnly = true)
	public HashMap<String, CaseStudyStudentPortalBean> getCaseStudySubmissionStatus(
			ArrayList<String> topics,String sapId) {
		
			HashMap<String, CaseStudyStudentPortalBean> topicSubmissionMap = new HashMap<>();
			if (topics == null || topics.size() == 0) {
				return topicSubmissionMap;
			}
			String subjectCommaSeparated = "";
			for (int i = 0; i < topics.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ topics.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ topics.get(i).replaceAll("'", "''") + "'";
				}
			}

			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "SELECT * FROM exam.case_study_submission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and topic in ("
					+ subjectCommaSeparated
					+ ") and a.sapid = ? ";

			////System.out.println("SQL = " + sql);
			List<CaseStudyStudentPortalBean> caseStudyFiles = jdbcTemplate.query(sql,
					new Object[] { sapId }, new BeanPropertyRowMapper(
							CaseStudyStudentPortalBean.class));
			if (caseStudyFiles != null && caseStudyFiles.size() > 0) {
				for (int i = 0; i < caseStudyFiles.size(); i++) {
					topicSubmissionMap.put(caseStudyFiles.get(i).getTopic(),
							caseStudyFiles.get(i));
				}
			}
			return topicSubmissionMap;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getCaseStudyTopics(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> caseStudyFiles = null;
	try{	
		String sql = "SELECT cs.topics FROM portal.case_study cs,exam.examorder eo where cs.month = eo.month and cs.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where  assignmentLive = 'Y') ";
		caseStudyFiles = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(String.class));
		//System.out.println("caseStudyFiles = "+caseStudyFiles);
	} catch (Exception e) {
		// TODO: handle exception
	}
	return caseStudyFiles;
	}
	
	
/*	public String getCaseStudySubmissionStatus(
			ArrayList<String> topics,String sapId) {
		
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "SELECT * FROM exam.case_study_submission a, exam.examorder eo where a.month = eo.month and a.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') "
					+ " and topic in ("
					+ subjectCommaSeparated
					+ ") and a.sapid = ? ";

			////System.out.println("SQL = " + sql);
			List<CaseStudyBean> caseStudyFiles = jdbcTemplate.query(sql,
					new Object[] { sapId }, new BeanPropertyRowMapper(
							CaseStudyBean.class));
			if (caseStudyFiles != null && caseStudyFiles.size() > 0) {
				for (int i = 0; i < caseStudyFiles.size(); i++) {
					topicSubmissionMap.put(caseStudyFiles.get(i).getTopic(),
							caseStudyFiles.get(i));
				}
			}
			return topicSubmissionMap;
	}*/
	
	
	
}
