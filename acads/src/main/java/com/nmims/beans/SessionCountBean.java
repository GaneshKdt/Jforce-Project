package com.nmims.beans;

public class SessionCountBean {
	private String id;
	private int sem;
	private String consumerProgramStructureId;
	private String subject;
	private String sapid;
	private String sessionId;
	private int count;

	public int getSem() {
		return sem;
	}

	public void setSem(int sem) {
		this.sem = sem;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "SessionCountBean [id=" + id + ", sem=" + sem + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", subject=" + subject + "]";
	}
	
	

}
