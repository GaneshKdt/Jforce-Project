package com.nmims.daos;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.AnnouncementMasterBean;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.ConsumerProgramStructureStudentPortal;
import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.Event;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.ExecutiveExamOrderStudentPortalBean;
import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.FacultyCourseFeedBackBean;
import com.nmims.beans.Job;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.MentionedDataBean;
import com.nmims.beans.MettlExamUpcomingBean;
import com.nmims.beans.Online_EventBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.Posts;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.SessionAttendanceFeedbackStudentPortal;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.SessionTrackBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.beans.TimetableStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.beans.louReportBean;
import com.nmims.beans.programStudentPortalBean;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaginationHelper;
import com.nmims.helpers.SFConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

@Service
//@Transactional(readOnly = true)

public class PortalDao extends BaseDAO{
	/*
	 * public void init(){ SFConnection sf= new
	 * SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN); this.connection =
	 * sf.getConnection(); }
	 */
	@Autowired
	ApplicationContext appContext;
   
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;

	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;

	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	private PartnerConnection connection;	

	@Autowired
	@Qualifier("slave1")
	private DataSource slaveDataSource;
	
	public PortalDao() {  
//		this.connection = SFConnection.getConnection();
//		System.out.println("inPortalDao got connection: "+this.connection);
 	}
    @Autowired
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static HashMap<String, BigDecimal> hashMap = null;
	private static HashMap<String,BigDecimal> orderMap = null;
	@Autowired
	SFConnection sfc;
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
	public ArrayList<String> getProgramStructure() {
	  jdbcTemplate = new JdbcTemplate(dataSource);

	        String sql = "Select program_structure from exam.program_structure where program_structure not in ('mba x','') ";

	        ArrayList<String> structureList = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));

	        return structureList;
	    }

	@Transactional(readOnly = false)
	public int updateProgramStatus(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.students set programStatus = 'Program Suspension' where sapid = ? ";
		//try{
			return jdbcTemplate.update(sql,new Object[]{sapid});
		//}catch(Exception e){
//			e.printStackTrace();
		//}
	}

	//to check if student needs to register for event
	/*  @Cacheable("returnList")*/
	@Transactional(readOnly = true)
	public ArrayList<String> getStudentApplicableForSubject(String bs){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select distinct s.sapid "
				+ " from exam.registration r, exam.program_subject ps, exam.students s "
				+ "where r.sapid = s.sapid "
				+ "and r.program = ps.program "
				+ "and r.sem = ps.sem "
				+ "and  ps.subject = 'Business Statistics' "
				+ "and s.sapid not in (select distinct pf.sapid from exam.passfail pf where subject = 'Business Statistics' and isPass = 'Y') "
				+ "and STR_TO_DATE(concat('30-',validityEndMonth,'-',validityEndYear), '%d-%b-%Y') >= curdate() ";


		ArrayList<String> returnList = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		/*//System.out.println("List:  "+returnList);*/
		return returnList;
	}
	
	@Transactional(readOnly = true)
	public List getStudentBeanForSapIdList(ArrayList<String> sapidList){
		
		Map<String, Object> params = new HashMap<String, Object>();
		  params.put("sapidList", sapidList);
		  NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		  
		  List<StudentStudentPortalBean> returnList = namedParameterJdbcTemplate.query(
		    "select * from exam.students where sapid in (:sapidList)",
		    params,
		    
		  new BeanPropertyRowMapper(StudentStudentPortalBean.class)
				  );
		
		
		return returnList;
	}

	@Transactional(readOnly = true)
	public boolean checkIfRegisteredForEvent(String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select count(*) from portal.event_registration where sapid = ?";
		try{
			int recordCount = jdbcTemplate.queryForObject(sql,new Object[]{userId},Integer.class);
			//System.out.println("Record count :"+recordCount);
			if(recordCount > 0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
//			e.printStackTrace();
			return false;
		}

	}

	@Transactional(readOnly = true)
	public Online_EventBean getLiveOnlineEvent(String program,String sem,String PrgmStructApplicable){
		Online_EventBean onlineEvent = new Online_EventBean();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = " select * from portal.online_Event where startDate <= now() and endDate >= now() "
		        + " and (program is null or program ='All' or program = ? ) and (sem is null or sem ='All' or sem = ? ) "
				+ " and (PrgmStructApplicable is null or PrgmStructApplicable ='All' or PrgmStructApplicable =? ) limit 1";
		
		try{
			onlineEvent = (Online_EventBean)jdbcTemplate.queryForObject(query, new Object[]{program,sem ,PrgmStructApplicable},new BeanPropertyRowMapper(Online_EventBean.class));
		}catch(Exception e){
			//System.out.println("getLiveOnlineEvent Error: "+e.getMessage());
			//return null;
		}
		return onlineEvent;
	}
	
	@Transactional(readOnly = true)
	public boolean getOnlineEventRegistration(String sapid,String eventId)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = " select count(*) from portal.event_registration where response is not null and sapid = ? and online_EventId = ? ";
		try{
			int rowCount = (int)jdbcTemplate.queryForObject(query,new Object[] {sapid,eventId}, Integer.class);
			if(rowCount > 0)
			{
				return true;
			}
		}catch(Exception e){
			//System.out.println("Online Event Registration :"+e.getMessage());
		}
		return false;
	}

	@Transactional(readOnly = true)
	//This method is a provision which will redirect the student to certificate portal if his authentication fails in studentportal//
	public boolean checkIfStudentBelongsToCertificatePortal(String userId){
		DataSource source = (DataSource)appContext.getBean("certDataSource");
		jdbcTemplate = new JdbcTemplate(source);
		String sql = " select count(*) from jforce_certificate.user where sapid = ? ";
		try{
			int recordCount = jdbcTemplate.queryForObject(sql,new Object[]{userId},Integer.class);
			////System.out.println("Record count :"+recordCount);
			if(recordCount > 0){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			//e.printStackTrace();
			return false;
		}

	}



	@Transactional(readOnly = false)
	public String insertAnnouncementMasterKey(final List<String> consumerprogramIdsList,final int announcementId) {
				
		//System.out.println("id from method"+announcementId);
		final String sql = "INSERT INTO portal.announcement_master_key_pivot (announcementId, master_key) VALUES (?, ?)" ; 
			
		String errorMessage = "";

		try {
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					
					ps.setInt(1, announcementId);
					ps.setString(2, consumerprogramIdsList.get(i));		
				}

				@Override
				public int getBatchSize() {
					return consumerprogramIdsList.size();
				}
			  });
			//System.out.println("batchInsertExtendedTestTime "+batchInsertExtendedTestTime);
			//System.out.println("List of DAO "+consumerprogramIdsList);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();

			return "Error in extension , Error : "+e.getMessage();
		}
		return errorMessage;
	}

	
	public int insertAnnouncement(List<String> consumerProgramIdsList ,AnnouncementStudentPortalBean bean) 
	{
		final String sql = "INSERT INTO portal.announcements "
				+ "(subject,"
				+ "description,"
				+ "startDate,"
				+ "endDate,"
				+ "active, "
				+ "category,"
				+ "attachment1,"
				+ "attachment2,"
				+ "attachment3,"
				+ "createdBy,"
				+ "createdDate,"
				+ "lastModifiedBy, "
				+ "lastModifiedDate )"
			//	+ "program,"
			//	+ "programStructure)"
				+ "VALUES ( ?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


		jdbcTemplate = new JdbcTemplate(dataSource);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		final AnnouncementStudentPortalBean a = bean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				ps.setString(1, a.getSubject());
				ps.setString(2, a.getDescription());
				ps.setString(3, a.getStartDate());
				ps.setString(4, a.getEndDate());
				ps.setString(5, a.getActive());
				ps.setString(6, a.getCategory());
				ps.setString(7, a.getAttachmentFile1Path());
				ps.setString(8, a.getAttachmentFile2Path());
				ps.setString(9, a.getAttachmentFile3Path());
				ps.setString(10, a.getCreatedBy());
				ps.setString(11, a.getLastModifiedBy());
			//	ps.setString(12, a.getProgram());
			//	ps.setString(13, a.getProgramStructure());

				return ps;
			}
		};
		


		jdbcTemplate.update(psc, keyHolder);//update

		int id = keyHolder.getKey().intValue();
		//System.out.println("List Of Ids "+consumerProgramIdsList);
		
		//Commented By Riya  :- It doesnt get inserted in post table since 2020
		//insertAnnouncementPostTable(a,id,"insert");
		
		//System.out.println("Announcement Id = "+id);
		insertAnnouncementMasterKey(consumerProgramIdsList,id);
		
		/* Insert into announcement temporary table */
		insertAnnouncementIntoTempTable(consumerProgramIdsList, id,bean);
		return id;

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
	public ArrayList<FacultyCourseFeedBackBean> getListOfSessionsAndTheirLastDates(String commaSeperatedSubjects){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select subject,MAX(date) as lastSessionDate from acads.sessions s where s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"' and s.subject in "
				+ " ("+commaSeperatedSubjects+") group by s.subject having MAX(date) >= NOW() ";
		////System.out.println(sql);
		ArrayList<FacultyCourseFeedBackBean> listOfSessionsAndTheirLastDates = (ArrayList<FacultyCourseFeedBackBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(FacultyCourseFeedBackBean.class));

		return listOfSessionsAndTheirLastDates;
	}

	@Transactional(readOnly = true)
	public double getExamOrderFromAcadMonthAndYear(String acadMonth,String acadYear)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT examorder.order FROM exam.examorder where acadMonth=? and year=? ";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{acadMonth,acadYear},Integer.class);


		}catch(DataAccessException e){
			//e.printStackTrace();
		}

		return examOrder;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, BigDecimal> getExamOrderMap(){

		if(orderMap == null || orderMap.size() == 0){

			final String sql = " Select * from exam.examorder";
			jdbcTemplate = new JdbcTemplate(dataSource);

			List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
			orderMap = new HashMap<String, BigDecimal>();
			for (ExamOrderStudentPortalBean row : rows) {
				orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
				orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			}
		}
		////System.out.println("Map "+hashMap);
		return orderMap;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<ExamOrderStudentPortalBean> getLiveFlagDetails(){
		List<ExamOrderStudentPortalBean> liveFlagList = new ArrayList<ExamOrderStudentPortalBean>();

		final String sql = " Select * from exam.examorder order by examorder.order ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		liveFlagList = (ArrayList<ExamOrderStudentPortalBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderStudentPortalBean>(ExamOrderStudentPortalBean.class));

		return liveFlagList;
	}
	
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<ExecutiveExamOrderStudentPortalBean> getResultliveFlagList(){
		List<ExecutiveExamOrderStudentPortalBean> liveFlagList = new ArrayList<ExecutiveExamOrderStudentPortalBean>();

		final String sql = " Select * from exam.executive_examorder order by executive_examorder.order ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		liveFlagList = (ArrayList<ExecutiveExamOrderStudentPortalBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExecutiveExamOrderStudentPortalBean>(ExecutiveExamOrderStudentPortalBean.class));

		return liveFlagList;
	}

	@Transactional(readOnly = true)
	public double getExamOrderFromExamMonthAndYear(String examMonth,String examYear)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT examorder.order FROM exam.examorder where month=? and year=? ";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{examMonth,examYear},Integer.class);


		}catch(Exception e){
			//e.printStackTrace();
		}

		return examOrder;
	}
	
	@Transactional(readOnly = true)
	//get content live for current drive//
	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double)((Integer) jdbcTemplate.queryForObject(sql,new Object[]{},Integer.class));
			return examOrder;

		}catch(Exception e){
			//e.printStackTrace();
			return 0.0;
		}



	}
	
	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getWaivedInSubjects(StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select sem,subject from exam.program_sem_subject where consumerProgramStructureId = ? and "
				+ " subject not in ( select if(subject = 'Business Communication and Etiquette','Business Communication',subject) as subject from exam.passfail where sapid in (?,?) and isPass = 'Y') and sem < ?";
		return (ArrayList<ProgramSubjectMappingStudentPortalBean>) jdbcTemplate.query(sql,new Object[] {student.getConsumerProgramStructureId(), student.getPreviousStudentId(), student.getSapid(), student.getSem()}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPassSubjectsForMbawxStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select sem from exam.mba_passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	@Transactional(readOnly = false)
	public void insertUserMailRecord(final ArrayList<MailStudentPortalBean> mailList, final String fromUserId,final String fromEmailId,final long mailTemplateId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId) VALUES(?,?,sysdate(),?,?,?) ";
		try{
			int[] batchUpdateDocumentRecordsResultSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					MailStudentPortalBean mailBean = mailList.get(i);
					ps.setString(1,StringUtils.join(mailBean.getSapIdRecipients(),","));
					ps.setString(2,StringUtils.join(mailBean.getMailIdRecipients(),","));
					ps.setString(3,fromUserId);
					ps.setString(4,fromEmailId);
					ps.setString(5,String.valueOf(mailTemplateId));
				}

				@Override
				public int getBatchSize() {
					return mailList.size();
				}
			});
		}catch(Exception e){
//			e.printStackTrace();
		}


	}

	@Transactional(readOnly = false)
	public long insertMailRecord(final ArrayList<MailStudentPortalBean> mailList,final String fromUserId){
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
		/*String sql = " INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId) VALUES(?,?,sysdate(),?,?,?) ";
		jdbcTemplate.update(sql,new Object[]{mailList.get(0).getSubject(),fromUserId,mailList.get(0).getFilterCriteria(),mailList.get(0).getBody(),mailList.get(0).getFromEmailId()});*/
	}
	

	
	public void updateAnnouncement(List<String> masterKeys,AnnouncementStudentPortalBean bean) 
	{
		String sql = "Update portal.announcements set "
				+ "subject=?,"
				+ "description=?,"
				+ "startDate=?,"
				+ "endDate=?,"
				+ "active=?,"
				+ "category=?,"
				+ "attachment1 = ?,"
				+ "attachment2 = ?,"
				+ "attachment3 = ?,"
				+ "program = ?,"
				+ "programStructure = ?, "
				+ "createdBy = ? , "
				+ "lastModifiedBy = ? , "
				+ " createdDate = current_timestamp() ,"
				+ "lastModifiedDate = current_timestamp() "
				+ " where id= ?";



		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1,bean.getSubject());
				preparedStatement.setString(2,bean.getDescription());
				preparedStatement.setString(3,bean.getStartDate());
				preparedStatement.setString(4,bean.getEndDate());
				preparedStatement.setString(5,bean.getActive());
				preparedStatement.setString(6,bean.getCategory());
				preparedStatement.setString(7,bean.getAttachmentFile1Path());
				preparedStatement.setString(8,bean.getAttachmentFile2Path());
				preparedStatement.setString(9,bean.getAttachmentFile3Path());
				preparedStatement.setString(10,bean.getProgram());
				preparedStatement.setString(11,bean.getProgramStructure());
				preparedStatement.setString(12,bean.getCreatedBy());
				preparedStatement.setString(13,bean.getLastModifiedBy());
				preparedStatement.setString(14,bean.getId());
				
			}});
		
		//Commented By Riya  :- It doesnt get inserted in post table since 2020
		//insertAnnouncementPostTable(bean,Integer.parseInt(bean.getId()),"update");
		
		/*update In Announcement temporaryTable */
		updateAnnouncementTempTable(masterKeys,bean);
		
		
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("rawtypes")
	public List<AnnouncementStudentPortalBean> getAllAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements order by startdate desc";
		List<AnnouncementStudentPortalBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
		return announcements;
	}	

	@SuppressWarnings("rawtypes")
	public List<AnnouncementStudentPortalBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String	sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() order by startDate desc ";
				List<AnnouncementStudentPortalBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	@Transactional(readOnly = true)
	//Added for SAS
	  @SuppressWarnings("rawtypes")
	public List<AnnouncementStudentPortalBean> getAllActiveAnnouncements(String program,String consumerProgramStructureId){

		jdbcTemplate = new JdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer();
		
		/* Commented By Riya as data is being retrieved from temp table
		/*if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
			//old query	 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";
			sql = "SELECT  " + 
			 		"    * " + 
			 		"FROM " + 
			 		"    portal.announcement_master_key_pivot amkp " + 
			 		"        INNER JOIN " + 
			 		"    portal.announcements a ON amkp.announcementId = a.id " + 
			 		"WHERE " + 
			 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate()  and a.active = 'Y' order by startDate desc ";
			
		}
		//added a condition for MBA-WX announcements
		else if("MBA - WX".equalsIgnoreCase(program) || "MBA-WX".equalsIgnoreCase(program)) {
		//old query		sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
		
			 sql = "SELECT  " + 
				 		"    * " + 
				 		"FROM " + 
				 		"    portal.announcement_master_key_pivot amkp " + 
				 		"        INNER JOIN " + 
				 		"    portal.announcements a ON amkp.announcementId = a.id " + 
				 		"WHERE " + 
				 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate()  and a.active = 'Y' order by startDate desc ";
		}else{			
			//old query	 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			 sql = "SELECT  " + 
			 		"    * " + 
			 		"FROM " + 
			 		"    portal.announcement_master_key_pivot amkp " + 
			 		"        INNER JOIN " + 
			 		"    portal.announcements a ON amkp.announcementId = a.id " + 
			 		"WHERE " + 
			 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.active = 'Y' order by startDate desc ";
		}*/
		sql.append("SELECT  " + 
		 		"    * " + 
		 		"FROM " + 
		 		"    portal.announcement_denormalized " + 
		 		"WHERE " + 
		 		"    master_key = ? AND startDate <= current_timestamp()  and endDate > current_timestamp() and active = 'Y' order by startDate desc ");
		
		List<AnnouncementStudentPortalBean> announcements = null;
		 try {
		 announcements = jdbcTemplate.query(sql.toString(),new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,consumerProgramStructureId);
					
				}}, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
		 }catch(Exception e)
		 {
//			 e.printStackTrace();
		 }
		//Commented By Riya as it returns null
		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	  
		@Transactional(readOnly = true)
		public PageStudentPortal<AnnouncementStudentPortalBean> getAllActiveAnnouncementsPaginated(String program,String consumerProgramStructureId , int pageNo , int pageSize ){

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = null;
			String countSql = null;
			if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
				//old query	 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";
				sql = "SELECT  " + 
				 		"    * " + 
				 		"FROM " + 
				 		"    portal.announcement_master_key_pivot amkp " + 
				 		"        INNER JOIN " + 
				 		"    portal.announcements a ON amkp.announcementId = a.id " + 
				 		"WHERE " + 
				 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.program <> 'All' and programStructure <> 'All' and a.active = 'Y'  order by startDate desc";
				
				countSql = "SELECT  " + 
				 		"   count(*) " + 
				 		"FROM " + 
				 		"    portal.announcement_master_key_pivot amkp " + 
				 		"        INNER JOIN " + 
				 		"    portal.announcements a ON amkp.announcementId = a.id " + 
				 		"WHERE " + 
				 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.program <> 'All' and programStructure <> 'All' and a.active = 'Y'  order by startDate desc";
				
			}
			//added a condition for MBA-WX announcements
			else if("MBA - WX".equalsIgnoreCase(program) || "MBA-WX".equalsIgnoreCase(program)) {
			//old query		sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			
				 sql = "SELECT  " + 
					 		"    * " + 
					 		"FROM " + 
					 		"    portal.announcement_master_key_pivot amkp " + 
					 		"        INNER JOIN " + 
					 		"    portal.announcements a ON amkp.announcementId = a.id " + 
					 		"WHERE " + 
					 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.program <> 'All' and a.active = 'Y'  order by startDate desc";
				 countSql = "SELECT  " + 
					 		"   count(*) " + 
					 		"FROM " + 
					 		"    portal.announcement_master_key_pivot amkp " + 
					 		"        INNER JOIN " + 
					 		"    portal.announcements a ON amkp.announcementId = a.id " + 
					 		"WHERE " + 
					 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.program <> 'All' and a.active = 'Y'  order by startDate desc";
				 
			}else{
				//old query	 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
				 sql = "SELECT  " + 
				 		"    * " + 
				 		"FROM " + 
				 		"    portal.announcement_master_key_pivot amkp " + 
				 		"        INNER JOIN " + 
				 		"    portal.announcements a ON amkp.announcementId = a.id " + 
				 		"WHERE " + 
				 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.active = 'Y'  order by startDate desc";
				 
				 countSql = "SELECT  " + 
					 		"   count(*) " + 
					 		"FROM " + 
					 		"    portal.announcement_master_key_pivot amkp " + 
					 		"        INNER JOIN " + 
					 		"    portal.announcements a ON amkp.announcementId = a.id " + 
					 		"WHERE " + 
					 		"    amkp.master_key = ? AND a.startDate <= sysdate()  and a.endDate > sysdate() and a.active = 'Y'  order by startDate desc";
			}
			PaginationHelper<AnnouncementStudentPortalBean> pagingHelper = new PaginationHelper<AnnouncementStudentPortalBean>();
			PageStudentPortal<AnnouncementStudentPortalBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, new Object[]{consumerProgramStructureId}, pageNo, pageSize, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
			
			//List<AnnouncementBean> announcements = jdbcTemplate.query(sql,new Object[]{consumerProgramStructureId}, new BeanPropertyRowMapper(AnnouncementBean.class));
			
		//	List<AnnouncementBean> announcements = page.getPageItems();	
			
			
		/*	List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
			if(jobAnnouncements != null && jobAnnouncements.size() > 0){
				announcements.addAll(jobAnnouncements);
			}*/
			
			return page;
		}	
	  
	  
	@Transactional(readOnly = true)	
	public String getStudentProgramStructure(String sapid){
		String programStructure = null;

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT PrgmStructApplicable FROM exam.students where sapid = ? limit 1";
		try{
			ArrayList<String> programStructureList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

			if(programStructureList != null && programStructureList.size() > 0){
				programStructure = programStructureList.get(0);
				////System.out.println("Program = "+ programStructure);
			}
		}catch(Exception e){
//			e.printStackTrace();
		}

		return programStructure;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentStudentPortalBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students s where "
					+ "    s.sapid = ?  and s.sem = (Select max(sem) from exam.students where sapid = ? )  ";



			////System.out.println("SQL = "+sql);

			student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid, sapid}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			//e.printStackTrace();
			return student;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList getLeadDetail(String id) {
		
		return null;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentsMostRecentRegistrationData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentStudentPortalBean student = null;
		try{
			String sql = "SELECT * FROM exam.registration r where "
					+ "    r.sapid = ?  and r.sem = (Select max(sem) from exam.registration where sapid = ? )  ";
			////System.out.println("SQL = "+sql);
			student = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid, sapid
			}, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			
			//set program for header here so as to use it in all other places
			student.setProgramForHeader(student.getProgram());
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = true)
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncementForSingleStudent(String startDate,int pageNo, int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// get all announcement for student from Enrollment month and year
		String sql = "SELECT * FROM portal.announcements where startDate >=?  and endDate <= sysdate() order by startDate desc";
		String countSql = "SELECT count(*) FROM portal.announcements where  startDate >=?  and endDate <= sysdate() order by startDate desc";

		////System.out.println("SQL = "+sql);

		PaginationHelper<AnnouncementStudentPortalBean> pagingHelper = new PaginationHelper<AnnouncementStudentPortalBean>();
		PageStudentPortal<AnnouncementStudentPortalBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, new Object[]{startDate}, pageNo, pageSize, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));

		return page;
	}
	
	@Transactional(readOnly = true)
	//Added for SAS
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncementForSingleStudent(String program,String consumerProgramStructureId,String startDate,int pageNo, int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// get all announcement for student from Enrollment month and year
		StringBuffer sql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		
		/* Commented By Riya as some of the announcement data is shifted in the history table 
		/*if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
			
			sql = "  SELECT  " + 
					"    a.* " + 
					"FROM " + 
					"    portal.announcement_master_key_pivot amkp " + 
					"        INNER JOIN " + 
					"    portal.announcements a ON amkp.announcementId = a.id " + 
					"WHERE " + 
					"  a.startDate >= ? AND endDate <= sysdate() AND master_key = ?  " +
					"        order by startDate desc ";
			countSql = " SELECT  " + 
					"   count(*) " + 
					"FROM " + 
					"    portal.announcement_master_key_pivot amkp " + 
					"        INNER JOIN " + 
					"    portal.announcements a ON amkp.announcementId = a.id " + 
					"WHERE " + 
					"   a.startDate >= ? AND endDate <= sysdate() AND master_key = ?  " +					
					"       order by startDate desc";			
		// sql = "SELECT * FROM portal.announcements where startDate >=?  and endDate <= sysdate() and  program= ? and programStructure = ?  order by startDate desc ";
		// countSql="SELECT count(*) FROM portal.announcements where startDate >=?  and endDate <= sysdate() and  program= ? and programStructure = ?  order by startDate desc";
		
		
		
		}else{
			
		sql= "SELECT  " + 
				"    a.* " + 
				"FROM " + 
				"    portal.announcement_master_key_pivot amkp " + 
				"        INNER JOIN " + 
				"    portal.announcements a ON amkp.announcementId = a.id " + 
				"WHERE " + 
				"   a.startDate >= ?  and endDate <= sysdate() and master_key = ? order by startDate desc ";	
			
		countSql= "SELECT  " + 
				"    count(*) " + 
				"FROM " + 
				"    portal.announcement_master_key_pivot amkp " + 
				"        INNER JOIN " + 
				"    portal.announcements a ON amkp.announcementId = a.id " + 
				"WHERE " + 
				"   a.startDate >= ?  and endDate <= sysdate() and master_key = ?";		
			//  and endDate <= sysdate() and 		
			// sql = "SELECT * FROM portal.announcements where startDate >=? and a.endDate <= sysdate() and (program= 'DBM' || program = 'All') and (programStructure = 'Jul2014' || programStructure = 'All') order by startDate desc";
			// countSql="SELECT count(*) FROM portal.announcements where startDate >=? and a.endDate <= sysdate() and (program= 'DBM' || program = 'All') and (programStructure = 'Jul2014' || programStructure = 'All') order by startDate desc";
		
		}*/
		
		sql.append("SELECT    a.*  FROM "
				+ "portal.announcement_master_key_pivot amkp "
				+ "INNER JOIN "
				+ "portal.announcements a ON amkp.announcementId = a.id "
				+ "WHERE "
				+ "a.startDate >= ?  and endDate <= current_timestamp() and master_key = ? "
				+ "union "
				+ "SELECT    a.*  FROM "
				+ "portal.announcement_master_key_pivot_history amkp "
				+ "INNER JOIN "
				+ "portal.announcements_history a ON amkp.announcementId = a.id "
				+ "WHERE "
				+ "a.startDate >= ?  and endDate <= current_timestamp() and master_key = ? order by startDate desc ;	");	
			
		    countSql.append(" select count(*) from (SELECT    a.*  FROM "
				+ "portal.announcement_master_key_pivot amkp "
				+ "INNER JOIN "
				+ "portal.announcements a ON amkp.announcementId = a.id "
				+ "WHERE "
				+ "a.startDate >= ?  and endDate <= current_timestamp() and amkp.master_key = ?  "
				+ "union "
				+ "SELECT    a.*  FROM "
				+ "portal.announcement_master_key_pivot_history amkp "
				+ "INNER JOIN "
				+ "portal.announcements_history a ON amkp.announcementId = a.id "
				+ "WHERE "
				+ "a.startDate >= ?  and endDate <= current_timestamp() and master_key = ? "
				+ "order by startDate desc) as t  ;");		
		
		
		
		
		 
		
		
		
		//System.out.println("SQL = "+sql);
		PaginationHelper<AnnouncementStudentPortalBean> pagingHelper = new PaginationHelper<AnnouncementStudentPortalBean>();
		PageStudentPortal<AnnouncementStudentPortalBean> page = new PageStudentPortal<AnnouncementStudentPortalBean>();
		try {
		page =  pagingHelper.fetchPage(jdbcTemplate, countSql.toString(), sql.toString(), new Object[]{startDate,consumerProgramStructureId,startDate,consumerProgramStructureId}, pageNo, pageSize, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
		}catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		//System.out.println("page available"+page.getPagesAvailable());
		//System.out.println("Start Date : "+startDate);
		//System.out.println("Consumer Structure Id: "+consumerProgramStructureId);
		//System.out.println("Program : "+program);
		//System.out.println("Row Count"+page.getRowCount());
		
		
				return page;
	}
	
	
	@Transactional(readOnly = true)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AnnouncementStudentPortalBean findById(String id){
	boolean history = false;
	//old query 	String sql = "SELECT * FROM portal.announcements WHERE id = ? ";
	String sql = "SELECT a.*,       " + 
				"				   coalesce(`amkp`.`count`,0) as `count`       " + 
				"				   FROM portal.announcements a        " + 
				"				  left join (        " + 
				"				  select announcementId, count(*) as `count` from portal.announcement_master_key_pivot group by announcementId       " + 
				"				  ) amkp ON a.id = amkp.announcementId   " + 
				"                  where a.id = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		AnnouncementStudentPortalBean announcement = new AnnouncementStudentPortalBean();
		try {
		announcement = (AnnouncementStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
		}catch(Exception e)
		{
//			e.printStackTrace();
			history = true;
			
		}
		/*Find In history Table */
		if(history)
			announcement = findByIdInHistory(id);
		return announcement;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteAnnouncement(String announcementId) throws Exception
	{
		//System.out.println("deleting announcement...");
		//Delete announcement from mapping and temp table
		int i = deleteWholeAnnouncementMapping(announcementId);
		
		
		
		
		if(i == 0)//It is present in announcement history table
			deleteAnnouncementInHistory(announcementId);
		else {
			String sql = "Delete from portal.announcements WHERE id = ?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql,new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,announcementId);
				}
				}); 
		}
			
		
		deleteWholeAnnouncementMappingInTemp(announcementId);
		
		/*====== As the No Announcement's data not being inserted in the post table since 2020=========*/
		/*Posts post = findSessionPostByReferenceId(Integer.parseInt(id)); 
		deleteSessionPost(post.getPost_id());
		//deleteFromRedis(post);
		refreshRedis(post);*/
	} 
	
	@Transactional(readOnly = true)
	public Posts findSessionPostByReferenceId(int id) {  
		List<Posts> posts= new ArrayList<Posts>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from lti.post where referenceId =? and type='Announcement'";
		try {
			posts = (ArrayList<Posts>)jdbcTemplate.query(sql, new Object[]{id},new BeanPropertyRowMapper(Posts.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		 return posts.get(0);   
	}
	
	@Transactional(readOnly = false)
	public void deleteSessionPost(String id) {  
		//System.out.println();
		String sql1 = "Delete from lti.post where post_id=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql1, new Object[] { 
				id
		});
	}
/*	public String deleteFromRedis(Posts posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
	  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";
	    	  //System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	}*/
	public String refreshRedis(Posts posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
	  	    String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
	    	  //System.out.println("IN deletePostByTimeboundIdAndPostIdFromRedis() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
//			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public PageStudentPortal<AnnouncementStudentPortalBean> getAnnouncementsPage(int pageNo, int pageSize) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		//String sql = "SELECT * FROM portal.announcements ";
		/*String sql = "SELECT a.*, " + 
				" coalesce(`amkp`.`count`,0) as `count` " + 
				" FROM portal.announcements a  " + 
				"left join (  " + 
				"select announcementId, count(*) as `count` from portal.announcement_master_key_pivot group by announcementId " + 
				") amkp ON a.id = amkp.announcementId";
		String countSql = "SELECT count(*) FROM portal.announcements  ";*/
		StringBuffer sql = new StringBuffer();
		StringBuffer countSql = new StringBuffer();
		sql.append("SELECT a.*, coalesce(`amkp`.`count`,0) as `count`     "
				+ "FROM portal.announcements a     "
				+ "inner join ( select announcementId, count(*) as `count` "
				+ "from portal.announcement_master_key_pivot group by announcementId) "
				+ "amkp ON a.id = amkp.announcementId  "
				+ "union "
				+ "SELECT a.*, coalesce(`amkp`.`count`,0) as `count`     "
				+ "FROM portal.announcements_history a     "
				+ "inner join "
				+ "( select announcementId, count(*) as `count` "
				+ "from portal.announcement_master_key_pivot_history group by announcementId) "
				+ "amkp ON a.id = amkp.announcementId order by id desc");
		countSql.append("select count(*) from (SELECT announcementId FROM  "
				+ "  portal.announcement_master_key_pivot_history group by (announcementId) "
				+ "	 union  "
				+ "	 SELECT announcementId FROM   "
				+ "	 portal.announcement_master_key_pivot group by (announcementId) ) as t ");


		////System.out.println("SQL = "+sql);

		PaginationHelper<AnnouncementStudentPortalBean> pagingHelper = new PaginationHelper<AnnouncementStudentPortalBean>();
		PageStudentPortal<AnnouncementStudentPortalBean> page = new PageStudentPortal<AnnouncementStudentPortalBean>();
		
		try {
		
			page =  pagingHelper.fetchPage(jdbcTemplate, countSql.toString(), sql.toString(), new Object[]{}, pageNo, pageSize, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
			
		}catch(Exception e)
		{
			
		}

		return page;
	}

	@Transactional(readOnly = true)
	public List<AnnouncementStudentPortalBean> getAllNewJobAnnouncements() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT jobs.jobId,"
				+ "    jobs.designation,"
				+ "    jobs.jobDescription,"
				+ "    jobs.desiredProfile,"
				+ "    jobs.experience,"
				+ "    jobs.location,"
				+ "    jobs.keywords,"
				+ "    jobs.contactmailId,"
				+ "    jobs.contactPhone,"
				+ "    jobs.jobPostDate,"
				+ "    jobs.companyId,"
				+ "		company.companyName, "
				+ "company.aboutCompany, "
				+ "company.industryType, "
				+ "company.websiteUrl "
				+ "FROM placement.jobs, placement.company "
				+ "where jobs.companyId = company.companyid "
				+ "and jobPostDate  >= SUBDATE(sysdate(), INTERVAL 10 DAY) order by jobs.jobPostDate desc";

		List<Job> jobs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Job.class));
		List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
		if(jobs != null){
			for (int i = 0; i < jobs.size(); i++) {
				AnnouncementStudentPortalBean a = new AnnouncementStudentPortalBean();
				Job job = (Job)jobs.get(i);
				a.setCategory("Placement");
				a.setStartDate(job.getJobPostDate());
				a.setSubject(job.getDesignation()+", "+job.getLocation());
				a.setDescription("New Job Added in Placement Portal for position of "+job.getDesignation()+" at "+job.getLocation()+". Please visit Placement portal for more details.");
				announcements.add(a);
			}
		}

		return announcements;
	}
	
	@Transactional(readOnly = false)
	public void updateStudentInfo(String sapid, String fatherName, String motherName) {
		String sql = "Update exam.students set "
				+ "fatherName=?,"
				+ "motherName=?,"
				+ "updatedByStudent= 'Y'"
				+ " where sapid= ? ";



		jdbcTemplate = new JdbcTemplate(dataSource);
		////System.out.println("updateStudentInfo QUERY-->"+fatherName+""+motherName);
		jdbcTemplate.update(sql, new Object[] { 
				fatherName,
				motherName,
				sapid
		});

	}
	
	@Transactional(readOnly = false)
	// This method is overloaded just below to have address fields like city,state,country etc.
	public void updateStudentContact(StudentStudentPortalBean student, String address, MailSender mailer) {
		String sql =  " Update exam.students set "
					+ " emailId=?,"
					+ " mobile=?,"
					+ " altPhone=?,"
					+ " fatherName=?,"
					+ " motherName=?,"
					
					+ " address=?,"
					+ " houseNoName=?,"
					+ " street=?,"
//					+ " landMark=?,"
					+ " locality=?,"
					+ " pin=?,"
					+ " city=?,"
					+ " state=?,"
					+ " country=?,"
					
					+ " industry=?,"
					+ " designation=?,"
					+ " lastModifiedBy=?,"
					+ " lastModifiedDate=sysdate(),"
					+ " abcId=?"
					
					+ " where sapid= ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					student.getEmailId(),
					student.getMobile(),
					student.getAltPhone(),
					student.getFatherName(),
					student.getMotherName(),
					address,
					student.getHouseNoName(),
					student.getStreet(),
//					student.getLandMark(),
					student.getLocality(),
					student.getPostalCode(),
					student.getCity(),
					student.getState(),
					student.getCountry(),
					student.getIndustry(),
					student.getDesignation(),
					student.getSapid(),
					student.getAbcId(),
					student.getSapid()
			});
		}catch(Exception e){
//			e.printStackTrace();
			try{
				jdbcTemplate.update(sql, new Object[] { 
						student.getEmailId(),
						student.getMobile(),
						student.getAltPhone(),
						student.getFatherName(),
						student.getMotherName(),
						address,
						student.getHouseNoName(),
						student.getStreet(),
//						student.getLandMark(),
						student.getLocality(),
						student.getPostalCode(),
						student.getCity(),
						student.getState(),
						student.getCountry(),
						student.getIndustry(),
						student.getDesignation(),
						student.getSapid(),
						student.getSapid()
				});
			}catch(Exception e2){
//				e.printStackTrace();
				mailer.mailStackTrace("Unable to update student profile on portal", e2);
			}
		}


	}
	
	@Transactional(readOnly = false)
	// overloaded updateStudentContact below to have address fields like city,state,country etc.
	public void updateStudentContact(String sapid, String email, String mobile,String address,	String altPhone,
									 String fatherName,String motherName,String industry,String designation,MailSender mailer,
									 	  String houseNoName,
									      String street,
									      String locality,
									      String landMark,
									      String city,
									      String state,
									      String country,
									      String pin) {
		String sql = "Update exam.students set "
				+ " emailId=?,"
				+ " mobile=?,"
				+ " address=?,"
				+ " altPhone=?,"
				+ " fatherName=?,"
				+ " motherName=?,"
				+ " lastModifiedBy=?,"
				+ " lastModifiedDate=sysdate(),"
				+ " industry=?,"
				+ " designation=?, "
				+ " houseNoName = ?,"
				+ " street = ?,"
				+ " locality = ?,"
				+ " landMark = ?," 
				+ " city = ?," 
				+ " state = ?,"
				+ " country=?,"
				+ " pin=?" 
				+ " where sapid= ? ";



		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					email,
					mobile,
					address,
					altPhone,
					fatherName,
					motherName,
					sapid,
					industry,
					designation,
					   houseNoName,
				       street,
				       locality,
				       landMark,
				       city,
				       state,
				       country,
				       pin,
					sapid
			});
			//System.out.println("In updateStudentContact updated sucess.");
		}catch(Exception e){
//			e.printStackTrace();
			try{
				jdbcTemplate.update(sql, new Object[] { 
						email,
						mobile,
						address,
						altPhone,
						fatherName,
						motherName,
						sapid,
						industry,
						designation,
						   houseNoName,
					       street,
					       locality,
					       landMark,
					       city,
					       state,
					       country,
					       pin,
						sapid
				});
			}catch(Exception e2){
				mailer.mailStackTrace("Unable to update student profile on portal", e2);
			}
		
		}


	}
	
	@Transactional(readOnly = false)
	public void updateOneSignalId(String sapid, String onsignalId) {
		String sql = "Update exam.students set "
				+ " onesignalId=?"
				+ " where sapid= ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					onsignalId,
					sapid
			});
		}catch(Exception e){
//			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = false)
	public void updateFirebaseToken(String sapid, String token) {
		String sql = "Update exam.students set "
				+ " firebaseToken=?"
				+ " where sapid= ? ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			jdbcTemplate.update(sql, new Object[] { 
					token,
					sapid
			});
		}catch(Exception e){
//			e.printStackTrace();
		}
	}

	 @Transactional(readOnly = false)
	 @Deprecated
	 public void updateStudentContactFromSFDC(String sapid, String email, String dob, String mobile,String address,	String altPhone,String fatherName,String motherName,String studentImageUrl,String validityEndYear,String validityEndMonth,String centerId,String centerName) {
			String sql = "Update exam.students set "
					+ " emailId=?,"
					+ " mobile=?,"
					+ " dob=DATE_FORMAT(?, '%Y-%m-%d'),"
					+ " address=?,"
					+ " altPhone=?,"
					+ " fatherName=?,"
					+ " motherName=?,"
					+ " validityEndYear=?,"
					+ " validityEndMonth=?,"
					+ " lastModifiedBy=?,"
					+ " lastModifiedDate=sysdate()," 
					+ " imageUrl=?,"
					+ " centerCode=?,"
					+ " centerName=? "
					+ " where sapid= ? ";



			jdbcTemplate = new JdbcTemplate(dataSource);
			try{
				jdbcTemplate.update(sql, new Object[] { 
						email,
						mobile,
						dob,
						address,
						altPhone,
						fatherName,
						motherName,
						validityEndYear,
						validityEndMonth,
						sapid,
						studentImageUrl,
						centerId,
						centerName,
						sapid
				});
			}catch(Exception e){
				//System.out.println("#################################################################################");
				//System.out.println("Error : unable to update profile in portal");
				//System.out.println("#################################################################################");
//				e.printStackTrace();
			}


		}
	 
	 @Transactional(readOnly = false)
	 public int updateStudentDetailsFromSFDC(String sapid, String firstName, String lastName, String fatherName, String motherName, String husbandName, 
				 	String dob, String studentImageUrl, String email, String mobile, String altPhone, 
				 	String address, String houseNoName, String street, String landMark, String locality, String pin, String city, String state, String country, 
				 	String centerName, String centerCode, String program, String prgmStructApplicable, 
				 	String enrollmentMonth, String enrollmentYear, String validityEndMonth, String validityEndYear, String programChanged, String oldProgram, 
					String highestQualification, String consumerType, int consumerProgramStructureId) throws Exception {

		String sql = "UPDATE exam.students "
					+ "SET firstName=?, "
						+ "lastName=?, "
						+ "fatherName=?, "
						+ "motherName=?, "
						+ "husbandName=?, "
						+ "dob=DATE_FORMAT(?, '%Y-%m-%d'), "
						+ "imageUrl=?, "
						+ "emailId=?, "
						+ "mobile=?, "
						+ "altPhone=?, "
						+ "address=?, "
						+ "houseNoName=?, "
						+ "street=?, "
						+ "landMark=?, "
						+ "locality=?, "
						+ "pin=?, "
						+ "city=?, "
						+ "state=?, "
						+ "country=?, "
						+ "centerName=?, "
						+ "centerCode=?, "
						+ "program=?, "
						+ "PrgmStructApplicable=?, "
						+ "enrollmentMonth=?, " 
						+ "enrollmentYear=?, "
						+ "validityEndMonth=?, "
						+ "validityEndYear=?, "
						+ "programChanged=?, "
						+ "oldProgram=?, "
						+ "highestQualification=?, "
						+ "consumerType=?, "
						+ "consumerProgramStructureId=?, "
						+ "lastModifiedBy=?, "
						+ "lastModifiedDate=sysdate() " 
					+ "WHERE sapid=? ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		int noOfRowsUpdated = jdbcTemplate.update(sql, new PreparedStatementSetter() {
		
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, firstName);
				ps.setString(2, lastName);
				ps.setString(3, fatherName);
				ps.setString(4, motherName);
				ps.setString(5, husbandName);
				ps.setString(6, dob);
				ps.setString(7, studentImageUrl);
				ps.setString(8, email);
				ps.setString(9, mobile);
				ps.setString(10, altPhone);
				ps.setString(11, address);
				ps.setString(12, houseNoName);
				ps.setString(13, street);
				ps.setString(14, landMark);
				ps.setString(15, locality);
				ps.setString(16, pin);
				ps.setString(17, city);
				ps.setString(18, state);
				ps.setString(19, country);
				ps.setString(20, centerName);
				ps.setString(21, centerCode);
				ps.setString(22, program);
				ps.setString(23, prgmStructApplicable);
				ps.setString(24, enrollmentMonth);
				ps.setInt(25, Integer.valueOf(enrollmentYear));
				ps.setString(26, validityEndMonth);
				ps.setInt(27, Integer.valueOf(validityEndYear));
				ps.setString(28, programChanged);
				ps.setString(29, oldProgram);
				ps.setString(30, highestQualification);
				ps.setString(31, consumerType);
				ps.setInt(32, consumerProgramStructureId);
				ps.setString(33, "Salesforce Admin");
				ps.setString(34, sapid);
			}
		});
		
		return noOfRowsUpdated;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllConsumerTypes() throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT name FROM exam.consumer_type";
		ArrayList<String> consumerTypeNameList = (ArrayList<String>) jdbcTemplate.queryForList(sql, String.class);
		
		return consumerTypeNameList;
	}
	
	@Transactional(readOnly = true)
	public int getConsumerTypeIdByConsumerType(String consumerType) {
		int consumerTypeId = 0;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT id FROM exam.consumer_type "
					+ "WHERE name = ? ";
		
		try {
			consumerTypeId = jdbcTemplate.queryForObject(sql, Integer.class, consumerType);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
		
		return consumerTypeId;
	}
	
	@Transactional(readOnly = true)
	public int getConsumerProgramStructureIdById(int consumerTypeId, int programId, int programStructureId) {
		int consumerProgramStructureId = 0;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT id FROM exam.consumer_program_structure "
					+ "WHERE consumerTypeId = ? "
					+ "AND programId = ? "
					+ "AND programStructureId = ?";
		try {
			consumerProgramStructureId = jdbcTemplate.queryForObject(sql, Integer.class, consumerTypeId, programId, programStructureId);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
		
		return consumerProgramStructureId;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getCenterCodeAndNameMap() {
		HashMap<String, String> centerCodeAndNameMap = new HashMap<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM exam.centers " 
					+ "ORDER BY centerName ASC";
		
		try {
			List<CenterStudentPortalBean> centers = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CenterStudentPortalBean.class));
			
			if(centers.size() > 0) {
				for(CenterStudentPortalBean center: centers) {
					String key = center.getCenterCode();
					
					if(!centerCodeAndNameMap.containsKey(key))
						centerCodeAndNameMap.put(key, center.getCenterName());
				}
			}
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
		
		return centerCodeAndNameMap;
	}

	 @Transactional(readOnly = true)
	public ArrayList<SessionAttendanceFeedbackStudentPortal> getPendingFeedbacksOld(String userId, String program) {
		ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select saf.*, s.*, f.* from acads.session_attendance_feedback saf, acads.sessions s, acads.faculty f, exam.examorder eo "
				+ " where saf.sessionId = s.id and saf.sapid = ? and (saf.feedbackGiven is null or saf.feedbackGiven = 'N') "
				+ " and saf.facultyId = f.facultyId "
				+ " and s.year = eo.year and s.month = eo.acadMonth "
				+ " and s.date < SUBDATE(sysdate(), INTERVAL 1 DAY)"
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y')"
				+ " and (s.isCancelled <> 'Y' or s.isCancelled is null) ";
		if ("MBA - WX".equalsIgnoreCase(program)) {
			sql = sql + " AND s.date >= DATE_FORMAT(STR_TO_DATE(CONCAT("+CURRENT_MBAWX_ACAD_YEAR+", '"+CURRENT_MBAWX_ACAD_MONTH+"', '30'),'%Y %M %d'),'%Y-%m-%d') ";
		}
		try {
			pendingFeedback = (ArrayList<SessionAttendanceFeedbackStudentPortal>)jdbcTemplate.query(sql, new Object[]{userId},new BeanPropertyRowMapper(SessionAttendanceFeedbackStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pendingFeedback;
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<SessionAttendanceFeedbackStudentPortal> getPendingFeedbacks(String userId, StudentStudentPortalBean studentRegistrationData) {
		ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String year = studentRegistrationData.getYear();
		String month = studentRegistrationData.getMonth();
		
		String sql =" SELECT  " + 
					"    saf.*, s.*, f.* " + 
					" FROM " + 
					"    acads.session_attendance_feedback saf, " + 
					"    acads.sessions s, " + 
					"    acads.faculty f, " + 
					"    exam.examorder eo " + 
					" WHERE " + 
					"    saf.sessionId = s.id " + 
					"        AND saf.sapid = ? " + 
					"        AND (saf.feedbackGiven IS NULL " + 
					"        OR saf.feedbackGiven = 'N') " + 
					"        AND saf.facultyId = f.facultyId " + 
					"        AND s.year = eo.year " + 
					"        AND s.month = eo.acadMonth " + 
					"        AND s.date < SUBDATE(SYSDATE(), INTERVAL 1 DAY) " + 
					"        AND eo.order = (SELECT examorder.order FROM exam.examorder WHERE acadSessionLive = 'Y' AND year = ? AND acadMonth = ? ) " + 
					"        AND (s.isCancelled <> 'Y' OR s.isCancelled IS NULL)";
		if (("MBA - WX".equalsIgnoreCase(studentRegistrationData.getProgram())) || ("M.Sc. (AI & ML Ops)".equalsIgnoreCase(studentRegistrationData.getProgram())) ) {
			if (month.equalsIgnoreCase("Apr")) {
				month = "Jan";
			}else if(month.equalsIgnoreCase("Oct")) {
				month = "Jul";
			}
			sql = sql 	+ " AND s.date BETWEEN (SELECT MIN(startDate) FROM lti.student_subject_config "
						+ " 	WHERE acadYear = "+CURRENT_MBAWX_ACAD_YEAR+" AND acadMonth = '"+CURRENT_MBAWX_ACAD_MONTH+"') "
						+ " AND (SELECT MAX(endDate) FROM lti.student_subject_config "
						+ "		WHERE acadYear = "+CURRENT_MBAWX_ACAD_YEAR+" AND acadMonth = '"+CURRENT_MBAWX_ACAD_MONTH+"' ) ";
		}
		try {
			pendingFeedback = (ArrayList<SessionAttendanceFeedbackStudentPortal>)jdbcTemplate.query(sql, new Object[]{userId, year, month},
					new BeanPropertyRowMapper(SessionAttendanceFeedbackStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pendingFeedback;
	}

	 @Transactional(readOnly = true)
	public ArrayList<SessionAttendanceFeedbackStudentPortal> getPendingFeedbacksSAS(String userId, String year, String month) {
		ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select saf.*, s.*, f.* from acads.session_attendance_feedback saf, acads.sessions s, acads.faculty f, exam.examorder eo "
				+ " where saf.sessionId = s.id and saf.sapid = ? and (saf.feedbackGiven is null or saf.feedbackGiven = 'N') "
				+ " and saf.facultyId = f.facultyId "
				+ " and s.year = eo.year and s.month = eo.acadMonth "
				+ " and s.date < SUBDATE(sysdate(), INTERVAL 1 DAY)"
				+ " and eo.order = (select examorder.order from exam.examorder where  year = ? and acadMonth = ? and acadSessionLive = 'Y')"
				+ " and (s.isCancelled <> 'Y' or s.isCancelled is null)";
		try {
			pendingFeedback = (ArrayList<SessionAttendanceFeedbackStudentPortal>)jdbcTemplate.query(sql, new Object[]{userId,year,month},new BeanPropertyRowMapper(SessionAttendanceFeedbackStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pendingFeedback;
	}
	

	 @Transactional(readOnly = false)
	public void saveFeedback(SessionAttendanceFeedbackStudentPortal feedback) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// checking Session Confirmation as getting Incorrect value for null for q1Response integer 
		if("Y".equals(feedback.getStudentConfirmationForAttendance())){
			//System.out.println("-------->Updating");
			String sql = "Update acads.session_attendance_feedback set "
					+ "feedbackGiven='Y',"					
					+ "q1Response=?,"
					+ "q2Response=?,"
					+ "q3Response=?,"
					+ "q4Response=?,"
					+ "q5Response=?,"
					+ "q6Response=?,"
					+ "q7Response=?,"
					+ "q8Response=?,"
					+ "q1Remark=?,"
					+ "q2Remark=?,"
					+ "q3Remark=?,"
					+ "q4Remark=?,"
					+ "q5Remark=?,"
					+ "q6Remark=?,"
					+ "q7Remark=?,"
					+ "q8Remark=?,"
					+ "feedbackRemarks=?, "
					+ " studentReviewAvg = ?"
					
					+ " where sapid= ? and sessionId = ? ";

			jdbcTemplate.update(sql, new Object[] {					
					feedback.getQ1Response(),
					feedback.getQ2Response(),
					feedback.getQ3Response(),
					feedback.getQ4Response(),
					feedback.getQ5Response(),
					feedback.getQ6Response(),
					feedback.getQ7Response(),
					feedback.getQ8Response(),
					feedback.getQ1Remark(),
					feedback.getQ2Remark(),
					feedback.getQ3Remark(),
					feedback.getQ4Remark(),
					feedback.getQ5Remark(),
					feedback.getQ6Remark(),
					feedback.getQ7Remark(),
					feedback.getQ8Remark(),
					feedback.getFeedbackRemarks(),
					feedback.getStudentReviewAvg(),
					
					feedback.getSapId(),
					feedback.getSessionId()
			});
		}else{
			String sql = "Update acads.session_attendance_feedback set "
					+ " feedbackGiven='Y',"
					+ " studentConfirmationForAttendance=?,"
					+ " reasonForNotAttending=?,"
					+ " otherReasonForNotAttending=?"
					+ " where sapid= ? and sessionId = ? ";
			jdbcTemplate.update(sql, new Object[] { 
					feedback.getStudentConfirmationForAttendance(),
					feedback.getReasonForNotAttending(),
					feedback.getOtherReasonForNotAttending(),
					feedback.getSapId(),
					feedback.getSessionId()
			});
		}
	}

	 @Transactional(readOnly = true)
	//public ArrayList<StudentStudentPortalBean> getStudentsListByCriteria(	StudentStudentPortalBean student) {
	 public ArrayList<StudentStudentPortalBean> getStudentsListByCriteria(String enrollmentMonth,String enrollmentYear,String registrationMonth,String registrationYear,String registrationSem,String programStructure ,String program) {
		ArrayList<StudentStudentPortalBean> studentList = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String enrollmentMonth = student.getEnrollmentMonth();
		String enrollmentYear = student.getEnrollmentYear();
		String registrationMonth = student.getAcadMonth();
		String registrationYear = student.getAcadYear();
		String registrationSem = student.getSem();
		String programStructure = student.getPrgmStructApplicable();
		String program = student.getProgram();*/
		// send Email to only Active Student and not Program Terminated Student 
		String sql = " SELECT s.* FROM exam.students s, exam.registration r where s.sapid = r.sapid and NOT(s.programStatus <=> 'Program Terminated') ";

		if(enrollmentMonth != null && !"".equals(enrollmentMonth)){
			sql += " and s.enrollmentMonth = '" + enrollmentMonth + "'  ";
		}

		if(enrollmentYear != null && !"".equals(enrollmentYear)){
			sql += " and s.enrollmentYear = '" + enrollmentYear + "'  ";
		}

		if(program != null && !"".equals(program)){
			sql += " and s.program = '" + program + "'  ";
		}

		if(programStructure != null && !"".equals(programStructure)){
			sql += " and s.prgmStructApplicable = '" + programStructure + "'  ";
		}

		if(registrationMonth != null && !"".equals(registrationMonth)){
			sql += " and r.month = '" + registrationMonth + "'  ";
		}

		if(registrationYear != null && !"".equals(registrationYear)){
			sql += " and r.year = '" + registrationYear + "'  ";
		}

		if(registrationSem != null && !"".equals(registrationSem)){
			sql += " and r.sem = '" + registrationSem + "'  ";
		}

		sql += " group by s.sapid ";

		////System.out.println("SQL = "+sql);

		studentList = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{},new BeanPropertyRowMapper(StudentStudentPortalBean.class));

		////System.out.println(" studentList size = "+studentList.size());
		return studentList;
	}

	 @Transactional(readOnly = true)
	//Changes added on 7/5/2018 from programCleared='Y' to programCleared<>'Y'
	//public String getStudentsCriteria(	StudentStudentPortalBean student) {
	 public String getStudentsCriteria(String enrollmentMonth,String enrollmentYear,String registrationMonth,String registrationYear,String registrationSem,String programStructure,String program) {
		 
		/*String enrollmentMonth = student.getEnrollmentMonth();
		String enrollmentYear = student.getEnrollmentYear();
		String registrationMonth = student.getAcadMonth();
		String registrationYear = student.getAcadYear();
		String registrationSem = student.getSem();
		String programStructure = student.getPrgmStructApplicable();
		String program = student.getProgram();*/
		// send Email to only Active Student and not Program Terminated Student 
		String sql = " SELECT s.* FROM exam.students s, exam.registration r where s.sapid = r.sapid  and s.programCleared <>'Y' and NOT(s.programStatus <=> 'Program Terminated')";

		if(enrollmentMonth != null && !"".equals(enrollmentMonth)){
			sql += " and s.enrollmentMonth = '" + enrollmentMonth + "'  ";
		}

		if(enrollmentYear != null && !"".equals(enrollmentYear)){
			sql += " and s.enrollmentYear = '" + enrollmentYear + "'  ";
		}

		if(program != null && !"".equals(program)){
			sql += " and s.program = '" + program + "'  ";
		}

		if(programStructure != null && !"".equals(programStructure)){
			sql += " and s.prgmStructApplicable = '" + programStructure + "'  ";
		}

		if(registrationMonth != null && !"".equals(registrationMonth)){
			sql += " and r.month = '" + registrationMonth + "'  ";
		}

		if(registrationYear != null && !"".equals(registrationYear)){
			sql += " and r.year = '" + registrationYear + "'  ";
		}

		if(registrationSem != null && !"".equals(registrationSem)){
			sql += " and r.sem = '" + registrationSem + "'  ";
		}

		sql += " group by s.sapid ";

		////System.out.println("SQL = "+sql);

		return sql;
	}

	 @Transactional(readOnly = true)
	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//updated as program list to be taken from program table
		//String sql = "SELECT program FROM exam.programs order by program asc";
		String sql = "SELECT code FROM exam.program order by code asc";

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<StudentMarksBean> getAllRegistrationsFromSAPID(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.registration where sapid = ? order by sem ";
		ArrayList<StudentMarksBean> getAllRegistrationsFromSAPID = (ArrayList<StudentMarksBean>)jdbcTemplate.query(sql,new Object[]{sapid},new BeanPropertyRowMapper(StudentMarksBean.class));
		return getAllRegistrationsFromSAPID;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<String> getAllValidStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!

		String validityEndMonth = "";
		if(month <= 6){
			validityEndMonth = "Jun";
		}else{
			validityEndMonth = "Dec";
		}

		String sql = "SELECT s.sapId FROM exam.students s, exam.examorder eo "
				+ " where s.validityEndMonth = eo.month and s.programcleared <> 'Y' and s.validityEndYear = eo.year and NOT(s.programStatus <=> 'Program Terminated') "
				+ " and eo.order >= (Select examorder.order from exam.examorder where year = '" + year + "' and month = '" + validityEndMonth+ "')";

		////System.out.println("SQL = "+sql);

		ArrayList<String> sapIdList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return sapIdList;
	}
	 
	 @Transactional(readOnly = true)
	//Query All Email Communications//
	public ArrayList<MailStudentPortalBean> getAllStudentCommunications(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from portal.user_mails ";
		ArrayList<MailStudentPortalBean> allEmailCommunications = (ArrayList<MailStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(MailStudentPortalBean.class));
		return allEmailCommunications;
	}
	 
	 @Transactional(readOnly = true)
	public String getCommaSeperatedSapIdListFromMailTable(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sapIdList = "";
		String sql = " select * from portal.user_mails ";
		ArrayList<MailStudentPortalBean> getAllMailsListFromUserMailTable = (ArrayList<MailStudentPortalBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(MailStudentPortalBean.class));
		for(MailStudentPortalBean mail:getAllMailsListFromUserMailTable){
			sapIdList = sapIdList + mail.getSapid()+",";
		}
		sapIdList = sapIdList.substring(0,  sapIdList.length()-1);
		return sapIdList;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<MailStudentPortalBean> getEmailCommunicationMadeToStudent(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from portal.mails where id in (select mailTemplateId from portal.user_mails where sapid like '%" + sapid + "%') order by createdDate desc LIMIT 200";
		ArrayList<MailStudentPortalBean> mailList = (ArrayList<MailStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(MailStudentPortalBean.class));
		return mailList;

	}
	 
	 @Transactional(readOnly = false)
	public void insertSingleMailCommunication(MailStudentPortalBean mail){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO portal.user_mails (sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId) VALUES(?,?,sysdate(),?,?,?) ";
		jdbcTemplate.update(sql,new Object[]{mail.getSapid(),mail.getMailId(),mail.getSapid(),mail.getFromEmailId(),mail.getMailTemplateId()});
	}
	 
	 @Transactional(readOnly = true) 
	public MailStudentPortalBean getSingleMail(String mailTemplateId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.mails where id = ? ";
		MailStudentPortalBean mail = (MailStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{mailTemplateId}, new BeanPropertyRowMapper(MailStudentPortalBean.class));
		return mail;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<CenterStudentPortalBean> getAllCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.centers where active = '1' order by centerCode asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<CenterStudentPortalBean> centers = (ArrayList<CenterStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(CenterStudentPortalBean.class));
		return centers;
	}

	 @Transactional(readOnly = true)
	public ArrayList<String> getAllLCs() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct lc FROM exam.centers where lc <> '' or lc is not null  order by lc asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> centers = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return centers;
	}

	 @Transactional(readOnly = false)
	public void saveAuthorization(UserAuthorizationStudentPortalBean userAuthorizationBean, String loggedInUser) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO portal.user_authorization(userId, roles, authorizedLC, authorizedCenters, "
				+ "createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES "
				+ "(?,?,?,?,?, sysdate(),?, sysdate())"
				+ " on duplicate key update "
				+ "	roles = ?,"
				+ " authorizedLC= ?,"
				+ " authorizedCenters = ?,"
				+ " lastModifiedBy = ?, "
				+ " lastModifiedDate = sysdate() ";

		jdbcTemplate.update(sql, new Object[] { 
				userAuthorizationBean.getUserId(),
				userAuthorizationBean.getRoles(),
				userAuthorizationBean.getAuthorizedLC(),
				userAuthorizationBean.getAuthorizedCenters(),
				loggedInUser,
				loggedInUser,
				userAuthorizationBean.getRoles(),
				userAuthorizationBean.getAuthorizedLC(),
				userAuthorizationBean.getAuthorizedCenters(),
				loggedInUser
		});

	}

	 @Transactional(readOnly = false)
	 public void updateAuthorizationTable( String userId,String roles) {
	 	String sql = "UPDATE portal.user_authorization SET roles=? WHERE userId=?" ; 
	 	jdbcTemplate = new JdbcTemplate(dataSource);
	 	int i = jdbcTemplate.update(sql, new Object[] { 
	 			roles,userId
	 	});
 	 	
	 }
	 
	 @Transactional(readOnly = true)
	public UserAuthorizationStudentPortalBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		
			UserAuthorizationStudentPortalBean user = (UserAuthorizationStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(UserAuthorizationStudentPortalBean.class));
			return user;
		
	}
	 // added by Ritesh
	 @Transactional(readOnly = true)
		public List<UserAuthorizationStudentPortalBean> getAllUserAuthorization() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " SELECT userId,roles FROM portal.user_authorization ";
			List<UserAuthorizationStudentPortalBean> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserAuthorizationStudentPortalBean.class));
			return users;
			
		}
	 // *****
	 @Transactional(readOnly = true)
	public ArrayList<String> getAuthorizedCenterCodes(UserAuthorizationStudentPortalBean userAuthorization) {
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

		////System.out.println("Student Portal: Authorized centers = "+centers);
		return centers;
	}

	 @Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDataForAcademicSession(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ";

			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}

	 @Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDataForContent(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}

	 @Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDataForNextBatch(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;

		try {

			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select * from exam.registration r, exam.examorder eo where r.sapid = ? and  r.month = eo.acadMonth "
					+ " and r.year = eo.year and eo.order = (select max(examorder.order) + 1 from exam.examorder where acadContentLive = 'Y') ";

			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return studentRegistrationData;
	}

	 @Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getScheduledSessionForStudents(ArrayList<String> subjects,StudentStudentPortalBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		StringBuffer sql = new StringBuffer(" SELECT * FROM acads.faculty f , acads.sessions s, exam.examorder eo");
		sql.append(" where s.facultyId = f.facultyId and s.month = eo.acadmonth and s.year = eo.year ");
		sql.append(" and eo.order = (select max(examorder.order) from exam.examorder where acadSessionLive = 'Y') ");
		sql.append(" and s.subject in ("+subjectCommaSeparated+") ");

		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql.append(" and (s.corporateName = 'M.sc') ");
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql.append(" and (s.corporateName = 'Verizon' or s.corporateName = 'All') ");
				break;
			
			case "Diageo":
				sql.append( "and (s.corporateName = 'Diageo') ");
				break;
				
			case "BAJAJ":
				sql.append(" and (s.corporateName = 'BAJAJ') ");
				break;
				
			default :
				sql.append(" and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon') && s.corporateName <> 'BAJAJ' "
						 + " and s.corporateName <> 'M.sc' " //Added Temporary to avoid normal session in calendar
						 + " or s.corporateName is null or s.corporateName = '') ");
				break;
			}
		}
		sql.append(" and date >= CURDATE()");
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql.append(" and (hasModuleId is null or hasModuleId <> 'Y') ");
		}else {
			sql.append(" and hasModuleId = 'Y' ");
		}
		
		sql.append(" order by date, startTime asc ");

		//System.out.println("getScheduledSessionForStudents sql :"+sql.toString());
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]{}, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		return scheduledSessionList;
		} 
		

	 @Transactional(readOnly = true)
	//Get Data of Sessions Attended by student
	public ArrayList<SessionDayTimeStudentPortal> getAttendedSessionsForStudent(ArrayList<String> subjects,StudentStudentPortalBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM acads.sessions s, acads.session_attendance_feedback saf, acads.faculty f");
		sql.append(" where saf.attended = 'Y' and saf.sapid = ? and s.id=saf.sessionId and s.facultyId=f.facultyId ");

		switch(student.getCenterName()){
		case "Verizon":
			sql.append(" and s.corporateName = 'Verizon' ");
			break;
		
		case "Diageo":
			sql.append( "and (s.corporateName = 'Diageo') ");
			break;
		
		default :
			sql.append(" and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon') or s.corporateName is null) ");
			break;
		}

		//System.out.println("getAttendededSessionForStudents sql :"+sql.toString());
		ArrayList<SessionDayTimeStudentPortal> attendedSessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]{student.getSapid()}, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		return attendedSessionList;
	}
	//End of Get Data of Sessions Attended by student
	 
	 @Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getProgramSubjectMappingList() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.program_subject order by program, sem, subject";

		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = (ArrayList<ProgramSubjectMappingStudentPortalBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class));
		return programSubjectMappingList;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<String> getFailSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select p.subject from exam.passfail p inner join exam.students s on s.sapid = p.sapid inner join exam.program_sem_subject p_s_s on p_s_s.consumerProgramStructureId = s.consumerProgramStructureId and p_s_s.subject = p.subject and p_s_s.sem = p.sem where p.isPass = 'N' and p.sapid = ? order by p.sem  asc";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}

	 @Transactional(readOnly = true)
	public ArrayList<AssignmentStudentPortalFileBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select p.subject,p.program,p.sem,p.assignmentscore from exam.passfail p inner join exam.students s on s.sapid = p.sapid inner join exam.program_sem_subject p_s_s on p_s_s.consumerProgramStructureId = s.consumerProgramStructureId and p_s_s.subject = p.subject and p_s_s.sem = p.sem where p.isPass = 'N' and p.sapid = ? order by p.sem  asc ";

		ArrayList<AssignmentStudentPortalFileBean> subjectsList = (ArrayList<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));

		return subjectsList;
	}

	 @Transactional(readOnly = true)
	public ArrayList<AssignmentStudentPortalFileBean> getANSNotProcessed(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem, assignmentscore from exam.marks where assignmentscore = 'ANS' and (processed <> 'Y' or processed is null ) and sapid = ? order by sem  asc ";

		ArrayList<AssignmentStudentPortalFileBean> subjectsList = (ArrayList<AssignmentStudentPortalFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentStudentPortalFileBean.class));

		return subjectsList;
	}
	 
	 @Transactional(readOnly = true)
	public String getMostRecentResultPeriod(){

		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		for (ExamOrderStudentPortalBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}

	 @Transactional(readOnly = true)
	public String getRecentExamDeclarationDate() {

		String declareDate = null;
		Date d = new Date();
		final String sql = "Select declareDate from exam.examorder where examorder.order = (Select max(examorder.order) from exam.examorder where live='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		String declareD = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			declareDate = sdfr.format(declareD);
		} catch (Exception e) {
			declareDate = "";
		}


		return declareDate;
	}

	 @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAStudentsMostRecentMarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select a.*, round((part1marks + part2marks + part3marks), 2) as mcq, round(part4marks, 2)  as part4marks ,  "
				+ " roundedTotal "
				+ " from  exam.examorder b, exam.marks a  left join exam.online_marks c "
				+ " on    a.sapid = c.sapid "
				+ " and a.month = c.month "
				+ " and a.year = c.year "
				+ " and a.subject = c.subject  "
				+ " where a.sapid = ? "
				+ " and a.year = b.year "
				+ " and  a.month = b.month "
				+ " and b.order = (Select max(examorder.order) from exam.examorder where live='Y') "
				+ " order by a.sem, a.subject asc ";

		////System.out.println("SQL = "+sql);

		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(sapId);
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if("Y".equals(marksBean.getMarkedForRevaluation()) && !"Y".equals(marksBean.getRevaulationResultDeclared())){
				marksBean.setWritenscore("Subject under Revaluation");
				//marksBean.setPart4marks("Pending For Reval");
			}
			if(subjectsPendingForAssignmentReval.contains(marksBean.getSubject())){
				marksBean.setAssignmentscore("Subject under Revaluation");
			}
			studentsMarksList.add(marksBean);
		}

		return studentsMarksList;
	}
	 
	 @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<String> getSubjectsPendingForAssigmentReval(String sapid){
		String sql = "SELECT distinct subject FROM exam.assignmentsubmission where markedForRevaluation ='Y' and revaulationResultDeclared ='N' and sapid = ? ";
		List<String> subjectList = jdbcTemplate.query(sql,new Object[]{sapid},new SingleColumnRowMapper(String.class));
		return subjectList;
	}
	 
	 @Transactional(readOnly = true)
	public String getMostRecentOfflineResultPeriod() {
		String recentPeriod = null;
		final String sql = "Select year, month from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')";
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ExamOrderStudentPortalBean> rows = jdbcTemplate.query(sql,new Object[]{},new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		for (ExamOrderStudentPortalBean row : rows) {
			recentPeriod = row.getMonth()+"-"+row.getYear();
		}
		return recentPeriod;
	}

	 @Transactional(readOnly = true)
	public String getRecentOfflineExamDeclarationDate() {
		String declareDate = null,offlineResult = "";
		Date d = new Date();
		final String sql = "Select oflineResultsDeclareDate from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y')";

		jdbcTemplate = new JdbcTemplate(dataSource);

		offlineResult = (String) jdbcTemplate.queryForObject(sql,new Object[]{},String.class);

		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			declareDate = sdfr.format(offlineResult);
		} catch (Exception e) {
			declareDate = "";
		}


		return declareDate;
	}
	 
	 @Transactional(readOnly = true)
	public List<StudentMarksBean> getAStudentsMostRecentOfflineMarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from exam.marks a, exam.examorder b where a.sapid = ? and a.year = b.year and "
				+ " a.month = b.month and b.order = (Select max(examorder.order) from exam.examorder where oflineResultslive='Y') order by sem, subject asc";

		////System.out.println("SQL = "+sql);

		//List<StudentMarksBean> studentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		List<String> subjectsPendingForAssignmentReval = getSubjectsPendingForAssigmentReval(sapId);
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if("Y".equals(marksBean.getMarkedForRevaluation()) && !"Y".equals(marksBean.getRevaulationResultDeclared())){
				marksBean.setWritenscore("Subject under Revaluation");
			}
			if(subjectsPendingForAssignmentReval.contains(marksBean.getSubject())){
				marksBean.setAssignmentscore("Subject under Revaluation");
			}
			studentsMarksList.add(marksBean);
		}
		return studentsMarksList;
	}

	 
	 @Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getStudentListForValidityExpiredNotification(){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " select *,IF(validityEndMonth ='Jun','6','12') as ValidityEnd,validityEndYear,validityEndMonth,MONTH(sysdate()) "
				+ " from exam.students where validityEndYear >= YEAR(sysdate()) group by sapid having "
				+ " (ValidityEnd - MONTH(sysdate())) < 6 and (validityEndYear - YEAR(SYSDATE())) < 1 " //Where month lies in 6 months prior to Validity End and also within the same year //
				+ " and (ValidityEnd - MONTH(sysdate())) > 0" //avoid negative value //
				+ " and programCleared <> 'Y' ";
		ArrayList<StudentStudentPortalBean> studentList = null;
		try{
			studentList = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			return studentList;
		}catch(Exception e){
//			e.printStackTrace();
			return null;
		}
	}

	 @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjectsForCurrentSession(String subject, String consumerProgramStructureId, String earlyAccess) {

		jdbcTemplate = new JdbcTemplate(dataSource);		
		List<ContentStudentPortalBean> contents = null;
		StringBuffer sql = new StringBuffer();
		//Commented Old Query
		/*String sql =  " SELECT c.* FROM acads.content c, exam.examorder eo where subject = ? "
					+ " and c.month = eo.acadMonth and c.year = eo.year "
					+ " and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') ";*/
		
		String corporateSQL = "";
		String endQuery = " AND  c.activeDate  <= current_timestamp() ";
		if ("113".equals(consumerProgramStructureId) && "Business Economics".equalsIgnoreCase(subject)) {
			corporateSQL = " AND programStructure = 'M.sc' ";
		}
		
		if("Yes".equalsIgnoreCase(earlyAccess)){
			sql.append(" SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year "
				+ " where subject = ? and eo.order = (select examorder.order from exam.examorder where acadMonth = ? and year = ? "
				+ " and acadContentLive = 'Y') " + corporateSQL+endQuery);
			
			contents = jdbcTemplate.query(sql.toString(), new Object[]{subject,CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));	

		}else{
			sql.append(" SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year "
				+ " where subject = ? and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') "
				+ corporateSQL+endQuery);
			
				contents = jdbcTemplate.query(sql.toString(), new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));	

			}

		//System.out.println("SQL in mgetContentsForSubjectsForCurrentSession = " + sql);
		return contents;
	}
	
	 @Transactional(readOnly = true)
	//Added to allow students with early access to see content uploaded for their drive.
		public List<ContentStudentPortalBean> getContentsForSubjectsForCurrentSession(String subject,String earlyAccess,StudentStudentPortalBean student) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = null;
			List<ContentStudentPortalBean> contents = null;
			
			String corporateSQL = "";
			if ("113".equals(student.getConsumerProgramStructureId()) && "Business Economics".equalsIgnoreCase(subject)) {
				corporateSQL = " AND programStructure = 'M.sc' ";
			}
			
			try{
				if("Yes".equalsIgnoreCase(earlyAccess)){
					//Commented Old Query
					/*sql = "SELECT * FROM acads.content c, exam.students s where subject = ? "
							+ " and c.month = s.enrollmentMonth and c.year = s.enrollmentYear "
							+ " and s.enrollmentMonth = ? and s.enrollmentYear = ? and s.sapid = ? ";*/
					
					sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year "
						+ " where subject = ? and eo.order = (select examorder.order from exam.examorder where acadMonth = ? and "
						+ " year = ? and acadContentLive = 'Y') " + corporateSQL;
					contents = jdbcTemplate.query(sql, new Object[]{subject,CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));
				}else{
					sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year "
						+ " where subject = ? and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') "
						+ corporateSQL;
					
					contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));	

				}
				//System.out.println("SQL getContentsForSubjectsForCurrentSession = "+sql);
			}catch(Exception e){
//				e.printStackTrace();
			}
			
			return contents;
		}
		
	 @Transactional(readOnly = true)
		public List<ContentStudentPortalBean> getContentsForLeads(StudentStudentPortalBean bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = null;
			List<ContentStudentPortalBean> contents = null;
			try{
					sql = "SELECT c.* FROM acads.content_forleads c where c.subject = ? ";
					contents = jdbcTemplate.query(sql, new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,bean.getSubject());
						}}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));	
			}catch(Exception e){
//				e.printStackTrace();
			}
			
			return contents;
		}
		
	 @Transactional(readOnly = true)
		public List<ContentStudentPortalBean> getAllContentsForLeads() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = null;
			List<ContentStudentPortalBean> contents = null;
			try{
					sql = "SELECT * FROM acads.content_forleads ";
					contents = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContentStudentPortalBean .class));	
			}catch(Exception e){
//				e.printStackTrace();
			}
			
			return contents;
		}
	 
	 
 // added by Ritesh
	 
	 @Transactional(readOnly = true)
		public List<SessionQueryAnswerStudentPortal> getQueriesByStudent(String sapId) {
			String sql = " Select queryType,createdDate,query,lastModifiedDate,answer,isAnswered from acads.session_query_answer where sapid = ? ";
			jdbcTemplate = new JdbcTemplate(dataSource);
  			List<SessionQueryAnswerStudentPortal> myQueries = jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
			return myQueries;		
		}
	 
	 // *****

	 @Transactional(readOnly = true)
	public List<SessionQueryAnswerStudentPortal> getQueriesForSessionByStudent(String subject, String sapId) {
		String sql = "Select sqa.* from acads.session_query_answer sqa, acads.sessions s where "
				+ " sqa.sapid = ? and s.Id = sqa.sessionId "
				+ " and s.subject = ? "
				+ " order by sqa.createdDate desc";
		List<SessionQueryAnswerStudentPortal> myQueries = jdbcTemplate.query(sql, new Object[]{sapId,subject }, new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		return myQueries;		
	}
	 
	 @Transactional(readOnly = true)
	public List<SessionQueryAnswerStudentPortal> getQueriesForCourseByStudent(String subject, String sapId) {
		String sql = "Select * from acads.session_query_answer where "
				+ " sapid = ? "
				+ " and subject = ? "
				+ " and queryType = 'Course Query' "
				+ " order by createdDate desc";
		List<SessionQueryAnswerStudentPortal> myCourseQueries = jdbcTemplate.query(sql, new Object[]{sapId,subject }, new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		return myCourseQueries;		
	}
	 
	 @Transactional(readOnly = true)
	public PassFailBean getPassFailStatus(String sapid, String subject) {
		String sql = "Select * from exam.passfail where sapid = ? and subject = ? ";
		try {
			PassFailBean passFailBean = (PassFailBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid, subject}, new BeanPropertyRowMapper(PassFailBean.class));
			return passFailBean;
		} catch (Exception e) {
			return null;
		}

	}

	 @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getRecordingForLastCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and "
				+ " c.year = eo.year where subject = ? "
				+ " and eo.order = (select max(examorder.order)-1 from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}

	 @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getRecordingForCurrentCycle(String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and "
				+ " c.year = eo.year where subject = ? "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where acadContentLive = 'Y') "
				+ " and contentType = 'Session Recording' and c.activeDate  <= current_timestamp() ";

		////System.out.println("SQL = "+sql);
		List<ContentStudentPortalBean> contents = jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
		return contents;
	}

	 @Transactional(readOnly = true)
	public HashMap<String, StudentStudentPortalBean> getAllStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentStudentPortalBean> students = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentStudentPortalBean.class));

		HashMap<String, StudentStudentPortalBean> studentsMap = new HashMap<>();
		for (StudentStudentPortalBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}
	 
	 @Transactional(readOnly = false)
	public void insertFacultyFeedBack(FacultyCourseFeedBackBean facultyCourseBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO acads.faculty_course_feedback (sapid,subject,program,year,month,q1CourseResponse,q2CourseResponse,q3CourseResponse,q4CourseResponse,q5CourseResponse,q6CourseResponse,q7CourseResponse,q8CourseResponse,q9CourseResponse,createdDate,lastModifiedDate,createdBy,lastModifiedBy) "
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?) ";
		jdbcTemplate.update(sql,new Object[]{facultyCourseBean.getSapid(),facultyCourseBean.getSubject(),facultyCourseBean.getProgram(),facultyCourseBean.getYear(),facultyCourseBean.getMonth(),facultyCourseBean.getQ1CourseResponse(),facultyCourseBean.getQ2CourseResponse(),facultyCourseBean.getQ3CourseResponse(),
				facultyCourseBean.getQ4CourseResponse(),facultyCourseBean.getQ5CourseResponse(),facultyCourseBean.getQ6CourseResponse(),facultyCourseBean.getQ7CourseResponse(),facultyCourseBean.getQ8CourseResponse(),facultyCourseBean.getQ9CourseResponse(),facultyCourseBean.getSapid(),facultyCourseBean.getSapid()});

	}

	 @Transactional(readOnly = false)
	public void saveEventRegisteration(Event feedback) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		// checking Session Confirmation as getting Incorrect value for null for q1Response integer 

		String sql = "Insert into  portal.event_registration (response,sapId,eventName,online_EventId,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "

					+ "values (? , ? , ? ,? ,? ,sysdate() ,? ,sysdate())";

		jdbcTemplate.update(sql, new Object[] { 
				feedback.getResponse(),
				feedback.getSapId(),
				feedback.getEventName(),
				feedback.getOnline_EventId(),
				feedback.getCreatedBy(),
				
				feedback.getLastModifiedBy()
				
		});
	}

	 @Transactional(readOnly = true)
	public List<TimetableStudentPortalBean> getStudentTimetableList(StudentStudentPortalBean student,boolean isCorporate) {
		String program = student.getProgram();
		String prgmStructApplicable = student.getPrgmStructApplicable();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		if(isCorporate){
			sql = "SELECT * FROM exam.corporate_timetable t, exam.examorder eo where t.examMonth = eo.month "
					+ " and t.examYear = eo.year and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? "
					+ " and eo.order = (select max(eo.order) from exam.timetable t, exam.examorder eo where t.examMonth = eo.month and t.examYear = eo.year "
					+ " and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? ) order by program, prgmStructApplicable, sem, date, startTime asc";
		}else{
			sql = "SELECT * FROM exam.timetable t, exam.examorder eo where t.examMonth = eo.month "
					+ " and t.examYear = eo.year and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? "
					+ " and eo.order = (select max(eo.order) from exam.timetable t, exam.examorder eo where t.examMonth = eo.month and t.examYear = eo.year "
					+ " and (t.program = ? or t.program = 'NA' ) and t.PrgmStructApplicable = ? ) order by program, prgmStructApplicable, sem, date, startTime asc";
		}


		List<TimetableStudentPortalBean> timeTableList = jdbcTemplate.query(sql, new Object[]{program, prgmStructApplicable, program, prgmStructApplicable}, new BeanPropertyRowMapper(TimetableStudentPortalBean.class));
		return timeTableList;
	}



	 @Transactional(readOnly = true)
	//Attended/Pending/Scheduled sessions 23rd Nov
	public int getAllPendingSessions(String subject,StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String	sql = " select distinct count(s.id) as pending from acads.sessions s"
				+ "  where s.date >= sysdate() and s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"' "
				+ "  and s.subject = ?"
				+ "  and (s.isCancelled <> 'Y' or s.isCancelled is null)";
		
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql += " and (s.corporateName = 'M.sc') ";
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql +=  " and s.corporateName = 'Verizon' ";
				break;
				
			case "Diageo":
				sql += "and (s.corporateName = 'Diageo') ";
				break;
				
			case "BAJAJ":
				sql += " and (s.corporateName = 'BAJAJ') ";
				break;
			
			default :
				sql += " and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' ) or s.corporateName is null) ";
				break;
			}
		}
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql += " and (s.hasModuleId is null or s.hasModuleId <> 'Y') ";
		}else {
			sql += " and s.hasModuleId = 'Y' ";
		}
		
		//System.out.println("In getAllPendingSessions SQL : "+sql);
		int countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
		return countOfPendingSessions;
	}
	
	 @Transactional(readOnly = true)
	public int getAllPendingSessionsNew(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String	sql =" SELECT DISTINCT COUNT(s.id) AS pending FROM acads.sessions s " + 
					 "        INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					 " WHERE  s.date >= SYSDATE() " + 
					 "      AND s.subject = ? AND ssm.consumerProgramStructureId = ? " +
					 "		AND s.year = ? AND s.month = ? " ;
		
		int countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject, cpsId,
				studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth()}, Integer.class);
		return countOfPendingSessions;
	}

	 @Transactional(readOnly = true)
	public int getAllAttendedSessions(String subject,String userId,StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String	sql = " select distinct count(attended) as attended "
				+ "  from acads.session_attendance_feedback saf, acads.sessions s"
				+ "  where saf.sessionId = s.id and s.subject = ? and saf.sapid = ?"
				+ "  and s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"' "
				+ "  and (s.isCancelled <> 'Y' or s.isCancelled is null) and saf.attended = 'Y'";
		
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql += " and (s.corporateName = 'M.sc') ";
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql +=  " and s.corporateName = 'Verizon' ";
				break;
				
			case "Diageo":
				sql += "and (s.corporateName = 'Diageo') ";
				break;
			
			case "BAJAJ":
				sql += " and (s.corporateName = 'BAJAJ') ";
				break;
			
			default :
				sql += " and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' ) or s.corporateName is null) ";
				break;
			}
		}
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql += " and (s.hasModuleId is null or s.hasModuleId <> 'Y') ";
		}else {
			sql += " and s.hasModuleId = 'Y' ";
		}
		
		//System.out.println("In getAllAttendedSessions : "+sql);
		int countOfAttendedSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject, userId}, Integer.class);
		return countOfAttendedSessions;
	}
	
	 @Transactional(readOnly = true)
	public int getAllAttendedSessionsNew(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="SELECT DISTINCT COUNT(attended) AS attended FROM acads.session_attendance_feedback saf " + 
					"        INNER JOIN " + 
					"    acads.sessions s ON saf.sessionId = s.id " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        AND (s.isCancelled <> 'Y' OR s.isCancelled IS NULL) " + 
					"        AND s.subject = ? " + 
					"        AND ssm.consumerProgramStructureId = ? AND saf.sapid = ? AND saf.attended = 'Y'" +
					"		 AND s.year = ? AND s.month = ? ";
		
		int countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject, cpsId, studentRegistrationForAcademicSession.getSapid(),
				studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth() }, Integer.class);
		return countOfPendingSessions;
	}

	 @Transactional(readOnly = true)
	public int getAllSessionsforSubject(String subject,StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String	sql = " select distinct count(s.id) as scheduled  from  acads.sessions s "
					+ " where s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"'  and s.subject = ? "
					+ " and (s.isCancelled <> 'Y' or s.isCancelled is null)";

		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql += " and (s.corporateName = 'M.sc') ";
		
		}else {
		switch(student.getCenterName()){
			case "Verizon":
				sql +=  " and s.corporateName = 'Verizon' ";
				break;
				
			case "Diageo":
				sql += "and (s.corporateName = 'Diageo') ";
				break;
				
			case "BAJAJ":
				sql += " and (s.corporateName = 'BAJAJ') ";
				break;
				
			default :
				sql += " and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' ) or s.corporateName is null) ";
				break;
			}
		}
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql += " and (hasModuleId is null or hasModuleId <> 'Y') ";
		}else {
			sql += " and hasModuleId = 'Y' ";
		}

		//System.out.println("In getAllSessionsforSubject SQL : "+sql);
		int countOfScheduledSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
		return countOfScheduledSessions;
	}
	
	 @Transactional(readOnly = true)
	public int getAllSessionsforSubjectNew(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String	sql =" SELECT DISTINCT COUNT(s.id) AS scheduled FROM acads.sessions s " + 
					 " INNER JOIN " +
					 " acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					 " WHERE s.subject = ? AND ssm.consumerProgramStructureId = ? " +
					 " AND s.year = ? AND s.month = ?";
		
		int countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject, cpsId, 
				studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth()}, Integer.class);
		return countOfPendingSessions;
	}

	 @Transactional(readOnly = true)
	public int getAllConductedSessionsforSubject(String subject,StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String	sql = " select distinct count(s.id) as conducted  from  acads.sessions s "
				+ " where s.year = '"+CURRENT_ACAD_YEAR+"' and s.month = '"+CURRENT_ACAD_MONTH+"'  and s.subject = ? "
				+ " and (s.isCancelled <> 'Y' or s.isCancelled is null) and date <= sysdate()";

		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql += " and (s.corporateName = 'M.sc') ";
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql +=  " and s.corporateName = 'Verizon' ";
				break;
	
			case "Diageo":
				sql += "and (s.corporateName = 'Diageo') ";
				break;
				
			case "BAJAJ":
				sql += " and (s.corporateName = 'BAJAJ') ";
				break;
				
			default :
				sql += " and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' ) or s.corporateName is null) ";
				break;
			}
		}
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql += " and (s.hasModuleId is null or s.hasModuleId <> 'Y') ";
		}else {
			sql += " and s.hasModuleId = 'Y' ";
		}

		//System.out.println("In getAllConductedSessionsforSubject SQL : "+sql);
		int countOfConductedSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject}, Integer.class);
		return countOfConductedSessions;
	}

	 @Transactional(readOnly = true)
	public int getAllConductedSessionsforSubjectNew(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String	sql =" SELECT DISTINCT COUNT(s.id) AS conducted FROM acads.sessions s " + 
					 "        INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					 " WHERE  s.date <= SYSDATE() " + 
					 "        AND s.subject = ? AND ssm.consumerProgramStructureId = ? " +
					 "		AND s.year = ? AND s.month = ? " ;
		
		int countOfPendingSessions = jdbcTemplate.queryForObject(sql, new Object[]{subject, cpsId, 
				studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth()}, Integer.class);
		return countOfPendingSessions;
	}
	
	 @Transactional(readOnly = true)
	public List<SessionAttendanceFeedbackStudentPortal> getSingleStudentAttendanceforSubject(String sapid,String subject,StudentStudentPortalBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String	sql = "select  s.subject, f.firstName as facultyFirstName, f.lastName as facultyLastName, s.sessionName, s.starttime, s.date, s.id ,saf.sapid, s.track,"
				+ " saf.attended, saf.studentConfirmationForAttendance, (s.date < now()) as conducted"
				+ " from acads.sessions s"
				+ " left join acads.session_attendance_feedback saf"
				+ " on s.id = saf.sessionId and saf.sapid= ? "
				+ " left join acads.faculty f on f.facultyId=s.facultyId"
				+ " where s.subject = ? "
				+ " and s.year = ? and s.month = ? and (s.isCancelled <> 'Y' or s.isCancelled is null) "; 
		
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			sql += " and (s.corporateName = 'M.sc') ";
			
		}else {
			switch(student.getCenterName()){
			case "Verizon":
				sql += " and (s.corporateName = 'Verizon' or s.corporateName = 'All') ";
				break;
			
			case "Diageo":
				sql +=  "and (s.corporateName = 'Diageo') ";
				break;
				
			case "BAJAJ":
				sql += " and (s.corporateName = 'BAJAJ') ";
				break;
				
			default :
				sql += " and ( (s.corporateName <> 'Diageo' && s.corporateName <> 'Verizon' && s.corporateName <> 'BAJAJ' ) or s.corporateName is null or s.corporateName = '') ";
				break;
			}
		}
		
		if (!TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
			sql += " and (hasModuleId is null or hasModuleId <> 'Y') ";
		}else {
			sql += " and hasModuleId = 'Y' ";
		}
		sql += " order by s.date";
		
		//System.out.println("In getSingleStudentAttendanceforSubject SQL : "+sql);
		List<SessionAttendanceFeedbackStudentPortal> SessionAttendanceList = jdbcTemplate.query(sql, 
				new Object[]{sapid,subject, getLiveAcadSessionYear(), getLiveAcadSessionMonth()},
				new BeanPropertyRowMapper(SessionAttendanceFeedbackStudentPortal.class));
		return SessionAttendanceList;
	}
	
	 @Transactional(readOnly = true)
	public List<SessionAttendanceFeedbackStudentPortal> getSingleStudentAttendanceforSubjectNew(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT s.subject, f.firstName as facultyFirstName, f.lastName as facultyLastName, s.sessionName, " +
					" s.starttime, s.date, s.id ,saf.sapid, s.track, saf.attended, " +
					" saf.studentConfirmationForAttendance, (s.date < now()) as conducted " + 
					" 	FROM acads.sessions s  " + 
					" 		INNER JOIN " +
					" acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"       LEFT JOIN " +
					"acads.session_attendance_feedback saf ON s.id = saf.sessionId AND saf.sapid = ? " + 
					"		LEFT JOIN "+ 
					"acads.faculty f on s.facultyId = f.facultyId " + 
					"        WHERE s.year = ? AND s.month = ? "+
					"			AND (s.isCancelled <> 'Y' OR s.isCancelled IS NULL) " + 
					"        	AND s.subject = ? " + 
					"        	AND ssm.consumerProgramStructureId = ? " +
					"			ORDER BY s.date ";
		
		List<SessionAttendanceFeedbackStudentPortal> SessionAttendanceList = jdbcTemplate.query(sql, new Object[]{studentRegistrationForAcademicSession.getSapid(), 
						studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth() , subject, cpsId},
				new BeanPropertyRowMapper(SessionAttendanceFeedbackStudentPortal.class));
		return SessionAttendanceList;
	}
	
	/**
	 * Returns Upcoming TEE Exams.
	 * Optimized by Abhay on date 2021-11-29
	 */
	 @Transactional(readOnly = true)
	public List<ExamBookingTransactionStudentPortalBean> getUpcomingExams(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = ""
//				+ " SELECT * "
//				+ " FROM `exam`.`exambookings` `eb` "
//				+ " INNER JOIN `exam`.`examcenter` `ec` "
//					+ " ON `eb`.`centerId` = `ec`.`centerId` "
//					+ " AND `eb`.`year` = `ec`.`year` "
//					+ " AND `eb`.`month` = `ec`.`month` "
//				+ " WHERE `sapid` = ? "
//				+ " AND `ec`.`year` = ? "
//				+ " AND `ec`.`month` = ? "
//				+ " AND `eb`.`booked` = 'Y' "
//				+ " AND STR_TO_DATE(CONCAT(`examDate`, ' ', `examEndTime`), '%Y-%m-%d %H:%i:%s') >= now() "
//				+ " ORDER BY `eb`.`examDate`, `eb`.`examTime`, `eb`.`subject` ASC";

		String sql = " SELECT  " + 
				"    year, " + 
				"    month, " + 
				"    DATE_FORMAT(examStartDateTime, '%Y-%m-%d') AS examDate, " + 
				"    DATE_FORMAT(examStartDateTime, '%H:%i:%s') AS examTime, " + 
				"    DATE_FORMAT(examEndDateTime, '%H:%i:%s') AS examEndTime, " + 
				"    DATE_FORMAT(reporting_start_date_time, '%H:%i:%s') AS examReportingTime, " + 
				"    subject, " + 
				"    examCenterName " + 
				"FROM " + 
				"    exam.exams_pg_scheduleinfo_mettl " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND examEndDateTime >= NOW() " ;
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		List<ExamBookingTransactionStudentPortalBean> bookingList = new ArrayList<ExamBookingTransactionStudentPortalBean>();
		try {
		bookingList = jdbcTemplate.query(sql, 
				new Object[]{sapid }, new BeanPropertyRowMapper<ExamBookingTransactionStudentPortalBean>(ExamBookingTransactionStudentPortalBean.class));
		
		bookingList  = bookingList.stream()
		.sorted(Comparator.comparing(ExamBookingTransactionStudentPortalBean::getExamDate).thenComparing(ExamBookingTransactionStudentPortalBean::getExamTime))
		.collect(Collectors.toList()); 
		
		return bookingList;
		}catch (Exception e) {
			return bookingList ;
		}
	}
	
	//added 6mar by PS start
	 @Transactional(readOnly = true)
	public ArrayList<VideoContentStudentPortalBean> getVideoContentForSubject(String subject,StudentStudentPortalBean student,String earlyAccess){
		//subject=subject.replaceAll("&", "_");
		//System.out.println("In getVideoContentForSubject got subject : "+subject);
		String sql = null;
		String corporateNameForQuery = "";
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			corporateNameForQuery = "s.corporateName = 'M.Sc' ";
		}else {
			corporateNameForQuery = getCorporateNameForQuery(student.getConsumerType());
		}
		
		if("Yes".equalsIgnoreCase(earlyAccess)){
			 /*sql="select vc.* from acads.video_content vc ,exam.students s "
					+ " where "
					+ " vc.year = s.enrollmentYear "
					+ " and vc.month = s.enrollmentMonth "
					+ " and vc.subject = ?"
					+ " and s.sapid = ? ";*/
			
			//Commented by Somesh updated query for earlyAccess students
			/* sql="SELECT vc.*,se.meetingKey FROM acads.video_content vc, exam.students s , acads.sessions se "
					+ " where se.id = vc.sessionId "
					+ " and vc.year = s.enrollmentYear "
					+ " AND vc.month = s.enrollmentMonth "
					+ " AND vc.sessionId = se.id "
					+ " AND " +corporateNameForQuery
					+ " AND vc.subject = ? "
					+ " AND s.sapid = ? "; */
			
			sql="SELECT vc.*,s.meetingKey FROM acads.video_content vc inner join acads.sessions s on vc.sessionId = s.id "
					+ " where vc.year = '"+CURRENT_ACAD_YEAR+"' "					
					+ " AND vc.month = '"+CURRENT_ACAD_MONTH+"' "					
					+ " AND " +corporateNameForQuery
					+ " AND vc.subject = ? ";
			
		}else{
//			sql="select * from acads.video_content where subject = ? and year="+getLiveAcadConentYear()+" and month='"+getLiveAcadConentMonth()+"'";
/*			sql="select s.track,concat('Prof. ',f.firstName,' ', f.lastName) as facultyName, v.* from acads.video_content v "
					+ " left join "
					+ " acads.faculty f "
					+ " on f.facultyId = v.facultyId "
					+ " left join "
					+ " acads.sessions s "
					+ " on s.id = v.sessionId"
					+ " where v.subject = ? and v.year="+getLiveAcadConentYear()+" and v.month='"+getLiveAcadConentMonth()+"' " ;
*/
			sql = " SELECT  " + 
					"    s.corporateName,s.track, " + 
					"    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    v.*,s.meetingKey " +
					"FROM " + 
					"    acads.video_content v " + 
					"        LEFT JOIN " + 
					"    acads.faculty f ON f.facultyId = v.facultyId " +
					"        LEFT JOIN " + 
					"    acads.sessions s ON s.id = v.sessionId " + 
					"WHERE " + 
					"    v.subject = ?  " + 
					"		AND v.year = " +getLiveAcadConentYear()+ 
					"       AND v.month =  '"+getLiveAcadConentMonth()+"'"+ 
					"       AND " +corporateNameForQuery;
			
		}
		ArrayList<VideoContentStudentPortalBean> videoContentList=null;
		try {
			//System.out.println("In getVideoContentForSubject sql: \n"+sql); 
			
			//Commented by Somesh updated query for earlyAccess students
			/*if("Yes".equalsIgnoreCase(earlyAccess)){
				videoContentList = (ArrayList<VideoContentBean>) jdbcTemplate.query(sql, 
						new Object[]{subject,student.getSapid()}, new BeanPropertyRowMapper(VideoContentBean.class));
			}else{
				videoContentList = (ArrayList<VideoContentBean>) jdbcTemplate.query(sql, 
						new Object[]{subject}, new BeanPropertyRowMapper(VideoContentBean.class));
			} */
			
			videoContentList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql,new Object[]{subject}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			
			//System.out.println("videoContentList.size() : "+videoContentList.size());
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		
		//System.out.println("SQL : "+sql+"\n After JDBC exceution In getVideoContentForSubject got subject : "+subject);
		return videoContentList;
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<VideoContentStudentPortalBean> getVideoContentForLeads(String subject,StudentStudentPortalBean student){
		//subject=subject.replaceAll("&", "_");
		//System.out.println("In getVideoContentForSubject got subject : "+subject);
		String sql = null;
		String corporateNameForQuery = "";
		if ("113".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
			corporateNameForQuery = "s.corporateName = 'M.Sc' ";
		}else {
			corporateNameForQuery = getCorporateNameForQuery(student.getConsumerType());
		}
		
		sql = " SELECT  " + 
					"    s.corporateName,s.track, " + 
					"    CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, " + 
					"    v.*,s.meetingKey " +
					"FROM " + 
					"    acads.video_content_forleads v " + 
					"        LEFT JOIN " + 
					"    acads.faculty f ON f.facultyId = v.facultyId " +
					"        LEFT JOIN " +
					"    acads.sessions s ON s.id = v.sessionId " + 
					"WHERE " + 
					"    v.subject = ?  " + 
					"		AND v.year = " +getLiveAcadConentYear()+ 
					"       AND v.month =  '"+getLiveAcadConentMonth()+"'"+ 
					"       AND " +corporateNameForQuery;
			
		ArrayList<VideoContentStudentPortalBean> videoContentList=null;
		try {
			//System.out.println("In getVideoContentForSubject sql: \n"+sql); 
			
			
				videoContentList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql, 
						new Object[]{subject}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));

			
			//System.out.println("videoContentList.size() : "+videoContentList.size());
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		
		//System.out.println("SQL : "+sql+"\n After JDBC exceution In getVideoContentForSubject got subject : "+subject);
		return videoContentList;
	}
	 
	 
	 @Transactional(readOnly = true)
	private String getCorporateNameForQuery(String consumerType) {
		if(consumerType == null) {
			return " (s.corporateName is null)";
		}else {
			if("Retail".equalsIgnoreCase(consumerType)) {
				return " (s.corporateName = '' or s.corporateName = 'All' or s.corporateName is null )";
			}else if("Verizon".equalsIgnoreCase(consumerType)) {
				return " (s.corporateName = 'Verizon' or s.corporateName = 'All' or s.corporateName is null )";
			}else if("CIPLA".equalsIgnoreCase(consumerType) ){
				return " ( s.corporateName = 'All' or s.corporateName is null )";
			}else {
				return " (s.corporateName = '"+consumerType+"' )";
			}
		}
	}

	//end
	
	 @Transactional(readOnly = true)
	public ArrayList<String> getAllActivePrograms(){
		String sql="SELECT distinct program FROM exam.program_subject where active = 'Y'";
		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, 
				new Object[]{},new SingleColumnRowMapper(String.class));
		return programList;
	}
	 
	 @Transactional(readOnly = true)
	public ArrayList<String> getAllActiveProgramStructure(){
		//updated as program list to be taken from program table and programStructure to be taken from program_structure table
		//String sql="SELECT distinct programStructure FROM exam.programs  order by programStructure asc";
		String sql="SELECT distinct program_structure FROM exam.program_structure  order by program_structure asc";
		
		ArrayList<String> programStructureList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programStructureList;
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<AcadCycleFeedback> getPendingAcadCycleFeedbacks(String userId,String sem,String program) {
		ArrayList<AcadCycleFeedback> pendingFeedback = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.acadSurvey_feedback where sapid = ? and sem = ? and program = ?";
		try {
			pendingFeedback = (ArrayList<AcadCycleFeedback>)jdbcTemplate.query(sql, new Object[]{userId,sem,program},new BeanPropertyRowMapper(AcadCycleFeedback.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pendingFeedback;
	}
	
	
	 @Transactional(readOnly = false)
	public int upsertSuveryConfiguration(AcadCycleFeedback survey) {
		ArrayList<AcadCycleFeedback> pendingFeedback = new ArrayList<>();
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = "insert into portal.survey_configuration  "
				+ "(year,month,type,live,createdBy,lastModifiedBy, createdDate,lastModifiedDate)"
				+ " values (?,?,?,?,?,?,sysdate(),sysdate()) "
				+ " on duplicate key update "
				+ " live = ?,"
				+ " lastModifiedBy = ?,"
				+ " lastModifiedDate = sysdate()";
		try {
		jdbcTemplate.update(sql, new Object[]{
					survey.getYear(),survey.getMonth(),survey.getType(),survey.getLive(),survey.getCreatedBy(),survey.getLastModifiedBy(),
					survey.getLive(),survey.getLastModifiedBy()});
		} catch (Exception e) {
//			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
	 @Transactional(readOnly = false)
	public void saveAcadCycleFeedback(AcadCycleFeedback feedback) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
			String sql = "Insert into  portal.acadSurvey_feedback "
					+ " (feedbackGiven,"
					+ " q1Response,"
					+ " q2Response,"
					+ " q3aResponse,"
					+ " q3bResponse,"
					+ " q3cResponse,"
					+ " q3dResponse,"
					+ " q3eResponse,"
					+ " q3fResponse,"
					+ " q4Response,"
					+ " q5Response,"
					+ " q6Response,"
					+ " q7Response,"
					+ " q8Response,"
					+ " q1Remark,"
					+ " q2Remark,"
					+ " q3aRemark,"
					+ " q3bRemark,"
					+ " q3cRemark,"
					+ " q3dRemark,"
					+ " q3eRemark,"
					+ " q3fRemark,"
					+ " q4Remark,"
					+ " q5Remark,"
					+ " q6Remark,"
					+ " q7Remark,"
					+ " q8Remark,"
					+ " sapid,"
					+ " sem,"
					+ " program,"
					+ " year,"
					+ " month,"
					+ " createdBy,"
					+ " lastModifiedBy,"
					+ " createdDate,"
					+ " lastModifiedDate) "
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())";
			jdbcTemplate.update(sql, new Object[] { 
					feedback.getFeedbackGiven(),
					feedback.getQ1Response(),
					feedback.getQ2Response(),
					feedback.getQ3aResponse(),
					feedback.getQ3bResponse(),
					feedback.getQ3cResponse(),
					feedback.getQ3dResponse(),
					feedback.getQ3eResponse(),
					feedback.getQ3fResponse(),
					feedback.getQ4Response(),
					feedback.getQ5Response(),
					feedback.getQ6Response(),
					feedback.getQ7Response(),
					feedback.getQ8Response(),
					feedback.getQ1Remark(),
					feedback.getQ2Remark(),
					feedback.getQ3aRemark(),
					feedback.getQ3bRemark(),
					feedback.getQ3cRemark(),
					feedback.getQ3dRemark(),
					feedback.getQ3eRemark(),
					feedback.getQ3fRemark(),
					feedback.getQ4Remark(),
					feedback.getQ5Remark(),
					feedback.getQ6Remark(),
					feedback.getQ7Remark(),
					feedback.getQ8Remark(),
					feedback.getSapid(),
					feedback.getSem(),
					feedback.getProgram(),
					feedback.getYear(),
					feedback.getMonth(),
					feedback.getCreatedBy(),
					feedback.getLastModifiedBy()
					
			});
		}
	
	
	 @Transactional(readOnly = true)
	public AcadCycleFeedback getSingleStudentsRegistrationData(String sapid,AcadCycleFeedback surveyList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		AcadCycleFeedback student = null;
		try{
			String sql = "SELECT * FROM exam.registration r where "
					+ "    r.sapid = ?  and year = ? and month = ?  ";

			////System.out.println("SQL = "+sql);

			student = (AcadCycleFeedback)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid,surveyList.getYear(),surveyList.getMonth()
			}, new BeanPropertyRowMapper(AcadCycleFeedback.class));
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsRegistrationData : Student Details Not Found  :"+e.getMessage());
			return null;
//			e.printStackTrace();
		}

	}
	 
	 @Transactional(readOnly = true)
	public PageStudentPortal<AcadCycleFeedback> getAcadFeedbackReport(String month,String year,int pageNo, int pageSize) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<AcadCycleFeedback> feedbackList = new ArrayList<AcadCycleFeedback>();
		try{
			String sql = "SELECT * FROM portal.acadSurvey_feedback where month = ? and year = ? ";
			String countSql = "SELECT count(*) FROM portal.acadSurvey_feedback where month = ? and year = ? ";

			////System.out.println("SQL = "+sql);
			PaginationHelper<AcadCycleFeedback> pagingHelper = new PaginationHelper<AcadCycleFeedback>();
			PageStudentPortal<AcadCycleFeedback> page = pagingHelper.fetchPage(jdbcTemplate,
					countSql, sql, new Object[]{month,year}, pageNo, pageSize,
					new BeanPropertyRowMapper(AcadCycleFeedback.class));
			
			return page;
			
		}catch(Exception e){
			//System.out.println("getAcadFeedbackReport : Feedback Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}

	}
	
	 @Transactional(readOnly = true)
	public AcadCycleFeedback  getLiveSurveyDetails(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		AcadCycleFeedback surveyList = new AcadCycleFeedback();
		try{
			String sql = "SELECT * FROM portal.survey_configuration where live = 'Y' and type = 'Academic Survey'";

			////System.out.println("SQL = "+sql);

			surveyList = (AcadCycleFeedback)jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(AcadCycleFeedback.class));
			return surveyList;
		}catch(Exception e){
			//System.out.println("getAcadFeedbackReport : Survey Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<AcadCycleFeedback>  getAllLiveSurveyDetails(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<AcadCycleFeedback> surveyList = new ArrayList<AcadCycleFeedback>();
		try{
			String sql = "SELECT * FROM portal.survey_configuration ";

			////System.out.println("SQL = "+sql);

			surveyList = (ArrayList<AcadCycleFeedback>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(AcadCycleFeedback.class));
			return surveyList;
		}catch(Exception e){
			//System.out.println("getAcadFeedbackReport : Survey Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}
	}
	
	 @Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<StudentMarksBean> getAExecutiveStudentsMostRecentMarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select a.*, round((part1marks + part2marks + part3marks), 2) as mcq, round(part4marks, 2)  as part4marks ,  "
				+ " roundedTotal "
				+ " from  exam.executive_examorder b, exam.marks a  left join exam.online_marks c "
				+ " on    a.sapid = c.sapid "
				+ " and a.month = c.month "
				+ " and a.year = c.year "
				+ " and a.subject = c.subject  "
				+ " where a.sapid = ? "
				+ " and a.year = b.year "
				+ " and  a.month = b.month "
				+ " and b.order = (Select max(executive_examorder.order) from exam.executive_examorder where resultLive='Y') "
				+ " order by a.sem, a.subject asc ";

		////System.out.println("SQL = "+sql);

		List<StudentMarksBean> allStudentsMarksList = jdbcTemplate.query(sql,new Object[]{sapId}, new BeanPropertyRowMapper(StudentMarksBean.class));
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		for(StudentMarksBean marksBean : allStudentsMarksList){
			if("Y".equals(marksBean.getMarkedForRevaluation()) && !"Y".equals(marksBean.getRevaulationResultDeclared())){
				marksBean.setWritenscore("Subject under Revaluation");
				//marksBean.setPart4marks("Pending For Reval");
			}
			
			studentsMarksList.add(marksBean);
		}

		return studentsMarksList;
	}
	
	 @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjectsForCurrentSessionNew(String subject,String earlyAccess,StudentStudentPortalBean student,String studentRegistrationMonth, String studentRegistrationYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		List<ContentStudentPortalBean> contents = null;
		try{
		
				sql = "SELECT c.*, eo.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and c.year = eo.year "
						+ " where subject = ? and c.month = eo.acadMonth and c.year = eo.year "
						+ " and eo.order = (select examorder.order from exam.examorder where acadMonth = ? and year = ? and acadContentLive = 'Y') ";
				contents = jdbcTemplate.query(sql, new Object[]{subject,studentRegistrationMonth,studentRegistrationYear}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));	

			
			//System.out.println("SQL getContentsForSubjectsForCurrentSessionNew = "+sql);
		}catch(Exception e){
//			e.printStackTrace();
		}
		
		return contents;
	}
	
	 @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjectsForLastCycles(String subject, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		List<ContentStudentPortalBean> contents = null;
		
		try{
			String sql =  " SELECT c.* FROM acads.content c inner join exam.examorder eo on c.month = eo.acadMonth and "
					+ " c.year = eo.year where subject = ? "
					+ " and eo.order = (select max(examorder.order-1) from exam.examorder where acadContentLive = 'Y') ";
			
			if ("113".equals(consumerProgramStructureId) && "Business Economics".equalsIgnoreCase(subject)) {
				sql = sql + " AND programStructure = 'M.sc' ";
			}
			
			contents= jdbcTemplate.query(sql, new Object[]{subject}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));
			
			//System.out.println("SQL getContentsForSubjectsForLastCycles = "+sql);
		}catch(Exception e){
//			e.printStackTrace();
		}
	
		return contents;
	}
	
	 @Transactional(readOnly = false)
	public void updateErrorFlag(String sapid,String errorMessage,MailSender mailer){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.students set errorFlag = 'Y', errorMessage = ? , lastModifiedBy= ? , lastModifiedDate = sysdate() where sapid = ? ";
		try{
			jdbcTemplate.update(sql,new Object[]{errorMessage,sapid,sapid});
		}catch(Exception e){
			//e.printStackTrace();
			mailer.mailStackTrace("Unable to update Error Flag in studentzone", e);
		}
	}
	
	@Transactional(readOnly = true) 
	@Deprecated
	public String getConsumerProId(String program,String programStructure,String consumerType) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select c_p_s.id from exam.consumer_program_structure as c_p_s "
				+ "left join exam.program as p on p.id = c_p_s.programId "
				+ "left join exam.program_structure as p_s on p_s.id = c_p_s.programStructureId "
				+ "left join exam.consumer_type as c_t on c_t.id = c_p_s.consumerTypeId where"
				+ " p.code = ? and "
				+ "p_s.program_structure = ? and "
				+ "c_t.name = ?";
		String consumerProgramStructureId = (String)jdbcTemplate.queryForObject(sql,new Object[] {program,programStructure,consumerType},String.class);
	    //System.out.println("list----"+consumerProgramStructureId);
		return consumerProgramStructureId;
	}
	 
	 @Transactional(readOnly = false)
	public boolean updatePasswordFlag(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update exam.students set changedPassword = 'Y',lastModifiedDate =sysdate(), lastModifiedPasswordDate = sysdate() where sapid = ? ";
		try{
			jdbcTemplate.update(sql,new Object[]{sapid});
			return true;
		}catch(Exception e){
//			e.printStackTrace();
		}
		return false;
	}
	
	/*public List<StudentBean> getStudentsAllRegistrationData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentBean> student = null;
		try{
			String sql = "SELECT * FROM exam.registration r where "
					+ "    r.sapid = ?   ";

			////System.out.println("SQL = "+sql);

			student = (List<StudentBean>)jdbcTemplate.query(sql, new Object[]{
					sapid
			}, new BeanPropertyRowMapper(StudentBean.class));
		
			return student;
		}catch(Exception e){
			//System.out.println("getSingleStudentsData : Student Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}

	}
	
	
	public Map<String,String> getProgramSubjectSemMap(String program,String prgmStructApp) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingBean> prgmDetails = null;
		Map<String,String> prgmSubSemMap = new HashMap<String, String>();
		try{
			String sql = "SELECT * FROM exam.program_subject ps where"
					+ " ps.program = ?  and ps.active = 'Y' and ps.prgmStructApplicable = ? ";

		    prgmDetails = (ArrayList<ProgramSubjectMappingBean>)jdbcTemplate.query(sql, new Object[]{program,prgmStructApp}, new BeanPropertyRowMapper(ProgramSubjectMappingBean.class));
			//System.out.println("SQL = "+sql);
			for(ProgramSubjectMappingBean bean : prgmDetails){
				String key = bean.getProgram()+"-"+bean.getSubject();
				
				if(!prgmSubSemMap.containsKey(key)){
				
					prgmSubSemMap.put(key, bean.getSem());
				}
				
			}
			
			return prgmSubSemMap;
		}catch(Exception e){
			//System.out.println("getProgramSubjectSemMap : Program Details Not Found  :"+e.getMessage());
			return null;
			//e.printStackTrace();
		}

	}*/
	
	//System generated announcements (insert data into posts table)
	//start
	
	 @Transactional(readOnly = false)
	public void insertAnnouncementPostTable(AnnouncementStudentPortalBean bean,int id,String type) {
		//System.out.println("++++++++  in EMBA post data entry   ++++++++++++++++");

		Posts posts = new Posts();
		posts.setUserId("System");
		posts.setRole("System");
		posts.setType("Announcement");
		
		posts.setFileName(bean.getSubject());
		posts.setContent(bean.getDescription());
		posts.setStartDate(bean.getStartDate());
		posts.setEndDate(bean.getEndDate());
		posts.setActive(bean.getActive());
		posts.setCategory(bean.getCategory());
		posts.setAttachment1(bean.getAttachmentFile1Path());
		posts.setAttachment2(bean.getAttachmentFile2Path());
		posts.setAttachment3(bean.getAttachmentFile3Path());
		posts.setReferenceId(id+"");
		
		posts.setYear(CURRENT_ACAD_YEAR);
		posts.setMonth(CURRENT_ACAD_MONTH);
		
		List<Integer> timeBoundIds = getTimeBoundIdEMBA(bean.getProgram(),bean.getProgramStructure());
			if (timeBoundIds.size() != 0) {
				if(type=="insert") {
					posts.setCreatedBy(bean.getCreatedBy());
					posts.setLastModifiedBy(bean.getCreatedBy());		
					posts.setVisibility(1);
					
					for(Integer timeBoundId : timeBoundIds) {
						//System.out.println("timeBoundIds----- " + timeBoundId);
						int post_id = insertIntoPostsTableEMBA(posts, timeBoundId);	
						posts.setPost_id(post_id+"");
						posts.setSubject_config_id(timeBoundId+"");
						//insert into redis
						//insertToRedis(posts);
						refreshRedis(posts);
					}
				}else {
					posts.setLastModifiedBy(bean.getLastModifiedBy());	
					updateSessionPostTable(posts);

					refreshRedis(posts);
				}
			} else {
				//System.out.println("No need to add in Post table.");
			}
		}
/*	public String insertToRedis(Posts posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
	  	    String url = SERVER_PATH+"timeline/api/post/savePostInRedis";
	    	  //System.out.println("IN savePostInRedisToCache() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	}*/
	 
	 @Transactional(readOnly = false)
	public String deletePostByTimeboundIdAndPostId(Posts posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
	  	    String url = SERVER_PATH+"timeline/api/post/deletePostByTimeboundIdAndPostId";
	    	  //System.out.println("IN deletePostByTimeboundIdAndPostIdInCache() got url : \n"+url);
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Posts> entity = new HttpEntity<Posts>(posts,headers);
			  
			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
//			e.printStackTrace();
			return "Error IN rest call got "+e.getMessage();
		}
	}
	 
	 @Transactional(readOnly = true)
	public List<Integer> getTimeBoundIdEMBA(String program,String programstructure) {
		
		//System.out.println("in getProgramSemSubjectIdEMBA Portal Dao program::"+program+"--"+"programstructure::"+programstructure);
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " select id from lti.student_subject_config where prgm_sem_subj_id in ( "
					+ " select pss.id  from exam.program_sem_subject as pss, " 
					+ " exam.program as p,exam.program_structure as ps, "
					+ " exam.consumer_program_structure as cps " 
					+ " where " 
					+ " cps.programId = p.id and cps.programStructureId=ps.id "
					+ " and pss.consumerProgramStructureId=cps.id";
		
		if( !"All".equals(program)   &&  !"All".equals(programstructure)  ) {
			sql = sql + " and p.code = '"+program+"' and ps.program_structure = '"+programstructure+"' ";
		}
		else if(!"All".equals(program)) {
			sql = sql + " and p.code = '"+program+"' ";
		}
		else if(!"All".equals(programstructure)) {
			sql = sql + " and  ps.program_structure = '"+programstructure+"' ";
		}
			sql = sql + " ) ";
		
		//System.out.println("Sql getProgramSemSubjectIdEMBA : "+sql);
		
		List<Integer> id = (List<Integer>) jdbcTemplate.query(sql,new SingleColumnRowMapper(Integer.class));
		
		//System.out.println("list----" + id);
		return id;
	}
				

	 @Transactional(readOnly = false)
		public void insertIntoPostProgSemSubEMBA(Integer programSemSubjectId, Integer postsId) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "INSERT INTO lti.post_prog_sem_sub (program_sem_subject_id, post_id) " + "VALUES(?,?)";
			jdbcTemplate.update(sql, new Object[] { programSemSubjectId, postsId });

		}
	 @Transactional(readOnly = false)
		public int insertIntoPostsTableEMBA(final Posts bean, final Integer timeBoundId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			KeyHolder holder = new GeneratedKeyHolder();
	  
			final String sql =" INSERT INTO lti.post ( userId, role, type, content, fileName, "
							+ " subject_config_id, startDate, endDate, active, category, "
							+ " attachment1, attachment2, attachment3, referenceId, acadYear, acadMonth,scheduleFlag, createdBy, "
							+ " createdDate, lastModifiedBy, lastModifiedDate, visibility) "  
							+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'N',?,sysdate(),?,sysdate(),?)";  
   
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

					ps.setString(1, bean.getUserId());
					ps.setString(2, bean.getRole());
					ps.setString(3, bean.getType());
					ps.setString(4, bean.getContent());
					ps.setString(5, bean.getFileName());
					ps.setInt(6, timeBoundId);
					ps.setString(7, bean.getStartDate());
					ps.setString(8, bean.getEndDate());
					ps.setString(9, bean.getActive());
					ps.setString(10, bean.getCategory());
					ps.setString(11, bean.getAttachment1());
					ps.setString(12, bean.getAttachment2());
					ps.setString(13, bean.getAttachment3());
					ps.setString(14, bean.getReferenceId());
					ps.setString(15, bean.getYear());
					ps.setString(16, bean.getMonth());
					ps.setString(17, bean.getCreatedBy());
					ps.setString(18, bean.getLastModifiedBy());
					ps.setInt(19, bean.getVisibility());
				
					return ps;
				}
			}, holder);

			int postId = holder.getKey().intValue();

			return postId;
		}
	 @Transactional(readOnly = false)	
		public void updateSessionPostTable(final Posts bean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			  
			 String sql = "UPDATE lti.post SET "
					+ "userId = ?, "
					+ "role = ?, "
					+ "type = ?, "
					+ "content = ?, "
					+ "fileName = ?,"
					+ "startDate = ?, "
					+ "endDate = ?, "
					+ "active = ?, "
					+ "category = ?, "
					+ "attachment1 = ?, "
					+ "attachment2 = ?, "
					+ "attachment3 = ?,  "
					+ "referenceId = ?, "
					+ "year = ?, "
					+ "month = ?,"
					+ "lastModifiedBy = ?, "
					+ "lastModifiedDate = sysdate() "
					+ "WHERE referenceId = ? ";

			jdbcTemplate.update(sql, new Object[] {
			bean.getUserId(),
			bean.getRole(),
			bean.getType(),
			bean.getContent(),
			bean.getFileName(),
			bean.getStartDate(),
			bean.getEndDate(),
			bean.getActive(),
			bean.getCategory(),
			bean.getAttachment1(),
			bean.getAttachment2(),
			bean.getAttachment3(),
			bean.getReferenceId(),
			bean.getYear(),
			bean.getMonth(),
			bean.getLastModifiedBy(),
			bean.getReferenceId()
			
			});
		}
	
	//System generated announcements (insert data into posts table)
	//end
	
	 @Transactional(readOnly = false)
		public void updateStudentDetails(StudentStudentPortalBean student,MailSender mailer) {
			String sql = "Update exam.students set "
			//personal Information fields
					+ " firstName = ?,"
					+ " middleName = ?,"
					+ " lastName = ?,"
//					+ " fatherName = ?,"		//Commented as student will update FatherName from Change Contact Details SR (cardNo: 2004)
//					+ " motherName = ?,"		//Commented as student will update MotherName from Change Contact Details SR (cardNo: 2004)
//					+ " husbandName = ?,"		//Commented as student will update SpouseName from Change Contact Details SR (cardNo: 2004)
					+ " age = ?,"
					+ " gender = ?,"
					+ " dob = ?,"
			//contact Information fields		
//					+ " emailId = ?,"			//Commented as student will update Email from Change Contact Details SR (cardNo: 2009)
//					+ " mobile = ?,"			//Commented as student will update Mobile from Change Contact Details SR (cardNo: 2009)
					+ " altPhone = ?,"
					+ " houseNoName = ?,"
					+ " street = ?,"
					+ " locality = ?,"
					//+ " landMark = ?,"
					+ " pin = ?,"
					+ " city = ?,"
					+ " state = ?,"
					+ " country = ?,"
					+ " address = ?,"
			//education/work Information  fields
					+ " highestQualification = ?,"
					+ " industry = ?,"
					+ " designation = ?,"
			//audit trails
					+ " lastModifiedBy = ?,"
					+ " lastModifiedDate = sysdate(),"
					+ " detailsConfirmedByStudent = 'Y',"
					+ " louConfirmed=true,"
					+ " louConfirmedTimestamp=?,"
					+ " abcId=?"
					+ " where sapid= ? ";

					jdbcTemplate = new JdbcTemplate(dataSource);
					try{
					jdbcTemplate.update(sql, new Object[] { 
							student.getFirstName(),
							student.getMiddleName(),
							student.getLastName(),
//							student.getFatherName(),
//							student.getMotherName(),
//							student.getHusbandName(),
							student.getAge(),
							student.getGender(),
							student.getDob(),
//							student.getEmailId(),
//							student.getMobile(),
							student.getAltPhone(),
							student.getHouseNoName(),
							student.getStreet(),
							student.getLocality(),
							//student.getLandMark(),
							student.getPin(),
							student.getCity(),
							student.getState(),
							student.getCountry(),
							student.getAddress(),
							student.getHighestQualification(),
							student.getIndustry(),
							student.getDesignation(),
							student.getSapid(),
							student.getLouConfirmedTimestamp(),
							student.getAbcId(),
							student.getSapid()
					});
					
					//System.out.println("In updateStudentContact updated success.");
					}catch(Exception e){
					//e.printStackTrace();
					
					try{
					jdbcTemplate.update(sql, new Object[] { 
							student.getFirstName(),
							student.getMiddleName(),
							student.getLastName(),
//							student.getFatherName(),
//							student.getMotherName(),
//							student.getHusbandName(),
							student.getAge(),
							student.getGender(),
							student.getDob(),
//							student.getEmailId(),
//							student.getMobile(),
							student.getAltPhone(),
							student.getHouseNoName(),
							student.getStreet(),
							student.getLocality(),
							//student.getLandMark(),
							student.getPin(),
							student.getCity(),
							student.getState(),
							student.getCountry(),
							student.getAddress(),
							student.getHighestQualification(),
							student.getIndustry(),
							student.getDesignation(),
							student.getSapid(),
							student.getLouConfirmedTimestamp(),
							student.getAbcId(),
							student.getSapid()
					});
					}catch(Exception e2){
					mailer.mailStackTrace("Confirm Details : Unable to update student profile on portal", e2);
					}
					}
		}

	 @Transactional(readOnly = true)
		public ArrayList<String> getStudentListForProfileUpdate(){
			jdbcTemplate = new JdbcTemplate(dataSource);
	
			String sql = " select distinct sapid from exam.students s"
					+ " where STR_TO_DATE(concat(s.validityEndYear,'-',s.validityEndMonth,'-01'), '%Y-%b-%d') < sysdate()"
					+ " and s.detailsConfirmedByStudent <> 'Y' ";
			ArrayList<String> studentList = new ArrayList<>();
			try{
				studentList = (ArrayList<String>)jdbcTemplate.query(sql,new SingleColumnRowMapper(String.class));
				return studentList;
			}catch(Exception e){
//				e.printStackTrace();
				return studentList;
			}
		}
		
	/*Acode
	 * 
	 * sessionsAttendedBySingleStudent
	 *
	 * @returns the count of the attended sessions
	 *
	 * @accepts sapid for querying the database
	 *     
	    
	    public int getSessionsAttendedBySingleStudent(String sapid) {
	        int attendedSessionsCount = 0;
	        try {
	            
	            String sql = "select count(saf.sessionId) as NoOfAttendedSessions  from  exam.students s , exam.registration r , exam.program_subject ps,  acads.sessions se, acads.session_attendance_feedback saf  where  s.sapid = ?  and s.program = r.program  and r.year= 2018 and r.month='Jul'  and ps.program = r.program  and ps.prgmStructApplicable = s.PrgmStructApplicable  and ps.sem = r.sem  and se.year= 2018 and se.month='Jul'  and se.subject = ps.subject  and se.id = saf.sessionId  and saf.sapid = s.sapid  group by s.sapid; ";
	            //System.out.println("-------> sql");
	            //System.out.println(sql);
	            attendedSessionsCount = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);

	            //System.out.println("--------> sessionsAttendedBySingleStudent");
	            //System.out.println(attendedSessionsCount);
	            
	            return attendedSessionsCount;
	    
	        } catch (Exception e) {
	            
	            //System.out.println(e.getMessage());
	            return -1;
	        }
	    }*/
	 @Transactional(readOnly = true)
	    public Map<String,String> getSemWiseNumberOfSubjectsToClear(StudentStudentPortalBean student){
	    	ArrayList<ProgramSubjectMappingStudentPortalBean> semWiseNumberOfSubjectsToClearList = new ArrayList<ProgramSubjectMappingStudentPortalBean>();
	    	Map<String,String> semWiseNumberOfSubjectsToClearMap = new HashMap<String, String>();
	    	try{
	    		
	    		String sql = "select sem, count(subject) as subjectsCount"
	    				+ " from exam.program_subject"
	    				+ " where"
	    				+ " program = ? and"
	    				+ " active = 'Y' and"
	    				+ " prgmStructApplicable=? "
	    				+ " group by sem";
	    		semWiseNumberOfSubjectsToClearList = (ArrayList<ProgramSubjectMappingStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{student.getProgram(),student.getPrgmStructApplicable()},new BeanPropertyRowMapper(ProgramSubjectMappingStudentPortalBean.class) );
	    		if(semWiseNumberOfSubjectsToClearList.size()>0){
	    			for(ProgramSubjectMappingStudentPortalBean bean : semWiseNumberOfSubjectsToClearList ){
	    				if(!semWiseNumberOfSubjectsToClearMap.containsKey(bean.getSem())){
	    					semWiseNumberOfSubjectsToClearMap.put(bean.getSem(), bean.getSubjectsCount());
	    				}
	    			}
	    		}
	    	
	    	}catch(Exception e){
	    		//System.out.println("Error : getSemWiseNumberOfSubjectsToClear ");
//	    		e.printStackTrace();
	    	}
	    	return semWiseNumberOfSubjectsToClearMap;
	    }
	    
	 @Transactional(readOnly = true)
	    public int getSessionsApplicableForSingleStudent(String year,String month, StudentMarksBean student) {
	        int sessionsCount = 0;
	        try {
	            
	            String sql = "select count(s.id) as NoOfAttendedSessions  "
	            		+ " from acads.sessions s "
	            		+ " where "
	            		+ " year = ? and "
	            		+ " month =? and "
	            		+ " isCancelled = 'N' and "
	            		+ " subject in (select subject from exam.program_subject where program = ? and sem = ? and active='Y') ";
	            
	            sessionsCount = (int) jdbcTemplate.queryForObject(sql, new Object[]{year,month,student.getProgram(),student.getSem()},Integer.class);

	            return sessionsCount;
	    
	        } catch (Exception e) {
	            
	            //System.out.println(e.getMessage());
	            return 0;
	        }
	    }

	 @Transactional(readOnly = false)
	public void updateConfirmDetailsFlag(ArrayList<String> studentList,MailSender mailSender){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = " update exam.students s"
				+ " set detailsConfirmedByStudent = 'N' "
				+ " where  detailsConfirmedByStudent = 'Y' and sapid in ("+StringUtils.join(studentList, ',')+")";
		int i = 0;
		try{
			//System.out.println("sql ::: "+sql);
			i = jdbcTemplate.update(sql);
		
		}catch(Exception e){
			try{
			i = jdbcTemplate.update(sql);
			}catch(Exception e2){
				mailSender.mailStackTrace("Error : UpdateConfirmDetailsFlag", e2);	
			}
			
		}
	}
	
	 @Transactional(readOnly = true)
	public FacultyStudentPortalBean getFacultyData (String userId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? ";
		try {
			FacultyStudentPortalBean faculty = (FacultyStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{userId}, new BeanPropertyRowMapper(FacultyStudentPortalBean.class));
			return faculty;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}	
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<String> getSubjectList (String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
//		String sql = "SELECT pss.subject FROM acads.faculty as f "
//				+ " left join lti.faculty_course as fc on fc.facultyId = f.facultyId "
//				+ " left join exam.program_sem_subject as pss on fc.program_sem_subject_id = pss.id"
//				+ " where f.facultyId = 'NGASCE0287' group by pss.subject; ";
		String sql =	
        "select pss.subject from "+
       " lti.timebound_faculty_mapping tf, "+
       " lti.student_subject_config sc , "+
        "exam.program_sem_subject pss "+
        "where tf.facultyId='"+userId+
        "' and tf.student_subject_config_id=sc.id "+
        "and pss.id=sc.prgm_sem_subj_id ";    
		try { 
			ArrayList<String> subjectList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return subjectList;

		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	 @Transactional(readOnly = true)
	public List<AnnouncementMasterBean> getConsumerProgramStructureData(){
		
		List<AnnouncementMasterBean> consumerProgramStructureList = new ArrayList<AnnouncementMasterBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="SELECT " + 
				"    cps.id," + 
				"    p.code AS program," + 
				"    ps.program_structure AS program_structure," + 
				"    ct.name AS consumer_type " + 
				"FROM " + 
				"    exam.consumer_program_structure cps " + 
				"        INNER JOIN " + 
				"    exam.program p ON cps.programId = p.id " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"ORDER BY ct.name , ps.program_structure , p.code  ";			
		
		//System.out.println("Sql"+sql);
		try {
			consumerProgramStructureList = (List<AnnouncementMasterBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(AnnouncementMasterBean.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		return consumerProgramStructureList;
	}

	 @Transactional(readOnly = true)
	public List<String> getAllConsumerProgramStructureId(){
		
		List<String> consumerProgramStructureIdList = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="SELECT " + 
				"    id " + 
				"FROM " + 
				"    exam.consumer_program_structure";
		
		try {
			
			consumerProgramStructureIdList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		return consumerProgramStructureIdList;
	}
	
	 @Transactional(readOnly = true)
	public List<Integer> getAllAnnouncementIdByProgram(){
		
		List<Integer> announcementIdsList = new ArrayList<Integer>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="SELECT " + 
				"    id " + 
				"FROM " + 
				"    portal.announcements " + 
				"WHERE " + 
				"    program = 'All' " + 
				"        AND programStructure = 'All'";
		
		try {
			
			announcementIdsList = (List<Integer>) jdbcTemplate.query(sql, new SingleColumnRowMapper(Integer.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		return announcementIdsList;
	}
	
	@Transactional(readOnly = true)
	public List<AnnouncementStudentPortalBean> getAllAnnouncementIdOfSingleProgram(){
		
		List<AnnouncementStudentPortalBean> announcementIdsList = new ArrayList<AnnouncementStudentPortalBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql="select * from portal.announcements a where program<> 'All' and programStructure <> 'All'" ;
						
		try {
			announcementIdsList = (List<AnnouncementStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<AnnouncementStudentPortalBean>(AnnouncementStudentPortalBean.class));
		 }catch(Exception e){
//			 e.printStackTrace();
		 }
		return announcementIdsList;
	}
	
	 @Transactional(readOnly = true)
	public List<AnnouncementStudentPortalBean> getAllAnnouncementIdOfAllProgramStructure(){
		
		List<AnnouncementStudentPortalBean> announcementIdsList = new ArrayList<AnnouncementStudentPortalBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="select * from portal.announcements a where program<> 'All' and programStructure = 'All'" ;
						
		try {
		
			announcementIdsList = (List<AnnouncementStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<AnnouncementStudentPortalBean>(AnnouncementStudentPortalBean.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		return announcementIdsList;
	}

	 @Transactional(readOnly = true)
	public List<String> getMasterKeyByAnnouncementId(String id) {
		try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT master_key " +			 
				"FROM " + 
				"    portal.announcement_master_key_pivot " + 
				"WHERE " + 
				"    announcementId = ? " ;
		
		List<String> masterKeyList = jdbcTemplate.queryForList(sql,new Object[] {id}, String.class);
		//System.out.println("SQL"+sql);
		//System.out.println("Key"+masterKeyList);
		//If mapping is not present in current table then it would be in history table
		if(masterKeyList.size() == 0)
			masterKeyList = getMasterKeyByAnnouncementIdInHistoryTable(id);
		return masterKeyList;
		
		}
		catch(Exception e) {
//			e.printStackTrace();
			return null;
		}
		
	}
	
	 @Transactional(readOnly = true)
	public ArrayList<AnnouncementMasterBean> getConsumerProgramStructureDataById(String commaSeperatedIds){
		
		ArrayList<AnnouncementMasterBean> consumerProgramStructureList = new ArrayList<AnnouncementMasterBean>();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String sql="SELECT " + 
				"cps.id AS id, "+	
				"    p.code AS program," + 
				"    ps.program_structure AS program_structure," + 
				"    ct.name AS consumer_type " + 
				"FROM " + 
				"    exam.consumer_program_structure cps " + 
				"        INNER JOIN " + 
				"    exam.program p ON cps.programId = p.id " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 		
				"		WHERE cps.id in(:consumerProgramStructureIds) " + 				
				" ORDER BY ct.name , p.code , ps.program_structure ";
		
		//System.out.println("Sql"+sql);
		//System.out.println("Comma Seperated "+commaSeperatedIds);
		
		List<Integer> consumerProgramStructureIds = Stream.of(commaSeperatedIds.split("\\D+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("consumerProgramStructureIds", consumerProgramStructureIds);
		
		try {
			consumerProgramStructureList = (ArrayList<AnnouncementMasterBean>) namedParameterJdbcTemplate.query(sql,parameters ,new BeanPropertyRowMapper(AnnouncementMasterBean.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		//System.out.println(consumerProgramStructureList);
		return consumerProgramStructureList;
	}
	
	 @Transactional(readOnly = true)
	public int getCountofConsumerProgramStructureDataById(String commaSeperatedIds){
		
		ArrayList<AnnouncementMasterBean> consumerProgramStructureList = new ArrayList<AnnouncementMasterBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="SELECT " + 
				"   count(cps.id) as count " + 			
				"FROM " + 
				"    exam.consumer_program_structure cps " + 
				"        INNER JOIN " + 
				"    exam.program p ON cps.programId = p.id " + 
				"        INNER JOIN " + 
				"    exam.program_structure ps ON ps.id = cps.programStructureId " + 
				"        INNER JOIN " + 
				"    exam.consumer_type ct ON ct.id = cps.consumerTypeId " + 
				"		WHERE cps.id in("+commaSeperatedIds+") " + 				
				" ORDER BY p.code , ps.program_structure;";
		
		
	
			int count = jdbcTemplate.queryForObject(sql, new Object[] {  }, Integer.class);

			//System.out.println(consumerProgramStructureList);
		return count;
	}
	

	@Transactional(readOnly = true)
	public int getProgramIdByCode(String program) {
		int programId = 0;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT id FROM exam.program "
					+ "WHERE code = ? ";
		try {	
			programId = jdbcTemplate.queryForObject(sql, Integer.class, program);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
		
		return programId;
	}
	
	@Transactional(readOnly = true)
	public int getProgramStructureIdByProgramStructure(String programStructure) {
		int programStructureId = 0;
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT id FROM exam.program_structure "
					+ "WHERE program_structure = ? ";
		try {
			programStructureId = jdbcTemplate.queryForObject(sql, Integer.class, programStructure);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
		
		return programStructureId;
	}
	
	 @Transactional(readOnly = true)
	public int getCpsIdByProgramAndProgramStucture(int programId , int programStructureId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		 
		String sql="select id from exam.consumer_program_structure where programId="+programId+" AND programStructureId = "+programStructureId+"";
		int id = 0;
			 try {
				 id = jdbcTemplate.queryForObject(sql, new Object[] {  }, Integer.class);
			 }catch(Exception e) {
				 return id;
			 }
		
		
		return id;
		
	}
	 
	 
	 @Transactional(readOnly = false)
	public int insertAnnouncementSingleMasterKey(final String announcementId ,final int master_key){
		final String sql = "INSERT INTO portal.announcement_master_key_pivot (announcementId, master_key) VALUES (?, ?)";


		jdbcTemplate = new JdbcTemplate(dataSource);


		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, announcementId);
				ps.setInt(2, master_key);
				
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int id = 0;
try {
		jdbcTemplate.update(psc, keyHolder);

		id = keyHolder.getKey().intValue();
}catch(Exception e){
//	e.printStackTrace();
	return id;
}	
		
		return id;

	}
	 
	 
	 @Transactional(readOnly = true)
public List<String> getAllConsumerProgramStructureIdByProgram(int programId){
		
		List<String> consumerProgramStructureIdList = new ArrayList<String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		

		String sql="SELECT " + 
				"    id " + 
				"FROM " + 
				"    exam.consumer_program_structure where programId='"+programId+"'";
		
		try {
			
			consumerProgramStructureIdList = (List<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		 }
		 catch(Exception e){
//			 e.printStackTrace();
			
		 }
		return consumerProgramStructureIdList;
	}

	 @Transactional(readOnly = true)
public AnnouncementStudentPortalBean findByIdAndMasterKey(String id,String masterKey){

	boolean isHistory = false;
	String sql = "SELECT  amkp.id AS masterId, " + 
			"    ps.id AS programStructureId,ps.program_structure AS programStructure ,p.id AS programId,p.code AS program,p.*,"
			+ " a.id, a.subject, a.description, a.startDate, a.endDate, a.active, a.category, a.attachment1, a.attachment2, a.attachment3,"
			+ " a.createdBy, a.createdDate, a.lastModifiedBy, a.lastModifiedDate  " + 
			"FROM  " + 
			"    portal.announcements a  " + 
			"        LEFT JOIN  " + 
			"    portal.announcement_master_key_pivot amkp ON amkp.announcementid = a.id  " + 
			"        LEFT JOIN  " + 
			"    exam.consumer_program_structure cps ON cps.id = amkp.master_key  " + 
			"        INNER JOIN  " + 
			"    exam.program p ON p.id = cps.programId  " + 
			"		INNER JOIN  " + 
			"	exam.program_structure ps ON ps.id = cps.programStructureId  " + 
			"WHERE  " + 
			"    a.id = ? AND amkp.master_key = ?";

	jdbcTemplate = new JdbcTemplate(dataSource);
	AnnouncementStudentPortalBean announcement = new AnnouncementStudentPortalBean();
	try {
	announcement = (AnnouncementStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] { id , masterKey }, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
	}catch(EmptyResultDataAccessException e)
	{
		System.out.println("Can not find in current announcement table.Hence find in history table. ");
		isHistory = true;
	}
		
	if(isHistory)
		announcement = findByIdAndMasterKeyHistory(id,masterKey);
	return announcement;
}

	 
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public int insertAnotherAnnouncement(AnnouncementStudentPortalBean bean , String masterId,String oldAnnouncementId) throws Exception
{
	final String sql = "INSERT INTO portal.announcements "
			+ "(subject,"
			+ "description,"
			+ "startDate,"
			+ "endDate,"
			+ "active, "
			+ "category,"
			+ "attachment1,"
			+ "attachment2,"
			+ "attachment3,"
			+ "createdBy,"
			+ "createdDate,"
			+ "lastModifiedBy, "
			+ "lastModifiedDate "		
			+ ")"
			+ "VALUES ( ?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";


	jdbcTemplate = new JdbcTemplate(dataSource);

	KeyHolder keyHolder = new GeneratedKeyHolder();

	final AnnouncementStudentPortalBean a = bean;
	PreparedStatementCreator psc = new PreparedStatementCreator() {

		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


			ps.setString(1, a.getSubject());
			ps.setString(2, a.getDescription());
			ps.setString(3, a.getStartDate());
			ps.setString(4, a.getEndDate());
			ps.setString(5, a.getActive());
			ps.setString(6, a.getCategory());
			ps.setString(7, a.getAttachmentFile1Path());
			ps.setString(8, a.getAttachmentFile2Path());
			ps.setString(9, a.getAttachmentFile3Path());
			ps.setString(10, a.getCreatedBy());
			ps.setString(11, a.getLastModifiedBy());
		

			return ps;
		}
	};
	


	jdbcTemplate.update(psc, keyHolder);

	int id = keyHolder.getKey().intValue();
	
	//Commented By Riya  :- It doesnt get inserted in post table since 2020
	//insertAnnouncementPostTable(a,id,"insert"); 
	
	updateAnnouncementMasterTable(id,masterId,bean.getMasterKey());
	
	//Inserting another announcement in Temporary table
		insertAnotherAnnouncementIdInTemp(oldAnnouncementId,bean,id);
	
	return id;

}

@Transactional(readOnly = false)
public void updateAnnouncementMasterTable(int id , String masterId,String masterKey) {
	String sql = " UPDATE portal.announcement_master_key_pivot SET announcementId=? WHERE id=?" ; 
	jdbcTemplate = new JdbcTemplate(dataSource);
	int i = jdbcTemplate.update(sql, new Object[] { 
			id,masterId
	});
	//System.out.println("masterId "+masterId);
	
	if(i == 0)
	{
		//The mapping would be in old mapping table, hence first delete it and the  insert it into new mapping table with new announcementId 
		insertIntoNewRecentMasterKey(id,masterId,masterKey);
	}

}


@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public void deleteAnnouncementProgram(String masterKey,String announcementId) throws Exception
{
	String sql = "DELETE FROM portal.announcement_master_key_pivot WHERE master_key=? and announcementId=?";
	jdbcTemplate = new JdbcTemplate(dataSource);
	
	int i = jdbcTemplate.update(sql, new PreparedStatementSetter() {
		public void setValues(PreparedStatement preparedStatement) throws SQLException {
			preparedStatement.setString(1,masterKey);
			preparedStatement.setString(2,announcementId);
		}
		});
	
	//Check as if i == 0 it means it is not present in current mapping table And also not in current announcement temp table
	//Deleting the announcementId from history table
		
	if(i == 0)
	{
			String sql1 = "DELETE FROM portal.announcement_master_key_pivot_history WHERE master_key=? and announcementId=?";
			 jdbcTemplate.update(sql1, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,masterKey);
						preparedStatement.setString(2,announcementId);
					}
					});
	}else
		deleteAnnoucementNewTable(masterKey,announcementId);

}

@Transactional(readOnly = true)
public List<MentionedDataBean> getMentionedDataBySapid(String sapid) {
	
	List<MentionedDataBean> mdBean = new ArrayList<MentionedDataBean>();
	
	String sql="SELECT  " + 
			"    mu.id AS id,mu.mention_to,mu.comment_id,pc.post_id,pc.sapid,pc.comment,pc.visibility,pc.createdDate,pc.lastModifiedDate,pc.master_comment_id,CONCAT(s.firstName ,' ', s.lastName) AS mentionBy " + 
			"FROM " + 
			"    lti.mentioned_users mu " + 
			"        INNER JOIN " + 
			"    lti.post_comments pc ON pc.id = mu.comment_id "
			+ "INNER JOIN  " + 
			"    exam.students s ON s.sapid = pc.sapid  " + 
			"WHERE " + 
			"    mu.mention_to = ?";

	jdbcTemplate = new JdbcTemplate(dataSource);
	
	try { 
		mdBean = (List<MentionedDataBean>) jdbcTemplate.query(sql, new Object[] { sapid }, new BeanPropertyRowMapper(MentionedDataBean.class));
	}catch(Exception e) {
//		e.printStackTrace();
	} 
	return mdBean;
	
}

@Transactional(readOnly = true)
public List<SessionQueryAnswerStudentPortal> getPublicQueriesForCourse(
		String sapId , String subject, String consumerProgramStructureId ) {

//	String sql = "Select * from acads.session_query_answer where (hasTimeBoundId <> 'Y' or hasTimeBoundId IS NULL) and subject = ? and sapid <> ? and isPublic = 'Y' order by createdDate desc ";
	String sql = " SELECT  " + 
			"    a.* " + 
			"FROM " + 
			"    acads.session_query_answer a " + 
			"        INNER JOIN " + 
			"    exam.students s ON s.sapid = a.sapid " + 
			"WHERE " + 
			"    (a.hasTimeBoundId <> 'Y' " + 
			"        OR a.hasTimeBoundId IS NULL) " + 
			"        AND a.subject = ? " + 
			"        AND s.consumerProgramStructureId = ? " + 
			"        AND a.sapid <> ?  " + 
			"        AND a.isPublic = 'Y'" + 
			"ORDER BY a.createdDate DESC ";
	
	List<SessionQueryAnswerStudentPortal> publicQueries = jdbcTemplate.query(
			sql,
			new Object[] { subject,
					consumerProgramStructureId,
					sapId  }, new BeanPropertyRowMapper(
					SessionQueryAnswerStudentPortal.class));
	return publicQueries;
}

@Transactional(readOnly = true)
	public ArrayList<String> getIndustryList() {
		
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername("rajiv.shah1@nmims.edu");
		 * config.setPassword("ngsce@2015LRzDdr5Igxw5fl5FEwzhnug4");
		 */ 
	    ArrayList<String> industryList = new ArrayList<String>();
		 
	
		try {
			/* connection = Connector.newConnection(config); */
			String query ="SELECT Industry__c FROM nm_WorkExperience__c " + 
						"GROUP BY Industry__c";

			this.connection = SFConnection.getConnection();
			
			if( connection == null ) {

				SFConnection sfConnection = new SFConnection(SFDC_USERID, SFDC_PASSWORD_TOKEN);
				connection = SFConnection.getConnection();
				
			}
			QueryResult qResult = connection.query(query);
			//System.out.println("inside----2----getNextSemRegistrationDetailsFromSFDCBySapid---"+query);
			//System.out.println("inside----3----getNextSemRegistrationDetailsFromSFDCBySapid----"+qResult.getSize());
			String industry = null;
			String id = null;
			if (qResult.getSize() > 0) {
				SObject[] records = qResult.getRecords();
				boolean done = false;
				while(!done) {
					for (SObject record : qResult.getRecords())
					{
						id = (String)record.getField("id");
						//System.out.println("###### record.Name: " + (String)record.getField("Industry__c"));
						industry = (String)record.getField("Industry__c");
						
						try {
							//bean.setId((String)record.getField("id"));
							if(!StringUtils.isBlank(industry)) {
								industryList.add(industry);
							}
						}catch (Exception e) {
//							e.printStackTrace();
						}
					}
					if (qResult.isDone()) {
						done = true;
					} else {
						qResult = connection.queryMore(qResult.getQueryLocator());
					}
				}
	
				}

			return industryList;
		}catch (Exception e) {

			//init();
			//getIndustryList();
//			e.printStackTrace();
			return industryList;
		}
	}

@Transactional(readOnly = false)
public void SaveAlmashinesId(String sapid,int id ) {
	//System.out.println("called update api...");
	String sql = " UPDATE exam.students SET almashinesId=? WHERE sapid=?" ; 
	jdbcTemplate = new JdbcTemplate(dataSource);
	jdbcTemplate.update(sql, new Object[] { 
			id,sapid
	});

}

@Transactional(readOnly = true)
public int findAlmashinesId(String sapid){

	String sql = "SELECT  almashinesId  FROM exam.students WHERE  a.id = ? AND amkp.master_key = ?";

	jdbcTemplate = new JdbcTemplate(dataSource);
	StudentStudentPortalBean student = (StudentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(StudentStudentPortalBean.class));

	return student.getAlmashinesId();
}

@Transactional(readOnly = true)
public String getYearOfPassing(String sapid) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	//System.out.println(sapid);
	String sql = "select max(resultProcessedYear) as resultProcessedYear from exam.passfail where sapid=?";

	jdbcTemplate = new JdbcTemplate(dataSource);
	PassFailBean passfail = (PassFailBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(PassFailBean.class));

	return passfail.getResultProcessedYear();
}

@Transactional(readOnly = true)
public String getYearOfPassingForMbawx(String sapid) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	//System.out.println(sapid);
	String sql = "select max(s.examYear) as resultProcessedYear from exam.mba_passfail p " + 
			"inner join exam.program_sem_subject pss on pss.id=p.prgm_sem_subj_id  " + 
			"inner join lti.student_subject_config s on s.id=p.timeboundId " + 
			" where sapid=? and pss.sem=5";

	String year="";
	try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		PassFailBean passfail = (PassFailBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(PassFailBean.class));

		year = passfail.getResultProcessedYear();
	} catch (DataAccessException e) { 
//		e.printStackTrace();
	}
	 return year;
}
@Transactional(readOnly = true)
	public boolean checkIfBookmarked(String sapId, String contentId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select count(*) from bookmarks.content_bookmarks cb where cb.sapid=? and cb.content_id=?";

		int count = (int)jdbcTemplate.queryForObject(sql, new Object[]{sapId, contentId}, new SingleColumnRowMapper(Integer.class));
		if(count==0){
			return false;
		} else{
			return true;
		}
	}


@Transactional(readOnly = false)
	public void setBookmark(ContentStudentPortalBean contentBean, String sapId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
		//Commented By Riya as it is shifted in controller
			/*if (sapId == null) {
				sapId = contentBean.getSapId();
			}*/

			if ("Y".equalsIgnoreCase(contentBean.getBookmarked())) {
				String searchSql = "select count(*) from bookmarks.content_bookmarks where sapid=? and content_id=?";
				int count = (int) jdbcTemplate.query(searchSql,  new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,sapId);
						preparedStatement.setString(2,contentBean.getId());
					}
					},new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
		                      return rs.getInt(1);
		                  }
						return 0;
					}
		            });

				String sql = "";
				if (count == 0) {
					sql = "insert into bookmarks.content_bookmarks(content_id,sapid,type,bookmarked) values(?,?,?,?) ";
					try {
						jdbcTemplate.update(sql, new PreparedStatementSetter() {
							public void setValues(PreparedStatement preparedStatement) throws SQLException {
								preparedStatement.setString(1,contentBean.getId());
								preparedStatement.setString(2,sapId);
								preparedStatement.setString(3,contentBean.getUrl());
								preparedStatement.setString(4,contentBean.getBookmarked());
							}
							});
					} catch (Exception e) {
//						e.printStackTrace();
					}
				}
			} else {
				String sql = "delete from bookmarks.content_bookmarks where sapid=? and content_id=?";
				try {
					jdbcTemplate.update(sql, new PreparedStatementSetter() {
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1,sapId);
							preparedStatement.setString(2,contentBean.getId());
						}
						});
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
//			ex.printStackTrace();
		}
	}

   @Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentBookmarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		List<ContentStudentPortalBean> contentBeanList = new ArrayList<ContentStudentPortalBean>();
		String sql = "select c.*, cb.bookmarked as bookmarked, cb.sapid as sapId from acads.content as c inner join bookmarks.content_bookmarks as cb "
				+ "on c.id=cb.content_id where cb.bookmarked='Y' and cb.sapid=? and cb.type = 'content'";
		try {
		contentBeanList = (ArrayList<ContentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));
		}catch(Exception e)
		{
//			e.printStackTrace();
		}
		return contentBeanList;
	}

@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getVideoContentBookmarks(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select c.*, concat('Prof. ',f.firstName,' ',f.lastName) as facultyName, cb.bookmarked as bookmarked, cb.sapid as sapId from acads.video_content as c inner join bookmarks.content_bookmarks as cb "
				+ "on c.id=cb.content_id inner join acads.faculty as f on f.facultyId = c.facultyId where cb.bookmarked='Y' and cb.sapid=? and cb.type = 'video'";

		List<VideoContentStudentPortalBean> videoContentBeanList = (ArrayList<VideoContentStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));

		return videoContentBeanList;
	}

	@Transactional(readOnly = true)
	public String getOutOfStudents(String sem, String month, String year, String program, String subject) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT  " + 
				"    COUNT(*) AS count " + 
				"FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    exam.students s ON pf.sapid = s.sapid " + 
				"WHERE " + 
				"    pf.sem = ? AND pf.program = ? " + 
				"        AND pf.writtenMonth = ? " + 
				"        AND pf.writtenYear = ? " + 				
				"        AND pf.subject = ? " + 
				"        AND pf.isPass = 'Y'";

		String count = (String) jdbcTemplate.queryForObject(sql,
				new Object[] { sem, program, month, year, subject },
				new SingleColumnRowMapper(String.class));

		return count;
	}
	
	
	@Transactional(readOnly = true)
	public List getRankSubjectWiseBySapId(final String sapId, final String sem, final String month,
			final String year, final String program, final String inSubject) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<LinkedCaseInsensitiveMap<String>> rankList = null;
		List<LinkedCaseInsensitiveMap<String>> onerallRankList = new ArrayList<LinkedCaseInsensitiveMap<String>>();
		List<LinkedCaseInsensitiveMap<String>> countList = null;		
		List rank = new ArrayList<>();
		StudentRankBean bean = new StudentRankBean();
		try {
			List<SqlParameter> parameters = Arrays.asList(new SqlParameter(Types.BIGINT));

			Map<String, Object> t = jdbcTemplate.call(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					CallableStatement callableStatement = con
							.prepareCall("{call exam.getRankSubjectWise (?,?,?,?,?,?)}");
					callableStatement.setNString(1, sapId);
					callableStatement.setNString(2, sem);
					callableStatement.setNString(3, month);
					callableStatement.setNString(4, year);
					callableStatement.setNString(5, program);
					callableStatement.setNString(6, inSubject);
//					callableStatement.registerOutParameter(2, Types.VARCHAR);
					return callableStatement;
				}
			}, parameters);

			rankList = (List<LinkedCaseInsensitiveMap<String>>) t.get("#result-set-1");
			onerallRankList = (List<LinkedCaseInsensitiveMap<String>>) t.get("#result-set-2");
			
			if (rankList.size() != 0) {
				bean.setSapid(String.valueOf(rankList.get(0).get("sapid")));
				bean.setSubject(String.valueOf(rankList.get(0).get("subject")));
				bean.setTotal(String .valueOf(rankList.get(0).get("total")).substring(0, String.valueOf(rankList.get(0).get("total")).indexOf("."))
						+ "/" + String.valueOf(rankList.get(0).get("outOfMarks")));
				bean.setRank(String.valueOf(rankList.get(0).get("rank")) + "/" + getOutOfStudents(sem, month, year, program, inSubject));
				bean.setName(String.valueOf(rankList.get(0).get("name")));
				bean.setStudentImage(String.valueOf(rankList.get(0).get("studentImage")));
			}
			rank.add(bean);
			rank.add(onerallRankList);
			
		} catch (Exception ex) {
//			ex.printStackTrace();
		}

		return rank;
	}
	
//public MettlExamUpcomingBean getMettlUpcomingQuickJoin(String sapid){
//		
//		CloseableHttpClient client = HttpClientBuilder.create().build();
//		String url = SERVER_PATH + "timeline/api/test/getTestQuestionsFromRedisByTestId";
//		MettlExamUpcomingBean responseBean = new MettlExamUpcomingBean();
//		
//		
//			HttpHeaders headers =  new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			
//			RestTemplate restTemplate = new RestTemplate();
//			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//			
//			MettlExamUpcomingBean beanToPostAsParam = new MettlExamUpcomingBean();
//			 
//			try {
//			 responseBean = restTemplate.postForObject(url, beanToPostAsParam, MettlExamUpcomingBean.class);
//			//System.out.println("responseBean :"+responseBean.toString());
//			
//		}catch(Exception e) {
//				//////System.out.println("Got exception for logErrorsREST : ");
//			//	e.printStackTrace();
//			//	logger.info("\n"+SERVER+": "+new Date()+" IN findAllTestQuestionsByTestId got testId : "+testId+" , Error :  "+e.getMessage());
//			return responseBean = getMettlUpcomingQuickJoinDB(sapid);
//			}
//		finally{
//			     //Important: Close the connect
//				 try {
//					client.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				//	logger.info("\n"+SERVER+": "+new Date()+" IN findAllTestQuestionsByTestId got testId : "+testId+" , Error :  "+e.getMessage());
//					
//				}
//			 }
//		
//		if(responseBean.getAcessKey() != null) {
//				return responseBean;
//		}else {
//			return responseBean = getMettlUpcomingQuickJoinDB(sapid);
//		}
//	
//	} 
	
	@Transactional(readOnly = true)
	public MettlExamUpcomingBean getMettlUpcomingQuickJoinDB(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		MettlExamUpcomingBean mettlExamUpcomingBean = new MettlExamUpcomingBean();
		
//		String sql = "SELECT " + 
//				"    * " + 
//				"FROM " + 
//				"    `exam`.`exams_pg_scheduleinfo_mettl` " + 
//				"WHERE " + 
//				"    `sapid` = ? " + 
//				"        AND DATE_ADD(CURRENT_TIMESTAMP, " + 
//				"        INTERVAL 15 MINUTE) BETWEEN `accessStartDateTime` AND `accessEndDateTime` " +
//				"		group by `sapid`, `acessKey` ";
		String sql = "SELECT " + 
				"    * " + 
				"FROM " + 
				"    `exam`.`exams_pg_scheduleinfo_mettl` " + 
				"WHERE " + 
				"    `sapid` = ? " + 
				"        AND DATE_ADD(CURRENT_TIMESTAMP, " + 
				"        INTERVAL 15 MINUTE) BETWEEN `reporting_start_date_time` AND `accessEndDateTime` " +
				"		group by `sapid`, `acessKey` ";
		
		//System.out.println(sql);
		
		
		return mettlExamUpcomingBean = (MettlExamUpcomingBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(MettlExamUpcomingBean.class));
	}
	
	@Transactional(readOnly = true)
	public boolean isDemoExamPending(String sapId){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =" SELECT COUNT(*) FROM exam.exambookings WHERE sapid = ? AND year = 2020 AND month = 'Jun' "
				  + " AND booked = 'Y' AND centerId <> -1 "
				  + " AND subject not IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') ";
		int bookingCount = (int)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new SingleColumnRowMapper(Integer.class));
		if (bookingCount > 0) {
			String sql2 = "SELECT COUNT(*) FROM exam.demoexam_attendance WHERE sapid = ? AND markAttend = 'Y'";
			int count = (int)jdbcTemplate.queryForObject(sql2, new Object[]{sapId}, new SingleColumnRowMapper(Integer.class));
			if(count==0){
				return true;
			} else{
				return false;
			}
		}else {
			return false;
		}
	}
	
	
	@Transactional(readOnly = true)
	public FacultyStudentPortalBean isFaculty(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? group by facultyId";
			FacultyStudentPortalBean faculty = (FacultyStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(FacultyStudentPortalBean.class));
			//System.out.println(faculty.getFirstName());
			return faculty;

	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getCommonSessionsSemesterBased(String semester,String program,String consumerProgramStructureId){

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		String sql =" SELECT asi.*, f.firstName, f.lastName " + 
					" FROM " + 
					"    acads.sessions asi, " + 
					"    exam.examorder eo, " +
					" 	 acads.faculty f " +
					" WHERE " + 
					"	 f.facultyId = asi.facultyId " +
					"    	 AND isCommon = 'Y' " + 
					"        AND (sem = :sem OR sem = 'All') " + 
					"        AND eo.order = (SELECT  " + 
					"            MAX(examorder.order) " + 
					"        FROM " + 
					"            exam.examorder " + 
					"        WHERE " + 
					"            acadSessionLive = 'Y') " + 
					"        AND ";
		
			if (!TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
				sql +=	" ((programList like (:program)) OR (programList='All')) " +
						" AND (asi.hasModuleId is null OR asi.hasModuleId <> 'Y') ";
			
			}else{
				sql +=	" ((programList LIKE (:program))) " +
						" AND (asi.hasModuleId is null or asi.hasModuleId = 'Y') ";
			}
		
			sql += " AND date >= curdate() ORDER BY asi.date,asi.startTime,asi.subject ";

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sem", semester);
		mapSource.addValue("program", "%"+program+"%");
		ArrayList<SessionDayTimeStudentPortal> getCommonSessionsList = (ArrayList<SessionDayTimeStudentPortal>)namedParameterJdbcTemplate.query(sql,mapSource,new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		return getCommonSessionsList;
	}
	 
	@Transactional(readOnly = true)
	public StudentsDataInRedisBean getResults(String userId){
		//System.out.println("Get Results");

		String url = SERVER_PATH + "timeline/api/results/getResultsDataFromRedisBySapid"; 
	//url = "https://uat-studentzone-ngasce.nmims.edu/timeline/api/results/getResultsDataFromRedisBySapid";
		 
		ResponseEntity<String> responseBean;
		StudentsDataInRedisBean beanToPostAsParam = new StudentsDataInRedisBean();

				HttpHeaders headers =  new HttpHeaders();
				headers.add("Content-Type", "application/json");
				RestTemplate restTemplate = new RestTemplate();
				StudentsDataInRedisBean response = new StudentsDataInRedisBean();
				beanToPostAsParam.setSapid(userId);
				HttpEntity<String> entity = new HttpEntity<>(beanToPostAsParam.toString(), headers);
				 Map<String,List> resultsData = new HashMap<String,List>();
		try {
			responseBean = restTemplate.postForEntity(url, beanToPostAsParam, String.class);
			//System.out.println(responseBean);

			JsonObject jsonResponse = new JsonParser().parse(responseBean.getBody()).getAsJsonObject();
			//System.out.println(jsonResponse);
			ObjectMapper objectMapper = new ObjectMapper();
			StudentsDataInRedisBean response_jackson = objectMapper.readValue(jsonResponse.toString(), StudentsDataInRedisBean.class);	

			if(response_jackson.getResultsData().get("studentMarksList").size()>0) {
				return response_jackson;
			}
			return null;


		} catch(Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getScheduledSessionForStudentsByCPSIdV1(StudentStudentPortalBean student, String year, String month, ArrayList<String> subjects) {
		
		//System.out.println("In getScheduledSessionForStudentsByCPSId ");
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String sql = " SELECT s.*, s.id as id, f.firstName, f.lastName FROM " + 
					 "    acads.sessions s" + 
					 "        INNER JOIN " + 
					 "    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					 "        INNER JOIN " + 
					 "    exam.program_sem_subject pss ON ssm.consumerProgramStructureId = pss.consumerProgramStructureId " + 
					 "        INNER JOIN " + 
					 "	  acads.faculty f ON s.facultyId = f.facultyId " +
					 "        AND ssm.program_sem_subject_id = pss.id " + 
					 "        AND s.month = ? " + 
					 "        AND s.year = ? " + 
					 "        AND ssm.consumerProgramStructureId = ? " + 
					 "        AND pss.sem = ? " + 
					 "		  AND pss.subject in ("+subjectCommaSeparated+") " +
					 "		  AND concat(date,' ',endTime) >= sysdate()" +
					 "		  order by date, startTime asc ";
		
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]
									{month,year,student.getConsumerProgramStructureId(), student.getSem()},  new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return scheduledSessionList;
	}

	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getPendingStudentsToRegisterInAlma() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select s.* from exam.students s where  " + 
				"	s.sapid in (select sapid from exam.registration where sem>1 )   " + 
				"	and s.almashinesId is null " + 
				"    and program !='MBA - WX' and program !='MBA - X' group by sapid ";
		ArrayList<StudentStudentPortalBean> students= new ArrayList<StudentStudentPortalBean>();
		try {
			students = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql.toString(), new Object[]{},  new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		} catch (DataAccessException e) { 
//			e.printStackTrace();
		} 
		return students;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<StudentStudentPortalBean> getPendingStudentsToRegisterInAlmaMbawx() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select s.* from exam.students s where  " + 
				"	s.sapid in (select sapid from exam.registration where sem>1 )   " + 
				"	and s.almashinesId is null and (s.programStatus is null or s.programStatus='') " + 
				"    and program  ='MBA - WX'  group by sapid ";
		ArrayList<StudentStudentPortalBean> students= new ArrayList<StudentStudentPortalBean>();
		try {
			students = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql.toString(), new Object[]{},  new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		} catch (DataAccessException e) { 
//			e.printStackTrace();
		} 
		return students;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getCommonSessionsSemesterBasedForUG(String semester,String program,String consumerProgramStructureId){

		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		String sql =" SELECT asi.*, f.firstName, f.lastName FROM " + 
					"    acads.sessions asi " +
					" 		INNER JOIN " +
					"	 exam.examorder eo ON eo.acadMonth = asi.month AND eo.year = asi.year " +
					" 		INNER JOIN " +
					"	 acads.faculty f ON asi.facultyId = f.facultyId " +
					" WHERE " + 
					"    isCommon = 'Y' AND (sem = :sem OR sem = 'All') " + 
					"        AND eo.order = (SELECT MAX(examorder.order) FROM exam.examorder WHERE acadSessionLive = 'Y') " + 
					"        AND (programList like (:program)) " +
					" 		 AND eo.acadMonth = asi.month AND eo.year = asi.year " + //Added to get only current cycle data
					"		 AND concat(date,' ',asi.endTime) >= sysdate() " +
					" ORDER BY asi.date,asi.startTime,asi.subject ";

		MapSqlParameterSource mapSource = new MapSqlParameterSource();
		mapSource.addValue("sem", semester);
		mapSource.addValue("program", "%"+program+"%");
		ArrayList<SessionDayTimeStudentPortal> getCommonSessionsList = (ArrayList<SessionDayTimeStudentPortal>)namedParameterJdbcTemplate.query(sql,mapSource,
																new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		return getCommonSessionsList;
	}
	
	@Transactional(readOnly = true)
	public int getPssIdBySubject(String subject, String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int pssId = 0;
		String sql = "SELECT id FROM exam.program_sem_subject WHERE subject = ? AND consumerProgramStructureId = ? " ;
		try {
			pssId = (int) jdbcTemplate.queryForObject(sql,new Object[] {subject, consumerProgramStructureId}, new SingleColumnRowMapper(Integer.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pssId;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<VideoContentStudentPortalBean> getVideoContentForSubjectNew(int programSemSubjectId, String earlyAccess){
		
		String sql = "";
		String sqlForEarlyAccess="";
		ArrayList<VideoContentStudentPortalBean> videoContentList = new ArrayList<VideoContentStudentPortalBean>();
		if("Yes".equalsIgnoreCase(earlyAccess)){
			sqlForEarlyAccess = " AND s.year = '"+CURRENT_ACAD_YEAR+"' AND s.month = '"+CURRENT_ACAD_MONTH+"' ";
		}else {
			sqlForEarlyAccess = " AND s.year = '" +getLiveAcadConentYear()+ "' AND s.month = '"+getLiveAcadConentMonth()+"' ";
		}
		
		sql = 	" SELECT  " + 
				"    s.corporateName,s.track, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, vc.*,s.meetingKey  " + 
				" FROM " + 
				"    acads.video_content vc " + 
				"        INNER JOIN " + 
				"    acads.sessions s ON vc.sessionId = s.id " + 
				"        INNER JOIN " + 
				"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
				"		INNER JOIN " + 
				"	 acads.faculty f ON vc.facultyId = f.facultyId " + 
				" WHERE 1 = 1 " + 
				"        AND ssm.program_sem_subject_id = ? " +
				sqlForEarlyAccess + 
				" ORDER BY CONCAT(s.date,s.startTime) " ;
				
		try {
			videoContentList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql,new Object[]{programSemSubjectId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		return videoContentList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getAllSessionsByCourseMapping(String userId){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionDayTimeStudentPortal> allSessions = new ArrayList<SessionDayTimeStudentPortal>();
		String sql =" SELECT s.*, s.id as id, f.firstName, f.lastName FROM " +
					"	exam.student_course_mapping scm " +
			        " 		INNER JOIN " +
					" 	exam.examorder eo ON scm.acadMonth = eo.acadMonth AND scm.acadYear = eo.year " +
			        " 		INNER JOIN " +
			        "	acads.sessions s ON eo.acadMonth = s.month AND eo.year = s.year " +
			        " 		INNER JOIN " +
			        "	acads.session_subject_mapping ssm ON s.id = ssm.sessionId AND scm.program_sem_subject_id = ssm.program_sem_subject_id " +
			        " 		INNER JOIN " +
			        "	acads.faculty f ON s.facultyId = f.facultyId " +
			        " WHERE role = 'Student' " +
			        " 	AND eo.order = (SELECT MAX(eo.order) FROM exam.examorder eo WHERE acadSessionLive = 'Y') " +
			        "	AND concat(s.date,' ',s.endTime) >= sysdate() " +
			        " 	AND userId = ? " +
			        "	order by s.date, s.startTime asc ";

		try {
			allSessions = (ArrayList<SessionDayTimeStudentPortal>) jdbcTemplate.query(sql, new Object[] {userId }, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		}catch(Exception e){
//			 e.printStackTrace();
		}
		return allSessions;
	}
	
	@Transactional(readOnly = true)
	public int getPassedSubjectForLateral(StudentStudentPortalBean student) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0; 
		String sql = "select count(*) from exam.passfail where isPass='Y' and (sapid =? or sapid=?) and subject in ("+
				"select subject from exam.program_sem_subject where consumerProgramStructureId = ?" 
				+ ")";  
		try {
			count = (int) jdbcTemplate.queryForObject(sql,new Object[] {student.getSapid() ,student.getPreviousStudentId(),student.getConsumerProgramStructureId()}, new SingleColumnRowMapper(Integer.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return count;
	}
	
	//Commented by Somesh as updated new query
	/*
	public ArrayList<SessionDayTimeBean> getScheduledSessionForStudentsByCPSIdV2(StudentBean student, String year, String month, ArrayList<String> subjects) {
	
		System.out.println("In getScheduledSessionForStudentsByCPSId ");
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String subjectCommaSeparated = "''";
		for (int i = 0; i < subjects.size(); i++) {
			if(i == 0){
				subjectCommaSeparated = "'" +subjects.get(i).replaceAll("'", "''") + "'";
			}else{
				subjectCommaSeparated = subjectCommaSeparated + ", '" + subjects.get(i).replaceAll("'", "''") + "'";
			}
		}
		
		String sql =" SELECT  " + 
					"    s.*, s.id AS id, f.firstName, f.lastName " + 
					" FROM " + 
					"    acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON s.id = ssm.sessionId " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode msc ON ssm.subjectCodeId = msc.id " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode_mapping mscm ON msc.id = mscm.subjectCodeId " + 
					"        AND ssm.consumerProgramStructureId = mscm.consumerProgramStructureId " + 
					"        INNER JOIN " + 
					"    acads.faculty f ON s.facultyId = f.facultyId " + 
					"        AND s.month = ? " + 
					"        AND s.year = ? " + 
					"        AND mscm.consumerProgramStructureId = ? " + 
					"        AND mscm.sem = ? " + 
					"		 AND msc.subjectname in ("+subjectCommaSeparated+") " +
					"        AND CONCAT(date, ' ', endTime) >= SYSDATE() " + 
					" ORDER BY date , startTime ASC;";
		
		ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<SessionDayTimeBean>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeBean>)jdbcTemplate.query(sql.toString(), new Object[]
									{month,year,student.getConsumerProgramStructureId(), student.getSem()},  new BeanPropertyRowMapper(SessionDayTimeBean.class));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return scheduledSessionList;
	}
	*/
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getScheduledSessionForStudentsByCPSIdV2(StudentStudentPortalBean student, String year, String month, ArrayList<String> currentSemPSSId) {
		
		//System.out.println("In getScheduledSessionForStudentsByCPSId ");
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String pssIdCommaSeparated = "''";
		if(currentSemPSSId==null) {
			pssIdCommaSeparated = "''";
		}else {
			for (int i = 0; i < currentSemPSSId.size(); i++) {
				if(i == 0){
					pssIdCommaSeparated = "'" +currentSemPSSId.get(i).replaceAll("'", "''") + "'";
				}else{
					pssIdCommaSeparated = pssIdCommaSeparated + ", '" + currentSemPSSId.get(i).replaceAll("'", "''") + "'";
				}
			}
		}
		
		String sql =" SELECT  " + 
					"    sessions.* " + 
					" FROM " + 
					"    acads.session_subject_mapping ssm " + 
					"        INNER JOIN " + 
					"    (SELECT  " + 
					"        s.*, f.firstName, f.lastName " + 
					"    FROM " + 
					"        acads.sessions s " + 
					"    INNER JOIN acads.faculty f ON s.facultyId = f.facultyId and s.year = ? and s.month = ? ) AS sessions ON ssm.sessionId = sessions.id " + 
					"        AND ssm.program_sem_subject_id IN  (" +pssIdCommaSeparated+ ") " + 
					"        AND CONCAT(date, ' ', endTime) >= SYSDATE() " +
					" ORDER BY date , startTime ASC ";
		
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]
									{year, month},  new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}

		return scheduledSessionList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getScheduledSessionForStudentsByCPSIdV3(String year, String month, ArrayList<Integer> currentSemPSSId) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		/*
		String pssIdCommaSeparated = "''";
		if(currentSemPSSId==null) {
			pssIdCommaSeparated = "''";
		}else {
			for (int i = 0; i < currentSemPSSId.size(); i++) {
				if(i == 0){
					pssIdCommaSeparated = "'" +currentSemPSSId.get(i).replaceAll("'", "''") + "'";
				}else{
					pssIdCommaSeparated = pssIdCommaSeparated + ", '" + currentSemPSSId.get(i).replaceAll("'", "''") + "'";
				}
			}
		}
		*/
		
		String sql =" SELECT  " + 
					"    qs.*, qs.program_sem_subject_id AS prgmSemSubId " + 
					" FROM " + 
					"    acads.quick_sessions qs " + 
					"        WHERE " + 
					"  	 program_sem_subject_id IN  (:currentSemPSSId) " + 
					"        AND CONCAT(date, ' ', endTime) >= SYSDATE() " +
					"		 AND year =:year AND month =:month " +
					" ORDER BY date , startTime ASC ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("currentSemPSSId", currentSemPSSId);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month", month);
		
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)namedParameterJdbcTemplate.query(sql, queryParams, 
								new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}

		return scheduledSessionList;
	}

	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentDetailsForSharingRank(StudentStudentPortalBean bean) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    r.sapid, "
				+ "    r.sem, "
				+ "    r.program, "
				+ "    r.consumerProgramStructureId, "
				+ "    eo.month, "
				+ "    eo.year, "
				+ "    (p.noOfSubjectsToClearSem * 100) AS subjectsCount "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.examorder eo ON r.month = eo.acadMonth "
				+ "        AND r.year = eo.year "
				+ "        INNER JOIN "
				+ "    exam.programs p ON p.program = r.program "
				+ "        AND p.consumerProgramStructureId = r.consumerProgramStructureId "
				+ "        AND p.active = 'Y' "
				+ "WHERE "
				+ "    r.sapid = ? AND r.sem = ? "
				+ "        AND eo.order = (SELECT  "
				+ "            MAX(eo.order) "
				+ "        FROM "
				+ "            exam.examorder eo "
				+ "        WHERE "
				+ "            r.month = eo.acadMonth "
				+ "                AND r.year = eo.year) "
				+ "        AND eo.live = 'Y' "
				+ "        AND r.consumerProgramStructureId IS NOT NULL";
		bean = (StudentStudentPortalBean) jdbcTemplate.queryForObject(query, new Object[] { bean.getSapid(), bean.getSem() }, new BeanPropertyRowMapper(StudentStudentPortalBean.class));
		
		return bean;
		
	}

	@Transactional(readOnly = true)
	public boolean isCourseMappingApplicableForCurrentAcadCycle(String sapid){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT COUNT(*) FROM " + 
					"    exam.student_course_mapping " + 
					" WHERE " + 
					"    acadMonth = '"+CURRENT_ACAD_MONTH+"' AND acadYear = "+CURRENT_ACAD_YEAR+" " + 
					"    AND userId = ? ";
		try{
			int rowCount = (int)jdbcTemplate.queryForObject(sql,new Object[] {sapid}, Integer.class);
			if(rowCount > 0){
				return true;
			}
		}catch(Exception e){
//			e.printStackTrace();
		}
			return false;
	}

	@Transactional(readOnly = true)
	public boolean checkIfStudentMarkedForUFM(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT COUNT(*) "
				+ " FROM `exam`.`ufm_students` `ufm` "
				+ " INNER JOIN `exam`.`examorder` `eo` ON `eo`.`year` = `ufm`.`year` AND `eo`.`month` = `ufm`.`month` "
				+ " WHERE `sapid` = ? "
				// TODO: Replace with live settings check
				+ " AND `eo`.`order` = ( SELECT MAX(`order`) FROM `exam`.`examorder` WHERE `timeTableLive` = 'Y' ) ";
		int rowCount = (int)jdbcTemplate.queryForObject(sql,new Object[] {sapid}, Integer.class);
		
		return rowCount > 0;
	}
	
	@Transactional(readOnly = true)
	public String getProgramNameFromCpsId(String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String program = "";
		String sql = "select p.name from exam.program p " + 
				" inner join exam.consumer_program_structure cps on cps.id= ?  " + 
				" where cps.programId=p.id " ;
		try {
			program = (String) jdbcTemplate.queryForObject(sql,new Object[] {consumerProgramStructureId}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return program;
	}
	
	@Transactional(readOnly = true)
	public String getSubjectnameForId( String subjectcodeMappingId ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String query = "SELECT  "
				+ "    subjectname "
				+ "FROM "
				+ "    exam.mdm_subjectcode_mapping scm "
				+ "        INNER JOIN "
				+ "    exam.mdm_subjectcode sc ON scm.subjectCodeId = sc.id "
				+ "WHERE "
				+ "    scm.id = ? " ;
		
		String subject = (String) jdbcTemplate.queryForObject( query, new Object[] { subjectcodeMappingId }, 
				new SingleColumnRowMapper(String.class) );
		
		return subject;
	}
	
	@Transactional(readOnly = true)
	public String getStudentsNameForSapid( String sapid ) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String query = "SELECT "
				+ "    CONCAT(firstName, ' ', lastName) "
				+ "FROM "
				+ "    exam.students "
				+ "WHERE "
				+ "    sapid = ? " ;
		
		String name = (String) jdbcTemplate.queryForObject( query, new Object[] { sapid }, 
				new SingleColumnRowMapper(String.class) );
		
		return name;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getTodaysSessions(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
		String sql =" SELECT  " + 
					"    s.* " + 
					" FROM " + 
					"    acads.session_subject_mapping ssc " + 
					"        INNER JOIN " + 
					"    exam.mdm_subjectcode_mapping msc ON msc.id = ssc.program_sem_subject_id " + 
					"    	 INNER JOIN " +
					"	 acads.sessions s ON ssc.sessionId = s.id " +
					" WHERE " +
					"	 s.date = CURDATE() " +
					"        AND CONCAT(ssc.consumerProgramStructureId, '-', msc.sem) = " + 
					"        (SELECT CONCAT(consumerProgramStructureId, '-', sem) FROM exam.registration WHERE sapid = ?) " + 
					" ORDER BY date , startTime ASC limit 2 ";
		
		try {
			sessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[] {sapid},
					new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return sessionList;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDataForCurrentCycle(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select r.* from exam.registration r where r.sapid = ? and  r.month = ? and r.year = ? ";
		StudentStudentPortalBean studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid, CURRENT_ACAD_MONTH, CURRENT_ACAD_YEAR}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		return studentRegistrationData;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getTodaysSessionsByPSSId(ArrayList<Integer> currentSemPSSId, StudentStudentPortalBean studentRegistrationForAcademicSession){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//		String pssIdsCommaSeparated = "''";
//		for (int i = 0; i < currentSemPSSId.size(); i++) {
//			if(i == 0){
//				pssIdsCommaSeparated = "'" +currentSemPSSId.get(i).replaceAll("'", "''") + "'";
//			}else{
//				pssIdsCommaSeparated = pssIdsCommaSeparated + ", '" + currentSemPSSId.get(i).replaceAll("'", "''") + "'";
//			}
//		}
		
		ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		
		String sql =" SELECT qs.*, qs.program_sem_subject_id AS prgmSemSubId FROM " + 
					"    acads.quick_sessions qs" + 
					" WHERE " + 
					"    CURRENT_TIMESTAMP BETWEEN SUBDATE(CONCAT(date,' ', startTime), INTERVAL 60 MINUTE) AND CONCAT(date,' ', endTime) " + 
					"        AND program_sem_subject_id IN (:currentSemPSSId ) " +
					"		 AND year =:year AND month =:month " +
					" ORDER BY startTime ";
		try {
			queryParams.addValue("currentSemPSSId", currentSemPSSId);
			queryParams.addValue("year", studentRegistrationForAcademicSession.getYear());
			queryParams.addValue("month", studentRegistrationForAcademicSession.getMonth());
			
			sessionList = (ArrayList<SessionDayTimeStudentPortal>)namedParameterJdbcTemplate.query(sql, queryParams, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return sessionList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<SessionDayTimeStudentPortal> getTodaysSessionsByPSSIdOld(ArrayList<String> currentSemPSSId){
		jdbcTemplate = new JdbcTemplate(dataSource);
				
				String pssIdCommaSeparated = "''";
				if(currentSemPSSId==null) {
				pssIdCommaSeparated = "''";
			}else {
					for (int i = 0; i < currentSemPSSId.size(); i++) {
						if(i == 0){
						pssIdCommaSeparated = "'" +currentSemPSSId.get(i).replaceAll("'", "''") + "'";
						}else{
						pssIdCommaSeparated = pssIdCommaSeparated + ", '" + currentSemPSSId.get(i).replaceAll("'", "''") + "'";
				}
					}
				}
				
				String sql =" SELECT  " + 
							"    * " + 
						" FROM " + 
							"    acads.quick_sessions " + 
							"        WHERE " + 
							"  	 program_sem_subject_id IN  (" +pssIdCommaSeparated+ ") " + 
							"        AND CONCAT(date, ' ', endTime) >= SYSDATE() " +
							" ORDER BY date , startTime ASC ";
				
				ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
				try {
					scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]
											{},  new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
				} catch (Exception e) {
//					e.printStackTrace();
				}
		
				return scheduledSessionList;
	}
	
	
	
	@Transactional(readOnly = true)
	public ArrayList<String> getPSSIds(String consumerProgramStructureId, String sem, ArrayList<String> waivedOffSubjects) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
//		String subjectCommaSeparated = "''";
//		String waivedOffSubjectSQL = "";
//		if (waivedOffSubjects != null && waivedOffSubjects.size() > 0) {
//			
//			for (int i = 0; i < waivedOffSubjects.size(); i++) {
//				if(i == 0){
//					subjectCommaSeparated = "'" +waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
//				}else{
//					subjectCommaSeparated = subjectCommaSeparated + ", '" + waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
//				}
//			}
//			waivedOffSubjectSQL = " AND subjectname NOT IN (" +subjectCommaSeparated+ ") ";
//		}
		
		String sql =" SELECT scm.id FROM exam.mdm_subjectcode sc " + 
					" INNER JOIN exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectcodeId " + 
					" WHERE scm.consumerProgramStructureId =:consumerProgramStructureId AND scm.sem =:sem ";
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("consumerProgramStructureId", consumerProgramStructureId);
		queryParams.addValue("sem", sem);
		
		if (waivedOffSubjects != null && waivedOffSubjects.size() > 0) {
			sql = sql + " AND subjectname NOT IN (:waivedOffSubjects) ";
			queryParams.addValue("waivedOffSubjects", waivedOffSubjects);
		}
		
		ArrayList<String> pssIdList = (ArrayList<String>)namedParameterJdbcTemplate.query(sql, queryParams, new SingleColumnRowMapper(String.class));
		return pssIdList;
	}
	
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentRegistrationDetails(String sapId) {
		StudentStudentPortalBean studentRegistrationData = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			String sql =  " SELECT * FROM exam.registration r WHERE r.sapid = ?  "
						+ " AND r.sem = (SELECT MAX(registration.sem) FROM exam.registration WHERE sapid = ?) ";
			studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return studentRegistrationData;
	}
	@Transactional(readOnly = true)
	public List<String> getUGPassSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";
		String sql = "select p.subject from remarkpassfail.passfail p " +  
				"where p.isPass > 0 AND p.active = 'Y' and p.sapid = ? AND p.isResultLive = 1 ; ";

		return jdbcTemplate.queryForList(sql, new Object[]{sapid}, String.class);
	}
	
	/*
	 *@param	sapId 
	 *@return	List<MailBean> 	:returning all archived mails communications of particular user from database
	 *@throws	Exception 		: If any exception occurs it will throws to caller
	 *@date 	Feb 13, 2021 */
	@Transactional(readOnly = true)
	public List<MailStudentPortalBean> getArchivedEmailCommunication(String sapId) throws Exception{
		StringBuilder GET_ALL_EMAILS=null;
		List<MailStudentPortalBean> emailCommunicationList=null;
		
		//Getting jdbcTemplet object
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Create StringBuilder object
		GET_ALL_EMAILS= new StringBuilder();
		
		//Prepare SQL query
		GET_ALL_EMAILS.append("SELECT * FROM PORTAL.MAILS WHERE ID IN ( ");
		GET_ALL_EMAILS.append("SELECT MAILTEMPLATEID FROM PORTAL.USER_MAILS WHERE SAPID LIKE '%"+sapId);
		GET_ALL_EMAILS.append("%') UNION SELECT * FROM PORTAL.MAILS_HISTORY WHERE ID IN ( ");  
		GET_ALL_EMAILS.append("SELECT MAILTEMPLATEID FROM PORTAL.USER_MAILS_HISTORY WHERE SAPID LIKE '%"+sapId);	
		GET_ALL_EMAILS.append("%') ORDER BY CREATEDDATE DESC");
		
		//execute SQL query
		emailCommunicationList=jdbcTemplate.query(GET_ALL_EMAILS.toString(),new BeanPropertyRowMapper(MailStudentPortalBean.class));
		//return mails list
		return emailCommunicationList;
		}
	
	
	/*This method is used to fetch a mail content based on mailTempletId from database. 
	 *@param	mailTemplateId 
	 *@return	MailBean 	:returning a archived mail communication details
	 *@throws	Exception 	: If any exception occurs it will throws to caller
	 *@date 	Feb 15, 2021 */
	@Transactional(readOnly = true)
	public MailStudentPortalBean getSingleArchiveMail(String mailTemplateId) throws Exception{
		String sql=null;
		MailStudentPortalBean mail=null;
		//Get jdbcTemplete object
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//Prepare SQL query
		sql = "select * from portal.mails_history where id = ? ";
		
		//use jdbcTemplate and execute SQL query
		mail = (MailStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[]{mailTemplateId}, new BeanPropertyRowMapper(MailStudentPortalBean.class));
		
		//return mail bean
		return mail;
	}
	

	@Transactional(readOnly = true)
	public StudentStudentPortalBean getStudentMaxSemRegistrationData(String sapId) {
        StudentStudentPortalBean studentRegistrationData = null;

        try {

            jdbcTemplate = new JdbcTemplate(dataSource);
            String sql = "select * from exam.registration r where r.sapid = ?  "
                    + "and r.sem = (select max(registration.sem) from exam.registration where sapid = ?) ";

            studentRegistrationData = (StudentStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{sapId,sapId}, new BeanPropertyRowMapper(StudentStudentPortalBean .class));
        } catch (Exception e) {
//            System.out.println("getStudentRegistrationData :"+e.getMessage());
        }
        return studentRegistrationData;
    }
	
	@Transactional(readOnly = true)
	public ExamOrderStudentPortalBean getExamOrderByYearMonth(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  "SELECT * FROM exam.examorder WHERE acadSessionLive = 'Y' AND year = ? AND acadMonth = ? ";
		ExamOrderStudentPortalBean order = (ExamOrderStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{year, month}, new BeanPropertyRowMapper(ExamOrderStudentPortalBean .class));
		return order;
	}
	

	//For LR Config
	//Added to find pssIds of Applicable subjects
	@Transactional(readOnly = true)
	public HashMap<String,String> getProgramSemSubjectId(ArrayList<String> lstOfApplicableSubjects,String masterKey) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		
		ArrayList<ConsumerProgramStructureStudentPortal> programSemSubjectIdList = new ArrayList<ConsumerProgramStructureStudentPortal>();
		
		
		StringBuilder sql = new StringBuilder();
		HashMap<String,String> pssIdWithSub = new HashMap<String,String>();
		
		
		
		sql.append("select msm.consumerProgramStructureId,msm.id  AS programSemSubjectId,sc.subjectname As Subject "
				   + " from exam.mdm_subjectcode sc  " 
				   + " inner join   "  
				   + " exam.mdm_subjectcode_mapping msm on sc.id = msm.subjectCodeId "
				   + " where sc.subjectname in (:subjects) "
				   + " and msm.consumerProgramStructureId = :masterKey and sc.active = 'Y';");
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("subjects", lstOfApplicableSubjects);
	    queryParams.addValue("masterKey", masterKey);
		//System.out.println("sql:- "+sql);
		
		try {	
			//programSemSubjectIdList =(ArrayList<ConsumerProgramStructure>) jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ConsumerProgramStructure.class));
			programSemSubjectIdList = (ArrayList<ConsumerProgramStructureStudentPortal>) namedParameterJdbcTemplate.query(sql.toString(), queryParams,
	                new BeanPropertyRowMapper<ConsumerProgramStructureStudentPortal>(ConsumerProgramStructureStudentPortal.class));
			//System.out.println("in getProgramSemSubjectId : "+programSemSubjectIdList);
		
			for(ConsumerProgramStructureStudentPortal pssIdsWithSub : programSemSubjectIdList)
				pssIdWithSub.put(pssIdsWithSub.getProgramSemSubjectId(), pssIdsWithSub.getSubject());
			
		}catch(Exception e) {
//			e.printStackTrace();
		}
		
		
			return pssIdWithSub;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<VideoContentStudentPortalBean> getVideoContentForSubjectNewForLR(String programSemSubjectId,String earlyAccess,double acadContentLiveOrder,double reg_order,String reg_month,String reg_year){
		
		String sql = "";
		String sql1 = "";
		ArrayList<VideoContentStudentPortalBean> videoContentList = new ArrayList<VideoContentStudentPortalBean>();
		if("Yes".equalsIgnoreCase(earlyAccess)){
			sql = 	" SELECT vc.*, s.meetingKey FROM acads.video_content vc " + 
					"        INNER JOIN acads.sessions s ON vc.sessionId = s.id " + 
					"        INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					" WHERE vc.year = '"+CURRENT_ACAD_YEAR+"' AND vc.month = '"+CURRENT_ACAD_MONTH+"'" + 
					"        AND ssm.program_sem_subject_id = ? ";
		}
		if(acadContentLiveOrder == reg_order)
			sql1 =" WHERE vc.year = '"+reg_year+"' AND vc.month = '"+reg_month+"'" ;
		else
			sql1 =" WHERE vc.year = '"+CURRENT_ACAD_YEAR+"' AND vc.month = '"+CURRENT_ACAD_MONTH+"'" ;
			
		
		
			sql = 	" SELECT vc.*, CONCAT('Prof. ', f.firstName, ' ', f.lastName) AS facultyName, s.corporateName, s.track " + 
					"	FROM acads.sessions s " + 
					"        INNER JOIN " + 
					"    acads.video_content vc ON vc.sessionId = s.id " + 
					"        INNER JOIN " + 
					"    acads.session_subject_mapping ssm ON ssm.sessionId = s.id " + 
					"        LEFT JOIN " + 
					"    acads.faculty f ON f.facultyId = vc.facultyId " + sql1 +
					//" WHERE vc.year = '"+getLiveAcadConentYear()+"' AND vc.month = '"+getLiveAcadConentMonth()+"'" +
					"    AND ssm.program_sem_subject_id = ? " ;
		
		try {
			videoContentList = (ArrayList<VideoContentStudentPortalBean>) jdbcTemplate.query(sql,new Object[]{programSemSubjectId}, new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return videoContentList;
	}
	
	/**
	 * @param passId - contains comma separated program sem subject id's.
	 * @param acadDateFormat - contains academic cycle in YYYY-MM-DD format. 
	 * @param earlyAccess - is student have early access on portal i.g. Yes or No
	 * @return List - return the video contents list for the applicable PSS id's and academic cycle.
	 * */
	@Transactional(readOnly = true)
	public List<VideoContentStudentPortalBean> getSessionRecording(String passId, String acadDateFormat, String earlyAccess) {
		StringBuilder GET_VIDEOS_BY_PSS = null;
		List<VideoContentStudentPortalBean> videoList = null;
		
		// Inject DataSource object to JdbcTemplate object
		jdbcTemplate = new JdbcTemplate(slaveDataSource);

		// Create StringBuilder object
		GET_VIDEOS_BY_PSS = new StringBuilder();

		//Create empty array list
		videoList = new ArrayList<VideoContentStudentPortalBean>();
		
		if("Yes".equalsIgnoreCase(earlyAccess)){
			// Prepare SQL Query For Lead Student
			GET_VIDEOS_BY_PSS.append(" SELECT vc.*, s.meetingKey,ssm.program_sem_subject_id as programSemSubjectId FROM acads.video_content vc ");
			GET_VIDEOS_BY_PSS.append(" INNER JOIN acads.sessions s ON vc.sessionId = s.id ");
			GET_VIDEOS_BY_PSS.append(" INNER JOIN acads.session_subject_mapping ssm ON ssm.sessionId = s.id ");
			GET_VIDEOS_BY_PSS.append(" WHERE vc.year = '"+CURRENT_ACAD_YEAR+"' AND vc.month = '"+CURRENT_ACAD_MONTH+"' ");
			GET_VIDEOS_BY_PSS.append(" AND ssm.program_sem_subject_id = ? ");
		}else {
			// Prepare SQL Query
			GET_VIDEOS_BY_PSS.append("SELECT track, CONCAT('Prof. ', firstName, ' ', lastName) AS facultyName, fileName, "); 
			GET_VIDEOS_BY_PSS.append("description, subject, sessionDate, thumbnailUrl, id, program_sem_subject_id as programSemSubjectId FROM acads.quick_video_content ");
			GET_VIDEOS_BY_PSS.append("WHERE  program_sem_subject_id = ? ");
			GET_VIDEOS_BY_PSS.append("AND acadDateFormat = '"+acadDateFormat+"' ");
		}
		
		try {
			// Execute JdbTemplate method
			videoList = jdbcTemplate.query(GET_VIDEOS_BY_PSS.toString(),new Object[] {passId},new BeanPropertyRowMapper(VideoContentStudentPortalBean.class));
			
		}catch (Exception e) {
//			e.printStackTrace();
		}
		
		// return videoList
		return videoList;
	}// getSessionRecording()
	
	@Transactional(readOnly = true)
    public List<ContentStudentPortalBean> getContentsForSubjectsForCurrentSessionNewLR(String programSemSubjectId, String earlyAccess,String acadDateFormat) {
  		
  		jdbcTemplate = new JdbcTemplate(dataSource);
  		StringBuffer sql = new StringBuffer();
  		List<ContentStudentPortalBean> contents = new ArrayList<ContentStudentPortalBean>();
  		
  		//StringBuffer sql1 = new StringBuffer();
  		
  		//if("Yes".equalsIgnoreCase(earlyAccess))
  			//sql1.append(" and c.month = '"+CURRENT_ACAD_MONTH+"' and c.year = " + CURRENT_ACAD_YEAR );
  		
		
  		
  		try{
//  			sql.append("select c.* " + 
//  					"from " + 
//  					"acads.contentid_consumerprogramstructureid_mapping ccm " + 
//  					"inner join  " + 
//  					"acads.content c on  c.id = ccm.contentId " + 
//  					"where  " + 
//  					" ccm.programSemSubjectId = ?  "  + sql1);
  			
  			//By Using Content Temporary Table
			sql.append("SELECT c.*,c.contentId as id FROM acads.content_denormalized c "
					  + "WHERE  c.programSemSubjectId = ?  AND acadDateFormat = ? and  c.activeDate  <= current_timestamp() ");
  					
  				
  				contents = jdbcTemplate.query(sql.toString(), new Object[]{programSemSubjectId,acadDateFormat}, new BeanPropertyRowMapper(ContentStudentPortalBean .class));	

  			//System.out.println("SQL getContentsForSubjectsForCurrentSessionNew = "+sql);
  			//System.out.println("programSemSubjectId = "+programSemSubjectId);
  			
  		
  			//System.out.println("SQL getContentsForSubjectsForCurrentSessionNew = "+sql);

  		}catch(Exception e){
//  			e.printStackTrace();
  		}
  		
  		return contents;
  	}
     
	@Transactional(readOnly = true)
	public List<ContentStudentPortalBean> getContentsForSubjectsForLastCyclesNew(String programSemSubjectId,String acadDateFormat) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//StudentBean studentDetails = getSingleStudentsData(sapid);
		
		
		
		//System.out.println("IN getContentsForSubjectsForLastCycles got \nsubject : "+programSemSubjectId);
		
		List<ContentStudentPortalBean> contents = new ArrayList<ContentStudentPortalBean>();
		
		StringBuffer sql1 = new StringBuffer();
		
		
		try{
		StringBuffer sql = new StringBuffer();
//			sql.append("select c.* " + 
//				"from  " + 
//				"acads.contentid_consumerprogramstructureid_mapping ccm  " + 
//				"inner join  " + 
//				"acads.content c on  c.id = ccm.contentId  " + 
//				"inner join  " + 
//				"exam.examorder eo  on c.year = eo.year and c.month = eo.acadMonth   " + 
//				"where  " + 
//				"eo.acadContentLive = 'Y'  " + 
//				" and eo.order = "+sql1 + 
//				" AND ccm.programSemSubjectId = ?   ") ;
		
		//By using Content Temporary table

		sql.append("SELECT c.*,c.contentId as id FROM  acads.content_denormalized c   " + 
				   "WHERE  c.programSemSubjectId = ?   AND acadDateFormat = ? ") ;
		
			contents= jdbcTemplate.query(sql.toString(), new Object[]{programSemSubjectId,acadDateFormat},new BeanPropertyRowMapper(ContentStudentPortalBean.class));
			//System.out.println("SQL getContentsForSubjectsForLastCycles = "+sql);
			
		}catch(Exception e){
//			e.printStackTrace();
		}
	
		return contents;
	}
	
	//Common method for current and last cycle content
	 public List<ContentStudentPortalBean> getContentsForLR(String programSemSubjectId, String acadDateFormat) {
	  		
	  		jdbcTemplate = new JdbcTemplate(dataSource);
	  		StringBuffer sql = new StringBuffer();
	  		List<ContentStudentPortalBean> contents = null;
	  		
	  		try{
  			//By Using Content Temporary Table
			sql.append("SELECT c.*,c.contentId as id FROM acads.content_denormalized c "
						  + "WHERE  c.programSemSubjectId = ?  AND acadDateFormat = ? and activeDate  <= current_timestamp() ");
	  					
	  				
	  		contents = jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,programSemSubjectId);
					preparedStatement.setString(2,acadDateFormat);
				}}, new BeanPropertyRowMapper(ContentStudentPortalBean.class));	

	  		}catch(Exception e){
//	  			e.printStackTrace();
	  		}
	  		
	  		return contents;
	  	}
	
	@Transactional(readOnly = true)
      public String getSubjectByProgramSemSubjectId(String programSemSubjectId) {
			
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql=	" SELECT  " + 
						"    subject " + 
						" FROM " + 
						"    exam.program_sem_subject " + 
						" WHERE " + 
						"    id = ? ";
		
			String subject = "";
			try {	
				subject = jdbcTemplate.queryForObject(sql, new Object[] {programSemSubjectId}, String.class);
			}catch(Exception e) {
//				e.printStackTrace();
			}
				return subject;
		}
      
      
     
     
  // Commented For now by Riya
     /*  public String getProgramSemSubjectId(StudentBean student) {
 		
 		jdbcTemplate = new JdbcTemplate(dataSource);
 		String programSemSubjectId = "";
 		String sql=	" SELECT id FROM  exam.program_sem_subject WHERE consumerProgramStructureId = ? AND subject = ? ";
 		
 		try {	
 			programSemSubjectId = jdbcTemplate.queryForObject(sql, new Object[] {student.getConsumerProgramStructureId(), student.getSubject() }, String.class);
 		}catch(Exception e) {
 			e.printStackTrace();
 		}
 			return programSemSubjectId;
 	}*/
     
   //Using content Live settings table
     /*public List<ContentBean> getContentsForSubjectsForCurrentSessionNew(String programSemSubjectId,String earlyAccess,StudentBean student) {
 		jdbcTemplate = new JdbcTemplate(dataSource);
 		String sql = null;
 		List<ContentBean> contents = null;
 		
 		try{
 			if("Yes".equalsIgnoreCase(earlyAccess)){
 				sql =" SELECT  " + 
 					 "    * " + 
 					 " FROM " + 
 					 "    acads.content c " + 
 					 "        INNER JOIN " + 
 					 "    acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " +
 					 "		  AND c.month = ? " + 
 					 "        AND c.year = ? " + 
 					 "        AND ccm.programSemSubjectId = ? ";
 		
 				contents = jdbcTemplate.query(sql, new Object[]{student.getEnrollmentMonth(),student.getEnrollmentYear(),
 												programSemSubjectId}, new BeanPropertyRowMapper(ContentBean .class));	
 		
 			}else{
 				sql =	" SELECT  " + 
 					"    * " + 
 					" FROM " + 
 					"    acads.content c " + 
 					"        LEFT JOIN " + 
 					"    acads.contentid_consumerprogramstructureid_mapping ccm ON ccm.contentId = c.id " + 
 					" WHERE " + 
 					"    ccm.programSemSubjectId = ? " + 
 					"        AND CONCAT(c.year, c.month) = (SELECT  " + 
 					"            CONCAT(cls.year, cls.month) " + 
 					"        FROM " + 
 					"            exam.content_live_settings cls " + 
 					"        WHERE " + 
 					"            cls.consumerProgramStructureId = ? " + 
 					"        ORDER BY STR_TO_DATE(CONCAT('01-', cls.month, '-', cls.year), " + 
 					"                '%d-%b-%Y') DESC " + 
 					"        LIMIT 1) " +
 					"  ";
 				System.out.println("SQL getContentsForSubjectsForCurrentSession = \n"+sql+"\n subject : "+programSemSubjectId);
 				contents = jdbcTemplate.query(sql, new Object[]{programSemSubjectId, student.getConsumerProgramStructureId()}, new BeanPropertyRowMapper(ContentBean .class));	
 			}
 		}catch(Exception e){
 			e.printStackTrace();
 		}
 		
 		return contents;
 	}*/
  
   //using content Live settings table retrieving last cycle content
 	
 	/*public List<ContentBean> getContentsForSubjectsForLastCyclesNew(String programSemSubjectId, String sapId) {
 		jdbcTemplate = new JdbcTemplate(dataSource);
 		StudentBean student = getSingleStudentsData(sapId);
 		String consumerProgramStructureId = student.getConsumerProgramStructureId();
 		
 		System.out.println("IN getContentsForSubjectsForLastCycles got \nsubject : "+programSemSubjectId+" \n consumerProgramStructureId : "+consumerProgramStructureId);
 		
 		List<ContentBean> contents = new ArrayList<ContentBean>();
 		String sql = "";
 		
 		try{
 			if (!StringUtils.isNumeric(programSemSubjectId)) {
 				
 				sql =  " SELECT c.* FROM acads.content c, exam.examorder eo where subject = ? "
 					 + " and c.month = eo.acadMonth and c.year = eo.year "
 					 + " and eo.order = (select max(examorder.order-1) from exam.examorder where acadContentLive = 'Y') ";
 				
 				contents= jdbcTemplate.query(sql, new Object[]{programSemSubjectId}, new BeanPropertyRowMapper(ContentBean .class));
 				System.out.println("In isNumeric True");
 				
 			}else {
 				System.out.println("In isNumeric False");
 			   sql =" SELECT  " + 
 					"    c.* " + 
 					" FROM " + 
 					"    acads.content c, " + 
 					"    acads.contentid_consumerprogramstructureid_mapping ccm " + 
 					" WHERE " + 
 					"    c.id = ccm.contentId " + 
 					"        AND ccm.programSemSubjectId = ? " + 
 					"        AND  concat(c.year,c.month) = ( " + 
 					"			select " + 
 					"				concat(c2.year,c2.month)  " + 
 					"			FROM " + 
 					"				acads.content c2, " + 
 					"				acads.contentid_consumerprogramstructureid_mapping ccm2 " + 
 					"			WHERE " + 
 					"				c2.id = ccm2.contentId " + 
 					"					AND ccm2.programSemSubjectId = ? " + 
 					"					AND STR_TO_DATE(concat('01-',c2.month,'-',c2.year), '%d-%b-%Y') < ( " + 
 					"						select  " + 
 					"							STR_TO_DATE(concat('01-',cls.month,'-',cls.year), '%d-%b-%Y') " + 
 					"                        from  " + 
 					"							exam.content_live_settings cls " + 
 					"						where  " + 
 					"							cls.consumerProgramStructureId = ? " + 
 					"							order by STR_TO_DATE(concat('01-',cls.month,'-',cls.year), '%d-%b-%Y')  desc  " + 
 					"							limit 1 " +
 					"                    ) " + 
 					"			group by concat(c2.year,c2.month)  " + 
 					"            order by STR_TO_DATE(concat('01-',c2.month,'-',c2.year), '%d-%b-%Y')  desc " + 
 					"            limit 1 " + 
 					"        ) " + 
 					" GROUP BY c.id " + 
 					" ";
 			   
 			   contents= jdbcTemplate.query(sql, new Object[]{programSemSubjectId,programSemSubjectId,consumerProgramStructureId}, 
 						 new BeanPropertyRowMapper(ContentBean .class));
 			}			

 			System.out.println("SQL getContentsForSubjectsForLastCycles = "+sql);
 		}catch(Exception e){
 			e.printStackTrace();
 		}
 	  
 		return contents;
 	}*/
 	
	@Transactional(readOnly = true)
     public String findStringForInClause(ArrayList<String> lstOfApplicableSubjects)
     {
    	 StringBuilder subjects = new StringBuilder();
 		
 		for(String subject: lstOfApplicableSubjects)
 		{
 			subjects.append("\"");
 			subjects.append(subject);
 			subjects.append("\",");
 		}
 		try{
 			return subjects.substring(0, subjects.length()-1);
 			
 		}catch(Exception e)
 		{
// 			e.printStackTrace();
 			return "";
 		}
     }
     

     //Get The Program Type From Program Code

     
      
	@Transactional(readOnly = true)
 	public String getProgramTypeFromCode(String programcode,String consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String program = "";
		String sql = "select programType from exam.programs   where program = ? and consumerProgramStructureId = ?" ;
		try {
			program = (String) jdbcTemplate.queryForObject(sql,new Object[] {programcode,consumerProgramStructureId}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
//			e.printStackTrace();
		} 
		return program;
	}

	@Transactional(readOnly = true)
 	public ArrayList<SessionDayTimeStudentPortal> getUpcomingCommonSessionsFromCommonQuickSessions(String consumerProgramStructureId, 
				String year, String month, String sem) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" SELECT " + 
					"	 qcs.* " + 
					" FROM " + 
					"    acads.quick_common_sessions qcs " + 
					"        WHERE " + 
					"  	 consumerProgramStructureId = ? " + 
					"		 AND year = ? AND month = ? " +
					"		 AND sem = ? " +
					"		 AND qcs.date >= CURDATE() ";
		
		ArrayList<SessionDayTimeStudentPortal> commonSessionsList = new ArrayList<SessionDayTimeStudentPortal>();
		try {
			commonSessionsList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql.toString(), new Object[]
							{consumerProgramStructureId, year, month, sem}, new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
	
		return commonSessionsList;
	}
 	
	@Transactional(readOnly = true)
 	public ArrayList<SessionDayTimeStudentPortal> getAllScheduledSessionsFromQuickSessions(String year, String month, ArrayList<Integer> currentSemPSSId) {
		
 		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
// 		jdbcTemplate = new JdbcTemplate(dataSource);
//		String pssIdCommaSeparated = StringUtils.join(currentSemPSSId, ",");
		
		String sql =" SELECT  " + 
					"    qs.*, qs.program_sem_subject_id AS prgmSemSubId " + 
					" FROM " + 
					"    acads.quick_sessions qs " + 
					"        WHERE " + 
					"  	 program_sem_subject_id IN  (:currentSemPSSId) " +
					"		 AND year =:year AND month =:month ";
		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("currentSemPSSId", currentSemPSSId);
	    queryParams.addValue("year", year);
	    queryParams.addValue("month", month);
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		try {
			scheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)namedParameterJdbcTemplate.query(sql, queryParams, 
								new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
	
		return scheduledSessionList;
	}


	
	@Transactional(readOnly = true)
 	public ArrayList<SessionDayTimeStudentPortal> getTodaysCommonSessionsByCPSId(StudentStudentPortalBean studentRegistrationForAcademicSession){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
		String sql =" SELECT " + 
					"    qcs.* " + 
					" FROM " + 
					"    acads.quick_common_sessions qcs " + 
					" WHERE " + 
					"    consumerProgramStructureId = ? AND sem = ? " + 
					"        AND year = ? AND month = ? " + 
					"        AND CURRENT_TIMESTAMP BETWEEN SUBDATE(CONCAT(date, ' ', startTime), INTERVAL 60 MINUTE) AND CONCAT(date, ' ', endTime) ";
		try {
			sessionList = (ArrayList<SessionDayTimeStudentPortal>)jdbcTemplate.query(sql, new Object[] {
					studentRegistrationForAcademicSession.getConsumerProgramStructureId(), studentRegistrationForAcademicSession.getSem(),
					studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth()}, 
					new BeanPropertyRowMapper(SessionDayTimeStudentPortal.class));
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return sessionList;
	}
 
	@Transactional(readOnly = true)
 	public ArrayList<Integer> getConsumerPgmStrIds(){
 		jdbcTemplate = new JdbcTemplate(dataSource);
		
		ArrayList<Integer> masterKeyList = new ArrayList();
		String sql ="SELECT " + 
				"    consumerProgramStructureId " + 
				"FROM " + 
				"    acads.session_query_answer q " + 
				"        INNER JOIN " + 
				"    exam.students s ON s.sapid = q.sapid " + 
				"GROUP BY consumerProgramStructureId";
		
		try {
			masterKeyList=(ArrayList<Integer>) jdbcTemplate.query(sql,new SingleColumnRowMapper(Integer.class));
		}catch (Exception e) {
//			e.printStackTrace();
		}
		return masterKeyList;
 	}
 	
	@Transactional(readOnly = true)
 	public ArrayList<SessionQueryAnswerStudentPortal> getPssIdandSubject(int consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionQueryAnswerStudentPortal> psssIdWithSubject = new ArrayList();
		String sql="SELECT id,subject FROM exam.program_sem_subject WHERE " + 
				"consumerProgramStructureId=?";
		try {
			psssIdWithSubject=(ArrayList<SessionQueryAnswerStudentPortal>) jdbcTemplate.query(sql, new Object[] {consumerProgramStructureId}, 
					new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		}catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
		return psssIdWithSubject;
	}
 	
	@Transactional(readOnly = true)
 	//Added by Saurabh
 	public ArrayList<SessionQueryAnswerStudentPortal> getQnAIdAndSubject(int consumerProgramStructureId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<SessionQueryAnswerStudentPortal> sessionQnAList = new ArrayList();
		String sql="SELECT " + 
				"    q.id,q.subject " + 
				"FROM " + 
				"    acads.session_query_answer q " + 
				"        INNER JOIN " + 
				"    exam.students s ON s.sapid = q.sapid " + 
				"WHERE s.consumerProgramStructureId=?";
		try {
			sessionQnAList=(ArrayList<SessionQueryAnswerStudentPortal>) jdbcTemplate.query(sql, new Object[] {consumerProgramStructureId}, 
					new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		}catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
		}
		return sessionQnAList;
	}
 	
	@Transactional(readOnly = false)
	public void updatePssIdForQnA(final ArrayList<String> sessionQueryAnswerIdList, final String pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "update acads.session_query_answer set programSemSubjectId=? where id=?";
		try {
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, pssId);
					ps.setString(2, sessionQueryAnswerIdList.get(i));
				}

				@Override
				public int getBatchSize() {
					return sessionQueryAnswerIdList.size();
				}
			});
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();

		}
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswerStudentPortal> getPublicQueriesForCourseV2(String sapId ,List<String> pssIdList, String year, String month ) {
		
		List<SessionQueryAnswerStudentPortal> publicQueries=new ArrayList<SessionQueryAnswerStudentPortal>();
//		String sql="SELECT  a.* FROM acads.session_query_answer a " + 
//				"WHERE (a.hasTimeBoundId <> 'Y' " + 
//				" OR a.hasTimeBoundId IS NULL) AND a.programSemSubjectId = ? " + 
//				" AND a.sapid <> ? AND a.isPublic = 'Y' ORDER BY a.createdDate DESC";
		namedParameterJdbcTemplate= new NamedParameterJdbcTemplate(dataSource);
		String sql="Select * from acads.session_query_answer where sapid <> (:sapId ) and (hasTimeBoundId <> 'Y' OR hasTimeBoundId IS NULL) and year=(:year ) and month=(:month ) and programSemSubjectId in (:pssIdList ) AND isPublic = 'Y' ORDER BY createdDate DESC";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		
		parameters.addValue("sapId", sapId);
		parameters.addValue("year", year);
		parameters.addValue("month", month);
		parameters.addValue("pssIdList", pssIdList);
		try{
		publicQueries = namedParameterJdbcTemplate.query(
				sql, parameters, new BeanPropertyRowMapper(
						SessionQueryAnswerStudentPortal.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		return publicQueries;
	}
	
	@Transactional(readOnly = true)
	public List<SessionQueryAnswerStudentPortal> getQueriesForSessionByStudentV2(String sapId, String programSemSubjectId) {
		String sql = "SELECT sqa.* FROM acads.session_query_answer sqa WHERE  sqa.sapid = ?  " + 
				"AND programSemSubjectId=? " + 
				"ORDER BY sqa.createdDate desc";
		List<SessionQueryAnswerStudentPortal> myQueries=new ArrayList<SessionQueryAnswerStudentPortal>();
		try {
		myQueries = jdbcTemplate.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setString(1, sapId);
				ps.setString(2, programSemSubjectId);
			}
		}, new BeanPropertyRowMapper(SessionQueryAnswerStudentPortal.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		return myQueries;		
	}


 	 //Added By Riya Operations performed on  Announcement Temporary table
    
 		//Updating  Announcement for that particular masterKey
	@Transactional(readOnly = false)
 		public void insertAnotherAnnouncementIdInTemp(String oldAnnouncementId,AnnouncementStudentPortalBean bean,int id)
 		{
 			jdbcTemplate = new JdbcTemplate(dataSource);
 			StringBuffer sql = new StringBuffer();
 			
 			//We aren't sure whether that announcementId is present in temp table or not, hence delete first and then insert it
 			String sql1 = "DELETE FROM portal.announcement_denormalized WHERE announcementId = ? AND master_key = ?";
 			jdbcTemplate.update(sql1,new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,oldAnnouncementId);
					preparedStatement.setString(2,bean.getMasterKey());
					
				}
				});
 			
 			 sql.append("INSERT INTO portal.announcement_denormalized "
 						+ "(subject,"
 						+ "master_key,"
 						+ "announcementId,"
 						+ "description,"
 						+ "startDate,"
 						+ "endDate,"
 						+ "active, "
 						+ "category,"
 						+ "attachment1,"
 						+ "attachment2,"
 						+ "attachment3 "	
 						+ ")"
 						+ "VALUES ( ?,?,?,?,?,?,?,?,?,?,?)");
 				

 				jdbcTemplate.update(sql.toString(),
 						new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,bean.getSubject());
						preparedStatement.setString(2,bean.getMasterKey());
						preparedStatement.setInt(3,id);
						preparedStatement.setString(4,bean.getDescription());
						preparedStatement.setString(5,bean.getStartDate());
						preparedStatement.setString(6,bean.getEndDate());
						preparedStatement.setString(7,bean.getActive());
						preparedStatement.setString(8,bean.getCategory());
						preparedStatement.setString(9,bean.getAttachmentFile1Path());
						preparedStatement.setString(10,bean.getAttachmentFile2Path());
						preparedStatement.setString(11,bean.getAttachmentFile3Path());
						
					}});
 				
 				
 			
 			
 		}
	
	@Transactional(readOnly = false)
 		public void insertIntoNewRecentMasterKey(int id,String masterId,String masterKey)
 		{
 			jdbcTemplate = new JdbcTemplate(dataSource);
 			//If that mapping is in the old mapping table , then delete it and insert mapping with new announcement Id in new table
 			String sql="DELETE FROM portal.announcement_master_key_pivot_history where id = ? ";
 			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,masterId);
				}
				});
 			
 			sql = "INSERT INTO portal.announcement_master_key_pivot (announcementId, master_key) VALUES (?, ?)";
 			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setInt(1,id);
					preparedStatement.setString(2,masterKey);
				}
				});
 			
 		}
	
	
 		//Deletion from New  Announcements Table 
	@Transactional(readOnly = false)
 		public void deleteAnnoucementNewTable(String masterKey,String announcementId)
 		{
 			String sql = "DELETE FROM portal.announcement_denormalized WHERE master_key=? and announcementId=?";
 			jdbcTemplate = new JdbcTemplate(dataSource);
 		
 			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1,masterKey);
					preparedStatement.setString(2,announcementId);
				}
				});
 			
 			
 		}
 		
	
 		//Insert  Announcement and masterKey into table
	@Transactional(readOnly = false)
 			public void insertAnnouncementIntoTempTable(final List<String> consumerprogramIdsList,final int announcementId,AnnouncementStudentPortalBean Bean ) {
 				
 				StringBuffer sql = new StringBuffer();
 				 sql.append("INSERT INTO portal.announcement_denormalized "
 						+ " (master_key,"
 						+ "announcementId,"
 						+ "subject,"
 						+ "description,"
 						+ "startDate,"
 						+ "endDate,"
 						+ "active,"
 						+ "category,"
 						+ "attachment1,"
 						+ "attachment2,"
 						+ "attachment3) "
 						+ "VALUES ( ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)") ; 
 					
 				
 			
 				final AnnouncementStudentPortalBean a = Bean;
 				
 					int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

 						@Override
 						public void setValues(PreparedStatement ps, int i) throws SQLException {
 							
 							ps.setString(1, consumerprogramIdsList.get(i));	
 							ps.setInt(2, announcementId);
 							ps.setString(3, a.getSubject());
 							ps.setString(4, a.getDescription());
 							ps.setString(5, a.getStartDate());
 							ps.setString(6, a.getEndDate());
 							ps.setString(7, a.getActive());
 							ps.setString(8, a.getCategory());
 							ps.setString(9, a.getAttachmentFile1Path());
 							ps.setString(10, a.getAttachmentFile2Path());
 							ps.setString(11, a.getAttachmentFile3Path());
 						
 						}

 						@Override
 						public int getBatchSize() {
 							return consumerprogramIdsList.size();
 						}
 					  });
 				
 				
 			}
 			
			@Transactional(readOnly = false)
 			public void updateAnnouncementTempTable(List<String> masterKeys,AnnouncementStudentPortalBean bean)
 			{
 				
 				StringBuffer sql = new StringBuffer();
 				
 				
 				 sql.append("Update portal.announcement_denormalized set " + 
 				 		" subject=?,  " + 
 				 		" description=?, " + 
 				 		" startDate=?, " + 
 				 		" endDate=?, " + 
 				 		" active=?, " + 
 				 		" category=?, " + 
 				 		" attachment1 = ?, " + 
 				 		" attachment2 = ?, " + 
 				 		" attachment3 = ? " + 
 				 		" where announcementId= ?");
 					 
 					int i = jdbcTemplate.update(sql.toString(),
 							new PreparedStatementSetter() {
 						public void setValues(PreparedStatement preparedStatement) throws SQLException {
 							preparedStatement.setString(1,bean.getSubject());
 							preparedStatement.setString(2,bean.getDescription());
 							preparedStatement.setString(3,bean.getStartDate());
 							preparedStatement.setString(4,bean.getEndDate());
 							preparedStatement.setString(5,bean.getActive());
 							preparedStatement.setString(6,bean.getCategory());
 							preparedStatement.setString(7,bean.getAttachmentFile1Path());
 							preparedStatement.setString(8,bean.getAttachmentFile2Path());
 							preparedStatement.setString(9,bean.getAttachmentFile3Path());
 							preparedStatement.setString(10,bean.getId());
 							
 						}});
 					
 				
 				/*If i = 0 , means it is not present in announcement temporary table, hence  the
 					insert  in the new table*/
 					
 				if(i==0)
 					insertAnnouncementIntoTempTable(masterKeys,Integer.parseInt(bean.getId()),bean);
 				
 				
 			}
			
 			//Find id and masterKey in history table
			@Transactional(readOnly = true)
 			public AnnouncementStudentPortalBean findByIdAndMasterKeyHistory(String id,String masterKey){

 				StringBuffer sql = new StringBuffer();
 				sql.append("SELECT  amkp.id AS masterId, " + 
 						"    ps.id AS programStructureId,ps.program_structure AS programStructure ,p.id AS programId,p.code AS program,p.*,"
 						+ " a.id, a.subject, a.description, a.startDate, a.endDate, a.active, a.category, a.attachment1, a.attachment2, a.attachment3,"
 						+ " a.createdBy, a.createdDate, a.lastModifiedBy, a.lastModifiedDate  " + 
 						"FROM  " + 
 						"    portal.announcements_history a  " + 
 						"        INNER JOIN  " + 
 						"    portal.announcement_master_key_pivot_history amkp ON amkp.announcementid = a.id  " + 
 						"        INNER JOIN  " + 
 						"    exam.consumer_program_structure cps ON cps.id = amkp.master_key  " + 
 						"        INNER JOIN  " + 
 						"    exam.program p ON p.id = cps.programId  " + 
 						"		INNER JOIN  " + 
 						"	exam.program_structure ps ON ps.id = cps.programStructureId  " + 
 						"WHERE  " + 
 						"    a.id = ? AND amkp.master_key = ?");

 				jdbcTemplate = new JdbcTemplate(dataSource);
 				AnnouncementStudentPortalBean announcement = (AnnouncementStudentPortalBean) jdbcTemplate.queryForObject(sql.toString(), new Object[] { id , masterKey }, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
 				
 				return announcement;
 			}
			
			@Transactional(readOnly = true)
 			public List<String> getMasterKeyByAnnouncementIdInHistoryTable(String id) {
 				try {
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				StringBuffer sql = new StringBuffer();
 				sql.append("SELECT master_key " +			 
 						"FROM " + 
 						"    portal.announcement_master_key_pivot_history   " + 
 						"WHERE " + 
 						"    announcementId = ?");
 				
 				List<String> masterKeyList =jdbcTemplate.queryForList(sql.toString(),new Object[] {id}, String.class);
 				
 			return masterKeyList;
 				}
 				catch(Exception e) {
// 					e.printStackTrace();
 					return null;
 				}
 				
 			}

 	//End of Announcement Temporary table implementation

 	/*================Announcement History Table Updation ===================*/
			@Transactional(readOnly = false)
 			public void updateAnnouncementHistoryTable(AnnouncementStudentPortalBean bean) throws Exception
 			{
 				String sql = "Update portal.announcements_history set "
 						+ "subject=?,"
 						+ "description=?,"
 						+ "startDate=?,"
 						+ "endDate=?,"
 						+ "active=?,"
 						+ "category=?,"
 						+ "attachment1 = ?,"
 						+ "attachment2 = ?,"
 						+ "attachment3 = ?,"
 						+ "program = ?,"
 						+ "programStructure = ?, "
 						+ "createdBy = ? , "
 						+ "lastModifiedBy = ? , "
 						+ " createdDate = current_timestamp() ,"
 						+ "lastModifiedDate = current_timestamp() "
 						+ " where id= ?";



 				jdbcTemplate = new JdbcTemplate(dataSource);

 				jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,bean.getSubject());
						preparedStatement.setString(2,bean.getDescription());
						preparedStatement.setString(3,bean.getStartDate());
						preparedStatement.setString(4,bean.getEndDate());
						preparedStatement.setString(5,bean.getActive());
						preparedStatement.setString(6,bean.getCategory());
						preparedStatement.setString(7,bean.getAttachmentFile1Path());
						preparedStatement.setString(8,bean.getAttachmentFile2Path());
						preparedStatement.setString(9,bean.getAttachmentFile3Path());
						preparedStatement.setString(10,bean.getProgram());
						preparedStatement.setString(11,bean.getProgramStructure());
						preparedStatement.setString(12,bean.getCreatedBy());
						preparedStatement.setString(13,bean.getLastModifiedBy());
						preparedStatement.setString(14,bean.getId());
					}
					});
 				
 				
 			}
 		/* 
 		 * Delete All the announcement Id mapping in mapping table
 		 */
			@Transactional(readOnly = false)
 			public int deleteWholeAnnouncementMapping(String announcementId) 
 			{
 				String sql = "DELETE FROM portal.announcement_master_key_pivot WHERE  announcementId=?";
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				return jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,announcementId);
					}
					});
 				
 			}
 			
			@Transactional(readOnly = false)
 			public void deleteWholeAnnouncementMappingInTemp(String announcementId) 
 			{
 				/* Delete From Temp Table too */
 				
 				String sql_temp = "DELETE FROM portal.announcement_denormalized WHERE  announcementId=?";
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				jdbcTemplate.update(sql_temp, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,announcementId);
					}
					});
 			
 			}
 			
			@Transactional(readOnly = false)
 			public void deleteAnnouncementInHistory(String announcementId) 
 			{
 				
 				/*Delete Announcement Mapping From Mapping Table*/
 				
 				String sql_mapping = "DELETE FROM portal.announcement_master_key_pivot_history WHERE  announcementId=?";
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				jdbcTemplate.update(sql_mapping, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,announcementId);
					}
					});
 				
 				/*Delete Announcement From Announcement Table*/
 				String sql = "Delete from portal.announcements_history WHERE id = ?";
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				int i = jdbcTemplate.update(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1,announcementId);
					}
					}); 
 			
 				
 			} 
 			
			@Transactional(readOnly = true)
 			public AnnouncementStudentPortalBean findByIdInHistory(String announcementId)
 			{
 				StringBuffer sql = new StringBuffer("SELECT a.*, count(amkp.announcementId) as `count`  "
 						+ "FROM portal.announcements_history a     "
 						+ "inner join  "
 						+ "portal.announcement_master_key_pivot_history  amkp ON a.id = amkp.announcementId  "
 						+ "where a.id = ? group by amkp.announcementId ");
 				
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				AnnouncementStudentPortalBean announcement = new AnnouncementStudentPortalBean();
 				try {
 				announcement = (AnnouncementStudentPortalBean) jdbcTemplate.queryForObject(sql.toString(), new Object[] {announcementId}, new BeanPropertyRowMapper(AnnouncementStudentPortalBean.class));
 				}catch(Exception e)
 				{
// 					e.printStackTrace();
 				}
 				return announcement;
 			}
 			
 			public void updateAnnouncementInHistory(List<String> masterKeys,AnnouncementStudentPortalBean bean) throws Exception
 			{
 				
 				/* First Delete the announcement and it's mapping in history */
 				deleteAnnouncementInHistory(bean.getId());
 				
 				/* Add That announcement as new entry in announcement table */
 				insertAnnouncement(masterKeys, bean);
 				
 				
 			}

 			
 			@Transactional(readOnly = true)
 			public ArrayList<String> getCurrentCycleSubjects(String consumerProgramStructureId, String sem, ArrayList<String> waivedOffSubjects) {
 				
 				jdbcTemplate = new JdbcTemplate(dataSource);
 				
 				String subjectCommaSeparated = "''";
 				String waivedOffSubjectSQL = "";
 				if (waivedOffSubjects != null && waivedOffSubjects.size() > 0) {
 					
 					for (int i = 0; i < waivedOffSubjects.size(); i++) {
 						if(i == 0){
 							subjectCommaSeparated = "'" +waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
 						}else{
 							subjectCommaSeparated = subjectCommaSeparated + ", '" + waivedOffSubjects.get(i).replaceAll("'", "''") + "'";
 						}
 					}
 					waivedOffSubjectSQL = " AND subjectname NOT IN (" +subjectCommaSeparated+ ") ";
 				}
 				
 				String sql =" SELECT subjectname FROM exam.mdm_subjectcode sc " + 
 							" INNER JOIN exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectcodeId " + 
 							" WHERE scm.consumerProgramStructureId = ? AND scm.sem = ? " + 
 							  waivedOffSubjectSQL;
 				ArrayList<String> pssIdList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[] {consumerProgramStructureId, sem}, new SingleColumnRowMapper(String.class));
 				return pssIdList;
 			}
	 	public ArrayList<StudentStudentPortalBean> getPendingStudentsToRegisterInAlmaMbax() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select s.* from exam.students s where  " + 
					"	s.sapid in (select sapid from exam.registration where sem>1 )   " + 
					"	and s.almashinesId is null and (s.programStatus is null or s.programStatus='') " + 
					"    and program  ='MBA - X'  group by sapid ";
			ArrayList<StudentStudentPortalBean> students= new ArrayList<StudentStudentPortalBean>();
			try {
				students = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql.toString(), new Object[]{},  new BeanPropertyRowMapper(StudentStudentPortalBean.class));
			} catch (DataAccessException e) { 
//				e.printStackTrace();
			} 
			return students;
		}
	 	public ArrayList<String> getPassSubjectsForMbaxStudent(String sapid) {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select sem from exam.mbax_passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

			ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

			return subjectsList;
		}
	 	public String getYearOfPassingForMbax(String sapid) {
	 		jdbcTemplate = new JdbcTemplate(dataSource);
	 		//System.out.println(sapid);
	 		String sql = "select max(s.examYear) as resultProcessedYear from exam.mbax_passfail p " + 
	 				"inner join exam.program_sem_subject pss on pss.id=p.prgm_sem_subj_id  " + 
	 				"inner join lti.student_subject_config s on s.id=p.timeboundId " + 
	 				" where sapid=? ";

	 		String year="";
	 		try {
	 			jdbcTemplate = new JdbcTemplate(dataSource);
	 			PassFailBean passfail = (PassFailBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(PassFailBean.class));

	 			year = passfail.getResultProcessedYear();
	 		} catch (DataAccessException e) { 
//	 			e.printStackTrace();
	 		}
	 		 return year;
	 	}
	 	
	 	@Transactional(readOnly = true)
	 	public ArrayList<String> getSubjectsForSem1and2(String consumerProgramStructureId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "select subject from exam.program_sem_subject where sem in (1,2) and consumerProgramStructureId=?";
			ArrayList<String> data =new ArrayList<String>();
	 		try {
	 		data = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {consumerProgramStructureId},new SingleColumnRowMapper(String.class));
	 		}
	 		catch (DataAccessException e) { 
//	 			e.printStackTrace();
	 		}
	 		return data;
	 	}
	 	
	 	@Transactional(readOnly = true)
	 	public String getSemFromStudentDetail(String sapid) {
	 		jdbcTemplate = new JdbcTemplate(dataSource);
	 		//System.out.println(sapid);
	 		String sql = "select sem from exam.students where sapid=? ";
	 		
	 		String sem="";
	 		try {
	 			jdbcTemplate = new JdbcTemplate(dataSource);
	 			StudentStudentPortalBean student = (StudentStudentPortalBean) jdbcTemplate.queryForObject(sql, new Object[] { sapid }, new BeanPropertyRowMapper(StudentStudentPortalBean.class));

	 			sem = student.getSem();
	 		} catch (DataAccessException e) { 
//	 			e.printStackTrace();
	 		}
	 		 return sem;
	 	}
	 	
	 	@Transactional(readOnly = true)
		public List<String> getPssIdBySubjectCodeId(String pssId){
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<String> pssIdList = new ArrayList<String>();
			String sql="SELECT id FROM exam.mdm_subjectcode_mapping WHERE subjectCodeId=(SELECT subjectCodeId FROM exam.mdm_subjectcode_mapping where id=?)";
			try {
			pssIdList = (List<String>)jdbcTemplate.query(sql,new Object[]{pssId}, new SingleColumnRowMapper(String.class));
			}catch (Exception e) {
				// TODO: handle exception
			}

			return pssIdList;
		}
	 	
	 	
	 	//ADDED BY GAURAV FOR LOU REPORT EXCEL DOWNLOAD
	 	
	 	@Transactional(readOnly = true)
		public ArrayList<String> getProgramTypeNameList(){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT DISTINCT (programType) FROM exam.programs WHERE active = 'Y' ORDER BY programType ";
			ArrayList<String> programTypeList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
			return programTypeList;
		}
	 	
	 	@Transactional(readOnly=true)
	 	public ArrayList<String> getProgramNameByProgramType(String ProgramType){
	 		jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT distinct(program) from exam.programs where programType = ?";
			ArrayList<String> programnameList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {ProgramType}, new SingleColumnRowMapper(String.class));
			return programnameList;
	 	}
	 	
	 	@Transactional(readOnly=true)
	 	public int getsembyprogramname(String programName){
	 		jdbcTemplate=new JdbcTemplate(dataSource);
	 		String sql="SELECT MAX(noOfSemesters) FROM exam.programs WHERE program = ?";
	 		int sem=jdbcTemplate.queryForObject(sql, new Object[] {programName},Integer.class);
	 		return sem;
	 	}
	 	
	 	@Transactional(readOnly=true)
	 	public ArrayList<StudentStudentPortalBean> generateloudata(louReportBean bean){
	 		jdbcTemplate = new JdbcTemplate(dataSource);
	 		ArrayList<Object> parameters = new ArrayList<Object>();
	 		String sql=" select sapid,firstName,lastName,fatherName,motherName,program,mobile,emailId,louConfirmed,louConfirmedTimeStamp,centerName,centerCode "
	 				 + " from exam.students where louConfirmed = 1 ";
	 		
	 		if(bean.getProgramType()!="") {
	 			sql = sql + " and program in (select program from exam.programs where programType=?) ";
	 			parameters.add(bean.getProgramType());
	 		}
	 		
	 		if(bean.getEnrollmentmonth()!="") {
	 			sql = sql + " and enrollmentMonth = ? ";
	 			parameters.add(bean.getEnrollmentmonth());
	 		}
	 		
	 		if(bean.getEnrollmentyear()!="") {
	 			sql = sql + " and enrollmentYear = ? ";
	 			parameters.add(bean.getEnrollmentyear());
	 		}
	 		
	 		if(bean.getDataOfSubmission()!="") {
	 			sql = sql + " and date(louConfirmedTimeStamp) = ?";
	 			parameters.add(bean.getDataOfSubmission());
	 		}
	 		
	 		if(bean.getProgramName()!="") {
	 			sql = sql + " and program = ?";
	 			parameters.add(bean.getProgramName());
	 		}
	 		
	 		if(bean.getSemTerm()!="") {
	 			sql = sql + " and sem = ?";
	 			parameters.add(bean.getSemTerm());
	 		}
//	 		System.out.println("sql:"+sql);
	 		Object[] args = parameters.toArray();
	 		ArrayList<StudentStudentPortalBean> list = (ArrayList<StudentStudentPortalBean>)jdbcTemplate.query(sql,args,new BeanPropertyRowMapper(StudentStudentPortalBean.class));
	 		return list;
	 	}
	 	
	 	@Transactional(readOnly=true) 
	 	public ArrayList<programStudentPortalBean> getprogramnamebyprogram() {
	 		jdbcTemplate =new JdbcTemplate(dataSource);
	 		String sql = " SELECT code,name FROM exam.program ";
	 		ArrayList<programStudentPortalBean> programs=(ArrayList<programStudentPortalBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(programStudentPortalBean.class));
	 		return programs;
	 	}
	 	
	 	@Transactional(readOnly=true) 
	 	public boolean checkLOUConfirmed(String sapid) {
	 		jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " select louConfirmed from exam.students where sapid = ?";
			boolean louConfirmed = jdbcTemplate.queryForObject(sql,new Object[]{sapid},boolean.class);
			return louConfirmed;
	 	}
	 	
	 	@Transactional(readOnly = false)
	 	public void savelouConfirmed(String sapid) {
	 		jdbcTemplate = new JdbcTemplate(dataSource);
	 		String sql = " update exam.students set louConfirmed=true,louConfirmedTimestamp=sysdate() where sapid=?";
			jdbcTemplate.update(sql,sapid);
	 	}
	 	
	 	@Transactional(readOnly=true)
	 	public ArrayList<CenterStudentPortalBean> getlcnamebycode() {
	 		
	 		jdbcTemplate=new JdbcTemplate(dataSource);
	 		String sql = " select lc,centerCode from exam.centers ";
	 		ArrayList<CenterStudentPortalBean> lc=(ArrayList<CenterStudentPortalBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(CenterStudentPortalBean.class));
	 		return lc;
	 	}

	 	@Transactional(readOnly=true)
	 	public ArrayList<SessionTrackBean> getAllTracksDetails() {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "SELECT * FROM acads.tracks WHERE active = 'Y' ";
			ArrayList<SessionTrackBean> tracksDetails = (ArrayList<SessionTrackBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionTrackBean.class));
			return tracksDetails;
		}
	 	
		@Transactional(readOnly = false)
		public int updateProgramStatus(String sapid,String programStatus){
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "update exam.students set programStatus = ? where sapid = ? ";
			return jdbcTemplate.update(sql,new Object[]{programStatus,sapid});
		}
		
		public long insertEMailRecord(MailStudentPortalBean emailbean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			// Changed since this will be only single insert//
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(
							"INSERT INTO portal.mails(subject,createdBy,createdDate,filterCriteria,body,fromEmailId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ",
							Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, emailbean.getSubject());
					statement.setString(2, emailbean.getCreatedBy());
					statement.setString(3, emailbean.getFilterCriteria());
					statement.setString(4, emailbean.getBody());
					statement.setString(5, emailbean.getFromEmailId());
					statement.setString(6, emailbean.getLastModifiedBy());
					return statement;
				}
			}, holder);

			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		}

		public long insertUserEMailRecord(MailStudentPortalBean mailBean,long mailTemplateId) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			String sql = " INSERT INTO portal.user_mails(sapid,mailId,createdDate,createdBy,fromEmailId,mailTemplateId,lastModifiedBy,lastModifiedDate) VALUES(?,?,sysdate(),?,?,?,?,sysdate()) ";
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, mailBean.getSapid());
					ps.setString(2, mailBean.getMailId());
					ps.setString(3, mailBean.getCreatedBy());
					ps.setString(4, mailBean.getFromEmailId());
					ps.setString(5, String.valueOf(mailTemplateId));
					ps.setString(6, mailBean.getLastModifiedBy());
					return ps;
				}
			}, holder);

			long primaryKey = holder.getKey().longValue();
			return primaryKey;
		}
}
