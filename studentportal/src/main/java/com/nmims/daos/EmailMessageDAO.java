package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EmailMessageBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.SubjectCodeBatchBean;

@Repository("emailMessageDAO")
public class EmailMessageDAO extends BaseDAO{

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<EmailMessageBean> getModulesForMail() throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmailMessageBean> modules = new ArrayList<EmailMessageBean>();
		
		String sql ="select id, type, module , replace(topic, '-', ' ') as topic, provider from communication.modules order by module, topic";
		
		modules = (ArrayList<EmailMessageBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<EmailMessageBean>(EmailMessageBean.class)); 
		return modules;
	}	

	@Transactional(readOnly = true)
	public EmailMessageBean getMessageForModule(EmailMessageBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		EmailMessageBean message = new EmailMessageBean();
		
		String sql ="SELECT * FROM communication.message WHERE moduleId=?;";
		
		message = jdbcTemplate.queryForObject(sql, new Object[] {bean.getModuleId()}, new BeanPropertyRowMapper<EmailMessageBean>(EmailMessageBean.class)); 
		return message;
	}

	@Transactional(readOnly = true)
	public ArrayList<EmailMessageBean> getVariabelsForMessage(EmailMessageBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<EmailMessageBean> variabels = new ArrayList<EmailMessageBean>();
		String sql ="SELECT * FROM communication.variables WHERE messageId=?;";
		
		variabels = (ArrayList<EmailMessageBean>)jdbcTemplate.query(sql, new Object[] { bean.getMessageId() }, 
				new BeanPropertyRowMapper<EmailMessageBean>(EmailMessageBean.class)); 
		return variabels;
	}

	@Transactional(readOnly = false)
	public void updateMessage(EmailMessageBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="UPDATE communication.message "
				+ "SET "
				+ "`subject` = ?, "
				+ "`body` = ?, "
				+ "`from` = ?, "
				+ "`fromEmailId` = ?, "
				+ "`lastModifiedBy` = ?, "
				+ "`lastModifiedDate` = sysdate() "
				+ "WHERE `moduleId` = ?";
		
		jdbcTemplate.update(sql, new Object[] { bean.getSubject(), bean.getBody(), bean.getFrom(), bean.getFromEmailId(), 
				bean.getLastModifiedBy(), bean.getModuleId()});
		
		return;
	}

	@Transactional(readOnly = false)
	public void updateProviderForModule(EmailMessageBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="update communication.modules "
				+ "set "
				+ "`provider` = ?, "
				+ "`lastModifiedBy` = ?, "
				+ "`lastModifiedDate` = sysdate() "
				+ "where `id` = ?";
		
		jdbcTemplate.update(sql, new Object[] { bean.getProvider(), bean.getLastModifiedBy(), bean.getModuleId()});
		
		return;
	}
	
	public ArrayList<String> getSapidsBySubjectCodeList(String subjectCodeId,String registrationMonth,String registrationYear) throws Exception{
		StringBuffer sql = new StringBuffer();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		sql.append("	select r.sapid from exam.mdm_subjectcode_mapping msm	" + 
					"	Inner join  " + 
					"	exam.registration r on msm.consumerProgramStructureId = r.consumerProgramStructureId and msm.sem = r.sem " + 
					"	where msm.subjectCodeId =:subjectCodeId ");
		
		parameters.addValue("subjectCodeId", subjectCodeId);
		if(!StringUtils.isBlank(registrationMonth)){
			sql.append(" and r.month =:registrationMonth ");
			parameters.addValue("registrationMonth", registrationMonth);
		}

		if(!StringUtils.isBlank(registrationYear)){
			sql.append(" and r.year =:registrationYear ");
			parameters.addValue("registrationYear", registrationYear);
		}

		ArrayList<String> studentList = (ArrayList<String>)namedParameterJdbcTemplate.queryForList(sql.toString(), parameters, String.class);
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getStudentListsBySapids(String enrollmentMonth ,String enrollmentYear,String programStructure,ArrayList<String> sapidsList) {
		ArrayList<StudentStudentPortalBean> studentList = new ArrayList<>();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer();
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		sql.append(" SELECT s.* FROM exam.students s  WHERE  NOT(s.programStatus <=> 'Program Terminated')  and s.sapid IN (:sapidsList) ");
		parameters.addValue("sapidsList", sapidsList);

		if(!StringUtils.isBlank(enrollmentMonth)){
			sql.append(" and s.enrollmentMonth =:enrollmentMonth  ");
			parameters.addValue("enrollmentMonth", enrollmentMonth);
		}

		if(!StringUtils.isBlank(enrollmentYear)){
			sql.append(" and s.enrollmentYear =:enrollmentYear ");
			parameters.addValue("enrollmentYear", enrollmentYear);
		}

		if(!StringUtils.isBlank(programStructure)){
			sql.append(" and s.prgmStructApplicable =:programStructure  ");
			parameters.addValue("programStructure", programStructure);
		}
		
		sql.append(" group by s.sapid ");

		studentList = (ArrayList<StudentStudentPortalBean>)namedParameterJdbcTemplate.query(sql.toString(), parameters,new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getStudentListsBytimeBoundIds(String enrollmentMonth ,String enrollmentYear,String programStructure,String timeboundId) {
		ArrayList<StudentStudentPortalBean> studentList = new ArrayList<>();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		StringBuffer sql = new StringBuffer();
		sql.append("	select s.* from " + 
				   "	lti.timebound_user_mapping tum		" + 
				   "	inner join		" + 
				   "	exam.students s on tum.userId = s.sapid	" + 
				   "	where tum.timebound_subject_config_id  =:timeboundId and role = 'Student' and NOT(s.programStatus <=> 'Program Terminated') ");
		parameters.addValue("timeboundId",timeboundId);

		if(!StringUtils.isBlank(enrollmentMonth)){
			sql.append(" and s.enrollmentMonth =:enrollmentMonth  ");
			parameters.addValue("enrollmentMonth", enrollmentMonth);
		}

		if(!StringUtils.isBlank(enrollmentYear)){
			sql.append(" and s.enrollmentYear =:enrollmentYear ");
			parameters.addValue("enrollmentYear", enrollmentYear);
		}
		if(!StringUtils.isBlank(programStructure)){
			sql.append(" and s.prgmStructApplicable =:programStructure  ");
			parameters.addValue("programStructure", programStructure);
		}
		
		studentList = (ArrayList<StudentStudentPortalBean>)namedParameterJdbcTemplate.query(sql.toString(), parameters,new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public List<SubjectCodeBatchBean> getSubjectcodeLists() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SubjectCodeBatchBean> subjectCode = new ArrayList<SubjectCodeBatchBean>();
		StringBuffer sql =  new StringBuffer("SELECT DISTINCT id as subjectCodeId ,subjectcode ,subjectname as subjectName FROM exam.mdm_subjectcode  where active = 'Y' and studentType = 'Regular' order by subjectname asc;");
	
		subjectCode = (ArrayList<SubjectCodeBatchBean>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(SubjectCodeBatchBean.class));
		return subjectCode;  
		
	}
	@Transactional(readOnly = true)
	public List<SubjectCodeBatchBean> getBatchDetails() {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		List<SubjectCodeBatchBean> subjectCode = new ArrayList<SubjectCodeBatchBean>();
		StringBuffer sql =  new StringBuffer("	select b.id as batchId ,b.name as batchName ,sc.subjectname as subjectName,ssc.id as timeboundId from exam.batch b 	" + 
											"	INNER JOIN	" + 
											"	lti.student_subject_config ssc ON b.id = ssc.batchId	" + 
											"	INNER JOIN 		" + 
											"	exam.mdm_subjectcode_mapping msm ON ssc.prgm_sem_subj_id = msm.id	" + 
											"	INNER JOIN		" + 
											"	exam.mdm_subjectcode sc ON msm.subjectCodeId = sc.id ");

		subjectCode = (ArrayList<SubjectCodeBatchBean>) namedParameterJdbcTemplate.query(sql.toString() ,new BeanPropertyRowMapper(SubjectCodeBatchBean.class));
		return subjectCode;  
		
	}
	
	@Transactional(readOnly = true)
	public EmailMessageBean getModuleIdByModuleAndTopic(String module, String topic) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    id as moduleId " + 
				"FROM " + 
				"    communication.modules " + 
				"WHERE " + 
				"    module = ? " + 
				"        AND topic = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {module, topic}, new BeanPropertyRowMapper<>(EmailMessageBean.class));  
		
	}

}
	
