package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.VideoContentCareerservicesBean;

public class VideoRecordingDao {

	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(VideoRecordingDao.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public boolean uploadVideoContent(VideoContentCareerservicesBean videoContent) {
		
		return saveVideoContent(videoContent);
	}
	
	public boolean updateVideoContent(VideoContentCareerservicesBean videoContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql= ""
				+ "UPDATE "
				+ "`products`.`video_content`"
					+ "SET"
					+ "`videoTypeId` = ?,"
					+ "`sessionId` = ?,"
					+ "`fileName` = ?,"
					+ "`facultyId` = ?,"
					+ "`keywords` = ?,"
					+ "`description` = ?,"
					+ "`defaultVideo` = ?,"
					+ "`duration` = ?,"
					+ "`videoTitle` = ?,"
					+ "`lastModifiedDate` = CURDATE(),"
					+ "`lastModifiedBy` = ?,"
					+ "`videoLink` = ?,"
					+ "`thumbnailUrl` = ?,"
					+ "`mobileUrlHd` = ?,"
					+ "`mobileUrlSd1` = ?,"
					+ "`mobileUrlSd2` = ?"
				+ "WHERE "
					+ "`id` = ?";
		try {
			jdbcTemplate.update(sql,new Object[] {
					videoContent.getVideoTypeId(), videoContent.getSessionId(), videoContent.getFileName(),
					videoContent.getFacultyId(), videoContent.getKeywords(), videoContent.getDescription(), 
			        videoContent.getDefaultVideo(),  videoContent.getDuration(), videoContent.getVideoTitle(), 
			        videoContent.getLastModifiedBy(), videoContent.getVideoLink(), videoContent.getThumbnailUrl(), 
			        videoContent.getMobileUrlHd(), videoContent.getMobileUrlSd1(), videoContent.getMobileUrlSd2(),
			        videoContent.getId()
			});
			return true;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
	
	public boolean saveVideoContent(final VideoContentCareerservicesBean videoContent) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			String sql = "INSERT INTO "
					+ "`products`.`video_content`"
					+ "( "
						+ "`videoTypeId`, `sessionId`, `fileName`, "
						+ "`facultyId`, `keywords`, `description`, "
						+ "`defaultVideo`, `duration`, `videoTitle`, "
						+ "`addedOn`, `addedBy`, `createdBy`, "
						+ "`createdDate`, `lastModifiedDate`, `lastModifiedBy`, "
						+ "`videoLink`, `thumbnailUrl`, `mobileUrlHd`, "
						+ "`mobileUrlSd1`, `mobileUrlSd2` "
					+ ")"
					+ "VALUES"
					+ "("
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "?, ?, ?, "
						+ "CURDATE(), ?, ?, "
						+ "CURDATE(), CURDATE(), ?, "
						+ "?, ?, ?, "
						+ "?, ?"
					+ ")";
			
			
			jdbcTemplate.update(sql, 
					new Object[] {
							videoContent.getVideoTypeId(), videoContent.getSessionId(), videoContent.getFileName(),
							videoContent.getFacultyId(), videoContent.getKeywords(), videoContent.getDescription(), 
					        videoContent.getDefaultVideo(),  videoContent.getDuration(), videoContent.getVideoTitle(), 
					        videoContent.getAddedBy(), videoContent.getCreatedBy(), 
					        videoContent.getLastModifiedBy(), 
					        videoContent.getVideoLink(), videoContent.getThumbnailUrl(), videoContent.getMobileUrlHd(), 
					        videoContent.getMobileUrlSd1(), videoContent.getMobileUrlSd2()
					});
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return false;
		}
	
		return true;
	}

	public VideoContentCareerservicesBean getVideoContentById(String id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ "SELECT "
					+ "* "
				+ "FROM "
				+ "`products`.`video_content` `vc` "
					+ "LEFT JOIN "
					+ "`products`.`video_content_type` `vct` "
					+ "ON "
					+ "`vc`.`videoTypeId` = `vct`.`videoTypeId` "
				+ "WHERE "
					+ "`vc`.`id` = ?";
		try {
			VideoContentCareerservicesBean videoContent = jdbcTemplate.queryForObject(
					 sql, 
					 new Object[] {
							 id
					 }, 
					 new BeanPropertyRowMapper<VideoContentCareerservicesBean>(VideoContentCareerservicesBean.class));
			return videoContent;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}

	public List<VideoContentCareerservicesBean> getAllVideoContent(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ "SELECT "
				+ "concat(`f`.`firstName` , ' ',  `f`.`lastName`) `facultyName`, "
				+ "`vc`.*, "
				+ "`vct`.*, "
				+ "`s`.`date` AS `sessionDate`, "
				+ "`s`.`sessionName` "
				+ "FROM "
				+ "`products`.`video_content` `vc` "
				+ "LEFT JOIN "
				+ "`products`.`video_content_type` `vct` "
				+ "ON "
				+ "`vc`.`videoTypeId` = `vct`.`videoTypeId` "
				+ "LEFT JOIN "
				+ "`products`.`sessions` `s` "
				+ "ON "
				+ "`s`.`id` = `vc`.`sessionId` "
				+ "LEFT JOIN "
				+ "`acads`.`faculty` `f` "
				+ "ON "
				+ "`f`.`facultyId` = `s`.`facultyId`";
		try {
			List<VideoContentCareerservicesBean> videoContent = jdbcTemplate.query(
					 sql, 
					 new BeanPropertyRowMapper<VideoContentCareerservicesBean>(VideoContentCareerservicesBean.class));
			return videoContent;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}
	
	public List<VideoContentCareerservicesBean> getAllVideoContentByTypeId(int id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`video_content` `vc` "
				+ "LEFT JOIN "
				+ "`products`.`video_content_type` `vct` "
				+ "ON "
				+ "`vc`.`videoTypeId` = `vct`.`videoTypeId` "
				+ "WHERE "
				+ "`vct`.`videoTypeId` = ?";
		try {
			List<VideoContentCareerservicesBean> videoContent = jdbcTemplate.query(
					 sql,  
					 new Object[] {
							 id
					 }, 
					 new BeanPropertyRowMapper<VideoContentCareerservicesBean>(VideoContentCareerservicesBean.class));
			return videoContent;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
			return null;
		}
	}

	public boolean deleteVideoContent(String id) {
		
		String sql = "delete from `products`.`video_content` where `id`=?";
		try {
			 jdbcTemplate.update(
					sql, 
					new Object[] {
							 id
				 	}
			 );
			 return true;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
	
	public List<SessionDayTimeBean> getSessionsWithoutVideoContent(){
		String sql = "SELECT * FROM `products`.`sessions` `s` WHERE `s`.`date` <= CURDATE() AND `s`.`id` NOT IN"
					+ "(SELECT sessionId FROM products.video_content WHERE sessionId IS NOT NULL) ;";
		try {
			List<SessionDayTimeBean> sessions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<SessionDayTimeBean>(SessionDayTimeBean.class));
			return sessions;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			return new ArrayList<SessionDayTimeBean>();
		}
	}
	
	public boolean checkIfRecordingExistsForSessionId(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`products`.`video_content` `vc` "
				+ "LEFT JOIN "
				+ "`products`.`video_content_type` `vct` "
				+ "ON "
				+ "`vc`.`videoTypeId` = `vct`.`videoTypeId` "
				+ "WHERE "
				+ "`vc`.`sessionId` = ?";
		try {
			int count = jdbcTemplate.queryForObject(
					 sql, 
					 new Object[] {
						 id
					 }, 
					 Integer.class);
			if(count > 0) {
				return true;
			}
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
	public VideoContentCareerservicesBean getVideoContentBySessionId(String id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`products`.`video_content` `vc` "
				+ "LEFT JOIN "
				+ "`products`.`video_content_type` `vct` "
				+ "ON "
				+ "`vc`.`videoTypeId` = `vct`.`videoTypeId` "
				+ "WHERE "
				+ "`vc`.`sessionId` = ?"
				+ "LIMIT 1";
		try {
			VideoContentCareerservicesBean videoContentBean = jdbcTemplate.queryForObject(
					 sql, 
					 new Object[] {
						 id
					 }, 
					 new BeanPropertyRowMapper<VideoContentCareerservicesBean>(VideoContentCareerservicesBean.class));
			return videoContentBean;
		} catch (DataAccessException e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}
	
}
