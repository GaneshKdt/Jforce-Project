package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.AISHEUGCReportsBean;


@Repository


public class AISHEUGCReportsDaoImpl implements  AISHEUGCReportsDao {

	//private static final Logger logger = LoggerFactory.getLogger(AISHEUGCReportsDao.class);
	
	
	@Autowired
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
 
   @Transactional(readOnly = true)
	public List<String> getListOfSapidForAllStudentsAppearedByExamYearMonthSem(String year, String month, String sem, String firstLetterOfProgram)throws Exception {
		List<String> listOfSapidForAllStudentsAppeared = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct sapid from exam.marks where year = ? and month = ? and sem = ? and writenscore not in ('AB','') ";
		listOfSapidForAllStudentsAppeared = jdbcTemplate.queryForList(sql,new Object[] { year, month, sem }, String.class);
		return listOfSapidForAllStudentsAppeared; 
	}
   
   @Transactional(readOnly = true)
	public List<String> getListOfProgramByExamYearMonthSemFirstLetterofProgramForAllStudentsAppeared(String year, String month, String sem, String firstLetterOfProgram)throws Exception {
		List<String> listOfProgramByFirstLetterofProgramForAllStudentsAppeared = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct program from exam.marks  where year =  ?  and month = ? and sem =? and program like '"+firstLetterOfProgram+"%'";
		listOfProgramByFirstLetterofProgramForAllStudentsAppeared = jdbcTemplate.queryForList(sql,new Object[] { year, month, sem }, String.class);
		return listOfProgramByFirstLetterofProgramForAllStudentsAppeared;
		
	}
 
   @Transactional(readOnly = true)
	public List<AISHEUGCReportsBean> getListOfStudentAppeared(List<String>SapidList, List<String>ProgramList,String firstLetterOfProgram )throws Exception {
		List<AISHEUGCReportsBean> listOfStudentAppeared = new ArrayList<AISHEUGCReportsBean>();
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String sql = new StringBuilder("select program,sem,count(sapid) as total from exam.students where sapid in (:SapidList) and program in (:ProgramList) group by program").toString();

		queryParams.addValue("SapidList", SapidList);
		queryParams.addValue("ProgramList", ProgramList);
			listOfStudentAppeared = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql.toString(), queryParams,
				new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
		return listOfStudentAppeared;
	}

   @Transactional(readOnly = true)
  	public List<AISHEUGCReportsBean> getListOfFemaleStudentAppeared(List<String>SapidList,List<String>ProgramList ,String firstLetterOfProgram )throws Exception {
  		List<AISHEUGCReportsBean> listOfFemaleStudentAppeared = new ArrayList<AISHEUGCReportsBean>();
  		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  		MapSqlParameterSource queryParams = new MapSqlParameterSource();
  		String sql = new StringBuilder("select program,sem,count(sapid) as girlsTotal from exam.students where sapid in (:SapidList) and  program in (:ProgramList) and gender='Female' group by program").toString();

  		queryParams.addValue("SapidList", SapidList);
  		queryParams.addValue("ProgramList", ProgramList);
  			listOfFemaleStudentAppeared = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql.toString(), queryParams,
  				new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
  		return listOfFemaleStudentAppeared;
  	}
   
   @Transactional(readOnly = true)
				public List<String> getListOfSapidForTotalMarksByExamYearMonthSem(String year, String month, String sem, String firstLetterOfProgram)throws Exception {
					List<String> ListOfSapid = new ArrayList<String>();
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "select distinct sapid from exam.marks  where year =  ?  and month = ? and sem =? and writenscore not in ('AB','') and program like '"+ firstLetterOfProgram + "%' ";
					ListOfSapid = jdbcTemplate.queryForList(sql,new Object[] { year, month, sem }, String.class);
					return ListOfSapid;
					
				}
				
			
				@Transactional(readOnly = true)
				public List<String> getListOfSapidNYForTotalMarks(String resultProcessedYear, String resultProcessedMonth,
						String firstLetterOfProgram) throws Exception{
					List<String> ListOfSapidNY = new ArrayList<String>();
					jdbcTemplate = new JdbcTemplate(dataSource);
					
			String sql = "(  SELECT DISTINCT CONCAT(sapid,IF(SUM(isPass = 'N'), 'Y', 'N'),IF(SUM(writtenscore NOT IN ('AB' , '')),'Y','N')) FROM  exam.passfail WHERE resultProcessedYear =  ? and resultProcessedMonth = ? AND program LIKE '"+ firstLetterOfProgram +"%' GROUP BY sapid)";
		
					 ListOfSapidNY = jdbcTemplate.queryForList(sql, new Object[]{resultProcessedYear, resultProcessedMonth}, String.class);

					 return ListOfSapidNY;
				}
				
			
				@Transactional(readOnly = true)
				public List<String> getListOfSapidAndProgramForTotalMarks(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram ) throws Exception{
					List<String> ListOfSapidAndProgram = new ArrayList<String>();
					namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
					String sql = new StringBuilder(" SELECT  CONCAT(sapid, program)  FROM    exam.students  WHERE  CONCAT(sapid, 'N', 'Y') IN (:SapidNYlist  ) AND program LIKE '"+firstLetterOfProgram +"%'  "
							+ " AND sapid IN (:Sapid)").toString();

					queryParams.addValue("SapidNYlist", SapidNYlist);
					queryParams.addValue("Sapid", Sapid);
				
					ListOfSapidAndProgram = (ArrayList<String>) namedJdbcTemplate.query(sql.toString(), queryParams,
							new SingleColumnRowMapper(String.class));
					return ListOfSapidAndProgram;
				}
   
                   
				@Transactional(readOnly = true)
				public List<AISHEUGCReportsBean> getListOfTotalMarks(List<String>SapidProgramList  )throws Exception {
					List<AISHEUGCReportsBean> ListOfTotalMarks = new ArrayList<AISHEUGCReportsBean>();
					namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
					String sql = new StringBuilder("SELECT sapid, program, sem, SUM(total) AS totalMarks FROM exam.passfail  WHERE CONCAT(sapid, program) IN (:SapidProgramList) group by sapid,program,sem").toString();

					queryParams.addValue("SapidProgramList", SapidProgramList);
						ListOfTotalMarks = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql.toString(), queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
					return ListOfTotalMarks;
				}
   

				public List<String> getListOfSapidAndProgramForFemaleTotalmarks(List<String> SapidNYlist,
						List<String> Sapid, String firstLetterOfProgram) throws Exception {
					List<String> ListOfSapidAndProgram = new ArrayList<String>();
					namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
					String sql = new StringBuilder(
							" SELECT  CONCAT(sapid, program)  FROM    exam.students  WHERE gender = 'Female' and CONCAT(sapid, 'N', 'Y') IN (:SapidNYlist  ) AND program LIKE '"
									+ firstLetterOfProgram + "%'  " + " AND sapid IN (:Sapid)").toString();

					queryParams.addValue("SapidNYlist", SapidNYlist);
					queryParams.addValue("Sapid", Sapid);

					ListOfSapidAndProgram = (ArrayList<String>) namedJdbcTemplate.query(sql.toString(), queryParams,
							new SingleColumnRowMapper(String.class));
					return ListOfSapidAndProgram;
				}
				
				@Transactional(readOnly = true)
				public List<String> getListOfSapidForAllPassByExamYearMonthSem(String year, String month, String sem, String firstLetterOfProgram)throws Exception {
					List<String> ListOfSapidForAllPass = new ArrayList<String>();
					jdbcTemplate = new JdbcTemplate(dataSource);
					String sql = "select distinct sapid from exam.marks where year = ? and month = ? and sem = ? and writenscore not in ('AB','') " + 
							"and program like '"+firstLetterOfProgram +"%' ";
					ListOfSapidForAllPass = jdbcTemplate.queryForList(sql,new Object[] { year, month, sem }, String.class);
					return ListOfSapidForAllPass;
					
				}	
				@Transactional(readOnly = true)
				public List<String> getListOfSapidNYForAllPass(String resultProcessedYear, String resultProcessedMonth,String firstLetterOfProgram)throws Exception {
					List<String> ListOfSapidNYForAllPass = new ArrayList<String>();
					jdbcTemplate = new JdbcTemplate(dataSource);
					
			String sql = "( select distinct concat(sapid,IF(SUM(isPass = 'N'), 'Y', 'N'), IF(SUM(writtenscore not in ('AB','')), 'Y', 'N')) from exam.passfail " + 
					"where resultProcessedYear = ? and resultProcessedMonth = ? and program like '"+firstLetterOfProgram +"%'  group by sapid)";
		
				ListOfSapidNYForAllPass = jdbcTemplate.queryForList(sql, new Object[]{resultProcessedYear, resultProcessedMonth}, String.class);
					 return ListOfSapidNYForAllPass;
				}	
				
				
				public List<AISHEUGCReportsBean> getListOfStudentPass(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram ) throws Exception{
					List<AISHEUGCReportsBean> ListOfNoOfStudentPass = new ArrayList<AISHEUGCReportsBean>();
					namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
					String sql = new StringBuilder(" select program,sem,count(sapid) as totalPass from exam.students  where concat(sapid,'N','Y') in (:SapidNYlist) AND program LIKE '"+firstLetterOfProgram +"%'"
			+ " AND sapid IN (:Sapid) group by program").toString();

					queryParams.addValue("SapidNYlist", SapidNYlist);
					queryParams.addValue("Sapid", Sapid);
				
					ListOfNoOfStudentPass = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql.toString(), queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
					return ListOfNoOfStudentPass;
				}
				
				public List<AISHEUGCReportsBean> getListOfFemaleStudentPass(List<String> SapidNYlist,List<String> Sapid, String firstLetterOfProgram ) throws Exception{
					List<AISHEUGCReportsBean> ListOfNoOfFeamleStudentPass = new ArrayList<AISHEUGCReportsBean>();
					namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
					MapSqlParameterSource queryParams = new MapSqlParameterSource();
					String sql = new StringBuilder("select program,sem,count(sapid) as girlsPass from exam.students  where concat(sapid,'N','Y') in (:SapidNYlist) AND program LIKE '"+firstLetterOfProgram +"%'"
							+ " AND sapid IN (:Sapid)and gender='Female' group by program ").toString();

					queryParams.addValue("SapidNYlist", SapidNYlist);
					queryParams.addValue("Sapid", Sapid);
					
					
					ListOfNoOfFeamleStudentPass = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql.toString(), queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
					return ListOfNoOfFeamleStudentPass;
				}

   @Transactional(readOnly = true)
	public ArrayList<AISHEUGCReportsBean>  getSapidAndCpsidForAllStudents() throws Exception{
	   
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String sql = "SELECT sapid,consumerProgramStructureId FROM exam.students";
		 ArrayList<AISHEUGCReportsBean> AllStudentsDetails = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql,queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
		 
				 return AllStudentsDetails;
				 }

   @Transactional(readOnly = true)
  	public ArrayList<AISHEUGCReportsBean>  getProgramAndProgramId()throws Exception {
  	   
  		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  		MapSqlParameterSource queryParams = new MapSqlParameterSource();
  		String sql = "select id AS programId,code As Program  from exam.program"
  				+ " ";
  		 ArrayList<AISHEUGCReportsBean> ListOfProgram = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql,queryParams,
  							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
  				 return ListOfProgram;
  				 }
   
 
   @Transactional(readOnly = true)
	public ArrayList<AISHEUGCReportsBean>   getapplicableSubjectBySemAndconsumerProgramStructureId()throws Exception {
	   
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String sql = "select sem ,consumerProgramStructureId,COUNT(subject) AS noOfApplicablesubject from  exam.program_sem_subject group by sem,consumerProgramStructureId";
		 ArrayList<AISHEUGCReportsBean>  ListOfapplicableSubject = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql,queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
				 return ListOfapplicableSubject;
				 }
   

   @Transactional(readOnly = true)
	public ArrayList<AISHEUGCReportsBean>   getProgramIdAndconsumerProgramStructureId()throws Exception {
	   
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String sql = "select id as consumerProgramStructureId,programId FROM exam.consumer_program_structure ";
		 ArrayList<AISHEUGCReportsBean>  ListOfapplicableSubject = (ArrayList<AISHEUGCReportsBean>) namedJdbcTemplate.query(sql,queryParams,
							new BeanPropertyRowMapper(AISHEUGCReportsBean.class));
				 return ListOfapplicableSubject;
				 }
}