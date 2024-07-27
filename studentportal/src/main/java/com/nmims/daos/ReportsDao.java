package com.nmims.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.nmims.beans.ReportBean;

public class ReportsDao extends BaseDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
	}

	@Override
	public void setBaseDataSource() {
		// TODO Auto-generated method stub
		this.baseDataSource = this.dataSource;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<ReportBean> getPowerbiReportsList(List<String> reportCategoryList) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportCategoryList", reportCategoryList);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		String sql = "SELECT * FROM powerbi_reports.powerbi_reportlist  where category in (:reportCategoryList)order by category";
		List<ReportBean> reportList = namedParameterJdbcTemplate.query(sql, params,
				new BeanPropertyRowMapper<>(ReportBean.class));
		return reportList;
	}
}
