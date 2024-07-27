package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.StudentsAnalyticsBean;

public class AnalyticsApiDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;		
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}

	@Transactional(readOnly = true)
	public List<StudentsAnalyticsBean> studentsClearedApplicableSubjectsData() {
		jdbcTemplate = new JdbcTemplate(dataSource);		
		List<StudentsAnalyticsBean> data = new ArrayList<StudentsAnalyticsBean>();
		String sql = "SELECT  " + 
				"    s.sapid, " + 
				"    CONCAT(firstName, ' ', lastName) AS studentName, " + 
				"    enrollmentMonth, " + 
				"    enrollmentYear, " + 
				"    s.emailId, " + 
				"    s.mobile, " + 
				"    isLateral, " + 
				"    c.lc, " + 
				"    c.centerName, " + 
				"    s.programCleared, " + 
				"    validityEndMonth, " + 
				"    validityEndYear, " + 
				"    MAX(r.sem) AS currentSem, " + 
				"    c.state, " + 
				"    c.city, " + 
				"    CASE " + 
				"        WHEN " + 
				"            SUM(CASE " + 
				"                WHEN " + 
				"                    pf.isPass = 'Y' AND pf.sem = 1 " + 
				"                        AND r.sem = 1 " + 
				"                        AND pss.subject = pf.subject " + 
				"                THEN " + 
				"                    1 " + 
				"                ELSE 0 " + 
				"            END) = p.noOfSubjectsToClearSem " + 
				"        THEN " + 
				"            'Y' " + 
				"        ELSE 'N' " + 
				"    END AS clearedSem1, " + 
				"    CASE " + 
				"        WHEN " + 
				"            SUM(CASE " + 
				"                WHEN " + 
				"                    pf.isPass = 'Y' AND pf.sem = 2 " + 
				"                        AND r.sem = 2 " + 
				"                        AND pss.subject = pf.subject " + 
				"                THEN " + 
				"                    1 " + 
				"                ELSE 0 " + 
				"            END) = p.noOfSubjectsToClearSem " + 
				"        THEN " + 
				"            'Y' " + 
				"        ELSE 'N' " + 
				"    END AS clearedSem2, " + 
				"    CASE " + 
				"        WHEN " + 
				"            SUM(CASE " + 
				"                WHEN " + 
				"                    pf.isPass = 'Y' AND pf.sem = 3 " + 
				"                        AND r.sem = 3 " + 
				"                        AND pss.subject = pf.subject " + 
				"                THEN " + 
				"                    1 " + 
				"                ELSE 0 " + 
				"            END) = p.noOfSubjectsToClearSem " + 
				"        THEN " + 
				"            'Y' " + 
				"        ELSE 'N' " + 
				"    END AS clearedSem3, " + 
				"    CASE " + 
				"        WHEN " + 
				"            SUM(CASE " + 
				"                WHEN " + 
				"                    pf.isPass = 'Y' AND pf.sem = 4 " + 
				"                        AND r.sem = 4 " + 
				"                        AND pss.subject = pf.subject " + 
				"                THEN " + 
				"                    1 " + 
				"                ELSE 0 " + 
				"            END) = p.noOfSubjectsToClearSem " + 
				"        THEN " + 
				"            'Y' " + 
				"        ELSE 'N' " + 
				"    END AS clearedSem4, " + 
				"    SUM(CASE " + 
				"        WHEN " + 
				"            pf.isPass = 'Y' AND pf.sem = 1 " + 
				"                AND r.sem = 1 " + 
				"                AND pss.sem = 1 " + 
				"                AND pss.subject = pf.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) + SUM(CASE " + 
				"        WHEN " + 
				"            pf.isPass = 'Y' AND pf.sem = 2 " + 
				"                AND r.sem = 2 " + 
				"                AND pss.sem = 2 " + 
				"                AND pss.subject = pf.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) + SUM(CASE " + 
				"        WHEN " + 
				"            pf.isPass = 'Y' AND pf.sem = 3 " + 
				"                AND r.sem = 3 " + 
				"                AND pss.sem = 3 " + 
				"                AND pss.subject = pf.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) + SUM(CASE " + 
				"        WHEN " + 
				"            pf.isPass = 'Y' AND pf.sem = 4 " + 
				"                AND r.sem = 4 " + 
				"                AND pss.sem = 4 " + 
				"                AND pss.subject = pf.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) AS subjectsCleared, " + 
				"    COUNT(DISTINCT (CASE " + 
				"            WHEN r.sem = pss.sem THEN CONCAT(r.sem, pss.sem, pss.subject) " + 
				"        END)) AS currentSubjectsApplicable, " + 
				"    p.noOfSubjectsToClear AS applicableSubjects " + 
				"FROM " + 
				"    exam.students s " + 
				"        LEFT JOIN " + 
				"    (SELECT  " + 
				"        * " + 
				"    FROM " + 
				"        exam.passfail " + 
				"    WHERE " + 
				"        isPass = 'Y') pf ON s.sapid = pf.sapid " + 
				"        LEFT JOIN " + 
				"    exam.registration r ON s.sapid = r.sapid " + 
				"        LEFT JOIN " + 
				"    exam.program_sem_subject pss ON s.consumerProgramStructureId = pss.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    exam.programs p ON s.consumerProgramStructureId = p.consumerProgramStructureId " + 
				"        LEFT JOIN " + 
				"    exam.centers c ON c.centerCode = s.centerCode " + 
				"WHERE " + 
			//	"    enrollmentYear = 2020        AND " + 
				"    PrgmStructApplicable NOT IN ('Jul2009' , 'Jul2008') " + 
				"        AND s.program NOT IN ('MBA - X' , 'MBA - WX') " + 
				"        AND s.sapid NOT IN (77777777770 , 77777777771, " + 
				"        77777777777, " + 
				"        77777777778, " + 
				"        88888888888) " + 
				"GROUP BY s.sapid;";
		try {
			data = (List<StudentsAnalyticsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentsAnalyticsBean.class));	
		} catch (Exception e) {
			// TODO Auto-generated catch block
						
		}
		return data;
		
	}
	
	@Transactional(readOnly = true)
	public List<StudentsAnalyticsBean> studentsAssignmentData() {
		jdbcTemplate = new JdbcTemplate(dataSource);		
		List<StudentsAnalyticsBean> data = new ArrayList<StudentsAnalyticsBean>();
		String sql = "SELECT  " + 
				"    s.sapid, " + 
				"    MAX(m.assignmentscore) AS assignmentScore, " + 
				"    CONCAT(s.firstName, ' ', s.lastName) AS studentName, " + 
				"    s.sem AS currentSem, " + 
				"    s.validityEndMonth, " + 
				"    s.validityEndYear, " + 
				"    s.emailId, " + 
				"    s.mobile, " + 
				"    m.subject, " + 
				"    SUM(CASE " + 
				"        WHEN " + 
				"            m.assignmentscore >= 0 " + 
				"                AND m.assignmentscore IS NOT NULL " + 
				"                AND m.assignmentscore NOT IN ('' , 'ANS') " + 
				"                AND m.subject = p.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) AS assignmentAttempts, " + 
				"    SUM(CASE " + 
				"        WHEN " + 
				"            m.writenscore >= 0 " + 
				"                AND m.writenscore IS NOT NULL " + 
				"                AND m.writenscore NOT IN ('' , 'AB') " + 
				"                AND m.subject = p.subject " + 
				"        THEN " + 
				"            1 " + 
				"        ELSE 0 " + 
				"    END) AS teeAttempts, " + 
				"    p.total, " + 
				"    p.failReason, " + 
				"    p.remarks, " + 
				"    p.isPass, " + 
				"    c.lc " + 
				"FROM " + 
				"    exam.students s " + 
				"        LEFT JOIN " + 
				"    exam.marks m ON s.sapid = m.sapid " + 
				"        LEFT JOIN " + 
				"    exam.passfail p ON s.sapid = p.sapid " + 
				"        LEFT JOIN " + 
				"    exam.centers c ON c.centerCode = s.centerCode " + 
				"WHERE " + 
				"    PrgmStructApplicable NOT IN ('Jul2009' , 'Jul2008') " + 
				"        AND s.program NOT IN ('MBA - X' , 'MBA - WX') " + 
				"        AND s.sapid NOT IN (77777777770 , 77777777771, " + 
				"        77777777777, " + 
				"        77777777778, " + 
				"        88888888888) " + 
				"        AND m.subject = p.subject " + 
				/**"        AND s.enrollmentMonth = 'Jul' " + 
				"        AND enrollmentYear = '2018' " + */
				"GROUP BY s.sapid , m.subject; " ;
		try {
			data = (List<StudentsAnalyticsBean>) jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(StudentsAnalyticsBean.class));	
		} catch (Exception e) {
			// TODO Auto-generated catch block
						
		}
		return data;
		
	}
}
