package com.nmims.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.nmims.beans.AnnouncementCareerservicesBean;
import com.nmims.beans.ExamOrderCareerservicesBean;
import com.nmims.beans.FacultyCareerservicesBean;

import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.UserAuthorizationBean;

public class LoginDAO extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(LoginDAO.class);
 
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	public StudentCareerservicesBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentCareerservicesBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";

			ArrayList<StudentCareerservicesBean> studentList = (ArrayList<StudentCareerservicesBean>)jdbcTemplate.query(sql, new Object[]{
					sapid,
					sapid
			}, new BeanPropertyRowMapper<StudentCareerservicesBean>(StudentCareerservicesBean.class));
			
			if(studentList != null && studentList.size() > 0){
				student = studentList.get(0);

				//set program for header here so as to use it in all other places
				student.setProgramForHeader(student.getProgram());
			}
			
			return student;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}
		
	}

	public List<ExamOrderCareerservicesBean> getLiveFlagDetails(){
		List<ExamOrderCareerservicesBean> liveFlagList = new ArrayList<ExamOrderCareerservicesBean>();

		final String sql = " Select * from exam.examorder order by examorder.order ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		liveFlagList = (ArrayList<ExamOrderCareerservicesBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderCareerservicesBean>(ExamOrderCareerservicesBean.class));

		return liveFlagList;
	}


	//	Added for SAS
	public List<AnnouncementCareerservicesBean> getAllActiveAnnouncements(String program,String progrmStructure){
			String sql = null;
			jdbcTemplate = new JdbcTemplate(dataSource);
			if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)){
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and program= ? and programStructure = ?  order by startDate desc ";

			}else{
				 sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > sysdate() and startDate <= sysdate() and(program= ? || program = 'All') and (programStructure = ? || programStructure = 'All')  order by startDate desc ";
			}
			
			List<AnnouncementCareerservicesBean> announcements = jdbcTemplate.query(sql, new Object[]{program,progrmStructure}, new BeanPropertyRowMapper<AnnouncementCareerservicesBean>(AnnouncementCareerservicesBean.class));

			return announcements;
		}
	public List<AnnouncementCareerservicesBean> getAllActiveAnnouncements(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM portal.announcements where active = 'Y' and endDate > date(sysdate()) order by startdate desc ";
		List<AnnouncementCareerservicesBean> announcements = jdbcTemplate.query(sql, new BeanPropertyRowMapper<AnnouncementCareerservicesBean>(AnnouncementCareerservicesBean.class));

		/*List<AnnouncementBean> jobAnnouncements = getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		return announcements;
	}	
	
	public double getMaxOrderWhereContentLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder = 0.0;
		try{

			String sql = "SELECT max(examorder.order) FROM exam.examorder where acadContentLive = 'Y'";

			examOrder = (double) jdbcTemplate.queryForObject(sql,new Object[]{},Double.class);


		}catch(Exception e){
			//logger.info("exception : "+e.getMessage());
		}

		return examOrder;

	}
	
	public UserAuthorizationBean getUserAuthorization(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM portal.user_authorization where userId = ?  ";
		try {
			UserAuthorizationBean user = jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper<UserAuthorizationBean>(UserAuthorizationBean.class));
			return user;
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}

	}

	public ArrayList<String> getAuthorizedCenterCodes(UserAuthorizationBean userAuthorization) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> centers = new ArrayList<String>();

		//Convert Mumbai, Kolkata, Delhi to 'Mumbai','Kolkata','Delhi'
		String authorizedLCWithQuotes = "'" + userAuthorization.getAuthorizedLC() + "'";
		authorizedLCWithQuotes = authorizedLCWithQuotes.replaceAll(",", "','");


		if(userAuthorization.getAuthorizedLC() != null && !"".equals(userAuthorization.getAuthorizedLC().trim())){
			String sql = "SELECT sfdcId FROM exam.centers where lc in (" + authorizedLCWithQuotes + ")";
			centers = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper<String>(String.class));
		}

		if(userAuthorization.getAuthorizedCenters() != null && !"".equals(userAuthorization.getAuthorizedCenters().trim())){
			//Add IC codes
			List<String> authorizedICs = Arrays.asList(userAuthorization.getAuthorizedCenters().split("\\s*,\\s*"));
			centers.addAll(authorizedICs);
		}

		return centers;
	}

	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper<String>(String.class));

		return subjectsList;
	}
	
	public FacultyCareerservicesBean isFaculty(String userId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where facultyId = ? group by facultyId";
		FacultyCareerservicesBean faculty = (FacultyCareerservicesBean)jdbcTemplate.queryForObject(sql, new Object[]{userId},new BeanPropertyRowMapper(FacultyCareerservicesBean.class));
		return faculty;
	}
	
}
