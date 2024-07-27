package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import com.nmims.dto.DissertationResultProcessingDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.ApplozicGroupBean;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ConsumerType;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.FailedSubjectCountCriteriaBean;
import com.nmims.beans.GroupExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramStructureBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.SubjectBean;
import com.nmims.beans.SubjectGroupsBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.helpers.PaginationHelper;

@Repository("dashboardDAO")
public class DashboardDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	 private PlatformTransactionManager transactionManager;
	private static HashMap<String, Integer> hashMap = null;
	private TransactionStatus status;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value( "${CURRENT_MBAWX_ACAD_MONTH}" )
	private String CURRENT_MBAWX_ACAD_MONTH;

	@Value( "${CURRENT_MBAWX_ACAD_YEAR}" )
	private String CURRENT_MBAWX_ACAD_YEAR;
	
    private static final Logger logger = LoggerFactory.getLogger(DashboardDAO.class);
    
    public void endTransaction(boolean activity) {
		if(activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}

	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}

    @Transactional(readOnly = true)
	public HashMap<String, Integer> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
			hashMap = new HashMap<String, Integer>();
			for (ExamOrderExamBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear(), Integer.valueOf(row.getOrder()));
			}
		}
		return hashMap;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	 public void setTransactionManager( PlatformTransactionManager transactionManager)
	 {    
	   this.transactionManager = transactionManager;  
	 }
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT facultyId FROM acads.faculty where active = 'Y' order by firstname, lastname asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> facultyList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, FacultyExamBean> getFacultiesMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, FacultyExamBean> map = new HashMap<>();
		String sql = "SELECT * FROM acads.faculty  ";

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		for (FacultyExamBean facultyBean : facultyList) {
			map.put(facultyBean.getFacultyId(), facultyBean);
		}
		return map;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getFacultyIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> map = new HashMap<>();
		String sql = "SELECT * FROM acads.faculty order by firstName, lastName asc ";

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		for (FacultyExamBean facultyBean : facultyList) {
			map.put(facultyBean.getFacultyId(), facultyBean.getFirstName().trim() + " " + facultyBean.getLastName().trim());
		}
		return map;
	}
	@Transactional(readOnly = true)
	public String getSpecialisationIdFromName(String specialisationName) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="SELECT   id" + 
				"				FROM  " + 
				"				    `exam`.`specialization_type`  " + 
				"				WHERE  " + 
				"				    `specializationType` =?  " +
				" AND id!=9";
		String sId= (String)jdbcTemplate.queryForObject(sql, new Object[] {specialisationName},String.class);
		return sId;
	}
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFaculties() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' order by firstname, lastname asc ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = false)
	public void insertFaculty(FacultyExamBean faculty) {
		final String sql = "INSERT INTO acads.faculty ( "
				+ " facultyId, firstname, lastname, email, mobile, active, createdBy, createdDate, lastModifiedBy, lastModifiedDate "
				+ ") "
				+ " VALUES( ?,?,?,?,?,'Y',?,sysdate(),?, sysdate()) ";

		jdbcTemplate = new JdbcTemplate(dataSource);


		final FacultyExamBean bean = faculty;
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, bean.getFacultyId());
				ps.setString(2, bean.getFirstName());
				ps.setString(3, bean.getLastName());
				ps.setString(4, bean.getEmail());
				ps.setString(5, bean.getMobile());
				ps.setString(6, bean.getCreatedBy());
				ps.setString(7, bean.getCreatedBy());
		
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//jdbcTemplate.update(psc);
		jdbcTemplate.update(psc, keyHolder);
		
		int id = keyHolder.getKey().intValue();
		
		faculty.setId(id+"");
		
	}
	
	@Transactional(readOnly = true)
	public FacultyExamBean findByName(String id) {
		String sql = "SELECT * FROM acads.faculty WHERE id = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyExamBean faculty = (FacultyExamBean) jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper(FacultyExamBean.class));

		return faculty;
	}
	
	@Transactional(readOnly = false)
	public void updateFaculty(FacultyExamBean faculty) {
		String sql = "Update acads.faculty set "

				+ "firstName = ?,"
				+ "lastName = ?,"
				+ "mobile = ?,"
				+ "email = ?"

				+ " where id='"+faculty.getId()+"'";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				faculty.getFirstName(),
				faculty.getLastName(),
				faculty.getMobile(),
				faculty.getEmail()


		});
		
	}
	
	@Transactional(readOnly = false)
	public void deactivateFaculty(FacultyExamBean faculty) {
		String sql = "Update acads.faculty set "
				+ "active = 'N' "
				+ " where id = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] {faculty.getId() });
		
	}
	
	@Transactional(readOnly = true)
	public Page<FacultyExamBean> getFacultyPage(int pageNo, int pageSize,	FacultyExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM acads.faculty  where 1 = 1 ";
		String countSql = "SELECT count(*) FROM acads.faculty  where 1 = 1  ";

		if( searchBean.getFacultyId() != null &&   !("".equals(searchBean.getFacultyId()))){
			sql = sql + " and facultyId like  ? ";
			countSql = countSql + " and facultyId like  ? ";
			parameters.add("%"+searchBean.getFacultyId()+"%");
		}
		if( searchBean.getFirstName() != null &&   !("".equals(searchBean.getFirstName()))){
			sql = sql + " and firstName like  ? ";
			countSql = countSql + " and firstName like  ? ";
			parameters.add("%"+searchBean.getFirstName()+"%");
		}
		if( searchBean.getLastName() != null &&   !("".equals(searchBean.getLastName()))){
			sql = sql + " and lastName like  ? ";
			countSql = countSql + " and lastName like  ? ";
			parameters.add("%"+searchBean.getLastName()+"%");
		}
		
		
		sql = sql + " order by firstName asc";

		Object[] args = parameters.toArray();

		PaginationHelper<FacultyExamBean> pagingHelper = new PaginationHelper<FacultyExamBean>();
		Page<FacultyExamBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(FacultyExamBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesWithAssignmentCount(AssignmentFileBean searchBean, String level) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = null;
		
		if("1".equalsIgnoreCase(level)){
			sql = "select asb.facultyId, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.facultyId = f.facultyId  group by asb.facultyId order by firstname, lastname asc";
		}else if("2".equalsIgnoreCase(level)){
			sql = "select asb.faculty2, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.faculty2 = f.facultyId  group by asb.faculty2 order by firstname, lastname asc";
		}else if("3".equalsIgnoreCase(level)){
			sql = "select asb.faculty3, firstname, lastname ,count(*) as assignmentsAllocated "
					+ " from exam.assignmentsubmission asb, acads.faculty f where year = ? and month = ? "
					+ " and asb.faculty3 = f.facultyId  group by asb.faculty3 order by firstname, lastname asc";
		}
		
		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new Object[]{
				searchBean.getYear(),
				searchBean.getMonth()
		} , new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FacultyExamBean> getFacultiesWithProjectCount(AssignmentFileBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = sql = "select psb.facultyId, firstname, lastname ,count(*) as assignmentsAllocated "
				+ " from exam.projectsubmission psb, acads.faculty f where year = ? and month = ? "
				+ " and psb.facultyId = f.facultyId  group by psb.facultyId order by firstname, lastname asc";
		
		ArrayList<FacultyExamBean> facultyList = (ArrayList<FacultyExamBean>) jdbcTemplate.query(sql, new Object[]{
				searchBean.getYear(),
				searchBean.getMonth()
		} , new BeanPropertyRowMapper(FacultyExamBean.class));
		return facultyList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<PassFailExamBean> getPassFailByLCIC(StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String lc = student.getLc();
		String ic = student.getCenterCode();
		String year = student.getYear();
		String month = student.getMonth();
		String program = student.getProgram();
		String subject = student.getSubject();
		String sem = student.getSem();
		
		String sql = "Select * from exam.passfail pf, exam.students s, exam.centers c  where s.sapid = pf.sapid "
				+ " and s.centerCode = c.centerCode ";
		
		if(lc != null && !"".equals(lc)){
			sql = sql + " and c.lc = ? ";
			parameters.add(lc);
		}
		
		if(ic != null && !"".equals(ic)){
			sql = sql + " and s.centerCode = ? ";
			parameters.add(ic);
		}
		
		if( year != null &&   !("".equals(year))){
			sql = sql + " and (pf.writtenyear = ?  OR pf.assignmentyear = ?) ";
			//countSql = countSql + " and (ps.writtenyear = ?  OR ps.assignmentyear = ?) ";
			parameters.add(year);
			parameters.add(year);
		}
		if( month != null &&   !("".equals(month))){
			sql = sql + " and (pf.writtenmonth = ? OR pf.assignmentmonth = ? ) ";
		//	countSql = countSql + " and (ps.writtenmonth = ? OR ps.assignmentmonth = ? ) ";
			parameters.add(month);
			parameters.add(month);
		}
		
		if( subject != null &&   !("".equals(subject))){
			sql = sql + " and pf.subject = ? ";
			parameters.add(subject);
		}
		
		if( program != null &&   !("".equals(program))){
			sql = sql + " and pf.program = ? ";
			parameters.add(program);
		}
		
		if( sem != null &&   !("".equals(sem))){
			sql = sql + " and pf.sem = ? ";
			parameters.add(sem);
		}
		
		Object[] args = parameters.toArray();
		
		ArrayList<PassFailExamBean> passFailList = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(PassFailExamBean.class));
		
		return passFailList;
	}


	//added because Of CRUD ProgramSubject :START
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean insertProgramSubjectContent(final ProgramSubjectMappingExamBean bean,JdbcTemplate jdbcTemplate) {
		
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
			    @Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement statement = con.prepareStatement("INSERT INTO exam.program_subject"
			        		+ " ( program, subject, sem, prgmStructApplicable, active,"
			        		+ " passScore,hasAssignment,assignmentNeededBeforeWritten ,writtenScoreModel , assignmentScoreModel ,"
			        		+ " createCaseForQuery, assignQueryToFaculty ,"

			        		+ " isGraceApplicable, maxGraceMarks, sifySubjectCode,"
			        		+ " createdBy,createdDate, lastModifiedBy, lastModifiedDate,hasTest,hasIA,"
			        		+ " studentType,specializationType,subjectCredits) "
			        		+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);

			        		/*+ " isGraceApplicable, maxGraceMarks, sifySubjectCode, studentType,"
			        		+ " createdBy,createdDate, lastModifiedBy, lastModifiedDate,specializationType) "
			        		+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?) ", Statement.RETURN_GENERATED_KEYS);*/

			        statement.setString(1, bean.getProgram());
			        statement.setString(2, bean.getSubject());
			        statement.setString(3, bean.getSem());
			        statement.setString(4, bean.getPrgmStructApplicable());
			        statement.setString(5, bean.getActive());
			        statement.setInt(6, bean.getPassScore());
			        statement.setString(7, bean.getHasAssignment());
			        statement.setString(8, bean.getAssignmentNeededBeforeWritten());
			        statement.setString(9, bean.getWrittenScoreModel());
			        statement.setString(10, bean.getAssignmentScoreModel());
			        statement.setString(11, bean.getCreateCaseForQuery());
			        statement.setString(12, bean.getAssignQueryToFaculty());
			        statement.setString(13, bean.getIsGraceApplicable());
			        statement.setInt(14, bean.getMaxGraceMarks());
			        statement.setInt(15, bean.getSifySubjectCode());

			        statement.setString(16, bean.getCreatedBy());
			        statement.setString(17, bean.getLastModifiedBy());
			        statement.setString(18, bean.getHasTest());
			        statement.setString(19, bean.getHasIA());
			        statement.setString(20, bean.getStudentType());
			        statement.setString(21, bean.getSpecializationType());
			        statement.setDouble(22, bean.getSubjectCredits());

			        /*statement.setString(16, bean.getStudentType());
			        statement.setString(17, bean.getCreatedBy());
			        statement.setString(18, bean.getLastModifiedBy());
			        statement.setString(19, bean.getSpecializationType());*/

			        return statement;
			    }
			}, holder);

			return true;
		} catch (Exception e) {
			
			return true;
		}
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> insertPrograms(ProgramsBean program) {
		
		HashMap<String,String> message = new HashMap<String,String>();
		
		try
		{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "insert into exam.programs"
				+ "(`program`, "
				+ "`programname`,"
				+ "`programcode`, "
				+ "`programDuration`,"
				+ "`programDurationUnit`, "
				+ "`programType`, "
				+ "`noOfSubjectsToClear`, "
				+ "`noOfSubjectsToClearLateral`, "
				+ "`programStructure`, "
				+ "`examDurationInMinutes`, "
				+ "`noOfSemesters`, "
				+ "`noOfSubjectsToClearSem`, "
				+ "`consumerProgramStructureId`, "
				+ "`active`,`description`,`createdBy`,`lastModifiedBy`) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(SQL,new Object[] {
		program.getProgram().trim(),
		program.getProgramname().trim(),
		program.getProgramcode().trim(),
		program.getProgramDuration().trim(),
		program.getProgramDurationUnit().trim(),
		program.getProgramType().trim(),
		program.getNoOfSubjectsToClear().trim(),
		program.getNoOfSubjectsToClearLateral().trim(),
		program.getProgramStructure().trim(),
		program.getExamDurationInMinutes().trim(),
		program.getNoOfSemesters().trim(),
		program.getNoOfSubjectsToClearSem().trim(),
		program.getConsumerProgramStructureId().trim(),
		program.getActive().trim(),
		program.getDescription(),
		program.getCreatedBy(),
		program.getLastModifiedBy()
		});
		message.put("success", "Successfully Program Inserted");
		return message;
		}
		catch (Exception e) {
		// TODO: handle exception
		
		message.put("error", "Error While Inserting Program: " + e.getMessage());
		return message;
		}
		}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateProgramsEntry(final ProgramsBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "update exam.programs set "
				+ " programcode= ? , "
				+ " programDuration= ? , "
				+ " programDurationUnit = ? ,"
				+ " programType= ? , "
				+ " noOfSubjectsToClear= ? ,"
				+ " noOfSubjectsToClearLateral= ? ,"
				+ " examDurationInMinutes= ? ,"
				+ " noOfSemesters=  ?,"
				+ " noOfSubjectsToClearSem = ?," 
				+ " active = ?,"
				+ " description=?, "
				+ " lastModifiedBy = ?, "
				+ " lastModifiedDate = current_timestamp() "
				+ " where consumerProgramStructureId  = ?";
		
		try {
			jdbcTemplate.update(sql, new Object[] { 
				bean.getProgramcode().trim(),
				bean.getProgramDuration().trim(),
				bean.getProgramDurationUnit().trim(),
				bean.getProgramType().trim(),
				bean.getNoOfSubjectsToClear().trim(),
				bean.getNoOfSubjectsToClearLateral().trim(),
				bean.getExamDurationInMinutes().trim(),
				bean.getNoOfSemesters().trim(),
				bean.getNoOfSubjectsToClearSem().trim(),
				bean.getActive().trim(),
				bean.getDescription(),
				bean.getLastModifiedBy(),
				bean.getConsumerProgramStructureId().trim()
			
			});
			
			/*HashMap<String,String> statusMap = updateProgramTableEntry(bean);
			if(statusMap.containsKey("error")){
				message.put("error", "UnSuccessfull Program Not Inserted");
				return message;
			}*/
			message.put("success", "Successfully Program Inserted");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			message.put("error", "UnSuccessfull Program Not Inserted");
		}
		return message;
	}

	@Transactional(readOnly = false)
	public HashMap<String,String> insertIntoProgramTable(ProgramExamBean program) {
		HashMap<String,String> message = new HashMap<String,String>();
		try
		{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "INSERT INTO exam.program(`code`, `name`, `specializationId`,`specializationType`, `modeOfLearning`,`createdBy`,`createdDate`,`lastModifiedBy`,`lastModifiedDate`)" + 
				"VALUES (?, ?,?, ?, ?,?,sysdate(),?,sysdate())";
		jdbcTemplate.update(SQL,new Object[] {
		program.getCode(),
		program.getName(),
		program.getSpecializationId() ,
		program.getSpecialization(),
		program.getModeOfLearning(),
		program.getCreatedBy(),
		program.getLastModifiedBy()
		
		});
		message.put("success", "Successfully Program Inserted");
		return message;
		}
		catch (Exception e) {
		// TODO: handle exception
		
		message.put("error", "Error While Inserting Program: " + e.getMessage());
		return message;
		}
	}
	
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateProgramTableEntry(final ProgramExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		System.out.println("This is the bean"+bean);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "UPDATE exam.program SET specializationId=?,code = ?, name = ?, specializationType=? , modeOfLearning=? , lastModifiedBy = ?, lastModifiedDate = sysdate() WHERE id = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] { 
				bean.getSpecializationId(),
				bean.getCode().trim(),
				bean.getName().trim(),
				bean.getSpecializationType(),
				bean.getModeOfLearning(),
				bean.getLastModifiedBy(),
				bean.getId()
			});
			message.put("success", "Successfully Program Inserted");
		} catch (DataAccessException e) {
//			e.printStackTrace();
			message.put("error", "UnSuccessfull Program Not Inserted");
		}
		return message;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> programSubjectList(ProgramSubjectMappingExamBean programSubjectMappingBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.program_subject " ;
		ArrayList<ProgramSubjectMappingExamBean> programSubjectList = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return programSubjectList;
	}

	

	@Transactional(readOnly = false)
	public void updateProgramSubjectsEntry(final ProgramSubjectMappingExamBean bean,JdbcTemplate jdbcTemplate) {
		
		
		String sql = "update exam.program_subject set "
				+ " active = ?, "
				+ " passScore = ? , "
				+ " hasAssignment= ? , "
				+ " assignmentNeededBeforeWritten= ? , "
				+ " writtenScoreModel= ? , "
				+ " assignmentScoreModel = ? ,"
				+ " isGraceApplicable= ? , "
				+ " maxGraceMarks= ? ,"
				+ " assignQueryToFaculty= ? ,"
				+ " createCaseForQuery= ? ,"
				+ " sifySubjectCode= ? ,"
				+ " lastModifiedDate=  sysdate(),"
				+ " hasTest = ?,"

				+ " hasIA = ?,"
				+ " studentType = ?, "

				+ " lastModifiedBy = ? ,"
				+ " specializationType =?, subjectCredits = ? "

				+ " where program  = ? and subject= ? and prgmStructApplicable = ? and sem = ? ";
		
		try {
			jdbcTemplate.update(sql, new Object[] { 
				
				
				bean.getActive().trim(),
				bean.getPassScore(),
				bean.getHasAssignment().trim(),
				bean.getAssignmentNeededBeforeWritten().trim(),
				bean.getWrittenScoreModel().trim(),
				bean.getAssignmentScoreModel().trim(),
				bean.getIsGraceApplicable().trim(),
				bean.getMaxGraceMarks(),
				bean.getAssignQueryToFaculty().trim(),
				bean.getCreateCaseForQuery().trim(),
				bean.getSifySubjectCode(),

				bean.getHasTest(),
				bean.getHasIA(),
				bean.getStudentType(),
				bean.getLastModifiedBy(),
				bean.getSpecializationType(),
				bean.getSubjectCredits(),
				
				bean.getProgram(),
				bean.getSubject(),
				bean.getPrgmStructApplicable(),
				bean.getSem()

				/*bean.getHasTest().trim(),
				bean.getLastModifiedBy().trim(),
				bean.getSpecializationType(),
				bean.getProgram().trim(),
				bean.getSubject().trim(),
				bean.getPrgmStructApplicable().trim(),
				bean.getSem().trim()*/

			});
			//updateIntoProgramSemSubject(bean,jdbcTemplate);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
	}
	
	@Transactional(readOnly = true)
	public ProgramSubjectMappingExamBean findProgramSubjectEntry(ProgramSubjectMappingExamBean bean) {
		String sql = "SELECT * FROM exam.program_subject where program=? and subject= ? and sem = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		ProgramSubjectMappingExamBean session = (ProgramSubjectMappingExamBean) jdbcTemplate.queryForObject(
				sql, new Object[] {bean.getProgram(),
						bean.getSubject(),bean.getSem()}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));

		return session;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getAllProgramSubjectList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select p_s_s.*,p.code as program, "
				+ "p_s.program_structure as prgmStructApplicable,"
				+ "c_t.name as consumerType ,"
				+ " s_t.specializationType as specializationName from exam.program_sem_subject as p_s_s "
				+ "left join exam.consumer_program_structure as c_p_s on c_p_s.id = p_s_s.consumerProgramStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "left join exam.specialization_type as s_t on s_t.id = p_s_s.specializationType";
		ArrayList<ProgramSubjectMappingExamBean> psList = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return psList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramsBean> getAllProgramsList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.programs" ;
		ArrayList<ProgramsBean> psList = (ArrayList<ProgramsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsBean.class));
		return psList;
	}
	
	@Transactional(readOnly = true)
	public ProgramsBean getSingleProgramDetails(String program) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.programs where program =?" ;
		ProgramsBean psList = (ProgramsBean)jdbcTemplate.queryForObject(sql, new Object[]{program}, new BeanPropertyRowMapper(ProgramsBean.class));
		return psList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProgramListFromProgramMaster() {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select distinct p.program from exam.programs p ";
		ArrayList<String> programListFromProgramMaster=(ArrayList<String>)jdbcTemplate.
				query(sql, new SingleColumnRowMapper(String.class));
		
		return programListFromProgramMaster;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProgStructListFromProgramMaster() {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select distinct  p.programStructure from exam.programs p ";
		ArrayList<String> programStructListFromProgramMaster=(ArrayList<String>)jdbcTemplate.
				query(sql, new SingleColumnRowMapper(String.class));
		
		return programStructListFromProgramMaster;
	}
	//END
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramStructureBean> getProgramStructureList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.program_structure;";
		ArrayList<ProgramStructureBean> programStructureBean = (ArrayList<ProgramStructureBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramStructureBean.class));
		return programStructureBean;
	}
	



	@Transactional(readOnly = false)
	public String insertSubject(SubjectBean subject) {
		try
		{
			jdbcTemplate=new JdbcTemplate(dataSource);
			String SQL = "insert into exam.subjects(`subjectname`,`subjectbbcode`,`commonSubject`) values(?,?,?)";
			jdbcTemplate.update(SQL,new Object[] {
				subject.getSubjectname(),
				subject.getSubjectbbcode(),
				subject.getCommonSubject()
			});
			return "Successfully Subject Inserted";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return "Error While Inserting Subject: " + e.getMessage();
		}
	}
	
	@Transactional(readOnly = true)
	public int getCountSubjects(SubjectBean subject) {
				List<Integer> count;
				String sql = "SELECT count(*) FROM exam.subjects WHERE subjectname = ?";
				
				count =  jdbcTemplate.query(sql,new Object[] {subject.getSubjectname()},new SingleColumnRowMapper<>(Integer.class));
				
				return count.get(0);
			}
	
	@Transactional(readOnly = true)
	public ArrayList<SubjectBean> getAllSubject(){
		try {
			jdbcTemplate=new JdbcTemplate(dataSource);
			String SQL = "SELECT * FROM exam.subjects";
			return (ArrayList<SubjectBean>) jdbcTemplate.query(SQL,new BeanPropertyRowMapper(SubjectBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public String updateSubject(SubjectBean bean) {
		try
		{
			jdbcTemplate=new JdbcTemplate(dataSource);
			String SQL = "update exam.subjects set subjectname = ?,subjectbbcode=?,commonSubject=? where id = ?";
			jdbcTemplate.update(SQL,new Object[] {
				bean.getSubjectname(),
				bean.getSubjectbbcode(),
				bean.getCommonSubject(),
				bean.getId()
			});
			return "Successfully Subject Updated";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return "Error While Updating Subject: " + e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String deleteSubject(int id) {
		try
		{
			jdbcTemplate=new JdbcTemplate(dataSource);
			String SQL = "delete from exam.subjects where id = ?";
			jdbcTemplate.update(SQL,new Object[] {
				id
			});
			return "Successfully Subject delete";
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return "Error While Deleting Subject: " + e.getMessage();
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramStructureByConsumerType(String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsStructureByConsumerType = null;
		String sql =  "select p_s.program_structure as name,p_s.id as id "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_t.name = ? group by p_s.id";
		
		try {
			programsStructureByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsStructureByConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerType(String consumerTypeId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
		
		String sql =  "select p.code as name,p.id as id from exam.consumer_program_structure"
				+ " as c_p_s left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_t.name = ?  group by p.id";
		
		try {
			programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsByConsumerType;  
		
	}


	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramByConsumerTypeAndPrgmStructure(String consumerTypeId,String programStructureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerTypeAndPrgmStructure = null;
		
		String sql =  "select p.code as name from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_t.name = ? and p_s.program_structure = ? group by c_p_s.id";
		
		try {
			programsByConsumerTypeAndPrgmStructure = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql, new Object[] {consumerTypeId,programStructureId},
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {
			
			
			return null;
		}
		
		return programsByConsumerTypeAndPrgmStructure;  
		
	}

	@Transactional(readOnly = true)
	public String getConsumerProgramStructureId(String consumerTypeName,String programStructureName ,String programName){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<String> consumerProgramStructureId = null;
		String sql =  "select c_p_s.id from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_t.name = ? and p_s.program_structure = ? and p.code = ? group by c_p_s.id";
		try {
			
			consumerProgramStructureId = (ArrayList<String>) jdbcTemplate.query(sql,new Object [] {consumerTypeName.trim(),programStructureName.trim(),programName.trim()},new SingleColumnRowMapper<>(String.class));
			
			
		} catch (Exception e) {
			
		}
		
		return consumerProgramStructureId.get(0);
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getSubjectByConsumerType(String consumerTypeId,String programId,String programStructureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
		
		String sql =  " select p_s_s.subject as name, p_s_s.* from exam.program_sem_subject as p_s_s "
					+ " where p_s_s.consumerProgramStructureId in "
					+ " (select c_p_s.id as id from consumer_program_structure as c_p_s where c_p_s.programId in("+programId+") "
					+ " and c_p_s.programStructureId in("+programStructureId+") and c_p_s.consumerTypeId in("+consumerTypeId+")) "
					+ " and studentType = 'TimeBound' "
					+ " group by p_s_s.subject";
		
		try {
			programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {	
			
			return null;
		}
		
		return programsByConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getSubjectByConsumerTypeAndSem(ConsumerProgramStructureExam consumerProgramStructure){
		
		String sem = consumerProgramStructure.getSem();
		String programId = consumerProgramStructure.getProgramId();
		String consumerTypeId = consumerProgramStructure.getConsumerTypeId();
		String programStructureId = consumerProgramStructure.getProgramStructureId();
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ConsumerProgramStructureExam> programsByConsumerType = null;
		
		String sql =  " select p_s_s.subject as name, p_s_s.* from exam.program_sem_subject as p_s_s "
					+ " where p_s_s.consumerProgramStructureId in "
					+ " (select c_p_s.id as id from consumer_program_structure as c_p_s where c_p_s.programId in("+programId+") "
					+ " and c_p_s.programStructureId in("+programStructureId+") and c_p_s.consumerTypeId in("+consumerTypeId+")) "
					+ " and studentType = 'TimeBound' "
					+ " and sem = "+sem
					+ " group by p_s_s.subject";
		
		try {
			programsByConsumerType = (ArrayList<ConsumerProgramStructureExam>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
			
		} catch (Exception e) {	
			
			return null;
		}
		
		return programsByConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getStudentBySubjectForEMBA(String consumerTypeId,String programId,String programStructureId, String subject, String groupid){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean> studentList = null;
		
		String sql =  " select s.* from exam.students s, exam.program_sem_subject pss, exam.registration r "
					+ " where r.month = '" + CURRENT_ACAD_MONTH + "' and r.year = "+ CURRENT_ACAD_YEAR +" "
					+ " and s.sapid = r.sapid  "
					+ " and s.sapid not in (select sapid from lti.groups_members where groupid = ?)"
					+ " and (s.programStatus <> 'Program Terminated' or s.programStatus is null) "
					+ " and s.consumerProgramStructureId = pss.consumerProgramStructureId "
					+ " and pss.id = (SELECT pss.id FROM exam.program_sem_subject pss where pss.subject= ? "
					+ " and pss.consumerProgramStructureId = (SELECT cps.id FROM exam.consumer_program_structure cps "
					+ " where cps.programId = ? and cps.programStructureId = ? and cps.consumerTypeId = ?)) "
					+ " and pss.active = 'Y'"
					+ " limit 100  ";
		
		try {
			studentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,new Object []{groupid, subject,programId, programStructureId, consumerTypeId},
						  new BeanPropertyRowMapper(StudentExamBean.class));

		} catch (Exception e) {
			
			return null;
		}
		
		return studentList;
		
	}

	
//	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String insertDataInProgramtable(final  ProgramSubjectMappingExamBean programSubjectMappingBean) {

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("ProgramDataInsert");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String statusmsg = "Success";

		
			try{
				ArrayList<ProgramExamBean> specialList = getSpecializationtypeList();
				for(ProgramExamBean pb:specialList){
					if(pb.getSpecializationType().equalsIgnoreCase(programSubjectMappingBean.getSpecializationName())){
						programSubjectMappingBean.setSpecializationType(pb.getId());
					}else if("No Specialization".equalsIgnoreCase(programSubjectMappingBean.getSpecializationName())){
						programSubjectMappingBean.setSpecializationType("0");
					}
				}
				insertProgramSubjectContent(programSubjectMappingBean,jdbcTemplate);
				String consumerProgramStructureId = getConsumerProgramStructureId(programSubjectMappingBean.getConsumerType(), programSubjectMappingBean.getPrgmStructApplicable(), programSubjectMappingBean.getProgram());
				programSubjectMappingBean.setConsumerProgramStructureId(consumerProgramStructureId);
				//programSubjectMappingBean.setHasTest("N"); commented by Riya as value should be same like mdm table
				
				if(getCountInProgramSemSubject(programSubjectMappingBean, jdbcTemplate)>0) {
					updateIntoProgramSemSubject(programSubjectMappingBean,jdbcTemplate);
				}else {
					insertIntoProgramSemSubject(programSubjectMappingBean,jdbcTemplate);		
				}
			
				
				 transactionManager.commit(status);
			}catch(Exception e){
				
				statusmsg = "Error";
				   transactionManager.rollback(status);
			         throw e;
				}
		
		return statusmsg;
	}
	
	

	
	public String updateDataInProgramtable(final  ProgramSubjectMappingExamBean programSubjectMappingBean) {

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("ProgramDataInsert");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(def);
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String statusmsg = "Success";

		
			try{
				ArrayList<ProgramExamBean> specialList = getSpecializationtypeList();
				for(ProgramExamBean pb:specialList){
					if(pb.getSpecializationType().equalsIgnoreCase(programSubjectMappingBean.getSpecializationName())){
						programSubjectMappingBean.setSpecializationType(pb.getId());
					}else if("No Specialization".equalsIgnoreCase(programSubjectMappingBean.getSpecializationName())){
						programSubjectMappingBean.setSpecializationType("0");
					}
				}
				
				String consumerProgramStructureId = getConsumerProgramStructureId(programSubjectMappingBean.getConsumerType(), programSubjectMappingBean.getPrgmStructApplicable(), programSubjectMappingBean.getProgram());
				programSubjectMappingBean.setConsumerProgramStructureId(consumerProgramStructureId);
				
				updateProgramSubjectsEntry(programSubjectMappingBean,jdbcTemplate);
				
				if(getCountInProgramSemSubject(programSubjectMappingBean, jdbcTemplate)>0) {
					updateIntoProgramSemSubject(programSubjectMappingBean,jdbcTemplate);	
				}
				else {
					
					insertIntoProgramSemSubject(programSubjectMappingBean,jdbcTemplate);
				}
				
				
				 transactionManager.commit(status);
			}catch(Exception e){
				
				statusmsg = "Error";
				   transactionManager.rollback(status);
			         throw e;
				}
		
		return statusmsg;
	}
	
	@Transactional(readOnly = true)
	public int getCountInProgramSemSubject(ProgramSubjectMappingExamBean data,JdbcTemplate jdbcTemplate) {
		List<Integer> count;
		String sql = "SELECT count(*) FROM exam.program_sem_subject WHERE consumerProgramStructureId = ? and subject = ? and sem = ?";
		
		count =  jdbcTemplate.query(sql,new Object[] {data.getConsumerProgramStructureId(),data.getSubject(),data.getSem()},new SingleColumnRowMapper<>(Integer.class));
		
		return count.get(0);
	}

	@Transactional(readOnly = false)
	public void insertIntoProgramSemSubject(ProgramSubjectMappingExamBean data,JdbcTemplate jdbcTemplate) {
		
		String sql = "insert into exam.program_sem_subject (consumerProgramStructureId,"
		+ "subject,sem,active,passScore,hasAssignment,assignmentNeededBeforeWritten,"
		+ "writtenScoreModel,assignmentScoreModel,isGraceApplicable,maxGraceMarks,createCaseForQuery,"
		+ "assignQueryToFaculty,sifySubjectCode,createdBy,createdDate,lastModifiedBy,lastModifiedDate,hasTest,studentType,specializationType,hasIA,description,subjectCredits )"
		+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?,?)";
		jdbcTemplate.update(sql,new Object[] {
		data.getConsumerProgramStructureId().trim(),
		data.getSubject().trim(),
		data.getSem().trim(),
		data.getActive().trim(),
		data.getPassScore(),
		data.getHasAssignment().trim(),
		data.getAssignmentNeededBeforeWritten().trim(),
		data.getWrittenScoreModel().trim(),
		data.getAssignmentScoreModel().trim(),
		data.getIsGraceApplicable().trim(),
		data.getMaxGraceMarks(),
		data.getCreateCaseForQuery().trim(),
		data.getAssignQueryToFaculty().trim(),
		data.getSifySubjectCode(),
		data.getCreatedBy().trim(),
		data.getLastModifiedBy().trim(),
		data.getHasTest().trim(),
		data.getStudentType(),
		data.getSpecializationType(),
		data.getHasIA(),
		data.getDescription(),
		data.getSubjectCredits()
		});
		// return timeExtendedStudentList;
		}
	
	@Transactional(readOnly = false)
	public void updateIntoProgramSemSubject(ProgramSubjectMappingExamBean data,JdbcTemplate jdbcTemplate) {
		
		String sql = "UPDATE exam.program_sem_subject SET "
				+ "active = ?, "
				+ "passScore = ?, hasAssignment = ?, assignmentNeededBeforeWritten = ?,"
				+ " writtenScoreModel = ?, assignmentScoreModel = ?, isGraceApplicable = ?,"
				+ " maxGraceMarks = ?, createCaseForQuery = ?, assignQueryToFaculty = ?, "
				+ "sifySubjectCode = ?, lastModifiedBy = ?, lastModifiedDate = sysdate(), hasTest = ?, studentType = ?,specializationType=?,hasIA=? ,description=? ,subjectCredits=? "
				+ "WHERE consumerProgramStructureId = ? and subject = ? and sem = ?";
		jdbcTemplate.update(sql,new Object[] {
		
		//for update
		
		data.getActive().trim(),
		data.getPassScore(),
		data.getHasAssignment().trim(),
		data.getAssignmentNeededBeforeWritten().trim(),
		data.getWrittenScoreModel().trim(),
		data.getAssignmentScoreModel().trim(),
		data.getIsGraceApplicable().trim(),
		data.getMaxGraceMarks(),
		data.getCreateCaseForQuery().trim(),
		data.getAssignQueryToFaculty().trim(),
		data.getSifySubjectCode(),

		data.getLastModifiedBy().trim(),
		data.getHasTest().trim(),
		data.getStudentType(),
		data.getSpecializationType(),
		data.getHasIA(),
		data.getDescription(),
		data.getSubjectCredits(),
		//for where condition
		data.getConsumerProgramStructureId().trim(),
		data.getSubject().trim(),
		data.getSem().trim()
		
		});
		// return timeExtendedStudentList;
		}


	// consumerProgramStructureMapping
	//start
	
//	public ArrayList<ConsumerType> getConsumerTypeList() {
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = " select * from exam.consumer_type" ;
//		ArrayList<ConsumerType> psList = (ArrayList<ConsumerType>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ConsumerType.class));
//		return psList;
//	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getProgramList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.program" ;
		ArrayList<ConsumerProgramStructureExam> psList = (ArrayList<ConsumerProgramStructureExam>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
		return psList;
	}
	
//	public ArrayList<ConsumerProgramStructure> getProgramStructureList() {
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = " select * from exam.program_structure" ;
//		ArrayList<ConsumerProgramStructure> psList = (ArrayList<ConsumerProgramStructure>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ConsumerProgramStructure.class));
//		return psList;
//	}
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getAllConsumerProgramStructureMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select c_p_s.*, c_t.name,p.code,p_s.program_structure,lpm.hasPaidSessionApplicable "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId " 
				+ "left join exam.live_programs_mapping as lpm on c_p_s.id = lpm.counsumer_program_structure_id";
		ArrayList<ConsumerProgramStructureExam> psList = (ArrayList<ConsumerProgramStructureExam>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
		return psList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerProgramStructureExam> getConsumerProgramStructureMappingFromId(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select c_p_s.id, c_t.name,p.code,p_s.program_structure "
				+ "from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId "
				+ "where c_p_s.id = ?";
		ArrayList<ConsumerProgramStructureExam> psList = (ArrayList<ConsumerProgramStructureExam>)jdbcTemplate.query(sql, new Object[]{id}, new BeanPropertyRowMapper(ConsumerProgramStructureExam.class));
		return psList;
	}
	
	@Transactional(readOnly = true)
	public int getConsumerProgramStructure(ConsumerProgramStructureExam ConsumerProgramStructureBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select count(*) from exam.consumer_program_structure where programId=? and programStructureId=? and consumerTypeId=?" ;
	
		
		int count = (int) jdbcTemplate.queryForObject(sql,new Object[] {
				ConsumerProgramStructureBean.getProgramId(),
				ConsumerProgramStructureBean.getProgramStructureId(),
				ConsumerProgramStructureBean.getConsumerTypeId()
		}
				,Integer.class);
		return count;
	}
	
	@Transactional( readOnly = false)
	public String insertIntoConsumerProgramStructureTable(ConsumerProgramStructureExam consumerProgramStructureBean) throws Exception{
		boolean toCommit = Boolean.FALSE;
		long primaryKey = 0;
		boolean result= false;
		String status;
		 try {
		  String sql = "INSERT INTO exam.consumer_program_structure "
		  		+ "(programId, programStructureId, consumerTypeId,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
		  		+ "VALUES (?,?,?,?,sysdate(),?,sysdate())";
		  jdbcTemplate = new JdbcTemplate(dataSource);
		  GeneratedKeyHolder holder = new GeneratedKeyHolder();
		 /* jdbcTemplate.update(sql,new Object[] {
				  ConsumerProgramStructureBean.getProgramId(),
				  ConsumerProgramStructureBean.getProgramStructureId(),
				  ConsumerProgramStructureBean.getConsumerTypeId(),
				  ConsumerProgramStructureBean.getCreatedBy(),
				  ConsumerProgramStructureBean.getLastModifiedBy()
		  });
		  return "true";*/
		  startTransaction("sessionInsertion");
		  jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				statement.setString(1, consumerProgramStructureBean.getProgramId());
				statement.setString(2, consumerProgramStructureBean.getProgramStructureId());
				statement.setString(3, consumerProgramStructureBean.getConsumerTypeId());
				statement.setString(4, consumerProgramStructureBean.getCreatedBy());
				statement.setString(5, consumerProgramStructureBean.getLastModifiedBy());
				return statement;
			}
		  },holder );
		  primaryKey = holder.getKey().longValue();

		  if(primaryKey != 0) {
				result =insertLiveSessionFlag(primaryKey, consumerProgramStructureBean.getHasPaidSessionApplicable());	
			}
		  
		 status= "true";
		 }
		 catch(Exception e) {
			 
		  if(e.getMessage().indexOf("Duplicate entry") != -1) {
			  status="Already Exist";
		  }
		  status=e.getMessage();
		 }finally {
			toCommit = primaryKey != 0 && result;
			endTransaction(toCommit);
		}
		 return status;
	}
	
	@Transactional(readOnly = true)
    public ArrayList<StudentSubjectConfigExamBean> getExecutiveCurrentSubject() {
        jdbcTemplate=new JdbcTemplate(dataSource);
        String sql =" SELECT " + 
                "    ssc.*, pss.subject, pss.sem, b.name AS batchName " + 
                " FROM " + 
                "    lti.student_subject_config ssc, " + 
                "    exam.program_sem_subject pss, " + 
                "    exam.batch b " + 
                " WHERE " + 
                "    prgm_sem_subj_id IN (SELECT  " + 
                "            id " + 
                "        FROM " + 
                "            exam.program_sem_subject " + 
                "        WHERE " + 
                "            studentType = 'TimeBound') " + 
                "        AND ssc.prgm_sem_subj_id = pss.id " + 
                "        AND ssc.batchId = b.id ";

        ArrayList<StudentSubjectConfigExamBean> programStructListFromProgramMaster = new ArrayList<StudentSubjectConfigExamBean>();

        try {
            programStructListFromProgramMaster =
                    (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
        } catch (Exception e) {

        }
        return programStructListFromProgramMaster;
    }
	
	@Transactional(readOnly = true)
	public String getPrgmSemSubId(ProgramSubjectMappingExamBean mappingBean, String subject){
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  " select pss.id from exam.program_sem_subject pss where consumerProgramStructureId in( "
					+ " select c_p_s.id FROM exam.consumer_program_structure as c_p_s, "
					+ " exam.program_sem_subject as p_s_s "
					+ " where c_p_s.programId = ? "
					+ " and c_p_s.programStructureId = ? "
					+ " and c_p_s.consumerTypeId = ? "
					+ " and c_p_s.id = p_s_s.consumerProgramStructureId "
					+ " and p_s_s.subject = ? ) "
					+ " and pss.subject = ? and sem = ? ";

		String prgmSemSubId = jdbcTemplate.queryForObject(sql, new Object[]{mappingBean.getProgramId(), mappingBean.getProgramStructureId(), 
																			mappingBean.getConsumerTypeId(), subject, subject, 
																			mappingBean.getSem(),},String.class);
		
		
		return prgmSemSubId;
	}
	
	@Transactional(readOnly = false)
	public int insertSubjectDate(final StudentSubjectConfigExamBean studentConfig){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		
		final String sql =" INSERT INTO lti.student_subject_config "
				   		+ " (prgm_sem_subj_id, batchId, startDate, endDate, acadYear, acadMonth, "
				   		+ " examYear, examMonth, sequence, "
				   		+ " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				   		+ " VALUES (?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) " ;
		
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				statement.setString(1, studentConfig.getPrgm_sem_subj_id());
				statement.setInt(2, studentConfig.getBatchId());
				statement.setString(3, studentConfig.getStartDate());
				statement.setString(4, studentConfig.getEndDate());
				statement.setString(5, studentConfig.getAcadYear());
				statement.setString(6, studentConfig.getAcadMonth());
				statement.setString(7, studentConfig.getExamYear());
				statement.setString(8, studentConfig.getExamMonth());
				statement.setString(9, studentConfig.getSequence());
				statement.setString(10, studentConfig.getCreatedBy());
				statement.setString(11, studentConfig.getLastModifiedBy());
				
				return statement;
			}
		}, holder);
		long primaryKey = holder.getKey().longValue();
		int timeBoundId = (int) primaryKey;
		
		return timeBoundId;
	}
	
	@Transactional(readOnly = true)
	public String getSubjectNameByPSSId(String programSemSubId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " select subject from exam.program_sem_subject where id = ? ";
		String subject = "";
		try {
			subject = jdbcTemplate.queryForObject(sql, new Object[] {programSemSubId}, String.class);
		} catch (Exception e) {
			
		}
		return subject;
	}
	
	@Transactional(readOnly = false)
	public void batchInsertSubjectDate(final StudentSubjectConfigExamBean studentConfig, final List<String> consumerTypeId){
		
		String sql = " INSERT INTO lti.student_subject_config "
				   + " (prgm_sem_subj_id, startDate, endDate, acadYear, acadMonth, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
				   + " VALUES (?,?,?,?,?,?,sysdate(),?,sysdate()) "
				   + " on duplicate key update "
				   + " prgm_sem_subj_id = ?, "
				   + " startDate = ?, "
				   + " endDate = ?,"
				   + " lastModifiedBy = ?, "
				   + " lastModifiedDate = sysdate() ";
		
		int[] batchInsertOfAssignmentsIfAll = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, studentConfig.getPrgm_sem_subj_id());
				ps.setString(2, studentConfig.getStartDate());
				ps.setString(3, studentConfig.getEndDate());
				ps.setString(4, studentConfig.getAcadYear());
				ps.setString(5, studentConfig.getAcadMonth());
				ps.setString(6, studentConfig.getCreatedBy());
				ps.setString(7, studentConfig.getLastModifiedBy());
				
				//On Duplicate
				ps.setString(8, studentConfig.getPrgm_sem_subj_id());
				ps.setString(9, studentConfig.getStartDate());
				ps.setString(10, studentConfig.getEndDate());
				ps.setString(11, studentConfig.getLastModifiedBy());
				
			}
			
			@Override
			public int getBatchSize() {
				return consumerTypeId.size();
			}

			
		});
		
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String updateConsumerProgramStructureMappingList(ConsumerProgramStructureExam ConsumerProgramStructure) {
		boolean flag=false;
		String message;
		try {
		startTransaction("updateMasteKeyDetails");
		String sql = "UPDATE exam.consumer_program_structure "
				+ "SET "
				+ "programId = ?, "
				+ "programStructureId = ?, "
				+ "consumerTypeId = ?, "
				+ "lastModifiedBy = ?, "
				+ "lastModifiedDate = sysdate() "
				+ "WHERE id = ?;";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql,new Object[] {
				ConsumerProgramStructure.getProgramId(),
				ConsumerProgramStructure.getProgramStructureId(),
				ConsumerProgramStructure.getConsumerTypeId(),
				ConsumerProgramStructure.getLastModifiedBy(),
				ConsumerProgramStructure.getId()
		});
		updateHasLiveSessionAccessFlag(ConsumerProgramStructure.getHasPaidSessionApplicable(), ConsumerProgramStructure.getId());
		flag=true;
		message= "true";
		
	}
	catch(DuplicateKeyException e) {
			return "Already Exist";
	}catch (Exception e) {
		message= e.getMessage();
	}finally {
		endTransaction(flag);
	}
	return message;		
		
}
	
	
	
	
	//end
	

	
	@Transactional(readOnly = false)
	public String insertIntoProgramStructureTable(ProgramStructureBean programStructureBean) {
		try {
			String sql = "insert into exam.program_structure(`program_structure`,`created_by`,`lastModifiedBy`) values(?,?,?)";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql,new Object[] {
				programStructureBean.getProgram_structure(),
				programStructureBean.getCreatedBy(),
				programStructureBean.getLastModifiedBy()
			});
			return "true";
		}
		catch(Exception e) {
			if(e.getMessage().indexOf("Duplicate entry") != -1) {
				return "Program Structure Already Exist";
			}
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String updateProgramStructureEntry(ProgramStructureBean programStructureBean) {
		try {
			String sql = "UPDATE `exam`.`program_structure` SET `program_structure`= ?,`lastModifiedBy` = ? WHERE `id`=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql,new Object[] {
				programStructureBean.getProgram_structure(),
				programStructureBean.getLastModifiedBy(),
				programStructureBean.getId()
			});
			return "true";
		}
		catch(Exception e) {
			if(e.getMessage().indexOf("Duplicate entry") != -1) {
				return "Program Structure Already Exist";
			}
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String insertIntoConsumerTypeTable(ConsumerType consumerType) {
		try {
			String sql = "INSERT INTO exam.consumer_type "
					+ "( `name`, `isCorporate`, `created_by`, `lastModifiedBy`) "
					+ "VALUES (?,?,?,?)";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql,new Object[] {
					consumerType.getName(),
					consumerType.getIsCorporate(),
					consumerType.getCreatedBy(),
					consumerType.getLastModifiedBy()
			});
			return "true";
		}
		catch(Exception e) {
			if(e.getMessage().indexOf("Duplicate entry") != -1) {
				return "Consumer Type Already Exist";
			}
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public String updateConsumerTypeEntry(ConsumerType consumerTypeBean) {
		try {
			String sql = "UPDATE `exam`.`consumer_type` SET `name`= ?,`isCorporate`= ?,`lastModifiedBy` = ? WHERE `id`=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql,new Object[] {
					consumerTypeBean.getName(),
					consumerTypeBean.getIsCorporate(),
					consumerTypeBean.getLastModifiedBy(),
					consumerTypeBean.getId()
			});
			return "true";
		}
		catch(Exception e) {
			
			if(e.getMessage().indexOf("Duplicate entry") != -1) {
				return "Consumer Type Already Exist";
			}
			return e.getMessage();
		}
	}
	
	
	
	@Transactional(readOnly = true)
	public ArrayList<ConsumerType> getConsumerTypeList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.consumer_type;";
		ArrayList<ConsumerType> consumerTypeList = (ArrayList<ConsumerType>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConsumerType.class));
		return consumerTypeList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramExamBean> getProgramListFromProgramCodeMaster() {
		jdbcTemplate=new JdbcTemplate(dataSource);
//		String sql= "  SELECT `id`, `code`,`name`,`Specialization` as 'specialization' ,`Mode Of Learning` as 'modeOfLearning' " + 
//				" FROM	`exam`.`program` ";
		String sql= "  SELECT * FROM	`exam`.`program` ";
		ArrayList<ProgramExamBean> programListFromProgramMaster=(ArrayList<ProgramExamBean>)jdbcTemplate.
				query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		
		return programListFromProgramMaster;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProgramNameListFromProgramCodeMaster() {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select code from exam.program  ";
		ArrayList<String> programListFromProgramMaster=(ArrayList<String>)jdbcTemplate.
				query(sql, new SingleColumnRowMapper(String.class));
		
		return programListFromProgramMaster;
	}
	
	@Transactional(readOnly = true)
	public String getProgramCodeFromId(int id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select code from exam.program where id = ? ";
		String programName=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {id},new SingleColumnRowMapper(String.class));
		return programName;
	}
	
	@Transactional(readOnly = true)
	public String getProgramNameFromId(int id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select name from exam.program where id = ? ";
		String programName=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {id},new SingleColumnRowMapper(String.class));
		return programName;
	}
	
	@Transactional(readOnly = true)
	public String getProgramStructureFromId(int id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select program_structure  from exam.program_structure where id = ? ";
		String programStructure=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {id},new SingleColumnRowMapper(String.class));
		return programStructure;
	}
	
	@Transactional(readOnly = true)
	public String getConsumerTypeFromId(int id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select name from exam.consumer_type where id = ? ";
		String consumerType=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {id},new SingleColumnRowMapper(String.class));
		return consumerType;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramsBean> getAllProgramList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select "
				+ " p_d.*,"
				+ " p.code as program,"
				+ " p_s.program_structure as programStructure,"
				+ " c_t.name as consumerType"
				+ " from exam.programs as p_d "
				+ " left join exam.consumer_program_structure as c_p_s on c_p_s.id = p_d.consumerProgramStructureId "
				+ " left join exam.program as p on p.id = c_p_s.programId "
				+ " left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ " left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId";
		ArrayList<ProgramsBean> psList = (ArrayList<ProgramsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsBean.class));
		return psList;
	}
	
	@Transactional(readOnly = true)
	public  ArrayList<String> getAllMasterKeyFromStudents(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> consumerProgramStructureId = null;
		String sql =  "SELECT "
				+ " distinct (concat(consumerType,'|',program,'|',PrgmStructApplicable)) as masterKey"
				+ " FROM"
				+ " exam.students";
		try {
			consumerProgramStructureId = (ArrayList<String>) jdbcTemplate.query(sql,new Object [] {},new SingleColumnRowMapper<>(String.class));
		} catch (Exception e) {
			
		}
		return consumerProgramStructureId;
		
	}
	
	@Transactional(readOnly = true)
	public  ArrayList<String> getAllMasterKeyFromCPP(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> consumerProgramStructureId = null;
		String sql =  "SELECT "
				+ " distinct (concat(consumerTypeId,'|',programId,'|',programStructureId)) as masterKey"
				+ " FROM"
				+ " exam.consumer_program_structure";
		try {
			consumerProgramStructureId = (ArrayList<String>) jdbcTemplate.query(sql,new Object [] {},new SingleColumnRowMapper<>(String.class));
		} catch (Exception e) {
			
		}
		return consumerProgramStructureId;
	}
	
	@Transactional(readOnly = true)
	public String getConsumerTypeIdFromName(String name) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select id from exam.consumer_type where name = ? ";
		String consumerType=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {name},new SingleColumnRowMapper(String.class));
		return consumerType;
	}
	
	@Transactional(readOnly = true)
	public String getProgramIdFromCode(String code) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select id from exam.program where code = ? ";
		String consumerType=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {code},new SingleColumnRowMapper(String.class));
		return consumerType;
	}
	
	@Transactional(readOnly = true)
	public String getProgramStructureIdFromName(String name) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql= " select id from  exam.program_structure where program_structure = ? ";
		String consumerType=(String)jdbcTemplate.
				queryForObject(sql, new Object[] {name},new SingleColumnRowMapper(String.class));
		return consumerType;
	}
	
	@Transactional(readOnly = false)
	public void updateStudentsConsumerProgramStructureId(){
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql = "update exam.students as s set s.consumerProgramStructureId = ("
				+ " select c_p_s.id from exam.consumer_program_structure as c_p_s"
				+ " left join exam.program as p on p.id = c_p_s.programId"
				+ " left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId"
				+ " left join exam.consumer_type as c on c.id = c_p_s.consumerTypeId"
				+ " where"
				+ " p.code = s.program and p_s.program_structure = s.PrgmStructApplicable and c.name = s.consumerType )";
		jdbcTemplate.update(sql);
	}
	
	
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramsBean> getAllProgramConsumerProgramStructureDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select distinct program,PrgmStructApplicable as programStructure ,consumerType from exam.students " ;
		ArrayList<ProgramsBean> psList = (ArrayList<ProgramsBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsBean.class));
		return psList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateProgramsDetailsEntry(final ProgramsBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "update exam.programs set "
				+ " consumerProgramStructureId= ? "
				+ " where program  = ? and programStructure=?";
		
		try {
			jdbcTemplate.update(sql, new Object[] { 
				bean.getConsumerProgramStructureId().trim(),
				bean.getProgram(),
				bean.getProgramStructure()
			});
			
			/*HashMap<String,String> statusMap = updateProgramTableEntry(bean);
			if(statusMap.containsKey("error")){
				message.put("error", "UnSuccessfull Program Not Inserted");
				return message;
			}*/
			message.put("success", "Successfully Program UpdAted");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			message.put("error", "UnSuccessfull Program Not UpdAted");
		}
		return message;
	}
	

	@Transactional(readOnly = true)
	public ArrayList<ProgramExamBean> getProgramTypeList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.program_type order by programType asc";
		ArrayList<ProgramExamBean> programTypeList = (ArrayList<ProgramExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		return programTypeList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> insertIntoProgramTypeTable(ProgramExamBean program) {
		HashMap<String,String> message = new HashMap<String,String>();
		try
		{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "insert into exam.program_type"
				+ "(`programType`)"
				+ "values(?)";
		jdbcTemplate.update(SQL,new Object[] {
		program.getProgramType()
		});
		message.put("success", "Successfully Program Inserted");
		return message;
		}
		catch (Exception e) {
		
		message.put("error", "Error While Inserting Program: " + e.getMessage());
		return message;
		}
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateProgramTypeEntry(ProgramExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "update exam.program_type set "
				+ " programType = ?  "
				+ " where id  = ?";
		try {
			jdbcTemplate.update(sql, new Object[] { 
					bean.getProgramType(),
					Integer.valueOf(bean.getId())
			});
			message.put("success", "Successfully ProgramType Updated");
		} catch (Exception e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProgramTypeNameList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select programType from exam.program_type order by programType asc";
		ArrayList<String> programTypeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programTypeList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramExamBean> getSpecializationtypeList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.specialization_type order by specializationType asc";
		ArrayList<ProgramExamBean> programTypeList = (ArrayList<ProgramExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramExamBean.class));
		return programTypeList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> insertIntoSpecializationtypeTable(ProgramExamBean program) {
		HashMap<String,String> message = new HashMap<String,String>();
		try
		{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "insert into exam.specialization_type"
				+ "(`specializationType`)"
				+ "values(?)";
		jdbcTemplate.update(SQL,new Object[] {
		program.getSpecializationType()
		});
		message.put("success", "Successfully Program Inserted");
		return message;
		}
		catch (Exception e) {
		
		message.put("error", "Error While Inserting Program: " + e.getMessage());
		return message;
		}
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateSpecializationTypeEntry(ProgramExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "update exam.specialization_type set "
				+ " specializationType = ?  "
				+ " where id  = ?";
		try {
			jdbcTemplate.update(sql, new Object[] { 
					bean.getSpecializationType(),
					Integer.valueOf(bean.getId())
			});
			message.put("success", "Successfully ProgramType Updated");
		} catch (Exception e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSpecializationtypeNameList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select specializationType from exam.specialization_type order by specializationType asc";
		ArrayList<String> programTypeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programTypeList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getTimeBoundStudentsList(StudentSubjectConfigExamBean fileBean){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> studentList = null;
		
		String subjectName = getSubjectNameByPSSId(fileBean.getPrgm_sem_subj_id());
		if (!StringUtils.isBlank(subjectName)) {
			String sql =  " SELECT s.sapid FROM "
						+ "		exam.students s "
						+ " 		INNER JOIN  exam.program_sem_subject pss on s.consumerProgramStructureId = pss.consumerProgramStructureId "
						+ " 		INNER JOIN  exam.registration r on s.sapid = r.sapid  and r.sem = pss.sem "
						+ " 	WHERE r.month = ? and r.year = ? "
//						+ " 	AND pss.id = ? "
						+ " 	AND pss.subject = ? and studentType = 'TimeBound' "
						+ " 	AND (s.programStatus <> 'Program Terminated' or s.programStatus is null) ";
			
			try {
				studentList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{fileBean.getAcadMonth(), fileBean.getAcadYear(), 
									subjectName}, new SingleColumnRowMapper(String.class));
	
			} catch (Exception e) {
				
				return null;
			}
		}
		return studentList;
		
	}
	
	//Start added by Abhay for MBAx Program Structure change Student
	@Transactional(readOnly = true)
	public ArrayList<String> getTimeBoundStudentsListforProgramStructureChangeSubject(StudentSubjectConfigExamBean fileBean){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> studentList = null;
		
		String subjectName = getSubjectNameByPSSId(fileBean.getPrgm_sem_subj_id());
		if (!StringUtils.isBlank(subjectName)) {
			String sql =  " SELECT " + 
					"    s.sapid " + 
					"FROM " + 
					"    exam.students s " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId " + 
					"        INNER JOIN " + 
					"    exam.student_course_mapping map ON s.sapid = map.userId " + 
					"        AND map.program_sem_subject_id = pss.id " + 
					"WHERE " + 
					"    map.acadYear = ? " + 
					"        AND map.acadMonth = ? " + 
					"        AND pss.subject = ? " + 
					"        AND pss.studentType = 'TimeBound' " + 
					"        AND (s.programStatus <> 'Program Terminated' " + 
					"        OR s.programStatus IS NULL) ";
			
			try {
				studentList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{
						fileBean.getAcadYear(), fileBean.getAcadMonth(), subjectName},
						new SingleColumnRowMapper(String.class));
	
			} catch (Exception e) {
				
				return studentList;
			}
		}
		return studentList;
		
	}
	//End added by Abhay 
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateStudentEntries(final List<TimeBoundUserMapping> studentSapIdList){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> errorList = new ArrayList<>();
		int i = 0;
		
		for (i = 0; i < studentSapIdList.size(); i++) {
			try {
				TimeBoundUserMapping bean = studentSapIdList.get(i);
				bean.setRole(bean.getStudentType());
				insertStudentMapping(bean, jdbcTemplate);
			} catch (Exception e) {
				
				errorList.add(2+i+"");
			}
		}
		return errorList;	
	}
	
	private void insertStudentMapping(TimeBoundUserMapping bean, JdbcTemplate jdbcTemplate){
		
		String sql =  " INSERT INTO lti.timebound_user_mapping (userId, timebound_subject_config_id, role, createdBy, "
					+ " createdDate, lastModifiedBy, lastModifiedDate) "
				    + " VALUES (?,?,?,?,sysdate(),?,sysdate()) ";
		
			jdbcTemplate.update(sql, new Object[] {bean.getUserId(),bean.getTimebound_subject_config_id(),bean.getRole(),
					bean.getCreatedBy(),bean.getCreatedBy()});
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getAllFacultyList(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> facultyMapping = new HashMap<>();
		try {
			String sql = " SELECT concat(facultyId ,' - ', firstName,' ',lastName) as facultyId_name, facultyId as userId FROM acads.faculty where active = 'Y' ";
			ArrayList<TimeBoundUserMapping> facultyList = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
			for (TimeBoundUserMapping bean : facultyList) {
				facultyMapping.put(bean.getUserId(), bean.getFacultyId_name());
			}
		} catch (Exception e) {
			
			return null;
		}
		return facultyMapping;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getAllCoordinatorList(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> coordinatorMapping = new HashMap<>();
		try {
			String sql = " SELECT concat(userId ,' - Course Coordinator') as coordinatorId_name, userId FROM portal.user_authorization where roles REGEXP 'Acads Admin' ";
			ArrayList<TimeBoundUserMapping> coordinatorList = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));

			for (TimeBoundUserMapping bean : coordinatorList) {
				coordinatorMapping.put(bean.getUserId(), bean.getCoordinatorId_name());
			}
		} catch (Exception e) {
			
			return null;
		}
		return coordinatorMapping;
	}
	
	@Transactional(readOnly = false)
	public void insertFacultyMapping(TimeBoundUserMapping bean){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
			String sql = " INSERT INTO lti.timebound_user_mapping (userId, timebound_subject_config_id, role, "
					   + " createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
					   + " VALUES (?,?,?,?, sysdate(),?,sysdate() ) ";
			jdbcTemplate.update(sql, new Object[]{bean.getUserId(),bean.getTimebound_subject_config_id(), bean.getRole() ,
													bean.getCreatedBy(),bean.getCreatedBy()});
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchInsertProgramNameEntries(final List<ProgramExamBean> programNameList){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> errorList = new ArrayList<>();
		int i = 0;
		for (i = 0; i < programNameList.size(); i++) {
			try {
				ProgramExamBean bean = programNameList.get(i);
				insertIntoProgramTable(bean);
			} catch (Exception e) {
				
				errorList.add(2+i+"");
			}
		}	
		return errorList;
		
	}
	
	@Transactional(readOnly = true)	
public ArrayList<StudentExamBean> studentsWithIncorrectMasterKeyJul2019(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid,program,PrgmStructApplicable,consumerType,consumerProgramStructureId,enrollmentYear,enrollmentMonth from exam.students where enrollmentMonth='Jul' and enrollmentYear=2019 " + 
				" and consumerProgramStructureId<79 " + 
				" and program in (select distinct p.code from exam.program p, exam.program_sem_subject pss,exam.consumer_program_structure cps " + 
				" where cps.id=pss.consumerProgramStructureId   and cps.programId=p.id and pss.consumerProgramStructureId >= 79 )";
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		try {
			studentList =(ArrayList<StudentExamBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(StudentExamBean.class));
		}catch(Exception e) {
			
		}
		return studentList;
		
	}

	@Transactional(readOnly = false)
	public String insertBatchDeatils(BatchExamBean bean){
		
		String status = "Success";
		String sql =  " INSERT INTO exam.batch (consumerProgramStructureId, acadYear, acadMonth, examYear, examMonth, "
					+ " name, sem, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " 
					+ " VALUES (?,?,?,?,?,?,?,?,sysdate(),?,sysdate())" ;
		try {
			jdbcTemplate.update(sql, new Object[]{
					bean.getConsumerProgramStructureId(), bean.getAcadYear(), bean.getAcadMonth(), bean.getExamYear(), bean.getExamMonth(),
					bean.getName(),bean.getSem() ,bean.getCreatedBy(),bean.getCreatedBy()});
			
		} catch (Exception e) {
			
			status = "Error";
		}
		return status;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBacthDeatils(){
		
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql =" select * from exam.batch " ;
		ArrayList<BatchExamBean> batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(BatchExamBean.class));
		return batchList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateBatchName(BatchExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = "update exam.batch set name = ?, lastModifiedBy = ?, lastModifiedDate = sysdate() where id  = ?";
		try {
			jdbcTemplate.update(sql, new Object[] {bean.getName(),bean.getLastModifiedBy(),bean.getId()});
			message.put("success", "Successfully ProgramType Updated");
		} catch (Exception e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> deleteBatch(int id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql="delete from exam.batch where id = ?";
		try {
			 jdbcTemplate.update(sql, new Object[] {id});
			 message.put("success", "Successfully ProgramType Updated");
		} catch (DataAccessException e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchList (ConsumerProgramStructureExam bean){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<BatchExamBean> batchList = null;
		
		String sql =  " select * from exam.batch "
					+ "	where acadYear = ? and acadMonth = ? "
					+ " and consumerProgramStructureId = " 
					+ " (select id from exam.consumer_program_structure "
					+ " where programId = ? and programStructureId = ? and consumerTypeId =? ) "
					+ " and sem = ? ";
		
		try {
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] 
						{bean.getAcadYear(), bean.getAcadMonth(),bean.getProgramId(), bean.getProgramStructureId(), 
						 bean.getConsumerTypeId(), bean.getSem()}, new BeanPropertyRowMapper(BatchExamBean.class));
		} catch (Exception e) {
			
			return null;
		}
		return batchList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchListByYearMonth (){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<BatchExamBean> batchList = null;
		
		String sql =  " select * from exam.batch where acadYear = '"+CURRENT_ACAD_YEAR+"' and acadMonth = '"+CURRENT_ACAD_MONTH+"'";
		
		try {
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(BatchExamBean.class));
		} catch (Exception e) {
			
			return null;
		}
		return batchList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getStudentListCPSId(ConsumerProgramStructureExam bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT  " + 
					"    s.* " + 
					" FROM " + 
					"    exam.students s " + 
					"        INNER JOIN " + 
					"    exam.registration r ON s.sapid = r.sapid " + 
					" WHERE " + 
					"    r.month = ? AND r.year = ? " + 
					"	 AND r.sem = ? " +
					"        AND s.consumerProgramStructureId = (SELECT  " + 
					"            consumerProgramStructureId " + 
					"        FROM " + 
					"            exam.batch " + 
					"        WHERE " + 
					"            id = ? ) " + 
					"        AND s.sapid NOT IN (SELECT  " + 
					"            userId " + 
					"        FROM " + 
					"            lti.timebound_user_mapping tum " + 
					"                INNER JOIN " + 
					"            lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
					"                INNER JOIN " + 
					"            exam.batch b ON ssc.batchId = b.id " + 
					"                INNER JOIN " + 
					"            exam.registration r ON r.sapid = tum.userId " + 
					"                AND r.month = b.acadMonth " + 
					"                AND r.year = b.acadYear " + 
					"        WHERE " + 
					"            tum.role = 'Student' " + 
					"				AND ssc.acadYear = ? " + 
					"               AND ssc.acadMonth = ? " + 
					"               AND r.sem = ? " + 
					"        GROUP BY tum.userId) " + 
					"        AND (programStatus <> 'Program Terminated' OR programStatus is null OR programStatus = '') ";

		
		ArrayList<StudentExamBean> studentsList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {bean.getAcadMonth(), bean.getAcadYear(), bean.getSem(), 
								bean.getBatchId(), bean.getAcadYear(), bean.getAcadMonth(), bean.getSem()}, new BeanPropertyRowMapper(StudentExamBean.class));
		return studentsList;
	}
	
	@Transactional(readOnly = false)
	public String addTimeBoundMapping (final TimeBoundUserMapping mappingBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		final String sql =" INSERT INTO lti.timebound_user_mapping "
						+ " (userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
						+ " VALUES (?, ?, ?, ?, sysdate(), ?, sysdate()) ";
		
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					statement.setString(1,mappingBean.getUserId());
					statement.setInt(2,mappingBean.getTimebound_subject_config_id());
					statement.setString(3,mappingBean.getRole());	
					statement.setString(4,mappingBean.getCreatedBy());
					statement.setString(5,mappingBean.getLastModifiedBy());
					
					return statement;
				}
				
			}, holder);
			long primaryKey = holder.getKey().longValue();
			return primaryKey+"";
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getTimeBoundId(String batchId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select id from lti.student_subject_config where batchId = ? ";

		ArrayList<Integer> timeBoundIdList = (ArrayList<Integer>) jdbcTemplate.query(sql, new Object[] {batchId}, new SingleColumnRowMapper(Integer.class));
		return timeBoundIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TimeBoundUserMapping> getExistingTimeboundStudents(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT tm.*,b.name FROM lti.timebound_user_mapping tm " 
					+ " INNER JOIN lti.student_subject_config ssc ON ssc.id = tm.timebound_subject_config_id "
					+ " INNER JOIN exam.batch b ON b.id = ssc.batchId "
					+ " where role = 'Student' and timebound_subject_config_id "
					+ " in (select id from lti.student_subject_config "
					+ " where acadYear='"+CURRENT_ACAD_YEAR+"' and acadMonth ='"+CURRENT_ACAD_MONTH+"');";
		
		ArrayList<TimeBoundUserMapping> userList = new ArrayList<TimeBoundUserMapping>();
		try {
			userList = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}
				
		return userList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> updateTimeBoundMapping(StudentSubjectConfigExamBean configBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql = " UPDATE lti.student_subject_config SET startDate = ? , endDate = ? where id  = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] {configBean.getStartDate(),configBean.getEndDate(), configBean.getId()});
			message.put("success", "Successfully ProgramType Updated");
		} catch (Exception e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> deleteTimeBoundMapping(String id) {
			
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql=" DELETE FROM lti.student_subject_config WHERE id = ? ";
		try {
			 jdbcTemplate.update(sql, new Object[] {id});
			 message.put("success", "Successfully ProgramType Updated");
		} catch (DataAccessException e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}
	
	@Transactional(readOnly = false)
	public long insertGenericGroups (final GroupExamBean group){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		long primaryKey = 0;
		
		final String sql =  " INSERT INTO lti.groups (timeBoundId, groupName, groupDescription, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
							" VALUES (?,?,?,?,sysdate(),?,sysdate()) " ;
		
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				statement.setInt(1, group.getTimeBoundId());
				statement.setString(2, group.getGroupName());
				statement.setString(3, group.getGroupDescription());
				statement.setString(4, group.getCreatedBy());
				statement.setString(5, group.getLastModifiedBy());
				return statement;
			}
			
		}, holder);
		primaryKey = holder.getKey().longValue();
		
		return primaryKey;
	}

	@Transactional(readOnly = false)
	public String batchUpdateStudentsForIncorrectMasterKeyJul2019(final ArrayList<StudentExamBean> studentsWithIncorrectData){
	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.students set consumerProgramStructureId = ?,lastModifiedBy='Batch API Hit',lastModifiedDate=sysdate() where sapid =? and consumerType =? and program=? and PrgmStructApplicable=?";
		
		try {
			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
	
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, studentsWithIncorrectData.get(i).getConsumerProgramStructureId());
					ps.setString(2, studentsWithIncorrectData.get(i).getSapid());
					ps.setString(3, studentsWithIncorrectData.get(i).getConsumerType());
					ps.setString(4, studentsWithIncorrectData.get(i).getProgram());
					ps.setString(5, studentsWithIncorrectData.get(i).getPrgmStructApplicable());
						
				}
	
				@Override
				public int getBatchSize() {
					return studentsWithIncorrectData.size();
				}
			  });
			return "";
		}catch(Exception e) {
			
			return "Error";
		}
	}

	@Transactional(readOnly = false)
	public String batchUpdateRegistrationForIncorrectMasterKeyJul2019(final ArrayList<StudentExamBean> studentsWithIncorrectData){
	
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.registration set consumerProgramStructureId = ? ,lastModifiedBy='Batch API Hit',lastModifiedDate=sysdate() where sapid =? and program=? and year=? and month=?";
		
		try {
			int[] batchInsert = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
	
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
				
					ps.setString(1, studentsWithIncorrectData.get(i).getConsumerProgramStructureId());
					ps.setString(2, studentsWithIncorrectData.get(i).getSapid());
					ps.setString(3, studentsWithIncorrectData.get(i).getProgram());
					ps.setString(4, studentsWithIncorrectData.get(i).getEnrollmentYear());					
					ps.setString(5, studentsWithIncorrectData.get(i).getEnrollmentMonth());
				}
	
				@Override
				public int getBatchSize() {
					return studentsWithIncorrectData.size();
				}
			 });
		
			return "";
			
		}catch(Exception e) {
			
			return "Error";
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchListByYearMonth (ConsumerProgramStructureExam bean){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<BatchExamBean> batchList = new ArrayList<BatchExamBean>();
		
		String sql =  " SELECT * from exam.batch WHERE acadYear = ? and acadMonth = ? and sem = ? ";
		try {
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {bean.getAcadYear(), bean.getAcadMonth(), bean.getSem()}, 
									new BeanPropertyRowMapper(BatchExamBean.class));
		} catch (Exception e) {
			
		}
		return batchList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TimeBoundUserMapping> getFacultiesByTimeboundId (String timeBoundId ,String role){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<TimeBoundUserMapping> faculties = new ArrayList<TimeBoundUserMapping>();
		
		String sql =" SELECT " + 
					"    tum.*, CONCAT(f.firstName,' ',f.lastName) AS facultyName, " + 
					"	 ssc.prgm_sem_subj_id " +
					" FROM " + 
					"    lti.timebound_user_mapping tum " + 
					"        LEFT JOIN " + 
					"    acads.faculty f ON f.facultyId = tum.userId " + 
					"		 LEFT JOIN " + 
					"	 lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id" +
					" WHERE " + 
					"    role = ? " + 
					"        AND timebound_subject_config_id = ? ";
		try {
			faculties = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql, new Object[] {role,timeBoundId}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}
		return faculties;
	}

	@Transactional(readOnly = true)
	public ArrayList<TimeBoundUserMapping> getCoordinatorsByTimeboundId (String timeBoundId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<TimeBoundUserMapping> coordinators = new ArrayList<TimeBoundUserMapping>();

		String sql =" SELECT tum.*, CONCAT('Course Coordinator - ',ua.userId) AS coordinatorName, " +
				"ssc.prgm_sem_subj_id " +
				"FROM " +
				"lti.timebound_user_mapping tum " +
				"LEFT JOIN " +
				"portal.user_authorization ua ON ua.userId = tum.userId " +
				"LEFT JOIN " +
				"lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " +
				"WHERE  timebound_subject_config_id = ?  AND role = 'Course Coordinator' ";
		try {
			coordinators = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql, new Object[] {timeBoundId}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}
		return coordinators;
	}
	
	@Transactional(readOnly = false)
	public boolean deleteCourseFacultyMapping(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM lti.timebound_user_mapping WHERE id=? ";
		
		try {
			jdbcTemplate.update(sql, new Object[] {id});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean deleteCourseCoordinatorMapping(String id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM lti.timebound_user_mapping WHERE id=? ";

		try {
			jdbcTemplate.update(sql, new Object[] {id});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public TimeBoundUserMapping findScheduledSessionById(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		TimeBoundUserMapping facultyMapping = new TimeBoundUserMapping();
		String sql = "SELECT * FROM lti.timebound_user_mapping WHERE role IN ('Faculty','Grader') and id = ? ";
		
		try {
			facultyMapping = (TimeBoundUserMapping) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}
		
		return facultyMapping;
	}
	
	@Transactional(readOnly = false)
	public boolean updateTimeBoundFacultyMapping(TimeBoundUserMapping bean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE lti.timebound_user_mapping SET userId = ?, lastModifiedDate=sysdate() WHERE id = ? ";
		
		try {
			jdbcTemplate.update(sql,new Object[] {bean.getUserId(), bean.getId()});
			return true;
		}
		catch(Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = true)
	public TimeBoundUserMapping findScheduledSessionForCoordinatorById(String id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		TimeBoundUserMapping coordinatorMapping = new TimeBoundUserMapping();
		String sql = "SELECT * FROM lti.timebound_user_mapping WHERE role = 'Course Coordinator' and id = ? ";

		try {
			coordinatorMapping = (TimeBoundUserMapping) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}

		return coordinatorMapping;
	}

	@Transactional(readOnly = false)
	public boolean updateTimeBoundCoordinatorMapping(TimeBoundUserMapping bean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE lti.timebound_user_mapping SET userId = ?, lastModifiedDate=sysdate() WHERE id = ? ";

		try {
			jdbcTemplate.update(sql,new Object[] {bean.getUserId(), bean.getId()});
			return true;
		}
		catch(Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = true)
	public List<TimeBoundUserMapping> getTimeboundUserMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TimeBoundUserMapping> timeBoundUserMappings=new ArrayList<>();				
				String sql = "SELECT " +
			    "* " +
			"FROM " +
			    "lti.timebound_user_mapping tum " +
			"WHERE " +
			    "timebound_subject_config_id IN (SELECT " + 
			            "ssc.id AS timebound_subject_config_id " +
			        "FROM " +
			            "lti.student_subject_config AS ssc " +
			        "WHERE " +
			            "(CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate) " +
			            "AND acadYear = " + CURRENT_MBAWX_ACAD_YEAR +
			                " AND acadMonth = '" + CURRENT_MBAWX_ACAD_MONTH + "') " +
			        "AND RIGHT(tum.userId, 7) != 'deleted' " +
			        "And userId not like '777777%' " +
			        "AND tum.role IN ('Student' , 'Course Coordinator') " +
			        "Order by timebound_subject_config_id, FIELD(tum.role,'Course Coordinator','Student') " ;
			        

			try {
				timeBoundUserMappings = (List<TimeBoundUserMapping>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
				logger.info("Exception: getTimeboundUserMappingList DAO" + timeBoundUserMappings.toString());
				return timeBoundUserMappings;

			} catch (Exception e) {
				logger.error("Exception: getTimeboundUserMappingList DAO",e);
				throw e;
			}
	}

	@Transactional(readOnly = true)
	public List<SubjectGroupsBean> getSubjectsToCheckIfExistsOrNotInSubjectGroups(TimeBoundUserMapping bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =  		"SELECT " +  
			    "tum.id AS timebound_user_mapping_id, " +
			    "tum.role, " +
			    "tum.userId, " +
			    "CONCAT('T', pss.sem) AS Term, " +
			    "pss.subject, " + 
			    "ssc.id AS student_subject_config_id, " +
			    "ssc.prgm_sem_subj_id, " +
			    "ssc.batchId, " + 
			    "ssc.acadYear, " + 
			    "ssc.acadMonth, " + 
			    "COALESCE(st.specializationType, '') AS specialisation, " +
			    "COALESCE(st.specializationInitials, '') AS specialisation_initials, " +
			    "b.name AS batchName, " +
			    "tum.createdBy, " +
			    "tum.lastModifiedBy " +
			"FROM " + 
			    "lti.timebound_user_mapping AS tum " +
			        "INNER JOIN " +
			    "lti.student_subject_config AS ssc ON tum.timebound_subject_config_id = ssc.id " +
			        "INNER JOIN " +
			    "exam.program_sem_subject AS pss ON ssc.prgm_sem_subj_id = pss.id " +
			        "INNER JOIN " +
			    "exam.batch AS b ON ssc.batchId = b.id " +
			        "AND b.consumerProgramStructureId = pss.consumerProgramStructureId " +
			        "LEFT JOIN " +
			    "exam.specialization_type AS st ON st.id = pss.specializationType " +
			"WHERE " +
			    "ssc.acadYear = " + CURRENT_MBAWX_ACAD_YEAR + " " +
			        "AND ssc.acadMonth = '" + CURRENT_MBAWX_ACAD_MONTH + "' " +
			        "AND tum.userId = ? " +
			        "AND (CURRENT_TIMESTAMP BETWEEN ssc.startDate AND ssc.endDate); ";

		ArrayList<SubjectGroupsBean> subjectGroupsBeans = (ArrayList<SubjectGroupsBean>) jdbcTemplate.query(sql, new Object[] {bean.getUserId()}, new BeanPropertyRowMapper(SubjectGroupsBean.class));
		return subjectGroupsBeans;
	}

	@Transactional(readOnly = true)
	public boolean checkIfRecordExistsInSubjectGroup(SubjectGroupsBean subjectGroupsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count=0;
		String sql =  "select count(*) from lti.subject_groups as sg where sg.timebound_user_mapping_id=? and sg.subject=? and sg.term=? and sg.specialisation=? and applozic_group_id is not null";

		count = (int) jdbcTemplate.queryForObject(sql, new Object[] {subjectGroupsBean.getTimebound_user_mapping_id(), subjectGroupsBean.getSubject(), subjectGroupsBean.getTerm(), subjectGroupsBean.getSpecialisation()}, new SingleColumnRowMapper(Integer.class));
		if(count!=0){
			return true;
		} else{
			return false;
		}
	}

	@Transactional(readOnly = false)
	public long insertRecordInSubjectGroup(final SubjectGroupsBean subjectGroupsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		long primaryKey = 0;

		final String sql =  " insert into lti.subject_groups (timebound_user_mapping_id, applozic_group_id, term, specialisation, specialisation_initials, subject, subject_initials, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
				"values (?,?,?,?,?,?,?,?,sysdate(),?,sysdate())" ;
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

					statement.setInt(1, subjectGroupsBean.getTimebound_user_mapping_id());
					statement.setInt(2, subjectGroupsBean.getApplozic_group_id());
					statement.setString(3, subjectGroupsBean.getTerm());
					statement.setString(4, subjectGroupsBean.getSpecialisation());
					statement.setString(5, subjectGroupsBean.getSpecialisation_initials());
					statement.setString(6, subjectGroupsBean.getSubject());
					statement.setString(7, subjectGroupsBean.getSubject_initials());
					statement.setString(8, subjectGroupsBean.getCreatedBy());
					statement.setString(9, subjectGroupsBean.getLastModifiedBy());
					return statement;
				}

			}, holder);
			primaryKey = holder.getKey().longValue();
		} catch(Exception ex){
		}
		return primaryKey;
	}

	@Transactional(readOnly = true)
	public SubjectGroupsBean getSubjectGroupById(SubjectGroupsBean subjectGroupsBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		SubjectGroupsBean subjectGroupsBean1=new SubjectGroupsBean();
		String sql =  "select sg.*,ag.clientGroupId,ag.groupId,ag.chat_group_name,tum.userId,tum.role from lti.subject_groups as sg inner join lti.timebound_user_mapping as tum on tum.id=sg.timebound_user_mapping_id " +
				"inner join lti.applozic_groups as ag on ag.id=sg.applozic_group_id " +
				"where sg.timebound_user_mapping_id=? and sg.subject=? and sg.term=? and sg.specialisation=?";

		subjectGroupsBean1 = (SubjectGroupsBean) jdbcTemplate.queryForObject(sql, new Object[] {subjectGroupsBean.getTimebound_user_mapping_id(), subjectGroupsBean.getSubject(), subjectGroupsBean.getTerm(), subjectGroupsBean.getSpecialisation()}, new BeanPropertyRowMapper(SubjectGroupsBean.class));
		return subjectGroupsBean1;
	}

	@Transactional(readOnly = true)
	public boolean checkIfApplozicGroupExist(String chat_group_name) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int exist=0;
		String sql =  "select exists(select ag.* from lti.applozic_groups as ag where ag.chat_group_name=?)";

		exist = (int) jdbcTemplate.queryForObject(sql, new Object[] {chat_group_name}, new SingleColumnRowMapper(Integer.class));

		if(exist!=0){
			return true;
		} else{
			return false;
		}
	}

	@Transactional(readOnly = true)
	public long getApplozicGroupIfExist(String chat_group_name) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		Long primaryKey=null;
		String sql =  "select ag.id from lti.applozic_groups as ag where ag.chat_group_name=?";
		try {
			primaryKey = (long) jdbcTemplate.queryForObject(sql, new Object[] {chat_group_name}, new SingleColumnRowMapper(Long.class));
			return primaryKey;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("insertRecordInApplozicGroups", e );

			//
			throw e;
		}
	
	}

	@Transactional(readOnly = false)
	public long insertRecordInApplozicGroups(final ApplozicGroupBean applozicGroupBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		long primaryKey = 0;

		final String sql =  " insert into lti.applozic_groups (groupId, clientGroupId, chat_group_name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
				"values (?,?,?,?,sysdate(),?,sysdate())" ;
		try {
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

					statement.setString(1, applozicGroupBean.getGroupId());
					statement.setString(2, applozicGroupBean.getClientGroupId());
					statement.setString(3, applozicGroupBean.getChat_group_name());
					statement.setString(4, applozicGroupBean.getCreatedBy());
					statement.setString(5, applozicGroupBean.getLastModifiedBy());
					return statement;
				}

			}, holder);
			primaryKey = holder.getKey().longValue();
			logger.info("Insert Applogic Groups Successfully.");
			return primaryKey;

		} catch(Exception ex){
			logger.error("insertRecordInApplozicGroups", ex );
			throw ex;
		}
	}
	
	@Transactional(readOnly = true)
	public ApplozicGroupBean getApplozicGroupById(Integer applozic_group_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ApplozicGroupBean applozicGroupBean=new ApplozicGroupBean();
		String sql =  "select * from lti.applozic_groups as ag where ag.id=?";

		applozicGroupBean = (ApplozicGroupBean) jdbcTemplate.queryForObject(sql, new Object[] {applozic_group_id}, new BeanPropertyRowMapper(ApplozicGroupBean.class));
		return applozicGroupBean;
	}

	@Transactional(readOnly = true)
	public List<TimeBoundUserMapping> getStudentBatchMapping(String pssId, String batchId, String isResit, String startDate){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TimeBoundUserMapping> timeBoundUserMappings = new ArrayList<>();
		String sql =" SELECT " +
				"    userId, " +
				"    b.name, " +
				"    pss.subject, " +
				"    CONCAT(firstName, ' ', lastname) AS studentName, " +
				"    emailId, " +
				"    imageUrl, " +
				"    mobile, " +
				"	 r.sem "  +
				"FROM " +
				"    lti.timebound_user_mapping AS tum " +
				"        INNER JOIN " +
				"    exam.students AS s ON s.sapid = tum.userId " +
				"        INNER JOIN " +
				"    exam.registration AS r ON s.sapid = r.sapid " +
				"        INNER JOIN " +
				"    lti.student_subject_config AS ssc ON tum.timebound_subject_config_id = ssc.id AND r.month = ssc.acadMonth AND r.year = ssc.acadYear " +
				"        INNER JOIN " +
				"    exam.batch AS b ON ssc.batchId = b.id " +
				"        INNER JOIN " +
				"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " +
				"WHERE " +
				"    pss.id = ? AND b.id = ? AND startDate = ? " +
				"        AND tum.role IN ('Student' ";
		
		if (isResit!=null && isResit.equals("Yes")) {
			sql += " , 'Resit')";
		} else {
			sql += ")";
		}

		timeBoundUserMappings = (List<TimeBoundUserMapping>) jdbcTemplate.query(sql,
				new Object[] { pssId, batchId, startDate }, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		return timeBoundUserMappings;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TimeBoundUserMapping> getExistingStudentsByTimeboundId(String timeBoundId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  "SELECT * FROM lti.timebound_user_mapping WHERE timebound_subject_config_id = ? and role in ('Student','resit')";
		
		ArrayList<TimeBoundUserMapping> userList = new ArrayList<TimeBoundUserMapping>();
		try {
			userList = (ArrayList<TimeBoundUserMapping>) jdbcTemplate.query(sql,new Object[] {timeBoundId}, new BeanPropertyRowMapper(TimeBoundUserMapping.class));
		} catch (Exception e) {
			
		}
				
		return userList;
	}
	
	@Transactional(readOnly = false)
	public HashMap<String,String> deleteStudentTimeBoundMapping(String id) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,String> message = new HashMap<String,String>();
		String sql=" DELETE FROM lti.timebound_user_mapping WHERE id = ? ";
		try {
			 jdbcTemplate.update(sql, new Object[] {id});
			 message.put("success", "Successfully ProgramType Updated");
		} catch (DataAccessException e) {
			
			message.put("error", "UnSuccessfull ProgramType Not Updated");
		}
		return message;
	}

	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getBatchesByMasterKey (ConsumerProgramStructureExam bean){

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<BatchExamBean> batchList = new ArrayList<BatchExamBean>();

		String sql =  " SELECT b.* " +
				"FROM " +
				"    exam.batch AS b " +
				"        INNER JOIN " +
				"    exam.consumer_program_structure AS cps ON cps.id = b.consumerProgramStructureId " +
				"        INNER JOIN " +
				"    exam.program p ON p.id = cps.programId " +
				"        INNER JOIN " +
				"    exam.consumer_type AS ct ON ct.id = cps.consumerTypeId " +
				"        INNER JOIN " +
				"    exam.program_structure AS ps ON ps.id = cps.programStructureid " +
				"WHERE " +
				"    ct.name = ? " +
				"        AND p.code = ? " +
				"        AND ps.program_structure = ? " +
				"        AND b.sem = ? " +
				"        AND b.acadYear = ? " +
				"        AND b.acadMonth = ? ";
		try {
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql,
					new Object[] { bean.getConsumerTypeId(), bean.getProgramId(), bean.getProgram_structure(),
							bean.getSem(), bean.getAcadYear(), bean.getAcadMonth() },
					new BeanPropertyRowMapper(BatchExamBean.class));
		} catch (Exception e) {
			
		}
		return batchList;
	}
	
	@Transactional(readOnly = false)
	public boolean insertDummyStudentMapping(int timeBoundId, String dummyUserId, String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " INSERT INTO lti.timebound_user_mapping (userId, timebound_subject_config_id, role, createdBy, "
					+ " createdDate, lastModifiedBy, lastModifiedDate) "
				    + " VALUES (?,?,'Student',?,sysdate(),?,sysdate()) ";
		try {
			jdbcTemplate.update(sql, new Object[] {dummyUserId,timeBoundId,userId,userId});
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public boolean insertLiveSessionFlag(Long consumerProgramStructureId, String liveFlag) throws SQLException {
		
		logger.debug("STRAT");
		System.out.println("insertLiveSessionFlag");
		StringBuilder INSERT_LIVE_SESSIONS_MAPPING = null;

		// Creating StringBuilder object
		String sql="INSERT INTO exam.live_programs_mapping (counsumer_program_structure_id,hasPaidSessionApplicable)  VALUES (?,?)";
		
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setLong(1, consumerProgramStructureId);
				ps.setString(2, liveFlag);
			}
		});
		logger.debug("END");
		return true;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getMasterKeysList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select id from exam.consumer_program_structure";
		ArrayList<String> masterkeysList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return masterkeysList;
	}
	
	@Transactional(readOnly = false)
	public void insertmasterKeyInLiveSessionMappingTable(String consumerProgramStructureId) {
		
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "INSERT INTO exam.live_programs_mapping (counsumer_program_structure_id)  VALUES (?)";
		jdbcTemplate.update(SQL,new Object[] {
				consumerProgramStructureId});
	}
	
	@Transactional(readOnly = false)
	public void updateLiveSessionFlag() {
		
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "update exam.live_programs_mapping set hasPaidSessionApplicable=0 where counsumer_program_structure_id IN (111,131,151,154,155,156,157,158,142,143,144,145,146,147,148,149,127,128,159,152,150,113,110,112)";
		jdbcTemplate.update(SQL);
	}
	

	@Transactional(readOnly = false)
	public void updateHasLiveSessionAccessFlag(String hasPaidSessionApplicable,String consumeProgramStructureId) {
		String sql = "UPDATE exam.live_programs_mapping SET hasPaidSessionApplicable=? WHERE counsumer_program_structure_id=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql,new Object[] {hasPaidSessionApplicable,consumeProgramStructureId});
	}
	
	@Transactional(readOnly = false)
	public void insertFailedSubjectCountCriteria(List<Integer> consumerProgramStructureId ,FailedSubjectCountCriteriaBean bean)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="insert into exam.timebound_subject_count_criteria (consumerProgramStructureId,failedSubjectCountCriteria,createdBy,createdDateTime,lastModifiedBy,lastModifiedDateTime)"
				+" values (?,?,?,sysdate(),?,sysdate())";
		int rowsAffectedArr[]=jdbcTemplate.batchUpdate(sql, 
				new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setInt(1, consumerProgramStructureId.get(i));
						ps.setInt(2, Integer.parseInt(bean.getFailedSubjectCount()));
						ps.setString(3, bean.getCreatedBy());
						ps.setString(4, bean.getLastModifiedBy());
					}
					
					@Override
					public int getBatchSize() {
						return consumerProgramStructureId.size();
					}
				}
			);
	}
	
	@Transactional(readOnly = false)
	public void updateFailedSubjectCountCriteria(FailedSubjectCountCriteriaBean bean)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update exam.timebound_subject_count_criteria set failedSubjectCountCriteria=?,lastModifiedBy=?,lastModifiedDateTime=sysdate() where consumerProgramStructureId=?";
		jdbcTemplate.update(sql, new Object[] {bean.getFailedSubjectCount(),bean.getLastModifiedBy(),bean.getConsumerProgramStructureId()});
	}
	
	@Transactional(readOnly = true)
	public List<FailedSubjectCountCriteriaBean> getFailedCriteriaDetails()
	{
		List<FailedSubjectCountCriteriaBean> failedCriteriaDetails = new ArrayList<FailedSubjectCountCriteriaBean>();
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="select consumerProgramStructureId,failedSubjectCountCriteria as failedSubjectCount from exam.timebound_subject_count_criteria";
			failedCriteriaDetails = jdbcTemplate.query(sql,new BeanPropertyRowMapper<FailedSubjectCountCriteriaBean>(FailedSubjectCountCriteriaBean.class));
		}
		catch(Exception e)
		{
		}
		return failedCriteriaDetails;
	}

	public List<TimeBoundUserMapping> getMappedStudent(String timeBoundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select userId,timebound_subject_config_id from lti.timebound_user_mapping where timebound_subject_config_id  = ?"
				+ " AND userId NOT LIKE '777777%'  " + 
				" AND role IN ('Student' , 'Resit')";
		return jdbcTemplate.query(sql,new Object[] {timeBoundId},new BeanPropertyRowMapper<>(TimeBoundUserMapping.class));
	}

	public String getStudentSubjectConfig(String timeboundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="Select prgm_sem_subj_id from lti.student_subject_config where id = ?";
		return jdbcTemplate.queryForObject(sql,new Object[] {timeboundId},String.class);
	}

	public DissertationResultProcessingDTO getProgram(String subjectId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="Select id,consumerProgramStructureId from exam.program_sem_subject where id = ? ";
		return (DissertationResultProcessingDTO) jdbcTemplate.queryForObject(sql,new Object[] {subjectId},new BeanPropertyRowMapper(DissertationResultProcessingDTO.class));
	}

	public int getTimboundDetails(String sapid, String timeboundId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select timebound_subject_config_id as timeBoundId from lti.timebound_user_mapping "
				+ "where userId = ? and timebound_subject_config_id = ? " ;
				
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid,timeboundId},  Integer.class);
	}

	
	
	public int deleteProgram(String id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		return jdbcTemplate.update("delete from exam.program where id = ?",new Object[] {id});
	}
	
	public int deleteProgramDetails(String id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		return jdbcTemplate.update("delete from exam.programs where id = ?",new Object[] {id});
	}

	
	
	@Transactional(readOnly = true)
	public String getSubjectNameByprgm_sem_subj_id(int prgm_sem_subj_id) throws Exception{
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql="select subject  FROM exam.program_sem_subject where id=?";	
		String subjectName = (String) jdbcTemplate.queryForObject(sql, new Object[]{prgm_sem_subj_id}, new SingleColumnRowMapper(String.class));	
		return subjectName;
	}
	
	

	@Transactional(readOnly = true)
    public StudentSubjectConfigExamBean getBatchDetailsByBatchId(String batchId) throws Exception {
        jdbcTemplate=new JdbcTemplate(dataSource);
        String sql ="select name as batchName,acadMonth,acadYear,examMonth,examYear from exam.batch where id=?";
        StudentSubjectConfigExamBean programStructListFromProgramMaster = new StudentSubjectConfigExamBean();
            programStructListFromProgramMaster =
            		  (StudentSubjectConfigExamBean) jdbcTemplate.queryForObject(sql, new Object[]{batchId}, new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));  
        return programStructListFromProgramMaster;
    }
	
	
	
	
}