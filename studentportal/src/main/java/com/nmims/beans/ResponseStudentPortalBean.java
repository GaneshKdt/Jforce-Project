package com.nmims.beans;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * old name - ResponseBean
 * @author
 *
 */
public class ResponseStudentPortalBean extends BaseStudentPortalBean  implements Serializable {
	private String result;
	private String status;
	private boolean state;
	private String message;
	private List<String> detailedMessage= new ArrayList<String>();
	List<ResponseStudentPortalBean> students= new ArrayList<ResponseStudentPortalBean>();
	private String sem;
	private String year;
	private String month;
	private String sapid;
	String errorDescription;
	String apiResponseBody;
	String opportunityIdForUpdate;
	String sapidForUpdate; 
	int successCount=0; 
	int failureCount=0;
	HashMap<String,String> logFileNameAndContentHashMap = new HashMap<String,String>();
	
	public HashMap<String, String> getLogFileNameAndContentHashMap() {
		return logFileNameAndContentHashMap;
	}
	public void setLogFileNameAndContentHashMap(HashMap<String, String> logFileNameAndContentHashMap) {
		this.logFileNameAndContentHashMap = logFileNameAndContentHashMap;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public int getFailureCount() {
		return failureCount;
	}
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getApiResponseBody() {
		return apiResponseBody;
	}
	public void setApiResponseBody(String apiResponseBody) {
		this.apiResponseBody = apiResponseBody;
	}
	public String getOpportunityIdForUpdate() {
		return opportunityIdForUpdate;
	}
	public void setOpportunityIdForUpdate(String opportunityIdForUpdate) {
		this.opportunityIdForUpdate = opportunityIdForUpdate;
	}
	public String getSapidForUpdate() {
		return sapidForUpdate;
	}
	public void setSapidForUpdate(String sapidForUpdate) {
		this.sapidForUpdate = sapidForUpdate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public List<ResponseStudentPortalBean> getStudents() {
		return students;
	}
	public void setStudents(List<ResponseStudentPortalBean> students) {
		this.students = students;
	}
	public List<String> getDetailedMessage() {
		return detailedMessage;
	}
	public void setDetailedMessage(List<String> detailedMessage) {
		this.detailedMessage = detailedMessage;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	} 
	public boolean isState() {
		return state;
	}
	public String getMessage() {
		return message;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "ResponseStudentPortalBean [result=" + result + ", status=" + status + ", state=" + state + ", message=" + message
				+ ", detailedMessage=" + detailedMessage + ", students=" + students + ", sem=" + sem + ", year=" + year
				+ ", month=" + month + ", sapid=" + sapid + ", errorDescription=" + errorDescription
				+ ", apiResponseBody=" + apiResponseBody + ", opportunityIdForUpdate=" + opportunityIdForUpdate
				+ ", sapidForUpdate=" + sapidForUpdate + ", successCount=" + successCount + ", failureCount="
				+ failureCount + "]";
	}
	
	 
	
}
