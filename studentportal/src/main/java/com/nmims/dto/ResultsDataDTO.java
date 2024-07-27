package com.nmims.dto;

import java.util.List;

public class ResultsDataDTO extends BaseDTO{
	private String declareDate; // ResultsFromRedisHelper.KEY_DECLARE_DATE
	private StudentDataDTO studentDetails; // ResultsFromRedisHelper.KEY_STUDENT_DETAILS
	private String size; // ResultsFromRedisHelper.KEY_SIZE
	private String mostRecentResultPeriod; // ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD
	private List<MarksDataDTO> studentMarksList; // ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST
	private List<PassFailDataDTO> passFailStatus; // ResultsFromRedisHelper.KEY_PASSFAIL_STATUS
	private List<MarksDataDTO> studentMarksHistory; // ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY

	public ResultsDataDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResultsDataDTO(String sapid) {
		super(sapid);
		// TODO Auto-generated constructor stub
	}

	public ResultsDataDTO(String sapid, List<PassFailDataDTO> passFailStatus) {
		super(sapid);
		this.passFailStatus = passFailStatus;
	}

	public ResultsDataDTO(String sapid, List<MarksDataDTO> studentMarksList, List<MarksDataDTO> studentMarksHistory) {
		super(sapid);
		this.studentMarksList = studentMarksList;
		this.studentMarksHistory = studentMarksHistory;
	}

	public String getDeclareDate() {
		return declareDate;
	}

	public void setDeclareDate(String declareDate) {
		this.declareDate = declareDate;
	}

	public StudentDataDTO getStudentDetails() {
		return studentDetails;
	}

	public void setStudentDetails(StudentDataDTO studentDetails) {
		this.studentDetails = studentDetails;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMostRecentResultPeriod() {
		return mostRecentResultPeriod;
	}

	public void setMostRecentResultPeriod(String mostRecentResultPeriod) {
		this.mostRecentResultPeriod = mostRecentResultPeriod;
	}

	public List<MarksDataDTO> getStudentMarksList() {
		return studentMarksList;
	}

	public void setStudentMarksList(List<MarksDataDTO> studentMarksList) {
		this.studentMarksList = studentMarksList;
	}

	public List<PassFailDataDTO> getPassFailStatus() {
		return passFailStatus;
	}

	public void setPassFailStatus(List<PassFailDataDTO> passFailStatus) {
		this.passFailStatus = passFailStatus;
	}

	public List<MarksDataDTO> getStudentMarksHistory() {
		return studentMarksHistory;
	}

	public void setStudentMarksHistory(List<MarksDataDTO> studentMarksHistory) {
		this.studentMarksHistory = studentMarksHistory;
	}
}
