package com.nmims.beans;

import java.io.Serializable;

public class WidgeResponseBean  implements Serializable {
	private String id;
	private int count;
	
	public WidgeResponseBean(String id,int count){
		this.id = id;
		this.count = count;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) { 
		this.count = count;
	}
	
	
	
}
