package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import com.nmims.beans.FacultyReallocationBean;
import com.nmims.daos.FacultyReallocationImplDao;
import com.nmims.services.FacultyReallocationImplService;

@RunWith(SpringRunner.class)
public class FacultyReallocationServiceTest 
{
	@Mock
	FacultyReallocationImplDao projectReallocationDAO;
	
	@InjectMocks
	FacultyReallocationImplService projectReallocationService;
	
	@Test
	public void getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId()
	{
		String year = "2023";
		String month = "Apr";
		String facultyId = "Apr";
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			ArrayList<FacultyReallocationBean> response = projectReallocationService.getProjectsAllocatedToFacultyByYearAndMonthOrFacultyId(year, month, facultyId);
			if(response != null)
			{
				actual = "Success";
			}
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getProjectsNotEvaluatedByYearAndMonthOrFacultyId()
	{
		String year = "2023";
		String month = "Apr";
		String facultyId = "NGASCE0479";
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			HashMap<String, FacultyReallocationBean> response = projectReallocationService.getProjectsNotEvaluatedByYearAndMonthOrFacultyId(year, month, facultyId);
			if(response != null)
			{
				actual = "Success";
			}
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void reallocateProjectsToFaculty()
	{
		FacultyReallocationBean bean = new FacultyReallocationBean();
		bean.setFacultyId("TEENGASCE0004");
		bean.setToFacultyId("NGASCE0479");
		bean.setYear("2023");
		bean.setMonth("Apr");
		bean.setUser("demo");
		bean.setSapids(Arrays.asList("77118182869","77118173898","77118216925"));
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			projectReallocationService.reallocateProjectsToFaculty(bean);
			actual = "Success";
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getSearchedFacultyList()
	{
		FacultyReallocationBean bean = new FacultyReallocationBean();
		
		ArrayList<FacultyReallocationBean> allocatedCount = new ArrayList<>();
		bean.setProjectsAllocated("25");
		bean.setFacultyId("NGASCE0479");
		allocatedCount.add(bean);
				
		HashMap<String, FacultyReallocationBean> realocatedCount = new HashMap<>();
		bean.setYetEvaluated("15");
		realocatedCount.put(bean.getFacultyId(), bean);
		
		Map<String, FacultyReallocationBean> facultyMap = new HashMap<>();
		bean.setFirstName("Faculty");
		bean.setLastName("Demo");
		facultyMap.put(bean.getFacultyId(), bean);
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			ArrayList<FacultyReallocationBean> response = projectReallocationService.getSearchedFacultyList(realocatedCount, allocatedCount, facultyMap);
			if(response != null)
			{
				actual = "Success";
			}
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getStudentsByFacultyIdAndYearAndMonth()
	{
		String year = "2023";
		String month = "Apr";
		String facultyId = "NGASCE0479";
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			List<FacultyReallocationBean> response = projectReallocationService.getStudentsByFacultyIdAndYearAndMonth(facultyId, year, month);
			if(response != null)
			{
				actual = "Success";
			}
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void setProgramDetailsToStudent()
	{
		FacultyReallocationBean bean = new FacultyReallocationBean();
		
		List<FacultyReallocationBean> studentsList = new ArrayList<>();
		bean.setSapid("77118216925");
		bean.setConsumerProgramStructureId("65");
		bean.setProgramStructure("Jul2014");
		
		Map<String, FacultyReallocationBean> allProgramDetailsMap = new HashMap<>();
		bean.setProgramCode("PGDITSM");
		bean.setProgramName("Post Graduate Diploma in Information Technology and Systems Management");
		allProgramDetailsMap.put(bean.getConsumerProgramStructureId()+bean.getProgramStructure(), bean);
		
		final String expected = "Success";
		String actual = "";
		
		try
		{
			List<FacultyReallocationBean> response = projectReallocationService.setProgramDetailsToStudent(studentsList, allProgramDetailsMap);
			if(response != null)
			{
				actual = "Success";
			}
		}
		catch(Exception e)
		{
			
		}
		
		assertEquals(expected, actual);
	}
}
