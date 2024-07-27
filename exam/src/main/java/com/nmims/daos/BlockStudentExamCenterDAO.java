package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.BlockStudentExamCenterBean;

@Repository("blockCenterDAO")
public class BlockStudentExamCenterDAO extends BaseDAO
{
	/*Variable*/
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	
	/*Methods*/
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	
	/*Dao Mathods*/
	
	//To getting all examCenterId in list from exam.examcenter_slot_mapping table
	@Transactional(readOnly = true)
	public ArrayList<String> getAllExamCenterSlotMappingId()
	{
		jdbcTemplate = new JdbcTemplate(dataSource);

		String query = "Select examcenterId as centerId From exam.examcenter_slot_mapping";

		ArrayList<String> examCenterIdList =(ArrayList<String>) jdbcTemplate.query(query, new SingleColumnRowMapper<String>(String.class));
		
		return examCenterIdList;
	}
	
	//To get All Current Exam Center List Except Null CenterName
	@Transactional(readOnly = true)
	public ArrayList<BlockStudentExamCenterBean> getAllExamCenterByCenterId(ArrayList<String> examcenterIdList) 
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String query = "Select centerId, examCenterName as centerName "
				+ "From exam.examcenter "
				+ "Where centerId in (:centerIdList) "
				+ "order by city asc";
		
		queryParams.addValue("centerIdList", examcenterIdList);
		
		ArrayList<BlockStudentExamCenterBean> examCentersList =(ArrayList<BlockStudentExamCenterBean>) namedJdbcTemplate.query(query, queryParams, new BeanPropertyRowMapper<BlockStudentExamCenterBean>(BlockStudentExamCenterBean.class));
		
		return examCentersList;
	}
	
	//To Block the centers in bulk for students
	@Transactional(readOnly = false)
	public int blockCentersInBulk(ArrayList<BlockStudentExamCenterBean> blockCenterList)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String sql = "Insert Into exam.student_blocked_centers"
				+ "(sapid,"
				+ "year,"
				+ "month,"
				+ "centerId) "
				+ "Values(?,?,?,?)";
		int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				BlockStudentExamCenterBean bean = blockCenterList.get(i);
				ps.setString(1, bean.getSapid());
				ps.setString(2, bean.getYear());
				ps.setString(3, bean.getMonth());
				ps.setString(4, bean.getCenterId());
			}
			public int getBatchSize() {
				return blockCenterList.size();
			}
		});
		return batchUpdate.length;
	}
	
	//To Fetch Students By master key From exam.students table
	@Transactional(readOnly = true)
	public ArrayList<BlockStudentExamCenterBean> getStudentsByMasterKey(String masterKeyId)
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String query = "Select sapid"
				+ " From exam.students Where consumerProgramStructureId=:masterKey";
		queryParams.addValue("masterKey", masterKeyId);
		ArrayList<BlockStudentExamCenterBean> studentList =(ArrayList<BlockStudentExamCenterBean>) namedJdbcTemplate.query(query, queryParams, new BeanPropertyRowMapper<BlockStudentExamCenterBean>(BlockStudentExamCenterBean.class));
		return studentList;
	}
	
	//To get Program Structure Name By Program Structure Id
	@Transactional(readOnly = true)
	public String getProgramStructureNameByProgramStructureId(String programStructureTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String name = null;
		String sql =  "select program_structure as programStructureName"
				+ " from exam.program_structure"
				+ " Where id = ?";
			name =  jdbcTemplate.queryForObject(sql, new Object[] {programStructureTypeId}, new SingleColumnRowMapper<>());
			return name; 
	}
	
	//To get Program Code By Program Id
	@Transactional(readOnly = true)
	public String getProgramNameByProgramId(String programId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String name = null;
		String sql =  "select code as programName"
				+ " from exam.program"
				+ " Where id = ?";
			name =  jdbcTemplate.queryForObject(sql, new Object[] {programId}, new SingleColumnRowMapper<>());
			return name; 
	}
	
	//To get Consumer Type Name By Consumer Type Id
	@Transactional(readOnly = true)
	public String getConsumerTypeNameByConsumerTypeId(String consumerTypeName){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String name = null;
		String sql =  "select name as consumerTypeName"
				+ " from exam.consumer_type"
				+ " Where id = ?";
			name =  jdbcTemplate.queryForObject(sql, new Object[] {consumerTypeName}, new SingleColumnRowMapper<>());
			return name; 
	}
	
	//To get All NV Students sapid list
	@Transactional(readOnly = true)
	public ArrayList<String> getNVStudentsSapid()
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "Select sapid From exam.marks Where writenscore='NV'";
		ArrayList<String> allNVStudentsList = (ArrayList<String>) jdbcTemplate.query(query, new SingleColumnRowMapper<String>(String.class));
		return allNVStudentsList;
	}
	
	//To get All marks detail of students by sapid
	@Transactional(readOnly = true)
	public ArrayList<BlockStudentExamCenterBean> getMarksDetailOfSapid(ArrayList<String> nvSapids)
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String query = "Select sapid,examorder,writenscore From exam.marks Where sapid in (:sapid)";
		queryParams.addValue("sapid", nvSapids);
		ArrayList<BlockStudentExamCenterBean> allStudentsMarksListBySapid =(ArrayList<BlockStudentExamCenterBean>) namedJdbcTemplate.query(query, queryParams, new BeanPropertyRowMapper<BlockStudentExamCenterBean>(BlockStudentExamCenterBean.class));
		return allStudentsMarksListBySapid;
	}
	
	//To Fetch Blocked Center Student By Year and Month or Sapid
	@Transactional(readOnly = true)
	public ArrayList<BlockStudentExamCenterBean> getBlockedCenterStudents(BlockStudentExamCenterBean searchBean)
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String query = "Select * From exam.student_blocked_centers Where year=:year and month=:month";
		queryParams.addValue("year", searchBean.getYear());
		queryParams.addValue("month", searchBean.getMonth());
		if(searchBean.getSapid() != null && !"".equals(searchBean.getSapid()))
		{
			query += " and sapid=:sapid";
			queryParams.addValue("sapid", searchBean.getSapid());
		}
		ArrayList<BlockStudentExamCenterBean> blockedCenterStudentsList =(ArrayList<BlockStudentExamCenterBean>) namedJdbcTemplate.query(query, queryParams, new BeanPropertyRowMapper<BlockStudentExamCenterBean>(BlockStudentExamCenterBean.class));
		return blockedCenterStudentsList;
	}
	
	//To Unblock Center Of Student
	@Transactional(readOnly = false)
	public int unblockCenterOfStudent(BlockStudentExamCenterBean searchBean)
	{
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String query = "Delete From exam.student_blocked_centers Where year=:year and month=:month and sapid=:sapid and centerId=:centerId";
		queryParams.addValue("year", searchBean.getYear());
		queryParams.addValue("month", searchBean.getMonth());
		queryParams.addValue("sapid", searchBean.getSapid());
		queryParams.addValue("centerId", searchBean.getCenterId());
		int count = namedJdbcTemplate.update(query, queryParams);
		return count;
	}
	
	//To Fetch Master key Id of Students By ConsumerTypeId, ProgramStructureId and ProgramId
	@Transactional(readOnly = true)
	public String getMasterKeyByConsumerTypeAndProgramStructureAndProgram(
			String consumerTypeId, String programStructureId, String programId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "Select id From exam.consumer_program_structure Where programId=? and programStructureId=? and consumerTypeId=?";
		String id = jdbcTemplate.queryForObject(query, new Object[] {programId,programStructureId,consumerTypeId},new SingleColumnRowMapper<String>(String.class));
		return id;
	}
}
