package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.beans.UpgradTestQuestionBean;
import com.nmims.beans.UpgradTestQuestionOptionBean;


@Repository("upgradAssessmentDao")
public class UpgradAssessmentDao {
	
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = false)
	public void callProcedureUpgradSessionplanidTimeboundidMapping() {
		try{
			 jdbcTemplate.execute("{call acads.proc_upgrad_sessionplanid_timeboundid_mapping()}");
		}catch(Exception e) {
			  
		}
	}
	
	@Transactional(readOnly = false)
	public long saveUpgradTest(final TestAcadsBean test) {
	
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO exam.upgrad_test "
				+ " (year, month, acadYear, acadMonth, testName, testDescription, startDate, endDate,  subject,"
				+ "  maxQuestnToShow, showResultsToStudents, active, facultyId, maxAttempt, randomQuestion,"
				+ " testQuestionWeightageReq, allowAfterEndDate, sendEmailAlert, sendSmsAlert, maxScore, duration, passScore, testType,"
				+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate,"
				+ " consumerTypeIdFormValue, programStructureIdFormValue, programIdFormValue, "
				+ " applicableType, referenceId ,id"
				+ ") "
        		+ " VALUES(?,?,?,?,?,?,?,?,?" //9 ?
        		+ "		   ,?,?,?,?,?,?" // 6 ? 
        		+ "		   ,?,?,?,?,?,?,?,?" // 8 ?
        		+ "		   ,?,sysdate(),?,sysdate()"
        		+ "		   ,?,?,?"
        		+ "		   ,?,?,?"
        		+ ") "
        		+ "on duplicate key update"
        		+ " year=?, "
        		+ " month=?, "
        		+ " acadYear=?, "
        		+ " acadMonth=?, "
        		+ " testName=?,"
        		+ " testDescription=?, "
        		+ " startDate=?, "
        		+ " endDate=?, "
        		+ " subject=?,"
				+ "  maxQuestnToShow=?, "
				+ " showResultsToStudents=?, "
				+ " active=?, "
				+ " facultyId=?,"
				+ " maxAttempt=?, "
				+ " randomQuestion=?,"
				+ " testQuestionWeightageReq=?, "
				+ " allowAfterEndDate=?, "
				+ " sendEmailAlert=?, "
				+ " sendSmsAlert=?, "
				+ " maxScore=?, "
				+ " duration=?, "
				+ " passScore=?, "
				+ " testType=?,"
				+ " lastModifiedBy='Upgrad', "
				+ " lastModifiedDate=sysdate(),"
				+ " consumerTypeIdFormValue=?, "
				+ " programStructureIdFormValue=?, "
				+ " programIdFormValue=?, "
				+ " applicableType=?, "
				+ " referenceId=?";
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
			        statement.setString(26, test.getConsumerTypeIdFormValue());
			        statement.setString(27, test.getProgramStructureIdFormValue()); 
			        statement.setString(28, test.getProgramIdFormValue());
			        statement.setString(29, test.getApplicableType()); 
			        statement.setLong(30, test.getReferenceId());
			        statement.setLong(31, test.getTestId());
			        
			        
			        
			        statement.setString(32, test.getYear());
			        statement.setString(33, test.getMonth());
			        statement.setString(34, test.getYear());
			        statement.setString(35, test.getAcadMonth()); 
			        statement.setString(36, test.getTestName()); 
			        statement.setString(37, test.getTestDescription()); 
			        statement.setString(38, test.getStartDate()); 
			        statement.setString(39, test.getEndDate());
			        statement.setString(40, test.getSubject());
			        
			        statement.setInt(41, test.getMaxQuestnToShow());
			        statement.setString(42, test.getShowResultsToStudents());
			        statement.setString(43, test.getActive());     
			        statement.setString(44, test.getFacultyId());
			        statement.setInt(45, test.getMaxAttempt());
			        statement.setString(46, test.getRandomQuestion());
			        
			        statement.setString(47, test.getTestQuestionWeightageReq()); 
			        statement.setString(48, test.getAllowAfterEndDate()); 
			        statement.setString(49, test.getSendEmailAlert()); 
			        statement.setString(50, test.getSendSmsAlert()); 
			        statement.setInt(51, test.getMaxScore());
			        statement.setInt(52, test.getDuration());
			        statement.setInt(53, test.getPassScore());
			        statement.setString(54, test.getTestType());
			        
			        statement.setString(55, test.getConsumerTypeIdFormValue());
			        statement.setString(56, test.getProgramStructureIdFormValue()); 
			        statement.setString(57, test.getProgramIdFormValue());
			        

			        statement.setString(58, test.getApplicableType()); 
			        statement.setLong(59, test.getReferenceId());
			        return statement;
			    }
			}, holder);
			//final long primaryKey = holder.getKey().longValue();
			
			//TestBean testToshow = getTestById(primaryKey) ;
			
			return 1;
		} catch (Exception e) {
			  
			return 0;
		}

		}
	
	@Transactional(readOnly = false)
	public String insertUpgradTestIdNConfigurationMappings(final TestAcadsBean bean) {
		try {
			String sql = " INSERT INTO exam.upgrad_test_testid_configuration_mapping "
					+ " (testId, type, referenceId, iaType, createdBy, createdDate) "
					+ " VALUES (?,?,?,?,?,sysdate()) "
					+ " ON DUPLICATE KEY UPDATE "
					+ " type = ?,"
					+ " referenceId = ?, "
					+ " iaType = ?, "
					+ " createdDate =sysdate() ";
			
			
			jdbcTemplate.update(sql, new Object[] { 
					bean.getTestId(),
					bean.getApplicableType(),
					bean.getReferenceId(),
					bean.getIaType(),
					bean.getCreatedBy(),
					// start ON DUPLICATE KEY UPDATE
					bean.getApplicableType(),
					bean.getReferenceId(),
					bean.getIaType()
			});
			
//			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//
//				@Override
//				public void setValues(PreparedStatement ps, int i) throws SQLException {
//
//					ps.setLong(1,bean.getId());
//					ps.setString(2,bean.getApplicableType());
//					
//					if("batch".equalsIgnoreCase(bean.getApplicableType()) ) {
//						ps.setLong(3,configIds.get(i));
//						ps.setString(4,bean.getIaType()); 
//					}else if("module".equalsIgnoreCase(bean.getApplicableType())) {
//						ps.setLong(3,configIds.get(i));
//						ps.setString(4,bean.getIaType()); 
//					}else if("old".equalsIgnoreCase(bean.getApplicableType())) {
//						ps.setLong(3,configIds.get(i));
//						ps.setString(4,"Test"); //temporary
//					}else {
//						ps.setLong(3, (long)0);
//					}
//					ps.setString(5,bean.getCreatedBy());		
//				}
//
//				@Override
//				public int getBatchSize() {
//					return configIds.size();
//				}
//			  });
			return "";
		} catch (DataAccessException e) {
			  
			return "Error in insertTestIdNConfigurationMappings : "+e.getMessage();
		}
	
	}
	
	@Transactional(readOnly = false)
	public void insertUpgradTestQuestions( List<UpgradTestQuestionBean> testQuestion,final Long testId) {
		for(final UpgradTestQuestionBean testQuestions:testQuestion) {
			
		String sql1 = "SELECT id FROM exam.upgrad_test_questions_type WHERE question_type = ?";
		final int questionTypeId =  jdbcTemplate.queryForObject(sql1, new Object[]{testQuestions.getQuestion_type()}, Integer.class);
			
		final String sql2 = "INSERT INTO `exam`.`upgrad_test_questions` "
				+ " (`id`,`testId`, `marks`, `type`, `chapter`, `question`, `description`, `isSubQuestion`, `active`, `createdBy`, `lastModifiedBy`, `copyCaseThreshold`, `uploadType`) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) "
				+ " ON DUPLICATE KEY UPDATE "
				+ " id = ?, "
				+ " testId = ?, "
				+ " marks = ?,"
				+ " type = ?,"
				+ " chapter = ?,"
				+ " question= ?,"
				+ " description = ?,"
				+ " isSubQuestion = ?,"
				+ " active = ?,"
				+ " copyCaseThreshold= ?, "
				+ " uploadType = ?" ;
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
		        statement.setLong(1, testQuestions.getQuestionNo());
		        statement.setLong(2, testId);
		        statement.setInt(3,testQuestions.getMarks());
		        statement.setInt(4, questionTypeId);
		        statement.setString(5,testQuestions.getChapter());
		        statement.setString(6, testQuestions.getQuestion());
		        statement.setString(7, testQuestions.getDescription());
		        statement.setObject(8, testQuestions.getIsSubQuestion());
		        statement.setString(9, testQuestions.getActive());
		        statement.setString(10, "Upgrad");
		        statement.setString(11, "Upgrad");
		        statement.setInt(12, testQuestions.getCopyCaseThreshold());
		        statement.setString(13, testQuestions.getUploadType());
	
		     // ON DUPLICATE KEY UPDATE Fields
		        statement.setLong(14, testQuestions.getQuestionNo());
		        statement.setLong(15, testId);
		        statement.setInt(16,testQuestions.getMarks());
		        statement.setInt(17, questionTypeId);
		        statement.setString(18,testQuestions.getChapter());
		        statement.setString(19, testQuestions.getQuestion());
		        statement.setString(20, testQuestions.getDescription());
		        statement.setObject(21, testQuestions.getIsSubQuestion());
		        statement.setString(22, testQuestions.getActive());
		        statement.setInt(23, testQuestions.getCopyCaseThreshold());
		        statement.setString(24, testQuestions.getUploadType());
	
		        
		        return statement;
		    }
		});
		
		insertUpgradTestQuestionOptions(testQuestions.getTestQuestionOptions(), testQuestions.getQuestionNo());
		}	
	}
	
	@Transactional(readOnly = false)
	public void insertUpgradTestQuestionOptions(List<UpgradTestQuestionOptionBean> testQuestionOption, final Long questionNo) {
		for(final UpgradTestQuestionOptionBean testQuestionOptions : testQuestionOption) {
			final String sql = "INSERT INTO `exam`.`upgrad_test_question_options` "
					+ " (`optionId`, `questionNo`, `optionData`, `isCorrect`, `createdBy`,  `lastModifiedBy`) "
						+ " VALUES (?, ?, ?, ?, ?, ?) "
						+ " ON DUPLICATE KEY UPDATE "
						+ " optionId = ?, "
						+ " questionNo = ?,"
						+ " optionData = ?, "
						+ " isCorrect = ? " ;
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
						statement.setLong(1, testQuestionOptions.getOptionId() );
						statement.setLong(2, questionNo);
						statement.setString(3, testQuestionOptions.getOptionData());
						statement.setString(4, testQuestionOptions.getIsCorrect());
						statement.setString(5, "Upgrad" );
						statement.setString(6, "Upgrad" );
						
						// ON DUPLICATE KEY UPDATE Fields
						statement.setLong(7, testQuestionOptions.getOptionId() );
						statement.setLong(8, questionNo);
						statement.setString(9, testQuestionOptions.getOptionData());
						statement.setString(10, testQuestionOptions.getIsCorrect());
					return statement;
			    }
			});
		}
	}
	
	@Transactional(readOnly = false)
	public void deleteSessionPlanModuleCascade(SessionPlanModuleBean module){
		String sql = " DELETE FROM `acads`.`upgrad_sessionplan_module` "
				+ " WHERE `sessionModuleNo`= ? AND `sessionPlanId`= ? ";  
		
		 jdbcTemplate.update(sql, new Object[] {
					module.getSessionModuleNo(),
					module.getSessionPlanId()} );
		
	}
	
}
