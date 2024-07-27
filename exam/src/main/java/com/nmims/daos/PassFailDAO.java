package com.nmims.daos;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.helpers.PaginationHelper;


public class PassFailDAO extends BaseDAO implements PassFailDAOInterface {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private HashMap<String, Integer> hashMap = null;
	
	String assignmentAfterWrittenMesage = "Assignment not present OR Assignment is submitted after written exam";
	String lessThan50Mesage = "Less than passing Score/Absent";
	String lessThan40Mesage = "Less than 40 marks/Absent";
	String subjectCutoffNotCleared = "Inidividual Subject Score Less than Cutoff";
	String ANS_Messge = "Result on Hold due to Non Submission of Assignment";
	String ANS_BAJAJ_Messge = "Result on Hold due to Non Submission of Assignment";
	String ANS_ACBM_Messge = "Result on Hold due to Non Submission of Assignment";
	String writtenAttemptMissing = "TEE attempt missing";
	String averageCutoffNotCleared = "Average cutoff not cleared";
	String allSubjectsNotAttempted = "All Subjects of sem not attempted";
	final String BYSAPID = "BYSAPID";
	final String BYGRNO = "BYGRNO";
	final String SAPID_DB = "sapid";
	final String SUBJECT_DB = "subject";

	private static final Logger logger = LoggerFactory.getLogger(PassFailDAO.class);
	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		setBaseDataSource();
		super.getLiveFlagDetails(true);
	}

	@Override
	public void setBaseDataSource() {
		this.baseDataSource = this.dataSource;
	}

	@Transactional(readOnly = true)//
	public boolean  hasAppearedForExamForGivenSemMonthYear(PassFailExamBean passFail, String examMode){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from exam.marks m, exam.examorder eo  "
				+ " where sapId = ?"
				+ " and m.year = eo.year "
				+ " and m.month = eo.month "
				+ " and m.month = ? "
				+ " and m.year = ? "
				+ " and m.sem = ? ";
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{passFail.getSapid(),passFail.getWrittenMonth(),passFail.getWrittenYear(),passFail.getSem()},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	// hasAppearedForExamForGivenSemMonthYearForExecutive Start
	@Transactional(readOnly = true)//
	public boolean  hasAppearedForExamForGivenSemMonthYearForExecutive(PassFailExamBean passFail){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from exam.marks m, exam.executive_examorder eo  "
				+ " where sapId = ?"
				+ " and m.year = eo.year "
				+ " and m.month = eo.month "
				+ " and m.month = ? "
				+ " and m.year = ? "
				+ " and m.sem = ? ";
		        //+ " and eo.order <= (select max(eeo.order) from exam.executive_examorder eeo where eeo.resultLive = 'Y')" discuss with sir 23may PS
		int count =0;
		 try {
			count = (int) jdbcTemplate.queryForObject(sql, new Object[]{passFail.getSapid(),passFail.getWrittenMonth(),passFail.getWrittenYear(),passFail.getSem()},Integer.class);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	// hasAppearedForExamForGivenSemMonthYearForExecutive End

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailProcessingBYSAPID(StudentExamBean searchBean){

		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.marks e where sapid in "
				+ " (Select sapid from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid = e.sapid and subject = e.subject and sapid <> 'Not Available')";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		//Query non processed records for BAJAJ( prev. ACBM).
		//IMP: Do not change order by clause in below query, as Pass Fail Processing depends on the same.
		StringBuffer sql = new StringBuffer("select * from exam.marks e "
				+ " inner join "//subQuery for inner join starts below
				+ " ( "
				+ "	select distinct m.sapid, subject, s.consumerType, r.program, ps.program_structure AS PrgmStructApplicable  "
				+ "		from exam.marks m, exam.students s "
				+ ", exam.registration r " 
				+ ", exam.consumer_program_structure cps " + 
				  ", exam.program_structure ps " 
				+ "		where (processed <> 'Y' or processed is null) "
				+ " 	and m.sapid <> 'Not Available' "
				+ " 	and s.sapid = m.sapid "
				+ " 	and s.sapid = r.sapid " 
				+ " 	and m.sapid = r.sapid " 
				+ " 	and r.sem = m.sem " 
				+ "     AND cps.id = r.consumerProgramStructureId "
				+ "     AND ps.id = cps.programStructureId "
				+ " AND (s.isLateral = 'N' AND s.programChanged IS NULL)");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and m.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and ps.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    
		sql.append("	) sp "//subQuery for inner join ends here
				+ " On "
				+ " e.sapid = sp.sapid "
				+ " AND "
				+ " e.subject = sp.subject "
				/* + " AND e.program <> 'ACBM' " */
				+ "	AND ( "
				+ " sp.consumerType <> 'BAJAJ' OR ( sp.PrgmStructApplicable = 'Jul2014' and sp.program = 'DBM' ) "
				+ " ) "
				+ " order by e.examorder * 1 asc");
		
		Object [] args = parameters.toArray();
		List<StudentMarksBean> studentMarksList =  jdbcTemplate.query(sql.toString(),args, new BeanPropertyRowMapper(StudentMarksBean.class));
		
		studentMarksList.addAll(getLateralPendingRecordsForPassFailProcessing(searchBean));
		
		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();
		if(studentMarksList != null){
		}
		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}
	
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getLateralPendingRecordsForPassFailProcessing(StudentExamBean searchBean){

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		StringBuffer sql = new StringBuffer("select * from exam.marks e "
				+ " inner join "//subQuery for inner join starts below
				+ " ( "
				+ "	select distinct m.sapid, subject, s.consumerType, s.program, s.PrgmStructApplicable  "
				+ "		from exam.marks m, exam.students s "
				+ "		where (processed <> 'Y' or processed is null) "
				+ " 	and m.sapid <> 'Not Available' "
				+ " 	and s.sapid = m.sapid "
				+ "  AND (s.isLateral = 'Y' OR s.programChanged = 'Y') ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and m.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    
		sql.append("	) sp "//subQuery for inner join ends here
				+ " On "
				+ " e.sapid = sp.sapid "
				+ " AND "
				+ " e.subject = sp.subject "
				/* + " AND e.program <> 'ACBM' " */
				+ "	AND ( "
				+ " sp.consumerType <> 'BAJAJ' OR ( sp.PrgmStructApplicable = 'Jul2014' and sp.program = 'DBM' ) "
				+ " ) "
				+ " order by e.examorder * 1 asc");
		
		Object [] args = parameters.toArray();
		List<StudentMarksBean> studentMarksBean =  jdbcTemplate.query(sql.toString(),args, new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class));
		return studentMarksBean;
	}


	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailProcessingForValidityEnd(StudentExamBean searchBean){

		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.marks e where sapid in "
				+ " (Select sapid from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid = e.sapid and subject = e.subject and sapid <> 'Not Available')";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		//Query non processed records for BAJAJ(prev. ACBM).
		//IMP: Do not change order by clause in below query, as Pass Fail Processing depends on the same.
		StringBuffer sql = new StringBuffer("select * from exam.marks e "
				+ " inner join "//subQuery for inner join starts below
				+ " ( "
				+ "	select distinct m.sapid, subject, s.consumerType, s.program, s.PrgmStructApplicable  "
				+ "		from exam.marks m, exam.students s, exam.registration r "
				+ "		where (processed <> 'Y' or processed is null) "
				+ " 	and m.sapid <> 'Not Available' "
				+ " 	and s.sapid = m.sapid "
				+ " 	and s.sapid = r.sapid "
				+ " 	and m.sapid = r.sapid "
				+ " 	and r.sem = m.sem "
				+ "");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and r.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    
		sql.append("	) sp "//subQuery for inner join ends here
				+ " On "
				+ " e.sapid = sp.sapid "
				+ " AND "
				+ " e.subject = sp.subject "
				/* + " AND e.program <> 'ACBM' " */
				+ "	AND ("
				+ " sp.consumerType <> 'BAJAJ' OR ( sp.PrgmStructApplicable = 'Jul2014' AND sp.program = 'DBM' ) "
				+ ") "
				+ " order by e.examorder * 1 asc");
		
		Object [] args = parameters.toArray();
		
		List<StudentMarksBean> studentMarksList =  jdbcTemplate.query(sql.toString(),args, new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}	

	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailForBajaj(StudentExamBean searchBean){
		// previously getPendingRecordsForPassFailForACBM
		jdbcTemplate = new JdbcTemplate(dataSource);



		//Query change made on 13/5/2017 since if a single subject was being attempted in a particular sem//
		/*String sql = " select e.* from exam.marks e "
				+ " inner join "
				+ " (select distinct sapid, subject, sem from exam.marks where (processed <> 'Y' or processed is null) "
				+ " and program = 'ACBM' ) sp "
				+ " On e.sapid = sp.sapid AND e.sem = sp.sem "
				+ " AND e.program = 'ACBM'  "
				+ " group by id "
				+ " order by e.examorder * 1 asc ";*/

		/*String sql = "select * from exam.marks where program = 'ACBM' and (processed <> 'Y' or processed is null) and concat(sapid, sem) in "
				+ " (Select concat(sapid, sem) from exam.marks where program = 'ACBM' ) "
				+ " order by examorder * 1 asc";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		
		//Query non processed records for BAJAJ(prev. ACBM).
		//IMP: Do not change order by clause in below query, as Pass Fail Processing depends on the same.
		StringBuffer sql = new StringBuffer("select * from exam.marks e "
				+ " inner join "//subQuery for inner join starts below
				+ " ( "
				+ "	select distinct m.sapid, subject, s.consumerType, s.program, s.PrgmStructApplicable  "
				+ "		from exam.marks m, exam.students s, exam.registration r "
				+ "		where (processed <> 'Y' or processed is null) "
				+ " 	and m.sapid <> 'Not Available' "
				+ " 	and s.sapid = m.sapid "
				+ " 	and s.sapid = r.sapid "
				+ " 	and m.sapid = r.sapid "
				+ " 	and r.sem = m.sem "
				/* + " 	and m.program = 'ACBM'" */
				+ " 	and ( "
							/* Only Bajaj */
					+ "		s.consumerType = 'BAJAJ'"
							/* Exclude DBM, include ACBM */
					+ " 	and not ( s.PrgmStructApplicable = 'Jul2014' and s.program = 'DBM' ) "
					+ " )"
				+ "");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and r.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    
		sql.append("	) sp "//subQuery for inner join ends here
				+ " On "
				+ " e.sapid = sp.sapid "
				+ " AND "
				+ " e.subject = sp.subject "
				/* + " AND e.program = 'ACBM'  " */
				+ "	AND ( "
					+ " sp.consumerType = 'BAJAJ' "
					+ " AND NOT ( sp.PrgmStructApplicable = 'Jul2014' AND sp.program = 'DBM' )"
				+ " ) "
				+ " group by id "
				+ " order by e.examorder * 1 asc");
		
		Object [] args = parameters.toArray();
		List<StudentMarksBean> studentMarksList = (List<StudentMarksBean>)jdbcTemplate.query(sql.toString(),args, new BeanPropertyRowMapper(StudentMarksBean.class));
		if(studentMarksList != null){

			for (StudentMarksBean studentMarksBean : studentMarksList) {
			}
		}
		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}	

	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailForBajajForASem(PassFailExamBean studentMarks) {
		// previously getPendingRecordsForPassFailForACBMForASem
		
		String sql = "select * from exam.marks where sapid = ? and sem = ? "
				+ " order by examorder * 1 asc";
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, new Object[]{studentMarks.getSapid(), studentMarks.getSem()},
				new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;
	}


	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getMarksRecordsForStudent(String sem, String sapId, String month, String year){

		jdbcTemplate = new JdbcTemplate(dataSource);

//
//		String sql = "select m.* from exam.marks m, exam.examorder eo "
//				+ " where m.sem = ? and m.sapid = ? and m.month = eo.month and m.year = eo.year "
//				+ " and eo.order <= (Select examorder.order from exam.examorder where month = ? and year = ? )";
//		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
//				new Object[]{sem, sapId, month, year},
//				new BeanPropertyRowMapper(StudentMarksBean.class));
		
		String sql = ""
				+ " SELECT `m`.*, `s`.`consumerType`, `ps`.`program_structure` AS `PrgmStructApplicable` "
				+ " FROM `exam`.`marks` `m` "
				+ " INNER JOIN ( "
					+ " SELECT * FROM `exam`.`examorder` `eo` "
					+ " WHERE `eo`.`order` <= ( "
						+ " SELECT `examorder`.`order` "
						+ " FROM `exam`.`examorder` "
						+ " WHERE "
							+ " `month` = ? "
						+ " AND `year` = ? "
					+ " ) "
				+ " ) `eo` "
				+ " ON `m`.`month` = `eo`.`month` and `m`.`year` = eo.year "
				
				+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `m`.`sapid` "
				+"INNER JOIN `exam`.`registration` `r` ON `r`.`sapid`=`s`.`sapid` AND `r`.`sem`=`m`.`sem` "
				+"INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id`=`r`.`consumerProgramStructureId` "
				+"INNER JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " WHERE "
				+ " m.sem = ? and m.sapid = ? AND s.isLateral='N' ";
		
		String forLateralSql = ""
				+ " SELECT `m`.*, `s`.`consumerType`, `ps`.`program_structure` AS `PrgmStructApplicable` "
				+ " FROM `exam`.`marks` `m` "
				+ " INNER JOIN ( "
					+ " SELECT * FROM `exam`.`examorder` `eo` "
					+ " WHERE `eo`.`order` <= ( "
						+ " SELECT `examorder`.`order` "
						+ " FROM `exam`.`examorder` "
						+ " WHERE "
							+ " `month` = ? "
						+ " AND `year` = ? "
					+ " ) "
				+ " ) `eo` "
				+ " ON `m`.`month` = `eo`.`month` and `m`.`year` = eo.year "
				
				+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `m`.`sapid` "
				+"INNER JOIN `exam`.`consumer_program_structure` `cps` ON `cps`.`id`=`s`.`consumerProgramStructureId` "
				+"INNER JOIN `exam`.`program_structure` `ps` ON `ps`.`id` = `cps`.`programStructureId` "
				+ " WHERE "
				+ " m.sem = ? and m.sapid = ? AND s.isLateral='Y' "; 
		
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
			new Object[]{
					month, year, 
					sem, sapId 
			},
			new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class)
		);
		
		
		List<StudentMarksBean> studentMarksListforLateral = jdbcTemplate.query(forLateralSql, 
				new Object[]{
						month, year, 
						sem, sapId 
				},
				new BeanPropertyRowMapper<StudentMarksBean>(StudentMarksBean.class)
			);
		
		studentMarksList.addAll(studentMarksListforLateral);
		
		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();
		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}
	
	//Start
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getMarksRecordsForStudentForExecutive(String sem, String sapId, String month, String year){

		jdbcTemplate = new JdbcTemplate(dataSource);


		String sql = "select m.* from exam.marks m, exam.executive_examorder eo, exam.executive_exam_bookings b,exam.students s "
				+ " where m.sem = ? and m.sapid = ? and m.month = eo.month and m.year = eo.year "
				+ " and b.sem = m.sem and b.sapid = m.sapid and m.month = b.month and m.year = b.year and m.subject = b.subject "
				+ " and s.sapid = m.sapid "
				+ " and s.sem = (select max(sem) from exam.students where sapid = s.sapid) "
				+ " and eo.acadMonth = s.enrollmentMonth and eo.acadYear = s.enrollmentYear "
				+ " and eo.order <= (Select eeo.order from exam.executive_examorder eeo where eeo.month = ? and eeo.year = ? and eeo.acadMonth = s.enrollmentMonth and eeo.acadYear = s.enrollmentYear  )"; //discuss with sir 23may ps

		HashMap<String, ArrayList> keysMap = new HashMap<>();

		try {
			List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
					new Object[]{sem, sapId, month, year},
					new BeanPropertyRowMapper(StudentMarksBean.class));

			List<StudentMarksBean> uniqueStudentList = new ArrayList<>();

			for(int i = 0 ; i < studentMarksList.size(); i++){
				StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
				String key = bean.getSapid().trim()+bean.getSubject().trim();
				if(!keysMap.containsKey(key)){
					ArrayList<StudentMarksBean> list = new ArrayList<>();
					list.add(bean);
					keysMap.put(key, list);
					uniqueStudentList.add(bean);
				}else{
					ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
					list.add(bean);
					keysMap.put(key, list);
				}
				
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		return keysMap;

	}
	//End

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getMarksRecordsForStudentForASubject(PassFailExamBean student){

		jdbcTemplate = new JdbcTemplate(dataSource);


		String sql = "select m.* from exam.marks m "
				+ " where  m.sapid = ? and m.subject = ? ";

		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
				new Object[]{ student.getSapid(), student.getSubject()},
				new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}	

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getStudentsEligibleForGrace(PassFailExamBean bean){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from exam.passfail pf,exam.students st,exam.program_subject ps where "
				+ " ps.isGraceApplicable = 'Y' and "
				+ " (pf.total >= ps.passScore - ps.maxGraceMarks and total < ps.passScore ) and"
				+ "  ("
				+ "	  (pf.resultProcessedYear = ? and pf.resultProcessedMonth = ?)"
				+ "  	or "
				+ "	  (pf.assignmentYear = ? and pf.assignmentMonth = ?) "
				+ "	 ) "
				/* + "  AND st.program <> 'ACBM' " */
				+ "  AND ("
					+ " st.consumerType <> 'BAJAJ' "
					+ " OR ( st.PrgmStructApplicable = 'Jul2014' and st.program = 'DBM' ) "
				+ " ) "
				+ "  and st.program = ps.program "
				+ "  and st.sapid = pf.sapid "
				+ " and ps.subject = pf.subject "
				+ " and st.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and ps.isGraceApplicable = 'Y' ";

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{bean.getWrittenYear(), bean.getWrittenMonth(),bean.getWrittenYear(), bean.getWrittenMonth()}, new BeanPropertyRowMapper(PassFailExamBean.class));

		return studentsMarksList;

	}
	
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getStudentsEligibleForGraceFromStaging(String writtenYear, String writtenMonth){
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select * from exam.passfail_staging pf,exam.students st,exam.program_subject ps where "
				+ " ps.isGraceApplicable = 'Y' and "
				+ " (pf.total >= ps.passScore - ps.maxGraceMarks and total < ps.passScore ) and"
				+ "  ("
				+ "	  (pf.resultProcessedYear = ? and pf.resultProcessedMonth = ?)"
				+ "  	or "
				+ "	  (pf.assignmentYear = ? and pf.assignmentMonth = ?) "
				+ "	 ) "
				/* + "  AND st.program <> 'ACBM' " */
				+ "  AND ("
				+ " st.consumerType <> 'BAJAJ' "
				+ " OR ( st.PrgmStructApplicable = 'Jul2014' and st.program = 'DBM' ) "
				+ " ) "
				+ "  and st.program = ps.program "
				+ "  and st.sapid = pf.sapid "
				+ " and ps.subject = pf.subject "
				+ " and st.PrgmStructApplicable = ps.prgmStructApplicable "
				+ " and ps.isGraceApplicable = 'Y' ";
		
		return jdbcTemplate.query(sql, new Object[]{writtenYear, writtenMonth,writtenYear, writtenMonth},
				new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
		
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getBajajStudentsEligibleForGrace(PassFailExamBean bean){
		/* previously getACBMStudentsEligibleForGrace */
		jdbcTemplate = new JdbcTemplate(dataSource);

//		String sql = "select * from exam.passfail where "
//				+ " (total = 38 OR total = 39 ) and"
//				+ "  ((resultProcessedYear = ? and resultProcessedMonth = ?)"
//				+ "  or (assignmentYear = ? and assignmentMonth = ?)) "
//				+ " AND program = 'ACBM' ";
		
		String sql = " SELECT * FROM `exam`.`passfail` `pf` "
				+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `pf`.`sapid` "
				+ " WHERE "
				+ " (`pf`.`total` = 38 OR `pf`.`total` = 39 ) "
				+ " AND "
				+ " ( "
					+ " ("
							+ " `pf`.`resultProcessedYear` = ? "
						+ " AND `pf`.`resultProcessedMonth` = ?"
					+ " ) "
					+ " OR "
					+ " ( "
							+ " `pf`.`assignmentYear` = ? "
						+ " AND `pf`.`assignmentMonth` = ?"
					+ " ) "
				+ " ) "
				+ " AND ( "
					+ " `s`.`consumerType` = 'BAJAJ' "
					+ " AND NOT ( s.PrgmStructApplicable = 'Jul2014' and s.program = 'DBM' ) "
				+ " ) ";

		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{bean.getWrittenYear(), bean.getWrittenMonth(),bean.getWrittenYear(), bean.getWrittenMonth()}, new BeanPropertyRowMapper(PassFailExamBean.class));

		return studentsMarksList;

	}
	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getBajajStudentsEligibleForGraceFromStaging(String writtenYear, String writtenMonth){
		/* previously getACBMStudentsEligibleForGrace */
		jdbcTemplate = new JdbcTemplate(dataSource);
		
//		String sql = "select * from exam.passfail where "
//				+ " (total = 38 OR total = 39 ) and"
//				+ "  ((resultProcessedYear = ? and resultProcessedMonth = ?)"
//				+ "  or (assignmentYear = ? and assignmentMonth = ?)) "
//				+ " AND program = 'ACBM' ";
		
		String sql = " SELECT * FROM `exam`.`passfail` `pf` "
				+ " INNER JOIN `exam`.`students` `s` "
				+ " ON `s`.`sapid` = `pf`.`sapid` "
				+ " WHERE "
				+ " (`pf`.`total` = 38 OR `pf`.`total` = 39 ) "
				+ " AND "
				+ " ( "
				+ " ("
				+ " `pf`.`resultProcessedYear` = ? "
				+ " AND `pf`.`resultProcessedMonth` = ?"
				+ " ) "
				+ " OR "
				+ " ( "
				+ " `pf`.`assignmentYear` = ? "
				+ " AND `pf`.`assignmentMonth` = ?"
				+ " ) "
				+ " ) "
				+ " AND ( "
				+ " `s`.`consumerType` = 'BAJAJ' "
				+ " AND NOT ( s.PrgmStructApplicable = 'Jul2014' and s.program = 'DBM' ) "
				+ " ) ";
		
		return jdbcTemplate.query(sql, new Object[]{writtenYear, writtenMonth,writtenYear, writtenMonth},
				new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
		
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailProcessingBYGRNO(){

		jdbcTemplate = new JdbcTemplate(dataSource);

		/*String sql = "select * from exam.marks e where grno in "
				+ " (Select grno  from exam.marks where (processed <> 'Y' OR processed is null) "
				+ "and grno = e.grno and subject = e.subject and sapid = 'Not Available')";*/

		String sql = "select * from exam.marks e "
				+ " inner join "
				+ " (select distinct grno, subject from exam.marks where (processed <> 'Y' or processed is null) and sapid = 'Not Available') as sp "
				+ " On "
				+ " e.grno = sp.grno "
				+ " AND "
				+ " e.subject = sp.subject order by e.examorder * 1 asc";

		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getGrno().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

		//return studentMarksList;
	}	

	@Transactional(readOnly = true)//
	private boolean checkIfRecordExists(PassFailExamBean bean, JdbcTemplate jdbcTemplate) {
		int count = 0;
		try{
			if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
				return false;
			}

			String sql = "SELECT count(*) FROM exam.passfail where sapid = ? and subject = ?";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
					bean.getSapid(),
					bean.getSubject()
			},Integer.class);


		}catch(Exception e){
			
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}

	}

	@Transactional(readOnly = true)//
	public int getPendingRecordsCountBySAPid(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "select count(*) from exam.marks e where sapid in "
				+ " (Select sapid from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid = e.sapid and subject = e.subject and sapid <> 'Not Available')";*/

		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21 
		StringBuffer sql = new StringBuffer("select count(m.id) from exam.marks m, exam.students s "
											//+ ", exam.registration r "
											+ "where (processed <> 'Y' OR processed is null) "
											+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720 
											+ " and m.sapid <> 'Not Available' "
											+ " and s.sapid = m.sapid "
											+ " and s.sem = (select max(sem) from exam.students where sapid = s.sapid) ");
											//+ " and s.sapid = r.sapid "
											//+ " and m.sapid = r.sapid "
											//+ " and r.sem = m.sem ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and m.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		int count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);

		return count;

	}

	@Transactional(readOnly = true)//
	public int getPendingRecordsCountByGRNO() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		/*String sql = "select count(*) from exam.marks e where grno in "
				+ " (Select grno  from exam.marks where (processed <> 'Y' OR processed is null)"
				+ " and grno = e.grno and subject = e.subject and sapid = 'Not Available')";*/

		String sql = "select count(id) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid = 'Not Available'";//just count(*) replaced with count(id) by Vilpesh on 2021-09-21
		int count = (int) jdbcTemplate.queryForObject(sql,new Object[]{},Integer.class);

		return count;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ArrayList<String> batchUpdateOldData(final List<StudentMarksBean> marksBeanList) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		int i = 0;
		ArrayList<String> errorList = new ArrayList<>();


		for (i = 0; i < marksBeanList.size(); i++) {
			try{
				StudentMarksBean bean = marksBeanList.get(i);
				//insertStudentMarksForUpsert(bean, jdbcTemplate);
			}catch(Exception e){
				
				errorList.add(i+"");
				//return i;
			}
		}
		return errorList;

	}

	@Transactional(readOnly = true)//
	public void isValidEntry(StudentMarksBean bean, JdbcTemplate jdbcTemplate){
		String programsql = "Select count(*) from programs where program = ? ";
		String subjectSql = "Select count(*) from subjects where subjectname = ? ";

		int validProgramCount = (int) jdbcTemplate.queryForObject(programsql, new Object[]{bean.getProgram()},Integer.class);
		int validSubjectCount = (int) jdbcTemplate.queryForObject(subjectSql, new Object[]{bean.getSubject()},Integer.class);

		ArrayList<String> errorMessageList = new ArrayList<>();
		if(validProgramCount == 0){
			errorMessageList.add("Invalid Program "+bean.getProgram()+" entered for Student "+bean.getSapid()+ " under subject "+bean.getSubject());
		}
	}

	@Transactional(readOnly = true)//
	public ArrayList<String> getAllPrograms() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT distinct program FROM exam.programs order by program asc";

		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		ArrayList<String> programList = (ArrayList<String>) jdbcTemplate.query(sql, new SingleColumnRowMapper(String.class));
		return programList;

	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)//
	private void updateProcessedState(PassFailExamBean m, JdbcTemplate jdbcTemplate) {
		//In tr 
		String sql = "Update exam.marks set "
				+ " processed='Y'"
				+ " where sapid = ? and subject = ?";


		jdbcTemplate.update(sql, new Object[] { 
				m.getSapid(),
				m.getSubject()
		});

	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)//
	private void updateProcessedStateBatchGRNO(final List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate) {

		final int batchSize = 1000;
		//lastModifiedDate - added by Vilpesh on 2021-12-18
		String sql = "Update exam.marks set "
				+ " processed='Y'"
				+ " ,lastModifiedDate = CURRENT_TIMESTAMP"
				+ " where sapid = 'Not Available' and grno = ? and subject = ?  ";


		for (int j = 0; j < studentList.size(); j += batchSize) {

			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);

			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					PassFailExamBean m = batchList.get(i);
					ps.setString(1, m.getGrno());
					ps.setString(2, m.getSubject());
				}
				public int getBatchSize() {
					return batchList.size();
				}


			});
		}


	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)//
	private void updateProcessedStateBatchSAPID(final List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate) {

		final int batchSize = 1000;
		//lastModifiedDate - added by Vilpesh on 2021-12-18
		String sql = "Update exam.marks set "
				+ " processed='Y'"
				+ " ,lastModifiedDate = CURRENT_TIMESTAMP"
				+ " where sapid = ? and subject = ?";


		for (int j = 0; j < studentList.size(); j += batchSize) {

			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);

			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i)	throws SQLException {
					PassFailExamBean m = batchList.get(i);
					ps.setString(1, m.getSapid());
					ps.setString(2, m.getSubject());
				}
				public int getBatchSize() {
					return batchList.size();
				}


			});
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertPassRecord(PassFailExamBean m, JdbcTemplate jdbcTemplate){
		try{
			final String sql = " INSERT INTO exam.passfail "
					+ "(sapid,"
					+ "subject,"
					+ "grno,"
					+ "writtenYear,"
					+ "writtenMonth,"
					+ "assignmentYear,"
					+ "assignmentMonth,"
					+ "name,"
					+ "program,"
					+ "sem,"
					+ "writtenscore,"
					+ "assignmentscore,"
					+ "total,"
					+ "failReason,"
					+ "remarks,"
					+ "isPass)"

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			jdbcTemplate.update(sql, new Object[] { 
					m.getSapid(),
					m.getSubject(),
					m.getGrno(),
					m.getWrittenYear(),
					m.getWrittenMonth(),
					m.getAssignmentYear(),
					m.getAssignmentMonth(),
					m.getName(),
					m.getProgram(),
					m.getSem(),
					m.getWrittenscore(),
					m.getAssignmentscore(),
					m.getTotal(),
					m.getFailReason(),
					m.getRemarks(),
					m.getIsPass()
			});

			updateProcessedState(m, jdbcTemplate);
		}catch(Exception e){
			
		}
	}

	@Transactional(readOnly = true)//
	public HashMap<String, StudentExamBean> getAllStudents() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select * from exam.students ";
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentExamBean.class));

		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();
		for (StudentExamBean student : students) {
			studentsMap.put(student.getSapid(), student);
		}

		return studentsMap;
	}
	
	@Transactional(readOnly = true)//
	public Map<String, StudentExamBean> getProgramDetailsFromStudents() {
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT `sapid`, `program`, `PrgmStructApplicable`, `consumerType` FROM `exam`.`students`";
		
		List<StudentExamBean>  studentExamBeans  = jdbcTemplate.query(sql, new BeanPropertyRowMapper<StudentExamBean>(StudentExamBean.class));
		
		return studentExamBeans.stream().collect(Collectors.toMap(StudentExamBean::getSapid, k -> k, (a , b) -> b));
	}

	//Old logic of pass fail. Should not be used.
	public ArrayList<PassFailExamBean> process(HashMap<String, ArrayList> keysMap) {

		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<PassFailExamBean>();
		//ArrayList<PassFailBean> passStudentList = new ArrayList<PassFailBean>();
		int passCount = 0;
		int failCount = 0;
		Iterator entries = keysMap.entrySet().iterator();
		//keysMap.size();

		HashMap<String, StudentExamBean> studentsMap = getAllStudents();
		//while (entries.hasNext()) {
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();

			PassFailExamBean passFailBean = new PassFailExamBean();
			StudentMarksBean currentBean = currentList.get(0);
			String sapId = currentBean.getSapid();
			StudentExamBean student = studentsMap.get(sapId);
			String programSructure = student.getPrgmStructApplicable();

			transferDataInPassBean(passFailBean, currentBean);

			boolean isLastAttemptvalid = isLastAttemptWithAssignment(currentList, passFailBean);
			//boolean hasOnlyTotalMarks = hasOnlyTotalMarks(currentList);
			if((!isLastAttemptvalid)){
				passFailBean.setIsPass("N");
				passFailBean.setFailReason(ANS_Messge);
				passFailBean.setTotal("");
				passFailStudentList.add(passFailBean);
				failCount++;
				continue;
			}

			boolean isAssgntBeforeWritten = false;
			//if(!"Jul2014".equalsIgnoreCase(programSructure) && !("Jul2013".equals(programSructure))){
			if("Offline".equals(student.getExamMode())){
				//Check if written attempt is after assignment, for Offline exam student
				isAssgntBeforeWritten = isAssignmentBeforeWritten(currentList, passFailBean);
			}else{
				double maxExamOrder = getMaxExamOrderForCurrentSubject(currentList);
				if(maxExamOrder > 15){
					//Last Attempt after Dec-2015
					isAssgntBeforeWritten = true; //Assignment can be given after written as well from Apr-2016, so need to check
				}else{
					//Last Attempt before Apr-2016, i.e. till Dec-2015
					//Check if written attempt is after assignment, for old pass fail cases before Apr-2016
					isAssgntBeforeWritten = isAssignmentBeforeWritten(currentList, passFailBean);
				}

			}

			if(!isAssgntBeforeWritten){
				//Check if it is old data which has only total marks, after last assignment attempt
				boolean hasAdditionalRecordWithOnlyTotal = hasAdditionalRecordWithOnlyTotal(currentList, passFailBean.getAssignmentAttemptOrder());

				if(hasAdditionalRecordWithOnlyTotal){
					passFailBean.setAssignmentscore("");
					passFailBean.setWrittenscore("");
					int totalMarks = getLatestTotalMarks(currentList,passFailBean);
					if(totalMarks >= 50){
						passFailBean.setTotal(totalMarks+"");
						passFailBean.setIsPass("Y");
						passFailStudentList.add(passFailBean);
						passCount++;
					}else{
						passFailBean.setTotal(totalMarks+"");
						passFailBean.setFailReason(lessThan50Mesage);
						passFailBean.setIsPass("N");
						passFailStudentList.add(passFailBean);
						failCount++;
					}
				}else{
					//Even if assignment is after written, display his best assignment score
					int bestAssignmentScore = calculateBestAssignmentScore(currentList,passFailBean);
					passFailBean.setTotal(passFailBean.getAssignmentscore());
					passFailBean.setIsPass("N");
					if("AB".equals(passFailBean.getWrittenscore()) || "#CC".equals(passFailBean.getWrittenscore()) 
							|| "RIA".equals(passFailBean.getWrittenscore()) || "NV".equals(passFailBean.getWrittenscore()) ){
						//Don't make written score blank
					}else{
						passFailBean.setWrittenscore("");
					}

					passFailBean.setFailReason(assignmentAfterWrittenMesage);
					passFailStudentList.add(passFailBean);
					failCount++;
				}
			}else{
				//Since written attempt is after assignment, get best of assignment and latest of written marks
				int bestAssignmentScore = calculateBestAssignmentScore(currentList,passFailBean);
				int latestWrittenScore = getLatestWrittenScore(currentList, passFailBean);
				//Check if it is old data that has no assignment and written marks, and has only total marks
				boolean hasOnlyTotalMarks = hasOnlyTotalMarks(currentList);
				if(bestAssignmentScore + latestWrittenScore >= 50){
					passFailBean.setIsPass("Y");
					passFailBean.setWrittenscore(latestWrittenScore+"");
					if("Project".equals(passFailBean.getSubject()) || "Module 4 - Project".equals(passFailBean.getSubject())){
						passFailBean.setAssignmentscore("");
					}else{
						passFailBean.setAssignmentscore(bestAssignmentScore+"");
					}

					passFailBean.setTotal(bestAssignmentScore+latestWrittenScore+"");
					passFailStudentList.add(passFailBean);
					passCount++;
				}else if(hasOnlyTotalMarks){

					int latestTotalMarks = getLatestTotalMarks(currentList,passFailBean);
					passFailBean.setAssignmentscore("");
					passFailBean.setWrittenscore("");
					passFailBean.setTotal(latestTotalMarks+"");
					if(latestTotalMarks >= 50){

						passFailBean.setIsPass("Y");
						passFailStudentList.add(passFailBean);
						passCount++;
					}else{
						passFailBean.setFailReason(lessThan50Mesage);
						passFailBean.setIsPass("N");
						passFailStudentList.add(passFailBean);
						failCount++;
					}

				}else{
					//Check if it is old data, that has additional record with only total marks
					boolean hasAdditionalRecordWithOnlyTotal = hasAdditionalRecordWithOnlyTotal(currentList, passFailBean.getWrittenAttemptOrder());
					if(hasAdditionalRecordWithOnlyTotal){
						int totalMarks = getLatestTotalMarks(currentList,passFailBean);
						passFailBean.setWrittenscore("");
						passFailBean.setAssignmentscore("");
						passFailBean.setTotal(totalMarks+"");
						if(totalMarks >= 50){
							passFailBean.setIsPass("Y");
							passFailStudentList.add(passFailBean);
							passCount++;
						}else{
							passFailBean.setFailReason(lessThan50Mesage);
							passFailBean.setIsPass("N");
							passFailStudentList.add(passFailBean);
							failCount++;
						}
					}else{

						if("Project".equals(passFailBean.getSubject()) || "Module 4 - Project".equals(passFailBean.getSubject())){
							passFailBean.setAssignmentscore("");
							//No need to set written score, its already set in getLatestWrittenScore()
						}else{
							//passFailBean.setWrittenscore(latestWrittenScore+"");
							passFailBean.setAssignmentscore(bestAssignmentScore+"");
						}
						passFailBean.setTotal(bestAssignmentScore+latestWrittenScore+"");
						passFailBean.setFailReason(lessThan50Mesage);
						passFailBean.setIsPass("N");
						passFailStudentList.add(passFailBean);
						failCount++;
					}
				}
			}
		}
		return passFailStudentList;

	}

	@Transactional(readOnly = true)//
	public HashMap<String,Integer> getProgramSubjectPassScoreMap(Map<String,ProgramSubjectMappingExamBean> configurationMap){
		//jdbcTemplate = new JdbcTemplate(dataSource);
		//String sql =" Select * from exam.program_subject ";

		//ArrayList<ProgramSubjectMappingExamBean> lstProgramSubject = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		String key = null;
		Set<Entry<String, ProgramSubjectMappingExamBean>> setEntry = null;
		HashMap<String, Integer> programSubjectPassScoreMap = new HashMap<>();
		if (null != configurationMap && configurationMap.size() > 0) {
			passFailLogger.info("getProgramSubjectPassScoreMap : " + configurationMap.size());
			setEntry = configurationMap.entrySet();
			for (Entry<String, ProgramSubjectMappingExamBean> entry : setEntry) {
				key = entry.getKey(); // bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
				programSubjectPassScoreMap.put(key, entry.getValue().getPassScore());
			}
		}
		return programSubjectPassScoreMap;
	}

	@Deprecated
	@Transactional(readOnly = true)//
	public HashMap<String,Integer> getProgramSubjectPassScoreMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" Select * from exam.program_subject ";

		ArrayList<ProgramSubjectMappingExamBean> lstProgramSubject = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));
		HashMap<String,Integer> programSubjectPassScoreMap = new HashMap<>();
		if(lstProgramSubject.size() > 0){
			for(ProgramSubjectMappingExamBean bean : lstProgramSubject){
				String key = bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
				programSubjectPassScoreMap.put(key, bean.getPassScore());
			}
		}

		return programSubjectPassScoreMap;
	}

	@Transactional(readOnly = true)//
	public HashMap<String,ProgramSubjectMappingExamBean> getProgramSubjectPassingConfigurationMap(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql =" Select * from exam.program_subject ";

		ArrayList<ProgramSubjectMappingExamBean> lstProgramSubject = (ArrayList<ProgramSubjectMappingExamBean>)jdbcTemplate.query(sql, new BeanPropertyRowMapper(ProgramSubjectMappingExamBean.class));

		return lstProgramSubject.stream().collect(Collectors
				.toMap(k -> k.getProgram() + "-" + k.getSubject() + "-" + k.getPrgmStructApplicable(),
						k -> k,
						(a, b) -> a, HashMap::new));
	}

	//Pass Fail logic for general students.
	public ArrayList<PassFailExamBean> processNew(HashMap<String, ArrayList> keysMap) {

		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<PassFailExamBean>();
		//ArrayList<PassFailBean> passStudentList = new ArrayList<PassFailBean>();
		int passCount = 0;
		int failCount = 0;
		int minPassScoreRequired = 0;

		Iterator entries = keysMap.entrySet().iterator();

		// create Program Subject PassScore map
		//HashMap<String,Integer> programSubjectPassScoreMap = getProgramSubjectPassScoreMap();//shifted down by Vilpesh on 2022-05-23 
		HashMap<String,ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap = getProgramSubjectPassingConfigurationMap();

		//Equivalent code by Vilpesh on 2022-05-23
		HashMap<String,Integer> programSubjectPassScoreMap = null;
		programSubjectPassScoreMap = this.getProgramSubjectPassScoreMap(programSubjectPassingConfigurationMap);
		
		//Code pulled out by Vilpesh on 2022-05-24 
		ArrayList<String> softSkillSubjects = new ArrayList<String>(Arrays.asList("Soft Skills for Managers",
				"Employability Skills - II Tally", "Start your Start up", "Design Thinking"));
		
		/*
		 * Algorithm (Old Algorithm Below, All configured in DB now):
		1. Check if Assignment is given, if not given then ANS, use latest written score and put result on hold
		2. If Assignment is given, then check if assignment is given before written for Offline cases and cases till Dec-2015. If not then fail
		3. If passed above step, then take best of assignment and latest of written and decide pass or fail based on total
		 */

		HashMap<String, StudentExamBean> studentsMap = getAllStudents();
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();
			//PassFailExamBean passFailBean = new PassFailExamBean();//shifted down by Vilpesh on 2021-12-17
			StudentMarksBean currentBean = currentList.get(0);
			String sapId = currentBean.getSapid();
			StudentExamBean student = studentsMap.get(sapId);
			String prgmStructApplicable = currentBean.getPrgmStructApplicable() != null ? currentBean.getPrgmStructApplicable() : student.getPrgmStructApplicable();	
			String programSubjectProgramStructureKey = currentBean.getProgram()+"-"+currentBean.getSubject()+"-"+ prgmStructApplicable;
			ProgramSubjectMappingExamBean passingConfiguration = programSubjectPassingConfigurationMap.get(programSubjectProgramStructureKey);
			//minPassScoreRequired = programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
			 passFailLogger.info("START Temporary changes inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject()+" programSubjectProgramStructureKey "+programSubjectProgramStructureKey);
				
			//Temporary changes, remove later
			if(programSubjectPassScoreMap.get(programSubjectProgramStructureKey) == null) {
				continue;
			}else {
				minPassScoreRequired = programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
			}
			 passFailLogger.info("END Temporary changes inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject()+" programSubjectProgramStructureKey "+programSubjectProgramStructureKey);
				
			PassFailExamBean passFailBean = new PassFailExamBean();
			//Temporary changes, end
			transferDataInPassBean(passFailBean, currentBean);
			int assignmentScore = 0;
			int termEndScore = 0;
			passFailLogger.info("START executeAssignmentRelatedPassFailLogic inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject());
			
			if("Y".equalsIgnoreCase(passingConfiguration.getHasAssignment()) && "Y".equalsIgnoreCase(passingConfiguration.getHasIA()) ){
				boolean processFurther = executeAssignmentRelatedPassFailLogic(currentList, passFailBean, student, passFailStudentList, passingConfiguration);
				if(!processFurther){
					//There could be scenarios where TEE logic need not be executed, if certain assignment conditions are not met
					continue;
				}
			}
			if(StringUtils.isNumeric(passFailBean.getAssignmentscore())){
				assignmentScore = Integer.parseInt(passFailBean.getAssignmentscore());
			}
			passFailLogger.info("END executeAssignmentRelatedPassFailLogic inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject());
			
			passFailLogger.info("START executeTEERelatedPassFailLogic  inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject());
			
			executeTEERelatedPassFailLogic(currentList, passFailBean, passingConfiguration);
			passFailLogger.info("END executeTEERelatedPassFailLogic inside for loop block  "+k+"/"+keysMap.size()+" Sapid "+currentBean.getSapid()+" Subject "+currentBean.getSubject());
			
			if(StringUtils.isNumeric(passFailBean.getWrittenscore())){
				termEndScore = Integer.parseInt(passFailBean.getWrittenscore());
			}
			
			int grace = calculateGraceMarksGiven(currentList, passFailBean, passingConfiguration);
			
			//Code pulled out so commented by Vilpesh on 2022-05-24
			//ArrayList<String> softSkillSubjects= new ArrayList<String>(Arrays.asList("Soft Skills for Managers","Employability Skills - II Tally","Start your Start up","Design Thinking"));
			
			if(softSkillSubjects.contains(currentBean.getSubject()) 
					 && (assignmentScore + termEndScore + grace < 15) ) {
				 //do not show this subject
			} else if(assignmentScore + termEndScore + grace >= minPassScoreRequired){//Pass
				
				passFailBean.setTotal(assignmentScore+termEndScore+"");
				passFailBean.setIsPass("Y");
//				passFailStudentList.add(passFailBean);
				passCount++;
			}else{//Fail
				passFailBean.setTotal(assignmentScore+termEndScore+"");
				passFailBean.setFailReason(lessThan50Mesage);
				passFailBean.setIsPass("N");
//				passFailStudentList.add(passFailBean);
				failCount++;
			}
		
			/*
			 * added by Abhay
			 * Purpose: To not pass students marked as copy-case in assignments
			*/
			try {
				 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM");  
					//Date writtenDate = sdf.parse(passFailBean.getWrittenYear()+"-"+passFailBean.getWrittenMonth());  
					//Date assignmentDate = sdf.parse(passFailBean.getAssignmentYear()+"-"+passFailBean.getAssignmentMonth());  
					Date date = sdf.parse("2022-Apr");
					List<StudentMarksBean>  reversedList = currentList.stream()
							.sorted(Comparator.comparing(StudentMarksBean::getExamorder).reversed())
							.collect(Collectors.toList());
					Date yearMonthDate = sdf.parse(reversedList.get(0).getYear()+"-"+reversedList.get(0).getMonth());  
					
//					if(writtenDate.equals(date) || writtenDate.after(date) || assignmentDate.equals(date) || assignmentDate.after(date) ) {	
					if(yearMonthDate.equals(date) || yearMonthDate.after(date) ) {	
						 if("Retail".equals(student.getConsumerType()) ){	
								checkifAnyAssignmentCopyCaseMark(reversedList, passFailBean);
						 }
					}
			}catch (Exception e) {
				// TODO: handle exception
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				passFailLogger.error(" Error Occur  "+errors);
			}
			 passFailLogger.info("processNew inside for loop block  "+k+"/"+keysMap.size());
			 passFailStudentList.add(passFailBean);
		}
		
		//Unused, so commented by Vilpesh on 2022-05-24 
		//if(passFailStudentList != null) {
		//}
		
		//code by Vilpesh on 2022-05-24 
		if(null != softSkillSubjects) {
			softSkillSubjects.clear();
			softSkillSubjects = null;
		}
		if(null != studentsMap) {
			studentsMap.clear();
			studentsMap = null;
		}
		
		return passFailStudentList;
	}
	
	public int calculateGraceMarksGiven(ArrayList<StudentMarksBean> currentList, PassFailExamBean passFailBean,
			ProgramSubjectMappingExamBean passingConfiguration) {
		
		if("Y".equalsIgnoreCase(passingConfiguration.getIsGraceApplicable())) {
			for (StudentMarksBean bean: currentList) {
				String grace = bean.getGracemarks();
				if(StringUtils.isNotBlank(grace)) {
					passFailBean.setGracemarks(grace);
					passFailBean.setGraceGiven("Y");
					return Integer.parseInt(grace);
				}
			}
		}
		return 0;
	}

	// processNewForExecutive Start
	public ArrayList<PassFailExamBean> processNewForExecutive(HashMap<String, ArrayList> keysMap) {

		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<PassFailExamBean>();
		int minPassScoreRequired = 0;

		Iterator entries = keysMap.entrySet().iterator();

		// create Program Subject PassScore map
		HashMap<String,Integer> programSubjectPassScoreMap = getProgramSubjectPassScoreMap();
		HashMap<String,ProgramSubjectMappingExamBean> programSubjectPassingConfigurationMap = getProgramSubjectPassingConfigurationMap();
		

		HashMap<String, StudentExamBean> studentsMap = getAllStudents(); //discuss with sir 23may PS
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();

			PassFailExamBean passFailBean = new PassFailExamBean();
			StudentMarksBean currentBean = currentList.get(0);
			String sapId = currentBean.getSapid();
			StudentExamBean student = studentsMap.get(sapId);

			String programSubjectProgramStructureKey = student.getProgram()+"-"+currentBean.getSubject()+"-"+student.getPrgmStructApplicable();
			ProgramSubjectMappingExamBean passingConfiguration = programSubjectPassingConfigurationMap.get(programSubjectProgramStructureKey);
			minPassScoreRequired = programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
			transferDataInPassBean(passFailBean, currentBean);
			int termEndScore = 0;
			
			if("Y".equalsIgnoreCase(passingConfiguration.getHasAssignment()) && "Y".equalsIgnoreCase(passingConfiguration.getHasIA()) ){
				boolean processFurther = executeAssignmentRelatedPassFailLogic(currentList, passFailBean, student, passFailStudentList, passingConfiguration);
				if(!processFurther){
					//There could be scenarios where TEE logic need not be executed, if certain assignment conditions are not met
					continue;
				}
			}

			for (StudentMarksBean b :currentList) {
			}
			executeTEERelatedPassFailLogic(currentList, passFailBean, passingConfiguration);

			int passCount = 0;
			int failCount = 0;
			if(StringUtils.isNumeric(passFailBean.getWrittenscore())){
				termEndScore = Integer.parseInt(passFailBean.getWrittenscore());
			
			if( termEndScore >= minPassScoreRequired){//Pass
				passFailBean.setIsPass("Y");
				passFailBean.setTotal(termEndScore+"");
				passFailStudentList.add(passFailBean);
				passCount++;
			}else{//Fail
				passFailBean.setTotal(termEndScore+"");
				passFailBean.setFailReason(lessThan50Mesage);
				passFailBean.setIsPass("N");
				passFailStudentList.add(passFailBean);
				failCount++;
			}
			}else {//Is AB
				passFailBean.setWrittenscore(passFailBean.getWrittenscore()+"");
				passFailBean.setTotal(passFailBean.getWrittenscore()+"");
				passFailBean.setFailReason(lessThan50Mesage);
				passFailBean.setIsPass("N");
				passFailStudentList.add(passFailBean);
				failCount++;
				
			}
		}
		return passFailStudentList;

	}
	// processNewForExecutive End


	public void executeTEERelatedPassFailLogic(ArrayList<StudentMarksBean> currentList, PassFailExamBean passFailBean,
			ProgramSubjectMappingExamBean passingConfiguration) {
		//String writtenScoreModel = passingConfiguration.getWrittenScoreModel();
		String writtenScoreModel = getWrittenPolicyApplicable(currentList,passingConfiguration);
		if("Latest".equalsIgnoreCase(writtenScoreModel)){
			getLatestWrittenScore(currentList, passFailBean);
		}else if("Best".equalsIgnoreCase(writtenScoreModel)){
			getBestWrittenScore(currentList, passFailBean);
		}else {
		}
	}
	
	private String getWrittenPolicyApplicable(ArrayList<StudentMarksBean> currentList,ProgramSubjectMappingExamBean passingConfiguration) {

		String latestYear="";
		String latestMonth="";
		String writtenScoreModel = passingConfiguration.getWrittenScoreModel();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		Date latesMarkstDate = null; 
		Date tempDate = null;
		int i = 0;
		for(StudentMarksBean bean: currentList) {

			 latestYear = bean.getYear();
			 latestMonth = bean.getMonth();
			try {
				if(i==0) {
					latesMarkstDate = formatter.parse("01-"+latestMonth+"-"+latestYear);
				} else {
					tempDate = formatter.parse("01-"+latestMonth+"-"+latestYear);
					if(tempDate.after(latesMarkstDate)) {
						latesMarkstDate = tempDate;
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
			}
			i++;
		}
	    try {
			Date dateToCheck = formatter.parse("01-Dec-2018");

			if(dateToCheck.after(latesMarkstDate)) { // give true if 01Dec18 comes after latest date of marks 
				return "Latest";
			}else {
				return writtenScoreModel;
			}
	    } catch (ParseException e) {
			// TODO Auto-generated catch block
			
			return e.getMessage();
		}
	}
	

	private int getBestWrittenScore(ArrayList<StudentMarksBean> currentStudentList,
								    PassFailExamBean passFailBean) {
		
		int latestWrittenScore = 0;
		int bestWrittenScore = 0;
		passFailBean.setWrittenAttemptOrder(0);
		//boolean scoreFound = false;//Not used, commented by Vilpesh on 2021-12-17
		
		String hasValidAttemptAfter_RIA_NV = "";
				
		String tempRecord = "";
		String tempYear="";
		String tempMonth="";
		double tempExamOrder = 0;
		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);
			String finalExamScore = bean.getWritenscore();
			String subject = bean.getSubject();
			double examOrder = Double.parseDouble(bean.getExamorder());
			try {
				if(finalExamScore == null || "".equals(finalExamScore.trim())){	
					finalExamScore = "";
					tempYear = bean.getYear();
					tempMonth = bean.getMonth();
					tempExamOrder = examOrder;
					//passFailBean.setWrittenAttemptOrder(examOrder);
					continue;
				}
				if("AB".equalsIgnoreCase(finalExamScore.trim())) {
					tempRecord = finalExamScore.trim();
					tempYear = bean.getYear();
					tempMonth = bean.getMonth();
					tempExamOrder = examOrder;
					continue;
				}
				
				// added to check RIA NV logic 28-jan-19 start
				if("NV".equalsIgnoreCase(finalExamScore.trim()) || "RIA".equalsIgnoreCase(finalExamScore.trim())) {
					tempRecord = finalExamScore.trim();
					
					hasValidAttemptAfter_RIA_NV = checkIfHasValidAttemptAfter_RIA_NV(currentStudentList);
					
					if("false".equalsIgnoreCase(hasValidAttemptAfter_RIA_NV)) {
						//break after setting written score as "NV" as NV will be carry forwarded in passfail record if there is no valid record after nv.
						passFailBean.setWrittenYear(bean.getYear());
						passFailBean.setWrittenMonth(bean.getMonth());
						passFailBean.setWrittenAttemptOrder(examOrder);
						passFailBean.setWrittenscore(finalExamScore.trim()+"");
						continue;
					}else if("true".equalsIgnoreCase(hasValidAttemptAfter_RIA_NV)) {
						continue; //continue to get best marks as list hasValidAttemptAfter_RIA_NV
					}else {
						throw new Exception("Error in getting hasValidAttemptAfter_RIA_NV, got : "+hasValidAttemptAfter_RIA_NV);
					}
				}
				// added to check RIA NV logic 28-jan-19 end
				
				latestWrittenScore = (int)Math.round(Double.parseDouble(finalExamScore));
				passFailBean.setLastWrittenscore(finalExamScore);
				if(latestWrittenScore >= bestWrittenScore){
					passFailBean.setWrittenYear(bean.getYear());
					passFailBean.setWrittenMonth(bean.getMonth());
					passFailBean.setGracemarks(bean.getGracemarks());
					passFailBean.setWrittenAttemptOrder(examOrder);
					passFailBean.setWrittenscore(latestWrittenScore+"");
					
					bestWrittenScore = latestWrittenScore;
					//scoreFound = true;//Not used, commented by Vilpesh on 2021-12-17
				}


			} catch (Exception e) {
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setWrittenAttemptOrder(examOrder);
			}

		}
		//If students only valid records are "AB"
		if("".equalsIgnoreCase(passFailBean.getWrittenscore()) && !"".equalsIgnoreCase(tempRecord) ) {
			passFailBean.setWrittenscore(tempRecord);
			passFailBean.setWrittenYear(tempYear);
			passFailBean.setWrittenMonth(tempMonth);
			passFailBean.setWrittenAttemptOrder(tempExamOrder);
		}
		//If students only valid records are blank
		if("".equalsIgnoreCase(passFailBean.getWrittenscore()) && "".equalsIgnoreCase(tempRecord) ) {
					passFailBean.setWrittenscore(tempRecord);
					passFailBean.setWrittenYear(tempYear);
					passFailBean.setWrittenMonth(tempMonth);
					passFailBean.setWrittenAttemptOrder(tempExamOrder);
		}
		return bestWrittenScore;
	}
	
	/* 
	 * returns true if list of marks for a subject has a valid tee attempt after a NV record
	 * e.g : For,  (10,NV,20,30) returns true and (10,20,NV,AB,"") returns false.  
	 * */
	private String checkIfHasValidAttemptAfter_RIA_NV(ArrayList<StudentMarksBean> currentStudentList) {
		int i = 0;
		boolean got_RIA_Nv_Record=false;
		boolean gotValidRecordAfter_RIA_Nv = false;
		int latestWrittenScore = 0;
		for(StudentMarksBean b : currentStudentList) {
			
			if("NV".equalsIgnoreCase(b.getWritenscore()) || "RIA".equalsIgnoreCase(b.getWritenscore())) {
				got_RIA_Nv_Record = true;
				gotValidRecordAfter_RIA_Nv = false;
				i++;
				continue;
			}
			
			if(got_RIA_Nv_Record) {
				
				try {
					latestWrittenScore = (int)Math.round(Double.parseDouble(b.getWritenscore()));
					gotValidRecordAfter_RIA_Nv = true;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					//
				}
			}
			
			i++;
		}
		
		if(gotValidRecordAfter_RIA_Nv) {
			return "true";
		}else {
			return "false";
		}
		
	}
	

	public boolean executeAssignmentRelatedPassFailLogic(ArrayList<StudentMarksBean> currentList, PassFailExamBean passFailBean,
			StudentExamBean student, ArrayList<PassFailExamBean> passFailStudentList, ProgramSubjectMappingExamBean passingConfiguration) {

		String assignmentNeededBeforeWritten = passingConfiguration.getAssignmentNeededBeforeWritten();
		String hasAssignment = passingConfiguration.getHasAssignment();

		if("Y".equalsIgnoreCase(hasAssignment)){
			boolean isLastAttemptvalid = isLastAttemptWithAssignment(currentList, passFailBean);
			if((!isLastAttemptvalid)){
				passFailBean.setIsPass("N");
				passFailBean.setFailReason(ANS_Messge);
				//Though ANS, still record his written score with Total as blank
				executeTEERelatedPassFailLogic(currentList, passFailBean, passingConfiguration);
				passFailBean.setTotal("");
				passFailStudentList.add(passFailBean);
				return false;
			}
		}

		if("Y".equalsIgnoreCase(assignmentNeededBeforeWritten)){


			boolean isAssgntBeforeWritten = false;

			//Changed  by Steffi to allow results live of offline students in APR/SEP cycle

			if("Offline".equals(student.getExamMode())){
				//Check if written attempt is after assignment, for Offline exam student
				//Commented by Steffi, as this is no longr needed. 08-Mar-2018
				//isAssgntBeforeWritten = isAssignmentBeforeWritten(currentList, passFailBean);
				isAssgntBeforeWritten = true;//Assignment can be after Written as well for Offline students, Decided on 08-Mar-2018, for live students only
			}else{
				double maxExamOrder = getMaxExamOrderForCurrentSubject(currentList);
				if(maxExamOrder > 15){
					//Last Attempt after Dec-2015
					isAssgntBeforeWritten = true; //Assignment can be given after written as well from Apr-2016, so no need to check
				}else{
					//Last Attempt before Apr-2016, i.e. till Dec-2015
					//Check if written attempt is after assignment, for old pass fail cases before Apr-2016
					isAssgntBeforeWritten = isAssignmentBeforeWritten(currentList, passFailBean);
				}

			}
			if(!isAssgntBeforeWritten){
				//Fail:Even if assignment is after written, display his best assignment score
				String assignmentScoreModel = passingConfiguration.getAssignmentScoreModel();
				if("Best".equalsIgnoreCase(assignmentScoreModel)){
					calculateBestAssignmentScore(currentList,passFailBean);
				}else{
					//Future Model of Assignment Scoring. As of now it is only Best of Assigment
				}

				passFailBean.setTotal(passFailBean.getAssignmentscore());
				passFailBean.setIsPass("N");
				if("AB".equals(passFailBean.getWrittenscore()) || "#CC".equals(passFailBean.getWrittenscore()) 
						|| "RIA".equals(passFailBean.getWrittenscore()) || "NV".equals(passFailBean.getWrittenscore()) ){
					//Don't make written score blank
				}else{
					passFailBean.setWrittenscore("");
				}

				passFailBean.setFailReason(assignmentAfterWrittenMesage);
				passFailStudentList.add(passFailBean);
				return false;
			}
		}

		//Since written attempt is after assignment, get best of assignment and latest of written marks
		String assignmentScoreModel = passingConfiguration.getAssignmentScoreModel();
		if("Best".equalsIgnoreCase(assignmentScoreModel)){
			calculateBestAssignmentScore(currentList,passFailBean);
		}else{
			//Future Model of Assignment Scoring. As of now it is only Best of Assigment
		}

		return true;
	}

	//Pass Fail logic for general students.
	public ArrayList<PassFailExamBean> processBajaj(HashMap<String, ArrayList> keysMap) {
		// previously processACBM
		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<PassFailExamBean>();
		//ArrayList<PassFailBean> passStudentList = new ArrayList<PassFailBean>();
		int passCount = 0;
		int failCount = 0;
		Iterator entries = keysMap.entrySet().iterator();
		//keysMap.size();

		HashMap<String, StudentExamBean> studentsMap = getAllStudents();
		//while (entries.hasNext()) {
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();

			PassFailExamBean passFailBean = new PassFailExamBean();
			StudentMarksBean currentBean = currentList.get(0);
			String sapId = currentBean.getSapid();
			StudentExamBean student = studentsMap.get(sapId);

			transferDataInPassBean(passFailBean, currentBean);
			boolean isLastAttemptvalid = isLastAttemptWithAssignment(currentList, passFailBean);
			//Should have given assignemnt atleast once
			if((!isLastAttemptvalid)){

				passFailBean.setIsPass("N");
				passFailBean.setSubjectCutoffCleared("NA");
//				passFailBean.setFailReason(ANS_ACBM_Messge);
				passFailBean.setFailReason(ANS_BAJAJ_Messge);
				//Though ANS, still record his written score with Total as blank
				int latestWrittenScore = getLatestWrittenScore(currentList, passFailBean);
				passFailBean.setTotal("");
				passFailStudentList.add(passFailBean);
				failCount++;
				continue;
			}

			boolean hasWrittenAttempt = hasWrittenAttempt(currentList);
			//Student is fail if he has not given Written Exam
			if((!hasWrittenAttempt)){
				passFailBean.setIsPass("N");
				passFailBean.setSubjectCutoffCleared("NA");
				//Though no written attempt, we need values of Assignment and written score with their year and month
				int bestAssignmentScore = calculateBestAssignmentScore(currentList,passFailBean);
				int latestWrittenScore = getLatestWrittenScore(currentList, passFailBean);
				if("AB".equals(passFailBean.getWrittenscore()) || "#CC".equals(passFailBean.getWrittenscore()) 
						|| "RIA".equals(passFailBean.getWrittenscore()) || "NV".equals(passFailBean.getWrittenscore()) ){
					passFailBean.setTotal(String.valueOf(bestAssignmentScore));//Don't make written score blank
				}else{
					passFailBean.setTotal("");
				}
				passFailBean.setFailReason(writtenAttemptMissing);

				passFailStudentList.add(passFailBean);
				failCount++;
				continue;
			}

			//Since written attempt is after assignment, get best of assignment and latest of written marks
			int bestAssignmentScore = calculateBestAssignmentScore(currentList,passFailBean);
			int latestWrittenScore = getLatestWrittenScore(currentList, passFailBean);

			if(bestAssignmentScore + latestWrittenScore >= 40){
				passFailBean.setIsPass("Y");
				passFailBean.setSubjectCutoffCleared("Y");
				passFailBean.setWrittenscore(latestWrittenScore+"");
				if("Project".equals(passFailBean.getSubject()) || "Module 4 - Project".equals(passFailBean.getSubject())){
					passFailBean.setAssignmentscore("");
				}else{
					passFailBean.setAssignmentscore(bestAssignmentScore+"");
				}

				passFailBean.setTotal(bestAssignmentScore+latestWrittenScore+"");
				passFailBean.setFailReason("");
				passFailStudentList.add(passFailBean);
				passCount++;
			}else{//Fail as subject cutoff not cleared 
				if("Project".equals(passFailBean.getSubject()) || "Module 4 - Project".equals(passFailBean.getSubject())){
					passFailBean.setAssignmentscore("");
					//No need to set written score, its already set in getLatestWrittenScore()
				}else{
					//passFailBean.setWrittenscore(latestWrittenScore+"");
					passFailBean.setAssignmentscore(bestAssignmentScore+"");
				}
				passFailBean.setTotal(bestAssignmentScore+latestWrittenScore+"");
				passFailBean.setFailReason(lessThan40Mesage);
				passFailBean.setSubjectCutoffCleared("N");
				passFailBean.setIsPass("N");
				passFailStudentList.add(passFailBean);
				failCount++;

			}
		}

		return passFailStudentList;

	}

	/*
	 * Commented by Sanket: 22-Feb-2017, as there is not sem wise passing.
	 * 
	 * public ArrayList<PassFailBean> processACBMSemWise(ArrayList<PassFailBean> tempAcbmPassFailStudentList) {
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < tempAcbmPassFailStudentList.size(); i++){
			PassFailBean bean = (PassFailBean)tempAcbmPassFailStudentList.get(i);
			String key = bean.getSapid().trim()+ bean.getSem();
			if(!keysMap.containsKey(key)){
				ArrayList<PassFailBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
			}else{
				ArrayList<PassFailBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}

		ArrayList<PassFailBean> passFailStudentList = new ArrayList<PassFailBean>();
		//ArrayList<PassFailBean> passStudentList = new ArrayList<PassFailBean>();
		Iterator entries = keysMap.entrySet().iterator();
		//keysMap.size();

		HashMap<String, StudentBean> studentsMap = getAllStudents();
		//while (entries.hasNext()) {
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<PassFailBean> currentList = (ArrayList)thisEntry.getValue();

			boolean alreadyFailed = checkIfAlreadyFailedACBM(currentList);//Check if aolready failed due to lack of assignment or below cutoff
			if(alreadyFailed){
				passFailStudentList.addAll(currentList);
				continue;
			}

			boolean attemptedAllSubjects = checkIfAttemptedAllSubjects(currentList);
			if(!attemptedAllSubjects){
				passFailStudentList.addAll(currentList);
				continue;
			}

			boolean hasClearedAveragePassing = checkIfClearedAvgPassingACBM(currentList);
			passFailStudentList.addAll(currentList);


		}

		return passFailStudentList;

	}*/


	private boolean checkIfClearedAvgPassingBajaj(ArrayList<PassFailExamBean> currentList) {
		boolean averageCutoffCleared = false;
		Double allSubjectsTotal = 0.0;
		for (PassFailExamBean bean : currentList) {
			String total = bean.getTotal();
			if(total != null && !"".equals(total)){
				allSubjectsTotal += Double.parseDouble(total);
			}else{
				allSubjectsTotal += 0;
			}
		}

		if(allSubjectsTotal/ 6 >= 50.0){
			averageCutoffCleared = true; 
		}	

		for (PassFailExamBean passFailBean : currentList) {
			if(averageCutoffCleared){
				passFailBean.setIsPass("Y");
			}else{
				passFailBean.setIsPass("N");
				passFailBean.setFailReason(averageCutoffNotCleared);
			}
		}

		return averageCutoffCleared;
	}


	private boolean checkIfAttemptedAllSubjects(ArrayList<PassFailExamBean> currentList) {
		boolean attemptedAllSubjects = true;
		if(currentList.size() < 6){
			attemptedAllSubjects = false;
		}

		if(!attemptedAllSubjects){
			for (PassFailExamBean bean : currentList) {
				bean.setIsPass("N");
				bean.setFailReason(allSubjectsNotAttempted);
			}
		}
		return attemptedAllSubjects;
	}


	private boolean checkIfAlreadyFailedBajaj(ArrayList<PassFailExamBean> currentList) {
		boolean alreadyFailed = false;
		String failReason = "";
		for (PassFailExamBean bean : currentList) {
			if("N".equals(bean.getIsPass())){
				alreadyFailed = true;
				failReason = bean.getFailReason();
				break;
			}
		}

		if(alreadyFailed){
			//If one subject failed, mark all failed
			for (PassFailExamBean bean : currentList) {
				bean.setIsPass("N");
				bean.setFailReason(failReason);
			}
		}
		return alreadyFailed;
	}


	private double getMaxExamOrderForCurrentSubject(ArrayList<StudentMarksBean> currentList) {
		//Check when was his most recent attempt for exam, either written or assignment from exam.marks table
		double maxExamOrder = 0;
		for (StudentMarksBean studentMarksBean : currentList) {
			double currentExamOrder = Double.parseDouble(studentMarksBean.getExamorder());
			if(currentExamOrder > maxExamOrder){
				maxExamOrder = currentExamOrder;
			}
		}
		return maxExamOrder;
	}


	private boolean isLastAttemptWithAssignment(ArrayList<StudentMarksBean> currentStudentList,PassFailExamBean passFailBean) {
		//Traverse from most recent marks backwards to oldest marks, to find assignment score
		for (int i = currentStudentList.size()-1; i >= 0; i--) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);
			String subject = bean.getSubject();
			if("Project".equals(subject.trim())){//Assignment not applicable for Project
				return true;
			}
			if("Module 4 - Project".equals(subject.trim())){
				return true;
			}

			String assignmentScore = bean.getAssignmentscore();
			if(assignmentScore == null){
				assignmentScore = "";
			}

			if("".equals(assignmentScore.trim())){//
				if(i == 0){//Reached end without assignment score, means old record with only total. This was to handle old records of 2008
					passFailBean.setAssignmentscore("");
					return true;
				}else{//Blank score, so check next assignment score record till u reach end
					continue;
				}

			}else if("ANS".equalsIgnoreCase(assignmentScore.trim())){//If recentmost score is ANS, written score will be NA.
				passFailBean.setAssignmentscore(assignmentScore);
				//passFailBean.setWrittenscore("NA");//Not to be made NA as per instructions from Rajiv sir. 26-Jan-2017
				passFailBean.setAssignmentYear(bean.getYear());
				passFailBean.setAssignmentMonth(bean.getMonth());
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				return false;
			}
			try {//Check if valid score
				Double.parseDouble(assignmentScore);
				passFailBean.setWrittenYear("");
				passFailBean.setWrittenMonth("");
				passFailBean.setAssignmentYear("");
				passFailBean.setAssignmentMonth("");
				return true;
			} catch (NumberFormatException e) {
				//Something other than number found, CC,NV,RIA, So has to be fail, since recent most attempt doesn't have assignment score.
				passFailBean.setAssignmentscore(assignmentScore);
				passFailBean.setAssignmentYear(bean.getYear());
				passFailBean.setAssignmentMonth(bean.getMonth());
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				return false;
			}
		}
		return false;
	}

	private void transferDataInPassBean(PassFailExamBean passFailBean, StudentMarksBean currentBean) {
		passFailBean.setSapid(currentBean.getSapid());
		passFailBean.setSubject(currentBean.getSubject());
		passFailBean.setSem(currentBean.getSem());
		passFailBean.setProgram(currentBean.getProgram());
		passFailBean.setGrno(currentBean.getGrno());
		passFailBean.setName(currentBean.getStudentname());
		passFailBean.setWrittenscore("");
		passFailBean.setAssignmentscore("");
		passFailBean.setWrittenYear("");
		passFailBean.setWrittenMonth("");
		passFailBean.setAssignmentYear("");
		passFailBean.setAssignmentMonth("");
		passFailBean.setStudentType(currentBean.getStudentType());
	}

	public void upsertPassFailRecordsBySAPID(ArrayList<PassFailExamBean> studentList,HashMap<String, String> exisintgRecordMap) {
		
		if(studentList == null || studentList.isEmpty())
			return;
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> updateList  = new ArrayList<>();
		ArrayList<PassFailExamBean> insertList  = new ArrayList<>();
		for(int i = 0; i < studentList.size(); i++){
			PassFailExamBean bean = studentList.get(i);
			if(exisintgRecordMap.containsKey(bean.getSapid().trim()+bean.getSubject().trim())){
				updateList.add(bean);
			}else{
				insertList.add(bean);
			}
			if(i % 1000 == 0){
			}
		}
		if(studentList != null) {
		}
		passFailLogger.info("START  updatePassFailBatch Size "+updateList.size()+" AND insertPassFailBatch Size "+insertList.size());
		updatePassFailBatch(updateList, jdbcTemplate, BYSAPID);
		insertPassFailBatch(insertList,jdbcTemplate, BYSAPID);
		passFailLogger.info("END updatePassFailBatch AND insertPassFailBatch ");
	}
	
	
	public void upsertPassFailStagingRecordsBySAPID(List<PassFailExamBean> studentList,Map<String, String> exisintgRecordMap) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<PassFailExamBean> updateList  = new ArrayList<>();
		List<PassFailExamBean> insertList  = new ArrayList<>();
		
		for(PassFailExamBean bean : studentList) {
			if(exisintgRecordMap.containsKey(bean.getSapid().trim()+bean.getSubject().trim()))
				updateList.add(bean);
			else insertList.add(bean);
		}
		
		passFailLogger.info("START  updatePassFailBatch Size "+updateList.size()+" AND insertPassFailBatch Size "+insertList.size());
		updatePassFailStagingBatch(updateList, jdbcTemplate, BYSAPID);
		insertPassFailStagingBatch(insertList,jdbcTemplate, BYSAPID);
		passFailLogger.info("END updatePassFailBatch AND insertPassFailBatch ");
	}


	public void upsertPassFailRecordsByGRNO(ArrayList<PassFailExamBean> studentList,HashMap<String, String> exisintgRecordMap) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> updateList  = new ArrayList<>();
		ArrayList<PassFailExamBean> insertList  = new ArrayList<>();

		for(int i = 0; i < studentList.size(); i++){
			PassFailExamBean bean = studentList.get(i);
			//boolean recordExists = checkIfRecordExists(bean, jdbcTemplate);
			//if(recordExists){
			if(exisintgRecordMap.containsKey(bean.getGrno().trim()+bean.getSubject().trim())){
				updateList.add(bean);
				//updatePassRecord(bean, jdbcTemplate);
			}else{
				insertList.add(bean);
				//insertPassRecord(bean, jdbcTemplate);
			}
			if(i % 1000 == 0){
			}
		}
		updatePassFailBatch(updateList, jdbcTemplate, BYGRNO);
		insertPassFailBatch(insertList,jdbcTemplate, BYGRNO);

	}
	
	public void upsertPassFailStagingRecordsByGRNO(ArrayList<PassFailExamBean> studentList,HashMap<String, String> exisintgRecordMap) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<PassFailExamBean> updateList  = new ArrayList<>();
		ArrayList<PassFailExamBean> insertList  = new ArrayList<>();

		for(int i = 0; i < studentList.size(); i++){
			PassFailExamBean bean = studentList.get(i);
			//boolean recordExists = checkIfRecordExists(bean, jdbcTemplate);
			//if(recordExists){
			if(exisintgRecordMap.containsKey(bean.getGrno().trim()+bean.getSubject().trim())){
				updateList.add(bean);
				//updatePassRecord(bean, jdbcTemplate);
			}else{
				insertList.add(bean);
				//insertPassRecord(bean, jdbcTemplate);
			}
			if(i % 1000 == 0){
			}
		}
		updatePassFailStagingBatch(updateList, jdbcTemplate, BYGRNO);
		insertPassFailStagingBatch(insertList,jdbcTemplate, BYGRNO);

	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, String> getKeysBySAPID(){

		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select * from exam.passfail";// only 2 columns are needed, so commented by Vilpesh on 2021-10-21.
		String sql = "select sapid, subject from exam.passfail";// added 2 columns, by Vilpesh on 2021-10-21.
		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PassFailExamBean.class));

		HashMap<String, String> keysMap = new HashMap<>(); 
		for(int m = 0 ; m < studentMarksList.size(); m++){
			PassFailExamBean bean = studentMarksList.get(m);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			keysMap.put(key,null);
		}

		return keysMap;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, String> getExistingRecordsFromPassFailStaging(){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT `sapid`, `subject` FROM `exam`.`passfail_staging`";
		
		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PassFailExamBean.class));

		HashMap<String, String> keysMap = new HashMap<>();
		
		for(int m = 0 ; m < studentMarksList.size(); m++){
			PassFailExamBean bean = studentMarksList.get(m);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			keysMap.put(key,null);
		}

		return keysMap;
	}
	
	@Transactional(readOnly = true)
	public Set<String> getExistingRecordsFromPassFail() {

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT `sapid`, `subject` FROM `exam`.`passfail`";

		return jdbcTemplate.query(sql, (rs) -> {
			Set<String> sapidAndSubjects = new HashSet<>();
			
			while (rs.next()) {
				String sapid = rs.getString(SAPID_DB);
				String subject = rs.getString(SUBJECT_DB);
				sapidAndSubjects.add(sapid.trim() + subject.trim());
			}
			return sapidAndSubjects;
		});

	}
	
	@Transactional(readOnly = true)
	public List<PassFailExamBean> getAllRecordsFromPassFailStaging(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM `exam`.`passfail_staging`";
		
		return jdbcTemplate.query(sql,new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<PassFailExamBean> getAllRecordsFromPassFailStagingByYearAndMonth(String year, String month){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM `exam`.`passfail_staging` WHERE `resultProcessedYear` = ? AND `resultProcessedMonth` = ?";
		
		return jdbcTemplate.query(sql,new Object[] {year, month},new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, String> getKeysByGRNO(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//just * replaced with grno, subject by Vilpesh on 2021-09-21
		String sql = "select grno, subject from exam.passfail";
		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PassFailExamBean.class));

		HashMap<String, String> keysMap = new HashMap<>(); 
		for(int m = 0 ; m < studentMarksList.size(); m++){
			PassFailExamBean bean = studentMarksList.get(m);
			String key = bean.getGrno().trim()+bean.getSubject().trim();
			keysMap.put(key,null);
		}

		return keysMap;
	}	
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public HashMap<String, String> getStagingKeysByGRNO(){

		jdbcTemplate = new JdbcTemplate(dataSource);
		
		//just * replaced with grno, subject by Vilpesh on 2021-09-21
		String sql = "select grno, subject from exam.passfail_staging";
		List<PassFailExamBean> studentMarksList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PassFailExamBean.class));

		HashMap<String, String> keysMap = new HashMap<>(); 
		for(int m = 0 ; m < studentMarksList.size(); m++){
			PassFailExamBean bean = studentMarksList.get(m);
			String key = bean.getGrno().trim()+bean.getSubject().trim();
			keysMap.put(key,null);
		}

		return keysMap;
	}	


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertPassFailBatch(List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate, final String type){
		int count = 0;
		final int batchSize = 1000;
		final String sql = " INSERT INTO exam.passfail "
				+ "(sapid,"
				+ "subject,"
				+ "grno,"
				+ "writtenYear,"
				+ "writtenMonth,"
				+ "assignmentYear,"
				+ "assignmentMonth,"
				+ "name,"
				+ "program,"
				+ "sem,"
				+ "writtenscore,"
				+ "assignmentscore,"
				+ "total,"
				+ "failReason,"
				+ "remarks,"
				+ "isPass,"
				+ "gracemarks,"
				+ "subjectCutoff,"
				+ "studentType,"
				+ "resultProcessedYear,"
				+ "resultProcessedMonth)"

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


		for (int j = 0; j < studentList.size(); j += batchSize) {
            count ++;
			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);
			try{
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						PassFailExamBean m = batchList.get(i);
						ps.setString(1, m.getSapid());
						ps.setString(2, m.getSubject());
						ps.setString(3, m.getGrno());
						ps.setString(4, m.getWrittenYear());
						ps.setString(5, m.getWrittenMonth());
						ps.setString(6, m.getAssignmentYear());
						ps.setString(7, m.getAssignmentMonth());
						ps.setString(8, m.getName());
						ps.setString(9, m.getProgram());
						ps.setString(10, m.getSem());
						ps.setString(11, m.getWrittenscore());
						ps.setString(12, m.getAssignmentscore());
						ps.setString(13, m.getTotal());
						ps.setString(14, m.getFailReason());
						ps.setString(15, m.getRemarks());
						ps.setString(16, m.getIsPass());
						ps.setString(17, m.getGracemarks());
						ps.setString(18, m.getSubjectCutoffCleared());
						ps.setString(19, m.getStudentType());
						ps.setString(20, m.getResultProcessedYear());
						ps.setString(21, m.getResultProcessedMonth());
					}
					public int getBatchSize() {
						return batchList.size();
					}


				});
				if(type.equals(BYGRNO)){
					updateProcessedStateBatchGRNO(batchList, jdbcTemplate);
				}else{
					updateProcessedStateBatchSAPID(batchList, jdbcTemplate);
				}
			}catch(Exception e){
				
			}

		}
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertPassFailStagingBatch(List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate, final String type){
		int count = 0;
		final int batchSize = 1000;
		final String sql = " INSERT INTO exam.passfail_staging "
				+ "(sapid,"
				+ "subject,"
				+ "grno,"
				+ "writtenYear,"
				+ "writtenMonth,"
				+ "assignmentYear,"
				+ "assignmentMonth,"
				+ "name,"
				+ "program,"
				+ "sem,"
				+ "writtenscore,"
				+ "assignmentscore,"
				+ "total,"
				+ "failReason,"
				+ "remarks,"
				+ "isPass,"
				+ "gracemarks,"
				+ "subjectCutoff,"
				+ "studentType,"
				+ "resultProcessedYear,"
				+ "resultProcessedMonth)"

				+ "	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


		for (int j = 0; j < studentList.size(); j += batchSize) {
            count ++;
			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);
			try{
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						PassFailExamBean m = batchList.get(i);
						ps.setString(1, m.getSapid());
						ps.setString(2, m.getSubject());
						ps.setString(3, m.getGrno());
						ps.setString(4, m.getWrittenYear());
						ps.setString(5, m.getWrittenMonth());
						ps.setString(6, m.getAssignmentYear());
						ps.setString(7, m.getAssignmentMonth());
						ps.setString(8, m.getName());
						ps.setString(9, m.getProgram());
						ps.setString(10, m.getSem());
						ps.setString(11, m.getWrittenscore());
						ps.setString(12, m.getAssignmentscore());
						ps.setString(13, m.getTotal());
						ps.setString(14, m.getFailReason());
						ps.setString(15, m.getRemarks());
						ps.setString(16, m.getIsPass());
						ps.setString(17, m.getGracemarks());
						ps.setString(18, m.getSubjectCutoffCleared());
						ps.setString(19, m.getStudentType());
						ps.setString(20, m.getResultProcessedYear());
						ps.setString(21, m.getResultProcessedMonth());
					}
					public int getBatchSize() {
						return batchList.size();
					}


				});
				if(BYGRNO.equals(type)){
					updateProcessedStateBatchGRNO(batchList, jdbcTemplate);
				}else{
					updateProcessedStateBatchSAPID(batchList, jdbcTemplate);
				}
			}catch(Exception e){
				
			}

		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updatePassFailBatch(List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate, final String type){
		final int batchSize = 1000;
		String sql = "Update exam.passfail set "
				+ " grno = ?,"
				+ " writtenYear = ?,"
				+ " writtenMonth = ?,"
				+ " assignmentYear = ?,"
				+ " assignmentMonth = ?,"
				+ " name = ?,"
				+ " program = ?,"
				+ " sem = ?,"
				+ " writtenscore = ?,"
				+ " assignmentscore = ?,"
				+ " total = ?,"
				+ " failReason = ?,"
				+ " remarks = ?,"
				+ " isPass = ?,"
				+ " gracemarks = ?,"
				+ " subjectCutoff = ?,"
				+ " studentType = ?,"
				+ " resultProcessedYear = ?,"
				+ " resultProcessedMonth = ? ";

		if(type.equals(BYGRNO)){
			sql = sql + " where  grno = ? and subject = ?";
		}else{
			sql = sql + " where  sapid = ? and subject = ?";
		}


		for (int j = 0; j < studentList.size(); j += batchSize) {

			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);
			try{
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						PassFailExamBean bean = batchList.get(i);
						ps.setString(1, bean.getGrno());
						ps.setString(2, bean.getWrittenYear());
						ps.setString(3, bean.getWrittenMonth());
						ps.setString(4, bean.getAssignmentYear());
						ps.setString(5, bean.getAssignmentMonth());
						ps.setString(6, bean.getName());
						ps.setString(7, bean.getProgram());
						ps.setString(8, bean.getSem());
						ps.setString(9, bean.getWrittenscore());
						ps.setString(10, bean.getAssignmentscore());
						ps.setString(11, bean.getTotal());
						ps.setString(12, bean.getFailReason());
						ps.setString(13, bean.getRemarks());
						ps.setString(14, bean.getIsPass());
						ps.setString(15, bean.getGracemarks());
						ps.setString(16, bean.getSubjectCutoffCleared());
						ps.setString(17, bean.getStudentType());
						ps.setString(18, bean.getResultProcessedYear());
						ps.setString(19, bean.getResultProcessedMonth());
						
						if(type.equals(BYGRNO)){
							ps.setString(20, bean.getGrno());
						}else{
							ps.setString(20, bean.getSapid());
						}
						ps.setString(21, bean.getSubject());
					}
					public int getBatchSize() {
						return batchList.size();
					}


				});

				if(type.equals(BYGRNO)){
					updateProcessedStateBatchGRNO(batchList, jdbcTemplate);
				}else{
					updateProcessedStateBatchSAPID(batchList, jdbcTemplate);
				}
			}catch(Exception e){
				
			}
		}
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updatePassFailStagingBatch(List<PassFailExamBean> studentList, JdbcTemplate jdbcTemplate, final String type){
		final int batchSize = 1000;
		String sql = "Update exam.passfail_staging set "
				+ " grno = ?,"
				+ " writtenYear = ?,"
				+ " writtenMonth = ?,"
				+ " assignmentYear = ?,"
				+ " assignmentMonth = ?,"
				+ " name = ?,"
				+ " program = ?,"
				+ " sem = ?,"
				+ " writtenscore = ?,"
				+ " assignmentscore = ?,"
				+ " total = ?,"
				+ " failReason = ?,"
				+ " remarks = ?,"
				+ " isPass = ?,"
				+ " gracemarks = ?,"
				+ " subjectCutoff = ?,"
				+ " studentType = ?,"
				+ " resultProcessedYear = ?,"
				+ " resultProcessedMonth = ? ";

		if(BYGRNO.equals(type))
			sql = sql + " where  grno = ? and subject = ?";
		else
			sql = sql + " where  sapid = ? and subject = ?";


		for (int j = 0; j < studentList.size(); j += batchSize) {

			final List<PassFailExamBean> batchList = studentList.subList(j, j + batchSize > studentList.size() ? studentList.size() : j + batchSize);
			try{
				jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

					@Override
					public void setValues(PreparedStatement ps, int i)	throws SQLException {
						PassFailExamBean bean = batchList.get(i);
						ps.setString(1, bean.getGrno());
						ps.setString(2, bean.getWrittenYear());
						ps.setString(3, bean.getWrittenMonth());
						ps.setString(4, bean.getAssignmentYear());
						ps.setString(5, bean.getAssignmentMonth());
						ps.setString(6, bean.getName());
						ps.setString(7, bean.getProgram());
						ps.setString(8, bean.getSem());
						ps.setString(9, bean.getWrittenscore());
						ps.setString(10, bean.getAssignmentscore());
						ps.setString(11, bean.getTotal());
						ps.setString(12, bean.getFailReason());
						ps.setString(13, bean.getRemarks());
						ps.setString(14, bean.getIsPass());
						ps.setString(15, bean.getGracemarks());
						ps.setString(16, bean.getSubjectCutoffCleared());
						ps.setString(17, bean.getStudentType());
						ps.setString(18, bean.getResultProcessedYear());
						ps.setString(19, bean.getResultProcessedMonth());
						
						if(BYGRNO.equals(type))
							ps.setString(20, bean.getGrno());
						else
							ps.setString(20, bean.getSapid());
						
						ps.setString(21, bean.getSubject());
					}
					
					public int getBatchSize() {
						return batchList.size();
					}


				});

				if(BYGRNO.equals(type))
					updateProcessedStateBatchGRNO(batchList, jdbcTemplate);
				else
					updateProcessedStateBatchSAPID(batchList, jdbcTemplate);
				
			}catch(Exception e){
				
			}
		}
	}

	private boolean isAssignmentBeforeWritten(ArrayList<StudentMarksBean> currentStudentList,PassFailExamBean passFailBean) {
		double lastAssgnmnetAttemptNumber = 0;
		double lastWrittenAttemptNumber = 0;
		boolean minOneAssignmentValueFound = false;
		boolean minOneValidWrittenValueFound = false;
		passFailBean.setAssignmentAttemptOrder(0);

		if(currentStudentList.size() > 0){
			String subject = currentStudentList.get(0).getSubject();
			if("Project".equals(subject.trim())){
				return true;
			}
			if("Module 4 - Project".equals(subject.trim())){
				return true;
			}
		}

		for (int i = 0; i < currentStudentList.size(); i++) {

			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);

			//int examOrder = Integer.parseInt(bean.getExamorder());
			double examOrder = Double.parseDouble(bean.getExamorder());
			String finalExamScore = bean.getWritenscore();
			String assignmentScore = bean.getAssignmentscore();
			if(finalExamScore == null){
				finalExamScore = "";
			}
			if(assignmentScore == null){
				assignmentScore = "";
			}

			try {
				Double.parseDouble(finalExamScore);
				minOneValidWrittenValueFound = true;
				if(lastWrittenAttemptNumber < examOrder){
					lastWrittenAttemptNumber = examOrder;
					passFailBean.setWrittenYear(bean.getYear());
					passFailBean.setWrittenMonth(bean.getMonth());
					//passFailBean.setWrittenscore(finalExamScore);
				}
			} catch (NumberFormatException e) {
				if("AB".equals(finalExamScore) || "#CC".equals(finalExamScore) || "RIA".equals(finalExamScore) 
						|| "NV".equals(finalExamScore)  ||  "".equals(finalExamScore)){
					passFailBean.setWrittenscore(finalExamScore);
					passFailBean.setWrittenYear(bean.getYear());
					passFailBean.setWrittenMonth(bean.getMonth());
				}
			}


			try {
				Double.parseDouble(assignmentScore);
				minOneAssignmentValueFound = true;
				if(lastAssgnmnetAttemptNumber < examOrder){
					lastAssgnmnetAttemptNumber = examOrder;
					passFailBean.setAssignmentYear(bean.getYear());
					passFailBean.setAssignmentMonth(bean.getMonth());
					passFailBean.setAssignmentAttemptOrder(examOrder);
					passFailBean.setAssignmentscore(assignmentScore);
				}
			} catch (NumberFormatException e) {
			}

		}
		if((lastAssgnmnetAttemptNumber <= lastWrittenAttemptNumber) && (minOneAssignmentValueFound) ){
			return true;
		}else{
			return false;
		}
	}

	private boolean hasAdditionalRecordWithOnlyTotal(ArrayList<StudentMarksBean> currentStudentList, double lastAttemptOrder) {
		boolean hasAdditionalRecordWithOnlyTotal = false;
		//int latestAssignmentAttemptNumber = passFailBean.getAssignmentAttemptOrder();
		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = currentStudentList.get(i);

			//int examOrder = Integer.parseInt(bean.getExamorder());
			double examOrder = Double.parseDouble(bean.getExamorder());
			String finalExamScore = bean.getWritenscore();
			String assignmentScore = bean.getAssignmentscore();

			if(    (finalExamScore == null || "".equals(finalExamScore.trim())) &&
					(assignmentScore == null || "".equals(assignmentScore.trim())) && (examOrder >= lastAttemptOrder) ){
				hasAdditionalRecordWithOnlyTotal = true;
				break;
			}

		}

		return hasAdditionalRecordWithOnlyTotal;
	}

	private int getLatestTotalMarks(ArrayList<StudentMarksBean> currentStudentList, PassFailExamBean passFailBean) {
		int latestTotalMarks = 0;
		/*StudentBean bean = (StudentBean)currentStudentList.get(currentStudentList.size()-1);
		latestTotalMarks = bean.getTotalMarksValue();
		 */

		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = currentStudentList.get(i);
			String finalExamScore = bean.getWritenscore();
			String assignmentScore = bean.getAssignmentscore();

			if(    (finalExamScore == null || "".equals(finalExamScore)) &&
					(assignmentScore == null || "".equals(assignmentScore))   	){

				try{
					latestTotalMarks = Integer.parseInt(bean.getTotal());
				}catch(Exception e){
					latestTotalMarks = 0;
				}
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setAssignmentYear(bean.getYear());
				passFailBean.setAssignmentMonth(bean.getMonth());
				passFailBean.setGracemarks(bean.getGracemarks());
				//currentStudentList.get(0).setWrittenPassingYear(bean.getYear());
				//currentStudentList.get(0).setWrittenPassingMonth(bean.getMonth());

				//currentStudentList.get(0).setAssgnPassingYear("");
				//currentStudentList.get(0).setAssgnPassingMonth("");
			}
		}
		return latestTotalMarks;
	}

	private int calculateBestAssignmentScore(ArrayList<StudentMarksBean> currentStudentList, PassFailExamBean passFailBean) {

		int bestAssignmentScore = 0;
		int currentAssignmentScore = 0;
		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);

			String assignmentScore = bean.getAssignmentscore();


			try {
				if(assignmentScore == null){
					assignmentScore = "";
				}
				currentAssignmentScore = (int)Math.round(Double.parseDouble(assignmentScore));
				passFailBean.setLastAssignmentscore(assignmentScore);
				if(currentAssignmentScore >= bestAssignmentScore){
					bestAssignmentScore = currentAssignmentScore;

					//currentStudentList.get(0).setAssgnPassingYear(bean.getYear());
					//currentStudentList.get(0).setAssgnPassingMonth(bean.getMonth());
					passFailBean.setAssignmentYear(bean.getYear());
					passFailBean.setAssignmentMonth(bean.getMonth());
					passFailBean.setAssignmentscore(currentAssignmentScore+"");
				}
			} catch (NumberFormatException e) {
			}

		}
		return bestAssignmentScore;
	}


/*
	private int getAssignmentScore(ArrayList<StudentMarksBean> currentStudentList, PassFailBean passFailBean) {

		boolean flag = hasWrittenForEveryAssignment(currentStudentList);
		if(flag){
			return calculateBestAssignmentScore(currentStudentList, passFailBean);
		}else{
			return getLatestAssignmentScore(currentStudentList, passFailBean);
		}

	}


	private int getLatestAssignmentScore(ArrayList<StudentMarksBean> currentStudentList, PassFailBean passFailBean) {

		int latestAssignmentScore = 0;

		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);
			String assignmentScore = bean.getAssignmentscore();
			try {
				if(assignmentScore == null || "".equals(assignmentScore)){
					assignmentScore = "";
					continue;
				}
				latestAssignmentScore = (int)Math.round(Double.parseDouble(assignmentScore));
				//currentStudentList.get(0).setWrittenPassingYear(bean.getYear());
				//currentStudentList.get(0).setWrittenPassingMonth(bean.getMonth());
				passFailBean.setAssignmentYear(bean.getYear());
				passFailBean.setAssignmentMonth(bean.getMonth());

			} catch (NumberFormatException e) {
			}

		}

		return latestAssignmentScore;
	}

	private boolean hasWrittenForEveryAssignment(ArrayList<StudentMarksBean> currentStudentList) {
		ArrayList<String> assignmentAttemptsList = new ArrayList<>();
		ArrayList<String> writtenAttemptsList = new ArrayList<>();

		boolean result = true;

		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);

			//int examOrder = Integer.parseInt(bean.getExamorder());
			String finalExamScore = bean.getWritenscore();
			String assignmentScore = bean.getAssignmentscore();

			if(finalExamScore == null){
				finalExamScore = "";
			}
			if(assignmentScore == null){
				assignmentScore = "";
			}
			try {
				Double.parseDouble(finalExamScore);
				writtenAttemptsList.add(bean.getExamorder());
			} catch (NumberFormatException e) {
			}


			try {
				Double.parseDouble(assignmentScore);
				assignmentAttemptsList.add(bean.getExamorder());
			} catch (NumberFormatException e) {
			}

		}

		for(int i = 0 ; i < assignmentAttemptsList.size(); i++){
			String assignmentAttemptNumber = assignmentAttemptsList.get(i);
			if(!writtenAttemptsList.contains(assignmentAttemptNumber)){
				return false;
			}
		}
		return result;

	}
*/
	private boolean hasWrittenAttempt(ArrayList<StudentMarksBean> currentStudentList) {

		boolean hasWrittenAttempt = false;

		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);

			String finalExamScore = bean.getWritenscore();

			if(finalExamScore == null){
				finalExamScore = "";
			}
			try {
				Double.parseDouble(finalExamScore);
				hasWrittenAttempt = true;
				break;
			} catch (NumberFormatException e) {
				//return hasWrittenAttempt;
			}



		}

		return hasWrittenAttempt;

	}


	private int getLatestWrittenScore(ArrayList<StudentMarksBean> currentStudentList, PassFailExamBean passFailBean/*, int assignmentScore*/) {

		int latestWrittenScore = 0;
		passFailBean.setWrittenAttemptOrder(0);
		//boolean isPassing = false;

		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = (StudentMarksBean)currentStudentList.get(i);
			String finalExamScore = bean.getWritenscore();
			String subject = bean.getSubject();
			//int examOrder = Integer.parseInt(bean.getExamorder());
			double examOrder = Double.parseDouble(bean.getExamorder());
			if("Project".equals(subject) && "AB".equals(finalExamScore)){
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setGracemarks(bean.getGracemarks());
				passFailBean.setWrittenAttemptOrder(examOrder);
				passFailBean.setWrittenscore(finalExamScore);
				latestWrittenScore = 0;
				continue; 
			} 
			if("Module 4 - Project".equals(subject) && "AB".equals(finalExamScore)){
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setGracemarks(bean.getGracemarks());
				passFailBean.setWrittenAttemptOrder(examOrder);
				passFailBean.setWrittenscore(finalExamScore);
				latestWrittenScore = 0;
				continue; 
			} 
			try {
				if(finalExamScore == null || "".equals(finalExamScore.trim())){	
					finalExamScore = "";
					continue;
				}
				latestWrittenScore = (int)Math.round(Double.parseDouble(finalExamScore));
				
				//currentStudentList.get(0).setWrittenPassingYear(bean.getYear());
				//currentStudentList.get(0).setWrittenPassingMonth(bean.getMonth());
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setGracemarks(bean.getGracemarks());
				passFailBean.setWrittenAttemptOrder(examOrder);
				passFailBean.setWrittenscore(latestWrittenScore+"");

				/*if(latestWrittenScore + assignmentScore >= 50){
					isPassing = true;
				}else{
					isPassing = false;
				}
				 */
			} catch (NumberFormatException e) {
				//Save AB, CC, NV also as valid value if it is latest score, 14-Oct-2014
				//if(!isPassing){//Commented due to issue reported on 11-May-2016 by Jigna
				latestWrittenScore = 0; //Made Zero since since online students have given projects and applied for reval due to the earlier drive WrittenScoreGetting queried in case of projects.
				passFailBean.setWrittenYear(bean.getYear());
				passFailBean.setWrittenMonth(bean.getMonth());
				passFailBean.setGracemarks(bean.getGracemarks());
				passFailBean.setWrittenAttemptOrder(examOrder);
				passFailBean.setWrittenscore(finalExamScore+"");
				//}
			}
			
		}
		return latestWrittenScore;
	}

	private boolean hasOnlyTotalMarks(ArrayList<StudentMarksBean> currentStudentList) {

		boolean hasOnlyTotalMarks = true;
		for (int i = 0; i < currentStudentList.size(); i++) {
			StudentMarksBean bean = currentStudentList.get(i);
			String finalExamScore = bean.getWritenscore();
			String assignmentScore = bean.getAssignmentscore();
			if(finalExamScore != null){
				if(!("".equals(finalExamScore.trim()))){
					return false;
				}
			}

			if(assignmentScore != null){
				if(!("".equals(assignmentScore.trim()))){
					return false;
				}
			}
			/*if((!("".equals(bean.getFinalExamScore()))) && (bean.getFinalExamScore() != null)){
				hasOnlyTotalMarks = false;
				break;
			}*/
		}
		return hasOnlyTotalMarks;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void applyGrace(List<PassFailExamBean> studentMarksList) {
		if(studentMarksList == null){
			return;
		}

		final List<PassFailExamBean> oneGraceList = new ArrayList<>();
		final List<PassFailExamBean> twoGraceList = new ArrayList<>();

		// create Program Subject PassScore map
		HashMap<String,Integer> programSubjectPassScoreMap = getProgramSubjectPassScoreMap();

		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			int marks = Integer.parseInt(bean.getTotal().trim());
			String key = bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
			int minPassScore = programSubjectPassScoreMap.get(key);
			if(marks == (minPassScore - 2)){
				twoGraceList.add(bean);
			}else if(marks == (minPassScore - 1)){
				oneGraceList.add(bean);
			}
		}

		//Update score in original mark database.
		String sql = "Update exam.marks "
				+ " set writenscore = writenscore + 1, "
				//+ " total = total + 1, "
				+ " gracemarks = '1' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail "
				+ " set writtenscore = writtenscore + 1, "
				+ " gracemarks = '1', "
				+ " total = total + 1 , "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});


		//Update score in original mark database.
		sql = "Update exam.marks "
				+ " set writenscore = writenscore + 2, "
				//+ " total = total + 2, "
				+ " gracemarks = '2' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";


		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail "
				+ " set writtenscore = writtenscore + 2, "
				+ " gracemarks = '2', "
				+ " total = total + 2 , "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});

	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public synchronized int applyGraceToPassFailStaging(List<PassFailExamBean> studentMarksList) {
		
		if(studentMarksList == null){
			return 0;
		}

		final List<PassFailExamBean> oneGraceList = new ArrayList<>();
		final List<PassFailExamBean> twoGraceList = new ArrayList<>();

		// create Program Subject PassScore map
		HashMap<String,Integer> programSubjectPassScoreMap = getProgramSubjectPassScoreMap();

		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			int marks = Integer.parseInt(bean.getTotal().trim());
			String key = bean.getProgram()+"-"+bean.getSubject()+"-"+bean.getPrgmStructApplicable();
			int minPassScore = programSubjectPassScoreMap.get(key);
			if(marks == (minPassScore - 2)){
				twoGraceList.add(bean);
			}else if(marks == (minPassScore - 1)){
				oneGraceList.add(bean);
			}
		}
		
		passFailLogger.info("twoGraceList : {} AND oneGraceList : {}",twoGraceList.size(), oneGraceList.size());

		//Update score in original mark database.
		String sql = "Update exam.marks "
				+ " set writenscore = writenscore + 1, "
				//+ " total = total + 1, "
				+ " gracemarks = '1' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int[] oneMarkGraceBatchUpdateResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});
		
		int oneMarkGraceBatchUpdateResultInt = Arrays.stream(oneMarkGraceBatchUpdateResult).sum();
		
		passFailLogger.info("Total rows updated for oneMarkGrace in marks table : {}",oneMarkGraceBatchUpdateResultInt);

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail_staging "
				+ " set writtenscore = writtenscore + 1, "
				+ " gracemarks = '1', "
				+ " total = total + 1 , "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int[] oneMarkGraceBatchUpdateResultPf = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});

		int oneMarkGraceBatchUpdateResultPfInt = Arrays.stream(oneMarkGraceBatchUpdateResultPf).sum();
		
		passFailLogger.info("Total rows updated for oneMarkGrace in passfail table : {}",oneMarkGraceBatchUpdateResultPfInt);

		//Update score in original mark database.
		sql = "Update exam.marks "
				+ " set writenscore = writenscore + 2, "
				//+ " total = total + 2, "
				+ " gracemarks = '2' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";


		int[] twoMarkGraceBatchUpdateResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});
		

		int twoMarkGraceBatchUpdateResultInt = Arrays.stream(twoMarkGraceBatchUpdateResult).sum();
		
		passFailLogger.info("Total rows updated for twoMarkGrace in marks table : {}",twoMarkGraceBatchUpdateResultInt);

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail_staging "
				+ " set writtenscore = writtenscore + 2, "
				+ " gracemarks = '2', "
				+ " total = total + 2 , "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		int[] twoMarkGraceBatchUpdateResultPf = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});
		
		int twoMarkGraceBatchUpdateResultPfInt = Arrays.stream(twoMarkGraceBatchUpdateResultPf).sum();
		
		passFailLogger.info("Total rows updated for twoMarkGrace in passfail table : {}",twoMarkGraceBatchUpdateResultPfInt);
		
		return twoMarkGraceBatchUpdateResultPfInt +  oneMarkGraceBatchUpdateResultPfInt;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void applyBajajGrace(List<PassFailExamBean> studentMarksList) {
		// previously applyACBMGrace
		if(studentMarksList == null){
			return;
		}

		final List<PassFailExamBean> oneGraceList = new ArrayList<>();
		final List<PassFailExamBean> twoGraceList = new ArrayList<>();

		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			int marks = Integer.parseInt(bean.getTotal().trim());
			if(marks == 38){
				twoGraceList.add(bean);
			}else if(marks == 39){
				oneGraceList.add(bean);
			}
		}


		//Update score in original mark database.
		String sql = "Update exam.marks "
				+ " set writenscore = writenscore + 1, "
				//+ " total = total + 1, "
				+ " gracemarks = '1' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail "
				+ " set writtenscore = writtenscore + 1, "
				+ " gracemarks = '1', "
				+ " total = '40', "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});


		//Update score in original mark database.
		sql = "Update exam.marks "
				+ " set writenscore = writenscore + 2, "
				//+ " total = total + 2, "
				+ " gracemarks = '2' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" //Vilpesh on 2022-03-04
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";


		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});

		//Update grace in Pass/Fail database
		sql = "Update exam.passfail "
				+ " set writtenscore = writtenscore + 2, "
				+ " gracemarks = '2', "
				+ " total = '40', "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";

		jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});

	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void applyBajajGraceToPassFailStaging(List<PassFailExamBean> studentMarksList) {
		// previously applyACBMGrace
		if(studentMarksList == null){
			return;
		}
		
		final List<PassFailExamBean> oneGraceList = new ArrayList<>();
		final List<PassFailExamBean> twoGraceList = new ArrayList<>();
		
		for (int i = 0; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			int marks = Integer.parseInt(bean.getTotal().trim());
			if(marks == 38){
				twoGraceList.add(bean);
			}else if(marks == 39){
				oneGraceList.add(bean);
			}
		}
		
		
		//Update score in original mark database.
		String sql = "Update exam.marks "
				+ " set writenscore = writenscore + 1, "
				//+ " total = total + 1, "
				+ " gracemarks = '1' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP" 
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
			
			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});
		
		//Update grace in Pass/Fail database
		sql = "Update exam.passfail_staging "
				+ " set writtenscore = writtenscore + 1, "
				+ " gracemarks = '1', "
				+ " total = '40', "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
			
			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = oneGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return oneGraceList.size();
			}
		});
		
		
		//Update score in original mark database.
		sql = "Update exam.marks "
				+ " set writenscore = writenscore + 2, "
				//+ " total = total + 2, "
				+ " gracemarks = '2' "
				+ " , lastModifiedDate = CURRENT_TIMESTAMP"
				+ " where sapid = ? and subject = ? and year = ? and month = ? and writenscore = ? ";
		
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
			
			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getWrittenYear());
				ps.setString(4, m.getWrittenMonth());
				ps.setString(5, m.getWrittenscore());
				//ps.setString(6, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});
		
		//Update grace in Pass/Fail database
		sql = "Update exam.passfail_staging "
				+ " set writtenscore = writtenscore + 2, "
				+ " gracemarks = '2', "
				+ " total = '40', "
				+ " failReason = '', "
				+ " isPass = 'Y' "
				+ " where sapid = ? and subject = ? and grno = ?";
		
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){
			
			@Override
			public void setValues(PreparedStatement ps, int i)	throws SQLException {
				PassFailExamBean m = twoGraceList.get(i);
				ps.setString(1, m.getSapid());
				ps.setString(2, m.getSubject());
				ps.setString(3, m.getGrno());
			}
			public int getBatchSize() {
				return twoGraceList.size();
			}
		});
		
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//
	public Page<PassFailExamBean> getPassFailPage(int pageNo, int pageSize, PassFailExamBean studentMarks, String authorizedCenterCodes) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql ="",countSql="";
		int i = 0;

		/*if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){//Since live flag is to be only verified for AEP logins.
			sql = "SELECT ps.* FROM exam.passfail ps, exam.students s where ps.sapid = s.sapid  ";
			countSql = "SELECT count(*) as count FROM exam.passfail  ps, exam.students s where ps.sapid = s.sapid ";
		}else{
			sql = "SELECT ps.* FROM exam.passfail ps, exam.students s where ps.sapid = s.sapid  ";
			countSql = "SELECT count(*) as count FROM exam.passfail  ps, exam.students s where ps.sapid = s.sapid ";
		}*/

		sql = "SELECT ps.*,s.gender,psub.sifySubjectCode FROM exam.passfail ps,exam.program_subject psub,exam.registration r"
				+ ",exam.consumer_program_structure cps,exam.program_structure p,exam.consumer_type ct,exam.students s where ";
		countSql = "SELECT count(ps.sapid) as count FROM exam.passfail  ps,exam.program_subject psub,exam.registration r"
				+ ",exam.consumer_program_structure cps,exam.program_structure p,exam.consumer_type ct,exam.students s where ";//just count(*) replaced with count(ps.sapid) by Vilpesh on 2021-09-21

		// added to show sifySubjectCode by PS
		sql = sql +" ps.sapid = r.sapid "
				+ " and psub.sem=ps.sem "
				/* + "	and psub.prgmStructApplicable = s.PrgmStructApplicable " */
				+ " and psub.program = ps.program "
				+ " and psub.subject = ps.subject "
				+ " and r.program=ps.program " 
				+ " and r.sem=ps.sem"
				+ " and r.consumerProgramStructureId=cps.id "
				+ " and cps.programStructureId=p.id "
				+ " and psub.prgmStructApplicable=p.program_structure "
				+ " and cps.consumerTypeId=ct.id "
				+ " and s.sapid=r.sapid "
				;
		countSql = countSql +" ps.sapid = r.sapid "
				+ " and psub.sem=ps.sem "
				/* + "	and psub.prgmStructApplicable = s.PrgmStructApplicable " */
				+ " and psub.program = ps.program "
				+ " and psub.subject = ps.subject "
				+ " and r.program=ps.program " 
				+ " and r.sem=ps.sem"
				+ " and r.consumerProgramStructureId=cps.id "
				+ " and cps.programStructureId=p.id "
				+ " and psub.prgmStructApplicable=p.program_structure "
				+ " and cps.consumerTypeId=ct.id "
				+ " and s.sapid=r.sapid "
				;
		
		
		if( studentMarks.getResultProcessedYear() != null &&   !("".equals(studentMarks.getResultProcessedYear())) 
				&& studentMarks.getResultProcessedMonth() != null &&   !("".equals(studentMarks.getResultProcessedMonth()))){
			if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
				sql = sql + " and ((ps.resultProcessedYear = ? or ps.resultProcessedYear = '' )and (ps.resultProcessedMonth = ? or ps.resultProcessedMonth = '' )) ";
				countSql = countSql + " and ((ps.resultProcessedYear = ? or ps.resultProcessedYear = '' )and (ps.resultProcessedMonth = ? or ps.resultProcessedMonth = '' )) ";
			}else{
				sql = sql + " and (ps.resultProcessedYear = ? and ps.resultProcessedMonth = ?) ";
				countSql = countSql + " and (ps.resultProcessedYear = ? and ps.resultProcessedMonth = ?) ";
			}
			parameters.add(studentMarks.getResultProcessedYear());
			parameters.add(studentMarks.getResultProcessedMonth());
		}
		
		if( studentMarks.getWrittenYear() != null && !("".equals(studentMarks.getWrittenYear())) &&
				studentMarks.getWrittenMonth() != null && !("".equals(studentMarks.getWrittenMonth()))){
			/*Commented by Steffi -- sql = sql + " and (ps.writtenyear = ?  and ps.writtenmonth = ?) ";
			countSql = countSql + " and (ps.writtenyear = ?  and ps.writtenmonth = ?) ";*/
			if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
				sql = sql + " and ((ps.writtenyear = ? or ps.writtenyear = '' )and (ps.writtenmonth = ? or ps.writtenmonth = '' )) ";
				countSql = countSql + " and ((ps.writtenyear = ? or ps.writtenyear = '' )and (ps.writtenmonth = ? or ps.writtenmonth = '' )) ";
			}else{
				sql = sql + " and (ps.writtenyear = ? and ps.writtenmonth = ?) ";
				countSql = countSql + " and (ps.writtenyear = ? and ps.writtenmonth = ?) ";
			}
			parameters.add(studentMarks.getWrittenYear());
			parameters.add(studentMarks.getWrittenMonth());
		}
		/*if( studentMarks.getWrittenMonth() != null &&   !("".equals(studentMarks.getWrittenMonth()))){
			sql = sql + " and (ps.writtenmonth = ? OR ps.assignmentmonth = ? ) ";
			countSql = countSql + " and (ps.writtenmonth = ? OR ps.assignmentmonth = ? ) ";
			parameters.add(studentMarks.getWrittenMonth());
			parameters.add(studentMarks.getWrittenMonth());
		}*/

		if( studentMarks.getAssignmentYear() != null &&   !("".equals(studentMarks.getAssignmentYear())) 
				&& studentMarks.getAssignmentMonth() != null &&   !("".equals(studentMarks.getAssignmentMonth()))){
			/*sql = sql + " and (ps.assignmentyear = ? and ps.assignmentmonth = ?) ";
			countSql = countSql + " and (ps.assignmentyear = ? and ps.assignmentmonth = ?) ";*/
			if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
				sql = sql + " and ((ps.assignmentyear = ? or ps.assignmentyear = '' )and (ps.assignmentmonth = ? or ps.assignmentmonth = '' )) ";
				countSql = countSql + " and ((ps.assignmentyear = ? or ps.assignmentyear = '' )and (ps.assignmentmonth = ? or ps.assignmentmonth = '' )) ";
			}else{
				sql = sql + " and (ps.assignmentyear = ? and ps.assignmentmonth = ?) ";
				countSql = countSql + " and (ps.assignmentyear = ? and ps.assignmentmonth = ?) ";
			}
			parameters.add(studentMarks.getAssignmentYear());
			parameters.add(studentMarks.getAssignmentMonth());
		}
		/*if( studentMarks.getAssignmentMonth() != null &&   !("".equals(studentMarks.getAssignmentMonth()))){
			sql = sql + " and (ps.assignmentmonth = ? OR ps.writtenmonth = ?) ";
			countSql = countSql + " and (ps.assignmentmonth = ? OR ps.writtenmonth = ?) ";
			parameters.add(studentMarks.getAssignmentMonth());
			parameters.add(studentMarks.getAssignmentMonth());
		}*/
		if( studentMarks.getGrno() != null &&   !("".equals(studentMarks.getGrno()))){
			sql = sql + " and ps.grno = ? ";
			countSql = countSql + " and ps.grno = ? ";
			parameters.add(studentMarks.getGrno());
		}
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql = sql + " and ps.sapid = ? ";
			countSql = countSql + " and ps.sapid = ? ";
			parameters.add(studentMarks.getSapid());
		}
		if( studentMarks.getName() != null &&   !("".equals(studentMarks.getName()))){
			sql = sql + " and ps.name like  ? ";
			countSql = countSql + " and ps.name like  ? ";
			parameters.add("%"+studentMarks.getName()+"%");
		}
		//consumerType
		if( studentMarks.getConsumerType() != null &&   !("".equals(studentMarks.getConsumerType()))){
			sql = sql + " and ct.name = ? ";
			countSql = countSql + " and ct.name = ? ";
			parameters.add(studentMarks.getConsumerType());
		}
		if( studentMarks.getProgram() != null &&   !("".equals(studentMarks.getProgram()))){
			sql = sql + " and ps.program = ? ";
			countSql = countSql + " and ps.program = ? ";
			parameters.add(studentMarks.getProgram());
		}
		if( studentMarks.getSem() != null &&   !("".equals(studentMarks.getSem()))){
			sql = sql + " and ps.sem = ? ";
			countSql = countSql + " and ps.sem = ? ";
			parameters.add(studentMarks.getSem());
		}
		if( studentMarks.getSubject() != null &&   !("".equals(studentMarks.getSubject()))){
			sql = sql + " and ps.subject = ? ";
			countSql = countSql + " and ps.subject = ? ";
			parameters.add(studentMarks.getSubject());
		}
		if( studentMarks.getIsPass() != null &&   !("".equals(studentMarks.getIsPass()))){
			sql = sql + " and ps.isPass = ? ";
			countSql = countSql + " and ps.isPass = ? ";
			parameters.add(studentMarks.getIsPass());
		}
		if( studentMarks.getGraceGiven() != null  &&   "Y".equals(studentMarks.getGraceGiven())){
			/*sql = sql + " and (gracemarks = '1' or gracemarks = '2' )  ";
			countSql = countSql + " and (gracemarks = '1' or gracemarks = '2' )  ";*/

			sql = sql + " and ps.gracemarks <> ''  ";
			countSql = countSql + " and ps.gracemarks <> ''  ";
		}

		if( studentMarks.getGraceGiven() != null  &&   "N".equals(studentMarks.getGraceGiven())){
			/*sql = sql + " and ((gracemarks <> '1' and gracemarks <> '2') or gracemarks is null )  ";
			countSql = countSql + " and ((gracemarks <> '1' and gracemarks <> '2') or gracemarks is null)  ";*/
			sql = sql + " and (ps.gracemarks = '' or ps.gracemarks is null )  ";
			countSql = countSql + " and (ps.gracemarks = '' or ps.gracemarks is null )   ";
		}

		if( studentMarks.getCenterCode() != null &&   !("".equals(studentMarks.getCenterCode()))){
			sql = sql + " and s.centerCode = ? ";
			countSql = countSql + " and s.centerCode = ? ";
			parameters.add(studentMarks.getCenterCode());
		}

		if(authorizedCenterCodes != null && !"".equals(authorizedCenterCodes.trim())){
			sql = sql + " and s.centerCode in (" + authorizedCenterCodes + ") ";
			countSql = countSql + " and s.centerCode in (" + authorizedCenterCodes + ") ";

			/*sql += " and concat(ps.assignmentyear,ps.assignmentmonth) in (select concat(year,month) from exam.examorder where live = 'Y') "
				+  "  and concat(ps.writtenyear,ps.writtenmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ";

			countSql += " and concat(ps.assignmentyear,ps.assignmentmonth) in (select concat(year,month) from exam.examorder where live = 'Y') "
					 +  "  and concat(ps.writtenyear,ps.writtenmonth) in (select concat(year,month) from exam.examorder where live = 'Y') ";*/

			if("EPBM".equalsIgnoreCase(studentMarks.getProgram()) || "MPDV".equalsIgnoreCase(studentMarks.getProgram()) )
			{
				//do nothing
			}else{
			//For IC and LC, check if pass fail records year, month is less than equal to result declared exam month and year
			//This is done to enure that they do not see results when exam team is uploading marks
			//They will be able to see results only after it is made live
			sql += "and (STR_TO_DATE(concat('01-',assignmentmonth,'-',assignmentyear), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y') "
					+ " or STR_TO_DATE(concat('01-',writtenmonth,'-',writtenyear), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y'))"; 

			countSql += "and (STR_TO_DATE(concat('01-',assignmentmonth,'-',assignmentyear), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y') "
					+ " or STR_TO_DATE(concat('01-',writtenmonth,'-',writtenyear), '%d-%b-%Y') <=   STR_TO_DATE(?, '%d-%b-%Y'))"; 

			parameters.add("01-" + getLiveOnlineExamResultMonth() + "-" + getLiveOnlineExamResultYear());
			parameters.add("01-" + getLiveOnlineExamResultMonth() + "-" + getLiveOnlineExamResultYear());

			}
		}
		
		sql =sql + "";
		
		if( studentMarks.getSapid() != null &&   !("".equals(studentMarks.getSapid()))){
			sql =sql + " group by subject,sem order by ps.sem, ps.subject , ps.sapid asc ";
		}else{
			sql = sql + "order by ps.sem, ps.subject , ps.sapid asc ";
		}

		Object[] args = parameters.toArray();
		PaginationHelper<PassFailExamBean> pagingHelper = new PaginationHelper<PassFailExamBean>();
		Page<PassFailExamBean> page =  pagingHelper.fetchPage(jdbcTemplate, countSql, sql, args, pageNo, pageSize, new BeanPropertyRowMapper(PassFailExamBean.class));


		return page;
	}

	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getRecordsForMarksheet(PassFailExamBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String examMode = studentMarks.getExamMode();
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;

		//This query not used
		String sql = "SELECT * FROM exam.passfail where  program = ? and sem = ? "
				+ " and ((writtenmonth = ? and writtenYear = ?) or (assignmentMonth = ? and assignmentYear  = ?)) ";

		//Select rows from current exam, plus any rows from past exam for same subject
		sql = "select * from exam.passfail   b"
				+ "	inner join"
				+ "	(select distinct sapid,sem from exam.passfail  where  program = ? and sem = ?"
				+ "	and ((writtenmonth = ? and writtenYear = ?) or (assignmentMonth = ? and assignmentYear  = ?)) ) a"
				+ "	on a.sapid = b.sapid"
				+ "	and b.sem = a.sem ";

		parameters.add(studentMarks.getProgram());
		parameters.add(studentMarks.getSem());
		parameters.add(studentMarks.getWrittenMonth());
		parameters.add(studentMarks.getWrittenYear());
		parameters.add(studentMarks.getWrittenMonth());
		parameters.add(studentMarks.getWrittenYear());


		if(examMode != null & !"".equals(examMode.trim())){
			sql =  sql + " and b.sapid in (select sapid from exam.exambookings where program = ? and sem = ? "
					+ " and year = ?  and  month = ? and booked = 'Y' and exammode = ?) ";
			parameters.add(studentMarks.getProgram());
			parameters.add(studentMarks.getSem());
			parameters.add(studentMarks.getWrittenYear());
			parameters.add(studentMarks.getWrittenMonth());
			parameters.add(studentMarks.getExamMode());
		}

		sql = sql + " order by b.sapid, b.subject";


		Object[] args = parameters.toArray();
		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(PassFailExamBean.class));

		return studentsMarksList;
	}

	@Transactional(readOnly = true)//
	public List<PassFailExamBean> getStudentMarksData(PassFailExamBean studentMarks) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//Object[] args = new Object[]{};
		ArrayList<Object> parameters = new ArrayList<Object>();

		int i = 0;


		String sql = "SELECT * FROM exam.passfail where  sapid = ? and sem = ? "
				+ " and ((writtenmonth = ? and writtenYear = ?) or (assignmentMonth = ? and assignmentYear  = ?)) ";

		sql = "select * from exam.passfail   b"
				+ "	inner join"
				+ "	(select distinct sapid,sem from exam.passfail  where   sapid = ? and sem = ?"
				+ "	and ((writtenmonth = ? and writtenYear = ?) or (assignmentMonth = ? and assignmentYear  = ?)) ) a"
				+ "	on a.sapid = b.sapid"
				+ "	and b.sem = a.sem ";



		List<PassFailExamBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{
				studentMarks.getSapid(),
				studentMarks.getSem(),
				studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear(),
				studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear()
		}, new BeanPropertyRowMapper(PassFailExamBean.class));

		return studentsMarksList;
	}

	@Transactional(readOnly = true)//
	public HashMap<String, MarksheetBean> getStudentsData(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);


		String sql = "SELECT students.sapid,"
				+ "    students.sem,"
				+ "    students.lastName,"
				+ "    students.firstName,"
				+ "    students.middleName,"
				+ "    students.fatherName,"
				+ "    students.husbandName,"
				+ "    students.motherName,"
				+ "    students.gender,"
				+ "    students.program,"
				+ "    students.enrollmentMonth,"
				+ "    students.enrollmentYear,"
				+ "    students.emailId,"
				+ "    students.mobile,"
				+ "    students.altPhone,"
				+ "    students.dob,"
				+ "    students.regDate,"
				+ "    students.isLateral,"
				+ "    students.isReReg,"
				+ "    students.address,"
				+ "    students.city,"
				+ "    students.state,"
				+ "    students.country,"
				+ "    students.pin,"
				+ "    students.centerCode,"
				+ "    students.centerName,"
				+ "    students.validityEndMonth,"
				+ "    students.validityEndYear, "
				+ "    students.PrgmStructApplicable "
				+ "    FROM exam.students, exam.passfail b where "
				+ "    students.sapid = b.sapid "
				//+ "    and b.writtenmonth = ? "
				//+ "    and b.writtenYear = ? "
				+ "    and b.sem = ? "
				+ "    and b.program = ? "
				+ "    and ((b.writtenmonth = ? and b.writtenYear = ?) or (b.assignmentMonth = ? and b.assignmentYear = ?))";



		List<MarksheetBean> studentsMarksList = jdbcTemplate.query(sql, new Object[]{
				bean.getSem(), 
				bean.getProgram(),
				bean.getWrittenMonth(), 
				bean.getWrittenYear(),
				bean.getWrittenMonth(), 
				bean.getWrittenYear()
		}, new BeanPropertyRowMapper(MarksheetBean.class));

		HashMap<String, MarksheetBean> studentsMap = new HashMap<>();
		for (int i = 0; i < studentsMarksList.size(); i++) {
			MarksheetBean b = studentsMarksList.get(i);
			b.setExamMonth(bean.getWrittenMonth());
			b.setExamYear(bean.getWrittenYear());
			studentsMap.put(b.getSapid().trim(), b);

		}
		return studentsMap;
	}

	@Transactional(readOnly = true)//
	public MarksheetBean getSingleStudentsData(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		MarksheetBean student = null;
		try{
			String sql = "SELECT *   FROM exam.students where "
					+ "    sapid = ? "
					+ "    and sem = (Select max(sem) from exam.students where sapid = ? )";



			student = (MarksheetBean)jdbcTemplate.queryForObject(sql, new Object[]{
					bean.getSapid(),
					bean.getSapid()
			}, new BeanPropertyRowMapper(MarksheetBean.class));
		}catch(Exception e){
			
		}
		return student;
	}

	@Transactional(readOnly = true)//
	public String getOnlineExamDeclarationDate(String month, String year) {

		String declareDate = null,decDate="";

		String sql = "Select declareDate from exam.examorder where month = ? and year = ?";
		Date d = new Date();
		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql, new Object[]{month, year},String.class);
		hashMap = new HashMap<String, Integer>();

		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");

		try {
			d = sdfr1.parse(decDate);
			declareDate = sdfr2.format(d);
		} catch (Exception e) {
			declareDate = "Not Available";
		}
		return declareDate;
	}
	
	@Transactional(readOnly = true)//
	public String getOnlineExamDeclarationDateForExecutive(String month, String year,String acadMonth, String acadYear) {

		String declareDate = null,decDate="";

		String sql = "Select resultDeclareDate from exam.executive_examorder "
					 + " where month = ? "
					 + " and year = ? "
					 + " and acadMonth =? "
					 + " and acadYear =? ";
		Date d = new Date();
		jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			decDate = (String) jdbcTemplate.queryForObject(sql, new Object[]{month, year,acadMonth,acadYear},String.class);
			decDate = decDate.split(" ", -1)[0]; 
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			
			return "Not Available";
		}
		hashMap = new HashMap<String, Integer>();

		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");

		try {
			d = sdfr1.parse(decDate);
			declareDate = sdfr2.format(d);
		} catch (Exception e) {
			
			declareDate = "Not Available";
		}
		return declareDate;
	}

	@Transactional(readOnly = true)//
	public ExamOrderExamBean getExamDetails(String month, String year) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ExamOrderExamBean exam = null;

		String sql = "select * from exam.examorder where month = ? and year = ?";
		try {
			exam = (ExamOrderExamBean)jdbcTemplate.queryForObject(sql, new Object[]{month, year},
					new BeanPropertyRowMapper(ExamOrderExamBean.class));
		} catch (Exception e) {
			return null;
		}
		return exam;
	}
	
	@Transactional(readOnly = true)//
	public ExecutiveExamOrderBean getExamDetailsForExecutive(String examMonth, String examYear,String acadMonth, String acadYear) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ExecutiveExamOrderBean exam = null;

		String sql = "select * from exam.executive_examorder"
					 + " where month = ? "
					 + " and year = ? "
					 + " and acadMonth = ? "
					 + " and acadYear = ?";
		try {
			exam = (ExecutiveExamOrderBean)jdbcTemplate.queryForObject(sql, new Object[]{examMonth, examYear,acadMonth,acadYear},
					new BeanPropertyRowMapper(ExecutiveExamOrderBean.class));
		} catch (Exception e) {
			
			return null;
		}
		return exam;
	}

	@Transactional(readOnly = true)//
	public String getOfflineExamDeclarationDate(String month, String year) {

		String declareDate = null,decDate="";

		String sql = "Select oflineResultsDeclareDate from exam.examorder where month = ? and year = ?";
		Date d = new Date();
		jdbcTemplate = new JdbcTemplate(dataSource);

		decDate = (String) jdbcTemplate.queryForObject(sql, new Object[]{month, year},String.class);

		SimpleDateFormat sdfr1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfr2 = new SimpleDateFormat("dd-MMM-yyyy");

		try {
			d = sdfr1.parse(decDate);
			declareDate = sdfr2.format(d);
		} catch (Exception e) {
			declareDate = "Not Available";
		}
		return declareDate;
	}

	@Transactional(readOnly = true)//
	public ArrayList<AssignmentFileBean> getFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";
		String sql = "select p.subject,p.program,p.sem,p.assignmentscore from exam.passfail p " + 
				"inner join exam.students s on s.sapid = p.sapid " + 
				"inner join exam.program_sem_subject p_s_s on p_s_s.consumerProgramStructureId = s.consumerProgramStructureId and p_s_s.subject = p.subject and p_s_s.sem = p.sem " + 
				"where p.isPass = 'N' and p.sapid = ?; ";

		ArrayList<AssignmentFileBean> subjectsList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentFileBean.class));

		return subjectsList;
	}

	@Transactional(readOnly = true)//
	public ArrayList<AssignmentFileBean> getUGFailSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";
		String sql = "select p.subject,p_s_s.sem, p.remarks, p.status from remarkpassfail.passfail p " + 
				"inner join exam.program_sem_subject p_s_s on p_s_s.id = p.programSemSubjectId " + 
				"where p.isPass < 1 AND p.active = 'Y' and p.sapid = ?; ";

		ArrayList<AssignmentFileBean> subjectsList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentFileBean.class));

		return subjectsList;
	}

	@Transactional(readOnly = true)//
	public List<String> getUGPassSubjectsForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select subject, program, sem from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";
		String sql = "select p.subject from remarkpassfail.passfail p " +  
				"where p.isPass > 0 AND p.active = 'Y' and p.sapid = ?; ";

		return jdbcTemplate.queryForList(sql, new Object[]{sapid}, String.class);
	}

	@Transactional(readOnly = true)//
	public ArrayList<String> getPassSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		//String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";
		
		String sql =  "select pf.subject from exam.passfail pf, exam.examorder eo where pf.isPass = 'Y' and pf.sapid = ? "
					  +" and pf.writtenMonth=eo.month "
					  +" and pf.writtenYear= eo.year "
					  +" and eo.order <= (  "
					  +" select max(e2.order) from exam.examorder e2 where e2.assignmentMarksLive='Y'  "
					  +" )  "
					  +" order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		if(subjectsList.contains("Business Communication and Etiquette")){
			subjectsList.add("Business Communication");
		}
		return subjectsList;
	}

	@Transactional(readOnly = true)//
	public ArrayList<String> getPassSubjectsNamesForSingleStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'Y' and sapid = ? order by sem  asc ";
		
		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getSubjectwithPassFailEntry(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where sapid = ? order by sem  asc ";
		
		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getFailSubjectsNamesForAStudent(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.passfail where isPass = 'N' and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}

	public static void main(String[] args) {
		try {
			double examOrder = Double.parseDouble("1.0");
		} catch (Exception e) {
			
		}
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String,ArrayList<PassFailExamBean>> getMapOfSapIdAndSemAndPassFailList(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String,ArrayList<PassFailExamBean>> mapOfKeyAndPassFailRecords = new HashMap<String,ArrayList<PassFailExamBean>>();
		String sql = " select * from exam.passfail ";
		String key = "";
		ArrayList<PassFailExamBean> passFailList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper(PassFailExamBean.class));
		for(PassFailExamBean passFail : passFailList){
			key = passFail.getSapid() + "-" + passFail.getSem();
			if(!mapOfKeyAndPassFailRecords.containsKey(key)){
				ArrayList<PassFailExamBean> list = new ArrayList<>();
				list.add(passFail);
				mapOfKeyAndPassFailRecords.put(key, list);

			}else{
				ArrayList<PassFailExamBean> list = (ArrayList)mapOfKeyAndPassFailRecords.get(key);
				list.add(passFail);
				mapOfKeyAndPassFailRecords.put(key, list);
			}
		}
		return mapOfKeyAndPassFailRecords;
	}

	@Transactional(readOnly = true)//
	public ArrayList<PassFailExamBean> getPassFailRecords(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where sapid = ? order by sem, subject  asc ";

		ArrayList<PassFailExamBean> passFailList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return passFailList;
	}

	@Transactional(readOnly = true)//
	public ArrayList<PassFailExamBean> getPassRecords(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where sapid = ? order by sem, subject  asc ";

		ArrayList<PassFailExamBean> passList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return passList;
	}

	@Transactional(readOnly = true)//
	public ArrayList<PassFailExamBean> getPassRecordsForStudentSelfTranscript(String sapId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = ""
				+ " SELECT `pf`.* "
				+ " FROM `exam`.`passfail` `pf` "
				+ " INNER JOIN `exam`.`examorder` `eo` "
					+ " ON `eo`.`year` = `pf`.`resultProcessedYear` AND `eo`.`month` = `pf`.`resultProcessedMonth`  "
				+ " WHERE `sapid` = ? "
				+ " AND `eo`.`live` = 'Y' AND `eo`.`assignmentMarksLive` = 'Y' "
				+ " ORDER BY `sem`, `subject` ASC ";

		ArrayList<PassFailExamBean> passList = (ArrayList<PassFailExamBean>)jdbcTemplate.query(sql, new Object[]{sapId}, new BeanPropertyRowMapper(PassFailExamBean.class));
		return passList;
	}

	@Transactional(readOnly = true)//
	public String getWrittenLastPassExamMonth(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<String> examMonthYearList = null;
		String sql = "select concat(writtenmonth, '-', writtenyear) from exam.passfail p , exam.examorder eo where "
				+ " p.writtenmonth = eo.month and p.writtenyear = eo.year and  sapid = ? "
				+ " order by eo.order desc limit 1";
		try {
			examMonthYearList = (ArrayList<String>) jdbcTemplate.query(sql,  new Object[]{sapid}, new SingleColumnRowMapper(String.class));
		} catch (Exception e) {
			
		}

		return examMonthYearList.get(0);
	}

	@Transactional(readOnly = true)//
	public String getAssignmentLastPassExamMonth(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String examMonthYearList = "";
		String sql = "select concat(assignmentMonth, '-', assignmentYear) from exam.passfail p , exam.examorder eo where "
				+ " p.assignmentMonth = eo.month and p.assignmentYear = eo.year and  sapid = ? "
				+ " order by eo.order desc limit 1";
		try {
			examMonthYearList = (String) jdbcTemplate.queryForObject(sql,  new Object[]{sapid}, String.class);
		} catch (Exception e) {
			
		}

		return examMonthYearList;
	}
		
	@Transactional(readOnly = true)//
	public double getLastWrittenPassExamMonthYear(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		
		String sql = "select eo.order from exam.examorder eo, exam.passfail p "
				+ " where p.writtenmonth = eo.month and p.writtenyear = eo.year and  sapid = ? "
				+ " order by eo.order desc limit 1";
		try {
			examOrder = jdbcTemplate.queryForObject(sql, new Object[]{sapid}, Double.class);
		} catch (Exception e) {
			
		}
		
		return examOrder;
	}
	
	@Transactional(readOnly = true)//
	public double getLastAssignmentPassExamMonthYear(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		double examOrder =0.0;
		
		String sql = "select eo.order from exam.examorder eo, exam.passfail p "
				+ " where p.assignmentMonth = eo.month and p.assignmentYear = eo.year and  sapid = ? "
				+ " order by eo.order desc limit 1";
		try {
			examOrder = jdbcTemplate.queryForObject(sql, new Object[]{sapid}, Double.class);
		} catch (Exception e) {
			
		}
		
		return examOrder;
	}

	@Transactional(readOnly = true)//
	public List<MarksheetBean> getStudentsForSR(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());

		
		// commented by sachin 
		/*String sql = "select s.*, sr.sem, sr.year, sr.month, sr.additionalInfo1 from exam.students s, portal.service_request sr "
				+ " where sr.sapid = s.sapid "
				+ " and s.sem = (select max(s2.sem) from exam.students s2 where s2.sapid = s.sapid) "
				+ " and   sr.id in ( "+ commaSeparatedList +" )  order by s.centerCode asc ";*/
		
		//Added by sachin for making pdf in order of SRID
		String sql = "select s.*,sr.id as serviceRequestId, sr.sem, sr.year, sr.month, sr.additionalInfo1 from exam.students s, portal.service_request sr "
				+ " where sr.sapid = s.sapid "
				+ " and s.sem = (select max(s2.sem) from exam.students s2 where s2.sapid = s.sapid) "
				+ " and   sr.id in ( "+ commaSeparatedList +" )  order by field(id, " + commaSeparatedList + " ) ";
		
		ArrayList<MarksheetBean> studentList = (ArrayList<MarksheetBean>)jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(MarksheetBean.class));
		
		return studentList;
	}
	
	@Transactional(readOnly = true)//
	public String getResultDeclareDateForSingleStudent(MarksheetBean bean){
		String sql ="";
		jdbcTemplate = new JdbcTemplate(dataSource);
		if("Online".equals(bean.getExamMode())){
			sql = "SELECT declareDate FROM exam.examorder where examorder.order = (Select max(eo.order) from exam.examorder eo, exam.marks m"
					+" where eo.year = m.year and eo.month = m.month and m.sapid = ?)";
		}else{
			sql = "SELECT oflineResultsDeclareDate FROM exam.examorder where examorder.order = (Select max(eo.order) from exam.examorder eo, exam.marks m"
					+" where eo.year = m.year and eo.month = m.month and m.sapid = ?)";
		}
		String declareDate = (String)jdbcTemplate.queryForObject(sql, new Object[]{bean.getSapid()}, String.class);
		return declareDate;
	}

	@Transactional(readOnly = true)//
	public MarksheetBean getStudentForSR(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select s.*, sr.sem, sr.year, sr.month, sr.additionalInfo1 from exam.students s, portal.service_request sr where sr.sapid = s.sapid "
				+ " and   sr.id =?  order by s.centerCode asc ";
		MarksheetBean student = (MarksheetBean)jdbcTemplate.queryForObject(sql, new Object[]{bean.getServiceRequestId()},new BeanPropertyRowMapper(MarksheetBean.class));

		return student;
	}

	@Transactional(readOnly = true)//
	public HashMap<String,String> getMapOfSapIdAndResultDeclareDate(List<MarksheetBean> markList){
		
		HashMap<String,String> mapOfSapIdAndResultDate = new HashMap<String,String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		for(MarksheetBean bean : markList){
			try {
			String sql = "";
			if("Online".equals(bean.getExamMode())){
				if("EPBM".equalsIgnoreCase(bean.getProgram()) || "MPDV".equalsIgnoreCase(bean.getProgram())) {
						/*sql = "SELECT resultDeclareDate FROM exam.executive_examorder e"
								+ "  where e.order = "
								+ "		(Select max(eo.order)"
								+ "		 from exam.executive_examorder eo, exam.marks m,exam.students s "
								+ " 		where eo.year = m.year and eo.month = m.month"
								+ "		 and eo.acadYear = s.enrollmentYear and eo.acadMonth = s.enrollmentMonth "
								+ "		 and s.sapid = m.sapid "
								+ "		 and m.sapid = ?)";*/
					/*sql = "SELECT resultDeclareDate FROM exam.executive_examorder e"
						+ "  where e.order = "
						+ "		(Select max(eo.order)"
						+ "		 from exam.executive_examorder eo, exam.marks m,exam.students s "
						+" 		 where eo.year = m.year and eo.month = m.month"
						+ "		 and eo.acadYear = s.enrollmentYear and eo.acadMonth = s.enrollmentMonth "
						+ "		 and s.sapid = m.sapid "
-							+ "		 and m.sapid = ?)";
+							+ "		 and m.sapid = ?)"; 
+							Commented but kept as logic to be discussed with sanket sir
+							*/
					sql = "SELECT resultDeclareDate FROM exam.executive_examorder e"
							+ "  where e.order = "
							+ "		(Select max(eo.order)"
							+ "		 from exam.executive_examorder eo,exam.students s "
							+" 		 where resultLive = 'Y '"
							+ "		 and eo.acadYear = s.enrollmentYear and eo.acadMonth = s.enrollmentMonth "
							+ "		 and s.sapid = ?)";
				}else {
				sql = "SELECT declareDate FROM exam.examorder where examorder.order = (Select max(eo.order) from exam.examorder eo, exam.marks m"
						+" where eo.year = m.year and eo.month = m.month and m.sapid = ? and m.processed='Y')";
			  }
			}else{
				sql = "SELECT oflineResultsDeclareDate FROM exam.examorder where examorder.order = (Select max(eo.order) from exam.examorder eo, exam.marks m"
						+" where eo.year = m.year and eo.month = m.month and m.sapid = ? and m.processed='Y')"; 
			}
			String resultDeclareDate = (String)jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
			mapOfSapIdAndResultDate.put(bean.getSapid(), resultDeclareDate);
			}catch(Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.error("Error causing SRID:"+bean.getServiceRequestId());
				logger.error("Error: "+errors.toString());
//				e.printStackTrace();
			}
		}
		logger.info(" number of valid declareddate and sapid:"+mapOfSapIdAndResultDate.size());
		
		return mapOfSapIdAndResultDate;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String,String> getMapOfSapIdAndResultDeclareDateForMbawx(List<MarksheetBean> markList){
		
		HashMap<String,String> mapOfSapIdAndResultDate = new HashMap<String,String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		for(MarksheetBean bean : markList){
			String sql = "";
			  
			sql = "SELECT  max(lastModifiedDate) from  exam.mba_passfail where sapid=? ";
				 
			 
			String resultDeclareDate = (String)jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
			mapOfSapIdAndResultDate.put(bean.getSapid(), resultDeclareDate);


		}
		return mapOfSapIdAndResultDate;
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String,String> getMapOfSapIdAndResultDeclareDateForMbax(List<MarksheetBean> markList){
		
		HashMap<String,String> mapOfSapIdAndResultDate = new HashMap<String,String>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		for(MarksheetBean bean : markList){
			String sql = "";
			  
			sql = "SELECT  max(lastModifiedDate) from  exam.mbax_passfail where sapid=? ";
				 
			 
			String resultDeclareDate = (String)jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
			mapOfSapIdAndResultDate.put(bean.getSapid(), resultDeclareDate);


		}
		return mapOfSapIdAndResultDate;
	}
	
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateSRStatus(PassFailExamBean bean, String status) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());

		String sql = "Update portal.service_request set requestStatus = ? where id in ( "+ commaSeparatedList +" )  ";
		jdbcTemplate.update(sql, new Object[]{status});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateSRWithCertificateNumberAndCurrentDate(PassFailExamBean bean, String certificateNumber) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String commaSeparatedList = generateCommaSeparatedList(bean.getServiceRequestIdList());

		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id in ( "+ commaSeparatedList +" )  ";
		jdbcTemplate.update(sql, new Object[]{certificateNumber});

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//
	public void updateSRWithCertificateNumberAndCurrentDateForSingleStudent(PassFailExamBean bean, String certificateNumber) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "Update portal.service_request set certificateNumber = ? , certificateGenerationDate = sysdate() where id = ?";
		jdbcTemplate.update(sql, new Object[]{certificateNumber,bean.getServiceRequestId()});

	}

	@Transactional(readOnly = true)//
	public ArrayList<AssignmentFileBean> getANSNotProcessed(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject, program, sem from exam.marks where assignmentscore = 'ANS' and "
				+ " (processed <> 'Y' or processed is null) and sapid = ? order by sem  asc ";

		ArrayList<AssignmentFileBean> subjectsList = (ArrayList<AssignmentFileBean>)jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(AssignmentFileBean.class));

		return subjectsList;
	}
	
	@Transactional(readOnly = true)//
	public ArrayList<String> getANSNotProcessedSubjectNames(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject from exam.marks where assignmentscore = 'ANS' and  (processed <> 'Y' or processed is null) and sapid = ? order by sem  asc ";

		ArrayList<String> subjectsList = (ArrayList<String>)jdbcTemplate.query(sql, new Object[]{sapid}, new SingleColumnRowMapper(String.class));

		return subjectsList;
	}

	@Transactional(readOnly = true)//
	public ArrayList<StudentExamBean> getProgramWiseSubjects(String program){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select subject,sem from exam.program_subject where program = ? and active = 'Y' ";

		ArrayList<StudentExamBean> subjectsList = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql, new Object[]{program}, new BeanPropertyRowMapper(StudentExamBean.class));

		return subjectsList;
	}

	@Transactional(readOnly = true)//
	public HashMap<String,ArrayList<String>> getSemWiseSubjectsMap(String programName,String prgramStruct) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = null;
		ArrayList<StudentExamBean> subjectList = null;
		//List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();

		sql = "SELECT * FROM exam.program_subject where program = ?  and prgmStructApplicable = ? and active = 'Y' order by subject asc";
		subjectList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,new Object[]{programName,prgramStruct}, new BeanPropertyRowMapper(StudentExamBean.class));

		if(subjectList.isEmpty()){
			sql = "SELECT * FROM exam.program_subject where program = ?  and prgmStructApplicable = ? and active = 'N' order by subject asc";
			subjectList = (ArrayList<StudentExamBean>) jdbcTemplate.query(sql,new Object[]{programName,prgramStruct}, new BeanPropertyRowMapper(StudentExamBean.class));
		}

		HashMap<String,ArrayList<String>> semWiseSubjectMap = new HashMap<>();
		for(StudentExamBean student : subjectList){
			if(!semWiseSubjectMap.containsKey(student.getSem())){
				semWiseSubjectMap.put(student.getSem(), new ArrayList<String>());
			}
			semWiseSubjectMap.get(student.getSem()).add(student.getSubject());
		}
		return semWiseSubjectMap;
	}

	@Transactional(readOnly = true)//
	public MarksheetBean getSingleStudentForSR(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select s.*, sr.sem, sr.year, sr.month, sr.additionalInfo1 from exam.students s, portal.service_request sr where sr.sapid = s.sapid "
				+ " and   sr.id =?  order by s.sem desc limit 1";
		MarksheetBean student = (MarksheetBean)jdbcTemplate.queryForObject(sql, new Object[]{bean.getServiceRequestId()},new BeanPropertyRowMapper(MarksheetBean.class));

		return student;
	}

	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getStudentsForReport(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		HashMap<String, ArrayList> keysMap = new HashMap<>();
		String sql = "SELECT * FROM exam.marks ";	
		
		try {
			ArrayList<StudentMarksBean> studentList = (ArrayList<StudentMarksBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(StudentMarksBean.class));
			List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
			
			for (int i = 0 ; i < studentList.size(); i++) {
				StudentMarksBean bean = (StudentMarksBean)studentList.get(i);
				String key = bean.getSapid().trim()+bean.getSubject().trim();
				if(!keysMap.containsKey(key)){
					ArrayList<StudentMarksBean> list = new ArrayList<>();
					list.add(bean);
					keysMap.put(key, list);
					uniqueStudentList.add(bean);
				}else{
					ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
					list.add(bean);
					keysMap.put(key, list);
				}
				
				if(i % 10000 == 0){
				}
				
			}
		} catch (Exception e) {
			
		}
		
		return keysMap;	
	}
	
	@Transactional(readOnly = true)//
	public HashMap<String, PassFailExamBean> getPassFailData(){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT * FROM exam.passfail ";
		ArrayList<PassFailExamBean> passFailData = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql, new BeanPropertyRowMapper(PassFailExamBean.class));
		
		HashMap<String, PassFailExamBean> passFailMap = new HashMap<>();
		for (PassFailExamBean bean : passFailData) {
			String key = bean.getSapid()+"-"+bean.getSubject();
			passFailMap.put(key, bean);
		}
		return passFailMap;
	}
	
	public ArrayList<StudentMarksBean> processForReport(HashMap<String, ArrayList> keysMap) {
		
		ArrayList<StudentMarksBean> passFailStudentList = new ArrayList<StudentMarksBean>();

		int newMarks = 0;
		int oldTotal = 0;
		int minPassScoreRequired = 0;
		
		Iterator entries = keysMap.entrySet().iterator();
		
		ArrayList<StudentMarksBean> newPassFailStudent = new ArrayList<>();
		HashMap<String,Integer> programSubjectPassScoreMap = getProgramSubjectPassScoreMap();
		HashMap<String, StudentExamBean> studentsMap = getAllStudents();
		HashMap<String, PassFailExamBean> passFailMap = getPassFailData();
		
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();
			PassFailExamBean passFailBean = new PassFailExamBean();
			try {
				StudentMarksBean currentBean = currentList.get(0);
				String sapId = currentBean.getSapid();
				String sapIdSubjectMap = currentBean.getSapid()+"-"+currentBean.getSubject();
				StudentExamBean student = studentsMap.get(sapId);
				PassFailExamBean passFail = passFailMap.get(sapIdSubjectMap);
				String programSubjectProgramStructureKey = student.getProgram()+"-"+currentBean.getSubject()+"-"+student.getPrgmStructApplicable();
				minPassScoreRequired = programSubjectPassScoreMap.get(programSubjectProgramStructureKey);
				
				int bestWrittenmarks = getBestWrittenScore(currentList, passFailBean);
				currentBean.setWritenscore(bestWrittenmarks+"");
				currentBean.setOldWrittenScore(passFailBean.getLastWrittenscore());
				
				int bestAssignmentMarks = calculateBestAssignmentScore(currentList, passFailBean);
				currentBean.setAssignmentscore(bestAssignmentMarks+"");
				currentBean.setOldAssignmentScore(passFailBean.getLastAssignmentscore());
				
				newMarks = bestWrittenmarks + bestAssignmentMarks;
				oldTotal = bestAssignmentMarks + Integer.parseInt(passFailBean.getLastWrittenscore());
				newMarks = bestWrittenmarks + bestAssignmentMarks;
				currentBean.setNewTotal(newMarks+"");				
				currentBean.setTotal(oldTotal+"");
				
				if (newMarks >= minPassScoreRequired) {
					if ("Y".equals(passFail.getIsPass())) {
						currentBean.setOldStatus("Pass");
					}else if("N".equals(passFail.getIsPass())){
						currentBean.setOldStatus("Fail");
					}
					currentBean.setNewStatus("Pass");
					newPassFailStudent.add(currentBean);
				}
				else {
					if ("Y".equals(passFail.getIsPass())) {
						currentBean.setOldStatus("Pass");
					}else if("N".equals(passFail.getIsPass())){
						currentBean.setOldStatus("Fail");
					}
					currentBean.setNewStatus("Fail");
					newPassFailStudent.add(currentBean);

				}
				
			} catch (Exception e) {
				
			}
			
		}
		
		return newPassFailStudent;	
	}
	 
	//Added by Stef
	@Transactional(readOnly = false)//
	public List<StudentMarksBean> getSingleStudentPassFailMarksData(String sapid, String program) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentMarksBean> studentsMarksList = new ArrayList<StudentMarksBean>();
		
		String sql = "SELECT m.*,p.total as totalMarks FROM exam.passfail p,exam.marks m "
				+ " where  "
				+ " p.sapid = m.sapid and "
				+ " p.program= m.program and "
				+ " p.subject = m.subject and "
				+ " p.sapid = ? and "
				+ " p.isPass = 'N'  and "
				+ " p.writtenMonth = m.month and "
				+ " p.writtenYear = m.year and "
				+ " p.writtenscore = m.writenscore and "
				+ " p.program = ? ";
		
		try{
		  studentsMarksList = jdbcTemplate.query(sql, new Object[]{
				sapid ,program
		}, new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
			return studentsMarksList;
		}
		return studentsMarksList;
	}

	@Transactional(readOnly = true)//
	public AssignmentFileBean getAssigmentRemarksForSingleStudentYearMonth(String sapid,String month,String year,String subject){
		AssignmentFileBean remarks = new AssignmentFileBean();
		String sql="Select sapid,finalReason,reason from exam.assignmentsubmission where sapid=? and month =? and year=? and subject=?";
		jdbcTemplate = new JdbcTemplate(dataSource);
		try{
			remarks=(AssignmentFileBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid ,month, year, subject
			},new BeanPropertyRowMapper(AssignmentFileBean.class));
			
		}catch(Exception e){
			
		}
		
		return remarks;
	}

	@Transactional(readOnly = true)//
	public HashMap<String, ArrayList> getPendingRecordsForPassFailForBajajForASem(StudentMarksBean studentMarks) {
		String sql = "select * from exam.marks where sapid = ? and sem = ? "
				+ " order by examorder * 1 asc";
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, new Object[]{studentMarks.getSapid(), studentMarks.getSem()},
				new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = false)//
	public HashMap<String, ArrayList> getMarksRecordsForStudentForASubject(StudentMarksBean student){
		//Transaction - readOnly changed to false, (Master Slave DB) Observed during June22, giving grace.
		//calling /applyGraceforValidityEnd, grace got added in Marks table but not in Passfail table. - Vilpesh 20220805 
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select m.* from exam.marks m "
				+ " where  m.sapid = ? and m.subject = ? ";

		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
				new Object[]{ student.getSapid(), student.getSubject()},
				new BeanPropertyRowMapper(StudentMarksBean.class));

		List<StudentMarksBean> uniqueStudentList = new ArrayList<>();
		HashMap<String, ArrayList> keysMap = new HashMap<>();

		for(int i = 0 ; i < studentMarksList.size(); i++){
			StudentMarksBean bean = (StudentMarksBean)studentMarksList.get(i);
			String key = bean.getSapid().trim()+bean.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<StudentMarksBean> list = new ArrayList<>();
				list.add(bean);
				keysMap.put(key, list);
				uniqueStudentList.add(bean);
			}else{
				ArrayList<StudentMarksBean> list = (ArrayList)keysMap.get(key);
				list.add(bean);
				keysMap.put(key, list);
			}
			if(i % 10000 == 0){
			}
		}
		// TODO Auto-generated method stub
		return keysMap;

	}
	
	@Transactional(readOnly = false)//
	public HashMap<String, ArrayList> getMarksRecords(String sapid, String subject){
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select m.* from exam.marks m  where  m.sapid = ? and m.subject = ? ";
		
		List<StudentMarksBean> studentMarksList = jdbcTemplate.query(sql, 
				new Object[]{ sapid, subject},
				new BeanPropertyRowMapper(StudentMarksBean.class));
		
		HashMap<String, ArrayList> keyMaps = new HashMap<>();
		
		String key = sapid.trim() + subject.trim();
		
		keyMaps.put(key, new ArrayList<>(studentMarksList));
		
		return keyMaps;
		
	}	

	@Transactional(readOnly = true)//
	public boolean  hasAppearedForExamForGivenSemMonthYearPreviewMarksheet(MarksheetBean passFail, String examMode){
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "Select count(*) from exam.marks m, exam.examorder eo  "
				+ " where sapId = ?"
				+ " and m.year = eo.year "
				+ " and m.month = eo.month "
				+ " and m.month = ? "
				+ " and m.year = ? "
				+ " and m.sem = ? ";
		if("Online".equals(examMode)){
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where live = 'Y')";
		}else{
			sql += " and eo.order <= (select max(examorder.order) from exam.examorder where oflineResultslive = 'Y')";
		}
		int count = (int) jdbcTemplate.queryForObject(sql, new Object[]{passFail.getSapid(),passFail.getExamMonth(),passFail.getExamYear(),passFail.getSem()},Integer.class);
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	//PassFail Dash board Start//
	/*public int getPendingRecordsForProject() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available' "
				+ " and subject ='Project'";
		try{
			count = (int) jdbcTemplate.queryForObject(sql,new Object[]{},Integer.class);
		}catch(Exception e){
			
		}
		return count;
	}*/
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineProject(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentMarksBean> studentList =  new ArrayList<StudentMarksBean>() ;
		/*String sql = "select m.*,s.PrgmStructApplicable as programStructApplicable "
				+ " from exam.marks m, exam.students s "
				+ " where m.sapid= s.sapid "
				+ " and (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available' "
				+ " and subject ='Project'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select * "
									+ " from exam.marks m "
									+ " where (m.processed <> 'Y' OR m.processed is null) "
									+ " and m.sapid <> 'Not Available' "
									//+ " and m.subject IN ('Project', 'Module 4 - Project')"
									+ " and m.subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Vilpesh on 2022-03-04
											+ "  ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		

	    Object [] args = parameters.toArray();
		
		try{
		studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			logger.error("PassFailDAO : getPendingRecordsForOnlineOfflineProject : "+e.getMessage());
		}
		return studentList;
	}

	@Transactional(readOnly = true)//
	public int getPendingCountForAbsent(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> studentList=new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and writenscore = 'AB'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select count(m.id) "
									+ " from exam.marks m, exam.students s "
									+ " where m.sapid= s.sapid "
									+ " and (m.processed <> 'Y' OR m.processed is null) "
									+ " and m.sapid <> 'Not Available' "
									+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //AB in Project exclusion -Vilpesh 20220720
									+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
									+ " and m.writenscore = 'AB'"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			//studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return count;
	}
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingRecordsForOnlineOffline(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentMarksBean> studentList =  new ArrayList<StudentMarksBean>() ;
		/*String sql = "select m.*,s.PrgmStructApplicable as programStructApplicable from exam.marks m, exam.students s where m.sapid= s.sapid "
				+ " and (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available'";*/
		ArrayList<Object> parameters = new ArrayList<Object>();
//		StringBuffer sql = new StringBuffer("select m.*,s.PrgmStructApplicable as programStructApplicable "
//				+ " from exam.marks m, exam.students s "
//				+ " where m.sapid= s.sapid "
//				+ " and (m.processed <> 'Y' OR m.processed is null) "
//				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
//				+ " and m.sapid <> 'Not Available'"
//											+ " ");
		
		StringBuffer sql = new StringBuffer("select m.*"
				+ " from exam.marks m "
				+ " where (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
				+ " and m.sapid <> 'Not Available'"
											+ " ");
		
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
//		if(!StringUtils.isBlank(searchBean.getProgram())) {
//			sql.append(" and s.program=?");
//			parameters.add(searchBean.getProgram());
//		}
//		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
//			sql.append(" and s.PrgmStructApplicable=?");
//			parameters.add(searchBean.getPrgmStructApplicable());
//		}
//		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
//			sql.append(" and s.consumerType=?");
//			parameters.add(searchBean.getConsumerType());
//		}

	    Object [] args = parameters.toArray();
		
		try{
		studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)//
	public int getPendingCountForNVRIA(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> pendingRecordsForNVRIA=new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and writenscore in ('NV','RIA')";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select count(m.id) "
				+ " from exam.marks m, exam.students s "
				+ " where (processed <> 'Y' OR processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.writenscore in ('NV','RIA')"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			//pendingRecordsForNVRIA=jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return count;
	}
	
	@Transactional(readOnly = true)//
	public int getPendingCountForANS(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> pendingRecordForANS = new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and assignmentscore = 'ANS'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select count(m.id) "
				+ " from exam.marks m, exam.students s "
				+ " where (processed <> 'Y' OR processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Project exclusion -Vilpesh 20220720
				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
				+ " and m.assignmentscore = 'ANS'"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			//pendingRecordForANS= jdbcTemplate.query(sql.toString(), args,new BeanPropertyRowMapper(StudentMarksBean.class));
		count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return count;
	}
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineWritten(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentMarksBean> studentList =  new ArrayList<StudentMarksBean>() ;
		/*String sql = "select m.*,s.PrgmStructApplicable as programStructApplicable from exam.marks m, exam.students s where m.sapid= s.sapid "
				+ " and (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and subject <> 'Project'"
				+ " and m.writenscore is not null and m.writenscore <> ''";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
//		StringBuffer sql = new StringBuffer( "select m.*,s.PrgmStructApplicable as programStructApplicable"
//				+ " from exam.marks m, exam.students s "
//				+ "	where m.sapid= s.sapid "
//				+ " and (m.processed <> 'Y' OR m.processed is null) "
//				+ " and m.sapid <> 'Not Available'"
//				//+ " and subject NOT IN ('Project', 'Module 4 - Project') "
//				+ " and subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " //Vilpesh on 2022-03-04
//				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
//				+ " and m.writenscore NOT IN ('AB','#CC','NV','RIA','WH')" //AB,#CC etc in Written exclusion -Vilpesh 20220720
//				+ " and m.writenscore is not null and m.writenscore <> ''"
//											+ " and m.sapid <> 'Not Available' "
//											+ " ");
		
		StringBuffer sql = new StringBuffer( "select m.* "
				+ " from exam.marks m "
				+ "	where (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available'"
				//+ " and subject NOT IN ('Project', 'Module 4 - Project') "
				+ " and subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social') " //Vilpesh on 2022-03-04
				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
				+ " and m.writenscore NOT IN ('AB','#CC','NV','RIA','WH')" //AB,#CC etc in Written exclusion -Vilpesh 20220720
				+ " and m.writenscore is not null and m.writenscore <> ''"			
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
//		if(!StringUtils.isBlank(searchBean.getProgram())) {
//			sql.append(" and s.program=?");
//			parameters.add(searchBean.getProgram());
//		}
//		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
//			sql.append(" and s.PrgmStructApplicable=?");
//			parameters.add(searchBean.getPrgmStructApplicable());
//		}
//		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
//			sql.append(" and s.consumerType=?");
//			parameters.add(searchBean.getConsumerType());
//		}

	    Object [] args = parameters.toArray();
		
		try{
		studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingRecordsForOnlineOfflineAssignment(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<StudentMarksBean> studentList =  new ArrayList<StudentMarksBean>() ;
		/*String sql = "select m.*,s.PrgmStructApplicable as programStructApplicable from exam.marks m, exam.students s where m.sapid= s.sapid "
				+ " and (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.assignmentscore is not null and m.assignmentscore <> '' and m.assignmentscore <> 'ANS'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
//		StringBuffer sql = new StringBuffer("select m.*,s.PrgmStructApplicable as programStructApplicable "
//				+ " from exam.marks m, exam.students s "
//				+ " where m.sapid= s.sapid "
//				+ " and (m.processed <> 'Y' OR m.processed is null) "
//				+ " and m.sapid <> 'Not Available'"
//				+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Project exclusion -Vilpesh 20220720
//				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
//				+ " and m.assignmentscore is not null and m.assignmentscore <> '' and m.assignmentscore <> 'ANS'"
//											+ " ");
		
		StringBuffer sql = new StringBuffer("select m.* "
				+ " from exam.marks m "
				+ " where (m.processed <> 'Y' OR m.processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Project exclusion -Vilpesh 20220720
				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
				+ " and m.assignmentscore is not null and m.assignmentscore <> '' and m.assignmentscore <> 'ANS'"
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
//		if(!StringUtils.isBlank(searchBean.getProgram())) {
//			sql.append(" and s.program=?");
//			parameters.add(searchBean.getProgram());
//		}
//		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
//			sql.append(" and s.PrgmStructApplicable=?");
//			parameters.add(searchBean.getPrgmStructApplicable());
//		}
//		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
//			sql.append(" and s.consumerType=?");
//			parameters.add(searchBean.getConsumerType());
//		}

	    Object [] args = parameters.toArray();
		
		try{
		studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		}catch(Exception e){
			
		}
		return studentList;
	}
	//PassFail Dash board End//
	
	//update passFail result process year-month
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updatePassFailResultProcessedYearMonth(StudentExamBean searchBean){
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("Update exam.passfail p set "
				+ " resultProcessedYear = ?,"
				+ " resultProcessedMonth = ?"
				+ " where concat(p.sapid,p.subject) = "
				+ "("
				+ " ");

		parameters.add(searchBean.getYear());
		parameters.add(searchBean.getMonth());
		
		sql.append("select concat(m.sapid,m.subject) "
				+ " from exam.marks m, exam.students s,exam.registration r  "
				+ " where m.sapid= s.sapid "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.assignmentscore is not null and m.assignmentscore <> '' and m.assignmentscore <> 'ANS'"
											+ " and s.sapid = r.sapid "
											+ " and m.sapid = r.sapid "
											+ " and r.sem = m.sem ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and r.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}
		
		sql.append(")");
		 
	    Object [] args = parameters.toArray();
		int updatedRows = jdbcTemplate.update(sql.toString(), args);
	}
	//update passFail result process year-month end

	@Transactional(readOnly = true)//
	public PassFailExamBean getResultProcessingYearMonthByBean(PassFailExamBean p) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "select * from exam.passfail where sapid = ? and subject = ?  ";

		try {
			PassFailExamBean bean = (PassFailExamBean)jdbcTemplate.queryForObject(sql, new Object[]{p.getSapid(),p.getSubject()}, new BeanPropertyRowMapper(PassFailExamBean.class));
			p.setResultProcessedYear(bean.getResultProcessedYear());
			p.setResultProcessedMonth(bean.getResultProcessedMonth());
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		
		return p;
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)//
	public Map<String,ArrayList<PassFailExamBean>> getPassFailRecordsWhereIsPassIsYAndOldIsPassStatusIsNBySapid(String sapid){

		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select * from exam.passfail where "
					 + " sapid = ? and resultProcessedYear = 2018 and resultProcessedMonth = 'Dec'  ";
		List<PassFailExamBean> studentsMarksList = new ArrayList<>();
	
		 try {
			studentsMarksList = jdbcTemplate.query(sql, new Object[]{sapid}, new BeanPropertyRowMapper(PassFailExamBean.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		 
	 	Map<String,ArrayList<PassFailExamBean>> keysMap = new HashMap<>();
		for(int i = 0 ; i < studentsMarksList.size(); i++){
			PassFailExamBean b = (PassFailExamBean)studentsMarksList.get(i);
			String key = b.getSapid().trim()+b.getSubject().trim();
			if(!keysMap.containsKey(key)){
				ArrayList<PassFailExamBean> list = new ArrayList<>();
				list.add(b);
				keysMap.put(key, list);
			}else{
				ArrayList<PassFailExamBean> list = keysMap.get(key);
				list.add(b);
				keysMap.put(key, list);
			}
			
		}
		 
		return keysMap;

	}
	
	@Transactional(readOnly = true)//
	public List<MarksheetBean> getStudentsDetailsByYearMonthProgramForExecutive(PassFailExamBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select s.* from exam.students s "
				+ " where "
				+ "  s.sem = (select max(s2.sem) from exam.students s2 where s2.sapid = s.sapid) "
				+ " and enrollmentMonth = ? and enrollmentYear = ? and program = ?  order by s.centerCode asc ";
			
		try {
				ArrayList<MarksheetBean> studentList = (ArrayList<MarksheetBean>)jdbcTemplate.query(sql, new Object[]{
						bean.getBatchMonth(),
						bean.getBatchYear(),
						bean.getProgram()
			}, new BeanPropertyRowMapper(MarksheetBean.class));
				return studentList;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				//
				
				return null;
			}
				
	}
	
	// countOFFailedSubjects
	
	@Transactional(readOnly = true)//
	public boolean checkIsPassedInNormalSubjects(MarksheetBean bean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 1;
		try{
			if("Not Available".equalsIgnoreCase(bean.getSapid().trim())){
				return false;
			}

			String sql = "SELECT count(*) FROM exam.passfail  "
						 + " where sapid = ?"
						 + " and isPass='N' "
						 + " and (writtenYear <> '' or writtenYear <> null)  "
						 + " and (writtenMonth <> '' or writtenMonth <> null)  ";

			count = (int) jdbcTemplate.queryForObject(sql, new Object[] { 
					bean.getSapid()
			},Integer.class);

		}catch(Exception e){
			//

		}

		if(count == 0){
			return true;
		}else{
			return false;
		}

	}
	
	//PassFailBean getSingleExecutiveStudentsCaseStudyMarks
	public boolean checkIsPassedInCaseStudy(MarksheetBean bean) {
		
		PassFailExamBean caseStudyScore = getSingleExecutiveStudentsCaseStudyMarks(bean.getSapid());
		
		if(!StringUtils.isBlank(caseStudyScore.getSapid())) {
			try {
				//int score = (int)Integer.parseInt(caseStudyScore.getTotal());
				String grade = caseStudyScore.getGrade();
				//if(score > 49) {
				if("A".equalsIgnoreCase(grade) || "B".equalsIgnoreCase(grade)  || "C".equalsIgnoreCase(grade)  || "D".equalsIgnoreCase(grade) ) {
					return true;
				}else {
					return false;
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				//
				return false;
			}
		
		}else {
			return false;
		}
		
	}
		
	@Transactional(readOnly = true)//
	public PassFailExamBean getSingleExecutiveStudentsCaseStudyMarks(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);
		PassFailExamBean bean = new PassFailExamBean();
		String sql ="select sapid,score as total,grade from exam.case_study_submission where sapid = ? ";
		try{
			bean = (PassFailExamBean)jdbcTemplate.queryForObject(sql, new Object[]{
					sapid 
			}, new BeanPropertyRowMapper(PassFailExamBean.class));
			}catch(Exception e){
				//
			}
		return bean;
	}
				
	@Transactional(readOnly = true)//			
	public HashMap<String,ArrayList<PassFailExamBean>> getExecutiveStudentsPassFailMarks(String program,String month,String year){
		HashMap<String,ArrayList<PassFailExamBean>> studentMarksMap = new HashMap<String,ArrayList<PassFailExamBean>>();
		jdbcTemplate = new JdbcTemplate(dataSource);
		//String sql ="SELECT * FROM exam.passfail where program = ? and sapid in (select s.sapid from exam.students s where s.enrollmentYear = ? and s.enrollmentMonth = ?)";
		String sql="";
		if("EPBM".equalsIgnoreCase(program)){
			 sql ="SELECT * FROM exam.passfail where program = ? and subject in ('Business Statistics- EP','Enterprise Miner') and sapid in (select s.sapid from exam.students s where s.enrollmentYear = ? and s.enrollmentMonth = ?) ";
		}else if("MPDV".equalsIgnoreCase(program)){
			sql ="SELECT * FROM exam.passfail where program = ? and subject in ('Business Statistics- MP','Visual Analytics') and sapid in (select s.sapid from exam.students s where s.enrollmentYear = ? and s.enrollmentMonth = ?)";
		}
		try{
			ArrayList<PassFailExamBean> studentMarks = (ArrayList<PassFailExamBean>) jdbcTemplate.query(sql, new Object[]{program,year,month}, new BeanPropertyRowMapper(PassFailExamBean.class));
			for(PassFailExamBean bean:studentMarks){
				ArrayList<PassFailExamBean> marks = new ArrayList<PassFailExamBean>();
				marks.add(bean);
				if(!studentMarksMap.containsKey(bean.getSapid())){
					studentMarksMap.put(bean.getSapid(), marks);
				}else{
					studentMarksMap.get(bean.getSapid()).add(bean);
				}
				
			}
			
			/*for(Entry b : studentMarksMap.entrySet()){
			}*/
		
			}catch(Exception e){
				
			}
		return studentMarksMap;
	}
				
	@Transactional(readOnly = true)//
	public MarksheetBean getExecutiveStudentForMarksheet(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "select s.* from exam.students s "
				+ " where s.sapid = ?";

		MarksheetBean studentList = (MarksheetBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(MarksheetBean.class));
		return studentList;
	}
	
	@Transactional(readOnly = true)//
	public MarksheetBean getSpecializations(String sapid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		MarksheetBean studentList = null;
		try {
			String sql = "select s.* from exam.students s "
					+ " where s.sapid = ?";

			studentList = (MarksheetBean)jdbcTemplate.queryForObject(sql, new Object[]{sapid}, new BeanPropertyRowMapper(MarksheetBean.class));
			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			
		}
		return studentList;
	}
	
	@Transactional(readOnly = true)//
	public String getResultDeclareDateForSingleStudentMBAWX(MarksheetBean bean){
		String sql = "";
		  
		sql = "SELECT  max(lastModifiedDate) from  exam.mba_passfail where sapid=? ";
		 
		String resultDeclareDate = (String)jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
		
		return resultDeclareDate;
	}	
	
	@Transactional(readOnly = true)//
	public String getResultDeclareDateForSingleStudentMBAX(MarksheetBean bean){
		String sql = "";
		  
		sql = "SELECT  max(lastModifiedDate) from  exam.mbax_passfail where sapid=? ";
		 
		String resultDeclareDate = (String)jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
		
		return resultDeclareDate;
	}	
	
	@Transactional(readOnly = true)
	public boolean checkifAnyCopyCaseMarkForAssignment(String sapid, String subject, String year, String month){
		String sql = " SELECT " + 
				"    COUNT(sapid) " + 
				"FROM " + 
				"    exam.assignmentsubmission  " + 
				"WHERE " + 
				"    reason = 'Copy Case' " + 
				"        AND finalReason = 'Copy Case' " + 
				" 		 AND sapid = ? " + 
				"        AND subject = ? "+
				"        AND year = ? " + 
				"        AND month = ? " ;
		jdbcTemplate = new JdbcTemplate(dataSource);
		Integer count =  jdbcTemplate.queryForObject(sql, new Object[]{sapid, subject, year, month}, Integer.class);
		return count > 0;
	}
	
	public void checkifAnyAssignmentCopyCaseMark( List<StudentMarksBean> list, PassFailExamBean passFailBean) {
		try {
//			List<StudentMarksBean>  reversedList = list.stream()
//			.sorted(Comparator.comparing(StudentMarksBean::getExamorder).reversed())
//			.collect(Collectors.toList()); 		
					
			for(StudentMarksBean bean : list ) {
				try {
					if("ANS".equals(bean.getAssignmentscore()) || bean.getAssignmentscore() == null  ){
						continue;
					}else if("0".equals( bean.getAssignmentscore()) ) {
						 if(checkifAnyCopyCaseMarkForAssignment(bean.getSapid(),bean.getSubject(), bean.getYear(), bean.getMonth())) {
							 	passFailBean.setAssignmentscore("0");
								passFailBean.setIsPass("N");
								passFailBean.setRemarks("Copy Case");
								if( !StringUtils.isBlank(passFailBean.getWrittenscore()) && !"AB".equals(passFailBean.getWrittenscore()) 
										&& !"RIA".equals(passFailBean.getWrittenscore()) && !"NV".equals(passFailBean.getWrittenscore())
										) {
									passFailBean.setTotal(passFailBean.getWrittenscore());
								}
								passFailBean.setAssignmentYear(bean.getYear());
								passFailBean.setAssignmentMonth(bean.getMonth());
						 }
						break;	 
					}else {
						break;
					}  
				}catch(Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					passFailLogger.error(bean.getSapid()+" "+bean.getSubject()+" "+ bean.getYear()
							+" "+bean.getMonth()+" Error in loop checkifAnyCopyCaseMarkInPerviousCycle  : "+errors.toString());
				}
	        }
		}catch (Exception e) {
			// TODO: handle exception
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			passFailLogger.error(" Error in checkifAnyCopyCaseMarkInPerviousCycle : "+errors.toString());
		}
	}
	
	@Transactional(readOnly = true)
	public List<PassFailExamBean> getPassFailBySapid(String sapid){
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql =""
				+ " SELECT "
					+ " pf.*, "
					+ " pss.subject, "
					+ " pss.sem, "
					+ " ssc.examYear, "
					+ " ssc.examMonth, "
					+ " ssc.acadYear, "
					+ " ssc.acadMonth, "
					+ " (COALESCE(pf.iaScore, 0) + COALESCE(pf.teeScore, 0)) AS total "
				+ " FROM  exam.mba_passfail pf "
				+ " INNER JOIN lti.student_subject_config ssc "
					+ " ON ssc.id = pf.timeBoundId "
				+ " INNER JOIN exam.program_sem_subject pss "
					+ " ON pf.prgm_sem_subj_id = pss.id " 
				+ " WHERE "
					+ " pf.sapid = ? "
				+ " AND pf.grade is  null "
				+ " ORDER BY ssc.startDate ASC ";
		System.out.println("sql-----" + sql.toString());
		return jdbcTemplate.query(
			sql, 
			new Object[] {
				sapid
			}, 
			new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class)
		);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PassFailExamBean> getResultDeclaredDateByYearAndMonth(ArrayList<String> listYearAndMonth)throws Exception
	{
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource queryParams = new MapSqlParameterSource();
		String query = "SELECT " + 
				"    declareDate AS resultDeclaredDate, "+ 
				"	 year, " + 
				"	 month " + 
				"FROM " + 
				"    exam.examorder " + 
				"WHERE " + 
				"    CONCAT(year,month) IN (:listYearAndMonth)";
		queryParams.addValue("listYearAndMonth", listYearAndMonth);
		List<PassFailExamBean> declaredDateList = namedParameterJdbcTemplate.query(query, queryParams,
				new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
		
		return declaredDateList;
	}
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getAbsentRecord(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> studentList=new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and writenscore = 'AB'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select * "
									+ " from exam.marks m, exam.students s "
									+ " where m.sapid= s.sapid "
									+ " and (m.processed <> 'Y' OR m.processed is null) "
									+ " and m.sapid <> 'Not Available' "
									+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //AB in Project exclusion -Vilpesh 20220720
									+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
									+ " and m.writenscore = 'AB'"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			studentList = (List<StudentMarksBean>) jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		//count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return studentList;
	}
	
	public List<PassFailExamBean> getPassFailRecordsByResultProcessedYearAndMonth(String examYear, String examMonth) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "SELECT `sapid`,`subject`, `isPass` FROM `exam`.`passfail` WHERE `resultProcessedYear` = ? AND `resultProcessedMonth` = ?";
		
		return jdbcTemplate.query(sql, new Object[] { examYear, examMonth },
				new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));
	}

	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingListForNVRIA(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> pendingRecordsForNVRIA=new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and writenscore in ('NV','RIA')";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select * "
				+ " from exam.marks m, exam.students s "
				+ " where (processed <> 'Y' OR processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.writenscore in ('NV','RIA')"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			pendingRecordsForNVRIA=jdbcTemplate.query(sql.toString(),args,new BeanPropertyRowMapper(StudentMarksBean.class));
		//count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return pendingRecordsForNVRIA;
	}
	
	@Transactional(readOnly = true)//
	public List<StudentMarksBean> getPendingListForANS(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int count = 0;
		List<StudentMarksBean> pendingRecordForANS = new ArrayList<StudentMarksBean>();
		/*String sql = "select count(*) from exam.marks where (processed <> 'Y' OR processed is null) "
				+ " and sapid <> 'Not Available'"
				+ " and assignmentscore = 'ANS'";*/
		
		ArrayList<Object> parameters = new ArrayList<Object>();
		//just count(*) replaced with count(m.id) by Vilpesh on 2021-09-21
		StringBuffer sql = new StringBuffer("select * "
				+ " from exam.marks m, exam.students s "
				+ " where (processed <> 'Y' OR processed is null) "
				+ " and m.sapid <> 'Not Available'"
				+ " and m.subject NOT IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')" //Project exclusion -Vilpesh 20220720
				+ " and m.subject NOT IN ('Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking')"//Softskill subjects exclusion -Vilpesh 20220720
				+ " and m.assignmentscore = 'ANS'"
											+ " and s.sapid = m.sapid "
											+ " ");
		
		
		
		if(!StringUtils.isBlank(searchBean.getYear())) {
			sql.append(" and m.year=?");
			parameters.add(searchBean.getYear());
		}
		if(!StringUtils.isBlank(searchBean.getMonth())) {
			sql.append(" and m.month=?");
			parameters.add(searchBean.getMonth());
		}
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		}

	    Object [] args = parameters.toArray();
		
		try{
			pendingRecordForANS= jdbcTemplate.query(sql.toString(), args,new BeanPropertyRowMapper(StudentMarksBean.class));
		//count = (int) jdbcTemplate.queryForObject(sql.toString(),args,Integer.class);
		}catch(Exception e){
			
		}
		return pendingRecordForANS;
	}
	
	
	@Transactional(readOnly = true)
	public List<StudentExamBean> getStudentList(StudentExamBean searchBean) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<Object> parameters = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(" Select s.sapid,s.PrgmStructApplicable from exam.students s where 1 = 1");
		
		if(!StringUtils.isBlank(searchBean.getProgram())) {
			sql.append(" and s.program=?");
			parameters.add(searchBean.getProgram());
		}
		if(!StringUtils.isBlank(searchBean.getPrgmStructApplicable())) {
			sql.append(" and s.PrgmStructApplicable=?");
			parameters.add(searchBean.getPrgmStructApplicable());
		}
		if(!StringUtils.isBlank(searchBean.getConsumerType())) {
			sql.append(" and s.consumerType=?");
			parameters.add(searchBean.getConsumerType());
		} 
		Object[] args = parameters.toArray();
		ArrayList<StudentExamBean> students = (ArrayList<StudentExamBean>)jdbcTemplate.query(sql.toString(),args, new BeanPropertyRowMapper(StudentExamBean.class));
		return students;
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentMarksBean> getStudentNotBookedStudent(StudentExamBean searchBean) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	String sql = "SELECT  " + 
			"    sapid, month, year, sem, subject, writenscore,studentname,program " + 
			"	 FROM  exam.marks " + 
			"	 WHERE " + 
			"    processed <> 'Y' "+
			"	 AND writenscore ='' " + 
			"	 AND month = ? " + 
			"    AND year = ?" + 
			"    AND subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')";
		return jdbcTemplate.query(sql,new Object[] {searchBean.getMonth(),searchBean.getYear()}, new BeanPropertyRowMapper<>(StudentMarksBean.class));
	}

	@Override
	public List<StudentMarksBean> getProjectAbsentList(StudentExamBean searchBean) {
		JdbcTemplate jbbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT  " + 
				"    sapid, month, year, sem, subject, writenscore,studentname,program " + 
				"	 FROM  exam.marks " + 
				"	 WHERE " + 
				"    processed <> 'Y' "+
				"	 AND writenscore = 'AB' " + 
				"	 AND month = ? " + 
				"    AND year = ?" + 
				"    AND subject IN ('Project', 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')";
		return jdbcTemplate.query(sql,new Object[] {searchBean.getMonth(),searchBean.getYear()}, new BeanPropertyRowMapper<>(StudentMarksBean.class));

	}
	
	@Transactional(readOnly = true)
	public List<MarksheetBean> getResultDeclareDateForMSCAIStudents(List<String> sapids){
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource paramValue = new MapSqlParameterSource();
		String sql = ""+
				"SELECT " + 
				"    `sapid`, " + 
				"    `lastModifiedDate` AS resultDeclarationDate " + 
				"FROM " + 
				"    `exam`.`mscaiml_md_q8_passfail` " + 
				"WHERE " + 
				"    `sapid` IN(:sapids)" +
				"    AND `grade` IS NOT NULL " + 
    			"	 AND `isResultLive` = 'Y' " + 
    			"	 AND `isPass` = 'Y' ";
		paramValue.addValue("sapids", sapids);
		return namedParameterJdbcTemplate.query(sql,paramValue,new BeanPropertyRowMapper<MarksheetBean>(MarksheetBean.class));
	}	
	
	@Transactional(readOnly = true)
	public String getResultDeclareDateForMSCAISingleStudent(MarksheetBean bean){
		String sql = "" +
				"SELECT " + 
				"    `lastModifiedDate` " + 
				"FROM " + 
				"    `exam`.`mscaiml_md_q8_passfail` " + 
				"WHERE " + 
				"    `sapid` = ?" +
				"    AND `grade` IS NOT NULL " + 
    			"	 AND `isResultLive` = 'Y' " + 
    			"	 AND `isPass` = 'Y' ";
		return jdbcTemplate.queryForObject(sql,new Object[]{bean.getSapid()},String.class);
	}

	public List<PassFailExamBean> getPassFailRecord(String sapid, String subject) {
		JdbcTemplate jbbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT sapid,subject, isPass " + 
						" from exam.passfail"
						+ " where sapid=? and subject=?   ";
		return jdbcTemplate.query(sql,new Object[] {sapid,subject}, new BeanPropertyRowMapper<PassFailExamBean>(PassFailExamBean.class));

	}

}
