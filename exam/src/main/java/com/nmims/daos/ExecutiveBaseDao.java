package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ExamOrderExamBean;

public abstract class ExecutiveBaseDao {
	protected DataSource baseDataSource;
	private JdbcTemplate jdbcTemplate;
	
	public abstract void setBaseDataSource();
	
	private List<ExamOrderExamBean> liveFlagList = null;
	
	private String liveExamMonth = null;
	private String liveExamYear = null;
	private String liveAcadContentMonth = null;
	private String liveAcadContentYear = null;
	private String liveAcadSessionMonth = null;
	private String liveAcadSessionYear = null;
	private String liveSasExamResultMonth = null;
	private String liveSasExamResultYear = null;
	
	public void refreshLiveFlagSettings(){
		getLiveFlagDetails(true);
	}

	@Transactional(readOnly = true)
	public List<ExamOrderExamBean> getLiveFlagDetails(boolean refresh){
		//Query only once, and refresh later when settings are changed
		if(this.liveFlagList == null || this.liveFlagList.size() == 0 || refresh == true){
			final String sql = " Select * from exam.examorder order by examorder.order asc";
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			this.liveFlagList = (ArrayList<ExamOrderExamBean>)jdbcTemplate.query(sql,new BeanPropertyRowMapper<ExamOrderExamBean>(ExamOrderExamBean.class));
			
			for (ExamOrderExamBean bean : liveFlagList) {
				if("Y".equalsIgnoreCase(bean.getAcadSessionLive())){
					liveAcadSessionMonth = bean.getAcadMonth();
					liveAcadSessionYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getAcadContentLive())){
					liveAcadContentMonth = bean.getMonth();
					liveAcadContentYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getTimeTableLive())){
					liveExamMonth = bean.getMonth();
					liveExamYear = bean.getYear();
				}
				
				if("Y".equalsIgnoreCase(bean.getLive())){
					liveSasExamResultMonth = bean.getMonth();
					liveSasExamResultYear = bean.getYear();
				}
				
				
				
			}
		}

		return this.liveFlagList;
	}

	public String getLiveExamMonth() {
		return liveExamMonth;
	}

	public void setLiveExamMonth(String liveExamMonth) {
		this.liveExamMonth = liveExamMonth;
	}

	public String getLiveExamYear() {
		return liveExamYear;
	}

	public void setLiveExamYear(String liveExamYear) {
		this.liveExamYear = liveExamYear;
	}

	public String getLiveAcadContentMonth() {
		return liveAcadContentMonth;
	}

	public void setLiveAcadContentMonth(String liveAcadContentMonth) {
		this.liveAcadContentMonth = liveAcadContentMonth;
	}

	public String getLiveAcadContentYear() {
		return liveAcadContentYear;
	}

	public void setLiveAcadContentYear(String liveAcadContentYear) {
		this.liveAcadContentYear = liveAcadContentYear;
	}

	public String getLiveAcadSessionMonth() {
		return liveAcadSessionMonth;
	}

	public void setLiveAcadSessionMonth(String liveAcadSessionMonth) {
		this.liveAcadSessionMonth = liveAcadSessionMonth;
	}

	public String getLiveAcadSessionYear() {
		return liveAcadSessionYear;
	}

	public void setLiveAcadSessionYear(String liveAcadSessionYear) {
		this.liveAcadSessionYear = liveAcadSessionYear;
	}

	public String getLiveSasExamResultMonth() {
		return liveSasExamResultMonth;
	}

	public void setLiveSasExamResultMonth(String liveSasExamResultMonth) {
		this.liveSasExamResultMonth = liveSasExamResultMonth;
	}

	public String getLiveSasExamResultYear() {
		return liveSasExamResultYear;
	}

	public void setLiveSasExamResultYear(String liveSasExamResultYear) {
		this.liveSasExamResultYear = liveSasExamResultYear;
	}



}
