package com.nmims.beans;

public class ScheduleCreationBean {
	
	private String programType;
	private String assessments_id;
	private String batch_id;
	private String subject_id;
	private String slotDate_id;
	private String slotTime_id;
	private String assessmentName;
	private String assessmentCustomName;
	private String assessmentDuration;
	private String startDate;
	/* private String link_id; */
	private String max_score;
	private String timeboundId;
	private String accessDuration;
	private String batchName;
	private String webProctoring;
	private String waitingRoom;

	public String getWaitingRoom() {
		return waitingRoom;
	}
	public void setWaitingRoom(String waitingRoom) {
		this.waitingRoom = waitingRoom;
	}
	public String getWebProctoring() {
		return webProctoring;
	}
	public void setWebProctoring(String webProctoring) {
		this.webProctoring = webProctoring;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getAccessDuration() {
		return accessDuration;
	}
	public void setAccessDuration(String accessDuration) {
		this.accessDuration = accessDuration;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	
	public String getMax_score() {
		return max_score;
	}
	public void setMax_score(String max_score) {
		this.max_score = max_score;
	}

	/*
	 * public String getLink_id() { return link_id; } public void setLink_id(String
	 * link_id) { this.link_id = link_id; }
	 */
	public String getAssessmentName() {
		return assessmentName;
	}
	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}
	public String getAssessmentCustomName() {
		return assessmentCustomName;
	}
	public void setAssessmentCustomName(String assessmentCustomName) {
		this.assessmentCustomName = assessmentCustomName;
	}
	public String getAssessmentDuration() {
		return assessmentDuration;
	}
	public void setAssessmentDuration(String assessmentDuration) {
		this.assessmentDuration = assessmentDuration;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getAssessments_id() {
		return assessments_id;
	}
	public void setAssessments_id(String assessments_id) {
		this.assessments_id = assessments_id;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public String getSubject_id() {
		return subject_id;
	}
	public void setSubject_id(String subject_id) {
		this.subject_id = subject_id;
	}
	public String getSlotDate_id() {
		return slotDate_id;
	}
	public void setSlotDate_id(String slotDate_id) {
		this.slotDate_id = slotDate_id;
	}
	public String getSlotTime_id() {
		return slotTime_id;
	}
	public void setSlotTime_id(String slotTime_id) {
		this.slotTime_id = slotTime_id;
	}
	
	@Override
	public String toString()
	{
		return "ProgramType:"+getProgramType()+",AssessmentId:"+getAssessments_id()+",AssessmentName:"+getAssessmentName()+",AssessmentCustomName:"+getAssessmentCustomName()
		+",AssessmentDuration:"+getAssessmentDuration()+",BatchId:"+getBatch_id()+",SubjectName:"+getSubject_id()+",SlotDate:"+getSlotDate_id()
		+",SlotTime:"+getSlotTime_id()+",SubjectStartDate:"+getStartDate()+",SubjectTimeBoundId:"+getTimeboundId()+",Max Marks:"+getMax_score()+",Access Duration:"+getAccessDuration()
		+ ",Batchname:"+getBatchName()+",Web Proctoring:"+getWebProctoring();
		
	}

}
