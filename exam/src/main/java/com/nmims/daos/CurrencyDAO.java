package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.CurrencyMappingBean;

public class CurrencyDAO extends BaseDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;		
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		setBaseDataSource();
	}
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
		
	}
	
	@Transactional(readOnly = false)
	public ArrayList<CurrencyMappingBean> getAllCurrencyValue() {
		ArrayList<CurrencyMappingBean> currencyList=new ArrayList<>();
		try {
		String sql = "select id,feeType_id as feeId,consumerProgramStructureId as consumerProgramStructureId , currency_id as currencyId ,price from currency.currency_master_mapping; ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		currencyList = (ArrayList<CurrencyMappingBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CurrencyMappingBean.class));
		 return currencyList;
		}catch(Exception e) {
			 return currencyList;
		}
	}
	@Transactional(readOnly = false)
	public Map<Integer,String> getCurrency() {
		Map<Integer,String> map=new HashMap<>();
		try{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = "Select id as currencyId,name as currencyName from currency.fee_currency";
			ArrayList<CurrencyMappingBean> currencyList = (ArrayList<CurrencyMappingBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(CurrencyMappingBean.class));
			for (CurrencyMappingBean currency : currencyList) {
				map.put(currency.getCurrencyId(), currency.getCurrencyName());
			}
			return map;
		 
		}catch(Exception e) {
			e.printStackTrace();
			
			return map;	
		} 
	}
	@Transactional(readOnly = false)
	public List<Integer> getMasterKey(String consumerType,ArrayList<String> programId,ArrayList<String> programStructureID) {
		ArrayList<Integer> masterkey=new ArrayList<Integer>();
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT id FROM exam.consumer_program_structure where programId in (:programId) and programStructureId in (:programStructureID) and consumerTypeId in (:consumerType)";

		MapSqlParameterSource paramMap=new MapSqlParameterSource();
		paramMap.addValue("programId", programId);
		paramMap.addValue("programStructureID", programStructureID);
		paramMap.addValue("consumerType", consumerType);
		
		
		masterkey = (ArrayList<Integer>)namedParameterJdbcTemplate.query(sql, paramMap,new SingleColumnRowMapper<>(Integer.class));
		
		}
		catch(Exception e) {
			e.printStackTrace();
			masterkey.add(Integer.parseInt("No Data Avaliable"));
			return masterkey;
		}
		 return masterkey;
	}
	@Transactional(readOnly = false)
	public HashMap<Integer,String> getFeeType(){
		HashMap<Integer,String> map=new HashMap<Integer, String>();
		try {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="select fee_id as feeId,fee_name as feeName from currency.fee_category";
		ArrayList<CurrencyMappingBean> feeType=(ArrayList<CurrencyMappingBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CurrencyMappingBean.class));
		 for(CurrencyMappingBean currency:feeType) {
			 map.put(currency.getFeeId(), currency.getFeeName());
		 }
		 return map;
		}catch(Exception e) {
			return map;
		}
	
	}
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public int saveCurrencyDetails(ArrayList<CurrencyMappingBean> currencyList) {	
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="insert into currency.currency_master_mapping(feeType_Id,consumerProgramStructureId,currency_id,price)values(?,?,?,?);";
			int[] teeMarksIds =jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CurrencyMappingBean currency=currencyList.get(i);
				//System.out.println("currency Insert"+currency);
				ps.setInt(1, currency.getFeeId());
				ps.setInt(2, currency.getConsumerProgramStructureId());
				ps.setInt(3, currency.getCurrencyId());
				ps.setDouble(4, currency.getPrice());
				
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return currencyList.size();
			}
		});
		 return currencyList.size();
		
	
	}catch(Exception e) {
		e.printStackTrace();
	  return currencyList.size();
	}
		
	}

	@Transactional (readOnly = false)
	public HashMap<String, String> updateCurrencyDetails(CurrencyMappingBean bean) {
	
			jdbcTemplate = new JdbcTemplate(dataSource);
			HashMap<String,String> message = new HashMap<String,String>();
			String sql = "update currency.currency_master_mapping set price=? where id=?";
			
			try {
				jdbcTemplate.update(sql, new Object[] { 
					bean.getPrice(),
					bean.getId()
				});
				
				message.put("success", "Successfully  Inserted");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message.put("error", "UnSuccessfull, Not Inserted");
			}
			return message;
		}

//
//	public int saveUniqueCurrencyValue(String currencyName) {
//		int count=0;
//		try {
//			jdbcTemplate = new JdbcTemplate(dataSource);
//			String sql="Insert into currency.fee_currency(name) values(?)";
//			jdbcTemplate.update(sql,new Object[] {currencyName});
//			count =1;
//		}catch(Exception e) {
//			e.printStackTrace();
//			return count;
//		}
//		return count;
//	}

	public HashMap<Integer,CurrencyMappingBean> getMapProgramsById() {
		HashMap<Integer,CurrencyMappingBean> map=new HashMap<Integer, CurrencyMappingBean>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="SELECT DISTINCT cps.id as id, p.code AS program, ps.program_structure AS programStructure,   ct.name AS consumerType "
				+ " FROM exam.consumer_program_structure cps "
				+ " INNER JOIN exam.program p ON cps.programId = p.id "
				+ " INNER JOIN exam.program_structure ps ON ps.id = cps.programStructureId "
                + " INNER JOIN exam.consumer_type ct ON ct.id = cps.consumerTypeId "
                + " ORDER BY id "; 
		ArrayList<CurrencyMappingBean> MapList=(ArrayList<CurrencyMappingBean>) jdbcTemplate.query(sql,new BeanPropertyRowMapper(CurrencyMappingBean.class));
		for(CurrencyMappingBean bean:MapList) {
			map.put(bean.getId(), bean);
		}
		return map;
	}

	
	
}
