package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentPaymentBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.OperationsRevenueBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramCompleteReportBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.ReRegistrationReportBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentLearningMetricsBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TCSExamBookingDataBean;
import com.nmims.beans.TCSMarksBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.helpers.PaginationHelper;
import com.sforce.soap.partner.sobject.SObject;

@Repository("tcsApiDAO")
public class TCSApiDAO extends BaseDAO{
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	

	
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
	
	@Transactional(readOnly = true)
	public List<TcsOnlineExamBean> getExamCenterDropdown(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT centerId, examCenterName FROM exam.examcenter where year = ? AND month = ?";
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		tcsOnlineExamBeanList = (List<TcsOnlineExamBean>) jdbcTemplate.query(sql, new Object[]{
														tcsOnlineExamBean.getExamYear(),
														tcsOnlineExamBean.getExamMonth()
														}, 
				new BeanPropertyRowMapper<TcsOnlineExamBean>(TcsOnlineExamBean.class));
		return tcsOnlineExamBeanList;
	}
	
	@Transactional(readOnly = true)
	public List<TcsOnlineExamBean> getExamDateDropdown(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		String sql = "SELECT DISTINCT date AS examDate FROM exam.examcenter_slot_mapping WHERE year = ? AND month = ? AND examcenterId = ?";
		tcsOnlineExamBeanList = (List<TcsOnlineExamBean>) jdbcTemplate.query(sql, new Object[]{
																tcsOnlineExamBean.getExamYear(),
																tcsOnlineExamBean.getExamMonth(),
																tcsOnlineExamBean.getCenterId()
																}, 
				new BeanPropertyRowMapper<TcsOnlineExamBean>(TcsOnlineExamBean.class));
		return tcsOnlineExamBeanList;
	}
	
	@Transactional(readOnly = true)
	public List<TcsOnlineExamBean> getExamStartTimeDropdown(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		String sql = "SELECT DISTINCT starttime AS examTime  FROM exam.examcenter_slot_mapping WHERE year = ? AND month = ?  AND examcenterId = ? AND date = ? ";
		tcsOnlineExamBeanList = (List<TcsOnlineExamBean>) jdbcTemplate.query(sql, new Object[]{
																tcsOnlineExamBean.getExamYear(),
																tcsOnlineExamBean.getExamMonth(),
																tcsOnlineExamBean.getCenterId(),
																tcsOnlineExamBean.getExamDate()
																}, 
				new BeanPropertyRowMapper<TcsOnlineExamBean>(TcsOnlineExamBean.class));
		return tcsOnlineExamBeanList;
	}
	
	@Transactional(readOnly = true)
	public String isAnySubjectPresentSameSlotForSapid(TcsOnlineExamBean tcsOnlineExamBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String result;
		String sql = " SELECT  " + 
				"    IF(COUNT(*) > 0, 'YES', 'NO') AS result " + 
				"FROM " + 
				"    exam.exambookings " + 
				"WHERE " + 
				"    sapid = ?  AND year = ? " + 
				"        AND month = ? " + 
				"        AND examDate = ? " + 
				"        AND examTime = ? "+ 
				"		AND booked = 'Y' ";
		result = (String) jdbcTemplate.queryForObject(
	            sql, new Object[] { 
	            		tcsOnlineExamBean.getUserId(),
	            		tcsOnlineExamBean.getExamYear(),
	            		tcsOnlineExamBean.getExamMonth(),
	            		tcsOnlineExamBean.getExamDate(),
	            		tcsOnlineExamBean.getExamTime()	            		
	            }, String.class);	
		return result;
	}
	
	@Transactional(readOnly = true)
	public String isExamBookingSlotEmpty(TcsOnlineExamBean tcsOnlineExamBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		String sql = " SELECT  " + 
					"    IF(CONVERT(COALESCE(booked, 0 ) ,UNSIGNED INTEGER) < CONVERT(COALESCE(capacity, 0 ),UNSIGNED INTEGER), 'YES', 'NO') AS result " + 
					"FROM " + 
					"    exam.examcenter_slot_mapping " + 
					"WHERE " + 
					"    examcenterId = ? " + 
					"        AND date = ? " + 
					"        AND starttime = ? " + 
					"        AND year = ? " + 
					"        AND month = ? ";
			String result = (String) jdbcTemplate.queryForObject(
			            sql, new Object[] { 
			            		tcsOnlineExamBean.getCenterId(),
			            		tcsOnlineExamBean.getExamDate(),
			            		tcsOnlineExamBean.getExamTime(),
			            		tcsOnlineExamBean.getExamYear(),
			            		tcsOnlineExamBean.getExamMonth()
			            }, String.class);	
		
		return result;	
	}
	
	@Transactional(readOnly = true)
	public Integer getExamBookingExtraAddedCount(TcsOnlineExamBean tcsOnlineExamBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		
		String sql = " SELECT  " + 
				"    IF(CONVERT( COALESCE(booked, 0) , UNSIGNED INTEGER) > CONVERT( COALESCE(capacity, 0) , UNSIGNED INTEGER), " + 
				"        (CONVERT( COALESCE(booked, 0) , UNSIGNED INTEGER) - CONVERT( COALESCE(capacity, 0) , UNSIGNED INTEGER)), " + 
				"        0) AS result " + 
				"FROM " + 
				"    exam.examcenter_slot_mapping " + 
				"WHERE " + 
				"    examcenterId = ? " + 
				"        AND date = ? " + 
				"        AND starttime = ? " + 
				"        AND year = ? " + 
				"        AND month = ? ";
	
		Integer result = (Integer) jdbcTemplate.queryForObject(
				sql, new Object[] { 
						tcsOnlineExamBean.getCenterId(),
						tcsOnlineExamBean.getExamDate(),
						tcsOnlineExamBean.getExamTime(),
						tcsOnlineExamBean.getExamYear(),
						tcsOnlineExamBean.getExamMonth()
				}, Integer.class);	
		
		return result;	
	}
	
	@Transactional(readOnly = false)
	public void increaseCountExamBookedSlot(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " UPDATE `exam`.`examcenter_slot_mapping`  " + 
				"SET  " + 
				"    `booked` = (COALESCE(booked, 0 ) + 1) " + 
				"WHERE " + 
				"    `examcenterId` = ? " + 
				"        AND `date` = ? " + 
				"        AND `starttime` = ? ";
		 jdbcTemplate.update(sql, new Object[] {
								tcsOnlineExamBean.getCenterId(),
								tcsOnlineExamBean.getExamDate(),
								tcsOnlineExamBean.getExamTime()
					});
	}
	
	@Transactional(readOnly = false)
	public void decreaseCountExamBookedSlot(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		TcsOnlineExamBean tcsOnlineExamBeanList = new TcsOnlineExamBean();
		String sql1 = " SELECT  " + 
				"    centerId, " + 
				"    examDate, " + 
				"    examTime, " + 
				"    year AS examYear, " + 
				"    month AS examMonth " + 
				"FROM " + 
				"    exam.exambookings " + 
				"WHERE " + 
				" booked = 'Y' "+
				"   AND  sapid = ? " + 
				"        AND subject = ? " + 
				"        AND year = ? " + 
				"        AND month = ? ";
		tcsOnlineExamBeanList = (TcsOnlineExamBean) jdbcTemplate.queryForObject(sql1, new Object[] {
				tcsOnlineExamBean.getUserId(),
				tcsOnlineExamBean.getSubject(),
				tcsOnlineExamBean.getExamYear(),
				tcsOnlineExamBean.getExamMonth()
		}, new BeanPropertyRowMapper<TcsOnlineExamBean> (TcsOnlineExamBean.class) );
		
		
		String sql2 = " UPDATE `exam`.`examcenter_slot_mapping`  " + 
				"SET  " + 
				"    `booked` = (COALESCE(booked, 0 ) - 1) " + 
				"WHERE " + 
				"    `examcenterId` = ? " + 
				"        AND `date` = ? " + 
				"        AND `starttime` = ? ";
		 jdbcTemplate.update(sql2, new Object[] {
				 tcsOnlineExamBeanList.getCenterId(),
				 tcsOnlineExamBeanList.getExamDate(),
				 tcsOnlineExamBeanList.getExamTime()
					});
	}
	
	@Transactional(readOnly = true)
	public ExamBookingTransactionBean getPrevExamBookingBean(TcsOnlineExamBean tcsOnlineExamBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ExamBookingTransactionBean prevExamBookingBean = new ExamBookingTransactionBean();
		
		String sql1 = " SELECT  " + 
				"    * " + 
				"FROM " + 
				"    exam.exambookings " + 
				"WHERE	 " + 
				"   booked = 'Y' " + 
				"        AND subject = ? " + 
				"        AND sapid = ? " + 
				"        AND year = ? " + 
				"        AND month = ?  "+
				" GROUP BY sapid , subject " + 
				"    ORDER BY lastModifiedDate DESC";
		
		prevExamBookingBean = (ExamBookingTransactionBean) jdbcTemplate.queryForObject(sql1, new Object[] {
				tcsOnlineExamBean.getSubject(),
				tcsOnlineExamBean.getUserId(),
				tcsOnlineExamBean.getExamYear(),
				tcsOnlineExamBean.getExamMonth()
		}, new BeanPropertyRowMapper<ExamBookingTransactionBean> (ExamBookingTransactionBean.class) );
		
		return prevExamBookingBean;
	}	
	
	@Transactional(readOnly = true)
	public TcsOnlineExamBean getRescheduleSapidDetailsForEmail(TcsOnlineExamBean tcsOnlineExamBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		TcsOnlineExamBean tcsOnlineExamBeanList = new TcsOnlineExamBean();
		String sql = " SELECT  " + 
				"    eb.sapid AS userId, " + 
				"    s.firstName, " + 
				"    eb.subject, " + 
				"    eb.examDate, " + 
				"    eb.examTime, " + 
				"    CASE " + 
				"        WHEN " + 
				"            eb.emailId = '' OR eb.emailId IS NULL " + 
				"                OR LENGTH(TRIM(eb.emailId)) = 0 " + 
				"        THEN " + 
				"            s.emailId " + 
				"        ELSE eb.emailId " + 
				"    END AS registeredEmailId " + 
				"FROM " + 
				"    exam.exambookings eb " + 
				"        INNER JOIN " + 
				"    exam.students s ON s.sapid = eb.sapid " + 
				"WHERE " + 
				"    eb.booked = 'Y' " + 
				"        AND eb.subject = ? " + 
				"        AND eb.sapid = ? " + 
				"        AND eb.year = ? " + 
				"        AND eb.month = ? " + 
				"GROUP BY eb.sapid , eb.subject " + 
				"ORDER BY eb.lastModifiedDate DESC "; 
		tcsOnlineExamBeanList = (TcsOnlineExamBean) jdbcTemplate.queryForObject(sql, new Object[] {
				tcsOnlineExamBean.getSubject(),
				tcsOnlineExamBean.getUserId(),
				tcsOnlineExamBean.getExamYear(),
				tcsOnlineExamBean.getExamMonth()
		}, new BeanPropertyRowMapper<TcsOnlineExamBean> (TcsOnlineExamBean.class) );
		
		return tcsOnlineExamBeanList;
		
	}
	
	@Transactional(readOnly = false)
	public void updatePrevExamBookingSlotForSapid(TcsOnlineExamBean tcsOnlineExamBean,String trackId,String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " UPDATE `exam`.`exambookings` " + 
				"SET " + 
				"    trackId = ?, " + 
				"   lastModifiedBy = ?, "+
				"   booked = 'N',  " + 
				" lastModifiedDate  = sysdate() "+
			//	"    `syncExamCenterProvider` = 'pending' " + 
				"WHERE " + 
				" booked = 'Y' "+
				"  AND  `sapid` = ? " + 
				"        AND `subject` = ? " + 
				"        AND `year` = ? " + 
				"        AND `month` = ? ";
		 jdbcTemplate.update(sql, new Object[] {
				 	trackId,
				 	lastModifiedBy,
				 	
					tcsOnlineExamBean.getUserId(),
					tcsOnlineExamBean.getSubject(),
					tcsOnlineExamBean.getExamYear(),
					tcsOnlineExamBean.getExamMonth()
		});
	}
	
	@Transactional(readOnly = false)
	public void insertRescheduleExamBookingSlotForSapid(ExamBookingTransactionBean examBookingTransactionBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO exam.exambookings "
				+ "(`sapid`, " + 
				" `subject`, " + 
				" `year`, " + 
				" `month`, " + 
				" `program`, " + 
				" `sem`, " + 
				" `trackId`, " + 
				" `amount`," + 
				" `tranDateTime`, " + 
				" `tranStatus`, " + 
				" `booked`," + 
				" `paymentMode`, " + 
				" `ddno`," + 
				" `bank`," + 
				" `ddAmount`, " + 
				" `centerId`," + 
				" `examDate`," + 
				" `examTime`, " + 
				" `transactionID`," + 
				" `requestID`," + 
				" `merchantRefNo`," + 
				" `secureHash`," + 
				" `respAmount`, " + 
				" `description`," + 
				" `responseCode`," + 
				" `respPaymentMethod`," + 
				" `isFlagged`," + 
				" `paymentID`," + 
				" `responseMessage`, " + 
				" `error`, " + 
				" `respTranDateTime`, " + 
				" `examMode`, " + 
				" `examEndTime`, " + 
				" `ddReason`, " + 
				" `ddDate`, " + 
				" `bookingCompleteTime`, " + 
				" `password`, " + 
				" `paymentOption`,  " +
				" `bankName`, " + 
				" `lastModifiedBy`, " +
				" emailId, " +
//				" testTaken, " +
				" `lastModifiedDate`) " 

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, sysdate())";
		 
		 jdbcTemplate.update(sql, new Object[] {
				 examBookingTransactionBean.getSapid(),
				 examBookingTransactionBean.getSubject(),
				 examBookingTransactionBean.getYear(),
				 examBookingTransactionBean.getMonth(),
				 examBookingTransactionBean.getProgram(),
				 examBookingTransactionBean.getSem(), 
				 examBookingTransactionBean.getTrackId(),
				 examBookingTransactionBean.getAmount(), 
				 examBookingTransactionBean.getTranDateTime(),			
				 examBookingTransactionBean.getTranStatus(),
				 examBookingTransactionBean.getBooked(), 		
				 examBookingTransactionBean.getPaymentMode(),
				 examBookingTransactionBean.getDdno(),			
				 examBookingTransactionBean.getBank(),			
				 examBookingTransactionBean.getDdAmount(),
				 examBookingTransactionBean.getCenterId(),			
				 examBookingTransactionBean.getExamDate(),			
				 examBookingTransactionBean.getExamTime(),			
				 examBookingTransactionBean.getTransactionID(),			
				 examBookingTransactionBean.getRequestID(),			 
				 examBookingTransactionBean.getMerchantRefNo(),			
				 examBookingTransactionBean.getSecureHash(),		
				 examBookingTransactionBean.getRespAmount()	,		
				 examBookingTransactionBean.getDescription(),			
				 examBookingTransactionBean.getResponseCode(),			
				 examBookingTransactionBean.getRespPaymentMethod(),
				 examBookingTransactionBean.getIsFlagged(), 
				 examBookingTransactionBean.getPaymentID(),			
				 examBookingTransactionBean.getResponseMessage(),			
				 examBookingTransactionBean.getError(),			
				 examBookingTransactionBean.getRespTranDateTime(),			
				 examBookingTransactionBean.getExamMode(),
				 examBookingTransactionBean.getExamEndTime(),			
				 examBookingTransactionBean.getDdReason(),
				 examBookingTransactionBean.getDdDate(),
				 examBookingTransactionBean.getBookingCompleteTime(),
				 examBookingTransactionBean.getPassword(),  
				 examBookingTransactionBean.getPaymentOption(), 
				 examBookingTransactionBean.getBankName(),
				 examBookingTransactionBean.getLastModifiedBy(),
				 examBookingTransactionBean.getEmailId()    
		 });
	}
	
	@Transactional(readOnly = true)
	public MettlRegisterCandidateBean getExamBookingSameDateRescheduleListForRegistration(TcsOnlineExamBean tcsOnlineExamBean) {
		MettlRegisterCandidateBean mettlRegisterCandidateBean = new MettlRegisterCandidateBean();
		String sql = " SELECT  " + 
				"    `bk`.`firstName`, " + 
				"    `bk`.`lastName`, " + 
				"    `bk`.`studentid` AS sapid, " + 
				"    `bk`.`emailId` AS emailAddress, " + 
				"    `esm`.`schedule_accessKey` AS scheduleAccessKey, " + 
				"    esm.schedule_id, " + 
				"    pss.id AS prgm_sem_subj_id, " + 
				"    bk.imageUrl AS candidateImage, " + 
				"    bk.imageUrl AS registrationImage " + 
				"FROM " + 
				"    (SELECT  " + 
				"        `assessments_id`, " + 
				"            `schedule_accessKey`, " + 
				"            `schedule_name`, " + 
				"            schedule_id " + 
				"    FROM " + 
				"        `exam`.`exams_schedule_mettl` " + 
				"    WHERE " + 
				"        `exam_start_date_time` LIKE ?) `esm` " + 
				"        INNER JOIN " + 
				"    `exam`.`pg_assessment` `pga` ON `esm`.`assessments_id` = `pga`.`assessmentId` " + 
				"        INNER JOIN " + 
				"    `exam`.`program_sem_subject` `pss` ON `pss`.`sifySubjectCode` = `pga`.`sifyCode` " + 
				"        AND `pss`.`active` = 'Y' " + 
				"        INNER JOIN " + 
				"    (SELECT  " + 
				"        `eb`.`booked`, " + 
				"            `eb`.`year`, " + 
				"            `eb`.`month`, " + 
				"            `eb`.`examMode`, " + 
				"            `eb`.`subject`, " + 
				"            `eb`.`centerId`, " + 
				"            `eb`.`examDate`, " + 
				"            `eb`.`examTime`, " + 
				"            `eb`.`examEndTime`, " + 
				"            `s`.* " + 
				"    FROM " + 
				"        `exam`.`exambookings` `eb` " + 
				"    INNER JOIN (SELECT  " + 
				"        `s2`.`sapid` AS studentid, " + 
				"            `s2`.`firstName`, " + 
				"            `s2`.`lastName`, " + 
				"            `s2`.`imageUrl`, " + 
				"            `s2`.`mobile`, " + 
				"            `s2`.`emailId`, " + 
				"            `s2`.`consumerProgramStructureId` " + 
				"    FROM " + 
				"        `exam`.`students` `s2` " + 
				"    WHERE " + 
				"        `sem` = (SELECT  " + 
				"                MAX(sem) " + 
				"            FROM " + 
				"                `exam`.`students` `s3` " + 
				"            WHERE " + 
				"                s2.sapid = s3.sapid) " + 
				"    GROUP BY sapid) s ON s.studentid = eb.sapid " + 
				"    WHERE " + 
				"        eb.booked = 'Y' " + 
				"            AND eb.sapid = ? " + 
				"            AND eb.subject = ? " + 
				"            AND eb.year = ? " + 
				"            AND eb.month = ? " + 
				"            AND eb.examMode = 'Online' " + 
				"            AND `eb`.`subject` NOT IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " + 
				"            AND `eb`.`centerId` <> '-1' " + 
				"            AND `eb`.`examDate` = ? ) `bk` ON `bk`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId` " + 
				"        AND `bk`.`subject` = `pss`.`subject` " + 
				"        AND `esm`.`schedule_name` = (CONCAT(`pss`.`sifySubjectCode`, " + 
				"            DATE_FORMAT(`bk`.`examDate`, '%w%m%e%Y'), " + 
				"            DATE_FORMAT(`bk`.`examTime`, '%H%i%s'), " + 
				"            DATE_FORMAT(`bk`.`examEndTime`, '%H%i%s'))) ";
		
		mettlRegisterCandidateBean = jdbcTemplate.queryForObject(sql, new Object[] {
				tcsOnlineExamBean.getExamDate()+"%",
				tcsOnlineExamBean.getUserId(),
				tcsOnlineExamBean.getSubject(),
				tcsOnlineExamBean.getExamYear(),
				tcsOnlineExamBean.getExamMonth(),
				tcsOnlineExamBean.getExamDate()
		}, new BeanPropertyRowMapper<MettlRegisterCandidateBean> (MettlRegisterCandidateBean.class) );
		return mettlRegisterCandidateBean;
	}
	
	@Transactional(readOnly = false)
	public void resetSyncExamCenterProvider(StudentMarksBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " UPDATE `exam`.`exambookings` " + 
				"SET " + 
			
				"    `syncExamCenterProvider` = 'completed' " + 
				"WHERE " + 
				"       `syncExamCenterProvider` = 'pending'  " + 
				"        AND `year` = ? " + 
				"        AND `month` = ? ";
		 jdbcTemplate.update(sql, new Object[] {
				studentMarks.getYear(),
				studentMarks.getMonth()
		});
	}
	
	@Transactional(readOnly = true)
	public String getCenterNameForPreivew( String centerId){
		
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT examCenterName FROM exam.examcenter where centerId  = ?";
			String examCenterName = (String) jdbcTemplate.queryForObject(sql, new Object[] { 
					 centerId	}, String.class);
			return examCenterName;
		}catch(Exception e) {
			
			return null;
		}
	}	
	
	@Transactional(readOnly = true)
	public ResponseBean getTcsExamBookingDataToDisplay(
				  int pageNo, 
				  int pageSize,
				  StudentMarksBean studentMarks,
				  String authorizedCenterCodes
				  ){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ResponseBean responseBean = new ResponseBean();
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());
		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());
	
		String sql = " SELECT    " + 
				"					     @concatMonth:=(CASE   " + 
				"				        WHEN eb.month = 'Apr' THEN 4   " + 
				"				        WHEN eb.month = 'Jun' THEN 6   " + 
				"				        WHEN eb.month = 'Sep' THEN 9   " + 
				"				        WHEN eb.month = 'Dec' THEN 12  " + 
				"				    END) AS concatMonth," + 
				"				    CONCAT(s.sapid," + 
				"				            @concatMonth," + 
				"				            eb.year," + 
				"				            pss.sifySubjectCode) AS 'uniqueRequestId'," + 
				"				    eb.year as examYear," + 
				"				    eb.month as examMonth," + 
				"				    eb.sapid as userId," + 
				"				    eb.password,   " + 
				"				    pss.sifySubjectCode AS 'subjectId'," + 
				"				    pss.subject, " + 
				"				    eb.emailId AS registeredEmailId , " + 
				"                   eb.testTaken," + 
				"				    s.firstName, " + 
				"				    s.lastName, " + 
				"				    s.program, " + 
				"				    eb.examDate,  " + 
				"				    eb.examTime,  " + 
				"				    c.centerId,  " + 
				"				    c.examCenterName, " + 
				"				    cs.centerName, " + 
				"				    cs.lc  " + 
				"					FROM   " + 
				"					    exam.exambookings eb   " + 
				"					        LEFT JOIN   " + 
				"					    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid))  " + 
				"					   s ON eb.sapid = s.sapid   " + 
				"					        LEFT JOIN   " + 
				"					    exam.examcenter c ON c.centerId = eb.centerId   " + 
				"					        LEFT JOIN   " + 
				"					    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId   " + 
				"					        AND pss.sem = eb.sem   " + 
				"					        AND eb.subject = pss.subject   " + 
				"					         INNER JOIN   " + 
				"					    exam.centers cs  ON cs.centerCode = s.centerCode   " +		
				" WHERE   " + 
				"					    eb.booked = 'Y'   " + 
				"					      AND eb.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', " + 
				"            'Simulation: Mimic Social' )  "
				+ "   			  AND    eb.centerId <> -(1) " + 
				"					        AND s.sapid NOT IN (SELECT DISTINCT   " + 
				"					            c.sapid   " + 
				"					        FROM   " + 
				"					            exam.corporate_center_usermapping c   " + 
				"					        WHERE   " + 
				"					            year = ? AND month = ?)   " + 
				"							 AND  eb.year = ? AND eb.month = ? 	" ;
				if(studentMarks.getCenterId() != null &&  !("".equals( studentMarks.getCenterId() ))) {
				 	sql += " AND eb.centerId = ?";
				 	parameters.add(studentMarks.getCenterId());	
				}
				if(studentMarks.getExamDate() != null &&  !("".equals( studentMarks.getExamDate() ))) {
				 	sql += " AND eb.examDate = ?";
				 	parameters.add(studentMarks.getExamDate());	
				}
				if(studentMarks.getExamTime() != null &&  !("".equals( studentMarks.getExamTime() ))) {
					sql += " AND eb.examTime = ?";
					parameters.add(studentMarks.getExamTime());	
				}
				if(studentMarks.getSapid() != null &&  !("".equals( studentMarks.getSapid() ))) {
					sql += " AND eb.sapid in ("+studentMarks.getSapid()+")";	
				}
				if(studentMarks.getTestTaken() != null &&  !("".equals( studentMarks.getTestTaken() ))) {
					if("Not Attempted".equals(studentMarks.getTestTaken())) {
						sql += " AND eb.testTaken is null ";	
					}else {
						sql += " AND eb.testTaken = ? ";	
						parameters.add(studentMarks.getTestTaken());		
					}	
				}
				if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
					sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				}
				sql += "					GROUP BY eb.sapid , eb.subject   " + 
				"					ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC, eb.lastModifiedDate DESC  ";
		
		String countSql  = " SELECT count(*) FROM ("
				+ "   SELECT    " + 
				
				"				count(eb.sapid)  " + 
				"					FROM   " + 
				"					    exam.exambookings eb   " + 
				"					        LEFT JOIN   " + 
				"					    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid))  " + 
				"					   s ON eb.sapid = s.sapid   " + 
				"					        LEFT JOIN   " + 
				"					    exam.examcenter c ON c.centerId = eb.centerId   " + 
				"					        LEFT JOIN   " + 
				"					    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId   " + 
				"					        AND pss.sem = eb.sem   " + 
				"					        AND eb.subject = pss.subject   " + 
				"					         INNER JOIN   " + 
				"					    exam.centers cs  ON cs.centerCode = s.centerCode   " +
				
				" WHERE   " + 
				"					    eb.booked = 'Y'   " + 
				"					      AND eb.subject NOT IN ('Project', 'Module 4 - Project',  'Simulation: Mimic Pro', 'Simulation: Mimic Social')  " +
				"						AND  eb.centerId <> -(1) "+
				"					        AND s.sapid NOT IN (SELECT DISTINCT   " + 
				"					            c.sapid   " + 
				"					        FROM   " + 
				"					            exam.corporate_center_usermapping c   " + 
				"					        WHERE   " + 
				"					            year = ? AND month = ?)   " + 
				"							 AND  eb.year = ? AND eb.month = ? 	" ;
				if(studentMarks.getCenterId() != null &&  !("".equals( studentMarks.getCenterId() ))) {
					countSql += " AND eb.centerId = ?";
				
				}
				if(studentMarks.getExamDate() != null &&  !("".equals( studentMarks.getExamDate() ))) {
					countSql += " AND eb.examDate = ?";
				 	
				}
				if(studentMarks.getExamTime() != null &&  !("".equals( studentMarks.getExamTime() ))) {
					countSql += " AND eb.examTime = ?";
					
				}
				if(studentMarks.getSapid() != null &&  !("".equals( studentMarks.getSapid() ))) {
					countSql += " AND eb.sapid in ("+studentMarks.getSapid()+") ";
					
				}
				if(studentMarks.getTestTaken() != null &&  !("".equals( studentMarks.getTestTaken() ))) {
					if("Not Attempted".equals(studentMarks.getTestTaken())) {
						countSql += " AND eb.testTaken is null ";	
					}else {
						countSql += " AND eb.testTaken = ? ";	
					}
				}
				if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
					countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				}
				
				countSql +=	" GROUP BY eb.sapid , eb.subject  ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC ) examBookingCount ";
		
		
		Object[] args = parameters.toArray();
		
		PaginationHelper<TcsOnlineExamBean> pagingHelper = new PaginationHelper<TcsOnlineExamBean>();
		Page<TcsOnlineExamBean> page;
		
		page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, 
				new BeanPropertyRowMapper(TcsOnlineExamBean.class));
		responseBean.setPage(page);

		return responseBean;
	}
	
	@Transactional(readOnly = true)
	public List<TcsOnlineExamBean> getExamBookingListForDownloadExcel(StudentMarksBean studentMarks,String authorizedCenterCodes){
		List<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());
		parameters.add(studentMarks.getYear());
		parameters.add(studentMarks.getMonth());
	
		String sql = " SELECT    " + 
				"					     @concatMonth:=(CASE   " + 
				"				        WHEN eb.month = 'Apr' THEN 4   " + 
				"				        WHEN eb.month = 'Jun' THEN 6   " + 
				"				        WHEN eb.month = 'Sep' THEN 9   " + 
				"				        WHEN eb.month = 'Dec' THEN 12  " + 
				"				    END) AS concatMonth," + 
				"				    CONCAT(s.sapid," + 
				"				            @concatMonth," + 
				"				            eb.year," + 
				"				            pss.sifySubjectCode) AS 'uniqueRequestId'," + 
				"				    eb.year as examYear," + 
				"				    eb.month as examMonth," + 
				"				    eb.sapid as userId," + 
				"				    eb.password,   " + 
				"				    pss.sifySubjectCode AS 'subjectId'," + 
				"				    pss.subject, " + 
				"				    s.firstName, " + 
				"				    s.lastName, " + 
				"				    s.program, " + 
				"				    eb.examDate,  " + 
				"				    eb.examTime,  " + 
				"				    c.centerId,  " + 
				"				    c.examCenterName,  " + 
				"				    cs.centerName,  " + 
				"				     cs.lc,   " + 
				"				     eb.testTaken,   " + 
				"				     eb.emailId AS registeredEmailId  " + 
				"					FROM   " + 
				"					    exam.exambookings eb   " + 
				"					        LEFT JOIN   " + 
				"					    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid))  " + 
				"					   s ON eb.sapid = s.sapid   " + 
				"					        LEFT JOIN   " + 
				"					    exam.examcenter c ON c.centerId = eb.centerId   " + 
				"					        LEFT JOIN   " + 
				"					    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId   " + 
				"					        AND pss.sem = eb.sem   " + 
				"					        AND eb.subject = pss.subject   " + 
				"					        INNER JOIN   " + 
				"					    exam.centers cs  ON cs.centerCode = s.centerCode   " +
				 " WHERE   " + 
				"					    eb.booked = 'Y'   " + 
				"					      AND eb.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', " + 
				"            'Simulation: Mimic Social' )  "
				+ "   			  AND    eb.centerId <> -(1) " + 
				"					        AND s.sapid NOT IN (SELECT DISTINCT   " + 
				"					            c.sapid   " + 
				"					        FROM   " + 
				"					            exam.corporate_center_usermapping c   " + 
				"					        WHERE   " + 
				"					            year = ? AND month = ?)   " + 
				"							 AND  eb.year = ? AND eb.month = ? 	" ;
				if(studentMarks.getCenterId() != null &&  !("".equals( studentMarks.getCenterId() ))) {
				 	sql += " AND eb.centerId = ?";
				 	parameters.add(studentMarks.getCenterId());	
				}
				if(studentMarks.getExamDate() != null &&  !("".equals( studentMarks.getExamDate() ))) {
				 	sql += " AND eb.examDate = ?";
				 	parameters.add(studentMarks.getExamDate());	
				}
				if(studentMarks.getExamTime() != null &&  !("".equals( studentMarks.getExamTime() ))) {
					sql += " AND eb.examTime = ?";
					parameters.add(studentMarks.getExamTime());	
				}
				if(studentMarks.getSapid() != null &&  !("".equals( studentMarks.getSapid() ))) {
					sql += " AND eb.sapid in ("+studentMarks.getSapid()+") ";	
				}
				if(studentMarks.getTestTaken() != null &&  !("".equals( studentMarks.getTestTaken() ))) {
					
					if("Not Attempted".equals(studentMarks.getTestTaken())) {
						sql += " AND eb.testTaken is null ";	
					}else {
						sql += " AND eb.testTaken = ? ";	
						parameters.add(studentMarks.getTestTaken());		
					}
					
				}
				if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
					sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
				}
				
				sql += "					GROUP BY eb.sapid , eb.subject   " + 
				"					ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC, eb.lastModifiedDate DESC  ";

		Object[] args = parameters.toArray();
		tcsOnlineExamBeanList =   jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<TcsOnlineExamBean>(TcsOnlineExamBean.class));

		return tcsOnlineExamBeanList;
	} 
	
	@Transactional(readOnly = true)
	public MettlSSOInfoBean getStudentDetailsForCopyJoinLink(TcsOnlineExamBean input) {
		MettlSSOInfoBean mettlSSOInfoBean = new MettlSSOInfoBean();
		String sql = " SELECT  " + 
				"    es.sapid, " + 
				"    es.emailId, " + 
				"    es.imageUrl, " + 
				"    es.subject, " + 
				"    es.year, " + 
				"    es.month, " + 
				"    es.trackId, " + 
				"    esm.schedule_accessUrl AS joinURL, " + 
				"    esm.schedule_id AS scheduleId, " + 
				"    esm.exam_start_date_time AS examStartDateTime, " + 
				"    esm.exam_end_date_time AS examEndDateTime, " + 
				"    esm.reporting_start_date_time as reporting_start_date_time, " + 
				"    esm.reporting_finish_date_time as reporting_finish_date_time, " + 
				"    (esm.exam_end_date_time + INTERVAL 3 HOUR) AS accessEndDateTime, " + 
				"    es.firstName, " + 
				"    es.lastName " + 
				"FROM " + 
				"    exam.exams_schedule_mettl esm " + 
				"        INNER JOIN " + 
				"    exam.pg_assessment pa ON pa.assessmentId = esm.assessments_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.sifySubjectCode = pa.sifyCode " + 
				"        INNER JOIN " + 
				"    (SELECT  " + 
				"        eb.year, " + 
				"            eb.month, " + 
				"            eb.trackId, " + 
				"            st.firstName, " + 
				"            st.lastName, " + 
				"            eb.sapid, " + 
				"            st.imageUrl, " + 
				"            eb.sem, " + 
				"            eb.subject, " + 
				"            eb.emailId AS emailId, " + 
				"            CONCAT(DATE_FORMAT(eb.examDate, '%Y%m%d'), DATE_FORMAT(eb.examTime, '%H%i%s')) AS examDateTime, " + 
				"            eb.examDate, " + 
				"            st.consumerProgramStructureId, " + 
				"            eb.testTaken " + 
				"    FROM " + 
				"        exam.exambookings eb " + 
				"    INNER JOIN exam.students st ON st.sapid = eb.sapid AND eb.booked = 'Y' " + 
				"    WHERE " + 
				"        eb.sapid = ? " + 
				"            AND eb.subject = ? " + 
				"            AND eb.month = ? " + 
				"            AND eb.year = ? " + 
				"            AND eb.emailId <> '' " + 
				"            AND eb.emailId IS NOT NULL " + 
				"    GROUP BY eb.sapid , eb.subject " + 
				"    ORDER BY eb.lastModifiedDate DESC) es ON pss.sem = es.sem " + 
				"        AND pss.subject = es.subject " + 
				"        AND es.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"        AND es.examDateTime = DATE_FORMAT(esm.exam_start_date_time, '%Y%m%d%H%i%s') " + 
				"WHERE " + 
				"    esm.active = 'Y' ";
		mettlSSOInfoBean = jdbcTemplate.queryForObject(
				sql,  
				new Object[]{ input.getUserId(), input.getSubject(),  input.getExamMonth(), input.getExamYear() },
				new BeanPropertyRowMapper<MettlSSOInfoBean>(MettlSSOInfoBean.class)
			);
		return mettlSSOInfoBean;
	}
	
	@Transactional(readOnly = true)
	public List<DemoExamAttendanceBean> getDemoExamLogBySapid(String sapid) {
		List<DemoExamAttendanceBean> demoExamAttendanceList = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String  sql = "SELECT * FROM exam.demoexam_attendance where sapid = ?";
		demoExamAttendanceList = jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper<DemoExamAttendanceBean>(DemoExamAttendanceBean.class));
		return demoExamAttendanceList;
	}
	
	@Transactional(readOnly = true)
	public List<TCSExamBookingDataBean> syncUpdatedExamBookingData(StudentMarksBean studentMarks, int offset) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    eb.year, " + 
				"    eb.month, " + 
				"    eb.sapid, " + 
				"    eb.password, " + 
				"    pss.sifySubjectCode, " + 
				"    pss.subject, " + 
				"    s.firstName, " + 
				"    s.lastName , " + 
				"    eb.examDate, " + 
				"    eb.examTime, " + 
				"    c.centerId " + 
				"FROM " + 
				"    exam.exambookings eb " + 
				"        LEFT JOIN " + 
				"    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid)) " + 
				"   s ON eb.sapid = s.sapid " + 
				"        LEFT JOIN " + 
				"    exam.examcenter c ON c.centerId = eb.centerId " + 
				"        LEFT JOIN " + 
				"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        AND pss.sem = eb.sem " + 
				"        AND eb.subject = pss.subject " + 
				"WHERE " + 
				"	 eb.syncExamCenterProvider = 'pending'	 " + 
				"    AND    eb.booked = 'Y' " + 
				"     AND eb.subject NOT IN ('Project', 'Module 4 - Project') " + 
				"        AND s.sapid NOT IN (SELECT DISTINCT " + 
				"            c.sapid " + 
				"        FROM " + 
				"            exam.corporate_center_usermapping c " + 
				"        WHERE " + 
				"            year = ? AND month = ?) " + 
				"		 AND  eb.year = ? AND eb.month = ? "+	
				"GROUP BY eb.sapid , eb.subject " + 
				"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC" ;
		
		List<TCSExamBookingDataBean> updatedBookingList = (List<TCSExamBookingDataBean>) 
				jdbcTemplate.query(sql, new Object[]{
										studentMarks.getYear(), 
										studentMarks.getMonth(),
										studentMarks.getYear(), 
										studentMarks.getMonth()
		}, new BeanPropertyRowMapper<TCSExamBookingDataBean>(TCSExamBookingDataBean.class));
		

		if(updatedBookingList.size() == 0) {
			return null;
		}
		return updatedBookingList;
	}
	
	

	
	
			@Transactional(readOnly = true)
			public List<TCSExamBookingDataBean> getConfirmedBookingForGivenYearMonthForTCS(StudentMarksBean studentMarks, int offset) {
				jdbcTemplate = new JdbcTemplate(dataSource);
				List<TCSExamBookingDataBean> completeBookingList = new ArrayList<TCSExamBookingDataBean>();
				
				
				String oldSql = "SELECT  " + 
						"    eb.year, " + 
						"    eb.month, " + 
						"    eb.sapid, " + 
						"    eb.password, " + 
						"    pss.sifySubjectCode, " + 
						"    pss.subject, " + 
						"    s.firstName, " + 
						"    s.lastName , " + 
						"    eb.examDate, " + 
						"    eb.examTime, " + 
						"    c.centerId " + 
						"FROM " + 
						"    exam.exambookings eb " + 
						"        LEFT JOIN " + 
						"    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid)) " + 
						"   s ON eb.sapid = s.sapid " + 
						"        LEFT JOIN " + 
						"    exam.examcenter c ON c.centerId = eb.centerId " + 
						"        LEFT JOIN " + 
						"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
						"        AND pss.sem = eb.sem " + 
						"        AND eb.subject = pss.subject " + 
						"WHERE " + 
						"    eb.booked = 'Y' " + 
						"        AND eb.subject NOT IN ('Project', 'Module 4 - Project') " + 
						"        AND s.sapid NOT IN (SELECT DISTINCT " + 
						"            c.sapid " + 
						"        FROM " + 
						"            exam.corporate_center_usermapping c " + 
						"        WHERE " + 
						"            year = ? AND month = ?) " + 
						"		 AND  eb.year = ? AND eb.month = ? "+	
						"GROUP BY eb.sapid , eb.subject " + 
						"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC" ;
		
				
				String newSql = "SELECT  " + 
						"    eb.year, " + 
						"    eb.month, " + 
						"    eb.sapid, " + 
						"    eb.password, " + 
						"    pss.sifySubjectCode, " + 
						"    pss.subject, " + 
						"    s.firstName, " + 
						"    s.lastName, " + 
						"    eb.examDate, " + 
						"    eb.examTime, " + 
						"    c.centerId " + 
						"FROM " + 
						"    exam.exambookings_history eb " + 
						"        LEFT JOIN " + 
						"    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid)) " + 
						"   s ON eb.sapid = s.sapid " + 
						"        LEFT JOIN " + 
						"    exam.examcenter_history c ON c.centerId = eb.centerId " + 
						"        LEFT JOIN " + 
						"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
						"        AND pss.sem = eb.sem " + 
						"        AND eb.subject = pss.subject " + 
						"WHERE " + 
						"    eb.booked = 'Y' " + 
						"        AND eb.subject NOT IN ('Project', 'Module 4 - Project') " + 
						"        AND s.sapid NOT IN (SELECT DISTINCT " + 
						"            c.sapid " + 
						"        FROM " + 
						"            exam.corporate_center_usermapping c " + 
						"        WHERE " + 
						"            year = ? AND month = ?) " + 
						"		 AND  eb.year = ? AND eb.month = ? "+	
						"GROUP BY eb.sapid , eb.subject " + 
						"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC";
		
				
			
				String oldcorporateSql = "SELECT  " + 
						"    eb.year, " + 
						"    eb.month, " + 
						"    eb.sapid, " + 
						"    eb.password, " + 
						"    pss.sifySubjectCode, " + 
						"    pss.subject, " + 
						"    s.firstName, " + 
						"    s.lastName, " + 
						"    eb.examDate, " + 
						"    eb.examTime, " + 
						"    c.centerId " + 
						"FROM " + 
						"    exam.exambookings eb " + 
						"        LEFT JOIN " + 
						"    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid)) " + 
						"   s ON eb.sapid = s.sapid " + 
						"        LEFT JOIN " + 
						"    exam.corporate_examcenter c ON c.centerId = eb.centerId " + 
						"        LEFT JOIN " + 
						"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
						"        AND pss.sem = eb.sem " + 
						"        AND eb.subject = pss.subject " + 
						"WHERE " + 
						"    eb.booked = 'Y' " + 
						"        AND eb.subject NOT IN ('Project', 'Module 4 - Project') " + 
						"        AND s.sapid NOT IN (SELECT DISTINCT " + 
						"            c.sapid " + 
						"        FROM " + 
						"            exam.corporate_center_usermapping c " + 
						"        WHERE " + 
						"            year = ? AND month = ?) " + 
						"		 AND  eb.year = ? AND eb.month = ? "+	
						"GROUP BY eb.sapid , eb.subject " + 
						"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC" ;
				
				String newcorporateSql = "SELECT  " + 
						"    eb.year, " + 
						"    eb.month, " + 
						"    eb.sapid, " + 
						"    eb.password, " + 
						"    pss.sifySubjectCode, " + 
						"    pss.subject, " + 
						"    s.firstName, " + 
						"    s.lastName, " + 
						"    eb.examDate, " + 
						"    eb.examTime, " + 
						"    c.centerId " + 
						"FROM " + 
						"    exam.exambookings_history eb " + 
						"        LEFT JOIN " + 
						"    (select * from  exam.students es where es.sem = (select max(sem) from exam.students where sapid = es.sapid)) " + 
						"   s ON eb.sapid = s.sapid " + 
						"        LEFT JOIN " + 
						"    exam.corporate_examcenter c ON c.centerId = eb.centerId " + 
						"        LEFT JOIN " + 
						"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = s.consumerProgramStructureId " + 
						"        AND pss.sem = eb.sem " + 
						"        AND eb.subject = pss.subject " + 
						"WHERE " + 
						"    eb.booked = 'Y' " + 
						"        AND eb.subject NOT IN ('Project', 'Module 4 - Project') " + 
						"        AND s.sapid NOT IN (SELECT DISTINCT " + 
						"            c.sapid " + 
						"        FROM " + 
						"            exam.corporate_center_usermapping c " + 
						"        WHERE " + 
						"            year = ? AND month = ?) " + 
						"		 AND  eb.year = ? AND eb.month = ? "+	
						"GROUP BY eb.sapid , eb.subject " + 
						"ORDER BY eb.sapid , eb.examDate , eb.examTime , eb.sem , eb.program , eb.subject , eb.centerId ASC" ;
		
		//		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
		//			oldSql = oldSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		//			newSql = newSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		//			oldcorporateSql = oldcorporateSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		//			newcorporateSql = newcorporateSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		//		}
				
				oldSql += " LIMIT 500 ";
				newSql += " LIMIT 500 ";
				
		//		corporate to be handled later
		//		oldcorporateSql += "  LIMIT 100;";
		//		newcorporateSql += "  LIMIT 100";
						
		//		List<TCSExamBookingDataBean> oldcorporateBookingList = (List<TCSExamBookingDataBean>) 
		//				jdbcTemplate.query(oldcorporateSql, new Object[]{studentMarks.getYear(), studentMarks.getMonth(),studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper<TCSExamBookingDataBean>(TCSExamBookingDataBean.class));
		//		
		//		List<TCSExamBookingDataBean> newcorporateBookingList = (List<TCSExamBookingDataBean>) 
		//				jdbcTemplate.query(newcorporateSql, new Object[]{studentMarks.getYear(), studentMarks.getMonth(),studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper<TCSExamBookingDataBean>(TCSExamBookingDataBean.class));
				
				List<TCSExamBookingDataBean> oldbookingList = (List<TCSExamBookingDataBean>) 
						jdbcTemplate.query(oldSql, new Object[]{studentMarks.getYear(), studentMarks.getMonth(),studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper<TCSExamBookingDataBean>(TCSExamBookingDataBean.class));
				
				List<TCSExamBookingDataBean> newbookingList = (List<TCSExamBookingDataBean>) 
						jdbcTemplate.query(newSql, new Object[]{studentMarks.getYear(), studentMarks.getMonth(),studentMarks.getYear(), studentMarks.getMonth()}, new BeanPropertyRowMapper<TCSExamBookingDataBean>(TCSExamBookingDataBean.class));
				if(oldbookingList!=null && oldbookingList.size()>0){
				completeBookingList.addAll(oldbookingList);
				}
				if(newbookingList!=null && newbookingList.size()>0){
					completeBookingList.addAll(newbookingList);
					}
		//		if(oldcorporateBookingList!=null && oldcorporateBookingList.size()>0){
		//			completeBookingList.addAll(oldcorporateBookingList);
		//		}
		//		if(newcorporateBookingList!=null && newcorporateBookingList.size()>0){
		//			completeBookingList.addAll(newcorporateBookingList);
		//		}
				return completeBookingList;
			}
		
		
//		public String getSifySubCode(String sapid, String subject, String sem) {
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql = "SELECT " + 
//					"    sifySubjectCode  " + 
//					" FROM " + 
//					"    exam.program_sem_subject " + 
//					" WHERE " + 
//					"    consumerProgramStructureId in (SELECT  " + 
//						"    consumerProgramStructureId " + 
//						" FROM " + 
//						"    exam.students  " + 
//						" WHERE " + 
//						"    sapid = "+sapid + ")" +
//					"AND subject = '"+ subject+"' and sem="+sem;
//			String sifySubjectCode = "";
//			try {
//				sifySubjectCode = jdbcTemplate.queryForObject(sql,  new SingleColumnRowMapper<>(String.class));
//			}
//			catch(Exception e) {
//				sifySubjectCode = "";
//			}
//			
//			return sifySubjectCode;
//			
//				
//		}
		
			@Transactional(readOnly = true)	
		public String getSifySubCode(String subject, String program, String prgmStructApplicable, String sem) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT " + 
					"    sifySubjectCode " + 
					" FROM " + 
					"    exam.program_subject " + 
					" WHERE " + 
					" 	subject = '"+ subject+"' AND prgmStructApplicable = '" + prgmStructApplicable + "'" +
							"        AND program='" + program + "'" +
							"        AND sem = '" +sem+ "'" ;
			String sifySubjectCode = "";
			try {
				sifySubjectCode = jdbcTemplate.queryForObject(sql,  new SingleColumnRowMapper<>(String.class));
			}
			catch(Exception e) {
				
				sifySubjectCode = "";
			}
			
			return sifySubjectCode;
			
				
		}
		
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ArrayList<String> addTcsData(final ArrayList<TCSMarksBean> beanList) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			int i = 0;
			ArrayList<String> errorList = new ArrayList<>();

			for (i = 0; i < beanList.size(); i++) {
				try{
		   			TCSMarksBean bean = beanList.get(i);
		   			boolean success= saveTcsData(bean);
		   			if(!success) {
		   				errorList.add(i+"");
		   			}
		   		}catch(Exception e){
		   			
		   			errorList.add(i+"");
		   		}
			}
			return errorList;

		}
		
		public boolean saveTcsData(TCSMarksBean studentMarks) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			boolean isSuccess = false;
			String sql = "INSERT INTO exam.tcs_marks_table " + 
						"		 (sapId, " + 
						"		  uniqueRequestId, " + 
						"		  name, " + 
						"		  examDate, " + 
						"		  examTime, " + 
						"		  year , " + 
						"		  month ," + 
						"		  subjectCode, " + 
						"		  subjectId, " + 
						"		  subject, " + 
						"		  sectionOneMarks, " + 
						"		  sectionTwoMarks, " + 
						"		  sectionThreeMarks, " + 
						"		  sectionFourMarks, " + 
						"		  sectionFiveMarks, " + 
						"		  totalScore," + 
						"		  centerCode," + 
						"		  password," + 
						"		  attendanceStatus," + 
						"		  studentType," + 
						"		  createdBy," + 
						"		  createdDate" + 
						"		  )  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate()) on " + 
						"		  duplicate key update " + 
						"		  uniqueRequestId = ?, " + 
						"		  name= ?, " + 
						"		  examDate= ?, " + 
						"		  examTime= ?, " + 
						"		  year = ?, " + 
						"		  month = ?," + 
						"		  subjectCode= ?, " + 
						"		  subjectId= ?, " + 
						"		  subject= ?, " + 
						"		  sectionOneMarks= ?, " + 
						"		  sectionTwoMarks= ?, " + 
						"		  sectionThreeMarks= ?, " + 
						"		  sectionFourMarks= ?, " + 
						"		  sectionFiveMarks= ?, " + 
						"		  totalScore= ?," + 
						"		  centerCode= ?," + 
						"		  password= ?," + 
						"		  attendanceStatus= ?," + 
						"		  studentType= ?," + 
						"		  lastModifiedBy =?, " + 
						"		  lastModifiedDate =sysdate() "; 
			 Object[] queryParams = {studentMarks.getSapid(),studentMarks.getUniqueRequestId(),studentMarks.getName(),
					 studentMarks.getExamDate(), studentMarks.getExamTime(), studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getSubjectCode(),
					 studentMarks.getSubjectId(), studentMarks.getSubject(), studentMarks.getSectionOneMarks(), studentMarks.getSectionTwoMarks(),
					 studentMarks.getSectionThreeMarks(), studentMarks.getSectionFourMarks(), studentMarks.getSectionFiveMarks(), studentMarks.getTotalScore(),
					 studentMarks.getCenterCode(), studentMarks.getPassword(), studentMarks.getAttendanceStatus(),studentMarks.getStudentType(),studentMarks.getCreatedBy(),
					 
					 studentMarks.getUniqueRequestId(),studentMarks.getName(),
					 studentMarks.getExamDate(), studentMarks.getExamTime(), studentMarks.getYear(), studentMarks.getMonth(), studentMarks.getSubjectCode(),
					 studentMarks.getSubjectId(), studentMarks.getSubject(), studentMarks.getSectionOneMarks(), studentMarks.getSectionTwoMarks(),
					 studentMarks.getSectionThreeMarks(), studentMarks.getSectionFourMarks(), studentMarks.getSectionFiveMarks(), studentMarks.getTotalScore(),
					 studentMarks.getCenterCode(), studentMarks.getPassword(), studentMarks.getAttendanceStatus(),studentMarks.getStudentType(),studentMarks.getCreatedBy()
					 
					 
			 	};
			try {
				jdbcTemplate.update(sql, queryParams);
				isSuccess = true;
			}catch(Exception e) {
				
			}
			return isSuccess;
			
		}
		
		@Transactional(readOnly = true)
		public List<TCSMarksBean> getTotalTCSDataSummary(String year,String month) throws ParseException{
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TCSMarksBean> tcsMarksListSummary=null;
		/*
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); Date fDate =
		 * sdf.parse(fromDate); Date tDate = sdf.parse(toDate); SimpleDateFormat sdf1 =
		 * new SimpleDateFormat("dd/MM/yyyy"); String formattedFDate =
		 * sdf1.format(fDate); String formattedTDate = sdf1.format(tDate);
		 */
			String sql="select distinct count(*) as subjectCount,year,month,subject,subjectId,studentType " + 
					"	from exam.tcs_marks_table s " + 
					"	where s.year=?  AND s.month=?  ";
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			parameters.add(year);
			parameters.add(month);
			
			sql+="group by subjectId";		
			try {
				Object[] args = parameters.toArray();
				tcsMarksListSummary = (List<TCSMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(TCSMarksBean.class));
			} catch (DataAccessException e) {
				
			}
			return tcsMarksListSummary;
		}
		
		@Transactional(readOnly = true)
		public ArrayList<TCSMarksBean> getDataFromTcsTable(TCSMarksBean studentMarks) throws ParseException{
			jdbcTemplate = new JdbcTemplate(dataSource);
			ArrayList<TCSMarksBean> tcsMarksList = null;
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			Date fDate = sdf.parse(fromDate);
//			Date tDate = sdf.parse(toDate);
//			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
//			String formattedFDate = sdf1.format(fDate);
//			String formattedTDate = sdf1.format(tDate);
			String sql="select distinct * " + 
					"	from exam.tcs_marks_table s " + 
					"	where s.year = ? AND s.month = ?  ";
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			parameters.add(studentMarks.getYear());
			parameters.add(studentMarks.getMonth());
			
			//sql+="group by subjectId"; incorrect group by condition		 
			try {
				Object[] args = parameters.toArray();
				tcsMarksList = (ArrayList<TCSMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(TCSMarksBean.class));
			} catch (DataAccessException e) {
				
			}
			return tcsMarksList;
		}

		@Transactional(readOnly = true)
		public int getTotalCountofBookingsForGivenYearMonth(String year,String month) throws ParseException{
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count=0;
			String sql="select count(*) " + 
					"	from exam.exambookings eb " + 
					"	where eb.year = ? AND eb.month = ?  AND eb.subject NOT IN ('Project', 'Module 4 - Project') AND eb.booked = 'Y'";
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			parameters.add(year);
			parameters.add(month);
				
			try {
				Object[] args = parameters.toArray();
				count = (int) jdbcTemplate.queryForObject(sql, args, Integer.class);
			} catch (DataAccessException e) {
				
				return -1;
			}
			return count;
		}
		
		@Transactional(readOnly = true)
		public List<TCSMarksBean> getTCSData(String year,String month,String subject,int subjectCode,String studentType){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TCSMarksBean> tcsMarksList=null;
			String sql="select distinct * from exam.tcs_marks_table s where s.year=? and s.month=? ";
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			parameters.add(year);
			parameters.add(month);
			
			if(subject != null && !("".equals(subject))){
						sql+="and s.subject=? " ;
						parameters.add(subject);
					}
			if(!("".equals(subjectCode)) && subjectCode > 0){
						sql+="and s.subjectId=? " ;
						parameters.add(subjectCode);
					}
			if(studentType != null && !("".equals(studentType))){
				sql+="and s.studentType=? " ;
				parameters.add(studentType);
			}		
			try {
				Object[] args = parameters.toArray();
				tcsMarksList = (List<TCSMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(TCSMarksBean.class));
			} catch (DataAccessException e) {
				
			}
			return tcsMarksList;
		}
		
		@Transactional(readOnly = true)
		public List<TCSMarksBean> getTCSDataDetails(String year,String month,String subject,int subjectCode,String studentType){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<TCSMarksBean> tcsMarksListSummary=null;
			String sql="select distinct count(*) as subjectCount,subject,subjectId,year,month,studentType "
					+ " from exam.tcs_marks_table s "
					+ " where s.year=? "
					+ " and s.month=? ";
			ArrayList<Object> parameters = new ArrayList<Object>();
			
			parameters.add(year);
			parameters.add(month);
			
			if(subject != null && !("".equals(subject))){
						sql+=" and s.subject=? " ;
						parameters.add(subject);
					}
			if(!("".equals(subjectCode)) && subjectCode > 0){
				sql+=" and s.subjectId=? " ;
				parameters.add(subjectCode);
			}
			if(studentType != null && !("".equals(studentType))){
				sql+="and s.studentType=? " ;
				parameters.add(studentType);
			}	
			sql+=" group by subjectId";		
			try {
				Object[] args = parameters.toArray();
				tcsMarksListSummary = (List<TCSMarksBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(TCSMarksBean.class));
			} catch (DataAccessException e) {
				
			}
			return tcsMarksListSummary;
		}
		
		@Transactional(readOnly = true)
		public  List<TCSMarksBean> getStudentTypeMap(){
			 List<TCSMarksBean> studentTypeList=null;
			 jdbcTemplate = new JdbcTemplate(dataSource);
			 String sql="SELECT distinct sapid,consumerType FROM exam.students where PrgmStructApplicable not in  ('Jul2009','Jul2008') ";
			 studentTypeList= (List<TCSMarksBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(TCSMarksBean.class));
			 return studentTypeList;
		}
		
		@Transactional(readOnly = true)
		public ExamOrderExamBean getCurrentExamdetails() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT " + 
					"    eo.* " + 
					"FROM " + 
					"    exam.examorder eo " + 
					"WHERE " + 
					" eo.order = " + 
					"    (SELECT  MAX(e.order)  FROM exam.examorder e WHERE  e.timeTableLive = 'Y') ";
			return  (ExamOrderExamBean) jdbcTemplate.queryForObject(sql,  new BeanPropertyRowMapper(ExamOrderExamBean.class));
		}
		
		@Transactional(readOnly = false)
		public void deletePGScheduleinfoMettl(TcsOnlineExamBean tcsOnlineExamBean) {
			String sql = "DELETE FROM exam.exams_pg_scheduleinfo_mettl  " + 
					"WHERE " + 
					"    sapid = ? " + 
					"    AND subject = ? " + 
					"    AND year = ? " + 
					"    AND month = ? "; 
			jdbcTemplate = new JdbcTemplate(dataSource);
			 jdbcTemplate.update(sql, new Object[] {
						tcsOnlineExamBean.getUserId(),
						tcsOnlineExamBean.getSubject(),
						tcsOnlineExamBean.getExamYear(),
						tcsOnlineExamBean.getExamMonth()
			});
		}
		
}