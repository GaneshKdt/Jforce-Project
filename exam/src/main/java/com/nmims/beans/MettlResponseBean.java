package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MettlResponseBean extends BaseExamBean  implements Serializable  {
	private int id;
	private int assessments_id;
	private String name;
	private String customAssessmentName;
	private String timebound_id;
	private String sifySubjectCode;
	private String schedule_id;
	private String schedule_name;
	private String schedule_accessKey;
	private String schedule_accessUrl;
	private String schedule_status;
	private String status;
	private String message;
	private String startOnDate;
	private String endsOnDate;
	private String startsOnTime;
	private String endsOnTime;
	private String active;
	private int totalMarks;
	private String [] results;
	private String sapid;
	private String student_name;
	private String batchId;
	private String email;
	private String previous_schedule_id;
	private String previous_score;
	private ArrayList<MettlResponseBean> mettlScheduleResponseBeanList;
	private int prgm_sem_subj_id;
	private String report_link;
	private String test_date;
	private String test_time;
	private String max_marks;
	
	private String subject;
	private String error;
	private String studentType;
	private String programType;
	
	private Integer duration;

	private boolean resultProcessed;
	
	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	@Override
	public String toString() {
		return "MettlResponseBean [id=" + id + ", assessments_id=" + assessments_id + ", name=" + name
				+ ", customAssessmentName=" + customAssessmentName + ", timebound_id=" + timebound_id
				+ ", sifySubjectCode=" + sifySubjectCode + ", schedule_id=" + schedule_id + ", schedule_name="
				+ schedule_name + ", schedule_accessKey=" + schedule_accessKey + ", schedule_accessUrl="
				+ schedule_accessUrl + ", schedule_status=" + schedule_status + ", status=" + status + ", message="
				+ message + ", startOnDate=" + startOnDate + ", endsOnDate=" + endsOnDate + ", startsOnTime="
				+ startsOnTime + ", endsOnTime=" + endsOnTime + ", active=" + active + ", totalMarks=" + totalMarks
				+ ", results=" + Arrays.toString(results) + ", sapid=" + sapid + ", student_name=" + student_name
				+ ", batchId=" + batchId + ", email=" + email + ", previous_schedule_id=" + previous_schedule_id
				+ ", previous_score=" + previous_score + ", mettlScheduleResponseBeanList="
				+ mettlScheduleResponseBeanList + ", prgm_sem_subj_id=" + prgm_sem_subj_id + ", report_link="
				+ report_link + ", test_date=" + test_date + ", test_time=" + test_time + ", max_marks=" + max_marks
				+ "]";
	}
	
	public ArrayList<MettlResponseBean> getMettlScheduleResponseBeanList() {
		return mettlScheduleResponseBeanList;
	}
	public void setMettlScheduleResponseBeanList(ArrayList<MettlResponseBean> mettlScheduleResponseBeanList) {
		this.mettlScheduleResponseBeanList = mettlScheduleResponseBeanList;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getStartOnDate() {
		return startOnDate;
	}
	public void setStartOnDate(String startOnDate) {
		this.startOnDate = startOnDate;
	}
	public String getEndsOnDate() {
		return endsOnDate;
	}
	public void setEndsOnDate(String endsOnDate) {
		this.endsOnDate = endsOnDate;
	}
	public String getStartsOnTime() {
		return startsOnTime;
	}
	public void setStartsOnTime(String startsOnTime) {
		this.startsOnTime = startsOnTime;
	}
	public String getEndsOnTime() {
		return endsOnTime;
	}
	public void setEndsOnTime(String endsOnTime) {
		this.endsOnTime = endsOnTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAssessments_id() {
		return assessments_id;
	}
	public void setAssessments_id(int assessments_id) {
		this.assessments_id = assessments_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustomAssessmentName() {
		return customAssessmentName;
	}
	public void setCustomAssessmentName(String customAssessmentName) {
		this.customAssessmentName = customAssessmentName;
	}
	public String getTimebound_id() {
		return timebound_id;
	}
	public void setTimebound_id(String timebound_id) {
		this.timebound_id = timebound_id;
	}
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getSchedule_name() {
		return schedule_name;
	}
	public void setSchedule_name(String schedule_name) {
		this.schedule_name = schedule_name;
	}
	public String getSchedule_accessKey() {
		return schedule_accessKey;
	}
	public void setSchedule_accessKey(String schedule_accessKey) {
		this.schedule_accessKey = schedule_accessKey;
	}
	public String getSchedule_accessUrl() {
		return schedule_accessUrl;
	}
	public void setSchedule_accessUrl(String schedule_accessUrl) {
		this.schedule_accessUrl = schedule_accessUrl;
	}
	public String getSchedule_status() {
		return schedule_status;
	}
	public void setSchedule_status(String schedule_status) {
		this.schedule_status = schedule_status;
	}
	public int getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}
	public String [] getResults() {
		return results;
	}
	public void setResults(String [] results) {
		this.results = results;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPrevious_schedule_id() {
		return previous_schedule_id;
	}
	public void setPrevious_schedule_id(String previous_schedule_id) {
		this.previous_schedule_id = previous_schedule_id;
	}
	public String getPrevious_score() {
		return previous_score;
	}
	public void setPrevious_score(String previous_score) {
		this.previous_score = previous_score;
	}
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getReport_link() {
		return report_link;
	}
	public void setReport_link(String report_link) {
		this.report_link = report_link;
	}
	

	public String getTest_date() {
		return test_date;
	}
	public void setTest_date(String test_date) {
		this.test_date = test_date;
	}
	public String getTest_time() {
		return test_time;
	}
	public void setTest_time(String test_time) {
		this.test_time = test_time;
	}
	public String getMax_marks() {
		return max_marks;
	}
	public void setMax_marks(String max_marks) {
		this.max_marks = max_marks;
	}
	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public boolean isResultProcessed() {
		return resultProcessed;
	}

	public void setResultProcessed(boolean resultProcessed) {
		this.resultProcessed = resultProcessed;
	}
	
}
