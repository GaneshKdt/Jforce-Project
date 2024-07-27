package com.nmims.daos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.StudentsTestDetailsStudentPortalBean;
import com.nmims.beans.TimeboundExamBookingBean;

@Repository("supportDao")
public class SupportDao  implements ISupportDAO{

	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = false)
	@Override
	public void addUgConsentOption(String optionId,String sapid) {
		String sql = "INSERT INTO portal.ugc_consentform (`sapid`, `consent_option`,`createdDate`) VALUES (?, ?,current_timestamp());";
		jdbcTemplate.update(sql,new Object[] {sapid,optionId});
	}
	
	@Override
	@Transactional(readOnly = true)
	public int checkStudentHasGivenConsent(String sapid) {
		String SQL = "Select count(*) from portal.ugc_consentform where  sapid = ? ";
		return (int) jdbcTemplate.queryForObject(SQL,new Object[] {sapid },Integer.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeboundExamBookingBean> getExamBookingDetails(String sapId) throws SQLException{
		List<TimeboundExamBookingBean> examBookingList = new ArrayList<TimeboundExamBookingBean>();
		//Prepare SQL query
		StringBuilder GET_EXAM_BOOKING = new StringBuilder("SELECT b.year,b.month,b.term as sem,pss.subject,pr.amount ")
		.append("FROM exam.mba_wx_bookings b INNER JOIN lti.student_subject_config ssc ON ssc.id = b.timeboundId ")
		.append("INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id ")
		.append("INNER JOIN exam.mba_wx_payment_records pr ON pr.id=b.paymentRecordId ")
		.append("WHERE b.sapid = ? AND b.bookingStatus='Y' ");
		
		//Execute SQL query and return booking  
		examBookingList = jdbcTemplate.query(GET_EXAM_BOOKING.toString(),
				new BeanPropertyRowMapper<TimeboundExamBookingBean>(TimeboundExamBookingBean.class), sapId);
		//return result
		return examBookingList;
	}

	@Transactional(readOnly = true)
	public List<String> getAllapplicableTimeboundIds(String sapid) throws SQLException{
		List<String> applicableTimeboundsIds = new ArrayList<>();
		
		final String ALL_TIMEBOUNDIDS_SQL = "select timebound_subject_config_id from lti.timebound_user_mapping where userId =? ";
		
		applicableTimeboundsIds = jdbcTemplate.query(ALL_TIMEBOUNDIDS_SQL, new SingleColumnRowMapper<>(String.class),sapid);
		
		return applicableTimeboundsIds;
	}
	
	@Transactional(readOnly = true)
	public List<StudentsTestDetailsStudentPortalBean> getIAScoresForStudentSubject(String sapid, String timeboundIds)
			throws SQLException {
		List<StudentsTestDetailsStudentPortalBean> iaScoreList = new ArrayList<StudentsTestDetailsStudentPortalBean>();
		String sql = "SELECT tst.*, pss.id,pss.subject,t.maxScore,t.testName,t.startDate, t.endDate , COALESCE(tst.score,0) as scoreInInteger , t.showResultsToStudents, ssc.acadYear as acadsYear, ssc.acadMonth as acadsMonth "
				+ " FROM exam.test_student_testdetails tst "
				+ " INNER JOIN exam.test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId"
				+ " INNER JOIN acads.sessionplan_module spm ON spm.id = ttcm.referenceId"
				+ " INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId"
				+ " INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId "
				+ " INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id"
				+ " INNER JOIN exam.test t ON t.id= tst.testId" + " WHERE stm.timeboundId in ( "+timeboundIds+" ) and tst.sapid=? ";

		iaScoreList = jdbcTemplate.query(sql, new Object[] { sapid },
				new BeanPropertyRowMapper<StudentsTestDetailsStudentPortalBean>(
						StudentsTestDetailsStudentPortalBean.class));

		return iaScoreList;
	}
	
}
