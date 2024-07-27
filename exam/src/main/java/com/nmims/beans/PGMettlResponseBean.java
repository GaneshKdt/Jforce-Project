package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PGMettlResponseBean  implements Serializable  {
	private String status;
	private List<MettlSSOInfoBean> data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<MettlSSOInfoBean> getData() {
		return data;
	}
	public void setData(List<MettlSSOInfoBean> data) {
		this.data = data;
	}
	
	
}
