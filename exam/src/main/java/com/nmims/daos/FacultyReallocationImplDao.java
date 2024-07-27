package com.nmims.daos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.FacultyReallocationBean;

@Repository
public class FacultyReallocationImplDao implements FacultyReallocationDao
{
	/*Variables*/
	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	
	
	/*Dao Methods*/
	//This Dao are use to fetch all active Faculty List with their name and facultyId
	@Transactional(readOnly = true)
	@Override
	public ArrayList<FacultyReallocationBean> getAllFacultyWithNameAndId() throws SQLException
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "Select firstname,lastname,facultyId From acads.faculty Where active = 'Y' Order By firstName, lastName asc";
		ArrayList<FacultyReallocationBean> facultyList =(ArrayList<FacultyReallocationBean>) jdbcTemplate.query(query, new BeanPropertyRowMapper<FacultyReallocationBean>(FacultyReallocationBean.class));
		return facultyList;
	}
	
	//This Dao are use to fetch all Allocated Projects count for Faculty
	@Transactional(readOnly = true)
	@Override
	public ArrayList<FacultyReallocationBean> getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws SQLException
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		String query = "Select facultyId,count(*) As projectsAllocated From exam.projectsubmission "
						+ "Where facultyId Is Not Null and year = :year and month = :month";
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		
		if(!"".equals(facultyId.trim()))
		{
			query = query + " and facultyId = :facultyId";
			paramSource.addValue("facultyId", facultyId);
		}
		
		query = query + " Group By facultyId";
		ArrayList<FacultyReallocationBean> searchedFacultyList =(ArrayList<FacultyReallocationBean>) namedJdbcTemplate.query(query,paramSource, new BeanPropertyRowMapper<FacultyReallocationBean>(FacultyReallocationBean.class));
		return searchedFacultyList;
	}
	
	//This Dao are used to fetch the Not Evaluated Projects count for faculty
	@Transactional(readOnly = false)
	@Override
	public HashMap<String,FacultyReallocationBean> getProjectsNotEvaluatedToFacultyByYearAndMonthOrFacultyId(String year, String month, String facultyId)throws SQLException
	{
		HashMap<String,FacultyReallocationBean> map = new HashMap<>();
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		
		String query = "Select facultyId,count(*) As yetEvaluated From exam.projectsubmission "
						+ "Where facultyId Is Not Null and evaluated = 'N' and year = :year and month = :month";
		
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		
		if(!"".equals(facultyId.trim()))
		{
			query = query + " and facultyId = :facultyId";
			paramSource.addValue("facultyId", facultyId);
		}
		
		query = query + " Group By facultyId";
		ArrayList<FacultyReallocationBean> searchedFacultyList =(ArrayList<FacultyReallocationBean>) namedJdbcTemplate.query(query,paramSource, new BeanPropertyRowMapper<FacultyReallocationBean>(FacultyReallocationBean.class));
		for(FacultyReallocationBean bean : searchedFacultyList)
		{
			map.put(bean.getFacultyId(), bean);
		}
		return map;
	}
	
	//This Dao are use to update the simple facultyId So that projects are reallocate simply to selected faculty
	@Transactional(readOnly = false)
	@Override
	public int reallocateProjectsToFaculty(String fromFacultyId, String toFacultyId, 
			String year, String month, List<String> sapids, String user)throws Exception 
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		
		String query = ""
				+ "Update exam.projectsubmission Set facultyId =:toFacultyId, "
				+ "lastModifiedBy =:user, "
				+ "lastModifiedDate = sysdate() "
				+ "Where evaluated = 'N' "
				+ "And facultyId =:fromFacultyId "
				+ "And year =:year "
				+ "And month =:month "
				+ "And sapid in (:sapids) ";
		
		paramSource.addValue("toFacultyId", toFacultyId);
		paramSource.addValue("user", user);
		paramSource.addValue("fromFacultyId", fromFacultyId);
		paramSource.addValue("year", year);
		paramSource.addValue("month", month);
		paramSource.addValue("sapids", sapids);
			
		int realloactedCount = namedJdbcTemplate.update(query, paramSource);
		
		return realloactedCount;
	}
	
	//This dao are use to fetch all sapid list whose project assigned to given facultyId and it still not evaluated
	@Transactional(readOnly = true)
	@Override
	public List<String> getSapidsByFacultyIdAndYearAndMonth(String facultyId, String year, String month)throws Exception
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "Select sapid From exam.projectsubmission Where evaluated = 'N' And facultyId = ? And year = ? And month = ?";
		List<String> sapidList = jdbcTemplate.query(query, new Object[] {facultyId,year,month},new SingleColumnRowMapper<String>());
		return sapidList;
	}
	
	//This dao are use to fetch all master keys and Program structure of given sapid's list
	@Transactional(readOnly = true)
	@Override
	public List<FacultyReallocationBean> getMasterKeysDetailList(List<String> sapids)throws Exception
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		String query = "Select sapid,consumerProgramStructureId,PrgmStructApplicable As programStructure From exam.students Where sapid in (:sapids)";
		paramSource.addValue("sapids", sapids);
		List<FacultyReallocationBean> masterKeysDetailList = namedJdbcTemplate.query(query, paramSource, new BeanPropertyRowMapper<FacultyReallocationBean>(FacultyReallocationBean.class));
		return masterKeysDetailList;
	}
	
	//This dao are use to fetch all programs details
	@Transactional(readOnly = true)
	@Override
	public List<FacultyReallocationBean> getAllProgramDetails()throws Exception
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "Select program As programCode,programname As programName,consumerProgramStructureId,programStructure From exam.programs";
		List<FacultyReallocationBean> programsDeatilList = jdbcTemplate.query(query, new BeanPropertyRowMapper<FacultyReallocationBean>(FacultyReallocationBean.class));
		return programsDeatilList;
	}
}