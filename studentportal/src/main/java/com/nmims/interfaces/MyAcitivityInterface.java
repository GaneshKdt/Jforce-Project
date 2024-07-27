package com.nmims.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nmims.beans.TimeSpentStudentBean;
import com.nmims.beans.TotalSessionDetailsStudentBean;
import com.nmims.beans.TotalVideoDetailsStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.beans.VideoAndSessionAttendanceCountStudentBean;

public interface MyAcitivityInterface {

	public Map<String, List<TimeSpentStudentBean>> myActivity(String sapid);
	
	public List<VideoAndSessionAttendanceCountStudentBean> getSessionAttendanceCount(String sapid);
	
	public List<TracksBean> getTrackDetails();
	
	public TotalSessionDetailsStudentBean getTotalSessionDetails(ArrayList<Integer> pss_id, String sapid);
	
	public TotalVideoDetailsStudentBean getTotalVideoDetails(String sapid);
	
	public List<Map<String, Object>> getPdfReadDetailsBySapid(ArrayList<Integer> pss_id, String sapid);

}