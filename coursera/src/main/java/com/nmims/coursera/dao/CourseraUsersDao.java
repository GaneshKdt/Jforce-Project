package com.nmims.coursera.dao;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.beans.StudentCourseraMappingBean;

@Repository
public class CourseraUsersDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	public StudentCourseraBean getSingleStudentsData(String sapid) {
		
		Session session = this.sessionFactory.getCurrentSession();
		String sql =  " SELECT * from exam.students s "
					+ " WHERE s.sapid=:sapid and s.sem = (Select max(sem) from exam.students where sapid =:sapid ) ";
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(StudentCourseraBean.class);
		query.setParameter("sapid", sapid);
	
		StudentCourseraBean student = (StudentCourseraBean) query.list().get(0);
		return student;
		}
	
	public String getStudentlearnerURL(String sapid) {
		
		Session session = this.sessionFactory.getCurrentSession();
		String sql =" SELECT learnerURL FROM " + 
					"    coursera.coursera_student_mapping csm " + 
					"        INNER JOIN " + 
					"    coursera.coursera_program_details cpd ON csm.coursera_program_id = cpd.id " + 
					" WHERE sapId =:sapid ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("sapid", sapid);
		String learnerURL = (String) query.list().get(0);
		return learnerURL;
	}
	
	public String getCourseraProgramIdBySapid(String sapId) {
		Session session = this.sessionFactory.getCurrentSession();
		String sql = " SELECT c.coursera_program_id FROM coursera.coursera_program_mapping c " +
					 " INNER JOIN exam.students s " +
					 " ON s.consumerProgramStructureId = c.consumer_program_structure_id WHERE s.sapid=:sapid ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("sapid", sapId);
		Integer coursera_program_id = (Integer) query.list().get(0);
		return String.valueOf(coursera_program_id);
	}
	
	public int insertCourseraEntry(String sapId, String createdBy, String coursera_program_id) {
		
		int count = 0;

		LocalDate now = LocalDate.now();
		LocalDate date = now.plusYears(1).minusDays(1);
		String expDate = date.toString() + "T23:59:59";
		LocalDateTime expiryDate = LocalDateTime.parse(expDate);
		
		String sql = " INSERT INTO coursera.coursera_student_mapping(sapId,createdBy,coursera_program_id,lastUpdatedBy,expiryDate,startDate) VALUES(:sapId,:createdBy,:coursera_program_id,:lastUpdatedBy,:expiryDate,current_timestamp())";
		Session session = sessionFactory.getCurrentSession();
		SQLQuery upsertQuery = session.createSQLQuery(sql);
		
		upsertQuery.setParameter("sapId", sapId);
		upsertQuery.setParameter("createdBy", createdBy);
		upsertQuery.setParameter("coursera_program_id", coursera_program_id);
		upsertQuery.setParameter("lastUpdatedBy", createdBy);
		upsertQuery.setParameter("expiryDate", expiryDate);
		count=upsertQuery.executeUpdate();
		
		return count;
	}
	
	public int checkForCourseraAvailibilityForMasterKey(String consumerProgramStructureId) {
		
		Session session = this.sessionFactory.getCurrentSession();
		String sql = " SELECT count(*) as count FROM coursera.coursera_program_mapping where consumer_program_structure_id=:consumerProgramStructureId";
		int count = 0;
		try {
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("consumerProgramStructureId", consumerProgramStructureId);
			count = ((BigInteger) query.list().get(0)).intValue();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return count;
		}
	}
	
	public StudentCourseraMappingBean checkStudentOptedForCoursera(String sapId) {
		
		Session session = this.sessionFactory.getCurrentSession();
		
		String sql=" SELECT * FROM coursera.coursera_student_mapping WHERE sapId=:sapId ";
		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(StudentCourseraMappingBean.class);
		query.setParameter("sapId", sapId);
		
		StudentCourseraMappingBean student = (StudentCourseraMappingBean) query.list().get(0);
		
		return student;
	
	}
	
	public String getStudentlearnerURLByCounsumerProgramStructureId(String masterKey) {
		String learnerURL="";
		Session session = this.sessionFactory.getCurrentSession();
		String sql =" SELECT learnerURL FROM coursera.coursera_program_details cpd  " + 
					" INNER JOIN coursera.coursera_program_mapping cpm " + 
					" ON cpd.id=cpm.coursera_program_id " + 
					" WHERE cpm.consumer_program_structure_id=:masterKey ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("masterKey", masterKey);
		learnerURL = (String) query.list().get(0);
		return learnerURL;
	}
	
}
