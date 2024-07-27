package com.nmims.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.controllers.BaseController;

@Repository("serviceRequestDao")
public class ServiceRequestDAO{

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate  namedParameterJdbcTemplate;
//	private DataSource dataSource;					//null value
	public BaseController ob = new BaseController();
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/*
	 * fetchCorporateExamCenterIdNameMap fetches the complete address described in
	 * the query
	 */
	@Transactional(readOnly = true)
	public HashMap<String, String> fetchCorporateExamCenterIdNameMap() {

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.corporate_examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

	
		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName() + "," + bean.getLocality() + ","
					+ bean.getCity() + "," + bean.getAddress());

		}

		return examCenterIdNameMap;
	}
	
	/*
	 * fetchExamCenterIdMap returns the complete address that are queried from the
	 * Database
	 * 
	 */
	@Transactional(readOnly = true)
	public HashMap<String, String> fetchExamCenterIdNameMap() {
		

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName() + "," + bean.getLocality() + ","
					+ bean.getCity() + "," + bean.getAddress());
		}

		return examCenterIdNameMap;
	}
	/**
	 * Checking for Service Request Type of Issuance Of Transcript 
	 *
	 * @return ServiceRequestType 
	 */
	@Transactional(readOnly = true)
	public List<String> checkingForTranscript(String Srid ) {
		
		String sql="SELECT "+
			  "id  "+  
			  " FROM "  +
			    " portal.service_request "+    
			   " WHERE " +
			        "serviceRequestType= 'Issuance of Transcript' and "+
			         "id in(" +Srid + ")"; 
		List<String> servicerequest= jdbcTemplate.queryForList(sql, String.class);
		return servicerequest;
	}
	@Transactional(readOnly = true)
	public List<String> getBlockedSapids() {


		String sql = "SELECT distinct sapid FROM exam.blocked_hallticket where  "
				+ "  blocked = 'Y'  ";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{}, new SingleColumnRowMapper(String.class));
		return subjectList;

	}
	@Transactional(readOnly = true)
	public StudentExamBean getSingleStudentsData(String sapid) {
		
		StudentExamBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";


			
			@SuppressWarnings({ "unchecked", "rawtypes" })
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
	public ArrayList<String> getSubjectsBooked(String sapid) {

		String sql = "SELECT subject FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'P' and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " and paymentMode = 'DD' "
				+ " order by a.subject asc";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<String> bookingList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public boolean isConfigurationLive(String configurationType) {
		
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
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedBooking(String sapid) {
		
		String sql = "SELECT * FROM exam.exambookings a, exam.examorder b where a.year = b.year and a.month = b.month "
				+ " and sapid = ? and a.booked = 'Y' and subject NOT IN ('Project', 'Module 4 - Project') and  b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
				+ " order by a.examDate, a.examTime, a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public boolean hallTicketDownloadedStatus(String month, String sapid) {
		int count = 0;
		try {
			

			String sql = "select count(*) from exam.exambookings WHERE sapid=? and month=? and htDownloaded = 'Y'";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid, month},Integer.class);

			if(count> 0) {
				return true;
			}
			else {
				return false;
				}

		} catch (Exception e) {
			
			return false;
		}
	}
	@Transactional(readOnly = true)
	public boolean hallTicketDownloadedStatusMbaWx(String month, String sapid) {
		int count = 0;
		try {
			

			String sql = "select count(*) from exam.mba_wx_bookings WHERE sapid=? and month=? and htDownloaded = 'Y'";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid, month},Integer.class);

			if(count> 0) {
				return true;
			}
			else {
				return false;
				}

		} catch (Exception e) {
			
			return false;
		}
	}
	@Transactional(readOnly = true)
	public ServiceRequestBean checkIfSrPaymentSuccessful(String trackId) {
		
		String sql = "SELECT * FROM portal.service_request where trackId=? and tranStatus='Payment Successful' and requestStatus<>'Cancelled' limit 1";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		ServiceRequestBean bean = null;
		try { 
			bean = (ServiceRequestBean) jdbcTemplate.queryForObject(sql, new Object[]{trackId}, new BeanPropertyRowMapper(ServiceRequestBean.class));
			
		} catch (DataAccessException e) { 
			
		} 
		return bean;
		 
	}

	@Transactional(readOnly = true)
	public List<ServiceRequestBean> getGeneratedSrDocuments(String sapid) {
		String query =  "SELECT id, sapid, filePath, documentType " + 
						"FROM exam.receipt_hallticket " + 
						"WHERE sapid = ?" + 
						"	AND documentType = 'SR E-Bonafide'";

		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ServiceRequestBean.class), sapid);
	}
	
	//Updating filepath into servicerequest_documents table
	@Transactional(readOnly = false)
	public void insertSrGeneratedDocumentFilePath(String sapid, String documenType, String filePath, String userId) {
		String query =  "INSERT INTO exam.receipt_hallticket (sapid, filePath, documentType, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
						"VALUES (?, ?, ?, ?, sysdate(), ?, sysdate())";
		
		jdbcTemplate.update(query, sapid, filePath, documenType, userId, userId);
	}

	//Getting the request status for the student who raise service request for Exit progarm
	@Transactional(readOnly = true)
	public String getExitWithdrawalStatusForSR(String sapid) {
		String srType = "Exit Program";
		String sql = "select requestStatus from portal.service_request where sapid=? and serviceRequestType=?";
		String withdrawalStatus = null;
		try {
			withdrawalStatus = jdbcTemplate.queryForObject(sql, new Object[] { sapid, srType }, String.class);
			return withdrawalStatus;
		} catch (Exception e) {
			withdrawalStatus = "empty";
			return withdrawalStatus;
		}
	}

	//Get SR type and status for Service Request
	@Transactional(readOnly = true)
	public HashMap getSRTypeAndStatus(String srId) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String sql = "select serviceRequestType,requestStatus from portal.service_request where id=?";
		List<Map<String, Object>> row = jdbcTemplate.queryForList(sql, new Object[] { srId });
		//stream is used for extracting key and values from the List<Map>
		row.stream().flatMap(m -> m.entrySet().stream()).
		forEach(e -> map.put(e.getKey(), e.getValue()));
		return map;
	}

	@Transactional(readOnly = true)
	public String getSapIdBySrId(String serviceRequestIdList) {
		String sql = "select sapid from  portal.service_request where id=?";
		return (String) jdbcTemplate.queryForObject(sql, new Object[] { serviceRequestIdList }, String.class);
	}

	// Executation time 15 milliseconds.
	// Get filePath for the Service Request
	@Transactional(readOnly = true)
	public String getFilePathBySrId(String srId) {
		String sql = "select filePath from portal.servicerequest_documents where serviceRequestId=? order by id desc limit 1";
		return jdbcTemplate.queryForObject(sql, new Object[] { srId }, String.class);
	}
	
	@Transactional(readOnly = false)//
	public void updateSRWithCertificateNumberAndCurrentDate(Integer srId, String certificateNumber) {

		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id =?";
		jdbcTemplate.update(sql, new Object[]{certificateNumber,srId});

	}
	
	@Transactional(readOnly = false)//
	public void updateSRWithCertificateNumberAndCurrentDate(String ServiceRequestIdList, String certificateNumber) {

		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id =?";
		jdbcTemplate.update(sql, new Object[]{certificateNumber,ServiceRequestIdList});

	}
	
	@Transactional(readOnly = false)//
	public void updateSRWithCertificateNumberAndCurrentDate(PassFailExamBean bean, String certificateNumber) {
		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());
		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id in ( "+ commaSeparatedList +" )  ";
		jdbcTemplate.update(sql, new Object[]{certificateNumber});

	}
		
	@Transactional(readOnly = true)//
	public ServiceRequestBean getServiceRequestForSR(Integer srId) {
		String sql = "select additionalInfo1 from portal.service_request "
				+ " where id =? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{srId}, new BeanPropertyRowMapper<>(ServiceRequestBean.class));
	}
	
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}
	
	
	@Transactional(readOnly = true)//
	public String getStudentUserIDCourseCompletionBadge(String sapid) {
		try {
		String sql = "SELECT userId FROM open_badges.users where sapid= ? limit 1";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { sapid }, String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	@Transactional(readOnly = true)//
	public String getStudentUniquehashCourseCompletionBadge(String userId) {
		try {
		String sql = "SELECT uniquehash FROM open_badges.badge_issued where userId = ?  and awardedAt = 'Program Completion' limit 1";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { userId }, String.class);
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public boolean insertAWSFilePath(String sapid,String Uniquehash,String fileType,String S3FilePath) {
		try {
			int count = 0;
			String query =  "INSERT INTO `portal`.`aws_uploaded_filespath` (`sapid`, `uniquehash`, `fileType`, `filepath`) VALUES (?, ?, ?, ?)"
							+ "on duplicate key update filepath = ?";
				
			count = (int) jdbcTemplate.update(query, sapid,Uniquehash,fileType,S3FilePath,S3FilePath);

			if(count> 0) {
				return true;
			}
			else {
				return false;
				}

		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public List<String> getProgramApplicableForProgramCompletion() throws Exception {
		List<String> programList = new ArrayList<String>();
		
	
		String sql = "select p.program from open_badges.badge b " + 
				"inner join "+ 
				"open_badges.badge_criteria bc on bc.badgeId =b.badgeId " + 
				"inner join " + 
				"open_badges.badge_criteria_param bcp on bcp.criteriaId = bc.criteriaId " + 
				"inner join " + 
				"open_badges.badge_masterkey_mapping map on b.badgeId = map.badgeId " + 
				"INNER JOIN exam.programs p on map.consumerProgramStructureId= p.consumerProgramStructureId " + 
				"where " + 
				"bcp.criteriaName = 'programCompletion' group by p.program";

		//@SuppressWarnings({ "unchecked", "rawtypes" })
		 programList = jdbcTemplate.queryForList(sql, new Object[]{}, String.class);
		
		 return programList;

	}
	
	@Transactional(readOnly=true)
	public boolean checkIfAppliedForSubjectRepeat(String sapid,String subject) {		

		    String sql = " select count(*) from  portal.service_request where serviceRequestType like 'subject repeat%' and tranStatus ='Payment Successful' " + 
					"   and sapid=? and informationForPostPayment=?";			
			
			int count= jdbcTemplate.queryForObject(sql, new Object[] {sapid,subject}, Integer.class);			
			
			return count>0?true:false;			
	}
	
}
