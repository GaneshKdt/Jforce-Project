package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.daos.LiveSessionAccessDAO;
import com.nmims.services.LiveSessionAccessService;

@RunWith(SpringRunner.class)
public class LiveSessionAccessServiceTest {

	@Mock
	LiveSessionAccessDAO liveSessionAccessDAO;
	
	@InjectMocks
	LiveSessionAccessService liveSessionAccessServices;
	
	@Test
	public void getListOfLiveSessionAccessMasterKeys()
	{
		
		List<String> timeboundPortalList=new ArrayList<String>(
				Arrays.asList("111","131","151","154","155","156","157","158","142","143","144","145","146","147","148","149"));
		ArrayList<String> listOfLiveSessionPrograms=new ArrayList<String>(Arrays.asList("110","112","113","128","159","127","150","152","175","183","162","162","163"));
		int expected=listOfLiveSessionPrograms.size();
		when(liveSessionAccessDAO.getNonPgProgramsList(timeboundPortalList)).thenReturn(listOfLiveSessionPrograms);
		ArrayList<String> returnList=liveSessionAccessServices.getListOfFreeLiveSessionAccessMasterKeys(timeboundPortalList);
		int actual=returnList.size();
		assertEquals(expected, actual);
	}
	
	@Test
	public void fetchPSSforLiveSessionAccess() {
		String sapId="77122894267";
		String year="2022";
		String month="Jul";
		List<Integer> listOfLiveSessionPssId=new ArrayList<Integer>(Arrays.asList(2042,1995,1996,1998,2000,2002));
		int expected=listOfLiveSessionPssId.size();
		when(liveSessionAccessDAO.fetchPSSforLiveSessionAccess(sapId, year, month)).thenReturn(listOfLiveSessionPssId);
		List<Integer> returnList=liveSessionAccessServices.fetchPSSforLiveSessionAccess(sapId, year, month);
		int actual=returnList.size();
		assertEquals(expected, actual);
	}
}
