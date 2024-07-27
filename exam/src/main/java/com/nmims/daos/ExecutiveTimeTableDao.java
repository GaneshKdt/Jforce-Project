package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FileBean;
import com.nmims.beans.ExecutiveTimetableBean;
import com.nmims.beans.StudentExamBean;


public class ExecutiveTimeTableDao extends BaseDAO{
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateSASTimeTable(final List<ExecutiveTimetableBean> timeTableList,FileBean fileBean) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();

		for (i = 0; i < timeTableList.size(); i++) {
			try{
				ExecutiveTimetableBean bean = timeTableList.get(i);
				upsertSASTimeTable(bean, jdbcTemplate,fileBean);
			}catch(Exception e){
				
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}
	private void upsertSASTimeTable(ExecutiveTimetableBean bean, JdbcTemplate jdbcTemplate,FileBean fileBean) {
		String sql = "";
	
			sql = "INSERT INTO exam.sas_timetable (enrollmentYear,enrollmentMonth,examYear, examMonth, PrgmStructApplicable, "
					+ " date, startTime, endTime, createdBy, lastModifiedBy, createdDate, lastModifiedDate) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,sysdate(),sysdate())"
					+ " on duplicate key update "
					+ "		enrollmentYear=?,enrollmentMonth=?,"
					+ "	    examYear = ?,"
					+ "	    examMonth = ?,"
					+ "	    PrgmStructApplicable = ?,"
					+ "	    date = ?,"
					+ "	    startTime = ?,"
					+ "	    endTime = ?,"
					+ "	    lastModifiedBy = ?,"
					+ "	    lastModifiedDate = sysdate()";
			
			jdbcTemplate.update(sql, new Object[] { 
					fileBean.getEnrollmentYear(),
					fileBean.getEnrollmentMonth(),
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getCreatedBy(),
					bean.getLastModifiedBy(),

					fileBean.getEnrollmentYear(),
					fileBean.getEnrollmentMonth(),
					bean.getExamYear(),
					bean.getExamMonth(),
					bean.getPrgmStructApplicable(),
					bean.getDate(),
					bean.getStartTime(),
					bean.getEndTime(),
					bean.getLastModifiedBy(),
			});
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public ArrayList<ExecutiveTimetableBean> getMostRecentExecutiveTimetable(StudentExamBean student){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="select et.* ,les.program,les.subject,les.prgmStructApplicable,les.acadYear,les.acadMonth,s.enrollmentYear as hidden, s.enrollmentMonth as hidden2 "
				+ " from exam.sas_timetable et, exam.live_exam_subjects les , exam.executive_examorder eo , exam.students s"
				+ " where eo.year = et.examYear and eo.month = et.examMonth "
				+ " and  et.examYear = les.examYear and et.examMonth = les.examMonth"
				+ " and s.enrollmentYear = eo.acadYear and s.enrollmentMonth = eo.acadMonth  "
				+ " and s.enrollmentYear = les.acadYear and s.enrollmentMonth = les.acadMonth "
				+ " and s.enrollmentYear = et.enrollmentYear and s.enrollmentMonth = et.enrollmentMonth  " //added new by PS 8/10/18 for hiding timetable from students of diff batch of same program . 
				+ " and s.sapid = ?"
				+ " and les.program = ? "
				+ " and eo.order = (select max(e.order) from exam.executive_examorder e where e.timeTableLive = 'Y'"
				+ "					 and s.enrollmentYear = e.acadYear and s.enrollmentMonth = e.acadMonth ) "
				+ " order by et.date,et.startTime";
		
		ArrayList<ExecutiveTimetableBean> timetableList = (ArrayList<ExecutiveTimetableBean>)jdbcTemplate.query(sql,new Object[]{student.getSapid(),student.getProgram()},new BeanPropertyRowMapper(ExecutiveTimetableBean.class));
		return timetableList;
	}

}
