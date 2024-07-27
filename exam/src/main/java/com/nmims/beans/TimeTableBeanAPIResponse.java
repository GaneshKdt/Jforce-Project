package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import com.nmims.beans.TimetableBean;

public class TimeTableBeanAPIResponse implements Serializable {
	
	 List<TimetableBean> timeTableList;
	 String mostRecentTimetablePeriod;
	 TreeMap<String,  ArrayList<TimetableBean>> programTimetableMap;
	 public List<TimetableBean> gettimeTableList() {
		 return timeTableList;
	 }
	 
	 public void settimeTableList(List<TimetableBean> timeTableList) {
		 this.timeTableList = timeTableList; 
	 }
	 
	 public String getmostRecentTimetablePeriod() {
		 return mostRecentTimetablePeriod;
	 }
	 
	 public void setmostRecentTimetablePeriod(String mostRecentTimetablePeriod) {
		 this.mostRecentTimetablePeriod = mostRecentTimetablePeriod;
	 }
	 
	 public void settreeMap(TreeMap<String,  ArrayList<TimetableBean>> programTimetableMap) {
		 this.programTimetableMap = programTimetableMap;
	 }
	 
	 public TreeMap<String,  ArrayList<TimetableBean>> gettreeMap() {
		 return programTimetableMap;
	 }
	 
	
}
