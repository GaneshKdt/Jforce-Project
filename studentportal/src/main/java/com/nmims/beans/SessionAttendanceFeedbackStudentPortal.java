package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - SessionAttendanceFeedback
 * @author
 *
 */
public class SessionAttendanceFeedbackStudentPortal extends BaseStudentPortalBean   implements Serializable  {
    private String sapId;
    private String sessionId;
    private String attended;
    private String attendTime;
    private String q1Response;
    private String q2Response;
    private String q3Response;
    private String q4Response;
    private String q5Response;
    private String q6Response;
    private String q7Response;
    private String q8Response;

    private String q1Remark;
    private String q2Remark;
    private String q3Remark;
    private String q4Remark;
    private String q5Remark;
    private String q6Remark;
    private String q7Remark;
    private String q8Remark;

    private String reAttendTime;
    private String feedbackTime;
    private String feedbackGiven;
    private String feedbackRemarks;

    private String date;
    private String startTime;
    private String day;
    private String subject;
    private String sessionName;
    private String firstName;
    private String lastName;
    private String facultyId;
    private String facultyFirstName;
    private String facultyLastName;
    private String studentConfirmationForAttendance;
    private String reasonForNotAttending;
    private String otherReasonForNotAttending;
    private String studentReviewAvg;
    private String id;
    private String conducted;

    private String track;



    public String getConducted() {
        return conducted;
    }

    public void setConducted(String conducted) {
        this.conducted = conducted;
    }

    public String getStudentConfirmationForAttendance() {
        return studentConfirmationForAttendance;
    }

    public void setStudentConfirmationForAttendance(String studentConfirmationForAttendance) {
        this.studentConfirmationForAttendance = studentConfirmationForAttendance;
    }

    public String getReasonForNotAttending() {
        return reasonForNotAttending;
    }

    public void setReasonForNotAttending(String reasonForNotAttending) {
        this.reasonForNotAttending = reasonForNotAttending;
    }

    public String getOtherReasonForNotAttending() {
        return otherReasonForNotAttending;
    }

    public void setOtherReasonForNotAttending(String otherReasonForNotAttending) {
        this.otherReasonForNotAttending = otherReasonForNotAttending;
    }

    public String getQ1Remark() {
        return q1Remark;
    }

    public void setQ1Remark(String q1Remark) {
        this.q1Remark = q1Remark;
    }

    public String getQ2Remark() {
        return q2Remark;
    }

    public void setQ2Remark(String q2Remark) {
        this.q2Remark = q2Remark;
    }

    public String getQ3Remark() {
        return q3Remark;
    }

    public void setQ3Remark(String q3Remark) {
        this.q3Remark = q3Remark;
    }

    public String getQ4Remark() {
        return q4Remark;
    }

    public void setQ4Remark(String q4Remark) {
        this.q4Remark = q4Remark;
    }

    public String getQ5Remark() {
        return q5Remark;
    }

    public void setQ5Remark(String q5Remark) {
        this.q5Remark = q5Remark;
    }

    public String getQ6Remark() {
        return q6Remark;
    }

    public void setQ6Remark(String q6Remark) {
        this.q6Remark = q6Remark;
    }

    public String getQ7Remark() {
        return q7Remark;
    }

    public void setQ7Remark(String q7Remark) {
        this.q7Remark = q7Remark;
    }

    public String getQ8Remark() {
        return q8Remark;
    }

    public void setQ8Remark(String q8Remark) {
        this.q8Remark = q8Remark;
    }

    public String getQ8Response() {
        return q8Response;
    }

    public void setQ8Response(String q8Response) {
        this.q8Response = q8Response;
    }

    public String getQ6Response() {
        return q6Response;
    }

    public void setQ6Response(String q6Response) {
        this.q6Response = q6Response;
    }

    public String getQ7Response() {
        return q7Response;
    }

    public void setQ7Response(String q7Response) {
        this.q7Response = q7Response;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	public String getFacultyFirstName() {
        return facultyFirstName;
    }

    public void setFacultyFirstName(String facultyFirstName) {
        this.facultyFirstName = facultyFirstName;
    }

    public String getFacultyLastName() {
        return facultyLastName;
    }

    public void setFacultyLastName(String facultyLastName) {
        this.facultyLastName = facultyLastName;
    }

    public String getFeedbackRemarks() {
        return feedbackRemarks;
    }

    public void setFeedbackRemarks(String feedbackRemarks) {
        this.feedbackRemarks = feedbackRemarks;
    }

    public String getSapId() {
        return sapId;
    }

    public void setSapId(String sapId) {
        this.sapId = sapId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAttended() {
        return attended;
    }

    public void setAttended(String attended) {
        this.attended = attended;
    }

    public String getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(String attendTime) {
        this.attendTime = attendTime;
    }

    public String getReAttendTime() {
        return reAttendTime;
    }

    public void setReAttendTime(String reAttendTime) {
        this.reAttendTime = reAttendTime;
    }

    public String getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(String feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public String getFeedbackGiven() {
        return feedbackGiven;
    }

    public void setFeedbackGiven(String feedbackGiven) {
        this.feedbackGiven = feedbackGiven;
    }

    public String getQ1Response() {
        return q1Response;
    }

    public void setQ1Response(String q1Response) {
        this.q1Response = q1Response;
    }

    public String getQ2Response() {
        return q2Response;
    }

    public void setQ2Response(String q2Response) {
        this.q2Response = q2Response;
    }

    public String getQ3Response() {
        return q3Response;
    }

    public void setQ3Response(String q3Response) {
        this.q3Response = q3Response;
    }

    public String getQ4Response() {
        return q4Response;
    }

    public void setQ4Response(String q4Response) {
        this.q4Response = q4Response;
    }

    public String getQ5Response() {
        return q5Response;
    }

    public void setQ5Response(String q5Response) {
        this.q5Response = q5Response;
    }

    public String getStudentReviewAvg() {
        return ((Integer.parseInt(q5Response) + Integer.parseInt(q6Response) + Integer.parseInt(q7Response) + Integer.parseInt(q8Response)) / 4) + "";
    }

    public void setStudentReviewAvg(String studentReviewAvg) {
        this.studentReviewAvg = studentReviewAvg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }


    @Override
	public String toString() {
		return "SessionAttendanceFeedbackStudentPortal [sapId=" + sapId + ", sessionId=" + sessionId + ", attended=" + attended
				+ ", attendTime=" + attendTime + ", q1Response=" + q1Response + ", q2Response=" + q2Response
				+ ", q3Response=" + q3Response + ", q4Response=" + q4Response + ", q5Response=" + q5Response
				+ ", q6Response=" + q6Response + ", q7Response=" + q7Response + ", q8Response=" + q8Response
				+ ", q1Remark=" + q1Remark + ", q2Remark=" + q2Remark + ", q3Remark=" + q3Remark + ", q4Remark="
				+ q4Remark + ", q5Remark=" + q5Remark + ", q6Remark=" + q6Remark + ", q7Remark=" + q7Remark
				+ ", q8Remark=" + q8Remark + ", reAttendTime=" + reAttendTime + ", feedbackTime=" + feedbackTime
				+ ", feedbackGiven=" + feedbackGiven + ", feedbackRemarks=" + feedbackRemarks + ", date=" + date
				+ ", startTime=" + startTime + ", day=" + day + ", subject=" + subject + ", sessionName=" + sessionName
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", facultyId=" + facultyId
				+ ", facultyFirstName=" + facultyFirstName + ", facultyLastName=" + facultyLastName
				+ ", studentConfirmationForAttendance=" + studentConfirmationForAttendance + ", reasonForNotAttending="
				+ reasonForNotAttending + ", otherReasonForNotAttending=" + otherReasonForNotAttending
				+ ", studentReviewAvg=" + studentReviewAvg + ", id=" + id + ", conducted=" + conducted + ", track="
				+ track + "]";
	}

}
