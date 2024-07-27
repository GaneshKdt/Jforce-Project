package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.ExecutiveTimetableBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;

public class ExecutiveExamBookingDao extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	
	public String getCommaSepareatedExecutiveSubjects(ArrayList<String> bookingList) {
		String subjectCommaSeparated = "";
		for (int i = 0; i < bookingList.size(); i++) {
			String str = bookingList.get(i);
			if(i == 0){
				subjectCommaSeparated = "'" +str.replace("'","\\'") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + str.replace("'","\\'") + "'";
			}
		}
		
		return subjectCommaSeparated;
	}

	
	private List<ExecutiveTimetableBean> timeTableList = null; 
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = dataSource;
	}

	@Transactional(readOnly = true)
	public HashMap<String,ArrayList<String>> getProgramSubjectMapping(){
		HashMap<String,ArrayList<String>> programSubjectMapping = new HashMap<String,ArrayList<String>>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " select * from exam.program_subject where (program = 'MPDV' or program = 'EPBM') and active = 'Y' ";
		ArrayList<ProgramSubjectMappingExamBean> sasActiveProgramSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));

		if(sasActiveProgramSubjectList !=null && sasActiveProgramSubjectList.size() > 0 ){
			for(ProgramSubjectMappingExamBean programSubjectBean : sasActiveProgramSubjectList){
				String key = programSubjectBean.getProgram()+"-"+programSubjectBean.getSem()+"-"+programSubjectBean.getPrgmStructApplicable();
				if(!programSubjectMapping.containsKey(key)){
					programSubjectMapping.put(key, new ArrayList<String>());
				}
				programSubjectMapping.get(key).add(programSubjectBean.getSubject().trim());
			}
		}
		return programSubjectMapping;
	}

	@Transactional(readOnly = true)
	public ExecutiveExamOrderBean getCurrentLiveExamSetup(StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ExecutiveExamOrderBean examLiveCurrently = null;
		//Find out exam set up for student batch, that is live as well as within exam registration dates
		String sql = "Select * from exam.executive_examorder where acadMonth = ? and acadYear = ? "
				+ " and registrationStartDate <= sysdate() and registrationEndDate >= sysdate()";
		try {
			ArrayList<ExecutiveExamOrderBean> examLiveList = (ArrayList<ExecutiveExamOrderBean>)jdbcTemplate.query(sql, new Object[]{student.getEnrollmentMonth(),student.getEnrollmentYear()},  
					new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));

			if(examLiveList != null && examLiveList.size() > 0){
				examLiveCurrently = examLiveList.get(0);
			}
		} catch (Exception e) {
			
		}

		return examLiveCurrently;
	}

	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students s where "
					+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";




			student = (StudentExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentExamBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
			
			return student;
		}catch(Exception e){
			return null;
			//
		}

	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSubjectsEligibleForExam(StudentExamBean student, ExecutiveExamOrderBean examLiveCurrently) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> eligibleSubjects = new ArrayList<>();

		String sapid = student.getSapid();
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		String examMonth = examLiveCurrently.getMonth();
		String examYear = examLiveCurrently.getYear();
		String acadMonth = examLiveCurrently.getAcadMonth();
		String acadYear = examLiveCurrently.getAcadYear();

		try {
			String sql = "select subject from exam.program_subject ps, exam.registration r where r.sapid = ? "
					+ " and r.program = ps.program  "
					+ " and r.sem = ps.sem "
					+ " and ps.prgmStructApplicable = ? "
					+ " and subject not in (select subject from exam.passfail where sapid = ? and isPass = 'Y') "
					+ " and subject in (select subject from exam.live_exam_subjects les where les.program = ? "
					+ " and les.prgmStructApplicable = ? and les.acadYear = ?  and les.acadMonth = ? "
					+ " and examYear = ? and examMonth = ? )";

			eligibleSubjects = (ArrayList<String> )jdbcTemplate.query(sql, new Object[]{sapid, programStructure, sapid, program, 
					programStructure, acadYear, acadMonth, examYear, examMonth}, new SingleColumnRowMapper(String.class));

		} catch (Exception e) {
			
		}

		return eligibleSubjects;
	}
	
	@Transactional(readOnly = true)
	public boolean isSubjectLiveForRegistration( String subject,StudentExamBean student, ExecutiveExamOrderBean examLiveCurrently) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) from exam.live_exam_subjects les"
				+ " 	 where les.program = ?"
				+ "		 and les.prgmStructApplicable = ?"
				+ "		 and les.acadYear = ? "
				+ "		 and les.acadMonth = ? "
				+ "		 and examYear = ?"
				+ "		 and examMonth = ? "
				+ "      and subject = ? ";
				

		int noOfRows = 0;
		try {
			String program = student.getProgram();
			String programStructure = student.getPrgmStructApplicable();
			String examMonth = examLiveCurrently.getMonth();
			String examYear = examLiveCurrently.getYear();
			String acadMonth = examLiveCurrently.getAcadMonth();
			String acadYear = examLiveCurrently.getAcadYear();
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{program, 
					programStructure, acadYear, acadMonth, examYear, examMonth,subject},Integer.class);
			if(noOfRows>0) {
				return true;
			}
		} catch (Exception e) {
			
		}

		return false;
	}
	
	@Transactional(readOnly = true)
	public int noOfAttemptsBySapidNSubject(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) FROM exam.marks where subject = ? and sapid = ?";
				

		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{subject,sapid},Integer.class);
		} catch (Exception e) {
			
		}

		return noOfRows;
	}
	
	@Transactional(readOnly = true)
	public int getMaxMarksBySapidNSubject(String sapid, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT total FROM exam.passfail where subject = ? and sapid = ? and booked = 'Y'";
				

		int total = 0;
		try {
			total = (int) jdbcTemplate.queryForObject(sql, new Object[]{subject,sapid},Integer.class);
		} catch (Exception e) {
			
		}

		return total;
	}

	@Transactional(readOnly = true)
	public HashMap<String, ExecutiveBean> getBookedSubjects(
			StudentExamBean student, ExecutiveExamOrderBean examLiveCurrently) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.executive_exam_bookings eb  , exam.executive_examcenter ec "
				+ " where eb.year = ? and eb.month = ? "
				+ " and eb.sapid = ?   and booked = 'Y' "
				+ " and eb.centerId = ec.centerId ";

		HashMap<String,ExecutiveBean> mapOfBookedSubjects = new HashMap<>();

		try{
			ArrayList<ExecutiveBean> bookingSubject = (ArrayList<ExecutiveBean>) jdbcTemplate.query(sql, 
					new Object[] {examLiveCurrently.getYear(), examLiveCurrently.getMonth(), student.getSapid()}, 
					new BeanPropertyRowMapper(ExecutiveBean.class));

			if(bookingSubject !=null && bookingSubject.size() > 0){
				for(ExecutiveBean bean : bookingSubject){
					String key = bean.getSapid()+"-"+bean.getSubject();
					mapOfBookedSubjects.put(key, bean);
				}
			}
		}catch(Exception e){
			
		}
		return mapOfBookedSubjects;
	}

	@Transactional(readOnly = true)
	public Map<String, List<ExamCenterBean>> getAvailableCenters(
			ExecutiveExamOrderBean examLiveCurrently, StudentExamBean student,
			ArrayList<String> subjects) {


		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();

		String year = examLiveCurrently.getYear();
		String month = examLiveCurrently.getMonth();
		String sapId = student.getSapid();
		//String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();


		sql = " SELECT *, (cs.capacity - COALESCE(cs.booked, 0)  - (COALESCE(cs.onHold , 0))) available "
				+ " FROM exam.sas_timetable st, exam.executive_examcenter ec, exam.executive_exam_center_slot_mapping cs "
				+ " where st.PrgmStructApplicable = ?"
				+ " and st.examMonth = cs.month and st.examYear = cs.year "
				+ " and ec.centerId = cs.examcenterId "
				+ " and st.examMonth = ? and st.examYear = ? "
				+ " and st.examMonth = ec.month "
				+ " and st.examYear = ec.year "
				+ " and st.date = cs.date "
				+ " and st.startTime = cs.startTime "
				+ " and st.enrollmentYear = ec.batchYear "
				+ " and st.enrollmentMonth = ec.batchMonth "
				+ " and st.enrollmentYear= ? "
				+ " and st.enrollmentMonth = ? "
				+ " order by ec.city asc";



		allCentersList = jdbcTemplate.query(sql, new Object[] {programStructure, month, year,student.getEnrollmentYear(),student.getEnrollmentMonth()},
				new BeanPropertyRowMapper(ExamCenterBean.class));


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle(examLiveCurrently, student);

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate()
			+ examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}

			int capacity = 0;

			if (examCenter.getCapacity() != null
					&& !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		for (String subject : subjects) {// Any of the available center can be
			// taken for any subject
			subjectAvailableCentersMap.put(subject, availableCentersList);
		}
		return subjectAvailableCentersMap;
	}

	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAvailableCentersForExecutiveExam(
			ExecutiveExamOrderBean examLiveCurrently, StudentExamBean student) {


		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();

		String year = examLiveCurrently.getYear();
		String month = examLiveCurrently.getMonth();
		String sapId = student.getSapid();
		//String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();


		sql = " SELECT *, (cs.capacity - COALESCE(cs.booked, 0)  - (COALESCE(cs.onHold , 0))) available "
				+ " FROM exam.sas_timetable st, exam.executive_examcenter ec, exam.executive_exam_center_slot_mapping cs "
				+ " where st.PrgmStructApplicable = ?"
				+ " and st.examMonth = cs.month and st.examYear = cs.year "
				+ " and ec.centerId = cs.examcenterId "
				+ " and st.examMonth = ? and st.examYear = ? "
				+ " and st.examMonth = ec.month "
				+ " and st.examYear = ec.year "
				+ " and st.date = cs.date "
				+ " and st.startTime = cs.startTime "
				+ " and st.enrollmentYear = ec.batchYear "
				+ " and st.enrollmentMonth = ec.batchMonth "
				+ " and st.enrollmentYear = cs.batchYear "
				+ " and st.enrollmentMonth = cs.batchMonth "
				+ " and st.enrollmentYear= ? "
				+ " and st.enrollmentMonth = ? "
				+ " order by cs.date, cs.startTime asc";



		allCentersList = jdbcTemplate.query(sql, new Object[] {programStructure, month, year,student.getEnrollmentYear(),student.getEnrollmentMonth()},
				new BeanPropertyRowMapper(ExamCenterBean.class));


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle(examLiveCurrently, student);

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate()
			+ examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}

			int capacity = 0;

			if (examCenter.getCapacity() != null
					&& !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}


		return availableCentersList;
	}

	@Transactional(readOnly = true)
	private HashMap<String, Integer> getConfirmedBookingForCurrentCycle(
			ExecutiveExamOrderBean examLiveCurrently, StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String year = examLiveCurrently.getYear();
		String month = examLiveCurrently.getMonth();


		String sql = "SELECT concat(eb.centerId, eb.examDate, eb.examTime) as uniqueKey, "
				+ " count(*) as bookedCount FROM exam.executive_exam_bookings eb where "
				+ " eb.booked = 'Y' "
				+ " and eb.year = ? and  eb.month = ?"
				+ " group by eb.centerId, eb.examDate, eb.examTime ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] {year, month}, new BeanPropertyRowMapper(
						ExamBookingTransactionBean.class));

		HashMap<String, Integer> centerDateTimeBookingMap = new HashMap<>();
		for (ExamBookingTransactionBean bean : bookingList) {
			String key = bean.getUniqueKey();
			int booked = Integer.parseInt(bean.getBookedCount());
			centerDateTimeBookingMap.put(key, booked);


		}

		return centerDateTimeBookingMap;
	}

	public List<ExamBookingTransactionBean> saveExamBooking(final List<ExamBookingTransactionBean> bookingsList) {
		try{

			ExamBookingTransactionBean bookingBean = bookingsList.get(0);
			jdbcTemplate = new JdbcTemplate(dataSource);



			String sql = "INSERT INTO exam.executive_exam_bookings "
					+ " ( sapId, booked,subject,programStructApplicable,program,"
					+ "	  sem,centerId,examDate,examTime,examEndTime,"
					+ "	  year,month,createdBy,createdDate,lastModifiedBy,"
					+ "	  lastModifiedDate)"
					+ " VALUES (?,?,?,?,?"
					+ "			,?,?,?,?,?"
					+ "			,?,?,?,sysdate(),?"
					+ "			,sysdate())";



			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
				//int index = 1;
				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingsList.get(i);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getBooked());
					ps.setString(3, bean.getSubject());
					ps.setString(4, bean.getPrgmStructApplicable());
					ps.setString(5, bean.getProgram());
					ps.setString(6, bean.getSem());
					ps.setString(7, bean.getCenterId());
					ps.setString(8, bean.getExamDate());
					ps.setString(9, bean.getExamTime());
					ps.setString(10, bean.getExamEndTime());
					ps.setString(11, bean.getYear());
					ps.setString(12, bean.getMonth());
					ps.setString(13, bean.getSapid());
					ps.setString(14, bean.getSapid());
				}
				public int getBatchSize() {
					return bookingsList.size();
				}
			});


			sql = "Update exam.executive_exam_center_slot_mapping"
					+ " set booked = COALESCE(booked, 0) + 1"
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return bookingsList.size();
				}
			});



			sql = "Select * from exam.executive_exam_bookings "
					+ " where sapid = ? "
					+ " and booked = 'Y' "
					+ " and month = ?"
					+ " and year = ? "
					+ " order by examDate";


			List<ExamBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {bookingBean.getSapid(), bookingBean.getMonth(), bookingBean.getYear()}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
			return completeBookings;

		}catch(Exception e){
			
			throw e;
		}


	}

	@Transactional(readOnly = true)
	public HashMap<String, String> getExamCenterIdNameMap() {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.executive_examcenter ec  ";

			///	+ " WHERE ec.year = ? and ec.month = ? ";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,
				new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));

		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(),
					bean.getExamCenterName() + "," + bean.getLocality() + ","
							+ bean.getCity());
		}

		return examCenterIdNameMap;
	}

	public void releaseExistingBookings(ExecutiveExamOrderBean examLiveCurrently,  List<ExamBookingTransactionBean> subjectsToReleaseList, StudentExamBean student) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String subjectCommaSeparated = getCommaSepareatedSubjects(subjectsToReleaseList);
		try{
			//Query existing bookings
			String sql = " select * from exam.executive_exam_bookings eb  "
					+ " where eb.year = ? and eb.month = ? "
					+ " and eb.sapid = ?   and booked = 'Y' "
					+ " and subject in ("+subjectCommaSeparated+") ";

			ArrayList<ExamBookingTransactionBean> bookedSubjects = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, 
					new Object[] {examLiveCurrently.getYear(), examLiveCurrently.getMonth(), student.getSapid()}, 
					new BeanPropertyRowMapper(ExamBookingTransactionBean.class));


			//Mark those bookings released 
			final ArrayList<ExamBookingTransactionBean> bookingsList = bookedSubjects;
			sql = "update exam.executive_exam_bookings "
					+ " set booked = 'RL'"
					+ " where sapid = ? and subject = ? and year = ? and month = ? ";

			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
				int index = 1;
				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingsList.get(i);
					ps.setString(index++, bean.getSapid());
					ps.setString(index++, bean.getSubject());
					ps.setString(index++, bean.getYear());
					ps.setString(index++, bean.getMonth());
				}
				public int getBatchSize() {
					return bookingsList.size();
				}
			});



			//Change Booked count
			sql = "Update exam.executive_exam_center_slot_mapping"
					+ " set booked = COALESCE(booked, 0) - 1"
					+ " where examcenterid = ? "
					+ " and date = ? "
					+ " and starttime = ?";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					ExamBookingTransactionBean bean = bookingsList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}
				public int getBatchSize() {
					return bookingsList.size();
				}
			});


		}catch(Exception e){
			
		}



		

	}
	/*
	//For executiveExamBookngReport Start
	public List<ExamBookingTransactionBean> get 
	//For executiveExamBookngReport End
*/	
	
	@Transactional(readOnly = true)
	public boolean isHallTicketLive(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select count(*) from exam.executive_examorder eo , exam.students s"
				+ " where eo.order in "
				+ " (select max(ee.order) from exam.executive_examorder ee where ee.timeTableLive = 'Y' )"
				+ " and eo.hallTicketStartDate <= sysdate()  "
				+ " and eo.hallTicketEndDate >= sysdate()"
				+ " and s.sapid = ? "
				+ " and eo.acadYear = s.enrollmentYear "
				+ " and eo.acadMonth = s.enrollmentMonth ";
				

		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		} catch (Exception e) {
			
		}

		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * SELECT count(*)
  FROM exam.executive_exam_bookings
  where sapid=77217167227 
	and examDate='2018-07-28'
    and examTime='12:30:00';
	 * */
	
	//check if seat already booked at given date time start
	@Transactional(readOnly = true)
	public boolean isSeatAlreadyBookedForDateNTime(String sapid,String date, String time,String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(*) " + 
				"  FROM exam.executive_exam_bookings " + 
				"  where sapid=?  " + 
				"	and examDate=? " + 
				"   and examTime=? "
				+ " and subject <> ? ";
				

		int noOfRows = 0;
		try {
			noOfRows = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid,date,time,subject},Integer.class);
		} catch (Exception e) {
			
		}

		if(noOfRows > 0){
			return true;
		}else{
			return false;
		}
	}
	//check if seat already booked at given date time end
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM exam.executive_exam_bookings a, exam.executive_examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(executive_examorder.order) from exam.executive_examorder where timeTableLive='Y' ) "
				+ " order by a.examDate, a.examTime, a.subject asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	
	@Transactional(readOnly = true)
	public List<ExecutiveTimetableBean> getTimetableForExecutiveExamGivenSubjects(ArrayList<String> subjects,StudentExamBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);

			String subjectCommaSeparated = getCommaSepareatedExecutiveSubjects(subjects);

			String sql = "SELECT a.*,les.subject "
					+ " FROM exam.sas_timetable a,exam.live_exam_subjects les, exam.executive_examorder b, exam.students s"
					+ " where  a.examYear = b.year "
					+ " and  a.examMonth = b.month "
					+ " and s.enrollmentYear = b.acadYear "
					+ " and s.enrollmentMonth = b.acadMonth "
					+ " and les.examYear = b.year "
					+ " and les.examMonth = b.month "
					+ " and les.program = s.program "
					+ " and s.sapid = ? "
					+ " and les.subject in ("+subjectCommaSeparated+")"
					+ " and b.order = (Select max(executive_examorder.order) from exam.executive_examorder where timeTableLive='Y') "
					+ " order by a.date, a.startTime asc";
			

			this.timeTableList = jdbcTemplate.query(sql, new Object[]{student.getSapid()},new BeanPropertyRowMapper(ExecutiveTimetableBean.class));

		
		return this.timeTableList;

	}
	
	@Transactional(readOnly = true)
	public String getMostRecentTimeTablePeriodExecutive(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.executive_examorder where executive_examorder.order = (Select max(executive_examorder.order) from exam.executive_examorder where timeTableLive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderExamBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderExamBean.class));
		for (ExamOrderExamBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT ec.centerId, ec.examCenterName, ec.locality, ec.city, ec.state, ec.address "
					+ " FROM exam.executive_examcenter ec, exam.executive_examorder eo   "
					+ " WHERE eo.year = ec.year and  eo.month = ec.month"
					+ " and eo.order = (Select max(executive_examorder.order) from exam.executive_examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,new Object[] {},new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, ExamCenterBean> examCenterIdNameMap = new HashMap<String, ExamCenterBean>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean);
		}
		return examCenterIdNameMap;
	}
	
	
	@Async
	public void saveHallTicketDownloaded(final String sapid, final ArrayList<ExamBookingTransactionBean> subjectsBooked) {

		String sql = "Update exam.executive_exam_bookings"
				+ " set htDownloaded = 'Y' , htDownloadDateTime = sysdate() "
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
}
