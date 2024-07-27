package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentsTestDetailsStudentPortalBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.beans.TestTypeStudentPortalBean;

@Repository("TestDao")
public class TestDAO  extends BaseDAO{
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
	
	public List<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		//String sql = "SELECT program FROM exam.programs order by program asc";
		String sql = "SELECT code FROM exam.program order by code asc";
		try {
			List<String> programList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return programList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
		
	}
	public List<String> getActiveSubjects() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select distinct subject from exam.program_subject where prgmStructApplicable = 'Jul2014' or "
				+ " prgmStructApplicable = 'Jul2009' or prgmStructApplicable = 'Jul2013' or prgmStructApplicable = 'Jul2017' or prgmStructApplicable = 'Jan2018' order by subject";
		try {
			List<String> subjectList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return subjectList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	
	public HashMap<String,TestTypeStudentPortalBean> getTestTypesMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.test_question_type ";
				try {
			List<TestTypeStudentPortalBean> testTypeList = (List<TestTypeStudentPortalBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeStudentPortalBean.class));
			HashMap<String,TestTypeStudentPortalBean> testTypeMap = new HashMap<>();
			for(TestTypeStudentPortalBean bean : testTypeList) {
				testTypeMap.put(bean.getType(), bean);
			}
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	

	public HashMap<Long,TestTypeStudentPortalBean> getTypeIdNTypeMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.test_question_type ";
				try {
			List<TestTypeStudentPortalBean> testTypeList = (List<TestTypeStudentPortalBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TestTypeStudentPortalBean.class));
			HashMap<Long,TestTypeStudentPortalBean> testTypeMap = new HashMap<>();
			for(TestTypeStudentPortalBean bean : testTypeList) {
				testTypeMap.put(bean.getId(), bean);
			}
			
			return testTypeMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	}
	
	
	//CRUD for tests Start
	public long saveTest(final TestStudentPortalBean test) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO exam.test "
				+ " (year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate,  subject,"
				+ "  maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, randomQuestion,"
				+ " testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert, maxScore, duration, passScore, testType,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
        		+ " VALUES(?,?,?,?,?,?,?,?,?" //9 ?
        		+ "		   ,?,?,?,?,?,?" // 6 ? 
        		+ "		   ,?,?,?,?,?,?,?,?" // 8 ?
        		+ "		   ,?,sysdate(),?,sysdate()) ";
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			        statement.setString(1, test.getYear());
			        statement.setString(2, test.getMonth());
			        statement.setString(3, test.getYear());
			        statement.setString(4, test.getAcadMonth()); 
			        statement.setString(5, test.getTestName()); 
			        statement.setString(6, test.getTestDescription()); 
			        statement.setString(7, test.getStartDate()); 
			        statement.setString(8, test.getEndDate());
			        statement.setString(9, test.getSubject());
			        
			        statement.setInt(10, test.getMaxQuestnToShow());
			        statement.setString(11, test.getShowResultsToStudents());
			        statement.setString(12, test.getActive());     
			        statement.setString(13, test.getFacultyId());
			        statement.setInt(14, test.getMaxAttempt());
			        statement.setString(15, test.getRandomQuestion());
			        
			        statement.setString(16, test.getTestQuestionWeightageReq()); 
			        statement.setString(17, test.getAllowAfterEndDate()); 
			        statement.setString(18, test.getSendEmailAlert()); 
			        statement.setString(19, test.getSendSmsAlert()); 
			        statement.setInt(20, test.getMaxScore());
			        statement.setInt(21, test.getDuration());
			        statement.setInt(22, test.getPassScore());
			        statement.setString(23, test.getTestType());
			        
			        statement.setString(24, test.getCreatedBy());
			        statement.setString(25, test.getLastModifiedBy());     
				       
			        return statement;
			    }
			}, holder);
			long primaryKey = holder.getKey().longValue();
			//System.out.println("In saveTest got primaryKey : "+primaryKey);
			return primaryKey;
		} catch (Exception e) {
			
			return 0;
		}

		}
		
		/* id, year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate, program,
		 *  subject, courseId, maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, 
		 *  randomQuestion, testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert,
		 *   maxScore, duration, passScore, testType, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		 * */
		public boolean updateTest(TestStudentPortalBean test) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test set "
							+ " year=?, "
							+ " month=?, "
							+ "testName=?, " 
							+ "testDescription=?, " 
							+ "startDate=?, "
							+ "endDate=?, "
							+ "subject=?, "
							+ "maxQuestnToShow=?, "
							+ "showResultsToStudents=?, "
							+ " active=?, "
							+ " facultyId=?, "
							+ "maxAttempt=?, " 
							+ "randomQuestion=?, " 
							+ "testQuestionWeightageReq=?, "
							+ "allowAfterEndDate=?, "
							+ "sendEmailAlert=?, " 
							+ "sendSmsAlert=?, "
							+ "maxScore=?, "
							+ "duration=?, " 
							+ "passScore=?, "
							+ "testType=?, " 
							+ "lastModifiedBy=? "       
								
							+ "where id=?";
			try {
				jdbcTemplate.update(sql,new Object[] {test.getYear(),test.getMonth(),test.getTestName(),
													  test.getTestDescription(),test.getStartDate(),test.getEndDate(),
													  test.getSubject(),test.getMaxQuestnToShow(),
													  test.getShowResultsToStudents(),test.getActive(),test.getFacultyId(), 
													  test.getMaxAttempt(),test.getRandomQuestion(),test.getTestQuestionWeightageReq(),
													  test.getAllowAfterEndDate(),test.getSendEmailAlert(),test.getSendSmsAlert(),
													  test.getMaxScore(),test.getDuration(),test.getPassScore(),test.getTestType(),test.getLastModifiedBy(),    
													  test.getId()
									});
				//System.out.println("IN Updated Test with id : "+test.getId());
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		public List<TestStudentPortalBean> getAllTests(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestStudentPortalBean> testsList=null;
			String sql="select * from exam.test Order By id desc ";
			try {
				 testsList = (List<TestStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}
		public List<TestStudentPortalBean> getTestsForFaculty(Set<Long> testIds){
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = 1;
			
			StringBuilder sb = new StringBuilder();
			List<TestStudentPortalBean> testsList=null;
			try {
				for(Long id : testIds) {
					if(count==1) {
						sb.append(id+"");
					}else {
						sb.append(","+id);
					}
				}
				String ids=sb.toString();
				//System.out.println("In getTestsForFaculty got ids: "+ids);
				String sql="select * from exam.test where id in ("+ids+") Order By id ";
				
				testsList = (List<TestStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return testsList;
		}
		public TestStudentPortalBean getTestById(Long id){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestStudentPortalBean test=null;
			String sql="select * from exam.test where id=?";
			try {
				 test = (TestStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TestStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return test;
		}
		
		public TestStudentPortalBean getTestByIdAndAttempts(Long id,String sapId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			TestStudentPortalBean test=null;
			String sql="select * from exam.test t " + 
					"   LEFT JOIN exam.test_student_testdetails s " + 
					"	on t.id = s.testId " + 
					"	where s.sapid=? and t.id=?";
			try {
				 test = (TestStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {sapId,id}, new BeanPropertyRowMapper(TestStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return test;
		}
		
		public int deleteTest(Long id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int row=0;	
		String sql="delete from exam.test where id=?";
		try {
			 row = jdbcTemplate.update(sql, new Object[] {id});
			 //System.out.println("Deleted "+row+" rows of TEST id " +id);
		} catch (Exception e) {
			
			return -1;
		}
		return row;
		}
		
		public List<TestStudentPortalBean> getAllTestsForStudent(List<String> allsubjects){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TestStudentPortalBean> testsList=null;
			String subjectCommaSeparated = "''";
			for (int i = 0; i < allsubjects.size(); i++) {
				if (i == 0) {
					subjectCommaSeparated = "'"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				} else {
					subjectCommaSeparated = subjectCommaSeparated + ", '"
							+ allsubjects.get(i).replaceAll("'", "''") + "'";
				}
			}
			////System.out.println("In getAllTestsForStudent subjectCommaSeparated: "+subjectCommaSeparated);
			String sql="select t.* from exam.test t " + 
					" where  "
					+ " "
					+ " subject in ("+subjectCommaSeparated+") "
					+ "Order By id desc ";
			try {
				 testsList = (List<TestStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TestStudentPortalBean.class));
				 //System.out.println("In getAllTestsForStudent sql: \n"+sql);
			} catch (Exception e) {
				
			}
			return testsList;
		}
		
		
	//CRUD for tests End
		

		//CRUD for Students test details start
		//id, sapid, testId, attempt, active, testStartedOn, testEndedOn, testCompleted, score, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public long saveStudentsTestDetails(final StudentsTestDetailsStudentPortalBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			final String sql = "INSERT INTO exam.test_student_testdetails "
					+ " ( sapid, testId, attempt, active, testStartedOn,  testCompleted, score, testQuestions, showResult,"
					+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
	        		+ " VALUES(?,?,?,?,sysdate(),?,?,?,?,"
	        		+ "		   ?,sysdate(),?,sysdate()) ";
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
				    @Override
				    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				        statement.setString(1, bean.getSapid());
				        statement.setLong(2, bean.getTestId());
				        statement.setInt(3, bean.getAttempt());
				        statement.setString(4, bean.getActive()); 
				        statement.setString(5, bean.getTestCompleted()); 
				        statement.setInt(6, bean.getScore()); 
				        statement.setString(7, bean.getTestQuestions()); 
				        statement.setString(8, bean.getShowResult()); 
				        statement.setString(9, bean.getCreatedBy());
				        statement.setString(10, bean.getLastModifiedBy());     
				        return statement;
				    }
				}, holder);
				long primaryKey = holder.getKey().longValue();
				//System.out.println("In savetestQuestion got primaryKey : "+primaryKey);
				return primaryKey;
			} catch (Exception e) {
				
				return 0;
			}

			}
		
		//id, sapid, testId, attempt, active, testStartedOn, testEndedOn, testCompleted, score, createdBy, createdDate, lastModifiedBy, lastModifiedDate
		public boolean updateStudentsTestDetails(StudentsTestDetailsStudentPortalBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="update exam.test_student_testdetails set "
							+ " "
							+ "	testEndedOn=sysdate(),"
							+ "	testCompleted=?, "
							+ "	score=?, "
							+ "	testQuestions=?, "
							+ " lastModifiedBy=?, "
							+ " lastModifiedDate = sysdate()  "    
								
							+ " where testId=? and sapid=? and attempt=? ";
			try {
				jdbcTemplate.update(sql,new Object[] {
													  bean.getTestCompleted(),
													  bean.getScore(),
													  bean.getTestQuestions(),
													  bean.getLastModifiedBy(),
													  
													  bean.getTestId(),bean.getSapid(),bean.getAttempt()
									});
				//System.out.println("IN updateStudentsTestDetails with sapid : "+bean.getSapid()+" and testId: "+bean.getTestId()+" and attempt : "+bean.getAttempt());
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			return false;
		}
		public List<StudentsTestDetailsStudentPortalBean> getStudentsTestDetailsBySapid(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsStudentPortalBean> testsByStudent=null;
			String sql="select * from exam.test_student_testdetails where sapid=?  Order By id ";
			try {
				testsByStudent = (List<StudentsTestDetailsStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(StudentsTestDetailsStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}
		
		public List<StudentsTestDetailsStudentPortalBean> getAttemptsDetailsBySapidNTestId(String sapid,Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsStudentPortalBean> testsByStudent=null;
			String sql="select * from exam.test_student_testdetails where sapid=? and testId = ?  Order By id ";
			try {
				testsByStudent = (List<StudentsTestDetailsStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsStudentPortalBean.class));
			} catch (Exception e) {
				
			}
			return testsByStudent;
		}
		public HashMap<Long, StudentsTestDetailsStudentPortalBean> getStudentsTestDetailsAndTestIdMapBySapid(String sapid){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<StudentsTestDetailsStudentPortalBean> testsByStudent=null;
			HashMap<Long, StudentsTestDetailsStudentPortalBean> testIdAndTestByStudentsMap = new HashMap<>(); 
			String sql="select * from exam.test_student_testdetails where sapid=?  Order By id asc ";
			try {
				testsByStudent = (List<StudentsTestDetailsStudentPortalBean>) jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(StudentsTestDetailsStudentPortalBean.class));
				
				for(StudentsTestDetailsStudentPortalBean test : testsByStudent) {
					testIdAndTestByStudentsMap.put(test.getTestId(), test);
				}
				
			} catch (Exception e) {
				
			}
			return testIdAndTestByStudentsMap;
		}
		public StudentsTestDetailsStudentPortalBean getStudentsTestDetailsBySapidAndTestId(String sapid, Long testId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			StudentsTestDetailsStudentPortalBean testByStudent= new StudentsTestDetailsStudentPortalBean();
			String sql="select * from exam.test_student_testdetails where sapid=? and testId=? Order By id desc limit 1 ";//get latest entry of students attempts
			try {
				testByStudent = (StudentsTestDetailsStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {sapid,testId}, new BeanPropertyRowMapper(StudentsTestDetailsStudentPortalBean.class));
			} catch (Exception e) {
				//
			}
			return testByStudent;
		}
		public int deleteStudentsTestDetailsById(Long id) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int row=0;	
			String sql="delete from exam.test_student_testdetails where id=?";
			try {
				 row = jdbcTemplate.update(sql, new Object[] {id});
				 //System.out.println("Deleted "+row+" rows of test_student_testdetails id " +id);
			} catch (Exception e) {
				
			}
			return row;
			}
		//CRUD for Students test details end
				
		

		public StudentStudentPortalBean getStudentRegistrationData(String sapId) {
			StudentStudentPortalBean studentRegistrationData = null;

			try {

				jdbcTemplate = new JdbcTemplate(dataSource);
				/*String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
						+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where assignmentLive = 'Y') ";*/
				
				String sql = "select * from exam.registration r where r.sapid = ? and  r.month = ? "
						+ " and r.year = ?  ";
				//System.out.println("Live Year/Month :"+getLiveAssignmentMonth()+getLiveAssignmentYear());
				studentRegistrationData = (StudentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] 
						{ sapId , getLiveAssignmentMonth(), getLiveAssignmentYear()},
								new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			} catch (Exception e) {
				// TODO: handle exception
				
				
			}
			return studentRegistrationData;
		}
				
}
