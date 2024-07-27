package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.CenterBean;
import com.nmims.beans.DocumentBean;
import com.nmims.beans.ExamOrderBean;
import com.nmims.beans.KnowlarityData;
import com.nmims.beans.SchedulerApisBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentDetailsFromSFDCBean;
import com.nmims.beans.StudentLearningMetricsBean;
import com.nmims.interfaces.StudentIdCardInterface;
import com.nmims.interfaces.StudentSubjectCourseInterface;

//@Component
public class StudentZoneDao {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LoggerFactory.getLogger(StudentZoneDao.class);
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("#{'${TIMEBOUND_PORTAL_LIST}'.split(',')}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	@Value("${CURRENT_PDDM_ACAD_MONTH}")
	private String CURRENT_PDDM_ACAD_MONTH;
	
	@Value("${CURRENT_PDDM_ACAD_YEAR}")
	private String CURRENT_PDDM_ACAD_YEAR;
	
	private List<String> MBAX_PORTAL_LIST = Arrays.asList("119", "126");
	
	@Autowired
	StudentSubjectCourseInterface studentCourse;
	
	@Autowired
	StudentIdCardInterface idCardService;
	
	public void setDataSource(DataSource dataSource) {
		//System.out.println("Setting Data Source " + dataSource);
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		//System.out.println("jdbcTemplate = " + jdbcTemplate);
	}

	public JdbcTemplate getJdbCTempalte() {
		return jdbcTemplate;
	}

	@Transactional(readOnly = false)
	public int insertKnowlarityData(final KnowlarityData kd, final java.sql.Date sqlDate) throws Exception
	{
		try
		{
			String query="insert into salesforce.knowlarity (callC,callDate,callTime,callUuid,calledNumber,customerCallDuration,salesforceId,menu,ownerid,priority,purpose,status,subject,whoid,customerStatus) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
			int rows=jdbcTemplate.execute(query,new PreparedStatementCallback<Integer>(){  
			    @Override  
			    public Integer doInPreparedStatement(PreparedStatement ps)  
			            throws SQLException, DataAccessException {  
			              
			        ps.setString(1,kd.getCall__c());  
			        ps.setDate(2,sqlDate); 
			        Time sqlTime=Time.valueOf(kd.getCall_time__c().substring(0,8));
			        ps.setTime(3,sqlTime);
			        ps.setString(4,kd.getCall_uuid__c());
			        ps.setString(5,kd.getCalled_number__c()); 
			        ps.setString(6,kd.getCustomer_call_duration__c());
			        ps.setString(7,kd.getId());
			        ps.setString(8,kd.getMenu__c());
			        ps.setString(9,kd.getOwnerid());
			        ps.setString(10,kd.getPriority());
			        ps.setString(11,kd.getPurpose__c());
			        ps.setString(12,kd.getStatus());
			        ps.setString(13,kd.getSubject());
			        ps.setString(14,kd.getWhoid());
			        ps.setString(15,kd.getCustomer_status__c());
			        return ps.executeUpdate();              
			    }  
			    });
			return rows;
		}
		catch(Exception e)
		{
			logger.info("Exceptoin in insertion is:"+e.getMessage());
			throw new Exception(e);
		}
	}

	public void insertSpecialisationDetails(StudentBean studentBean,
			HashMap<String, String> portalSpecialisationMapping) {
			String sql = " INSERT INTO `lti`.`mba_specialisation_details` "
					+ " (`sapid`, `specializationType`, `specialisation1`, `specialisation2`, "
					+ " `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) "
					+ " VALUES (?, ?, ?, ?, ?, sysdate(), ?, sysdate() ) ";
			jdbcTemplate.update(sql,
					new Object[] { studentBean.getSapid(), studentBean.getSpecializationType(),
							portalSpecialisationMapping.get(studentBean.getSpecialisation1()),
							portalSpecialisationMapping.get(studentBean.getSpecialisation2()), studentBean.getCreatedBy(),
							studentBean.getLastModifiedBy() });
		
	}

	@Transactional(readOnly = false)
	public long insertMailRecordFilterCriteria(final String filterCriteria,final String userId,final String fromEmailID,final String subject,final String htmlBody) {
		//jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, subject);
				statement.setString(2, userId);
				statement.setString(3,filterCriteria);
				statement.setString(4,htmlBody);
				statement.setString(5,fromEmailID);
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public long insertMailRecord(final String userId,final String fromEmailID,final String subject,final String htmlBody) {
		//jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,body,fromEmailId) VALUES(?,?,sysdate(),?,?) ", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, subject);
				statement.setString(2, userId);
				statement.setString(3,htmlBody);
				statement.setString(4,fromEmailID);
				return statement;
			}
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public void insertUserMailRecord(String userId, String fromEmailID,String sapid,String emailId,
			long insertedMailId) {
		//jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId) VALUES(?,?,sysdate(),?,?,?) ";
		try{
			int batchUpdateDocumentRecordsResultSize = jdbcTemplate.update(sql, new Object[]{
					sapid,
					emailId,
					userId,
					fromEmailID,
					String.valueOf(insertedMailId)
			});
		}
		catch(Exception e)
		{
			logger.info("Exception in insertMailRecord method :"+e);
			//e.printStackTrace();
		}
		
	}
	
	public HashMap<String, StudentBean> getAllStudents() {
		//System.out.println("jdbcTemplate = " + jdbcTemplate);
		//updated as program list to be taken from program table
		//String sql = "Select s.*, p.programname from exam.students s, exam.programs p where s.program = p.program ";
		String sql = "Select s.*, s.program as programname from exam.students s ";
		ArrayList<StudentBean> students = (ArrayList<StudentBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(StudentBean.class));

		HashMap<String, StudentBean> studentsMap = new HashMap<>();
		for (StudentBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}
		return studentsMap;
	}
	
	//batch update of address details start

	public String batchUpdateAllStudentsAddress(final List<StudentBean> studentList) {
		
		String errorMessage="";
		String sql = "update exam.students " //changed to world fro testing
				+ " set "
				+ " houseNoName = ?,"
				+ " street = ?,"
				+ " locality = ?,"
				+ " landMark = ?," 
				+ " city = ?," 
				+ " state = ?,"
				+ " country=?,"
				+ " pin=?,"
				+ " highestQualification=?,"
				+ " age=?,"
				+ " dob=?,"
				+ " regDate=?,"
				+ " centercode=?,"
				+ " centerName=?,"
				+ " lastModifiedDate=sysdate(),"
				+ " lastModifiedBy = ?" 
				+ " where sapid = ?";
		
		try {
			int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					StudentBean bean = studentList.get(i);
					
					ps.setString(1, bean.getHouseNoName());
					ps.setString(2, bean.getStreet());
					ps.setString(3, bean.getLocality());
					ps.setString(4, bean.getLandMark());
					ps.setString(5, bean.getCity());
					ps.setString(6, bean.getState());
					ps.setString(7, bean.getCountry());
					ps.setString(8, bean.getPin());
					ps.setString(9, bean.getHighestQualification());
					ps.setString(10, bean.getAge().substring(0, 2));
					ps.setString(11, bean.getDob());
					ps.setString(12, bean.getRegDate());
					ps.setString(13, bean.getCenterCode());
					ps.setString(14, bean.getCenterName());
					ps.setString(15, "Admin API");
					ps.setString(16, bean.getSapid());
				}

				public int getBatchSize() {
					return studentList.size();
				}
			});

			//System.out.println("Updated students address : "+result.length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMessage = "Error in batch upload. Error :"+e.getMessage();
		}

		return errorMessage;
	}

	//end

	// sync but not record in Registration Table
	public HashMap<String, StudentBean> getAllSyncStudentsNotPresentInRegistrationDataTable(String mismactMonth , String mismatchYear , ArrayList<String> sapIdList){
		HashMap<String, StudentBean> studentsMap = new HashMap<>();
		
		String commaSeparatedList = generateCommaSeparatedList(sapIdList);
		
		//System.out.println("jdbcTemplate = " + jdbcTemplate);
		String sql = " select * from exam.students where  "
				+ " sapid not In ( select sapid from exam.registration where month = ?  and year = ? ) "
				+ " and sapid in (" + commaSeparatedList + ") ";
		
		ArrayList<StudentBean> students = (ArrayList<StudentBean>) jdbcTemplate.query(sql,
				new Object[] { mismactMonth, mismatchYear },
				new BeanPropertyRowMapper(StudentBean.class));
		
		if (students.size() > 0) {
			for (StudentBean student : students) {
				studentsMap.put(student.getSapid(), student);
			}
		}
		
		return studentsMap;
	}
	
	private String generateCommaSeparatedList(ArrayList<String> sapIdList) {
		String commaSeparatedList = "";
		for(String sapId : sapIdList){
			if(commaSeparatedList =="" || commaSeparatedList == null){
				commaSeparatedList = sapId +",";
			}else{
				commaSeparatedList = commaSeparatedList + sapId+",";
			}
		}
		
		if (commaSeparatedList.endsWith(",")) {
			commaSeparatedList = commaSeparatedList.substring(0,
					commaSeparatedList.length() - 1);
		}
		return commaSeparatedList;
	}
	
	// query Student whose registration for current Acads Month and Year not
	// present
	public HashMap<String, StudentBean> getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear(
			String CURRENT_ACAD_MONTH, String CURRENT_ACAD_YEAR) {
		HashMap<String, StudentBean> studentsMap = new HashMap<String, StudentBean>();
		try {
			//System.out.println("jdbcTemplate = " + jdbcTemplate);
			String sql = " select * from exam.students where enrollmentMonth = ? and enrollmentYear = ? and "
					+ " sapid not In ( select sapid from exam.registration where month = ?  and year = ? ) ";

			ArrayList<StudentBean> students = (ArrayList<StudentBean>) jdbcTemplate.query(sql,
					new Object[] { CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR },
					new BeanPropertyRowMapper(StudentBean.class));

			studentsMap = new HashMap<>();
			if (students.size() > 0) {
				for (StudentBean student : students) {
					studentsMap.put(student.getSapid(), student);
				}
			}
		} catch (DataAccessException e) {
			logger.info(e.getMessage());
		}
		return studentsMap;
	}

	
	// query Student whose program in registration different for current Acads
	// Month and Year
	public HashMap<String, StudentBean> getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear(
			String CURRENT_ACAD_MONTH, String CURRENT_ACAD_YEAR) {
		//System.out.println("jdbcTemplate = " + jdbcTemplate);
		String sql = " select * from exam.students where enrollmentMonth = ? and enrollmentYear = ? and "
				+ " concat(sapid,program) not In ( select concat(sapid,program) from exam.registration where  month = ?  and year = ?) ";

		HashMap<String, StudentBean> studentsMap=new HashMap<String, StudentBean>();
		try {
			ArrayList<StudentBean> students = (ArrayList<StudentBean>) jdbcTemplate.query(sql,
					new Object[] { CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR },
					new BeanPropertyRowMapper(StudentBean.class));

			studentsMap = new HashMap<>();
			if (students.size() > 0) {
				for (StudentBean student : students) {
					studentsMap.put(student.getSapid(), student);
				}
			}
		} catch (DataAccessException e) { 
			logger.info(e.getMessage());
		}
		return studentsMap;
	}

	
	public ArrayList<String> getFedExAllInvalidPinCodes() {
		ArrayList<String> fedExInvalidPincode = new ArrayList<String>();
		String sql = " select pincode from portal.fedex_invalidpincode ";
		fedExInvalidPincode = (ArrayList<String>) jdbcTemplate.queryForList(sql, String.class);
		return fedExInvalidPincode;
	}

	public void batchInsertFedExPincode(final DocumentBean fileBean) {
		//System.out.println("jdbcTemplate = " + jdbcTemplate);

		// Clear FedEx Pincode Table
		String sqlEmptyPicodeTable = "delete from portal.fedex_invalidpincode ";
		jdbcTemplate.update(sqlEmptyPicodeTable);

		String sql = "INSERT INTO portal.fedex_invalidpincode ( pincode,createdDate ) VALUES (?,sysDate())";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				String pincode = fileBean.getFedExInValidPinCodeList().get(i);
				ps.setString(1, pincode);
			}

			@Override
			public int getBatchSize() {
				return fileBean.getFedExInValidPinCodeList().size();
			}
		});

	}

	public ArrayList<StudentBean> batchUpsertStudentMaster(List<StudentBean> studentList) {
		int i = 0;
		ArrayList<StudentBean> errorList = new ArrayList<>();
		for (i = 0; i < studentList.size(); i++) {
			StudentBean bean = studentList.get(i);
			try {
				if("false".equals(bean.getIsReReg())){// Upsert  Fresh Student Admission 
					upsertStudenMaster(bean);
					//System.out.println("Upsert Called : "+ bean.getSapid());
				}else{// Re-Registration Record update 
					updateStudentMaster(bean);
					//System.out.println("Update Called : "+ bean.getSapid());
				}
				// System.out.println("Upserted Student row "+i);
			} catch (Exception e) {
				e.printStackTrace();
				bean.setErrorRecord(true);
				bean.setErrorMessage(e.getMessage());
				bean.setStudentZoneSyncErrorTable("Students");
				errorList.add(bean);
			}
		}
		return errorList;
	}
	
	public ArrayList<String> getAllPrograms() {
		//updated as program list to be taken from program table
		//String sql = "SELECT program FROM exam.programs order by program asc";
		String sql = "SELECT code FROM exam.program  order by code asc";
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;

	}
	
	public ArrayList<String> getAllSubjects() {
		String sql = "SELECT subjectname FROM exam.subjects order by subjectname asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return subjectList;

	}
	
	public ArrayList<String> getAllCenters() {
		String sql = "SELECT * FROM exam.centers  order by centerName asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		ArrayList<String> centersList = new ArrayList<String>();
		ArrayList<CenterBean> centers = (ArrayList<CenterBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterBean.class));
		
		for (int i = 0; i < centers.size(); i++) {
			centersList.add(centers.get(i).getCenterCode());
		}
		
		return centersList;
	}
	
	private void updateStudentMaster(StudentBean bean){
		String sql = " UPDATE exam.students "
				   + "  set program = ? ,"
				   + "  centerCode = ?,"
				   + "	centerName = ?,"
				   + "	validityEndMonth = ?," 
				   + "	validityEndYear = ?,"
				   + "	lastModifiedBy = ?,"
				   + "	lastModifiedDate = sysdate(),"
				   + "  PrgmStructApplicable = ?," 
				   + "  imageUrl = ? ,"
				   + " consumerProgramStructureId =?"
				   + "  where sapid = ? "; 
		
		String program = bean.getProgram();
		String centerCode = bean.getCenterCode();
		String centerName = bean.getCenterName();
		String validityEndMonth = bean.getValidityEndMonth();
		String validityEndYear = bean.getValidityEndYear();
		String lastModifiedBy = bean.getLastModifiedBy();
		String PrgmStructApplicable = bean.getPrgmStructApplicable();
		String imageUrl = bean.getImageUrl();
		String consumerProgramStructureId = bean.getConsumerProgramStructureId();
		String sapid = bean.getSapid();
		
		jdbcTemplate.update(sql,
				      new Object[] { program, centerCode, centerName, validityEndMonth, validityEndYear, lastModifiedBy,
						PrgmStructApplicable, imageUrl , consumerProgramStructureId, sapid 
						});
	}
	
	private void upsertStudenMaster(StudentBean bean) {
		String sql = "INSERT INTO exam.students (sapid, sem, lastName, firstName,"
				+ " middleName, fatherName, husbandName, motherName, gender, program, oldProgram,  enrollmentMonth, enrollmentYear,"
				+ " emailId, mobile, altPhone, dob, regDate, isLateral , address, city, state, "
				+ " country, pin, centerCode, centerName, validityEndMonth, validityEndYear, createdBy, createdDate, "
				+ " lastModifiedBy, lastModifiedDate,PrgmStructApplicable, imageurl,previousStudentId,existingStudentNoForDiscount,consumerProgramStructureId,consumerType,highestQualification) VALUES "
				+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(), ?,?,?,?,?,?,?)"
				+ " on duplicate key update " + "  program = ?,"
				+ "	 centerCode = ?,"
				+ "	 centerName = ?,"
				+ "	 validityEndMonth = ?," 
				+ "	 validityEndYear = ?,"
				+ "	 lastModifiedBy = ?,"
				+ "	 lastModifiedDate = sysdate(),"
				+ "	 PrgmStructApplicable = ?," 
				+ "	 consumerProgramStructureId = ?,"
				+ "  consumerType = ?,"
				+ "  imageUrl = ? "; 
				
		String sapid = bean.getSapid();
		String sem = bean.getSem();
		String lastName = bean.getLastName();
		String firstName = bean.getFirstName();
		String middleName = bean.getMiddleName();
		String fatherName = bean.getFatherName();
		String husbandName = bean.getHusbandName();
		String motherName = bean.getMotherName();
		String gender = bean.getGender();
		String program = bean.getProgram();
		String oldProgram = bean.getOldProgram();
		String enrollmentMonth = bean.getEnrollmentMonth();
		String enrollmentYear = bean.getEnrollmentYear();
		String emailId = bean.getEmailId();
		String mobile = bean.getMobile();
		String altPhone = bean.getAltPhone();
		String dob = bean.getDob();
		String regDate = bean.getRegDate();
		String isLateral = bean.getIsLateral();
		String isReReg = bean.getIsReReg();
		String address = bean.getAddress();
		String city = bean.getCity();
		String state = bean.getState();
		String country = bean.getCountry();
		String pin = bean.getPin();
		String centerCode = bean.getCenterCode();
		String centerName = bean.getCenterName();
		String validityEndMonth = bean.getValidityEndMonth();
		String validityEndYear = bean.getValidityEndYear();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String PrgmStructApplicable = bean.getPrgmStructApplicable();
		String imageUrl = bean.getImageUrl();
		String previousStudentId = bean.getPreviousStudentId();
		String existingStudentNoForDiscount = bean.getExistingStudentNoForDiscount();
		String program_master = bean.getConsumerProgramStructureId();
		String consumerType = bean.getConsumerType();
		String highestQualification = bean.getHighestQualification();

		jdbcTemplate.update(sql,
				new Object[] { sapid, sem, lastName, firstName, middleName, fatherName, husbandName, motherName, gender,
						program, oldProgram, enrollmentMonth, enrollmentYear, emailId, mobile, altPhone, dob, regDate,
						isLateral, address, city, state, country, pin, centerCode, centerName,
						validityEndMonth, validityEndYear, createdBy, lastModifiedBy, PrgmStructApplicable, imageUrl,
						previousStudentId,existingStudentNoForDiscount,program_master,consumerType, highestQualification,
						
						program, centerCode, centerName, validityEndMonth, validityEndYear, lastModifiedBy,
						PrgmStructApplicable,program_master,consumerType, imageUrl });
		
		addSFDC_CS_PurchaseInfo(bean);
	}
	
		//To add record about the product purchased from SFDC
		public void addSFDC_CS_PurchaseInfo(StudentBean studentBean) {

			try {
				if(studentBean.getPurhcasedCSProduct() == null) {
					return;
				}else if(!studentBean.getPurhcasedCSProduct().equals("Career Development")) {
					return;
				}
				String packageName = studentBean.getPurhcasedCSProduct();
				String packageType = "";
				
				packageType = "Normal";
				
				String sql = "INSERT INTO "
						+ "`products`.`purchases_sfdc` "
						+ "("
							+ "`sapid`, `packageName`, `packageType`, `pending`, `addedBy`, `updatedBy`"
						+ ") "
						+ "VALUES"
						+ "("
							+ "?, ?, ?, ?, ?, ?"
						+ ")"
						+ "ON DUPLICATE KEY "
						+ "UPDATE "
						+ "`sapid`=?";
				jdbcTemplate.update(
					sql,
					new Object[] {
						studentBean.getSapid(), packageName, packageType, true, "SalesForce Sync", "SalesForce Sync",
						studentBean.getSapid()
					}
				);
			}catch (Exception e) {
				//If an error is found here, add it to failed initiations table.
				if(studentBean.getPurhcasedCSProduct() == null || !studentBean.getPurhcasedCSProduct().equals("Career Development")) {
					return;
				}
				String packageName = studentBean.getPurhcasedCSProduct();
				String sql = "INSERT INTO `products`.`purchases_failed_initiations`"
						+ "("
							+ "`sapid`, `packageId`, `message`,"
							+ "`addedBy`, `updatedBy`"
						+ ")"
						+ "VALUES"
						+ "("
						+ "?, ?, ?, "
						+ "?, ?"
						+ ") ";

				jdbcTemplate.update(
					sql,
					new Object[] {
						studentBean.getSapid(), "SFDC Sync package : " + packageName, "Error adding package during registration : " + e.getMessage(), 
						"Registration Sync", "Registration Sync"
					}
				);
			}
		}

	public ArrayList<StudentBean> batchUpsertRegistration(List<StudentBean> studentList) {
		int i = 0;
		ArrayList<StudentBean> errorList = new ArrayList<>();

		for (i = 0; i < studentList.size(); i++) {
			StudentBean bean = studentList.get(i);
			String isRecordOfFutureDriveAndOldRegSystem = checkIsRecordOfFutureDriveAndOldRegSystem(bean);
			//System.out.println("In batchUpsertRegistration isRecordOfFutureDriveAndOldRegSystem"+isRecordOfFutureDriveAndOldRegSystem);
			try {
				if("false".equalsIgnoreCase(isRecordOfFutureDriveAndOldRegSystem)) {

					upsertRegistration(bean);
					upsertStatusOfRegistrationInStagingTableEntry(bean);
					idCardService.generateIdCardForStudent(bean);
					
					if(!(TIMEBOUND_PORTAL_LIST.contains(bean.getConsumerProgramStructureId()) || MBAX_PORTAL_LIST.contains(bean.getConsumerProgramStructureId()))) {
						studentCourse.insertIntoStudentSubjectCourse(bean.getSapid()); 
					}
					
					//System.out.println("upsertRegistration Student row "+i);
				} else if("true".equalsIgnoreCase(isRecordOfFutureDriveAndOldRegSystem)) {
					
					upsertRegistrationInStagingTable(bean);
					//System.out.println("upsertRegistrationInStagingTable Student row "+i);
				}else {
					throw new Exception(isRecordOfFutureDriveAndOldRegSystem);
				}
			} catch (Exception e) {
				e.printStackTrace();
				bean.setErrorMessage(e.getMessage());
				bean.setErrorRecord(true);
				bean.setStudentZoneSyncErrorTable("Registration");
				errorList.add(bean);
			}
		}
		return errorList;
	}
	
	/*
	 * returns "true" if  reg is for future drive 
	 * 	and program is not (MBA - X and MBA - WX and M.Sc. (AI & ML Ops)) 
	 * */
	private String checkIsRecordOfFutureDriveAndOldRegSystem(StudentBean bean) {
		String year="";
		String month="";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		
		year = bean.getYear();
		month = bean.getMonth();
		String program ="";
		try {
			program = bean.getProgram().trim();
			if(StringUtils.isBlank(program)){
				return "Got blank value for program for sapid : "+bean.getSapid();
			}
		} catch (Exception e1) {
			return "Got null value for program for sapid : "+bean.getSapid();
		}

	    	if(!"1".equalsIgnoreCase(bean.getSem())) {
	    		
	    		//get latest sem details
	    		StudentBean  latestRegData = getLatestRegistrationDataBySapid(bean.getSapid());
	    		if(StringUtils.isBlank(latestRegData.getSapid())) {
	    			//return "Error in getting latest reg data";
	    			return "false"; //returning false since no data in registration table (student is lateral).
	    		}
	    		if("MBA - X".equalsIgnoreCase(program) || "MBA - WX".equalsIgnoreCase(program) || "M.Sc. (AI & ML Ops)".equalsIgnoreCase(program)) {
	    			return "false";
	    		}else if(TIMEBOUND_PORTAL_LIST.contains(latestRegData.getConsumerProgramStructureId())) { //For PDDM masterkey staging
	    			return compareMonthAndYear(CURRENT_PDDM_ACAD_MONTH, CURRENT_PDDM_ACAD_YEAR, month, year);
	    		}else {
	    			try {
	    				
	    				return compareMonthAndYear(CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR, month, year);
	    				//shifted in common method by Riya
						//get live acads year month
						/*ExamOrderBean examOrderBean =  getExamOrderBeanWhereContentLive();
						String liveAcadMonth = CURRENT_ACAD_MONTH;
						String liveAcadYear = CURRENT_ACAD_YEAR;
						
						Date liveAcadsDate = formatter.parse("01-"+liveAcadMonth+"-"+liveAcadYear+"");
						Date regRecordDate = formatter.parse("01-"+month+"-"+year+"");
						
						//System.out.println("IN checkIsRecordOfCurrentDrive got \n[ regRecordDate : "+regRecordDate+" \n liveAcadsDate : "+liveAcadsDate+" ]"); 
						
						if(regRecordDate.after(liveAcadsDate)) {  
							return "true";
						}else {
							return "false";
						}*/
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						return e.getMessage();
					}
	    			
	    		}
	    		
	    		
	    	}else {
	    		return "false";
	    	}
	    	
			
	    
			
	}
	private void upsertRegistration(StudentBean bean) {
		String sql = "INSERT INTO exam.registration (sapid, program, sem, month, year, createdBy, createdDate, "
				+ " lastModifiedBy, lastModifiedDate,consumerProgramStructureId) VALUES " + "(?,?,?,?,?,?,sysdate(),?,sysdate(),?) "
				+ " on duplicate key update " + " sapid = ?," + "	program = ?," + " sem = ?,"
				+ "	month = ?," + "	 year = ?," + "	 lastModifiedBy = ? , consumerProgramStructureId = ?";
		String sapid = bean.getSapid();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String month = bean.getMonth();
		String year = bean.getYear();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String masterkey = bean.getConsumerProgramStructureId();
		jdbcTemplate.update(sql, new Object[] { sapid, program, sem, month, year, createdBy, lastModifiedBy,masterkey, sapid,
				program, sem, month, year, lastModifiedBy,masterkey });

	}
	
	private void upsertRegistrationInStagingTable(StudentBean bean) {
		String sql = "INSERT INTO exam.registration_staging_future_records (sapid, program, sem, month, year, createdBy, createdDate, "
				+ " lastModifiedBy, lastModifiedDate,consumerProgramStructureId) VALUES " + "(?,?,?,?,?,?,sysdate(),?,sysdate(),?) "
				+ " on duplicate key update " + " sapid = ?," + "	program = ?," + " sem = ?,"
				+ "	month = ?," + "	 year = ?," + "	 lastModifiedBy = ?," + "	 consumerProgramStructureId = ?";

		String sapid = bean.getSapid();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String month = bean.getMonth();
		String year = bean.getYear();

		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
		String masterKey = bean.getConsumerProgramStructureId();
		jdbcTemplate.update(sql, new Object[] { sapid, program, sem, month, year, createdBy, lastModifiedBy,masterKey, sapid,
				program, sem, month, year, lastModifiedBy,masterKey });

	}
	
	private void upsertStatusOfRegistrationInStagingTableEntry(StudentBean bean) {
		/*String sql = "INSERT INTO exam.registration_staging_future_records (sapid, program, sem, month, year, createdBy, createdDate, "
				+ " lastModifiedBy, lastModifiedDate,movedToRegistrationTable) VALUES " + "(?,?,?,?,?,?,sysdate(),?,sysdate(),'Y') "
				+ " on duplicate key update " + " sapid = ?," + "	program = ?," + " sem = ?,"
				+ "	month = ?," + "	 year = ?," + "	 lastModifiedBy = ?, movedToRegistrationTable = 'Y'";*/
		
		String sql = " update exam.registration_staging_future_records "
				+ "		 set lastModifiedDate = sysdate(),lastModifiedBy = ?,program = ?,	month = ?,	 year = ?, movedToRegistrationTable = 'Y',consumerProgramStructureId=? "
				+ "		 where " + " sapid = ? and sem = ?"
				+ "" + " " + "";

		String sapid = bean.getSapid();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String month = bean.getMonth();
		String year = bean.getYear();

		String lastModifiedBy = bean.getLastModifiedBy();
		String masterkey = bean.getConsumerProgramStructureId();
		jdbcTemplate.update(sql, new Object[] { lastModifiedBy, program,  month, year,masterkey,
												sapid,sem });

	}
	
	public Map<String,StudentLearningMetricsBean> getStudentLearningMetricsMap()
	{
		Map<String,StudentLearningMetricsBean> mapOfSapIdAndStudentLearningMetrics = new HashMap<>();
		try{
		String sql = "Select * from portal.student_learning_metrics ";
		ArrayList<StudentLearningMetricsBean> studentLearningMetricsList = (ArrayList<StudentLearningMetricsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentLearningMetricsBean.class));
		
		
		for(StudentLearningMetricsBean bean : studentLearningMetricsList)
		{
			mapOfSapIdAndStudentLearningMetrics.put(bean.getSapid() , bean );
		}
		}catch(Exception e){
			//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			e.printStackTrace();
		}
		return mapOfSapIdAndStudentLearningMetrics;
	}
	
	
	public void updateStudentContactFromSFDC(String sapid, String email, String mobile,String address,	String altPhone,String fatherName,String motherName,String studentImageUrl,String validityEndYear,String validityEndMonth) {
		String sql = "Update exam.students set "
				+ " emailId=?,"
				+ " mobile=?,"
				+ " address=?,"
				+ " altPhone=?,"
				+ " fatherName=?,"
				+ " motherName=?,"
				+ " validityEndYear=?,"
				+ " validityEndMonth=?,"
				+ " lastModifiedBy=?,"
				+ " lastModifiedDate=sysdate()," 
				+ " imageUrl=? "
				+ " where sapid= ? ";



		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					email,
					mobile,
					address,
					altPhone,
					fatherName,
					motherName,
					validityEndYear,
					validityEndMonth,
					sapid,
					studentImageUrl,
					sapid
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	public String updateAllStudentDetailsFromSFDC(StudentDetailsFromSFDCBean bean) {
		/* columns from exam.students
		 * sapid, sem, lastName, firstName, middleName, fatherName, husbandName, motherName,
		 *  gender, program, enrollmentMonth, enrollmentYear, emailId, mobile, altPhone, dob,
		 *   regDate, isLateral, isReReg, address, city, state, country, pin, centerCode, centerName,
		 *    validityEndMonth, validityEndYear, createdBy, createdDate, lastModifiedBy, lastModifiedDate,
		 *     PrgmStructApplicable, updatedByStudent, programChanged, imageUrl, oldProgram, previousStudentId,
		 *      programCleared, programStatus, programRemarks, industry, designation, emailSentProgramCleared,
		 *       existingStudentNoForDiscount, onesignalId, deRegistered, street,locality,landMark,houseNoName
		 * */
		String sql = "Update world.students set " // changed to world for testing purpose.
				+ " sem = ?, lastName=?, firstName=?, fatherName =?," // 4 cols
				+ " husbandName=?, motherName=?, gender=?, program=?, enrollmentMonth=?," // 5 cols
				+ " enrollmentYear=?, emailId=?, mobile=?, altPhone=?, dob=?, " // 5 cols
				//+ " regDate=?, isLateral=?, isReReg=?, address=?, city=?, " // 5 cols
				+ " regDate=?, isLateral=?, address=?, city=?, " // 4 cols
				//+ " state=?, country=?, pin=?, centerCode=?, centerName=?," // 5 cols
				+ " state=?, country=?, pin=?, centerCode=?," // 4 cols
				+ " validityEndMonth=?, validityEndYear=?, lastModifiedBy='SFDC api', lastModifiedDate=sysdate(), PrgmStructApplicable=?, " // 3 cols
				+ " programChanged=?, imageUrl=?, oldProgram=?, previousStudentId=?, " // 4 cols
				//+ " programStatus=?, " // 1 cols
			//	+ " existingStudentNoForDiscount=?, deRegistered=?," // 2 cols
				+ " deRegistered=?," // 1 cols
				+ " street=?, locality=? , landMark=?, houseNoName=?," //4 
			//	+ " totalExperience=?, annualSalary=?, companyName=?, ugQualification=?, age=?, highestQualification=?, industry=?, designation=?" // 8 cols
				+ " age=?, highestQualification=?" // 2 cols
				+ " where sapid= ? ";

		
		String error="";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			/* properties changed for StudentDetailsFromSFDCBean to be used in below update query
	//enrollmentMonth 
	private String session;
	
	//enrollmentYear
	private String year;
	
	//mobile	
	private String mobileNo;
	
	//regDate	
	private String accountConfirmDate;
	
	//imageUrl	
	private String studentImage;
	
			 * */
			jdbcTemplate.update(sql, new Object[] { 
				bean.getSem(),bean.getLastName(),bean.getFirstName(),bean.getFatherName(), //4
				bean.getHusbandName(),bean.getMotherName(),bean.getGender(),bean.getProgram(),bean.getSession(), // 5 
				bean.getYear(),bean.getEmailId(),bean.getMobileNo(),bean.getAltPhone(),bean.getDob(), // 5 
			//	bean.getAccountConfirmDate(),bean.getIsLateral(),bean.getIsReReg(),bean.getAddress(),bean.getCity(), // 5 
				bean.getRegDate(),bean.getIsLateral(),bean.getAddress(),bean.getCity(),
			//	bean.getState(),bean.getCountry(),bean.getPin(),bean.getCenterCode(),bean.getCenterName(), // 5 
				bean.getState(),bean.getCountry(),bean.getPin(),bean.getCenterCode(),
				bean.getValidityEndMonth(),bean.getValidityEndYear(),bean.getPrgmStructApplicable(),// 3
				bean.getProgramChanged(),bean.getStudentImage(),bean.getOldProgram(),bean.getPreviousStudentId(), // 4 
			//	bean.getProgramStatus(), // 1 
			//	bean.getExistingStudentNoForDiscount(),bean.getDeRegistered(), // 2 
				bean.getDeRegistered(),
				bean.getStreet(),bean.getLocality(),bean.getLandMark(),bean.getHouseNoName(),// 4
			//	bean.getTotalExperience(),bean.getAnnualSalary(),bean.getCompanyName(),bean.getUgQualification(),bean.getAge(),bean.getHighestQualification(),bean.getIndustry(),bean.getDesignation(), // 8 
				bean.getAge(),bean.getHighestQualification(), // 8 
				bean.getSapid() //for where clause
			});
		}catch(Exception e){
			e.printStackTrace();
			return error="Error while updating in database. Error: "+e.getMessage();
		}
		return error;
	}

	
	public StudentBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentBean student = null;
		try{
			String sql = "SELECT * FROM exam.students s where "
					+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";
			//System.out.println("SQL = "+sql);
			student = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentBean.class));
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}
	}
	
	
	public HashMap<String, StudentBean> getAllRegistrationDataTable(){
		HashMap<String, StudentBean> studentsMap = new HashMap<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.registration ";
		try{
			ArrayList<StudentBean> students = (ArrayList<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			if (students.size() > 0) {
				for (StudentBean student : students) {
					String key= student.getSapid()+"|"+student.getProgram()+"|"+student.getSem()+"|"+student.getYear()+"|"+student.getMonth();
					if(!studentsMap.containsKey(key)){
						studentsMap.put(key, student);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return studentsMap;
		}
		return studentsMap;
	}

	
	
	public HashMap<String, String> getAllConsumerTypes(){
		HashMap<String, String> consumerTypeList = new HashMap<String, String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM exam.consumer_type ";
		try{
			ArrayList<StudentBean> consumerList = (ArrayList<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			if (consumerList.size() > 0) {
				for (StudentBean consumer : consumerList) {
					String key= consumer.getId();
					if(!consumerTypeList.containsKey(key)){
						consumerTypeList.put(key, consumer.getName());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return consumerTypeList;
		}
		return consumerTypeList;
	}
	
	public HashMap<String, String> getAllProgramStructureTypes(){
		HashMap<String, String> prgmstrucTypeList = new HashMap<String, String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM exam.program_structure ";
		try{
			ArrayList<StudentBean> prgmstructList = (ArrayList<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			if (prgmstructList.size() > 0) {
				for (StudentBean prgmStruct : prgmstructList) {
					String key= prgmStruct.getId();
					if(!prgmstrucTypeList.containsKey(key)){
						prgmstrucTypeList.put(key, prgmStruct.getProgram_structure());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return prgmstrucTypeList;
		}
		return prgmstrucTypeList;
	}
	
	public HashMap<String, String> getAllProgramTypes(){
		HashMap<String, String> prgmTypeList = new HashMap<String, String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM exam.program ";
		try{
			ArrayList<StudentBean> prgmList = (ArrayList<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			if (prgmList.size() > 0) {
				for (StudentBean prgm : prgmList) {
					String key= prgm.getId();
					if(!prgmTypeList.containsKey(key)){
						prgmTypeList.put(key, prgm.getCode());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return prgmTypeList;
		}
		return prgmTypeList;
	}
	
	public HashMap<String, String> getConsumerMasterList(){
		HashMap<String, String> consumerMasterTypeList = new HashMap<String, String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM exam.consumer_program_structure ";
		try{
			ArrayList<StudentBean> consumerList = (ArrayList<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			if (consumerList.size() > 0) {
				for (StudentBean consumer : consumerList) {
					String key= consumer.getProgramId()+"|"+consumer.getProgramStructureId()+"|"+consumer.getConsumerTypeId();
					if(!consumerMasterTypeList.containsKey(key)){
						consumerMasterTypeList.put(key, consumer.getId());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return consumerMasterTypeList;
		}
		return consumerMasterTypeList;
	}

	public List<StudentBean> getRegistrationDataFromStagingTable(){
		HashMap<String, StudentBean> studentsMap = new HashMap<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.registration_staging_future_records where movedToRegistrationTable = 'N' ";
		List<StudentBean> students = new ArrayList<>();
		try{
			students = (List<StudentBean>) jdbcTemplate.query(sql,new Object[] { },new BeanPropertyRowMapper(StudentBean.class));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return students;
	}
	

	public StudentBean getLatestRegistrationDataBySapid(String sapid){
		HashMap<String, StudentBean> studentsMap = new HashMap<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.registration r where r.sapid = ? and r.sem = (select max(sem) from exam.registration where sapid = r.sapid )  ";
		StudentBean latestRegData = new StudentBean();
		try{
			latestRegData = (StudentBean) jdbcTemplate.queryForObject(sql,new Object[] {sapid},new BeanPropertyRowMapper(StudentBean.class));
		}catch(Exception e){
			e.printStackTrace();
		}
		return latestRegData;
	}
	
	public ExamOrderBean getExamOrderBeanWhereContentLive(){
		ExamOrderBean examOrder = null;
		try{

			String sql = "SELECT * FROM exam.examorder e where e.order = (select max(eo.order) from exam.examorder eo where eo.acadContentLive = 'Y')";

			examOrder =  (ExamOrderBean)jdbcTemplate.queryForObject(sql,new Object[]{}, new BeanPropertyRowMapper(ExamOrderBean.class));
			return examOrder;

		}catch(Exception e){
			e.printStackTrace();
			return examOrder;
		}
		}
	public String getNoOfSubjectsPassedInPassFailBySapid(String sapId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select count(*) from exam.passfail where sapid = ? and isPass= 'Y' ";
		try{
			int recordCount = jdbcTemplate.queryForObject(sql,new Object[]{sapId},Integer.class);
			//System.out.println("Record count :"+recordCount);
			return ""+recordCount;
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}

	}
	public String getNoOfSubjectsToClearProgramBySapid(String sapId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		/*String sql = " SELECT  " + 
					"    p.noOfSubjectsToClear " + 
					"FROM " + 
					"    exam.programs p, " + 
					"    exam.students s " + 
					"WHERE " + 
					"    p.program = s.program " + 
					"        AND p.programStructure = s.PrgmStructApplicable " + 
					"        AND s.sapid = ? " + 
					"        AND s.sem = (SELECT  " + 
					"            MAX(sem) " + 
					"        FROM " + 
					"            exam.students " + 
					"        WHERE " + 
					"            sapid = s.sapid) ";*/
		String sql = " SELECT  " + 
				"    p.noOfSubjectsToClear " + 
				"FROM " + 
				"    exam.programs p, " + 
				"    exam.students s " + 
				"WHERE " + 
				"    p.consumerProgramStructureId = s.consumerProgramStructureId " + 
				//"        AND p.programStructure = s.PrgmStructApplicable " + 
				"        AND s.sapid = ? " + 
				"        AND s.sem = (SELECT  " + 
				"            MAX(sem) " + 
				"        FROM " + 
				"            exam.students " + 
				"        WHERE " + 
				"            sapid = s.sapid) ";
		try{
			int recordCount = jdbcTemplate.queryForObject(sql,new Object[]{sapId},Integer.class);
			//System.out.println("Record count :"+recordCount);
			return ""+recordCount;
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}

	}

	public StudentBean getPassedYearMonthBySapid(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentBean student = null;
		try{
			//updated as program list to be taken from program table
			/*String sql = "				SELECT     " + 
					"					    year,    " + 
					"					    month,    " + 
					"					    STR_TO_DATE(CONCAT('01-', month, '-', year),    " + 
					"					            '%d-%b-%Y') AS date, " + 
					"						programname  " + 
					"					FROM    " + 
					"					    exam.marks m,exam.programs p,exam.students s    " + 
					"					WHERE    " + 
					"					    m.sapid = ?  " + 
					"                        and  m.sapid = s.sapid " + 
					"                        and s.sem = (Select max(sem) from exam.students where sapid = s.sapid ) " + 
					"                        and m.program = p.program " + 
					"                        and s.PrgmStructApplicable = p.programStructure " + 
					"					GROUP BY year , month    " + 
					"					ORDER BY date DESC    " + 
					"					LIMIT 1 ";*/
			String sql = "				SELECT     " + 
					"					    year,    " + 
					"					    month,    " + 
					"					    STR_TO_DATE(CONCAT('01-', month, '-', year),    " + 
					"					            '%d-%b-%Y') AS date, " + 
					"						programname  " + 
					"					FROM    " + 
					"					    exam.marks m,exam.programs p,exam.students s    " + 
					"					WHERE    " + 
					"					    m.sapid = ?  " + 
					"                        and  m.sapid = s.sapid " + 
					"                        and s.sem = (Select max(sem) from exam.students where sapid = s.sapid ) " + 
					"                        and m.program = p.program " + 
					"                        and s.PrgmStructApplicable = p.programStructure " + 
					"                        and s.consumerProgramStructureId = p.consumerProgramStructureId " + 
					"					GROUP BY year , month    " + 
					"					ORDER BY date DESC    " + 
					"					LIMIT 1 ";
			student = (StudentBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid
			}, new BeanPropertyRowMapper(StudentBean.class));
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}

	}
	

public ArrayList<String> getAllProgramStructureList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> programStructureList = new ArrayList<String>();
		try{
			String str="select program_structure from exam.program_structure";
			programStructureList=(ArrayList<String>)jdbcTemplate.query(str,new SingleColumnRowMapper(String.class));
		}catch(Exception e){
			//System.out.println("Error in fetching program structure list");
			e.printStackTrace();
		}
		return programStructureList;
	}
    //@Async
	@Transactional(readOnly = false)
	public void updateLastSyncedTime(SchedulerApisBean bean) {
	String sql = "update portal.scheduler_apis set lastSync = NOW(),error=? where syncType = ?";
	//jdbcTemplate = new JdbcTemplate(dataSource);
	try{
		//System.out.println(bean.getError()+":"+bean.getSyncType());
	jdbcTemplate.update(sql, new Object[] { 
			bean.getError(),
			bean.getSyncType() 
	});
	}
	catch(Exception e)
	{
		logger.info("Exception in updateLastSyncedTime: "+e);
		//System.out.println(e);
		e.printStackTrace();
	} 
	}

	public List<StudentBean> getStudentsRegisteredForCurrentCycle(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentBean> studentList = new ArrayList<StudentBean>();
		try{
			String sql="select * from exam.registration where year=? and month=? ";
			studentList = jdbcTemplate.query(sql,new Object[] {year,month},new BeanPropertyRowMapper(StudentBean.class));
		}catch(Exception e){
			//System.out.println("Error in fetching program structure list");
			e.printStackTrace();
		}
		return studentList;
	}
	
	//Made a separate method to check month year is in future staging or not
		public String compareMonthAndYear(String liveAcadMonth,String liveAcadYear,String month,String year) {
			//get live acads year month
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			ExamOrderBean examOrderBean =  getExamOrderBeanWhereContentLive();
			
			
			Date liveAcadsDate = formatter.parse("01-"+liveAcadMonth+"-"+liveAcadYear+"");
			Date regRecordDate = formatter.parse("01-"+month+"-"+year+"");
			
			//System.out.println("IN checkIsRecordOfCurrentDrive got \n[ regRecordDate : "+regRecordDate+" \n liveAcadsDate : "+liveAcadsDate+" ]"); 
			
			if(regRecordDate.after(liveAcadsDate)) {  
				return "true";
			}else {
				return "false";
			}
			
			}catch(Exception e) {
				return e.getMessage();
			}
			
		}
}
