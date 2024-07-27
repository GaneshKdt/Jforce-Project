package com.nmims.enums;

public enum StudentTermCleared {
	YES("Yes"),
	NO("No");	
	
	private StudentTermCleared(String studentTermCleared) {
		this.studentTermCleared = studentTermCleared;
	}

	private final String studentTermCleared;

	public String getStudentTermCleared() {
		return studentTermCleared;
	}
}
