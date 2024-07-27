package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreeCourseSubjectResponseBean  implements Serializable {
	private String status;
	private String message;
	ArrayList<ContentStudentPortalBean> resourceContent;
	ArrayList<VideoContentStudentPortalBean> videoContent;
	ArrayList<TestStudentPortalBean> quizList;
	
	private List<LeadModuleStatusBean> subjectList;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<LeadModuleStatusBean> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(List<LeadModuleStatusBean> subjectList) {
		this.subjectList = subjectList;
	}
	public ArrayList<ContentStudentPortalBean> getResourceContent() {
		return resourceContent;
	}
	public void setResourceContent(ArrayList<ContentStudentPortalBean> resourceContent) {
		this.resourceContent = resourceContent;
	}
	public ArrayList<VideoContentStudentPortalBean> getVideoContent() {
		return videoContent;
	}
	public void setVideoContent(ArrayList<VideoContentStudentPortalBean> videoContent) {
		this.videoContent = videoContent;
	}
	public ArrayList<TestStudentPortalBean> getQuizList() {
		return quizList;
	}
	public void setQuizList(ArrayList<TestStudentPortalBean> quizList) {
		this.quizList = quizList;
	}
	
	
}
