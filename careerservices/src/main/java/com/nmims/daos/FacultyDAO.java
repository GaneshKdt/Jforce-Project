package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.FacultyCareerservicesBean;


public class FacultyDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Value("${SERVER_PATH}")
	String SERVER_PATH;

	private static final Logger logger = LoggerFactory.getLogger(FacultyDAO.class);
 
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	public FacultyCareerservicesBean findfacultyByFacultyId(String facultyId) {
		String sql = "SELECT * FROM acads.faculty WHERE facultyId = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyCareerservicesBean faculty = (FacultyCareerservicesBean) jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));

		if(faculty != null && faculty.getImgUrl() != null) {
			faculty.setImgUrl(SERVER_PATH + faculty.getImgUrl());
		}
		return faculty;
	}
	
	public List<FacultyCareerservicesBean> getAllFacultiesNotInCS() {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`acads`.`faculty` "
				+ "WHERE "
				+ "`id` "
				+ "NOT IN "
				+ "("
					+ "SELECT "
					+ "`facultyTableId` AS `id` "
					+ "from "
					+ "`products`.`speakers` "
					+ "`s`"
				+ ")";

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<FacultyCareerservicesBean> faculty = jdbcTemplate.query(sql, new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
		List<FacultyCareerservicesBean> facultyToReturn = new ArrayList<FacultyCareerservicesBean>();
		
		for (FacultyCareerservicesBean facultyBean : faculty) {
			if(facultyBean != null && facultyBean.getImgUrl() != null) {
				facultyBean.setImgUrl(SERVER_PATH + facultyBean.getImgUrl());
			}
			facultyToReturn.add(facultyBean);
		}
		return facultyToReturn;
	}

	public FacultyCareerservicesBean getSpeakerDetails(String facultyId) {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`acads`.`faculty` `f` "
				+ "LEFT JOIN "
				+ "`products`.`speakers` `s` "
				+ "ON "
				+ "`s`.`facultyTableId` = `f`.`id` "
				+ "WHERE facultyId = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		FacultyCareerservicesBean faculty = (FacultyCareerservicesBean) jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
		if(faculty != null && faculty.getImgUrl() != null) {
			faculty.setImgUrl(SERVER_PATH + faculty.getImgUrl());
		}
		return faculty;
	}

	public List<FacultyCareerservicesBean> getAllSpeakerDetails() {
		String sql = "SELECT "
				+ "* "
				+ "FROM "
				+ "`acads`.`faculty` `f` "
				+ "RIGHT JOIN "
				+ "`products`.`speakers` `s` "
				+ "ON "
				+ "`s`.`facultyTableId` = `f`.`id`";

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<FacultyCareerservicesBean> facultyList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
		List<FacultyCareerservicesBean> facultyToReturn = new ArrayList<FacultyCareerservicesBean>();
		for (FacultyCareerservicesBean facultyBean : facultyList) {
			if(facultyBean != null && facultyBean.getImgUrl() != null) {
				facultyBean.setImgUrl(SERVER_PATH + facultyBean.getImgUrl());
			}
			facultyToReturn.add(facultyBean);
		}
		return facultyList;
	}

	public boolean checkIfFacultyIsForCS(String facultyId) {
		String sql = "SELECT "
				+ "count(*) "
				+ "FROM "
				+ "`acads`.`faculty` `f` "
				+ "LEFT JOIN "
				+ "`products`.`speakers` `s` "
				+ "ON "
				+ "`s`.`facultyTableId` = `f`.`id` "
				+ "WHERE `f`.`id` = ?";
		
		try {
			int faculty = jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, Integer.class);

			if(faculty == 0) {
				return false;
			}else {
				return true;
			}
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}

	public boolean addFacultyForCS(String facultyId) {
		
		if(checkIfFacultyIsForCS(facultyId)) {
			return true;
		}
		String facultyTableId = getTableIdFromFacultyId(facultyId);
		if(facultyTableId == null) {
			return false;
		}
		String sql = "INSERT INTO `products`.`speakers`"
				+ "("
				+ "`facultyTableId`"
				+ ")"
				+ "VALUES "
				+ "("
				+ "?"
				+ ")";

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			jdbcTemplate.update(sql, new Object[] { facultyTableId });
			return true;
		}catch (Exception e) {
			return false;
		}
	}

	public boolean deleteCSFaculty(String facultyId) {
		
		String facultyTableId = getTableIdFromFacultyId(facultyId);
		if(facultyTableId == null) {
			return false;
		}
		
		String sql = "DELETE FROM `products`.`speakers`"
				+ "WHERE "
				+ "`facultyTableId` =  ?";

		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			jdbcTemplate.update(sql, new Object[] { facultyTableId });
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	private String getTableIdFromFacultyId(String facultyId) {
		String sql = "SELECT "
				+ "`id` "
				+ "FROM "
				+ "`acads`.`faculty` `f` "
				+ "WHERE `f`.`facultyId` = ?";
		
		try {
			String faculty = jdbcTemplate.queryForObject(sql, new Object[] { facultyId }, String.class);
			return faculty;
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return null;
	}

	public ArrayList<FacultyCareerservicesBean> getAllFacultyRecords() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM acads.faculty where active = 'Y' ";
		ArrayList<FacultyCareerservicesBean> facultyNameAndIdList = null;
		try{
			facultyNameAndIdList = (ArrayList<FacultyCareerservicesBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<FacultyCareerservicesBean>(FacultyCareerservicesBean.class));
			List<FacultyCareerservicesBean> facultyToReturn = new ArrayList<FacultyCareerservicesBean>();
			for (FacultyCareerservicesBean facultyBean : facultyNameAndIdList) {
				if(facultyBean != null && facultyBean.getImgUrl() != null) {
					facultyBean.setImgUrl(SERVER_PATH + facultyBean.getImgUrl());
				}
				facultyToReturn.add(facultyBean);
			}
			return facultyNameAndIdList;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
			return null;
		}

	}

	public boolean updateCSFaculty(FacultyCareerservicesBean facultyBean) {
		String sql = ""
				+ "UPDATE `products`.`speakers`"
				+ "SET "
				+ "`speakerLinkedInProfile` = ?, "
				+ "`speakerTwitterProfile` = ?, "
				+ "`speakerFacebookProfile` = ? "
				+ "WHERE `facultyTableId` = ?";
		try{
			jdbcTemplate.update(
					sql,
					new Object[] { 
							facultyBean.getSpeakerLinkedInProfile(),
							facultyBean.getSpeakerTwitterProfile(),
							facultyBean.getSpeakerFacebookProfile(),
							facultyBean.getId()
						}
					);
			return true;
		}catch(Exception e){
			logger.info("exception : "+e.getMessage());
		}
		return false;
	}
}
