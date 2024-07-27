package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentExamBean;

@Repository("sifyDAO")
public class SifyDAO extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	long primaryKey;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> addData(final ArrayList<SifyMarksBean> beanList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < beanList.size(); i++) {
			try{
	   			SifyMarksBean bean = beanList.get(i);
	   			long key= saveData(bean);
	   			if(key==0) {
	   				errorList.add(i+"");
	   			}
	   		}catch(Exception e){
	   			
	   			errorList.add(i+"");
	   		}
		}
		return errorList;

	}
	
	/*MCode
	 * 
	 * public long saveData(final SifyMarksBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStmt = con.prepareStatement("INSERT INTO exam.sify_marks_table (sapId, name, examCode, "
								+ "year , month ,subjectCode, examDate, " + 
								"	               centerCode, questionPaperNumber , " + 
								"	               sectionOneMarks, sectionOneNegativeMarks, sectionOneCorrectAnswer, " + 
								"	               sectionOneWrongAnswer, sectionOneAttempt, sectionOneNotAttempt ," + 
								"	               sectionTwoMarks, sectionTwoNegativeMarks, sectionTwoCorrectAnswer, " + 
								"	               sectionTwoWrongAnswer, sectionTwoAttempt, sectionTwoNotAttempt ," + 
								"	               sectionThreeMarks, sectionThreeNegativeMarks, sectionThreeCorrectAnswer, " + 
								"	               sectionThreeWrongAnswer, sectionThreeAttempt, sectionThreeNotAttempt ," + 
								"	               accessToken,subject) " + 
								"	               VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'Business Economics') ", Statement.RETURN_GENERATED_KEYS);
						   preparedStmt.setString (1, bean.getSapid());
				           preparedStmt.setString (2, bean.getName());
				           preparedStmt.setString(3, bean.getExamCode());
				           preparedStmt.setString(4, bean.getYear());
				           preparedStmt.setString(5, bean.getMonth());
				           preparedStmt.setString(6, bean.getSubjectCode());
				           preparedStmt.setString(7,bean.getExamDate());
				           preparedStmt.setString(8,bean.getCenterCode());
				           preparedStmt.setInt(9, bean.getQuestionPaperNumber());
				           preparedStmt.setDouble(10, bean.getSectionOneMarks());
				           preparedStmt.setDouble(11, bean.getSectionOneNegativeMarks());
				           preparedStmt.setInt(12, bean.getSectionOneCorrectAnswer());
				           preparedStmt.setInt(13, bean.getSectionOneWrongAnswer());
				           preparedStmt.setInt(14, bean.getSectionOneAttempt());
				           preparedStmt.setInt(15, bean.getSectionOneNotAttempt());
				           preparedStmt.setDouble(16, bean.getSectionTwoMarks());
				           preparedStmt.setDouble(17, bean.getSectionTwoNegativeMarks());
				           preparedStmt.setInt(18, bean.getSectionTwoCorrectAnswer());
				           preparedStmt.setInt(19, bean.getSectionTwoWrongAnswer());
				           preparedStmt.setInt(20, bean.getSectionTwoAttempt());
				           preparedStmt.setInt(21, bean.getSectionTwoNotAttempt());
				           preparedStmt.setDouble(22, bean.getSectionThreeMarks());
				           preparedStmt.setDouble(23, bean.getSectionThreeNegativeMarks());
				           preparedStmt.setInt(24, bean.getSectionThreeCorrectAnswer());
				           preparedStmt.setInt(25, bean.getSectionThreeWrongAnswer());
				           preparedStmt.setInt(26, bean.getSectionThreeAttempt());
				           preparedStmt.setInt(27, bean.getSectionThreeNotAttempt());
				           preparedStmt.setDouble(28, bean.getAccessToken()); 
				           preparedStmt.setString(29, bean.getSubject());
			        return preparedStmt;
			    }
			}, holder);

			primaryKey = holder.getKey().longValue();
			return primaryKey;
		} catch (DataAccessException e) {
			
			return 0;
		}
	}*/
	
	//Added on 7/7/2018  ----start----
	public long saveData(final SifyMarksBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStmt = con.prepareStatement("INSERT INTO exam.sify_marks_table "
								+ "(sapId, "
								+ " name, "
								+ " examCode, "
								+ " year , "
								+ " month ,"
								+ " subjectCode, "
								+ " examDate, "
								+ " sectionOneMarks, "
								+ " sectionTwoMarks, "
								+ " sectionThreeMarks, "
								+ " sectionFourMarks, "
								+ " totalScore,"
								+ " studentType,"
								+ " subject)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) on "
								+ " duplicate key update "
								+ " subjectCode = ? ,"
								+ "	 examCode =? , " 
								+ " examDate = ?, "
								+ " sectionOneMarks = ?, "
								+ " sectionTwoMarks = ?, "
								+ " sectionThreeMarks =?, "
								+ " sectionFourMarks =?, "
								+ " totalScore = ?  ", Statement.RETURN_GENERATED_KEYS);
						   preparedStmt.setString (1, bean.getSapid());
				           preparedStmt.setString (2, bean.getName());
				           preparedStmt.setString(3, bean.getExamCode());
				           preparedStmt.setString(4, bean.getYear());
				           preparedStmt.setString(5, bean.getMonth());
				           preparedStmt.setInt(6, bean.getSubjectCode());
				           preparedStmt.setString(7,bean.getExamDate());
				           preparedStmt.setDouble(8, bean.getSectionOneMarks());
				           preparedStmt.setDouble(9, bean.getSectionTwoMarks());
				           preparedStmt.setDouble(10, bean.getSectionThreeMarks());
				           preparedStmt.setDouble(11, bean.getSectionFourMarks());
				           preparedStmt.setDouble(12, bean.getTotalScore());
				           preparedStmt.setString(13, bean.getStudentType());
				           preparedStmt.setString(14, bean.getSubject());
				           preparedStmt.setInt(15, bean.getSubjectCode());
				           preparedStmt.setString(16, bean.getExamCode());
				           preparedStmt.setString(17, bean.getExamDate());
				           preparedStmt.setDouble(18, bean.getSectionOneMarks());
				           preparedStmt.setDouble(19, bean.getSectionTwoMarks());
				           preparedStmt.setDouble(20, bean.getSectionThreeMarks());
				           preparedStmt.setDouble(21, bean.getSectionFourMarks());
				           preparedStmt.setDouble(22, bean.getTotalScore());
			        return preparedStmt;
			    }
			}, holder);

			primaryKey = holder.getKey().longValue();
			return primaryKey;
		} catch (DataAccessException e) {
			
			return 0;
		}
	}
	
	@Transactional(readOnly = true)
	public List<SifyMarksBean> getTotalDataSummary(String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SifyMarksBean> sifyMarksListSummary=null;
		String sql="select distinct count(*) as subjectCount,year,month,subject,subjectCode "
				+ " from exam.sify_marks_table s"
				+ " where s.examCode=? "
				+ " and s.month=? ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		parameters.add(year);
		parameters.add(month);
		
		sql+="group by subjectCode";		
		try {
			Object[] args = parameters.toArray();
			sifyMarksListSummary = (List<SifyMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SifyMarksBean.class));
		} catch (DataAccessException e) {
			
		}
		return sifyMarksListSummary;
	}
	//Added on 7/7/2018  ----end----
	
	@Transactional(readOnly = true)
	public List<SifyMarksBean> getData(String year,String month,String subject,int subjectCode,String studentType){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SifyMarksBean> sifyMarksList=null;
		String sql="select distinct * from exam.sify_marks_table s where s.examCode=? and s.month=? and studentType=?";
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		parameters.add(year);
		parameters.add(month);
		parameters.add(studentType);
		if(subject != null && !("".equals(subject))){
					sql+="and s.subject=? " ;
					parameters.add(subject);
				}
		if(!("".equals(subjectCode)) && subjectCode > 0){
					sql+="and s.subjectCode=? " ;
					parameters.add(subjectCode);
				}
				
		try {
			Object[] args = parameters.toArray();
			sifyMarksList = (List<SifyMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SifyMarksBean.class));
		} catch (DataAccessException e) {
			
		}
		return sifyMarksList;
	}
	
	@Transactional(readOnly = true)
	public List<SifyMarksBean> getDataSummary(String year,String month,String subject,int subjectCode,String studentType){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<SifyMarksBean> sifyMarksListSummary=null;
		String sql="select distinct count(*) as subjectCount,subject,subjectCode,year,month,studentType "
				+ " from exam.sify_marks_table s"
				+ " where s.examCode=? "
				+ " and s.month=?"
				+ " and s.studentType=? ";
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		parameters.add(year);
		parameters.add(month);
		parameters.add(studentType);
		if(subject != null && !("".equals(subject))){
					sql+="and s.subject=? " ;
					parameters.add(subject);
				}
		if(!("".equals(subjectCode)) && subjectCode > 0){
			sql+="and s.subjectCode=? " ;
			parameters.add(subjectCode);
		}
		sql+="group by subjectCode";		
		try {
			Object[] args = parameters.toArray();
			sifyMarksListSummary = (List<SifyMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SifyMarksBean.class));
		} catch (DataAccessException e) {
			
		}
		return sifyMarksListSummary;
	}
	//
	
	
	
	@Transactional(readOnly = true)
	public ArrayList<SifyMarksBean> dataFromSify(String year, String month, String studentType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SifyMarksBean> sifyMarksList=null;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql="select distinct * from exam.sify_marks_table s where s.year=? and s.month=? and s.studentType=?" ;
		try {
			parameters.add(year);
			parameters.add(month);
			parameters.add(studentType);
			Object[] args = parameters.toArray();
			sifyMarksList = (ArrayList<SifyMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(SifyMarksBean.class));
		} catch (DataAccessException e) {
			
		}
		return sifyMarksList;
	}
	
	
	
/*	public ArrayList<String> getUniqueSubjectCodes(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select distinct sifySubjectCode from exam.program_subject s where active = 'Y' and sifySubjectCode <> '0'" ;
		ArrayList<String> subjectCode = null;
		try {
			subjectCode = (ArrayList<String>) jdbcTemplate.query(sql, new Object[] {}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			
		}
		return subjectCode;
	}*/
	
	@Transactional(readOnly = true)
	public HashMap<Integer,String> getUniqueSubjectCodes(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select distinct sifySubjectCode, subject from exam.program_subject s where active = 'Y' and sifySubjectCode <> '0'" ;
		ArrayList<ProgramSubjectMappingExamBean> subjectCode = null;
		HashMap<Integer,String> subjectCodeMap = new HashMap<Integer,String>();
		try {
			subjectCode = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[] {},new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
			for(ProgramSubjectMappingExamBean ps : subjectCode){
				if(!subjectCodeMap.containsKey(ps.getSifySubjectCode())){
					subjectCodeMap.put(ps.getSifySubjectCode(), ps.getSubject());
				}
			}
		} catch (Exception e) {
			
		}
		return subjectCodeMap;
	}
	
	
	/*public HashMap<Integer,ProgramSubjectMappingBean> getUniqueSubjectCodeProgramMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select * from program_subject" ;
		ArrayList<ProgramSubjectMappingBean> ProgramList = null;
		HashMap<Integer,ProgramSubjectMappingBean> ProgramMap = new HashMap<Integer,ProgramSubjectMappingBean>();
		try {
			ProgramList = (ArrayList<ProgramSubjectMappingBean>) jdbcTemplate.query(sql, new Object[] {},new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
			for(ProgramSubjectMappingBean bean : ProgramList){
				if(!ProgramMap.containsKey(bean.getSifySubjectCode())){
					ProgramMap.put(bean.getSifySubjectCode(), bean);
				}
			}
		} catch (Exception e) {
			
		}
		return ProgramMap;
	}*/
	
	@Transactional(readOnly = true)
	public HashMap<String,Integer> getMissingSubjectCodes(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select * from exam.program_subject s where active = 'Y' and sifySubjectCode = '0' and subject NOT IN ('Project', 'Module 4 - Project')and subject not in (select ps.subject from program_subject ps where program = 'EPBM' and sifySubjectCode = '0')" ;
		ArrayList<ProgramSubjectMappingExamBean> subjectCode = null;
		HashMap<String,Integer> subjectCodeMissingMap = new HashMap<String,Integer>();
		try {
			subjectCode = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[] {},new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
			for(ProgramSubjectMappingExamBean ps : subjectCode){
				String key = ps.getProgram()+"|"+ps.getPrgmStructApplicable()+"|"+ps.getSubject();
				if(!subjectCodeMissingMap.containsKey(key)){
					subjectCodeMissingMap.put(key, ps.getSifySubjectCode());
				}
			}
		} catch (Exception e) {
			
		}
		return subjectCodeMissingMap;
	}
}
