package com.nmims.beans;

public class RemovalOfFacultyFromAllStageOfRevaluationBean {
	
	
	private Integer rowsAffectedFromAssignmentSubmission ;
	private Integer rowsAffectedFromQAssignmentSubmission ;
	
	public Integer getRowsAffectedFromAssignmentSubmission() {
		return rowsAffectedFromAssignmentSubmission;
	}
	public void setRowsAffectedFromAssignmentSubmission(Integer rowsAffectedFromAssignmentSubmission) {
		this.rowsAffectedFromAssignmentSubmission = rowsAffectedFromAssignmentSubmission;
	}
	public Integer getRowsAffectedFromQAssignmentSubmission() {
		return rowsAffectedFromQAssignmentSubmission;
	}
	public void setRowsAffectedFromQAssignmentSubmission(Integer rowsAffectedFromQAssignmentSubmission) {
		this.rowsAffectedFromQAssignmentSubmission = rowsAffectedFromQAssignmentSubmission;
	}
	@Override
	public String toString() {
		return "RemovalOfFacultyFromAllStageOfRevaluationBean [rowsAffectedFromAssignmentSubmission="
				+ rowsAffectedFromAssignmentSubmission + ", rowsAffectedFromQAssignmentSubmission="
				+ rowsAffectedFromQAssignmentSubmission + "]";
	}

	
	
}
