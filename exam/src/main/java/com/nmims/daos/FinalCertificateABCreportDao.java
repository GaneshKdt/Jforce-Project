package com.nmims.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.FinalCertificateABCreportBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;

@Repository
public class FinalCertificateABCreportDao extends BaseDAO{
	@Override
	public void setBaseDataSource() {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	@Autowired
	private DataSource dataSource;
	
//	
//	public List<FinalCertificateABCreportBean> getServiceRequestDataByExamCycle(String examYear ,String examMonth){
//		String sql = " 	SELECT     " + 
//				"   distinct (sr.sapid), certificateNumber    " + 
//				"FROM    " + 
//				"    portal.service_request sr    " + 
//				"        JOIN    " + 
//				"    exam.passfail pf ON sr.sapid = pf.sapid    " + 
//				"WHERE    " + 
//				"    sr.serviceRequestType = 'Issuance of Final Certificate'    " + 
//				"        AND sr.certificateNumber IS NOT NULL    " + 
//				"        AND resultProcessedYear = ?    " + 
//				"        AND resultProcessedMonth = ? ;    " ;
//		
//		return (List<FinalCertificateABCreportBean>)jdbcTemplate.query(sql, new Object[] {examYear,examMonth},new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class));
//	};
//	

	
	public List<FinalCertificateABCreportBean> getServiceRequestData(List<String> sapidList){
		String sql =  "        SELECT    " + 
				"    sapid, certificateNumber   " + 
				"FROM   " + 
				"    portal.service_request   " + 
				"WHERE   " + 
				"    serviceRequestType = 'Issuance of Final Certificate'   " + 
				//TBD with shiv and pranit 
//				"        AND certificateNumber IS NOT NULL   " + 
				"        AND sapid IN (:sapids) " +
				"        AND requestStatus = 'Closed' ";
		return (List<FinalCertificateABCreportBean>)namedParameterJdbcTemplate.query(sql,  new MapSqlParameterSource().addValue("sapids",sapidList),new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class));
	}
	
	
	public List<String> getSapidForABCFinalCertificateOnExamCycle(String examMonth ,String examYear) throws Exception{
		List<String> sapid = new ArrayList<String>();
		String sql = "SELECT distinct sapid FROM exam.passfail";
		
		if ((!StringUtils.isBlank(examMonth) && !StringUtils.isBlank(examYear))) {

				sql=sql + " where resultProcessedMonth= ? and resultProcessedYear = ? ";
			
				sapid= (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{examMonth,examYear},new SingleColumnRowMapper<>(String.class));
		}
		else if ((StringUtils.isBlank(examMonth) && StringUtils.isBlank(examYear))) {

			sapid= (ArrayList<String>)jdbcTemplate.query(sql ,new SingleColumnRowMapper<>(String.class));
	}
		else {
			throw new Exception("Improper Input Year Or Month Input ");
		}
		
		return sapid;
	}
	
	
	public Map<String, StudentExamBean>getStudentData(List<String> sapid){
		String sql =
				"SELECT     " + 
				"	program ,    " + 
				"    enrollmentMonth,    " + 
				"    enrollmentYear,    " + 
				"    sapid,    " + 
				"    concat(firstName,\" \",lastName) as 'firstName',    " + 
				"    gender,    " + 
				"    dob,    " + 
				"    mobile,    " + 
				"    emailId,    " + 
				"    fatherName,    " + 
				"    motherName    " + 
				"FROM    " + 
				"    exam.students    " + 
				"WHERE sapid in(:sapid) ";
		MapSqlParameterSource query = new MapSqlParameterSource();
		query.addValue("sapid", sapid);
		List<StudentExamBean> studentData= (List<StudentExamBean>)namedParameterJdbcTemplate.query(sql, query,new BeanPropertyRowMapper<>(StudentExamBean.class));
		Map<String, StudentExamBean> studentMap =  new HashMap<String,StudentExamBean>();
		for(StudentExamBean bean : studentData) {
			studentMap.put(bean.getSapid(),bean);
		}
		return studentMap;
	};
	
//	public FinalCertificateABCreportBean getStudentLastYearMonthAndResultDeclareDate(String sapid){
//		String sql = "SELECT     " + 
//				"    eo.declareDate,writtenmonth,  writtenyear    " + 
//				"FROM    " + 
//				"    exam.passfail p,    " + 
//				"    exam.examorder eo    " + 
//				"WHERE    " + 
//				"    p.writtenmonth = eo.month    " + 
//				"        AND p.writtenyear = eo.year    " + 
//				"        AND sapid = ?    " + 
//				"ORDER BY eo.order DESC    " + 
//				"LIMIT 1    ";
//		
//		return (FinalCertificateABCreportBean)jdbcTemplate.query(sql, new Object[] {sapid}, new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class)); 
//	}
	
	public FinalCertificateABCreportBean getPassingYearAndPassingMonth(String sapid ) {
		String sql = "      Select resultProcessedMonth as 'passingMonth',  resultProcessedYear as 'passingYear' from exam.passfail " + 
				"		where sapid = ? " + 
				"		ORDER BY resultProcessedYear DESC" + 
				"		LIMIT 1";

		return (FinalCertificateABCreportBean)jdbcTemplate.queryForObject(sql, new Object [] {sapid} ,new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class));
	}
	
	
	public Map<String, String> getResultDeclareDateByYearMonthList(Set<String> applicableYearMonthList) {
		List<String> temp = new ArrayList<>(applicableYearMonthList);
		String sql = "SELECT concat(month,year) as 'monthyear' , declareDate from " + 
				"exam.examorder where concat(month,year) in (:temp) ";
		
		List<FinalCertificateABCreportBean>monthYearList=  (List<FinalCertificateABCreportBean>)namedParameterJdbcTemplate.query(sql,  new MapSqlParameterSource().addValue("temp",applicableYearMonthList),new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class));
		Map<String, String> monthYearMap = new HashMap<String, String>();
		for(FinalCertificateABCreportBean bean : monthYearList) {
			monthYearMap.put(bean.getMonthyear(), bean.getDeclareDate());
		}
		return monthYearMap;
		
	}
	
	
	
	public List<FinalCertificateABCreportBean> getAllTimeReportStudentData() {
		String sql = " SELECT       " + 
				"    sapid,certificateNumber      " + 
				"FROM      " + 
				"    portal.service_request sr      " + 
				"WHERE      " + 
				"    sr.serviceRequestType = 'Issuance of Final Certificate'      " + 
//				"        AND sr.certificateNumber IS NOT NULL ";+
				"        AND requestStatus = 'Closed' ";
		
		return (List<FinalCertificateABCreportBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FinalCertificateABCreportBean.class));
	}


	public Map<String,List<PassFailExamBean>> getPassfailListByYearMonth(String examMonth, String examYear) throws Exception {
		List<PassFailExamBean> sapidList = new ArrayList<PassFailExamBean>();
		String sql = "SELECT * FROM exam.passfail ";
		
		if ((!StringUtils.isBlank(examMonth) && !StringUtils.isBlank(examYear))) {

			sql=sql + " where resultProcessedMonth= ? and resultProcessedYear = ? ";
		
			 sapidList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql, new Object[]{examMonth,examYear},new BeanPropertyRowMapper<>(PassFailExamBean.class));
	}
	else if ((StringUtils.isBlank(examMonth) && StringUtils.isBlank(examYear))) {

		sapidList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(PassFailExamBean.class));
}
	else {
		throw new Exception("Improper Input Year Or Month Input ");
	}
		 return sapidList.stream().collect(Collectors.groupingBy(PassFailExamBean::getSapid));
	}
	public List<String> getSapidForABCFinalCertificate(){
		String sql = "SELECT distinct sapid FROM exam.passfail; ";
		 List<String> sapid = (ArrayList<String>)jdbcTemplate.query(sql,new SingleColumnRowMapper<>(String.class));
		return sapid;
	}

	
}