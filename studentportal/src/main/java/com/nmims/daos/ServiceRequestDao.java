package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.ConfigurationStudentPortal;
import com.nmims.beans.ConsumerProgramStructureStudentPortal;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.FeedbackBean;
import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.PaymentOptionsStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.SRTeeRevaluationReportBean;
import com.nmims.beans.ServiceRequestCustomPDFContentBean;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.ServiceRequestType;
import com.nmims.beans.SrTypeMasterKeyMapping;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VerifyContactDetailsBean;
import com.nmims.controllers.BaseController;
import com.nmims.dto.StudentSrDTO;
import com.nmims.helpers.PaginationHelper;

@Repository("serviceRequestDao")
public class ServiceRequestDao extends BaseDAO {

	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	public BaseController ob = new BaseController();
	
	public void setBaseDataSource() {
	 this.baseDataSource = this.dataSource;
		
	}
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private final String REFUND = "Refund";
	////Executation time is 3 milliseconds.
	@Transactional(readOnly = true)
	public ArrayList<String> getActiveSRTypes() {
		String sql = "select serviceRequestName from portal.service_request_types where active = 'Y' "
				+ " and startTime <= sysdate() and endTime >= sysdate() order by serviceRequestName asc";
		return (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<SRTeeRevaluationReportBean> getSRTeeCurrentReportData(String sapid){
		String sql="SELECT " + 
				"   distinct serv_req.sapId as 'sapid'," + 
				"   serv_req.id as 'serviceRequest_no'," +
				"   pg_assessment.assessmentId as 'testID' ,  "+
				"  marks.sem as 'sem'," + 
				"    pg_assessment.sifyCode as 'subCode'," + 
				"    pg_assessment.name as 'assessment_Name'," + 
				"    CONCAT(scheduleinfo.firstname," + 
				"            '  '," + 
				"            scheduleinfo.lastname) AS 'testTaker_name'," + 
				"    met_marks.student_name as 'studentName'," + 
				"    met_marks.month AS 'examMonth'," + 
				"    met_marks.year AS 'examYear'," + 
				"   scheduleinfo.emailId AS 'testTaker_EmailId'," + 
				"   scheduleinfo.acessKey AS 'testInvitation_Key'," + 
				"   marks.program as 'program'," + 
				"  met_marks.section4_marks AS 'original_Sec_4_Marks'," + 
				"   marks.total as 'original_Total'," + 
				" marks.roundedTotal as 'score_Rounded'" + 
				"   FROM" + 
				"    portal.service_request serv_req" + 
				"        JOIN" + 
				"    exam.pg_assessment pg_assessment ON pg_assessment.name = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        JOIN" + 
				"    exam.exams_pg_scheduleinfo_mettl scheduleinfo ON serv_req.sapId = scheduleinfo.sapid" + 
				"        AND scheduleinfo.subject = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        AND pg_assessment.sifyCode = scheduleinfo.sifySubjectCode" + 
				"        " + 
				"        JOIN" + 
				"    exam.mettl_marks met_marks ON serv_req.sapid = met_marks.sapid" + 
				"        AND met_marks.subject = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        JOIN" + 
				"    exam.exams_schedule_mettl exam_schedule ON exam_schedule.schedule_id = met_marks.schedule_id" + 
				"    and exam_schedule.schedule_accessKey = scheduleinfo.acessKey" + 
				"   join" + 
				" exam.online_marks marks on serv_req.sapid= marks.sapid and" + 
				" marks.subject=SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"      ']'," + 
				"      1) where serv_req.sapId=?" ;
		ArrayList<SRTeeRevaluationReportBean> srTeeReportData = (ArrayList<SRTeeRevaluationReportBean>)jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(SRTeeRevaluationReportBean.class));
		return srTeeReportData;
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SRTeeRevaluationReportBean> getSRTeeHistoryReportData(String sapid){
		String sql="SELECT " + 
				"   distinct serv_req.sapId," + 
				"   serv_req.id as 'serviceRequest_no'," + 
				"   pg_assessment.assessmentId as 'testID'  , "+
				"    marks.sem as 'sem'," + 
				"    pg_assessment.sifyCode as 'subCode'," + 
				"    pg_assessment.name as 'assessment_Name'," + 
				"    CONCAT(scheduleinfo.firstname," + 
				"            '  '," + 
				"            scheduleinfo.lastname) AS 'testTaker_name'," + 
				"    met_marks.student_name as 'studentName'," + 
				"    met_marks.month AS 'examMonth'," + 
				"    met_marks.year AS 'examYear'," + 
				"    scheduleinfo.emailId AS 'testTaker_EmailId'," + 
				"    scheduleinfo.acessKey AS 'testInvitation_Key'," + 
				"   marks.program as 'program'," + 
				"    met_marks.section4_marks AS 'original_Sec_4_Marks'," + 
				"   marks.total as 'original_Total'," + 
				"    marks.roundedTotal as 'score_Rounded'" + 
				"     FROM" + 
				"    portal.service_request serv_req" + 
				"        JOIN" + 
				"    exam.pg_assessment pg_assessment ON pg_assessment.name = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        JOIN" + 
				"    exam.exams_pg_scheduleinfo_history_mettl scheduleinfo ON serv_req.sapId = scheduleinfo.sapid" + 
				"        AND scheduleinfo.subject = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        AND pg_assessment.sifyCode = scheduleinfo.sifySubjectCode" + 
				"        " + 
				"        JOIN" + 
				"    exam.mettl_marks met_marks ON serv_req.sapid = met_marks.sapid" + 
				"        AND met_marks.subject = SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"            ']'," + 
				"            1)" + 
				"        JOIN" + 
				"    exam.exams_schedule_history_mettl exam_schedule ON exam_schedule.schedule_id = met_marks.schedule_id" + 
				"    and exam_schedule.schedule_accessKey = scheduleinfo.acessKey" + 
				"   join" + 
				" exam.online_marks marks on serv_req.sapid= marks.sapid and" + 
				" marks.subject=SUBSTRING_INDEX(SUBSTRING_INDEX(serv_req.description, '[', - 1)," + 
				"      ']'," + 
				"      1)" + 
				"        where serv_req.sapId=?;" ;
		ArrayList<SRTeeRevaluationReportBean> srTeeReportData = (ArrayList<SRTeeRevaluationReportBean>)jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(SRTeeRevaluationReportBean.class));
		return srTeeReportData;
		
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getSRPaymentFromMerchantId(String merchantId){
		String sql = " select * from portal.service_request where trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		
		ArrayList<AdhocPaymentStudentPortalBean> listOfFailedPaymentSR = (ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		////System.out.println("listOfFailedPaymentSR-->"+listOfFailedPaymentSR);
		return listOfFailedPaymentSR;
	}
	@Transactional(readOnly = true)
	public HashMap<String, StudentStudentPortalBean> getAllStudents() {
		String sql = "Select * from exam.students ";
		ArrayList<StudentStudentPortalBean> students = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		
		HashMap<String, StudentStudentPortalBean> studentsMap = new HashMap<>();
		for (StudentStudentPortalBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}
		
		return studentsMap;
	}
	@Transactional(readOnly = false)
	public boolean insertIntoOtpVerification(VerifyContactDetailsBean verifyBean) {
		try {
			String sql = "insert into portal.otp_verification (sapid,sr_id,created_date,sub_type,otp) values(?,?,now(),?,?)";
			jdbcTemplate.update(sql,new Object[] {verifyBean.getSapid(),verifyBean.getSrid(),verifyBean.getSubType(),verifyBean.getOtp()});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Transactional(readOnly= true)
	public VerifyContactDetailsBean checkOtp(VerifyContactDetailsBean verifyBean) {
		String sql ="SELECT " + 
				"    *   " + 
				"  FROM   " + 
				"    portal.otp_verification" + 
				" WHERE " + 
				"    sapid = ?  " + 
				"and sub_type= ?  "+
				"  ORDER BY created_date DESC  " + 
				"  LIMIT 1 ";
		VerifyContactDetailsBean bean= jdbcTemplate.queryForObject(sql,new Object[] {verifyBean.getSapid(),verifyBean.getSubType()} ,new BeanPropertyRowMapper<>(VerifyContactDetailsBean.class));
		return bean;
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getListOfMarksheetsIssuedTwiceInTwentyFourHours(){
		String sql = "SELECT * FROM portal.service_request WHERE createdDate > DATE_SUB(NOW(), INTERVAL 1 HOUR) "
				    +" and serviceRequestType = 'Issuance of Marksheet' and tranStatus = 'Payment Successful' "
				    + " group by sapid "
				    + " having count(sapid) > 2 ";
		ArrayList<ServiceRequestStudentPortal> getListOfMarksheetsIssuedTwiceInTwentyFourHours = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		return getListOfMarksheetsIssuedTwiceInTwentyFourHours;
	}
	
	@Transactional(readOnly = false)
	public void insertMarksheetRecordsBatchWise(ArrayList<ServiceRequestStudentPortal> listOfSR) throws SQLException{
		String sql = "INSERT INTO portal.service_request (sapid,serviceRequestType,amount,issued,year,month,sem,requestStatus,serviceRequestType) VALUES(?,?,?,?,?,?,?,?,?) ";
		Connection conn = dataSource.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		final int batchSize = 1000;
		int count = 0;
		for(ServiceRequestStudentPortal service : listOfSR){
			ps.setString(1,service.getSapId());
			ps.setString(2,service.getServiceRequestType());
			ps.setString(3,service.getAmount());
			ps.setString(4,service.getIssued());
			ps.setString(5,service.getYear());
			ps.setString(6,service.getMonth());
			ps.setString(7, service.getSem());
			ps.setString(8,service.getRequestStatus());
			ps.setString(9,service.getServiceRequestType());
			ps.addBatch();
			if(++count%batchSize==0){
				ps.executeBatch();
			}
		}
		ps.executeBatch(); // insert remaining records
		ps.close();
		conn.close();
	}
	
	// query all exambooking payment records
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getExamBookingPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM exam.exambookings  where trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getExamBookingHistoryPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM exam.exambookings_history  where trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}

	
	// query all exambooking payment records
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getMBAWXExamBookingPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM exam.mba_wx_payment_records where paymentType = 'Exam Booking' and trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}

	
	// query all exambooking payment records
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getMBAXExamBookingPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM exam.mba_x_payment_records where paymentType = 'Exam Booking' and trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}
	
	//Added by Vikas 01/08/2016//
	@Transactional(readOnly = false)
	public void saveFeedBack(final FeedbackBean feedBack,final StudentStudentPortalBean student){
		{
			final String sql = "INSERT INTO portal.feedback "
					+ " (comments,category,createdDate,rating,createdBy,lastModifiedBy , lastModifiedDate)"
					+ " VALUES "
					+ " (?,?,sysdate(),?,?,?,sysdate())";
			

			PreparedStatementCreator psc = new PreparedStatementCreator() {
				
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1,feedBack.getComments());
					ps.setString(2,feedBack.getCategory());
					ps.setString(3,feedBack.getRating());
					ps.setString(4,student.getSapid());
					ps.setString(5,feedBack.getLastModifiedBy());
					return ps;
				}
			};
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(psc, keyHolder);
		}
	}
	//Added by Vikas on 03/08/2016//
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getBonafideIssuedCertificateBySapid(String sapid){
		String sql = "select * from portal.service_request "
				+ "where sapid = ? "
				+ "and serviceRequestType = 'Issuance of Bonafide' and requestStatus !='Cancelled' "
				+ "and (tranStatus ='Free' or tranStatus ='Payment Successful') " ;
		ArrayList<ServiceRequestStudentPortal> getBonafideIssuedCertificateList = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		return getBonafideIssuedCertificateList;
	}
	@Transactional(readOnly = true)
	public List<FeedbackBean> queryAllFeedBacks(){
		String sql = "SELECT * from portal.feedback";
		List<FeedbackBean> listOfFeedBacks = jdbcTemplate.query(sql,new BeanPropertyRowMapper(FeedbackBean.class));
		if(listOfFeedBacks.size()>0){
			return listOfFeedBacks;
		}
		return null;
	}
	//end//
	@Transactional(readOnly = true)
	public HashMap<String, CenterStudentPortalBean> getICLCMap() {
		String sql = "SELECT * FROM exam.centers ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterStudentPortalBean> centers = (ArrayList<CenterStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterStudentPortalBean.class));
		
		HashMap<String, CenterStudentPortalBean> icLcMap = new HashMap<>();
		for (CenterStudentPortalBean center : centers) {
			icLcMap.put(center.getCenterCode(), center);
		}
		
		return icLcMap;
	}
	//Executation time 4 milliseconds.
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getSingleStudentsData(String sapid) {

		StudentStudentPortalBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students s where "
					+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";

			////System.out.println("SQL = "+sql);

			student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));

			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());

		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			////System.out.println("Student Details not found"+sapid);
			
		}
		////System.out.println("Returning... "+ student);
		return student;
	}
	
	// Insert refund Payment record 
	@Transactional(readOnly = false)
	public int InsertRefundTransactions(AdhocPaymentStudentPortalBean bean) throws Exception
	{
		int noOfRowsUpdated = 0;
		try
		{
			String sql ="INSERT INTO exam.ad_hoc_Refund "
						 +" (feesType,description,amount,createdDate,createdBy,lastModifiedBy, lastModifiedDate, merchantRefNo,sapId,paymentOption,status,refundId,refId) "
					     +" VALUES "
					     +" (?,?,?,sysdate(),?,?, sysdate(),?,?,?,?,?,?)";
			//System.out.println(" ===========>>>>> refund amount : " + bean.getRefundAmount());
			//System.out.println(" ===========>>>>> getCreatedBy amount : " + bean.getCreatedBy());
			//System.out.println(" ===========>>>>> getLastModifiedBy amount : " + bean.getLastModifiedBy());
			//System.out.println(" ===========>>>>> getMerchantRefNo amount : " + bean.getMerchantRefNo());
			//System.out.println(" ===========>>>>> getSapId amount : " + bean.getSapId());
			//System.out.println(" ===========>>>>> getPaymentOption amount : " + bean.getPaymentOption());
			//System.out.println(" ===========>>>>> getRefId amount : " + bean.getRefId());
			//System.out.println(" ===========>>>>> getStatus amount : " + bean.getStatus());
			noOfRowsUpdated = jdbcTemplate.update(sql,new Object[]{bean.getFeesType(),bean.getDescription(),
					bean.getRefundAmount(),bean.getCreatedBy(),bean.getLastModifiedBy(),bean.getMerchantRefNo(),bean.getSapId(),bean.getPaymentOption(),bean.getStatus(),bean.getRefundId(),bean.getRefId()});
		}catch(Exception e)
		{
			throw e;
		}
		return noOfRowsUpdated;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getListOfInitiatedRefundTransaction() {
		String sql = "SELECT *, amount AS `refundAmount` FROM exam.ad_hoc_refund where status='pending'";
		return  (ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,String> mapOfTrackIdAndRefundDescription(){
		String sql = "select * from exam.ad_hoc_refund ";
		HashMap<String,String> mapOfTrackIdAndRefundDescription = new HashMap<String,String>();
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdHocRefundRecords = (ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		if(listOfAdHocRefundRecords!=null && listOfAdHocRefundRecords.size()>0){
			for(AdhocPaymentStudentPortalBean bean : listOfAdHocRefundRecords){
				mapOfTrackIdAndRefundDescription.put(bean.getMerchantRefNo(),bean.getDescription());
			}
		}
		return mapOfTrackIdAndRefundDescription;
	}
	@Transactional(readOnly = false)
	public int updateRefundTransactions(String trackId, ServiceRequestStudentPortal bean) throws Exception{
		int noOfRowsUpdated = 0;
		try {		
		

		String sql = "Update portal.service_request"
				+ " set ResponseMessage = ? ,"
				+ " transactionID = ? ,"
				+ " merchantRefNo = ? ,"
				+ " amount = ? ,"
				+ " responseCode = '0' ,"
				+ " isFlagged = ? ,"
				+ " paymentID = ? ,"
				+ " respTranDateTime = ? ,"
				+ " refundStatus = ? ,"
				+ " refundAmount = ? ,"
				+ " tranStatus = '"+REFUND+"' "
				+ " where  trackId = ? ";
				

		////System.out.println("SQL = "+sql);

		noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
				bean.getTransactionType(),
				bean.getTransactionID(),
				bean.getMerchantRefNo(),
				bean.getAmount(),
				bean.getIsFlagged(),
				bean.getPaymentID(),
				bean.getRespTranDateTime(),
				bean.getRefundStatus(),
				bean.getRefundAmount(),
				bean.getTrackId()
		});

		////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);
		
		} catch (Exception e) {
			throw e;
		}

		
		return noOfRowsUpdated;
	}
	@Transactional(readOnly = true)
	public String getPendingAmountFromSapId(String sapid){
		String sql = "SELECT pendingAmount from portal.ad_hoc_payment where sapid = ?";
		String pendingAmount = "";
		try{
			pendingAmount = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, String.class);
			////System.out.println("Pending Amount-->"+pendingAmount);
		}catch(Exception e){
//			e.printStackTrace();
		}
		return pendingAmount;
	}
	
	/**
	 * Check service request already exists or not
	 * */
	@Transactional(readOnly = true)
	public boolean checkServiceRequestExist(String trackID,String sapid,String year,String month,String sem) {
		//System.out.println("inside check Service Request function");
		try {
			//System.out.println("trackId : " + trackID + " | sapId : " + sapid + " | year : " + year + " | month : " +  month +" | sem" + sem);
			final String SQL = "select id from portal.service_request where trackId = ? and sapid = ? and year = ? and month = ? and sem = ?";
			int id = jdbcTemplate.queryForObject(SQL, new Object[] {trackID,sapid,year,month,sem},Integer.class);
			if(id == 0 || id < 0) {
				return false;
			}else { 
				return true;
			}
		}
		catch(EmptyResultDataAccessException e) {
			return false;
		}
	}
	@Transactional(readOnly = false)
	public void insertServiceRequest(final ServiceRequestStudentPortal sr) {
		try {
		final String sql = "INSERT INTO portal.service_request "
				+ " (serviceRequestType,sapId,trackId,amount,tranDateTime,tranStatus,requestStatus,"
				+ "description,createdBy,createdDate,lastModifiedBy,lastModifiedDate, category, hasDocuments, informationForPostPayment,"
				+ " year, month, sem, postalAddress,additionalInfo1,noOfCopies,issued,modeOfDispatch,srAttribute,paymentOption, houseNoName,street,locality,city,state,country,pin,device)"
				+ " VALUES "
				+ " (?,?,?,?,sysdate(),?,?,?,?,sysdate(),?,sysdate(), ?, ?, ?, ?, ?,?,?,?,?,?,?,?,? , ?,?,?,?,?,?,?,?)";
		//System.out.println("================>>>>>>>>>>>>>>>> inserting into service request table");
		String tempSRType = sr.getServiceRequestType();
		if(tempSRType.equals(ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION)){
			tempSRType = ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION;//So that same search works for both online and offline
		}
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		final String srType = tempSRType;
		PreparedStatementCreator psc = new PreparedStatementCreator() { 
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, srType);
				ps.setString(2, sr.getSapId());
				ps.setString(3, sr.getTrackId());
				ps.setString(4, sr.getAmount());
				ps.setString(5, sr.getTranStatus());
				ps.setString(6, sr.getRequestStatus());
				ps.setString(7, sr.getDescription());
				ps.setString(8, sr.getCreatedBy());
				ps.setString(9, sr.getLastModifiedBy());
				ps.setString(10, sr.getCategory());
				ps.setString(11, sr.getHasDocuments());
				ps.setString(12, sr.getInformationForPostPayment());
				ps.setString(13, sr.getYear());
				ps.setString(14, sr.getMonth());
				ps.setString(15, sr.getSem());
				ps.setString(16, sr.getPostalAddress());
				ps.setString(17, sr.getAdditionalInfo1());
				ps.setString(18, sr.getNoOfCopies());
				ps.setString(19, sr.getIssued());
				ps.setString(20, sr.getModeOfDispatch());
				ps.setString(21, sr.getSrAttribute());
				ps.setString(22, sr.getPaymentOption());
				ps.setString(23, sr.getHouseNoName());
				ps.setString(24, sr.getStreet());
				//ps.setString(25, sr.getLandMark());
				ps.setString(25, sr.getLocality());
				ps.setString(26, sr.getCity());
				ps.setString(27, sr.getState());
				ps.setString(28, sr.getCountry());
				ps.setString(29, sr.getPin());
				ps.setString(30, sr.getDevice());
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();


		jdbcTemplate.update(psc, keyHolder);
		
		Long id = keyHolder.getKey().longValue();
//		System.out.println("SR Id = "+id);
		sr.setId(id);
		insertServiceRequestStatusHistory(sr,"Create");
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Transactional(readOnly = false)
	public void InsertAdHocPaymentRequest(final AdhocPaymentStudentPortalBean adhocPaymentBean)
	{
		final String sql ="INSERT INTO portal.ad_hoc_payment "
			      +" (paymenttype,description,amount,createdDate,createdBy,emailId,mobile,trackId,year,month,sapId) "
			      +" VALUES "
			      +" (?,?,?,sysdate(),?,?,?,?,?,?,?)";
	    jdbcTemplate.update(sql, new Object[]{adhocPaymentBean.getPaymentType(),adhocPaymentBean.getDescription(),
	    		adhocPaymentBean.getAmount(),adhocPaymentBean.getCreatedBy(),adhocPaymentBean.getEmailId(),adhocPaymentBean.getMobile(),
	    		adhocPaymentBean.getTrackId(),
	    		                                adhocPaymentBean.getYear(),
	    		                                adhocPaymentBean.getMonth(),adhocPaymentBean.getSapId()});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String>  getSubjectsAppearedForSemesterMessageList(ServiceRequestStudentPortal sr, String examMode){
		ArrayList<String> semesterAppearedMessageList = new ArrayList<String>();
		String sql = "SELECT " + 
				"	count(*)" + 
				"FROM" + 
				"    exam.marks m," + 
				"    exam.examorder eo," + 
				"    exam.passfail p" + 
				"    " + 
				"WHERE" + 
				"    m.sapId = ?" + 
				"		AND m.year = eo.year" + 
				"        AND m.month = eo.month" + 
				"        and p.sapid = m.sapid" + 
				"        and p.subject = m.subject" + 
				"        AND " + 
				"        (" + 
				"			(m.month = ?" + 
				"			AND m.year = ?)" + 
				"            or" + 
				"            (" + 
				"				p.resultProcessedMonth = ?" + 
				"                and p.resultProcessedYear = ?"
				+ "				 and m.sem = p.sem " + 
				"            )" + 
				"        )" + 
				"        AND m.sem = ?";
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}
		
		
		try{
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord1-->"+sr.getMarksheetDetailRecord1());*/
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord1());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
			
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
			
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
			/*////System.out.println("SERVICE BEAN AFTER PIPED VALUES-->"+serviceBean.getMonth()+"<-->"+serviceBean.getYear()+"<-->"+serviceBean.getSem());*/
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
			
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
		}
		
		
		return semesterAppearedMessageList;
		}catch(Exception e){
			////System.out.println("");
			//System.out.println("getSubjectsAppearedForSemesterMessageList :"+e.getMessage());
			return null;
		}
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getMarksheetPrintedCount(ServiceRequestStudentPortal sr){
		String sql = "select count(*) from portal.service_request_history where sapid = ? and status ='Pending' and sem = ? and year = ? and month = ? ";
		ArrayList<String> listOfMarksheetsPrintedMessage = new ArrayList<String>();
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			////System.out.println("Entered getMarksheetPrintedCount loop sem1 loop");
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord1());
			int countOfSem1PrintedMarkheetBean = 0;
			countOfSem1PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			////System.out.println("count of prints"+countOfSem1PrintedMarkheetBean);
			if(countOfSem1PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" for "+serviceBean.getYear());
			}
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
			
			int countOfSem2PrintedMarkheetBean = 0;
			countOfSem2PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem2PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" for "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
			
			int countOfSem3PrintedMarkheetBean = 0;
			countOfSem3PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem3PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" for "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
			
			int countOfSem4PrintedMarkheetBean = 0;
			countOfSem4PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem4PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" and "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord5()!=null && !"".equals(sr.getMarksheetDetailRecord5())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord5());
			
			int countOfSem4PrintedMarkheetBean = 0;
			countOfSem4PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem4PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" and "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord6()!=null && !"".equals(sr.getMarksheetDetailRecord6())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord6());
			
			int countOfSem6PrintedMarkheetBean = 0;
			countOfSem6PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem6PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" and "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord7()!=null && !"".equals(sr.getMarksheetDetailRecord7())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord7());
			
			int countOfSem7PrintedMarkheetBean = 0;
			countOfSem7PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem7PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" and "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		if(sr.getMarksheetDetailRecord8()!=null && !"".equals(sr.getMarksheetDetailRecord8())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord8());
			
			int countOfSem8PrintedMarkheetBean = 0;
			countOfSem8PrintedMarkheetBean = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getSem(),serviceBean.getYear(),serviceBean.getMonth()},Integer.class);
			if(countOfSem8PrintedMarkheetBean > 0){
				listOfMarksheetsPrintedMessage.add("Marksheet is printed for Sem "+ serviceBean.getSem()  +" for Month "+serviceBean.getMonth()+" and "+serviceBean.getYear()+" for Sem "+serviceBean.getSem());
			}
		}
		////System.out.println("size of printed marksheets-->"+listOfMarksheetsPrintedMessage.size());
		return listOfMarksheetsPrintedMessage;
		
	}
	public String generateSemesterAppearedMessage(ServiceRequestStudentPortal sr,int count){
		if(count==0){
			return "You have not appeared for Semester " + sr.getSem() + " in Year " + sr.getYear()
					+ " and month " + sr.getMonth();
		}else{
			return "";
		}
		
	}
	@Transactional(readOnly = true)
	public ArrayList<String> resultDeclaredMessage(ServiceRequestStudentPortal sr){
		ArrayList<String> resultDeclaredMessage = new ArrayList<String>();
		String sql = "select live from exam.examorder where year=? and month =?";
		
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord1());
			String live = (String)jdbcTemplate.queryForObject(sql, new Object[]{bean.getYear(),bean.getMonth()}, String.class);
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord2());
			String live = (String)jdbcTemplate.queryForObject(sql, new Object[]{bean.getYear(),bean.getMonth()}, String.class);
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord3());
			String live = (String)jdbcTemplate.queryForObject(sql, new Object[]{bean.getYear(),bean.getMonth()}, String.class);
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord4());
			String live = (String)jdbcTemplate.queryForObject(sql, new Object[]{bean.getYear(),bean.getMonth()}, String.class);
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		return resultDeclaredMessage;
	}
	public String generateMarksheetIssuedMessage(ServiceRequestStudentPortal sr,int count){
		if(count==0){
			return "";
		}else{
			return "";
		}
		
	}
	public void getYearMonthAndSemFromPipedValues(ServiceRequestStudentPortal service,String marksheetRecord){
		//System.out.println("inside getYearMonthAndSemFromPipedValues---1---"+marksheetRecord);
		String [] arr = marksheetRecord.split("\\|");
		//System.out.println("ARRAY SIZE-->"+arr.length);
		//System.out.println("SPLIT ARRAY PARAMETERS-->"+arr[0]+"<-->"+arr[1]+"<-->"+arr[2]);
		service.setYear(arr[0]);
        service.setMonth(arr[1]);
		service.setSem(arr[2]);
		//System.out.println("inside getYearMonthAndSemFromPipedValues---2---"+service);
		try { //added for mobile
			String id = arr[3]; 
			service.setId(Long.parseLong(id));
		} catch (Exception e) {}
	}
	
	
	//Will generate the marksheet summary for 
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> listOfMarksheetDetailsAndAmountToBePaidMBAWX(ServiceRequestStudentPortal sr,int secondMarksheetIssuedAmount,HttpServletRequest request,boolean isCertificate) {
		ArrayList<ServiceRequestStudentPortal> listOfMarksheetDetailsAndAmountToBePaid = new ArrayList<ServiceRequestStudentPortal>();
		int totalFeesForMarksheetPayment = 0;//set total fees of marksheet payment//
		
		
		String sql =  " Select count(srh.id) from portal.service_request_history srh, portal.service_request sr "
					+ " where srh.sapId = ? and srh.year = ? and srh.month = ? and srh.sem = ? and srh.serviceRequestType = ?  "
					+ " and sr.tranStatus in ('Payment Successful','Free') "
					+ " and srh.serviceRequestId = sr.id ";
		
		totalFeesForMarksheetPayment = (int) (totalFeesForMarksheetPayment + Double.parseDouble(sr.getCourierAmount()));
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord1());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			 //Value set just to show on the marksheet summary
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			////System.out.println("count for getMarksheetDetailRecord1 "+count);
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));//Set the amount as courier plus the cost of marksheet if duplicate
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));//This value is set only for diplay purpose//
				service.setAdditionalInfo1("Duplicate");
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount; 
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord2());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord2 "+count);*/
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
				service.setAdditionalInfo1("Duplicate");
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord3-->"+sr.getMarksheetDetailRecord3());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord3());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord3 "+count);*/
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
				service.setAdditionalInfo1("Duplicate");
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
			
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord4());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			
			
			
			
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		
		
		if(sr.getMarksheetDetailRecord5()!=null && !"".equals(sr.getMarksheetDetailRecord5())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord5());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());  
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			
			
			
			
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		
		if(sr.getMarksheetDetailRecord6()!=null && !"".equals(sr.getMarksheetDetailRecord6())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord6());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			//service.setLandMark(sr.getLandMark());  
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Gradesheet Issued for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Gradesheet Issuance for Term "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		
		////System.out.println("TOTAL MARKSHEET PAYMENT VALUE----->"+totalFeesForMarksheetPayment);
		
		request.getSession().setAttribute("amount", isCertificate ?  ob.generateAmountBasedOnCriteria(String.valueOf(totalFeesForMarksheetPayment), "GST"):String.valueOf(totalFeesForMarksheetPayment));
		
		return listOfMarksheetDetailsAndAmountToBePaid;
	}
	
	
	//Will generate the marksheet summary for
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> listOfMarksheetDetailsAndAmountToBePaid(ServiceRequestStudentPortal sr,int secondMarksheetIssuedAmount,HttpServletRequest request,boolean isCertificate) {
		ArrayList<ServiceRequestStudentPortal> listOfMarksheetDetailsAndAmountToBePaid = new ArrayList<ServiceRequestStudentPortal>();
		int totalFeesForMarksheetPayment = 0;//set total fees of marksheet payment//
		
		
		String sql =  " Select count(srh.id) from portal.service_request_history srh, portal.service_request sr "
					+ " where srh.sapId = ? and srh.year = ? and srh.month = ? and srh.sem = ? and srh.serviceRequestType = ?  "
					+ " and sr.tranStatus in ('Payment Successful','Free') "
					+ " and srh.serviceRequestId = sr.id ";
		
		totalFeesForMarksheetPayment = (int) (totalFeesForMarksheetPayment + Double.parseDouble(sr.getCourierAmount()));
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord1());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			 //Value set just to show on the marksheet summary
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			////System.out.println("count for getMarksheetDetailRecord1 "+count);
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));//Set the amount as courier plus the cost of marksheet if duplicate
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));//This value is set only for diplay purpose//
				service.setAdditionalInfo1("Duplicate");
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount; 
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord2());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord2 "+count);*/
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
				service.setAdditionalInfo1("Duplicate");
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord3-->"+sr.getMarksheetDetailRecord3());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord3());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord3 "+count);*/
			if(count >0){
				service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
				service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
				service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
				totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
				service.setAdditionalInfo1("Duplicate");
			}else{
				service.setAmount(sr.getCourierAmount());
				service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
				service.setAmountToBeDisplayedForMarksheetSummary("0");
				service.setAdditionalInfo1("New");
			}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
			
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord4());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		
		if(sr.getMarksheetDetailRecord5()!=null && !"".equals(sr.getMarksheetDetailRecord5())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord5());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		
		if(sr.getMarksheetDetailRecord6()!=null && !"".equals(sr.getMarksheetDetailRecord6())){
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord4-->"+sr.getMarksheetDetailRecord4());*/
			getYearMonthAndSemFromPipedValues(service,sr.getMarksheetDetailRecord6());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		if(!StringUtils.isEmpty(sr.getMarksheetDetailRecord7())) {
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(service, sr.getMarksheetDetailRecord7());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		

		if(!StringUtils.isEmpty(sr.getMarksheetDetailRecord8())) {
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(service, sr.getMarksheetDetailRecord8());
			service.setSapId(sr.getSapId());
			service.setIssued(sr.getIssued());
			service.setModeOfDispatch(sr.getModeOfDispatch());
			service.setServiceRequestType(sr.getServiceRequestType());
			service.setCourierAmount(sr.getCourierAmount());
			service.setPostalAddress(sr.getPostalAddress());
			service.setLandMark(sr.getLandMark());
			service.setPin(sr.getPin());
			service.setLocality(sr.getLocality());
			service.setStreet(sr.getStreet());
			service.setHouseNoName(sr.getHouseNoName());
			service.setCity(sr.getCity());
			service.setState(sr.getState());
			service.setCountry(sr.getCountry());
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{service.getSapId(),service.getYear(),service.getMonth(),service.getSem(),service.getServiceRequestType()},Integer.class);
			/*////System.out.println("count for getMarksheetDetailRecord4 "+count);*/
			if(count >0){
			service.setDescriptionToBeShownInMarksheetSummary("Duplicate Marksheet Issued for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+secondMarksheetIssuedAmount);
			service.setAmount(String.valueOf(secondMarksheetIssuedAmount+Integer.parseInt(sr.getCourierAmount())));
			service.setAmountToBeDisplayedForMarksheetSummary(String.valueOf(secondMarksheetIssuedAmount));
			totalFeesForMarksheetPayment +=secondMarksheetIssuedAmount;
			service.setAdditionalInfo1("Duplicate");
		}else{
			service.setAmount(sr.getCourierAmount());
			service.setDescriptionToBeShownInMarksheetSummary("Marksheet Issuance for Semester "+service.getSem()+" for the month "+service.getMonth()+" and Year "+service.getYear()+". Amount :-"+service.getAmount());
			service.setAmountToBeDisplayedForMarksheetSummary("0");
			service.setAdditionalInfo1("New");
		}
			listOfMarksheetDetailsAndAmountToBePaid.add(service);
		}
		
		////System.out.println("TOTAL MARKSHEET PAYMENT VALUE----->"+totalFeesForMarksheetPayment);
		
		sr.setTotalAmountToBePayed( String.valueOf(totalFeesForMarksheetPayment));
		sr.setAmount(String.valueOf(totalFeesForMarksheetPayment));
		request.getSession().setAttribute("amount", isCertificate ?  ob.generateAmountBasedOnCriteria(String.valueOf(totalFeesForMarksheetPayment), "GST"):String.valueOf(totalFeesForMarksheetPayment));
		
		return listOfMarksheetDetailsAndAmountToBePaid;
	}
	@Transactional(readOnly = false)
	public void insertServiceRequestHistory(final ServiceRequestStudentPortal sr) {
		final String sql = "INSERT INTO portal.service_request_history "
				+ " (serviceRequestType,sapId,createdBy,createdDate,lastModifiedBy , lastModifiedDate, year, month, sem, serviceRequestId)"
				+ " VALUES "
				+ " (?,?,?,sysdate(),?,sysdate(),?, ?,?,?)";
		
		//System.out.println("===============>>>>>>>>>>>>>>>> insertIntoService Request history");
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, sr.getServiceRequestType());
				ps.setString(2, sr.getSapId());
				ps.setString(3, sr.getSapId());
				ps.setString(4, sr.getSapId());
				ps.setString(5, sr.getYear());
				ps.setString(6, sr.getMonth());
				ps.setString(7, sr.getSem());
				ps.setString(8, sr.getId()+"");
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, keyHolder);
	}
	@Transactional(readOnly = false)
	public void updateAdHocPayment(final AdhocPaymentStudentPortalBean adhocPaymentBean)
	{
		String sql = "Update portal.ad_hoc_payment"
				+ " set "
				+ " paymenttype = ? ,"
				+ " description = ? ,"
				+ " amount = ? ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() ,"
				+ " createdBy = ? ,"
				+ " createdDate = sysdate(), lastModifiedBy=? , lastModifiedDate= sysdate() "
				+ " where sapid = ?";

		////System.out.println("SQL = "+sql);

		int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
				adhocPaymentBean.getPaymentType(),
				adhocPaymentBean.getDescription(),
				adhocPaymentBean.getAmount(),
				adhocPaymentBean.getLastModifiedBy(),
				adhocPaymentBean.getCreatedBy(),
				adhocPaymentBean.getSapId()
		});
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertOnlineTransaction(ServiceRequestStudentPortal responseBean) {
		//System.out.println("===========>>>>>> inside insertOnlineTransaction function");
		try{

			String sql = "insert into portal.onlinetransactions "
					+ "( ResponseMessage ,transactionID ,requestID ,merchantRefNo ,secureHash ,respAmount , description, "
					+ " responseCode ,respPaymentMethod, isFlagged , paymentID , error , respTranDateTime ,   "
					+ "  sapid , trackId ) values "
					+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			////System.out.println("SQL = "+sql);
			//System.out.println("==========> sql : " + responseBean.getSecureHash());
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
					responseBean.getSapId(),
					responseBean.getTrackId()
			});

			////System.out.println("insertOnlineTransaction: noOfRowsUpdated = "+noOfRowsUpdated);
		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	@Transactional(readOnly= true)
	public int getContactDetailSrId() {
		String sql= " SELECT id FROM portal.service_request_types WHERE serviceRequestName = 'Change In Contact Details' ";
		int id= jdbcTemplate.queryForObject(sql, Integer.class);
		return id;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertOnlineTransactionForAdhocPayment(AdhocPaymentStudentPortalBean responseBean) {
		try{

			String sql = "insert into portal.onlinetransactions "
					+ "( ResponseMessage ,transactionID ,requestID ,merchantRefNo ,secureHash ,respAmount , description, "
					+ " responseCode ,respPaymentMethod, isFlagged , paymentID , error , respTranDateTime ,   "
					+ "  sapid , trackId ) values "
					+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			////System.out.println("SQL = "+sql);

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
					responseBean.getSapId(),
					responseBean.getTrackId()
			});

			////System.out.println("insertOnlineTransaction: noOfRowsUpdated = "+noOfRowsUpdated);
		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	@Transactional(readOnly = false)
	public void updateAmountForReOpenedSRCase(String srId){
		String sql = "Update portal.service_request set amount = 100 where id = '"+srId+"'";
		int noOfRowsUpdated = jdbcTemplate.update(sql);
		////System.out.println(noOfRowsUpdated);
		
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSRTransactionDetails(ServiceRequestStudentPortal responseBean) {
		try{

			String sql = "Update portal.service_request"
					+ " set "
					+ " requestStatus = ? ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " tranStatus = '"+ServiceRequestStudentPortal.TRAN_STATUS_SUCCESSFUL+"',  "
					+ " bankName = ?"
					+ " where sapid = ? "
					+ " and trackId = ?";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getRequestStatus(),
					responseBean.getResponseMessage(),
					responseBean.getTransactionID(),
					responseBean.getRequestID(),
					responseBean.getMerchantRefNo(),
					responseBean.getSecureHash(),
					responseBean.getRespAmount(),
					responseBean.getResponseCode(),
					responseBean.getRespPaymentMethod(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					responseBean.getBankName(),
					responseBean.getSapId(),
					responseBean.getTrackId()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamBookingAmount(AdhocPaymentStudentPortalBean responseBean) {
		
		try{

			String sql = "Update exam.exambookings"
					+ " set "
					+ " amount = amount + " + responseBean.getRespAmount() + " "
					+ " where sapid = ? "
					+ " and year = '2016' "
					+ " and month = 'Dec' "
					+ " and booked = 'Y' ";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getSapId()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateExamBookingAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		
		try{

			String sql = "Update exam.exambookings"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getMerchantRefNo()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}


	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateMBAWXExamBookingAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		try{
			String sql = "Update exam.mba_wx_payment_records"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";

			////System.out.println("SQL = "+sql);
			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
				responseBean.getMerchantRefNo()
			});
		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateMBAXExamBookingAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		try{
			String sql = "Update exam.mba_x_payment_records"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";

			////System.out.println("SQL = "+sql);
			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
				responseBean.getMerchantRefNo()
			});
		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}
	@Transactional(readOnly = false)
	public int updatePendingRefundTransaction(Long id,String status) {
		try {
			String sql = "update exam.ad_hoc_refund set status = ? where id = ?";
			return jdbcTemplate.update(sql, new Object[] { status,id });
		}
		catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			return 0;
		}
	}
	
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateServiceRequestAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		
		try{

			String sql = "Update portal.service_request"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";
					

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getMerchantRefNo()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updatePendingPaymentTransactionDetails(AdhocPaymentStudentPortalBean responseBean) {
		try{

			String sql = "Update portal.ad_hoc_payment"
					+ " set "
					+ " requestStatus = ? ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " trackId = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " pendingAmount = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " lastModifiedBy = ? ,"
					+ " lastModifiedDate = sysdate() ,"
					+ " createdBy = ? ,"
					+ " createdDate = sysdate(), "
					+ " tranStatus = '"+ServiceRequestStudentPortal.TRAN_STATUS_SUCCESSFUL+"'  "


					+ " where sapid = ?";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getRequestStatus(),
					responseBean.getResponseMessage(),
					responseBean.getTransactionID(),
					responseBean.getRequestID(),
					responseBean.getMerchantRefNo(),
					responseBean.getTrackId(),
					responseBean.getSecureHash(),
					responseBean.getRespAmount(),
					responseBean.getResponseCode(),
					responseBean.getRespPaymentMethod(),
					responseBean.getPendingAmount(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					responseBean.getSapId(),
					responseBean.getSapId(),
					responseBean.getSapId()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAdhocPaymentTransactionDetails(AdhocPaymentStudentPortalBean responseBean) {
		try{

			String sql = "Update portal.ad_hoc_payment"
					+ " set "
					+ " requestStatus = ? ,"
					+ " ResponseMessage = ? ,"
					+ " transactionID = ? ,"
					+ " requestID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " secureHash = ? ,"
					+ " respAmount = ? ,"
					+ " responseCode = ? ,"
					+ " respPaymentMethod = ? ,"
					+ " pendingAmount = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " lastModifiedBy = ? ,"
					+ " lastModifiedDate = sysdate() ,"
					+ " createdBy = ? ,"
					+ " createdDate = sysdate(), "
					+ " paymentOption = ?,"
					+ " tranStatus = '"+ServiceRequestStudentPortal.TRAN_STATUS_SUCCESSFUL+"'  "

					+ " where trackId = ?";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getRequestStatus(),
					responseBean.getResponseMessage(),
					responseBean.getTransactionID(),
					responseBean.getRequestID(),
					responseBean.getMerchantRefNo(),
					responseBean.getSecureHash(),
					responseBean.getRespAmount(),
					responseBean.getResponseCode(),
					responseBean.getRespPaymentMethod(),
					responseBean.getPendingAmount(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					responseBean.getEmailId(),
					responseBean.getEmailId(),
					responseBean.getPaymentOption(),
					responseBean.getTrackId()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSRTransactionDetailsFromAPI(ServiceRequestStudentPortal responseBean) {
		try{

			String sql = "Update portal.service_request"
					+ " set "
					+ " requestStatus = ? ,"
					+ " transactionID = ? ,"
					+ " merchantRefNo = ? ,"
					+ " respAmount = ? ,"
					+ " isFlagged = ? ,"
					+ " paymentID = ? ,"
					+ " error = ? ,"
					+ " respTranDateTime = ? ,"
					+ " lastModifiedBy = ?,"
					+ " lastModifiedDate = sysdate(),"
					+ " tranStatus = '"+ServiceRequestStudentPortal.TRAN_STATUS_SUCCESSFUL+"',  "
					+ " respPaymentMethod = ?,"
					+ " responseCode = ?,"
					+ " bankName = ?,"
					+ " responseMessage = ?"
					+ " where id = ?";

			//System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getRequestStatus(),
					responseBean.getTransactionID(),
					responseBean.getMerchantRefNo(),
					responseBean.getRespAmount(),
					responseBean.getIsFlagged(),
					responseBean.getPaymentID(),
					responseBean.getError(),
					responseBean.getRespTranDateTime(),
					"SRTransactionAPI",
					responseBean.getRespPaymentMethod(),
					responseBean.getResponseCode(),
					responseBean.getBankName(),
					responseBean.getResponseMessage(),
					responseBean.getId()
					
			});

			////System.out.println("updateSRTransactionDetailsFromAPI(): noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}

	}
	@Transactional(readOnly = true)
	public String getMostRecentAssignmentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')";

		List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		for (ExamOrderStudentPortalBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	
	//To be changed after multi-level evaluation is over
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getAStudentsMostRecentAssignmentMarks(String sapId) {

		String sql = "select a.*, c.finalReason as reason, c.markedForRevaluation, c.toBeEvaluated  from exam.marks a, exam.examorder b, exam.assignmentsubmission c "
				+ " where b.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y') "
				+ " and a.sapid = ? and a.year = b.year and a.month = b.month "
				+ " and a.year = c.year and a.month = c.month and a.sapid = c.sapid and a.subject = c.subject "
				+ " order by sem, subject asc";

		////System.out.println("SQL = "+sql);

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade(){
		String sql = " SELECT * from portal.ad_hoc_payment where sapid <> '' and tranStatus = 'Payment Successful' ";
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = new ArrayList<AdhocPaymentStudentPortalBean>();
		try{
			 listOfAdhocPaymentsMade = (ArrayList<AdhocPaymentStudentPortalBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		}catch(Exception e){
//			e.printStackTrace();
		}
		return listOfAdhocPaymentsMade;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMadeByTrackId(String trackId){
		String sql = " SELECT * from portal.ad_hoc_payment where sapid <> '' and tranStatus = 'Payment Successful' and trackId in ("+ trackId +") ";
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = new ArrayList<AdhocPaymentStudentPortalBean>();
		try{
			 listOfAdhocPaymentsMade = (ArrayList<AdhocPaymentStudentPortalBean>) jdbcTemplate.query(sql,new Object[] {}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		}catch(Exception e){
//			e.printStackTrace();
		}
		return listOfAdhocPaymentsMade;
	}
	
	@Transactional(readOnly = true)
	public AdhocPaymentStudentPortalBean getAdhocPaymentBeanByTrackId(String trackId) {
		String sql = " SELECT * from portal.ad_hoc_payment where trackId  = ?";
		AdhocPaymentStudentPortalBean adhocPaymentBean = new AdhocPaymentStudentPortalBean();
		try{
			adhocPaymentBean =  (AdhocPaymentStudentPortalBean) jdbcTemplate.queryForObject(sql,new Object[] {trackId},
					new BeanPropertyRowMapper<AdhocPaymentStudentPortalBean>(AdhocPaymentStudentPortalBean.class));
		}catch(Exception e){
//			e.printStackTrace();
		}
		return adhocPaymentBean;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMadeByDateRange(String startDate,String endDate){
		String sql = " SELECT * from portal.ad_hoc_payment where sapid <> '' and tranStatus = 'Payment Successful' and createdDate >= ? and createdDate <= ?";
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = new ArrayList<AdhocPaymentStudentPortalBean>();
		try{
			 listOfAdhocPaymentsMade = (ArrayList<AdhocPaymentStudentPortalBean>) jdbcTemplate.query(sql,new Object[] {startDate,endDate}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		}catch(Exception e){
//			e.printStackTrace();
		}
		return listOfAdhocPaymentsMade;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocRefundPaymentsMade(){
		String sql = " SELECT * from exam.ad_hoc_Refund where sapid <> ''";
		ArrayList<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = new ArrayList<AdhocPaymentStudentPortalBean>();
		try{
			 listOfAdhocPaymentsMade = (ArrayList<AdhocPaymentStudentPortalBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		}catch(Exception e){
//			e.printStackTrace();
		}
		return listOfAdhocPaymentsMade;
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getServiceRequestHistoryList(ServiceRequestStudentPortal sr, String authorizedCenterCodes){
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "select id, sapid, year, month, status, sem,serviceRequestType  from portal.service_request_history where sapid = ? ";
		
		if(sr.getServiceRequestType()!=null && sr.getServiceRequestType()!="") {
			sql=sql+ "and serviceRequestType='"+sr.getServiceRequestType()+"'";
		}
		ArrayList<ServiceRequestStudentPortal> serviceRequestHistoryList  = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sr.getSapId()}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		return serviceRequestHistoryList;
	}
	@Transactional(readOnly = false)
	public void updateStudentAssignmentMarks(final List<StudentMarksBean> studentAssignmentMarks){
		String sql = "update exam.marks set oldAssignmentScore = ? where subject = ? and sapid = ? and year = ? and month = ? ";
		int [] results = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				StudentMarksBean marksBean = studentAssignmentMarks.get(i);
				ps.setString(1,marksBean.getAssignmentscore());
				ps.setString(2, marksBean.getSubject());
				ps.setString(3,marksBean.getSapid());
				ps.setString(4,marksBean.getYear());
				ps.setString(5,marksBean.getMonth());
			}
			
			@Override
			public int getBatchSize() {
				
				return studentAssignmentMarks.size();
			}
		});
		////System.out.println("Old Assignement Marks Archived marks table");	
	
	}
	
	@Transactional(readOnly = false)
	public void updateStudentWrittenMarks(final List<StudentMarksBean> studentAssignmentMarks){
		String sql = "update exam.marks set oldWrittenScore = ? where subject = ? and sapid = ? and year = ? and month = ? ";
		int [] results = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				StudentMarksBean marksBean = studentAssignmentMarks.get(i);
				ps.setString(1,marksBean.getWritenscore());
				ps.setString(2, marksBean.getSubject());
				ps.setString(3,marksBean.getSapid());
				ps.setString(4,marksBean.getYear());
				ps.setString(5,marksBean.getMonth());
			}
			
			@Override
			public int getBatchSize() {
				
				return studentAssignmentMarks.size();
			}
		});
		////System.out.println("Old Assignement Marks Archived to marks table");	
	
	}
	@Transactional(readOnly = true)
	public PageStudentPortal<ServiceRequestStudentPortal> getServiceRequestPage(int pageNo, int pageSize,	ServiceRequestStudentPortal sr, String authorizedCenterCodes) {
		ArrayList<Object> parameters = new ArrayList<Object>();

		String sql =  " SELECT s.firstName,s.middleName,s.lastName,s.fatherName,s.motherName,s.husbandName,isLateral,programChanged,programStatus, "
					+ " s.emailId,s.mobile,s.abcId,sr.id, sr.serviceRequestType, sr.sapId, sr.trackId, sr.amount, sr.tranDateTime, sr.service_requestcol, sr.tranStatus, "
					+ " sr.requestStatus, sr.transactionID, sr.requestID, sr.merchantRefNo, sr.secureHash, sr.respAmount, sr.description, sr.responseCode, "
					+ " sr.respPaymentMethod, sr.isFlagged, sr.paymentID, sr.responseMessage, sr.error, sr.respTranDateTime, sr.createdBy, sr.createdDate, sr.lastModifiedBy, "
					+ " sr.lastModifiedDate, sr.category, sr.informationForPostPayment, sr.hasDocuments, sr.year, sr.month, sr.sem, sr.postalAddress, sr.requestClosedDate, "
					+ " REPLACE(sr.additionalInfo1, 'Others : ', '') as additionalInfo1, sr.cancellationReason, sr.certificateNumber, sr.certificateGenerationDate, sr.refundStatus, sr.refundAmount, sr.noOfCopies, "
					+ " sr.issued,sr.modeOfDispatch, sr.srAttribute, sr.paymentOption, sr.bankName, sr.landMark, sr.houseNoName, sr.street, sr.locality, sr.pin, sr.city, sr.state, sr.country, sr.device ,"
					+ " s.landMark as `Old_LandMark`"
					+ " FROM exam.students s , portal.service_request sr "
					+ " where sr.sapId = s.sapId "
					+ " and serviceRequestType <> '' "
					+ " and s.sem =(select max(s2.sem) from exam.students s2 where s2.sapid= s.sapid) ";
		String countSql = "SELECT count(*) FROM portal.service_request sr, exam.students s "
				+ "where sr.sapId = s.sapId "
				+ "and s.sem =(select max(s2.sem) from exam.students s2 where s2.sapid= s.sapid)";

		if( sr.getId() != null ){
			sql = sql + " and id = ? ";
			countSql = countSql + " and id = ? ";
			parameters.add(sr.getId());
		}
		if( sr.getRequestID() != null ){
			sql = sql + " and requestID = ? ";
			countSql = countSql + " and requestID = ? ";
			parameters.add(sr.getRequestID());
		}
		if( sr.getServiceRequestType() != null &&   !("".equals(sr.getServiceRequestType()))){
			sql = sql + " and ServiceRequestType = ? ";
			countSql = countSql + " and ServiceRequestType = ? ";
			parameters.add(sr.getServiceRequestType());
		}
		if( sr.getSapId() != null &&   !("".equals(sr.getSapId()))){
			sql = sql + " and sr.SapId = ? ";
			countSql = countSql + " and sr.SapId = ? ";
			parameters.add(sr.getSapId());
		}
		if( sr.getTranStatus() != null &&   !("".equals(sr.getTranStatus()))){
			sql = sql + " and sr.TranStatus = ? ";
			countSql = countSql + " and sr.TranStatus = ? ";
			parameters.add(sr.getTranStatus());
		}
		if( sr.getRequestStatus() != null &&   !("".equals(sr.getRequestStatus()))){
			sql = sql + " and sr.RequestStatus = ? ";
			countSql = countSql + " and sr.RequestStatus = ? ";
			parameters.add(sr.getRequestStatus());
		}
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		sql = sql + " order by  sr.createdDate desc ";

		Object[] args = parameters.toArray();
		//System.out.println("In getServiceRequestPage SQL = "+sql);

		PaginationHelper<ServiceRequestStudentPortal> pagingHelper = new PaginationHelper<ServiceRequestStudentPortal>();
		PageStudentPortal<ServiceRequestStudentPortal> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));


		return page;
	}
	@Transactional(readOnly = false)
	public void markForRevaluation(String sapid, String subject) {
		String sql = "update exam.assignmentsubmission  asb, exam.examorder eo "
				+ " set markedForRevaluation = 'Y'  "
				+ " where asb.sapid = ? and asb.subject = ? "
				+ " and asb.year = eo.year and asb.month = eo.month "
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')" ;
		
		jdbcTemplate.update(sql, new Object[]{sapid, subject});
		
	}
	//Executation time is 3 milliseconds.
	@Transactional(readOnly = false)
	public int updateSrReqStatusCancelReason(Long srId, String requestStatus, String cancellationReason, String userId) {
		String sr_sql = "UPDATE portal.service_request " + 
						"SET requestStatus = ?, " + 
						"	cancellationReason = ?, " + 
						"	lastModifiedBy = ?, " + 
						"	lastModifiedDate = sysdate() " + 
						"WHERE id = ? "; 
				
		return jdbcTemplate.update(sr_sql, requestStatus, cancellationReason, userId, srId);
	}	
	@Transactional(readOnly = false)
	public int countOfSrHistoryBySrId(String srId) {
		String countSrHistory_sql = "SELECT count(*) " + 
									"FROM portal.service_request_history " + 
									"WHERE serviceRequestId = ? ";
		
		return jdbcTemplate.queryForObject(countSrHistory_sql, Integer.class, srId);
	}	
	@Transactional(readOnly = false)
	public int updateSrHistoryReqStatus(String srId, String requestStatus, String userId) {
		String srHistory_sql =  "UPDATE portal.service_request_history " + 
								"SET status = ?, " + 
								"	lastModifiedBy = ?, " + 
								"	lastModifiedDate = sysdate() " + 
								"WHERE serviceRequestId = ? " ;
		
		return jdbcTemplate.update(srHistory_sql, requestStatus, userId, srId);
	}	
	@Transactional(readOnly = false)
	public int insertSrHistoryReqStatus(String serviceRequestType, String sapid, String year, String month, String createdDate, String createdBy,
										String userId, String sem, String serviceRequestId, String status) {
		String srHistoryInsert_sql ="INSERT INTO portal.service_request_history (serviceRequestType, sapId, year, month, createdDate, createdBy, "
																				+ "lastModifiedBy, lastModifiedDate, sem, serviceRequestId, status) " + 
									"VALUES(?, ?, ?, ?, ?, ?, ?, sysdate(), ?, ?, ?)";
		
		return jdbcTemplate.update(srHistoryInsert_sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, serviceRequestType);
				ps.setString(2, sapid);
				ps.setString(3, year);
				ps.setString(4, month);
				ps.setString(5, createdDate);
				ps.setString(6, createdBy);
				ps.setString(7, userId);
				ps.setString(8, sem);
				ps.setString(9, serviceRequestId);
				ps.setString(10, status);
			}
		});
	}	
	@Transactional(readOnly = false)
	public ServiceRequestStudentPortal findSrById(Long id) {
		String findSr_sql = "SELECT amount, " + 
							"		description, " + 
							"		year, " + 
							"		month, " + 
							"		sem, " +
							"		createdBy, " +
							"    	createdDate, " + 
							"    	informationForPostPayment " + 
							"FROM portal.service_request " + 
							"WHERE id = ? ";
		
		return jdbcTemplate.queryForObject(findSr_sql, new BeanPropertyRowMapper<>(ServiceRequestStudentPortal.class), id);
	}	
	@Transactional(readOnly = false)
	public void saveServiceHistoryStatus(String status, Long id) {
		String sql = "Update portal.service_request_history "
				+ " set status = ? "
				+ " where id = ? ";
		jdbcTemplate.update(sql, new Object[]{status,id});
	}
	
	//Deprecated, use updateClosedDateForSR() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	@Transactional(readOnly = false)
	public void setClosedDateForServiceRequest(String requestStatus, Long id, String userId){
		
		String sql = "Update portal.service_request "
				+ " set requestClosedDate = sysdate() ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() "
				+ " where id = ? ";

		
		jdbcTemplate.update(sql, new Object[]{userId,id});
	}
	//Executation time is 7 milliseconds.
	@Transactional(readOnly = false)
	public int updateClosedDateForSR(Long srId, String userId) {
		String sql= "UPDATE portal.service_request " + 
					"SET requestClosedDate = sysdate(), " + 
					"	lastModifiedBy = ?, " + 
					"	lastModifiedDate = sysdate() " + 
					"WHERE id = ? ";

		return jdbcTemplate.update(sql, userId, srId);
	}
	@Transactional(readOnly = false)
	public void insertServiceRequestDocument(final ServiceRequestDocumentBean document) {
		final String sql = "INSERT INTO portal.servicerequest_documents "
				+ " (documentName,serviceRequestId,filePath)"
				+ " VALUES "
				+ " (?,?,?)";
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, document.getDocumentName());
				ps.setLong(2, document.getServiceRequestId());
				ps.setString(3, document.getFilePath());
				
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();


		jdbcTemplate.update(psc, keyHolder);

		Long id = keyHolder.getKey().longValue();
		////System.out.println("SR Id = "+id);
		document.setId(id);
		
	}
	@Transactional(readOnly = false)
	public void updateDocumentStatus(ServiceRequestStudentPortal sr) {
		String sql = "Update portal.service_request "
				+ " set hasDocuments = ? "
				+ " where id = ? ";
		jdbcTemplate.update(sql, new Object[]{sr.getHasDocuments(), sr.getId()});
		
	}
	@Transactional(readOnly = true)
	public List<ServiceRequestDocumentBean> getDocuments(Long serviceRequestId) {
		String sql = "SELECT * FROM portal.servicerequest_documents where serviceRequestId = ? ";
		List<ServiceRequestDocumentBean> documents = jdbcTemplate.query(sql, new Object[]{serviceRequestId}, new BeanPropertyRowMapper(ServiceRequestDocumentBean.class));
		return documents;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getAllSRTypes() {
		String sql = "select serviceRequestName from portal.service_request_types   order by serviceRequestName asc";
		return (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getAllActiveSRTypes() {
		String sql = "select serviceRequestName from portal.service_request_types  where  endTime >=Now() and startTime <= Now() and active = 'Y' order by serviceRequestName asc";
		return (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getFailedOrCurrentSubjects(String sapid) {
		String sql = "select ps.subject from exam.program_subject ps, exam.registration r, "
				+ " exam.students s where r.program = ps.program and r.sem = ps.sem "
				+ " and s.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and s.sapid = r.sapid and s.sapid = ? "
				+ " and ps.subject not in (select subject from exam.passfail where sapid = ? and isPass = 'Y') order by ps.sem";
		
		return (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid, sapid}, new SingleColumnRowMapper(String.class));
	}
	@Transactional(readOnly = true)
	public String getMostRecentOnlineResultPeriod() {
		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		for (ExamOrderStudentPortalBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	
	@Transactional(readOnly = true)
	public String getMostRecentOfflineResultPeriod() {
		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')";

		List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		for (ExamOrderStudentPortalBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getAStudentsMostRecentOnlineExamMarks(String sapid) {
		String sql = "select a.*  from exam.marks a, exam.examorder b "
				+ " where b.order = (Select max(examorder.order) from exam.examorder where live='Y') "
				+ " and a.sapid = ? and a.year = b.year and a.month = b.month "
				+ " order by sem, subject asc";

		////System.out.println("SQL = "+sql);

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{sapid}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getAStudentsMostRecentOfflineExamMarks(String sapid) {
		String sql = "select a.*  from exam.marks a, exam.examorder b "
				+ " where b.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y') "
				+ " and a.sapid = ? and a.year = b.year and a.month = b.month "
				+ " order by sem, subject asc";

		////System.out.println("SQL = "+sql);

		List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{sapid}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsMarksList;
	}
	@Transactional(readOnly = false)
	public void markOnlineTEEForRevaluation(String sapid, String subject) {
		String sql = "update exam.marks  m, exam.examorder eo "
				+ " set markedForRevaluation = 'Y'  "
				+ " where m.sapid = ? and m.subject = ? "
				+ " and m.year = eo.year and m.month = eo.month "
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where Live='Y')" ;
		
		jdbcTemplate.update(sql, new Object[]{sapid, subject});
		
	}
	@Transactional(readOnly = false)
	public void markOfflineTEEForRevaluation(String sapid, String subject) {
		String sql = "update exam.marks  m, exam.examorder eo "
				+ " set markedForRevaluation = 'Y'  "
				+ " where m.sapid = ? and m.subject = ? "
				+ " and m.year = eo.year and m.month = eo.month "
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')" ;
		
		jdbcTemplate.update(sql, new Object[]{sapid, subject});
		
	}
	@Transactional(readOnly = false)
	public void markForPhotocopy(String sapid, String subject) {
		String sql = "update exam.marks  m, exam.examorder eo "
				+ " set markedForPhotocopy = 'Y'  "
				+ " where m.sapid = ? and m.subject = ? "
				+ " and m.year = eo.year and m.month = eo.month "
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')" ;
		
		jdbcTemplate.update(sql, new Object[]{sapid, subject});
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getPaymentInitiatedSRList() {
		String sql = "Select * from portal.service_request where tranStatus = ?  and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800) ";
		return (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{ServiceRequestStudentPortal.TRAN_STATUS_INITIATED}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	@Transactional(readOnly = false)
	public void markServiceRequestFailed(ServiceRequestStudentPortal serviceRequest) {
		String sql = "Update portal.service_request "
				+ " set tranStatus = ? ,"
				+ " requestStatus = ? "
				+ " where id = ? ";
		
		jdbcTemplate.update(sql, new Object[]{ServiceRequestStudentPortal.TRAN_STATUS_EXPIRED, ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED, serviceRequest.getId()});
	}
	@Transactional(readOnly = true)
	public ServiceRequestStudentPortal findById(String id) {
		String sql = "Select * from portal.service_request where id = ? ";
		return (ServiceRequestStudentPortal)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> findByTrackId(String trackId) {
		String sql = "Select * from portal.service_request where trackId = ? ";
	return	(ArrayList<ServiceRequestStudentPortal>) jdbcTemplate.query(sql,new Object[]{trackId}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		
		//return (ServiceRequest)jdbcTemplate.queryForObject(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ServiceRequest.class));
	}
	@Transactional(readOnly = true)
	public List<ConfigurationStudentPortal> getCurrentConfigurationList() {
		String sql = "SELECT * FROM portal.service_request_types order by serviceRequestName asc";
		List<ConfigurationStudentPortal> currentConfList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConfigurationStudentPortal.class));
		return currentConfList;
	} 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateConfiguration(ConfigurationStudentPortal conf){
		String sql = "Update portal.service_request_types set "
				+ " startTime=? , "
				+ " endTime = ? ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() "
				+ " where serviceRequestName = ? ";

		jdbcTemplate.update(sql, new Object[] { 
				conf.getStartTime(),
				conf.getEndTime(),
				conf.getLastModifiedBy(),
				conf.getServiceRequestName()
		});
	}
	@Transactional(readOnly = true)
	public int getMarksheetIssuedCount(ServiceRequestStudentPortal sr) {
		
		String sql = "Select count(*) from portal.service_request_history where sapId = ? and year = ? and month = ? and sem = ? and serviceRequestType = ?  ";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(), sr.getYear(), sr.getMonth(), sr.getSem(), sr.getServiceRequestType()},Integer.class);
		return count;
	}
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getMarksheetsIssued(ServiceRequestStudentPortal sr) {
		
		String sql = "Select * from portal.service_request_history where sapId = ? and year = ? and month = ? and sem = ? and serviceRequestType = ?  ";
		List<ServiceRequestStudentPortal> listOfIssuedMarkSheets = jdbcTemplate.query(sql, new Object[]{sr.getSapId(), sr.getYear(), sr.getMonth(), sr.getSem(), sr.getServiceRequestType()},new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		return listOfIssuedMarkSheets;
	}
	@Transactional(readOnly = true)
	public int getDiplomaCertInintiatedCount(ServiceRequestStudentPortal sr) {
		
		String sql = "Select count(*) from portal.service_request where sapId = ? and serviceRequestType = ? and tranStatus = 'Initiated' ";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(), sr.getServiceRequestType()},Integer.class);
		return count;
	}
	@Transactional(readOnly = true)
	public int getDiplomaIssuedCount(ServiceRequestStudentPortal sr) {
		
		String sql = "Select count(*) from portal.service_request_history where sapId = ? and serviceRequestType = ?";
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(), sr.getServiceRequestType()},Integer.class);
		return count;
	}
	@Transactional(readOnly = true)
	public int getSubjectsAppearedForSemester(ServiceRequestStudentPortal sr, String examMode){
		String sql = "Select count(*) from exam.marks m, exam.examorder eo  "
				+ " where sapId = ?"
				+ " and m.year = eo.year "
				+ " and m.month = eo.month "
				+ " and m.month = ? "
				+ " and m.year = ? "
				+ " and m.sem = ? ";
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}
				
		
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),sr.getMonth(),sr.getYear(),sr.getSem()},Integer.class);
		return count;
	}
	@Transactional(readOnly = true)
	public int getSubjectsAppeared(ServiceRequestStudentPortal sr, String examMode) {
		String sql = "Select count(*) from exam.marks m, exam.examorder eo  "
				+ " where sapId = ?"
				+ " and m.year = eo.year "
				+ " and m.month = eo.month ";
				
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}
				
		
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId()},Integer.class);
		return count;
	}
	@Transactional(readOnly = true)
	public String isResultDeclared(ServiceRequestStudentPortal sr,String examMode){
		
		String sql = "select live from exam.examorder where year=? and month =?";
		try{
			
		
		String declareDate = (String)jdbcTemplate.queryForObject(sql, new Object[]{sr.getYear(),sr.getMonth()}, String.class);
		////System.out.println("declare Date-->"+declareDate);
		if("N".equals(declareDate)){
			return "Result is not declared for "+sr.getYear()+" Year and Month :-"+sr.getMonth();
		}else{
			return "Yes";
		}
		}catch(EmptyResultDataAccessException e){
			return "Exam was not conducted for year "+sr.getYear()+" and month "+sr.getMonth();
		}
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> listOfSubjectsAppeared(ServiceRequestStudentPortal sr, String examMode){
		String sql = "Select * from exam.marks m, exam.examorder eo  "
				+ " where sapId = ? "
				+ " and m.year = eo.year "
				+ " and m.month = eo.month ";
				
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y') order by sem asc";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y') order by sem asc";
		}
		ArrayList<ServiceRequestStudentPortal> listOfSubjectsAppeared = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sr.getSapId()}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		return listOfSubjectsAppeared;
 	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getStudentsSR(String sapid) {
		String sql = "Select * from portal.service_request where (requestStatus = 'Submitted' or  requestStatus = 'In Progress' or requestStatus = 'Closed' or requestStatus = 'Re-Opened') "
				+ " and sapid = ? ";
		return (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getStudentsPendingSR(String sapid) {
		String sql = "Select * from portal.service_request where (requestStatus = 'Submitted' or  requestStatus = 'In Progress' or requestStatus = 'Re-Opened') "
				+ " and sapid = ? ";
		return (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getStudentsClosedSR(String sapid) {
		String sql = "Select * from portal.service_request where (requestStatus = 'Closed') "
				+ " and sapid = ? ";
		return (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	@Transactional(readOnly = true)
	public HashMap<String,String> mapOfActiveSRTypesAndTAT(){
		String sql = "select * from portal.service_request_types where active = 'Y' order by serviceRequestName asc";
				
		HashMap<String,String> mapOfActiveSRTypesAndTAT = new HashMap<String,String>();
		ArrayList<ServiceRequestStudentPortal> activeSRTypes = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		if(activeSRTypes!=null && activeSRTypes.size()>0){
			
			for(ServiceRequestStudentPortal serviceBean : activeSRTypes){
				
				mapOfActiveSRTypesAndTAT.put(serviceBean.getServiceRequestName(),serviceBean.getTat());
			}
		}
		return mapOfActiveSRTypesAndTAT;
	}
	@Transactional(readOnly = true)
	public HashMap<String,String> getMapOfSRTypesAndTAT(){
		String sql = "select * from portal.service_request_types  order by serviceRequestName asc";
				
		HashMap<String,String> mapOfActiveSRTypesAndTAT = new HashMap<String,String>();
		ArrayList<ServiceRequestStudentPortal> activeSRTypes = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		if(activeSRTypes!=null && activeSRTypes.size()>0){
			
			for(ServiceRequestStudentPortal serviceBean : activeSRTypes){
				
				mapOfActiveSRTypesAndTAT.put(serviceBean.getServiceRequestName(),serviceBean.getTat());
			}
		}
		return mapOfActiveSRTypesAndTAT;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getPCPBookingPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM acads.pcpbookings  where trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		////System.out.println("PCP Booking Query--->"+sql);
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}
	@Transactional(readOnly = true)
	public ArrayList<AdhocPaymentStudentPortalBean> getAssignmentFeesPaymentFromMerchantId(String merchantId)
	{
		String sql = " SELECT * FROM exam.assignmentpayment  where trackId = ? and (respAmount <> '' || respAmount) limit 1 ";
		////System.out.println("Assignment Fees --->"+sql);
		ArrayList<AdhocPaymentStudentPortalBean> lstOfFailPayment =(ArrayList<AdhocPaymentStudentPortalBean>)jdbcTemplate.query(sql,new Object[]{merchantId}, new BeanPropertyRowMapper(AdhocPaymentStudentPortalBean.class));
		return lstOfFailPayment;
	}
	
	@Transactional(readOnly = false)
	public void saveDoucmentCollectedStatus(String issued, Long id, String userId) {
		String sql = "Update portal.service_request "
				+ " set issued = ? ,"
				+ " lastModifiedBy = ? "
				+ " where id = ? ";
		jdbcTemplate.update(sql, new Object[]{issued, userId, id});
	}
	
	@Transactional(readOnly = false)
	public int updateBonafideReason(String reason, Long id, String userId) {
		String sql = "UPDATE portal.service_request "
					+ "SET additionalInfo1 = ?, "
						+ "lastModifiedBy = ?, "
						+ "lastModifiedDate = sysdate() "
					+ "WHERE id = ? ";
		
		return jdbcTemplate.update(sql, reason, userId, id);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updatePCPBookingAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		
		try{

			String sql = "Update acads.pcpbookings"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getMerchantRefNo()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}


	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateAssignmentFeesAmountForRefund(AdhocPaymentStudentPortalBean responseBean) {
		
		try{

			String sql = "Update exam.assignmentpayment"
					+ " set "
					+ " amount = amount - " + responseBean.getRefundAmount() + " "
					+ " where trackId = ? ";

			////System.out.println("SQL = "+sql);

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					responseBean.getMerchantRefNo()
			});

			////System.out.println("noOfRowsUpdated = "+noOfRowsUpdated);

		}catch(Exception e){
//			e.printStackTrace();
			throw e;
		}


	}
	@Transactional(readOnly = true)
	public int getNumberOfMarkEntries(String sapid){
		String sql = "select count(*) from exam.marks where sapid = ? ";
		int countOfRecords = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		return countOfRecords;
	}
	@Transactional(readOnly = true)
	public int getNumberOfMarkEntriesMBAWX(String sapid){
		String sql = "select count(*) from exam.mba_passfail where sapid = ?  and isResultLive = 'Y'";
		int countOfRecords = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		System.out.println(countOfRecords);
		return countOfRecords;
	}
	@Transactional(readOnly = true)
	public int getNumberOfMarkEntriesMBAX(String sapid){
		String sql = "select count(*) from exam.mbax_passfail where sapid = ?  and isResultLive = 'Y'";
		int countOfRecords = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		System.out.println(countOfRecords);
		return countOfRecords;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public int getNumberOfsubjectsCleared(String sapid, boolean isLateral){

	/*	String sql = "Select count(*) from exam.passfail pf, exam.examorder eo where pf.isPass = 'Y' and pf.sapid = ? "
				  +" and pf.writtenMonth=eo.month "
				  +" and pf.writtenYear= eo.year ";
		
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}*/
		
	/*	String sql = "Select count(*) from exam.passfail pf, exam.students s where pf.isPass = 'Y' and pf.sapid = ? "
				  + " and pf.sapid = s.sapid ";		*/
		
//		Changes in sql because lateral students with same sapid getting old sapid subjects also
		
		String sql = "Select count(*) from exam.passfail pf where pf.isPass = 'Y' and pf.sapid = ? ";
		
		if(isLateral){
			sql = sql + " and (pf.sem = '2' or pf.sem = '3' or pf.sem = '4') ";
		}
		
		//System.out.println("SQL = "+sql);

		int numberOfsubjectsCleared = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);

		return numberOfsubjectsCleared;
	}

	//Deprecated, use deleteSrHistoryBySrId() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	@Transactional(readOnly = false)
	public void deleteSRHistoryForSR(String serviceRequestId) {
		String sql = "Delete from portal.service_request_history where serviceRequestId = ? ";
		jdbcTemplate.update(sql, new Object[]{serviceRequestId});
		
	}
	@Transactional(readOnly = false)
	public int deleteSrHistoryBySrId(long srId) {
		String serviceRequestId = String.valueOf(srId);
		String sql= "DELETE FROM portal.service_request_history " + 
					"WHERE serviceRequestId = ? ";
		
		return jdbcTemplate.update(sql, serviceRequestId);
	}

	// UnmarkReval for cancelled assignment SR
//	public void UnmarkForRevaluation(String sapid, String subject) {
//		String sql = "update exam.assignmentsubmission  asb, exam.examorder eo "
//				+ " set markedForRevaluation = 'N'  "
//				+ " where asb.sapid = ? and asb.subject = ? "
//				+ " and asb.year = eo.year and asb.month = eo.month "
//				+ " and eo.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')" ;
//		
//		jdbcTemplate.update(sql, new Object[]{sapid, subject});
//		
//	}
	@Transactional(readOnly = false)
	public int unmarkForRevaluation(String sapid, String subject) {
		String sql= "UPDATE exam.assignmentsubmission asb " + 
					"	INNER JOIN exam.examorder eo " + 
					"		ON asb.year = eo.year " + 
					"		AND asb.month = eo.month " + 
					"SET asb.markedForRevaluation = 'N' " + 
					"WHERE asb.sapid = ? " + 
					"	AND asb.subject = ? " + 
					"	AND eo.order = (SELECT max(examorder.order) " + 
					"					FROM exam.examorder " + 
					"                   WHERE assignmentMarksLive='Y')";
		
		return jdbcTemplate.update(sql, sapid, subject);
	}
	
	// Added by Steffi to UnmarkReval for cancelled tee SR
//	public void UnmarkForTeeRevaluation(String sapid, String subject) {
//		String sql = "update exam.marks m , exam.examorder eo  "
//				+ " set m.markedForRevaluation = 'N' "
//				+ " where m.sapid = ?  "
//				+ " and m.subject = ? "
//				+ " and m.year = eo.year and m.month = eo.month "
//				+ " and eo.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')" ;
//		
//		jdbcTemplate.update(sql, new Object[]{sapid, subject});
//		
//	}
	@Transactional(readOnly = false)
	public int unmarkForTeeRevaluation(String sapid, String subject) {
		String sql= "UPDATE exam.marks m " + 
					"	INNER JOIN exam.examorder eo " + 
					"		ON m.year = eo.year " + 
					"       AND m.month = eo.month " + 
					"SET m.markedForRevaluation = 'N' " + 
					"WHERE m.sapid = ? " + 
					"	AND m.subject = ? " + 
					"	AND eo.order = (SELECT max(examorder.order) " + 
					"					FROM exam.examorder " + 
					"                   WHERE assignmentMarksLive='Y')";
		
		return jdbcTemplate.update(sql, sapid, subject);
	}
	
	/*added on 6/2/2018*/
	//Executation time is 5 milliseconds.
	@Transactional(readOnly = true)
	public ArrayList<String> getSRTypesForExtendedTimeStudents(String sapid) {
		String sql = "select serviceRequestName from portal.service_request_types where sapidsToAllowAfterEndDate like ? order by serviceRequestName asc";
		return (ArrayList<String>)jdbcTemplate.query(sql, new Object [] {"%"+ sapid + "%"},new SingleColumnRowMapper(String.class));
	}
	@Transactional(readOnly = false)
	public void UpdateSRTime(String sapidList,String srName ){
		String sql = "update portal.service_request_types set sapidsToAllowAfterEndDate = ? where serviceRequestName = ? ";
		jdbcTemplate.update(sql, new Object[] {sapidList,srName});
		
	}
	@Transactional(readOnly = true)
	public String selectExtendedSRTime(String srName) {
		String sql = " SELECT sapidsToAllowAfterEndDate FROM portal.service_request_types where serviceRequestName = ? ";
	
	     String lstOfStudents =jdbcTemplate.queryForObject(sql,new Object[]{srName},String.class);
		return lstOfStudents;

		
	}
	/*	end*/

	@Transactional(readOnly = true)
	public HashMap<String,ProgramsStudentPortalBean> getProgramDetails() {
		//updated as program list to be taken from program table
		String sql = "select * from exam.programs" ;
		
		ArrayList<ProgramsStudentPortalBean> beanList = (ArrayList<ProgramsStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsStudentPortalBean.class));
		HashMap<String,ProgramsStudentPortalBean> programsInfoList = new HashMap<String,ProgramsStudentPortalBean>();
		for(ProgramsStudentPortalBean bean: beanList){
			String key = bean.getConsumerProgramStructureId();
			if(!programsInfoList.containsKey(key)){
				programsInfoList.put(key, bean);
			}
		}
		return programsInfoList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> getApplicableSubject(String prgmStructure, String program){
			
		ArrayList<String> applicableSubjects = new ArrayList<>();
		String sql =" select subject from exam.program_subject where program = ? and prgmStructApplicable = ? ";
		try {
			applicableSubjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{program, prgmStructure}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return applicableSubjects;
			
	}


	@Transactional(readOnly = true)
	public List getIfFinalCertificateIssued(String sapid) {
		String sql = "select * from portal.service_request where sapid = ? and serviceRequestType = 'Issuance of Final Certificate' and (tranStatus ='Free' or tranStatus ='Payment Successful') " ;
		List getIfFinalCertificateIssued = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		
		return getIfFinalCertificateIssued;
		
	}
	@Transactional(readOnly = false)
	public String saveCheckSum(String trackId,String amount,HttpServletRequest request) {
		try {
			String sql = "UPDATE `portal`.`service_request` SET `secureHash`= ? WHERE `trackId`=? and `amount`=?;";
			jdbcTemplate.update(sql, new Object[] {request.getParameter("CHECKSUMHASH"),trackId,amount});
			return "true";
		} catch (Exception e) {
//			e.printStackTrace();
			return e.getMessage();
		}
	}
	@Transactional(readOnly = true)
	public ArrayList<PaymentOptionsStudentPortalBean> getPaymentOptions() {
		String sql = "SELECT * FROM portal.payment_options where active = 'Y';";
		return (ArrayList<PaymentOptionsStudentPortalBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(PaymentOptionsStudentPortalBean.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<PaymentOptionsStudentPortalBean> getExamBookingPaymentOptions() {
		String sql = "SELECT * FROM portal.payment_options where exambookingActive = 'Y';";
		return (ArrayList<PaymentOptionsStudentPortalBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(PaymentOptionsStudentPortalBean.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getProgramsRegisteredByStudent(String sapId, String CURRENT_MBAWX_ACAD_MONTH,String CURRENT_MBAWX_ACAD_YEAR) {
		//System.out.println("sapId--"+sapId+"==CURRENT_MBAWX_ACAD_MONTH---"+CURRENT_MBAWX_ACAD_MONTH +"---CURRENT_MBAWX_ACAD_YEAR---"+CURRENT_MBAWX_ACAD_YEAR);
		String sql =  "SELECT * FROM exam.registration where sapid=? "
				+ " and month=? and year=? "
				+ "and sem = (Select max(sem) from exam.registration where sapid = ? )";
		System.out.println("sql--"+sql);
		ArrayList<StudentStudentPortalBean> studentList = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{sapId, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		System.out.println("Student bean in dao---"+studentList);
		return studentList;
	}
	@Transactional(readOnly = false)
	public int deRegisterUser(ServiceRequestStudentPortal sr) {
		String sql ="";
		int row = 0;
		sql = "DELETE from exam.registration where sapid=? and program=? and month=? and year=? and sem=?";
		row = jdbcTemplate.update(sql, new Object[]{
				sr.getSapId(),sr.getProgram(),sr.getMonth(),sr.getYear(),sr.getSem()
				});
		//System.out.println("rows deleted---"+row);
//		sql = "INSERT INTO exam.registration_staging_future_records (sapid, program, sem, month, year, createdBy, createdDate, "
//				+ " lastModifiedBy, lastModifiedDate) VALUES " + "(?,?,?,?,?,?,sysdate(),?,sysdate()) "
//				+ " on duplicate key update " + " sapid = ?," + "	program = ?," + " sem = ?,"
//				+ "	month = ?," + "	 year = ?," + "	 lastModifiedBy = ?";

//		String sapid = sr.getSapId();
//		String program = sr.getProgram();
//		String sem = sr.getSem();
//		String month = sr.getMonth();
//		String year = sr.getYear();
//
//		String createdBy = sr.getCreatedBy();
//		String lastModifiedBy = sr.getLastModifiedBy();
//		
//		
//
//		row = jdbcTemplate.update(sql, new Object[] { sapid, program, sem, month, year, createdBy, lastModifiedBy , sapid,
//				program, sem, month, year, lastModifiedBy });
		//System.out.println("rows updated---"+row);
		
		return row;
		
	}
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestStudentPortal> getDeregSRForStudent(String sapId, String CURRENT_MBAWX_ACAD_MONTH,String CURRENT_MBAWX_ACAD_YEAR) {
		//System.out.println("CURRENT_MBAWX_ACAD_MONTH---"+CURRENT_MBAWX_ACAD_MONTH +"---CURRENT_MBAWX_ACAD_YEAR---"+CURRENT_MBAWX_ACAD_YEAR);
		String sql =  "SELECT * FROM portal.service_request where sapid=? "
					+ " and serviceRequestType='Program De-Registration' and month=? and year=? ";
		System.out.println("sql--"+sql + sapId + CURRENT_MBAWX_ACAD_MONTH + CURRENT_MBAWX_ACAD_YEAR);
		ArrayList<ServiceRequestStudentPortal> serviceRequestList = (ArrayList<ServiceRequestStudentPortal>)jdbcTemplate.query(sql, new Object[]{sapId, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
		System.out.println("Student bean in dao---"+serviceRequestList);
		return serviceRequestList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String>  getSubjectsAppearedForSemesterMessageListForMBAWX(ServiceRequestStudentPortal sr){
		ArrayList<String> semesterAppearedMessageList = new ArrayList<String>();
		
			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 1--------------");
			String sql = "SELECT " + 
					"	count(*)  " + 
					"FROM   " + 
					"	exam.mba_passfail pf   " + 
					"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
					"	INNER JOIN  exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id   " + 
					"WHERE   " + 
					"	pf.sapid = ? and" + 
					"    ssc.examMonth = ? and ssc.examYear=? and " + 
					"    pss.sem = ? " ;
		
		
		try{
			
//		ArrayList<EmbaPassFailBean>	subjectsAppearedList = new ArrayList<EmbaPassFailBean>();
		ArrayList<ServiceRequestStudentPortal> srBeanList = getYearMonthAndSemFromPipedValuesForMBAWX(sr);
		
		for(ServiceRequestStudentPortal bean : srBeanList) {
			ServiceRequestStudentPortal serviceBean = bean;
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 2--------------"+count);
//			int count = subjectsAppearedList.size();
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
		}
//		if(srBeanList.get(0)!=null){
//			ServiceRequest serviceBean = srBeanList.get(0); 
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 2--------------"+count);
////			int count = subjectsAppearedList.size();
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//			
//		}
//		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 3--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
//		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 4--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
//		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 5--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
		
		
		return semesterAppearedMessageList;
		}catch(Exception e){
			////System.out.println("");
			//System.out.println("getSubjectsAppearedForSemesterMessageList :"+e.getMessage());
			return null;
		}
	}
	
	public ArrayList<String> resultDeclaredMessageForMBAWX(ServiceRequestStudentPortal sr){
		ArrayList<String> resultDeclaredMessage = new ArrayList<String>();
		//System.out.println("inside resultDeclaredMessageForMBAWX-----1-----");
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord1());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}			
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord2());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord3());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord4());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		
		if(sr.getMarksheetDetailRecord5()!=null && !"".equals(sr.getMarksheetDetailRecord5())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord5());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		
		if(sr.getMarksheetDetailRecord6()!=null && !"".equals(sr.getMarksheetDetailRecord6())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord6());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		
		if(sr.getMarksheetDetailRecord7()!=null && !"".equals(sr.getMarksheetDetailRecord7())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord7());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		
		if(sr.getMarksheetDetailRecord8()!=null && !"".equals(sr.getMarksheetDetailRecord8())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord8());
			String live = "";
			if(bean.getMonth() == "Oct" && bean.getYear() == "2019") {
				live= "Y";
			}
			if(bean.getMonth() == "Jan" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Apr" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2020") {
				live= "Y";
			}
			if(bean.getMonth() == "Jul" && bean.getYear() == "2019") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		//System.out.println("inside resultDeclaredMessageForMBAWX-----2-----");
		return resultDeclaredMessage;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getSubjectsAppearedForSemesterForMBAWX(ServiceRequestStudentPortal sr){
		String sql = "SELECT " + 
				"	count(*) " + 
				"FROM   " + 
				"	exam.mba_passfail pf   " + 
				"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
				"	INNER JOIN  exam.program_sem_subject pss ON pf.prgm_sem_subj_id = pss.id   " + 
				"WHERE   " + 
				"	pf.sapid = ? and" + 
				"    ssc.examMonth = ? and ssc.examYear=? and " + 
				"    pss.sem = ?  ";
		
		ArrayList<ServiceRequestStudentPortal> srBeanList = getYearMonthAndSemFromPipedValuesForMBAWX(sr);
		ArrayList<Integer> countList = new ArrayList<Integer>();
		try{
			
			for(ServiceRequestStudentPortal bean : srBeanList) {
				ServiceRequestStudentPortal serviceBean = bean;
				int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
				System.out.println("count in getSubjectsAppearedForSemesterForMBAWX-------"+count);
				countList.add(count);
			}
			
			}catch(Exception e){
				////System.out.println("");
				System.out.println("getSubjectsAppearedForSemesterForMBAWX :"+e.getMessage());
				
			}
		return countList;
		
	}
	
	public ArrayList<ServiceRequestStudentPortal> getYearMonthAndSemFromPipedValuesForMBAWX(ServiceRequestStudentPortal sr){
		ArrayList<ServiceRequestStudentPortal> srBeanList = new ArrayList<ServiceRequestStudentPortal>();
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord1());
			//System.out.println("serviceBean-----1--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
			
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
			//System.out.println("serviceBean-----2--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
			//System.out.println("serviceBean-----3--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
			//System.out.println("serviceBean-----4--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		
		return srBeanList;
		
	}
	
	
	

	@Transactional(readOnly = true)
	public ArrayList<String>  getSubjectsAppearedForSemesterMessageListForMBAX(ServiceRequestStudentPortal sr){
		ArrayList<String> semesterAppearedMessageList = new ArrayList<String>();
		
			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 1--------------");
			String sql = "SELECT " + 
					"	count(*)  " + 
					"FROM   " + 
					"	exam.mbax_passfail pf   " + 
					"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
					"	INNER JOIN  exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   " + 
					"WHERE   " + 
					"	pf.sapid = ? and" + 
					"    ssc.examMonth = ? and ssc.examYear=? and " + 
					"    pss.sem = ? " ;
		
		
		try{
			
//		ArrayList<EmbaPassFailBean>	subjectsAppearedList = new ArrayList<EmbaPassFailBean>();
		ArrayList<ServiceRequestStudentPortal> srBeanList = getYearMonthAndSemFromPipedValuesForMBAWX(sr);
		
		for(ServiceRequestStudentPortal bean : srBeanList) {
			ServiceRequestStudentPortal serviceBean = bean;
			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 2--------------"+count);
//			int count = subjectsAppearedList.size();
			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
			if(!"".equals(semesterAppearedMessage)){
				semesterAppearedMessageList.add(semesterAppearedMessage);
			}
		}
//		if(srBeanList.get(0)!=null){
//			ServiceRequest serviceBean = srBeanList.get(0); 
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 2--------------"+count);
////			int count = subjectsAppearedList.size();
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//			
//		}
//		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 3--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
//		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 4--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
//		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
//			ServiceRequest serviceBean = new ServiceRequest();
//			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
////			ArrayList<EmbaPassFailBean>	subjectsAppearedList = (ArrayList<EmbaPassFailBean>)jdbcTemplate.query(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, new BeanPropertyRowMapper<EmbaPassFailBean>(EmbaPassFailBean.class));
//			//System.out.println("inside if getSubjectsAppearedForSemesterMessageListForMBAWX 5--------------");
////			int count = subjectsAppearedList.size();
//			int count = (int) jdbcTemplate.queryForObject(sql, new Object[] {sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()}, Integer.class);
//			String semesterAppearedMessage = generateSemesterAppearedMessage(serviceBean,count);
//			if(!"".equals(semesterAppearedMessage)){
//				semesterAppearedMessageList.add(semesterAppearedMessage);
//			}
//		}
		
		
		return semesterAppearedMessageList;
		}catch(Exception e){
			////System.out.println("");
			//System.out.println("getSubjectsAppearedForSemesterMessageList :"+e.getMessage());
			return null;
		}
	}
	
	public ArrayList<String> resultDeclaredMessageForMBAX(ServiceRequestStudentPortal sr){
		ArrayList<String> resultDeclaredMessage = new ArrayList<String>();
		//System.out.println("inside resultDeclaredMessageForMBAWX-----1-----");
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord1());
			String live = "";
			if(bean.getMonth() == "Dec" && bean.getYear() == "2019") {
				live= "Y";
			}
			
			if(bean.getMonth() == "Mar" && bean.getYear() == "2020") {
				live= "Y";
			}
			/*////System.out.println("LIVE-->"+live);*/
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord2());
			String live = "";
			if(bean.getMonth() == "Dec" && bean.getYear() == "2019") {
				live= "Y";
			}
			
			if(bean.getMonth() == "Mar" && bean.getYear() == "2020") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord3());
			String live = "";
			if(bean.getMonth() == "Dec" && bean.getYear() == "2019") {
				live= "Y";
			}
			
			if(bean.getMonth() == "Mar" && bean.getYear() == "2020") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
			try{
			getYearMonthAndSemFromPipedValues(bean,sr.getMarksheetDetailRecord4());
			String live = "";
			if(bean.getMonth() == "Dec" && bean.getYear() == "2019") {
				live= "Y";
			}
			
			if(bean.getMonth() == "Mar" && bean.getYear() == "2020") {
				live= "Y";
			}
			if("N".equals(live)){
				resultDeclaredMessage.add("Result is not declared for "+bean.getYear()+" Year and Month :-"+bean.getMonth()) ;
			}
			}catch(EmptyResultDataAccessException e){
				//Will enter loop if there is no record or row in table.//
				resultDeclaredMessage.add("Exam was not conducted for year "+bean.getYear()+" and month "+bean.getMonth());
			}
		}
		//System.out.println("inside resultDeclaredMessageForMBAWX-----2-----");
		return resultDeclaredMessage;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<Integer> getSubjectsAppearedForSemesterForMBAX(ServiceRequestStudentPortal sr){
		String sql = "SELECT " + 
				"	count(*) " + 
				"FROM   " + 
				"	exam.mbax_passfail pf   " + 
				"	INNER JOIN  lti.student_subject_config ssc ON ssc.id = pf.timeBoundId   " + 
				"	INNER JOIN  exam.program_sem_subject pss ON ssc.prgm_sem_subj_id = pss.id   " + 
				"WHERE   " + 
				"	pf.sapid = ? and" + 
				"    ssc.examMonth = ? and ssc.examYear=? and " + 
				"    pss.sem = ?  ";
		
		ArrayList<ServiceRequestStudentPortal> srBeanList = getYearMonthAndSemFromPipedValuesForMBAWX(sr);
		ArrayList<Integer> countList = new ArrayList<Integer>();
		try{
			
			for(ServiceRequestStudentPortal bean : srBeanList) {
				ServiceRequestStudentPortal serviceBean = bean;
				int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sr.getSapId(),serviceBean.getMonth(),serviceBean.getYear(),serviceBean.getSem()},Integer.class);
				//System.out.println("count in getSubjectsAppearedForSemesterForMBAWX-------"+count);
				countList.add(count);
			}
			
			}catch(Exception e){
				////System.out.println("");
				//System.out.println("getSubjectsAppearedForSemesterForMBAWX :"+e.getMessage());
				
			}
		return countList;
		
	}
	
	public ArrayList<ServiceRequestStudentPortal> getYearMonthAndSemFromPipedValuesForMBAX(ServiceRequestStudentPortal sr){
		ArrayList<ServiceRequestStudentPortal> srBeanList = new ArrayList<ServiceRequestStudentPortal>();
		if(sr.getMarksheetDetailRecord1()!=null && !"".equals(sr.getMarksheetDetailRecord1())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord1());
			//System.out.println("serviceBean-----1--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
			
		}
		if(sr.getMarksheetDetailRecord2()!=null && !"".equals(sr.getMarksheetDetailRecord2())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			/*////System.out.println("getMarksheetDetailRecord2-->"+sr.getMarksheetDetailRecord2());*/
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord2());
			//System.out.println("serviceBean-----2--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		if(sr.getMarksheetDetailRecord3()!=null && !"".equals(sr.getMarksheetDetailRecord3())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord3());
			//System.out.println("serviceBean-----3--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
			//System.out.println("serviceBean-----4--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		if(sr.getMarksheetDetailRecord4()!=null && !"".equals(sr.getMarksheetDetailRecord4())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord4());
			//System.out.println("serviceBean-----4--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		
		if(sr.getMarksheetDetailRecord5()!=null && !"".equals(sr.getMarksheetDetailRecord5())){
			ServiceRequestStudentPortal serviceBean = new ServiceRequestStudentPortal();
			getYearMonthAndSemFromPipedValues(serviceBean,sr.getMarksheetDetailRecord5());
			//System.out.println("serviceBean-----5--------!!!!!"+serviceBean);
			srBeanList.add(serviceBean);
		}
		
		return srBeanList;
		
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<String> getApplicableSubjectNew(String consumerProgramStructure){
		
		ArrayList<String> applicableSubjects = new ArrayList<>();
		String sql =" select subject from exam.program_sem_subject where consumerProgramStructureId = ? ";
		try {
			applicableSubjects = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{consumerProgramStructure}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return applicableSubjects;
			
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public List<String> getSubjectsClearedCurrentProgramNew(String sapid, String isLateral,String cps_id){
//Changes in sql because lateral students with same sapid getting old sapid subjects also
		ArrayList<String> subjectsCleared = new ArrayList<>();
		String sql = "Select pf.subject from exam.passfail pf "
                + " INNER JOIN exam.program_sem_subject pss on pss.consumerProgramStructureId= ? and pss.subject=pf.subject "
				+ " INNER JOIN `exam`.`examorder` `eo` "
				+ " ON `eo`.`year` = `pf`.`resultProcessedYear` AND `eo`.`month` = `pf`.`resultProcessedMonth`  "
				+ " where pf.isPass = 'Y' and pf.sapid = ? "
				+ " AND `eo`.`live` = 'Y' AND `eo`.`assignmentMarksLive` = 'Y' ";
		if(isLateral.equalsIgnoreCase("Y")){
			//sql = sql + " and (pf.sem = '2' or pf.sem = '3' or pf.sem = '4') ";
		}
		//System.out.println("SQL = "+sql);
		try {
		subjectsCleared =  (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{cps_id,sapid},new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return subjectsCleared;
	}


	@Transactional(readOnly = false)
	public int updateStudentProgramStatus(String sapid) {
		int noOfRowsUpdated=0;
		String sql = "Update exam.students"
				+ " set programStatus = 'Program Withdrawal'  "
				+ " where  sapid = ? "; 
			
		try {
			noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { 
					sapid
			});
		} catch (DataAccessException e) {
//			e.printStackTrace();
		}

			return noOfRowsUpdated;

	}
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getRecentRegisterationByStudent(String sapId) {
		String sql =  "SELECT * FROM exam.registration where sapid=? "
				+ "and sem = (Select max(sem) from exam.registration where sapid = ? )";
		StudentStudentPortalBean studentList = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		
		return studentList;
	}


	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getSemSubjectCountMapping(StudentStudentPortalBean student) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> SemSubjectCountMap= new ArrayList();
		String sql = " SELECT " +
			    " `sem`, COUNT(sem) AS `subjectsCount` " +
			    " FROM " +
			       " `exam`.`program_sem_subject` " +
			    " WHERE " +
			        " `consumerProgramStructureId` = ? and passScore>0 " +
			    " GROUP BY `sem` ";
		try {
			SemSubjectCountMap = (ArrayList<ProgramSubjectMappingStudentPortalBean>) jdbcTemplate.query(
					sql, new Object[]{student.getConsumerProgramStructureId()}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));
			
		} catch (Exception e) {  
//			e.printStackTrace();
		}
		
		return SemSubjectCountMap;
		
	}


	@Transactional(readOnly = true)
	public PassFailBean getPassedSubjectCount(String sem,String sapid) {
		PassFailBean passFailBean= new PassFailBean();  
		
		String sql =""
				+ " SELECT COUNT(*) AS `count`, `sem`, "
				+ " IF( "
					+ " MAX(STR_TO_DATE(CONCAT( '00,', `writtenMonth`, ',', `writtenYear`), '%d,%b,%Y')) > "
					+ " MAX(STR_TO_DATE(CONCAT( '00,', `assignmentMonth`, ',', `assignmentYear`), '%d,%b,%Y')), "
					+ " DATE_FORMAT(MAX(STR_TO_DATE(CONCAT( '00,', `writtenMonth`, ',', `writtenYear`),'%d,%b,%Y')), '%Y'), "
					+ " DATE_FORMAT(MAX(STR_TO_DATE(CONCAT( '00,', `assignmentMonth`, ',', assignmentYear ),'%d,%b,%Y')), '%Y') "
				+ " ) AS `writtenYear`, "
				+ " IF( "
					+ " MAX(STR_TO_DATE(CONCAT( '00,', writtenMonth, ',', writtenYear ),'%d,%b,%Y')) > "
					+ " MAX(STR_TO_DATE(CONCAT( '00,', assignmentMonth, ',', assignmentYear ),'%d,%b,%Y')), "
					+ " DATE_FORMAT(max(STR_TO_DATE(CONCAT( '00,', writtenMonth, ',', writtenYear ),'%d,%b,%Y')),  '%b'), "
					+ " DATE_FORMAT(max(STR_TO_DATE(CONCAT( '00,', assignmentMonth, ',', assignmentYear ),'%d,%b,%Y')),  '%b') "
				+ " ) AS `writtenMonth` "
				+ " FROM `exam`.`passfail` `pf` "
				+ " INNER JOIN `exam`.`examorder` `eo` "
					+ " ON `eo`.`year` = `pf`.`resultProcessedYear` AND `eo`.`month` = `pf`.`resultProcessedMonth` "
				
				+ " WHERE `sapid` = ? AND `isPass` = 'Y' AND `sem` = ? "
					+ " AND `eo`.`live` = 'Y' AND `eo`.`assignmentMarksLive` = 'Y' ";   
			
			try {
				passFailBean = (PassFailBean)jdbcTemplate.query(sql, new Object[]{sapid,sem}, new BeanPropertyRowMapper(PassFailBean.class)).get(0);
				
			} catch (Exception e) {   
//				e.printStackTrace();
			}
		////System.out.println("passFailBean:::"+passFailBean);  
		return passFailBean;   
	}
	@Transactional(readOnly = true)
	public PassFailBean getPassedSubjectCountForMbaWx(String sem,String sapid) {
	
		PassFailBean passFailBean= new PassFailBean(); 
		String sql2=" SELECT " + 
					"    COUNT(pf.timeboundId) AS count " + 
					"FROM " + 
					"    exam.mba_passfail pf " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
					"WHERE " + 
					" pss.sem = ? and " + 
					"    pf.sapid = ? " + 
					"        AND pf.isPass = 'Y'";        
			try {
				passFailBean = (PassFailBean)jdbcTemplate.query(sql2, new Object[]{sem,sapid}, new BeanPropertyRowMapper(PassFailBean.class)).get(0);
				
			} catch (Exception e) {   
//				e.printStackTrace();
			}
		return passFailBean;   
	}
	@Transactional(readOnly = true)
	public boolean checkIfFailedForThisSemMbaWx(String sem,String sapid) {
		
		PassFailBean passFailBean= new PassFailBean(); 
		String sql2=" SELECT " + 
					"    COUNT(pf.timeboundId) AS count " + 
					"FROM " + 
					"    exam.mba_passfail pf " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
					"WHERE " + 
					" pss.sem = ? and " + 
					"    pf.sapid = ? " + 
					"        AND pf.isPass = 'N'";        
			try {
				passFailBean = (PassFailBean)jdbcTemplate.query(sql2, new Object[]{sem,sapid}, new BeanPropertyRowMapper(PassFailBean.class)).get(0);
				
			} catch (Exception e) {   
//				e.printStackTrace();
			}
		 if(passFailBean.getCount()>0) {
			 //System.out.println("passFailBean.getCount()"+passFailBean.getCount());
			 //failed
			 return true;
		 }
		 return false;
	}
	@Transactional(readOnly = true)
public boolean checkIfFailedForThisSemMbax(String sem,String sapid) {
		
		PassFailBean passFailBean= new PassFailBean(); 
		String sql2=" SELECT " + 
					"    COUNT(pf.timeboundId) AS count " + 
					"FROM " + 
					"    exam.mbax_passfail pf " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
					"WHERE " + 
					" pss.sem = ? and " + 
					"    pf.sapid = ? " + 
					"        AND pf.isPass = 'N'";        
			try {
				passFailBean = (PassFailBean)jdbcTemplate.query(sql2, new Object[]{sem,sapid}, new BeanPropertyRowMapper(PassFailBean.class)).get(0);
				
			} catch (Exception e) {   
//				e.printStackTrace();
			}
		 if(passFailBean.getCount()>0) {
			 System.out.println("passFailBean.getCount()"+passFailBean.getCount());
			 //failed
			 return true;
		 }
		 return false;
	}
	@Transactional(readOnly = true)
	public PassFailBean getPassedSubjectCountForMbax(String sem,String sapid) {
		
		PassFailBean passFailBean= new PassFailBean(); 
		String sql2=" SELECT " + 
					"    COUNT(pf.timeboundId) AS count " + 
					"FROM " + 
					"    exam.mbax_passfail pf " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
					"WHERE " + 
					" pss.sem = ?  and " + 
					"    pf.sapid = ? " + 
					"        AND pf.isPass = 'Y' ";        
			try {
				passFailBean = (PassFailBean)jdbcTemplate.query(sql2, new Object[]{sem,sapid}, new BeanPropertyRowMapper(PassFailBean.class)).get(0);
				
			} catch (Exception e) {   
//				e.printStackTrace();
			}
		return passFailBean;   
	}
	@Transactional(readOnly = true)
	public boolean checkIfDeRegLiveForMBAWX(String sapId) {

		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`students` `s` "
		
		    /* get Acad Year/Month details */
		    + " INNER JOIN  ( "
		    	+ " SELECT "
			    	+ " MAX(`sem`) as `currentSem`, "
			    	+ " `month` AS `acadsMonth`, "
			    	+ " `year` AS `acadsYear`, "
			    	+ " `sapid` "
		    	+ " FROM  `exam`.`registration` "
		    	+ " GROUP BY `sapid` "
		    + " ) `reg` "
		    + " ON `s`.`sapid` = `reg`.`sapid` "
		
		    /* get Exam Year/Month details */
				/* get timebound ids for student */
			    + " LEFT JOIN `lti`.`timebound_user_mapping` `tum` "
			    + " ON `tum`.`userId` = `s`.`sapid` "
		
				/* get ssc list for student */
			    + " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
		
		    /* get Live Settings details */
		    + " RIGHT JOIN `exam`.`mba_wx_exam_live_setting` `mels` "
		    + " ON ( "
			    	+ " `s`.`consumerProgramStructureId` = `mels`.`consumerProgramStructureId` "
			    + " AND `mels`.`acadsYear` = `reg`.`acadsYear` "
			    + " AND `mels`.`acadsMonth` = `reg`.`acadsMonth` "
			    + " AND `mels`.`examYear` = `ssc`.`examYear` "
			    + " AND `mels`.`examMonth` = `ssc`.`examMonth` "
		    + " ) "
			+ " WHERE "
				+ " `s`.`sapid` = ? "
				+ " AND (sysdate() BETWEEN `mels`.`startTime` AND `mels`.`endTime`)"
				+ " AND `mels`.`type` = 'Program De-Registration' ";;
		int count = (int) jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapId },
			Integer.class
		);
		
		if(count == 0) {
			return false;
		}
		return true;
	}
	@Transactional(readOnly = true)
	public Date getLastDateForDeRegMBAWX(String sapId) {

		String sql = ""
			+ " SELECT `mels`.`endTime` "
			+ " FROM `exam`.`students` `s` "
		
		    /* get Acad Year/Month details */
		    + " INNER JOIN  ( "
		    	+ " SELECT "
			    	+ " MAX(`sem`) as `currentSem`, "
			    	+ " `month` AS `acadsMonth`, "
			    	+ " `year` AS `acadsYear`, "
			    	+ " `sapid` "
		    	+ " FROM  `exam`.`registration` "
		    	+ " GROUP BY `sapid` "
		    + " ) `reg` "
		    + " ON `s`.`sapid` = `reg`.`sapid` "
		
		    /* get Exam Year/Month details */
				/* get timebound ids for student */
			    + " LEFT JOIN `lti`.`timebound_user_mapping` `tum` "
			    + " ON `tum`.`userId` = `s`.`sapid` "
		
				/* get ssc list for student */
			    + " LEFT JOIN `lti`.`student_subject_config` `ssc` "
			    + " ON `ssc`.`id` = `tum`.`timebound_subject_config_id` "
		
		    /* get Live Settings details */
		    + " RIGHT JOIN `exam`.`mba_wx_exam_live_setting` `mels` "
		    + " ON ( "
			    	+ " `s`.`consumerProgramStructureId` = `mels`.`consumerProgramStructureId` "
			    + " AND `mels`.`acadsYear` = `reg`.`acadsYear` "
			    + " AND `mels`.`acadsMonth` = `reg`.`acadsMonth` "
			    + " AND `mels`.`examYear` = `ssc`.`examYear` "
			    + " AND `mels`.`examMonth` = `ssc`.`examMonth` "
		    + " ) "
			+ " WHERE "
				+ " `s`.`sapid` = ? "
				+ " AND `mels`.`type` = 'Program De-Registration' ";;
		Date endDate = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{ sapId },
			Date.class
		);
		
		return endDate;
	}
	@Transactional(readOnly = true)
	public ServiceRequestStudentPortal findSRBySapIdAndType(String sapid,String type) {
		String sql = "Select * from portal.service_request where sapid = ? and serviceRequestType=? order by id desc limit 1";
		return (ServiceRequestStudentPortal)jdbcTemplate.queryForObject(sql, new Object[]{sapid,type}, new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getFailedSubjectsForStudentMBAWX(String sapId, String charges) {

		String sql =  ""
				+ " SELECT `pss`.`subject` AS `subject`, `pss`.`sem`, " + charges + " AS `amount`  "
				+ " FROM exam.mba_passfail `pf` "
				+ " LEFT JOIN `exam`.`program_sem_subject` `pss` "
				+ " ON `pss`.`id` = `pf`.`prgm_sem_subj_id` "
				+ " WHERE `sapid` = ? AND `isPass`='N' AND `isResultLive` = 'Y' ";

		List<ServiceRequestStudentPortal> failedSubjectsList = jdbcTemplate.query(
			sql, 
			new Object[]{
				sapId
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		return failedSubjectsList;
	}
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getRepeatAppliedSubjectsMBAWX(String sapId, String CURRENT_MBAWX_ACAD_MONTH, String CURRENT_MBAWX_ACAD_YEAR, String type) {

		String sql =  ""
				+ " SELECT * "
				+ " FROM `portal`.`service_request` `sr` "
				+ " WHERE `sapid` = ? AND `year` = ? AND `month` = ? "
				+ " AND `tranStatus` = ? AND `serviceRequestType` = ?";

		List<ServiceRequestStudentPortal> appliedSubjectsList = jdbcTemplate.query(
			sql, 
			new Object[]{
				sapId, CURRENT_MBAWX_ACAD_YEAR, CURRENT_MBAWX_ACAD_MONTH, 
				ServiceRequestStudentPortal.TRAN_STATUS_SUCCESSFUL, type
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		return appliedSubjectsList;
	}
	@Transactional(readOnly = true)
	public boolean getReRegLiveMBAWX(String sapId, String CURRENT_MBAWX_ACAD_MONTH, String CURRENT_MBAWX_ACAD_YEAR, String type) {

		String sql =  ""
				+ " SELECT count(*) "
				+ " FROM `lti`.`timebound_user_mapping` `tum` "
				+ " LEFT JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `tum`.`userId` "
				
				+ " LEFT JOIN `exam`.`mba_wx_exam_live_setting` `ls` "
				+ " ON `ls`.`consumerProgramStructureId` = `s`.`consumerProgramStructureId` "
				+ " WHERE "
				+ " `tum`.`userId` = ? "
				+ " AND `ls`.`acadsYear` = ? "
				+ " AND `ls`.`acadsMonth` = ? "
				+ " AND sysdate() BETWEEN `ls`.`startTime` AND `ls`.`endTime` "
				+ " AND `ls`.`type` = ? ";

		//System.out.println(sapId+ CURRENT_MBAWX_ACAD_YEAR+ CURRENT_MBAWX_ACAD_MONTH+ type);
		int liveBeans = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{
				sapId, CURRENT_MBAWX_ACAD_YEAR, CURRENT_MBAWX_ACAD_MONTH, type
			}, 
			Integer.class
		);
		return liveBeans > 0;
	}
	@Transactional(readOnly = true)
	public ServiceRequestStudentPortal getServiceRequestBySapidAndTrackId(String sapId, String trackId) {

		String sql =  ""
				+ " SELECT * "
				+ " FROM `portal`.`service_request` `sr` "
				+ " WHERE `sapid` = ? AND `trackId` = ?"
				+ " LIMIT 1";

		ServiceRequestStudentPortal sr = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{
				sapId, trackId
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		return sr;
	}
	
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getListOfServiceRequestBySapidAndTrackId(String sapId, String trackId) {
		try {
		String sql =  ""
				+ " SELECT * "
				+ " FROM `portal`.`service_request` `sr` "
				+ " WHERE `sapid` = ? AND `trackId` = ?";

		return (List<ServiceRequestStudentPortal>) jdbcTemplate.query(
			sql, 
			new Object[]{
				sapId, trackId
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		}
		catch (Exception e) {
			// TODO: handle exception
			return new ArrayList<ServiceRequestStudentPortal>();
		}
	}
	
	@Transactional(readOnly = true) 
	public List<ServiceRequestStudentPortal> findSRByTrackId(String trackId, String sapid) {

		String sql =  ""
				+ " SELECT *, `informationForPostPayment` AS `subject` "
				+ " FROM `portal`.`service_request` `sr` "
				+ " WHERE `sapid` = ? AND `trackId` = ?";

		List<ServiceRequestStudentPortal> srList = jdbcTemplate.query(
			sql, 
			new Object[]{
				sapid, trackId
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		return srList;
	}
	@Transactional(readOnly = true)
	public Integer getMaxSemofProgram(StudentStudentPortalBean student) {
		Integer maxSemofProgram= 0;
		//System.out.println("student.getConsumerProgramStructureId():"+student.getConsumerProgramStructureId());
		String sql = " SELECT " +
			    " max(`sem`) " +
			    " FROM " +
			       " `exam`.`program_sem_subject` " +
			    " WHERE " +
			        " `consumerProgramStructureId` = ? " ;
		try {
			maxSemofProgram = (Integer) jdbcTemplate.queryForObject(
					sql, new Object[]{student.getConsumerProgramStructureId()}, new SingleColumnRowMapper(Integer.class));
		//System.out.println(maxSemofProgram);	
		} catch (Exception e) {  
//			e.printStackTrace();
		}
		
		return maxSemofProgram;
		
	}
	@Transactional(readOnly = true)
	public Integer noOfSubjectsToClear(String sapId) {
		Integer noOfSubjectsToClear = null;
		
		String sql = "SELECT  " + 
				"    noOfSubjectsToClear " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.programs ps ON s.consumerProgramStructureId = ps.consumerProgramStructureId " + 
				"WHERE " + 
				"    sapid = ?;";
		
		noOfSubjectsToClear = (Integer) jdbcTemplate.queryForObject(sql, new Object[]{sapId} , new SingleColumnRowMapper(Integer.class));
		
		return noOfSubjectsToClear;
	}
	@Transactional(readOnly = true)
	public List<String> getSubjectsClearedCurrentProgramMBAWX(String sapid){
		
		String sql = "SELECT  " + 
				"    pss.subject " + 
				"FROM " + 
				"    exam.mba_passfail pf " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
				"WHERE " + 
				"    pf.sapid = ? " + 
				"        AND pf.grade IS NOT NULL " + 
				"        AND pf.isResultLive = 'Y' " +
				" 		 AND pf.isPass='Y' " +
				" 		 AND 1 = 1; ";
		
		return  (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid},new SingleColumnRowMapper(String.class));
		
		}			
	@Transactional(readOnly = true)
	public List<String> getSubjectsClearedCurrentProgramMBAX(String sapid){
		
		String sql = "SELECT  " + 
				"    pss.subject " + 
				"FROM " + 
				"    exam.mbax_passfail pf " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
				"WHERE " + 
				"    pf.sapid = ? " + 
				"        AND pf.grade IS NOT NULL " + 
				"        AND pf.isResultLive = 'Y' " +
				" 		 AND 1 = 1; ";
		
		return  (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid},new SingleColumnRowMapper(String.class));
		
	}
	
	@Transactional(readOnly = true)
	public List<String> getClearedSubjectsForStructureChangeStudentMBAX(String sapid){
		
		String sql = "SELECT " + 
				"    pss.subject " + 
				"FROM " + 
				"    exam.mbax_passfail pf " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
				"        INNER JOIN " + 
				"    exam.mbax_change_structure_mapping map ON map.oldPssId = `ssc`.`prgm_sem_subj_id` " + 
				"        AND pf.sapid = map.sapid " + 
				"        INNER JOIN " + 
				"    `exam`.`program_sem_subject` `pss` ON map.newPssId = `pss`.`id` " + 
				"WHERE " + 
				"    pf.sapid = ? " + 
				"        AND pf.grade IS NOT NULL " + 
				"        AND pf.isResultLive = 'Y' ";
		
		return jdbcTemplate.query(sql, new Object[]{sapid},new SingleColumnRowMapper<String>(String.class));
		
		}
	
	@Transactional(readOnly = true)
	public LinkedInAddCertToProfileBean getLinkedInProfile(String sapid) {
		
		
		String sql = "SELECT  " + 
				"    * " + 
				"FROM " + 
				"    linked_in.profile " + 
				"WHERE " + 
				"  1 = 1 " +
				"  AND   sapid = ? ;";
		
		return  (LinkedInAddCertToProfileBean) jdbcTemplate.queryForObject(sql,  new Object[]{sapid},  new BeanPropertyRowMapper(LinkedInAddCertToProfileBean.class));
	}
	@Transactional(readOnly = false)
	public int createLinkedInProfile(String personId, String sapid, String authorization_code, String  access_token, String expires_in) {
		
		String sql = "INSERT "
				+ " INTO linked_in.profile "
				+ "(personId, sapid, authorization_code, access_token, expires_in) "
				+ " VALUES ( ?, ?, ?, ?, ?);";	
				
			return jdbcTemplate.update(sql,personId, sapid, authorization_code, access_token, expires_in)	;
		
	}


	@Transactional(readOnly = false)
	public ServiceRequestStudentPortal massUpdateSRStatus(ServiceRequestStudentPortal bean) {
		int noOfRowsUpdated = 0; 
		 
		String[] idArray = bean.getServiceRequestIdList().split("\\n");  
		ArrayList<String> failedList = new ArrayList<String>(); 
		ArrayList<String> errorlist=new ArrayList<String>();
		for(String id : idArray) {
			try {
				String sql = "Update portal.service_request set requestStatus = ? , "
						+ " lastModifiedBy = ? ,"
						+ " lastModifiedDate = sysdate() "
						+ " where id = ? ";
				noOfRowsUpdated = jdbcTemplate.update(sql, new Object[]{bean.getStatus(),bean.getUserId(),id});
				
				String sql2 = "Update portal.service_request_history set status = ? where serviceRequestId = ?";
				jdbcTemplate.update(sql2, new Object[]{bean.getStatus(),id});
				
				if(noOfRowsUpdated==0) { 
					errorlist.add(id+": Invalid Sr id");
				}
		    }catch(Exception e)
			{ 
//		    	e.printStackTrace();  
		    	errorlist.add(id+": "+e.getMessage());
			}
		} 
		bean.setErrorList(errorlist);
		return bean;
	} 
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}else {
			commaSeparatedList = "'"+commaSeparatedList+"'";
		}
		return commaSeparatedList;
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
					statement.setString(8, trackId);//
					statement.setString(9, sapid);
					statement.setString(10, fileName); 
					
					return statement;
				}
			}, keyHolder);
		} catch (DataAccessException e) {  
//			e.printStackTrace();
		}
		
		return keyHolder.getKey().intValue();
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramsStudentPortalBean> getConsumerProgramStructureList() {
		ArrayList<ProgramsStudentPortalBean> SemSubjectCountMap= new ArrayList();
		String sql = "select cps.id,p.code,ps.program_structure as programStructure from exam.consumer_program_structure cps  " + 
				"left join exam.program p on cps.programId= p.id    " + 
				"left join exam.program_structure ps on cps.programStructureId= ps.id";
		try {
			SemSubjectCountMap = (ArrayList<ProgramsStudentPortalBean>) jdbcTemplate.query(
					sql, new Object[]{}, new BeanPropertyRowMapper(ProgramsStudentPortalBean.class));
			
		} catch (Exception e) {  
			e.printStackTrace();
		}
		
		return SemSubjectCountMap;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestType> getSrTypeList() {
		ArrayList<ServiceRequestType> SemSubjectCountMap= new ArrayList();
		String sql = "SELECT * FROM portal.service_request_types";
		try {
			SemSubjectCountMap = (ArrayList<ServiceRequestType>) jdbcTemplate.query(
					sql, new Object[]{}, new BeanPropertyRowMapper(ServiceRequestType.class));
			
		} catch (Exception e) {  
			e.printStackTrace();
		}
		
		return SemSubjectCountMap;
	}
	
	@Transactional(readOnly = false)
	public void truncateMasterKeyAndSrMappingTable(){ 
		
		final String sql = "truncate table portal.srtype_masterkey_mapping"; 
		jdbcTemplate.update(sql,new Object[]{});
	}
	
	@Transactional(readOnly = false)
	public void insertMasterKeyAndSrMapping(final SrTypeMasterKeyMapping map) {
		final String sql = "INSERT INTO portal.srtype_masterkey_mapping "
				+ " (masterkey,srtype_Id)"
				+ " VALUES "
				+ " (?,?)";
		
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, map.getMasterkey());
				ps.setString(2, map.getSrtype_Id());
				
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, keyHolder);

		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getSRTypesForConsumerProgramStructureId(String id) {
		String sql ="SELECT " + 
					"    st.serviceRequestName " + 
					"FROM " + 
					"    portal.srtype_masterkey_mapping m " + 
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps ON cps.id = m.masterkey " + 
					"        INNER JOIN " + 
					"    portal.service_request_types st ON st.active = 'Y'and st.startTime <= sysdate() and st.endTime >= sysdate() AND m.srtype_Id = st.id " + 
					"WHERE " + 
					"    m.masterkey = ? and m.active='Y'  "+ 
					"ORDER "+ 
					"	by st.serviceRequestName asc";
		return (ArrayList<String>)jdbcTemplate.query(sql, new Object [] {id},new SingleColumnRowMapper(String.class));
	 
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ServiceRequestType> getSRMasterKeyMappingForCpsId(String id) {
		String sql ="SELECT " + 
					"    m.*,st.serviceRequestName " + 
					"FROM " + 
					"    portal.srtype_masterkey_mapping m " + 
					"        INNER JOIN " + 
					"    exam.consumer_program_structure cps ON cps.id = m.masterkey " + 
					"        INNER JOIN " + 
					"    portal.service_request_types st ON st.active = 'Y'and st.startTime <= sysdate() and st.endTime >= sysdate() AND m.srtype_Id = st.id " + 
					"WHERE " + 
					"    m.masterkey = ?  "+ 
					"ORDER "+ 
					"	by st.serviceRequestName asc";
		return (ArrayList<ServiceRequestType>)jdbcTemplate.query(sql, new Object [] {id},new BeanPropertyRowMapper(ServiceRequestType.class));
	
	}
	
	@Transactional(readOnly = true)
public ArrayList<ConsumerProgramStructureStudentPortal> getConsumerTypeList(){
		
		
		ArrayList<ConsumerProgramStructureStudentPortal> ConsumerType = null;
		String sql =  "SELECT id,name FROM exam.consumer_type";
		
		try {
			ConsumerType = (ArrayList<ConsumerProgramStructureStudentPortal>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(ConsumerProgramStructureStudentPortal.class));
			System.out.println(ConsumerType);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
		return ConsumerType;  
		
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String>  getconsumerProgramStructureIds(String programId,String programStructureId, String consumerTypeId){
		
		String sql =  "SELECT id FROM exam.consumer_program_structure "
				+ "where programId in ("+ programId +") and "
				+ "programStructureId in ("+ programStructureId +") and "
				+ "consumerTypeId in ("+ consumerTypeId +")";

		ArrayList<String> consumerProgramStructureIds = (ArrayList<String>) jdbcTemplate.query(
				sql,  new SingleColumnRowMapper(
						String.class));
		
		System.out.println("------------->>>  consumerProgramStructureIds:--  "  + consumerProgramStructureIds);
		
		return consumerProgramStructureIds;
	}
	@Transactional(readOnly = false)
	public int  activateDeactivateSRsByCpsId(ServiceRequestType bean){
		
		String sql = "Update portal.srtype_masterkey_mapping"
				+ " set active = ? " 
				+ " where  id = ? ";
		int noOfRowsUpdated = 0;
		noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] {  
				bean.getActive() ,
				bean.getId()
		});
		return noOfRowsUpdated;
	}
	
	@Transactional(readOnly = true)
	public boolean getStudentsClosedExitSR(String sapid) {
		String  sql =  " Select count(*) from portal.service_request where (requestStatus = 'Closed') "
					+ " AND serviceRequestType = 'Exit Program' AND sapid = ? ";
		int count = (Integer)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new SingleColumnRowMapper(Integer.class));
		
		return (count>0)?true:false;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSubjectsClearedCurrentProgramPDDM(String sapid){
		List<String> passlist = new ArrayList<String>();
		try {
			String sql = "SELECT  " + 
						"    pss.subject " + 
						"FROM " + 
						"    exam.mba_passfail pf " + 
						"        INNER JOIN " + 
						"    lti.student_subject_config ssc ON ssc.id = pf.timeBoundId " + 
						"        INNER JOIN " + 
						"    exam.program_sem_subject pss ON pss.id = pf.prgm_sem_subj_id " + 
						"WHERE " + 
						"    pf.sapid = ? " + 
						"        AND pf.isResultLive = 'Y' " +
						" 		 AND pf.isPass='Y' ";
			passlist = jdbcTemplate.query(sql, new Object[]{sapid},new SingleColumnRowMapper(String.class));
		} catch (Exception e) {  
//			e.printStackTrace();
		}
		return  passlist;
	}
	

	@Transactional
	public String adhocTransactionStatusByTrackId(String track_id) {
		String status = null;
		String sql = "select `tranStatus` from `portal`.`ad_hoc_payment` where `trackId` = ?";
		try {
			status = jdbcTemplate.queryForObject(sql,new Object[] {track_id}, String.class);
		} catch (Exception e) {
			
		}
		return status;
	}
	
//	commented by Aneel Prajapati replaced by streams API
//	//Executation time 2 milliseconds.
//	//Getting the details for the content of certificate pdf. 
//	@Transactional(readOnly = true)
//	public StudentSrDTO getshowPdfDetails(Long srId) {
//
//		String sql = "SELECT es.firstName,es.lastName,es.enrollmentMonth,es.enrollmentYear,es.sem,"
//			   +" es.programStatus,es.gender,ps.requestStatus,ep.programDuration,ep.programDurationUnit,"
//               +" concat(ep.programname,' (',es.program,')') as programname,ep.program "
//               +" from exam.students as es,portal.service_request as ps,exam.programs as ep "
//               +" where es.sapid=ps.sapid and es.program=ep.program and ps.id=? order by ep.id desc limit 1";
//		
//		return jdbcTemplate.queryForObject(sql, new Object[] { srId }, new BeanPropertyRowMapper<>(StudentSrDTO.class));
//	}
	
	//getting the filepath of Service Request
	@Transactional(readOnly = true)
	public String getFilePathBySrId(String sapid) {
		String sql = "SELECT filePath FROM exam.receipt_hallticket where sapId=? and documentType = 'SR E-Bonafide' order by id desc limit 1";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, String.class);
	}
	
	//Getting the count of issuance of bonafide sr raised by user
	@Transactional(readOnly = true)
	public int getBonafideIssuedCertificateBySapidCount(String sapid) {
		String sql = "select count(serviceRequestType) from portal.service_request " + "where sapid = ? "
				   + " and serviceRequestType = 'Issuance of Bonafide' and requestStatus NOT IN ('Cancelled','Payment Failed')";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { sapid }, Integer.class);
		return count;
	}

	@Transactional(readOnly = true)
	public ServiceRequestStudentPortal getServiceRequestBySrId(Long srId) {
		String sql =  ""
				+ " SELECT * "
				+ " FROM `portal`.`service_request` `sr` "
				+ " WHERE `id` = ? ";

		ServiceRequestStudentPortal sr = jdbcTemplate.queryForObject(
			sql, 
			new Object[]{
				srId
			}, 
			new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class)
		);
		return sr;
	}
	
	@Transactional(readOnly = false)
	public Long insertFreeServiceRequest(String serviceRequestType, String sapid, String amount, String transactionStatus, String requestStatus, String serviceRequestDescription, 
										String createdBy, String lastModifiedBy, String category, String hasDocuments, String modeOfDispatch, String device) {
	String query =  "INSERT INTO portal.service_request " + 
					"(serviceRequestType, sapId, amount, tranDateTime, tranStatus, requestStatus, description, " + 
					"createdBy, createdDate, lastModifiedBy, lastModifiedDate, category, hasDocuments, modeOfDispatch, device) " + 
					"VALUES (?, ?, ?, sysdate(), ?, ?, ?, ?, sysdate(), ?, sysdate(), ?, ?, ?, ?)";
	
	KeyHolder keyHolder = new GeneratedKeyHolder();
	jdbcTemplate.update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
								statement.setString(1, serviceRequestType);
								statement.setString(2, sapid);
								statement.setString(3, amount);
								statement.setString(4, transactionStatus);
								statement.setString(5, requestStatus);
								statement.setString(6, serviceRequestDescription);
								statement.setString(7, createdBy);
								statement.setString(8, lastModifiedBy);
								statement.setString(9, category);
								statement.setString(10, hasDocuments);
								statement.setString(11, modeOfDispatch);
								statement.setString(12, device);
								return statement;
							}
						}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	@Transactional(readOnly = true)
	public List<String> getNotClosedCancelledFailedSrDescriptionList(Long sapid, String type) {
		String query =  "SELECT description " + 
						"FROM portal.service_request " + 
						"WHERE sapid = ?" + 
						"	AND serviceRequestType = ?" +
						"	AND requestStatus NOT IN ('Closed', 'Cancelled', 'Payment Failed')";
		
		return jdbcTemplate.queryForList(query, String.class, sapid, type);
	}
	
	@Transactional(readOnly = false)
	public long updateSrHasDocumentsFlag(Long serviceRequestId, String hasDocuments, String user) {
		String query =  "UPDATE portal.service_request " + 
						"SET hasDocuments = ?," + 
						"	 lastModifiedBy = ?," + 
						"    lastModifiedDate = sysdate() " + 
						"WHERE id = ?";
		
		return jdbcTemplate.update(query, hasDocuments, user, serviceRequestId);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentFatherName(Long sapid, String fatherName, String user) {
		String sql= "UPDATE exam.students " + 
					"SET fatherName = ?, " + 
					"	 lastModifiedBy = ?, " + 
					"    lastModifiedDate = sysdate() " + 
					"WHERE sapid = ?";
		
		return jdbcTemplate.update(sql, fatherName, user, sapid);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentMotherName(Long sapid, String motherName, String user) {
		String sql= "UPDATE exam.students " + 
					"SET motherName = ?, " + 
					"	 lastModifiedBy = ?, " + 
					"    lastModifiedDate = sysdate() " + 
					"WHERE sapid = ?";
		
		return jdbcTemplate.update(sql, motherName, user, sapid);
	}
	
	@Transactional(readOnly = false)
	public int updateStudentSpouseName(Long sapid, String spouseName, String user) {
		String sql= "UPDATE exam.students " + 
					"SET husbandName = ?, " + 
					"	 lastModifiedBy = ?, " + 
					"    lastModifiedDate = sysdate() " + 
					"WHERE sapid = ?";
		
		return jdbcTemplate.update(sql, spouseName, user, sapid);
	}

	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getSRBySrId(Long SrId){
		String sql="select * from portal.service_request where id=?";
		return (List<ServiceRequestStudentPortal>)jdbcTemplate.query(sql,new Object[]{SrId},new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getSRBySapIdandtype(String sapid,String type){
		String sql="select * from portal.service_request where sapid=? and serviceRequestType=? and requestStatus in ('Submitted','Closed','In Progress')";
		return (List<ServiceRequestStudentPortal>)jdbcTemplate.query(sql,new Object[]{sapid,type},new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}
	
	@Transactional(readOnly = true)
	public boolean getReRegLiveMBAX(String sapId, String CURRENT_MBAX_ACAD_MONTH, String CURRENT_MBAX_ACAD_YEAR, String type) {

		StringBuffer sql =  new StringBuffer("  SELECT count(*)  FROM `exam`.`students` `s`  INNER JOIN `exam`.`mba_x_exam_live_setting` `ls`  "
				+ " ON `ls`.`consumerProgramStructureId` = `s`.`consumerProgramStructureId`  WHERE  " 
				+" `s`.`sapid` = ?  AND `ls`.`acadsYear` = ? AND `ls`.`acadsMonth` = ? AND sysdate() BETWEEN `ls`.`startTime` AND `ls`.`endTime`  AND `ls`.`type` = ? ");
	
		int liveBeans = jdbcTemplate.queryForObject(sql.toString(), new Object[]{sapId, CURRENT_MBAX_ACAD_YEAR, CURRENT_MBAX_ACAD_MONTH, type}, Integer.class);
		return liveBeans > 0;
	}
	
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getFailedSubjectsForStudentMBAX(String sapId, String charges) {

		String sql =  "SELECT `pss`.`subject` AS `subject`, `pss`.`sem`, " + charges + " AS `amount`   FROM exam.mbax_passfail `pf`  LEFT JOIN `exam`.`program_sem_subject` `pss`  ON `pss`.`id` = `pf`.`prgm_sem_subj_id`  WHERE `sapid` = ? AND `isPass`='N' AND `isResultLive` = 'Y' ";
		List<ServiceRequestStudentPortal> failedSubjectsList = jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper<ServiceRequestStudentPortal>(ServiceRequestStudentPortal.class));
		return failedSubjectsList;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSemRegisterationByStudent(String sapId) {
		String sql =  "SELECT sem FROM exam.registration where sapid=? ";
		List<String> getSem=new ArrayList<String>();
	 getSem = (List<String>)jdbcTemplate.query(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
		
		return getSem;
	}
	
	@Transactional(readOnly = true)
	public List<String> getPssIdOfStudentBySapId(String sapid) {
	    List<String> AllpssId =new ArrayList<String>();
	    String sql = "SELECT prgm_sem_subj_id FROM exam.mba_passfail where isPass='N' and  sapid=? ";
		AllpssId = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return AllpssId;
	}
	
	@Transactional(readOnly = true)
	public int getSemByFailedPssId(ArrayList<String> getPssid) {
	    int getSem = 0;
	    String sql = "SELECT count(*) FROM exam.program_sem_subject WHERE id IN (:getPssid) and sem <= 4 ";			
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("getPssid", getPssid);
	    getSem = (Integer)namedParameterJdbcTemplate.queryForObject(sql,queryParams, new SingleColumnRowMapper(Integer.class));
		return getSem;
	}

	@Transactional(readOnly = true)
	public StudentStudentPortalBean getMonthAndYearOfSemV(String sapId) {
		StudentStudentPortalBean bean=new StudentStudentPortalBean();
		String sql = "SELECT month,year FROM exam.registration where sapid=? and sem=5";
		bean = (StudentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
		return bean;
	}
	
	@Transactional(readOnly = true)
	public List<String> getTimeBoundsubjectconfigId(String sapid) {
		List<String> timebound_subject_config_id =new ArrayList<String>();
		String sql = "select timebound_subject_config_id from lti.timebound_user_mapping where userId= ? ";
		timebound_subject_config_id=	(ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return timebound_subject_config_id;
	}
	
	@Transactional(readOnly = true)
	public String getTimeBoundStartedDateAndTime(ArrayList<String> getTimeBoundsubjectconfigId,String acadYear,String acadmonth) {
		   
		try {
		    String startTime="";
		    String sql = "SELECT startDate FROM lti.student_subject_config where id in (:getTimeBoundsubjectconfigId) and acadYear=:acadYear  and acadmonth=:acadmonth  order by sequence limit 1 ";			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("getTimeBoundsubjectconfigId", getTimeBoundsubjectconfigId);
			queryParams.addValue("acadYear", acadYear);
			queryParams.addValue("acadmonth", acadmonth);
			startTime=	(String)namedParameterJdbcTemplate.queryForObject(sql,queryParams, new SingleColumnRowMapper(String.class));
			return startTime;
	    }catch(Exception e) {
	    	return "";
	    }
	}
	
	@Transactional(readOnly = true)
	public String getLatestSemBySapId(String sapId) {
		String sql =  "Select max(sem) from exam.registration where sapid = ? ";
		String sem = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new SingleColumnRowMapper(String.class));
		return sem;
	}
	
	@Transactional(readOnly = true)
	public int getCountOfTotalPassedMScSubjectBySapid(String sapid) {
	    int count=0;
	    try {
	    	String sql = "SELECT count(*) FROM exam.mba_passfail where  sapid=:sapid and isPass='Y' and grade is not null";			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("sapid", sapid);
			count=	(Integer)namedParameterJdbcTemplate.queryForObject(sql,queryParams, new SingleColumnRowMapper(Integer.class));
			return count;
		} catch (Exception e) {
			return count;
		}
	}
	
	@Transactional(readOnly = true)
	public String getProgramStatusOfStudent(String sapid) {
		 String programStatus="";
		 try {
			 String sql = "SELECT programStatus " + 
						"FROM exam.students " + 
						"WHERE sapid = ?" + 
						"	AND sem = (	SELECT max(sem)  " + 
						"				FROM exam.students  " + 
						"				WHERE sapid = ?	)";
			 programStatus = (String)jdbcTemplate.queryForObject(sql, new Object[]{sapid,sapid}, new SingleColumnRowMapper(String.class));
			 return programStatus;
		 }catch(Exception e) {
			 return "";
		 }
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentInfo(String sapid) {
		
		String sql = "select * from exam.students "
					+ "where sapid=? "
					+ "	and sem = (select max(sem) "
					+ "				from exam.students "
					+ "				where sapid = ?) ";
		
		return  jdbcTemplate.queryForObject(
				sql, 
				new Object[]{ sapid,sapid }, 
			    new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class)
				);
	}

	@Transactional(readOnly = true)
	public String getSRAdditionalInfo(Long srId) {
		String sql = "select REPLACE(additionalInfo1, 'Others : ', '') as additionalInfo1 from portal.service_request "
				+ " where id =? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{srId}, String.class);
	}

	@Transactional(readOnly = true)
	public boolean checkExitStudent(String sapid) {
		String sql =  "select count(*) "
					+ "from portal.service_request "
					+ "where serviceRequestType='Exit Program' "
					+ "	and requestStatus='Closed' "
					+ "    and sapid=?";
		int count = jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
		return count > 0 ? true : false;
	}

	@Transactional(readOnly = false)//
	public int updateSRWithCertificateNumberAndCurrentDate(Long srId, String certificateNumber) {

		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id =?";
		return jdbcTemplate.update(sql, new Object[]{certificateNumber,srId});
	}

	//Inserting service request document record in receipt_hallticket table
	@Transactional(readOnly = false)
	public int insertSrGeneratedDocumentFilePath(String sapid, String documenType, String filePath, String userId) {
		String query =  "INSERT INTO exam.receipt_hallticket (sapid, filePath, documentType, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
						"VALUES (?, ?, ?, ?, sysdate(), ?, sysdate())";
		
		return jdbcTemplate.update(query, sapid, filePath, documenType, userId, userId);
	}

	@Transactional(readOnly = true)//
	public HashMap<String, ProgramsStudentPortalBean> getProgramMap() {
		String sql = " SELECT ps.*, p.name as programname,ps.program as programCode "
				   + " FROM exam.programs ps "
				   + " INNER JOIN exam.program p ON ps.program = p.code ";
		List<ProgramsStudentPortalBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsStudentPortalBean.class));
		
		HashMap<String, ProgramsStudentPortalBean> programMap = new HashMap<String, ProgramsStudentPortalBean>();
		for (int i = 0; i < programList.size(); i++) {
			programMap.put(programList.get(i).getProgram()+"-"+programList.get(i).getProgramStructure(), programList.get(i));
		}
		return programMap;
	}
	
	@Transactional(readOnly = false)
	public int updateFailedAdhocPayments(AdhocPaymentStudentPortalBean bean) {
		String sql = "UPDATE portal.ad_hoc_payment SET tranStatus = ?, lastModifiedDate = sysdate(), lastModifiedby = ? WHERE id = ?";
		return jdbcTemplate.update(sql, new Object[] { bean.getTranStatus(), bean.getLastModifiedBy(), bean.getId() });
	}

	@Transactional(readOnly = true)
	public String getExamMonthByAcadMonth(String month,String year) {
		String sql="select month from exam.examorder where acadMonth=? and year=?";
		return jdbcTemplate.queryForObject(sql,new Object[] {month,year}, String.class);
	}
	@Transactional(readOnly = true)
	public int getpassfailstatus(String sapId) {
		String sql="select count(*) from exam.passfail where sapid=? and isPass='N'";
		int exam=jdbcTemplate.queryForObject(sql,new Object[] {sapId},int.class );
		return exam;
	}
	@Transactional(readOnly = true)
	public ServiceRequestType getSRType(String sr)
	{
		String sql="select * from portal.service_request_types where serviceRequestName=? ";
		ServiceRequestType type=jdbcTemplate.queryForObject(sql, new Object[] {sr},new BeanPropertyRowMapper<ServiceRequestType>(ServiceRequestType.class));
		return type;
	}
	@Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getSRByStudentDetails(String sapid,String srtype,String month,String year)
	{
		String sql="select * from portal.service_request where sapId=? and serviceRequestType=? and month=? and year=? and requestStatus in ('Submitted','Closed','In Progress')";
		return (List<ServiceRequestStudentPortal>)jdbcTemplate.query(sql,new Object[]{sapid,srtype,month,year},new BeanPropertyRowMapper(ServiceRequestStudentPortal.class));
	}

	@Transactional(readOnly = true)
	public int checkIfProjectCopyCaseRemark(String sapid, int year, String month, int sem, String subject) {
		String query =  "SELECT COUNT(*) FROM exam.marks " + 
						"WHERE year = ?" + 
						"	AND month = ?" + 
						"	AND sapid = ?" + 
						"	AND sem = ?" + 
						"	AND subject = ?" + 
						"	AND remarks IN ('Copy Case', 'Copy Case-Other Student', 'Copy Case-Internet/Course Book')";
		
		return jdbcTemplate.queryForObject(query, Integer.class, year, month, sapid, sem, subject);
	}
	
	public Integer getEBonafidePurposeCount(String sapId, String srType, String purpose) {
		StringBuffer sql = new StringBuffer("SELECT  "
					+ "	COUNT(id) "
					+ "From  "
					+ "	portal.service_request "
					+ "WHERE  "
					+ "	serviceRequestType=? "
					+ "AND  ");
		if("Others : ".equals(purpose)) {
			sql.append(" additionalInfo1 like ? ");
		    purpose +="%"; 
		}
		else
			sql.append(" additionalInfo1=? ");
		
			sql.append("AND"
					+ "	sapId=? "
					+ "AND "
					+ " requestStatus NOT IN ('Cancelled','Payment Failed')"
					+ "AND  "
					+ " createdDate > '2022-09-17' ") ;
			
		return jdbcTemplate.queryForObject(sql.toString(), new Object[] {srType, purpose, sapId}, Integer.class);
	}
	
	@Transactional(readOnly=true)
	public ArrayList<String> getApprovedStudentList(String serviceRequestType,String requestStatus){
		String query="select sapid from portal.service_request where serviceRequestType=? and requestStatus=?";
		return (ArrayList<String>)jdbcTemplate.query(query,new Object[] {serviceRequestType,requestStatus}, new SingleColumnRowMapper(String.class));
	}
		
	@Transactional(readOnly = true)
	public int getCountOfTotalPassedPDDMSubjectBySapid(String sapid) {
	    int count=0;
	    try {
	    	String sql = "SELECT count(*) FROM exam.mba_passfail where  sapid=:sapid and isPass='Y'";			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("sapid", sapid);
			count=	(Integer)namedParameterJdbcTemplate.queryForObject(sql,queryParams, new SingleColumnRowMapper(Integer.class));
			return count;
		} catch (Exception e) {
			return count;
		}
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getMonthAndYearOfbysem(String sapId,String sem) {
		StudentStudentPortalBean bean=new StudentStudentPortalBean();
		String sql = "SELECT month,year FROM exam.registration where sapid=? and sem=?";
		bean = (StudentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{sapId,sem}, new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
		return bean;
	}
	
	public ArrayList<StudentStudentPortalBean> getMappedNewMasterKey() {
		ArrayList<StudentStudentPortalBean> newmasterkeyList = new ArrayList<StudentStudentPortalBean>();
		try {
		String sql =" SELECT DISTINCT cps.id as consumerProgramStructureId, p.code AS program, ps.program_structure AS programStructure,   ct.name AS consumerType  " + 
					"	FROM  " + 
					" exam.consumer_program_structure cps   " + 
					" 	INNER JOIN   " + 
					" exam.program p ON cps.programId = p.id   " + 
					"	INNER JOIN  " + 
					" exam.program_structure ps ON ps.id = cps.programStructureId   " + 
					"	INNER JOIN   " + 
					" exam.consumer_type ct ON ct.id = cps.consumerTypeId  " + 
					"					        " ;			
			newmasterkeyList = (ArrayList<StudentStudentPortalBean>) jdbcTemplate.query(sql,
					new Object[] {},
					new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		   return newmasterkeyList;
		}catch(Exception e) {
			e.printStackTrace();
		}
		 return newmasterkeyList;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getSingleStudentsDatabySapid(String sapid) {
		StudentStudentPortalBean student = new StudentStudentPortalBean();
		try {
			String sql = "SELECT sapid,sem,consumerProgramStructureId  FROM exam.registration s where " + 
					"					     s.sapid = ?  and s.sem = (Select max(sem) from exam.registration where sapid = ? ) ";
		 student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid,sapid}, new BeanPropertyRowMapper<>(StudentStudentPortalBean.class));
			return student;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return student;

	}
	
	public boolean checkStudentRaisedExit(String sapid) {
		String sql =" SELECT COUNT(*) FROM portal.service_request " + 
					" WHERE serviceRequestType = 'Exit Program' " + 
					" AND (requestStatus = 'Closed' OR requestStatus = 'Submitted') " + 
					" AND sapid = ? ";
		int count = jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
		return count > 0 ? true : false;
	}
	
	@Transactional(readOnly = false)
	public void saveEBonafideContent(List<ServiceRequestCustomPDFContentBean> eBonafidePdfContentList, String userId) {
		String sql = "INSERT INTO "
				   + " portal.servicerequest_custompdfcontent ("
			       + " serviceRequestId,contentPosition,content,createdDate,createdBy,lastModifiedDate,lastModifiedBy ) "
				   + " VALUES( ?, ?, ?, sysdate(), ?, sysdate(), ?) "
				   + " ON DUPLICATE KEY UPDATE "
				   + " content=values(content),"
				   + " createdDate=values(createdDate), "
				   + " createdBy=values(createdBy), "
				   + " lastModifiedDate=values(lastModifiedDate), "
				   + " lastModifiedBy=values(lastModifiedBy)";
		
		jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ServiceRequestCustomPDFContentBean eBonafidePdfContentBean = eBonafidePdfContentList.get(i);
						ps.setLong(1, eBonafidePdfContentBean.getServiceRequestId());
						ps.setString(2, eBonafidePdfContentBean.getContentPosition());	
						ps.setString(3, eBonafidePdfContentBean.getContent());	
						ps.setString(4, userId);	
						ps.setString(5, userId);	
						
					}

					@Override
					public int getBatchSize() {
						
						return eBonafidePdfContentList.size();
					}
		});
		
	}
	
	@Transactional(readOnly = true)
	public List<ServiceRequestCustomPDFContentBean> getEBonafidePDFContent(Long srId) {
	String sql = "SELECT "
			+ "    serviceRequestId, contentPosition, content "
			+ "FROM "
			+ "    portal.servicerequest_custompdfcontent "
			+ "WHERE "
			+ "    serviceRequestId = ? ";
	
	return jdbcTemplate.query(sql, new Object[] {srId}, new BeanPropertyRowMapper<>(ServiceRequestCustomPDFContentBean.class));
	}
	
	@Transactional(readOnly = true)
	public Integer checkEBonafidePDFContent(Long srId) {
		String sql = " SELECT "
				+ "    COUNT(serviceRequestId) "
				+ "FROM "
				+ "    portal.servicerequest_custompdfcontent "
				+ "WHERE "
				+ "    serviceRequestId = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {srId}, Integer.class);
	}
	
	@Transactional(readOnly = true)
	public int getAlreadyRaisedForExitProgram(String sapid, String serviceRequestType) {
		String sql =  " select count(*) from portal.service_request where sapid = ? and serviceRequestType = ? "
					+ " and (requestStatus = 'Closed' OR requestStatus = 'Submitted' OR requestStatus = 'Payment Pending' OR requestStatus = 'In Progress' ) ";
		int count =	(Integer)jdbcTemplate.queryForObject(sql,new Object[] {sapid, serviceRequestType}, new SingleColumnRowMapper(Integer.class));
		return count;
	}
	
	@Transactional(readOnly = true)
	public List<ProgramsStudentPortalBean> getListOfProgramsbymasterKey(String consumerProramStructureId) {
		String query =  "select consumerProgramStructureId ,newConsumerProgramStructureId ,sem as noOfSemesters from exam.exit_program_certificate_mapping where consumerProgramStructureId=? ";	
		return jdbcTemplate.query(query,new Object[] {consumerProramStructureId},new BeanPropertyRowMapper(ProgramsStudentPortalBean.class));
	}
	
    @Transactional(readOnly = true)
	public StudentSrDTO getStudentDetailsBySapId(String sapId) {
		String sql = "SELECT   "
				+ "    firstName,  "
				+ "    lastName,  "
				+ "    enrollmentMonth,  "
				+ "    enrollmentYear,  "
				+ "    sem,  "
				+ "    programStatus,  "
				+ "    gender,  "
				+ "    validityEndMonth,  "
				+ "    validityEndYear,  "
				+ "    program  "
				+ "FROM  "
				+ "    exam.students  "
				+ "WHERE  "
				+ "    sapid = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {sapId}, new BeanPropertyRowMapper<>(StudentSrDTO.class));
	}
    
    @Transactional(readOnly = true)
	public StudentSrDTO getProgramDetailsByProgram(String program) {
		String sql = " SELECT    "
				+ "    programDuration, programDurationUnit, programname, program   "
				+ " FROM   "
				+ "    exam.programs   "
				+ " WHERE   "
				+ "    program = ? "
				+ " limit 1 ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {program}, new BeanPropertyRowMapper<>(StudentSrDTO.class));
	}
    
    @Transactional(readOnly = true)
	public List<String> getApplicableSubjectList(String sapId) {

		String sql = "SELECT  " + 
				"    subject " + 
				"FROM " + 
				"    exam.student_current_subject " + 
				"WHERE " + 
				"    sapid = ? ";

		List<String> studentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new SingleColumnRowMapper(String.class));
		return studentsMarksList;
	}
    @Transactional(readOnly = false)
	public void insertServiceRequestStatusHistory(final ServiceRequestStudentPortal sr, String status) {
		try {
		final String sql = "INSERT INTO portal.service_request_status_history (id,serviceRequestType,sapId,trackId,amount,tranDateTime,service_requestcol,tranStatus,requestStatus,transactionID,requestID,merchantRefNo, " + 
				"secureHash,respAmount,description,responseCode,respPaymentMethod,isFlagged,paymentID,responseMessage,error,respTranDateTime,createdBy,createdDate,lastModifiedBy,lastModifiedDate,category, " + 
				"informationForPostPayment,hasDocuments,year,month,sem,postalAddress,requestClosedDate,additionalInfo1,cancellationReason,refundStatus, " + 
				"refundAmount,noOfCopies,issued,modeOfDispatch,srAttribute,paymentOption,bankName,landMark,houseNoName,street,locality,pin,city,state,country,device,action) " + 
				"VALUES (?,?,?,?,?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String tempSRType = sr.getServiceRequestType();
		if(tempSRType.equals(ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION)){
			tempSRType = ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION;//So that same search works for both online and offline
		}
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		final String srType = tempSRType;
		PreparedStatementCreator psc = new PreparedStatementCreator() { 
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, sr.getId());
				ps.setString(2, srType);
				ps.setString(3, sr.getSapId());
				ps.setString(4, sr.getTrackId());
				ps.setString(5, sr.getAmount());
				ps.setString(6, sr.getService_requestcol());
				ps.setString(7, sr.getTranStatus());
				ps.setString(8, sr.getRequestStatus());
				ps.setString(9, sr.getTransactionID());
				ps.setString(10, sr.getRequestID());
				ps.setString(11, sr.getMerchantRefNo());
				ps.setString(12, sr.getSecureHash());
				ps.setString(13, sr.getRespAmount());
				ps.setString(14, sr.getDescription());
				ps.setString(15, sr.getResponseCode());
				ps.setString(16, sr.getRespPaymentMethod());
				ps.setString(17, sr.getIsFlagged());
				ps.setString(18, sr.getPaymentID());
				ps.setString(19, sr.getResponseMessage());
				ps.setString(20, sr.getError());
				ps.setString(21, sr.getRespTranDateTime());
				ps.setString(22, sr.getCreatedBy());
				ps.setString(23, sr.getLastModifiedBy());
				ps.setString(24, sr.getCategory());
				ps.setString(25, sr.getInformationForPostPayment());
				ps.setString(26, sr.getHasDocuments());
				ps.setString(27, sr.getYear());
				ps.setString(28, sr.getMonth());
				ps.setString(29, sr.getSem());
				ps.setString(30, sr.getPostalAddress());
				ps.setString(31, sr.getRequestClosedDate());
				ps.setString(32, sr.getAdditionalInfo1());
				ps.setString(33, sr.getCancellationReason());
				ps.setString(34, sr.getRefundStatus());
				ps.setString(35, sr.getRefundAmount());
				ps.setString(36, sr.getNoOfCopies());
				ps.setString(37, sr.getIssued());
				ps.setString(38, sr.getModeOfDispatch());
				ps.setString(39, sr.getSrAttribute());
				ps.setString(40, sr.getPaymentOption());
				ps.setString(41, sr.getBankName());
				ps.setString(42, sr.getLandMark());
				ps.setString(43, sr.getHouseNoName());
				ps.setString(44, sr.getStreet());
				ps.setString(45, sr.getLocality());
				ps.setString(46, sr.getPin());
				ps.setString(47, sr.getCity());
				ps.setString(48, sr.getState());
				ps.setString(49, sr.getCountry());
				ps.setString(50, sr.getDevice());
				ps.setString(51, status);
				return ps;
			}
		};
		jdbcTemplate.update(psc);
		
		}
		catch (Exception e) {
			
		
		}
	}

    @Transactional(readOnly = true)
	public List<StudentMarksBean> getAStudentsMostRecentRGAssignmentMarks(String sapId) {

		String sql = "select rp.*,rp.iaScore as assignmentscore, q.finalReason as reason, q.markedForRevaluation, q.toBeEvaluated,q.sem  from exam.examorder b, " + 
				//"exam.assignmentsubmission c, " + 
				"remarkpassfail.marks rp, " + 
				"exam.quick_assignmentsubmission q " + 
				"				  where b.order = (Select max(examorder.order) from exam.examorder where assignmentMarksLive='Y')  " + 
				"				  and rp.sapid = ? " + 
				"                  and rp.assignmentYear = b.year and rp.assignmentMonth = b.month  " + 
				"                  and rp.assignmentYear = q.year and rp.assignmentMonth = q.month and rp.sapid = q.sapid and rp.subject = q.subject " + 
				//"                  and q.year = c.year and q.month = c.month and q.sapid = c.sapid and q.subject = c.subject " + 
				"                  order by q.sem, rp.subject asc";

		List<StudentMarksBean> studentsRGMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		return studentsRGMarksList;
	}
    
  
    @Transactional(readOnly = true)
    public int checkSapidExistForQ8(String sapid) {
    	String sql = "select count(*) from exam.mscaiml_md_q8_passfail where sapid =?";
    	return jdbcTemplate.queryForObject(sql, new Object[] {sapid},Integer.class);
    }
    
    @Transactional(readOnly = true)
	public List<ServiceRequestStudentPortal> getServiceRequestTypeSapIdBySrIds(List<String> serviceRequestIdList) {
		String query =  "SELECT id, serviceRequestType, sapId " + 
						"FROM portal.service_request " + 
						"WHERE id IN (:ids)";
		
		return namedParameterJdbcTemplate.query(query,new MapSqlParameterSource().addValue("ids", serviceRequestIdList), new BeanPropertyRowMapper<>(ServiceRequestStudentPortal.class));
    }
    
    @Transactional(readOnly = true)
    public List<Integer> getClearedPssIdForQ7(String sapid) {
    	
    	String sql = "" +
    			"SELECT  " + 
    			"    `prgm_sem_subj_id` " + 
    			"FROM " + 
    			"    `exam`.`mscaiml_md_q7_passfail` " + 
    			"WHERE " + 
    			"    `sapid` = ? " + 
    			"    AND `grade` IS NOT NULL " + 
    			"	 AND `isResultLive` = 'Y' " + 
    			"	 AND `isPass` = 'Y' ";
    	
    	return jdbcTemplate.query(sql, new Object[] {sapid},new SingleColumnRowMapper<Integer>(Integer.class));
    }
    
    @Transactional(readOnly = true)
    public List<Integer> getClearedPssIdForQ8(String sapid) {
    	
    	String sql = "" +
    			"SELECT  " + 
    			"    `prgm_sem_subj_id` " + 
    			"FROM " + 
    			"    `exam`.`mscaiml_md_q8_passfail` " + 
    			"WHERE " + 
    			"    `sapid` = ? " + 
    			"    AND `grade` IS NOT NULL " + 
    			"	 AND `isResultLive` = 'Y' " + 
    			"	 AND `isPass` = 'Y' ";
    	
    	return jdbcTemplate.query(sql, new Object[] {sapid},new SingleColumnRowMapper<Integer>(Integer.class));
    }
    
    @Transactional(readOnly = true)
    public List<String> getSubjectNameByPssIds(List<Integer> pssIds) {
    	String sql = "" +
    			"SELECT  " + 
    			"    `subject` " + 
    			"FROM " + 
    			"    `exam`.`program_sem_subject` " + 
    			"WHERE " + 
    			"    `id` in(:pssIds) ";
    	return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue("pssIds", pssIds),new SingleColumnRowMapper<String>(String.class));
    }
    
}
