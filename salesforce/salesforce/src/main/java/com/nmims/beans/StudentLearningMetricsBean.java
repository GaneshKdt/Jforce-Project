package com.nmims.beans;

public class StudentLearningMetricsBean {

	/*old code
	 * 
	 * private String sapid;
	private String sem1NoOfANS;
	private String sem1NoOfAssignSubmitted;
	private String sem1ANSSubjects;
	private String sem1SubmittedSubjects;

	private String sem2NoOfANS;
	private String sem2NoOfAssignSubmitted;
	private String sem2ANSSubjects;
	private String sem2SubmittedSubjects;

	private String sem3NoOfANS;
	private String sem3NoOfAssignSubmitted;
	private String sem3ANSSubjects;
	private String sem3SubmittedSubjects;

	private String sem4NoOfANS;
	private String sem4NoOfAssignSubmitted;
	private String sem4ANSSubjects;
	private String sem4SubmittedSubjects;

	private String totalNoOfANS;
	private String totalNoOfAssignSubmitted;
	private String allANSSubjects;
	private String allSubmittedSubjects;
	
	private String sem1NoOfPassedSubjects;
	private String sem1NoOfFailedSubjects;
	private String sem1PassedSubjects;
	private String sem1FailedSubjects;

	private String sem2NoOfPassedSubjects;
	private String sem2NoOfFailedSubjects;
	private String sem2PassedSubjects;
	private String sem2FailedSubjects;
	
	private String sem3NoOfPassedSubjects;
	private String sem3NoOfFailedSubjects;
	private String sem3PassedSubjects;
	private String sem3FailedSubjects;
	
	private String sem4NoOfPassedSubjects;
	private String sem4NoOfFailedSubjects;
	private String sem4PassedSubjects;
	private String sem4FailedSubjects;
	
	private String totalNoOfPassedSubjects;
	private String totalNoOfFailedSubjects;
	private String allPassedSubjects;
	private String allFailedSubjects;
	
	private String sem1NoOfBookedSubjects;
	private String sem1NoOfBookingPendingSubjects;
	private String sem1BookedSubjects;
	private String sem1BookingPendingSubjects;
	
	private String sem2NoOfBookedSubjects;
	private String sem2NoOfBookingPendingSubjects;
	private String sem2BookedSubjects;
	private String sem2BookingPendingSubjects;
	
	private String sem3NoOfBookedSubjects;
	private String sem3NoOfBookingPendingSubjects;
	private String sem3BookedSubjects;
	private String sem3BookingPendingSubjects;
	
	private String sem4NoOfBookedSubjects;
	private String sem4NoOfBookingPendingSubjects;
	private String sem4BookedSubjects;
	private String sem4BookingPendingSubjects;
	
	private String totalNoOfBookedSubjects;
	private String totalNoOfBookingPendingSubjects;
	private String allBookedSubjects;
	private String allBookingPendingSubjects;
	
	private String sem1NoOfLecturesAttended;
	private String sem2NoOfLecturesAttended;
	private String sem3NoOfLecturesAttended;
	private String sem4NoOfLecturesAttended;

	private String reRegProbability;
	private String noOfSubjetsApplicable;
	
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem1NoOfANS() {
		return sem1NoOfANS;
	}
	public void setSem1NoOfANS(String sem1NoOfANS) {
		this.sem1NoOfANS = sem1NoOfANS;
	}
	public String getSem1NoOfAssignSubmitted() {
		return sem1NoOfAssignSubmitted;
	}
	public void setSem1NoOfAssignSubmitted(String sem1NoOfAssignSubmitted) {
		this.sem1NoOfAssignSubmitted = sem1NoOfAssignSubmitted;
	}
	public String getSem1ANSSubjects() {
		return sem1ANSSubjects;
	}
	public void setSem1ANSSubjects(String sem1ansSubjects) {
		sem1ANSSubjects = sem1ansSubjects;
	}
	public String getSem1SubmittedSubjects() {
		return sem1SubmittedSubjects;
	}
	public void setSem1SubmittedSubjects(String sem1SubmittedSubjects) {
		this.sem1SubmittedSubjects = sem1SubmittedSubjects;
	}
	public String getSem2NoOfANS() {
		return sem2NoOfANS;
	}
	public void setSem2NoOfANS(String sem2NoOfANS) {
		this.sem2NoOfANS = sem2NoOfANS;
	}
	public String getSem2NoOfAssignSubmitted() {
		return sem2NoOfAssignSubmitted;
	}
	public void setSem2NoOfAssignSubmitted(String sem2NoOfAssignSubmitted) {
		this.sem2NoOfAssignSubmitted = sem2NoOfAssignSubmitted;
	}
	public String getSem2ANSSubjects() {
		return sem2ANSSubjects;
	}
	public void setSem2ANSSubjects(String sem2ansSubjects) {
		sem2ANSSubjects = sem2ansSubjects;
	}
	public String getSem2SubmittedSubjects() {
		return sem2SubmittedSubjects;
	}
	public void setSem2SubmittedSubjects(String sem2SubmittedSubjects) {
		this.sem2SubmittedSubjects = sem2SubmittedSubjects;
	}
	public String getSem3NoOfANS() {
		return sem3NoOfANS;
	}
	public void setSem3NoOfANS(String sem3NoOfANS) {
		this.sem3NoOfANS = sem3NoOfANS;
	}
	public String getSem3NoOfAssignSubmitted() {
		return sem3NoOfAssignSubmitted;
	}
	public void setSem3NoOfAssignSubmitted(String sem3NoOfAssignSubmitted) {
		this.sem3NoOfAssignSubmitted = sem3NoOfAssignSubmitted;
	}
	public String getSem3ANSSubjects() {
		return sem3ANSSubjects;
	}
	public void setSem3ANSSubjects(String sem3ansSubjects) {
		sem3ANSSubjects = sem3ansSubjects;
	}
	public String getSem3SubmittedSubjects() {
		return sem3SubmittedSubjects;
	}
	public void setSem3SubmittedSubjects(String sem3SubmittedSubjects) {
		this.sem3SubmittedSubjects = sem3SubmittedSubjects;
	}
	public String getSem4NoOfANS() {
		return sem4NoOfANS;
	}
	public void setSem4NoOfANS(String sem4NoOfANS) {
		this.sem4NoOfANS = sem4NoOfANS;
	}
	public String getSem4NoOfAssignSubmitted() {
		return sem4NoOfAssignSubmitted;
	}
	public void setSem4NoOfAssignSubmitted(String sem4NoOfAssignSubmitted) {
		this.sem4NoOfAssignSubmitted = sem4NoOfAssignSubmitted;
	}
	public String getSem4ANSSubjects() {
		return sem4ANSSubjects;
	}
	public void setSem4ANSSubjects(String sem4ansSubjects) {
		sem4ANSSubjects = sem4ansSubjects;
	}
	public String getSem4SubmittedSubjects() {
		return sem4SubmittedSubjects;
	}
	public void setSem4SubmittedSubjects(String sem4SubmittedSubjects) {
		this.sem4SubmittedSubjects = sem4SubmittedSubjects;
	}
	public String getTotalNoOfANS() {
		return totalNoOfANS;
	}
	public void setTotalNoOfANS(String totalNoOfANS) {
		this.totalNoOfANS = totalNoOfANS;
	}
	public String getTotalNoOfAssignSubmitted() {
		return totalNoOfAssignSubmitted;
	}
	public void setTotalNoOfAssignSubmitted(String totalNoOfAssignSubmitted) {
		this.totalNoOfAssignSubmitted = totalNoOfAssignSubmitted;
	}
	public String getAllANSSubjects() {
		return allANSSubjects;
	}
	public void setAllANSSubjects(String allANSSubjects) {
		this.allANSSubjects = allANSSubjects;
	}
	public String getAllSubmittedSubjects() {
		return allSubmittedSubjects;
	}
	public void setAllSubmittedSubjects(String allSubmittedSubjects) {
		this.allSubmittedSubjects = allSubmittedSubjects;
	}
	public String getSem1NoOfPassedSubjects() {
		return sem1NoOfPassedSubjects;
	}
	public void setSem1NoOfPassedSubjects(String sem1NoOfPassedSubjects) {
		this.sem1NoOfPassedSubjects = sem1NoOfPassedSubjects;
	}
	public String getSem1NoOfFailedSubjects() {
		return sem1NoOfFailedSubjects;
	}
	public void setSem1NoOfFailedSubjects(String sem1NoOfFailedSubjects) {
		this.sem1NoOfFailedSubjects = sem1NoOfFailedSubjects;
	}
	public String getSem1PassedSubjects() {
		return sem1PassedSubjects;
	}
	public void setSem1PassedSubjects(String sem1PassedSubjects) {
		this.sem1PassedSubjects = sem1PassedSubjects;
	}
	public String getSem1FailedSubjects() {
		return sem1FailedSubjects;
	}
	public void setSem1FailedSubjects(String sem1FailedSubjects) {
		this.sem1FailedSubjects = sem1FailedSubjects;
	}
	public String getSem2NoOfPassedSubjects() {
		return sem2NoOfPassedSubjects;
	}
	public void setSem2NoOfPassedSubjects(String sem2NoOfPassedSubjects) {
		this.sem2NoOfPassedSubjects = sem2NoOfPassedSubjects;
	}
	public String getSem2NoOfFailedSubjects() {
		return sem2NoOfFailedSubjects;
	}
	public void setSem2NoOfFailedSubjects(String sem2NoOfFailedSubjects) {
		this.sem2NoOfFailedSubjects = sem2NoOfFailedSubjects;
	}
	public String getSem2PassedSubjects() {
		return sem2PassedSubjects;
	}
	public void setSem2PassedSubjects(String sem2PassedSubjects) {
		this.sem2PassedSubjects = sem2PassedSubjects;
	}
	public String getSem2FailedSubjects() {
		return sem2FailedSubjects;
	}
	public void setSem2FailedSubjects(String sem2FailedSubjects) {
		this.sem2FailedSubjects = sem2FailedSubjects;
	}
	public String getSem3NoOfPassedSubjects() {
		return sem3NoOfPassedSubjects;
	}
	public void setSem3NoOfPassedSubjects(String sem3NoOfPassedSubjects) {
		this.sem3NoOfPassedSubjects = sem3NoOfPassedSubjects;
	}
	public String getSem3NoOfFailedSubjects() {
		return sem3NoOfFailedSubjects;
	}
	public void setSem3NoOfFailedSubjects(String sem3NoOfFailedSubjects) {
		this.sem3NoOfFailedSubjects = sem3NoOfFailedSubjects;
	}
	public String getSem3PassedSubjects() {
		return sem3PassedSubjects;
	}
	public void setSem3PassedSubjects(String sem3PassedSubjects) {
		this.sem3PassedSubjects = sem3PassedSubjects;
	}
	public String getSem3FailedSubjects() {
		return sem3FailedSubjects;
	}
	public void setSem3FailedSubjects(String sem3FailedSubjects) {
		this.sem3FailedSubjects = sem3FailedSubjects;
	}
	public String getSem4NoOfPassedSubjects() {
		return sem4NoOfPassedSubjects;
	}
	public void setSem4NoOfPassedSubjects(String sem4NoOfPassedSubjects) {
		this.sem4NoOfPassedSubjects = sem4NoOfPassedSubjects;
	}
	public String getSem4NoOfFailedSubjects() {
		return sem4NoOfFailedSubjects;
	}
	public void setSem4NoOfFailedSubjects(String sem4NoOfFailedSubjects) {
		this.sem4NoOfFailedSubjects = sem4NoOfFailedSubjects;
	}
	public String getSem4PassedSubjects() {
		return sem4PassedSubjects;
	}
	public void setSem4PassedSubjects(String sem4PassedSubjects) {
		this.sem4PassedSubjects = sem4PassedSubjects;
	}
	public String getSem4FailedSubjects() {
		return sem4FailedSubjects;
	}
	public void setSem4FailedSubjects(String sem4FailedSubjects) {
		this.sem4FailedSubjects = sem4FailedSubjects;
	}
	public String getTotalNoOfPassedSubjects() {
		return totalNoOfPassedSubjects;
	}
	public void setTotalNoOfPassedSubjects(String totalNoOfPassedSubjects) {
		this.totalNoOfPassedSubjects = totalNoOfPassedSubjects;
	}
	public String getTotalNoOfFailedSubjects() {
		return totalNoOfFailedSubjects;
	}
	public void setTotalNoOfFailedSubjects(String totalNoOfFailedSubjects) {
		this.totalNoOfFailedSubjects = totalNoOfFailedSubjects;
	}
	public String getAllPassedSubjects() {
		return allPassedSubjects;
	}
	public void setAllPassedSubjects(String allPassedSubjects) {
		this.allPassedSubjects = allPassedSubjects;
	}
	public String getAllFailedSubjects() {
		return allFailedSubjects;
	}
	public void setAllFailedSubjects(String allFailedSubjects) {
		this.allFailedSubjects = allFailedSubjects;
	}
	public String getSem1NoOfBookedSubjects() {
		return sem1NoOfBookedSubjects;
	}
	public void setSem1NoOfBookedSubjects(String sem1NoOfBookedSubjects) {
		this.sem1NoOfBookedSubjects = sem1NoOfBookedSubjects;
	}
	public String getSem1NoOfBookingPendingSubjects() {
		return sem1NoOfBookingPendingSubjects;
	}
	public void setSem1NoOfBookingPendingSubjects(String sem1NoOfBookingPendingSubjects) {
		this.sem1NoOfBookingPendingSubjects = sem1NoOfBookingPendingSubjects;
	}
	public String getSem1BookedSubjects() {
		return sem1BookedSubjects;
	}
	public void setSem1BookedSubjects(String sem1BookedSubjects) {
		this.sem1BookedSubjects = sem1BookedSubjects;
	}
	public String getSem1BookingPendingSubjects() {
		return sem1BookingPendingSubjects;
	}
	public void setSem1BookingPendingSubjects(String sem1BookingPendingSubjects) {
		this.sem1BookingPendingSubjects = sem1BookingPendingSubjects;
	}
	public String getSem2NoOfBookedSubjects() {
		return sem2NoOfBookedSubjects;
	}
	public void setSem2NoOfBookedSubjects(String sem2NoOfBookedSubjects) {
		this.sem2NoOfBookedSubjects = sem2NoOfBookedSubjects;
	}
	public String getSem2NoOfBookingPendingSubjects() {
		return sem2NoOfBookingPendingSubjects;
	}
	public void setSem2NoOfBookingPendingSubjects(String sem2NoOfBookingPendingSubjects) {
		this.sem2NoOfBookingPendingSubjects = sem2NoOfBookingPendingSubjects;
	}
	public String getSem2BookedSubjects() {
		return sem2BookedSubjects;
	}
	public void setSem2BookedSubjects(String sem2BookedSubjects) {
		this.sem2BookedSubjects = sem2BookedSubjects;
	}
	public String getSem2BookingPendingSubjects() {
		return sem2BookingPendingSubjects;
	}
	public void setSem2BookingPendingSubjects(String sem2BookingPendingSubjects) {
		this.sem2BookingPendingSubjects = sem2BookingPendingSubjects;
	}
	public String getSem3NoOfBookedSubjects() {
		return sem3NoOfBookedSubjects;
	}
	public void setSem3NoOfBookedSubjects(String sem3NoOfBookedSubjects) {
		this.sem3NoOfBookedSubjects = sem3NoOfBookedSubjects;
	}
	public String getSem3NoOfBookingPendingSubjects() {
		return sem3NoOfBookingPendingSubjects;
	}
	public void setSem3NoOfBookingPendingSubjects(String sem3NoOfBookingPendingSubjects) {
		this.sem3NoOfBookingPendingSubjects = sem3NoOfBookingPendingSubjects;
	}
	public String getSem3BookedSubjects() {
		return sem3BookedSubjects;
	}
	public void setSem3BookedSubjects(String sem3BookedSubjects) {
		this.sem3BookedSubjects = sem3BookedSubjects;
	}
	public String getSem3BookingPendingSubjects() {
		return sem3BookingPendingSubjects;
	}
	public void setSem3BookingPendingSubjects(String sem3BookingPendingSubjects) {
		this.sem3BookingPendingSubjects = sem3BookingPendingSubjects;
	}
	public String getSem4NoOfBookedSubjects() {
		return sem4NoOfBookedSubjects;
	}
	public void setSem4NoOfBookedSubjects(String sem4NoOfBookedSubjects) {
		this.sem4NoOfBookedSubjects = sem4NoOfBookedSubjects;
	}
	public String getSem4NoOfBookingPendingSubjects() {
		return sem4NoOfBookingPendingSubjects;
	}
	public void setSem4NoOfBookingPendingSubjects(String sem4NoOfBookingPendingSubjects) {
		this.sem4NoOfBookingPendingSubjects = sem4NoOfBookingPendingSubjects;
	}
	public String getSem4BookedSubjects() {
		return sem4BookedSubjects;
	}
	public void setSem4BookedSubjects(String sem4BookedSubjects) {
		this.sem4BookedSubjects = sem4BookedSubjects;
	}
	public String getSem4BookingPendingSubjects() {
		return sem4BookingPendingSubjects;
	}
	public void setSem4BookingPendingSubjects(String sem4BookingPendingSubjects) {
		this.sem4BookingPendingSubjects = sem4BookingPendingSubjects;
	}
	public String getTotalNoOfBookedSubjects() {
		return totalNoOfBookedSubjects;
	}
	public void setTotalNoOfBookedSubjects(String totalNoOfBookedSubjects) {
		this.totalNoOfBookedSubjects = totalNoOfBookedSubjects;
	}
	public String getTotalNoOfBookingPendingSubjects() {
		return totalNoOfBookingPendingSubjects;
	}
	public void setTotalNoOfBookingPendingSubjects(String totalNoOfBookingPendingSubjects) {
		this.totalNoOfBookingPendingSubjects = totalNoOfBookingPendingSubjects;
	}
	public String getAllBookedSubjects() {
		return allBookedSubjects;
	}
	public void setAllBookedSubjects(String allBookedSubjects) {
		this.allBookedSubjects = allBookedSubjects;
	}
	public String getAllBookingPendingSubjects() {
		return allBookingPendingSubjects;
	}
	public void setAllBookingPendingSubjects(String allBookingPendingSubjects) {
		this.allBookingPendingSubjects = allBookingPendingSubjects;
	}
	public String getSem1NoOfLecturesAttended() {
		return sem1NoOfLecturesAttended;
	}
	public void setSem1NoOfLecturesAttended(String sem1NoOfLecturesAttended) {
		this.sem1NoOfLecturesAttended = sem1NoOfLecturesAttended;
	}
	public String getSem2NoOfLecturesAttended() {
		return sem2NoOfLecturesAttended;
	}
	public void setSem2NoOfLecturesAttended(String sem2NoOfLecturesAttended) {
		this.sem2NoOfLecturesAttended = sem2NoOfLecturesAttended;
	}
	public String getSem3NoOfLecturesAttended() {
		return sem3NoOfLecturesAttended;
	}
	public void setSem3NoOfLecturesAttended(String sem3NoOfLecturesAttended) {
		this.sem3NoOfLecturesAttended = sem3NoOfLecturesAttended;
	}
	public String getSem4NoOfLecturesAttended() {
		return sem4NoOfLecturesAttended;
	}
	public void setSem4NoOfLecturesAttended(String sem4NoOfLecturesAttended) {
		this.sem4NoOfLecturesAttended = sem4NoOfLecturesAttended;
	}
	public String getReRegProbability() {
		return reRegProbability;
	}
	public void setReRegProbability(String reRegProbability) {
		this.reRegProbability = reRegProbability;
	}
	public String getNoOfSubjetsApplicable() {
		return noOfSubjetsApplicable;
	}
	public void setNoOfSubjetsApplicable(String noOfSubjetsApplicable) {
		this.noOfSubjetsApplicable = noOfSubjetsApplicable;
	}
	
	@Override
	public String toString() {
		return "StudentLearningMetricsBean [sapid=" + sapid + ", sem1NoOfANS=" + sem1NoOfANS
				+ ", sem1NoOfAssignSubmitted=" + sem1NoOfAssignSubmitted + ", sem1ANSSubjects=" + sem1ANSSubjects
				+ ", sem1SubmittedSubjects=" + sem1SubmittedSubjects + ", sem2NoOfANS=" + sem2NoOfANS
				+ ", sem2NoOfAssignSubmitted=" + sem2NoOfAssignSubmitted + ", sem2ANSSubjects=" + sem2ANSSubjects
				+ ", sem2SubmittedSubjects=" + sem2SubmittedSubjects + ", sem3NoOfANS=" + sem3NoOfANS
				+ ", sem3NoOfAssignSubmitted=" + sem3NoOfAssignSubmitted + ", sem3ANSSubjects=" + sem3ANSSubjects
				+ ", sem3SubmittedSubjects=" + sem3SubmittedSubjects + ", sem4NoOfANS=" + sem4NoOfANS
				+ ", sem4NoOfAssignSubmitted=" + sem4NoOfAssignSubmitted + ", sem4ANSSubjects=" + sem4ANSSubjects
				+ ", sem4SubmittedSubjects=" + sem4SubmittedSubjects + ", totalNoOfANS=" + totalNoOfANS
				+ ", totalNoOfAssignSubmitted=" + totalNoOfAssignSubmitted + ", allANSSubjects=" + allANSSubjects
				+ ", allSubmittedSubjects=" + allSubmittedSubjects + ", sem1NoOfPassedSubjects="
				+ sem1NoOfPassedSubjects + ", sem1NoOfFailedSubjects=" + sem1NoOfFailedSubjects
				+ ", sem1PassedSubjects=" + sem1PassedSubjects + ", sem1FailedSubjects=" + sem1FailedSubjects
				+ ", sem2NoOfPassedSubjects=" + sem2NoOfPassedSubjects + ", sem2NoOfFailedSubjects="
				+ sem2NoOfFailedSubjects + ", sem2PassedSubjects=" + sem2PassedSubjects + ", sem2FailedSubjects="
				+ sem2FailedSubjects + ", sem3NoOfPassedSubjects=" + sem3NoOfPassedSubjects
				+ ", sem3NoOfFailedSubjects=" + sem3NoOfFailedSubjects + ", sem3PassedSubjects=" + sem3PassedSubjects
				+ ", sem3FailedSubjects=" + sem3FailedSubjects + ", sem4NoOfPassedSubjects=" + sem4NoOfPassedSubjects
				+ ", sem4NoOfFailedSubjects=" + sem4NoOfFailedSubjects + ", sem4PassedSubjects=" + sem4PassedSubjects
				+ ", sem4FailedSubjects=" + sem4FailedSubjects + ", totalNoOfPassedSubjects=" + totalNoOfPassedSubjects
				+ ", totalNoOfFailedSubjects=" + totalNoOfFailedSubjects + ", allPassedSubjects=" + allPassedSubjects
				+ ", allFailedSubjects=" + allFailedSubjects + ", sem1NoOfBookedSubjects=" + sem1NoOfBookedSubjects
				+ ", sem1NoOfBookingPendingSubjects=" + sem1NoOfBookingPendingSubjects + ", sem1BookedSubjects="
				+ sem1BookedSubjects + ", sem1BookingPendingSubjects=" + sem1BookingPendingSubjects
				+ ", sem2NoOfBookedSubjects=" + sem2NoOfBookedSubjects + ", sem2NoOfBookingPendingSubjects="
				+ sem2NoOfBookingPendingSubjects + ", sem2BookedSubjects=" + sem2BookedSubjects
				+ ", sem2BookingPendingSubjects=" + sem2BookingPendingSubjects + ", sem3NoOfBookedSubjects="
				+ sem3NoOfBookedSubjects + ", sem3NoOfBookingPendingSubjects=" + sem3NoOfBookingPendingSubjects
				+ ", sem3BookedSubjects=" + sem3BookedSubjects + ", sem3BookingPendingSubjects="
				+ sem3BookingPendingSubjects + ", sem4NoOfBookedSubjects=" + sem4NoOfBookedSubjects
				+ ", sem4NoOfBookingPendingSubjects=" + sem4NoOfBookingPendingSubjects + ", sem4BookedSubjects="
				+ sem4BookedSubjects + ", sem4BookingPendingSubjects=" + sem4BookingPendingSubjects
				+ ", totalNoOfBookedSubjects=" + totalNoOfBookedSubjects + ", totalNoOfBookingPendingSubjects="
				+ totalNoOfBookingPendingSubjects + ", allBookedSubjects=" + allBookedSubjects
				+ ", allBookingPendingSubjects=" + allBookingPendingSubjects + ", sem1NoOfLecturesAttended="
				+ sem1NoOfLecturesAttended + ", sem2NoOfLecturesAttended=" + sem2NoOfLecturesAttended
				+ ", sem3NoOfLecturesAttended=" + sem3NoOfLecturesAttended + ", sem4NoOfLecturesAttended="
				+ sem4NoOfLecturesAttended + ", reRegProbability=" + reRegProbability + ", noOfSubjetsApplicable="
				+ noOfSubjetsApplicable + "]";
	}
	*/
	private String sapid;
	private int sem1NoOfANS;
	private int sem1NoOfAssignSubmitted;
	private String sem1ANSSubjects;
	private String sem1SubmittedSubjects;

	private int sem2NoOfANS;
	private int sem2NoOfAssignSubmitted;
	private String sem2ANSSubjects;
	private String sem2SubmittedSubjects;

	private int sem3NoOfANS;
	private int sem3NoOfAssignSubmitted;
	private String sem3ANSSubjects;
	private String sem3SubmittedSubjects;

	private int sem4NoOfANS;
	private int sem4NoOfAssignSubmitted;
	private String sem4ANSSubjects;
	private String sem4SubmittedSubjects;

	
	
	private int sem1NoOfPassedSubjects;
	private int sem1NoOfFailedSubjects;
	private String sem1PassedSubjects;
	private String sem1FailedSubjects;

	private int sem2NoOfPassedSubjects;
	private int sem2NoOfFailedSubjects;
	private String sem2PassedSubjects;
	private String sem2FailedSubjects;
	
	private int sem3NoOfPassedSubjects;
	private int sem3NoOfFailedSubjects;
	private String sem3PassedSubjects;
	private String sem3FailedSubjects;
	
	private int sem4NoOfPassedSubjects;
	private int sem4NoOfFailedSubjects;
	private String sem4PassedSubjects;
	private String sem4FailedSubjects;
	
	private int totalNoOfPassedSubjects;
	private int totalNoOfFailedSubjects;
	private int totalNoOfLecturedAttended;
	private int totalNoOfANS;
	private int totalNoOfAssignSubmitted;
	private int totalNoOfBookedSubjects;
	private int totalNoOfBookingPendingSubjects;
	
	private String allBookedSubjects;
	private String allBookingPendingSubjects;
	private String allANSSubjects;
	private String allSubmittedSubjects;
	private String allPassedSubjects;
	private String allFailedSubjects;
	
	
	private int sem1NoOfBookedSubjects;
	private int sem1NoOfBookingPendingSubjects;
	private String sem1BookedSubjects;
	private String sem1BookingPendingSubjects;
	
	private int sem2NoOfBookedSubjects;
	private int sem2NoOfBookingPendingSubjects;
	private String sem2BookedSubjects;
	private String sem2BookingPendingSubjects;
	
	private int sem3NoOfBookedSubjects;
	private int sem3NoOfBookingPendingSubjects;
	private String sem3BookedSubjects;
	private String sem3BookingPendingSubjects;
	
	private int sem4NoOfBookedSubjects;
	private int sem4NoOfBookingPendingSubjects;
	private String sem4BookedSubjects;
	private String sem4BookingPendingSubjects;
	
	
	
	private int sem1NoOfLecturesAttended;
	private int sem2NoOfLecturesAttended;
	private int sem3NoOfLecturesAttended;
	private int sem4NoOfLecturesAttended;

	private double reRegProbability;
	private int noOfSubjetsApplicable;
	
	int numberOfSubjectsApplicable;
	int numberOfSubjectsExceptCurrentCycle;
	

	
	public int getNumberOfSubjectsApplicable() {
		return numberOfSubjectsApplicable;
	}



	public void setNumberOfSubjectsApplicable(int numberOfSubjectsApplicable) {
		this.numberOfSubjectsApplicable = numberOfSubjectsApplicable;
	}



	public int getNumberOfSubjectsExceptCurrentCycle() {
		if(numberOfSubjectsExceptCurrentCycle == 0){
			return numberOfSubjectsExceptCurrentCycle = 1; //So that division does not fail
		}else{
			return numberOfSubjectsExceptCurrentCycle = numberOfSubjectsExceptCurrentCycle;
		}
	}



	public void setNumberOfSubjectsExceptCurrentCycle(
			int numberOfSubjectsExceptCurrentCycle) {
		if(numberOfSubjectsExceptCurrentCycle == 0){
			numberOfSubjectsExceptCurrentCycle = 1; //So that division does not fail
		}else{
			numberOfSubjectsExceptCurrentCycle = numberOfSubjectsExceptCurrentCycle;
		}
		this.numberOfSubjectsExceptCurrentCycle = numberOfSubjectsExceptCurrentCycle;
	}



	public String getSapid() {
		return sapid;
	}



	public void setSapid(String sapid) {
		this.sapid = sapid;
	}



	public int getSem1NoOfANS() {
		return sem1NoOfANS;
	}



	public void setSem1NoOfANS(String sem1NoOfANS) {
		this.sem1NoOfANS = Integer.valueOf(sem1NoOfANS) ;
	}



	public int getSem1NoOfAssignSubmitted() {
		return sem1NoOfAssignSubmitted;
	}



	public void setSem1NoOfAssignSubmitted(String sem1NoOfAssignSubmitted) {
		this.sem1NoOfAssignSubmitted = Integer.valueOf(sem1NoOfAssignSubmitted) ;
	}



	public String getSem1ANSSubjects() {
		return sem1ANSSubjects;
	}



	public void setSem1ANSSubjects(String sem1ansSubjects) {
		this.sem1ANSSubjects =  sem1ansSubjects ;
	}



	public String getSem1SubmittedSubjects() {
		return sem1SubmittedSubjects;
	}



	public void setSem1SubmittedSubjects(String sem1SubmittedSubjects) {
		this.sem1SubmittedSubjects = sem1SubmittedSubjects ;
	}



	public int getSem2NoOfANS() {
		return sem2NoOfANS;
	}



	public void setSem2NoOfANS(String sem2NoOfANS) {
		this.sem2NoOfANS =Integer.valueOf(sem2NoOfANS) ;
	}



	public int getSem2NoOfAssignSubmitted() {
		return sem2NoOfAssignSubmitted;
	}



	public void setSem2NoOfAssignSubmitted(String sem2NoOfAssignSubmitted) {
		this.sem2NoOfAssignSubmitted =Integer.valueOf(sem2NoOfAssignSubmitted)  ;
	}



	public String getSem2ANSSubjects() {
		return sem2ANSSubjects;
	}



	public void setSem2ANSSubjects(String sem2ansSubjects) {
		sem2ANSSubjects = sem2ansSubjects ;
	}



	public String getSem2SubmittedSubjects() {
		return sem2SubmittedSubjects;
	}



	public void setSem2SubmittedSubjects(String sem2SubmittedSubjects) {
		this.sem2SubmittedSubjects = sem2SubmittedSubjects ;
	}



	public int getSem3NoOfANS() {
		return sem3NoOfANS;
	}



	public void setSem3NoOfANS(String sem3NoOfANS) {
		this.sem3NoOfANS = Integer.valueOf(sem3NoOfANS) ;
	}



	public int getSem3NoOfAssignSubmitted() {
		return sem3NoOfAssignSubmitted;
	}


	public void setSem3NoOfAssignSubmitted(String sem3NoOfAssignSubmitted) {
		this.sem3NoOfAssignSubmitted =Integer.valueOf(sem3NoOfAssignSubmitted) ;
	}



	public String getSem3ANSSubjects() {
		return sem3ANSSubjects;
	}



	public void setSem3ANSSubjects(String sem3ansSubjects) {
		sem3ANSSubjects = sem3ansSubjects ;
	}



	public String getSem3SubmittedSubjects() {
		return sem3SubmittedSubjects;
	}



	public void setSem3SubmittedSubjects(String sem3SubmittedSubjects) {
		this.sem3SubmittedSubjects =sem3SubmittedSubjects ;
	}



	public int getSem4NoOfANS() {
		return sem4NoOfANS;
	}



	public void setSem4NoOfANS(String sem4NoOfANS) {
		this.sem4NoOfANS = Integer.valueOf(sem4NoOfANS) ;
	}



	public int getSem4NoOfAssignSubmitted() {
		return sem4NoOfAssignSubmitted;
	}



	public void setSem4NoOfAssignSubmitted(String sem4NoOfAssignSubmitted) {
		this.sem4NoOfAssignSubmitted = Integer.valueOf(sem4NoOfAssignSubmitted) ;
	}



	public String getSem4ANSSubjects() {
		return sem4ANSSubjects;
	}



	public void setSem4ANSSubjects(String sem4ansSubjects) {
		sem4ANSSubjects =sem4ansSubjects ;
	}



	public String getSem4SubmittedSubjects() {
		return sem4SubmittedSubjects;
	}



	public void setSem4SubmittedSubjects(String sem4SubmittedSubjects) {
		this.sem4SubmittedSubjects =sem4SubmittedSubjects  ;
	}



	public int getSem1NoOfPassedSubjects() {
		return sem1NoOfPassedSubjects;
	}



	public void setSem1NoOfPassedSubjects(String sem1NoOfPassedSubjects) {
		this.sem1NoOfPassedSubjects = Integer.valueOf(sem1NoOfPassedSubjects) ;
	}



	public int getSem1NoOfFailedSubjects() {
		return sem1NoOfFailedSubjects;
	}



	public void setSem1NoOfFailedSubjects(String sem1NoOfFailedSubjects) {
		this.sem1NoOfFailedSubjects = Integer.valueOf(sem1NoOfFailedSubjects) ;
	}



	public String getSem1PassedSubjects() {
		return sem1PassedSubjects;
	}



	public void setSem1PassedSubjects(String sem1PassedSubjects) {
		this.sem1PassedSubjects = sem1PassedSubjects ;
	}



	public String getSem1FailedSubjects() {
		return sem1FailedSubjects;
	}



	public void setSem1FailedSubjects(String sem1FailedSubjects) {
		this.sem1FailedSubjects = sem1FailedSubjects ;
	}



	public int getSem2NoOfPassedSubjects() {
		return sem2NoOfPassedSubjects;
	}



	public void setSem2NoOfPassedSubjects(String sem2NoOfPassedSubjects) {
		this.sem2NoOfPassedSubjects = Integer.valueOf(sem2NoOfPassedSubjects) ;
	}



	public int getSem2NoOfFailedSubjects() {
		return sem2NoOfFailedSubjects;
	}



	public void setSem2NoOfFailedSubjects(String sem2NoOfFailedSubjects) {
		this.sem2NoOfFailedSubjects = Integer.valueOf(sem2NoOfFailedSubjects) ;
	}



	public String getSem2PassedSubjects() {
		return sem2PassedSubjects;
	}



	public void setSem2PassedSubjects(String sem2PassedSubjects) {
		this.sem2PassedSubjects = sem2PassedSubjects ;
	}



	public String getSem2FailedSubjects() {
		return sem2FailedSubjects;
	}



	public void setSem2FailedSubjects(String sem2FailedSubjects) {
		this.sem2FailedSubjects =sem2FailedSubjects  ;
	}



	public int getSem3NoOfPassedSubjects() {
		return sem3NoOfPassedSubjects;
	}



	public void setSem3NoOfPassedSubjects(String sem3NoOfPassedSubjects) {
		this.sem3NoOfPassedSubjects = Integer.valueOf(sem3NoOfPassedSubjects) ;
	}



	public int getSem3NoOfFailedSubjects() {
		return sem3NoOfFailedSubjects;
	}



	public void setSem3NoOfFailedSubjects(String sem3NoOfFailedSubjects) {
		this.sem3NoOfFailedSubjects = Integer.valueOf(sem3NoOfFailedSubjects) ;
	}



	public String getSem3PassedSubjects() {
		return sem3PassedSubjects;
	}



	public void setSem3PassedSubjects(String sem3PassedSubjects) {
		this.sem3PassedSubjects = sem3PassedSubjects ;
	}



	public String getSem3FailedSubjects() {
		return sem3FailedSubjects;
	}



	public void setSem3FailedSubjects(String sem3FailedSubjects) {
		this.sem3FailedSubjects = sem3FailedSubjects ;
	}



	public int getSem4NoOfPassedSubjects() {
		return sem4NoOfPassedSubjects;
	}



	public void setSem4NoOfPassedSubjects(String sem4NoOfPassedSubjects) {
		this.sem4NoOfPassedSubjects =Integer.valueOf(sem4NoOfPassedSubjects)  ;
	}



	public int getSem4NoOfFailedSubjects() {
		return sem4NoOfFailedSubjects;
	}



	public void setSem4NoOfFailedSubjects(String sem4NoOfFailedSubjects) {
		this.sem4NoOfFailedSubjects = Integer.valueOf(sem4NoOfFailedSubjects) ;
	}



	public String getSem4PassedSubjects() {
		return sem4PassedSubjects;
	}



	public void setSem4PassedSubjects(String sem4PassedSubjects) {
		this.sem4PassedSubjects = sem4PassedSubjects ;
	}



	public String getSem4FailedSubjects() {
		return sem4FailedSubjects;
	}



	public void setSem4FailedSubjects(String sem4FailedSubjects) {
		this.sem4FailedSubjects =sem4FailedSubjects  ;
	}



	public int getTotalNoOfPassedSubjects() {
		return totalNoOfPassedSubjects;
	}



	public void setTotalNoOfPassedSubjects(int totalNoOfPassedSubjects) {
		this.totalNoOfPassedSubjects = totalNoOfPassedSubjects;
		//Pass Fail entries mean number of subjects for previous cycles
		setNumberOfSubjectsExceptCurrentCycle(this.totalNoOfFailedSubjects + this.totalNoOfPassedSubjects);
	}



	public int getTotalNoOfFailedSubjects() {
		return totalNoOfFailedSubjects;
	}



	public void setTotalNoOfFailedSubjects(int totalNoOfFailedSubjects) {
		this.totalNoOfFailedSubjects = totalNoOfFailedSubjects;
		//Pass Fail entries mean number of subjects for previous cycles
		setNumberOfSubjectsExceptCurrentCycle(this.totalNoOfFailedSubjects + this.totalNoOfPassedSubjects);
	}



	public int getTotalNoOfLecturedAttended() {
		return totalNoOfLecturedAttended;
	}



	public void setTotalNoOfLecturedAttended(int totalNoOfLecturedAttended) {
		this.totalNoOfLecturedAttended = totalNoOfLecturedAttended;
	}



	public int getTotalNoOfANS() {
		return totalNoOfANS;
	}



	public void setTotalNoOfANS(int totalNoOfANS) {
		this.totalNoOfANS = totalNoOfANS;
	}



	public int getTotalNoOfAssignSubmitted() {
		return totalNoOfAssignSubmitted;
	}



	public void setTotalNoOfAssignSubmitted(int totalNoOfAssignSubmitted) {
		this.totalNoOfAssignSubmitted = totalNoOfAssignSubmitted;
	}



	public int getTotalNoOfBookedSubjects() {
		return totalNoOfBookedSubjects;
	}



	public void setTotalNoOfBookedSubjects(int totalNoOfBookedSubjects) {
		this.totalNoOfBookedSubjects = totalNoOfBookedSubjects;
	}



	public int getTotalNoOfBookingPendingSubjects() {
		return totalNoOfBookingPendingSubjects;
	}



	public void setTotalNoOfBookingPendingSubjects(
			int totalNoOfBookingPendingSubjects) {
		this.totalNoOfBookingPendingSubjects = totalNoOfBookingPendingSubjects;
	}



	public String getAllBookedSubjects() {
		return allBookedSubjects;
	}



	public void setAllBookedSubjects(String allBookedSubjects) {
		this.allBookedSubjects =allBookedSubjects;
	}



	public String getAllBookingPendingSubjects() {
		return allBookingPendingSubjects;
	}



	public void setAllBookingPendingSubjects(String allBookingPendingSubjects) {
		this.allBookingPendingSubjects = allBookingPendingSubjects;
	}



	public String getAllANSSubjects() {
		return allANSSubjects;
	}



	public void setAllANSSubjects(String allANSSubjects) {
		this.allANSSubjects =allANSSubjects;
	}



	public String getAllSubmittedSubjects() {
		return allSubmittedSubjects;
	}



	public void setAllSubmittedSubjects(String allSubmittedSubjects) {
		this.allSubmittedSubjects = allSubmittedSubjects;
	}



	public String getAllPassedSubjects() {
		return allPassedSubjects;
	}



	public void setAllPassedSubjects(String allPassedSubjects) {
		this.allPassedSubjects = allPassedSubjects;
	}



	public String getAllFailedSubjects() {
		return allFailedSubjects;
	}



	public void setAllFailedSubjects(String allFailedSubjects) {
		this.allFailedSubjects =allFailedSubjects;
	}



	public int getSem1NoOfBookedSubjects() {
		return sem1NoOfBookedSubjects;
	}



	public void setSem1NoOfBookedSubjects(String sem1NoOfBookedSubjects) {
		this.sem1NoOfBookedSubjects = Integer.valueOf(sem1NoOfBookedSubjects) ;
	}



	public int getSem1NoOfBookingPendingSubjects() {
		return sem1NoOfBookingPendingSubjects;
	}



	public void setSem1NoOfBookingPendingSubjects(
			String sem1NoOfBookingPendingSubjects) {
		this.sem1NoOfBookingPendingSubjects = Integer.valueOf(sem1NoOfBookingPendingSubjects) ;
	}



	public String getSem1BookedSubjects() {
		return sem1BookedSubjects;
	}



	public void setSem1BookedSubjects(String sem1BookedSubjects) {
		this.sem1BookedSubjects = sem1BookedSubjects ;
	}



	public String getSem1BookingPendingSubjects() {
		return sem1BookingPendingSubjects;
	}



	public void setSem1BookingPendingSubjects(String sem1BookingPendingSubjects) {
		this.sem1BookingPendingSubjects = sem1BookingPendingSubjects ;
	}



	public int getSem2NoOfBookedSubjects() {
		return sem2NoOfBookedSubjects;
	}



	public void setSem2NoOfBookedSubjects(String sem2NoOfBookedSubjects) {
		this.sem2NoOfBookedSubjects =Integer.valueOf(sem2NoOfBookedSubjects)  ;
	}



	public int getSem2NoOfBookingPendingSubjects() {
		return sem2NoOfBookingPendingSubjects;
	}



	public void setSem2NoOfBookingPendingSubjects(
			String sem2NoOfBookingPendingSubjects) {
		this.sem2NoOfBookingPendingSubjects = Integer.valueOf(sem2NoOfBookingPendingSubjects) ;
	}



	public String getSem2BookedSubjects() {
		return sem2BookedSubjects;
	}



	public void setSem2BookedSubjects(String sem2BookedSubjects) {
		this.sem2BookedSubjects =sem2BookedSubjects ;
	}



	public String getSem2BookingPendingSubjects() {
		return sem2BookingPendingSubjects;
	}



	public void setSem2BookingPendingSubjects(String sem2BookingPendingSubjects) {
		this.sem2BookingPendingSubjects = sem2BookingPendingSubjects  ;
	}



	public int getSem3NoOfBookedSubjects() {
		return sem3NoOfBookedSubjects;
	}



	public void setSem3NoOfBookedSubjects(String sem3NoOfBookedSubjects) {
		this.sem3NoOfBookedSubjects = Integer.valueOf(sem3NoOfBookedSubjects) ;
	}



	public int getSem3NoOfBookingPendingSubjects() {
		return sem3NoOfBookingPendingSubjects;
	}



	public void setSem3NoOfBookingPendingSubjects(
			String sem3NoOfBookingPendingSubjects) {
		this.sem3NoOfBookingPendingSubjects = Integer.valueOf(sem3NoOfBookingPendingSubjects) ;
	}



	public String getSem3BookedSubjects() {
		return sem3BookedSubjects;
	}



	public void setSem3BookedSubjects(String sem3BookedSubjects) {
		this.sem3BookedSubjects = sem3BookedSubjects ;
	}



	public String getSem3BookingPendingSubjects() {
		return sem3BookingPendingSubjects;
	}



	public void setSem3BookingPendingSubjects(String sem3BookingPendingSubjects) {
		this.sem3BookingPendingSubjects = sem3BookingPendingSubjects ;
	}



	public int getSem4NoOfBookedSubjects() {
		return sem4NoOfBookedSubjects;
	}



	public void setSem4NoOfBookedSubjects(String sem4NoOfBookedSubjects) {
		this.sem4NoOfBookedSubjects =Integer.valueOf(sem4NoOfBookedSubjects)  ;
	}



	public int getSem4NoOfBookingPendingSubjects() {
		return sem4NoOfBookingPendingSubjects;
	}



	public void setSem4NoOfBookingPendingSubjects(
			String sem4NoOfBookingPendingSubjects) {
		this.sem4NoOfBookingPendingSubjects =Integer.valueOf(sem4NoOfBookingPendingSubjects) ;
	}



	public String getSem4BookedSubjects() {
		return sem4BookedSubjects;
	}



	public void setSem4BookedSubjects(String sem4BookedSubjects) {
		this.sem4BookedSubjects =sem4BookedSubjects ;
	}



	public String getSem4BookingPendingSubjects() {
		return sem4BookingPendingSubjects;
	}



	public void setSem4BookingPendingSubjects(String sem4BookingPendingSubjects) {
		this.sem4BookingPendingSubjects =sem4BookingPendingSubjects ;
	}



	public int getSem1NoOfLecturesAttended() {
		return sem1NoOfLecturesAttended;
	}



	public void setSem1NoOfLecturesAttended(String sem1NoOfLecturesAttended) {
		this.sem1NoOfLecturesAttended =Integer.valueOf(sem1NoOfLecturesAttended) ;
	}



	public int getSem2NoOfLecturesAttended() {
		return sem2NoOfLecturesAttended;
	}



	public void setSem2NoOfLecturesAttended(String sem2NoOfLecturesAttended) {
		this.sem2NoOfLecturesAttended = Integer.valueOf(sem2NoOfLecturesAttended) ;
	}



	public int getSem3NoOfLecturesAttended() {
		return sem3NoOfLecturesAttended;
	}



	public void setSem3NoOfLecturesAttended(String sem3NoOfLecturesAttended) {
		this.sem3NoOfLecturesAttended =Integer.valueOf(sem3NoOfLecturesAttended) ;
	}



	public int getSem4NoOfLecturesAttended() {
		return sem4NoOfLecturesAttended;
	}



	public void setSem4NoOfLecturesAttended(String sem4NoOfLecturesAttended) {
		this.sem4NoOfLecturesAttended =Integer.valueOf(sem4NoOfLecturesAttended) ;
	}



	public double getReRegProbability() {
		
		reRegProbability = (((double)totalNoOfBookedSubjects / numberOfSubjectsApplicable )  * 30.0)
							+ (((double)totalNoOfAssignSubmitted / numberOfSubjectsApplicable) * 30.0) 
							+ (((double)totalNoOfPassedSubjects / numberOfSubjectsExceptCurrentCycle) * 30.0)
							+ (((double)totalNoOfLecturedAttended / (numberOfSubjectsApplicable * 4)) * 10.0)
							- (((double)totalNoOfFailedSubjects / numberOfSubjectsExceptCurrentCycle) * 10.0)
							- (((double)totalNoOfANS / numberOfSubjectsApplicable) * 10.0);
		return (double) Math.round(reRegProbability * 100)/100;
	}
	public void setReRegProbability(double reRegProbability) {
		this.reRegProbability = reRegProbability;
	}



	public int getNoOfSubjetsApplicable() {
		return noOfSubjetsApplicable;
	}



	public void setNoOfSubjetsApplicable(String noOfSubjetsApplicable) {
		this.noOfSubjetsApplicable = Integer.valueOf(noOfSubjetsApplicable);
	}



	@Override
	public String toString() {
		return "StudentLearningMetricsBean [sapid=" + sapid + ", sem1NoOfANS=" + sem1NoOfANS
				+ ", sem1NoOfAssignSubmitted=" + sem1NoOfAssignSubmitted + ",sem2NoOfANS=" + sem2NoOfANS
				+ ", sem2NoOfAssignSubmitted=" + sem2NoOfAssignSubmitted + ", sem3NoOfANS=" + sem3NoOfANS
				+ ", sem3NoOfAssignSubmitted=" + sem3NoOfAssignSubmitted + ",sem4NoOfANS=" + sem4NoOfANS
				+ ", sem4NoOfAssignSubmitted=" + sem4NoOfAssignSubmitted + ",totalNoOfANS=" + totalNoOfANS
				+ ", totalNoOfAssignSubmitted=" + totalNoOfAssignSubmitted + ", sem1NoOfPassedSubjects="
				+ sem1NoOfPassedSubjects + ", sem1NoOfFailedSubjects=" + sem1NoOfFailedSubjects
				+ ",sem2NoOfPassedSubjects=" + sem2NoOfPassedSubjects + ", sem2NoOfFailedSubjects="
				+ sem2NoOfFailedSubjects + ",sem3NoOfPassedSubjects=" + sem3NoOfPassedSubjects
				+ ", sem3NoOfFailedSubjects=" + sem3NoOfFailedSubjects + ",sem4NoOfPassedSubjects=" + sem4NoOfPassedSubjects
				+ ", sem4NoOfFailedSubjects=" + sem4NoOfFailedSubjects + ", totalNoOfPassedSubjects=" + totalNoOfPassedSubjects
				+ ", totalNoOfFailedSubjects=" + totalNoOfFailedSubjects + ",sem1NoOfBookedSubjects=" + sem1NoOfBookedSubjects
				+ ", sem1NoOfBookingPendingSubjects=" + sem1NoOfBookingPendingSubjects + ", sem2NoOfBookedSubjects=" + sem2NoOfBookedSubjects + ","
				+ " sem2NoOfBookingPendingSubjects="+sem2NoOfBookingPendingSubjects + ", sem3NoOfBookedSubjects="
				+ sem3NoOfBookedSubjects + ", sem3NoOfBookingPendingSubjects=" + sem3NoOfBookingPendingSubjects
				+ ",sem4NoOfBookedSubjects=" + sem4NoOfBookedSubjects
				+ ", sem4NoOfBookingPendingSubjects=" + sem4NoOfBookingPendingSubjects + ", totalNoOfBookedSubjects=" + totalNoOfBookedSubjects + ", totalNoOfBookingPendingSubjects="
				+ totalNoOfBookingPendingSubjects + ",sem1NoOfLecturesAttended="
				+ sem1NoOfLecturesAttended + ", sem2NoOfLecturesAttended=" + sem2NoOfLecturesAttended
				+ ", sem3NoOfLecturesAttended=" + sem3NoOfLecturesAttended + ", sem4NoOfLecturesAttended="
				+ sem4NoOfLecturesAttended + ", reRegProbability=" + reRegProbability + ", numberOfSubjectsApplicable ="
				+ numberOfSubjectsApplicable + " numberOfSubjectsExceptCurrentCycle = "+numberOfSubjectsExceptCurrentCycle+ "]";
	}
	
	
	
	
}
