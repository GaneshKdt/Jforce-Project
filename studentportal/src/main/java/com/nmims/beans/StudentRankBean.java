package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class StudentRankBean implements Serializable{

    private String id;
    private String sapid;
    private String name;
    private String subject;
    private String total;
    private String rank;
    private String studentImage;
    private String outOfMarks;
    private String errorMessage;
    private String sem;
    private String program;
    private String consumerProgramStructureId;
    private String acadMonth;
    private String month;
    private String year;
    private String subjectcodeMappingId;
    private String subjectCodeId;
    private boolean errorRecord;
    private String subjectsCount;
    private String subjectTotal;
    private List<StudentRankBean> overAllCycleWiseRank;
    private StudentRankBean cycleWiseStudentsRank;
    private List<StudentRankBean> overAllSubjectWiseRank;
    private StudentRankBean subjectWiseStudentsRank;

    public String getSapid() {
        return sapid;
    }

    public void setSapid(String sapid) {
        this.sapid = sapid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getStudentImage() {
        return studentImage;
    }

    public void setStudentImage(String studentImage) {
        this.studentImage = studentImage;
    }

    public String getOutOfMarks() {
        return outOfMarks;
    }

    public void setOutOfMarks(String outOfMarks) {
        this.outOfMarks = outOfMarks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isErrorRecord() {
        return errorRecord;
    }

    public void setErrorRecord(boolean errorRecord) {
        this.errorRecord = errorRecord;
    }

    public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSubjectcodeMappingId() {
		return subjectcodeMappingId;
	}

	public void setSubjectcodeMappingId(String subjectcodeMappingId) {
		this.subjectcodeMappingId = subjectcodeMappingId;
	}

	public String getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public String getSubjectsCount() {
		return subjectsCount;
	}

	public void setSubjectsCount(String subjectsCount) {
		this.subjectsCount = subjectsCount;
	}

	public String getSubjectTotal() {
		return subjectTotal;
	}

	public void setSubjectTotal(String subjectTotal) {
		this.subjectTotal = subjectTotal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<StudentRankBean> getOverAllCycleWiseRank() {
		return overAllCycleWiseRank;
	}

	public void setOverAllCycleWiseRank(List<StudentRankBean> overAllCycleWiseRank) {
		this.overAllCycleWiseRank = overAllCycleWiseRank;
	}

	public StudentRankBean getCycleWiseStudentsRank() {
		return cycleWiseStudentsRank;
	}

	public void setCycleWiseStudentsRank(StudentRankBean cycleWiseStudentsRank) {
		this.cycleWiseStudentsRank = cycleWiseStudentsRank;
	}

	public List<StudentRankBean> getOverAllSubjectWiseRank() {
		return overAllSubjectWiseRank;
	}

	public void setOverAllSubjectWiseRank(List<StudentRankBean> overAllSubjectWiseRank) {
		this.overAllSubjectWiseRank = overAllSubjectWiseRank;
	}

	public StudentRankBean getSubjectWiseStudentsRank() {
		return subjectWiseStudentsRank;
	}

	public void setSubjectWiseStudentsRank(StudentRankBean subjectWiseStudentsRank) {
		this.subjectWiseStudentsRank = subjectWiseStudentsRank;
	}

	@Override
	public String toString() {
		return "StudentRankBean [sapid=" + sapid + ", name=" + name + ", subject=" + subject + ", total=" + total
				+ ", rank=" + rank + ", sem=" + sem + ", program=" + program + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", month=" + month + ", year=" + year + ", overAllCycleWiseRank="
				+ overAllCycleWiseRank + ", cycleWiseStudentsRank=" + cycleWiseStudentsRank
				+ ", overAllSubjectWiseRank=" + overAllSubjectWiseRank + ", subjectWiseStudentsRank="
				+ subjectWiseStudentsRank + "]";
	}
}
