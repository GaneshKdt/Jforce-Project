package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.StudentStudentPortalBean;

@Repository
public class EarnedBadgesNotificationDaoImpl implements EarnedBadgesNotificationDao {

	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional(readOnly = false)
	public void updateEarnedBadgesNotificationStatus(String status, Long issuedId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE open_badges.badge_issued  " + 
				"SET  " + 
				"    isNotificationSent = ? " + 
				"WHERE " + 
				"    issuedId = ?";
		jdbcTemplate.update(sql, new Object[] {status, issuedId});
	}
	
	@Override
	@Transactional(readOnly = true)
	public OpenBadgeBean getBadgeDetailsByBadgeId(Integer badgeId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    badgeName, attachment " + 
				"FROM " + 
				"    open_badges.badge " + 
				"WHERE " + 
				"    badgeId = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[] {badgeId}, new BeanPropertyRowMapper<>(OpenBadgeBean.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getCriteriaTypeByAwardedAt(String awardedAt) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    bc.criteriatype " + 
				"FROM " + 
				"    open_badges.badge_criteria bc " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bc.badgeId = bi.badgeId " + 
				"WHERE " + 
				"    bi.awardedAt = ? " + 
				"LIMIT 1 ";
		return jdbcTemplate.queryForObject(sql, new Object[] {awardedAt}, Integer.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getSemesterBySapIdAndAwardedAt(String sapId, String awardedAt) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    sem " + 
				"FROM " + 
				"    exam.passfail " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND subject = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapId, awardedAt}, Integer.class);
	}

	@Override
	@Transactional(readOnly = true)
	public StudentStudentPortalBean getRegistrationBySapIdAndSemester(String sapId, Integer semester) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  sem, program " + 
				"FROM " + 
				"    exam.registration " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND sem = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[] {sapId, semester}, new BeanPropertyRowMapper<StudentStudentPortalBean>(StudentStudentPortalBean.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getSemesterByBadgeIdAndSapId(Integer badgeId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    criteriaValue AS sem " + 
				"FROM " + 
				"    open_badges.badge_criteria_param " + 
				"WHERE " + 
				"    criteriaId = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[] {badgeId}, Integer.class);
	}
		
}
