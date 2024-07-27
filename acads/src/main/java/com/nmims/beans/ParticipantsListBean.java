package com.nmims.beans;

import java.util.List;

public class ParticipantsListBean {
	
	private int page_count;
	private  int page_size;
	private int total_records;
	private String next_page_token;
	List<ParticipantReportBean> participants;
	
	public int getPage_count() {
		return page_count;
	}
	public void setPage_count(int page_count) {
		this.page_count = page_count;
	}
	public int getPage_size() {
		return page_size;
	}
	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}
	public int getTotal_records() {
		return total_records;
	}
	public void setTotal_records(int total_records) {
		this.total_records = total_records;
	}
	public String getNext_page_token() {
		return next_page_token;
	}
	public void setNext_page_token(String next_page_token) {
		this.next_page_token = next_page_token;
	}
	public List<ParticipantReportBean> getParticipants() {
		return participants;
	}
	public void setParticipants(List<ParticipantReportBean> participants) {
		this.participants = participants;
	}
	
	@Override
	public String toString() {
		return "ParticipantsListBean [page_count=" + page_count + ", page_size=" + page_size + ", total_records="
				+ total_records + ", next_page_token=" + next_page_token + ", participants=" + participants + "]";
	}
}
