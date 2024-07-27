package com.nmims.beans;

import java.util.ArrayList;
import java.util.List;

public class StudentSubjectMappingConfigBean {

	private String defaultBookingStartTime;
	private String defaultBookingEndTime;
	private String examYear;
	private String examMonth;
	private List<StudentSubjectMappingPhaseBean> phaseList = new ArrayList<>();

	public StudentSubjectMappingConfigBean() {
		super();
	}

	public String getDefaultBookingStartTime() {
		return defaultBookingStartTime;
	}

	public void setDefaultBookingStartTime(String defaultBookingStartTime) {
		this.defaultBookingStartTime = defaultBookingStartTime;
	}

	public String getDefaultBookingEndTime() {
		return defaultBookingEndTime;
	}

	public void setDefaultBookingEndTime(String defaultBookingEndTime) {
		this.defaultBookingEndTime = defaultBookingEndTime;
	}

	public class StudentSubjectMappingPhaseBean {

		String acadYear;
		String acadMonth;
		String bookingStartTime;
		String bookingEndTime;

		public StudentSubjectMappingPhaseBean(String acadYear, String acadMonth, String bookingStartTime,
				String bookingEndTime) {
			super();
			this.acadYear = acadYear;
			this.acadMonth = acadMonth;
			this.bookingStartTime = bookingStartTime;
			this.bookingEndTime = bookingEndTime;
		}

		public String getAcadCycle() {
			return acadMonth.trim() + acadYear.trim();
		}

		public String getAcadYear() {
			return acadYear;
		}

		public void setAcadYear(String acadYear) {
			this.acadYear = acadYear;
		}

		public String getAcadMonth() {
			return acadMonth;
		}

		public void setAcadMonth(String acadMonth) {
			this.acadMonth = acadMonth;
		}

		public String getBookingStartTime() {
			return bookingStartTime;
		}

		public void setBookingStartTime(String bookingStartTime) {
			this.bookingStartTime = bookingStartTime;
		}

		public String getBookingEndTime() {
			return bookingEndTime;
		}

		public void setBookingEndTime(String bookingEndTime) {
			this.bookingEndTime = bookingEndTime;
		}

		@Override
		public String toString() {
			return "StudentSubjectMappingPhaseBean [acadYear=" + acadYear + ", acadMonth=" + acadMonth
					+ ", bookingStartTime=" + bookingStartTime + ", bookingEndTime=" + bookingEndTime + "]";
		}

	}

	public List<StudentSubjectMappingPhaseBean> getPhaseList() {
		return phaseList;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	@Override
	public String toString() {
		return "StudentSubjectMappingConfigBean [defaultBookingStartTime=" + defaultBookingStartTime
				+ ", defaultBookingEndTime=" + defaultBookingEndTime + ", phaseBeans=" + phaseList + "]";
	}

}
