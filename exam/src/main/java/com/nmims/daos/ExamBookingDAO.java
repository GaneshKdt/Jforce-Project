package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.log.SysoCounter;
import com.nmims.beans.ExamAdhocPaymentBean;
import com.nmims.beans.ExamAnnouncementBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.Demoexam_keysBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingMBAWX;
import com.nmims.beans.ExamBookingRefundRequestReportBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamConflictTransactionBean;
import com.nmims.beans.ExamFeeExemptSubjectBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FailTransactionBean;
import com.nmims.beans.IdCardExamBean;
import com.nmims.beans.MailBean;
import com.nmims.beans.PGReexamEligibleStudentsBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.RefundRequestBean;
import com.nmims.beans.RequestFormBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.helpers.PaginationHelper;

@Repository("examBookingDAO")
public class ExamBookingDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated";
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private final String REFUND = "Refund";
	private final String EXPIRED = "Expired"; 
	private final String TRANSACTION_FAILED = "Transaction Failed";
	final String DD_APPROVAL_PENDING = "DD Approval Pending";
	private final String DD_APPROVED = "DD Approved";
	private final String DD_REJECTED = "DD Rejected";
	private final String CENTER_CHANGED_BOOKED = "Center Changed and Booked";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	private final String CANCELLATION_WITHOUT_REFUND = "Cancellation Without Refund";
	private final String CANCELLATION_WITH_REFUND = "Cancellation With Refund";
	
	@Value("#{'${CORPORATE_CENTERS}'.split(',')}")
	private List<String> corporateCenterList;
	
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	private List<TimetableBean> timeTableList = null;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}
	
	private String getCenterSlotMappingUpdateSQL(boolean isCorporateExamCenterStudent) {
		String sql = null;
		if(isCorporateExamCenterStudent){
			sql = "Update exam.corporate_examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";
		}else{

			sql = "Update exam.examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";
		}
		return sql;
	}


	@Transactional(readOnly = false)
	public void insertApiRequest(TransactionBean transactionBean){
		String sql = " INSERT INTO portal.api_request " + 
				" (requestId, " + 
				" channel, " + 
				" accountId, " + 
				" referenceNo, " + 
				" amount, " + 
				" mode, " + 
				" currency, " + 
				" cuurencyCode, " + 
				" description, " + 
				" returnUrl, " + 
				" name, " + 
				" address, " + 
				" city, " + 
				" country, " + 
				" postalCode, " + 
				" phone, " + 
				" email, " + 
				" sapid, " + 
				" lastModifiedDate, " + 
				" createdDate, " + 
				" lastModifiedBy, " + 
				" createdBy)"+
				" VALUES " + 
				" ( ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " +
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" ?, " + 
				" sysdate(), " +
				" sysdate(), " +
				" ?, " + 
				" ?) ";


		jdbcTemplate.update(sql,new Object[]{transactionBean.getRequestId(),transactionBean.getChannel(),
				transactionBean.getAccountId(),transactionBean.getReferenceNo(),transactionBean.getAmount(),transactionBean.getMode(),
				transactionBean.getCurrency(),transactionBean.getCurrencyCode(),transactionBean.getDescription(),transactionBean.getReturnUrl(),
				transactionBean.getName(),transactionBean.getAddress(),transactionBean.getCity(),transactionBean.getCountry(),transactionBean.getPostalCode(),
				transactionBean.getPhone(),transactionBean.getEmail(),transactionBean.getSapid(),transactionBean.getSapid(),transactionBean.getSapid()});


	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<ExamAnnouncementBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<ExamAnnouncementBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamAnnouncementBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	@Transactional(readOnly = true)
	public String getCountTestTaken(String examDate, String examTime){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ "select count(*) as total "
				+ "from exam.exambookings "
				+ "where testTaken = 'Portal Started' "
				+ "and examDate = ? AND examTime = ?";
		return jdbcTemplate.queryForObject(
			sql, 
			new Object[] { 
				examDate, examTime 
			}, 
			String.class
		);
	}	
	
	
//	Added for SAS
  @SuppressWarnings("rawtypes")
  @Transactional(readOnly = true)
	public List<ExamAnnouncementBean> getAllActiveAnnouncements(String program,String programStructure){
		String sql = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
			 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";

		}else{
			 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
		}
		List<ExamAnnouncementBean> announcements = jdbcTemplate.query(sql, new Object[]{program,programStructure}, new BeanPropertyRowMapper(ExamAnnouncementBean.class));

		return announcements;
	}
  
  @Transactional(readOnly = true)
	public List<PassFailExamBean> getFailedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where isPass = 'N' and sapid = ? order by sem, subject asc";

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return studentsMarksList;
	}
  	
  @Transactional(readOnly = true)
	public List<PassFailExamBean> getPassFailedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where sapid = ? order by sem, subject asc";

		return jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
	}


  @Transactional(readOnly = true)
	public List<StudentMarksBean> getWrittenAttempts(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.marks where sapid = ? "
				+ " and writenscore is not null and writenscore <> '' and writenscore <> 'NA' ";


		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}
  	
  @Transactional(readOnly = true)
	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Double.class);


		}catch(Exception e){
			//
		}

		return examOrder;

	}
  
  @Transactional(readOnly = true)
	public List<PassFailExamBean> getPassSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where isPass = 'Y' and sapid = ? order by sem, subject asc";

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return studentsMarksList;
	}

  @Transactional(readOnly = true)
	public List<PassFailExamBean> getPassFailList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where  sapid = ? order by sem, subject asc";

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return studentsMarksList;
	}
  	
  @Transactional(readOnly = true)
	public ArrayList<String> getAssignSubmittedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "SELECT subject FROM exam.assignmentStatus a, exam.examorder b where a.examYear = b.year and a.examMonth = b.month "
				+ " and sapid = ? and submitted = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')  "
				+ " order by a.subject asc";*/

		String sql = "SELECT distinct subject FROM exam.assignmentStatus  where  "
				+ "  sapid = ? and submitted = 'Y'  "
				+ " order by subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return subjectList;
	}

  @Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject order by program, sem, subject";

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return programSubjectMappingList;
	}

  @Transactional(readOnly = true)
	public ArrayList<String> getSubjectsForStudents(StudentExamBean student,int lastSem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subject from exam.program_subject "
				+ " where program = ? and prgmStructApplicable = ? and sem <= ? ";

		//For lateral admission do not take subjects from Sem 1 and 2
		if("Y".equalsIgnoreCase(student.getIsLateral())){
			sql = sql + " and sem >=  " + student.getSem(); //Semester from student table, i.e. semester of very first enrollment. Can be 2 or 3
		}
		sql = sql + " order by program, sem, subject";
		
		
		ArrayList<String> subjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{
				student.getProgram(), student.getPrgmStructApplicable(), lastSem}, new SingleColumnRowMapper(String.class));
	

		return subjects;
	}

  @Transactional(readOnly = true)
	public List<TimetableBean> getTimetableListForGivenSubjects(ArrayList<String> subjects,StudentExamBean student) {

		if("Online".equals(student.getExamMode()) && !student.isCorporateExamCenterStudent()){
			return getTimetableForRegularOnlineExam();//For online students, slots are open, and not by subjects like offline students
		}
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<TimetableBean> tempTimeTableList = new ArrayList<TimetableBean>();
		if(student.isCorporateExamCenterStudent()){
			sql = "SELECT * FROM exam.corporate_timetable a, exam.examorder b "
					+ " where  a.examyear = b.year and  a.examMonth = b.month and "
					+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "

					+ " and a.prgmStructApplicable = ? "
					+ " order by a.subject, a.date, a.starttime  asc";
			tempTimeTableList = jdbcTemplate.query(sql, new Object[]{student.getPrgmStructApplicable()},new BeanPropertyRowMapper(TimetableBean.class));
		}else{
			sql = "SELECT * FROM exam.timetable a, exam.examorder b "
					+ " where  a.examyear = b.year and  a.examMonth = b.month and "
					+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and a.program = ? "
					+ " and a.prgmStructApplicable = ? "
					+ " order by a.subject, a.date, a.starttime  asc";
			tempTimeTableList = jdbcTemplate.query(sql, new Object[]{student.getProgram(), student.getPrgmStructApplicable()},new BeanPropertyRowMapper(TimetableBean.class));
		}



		List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
		if(tempTimeTableList != null && tempTimeTableList.size() > 0){
			for (int i = 0; i < tempTimeTableList.size(); i++) {
				TimetableBean bean = tempTimeTableList.get(i);
				if(subjects.contains(bean.getSubject())){
					timeTableList.add(bean);
				}
			}
		}
		return timeTableList;
	}
/*	public List<MbaWxTimeTableBean> getTimetableListForGivenTimeboundIds(ArrayList<String> timeboundIds,StudentBean student) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<MbaWxTimeTableBean> tempTimeTableList = new ArrayList<MbaWxTimeTableBean>();
		String ids = "'" + StringUtils.join(timeboundIds, "\',\'") + "'";
			sql = "SELECT a.* FROM exam.mba_wx_time_table a where a.timeboundId in("+ids+")"; 
			tempTimeTableList = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(MbaWxTimeTableBean.class));
		 
		return tempTimeTableList;
	}*/
	//Newly added//
  @Transactional(readOnly = true)
	public List<TimetableBean> getTimeTableListForCorporateStudents(ArrayList<String> subjects,StudentExamBean student) {



		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.corporate_timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and a.program = ? "
				+ " and a.prgmStructApplicable = ? "
				+ " order by a.subject, a.date, a.starttime  asc";

		List<TimetableBean> tempTimeTableList = jdbcTemplate.query(sql, new Object[]{student.getProgram(), student.getPrgmStructApplicable()},new BeanPropertyRowMapper(TimetableBean.class));

		List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
		if(tempTimeTableList != null && tempTimeTableList.size() > 0){
			for (int i = 0; i < tempTimeTableList.size(); i++) {
				TimetableBean bean = tempTimeTableList.get(i);
				if(subjects.contains(bean.getSubject())){
					timeTableList.add(bean);
				}
			}
		}
		return timeTableList;
	}
	//Ended//

  @Transactional(readOnly = true)
	public List<TimetableBean> getTimetableForRegularOnlineExam() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		if(this.timeTableList == null || this.timeTableList.size() == 0){



			String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
					+ " where  a.examyear = b.year and  a.examMonth = b.month and "
					+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "

				+ " order by a.date, a.starttime asc";

			this.timeTableList = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(TimetableBean.class));

		}
		return this.timeTableList;

	}


	public List<ExamCenterBean> getExamCenters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertFailTransaction(FailTransactionBean bean){
		try{
			final String sql = " INSERT INTO exam.failtransactions "
					+ "(sapid,"
					+ "year,"
					+ "month,"
					+ "trackId,"
					+ "tranStatus,"
					+ "amount,"
					+ "resIPAddress,"
					+ "ResErrorText,"
					+ "ResPaymentId,"
					+ "ResTrackID,"
					+ "ResErrorNo,"
					+ "ResResult,"
					+ "ResPosdate,"
					+ "ResTranId,"
					+ "ResAuth,"
					+ "ResAVR,"
					+ "ResRef,"
					+ "ResAmount,"
					+ "Resudf1,"
					+ "Resudf2,"
					+ "Resudf3,"
					+ "Resudf4,"
					+ "Resudf5,"
					+ "tranDateTime)"

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate())";

			jdbcTemplate.update(sql, new Object[] { 
					bean.getSapid(),
					bean.getYear(),
					bean.getMonth(),
					bean.getTrackId(),
					bean.getTranStatus(),
					bean.getAmount(),
					bean.getResIPAddress(),
					bean.getResErrorText(),
					bean.getResPaymentId(),
					bean.getResTrackID(),
					bean.getResErrorNo(),
					bean.getResResult(),
					bean.getResPosdate(),
					bean.getResTranId(),
					bean.getResAuth(),
					bean.getResAVR(),
					bean.getResRef(),
					bean.getResAmount(),
					bean.getResudf1(),
					bean.getResudf2(),
					bean.getResudf3(),
					bean.getResudf4(),
					bean.getResudf5(),
			});

		}catch(Exception e){
			
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertExamBookingTransaction(final List<ExamBookingTransactionBean> bookingList){

		final String sql = " INSERT INTO exam.exambookings "
				+ "(sapid,"
				+ " subject,"
				+ " year, "
				+ " month, "
				+ " trackId, "
				+ " amount, "
				+ " tranDateTime, "
				+ " tranStatus, "
				+ " booked,"
				+ " ddno,"
				+ " bank,"
				+ " ddAmount,"
				+ " program,"
				+ " sem,"
				+ " paymentMode,"
				+ " centerId,"
				+ " examDate,"
				+ " examTime,"
				+ " examMode,"
				+ " examEndTime,"
				+ " ddDate,"
				+ " lastModifiedBy,"
				+ "lastModifiedDate)"

				+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, sysdate())";


		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				ExamBookingTransactionBean bean = bookingList.get(i);
				ps.setString(1, bean.getSapid());
				ps.setString(2, bean.getSubject());
				ps.setString(3, bean.getYear());
				ps.setString(4, bean.getMonth());
				ps.setString(5, bean.getTrackId());
				ps.setString(6, bean.getAmount());
				ps.setString(7, bean.getTranStatus());
				ps.setString(8, bean.getBooked());
				ps.setString(9, bean.getDdno());
				ps.setString(10, bean.getBank());
				ps.setString(11, bean.getDdAmount());
				ps.setString(12, bean.getProgram());
				ps.setString(13, bean.getSem());
				ps.setString(14, bean.getPaymentMode());
				ps.setString(15, bean.getCenterId());
				ps.setString(16, bean.getExamDate());
				ps.setString(17, bean.getExamTime());
				ps.setString(18, bean.getExamMode());
				ps.setString(19, bean.getExamEndTime());
				ps.setString(20, bean.getDdDate());
				ps.setString(21, bean.getLastModifiedBy());
			}
			public int getBatchSize() {
				return bookingList.size();
			}
		});



	}
	public int sumOfArray(int [] array){
		int sumOfArray = 0;
		for(Integer i : array){
			sumOfArray = sumOfArray + i;
		}
		return sumOfArray;
	}


	public boolean checkUpdateCounts(int[] updateCounts) {
		boolean result = true;
		for (int i=0; i<updateCounts.length; i++) {
			if (updateCounts[i] >= 0) {
			}
			else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
			}
			else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
				return false;
			}else{
			}
		}

		return result;
	}  
	
	//check if already registered for another subject on date time start
	@Transactional(readOnly = true)
	public boolean checkIfAlreadyBookedAtSameDateTime(String sapid,String subject, String date, String time)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);

//		String sql = "SELECT count(*) FROM exam.exambookings where sapid = ? and examDate = ? and examTime = ? and subject not in ('"+subject+"') and booked ='Y' ";
//		int count = 0;  

		String sql = "SELECT count(*) FROM exam.exambookings where sapid = ? and examDate = ? and examTime = ? and subject not in ("+subject+") and booked ='Y' ";
		int count = 0;
		try {
			count = jdbcTemplate.queryForObject(sql.toString(), new Object[] {sapid,date,time}, Integer.class);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		if(count >0)
		{
			return true;
		}else
		{
			return false;
		}
	}
	//check if already registered for another subject on date time end

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public void upsertOnlineInitiationTransaction(String sapid, 
			final List<ExamBookingTransactionBean> bookingList,
			boolean hasProject, boolean isOnline) throws Exception{
		Connection conn = dataSource.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try{

			int noOfEntriesRequiredInBookingsTable = bookingList.size();
			conn.setAutoCommit(false);

			String sql = " INSERT INTO exam.exambookings "
					+ "(sapid,"
					+ " subject,"
					+ " year, "
					+ " month, "
					+ " trackId, "
					+ " amount, "
					+ " tranDateTime, "
					+ " tranStatus, "
					+ " booked,"
					+ " ddno,"
					+ " bank,"
					+ " ddAmount,"
					+ " program,"
					+ " sem,"
					+ " paymentMode,"
					+ " centerId,"
					+ " examDate,"
					+ " examTime,"
					+ " examMode,"
					+ " examEndTime,paymentOption)"

				+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?, ?,?,?)"
				+ " on duplicate key update "
				+ " trackId = ?,"
				+ " tranDateTime = sysdate(),"
				+ " tranStatus = ?, "
				+ " centerId = ?, "
				+ " amount =? ";


			pstmt = conn.prepareStatement(sql);

			for (ExamBookingTransactionBean bean : bookingList) {
				pstmt.setString(1, bean.getSapid());
				pstmt.setString(2, bean.getSubject());
				pstmt.setString(3, bean.getYear());
				pstmt.setString(4, bean.getMonth());
				pstmt.setString(5, bean.getTrackId());
				pstmt.setString(6, bean.getAmount());
				pstmt.setString(7, bean.getTranStatus());
				pstmt.setString(8, bean.getBooked());
				pstmt.setString(9, bean.getDdno());
				pstmt.setString(10, bean.getBank());
				pstmt.setString(11, bean.getDdAmount());
				pstmt.setString(12, bean.getProgram());
				pstmt.setString(13, bean.getSem());
				pstmt.setString(14, bean.getPaymentMode());
				pstmt.setString(15, bean.getCenterId());
				pstmt.setString(16, bean.getExamDate());
				pstmt.setString(17, bean.getExamTime());
				pstmt.setString(18, bean.getExamMode());
				pstmt.setString(19, bean.getExamEndTime());
				pstmt.setString(20, bean.getPaymentOption());
				
				pstmt.setString(21, bean.getTrackId());
				pstmt.setString(22, bean.getTranStatus());
				pstmt.setString(23, bean.getCenterId());
				pstmt.setString(24, bean.getAmount());
				

				pstmt.addBatch();
			}

			int[] updateCounts = pstmt.executeBatch();

			boolean isSuccess = checkUpdateCounts(updateCounts);
			if(!isSuccess){
				throw new Exception("Error: An error has occured in initiating exam booking. Please select exam center and try again.");
			}

			int examBookingDBUpdateResultsSum = sumOfArray(updateCounts);
			if(noOfEntriesRequiredInBookingsTable != examBookingDBUpdateResultsSum){
				throw new Exception("An error has occured in initiating exam booking. Please select exam center and try again.");
			}

			conn.commit();


			/*int noOfUpdationsRequiredForBlockingSeats = bookingList.size();

			if(hasProject){
				//No seat is blocked for Project subject
				noOfUpdationsRequiredForBlockingSeats = bookingList.size() - 1;
			}

			//Insert tracking numbers for current interaction
			String sql = " INSERT INTO exam.exambookings "
					+ "(sapid,"
					+ " subject,"
					+ " year, "
					+ " month, "
					+ " trackId, "
					+ " amount, "
					+ " tranDateTime, "
					+ " tranStatus, "
					+ " booked,"
					+ " ddno,"
					+ " bank,"
					+ " ddAmount,"
					+ " program,"
					+ " sem,"
					+ " paymentMode,"
					+ " centerId,"
					+ " examDate,"
					+ " examTime,"
					+ " examMode,"
					+ " examEndTime)"

				+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?, ?,?)"
				+ " on duplicate key update "
				+ " trackId = ?,"
				+ " tranDateTime = sysdate(),"
				+ " tranStatus = ?, "
				+ " centerId = ?, "
				+ " amount =? ";


			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getSubject());
					ps.setString(3, bean.getYear());
					ps.setString(4, bean.getMonth());
					ps.setString(5, bean.getTrackId());
					ps.setString(6, bean.getAmount());
					ps.setString(7, bean.getTranStatus());
					ps.setString(8, bean.getBooked());
					ps.setString(9, bean.getDdno());
					ps.setString(10, bean.getBank());
					ps.setString(11, bean.getDdAmount());
					ps.setString(12, bean.getProgram());
					ps.setString(13, bean.getSem());
					ps.setString(14, bean.getPaymentMode());
					ps.setString(15, bean.getCenterId());
					ps.setString(16, bean.getExamDate());
					ps.setString(17, bean.getExamTime());
					ps.setString(18, bean.getExamMode());
					ps.setString(19, bean.getExamEndTime());

					ps.setString(20, bean.getTrackId());
					ps.setString(21, bean.getTranStatus());
					ps.setString(22, bean.getCenterId());
					ps.setString(23, bean.getAmount());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			int examBookingDBUpdateResultsSum = sumOfArray(examBookingDBUpdateResults);

			if(noOfEntriesRequiredInBookingsTable != examBookingDBUpdateResultsSum){
				throw new Exception("An error has occured in initiating exam booking. Please select exam center and try again.");
			}

			 */
			/*sql = "Update exam.examcenter_slot_mapping"
					+ " set onHold = COALESCE(onhold, 0) + 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());


				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			int slotBookingDBUpdateResultsSum = sumOfArray(slotBookingDBUpdateResults);

			if(noOfUpdationsRequiredForBlockingSeats != slotBookingDBUpdateResultsSum && isOnline){
				//Slots are updated only for online exam students
				throw new Exception("An error has occured in initiating exam booking. Please select exam center and try again.");
			}

			/*int[] result = ArrayUtils.addAll(examBookingDBUpdateResults, slotBookingDBUpdateResults);

			if(result == null){
				throw new Exception("Error in updating transaction details OR updating on Hold seats.");
			}
			return result;*/

		}catch(Exception e){
			
			conn.rollback();
			throw e;
		}finally{
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}

	}
	
	@Transactional(readOnly = false)
	public void markTransactionsFailed(final List<ExamBookingTransactionBean> bookingList){
		//Insert tracking numbers for current interaction
		String sql = "Update exam.exambookings "
				+ " set tranStatus = '" + TRANSACTION_FAILED + "',"
				+ " error = ?,"
				+ " lastModifiedBy = 'AutoBooking Scheduler',"
				+ " lastModifiedDate = sysdate()"
				+ " where  "
				+ "  sapid = ? "
				+ " and year = ? "
				+ " and month = ? "
				+ " and trackid = ? "
				+ " and booked = 'N'"
				+ " and paymentMode = 'Online' ";
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getError());
					ps.setString(2, bean.getSapid());
					ps.setString(3, bean.getYear());
					ps.setString(4, bean.getMonth());
					ps.setString(5, bean.getTrackId());
				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});


		}catch(Exception e){
			
			
		}



	}
	
	@Transactional(readOnly = false)
	public int markTransactionsFailed(ExamBookingTransactionBean bean) {
		String sql = "Update exam.exambookings set tranStatus = '" + TRANSACTION_FAILED + "', "
				+ " error = ?, paymentOption=?, lastModifiedBy = 'AutoProjectBooking Scheduler', lastModifiedDate = sysdate() "
				+ " where sapid = ? and year = ? and month = ? and trackid = ? and booked = 'N' ";
		
		return jdbcTemplate.update(sql,bean.getError(), bean.getPaymentOption(), bean.getSapid(), bean.getYear(), bean.getMonth(), bean.getTrackId());
	}
	
	@Transactional(readOnly = false)
	public int updatePendingTxnDetails(ExamBookingTransactionBean bean) {
		String sql = "Update exam.exambookings set error = ?, paymentOption=?, lastModifiedBy = 'AutoProjectBooking Scheduler', lastModifiedDate = sysdate() "
				+ " where sapid = ? and year = ? and month = ? and trackid = ? and booked = 'N' ";
		
		return jdbcTemplate.update(sql,bean.getError(), bean.getPaymentOption(), bean.getSapid(), bean.getYear(), bean.getMonth(), bean.getTrackId());
	}
	

	/*@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public void markTransactionsFailed(ExamBookingTransactionBean bean){
		try{
			//Insert tracking numbers for current interaction
			String sql = "Update exam.exambookings "
					+ " set tranStatus = '" + TRANSACTION_FAILED + "'"
					+ " where trackid = ? ";
					//+ " and booked = 'N'"
					//+ " and paymentMode = 'Online' ";


			jdbcTemplate = new JdbcTemplate(dataSource);
			int examBookingDBUpdateResults = jdbcTemplate.update(sql, new Object[]{bean.getTrackId()});




		}catch(Exception e){
			
		}

	}*/


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int[] clearOldOnlineInitiationTransaction(String sapid, String trackId){
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);

			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
			String sql = "Select * from exam.exambookings "
					+ " where sapid = ? "
					+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
					+ " and booked = 'N'"
					+ " and paymentMode = 'Online' ";

			//Track id will be passed by SessionListener to expire only those rows which came from current transaction only.
			//This avoids issue where if student comes back and is in middle of payment process, then it will have new track id and Session listener will not impact those rows.
			if(trackId != null){
				sql = sql +  " and trackid = '"+trackId+"' ";
			}

			List<ExamBookingTransactionBean> oldIncompleteBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));


			sql = "Update exam.examcenter_slot_mapping"
					+ " set onHold = COALESCE(onhold, 0) - 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";


			final List<ExamBookingTransactionBean> bookingList = oldIncompleteBookings;
			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});





			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
			sql = "Update exam.exambookings "
					+ " set tranStatus = '" + EXPIRED + "'"
					+ " where sapid = ? "
					+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
					+ " and booked = 'N'"
					+ " and paymentMode = 'Online' ";

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { sapid});
			//int[] result = ArrayUtils.addAll(examBookingDBUpdateResults, slotBookingDBUpdateResults);
			return slotBookingDBUpdateResults;

		}catch(Exception e){
			
			return null;
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getExpiredTransaction(){
		try {
			String sql = "select * from exam.exambookings where booked = 'N' and paymentMode = 'Online' "
					+ " AND centerId <> '-1' AND subject NOT IN ('Project', 'Module 4 - Project') "
					+ " and tranStatus = 'Online Payment Initiated' and ((TIMESTAMPDIFF(second,tranDateTime, sysdate())) > 10800) "
					+ " group by trackId;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			return (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void clearOldOnlineInitiationTransactionPeriodically(List<ExamBookingTransactionBean> list){
			
			String sql = " UPDATE exam.exambookings " + 
					"SET  " + 
					"    tranStatus = '"+EXPIRED+"', `lastModifiedBy` = 'ClearOnHoldSeats Scheduler', lastModifiedDate = current_timestamp() " + 
					"WHERE " + 
					"    sapid = ? AND year = ? " + 
					"        AND month = ? " + 
					"        AND trackId = ? ";
			
			try {
				jdbcTemplate = new JdbcTemplate(dataSource);
				int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						ExamBookingTransactionBean bean = list.get(i);
						ps.setString(1, bean.getSapid());
						ps.setString(2, bean.getYear());
						ps.setString(3, bean.getMonth());
						ps.setString(4, bean.getTrackId());
					}
					public int getBatchSize() {
						return list.size();
					}
				});
			}catch (Exception e) {
				// TODO: handle exception
			}
			

//			String sql = "update  exam.exambookings "
//					+ " set tranStatus = '"+EXPIRED+"'"
//
//					+ " where booked = 'N'"
//					+ " and paymentMode = 'Online' "
//					+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
//					+ " and ((TIMESTAMPDIFF(second,tranDateTime, sysdate())) > 10800) ";


//			try{
//			int noOfRowsChanged = jdbcTemplate.update(sql);
//
//
//
//			/*jdbcTemplate = new JdbcTemplate(dataSource);
//
//			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
//			String sql = "Select * from exam.exambookings "
//					+ " where tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
//					+ " and booked = 'N'"
//					+ " and paymentMode = 'Online' "
//					+ " and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800)";
//
//
//			List<ExamBookingTransactionBean> oldIncompleteBookings = jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
//
//
//			if(oldIncompleteBookings != null && oldIncompleteBookings.size() > 0){
//
//				sql = "Update exam.examcenter_slot_mapping"
//						+ " set onHold = COALESCE(onhold, 0) - 1 "
//						+ " where examcenterid = ? "
//						+ " and date = ? "
//						+ " and starttime = ?";
//
//				final List<ExamBookingTransactionBean> bookingList = oldIncompleteBookings;
//				int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
//
//					@Override
//					public void setValues(PreparedStatement ps, int i)	throws SQLException {
//						ExamBookingTransactionBean bean = bookingList.get(i);
//						ps.setString(1, bean.getCenterId());
//						ps.setString(2, bean.getExamDate());
//						ps.setString(3, bean.getExamTime());
//
//					}
//					public int getBatchSize() {
//						return bookingList.size();
//					}
//				});
//
//
//
//
//
//				//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
//				sql = "Update exam.exambookings "
//						+ " set tranStatus = '" + EXPIRED + "'"
//						+ " where trackid = ? "
//						+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
//						+ " and booked = 'N'"
//						+ " and paymentMode = 'Online' ";
//
//
//				slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
//
//					@Override
//					public void setValues(PreparedStatement ps, int i)	throws SQLException {
//						ExamBookingTransactionBean bean = bookingList.get(i);
//						ps.setString(1, bean.getTrackId());
//					}
//					public int getBatchSize() {
//						return bookingList.size();
//					}
//				});
//
//
//
//
//			}*/
//
//
//		}catch(Exception e){
//			try{
//			int noOfRowsChanged = jdbcTemplate.update(sql);
//
//			}catch(Exception e2){
//			}
//			
//
//		}

	}

	
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";



			ArrayList<StudentExamBean> studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));

			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);
				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			return student;
		}catch(Exception e){
			
			return null;
		}

	}
	
	@Transactional(readOnly = true)
	public UserAuthorizationExamBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationExamBean user = (UserAuthorizationExamBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationExamBean.class));
			return user;
		} catch (Exception e) {
			return null;
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAuthorizedCenterCodes(UserAuthorizationExamBean userAuthorization) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> centers = new ArrayList<String>();

		//Convert Mumbai, Kolkata, Delhi to 'Mumbai','Kolkata','Delhi'
		String authorizedLCWithQuotes = "'" + userAuthorization.getAuthorizedLC() + "'";
		authorizedLCWithQuotes = authorizedLCWithQuotes.replaceAll(",", "','");


		if(userAuthorization.getAuthorizedLC() != null && !"".equals(userAuthorization.getAuthorizedLC().trim())){
			String sql = "SELECT sfdcId FROM exam.centers where lc in (" + authorizedLCWithQuotes + ")";
			centers = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		}

		if(userAuthorization.getAuthorizedCenters() != null && !"".equals(userAuthorization.getAuthorizedCenters().trim())){
			//Add IC codes
			List<String> authorizedICs = Arrays.asList(userAuthorization.getAuthorizedCenters().split("\\s*,\\s*"));
			centers.addAll(authorizedICs);
		}

		return centers;
	}
	
	@Transactional(readOnly = true)
	public StudentMarksBean getRegistrationForYearMonthSem(String sapid,String sem){
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM exam.registration "
					+ " where  sapid = ? and sem = ?";
			return (StudentMarksBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid,sem},new BeanPropertyRowMapper(StudentMarksBean.class));
		} catch (Exception e) {
			//
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getRegistrations(String sapid) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.registration where sapid = ? ";
		List<StudentMarksBean> registrationList = new ArrayList<>();
		try{
			registrationList = jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
		}
		return registrationList;
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getActiveRegistrations(String sapid) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.registration where sapid = ? "
				+ " and STR_TO_DATE(concat('01-',month,'-',year), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y') ";
		List<StudentMarksBean> registrationList = new ArrayList<>();
		try{
			registrationList = jdbcTemplate.query(sql, new Object[]{sapid,"01-"+getLiveAcadConentMonth()  + "-"+ getLiveAcadConentYear()},new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
		}
		return registrationList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getExamBookingStatus(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and tranStatus <> '" + EXPIRED +"' "
				+ " and tranStatus <> '" + ONLINE_PAYMENT_INITIATED +"' "
				+ " order by a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getUnSuccessfulExamBookings(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT DISTINCT `eb`.`trackId`, `eb`.`tranStatus`, `eb`.`sapid`, `eb`.`subject`, `eb`.`paymentOption` "
				+ " FROM `exam`.`exambookings` `eb` "
				+ " INNER JOIN `exam`.`examorder` `eo` ON `eb`.`year` = `eo`.`year` AND `eb`.`month` = `eo`.`month` "
				+ " WHERE "
				+ " `eb`.`sapid` = ? "
				+ " AND ("
						// Check if time table live in case of normal exams
					+ " ( `eo`.`order` = (Select MAX(`order`) FROM `exam`.`examorder` WHERE `timeTableLive` = 'Y' ) AND `eb`.`subject` NOT IN ('Project', 'Module 4 - Project')) "
					
						// Check if project submission live for project subjects
					+ " OR ( `eo`.`order` = (Select MAX(`order`) FROM `exam`.`examorder` WHERE `projectSubmissionLive` = 'Y') AND `eb`.`subject` IN ('Project', 'Module 4 - Project')) "
				+ ") "
				+ " AND `eb`.`booked` <> 'Y' "
				+ " AND `eb`.`booked` <> 'RL' "
				+ " AND `eb`.`booked` <> 'RF' "
				+ " AND `eb`.`paymentMode` = 'Online'"
				+ " ORDER BY `eb`.`subject` asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedBookings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllUnSuccessfulExamBookings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b, exam.students s where a.year = b.year and a.month = b.month "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800) "
				+ " and a.booked <> 'Y' "
				+ " and a.booked <> 'RL' "
				+ " and a.booked <> 'RF' "
				+ " and a.centerId <> '-1' "
				+ " and a.tranStatus not in ('" + CANCELLATION_WITH_REFUND + "','" + CANCELLATION_WITHOUT_REFUND + "') "
				+ " and ( "
					/* Also take failed transactions into scheduler if time is between 5-5:15 am */
					+ " a.tranStatus <> '" + TRANSACTION_FAILED + "' "
					+ " OR CURRENT_TIME() between '05:00:00' AND '05:20:00' "
				+ " )"
				/* + " and a.tranStatus <> '" + EXPIRED + "'" */
				+ " and a.sapid = s.sapid "
				+ " and a.paymentMode = 'Online'"
				/* + " and a.paymentOption = 'billdesk'" */
				/* + " and a.subject NOT IN ('Project', 'Module 4 - Project')" */
				+ " and DATEDIFF(sysdate(),tranDateTime) <= 2" /* to avoid expired transactions older than 2 day */
				+ " and DATEDIFF(sysdate(),tranDateTime) >= 0" /* to consider current day transaction */
				+ " group by a.trackId "
				+ " order by a.tranDateTime asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getUnSuccessfulExamBookingsByTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct a.trackId FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and trackId = ? and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and a.booked <> 'Y' "
				+ " and a.paymentMode = 'Online'"
				+ " order by a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsBooked(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'P' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and paymentMode = 'DD' "
				+ " order by a.subject asc";

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsBookedForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and a.subject NOT IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.subject asc";

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = false)
	public List<ExamBookingTransactionBean> getAlreadyBookedSubjectsForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject, trackId FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and a.subject NOT IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.subject asc";

		List<ExamBookingTransactionBean> list = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper<ExamBookingTransactionBean>(ExamBookingTransactionBean.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getSubjectsCentersForTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings where trackId = ?";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getApprovedOnlineTransSubjects(String sapid) {
		ArrayList<String> approvedOnlineTransactionSubjects = new ArrayList<>();
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked <> 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and ( "
				+ "(paymentMode = 'DD') "
				+ " OR "
				+ "(paymentMode = 'Online' and tranStatus = '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "') "
				+ ")"
				+ " order by a.subject asc";*/

		String year = getLiveExamYear();
		String month = getLiveExamMonth();

		String sql = "SELECT * FROM exam.exambookings e where e.year = ? and e.month = ? "
				+ " and e.sapid = ? and e.booked <> 'Y'  "
				+ " AND `e`.`description` not like '%to be refunded'  "
				+ " and e.paymentMode = 'Online' and e.tranStatus = '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "' "
				+ " and concat(e.sapid,e.subject) not in ( SELECT concat(eb.sapid,eb.subject)"
				+ " from  exam.exambookings eb where eb.booked = 'Y' and e.year=eb.year and e.month=eb.month)" 
				+ " order by subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{year, month, sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		
		for (ExamBookingTransactionBean bean : bookingList) {
			approvedOnlineTransactionSubjects.add(bean.getSubject());
		}
		
		return approvedOnlineTransactionSubjects;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getApprovedOnlineTransactions(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked <> 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and paymentMode = 'Online' "
				+ " and tranStatus = '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "' "
				+ " order by a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingFromSapidFromMonthAndYearWithoutTimeTableFlag(String sapid,String year ,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings_history a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and a.year = ? and a.month = ? "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid,year,month}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getConfirmedBookedSapIdListFromMonthAndYearWithoutTimeTableFlag(String month,String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct sapid FROM exam.exambookings_history a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and a.booked = 'Y' "
				+ " and a.year = ? and a.month = ?  "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> sapIdList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{year,month},new SingleColumnRowMapper(String.class));
		return sapIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and subject NOT IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingMBAWX> getConfirmedBookingForMBAWx(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT   " + 
									"a.* ,t.examStartDateTime,t.examEndDateTime,s.centerId ,a.year as examYear,a.month as examMonth  " + 
							"FROM " + 
								    "exam.mba_wx_bookings a, " + 
						    		"exam.mba_wx_exam_live_setting b , exam.mba_wx_time_table t,exam.mba_wx_slots s  " + 
				    		"WHERE " + 
				    				"a.year = b.examYear AND a.month = b.examMonth  AND b.type='Hall Ticket' " + 
			    					"AND a.sapid = ?  " + 
			    					"AND a.bookingStatus = 'Y' " + 
			    					"AND t.timeboundId=a.timeboundId and s.slotId=a.slotId " + 
			    					"AND b.examYear=a.year and b.examMonth =a.month " + 
	    					"ORDER BY "+
	    					        "a.lastUpdatedOn , a.timeboundId ASC";  

		ArrayList<ExamBookingMBAWX> bookingList = (ArrayList<ExamBookingMBAWX>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingMBAWX.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public String getSubjectByTimeboundId(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT ps.subject " + 
				"FROM" + 
				"    lti.student_subject_config ssc, exam.program_sem_subject ps " + 
				"WHERE" + 
				"    ssc.id =? " + 
				"AND ssc.prgm_sem_subj_id = ps.id"; 

		ArrayList<StudentSubjectConfigExamBean> subjectList = (ArrayList<StudentSubjectConfigExamBean>) jdbcTemplate.query(sql, new Object[]{id}, new BeanPropertyRowMapper(StudentSubjectConfigExamBean.class));
		return subjectList.get(0).getSubject();  
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,ArrayList<ExamBookingTransactionBean>> getMapOfSapIdAndConfirmedBookingFromMonthAndYear(ExamBookingTransactionBean transactionBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Query all the unique sapid and then query the confirmed bookings without timetable flag and then add it to a map which will be used to generate fee receipts.//
		HashMap<String,ArrayList<ExamBookingTransactionBean>> mapOfUniqueSapidAndExamBookingsFromMonthAndYear = new HashMap<String,ArrayList<ExamBookingTransactionBean>>();
		String sqlForDistinctSapid = "SELECT distinct a.sapid FROM exam.exambookings_history a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and a.year = ? and a.month = ? and a.booked = 'Y' "
				+ " group by a.sapid asc";
		ArrayList<String> sapIdList = (ArrayList<String>)jdbcTemplate.query(sqlForDistinctSapid, new Object[]{transactionBean.getYear(),transactionBean.getMonth()},new SingleColumnRowMapper(String.class));

		for(String sapid : sapIdList){
			ArrayList<ExamBookingTransactionBean> confirmedBookingList = getConfirmedBookingFromSapidFromMonthAndYearWithoutTimeTableFlag(sapid,transactionBean.getYear(),transactionBean.getMonth());
			mapOfUniqueSapidAndExamBookingsFromMonthAndYear.put(sapid, confirmedBookingList);

		}

		return mapOfUniqueSapidAndExamBookingsFromMonthAndYear;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getConfirmedBookedSapIdListFromMonthAndYear(String month,String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct sapid FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and a.year = ? and a.month = ? "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> sapIdList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{year,month},new SingleColumnRowMapper(String.class));
		return sapIdList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBookingsForMonthAndYear(String examYear,String examMonth){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.exambookings where year = ? and month = ? and booked <> 'N' and paymentMode = 'Online' order by trackid ";
		ArrayList<ExamBookingTransactionBean> getConfirmedBookingsForMonthAndYear = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{examYear,examMonth},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return getConfirmedBookingsForMonthAndYear;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getConfirmedBookingSubjects(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = false)
	public void insertDocumentRecord(String fileName,String year,String month,String sapid,String doucmentType){
		String sql = "INSERT INTO exam.receipt_hallticket "
				+ " (sapid,year,month,filePath,documentType,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
				+ " VALUES(?,?,?,?,?,?,sysdate(),?,sysdate())"
				+" ON DUPLICATE KEY UPDATE "
				+" lastModifiedBy = ? , filePath = ? ";

		int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { sapid, year, month, fileName, doucmentType,sapid,sapid,sapid,fileName});
	}
	
	@Transactional(readOnly = false)
	public int insertDocumentRecord(final String fileName,final String year,final String month,final String sapid,final String doucmentType,final String trackId){
		jdbcTemplate = new JdbcTemplate(dataSource); 
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			final String sql = "INSERT INTO exam.receipt_hallticket "
					+ " (sapid,year,month,filePath,documentType,createdBy,createdDate,lastModifiedBy,lastModifiedDate,referenceId) "
					+ " VALUES(?,?,?,?,?,?,sysdate(),?,sysdate(),?)"
					+" ON DUPLICATE KEY UPDATE "
					+" lastModifiedBy = ? , filePath = ? ";
			 
			jdbcTemplate.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					statement.setString(1, sapid);
					statement.setString(2, year);
					statement.setString(3, month);
					statement.setString(4, fileName);
					statement.setString(5, doucmentType);
					statement.setString(6, sapid);
					statement.setString(7, sapid);
					statement.setString(8, trackId); //
					statement.setString(9, sapid);
					statement.setString(10, fileName); 
					
					return statement;
				}
			}, keyHolder);
		} catch (DataAccessException e) {  
			
		}
		
		return keyHolder.getKey().intValue();
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> listOfDocumentsBasedOnSapidAndDocType(String sapid,String documentType){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.receipt_hallticket where sapid = ? and documentType = ? ";
		try{
			ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql,new Object[]{sapid,documentType}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			return listOfFeeReceiptsBasedOnSapid;
		}catch(Exception e){
			
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public void batchInsertOfDocumentRecords(final ArrayList<ExamBookingTransactionBean> transactionBeanList,final String doucmentType){
		String sql = "INSERT INTO exam.receipt_hallticket "
				+ " (sapid,year,month,filePath,documentType,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
				+ " VALUES(?,?,?,?,?,?,sysdate(),?,sysdate())";
		/*+" ON DUPLICATE KEY UPDATE "
				+" lastModifiedBy = ? ";*/

		int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExamBookingTransactionBean transactionBean = transactionBeanList.get(i);
				ps.setString(1,transactionBean.getSapid());
				ps.setString(2,transactionBean.getYear());
				ps.setString(3,transactionBean.getMonth());
				ps.setString(4,transactionBean.getFilePath());
				ps.setString(5,doucmentType);
				ps.setString(6,transactionBean.getSapid());
				ps.setString(7,transactionBean.getSapid());


			}

			@Override
			public int getBatchSize() {
				return transactionBeanList.size();
			}
		});
	}
	
	@Transactional(readOnly = true)
	public boolean alreadyDownloadedDocument(ExamBookingTransactionBean examBookingBean,String documentType)
	{
		StringBuffer sql = new StringBuffer("Select count(*) from exam.receipt_hallticket ");
		sql.append(" where ");
		sql.append(" year = ? and month = ? ");
		sql.append(" and documentType =? ");
		jdbcTemplate = new JdbcTemplate(dataSource);

		int count = jdbcTemplate.queryForObject(sql.toString(), new Object[] {examBookingBean.getYear(),examBookingBean.getMonth(),documentType  }, Integer.class);
		if(count >0)
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	
	@Transactional(readOnly = false)
	public void insertAdHocRefundRecord(ExamAdhocPaymentBean bean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO exam.ad_hoc_refund "
				+" (sapid,merchantRefNo,createdBy,createdDate,lastModifiedBy , lastModifiedDate) "
				+ " VALUES(?,?,?,sysdate(),?, sysdate()) ";
		int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] {bean.getSapId(),bean.getMerchantRefNo(),bean.getCreatedBy(),
				bean.getLastModifiedBy()});
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getCorporateExamCenterIdNameMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.corporate_examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,new Object[]{}, new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			if(bean.getCity() != null && bean.getExamCenterName() != null && bean.getCity().equals(bean.getExamCenterName())) {
				examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName());
			} else {
				examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName()+","+bean.getLocality()+ ","+ bean.getCity());
			}
		}

		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,Boolean> getConfirmedBookingsExcludingCurrentCycleMap(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> getConfirmedBookingsExcludingCurrentCycleList = new ArrayList<ExamBookingTransactionBean>();
		HashMap<String,Boolean> mapOfSubjectAndIsBookedStatus = new HashMap<String,Boolean>();
		String sql = " select eb.* from exam.exambookings eb , exam.examorder eo where "
				+ "  eb.sapid = ? and eb.booked = 'Y' and "
				+ " eb.year = eo.year and eb.month = eo.month  and "
				+ " eo.order < (select MAX(examorder.order) from exam.examorder where timeTableLive = 'Y') ";
		getConfirmedBookingsExcludingCurrentCycleList = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		for(ExamBookingTransactionBean examTranBean: getConfirmedBookingsExcludingCurrentCycleList){
			mapOfSubjectAndIsBookedStatus.put(examTranBean.getSubject(), true);
		}
		return mapOfSubjectAndIsBookedStatus;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedBookingPastToPresent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings  where sapid = ? and booked = 'Y' "
				+ " order by examDate, examTime, subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings where sapid = ?  and booked = 'Y' order by examDate, examTime, subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}


	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and (a.booked = 'Y' or a.booked = 'RL') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.subject asc";*/

		String month = getLiveExamMonth();
		String year = getLiveExamYear();


		String sql = "SELECT * FROM exam.exambookings  where year = ? and month = ? "
				+ " and sapid = ? and (booked = 'Y' or booked = 'RL') and subject NOT IN ('Project', 'Module 4 - Project') ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{year, month, sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	//Added for getting details of student exam bookings in massFeeRecieptDownload--->
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBooking(String sapid,String month,String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings_history  where year = ? and month = ? "
				+ " and sapid = ? and (booked = 'Y' or booked = 'RL') ";
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{year, month, sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}//--->end
	
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBookings(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and (a.booked = 'Y') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.subject asc";*/
		String year = getLiveExamYear();
		String month = getLiveExamMonth();

		String sql = "SELECT * FROM exam.exambookings  where "
				+ "  sapid = ? and booked = 'Y' and  month = ? and year = ? ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid, month, year}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedOrRelesedBooking() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and  (a.booked = 'Y' or a.booked = 'RL') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	@Transactional(readOnly = true)
	public Page<ExamBookingTransactionBean> getDDsPage(int pageNo, int pageSize, ExamBookingTransactionBean transaction) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT distinct eb.sapid, year, month, transtatus, ddno, bank, ddReason, amount, emailId, mobile, ddDate , trackId, "
				+ " altphone, firstName, lastName , count(subject) subjectCount "
				+ " FROM exam.exambookings eb, exam.students s where 1 = 1 and paymentMode = 'DD' "
				+ " and eb.sapid = s.sapid ";

		String countSql = "SELECT count(*) "
				+ " FROM exam.exambookings eb, exam.students s where 1 = 1 and paymentMode = 'DD' "
				+ " and eb.sapid = s.sapid ";

		if( transaction.getBank() != null &&   !("".equals(transaction.getBank()))){
			sql = sql + " and bank like  ? ";
			countSql = countSql + " and bank like  ? ";
			parameters.add("%"+transaction.getBank()+"%");
		}
		if( transaction.getDdno() != null &&   !("".equals(transaction.getDdno()))){
			sql = sql + " and ddno = ? ";
			countSql = countSql + " and ddno = ? ";
			parameters.add(transaction.getDdno());
		}
		if( transaction.getSapid() != null &&   !("".equals(transaction.getSapid()))){
			sql = sql + " and eb.sapid = ? ";
			countSql = countSql + " and eb.sapid = ? ";
			parameters.add(transaction.getSapid());
		}
		if( transaction.getYear() != null &&   !("".equals(transaction.getYear()))){
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(transaction.getYear());
		}
		if( transaction.getMonth() != null &&   !("".equals(transaction.getMonth()))){
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(transaction.getMonth());
		}
		if( transaction.getTranStatus() != null &&   !("".equals(transaction.getTranStatus()))){
			sql = sql + " and tranStatus = ? ";
			countSql = countSql + " and tranStatus = ? ";
			parameters.add(transaction.getTranStatus());
		}

		sql = sql + " group by eb.sapid, eb.ddno, eb.tranStatus, eb.trackId ";
		//countSql = countSql + " group by eb.sapid, eb.ddno, eb.tranStatus, eb.trackId ";

		Object[] args = parameters.toArray();

		PaginationHelper<ExamBookingTransactionBean> pagingHelper = new PaginationHelper<ExamBookingTransactionBean>();
		Page<ExamBookingTransactionBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));


		return page;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, StudentExamBean> getAllStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void approveDD(String sapid, String ddno, String year, String month, String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "Update exam.exambookings"
				+ " set booked = 'A' ,"
				+ " tranStatus = '" + DD_APPROVED + "' "
				+ " where sapid = ? "
				+ " and ddno = ? "
				+ " and year = ?"
				+ " and month = ?"
				+ " and trackId = ?";
		int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { sapid, ddno, year, month, trackId});

	}
	
	@Transactional(readOnly = false)
	public void rejectDD(String sapid, String ddno, String year, String month, String reason, String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "Update exam.exambookings"
				+ " set booked = 'R' ,"
				+ " ddReason = ? , "
				+ " tranStatus = '" + DD_REJECTED + "' "
				+ " where sapid = ? "
				+ " and ddno = ? "
				+ " and year = ?"
				+ " and month = ?"
				+ " and trackId = ?";

		int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] {reason,  sapid, ddno, year, month, trackId});

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> updateSeatsForDD(String sapid, final List<ExamBookingTransactionBean> bookingList, 
			boolean isCorporateExamCenterStudent) {
		try{
			String subjectCommaSeparated = getCommaSepareatedSubjects(bookingList);
			/*for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
				}
			}*/
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " centerId = ?, "
					+ " examDate = ?, "
					+ " examTime = ?, "
					+ " examEndTime = ?, "
					+ " bookingCompleteTime = sysdate() "
					+ " where sapid = ? "
					+ " and subject = ? "
					+ " and tranStatus = '" + DD_APPROVED + "' "
					+ " and booked <> 'Y' ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());
					ps.setString(4, bean.getExamEndTime());
					ps.setString(5, bean.getSapid());
					ps.setString(6, bean.getSubject());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			sql = "Select * from exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and a.sapid = ? "
					+ " and tranStatus = '"+DD_APPROVED+"'"
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and a.booked = 'Y'"
					+ " and a.subject in ("+subjectCommaSeparated+") ";


			List<ExamBookingTransactionBean> toBeUpdatedBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			final List<ExamBookingTransactionBean> toBeUpdatedBookingsList = toBeUpdatedBookings;

			sql = getCenterSlotMappingUpdateSQL(isCorporateExamCenterStudent);

			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = toBeUpdatedBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return toBeUpdatedBookingsList.size();
				}
			});


			sql = "Select * from exam.exambookings  a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and sapid = ? "
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and booked = 'Y' order by a.examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> updateSeatsForOnlineApprovedTransaction(String sapid, 
			final List<ExamBookingTransactionBean> bookingList, boolean isCorporateExamCenterStudent) {
		try{
			String subjectCommaSeparated = getCommaSepareatedSubjects(bookingList);
			/*for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
				}
			}*/
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " centerId = ?, "
					+ " examDate = ?, "
					+ " examTime = ?, "
					+ " examEndTime = ?, "
					+ " bookingCompleteTime = sysdate() "
					+ " where sapid = ? "
					+ " and subject = ? "
					+ " and tranStatus = '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "' "
					+ " and booked <> 'Y' ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());
					ps.setString(4, bean.getExamEndTime());
					ps.setString(5, bean.getSapid());
					ps.setString(6, bean.getSubject());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			sql = "Select * from exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and a.sapid = ? "
					+ " and tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"'"
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and a.booked = 'Y'"
					+ " and a.subject in ("+subjectCommaSeparated+") ";


			List<ExamBookingTransactionBean> toBeUpdatedBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			final List<ExamBookingTransactionBean> toBeUpdatedBookingsList = toBeUpdatedBookings;

			sql = getCenterSlotMappingUpdateSQL(isCorporateExamCenterStudent);

			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = toBeUpdatedBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return toBeUpdatedBookingsList.size();
				}
			});


			sql = "Select * from exam.exambookings  a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and sapid = ? "
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and booked = 'Y' order by a.examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}

	}



	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> updateSeatsForRealeasedNoCharges(String sapid, 
			final List<ExamBookingTransactionBean> bookingList, boolean isCorporateExamCenterStudent) {
		try{
			String subjectCommaSeparated = getCommaSepareatedSubjects(bookingList);
			/*for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
				}
			}*/
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " centerId = ?, "
					+ " examDate = ?, "
					+ " examTime = ?, "
					+ " examEndTime = ?, "
					+ " bookingCompleteTime = sysdate() "
					+ " where sapid = ? "
					+ " and subject = ? "
					+ " and tranStatus = '" + SEAT_RELEASED_NO_CHARGES + "' "
					+ " and booked <> 'Y' ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());
					ps.setString(4, bean.getExamEndTime());
					ps.setString(5, bean.getSapid());
					ps.setString(6, bean.getSubject());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			sql = "Select * from exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and a.sapid = ? "
					+ " and tranStatus = '"+SEAT_RELEASED_NO_CHARGES+"'"
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and a.booked = 'Y'"
					+ " and a.subject in ("+subjectCommaSeparated+") ";


			List<ExamBookingTransactionBean> toBeUpdatedBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			final List<ExamBookingTransactionBean> toBeUpdatedBookingsList = toBeUpdatedBookings;

			//Release hold seats and marks them as booked 
			sql = getCenterSlotMappingUpdateSQL(isCorporateExamCenterStudent);

			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = toBeUpdatedBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return toBeUpdatedBookingsList.size();
				}
			});


			sql = "Select * from exam.exambookings  a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and sapid = ? "
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and booked = 'Y' order by a.examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> updateCenterForReleasedSeatsForStudents(String sapid, 
			String year, String month, final List<ExamBookingTransactionBean> bookingList) {
		try{
			String subjectCommaSeparated = getCommaSepareatedSubjects(bookingList);
			/*for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
				}
			}*/
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " tranStatus = '"+CENTER_CHANGED_BOOKED+"' , "
					+ " centerId = ?, "
					+ " examDate = ?, "
					+ " examTime = ?, "
					+ " examEndTime = ?, "
					+ " bookingCompleteTime = sysdate() "
					+ " where sapid = ? "
					+ " and subject = ? "
					+ " and booked = 'RL' "
					+ " and year = ?"
					+ " and month = ? ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());
					ps.setString(4, bean.getExamEndTime());
					ps.setString(5, bean.getSapid());
					ps.setString(6, bean.getSubject());
					ps.setString(7, bean.getYear());
					ps.setString(8, bean.getMonth());
				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

			sql = "Select * from exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and a.sapid = ? "
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and a.booked = 'Y'"
					+ " and a.year = ?"
					+ " and a.month = ?"
					+ " and a.subject in ("+subjectCommaSeparated+") ";


			List<ExamBookingTransactionBean> toBeUpdatedBookings = jdbcTemplate.query(sql, new Object[] {sapid, year, month}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			final List<ExamBookingTransactionBean> toBeUpdatedBookingsList = toBeUpdatedBookings;

			//Release hold seats and marks them as booked 
			sql = "Update exam.examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1"
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";


			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = toBeUpdatedBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return toBeUpdatedBookingsList.size();
				}
			});


			sql = "Select * from exam.exambookings  a, exam.examorder b where a.year = b.year and a.month = b.month "
					+ " and sapid = ? "
					+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
					+ " and booked = 'Y' order by a.examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> updateSeatsForOnlineTransaction(ExamBookingTransactionBean responseBean) {
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);


			String sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " description = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+ONLINE_PAYMENT_SUCCESSFUL+"' , "
					+ " bookingCompleteTime = sysdate() "


					+ " where sapid = ? "
					+ " and trackId = ?";


			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getResponseMessage(),
					responseBean.getTransactionID(),
					responseBean.getRequestID(),
					responseBean.getMerchantRefNo(),
					responseBean.getSecureHash(),
					responseBean.getRespAmount(),
					responseBean.getDescription(),
					responseBean.getResponseCode(),
					responseBean.getRespPaymentMethod(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					responseBean.getSapid(),
					responseBean.getTrackId()
			});



			sql = "Select * from exam.exambookings "
					+ " where sapid = ? "
					+ " and trackId = ? order by examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {responseBean.getSapid(), responseBean.getTrackId()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));


			//Release hold seats and marks them as booked 
			sql = "Update exam.examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1, "
					+ " onHold = COALESCE(onhold, 0) - 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";

			final List<ExamBookingTransactionBean> completeBookingsList = completeBookings;

			int [] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = completeBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return completeBookingsList.size();
				}
			});

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertOnlineTransaction(ExamBookingTransactionBean responseBean) {
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);


			String sql = "insert into exam.onlinetransactions "
					+ "( ResponseMessage ,transactionID ,requestID ,merchantRefNo ,secureHash ,respAmount , description, "
					+ " responseCode ,respPaymentMethod, isFlagged , paymentID , error , respTranDateTime ,   "
					+ "  sapid , trackId) values "
					+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getResponseMessage(),
					responseBean.getTransactionID(),
					responseBean.getRequestID(),
					responseBean.getMerchantRefNo(),
					responseBean.getSecureHash(),
					responseBean.getRespAmount(),
					responseBean.getDescription(),
					responseBean.getResponseCode(),
					responseBean.getRespPaymentMethod(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					responseBean.getSapid(),
					responseBean.getTrackId()
			});


		}catch(Exception e){
			throw e;
		}

	}

	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentWithValidity(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "select * from exam.students s, exam.examorder eo where"
					+ "		s.sapid = ?"
					+ "     and s.sem = (Select max(sem) from exam.students where sapid = ? )"
					+ " 	and s.validityendyear = eo.year"
					+ " 	and s.validityendmonth = eo.month"
					+ "		and eo.order >= (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					;

			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
		}
		return student;
	}

	@Transactional(readOnly = false)
	public int approveOnlineTransactions(String trackId, ExamBookingTransactionBean responseBean, 
				List<ExamBookingTransactionBean> bookingList) throws Exception{
		int noOfRowsUpdated = 0;
		
		String subjectCommaSeparated = getCommaSepareatedSubjects(bookingList);
		/*for (int i = 0; i < confirmedBookings.size(); i++) {
			ExamBookingTransactionBean bean = confirmedBookings.get(i);
			if(i == 0){
				subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
			}
		}*/
		
		try {		
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "";
			if("Project".equalsIgnoreCase(responseBean.getSubject()) || "Module 4 - Project".equalsIgnoreCase(responseBean.getSubject())){
				 sql = "Update exam.exambookings"
							+ " set ResponseMessage = ? ,"
							+ " transactionID = ? ,"
							+ " merchantRefNo = ? ,"
							+ " respAmount = ? ,"
							+ " responseCode = ? ,"
							+ " description = ? ,"
							+ " respPaymentMethod = ? ,"
							+ " responseMessage = ? ,"
							+ " bankName =? , "
							+ " isFlagged = ? ,"
							+ " paymentID = ? ,"
							+ " respTranDateTime = ? ,"
							+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"',"
							+ " booked = 'Y',"
							+ " lastModifiedBy = ?,"
							+ " lastModifiedDate = sysdate() "

						+ " where  trackId = ? "
						+ " and booked <> 'Y' ";
			}else{
			 sql = "Update exam.exambookings"
					+ " set ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " respAmount = ? ,"
					+ " responseCode = ? ,"
					+ " description = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " responseMessage = ? ,"
					+ " bankName =? , "
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"',"
					+ " lastModifiedBy = ?,"
					+ " lastModifiedDate = sysdate() "


				+ " where  trackId = ? "
				+ " and booked <> 'Y' ";
			}
			if(bookingList != null && bookingList.size() > 0){
				sql += " and subject not in ("+subjectCommaSeparated+") ";
			}
			
				


			noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getTransactionType(),
					responseBean.getTransactionID(),
					responseBean.getMerchantRefNo(),
					responseBean.getRespAmount(),
					responseBean.getResponseCode(),
					responseBean.getDescription(),
					responseBean.getRespPaymentMethod(),
					responseBean.getResponseMessage(),
					responseBean.getBankName(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getRespTranDateTime(),
					responseBean.getLastModifiedBy(),

					responseBean.getTrackId()
					

			});


		} catch (Exception e) {
			throw e;
		}


		return noOfRowsUpdated;
	}


	/*private String getCommaSepareatedSubjects(List<ExamBookingTransactionBean> bookingList) {
		String subjectCommaSeparated = "";
		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			if(i == 0){
				subjectCommaSeparated = "'" +bean.getSubject().replace("'","\\'") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject().replace("'","\\'") + "'";
			}
		}
		
		return subjectCommaSeparated;
	}*/
	
	@Transactional(readOnly = false)
	public int updateRefundTransactions(String trackId, ExamBookingTransactionBean responseBean) throws Exception{
		int noOfRowsUpdated = 0;
		try {		
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "Update exam.exambookings"
					+ " set ResponseMessage = ? ,"
					+ " booked = 'RF' ,"
					+ " transactionID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " respAmount = ? ,"
					+ " responseCode = '0' ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+REFUND+"' "


				+ " where  trackId = ? "
				+ " and booked <> 'Y' ";


			noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getTransactionType(),
					responseBean.getTransactionID(),
					responseBean.getMerchantRefNo(),
					responseBean.getRespAmount(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getRespTranDateTime(),
					responseBean.getTrackId()
			});


		} catch (Exception e) {
			throw e;
		}


		return noOfRowsUpdated;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfStudentAlreadyPassed(AssignmentStatusBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		try{
			if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
				return false;
			}

			String sql = "SELECT count(*) FROM exam.passfail where sapid = ? and subject = ? and isPass = 'Y'";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
					bean.getSapid(),
					bean.getSubject()
			},Integer.class);


		}catch(Exception e){
			//
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}

	}

	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getExemptStudentList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean>	studentList = new ArrayList<>();
		try{
			String sql = "select * from exam.examfeeexempt s, exam.examorder eo where"
					+ " 	s.year = eo.year"
					+ " 	and s.month = eo.month"
					+ "		and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
			//
		}
		return studentList;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ExamBookingTransactionBean> insertSeatsForFreeSubjects(String sapid, String trackId, 
			final List<ExamBookingTransactionBean> bookingsList,boolean isCorporateExamCenterStudent) {
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = " INSERT INTO exam.exambookings "
					+ "(sapid,"
					+ " subject,"
					+ " year, "
					+ " month, "
					+ " trackId, "
					+ " amount, "
					+ " tranDateTime, "
					+ " tranStatus, "
					+ " booked,"
					+ " ddno,"
					+ " bank,"
					+ " ddAmount,"
					+ " program,"
					+ " sem,"
					+ " paymentMode,"
					+ " centerId,"
					+ " examDate,"
					+ " examTime,"
					+ " examMode,"
					+ " examEndTime,"
					+ " bookingCompleteTime)"

					+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate())";


			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingsList.get(i);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getSubject());
					ps.setString(3, bean.getYear());
					ps.setString(4, bean.getMonth());
					ps.setString(5, bean.getTrackId());
					ps.setString(6, bean.getAmount());
					ps.setString(7, bean.getTranStatus());
					ps.setString(8, bean.getBooked());
					ps.setString(9, bean.getDdno());
					ps.setString(10, bean.getBank());
					ps.setString(11, bean.getDdAmount());
					ps.setString(12, bean.getProgram());
					ps.setString(13, bean.getSem());
					ps.setString(14, bean.getPaymentMode());
					ps.setString(15, bean.getCenterId());
					ps.setString(16, bean.getExamDate());
					ps.setString(17, bean.getExamTime());
					ps.setString(18, bean.getExamMode());
					ps.setString(19, bean.getExamEndTime());
				}
				public int getBatchSize() {
					return bookingsList.size();
				}
			});



			sql = "Select * from exam.exambookings "
					+ " where sapid = ? "
					+ " and trackId = ? order by examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {sapid, trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

			sql = getCenterSlotMappingUpdateSQL(isCorporateExamCenterStudent);

			final List<ExamBookingTransactionBean> completeBookingsList = completeBookings;

			int [] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = completeBookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return completeBookingsList.size();
				}
			});

			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}
	}

	@Transactional(readOnly = false)
	public void updateStudentContact(String sapid, String email, String mobile,	String altPhone) {
		String sql = "Update exam.students set "
				+ "emailId=?,"
				+ "mobile=?,"
				+ "altPhone=?"
				+ " where sapid= ? ";



		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				email,
				mobile,
				altPhone,
				sapid
		});

	}

	@Transactional(readOnly = false)
	public List<ExamBookingTransactionBean> updateSeatsForOnlineUsingSingleConnection(ExamBookingTransactionBean responseBean, 
			boolean isCorporateExamCenterStudent) throws Exception{

		Connection conn = dataSource.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try{

			conn.setAutoCommit(false);

			String sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " description = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+ONLINE_PAYMENT_SUCCESSFUL+"',"
					+ " bookingCompleteTime = sysdate(), "
					+ " bankName = ?, paymentOption=? "
 
					+ " where sapid = ? "
					+ " and trackId = ? "
					+ " and booked <> 'Y' ";


			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1,responseBean.getResponseMessage());
			pstmt.setString(2,responseBean.getTransactionID());
			pstmt.setString(3,responseBean.getRequestID());
			pstmt.setString(4,responseBean.getMerchantRefNo());
			pstmt.setString(5,responseBean.getSecureHash());
			pstmt.setString(6,responseBean.getRespAmount());
			pstmt.setString(7,responseBean.getDescription());
			pstmt.setString(8,responseBean.getResponseCode());
			pstmt.setString(9,responseBean.getRespPaymentMethod());
			pstmt.setString(10,responseBean.getIsFlagged());
			pstmt.setString(11,responseBean.getPaymentID());
			pstmt.setString(12,responseBean.getError());
			pstmt.setString(13,responseBean.getRespTranDateTime());
			pstmt.setString(14, responseBean.getBankName());
			pstmt.setString(15, responseBean.getPaymentOption());
			pstmt.setString(16,responseBean.getSapid());
			pstmt.setString(17,responseBean.getTrackId());

			int noOfRowsUpdated = pstmt.executeUpdate();


			//Reduce Hold seat and increment booked seat only if there was booking done and seats were updated.
			//Student may do a browser refresh which may double book.
			if(noOfRowsUpdated > 0){
				sql = "Select * from exam.exambookings "
						+ " where sapid = ? "
						+ " and trackId = ? order by examDate";



				pstmt = conn.prepareStatement(sql);

				List<ExamBookingTransactionBean> completeBookings = new ArrayList<ExamBookingTransactionBean>();

				pstmt.setString(1,responseBean.getSapid());
				pstmt.setString(2,responseBean.getTrackId());

				rs = pstmt.executeQuery();

				while (rs.next()) {

					String centerId = rs.getString("centerid");
					String date = rs.getString("examdate");
					String time = rs.getString("examtime");

					ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
					bean.setCenterId(centerId);
					bean.setExamDate(date);
					bean.setExamTime(time);

					completeBookings.add(bean);

				}

				sql = getCenterSlotMappingUpdateSQL(isCorporateExamCenterStudent);


				pstmt = conn.prepareStatement(sql);

				for (int i = 0; i < completeBookings.size(); i++) {
					ExamBookingTransactionBean bean = completeBookings.get(i);
					pstmt.setString(1, bean.getCenterId());
					pstmt.setString(2, bean.getExamDate());
					pstmt.setString(3, bean.getExamTime());

					pstmt.executeUpdate();
				}
			}

			conn.commit();

		}catch(Exception e){
			
			conn.rollback();
			throw e;
		}finally{
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.exambookings "
				+ " where sapid = ? "
				+ " and trackId = ? "
				+ " and booked = 'Y' order by examDate";


		List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {responseBean.getSapid(), responseBean.getTrackId()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return completeBookings;
	}
	
	@Transactional(readOnly = false)
	public int saveProjectBookingTransactionFailed(ExamBookingTransactionBean responseBean){
		//Prepare SQL
		String UPDATE_SQL="update exam.exambookings set error = ?, paymentOption = ? where sapid = ? and trackId = ? and booked <> 'Y'";
		
		//Execute query and return updated count.
		return jdbcTemplate.update(UPDATE_SQL,responseBean.getError(),responseBean.getPaymentOption(),responseBean.getSapid(),responseBean.getTrackId());
	}
	
	@Transactional(readOnly = false)
	public List<ExamBookingTransactionBean> updateSeatsForConflictUsingSingleConnection(ExamBookingTransactionBean responseBean) throws Exception{

		Connection conn = dataSource.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try{

			conn.setAutoCommit(false);

			String sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " description = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " bankName = ? ,"
					+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"',"
					+ " bookingCompleteTime = sysdate(), "
					+ " lastModifiedBy = 'AutoProjectBooking Scheduler',"
					+ " lastModifiedDate =sysdate(), paymentOption=? "


					+ " where sapid = ? "
					+ " and trackId = ?";


			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1,responseBean.getResponseMessage());
			pstmt.setString(2,responseBean.getTransactionID());
			pstmt.setString(3,responseBean.getRequestID());
			pstmt.setString(4,responseBean.getMerchantRefNo());
			pstmt.setString(5,responseBean.getSecureHash());
			pstmt.setString(6,responseBean.getRespAmount());
			pstmt.setString(7,responseBean.getDescription());
			pstmt.setString(8,responseBean.getResponseCode());
			pstmt.setString(9,responseBean.getRespPaymentMethod());
			pstmt.setString(10,responseBean.getIsFlagged());
			pstmt.setString(11,responseBean.getPaymentID());
			pstmt.setString(12,responseBean.getError());
			pstmt.setString(13,responseBean.getRespTranDateTime());
			pstmt.setString(14,responseBean.getBankName());
			pstmt.setString(15, responseBean.getPaymentOption());
			pstmt.setString(16,responseBean.getSapid());
			pstmt.setString(17,responseBean.getTrackId());

			int noOfRowsUpdated = pstmt.executeUpdate();



			sql = "Select * from exam.exambookings "
					+ " where sapid = ? "
					+ " and trackId = ? order by examDate";



			pstmt = conn.prepareStatement(sql);


			List<ExamBookingTransactionBean> completeBookings = new ArrayList<ExamBookingTransactionBean>();

			pstmt.setString(1,responseBean.getSapid());
			pstmt.setString(2,responseBean.getTrackId());

			rs = pstmt.executeQuery();

			while (rs.next()) {

				String centerId = rs.getString("centerid");
				String date = rs.getString("examdate");
				String time = rs.getString("examtime");

				ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
				bean.setCenterId(centerId);
				bean.setExamDate(date);
				bean.setExamTime(time);

				completeBookings.add(bean);

			}


			//Release hold seats and marks them as booked 
			sql = "Update exam.examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1 "
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";

			pstmt = conn.prepareStatement(sql);

			for (int i = 0; i < completeBookings.size(); i++) {
				ExamBookingTransactionBean bean = completeBookings.get(i);
				pstmt.setString(1, bean.getCenterId());
				pstmt.setString(2, bean.getExamDate());
				pstmt.setString(3, bean.getExamTime());

				pstmt.executeUpdate();
			}

			conn.commit();

		}catch(Exception e){
			
			conn.rollback();
			throw e;
		}finally{
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.exambookings "
				+ " where sapid = ? "
				+ " and trackId = ? order by examDate";


		List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {responseBean.getSapid(), responseBean.getTrackId()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return completeBookings;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getBookingsForAttendanceSheet(	ExamBookingTransactionBean bean) throws ParseException {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT * FROM  exam.examorder b, exam.students c, exam.examcenter ec, exam.exambookings a " +
				" where a.year = b.year and a.month = b.month "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') " +
				" and a.sapid = c.sapid " +
				" and a.centerId = ec.centerId " +
				" and a.subject NOT IN ('Project', 'Module 4 - Project') ";


		if( bean.getSubject() != null &&   !("".equals(bean.getSubject()))){
			sql = sql + " and a.subject = ? ";
			parameters.add(bean.getSubject());
		}
		if( bean.getProgram() != null &&   !("".equals(bean.getProgram()))){
			sql = sql + " and a.program = ? ";
			parameters.add(bean.getProgram());
		}
		if( bean.getSem() != null &&   !("".equals(bean.getSem()))){
			sql = sql + " and a.sem = ? ";
			parameters.add(bean.getSem());
		}
		if( bean.getYear() != null &&   !("".equals(bean.getYear()))){
			sql = sql + " and a.year = ? ";
			parameters.add(bean.getYear());
		}
		if( bean.getMonth() != null &&   !("".equals(bean.getMonth()))){
			sql = sql + " and a.month = ? ";
			parameters.add(bean.getMonth());
		}
		if( bean.getCenterId() != null &&   !("".equals(bean.getCenterId()))){
			sql = sql + " and a.centerId = ? ";
			parameters.add(bean.getCenterId());
		}



		sql = sql + " group by a.sapid, a.subject  order by c.lastname, c.firstname asc ";

		Object[] args = parameters.toArray();

		ArrayList<ExamBookingTransactionBean> completeBookings = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, 
				args, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		if(completeBookings != null && completeBookings.size() > 0){
			//Below information needed on Attendance Sheet PDF
			bean.setExamDate(completeBookings.get(0).getExamDate());
			bean.setExamTime(completeBookings.get(0).getExamTime());
			bean.setExamEndTime(completeBookings.get(0).getExamEndTime());
			bean.setExamCenterName(completeBookings.get(0).getExamCenterName());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			Date formattedDate = formatter.parse(bean.getExamDate());
			String examDate = dateFormatter.format(formattedDate);
			bean.setExamDate(examDate);

		}
		return completeBookings;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateExemptFeeList(final List<StudentMarksBean> exemptFeeList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < exemptFeeList.size(); i++) {
			try{
				StudentMarksBean bean = exemptFeeList.get(i);
				upsertExemptionStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}

	private void upsertExemptionStatus(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.examfeeexempt "
				+ "(sapid, year, month, sem, createdBy, createdDate, lastModifiedBy , lastModifiedDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,sysdate(),?,sysdate())"
				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    sem = ?,"
				+ "	    lastModifiedBy = ?, "
				+ "	    lastModifiedDate = sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String sem = bean.getSem();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();
	
		jdbcTemplate.update(sql, new Object[] { 
				sapid,
				year,
				month,
				sem,
				createdBy,
				lastModifiedBy,
				sapid,
				year,
				month,
				sem,
				lastModifiedBy		
                
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateExemptFeeSubjectsList(final List<StudentMarksBean> exemptFeeList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < exemptFeeList.size(); i++) {
			try{
				StudentMarksBean bean = exemptFeeList.get(i);
				upsertExemptionSubjectsStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;
	}

	private void upsertExemptionSubjectsStatus(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.examfeeexemptsubject "
				+ "(sapid, year, month, subject, createdBy, createdDate, lastModifiedBy ,lastModifiedDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    subject = ?,"
				+ "     lastModifiedBy=? , "
				+ "     lastModifiedDate= sysdate() ";

		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String subject = bean.getSubject();
		String createdBy = bean.getCreatedBy();
		String lastModifiedBy = bean.getLastModifiedBy();

		jdbcTemplate.update(sql, new Object[] { 
				sapid,
				year,
				month,
				subject,
				createdBy,
				lastModifiedBy,
				sapid,
				year,
				month,
				subject,
				lastModifiedBy
		});

	}

	@Async
	@Transactional(readOnly = false)
	public void saveHallTicketDownloaded(final String sapid, final ArrayList<ExamBookingTransactionBean> subjectsBooked) {

		String sql = "Update exam.exambookings"
				+ " set htDownloaded = 'Y', htDownloadedDate=sysdate() "
				+ " where sapid = ? "
				+ " and year = ? "
				+ " and month = ?"
				+ " and subject = ?"
				+ " and booked = 'Y' ";

		int[] results = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				ExamBookingTransactionBean bean = subjectsBooked.get(i);
				ps.setString(1, sapid);
				ps.setString(2, bean.getYear());
				ps.setString(3, bean.getMonth());
				ps.setString(4, bean.getSubject());


			}
			public int getBatchSize() {
				return subjectsBooked.size();
			}
		});

	}

	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getAllvalidStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean> validStudents = null;
		try{
			String sql = "select * from exam.students s, exam.examorder eo where"
					+ "		s.validityendyear = eo.year"
					+ " 	and s.validityendmonth = eo.month"
					+ "		and eo.order >= (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					;

			validStudents = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
			
		}
		return validStudents;
	}

	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getReleasedExamBookingsForStudent(ExamBookingTransactionBean examBooking) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM   exam.students s, exam.examcenter b, exam.exambookings a where a.centerId = b.centerId and a.year = ? "
				+ " and a.month = ? and a.booked = 'RL' and a.sapid = ? "
				+ " and a.sapid = s.sapid"
				+ " order by a.centerId, a.examDate,  a.examTime, a.sem, a.program, a.subject asc";

		List<ExamBookingTransactionBean> bookings = jdbcTemplate.query(sql, new Object[]{
				examBooking.getYear(),
				examBooking.getMonth(),
				examBooking.getSapid()
		}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		return bookings;
	}

	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getMarksCheckingSheet(ExamBookingTransactionBean bean) throws ParseException{
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql = "SELECT a.*, ec.*, c.sapid, c.firstName, c.lastName, a.program, m.writenscore "
				+ "  FROM  exam.examorder b, exam.students c, exam.examcenter ec, exam.exambookings a , exam.marks m "
				+ " where a.year = b.year and a.month = b.month  and m.year = b.year and m.month = b.month "
				+ " and c.sapid = m.sapid  and a.subject = m.subject"
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')  "
				+ " and a.sapid = c.sapid and a.centerId = ec.centerId   ";


		if( bean.getSubject() != null &&   !("".equals(bean.getSubject()))){
			sql = sql + " and a.subject = ? ";
			parameters.add(bean.getSubject());
		}
		if( bean.getProgram() != null &&   !("".equals(bean.getProgram()))){
			sql = sql + " and a.program = ? ";
			parameters.add(bean.getProgram());
		}
		if( bean.getSem() != null &&   !("".equals(bean.getSem()))){
			sql = sql + " and a.sem = ? ";
			parameters.add(bean.getSem());
		}
		if( bean.getYear() != null &&   !("".equals(bean.getYear()))){
			sql = sql + " and a.year = ? ";
			parameters.add(bean.getYear());
		}
		if( bean.getMonth() != null &&   !("".equals(bean.getMonth()))){
			sql = sql + " and a.month = ? ";
			parameters.add(bean.getMonth());
		}
		if( bean.getCenterId() != null &&   !("".equals(bean.getCenterId()))){
			sql = sql + " and a.centerId = ? ";
			parameters.add(bean.getCenterId());
		}



		sql = sql + " group by a.sapid, a.subject  order by c.lastname, c.firstname asc ";

		Object[] args = parameters.toArray();

		ArrayList<ExamBookingTransactionBean> marksList = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, 
				args, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		if(marksList != null && marksList.size() > 0){
			//Below information needed on Attendance Sheet PDF
			bean.setExamDate(marksList.get(0).getExamDate());
			bean.setExamTime(marksList.get(0).getExamTime());
			bean.setExamEndTime(marksList.get(0).getExamEndTime());
			bean.setExamCenterName(marksList.get(0).getExamCenterName());

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			Date formattedDate = formatter.parse(bean.getExamDate());
			String examDate = dateFormatter.format(formattedDate);
			bean.setExamDate(examDate);

		}
		return marksList;
	}

	@Transactional(readOnly = true)
	public boolean isConfigurationLive(String configurationType) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select count(*) from exam.configuration where configurationType = ? and startTime <= sysdate() and endTime >= sysdate() ";
		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{configurationType},Integer.class);
		} catch (Exception e) {
			
		}

		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}

	//this method use to check extended exam registration date for allowing to book seat for particular time even after exam registration is closed
	@Transactional(readOnly = true)
	public boolean isExtendedExamRegistrationConfigurationLive(String configurationType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.configuration where configurationType = ? and extendStartTime <= sysdate() and extendEndTime >= sysdate() ";

		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{configurationType},Integer.class);
		} catch (Exception e) {
			//
		}

		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}


	//This method is used for exam booking conflict scheduler to run it even after 1 days post end of rgistration window
	@Transactional(readOnly = true)
	public boolean isConfigurationLivePost1Day(String configurationType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.configuration where configurationType = ? and startTime <= sysdate() "
				+ "and ("
				+ " DATE_ADD(endTime,INTERVAL 3 DAY) >= sysdate() "
				+ " OR DATE_ADD(extendEndTime,INTERVAL 3 DAY) >= sysdate() "
				+ ")";

		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{configurationType},Integer.class);
		} catch (Exception e) {
			//
		}

		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}

	
	@Transactional(readOnly = true)
	public ArrayList<String> getIndividualFreeSubjects(StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String>	subjectList = new ArrayList<>();

		try{
			/*String sql = "select s.subject from exam.examfeeexemptsubject s, exam.examorder eo where"
					+ "		s.sapid = ? "
					+ " 	and s.year = eo.year"
					+ " 	and s.month = eo.month"
					+ "		and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";*/

			String year = getLiveExamYear();
			String month = getLiveExamMonth();
			String sql = "select subject from exam.examfeeexemptsubject  where"
					+ "		sapid = ? "
					+ " 	and year = ?"
					+ " 	and month = ?";

			subjectList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{student.getSapid(), year, month}, new SingleColumnRowMapper(String.class));

		}catch(Exception e){
			
		}
		return subjectList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ArrayList<String>> getStudentFreeSubjectsMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, ArrayList<String>>	studentFreeSubjectsMap = new HashMap<String, ArrayList<String>>();

		try{
			/*String sql = "select s.subject from exam.examfeeexemptsubject s, exam.examorder eo where"
					+ "		s.sapid = ? "
					+ " 	and s.year = eo.year"
					+ " 	and s.month = eo.month"
					+ "		and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";*/

			String year = getLiveExamYear();
			String month = getLiveExamMonth();
			String sql = "select * from exam.examfeeexemptsubject  where"
					+ " 	year = ?"
					+ " 	and month = ?";

			ArrayList<ExamBookingTransactionBean> feeExepmtStudents = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{ year, month}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			for (ExamBookingTransactionBean bean : feeExepmtStudents) {
				if(studentFreeSubjectsMap.containsKey(bean.getSapid())){
					studentFreeSubjectsMap.get(bean.getSapid()).add(bean.getSubject());
				}else{
					ArrayList<String> subjects = new ArrayList<String>();
					subjects.add(bean.getSubject());
					studentFreeSubjectsMap.put(bean.getSapid(), subjects);
				}
				
			}

		}catch(Exception e){
			
		}
		return studentFreeSubjectsMap;
	}




	@Transactional(readOnly = true)
	public String getLastSemRegisteredBeforeResitExam(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sem = null;
		try{
			String sql = "SELECT max(a.sem) FROM exam.registration a, exam.examorder b where a.year = b.year and a.month = b.acadMonth "
					+ " and sapid = ? and  b.order < (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";



			sem = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		}catch(Exception e){
			//
			return null;
		}
		return sem;
	}

	@Transactional(readOnly = true)
	public boolean checkIfProjectIsCleared(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		try{

			String sql = "SELECT count(*) FROM exam.passfail where sapid = ? and subject IN ('Project', 'Module 4 - Project') and isPass = 'Y'";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[] { sapid },Integer.class);


		}catch(Exception e){
			//
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	
	@Transactional(readOnly = true)
	public double getExamOrderFromAcadMonthAndYear(String acadMonth,String acadYear)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		try{

			String sql = "SELECT examorder.order FROM exam.examorder where acadMonth=? and year=? ";

			examOrder = (double) jdbcTemplate.queryForObject(sql, new Object[]{acadMonth,acadYear},Integer.class);


		}catch(Exception e){
			
		}

		return examOrder;
	}
	
	@Transactional(readOnly = true)
	public double getExamOrderFromExamMonthAndYear(String examMonth,String examYear)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		try{

			String sql = "SELECT examorder.order FROM exam.examorder where month=? and year=? ";

			examOrder = (double) jdbcTemplate.queryForObject(sql, new Object[]{examMonth,examYear},Integer.class);


		}catch(Exception e){
			
		}

		return examOrder;
	}

	@Transactional(readOnly = true)
	public List<String> getBlockedSapids() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "SELECT subject FROM exam.assignmentStatus a, exam.examorder b where a.examYear = b.year and a.examMonth = b.month "
				+ " and sapid = ? and submitted = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')  "
				+ " order by a.subject asc";*/

		String sql = "SELECT distinct sapid FROM exam.blocked_hallticket where  "
				+ "  blocked = 'Y'  ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(String.class));
		return subjectList;

	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ProgramExamBean> getProgramDetailsMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);


		HashMap<String,ProgramExamBean> resultMap = new HashMap<String,ProgramExamBean>();

		String sql = "SELECT * from exam.programs ";
		ArrayList<ProgramExamBean> programList = (ArrayList<ProgramExamBean>)jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(ProgramExamBean.class));

		for(ProgramExamBean program : programList){
			resultMap.put(program.getConsumerProgramStructureId(), program);
			//resultMap.put(program.getProgram()+"-"+program.getProgramStructure(), program);

		}

		return resultMap;
	}
	
	@Transactional(readOnly = false)
	public void updateConfilctTransactionDetails(final ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings,
			final ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings) throws ParseException {

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			String sql = "INSERT INTO exam.exambookings_conflict_transactions "
					+ " (trackid, sapid,year,month,amount,gatewayAmount,emailId,mobile,transactionStartTime,transactionEndTime, "
					+ " transactionTime, action, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
					+ " ON DUPLICATE KEY UPDATE "
					+ " amount = ?,"
					+ " gatewayAmount = ?,"
					+ " action = ?,"
					+ " lastModifiedDate = sysdate(),"
					+ " lastModifiedBy = ? ";

			try {
			//Upsert successfulButCenterNotAvailableExamBookings
			int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean transactionBean = successfulButCenterNotAvailableExamBookings.get(i);
					String timeDiff = "";
					try {
						String tranTime = transactionBean.getTranDateTime();
						String respTranTime = transactionBean.getRespTranDateTime();
						Date startTime = df.parse(tranTime.substring(0, 19));
						Date endTime = df.parse(respTranTime);

						long diff = endTime.getTime() - startTime.getTime();
						long diffSeconds = diff / 1000;         
						long diffMinutes = diff / (60 * 1000);  
						diffSeconds = diffSeconds - (diffMinutes * 60);
						timeDiff = diffMinutes +":"+diffSeconds;
					} catch (Exception e) {
						
					}

					ps.setString(1,transactionBean.getTrackId());
					ps.setString(2,transactionBean.getSapid());
					ps.setString(3,transactionBean.getYear());
					ps.setString(4,transactionBean.getMonth());
					ps.setString(5,transactionBean.getAmount());
					ps.setString(6,transactionBean.getRespAmount());
					ps.setString(7,transactionBean.getEmailId());
					ps.setString(8,transactionBean.getMobile());
					ps.setString(9,transactionBean.getTranDateTime());
					ps.setString(10,transactionBean.getRespTranDateTime());
					ps.setString(11,timeDiff);

					ps.setString(12,transactionBean.getAction());
					ps.setString(13,"System");
					ps.setString(14,"System");
					ps.setString(15,transactionBean.getAmount());
					ps.setString(16,transactionBean.getRespAmount());
					ps.setString(17,transactionBean.getAction());
					ps.setString(18,"System");


				}

				@Override
				public int getBatchSize() {
					return successfulButCenterNotAvailableExamBookings.size();
				}
			});
			}catch(Exception e){
				
				try {
					//Upsert successfulButCenterNotAvailableExamBookings
					int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						@Override
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ExamBookingTransactionBean transactionBean = successfulButCenterNotAvailableExamBookings.get(i);
							String timeDiff = "";
							try {
								String tranTime = transactionBean.getTranDateTime();
								String respTranTime = transactionBean.getRespTranDateTime();
								Date startTime = df.parse(tranTime.substring(0, 19));
								Date endTime = df.parse(respTranTime);

								long diff = endTime.getTime() - startTime.getTime();
								long diffSeconds = diff / 1000;         
								long diffMinutes = diff / (60 * 1000);  
								diffSeconds = diffSeconds - (diffMinutes * 60);
								timeDiff = diffMinutes +":"+diffSeconds;
							} catch (Exception e) {
								
							}

							ps.setString(1,transactionBean.getTrackId());
							ps.setString(2,transactionBean.getSapid());
							ps.setString(3,transactionBean.getYear());
							ps.setString(4,transactionBean.getMonth());
							ps.setString(5,transactionBean.getAmount());
							ps.setString(6,transactionBean.getRespAmount());
							ps.setString(7,transactionBean.getEmailId());
							ps.setString(8,transactionBean.getMobile());
							ps.setString(9,transactionBean.getTranDateTime());
							ps.setString(10,transactionBean.getRespTranDateTime());
							ps.setString(11,timeDiff);

							ps.setString(12,transactionBean.getAction());
							ps.setString(13,"System");
							ps.setString(14,"System");
							ps.setString(15,transactionBean.getAmount());
							ps.setString(16,transactionBean.getRespAmount());
							ps.setString(17,transactionBean.getAction());
							ps.setString(18,"System");


						}

						@Override
						public int getBatchSize() {
							return successfulButCenterNotAvailableExamBookings.size();
						}
					});}catch(Exception e2){
					}
			}
			try{
			//Upsert successfulButAlreadyBookedExamBookings
				int[] result  = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean transactionBean = successfulButAlreadyBookedExamBookings.get(i);
					String timeDiff = "";
					try {
						String tranTime = transactionBean.getTranDateTime();
						String respTranTime = transactionBean.getRespTranDateTime();
						Date startTime = df.parse(tranTime.substring(0, 19));
						Date endTime = df.parse(respTranTime);

						long diff = endTime.getTime() - startTime.getTime();
						long diffSeconds = diff / 1000;         
						long diffMinutes = diff / (60 * 1000);  
						diffSeconds = diffSeconds - (diffMinutes * 60);
						timeDiff = diffMinutes +":"+diffSeconds;
					} catch (Exception e) {
						
					}

					ps.setString(1,transactionBean.getTrackId());
					ps.setString(2,transactionBean.getSapid());
					ps.setString(3,transactionBean.getYear());
					ps.setString(4,transactionBean.getMonth());
					ps.setString(5,transactionBean.getAmount());
					ps.setString(6,transactionBean.getRespAmount());
					ps.setString(7,transactionBean.getEmailId());
					ps.setString(8,transactionBean.getMobile());
					ps.setString(9,transactionBean.getTranDateTime());
					ps.setString(10,transactionBean.getRespTranDateTime());
					ps.setString(11,timeDiff);

					ps.setString(12,transactionBean.getAction());
					ps.setString(13,"System");
					ps.setString(14,"System");
					ps.setString(15,transactionBean.getAmount());
					ps.setString(16,transactionBean.getRespAmount());
					ps.setString(17,transactionBean.getAction());
					ps.setString(18,"System");


				}

				@Override
				public int getBatchSize() {
					return successfulButAlreadyBookedExamBookings.size();
				}
			});

		} catch (Exception e) {
			try{
				//Upsert successfulButAlreadyBookedExamBookings
					int[] result  = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ExamBookingTransactionBean transactionBean = successfulButAlreadyBookedExamBookings.get(i);
						String timeDiff = "";
						try {
							String tranTime = transactionBean.getTranDateTime();
							String respTranTime = transactionBean.getRespTranDateTime();
							Date startTime = df.parse(tranTime.substring(0, 19));
							Date endTime = df.parse(respTranTime);

							long diff = endTime.getTime() - startTime.getTime();
							long diffSeconds = diff / 1000;         
							long diffMinutes = diff / (60 * 1000);  
							diffSeconds = diffSeconds - (diffMinutes * 60);
							timeDiff = diffMinutes +":"+diffSeconds;
						} catch (Exception e) {
							
						}

						ps.setString(1,transactionBean.getTrackId());
						ps.setString(2,transactionBean.getSapid());
						ps.setString(3,transactionBean.getYear());
						ps.setString(4,transactionBean.getMonth());
						ps.setString(5,transactionBean.getAmount());
						ps.setString(6,transactionBean.getRespAmount());
						ps.setString(7,transactionBean.getEmailId());
						ps.setString(8,transactionBean.getMobile());
						ps.setString(9,transactionBean.getTranDateTime());
						ps.setString(10,transactionBean.getRespTranDateTime());
						ps.setString(11,timeDiff);

						ps.setString(12,transactionBean.getAction());
						ps.setString(13,"System");
						ps.setString(14,"System");
						ps.setString(15,transactionBean.getAmount());
						ps.setString(16,transactionBean.getRespAmount());
						ps.setString(17,transactionBean.getAction());
						ps.setString(18,"System");


					}

					@Override
					public int getBatchSize() {
						return successfulButAlreadyBookedExamBookings.size();
					}
				});

			} catch (Exception e2) {
			}
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamConflictTransactionBean> getAllConflictTransactions(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM exam.exambookings_conflict_transactions  "
				+ " where  year = ? and month = ?"
				+ " order by  trackId asc";


		ArrayList<ExamConflictTransactionBean> transactionList = (ArrayList<ExamConflictTransactionBean>) jdbcTemplate.query(sql, new Object[]{year,month}, new BeanPropertyRowMapper(ExamConflictTransactionBean.class));
		
		return transactionList;
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getApr2020SuccessBooking(String sapid){
		String sql = "select * from `exam`.`exam_booking_apr_2020` where sapid = ? and booked='Y'";
		return (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
	}
	
	@Transactional(readOnly = false)
	public boolean insertIntoRefund(ExamBookingTransactionBean examBookingTransactionBean) {
		try {
			String sql = "insert into `exam`.`refund_request`(sapid,trackId,amount,description) values(?,?,?,?)";
			int status = jdbcTemplate.update(sql,new Object[] {examBookingTransactionBean.getSapid(),examBookingTransactionBean.getTrackId(),examBookingTransactionBean.getAmount(),examBookingTransactionBean.getDescription()});
			if(status == 1) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean insertIntoExcept(ExamBookingTransactionBean examBookingTransactionBean) {
		try {
			String sql = "insert into `exam`.`examfeeexemptsubject`(sapid,year,month,subject,createdBy,createdDate,lastModifiedBy,lastModifiedDate) values(?,?,?,?,?,sysdate(),?,sysdate())";
			int status = jdbcTemplate.update(sql,new Object[] {examBookingTransactionBean.getSapid(),examBookingTransactionBean.getYear(),examBookingTransactionBean.getMonth(),examBookingTransactionBean.getSubject(),examBookingTransactionBean.getCreatedBy(),examBookingTransactionBean.getLastModifiedBy()});
			if(status == 1) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFailSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getNotPassedSubjectsBasedOnSapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select ps.subject from exam.registration er,exam.program_subject ps, exam.students s "
				+" where er.sapid = ? " 
				+" and s.sapid = er.sapid "
				+" and er.program = ps.program "
				+" and er.sem = ps.sem "
				+" and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+" and ps.subject not in (select subject from exam.passfail where sapid = ?)";
		ArrayList<String> notPassedSubjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid,sapid}, new SingleColumnRowMapper(String.class));
		return notPassedSubjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentMarksBean> getAllRegistrationsFromSAPID(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.registration where sapid = ? order by sem ";
		ArrayList<StudentMarksBean> getAllRegistrationsFromSAPID = (ArrayList<StudentMarksBean>)jdbcTemplate.query(sql,new Object[]{sapid},new BeanPropertyRowMapper(StudentMarksBean.class));
		return getAllRegistrationsFromSAPID;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<ExamOrderExamBean> getLiveFlagDetails(){
		List<ExamOrderExamBean> liveFlagList = new ArrayList<ExamOrderExamBean>();

		final String sql = " Select * from exam.examorder order by examorder.order ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		liveFlagList = (ArrayList<ExamOrderExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));

		return liveFlagList;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfStudentApplicableForSubmission(String sapid,String year,String month,String sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select r.* from exam.registration r "
				+ " inner join exam.students s on s.sapid = r.sapid"
				+ " where "
				+ " r.sapid = ?"
				+ " and r.sem = ? "
				+ " and STR_TO_DATE(concat(r.year,'-',r.month,'-01'), '%Y-%b-%d') <= DATE_ADD(STR_TO_DATE(concat(?,'-',?,'-01'), '%Y-%b-%d'), INTERVAL -5 MONTH)"
				+ " and STR_TO_DATE(concat(s.validityEndYear,'-',s.validityEndMonth,'-31'), '%Y-%b-%d') > sysdate()"; // added to stop project submission after validity end
		boolean isApplicable = false;
		List<StudentMarksBean> registrationList = new ArrayList<>();
		HashMap<String,StudentMarksBean> registrationMap = new HashMap<String,StudentMarksBean>();
		try{
			registrationList = jdbcTemplate.query(sql, new Object[]{sapid,sem,year,month},new BeanPropertyRowMapper(StudentMarksBean.class));
			if(registrationList.isEmpty()){
				isApplicable = false;
			}else{
				isApplicable = true;
			}
			
		}catch(Exception e){
			
		}
		return isApplicable;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedProjectBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and subject IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrReleasedProjectExamBookings(String sapid,String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String month = getLiveProjectExamMonth();
		String year = getLiveProjectExamYear();
		String sql = "SELECT * FROM exam.exambookings  where year = ? and month = ? and trackId=? "
				+ " and sapid = ? and (booked = 'Y' or booked = 'RL') ";
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{year, month, trackId,sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProjectBookingforCurrentLiveExam(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getProjectExemptStudentList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean>	studentList = new ArrayList<>();
		try{
			String sql = "select * from exam.examfeeexempt s, exam.examorder eo where"
					+ " 	s.year = eo.year"
					+ " 	and s.month = eo.month"
					+ "		and eo.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y')";

			studentList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
			//
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ArrayList<String>> getStudentFreeProjectMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, ArrayList<String>>	studentFreeSubjectsMap = new HashMap<String, ArrayList<String>>();

		try{
			String year = getLiveProjectExamYear();
			String month = getLiveProjectExamMonth();
			String sql = "select * from exam.examfeeexemptsubject  where"
					+ " 	year = ?"
					+ " 	and month = ?";
			ArrayList<ExamBookingTransactionBean> feeExepmtStudents = (ArrayList<ExamBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{ year, month}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			for (ExamBookingTransactionBean bean : feeExepmtStudents) {
				if(studentFreeSubjectsMap.containsKey(bean.getSapid())){
					studentFreeSubjectsMap.get(bean.getSapid()).add(bean.getSubject());
				}else{
					ArrayList<String> subjects = new ArrayList<String>();
					subjects.add(bean.getSubject());
					studentFreeSubjectsMap.put(bean.getSapid(), subjects);
				}
			}
		}catch(Exception e){
			
		}
		return studentFreeSubjectsMap;
	}
	
	@Transactional(readOnly = true)
	public boolean isResultLiveForLastProjectSubmissionCycle(){
		String sql = " select * from exam.examorder eo where eo.order = "
				+ "(select max(examorder.order)-0.5 from exam.examorder where  projectSubmissionLive = 'Y')";
		ExamOrderExamBean examOrder = (ExamOrderExamBean) jdbcTemplate.queryForObject(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		if("Y".equals(examOrder.getLive())){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllUnSuccessfulProjectBookings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b, exam.students s where a.year = b.year and a.month = b.month "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800) "
				+ " and a.booked <> 'Y' "
				+ " and a.booked <> 'RL' "
				+ " and a.booked <> 'RF' "
				+ " and a.booked <> 'CL' "
				+ " and a.tranStatus not in ('" + CANCELLATION_WITH_REFUND + "','" + CANCELLATION_WITHOUT_REFUND + "') "
				//+ " and a.tranStatus <> '" + TRANSACTION_FAILED + "'"
				//+ " and a.tranStatus <> '" + EXPIRED + "'"
				+ " and a.tranStatus <> '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "'"
				+ " and a.sapid = s.sapid "
				+ " and a.subject IN ('Project', 'Module 4 - Project')"
				+ " and a.paymentMode = 'Online'"
				+ " and DATEDIFF( sysdate(), tranDateTime) <= 2" // to avoid expired transactions older than 2 day
				//+ " and DATEDIFF(tranDateTime, sysdate()) >= 0"
				+ " group by a.trackId "
				+ " order by a.tranDateTime asc";
		

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getProjectBookedForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b "
				+ " where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and subject = 'Project' and a.booked = 'Y' "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.subject asc";

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getModuleProjectBookedForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b "
				+ " where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and subject = 'Module 4 - Project' and a.booked = 'Y' "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.subject asc";

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedProjectBookings() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b "
				+ " where a.year = b.year and a.month = b.month and subject IN ('Project', 'Module 4 - Project') "
				+ " and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.subject,a.sapid asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedDoubleBooking(ArrayList<String> sapidList) {
		String sapid = StringUtils.join(sapidList, ',');
		//String sapid = getCommaSepareatedSapid(sapidList);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* , s.firstName, s.lastName, s.mobile, s.altPhone, s.emailId "
				+ " FROM exam.exambookings a, exam.examorder b, exam.students s"
				+ " where "
				+ " a.year = b.year "
				+ " and a.month = b.month"
				+ " and a.sapid = s.sapid "
				+ " and a.sapid in ("+sapid+")"
				+ " and a.booked = 'Y' "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.sapid,a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getAllConfirmedDoubleProjectBookings(ArrayList<String> sapidList) {
		String sapid = StringUtils.join(sapidList, ',');
		//String sapid = getCommaSepareatedSapid(sapidList);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* , s.firstName, s.lastName, s.mobile, s.altPhone, s.emailId"
				+ " FROM exam.exambookings a, exam.examorder b, exam.students s"
				+ " where"
				+ " a.year = b.year "
				+ "	and a.month = b.month "
				+ "	and subject IN ('Project', 'Module 4 - Project') "
				+ " and a.sapid = s.sapid "
				+ " and  sapid in ("+sapid+")  "
				+ " and a.booked = 'Y' "
				+ " and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y') "
				+ " order by a.sapid,a.subject asc";


		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}


	
//	public String getConsumerProId(String program,String programStructure,String consumerType) {
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = "select c_p_s.id from exam.consumer_program_structure as c_p_s "
//				+ "left join exam.program as p on p.id = c_p_s.programId "
//				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
//				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId where"
//				+ " p.code = ? and "
//				+ "p_s.program_structure = ? and "
//				+ "c_t.name = ?";
//		String consumerProgramStructureId = (String)jdbcTemplate.queryForObject(sql,new Object[] {program,programStructure,consumerType},String.class);
//		return consumerProgramStructureId;
//	}
	
	@Transactional(readOnly = true)
	public ArrayList<Demoexam_keysBean> getDemoExamKeysForSubjects(List<String> subjects){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectsList ="'" + StringUtils.join(subjects, "','")+ "'";
		String sql = "SELECT * FROM exam.demoexam_keys where subject IN("+subjectsList +") ";

		ArrayList<Demoexam_keysBean> subjectLinkMap = (ArrayList<Demoexam_keysBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(Demoexam_keysBean.class));
		
		return subjectLinkMap;
	}
	
	@Transactional(readOnly = false)
	public void assignPass(String sapid,String password,String month,String year) {


        jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "";
        try {
           sql = "update exam.exambookings e " + 
                " set e.password=? " + 
                " where e.sapid=? " + 
                " and e.year=? " + 
                " and e.month=? " + 
                " and e.booked = 'Y'";

          jdbcTemplate.update(sql,new Object[] {
        		  password,
        		  sapid,
        		  year,
        		  month});
          
        }
        
        catch(Exception e) {
                        
                        
        }
        
}
	
	
	/*public String getCommaSepareatedSapid(ArrayList<String> sapidList) {
		String sapidCommaSeparated = "";
		for (int i = 0; i < sapidList.size(); i++) {
			String sapid = sapidList.get(i);
			if (i == 0) {
				sapidCommaSeparated = "'" + sapid.replace("'", "\\'") + "'";
			} else {
				sapidCommaSeparated = sapidCommaSeparated + ", '" + sapid.replace("'", "\\'") + "'";
			}
		}

		return sapidCommaSeparated;
	}*/
	
	@Transactional(readOnly = false)
	public void updateProjectConfilctTransactionDetails(final ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings) throws ParseException {

		

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			String sql = "INSERT INTO exam.exambookings_conflict_transactions "
					+ " (trackid, sapid,year,month,amount,gatewayAmount,emailId,mobile,transactionStartTime,transactionEndTime, "
					+ " transactionTime, action, createdBy, createdDate, lastModifiedBy, lastModifiedDate) "
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate()) "
					+ " ON DUPLICATE KEY UPDATE "
					+ " amount = ?,"
					+ " gatewayAmount = ?,"
					+ " action = ?,"
					+ " lastModifiedDate = sysdate(),"
					+ " lastModifiedBy = ? ";

			try {
			//Upsert successfulButAlreadyBookedExamBookings
			int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean transactionBean = successfulButAlreadyBookedExamBookings.get(i);
					String timeDiff = "";
					try {
						String tranTime = transactionBean.getTranDateTime();
						String respTranTime = transactionBean.getRespTranDateTime();
						Date startTime = df.parse(tranTime.substring(0, 19));
						Date endTime = df.parse(respTranTime);

						long diff = endTime.getTime() - startTime.getTime();
						long diffSeconds = diff / 1000;         
						long diffMinutes = diff / (60 * 1000);  
						diffSeconds = diffSeconds - (diffMinutes * 60);
						timeDiff = diffMinutes +":"+diffSeconds;
					} catch (Exception e) {
						
					}

					ps.setString(1,transactionBean.getTrackId());
					ps.setString(2,transactionBean.getSapid());
					ps.setString(3,transactionBean.getYear());
					ps.setString(4,transactionBean.getMonth());
					ps.setString(5,transactionBean.getAmount());
					ps.setString(6,transactionBean.getRespAmount());
					ps.setString(7,transactionBean.getEmailId());
					ps.setString(8,transactionBean.getMobile());
					ps.setString(9,transactionBean.getTranDateTime());
					ps.setString(10,transactionBean.getRespTranDateTime());
					ps.setString(11,timeDiff);

					ps.setString(12,transactionBean.getAction());
					ps.setString(13,"System");
					ps.setString(14,"System");
					ps.setString(15,transactionBean.getAmount());
					ps.setString(16,transactionBean.getRespAmount());
					ps.setString(17,transactionBean.getAction());
					ps.setString(18,"System");


				}

				@Override
				public int getBatchSize() {
					return successfulButAlreadyBookedExamBookings.size();
				}
			});

		

		} catch (Exception e) {
			try{
			
			//Upsert successfulButAlreadyBookedExamBookings
			int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean transactionBean = successfulButAlreadyBookedExamBookings.get(i);
					String timeDiff = "";
					try {
						String tranTime = transactionBean.getTranDateTime();
						String respTranTime = transactionBean.getRespTranDateTime();
						Date startTime = df.parse(tranTime.substring(0, 19));
						Date endTime = df.parse(respTranTime);

						long diff = endTime.getTime() - startTime.getTime();
						long diffSeconds = diff / 1000;         
						long diffMinutes = diff / (60 * 1000);  
						diffSeconds = diffSeconds - (diffMinutes * 60);
						timeDiff = diffMinutes +":"+diffSeconds;
					} catch (Exception e) {
						
					}

					ps.setString(1,transactionBean.getTrackId());
					ps.setString(2,transactionBean.getSapid());
					ps.setString(3,transactionBean.getYear());
					ps.setString(4,transactionBean.getMonth());
					ps.setString(5,transactionBean.getAmount());
					ps.setString(6,transactionBean.getRespAmount());
					ps.setString(7,transactionBean.getEmailId());
					ps.setString(8,transactionBean.getMobile());
					ps.setString(9,transactionBean.getTranDateTime());
					ps.setString(10,transactionBean.getRespTranDateTime());
					ps.setString(11,timeDiff);

					ps.setString(12,transactionBean.getAction());
					ps.setString(13,"System");
					ps.setString(14,"System");
					ps.setString(15,transactionBean.getAmount());
					ps.setString(16,transactionBean.getRespAmount());
					ps.setString(17,transactionBean.getAction());
					ps.setString(18,"System");


				}

				@Override
				public int getBatchSize() {
					return successfulButAlreadyBookedExamBookings.size();
				}
			});
			}catch(Exception e2){
			}
		}

	}
	
	@Transactional(readOnly = false)
	public void updateSeatsForAlreadyBookedConflictUsingSingleConnection(ExamBookingTransactionBean responseBean) throws Exception{

		Connection conn = dataSource.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try{
			conn.setAutoCommit(false);

			String sql = "Update exam.exambookings"
					+ " set booked = 'N' ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " description = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"',"
					+ " bookingCompleteTime = sysdate(), "
					+ " lastModifiedBy = 'AutoProjectBooking Scheduler',"
					+ " lastModifiedDate =sysdate(), paymentOption=? "


					+ " where sapid = ? "
					+ " and trackId = ?";


			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1,responseBean.getResponseMessage());
			pstmt.setString(2,responseBean.getTransactionID());
			pstmt.setString(3,responseBean.getRequestID());
			pstmt.setString(4,responseBean.getMerchantRefNo());
			pstmt.setString(5,responseBean.getSecureHash());
			pstmt.setString(6,responseBean.getRespAmount());
			pstmt.setString(7,responseBean.getDescription()+" to be refunded");
			pstmt.setString(8,responseBean.getResponseCode());
			pstmt.setString(9,responseBean.getRespPaymentMethod());
			pstmt.setString(10,responseBean.getIsFlagged());
			pstmt.setString(11,responseBean.getPaymentID());
			pstmt.setString(12,responseBean.getError());
			pstmt.setString(13,responseBean.getRespTranDateTime());
			pstmt.setString(14, responseBean.getPaymentOption());
			pstmt.setString(15,responseBean.getSapid());
			pstmt.setString(16,responseBean.getTrackId());

			int noOfRowsUpdated = pstmt.executeUpdate();




			conn.commit();

		}catch(Exception e){
			
			conn.rollback();
			throw e;
		}finally{
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}

}
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getBookedSubjectList(RequestFormBean requestFormBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select e.*,ec.examCenterName from exam.exambookings e,exam.examcenter ec where ec.centerId = e.centerId and e.year='2020' and e.month='Apr' and e.sapid=? and e.booked='Y' and e.subject NOT IN ('Project', 'Module 4 - Project')";
			return jdbcTemplate.query(sql, new Object[] { requestFormBean.getSapid() }, new BeanPropertyRowMapper<ExamBookingTransactionBean>(ExamBookingTransactionBean.class));
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean checkAlreadyRefundRequestSubmitted(RequestFormBean requestFormBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select count(*) as count from exam.refund_request where sapid = ?";
			int countOfRefund = jdbcTemplate.queryForObject(sql, new Object[] { requestFormBean.getSapid() }, Integer.class);
			if(countOfRefund > 0) {
				return true;
			}else {
				return false;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean checkAlreadyCarryForwardRequestSubmitted(RequestFormBean requestFormBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "SELECT count(*) as count FROM exam.examfeeexemptsubject where year='2020' and month='Jun' and sapid = ?; ";
			int countOfExcept = jdbcTemplate.queryForObject(sql, new Object[] { requestFormBean.getSapid() }, Integer.class);
			if(countOfExcept > 0 ) {
				return true;
			}else {
				return false;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			
			return true;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean isBookingFound(String year,String month,String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select count(*) from exam.exambookings where year=? and month=? and sapid=? and booked='Y'";
			int countOfBooking = jdbcTemplate.queryForObject(sql, new Object[] {year,month,sapid},Integer.class);
			if(countOfBooking > 0) {
				return true;
			}
			return false;
			
		}
		catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public boolean isCarryForwardRefundRequestFlagLive(String year,String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql = "select count(*) from exam.carry_forword_refund_exambooking_flag where year=? and month=? and status='Y'";
			int countOfBooking = jdbcTemplate.queryForObject(sql, new Object[] {year,month},Integer.class);
			if(countOfBooking > 0) {
				return true;
			}
			return false;
			
		}
		catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingRefundRequestReportBean> getExamBookingRefundRequestReport() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select eb.sapid, eb.subject, trackId, sum(eb.amount) as amount, eb.month, eb.year, "
				+ "concat(s.firstName, ' ', s.lastName) as name, s.emailId, s.mobile  from exam.exambookings as eb " + 
				"inner join exam.students as s on eb.sapid=s.sapid "
				+ "inner join exam.carry_forword_refund_exambooking_flag as c on c.year=eb.year and c.month=eb.month "
				+ " group by eb.sapid";
		List<ExamBookingRefundRequestReportBean> examBookingRefundRequestReportBeans = (List<ExamBookingRefundRequestReportBean>) jdbcTemplate.query(sql, new Object[] {  }, new BeanPropertyRowMapper(ExamBookingRefundRequestReportBean.class));

		return examBookingRefundRequestReportBeans;
	}
	
	@Transactional(readOnly = true)
	public List<ExamFeeExemptSubjectBean> getCarryForwardExamBookingRefundRequest(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select concat(s.firstName, ' ', s.lastName) as name, s.emailId, s.mobile,e.* from exam.examfeeexemptsubject e,exam.students s where s.sapid = e.sapid and year=2020 and month='Jun' group by sapid";
		List<ExamFeeExemptSubjectBean> examFeeExemptSubjectBeans = (List<ExamFeeExemptSubjectBean>) jdbcTemplate.query(sql, new Object[] {  }, new BeanPropertyRowMapper(ExamFeeExemptSubjectBean.class));

		return examFeeExemptSubjectBeans;
	}
	
	@Transactional(readOnly = true)
	public List<RefundRequestBean> getExamBookingRefundRequests(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT concat(s.firstName, ' ', s.lastName) as name, s.emailId, s.mobile,r_r.* FROM exam.refund_request r_r,exam.students s where s.sapid = r_r.sapid  group by r_r.trackId;";
		List<RefundRequestBean> refundRequestBeans = (List<RefundRequestBean>) jdbcTemplate.query(sql, new Object[] {  }, new BeanPropertyRowMapper(RefundRequestBean.class));

		return refundRequestBeans;
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getExamBookingPPRefundAmont(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT s.firstName,s.lastName,s.emailId,s.mobile,e.* FROM exam.exambookings e,exam.students s where s.sapid = e.sapid and year=2020 and month='Apr' and e.sapid not in (SELECT distinct sapid FROM exam.refund_request) and subject NOT IN ('Project', 'Module 4 - Project') and booked in ('PP','RL') and (amount <> 0 and amount = 500) group by trackId order by e.sapid asc;";
		List<ExamBookingTransactionBean> examBookingTransactionBean = (List<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[] {  }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		return examBookingTransactionBean;
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getSlotChangeBooking(String sapid,String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.exambookings where year=? and month=? and sapid=? and tranStatus='Seat Released' group by trackId";
		List<ExamBookingTransactionBean> refundRequestBeans = (List<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[] { year,month,sapid }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return refundRequestBeans;
	}
	
	@Transactional(readOnly = true)
	public List<StudentExamBean> getDemoNotCompletedStudent(){
        jdbcTemplate = new JdbcTemplate(dataSource);
        List<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
        String sql =" SELECT eb.sapid, s.firstName, s.lastName, s.emailId,s.firebaseToken,s.mobile FROM exam.exambookings eb " + 
                    "  INNER JOIN exam.students s ON s.sapid = eb.sapid " + 
                    "  LEFT JOIN exam.demoexam_attendance da ON eb.sapid = da.sapid " + 
                    "      WHERE (markAttend = 'N' OR markAttend IS NULL)  " + 
                    "        AND eb.year =2020 AND eb.month='Jun' AND eb.booked = 'Y' AND eb.centerId <> -1  " + 
                    "        AND eb.subject not IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " + 
                    "          GROUP BY eb.sapid ";
        
        studentList = (List<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(StudentExamBean.class));
        return studentList;
    }
	
	@Transactional(readOnly = true)
	public List<StudentExamBean> getTomorrowExamStudentList(){
		
        jdbcTemplate = new JdbcTemplate(dataSource);
        List<StudentExamBean> studentList = new ArrayList<StudentExamBean>();
        String sql ="SELECT * FROM exam.students WHERE sapid IN " + 
        		"	(SELECT sapid FROM exam.exambookings " + 
        		"        WHERE examDate = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND booked = 'Y') ";
        studentList = (List<StudentExamBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(StudentExamBean.class));
        return studentList;
    }
	
	@Transactional(readOnly = false)
	public long insertMailRecord(final ArrayList<MailBean> mailList,final String fromUserId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Changed since this will be only single insert//
		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, mailList.get(0).getSubject());
		        statement.setString(2, fromUserId);
		        statement.setString(3,mailList.get(0).getFilterCriteria());
		        statement.setString(4,mailList.get(0).getBody());
		        statement.setString(5,mailList.get(0).getFromEmailId());
		        return statement;
		    }
		}, holder);

		long primaryKey = holder.getKey().longValue();
		return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public void insertUserMailRecord(final ArrayList<MailBean> mailList, final String fromUserId,final String fromEmailId,final long mailTemplateId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ";
		try{
			int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MailBean mailBean = mailList.get(i);
					ps.setString(1,StringUtils.join(mailBean.getSapIdRecipients(),","));
					ps.setString(2,StringUtils.join(mailBean.getMailIdRecipients(),","));
					ps.setString(3,fromUserId);
					ps.setString(4,fromEmailId);
					ps.setString(5,String.valueOf(mailTemplateId));
					ps.setString(6, fromUserId);
				}

				@Override
				public int getBatchSize() {
					return mailList.size();
				}
			  });
		}catch(Exception e){
			
		}
	}
	 
	@Transactional(readOnly = true)
	public List<String> getExamBookingEligibleSubjectsForCycle(String sapid, String liveExamMonth, String liveExamYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `subject` "
				+ " FROM `exam`.`student_cycle_subject_config` "
				+ " WHERE `sapid` = ? AND `year` = ? AND `month` = ? "
				+ " order by subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		return jdbcTemplate.queryForList(
			sql, 
			new Object[]{
				sapid, liveExamYear, liveExamMonth
			},
			String.class
		);
	}

	@Transactional(readOnly = true)
	public List<String> getFailedAssignSubmittedSubjectsList(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `subject` "
				+ " FROM `exam`.`passfail` "
				+ " WHERE `sapid` = ? "
				+ " AND `isPass` = 'N' "
				+ " AND `assignmentscore` = 'ANS' ";

		return jdbcTemplate.queryForList(
			sql, 
			new Object[]{
				sapid
			},
			String.class
		);
	}
	
	@Transactional(readOnly = false)
	public void markTransactionsPending(final List<ExamBookingTransactionBean> bookingList) {
		//Insert tracking numbers for current interaction
		String sql = ""
				+ " UPDATE `exam`.`exambookings` "
				+ " SET `tranStatus` = '" + ONLINE_PAYMENT_INITIATED + "',"
				+ " `error` = ?,"
				+ " `lastModifiedBy` = 'AutoBooking Scheduler',"
				+ " `lastModifiedDate` = sysdate()"
				+ " WHERE  "
				+ " `sapid` = ? "
				+ " AND `year` = ? "
				+ " AND `month` = ? "
				+ " AND `trackid` = ? "
				+ " AND `booked` = 'N'"
				+ " AND `paymentMode` = 'Online' ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				ExamBookingTransactionBean bean = bookingList.get(i);
				ps.setString(1, bean.getError());
				ps.setString(2, bean.getSapid());
				ps.setString(3, bean.getYear());
				ps.setString(4, bean.getMonth());
				ps.setString(5, bean.getTrackId());
			}
			public int getBatchSize() {
				return bookingList.size();
			}
		});



	}
	
	@Transactional(readOnly = true)
	public List<PGReexamEligibleStudentsBean> getFailedStudentList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT p.sapid, p.subject, p.writtenYear AS examYear, p.writtenMonth AS examMonth, p.program AS examProgram, p.sem AS examSem, p.failReason, s.firstName, s.lastName, s.program, s.firstName, s.lastName, s.validityEndMonth AS studentValidityEndMonth, s.validityEndYear AS studentValidityEndYear, s.PrgmStructApplicable, s.consumerType, s.enrollmentMonth, s.enrollmentYear FROM exam.passfail p INNER JOIN (SELECT * FROM exam.students WHERE (programStatus IS NULL OR programStatus = '') AND consumerType = 'Retail' AND (program NOT IN ('MBA - WX' , 'MBA - X')) GROUP BY sapId) s ON s.sapid = p.sapid WHERE (s.programCleared IS NULL or s.programCleared <> 'Y') and p.isPass = 'N';";
		return (List<PGReexamEligibleStudentsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(PGReexamEligibleStudentsBean.class));
	}
	
	public List<PGReexamEligibleStudentsBean> getPassFailedNotProcessed() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT m.sapid, m.subject, m.year AS examYear, m.month AS examMonth, m.program AS examProgram, m.sem AS examSem, \"\" as failReason, s.firstName, s.lastName, s.program, s.firstName, s.lastName, s.validityEndMonth AS studentValidityEndMonth, s.validityEndYear AS studentValidityEndYear, s.PrgmStructApplicable, s.consumerType, s.enrollmentMonth, s.enrollmentYear FROM exam.marks m INNER JOIN (SELECT * FROM exam.students WHERE (programStatus IS NULL OR programStatus = '') AND consumerType = 'Retail' AND (program NOT IN ('MBA - WX' , 'MBA - X')) GROUP BY sapId) s ON s.sapid = m.sapid INNER JOIN exam.examorder e ON e.month = m.month AND e.year = m.year WHERE (s.programCleared IS NULL or s.programCleared <> 'Y') and e.live = 'Y' and m.processed = 'N';";
		return (List<PGReexamEligibleStudentsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(PGReexamEligibleStudentsBean.class));
	}
	
	@Transactional(readOnly = true)
	public ExamBookingTransactionBean checkIfAssignmentPaymentSuccessful(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	    String sql = " select * from exam.assignmentpayment where trackId=? and tranStatus='Online Payment Successful' limit 1";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
 
		ExamBookingTransactionBean booking= null;
		try {
			booking= (ExamBookingTransactionBean) jdbcTemplate.queryForObject(sql,new Object[]{trackId},new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			
		} catch (DataAccessException e) { 
		}
		return booking;
	}
	
	@Transactional(readOnly = true)
	public boolean checkIfProjectPaymentSuccessful(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.exambookings where trackId=? and tranStatus='Online Payment Successful'";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
 
		ArrayList<ExamBookingTransactionBean> bookingList= new ArrayList<ExamBookingTransactionBean> ();
		try {
			bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		} catch (DataAccessException e) {
			 
		} 
		if(bookingList.size()>0) return true;
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getConfirmedAssignmentBooking(String sapid,String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamBookingTransactionBean> bookingList= new ArrayList<ExamBookingTransactionBean>();
		try { 
			String sql = " select * from exam.assignmentpayment " + 
					" where  sapid=? and trackId=? and booked='Y'";
 
			bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid,trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
        } catch (Exception e) { 
			
		} 
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentDataWithInValidity(String sapid, String maxTimeTableLiveYearMonth) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = " SELECT  " + 
					"    * " + 
					"FROM " + 
					"    exam.students s " + 
					"WHERE " + 
					"    s.sapid = ? " + 
					"        AND s.sem = (SELECT  " + 
					"            MAX(sem) " + 
					"        FROM " + 
					"            exam.students " + 
					"        WHERE " + 
					"            sapid = ? ) " + 
					"        AND STR_TO_DATE(?, '%Y-%m-%d') <= STR_TO_DATE(CONCAT(s.validityEndYear, '-', s.validityEndMonth, '-31'), '%Y-%b-%d') " ;
			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid, maxTimeTableLiveYearMonth
			}, new BeanPropertyRowMapper(StudentExamBean.class));

		}catch(Exception e){
		}
		return student;
	}

	@Transactional(readOnly = true)
	public String getMaxTimeTableLiveYearMonth() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String maxTimeTableLiveYearMonth = null;
		try{
			String sql = "select STR_TO_DATE(concat(year,'-',month,'-31'), '%Y-%b-%d') from exam.examorder eo where  eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "  ;
			
			maxTimeTableLiveYearMonth= jdbcTemplate.queryForObject(sql, String.class);
			
		}catch(Exception e){
		}
		return maxTimeTableLiveYearMonth;
	}
	
	
	@Transactional(readOnly = true)
	public ExamBookingTransactionBean getProjectTransactionStatusByTrackId(String track_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ExamBookingTransactionBean examBean = new ExamBookingTransactionBean();
		String sql = "select * from exam.exambookings WHERE  trackId = ?";
		try {
			examBean = (ExamBookingTransactionBean) jdbcTemplate.queryForObject(sql, new Object[] { track_id },
					new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		} catch (Exception e) {
		}
		return examBean;
	}
	
	
	@Transactional
	public List<ExamBookingTransactionBean> getExamBookingTransactionBean(String sapid, String trackid){
		jdbcTemplate = new  JdbcTemplate(dataSource);
		
		String sql = "Select * from exam.exambookings "
				+ " where sapid = ? "
				+ " and trackId = ? order by examDate";
		List<ExamBookingTransactionBean> completeBookings  = new ArrayList<>();
		try {
			completeBookings = jdbcTemplate.query(sql, new Object[] {sapid, trackid}, 
					new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		} catch (Exception e) {
		}
		return completeBookings;
	}
	@Transactional(readOnly = true)
	public List<StudentExamBean> getDemoNotCompletedStudents(String year,String month){
        jdbcTemplate = new JdbcTemplate(dataSource);
        List<StudentExamBean> studentsList = new ArrayList<StudentExamBean>();
        String sql =" SELECT eb.sapid, s.firstName, s.lastName, s.emailId,s.firebaseToken,s.mobile, max(eb.examDate) as maxExamDate FROM exam.exambookings eb " + 
                    "  INNER JOIN exam.students s ON s.sapid = eb.sapid " + 
                    "  LEFT JOIN exam.demoexam_attendance da ON eb.sapid = da.sapid " + 
                    "      WHERE (markAttend = 'N' OR markAttend IS NULL)  " +
                    "        AND eb.year =? AND eb.month=? AND eb.booked = 'Y' AND eb.centerId <> -1  " + 
                    "        AND eb.subject not IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " + 
                    "          GROUP BY eb.sapid ";
        
        studentsList =  jdbcTemplate.query(sql, new Object[] {year,month}, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
       
        return studentsList;
    }
	@Transactional(readOnly = true)
	public IdCardExamBean getIdCardForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sapid,fileName from portal.digital_id_cards where sapid = ?";
		IdCardExamBean idCardBean = jdbcTemplate.queryForObject(sql, new Object[] { sapid },
				new BeanPropertyRowMapper<IdCardExamBean>(IdCardExamBean.class));
		return idCardBean;
	}
	
//	@Transactional(readOnly = true)
//	public String getFirstExamDate(String year,String month) {
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = "SELECT date FROM exam.examcenter_slot_mapping where year =? and month = ? order by date limit  1";
//		String FirstExamDate =  (String)jdbcTemplate.queryForObject(sql, new Object[]{year, month}, new SingleColumnRowMapper(String.class));
//		return FirstExamDate;
//	}
	@Transactional(readOnly = false)
	public List<ExamBookingTransactionBean> getAllBookingsBySapId(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.exambookings where sapid = ? and centerId <> -1";
		List<ExamBookingTransactionBean> transactionBeans = 
				jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return transactionBeans;
	}
	
	@Transactional(readOnly = true)
	public boolean isStudentAllowedToChangeSlotPriorNumberOfDays(String sapid, String numberOfPriorDays) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    COUNT(`sapid`) " + 
				" FROM " + 
				"    `exam`.`exambookings` " + 
				" WHERE " + 
				"    `sapid` = ? " + 
				"        AND `centerId` <> -1 " + 
				"        AND `booked` = 'Y' " + 
				"        AND SYSDATE() < DATE_SUB(CONCAT(`examDate`, ' ', `examTime`),INTERVAL ? DAY)";
		
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] {sapid, numberOfPriorDays}, Integer.class);
		
		return count > 0;
	}

	@Transactional(readOnly = true)
	public boolean checkIfExamTimetableActiveForCurrentExamYearMonth() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT " + 
				"	count(esm.examcenterId) " + 
				"FROM " + 
				"	exam.examcenter_slot_mapping esm " + 
				"INNER JOIN exam.examorder e  " + 
				"ON " + 
				"	e.`year` = esm.`year` " + 
				"	AND e.`month` = esm.`month` " + 
				"	AND e.`order` = ( " + 
				"	SELECT " + 
				"		max(e2.ORDER) " + 
				"	FROM " + 
				"		exam.examorder e2 " + 
				"	WHERE " + 
				"		e2.timeTableLive = 'Y') " + 
				"WHERE " + 
				"	esm.`date` > SYSDATE()";
		
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
		return count > 0;
	}
		
	public ArrayList<ExamBookingTransactionBean> getConfirmedProjectBookingApplicableCycle(String sapid, String  month, String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month ";
				if (sapid != null && !("".equals(sapid))) {
					sql = sql + " and sapid = ? ";
					parameters.add(sapid);
				}
				sql = sql + "and a.booked = 'Y' and subject IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where projectSubmissionLive='Y' ";
				if (year != null && !("".equals(year))) {
					sql = sql + " and year = ? ";
					parameters.add(year);
				}
				if (month != null && !("".equals(month))) {
					sql = sql + " and month = ? ";
					parameters.add(month);
				}
				sql = sql + ") order by a.examDate, a.examTime, a.subject asc";
				Object[] args = parameters.toArray();
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	public boolean isBookingLiveInStudentCycleSubjectConfig(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    COUNT(*) AS 'count' " + 
				" FROM " + 
				"    `exam`.`student_cycle_subject_config` " + 
				" WHERE " + 
				"    sapid = ? AND `year` = ? " + 
				"        AND `month` = ? " + 
				"        AND SYSDATE() BETWEEN `bookingStartDateTime` AND `bookingEndDateTime` ";
		
		Integer count = jdbcTemplate.query(sql, new Object[] {sapid, getLiveExamYear(), getLiveExamMonth()}, new CountResultSetExtractor("count"));
		
		return count.intValue() > 0;
	}
	
	class CountResultSetExtractor implements ResultSetExtractor<Integer>{
		String columnName;
		
		CountResultSetExtractor(String columnName){
		this.columnName = columnName;
		}
		
		@Override
		public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
			int count = 0;
			if (rs.next())
				return rs.getInt(columnName);
			return count;
		}
		
	}
}
