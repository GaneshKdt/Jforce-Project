package com.nmims.daos;



import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MBACentersBean;

@Repository("mbawxexamCenterDAO")
public class MBAWXCentersDAO extends BaseDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

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
	public List<MBACentersBean> getCentersList(){
		try {
			String sql = "SELECT * FROM `exam`.`mba_wx_centers`";
			jdbcTemplate = new JdbcTemplate(dataSource);
			List<MBACentersBean> centersBeansList = jdbcTemplate.query(sql, new Object[] {},
					new BeanPropertyRowMapper<MBACentersBean>(MBACentersBean.class));
			return centersBeansList;
		}
		catch (Exception e) {
			// TODO: handle exception
			return new ArrayList<MBACentersBean>();
		}
	}
	
	@Transactional(readOnly = true)
	public List<MBACentersBean> getActiveCentersList(){
		String sql = "SELECT * FROM `exam`.`mba_wx_centers` WHERE `active` = 'Y'";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<MBACentersBean> centersBeansList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper<MBACentersBean>(MBACentersBean.class));
		return centersBeansList;
	}
	
	@Transactional(readOnly = false)
	public String deleteCenterById(String centerId){
		try {
			String sql = "delete from exam.mba_wx_centers where centerId=?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			int count = jdbcTemplate.update(sql, new Object[] {
					centerId
			});
			if(count > 0) {
				return "Successfully record deleted, centerId: " + centerId;
			}
			return "Failed to delete record, centerId: " + centerId;
		}
		catch (Exception e) {
			// TODO: handle exception
			return e.getMessage();
		}
	}
	
	@Transactional(readOnly = false)
	public List<MBACentersBean> batchInsertCenters(final List<MBACentersBean> centersBeansList) {
		// Insert tracking numbers for current interaction
		String sql = ""
				+ " INSERT INTO `exam`.`mba_wx_centers` "
				+ " ( "
					+ " `name`, `city`, `state`, "
					+ " `capacity`, `address`, `googleMapUrl`, "
					+ " `locality`, `active`, "
					+ " `createdBy`, `lastModifiedBy` "
				+ " ) "
				+ " VALUES "
				+ " ( "
					+ " ?, ?, ?, "
					+ " ?, ?, ?, "
					+ " ?, ?, "
					+ " ?, ? "
				+ " ) ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		List<MBACentersBean> toReturn = new ArrayList<MBACentersBean>();
		for (MBACentersBean center : centersBeansList) {
			try {
				jdbcTemplate.update(
					sql, 
					new Object[] {
						center.getName(), center.getCity(), center.getState(),
						center.getCapacity(), center.getAddress(), center.getGoogleMapUrl(),
						center.getLocality(), center.getActive(), 
						center.getCreatedBy(), center.getLastModifiedBy()
					}
				);
			}catch (Exception e) {
				
				center.setError("Error inserting record : " + e.getLocalizedMessage());
			}
			toReturn.add(center);
		}
		return toReturn;
	}
	
	@Transactional(readOnly = true)
	public MBACentersBean getCenterById(Long centerId) {
		
		String sql = ""
				+ " SELECT * "
				+ " FROM `exam`.`mba_wx_centers` "
				+ " WHERE `centerId` = ? ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		MBACentersBean bean = jdbcTemplate.queryForObject(
			sql, 
			new Object[] { centerId },
			new BeanPropertyRowMapper<MBACentersBean>(MBACentersBean.class)
		);
		return bean;
	}
	
	@Transactional(readOnly = false)
	public void updateCenterDetails(MBACentersBean centerBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_wx_centers` "
				+ " SET "
				+ " `name` = ?, `city` = ?, `state` = ?, "
				+ " `capacity` = ?, `address` = ?, `googleMapUrl` = ?, "
				+ " `locality` = ?, `active` = ?, "
				+ " `lastModifiedBy` = ?, `lastModifiedOn` = sysdate() "
				+ " WHERE `centerId` = ? ";
		
		jdbcTemplate.update(
			sql, 
			new Object[] {
				centerBean.getName(), centerBean.getCity(), centerBean.getState(),
				centerBean.getCapacity(), centerBean.getAddress(), centerBean.getGoogleMapUrl(),
				centerBean.getLocality(), centerBean.getActive(), 
				centerBean.getLastModifiedBy(),
				centerBean.getCenterId()
			}
		);
	}
	
	@Transactional(readOnly = false)
	public void toggleCenterActive(String centerId, String active, String userId) {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = ""
				+ " UPDATE `exam`.`mba_wx_centers` "
				+ " SET "
				+ " `active` = ?, `lastModifiedBy` = ?, `lastModifiedOn` = sysdate() "
				+ " WHERE `centerId` = ? ";
		
		jdbcTemplate.update(
				sql, 
				new Object[] { active, userId, centerId }
		);
	}

}
