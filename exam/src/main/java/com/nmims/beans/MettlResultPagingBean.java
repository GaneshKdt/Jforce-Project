package com.nmims.beans;

public class MettlResultPagingBean {
	private String previous;
	private String next;
	
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	
	@Override
	public String toString() {
		return "MettlResultPagingBean [previous=" + previous + ", next=" + next + "]";
	}
	
	
}
