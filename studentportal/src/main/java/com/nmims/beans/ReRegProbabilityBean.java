package com.nmims.beans;

import java.io.Serializable;

public class ReRegProbabilityBean extends BaseStudentPortalBean   implements Serializable  {
	int numberOfSubjectsApplicable;
	int numberOfSubjectsExceptCurrentCycle;
	int numberOfAssignmentsSubmitted;
	int numberOfWrittenAttempts;
	int numberOfExamBookings;
	int numberOfSubjectsPassed;
	int numberOfSubjectsFailed;
	int numberOfLecturesAttended;
	int numberOfANS;
	double reRegProbability;
	
	
	
	
	public int getNumberOfSubjectsExceptCurrentCycle() {
		return numberOfSubjectsExceptCurrentCycle;
	}
	public void setNumberOfSubjectsExceptCurrentCycle(
			int numberOfSubjectsExceptCurrentCycle) {
		if(numberOfSubjectsExceptCurrentCycle == 0){
			numberOfSubjectsExceptCurrentCycle = 1; //So that division does not fail
		}else{
			this.numberOfSubjectsExceptCurrentCycle = numberOfSubjectsExceptCurrentCycle;
		}
		
	}
	public int getNumberOfANS() {
		return numberOfANS;
	}
	public void setNumberOfANS(int numberOfANS) {
		this.numberOfANS = numberOfANS;
	}
	public int getNumberOfSubjectsFailed() {
		return numberOfSubjectsFailed;
	}
	public void setNumberOfSubjectsFailed(int numberOfSubjectsFailed) {
		this.numberOfSubjectsFailed = numberOfSubjectsFailed;
	}
	public int getNumberOfSubjectsApplicable() {
		return numberOfSubjectsApplicable;
	}
	public void setNumberOfSubjectsApplicable(int numberOfSubjectsApplicable) {
		this.numberOfSubjectsApplicable = numberOfSubjectsApplicable;
	}
	public int getNumberOfAssignmentsSubmitted() {
		return numberOfAssignmentsSubmitted;
	}
	public void setNumberOfAssignmentsSubmitted(int numberOfAssignmentsSubmitted) {
		this.numberOfAssignmentsSubmitted = numberOfAssignmentsSubmitted;
	}
	public int getNumberOfWrittenAttempts() {
		return numberOfWrittenAttempts;
	}
	public void setNumberOfWrittenAttempts(int numberOfWrittenAttempts) {
		this.numberOfWrittenAttempts = numberOfWrittenAttempts;
	}
	public int getNumberOfExamBookings() {
		return numberOfExamBookings;
	}
	public void setNumberOfExamBookings(int numberOfExamBookings) {
		this.numberOfExamBookings = numberOfExamBookings;
	}
	public int getNumberOfSubjectsPassed() {
		return numberOfSubjectsPassed;
	}
	public void setNumberOfSubjectsPassed(int numberOfSubjectsPassed) {
		this.numberOfSubjectsPassed = numberOfSubjectsPassed;
	}
	public int getNumberOfLecturesAttended() {
		return numberOfLecturesAttended;
	}
	public void setNumberOfLecturesAttended(int numberOfLecturesAttended) {
		this.numberOfLecturesAttended = numberOfLecturesAttended;
	}
	public double getReRegProbability() {
		
		reRegProbability = (((double)numberOfExamBookings / numberOfSubjectsApplicable )  * 30.0)
							+ (((double)numberOfAssignmentsSubmitted / numberOfSubjectsApplicable) * 30.0) 
							+ (((double)numberOfSubjectsPassed / numberOfSubjectsExceptCurrentCycle) * 30.0)
							+ (((double)numberOfLecturesAttended / (numberOfSubjectsApplicable * 4)) * 10.0)
							- (((double)numberOfSubjectsFailed / numberOfSubjectsExceptCurrentCycle) * 10.0)
							- (((double)numberOfANS / numberOfSubjectsApplicable) * 10.0);
		
		return (double) Math.round(reRegProbability * 100)/100;
	}
	public void setReRegProbability(double reRegProbability) {
		this.reRegProbability = reRegProbability;
	}
	@Override
	public String toString() {
		return "ReRegProbabilityBean [numberOfSubjectsApplicable="
				+ numberOfSubjectsApplicable
				+ ", numberOfSubjectsExceptCurrentCycle="
				+ numberOfSubjectsExceptCurrentCycle
				+ ", numberOfAssignmentsSubmitted="
				+ numberOfAssignmentsSubmitted + ", numberOfWrittenAttempts="
				+ numberOfWrittenAttempts + ", numberOfExamBookings="
				+ numberOfExamBookings + ", numberOfSubjectsPassed="
				+ numberOfSubjectsPassed + ", numberOfSubjectsFailed="
				+ numberOfSubjectsFailed + ", numberOfLecturesAttended="
				+ numberOfLecturesAttended + ", numberOfANS=" + numberOfANS
				+ ", reRegProbability=" + reRegProbability + "]";
	}
	
	
	public static void main(String[] args) {
		System.out.println((double)1/12);
	}

}
