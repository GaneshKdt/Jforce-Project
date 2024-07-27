package com.nmims.beans;

public class StudentDataMismatchBean implements Comparable<StudentDataMismatchBean>{
	private String sapid;
	private String firstName;
	private String lastName;
	private String mismatchType;
	private String salesforceValue;
	private String studentZoneValue;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getMismatchType() {
		return mismatchType;
	}
	public void setMismatchType(String mismatchType) {
		this.mismatchType = mismatchType;
	}
	public String getSalesforceValue() {
		return salesforceValue;
	}
	public void setSalesforceValue(String salesforceValue) {
		this.salesforceValue = salesforceValue;
	}
	public String getStudentZoneValue() {
		return studentZoneValue;
	}
	public void setStudentZoneValue(String studentZoneValue) {
		this.studentZoneValue = studentZoneValue;
	}
	
	@Override
	public int compareTo(StudentDataMismatchBean bean) {
		return mismatchType.compareTo(bean.mismatchType);
	}
	
	
	@Override
	public String toString() {
		return "StudentDataMismatchBean [sapid=" + sapid + ", firstName="
				+ firstName + ", lastName=" + lastName + ", mismatchType="
				+ mismatchType + ", salesforceValue=" + salesforceValue
				+ ", studentZoneValue=" + studentZoneValue + "]";
	}
	
}
