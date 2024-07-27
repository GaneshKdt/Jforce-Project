package com.nmims.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.nmims.beans.StudentBean;

@Component
public class UserDao {
	
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource) {
		System.out.println("Setting Data Source " + dataSource);
		this.dataSource = dataSource;
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		System.out.println("jdbcTemplate = " + jdbcTemplate);
	}
	
	public HashMap<String,StudentBean> mapOfSapIdAndStudentBeanBean(){
		List<StudentBean> listOfStudentBean = jdbcTemplate.query("select * from exam.students",new BeanPropertyRowMapper(StudentBean.class));
		HashMap<String,StudentBean> mapOfSapIdAndStudentBeanRecord = new HashMap<String,StudentBean>();
		for(StudentBean s:listOfStudentBean){
			mapOfSapIdAndStudentBeanRecord.put(s.getSapid(), s);
		}
		System.out.println(" map size "+mapOfSapIdAndStudentBeanRecord.size());
		return mapOfSapIdAndStudentBeanRecord;
	}
	
	public HashMap<String,StudentBean> mapOfRegistrationSapIdAndStudentBeanBean(){
		List<StudentBean> listOfStudentBean = jdbcTemplate.query("select * from exam.registration",new BeanPropertyRowMapper(StudentBean.class));
		HashMap<String,StudentBean> mapOfSapIdAndStudentBeanRecord = new HashMap<String,StudentBean>();
		for(StudentBean s:listOfStudentBean){
			mapOfSapIdAndStudentBeanRecord.put(s.getSapid(), s);
		}
		System.out.println(" map size "+mapOfSapIdAndStudentBeanRecord.size());
		return mapOfSapIdAndStudentBeanRecord;
	}
	
	public void crudServiceFroStudentMaster(String crudType,Map<String, Object> mapOfParameters){
		String sql ="";
		switch(crudType){
		case "INSERT":
			sql = " insert into exam.students (sapid,firstName,lastName,gender,emailId,mobile,createdBy,createdDate,lastModifiedBy,lastModifiedDate, "
					+ " program,centerCode,centerName,city,country,address,validityEndYear,validityEndMonth,dob,isLateral,PrgmStructApplicable) VALUES(:sapid,:firstName,:lastName,:gender,:emailId,:mobile,:createdBy,:createdDate,:lastModifiedBy,:lastModifiedDate, "
					+ " :program,:centerCode,:centerName,:city,:country,:address,:validityEndYear,:validityEndMonth,:dob,:isLateral,:PrgmStructApplicable) ";
			jdbcTemplate.update(sql,mapOfParameters);	  
			break;
		case "UPDATE":
			sql = " UPDATE exam.students "
					+ " SET "
					+ " sapid = :sapid, "
					+ " firstName = :firstName, "
					+ " lastName = :lastName, "
					+ " gender = :gender, "
					+ " emailId = :emailId, "
					+ " mobile = :mobile, "
					+ " createdBy = :createdBy, "
					+ " createdDate = :createdDate, "
					+ " lastModifiedBy = :lastModifiedBy, "
					+ " lastModifiedDate = :lastModifiedDate, "
					+ " program = :program, "
					+ " centerCode = :centerCode, "
					+ " centerName = :centerName, "
					+ " city = :city, "
					+ " country = :country, "
					+ " address = :address, "
					+ " validityEndYear = :validityEndYear, "
					+ " validityEndMonth = :validityEndMonth, "
					+ " dob = :dob, "
					+ " isLateral = :isLateral, "
					+ " PrgmStructApplicable = :PrgmStructApplicable "
					+ " WHERE sapid=:sapid ";
			jdbcTemplate.update(sql,mapOfParameters);
			break;
			
			
		}
	}
	
	
	
	public void crudServiceForRegistration(String crudType,Map<String, Object> mapOfParameters){
		String sql ="";
		switch(crudType){
		case "INSERT":
			sql = " insert into exam.registration (sapid,createdBy,createdDate,lastModifiedBy,lastModifiedDate, "
					+ " program,year,month,sem) VALUES(:sapid,:createdBy,:createdDate,:lastModifiedBy,:lastModifiedDate,:program,:year,:month,:sem) ";
			jdbcTemplate.update(sql,mapOfParameters);
			  
			break;
		case "UPDATE":
			sql = " UPDATE exam.registration "
					+ " SET "
					+ " sapid = :sapid, "
					+ " lastModifiedBy = :lastModifiedBy, "
					+ " lastModifiedDate = :lastModifiedDate, "
					+ " program = :program, "
					+ " year = :year, "
					+ " month = :month, "
					+ " sem = :sem "
					+ " WHERE sapid=:sapid "
					+ " and year=:year "
					+ " and month=:month ";
			jdbcTemplate.update(sql,mapOfParameters);
			break;
			
			
		}
	}
}
