package com.nmims.daos;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.nmims.beans.ProgramBean;

@Component
public class ProgramDao {
	
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired 
	public void setDataSource(DataSource dataSource) {
		System.out.println("Setting Data Source " + dataSource);
		this.dataSource = dataSource;
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		System.out.println("jdbcTemplate = " + jdbcTemplate);
	}
	
	public HashMap<String,String> mapOfProgramNameAndAbbr(){
		//updated as program list to be taken from program table
		//String sql = " select * from exam.programs ";
		String sql = " select * from exam.program ";
		HashMap<String,String> mapOfProgramNameAndAbbr = new HashMap<String,String>();
		List<ProgramBean> listOfAllPrograms = jdbcTemplate.query(sql,new BeanPropertyRowMapper(ProgramBean.class));
		for(ProgramBean prog : listOfAllPrograms){
			//mapOfProgramNameAndAbbr.put(prog.getProgramname(), prog.getProgram());
			mapOfProgramNameAndAbbr.put(prog.getName(), prog.getCode());
		}
		return mapOfProgramNameAndAbbr;
	}
}
