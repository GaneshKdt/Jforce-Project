/**
 * 
 */
package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.StudentSessionCoursesBean;

/**
 * @author vil_m
 *
 */
public class StudentSessionCoursesDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	
	public static final String SCHEMA_EXAM = "exam";
	public static final String TABLE_STUDENT_SESSION_COURSES_MAPPING = "student_session_courses_mapping";
	
	//public static final String ROUND_BRACKET_OPEN = "(";
	//public static final String ROUND_BRACKET_CLOSE = ")";
	//public static final String COMMA = ",";
	
	private static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesDAO.class);
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		//setBaseDataSource();
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void endTransaction(boolean activity) {
		if (activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}

	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;

		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}
	
	public StudentSessionCoursesBean read(final StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesDAO : read");
		String sql = null;
		StringBuffer strBuf = null;
		StudentSessionCoursesBean dataBean = null;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append("SELECT userId, program_sem_subject_id, acadYear, acadMonth, role");
			strBuf.append(" FROM ").append(SCHEMA_EXAM).append(".").append(TABLE_STUDENT_SESSION_COURSES_MAPPING).append(" t");
			strBuf.append(" WHERE t.userId = ?");
			
			sql = strBuf.toString();
			
			logger.info("StudentSessionCoursesDAO : read : Query : "+ sql);
			
			dataBean = jdbcTemplate.execute(sql, new PreparedStatementCallback<StudentSessionCoursesBean>() {
	
				@Override
				public StudentSessionCoursesBean doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
					// TODO Auto-generated method stub
					int idx = 1;
					arg0.setString(idx++, bean.getSapId());
					
					StudentSessionCoursesBean bean = null;
					List<Integer> listCourses = null;
					listCourses = new ArrayList<Integer>();
					ResultSet rs = arg0.executeQuery();
					while (rs.next()) {
						if(null == bean) {
							bean = new StudentSessionCoursesBean();
							bean.setSapId(rs.getString("userId"));
							bean.setAcadYear(rs.getString("acadYear"));
							bean.setAcadMonth(rs.getString("acadMonth"));
							bean.setRole(rs.getString("role"));
							bean.setCourseIds(listCourses);
						}
						listCourses.add(rs.getInt("program_sem_subject_id"));
					}
					return bean;
				}
			});
			if(null != dataBean) {
				logger.info("StudentSessionCoursesDAO : read : List Size : "+ dataBean.getCourseIds().size());
			}
		
		} catch(DataAccessException de) {
			logger.error("StudentSessionCoursesDAO : read : " + de.getMessage());
			throw de;
		} catch(Exception e) {
			logger.error("StudentSessionCoursesDAO : read : " + e.getMessage());
			throw e;
		} finally {
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return dataBean;
	}
	
	/*public int delete(final StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesDAO : delete");
		String sql = null;
		int rowsDeleted = -1;
		StringBuffer strBuf = null;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append("DELETE FROM ").append(SCHEMA_EXAM).append(".").append(TABLE_STUDENT_SESSION_COURSES_MAPPING);
			strBuf.append(" WHERE userId = ?");
			
			sql = strBuf.toString();
			
			rowsDeleted = jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement arg0) throws SQLException {
					// TODO Auto-generated method stub
					arg0.setString(1, bean.getSapId());
				}
			});
			logger.info("StudentSessionCoursesDAO : delete : Total Rows Deleted : " + rowsDeleted);
		} catch (DataAccessException de) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : delete : Error : " + de.getMessage());
			throw de;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : delete : Error : " + e.getMessage());
			throw e;
		} finally {
			sql = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return rowsDeleted;
	}*/
	
	public int delete(final StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesDAO : delete");
		String sql = null;
		int rowsDeleted = -1;
		StringBuffer strBuf = null;
		//List<Integer> courseIdList = null;
		//String courseIdStr = null;
		try {
			/*courseIdStr = ROUND_BRACKET_OPEN;
			courseIdList = bean.getCourseIds();
			for(int i = 0; i < courseIdList.size(); i++) {
				if(i > 0) {
					courseIdStr += COMMA;
		          } 
				courseIdStr += courseIdList.get(i);
			}
			courseIdStr += ROUND_BRACKET_CLOSE;*/
			
			strBuf = new StringBuffer();
			strBuf.append("DELETE FROM ").append(SCHEMA_EXAM).append(".").append(TABLE_STUDENT_SESSION_COURSES_MAPPING);
			strBuf.append(" WHERE userId = ? AND acadYear = ? AND acadMonth = ?");
			//strBuf.append(" WHERE userId = ? AND acadYear = ? AND acadMonth = ? AND program_sem_subject_id in ").append(courseIdStr);
			
			sql = strBuf.toString();
			logger.info("StudentSessionCoursesDAO : delete : Query : "+ sql);
			
			rowsDeleted = jdbcTemplate.update(sql, new PreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement arg0) throws SQLException {
					// TODO Auto-generated method stub
					arg0.setString(1, bean.getSapId());
					arg0.setInt(2, toInteger(bean.getAcadYear()));
					arg0.setString(3, bean.getAcadMonth());
				}
			});
			logger.info("StudentSessionCoursesDAO : delete : Total Rows Deleted : " + rowsDeleted);
		} catch (DataAccessException de) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : delete : Error : " + de.getMessage());
			throw de;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : delete : Error : " + e.getMessage());
			throw e;
		} finally {
			//courseIdStr = null;
			sql = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return rowsDeleted;
	}
	
	public int update(final StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesDAO : update");
		List<Integer> courseIdList = null;
		Integer courseId = null;
		boolean isUpdated = Boolean.FALSE;
		int rowsAffected = 0;
		
		try {
			startTransaction("updateSSCBean");
			courseIdList = bean.getCourseIds();
			for(int i = 0; i < courseIdList.size(); i++) {
				courseId = courseIdList.get(i);
				rowsAffected += this.update(bean, courseId);
				if(1 == rowsAffected) {
					isUpdated = Boolean.TRUE;
				} else if(0 == rowsAffected) {
					isUpdated = Boolean.FALSE;
					break;
				}
			}
		} catch(Exception ex) {
			isUpdated = Boolean.FALSE;
			throw ex;
		} finally {
			endTransaction(isUpdated);
			if(isUpdated) {
				logger.info("StudentSessionCoursesDAO : update : COMMIT. Total Rows Updated : "+rowsAffected);
			} else {
				logger.error("StudentSessionCoursesDAO : update : ROLLBACK...No rows updated.");
			}
		}
		return rowsAffected;
	}
	
	public int update(final StudentSessionCoursesBean bean, final Integer courseId) {
		logger.info("Entering StudentSessionCoursesDAO : update");
		StringBuffer strBuf = null;
		final String sql;
		int rowsUpdated = 0;
		
		try {
			strBuf = new StringBuffer();
			strBuf.append("UPDATE ").append(SCHEMA_EXAM).append(".").append(TABLE_STUDENT_SESSION_COURSES_MAPPING);
			strBuf.append(" SET");
			strBuf.append(" acadYear = ?, acadMonth = ?, updatedBy = ?, updatedDate = current_timestamp()");
			strBuf.append(" WHERE userId = ? AND program_sem_subject_id = ?");
			
			sql = strBuf.toString();
			emptyStringBuffer(strBuf);
			
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					int idx = 1;
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					
					ps.setInt(idx++, toInteger(bean.getAcadYear()));
					ps.setString(idx++, bean.getAcadMonth());
					ps.setString(idx++, bean.getLastModifiedBy());
					
					ps.setString(idx++, bean.getSapId());
					ps.setInt(idx++, courseId);
					
					return ps;
				}
			};
			rowsUpdated = jdbcTemplate.update(psc);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : update : " + e.getMessage());
			throw e;
		} finally {
			logger.info("StudentSessionCoursesDAO : update : Total Rows updated : "+ rowsUpdated);
		}
		return rowsUpdated;
	}
	
	public int create(final StudentSessionCoursesBean bean) {
		logger.info("Entering StudentSessionCoursesDAO : create");
		List<Integer> courseIdList = null;
		Integer courseId = null;
		boolean isSaved = Boolean.FALSE;
		boolean isDeleted = Boolean.FALSE;
		boolean toCommit = Boolean.FALSE;
		int rowsDeleted = 0;
		int rowsAffected = 0;
		
		try {
			startTransaction("createSSCBean");
			rowsDeleted = this.delete(bean);
			if(rowsDeleted >= 0) {
				isDeleted = Boolean.TRUE;
			}
			courseIdList = bean.getCourseIds();
			for(int i = 0; i < courseIdList.size(); i++) {
				courseId = courseIdList.get(i);
				rowsAffected += this.create(bean, courseId);
				if(1 == rowsAffected) {
					isSaved = Boolean.TRUE;
				} else if(0 == rowsAffected) {
					isSaved = Boolean.FALSE;
					break;
				}
			}
		} catch(Exception ex) {
			isSaved = Boolean.FALSE;
			throw ex;
		} finally {
			toCommit = (isSaved && isDeleted);
			endTransaction(toCommit);
			if(toCommit) {
				logger.info("StudentSessionCoursesDAO : create : COMMIT. Total Rows Created : "+rowsAffected);
			} else {
				logger.error("StudentSessionCoursesDAO : create : ROLLBACK...No rows inserted.");
			}
		}
		return rowsAffected;
	}
	
	public int create(final StudentSessionCoursesBean bean, final Integer courseId) {
		logger.info("Entering StudentSessionCoursesDAO : create for CourseId : "+courseId);
		
		Long rowId = -1L;
		StringBuffer strBuf = null;
		KeyHolder keyHolder = null;
		int rowsAffected = -1;
		
		try {
			bean.setStatus(StudentSessionCoursesBean.KEY_ERROR);
			strBuf = new StringBuffer();
			strBuf.append("INSERT INTO ").append(SCHEMA_EXAM).append(".").append(TABLE_STUDENT_SESSION_COURSES_MAPPING);
			strBuf.append(" (userId, program_sem_subject_id, acadYear, acadMonth, role");
			strBuf.append(" , createdBy, createdDate, updatedBy, updatedDate)");
			strBuf.append(" VALUES");
			strBuf.append(" (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), ?, CURRENT_TIMESTAMP())");
			
			final String sql = strBuf.toString();

			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

					ps.setString(1, bean.getSapId());
					ps.setInt(2, courseId);
					ps.setInt(3, toInteger(bean.getAcadYear()));
					ps.setString(4, bean.getAcadMonth());
					ps.setString(5, bean.getRole());
					ps.setString(6, bean.getCreatedBy());
					ps.setString(7, bean.getLastModifiedBy());
					
					return ps;
				}
			};
			
			keyHolder = new GeneratedKeyHolder();
			rowsAffected = jdbcTemplate.update(psc, keyHolder);
			rowId = keyHolder.getKey().longValue();
			logger.info("StudentSessionCoursesDAO : Success, Row Created with StudentSessionCourses Id :" + rowId);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("StudentSessionCoursesDAO : StudentSessionCourses Error : " + e.getMessage());
			throw e;
		} finally {
			emptyStringBuffer(strBuf);
			strBuf = null;
			keyHolder = null;
		}
		return rowsAffected;
	}
	
	public static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
}
