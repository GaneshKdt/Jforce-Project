package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.ConfigurationAcads;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.ProgramBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TransactionBean;

@Component
public class PCPBookingDAO extends BaseDAO{
	
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated";
	private final String ONLINE_PAYMENT_SUCCESSFUL = "Online Payment Successful"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private final String REFUND = "Refund";
	private final String EXPIRED = "Expired"; 
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static HashMap<String, Integer> hashMap = null;
	private ArrayList<String> commonGroup1SubjectList = null;
	private ArrayList<String> commonGroup2SubjectList = null; 

	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	
	public HashMap<String, Integer> getExamOrderMap(){

		if(hashMap == null || hashMap.size() == 0){

			final String sql = " Select * from examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderAcadsBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
			hashMap = new HashMap<String, Integer>();
			for (ExamOrderAcadsBean row : rows) {
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

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	@Transactional(readOnly = true)
	public boolean alreadyDownloadedDocument(PCPBookingTransactionBean examBookingBean,String documentType)
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
	
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getSubjectsForCurrentCycle(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Subjects registered but not given exam for or results not declared for from past cycles, not the from the session which is made live
		String sql = "select r.year, r.month, r.program, r.sem , ps.subject from exam.registration r, exam.program_subject ps, exam.students s , exam.examorder eo "
				+ " where r.sapid = ? and s.sapid = ? and ps.program = r.program and ps.sem = r.sem "
				+ " and s.prgmStructApplicable = ps.prgmStructApplicable and r.month = eo.acadMonth and r.year = eo.year "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ";

		ArrayList<PCPBookingTransactionBean> subjectsList = (ArrayList<PCPBookingTransactionBean>)jdbcTemplate.query(sql, 
				new Object[]{sapId, sapId}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));

		return subjectsList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
	public void upsertOnlineInitiationTransaction(String sapid, final List<PCPBookingTransactionBean> bookingList){
		try{

			//Insert tracking numbers for current interaction
			String sql = " INSERT INTO acads.pcpbookings "
					+ "(sapid,"
					+ " subject,"
					+ " year, "
					+ " month, "
					+ " trackId, "
					+ " amount, "
					+ " tranDateTime, "
					+ " tranStatus, "
					+ " booked,"
					+ " program,"
					+ " sem,"
					+ " center,"
					+ " paymentMode)"

				+ "	VALUES(?,?,?,?,?,?,sysdate(),?,?,?,?,?,?)"
				+ " on duplicate key update "
				+ " trackId = ?,"
				+ " tranDateTime = sysdate(),"
				+ " tranStatus = ?, "
				+ " amount =? ";


			jdbcTemplate = new JdbcTemplate(dataSource);
			int[] examBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					PCPBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getSapid());
					ps.setString(2, bean.getSubject());
					ps.setString(3, bean.getYear());
					ps.setString(4, bean.getMonth());
					ps.setString(5, bean.getTrackId());
					ps.setString(6, bean.getAmount());
					ps.setString(7, bean.getTranStatus());
					ps.setString(8, bean.getBooked());
					ps.setString(9, bean.getProgram());
					ps.setString(10, bean.getSem());
					ps.setString(11, bean.getCenter());
					ps.setString(12, bean.getPaymentMode());
	

					ps.setString(13, bean.getTrackId());
					ps.setString(14, bean.getTranStatus());
					ps.setString(15, bean.getAmount());

				}
				public int getBatchSize() {
					return bookingList.size();
				}
			});

		}catch(Exception e){
			  
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void clearOldOnlineInitiationTransaction(String sapid, String trackId){
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);

			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
			String sql = "Update acads.pcpbookings "
					+ " set tranStatus = '" + EXPIRED + "'"
					+ " where sapid = ? "
					+ " and tranStatus = '"+ONLINE_PAYMENT_INITIATED+"'"
					+ " and booked = 'N'"
					+ " and paymentMode = 'Online' ";

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { sapid});

		}catch(Exception e){
			  
		}

	}
	
	
	//Saving all transactions in exam transaction table
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertOnlineTransaction(PCPBookingTransactionBean responseBean) {
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<PCPBookingTransactionBean> updateSeatsForOnlineTransaction(PCPBookingTransactionBean responseBean) {
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);


			String sql = "Update acads.pcpbookings"
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
					+ " tranStatus = ? , "
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
					responseBean.getTranStatus(),
					responseBean.getSapid(),
					responseBean.getTrackId()
			});


			sql = "Select * from acads.pcpbookings "
					+ " where sapid = ? "
					+ " and trackId = ? order by subject";


			List<PCPBookingTransactionBean> completeBookings = jdbcTemplate.query(sql, new Object[] {responseBean.getSapid(), responseBean.getTrackId()}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
			
			return completeBookings;

		}catch(Exception e){
			  
			throw e;
		}

	}
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getConfirmedBooking(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.pcpbookings a, exam.examorder b where a.year = b.year and a.month = b.acadMonth "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where acadSessionLive='Y') "
				+ " order by a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<PCPBookingTransactionBean> bookingList = (ArrayList<PCPBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public ArrayList<String> listOfDistincSapidBookingsFromMonthAndYear(PCPBookingTransactionBean pcpBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sqlOfDistinctSapid = "select distinct sapid from acads.pcpbookings where year = ? and month = ? and booked ='Y'";
		ArrayList<String> listOfDistinctSapid = (ArrayList<String>)jdbcTemplate.query(sqlOfDistinctSapid, new Object[]{pcpBean.getYear(),pcpBean.getMonth()},new SingleColumnRowMapper(String.class));
		return listOfDistinctSapid;
	}
	@Transactional(readOnly = true)
	public HashMap<String,ArrayList<PCPBookingTransactionBean>> mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag(PCPBookingTransactionBean pcpBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,ArrayList<PCPBookingTransactionBean>> mapOfSapidAndPCPBookingListToBeReturned = new HashMap<String,ArrayList<PCPBookingTransactionBean>>();
		
		//Get all the distinct sapid bookings from month and year//
		String sqlOfDistinctSapid = "select distinct sapid from acads.pcpbookings where year = ? and month = ? and booked ='Y'";
		ArrayList<String> listOfDistinctSapid = (ArrayList<String>)jdbcTemplate.query(sqlOfDistinctSapid, new Object[]{pcpBean.getYear(),pcpBean.getMonth()},new SingleColumnRowMapper(String.class));
		for(String sapid : listOfDistinctSapid){
			ArrayList<PCPBookingTransactionBean> getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag = getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag(sapid,pcpBean);
			
			if(getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag!=null && getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag.size()>0){
				mapOfSapidAndPCPBookingListToBeReturned.put(sapid, getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag);
			}
			
		}
		return mapOfSapidAndPCPBookingListToBeReturned;
	}
	@Transactional(readOnly = false)
	public void batchInsertOfDocumentRecords(final ArrayList<PCPBookingTransactionBean> transactionBeanList,final String documentType){
		String sql = "INSERT INTO exam.receipt_hallticket "
				+ " (sapid,year,month,filePath,documentType,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
				+ " VALUES(?,?,?,?,?,?,sysdate(),?,sysdate())";
				/*+" ON DUPLICATE KEY UPDATE "
				+" lastModifiedBy = ? ";*/
		
		int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				PCPBookingTransactionBean transactionBean = transactionBeanList.get(i);
				ps.setString(1,transactionBean.getSapid());
				ps.setString(2,transactionBean.getYear());
				ps.setString(3,transactionBean.getMonth());
				ps.setString(4,transactionBean.getFilePath());
				ps.setString(5,documentType);
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
	public ArrayList<PCPBookingTransactionBean> getConfirmedBookingForReceipts(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.* FROM acads.pcpbookings a, exam.examorder b where a.year = b.year and a.month = b.acadMonth "
				+ " and sapid = ? and a.booked = 'Y' and  b.order = (Select max(examorder.order) from exam.examorder where acadSessionLive='Y') "
				+ " order by a.subject asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<PCPBookingTransactionBean> bookingList = (ArrayList<PCPBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getConfirmedBookingForReceiptsFromMonthAndYearWithoutAcadsFlag(String sapid,PCPBookingTransactionBean pcpBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT a.*, e.firstName, e.lastName FROM acads.pcpbookings a,exam.students e where "
				+ " a.booked = 'Y' and a.year = ? and a.month = ? and a.sapid = ? "
				+ " and e.sapid = a.sapid";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<PCPBookingTransactionBean> bookingList = (ArrayList<PCPBookingTransactionBean>) jdbcTemplate.query(sql, new Object[]{pcpBean.getYear(),pcpBean.getMonth(),sapid}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public HashMap<String, String> getProgramDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		//String sql = "SELECT * FROM exam.programs";
		String sql = "SELECT * FROM exam.program";
		List<ProgramBean> programList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramBean.class));
		HashMap<String, String> programCodeNameMap = new HashMap<String, String>();

		for (int i = 0; i < programList.size(); i++) {
			//programCodeNameMap.put(programList.get(i).getProgram(), programList.get(i).getProgramname());
			programCodeNameMap.put(programList.get(i).getCode(), programList.get(i).getName());
		}

		return programCodeNameMap;
	}
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getPaymentInitiatedSRList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from acads.pcpbookings  where tranStatus = ?  and (time_to_sec(timediff(sysdate(), tranDateTime)) > 1800) ";
		return (ArrayList<PCPBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{ONLINE_PAYMENT_INITIATED}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
	}
	@Transactional(readOnly = false)
	public void markBookingsExpired(PCPBookingTransactionBean pcpBooking) {
		try{

			jdbcTemplate = new JdbcTemplate(dataSource);

			//Delete old transaction tracking number which would have got created last time student clicked for Online Payment but didn't enter anything on payment gatway.
			String sql = "Update acads.pcpbookings "
					+ " set tranStatus = '" + EXPIRED + "'"
					+ " where sapid = ? "
					+ " and trackId = ?  ";

			int noOfRowsUpdated = jdbcTemplate.update(sql, new Object[] { pcpBooking.getSapid(), pcpBooking.getTrackId()});

		}catch(Exception e){
			  
		}
		
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
	@Transactional(readOnly = true)
	public List<ConfigurationAcads> getCurrentConfigurationList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.configuration order by configurationType asc";
		List<ConfigurationAcads> currentConfList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ConfigurationAcads.class));
		return currentConfList;
	}
	@Transactional(readOnly = false)
	public void updateConfiguration(ConfigurationAcads configuration) {
		String sql = "Update exam.configuration set "
				+ " startTime=? , "
				+ " endTime = ? ,"
				+ " lastModifiedBy = ? ,"
				+ " lastModifiedDate = sysdate() "
				+ " where configurationType = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { 
				configuration.getStartTime(),
				configuration.getEndTime(),
				configuration.getLastModifiedBy(),
				configuration.getConfigurationType()
		});
		
	}
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getFailedSubjects(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.passfail  where sapId = ?  and isPass = 'N' order by sem, subject asc ";
		return (ArrayList<PCPBookingTransactionBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
	}
	@Transactional(readOnly = true)
	public ArrayList<PCPBookingTransactionBean> getConfirmedBookingForGivenYearMonth(PCPBookingTransactionBean bean, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM   exam.students s,  acads.pcpbookings a where  a.year = ? "
				+ " and a.month = ? and a.booked = 'Y' "
				+ " and a.sapid = s.sapid ";
		
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		
		sql +=  " group by a.sapid, a.subject "
				+ " order by a.sapid asc";


		ArrayList<PCPBookingTransactionBean> bookingList = (ArrayList<PCPBookingTransactionBean>) 
				jdbcTemplate.query(sql, new Object[]{bean.getYear(), bean.getMonth()}, new BeanPropertyRowMapper(PCPBookingTransactionBean.class));
		return bookingList;
	}
	@Transactional(readOnly = true)
	public HashMap<String, StudentAcadsBean> getAllStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentAcadsBean> students = (ArrayList<StudentAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentAcadsBean.class));
		
		HashMap<String, StudentAcadsBean> studentsMap = new HashMap<>();
		for (StudentAcadsBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}
		
		return studentsMap;
	}
	@Transactional
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
	@Transactional(readOnly = true)
	public HashMap<String, CenterAcadsBean> getICLCMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers ";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterAcadsBean> centers = (ArrayList<CenterAcadsBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterAcadsBean.class));
		
		HashMap<String, CenterAcadsBean> icLcMap = new HashMap<>();
		for (CenterAcadsBean center : centers) {
			icLcMap.put(center.getCenterCode(), center);
		}
		
		return icLcMap;
	}



}
