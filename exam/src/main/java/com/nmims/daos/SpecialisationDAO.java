package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.ObjArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.Specialisation;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.helpers.MailSender;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpecialisationDAO {


	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	//@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR = "2023";

	//@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH = "Apr";
	
	@Value("${NEXT_MBAWX_ACAD_YEAR}")
	private String NEXT_MBAWX_ACAD_YEAR;

	@Value("${NEXT_MBAWX_ACAD_MONTH}")
	private String NEXT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getAllSpecialisation(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> specialisationList = null;
		/*
		 * updated to fetch specialization_type of only applicable type
		 */
		String sql = " SELECT * FROM exam.specialization_type where id IN (9,10,11,12,13) ";
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getAllSpecialisationSubjects(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String sql =" SELECT  "
				+ "    pss.*, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "	FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "	WHERE "
				+ "    sem = 3 "
				/*
				 * updated to fetch specialization_type of only applicable type
				 */
				+ "        AND st.id IN (9,10,11,12,13) "
				+ " ORDER BY pss.specializationType + 0 ASC";
		
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
	}

	@Transactional(readOnly = false)
	public boolean saveStudentSpecialisationDetails(Specialisation specialisation) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " INSERT INTO lti.mba_specialisation_details " + 
					  " (sapid, specializationType, specialisation1, specialisation2, term, program_sem_subject_id, " +
					  "  createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
					  " VALUES ( ?,?,?,?,?,?,?,sysdate(),?, sysdate()) " ;
		
		try {
			jdbcTemplate.update(sql, new Object[] {
					specialisation.getSapid(),
					specialisation.getSpecializationType(),
					specialisation.getSpecialisation1(),
					specialisation.getSpecialisation2(),
					specialisation.getTerm(),
					specialisation.getProgram_sem_subject_id(),
					specialisation.getSapid(),
					specialisation.getSapid()
			});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean insertTimeBoundStudentMapping(Specialisation specialisation) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" INSERT INTO lti.timebound_user_mapping_staging " +
				 	" ( userId, timebound_subject_config_id, `program_sem_sub_id`, `specializationType`, "
				 	+ "role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
				 	" VALUES (?, ?, ?, ?, 'Student', ?, sysdate(), ?, sysdate());";
		try {
			jdbcTemplate.update(sql, new Object[] {
					specialisation.getSapid(),
					specialisation.getTimeBoundId(),
					specialisation.getId(),
					specialisation.getSpecializationType(),
					specialisation.getSapid(),
					specialisation.getSapid(),
					
			});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = false)
	public void updateSpecialisationDetails(Specialisation specialisation) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" UPDATE lti.mba_specialisation_details " +
				 	" SET specializationType = ?, specialisation1 = ?, specialisation2 = ?, " +
				 	" lastModifiedBy = ?, lastModifiedDate = sysdate() WHERE sapid = ? ";
		try {
			jdbcTemplate.update(sql, new Object[] {
					specialisation.getSpecializationType(),
					specialisation.getSpecialisation1(),
					specialisation.getSpecialisation2(),
					specialisation.getSapid(),
					specialisation.getSapid(),
			});
			//return true;
		} catch (Exception e) {
			
			//return false;
		}
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> isSpecialisationDone2(String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
		String sql =" SELECT " + 
					"    msd.*, pss.subject " + 
					" FROM " + 
					"    lti.mba_specialisation_details msd " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss " + 
					" WHERE " + 
					"    pss.id = msd.program_sem_subject_id " + 
					"        AND msd.sapid = ? " ;
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getPreviousElectiveDetails(String sapid, int maxTerm, String year, String month) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
		
		/*
		String sql =" SELECT  " + 
					"    msd.*, st.id as specializationType, pss.subject, pss.sem AS term, pss.id " + 
					" FROM " + 
					"    lti.timebound_user_mapping tum " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
					"        INNER JOIN " + 
					"    lti.mba_specialisation_details msd ON msd.sapid = tum.userId " + 
					"	 	 INNER JOIN " +
					"	 exam.specialization_type st ON st.id = pss.specializationType " +
					"        AND tum.userId = ? " + 
					//Commented as now no Term wise TimeBound entry will be there 
					//"		 AND pss.sem = ? " +
					" 		 AND ssc.acadyear = '"+year+"' and ssc.acadMonth='"+month+"'" +
					"        AND pss.subject IN (SELECT  " + 
					"            subject " + 
					"        FROM " + 
					"            exam.program_sem_subject " + 
					"        WHERE " + 
					"            specializationType IN (SELECT  " + 
					"                    id " + 
					"                FROM " + 
					"                    exam.specialization_type " + 
					"                WHERE " + 
					"                    specializationType <> 'No Specialization')) " ;
		*/
		
		String sql = "SELECT  "
				+ "    msd.*, "
				+ "    sum.specializationType, "
				+ "    pss.subject, "
				+ "    pss.sem AS term, "
				+ "    pss.id "
				+ "FROM "
				+ "    lti.timebound_user_mapping tum "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    lti.mba_specialisation_details msd ON msd.sapid = tum.userId "
				+ "        INNER JOIN "
				+ "    lti.specialization_user_mapping sum ON ssc.id = sum.student_subject_config_id AND sum.userId = tum.userId "
				+ "WHERE "
				+ "    tum.userId = ? "
				+ "        AND ssc.acadyear = ? "
				+ "        AND ssc.acadMonth = ?";
		
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[]{ sapid, year, month },
					new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> isSpecialisationDone(String sapid, int maxTerm, String year, String month) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();

		
		String sql = "SELECT  "
				+ "    msd.*, st.specializationType, pss.subject, pss.sem AS term "
				+ "FROM "
				+ "    lti.timebound_user_mapping_staging tum "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    lti.mba_specialisation_details msd ON msd.sapid = tum.userId "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON st.id = tum.specializationType "
				+ "        AND tum.userId = ? "
				+ "        AND ssc.acadyear = ? "
				+ "        AND ssc.acadMonth = ? "
				+ "        AND pss.subject IN (SELECT  "
				+ "            subject "
				+ "        FROM "
				+ "            exam.program_sem_subject "
				+ "        WHERE "
				+ "            specializationType IN (SELECT  "
				+ "                    id "
				+ "                FROM "
				+ "                    exam.specialization_type "
				+ "                WHERE "
				/*
				 * updated to fetch specialization_type of only applicable type
				 */
				+ "                    id IN (9,10,11,12,13) ) )";
		
		/*
		String sql = "SELECT  "
					+ "    msd.*, "
					+ "    sum.specializationType, "
					+ "    pss.subject, "
					+ "    pss.sem AS term, "
					+ "    pss.id "
					+ "FROM "
					+ "    lti.timebound_user_mapping tum "
					+ "        INNER JOIN "
					+ "    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id "
					+ "        INNER JOIN "
					+ "    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
					+ "        INNER JOIN "
					+ "    lti.mba_specialisation_details msd ON msd.sapid = tum.userId "
					+ "        INNER JOIN "
					+ "    lti.specialization_user_mapping sum ON ssc.id = sum.student_subject_config_id AND sum.userId = tum.userId "
					+ "WHERE "
					+ "    tum.userId = ? "
					+ "        AND ssc.acadyear = ? "
					+ "        AND ssc.acadMonth = ?";
		*/
		
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[]{ sapid, year, month },
					new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}

	@Transactional(readOnly = true)
	public Specialisation getExistingSpecialisationDetails(String sapid) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM lti.mba_specialisation_details WHERE sapid = ? ";

		Specialisation specialisation = (Specialisation) jdbcTemplate.queryForObject(sql, new Object[]{sapid}, 
					new BeanPropertyRowMapper(Specialisation.class));
		
		return specialisation;

	}

	@Transactional(readOnly = false)
	public boolean updateSpecilisationDetails (Specialisation specialisation) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" UPDATE lti.mba_specialisation_details  " + 
					" SET  " + 
					"    specializationType = ?, " + 
					"    specialisation1 = ?, " + 
					"    specialisation2 = ?, " + 
					"    lastModifiedBy = ?, " + 
					"    lastModifiedDate = sysdate() " + 
					" WHERE " + 
					"    sapid = ? ";
		try {
			jdbcTemplate.update(sql, new Object[]{ 
					specialisation.getSpecializationType(),
					specialisation.getSpecialisation1(),
					specialisation.getSpecialisation2(),
					specialisation.getSapid(),
					specialisation.getSapid()
			});
			return true;
		} catch (Exception e) {
			//mailer.mailStackTrace("Unable to update Specialisation details on portal", e);
			return false;
		}
	}

	@Transactional(readOnly = false)
	public boolean insertExistingSpecialisationIntoHistory (Specialisation specialisation) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " INSERT INTO lti.mba_specialisation_details_history " + 
					  " (sapid, specializationType, specialisation1, specialisation2, " +
					  "  createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
					  " VALUES ( ?,?,?,?,?,sysdate(),?, sysdate()) " ;
		try {
			jdbcTemplate.update(sql, new Object[] {
					specialisation.getSapid(),
					specialisation.getSpecializationType(),
					specialisation.getSpecialisation1(),
					specialisation.getSpecialisation2(),
					specialisation.getSapid(),
					specialisation.getSapid()
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	
	}

	@Transactional(readOnly = false)
	public Long insertStudentSpecialisationDetailsinSR(final Specialisation specialisation) {
		
		Long srId = null;
		jdbcTemplate = new JdbcTemplate(dataSource);
		final String sql =  " INSERT INTO portal.service_request (serviceRequestType, sapId, amount, tranDateTime, tranStatus, "
					+ "	requestStatus, category, hasDocuments,  refundStatus, createdBy, createdDate, lastModifiedBy, lastModifiedDate )"
					+ " VALUES ( 'Change in Specialisation', ?, 0, sysdate(), 'Free', 'Submitted', 'Academics', 'N', 'N', "
					+ " ?, sysdate(), ?, sysdate() ); ";			 
		
		try {
			
			PreparedStatementCreator psc = new PreparedStatementCreator() { 
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					ps.setString(1, specialisation.getSapid());
					ps.setString(2, specialisation.getSapid());
					ps.setString(3, specialisation.getSapid());
					return ps;
				}
			};
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(psc, keyHolder);
			srId = keyHolder.getKey().longValue();
			return srId;
			
		} catch (Exception e) {
			return srId;
		}
	}

	@Transactional(readOnly = true)
	public String isServiceRequestRaisedForSpecialisation(String sapId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =  " SELECT COUNT(*) from portal.service_request "
					+ " WHERE sapId = ? AND serviceRequestType = 'Change in Specialisation' AND requestStatus = 'Submitted'";
		try {
			int count = jdbcTemplate.queryForObject(sql, new Object[] { sapId }, Integer.class);
			if( count > 0 ) {
				return "true";
			}
		}catch (Exception e) {
			
		}
		
		return "false";
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public boolean isSpecialisationApplicable(StudentExamBean student, boolean isCurrentSem) {
		
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String currentSem = "";
		if (isCurrentSem) {
			currentSem = "		 AND r.year = "+CURRENT_MBAWX_ACAD_YEAR+" AND r.month = '"+CURRENT_MBAWX_ACAD_MONTH+"' ";
		}
		
		String sql =" SELECT * FROM " + 
					"    exam.mba_wx_exam_live_setting els " + 
					"        INNER JOIN " + 
					"    exam.registration r ON r.consumerProgramStructureId = els.consumerProgramStructureId " + 
					" WHERE " + 
					"    r.sapid = ? " + 
					currentSem +
					"        AND r.sem BETWEEN 2 AND 3 " + 
					"        AND CURRENT_TIMESTAMP() > startTime " + 
					"        AND CURRENT_TIMESTAMP() < endTime " + 
					"		 AND acadsYear = "+CURRENT_MBAWX_ACAD_YEAR+" AND acadsMonth = '"+CURRENT_MBAWX_ACAD_MONTH+"' "+
					"        AND type = 'Specialisation Elective Selection'" ;
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[] { student.getSapid() }, 
					new BeanPropertyRowMapper(Specialisation.class));
			if( specialisationList.size() > 0 ) {
				return true;
			}
		}catch (Exception e) {
			
		}
		
		return false;
	}

	@Transactional(readOnly = true)
	public boolean isSpecialisationApplicableTerm5(StudentExamBean student, boolean isCurrentSem) {
		
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String currentSem = "";
		if (isCurrentSem) {
			currentSem = "		 AND r.year = "+CURRENT_MBAWX_ACAD_YEAR+" AND r.month = '"+CURRENT_MBAWX_ACAD_MONTH+"' ";
		}
		
		String sql =" SELECT * FROM " + 
					"    exam.mba_wx_exam_live_setting els " + 
					"        INNER JOIN " + 
					"    exam.registration r ON r.consumerProgramStructureId = els.consumerProgramStructureId " + 
					" WHERE " + 
					"    r.sapid = ? " + 
					currentSem +
					"        AND r.sem=4 " + 
					"        AND CURRENT_TIMESTAMP() > startTime " + 
					"        AND CURRENT_TIMESTAMP() < endTime " + 
					"		 AND acadsYear = "+CURRENT_MBAWX_ACAD_YEAR+" AND acadsMonth = '"+CURRENT_MBAWX_ACAD_MONTH+"' "+
					"        AND type = 'Specialisation Elective Selection Term-5'" ;
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[] { student.getSapid() }, 
					new BeanPropertyRowMapper(Specialisation.class));
			
			if( specialisationList.size() > 0 ) {
				return true;
			}
		}catch (Exception e) {
			
		}
		
		return false;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public StudentExamBean getSingleStudentsData(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		StudentExamBean student = new StudentExamBean();
		try {
			String sql = "SELECT * FROM exam.students where sapid = ? ";
			student = (StudentExamBean) jdbcTemplate.queryForObject(sql,new Object[] { sapid }, 
									new BeanPropertyRowMapper(StudentExamBean.class));
		} catch (Exception e) {
			
		}
		return student;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ServiceRequestBean getSingleSR(Long srId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ServiceRequestBean srBean = new ServiceRequestBean();
		try {
			String sql = "SELECT * FROM portal.service_request where id = ? ";
			srBean =(ServiceRequestBean) jdbcTemplate.queryForObject(sql, new Object[] {srId},
						new BeanPropertyRowMapper(ServiceRequestBean.class));
		} catch (Exception e) {
			
		}
		return srBean;
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getSpecialisationTimeBoundIds(String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> specialisationList = new ArrayList<String>();
		
		String sql =" SELECT  " + 
					"    tum.timebound_subject_config_id " + 
					" FROM " + 
					"    lti.timebound_user_mapping_staging tum " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
					"        INNER JOIN " + 
					"    lti.mba_specialisation_details msd ON msd.sapid = tum.userId " + 
					"	 	 INNER JOIN " +
					"	 exam.specialization_type st ON st.id = pss.specializationType " +
					"        AND tum.userId = ? " + 
					" 		 AND ssc.acadyear = ? " +
					"	     AND ssc.acadMonth = ? " +
					"        AND pss.subject IN (SELECT  " + 
					"            subject " + 
					"        FROM " + 
					"            exam.program_sem_subject " + 
					"        WHERE " + 
					"            specializationType IN (SELECT  " + 
					"                    id " + 
					"                FROM " + 
					"                    exam.specialization_type " + 
					"                WHERE " + 
					"                    specializationType <> 'No Specialization' " +
					/*
					 * updated to fetch specialization_type of only applicable type
					 */
					"                   AND id IN (9,10,11,12,13) ) ) " ;
		try {
			specialisationList = (ArrayList<String>) jdbcTemplate.query(sql, new Object[]{sapid, NEXT_MBAWX_ACAD_YEAR, NEXT_MBAWX_ACAD_MONTH},
					new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}
	
	/*
	public boolean updateExistingTimeBoundMapping(String sapid, ArrayList<String> timeBoundIds) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String idList = StringUtils.join(timeBoundIds, ",");
		
		String sql =  " UPDATE lti.timebound_user_mapping SET userId='"+sapid+"0_deleted' "
					+ " WHERE userId ='"+sapid+"' and timebound_subject_config_id in (" + idList +")";
		try {
			jdbcTemplate.update(sql, new Object[] {});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}
	*/

	@Transactional(readOnly = false)
	public boolean insertExistingTimeBoundMappingIntoHistory(String sapid, ArrayList<String> timeBoundIds) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String idList = StringUtils.join(timeBoundIds, ",");
		
		String sql =" INSERT INTO lti.timebound_user_mapping_history " +
			 		" ( userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
			 		" VALUES (?, ?, 'Student', ?, sysdate(), ?, sysdate()) ";
		
		String sql2 = "DELETE FROM lti.timebound_user_mapping_staging WHERE userId = ? and timebound_subject_config_id = ? ";
	
		try {
			for (String timeBoundId : timeBoundIds) {
				//Save date in history table
				jdbcTemplate.update(sql, new Object[] {sapid,timeBoundId,sapid,sapid});
				//delete data from main table
				jdbcTemplate.update(sql2, new Object[] {sapid,timeBoundId});
			}
			return true;
			
		} catch (Exception e) {
			
			return false;
		}
	}
	
	//Old Query
	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getElectiveReport(Specialisation specialisation, String authorizedCenterCodes) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> electiveStudentList = null;
		String sql =" SELECT " + 
					"    userId AS sapid, " + 
					"    ssc.id AS timeBoundId, " + 
					"    st.specializationType, " + 
					"    ssc.acadYear, " + 
					"    ssc.acadMonth, " + 
					"    pss.subject, " + 
					"    r.sem AS term " + 
					" FROM " + 
					"    lti.timebound_user_mapping tum " + 
					"        INNER JOIN " + 
					"    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
					"        INNER JOIN " + 
					"    exam.specialization_type st ON st.id = pss.specializationType " +
					"		 INNER JOIN " +
					"	 exam.registration r on r.sapid = tum.userId " +
					"	 	 AND r.month = ? AND r.year = ? and r.sem = ? " +
					"		 INNER JOIN " +
					"	 exam.students s on s.sapid = tum.userId " +
					// TimeBound can be add for Term 3 or Term 4
					"		 AND (pss.sem = 3 OR pss.sem = 4) " + 
					"        AND ssc.acadMonth = ? " + 
					"        AND ssc.acadYear = ? " + 
					"        AND tum.role = 'Student' ";
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
		 
			electiveStudentList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[] {specialisation.getAcadMonth(), specialisation.getAcadYear(), 
								specialisation.getTerm(), specialisation.getAcadMonth(), specialisation.getAcadYear()}, new BeanPropertyRowMapper(Specialisation.class));
		
			return electiveStudentList;
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getElectiveCompletedReport(Specialisation specialisation, String authorizedCenterCodes) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> electiveStudentList = null;
		StringBuffer termSql = new StringBuffer();
		if (specialisation.getTerm().equalsIgnoreCase("5")) {
			termSql.append(" pss.sem = 5 ");
		}else {
			termSql.append(" (pss.sem = 3 OR pss.sem = 4) ");
		}
		
		if(!StringUtils.isBlank(specialisation.getConsumerProgramStructureId()))
			termSql.append(" AND s.consumerProgramStructureId = "+specialisation.getConsumerProgramStructureId());
		
		String sql =" SELECT " + 
					"    userId AS sapid," + 
					"    CONCAT(s.firstName,' ', s.lastName) AS name, " + 
					"    s.emailId," + 
					"    s.mobile," + 
					"    msd.specializationType," + 
					"    pss.subject," + 
					"    st.specializationType AS subject_specialization," + 
					"	 msd.specialisation1, " +
					"	 msd.specialisation2, " +
					"    ssc.acadYear," + 
					"    ssc.acadMonth" + 
					" FROM" + 
					"    lti.timebound_user_mapping_staging tum" + 
					"        INNER JOIN" + 
					"    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id" + 
					"        INNER JOIN" + 
					"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
					"        INNER JOIN" + 
					"    exam.specialization_type st ON st.id = tum.specializationType" + 
					"        INNER JOIN" + 
					"    lti.mba_specialisation_details msd ON tum.userId = msd.sapid" + 
					"        INNER JOIN" + 
					"    exam.students s ON s.sapid = tum.userId " +
					" WHERE " + 
					  termSql + 
					"        AND ssc.acadMonth = ? " + 
					"        AND ssc.acadYear = ? " + 
					"        AND tum.role = 'Student'  ";
		
		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
		}
	
		electiveStudentList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[] {specialisation.getNextMonth(),
					specialisation.getNextYear()}, new BeanPropertyRowMapper(Specialisation.class));
		
		return electiveStudentList;
	}
	
	//Old
	@Transactional(readOnly = true)
	public ArrayList<StudentExamBean> getElectivePendingReport(Specialisation specialisation){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<StudentExamBean> electivePendingStudentList = null;
		String sql =" SELECT  " + 
					"    r.*, timeBoundMapping.* " + 
					" FROM " + 
					"    (SELECT  " + 
					"        r.*, s.firstName, s.lastName, s.emailId, s.mobile " + 
					"    FROM " + 
					"        exam.registration r " + 
					"    INNER JOIN exam.students s ON s.sapid = r.sapid " + 
					"    WHERE " + 
					"        r.program = 'MBA - WX'  AND r.sem = ? AND r.month = ? AND r.year = ?  " + 
					"    GROUP BY r.sapid) r " + 
					"        LEFT JOIN " + 
					"    (SELECT  " + 
					"        tum.timebound_subject_config_id, tum.userId " + 
					"    FROM " + 
					"        lti.timebound_user_mapping tum " + 
					"    INNER JOIN lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id " + 
					"    INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
					"    WHERE " + 
					//TimeBound can be add for Term 3 or Term 4
					"         (pss.sem = 4 or pss.sem = 3) AND ssc.acadMonth = ? AND ssc.acadYear = ? ) " + 
					"			timeBoundMapping ON timeBoundMapping.userId = r.sapid " + 
					" 	WHERE " + 
					"    	timeBoundMapping.timebound_subject_config_id IS NULL " + 
					"";
		
		electivePendingStudentList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql, new Object[] { specialisation.getTerm(), specialisation.getAcadMonth(),
				specialisation.getAcadYear(),specialisation.getAcadMonth(), specialisation.getAcadYear()}, new BeanPropertyRowMapper(StudentExamBean.class));
		
		return electivePendingStudentList;

	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getElectivesPendingReport(Specialisation specialisation){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> electivePendingStudentList = null;
		StringBuffer termSql = new StringBuffer();
		StringBuffer currentTermSql = new StringBuffer();
		if (specialisation.getTerm().equalsIgnoreCase("5")) {
			termSql.append(" AND pss.sem = 5 ");
			currentTermSql.append(" AND sem = 4 ");
		}else {
			termSql.append(" AND (pss.sem = 3 OR pss.sem = 4) ");
			currentTermSql.append(" AND sem IN (2 , 3) ");
		}
		
		if(!StringUtils.isBlank(specialisation.getConsumerProgramStructureId()))
			currentTermSql.append(" AND consumerProgramStructureId = "+specialisation.getConsumerProgramStructureId());
		
		String sql =" SELECT " + 
					"    s.sapid," + 
					"    CONCAT(s.firstName,' ', s.lastName) AS name," + 
					"    emailId," + 
					"    mobile," + 
					"    msd.specializationType," +
					"	 msd.specialisation1," +
					"	 msd.specialisation2 " +
					" FROM" + 
					"    exam.students s" + 
					"        INNER JOIN" + 
					"    lti.mba_specialisation_details msd ON s.sapid = msd.sapid" + 
					" WHERE" + 
					"    s.sapid IN (SELECT " + 
					"            sapid" + 
					"        FROM" + 
					"            exam.registration" + 
					"        WHERE" + 
					"            program = 'MBA - WX' " +
					"				 AND month = ? AND year = ? " + 
									 currentTermSql		+
					"                AND sapid NOT IN (SELECT " + 
					"                    userId AS sapid" + 
					"                FROM" + 
					"                    lti.timebound_user_mapping_staging tum" + 
					"                        INNER JOIN" + 
					"                    lti.student_subject_config ssc ON ssc.id = tum.timebound_subject_config_id" + 
					"                        INNER JOIN" + 
					"                    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
					"                        INNER JOIN" + 
					"                    exam.specialization_type st ON st.id = pss.specializationType" + 
					"                        INNER JOIN" + 
					"                    exam.students s ON s.sapid = tum.userId" + 
											 termSql + 
					"                        AND ssc.acadMonth = ? " + 
					"                        AND ssc.acadYear = ? " + 
					"                        AND tum.role = 'Student'" + 
					"                GROUP BY userId))";
		
		electivePendingStudentList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[] { specialisation.getAcadMonth(), specialisation.getAcadYear(),
				specialisation.getNextMonth(), specialisation.getNextYear()}, new BeanPropertyRowMapper(Specialisation.class));
		
		return electivePendingStudentList;

	}

	@Transactional(readOnly = true)
	public int getMaxSemForCuurentCycle(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		
		try {
			String sql =  " SELECT MAX(sem) FROM exam.registration WHERE year = '"+CURRENT_MBAWX_ACAD_YEAR+"' AND month = '"+CURRENT_MBAWX_ACAD_MONTH+"' "
						+ " AND sapid = ? ";
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		} catch (Exception e) {
			
		}
		
		return count;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getRepeatStudentElectives(String sapid) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
		
		String sql =" SELECT  " + 
					"    msd.*, st.specializationType, pss.subject, pss.sem AS term, pss.id " + 
					" FROM " + 
					"    lti.mba_specialisation_details msd " + 
					"        INNER JOIN " + 
					"    exam.mba_passfail mp ON msd.sapid = mp.sapid " + 
					"        INNER JOIN " + 
					"    exam.program_sem_subject pss ON mp.prgm_sem_subj_id = pss.id " + 
					"        INNER JOIN " + 
					"    exam.specialization_type st ON st.id = pss.specializationType " + 
					" WHERE " + 
					"    mp.sapid = ? " + 
					" GROUP BY pss.subject " ;
		try {
			specialisationList = (ArrayList<Specialisation>) jdbcTemplate.query(sql, new Object[]{sapid},new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return specialisationList;
	}

	@Transactional(readOnly = true)
	public int getMaxSem(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		
		try {
			String sql =  " SELECT MAX(sem) FROM exam.registration WHERE sapid = ? ";
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		} catch (Exception e) {
			
		}
		
		return count;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getAllStagingTableTimeBoundMapping(TimeBoundUserMapping mappingBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> timeBoundMappingList = new ArrayList<Specialisation>();
		String termSql = "";
		
		if (mappingBean.getSem().equalsIgnoreCase("5")) {
			termSql = " AND sem = 5 ";
		}else {
			termSql = " AND sem IN (3 , 4) ";
		}
		
		String sql =" SELECT "
				+ "    userId AS sapid, timebound_subject_config_id AS timeBoundId, "
				+ "	   tum.program_sem_sub_id AS program_sem_subject_id, specializationType "
				+ " FROM "
				+ "    lti.timebound_user_mapping_staging tum "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON tum.timebound_subject_config_id = ssc.id "
				+ "        INNER JOIN "
				+ "    exam.registration r ON tum.userId = r.sapid "
				+ "        AND ssc.acadYear = r.year "
				+ "        AND ssc.acadMonth = r.month "
				+ " WHERE "
				+ "    acadYear = ? AND acadMonth = ? "
				+ termSql ;
		try {
			timeBoundMappingList = (ArrayList<Specialisation>) jdbcTemplate.query(sql,new Object[] {mappingBean.getAcadYear(), mappingBean.getAcadMonth()}, 
																new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return timeBoundMappingList;
	}

	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getAllStagingTableTimeBoundMapping_V2(TimeBoundUserMapping mappingBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Specialisation> timeBoundMappingList = new ArrayList<Specialisation>();
		
		String sql ="SELECT "
				+ "    userId AS sapid, timebound_subject_config_id AS timeBoundId, "
				+ "	   tums.program_sem_sub_id AS program_sem_subject_id, specializationType "
				+ " FROM "
				+ "    lti.timebound_user_mapping_staging tums "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON tums.timebound_subject_config_id = ssc.id "
				+ "        INNER JOIN "
				+ "    exam.registration r ON tums.userId = r.sapid "
				+ "        AND ssc.acadYear = r.year "
				+ "        AND ssc.acadMonth = r.month "
				+ " WHERE "
				+ "    tums.year = ? AND tums.month = ? AND moved = false";
		try {
			timeBoundMappingList = (ArrayList<Specialisation>) jdbcTemplate.query(sql,
					new Object[] {mappingBean.getAcadYear().trim(), mappingBean.getAcadMonth().trim()}, 
					new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return timeBoundMappingList;
	}

	@Transactional(readOnly = false)
	public String batchInsertTimeBoundIds(ArrayList<Specialisation> timeBoundList){
		
		/* Alter table query
		 	ALTER TABLE lti.timebound_user_mapping 
			ADD CONSTRAINT constraint_name UNIQUE (userId,timebound_subject_config_id,role); 
		*/
		
		final int batchSize = 1000;
		
		/*
		String sql =" INSERT INTO lti.timebound_user_mapping " +
			 		" ( userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
			 		" VALUES (?, ?, 'Student', 'Admin', sysdate(), 'Admin', sysdate()) " +
			 		" ON DUPLICATE KEY UPDATE lastModifiedDate = sysdate(), lastModifiedBy = 'Admin'";
		*/
		
		for (int i = 0; i < timeBoundList.size(); i+= batchSize) {
			final List<Specialisation> batchList = timeBoundList.subList(i, i + batchSize > timeBoundList.size() ? timeBoundList.size() : i + batchSize);
			try {
				/*
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						Specialisation specialisation = batchList.get(i);
						ps.setString(1, specialisation.getSapid());
						ps.setString(2, specialisation.getTimeBoundId());
					}
					
					public int getBatchSize() {
						return batchList.size();
					}
				});
				 */
				
				//Added for to insert into timebound_user_mapping and delete from staging table same time
				for (Specialisation sp : batchList) {
					jdbcTemplate.batchUpdate(new String[] {
						" INSERT INTO lti.timebound_user_mapping " +
				 		" ( userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
				 		" VALUES ("+sp.getSapid()+", "+sp.getTimeBoundId()+", 'Student', 'Admin', sysdate(), 'Admin', sysdate()) " +
				 		" ON DUPLICATE KEY UPDATE lastModifiedDate = sysdate(), lastModifiedBy = 'Admin'",
				 		" DELETE FROM lti.timebound_user_mapping_staging WHERE timebound_subject_config_id = "+sp.getTimeBoundId()+" AND userId = "+sp.getSapid() 
				 	});
				}
			}catch (Exception e) {
				
				return e.getMessage();
			}
		}
		return "";
	}

	@Transactional(readOnly = true)
	public Specialisation getSpecializationsOfStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource); 
		Specialisation bean = null;
		try { 
			String sql =  " SELECT t1.specializationType as specialisation1,t2.specializationType as specialisation2 " + 
					"FROM lti.mba_specialisation_details s " + 
					"left join exam.specialization_type t1 on s.specialisation1=t1.id  " + 
					"left join exam.specialization_type t2 on s.specialisation2=t2.id  " + 
					" where s.sapid=? ";
			bean = (Specialisation) jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(Specialisation.class));
		} catch (Exception e) {
			
		}
		return bean;
	}

	@Transactional(readOnly = true)
	public boolean checkIsElectiveSelectionLive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "SELECT COUNT(*) FROM exam.mba_wx_exam_live_setting WHERE type = 'Specialisation Elective Selection' AND CURDATE() > endTime ";
		try{
			int rowCount = (int)jdbcTemplate.queryForObject(query,new Object[] {}, Integer.class);
			if(rowCount > 0){
				return true;
			}
		}catch(Exception e){
			
		}
		return false;
	}
	

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getSpecialisationPrerequisite() throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectListWithPrerequisite = new ArrayList<>();
		
		String sql =" SELECT  "
				+ "    sp.parent, sp.child, pss.*, st.specializationType AS specializationTypeName, ssc.sequence, ssc.id AS timeBoundId "
				+ "FROM "
				+ "    specialization_prerequisite sp "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON sp.parent = pss.id"
				+ "		   INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '')";

		specialisationSubjectListWithPrerequisite = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return specialisationSubjectListWithPrerequisite;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getCoreSubject(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> coreSubject = new ArrayList<>();
		
		String sql ="SELECT  "
				+ "    pss.*, "
				+ "    sc.prgm_sem_subj_id AS id, "
				+ "    sc.term AS sem, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "FROM "
				+ "    exam.specialization_core sc "
				+ "        INNER JOIN "
				+ "    exam.program_sem_subject pss ON sc.prgm_sem_subj_id = pss.id "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '')";

		coreSubject = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		return coreSubject;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getSubjectCount( ){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectCount = null;
		
		String sql ="SELECT  "
				+ "	count(pss.id) as subjectCount,  "
				+ "    pss.specializationType "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "WHERE "
				+ "    pss.sem = 3 "
				+ "GROUP BY pss.specializationType "
				+ "ORDER BY pss.specializationType + 0 ASC";
		try {
			specialisationSubjectCount = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectCount;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getAllSpecialisationSubjectsForSemThree(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String sql =" SELECT  "
				+ "    pss.*, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "WHERE "
				+ "    pss.sem = 3 "
				+ "ORDER BY pss.specializationType + 0 ASC";
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getCommonSubjectsForSemFour(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String sql =" SELECT  "
				+ "    pss.*, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "WHERE "
				+ "    pss.sem = 4 AND pss.id NOT IN ( 1741,1563,1565,1570,1740 )"
				+ "ORDER BY pss.specializationType + 0 ASC";
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getAllSpecialisationSubjectsWithDifferentDelivery(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String sql =" SELECT  "
				+ "    pss.*, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ " 		 AND ssc.acadyear = '"+NEXT_MBAWX_ACAD_YEAR+"' and ssc.acadMonth='"+NEXT_MBAWX_ACAD_MONTH+"'" 
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "WHERE pss.id IN ( 1741,1563,1565,1570,1740 ) "
				+ "ORDER BY pss.specializationType + 0 ASC";
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getCommonSubjects(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String sql ="SELECT "
				+ "    cs.*, st.specializationType AS specializationTypeName "
				+ "FROM "
				+ "    exam.common_specialization_subject cs "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON cs.specializationType = st.id";
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
		
	}

	@Transactional(readOnly = true)
	public ArrayList<ProgramSubjectMappingExamBean> getAutoSelectSubject(){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> autoSelectSubject = null;
		
		String sql ="SELECT * FROM exam.specialization_autoselect_subject ";
		try {
			autoSelectSubject = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query(sql, 
					new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		} catch (Exception e) {
			
		}
		return autoSelectSubject;
		
	}

	@Transactional(readOnly = false)
	public boolean insertTimeBoundStudentMapping_V2(Specialisation specialisation, ProgramSubjectMappingExamBean programSubjectMappingBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" INSERT INTO lti.timebound_user_mapping_staging " +
				 	" ( userId, timebound_subject_config_id, `program_sem_sub_id`, `specializationType`, "
				 	+ "role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
				 	" VALUES (?, ?, ?, ?, 'Student', ?, sysdate(), ?, sysdate());";
		try {
			jdbcTemplate.update(sql, new Object[] {
					specialisation.getSapid(),
					specialisation.getTimeBoundId(),
					programSubjectMappingBean.getId(),
					programSubjectMappingBean.getSpecializationType(),
					specialisation.getSapid(),
					specialisation.getSapid(),
					
			});
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}

	@Transactional(readOnly = true)
	public Boolean isPaymentApplicableForSR ( String sapid ) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    COUNT(*) > 0 "
				+ "FROM "
				+ "    portal.service_request sr "
				+ "WHERE "
				+ "    sr.sapid = ? "
				+ "        AND serviceRequestType = 'Change in Specialisation'";
		
		Boolean isPaymentApplicable = (Boolean) jdbcTemplate.queryForObject( query, Boolean.class, sapid);
		
		return isPaymentApplicable;
		
	}

	@Transactional(readOnly = true)
	public Specialisation getSpecializationDetailsForStudent( String sapid ) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		Specialisation response = new Specialisation();
		
		String query = "SELECT "
				+ "    sd.*, "
				+ "    GROUP_CONCAT( DISTINCT st.specializationType "
				+ "        SEPARATOR ' and ') AS specialization, "
				+ "	CASE  "
				+ "		WHEN COUNT(sr.id) > 0 THEN 5000 "
				+ "        ELSE 0 "
				+ "	END AS amount, "
				+ "    sr.createdDate "
				+ "FROM "
				+ "    lti.mba_specialisation_details sd "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON st.id IN (specialisation1 , specialisation2) "
				+ "		INNER JOIN  "
				+ "	portal.service_request sr ON sd.sapid = sr.sapid "
				+ "WHERE "
				+ "    sd.sapid = ? "
				+ "    AND serviceRequestType = 'Change in Specialisation' "
				+ "GROUP BY sapid "
				+ "ORDER BY sd.createdDate;";
		
		response = (Specialisation) jdbcTemplate.queryForObject( query,  new Object[]{sapid},
					new BeanPropertyRowMapper(Specialisation.class));

		return response;

	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unused")
	public String batchInsertSpecializationMappings(ArrayList<Specialisation> timeBoundList, String year, String month, String userid) {
		
		String query = "INSERT INTO `lti`.`specialization_user_mapping` "
					+ "(`userId`,`student_subject_config_id`,`program_sem_sub_id`,`specializationType`, `year`, `month`, "
					+ "`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) "
					+ "VALUES "
					+ "(?,?,?,?,?,?,?,sysdate(),?,sysdate()); ";
		
		String errorMessage = "";
		
		try {
			
			int[] batchInsertExtendedTestTime = jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {

					ps.setString(1, timeBoundList.get(i).getSapid() );
					ps.setString(2, timeBoundList.get(i).getTimeBoundId() );
					ps.setString(3, timeBoundList.get(i).getProgram_sem_subject_id() );
					ps.setString(4, timeBoundList.get(i).getSpecializationType());
					ps.setString(5, year);
					ps.setString(6, month);
					ps.setString(7, userid);
					ps.setString(8, userid);
					
				}

				@Override
				public int getBatchSize() {
					return timeBoundList.size();
				}
			  });

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return "Error in extension , Error : "+e.getMessage();
		}
		return errorMessage;
	}
	
	public ArrayList<ProgramSubjectMappingExamBean> getAllSpecialisationSubjectsForSemFive(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = null;
		
		String query = "SELECT  "
				+ "    pss.*, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ "        AND ssc.acadyear = ? "
				+ "        AND ssc.acadMonth = ? "
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "		   OR pss.specializationType <> '') "
				+ "WHERE "
				+ "    sem = 5 "
				+ "		AND consumerProgramStructureId = 151 " //Temporary added by Somesh for MBA-WX(OCT-20) on 2023-01-25
				+ "ORDER BY pss.specializationType + 0 ASC;";
		
		try {
			specialisationSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) jdbcTemplate.query( query,
					new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class), NEXT_MBAWX_ACAD_YEAR, NEXT_MBAWX_ACAD_MONTH );
		} catch (Exception e) {
			
		}
		return specialisationSubjectList;
	}
	
	@Transactional(readOnly = true)
	public Specialisation getSpecializationIds(String sapid) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		Specialisation specialisation = new Specialisation();

		String sql = "select * from lti.mba_specialisation_details where sapid = ?";

		specialisation = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Specialisation.class), sapid);

		return specialisation;
	}

	@Transactional(readOnly = true)
	public String getSpecializationBasedOnNomenclature(String specialisationIds) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select group_concat(specializationType order by specializationType separator ' & ') from exam.specialization_type where id in ("+
				specialisationIds+")";

		String specialisation = jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper<>(String.class));

		return specialisation;
	}

	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getSubjectForBatch(String batchId, String acadYear, String acadMonth) 
			throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT  "
				+ "    pss.subject, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId, "
				+ "    ssc.batchId, "
				+ "    st.id AS specializationType, "
				+ "    pss.id AS prgm_sem_subj_id "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "WHERE "
				+ "    ssc.batchId = ?"
				+ "	   	   AND ssc.acadyear = ? "
				+ "		   AND ssc.acadMonth = ? ";

		List<ProgramSubjectMappingExamBean> specialisationSubjectList = jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(ProgramSubjectMappingExamBean.class),
				batchId, acadYear, acadMonth);

		return specialisationSubjectList;
	}

	@Transactional(readOnly = true)
	public Boolean checkIfSpecializationDetailsExists(String year, String month, Integer sem) throws Exception{
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  "
				+ "    EXISTS( SELECT  "
				+ "            * "
				+ "        FROM "
				+ "            exam.specialization_subject_mapping "
				+ "        WHERE "
				+ "            year = ? AND month = ? "
				+ "                AND sem = ?);";

		Boolean exists = jdbcTemplate.queryForObject(sql, 
				new SingleColumnRowMapper<>(Boolean.class),
				year, month, sem);

		return exists;    
	}

	@Transactional(readOnly = true)
	public List<Specialisation> getSpecializationDetails(String year, String month, Integer sem) 
			throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT "
				+ "    subject, timeboundid, specialization, block, sequence, year AS acadYear, "
				+ "    month AS acadMonth, sem, isCoreSubject, hasPrerequisite, prerequisite, program_sem_subject_id "
				+ "FROM "
				+ "    exam.specialization_subject_mapping "
				+ "WHERE "
				+ "    year = ? AND month = ? "
				+ "        AND sem = ?";

		List<Specialisation> specialisationSubjectList = jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(Specialisation.class),
				year, month, sem);

		return specialisationSubjectList;
	}
	
	@Transactional(readOnly = true)
	public List<Specialisation> getBatchsForYearMonth(String acadYear, String acadMonth) 
			throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT  "
				+ "    pss.subject, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    ssc.sequence, "
				+ "    ssc.id AS timeBoundId, "
				+ "    ssc.batchId, "
				+ "    b.name AS batchName, "
				+ "    st.id AS specialization "
				+ "FROM "
				+ "    exam.program_sem_subject pss "
				+ "        INNER JOIN "
				+ "    lti.student_subject_config ssc ON pss.id = ssc.prgm_sem_subj_id "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON pss.specializationType = st.id "
				+ "        AND (pss.specializationType IS NOT NULL "
				+ "        OR pss.specializationType <> '') "
				+ "        INNER JOIN "
				+ "    exam.batch b ON ssc.batchId = b.id "
				+ "WHERE "
				+ "    pss.sem = 4 AND ssc.acadyear = ? "
				+ "        AND ssc.acadMonth = ? "
				+ "GROUP BY st.id";

		List<Specialisation> batchList = jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(Specialisation.class),
				acadYear, acadMonth);

		return batchList;
	}

	@Transactional(readOnly = false)
	public void saveSpecializationDetails(List<Specialisation> specialisation) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "INSERT INTO `exam`.`specialization_subject_mapping` "
				+ "(`subject`,`timeboundid`,`specialization`,`block`,`sequence`,`year`,`month`,`sem`,`isCoreSubject`, "
				+ "`hasPrerequisite`,`prerequisite`,`createdBy`,`createdDate`,`lastModifiedBy`,`lastModifiedDate`, `program_sem_subject_id`) "
				+ "VALUES "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate(),?)"
				+ "ON DUPLICATE KEY UPDATE "
				+ "block = ?, "
				+ "sequence = ?, "
				+ "isCoreSubject = ?, "
				+ "hasPrerequisite = ?, "
				+ "prerequisite = ?, "
				+ "lastModifiedBy = ?, "
				+ "lastModifiedDate = sysdate(), "
				+ "program_sem_subject_id = ? ";
			
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, specialisation.get(i).getSubject());
				ps.setString(2, specialisation.get(i).getTimeBoundId());
				ps.setString(3, specialisation.get(i).getSpecialization());
				ps.setInt(4, specialisation.get(i).getBlock());
				ps.setInt(5, specialisation.get(i).getSequence());
				ps.setString(6, specialisation.get(i).getAcadYear());
				ps.setString(7, specialisation.get(i).getAcadMonth());
				ps.setInt(8, specialisation.get(i).getSem());
				ps.setBoolean(9, specialisation.get(i).getIsCoreSubject());
				ps.setBoolean(10, specialisation.get(i).getHasPrerequisite());
				ps.setString(11, specialisation.get(i).getPrerequisite());
				ps.setString(12, specialisation.get(i).getUserId());
				ps.setString(13, specialisation.get(i).getUserId());
				ps.setString(14, specialisation.get(i).getProgram_sem_subject_id());

				ps.setInt(15, specialisation.get(i).getBlock());
				ps.setInt(16, specialisation.get(i).getSequence());
				ps.setBoolean(17, specialisation.get(i).getIsCoreSubject());
				ps.setBoolean(18, specialisation.get(i).getHasPrerequisite());
				ps.setString(19, specialisation.get(i).getPrerequisite());
				ps.setString(20, specialisation.get(i).getUserId());
				ps.setString(21, specialisation.get(i).getProgram_sem_subject_id());
			}

			@Override
			public int getBatchSize() {
				return specialisation.size();
			}
		});

	}

	@Transactional(readOnly = false)
	public void deleteSpecializationInstance(String year, String month, Integer sem, String timeBoundId, String specialization) 
			throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "DELETE FROM exam.specialization_subject_mapping  "
				+ "WHERE "
				+ "    year = ? AND month = ? "
				+ "    AND sem = ? "
				+ "    AND timeboundid = ? "
				+ "    AND specialization = ?";
			
		jdbcTemplate.update(sql, new Object[] { 
				year, month, sem, timeBoundId, specialization
		});
		
	}

	@Transactional(readOnly = true)
	public List<ProgramSubjectMappingExamBean> getCommonSubject(String year, String month, Integer sem, String specialization) 
			throws Exception{

		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		String query ="SELECT "
				+ "    subject, "
				+ "    sem, "
				+ "    scs.specializationType, "
				+ "    timeBoundId, "
				+ "    year AS acadYear, "
				+ "    month AS acadMonth, "
				+ "    st.specializationType AS specializationTypeName, "
				+ "    scs.prgm_sem_subj_id "
				+ "FROM "
				+ "    exam.specialization_common_subject scs "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON scs.specializationType = st.id "
				+ "WHERE "
				+ "    year = :year AND month = :month "
				+ "        AND sem = :sem "
				+ "        AND scs.specializationType = :specialization";

		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		queryParams.addValue("year", year.trim());
		queryParams.addValue("month", month);
		queryParams.addValue("sem", sem);
		queryParams.addValue("specialization", specialization);
		
		List<ProgramSubjectMappingExamBean> commonSubjects =  namedJdbcTemplate.query(query, 
				queryParams, new BeanPropertyRowMapper<>(ProgramSubjectMappingExamBean.class));
		
		return commonSubjects;
	}

	@Transactional(readOnly = true)
	public List<Specialisation> fetchBlockSequenceDetails(String year, String month, Integer sem) 
			throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT  "
				+ "    block, MAX(sequence) AS maxSequenceInBlock "
				+ "FROM "
				+ "    exam.specialization_subject_mapping "
				+ "WHERE "
				+ "    year = ? AND month = ? "
				+ "        AND sem = ? "
				+ "GROUP BY block , year , month , sem "
				+ "ORDER BY block , specialization";

		List<Specialisation> specialisationSubjectList = jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(Specialisation.class),
				year, month, sem);

		return specialisationSubjectList;
	}

	@Transactional(readOnly = true)
	public Specialisation fetchStudentDetailsForSpecialization(String sapid) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT  "
				+ "    msd.*, "
				+ "    GROUP_CONCAT(st.specializationType "
				+ "        ORDER BY st.specializationType "
				+ "        SEPARATOR ' & ') AS specializationType "
				+ "FROM "
				+ "    lti.mba_specialisation_details msd "
				+ "        INNER JOIN "
				+ "    exam.specialization_type st ON msd.specialisation1 = st.id "
				+ "        OR msd.specialisation2 = st.id "
				+ "WHERE "
				+ "    sapid = ? "
				+ "GROUP BY sapid";

		Specialisation specialisation = (Specialisation) jdbcTemplate.queryForObject(query, new Object[]{sapid}, 
					new BeanPropertyRowMapper(Specialisation.class));
		
		return specialisation;

	}

	@Transactional(readOnly = false)
	public void batchInsertTimeBoundStudentMapping(List<Specialisation> specialization) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query =" INSERT INTO lti.timebound_user_mapping_staging " +
				" ( userId, timebound_subject_config_id, `program_sem_sub_id`, `specializationType`, "
				+ "role, createdBy, createdDate, lastModifiedBy, lastModifiedDate, `year`, `month` ) " + 
				" VALUES (?, ?, ?, ?, 'Student', ?, sysdate(), ?, sysdate(), ?, ?);";

		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, specialization.get(i).getUserId());
				ps.setString(2, specialization.get(i).getTimeBoundId() );
				ps.setString(3, specialization.get(i).getProgram_sem_subject_id() );
				ps.setString(4, specialization.get(i).getSpecialization());
				ps.setString(5, specialization.get(i).getUserId());
				ps.setString(6, specialization.get(i).getUserId());
				ps.setString(7, specialization.get(i).getAcadYear());
				ps.setString(8, specialization.get(i).getAcadMonth());

			}

			@Override
			public int getBatchSize() {
				return specialization.size();
			}
		});

		return;
	}

	@Transactional(readOnly = false)
	public void batchInsertTimeBoundStudentMappingHistory(List<Specialisation> specialization) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query =" INSERT INTO lti.timebound_user_mapping_history " +
				" ( userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
				" VALUES (?, ?, 'Student', ?, sysdate(), ?, sysdate());";

		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				
				ps.setString(1, specialization.get(i).getUserId());
				ps.setString(2, specialization.get(i).getTimeBoundId() );
				ps.setString(3, specialization.get(i).getUserId());
				ps.setString(4, specialization.get(i).getUserId());

			}

			@Override
			public int getBatchSize() {
				return specialization.size();
			}
		});

		return;
	}

	@Transactional(readOnly = true)
	public List<Specialisation> fetchOptedinSpecialization(String userId) 
			throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT "
				+ "    userId, subject, timeboundid, program_sem_subject_id, specialization, block, "
				+ "    sequence, ssm.year, ssm.month, sem, isCoreSubject, hasPrerequisite, prerequisite "
				+ "FROM "
				+ "    lti.timebound_user_mapping_staging tums "
				+ "        INNER JOIN "
				+ "    exam.specialization_subject_mapping ssm ON tums.timebound_subject_config_id = ssm.timeboundid "
				+ "        AND tums.specializationType = ssm.specialization "
				+ "WHERE "
				+ "    userid = ? group by subject, year, month";

		List<Specialisation> optedinSpecialization = jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(Specialisation.class),
				userId);

		return optedinSpecialization;
	}

	@Transactional(readOnly = false)
	public void deleteStudentsExistingSpecialization(String sapid) throws Exception{
		
		jdbcTemplate = new JdbcTemplate(dataSource);

		
		String query ="DELETE FROM lti.timebound_user_mapping_staging "
				+ "WHERE "
				+ "    userId = ?";
		
		jdbcTemplate.update(query, new Object[] {sapid});

	}

	@Transactional(readOnly = false)
	public void batchInsertTimeBoundIds_V2(ArrayList<Specialisation> timeBoundList) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);

 		/*
 		 * delete post migration has been removed for now as there will be entries for multiple
 		 * semester happening in one go, to avoid deletion of important details
 		 * next cycle entries will be migrated once that cycle start 
 		 */
		String query = " INSERT INTO lti.timebound_user_mapping " +
		 		" ( userId, timebound_subject_config_id, role, createdBy, createdDate, lastModifiedBy, lastModifiedDate) " + 
		 		" VALUES (?, ?, 'Student', 'batchInsertTimeBoundIds_V2', sysdate(), 'batchInsertTimeBoundIds_V2', sysdate()) " +
		 		" ON DUPLICATE KEY UPDATE lastModifiedDate = sysdate(), lastModifiedBy = 'batchInsertTimeBoundIds_V2'";
		
		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, timeBoundList.get(i).getSapid() );
				ps.setString(2, timeBoundList.get(i).getTimeBoundId() );
				
			}

			@Override
			public int getBatchSize() {
				return timeBoundList.size();
			}
		  });
		
		return;
	}

	@Transactional(readOnly = false)
	public void batchInsertSpecializationMappings_V2(ArrayList<Specialisation> timeBoundList, String year, String month, String userid) 
		throws Exception{
		
		String query = "INSERT INTO `lti`.`specialization_user_mapping` "
					+ "(`userId`,`student_subject_config_id`,`program_sem_sub_id`,`specializationType`, `year`, `month`, "
					+ "`createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`) "
					+ "VALUES "
					+ "(?,?,?,?,?,?,?,sysdate(),?,sysdate())"
					+ "ON DUPLICATE KEY UPDATE "
					+ "student_subject_config_id = ?, "
					+ "program_sem_sub_id = ?, "
					+ "specializationType = ?, "
					+ "lastModifiedBy = ?, "
					+ "lastModifiedDate = sysdate(); ";
		
		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, timeBoundList.get(i).getSapid() );
				ps.setString(2, timeBoundList.get(i).getTimeBoundId() );
				ps.setString(3, timeBoundList.get(i).getProgram_sem_subject_id() );
				ps.setString(4, timeBoundList.get(i).getSpecializationType());
				ps.setString(5, year);
				ps.setString(6, month);
				ps.setString(7, userid);
				ps.setString(8, userid);
				ps.setString(9, timeBoundList.get(i).getTimeBoundId());
				ps.setString(10, timeBoundList.get(i).getProgram_sem_subject_id());
				ps.setString(11, timeBoundList.get(i).getSpecializationType());
				ps.setString(12, userid);

			}

			@Override
			public int getBatchSize() {
				return timeBoundList.size();
			}
		});

		return;
	}

	@Transactional(readOnly = false)
	public void batchUpdateStagingMovedToTimebound(ArrayList<Specialisation> timeBoundList, String year, String month, String userid) 
		throws Exception{

		String query = "UPDATE lti.timebound_user_mapping_staging  "
				+ "SET  "
				+ "    moved = TRUE, "
				+ "    lastModifiedBy = ?, "
				+ "    lastModifiedDate = SYSDATE() "
				+ "WHERE "
				+ "    userId = ? AND year = ? "
				+ "        AND month = ?;";
		
		jdbcTemplate.batchUpdate( query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				ps.setString(1, userid);
				ps.setString(2, timeBoundList.get(i).getSapid());
				ps.setString(3, year.trim());
				ps.setString(4, month.trim());

			}

			@Override
			public int getBatchSize() {
				return timeBoundList.size();
			}
		});

		return;
	}
	
	/**
	 */
	
	@Transactional(readOnly = true)
	public ArrayList<String> getAllRegistrationDetails(String month,String year, String sem,String masterkey) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> usersList = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT sapid FROM exam.registration WHERE month = ? AND YEAR = ? AND sem = ? and consumerProgramStructureId = ? ");

		usersList = (ArrayList<String>) jdbcTemplate.query(sql.toString(),new Object[] {month, year,sem,masterkey},new SingleColumnRowMapper(String.class));
		
		return usersList;
	}
	
	
	@Transactional(readOnly = true)
	public ArrayList<Specialisation> getAllStudentSpecializationDetails(ArrayList<String> userIds,String acadMonth,String acadYear) {
		
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		ArrayList<Specialisation> usersList = new ArrayList<Specialisation>();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT student_subject_config_id AS timeBoundId,userId AS sapid,specializationType,program_sem_sub_id AS program_sem_subject_id  FROM lti.specialization_user_mapping WHERE userId  in (:userIds) 	AND month =:acadMonth  AND year =:acadYear ");
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userIds", userIds);
		parameters.addValue("acadMonth", acadMonth);
		parameters.addValue("acadYear", acadYear);
	
		usersList = (ArrayList<Specialisation>) namedJdbcTemplate.query(sql.toString(), parameters,new BeanPropertyRowMapper(Specialisation.class));
	
		return usersList;
	}
	
	
	@Transactional(readOnly = true)
	public HashMap<String,Specialisation> getStudentDetails(ArrayList<String> userIds) {
			
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.students WHERE sapid IN (:userIds) ";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userIds", userIds);
		
		HashMap<String,Specialisation>	specializationTypes = namedJdbcTemplate.query(sql, parameters,new ResultSetExtractor<HashMap>(){
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,Specialisation> mapRet= new HashMap<String,Specialisation>();
		        while(rs.next()){
		        	Specialisation special = new Specialisation();
		        	special.setName(rs.getString("firstName")+" "+rs.getString("lastName"));
		        	special.setEmailId(rs.getString("emailId"));
		        	special.setMobile(rs.getString("mobile"));
		            mapRet.put(rs.getString("sapid"),special);
		        }
		        return mapRet;
		    }
		});
		return specializationTypes;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String,MDMSubjectCodeMappingBean> getSubjectNameFromPssId() {
			
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT scm.id,sc.subjectcode,sc.subjectname,scm.subjectCodeId FROM" + 
				   " 	exam.mdm_subjectcode sc		" + 
				   "	INNER JOIN	" + 
				   "	exam.mdm_subjectcode_mapping scm ON sc.id = scm.subjectCodeId	");
		
		
		HashMap<String,MDMSubjectCodeMappingBean>	specializationTypes = namedJdbcTemplate.query(sql.toString(),new ResultSetExtractor<HashMap>(){
		    @Override
		    public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		        HashMap<String,MDMSubjectCodeMappingBean> mapRet= new HashMap<String,MDMSubjectCodeMappingBean>();
		        while(rs.next()){
		        	MDMSubjectCodeMappingBean bean = new MDMSubjectCodeMappingBean();
		        	bean.setSubjectCode(rs.getString("subjectcode"));
		        	bean.setSubjectName(rs.getString("subjectname"));
		        	bean.setSubjectCodeId(rs.getString("subjectCodeId"));
		            mapRet.put(rs.getString("id"),bean);
		        }
		        return mapRet;
		    }
		});
		return specializationTypes;
	}
	
	@Transactional(readOnly = true)
	public int getStudentMasterKey(String sapid) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int masterKey = 0;
		
		try {
			String sql =  "SELECT consumerProgramStructureId from exam.students where sapid = ? ";
			masterKey = (int) jdbcTemplate.queryForObject(sql, new Object[]{sapid},Integer.class);
		} catch (Exception e) {
			
		}
		
		return masterKey;
		
	}
	
	/**
	 * 
	 */
}
