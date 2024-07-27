package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

@Repository("studentCourseMappingDao")
public class StudentCourseMappingDao 
{
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	static final Map<String, String> specializationType = ImmutableMap.of(
		    "Finance", "16",
		  "Marketing" , "17"
		);
	
	@Transactional(readOnly = true)
	public String getStudentMasterkey(String sapid) {
			String sql = "SELECT consumerProgramStructureId FROM exam.students  where sapid =:sapid ";
			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
		    queryParams.addValue("sapid", sapid);
		    return  namedParameterJdbcTemplate.queryForObject(sql,queryParams, String.class);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> checkAnyOtherElectiveInTable(String sapid,String masterkey,String sem) {
		
			StringBuffer sql = new StringBuffer( "	SELECT scm.id FROM exam.mdm_subjectcode_mapping scm	" + 
						"	INNER JOIN	" + 
						"	exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id 	" + 
						"	INNER JOIN	" + 
						"	exam.student_current_subject scs ON scm.id = scs.programSemSubjectId	" + 
						"	WHERE scm.consumerProgramStructureId = ? AND scm.sem = ? AND sc.specializationType IS NOT NULL AND scs.sapid = ? ;");
			
			ArrayList<String>  student = (ArrayList<String> ) jdbcTemplate.query(sql.toString(), new Object[]{masterkey, sem,sapid}, new SingleColumnRowMapper(String.class));
			return student;
	}
	
	
	public void deleteAnyOtherElectiveInTable(final String sapid,ArrayList<String> pssId) {
		
		String sql = "Delete from exam.student_current_subject where sapid =:sapid and programSemSubjectId in (:programSemSubjectId) "; 
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
	    queryParams.addValue("sapid", sapid);
	    queryParams.addValue("programSemSubjectId", pssId);
		  
	    namedParameterJdbcTemplate.update(sql,queryParams);
	}
	
	public  HashMap<String,String> getsubjectsWithPssId(String subjectElectiveType,String masterKey,String sem) {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select msm.id  AS programSemSubjectId,sc.subjectname As Subject "
				   + " from exam.mdm_subjectcode sc  " 
				   + " inner join   "  
				   + " exam.mdm_subjectcode_mapping msm on sc.id = msm.subjectCodeId "
				   + " where   sc.specializationType  =:subjectElectiveType AND "
				   + "  msm.consumerProgramStructureId = :masterKey AND sem =:sem  AND sc.active = 'Y' ");
	
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
	    queryParams.addValue("masterKey", masterKey);
	    queryParams.addValue("subjectElectiveType", specializationType.get(subjectElectiveType));
	    queryParams.addValue("sem", sem);
	   
	    HashMap<String,String>	programSemSubjectIdList = namedParameterJdbcTemplate.query(sql.toString(),queryParams, new ResultSetExtractor<HashMap>(){
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,String> mapRet= new HashMap<String,String>();
		        while(rs.next()){
		            mapRet.put(rs.getString("programSemSubjectId"),rs.getString("Subject"));
		        }
		        return mapRet;
		    }
		});
	    return programSemSubjectIdList;
	}

	@Transactional(readOnly = false)
	public int batchInsertStudentPssIdsMappings(final HashMap<String,String> subjectList,final String sapid,final String month,final String year) {
		StringBuffer sql = new StringBuffer( "INSERT INTO  exam.student_current_subject  " + 
				"( sapid, programSemSubjectId, subject,year,month) " + 
				" VALUES " + 
				"( ?, ?, ?,?,?) ");
		
		final List< Entry<String,String> > list= getMapAsEntryList(subjectList);
		 
		int[] result = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter(){
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
	
}
