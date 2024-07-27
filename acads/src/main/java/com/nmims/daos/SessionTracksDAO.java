package com.nmims.daos;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.SessionTrackBean;
@Component("sessionTracksDao")
public class SessionTracksDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional(readOnly=true)
	public ArrayList<String> getTrackNames(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT track FROM acads.tracks WHERE active = 'Y' ";
		ArrayList<String> tracks = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return tracks;
	}
	
	@Transactional(readOnly=true)
	public ArrayList<SessionTrackBean> getAllTracksDetails() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.tracks WHERE active = 'Y' ";
		ArrayList<SessionTrackBean> tracksDetails = (ArrayList<SessionTrackBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(SessionTrackBean.class));
		return tracksDetails;
	}
	
	@Transactional(readOnly=false)
	public int updateSessionTrackColor(SessionTrackBean trackBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "UPDATE acads.tracks SET hexCode = ?, border = ?, fontColor = ? WHERE track = ? ";
		int result = jdbcTemplate.update(sql, new Object[]{trackBean.getHexCode(), trackBean.getBorder(), trackBean.getFontColor(), trackBean.getTrack()});
		return result;
	}
	
	@Transactional(readOnly=false)
	public ArrayList<String> getListOfTracksName(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " SELECT DISTINCT track FROM acads.sessions WHERE track <> ''";
		ArrayList<String> tracks = (ArrayList<String>)jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return tracks;
	}
		
	@Transactional(readOnly=false)
	public void insertTrackDetails(SessionTrackBean sessionTrackBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql=" INSERT INTO acads.tracks (track, border, fontColor, " + 
				"					  active, hexCode, colorClass) " + 
				"					  VALUES (?,?,?,?,?,?)";
		 jdbcTemplate.update(sql, new Object[] {sessionTrackBean.getTrack(),sessionTrackBean.getBorder(),sessionTrackBean.getFontColor(),
				"Y",sessionTrackBean.getHexCode(),sessionTrackBean.getColorClass()});		
	}
	
}