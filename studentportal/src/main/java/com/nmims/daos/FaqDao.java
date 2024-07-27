package com.nmims.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.FaqCategoryBean;
import com.nmims.beans.FaqProgramGroupBean;
import com.nmims.beans.FaqQuestionAnswerTableBean;
import com.nmims.beans.FaqSubCategoryBean;

@Repository("faqdao")
public class FaqDao {

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	public ArrayList<FaqProgramGroupBean> getFAQProgramGroups() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM faq.faq_groups";

		try {
			ArrayList<FaqProgramGroupBean> groups = new ArrayList<FaqProgramGroupBean>(
					jdbcTemplate.query(sql, new BeanPropertyRowMapper(FaqProgramGroupBean.class)));
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Transactional(readOnly = true)
	public ArrayList<FaqCategoryBean> getFAQCategories() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * FROM faq.faq_category";

		try {
			ArrayList<FaqCategoryBean> groups = new ArrayList<FaqCategoryBean>(
					jdbcTemplate.query(sql, new BeanPropertyRowMapper(FaqCategoryBean.class)));
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Transactional(readOnly = true)
	public ArrayList<FaqSubCategoryBean> getAllFAQSubCategories() {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * from faq.faq_subcategory";

		try {
			ArrayList<FaqSubCategoryBean> groups = new ArrayList<FaqSubCategoryBean>(
					jdbcTemplate.query(sql, new BeanPropertyRowMapper(FaqSubCategoryBean.class)));
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Transactional(readOnly = true)
	public ArrayList<FaqSubCategoryBean> getFAQSubCategories(String categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT * from faq.faq_subcategory where categoryId=?";

		try {
			ArrayList<FaqSubCategoryBean> groups = new ArrayList<FaqSubCategoryBean>(
					jdbcTemplate.query(sql, new PreparedStatementSetter() {

						@Override
						public void setValues(PreparedStatement ps) throws SQLException {

							ps.setString(1, categoryId);

						}

					}, new BeanPropertyRowMapper(FaqSubCategoryBean.class)));
			return groups;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Transactional(readOnly = false)
	public int addFaqEntry(String question, String answer, String faqGroupId, String categoryId, String subcategoryid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int a = 0;

		String sql = "INSERT INTO `faq`.`faq_question_answer` (`question`, `answer`, `faqGroupId`, `categoryId`, `subCategoryId`) VALUES (?, ?, ?, ?, ?);";

		// Object[] parameter = new Object[] { question, answer, faqGroupId,
		// categoryId,subcategoryid };

		int[] parametertypes = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };

		try {

			a = jdbcTemplate.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {

					ps.setString(1, question);
					ps.setString(2, answer);
					ps.setString(3, faqGroupId);
					ps.setString(4, categoryId);
					ps.setString(5, subcategoryid);
				}

			});

		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}

		return a;
	}

	@Transactional(readOnly = false)
	public int addFaqCategoryEntry(String categoryname) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int a = 0;

		String sql = "INSERT INTO `faq`.`faq_category` (`categoryname`) VALUES (?);";

		// Object[] parameter = new Object[] { question, answer, faqGroupId,
		// categoryId,subcategoryid };

		int[] parametertypes = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };

		try {

			a = jdbcTemplate.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {

					ps.setString(1, categoryname);

				}

			});

		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}

		return a;
	}

	@Transactional(readOnly = false)
	public int addFaqSubCategoryEntry(String subcategoryname,String categoryid) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		int a = 0;

		String sql = "INSERT INTO `faq`.`faq_subcategory` (`subCategoryName`, `categoryId`) VALUES (?, ?);";

		// Object[] parameter = new Object[] { question, answer, faqGroupId,
		// categoryId,subcategoryid };

		int[] parametertypes = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };

		try {

			a = jdbcTemplate.update(sql, new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {

					ps.setString(1, subcategoryname);
					ps.setString(2, categoryid);

				}

			});

		} catch (Exception e) {

			e.printStackTrace();
			return 0;
		}

		return a;
	}
	
	@Transactional(readOnly = true)
	public String getFaqGroupType(String masterKey) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT faqGroupId FROM faq.faq_group_masterkey_mapping where masterKey=?";
		String groupType = "";
		try {
			groupType = jdbcTemplate.queryForObject(sql, new Object[] { masterKey }, String.class);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return groupType;
	}

	@Transactional(readOnly = true)
	public ArrayList<FaqQuestionAnswerTableBean> getListOfFaqQuestionAnswer(String faqGroupId, String category,
			String subcategory) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FaqQuestionAnswerTableBean> listOfQuestionAnswer;
		ArrayList<Object> parameters = new ArrayList<Object>();
		String sql = "SELECT * FROM faq.faq_question_answer   ";
		
		if (faqGroupId != null
				&& !("".equals(faqGroupId))) {
			sql = sql + " where faqGroupId=? ";
			parameters.add(faqGroupId);
		}
		
		if (category != null
				&& !("".equals(category))) {
			sql = sql + " and categoryId=? ";
			parameters.add(category);
		}
		if (subcategory != null
				&& !("".equals(subcategory))) {
			sql = sql + " and subCategoryId=? ";
			parameters.add(subcategory);
		}
		Object[] args = parameters.toArray();

		try {
			listOfQuestionAnswer = new ArrayList<FaqQuestionAnswerTableBean>(
			//jdbcTemplate.query(sql, new Object[] { faqGroupId, category, subcategory },new BeanPropertyRowMapper(FaqQuestionAnswerTableBean.class)));
			jdbcTemplate.query(sql, args,new BeanPropertyRowMapper(FaqQuestionAnswerTableBean.class)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return listOfQuestionAnswer;

	}

	@Transactional(readOnly = true)
	public ArrayList<FaqQuestionAnswerTableBean> getAllFaqQuestionAnswer(String faqGroupId) {
		//System.out.println("call1");
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FaqQuestionAnswerTableBean> listOfQuestionAnswer;
		String sql = "SELECT * FROM faq.faq_question_answer where faqGroupId=?";

		try {
			listOfQuestionAnswer = new ArrayList<FaqQuestionAnswerTableBean>(jdbcTemplate.query(sql,
					new Object[] { faqGroupId }, new BeanPropertyRowMapper(FaqQuestionAnswerTableBean.class)));
			//System.out.println(listOfQuestionAnswer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return listOfQuestionAnswer;

	}

	@Transactional(readOnly = true)
	public ArrayList<FaqQuestionAnswerTableBean> getAllFaqQuestionAnswerForAdminSide() {
		//System.out.println("call1");
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FaqQuestionAnswerTableBean> listOfQuestionAnswer;
		String sql = "SELECT * FROM faq.faq_question_answer";

		try {
			listOfQuestionAnswer = new ArrayList<FaqQuestionAnswerTableBean>(
					jdbcTemplate.query(sql, new BeanPropertyRowMapper(FaqQuestionAnswerTableBean.class)));
			//System.out.println(listOfQuestionAnswer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return listOfQuestionAnswer;

	}

	@Transactional(readOnly = true)
	public boolean isAbleToAddThisQuestion(String faqGroup, String faqCategory, String faqSubCategory, String question,
			String answer) throws SQLException {
		// int i=jdbcTemplate.queryForObject(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory},Integer.class);

		// SqlRowSet rs=jdbcTemplate.queryForRowSet(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory});
		// System.out.println("i is "+i);

		//System.out.println("1");
		jdbcTemplate = new JdbcTemplate(dataSource);
		Connection con = dataSource.getConnection();

		//System.out.println("2");
		String sql = "SELECT id FROM faq.faq_question_answer where question=? and answer=?and faqGroupId=?and categoryId=?and subCategoryId=?";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, question);
			pstmt.setString(2, answer);
			pstmt.setString(3, faqGroup);
			pstmt.setString(4, faqCategory);
			pstmt.setString(5, faqSubCategory);
			//System.out.println("inside dao");

			ResultSet rs = pstmt.executeQuery();

			//System.out.println("test1");

			if (rs.next()) {
				//System.out.println("test2");
				return false;
			} else {
				//System.out.println("test3");
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional(readOnly = true)
	public boolean isAbleToAddThisCategory(String faqCategory) throws SQLException {
		// int i=jdbcTemplate.queryForObject(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory},Integer.class);

		// SqlRowSet rs=jdbcTemplate.queryForRowSet(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory});
		// System.out.println("i is "+i);

		//System.out.println("1");
		jdbcTemplate = new JdbcTemplate(dataSource);
		Connection con = dataSource.getConnection();

		//System.out.println("2");
		String sql = "SELECT id FROM faq.faq_category where categoryname=?;";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, faqCategory);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	@Transactional(readOnly = true)
	public boolean isAbleToAddThisSubCategory(String faqsubCategory,String categoryid) throws SQLException {
		// int i=jdbcTemplate.queryForObject(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory},Integer.class);

		// SqlRowSet rs=jdbcTemplate.queryForRowSet(sql,new Object[]
		// {question,answer,faqGroup,faqCategory,faqSubCategory});
		// System.out.println("i is "+i);

		//System.out.println("1");
		jdbcTemplate = new JdbcTemplate(dataSource);
		Connection con = dataSource.getConnection();

		//System.out.println("2");
		String sql = "SELECT * FROM faq.faq_subcategory where subCategoryName=? and categoryId=?;";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, faqsubCategory);
			pstmt.setString(2, categoryid);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	@Transactional(readOnly = false)
	public String deleteFaqEntry(int id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM `faq`.`faq_question_answer` WHERE `id`= ? ";
		int a=0;
		String msg="Successfully deleted FAQ Entry";
		try {
			 a=jdbcTemplate.update(sql, new Object[] { id });
			 
				if(a==0)
				{
					return null;
				}
				
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	@Transactional(readOnly = false)
	public String deleteRecordsFromFaqQAT(int id ,String type) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql="";
		if(type.equals("category"))
		{
			 sql = "DELETE FROM `faq`.`faq_question_answer` WHERE `categoryId`= ? ";
		}
		else if(type.equals("subcategory"))
		{
			sql = "DELETE FROM `faq`.`faq_question_answer` WHERE `subCategoryId`= ? ";
		}

		int a=0;
		String msg="Successfully deleted FAQ Entry";
		try {
			 a=jdbcTemplate.update(sql, new Object[] { id });
			 
				if(a==0)
				{
					return null;
				}
				
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	@Transactional(readOnly = false)
	public String deleteFaqCategoryEntry(int id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM `faq`.`faq_category` WHERE `id`=?;";
		int a=0;
		String msg="Successfully deleted FAQ Entry";
		try {
			 a=jdbcTemplate.update(sql, new Object[] { id });
			 
				if(a==0)
				{
					return null;
				}
				
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	@Transactional(readOnly = false)
	public String deleteFaqSubCategoryEntry(int id) {

		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM `faq`.`faq_subcategory` WHERE `id`=?;";
		int a=0;
		String msg="Successfully deleted FAQ Entry";
		try {
			 a=jdbcTemplate.update(sql, new Object[] { id });
			 
				if(a==0)
				{
					return null;
				}
				
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	

	/*public ArrayList<FaqQuestionAnswerTableBean> getFaqById(int Id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FaqQuestionAnswerTableBean> listOfQuestionAnswer;
		String sql = "SELECT * FROM faq.faq_question_answer where id=?";

		try {

			listOfQuestionAnswer = new ArrayList<FaqQuestionAnswerTableBean>(jdbcTemplate.query(sql,
					new Object[] { Id }, new BeanPropertyRowMapper(FaqQuestionAnswerTableBean.class)));
			//System.out.println(listOfQuestionAnswer);
		} catch (Exception e) {
			e.printStackTrace(); 
			return null;
		}

		return listOfQuestionAnswer;

	}*/
	
	@Transactional(readOnly = true)
	public FaqQuestionAnswerTableBean getFaqById(int id) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		FaqQuestionAnswerTableBean listOfQuestionAnswer;
		String sql = "SELECT * FROM faq.faq_question_answer where id=?";

		try {
			listOfQuestionAnswer = (FaqQuestionAnswerTableBean) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(FaqQuestionAnswerTableBean.class), id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return listOfQuestionAnswer;

	}

	@Transactional(readOnly = false)
	public int updatefaqData(int faqid, String faqGroup, String faqCategory, String faqSubCategory, String question,
			String answer) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "UPDATE `faq`.`faq_question_answer` SET `question`=?, `answer`=?, `faqGroupId`=?, `categoryId`=?, `subCategoryId`=? WHERE `id`=?;";
		int rowsEffected = 0;
		try {
			rowsEffected = jdbcTemplate.update(sql, question, answer, faqGroup, faqCategory, faqSubCategory, faqid);

		} catch (Exception e) {
			return 0;
		}

		return rowsEffected;
	}
	
	@Transactional(readOnly = false)
	public int updatefaqCategoryData(int categoryid, String faqCategory) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "UPDATE `faq`.`faq_category` SET `categoryname`=? WHERE `id`=?;";
		int rowsEffected = 0;
		try {
			rowsEffected = jdbcTemplate.update(sql, faqCategory, categoryid);

		} catch (Exception e) {
			return 0;
		}

		return rowsEffected;
	}
	
	@Transactional(readOnly = false)
	public int updatefaqSubCategoryData(String subcategoryname,int subcatId) {
		jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "UPDATE `faq`.`faq_subcategory` SET `subCategoryName`=? WHERE `id`=?;";
		int rowsEffected = 0;
		try {
			rowsEffected = jdbcTemplate.update(sql, subcategoryname, subcatId);

		} catch (Exception e) {
			return 0;
		}

		return rowsEffected;
	}
	
	/*public ArrayList<FaqSubCategoryBean> getFaqSubCategoryList(String faqCategory) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		ArrayList<FaqSubCategoryBean> faqsubCategorylist;
		String sql = "SELECT id,subCategoryName FROM faq.faq_subcategory where categoryId=?";

		try {
			faqsubCategorylist= new ArrayList<FaqSubCategoryBean>(jdbcTemplate.query(sql,
					new Object[] { faqCategory }, new BeanPropertyRowMapper(FaqSubCategoryBean.class)));
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

		return faqsubCategorylist;

	}*/
	
	//to get subcategry list subcategry is not in faq
	@Transactional(readOnly = true)
	public List<Integer> getSubCategoryIdsFromCategoryId(String categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<Integer> arrayList = new ArrayList<>();
		
		try {
			arrayList = jdbcTemplate.queryForList("select id from faq.faq_subcategory where categoryId = ?", Integer.class, categoryId);
		}
		catch(Exception ex) {
			//ex.printStackTrace();
		}
		//System.out.println("SubCat arrayList: " + arrayList.toString());

		return arrayList;
	}
	
	
	@Transactional(readOnly = true)
	public List<FaqSubCategoryBean> getListOfSubCategerys() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * from faq.faq_subcategory";	
		return jdbcTemplate.query(sql,new Object[] {}, new BeanPropertyRowMapper<FaqSubCategoryBean>(FaqSubCategoryBean.class));
	}
	
	@Transactional(readOnly = true)
	public List<FaqQuestionAnswerTableBean> getListOfFaqQnA(String faqGroupTypeId, String categoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM faq.faq_question_answer where faqGroupId=? and categoryId=?";	
		return jdbcTemplate.query(sql, new Object[] {faqGroupTypeId, categoryId}, new BeanPropertyRowMapper<FaqQuestionAnswerTableBean>(FaqQuestionAnswerTableBean.class));
	}
	
	@Transactional(readOnly = true)
	public ArrayList<FaqQuestionAnswerTableBean> getListOfFaqQnASubCategory(String faqGroupId, String categeryId, String subCategoryId) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM faq.faq_question_answer where faqGroupId=? and categoryId=? and subCategoryId=? ";
		return (ArrayList<FaqQuestionAnswerTableBean>) jdbcTemplate.query(sql, new Object[]{faqGroupId, categeryId, subCategoryId}, 
				new BeanPropertyRowMapper<FaqQuestionAnswerTableBean>(FaqQuestionAnswerTableBean.class));
	}


}
