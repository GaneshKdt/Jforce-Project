package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.InterviewBean;
import com.nmims.beans.ProgressDetailsBean;

public class ProgressDetailsDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	private static final Logger logger = LoggerFactory.getLogger(ProgressDetailsDAO.class);
 
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ArrayList<ProgressDetailsBean> getFeatures(String packageName, int dutationMax) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql ="SELECT "+
			    	"pf.featureId, f.featureName "+
			    	"FROM "+
			        "products.package_features AS pf "+
			        "LEFT JOIN "+
			        "products.features AS f ON pf.featureId = f.featureId "+
			        "WHERE "+
			        "pf.packageId = (SELECT " + 
			        "packageId " + 
			        "FROM " + 
			        "products.packages " + 
			        "WHERE " + 
			        "packageName = ? "+
			        "AND durationMax = ? ) ";
		
		ArrayList<ProgressDetailsBean> features = (ArrayList<ProgressDetailsBean>)jdbcTemplate.query(sql, 
				new Object[] { packageName, dutationMax },
				new BeanPropertyRowMapper<>(ProgressDetailsBean.class));

		return features;
	}
	
	public ArrayList<ProgressDetailsBean> getPackage() {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT DISTINCT " + 
				"packageName " + 
				"FROM " + 
				"products.packages ";
		
		ArrayList<ProgressDetailsBean> packageName = (ArrayList<ProgressDetailsBean>)jdbcTemplate.query(sql, 
				new BeanPropertyRowMapper<>(ProgressDetailsBean.class));
		
		return packageName;
	}
	
	public ArrayList<ProgressDetailsBean> getDuration(String packageName) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"durationMax " + 
				"FROM " + 
				"products.packages where packageName= ? ";
		
		ArrayList<ProgressDetailsBean> duration = (ArrayList<ProgressDetailsBean>)jdbcTemplate.query(sql, 
				new Object[] { packageName }, 
				new BeanPropertyRowMapper<>(ProgressDetailsBean.class));
		
		return duration;
	}
	
	public String getPackageId(String packageName, int durationMax) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"packageID " + 
				"FROM " + 
				"products.packages " + 
				"WHERE " + 
				"packageName = ? AND durationMax = ?";
		
		String packageId = (String) jdbcTemplate.queryForObject(sql,
				new Object[] { packageName, durationMax}, String.class);
		
		return packageId;
	}
	
	public String getEntitlementId(String packageId, String featureId){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT  " + 
				"    entitlementId " + 
				"FROM " + 
				"    products.entitlements_info " + 
				"WHERE " + 
				"    packageFeaturesId = (SELECT " + 
				"            uid " + 
				"        FROM " + 
				"            products.package_features " + 
				"        WHERE " + 
				"            packageId = ? AND featureId = ? )";
		
		String entitlementId = (String)jdbcTemplate.queryForObject(sql, 
				new Object[] { packageId, featureId }, 
				String.class);
		return entitlementId;
	}
	
	
	public void updateProgress(ProgressDetailsBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		if(checkIfRowExist(bean)) {
			updateCareerCounselling(bean);
		}else {
			 throw new Exception("Check the data uploaded, the values doesn't seem to exist.");  
		}
	}
	
	private boolean checkIfRowExist(ProgressDetailsBean bean) throws Exception {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT " + 
				"    COUNT(*) " + 
				"FROM " + 
				"    products.entitlements_student_data " + 
				"WHERE " + 
				"    sapid = ? " + 
				"        AND entitlementId = ?";
		
		boolean isPresent = (boolean)jdbcTemplate.queryForObject(sql, new Object[] {bean.getSapid(), bean.getEntitlementId()} , 
				Boolean.class);
		
		return isPresent;
		
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void updateCareerCounselling( ProgressDetailsBean bean ) throws Exception{

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			String sql="UPDATE products.entitlements_student_data " + 
					"SET " + 
					"    activated = TRUE, " + 
					"    activationDate = ? " + 
					"WHERE " + 
					"    sapid = ? " + 
					"        AND entitlementId = ? ";
			
			jdbcTemplate.update(sql, new Object[] { bean.getActivationDate(), bean.getSapid(), bean.getEntitlementId()});
			
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			throw e;
		}
	}
	
	/*
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void updatePracticeInterviews( ProgressDetailsBean bean ) throws Exception {

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		try {
			String sql="UPDATE products.entitlements_student_data " + 
					"SET  " + 
					"    activationDate = ?, " + 
					"    dateAdded = sysdate() " + 
					"WHERE " + 
					"    sapid = ? " + 
					"        AND entitlementId = (SELECT  " + 
					"            entitlementId " + 
					"        FROM " + 
					"            products.entitlements_info " + 
					"        WHERE " + 
					"            packageFeaturesId = (SELECT  " + 
					"                    uid " + 
					"                FROM " + 
					"                    products.package_features " + 
					"                WHERE " + 
					"                    packageId = ?" + 
					"                        AND featureId = (SELECT  " + 
					"                            featureId " + 
					"                        FROM " + 
					"                            products.features " + 
					"                        WHERE " + 
					"                            featureName = 'Practice Interviews')))";
	
			jdbcTemplate.update(sql, new Object[] { bean.getPiStartDate(), bean.getSapid(), bean.getPackageId()});
			
		}catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			throw e;
		}
	}
	*/
	
}
