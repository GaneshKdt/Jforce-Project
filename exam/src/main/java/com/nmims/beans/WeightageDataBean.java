package com.nmims.beans;



import java.io.Serializable; 

public class WeightageDataBean extends BaseExamBean implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private Long courseId;

	private String weightagetype;
	
	private String weightageassigned;

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public String getweightagetype() {
		return weightagetype;
	}

	public void setweightagetype(String weightagetype) {
		this.weightagetype = weightagetype;
	}

	public String getweightageassigned() {
		return weightageassigned;
	}

	public void setweightageassigned(String weightageassigned) {
		this.weightageassigned = weightageassigned;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "weightageData [courseId=" + courseId + ", weightagetype="
				+ weightagetype + ", weightageassigned=" + weightageassigned
				+ "]";
	}
	
	
}
