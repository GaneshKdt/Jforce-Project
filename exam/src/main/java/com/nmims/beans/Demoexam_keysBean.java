package com.nmims.beans;

import java.io.Serializable;

public class Demoexam_keysBean  implements Serializable {
	private String subject;
	private String key;
	private String link;
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Demoexam_keysBean [subject=" + subject + ", key=" + key + "]";
	}
	
	
	
}