package com.nmims.daos;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.OpenBadgeLectureAttendanceBean;
import com.nmims.beans.OpenBadgesAppearedForTEEBean;
import com.nmims.beans.OpenBadgesCriteriaBean;
import com.nmims.beans.OpenBadgesCriteriaParamBean;
import com.nmims.beans.OpenBadgesEvidenceBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesTopInAssignmentDto;
import com.nmims.beans.OpenBadgesUsersBean;

import com.nmims.dto.OpenBadgesForReRegSemDto;
import com.nmims.dto.OpenBadgesTopInProgramDto;
import com.nmims.dto.OpenBadgesTopInSemesterDto;
import com.nmims.dto.BadgeKeywordsDto;
import com.nmims.dto.OpenBadgesForReRegSemDto;
import com.nmims.dto.OpenBadgesPortalVisitStreakDto;
import com.nmims.dto.OpenBadgesTopInSemesterDto;
import com.nmims.dto.OpenBadgesTopInTEEDto;
import com.nmims.dto.OpenBadgesTopInsubjectDto;


@Repository("openBadgesDAO")
public class OpenBadgesDAO {
	
	@Autowired
	@Qualifier("analyticsDataSource")
	private DataSource analyticsDataSource;
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly = true)
	public Integer getBadgeUserId(String sapId, Integer consumerProgramStructureId) {
		String sql = "SELECT  " + 
				"    userId " + 
				"FROM " + 
				"    open_badges.users " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND consumerProgramStructureId = ? ";
		Integer userId = jdbcTemplate.queryForObject(sql, new Object[] { sapId, consumerProgramStructureId}, Integer.class);
		return userId;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgeBean> getAllBadgesList(Integer consumerProgramStructureId) {
		List<OpenBadgeBean> openBadgeBeanList = new ArrayList<OpenBadgeBean>();
//		String sql = "select badgeId, badgeName from open_badges.badge where status = 1 ";
		String sql = "SELECT " + 
				"    b.badgeId, b.badgeName " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_masterkey_mapping map ON map.badgeId = b.badgeId " + 
				"WHERE " + 
				"    b.status = 1 " + 
				"        AND map.consumerProgramStructureId = ? " ;
		openBadgeBeanList =	jdbcTemplate.query(sql,new Object[] {consumerProgramStructureId} , new BeanPropertyRowMapper<OpenBadgeBean>(OpenBadgeBean.class));
		return openBadgeBeanList;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesCriteriaBean getCriteriaDetailsByBadgeId(Integer badgeId) {
	
		OpenBadgesCriteriaBean openBadgesCriteriaBean = new OpenBadgesCriteriaBean();
		String sql = "SELECT * FROM open_badges.badge_criteria where badgeId = ? ";
		openBadgesCriteriaBean = jdbcTemplate.queryForObject(sql, new Object[] { badgeId }, new BeanPropertyRowMapper<OpenBadgesCriteriaBean>(OpenBadgesCriteriaBean.class));
		return openBadgesCriteriaBean;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesCriteriaParamBean getCriteriaParamDetails(Integer criteriaId) {
				
		String sql = "SELECT criteriaId, criteriaName, criteriaValue FROM open_badges.badge_criteria_param where criteriaId = ? ";
		
		OpenBadgesCriteriaParamBean criteriaDetails =	jdbcTemplate.queryForObject(sql, new Object[]{ criteriaId},  new BeanPropertyRowMapper<OpenBadgesCriteriaParamBean>(OpenBadgesCriteriaParamBean.class));
		
		return criteriaDetails ;
			
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesIssuedBean> getApplicableSubjectList(String sapid) {
		List<OpenBadgesIssuedBean> openBadgesIssuedBeanList = new ArrayList<OpenBadgesIssuedBean>();
		
		String sql = "SELECT  " + 
				"    pss.subject AS awardedAt " + 
				"FROM " + 
				"    exam.program_sem_subject pss " + 
				"        INNER JOIN " + 
				" ( SELECT  " + 
				"    s.consumerProgramStructureId, r.sem " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.registration r ON r.sapid = s.sapid " + 
				"WHERE " + 
				"    s.sapid = ? ) rs "+
				"   ON rs.consumerProgramStructureId = pss.consumerProgramStructureId AND rs.sem = pss.sem " + 
				" WHERE  pss.subject not in ('Assignment','Project') "+
				" ORDER BY pss.sem, pss.subject";
		openBadgesIssuedBeanList =	jdbcTemplate.query(sql, new Object[]{sapid},  new BeanPropertyRowMapper<OpenBadgesIssuedBean>(OpenBadgesIssuedBean.class));
		
		return openBadgesIssuedBeanList;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getIssuedBadgeList(Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = " SELECT " + 
				"    bi.uniquehash, " + 
				"    REPLACE(bi.awardedAt, '&', 'and') as awardedAt, " + 
				"    b.badgeName, " + 
				"    b.badgeId, " + 
				"    b.attachment " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.badgeId = b.badgeId " + 
				"WHERE " + 
				"    bi.isClaimed = 0 " + 
				"        AND bi.userId = ? ";
		list = jdbcTemplate.query(sql, new Object[] { userId}, new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class) );
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getClaimedBadgeList( Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = " SELECT " + 
				"    bi.uniquehash, " + 
				"    bi.awardedAt, " + 
				"    b.badgeName, " + 
				"    b.badgeId, " + 
				"    b.attachment " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.badgeId = b.badgeId " + 
				"WHERE " + 
				"    bi.isClaimed = 1 " + 
				"        AND bi.isRevoked = 0 " + 
				"        AND bi.userId = ? ";
		list = jdbcTemplate.query(sql, new Object[] { userId}, new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class) );
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getRevokedBadgeList( Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = " SELECT " + 
				"    bi.uniquehash, " + 
				"    bi.awardedAt, " + 
				"    b.badgeName, " + 
				"    b.badgeId, " + 
				"    b.attachment " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.badgeId = b.badgeId " + 
				"WHERE " + 
				"    bi.isClaimed = 1 " + 
				"        AND bi.isRevoked = 1 " + 
				"        AND bi.userId = ? ";
		list = jdbcTemplate.query(sql, new Object[] { userId}, new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class) );
		return list;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesUsersBean getLockedBadge(Integer userId, Integer badgeId, String awardedAt ) {
		OpenBadgesUsersBean  bean = new OpenBadgesUsersBean();
		String sql = "   SELECT " + 
				"    b.badgeId, " + 
				"    b.badgeName, " + 
				"    b.attachment, " + 
				"    COUNT(bi.awardedAt) AS isBadgeIssued " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.badgeId = b.badgeId " + 
				"		AND bi.awardedAt = ? " + 
				"    	AND  bi.userId = ? "+
				"WHERE " + 
				"   b.badgeId = ? " ;
		bean = jdbcTemplate.queryForObject(sql, new Object[] {awardedAt, userId, badgeId }, new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class) );
		return bean;
	}
	
	@Transactional(readOnly = true)
	public Integer checkSubjectPass(String sapid, String subject) {
		String sql = "SELECT count(*) FROM exam.passfail WHERE isPass = 'Y' AND sapid = ? AND subject = ? ";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] {sapid, subject}, Integer.class);
		return count;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesIssuedBean getIssuedBadgeDetailsById(String uniquehash, String sapid) {
		OpenBadgesIssuedBean openBadgesIssuedBean = new OpenBadgesIssuedBean();
		String sql = " SELECT  " + 
				"    bi.uniquehash, " + 
				"    bi.awardedAt, " + 
				"    DATE_FORMAT(bi.dateissued, '%b %d, %Y') AS dateissued, " + 
				"    DATE_FORMAT(bi.dateexpire, '%b %d, %Y') AS dateexpire, " + 
				"    bi.isClaimed, " + 
				"    bi.isRevoked, " + 
				"    bi.revocationReason, " + 
				"    b.attachment, " + 
				"    b.badgeName, " + 
				"    b.badgeDescription, " + 
				"    b.issuername, " + 
				"    bc.criteriaDescription, " + 
				"    bc.criteriatype, " + 
				"    bc.criteriaId " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria bc ON bc.badgeId = b.badgeId " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.badgeId = bc.badgeId " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.userId = bi.userId " + 
				"WHERE " + 
				"    bi.uniquehash = ? AND u.sapid = ? " ;
		
		openBadgesIssuedBean =	jdbcTemplate.queryForObject(sql, new Object[]{uniquehash, sapid},  new BeanPropertyRowMapper<OpenBadgesIssuedBean>(OpenBadgesIssuedBean.class));
		return openBadgesIssuedBean ;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesIssuedBean getNotIssuedBadgeDetailsById(Integer badgeId) {
		OpenBadgesIssuedBean openBadgesIssuedBean = new OpenBadgesIssuedBean();
		String sql = " SELECT  " + 
				"    '0' AS isBadgeIssued, " + 
				"    b.attachment, " + 
				"    b.badgeName, " + 
				"    b.badgeDescription, " + 
				"    b.issuername, " + 
				"    bc.criteriaDescription, " + 
				"    bc.criteriatype, " + 
				"    bc.criteriaId " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria bc ON bc.badgeId = b.badgeId " + 
				"WHERE " + 
				"    b.badgeId = ? " ;
		
		openBadgesIssuedBean =	jdbcTemplate.queryForObject(sql, new Object[]{badgeId},  new BeanPropertyRowMapper<OpenBadgesIssuedBean>(OpenBadgesIssuedBean.class));
		return openBadgesIssuedBean ;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesEvidenceBean> getEvidenceByIssuedId(String uniquehash) {
		List<OpenBadgesEvidenceBean> openBadgesEvidenceBeanList = new ArrayList<OpenBadgesEvidenceBean>();
		String sql = " SELECT  " + 
				"    be.evidenceType, " + 
				"    be.evidenceValue " + 
				"FROM " + 
				"    open_badges.badge_issued bi " + 
				"     	INNER JOIN " + 
				"    open_badges.badge_evidence be ON be.issuedId = bi.issuedId " + 
				"WHERE  " + 
				"    uniquehash = ? ";
		
		openBadgesEvidenceBeanList =	jdbcTemplate.query(sql, new Object[]{uniquehash},  new BeanPropertyRowMapper<OpenBadgesEvidenceBean>(OpenBadgesEvidenceBean.class));
		return openBadgesEvidenceBeanList;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesCriteriaParamBean> getCriteriaParamList(Integer criteriaId) {
		List<OpenBadgesCriteriaParamBean> openBadgesCriteriaParamBeanList = new ArrayList<OpenBadgesCriteriaParamBean>();
		String sql = " select criteriaId, criteriaName, criteriaValue from open_badges.badge_criteria_param where criteriaId = ? ";
		openBadgesCriteriaParamBeanList =	jdbcTemplate.query(sql, new Object[]{criteriaId},  new BeanPropertyRowMapper<OpenBadgesCriteriaParamBean>(OpenBadgesCriteriaParamBean.class));
		return openBadgesCriteriaParamBeanList;
	} 
	
	@Transactional(readOnly = false)
	public void claimedMyBadge(String uniquehash) {
		String sql = " UPDATE `open_badges`.`badge_issued` SET `isClaimed`='1' WHERE `uniquehash`= ?" ;
		jdbcTemplate.update(sql, new Object[]{uniquehash});
	}
	
	@Transactional(readOnly = false)
	public void revokedMyBadge(String uniquehash) {
		String sql = " UPDATE `open_badges`.`badge_issued` SET `isRevoked`='1' WHERE `uniquehash`= ?" ;
		jdbcTemplate.update(sql, new Object[]{uniquehash});
	}
	
	@Transactional(readOnly = false)
	public void reclaimedRevokedMyBadge(String uniquehash) {
		String sql = " UPDATE `open_badges`.`badge_issued` SET `isRevoked`='0' WHERE `uniquehash`= ?" ;
		jdbcTemplate.update(sql, new Object[]{uniquehash});
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesUsersBean getPublicBadgeDetails(String uniquehash) {
		OpenBadgesUsersBean openBadgesUsersBean = new OpenBadgesUsersBean();
		String sql = " SELECT  " + 
				"	bi.uniquehash, " + 
				"    bi.awardedAt, " + 
				"    DATE_FORMAT(bi.dateissued, '%b %d, %Y') AS dateissued, " + 
				"    DATE_FORMAT(bi.dateexpire, '%b %d, %Y') AS dateexpire, " + 
				"    bi.isClaimed, " + 
				"    bi.isRevoked, " + 
				"    bi.revocationReason, " + 
				
				
				"    bc.criteriaDescription, " + 
				"    bc.criteriatype, " + 

				"    bc.criteriaId, " + 


				"    b.attachment, " + 
				"    b.badgeName, " + 
				"    b.badgeDescription, " + 
				"    b.issuername, " + 
				"    u.sapid, " + 
				"    u.firstname, " + 
				"    u.lastname, " + 
				"    u.emailId, " + 

				"    u.consumerProgramStructureId, "+
				

				"    u.sapid " + 

				"FROM " + 
				"    open_badges.badge_issued bi " + 
				"        INNER JOIN " + 
				"    open_badges.badge b ON b.badgeId = bi.badgeId " + 
				"    	 INNER JOIN " + 
				"    open_badges.badge_criteria bc ON bc.badgeId = b.badgeId " + 
				"    	 INNER JOIN " + 
				"    open_badges.users u ON u.userId = bi.userId " + 
				"WHERE " + 
				"    bi.uniquehash = ? ";
		
		openBadgesUsersBean =	jdbcTemplate.queryForObject(sql, new Object[]{uniquehash},  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		return openBadgesUsersBean ;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getAllStudentForScheduler() {
		List<OpenBadgesUsersBean> openBadgesUsersBean = new ArrayList<OpenBadgesUsersBean>();
		String sql = "select s.sapid, s.consumerProgramStructureId, s.firstname, s.lastname, s.emailId "+
				  " FROM exam.students s "+
				  "   INNER JOIN " +
				  "  exam.programs p ON p.consumerProgramStructureId = s.consumerProgramStructureId " + 
				  " LEFT JOIN " + 
				  " open_badges.users u ON u.sapid = s.sapid  "+
				  " AND u.consumerProgramStructureId = s.consumerProgramStructureId "+
				  " WHERE "+
				  " (p.programType not in ('Executive Programs', 'Master', 'Modular Program') " + 
				  "  or p.consumerProgramStructureId  in (136, 132, 161, 135, 134, 137, 141, 138, 133, 140, 139) " + 
				  "  or s.program in ( 'M.Sc. (App. Fin.)')  " + 
				  "   )" + 
				  "  AND s.validityEndYear > 2016 " + 
				  "        AND u.sapid IS NULL "  ;
		
	
		openBadgesUsersBean =  jdbcTemplate.query(sql, new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class) );
		return openBadgesUsersBean;
	}
	
	@Transactional(readOnly = false)
	public void insertBadgeUser(OpenBadgesUsersBean usersBean) {
	  String sql = "INSERT INTO `open_badges`.`users`  " + 
				"(`sapid`, `consumerProgramStructureId`, `firstname`, `lastname`, `emailId`, " + 
				" `createdBy`, `lastModifiedBy`) "+
				" VALUES "+
				"(?, ?, ?, ?, ?, ?, ? )"+
				" ON DUPLICATE KEY UPDATE "+
				" consumerProgramStructureId = ?, "+
				" firstname = ?, "+
				" lastname = ?, "+
				" emailId = ?, "+
				" lastModifiedBy = ?";
		
		
		jdbcTemplate.update(sql, new Object[] {
				usersBean.getSapid(),
				usersBean.getConsumerProgramStructureId(),
				usersBean.getFirstname(),
				usersBean.getLastname(),
				usersBean.getEmailId(),
				usersBean.getCreatedBy(),
				usersBean.getLastModifiedBy(),
				
				// start ON DUPLICATE KEY UPDATE
				usersBean.getConsumerProgramStructureId(),
				usersBean.getFirstname(),
				usersBean.getLastname(),
				usersBean.getEmailId(),
				usersBean.getLastModifiedBy()
				
		});

		
	}
	
	@Transactional(readOnly = false)
	public Long insertBadgeIssued(final OpenBadgesUsersBean usersBean) {
	  final	String sql = " INSERT INTO `open_badges`.`badge_issued`  " + 
				" (`badgeId`, `userId`, `uniquehash`,  " + 
				" `awardedAt`,  `createdBy`, `lastModifiedBy`, isClaimed)  " + 
				" VALUES "+
				"(?, ?, ?, ?,  ?, ?, ?) ";
		
	  	if(usersBean.getIsClaimed() == null) {
	  		usersBean.setIsClaimed(0);
		}
		PreparedStatementCreator psc = new PreparedStatementCreator() {

			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				ps.setInt(1, usersBean.getBadgeId());
				ps.setInt(2, usersBean.getUserId());
				ps.setString(3, usersBean.getUniquehash());
				ps.setString(4, usersBean.getAwardedAt());
				ps.setString(5, usersBean.getCreatedBy());
				ps.setString(6, usersBean.getLastModifiedBy());
				ps.setInt(7, usersBean.getIsClaimed());
				
				return ps;
			}
		};
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(psc, keyHolder);
		Long issuedId = keyHolder.getKey().longValue();
		
		return issuedId;
	}
	
	@Transactional(readOnly = false)
	public void insertEvidence(OpenBadgesEvidenceBean evidenceBean) {
		String sql = "INSERT INTO `open_badges`.`badge_evidence` " + 
				" (`issuedId`, `evidenceType`, `evidenceValue`, `createdBy`, `lastModifiedBy`) " + 
				"VALUES "+
				" (?, ?, ?, ?, ?) ";
		jdbcTemplate.update(sql, new Object[] {
				evidenceBean.getIssuedId(),
				evidenceBean.getEvidenceType(),
				evidenceBean.getEvidenceValue(),
				evidenceBean.getCreatedBy(),
				evidenceBean.getLastModifiedBy()
		});
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesIssuedBean> getApplicableSubjectForBadge(String sapid, Integer badgeId) {
		List<OpenBadgesIssuedBean> list = new ArrayList<OpenBadgesIssuedBean>();
		String sql = "SELECT  " + 
				"    pss.subject AS awardedAt " + 
				"FROM " + 
				"    exam.program_sem_subject pss " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.awardedAt = pss.subject " + 
				"        AND u.userId = bi.userId " + 
				"        AND bi.badgeId = ? " + 
				"WHERE " + 
				"    bi.userId IS NULL " + 
				"    AND pss.subject not in ('Assignment','Project','Orientation') " + 
				"    AND u.sapid = ? ";
		list =	jdbcTemplate.query(sql, new Object[]{badgeId, sapid },  new BeanPropertyRowMapper<OpenBadgesIssuedBean>(OpenBadgesIssuedBean.class));
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getAssignmentsubmissionList( Integer badgeId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = "SELECT   " + 
				"u.sapid, " + 
				"u.userId, " + 
				"a.subject AS awardedAt   " + 
				"FROM  " + 
				"exam.assignmentsubmission a " + 
				"	INNER JOIN  " + 
				"open_badges.users u ON u.sapid = a.sapid  " + 
				"	LEFT JOIN   " + 
				"open_badges.badge_issued bi ON bi.awardedAt = a.subject   " + 
				"	AND u.userId = bi.userId   " + 
				"	AND bi.badgeId = ?   " + 
				"WHERE   " + 
				"bi.userId IS NULL   " + 
				"AND a.status = 'Submitted'  "+
				"AND a.subject NOT IN ('Assignment','Project','Orientation')  "+
				"GROUP BY a.sapId , a.subject ";
		list =	jdbcTemplate.query(sql, new Object[]{badgeId },  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesEvidenceBean> getAssignmentsubmissionEvidenceList(Integer badgeId) {
		String sql = " SELECT  " + 
				"    bi.issuedId AS issuedId, " + 
				"    'htmlText' AS evidenceType, " + 
				"    CONCAT(CONCAT('<p> Exam Year / Month : <strong>', " + 
				"                    year, " + 
				"                    ' / ', " + 
				"                    month, " + 
				"                    ' </strong></p>'), " + 
				"            ' ', " + 
				"            CONCAT('<p> Date : <strong>', " + 
				"                    DATE_FORMAT(a.createdDate, '%Y-%m-%d  %h:%i  %p'), " + 
				"                    ' </strong> </p>')) AS evidenceValue " + 
				"FROM " + 
				"    exam.assignmentsubmission a " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = a.sapid " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.awardedAt = a.subject " + 
				"        AND u.userId = bi.userId " + 
				"        AND bi.badgeId = ? " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_evidence be ON bi.issuedId = be.issuedId " + 
				"WHERE " + 
				"    a.status = 'Submitted' " + 
				"        AND be.issuedId IS NULL " + 
				"        AND a.subject NOT IN ('Assignment' , 'Project', 'Orientation') " + 
				"GROUP BY a.sapId , a.subject " + 
				"ORDER BY a.createdDate ASC ";
		List<OpenBadgesEvidenceBean> evidenceBean = jdbcTemplate.query(sql, new Object[]{badgeId},new BeanPropertyRowMapper<OpenBadgesEvidenceBean>( OpenBadgesEvidenceBean.class));
		return evidenceBean;
	}

	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getAskQueryList( Integer badgeId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = "SELECT  " + 
				"    u.sapid,  " + 
				"    u.userId,  " + 
				"    a.subject AS awardedAt " + 
				"FROM " + 
				"    acads.session_query_answer a " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = a.sapId " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.awardedAt = a.subject " + 
				"        AND u.userId = bi.userId " + 
				"        AND bi.badgeId = ? " + 
				"WHERE " + 
				"    bi.userId IS NULL " + 
				"        AND a.subject NOT IN ('Assignment','Project','Orientation') " + 
				"        AND a.isPublic = 'Y' " + 
				"        AND queryType IN ('Academic' , 'Course Query') " + 
				"GROUP BY a.sapId , a.subject " + 
				"HAVING COUNT(a.sapid) >= 1 ";
		list =	jdbcTemplate.query(sql, new Object[]{badgeId },  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		
		return list;
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesEvidenceBean getAskQueryEvidence(String sapid, String subject) {
		String sql = " SELECT " +  
				"'htmlText' AS evidenceType,  " + 
				"    CONCAT(IFNULL(CONCAT('<p> Session Name : <strong>', " + 
				"                            s.sessionName, " + 
				"                            '</strong></p>'), " + 
				"                    ''), " + 
				"            ' ', " + 
				"            CONCAT('<p> Query : <strong>', " + 
				"                    a.query, " + 
				"                    ' </strong></p>'), " + 
				"            ' ', " + 
				"            CONCAT('<p> Date : <strong>', " + 
				"                    DATE_FORMAT(a.createdDate, '%Y-%m-%d  %h:%i  %p'), " + 
				"                    ' </strong> </p>')) AS evidence_value " + 
				"FROM " + 
				"    acads.session_query_answer a " + 
				"        LEFT JOIN " + 
				"    acads.sessions s ON s.id = a.sessionId " + 
				"WHERE " + 
				"    a.sapId = ? " + 
				"        AND a.subject = ? " + 
				"        AND a.isPublic = 'Y' " + 
				"        AND queryType IN ('Academic' , 'Course Query') " + 
				"ORDER BY a.createdDate ASC " + 
				"LIMIT  1 ";
		OpenBadgesEvidenceBean evidenceBean = jdbcTemplate.queryForObject(sql, new Object[]{sapid, subject},new BeanPropertyRowMapper<OpenBadgesEvidenceBean>( OpenBadgesEvidenceBean.class));
		return evidenceBean;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesCriteriaParamBean> getCriteriaDetails(String criteriaName) {
		List<OpenBadgesCriteriaParamBean> criteriaParamBean = new ArrayList<OpenBadgesCriteriaParamBean>();
		String sql = " SELECT " + 
				"    b.badgeId, " + 
				"    cp.criteriaId, " + 
				"    cp.criteriaName, " + 
				"    cp.criteriaValue " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria c ON c.badgeId = b.badgeId " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria_param cp ON cp.criteriaId = c.criteriaId " + 
				"WHERE " + 
				"    cp.criteriaName = ? "+
				"    AND b.status = 1 ";
		criteriaParamBean = jdbcTemplate.query(sql, new Object[] {criteriaName}, new BeanPropertyRowMapper<OpenBadgesCriteriaParamBean>(OpenBadgesCriteriaParamBean.class));
		return criteriaParamBean;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getMasterKeyListByBadgeId(Integer badgeId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = "SELECT  " + 
				"    map.consumerProgramStructureId " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_masterkey_mapping map ON map.badgeId = b.badgeId " + 
				"        INNER JOIN " + 
				"    exam.consumer_program_structure cps ON cps.id = map.consumerProgramStructureId " + 
				"WHERE " + 
				"    b.badgeId = ?   " + 
				"ORDER BY map.consumerProgramStructureId "  ;
		list =	jdbcTemplate.query(sql, new Object[]{badgeId },  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		return list;	
	}
	
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getProgramCompletionBadgeNotIssuedStudentList(Integer badgeId, Integer consumerProgramStructureId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();

		String sql = " SELECT " + 
				"    u.userId, u.sapid " + 
				"FROM " + 
				"    exam.students s  " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = s.sapid and u.consumerProgramStructureId = s.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON u.userId = bi.userId AND bi.badgeId = ? " + 
				"WHERE " + 
				"    bi.awardedAt IS NULL " + 
				"    AND u.consumerProgramStructureId = ?  AND (programStatus = '' OR programStatus is null OR programStatus = 'Program Withdrawal') " ; 
		list =	jdbcTemplate.query(sql, new Object[]{badgeId, consumerProgramStructureId },  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		return list;
	} 
	
	
	
	@Transactional(readOnly = false)
	public List<OpenBadgesEvidenceBean> getProgramCompletionEvidenList(Integer badgeId) {
		List<OpenBadgesEvidenceBean> list = new ArrayList<OpenBadgesEvidenceBean>();
		String sql =  "SELECT  " + 
				"     bi.issuedId AS issuedId, " + 
				"    'htmlText' AS evidenceType, " + 
				"    CONCAT('<p> <strong>', " + 
				"      pf.total, " + 
				"      ' </strong> marks out of 100 in <strong>', " + 
				"      pf.subject, " + 
				"      '</strong>',IF(STRCMP('Project',subject) = 0, '', ' subject'),' in the semester ', " + 
				"      pf.sem, " + 
				"      ' exam</p>') AS evidenceValue " + 
				"FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = pf.sapid " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.userId = u.userId AND bi.badgeId = ? " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_evidence be ON be.issuedId = bi.issuedId " + 
				"WHERE " + 
				"    pf.isPass = 'Y' AND be.issuedId IS NULL " + 
				"ORDER BY  bi.issuedId, pf.sem ";
		list =	jdbcTemplate.query(sql, new Object[]{ badgeId },  new BeanPropertyRowMapper<OpenBadgesEvidenceBean>(OpenBadgesEvidenceBean.class));
		return list;
	
	}
	
	
	// Start Top in Assignment 
	@Transactional(readOnly = true)
	public List<Integer> getSemesterListByMasterKey(Integer consumerProgramStructureId){
		String sql = " SELECT  " + 
				"    sem " + 
				"FROM " + 
				"    exam.program_sem_subject " + 
				"WHERE " + 
				"    active = 'Y' " + 
				"        AND consumerProgramStructureId = ? " + 
				"GROUP BY sem " + 
				"ORDER BY sem ";
		List<Integer> list =	jdbcTemplate.query(sql, new Object[] { consumerProgramStructureId } , new SingleColumnRowMapper<Integer>( Integer.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesTopInAssignmentDto> getAcadNExamYearMonthList() {
		List<OpenBadgesTopInAssignmentDto> list = new ArrayList<>();
		String sql =  " SELECT " + 
				"    month AS examMonth, " + 
				"    year AS examYear, " + 
				"    acadMonth , " + 
				"    year AS acadYear " + 
				"FROM " + 
				"    exam.examorder o " + 
				"WHERE " + 
				"    month IN ('Jun' , 'Dec') AND live = 'Y' " + 
				"        AND year >= 2014 " + 
				"ORDER BY o.order ";
		list =	jdbcTemplate.query(sql,  new BeanPropertyRowMapper<OpenBadgesTopInAssignmentDto>(OpenBadgesTopInAssignmentDto.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public List< OpenBadgesTopInAssignmentDto> getAllStudentDataForTopInAssignment(Integer badgeId, String month, Integer year, List<String> applicableSapids, String subject, Integer sem, Integer criteriaValue) {
		StringBuffer sql = new StringBuffer("select sapid, subject as subjectname, userId, assignmentscore,  assignmentYear, assignmentMonth, DATE_FORMAT(submissionDate, '%Y-%m-%d') AS submissionDate from    "
				+ "( SELECT    "
				+ "		 *,   "
				+ "		 @curRank:=IF(@prev = assignmentscore,   "
				+ "			 @curRank,   "
				+ "			 @curRank + 1) AS `studentrank`,   "
				+ "		 @prev AS previous,   "
				+ "		 @prev:=assignmentscore   "
				+ "	 FROM   "
				+ "	 (SELECT    "
				+ "		 pf.sapid,   "
				+ "		 pf.subject,   "
				+ "		 pf.assignmentscore,   "
				+ "		 pf.assignmentYear,   "
				+ "		 pf.assignmentMonth,  "
				+ "		 bu.userId,  "
				+ "		 a.createdDate AS submissionDate  "
				+ "		 FROM   "
				+ "		 (SELECT @curRank:=0) r,   "
				+ "		 (SELECT @prev:=NULL) AS init,   "
				+ "		 exam.assignmentsubmission a    "
				+ "			 INNER JOIN   "
				+ "		 exam.passfail pf ON a.sapid = pf.sapid AND a.subject = pf.subject "
				+ "      AND a.year = pf.assignmentYear AND a.month = pf.assignmentMonth "
				+ "			 INNER JOIN   "
				+ "		 open_badges.users bu ON pf.sapid = bu.sapid   "
				+ "	 WHERE   "
				+ "		 pf.assignmentMonth = :month   "
				+ "			 AND pf.assignmentYear = :year   "
				+ "			 AND pf.subject = :subject  "
				+ "			 AND pf.sem = :sem  "
				+ "			 AND pf.isPass = 'Y'   "
				+ "			 AND pf.resultProcessedMonth = :month   "
				+ "			 AND pf.resultProcessedYear = :year   "
				+ "			 AND pf.sapid  IN ( :sapIds)    "
				+ "			 ORDER BY CAST(pf.assignmentscore AS UNSIGNED)  DESC  "
				+ "			 ) t1 )  t_final where studentrank = :criteriaValue ");
		

		
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("month", month);
		queryParams.addValue("year", year);
		queryParams.addValue("subject", subject);
		queryParams.addValue("sem", sem);
		queryParams.addValue("sapIds", applicableSapids);
		queryParams.addValue("criteriaValue", criteriaValue);
		
		List<OpenBadgesTopInAssignmentDto> studentlists = namedJdbcTemplate.query(sql.toString(), queryParams,
				new BeanPropertyRowMapper<OpenBadgesTopInAssignmentDto>(OpenBadgesTopInAssignmentDto.class));
		
		return studentlists;
	}
	
	@Transactional(readOnly = true)
	public BadgeKeywordsDto getProgramNameAndSemForAssignmentBadge(String sapid, String subject) {
		String sql = " SELECT  " + 
				"    pss.sem AS semester, p.programname AS programNameFull " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"        INNER JOIN " + 
				"    exam.programs p ON s.consumerProgramStructureId = p.consumerProgramStructureId " + 
				"WHERE " + 
				"    pss.subject = ?  " + 
				"        AND s.sapid = ? "; 
		BadgeKeywordsDto dto =jdbcTemplate.queryForObject(sql, new Object[]{subject, sapid },new BeanPropertyRowMapper<BadgeKeywordsDto>( BadgeKeywordsDto.class));
		return dto ;
	}
	// END Top in Assignment 
	
	// START Top in semester
	@Transactional(readOnly = true)
	public Integer getMaxRegistration(String sapid){
		String sql = " SELECT  " + 
				"    sem " + 
				"FROM " + 
				"    exam.registration " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND sem = (SELECT  " + 
				"            MAX(sem) " + 
				"        FROM " + 
				"            exam.registration " + 
				"        WHERE " + 
				"            sapid = ? ) ";
		Integer sem =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid, sapid },  Integer.class);
		return sem;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesTopInSemesterDto> getSemesterListForTopInSemesterBadge(Integer consumerProgramStructureId){
		String sql = " SELECT  " + 
				"    sem, COUNT(sem) AS subjectCount " + 
				"FROM " + 
				"    exam.program_sem_subject " + 
				"WHERE " + 
				"    active = 'Y' " + 
				"        AND consumerProgramStructureId = ? " + 
				"GROUP BY sem " + 
				"ORDER BY sem ";
		List<OpenBadgesTopInSemesterDto> list =	jdbcTemplate.query(sql, 
			new Object[] { consumerProgramStructureId }	
			, new BeanPropertyRowMapper<OpenBadgesTopInSemesterDto>( OpenBadgesTopInSemesterDto.class));
		return list;
	}
	
	
	@Transactional(readOnly = true)
	public List<OpenBadgesEvidenceBean> getTopInSemesterEvidenList(Integer badgeId, Integer sem) {
		List<OpenBadgesEvidenceBean> list = new ArrayList<OpenBadgesEvidenceBean>();
		String sql =  "SELECT  " + 
				"     bi.issuedId AS issuedId, " + 
				"    'htmlText' AS evidenceType, " + 
				"    CONCAT('<p> <strong>', " + 
				"      pf.total, " + 
				"      ' </strong> marks out of 100 in <strong>', " + 
				"      pf.subject, " + 
				"      '</strong>',IF(STRCMP('Project',subject) = 0, '', ' subject'),' in the semester ', " + 
				"      pf.sem, " + 
				"      ' exam</p>') AS evidenceValue " + 
				"FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = pf.sapid " + 
				"        INNER JOIN " + 
				"    open_badges.badge_issued bi ON bi.userId = u.userId AND bi.badgeId = ? " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_evidence be ON be.issuedId = bi.issuedId " + 
				"WHERE " + 
				"    pf.isPass = 'Y' AND be.issuedId IS NULL " + 
				"    AND bi.awardedAt = ? " + 
				"    AND pf.sem = ? " + 
				"ORDER BY  bi.issuedId ";
		String awardedAt = "Semester "+sem;
		list =	jdbcTemplate.query(sql, new Object[] {badgeId, awardedAt, sem },  new BeanPropertyRowMapper<OpenBadgesEvidenceBean>(OpenBadgesEvidenceBean.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public boolean passfailExists(String sapid, Integer sem) {
		String sql = "SELECT  " + 
				"    count(*)  " + 
				"FROM " + 
				"    exam.passfail " + 
				"WHERE " + 
				"    sapid = ? AND sem = ? ";
		Integer count =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid, sem },  Integer.class);
		
		return count > 0 ;
	}
	
	@Transactional(readOnly = true)
	public String getProgramNameBySem(String sapid, Integer sem) {
		String sql = " SELECT " + 
				"    p.programname " + 
				"FROM " + 
				"    exam.registration r " + 
				"        INNER JOIN " + 
				"    exam.programs p ON r.consumerProgramStructureId = p.consumerProgramStructureId " + 
				"WHERE " + 
				"    r.sapid = ? AND r.sem = ? " ;
		
		try {
		String programname =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid, sem },  String.class);
		return programname ;
		}catch(Exception e)
		{
			//e.printStackTrace();
			return "";
		}
		
	}
	
	@Transactional(readOnly = false)
	public List<OpenBadgesForReRegSemDto> getAllStudentDataForReReg(Integer sem,Integer masterKey,Integer badgeId)
	{
		
		
		
		StringBuffer sql_student = new StringBuffer(" SELECT u.userId,u.sapid, r.createdDate,r.month,r.year  from		" + 
													"	exam.students s  " + 
													"	INNER JOIN		" + 
													"	exam.registration r ON s.sapid = r.sapid 	" + 
													"	INNER JOIN		" + 
													"	open_badges.users u ON r.sapid = u.sapid		"+
													"	LEFT JOIN 		" + 
													"	open_badges.badge_issued bi ON  bi.userId = u.userId AND u.userId = bi.userId  AND bi.badgeId = ? 		" + 
													"	WHERE  r.consumerProgramStructureId = ? AND r.sem = ? AND (s.programStatus is null or programStatus = '')	" + 
													" 	AND bi.userId is NULL	group by s.sapid; ");
					
		
		List<OpenBadgesForReRegSemDto> studentlists = null;
		
		try {
		
			
			
			studentlists = jdbcTemplate.query(sql_student.toString(),new Object[] {badgeId,masterKey,sem},new BeanPropertyRowMapper(OpenBadgesForReRegSemDto.class));
			
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		return studentlists;
	}
	

	@Transactional(readOnly = false)
	public int batchJobForReRegistrationEvidence(final List<OpenBadgesEvidenceBean> evidenceList) {
		String sql = "INSERT INTO `open_badges`.`badge_evidence` " + 
				" (`issuedId`, `evidenceType`, `evidenceValue`, `createdBy`, `lastModifiedBy`) " + 
				"VALUES "+
				" (?, ?, ?, ?, ?) ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			int i[] = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					OpenBadgesEvidenceBean m = evidenceList.get(i);
					ps.setString(1, new String(m.getIssuedId().toByteArray()));
					ps.setString(2, "htmlText");
					ps.setString(3, m.getEvidenceValue());
					ps.setString(4, "ReRegistrationSemScheduler");
					
					ps.setString(5, "ReRegistrationSemScheduler");
				}
				public int getBatchSize() {
					return evidenceList.size();
				}
			});
			return i.length;
		} catch (DataAccessException e) {
			//e.printStackTrace();
			return 0;
		}
		
	
	}
	

	@Transactional(readOnly = true)
	public String getCriteriaNameByCriteriaId(Integer criteriaId) {
		
		String sql = "SELECT criteriaName FROM open_badges.badge_criteria_param where criteriaId = ? ";
		
		String criteriaName =	jdbcTemplate.queryForObject(sql, new Object[]{ criteriaId},  String.class);
		
		return criteriaName ;
		
	}
	
	@Transactional(readOnly = true)
	public String getProgramName(String sapid) {
		String sql = " SELECT " + 
				"    p.programname " + 
				"FROM " + 
				"    exam.students s " + 
				"        INNER JOIN " + 
				"    exam.programs p ON s.consumerProgramStructureId = p.consumerProgramStructureId " + 
				"WHERE " + 
				"    s.sapid = ? " ;
		String programname =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid },  String.class);
		
		return programname ;
	}

	
	

	//start in badge alumni
	@Transactional(readOnly = false)
	public  List<OpenBadgesUsersBean> getAllStudentDataForAlumni(Integer masterKey,Integer badgeId)
	{

		
		
		StringBuffer sql_student = new StringBuffer("	SELECT u.userId,u.sapid,s.consumerProgramStructureId FROM exam.students s " + 
													"	INNER JOIN		" + 
													"	open_badges.users u on s.sapid = u.sapid AND s.consumerProgramStructureId = u.consumerProgramStructureId		" + 
													"	LEFT JOIN	" + 
													"	open_badges.badge_issued bi on u.userId = bi.userId AND bi.badgeId = ? " + 
													"	WHERE   s.consumerProgramStructureId = ? AND bi.userId is NULL AND (programStatus = '' OR programStatus is null) ;  ");
					
		
		List<OpenBadgesUsersBean> studentlists = new ArrayList<OpenBadgesUsersBean>();
		
		try {
		
			
			
			studentlists = jdbcTemplate.query(sql_student.toString(),new Object[] {badgeId,masterKey},new BeanPropertyRowMapper(OpenBadgesUsersBean.class));
			
			
		}catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		return studentlists;
	}
	
	
	
	@Transactional(readOnly = true)
	public String getProgramNameByProgramId(String programId) {
		
		String sql = "SELECT  " + 
				"    code " + 
				"FROM " + 
				"    exam.program " + 
				"WHERE " + 
				"    id = ? ";
		String programcode = jdbcTemplate.queryForObject(sql, new Object[] { programId}, String.class);
		return programcode;
	}
	 
	@Transactional(readOnly = true)
	public String getCertificatePath(String uniquehash) {
		String filepath = null;
		try {
			
		String sql = " select filepath from portal.aws_uploaded_filespath where uniquehash = ? and filetype = 'Final Certificate' limit 1"; 
		filepath =jdbcTemplate.queryForObject(sql, new Object[] { uniquehash}, String.class);

		}catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return filepath;
	}
	
	//end in badge alumni
	@Transactional(readOnly = true)
	public List< OpenBadgesTopInsubjectDto > getAllStudentDataForTopInSubject(Integer criteriaValue,Integer masterKey,Integer badgeId)
	{
			
		StringBuffer sql_student = new StringBuffer(" select s.sapid,s.subject,s.total,s.outOfMarks,bu.userId, s.rank from exam.subject_wise_rank s	" +
													"	INNER JOIN		" + 
													"	open_badges.users bu ON s.sapid = bu.sapid		"+
													"	LEFT JOIN 		" + 
													" open_badges.badge_issued bi on bi.userId = bu.userId  AND  bi.awardedAt = s.subject and bi.badgeId = ? " + 
													"	WHERE  s.masterKey = ? and s.rank like '"+criteriaValue+"/%' and  bi.issuedId is null;	")  ;
					
		
		List< OpenBadgesTopInsubjectDto > studentlists = jdbcTemplate.query(sql_student.toString(),new Object[] {badgeId,masterKey},new BeanPropertyRowMapper<OpenBadgesTopInsubjectDto>(OpenBadgesTopInsubjectDto.class));	
		return studentlists;
	}
	
	@Transactional(readOnly = true)
	public String getSemesterNameBySubject(String sapid, String subjectName) {
		String sql = " SELECT "
				+ "    r.sem, p.name AS programFullName "
				+ "FROM "
				+ "    exam.registration r "
				+ "        INNER JOIN "
				+ "    exam.program p ON r.program = p.code "
				+ "WHERE "
				+ "    r.sapid = ? "
				+ "        AND r.month = ? "
				+ "        AND r.year = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{ sapid, subjectName },  String.class);
		
	}
	
	@Transactional(readOnly = true)
	public List<String> getSubjectList(Integer consumerProgramStructureId, Integer sem) {
		String sql = " select subject from exam.program_sem_subject where consumerProgramStructureId = ? and sem = ? and hasAssignment = 'Y' " ;
		
		return jdbcTemplate.query(sql, new Object[]{ consumerProgramStructureId, sem },  new SingleColumnRowMapper<String>(String.class));
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesTopInSemesterDto > getToppedInSemApplicableList(
			Integer sem, Integer masterKey,
			String examMonth, Integer examYear,
			Integer rankCriteria, Integer badgeId
			){
		
		String sql = " SELECT  " + 
				"   bu.userId, c.sapid, "+rankCriteria+" AS `rank`,  c.total As totalMarks , c.outOfMarks,  c.sem " + 
				"FROM " + 
				"    exam.cycle_wise_rank c " + 
				"        INNER JOIN " + 
				"    open_badges.users bu ON c.sapid = bu.sapid " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.userId = bu.userId " + 
				"        AND bi.awardedAt = 'Semester "+sem+"' " + 
				"        AND bi.badgeId = ? " + 
				"WHERE " + 
				"    c.year = ? and c.month = ?  " + 
				"AND c.masterKey = ? and c.sem = ? and c.rank like '"+rankCriteria+"/%'" + 
				"        AND bi.issuedId IS NULL ";
					
		
		List< OpenBadgesTopInSemesterDto > studentlists = jdbcTemplate.query(sql,new Object[] {
				badgeId, examYear, examMonth,  masterKey,
				sem
				},
				new BeanPropertyRowMapper<OpenBadgesTopInSemesterDto>(OpenBadgesTopInSemesterDto.class));	
		return studentlists;
	}

		
		@Transactional(readOnly = true)
		public List<String> getApplicableSapids(String month, Integer year, Integer masterkey, String subject, Integer sem){ 
			String sql = "select sapid "
					+ " from exam.subject_wise_rank "
					+ " where "
					+ " month = ? "
					+ " and year = ? "
					+ " and masterkey = ? "
					+ " and subject = ? "
					+ " and sem = ? ";
					
			List<String> list = jdbcTemplate.query(sql, new Object[] { month, year, masterkey, subject, sem} , new SingleColumnRowMapper<String>( String.class));
			return list;
			
		}

		
		@Transactional(readOnly = true)
		public List< OpenBadgesTopInTEEDto > getAllStudentDataForTopInTEE(String month, Integer year, List<String> applicableSapids, String subject, Integer sem, Integer criteriaValue) {
			StringBuffer sql = new StringBuffer("select sapid,subject, userId, writtenscore, studentrank, writtenYear, writtenMonth from    "
					+ "( SELECT    "
					+ "		 *,   "
					+ "		 @curRank:=IF(@prev = writtenscore,   "
					+ "			 @curRank,   "
					+ "			 @curRank + 1) AS `studentrank`,   "
					+ "		 @prev AS previous,   "
					+ "		 @prev:=writtenscore   "
					+ "	 FROM   "
					+ "	 (SELECT    "
					+ "		 pf.sapid,   "
					+ "		 pf.subject,   "
					+ "		 pf.writtenscore,   "
					+ "		 pf.writtenMonth,   "
					+ "		 pf.writtenYear,  "
					+ "		 bu.userId  "
					+ "		 FROM   "
					+ "		 (SELECT @curRank:=0) r,   "
					+ "		 (SELECT @prev:=NULL) AS init,   "
					+ "		 exam.passfail pf   "
					+ "			 INNER JOIN   "
					+ "		 open_badges.users bu ON pf.sapid = bu.sapid   "
					+ "	 WHERE   "
					+ "		 writtenMonth = :month   "
					+ "			 AND pf.writtenYear = :year   "
					+ "			 AND pf.subject = :subject  "
					+ "			 AND pf.sem = :sem  "
					+ "			 AND pf.isPass = 'Y'   "
					+ "			 AND pf.resultProcessedMonth = :month   "
					+ "			 AND pf.resultProcessedYear = :year   "
					+ "			 AND pf.sapid  IN ( :sapIds)    "
					+ "			 ORDER BY writtenscore DESC  "
					+ "			 ) t1 )  t_final where studentrank = :criteriaValue ");
			

			
			MapSqlParameterSource queryParams = new MapSqlParameterSource();
			queryParams.addValue("month", month);
			queryParams.addValue("year", year);
			queryParams.addValue("subject", subject);
			queryParams.addValue("sem", sem);
			queryParams.addValue("sapIds", applicableSapids);
			queryParams.addValue("criteriaValue", criteriaValue);
			
			List<OpenBadgesTopInTEEDto> studentlists = namedJdbcTemplate.query(sql.toString(), queryParams,
					new BeanPropertyRowMapper<OpenBadgesTopInTEEDto>(OpenBadgesTopInTEEDto.class));
			
			return studentlists;
		}
		
		//end in badge TEE
		
		
	@Transactional(readOnly = true)
	public OpenBadgesAppearedForTEEBean getExamDateAndTimeFromHistory(String month, Integer year, String sapid, String subject) {
		
		String sql = "SELECT "
				+ "    examDate, examTime "
				+ " FROM "
				+ "    exam.exambookings_history "
				+ " WHERE "
				+ "    sapid = ? "
				+ "        AND `subject` = ? "
				+ "        AND `year` = ? "
				+ "        AND `month` = ? "
				+ "        AND `booked` = 'Y'";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { sapid, subject, year, month },
				new BeanPropertyRowMapper<OpenBadgesAppearedForTEEBean>(OpenBadgesAppearedForTEEBean.class));
		
	}
	
	@Transactional(readOnly = true)
	public OpenBadgesAppearedForTEEBean getExamDateAndTime(String month, Integer year, String sapid, String subject) {
		
		
		String sql = "SELECT "
				+ "    examDate, examTime "
				+ " FROM "
				+ "    exam.exambookings "
				+ " WHERE "
				+ "    sapid = ? "
				+ "        AND `subject` = ? "
				+ "        AND `year` = ? "
				+ "        AND `month` = ? "
				+ "        AND `booked` = 'Y'";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { sapid, subject, year, month },
				new BeanPropertyRowMapper<OpenBadgesAppearedForTEEBean>(OpenBadgesAppearedForTEEBean.class));
		
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesAppearedForTEEBean> getAppearedTEEApplicableSapids(String month, Integer year, Integer masterkey, String subject, Integer sem, Integer badgeId){ 
		String sql = "SELECT " + 
				"    r.sapid AS sapId, u.userId " + 
				"FROM " + 
				"    exam.registration r " + 
				"        INNER JOIN " + 
				"    open_badges.users u ON r.sapid = u.sapid " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.userId = u.userId " + 
				"        AND bi.awardedAt = ? " + 
				"        AND bi.badgeId = ? " + 
				"WHERE " + 
				"    r.year = ? AND r.month = ? " + 
				"        AND r.consumerProgramStructureId = ? " + 
				"        AND r.sem = ? " + 
				"        AND bi.issuedId IS NULL ";
				
		List<OpenBadgesAppearedForTEEBean> list = jdbcTemplate.query(sql, new Object[] { subject, badgeId, year, month,  masterkey,  sem} ,
				new BeanPropertyRowMapper<OpenBadgesAppearedForTEEBean>( OpenBadgesAppearedForTEEBean.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public boolean checkAppearedTEERegularAttempt(String sapid, String subject, String examMonth, Integer examYear) {
		String sql = "SELECT " + 
				"   count( m.sapid) " + 
				"FROM " + 
				"    exam.marks m " + 
				"        INNER JOIN " + 
				"    exam.passfail pf ON m.sapid = pf.sapid " + 
				"        AND m.subject = pf.subject " + 
				"        INNER JOIN " + 
				"    exam.examorder o ON o.month = pf.resultProcessedMonth " + 
				"        AND o.year = pf.resultProcessedYear " + 
				"WHERE " + 
				"    pf.sapid = ? " + 
				"        AND pf.subject = ? " +
				"        AND pf.subject NOT IN ('Project' , 'Module 4 - Project','Simulation: Mimic Social','Simulation: Mimic Pro')" + 
				"        AND m.year = ? " + 
				"        AND m.month = ? " + 
				"        AND m.writenscore NOT IN ('AB' , '#CC', 'NV', 'RIA', '') " + 
				"        AND m.processed = 'Y' " + 
				"        AND o.live = 'Y'  ";
		Integer count =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid, subject, examYear, examMonth },  Integer.class);
		
		return count > 0 ;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSapidList(String monthName, Integer year, Integer consumerProgramStructureId, Integer sem) {
		String sql = "SELECT "
				+ "    sapid "
				+ "FROM "
				+ "    exam.registration "
				+ "WHERE "
				+ "    month = ? AND year = ? "
				+ "        AND consumerProgramStructureId = ? "
				+ "        AND sem = ?";
		
		return jdbcTemplate.queryForList(sql, new Object[]{monthName, year, consumerProgramStructureId, sem}, String.class);
	}

	@Transactional(readOnly = true)
	public Map<String, OpenBadgesPortalVisitStreakDto> getPageVisitDetails(String sapId, String startDate, String endDate, String tableName) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(analyticsDataSource);
		String sql = "SELECT "
				+ "    sapid,"
				+ "    SUM(timespent) AS timeSpent, "
				+ "    DATE(created_at) AS createdDate "
				+ "FROM lti.page_visits  "
				+ "WHERE "
				+ "    sapid = ? "
				+ "  AND  DATE_FORMAT(created_at, '%Y-%m-%d') between ? and ? "
				+ "GROUP BY createdDate ";
		
		return jdbcTemplate.query(sql, new Object[] {sapId, startDate, endDate}, (ResultSet rs) -> {
														return customResultSetBeanMapper(rs, String.class, OpenBadgesPortalVisitStreakDto.class);
		});
	}
	
	/**
     * A customResultSetBeanMapper which returns a Map wherein the first ResultSet value is set as the Key, and second as it's Value 
     * @param rs - ResultSet consisting of the result returned from the database query
     * @param keyClass - Class to be set for Key
     * @param valueClass - Class to be set for Value
     * @return - Map containing the values from the ResultSet provided
     * @throws SQLException
     * @author anilkumar.prajapati
     */
    private <K, V> Map<K, V> customResultSetBeanMapper(ResultSet rs, Class<K> keyClass, Class<V> valueClass) throws SQLException {
    	TreeMap<K, V> resultMap= new TreeMap<>();
        while(rs.next()) {
            resultMap.put(keyClass.cast(rs.getString("createdDate")), new BeanPropertyRowMapper<>(valueClass).mapRow(rs, rs.getRow()));
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
	public OpenBadgesPortalVisitStreakDto getbadgeDetailsBySapId(String sapId, String semester) {
    	String sql = "SELECT "
    			+ "    p.name AS programNameFull "
    			+ "FROM "
    			+ "    exam.registration r "
    			+ "        INNER JOIN "
    			+ "    exam.program p ON r.program = p.code "
    			+ "WHERE "
    			+ "    r.sapid = ? AND r.sem = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{sapId, semester}, new BeanPropertyRowMapper<>(OpenBadgesPortalVisitStreakDto.class));
	}

    @Transactional(readOnly = true)
	public OpenBadgesPortalVisitStreakDto getStudentDetailsForStreak(String sapId) {
		String sql = " SELECT "
				+ "    userId  "
				+ "FROM "
				+ "    open_badges.users  "
				+ "WHERE "
				+ "    sapid = ? ";
		
		return jdbcTemplate.queryForObject(sql, new Object[] {sapId}, new BeanPropertyRowMapper<>(OpenBadgesPortalVisitStreakDto.class));
	}
	
    @Transactional(readOnly = true)
	public Integer getMasterKeyBySapId(String sapId) {
		String sql = "SELECT "
				+ "    consumerProgramStructureId "
				+ "FROM "
				+ "    open_badges.users "
				+ "WHERE"
				+ "    sapid = ? "; 
		
		return	jdbcTemplate.queryForObject(sql, new Object[]{sapId}, Integer.class);
	}

    @Transactional(readOnly = true)
	public Map<String, OpenBadgesPortalVisitStreakDto> getPageVisitDetailsBySapId(String sapId, String startDate, String endDate) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(analyticsDataSource);
		String sql = "SELECT "
				+ "    sapid,"
				+ "    SUM(timespent) AS timeSpent, "
				+ "    DATE(created_at) AS createdDate "
				+ "FROM lti.page_visits  "  
				+ "WHERE "
				+ "    sapid = ? "
				+ "  AND  DATE_FORMAT(created_at, '%Y-%m-%d') between ? and ? "
				+ "GROUP BY createdDate ";
		
		return jdbcTemplate.query(sql, new Object[] {sapId, startDate, endDate}, (ResultSet rs) -> {
														return customResultSetBeanMapper(rs, String.class, OpenBadgesPortalVisitStreakDto.class);
		});
	}
   
	public boolean checkIfBadgeAlreadyAwarded(Integer userId, String awardedAt, Integer badgeId) {
		
		String sql = "SELECT " + 
				"    COUNT(`issuedId`) " + 
				" FROM " + 
				"    `open_badges`.`badge_issued` " + 
				" WHERE " + 
				"    `awardedAt` = ? AND `badgeId` = ? " + 
				"        AND `userId` = ?";
		
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { awardedAt, badgeId, userId }, Integer.class);
		
		return count > 0;
	}
	
	public List<OpenBadgesUsersBean> getProgramCompletionList(Integer badgeId, Integer consumerProgramStructureId, Integer criteriaValue) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		String sql = " SELECT " + 
				"    u.userId, pf.sapid, 'Program Completion' AS awardedAt  " + 
				"FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    exam.students s on s.sapid = pf.sapid" + 
				"        INNER JOIN " + 
				"    open_badges.users u ON u.sapid = s.sapid " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON u.userId = bi.userId AND bi.badgeId = ? " + 
				"WHERE " + 
				"    pf.isPass = 'Y'  " + 
				"    AND s.programCleared = 'Y' " + 
				"    AND bi.awardedAt IS NULL " + 
				"    AND u.consumerProgramStructureId = ? " + 
				"GROUP BY pf.sapid " + 
				"HAVING COUNT(*) = ? ";
		list =	jdbcTemplate.query(sql, new Object[]{badgeId, consumerProgramStructureId,  criteriaValue},  new BeanPropertyRowMapper<OpenBadgesUsersBean>(OpenBadgesUsersBean.class));
		return list;
	}
	
	public List<OpenBadgeLectureAttendanceBean> getNotIssuedBadgeUserIdList(Integer badgeId, Integer consumerProgramStructureId, String year, String month) {
		List<OpenBadgeLectureAttendanceBean> list = new ArrayList<OpenBadgeLectureAttendanceBean>();
		String sql = "SELECT  " + 
				"    u.userId, " + 
				"    u.sapid,  " + 
				"    r.year,  " + 
				"    r.month,  " + 
				"    r.sem,  " + 
				"    pss.subject, " + 
				"    pss.id AS program_sem_subject_id " + 
				"FROM " + 
				"    open_badges.users u " + 
				"        INNER JOIN " + 
				"    exam.registration r ON r.sapid = u.sapid " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.consumerProgramStructureId = r.consumerProgramStructureId " + 
				"        AND pss.sem = r.sem " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON bi.userId = u.userId AND bi.badgeId = ? " + 
				"        AND pss.sem = r.sem " + 
				"        AND pss.subject = bi.awardedAt " + 
				"WHERE " + 
				"    u.consumerProgramStructureId = ? " + 
				"        AND pss.subject NOT IN ('Assignment' , 'Project', 'Orientation') " + 
				"        AND bi.userId IS NULL AND r.year=? AND r.month=? " ;
		list =	jdbcTemplate.query(sql, new Object[]{ badgeId, consumerProgramStructureId, year, month },  new BeanPropertyRowMapper<OpenBadgeLectureAttendanceBean>(OpenBadgeLectureAttendanceBean.class));
		return list;
	}
	
	
	public List<OpenBadgeLectureAttendanceBean> getLectureAttendanceDataForProcess(Integer program_sem_subject_id, Integer year, 
			String month, String subject, String sapid
			) {
		List<OpenBadgeLectureAttendanceBean> list = new ArrayList<OpenBadgeLectureAttendanceBean>();
		String sql = "SELECT " + 
				" saf.attended,  " + 
				" DATE_FORMAT(saf.attendTime, '%Y-%m-%d  %h:%i  %p') as attendTime, " + 
				" DATE_FORMAT(concat(s.date,' ',s.startTime), '%Y-%m-%d  %h:%i  %p') as sessionTime, " + 
				" s.id AS sessionId,   " + 
				" s.track, " + 
				" s.sessionName " + 
				" FROM   " + 
				" (SELECT  " + 
				" sessionId  " + 
				" FROM  " + 
				" acads.session_subject_mapping " + 
				" WHERE " + 
				" program_sem_subject_id = ?  ) smap " + 
				" INNER JOIN  " + 
				" (SELECT " + 
				" id, date, startTime, sessionName, track " + 
				" FROM  " + 
				" acads.sessions  " + 
				" WHERE  " + 
				" isCancelled = 'N' " + 
				" AND year = ? " + 
				" AND month =  ? " + 
				" AND subject = ?) s ON smap.sessionId = s.id " + 
				" LEFT JOIN " + 
				" (SELECT " + 
				" sessionId, attended, attendTime " + 
				" FROM  " + 
				" acads.session_attendance_feedback " + 
				" WHERE " + 
				" sapid = ? ) saf ON s.id = saf.sessionId " + 
				" ORDER BY s.track,s.date,s.startTime" ;
		
		list =	jdbcTemplate.query(sql, new Object[]{ 
				program_sem_subject_id, 
				year, month, subject,
				sapid
				},  new BeanPropertyRowMapper<OpenBadgeLectureAttendanceBean>(OpenBadgeLectureAttendanceBean.class));
		return list;
	}
	
	// START Top in program
	@Transactional(readOnly = true)
	public String getExamOrder(String month, Integer year) {
		String sql = " " + 
				"SELECT  " + 
				"    o.order " + 
				"FROM " + 
				"    exam.examorder o " + 
				"WHERE " + 
				"    o.month = ? AND o.year = ? ";
		return jdbcTemplate.queryForObject(sql, new Object[]{ month, year },  String.class);
	}

	@Transactional(readOnly = true)
	public OpenBadgesTopInProgramDto getYearMonthForTopInProgram(String examorder) {

		String sql = " SELECT  " + 
				"    month AS examMonth, year, acadMonth  " + 
				"FROM " + 
				"    exam.examorder o " + 
				"WHERE " + 
				"    o.order = ? " + 
				"ORDER BY o.order DESC";
		OpenBadgesTopInProgramDto dto = jdbcTemplate.queryForObject(sql, new Object[] {examorder}, new BeanPropertyRowMapper<OpenBadgesTopInProgramDto>(OpenBadgesTopInProgramDto.class));
		return dto;
	}

	@Transactional(readOnly = true)
	public List<OpenBadgesTopInProgramDto> getTotalMarksBYSemForTopInProgram(OpenBadgesTopInProgramDto dto, Integer consumerProgramStructureId) {

		String sql = " SELECT  " + 
				"    pf.sapid AS sapid, " + 
				"    s.program , " + 
				"    SUM(total) AS totalMarks, " + 
				"    COUNT(pss.subject) * 100 AS outOfMarks " + 
 
				"FROM " + 
				"    exam.passfail pf " + 
				"        INNER JOIN " + 
				"    exam.students s ON pf.sapid = s.sapid " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.subject = pf.subject " + 
				"        AND pss.consumerProgramStructureId = ? " + 
				"        AND pss.sem = ? " + 
				"        INNER JOIN " + 
				"    exam.registration r ON pf.sapid = r.sapid AND pf.sem = r.sem " + 
				"WHERE " + 
				"    pf.sem = ? " + 
				"        AND s.consumerProgramStructureId = ? " + 
				"        AND r.month = ? " + 
				"		 AND r.year = ? " + 
				"        AND pf.resultProcessedMonth = ? " + 
				"        AND pf.resultProcessedYear = ? " + 
				"        AND pf.isPass = 'Y' " + 
				"        AND s.programCleared = 'Y'  " + 
				"GROUP BY sapid " + 
				"HAVING COUNT(pf.sapid) = ? " + 
				"ORDER BY SUM(total) DESC";
		List<OpenBadgesTopInProgramDto> list = jdbcTemplate.query(sql, new Object[] {
				consumerProgramStructureId , dto.getSem(), dto.getSem(),
				consumerProgramStructureId,  dto.getAcadMonth(), dto.getYear(), 
				dto.getExamMonth(), dto.getYear(), dto.getSubjectCount()
		}, new BeanPropertyRowMapper<OpenBadgesTopInProgramDto>(OpenBadgesTopInProgramDto.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public Integer getUserIdIfBadgeNotIssued(String sapid, Integer badgeId) {
		String sql = " SELECT  " + 
				"    u.userId " + 
				"FROM " + 
				"    open_badges.users u " + 
				"        LEFT JOIN " + 
				"    open_badges.badge_issued bi ON u.userId = bi.userId AND bi.badgeId = ? " + 
				"WHERE " + 
				"    u.sapid = ? " + 
				"        AND bi.awardedAt IS NULL ";
		return jdbcTemplate.queryForObject(sql, new Object[]{ badgeId, sapid },  Integer.class);
	}
	
	@Transactional(readOnly = true)
	public List<Integer> getMasterKeyListbyCriteriaName(String criteriaName) {
		String sql = " SELECT  " + 
				"    map.consumerProgramStructureId " + 
				"FROM " + 
				"    open_badges.badge b " + 
				"        INNER JOIN " + 
				"    open_badges.badge_masterkey_mapping map ON map.badgeId = b.badgeId " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria c ON c.badgeId = b.badgeId " + 
				"        INNER JOIN " + 
				"    open_badges.badge_criteria_param cp ON cp.criteriaId = c.criteriaId " + 
				"WHERE " + 
				"    cp.criteriaName = ? " + 
				"GROUP BY map.consumerProgramStructureId ";
		List<Integer> list =	jdbcTemplate.query(sql, new Object[] { criteriaName } , new SingleColumnRowMapper<Integer>( Integer.class));
		return list;
	}
	
	@Transactional(readOnly = true)
	public boolean programCleared(String sapid) {
		String sql = "SELECT  " + 
				"    count(*)  " + 
				"FROM " + 
				"    exam.students " + 
				"WHERE " + 
				"    sapid = ? AND programCleared = 'Y' ";
		Integer count =	jdbcTemplate.queryForObject(sql, new Object[]{ sapid  },  Integer.class);
		
		return count > 0 ;
	}
	
	@Transactional(readOnly = true)
	public String getProgramNameBySapId(String sapId) {
		
		String sql = "SELECT  " + 
				"    program " + 
				"FROM " + 
				"    exam.students " + 
				"WHERE " + 
				"    sapid = ? ";
		String programcode = jdbcTemplate.queryForObject(sql, new Object[] { sapId}, String.class);
		return programcode;
	}
	
	@Transactional(readOnly = true)
	public List<OpenBadgesUsersBean> getDashboardBadgeList(Integer userId) {
		List<OpenBadgesUsersBean> list = new ArrayList<OpenBadgesUsersBean>();
		
		StringBuilder sqlString = new StringBuilder("SELECT dateissued, bi.uniquehash, REPLACE(bi.awardedAt, '&', 'and') "
				+ "AS awardedAt, b.badgeName, b.badgeId, b.attachment "
				+ "FROM open_badges.badge b "
				+ "INNER JOIN open_badges.badge_issued bi ON bi.badgeId=b.badgeId "
				+ "WHERE bi.isClaimed=0 AND bi.userId=? ORDER BY dateissued DESC LIMIT 1;");

		list = jdbcTemplate.query(sqlString.toString(), new Object[] { userId }, 
				new BeanPropertyRowMapper<>(OpenBadgesUsersBean.class));

		return list;
	}

}
