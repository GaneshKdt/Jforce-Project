package com.nmims.beans;

public class FileMigrationBean 
{
	private int id ;
	
	private String filePath;

	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "FileMigrationBean [id=" + id + ", filePath=" + filePath + "]";
	}

}
