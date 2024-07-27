package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAExamBookingDetailsResponseBean  implements Serializable   {
	
	private String sapid;
	private MBAStudentDetailsBean currentRegistrationDetails;
	private List<String> subjectsAppliedFor;
	private List<MBAExamBookingRequest> bookings;
	private List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList;
	
	private boolean examBookingLive;
	private boolean canBook;
	private boolean bookingForResit;
	private boolean reAppearingForSem;
	private String canNotBookReason;

	public boolean isCanBook() {
		return canBook;
	}

	public void setCanBook(boolean canBook) {
		this.canBook = canBook;
	}

	public String getCanNotBookReason() {
		return canNotBookReason;
	}

	public void setCanNotBookReason(String canNotBookReason) {
		this.canNotBookReason = canNotBookReason;
	}

	public List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsList() {
		return failedSubjectsList;
	}

	public void setFailedSubjectsList(List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList) {
		this.failedSubjectsList = failedSubjectsList;
	}

	@Override
	public String toString() {
		return "MBAExamBookingDetailsResponseBean [bookings=" + bookings + ", failedSubjectsList=" + failedSubjectsList
				+ ", toString()=" + super.toString() + "]";
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public MBAStudentDetailsBean getCurrentRegistrationDetails() {
		return currentRegistrationDetails;
	}

	public void setCurrentRegistrationDetails(MBAStudentDetailsBean currentRegistrationDetails) {
		this.currentRegistrationDetails = currentRegistrationDetails;
	}

	public boolean isExamBookingLive() {
		return examBookingLive;
	}

	public void setExamBookingLive(boolean examBookingLive) {
		this.examBookingLive = examBookingLive;
	}

	public List<String> getSubjectsAppliedFor() {
		return subjectsAppliedFor;
	}

	public void setSubjectsAppliedFor(List<String> subjectsAppliedFor) {
		this.subjectsAppliedFor = subjectsAppliedFor;
	}

	public List<MBAExamBookingRequest> getBookings() {
		return bookings;
	}

	public void setBookings(List<MBAExamBookingRequest> bookings) {
		this.bookings = bookings;
	}

	public boolean isBookingForResit() {
		return bookingForResit;
	}

	public void setBookingForResit(boolean bookingForResit) {
		this.bookingForResit = bookingForResit;
	}

	public boolean isReAppearingForSem() {
		return reAppearingForSem;
	}

	public void setReAppearingForSem(boolean reAppearingForSem) {
		this.reAppearingForSem = reAppearingForSem;
	}

}
