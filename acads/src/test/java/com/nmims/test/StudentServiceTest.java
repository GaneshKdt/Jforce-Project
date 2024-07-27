package com.nmims.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.daos.StudentDAO;
import com.nmims.services.StudentService;

@RunWith(SpringRunner.class)
public class StudentServiceTest {

	@Mock
	StudentDAO studentDAO;
	
	@InjectMocks
	StudentService studentService;
	
	@Test
	public void getListOfLiveSessionAccessMasterKeys()
	{
		
		List<String> timeboundPortalList=new ArrayList<String>(
				Arrays.asList("111","131","151","154","155","156","157","158","142","143","144","145","146","147","148","149"));
		ArrayList<String> listOfLiveSessionPrograms=new ArrayList<String>(Arrays.asList("110","112","113","128","159","127","150","152","175","183","162","162","163"));
		int expected=listOfLiveSessionPrograms.size();
		when(studentDAO.getNon_pgProgramList(timeboundPortalList)).thenReturn(listOfLiveSessionPrograms);
		ArrayList<String> returnList=studentService.getListOfLiveSessionAccessMasterKeys(timeboundPortalList);
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
		when(studentDAO.fetchPSSforLiveSessionAccess(sapId)).thenReturn(listOfLiveSessionPssId);
		List<Integer> returnList=studentService.fetchPSSforLiveSessionAccess(sapId);
		int actual=returnList.size();
		assertEquals(expected, actual);
	}

}
