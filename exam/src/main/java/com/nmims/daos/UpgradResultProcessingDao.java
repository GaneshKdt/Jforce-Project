package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.EMBABatchSubjectBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MBAXMarksBean;
import com.nmims.beans.MBAXMarksPreviewBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TEEResultStudentDetailsBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.controllers.UpgradResultProcessingController;
import com.nmims.helpers.MettlHelper;
import com.nmims.helpers.UpgradHelper;
import com.nmims.services.UpgradAssessmentService;
import com.nmims.services.UpgradResultProcessingService;

@Repository("upgradResultProcessingDao")
public class UpgradResultProcessingDao {
	
private static final Logger logger = LoggerFactory.getLogger(UpgradResultProcessingDao.class);
	
	private PlatformTransactionManager transactionManager;
	
	private JdbcTemplate jdbcTemplate;
	
	@Value("${TEST_USER_SAPIDS}")
	private String TEST_USER_SAPIDS;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Transactional(readOnly = true)
	public List<TEEResultBean> readMBAXScoreFromTeeMarks(TEEResultBean resultBean){
		ArrayList<TEEResultBean> teeResultBeans = new ArrayList<TEEResultBean>();
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT "
				+ " m_m.*, s_s_c.batchId, p_s_s.subject ,es.schedule_name"
				+ " FROM "
				+ " exam.mbax_marks m_m, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s, exam.mbax_exams_schedule es"
				+ " WHERE"
				+ " s_s_c.id = m_m.timebound_id AND "
				+ " s_s_c.prgm_sem_subj_id = p_s_s.id AND" + 
				" es.timebound_id = m_m.timebound_id AND " + 
				" es.schedule_id = m_m.schedule_id";
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and m_m.schedule_id = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and s_s_c.batchId = ?";
			parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and m_m.timebound_id = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		if(!StringUtils.isBlank(resultBean.getSapid()) ) {
			sql= sql+ " and m_m.sapid = ?";
			parameters.add(resultBean.getSapid());
		}
		Object[] args = parameters.toArray();
		try {
			teeResultBeans = (ArrayList<TEEResultBean>) jdbcTemplate.query(sql, args,
					new BeanPropertyRowMapper(TEEResultBean.class));
		}catch(Exception e) {
			logger.error("readMBAXScoreFromTeeMarks() : " , e);
		}
		return teeResultBeans;
	}
	
	@Transactional(readOnly = false)
	public int saveMbaxStudentMarksBeforeRIANV(TEEResultBean m){
		String sql2 = "Update exam.mbax_marks set "
				+ " processed='N', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate(),"
				+ "	previous_score=? "
				+ " where timebound_id =? and sapid = ?";
		int i = jdbcTemplate.update(sql2, new Object[] { 
				m.getLastModifiedBy(),
				m.getScore(),
				m.getTimebound_id(),
				m.getSapid()
		});

		return i;
	}
	
	@Transactional(readOnly = true)
	public String getMbaxStudentPreviousScore(TEEResultBean m){
		String sql2 = "Select previous_score from  exam.mbax_marks  "
				+ " where prgm_sem_subj_id =? and sapid = ?";
		String i = (String)jdbcTemplate.queryForObject(sql2, new Object[] { 
				m.getPrgm_sem_subj_id(),
				m.getSapid()
		},new SingleColumnRowMapper(String.class));

		return i;
	}
	
	@Transactional(readOnly = false)
	public int updateMbaxSubjectScore(TEEResultBean m,String status){
		String sql2 = "Update exam.mbax_marks set "
				+ " processed='N', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate(),"
				+ "	score=?,"
				+ " status=? "
				+ " where prgm_sem_subj_id =? and sapid = ?";
		int i = jdbcTemplate.update(sql2, new Object[] { 
				m.getLastModifiedBy(),
				m.getScore(),
				status,
				m.getPrgm_sem_subj_id(),
				m.getSapid()
		});

		return i;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,String> getSubjectListForMasterKey(int masterKey){
		String sql = "select id,subject from exam.program_sem_subject where consumerProgramStructureId=?";
		 HashMap<String,String> subjectList = new HashMap<>();
		ArrayList<ProgramSubjectMappingExamBean> programDetails = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new Object[] { 
				masterKey
		},new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		
		if(programDetails.size() >0) {
			for(ProgramSubjectMappingExamBean bean:programDetails) {
				if(!subjectList.containsKey(bean.getSubject())) {
					subjectList.put(bean.getSubject(),""+bean.getId());
				}
			}
		}
		return subjectList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,String> getBatchListForMasterKey(int masterKey){
		String sql = "SELECT `name` AS `batchName`,`id` AS `batchId` FROM exam.batch where consumerProgramStructureId=?";
		 HashMap<String,String> batchList = new HashMap<>();
		ArrayList<EMBABatchSubjectBean> batchDetails = (ArrayList<EMBABatchSubjectBean>)jdbcTemplate.query(sql, new Object[] { 
				masterKey
		},new BeanPropertyRowMapper(EMBABatchSubjectBean.class));
		
		if(batchDetails.size() >0) {
			for(EMBABatchSubjectBean bean:batchDetails) {
				if(!batchList.containsKey(bean.getBatchName())) {
					batchList.put(bean.getBatchName(),bean.getBatchId());
				}
			}
		}
		return batchList;
	}
	
	@Transactional(readOnly = true)
	public List<BatchExamBean> getBatchesListForMasterKey(int masterKey){
		List<BatchExamBean> batchList = new ArrayList<BatchExamBean>();
		try {
			String sql = "SELECT * FROM exam.batch where (consumerProgramStructureId = ? OR consumerProgramStructureId = 126 OR consumerProgramStructureId = 162) ";
			batchList = jdbcTemplate.query(
				sql, 
				new Object[] {masterKey},
				new BeanPropertyRowMapper<BatchExamBean>(BatchExamBean.class)
			);
			return batchList;
		} catch (Exception e) {
			// TODO: handle exception
			
			return batchList;
		}
	}
	
	@Transactional(readOnly = true)
	public int getPSSIdFromTimeboundIdBatchId(String timeboundId,String batchId) {
		int id =0;
		String sql = "select prgm_sem_subj_id from lti.student_subject_config where id=? and batchId=?";
		try {
		id= (int)jdbcTemplate.queryForObject(sql, new Object[] { 
				timeboundId,batchId
		},Integer.class);
		}catch(Exception e) {
			
		}
		return id;
	}
	
	@Transactional(readOnly = true)
	public List<TEEResultBean> getABRecordsMBAX( TEEResultBean bean ) {
		String sql = ""
			+ "SELECT "
			    +" `tum`.`timebound_subject_config_id` AS `timebound_id`, "
			    +" `tm`.`schedule_id` AS `schedule_id`, "
			    +" `tum`.`userId` AS `sapid`, "
			    +" concat(`students`.`firstName`,' ', `students`.`lastName`) AS student_name, "
			    +" `ssc`.`batchId` AS `batchId`, "
			    +" `batch`.`name` AS `batch`, "
			 //   +" `students`.`program` AS `program`, "
			    +" `pss`.`sem` AS `sem`, "
			    +" `pss`.`subject` AS `subject`, "
			    +" 'AB' AS `status`, "
			    +" `pss`.`id` AS `prgm_sem_subj_id` "
			   
		    +" FROM `lti`.`timebound_user_mapping` `tum` "

		    /* get students with marks. */
		    +" LEFT JOIN `exam`.`mbax_marks` `tm` "
		    +" ON `tum`.`timebound_subject_config_id` = `tm`.`timebound_id` AND `tum`.`userId` = `tm`.`sapid` "

		    /* get Subject and semester details */
		    +" INNER JOIN  `lti`.`student_subject_config` `ssc` "
		    +" ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "

		    /* get Subject and semester details */
		    +" INNER JOIN `exam`.`program_sem_subject` `pss` "
		    +" ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

		    /* get Student details */
		    +" INNER JOIN `exam`.`students` `students` "
		    +" ON `students`.`sapid` = `tum`.`userId` "

		    /* get batch details */
		    +" INNER JOIN `exam`.`batch` `batch` "
		    +" ON `batch`.`id` = `ssc`.`batchId` "
		   
		    

		    +" WHERE "
		    /* Get only students */
		    +" `role` = 'Student' "
		   // +" AND `tm`.`prgm_sem_subj_id` = ? "
		    +" AND `tum`.`timebound_subject_config_id` = ? "
		    +" AND ( "
			    +" `tm`.`score` IS NULL "
			    + "OR `tm`.`status` = 'Not Attempted' "
		    +" ) AND " 
			    + " ( `tm`.`status` not in ('AB','RIA','NV','')  or `tm`.`status` IS NULL) "
		    /* Remove test users from the list */
    			+ " AND `tum`.`userId` NOT IN (  " + TEST_USER_SAPIDS + ")"
		    /* Remove terminated users from the list */
			+ " AND NOT(`students`.`programStatus` <=> 'Program Terminated') ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getTimebound_id() );
		//parameters.add( TEST_USER_SAPIDS );
		logger.info("getABRecordsMBAX() :  "+sql);
		return jdbcTemplate.query(
			sql, 
			parameters.toArray(), 
			new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<TEEResultBean> getABRecordsFor100MarksExamMBAX( TEEResultBean bean ) {
		String sql = ""
			+ "SELECT "
			    +" `b`.`timeboundId` AS `timebound_id`, "
			    +" `tm`.`schedule_id` AS `schedule_id`, "
			    +" `b`.`sapid` AS `sapid`, "
			    +" concat(`students`.`firstName`,' ', `students`.`lastName`) AS student_name, "
			    +" `ssc`.`batchId` AS `batchId`, "
			    +" `batch`.`name` AS `batch`, "
			 /*   +" `students`.`program` AS `program`, " */
			    +" `pss`.`sem` AS `sem`, "
			    +" `pss`.`subject` AS `subject`, "
			    +" 'AB' AS `status`, "
			    +" `pss`.`id` AS `prgm_sem_subj_id` "
			   
		    +" FROM `exam`.`mba_x_bookings` `b` "

		    /* get students with marks. */
		    +" LEFT JOIN `exam`.`mbax_marks` `tm` "
		    +" ON `b`.`timeboundId` = `tm`.`timebound_id` AND `b`.`sapid` = `tm`.`sapid` "

		    /* get Subject and semester details */
		    +" INNER JOIN  `lti`.`student_subject_config` `ssc` "
		    +" ON `ssc`.`id` = `b`.`timeboundId` "

		    /* get Subject and semester details */
		    +" INNER JOIN `exam`.`program_sem_subject` `pss` "
		    +" ON `ssc`.`prgm_sem_subj_id` = `pss`.`id` "

		    /* get Student details */
		    +" INNER JOIN `exam`.`students` `students` "
		    +" ON `students`.`sapid` = `b`.`sapid` "

		    /* get batch details */
		    +" INNER JOIN `exam`.`batch` `batch` "
		    +" ON `batch`.`id` = `ssc`.`batchId` "
		   
		    +" WHERE "
		    /* Get only students */
		    +" `b`.`bookingStatus` = 'Y' "
		   // +" AND `tm`.`prgm_sem_subj_id` = ? "
		    +" AND `b`.`timeboundId` = ? "
		    +" AND ( "
			    +" `tm`.`score` IS NULL "
			    + "OR `tm`.`status` = 'Not Attempted' "
		    +" ) AND " 
			    + " ( `tm`.`status` not in ('AB','RIA','NV','')  or `tm`.`status` IS NULL) "
		    /* Remove test users from the list */
    			+ " AND `b`.`sapid` NOT IN (  " + TEST_USER_SAPIDS + ")"
		    /* Remove terminated users from the list */
			+ " AND NOT(`students`.`programStatus` <=> 'Program Terminated') ";

		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add( bean.getTimebound_id() );
		//parameters.add( TEST_USER_SAPIDS );
		logger.info("getABRecordsMBAX() :  "+sql);
		return jdbcTemplate.query(
			sql, 
			parameters.toArray(), 
			new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
		);
	}
	
	@Transactional(readOnly = false)
	public int batchUpdate(final List<TEEResultBean> successBeanList,final String userId) {
		try{

			String sql = "INSERT INTO `exam`.`mbax_marks`" + 
					"(`timebound_id`," + 
					"`sapid`," + 
					"`score`," + 
					"`max_score`," + 
					"`status`," + 
					"`lastModifiedBy`," + 
					"`created_at`," + 
					"`updated_at`," + 
					"`processed`," + 
					"`prgm_sem_subj_id`,"
					+ "`schedule_id`,"
					+ "`student_name`)" + 
					"VALUES" + 
					"(?," + 
					"?," + 
					"?," + 
					"40," + 
					"?," + 
					"?," + 
					"sysdate()," + 
					"sysdate()," +
					"'N'," + 
					"?,"
					+ "?,"
					+ "?) ON DUPLICATE KEY UPDATE "
					+ " `score` = 0, "
					+ " `status` = ?,"
					+ " `lastModifiedBy` = ?, "
					+ " `processed` = 'N', "
					+ " `schedule_id` = ? " ;



			int[] successIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					TEEResultBean bean = successBeanList.get(i);
					ps.setString(1, bean.getTimebound_id());
					ps.setString(2, bean.getSapid());
					ps.setInt(3, 0);
					ps.setString(4, bean.getStatus());
					ps.setString(5, userId);
					ps.setInt(6, bean.getPrgm_sem_subj_id());
					ps.setString(7, bean.getSchedule_id());
					ps.setString(8, bean.getStudent_name());
					

					ps.setString(9, bean.getStatus());
					ps.setString(10, userId);
					ps.setString(11, bean.getSchedule_id());
				}
				public int getBatchSize() {
					return successBeanList.size();
				}
			});
			
			return successIds.length;
		}catch(Exception e) {
			logger.error("batchUpdate mbax_marks",e);
			return 0;
		}
	}
	
	
	@Transactional(readOnly = false)
	public int batchUpdateForHistoryTable(final List<TEEResultBean> successBeanList,final String userId) {
		try{

			String sql = "INSERT INTO `exam`.`mbax_marks_history`" + 
					"(`timebound_id`," + 
					"`sapid`," + 
					"`score`," + 
					"`max_score`," + 
					"`status`," + 
					"`lastModifiedBy`," + 
					"`created_at`," + 
					"`updated_at`," + 
					"`processed`," + 
					"`prgm_sem_subj_id`,"
					+ "`schedule_id`,"
					+ "`student_name`)" + 
					"VALUES" + 
					"(?," + 
					"?," + 
					"?," + 
					"40," + 
					"?," + 
					"?," + 
					"sysdate()," + 
					"sysdate()," +
					"'N'," + 
					"?,"
					+ "?,"
					+ "?) ON DUPLICATE KEY UPDATE "
					+ " `score` = 0, "
					+ " `status` = ?,"
					+ " `lastModifiedBy` = ?, "
					+ " `processed` = 'N', "
					+ " `schedule_id` = ? " ;



			int[] successIds = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					TEEResultBean bean = successBeanList.get(i);
					ps.setString(1, bean.getTimebound_id());
					ps.setString(2, bean.getSapid());
					ps.setInt(3, 0);
					ps.setString(4, bean.getStatus());
					ps.setString(5, userId);
					ps.setInt(6, bean.getPrgm_sem_subj_id());
					ps.setString(7, bean.getSchedule_id());
					ps.setString(8, bean.getStudent_name());
					
					ps.setString(9, bean.getStatus());
					ps.setString(10, userId);
					ps.setString(11, bean.getSchedule_id());
				}
				public int getBatchSize() {
					return successBeanList.size();
				}
			});
			
			return successIds.length;
		}catch(Exception e) {
			logger.error("batchUpdate mbax_marks_history",e);
			return 0;
		}
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean> getAllMBAXStudentNotProcessedList(){
		ArrayList<TEEResultBean> studentsList = new ArrayList<TEEResultBean>();
		try {
			String sql = "SELECT * FROM exam.mbax_marks where processed='N';";
			studentsList = (ArrayList<TEEResultBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(TEEResultBean.class));
			return studentsList;
		}
		catch (Exception e) {
			logger.error("getAllMBAXStudentNotProcessedList() ",e);
			return studentsList;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean>  getAllMBAXStudentsEligibleForPassFail( TEEResultBean resultBean){
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
	
		String sql= "select t.sapid , t.timebound_id,t.schedule_id , es.max_score, t.score,t.status,t.processed,t.prgm_sem_subj_id  " + 
				"from exam.mbax_marks t , exam.mbax_exams_schedule es " + 
				"where " + 
				"t.timebound_id = es.timebound_id and " + 
				"t.schedule_id = es.schedule_id and " + 
				"t.processed = 'N' and " + 
				"t.timebound_id =? and " + 
				"t.schedule_id=? and " + 
				"t.sapid not in ("+TEST_USER_SAPIDS+") and " + 
				//"t.score <> '' and "+
				"t.score is not null and "
				+ "t.status<>'Not Attempted'";
		try {
		eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(sql, new Object[] {resultBean.getTimebound_id(),resultBean.getSchedule_id()}, new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		}catch(Exception e) {
			logger.error("Error in getting eligible students for pass fail trigger : ",e);
		}
		return eligibleList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentsTestDetailsExamBean> getMBAXIAScoresForStudentSubject(String sapid,String timeboundId,String iaType){
		
		ArrayList<StudentsTestDetailsExamBean> iaScoreList = new ArrayList<StudentsTestDetailsExamBean>();
		
		String sql="SELECT  "
				+ "    tst.*, "
				+ "    t.startDate, "
				+ "    t.endDate, "
				+ "    COALESCE(tst.score, 0) AS scoreInInteger, "
				+ "    t.showResultsToStudents, "
				+ "    t.maxScore, "
				+ "    ttcm.iaType "
				+ "FROM "
				+ "    exam.upgrad_student_assesmentscore tst "
				+ "        INNER JOIN "
				+ "    exam.upgrad_test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId "
				+ "        INNER JOIN "
				+ "    acads.upgrad_sessionplan_module spm ON spm.id = ttcm.referenceId "
				+ "        INNER JOIN "
				+ "    acads.upgrad_sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId = spm.sessionPlanId "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON ssc.id = stm.timeboundId "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.upgrad_test t ON t.id = tst.testId "
				+ "WHERE "
				+ "    stm.timeboundId = ? "
				+ "        AND tst.sapid = ? "
				+ "        AND ttcm.iaType = ? "
				+ "        AND t.showResultsToStudents = 'Y' "
				+ "        AND spm.topic <> 'Generic Module For Session Plan'"; //added to avoid generic module IA Scores
		
		try {
			iaScoreList = (ArrayList<StudentsTestDetailsExamBean>)jdbcTemplate.query(sql, new Object[] {timeboundId,sapid,iaType}, 
					new BeanPropertyRowMapper<StudentsTestDetailsExamBean>(StudentsTestDetailsExamBean.class));
		}catch(Exception e) {
			logger.error("Error = IAScoreList ",e);
		}
		return iaScoreList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertMBAXPassFail(ArrayList<EmbaPassFailBean> finalListforPassFail) {
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < finalListforPassFail.size(); i++) {
			try{
				EmbaPassFailBean bean = finalListforPassFail.get(i);
				upsertMBAXPassFailStatus(bean);
			}catch(Exception e){
				logger.error("upsertMBAXPassFail() ",e);
				errorList.add(i+"");
			}
		}
		return errorList;

	}
	public void upsertMBAXPassFailStatus(EmbaPassFailBean bean) {
		boolean recordExists = checkIfRecordExistsInPassFail(bean);
		if(recordExists){
			updateMBAXPassFailStatus(bean);
		}else{
			insertMBAXPassFailStatus(bean);
		}
	}
	
	@Transactional(readOnly = true)
	private boolean checkIfRecordExistsInPassFail(EmbaPassFailBean bean) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.mbax_passfail where  sapid = ? and prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				bean.getPrgm_sem_subj_id(),
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateMBAXPassFailStatus(EmbaPassFailBean m){
		//TransactionDefinition def = new DefaultTransactionDefinition();
		//TransactionStatus status = transactionManager.getTransaction(def);
		try {
			String sql = "Update exam.mbax_passfail set "
					//+ " sem=?, "
					//+ " attempt=?, "
					+ " timeboundId = ? ,"
					+ " schedule_id= ? , "
					+ " iaScore = ?, "
					+ " teeScore = ?, "
					+ " graceMarks = ?, "
					+ " isPass = ?, "
					+ " failReason = ?, "
					+ " lastModifiedBy = ?, "
					+ " lastModifiedDate = sysdate(),"
					+ " status = ?,"
					+ " grade = ?, "
					+ " points = ?, "
					+ " isResultLive = 'N' "
					+ " where prgm_sem_subj_id = ? and sapid = ?";
			jdbcTemplate.update(sql, new Object[] { 
					//m.getSem(),
					//m.getAttempt(),
					m.getTimeboundId(),
					m.getSchedule_id(),
					m.getIaScore(),
					m.getTeeScore(),
					m.getGraceMarks(),
					m.getIsPass(),
					m.getFailReason(),
					m.getLastModifiedBy(),
					m.getStatus(),
					m.getGrade(),
					m.getPoints(),
					m.getPrgm_sem_subj_id(),
					m.getSapid()
			});

			String sql2 = "Update exam.mbax_marks set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql2, new Object[] { 
					m.getLastModifiedBy(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			String sql3 = "Update exam.mbax_marks_history set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where schedule_id=? and timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql3, new Object[] { 
					m.getLastModifiedBy(),
					m.getSchedule_id(),
					m.getTimeboundId(),
					m.getSapid()
			});

		//	transactionManager.commit(status);

		}
		catch (Exception e) {
			// TODO: handle exception
		//	transactionManager.rollback(status);
			logger.error("updateMBAXPassFailStatus() ",e);
			
		}
	}	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertMBAXPassFailStatus(EmbaPassFailBean m){
	//	TransactionDefinition def = new DefaultTransactionDefinition();
	//	TransactionStatus status = transactionManager.getTransaction(def);
		try {

			String sql = "Insert into exam.mbax_passfail ( "
					+ " timeboundId,"
					+ " sapid,"
					+ " prgm_sem_subj_id,"
					//+ " sem, "
					//+ " attempt, "
					+ " schedule_id, "
					+ " iaScore, "
					+ " teeScore, "
				//	+ " project, "
					+ " graceMarks, "
					+ " isPass, "
					+ " failReason, "
					+ " status,"
					+ " createdBy, "
					+ " lastModifiedBy, "
					+ " lastModifiedDate,"
					+ " createdDate,"
					+ " isResultLive)"
					+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),'N')";
			jdbcTemplate.update(sql, new Object[] { 
					m.getTimeboundId(),
					m.getSapid(),
					m.getPrgm_sem_subj_id(),
					m.getSchedule_id(),
					m.getIaScore(),
					m.getTeeScore(),
				//	m.getProject(),
					m.getGraceMarks(),
					m.getIsPass(),
					m.getFailReason(),
					m.getStatus(),
					m.getCreatedBy(),
					m.getLastModifiedBy()

			});

			String sql2 = "Update exam.mbax_marks set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql2, new Object[] { 
					m.getLastModifiedBy(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			
			String sql3 = "Update exam.mbax_marks_history set "

				+ " processed='Y', "
				+ " lastModifiedBy=?, "
				+ " updated_at=sysdate() "
				+ " where schedule_id=? and timebound_id =? and sapid = ?";
			jdbcTemplate.update(sql3, new Object[] { 
					m.getLastModifiedBy(),
					m.getSchedule_id(),
					m.getTimeboundId(),
					m.getSapid()
			});
			
			
	//		transactionManager.commit(status);

		}
		catch (Exception e) {
			// TODO: handle exception
	//		transactionManager.rollback(status);
			logger.error("insertMBAXPassFailStatus() ",e);
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getAllFailedMBAXStudentsForGrace(String assessmentId, String scheduleId){
		ArrayList<EmbaPassFailBean> eligibleList = new ArrayList<EmbaPassFailBean>();
		
		String sql ="select mbp.*,es.max_score" + 
				" from  exam.mbax_passfail mbp ,exam.mbax_exams_schedule es " + 
				" where " + 
				" mbp.timeboundId=? and" + 
				" mbp.isPass ='N' and  " + 
				" mbp.timeboundId=es.timebound_id and" + 
				" mbp.schedule_id=es.schedule_id and" + 
				" mbp.schedule_id = ?";
try {
		eligibleList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {assessmentId,scheduleId}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
}catch(Exception e) {
	logger.error("getAllFailedMBAXStudentsForGrace() ",e);
}
		return eligibleList;
	}
	
	@Transactional(readOnly = true)
	public List<MBAXMarksBean> getMBAXMarksStudentList(){
		ArrayList<MBAXMarksBean> mbaxMarksBean = new ArrayList<MBAXMarksBean>();
		String sql = "SELECT t_m.*, s_s_c.batchId, p_s_s.subject FROM exam.mbax_marks t_m, lti.student_subject_config s_s_c, exam.program_sem_subject p_s_s WHERE s_s_c.id = t_m.timebound_id AND s_s_c.prgm_sem_subj_id = p_s_s.id;";
		mbaxMarksBean = (ArrayList<MBAXMarksBean>) jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(MBAXMarksBean.class));
		return mbaxMarksBean;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<BatchExamBean> getMBAXBatchesList(){
		ArrayList<BatchExamBean> batchList = new ArrayList<BatchExamBean>();
		try {
			String sql = "SELECT * FROM exam.mbax_batch;";
			batchList = (ArrayList<BatchExamBean>) jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper(BatchExamBean.class));
			return batchList;
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("getMBAXBatchesList() ",e);
			return batchList;
		}
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsForMBAXLive(TEEResultBean resultBean){
		
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="update exam.mbax_passfail set isResultLive ='Y' where schedule_id = ? and timeboundId =?";
		parameters.add(resultBean.getSchedule_id());
		parameters.add(resultBean.getTimebound_id());
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsForMBAXScheduleLive(TEEResultBean resultBean){
		
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="update exam.mbax_exams_schedule set isResultLive ='Y' where schedule_id = ? and timebound_id =?";
		parameters.add(resultBean.getSchedule_id());
		parameters.add(resultBean.getTimebound_id());
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = true)
	public String getMaxMBAXScheduleForTimeBoundId(String timebound_id, boolean reExam){
		String scheduleId = "";
		try {
			
			String sql = "SELECT schedule_id FROM exam.mbax_exams_schedule where timebound_id = ? "
					+ " AND exam_start_date_time in ( SELECT  max(exam_start_date_time) FROM exam.mbax_exams_schedule where timebound_id = ? ) " ;
			if(reExam) {
				sql += " AND max_score = '100' ";
			} else {
				sql += " AND max_score <> '100' ";
			}
			scheduleId = (String) jdbcTemplate.queryForObject(sql, new Object[] {timebound_id, timebound_id},
					new SingleColumnRowMapper(String.class));
			logger.info("MAx scheduleId : " + scheduleId);
			return scheduleId;
		}
		catch (Exception e) {
			logger.error("getMaxMBAXScheduleForTimeBoundId() ",e);
			return scheduleId;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getMBAXPassFailResultsForReport(TEEResultBean resultBean){
		ArrayList<EmbaPassFailBean> passFailRecords = new ArrayList<EmbaPassFailBean>();
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		String sql =""
				+ " SELECT "
					+ " `mp`.`timeboundId`, "
					+ " `mp`.`sapid`, "
					+ " `mp`.`schedule_id`, "
					+ " `mp`.`attempt`, "
					+ " `mp`.`sem`, "
					+ " `mp`.`graceMarks`, "
					+ " `mp`.`isPass`, "
					+ " `mp`.`failReason`, "
					+ " `mp`.`isResultLive`, "
					+ " `mp`.`status`, "
					+ " `mp`.`grade`, "
					+ " `mp`.`points`, "
					+ " `mp`.`prgm_sem_subj_id`, "
					+ " COALESCE(`mp`.`iaScore`, 0) AS `iaScore`, "
					+ " COALESCE(`mp`.`teeScore`, 0) AS `teeScore`, "
					+ " (COALESCE(`mp`.`iaScore`, 0) +  COALESCE(`mp`.`teeScore`, 0)) AS `total`, "
					+ " `pss`.`subject`, "
					+ " `ssc`.`batchId` as `batch_id`, "
					+ " `b`.`name` as `batchName`, "
					+ " `c`.`lc` as `lc`, "
					+ " `pss`.`sem` AS `sem` "
				+ " FROM `exam`.`mbax_passfail` `mp` "
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` ON `ssc`.`id` = `mp`.`timeboundId`"
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `mp`.`prgm_sem_subj_id` "
				+ " INNER JOIN `exam`.`batch` b ON b.id = `ssc`.`batchId` "
				+ " INNER JOIN `exam`.`students` s ON s.sapid = mp.sapid "
				+ " Inner JOIN `exam`.`centers` c on s.centerCode = c.centerCode "
				+ " WHERE 1 ";
		
		if(!StringUtils.isBlank(resultBean.getSchedule_id()) ) {
			sql= sql+ " and `mp`.`schedule_id` = ?";
			parameters.add(resultBean.getSchedule_id());
		}
		if(!StringUtils.isBlank(resultBean.getBatchId()) ) {
			sql= sql+ " and `ssc`.`batchId` = ?";
			parameters.add(resultBean.getBatchId());
		}
		if(!StringUtils.isBlank(resultBean.getTimebound_id()) ) {
			sql= sql+ " and `mp`.`timeboundId` = ?";
			parameters.add(resultBean.getTimebound_id());
		}
		Object[] args = parameters.toArray();
		passFailRecords = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return passFailRecords;  
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public ArrayList<MBAXMarksBean> upsertMBAXMarks(final ArrayList<MBAXMarksBean> mbaxResultBeanList,HttpServletRequest request) {
		try {
			// Insert tracking numbers for current interaction
			String sql = " INSERT INTO exam.mbax_marks (`prgm_sem_subj_id`,`timebound_id`,`sapid`,`student_name`,`score`,`max_score`,`report_link`, `test_date`, `test_time`,`schedule_id`,`status`,`lastModifiedBy`, `processed`)"
					+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE max_score=?, score = ? , lastModifiedBy=? , `processed`=?, `status`= ?, `student_name` = ? , `report_link`=?, `test_date`=?, `test_time`=?,`schedule_id`=?  ;";
			String sql_history = "INSERT INTO exam.mbax_marks_history(`prgm_sem_subj_id`,`timebound_id`,`sapid`,`student_name`,`score`,`max_score`,`report_link`, `test_date`, `test_time`,`schedule_id`,`status`,`lastModifiedBy`, `processed`)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE max_score = ?, score = ? , lastModifiedBy=? , `processed`=? , `status`= ?, `student_name` = ? , `report_link`=?, `test_date`=?, `test_time`=?,`schedule_id`=?;";
			String updatesql = "UPDATE `exam`.`mbax_marks` SET `score`=?, `max_score`='100',`schedule_id`=? , `processed`=?, `status`= ? , `student_name` = ? , `report_link`=?, `test_date`=?, `test_time`=?  WHERE `timebound_id`= ? and`sapid`=?;";
			
			ArrayList<MBAXMarksBean> mbaxResultBeanErrorList = new ArrayList<MBAXMarksBean>();
			for(int i=0; i < mbaxResultBeanList.size();i++) {
				MBAXMarksBean bean = mbaxResultBeanList.get(i);
				try {
					if(bean.getMax_score() == 100) {
						//resit exam
						jdbcTemplate.update(updatesql,new Object[] {
								bean.getScore(),
								bean.getSchedule_id(),
								"N",
								bean.getStatus(),
								bean.getStudent_name(),
								bean.getReport_link(),
								bean.getTest_date(),
								bean.getTest_time(),
								bean.getTimebound_id(),
								bean.getSapid()
								
						});
					}else {
						//regular exam
						
						jdbcTemplate.update(sql,new Object[] {
								bean.getPrgm_sem_subj_id(),
								bean.getTimebound_id(),
								bean.getSapid(),
								bean.getStudent_name(),
								bean.getScore(),
								bean.getMax_score(),
								bean.getReport_link(),
								bean.getTest_date(),
								bean.getTest_time(),
								bean.getSchedule_id(),
								bean.getStatus(),
								bean.getLastModifiedBy(),
								"N",
								bean.getMax_score(),
								bean.getScore(),
								bean.getLastModifiedBy(),
								"N",
								bean.getStatus(),
								bean.getStudent_name(),
								bean.getReport_link(),
								bean.getTest_date(),
								bean.getTest_time(),
								bean.getSchedule_id()
						});
					}
					jdbcTemplate.update(sql_history,new Object[] {
							bean.getPrgm_sem_subj_id(),
							bean.getTimebound_id(),
							bean.getSapid(),
							bean.getStudent_name(),
							bean.getScore(),
							bean.getMax_score(),
							bean.getReport_link(),
							bean.getTest_date(),
							bean.getTest_time(),
							bean.getSchedule_id(),
							bean.getStatus(),
							bean.getLastModifiedBy(),
							"N",
							bean.getMax_score(),
							bean.getScore(),
							bean.getLastModifiedBy(),
							"N",
							bean.getStatus(),
							bean.getStudent_name(),
							bean.getReport_link(),
							bean.getTest_date(),
							bean.getTest_time(),
							bean.getSchedule_id()
					});
				}
				catch (Exception e) {
					// TODO: handle exception
					
					mbaxResultBeanErrorList.add(bean);
				}
			}
			if(mbaxResultBeanErrorList.size() == mbaxResultBeanList.size()) {
				return null;
			}
			int totalResult = mbaxResultBeanList.size() - mbaxResultBeanErrorList.size();
			request.setAttribute("totalResult", totalResult);
			return mbaxResultBeanErrorList;
		} catch (Exception e) {
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public String getScheduleKeyFromScheduleId(String id){
		String sql ="Select schedule_accessKey from exam.mbax_exams_schedule where schedule_id=?"; 
		String scheduleId="";
		try {
			scheduleId = (String) jdbcTemplate.queryForObject(sql, new Object[] {id},String.class);
		}catch(Exception e) {
			
		}
		return scheduleId;
	}
	
	@Transactional(readOnly = true)
	public String getScheduleKeyFromScheduleIdAndTimeBoundId(String id,String timeBoundId){
		String sql ="Select schedule_accessKey from exam.mbax_exams_schedule where schedule_id=? and timebound_id=?"; 
		String scheduleId="";
		try {
			scheduleId = (String) jdbcTemplate.queryForObject(sql, new Object[] {id,timeBoundId},String.class);
		}catch(Exception e) {
			
		}
		return scheduleId;
	}
	
	@Transactional(readOnly = true)
	public ExamsAssessmentsBean getScheduleFromScheduleIdAndTimeBoundId(String id,String timeBoundId){
		String sql ="Select * from exam.mbax_exams_schedule where schedule_id=? and timebound_id=?";
		try {
			ExamsAssessmentsBean schedule = jdbcTemplate.queryForObject(
				sql, 
				new Object[] {
					id, timeBoundId
				},
				new BeanPropertyRowMapper<ExamsAssessmentsBean>(ExamsAssessmentsBean.class)
			);
			return schedule;
		}catch(Exception e) {
			
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean>  getReExamStudentDetails(String batchId,String timeBoundId,String program){
		ArrayList<Object> parameters = new ArrayList<Object>();
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		String sql ="SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    lti.timebound_user_mapping tum ON tum.userId = s.sapid " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"WHERE " + 
				"    s.program = ? " + 
				"        AND ssc.batchId = ? " + 
				"        AND tum.timebound_subject_config_id = ? " ;
		parameters.add(program);
		parameters.add(batchId);
		parameters.add(timeBoundId);
		Object[] args = parameters.toArray();
		try {
			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		}catch(Exception e) {
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean>  getAllStudentDetailsForBatchAndTimebound(String batchId,String timeBoundId,String program){
		ArrayList<Object> parameters = new ArrayList<Object>();
		ArrayList<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
		String sql ="SELECT  " + 
				"    s.* " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    lti.timebound_user_mapping tum ON tum.userId = s.sapid " + 
				"        AND tum.timebound_subject_config_id = ? " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id " + 
				"        LEFT JOIN " + 
				"    exam.mbax_marks mk ON mk.sapid = tum.userId " + 
				"        AND mk.timebound_id = ssc.id " + 
				"WHERE " + 
				"    s.program = ? " + 
				"        AND ssc.batchId = ? " + 
				"        AND (mk.status = 'Not Attempted' OR mk.status IS NULL OR mk.processed = 'N') ";
		parameters.add(timeBoundId);
		parameters.add(program);
		parameters.add(batchId);
		Object[] args = parameters.toArray();
		try {
			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql,args, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		}catch(Exception e) {
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public int getProgramSemSubjectIdFromTimeboundId(String timeboundId){
		String sql ="SELECT prgm_sem_subj_id FROM lti.student_subject_config where id=?"; 
		int pssi=0;
		try {
			pssi = (int) jdbcTemplate.queryForObject(sql, new Object[] {timeboundId},Integer.class);
		}catch(Exception e) {
			
		}
		return pssi;
	}
	

	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertMarks(ArrayList<MettlResponseBean> mettlResponseBeanList) {
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < mettlResponseBeanList.size(); i++) {
			MettlResponseBean bean = mettlResponseBeanList.get(i);
			try{
				upsertMarksHistoryStatus(bean, jdbcTemplate);
				upsertMarksStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(bean.getEmail()+"");
			}
		}
		return errorList;

	}
	public void upsertMarksStatus(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		boolean recordExists = checkIfRecordExistsInMarks(bean, jdbcTemplate);
		if(recordExists){
			TEEResultBean mbean = getStudentsPreviousScore(bean, jdbcTemplate);
			if(mbean != null) {
				//bean.setPrevious_schedule_id(mbean.getSchedule_id());
				bean.setPrevious_score(""+mbean.getScore());
			}
			updateMarksStatus(bean, jdbcTemplate);
		}else{
			insertMarksStatus(bean, jdbcTemplate);
		}
	}
	
	@Transactional(readOnly = true)
	private boolean checkIfRecordExistsInMarks(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.mbax_marks where  sapid = ? and  prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				//bean.getTimebound_id(),
				bean.getPrgm_sem_subj_id()
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	private TEEResultBean getStudentsPreviousScore(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {

		TEEResultBean mbean = new TEEResultBean();
		String sql = "SELECT score,schedule_id FROM exam.mbax_marks where  sapid = ? and prgm_sem_subj_id = ?";
		try {
			mbean = (TEEResultBean) jdbcTemplate.queryForObject(sql, new Object[] { 
					bean.getSapid(),
					//bean.getTimebound_id()
					bean.getPrgm_sem_subj_id()
			},new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		}catch(Exception e) {
			
		}
		return mbean;

	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateMarksStatus(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = ""
				+ " Update exam.mbax_marks set "
				//+ " sem=?, attempt=?, "
				+ " schedule_id = ?, student_name = ?, score = ?, "
				+ " max_score = ?, report_link = ?, test_date = ?, "
				+ " test_time = ?, "
				//+ " previous_score=?, "
				//+ " previous_schedule_id=?, "
				+ " processed = 'N', lastModifiedBy = ?, updated_at = sysdate(),"
				+ " status = ? , timebound_id = ? "
				+ " where  prgm_sem_subj_id = ? and sapid = ?";
		jdbcTemplate.update(sql, new Object[] { 
				//m.getSem(), m.getAttempt(),
				m.getSchedule_id(), m.getStudent_name(), m.getTotalMarks(),
				m.getMax_marks(), m.getReport_link(), m.getTest_date(),
				m.getTest_time(), // m.getPrevious_score(), m.getPrevious_schedule_id(),
				m.getLastModifiedBy(), m.getStatus(), m.getTimebound_id(),
				m.getPrgm_sem_subj_id(), m.getSapid()
		});
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertMarksStatus(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = "Insert into exam.mbax_marks "
				+ " ( "
					+ " timebound_id, schedule_id, prgm_sem_subj_id,"
					//+ " sem, "
					//+ " attempt, "
					+ " student_name, sapid, score, "
					+ " max_score, processed, report_link, "
					+ " test_date, test_time, status, "
					+ " createdBy, lastModifiedBy, "
					+ " created_at, updated_at "
				+ " )"
				+ "	VALUES "
				+ " ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, 'N', ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " sysdate(), sysdate() "
				+ ")";
		jdbcTemplate.update(sql, new Object[] { 
				m.getTimebound_id(), m.getSchedule_id(), m.getPrgm_sem_subj_id(),
				//m.getSem(),
				//m.getAttempt(),
				m.getStudent_name(), m.getSapid(), m.getTotalMarks(),
				m.getMax_marks(), m.getReport_link(),
				m.getTest_date(), m.getTest_time(), m.getStatus(),
				m.getCreatedBy(), m.getLastModifiedBy()
		});
	}

	
	public void upsertMarksHistoryStatus(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		boolean recordExists = checkIfRecordExistsInMarksHistory(bean, jdbcTemplate);
		if(recordExists){
			updateMarksStatusHistory(bean, jdbcTemplate);
		}else{
			insertMarksStatusHistory(bean, jdbcTemplate);
		}
	}
	
	@Transactional(readOnly = true)
	private boolean checkIfRecordExistsInMarksHistory(MettlResponseBean bean, JdbcTemplate jdbcTemplate) {
		if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
			return false;
		}
		String sql = "SELECT count(*) FROM exam.mbax_marks_history where  sapid = ? and timebound_id = ? and schedule_id = ? and prgm_sem_subj_id = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
				bean.getSapid(),
				bean.getTimebound_id(),
				bean.getSchedule_id(),
				bean.getPrgm_sem_subj_id()
		},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateMarksStatusHistory(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = " "
				+ "Update exam.mbax_marks_history set "
				//+ " sem=?, attempt=?, "
				+ " student_name = ?, score = ?, max_score = ?, "
				+ " status = ?, report_link = ?, test_date = ?, "
				+ " test_time = ?, processed = ? , lastModifiedBy = ?, "
				+ " updated_at = sysdate() "
				+ " where timebound_id = ? and sapid = ? and schedule_id = ? and prgm_sem_subj_id = ?";
		jdbcTemplate.update(sql, new Object[] { 
				//m.getSem(), m.getAttempt(),
				m.getStudent_name(), m.getTotalMarks(), m.getMax_marks(),
				m.getStatus(), m.getReport_link(), m.getTest_date(),
				m.getTest_time(), "N", m.getLastModifiedBy(),
				m.getTimebound_id(), m.getSapid(), m.getSchedule_id(), m.getPrgm_sem_subj_id()
		});
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertMarksStatusHistory(MettlResponseBean m, JdbcTemplate jdbcTemplate){
		String sql = ""
			+ " Insert into exam.mbax_marks_history "
			+ " ( "
				+ " timebound_id, schedule_id, student_name, "
				//+ " sem, attempt, "
				+ " sapid, score, max_score, "
				+ " report_link, test_date, test_time,"
				+ " status, processed, prgm_sem_subj_id, "
				+ " createdBy, lastModifiedBy, "
				+ " created_at, updated_at "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, "
				+ " sysdate(), sysdate() "
			+ " ) ";
		jdbcTemplate.update(sql, new Object[] { 
				m.getTimebound_id(), m.getSchedule_id(), m.getStudent_name(),
				//m.getSem(), m.getAttempt(),
				m.getSapid(), m.getTotalMarks(), m.getMax_marks(),
				m.getReport_link(), m.getTest_date(), m.getTest_time(),
				m.getStatus(), "N", m.getPrgm_sem_subj_id(),
				m.getCreatedBy(), m.getLastModifiedBy()
		});
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsForProjectLive(TEEResultBean resultBean){
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		// TODO: add check for project subjects after project flag added to subject level
		String sql =""
				+ " UPDATE `exam`.`mbax_passfail` "
				+ " SET `isResultLive` ='Y' "
				+ " WHERE `timeboundId` = ? "
				+ " AND `timeboundId` IN ( "
					+ " SELECT `ssc`.`id` "
					+ " FROM `lti`.`student_subject_config` `ssc` "
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
					+ " WHERE `pss`.`subject` = 'Capstone Project' "
				+ " )";
		parameters.add(resultBean.getTimebound_id());
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<EmbaPassFailBean>  getAllFailedStudentsForGraceProject(String assessmentId){
		ArrayList<EmbaPassFailBean> eligibleList = new ArrayList<EmbaPassFailBean>();
		String sql =""
				+ " SELECT * "
				+ " FROM `exam`.`mbax_passfail` `mbp` "
				+ " WHERE `timeboundId` = ? "
				+ " AND `mbp`.`isPass` ='N' "
				/* TODO : change logic to test for project subject */
				+ " AND `timeboundId` IN ( "
					+ " SELECT `ssc`.`id` "
					+ " FROM `lti`.`student_subject_config` `ssc` "
					+ " INNER JOIN `exam`.`program_sem_subject` `pss` "
					+ " ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
					+ " WHERE `pss`.`subject` = 'Capstone Project' "
				+ " )";

		eligibleList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {assessmentId}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
		return eligibleList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean>  getAllStudentsEligibleForProjectForPassFail( TEEResultBean resultBean){
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
	
		String sql= ""
				+ " SELECT * "
				+ " FROM `exam`.`mbax_marks` "
				+ " WHERE `processed` = 'N'"
				+ " and `timebound_id` = ? "
				+ " and `sapid` NOT IN ("+TEST_USER_SAPIDS+") "
				+ " AND (`score` <> '' OR `score` = 0) "
				+ " AND `score` IS NOT NULL "
				+ " AND `status` <> 'Not Attempted' ";
		try {
			eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(
				sql, 
				new Object[] { resultBean.getTimebound_id() }, 
				new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
			);
		}catch(Exception e) {
			
		}
		return eligibleList;
	}

	@Transactional(readOnly = true)
	public List<TEEResultBean> getTeeMarksStudentListForCapstone(){
		String sql = ""
				+ " SELECT `tm`.*, `ssc`.`batchId`, `pss`.`subject` "
				+ " FROM `exam`.`mbax_marks` `tm` "
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` ON `tm`.`timebound_id` = `ssc`.`id` "
				+ " INNER JOIN `exam`.`program_sem_subject` `pss` ON `pss`.`id` = `ssc`.`prgm_sem_subj_id` "
				+ " WHERE `pss`.`subject` = 'Capstone Project' ";
		return jdbcTemplate.query(
			sql,
			new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class)
		);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> upsertTeeMarksForProjectSubject(ArrayList<MettlResponseBean> mettlResponseBeanList) {
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();
		for (i = 0; i < mettlResponseBeanList.size(); i++) {
			MettlResponseBean bean = mettlResponseBeanList.get(i);
			updatePssIdForUser(bean);
			try{
				// Dont insert into marks history for Project
				// upsertTeeMarksHistoryStatus(bean, jdbcTemplate);
				upsertMarksStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(bean.getSapid());
			}
		}
		return errorList;

	}
	
	@Transactional(readOnly = true)
	private void updatePssIdForUser(MettlResponseBean bean) {
		String sql = ""
				+ " SELECT `pss2`.`id` "
				
				+ " FROM `lti`.`timebound_user_mapping` `tum` "
				
				+ " INNER JOIN `lti`.`student_subject_config` `ssc` "
				+ " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
				
				+ " INNER JOIN `exam`.`registration` `r` "
				+ " ON `r`.`sapid` = `tum`.`userId` "
				+ " AND `r`.`year` = `ssc`.`acadYear` "
				+ " AND `r`.`month` = `ssc`.`acadMonth` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss1` "
				+ " ON `pss1`.`id` = `ssc`.`prgm_sem_subj_id` "
				
				+ " INNER JOIN `exam`.`program_sem_subject` `pss2` "
				+ " ON `pss2`.`subject` = `pss1`.`subject` "
				+ " AND `r`.`sem` = `pss2`.`sem` "
				+ " AND `pss2`.`consumerProgramStructureId` = `pss1`.`consumerProgramStructureId` "
				
				+ " WHERE `tum`.`timebound_subject_config_id` = ? AND `tum`.`userId` = ? ";
		try {
			int pssId = jdbcTemplate.queryForObject(
				sql, 
				new Object[] { bean.getTimebound_id(), bean.getSapid() },
				Integer.class
			);
			bean.setPrgm_sem_subj_id(pssId);
		} catch (Exception e) {
			
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<TEEResultBean> getEligibleStudentsForPassFailBOPSubject(Integer timeboundId) {
		String sql = " SELECT  " + 
				"    map.userId AS sapid, " + 
				"    pss.passScore, " + 
				"    pss.id AS prgm_sem_subj_id, " + 
				"    conf.id AS timebound_id " + 
				"FROM " + 
				"    lti.timebound_user_mapping map " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config conf ON conf.id = map.timebound_subject_config_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = conf.prgm_sem_subj_id " + 
				"WHERE " + 
				"    map.timebound_subject_config_id = ? ";
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
		eligibleList = (ArrayList<TEEResultBean>)jdbcTemplate.query(sql, new Object[] {timeboundId}, new BeanPropertyRowMapper<TEEResultBean>(TEEResultBean.class));
		return eligibleList;
	}
	
	@Transactional(readOnly = false)
	public int  passFailResultsLiveForBOPSubject(Integer timeboundId){
		int count = 0;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="update exam.mbax_passfail set isResultLive ='Y' where timeboundId = ? ";
		parameters.add(timeboundId);
		Object[] args = parameters.toArray();
		count = jdbcTemplate.update(sql,args);
		return count;
	}
	
	@Transactional(readOnly = true)
	public Integer getPssIdByTimeboundId(Long timeboundId) {
		String sql = " select prgm_sem_subj_id from lti.student_subject_config where id = ? ";
		Integer pssId = (Integer) jdbcTemplate.queryForObject(sql, new Object[] { timeboundId }, Integer.class);
		return pssId;
	}
}
