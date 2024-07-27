package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.ForumStudentPortalBean;

public class ForumDAO {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional(readOnly = true)
	public ExamOrderStudentPortalBean getForumCurrentlyLive() {
		ExamOrderStudentPortalBean forumLiveBean = null;
		String sql = "Select * from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where forumLive = 'Y') ";
		try {
			forumLiveBean = (ExamOrderStudentPortalBean)jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ExamOrderStudentPortalBean.class));
		} catch (Exception e) {
			//
		}
		return forumLiveBean;
	}

	@Transactional(readOnly = false)
	public void createThread(ForumStudentPortalBean forumBean) {
		final String sql = "INSERT INTO collaborate.forum_thread "
				+ " (year,month,subject,title,description,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
				+ " VALUES "
				+ " (?,?,?,?,?,?,sysdate(),?,sysdate(),?)";



		final ForumStudentPortalBean bean = forumBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, bean.getYear());
				ps.setString(2, bean.getMonth());
				ps.setString(3, bean.getSubject());
				ps.setString(4, bean.getTitle());
				ps.setString(5, bean.getDescription());
				ps.setString(6, bean.getCreatedBy());
				ps.setString(7, bean.getCreatedBy());
				ps.setString(8, bean.getStatus());
		
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//jdbcTemplate.update(psc);
		jdbcTemplate.update(psc, keyHolder);
		
		long id = keyHolder.getKey().longValue();
		//System.out.println("Inserted Thread");
		
		forumBean.setId(id);
		
	}

	@Transactional(readOnly = true)
	public ArrayList<String> getFacultyEmailForAnActiveForum(){
		String sql ="select af.email from collaborate.forum_thread ct,acads.faculty af where ct.createdBy=af.facultyId and ct.status='Active' and"
					+"(select count(*) from collaborate.forum_thread_reply ctr where ctr.parentPostId=ct.id) >0";
		ArrayList<String> emailIds = (ArrayList<String>)jdbcTemplate.queryForList(sql, String.class);
		return emailIds;
	}
	
	@Transactional(readOnly = true)
	public List<ForumStudentPortalBean> getForumThreadsForSubject(String subject){
		
		String sql = "SELECT * from collaborate.forum_thread ft, exam.examorder eo where "
				+ " ft.month = eo.acadmonth and ft.year = eo.year "
				+ " and subject= ? "
				+ " and status= 'Active' "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where forumLive = 'Y') "
				+ " ORDER BY createdDate DESC";
		List<ForumStudentPortalBean> listOfForumsRelatedToSubject = jdbcTemplate.query(sql, new Object[]{subject},new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		return listOfForumsRelatedToSubject;
	}
	
	@Transactional(readOnly = true)
	public String getEmailIdOfFaculty(String forumCreatedBy){
			String sql ="select af.email from acads.faculty af,collaborate.forum_thread cf where cf.createdBy=af.facultyId and cf.createdBy=?";
			String emailId = (String)jdbcTemplate.queryForObject(sql,new Object[]{forumCreatedBy}, String.class);
			return emailId;
	 		
 	}
	
	@Transactional(readOnly = true)
	public String getFacultyName(String id){
		String sql ="select CONCAT(firstName,' ',lastName) from acads.faculty where facultyId=?";
		String emailId = (String)jdbcTemplate.queryForObject(sql,new Object[]{id}, String.class);
		return emailId;
		
	}
	
	@Transactional(readOnly = true)
	public ForumStudentPortalBean getForumById(String id) {
		ForumStudentPortalBean forumThreadBean = null;
		String sql = "select *,af.firstName,af.lastName from collaborate.forum_thread cft,acads.faculty af where cft.id=? and cft.createdBy=af.facultyId";
		
		try {
			forumThreadBean = (ForumStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		} catch (Exception e) {
			//
		}
		return forumThreadBean;
	}
	
	@Transactional(readOnly = true)
	public ForumStudentPortalBean getForumReplyById(String id) {
		ForumStudentPortalBean forumThreadBean = null;
		String sql = "Select * from collaborate.forum_thread_reply where id = ? ";

		try {
			forumThreadBean = (ForumStudentPortalBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		} catch (Exception e) {
			//
		}
		return forumThreadBean;
	}
	
	@Transactional(readOnly = false)
	public void updateReply(ForumStudentPortalBean forum){
		
		String sql = "UPDATE collaborate.forum_thread_reply SET description=? , lastModifiedDate=sysdate() WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{forum.getDescription(),forum.getId()});
	}
	
	@Transactional(readOnly = false)
	public void deleteThreadReply(String replyId){
		
		String sql = "UPDATE collaborate.forum_thread_reply SET status='Delete' WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{replyId});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumStudentPortalBean> getThreadRepliesOfMainThread(String parentpostid) {
		ArrayList<ForumStudentPortalBean> threadReplies = null;
		ArrayList<ForumStudentPortalBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentpostid = ? "
				+ " and ftr.createdBy = s.sapid and status='Active' and (parentReplyId is null or parentReplyId = '' ) order by createdDate desc "; //Exclude Level 2 reply by checking parentReplyId is null
		
		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId,af.email  from collaborate.forum_thread_reply ftr, acads.faculty af where parentpostid = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active' and (parentReplyId is null or parentReplyId = '' ) order by createdDate desc ";
		try {
			threadReplies = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
			threadRepliesForFaculty = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		} catch (Exception e) {
			
		}
		threadReplies.addAll(threadRepliesForFaculty);
		return threadReplies;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumStudentPortalBean> getThreadRepliesOfMainThreadOrderAsc(String parentpostid) {
		ArrayList<ForumStudentPortalBean> threadReplies = null;
		ArrayList<ForumStudentPortalBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentpostid = ? "
				+ " and ftr.createdBy = s.sapid and status='Active' and (parentReplyId is null or parentReplyId = '' ) "; //Exclude Level 2 reply by checking parentReplyId is null
		
		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId,af.email  from collaborate.forum_thread_reply ftr, acads.faculty af where parentpostid = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active' and (parentReplyId is null or parentReplyId = '' ) ";
		try {
			threadReplies = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
			threadRepliesForFaculty = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		} catch (Exception e) {
			
		}
		threadReplies.addAll(threadRepliesForFaculty);
		return threadReplies;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumStudentPortalBean> getThreadRepliesOfParentReply(String parentpostid) {
		ArrayList<ForumStudentPortalBean> threadReplies = null;
		ArrayList<ForumStudentPortalBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentReplyId = ? "
				+ " and ftr.createdBy = s.sapid and status='Active'";

		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId,af.email from collaborate.forum_thread_reply ftr, acads.faculty af where parentReplyId = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active'";
		try {
			threadReplies = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
			threadRepliesForFaculty = (ArrayList<ForumStudentPortalBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumStudentPortalBean.class));
		} catch (Exception e) {
			
		}
		
		threadReplies.addAll(threadRepliesForFaculty);
		 Collections.sort(threadReplies, new Comparator<ForumStudentPortalBean>() {
		        @Override
		        public int compare(ForumStudentPortalBean object1, ForumStudentPortalBean object2) {
		        	String o1=object1.getCreatedDate();
		        	String o2=object2.getCreatedDate();
		            return  (o2).compareTo(o1);
		            
		        }
		    });
		return threadReplies;
	}
	
	@Transactional(readOnly = false)
	public void createThreadReply(ForumStudentPortalBean forumBean) {
		
			final String sql = "INSERT INTO collaborate.forum_thread_reply "
					+ " (parentPostId,parentReplyId,description,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
					+ " VALUES "
					+ " (?,?,?,?,sysdate(),?,sysdate(),'Active')";

			

			final ForumStudentPortalBean bean = forumBean;
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, bean.getParentPostId()+"");
					ps.setString(2, bean.getParentReplyId());
					ps.setString(3, bean.getDescription());
					ps.setString(4, bean.getCreatedBy());
					ps.setString(5, bean.getLastModifiedBy());
				
			
					return ps;
				}
			};
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			//jdbcTemplate.update(psc);
			jdbcTemplate.update(psc, keyHolder);
			
			long id = keyHolder.getKey().longValue();
			//System.out.println("Inserted Thread Reply");
			
			forumBean.setId(id);
			
		}
}
