package com.nmims.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.PassFailBean;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.nmims.beans.IdCardStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

@Repository("studentDAO")
public class StudentDAO extends BaseDAO {
	
	Logger logger  = LoggerFactory.getLogger(StudentDAO.class);

	@Autowired
	ApplicationContext act;
	private NamedParameterJdbcTemplate  namedParameterJdbcTemplate;
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private PartnerConnection connection;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	public StudentDAO() {
		this.connection = SFConnection.getConnection();
	}
	
	public void init(){
		this.connection = SFConnection.getConnection();
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingStudentPortalBean> getAllApplicableSubjectsForStudent(StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT `sem`, `subject` "
			+ " FROM `exam`.`program_sem_subject` "
			+ " WHERE `consumerProgramStructureId` = ? "
			+ " AND `sem` < ( SELECT MAX(`sem`) FROM `exam`.`registration` WHERE `sapid` = ? ) "
			+ " AND `active` = 'Y' ";
		return jdbcTemplate.query(
			sql,
			new Object[] {	student.getConsumerProgramStructureId(), student.getSapid() },
			new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class)
		);
	}
	@Transactional(readOnly = false)
	public int updateStudentEmailId(Long sapid, String emailId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query ="";
		try {
			query =  "UPDATE exam.students " + 
					"SET emailId = ?, " + 
					"    lastModifiedDate = sysdate() " + 
					"WHERE sapid = ?";
			logger.info("Updating The StudentMobile Number sapid {} EmailId {}",sapid,emailId);
			
		} catch (Exception e) {
			e.printStackTrace();
			}
		return jdbcTemplate.update(query, emailId, sapid);
	}
	@Transactional(readOnly = false)
	public int updateStudentMobileNo(Long sapid, String mobile) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query ="";
		try {
			query =  "UPDATE exam.students " + 
					"SET mobile = ?, " +  
					"    lastModifiedDate = sysdate() " + 
					"WHERE sapid = ?";
			logger.info("Updating The StudentMobile Number sapid {} mobile   {}",sapid,mobile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jdbcTemplate.update(query, mobile, sapid);
	}
	@Transactional(readOnly = true)
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
			//e.printStackTrace();
			return false;
		}
	}

	@Transactional(readOnly = true)
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
			//e.printStackTrace();
			return null;
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}
	
	//Execution time is 16 milliseconds.
	@Transactional(readOnly = false)
	public Map<String, Object> getStudentFirstNameAndEmail(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT firstName, emailId, consumerProgramStructureId " + 
						"FROM exam.students " + 
						"WHERE sapid = ? ";
		
		return jdbcTemplate.queryForMap(query, sapid);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> getStudentPersonalDetails(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT sapid, firstName, lastName, fatherName, motherName, husbandName, emailId, mobile " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)" + 
						"				FROM exam.students" + 
						"				WHERE sapid = ?	)";
		
		return jdbcTemplate.queryForMap(query, sapid, sapid);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> getStudentFatherMotherHusbandName(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT fatherName, motherName, husbandName " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)" + 
						"				FROM exam.students" + 
						"               WHERE sapid = ? )";
		
		return jdbcTemplate.queryForMap(query, sapid, sapid);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> getStudentEmailIdMobileNo(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT sapid, emailId, mobile " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)" + 
						"				FROM exam.students" + 
						"				WHERE sapid = ?	)";

		return jdbcTemplate.queryForMap(query, sapid, sapid);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> getStudentEnrollmentYearMonth(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT enrollmentYear, enrollmentMonth " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)  " + 
						"				FROM exam.students  " + 
						"				WHERE sapid = ?	)";

		return jdbcTemplate.queryForMap(query, sapid, sapid);
	}
	
	@Transactional(readOnly = true)
	public String getStudentProgramStatus(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "SELECT programStatus " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)  " + 
						"				FROM exam.students  " + 
						"				WHERE sapid = ?	)";
		
		return jdbcTemplate.queryForObject(query, String.class, sapid, sapid);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentEmailId(Long sapid, String emailId, String user) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.students " + 
						"SET emailId = ?, " + 
						"	 lastModifiedBy = ?, " + 
						"    lastModifiedDate = sysdate() " + 
						"WHERE sapid = ?";
		
		return jdbcTemplate.update(query, emailId, user, sapid);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentMobileNo(Long sapid, String mobile, String user) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.students " + 
						"SET mobile = ?, " + 
						"	 lastModifiedBy = ?, " + 
						"    lastModifiedDate = sysdate() " + 
						"WHERE sapid = ?";
		
		return jdbcTemplate.update(query, mobile, user, sapid);
	}
	
	@Transactional(readOnly = false)
	public int updateActiveProgramStatus(Long sapid, String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.students " + 
						"SET programStatus = null," + 
						"	 programCleared = 'N'," + 
						"	 lastModifiedBy = ?," + 
						"	 lastModifiedDate = sysdate() " + 
						"WHERE sapid = ?";
		
		return jdbcTemplate.update(query, userId, sapid);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int updateStudentProgramStatusOnExitApproval(Long sapid, String userId,StudentStudentPortalBean newMappedProgram) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query =  "UPDATE exam.students " + 
						"SET programCleared = 'Y'," + 
						"	 programStatus = 'Program Withdrawal'," + 
						"	 lastModifiedBy = ?," + 
						"	 lastModifiedDate = sysdate()," + 
						"    program = ?," + 
						"	 PrgmStructApplicable = ?," + 
						"	 consumerType = ?," + 
						"	 consumerProgramStructureId = ?" + 
						" WHERE sapid = ?";
		
    		String query1 =  "update  exam.registration set consumerProgramstructureId=?,program=? where sapid =?"; 
    		
    		int count2=jdbcTemplate.update(query, userId,newMappedProgram.getProgram(),newMappedProgram.getProgramStructure(),newMappedProgram.getConsumerType(),newMappedProgram.getConsumerProgramStructureId(), sapid);
    		int count1=jdbcTemplate.update(query1,newMappedProgram.getConsumerProgramStructureId(),newMappedProgram.getProgram(),sapid);
    		return (count1+count2); 	
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSem1and2PassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? and sem in(1,2) order by sem asc ";
		
		return (ArrayList<String>) jdbcTemplate.queryForList(
			sql, 
			new Object[]{ sapid }, 
			String.class
		);
	}

	@Transactional(readOnly = true)
	public HashMap<String, Integer> getapplicableSubjectsForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= "SELECT ps.subject, ps.sem " + 
					"FROM exam.program_subject ps " + 
					"INNER JOIN exam.students s " + 
					"ON ps.program = s.program " + 
					"	AND ps.prgmStructApplicable = s.PrgmStructApplicable " + 
					"WHERE s.sapid = ? " + 
					"	AND ps.sem <= (	SELECT max(sem) " + 
					"					FROM exam.registration " + 
					"                    WHERE sapid = ? ) " + 
					"ORDER BY ps.sem";
		
		HashMap<String, Integer> semSubjectMap = jdbcTemplate.query(sql, (ResultSet rs) -> { 
																				return resultSetMapper(rs, String.class, Integer.class);
																			}, sapid, sapid);
		return semSubjectMap;
	}
	
	/**
	 * A ResultSet Mapper which returns a HashMap wherein the first ResultSet value is set as the Key, and second as it's Value 
	 * @param rs - ResultSet consisting of the result returned from the database query
	 * @param keyClass - Class to be set for Key
	 * @param valueClass - Class to be set for Value
	 * @return - HashMap containing the values from the ResultSet provided
	 * @throws SQLException
	 */
	private <K, V> HashMap<K, V> resultSetMapper(ResultSet rs, Class<K> keyClass, Class<V> valueClass) throws SQLException {
		HashMap<K, V> resultMap= new HashMap<>();
        while(rs.next()) {
        	resultMap.put(keyClass.cast(rs.getObject(1)), valueClass.cast(rs.getObject(2)));
        }
        
        return resultMap;
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
    	public List<ProgramSubjectMappingStudentPortalBean> getAllApplicableSubjectsForMBAStudent(StudentStudentPortalBean student){
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
    			new BeanPropertyRowMapper<ProgramSubjectMappingStudentPortalBean>(ProgramSubjectMappingStudentPortalBean.class)
    		);
    	}
    	
        @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    	public void saveIdCardDetailsForAStudent(String sapId, String fileName, String uniqueHashKey,String createdBy) {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "INSERT INTO portal.digital_id_cards(sapid, fileName, uniqueHashKey, "
    				+ " createdBy, lastModifiedBy) VALUES "
    				+ "(?,?,?,?,?) ON DUPLICATE KEY UPDATE fileName=?, uniqueHashKey=? ";

    		jdbcTemplate.update(sql, new Object[] { 
    				sapId,
    				fileName,
    				uniqueHashKey,
    				createdBy,
    				createdBy,
    				fileName,
    				uniqueHashKey
    		});

    	}
    	
    	@Transactional(readOnly = true)
    	public StudentStudentPortalBean getSingleStudentByUniqueHash(String uniqueHashKey) {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		StudentStudentPortalBean student = null;
    			String sql = "Select st.firstName,st.lastName,st.imageUrl,st.program,st.dob,st.sapid,st.enrollmentMonth,st.enrollmentYear,st.mobile,st.centerCode,st.centerName, st.validityEndYear , st.validityEndMonth from exam.students st inner join portal.digital_id_cards dc " + 
    					"on st.sapid=dc.sapid where dc.uniqueHashKey=?  ";
    			student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{uniqueHashKey}, new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
    			return student;

    	}
    	
    	@Transactional(readOnly = true)
    	public String getIdCardFileNameBySapid(String sapid) {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "SELECT fileName FROM portal.digital_id_cards where sapid=?  ";
    			String fileName = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new SingleColumnRowMapper<>(String.class));
    			return fileName;

    	}
    	
    	@Transactional(readOnly = true)
    	public HashMap<String, String> getMapOfLCName() {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		HashMap<String, String> mapOfLcName=new HashMap<String, String>();
    		String sql= "SELECT centerCode,lc FROM exam.centers";
    		try {
    		mapOfLcName = jdbcTemplate.query(sql, (ResultSet rs) -> { 
				return resultSetMapper(rs, String.class, String.class);
			});
    		}catch (Exception e) {}
    		return mapOfLcName;
    	}
    	
    	@Transactional(readOnly = true)
		public String getBlooadgroupFormSFDC(String sapid) throws Exception
		{
				String  bloodGroup = "";
				StringBuffer sfdcQuery = new StringBuffer(" SELECT nm_BloodGroup__c FROM Account where nm_StudentNo__c = '"+sapid+"' limit 1 ");
				QueryResult qResult = new QueryResult();
				qResult = connection.query(sfdcQuery.toString());

				if (qResult.getSize() > 0) {
			
					SObject[] records = qResult.getRecords();
					SObject s = (SObject) records[0];
					bloodGroup= (String) s.getField("nm_BloodGroup__c");
				}
		
			return bloodGroup;
		}
    	
    	@Transactional(readOnly = true)
    	public List<PassFailBean> getSemAndisPassResult(String sapid) {
    		List<PassFailBean> resultList=new ArrayList<PassFailBean>();
    		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "select pss.sem,pf.isPass from exam.mba_passfail pf inner join exam.program_sem_subject pss on pf.prgm_sem_subj_id=pss.id where pf.sapid=? ";
    			resultList = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailBean.class));
        		return resultList;
    	}

    	@Transactional(readOnly = true)
    	public int getCountOfPassedSubject(String sapid) {
 
    		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "select  count(*) from exam.mba_passfail pf inner join exam.program_sem_subject pss on pf.prgm_sem_subj_id=pss.id where pf.sapid=?and isPass='Y' ";
    			int count = (Integer)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new SingleColumnRowMapper(Integer.class));
    		return count;
    	}
    	@Transactional(readOnly = true)
    	public int totalSubjectCount(String consumerProgramStructureId,int sem) {
     		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "count(*) from exam.program_sem_subject where consumerProgramSTructureId=? and sem<=?";
    			int count = (Integer)jdbcTemplate.queryForObject(sql, new Object[]{consumerProgramStructureId,sem}, new SingleColumnRowMapper(Integer.class));
    		return count;
    	}
    	@Transactional(readOnly = true)
    	public int totalSubjectCountinParticularSem(String consumerProgramStructureId,String sem) {	
    		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "select count(*) from exam.program_sem_subject where consumerProgramStructureId=? and sem=?";
    			int count = (Integer)jdbcTemplate.queryForObject(sql, new Object[]{consumerProgramStructureId,sem}, new SingleColumnRowMapper(Integer.class));
    			return count;  		
    	}
    	@Transactional(readOnly = true)
    	public int getCountOfPassedSubjectforparticularSem(String sapid,String sem) {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    			String sql = "select  count(*) from exam.mba_passfail pf inner join exam.program_sem_subject pss on pf.prgm_sem_subj_id=pss.id where pf.sapid=? and pss.sem =? and isPass='Y' ";
    			int count = (Integer)jdbcTemplate.queryForObject(sql, new Object[]{sapid,sem}, new SingleColumnRowMapper(Integer.class));
    			
    		return count;
    	}
    	@Transactional(readOnly = true)
		public ArrayList<PassFailBean> getListOfSemWithIsPassLateral(StudentStudentPortalBean student) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select isPass, sem from exam.passfail where  (sapid = ? or sapid=?) order by sem  asc ";

			ArrayList<PassFailBean> subjectsList = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{student.getSapid(),student.getPreviousStudentId()}, new BeanPropertyRowMapper(PassFailBean.class));

			return subjectsList;
		}
		@Transactional(readOnly = true)
		public ArrayList<PassFailBean> getListOfSemWithIsPass(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select isPass, sem from exam.passfail where sapid=? order by sem  asc ";

			ArrayList<PassFailBean> subjectsList = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailBean.class));

			return subjectsList;
		}

		@Transactional(readOnly = true)
    	public List<PassFailBean> getSemAndPassedSubjects(String sapid) {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		List<PassFailBean> resultList=new ArrayList<PassFailBean>();
    		String sql = "select pss.sem,pf.isPass from exam.mba_passfail pf inner join exam.program_sem_subject pss on pf.prgm_sem_subj_id=pss.id where pf.sapid=? order by pss.sem";
    		resultList = (ArrayList<PassFailBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailBean.class));
        	return resultList;
    	}
		@Transactional(readOnly = true)
    	public List<String> getAllPSSIdByCPSIdAndSem(String consumerProgramStructureId, String sem) {	
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "select id from exam.program_sem_subject where consumerProgramStructureId=? and sem=?";
    		ArrayList<String> count = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{consumerProgramStructureId,sem}, new SingleColumnRowMapper(String.class));
    		return count;  		
    	}
    	
    	@Transactional(readOnly = true)
    	public int getCountOfPassedSubjectforparticularSem(String sapid, List<String> listOfPssId) {
    		namedParameterJdbcTemplate =new NamedParameterJdbcTemplate(dataSource);
    		MapSqlParameterSource paramSource= new MapSqlParameterSource();	
    		String sql = "select count(*) from exam.mba_passfail where sapid=:sapid and isPass='Y' and prgm_sem_subj_id in(:listOfPssId)";
    		paramSource.addValue("sapid", sapid);
    		paramSource.addValue("listOfPssId", listOfPssId);
    			
    		int count = (Integer)namedParameterJdbcTemplate.queryForObject(sql,paramSource, new SingleColumnRowMapper(Integer.class));
    		return count;
    	}

    	@Transactional(readOnly = true)
    	public List<String> getAllPSSIdByCPSIdtillSem(String consumerProgramStructureId, String sem) {	
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "select id from exam.program_sem_subject where consumerProgramStructureId=? and sem<=?";
    		ArrayList<String> count = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{consumerProgramStructureId,sem}, new SingleColumnRowMapper(String.class));
    		return count;  		
    	}
    	@Transactional(readOnly = true)
    	public int getCountOfPassedSubjectforparticularSemForMsc(String sapid, List<String> listOfPssId) {
    		namedParameterJdbcTemplate =new NamedParameterJdbcTemplate(dataSource);
    		MapSqlParameterSource paramSource= new MapSqlParameterSource();	
    		String sql = "select count(*) from exam.mba_passfail where sapid=:sapid and isPass='Y' and prgm_sem_subj_id in(:listOfPssId) and grade is not null";
    		paramSource.addValue("sapid", sapid);
    		paramSource.addValue("listOfPssId", listOfPssId);
    			
    		int count = (Integer)namedParameterJdbcTemplate.queryForObject(sql,paramSource, new SingleColumnRowMapper(Integer.class));
    		return count;
    	}
    	
    	@Transactional(readOnly = false)
    	public int updateCPSIDinRegistrationforExit(Long sapid,String CPSiD) {
    		try {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String query =  "update  exam.registration set consumerProgramstructureId=? where sapid =?";   		
    		return jdbcTemplate.update(query,CPSiD,sapid);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    		return 0;
    	}

    	public List<StudentStudentPortalBean> getStudentsListForEnrollmentYearMonth(String enrollmentMonth, String enrollmentYear){
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "select * from exam.students where enrollmentMonth=? and enrollmentYear=? and sapid not in (SELECT sapid FROM portal.digital_id_cards) limit 1000";
    		return jdbcTemplate.query(
    			sql,
    			new Object[] {enrollmentMonth,enrollmentYear},
    			new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class)
    		);
    	}
    	
    	@Transactional(readOnly = true)
		public String getAddressFormSFDC(String sapid) throws Exception
		{
				String  address = "";
				StringBuffer sfdcQuery = new StringBuffer(" SELECT nm_PermanentAddress__c FROM Account where nm_StudentNo__c = '"+sapid+"' limit 1 ");
				QueryResult qResult = new QueryResult();
				qResult = connection.query(sfdcQuery.toString());

				if (qResult.getSize() > 0) {
			
					SObject[] records = qResult.getRecords();
					SObject s = (SObject) records[0];
					address= (String) s.getField("nm_PermanentAddress__c");
				}
		
			return address;
		}
    	
    	@Transactional(readOnly = true)
    	public StudentStudentPortalBean getStudentDetailsBySapid(String sapid){
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "select sapid,firstName,lastName,dob,imageUrl,mobile,program,regDate,address,centerName,centerCode,enrollmentMonth,enrollmentYear,validityEndMonth,validityEndYear from exam.students where sapid=?";
    		return jdbcTemplate.queryForObject(
    			sql,
    			new Object[] {sapid},
    			new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class)
    		);
    	}
    	
    	@Transactional(readOnly = true)
		public IdCardStudentPortalBean getSpecialazationFromSFDC(String sapid) throws Exception
		{
    			IdCardStudentPortalBean idCardBean = new IdCardStudentPortalBean();
				String specializationType = "";
				String specialzation1 = "";
				String specialzation2 = "";
				String programName = "";
				StringBuffer sfdcQuery = new StringBuffer(" SELECT Program_Type_Ex__pc,Specialization_Type__c, Program_1_Abbreviation__pc, Program_2_Abbreviation__pc FROM Account where Account.nm_StudentNo__c = '"+sapid+"' limit 1 ");
				QueryResult qResult = new QueryResult();
				qResult = connection.query(sfdcQuery.toString());

				if (qResult.getSize() > 0) {
			
					SObject[] records = qResult.getRecords();
					SObject s = (SObject) records[0];
					specializationType= (String) s.getField("Specialization_Type__c");
					specialzation1 = (String) s.getField("Program_1_Abbreviation__pc");
					specialzation2 = (String) s.getField("Program_2_Abbreviation__pc");
					programName =  (String) s.getField("Program_Type_Ex__pc");
				}
				idCardBean.setSpecializationType(specializationType);
				idCardBean.setSpecialization1(specialzation1);
				idCardBean.setSpecialization2(specialzation2);
				idCardBean.setProgramName(programName);
				
			return idCardBean;
		}
    	
    	@Transactional(readOnly = true)
		public String getAccountIdFromSFDC(String sapid) throws Exception
		{
				String  accountId = "";
				StringBuffer sfdcQuery = new StringBuffer(" SELECT Id FROM Account where nm_StudentNo__c = '"+sapid+"' limit 1 ");
				QueryResult qResult = new QueryResult();
				qResult = connection.query(sfdcQuery.toString());

				if (qResult.getSize() > 0) {
			
					SObject[] records = qResult.getRecords();
					SObject s = (SObject) records[0];
					accountId= (String) s.getField("Id");
				}
		
			return accountId;
		}
    	
    	@Transactional(readOnly = true)
		public HashMap<String, String> getPermanentAndShippingAddresBySapid(String sapid) throws Exception
		{
    			HashMap<String, String> mapOfStudentAddress = new HashMap<String, String>();
				String  Shipping_Address__c = "";
				String  nm_PermanentAddress__c = "";
				StringBuffer sfdcQuery = new StringBuffer(" SELECT nm_PermanentAddress__c,Shipping_Address__c FROM Account where nm_StudentNo__c = '"+sapid+"' limit 1 ");
				QueryResult qResult = new QueryResult();
				qResult = connection.query(sfdcQuery.toString());

				if (qResult.getSize() > 0) {
			
					SObject[] records = qResult.getRecords();
					SObject s = (SObject) records[0];
					Shipping_Address__c= (String) s.getField("Shipping_Address__c");
					nm_PermanentAddress__c= (String) s.getField("nm_PermanentAddress__c");
					mapOfStudentAddress.put("shippingAddress", Shipping_Address__c);
					mapOfStudentAddress.put("permanentAddress", nm_PermanentAddress__c);
					if(!StringUtils.isBlank(Shipping_Address__c)) {
						mapOfStudentAddress.put("shippingAddress", Shipping_Address__c);
					}else {
						mapOfStudentAddress.put("shippingAddress", nm_PermanentAddress__c);
					}
				}
				
			return mapOfStudentAddress;
		}


		public List<String> getSubjectNameByPssId(List<Integer> waivedInSubjectId) {
		
				String sql =" SELECT subjectname FROM exam.mdm_subjectcode_mapping msm " + 
							" INNER JOIN  exam.mdm_subjectcode ms ON msm.subjectCodeId = ms.id " + 
							" WHERE msm.id IN (:waivedInSubjectId) "+
							" GROUP BY subjectname ";
				
				
				namedParameterJdbcTemplate =new NamedParameterJdbcTemplate(dataSource);
	    		MapSqlParameterSource paramSource= new MapSqlParameterSource();	
	    	
	    		paramSource.addValue("waivedInSubjectId", waivedInSubjectId);
	    			
	    		List<String> subject = namedParameterJdbcTemplate.queryForList(sql,paramSource,String.class);
	    		return subject;
				
	    		
		
		}

    	
    	
    	@Transactional(readOnly = true)
    	public List<String> getsapIdsFromRegistration(String month,String year) throws Exception {
    		jdbcTemplate = new JdbcTemplate(dataSource);
    		String sql = "select sapid from exam.registration  where   month=? and year=? ";
    		List<String> sapidList =  jdbcTemplate.queryForList(sql,new Object[] {month,year},String.class);
    		return sapidList;
    	}
    	
    	@Transactional(readOnly = true)
    	public List<StudentStudentPortalBean> getstudentInfoList() throws Exception{

            List<StudentStudentPortalBean> studentList = new ArrayList<StudentStudentPortalBean>();
            NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//
//            //Create MapSqlParameterSource object
//            MapSqlParameterSource queryParams = new MapSqlParameterSource();

            String sql = new StringBuilder("select sapid,firstName,lastName,program,sem,regDate,previousStudentId,isLateral,PrgmStructApplicable,consumerProgramStructureId from exam.students where isLateral='Y'").toString();

//            //Adding parameters in SQL parameter map.
//            queryParams.addValue("sapids", sapidList);
           
            	studentList = (List<StudentStudentPortalBean>) namedJdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
           
            return studentList;
        }
    	
		@Transactional(readOnly = true)
		public List<StudentStudentPortalBean> getsubjectSemByMasterKeyList(Set<String> masterKeyList)
				throws Exception {
			// TODO Auto-generated method stub
			List<StudentStudentPortalBean> studentList = new ArrayList<StudentStudentPortalBean>();
			NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			// Create MapSqlParameterSource object
			MapSqlParameterSource queryParams = new MapSqlParameterSource();

			String sql = new StringBuilder(
					"select sem,subject,consumerProgramStructureId from program_sem_subject  where consumerProgramStructureId in (:masterKeyList)")
							.toString();
			// Adding parameters in SQL parameter map.
			queryParams.addValue("masterKeyList", masterKeyList);
			studentList = (List<StudentStudentPortalBean>) namedJdbcTemplate.query(sql, queryParams,
					new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
			return studentList;
		}

		
}
