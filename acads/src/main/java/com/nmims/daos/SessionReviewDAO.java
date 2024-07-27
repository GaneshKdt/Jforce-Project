package com.nmims.daos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionReviewBean;

@Component
public class SessionReviewDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
 

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value( "${MAX_WEBEX_USERS}" )
	private int MAX_WEBEX_USERS2;
	private int MAX_WEBEX_USERS=2000;
	
	
	@Transactional(readOnly = true)
	public List<SessionDayTimeAcadsBean> reviewListByFacultyId(String facultyId,String action){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select asr.reviewed,asr.id,f.firstName,f.lastName,asi.sessionName,asi.date,asi.subject,asi.corporateName "
				+ "  from acads.session_review asr,acads.faculty f,acads.sessions asi where "
				+ " asr.facultyId = f.facultyId and asr.sessionId = asi.id and f.active = 'Y' ";
		
		if("view".equals(action))
		{
			sql +=" and asr.facultyId = ?  order by asi.subject,asi.date ,asi.sessionName";
		}else{
			sql +=" and asr.reviewerFacultyId = ?  order by asi.subject,asi.date ,asi.sessionName";
		}
		
		return jdbcTemplate.query(sql, new Object[]{facultyId},new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
	}
	
	@Transactional(readOnly = true)
	public SessionReviewBean findSessionReviewById(String reviewId) {
		String sql ="select * from acads.session_review where id = ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		SessionReviewBean reviewBean = (SessionReviewBean)jdbcTemplate.queryForObject(sql, new Object[]{reviewId},new BeanPropertyRowMapper(SessionReviewBean.class));
		return reviewBean;
	}
	
	
	/*stef added on Sep-2017*/
	@Transactional(readOnly = true)
	public List<SessionReviewBean> reviewListBasedOnCriteria(SessionReviewBean reviewBean){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = " select asr.*,s.* from acads.session_review asr,acads.faculty af , acads.sessions s"
				+ " where s.id = asr.sessionId ";
		StringBuffer sBuffer = new StringBuffer(sql);
		MapSqlParameterSource namedParams= new MapSqlParameterSource();

		if(!StringUtils.isBlank(reviewBean.getSubject())){
			sBuffer.append(" and s.subject = :subject ");
			namedParams.addValue("subject",reviewBean.getSubject().trim());
		}
		if(!StringUtils.isBlank(reviewBean.getMonth())){
			sBuffer.append(" and s.month = :month ");
			namedParams.addValue("month",reviewBean.getMonth().trim());
		}
		if(!StringUtils.isBlank(reviewBean.getYear())){
			sBuffer.append(" and s.year = :year ");
			namedParams.addValue("year",reviewBean.getYear().trim());
		}
	
		if(!StringUtils.isBlank(reviewBean.getFirstName())){
			sBuffer.append(" and asr.reviewerFacultyId = af.facultyId and ( af.firstName Like :firstName ) " );
			namedParams.addValue("firstName","%"+reviewBean.getFirstName().trim()+"%");
			
		}
		
		if(!StringUtils.isBlank(reviewBean.getLastName())){
			sBuffer.append(" and ( af.lastName Like :lastName ) " );
			namedParams.addValue("lastName","%"+reviewBean.getLastName().trim()+"%");
		}
		
		if(!StringUtils.isBlank(reviewBean.getQ1Response())){
			sBuffer.append(" and asr.q1Response = :q1Response " );
			namedParams.addValue("q1Response",reviewBean.getQ1Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ2Response())){
			sBuffer.append(" and asr.q2Response  = :q2Response " );
			namedParams.addValue("q2Response",reviewBean.getQ2Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ3Response())){
			sBuffer.append(" and asr.q3Response =  :q3Response " );
			namedParams.addValue("q3Response",reviewBean.getQ3Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ4Response())){
			sBuffer.append(" and asr.q4Response = :q4Response " );
			namedParams.addValue("q4Response",reviewBean.getQ4Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ5Response())){
			sBuffer.append(" and asr.q5Response = :q5Response  " );
			namedParams.addValue("q5Response",reviewBean.getQ5Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getQ6Response())){
			sBuffer.append(" and asr.q6Response = :q6Response " );
			namedParams.addValue("q6Response",reviewBean.getQ6Response().trim());	
		}
		if(!StringUtils.isBlank(reviewBean.getReviewed())){
			sBuffer.append(" and asr.reviewed = :reviewed " );
			namedParams.addValue("reviewed",reviewBean.getReviewed().trim());	
		}
		
		sBuffer.append("  group by asr.sessionId, asr.reviewerFacultyId  order by asr.id ");

		return namedParameterJdbcTemplate.query(sBuffer.toString(),namedParams,new BeanPropertyRowMapper(SessionReviewBean.class));
	}/*---------------------*/
	
	@Transactional(readOnly = true)
	public HashMap<String,FacultyAcadsBean> getAllFacultyMapper() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";

		ArrayList<FacultyAcadsBean> facultyList = (ArrayList<FacultyAcadsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyAcadsBean.class));
		HashMap<String,FacultyAcadsBean> facultyIdAndFacultyBeanMap = new HashMap<String,FacultyAcadsBean>();
		for(FacultyAcadsBean bean :facultyList)
		{
			facultyIdAndFacultyBeanMap.put(bean.getFacultyId(), bean);
		}
		return facultyIdAndFacultyBeanMap;
	}
	
	protected BeanPropertySqlParameterSource getParameterSource(SessionReviewBean reviewBean) {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(reviewBean);
		return parameterSource;
	}
	
	@Transactional(readOnly = false)
	public void updateFacultyReview(SessionReviewBean reviewBean){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		SqlParameterSource parameterSource = getParameterSource(reviewBean);
		String sql = " update acads.session_review set q1Response=:q1Response,q2Response=:q2Response,q3Response=:q3Response, "
				+ " q4Response=:q4Response,q5Response=:q5Response,q6Response=:q6Response,q1Remarks=:q1Remarks, "
				+ " q2Remarks=:q2Remarks,q3Remarks=:q3Remarks,q4Remarks=:q4Remarks,q5Remarks=:q5Remarks, q6Remarks=:q6Remarks,peerReviewAvg=:peerReviewAvg, "
				+ " lastModifiedDate=:lastModifiedDate,reviewed=:reviewed,lastModifiedBy=:lastModifiedBy where id=:id ";
		
		namedParameterJdbcTemplate.update(sql, parameterSource);

	}
	
	@Transactional(readOnly = true)
	public HashMap<String,SessionDayTimeAcadsBean> mapOfSessionIdAndSessionBeanFromGivenSubjectList(Set<String> setOfSubject ,String Current_Acad_Month,int Current_Acad_Year){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("setOfSubject",setOfSubject);
		paramSource.addValue("Current_Acad_Month",Current_Acad_Month);
		paramSource.addValue("Current_Acad_Year",Current_Acad_Year);

		String sql = " select * from acads.sessions where subject in (:setOfSubject) and month=:Current_Acad_Month and year=:Current_Acad_Year";
		List<SessionDayTimeAcadsBean> sessionListFromId = new ArrayList<SessionDayTimeAcadsBean>();
		HashMap<String,SessionDayTimeAcadsBean> mapOfSessionIdAndSessionBean = new HashMap<String,SessionDayTimeAcadsBean>();
		try{
			sessionListFromId = namedParameterJdbcTemplate.query(sql, paramSource,new BeanPropertyRowMapper(SessionDayTimeAcadsBean.class));
			for(SessionDayTimeAcadsBean s : sessionListFromId){
				mapOfSessionIdAndSessionBean.put(s.getId(),s);
			}
			return mapOfSessionIdAndSessionBean;
		}catch(Exception e){
			  
			return null;
		}

	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateSessionReviewFacultyMapping(final List<SessionReviewBean> sessionReviewFacultyList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (SessionReviewBean sessionReview : sessionReviewFacultyList) {
			try{
				upsertSessionReviewFacultyMapping(sessionReview, jdbcTemplate);

			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	
	@Transactional(readOnly = false)
	private void upsertSessionReviewFacultyMapping(SessionReviewBean bean, JdbcTemplate jdbcTemplate) {
		StringBuffer sql = new StringBuffer(" INSERT INTO acads.session_review ( sessionId,facultyId,reviewed,reviewerFacultyId, createdBy , createdDate,"
				+ "lastModifiedBy, lastModifiedDate )");
		sql.append(" VALUES ");
		sql.append(" ( ? , ? , ? , ? , ? , sysdate(),?,sysdate() ) ");
		sql.append(" on duplicate key update ");
		sql.append(" reviewerFacultyId = ? , lastModifiedBy = ? , sessionId = ? , facultyId = ? , lastModifiedDate = sysdate() ");

		jdbcTemplate.update(sql.toString(), new Object[] { 
				bean.getSessionId(),
				bean.getFacultyId(),
				bean.getReviewed(),
				bean.getReviewerFacultyId(),
				bean.getCreatedBy(),
				bean.getLastModifiedBy(),
				bean.getReviewerFacultyId(),
				bean.getLastModifiedBy(),
				bean.getSessionId(),
				bean.getFacultyId()

		});
	}
	
}



