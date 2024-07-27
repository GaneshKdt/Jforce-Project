package com.nmims.daos;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("gatewayTransactionDao")
public class GatewayTransactionsDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Transactional(readOnly = true)
	public String getPaymentOptionByTrackId(String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String paymentOption = null;
		String sql = "select payment_option from payment_gateway.transaction  where track_id =  ? ";
		try {
			paymentOption = jdbcTemplate.queryForObject(sql, new Object[] {trackId}, String.class);
		} catch (Exception e) {
		}
		return paymentOption;
	}
	
}
