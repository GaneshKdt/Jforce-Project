package com.nmims.paymentgateways.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;



import org.springframework.stereotype.Repository;

import com.nmims.paymentgateways.bean.PaymentOptionsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class PaymentOptionsDAO {
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	class PaymentOptionsRowMapper implements RowMapper<PaymentOptionsBean>{

		@Override
		public PaymentOptionsBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			PaymentOptionsBean paymentOptionsBean = new PaymentOptionsBean();
			paymentOptionsBean.setId(rs.getString("id"));
			paymentOptionsBean.setImage(rs.getString("image"));
			paymentOptionsBean.setName(rs.getString("name"));
			paymentOptionsBean.setExambookingActive(rs.getString("exambookingActive"));
			return paymentOptionsBean;
		}

	}
	
	public ArrayList<PaymentOptionsBean> getActivePaymentGateway(){
		String sql = "SELECT * FROM portal.payment_options where active = 'Y';";
		return (ArrayList<PaymentOptionsBean>) jdbcTemplate.query(sql,new PaymentOptionsRowMapper());
	}
	
}
