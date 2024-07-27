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
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ForumAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.helpers.PaginationHelper;

public class ForumDAO extends BaseDAO{
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Transactional(readOnly = false)
	public void updateReply(ForumAcadsBean forum){
		
		String sql = "UPDATE collaborate.forum_thread_reply SET description=? , lastModifiedDate=sysdate() WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{forum.getDescription(),forum.getId()});
	}
	
	@Transactional(readOnly = true)
	public ExamOrderAcadsBean getForumCurrentlyLive() {
		ExamOrderAcadsBean forumLiveBean = null;
		String sql = "Select * from exam.examorder where "
				+ " examorder.order = (Select max(examorder.order) from exam.examorder where forumLive = 'Y') ";
		try {
			forumLiveBean = (ExamOrderAcadsBean)jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper(ExamOrderAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return forumLiveBean;
	}
	
	@Transactional(readOnly = true)
	public List<ForumAcadsBean> getForumThreadsForSubject(String subject){
		String sql = "SELECT * from collaborate.forum_thread ft, exam.examorder eo where "
				+ " ft.month = eo.acadmonth and ft.year = eo.year "
				+ " and subject= ? "
				+ " and status= 'Active' "
				+ " and eo.order = (select max(examorder.order) from exam.examorder where forumLive = 'Y') "
				+ " ORDER BY createdDate DESC";
		List<ForumAcadsBean> listOfForumsRelatedToSubject = jdbcTemplate.query(sql, new Object[]{subject},new BeanPropertyRowMapper(ForumAcadsBean.class));
		return listOfForumsRelatedToSubject;
	}
	
	@Transactional(readOnly = true)
	public ForumAcadsBean getForumById(String id) {
		ForumAcadsBean forumThreadBean = null;
		String sql = "select *,af.firstName,af.lastName from collaborate.forum_thread cft,acads.faculty af where cft.id=? and cft.createdBy=af.facultyId";
		try {
			forumThreadBean = (ForumAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return forumThreadBean;
	}
	
	@Transactional(readOnly = true)
	public ForumAcadsBean getForumReplyById(String id) {
		ForumAcadsBean forumThreadBean = null;
		String sql = "Select * from collaborate.forum_thread_reply where id = ? ";

		try {
			forumThreadBean = (ForumAcadsBean)jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return forumThreadBean;
	}
	
	@Transactional(readOnly = false)
	public void deleteThreadReply(String replyId){
		
		String sql = "UPDATE collaborate.forum_thread_reply SET status='Delete' WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{replyId});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFacultyEmailForAnActiveForum(){
		String sql ="select af.email from collaborate.forum_thread ct,acads.faculty af where ct.createdBy=af.facultyId and ct.status='Active' and"
					+"(select count(*) from collaborate.forum_thread_reply ctr where ctr.parentPostId=ct.id) >0";
		ArrayList<String> emailIds = (ArrayList<String>)jdbcTemplate.queryForList(sql, String.class);
		return emailIds;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumAcadsBean> getThreadRepliesOfMainThreadAsc(String parentpostid) {
		ArrayList<ForumAcadsBean> threadReplies = null;
		ArrayList<ForumAcadsBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentpostid = ? "
				+ " and ftr.createdBy = s.sapid and status='Active' and (parentReplyId is null or parentReplyId = '' ) "; //Exclude Level 2 reply by checking parentReplyId is null

		
		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId  from collaborate.forum_thread_reply ftr, acads.faculty af where parentpostid = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active' and (parentReplyId is null or parentReplyId = '' or parentReplyId='null') ";
		try {
			threadReplies = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
			threadRepliesForFaculty = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		threadReplies.addAll(threadRepliesForFaculty);
		
		return threadReplies;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumAcadsBean> getThreadRepliesOfMainThreadOrderDsc(String parentpostid) {
		ArrayList<ForumAcadsBean> threadReplies = null;
		ArrayList<ForumAcadsBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentpostid = ? "
				+ " and ftr.createdBy = s.sapid and status='Active' and (parentReplyId is null or parentReplyId = '' ) order by createdDate desc "; //Exclude Level 2 reply by checking parentReplyId is null


		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId  from collaborate.forum_thread_reply ftr, acads.faculty af where parentpostid = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active' and (parentReplyId is null or parentReplyId = '' or parentReplyId='null') order by createdDate desc ";
		try {
			threadReplies = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
			threadRepliesForFaculty = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}

		threadReplies.addAll(threadRepliesForFaculty);

		return threadReplies;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumAcadsBean> getThreadRepliesOfParentReply(String parentpostid) {
		ArrayList<ForumAcadsBean> threadReplies = null;
		ArrayList<ForumAcadsBean> threadRepliesForFaculty = null;
		String sql = "Select  ftr.*, s.firstName, s.lastName, s.imageUrl from collaborate.forum_thread_reply ftr, exam.students s where parentReplyId = ? "
				+ " and ftr.createdBy = s.sapid and status='Active'";
		
		String sqlForFaculty = "Select  ftr.*,af.firstName,af.lastName,af.facultyId from collaborate.forum_thread_reply ftr, acads.faculty af where parentReplyId = ? "
				+ " and ftr.createdBy = af.facultyId and status='Active'";

		try {
			threadReplies = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
			threadRepliesForFaculty = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sqlForFaculty, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}
		
		threadReplies.addAll(threadRepliesForFaculty);
		 
		 Collections.sort(threadReplies, new Comparator<ForumAcadsBean>() {
		        @Override
		        public int compare(ForumAcadsBean object1, ForumAcadsBean object2) {
		        	Date o1=object1.getCreatedDate();
		        	Date o2=object2.getCreatedDate();
		            return  (o2).compareTo(o1);
		            
		        }
		    });
		return threadReplies;
	}
	
	@Transactional(readOnly = false)
	public void updateStatusOfThread(String id,String statusOfThread){
		
		String sql = "UPDATE collaborate.forum_thread SET status=? WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{statusOfThread,id});
	}
	
	@Transactional(readOnly = false)
	public void updateTitleOfForumThread(String id,String title){
		String sql = "UPDATE collaborate.forum_thread SET title=? WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{title,id});
	}
	
	@Transactional(readOnly = false)
	public void updateStatusOfThreadReply(String id,String statusOfThread){
		
		String sql = "UPDATE collaborate.forum_thread_reply SET status=? WHERE id=?";
		jdbcTemplate.update(sql, new Object[]{statusOfThread,id});
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ForumAcadsBean> getThreadReplies(String parentpostid) {
		ArrayList<ForumAcadsBean> threadReplies = null;
		String sql = "Select ftr.*, s.firstName, s.lastName from collaborate.forum_thread_reply ftr, exam.students s where parentpostid = ? "
				+ " and ftr.createdBy = s.sapid and status='Active'";

		try {
			threadReplies = (ArrayList<ForumAcadsBean>)jdbcTemplate.query(sql, new Object[]{parentpostid}, new BeanPropertyRowMapper(ForumAcadsBean.class));
		} catch (Exception e) {
			  
		}
		return threadReplies;
	}
	
	@Transactional(readOnly = true)
	public PageAcads<ForumAcadsBean> getThreadsRelatedToSubjectPage(int pageNo,int pageSize,ForumAcadsBean forumBean){
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "Select * from collaborate.forum_thread";
		String countSql = "Select count(*) from collaborate.forum_thread";
		
		if( forumBean.getSubject() != null ){
			sql = sql + " where subject = ? ";
			countSql = countSql + " where subject = ? ";
			parameters.add(forumBean.getSubject());
		}
		//sql = sql + " and status='Active'";
		Object[] args = parameters.toArray();
		
		PaginationHelper<ForumAcadsBean> pagingHelper = new PaginationHelper<ForumAcadsBean>();
		PageAcads<ForumAcadsBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(ForumAcadsBean.class));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void createThread(ForumAcadsBean forumBean) {
		final String sql = "INSERT INTO collaborate.forum_thread "
				+ " (year,month,subject,title,description,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
				+ " VALUES "
				+ " (?,?,?,?,?,?,sysdate(),?,sysdate(),?)";

		final ForumAcadsBean bean = forumBean;
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, bean.getYear());
				ps.setString(2, bean.getMonth());
				ps.setString(3, bean.getSubject());
				ps.setString(4, bean.getTitle());
				ps.setString(5, bean.getDescription());
				ps.setString(6, bean.getCreatedBy());
				ps.setString(7, bean.getLastModifiedBy());
				ps.setString(8, bean.getStatus());
				return ps;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//jdbcTemplate.update(psc);
		jdbcTemplate.update(psc, keyHolder);
		
		long id = keyHolder.getKey().longValue();
		forumBean.setId(id);
		
	}
	
	@Transactional(readOnly = false)
	public void createThreadReply(ForumAcadsBean forumBean){
		final String sql = "INSERT INTO collaborate.forum_thread_reply "
				+ " (parentPostId,parentReplyId,description,createdBy,createdDate,lastModifiedBy,lastModifiedDate,status) "
				+ " VALUES "
				+ " (?,?,?,?,sysdate(),?,sysdate(),'Active')";

		final ForumAcadsBean bean = forumBean;
		try{
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, bean.getParentPostId());
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
			forumBean.setId(id);
			
		}catch(Exception e){
			  
		}
	}
	
	@Transactional(readOnly = true)
	public ArrayList<String> getFacultySubjectListForSessions(String facultyId, String year, String month) {
		ArrayList<String> subjectList = null;

		try {
			String sql = "select subject from acads.sessions " + 
					"where facultyId=? or altFacultyId=? or altFacultyId2=? or altFacultyId3=?" + 
					"and year=? and month=? " + 
					"group by subject";

		 subjectList = (ArrayList<String>) jdbcTemplate.query(sql,new Object[]{facultyId,facultyId,facultyId, facultyId,year, month} ,new SingleColumnRowMapper<>(String.class));
		} catch (Exception e) {
			  
		}
		return subjectList;
	}
	
}
