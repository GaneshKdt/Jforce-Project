package com.nmims.daos;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;

@Repository
public class RemovalOfFacultyFromRevaluationDaoImpl implements RemovalOfFacultyFromRevaluationDao {
	
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Transactional(readOnly = false)
	public int removalOfactultyFromAssignmentSubmissionStageTwo(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		MapSqlParameterSource query = new MapSqlParameterSource();
		
		String sql = "UPDATE `exam`.`assignmentsubmission`   " + 
				"SET   " + 
				"    `faculty2` = '',  " + 
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE  " + 
				"    `faculty2` = :facultyId  " +  
				"        AND `year` = :year  " + 
				"        AND `month` = :month  " ;
		
		query.addValue("facultyId", facultyid);
		
		query.addValue("year", examYear);
		
		query.addValue("month", examMonth);
		query.addValue("userId", userId);
		if (!StringUtils.isEmpty(subject)) {
			sql = sql + " AND `subject` = :subject";
			
			query.addValue("subject", subject);
		}
	
		return namedParameterJdbcTemplate.update(sql, query);
	}

	@Transactional(readOnly = false)
	public int removalOfactultyFromAssignmentSubmissionStageThree(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		
		MapSqlParameterSource query = new MapSqlParameterSource();
		
		
		String sql ="UPDATE `exam`.`assignmentsubmission`    " + 
				"SET    " + 
				"    `faculty3` = '',   " +
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE   " + 
				"    `faculty3` = :facultyId  " + 
				"        AND `year` = :year   " + 
				"        AND `month` = :month " ;
		query.addValue("facultyId", facultyid);
		
		query.addValue("year", examYear);
		
		query.addValue("month", examMonth);
		query.addValue("userId", userId);

		if (!StringUtils.isEmpty(subject)) {
			
			sql = sql + "  AND `subject`= :subject  ";
			query.addValue("subject", subject);
		}
		

		
		return namedParameterJdbcTemplate.update(sql, query);

	}

	@Transactional(readOnly = false)
	public int removalOfactultyFromAssignmentSubmissionStageFour(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		
		MapSqlParameterSource query = new MapSqlParameterSource();
		
		String sql = "UPDATE `exam`.`assignmentsubmission`   " + 
				"SET   " + 
				"    `facultyIdRevaluation` = '',  " + 
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE  " + 
				"    `facultyIdRevaluation` = :facultyId" + 
				"        AND `year` = :year " + 
				"        AND `month` = :month " ;
		
		query.addValue("facultyId", facultyid);
		
		query.addValue("year", examYear);
		query.addValue("userId", userId);
		
		query.addValue("month", examMonth);

		if (!StringUtils.isEmpty(subject)) {
			
			sql = sql + "  AND `subject` = :subject  ";
			
			query.addValue("subject", subject);
		}
		return namedParameterJdbcTemplate.update(sql, query);

	}

	@Transactional(readOnly = true)
	public ArrayList<AssignmentFileBean> SearchFacultyFromAllStages(String examYear, String examMonth, String subject,
			String facultyId) {
		ArrayList<AssignmentFileBean> facultyListFromAllStages = new ArrayList<AssignmentFileBean>();
		
			MapSqlParameterSource query = new MapSqlParameterSource();
			
			String sql= "SELECT   " + 
					"    `sapid`,  " + 
					"    `subject`,  " + 
					"    `year`,  " + 
					"    `month`,  " + 
					"    `facultyId`,  " + 
					"    `faculty2`,  " + 
					"    `faculty3`,  " + 
					"    `facultyIdRevaluation`  " + 
					"FROM  " + 
					"    `exam`.`quick_assignmentsubmission`  " + 
					"WHERE  " + 
					"    :facultyId   IN ( `faculty2`,  " + 
					"        `faculty3`,  " + 
					"        `facultyIdRevaluation`)  "; 
			
			
					query.addValue("facultyId", facultyId);
					
					if (!StringUtils.isEmpty(examYear)) {
						
						
					sql = sql + " AND `year` = :year  " ;
					
					query.addValue("year", examYear);
					
					}
					
					if (!StringUtils.isEmpty(examYear)) {
						
						sql = sql + " AND `month` = :month  " ;
						
						query.addValue("month", examMonth);
						
						}
					
					if (!StringUtils.isEmpty(subject)) {
						
						sql = sql + "    AND `subject` = :subject  " ; 
						
						query.addValue("subject",subject);
						
						}
					
					facultyListFromAllStages=(ArrayList<AssignmentFileBean>) namedParameterJdbcTemplate.query(sql, query, new BeanPropertyRowMapper<>(AssignmentFileBean.class));
				
		
		return facultyListFromAllStages;
	}

	@Transactional(readOnly = false)
	public int removalOfactultyFromQAssignmentSubmissionStageTwo(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		
		MapSqlParameterSource query = new MapSqlParameterSource();
		
		
		String sql = "UPDATE `exam`.`quick_assignmentsubmission`   " + 
				"SET   " + 
				"    `faculty2` = ''  ," + 
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE  " + 
				"    `faculty2` = :facultyId  " + 
				"        AND `year` = :year " + 
				"        AND `month` = :month " ;
		
		
		
		query.addValue("facultyId", facultyid);
		query.addValue("userId", userId);
		
		query.addValue("month", examMonth);
		
		query.addValue("year", examYear);
		
		if (!StringUtils.isEmpty(subject)){
			
		sql = sql +"   AND `subject` = :subject ";
		
		query.addValue("subject", subject);
		
		}
		
		return namedParameterJdbcTemplate.update(sql, query);
	}

	@Transactional(readOnly = false)
	public int removalOfactultyFromQAssignmentSubmissionStageThree(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		MapSqlParameterSource query = new MapSqlParameterSource();
				
		String sql = " UPDATE `exam`.`quick_assignmentsubmission`    " + 
				"SET    " + 
				"    `faculty3` = '' ,  " + 
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE   " + 
				"    `faculty3` = :facultyId   " + 
				"        AND `year` = :year" + 
				"        AND `month` = :month " ;
		query.addValue("facultyId", facultyid);
		
		query.addValue("month", examMonth);
		
		query.addValue("year", examYear);
		query.addValue("userId", userId);
		
		if (!StringUtils.isEmpty(subject)) {
			
			sql = sql + "    AND `subject` = :subject   ";
			query.addValue("subject", subject);
		}
		
		
		return namedParameterJdbcTemplate.update(sql, query);
	}

	@Transactional(readOnly = false)
	public int removalOfactultyFromQAssignmentSubmissionStageFour(String examYear,
			String examMonth, String subject, String facultyid,String userId) {
		
		MapSqlParameterSource query = new MapSqlParameterSource();
		
		String sql = " UPDATE `exam`.`quick_assignmentsubmission`   " + 
				"SET   " + 
				"    `facultyIdRevaluation` = '',  " + 
				"     `lastModifiedBy`= :userId,  " + 
				"    `lastModifiedDate` = sysdate()  " +
				"WHERE  " + 
				"    `facultyIdRevaluation` = :facultyId" + 
				"        AND `year` = :year  " + 
				"        AND `month` = :month  " ;
		
		query.addValue("facultyId", facultyid);
		
		query.addValue("month", examMonth);
		
		query.addValue("year", examYear);
		query.addValue("userId", userId);
		
		if (!StringUtils.isEmpty(subject)) {
			
		sql = sql + " AND `subject` = :subject  " ;
		
		query.addValue("subject", subject);
		}
		 
		return namedParameterJdbcTemplate.update(sql, query );
	}
}
