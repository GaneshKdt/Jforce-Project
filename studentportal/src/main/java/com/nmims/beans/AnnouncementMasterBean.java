package com.nmims.beans;

import java.io.Serializable;

public class AnnouncementMasterBean  implements Serializable{

	private int id;
	private String program;
	private String program_structure;
	private String consumer_type;
	private String master_key;
	
	public String getMaster_key() {
		return master_key;
	}
	public void setMaster_key(String master_key) {
		this.master_key = master_key;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgram_structure() {
		return program_structure;
	}
	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}
	public String getConsumer_type() {
		return consumer_type;
	}
	public void setConsumer_type(String consumer_type) {
		this.consumer_type = consumer_type;
	}
	@Override
	public String toString() {
		return "AnnouncementMasterBean [id=" + id + ", program=" + program + ", program_structure=" + program_structure
				+ ", consumer_type=" + consumer_type + "]";
	}
	
}
