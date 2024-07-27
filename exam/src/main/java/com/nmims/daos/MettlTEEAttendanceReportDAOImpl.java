package com.nmims.daos;

import java.time.Year;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.MettlTEEAttendanceReportBean;
/**
 * 
 * @author shivam.pandey.EXT
 *
 */
@Repository
public class MettlTEEAttendanceReportDAOImpl extends BaseDAO implements MettlTEEAttendanceReportDAO{
	
	/*Variables*/
	@Autowired
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;	
	@Autowired
	@Qualifier("analyticsDataSource")
	private DataSource analyticsDataSource;
	
	/*Implemented Methods*/
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	
	@Override
	public List<MettlTEEAttendanceReportBean> getTEEAttendanceReportByCycle(String examYear, String examMonth)throws Exception 
	{
		//getting mettl attendance data from analytics DB
		namedJdbcTemplate = new NamedParameterJdbcTemplate(analyticsDataSource);
		MapSqlParameterSource param = new MapSqlParameterSource();
		
		String sql = "" +
				" SELECT" + 
				" 		sapid,subject,year,month,firstname as firstName,lastname as lastName,emailId,testTaken," + 
				" 		DATE(accessStartDateTime) AS examDate," + 
				" 		TIME(accessStartDateTime) AS examTime" + 
				" FROM" + 
				" 		`exam`.`exams_pg_scheduleinfo_mettl`" + 
				" WHERE" + 
				" 		year = :year" +
				" AND " +
				" 		month = :month";
		
		param.addValue("year", examYear);
		param.addValue("month", examMonth);
				
		List<MettlTEEAttendanceReportBean> teeAttendanceReport = namedJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<MettlTEEAttendanceReportBean>(MettlTEEAttendanceReportBean.class));
		
		return teeAttendanceReport;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public List<MettlTEEAttendanceReportBean> getICAndCenterCode() throws Exception 
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//To getting current Year
		int currentYear = Year.now().getValue();
		
		String sql = "" +
				" SELECT" + 
				" 		sapid, centerName AS ic, centerCode" + 
				" FROM" + 
				" 		`exam`.`students`" + 
				" WHERE" + 
				"    	validityEndYear >= "+currentYear;
		
		List<MettlTEEAttendanceReportBean> icAndCenterCodeList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<MettlTEEAttendanceReportBean>(MettlTEEAttendanceReportBean.class));
		
		return icAndCenterCodeList;
	}
	
	@Override
	public List<MettlTEEAttendanceReportBean> getAllLCList() throws Exception 
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "" +
				" SELECT" + 
				"    centerCode, lc" + 
				" FROM" + 
				"    `exam`.`centers`" +
				" WHERE centerCode <> '' ";
		
		List<MettlTEEAttendanceReportBean> studentLCList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<MettlTEEAttendanceReportBean>(MettlTEEAttendanceReportBean.class));
		
		return studentLCList;
	}
	
	@Override
	public List<MettlTEEAttendanceReportBean> getAllProgramList() throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "" +
				" SELECT " +
				" 	code as programCode, name as programName" +
				" FROM " +
				" 	`exam`.`program`";
		
		List<MettlTEEAttendanceReportBean> allProgramList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<MettlTEEAttendanceReportBean>(MettlTEEAttendanceReportBean.class));
		
		return allProgramList;
	}
	
	@Override
	public List<MettlTEEAttendanceReportBean> getSemAndProgramList(String year, String month) throws Exception 
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    sapid, " + 
				"    subject, " + 
				"    examDate, " + 
				"    examTime, " + 
				"    sem, " + 
				"    program AS programCode " + 
				"FROM " + 
				"    `exam`.`exambookings` " + 
				"WHERE " + 
				"    booked = 'Y' AND centerId <> '-1' " + 
				"        AND year = ? " + 
				"        AND month = ? ";
		
		List<MettlTEEAttendanceReportBean> semAndProgList = jdbcTemplate.query(sql, new Object[] {year,month}, new BeanPropertyRowMapper<MettlTEEAttendanceReportBean>(MettlTEEAttendanceReportBean.class));
		
		return semAndProgList;
	}
}
