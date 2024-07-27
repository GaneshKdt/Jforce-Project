package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentPendingSubjectBean;

@Component
public class StudentDAO extends BaseDAO {

	@Autowired
	ApplicationContext act;
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	public List<ProgramSubjectMappingExamBean> getAllApplicableSubjectsForStudent(StudentExamBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `sem`, `subject`, `id`  "
			+ " FROM `exam`.`program_sem_subject` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `sem` < ( SELECT MAX(`sem`) FROM `exam`.`registration` WHERE `sapid` = ? ) "
			+ " AND `active` = 'Y' ";
		return jdbcTemplate.query(
			sql,
			new Object[] {	student.getConsumerProgramStructureId(), student.getSapid() },
			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
		);
	}
	
	
	public List<StudentPendingSubjectBean> getAllCurrentlyApplicableSubjectsForStudent(StudentExamBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `msm`.`sem`, `msc`.`subjectname` AS `subject`, `subjectCodeId` AS `subjectId` "
			+ " FROM `exam`.`mdm_subjectcode_mapping` `msm` "
			+ " INNER JOIN `exam`.`mdm_subjectcode` `msc` ON `msc`.`id` = `msm`.`subjectCodeId` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `sem` <= ( SELECT MAX(`sem`) FROM `exam`.`registration` WHERE `sapid` = ? ) "
//			+ " AND `active` = 'Y' "
			;
		return jdbcTemplate.query(
			sql,
			new Object[] {	student.getConsumerProgramStructureId(), student.getSapid() },
			new BeanPropertyRowMapper<StudentPendingSubjectBean>(StudentPendingSubjectBean.class)
		);
	}
	
	public boolean checkIfStudentIsLateral(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`students` `s1` "
			+ " WHERE `sapid` = ? AND `isLateral` = 'Y' "
			+ " AND `sem` = ( SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s1`.`sapid` ) " ;
		try {
			int count = jdbcTemplate.queryForObject(
				sql,
				new Object[] { sapid },
				Integer.class
			);
					
			return count > 0;
		} catch (Exception e) {
			
			return false;
		}
	}

	public String getPreviousStudentNumber(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `previousStudentId` "
			+ " FROM `exam`.`students` `s1` "
			+ " WHERE `sapid` = ? AND `isLateral` = 'Y' "
			+ " AND `sem` = ( SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s1`.`sapid` ) " ;
		try {
			return jdbcTemplate.queryForObject(
				sql,
				new Object[] { sapid },
				String.class
			);
		} catch (Exception e) {
			
			return null;
		}
	}

	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSem1And2PassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? and sem in(1,2) order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}

	
	@Transactional(readOnly = true)
	public StudentExamBean getStudentInfo(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select * from exam.students "
					+ "where sapid=? "
					+ "	and sem = (	select max(sem) "
					+ "				from exam.students "
					+ "				where sapid = ?) ";
		
		return  jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ sapid,sapid }, 
			    new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
				);
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getStudentDetails(Long sapid, int sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT * FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = ?";
		
		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(StudentExamBean.class), sapid, sem);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentDetails(String sapid, String sem, String firstName, String middleName, String lastName, String fatherName, String motherName, String husbandName, 
								 	String gender, String dob, String email, String mobile, String altPhone, 
								 	String address, String houseNoName, String street, String landMark, String locality, String pin, String city, String state, String country, 
//								 	String age, String imageUrl, String centerName, String centerCode, String program, String prgmStructApplicable, 
//								 	String enrollmentMonth, String enrollmentYear, String validityEndMonth, String validityEndYear, 
//								 	String programChanged, String oldProgram, String highestQualification, 
									String programCleared, String programStatus, String programRemarks, String industry, String designation, String userId) {

		String query = "UPDATE exam.students "
					+ "SET firstName=?, "
						+ "middleName=?, "
						+ "lastName=?, "
						+ "fatherName=?, "
						+ "motherName=?, "
						+ "husbandName=?, "
						+ "gender=?, "
						+ "dob=DATE_FORMAT(?, '%Y-%m-%d'), "
//						+ "age=?, "
//						+ "imageUrl=?, "
						+ "emailId=?, "
						+ "mobile=?, "
						+ "altPhone=?, "
						+ "address=?, "
						+ "houseNoName=?, "
						+ "street=?, "
						+ "landMark=?, "
						+ "locality=?, "
						+ "pin=?, "
						+ "city=?, "
						+ "state=?, "
						+ "country=?, "
//						+ "centerName=?, "
//						+ "centerCode=?, "
//						+ "program=?, "
//						+ "PrgmStructApplicable=?, "
//						+ "enrollmentMonth=?, " 
//						+ "enrollmentYear=?, "
//						+ "validityEndMonth=?, "
//						+ "validityEndYear=?, "
//						+ "programChanged=?, "
//						+ "oldProgram=?, "
						+ "programCleared=?, "
						+ "programStatus=?, "
						+ "programRemarks=?, "
//						+ "highestQualification=?, "
						+ "industry=?, "
						+ "designation=?, "
//						+ "consumerType=?, "
//						+ "consumerProgramStructureId=?, "
						+ "lastModifiedBy=?, "
						+ "lastModifiedDate=sysdate() " 
					+ "WHERE sapid=? "
					+ "	AND sem = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int noOfRowsUpdated = jdbcTemplate.update(query, new PreparedStatementSetter() {
		
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, firstName);
				ps.setString(2, middleName);
				ps.setString(3, lastName);
				ps.setString(4, fatherName);
				ps.setString(5, motherName);
				ps.setString(6, husbandName);
				ps.setString(7, gender);
				ps.setString(8, dob);
				ps.setString(9, email);
				ps.setString(10, mobile);
				ps.setString(11, altPhone);
				ps.setString(12, address);
				ps.setString(13, houseNoName);
				ps.setString(14, street);
				ps.setString(15, landMark);
				ps.setString(16, locality);
				ps.setString(17, pin);
				ps.setString(18, city);
				ps.setString(19, state);
				ps.setString(20, country);
				ps.setString(21, programCleared);
				ps.setString(22, programStatus);
				ps.setString(23, programRemarks);
				ps.setString(24, industry);
				ps.setString(25, designation);
				ps.setString(26, userId);
				ps.setString(27, sapid);
				ps.setInt(28, Integer.valueOf(sem));
			}
		});
		
		return noOfRowsUpdated;
	}

    @Transactional(readOnly = true)
  	public List<ProgramSubjectMappingExamBean> getAllApplicableSubjectsForMBAStudent(StudentExamBean student){
  		jdbcTemplate = new JdbcTemplate(dataSource);
  		String sql = ""
  			+ " SELECT `sem`, `subject` "
  			+ " FROM `exam`.`program_sem_subject` "
  			+ " WHERE `consumerProgramStructureId` = ? "
  			+ " AND `sem` < ( SELECT MIN(`sem`) FROM `exam`.`registration` WHERE `sapid` = ? ) "
  			+ " AND `active` = 'Y' ";
  		return jdbcTemplate.query(
  			sql,
  			new Object[] {	student.getConsumerProgramStructureId(), student.getSapid() },
  			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
  		);
  	}
    
    @Transactional(readOnly = true)
	public ArrayList<String> getApplicableSubjectNew(String consumerProgramStructure){
		
		ArrayList<String> applicableSubjects = new ArrayList<>();
		String sql =" select subject from exam.program_sem_subject where consumerProgramStructureId = ? ";
		try {
			applicableSubjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{consumerProgramStructure}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return applicableSubjects;
			
	}
    
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentNameEmailId(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT CONCAT(firstName, ' ', lastName) AS name, emailId " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)" + 
						"				FROM exam.students" + 
						"				WHERE sapid = ?	)";

		return jdbcTemplate.queryForMap(query, sapid, sapid);
	}
}
