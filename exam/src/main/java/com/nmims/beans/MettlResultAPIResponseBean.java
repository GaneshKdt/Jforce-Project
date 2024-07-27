package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultAPIResponseBean  implements Serializable  {

	private String status;
	private MettlResultCandidateBean candidate;
	private List<MettlResultCandidateBean> candidates;
	private MettlResultPagingBean paging;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public MettlResultCandidateBean getCandidate() {
		return candidate;
	}
	public void setCandidate(MettlResultCandidateBean candidate) {
		this.candidate = candidate;
	}
	
	public List<MettlResultCandidateBean> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<MettlResultCandidateBean> candidates) {
		this.candidates = candidates;
	}
	public MettlResultPagingBean getPaging() {
		return paging;
	}
	public void setPaging(MettlResultPagingBean paging) {
		this.paging = paging;
	}
	
	@Override
	public String toString() {
		return "MettlResultAPIResponseBean [status=" + status + ", candidate=" + candidate + ", candidates="
				+ candidates + ", paging=" + paging + "]";
	}
}
