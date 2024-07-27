package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ProgramExamBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.ModeOfDeliveryServiceImpl;

@RunWith(SpringRunner.class)
public class ModeOfDeliveryServiceImplTest {

	@InjectMocks
	private ModeOfDeliveryServiceImpl modService;
	@Mock
	private StudentMarksDAO studentMarksDAO;

	
	@Test
	public void getModProgramMap()
	{
		List<ProgramExamBean> beanList = new ArrayList<ProgramExamBean>();
		ProgramExamBean bean = new ProgramExamBean();
		bean.setCode("MBA - WX");
		bean.setName("Master of Business Administration (Working Executives)");
		bean.setModeOfLearning("ODL");
		beanList.add(bean);
		ProgramExamBean bean1 = new ProgramExamBean();
		bean1.setCode("PGDBM - ITM");
		bean1.setName("Post Graduate Diploma in Business Management (International Trade)");
		bean1.setModeOfLearning("ODL");
		beanList.add(bean1);
		
		boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(studentMarksDAO.getModProgramList()).thenReturn(beanList);
			Map<String, ProgramExamBean> modProgramMap = modService.getModProgramMap();
			actual = true;
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getModProgramMapException()
	{
		List<ProgramExamBean> beanList = new ArrayList<ProgramExamBean>();
		ProgramExamBean bean = new ProgramExamBean();
		bean.setCode(null);//to get Null Pointer Exception 
		bean.setName("Master of Business Administration (Working Executives)");
		bean.setModeOfLearning("ODL");
		beanList.add(bean);
		ProgramExamBean bean1 = new ProgramExamBean();
		bean1.setCode(null);//to get Null Pointer Exception
		bean1.setName("Post Graduate Diploma in Business Management (International Trade)");
		bean1.setModeOfLearning("ODL");
		beanList.add(bean1);
		
		boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(studentMarksDAO.getModProgramList()).thenReturn(beanList);
			Map<String, ProgramExamBean> modProgramMap = modService.getModProgramMap();
			actual = true;
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertNotSame(expected, actual);
	}
	
}
