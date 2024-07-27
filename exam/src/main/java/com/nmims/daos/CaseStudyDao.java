package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.helpers.PaginationHelper;

public class CaseStudyDao {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getExecutiveStudentRegistrationData(String sapId) {
		StudentExamBean studentRegistrationData = null;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM exam.registration a "
					+ " WHERE "
					+ " a.sapid = ? "
					+ " AND a.sem = (SELECT MAX(b.sem) FROM exam.registration b WHERE b.sapid = ?)";
			studentRegistrationData = (StudentExamBean) jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId},
					new BeanPropertyRowMapper(StudentExamBean.class));
		    } catch (Exception e) {
			// TODO: handle exception
		 }
		return studentRegistrationData;
	 }
	
	@Transactional(readOnly = false)
	public void saveCaseStudyDetails(CaseStudyExamBean bean, String year, String month,String startDate, String endDate){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO portal.case_study (batchYear, batchMonth, topic, "
				+ " filePath, questionFilePreviewPath, lastModifiedBy, lastModifiedDate, program, startDate, endDate) "
				+ " VALUES (?,?,?,?,?,?,sysdate(),?,?,?)"
				+ " on duplicate key update "
				+ "	    filePath = ?,"
				+ "	    questionFilePreviewPath = ?,"
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate() ";
		
		String topic = bean.getTopic();
		String filePath = bean.getFilePath();
		String questionFilePreviewPath = bean.getQuestionFilePreviewPath();
		String lastModifiedBy = bean.getLastModifiedBy();
		String program = bean.getProgram();

		jdbcTemplate.update(sql, new Object[] { year, month, topic,
				 filePath,
				questionFilePreviewPath, lastModifiedBy,program,startDate,endDate,filePath,
				questionFilePreviewPath, lastModifiedBy });

	}
	
	@Transactional(readOnly = true)
	public List<CaseStudyExamBean> getCaseStudyList(String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<CaseStudyExamBean> caseStudyFiles = null;
	try{	
		String sql = "SELECT a.* FROM portal.case_study a"
				+ " WHERE  "
				+ "  a.batchYear = ? "
				+ " AND a.batchMonth = ? "
				+ " AND a.startDate <= now() "
				+ " AND a.endDate > now() ";
		caseStudyFiles = jdbcTemplate.query(sql, new Object[]{year,month}, new BeanPropertyRowMapper(CaseStudyExamBean .class));
	} catch (Exception e) {
		// TODO: handle exception
	}
	return caseStudyFiles;
	}
	
	/*public HashMap<String, CaseStudyBean> getCaseStudySubmissionStatus(
			List<String> topics,String sapId,String year,String month) {
		
			HashMap<String, CaseStudyBean> topicSubmissionMap = new HashMap<>();
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

			String sql = "SELECT a.* FROM exam.case_study_submission a "
					+ " WHERE"
					+ " a.sapid = ? "
					+ " AND a.batchMonth = ?"
					+ " AND a.batchYear = ?"
					+ " AND a.topic in ("+subjectCommaSeparated+") ";

			List<CaseStudyBean> caseStudyFiles = jdbcTemplate.query(sql,new Object[] { sapId,month,year }, new BeanPropertyRowMapper(CaseStudyBean.class));
			if (caseStudyFiles != null && caseStudyFiles.size() > 0) {
				for (int i = 0; i < caseStudyFiles.size(); i++) {
					topicSubmissionMap.put(caseStudyFiles.get(i).getTitle(),
							caseStudyFiles.get(i));
				}
			}
			return topicSubmissionMap;
	}*/
	
	@Transactional(readOnly = true)
	public CaseStudyExamBean getCaseStudySubmissionStatus(
			
				List<String> topics,String sapId,String year,String month) {
		CaseStudyExamBean caseStudyFiles = new CaseStudyExamBean();
			try{
				if (topics == null || topics.size() == 0) {
					return new CaseStudyExamBean();
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
				 
				String sql = "SELECT a.* FROM exam.case_study_submission a "
						+ " WHERE"
						+ " a.sapid = ? "
						+ " AND a.batchMonth = ?"
						+ " AND a.batchYear = ?"
						+ " AND a.topic in ("+subjectCommaSeparated+") ";
		
				 caseStudyFiles = (CaseStudyExamBean)jdbcTemplate.queryForObject(sql,new Object[] { sapId,month,year }, new BeanPropertyRowMapper(CaseStudyExamBean.class));
			}
			catch(EmptyResultDataAccessException e){
				if(caseStudyFiles == null || "".equals(caseStudyFiles)){
					return new CaseStudyExamBean();
				} 	
			}
			return caseStudyFiles;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getCaseStudyTopics(String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> caseStudyFiles = new ArrayList<String>();
	try{	
		String sql = "SELECT a.title FROM portal.case_study a"
				+ " WHERE "
				+ "  a.batchMonth = ? "
				+ " AND a.batchYear = ? "
				+ " AND a.startDate <= now()"
				+ " AND a.endDate > now() ";
		caseStudyFiles = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{month,year}, new SingleColumnRowMapper(String.class));
	} catch (Exception e) {
		// TODO: handle exception
	}
	return caseStudyFiles;
	}
	
	@Transactional(readOnly = false)
	public void saveCaseSubmissionDetails(CaseStudyExamBean bean,
			StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO exam.case_study_submission (batchYear, batchMonth, topic, sapid,"
				+ "  previewPath, status, studentFilePath, "
				+ " lastModifiedBy, lastModifiedDate, evaluated,evaluationCount) VALUES (?,?,?,?,?,?,?,?,sysdate(),'N','0')"
				+ " on duplicate key update "
				+ "     topic = ?,"
				+ "	    studentFilePath = ?,"
				+ "	    previewPath = ?,"
				+ "	    status = ?,"
				+ "	    evaluated = 'N', "
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate() ";

		String year = bean.getBatchYear();
		String month = bean.getBatchMonth();
		String topic = bean.getTopic();
		String sapId = bean.getSapid();
		String previewPath = bean.getPreviewPath();
		String status = bean.getStatus();
		String studentFilePath = bean.getStudentFilePath();
		String lastModifiedBy = bean.getLastModifiedBy();
	

		jdbcTemplate.update(sql, new Object[] { year, month, topic, sapId,
				 previewPath, status,studentFilePath,
				lastModifiedBy,

				topic,studentFilePath, previewPath, status, lastModifiedBy });

		sql = " INSERT INTO exam.casestudysubmissionhistory (batchYear, batchMonth, topic, sapid,  "
				+ " studentFilePath, previewPath, uploadDate) VALUES (?,?,?,?,?,?,sysdate())";

		jdbcTemplate.update(sql, new Object[] { year, month, topic, sapId,
				previewPath,studentFilePath

		});

	}
	
	@Transactional(readOnly = true)
	public CaseStudyExamBean getSubmittedCaseStudyDetails(String sapid,String year,String month ,String title){
		CaseStudyExamBean caseStudyFiles = new CaseStudyExamBean();
		try{
			String sql="select * from exam.case_study_submission where sapid = ? and batchYear = ? and batchMonth = ? and topic = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			 caseStudyFiles = (CaseStudyExamBean) jdbcTemplate.queryForObject(sql,new Object[] { sapid,year,month,title }, new BeanPropertyRowMapper(CaseStudyExamBean.class));
		}catch(EmptyResultDataAccessException e){
			return new CaseStudyExamBean();
		}
		
		return caseStudyFiles;
	}
	
	@Transactional(readOnly = true)
	public List<CaseStudyExamBean> getAllSubmittedCaseStudyDetails(String year,String month){
		List<CaseStudyExamBean> caseStudyFiles = new ArrayList<CaseStudyExamBean>();
		try{
			String sql="select * from exam.case_study_submission where  batchYear = ? and batchMonth = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
			 caseStudyFiles = (List<CaseStudyExamBean>) jdbcTemplate.query(sql,new Object[] { year,month }, new BeanPropertyRowMapper(CaseStudyExamBean.class));
		}catch(EmptyResultDataAccessException e){
			return caseStudyFiles;
		}
		
		return caseStudyFiles;
	}
	
	@Transactional(readOnly = true)
	public List<CaseStudyExamBean> getAllUncheckedCaseStudyDetails(String year,String month,String program){
		List<CaseStudyExamBean> caseStudyFiles = new ArrayList<CaseStudyExamBean>();
		try{
			String sql="select cs.* from exam.case_study_submission cs, exam.students s where  cs.sapid = s.sapid and cs.batchYear = ? and cs.batchMonth = ? "
					+ " and s.program = ? and (cs.evaluated <> 'Y' or cs.evaluated is null) and (cs.facultyId = '' or cs.facultyId is null )";
			jdbcTemplate = new JdbcTemplate(dataSource);
			 caseStudyFiles = (List<CaseStudyExamBean>) jdbcTemplate.query(sql,new Object[] { year,month,program }, new BeanPropertyRowMapper(CaseStudyExamBean.class));
		}catch(EmptyResultDataAccessException e){
			return caseStudyFiles;
		}
		
		return caseStudyFiles;
	}
	
	@Transactional(readOnly = false)
	public void allocateCaseStudy(final List<CaseStudyExamBean> caseStudySubSet, final	String facultyId) {
		String sql = "update exam.case_study_submission "
				+ " set facultyId = ? "
				+ " where batchYear = ? "
				+ " and batchMonth = ?"
				+ " and sapid = ?";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				CaseStudyExamBean c = caseStudySubSet.get(i);
				ps.setString(1, facultyId);
				ps.setString(2, c.getBatchYear());
				ps.setString(3, c.getBatchMonth());
				ps.setString(4, c.getSapid());
			
			}
			public int getBatchSize() {
				return caseStudySubSet.size();
			}

		});

	}
	
	@Transactional(readOnly = true)
	public int getNumberOfCaseStudyNotAssignedToFaculty(CaseStudyExamBean csBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.case_study_submission cs, exam.students s where "
				+ " cs.batchYear = ? and cs.batchMonth = ?  and cs.sapid = s.sapid  and  (cs.facultyId is null or cs.facultyId = '' ) ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(csBean.getBatchYear());
		parameters.add(csBean.getBatchMonth());


		int numberOfCaseStudy = (int) jdbcTemplate.queryForObject(sql, parameters.toArray(),Integer.class);
		return numberOfCaseStudy;
	}
	
	@Transactional(readOnly = true)
	public List<CaseStudyExamBean> getAllCaseStudyAssignedToAFaculty(CaseStudyExamBean csBean, String facultyId){
		List<CaseStudyExamBean> caseStudyFiles = new ArrayList<CaseStudyExamBean>();
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			String sql="select cs.* from exam.case_study_submission cs, exam.students s where  cs.sapid = s.sapid and cs.batchYear = ? and cs.batchMonth = ? "
					+ " and s.program = ? and cs.facultyId = ?";
			parameters.add(csBean.getBatchYear());
			parameters.add(csBean.getBatchMonth());
			parameters.add(csBean.getProgram());
			parameters.add(facultyId);
		
			if(!StringUtils.isBlank(csBean.getEvaluated())){
				sql+= " and evaluated = ?";
				parameters.add(csBean.getEvaluated());

			}
			
			
			 caseStudyFiles = (List<CaseStudyExamBean>) jdbcTemplate.query(sql,parameters.toArray(), new BeanPropertyRowMapper(CaseStudyExamBean.class));
		}catch(EmptyResultDataAccessException e){
			return caseStudyFiles;
		}
		
		return caseStudyFiles;
	}
	
	
	@Transactional(readOnly = true)
	public CaseStudyExamBean getSingleCaseStudyForFaculty(CaseStudyExamBean csBean,String facultyId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql = "select * from exam.case_study_submission cs where cs.batchYear = ? and cs.batchMonth = ? "
				+ " and cs.topic = ?  and cs.sapid = ? and facultyId = ? ";
		parameters.add(csBean.getBatchYear());
		parameters.add(csBean.getBatchMonth());
		parameters.add(csBean.getTopic());
		parameters.add(csBean.getSapid());
		parameters.add(facultyId);
		CaseStudyExamBean  caseStudy = new CaseStudyExamBean();
		try{
		  caseStudy = (CaseStudyExamBean)jdbcTemplate.queryForObject(sql, parameters.toArray(), new BeanPropertyRowMapper(CaseStudyExamBean.class));
		}catch(EmptyResultDataAccessException e){
			new CaseStudyExamBean();
		}
		return caseStudy;
	}
	
	@Transactional(readOnly = false)
	public void evaluateCaseStudy(CaseStudyExamBean caseStudy) {	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Update exam.case_study_submission set evaluated = 'Y', score = ? , evaluationDate = sysdate() , remarks = ? , reason = ?, "
				+ " evaluationCount = ? ,"
				+ "q1Marks = ?, q1Remarks = ?, "
				+ "q2Marks = ?, q2Remarks = ?, "
				+ "q3Marks = ?, q3Remarks = ?, "
				+ "q4Marks = ?, q4Remarks = ?, "
				+ "q5Marks = ?, q5Remarks = ?, "
				+ "q6Marks = ?, q6Remarks = ?, "
				+ "grade =?"
				+ " where batchYear = ? and batchMonth = ? and topic = ?  and sapid = ?  ";
		jdbcTemplate.update(sql, new Object[]{
				caseStudy.getScore(),
				caseStudy.getRemarks(),
				caseStudy.getReason(),
				caseStudy.getEvaluationCount(),
				caseStudy.getQ1Marks(),caseStudy.getQ1Remarks(),
				caseStudy.getQ2Marks(),caseStudy.getQ2Remarks(),
				caseStudy.getQ3Marks(),caseStudy.getQ3Remarks(),
				caseStudy.getQ4Marks(),caseStudy.getQ4Remarks(),
				caseStudy.getQ5Marks(),caseStudy.getQ5Remarks(),
				caseStudy.getQ6Marks(),caseStudy.getQ6Remarks(),
				caseStudy.getGrade(),
				
				caseStudy.getBatchYear(),
				caseStudy.getBatchMonth(),
				caseStudy.getTopic(),
				caseStudy.getSapid()
		});

		sql = "insert into exam.casestudyevaluationhistory (batchYear, batchMonth, sapid, topic, facultyId, createdBy, createdDate, score, remarks, reason,grade) values "
				+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?,?,?)";

		jdbcTemplate.update(sql, new Object[]{
				caseStudy.getBatchYear(),
				caseStudy.getBatchMonth(),
				caseStudy.getSapid(),
				caseStudy.getTopic(),
				caseStudy.getFacultyId(),
				caseStudy.getFacultyId(),
				caseStudy.getScore(),
				caseStudy.getRemarks(),
				caseStudy.getReason(),
				caseStudy.getGrade()
		});
	}
	
	@Transactional(readOnly = true)
	public List<CaseStudyExamBean> getEvaluatedCaseStudyList(CaseStudyExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		
		String sql = "SELECT a.*,f.firstName,f.lastName,s.program,s.PrgmStructApplicable FROM exam.case_study_submission a, acads.faculty f, exam.students s where a.facultyId = f.facultyId and a.sapid=s.sapid";
		String countSql = "SELECT count(*) FROM exam.case_study_submission a, acads.faculty f where a.facultyId = f.facultyId  ";
		
		if( searchBean.getBatchYear() != null &&   !("".equals(searchBean.getBatchYear()))){
			sql = sql + " and a.batchYear = ? ";
			countSql = countSql + " and a.batchYear = ? ";
			parameters.add(searchBean.getBatchYear());
			
		}

		if( searchBean.getBatchMonth() != null &&   !("".equals(searchBean.getBatchMonth()))){
			sql = sql + " and a.batchMonth = ? ";
			countSql = countSql + " and a.batchMonth = ? ";
			parameters.add(searchBean.getBatchMonth());
			
		}
		
		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and a.facultyId = ?";
			countSql = countSql + " and a.facultyId = ? ";
			parameters.add(searchBean.getFacultyId());
			
		}

		if( searchBean.getEvaluated() != null  &&   ("N".equals(searchBean.getEvaluated()))){
			sql = sql + " and (( evaluated = ? or evaluated is null ))";
			countSql = countSql + " and (( evaluated = ? or evaluated is null )) ";
			parameters.add(searchBean.getEvaluated());
			
		}

		if( searchBean.getEvaluated() != null &&   ("Y".equals(searchBean.getEvaluated()))){
			sql = sql + " and (evaluated = ?)";
			countSql = countSql + " and  (evaluated = ?)  ";
			parameters.add(searchBean.getEvaluated());
			
		}

		sql = sql + " group by a.sapid, a.topic order by a.topic asc ";
		Object[] args = parameters.toArray();
		
		
		List<CaseStudyExamBean> evaluatedList =  (List<CaseStudyExamBean>) jdbcTemplate.query( sql, args, new BeanPropertyRowMapper(CaseStudyExamBean.class));

		return evaluatedList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFacultyForCaseStudy(){
		 ArrayList<String> facultyList = new ArrayList<String>();
		try{
			String sql="SELECT facultyId FROM acads.faculty where facultyType = 'Executive'";
			jdbcTemplate = new JdbcTemplate(dataSource);
			facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(String.class));
		}catch(EmptyResultDataAccessException e){
			return facultyList;
		}
		
		return facultyList;
	}
	
	@Transactional(readOnly = false)
	public void changeCaseStudyEvaluationCount(CaseStudyExamBean bean,String val) {
		String sql = "update exam.case_study_submission "
				+ " set evaluationCount = ? "
				+ " where batchYear = ? "
				+ " and batchMonth = ?"
				+ " and sapid = ?"
				+ "	and topic = ?";
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(val);
		parameters.add(bean.getBatchYear());
		parameters.add(bean.getBatchMonth());
		parameters.add(bean.getSapid());
		parameters.add(bean.getTopic());
		Object[] args = parameters.toArray();
	
		jdbcTemplate.update(sql, args);
	}
	
	@Transactional(readOnly = false)
	public void insertDownloadDetails(String title,String user,String filePath) {
		String sql = "insert into exam.report_audit_trails (title,filePath,downloadedBy,downloadedDate)"
				+ " values (?,?,?,sysdate()) ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(title);
		parameters.add(filePath);
		parameters.add(user);
	
		Object[] args = parameters.toArray();
	
		jdbcTemplate.update(sql, args);
	}
	
}
