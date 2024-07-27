package com.nmims.daos;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nmims.beans.ExamOrderCareerservicesBean;

public abstract class BaseDAO {

	protected DataSource baseDataSource;
	private JdbcTemplate jdbcTemplate;
	private List<ExamOrderCareerservicesBean> liveFlagList = null;

	private String liveExamMonth = null;
	private String liveExamYear = null;
	private String liveAcadConentMonth = null;
	private String liveAcadConentYear = null;
	private String liveAcadSessionMonth = null;
	private String liveAcadSessionYear = null;

	private String liveOnlineExamResultMonth = null;
	private String liveOnlineExamResultYear = null;

	private String liveOfflineExamResultMonth = null;
	private String liveOfflineExamResultYear = null;

	private String liveAssignmentMonth = null;
	private String liveAssignmentYear = null;

	private String liveResitAssignmentMonth = null;
	private String liveResitAssignmentYear = null;

	private String liveAssignmentMarksMonth = null;
	private String liveAssignmentMarksYear = null;
	
	private String liveProjectExamMonth = null;
	private String liveProjectExamYear = null;

	public abstract void setBaseDataSource();

	public void refreshLiveFlagSettings() {
		getLiveFlagDetails(true);
	}

	public List<ExamOrderCareerservicesBean> getLiveFlagDetails(boolean refresh) {
		// Query only once, and refresh later when settings are changed
		if (this.liveFlagList == null || this.liveFlagList.size() == 0 || refresh == true) {
			final String sql = " Select * from exam.examorder order by examorder.order asc";
			jdbcTemplate = new JdbcTemplate(baseDataSource);
			this.liveFlagList = (ArrayList<ExamOrderCareerservicesBean>) jdbcTemplate.query(sql,
					new BeanPropertyRowMapper<ExamOrderCareerservicesBean>(ExamOrderCareerservicesBean.class));

			for (ExamOrderCareerservicesBean bean : liveFlagList) {
				if ("Y".equalsIgnoreCase(bean.getAcadSessionLive())) {
					liveAcadSessionMonth = bean.getAcadMonth();
					liveAcadSessionYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getAcadContentLive())) {
					liveAcadConentMonth = bean.getAcadMonth();
					liveAcadConentYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getTimeTableLive())) {
					liveExamMonth = bean.getMonth();
					liveExamYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getLive())) {
					liveOnlineExamResultMonth = bean.getMonth();
					liveOnlineExamResultYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getOflineResultslive())) {
					liveOfflineExamResultMonth = bean.getMonth();
					liveOfflineExamResultYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getAssignmentLive())) {
					liveAssignmentMonth = bean.getAcadMonth();
					liveAssignmentYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getResitAssignmentLive())) {
					liveResitAssignmentMonth = bean.getMonth();
					liveResitAssignmentYear = bean.getYear();
				}

				if ("Y".equalsIgnoreCase(bean.getAssignmentMarksLive())) {
					liveAssignmentMarksMonth = bean.getMonth();
					liveAssignmentMarksYear = bean.getYear();
				}
				if ("Y".equalsIgnoreCase(bean.getProjectSubmissionLive())) {
					liveProjectExamMonth = bean.getMonth();
					liveProjectExamYear = bean.getYear();
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

	public String getLiveAcadConentMonth() {
		return liveAcadConentMonth;
	}

	public void setLiveAcadConentMonth(String liveAcadConentMonth) {
		this.liveAcadConentMonth = liveAcadConentMonth;
	}

	public String getLiveAcadConentYear() {
		return liveAcadConentYear;
	}

	public void setLiveAcadConentYear(String liveAcadConentYear) {
		this.liveAcadConentYear = liveAcadConentYear;
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

	public String getLiveOnlineExamResultMonth() {
		return liveOnlineExamResultMonth;
	}

	public void setLiveOnlineExamResultMonth(String liveOnlineExamResultMonth) {
		this.liveOnlineExamResultMonth = liveOnlineExamResultMonth;
	}

	public String getLiveOnlineExamResultYear() {
		return liveOnlineExamResultYear;
	}

	public void setLiveOnlineExamResultYear(String liveOnlineExamResultYear) {
		this.liveOnlineExamResultYear = liveOnlineExamResultYear;
	}

	public String getLiveOfflineExamResultMonth() {
		return liveOfflineExamResultMonth;
	}

	public void setLiveOfflineExamResultMonth(String liveOfflineExamResultMonth) {
		this.liveOfflineExamResultMonth = liveOfflineExamResultMonth;
	}

	public String getLiveOfflineExamResultYear() {
		return liveOfflineExamResultYear;
	}

	public void setLiveOfflineExamResultYear(String liveOfflineExamResultYear) {
		this.liveOfflineExamResultYear = liveOfflineExamResultYear;
	}

	public String getLiveAssignmentMonth() {
		return liveAssignmentMonth;
	}

	public void setLiveAssignmentMonth(String liveAssignmentMonth) {
		this.liveAssignmentMonth = liveAssignmentMonth;
	}

	public String getLiveAssignmentYear() {
		return liveAssignmentYear;
	}

	public void setLiveAssignmentYear(String liveAssignmentYear) {
		this.liveAssignmentYear = liveAssignmentYear;
	}

	public String getLiveResitAssignmentMonth() {
		return liveResitAssignmentMonth;
	}

	public void setLiveResitAssignmentMonth(String liveResitAssignmentMonth) {
		this.liveResitAssignmentMonth = liveResitAssignmentMonth;
	}

	public String getLiveResitAssignmentYear() {
		return liveResitAssignmentYear;
	}

	public void setLiveResitAssignmentYear(String liveResitAssignmentYear) {
		this.liveResitAssignmentYear = liveResitAssignmentYear;
	}

	public String getLiveAssignmentMarksMonth() {
		return liveAssignmentMarksMonth;
	}

	public void setLiveAssignmentMarksMonth(String liveAssignmentMarksMonth) {
		this.liveAssignmentMarksMonth = liveAssignmentMarksMonth;
	}

	public String getLiveAssignmentMarksYear() {
		return liveAssignmentMarksYear;
	}

	public void setLiveAssignmentMarksYear(String liveAssignmentMarksYear) {
		this.liveAssignmentMarksYear = liveAssignmentMarksYear;
	}

	public String getLiveProjectExamYear() {
		return liveProjectExamYear;
	}

	public void setLiveProjectExamYear(String liveProjectExamYear) {
		this.liveProjectExamYear = liveProjectExamYear;
	}

	public String getLiveProjectExamMonth() {
		return liveProjectExamMonth;
	}

	public void setLiveProjectExamMonth(String liveProjectExamMonth) {
		this.liveProjectExamMonth = liveProjectExamMonth;
	}



}
