package com.nmims.daos;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import com.nmims.beans.KnowYourPolicyBean;
import com.nmims.helpers.SFConnection;

@Service
public class KnowYourPolicyDAO extends BaseDAO {
	@Autowired
	@Qualifier("slave1")
	private DataSource slaveDataSource;

	public KnowYourPolicyDAO() {
//		this.connection = SFConnection.getConnection();
//		System.out.println("inPortalDao got connection: "+this.connection);
	}

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	SFConnection sfc;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	public List<KnowYourPolicyBean> getAllGroups() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select groupId,groupName from knowyourpolicy.policygroups";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(KnowYourPolicyBean.class));
	}

	public void savePolicy(String createdBy, String title, String description, int groupId, int categoryId,
			int subcategoryId) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String sql = " insert into knowyourpolicy.policydetails(title,description,groupId,createdBy,createdDate,categoryId,subcategoryId";
		sql = sql + ") values(:title,:description,:groupId,:createdBy,sysdate(),:categoryId,:subcategoryId";
		parameters.addValue("title", title);
		parameters.addValue("description", description);
		parameters.addValue("groupId", groupId);
		parameters.addValue("createdBy", createdBy);
		parameters.addValue("categoryId", categoryId);
		parameters.addValue("subcategoryId", subcategoryId);
		sql = sql + ")";
		namedParameterJdbcTemplate.update(sql, parameters);
	}

	public List<KnowYourPolicyBean> getAllCategory() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select categoryId,categoryName from knowyourpolicy.policycategory";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(KnowYourPolicyBean.class));
	}

	public List<KnowYourPolicyBean> getAllSubcategory() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select subcategoryId,subcategoryName,categoryId from knowyourpolicy.policysubcategory";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(KnowYourPolicyBean.class));
	}

	public List<KnowYourPolicyBean> getAllPolicy() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from knowyourpolicy.policydetails";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(KnowYourPolicyBean.class));
	}

	public void updatepolicy(int policyId, String title, String description, int groupId, int categoryId,
			int subcategoryId, String lastModifiedBy) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " update knowyourpolicy.policydetails set title=:title,description=:description,groupId=:groupId,lastModifiedBy=:lastModifiedBy,lastModifiedDate=sysdate()";
		sql = sql + ",categoryId=:categoryId";
		sql = sql + ",subcategoryId=:subcategoryId";
		sql = sql + " where policyId=:policyId";
		parameters.addValue("title", title);
		parameters.addValue("description", description);
		parameters.addValue("groupId", groupId);
		parameters.addValue("lastModifiedBy", lastModifiedBy);
		parameters.addValue("categoryId", categoryId);
		parameters.addValue("subcategoryId", subcategoryId);
		parameters.addValue("policyId", policyId);
		namedParameterJdbcTemplate.update(sql, parameters);
	}

	public void deletepolicy(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policydetails where policyId=?";
		jdbcTemplate.update(sql, id);

	}

	public void updatecategory(String categoryName, String lastModifiedBy, int categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update knowyourpolicy.policycategory set categoryName=?,lastModifiedBy=?,lastModifiedDate=sysdate() where categoryId=?";
		jdbcTemplate.update(sql, categoryName, lastModifiedBy, categoryId);
	}

	public void deletecategory(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policycategory where categoryId=?";
		jdbcTemplate.update(sql, id);
	}

	public void addcategory(String categoryName, String createdBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into knowyourpolicy.policycategory(categoryName,createdDate,createdBy)  values(?,sysdate(),?)";
		jdbcTemplate.update(sql, categoryName, createdBy);
	}

	public List<KnowYourPolicyBean> fetchsubcategoryusingcategory(int categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select categoryId,subcategoryName,subcategoryId from knowyourpolicy.policysubcategory where categoryId=?";
		return jdbcTemplate.query(sql, new Object[] { categoryId },
				new BeanPropertyRowMapper(KnowYourPolicyBean.class));
	}

	public void updatepolicysubcategory(String subcategoryName, String lastModifiedBy, int subcategoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update knowyourpolicy.policysubcategory set subcategoryName=?,lastModifiedBy=?,lastModifiedDate=sysdate() where subcategoryId=? ";
		jdbcTemplate.update(sql, subcategoryName, lastModifiedBy, subcategoryId);
	}

	public void deletepolicysubcategory(int subcategoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policysubcategory where subcategoryId=?";
		jdbcTemplate.update(sql, subcategoryId);
	}

	public void addsubcategory(String subcategoryName, int categoryId, String createdBy) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "insert into knowyourpolicy.policysubcategory(subcategoryName,categoryId,createdBy,createdDate) values(?,?,?,sysdate())";
		jdbcTemplate.update(sql, subcategoryName, categoryId, createdBy);
	}

	public void deletesubcategorybasedcategory(int categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policysubcategory where categoryId=?";
		jdbcTemplate.update(sql, categoryId);
	}

	public void deletepolicybasedoncategory(int categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policydetails where categoryId=?";
		jdbcTemplate.update(sql, categoryId);
	}

	public void deletepolicybasedonsubcategory(int subcategoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "delete from knowyourpolicy.policydetails where subcategoryId=?";
		jdbcTemplate.update(sql, subcategoryId);
	}
	public int getcategoryIdBySubcategoryId(int subcategoryId) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String sql="select categoryId from knowyourpolicy.policysubcategory where subcategoryId=?";
		return jdbcTemplate.queryForObject(sql,new Object[]{subcategoryId},Integer.class);
	}
}
