package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class MBAXMarksPreviewBean  implements Serializable  {
	private ArrayList<MBAXMarksBean> mbaxMarksBean;
	private int totalRows;
	private int totalColumns;
	private String status;
	private String message;
	
	public ArrayList<MBAXMarksBean> getMbaxMarksBean() {
		return mbaxMarksBean;
	}
	public void setMbaxMarksBean(ArrayList<MBAXMarksBean> mbaxMarksBean) {
		this.mbaxMarksBean = mbaxMarksBean;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getTotalColumns() {
		return totalColumns;
	}
	public void setTotalColumns(int totalColumns) {
		this.totalColumns = totalColumns;
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
