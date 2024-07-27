package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseCareerservicesBean implements Serializable{
	
	ArrayList<InterviewBean> studentInterview;
	InterviewBean interviewDetails;
	InterviewBean studentDetails;
	
	public ArrayList<InterviewBean> getStudentInterview() {
		return studentInterview;
	}
	public void setStudentInterview(ArrayList<InterviewBean> studentInterview) {
		this.studentInterview = studentInterview;
	}
	public InterviewBean getInterviewDetails() {
		return interviewDetails;
	}
	public void setInterviewDetails(InterviewBean interviewDetails) {
		this.interviewDetails = interviewDetails;
	}
	public InterviewBean getStudentDetails() {
		return studentDetails;
	}
	public void setStudentDetails(InterviewBean studentDetails) {
		this.studentDetails = studentDetails;
	}
	
}
