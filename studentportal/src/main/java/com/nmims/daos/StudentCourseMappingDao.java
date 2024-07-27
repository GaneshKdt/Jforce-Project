package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

@Repository("studentCourseMappingDao")
public class StudentCourseMappingDao 
{
	private PartnerConnection connection;

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value("#{'${TIMEBOUND_PORTAL_LIST}'.split(',')}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	final String[] electiveMasterkeys = {"128"};
	
	final String[] electiveSem = {"5","6"};
	
	final int lateralSemLimit = 1;
	
	static final Map<String, String> specializationType = ImmutableMap.of(
		    "Finance", "16",
		  "Marketing" , "17"
		);
	
	public StudentCourseMappingDao () {
		this.connection = SFConnection.getConnection();
	}
	
	public void init(){
		this.connection = SFConnection.getConnection();
	}
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private List<String> MBAX_PORTAL_LIST = Arrays.asList("119", "126");
	
	@Transactional(readOnly = true)
	public ArrayList<String> getCurrentCycleSubjectlist(String consumerProgramStructureId, String sem,String sapid,boolean sfdccall) throws Exception
	{
		StringBuffer elective = new StringBuffer();
		//For Elective Subjects
		if(Arrays.asList(electiveMasterkeys).contains(consumerProgramStructureId) && Arrays.asList(electiveSem).contains(sem)) {
		
			elective.append(" AND (sc.specializationType is null ");
			
			if(sfdccall) {
				String electiveType = getElectiveSubjectListFromSFDC(sapid,sem);
				if(electiveType.length() > 0)
					elective.append("  OR sc.specializationType = "+specializationType.get(electiveType));
			}
			elective.append(" )");
		}

		StringBuffer sql = new StringBuffer(" SELECT subjectname FROM exam.mdm_subjectcode sc " + 
					" INNER JOIN exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectcodeId " + 
					" WHERE scm.consumerProgramStructureId = ? AND scm.sem = ? " + 
					elective);
		
		ArrayList<String> pssIdList = (ArrayList<String>)jdbcTemplate.query(sql.toString(), new Object[] {consumerProgramStructureId, sem}, new SingleColumnRowMapper(String.class));
		
		return pssIdList;
	}

	@Transactional(readOnly = false)
	public int batchInsertStudentPssIdsMappings(final HashMap<String,String> subjectList,String sapid,String month,String year) {
		String sql = "INSERT INTO  exam.student_current_subject  ( sapid, programSemSubjectId, subject,year,month)  VALUES ( ?, ?, ?,?,?) ";
		
		final List< Entry<String,String> > list= getMapAsEntryList(subjectList);
		 
		int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException,DuplicateKeyException {
				
					Entry<String,String> entry = list.get(i);
					ps.setString(1, sapid);
					ps.setString(2, entry.getKey());
					ps.setString(3, entry.getValue());
					ps.setString(4,year);
					ps.setString(5, month);
				}
				public int getBatchSize() {
					return subjectList.size();
				}
			});
		return result.length;
	}
	
	private List<Entry<String,String>> getMapAsEntryList(Map<String, String> client) {
        List<Entry<String,String>> result=new ArrayList<Entry<String,String>>();
         for(Entry<String,String> entry : client.entrySet())
         {
             result.add(entry);
         }  
        return result;
    }
	
	@Transactional(readOnly = true)
	public ArrayList<String> getNotPassedSubjectsBasedOnSapid(String sapid,String sem,String masterkey){
		
		StringBuffer sql = new StringBuffer("select ps.subject from exam.registration er,exam.program_subject ps, exam.students s "
				+" where er.sapid =:sapid " 
				+" and s.sapid = er.sapid "
				+" and er.program = ps.program "
				+" and er.sem = ps.sem "
				+" and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+" and ps.subject not in (select subject from exam.passfail where sapid =:sapid) ");
		
		if(Arrays.asList(electiveMasterkeys).contains(masterkey) && Arrays.asList(electiveSem).contains(sem)) //For Elective
			sql.append(" and er.sem < :sem  ");
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
	    queryParams.addValue("sapid", sapid);
	    queryParams.addValue("sapid", sapid);
	    queryParams.addValue("sem", sem);
	  
	    return (ArrayList<String>)namedParameterJdbcTemplate.query(sql.toString(),queryParams, new SingleColumnRowMapper(String.class));
	}

	@Transactional(readOnly = true)
	public int  getSemesterCountBySapid(String sapid) {
			String sql = "SELECT count(*) FROM exam.registration  where sapid = ? and sem > "+lateralSemLimit;
			return (int)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public int  checkSapidInCourseTable(String sapid,String month,String year) {
			String sql = "SELECT count(*) FROM exam.student_current_subject  where sapid = ? and year = ? and month = ? ";
			return (int)jdbcTemplate.queryForObject(sql, new Object[]{sapid,year,month}, Integer.class);
	}

	@Transactional(readOnly = true)
	public List<String> getListOfStudents(String month,String year) {

			StringBuffer sql = new StringBuffer("SELECT sapid FROM exam.registration where  month =:month AND year =:year  AND consumerProgramStructureId not in (:consumerProgramStructureId) AND  consumerProgramStructureId not in (:upgrad_portal_list) ");
			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
		    queryParams.addValue("consumerProgramStructureId", TIMEBOUND_PORTAL_LIST);
		    queryParams.addValue("month",month);
		    queryParams.addValue("year",year );
		    queryParams.addValue("upgrad_portal_list",MBAX_PORTAL_LIST );
		    
		    List<String>  studentList = (ArrayList<String>) namedParameterJdbcTemplate.query(sql.toString(), queryParams,
		    		 new SingleColumnRowMapper(String.class));
		   
		    return studentList;			
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPSSIds(String sapid,String year,String month) {
	
		String sql =" SELECT programSemSubjectId FROM exam.student_current_subject   WHERE sapid =:sapid and  year =:year and month =:month   ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);
		
		ArrayList<String> pssIdList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		return pssIdList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getPSSID(String sapid,String year,String month) {
	
		String sql =" SELECT programSemSubjectId FROM exam.student_current_subject   WHERE sapid =:sapid and  year =:year and month =:month   ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);
		
		ArrayList<Integer> pssIdList = (ArrayList<Integer>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(Integer.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getCurrentCycleSubjects(String sapid,String year,String month) {
		
		String sql =" SELECT subject FROM exam.student_current_subject  WHERE sapid =:sapid and year =:year and month =:month  ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);

		ArrayList<String> pssIdList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,String> getProgramSemSubjectId(String sapid,String year,String month) {
			
		StringBuilder sql = new StringBuilder();
		
		sql.append("select programSemSubjectId, subject  from exam.student_current_subject   " 
				   + " where sapid =:sapid and year =:year and month =:month ;");
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("sapid", sapid);
		queryParams.addValue("year", year);
		queryParams.addValue("month", month);
		
		HashMap<String,String>	programSemSubjectIdList = namedParameterJdbcTemplate.query(sql.toString(),queryParams, new ResultSetExtractor<HashMap>(){
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,String> mapRet= new HashMap<String,String>();
		        while(rs.next()){
		            mapRet.put(rs.getString("programSemSubjectId"),rs.getString("subject"));
		        }
		        return mapRet;
		    }
		});
		
		return programSemSubjectIdList;
	}
	
	public String getElectiveSubjectListFromSFDC(String sapid,String sem) throws Exception
	{
			String  electiveSubject = "";
			StringBuffer sfdcQuery = new StringBuffer(" SELECT Elective_Subject_Type__c FROM Opportunity where Account.nm_StudentNo__c = '" +sapid+"' and nm_Semester__c = '"+sem+"' AND  Elective_Subject_Name__c  <> null ");
			QueryResult qResult = new QueryResult();
			qResult = connection.query(sfdcQuery.toString());

			if (qResult.getSize() > 0) {
		
				SObject[] records = qResult.getRecords();
				SObject s = (SObject) records[0];
				electiveSubject = (String) s.getField("Elective_Subject_Type__c");		
			}
	
		return electiveSubject;
	}
	
	public int deleteCourseOfStudent(String sapid,String year,String month) {
		String sql = "Delete from exam.student_current_subject where sapid =:sapid and year =:year and month =:month "; 
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
	    queryParams.addValue("sapid", sapid);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month",month);
	    
	    return namedParameterJdbcTemplate.update(sql,queryParams);
	}
	
	public List<Integer> getSubjectCodeIdByPssId(Set<String> pssId) {
		
		MapSqlParameterSource paramSource= new MapSqlParameterSource();	
		paramSource.addValue("pssId", pssId);
		return namedParameterJdbcTemplate.queryForList(" SELECT subjectCodeId FROM exam.mdm_subjectcode_mapping WHERE id IN (:pssId) ",paramSource,Integer.class);
	}
}
