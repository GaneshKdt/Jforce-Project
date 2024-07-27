package com.nmims.daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.MassUploadTrackingSRBean;

@Repository
public class MassUploadTrackingSRDAOImpl implements MassUploadTrackingSRDAO{
	
	/*Variables*/
	@Autowired
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	//saving sr tracking details into database
	@Override
	@Transactional(readOnly = false)
	public void saveSrExcelRecord(List<MassUploadTrackingSRBean> srUploadList) throws SQLException {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" INSERT into portal.servicerequest_trackingrecords (serviceRequestId,trackId,courierName,url,mailStatus,createdDate,createdBy,lastModifiedDate,lastModifiedBy) "
				+ " VALUES (?,?,?,?,'N',sysdate(),?,sysdate(),?) "
				+ " ON DUPLICATE KEY UPDATE "
				+ " trackId=values(trackId),"
				+ " courierName=values(courierName),"
				+ " url=values(url),"
				+ " mailStatus=values(mailStatus),"
				+ " createdDate=sysdate(), "
				+ " createdBy=values(createdBy), "
				+ " lastModifiedDate=values(lastModifiedDate), "
				+ " lastModifiedBy=values(lastModifiedBy)";
		 jdbcTemplate.batchUpdate(sql,
				new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				MassUploadTrackingSRBean massUploadTrackingSRBean = srUploadList.get(i);
				ps.setInt(1, massUploadTrackingSRBean.getServiceRequestId());
				ps.setString(2, massUploadTrackingSRBean.getTrackId());	
				ps.setString(3, massUploadTrackingSRBean.getCourierName());	
				ps.setString(4, massUploadTrackingSRBean.getUrl());	
				ps.setString(5, massUploadTrackingSRBean.getCreatedBy());	
				ps.setString(6, massUploadTrackingSRBean.getLastModifiedBy());	
			}
			
			@Override
			public int getBatchSize() {
				
				return srUploadList.size();
			}
		 });
	 }

	//getting all sr tracking deatils
	@Override
	@Transactional(readOnly = true)
	public List<MassUploadTrackingSRBean> getTrackingDetailsList(MassUploadTrackingSRBean searchBean) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder("SELECT * FROM portal.servicerequest_trackingrecords where 1=1");
		
		if(StringUtils.isNotBlank(searchBean.getFromDate())  && StringUtils.isNotBlank(searchBean.getToDate())) {
			parameters.addValue("fromDate", searchBean.getFromDate());
			parameters.addValue("toDate", searchBean.getToDate());
			sql.append(" AND createdDate BETWEEN :fromDate AND :toDate ");
		}
		if(StringUtils.isNotBlank(searchBean.getFromDate())  && StringUtils.isBlank(searchBean.getToDate())) {
			parameters.addValue("fromDate", searchBean.getFromDate());
			sql.append(" AND createdDate>=:fromDate ");
		}
		if(StringUtils.isBlank(searchBean.getFromDate())  && StringUtils.isNotBlank(searchBean.getToDate())) {
			parameters.addValue("toDate", searchBean.getToDate());
			sql.append(" AND createdDate<=:toDate ");
		}
		if(searchBean.getServiceRequestId()!= null){
			parameters.addValue("srId", searchBean.getServiceRequestId());
			sql.append(" AND serviceRequestId=:srId ");
		}
		if(StringUtils.isNotBlank(searchBean.getCourierName())) {
			parameters.addValue("courierName", searchBean.getCourierName());
			sql.append(" AND courierName=:courierName ");
		}
		
		return namedParameterJdbcTemplate.query(sql.toString(), parameters, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
	}

	//delete sr tracking deatils by id
	@Override
	@Transactional(readOnly = false)
	public Integer deleteMassUploadTrackingBySrId(Integer srId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE from portal.servicerequest_trackingrecords where serviceRequestId=? ";
		
		return jdbcTemplate.update(sql, new Object[]{srId});
	}

	//save sr tracking deatils
	@Override
	@Transactional(readOnly = false)
	public Integer updateMassUploadSR(MassUploadTrackingSRBean massUploadTrackingSRBean) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "update portal.servicerequest_trackingrecords set trackId=?, courierName=?, url=?, mailStatus='N', lastModifiedDate=sysdate(), lastModifiedBy=? "
				   + " where serviceRequestId=? ";
		
	    return jdbcTemplate.update(sql, new Object[] { massUploadTrackingSRBean.getTrackId(), massUploadTrackingSRBean.getCourierName(),
		massUploadTrackingSRBean.getUrl(),massUploadTrackingSRBean.getLastModifiedBy(), massUploadTrackingSRBean.getServiceRequestId()});
	}
	
	//get sr tracking deatils by SRId
	@Override
	@Transactional(readOnly = true)
	public MassUploadTrackingSRBean getMassUploadTrackingBySRId(Integer srId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from portal.servicerequest_trackingrecords where serviceRequestId=?";
		
		MassUploadTrackingSRBean massUploadTrackingSRBean = (MassUploadTrackingSRBean) jdbcTemplate.queryForObject(sql, 
		new Object[]{srId}, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
		
		return massUploadTrackingSRBean;
	}

	//check srId and valid serviceRequestType present or not
	@Override
	@Transactional(readOnly = true)
	public boolean isSRValid(Integer srId,List<String> srTypeList) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "select count(id) "
				   + "from portal.service_request "
				   + "where id=:srId "
				   + "and serviceRequestType "
				   + "in (:srTypeList)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("srId", srId);
		parameters.addValue("srTypeList", srTypeList);
		
		return (namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class))!=0 ? true : false;
	}

	//getting srIdList for tracking link
	@Override
	@Transactional(readOnly = true)
	public List<Long> getSrIdList(List<Long> srIdList) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = " select distinct serviceRequestId from portal.servicerequest_trackingrecords "
				   + " where serviceRequestId in (:srIdList) ";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("srIdList", srIdList);
		
		return namedParameterJdbcTemplate.queryForList(sql, parameters, Long.class);
	}

	//getting the list of records for sending tracking email to the students 
	@Override
	@Transactional(readOnly = true)
	public List<MassUploadTrackingSRBean> getListSendEmailNotification() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT ps.serviceRequestType,ps.amount,CONCAT(pe.firstName,' ',pe.lastName) as studentName,pe.emailId, "
				   + "pr.serviceRequestId,pr.trackId,pr.courierName,pr.url "
				   + "FROM portal.service_request AS ps, "
				   + "portal.servicerequest_trackingrecords AS pr, "
				   + "exam.students AS pe "
				   + "WHERE ps.id=pr.serviceRequestId "
				   + "AND pe.sapid=ps.sapid "
				   + "AND pr.mailStatus='N' ";
		
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<MassUploadTrackingSRBean>(MassUploadTrackingSRBean.class));
	}

	//check whether srId is exist or not in servicerequest_trackingRecord table
	@Override
	@Transactional(readOnly = true)
	public boolean isSrIdExist(Long srId) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT count(serviceRequestId) "
				   + "FROM portal.servicerequest_trackingrecords "
			   	   + "WHERE serviceRequestId=? ";
		
		 return (jdbcTemplate.queryForObject(sql,new Object[] {srId}, Integer.class))>0?true:false;
	}

	//updating the status of mail in servicerequest_trackingRecords table
	@Override
	@Transactional(readOnly = false)
	public Integer updateTrackingMailStatus(String status,List<Integer> srIdList) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String commaSeparatedList = srIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
		
		String sql = "UPDATE portal.servicerequest_trackingrecords "
				   + "SET mailStatus=?,mailSendDate=sysdate() "
				   + "WHERE serviceRequestId "
				   + "IN ("+commaSeparatedList+")";
		
		return jdbcTemplate.update(sql,status);
	}

	//getting tracking status details by srid 
	@Override
	@Transactional(readOnly = true)
	public Map<Long, MassUploadTrackingSRBean> getTrackingMailStatus(List<Long> srIdList) {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		Map<Long, MassUploadTrackingSRBean> trackingMailStatus = new HashMap<Long, MassUploadTrackingSRBean>();
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql="SELECT serviceRequestId,mailStatus,createdBy,createdDate,lastModifiedBy,lastModifiedDate "
				 + "FROM portal.servicerequest_trackingrecords "
				 + "WHERE serviceRequestId "
				 + "IN (:srIdList)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("srIdList", srIdList);
		
		List<MassUploadTrackingSRBean> trackingBean = namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
		trackingBean.stream().forEach(list->trackingMailStatus.put((long) list.getServiceRequestId(), list));
			
		return trackingMailStatus;
	}

	//get map of srId and sapId from service_request table
	@Override
	@Transactional(readOnly = true)
	public Map<Integer, String> getMapOfSrIdAndSapId(List<String> srIdList) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<Integer, String> mapOfSrIdAndSapId = new HashMap<Integer, String>();
		
		
		String sql="SELECT id as serviceRequestId,sapId "
				 + "FROM portal.service_request "
				 + "WHERE id In (:srIdList) ";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("srIdList", srIdList);
		
		List<MassUploadTrackingSRBean> srDetailsList = namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
		
		for(MassUploadTrackingSRBean bean : srDetailsList) {
			mapOfSrIdAndSapId.put(bean.getServiceRequestId(), bean.getSapId());
		}
			
		return mapOfSrIdAndSapId;
	}

	//Get list of SR details
	@Override
	@Transactional(readOnly = true)
	public List<MassUploadTrackingSRBean> getSRDetailsList(List<Integer> srIdList) {
		
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "SELECT  "
				+ "    id as serviceRequestId, sapId, serviceRequestType "
				+ "FROM "
				+ "    portal.service_request "
				+ "WHERE "
				+ "    id In (:srIdList)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("srIdList", srIdList);
		
		return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
	}
	
	//Get list of Student details
	@Override
	@Transactional(readOnly = true)
	public List<MassUploadTrackingSRBean> getStudentDetailsList(List<String> sapIdList) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		String sql = "SELECT  "
				+ "    sapId, CONCAT(firstName, ' ',lastName) as studentName "
				+ "FROM "
				+ "    exam.students "
				+ "WHERE "
				+ "    sapId In (:sapIdList)";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("sapIdList", sapIdList);
		
		return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(MassUploadTrackingSRBean.class));
	}

}
