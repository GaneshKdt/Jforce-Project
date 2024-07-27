package com.nmims.beans;

import java.util.ArrayList;
import java.util.List;

public class MettlFetchTestResultBean {
	String schedule_accessKey;
	List<MettlPGResponseBean> confirmBookingList = new ArrayList<MettlPGResponseBean>();
	List<MettlPGResponseBean> failureResponse = new ArrayList<MettlPGResponseBean>();
	int bookingcount ;
	int successcount ;
	int transferredCount;
	int bodAppliedCount;
	boolean pullTaskCompleted;
	boolean pullProcessStart;
	String examMonth;
	String examYear;
	
	public String getSchedule_accessKey() {
		return schedule_accessKey;
	}
	public void setSchedule_accessKey(String schedule_accessKey) {
		this.schedule_accessKey = schedule_accessKey;
	}
	public List<MettlPGResponseBean> getConfirmBookingList() {
		return confirmBookingList;
	}
	public void setConfirmBookingList(List<MettlPGResponseBean> confirmBookingList) {
		this.confirmBookingList = confirmBookingList;
	}
	public List<MettlPGResponseBean> getFailureResponse() {
		return failureResponse;
	}
	public void setFailureResponse(List<MettlPGResponseBean> failureResponse) {
		this.failureResponse = failureResponse;
	}
	public int getSuccesscount() {
		return successcount;
	}
	public void setSuccesscount(int successcount) {
		this.successcount = successcount;
	}
	public int getBookingcount() {
		return bookingcount;
	}
	public void setBookingcount(int bookingcount) {
		this.bookingcount = bookingcount;
	}
	public boolean isPullTaskCompleted() {
		return pullTaskCompleted;
	}
	public void setPullTaskCompleted(boolean pullTaskCompleted) {
		this.pullTaskCompleted = pullTaskCompleted;
	}
	public boolean isPullProcessStart() {
		return pullProcessStart;
	}
	public void setPullProcessStart(boolean pullProcessStart) {
		this.pullProcessStart = pullProcessStart;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public int getTransferredCount() {
		return transferredCount;
	}
	public void setTransferredCount(int transferredCount) {
		this.transferredCount = transferredCount;
	}
	public int getBodAppliedCount() {
		return bodAppliedCount;
	}
	public void setBodAppliedCount(int bodAppliedCount) {
		this.bodAppliedCount = bodAppliedCount;
	}
}
