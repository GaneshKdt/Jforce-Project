package com.nmims.beans;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

 
public class StatusBean {
	int successCount=0; 
	int failureCount=0;
	String errorDescription;
	String apiResponseBody;
	String opportunityIdForUpdate;
	String sapidForUpdate;
	String message;
	List<String> detailedMessage= new ArrayList<String>(); 
	String sapid;
	String sem;
	String year;
	String month;
	String status;
    List<StatusBean> students= new ArrayList<StatusBean>(); 
    HashMap<String,String> logFileNameAndContentHashMap = new HashMap<String,String>();
	
	public HashMap<String, String> getLogFileNameAndContentHashMap() {
		return logFileNameAndContentHashMap;
	}
	public void setLogFileNameAndContentHashMap(HashMap<String, String> logFileNameAndContentHashMap) {
		this.logFileNameAndContentHashMap = logFileNameAndContentHashMap;
	}
	public int getFailureCount() {
		return failureCount;
	}
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public List<StatusBean> getStudents() {
		return students;
	}
	public void setStudents(List<StatusBean> students) {
		this.students = students;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	} 

	public  List<String> getDetailedMessage() {
		return detailedMessage;
	}

	public void setDetailedMessage( List<String> detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSapidForUpdate() {
		return sapidForUpdate;
	}

	public void setSapidForUpdate(String sapidForUpdate) {
		this.sapidForUpdate = sapidForUpdate;
	}

	public String getOpportunityIdForUpdate() {
		return opportunityIdForUpdate;
	}

	public void setOpportunityIdForUpdate(String opportunityIdForUpdate) {
		this.opportunityIdForUpdate = opportunityIdForUpdate;
	}

	public String getApiResponseBody() {
		return apiResponseBody;
	}

	public void setApiResponseBody(String apiResponseBody) {
		this.apiResponseBody = apiResponseBody;
	}
 
 
	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	@Override
	public String toString() {
		return "StatusBean [successCount=" + successCount + ", failureCount=" + failureCount + ", errorDescription="
				+ errorDescription + ", apiResponseBody=" + apiResponseBody + ", opportunityIdForUpdate="
				+ opportunityIdForUpdate + ", sapidForUpdate=" + sapidForUpdate + ", message=" + message
				+ ", detailedMessage=" + detailedMessage + ", sapid=" + sapid + ", sem=" + sem + ", year=" + year
				+ ", month=" + month + ", status=" + status + ", students=" + students + "]";
	}

	 
	
}
