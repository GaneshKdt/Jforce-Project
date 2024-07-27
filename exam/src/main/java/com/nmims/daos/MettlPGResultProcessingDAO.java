package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ConsumerType;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.MettlEvaluatorInfo;
import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlResultQuestionResponse;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.MettlSectionQuestionResponse;
import com.nmims.beans.MettlStudentSectionInfo;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramStructureBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.QuestionFileBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.helpers.DateHelper;

@Repository("mettlPGResultProcessingDAO")
public class MettlPGResultProcessingDAO extends BaseDAO {

	private static HashMap<String, BigDecimal> hashMap = null;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;

	@Value("${SERVER}")
	private String SERVER;
	public static final Logger logger = LoggerFactory.getLogger(MettlPGResultProcessingDAO.class);
	public static final Logger transferMettlMarksToOnlineMarks = LoggerFactory.getLogger("transferMettlMarksToOnlineMarks");
	
	//start - DB Constants - added on 2022-02-21 by Vilpesh
	
	private static final String DB_CURRENT_TIMESTAMP = "current_timestamp";
	public static final String METTL_MARKS_NOTPROCESSED = "N";
	public static final String METTL_MARKS_TO_ONLINE_MARKS_N = "N";
	
	//start - DB Constants - added on 2022-02-23 by Vilpesh
	public static final String MARKS_NOTPROCESSED = "N";
	public static final String METTL_MARKS_TO_ONLINE_MARKS_Y = "Y";
	public static final String METTL_MARKS_STATUS_ATTEMPTED = "Attempted";
	public static final String MARKS_PARAM_WRITTEN_STR = "written";
	//end - DB Constants
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	@Transactional(readOnly = true)
	public List<MettlResultsSyncBean> getListOfSchedulesForExam(MettlResultsSyncBean inputBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String conditions = "";
		List<String> parameters = new ArrayList<String>();

		if(!StringUtils.isBlank(inputBean.getSifySubjectCode()) && !inputBean.getSifySubjectCode().equals("All")) {
			conditions += " AND `ea`.`sifyCode` = ? ";
			parameters.add(inputBean.getSifySubjectCode());
		}
		if(!StringUtils.isBlank(inputBean.getFromDate()) && !inputBean.getFromDate().equals("All")) {
			conditions += " AND DATE(`es`.`exam_start_date_time`) >= ? ";
			parameters.add(inputBean.getFromDate());
		}
		if(!StringUtils.isBlank(inputBean.getToDate()) && !inputBean.getToDate().equals("All")) {
			conditions += " AND DATE(`es`.`exam_start_date_time`) <= ? ";
			parameters.add(inputBean.getToDate());
		}

		if(inputBean.getCustomFetchInput() != null) {
			String listOfAccessKeys = "";
			List<MettlPGResponseBean> beans = inputBean.getCustomFetchInput();
			for (MettlPGResponseBean bean : beans) {
				listOfAccessKeys += "'" + bean.getSchedule_accessKey() + "',";
			}
			listOfAccessKeys += "''";
			conditions += " AND `es`.`schedule_accessKey` IN ( " + listOfAccessKeys +  " )";
		}
		
		String sql = ""
				+ " SELECT * "
				+ " FROM `exam`.`pg_assessment` `ea` "
				+ " INNER JOIN `exam`.`exams_schedule_mettl` `es` ON `es`.`assessments_id` = `ea`.`assessmentId` "
				+ " WHERE 1 "
				+ conditions;
		return jdbcTemplate.query(
			sql,
			parameters.toArray(),
			new BeanPropertyRowMapper<MettlResultsSyncBean>(MettlResultsSyncBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<MettlPGResponseBean> getListOfBookingsToProcessResults(MettlResultsSyncBean inputBean){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String ebConditions = "";
		String conditions = "";
		List<String> parameters = new ArrayList<String>();

		if(!StringUtils.isBlank(inputBean.getProgramId()) && !inputBean.getProgramId().equals("All")) {
			conditions += " AND `cps`.`programId` = ? ";
			parameters.add(inputBean.getProgramId());
		}
		if(!StringUtils.isBlank(inputBean.getProgramStructureId()) && !inputBean.getProgramStructureId().equals("All")) {
			conditions += " AND `cps`.`programStructureId` = ? ";
			parameters.add(inputBean.getProgramStructureId());
		}
		if(!StringUtils.isBlank(inputBean.getConsumerTypeId()) && !inputBean.getConsumerTypeId().equals("All")) {
			conditions += " AND `cps`.`consumerTypeId` = ? ";
			parameters.add(inputBean.getConsumerTypeId());
		}
//		if(inputBean.getCustomFetchInput() != null) {
//			String listOfSapidsAndAccessKeys = "";
//			for(MettlPGResponseBean bean: inputBean.getCustomFetchInput()) {
//				listOfSapidsAndAccessKeys += "('" + bean.getSapid() + "', '" + bean.getSchedule_accessKey() + "'),";
//			}
//			listOfSapidsAndAccessKeys += "('', '')";
//			conditions += " AND (`eb`.`sapid`, `es`.`schedule_accessKey`) IN ( " + listOfSapidsAndAccessKeys + " ) ";
//		}
//	
		// Condition to specifically fetch certain students data.
//		if(inputBean.getCustomFetchInput() != null) {
//			String listOfSapids = "";
//			for(MettlPGResponseBean bean: inputBean.getCustomFetchInput()) {
//				listOfSapids += "'" + bean.getSapid() + "',";
//			}
//			listOfSapids += "('')";
//			ebConditions += " AND `sapid` IN ( " + listOfSapids + " ) ";
//		}
	
		if(!StringUtils.isBlank(inputBean.getSifySubjectCode()) && !inputBean.getSifySubjectCode().equals("All")) {
			conditions += " AND `pss`.`sifySubjectCode` = ? ";
			parameters.add(inputBean.getSifySubjectCode());
		}
		if(!StringUtils.isBlank(inputBean.getFromDate()) && !inputBean.getFromDate().equals("All")) {
			conditions += " AND `eb`.`examDate` >= ? ";
			parameters.add(inputBean.getFromDate());
		}
		if(!StringUtils.isBlank(inputBean.getToDate()) && !inputBean.getToDate().equals("All")) {
			conditions += " AND `eb`.`examDate` <= ? ";
			parameters.add(inputBean.getToDate());
		}
		

		if(!StringUtils.isBlank(inputBean.getExamYear()) && !inputBean.getExamYear().equals("All")) {
			ebConditions += " AND `year` = '" + inputBean.getExamYear() + "' ";
		}
		if(!StringUtils.isBlank(inputBean.getExamMonth()) && !inputBean.getExamMonth().equals("All")) {
			ebConditions += " AND `month` = '" + inputBean.getExamMonth() + "' ";
		}
		
		if(inputBean.getCustomFetchInput() != null) {
			String listOfSapidsAndSubjects = "";
			for(MettlPGResponseBean bean: inputBean.getCustomFetchInput()) {
				listOfSapidsAndSubjects += "('" + bean.getSapid() + "', '" + bean.getSubject() + "'),";
			}
			listOfSapidsAndSubjects += "('', '')";
			ebConditions += " AND (`sapid`, `subject`) IN ( " + listOfSapidsAndSubjects + " ) ";
		}
		
		String sql = ""
				+ " SELECT "
					+ " CONCAT(`s`.`firstName`, ' ', `s`.`lastName`) AS `student_name`, "
					+ " `ps`.`programType` , "
					+ " `s`.`consumerType` AS `studentType`, "
					+ " `pss`.`sem`, "
					+ " `pss`.`sifySubjectCode`, "
					+ " `pss`.`id` AS `prgm_sem_subj_id`, "
					+ " `eb`.`sapid`, "
					+ " `eb`.`program`, "
					+ " `eb`.`subject`, "
					+ " `eb`.`emailId` AS `email`, "
					+ " `eb`.`year`, "
					+ " `eb`.`month`, "
					+ " `es`.`exam_start_date_time` AS `startOnDate`, "
					+ " `es`.* "
				+ " FROM ( "
						+ " SELECT "
							+ " `examDate`, `examTime`, `examEndTime`, "
							+ " `emailId`, `sapid`, `program`, `subject`,"
							+ " `year`, `month`, sem  "
						+ " FROM `exam`.`exambookings` "
						+ " WHERE `booked` = 'Y' "
						+ ebConditions
				+ " ) `eb` "
				+ " INNER JOIN ( "
					+ " SELECT "
						+ " `s2`.`sapid` AS `sapid`, "
						+ " `s2`.`firstName` AS `firstName`, "
						+ " `s2`.`lastName` AS `lastName`, "
						+ " `s2`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
						+ " `s2`.`consumerType` "
					+ " FROM `exam`.`students` `s2` "
					+ " WHERE "
						+ " `s2`.`sem` = ( "
							+ " SELECT MAX(`s3`.`sem`) "
							+ " FROM `exam`.`students` `s3` "
							+ " WHERE `s2`.`sapid` = `s3`.`sapid` "
						+ " ) "
						+ " and (s2.isLateral = 'N' AND s2.programChanged IS NULL)"
				+ " ) `s` ON `s`.`sapid` = `eb`.`sapid` "
				+ "  INNER JOIN "
				+ "  `exam`.`registration` r ON r.sapid = `eb`.`sapid` and `eb`.`sem` = r.sem  "
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `r`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` "
					+ " AND `eb`.`subject` = `pss`.`subject` "
				+ " INNER JOIN `exam`.`exams_schedule_mettl` `es` "
					+ "ON `es`.`schedule_name` = CONCAT( "
						+ " `pss`.`sifySubjectCode`, "
						+ " DATE_FORMAT(`eb`.`examDate`, '%w%m%e%Y'), "
						+ " DATE_FORMAT(`eb`.`examTime`, '%H%i%s'), "
						+ " DATE_FORMAT(`eb`.`examEndTime`, '%H%i%s') "
					+ " ) "
				+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
				+ " INNER JOIN `exam`.`programs` `ps` ON `ps`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` "
				+ " WHERE 1 "
				+ conditions
				+ " ORDER BY `examDate`, `examTime`";

		String sqlLateralY = ""
				+ " SELECT "
					+ " CONCAT(`s`.`firstName`, ' ', `s`.`lastName`) AS `student_name`, "
					+ " `ps`.`programType`, "
					+ " `s`.`consumerType` AS `studentType`, "
					+ " `pss`.`sem`, "
					+ " `pss`.`sifySubjectCode`, "
					+ " `pss`.`id` AS `prgm_sem_subj_id`, "
					+ " `eb`.`sapid`, "
					+ " `eb`.`program`, "
					+ " `eb`.`subject`, "
					+ " `eb`.`emailId` AS `email`, "
					+ " `eb`.`year`, "
					+ " `eb`.`month`, "
					+ " `es`.`exam_start_date_time` AS `startOnDate`, "
					+ " `es`.* "
				+ " FROM ( "
						+ " SELECT "
							+ " `examDate`, `examTime`, `examEndTime`, "
							+ " `emailId`, `sapid`, `program`, `subject`,"
							+ " `year`, `month`, sem  "
						+ " FROM `exam`.`exambookings` "
						+ " WHERE `booked` = 'Y' "
						+ ebConditions
				+ " ) `eb` "
				+ " INNER JOIN ( "
					+ " SELECT "
						+ " `s2`.`sapid` AS `sapid`, "
						+ " `s2`.`firstName` AS `firstName`, "
						+ " `s2`.`lastName` AS `lastName`, "
						+ " `s2`.`consumerProgramStructureId` AS `consumerProgramStructureId`, "
						+ " `s2`.`consumerType` "
					+ " FROM `exam`.`students` `s2` "
					+ " WHERE "
						+ " `s2`.`sem` = ( "
							+ " SELECT MAX(`s3`.`sem`) "
							+ " FROM `exam`.`students` `s3` "
							+ " WHERE `s2`.`sapid` = `s3`.`sapid` "
						+ " ) "
						+ " and (s2.isLateral = 'Y' OR s2.programChanged='Y')"
				+ " ) `s` ON `s`.`sapid` = `eb`.`sapid` "
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `s`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` "
					+ " AND `eb`.`subject` = `pss`.`subject` "
				+ " INNER JOIN `exam`.`exams_schedule_mettl` `es` "
					+ "ON `es`.`schedule_name` = CONCAT( "
						+ " `pss`.`sifySubjectCode`, "
						+ " DATE_FORMAT(`eb`.`examDate`, '%w%m%e%Y'), "
						+ " DATE_FORMAT(`eb`.`examTime`, '%H%i%s'), "
						+ " DATE_FORMAT(`eb`.`examEndTime`, '%H%i%s') "
					+ " ) "
				+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
				+ " INNER JOIN `exam`.`programs` `ps` ON `ps`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` "
				+ " WHERE 1 "
				+ conditions
				+ " ORDER BY `examDate`, `examTime`";
		List<MettlPGResponseBean> list1 = jdbcTemplate.query(
				sql,
				parameters.toArray(),
				new BeanPropertyRowMapper<MettlPGResponseBean>(MettlPGResponseBean.class)
			);
		
		List<MettlPGResponseBean> list2 = jdbcTemplate.query(
				sqlLateralY,
				parameters.toArray(),
				new BeanPropertyRowMapper<MettlPGResponseBean>(MettlPGResponseBean.class)
			);
		
		List<MettlPGResponseBean> finallist = new ArrayList<>();
		if(list1 != null && list1.size() >0 ) {
			finallist.addAll(list1);
		}
		if(list2 != null && list2.size() >0 ) {
			finallist.addAll(list2);
		}
		return finallist;
	}
	
	@Transactional(readOnly = true)
	public List<ConsumerType> getConsumerTypeList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT `id`, `name` FROM `exam`.`consumer_type` ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<ConsumerType>(ConsumerType.class));
	}
	
	@Transactional(readOnly = true)
	public List<ProgramStructureBean> getProgramStructureList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT `id`, `program_structure` FROM `exam`.`program_structure` ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProgramStructureBean>(ProgramStructureBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<ProgramExamBean> getProgramList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT `id`, `code` FROM `exam`.`program` ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProgramExamBean>(ProgramExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getSubjectsList(ConsumerProgramStructureExam cps) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `sifySubjectCode`, `subject` "
				+ " FROM `exam`.`program_sem_subject` `pss` "
				+ " INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id` = `pss`.`consumerProgramStructureId` "
				+ " WHERE `active` = 'Y' ";

		String conditions = "";
		List<String> parameters = new ArrayList<String>();

		if(!StringUtils.isBlank(cps.getProgramId()) && !cps.getProgramId().equals("All")) {
			conditions += " AND `cps`.`programId` = ? ";
			parameters.add(cps.getProgramId());
		}
		if(!StringUtils.isBlank(cps.getProgramStructureId()) && !cps.getProgramStructureId().equals("All")) {
			conditions += " AND `cps`.`programStructureId` = ? ";
			parameters.add(cps.getProgramStructureId());
		}
		if(!StringUtils.isBlank(cps.getConsumerTypeId()) && !cps.getConsumerTypeId().equals("All")) {
			conditions += " AND `cps`.`consumerTypeId` = ? ";
			parameters.add(cps.getConsumerTypeId());
		}
		
		return jdbcTemplate.query(
			sql + conditions,
			parameters.toArray(),
			new BeanPropertyRowMapper<ProgramSubjectMappingExamBean>(ProgramSubjectMappingExamBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfRecordExistsInMettlMarks(MettlPGResponseBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = ""
			//+ " SELECT count(*) "
			+ " SELECT count(sapid) "
			+ " FROM exam.mettl_marks "
			+ " WHERE sapid = ? "
			+ " AND subject = ? "
			+ " AND year = ? "
			+ " AND month = ? ";
		int count = jdbcTemplate.queryForObject(
			sql, 
			new Object[] { 
				bean.getSapid(), bean.getSubject(),
				bean.getYear(), bean.getMonth()
			},
			Integer.class
		);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = false)
	public int updateMettlMarksStatus(MettlPGResponseBean m){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`mettl_marks` "
				+ " SET "
					+ " `schedule_id`=?, `student_name` = ?, `studentType` = ?, "
					+ " `totalMarks` = ?, `max_score` = ?, `status` = ?, "
					+ " `section1_marks` = ?, `section2_marks` = ?, `section3_marks` = ?, `section4_marks` = ?, "
					+ " `section1_maxmarks` = ?, `section2_maxmarks` = ?, `section3_maxmarks` = ?, `section4_maxmarks` = ?, "
					+ " `processed` = ?, `movedToOnlineMarks` = ?, `lastModifiedBy` = ?, `updated_at` = " + DB_CURRENT_TIMESTAMP //sysdate() " //sysdate replaced on 2022-02-21 by Vilpesh
				+ " WHERE "
					+ " `subject` = ? AND `sapid` = ? AND `year` = ? AND `month` = ?";
		return jdbcTemplate.update(sql, new Object[] {
				m.getSchedule_id(), m.getStudent_name(), m.getStudentType(),
				m.getTotalMarks(), m.getMax_marks(), m.getStatus(),
				m.getSection1_marks(), m.getSection2_marks(), m.getSection3_marks(), m.getSection4_marks(),
				m.getSection1_maxmarks(), m.getSection2_maxmarks(), m.getSection3_maxmarks(), m.getSection4_maxmarks(),
				//"N", "N", m.getLastModifiedBy(), //N replaced on 2022-02-21 by Vilpesh
				METTL_MARKS_NOTPROCESSED, METTL_MARKS_TO_ONLINE_MARKS_N, m.getLastModifiedBy(),
				m.getSubject(), m.getSapid(), m.getYear(), m.getMonth()
		});
		
	}
	
	@Transactional(readOnly = false)
	public int insertMettlMarksStatus(MettlPGResponseBean m){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO exam.mettl_marks ( "
					+ " `schedule_id`, `subject`, `prgm_sem_subj_id`, `studentType`, "
					+ " `year`, `month`, `student_name`, "
					+ " `sapid`, `totalMarks`, `max_score`, `processed`, "
					+ " `section1_marks`, `section2_marks`, `section3_marks`, `section4_marks`, "
					+ " `section1_maxmarks`, `section2_maxmarks`, `section3_maxmarks`, `section4_maxmarks`, "
					+ " `status`, `createdBy`, `lastModifiedBy`, `movedToOnlineMarks`, "
					+ " `created_at`, `updated_at` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, ?, "
					+ " ?, ?, ?, ?, "
					//+ " sysdate(), sysdate() " //sysdate replaced on 2022-02-21 by Vilpesh
					+ DB_CURRENT_TIMESTAMP + ", " + DB_CURRENT_TIMESTAMP
				+ ")";
		return jdbcTemplate.update(sql, new Object[] { 
				m.getSchedule_id(), m.getSubject(), m.getPrgm_sem_subj_id(), m.getStudentType(),
				m.getYear(), m.getMonth(), m.getStudent_name(),
				m.getSapid(), m.getTotalMarks(), m.getMax_marks(), METTL_MARKS_NOTPROCESSED, //"N",
				m.getSection1_marks(), m.getSection2_marks(), m.getSection3_marks(), m.getSection4_marks(),
				m.getSection1_maxmarks(), m.getSection2_maxmarks(), m.getSection3_maxmarks(), m.getSection4_maxmarks(),
				m.getStatus(), m.getCreatedBy(), m.getLastModifiedBy(), METTL_MARKS_TO_ONLINE_MARKS_N //"N" //N replaced on 2022-02-21 by Vilpesh
		});
	}
	
	@Transactional(readOnly = true)
	public List<MettlPGResponseBean> getMarksForTransfer(MettlPGResponseBean inputBean) throws ParseException{
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MettlPGResponseBean> marksList = null;

		String sql=""
				+ " SELECT "
					+ " `m`.*, "
					+ " CONCAT(`s`.`firstName`, ' ', `s`.`lastName`) AS `student_name`"
				+ " FROM `exam`.`mettl_marks` `m` "
				+ " INNER JOIN ( "
					+ " SELECT "
						+ " `s2`.`sapid` AS `sapid`, "
						+ " `s2`.`firstName` AS `firstName`, "
						+ " `s2`.`lastName` AS `lastName`, "
						+ " `s2`.`consumerProgramStructureId` AS `consumerProgramStructureId` "
					+ " FROM `exam`.`students` `s2` "
					+ " WHERE "
						+ " `s2`.`sem` = ( "
							+ " SELECT MAX(`s3`.`sem`) "
							+ " FROM `exam`.`students` `s3` "
							+ " WHERE `s2`.`sapid` = `s3`.`sapid` "
						+ " ) "
				+ " ) `s` ON `s`.`sapid` = `m`.`sapid` "
				
				//+ " WHERE `year` = ? AND `month` = ? AND `movedToOnlineMarks` <> 'Y' AND `status` = 'Attempted' ";
				+ " WHERE `year` = ? AND `month` = ? AND `movedToOnlineMarks` <> '"+METTL_MARKS_TO_ONLINE_MARKS_Y+"' AND `status` = '"+METTL_MARKS_STATUS_ATTEMPTED+"' ";//by Vilpesh 2022-02-23
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		parameters.add(inputBean.getYear());
		parameters.add(inputBean.getMonth());
	 
		try {
			Object[] args = parameters.toArray();
			marksList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<MettlPGResponseBean>(MettlPGResponseBean.class));
		} catch (DataAccessException e) {
			transferMettlMarksToOnlineMarks.error("\n" + SERVER + ": " + DateHelper.currentDateTime(null)
					+ " : getMarksForTransfer : " + e.getMessage());//by Vilpesh 2022-02-23
		}
		return marksList;
	}

	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getConfirmedBookingForGivenYearMonth(String sapid, String subject, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " "
				+ " SELECT * "
				+ " FROM `exam`.`exambookings` "
				+ " WHERE `year` = ? "
				+ " AND `month` = ? "
				+ " AND `sapid` = ? "
				+ " AND `subject` = ? "
				+ " AND `booked` = 'Y' "
				+ "UNION"
				+ " SELECT * "
				+ " FROM `exam`.`exambookings_history` "
				+ " WHERE `year` = ? "
				+ " AND `month` = ? "
				+ " AND `sapid` = ? "
				+ " AND `subject` = ? "
				+ " AND `booked` = 'Y' ";

		return jdbcTemplate.query(
			sql, 
			new Object[]{
				year, month, sapid, subject,
				year, month, sapid, subject
			}, 
			new BeanPropertyRowMapper<ExamBookingTransactionBean>(ExamBookingTransactionBean.class)
		);
	}
	
	
	public void batchUpsertOnlineExamMarks(List<OnlineExamMarksBean> listToTransfer, List<OnlineExamMarksBean> successList, List<OnlineExamMarksBean> errorList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		StudentMarksBean studentMarksBean = null;
		OnlineExamMarksBean bean = null;
		int listToTransferSize = 0;
		listToTransferSize = listToTransfer.size();//by Vilpesh 2022-02-23
		for (i = 0; i < listToTransferSize; i++) {
			bean = listToTransfer.get(i);

			transferMettlMarksToOnlineMarks.info(
				"\n"+SERVER+": "
				//+ new Date()+" transferScoresToOnlineMarks Batch Upsert "
				+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Batch Upsert "  //by Vilpesh 2022-02-23
				+ (i + 1) + "/" + listToTransferSize
			);
			try{
				upsertOnlineExamMarks(bean, jdbcTemplate);
				studentMarksBean = convertBean(bean);
				upsertMarks(studentMarksBean, jdbcTemplate, "written");
				successList.add(bean);
			}catch(Exception e){
				transferMettlMarksToOnlineMarks.error(
					"\n"+SERVER+": "
					+ DateHelper.currentDateTime(null)+" : transferScoresToOnlineMarks : Batch Upsert Error"
					+ e.getMessage()
					+ new Gson().toJson(bean)
					
				);
				
				errorList.add(bean);
			}
		}
	}
	
	//new method that updates data in online marks and marks table
	public int upsertOnlineMarksAndMarks(OnlineExamMarksBean onlineExamMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int onlineMarksInserted = upsertOnlineExamMarks(onlineExamMarks, jdbcTemplate);
		
		//doesn't update marks table if no entries were made in online marks
		if(onlineMarksInserted > 0) {
			//converts to marks bean
			StudentMarksBean studentMarksBean = convertBean(onlineExamMarks);
			upsertMarks(studentMarksBean, jdbcTemplate, MARKS_PARAM_WRITTEN_STR);
		}
		return onlineMarksInserted;
	}
	
	@Transactional(readOnly = false)
	private int upsertOnlineExamMarks(OnlineExamMarksBean bean, JdbcTemplate jdbcTemplate2) {
		String sql = "INSERT INTO exam.online_marks (year, month, sapid, subject,program, sem, name, total, roundedTotal, "
				+ "part1marks, part2marks, part3marks, part4marks, studentType, createdBy, createdDate, "
				+ "lastModifiedBy, lastModifiedDate) VALUES "
				//+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+DB_CURRENT_TIMESTAMP+",?,"+DB_CURRENT_TIMESTAMP+")" //by Vilpesh 2022-02-23
				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    subject = ?,"
				+ "	    name = ?,"
				+ "	    program = ?,"
				+ "	    sem = ?,"
				+ "	    total = ?,"
				+ "	    roundedTotal = ?,"
				+ "	    part1marks = ?,"
				+ "	    part2marks = ?,"
				+ "	    part3marks = ?,"
				+ "	    part4marks = ?,"
				+ "     studentType = ?,"
				+ "	    lastModifiedBy = ?,"
				//+ "	    lastModifiedDate = sysdate()";
				+ "	    lastModifiedDate = " + DB_CURRENT_TIMESTAMP;//by Vilpesh 2022-02-23
      
		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String program = bean.getProgram();
		String sem = bean.getSem();
		String name = bean.getName();
		double total = bean.getTotal();
		String roundedTotal = bean.getRoundedTotal();
		double part1marks = bean.getPart1marks();
		double part2marks = bean.getPart2marks();
		double part3marks = bean.getPart3marks();
		double part4marks = bean.getPart4marks();
		String studentType = bean.getStudentType();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		return jdbcTemplate2.update(sql, new Object[] { 
				year,
				month,
				sapid,
				subject,
				program,
				sem,
				name,
				total,
				roundedTotal,
				part1marks,
				part2marks,
				part3marks,
				part4marks,
				studentType,
				createdBy,
				lastModifiedBy,
				sapid,
				subject,
				name,
				program,
				sem,
				total,
				roundedTotal,
				part1marks,
				part2marks,
				part3marks,
				part4marks,
				studentType,
				lastModifiedBy
		});

	}
	

	private StudentMarksBean convertBean(OnlineExamMarksBean bean) {
		StudentMarksBean studentMarksBean = new StudentMarksBean();
		studentMarksBean.setSapid(bean.getSapid());
		studentMarksBean.setGrno("Not Available");
		studentMarksBean.setStudentname(bean.getName());
		studentMarksBean.setSubject(bean.getSubject());
		studentMarksBean.setWritenscore(bean.getRoundedTotal());
		studentMarksBean.setYear(bean.getYear());
		studentMarksBean.setMonth(bean.getMonth());
		studentMarksBean.setCreatedBy(bean.getCreatedBy());
		studentMarksBean.setLastModifiedBy(bean.getLastModifiedBy());
		studentMarksBean.setProgram(bean.getProgram());
		studentMarksBean.setSem(bean.getSem());
		studentMarksBean.setStudentType(bean.getStudentType());
		return studentMarksBean;
	}
	
	public void upsertMarks(StudentMarksBean bean, JdbcTemplate jdbcTemplate, String type) {
		boolean recordExists = checkIfRecordExists(bean, jdbcTemplate);
		if(recordExists){
			updateStudentMarksForUpsert(bean, jdbcTemplate, type);
		}else{
			insertStudentMarksForUpsert(bean, jdbcTemplate);
		}
	}

	@Transactional(readOnly = true)
	private boolean checkIfRecordExists(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}

		//String sql = "SELECT count(*) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";
		String sql = "SELECT count(id) FROM exam.marks where year = ? and month = ? and sapid = ? and subject = ?";//by Vilpesh 2022-02-23

		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getYear(),
				bean.getMonth(),
				bean.getSapid(),
				bean.getSubject()
		},Integer.class);

		if(count == 0){
			return false;
		}else{
			return true;
		}

	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateStudentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate, String type){
		if("written".equalsIgnoreCase(type)){
			updateWrittenMarksForUpsert(m, jdbcTemplate);
		}else{
			updateAsignmentMarksForUpsert(m, jdbcTemplate);
		}


	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateWrittenMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.marks set "
				+ " writenscore=?, "
				+ " grno=?, "
				+ " program=?, "
				+ " sem=?, "
				//+ " processed = 'N', "
				+ " processed = '"+MARKS_NOTPROCESSED+"', " //by Vilpesh 2022-02-23
				+ " studentType = ?,"
				+ " lastModifiedBy=?, "
				+ " remarks=?, "
				//+ " lastModifiedDate=sysdate() "
				+ " lastModifiedDate="+DB_CURRENT_TIMESTAMP //by Vilpesh 2022-02-23
				+ " where year =? and month=? and sapid = ? and subject = ?";

		jdbcTemplate.update(sql, new Object[] { 
				m.getWritenscore(),
				m.getGrno(),
				m.getProgram(),
				m.getSem(),
				m.getStudentType(),
				m.getLastModifiedBy(),
				m.getRemarks(),
				m.getYear(),
				m.getMonth(),
				m.getSapid(),
				m.getSubject()
		});

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAsignmentMarksForUpsert(StudentMarksBean m, JdbcTemplate jdbcTemplate){
		String sql = "Update exam.marks set "
				+ "year=?,"
				+ "month=?,"
				//	+ "examorder=?,"
				+ "grno=?,"
				+ "sapid=?,"
				+ "studentname=?,"
				+ "program=?,"
				+ "sem=?,"
				+ "subject=?,"
				+ "assignmentscore=?,"
				//+ "gracemarks=?,"
				//+ "total=?,"
				+ "attempt=?,"
				+ "source=?,"
				+ "location=?,"
				+ "centercode=?,"
				+ "remarks=?, "
				+ "syllabusYear=?, "
				//+ "processed = 'N', "
				+ "processed = '"+MARKS_NOTPROCESSED+"', " //by Vilpesh 2022-02-23
				+ "studentType=?, "
				+ "lastModifiedBy=?, "
				//+ "lastModifiedDate=sysdate() "
				+ "lastModifiedDate="+DB_CURRENT_TIMESTAMP //by Vilpesh 2022-02-23
				+ " where year =? and month=? and sapid = ? and subject = ?";


		jdbcTemplate.update(sql, new Object[] { 
				m.getYear(),
				m.getMonth(),
				//		getExamOrder(m.getMonth().trim(),m.getYear().trim()),
				m.getGrno(),
				m.getSapid(),
				m.getStudentname(),
				m.getProgram(),
				m.getSem(),
				m.getSubject(),
				m.getAssignmentscore(),
				//m.getGracemarks(),
				//m.getTotal(),
				m.getAttempt(),
				m.getSource(),
				m.getLocation(),
				m.getCentercode(),
				m.getRemarks(),
				m.getSyllabusYear(),
				m.getStudentType(),
				m.getLastModifiedBy(),
				m.getYear(),
				m.getMonth(),
				m.getSapid(),
				m.getSubject()
		});

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertStudentMarksForUpsert(StudentMarksBean marksBean, JdbcTemplate jdbcTemplate){

		final String sql = " INSERT INTO exam.marks(		"
				+ "year,"
				+ "month,"
				+ "examorder,"
				+ "grno,"
				+ "sapid,"
				+ "studentname,"
				+ "program,"
				+ "sem,"
				+ "subject,"
				+ "writenscore,"
				+ "assignmentscore,"
				+ "gracemarks,"
				+ "total,"
				+ "attempt,"
				+ "source,"
				+ "location,"
				+ "centercode,"
				+ "remarks,"
				+ "syllabusYear,"
				+ "studentType,"
				+ "createdBy,"
				+ "createdDate,"
				+ "lastModifiedBy,"
				+ "lastModifiedDate,"
				+ " processed)"
				//+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),'N')";
				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+DB_CURRENT_TIMESTAMP+",?,"+DB_CURRENT_TIMESTAMP+",'"+MARKS_NOTPROCESSED+"')";//by Vilpesh 2022-02-23


		final StudentMarksBean m = marksBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, m.getYear());
				ps.setString(2, m.getMonth());
				ps.setString(3, getExamOrder(m.getMonth().trim(),m.getYear().trim()));
				ps.setString(4, m.getGrno());
				ps.setString(5, m.getSapid());
				ps.setString(6, m.getStudentname());
				ps.setString(7, m.getProgram());
				ps.setString(8, m.getSem());
				ps.setString(9, m.getSubject());
				ps.setString(10, m.getWritenscore());
				ps.setString(11, m.getAssignmentscore());
				ps.setString(12, m.getGracemarks());
				ps.setString(13, m.getTotal());
				ps.setString(14, m.getAttempt());
				ps.setString(15, m.getSource());
				ps.setString(16, m.getLocation());
				ps.setString(17, m.getCentercode());
				ps.setString(18, m.getRemarks());
				ps.setString(19, m.getSyllabusYear());
				ps.setString(20, m.getStudentType());
				ps.setString(21, m.getCreatedBy());
				ps.setString(22, m.getLastModifiedBy());

				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(psc, keyHolder);

		int id = keyHolder.getKey().intValue();
		return id;
	}
	

	public String getExamOrder(String month, String year) throws SQLException{
		String examOrder = "0";
		HashMap<String, BigDecimal> hashMap = getExamOrderMap();
		BigDecimal examOrderDecimal = hashMap.get(month+year);
		BigDecimal toSubtract;
		if(examOrderDecimal != null){
			examOrder = examOrderDecimal.toString();

			return examOrder;
		}else {
			BigDecimal tempExamOrderDecimal = hashMap.get("Dec"+year);
			if("Jan".equals(month)) {
				toSubtract= new BigDecimal("1.9");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("Feb".equals(month)) {
				toSubtract= new BigDecimal("1.8");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("Mar".equals(month)) {
				toSubtract= new BigDecimal("1.7");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}else if("May".equals(month)) {
				toSubtract= new BigDecimal("1.4");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Jul".equals(month)) {
				toSubtract= new BigDecimal("0.9");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Aug".equals(month)) {
				toSubtract= new BigDecimal("0.8");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Oct".equals(month)) {
				toSubtract= new BigDecimal("0.4");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			} else if("Nov".equals(month)) {
				toSubtract= new BigDecimal("0.3");
				return tempExamOrderDecimal.subtract(toSubtract).toString();
			}
		}
		if("0".equals(examOrder)){
			throw new SQLException("Exam order not found");
		}
		return examOrder;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, BigDecimal> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));
			hashMap = new HashMap<String, BigDecimal>();
			for (ExamOrderExamBean row : rows) {
				hashMap.put(row.getMonth()+row.getYear(), BigDecimal.valueOf(Double.parseDouble(row.getOrder())));
			}
		}
		return hashMap;
	}
	
	@Transactional(readOnly = false)
	public int changeTransferStatusInMettlMarks(OnlineExamMarksBean bean, String userId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`mettl_marks` "
				//+ " SET `movedToOnlineMarks` = 'Y', `lastModifiedBy` = ?, `updated_at` = sysdate() "
				+ " SET `movedToOnlineMarks` = '"+METTL_MARKS_TO_ONLINE_MARKS_Y+"', `lastModifiedBy` = ?, `updated_at` = "+DB_CURRENT_TIMESTAMP //by Vilpesh 2022-02-23
				+ " WHERE `subject` = ? AND `sapid` = ? AND `year` = ? AND `month` = ? ";
		
		return jdbcTemplate.update(
			sql, 
			new Object[] {
				userId, bean.getSubject(), bean.getSapid(), bean.getYear(), bean.getMonth()
			}
		);
	}
	
	@Transactional(readOnly = false)
	public void batchUpsertMettlStudentMarks(final List<MettlStudentTestInfo> beans) {
		String sql = ""
				+ " INSERT INTO `mettl`.`student_test_info` ( "
					+ " `sapid`, `scheduleAccessKey`, `emailId`, "
					+ " `startTime`, `endTime`, `completionMode`, "
					+ " `totalMarks`, `maxMarks`, `percentile`, "
					+ " `attemptTime`, `candidateCredibilityIndex`, `totalQuestion`, "
					+ " `totalCorrectAnswers`, `totalUnAnswered`, `pdfReport`, "
					+ " `htmlReport` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ? "
				+ " ) ON DUPLICATE KEY UPDATE "
					+ " `emailId` = ?, "
					+ " `startTime` = ?, "
					+ " `endTime` = ?, "
					+ " `completionMode` = ?, "
					+ " `totalMarks` = ?, "
					+ " `maxMarks` = ?, "
					+ " `percentile` = ?, "
					+ " `attemptTime` = ?, "
					+ " `candidateCredibilityIndex` = ?, "
					+ " `totalQuestion` = ?, "
					+ " `totalCorrectAnswers` = ?, "
					+ " `totalUnAnswered` = ?, "
					+ " `pdfReport` = ?, "
					+ " `htmlReport` = ? ";
		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					MettlStudentTestInfo bean = beans.get(index);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getScheduleAccessKey());
					ps.setString(3, bean.getEmailId());
					ps.setString(4, bean.getStartTime());
					ps.setString(5, bean.getEndTime());
					ps.setString(6, bean.getCompletionMode());
					ps.setDouble(7, bean.getTotalMarks());
					ps.setDouble(8, bean.getMaxMarks());
					ps.setDouble(9, bean.getPercentile());
					ps.setInt(10, bean.getAttemptTime());
					ps.setString(11, bean.getCandidateCredibilityIndex());
					ps.setInt(12, bean.getTotalQuestion());
					ps.setInt(13, bean.getTotalCorrectAnswers());
					ps.setInt(14, bean.getTotalUnAnswered());
					ps.setString(15, bean.getPdfReport());
					ps.setString(16, bean.getHtmlReport());
					

					ps.setString(17, bean.getEmailId());
					ps.setString(18, bean.getStartTime());
					ps.setString(19, bean.getEndTime());
					ps.setString(20, bean.getCompletionMode());
					ps.setDouble(21, bean.getTotalMarks());
					ps.setDouble(22, bean.getMaxMarks());
					ps.setDouble(23, bean.getPercentile());
					ps.setInt(24, bean.getAttemptTime());
					ps.setString(25, bean.getCandidateCredibilityIndex());
					ps.setInt(26, bean.getTotalQuestion());
					ps.setInt(27, bean.getTotalCorrectAnswers());
					ps.setInt(28, bean.getTotalUnAnswered());
					ps.setString(29, bean.getPdfReport());
					ps.setString(30, bean.getHtmlReport());
				}
				
				@Override
				public int getBatchSize() {
					return beans.size();
				}
		});
	}
	
	@Transactional(readOnly = false)
	public void batchUpsertMettlStudentSectionInfo(final List<MettlStudentSectionInfo> beans) {
		String sql = ""
				+ " INSERT INTO `mettl`.`student_test_section_info` ( "
					+ " `sapid`, `scheduleAccessKey`, `sectionName`, "
					+ " `sectionNumber`, `totalMarks`, `maxMarks`, "
					+ " `timeTaken`, `totalQuestion`, `totalCorrectAnswers`, "
					+ " `totalUnAnswered` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, ? "
				+ " ) ON DUPLICATE KEY UPDATE "
					+ " `sectionNumber` = ?, "
					+ " `totalMarks` = ?, "
					+ " `maxMarks` = ?, "
					+ " `timeTaken` = ?, "
					+ " `totalQuestion` = ?, "
					+ " `totalCorrectAnswers` = ?, "
					+ " `totalUnAnswered` = ? ";
		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					MettlStudentSectionInfo bean = beans.get(index);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getScheduleAccessKey());
					ps.setString(3, bean.getSectionName());
					ps.setInt(4, bean.getSectionNumber());
					ps.setDouble(5, bean.getTotalMarks());
					ps.setDouble(6, bean.getMaxMarks());
					ps.setInt(7, bean.getTimeTaken());
					ps.setInt(8, bean.getTotalQuestion());
					ps.setInt(9, bean.getTotalCorrectAnswers());
					ps.setInt(10, bean.getTotalUnAnswered());

					ps.setInt(11, bean.getSectionNumber());
					ps.setDouble(12, bean.getTotalMarks());
					ps.setDouble(13, bean.getMaxMarks());
					ps.setInt(14, bean.getTimeTaken());
					ps.setInt(15, bean.getTotalQuestion());
					ps.setInt(16, bean.getTotalCorrectAnswers());
					ps.setInt(17, bean.getTotalUnAnswered());
				}
				
				@Override
				public int getBatchSize() {
					return beans.size();
				}
		});
	}
	
	@Transactional(readOnly = false)
	public void batchUpsertMettlQuestionResponse(final List<MettlSectionQuestionResponse> beans) {
		String sql = ""
				+ " INSERT INTO `mettl`.`student_test_section_question_response` ( "
					+ " `sapid`, `scheduleAccessKey`, `sectionName`, "
					+ " `questionId`, `apiQuestionType`, `version`, "
					+ " `studentResponse`, `minMarks`, `maxMarks`, "
					+ " `marksScored`, `isAttempted`, `timeSpent`, `bonusMarks` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ? "
				+ " ) ON DUPLICATE KEY UPDATE "
					+ " `apiQuestionType` = ?, "
					+ " `version` = ?, "
					+ " `studentResponse` = ?, "
					+ " `minMarks` = ?, "
					+ " `maxMarks` = ?, "
					+ " `marksScored` = ?, "
					+ " `isAttempted` = ?, "
					+ " `timeSpent` = ?, "
					+ " `bonusMarks` = ? ";
		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					MettlSectionQuestionResponse bean = beans.get(index);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getScheduleAccessKey());
					ps.setString(3, bean.getSectionName());
					ps.setString(4, bean.getQuestionId());
					ps.setString(5, bean.getApiQuestionType());
					ps.setString(6, bean.getVersion());
					ps.setString(7, bean.getStudentResponse());
					ps.setDouble(8, bean.getMinMarks());
					ps.setDouble(9, bean.getMaxMarks());
					ps.setDouble(10, bean.getMarksScored());
					ps.setBoolean(11, bean.isAttempted());
					ps.setInt(12, bean.getTimeSpent());
					ps.setDouble(13, bean.getBonusMarks());

					ps.setString(14, bean.getApiQuestionType());
					ps.setString(15, bean.getVersion());
					ps.setString(16, bean.getStudentResponse());
					ps.setDouble(17, bean.getMinMarks());
					ps.setDouble(18, bean.getMaxMarks());
					ps.setDouble(19, bean.getMarksScored());
					ps.setBoolean(20, bean.isAttempted());
					ps.setInt(21, bean.getTimeSpent());
					ps.setDouble(22, bean.getBonusMarks());
				}
				
				@Override
				public int getBatchSize() {
					return beans.size();
				}
		});
	}
	
	@Transactional(readOnly = false)
	public void batchUpsertMettlEvaluatorInfo(final List<MettlEvaluatorInfo> beans) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `mettl`.`student_question_evaluator_info` ( "
					+ " `sapid`, `scheduleAccessKey`, `sectionName`, "
					+ " `questionId`, `evaluatorEmail`, `evaluatorName`, "
					+ " `marksAwarded`, `evaluationComments`, `evaluationTime`, "
					+ " `evaluatorRole` "
				+ " ) VALUES ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ? "
				+ " ) ON DUPLICATE KEY UPDATE  "
					+ " `evaluatorName` = ?, "
					+ " `marksAwarded` = ?, "
					+ " `evaluationComments` = ?, "
					+ " `evaluationTime` = ?, "
					+ " `evaluatorRole` = ? ";
		jdbcTemplate.batchUpdate(
			sql,
			new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					MettlEvaluatorInfo bean = beans.get(index);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getScheduleAccessKey());
					ps.setString(3, bean.getSectionName());
					ps.setString(4, bean.getQuestionId());
					ps.setString(5, bean.getEvaluatorEmail());
					ps.setString(6, bean.getEvaluatorName());
					ps.setDouble(7, bean.getMarksAwarded());
					ps.setString(8, bean.getEvaluationComments());
					ps.setString(9, bean.getEvaluationTime());
					ps.setString(10, bean.getEvaluatorRole());

					ps.setString(11, bean.getEvaluatorName());
					ps.setDouble(12, bean.getMarksAwarded());
					ps.setString(13, bean.getEvaluationComments());
					ps.setString(14, bean.getEvaluationTime());
					ps.setString(15, bean.getEvaluatorRole());
					
				}
				
				@Override
				public int getBatchSize() {
					return beans.size();
				}
		});
	}
	
	@Transactional(readOnly = true)
	public List<MettlSectionQuestionResponse> getStudentResponsesForBenefitOfDoubtQuestion(String questionId,String year,String month) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT stsqr.* "
				+ " FROM `mettl`.`student_test_section_question_response` `stsqr` "
				+ " INNER JOIN `exam`.`exams_schedule_mettl` `esm` ON `esm`.`schedule_accessKey` = `stsqr`.`scheduleAccessKey` "
				+ " INNER JOIN  exam.mettl_marks m  ON m.schedule_id = esm.schedule_id AND m.sapid = stsqr.sapid "
				+ " WHERE `questionId` = ? AND `maxMarks` <> (`bonusMarks` + `marksScored`) "
				+ " AND m.year = ? AND m.month = ? ";
		
		return jdbcTemplate.query(
			sql,
			new Object[] { questionId,year,month },
			new BeanPropertyRowMapper<MettlSectionQuestionResponse>(MettlSectionQuestionResponse.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlStudentTestInfo> getAllStudentTestInfos() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * FROM `mettl`.`student_test_info` `sti` ";
		
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MettlStudentTestInfo>(MettlStudentTestInfo.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public MettlStudentTestInfo getStudentTestInfo(String sapid, String scheduleAccessKey) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `mettl`.`student_test_info` "
				+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? ";

		return jdbcTemplate.queryForObject(
			sql,
			new Object[] { sapid, scheduleAccessKey },
			new BeanPropertyRowMapper<MettlStudentTestInfo>(MettlStudentTestInfo.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlStudentSectionInfo> getStudentTestSectionInfoList(String sapid, String scheduleAccessKey) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `mettl`.`student_test_section_info` "
				+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? ";

		return jdbcTemplate.query(
			sql,
			new Object[] { sapid, scheduleAccessKey },
			new BeanPropertyRowMapper<MettlStudentSectionInfo>(MettlStudentSectionInfo.class)
		);
		
	}

	@Transactional(readOnly = true)
	public List<MettlStudentSectionInfo> getStudentTestSectionInfoListForDQ(String sapid, String scheduleAccessKey) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `mettl`.`student_test_section_info` "
				+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? AND `sectionName` in ('Section 4_10 marks_DQ','Section 4_10 mark_DQ')";

		return jdbcTemplate.query(
			sql,
			new Object[] { sapid, scheduleAccessKey },
			new BeanPropertyRowMapper<MettlStudentSectionInfo>(MettlStudentSectionInfo.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlSectionQuestionResponse> getQuestionsForSection(String sapid, String scheduleAccessKey, String sectionName) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `mettl`.`student_test_section_question_response` "
				+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? AND `sectionName` = ? ";

		return jdbcTemplate.query(
			sql,
			new Object[] { sapid, scheduleAccessKey, sectionName },
			new BeanPropertyRowMapper<MettlSectionQuestionResponse>(MettlSectionQuestionResponse.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlEvaluatorInfo> getEvaluatorInfoForQuestion(String sapid, String scheduleAccessKey,
			String sectionName, String questionId) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT * "
				+ " FROM `mettl`.`student_question_evaluator_info` "
				+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? AND `sectionName` = ? AND `questionId` = ? ";

		return jdbcTemplate.query(
			sql,
			new Object[] { sapid, scheduleAccessKey, sectionName, questionId },
			new BeanPropertyRowMapper<MettlEvaluatorInfo>(MettlEvaluatorInfo.class)
		);
		
	}
	
	@Transactional(readOnly = false)
	public void updateBonusMarksForQuestion(MettlSectionQuestionResponse question) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " UPDATE `mettl`.`student_test_section_question_response` "
			+ " SET `bonusMarks` = ? "
			+ " WHERE `sapid` = ? AND `scheduleAccessKey` = ? AND `sectionName` = ? AND `questionId` = ? ";

		jdbcTemplate.update(
			sql,
			new Object[] {
				question.getBonusMarks(),
				question.getSapid(), question.getScheduleAccessKey(), question.getSectionName(), question.getQuestionId()
			}
		);
	}
	
	@Transactional(readOnly = true)
	public MettlPGResponseBean fetchMarksForStudentTestInfo(MettlStudentTestInfo mettlStudentTestInfo) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT " + 
				"    `mm`.*,pt.programType " + 
				"FROM " + 
				"    `exam`.`mettl_marks` `mm` " + 
				"        INNER JOIN " + 
				"    (SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        `exam`.`exams_schedule_mettl` `em` " + 
				"    WHERE " + 
				"        `em`.`schedule_accessKey` = ? UNION ALL SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        `exam`.`exams_schedule_history_mettl` `e` " + 
				"    WHERE " + 
				"        `e`.`schedule_accessKey` = ? ) es ON `es`.`schedule_id` = `mm`.`schedule_id` " +
				" INNER JOIN "+
                " (select p.programType,pss.id from exam.program_sem_subject pss inner join exam.programs p on p.consumerProgramStructureId=pss.consumerProgramStructureId) pt ON "+
                " `mm`.`prgm_sem_subj_id`=`pt`.`id` "+
				"WHERE " + 
				"    `mm`.`sapid` = ? ";

		return jdbcTemplate.queryForObject(
			sql,
			new Object[] {mettlStudentTestInfo.getScheduleAccessKey() , mettlStudentTestInfo.getScheduleAccessKey(),
					mettlStudentTestInfo.getSapid() },
			new BeanPropertyRowMapper<MettlPGResponseBean>(MettlPGResponseBean.class)
		);
		
	}
	

	@Transactional(readOnly = false)
	public void insertCorrectionInfo(MettlPGResponseBean db, MettlPGResponseBean actual) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " INSERT INTO `temp`.`mettl_correction_list` "
				+ " (`sapid`, `scheduleAccessKey`, `subject`, `section`, `dbMarks`, `actualMarks`) VALUES "
				+ " (?, ?, ?, ?, ?, ?) ";

		jdbcTemplate.update(
			sql,
			new Object[] { db.getSapid(), actual.getSchedule_accessKey(), db.getSubject(), "4", db.getSection4_marks(), actual.getSection4_marks() }
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlStudentTestInfo> getMettlMarksEvaluationInfo(MettlResultsSyncBean inputBean){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String listOfSapidsAndAccessKeys = "";
		if(inputBean.getCustomFetchInput() != null) {
			listOfSapidsAndAccessKeys = getCommaSeparatedSapidAccessKey(inputBean.getCustomFetchInput(), inputBean.getExamYear(), inputBean.getExamMonth());
		}
		
		String sql = ""
			+ " SELECT `t1`.*, `mm`.`student_name`, `mm`.`subject` "
			+ " FROM `mettl`.`student_test_info` `t1` "
			+ " INNER JOIN `exam`.`exams_schedule_mettl` `esm` ON `esm`.`schedule_accessKey` = `t1`.`scheduleAccessKey` "
			+ " INNER JOIN `exam`.`mettl_marks` `mm` ON `t1`.`sapid` = `mm`.`sapid` AND `mm`.`schedule_id` = `esm`.`schedule_id` " 
			+ " WHERE (`t1`.`sapid`, `t1`.`scheduleAccessKey`) IN ( " + listOfSapidsAndAccessKeys + " ) ";
		List<MettlStudentTestInfo> testInfoList = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MettlStudentTestInfo>(MettlStudentTestInfo.class)
		);
		return testInfoList;
	}
	
	@Transactional(readOnly = true)
	public List<MettlStudentSectionInfo> getMettlStudentTestInfoToBean(MettlResultsSyncBean inputBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String listOfSapidsAndAccessKeys = "";
		if(inputBean.getCustomFetchInput() != null) {
			listOfSapidsAndAccessKeys = getCommaSeparatedSapidAccessKey(inputBean.getCustomFetchInput(), inputBean.getExamYear(), inputBean.getExamMonth());
		}
		
		String sql = ""
			+ " SELECT `t1`.*, `mm`.`student_name`, `mm`.`subject` "
			+ " FROM `mettl`.`student_test_section_info` `t1` " 
			+ " INNER JOIN `exam`.`exams_schedule_mettl` `esm` ON `esm`.`schedule_accessKey` = `t1`.`scheduleAccessKey` "
			+ " INNER JOIN `exam`.`mettl_marks` `mm` ON `t1`.`sapid` = `mm`.`sapid` AND `mm`.`schedule_id` = `esm`.`schedule_id` " 
			+ " WHERE (`t1`.`sapid`, `t1`.`scheduleAccessKey`) IN ( " + listOfSapidsAndAccessKeys + " ) ";
		
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MettlStudentSectionInfo>(MettlStudentSectionInfo.class)
		);
		
	}
	
	@Transactional(readOnly = true)
	public List<MettlSectionQuestionResponse> getQuestionInfo(MettlResultsSyncBean inputBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String listOfSapidsAndAccessKeys = "";
		if(inputBean.getCustomFetchInput() != null) {
			listOfSapidsAndAccessKeys = getCommaSeparatedSapidAccessKey(inputBean.getCustomFetchInput(), inputBean.getExamYear(), inputBean.getExamMonth());
		}
		
		String sql = ""
			+ " SELECT `t1`.*, `mm`.`student_name`, `mm`.`subject` "
			+ " FROM `mettl`.`student_test_section_question_response` `t1` "
			+ " INNER JOIN `exam`.`exams_schedule_mettl` `esm` ON `esm`.`schedule_accessKey` = `t1`.`scheduleAccessKey` "
			+ " INNER JOIN `exam`.`mettl_marks` `mm` ON `t1`.`sapid` = `mm`.`sapid` AND `mm`.`schedule_id` = `esm`.`schedule_id` " 
			+ " WHERE (`t1`.`sapid`, `t1`.`scheduleAccessKey`) IN ( " + listOfSapidsAndAccessKeys + " ) ";
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MettlSectionQuestionResponse>(MettlSectionQuestionResponse.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<MettlEvaluatorInfo> getQuestionEvaluatorInfo(MettlResultsSyncBean inputBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String listOfSapidsAndAccessKeys = "";
		if(inputBean.getCustomFetchInput() != null) {
			listOfSapidsAndAccessKeys = getCommaSeparatedSapidAccessKey(inputBean.getCustomFetchInput(), inputBean.getExamYear(), inputBean.getExamMonth());
		}
		
		String sql = ""
			+ " SELECT `t1`.*, `mm`.`student_name`, `mm`.`subject` "
			+ " FROM `mettl`.`student_question_evaluator_info` `t1` "
			+ " INNER JOIN `exam`.`exams_schedule_mettl` `esm` ON `esm`.`schedule_accessKey` = `t1`.`scheduleAccessKey` "
			+ " INNER JOIN `exam`.`mettl_marks` `mm` ON `t1`.`sapid` = `mm`.`sapid` AND `mm`.`schedule_id` = `esm`.`schedule_id` " 
			+ " WHERE (`t1`.`sapid`, `t1`.`scheduleAccessKey`) IN ( " + listOfSapidsAndAccessKeys + " )  ";
		 
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<MettlEvaluatorInfo>(MettlEvaluatorInfo.class)
		);
	}
	
	@Transactional(readOnly = true)
	private String getCommaSeparatedSapidAccessKey(List<MettlPGResponseBean> beans, String year, String month) {
		String listOfSapidsAndSubjects = "";
		for(MettlPGResponseBean bean: beans) {
			listOfSapidsAndSubjects += "('" + bean.getSapid() + "', '" + bean.getSubject() + "'),";
		}
		listOfSapidsAndSubjects += "('', '')";
		String sql = ""
			+ " SELECT `mm`.`sapid`, `es`.`schedule_accessKey` "
			+ " FROM `exam`.`mettl_marks` `mm` "
			+ " INNER JOIN `exam`.`exams_schedule_mettl` `es` "
				+ " ON `es`.`schedule_id` = `mm`.`schedule_id` "
			+ " WHERE `mm`.`year` = ? "
			+ " AND `mm`.`month` = ? "
			+ " AND (`mm`.`sapid`, `mm`.`subject`) IN ( " + listOfSapidsAndSubjects + " ) ";
		
		List<MettlPGResponseBean> respList = jdbcTemplate.query(
			sql,
			new Object[] { year, month },
			new BeanPropertyRowMapper<MettlPGResponseBean>(MettlPGResponseBean.class)
		);
		
		String listOfSapidsAndAccessKeys = "";
		for(MettlPGResponseBean bean: respList) {
			listOfSapidsAndAccessKeys += "('" + bean.getSapid() + "', '" + bean.getSchedule_accessKey() + "'),";
		}
		listOfSapidsAndAccessKeys += "('', '')";
		
		return listOfSapidsAndAccessKeys;
	}
	
	@Transactional(readOnly = true)
	public Map<String, String> getAllQuestions() {
		String sql = ""
			+ " SELECT `questionId`, `questionText` "
			+ " FROM `mettl`.`questions_details` ";
		List<QuestionFileBean> listOfQuestions = jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<QuestionFileBean>(QuestionFileBean.class)
		);
		
		Map<String, String> mapOfQuestions = new HashMap<String, String>();
		for (QuestionFileBean bean : listOfQuestions) {
			mapOfQuestions.put(bean.getQuestionId(), bean.getQuestionText());
		}
		return mapOfQuestions;
	}

	@Transactional(readOnly = false)
	public int insertBodQuestionIds(String examYear, String examMonth, String createdBy, List<String> questionIdsWithoutDuplicate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String SQL = "INSERT INTO `exam`.`pg_bod_questions` (`examYear`, `examMonth`, `question_id`, `created_by`) "
				+ " VALUES  (?, ?, ?, ?)";
		
		int[] rowsAffected = jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, Integer.valueOf(examYear));
				ps.setString(2, examMonth);
				ps.setString(3, questionIdsWithoutDuplicate.get(i));
				ps.setString(4, createdBy);
			}
			
			@Override
			public int getBatchSize() {
				return questionIdsWithoutDuplicate.size();
			}
		});
			
		return Arrays.stream(rowsAffected).sum();
	}

	@Transactional(readOnly = true)
	public Set<String> getBodQuestionIds(String examYear, String examMonth) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT `question_id` FROM `exam`.`pg_bod_questions` WHERE `examYear` = ? AND `examMonth` = ? ";
		
		return jdbcTemplate.query(sql, new Object[] {examYear, examMonth}, new SetResultSetExtractor("question_id"));
	}
	
	class SetResultSetExtractor implements ResultSetExtractor<Set<String>>{
		
		private String columnName;
		
		SetResultSetExtractor(String columnName){
			this.columnName = columnName;
		}

		@Override
		public Set<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Set<String> setOfRecords = new HashSet<String>(rs.getFetchSize());
			
			while(rs.next())
				setOfRecords.add(rs.getString(this.columnName));
			
			return setOfRecords;
		}
		
	}
	
}
