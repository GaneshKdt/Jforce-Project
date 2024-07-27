/**
 * 
 */
package com.nmims.daos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.MDMSubjectCodeBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;
import org.springframework.jdbc.core.PreparedStatementCallback;
import java.sql.PreparedStatement;

/**
 * @author vil_m
 *
 */
public class MDMSubjectCodeDAO extends BaseDAO {
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private PlatformTransactionManager transactionManager;
	private TransactionStatus status;
	private static final Logger logger = LoggerFactory.getLogger(MDMSubjectCodeDAO.class);

	private static final String SPACE = " ";
	private static final String COMMA = ",";
	private static final String CURLY_OPENB = "(";
	private static final String CURLY_CLOSEB = ")";
	private static final String EQUALTO = "=";
	private static final String Q_MARK = "?";
	private static final String S_SELECT = "Select ";
	private static final String S_FROM = " from ";
	private static final String S_INSERT_INTO = "insert into ";
	private static final String S_VALUES = " values ";
	private static final String S_UPDATE = "update ";
	private static final String S_SET = "set ";
	private static final String S_WHERE = " where ";

	private static final String TABLE_MDM_SUBJECTCODE = "exam.mdm_subjectcode";
	private static final String COL_ID = "id";
	private static final String COL_SUBJECTNAME = "subjectname";
	private static final String COL_SUBJECTCODE = "subjectcode";
	private static final String COL_COMMONSUBJECT = "commonSubject";
	private static final String COL_ACTIVE = "active";
	private static final String COL_IS_PROJECT = "isProject";
	private static final String COL_SIFY_SUBJECTCODE = "sifySubjectCode";
	private static final String COL_SPECIALIZATIONTYPE = "specializationType";
	private static final String COL_STUDENTTYPE = "studentType";
	private static final String COL_DESCRIPTION = "description";
	private static final String COL_SESSIONTIME = "sessionTime";
	private static final String COL_CREATEDBY = "createdBy";
	private static final String COL_CREATEDDATE = "createdDate";
	private static final String COL_LASTMODIFIEDBY = "lastModifiedBy";
	private static final String COL_LASTMODIFIEDDATE = "lastModifiedDate";

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		setBaseDataSource();
		// super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	protected void endTransaction(boolean activity) {
		if(activity) {
			transactionManager.commit(this.status);
		} else {
			transactionManager.rollback(this.status);
		}
		this.status = null;
	}
	
	public void end_Transaction(boolean activity) {
		this.endTransaction(activity);
	}

	/*@Deprecated 
	public void startTransaction(String transactionName) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.status = transactionManager.getTransaction(def);
	}*/

	/*@Deprecated
	public void startTransaction(String transactionName, boolean readOnly) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setReadOnly(readOnly);//for update - Boolean.FALSE, read - Boolean.TRUE
		this.status = transactionManager.getTransaction(def);
	}*/
	
	protected void startTransaction(String transactionName, boolean readOnly, int propagationBehaviour) {
		DefaultTransactionDefinition def = null;
		
		def = new DefaultTransactionDefinition();
		def.setName(transactionName);
		def.setPropagationBehavior(propagationBehaviour);
		def.setReadOnly(readOnly);//for update - Boolean.FALSE, read - Boolean.TRUE
		this.status = transactionManager.getTransaction(def);
	}
	
	public void start_Transaction_U_PR(String transactionName) {
		this.startTransaction(transactionName, Boolean.FALSE, TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public void start_Transaction_R_PR(String transactionName) {
		this.startTransaction(transactionName, Boolean.TRUE, TransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	public boolean batchSaveMDMSubjectCodeData(final List<MDMSubjectCodeBean> list) {
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;
		
		try {
			logger.info("MDMSubjectCodeDAO.batchSaveMDMSubjectCodeData : Total : " + list.size());
			strBuf = new StringBuffer();
			strBuf.append(S_INSERT_INTO).append(TABLE_MDM_SUBJECTCODE).append(SPACE);
			strBuf.append(CURLY_OPENB);
			strBuf.append(COL_SUBJECTNAME).append(COMMA);
			strBuf.append(COL_SUBJECTCODE).append(COMMA).append(COL_COMMONSUBJECT).append(COMMA).append(COL_ACTIVE)
					.append(COMMA);
			strBuf.append(COL_IS_PROJECT).append(COMMA);
			//strBuf.append(COL_SIFY_SUBJECTCODE).append(COMMA)
			strBuf.append(COL_SPECIALIZATIONTYPE).append(COMMA);
			strBuf.append(COL_STUDENTTYPE).append(COMMA).append(COL_CREATEDBY).append(COMMA);
			strBuf.append(COL_CREATEDDATE).append(COMMA).append(COL_LASTMODIFIEDBY).append(COMMA)
					.append(COL_LASTMODIFIEDDATE).append(COMMA).append(COL_DESCRIPTION);
			strBuf.append(COMMA).append(COL_SESSIONTIME);
			strBuf.append(CURLY_CLOSEB);
			strBuf.append(S_VALUES).append(CURLY_OPENB);
			strBuf.append("?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), ?, CURRENT_TIMESTAMP(), ?, ?")
					.append(CURLY_CLOSEB);
			query = strBuf.toString();
	
			start_Transaction_U_PR("BatchSaveSubjectCode");
	
			int[] arrResults = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int arg1) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, list.get(arg1).getSubjectname());
					ps.setString(2, list.get(arg1).getSubjectcode());
					ps.setString(3, list.get(arg1).getCommonSubject());
					ps.setString(4, list.get(arg1).getActive());
					ps.setInt(5, toInteger(list.get(arg1).getIsProject()));
					//ps.setInt(6, list.get(arg1).getSifySubjectCode());
					ps.setString(6, list.get(arg1).getSpecializationType());
					ps.setString(7, list.get(arg1).getStudentType());
					ps.setString(8, list.get(arg1).getCreatedBy());
					ps.setString(9, list.get(arg1).getLastModifiedBy());
					ps.setString(10, list.get(arg1).getDescription());
					ps.setInt(11, list.get(arg1).getSessionTime());
				}
				
				@Override
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return list.size();
				}
			});
			logger.info("MDMSubjectCodeDAO.batchSaveMDMSubjectCodeData : Total Rows created : " + arrResults.length);
			/*for(int k = 0; k < arrResults.length; k++) {
				logger.info("Array : " + k + " > " + arrResults[k]);
			}*/
			isSuccess = Boolean.TRUE;
			end_Transaction(isSuccess);
		} catch (Exception e) {
			end_Transaction(Boolean.FALSE);
			logger.error("MDMSubjectCodeDAO.batchSaveMDMSubjectCodeData : " + e.getMessage());
			throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}

	public boolean saveMDMSubjectCodeData(MDMSubjectCodeBean mdmSubjectCodeBean) {
		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;
		//DefaultTransactionDefinition def = null;
		//TransactionStatus status = null;
		Object[] arrObj = null;
		int[] argTypes = null;
		int arraySize = 11;
		int index = 0;
		
		try {
			arrObj = new Object[arraySize];
			arrObj[index++] = mdmSubjectCodeBean.getSubjectname();
			arrObj[index++] = mdmSubjectCodeBean.getSubjectcode();
			arrObj[index++] = mdmSubjectCodeBean.getCommonSubject();
			arrObj[index++] = mdmSubjectCodeBean.getActive();//4
			arrObj[index++] = toInteger(mdmSubjectCodeBean.getIsProject());//5
			//arrObj[index++] = mdmSubjectCodeBean.getSifySubjectCode();
			arrObj[index++] = mdmSubjectCodeBean.getSpecializationType();//6
			arrObj[index++] = mdmSubjectCodeBean.getStudentType();
			arrObj[index++] = mdmSubjectCodeBean.getCreatedBy();
			arrObj[index++] = mdmSubjectCodeBean.getLastModifiedBy();//9
			arrObj[index++] = mdmSubjectCodeBean.getDescription();
			arrObj[index++] = mdmSubjectCodeBean.getSessionTime();
	
			strBuf = new StringBuffer();
			strBuf.append(S_INSERT_INTO).append(TABLE_MDM_SUBJECTCODE).append(SPACE);
			strBuf.append(CURLY_OPENB);
			strBuf.append(COL_SUBJECTNAME).append(COMMA);
			strBuf.append(COL_SUBJECTCODE).append(COMMA).append(COL_COMMONSUBJECT).append(COMMA).append(COL_ACTIVE)
					.append(COMMA);
			strBuf.append(COL_IS_PROJECT).append(COMMA);
			//strBuf.append(COL_SIFY_SUBJECTCODE).append(COMMA);
			strBuf.append(COL_SPECIALIZATIONTYPE).append(COMMA);
			strBuf.append(COL_STUDENTTYPE).append(COMMA).append(COL_CREATEDBY).append(COMMA);
			strBuf.append(COL_CREATEDDATE).append(COMMA).append(COL_LASTMODIFIEDBY).append(COMMA)
					.append(COL_LASTMODIFIEDDATE).append(COMMA).append(COL_DESCRIPTION);
			strBuf.append(COMMA).append(COL_SESSIONTIME);
			strBuf.append(CURLY_CLOSEB);
			strBuf.append(S_VALUES).append(CURLY_OPENB);
			strBuf.append("?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP(), ?, CURRENT_TIMESTAMP(),?,?")
					.append(CURLY_CLOSEB);
	
			index = 0;
			argTypes = new int[arraySize];
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;//4
			argTypes[index++] = java.sql.Types.INTEGER;
			//argTypes[index++] = java.sql.Types.INTEGER;
			argTypes[index++] = java.sql.Types.VARCHAR;//6
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;
			// argTypes[index] = java.sql.Types.TIMESTAMP;
			argTypes[index++] = java.sql.Types.VARCHAR;
			argTypes[index++] = java.sql.Types.VARCHAR;
	
			query = strBuf.toString();
	
			//def = new DefaultTransactionDefinition();
			//def.setName("SaveSubjectCode");
			//def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			//status = transactionManager.getTransaction(def);
			start_Transaction_U_PR("SaveSubjectCode");
			
			index = jdbcTemplate.update(query, arrObj, argTypes);
			logger.info("MDMSubjectCodeDAO.saveMDMSubjectCodeData : Total Rows created : " + index);
			//transactionManager.commit(status);
			if (1 == index) {
				isSuccess = Boolean.TRUE;
				end_Transaction(isSuccess);
	
				mdmSubjectCodeBean.setStatus(KEY_SUCCESS);
				mdmSubjectCodeBean.setMessage("Entries Inserted Successfully");
			} else {
				end_Transaction(Boolean.FALSE);
			}
		} catch (Exception e) {
			//transactionManager.rollback(status);
			end_Transaction(Boolean.FALSE);
			logger.error("MDMSubjectCodeDAO.saveMDMSubjectCodeData : " + e.getMessage());
			throw e;
		}  finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}

	@Transactional(readOnly = true)
	public List<MDMSubjectCodeBean> fetchMDMSubjectCodeList(final int columnCount) {
		String sql = null;
		StringBuilder strBuil = null;
		List<MDMSubjectCodeBean> subjCodeList = null;
		
		strBuil = new StringBuilder();
		strBuil.append(S_SELECT);
		strBuil.append(COL_ID).append(COMMA).append(COL_SUBJECTCODE);
		
		if(columnCount == 1) {
			strBuil.append(COMMA).append(COL_SUBJECTNAME).append(COMMA).append(COL_COMMONSUBJECT).append(COMMA);
			strBuil.append(COL_ACTIVE).append(COMMA).append(" if(isProject = 1, 'Y' , 'N') ").append(COL_IS_PROJECT).append(COMMA);
			//strBuil.append(COL_SIFY_SUBJECTCODE).append(COMMA);
			strBuil.append(COL_SPECIALIZATIONTYPE).append(COMMA);
			strBuil.append(COL_STUDENTTYPE).append(COMMA).append(COL_DESCRIPTION).append(COMMA);
			strBuil.append(COL_SESSIONTIME).append(SPACE);
		}
		strBuil.append(S_FROM).append(TABLE_MDM_SUBJECTCODE);

		sql = strBuil.toString();

		//List<MDMSubjectCodeBean> subjCodeList = (ArrayList<MDMSubjectCodeBean>) jdbcTemplate.query(sql, new Object[] {},
		//		new BeanPropertyRowMapper(MDMSubjectCodeBean.class));
		subjCodeList = jdbcTemplate.execute(sql, new PreparedStatementCallback<List<MDMSubjectCodeBean>>() {

			@Override
			public List<MDMSubjectCodeBean> doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				
				MDMSubjectCodeBean bean = null;
				List<MDMSubjectCodeBean> listSC = null;
				listSC = new ArrayList<MDMSubjectCodeBean>();
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					bean = new MDMSubjectCodeBean();
					bean.setId(rs.getInt(COL_ID));
					bean.setSubjectcode(rs.getString(COL_SUBJECTCODE));
					
					if(columnCount == 1) {
						bean.setSubjectname(rs.getString(COL_SUBJECTNAME));
						bean.setCommonSubject(rs.getString(COL_COMMONSUBJECT));
						bean.setActive(rs.getString(COL_ACTIVE));
						bean.setIsProject(rs.getString(COL_IS_PROJECT));
						bean.setSpecializationType(rs.getString(COL_SPECIALIZATIONTYPE));
						bean.setStudentType(rs.getString(COL_STUDENTTYPE));
						bean.setDescription(rs.getString(COL_DESCRIPTION));
						bean.setSessionTime(Integer.parseInt(rs.getString(COL_SESSIONTIME)));
					}
					
					listSC.add(bean);
				}
				return listSC;
			}
		});
		if (null != subjCodeList) {
			logger.info("MDMSubjectCodeDAO.fetchMDMSubjectCodeList Size :" + subjCodeList.size());
		}
		emptyStringBuilder(strBuil);
		strBuil = null;
		sql = null;
		return subjCodeList;
	}

	@Transactional(readOnly = true)
	public boolean checkIfDuplicateMDMSubjectCode(final String subjectcode) {
		logger.info("MDMSubjectCodeDAO.checkIfDuplicateMDMSubjectCode : " + subjectcode);
		boolean isDuplicate = Boolean.TRUE;
		String sql = null;
		StringBuilder strBuil = null;
		
		strBuil = new StringBuilder();
		strBuil.append(S_SELECT).append(" count(subjectcode) countSubjCode").append(S_FROM).append(TABLE_MDM_SUBJECTCODE);
		//strBuil.append(S_WHERE).append(" subjectcode = '").append(subjectcode).append("'");
		strBuil.append(S_WHERE).append(" subjectcode = ?");
		sql = strBuil.toString();

		//Integer countSubjectCode = jdbcTemplate.queryForObject(sql, Integer.class);
		Integer countSubjectCode = jdbcTemplate.execute(sql, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				int idx = 1;
				arg0.setString(idx, subjectcode);
				
				Integer rowCount = null;
				ResultSet rs = arg0.executeQuery();
				while (rs.next()) {
					rowCount = rs.getInt("countSubjCode");
				}
				return rowCount;
			}
		});
		
		logger.info("MDMSubjectCodeDAO.checkIfDuplicateMDMSubjectCode : " + countSubjectCode);
		if (null != countSubjectCode && countSubjectCode == 0) {
			isDuplicate = Boolean.FALSE;
		}

		emptyStringBuilder(strBuil);
		strBuil = null;
		sql = null;
		return isDuplicate;
	}

	public boolean updateMDMSubjectCodeData(MDMSubjectCodeBean mdmSubjectCodeBean) {

		boolean isSuccess = Boolean.FALSE;
		StringBuffer strBuf = null;
		String query = null;
		//DefaultTransactionDefinition def = null;
		//TransactionStatus status = null;
		Object[] arrObj = null;
		int arraySize = 10;
		int index = 0;
		
		try {
			logger.info("MDMSubjectCodeDAO.updateMDMSubjectCodeData Updating Id: "+mdmSubjectCodeBean.getId());
			arrObj = new Object[arraySize];
			arrObj[index++] = mdmSubjectCodeBean.getSubjectname();
			arrObj[index++] = mdmSubjectCodeBean.getSubjectcode();
			arrObj[index++] = mdmSubjectCodeBean.getCommonSubject();
			arrObj[index++] = mdmSubjectCodeBean.getActive();// 4
			arrObj[index++] = toInteger(mdmSubjectCodeBean.getIsProject());
			//arrObj[index++] = mdmSubjectCodeBean.getSifySubjectCode();//
			arrObj[index++] = mdmSubjectCodeBean.getSpecializationType();// 6
			arrObj[index++] = mdmSubjectCodeBean.getStudentType();// 7
			arrObj[index++] = mdmSubjectCodeBean.getLastModifiedBy();// 8
			arrObj[index++] = mdmSubjectCodeBean.getDescription();
			arrObj[index++] = mdmSubjectCodeBean.getSessionTime();

	
			strBuf = new StringBuffer();
			strBuf.append(S_UPDATE).append(TABLE_MDM_SUBJECTCODE).append(SPACE);
			strBuf.append(S_SET);
			strBuf.append(COL_SUBJECTNAME).append(EQUALTO).append(Q_MARK).append(COMMA);
			strBuf.append(COL_SUBJECTCODE).append(EQUALTO).append(Q_MARK).append(COMMA).append(COL_COMMONSUBJECT)
					.append(EQUALTO).append(Q_MARK).append(COMMA).append(COL_ACTIVE).append(EQUALTO).append(Q_MARK)
					.append(COMMA);
			strBuf.append(COL_IS_PROJECT).append(EQUALTO).append(Q_MARK).append(COMMA);
			//strBuf.append(COL_SIFY_SUBJECTCODE).append(EQUALTO).append(Q_MARK).append(COMMA)
			strBuf.append(COL_SPECIALIZATIONTYPE).append(EQUALTO).append(Q_MARK).append(COMMA);
			strBuf.append(COL_STUDENTTYPE).append(EQUALTO).append(Q_MARK).append(COMMA);
			strBuf.append(COL_LASTMODIFIEDBY).append(EQUALTO).append(Q_MARK).append(COMMA).append(COL_LASTMODIFIEDDATE);
			strBuf.append(EQUALTO).append("CURRENT_TIMESTAMP()").append(COMMA).append(COL_DESCRIPTION).append(EQUALTO).append(Q_MARK).append(COMMA);
			strBuf.append(COL_SESSIONTIME).append(EQUALTO).append(Q_MARK).append(SPACE);
			strBuf.append(S_WHERE);
			strBuf.append(COL_ID).append(EQUALTO);
			strBuf.append(mdmSubjectCodeBean.getId());
			
			query = strBuf.toString();
	
			//def = new DefaultTransactionDefinition();
			//def.setName("UpdateSubjectCode");
			//def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			//status = transactionManager.getTransaction(def);
			start_Transaction_U_PR("UpdateSubjectCode");
	
			jdbcTemplate.update(query, arrObj);
			//transactionManager.commit(status);
			isSuccess = Boolean.TRUE;
			end_Transaction(isSuccess);
			logger.info("MDMSubjectCodeDAO.updateMDMSubjectCodeData : Row updated.");
			
			//isSuccess = Boolean.TRUE;
			mdmSubjectCodeBean.setStatus(KEY_SUCCESS);
			mdmSubjectCodeBean.setMessage("Entries Updated Successfully");
		} catch (Exception e) {
			//transactionManager.rollback(status);
			end_Transaction(Boolean.FALSE);
			logger.error("MDMSubjectCodeDAO.updateMDMSubjectCodeData : " + e.getMessage());
			throw e;
		} finally {
			query = null;
			emptyStringBuffer(strBuf);
			strBuf = null;
		}
		return isSuccess;
	}
	
	public synchronized Integer deleteMDMSubjectCode(final MDMSubjectCodeBean bean) {
		Integer retVal = null;
		try {
			logger.info("MDMSubjectCodeDAO.deleteMDMSubjectCode Deleting Id: "+bean.getId());
			start_Transaction_U_PR("deleteMDMSubjectCode");
			retVal = jdbcTemplate.execute("delete from exam.mdm_subjectcode where id = ?",
					new PreparedStatementCallback<Integer>() {
						@Override
						public Integer doInPreparedStatement(PreparedStatement ps)
								throws SQLException, DataAccessException {
							Integer val = null;
							ps.setInt(1, bean.getId());
							val = ps.executeUpdate();
							return val;
						}
					});
			end_Transaction(Boolean.TRUE);
			bean.setStatus(KEY_SUCCESS);
			bean.setMessage("Total Rows Deleted : " + retVal);
			logger.info("MDMSubjectCodeDAO.deleteMDMSubjectCode Total Rows Deleted : " + retVal);
		} catch (DataAccessException dae) {
			end_Transaction(Boolean.FALSE);
			logger.error("MDMSubjectCodeDAO.deleteMDMSubjectCode : " + dae.getMessage());

			bean.setStatus(KEY_ERROR);
			bean.setMessage(dae.getMessage());
		}

		return retVal;
	}
	
	public static void emptyStringBuilder(StringBuilder strBuil) {
		if(null != strBuil) {
			strBuil.delete(0, strBuil.length() - 1);
		}
	}
	
	public static void emptyStringBuffer(StringBuffer strBuf) {
		if(null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
	
	//MDMSubjectCodeMapping
	public List<ConsumerProgramStructureExam> fetchPrgmStruc_By_ConsumerType(final String consumerTypeId) {
		List<ConsumerProgramStructureExam> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		logger.info("MDMSubjectCodeDAO.fetchPrgmStruc_By_ConsumerType : (ConsuType) : ("+consumerTypeId+")");
		list1 = jdbcTemplate.execute("{ call exam.get_mdm_prgmstruct_by_consumertype(?)}",
				new CallableStatementCallback<List<ConsumerProgramStructureExam>>() {
					@Override
					public List<ConsumerProgramStructureExam> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setInt(1, toInteger(consumerTypeId));
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();

						List<ConsumerProgramStructureExam> listCPS = null;
						ConsumerProgramStructureExam cps = null;
						listCPS = new ArrayList<ConsumerProgramStructureExam>();
						while (rs.next()) {
							cps = new ConsumerProgramStructureExam();
							cps.setProgramStructureId(rs.getString(1));
							cps.setProgram_structure(rs.getString(2));
							listCPS.add(cps);
						}
						return listCPS;
					}
				});
		return list1;
	}

	public List<ConsumerProgramStructureExam> fetchProgram_By_PrgmStruc_ConsumerType(final String prgrmStrucId,
			final String consumerTypeId) {
		List<ConsumerProgramStructureExam> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		logger.info("MDMSubjectCodeDAO.fetchProgram_By_PrgmStruc_ConsumerType : (PrgmStruct, ConsuType) : ("+prgrmStrucId+","+consumerTypeId+")");
		list1 = jdbcTemplate.execute("{ call exam.get_mdm_prgm_by_prgmstruct_consumertype(?,?)}",
				new CallableStatementCallback<List<ConsumerProgramStructureExam>>() {
					@Override
					public List<ConsumerProgramStructureExam> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.setInt(1, toInteger(prgrmStrucId));
						cs.setInt(2, toInteger(consumerTypeId));
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();

						List<ConsumerProgramStructureExam> listCPS = null;
						ConsumerProgramStructureExam cps = null;
						listCPS = new ArrayList<ConsumerProgramStructureExam>();
						while (rs.next()) {
							cps = new ConsumerProgramStructureExam();
							cps.setProgramId(rs.getString(1));
							cps.setCode(rs.getString(2));
							listCPS.add(cps);
						}
						return listCPS;
					}
				});
		return list1;
	}
	
	/*public boolean saveMDMSubjectCodeMappingData(final MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		boolean isSuccess = Boolean.FALSE;
		DefaultTransactionDefinition def = null;
		TransactionStatus status = null;
		logger.info("Save : Consu,PrgmStruc,Prgm,Sem,SubjCode : " + mdmSubjectCodeMappingBean.getConsumerType() + ","
				+ mdmSubjectCodeMappingBean.getPrgmStructApplicable() + "," + mdmSubjectCodeMappingBean.getProgram()
				+ "," + mdmSubjectCodeMappingBean.getSem() + "," + mdmSubjectCodeMappingBean.getSubjectCodeId());
		
			List<SqlParameter> paramList = new ArrayList<SqlParameter>();
			paramList.add(new SqlParameter("consumertypeid", Types.INTEGER));
			paramList.add(new SqlParameter("programstructureid", Types.INTEGER));
			paramList.add(new SqlParameter("programid", Types.INTEGER));
			paramList.add(new SqlParameter("semester", Types.INTEGER));
			paramList.add(new SqlParameter("subjectcodeid", Types.INTEGER));
			paramList.add(new SqlParameter("active", Types.VARCHAR));
			paramList.add(new SqlParameter("createbyuser", Types.VARCHAR));

			def = new DefaultTransactionDefinition();
			def.setName("SaveMDMSubjectCodeMapping");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			status = transactionManager.getTransaction(def);

			Map<String, Object> resultMap = jdbcTemplate.call(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection connection) throws SQLException {

					CallableStatement callableStatement = connection
							.prepareCall("{call save_mdm_subjectcodemapping(?, ?, ?, ?, ?, ?, ?)}");
					callableStatement.setInt(1, toInteger(mdmSubjectCodeMappingBean.getConsumerType()));
					callableStatement.setInt(2, toInteger(mdmSubjectCodeMappingBean.getPrgmStructApplicable()));
					callableStatement.setInt(3, toInteger(mdmSubjectCodeMappingBean.getProgram()));
					callableStatement.setInt(4, toInteger(mdmSubjectCodeMappingBean.getSem()));
					callableStatement.setInt(5, toInteger(mdmSubjectCodeMappingBean.getSubjectCodeId()));
					callableStatement.setString(6, mdmSubjectCodeMappingBean.getActive());
					callableStatement.setString(7, mdmSubjectCodeMappingBean.getCreatedBy());
					callableStatement.execute();
					return callableStatement;
				}
			}, paramList);

			transactionManager.commit(status);
			isSuccess = Boolean.TRUE;
			mdmSubjectCodeMappingBean.setStatus(KEY_SUCCESS);
			mdmSubjectCodeMappingBean.setMessage("Entries Inserted Successfully");
		
		return isSuccess;
	}*/
	
	public boolean saveMDMSubjectCodeMappingData(final MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		boolean isSuccess = Boolean.FALSE;
		//DefaultTransactionDefinition def = null;
		//TransactionStatus status = null;
		CallableStatement csmt = null;
		try {
			logger.info("Save : Consu,PrgmStruc,Prgm,Sem,SubjCode : " + mdmSubjectCodeMappingBean.getConsumerType() + ","
					+ mdmSubjectCodeMappingBean.getPrgmStructApplicable() + "," + mdmSubjectCodeMappingBean.getProgram()
					+ "," + mdmSubjectCodeMappingBean.getSem() + "," + mdmSubjectCodeMappingBean.getSubjectCodeId());
			//def = new DefaultTransactionDefinition();
			//def.setName("SaveMDMSubjectCodeMapping");
			//def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			//status = transactionManager.getTransaction(def);
			start_Transaction_U_PR("SaveMDMSubjectCodeMapping");

			csmt = jdbcTemplate.getDataSource().getConnection().
					 prepareCall("{call exam.save_mdm_subjectcodemapping(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			csmt.setInt(1, toInteger(mdmSubjectCodeMappingBean.getConsumerType()));
			csmt.setInt(2, toInteger(mdmSubjectCodeMappingBean.getPrgmStructApplicable()));
			csmt.setInt(3, toInteger(mdmSubjectCodeMappingBean.getProgram()));
			csmt.setInt(4, toInteger(mdmSubjectCodeMappingBean.getSem()));
			csmt.setInt(5, toInteger(mdmSubjectCodeMappingBean.getSubjectCodeId()));
			csmt.setString(6, mdmSubjectCodeMappingBean.getActive());
			
			csmt.setInt(7, mdmSubjectCodeMappingBean.getPassScore());
			csmt.setString(8, mdmSubjectCodeMappingBean.getHasIA());
			csmt.setString(9, mdmSubjectCodeMappingBean.getHasTest());
			csmt.setString(10, mdmSubjectCodeMappingBean.getHasAssignment());
			csmt.setString(11, mdmSubjectCodeMappingBean.getAssignmentNeededBeforeWritten());
			csmt.setString(12, mdmSubjectCodeMappingBean.getWrittenScoreModel());
			csmt.setString(13, mdmSubjectCodeMappingBean.getAssignmentScoreModel());
			csmt.setString(14, mdmSubjectCodeMappingBean.getCreateCaseForQuery());
			csmt.setString(15, mdmSubjectCodeMappingBean.getAssignQueryToFaculty());
			csmt.setString(16, mdmSubjectCodeMappingBean.getIsGraceApplicable());
			csmt.setInt(17, mdmSubjectCodeMappingBean.getMaxGraceMarks());
			
			csmt.setString(18, mdmSubjectCodeMappingBean.getCreatedBy());
			csmt.setInt(19, mdmSubjectCodeMappingBean.getSifySubjectCode());
			csmt.setDouble(20, mdmSubjectCodeMappingBean.getSubjectCredits());
			csmt.setString(21, mdmSubjectCodeMappingBean.getHasTEE());
			csmt.execute();
			
			//transactionManager.commit(status);
			isSuccess = Boolean.TRUE;
			end_Transaction(isSuccess);
			mdmSubjectCodeMappingBean.setStatus(KEY_SUCCESS);
			mdmSubjectCodeMappingBean.setMessage("Entries Inserted Successfully");
		} catch (SQLException e) {
			//transactionManager.rollback(status);
			end_Transaction(Boolean.FALSE);
			logger.error("MDMSubjectCodeDAO.saveMDMSubjectCodeMappingData : " + e.getMessage());

			mdmSubjectCodeMappingBean.setStatus(KEY_ERROR);
			mdmSubjectCodeMappingBean.setMessage(e.getMessage());// "Entries Not inserted Correctly"
		} catch (Exception e) {
			//transactionManager.rollback(status);
			end_Transaction(Boolean.FALSE);
			throw e;
		}
		return isSuccess;
	}
	
	public synchronized List<MDMSubjectCodeMappingBean> fetchMDMSubjectCodeMappingList() {
		List<MDMSubjectCodeMappingBean> list1 = null;
		
		//Add at start in Stored procedure, after declaring variables, START TRANSACTION READ ONLY;
		//Add at end in Stored procedure, COMMIT;
		
		list1 = jdbcTemplate.execute("{call exam.getall_mdm_subjectcodemapping()}",
				new CallableStatementCallback<List<MDMSubjectCodeMappingBean>>() {
					@Override
					public List<MDMSubjectCodeMappingBean> doInCallableStatement(CallableStatement cs)
							throws SQLException, DataAccessException {
						cs.executeUpdate();
						ResultSet rs = cs.getResultSet();
						
						//Integer serialNumber = 1;
						List<MDMSubjectCodeMappingBean> scmList = null;
						MDMSubjectCodeMappingBean bean = null;
						scmList = new ArrayList<MDMSubjectCodeMappingBean>();
						while (rs.next()) {
							bean = new MDMSubjectCodeMappingBean();
							bean.setConsumerProgramStructureId(rs.getString("consumerProgramStructureId"));
							bean.setActive(rs.getString("active"));
							bean.setSem(rs.getString("sem"));
							bean.setSubjectCodeId(rs.getString("subjectCodeId"));
							bean.setSubjectCode(rs.getString("subjectCode"));
							bean.setSubjectName(rs.getString("subjectName"));//for display purpose
							bean.setConsumerTypeId(rs.getString("consumerTypeId"));
							bean.setConsumerType(rs.getString("consumerType"));
							bean.setProgramStructureId(rs.getString("programStructureId"));
							bean.setPrgmStructApplicable(rs.getString("prgmStructApplicable"));
							bean.setProgramId(rs.getString("programId"));
							bean.setProgram(rs.getString("program"));
							bean.setProgramFullName(rs.getString("programName"));
							
							bean.setSifySubjectCode(rs.getInt("sifySubjectCode"));
							bean.setPassScore(rs.getInt("passScore"));
							bean.setHasIA(rs.getString("hasIA"));
							bean.setHasTest(rs.getString("hasTest"));
							bean.setHasAssignment(rs.getString("hasAssignment"));
							bean.setHasTEE(rs.getString("hasTEE"));
							bean.setAssignmentNeededBeforeWritten(rs.getString("assignmentNeededBeforeWritten"));
							bean.setAssignmentScoreModel(rs.getString("assignmentScoreModel"));
							bean.setWrittenScoreModel(rs.getString("writtenScoreModel"));
							bean.setCreateCaseForQuery(rs.getString("createCaseForQuery"));
							bean.setAssignQueryToFaculty(rs.getString("assignQueryToFaculty"));
							bean.setIsGraceApplicable(rs.getString("isGraceApplicable"));
							bean.setMaxGraceMarks(rs.getInt("maxGraceMarks"));
							bean.setSubjectCredits(rs.getDouble("subjectCredits"));
							
							bean.setId(rs.getInt("id"));
							//serialNumber = serialNumber + 1;
							scmList.add(bean);
						}
						return scmList;
					}
				});
		return list1;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public synchronized Integer deleteMDMSubjectCodeMapping(final MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		Integer rowsAffected = -1;
		
		try {
			logger.info("MDMSubjectCodeDAO.deleteMDMSubjectCodeMapping  Deleting: (Id) ("+mdmSubjectCodeMappingBean.getId()+ ")");
			rowsAffected = jdbcTemplate.execute("{call exam.delete_mdm_subjectcodemapping(?)}",
					new CallableStatementCallback<Integer>() {
						@Override
						public Integer doInCallableStatement(CallableStatement cs)
								throws SQLException, DataAccessException {
							/*cs.setInt(1, toInteger(mdmSubjectCodeMappingBean.getSem()));
							cs.setInt(2, toInteger(mdmSubjectCodeMappingBean.getSubjectCodeId()));
							cs.setInt(3, toInteger(mdmSubjectCodeMappingBean.getConsumerProgramStructureId()));*/
							
							cs.setInt(1, mdmSubjectCodeMappingBean.getId());
							Integer rowCount = cs.executeUpdate();
							return rowCount;
						}
					});
			mdmSubjectCodeMappingBean.setStatus(KEY_SUCCESS);
			mdmSubjectCodeMappingBean.setMessage("Total Rows Deleted : "+ rowsAffected);
			logger.info("MDMSubjectCodeDAO.deleteMDMSubjectCodeMapping Total Rows Deleted : " + rowsAffected);
		} catch (DataAccessException dae) {
			logger.error("MDMSubjectCodeDAO.deleteMDMSubjectCodeMapping : " + dae.getMessage());

			mdmSubjectCodeMappingBean.setStatus(KEY_ERROR);
			mdmSubjectCodeMappingBean.setMessage(dae.getMessage());
		}
		return rowsAffected;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public synchronized Integer updateMDMSubjectCodeMapping(final MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		Integer rowsAffected = -1;
		
		try {
			logger.info("MDMSubjectCodeDAO.updateMDMSubjectCodeMapping  Updating: (Id) ("+mdmSubjectCodeMappingBean.getId()+")");
			rowsAffected = jdbcTemplate.execute("{call exam.update_mdm_subjectcodemapping(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}",
					new CallableStatementCallback<Integer>() {
						@Override
						public Integer doInCallableStatement(CallableStatement cs)
								throws SQLException, DataAccessException {
							/*cs.setInt(1, toInteger(oldSemester));
							cs.setInt(2, toInteger(oldSubjectCodeId));
							cs.setInt(3, toInteger(oldConsumerProgramStructureId));*/
							cs.setInt(1, mdmSubjectCodeMappingBean.getId());
							cs.setString(2, mdmSubjectCodeMappingBean.getActive());
							
							cs.setInt(3, mdmSubjectCodeMappingBean.getPassScore());
							cs.setString(4, mdmSubjectCodeMappingBean.getHasIA());
							cs.setString(5, mdmSubjectCodeMappingBean.getHasTest());
							cs.setString(6, mdmSubjectCodeMappingBean.getHasAssignment());
							cs.setString(7, mdmSubjectCodeMappingBean.getAssignmentNeededBeforeWritten());
							cs.setString(8, mdmSubjectCodeMappingBean.getWrittenScoreModel());
							cs.setString(9, mdmSubjectCodeMappingBean.getAssignmentScoreModel());
							cs.setString(10, mdmSubjectCodeMappingBean.getCreateCaseForQuery());
							cs.setString(11, mdmSubjectCodeMappingBean.getAssignQueryToFaculty());
							cs.setString(12, mdmSubjectCodeMappingBean.getIsGraceApplicable());
							cs.setInt(13, mdmSubjectCodeMappingBean.getMaxGraceMarks());
							cs.setString(14, mdmSubjectCodeMappingBean.getLastModifiedBy());
							cs.setInt(15, mdmSubjectCodeMappingBean.getSifySubjectCode());
							cs.setDouble(16, mdmSubjectCodeMappingBean.getSubjectCredits());
							cs.setString(17, mdmSubjectCodeMappingBean.getHasTEE());

							Integer rowCount = cs.executeUpdate();
							return rowCount;
						}
					});
			mdmSubjectCodeMappingBean.setStatus(KEY_SUCCESS);
			mdmSubjectCodeMappingBean.setMessage("Total Rows Updated : "+ rowsAffected);
			logger.info("MDMSubjectCodeDAO.updateMDMSubjectCodeMapping Total Rows Updated : " + rowsAffected);
		} catch (DataAccessException dae) {
			logger.error("MDMSubjectCodeDAO.updateMDMSubjectCodeMapping : " + dae.getMessage());

			mdmSubjectCodeMappingBean.setStatus(KEY_ERROR);
			mdmSubjectCodeMappingBean.setMessage(dae.getMessage());
		}
		return rowsAffected;
	}
	
	//-------//
	/*
	CREATE TABLE `mdm_subjectcode` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `subjectname` varchar(100) NOT NULL,
	  `subjectcode` varchar(60) NOT NULL,
	  `commonSubject` varchar(10) DEFAULT NULL,
	  `specializationType` varchar(45) DEFAULT NULL,
	  `studentType` varchar(45) DEFAULT NULL,
	  `isProject` tinyint(4) NOT NULL,
	  `active` varchar(2) NOT NULL,
	  `createdBy` varchar(45) DEFAULT NULL,
	  `createdDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  `lastModifiedBy` varchar(45) DEFAULT NULL,
	  `lastModifiedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  `description` varchar(500) DEFAULT NULL,
	  PRIMARY KEY (`id`),
	  UNIQUE KEY `subjectcode` (`subjectcode`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/

	/*CREATE TABLE `mdm_subjectcode_mapping` (
	  `id` int(11) NOT NULL AUTO_INCREMENT,
	  `sem` tinyint(4) NOT NULL,
	  `subjectCodeId` int(11) NOT NULL,
	  `consumerProgramStructureId` int(11) NOT NULL,
	  `active` varchar(1) NOT NULL,
	  `sifySubjectCode` int(11) NOT NULL DEFAULT '0',
	  `passScore` smallint(6) NOT NULL DEFAULT '50',
	  `hasIA` varchar(8) DEFAULT NULL,
	  `hasTest` varchar(8) NOT NULL DEFAULT 'N',
	  `hasAssignment` varchar(8) NOT NULL DEFAULT 'Y',
	  `assignmentNeededBeforeWritten` varchar(16) NOT NULL DEFAULT 'N',
	  `writtenScoreModel` varchar(24) NOT NULL DEFAULT 'Latest',
	  `assignmentScoreModel` varchar(24) NOT NULL DEFAULT 'Best',
	  `createCaseForQuery` varchar(8) NOT NULL DEFAULT 'N',
	  `assignQueryToFaculty` varchar(8) NOT NULL DEFAULT 'Y',
	  `isGraceApplicable` varchar(8) NOT NULL DEFAULT 'Y',
	  `maxGraceMarks` smallint(6) NOT NULL DEFAULT '2',
	  `createdBy` varchar(45) DEFAULT NULL,
	  `createdDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  `lastModifiedBy` varchar(45) DEFAULT NULL,
	  `lastModifiedDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (`id`),
	  UNIQUE KEY `subjectCodeIdCPSIdSem` (`subjectCodeId`,`consumerProgramStructureId`,`sem`),
	  KEY `fk_mdm_sc` (`subjectCodeId`),
	  KEY `fk_mdm_subjectcode_mapping_cps` (`consumerProgramStructureId`),
	  CONSTRAINT `fk_mdm_sc` FOREIGN KEY (`subjectCodeId`) REFERENCES `mdm_subjectcode` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
	  CONSTRAINT `fk_mdm_subjectcode_mapping_cps` FOREIGN KEY (`consumerProgramStructureId`) REFERENCES `consumer_program_structure` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
	) ENGINE=InnoDB DEFAULT CHARSET=utf8;
	*/
	//-------//
	
	//-------//
	/*CREATE 
	    ALGORITHM = UNDEFINED 
	    DEFINER = `root`@`localhost` 
	    SQL SECURITY DEFINER
	VIEW `vw_mdm_consumerprogramstructure` AS
    select 
        `cps`.`id` AS `id`,
        `cps`.`programStructureId` AS `programStructureId`,
        `cps`.`programId` AS `programId`,
        `cps`.`consumerTypeId` AS `consumerTypeId`,
        `ps`.`program_structure` AS `program_structure`,
        `p`.`code` AS `programCode`,
        `p`.`name` AS `programName`,
        `ct`.`name` AS `consumerName`
    from
        (((`consumer_program_structure` `cps`
        join `program_structure` `ps` ON ((`cps`.`programStructureId` = `ps`.`id`)))
        join `program` `p` ON ((`cps`.`programId` = `p`.`id`)))
        join `consumer_type` `ct` ON ((`cps`.`consumerTypeId` = `ct`.`id`)))
    order by `ct`.`id` , `p`.`id` , `ps`.`id`;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `get_mdm_prgmstruct_by_consumertype`(
	IN consuTypeId int(11))
	BEGIN

	select distinct ps.id as 'programstructureid', ps.program_structure as 'programstructure'
	from exam.consumer_program_structure cps
	inner join exam.program_structure ps on ps.id = cps.programStructureId
	where cps.consumerTypeId = consuTypeId;

	END$$
	DELIMITER;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `get_mdm_prgm_by_prgmstruct_consumertype`(
	IN prgmStrucId int(11),
	IN consuTypeId int(11))
	BEGIN

	select distinct p.id as 'programid', p.code as 'programcode' 
	from exam.consumer_program_structure cps
	inner join exam.program p on p.id = cps.programId
	where cps.consumerTypeId = consuTypeId and cps.programStructureId = prgmStrucId;

	END$$
	DELIMITER;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `save_mdm_subjectcodemapping`(
	IN consutypeid INT(11),IN programstrucid INT(11),IN progid INT(11),
	IN semester INT(11),IN subjectcodeid INT(11),IN active VARCHAR(2),
	IN pascore SMALLINT(6),IN hasia VARCHAR(8),IN hastest VARCHAR(8),IN hasassign VARCHAR(8),
	IN assignneeded VARCHAR(16),IN writscoremodel VARCHAR(24),IN assiscoremodel VARCHAR(24),
	IN createcaseforque VARCHAR(8),
	IN assignqueryfac VARCHAR(8),IN isgraceapp VARCHAR(8),IN maxgracemarks SMALLINT,
	IN createbyuser VARCHAR(45),IN sifySubjCode INT(11))
	BEGIN
	
	declare cpsid int(11);
	
	select id
	from `exam`.`consumer_program_structure`
	where consumerTypeId = consutypeid and
	programStructureId = programstrucid and
	programId = progid
	into cpsid;
	
	INSERT INTO `exam`.`mdm_subjectcode_mapping`
	(`sem`,`subjectCodeId`,`consumerProgramStructureId`,
	`active`,`sifySubjectCode`,
	`passScore`,`hasIA`,`hasTest`,`hasAssignment`,
	`assignmentNeededBeforeWritten`, `writtenScoreModel`,`assignmentScoreModel`,
	`createCaseForQuery`, `assignQueryToFaculty`, `isGraceApplicable`, `maxGraceMarks`,
	`createdBy`,`createdDate`,
	`lastModifiedBy`,`lastModifiedDate`)
	VALUES
	(semester,subjectcodeid,cpsid,
	active,sifySubjCode,
	pascore, hasia, hastest, hasassign,
	assignneeded, writscoremodel, assiscoremodel, 
	createcaseforque, assignqueryfac, isgraceapp, maxgracemarks,
	createbyuser, CURRENT_TIMESTAMP(),
	createbyuser, CURRENT_TIMESTAMP());
	
	END$$
	DELIMITER;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `getall_mdm_subjectcodemapping`()
	BEGIN
	
	select scm.id, scm.consumerProgramStructureId, scm.active, scm.sem, 
	scm.subjectCodeId, scm.passScore, scm.sifySubjectCode, scm.hasIA, scm.hasTest, scm.hasAssignment,
	scm.assignmentNeededBeforeWritten, scm.writtenScoreModel, scm.assignmentScoreModel,
	scm.createCaseForQuery, scm.assignQueryToFaculty, scm.isGraceApplicable, scm.maxGraceMarks,
	sc.subjectcode as 'subjectCode',sc.subjectname as 'subjectName',
	vw.consumerTypeId, vw.consumerName as 'consumerType', 
	vw.programStructureId, vw.program_structure as 'prgmStructApplicable',
	vw.programId, vw.programCode  as 'program'
	from `exam`.`mdm_subjectcode_mapping` scm 
	join `exam`.`mdm_subjectcode` sc on scm.subjectCodeId = sc.id
	join exam.vw_mdm_consumerprogramstructure vw on scm.consumerProgramStructureId  = vw.id
	order by scm.lastModifiedDate asc;
	
	END$$
	DELIMITER;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `delete_mdm_subjectcodemapping`(
	IN mappId int(11))
	BEGIN*/
	/*declare prgmstruc varchar(160);
	declare prgmcode varchar(160);
	declare subjname varchar(160);
	declare consprgmstrid int(11);
	declare semester int(11);
	
	select
	sc.subjectname,scm.sem,scm.consumerProgramStructureId,
	pst.program_structure,ps.code
	from exam.mdm_subjectcode_mapping scm
	join exam.mdm_subjectcode sc on sc.id = scm.subjectCodeId
	join consumer_program_structure cps on scm.consumerProgramStructureId = cps.id
	join exam.program_structure pst on pst.id = cps.programStructureId
	join exam.program ps on ps.id = cps.programId
	join exam.consumer_type ct on ct.id = cps.consumerTypeId
	where scm.id = mappId
	into subjname,semester,consprgmstrid,prgmstruc,prgmcode;
	
	update exam.program_sem_subject set active ='ZZ', 
	description = concat(description, ' ', 'deleted for mdmSCM ',mappId)
	where sem = semester and subject = subjname and consumerProgramStructureId = consprgmstrid;
	
	update exam.program_subject set active ='ZZ'
	where sem = semester and subject = subjname 
	and prgmStructApplicable = prgmstruc and program = prgmcode;*/
	
	/*delete from exam.mdm_subjectcode_mapping
	where id = mappId;
	
	END$$
	DELIMITER;
	*/
	//-------//

	//-------//
	/*DELIMITER $$
	CREATE DEFINER=`root`@`localhost` PROCEDURE `update_mdm_subjectcodemapping`(
	IN mappId int(11),IN actve varchar(2),
	IN pascore SMALLINT(6),IN hia VARCHAR(8),IN htest VARCHAR(8),IN hassign VARCHAR(8),
	IN assignneeded VARCHAR(16),IN writscoremodel VARCHAR(24),IN assiscoremodel VARCHAR(24),
	IN createcaseforque VARCHAR(8),
	IN assignqueryfac VARCHAR(8),IN isgraceapp VARCHAR(8),IN mgracemarks SMALLINT,
	IN lastModBy VARCHAR(45),IN sifySubjCode INT(11)
	)
	BEGIN
	
	update exam.mdm_subjectcode_mapping 
	set active = actve, sifySubjectCode = sifySubjCode,
	passScore = pascore, hasIA = hia, hasTest = htest, hasAssignment = hassign,
	assignmentNeededBeforeWritten = assignneeded,writtenScoreModel = writscoremodel,
	assignmentScoreModel = assiscoremodel, createCaseForQuery = createcaseforque, 
	assignQueryToFaculty = assignqueryfac, isGraceApplicable = isgraceapp, maxGraceMarks = mgracemarks,
	lastModifiedBy = lastModBy, lastModifiedDate = CURRENT_TIMESTAMP()
	where id = mappId;
	
	END$$
	DELIMITER;
	*/
	//-------//

	public int deleteProgramSubject(String programStructure,String program,String sem,String subject) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "delete from exam.program_subject where program = ? and sem = ? and subject = ? and prgmStructApplicable = ? ";
		return jdbcTemplate.update(SQL,new Object[] {program,sem,subject,programStructure});
	}
	

	public int deleteProgramSemSubject(Integer id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "delete from exam.program_sem_subject where id = ? ";
		return jdbcTemplate.update(SQL,new Object[] {id});
	}
	
	
	public int deleteInMdm(Integer id) {
		jdbcTemplate=new JdbcTemplate(dataSource);
		String SQL = "delete from exam.mdm_subjectcode_mapping where id = ? ";
		return jdbcTemplate.update(SQL,new Object[] {id});
	}
}