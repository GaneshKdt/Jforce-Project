package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nmims.beans.TimeSpentStudentBean;
import com.nmims.beans.TracksBean;
import com.nmims.daos.MyActivityDao;
import com.nmims.services.MyActivityService;

public class MyActivityServiceTest {
	
	@InjectMocks
	MyActivityService myActivityService;
	
	@Mock
	MyActivityDao myActivityDao;
	
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
	
	//--- Time Spent Details ---//
	public TimeSpentStudentBean getTimeSpentStudentDetails() {
		
		TimeSpentStudentBean timeSpentStudentBean = new TimeSpentStudentBean();
		timeSpentStudentBean.setSapid("77777777771");
		timeSpentStudentBean.setDay_name("Monday");
		timeSpentStudentBean.setWeek_name("Week 1");
		timeSpentStudentBean.setHours(10);
		timeSpentStudentBean.setMinutes(30);
		timeSpentStudentBean.setSeconds(50);
		return timeSpentStudentBean;
	}
	
	//--- Track Details ---//
	public TracksBean trackDetails() {
		
		TracksBean tracksBean = new TracksBean();
		tracksBean.setId(1);
		tracksBean.setTrack("Weekend Batch - Slow Track");
		tracksBean.setBorder("#000000");
		tracksBean.setFontColor("#FFFFFF");
		tracksBean.setActive("Y");
		tracksBean.setHexCode("#FFC999");
		tracksBean.setColorClass("WeekendBatchSlowTrack");
		return tracksBean;
	}

	//--- Get My Activity Details ---//
	@Test
	public void myActivity() {
		
		TimeSpentStudentBean timeSpentStudentBean = getTimeSpentStudentDetails();
		
		Map<String, List<TimeSpentStudentBean>> expected = new HashMap<String, List<TimeSpentStudentBean>>();
		
		List<TimeSpentStudentBean> myActivityDetails = new ArrayList<TimeSpentStudentBean>();
		myActivityDetails.add(timeSpentStudentBean);
		
		when(myActivityDao.getTimeSpentOfCurrentWeekBySapid(timeSpentStudentBean.getSapid())).thenReturn(myActivityDetails);
		expected.put("This_Week", myActivityDetails);
		
		when(myActivityDao.getTimeSpentOfCurrentMonthBySapid(timeSpentStudentBean.getSapid())).thenReturn(myActivityDetails);
		expected.put("Current_Month", myActivityDetails);
		
		when(myActivityDao.getTimeSpentOfLastMonthBySapid(timeSpentStudentBean.getSapid())).thenReturn(myActivityDetails);
		expected.put("Last_Month", myActivityDetails);
		
		Map<String, List<TimeSpentStudentBean>> actual = myActivityService.myActivity(timeSpentStudentBean.getSapid());
		
		assertEquals(expected, actual);
		
	}
	
	//--- Get Track Details ---//
	@Test
	public void getTrackDetails() {
		
		TracksBean tracksBean = trackDetails();
		
		List<TracksBean> expected = new ArrayList<TracksBean>();
		expected.add(tracksBean);
		
		when(myActivityDao.getTrackDetails()).thenReturn(expected);
		
		List<TracksBean> actual = myActivityService.getTrackDetails();
		
		assertEquals(expected, actual);
	}

}