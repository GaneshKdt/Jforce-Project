package com.nmims.service;

public class PassFailTestBean {
	private String sapid;
	private String subject;
	private String grno;
	private String writtenYear;
	private String writtenMonth;
	private String assignmentYear;
	private String assignmentMonth;
	private String resultProcessedYear;
	private String resultProcessedMonth;
	private String name;
	private String program;
	private String sem;
	private String writtenscore;
	private String assignmentscore;
	private String total;
	private String failReason;
	private String remarks;
	private String isPass;
	private String gracemarks;
	private String subjectCutoff;
	private String studentType;
	private String oldIsPassStatus;
	private String isResultLive;

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getGrno() {
		return grno;
	}

	public void setGrno(String grno) {
		this.grno = grno;
	}

	public String getWrittenYear() {
		return writtenYear;
	}

	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}

	public String getWrittenMonth() {
		return writtenMonth;
	}

	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
	}

	public String getAssignmentYear() {
		return assignmentYear;
	}

	public void setAssignmentYear(String assignmentYear) {
		this.assignmentYear = assignmentYear;
	}

	public String getAssignmentMonth() {
		return assignmentMonth;
	}

	public void setAssignmentMonth(String assignmentMonth) {
		this.assignmentMonth = assignmentMonth;
	}

	public String getResultProcessedYear() {
		return resultProcessedYear;
	}

	public void setResultProcessedYear(String resultProcessedYear) {
		this.resultProcessedYear = resultProcessedYear;
	}

	public String getResultProcessedMonth() {
		return resultProcessedMonth;
	}

	public void setResultProcessedMonth(String resultProcessedMonth) {
		this.resultProcessedMonth = resultProcessedMonth;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getWrittenscore() {
		return writtenscore;
	}

	public void setWrittenscore(String writtenscore) {
		this.writtenscore = writtenscore;
	}

	public String getAssignmentscore() {
		return assignmentscore;
	}

	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getIsPass() {
		return isPass;
	}

	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}

	public String getGracemarks() {
		return gracemarks;
	}

	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}

	public String getSubjectCutoff() {
		return subjectCutoff;
	}

	public void setSubjectCutoff(String subjectCutoff) {
		this.subjectCutoff = subjectCutoff;
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getOldIsPassStatus() {
		return oldIsPassStatus;
	}

	public void setOldIsPassStatus(String oldIsPassStatus) {
		this.oldIsPassStatus = oldIsPassStatus;
	}

	public String getIsResultLive() {
		return isResultLive;
	}

	public void setIsResultLive(String isResultLive) {
		this.isResultLive = isResultLive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignmentMonth == null) ? 0 : assignmentMonth.hashCode());
		result = prime * result + ((assignmentYear == null) ? 0 : assignmentYear.hashCode());
		result = prime * result + ((assignmentscore == null) ? 0 : assignmentscore.hashCode());
		result = prime * result + ((failReason == null) ? 0 : failReason.hashCode());
		result = prime * result + ((gracemarks == null) ? 0 : gracemarks.hashCode());
		result = prime * result + ((grno == null) ? 0 : grno.hashCode());
		result = prime * result + ((isPass == null) ? 0 : isPass.hashCode());
		result = prime * result + ((isResultLive == null) ? 0 : isResultLive.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((oldIsPassStatus == null) ? 0 : oldIsPassStatus.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((resultProcessedMonth == null) ? 0 : resultProcessedMonth.hashCode());
		result = prime * result + ((resultProcessedYear == null) ? 0 : resultProcessedYear.hashCode());
		result = prime * result + ((sapid == null) ? 0 : sapid.hashCode());
		result = prime * result + ((sem == null) ? 0 : sem.hashCode());
		result = prime * result + ((studentType == null) ? 0 : studentType.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((subjectCutoff == null) ? 0 : subjectCutoff.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
		result = prime * result + ((writtenMonth == null) ? 0 : writtenMonth.hashCode());
		result = prime * result + ((writtenYear == null) ? 0 : writtenYear.hashCode());
		result = prime * result + ((writtenscore == null) ? 0 : writtenscore.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PassFailTestBean))
			return false;
		PassFailTestBean other = (PassFailTestBean) obj;
		if (assignmentMonth == null) {
			if (other.assignmentMonth != null)
				return false;
		} else if (!assignmentMonth.equals(other.assignmentMonth))
			return false;
		if (assignmentYear == null) {
			if (other.assignmentYear != null)
				return false;
		} else if (!assignmentYear.equals(other.assignmentYear))
			return false;
		if (assignmentscore == null) {
			if (other.assignmentscore != null)
				return false;
		} else if (!assignmentscore.equals(other.assignmentscore))
			return false;
		if (failReason == null) {
			if (other.failReason != null)
				return false;
		} else if (!failReason.equals(other.failReason))
			return false;
		if (gracemarks == null) {
			if (other.gracemarks != null)
				return false;
		} else if (!gracemarks.equals(other.gracemarks))
			return false;
		if (grno == null) {
			if (other.grno != null)
				return false;
		} else if (!grno.equals(other.grno))
			return false;
		if (isPass == null) {
			if (other.isPass != null)
				return false;
		} else if (!isPass.equals(other.isPass))
			return false;
		if (isResultLive == null) {
			if (other.isResultLive != null)
				return false;
		} else if (!isResultLive.equals(other.isResultLive))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (oldIsPassStatus == null) {
			if (other.oldIsPassStatus != null)
				return false;
		} else if (!oldIsPassStatus.equals(other.oldIsPassStatus))
			return false;
		if (program == null) {
			if (other.program != null)
				return false;
		} else if (!program.equals(other.program))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (resultProcessedMonth == null) {
			if (other.resultProcessedMonth != null)
				return false;
		} else if (!resultProcessedMonth.equals(other.resultProcessedMonth))
			return false;
		if (resultProcessedYear == null) {
			if (other.resultProcessedYear != null)
				return false;
		} else if (!resultProcessedYear.equals(other.resultProcessedYear))
			return false;
		if (sapid == null) {
			if (other.sapid != null)
				return false;
		} else if (!sapid.equals(other.sapid))
			return false;
		if (sem == null) {
			if (other.sem != null)
				return false;
		} else if (!sem.equals(other.sem))
			return false;
		if (studentType == null) {
			if (other.studentType != null)
				return false;
		} else if (!studentType.equals(other.studentType))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (subjectCutoff == null) {
			if (other.subjectCutoff != null)
				return false;
		} else if (!subjectCutoff.equals(other.subjectCutoff))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		if (writtenMonth == null) {
			if (other.writtenMonth != null)
				return false;
		} else if (!writtenMonth.equals(other.writtenMonth))
			return false;
		if (writtenYear == null) {
			if (other.writtenYear != null)
				return false;
		} else if (!writtenYear.equals(other.writtenYear))
			return false;
		if (writtenscore == null) {
			if (other.writtenscore != null)
				return false;
		} else if (!writtenscore.equals(other.writtenscore))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PassFailTestBean [sapid=" + sapid + ", subject=" + subject + ", grno=" + grno + ", writtenYear="
				+ writtenYear + ", writtenMonth=" + writtenMonth + ", assignmentYear=" + assignmentYear
				+ ", assignmentMonth=" + assignmentMonth + ", resultProcessedYear=" + resultProcessedYear
				+ ", resultProcessedMonth=" + resultProcessedMonth + ", name=" + name + ", program=" + program
				+ ", sem=" + sem + ", writtenscore=" + writtenscore + ", assignmentscore=" + assignmentscore
				+ ", total=" + total + ", failReason=" + failReason + ", remarks=" + remarks + ", isPass=" + isPass
				+ ", gracemarks=" + gracemarks + ", subjectCutoff=" + subjectCutoff + ", studentType=" + studentType
				+ ", oldIsPassStatus=" + oldIsPassStatus + ", isResultLive=" + isResultLive + "]";
	}

}
