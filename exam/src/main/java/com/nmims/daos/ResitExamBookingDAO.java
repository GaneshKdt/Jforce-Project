package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.FailTransactionBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableBean;
import com.nmims.helpers.PaginationHelper;

@Repository("resitExamBookingDAO")
public class ResitExamBookingDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated";
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private final String REFUND = "Refund";
	private final String EXPIRED = "Expired"; 
	final String DD_APPROVAL_PENDING = "DD Approval Pending";
	private final String DD_APPROVED = "DD Approved";
	private final String DD_REJECTED = "DD Rejected";
	private final String CENTER_CHANGED_BOOKED = "Center Changed and Booked";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	
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

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return studentsMarksList;
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
		String sql = "SELECT subject FROM exam.assignmentStatus a, exam.examorder b where a.examYear = b.year and a.examMonth = b.month "
				+ " and sapid = ? and submitted = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')  "
				+ " order by a.subject asc";

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
			sql = sql + " and sem >= 3 ";
		}
		sql = sql + " order by program, sem, subject";

		ArrayList<String> subjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{
				student.getProgram(), student.getPrgmStructApplicable(), lastSem}, new SingleColumnRowMapper(String.class));
		return subjects;
	}

	@Transactional(readOnly = true)
	public List<TimetableBean> getTimetableListForGivenSubjects(ArrayList<String> subjects, String program, String programStructure) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and a.program = ? "
				+ " and a.prgmStructApplicable = ? "
				+ " order by a.date, a.starttime asc";

		List<TimetableBean> tempTimeTableList = jdbcTemplate.query(sql, new Object[]{program, programStructure},new BeanPropertyRowMapper(TimetableBean.class));
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
				+ " ddDate)"

				+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


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
			}
			public int getBatchSize() {
				return bookingList.size();
			}
		});



	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public int[] upsertOnlineInitiationTransaction(String sapid, final List<ExamBookingTransactionBean> bookingList){
		try{



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



			sql = "Update exam.examcenter_slot_mapping"
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


			int[] result = ArrayUtils.addAll(examBookingDBUpdateResults, slotBookingDBUpdateResults);

			if(result == null){
				throw new Exception("Error in updating transaction details OR updating on Hold seats.");
			}
			return result;

		}catch(Exception e){
			
			return null;
		}

	}


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


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void clearOldOnlineInitiationTransactionPeriodically(){
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);

			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
			String sql = "Select * from exam.exambookings "
					+ " where tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
					+ " and booked = 'N'"
					+ " and paymentMode = 'Online' "
					+ " and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800)";


			List<ExamBookingTransactionBean> oldIncompleteBookings = jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));


			if(oldIncompleteBookings != null && oldIncompleteBookings.size() > 0){

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
						+ " where trackid = ? "
						+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
						+ " and booked = 'N'"
						+ " and paymentMode = 'Online' ";


				slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						ExamBookingTransactionBean bean = bookingList.get(i);
						ps.setString(1, bean.getTrackId());
					}
					public int getBatchSize() {
						return bookingList.size();
					}
				});

			}


		}catch(Exception e){
			

		}

	}

	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";



			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
		}catch(Exception e){
			//
		}
		return student;
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getRegistrations(String sapid) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.registration "
				+ " where  sapid = ? ";
		List<StudentMarksBean> registrationList = new ArrayList<>();
		try{
			registrationList = jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(StudentMarksBean.class));
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
		String sql = "SELECT distinct a.trackId, tranStatus FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and a.booked <> 'Y' "
				+ " and a.booked <> 'RL' "
				+ " and a.booked <> 'RF' "
				+ " and a.paymentMode = 'Online'"
				+ " order by a.subject asc";

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
				+ " and a.sapid = s.sapid "
				+ " and a.paymentMode = 'Online'"
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
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.subject asc";

		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getSubjectsCentersForTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject, centerId FROM exam.exambookings where trackId = ?";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getDDAndApprovedOnlineTransSubjects(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked <> 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and ( "
				+ "(paymentMode = 'DD') "
				+ " OR "
				+ "(paymentMode = 'Online' and tranStatus = '" + ONLINE_PAYMENT_MANUALLY_APPROVED + "') "
				+ ")"
				//	+ " and tranStatus = '" + DD_APPROVED + "' "
				+ " order by a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
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
	public ArrayList<ExamBookingTransactionBean> getConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrRelesedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and (a.booked = 'Y' or a.booked = 'RL') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
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
	public List<ExamBookingTransactionBean> updateSeatsForDD(String sapid, final List<ExamBookingTransactionBean> bookingList) {
		try{
			String subjectCommaSeparated = "";
			for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject() + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject() + "'";
				}
			}
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " centerId = ?, "
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
					ps.setString(2, bean.getSapid());
					ps.setString(3, bean.getSubject());

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
	public List<ExamBookingTransactionBean> updateSeatsForOnlineApprovedTransaction(String sapid, final List<ExamBookingTransactionBean> bookingList) {
		try{
			String subjectCommaSeparated = "";
			for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject() + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject() + "'";
				}
			}
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " centerId = ?, "
					+ " examDate = ?, "
					+ " examTime = ?, "
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
					ps.setString(4, bean.getSapid());
					ps.setString(5, bean.getSubject());

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
	public List<ExamBookingTransactionBean> updateSeatsForRealeasedNoCharges(String sapid, final List<ExamBookingTransactionBean> bookingList) {
		try{
			String subjectCommaSeparated = "";
			for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject() + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject() + "'";
				}
			}
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
	public List<ExamBookingTransactionBean> updateCenterForReleasedSeatsForStudents(String sapid, 
			String year, String month, final List<ExamBookingTransactionBean> bookingList) {
		try{
			String subjectCommaSeparated = "";
			for (int i = 0; i < bookingList.size(); i++) {
				ExamBookingTransactionBean bean = bookingList.get(i);
				if(i == 0){
					subjectCommaSeparated = "'" +bean.getSubject() + "'";
				}else{
					subjectCommaSeparated = subjectCommaSeparated + ", '" + bean.getSubject() + "'";
				}
			}
			jdbcTemplate = new JdbcTemplate(dataSource);

			String 	sql = "Update exam.exambookings"
					+ " set booked = 'Y' ,"
					+ " tranStatus = '"+CENTER_CHANGED_BOOKED+"' , "
					+ " centerId = ?, "
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
					ps.setString(2, bean.getSapid());
					ps.setString(3, bean.getSubject());
					ps.setString(4, bean.getYear());
					ps.setString(5, bean.getMonth());
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
					+ "  sapid , trackId ) values "
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
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		}catch(Exception e){
		}
		return student;
	}

	@Transactional(readOnly = false)
	public int approveOnlineTransactions(String trackId, ExamBookingTransactionBean responseBean) throws Exception{
		int noOfRowsUpdated = 0;
		try {		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "Update exam.exambookings"
				+ " set ResponseMessage = ? ,"
				+ " transactionID = ? ,"
				+ " merchantRefNo = ? ,"
				+ " respAmount = ? ,"
				+ " responseCode = '0' ,"
				+ " isFlagged = ? ,"
				+ " paymentID = ? ,"
				+ " respTranDateTime = ? ,"
				+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"' "


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
	public List<ExamBookingTransactionBean> insertSeatsForFreeSubjects(String sapid, String trackId, final List<ExamBookingTransactionBean> bookingsList) {
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

			//Release hold seats and marks them as booked 
			sql = "Update exam.examcenter_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1 "
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
	public List<ExamBookingTransactionBean> updateSeatsForOnlineUsingSingleConnection(ExamBookingTransactionBean responseBean) throws Exception{

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
					+ " bookingCompleteTime = sysdate() "


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
			pstmt.setString(14,responseBean.getSapid());
			pstmt.setString(15,responseBean.getTrackId());

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


				//Release hold seats and marks them as booked 
				sql = "Update exam.examcenter_slot_mapping"
						+ " set booked = COALESCE(booked, 0) + 1, "
						+ " onHold = COALESCE(onhold, 0) - 1 "
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
					+ " tranStatus = '"+ONLINE_PAYMENT_MANUALLY_APPROVED+"',"
					+ " bookingCompleteTime = sysdate() "


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
			pstmt.setString(14,responseBean.getSapid());
			pstmt.setString(15,responseBean.getTrackId());

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
				upsertAssignmentStatus(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}
	
	@Transactional(readOnly = false)
	private void upsertAssignmentStatus(StudentMarksBean bean, JdbcTemplate jdbcTemplate) {
		String sql = "INSERT INTO exam.examfeeexempt "
				+ "(sapid, year, month, sem, createdBy, createdDate, lastModifiedBy , lastModifiedDate)"
				+ " VALUES "
				+ "(?,?,?,?,?,sysdate() , ?, sysdate())"
				+ " on duplicate key update "
				+ "	    sapid = ?,"
				+ "	    year = ?,"
				+ "	    month = ?,"
				+ "	    sem = ?,"
				+ "	    createdBy = ?, "
				+ "	    createdDate = sysdate() ";


		String year = bean.getYear();
		String month = bean.getMonth();
		String sapid = bean.getSapid();
		String sem = bean.getSem();
		String createdBy = bean.getCreatedBy();
		


		jdbcTemplate.update(sql, new Object[] { 
				sapid,
				year,
				month,
				sem,
				createdBy,
				sapid,
				year,
				month,
				sem,
				createdBy,
				bean.getLastModifiedBy()
		});

		
	}
	
	@Async
	@Transactional(readOnly = false)
	public void saveHallTicketDownloaded(final String sapid, final ArrayList<ExamBookingTransactionBean> subjectsBooked) {
		
		String sql = "Update exam.exambookings"
				+ " set htDownloaded = 'Y' "
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
	public List<TimetableBean> getTimetableForResit() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.timetable a, exam.examorder b "
				+ " where  a.examyear = b.year and  a.examMonth = b.month and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "

				+ " order by a.date, a.starttime asc";

		List<TimetableBean> timeTableList = jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(TimetableBean.class));
		

		return timeTableList;

	}


}
