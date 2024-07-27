package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nmims.beans.ProgramsBean;

@Repository("exitSrMdmDao")
public class ExitSrMdmDao extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;		
	}
	
	@Transactional(readOnly = false)
	  public String getMasterkey(String programname, String programStructure, String consumerType) {	
			String sql = "SELECT id FROM exam.consumer_program_structure where programId=? and programStructureId=? and consumerTypeId=?;";
			jdbcTemplate = new JdbcTemplate(dataSource);
			String masterkey=(String)jdbcTemplate.queryForObject(sql,new Object[] {programname,programStructure,consumerType}, new SingleColumnRowMapper(String.class));
			return masterkey;
		}
		
		@Transactional(readOnly = false)
		public boolean  insertCertificateByMasterkey(String consumerProgramStructureId, String sem,String newMasterkey,String userId) throws SQLException {
				// Creating StringBuilder object
			try {
			String sql= " INSERT INTO exam.exit_program_certificate_mapping "
					  + " (consumerProgramStructureId,sem,newConsumerProgramStructureId,createdBy,createdDate,lastModifiedBy,lastModifiedDate) "
					  + " VALUES (?,?,?,?,sysdate(),?,sysdate()) ";
			
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, consumerProgramStructureId);
					ps.setString(2, sem);
					ps.setString(3, newMasterkey);
					ps.setString(4, userId);
					ps.setString(5, userId);
				}
			});
			
			return true;
			}catch(Exception e) {
				return false;
			}
		}
		
		@Transactional(readOnly = false)
		  public int getTotalSemByMasterKey(String masterkey) {
			 String sql = "select noOfSemesters from exam.programs where consumerProgramStructureId=?;";
			 jdbcTemplate = new JdbcTemplate(dataSource);
			 int sem=(Integer)jdbcTemplate.queryForObject(sql,new Object[] {masterkey}, new SingleColumnRowMapper(Integer.class));
			 return sem;	
			}
	
		
		@Transactional(readOnly=false)
		public void updateSemCertificateExitprogram(String sem,String newMasterKey,String id,String userId) {
			String sql = "update  exam.exit_program_certificate_mapping set sem=?,newConsumerProgramStructureId=?,lastModifiedBy=?,lastModifiedDate=sysdate() where id=?";
	         jdbcTemplate = new JdbcTemplate(dataSource);
			 jdbcTemplate.update(sql, new Object[] { sem,newMasterKey,userId,id});	
		}
		
		 public String getProgramafetrExitId(String programCode) {				
				String sql = "select id from exam.program where  code=?";
				jdbcTemplate = new JdbcTemplate(dataSource);
				String id=(String)jdbcTemplate.queryForObject(sql,new Object[] {programCode}, new SingleColumnRowMapper(String.class));
				return id;				
			}
		
		 @Transactional(readOnly=false)
			public void deleteSemCertificateExitprogram(String id) {				
				String sql = "DELETE FROM exam.exit_program_certificate_mapping WHERE id=?";
		         jdbcTemplate = new JdbcTemplate(dataSource);
				 jdbcTemplate.update(sql, new Object[] {id});		
			}
		 
		 @Transactional(readOnly=false)
		 public Integer getAlreadyEntryCheck(String masterkey,String sem) {		
				String sql = "SELECT count(*) FROM exam.exit_program_certificate_mapping where consumerProgramStructureId=? and sem=?";
				jdbcTemplate = new JdbcTemplate(dataSource);
				int entry=(Integer)jdbcTemplate.queryForObject(sql,new Object[] {masterkey,sem}, new SingleColumnRowMapper(Integer.class));
				return entry;
				
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getMappedNewMasterKey (){

				jdbcTemplate = new JdbcTemplate(dataSource);
				ArrayList<ProgramsBean> newmasterkeyList = new ArrayList<ProgramsBean>();
				String sql =" SELECT DISTINCT cps.id as id, p.code AS program, ps.program_structure AS programStructure,   ct.name AS consumerType  " + 
							"	FROM  " + 
							" exam.consumer_program_structure cps   " + 
							" 	INNER JOIN   " + 
							" exam.program p ON cps.programId = p.id   " + 
							"	INNER JOIN  " + 
							" exam.program_structure ps ON ps.id = cps.programStructureId   " + 
							"	INNER JOIN   " + 
							" exam.consumer_type ct ON ct.id = cps.consumerTypeId  " + 
							"					        " + 
							"					 ORDER BY id";			
					newmasterkeyList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql,
							new Object[] {},
							new BeanPropertyRowMapper(ProgramsBean.class));
				   return newmasterkeyList;
			}

		 @Transactional(readOnly=false)
			public ArrayList<String> getlistofConsumerProgramStructureIdbySem(String sem){				
				String sql="select consumerProgramStructureId from exam.programs where noOfSemesters=? ";			
				ArrayList<String> getlistofConsumerProgramStructureId = (ArrayList<String>) jdbcTemplate.query(sql,new Object[] {sem}, new SingleColumnRowMapper(String.class));
				return getlistofConsumerProgramStructureId;			
			}

		 
		 @Transactional(readOnly=false)
			public ArrayList<ProgramsBean> getListOfSrExitData(){				
				String sql="select * from exam.exit_program_certificate_mapping";				
				ArrayList<ProgramsBean> listOfSrExitData = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql,new Object[] {}, new BeanPropertyRowMapper(ProgramsBean.class));
				return listOfSrExitData;
				
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getMasterKeysList(){
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "Select * from exam.consumer_program_structure";
				ArrayList<ProgramsBean> masterkeysList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsBean.class));
				return masterkeysList;
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getListofconsumerType(){
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "Select * from exam.consumer_program_structure";
				ArrayList<ProgramsBean> masterkeysList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsBean.class));
				return masterkeysList;
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getListofProgramcode(){
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "Select * from exam.consumer_program_structure";
				ArrayList<ProgramsBean> masterkeysList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsBean.class));
				return masterkeysList;
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getListofProgramStructure(){
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "Select * from exam.consumer_program_structure";
				ArrayList<ProgramsBean> masterkeysList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsBean.class));
				return masterkeysList;
			}
		 
		 @Transactional(readOnly = true)
			public ArrayList<ProgramsBean> getListOfMappedProgramData(){
				jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "select * from exam.exit_program_certificate_mapping";
				ArrayList<ProgramsBean> masterkeysList = (ArrayList<ProgramsBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramsBean.class));
				return masterkeysList;
			}
}
