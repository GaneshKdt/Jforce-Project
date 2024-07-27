package com.nmims.beans;

import java.util.ArrayList;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.ExamsAssessmentsBean;

public class FailedregistrationExcelBean {
	
	private ArrayList<MettlRegisterCandidateBean> userList; 
	private ExamsAssessmentsBean examBean; 
	private String subject;
	private String endTime;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public ArrayList<MettlRegisterCandidateBean> getUserList() {
		return userList;
	}
	public void setUserList(ArrayList<MettlRegisterCandidateBean> userList) {
		this.userList = userList;
	}
	public ExamsAssessmentsBean getExamBean() {
		return examBean;
	}
	public void setExamBean(ExamsAssessmentsBean examBean) {
		this.examBean = examBean;
	}
}
