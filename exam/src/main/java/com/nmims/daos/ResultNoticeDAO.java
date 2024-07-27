package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.ResultNotice;


@Component
public class ResultNoticeDAO extends BaseDAO{

	@Autowired
	ApplicationContext appContext;
	@Autowired
	DataSource dataSource;
	//private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	

		
	
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int InsertResultNotice(ResultNotice bean){
		final String sql = "INSERT INTO result_notice " + " ( " + " year, "
				+ " month, " + " program, " + " programStructure, "
				+ " title, " + " description, " + " createdBy, "
				+ " createdDate, " + " lastModifiedBy, " + " lastModifiedDate, "
				+ " type) "
				+ " VALUES " + " ( ?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)";


		jdbcTemplate = new JdbcTemplate(dataSource);

		StringBuilder builder = new StringBuilder();

		
		final ResultNotice a = bean;
		for( int i = 0 ; i < a.getProgramList().size(); i++ ) {
		    builder.append("?,");
		}
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1,a.getYear());
				ps.setString(2, a.getMonth());
				ps.setString(3,a.getProgram());
				ps.setString(4,a.getProgramList().toString());
				ps.setString(5, a.getTitle());
				ps.setString(6, a.getDescription());
				ps.setString(7, a.getCreatedBy());
				ps.setString(8,a.getLastModifiedBy());
				ps.setString(9, a.getType());
				return ps;
			}
			
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(psc, keyHolder);

		int id = keyHolder.getKey().intValue();
		return id;

	}

	
	public ResultNotice getResultNotice(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ResultNotice resultNotice = null;
		try{
			String sql = "SELECT distinct r.title , r.description FROM exam.marks a, exam.examorder b, exam.result_notice r , exam.programs p where  a.month = b.month and a.year = b.year and " 
				 + " b.live = 'Y' and r.program=p.programStructure "
			      +  " and a.sapid = ? order by a.sem  asc   ";

			resultNotice = (ResultNotice)jdbcTemplate.queryForObject(sql, new Object[]{sapId}, new BeanPropertyRowMapper(ResultNotice.class));
		}catch(Exception e){
			
		}
		return resultNotice;
	}

}


