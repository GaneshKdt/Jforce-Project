package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamLiveSettingMBAWX;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.ExecutiveExamCenter;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.ExecutiveTimetableBean;
import com.nmims.beans.Page;
import com.nmims.helpers.PaginationHelper;

public class ExecutiveExamDao extends ExecutiveBaseDao{
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
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int insertExecutiveExamCenter(ExecutiveExamCenter examCenter) {

		PreparedStatementCreator psc = null;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final ExecutiveExamCenter e = examCenter;
		jdbcTemplate = new JdbcTemplate(dataSource);
		String errorMessage = null;
		final String sql = "INSERT INTO exam.executive_examcenter("
					+ "examCenterName," + "locality," + "city," + "state,"
					+ "capacity," + "address," + "createdBy,"
					+ "lastModifiedBy," + "createdDate," + "lastModifiedDate,"
					+ "year," + "month," + "googleMapUrl,batchYear, batchMonth )"
					+ "VALUES ( ?,?,?,?,?,?,?,?,sysdate(),sysdate(),?,?,?,?,?)";
			psc = new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, e.getExamCenterName());
					ps.setString(2, e.getLocality());
					ps.setString(3, e.getCity());
					ps.setString(4, e.getState());
					ps.setString(5, e.getCapacity());
					ps.setString(6, e.getAddress());
					ps.setString(7, e.getCreatedBy());
					ps.setString(8, e.getLastModifiedBy());
					ps.setString(9, e.getYear());
					ps.setString(10, e.getMonth());
					ps.setString(11, e.getGoogleMapUrl());
					ps.setString(12, e.getBatchYear());
					ps.setString(13, e.getBatchMonth());
				

					return ps;
				}
			};
		
			 String sqlForSlotMapping ="";
			  int centerId=0;
		try {
			jdbcTemplate.update(psc, keyHolder);

			centerId = keyHolder.getKey().intValue();
			
			String sqlForExamSlots = "SELECT * FROM exam.sas_timetable "
					+ " where examYear="+e.getYear()+" "
							+ " and examMonth='"+e.getMonth()+"' "
							+ " and enrollmentYear='"+e.getBatchYear()+"'" 
							+ " and enrollmentMonth= '"+e.getBatchMonth()+"'   group by date, startTime";

			if(centerId != 0) {
			List<ExecutiveTimetableBean> examCenterSlots = (List<ExecutiveTimetableBean>)jdbcTemplate.query(sqlForExamSlots,new BeanPropertyRowMapper(ExecutiveTimetableBean.class));
			
			for(ExecutiveTimetableBean slot : examCenterSlots) {
				//id, examcenterId, date, startTime, capacity, booked, onHold, year, month
				 sqlForSlotMapping = "INSERT INTO exam.executive_exam_center_slot_mapping("
						+ " examcenterId, date, startTime, capacity, booked, onHold, year, month,batchYear, batchMonth  )"
						+ "VALUES ( ?,?,?,?,0,0,?,?,?,?)";
				
				try {
					jdbcTemplate.update(sqlForSlotMapping, new Object[] {
							centerId, slot.getDate(), slot.getStartTime(), e.getCapacity(),e.getYear(),e.getMonth(),e.getBatchYear(),e.getBatchMonth() 
					});
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					
					errorMessage= e1.getMessage();
				}

			}
				
			}else {
				errorMessage="Error in saving center in Db";
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			errorMessage= e1.getMessage();
		} 
		if(errorMessage==null) {
			return centerId;
		}else {
			return 0;
		} 
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String updateExecutiveExamCenter(ExecutiveExamCenter examCenter) {
		String centerId = examCenter.getCenterId();
		jdbcTemplate = new JdbcTemplate(dataSource);
		String errorMessage = null;
		final String sql = "update exam.executive_examcenter set "
					+ "examCenterName = ? ," 
					+ "locality = ? ," 
					+ "city = ? ," 
					+ "state = ? ," 
					+ "address = ? ," 
					+ "lastModifiedBy = ?," 
					+ "lastModifiedDate = sysdate(),"
					+ "year = ? ," 
					+ "month = ?,"
					+ "googleMapUrl = ? "
					+ " where centerId = ?";
		
		try {
				jdbcTemplate.update(sql, new Object[] { 
						examCenter.getExamCenterName(),
						examCenter.getLocality(),
						examCenter.getCity(),
						examCenter.getState(),
						examCenter.getAddress(),
						examCenter.getLastModifiedBy(),
						examCenter.getYear(),
						examCenter.getMonth(),
						examCenter.getGoogleMapUrl(),
						examCenter.getCenterId()
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				errorMessage = e.getMessage();
			}
		return errorMessage;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public ExecutiveExamCenter findById(String centerId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM exam.executive_examcenter " + "where centerId = ?";
		ExecutiveExamCenter examCenter = (ExecutiveExamCenter) jdbcTemplate
				.queryForObject(sql, new Object[] { centerId },
						new BeanPropertyRowMapper(ExecutiveExamCenter.class));

		return examCenter;
	}
	
	
	
	public String deleteSASExamCenter(String centerId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String error=null;
		String sql = "Delete from exam.executive_examcenter where centerId = ?";
		String sqlSlots = "Delete from exam.executive_exam_center_slot_mapping where examcenterId = ?";
	
		try {
			jdbcTemplate.update(sql, new Object[] { centerId});
			jdbcTemplate.update(sqlSlots, new Object[] { centerId});
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			 error=e.getMessage();
		}
		return error;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<ExecutiveExamCenter> getExecutiveExamCentersPage(int pageNo, int pageSize,
			ExecutiveExamCenter examCenter) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "", countSql = "";
	
			sql = "SELECT * FROM exam.executive_examcenter where 1 = 1 ";
			countSql = "SELECT count(*) FROM executive_examcenter where 1 = 1 ";
		

		if (examCenter.getExamCenterName() != null
				&& !("".equals(examCenter.getExamCenterName()))) {
			sql = sql + " and examCenterName like  ? ";
			countSql = countSql + " and examCenterName like  ? ";
			parameters.add("%" + examCenter.getExamCenterName() + "%");
		}
		if (examCenter.getCity() != null && !("".equals(examCenter.getCity()))) {
			sql = sql + " and city = ? ";
			countSql = countSql + " and city = ? ";
			parameters.add(examCenter.getCity());
		}
		if (examCenter.getState() != null
				&& !("".equals(examCenter.getState()))) {
			sql = sql + " and state = ? ";
			countSql = countSql + " and state = ? ";
			parameters.add(examCenter.getState());
		}
		
		if (examCenter.getYear() != null && !("".equals(examCenter.getYear()))) {
			sql = sql + " and year = ? ";
			countSql = countSql + " and year = ? ";
			parameters.add(examCenter.getYear());
		}
		if (examCenter.getMonth() != null
				&& !("".equals(examCenter.getMonth()))) {
			sql = sql + " and month = ? ";
			countSql = countSql + " and month = ? ";
			parameters.add(examCenter.getMonth());
		}
		
		if (examCenter.getBatchYear() != null && !("".equals(examCenter.getBatchYear()))) {
			sql = sql + " and batchYear = ? ";
			countSql = countSql + " and batchYear = ? ";
			parameters.add(examCenter.getBatchYear());
		}
		if (examCenter.getBatchMonth() != null
				&& !("".equals(examCenter.getBatchMonth()))) {
			sql = sql + " and batchMonth = ? ";
			countSql = countSql + " and batchMonth = ? ";
			parameters.add(examCenter.getBatchMonth());
		}

		sql = sql + " order by city ";
		Object[] args = parameters.toArray();

		PaginationHelper<ExecutiveExamCenter> pagingHelper = new PaginationHelper<ExecutiveExamCenter>();
		Page<ExecutiveExamCenter> page = pagingHelper.fetchPage(jdbcTemplate,
				countSql, sql, args, pageNo, pageSize,
				new BeanPropertyRowMapper(ExecutiveExamCenter.class));

		return page;
	}

	@Transactional(readOnly = true)
	public List<ExecutiveExamCenter> getExecutiveExamCenterSlots(ExecutiveExamCenter ec) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		
			sql = "SELECT b.centerId, a.date, a.starttime, a.capacity, COALESCE(a.onhold, 0) onHold,"
					+ " (a.capacity - COALESCE(a.booked, 0)  - (COALESCE(a.onHold , 0))) available ,"
					+ " a.year, a.month, "
					+ " COALESCE(a.booked, 0) booked,"
					+ " b.examCenterName, b.locality, b.city, b.state, b.address "

					+ " FROM exam.executive_exam_center_slot_mapping a, exam.executive_examcenter b  "

					+ " WHERE a.examcenterid = b.centerId "
					+ " and a.year = ? and a.month = ? "
					+ " and a.examcenterid = ?";
		


		List<ExecutiveExamCenter> allCentersList = jdbcTemplate.query(sql,
				new Object[] { ec.getYear(), ec.getMonth(), ec.getCenterId() },
				new BeanPropertyRowMapper(ExecutiveExamCenter.class));

		return allCentersList;
	}
	
	public void updateCapacity(List<ExecutiveExamCenter> centerSlotList) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		
			sql = "update exam.executive_exam_center_slot_mapping set capacity = ? "
					+ " where examcenterId = ? and date = ? and starttime = ? and year = ? and month = ? ";


		for (ExecutiveExamCenter ExecutiveExamCenter : centerSlotList) {
			jdbcTemplate
			.update(sql,
					new Object[] { ExecutiveExamCenter.getCapacity(),
					ExecutiveExamCenter.getCenterId(),
					ExecutiveExamCenter.getDate(),
					ExecutiveExamCenter.getStarttime(),
					ExecutiveExamCenter.getYear(),
					ExecutiveExamCenter.getMonth() });
		}

	}
	
	
	
	//added because of sas subject insertion details :START
	public void insertMakeLiveSubjectsEntry(ExecutiveExamOrderBean executiveExamOrderBean) {
		String sql="INSERT INTO  exam.live_exam_subjects ("
				+ "id, subject , program , prgmStructApplicable , acadYear , acadMonth , examYear, examMonth, createdBy"
				+ " , createdDate ,  lastModifiedBy, lastModifiedDate ) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,sysdate(),?,sysdate())";
		int id=executiveExamOrderBean.getId();
		String subject=executiveExamOrderBean.getSubject();
		String program=executiveExamOrderBean.getProgram();
		String prgmStructApplicable=executiveExamOrderBean.getPrgmStructApplicable();
		String year=executiveExamOrderBean.getAcadYear();
		String month=executiveExamOrderBean.getAcadMonth();
		String examYear=executiveExamOrderBean.getExamYear();
		String examMonth=executiveExamOrderBean.getExamMonth();
		String createdBy=executiveExamOrderBean.getCreatedBy();
		String lastModifiedBy=executiveExamOrderBean.getLastModifiedBy();
		
		jdbcTemplate.update(sql , new Object[] {
				id,
				subject,
				program,
				prgmStructApplicable,
				year,
				month,
				examYear,
				examMonth,
				createdBy,
				lastModifiedBy
		});
		
	}
	
	

	//added because of sas subject insertion details:END
	@Transactional(readOnly = true)
	public List<ExecutiveExamOrderBean> getSubjectsList(ExecutiveExamOrderBean ec) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		
			sql = "select * from exam.live_exam_subjects s where s.program=? and " 
			+ " s.prgmStructApplicable=? and s.acadYear=? and s.acadMonth=? and s.examYear=? and s.examMonth=?";
		


		List<ExecutiveExamOrderBean> subjectList = jdbcTemplate.query(sql,
				new Object[] { ec.getProgram(),ec.getPrgmStructApplicable(),ec.getAcadYear(),
						ec.getAcadMonth(),ec.getExamYear(),ec.getExamMonth()},
				new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));

		return subjectList;
	}
	
	
	@Transactional(readOnly = true)		
	public ExecutiveExamOrderBean findSubjectById(String id) {
		String sql = "select * from Exam.live_exam_subjects s where s.id=? ";

		jdbcTemplate = new JdbcTemplate(dataSource);
		ExecutiveExamOrderBean session = (ExecutiveExamOrderBean) jdbcTemplate.queryForObject(
				sql, new Object[] { id }, new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));

		return session;
	}
	
	@Transactional(readOnly = true)
	public List<String> getSubjectListBasedOnProgram(String program,String programStructureApplicable) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "";
		
			sql = "select ps.subject from exam.program_subject ps where ps.program=? and ps.prgmStructApplicable=? ";
		


		List<String> subjectListBassedOnProgram = jdbcTemplate.query(sql,
				new Object[] { program,programStructureApplicable },
				new SingleColumnRowMapper(String.class));

		return subjectListBassedOnProgram;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public ExecutiveExamOrderBean findById(Integer id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.live_exam_subjects s where s.id= ? ";
		ExecutiveExamOrderBean executiveExamOrderBean = (ExecutiveExamOrderBean) jdbcTemplate
				.queryForObject(sql, new Object[] { id },
						new BeanPropertyRowMapper(ExecutiveExamCenter.class));

		return executiveExamOrderBean;
	}
	
	
	public void deleteSubjectsEntry(int id) {
		String sql = "DELETE FROM exam.live_exam_subjects " + 
				"				WHERE id = ?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, new Object[] { 
				id
		});
		
	}
	
	
	public void updateSubjectsEntry(ExecutiveExamOrderBean executiveExamOrderBean) {
		String sql = "update exam.live_exam_subjects "
				+ " set "  
				+ " acadYear = ?,"
				+ " acadMonth = ?" 
				+ " where id = ?";

		jdbcTemplate.update(sql, new Object[]{executiveExamOrderBean.getAcadYear(),
				executiveExamOrderBean.getAcadMonth(),executiveExamOrderBean.getId()});

		
	}
	
	//pass generation for executive_exam_bookings table :-START
		/*
		 @Transactional(readOnly = true) 
		 public ArrayList<ExecutiveBean> listOfExecutiveExamBookings(ExecutiveBean executiveBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " select * from exam.executive_exam_bookings e " + 
					"where e.year=? " + 
					"and e.month=? " + 
					"and  (e.password = '' OR e.password IS NULL)" ;
			ArrayList<ExecutiveBean> bookedList = (ArrayList<ExecutiveBean>)jdbcTemplate.query(sql, new Object[]{
					executiveBean.getExamYear(),executiveBean
					.getExamMonth()}, new BeanPropertyRowMapper(ExecutiveBean.class));
			return bookedList;
		}
		
		
		
        public boolean assignPassword(ExecutiveBean executiveBean) {
            jdbcTemplate = new JdbcTemplate(dataSource);
            String sql = "";
            try {
               sql = "update exam.executive_exam_bookings e " + 
                    " set e.password= ? " + 
                    " where e.sapId= ? " + 
                    " and e.year=? " + 
                    " and e.month=? ";

              jdbcTemplate.update(sql,new Object[] {
            executiveBean.getPassword(),
            executiveBean.getSapid(),
            executiveBean.getExamYear(),
            executiveBean.getExamMonth()});
            }
            
            catch(Exception e) {
                            
                            return false;
            }
            return true;
}

		
		
		@Transactional(readOnly = true)
		public ArrayList<ExecutiveBean> listOfBookings(ExecutiveBean executiveBean) {
			jdbcTemplate = new JdbcTemplate(dataSource);
			String sql = " select * from exam.executive_exam_bookings e " + 
					"	where e.year=? " + 
					"	and e.month=? " + 
					"	and e.booked='Y' " ;
			ArrayList<ExecutiveBean> confirmedPassList = (ArrayList<ExecutiveBean>)jdbcTemplate.query(sql, new Object[]{executiveBean
					.getExamYear(),executiveBean.getExamMonth()}, new BeanPropertyRowMapper(ExecutiveBean.class));
			return confirmedPassList;
		}*/
		//END
	

	@Transactional(readOnly = true)
	public List<ExamBookingTransactionBean> getExecutiveABRecords(ExamBookingTransactionBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String year = searchBean.getYear();
		String month = searchBean.getMonth();

		String sql = "select eb.* from exam.executive_exam_bookings eb " + 
				"				 left outer join exam.marks m  " + 
				"				 on m.sapid = eb.sapid and m.subject = eb.subject  " + 
				"				 and m.year = eb.year and m.month = eb.month  " + 
				"				 where " + 
				"				 eb.booked = 'Y'   " + 
				"				 and eb.year = ? and eb.month = ?  " + 
				"				 and (m.writenscore is null or m.writenscore = '' or m.writenscore ='AB')";
				
		Object[] args = new Object[]{year, month};

		try {
			return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(ExamBookingTransactionBean.class));
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
			return null;
		}
	
	}

	public boolean updateHallTicketStats(ExamLiveSettingMBAWX exam) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			String sql = "INSERT INTO  exam.mba_wx_exam_live_setting ("
					+ "acadsYear, acadsMonth , examYear , examMonth  ,consumerProgramStructureId,  created_at , updated_at, type)"
					+ " VALUES (?,?,?,?,'111',sysdate(),sysdate(),?)";

			jdbcTemplate.update(sql, new Object[] { exam.getAcadsYear(), exam.getAcadsMonth(), exam.getExamYear(),
					exam.getExamMonth(), "Hall Ticket" });
			return true;
		} catch (DataAccessException e) {
			return false;
		}

	}
	
	@Transactional(readOnly = true)
	public List<ExamLiveSettingMBAWX> getHallTicketLives() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = " select * from exam.mba_wx_exam_live_setting  where type ='Hall Ticket'";
		List<ExamLiveSettingMBAWX> examLiveSettingMBAWX = (List<ExamLiveSettingMBAWX>) jdbcTemplate.query(sql,
				new Object[] {}, new BeanPropertyRowMapper(ExamLiveSettingMBAWX.class));

		return examLiveSettingMBAWX;

	}
	
	public boolean deleteFromHallTicketLive(ExamLiveSettingMBAWX exam) {

		try {
			String sql = "DELETE FROM exam.mba_wx_exam_live_setting "
					+ "				WHERE acadsYear=? and acadsMonth=? and examYear =  ? "
					+ "and examMonth=? and  consumerProgramStructureId=? and type=?";
			jdbcTemplate = new JdbcTemplate(dataSource);
			jdbcTemplate.update(sql, new Object[] { exam.getAcadsYear(), exam.getAcadsMonth(), exam.getExamYear(),
					exam.getExamMonth(), "111", "Hall Ticket" });
			return true;
		} catch (DataAccessException e) {
			return false;
		}

	}
}
