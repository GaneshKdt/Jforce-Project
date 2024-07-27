package com.nmims.dto;
import java.util.ArrayList;
public class SpecialNeedStudent {
	private ArrayList<String> ListOfSapid;
	public ArrayList<String> getListOfSapid() {
		return ListOfSapid;
	}
	public void setListOfSapid(ArrayList<String> listOfSapid) {
		ListOfSapid = listOfSapid;
	}
	@Override
	public String toString() {
		return "SpecialNeedStudent [ListOfSapid=" + ListOfSapid + "]";
	}
}