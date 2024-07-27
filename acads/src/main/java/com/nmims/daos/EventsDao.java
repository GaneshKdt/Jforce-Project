package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.EventBean;

public class EventsDao extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

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
	public long saveEvent(final EventBean event) {
	jdbcTemplate = new JdbcTemplate(dataSource);
	GeneratedKeyHolder holder = new GeneratedKeyHolder();
	try {
		jdbcTemplate.update(new PreparedStatementCreator() {
		    @Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		        PreparedStatement statement = con.prepareStatement("INSERT INTO acads.events"
		        		+ " (eventName,description,startDateTime,endDateTime,createdBy,createdDateTime,lastModifiedBy,lastModifiedDateTime) "
		        		+ " VALUES(?,?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, event.getEventName());
		        statement.setString(2, event.getDescription());
		        statement.setString(3, event.getStartDateTime());
		        statement.setString(4, event.getEndDateTime());
		        statement.setString(5, event.getCreatedBy());
		        statement.setString(6, event.getCreatedDateTime());
		        statement.setString(7, event.getLastModifiedBy());
		        statement.setString(8, event.getLastModifiedDateTime());
		        return statement;
		    }
		}, holder);
	} catch (DataAccessException e) {
		  
	}

	long primaryKey = holder.getKey().longValue();
	return primaryKey;
	}
	
	@Transactional(readOnly = false)
	public boolean updateEvent(EventBean event) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="update acads.events set "
						+ " eventName=?, "
						+ "description=?, "
						+ "startDateTime=?, "
						+ "endDateTime=?, "
						+ "lastModifiedBy=?, "
						+ "lastModifiedDateTime=sysdate()"
							+ "where id=?";
		try {
			jdbcTemplate.update(sql,new Object[] {event.getEventName(),
												  event.getDescription(), 
												  event.getStartDateTime(),
												  event.getEndDateTime(),
												  event.getLastModifiedBy(), 
												  event.getId()
								});
			return true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			  
		}
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<EventBean> getAllEvents(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<EventBean> eventsList=null;
		String sql="select * from acads.events Order By createdDateTime desc";
		try {
			 eventsList = (List<EventBean>) jdbcTemplate.query(sql, new Object[] {}, new BeanPropertyRowMapper(EventBean.class));
		} catch (DataAccessException e) {
			  
		}
		return eventsList;
	}
	
	@Transactional(readOnly = true)
	public EventBean getEventById(int id){
		jdbcTemplate = new JdbcTemplate(dataSource);
		EventBean event=null;
		String sql="select * from acads.events where id=?";
		try {
			 event = (EventBean) jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper(EventBean.class));
		} catch (DataAccessException e) {
			  
		}
		return event;
	}
	
	@Transactional(readOnly = false)
	public int deleteEvent(int id) {
	int row=0;	
	String sql="delete from acads.events where id=?";
	try {
		 row = jdbcTemplate.update(sql, new Object[] {id});
	} catch (DataAccessException e) {
		  
	}
	return row;
	}
	

}
