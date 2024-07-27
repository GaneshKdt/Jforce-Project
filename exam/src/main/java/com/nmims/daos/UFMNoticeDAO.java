package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UFMIncidentBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.helpers.PaginationHelper;

@Repository("ufmNoticeDAO")
public class UFMNoticeDAO extends BaseDAO{

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}
	@Transactional(readOnly = true)
	public int validateUFMStudentRecord(String sapid , String subject ,String category , String year ,String month){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql =  
				"SELECT count(*) FROM exam.ufm_students where sapid =:sapid   " + 
				" and year=:year   " + 
				" and month =:month    " + 
				" and category =:category  "+
				" and subject =:subject ";
		MapSqlParameterSource query = new MapSqlParameterSource();
		query.addValue("year", year);
		query.addValue("month", month);
		query.addValue("subject", subject);
		query.addValue("sapid", sapid);
		query.addValue("category", category);
		return namedParameterJdbcTemplate.queryForObject(sql, query,Integer.class);
	}
	@Transactional(readOnly = true)
	public List<UFMIncidentBean> getIncidentDetailsForPDF(String sapid, List<String> subject,String year, String month,String category)
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql="SELECT         " + 
				"    ufm_incident_id as 'id', incident_details as 'incident', time_stamp, video_number        " + 
				"FROM        " + 
				"    exam.ufm_incident_details ID        " + 
				"        INNER JOIN        " + 
				"    ufm_students stu ON ID.ufm_incident_id = stu.Id        " + 
				"WHERE        " + 
				"    sapid = :SAPID        " + 
				"        AND subject in  ( :SUBJECT )       " + 
				"        AND year = :YEAR        " + 
				"        AND month = :MONTH        " + 
				"        AND Category = :CATEGORY ";
		MapSqlParameterSource query = new MapSqlParameterSource();
		query.addValue("SAPID", sapid);
		query.addValue("SUBJECT", subject);
		query.addValue("YEAR", year);
		query.addValue("MONTH", month);
		query.addValue("CATEGORY", category);
		List<UFMIncidentBean>listOfIncidents= namedParameterJdbcTemplate.query(sql, query,new BeanPropertyRowMapper<>(UFMIncidentBean.class));
		return listOfIncidents;
		
	}
	
	@Transactional(readOnly = true)
	public UFMNoticeBean getExamDateTime(String sapid, String subject,String year, String month)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql=""
				+ "SELECT examDate,examTime FROM `exam`.`exambookings` "
				+ "WHERE `sapid` = ? and subject=? and year=? and month=? and booked='Y'";
		return jdbcTemplate.queryForObject(
				sql,
				new Object[] {
					sapid, subject, year, month
				},
				new BeanPropertyRowMapper<UFMNoticeBean>(UFMNoticeBean.class)
			);
	}
	
	@Transactional(readOnly = true)
	public int checkIfBookingExists(String sapid, String subject, String examDate, String examTime, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT SUM(numRows) "
			
			+ " FROM ("
				
				+ " SELECT COUNT(*) AS `numRows` "
				+ " FROM `exam`.`exambookings` "
				+ " WHERE `sapid` = ? "
					+ " AND `subject` = ? "
					+ " AND `examDate` = ? "
					+ " AND `examTime` = ? "
					+ " AND `year` = ? "
					+ " AND `month` = ? "
					+ " AND `booked` = 'Y' "

				+ " UNION "
				
				+ " SELECT COUNT(*) AS `numRows` "
				+ " FROM `exam`.`exambookings_history` "
				+ " WHERE `sapid` = ? "
					+ " AND `subject` = ? "
					+ " AND `examDate` = ? "
					+ " AND `examTime` = ? "
					+ " AND `year` = ? "
					+ " AND `month` = ? "
					+ " AND `booked` = 'Y' "

			+ " ) eb ";

		return jdbcTemplate.queryForObject(
			sql,
			new Object[] {
				sapid, subject, examDate, examTime, year, month,
				sapid, subject, examDate, examTime, year, month
			},
			Integer.class
		);
	}
	
	@Transactional(readOnly = false)
	public void upsertShowCause(UFMNoticeBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " INSERT INTO `exam`.`ufm_students` ( "
				+ " `sapid`, `subject`, `year`, "
				+ " `month`, `examDate`, `examTime`, "
				+ " `stage`, `ufmMarkReason`, `showCauseGenerationDate`, `showCauseDeadline`, "
				+ " `createdBy`, `lastModifiedBy`, `category` "
			+ " ) VALUES ( "
				+ " ?, ?, ?, "
				+ " ?, ?, ?, "
				+ " ?, ?, sysdate(), ?, "
				+ " ?, ?, ? "
			+ " ) ";
		// Commented out upsert logic. confirm with shiv if the template is supposed to allow Upsert.
//			+ " ON DUPLICATE KEY "
//			+ " UPDATE `stage` = ?, `ufmMarkReason` = ?, `showCauseDeadline` = ?, `lastModifiedBy` = ? ";

		jdbcTemplate.update(
			sql,
			new Object[] {
				bean.getSapid(), bean.getSubject(), bean.getYear(),
				bean.getMonth(), bean.getExamDate(), bean.getExamTime(),
				bean.getStage(), bean.getUfmMarkReason(), bean.getShowCauseDeadline(),
				bean.getCreatedBy(), bean.getLastModifiedBy(), bean.getCategory(),
				// Commented out upsert logic. confirm with shiv if the template is supposed to allow Upsert.
//				bean.getStage(), bean.getUfmMarkReason(), bean.getShowCauseDeadline(), bean.getLastModifiedBy()
			}	
		);
	}
	

	@Transactional(readOnly = true)
	public StudentExamBean getStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT * FROM exam.students s WHERE sapid = ? AND sem = (SELECT max(sem) FROM exam.students s2 WHERE s2.sapid = s.sapid)";

		return jdbcTemplate.queryForObject(
			sql,
			new Object[] { sapid },
			new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class)
		);
	}
	
	@Transactional(readOnly = false)
	public UFMNoticeBean getUFMBean(String sapid, String subject, String year, String month, String category) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT "
				+ " *, "
				+ " DATE_FORMAT(`showCauseDeadline`, '%Y-%m-%d %H:%i:%s') AS `showCauseDeadline`, "
				+ " DATE_FORMAT(`showCauseSubmissionDate`, '%Y-%m-%d %H:%i:%s') AS `showCauseSubmissionDate`, "
				+ " DATE_FORMAT(`showCauseGenerationDate`, '%Y-%m-%d %H:%i:%s') AS `showCauseGenerationDate`, "
				+ " `c`.`lc` AS `lcName`, "
				+ " `s`.`PrgmStructApplicable` AS `programStructure`, "
				+ " `s`.`emailId` AS `emailId` "
			+ " FROM `exam`.`ufm_students` `ufm` "
			+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `ufm`.`sapid` "
				+ " AND `s`.`sem` = (SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s`.`sapid`)"
			+ " LEFT JOIN `exam`.`centers` `c` ON `c`.`centerCode` = `s`.`centerCode` "
			+ " WHERE `ufm`.`sapid` = ? "
				+ " AND `ufm`.`subject` = ? "
				+ " AND `ufm`.`year` = ? "
				+ " AND `ufm`.`month` = ? "
				+ " AND `ufm`.`category` = ? ";

		return jdbcTemplate.queryForObject(
			sql,
			new Object[] { sapid, subject, year, month, category },
			new BeanPropertyRowMapper<UFMNoticeBean>(UFMNoticeBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public Page<UFMNoticeBean> getStudentsMarkedForShowCause(UFMNoticeBean inputBean, int pageNo, int pageSize)throws Exception {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT *, `c`.`lc` AS `lcName`, `s`.`PrgmStructApplicable` AS `programStructure`, `s`.`centerName` AS `icName` "
			+ " FROM `exam`.`ufm_students` `ufm` "
			+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `ufm`.`sapid` "
				+ " AND `s`.`sem` = (SELECT MAX(`sem`) FROM `exam`.`students` `s2` WHERE `s2`.`sapid` = `s`.`sapid`)"
			+ " LEFT JOIN `exam`.`centers` `c` ON `c`.`centerCode` = `s`.`centerCode` "
			+ " WHERE 1 ";
		
		String countSql = ""
				+ " SELECT count(*) FROM `exam`.`ufm_students` `ufm` "
				+ " Where 1 ";
		
		List<Object> parameters = new ArrayList<Object>();
		if(!StringUtils.isBlank(inputBean.getSapid())) {
			sql += " AND `ufm`.`sapid` = ? ";
			parameters.add(inputBean.getSapid());
			countSql += " AND `ufm`.`sapid` = ? ";
		}

		if(!StringUtils.isBlank(inputBean.getYear())) {
			sql += " AND `ufm`.`year` = ? ";
			parameters.add(inputBean.getYear());
			countSql += " AND `ufm`.`year` = ? ";
		}

		if(!StringUtils.isBlank(inputBean.getMonth())) {
			sql += " AND `ufm`.`month` = ? ";
			parameters.add(inputBean.getMonth());
			countSql += " AND `ufm`.`month` = ? ";
		}
		
		if(!StringUtils.isBlank(inputBean.getSubject())) {
			sql += " AND `ufm`.`subject` = ? ";
			parameters.add(inputBean.getSubject());
			countSql += " AND `ufm`.`subject` = ? ";
		}
		
		Object[] args = parameters.toArray();
		
		PaginationHelper<UFMNoticeBean> pagingHelper = new PaginationHelper<UFMNoticeBean>();
		Page<UFMNoticeBean> page = new Page<UFMNoticeBean>();
		
		page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper<UFMNoticeBean>(UFMNoticeBean.class));
		
		return page;
	}
	
	@Transactional(readOnly = false)
	public void saveDocumentNamesForBean(UFMNoticeBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String subjectsList = "";
		
		List<UFMNoticeBean> commonSubjects = bean.getSubjectsList();
		for (UFMNoticeBean ufmNoticeBean : commonSubjects) {
			subjectsList += "'" + StringEscapeUtils.escapeSql(ufmNoticeBean.getSubject()) + "',";
		}
		subjectsList += "''";
		String sql = ""
			+ " UPDATE `exam`.`ufm_students` " 
			+ " SET `showCauseNoticeURL` = ?, `decisionNoticeURL` = ?, `lastModifiedBy` = ? "
			+ " WHERE `sapid` = ? AND `subject` in ( " + subjectsList + ") AND `year` = ? AND `month` = ? AND `category` = ? ";

		
		jdbcTemplate.update(
			sql,
			new Object[] { 
				bean.getShowCauseNoticeURL(), bean.getDecisionNoticeURL(), bean.getLastModifiedBy(),
				bean.getSapid(), bean.getYear(), bean.getMonth(), bean.getCategory()
			}
		);
		return;
	}
	
	@Transactional(readOnly = true)
	public int checkIfUFMShowCauseRecordExists(String sapid, String subject, String examDate, String examTime, String year, String month, String category) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT count(*) "
			+ " FROM `exam`.`ufm_students` "
			
			+ " WHERE `sapid` = ? "
			+ " AND `subject` = ? "
			+ " AND `year` = ? "
			+ " AND `month` = ? "
			+ " AND `category` = ? "
			+ " AND `stage` IN ( ?, ? ) ";
		return jdbcTemplate.queryForObject(
			sql,
			new Object[] {
				sapid, subject, year, month, category,
				UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE, UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED 
			},
			Integer.class
		);
	}
	
	@Transactional(readOnly = false)
	public void updateUFMActionRecord(UFMNoticeBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " UPDATE `exam`.`ufm_students` "
			+ " SET `stage` = ?, `lastModifiedBy` = ?, `lastModifiedDate` = sysdate() "
			+ " WHERE `sapid` = ? "
			+ " AND `subject` = ? "
			+ " AND `year` = ? "
			+ " AND `month` = ? "
			+ " AND `category` = ? ";

		jdbcTemplate.update(
			sql,
			new Object[] {
				bean.getStage(),bean.getLastModifiedBy(),
				bean.getSapid(), bean.getSubject(), bean.getYear(), bean.getMonth(), bean.getCategory()
			}
		);
		
		return;
	}
	
	@Transactional(readOnly = true)
	public List<UFMNoticeBean> getListOfUFMMarkedSubjectsForStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM `exam`.`ufm_students` WHERE `sapid` = ? ";
		
		return jdbcTemplate.query(
			sql,
			new Object[] {sapid},
			new BeanPropertyRowMapper<UFMNoticeBean>(UFMNoticeBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<UFMNoticeBean> getListOfShowCauseSubjectsForStudentYearMonth(String sapid, String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT * FROM `exam`.`ufm_students` WHERE `sapid` = ? AND `year` = ? AND `month` = ? ";
		
		return jdbcTemplate.query(
			sql,
			new Object[] { sapid, year, month },
			new BeanPropertyRowMapper<UFMNoticeBean>(UFMNoticeBean.class)
		);
	}
	
	@Transactional(readOnly = false)
	public void setStudentResponse(UFMNoticeBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`ufm_students` "
				+ " SET `stage` = ?, "
				+ " `showCauseResponse` = ?, "
				+ " `showCauseSubmissionDate` = sysdate(), "
				+ " `lastModifiedBy` = ?, "
				+ " `lastModifiedDate` = sysdate() "
				+ " WHERE `sapid` = ? AND `year` = ? AND `month` = ? AND `subject` = ? AND `category` = ? ";
		jdbcTemplate.update(
			sql,
			new Object[] {
				UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED, bean.getShowCauseResponse(), bean.getLastModifiedBy(),
				bean.getSapid(), bean.getYear(), bean.getMonth(), bean.getSubject(), bean.getCategory() }
		);
	}

	@Transactional(readOnly = true)
	public boolean checkIfStudentMarkedForUFMInCurrentCycle(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
			+ " SELECT COUNT(*) "
			+ " FROM `exam`.`ufm_students` `ufm` "
			+ " INNER JOIN `exam`.`examorder` `eo` ON `eo`.`year` = `ufm`.`year` AND `eo`.`month` = `ufm`.`month` "
			+ " WHERE `sapid` = ? "
			// TODO: Replace with live settings check
			+ " AND `eo`.`order` = ( SELECT MAX(`order`) FROM `exam`.`examorder` WHERE `timeTableLive` = 'Y' ) ";
		int rowCount = (int)jdbcTemplate.queryForObject(sql,new Object[] {sapid}, Integer.class);
		
		return rowCount > 0;
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getPendingRIARecords(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT  " + 
				"       m.sapid, m.subject, m.year, m.month  " + 
				"FROM " + 
				"    exam.ufm_students ufm " + 
				"        INNER JOIN " + 
				"    exam.marks m ON m.sapid = ufm.sapid " + 
				"        AND m.year = ufm.year " + 
				"        AND m.month = ufm.month " + 
				"        AND m.subject = ufm.subject " + 
				"WHERE " + 
				"    m.year = ? AND m.month = ? " + 
				"        AND ufm.stage IN (? , ?) " + 
				"        AND m.writenscore <> 'RIA' ";
		
		return jdbcTemplate.query(
			sql,
			new Object[] { year, month, UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_STUDENT_RESPONDED, UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE },
			new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class)
		);
	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getPendingNVRecords(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT  " + 
				"       m.sapid, m.subject, m.year, m.month " + 
				"FROM " + 
				"    exam.ufm_students ufm " + 
				"        INNER JOIN " + 
				"    exam.marks m ON m.sapid = ufm.sapid " + 
				"        AND m.year = ufm.year " + 
				"        AND m.month = ufm.month " + 
				"        AND m.subject = ufm.subject " + 
				"WHERE " + 
				"    m.year = ? AND m.month = ? " + 
				"       AND ufm.stage = ? " + 
				"        AND m.writenscore <> 'NV' ";
		
		return jdbcTemplate.query(
			sql,
			new Object[] { year, month, UFMNoticeBean.UFM_STAGE_PENALTY_ISSUED},
			new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class)
		);
	}
	
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getPendingScoredRecords(String year, String month) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT  " + 
				"       m.sapid, m.subject, m.year, m.month, m.writenscore, m.writtenBeforeRIANV  " + 
				"FROM " + 
				"    exam.ufm_students ufm " + 
				"        INNER JOIN " + 
				"    exam.marks m ON m.sapid = ufm.sapid " + 
				"        AND m.year = ufm.year " + 
				"        AND m.month = ufm.month " + 
				"        AND m.subject = ufm.subject " + 
				"WHERE " + 
				"    m.year = ? AND m.month = ? " + 
				"       AND ufm.stage = ? " + 
				"        AND m.writenscore in ('RIA', 'NV')";
		
		return jdbcTemplate.query(
			sql,
			new Object[] { year, month, UFMNoticeBean.UFM_STAGE_WARNING_ISSUED},
			new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class)
		);
	}
	
	
	@Transactional(readOnly = false)
	public int[] applyRIA(List<StudentMarksBean> list, String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`marks`  " + 
				"SET  " + 
				"    writtenBeforeRIANV = writenscore, " + 
				"    `writenscore` = 'RIA', " + 
				"    processed = 'N', " + 
				"    lastModifiedBy = ?, " + 
				"    lastModifiedDate = CURRENT_TIMESTAMP() " + 
				"WHERE " + 
				"    sapid = ?  AND year = ? " + 
				"        AND month = ? " + 
				"        AND subject = ? " ;
		return jdbcTemplate.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						StudentMarksBean bean = list.get(index);
						ps.setString(1, lastModifiedBy);
						ps.setString(2, bean.getSapid());
						ps.setString(3, bean.getYear());
						ps.setString(4, bean.getMonth());
						ps.setString(5, bean.getSubject());
					}
					
					@Override
					public int getBatchSize() {
						return list.size();
					}
			});
	}
	
	
	@Transactional(readOnly = false)
	public int[] applyNV(List<StudentMarksBean> list, String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`marks`  " + 
				"SET  " + 
				"    `writenscore` = 'NV', " + 
				"    processed = 'N', " + 
				"    lastModifiedBy = ?, " + 
				"    lastModifiedDate = CURRENT_TIMESTAMP() " + 
				"WHERE " + 
				"    sapid = ?  AND year = ? " + 
				"        AND month = ? " + 
				"        AND subject = ? " ;
		return jdbcTemplate.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						StudentMarksBean bean = list.get(index);
						ps.setString(1, lastModifiedBy);
						ps.setString(2, bean.getSapid());
						ps.setString(3, bean.getYear());
						ps.setString(4, bean.getMonth());
						ps.setString(5, bean.getSubject());
					}
					
					@Override
					public int getBatchSize() {
						return list.size();
					}
			});
	}
	
	@Transactional(readOnly = false)
	public int[] applyScored(List<StudentMarksBean> list, String lastModifiedBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " UPDATE `exam`.`marks`  " + 
				"SET  " + 
				"    `writenscore` = writtenBeforeRIANV, " + 
				"    writtenBeforeRIANV = null, " + 
				"    processed = 'N', " + 
				"    lastModifiedBy = ?, " + 
				"    lastModifiedDate = CURRENT_TIMESTAMP() " + 
				"WHERE " + 
				"    sapid = ?  AND year = ? " + 
				"        AND month = ? " + 
				"        AND subject = ? " ;
		return jdbcTemplate.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						StudentMarksBean bean = list.get(index);
						ps.setString(1, lastModifiedBy);
						ps.setString(2, bean.getSapid());
						ps.setString(3, bean.getYear());
						ps.setString(4, bean.getMonth());
						ps.setString(5, bean.getSubject());
					}
					
					@Override
					public int getBatchSize() {
						return list.size();
					}
			});
	}
	@Transactional(readOnly = false)
	public int insertIntoIncidentDetails(UFMIncidentBean incidentDetails){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "INSERT INTO exam.ufm_incident_details VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql,new Object[] {incidentDetails.getUfm_student_id(),incidentDetails.getIncident(),incidentDetails.getTime_Stamp(),incidentDetails.getVideo_Number()}); 
}

	public List<UFMNoticeBean> getUFMStudentDetailsForReport(String year, String month, String category) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "SELECT      " + 
				"    *     " + 
				"FROM     " + 
				"    exam.ufm_students     " + 
				"WHERE     " + 
				"    year = :year     " + 
				"    AND     " +
				"    month = :month     " + 
				"    AND category = :category ";
		MapSqlParameterSource query = new MapSqlParameterSource();
		query.addValue("year", year);
		query.addValue("month", month);
		query.addValue("category", category);
		return (List<UFMNoticeBean>)namedParameterJdbcTemplate.query(sql, query,new BeanPropertyRowMapper<>(UFMNoticeBean.class));
	}

	public ArrayList<UFMIncidentBean> getincidentDetailsForReport(List<Integer> listOfId) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "    " + 
				"SELECT     " + 
				"    incident_details as 'incident',"
				+ "   ufm_incident_id as 'id' ,"
				+ " time_stamp  ,"
				+ "video_number " + 
				"FROM    " + 
				"    exam.ufm_incident_details    " + 
				"WHERE    " + 
				"    ufm_incident_id IN (:listOfId)";
		MapSqlParameterSource query = new MapSqlParameterSource();
		query.addValue("listOfId", listOfId);
		return (ArrayList<UFMIncidentBean>)namedParameterJdbcTemplate.query(sql, query , new BeanPropertyRowMapper<>(UFMIncidentBean.class));
	}
}