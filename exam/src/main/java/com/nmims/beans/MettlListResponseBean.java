package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class MettlListResponseBean  implements Serializable  {
	
	private ArrayList<MettlResponseBean> mettlResponseBeans;
	private String status;
	private String message;
	public ArrayList<MettlResponseBean> getMettlResponseBeans() {
		return mettlResponseBeans;
	}
	public void setMettlResponseBeans(ArrayList<MettlResponseBean> mettlResponseBeans) {
		this.mettlResponseBeans = mettlResponseBeans;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
