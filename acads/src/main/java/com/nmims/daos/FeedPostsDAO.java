package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EventBean;
import com.nmims.beans.FeedPostsBean;

public class FeedPostsDAO extends BaseDAO{
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}	
 	
	@Transactional(readOnly = true)
	public List<FeedPostsBean> getAllFeedPosts(String acadYear,String acadMonth){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<FeedPostsBean> feedPostsList = null;
		String sql=" SELECT  "+
				"	 p.post_id,	" + 
				"    ssc.acadYear, " + 
				"    ssc.acadMonth, " + 
				"    content, " + 
				"    p.content AS postDescription, " + 
				"    p.filePath AS postURL, " + 
				"    pss.sem AS term, " + 
				"    pss.subject, " + 
				"    p.createdDate AS postedOn, " + 
				"    COALESCE(`pc`.`count`, 0) AS `noOfComments`,"
				+ "	p.userId as facultyName ,"
				+ "	type as postType ,"
				+ "	b.name as batch " + 
				"FROM " + 
				"    lti.post p " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.id = p.subject_config_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id "
				+ "		INNER JOIN  " + 
				"	exam.batch b ON b.id = ssc.batchId	" + 
				"        LEFT JOIN " + 
				"    (SELECT  " + 
				"        post_id, COUNT(*) AS `count` " + 
				"    FROM " + 
				"        lti.post_comments " + 
				"    GROUP BY post_id) pc ON p.post_id = pc.post_id " + 
				"WHERE " + 
				"    ssc.acadYear = ? " + 
				"        AND ssc.acadMonth = ?" ;			
		try {
			feedPostsList = (List<FeedPostsBean>) jdbcTemplate.query(sql, new Object[] {acadYear,acadMonth}, new BeanPropertyRowMapper(FeedPostsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return feedPostsList;
	}

	@Transactional(readOnly = true)
	public ArrayList<FeedPostsBean> getCommentsByPostId(String post_id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FeedPostsBean> feedPostsList = new ArrayList<FeedPostsBean>();
		String sql="select sapid,comment,createdDate as postedOn from lti.post_comments where post_id = ?" ;			
		try {
			feedPostsList = (ArrayList<FeedPostsBean>) jdbcTemplate.query(sql, new Object[] {post_id}, new BeanPropertyRowMapper(FeedPostsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return feedPostsList;
		
	}
		
	@Transactional(readOnly = true)
	public List<FeedPostsBean> getAllFeedComments(String acadYear,String acadMonth){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<FeedPostsBean> feedCommentsList = null;
		String sql="SELECT  " + 
				"    ssc.acadYear, " + 
				"    ssc.acadMonth, " + 
				"    pst.content AS postDescription, " + 
				"    pst.filePath AS postURL, " + 
				"    pst.userId AS facultyName, " + 
				"    pst.createdDate AS postPostedOn, " + 
				"    c.sapid, " + 
				"    c.comment, " + 
				"    c.createdDate AS commentPostedOn, " + 
				"    pss.sem AS term, " + 
				"    pss.subject, " + 
				"    b.name AS batch " + 
				"FROM " + 
				"    lti.post_comments c " + 
				"        INNER JOIN " + 
				"    lti.post pst ON pst.post_id = c.post_id " + 
				"        INNER JOIN " + 
				"    lti.student_subject_config ssc ON ssc.id = pst.subject_config_id " + 
				"        INNER JOIN " + 
				"    exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
				"        INNER JOIN " + 
				"    exam.batch b ON b.id = ssc.batchId " + 
				"WHERE " + 
				"    c.post_id IN (SELECT  " + 
				"            p.post_id " + 
				"        FROM " + 
				"            lti.post p " + 
				"                INNER JOIN " + 
				"            lti.student_subject_config ssc ON ssc.id = p.subject_config_id " + 
				"                INNER JOIN " + 
				"            exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id " + 
				"                INNER JOIN " + 
				"            exam.batch b ON b.id = ssc.batchId " + 
				"                LEFT JOIN " + 
				"            (SELECT  " + 
				"                post_id, COUNT(*) AS `count` " + 
				"            FROM " + 
				"                lti.post_comments " + 
				"            GROUP BY post_id) pc ON p.post_id = pc.post_id " + 
				"        WHERE " + 
				"            ssc.acadYear = ? " + 
				"                AND ssc.acadMonth = ?) " + 
				"ORDER BY c.post_id" ;			
		try {
			feedCommentsList = (List<FeedPostsBean>) jdbcTemplate.query(sql, new Object[] {acadYear,acadMonth}, new BeanPropertyRowMapper(FeedPostsBean.class));
		} catch (DataAccessException e) {
			  
		}
		return feedCommentsList;
	}
	

}
