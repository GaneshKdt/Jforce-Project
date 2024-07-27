package com.nmims.daos;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TimetableBean;
import com.nmims.helpers.PaginationHelper;

@Repository("examCenterDAO")
public class ExamCenterDAO extends BaseDAO {
	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private JdbcTemplate jdbcTemplate;
	private final String SEAT_RELEASED = "Seat Released";
	private final String SEAT_RELEASED_NO_CHARGES = "Seat Released - No Charges";
	private final String SEAT_RELEASED_SUBJECT_CLEARED = "Seat Released - Subject Cleared";
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated";

	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	} 
	
	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertExamCenter(ExamCenterBean examCenter, boolean isCorporate) {

		PreparedStatementCreator psc = null;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final ExamCenterBean e = examCenter;
		jdbcTemplate = new JdbcTemplate(dataSource);

		if (isCorporate) {
			final String corporateSql = "INSERT INTO exam.corporate_examcenter(" + "examCenterName," + "locality,"
					+ "city," + "state," + "capacity," + "address," + "createdBy," + "lastModifiedBy," + "createdDate,"
					+ "lastModifiedDate," + "mode," + "year," + "ic," + "googleMapUrl," + "month)"
					+ "VALUES ( ?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?,?,?,?)";
			psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(corporateSql);
					ps.setString(1, e.getExamCenterName());
					ps.setString(2, e.getLocality());
					ps.setString(3, e.getCity());
					ps.setString(4, e.getState());
					ps.setString(5, e.getCapacity());
					ps.setString(6, e.getAddress());
					ps.setString(7, e.getCreatedBy());
					ps.setString(8, e.getLastModifiedBy());
					ps.setString(9, e.getMode());
					ps.setString(10, e.getYear());
					ps.setString(11, e.getIc());
					ps.setString(12, e.getGoogleMapUrl());
					ps.setString(13, e.getMonth());

					return ps;
				}
			};
		} else {
			final String sql = "INSERT INTO exam.examcenter(" + "examCenterName," + "locality," + "city," + "state,"
					+ "capacity," + "address," + "createdBy," + "lastModifiedBy," + "createdDate," + "lastModifiedDate,"
					+ "mode," + "year," + "month," + "googleMapUrl," + "ic)"
					+ "VALUES ( ?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?,?,?,?)";
			psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, e.getExamCenterName());
					ps.setString(2, e.getLocality());
					ps.setString(3, e.getCity());
					ps.setString(4, e.getState());
					ps.setString(5, e.getCapacity());
					ps.setString(6, e.getAddress());
					ps.setString(7, e.getCreatedBy());
					ps.setString(8, e.getLastModifiedBy());
					ps.setString(9, e.getMode());
					ps.setString(10, e.getYear());
					ps.setString(11, e.getMonth());
					ps.setString(12, e.getGoogleMapUrl());
					ps.setString(13, e.getIc());

					return ps;
				}
			};
		}

		jdbcTemplate.update(psc, keyHolder);

		int centerId = keyHolder.getKey().intValue();
		return centerId;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public ExamCenterBean findById(String centerId, boolean isCorporate) {
		String sql = "";
		if (isCorporate) {
			sql = "SELECT * FROM exam.corporate_examcenter " + "where centerId = ?";
		} else {
			sql = "SELECT * FROM exam.examcenter " + "where centerId = ?";
		}

		jdbcTemplate = new JdbcTemplate(dataSource);
		ExamCenterBean examCenter = (ExamCenterBean) jdbcTemplate.queryForObject(sql, new Object[] { centerId },
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return examCenter;
	}
	
	@Transactional(readOnly = false)
	public void updateExamCenter(ExamCenterBean examCenter) {
		String sql = "Update exam.examcenter set "

				+ "examCenterName=?," + "city=?," + "state=?," + "capacity=?," + "address=?," + "lastModifiedBy=?,"
				+ "locality=?,"
				// + "mode=?,"
				+ "year=?," + "month=?," + "googleMapUrl=?," + "lastModifiedDate=sysdate()"

				+ "  where centerId = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.update(sql, new Object[] { examCenter.getExamCenterName(), examCenter.getCity(),
				examCenter.getState(), examCenter.getCapacity(), examCenter.getAddress(),
				examCenter.getLastModifiedBy(), examCenter.getLocality(),
				// examCenter.getMode(),
				examCenter.getYear(), examCenter.getMonth(), examCenter.getGoogleMapUrl(), examCenter.getCenterId() });
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<ExamCenterBean> getExamCentersPage(int pageNo, int pageSize, ExamCenterBean examCenter,
			boolean isCorporate) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "", countSql = "";
		if (isCorporate) {
			sql = "SELECT * FROM exam.corporate_examcenter where 1 = 1 ";
			countSql = "SELECT count(*) FROM exam.corporate_examcenter where 1 = 1 ";
		} else {
			sql = "SELECT * FROM exam.examcenter where 1 = 1 ";
			countSql = "SELECT count(*) FROM exam.examcenter where 1 = 1 ";
		}

		if (examCenter.getExamCenterName() != null && !("".equals(examCenter.getExamCenterName()))) {
			sql = sql + " and examCenterName like  ? ";
			countSql = countSql + " and examCenterName like  ? ";
			parameters.add("%" + examCenter.getExamCenterName() + "%");
		}
		if (examCenter.getCity() != null && !("".equals(examCenter.getCity()))) {
			sql = sql + " and city = ? ";
			countSql = countSql + " and city = ? ";
			parameters.add(examCenter.getCity());
		}
		if (examCenter.getState() != null && !("".equals(examCenter.getState()))) {
			sql = sql + " and state = ? ";
			countSql = countSql + " and state = ? ";
			parameters.add(examCenter.getState());
		}
		if (examCenter.getMode() != null && !("".equals(examCenter.getMode()))) {
			sql = sql + " and mode = ? ";
			countSql = countSql + " and mode = ? ";
			parameters.add(examCenter.getMode());
		}
		if (examCenter.getYear() != null && !("".equals(examCenter.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(examCenter.getYear());
		}
		if (examCenter.getMonth() != null && !("".equals(examCenter.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(examCenter.getMonth());
		}

		sql = sql + " order by city ";
		Object[] args = parameters.toArray();

		PaginationHelper<ExamCenterBean> pagingHelper = new PaginationHelper<ExamCenterBean>();
		Page<ExamCenterBean> page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return page;
	}
	
	@Transactional(readOnly = false)
	public void deleteExamCenter(String centerId, boolean isCorporate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		if (isCorporate) {
			sql = "Delete from exam.corporate_examcenter where centerId = ?";
		} else {
			sql = "Delete from exam.examcenter where centerId = ?";
		}

		jdbcTemplate.update(sql, new Object[] { centerId });

	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAllExamCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examcenter  ";
		List<ExamCenterBean> examCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return examCentersList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAllOfflineExamCenters() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.examcenter a, exam.examorder b"
				+ " where a.year = b.year and  a.month = b.month and " + " a.mode = 'Offline' and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by a.city asc ";

		List<ExamCenterBean> examCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return examCentersList;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAllCorporateExamCenters(String usermappedId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.corporate_examcenter a, exam.examorder b"
				+ " where a.year = b.year and  a.month = b.month and a.centerId = ? and "
				+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by a.city asc ";

		List<ExamCenterBean> examCentersList = jdbcTemplate.query(sql, new Object[] { usermappedId },
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return examCentersList;
	}
	
	@Transactional(readOnly = true)
	public List<TimetableBean> getTimetableList(boolean isCorporateCenter,ExamCenterBean examCenter) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<TimetableBean> timeTableList = new ArrayList<TimetableBean>();
		if (isCorporateCenter) {
			sql = "SELECT distinct  date, starttime, examYear, examMonth FROM exam.corporate_timetable a, exam.examorder b"
					+ " where  a.examyear = b.year and  a.examMonth = b.month and"
					+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					+ " and a.mode = 'Online'"
					+ " and a.ic=? "
					+ " order by subject, date, startTime asc";
			timeTableList = jdbcTemplate.query(sql,new Object[] { examCenter.getIc()},
					new BeanPropertyRowMapper(TimetableBean.class));

		} else {
			sql = "SELECT distinct  date, starttime, examYear, examMonth FROM exam.timetable a, exam.examorder b"
					+ " where  a.examyear = b.year and  a.examMonth = b.month and"
					+ " b.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
					+ " and a.mode = 'Online' "
					+ " order by subject, date, startTime asc";
			timeTableList = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper(TimetableBean.class));

		}

		return timeTableList;
	}
	
	@Transactional(readOnly = false)
	public void createExamCenterSubjetCapacityRecords(int centerId, ExamCenterBean examCenter, boolean isCorporate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		List<TimetableBean> timeTableList = null;

		List<ExamCenterSlotMappingBean> examCenterSubectMappingList = new ArrayList<>();


		//timeTableList = getTimetableList(isCorporate);
		timeTableList = getTimetableList(isCorporate, examCenter);
		

		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);

			ExamCenterSlotMappingBean ecSubjectMappingBean = new ExamCenterSlotMappingBean();
			ecSubjectMappingBean.setExamcenterId(centerId + "");
			ecSubjectMappingBean.setYear(bean.getExamYear());
			ecSubjectMappingBean.setMonth(bean.getExamMonth());
			ecSubjectMappingBean.setDate(bean.getDate());
			ecSubjectMappingBean.setStarttime(bean.getStartTime());
			ecSubjectMappingBean.setCapacity(examCenter.getCapacity());

			examCenterSubectMappingList.add(ecSubjectMappingBean);
		}

		final List<ExamCenterSlotMappingBean> ecSubList = examCenterSubectMappingList;
		if (isCorporate) {

			sql = "Insert into exam.corporate_examcenter_slot_mapping ("
					+ " examcenterId, date, starttime, capacity, year, month)" + " values (?,?,?,?,?,?)";
		} else {
			sql = "Insert into exam.examcenter_slot_mapping ("
					+ " examcenterId, date, starttime, capacity, year, month)" + " values (?,?,?,?,?,?)";
		}

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExamCenterSlotMappingBean e = ecSubList.get(i);
				ps.setString(1, e.getExamcenterId());
				ps.setString(2, e.getDate());
				ps.setString(3, e.getStarttime());
				ps.setString(4, e.getCapacity());
				ps.setString(5, e.getYear());
				ps.setString(6, e.getMonth());
			}

			public int getBatchSize() {
				return ecSubList.size();
			}

		});
	}
	
	@Transactional(readOnly = false)
	public void batchInsertCenterUserMapping(final ArrayList<ExamCenterBean> centerUserMappingList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " INSERT INTO exam.corporate_center_usermapping (sapid,year,month,centerId) VALUES (?,?,?,?) ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExamCenterBean e = centerUserMappingList.get(i);
				ps.setString(1, e.getSapid());
				ps.setString(2, e.getYear());
				ps.setString(3, e.getMonth());
				ps.setString(4, e.getCenterId());
			}

			public int getBatchSize() {
				return centerUserMappingList.size();
			}

		});

	}


	/*CODE NOT IN USE
	 * public void updateExamCenterSubjetCapacityRecords(String centerId,
			String capacity) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		List<TimetableBean> timeTableList = getTimetableList(false);
		List<ExamCenterSlotMappingBean> examCenterSubectMappingList = new ArrayList<>();

		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableBean bean = timeTableList.get(i);

			ExamCenterSlotMappingBean ecSubjectMappingBean = new ExamCenterSlotMappingBean();
			ecSubjectMappingBean.setExamcenterId(centerId + "");
			ecSubjectMappingBean.setYear(bean.getExamYear());
			ecSubjectMappingBean.setMonth(bean.getExamMonth());
			ecSubjectMappingBean.setDate(bean.getDate());
			ecSubjectMappingBean.setStarttime(bean.getStartTime());
			ecSubjectMappingBean.setCapacity(capacity);

			examCenterSubectMappingList.add(ecSubjectMappingBean);
		}

		final List<ExamCenterSlotMappingBean> ecSubList = examCenterSubectMappingList;
		String sql = "Update exam.examcenter_slot_mapping " + " set capacity = ? " + " where examcenterId = ? "
				+ " and year = ?" + " and month = ? ";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ExamCenterSlotMappingBean e = ecSubList.get(i);
				ps.setString(1, e.getCapacity());
				ps.setString(2, e.getExamcenterId());
				ps.setString(3, e.getYear());
				ps.setString(4, e.getMonth());
			}

			public int getBatchSize() {
				return ecSubList.size();
			}

		});

	}*/

	
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getExamCenterSlots(ExamCenterBean ec) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		if ("All".equals(ec.getIc())) {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, COALESCE(a.onhold, 0) onHold,"
					+ " (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " a.year, a.month, " + " COALESCE(a.booked, 0) booked,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "

					+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b  "

					+ " WHERE a.examcenterid = b.centerId " + " and a.year = ? and a.month = ? "
					+ " and a.examcenterid = ?";
		} else {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, COALESCE(a.onhold, 0) onHold,"
					+ " (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " a.year, a.month, " + " COALESCE(a.booked, 0) booked,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "

					+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b  "

					+ " WHERE a.examcenterid = b.centerId " + " and a.year = ? and a.month = ? "
					+ " and a.examcenterid = ?";
		}


		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,
				new Object[] { ec.getYear(), ec.getMonth(), ec.getCenterId() },
				new BeanPropertyRowMapper(ExamCenterBean.class));

		return allCentersList;
	}
	
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAllExamCenterSlots() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, COALESCE(a.onhold, 0) onHold,"
				+ " (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ," + " a.year, a.month, "
				+ " COALESCE(a.booked, 0) booked," + " b.examcentername, b.locality, b.city, b.state, b.address "

				+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "

				+ " WHERE a.examcenterid = b.centerId " + " and a.year = c.year and  a.month = c.month"
				+ " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') ";


		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		return allCentersList;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getExamCenterIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			if(bean.getCity() != null && bean.getExamCenterName() != null && bean.getCity().equals(bean.getExamCenterName())) {
				examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName());
			} else {
				examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName()+","+bean.getLocality()+ ","+ bean.getCity());
			}
		}

		return examCenterIdNameMap;
	}

	/*
	 * fetchExamCenterIdMap returns the complete address that are queried from the
	 * Database
	 * 
	 */
	@Transactional(readOnly = true)
	public HashMap<String, String> fetchExamCenterIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName() + "," + bean.getLocality() + ","
					+ bean.getCity() + "," + bean.getAddress());
		}

		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getExamCenterIdNameMapForGivenMonthAndYear(String year, String month,
			boolean isCorporate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		if (isCorporate) {
			sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

					+ " FROM exam.corporate_examcenter ec   "

					+ " WHERE ec.year = ? and ec.month = ?";
		} else {

			sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

					+ " FROM exam.examcenter ec  "

					+ " WHERE ec.year = ? and ec.month = ?";
		}
		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] { year, month },
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(),
					bean.getExamCenterName() + "," + bean.getLocality() + "," + bean.getCity());
		}
		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, String> getCorporateExamCenterIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.corporate_examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(),
					bean.getExamCenterName() + "," + bean.getLocality() + "," + bean.getCity());

		}

		return examCenterIdNameMap;
	}

	/*
	 * fetchCorporateExamCenterIdNameMap fetches the complete address described in
	 * the query
	 */
	@Transactional(readOnly = true)
	public HashMap<String, String> fetchCorporateExamCenterIdNameMap() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address "

				+ " FROM exam.corporate_examcenter ec, exam.examorder eo   "

				+ " WHERE eo.year = ec.year and  eo.month = ec.month"
				+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));
		HashMap<String, String> examCenterIdNameMap = new HashMap<String, String>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean.getExamCenterName() + "," + bean.getLocality() + ","
					+ bean.getCity() + "," + bean.getAddress());

		}

		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public boolean hallTicketDownloadedStatus(String month, String sapid) {
		int count = 0;
		try {
			jdbcTemplate = new JdbcTemplate(dataSource);

			String sql = "select count(*) from exam.exambookings WHERE sapid=? and month=? and htDownloaded = 'Y'";
			
			
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{month, sapid},Integer.class);

			if(count> 0) {
				return true;
			}
			else {
				return false;
				}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ExamCenterBean> getExamCenterCenterDetailsMap(boolean isCorporate) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		if (isCorporate) {
			sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address, ec.mode "

					+ " FROM exam.corporate_examcenter ec, exam.examorder eo   "

					+ " WHERE eo.year = ec.year and  eo.month = ec.month"
					+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		} else {
			sql = "SELECT ec.centerId, ec.examcentername, ec.locality, ec.city, ec.state, ec.address, ec.mode "

					+ " FROM exam.examcenter ec, exam.examorder eo   "

					+ " WHERE eo.year = ec.year and  eo.month = ec.month"
					+ " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')";
		}

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));

		HashMap<String, ExamCenterBean> examCenterIdNameMap = new HashMap<String, ExamCenterBean>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean);
		}

		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, ExamCenterBean> getExamCenterDetailsMapForMbaWx() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		
		
			sql = "SELECT ec.centerId, ec.name as examcentername,  ec.city, ec.state, ec.address "

					+ " FROM  exam.mba_wx_centers ec";
		

		List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql, new Object[] {},
				new BeanPropertyRowMapper(ExamCenterBean.class));

		HashMap<String, ExamCenterBean> examCenterIdNameMap = new HashMap<String, ExamCenterBean>();
		for (int i = 0; i < allCentersList.size(); i++) {
			ExamCenterBean bean = allCentersList.get(i);
			examCenterIdNameMap.put(bean.getCenterId(), bean);
		}

		return examCenterIdNameMap;
	}
	
	@Transactional(readOnly = true)
	public HashMap<String, Integer> getConfirmedBookingForCurrentCycle() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String year = getLiveExamYear();
		String month = getLiveExamMonth();

		/*
		 * String sql =
		 * "SELECT concat(eb.centerId, eb.examDate, eb.examTime) as uniqueKey, count(*) as bookedCount FROM exam.exambookings eb, exam.examorder eo where "
		 * + " (eb.booked = 'Y' or  tranStatus = '" + ONLINE_PAYMENT_INITIATED + "' )" +
		 * " and eb.examMode = 'Online' " +
		 * " and eb.year = eo.year and  eb.month = eo.month" +
		 * " and eo.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') "
		 * + " group by eb.centerId, eb.examDate, eb.examTime ";
		 */

		String sql = "SELECT concat(eb.centerId, eb.examDate, eb.examTime) as uniqueKey, "
				+ " count(*) as bookedCount FROM exam.exambookings eb where " + " (eb.booked = 'Y' or  tranStatus = '"
				+ ONLINE_PAYMENT_INITIATED + "' )" + " and eb.examMode = 'Online' "
				+ " and eb.year = ? and  eb.month = ?" + " group by eb.centerId, eb.examDate, eb.examTime ";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
				.query(sql, new Object[] { year, month }, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

		HashMap<String, Integer> centerDateTimeBookingMap = new HashMap<>();
		for (ExamBookingTransactionBean bean : bookingList) {
			String key = bean.getUniqueKey();
			int booked = Integer.parseInt(bean.getBookedCount());
			centerDateTimeBookingMap.put(key, booked);


		}

		return centerDateTimeBookingMap;
	}

	
	@Transactional(readOnly = true)
	public Map<String, List<ExamCenterBean>> getAvailableCentersForGivenSubjects(ArrayList<String> subjects) {

		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		// Select all center-slot mappings for current session.

		/*
		 * sql =
		 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
		 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
		 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
		 * + " WHERE a.examcenterid = b.centerId " +
		 * " and a.year = c.year and  a.month = c.month" +
		 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
		 * ;
		 */
		String year = getLiveExamYear();
		String month = getLiveExamMonth();
		sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
				+ " b.examcentername, b.locality, b.city, b.state, b.address "
				+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b  " + " WHERE a.examcenterid = b.centerId "
				+ " and a.year = ? and  a.month = ?  order by b.city asc";
		

		allCentersList = jdbcTemplate.query(sql, new Object[] { year, month },
				new BeanPropertyRowMapper(ExamCenterBean.class));


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate() + examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}

			int capacity = 0;

			if (examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		for (String subject : subjects) {// Any of the available center can be
			// taken for any subject
			subjectAvailableCentersMap.put(subject, availableCentersList);
		}
		return subjectAvailableCentersMap;
	}
	
	@Transactional(readOnly = true)
	public Map<String, List<ExamCenterBean>> getAvailableCentersForGivenSubjects(ArrayList<String> subjects,String sapid) {

		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		// Select all center-slot mappings for current session.

		/*
		 * sql =
		 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
		 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
		 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
		 * + " WHERE a.examcenterid = b.centerId " +
		 * " and a.year = c.year and  a.month = c.month" +
		 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
		 * ;
		 */
		sql="SELECT " + 
				"    CASE" + 
				"        WHEN" + 
				"            enrollmentMonth = 'Jan'" + 
				"                AND enrollmentYear = '2023'" + 
				"                AND (sem = 1 OR isLateral = 'Y')" + 
				"        THEN" + 
				"            '2'" + 
				"        ELSE '1'" + 
				"    END AS phase" + 
				"    FROM" + 
				"    exam.students where sapid = ? ";
		String phase=jdbcTemplate.queryForObject(sql, new Object[] {sapid},new SingleColumnRowMapper<>(String.class));
		
		String year = getLiveExamYear();
		String month = getLiveExamMonth();
		sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
				+ " b.examcentername, b.locality, b.city, b.state, b.address "
				+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examcenter_phase_mapping c  " 
				+ " WHERE a.examcenterid = b.centerId and a.examcenterId=c.centerId"
				+ " and a.year = ? and  a.month = ? and c.phaseId = ? order by b.city asc";
		

		allCentersList = jdbcTemplate.query(sql, new Object[] { year, month, phase },
				new BeanPropertyRowMapper(ExamCenterBean.class));


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate() + examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}

			int capacity = 0;

			if (examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		for (String subject : subjects) {// Any of the available center can be
			// taken for any subject
			subjectAvailableCentersMap.put(subject, availableCentersList);
		}
		return subjectAvailableCentersMap;
	}
	
	@Transactional(readOnly = true)
	public Map<String, List<ExamCenterBean>> getAvailableCentersForGivenSubjectsCorporateDiageo(
			ArrayList<String> subjects, String sapId) {


		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		// Select all center-slot mappings for current session.
		sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
				+ " b.examcentername, b.locality, b.city, b.state, b.address "
				+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b, exam.examorder c,  exam.corporate_center_usermapping ccu  "
				+ " WHERE a.examcenterid = b.centerId and "
				+" ccu.centerId =  b.centerId "
				+ " and a.year = c.year and  a.month = c.month "
				+ " and ccu.sapid = ? "
				+ " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc";
		allCentersList = jdbcTemplate.query(sql, new Object[] { sapId },
				new BeanPropertyRowMapper(ExamCenterBean.class));

		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate()
					+ examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			} 

			int capacity = 0;

			if (examCenter.getCapacity() != null
					&& !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		for (String subject : subjects) {// Any of the available center can be
			// taken for any subject
			subjectAvailableCentersMap.put(subject, availableCentersList);
		}
		return subjectAvailableCentersMap;
	}
	
	@Transactional(readOnly = true)
	public Map<String, List<ExamCenterBean>> getAvailableCentersForGivenSubjectsCorporate(
			ArrayList<String> subjects, String centerId) {


		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
		String sql = "";
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		// Select all center-slot mappings for current session.
		sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
				+ " b.examcentername, b.locality, b.city, b.state, b.address, b.googleMapUrl "
				+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b, exam.examorder c   "
				+ " WHERE a.examcenterid = b.centerId " + " and a.year = c.year and  a.month = c.month "
				+ " and b.centerId = ? "
				+ " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc";
		
		allCentersList = jdbcTemplate.query(sql, new Object[] { centerId },
				new BeanPropertyRowMapper(ExamCenterBean.class));


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);

			String key = examCenter.getCenterId() + examCenter.getDate() + examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}

			int capacity = 0;

			if (examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;

			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		for (String subject : subjects) {// Any of the available center can be
			// taken for any subject
			subjectAvailableCentersMap.put(subject, availableCentersList);
		}
		return subjectAvailableCentersMap;
	}
	
//	@Transactional(readOnly = true)
//	public Map<String, List<ExamCenterBean>> getAvailableCentersForGivenSubjects(ArrayList<String> subjects
//			, ArrayList<String> blockedCentersId) {
//
//		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = new HashMap<String, List<ExamCenterBean>>();
//		String sql = "";
//		jdbcTemplate = new JdbcTemplate(dataSource);
//		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
//		// Select all center-slot mappings for current session.
//
//		/*
//		 * sql =
//		 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
//		 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
//		 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
//		 * + " WHERE a.examcenterid = b.centerId " +
//		 * " and a.year = c.year and  a.month = c.month" +
//		 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
//		 * ;
//		 */
//		String year = getLiveExamYear();
//		String month = getLiveExamMonth();
//		sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
//				+ " b.examcentername, b.locality, b.city, b.state, b.address "
//				+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b  " + " WHERE a.examcenterid = b.centerId "
//				+ " and a.year = ? and  a.month = ?  order by b.city asc";
//		
//
//		allCentersList = jdbcTemplate.query(sql, new Object[] { year, month },
//				new BeanPropertyRowMapper(ExamCenterBean.class));
//
//
//		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();
//
//		List<ExamCenterBean> availableCentersList = new ArrayList<>();
//		for (int j = 0; j < allCentersList.size(); j++) {
//			ExamCenterBean examCenter = allCentersList.get(j);
//
//			if(blockedCentersId.contains(examCenter.getCenterId()))
//			{
//				continue;
//			}
//			
//			String key = examCenter.getCenterId() + examCenter.getDate() + examCenter.getStarttime();
//
//			int booked = 0;
//
//			if (centerDateTimeBookingMap.containsKey(key)) {
//				booked = centerDateTimeBookingMap.get(key);
//			}
//
//			int capacity = 0;
//
//			if (examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())) {
//				capacity = Integer.parseInt(examCenter.getCapacity());
//			}
//
//			int available = capacity - booked;
//
//			examCenter.setAvailable(available);
//
//			if (available > 0) {
//
//				availableCentersList.add(examCenter);
//			}
//		}
//
//		for (String subject : subjects) {// Any of the available center can be
//			// taken for any subject
//			subjectAvailableCentersMap.put(subject, availableCentersList);
//		}
//		return subjectAvailableCentersMap;
//	}
	
	
	

	/*
	 * public List<ExamCenterBean> getAvailableCentersForResitExam(String sapId) {
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource);
	 * 
	 * //Select all center-slot mappings for current session. String sql =
	 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
	 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
	 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
	 * + " WHERE a.examcenterid = b.centerId " +
	 * " and a.year = c.year and  a.month = c.month " +
	 * " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')"
	 * +
	 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
	 * ;
	 * 
	 * 
	 * List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,new
	 * Object[]{sapId}, new BeanPropertyRowMapper(ExamCenterBean.class));
	 * 
	 * 
	 * HashMap<String, Integer> centerDateTimeBookingMap =
	 * getConfirmedBookingForCurrentCycle();
	 * 
	 * List<ExamCenterBean> availableCentersList = new ArrayList<>(); for (int j =
	 * 0; j < allCentersList.size(); j++) { ExamCenterBean examCenter =
	 * allCentersList.get(j); String key = examCenter.getCenterId() +
	 * examCenter.getDate() + examCenter.getStarttime();
	 * booked = 0;
	 * 
	 * if(centerDateTimeBookingMap.containsKey(key)){ booked =
	 * centerDateTimeBookingMap.get(key); } int capacity = 0;
	 * 
	 * if(examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())){
	 * capacity = Integer.parseInt(examCenter.getCapacity()); }
	 * 
	 * int available = capacity - booked; examCenter.setAvailable(available);
	 * " available = "+ available);
	 * 
	 * if( available > 0){ availableCentersList.add(examCenter); } }
	 * 
	 * return availableCentersList; }
	 */
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAvailableCentersForRegularOnlineExamCorporateDiageo(
			String sapId, boolean isCorporateExamCenterStudent, String centerId) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> corporateCenterUserMapping = new HashMap<String, String>();
		String sql = "";

		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		/*
		 * boolean isCorporate = false; if(request!=null){
		 * corporateCenterUserMapping =
		 * (HashMap<String,String>)request.getSession
		 * ().getAttribute("corporateCenterUserMapping");
		 * if(corporateCenterUserMapping!=null &&
		 * !corporateCenterUserMapping.isEmpty()){
		 * if(corporateCenterUserMapping.containsKey(sapId)){ isCorporate =
		 * true; centerId = corporateCenterUserMapping.get(sapId); } } }
		 */

		String year = getLiveExamYear();
		String month = getLiveExamMonth();
		/*if (isCorporateExamCenterStudent) {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "
					+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b, exam.examorder c   "
					+ " WHERE a.examcenterid = b.centerId "
					+ " and b.centerId = ? "
					+ " and a.year = c.year and  a.month = c.month "
					+ " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')"
					+ " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc";
			allCentersList = jdbcTemplate.query(sql, new Object[] { centerId,
					sapId }, new BeanPropertyRowMapper(ExamCenterBean.class));

		} else {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "
					+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
					+ " WHERE a.examcenterid = b.centerId "
					+ " and a.year = c.year and  a.month = c.month "
					+ " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')"
					+ " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc";
			allCentersList = jdbcTemplate.query(sql, new Object[] { sapId },
					new BeanPropertyRowMapper(ExamCenterBean.class));
		}*/	
		
		if (isCorporateExamCenterStudent) {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "
					+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b, exam.corporate_center_usermapping ccu  "
					+ " WHERE a.examcenterid = b.centerId and "
					+" ccu.centerId =  b.centerId "
					+ " and a.year = ? and  a.month = ? " 
					+ " and ccu.sapid = ? "
					+ " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')  order by  a.date, a.starttime asc";		
			allCentersList = jdbcTemplate.query(sql, new Object[] { 
					year, month, sapId,  sapId}, new BeanPropertyRowMapper(ExamCenterBean.class));

		}


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);
			String key = examCenter.getCenterId() + examCenter.getDate()
					+ examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}
			int capacity = 0;

			if (examCenter.getCapacity() != null
					&& !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;
			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		return availableCentersList;
	}
	
	@Transactional(readOnly = true)
	public List<ExamCenterBean> getAvailableCentersForRegularOnlineExam(
			String sapId, boolean isCorporateExamCenterStudent, String centerId) {


		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, String> corporateCenterUserMapping = new HashMap<String, String>();
		String sql = "";

		List<ExamCenterBean> allCentersList = new ArrayList<ExamCenterBean>();
		/*
		 * boolean isCorporate = false; if(request!=null){ corporateCenterUserMapping =
		 * (HashMap<String,String>)request.getSession
		 * ().getAttribute("corporateCenterUserMapping");
		 * if(corporateCenterUserMapping!=null &&
		 * !corporateCenterUserMapping.isEmpty()){
		 * if(corporateCenterUserMapping.containsKey(sapId)){ isCorporate = true;
		 * centerId = corporateCenterUserMapping.get(sapId); } } }
		 */
		
		sql="SELECT " + 
				"    CASE" + 
				"        WHEN" + 
				"            enrollmentMonth = 'Jan'" + 
				"                AND enrollmentYear = '2023'" + 
				"                AND (sem = 1 OR isLateral = 'Y')" + 
				"        THEN" + 
				"            '2'" + 
				"        ELSE '1'" + 
				"    END AS phase" + 
				"    FROM" + 
				"    exam.students where sapid = ? ";
		String phase=jdbcTemplate.queryForObject(sql, new Object[] {sapId},new SingleColumnRowMapper<>(String.class));
		
		String year = getLiveExamYear();
		String month = getLiveExamMonth();
		/*
		 * if (isCorporateExamCenterStudent) { sql =
		 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
		 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
		 * " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b, exam.examorder c   "
		 * + " WHERE a.examcenterid = b.centerId " + " and b.centerId = ? " +
		 * " and a.year = c.year and  a.month = c.month " +
		 * " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')"
		 * +
		 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
		 * ; allCentersList = jdbcTemplate.query(sql, new Object[] { centerId, sapId },
		 * new BeanPropertyRowMapper(ExamCenterBean.class));
		 * 
		 * } else { sql =
		 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
		 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
		 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
		 * + " WHERE a.examcenterid = b.centerId " +
		 * " and a.year = c.year and  a.month = c.month " +
		 * " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')"
		 * +
		 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y') order by b.city asc"
		 * ; allCentersList = jdbcTemplate.query(sql, new Object[] { sapId }, new
		 * BeanPropertyRowMapper(ExamCenterBean.class)); }
		 */

		if (isCorporateExamCenterStudent) {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "
					+ " FROM exam.corporate_examcenter_slot_mapping a, exam.corporate_examcenter b   "
					+ " WHERE a.examcenterid = b.centerId " + " and b.centerId = ? "
					+ " and a.year = ? and  a.month = ? "
					+ " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y')  order by b.city asc";

			allCentersList = jdbcTemplate.query(sql, new Object[] { centerId, year, month, sapId },
					new BeanPropertyRowMapper(ExamCenterBean.class));

		} else {
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " b.examcentername, b.locality, b.city, b.state, b.address "
					+ " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examcenter_phase_mapping c "
					+ " WHERE a.examcenterid = b.centerId and a.examcenterId=c.centerId "
					+ " and a.year = ? and  a.month = ?  and c.phaseId = ? "
					+ " and concat(b.centerid,a.date,a.starttime) not in (select concat(centerId, examdate, examtime) from exam.exambookings where sapid = ? and booked = 'Y') order by b.city asc";

			allCentersList = jdbcTemplate.query(sql, new Object[] { year, month,phase, sapId },
					new BeanPropertyRowMapper(ExamCenterBean.class));
		}


		HashMap<String, Integer> centerDateTimeBookingMap = getConfirmedBookingForCurrentCycle();

		List<ExamCenterBean> availableCentersList = new ArrayList<>();
		for (int j = 0; j < allCentersList.size(); j++) {
			ExamCenterBean examCenter = allCentersList.get(j);
			String key = examCenter.getCenterId() + examCenter.getDate() + examCenter.getStarttime();

			int booked = 0;

			if (centerDateTimeBookingMap.containsKey(key)) {
				booked = centerDateTimeBookingMap.get(key);
			}
			int capacity = 0;

			if (examCenter.getCapacity() != null && !"".equals(examCenter.getCapacity())) {
				capacity = Integer.parseInt(examCenter.getCapacity());
			}

			int available = capacity - booked;
			examCenter.setAvailable(available);

			if (available > 0) {

				availableCentersList.add(examCenter);
			}
		}

		return availableCentersList;
	}

	/*
	 * public Map<String, ArrayList<String>>
	 * getAvailableCenterIDSForGivenSubjects(List<TimetableBean> timeTableList) {
	 * Map<String, ArrayList<String>> subjectAvailableCentersMap = new
	 * HashMap<String, ArrayList<String>>();
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource);
	 * 
	 * //Select all center-slot mappings for current session. String sql =
	 * "SELECT b.centerId, a.date, a.starttime, a.capacity, (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
	 * + " b.examcentername, b.locality, b.city, b.state, b.address " +
	 * " FROM exam.examcenter_slot_mapping a, exam.examcenter b, exam.examorder c   "
	 * + " WHERE a.examcenterid = b.centerId " +
	 * " and a.year = c.year and  a.month = c.month" +
	 * " and c.order = (Select max(examorder.order) from exam.examorder where timeTableLive='Y')"
	 * ;
	 * 
	 * 
	 * List<ExamCenterBean> allCentersList = jdbcTemplate.query(sql,new Object[]{},
	 * new BeanPropertyRowMapper(ExamCenterBean.class));
	 * 
	 * 
	 * 
	 * for (int i = 0; i < timeTableList.size(); i++) { TimetableBean timetableBean
	 * = timeTableList.get(i); ArrayList<String> availableCentersList = new
	 * ArrayList<>();
	 * 
	 * for (int j = 0; j < allCentersList.size(); j++) { ExamCenterBean examCenter =
	 * allCentersList.get(j); int available = examCenter.getAvailable();
	 * 
	 * if(examCenter.getDate().equals(timetableBean.getDate()) &&
	 * examCenter.getStarttime().equals(timetableBean.getStartTime()) && available >
	 * 0){
	 * 
	 * availableCentersList.add(examCenter.getCenterId()); } }
	 * subjectAvailableCentersMap.put(timetableBean.getSubject() +
	 * timetableBean.getStartTime(), availableCentersList);
	 * 
	 * } return subjectAvailableCentersMap; }
	 */
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedOrReleasedBooking(ExamBookingTransactionBean booking) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
				+ " and a.sapid = ? and (a.booked = 'Y' or a.booked = 'RL' or (a.booked='N' and a.tranStatus = 'Cancellation With Refund') or (a.booked='CL' and a.tranStatus = 'Cancellation Without Refund')) " + " order by a.subject, a.sem  asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	public ArrayList<ExamBookingTransactionBean> getConfirmedNotReleaseBooking(ExamBookingTransactionBean booking) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
				+ " and a.sapid = ? and (a.booked = 'Y' ) " + " order by a.subject, a.sem  asc";

		ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate.query(
				sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
				new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		return bookingList;
	}
	
	@Transactional(readOnly = true)
	//To check for given trackId and subject records are present in the table.
	public boolean checkReleasingSubjectPresent(String sapid,String subject, String trackId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		//Prepare SQL query
		String SQL = "SELECT count(*) as 'count' FROM exam.exambookings where sapid = ? AND subject=? and trackId=? ";

			//Execute jdbcTemplate method.
			
		Integer count = jdbcTemplate.query(SQL, new Object[] {sapid, subject, trackId}, (rs) -> rs.next() ? rs.getInt("count") : 0);
		
		return count.intValue() > 0;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<ExamBookingTransactionBean> releaseBookings(String sapid,
			final List<ExamBookingTransactionBean> bookingList, ExamBookingTransactionBean booking, String noCharges,
			boolean returnModifiedBookings, boolean isCorporateStudent) {

			String status = "";
			if ("Passed".equalsIgnoreCase(noCharges)) {
				status = SEAT_RELEASED_SUBJECT_CLEARED;
			} else if ("true".equalsIgnoreCase(noCharges)) {
				status = SEAT_RELEASED_NO_CHARGES;
			} else {
				status = SEAT_RELEASED;
			}

			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "Update exam.exambookings set booked = 'RL' , tranStatus = '" + status + "' , releaseReason = ?, lastModifiedDate=sysdate() , lastModifiedBy = ? , emailId = null "
						+ " where sapid = ? and subject = ? and booked = 'Y' and trackId = ? ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getReleaseReason());
					ps.setString(2, booking.getLastModifiedBy());
					ps.setString(3, bean.getSapid());
					ps.setString(4, bean.getSubject());
					ps.setString(5, bean.getTrackId());

				}

				public int getBatchSize() {
					return bookingList.size();
				}
			});

			// Release hold seats and marks them as booked
			/*
			 * sql = "Update exam.examcenter_slot_mapping" +
			 * " set booked = COALESCE(booked, 0) - 1" + " where examcenterid = ? " +
			 * " and date = ? " + " and starttime = ?";
			 */

			sql = getReduceBookedCountSQL(isCorporateStudent);

			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}

				public int getBatchSize() {
					return bookingList.size();
				}
			});


			if (!returnModifiedBookings) {
				return null;
			} else {

				sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
						+ " and a.sapid = ? and ( a.booked = 'Y'  or a.booked = 'RL' or (a.booked='N' and a.tranStatus = 'Cancellation With Refund') or (a.booked='CL' and a.tranStatus = 'Cancellation Without Refund')) "
						+ " order by a.examDate, a.subject asc";


				ArrayList<ExamBookingTransactionBean> completeBookings = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
						.query(sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
								new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

				return completeBookings;
			}

		} catch (Exception e) {
			try {
				String sql = "Update exam.exambookings set booked = 'RL' , tranStatus = '" + status + "' , releaseReason = ?, lastModifiedDate=sysdate() , lastModifiedBy = ? , emailId = null "
						+ " where sapid = ? and subject = ? and booked = 'Y' and trackId = ? ";


				int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ExamBookingTransactionBean bean = bookingList.get(i);
						ps.setString(1, bean.getReleaseReason());
						ps.setString(2, booking.getLastModifiedBy());
						ps.setString(3, bean.getSapid());
						ps.setString(4, bean.getSubject());
						ps.setString(5, bean.getTrackId());

					}

					public int getBatchSize() {
						return bookingList.size();
					}
				});

				// Release hold seats and marks them as booked
				/*
				 * sql = "Update exam.examcenter_slot_mapping" +
				 * " set booked = COALESCE(booked, 0) - 1" + " where examcenterid = ? " +
				 * " and date = ? " + " and starttime = ?";
				 */

				sql = getReduceBookedCountSQL(isCorporateStudent);

				slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ExamBookingTransactionBean bean = bookingList.get(i);
						ps.setString(1, bean.getCenterId());
						ps.setString(2, bean.getExamDate());
						ps.setString(3, bean.getExamTime());

					}

					public int getBatchSize() {
						return bookingList.size();
					}
				});


				if (!returnModifiedBookings) {
					return null;
				} else {

					sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
							+ " and a.sapid = ? and ( a.booked = 'Y'  or a.booked = 'RL' or (a.booked='N' and a.tranStatus = 'Cancellation With Refund') or (a.booked='CL' and a.tranStatus = 'Cancellation Without Refund')) "
							+ " order by a.examDate, a.subject asc";


					ArrayList<ExamBookingTransactionBean> completeBookings = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
							.query(sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
									new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

					return completeBookings;
				}

			} catch (Exception e2) {
				throw e2;
			}
		}

	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<ExamBookingTransactionBean> cancelBookings(String sapid,
			final List<ExamBookingTransactionBean> bookingList, ExamBookingTransactionBean booking, String refund,
			boolean returnModifiedBookings, boolean isCorporateStudent,final String booked,final String status,final String concatenate) {

			

			jdbcTemplate = new JdbcTemplate(dataSource);
			try {
				String sql = "Update exam.exambookings set booked = '" + booked + "' , tranStatus = '" + status + "' , releaseReason = ? , lastModifiedDate=sysdate() , lastModifiedBy = ? , description = ? "
						+ " where sapid = ? and subject = ? and booked = 'Y' and trackId = ? ";


			int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getReleaseReason());
					ps.setString(2, booking.getLastModifiedBy());
					ps.setString(3, bean.getDescription()+concatenate);
					ps.setString(4, bean.getSapid());
					ps.setString(5, bean.getSubject());
					ps.setString(6, bean.getTrackId());
					
				}

				public int getBatchSize() {
					return bookingList.size();
				}
			});

			sql = getReduceBookedCountSQL(isCorporateStudent);

			slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ExamBookingTransactionBean bean = bookingList.get(i);
					ps.setString(1, bean.getCenterId());
					ps.setString(2, bean.getExamDate());
					ps.setString(3, bean.getExamTime());

				}

				public int getBatchSize() {
					return bookingList.size();
				}
			});


			if (!returnModifiedBookings) {
				return null;
			} else {

				sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
						+ " and a.sapid = ? and ( a.booked = 'Y'  or a.booked = 'RL' or (a.booked='N' and a.tranStatus = 'Cancellation With Refund') or (a.booked='CL' and a.tranStatus = 'Cancellation Without Refund'))  "
						+ " order by a.examDate, a.subject asc";


				ArrayList<ExamBookingTransactionBean> completeBookings = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
						.query(sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
								new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

				return completeBookings;
			}

		} catch (Exception e) {
			try {
				String sql = "Update exam.exambookings set booked = '" + booked + "' , tranStatus = '" + status + "' , releaseReason = ?, lastModifiedDate=sysdate() , lastModifiedBy = ? , description = ? "
						+ " where sapid = ? and subject = ? and booked = 'Y' and trackId = ? ";


				int[] slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ExamBookingTransactionBean bean = bookingList.get(i);
						ps.setString(1, bean.getReleaseReason());
						ps.setString(2, booking.getLastModifiedBy());
						ps.setString(3, bean.getDescription()+concatenate);
						ps.setString(4, bean.getSapid());
						ps.setString(5, bean.getSubject());
						ps.setString(6, bean.getTrackId());
						
						
					}

					public int getBatchSize() {
						return bookingList.size();
					}
				});


				sql = getReduceBookedCountSQL(isCorporateStudent);

				slotBookingDBUpdateResults = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ExamBookingTransactionBean bean = bookingList.get(i);
						ps.setString(1, bean.getCenterId());
						ps.setString(2, bean.getExamDate());
						ps.setString(3, bean.getExamTime());

					}

					public int getBatchSize() {
						return bookingList.size();
					}
				});


				if (!returnModifiedBookings) {
					return null;
				} else {

					sql = "SELECT * FROM exam.exambookings a, exam.students s where a.sapid = s.sapid and  a.year = ? and a.month = ? "
							+ " and a.sapid = ? and ( a.booked = 'Y'  or a.booked = 'RL' or (a.booked='N' and a.tranStatus = 'Cancellation With Refund') or (a.booked='CL' and a.tranStatus = 'Cancellation Without Refund'))  "
							+ " order by a.examDate, a.subject asc";


					ArrayList<ExamBookingTransactionBean> completeBookings = (ArrayList<ExamBookingTransactionBean>) jdbcTemplate
							.query(sql, new Object[] { booking.getYear(), booking.getMonth(), booking.getSapid() },
									new BeanPropertyRowMapper(ExamBookingTransactionBean.class));

					return completeBookings;
				}

			} catch (Exception e2) {
				throw e2;
			}
		}

	}
	
	@Transactional(readOnly = false)
	private String getReduceBookedCountSQL(boolean isCorporateStudent) {
		String sql = "";
		if (isCorporateStudent) {
			sql = "Update exam.corporate_examcenter_slot_mapping" + " set booked = COALESCE(booked, 0) - 1"
					+ " where examcenterid = ? " + " and date = ? " + " and starttime = ?";
		} else {
			sql = "Update exam.examcenter_slot_mapping" + " set booked = COALESCE(booked, 0) - 1"
					+ " where examcenterid = ? " + " and date = ? " + " and starttime = ?";
		}
		return sql;

	}
	
	@Transactional(readOnly = false)
	public void updateCapacity(List<ExamCenterBean> centerSlotList, String ic) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		if ("All".equals(ic)) {
			sql = "update exam.examcenter_slot_mapping set capacity = ? "
					+ " where examcenterId = ? and date = ? and starttime = ? and year = ? and month = ? ";

		} else {
			sql = "update exam.corporate_examcenter_slot_mapping set capacity = ? "
					+ " where examcenterId = ? and date = ? and starttime = ? and year = ? and month = ? ";

		}

		for (ExamCenterBean examCenterBean : centerSlotList) {
			jdbcTemplate.update(sql,
					new Object[] { examCenterBean.getCapacity(), examCenterBean.getCenterId(), examCenterBean.getDate(),
							examCenterBean.getStarttime(), examCenterBean.getYear(), examCenterBean.getMonth() });
		}

	}

	@SuppressWarnings("null")
	@Transactional(readOnly = true)
	public HashMap<String, String> getCorporateCenterUserMapping() {
		HashMap<String, String> corporateCenterUserMapping = new HashMap<String, String>();
		String sql = " SELECT * FROM exam.corporate_center_usermapping ecu,exam.examorder ee where ecu.year = ee.year and ecu.month = ee.month "
				+ " and ee.order = (SELECT max(examorder.order) FROM exam.examorder where timeTableLive = 'Y') ";
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<ExamCenterBean> listOfCorporateUserMappings = (ArrayList<ExamCenterBean>) jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ExamCenterBean.class));
		for (ExamCenterBean exam : listOfCorporateUserMappings) {

			corporateCenterUserMapping.put(exam.getSapid(), exam.getCenterId());
		}
		return corporateCenterUserMapping;
	}
	
	@Transactional(readOnly = true)
	public List<String> getBookedSubjectList(String sapid,String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT subject FROM exam.exambookings where sapid=? and year=? and month=? and booked='Y'";
		return jdbcTemplate.query(sql, new Object[] {sapid,year,month},new SingleColumnRowMapper<String>(String.class));
	} 

	/*
	 * 
	 * 
	 * @SuppressWarnings("rawtypes") public List<Job> getAllJobs(){
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource); String sql =
	 * "SELECT jobs.jobId," + "    jobs.designation," + "    jobs.jobDescription," +
	 * "    jobs.desiredProfile," + "    jobs.experience," + "    jobs.location," +
	 * "    jobs.keywords," + "    jobs.contactmailId," + "    jobs.contactPhone," +
	 * "    jobs.jobPostDate," + "    jobs.companyId," +
	 * "		company.companyName, " + "company.aboutCompany, " +
	 * "company.industryType, " + "company.websiteUrl " +
	 * "FROM placement.jobs, placement.company " +
	 * "where jobs.companyId = company.companyid";
	 * 
	 * List<Job> jobs = jdbcTemplate.query(sql, new
	 * BeanPropertyRowMapper(Job.class));
	 * 
	 * return jobs; }
	 * 
	 * 
	 * @SuppressWarnings("rawtypes") public Page<Job> getJobsPage(int pageNo, int
	 * pageSize){
	 * 
	 * jdbcTemplate = new JdbcTemplate(dataSource); String sql =
	 * "SELECT jobs.jobId," + "    jobs.designation," + "    jobs.jobDescription," +
	 * "    jobs.desiredProfile," + "    jobs.experience," + "    jobs.location," +
	 * "    jobs.keywords," + "    jobs.contactmailId," + "    jobs.contactPhone," +
	 * "    jobs.jobPostDate," + "    jobs.companyId," +
	 * "		company.companyName, " + "company.aboutCompany, " +
	 * "company.industryType, " + "company.websiteUrl " +
	 * "FROM placement.jobs, placement.company " +
	 * "where jobs.companyId = company.companyid";
	 * 
	 * 
	 * String countSql = "SELECT count(*) " +
	 * "FROM placement.jobs, placement.company " +
	 * "where jobs.companyId = company.companyid";
	 * 
	 * 
	 * PaginationHelper<Job> pagingHelper = new PaginationHelper<Job>(); Page<Job>
	 * page = pagingHelper.fetchPage(jdbcTemplate, countSql, sql, new Object[]{},
	 * pageNo, pageSize, new BeanPropertyRowMapper(Job.class));
	 * 
	 * return page;
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public List<Job> getCompanyJobs(String companyId) { jdbcTemplate = new
	 * JdbcTemplate(dataSource); String sql = "SELECT jobs.jobId," +
	 * "    jobs.designation," + "    jobs.jobDescription," +
	 * "    jobs.desiredProfile," + "    jobs.experience," + "    jobs.location," +
	 * "    jobs.keywords," + "    jobs.contactmailId," + "    jobs.contactPhone," +
	 * "    jobs.jobPostDate," + "    jobs.companyId," +
	 * "		company.companyName, " + "company.aboutCompany, " +
	 * "company.industryType, " + "company.websiteUrl " +
	 * "FROM placement.jobs, placement.company " +
	 * "where jobs.companyId = company.companyid and company.companyId = ? order by jobs.jobPostDate desc"
	 * ;
	 * 
	 * List<Job> jobs = jdbcTemplate.query(sql, new Object[]{companyId}, new
	 * BeanPropertyRowMapper(Job.class));
	 * 
	 * 
	 * return jobs; }
	 * 
	 * 
	 * public String applyJob(Job job, StudentBean student) { // TODO Auto-generated
	 * method stub return null; }
	 */
	
	@Transactional(readOnly = true)
	public List<String> getBlockedCentersIdBySapid(String sapid,String year,String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select centerId FROM exam.student_blocked_centers where sapid=? and year=? and month=?";
		return jdbcTemplate.query(sql, new Object[] {sapid,year,month},new SingleColumnRowMapper<String>(String.class));
	} 
	
	@Transactional(readOnly = false)
	public boolean archiveReleasedExamBooking(final String sapid, final String subject,final String year,final String month,final String trackId,final String examStartDateTime)
	{
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="INSERT INTO exam.exams_pg_scheduleinfo_history_mettl "
					+" (subject, year, month, trackId, sapid, firstname, lastname, emailId, examStartDateTime, "  
					+" examEndDateTime, accessStartDateTime, accessEndDateTime, reporting_start_date_time, reporting_finish_date_time, sifySubjectCode, "  
					+" scheduleName, acessKey, joinURL, createdBy, createdDateTime, examCenterName, testTaken) "
					+" SELECT subject, year, month, trackId, sapid, firstname, lastname, emailId, examStartDateTime, "
					+" examEndDateTime, accessStartDateTime, accessEndDateTime, reporting_start_date_time, reporting_finish_date_time, sifySubjectCode, "
					+" scheduleName, acessKey, joinURL, createdBy, createdDateTime, examCenterName, 'Archived' testTaken FROM exam.exams_pg_scheduleinfo_mettl where sapid= ? and subject= ? " 
					+" and year= ? and month= ? and trackId= ? and examStartDateTime= ? ";
			int rowsInserted=jdbcTemplate.update(sql, new Object[] {sapid,subject,year,month,trackId,examStartDateTime});
			if(rowsInserted>0)
			{
				transactionManager.commit(status);
				return true;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is:"+e.getMessage());
			transactionManager.rollback(status);
			return false;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public boolean deleteReleasedExamBooking(final String sapid, final String subject,final String year,final String month,final String trackId,final String examStartDateTime)
	{
		TransactionDefinition def = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(def);
		try
		{
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql="delete from exam.exams_pg_scheduleinfo_mettl where sapid= ? and subject= ? " 
					+" and year= ? and month= ? and trackId= ? and examStartDateTime= ? ";
			int rowsDeleted=jdbcTemplate.update(sql,new Object[] {sapid,subject,year,month,trackId,examStartDateTime});
			if(rowsDeleted>0)
			{
				transactionManager.commit(status);
				return true;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is:"+e.getMessage());
			transactionManager.rollback(status);
			return false;
		}
		return false;
	}
	
}
