package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Q7Q8DissertationResultBean implements Serializable {
	
	
	
	private  ArrayList<DissertationResultDTO> q7ResultList=new ArrayList<DissertationResultDTO>();
	private  ArrayList<DissertationResultDTO> q8ResultList=new ArrayList<DissertationResultDTO>();
	private List<EmbaPassFailBean> passFailResultsList=new ArrayList<EmbaPassFailBean>();
	public ArrayList<DissertationResultDTO> getQ7ResultList() {
		return q7ResultList;
	}
	public void setQ7ResultList(ArrayList<DissertationResultDTO> q7ResultList) {
		this.q7ResultList = q7ResultList;
	}
	public ArrayList<DissertationResultDTO> getQ8ResultList() {
		return q8ResultList;
	}
	public void setQ8ResultList(ArrayList<DissertationResultDTO> q8ResultList) {
		this.q8ResultList = q8ResultList;
	}
	public List<EmbaPassFailBean> getPassFailResultsList() {
		return passFailResultsList;
	}
	public void setPassFailResultsList(List<EmbaPassFailBean> passFailResultsList) {
		this.passFailResultsList = passFailResultsList;
	}
	
	@Override
	public String toString() {
		return "Q7Q8DissertationResultBean [q7ResultList=" + q7ResultList + ", q8ResultList=" + q8ResultList
				+ ", passFailResultsList=" + passFailResultsList + "]";
	}
	
	
}
