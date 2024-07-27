package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.SessionQueryAnswerStudentPortal;

@Repository("sessionQueryAnswerDAO")
public class SessionQueryAnswerDAO extends BaseDAO {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

//	get list of faculty 
	@Transactional(readOnly = true)
	public List<FacultyStudentPortalBean> getFacultyForASubject(String subject, String consumerProgramStructureId,
			String currentAcadYear, String currentAcadMonth
			) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FacultyStudentPortalBean> facultyForASubject = new ArrayList<>();

		/*
		 * String sql ="SELECT f.* FROM acads.faculty_course fc, acads.faculty f " +
		 * " where fc.subject = ? " + "and fc.year = '"+CURRENT_ACAD_YEAR+"'  " +
		 * "and fc.month = '"+CURRENT_ACAD_MONTH+"' " +
		 * " and f.facultyId = fc.facultyId";
		 */

		String sql = "  SELECT  " + 
				"    f.* " + 
				"FROM " + 
				"    acads.session_subject_mapping ssm " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON ssm.sessionId = s.id " + 
				"        INNER JOIN " + 
				"    acads.faculty f ON f.facultyId = s.facultyId " + 
				"        OR f.facultyId = s.altFacultyId " + 
				"        OR f.facultyId = s.altFacultyId2 " + 
				"        OR f.facultyId = s.altFacultyId3 " + 
				"WHERE " + 
				"         s.year = ? " + 
				"        AND s.month = ? " + 
				"    	 AND s.subject = ? " +
				"        AND ssm.consumerProgramStructureId = ? " +  
				"        AND f.facultyId <> 'NGASCE7777' " + 
				"GROUP BY f.facultyId  ";
		
		try {
			facultyForASubject = (ArrayList<FacultyStudentPortalBean>) jdbcTemplate.query(sql,
					new PreparedStatementSetter() {
						
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							// TODO Auto-generated method stub
							ps.setString(1, currentAcadYear);
							ps.setString(2, currentAcadMonth);
							ps.setString(3, subject);
							ps.setString(4, consumerProgramStructureId);
						}
					}, 
					new BeanPropertyRowMapper<FacultyStudentPortalBean>(FacultyStudentPortalBean.class));
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return facultyForASubject;

	}

	@Transactional(readOnly = true)
	public ExamOrderStudentPortalBean getExamOrderBeanWhereContentLive(){
		ExamOrderStudentPortalBean examOrder = null;
		try{

			String sql = "SELECT * FROM exam.examorder e where e.order = (select max(eo.order) from exam.examorder eo where eo.acadContentLive = 'Y')";

			examOrder =  (ExamOrderStudentPortalBean)jdbcTemplate.queryForObject(sql,new Object[]{}, new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
			return examOrder;

		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public String getFaultyIdToAnswerCourseQuery(String subject) {


		try{

			String sql="select  facultyId, altFacultyId,altFacultyId2, altFacultyId3 " +
					"	from acads.sessions where subject= ? "
					+ " and year= ? and month= ? ";

			System.out.println(" LiveAcadYear: "+getLiveAcadConentYear()+" LiveAcadMonth: "+getLiveAcadConentMonth());
			List<SessionDayTimeStudentPortal> listOfSessions = (List<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql,
					new Object[]{subject, getLiveAcadConentYear(),
							getLiveAcadConentMonth()}, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));

			HashSet<String> listOfFacultyId = new HashSet<String>();

			if(listOfSessions!=null && !listOfSessions.isEmpty()) {
				System.out.println("listOfSessions : "+listOfSessions.size());
				for(SessionDayTimeStudentPortal session :listOfSessions){
					listOfFacultyId.add(session.getFacultyId());
					listOfFacultyId.add(session.getAltFacultyId());
					listOfFacultyId.add(session.getAltFacultyId2());
					listOfFacultyId.add(session.getAltFacultyId3());
				}
				String facultyId = null;
				for(String f :listOfFacultyId){
					if(!StringUtils.isBlank(f)) {
						facultyId=f;
						System.out.println("Assigned facultyId : "+facultyId);
						break;
					}
				}
				return facultyId;
			}else {
				return null;
			}
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}

	}
	
	@Transactional(readOnly = false)
	public void updateSalesforceErrorMessage(SessionQueryAnswerStudentPortal sessionQuery) {
		String sql = "Update acads.session_query_answer"
				+ " set errorMessage = ? , "
				+ " caseId = ?,"
				+ " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";
		jdbcTemplate.update(sql,
				new Object[] {
						sessionQuery.getErrorMessage(),
						sessionQuery.getCaseId(),
						sessionQuery.getLastModifiedBy(),
						sessionQuery.getId()
				}
		);

	}

	@Transactional(readOnly = false)
	public long saveQuery(final SessionQueryAnswerStudentPortal sessionQuery) {
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		/*String sql = "INSERT INTO acads.session_query_answer(sapId, sessionId, query,queryType, assignedToFacultyId, createdBy, createdDate) VALUES "
				+ "(?,?,?,?,?,?, sysdate())";

		jdbcTemplate.update(sql, new Object[] { sessionQuery.getSapId(),
				sessionQuery.getSessionId(), sessionQuery.getQuery(), sessionQuery.getQueryType(),assignedToFacultyId,
				sessionQuery.getSapId() });*/
		if(sessionQuery.getHasTimeBoundId() == null) {
			sessionQuery.setHasTimeBoundId("N");
		}
		jdbcTemplate.update(new PreparedStatementCreator() {
						
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO acads.session_query_answer(sapId, sessionId, query,queryType, assignedToFacultyId, createdBy, createdDate, subject, month, year,timeBoundId,hasTimeBoundId,programSemSubjectId) VALUES (?,?,?,?,?,?, sysdate(),?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, sessionQuery.getSapId());
				statement.setString(2, sessionQuery.getSessionId());
				statement.setString(3,sessionQuery.getQuery());
				statement.setString(4,sessionQuery.getQueryType());
				statement.setString(5,sessionQuery.getAssignedToFacultyId());
				statement.setString(6,sessionQuery.getSapId());
				statement.setString(7,sessionQuery.getSubject());
				statement.setString(8,sessionQuery.getMonth());
				statement.setString(9,sessionQuery.getYear());
				statement.setString(10,sessionQuery.getTimeBoundId());
				statement.setString(11,sessionQuery.getHasTimeBoundId());
				statement.setInt(12,sessionQuery.getProgramSemSubjectId());
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = true)
	public Integer checkForSameAskQuery(SessionQueryAnswerStudentPortal sessionQuery) {
		String sql = " SELECT  " + 
				"    COUNT(*) " + 
				"FROM " + 
				"    acads.session_query_answer " + 
				"WHERE " + 
				"    assignedToFacultyId = ? " + 
				"        AND query = ? " + 
				"        AND sapid = ? " +
				"        AND subject = ?  " +
				"        AND queryType = ? " ;
		Integer count =	jdbcTemplate.queryForObject(sql, new Object[] {
				sessionQuery.getAssignedToFacultyId(),
				sessionQuery.getQuery(), 
				sessionQuery.getSapId(),
				sessionQuery.getSubject(),
				sessionQuery.getQueryType()
				
				}, Integer.class);
		return count;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,ProgramSubjectMappingStudentPortalBean> getProgramSubjectPassingConfigurationMap(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		String sql =" Select * from exam.program_subject ";

		ArrayList<ProgramSubjectMappingStudentPortalBean> lstProgramSubject = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class));
		HashMap<String,ProgramSubjectMappingStudentPortalBean> programSubjectPassScoreMap = new HashMap<>();
		if(lstProgramSubject.size() > 0){
			for(ProgramSubjectMappingStudentPortalBean bean : lstProgramSubject){
				String key = bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
				programSubjectPassScoreMap.put(key, bean);
			}
		}

		return programSubjectPassScoreMap;
	}
}
