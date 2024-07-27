package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.util.ObjectIdMap;
import com.nmims.dto.CustomCourseWaiverDTO;
import com.nmims.repository.CustomCourseWaiverRepository;

@Repository
public class CustomCourseWaiverDAO  implements CustomCourseWaiverRepository{

	

	@Autowired
	DataSource dataSource;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate nameParamerterJdbcTemplate;
	
	
	@Override
	public List<Integer> getWaivedInPss(String sapid,int sem) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mdm_pss_id FROM exam.custom_waived_in_subjects where sapid = ?  and sem = ?" ; 
		
		return jdbcTemplate.queryForList(sql,new Object[] {sapid,sem},Integer.class);
	}
	
	@Override
	public List<Integer> getWaivedOffPss(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mdm_pss_id FROM exam.waivedoff_subject where sapid = ?" ; 
		
		return jdbcTemplate.queryForList(sql,new Object[] {sapid},Integer.class);
	}
	

	@Override
	public List<CustomCourseWaiverDTO> getSubjectCodeId(List<Integer> waivedInPssId) {
		String sql = " SELECT subjectCodeId ,id as pssId,sem FROM exam.mdm_subjectcode_mapping msm "
				+ " WHERE msm.id IN (:waivedInPssId) ";

		nameParamerterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("waivedInPssId", waivedInPssId);

		
		List<CustomCourseWaiverDTO> subject = nameParamerterJdbcTemplate.query(sql, paramSource,
				new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		return subject;

	}



	@Override
	public List<CustomCourseWaiverDTO> getSubjectName(Set<Integer> subjectCodeIds) {
		String sql = " SELECT id as subjectCodeId,subjectName FROM exam.mdm_subjectcode ms where id in (:subjectCodeIds) ";
		
		nameParamerterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
	
		paramSource.addValue("subjectCodeIds", subjectCodeIds);
	
		return nameParamerterJdbcTemplate.query(sql, paramSource, new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		 
}

	@Override
	public int getStudentCurrentSem(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select max(sem) from exam.registration where sapid = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
	}

	@Override
	public int checkSapidExist(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select exists(select * from exam.custom_waived_in_subjects where sapid = ?) as count";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid}, Integer.class);
	}

	@Transactional(readOnly = true)
	public int getStudentMasterKey(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT consumerProgramStructureId from exam.students where sapid = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[] { sapid }, Integer.class);

	}
	
	@Override
	public List<CustomCourseWaiverDTO> getSubjectCodeIdByMasterKey(int masterKey) {
		String sql = " SELECT id as pssId,subjectCodeId,sem FROM exam.mdm_subjectcode_mapping msm "
				+ " WHERE consumerProgramStructureId =?";
		jdbcTemplate = new JdbcTemplate(dataSource);

		return jdbcTemplate.query(sql, new Object[] {masterKey},new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		 

	}

	@Override
	public List<CustomCourseWaiverDTO> getApplicableSubject(List<Integer> subjectCodeId) {
		String sql = " SELECT subjectCodeId,subjectname  FROM exam.mdm_subjectcode msm "
				+ " WHERE msm.id IN (:subjectCodeId) ";

		nameParamerterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		paramSource.addValue("subjectCodeId", subjectCodeId);

		
		List<CustomCourseWaiverDTO> subject = nameParamerterJdbcTemplate.query(sql, paramSource,new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		return subject;
	}

	@Override
	public List<Integer> getTotalNumberOfSem(int masterKey) {
		String sql = "  SELECT sem FROM exam.mdm_subjectcode_mapping WHERE consumerProgramStructureId =? group by sem";
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		return jdbcTemplate.queryForList(sql, new Object[] {masterKey},Integer.class);
		 
	}

	@Override
	public int saveWaivedInSubject(int pssId, int sem, Long sapid, String loggedUser) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.custom_waived_in_subjects(mdm_pss_id,sem,sapid,createdBy,createdDate,lastModifiedBy,lastModifiedDate)"
				+ "Values(?,?,?,?,sysdate(),?,sysdate())"
				+ "ON DUPLICATE KEY UPDATE "
				+ " sem = ?";
		
		return jdbcTemplate.update(sql,new Object[] {pssId,sem,sapid,loggedUser,loggedUser,sem});
	}

	@Override
	public int saveWaivedOffSubject(int pssId, Long sapid, String loggedUser) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.waivedoff_subject(mdm_pss_id,sapid,createdBy,createdDate,lastModifiedBy,lastModifiedDate)"
				+ "Values(?,?,?,sysdate(),?,sysdate())" ;
		
		return jdbcTemplate.update(sql,new Object[] {pssId,sapid,loggedUser,loggedUser});
	}

	@Override
	public List<CustomCourseWaiverDTO> getWaivedInPss(Long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT mdm_pss_id as pssId,sem FROM exam.custom_waived_in_subjects where sapid = ?" ; 
		
		return jdbcTemplate.query(sql,new Object[] {sapid},new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
	}

	@Override
	@Transactional(readOnly = false)
	public int deleteFromWaivedIN(long sapid, int pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "delete from exam.custom_waived_in_subjects where sapid = ? and mdm_pss_id = ?";
		return jdbcTemplate.update(sql,new Object[] {sapid,pssId});
	}

	@Override
	@Transactional(readOnly = false)
	public int deleteFromWaivedOff(long sapid, int pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	
		String sql = "delete from exam.waivedoff_subject where sapid = ? and mdm_pss_id = ?";
		return jdbcTemplate.update(sql,new Object[] {sapid,pssId});
	}

	@Override
	public List<CustomCourseWaiverDTO> getAllSubjectCodeId() {
		String sql = " SELECT id as pssId,subjectCodeId FROM exam.mdm_subjectcode_mapping msm ";
		jdbcTemplate = new JdbcTemplate(dataSource);

		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
		 
	}

	@Override
	public int upsertWaivedIn(List<CustomCourseWaiverDTO> waivedIn,String loggedUser) {
		int insertCount[];
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.custom_waived_in_subjects(mdm_pss_id,sem,sapid,createdBy,createdDate,lastModifiedBy,lastModifiedDate)"
				+ "Values(?,?,?,?,sysdate(),?,sysdate())"
				+ "ON DUPLICATE KEY UPDATE "
				+ " sem = ?,"
				+ "createdBy =?,"
				+ "createdDate=sysDate(),"
				+ "lastModifiedBy =?,"
				+ "lastModifiedDate =sysDate()" ;
		
		insertCount = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CustomCourseWaiverDTO bean = waivedIn.get(i);
				ps.setInt(1, bean.getPssId());
				ps.setInt(2, bean.getSem());
				ps.setLong(3, bean.getSapid());
				ps.setString(4, loggedUser);
				ps.setString(5, loggedUser);
				ps.setInt(6, bean.getSem());
				ps.setString(7, loggedUser);
				ps.setString(8, loggedUser);
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return waivedIn.size();
			}
		});
		return insertCount.length;
	}

	@Override
	public int upsertWaivedOff(List<CustomCourseWaiverDTO> waivedOff, String loggedUser) {
		int insertCount[];
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.waivedoff_subject(mdm_pss_id,sapid,createdBy,createdDate,lastModifiedBy,lastModifiedDate)"
				+ "Values(?,?,?,sysdate(),?,sysdate())"
				+ "ON DUPLICATE KEY UPDATE "
				+ "createdBy =?,"
				+ "createdDate=sysDate(),"
				+ "lastModifiedBy =?,"
				+ "lastModifiedDate =sysDate()" ;
				
		
		insertCount = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CustomCourseWaiverDTO bean = waivedOff.get(i);
				ps.setInt(1, bean.getPssId());
				ps.setLong(2, bean.getSapid());
				ps.setString(3, loggedUser);
				ps.setString(4, loggedUser);
				ps.setString(5, loggedUser);
				ps.setString(6, loggedUser);
		
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return waivedOff.size();
			}
		});
		return insertCount.length;
			
	}

	@Override
	public int upsertInDelhivery(List<CustomCourseWaiverDTO> waivedIn, String loggedUser) {
		int insertCount[];
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.student_course_mapping(program_sem_subject_id,userId,acadYear,acadMonth,role,createdBy,createdDate,lastModifiedBy,lastModifiedDate)"
				+ "Values(?,?,?,?,?,?,sysdate(),?,sysdate())"
				+ "ON DUPLICATE KEY UPDATE "
				+ "createdBy =?,"
				+ "createdDate=sysDate(),"
				+ "lastModifiedBy =?,"
				+ "lastModifiedDate =sysDate()" ;
				
		
		insertCount = jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CustomCourseWaiverDTO bean = waivedIn.get(i);
				ps.setInt(1, bean.getPssId());
				ps.setLong(2, bean.getSapid());
				ps.setString(3, bean.getYear());
				ps.setString(4,bean.getMonth());
				ps.setString(5, "Student");
				ps.setString(6, loggedUser);
				ps.setString(7, loggedUser);
				ps.setString(8, loggedUser);
				ps.setString(9, loggedUser);
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return waivedIn.size();
			}
		});
		
		return insertCount.length;
		}

	@Override
	public int deleteStudentCurrentSubject(long sapid, int pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "delete from exam.student_current_subject where sapid = ? and programSemSubjectId = ?";
		return jdbcTemplate.update(sql,new Object[] {sapid,pssId});

	}

	@Override
	public void upsertInStudentCourseMapping(CustomCourseWaiverDTO customCourseWaiverDTO, String loggerUser) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
	    String sql = "INSERT INTO exam.student_course_mapping (program_sem_subject_id, userId, acadYear, acadMonth, role, " +
	            "createdBy, createdDate, lastModifiedBy, lastModifiedDate) " +
	            "VALUES (?, ?, ?, ?, ?, ?, sysdate(), ?, sysdate())";
	    
	    System.out.println("customCourseWaiverDTO" + customCourseWaiverDTO);
	    int insertCount = jdbcTemplate.update(sql, new Object[] {
	            customCourseWaiverDTO.getPssId(),
	            customCourseWaiverDTO.getSapid(),
	            customCourseWaiverDTO.getYear(),
	            customCourseWaiverDTO.getMonth(),
	            "Student",
	            loggerUser,
	            loggerUser
	    });
	    
	}

	@Override
	public CustomCourseWaiverDTO getAcadMonthAndYear(long sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql ="select month,year from exam.registration where  sapid = ? "
				+ "and sem = (select max(sem) from exam.registration where sapid =?) ";
		
		
		return jdbcTemplate.queryForObject(sql, new Object[] {sapid,sapid}, new BeanPropertyRowMapper<>(CustomCourseWaiverDTO.class));
	}
	
	@Override
	public int deleteFromStudentCourseMapping(long sapid, int pssId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		System.out.println(sapid+"sapid"+pssId);
		String sql = "delete from exam.student_course_mapping where userId = ? and program_sem_subject_id = ?";
		return jdbcTemplate.update(sql,new Object[] {sapid,pssId});

	}

	@Override
	public int deleteFromDelhivery(List<CustomCourseWaiverDTO> waivedOff) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int[] deletCount = null;
		String sql = "delete from exam.student_course_mapping where userId = ? and program_sem_subject_id = ?";
		deletCount =  jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CustomCourseWaiverDTO bean = waivedOff.get(i);
				ps.setLong(1, bean.getSapid());
				ps.setInt(2, bean.getPssId());
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return waivedOff.size();
			}
		});
		return deletCount.length;
	}
	
}

