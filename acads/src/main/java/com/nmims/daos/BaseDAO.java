package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;


public abstract class BaseDAO {

	protected DataSource baseDataSource;
	private JdbcTemplate jdbcTemplate;
	private List<ExamOrderAcadsBean> liveFlagList = null;
	
	private String liveExamMonth = null;
	private String liveExamYear = null;
	private String liveAcadConentMonth = null;
	private String liveAcadConentYear = null;
	private String liveAcadSessionMonth = null;
	private String liveAcadSessionYear = null;
	
	private String liveOnlineExamResultYear = null;
	
	private String liveOfflineExamResultMonth = null;
	private String liveOfflineExamResultYear = null;
	private String liveOnlineExamResultMonth=null;
	private String liveAssignmentMonth = null;
	private String liveAssignmentYear = null;
	
	private String liveResitAssignmentMonth = null;
	private String liveResitAssignmentYear = null;
	
	private String liveAssignmentMarksMonth = null;
	private String liveAssignmentMarksYear = null;

	public abstract void setBaseDataSource();
	
		/*public BaseDAO(){
		getLiveFlagDetails();
	}*/
	
	public void refreshLiveFlagSettings(){
		getLiveFlagDetails(true);
	}

	public List<ExamOrderAcadsBean> getLiveFlagDetails( boolean refresh){
		//Query only once, and refresh later when settings are changed
		if(this.liveFlagList == null || this.liveFlagList.size() == 0 || refresh == true){
			final String sql = " Select * from exam.examorder order by examorder.order asc";
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			this.liveFlagList = (ArrayList<ExamOrderAcadsBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderAcadsBean>(ExamOrderAcadsBean.class));
			
			for (ExamOrderAcadsBean bean : liveFlagList) {
				if("Y".equalsIgnoreCase(bean.getAcadSessionLive())){
					liveAcadSessionMonth = bean.getAcadMonth();
					liveAcadSessionYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getAcadContentLive())){
					liveAcadConentMonth = bean.getAcadMonth();
					liveAcadConentYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getTimeTableLive())){
					liveExamMonth = bean.getMonth();
					liveExamYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getLive())){
					liveOnlineExamResultMonth = bean.getMonth();
					liveOnlineExamResultYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getOflineResultslive())){
					liveOfflineExamResultMonth = bean.getMonth();
					liveOfflineExamResultYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getAssignmentLive())){
					liveAssignmentMonth = bean.getAcadMonth(); 
					liveAssignmentYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getResitAssignmentLive())){
					liveResitAssignmentMonth = bean.getMonth();
					liveResitAssignmentYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getAssignmentMarksLive())){
					liveAssignmentMarksMonth = bean.getMonth();
					liveAssignmentMarksYear = bean.getYear();
				}
			}
		}

		return this.liveFlagList;
	}

	public String getLiveExamMonth() {
		return liveExamMonth;
	}

	public void setLiveExamMonth(String liveExamMonth) {
		this.liveExamMonth = liveExamMonth;
	}

	public String getLiveExamYear() {
		return liveExamYear;
	}

	public void setLiveExamYear(String liveExamYear) {
		this.liveExamYear = liveExamYear;
	}

	public String getLiveAcadConentMonth() {
		return liveAcadConentMonth;
	}

	public void setLiveAcadConentMonth(String liveAcadConentMonth) {
		this.liveAcadConentMonth = liveAcadConentMonth;
	}

	public String getLiveAcadConentYear() {
		return liveAcadConentYear;
	}

	public void setLiveAcadConentYear(String liveAcadConentYear) {
		this.liveAcadConentYear = liveAcadConentYear;
	}

	public String getLiveAcadSessionMonth() {
		return liveAcadSessionMonth;
	}

	public void setLiveAcadSessionMonth(String liveAcadSessionMonth) {
		this.liveAcadSessionMonth = liveAcadSessionMonth;
	}

	public String getLiveAcadSessionYear() {
		return liveAcadSessionYear;
	}

	public void setLiveAcadSessionYear(String liveAcadSessionYear) {
		this.liveAcadSessionYear = liveAcadSessionYear;
	}

	public String getLiveOnlineExamResultMonth() {
		return liveOnlineExamResultMonth;
	}

	public void setLiveOnlineExamResultMonth(String liveOnlineExamResultMonth) {
		this.liveOnlineExamResultMonth = liveOnlineExamResultMonth;
	}

	public String getLiveOnlineExamResultYear() {
		return liveOnlineExamResultYear;
	}

	public void setLiveOnlineExamResultYear(String liveOnlineExamResultYear) {
		this.liveOnlineExamResultYear = liveOnlineExamResultYear;
	}

	public String getLiveOfflineExamResultMonth() {
		return liveOfflineExamResultMonth;
	}

	public void setLiveOfflineExamResultMonth(String liveOfflineExamResultMonth) {
		this.liveOfflineExamResultMonth = liveOfflineExamResultMonth;
	}

	public String getLiveOfflineExamResultYear() {
		return liveOfflineExamResultYear;
	}

	public void setLiveOfflineExamResultYear(String liveOfflineExamResultYear) {
		this.liveOfflineExamResultYear = liveOfflineExamResultYear;
	}

	public String getLiveAssignmentMonth() {
		return liveAssignmentMonth;
	}

	public void setLiveAssignmentMonth(String liveAssignmentMonth) {
		this.liveAssignmentMonth = liveAssignmentMonth;
	}

	public String getLiveAssignmentYear() {
		return liveAssignmentYear;
	}

	public void setLiveAssignmentYear(String liveAssignmentYear) {
		this.liveAssignmentYear = liveAssignmentYear;
	}

	public String getLiveResitAssignmentMonth() {
		return liveResitAssignmentMonth;
	}

	public void setLiveResitAssignmentMonth(String liveResitAssignmentMonth) {
		this.liveResitAssignmentMonth = liveResitAssignmentMonth;
	}

	public String getLiveResitAssignmentYear() {
		return liveResitAssignmentYear;
	}

	public void setLiveResitAssignmentYear(String liveResitAssignmentYear) {
		this.liveResitAssignmentYear = liveResitAssignmentYear;
	}

	public String getLiveAssignmentMarksMonth() {
		return liveAssignmentMarksMonth;
	}

	public void setLiveAssignmentMarksMonth(String liveAssignmentMarksMonth) {
		this.liveAssignmentMarksMonth = liveAssignmentMarksMonth;
	}

	public String getLiveAssignmentMarksYear() {
		return liveAssignmentMarksYear;
	}

	public void setLiveAssignmentMarksYear(String liveAssignmentMarksYear) {
		this.liveAssignmentMarksYear = liveAssignmentMarksYear;
	}

	
	


	@Transactional(readOnly = true)
	public HashMap<String,ProgramSubjectMappingAcadsBean> getProgramSubjectPassingConfigurationMap(){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		String sql =" Select * from exam.program_subject ";

		ArrayList<ProgramSubjectMappingAcadsBean> lstProgramSubject = (ArrayList<ProgramSubjectMappingAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingAcadsBean.class));
		HashMap<String,ProgramSubjectMappingAcadsBean> programSubjectPassScoreMap = new HashMap<>();
		if(lstProgramSubject.size() > 0){
			for(ProgramSubjectMappingAcadsBean bean : lstProgramSubject){
				String key = bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
				programSubjectPassScoreMap.put(key, bean);
			}
		}

		return programSubjectPassScoreMap;
	}


	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypeList(){
		
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ArrayList<ConsumerProgramStructureAcads> ConsumerType = null;
		String sql =  "SELECT id,name FROM exam.consumer_type";
		
		try {
			ConsumerType = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
		} catch (Exception e) {
			  
			return null;
		}
		
		return ConsumerType;  
		
	}

	@Transactional(readOnly = true)
	public ArrayList<ContentAcadsBean>  getconsumerProgramStructureIdsWithSubject(String programId,String programStructureId, String consumerTypeId, String subject){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
		String sql =" SELECT  " + 
					"    pss.consumerProgramStructureId, " + 
					"    pss.id AS programSemSubjectId " + 
					" FROM " + 
					"    exam.program_sem_subject pss " + 
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps ON cps.id = pss.consumerProgramStructureId " + 
					"        AND cps.programId IN ("+ programId +") " + 
					"        AND cps.programStructureId IN ("+ programStructureId +") " + 
					"        AND cps.consumerTypeId IN ("+ consumerTypeId +") " + 
					"        AND pss.subject = ? ";

		ArrayList<ContentAcadsBean> consumerProgramStructureIds = (ArrayList<ContentAcadsBean>) jdbcTemplate.query(
								sql, new Object[] {subject},  new BeanPropertyRowMapper(ContentAcadsBean.class));
		
		
		return consumerProgramStructureIds;
	}
	
	@Transactional(readOnly = true)
	public ContentAcadsBean getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(String programId,String programStructureId, 
																							String consumerTypeId, String subject){

		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ContentAcadsBean consumerProgramStructureId = new ContentAcadsBean();
		
		String sql =" SELECT  " + 
					"    pss.consumerProgramStructureId, " + 
					"    pss.id AS programSemSubjectId " + 
					" FROM " + 
					"    exam.program_sem_subject pss " + 
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps ON cps.id = pss.consumerProgramStructureId " + 
					"        AND cps.programId = ? " + 
					"        AND cps.programStructureId = ? " + 
					"        AND cps.consumerTypeId = ? " + 
					"        AND pss.subject = ? ";

		try {
			consumerProgramStructureId = (ContentAcadsBean) jdbcTemplate.queryForObject(sql,new Object [] 
					{programId,programStructureId,consumerTypeId,subject},new BeanPropertyRowMapper(ContentAcadsBean.class));


		} catch (Exception e) {
			  
		}

		return consumerProgramStructureId;
	}

	@Transactional(readOnly = true)
	public List<String>  getconsumerProgramStructureIds(String programId,String programStructureId, String consumerTypeId){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
		String sql =  "SELECT id FROM exam.consumer_program_structure "
				+ "where programId in ("+ programId +") and "
				+ "programStructureId in ("+ programStructureId +") and "
				+ "consumerTypeId in ("+ consumerTypeId +")";

		List<String> consumerProgramStructureIds = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		
		return consumerProgramStructureIds;
	}
	
	@Transactional(readOnly = true)
	public Map<String,String> getProgramStructureIdNameMap(){
		
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ArrayList<ConsumerProgramStructureAcads> programStructureList = null;
		
		Map<String,String> programStructureIdNameMap = new HashMap<>();
		
		String sql =  "SELECT id,program_structure as name FROM exam.program_structure";
		
		try {
			programStructureList = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
			for(ConsumerProgramStructureAcads c : programStructureList) {
				programStructureIdNameMap.put(c.getId(), c.getName());
			}
			
		} catch (Exception e) {
			
			  
		}
		
		return programStructureIdNameMap;  
		
	}
	
	@Transactional(readOnly = true)
	public Map<String,String> getProgramIdNameMap(){
		
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		ArrayList<ConsumerProgramStructureAcads> programList = null;
		
		Map<String,String> programIdNameMap = new HashMap<>();
		
		String sql =  "SELECT id, code AS name FROM exam.program ORDER BY name";
		
		try {
			programList = (ArrayList<ConsumerProgramStructureAcads>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerProgramStructureAcads.class));
			for(ConsumerProgramStructureAcads c : programList) {
				programIdNameMap.put(c.getId(), c.getName());
			}
			
		} catch (Exception e) {
			  
		}
		
		return programIdNameMap;  
	}

	@Transactional(readOnly = true)
	public List<Long> getTimeboundIdsByProgramSemSubjectIds(ArrayList<String> programSemSubjectIds, String referenceId, String startDate) {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
//<<<<<<< HEAD
//		String sql =  " SELECT id FROM lti.student_subject_config "
//					+ " WHERE prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
//					+ " AND acadYear = '"+ year +"' AND acadMonth = '"+ month +"' ";
//=======
		String sql =  " SELECT id FROM lti.student_subject_config "
					+ " where prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
					+ " and batchId=" + referenceId + " and startDate = ?";

		List<Long> timeboundIds= new ArrayList<>();
		try {
			timeboundIds = (List<Long>) jdbcTemplate.query(sql, new Object[] {startDate}, new SingleColumnRowMapper(Long.class));
			
		} catch (DataAccessException e) {
			  
		}
		
		return timeboundIds;
	}
	
	public List<Long> getTimeboundIdsByProgramSemSubjectIdsMBAX(ArrayList<String> programSemSubjectIds, String referenceId) {
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
//<<<<<<< HEAD
//		String sql =  " SELECT id FROM lti.student_subject_config "
//					+ " WHERE prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
//					+ " AND acadYear = '"+ year +"' AND acadMonth = '"+ month +"' ";
//=======
		String sql =  " SELECT id FROM lti.student_subject_config "
					+ " where prgm_sem_subj_id in ("+StringUtils.join(programSemSubjectIds, ", ")+") "
					+ " and batchId=" + referenceId;

//		System.out.println("IN getTimeboundIdsByProgramSemSubjectIds sql : \n"+sql);
		List<Long> timeboundIds= new ArrayList<>();
		try {
			timeboundIds = (List<Long>) jdbcTemplate.query(sql,  new SingleColumnRowMapper(Long.class));
			
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
//		System.out.println("In getTimeboundIdsByProgramSemSubjectIds  timeboundIds:--  "  + timeboundIds);
		
		return timeboundIds;
	}
	

	/*
	public ArrayList<String>  getconsumerProgramStructureIdsWithSubject(String programId,String programStructureId, String consumerTypeId, String subject){
		jdbcTemplate = new JdbcTemplate(baseDataSource);
		
		String sql =  "SELECT c_p_s.id FROM exam.consumer_program_structure as c_p_s, "
				+ "exam.program_sem_subject as p_s_s "
				+ "where c_p_s.programId in ("+ programId +") "
				+ "and c_p_s.programStructureId in ("+ programStructureId +") "
				+ "and c_p_s.consumerTypeId in ("+ consumerTypeId +") "
				+ "and c_p_s.id = p_s_s.consumerProgramStructureId "
				+ "and p_s_s.subject=?";

		ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
				sql, new Object[] {subject},  new SingleColumnRowMapper(
						String.class));
		
		
		return consumerProgramStructureIds;
	}
	*/
	
}
