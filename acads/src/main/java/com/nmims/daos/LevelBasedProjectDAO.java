package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.VivaSlotBookingBean;

@Component
public class LevelBasedProjectDAO {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional(readOnly = false)
	public List<VivaSlotBookingBean> getVivaSlotByFacultyId(String facultyId) {
		try {
			String sql = "select vsb.year,vsb.month,vsb.sapid,vsb.meetingkey,vsb.hostId,vs.date as date,vs.start_time as start_time,vs.end_time as end_time from exam.viva_slot_booking vsb INNER JOIN exam.viva_slots vs ON vs.id = vsb.viva_slots_id  where  vsb.booked='Y' and vsb.facultyId = ?;";
			return jdbcTemplate.query(sql, new Object[] {facultyId},new BeanPropertyRowMapper<VivaSlotBookingBean>(VivaSlotBookingBean.class));
		}catch (Exception e) {
			// TODO: handle exception
			  
			return new ArrayList<VivaSlotBookingBean>();
		}
	}
	
}
