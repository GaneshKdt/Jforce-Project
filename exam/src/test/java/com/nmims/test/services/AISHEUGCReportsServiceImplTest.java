package com.nmims.test.services;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nmims.beans.AISHEUGCExcelReportBean;
import com.nmims.beans.AISHEUGCReportsBean;
import com.nmims.daos.AISHEUGCReportsDao;
import com.nmims.services.impl.AISHEUGCReportsServiceImpl;


//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AISHEUGCReportsServiceImplTest {
	
	
	
	
	//@InjectMocks
	@Autowired
	AISHEUGCReportsServiceImpl service;
	
	@Mock
	AISHEUGCReportsDao dao;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	
	


	
	@Test
	public void test1() throws Exception{
		
		String	enrollmentYear ="2021";
		String	enrollmentMonth ="Dec";
		String	sem ="1";
		//String	firstLetterOfProgram ="P";
		ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = service.getAllListOfProgram(enrollmentYear,enrollmentMonth,sem);
boolean flag= false;

		  if(AllListOfProgram != null && AllListOfProgram.size() > 0){
			  
			  flag = true;
		  }
		  for(AISHEUGCExcelReportBean bean :AllListOfProgram) {
			  System.out.println("program count " +bean);
		  }
		
assertEquals(true,flag);
	}
	
	
	
	
	
	@Test
	public void test2() throws Exception{
		
		String	enrollmentYear ="2021";
		String	enrollmentMonth ="Dec";
		String	sem ="1";
		//String	firstLetterOfProgram ="P";
		ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = service.getAllListOfProgram(enrollmentYear,enrollmentMonth,sem);
boolean flag= false;

		  if(AllListOfProgram != null && AllListOfProgram.size() > 0){
			  for(AISHEUGCExcelReportBean bean :AllListOfProgram) {
				  if(bean.getProgram().equals("DMM") && bean.getTotalNoOfStudentsAbove60percentage().equals("75") && bean.getProgram().equals("DOM") && bean.getTotalNoOfStudentsAbove60percentage().equals("34") ){
					  
				  }
			  }
			  
			  flag = true;
		  }
		  
		
assertEquals(true,flag);
	}
	
	
	
	
	
	
	@Test
	public void test3() throws Exception{
	
	String	enrollmentYear ="2021";
	String	enrollmentMonth ="Dec";
	String	sem ="1";
	String	firstLetterOfProgram ="D";
	ArrayList<AISHEUGCReportsBean> ListOfAppreadStudentByFirstLetterOfPrograms = service.getListOfAppreadStudentByFirstLetterOfPrograms(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
boolean flag= false;
System.out.println("ListOfAppreadStudentByFirstLetterOfPrograms" + ListOfAppreadStudentByFirstLetterOfPrograms.toString());

	  for(AISHEUGCReportsBean bean :ListOfAppreadStudentByFirstLetterOfPrograms) {
		  if(bean.getProgram().equals("DMM") && bean.getTotal().equals("153") ){
			  flag = true;
		  }
		  System.out.println("program count test 3  " +bean);
	  }
assertEquals(true,flag);
}
	
	
	public void test4() throws Exception{
	
	String	enrollmentYear ="2018";
	String	enrollmentMonth ="Dec";
	String	sem ="1";
	//String	firstLetterOfProgram ="D";
	ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = service.getAllListOfProgram(enrollmentYear,enrollmentMonth,sem);
boolean flag= false;

	  if(AllListOfProgram != null || AllListOfProgram.size() > 0){
		  flag = true;
	  }
assertEquals(true,flag);
}

	
	@Test
	public void test5() throws Exception{
	
	String	enrollmentYear ="2021";
	String	enrollmentMonth ="Dec";
	String	sem ="1";
	String	firstLetterOfProgram ="D";
	ArrayList<AISHEUGCReportsBean> ListOfStudentPassedByFirstLetterOfProgram = service.getListOfStudentPassesByFirstLetterOfPrograms(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
boolean flag= false;

System.out.println("ListOfStudentPassedByFirstLetterOfProgram" + ListOfStudentPassedByFirstLetterOfProgram.toString());
if(ListOfStudentPassedByFirstLetterOfProgram != null &&  ListOfStudentPassedByFirstLetterOfProgram.size() > 0 ||ListOfStudentPassedByFirstLetterOfProgram.size()== 9){
		  for(AISHEUGCReportsBean bean :ListOfStudentPassedByFirstLetterOfProgram) {
			if(bean.getProgram().equals("DMM") && bean.getTotalPass().equals("93") ){
				 flag = true;
		  }
			System.out.println("program count test 5  " +bean.toString());
		  }
}

assertEquals(true,flag);
}
	
	
	@Test
	public void test6() throws Exception{
	
	String	enrollmentYear ="2021";
	String	enrollmentMonth ="Dec";
	String	sem ="1";
	String	firstLetterOfProgram ="D";
	ArrayList<AISHEUGCReportsBean> ListOfFemaleStudentAppearedByFirstLetterOfProgram = service.getListOfFemalesAppearedByFirstLetterOfPrograms(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
boolean flag= false;
System.out.println("ListOfFemaleStudentAppearedByFirstLetterOfProgram" + ListOfFemaleStudentAppearedByFirstLetterOfProgram.toString());

	  for(AISHEUGCReportsBean bean :ListOfFemaleStudentAppearedByFirstLetterOfProgram) {
		  if(bean.getProgram().equals("DMM") && bean.getGirlsTotal().equals("75") ){
			  flag = true;
		  }
		  System.out.println("program count test 6  " +bean);
	  }
assertEquals(true,flag);
}
	
	@Test
	public void test7() throws Exception{
		
		String	enrollmentYear ="2021";
		String	enrollmentMonth ="Dec";
		String	sem ="1";
		String	firstLetterOfProgram ="D";
		ArrayList<AISHEUGCReportsBean> ListOfFemaleStudentPassedByFirstLetterOfProgram = service.getListOfFemalePassesByFirstLetterOfProgram(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
boolean flag= false;
System.out.println("program count test 7 Total size  " +ListOfFemaleStudentPassedByFirstLetterOfProgram.size());
		
			  for(AISHEUGCReportsBean bean :ListOfFemaleStudentPassedByFirstLetterOfProgram) {
				  System.out.println("program count test 7  Before " +bean);
				  if(bean.getProgram().equals("DITM") && bean.getGirlsPass().equals("14") ){
					  flag = true;
				  }
				  System.out.println("program count test 7  " +bean);
			  }	
assertEquals(true,flag);
	}
	
	
	
	@Test
	public void test8() throws Exception{
	
	String	enrollmentYear ="2021";
	String	enrollmentMonth ="Dec";
	String	sem ="1";
	String	firstLetterOfProgram ="D";
	ArrayList<AISHEUGCExcelReportBean> ListOfExcel = service.getExcelListByFirstLetterOfProgram(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
boolean flag= false;
System.out.println("ListOfExcel" + ListOfExcel.toString());

	  for(AISHEUGCExcelReportBean bean :ListOfExcel) {
		  if(bean.getProgram().equals("DMM") && bean.getTotalNoOfGirlsStudentsPasseded().equals("56") ){
			  flag = true;
		  }
		  System.out.println("program count test 8  " +bean);
	  }
assertEquals(true,flag);
}	
	
		
	
	@Test
	public void test9() throws Exception{
		
		String	enrollmentYear ="2021";
		String	enrollmentMonth ="Dec";
		String	sem ="1";
		String	firstLetterOfProgram ="D";
		ArrayList<AISHEUGCExcelReportBean> AllListOfProgram = service.getAllListOfProgram(enrollmentYear,enrollmentMonth,sem);
boolean flag= false;

		  if(AllListOfProgram != null && AllListOfProgram.size() > 0 ){
			  for(AISHEUGCExcelReportBean bean :AllListOfProgram) {
				  
				  if(bean.getProgram().equals("DITM") && bean.getTotalNoOfStudentsAbove60percentage()==24 ){
					  flag = true;  
				  }
			  
			  
			  System.out.println("program count test 9   " +bean);
		  }
		  }
		
assertEquals(true,flag);
	}
	
	
	
	//fail on purpose
	@Test 
	public void test10() throws Exception{
		
		String	enrollmentYear ="2021";
		String	enrollmentMonth ="Dec";
		String	sem ="1";
		String	firstLetterOfProgram ="D";
		ArrayList<AISHEUGCExcelReportBean> ListOfExcel = service.getExcelListByFirstLetterOfProgram(enrollmentYear,enrollmentMonth,sem,firstLetterOfProgram);
	boolean flag= false;
	System.out.println("ListOfExcel" + ListOfExcel.toString());

		  for(AISHEUGCExcelReportBean bean :ListOfExcel) {
			  if(bean.getProgram().equals("DMM") && bean.getTotalNoOfGirlsStudentsPasseded().equals("101") ){
				  flag = true;
			  }
			  System.out.println("program count test 10  " +bean);
		  }
	assertEquals(true,flag);
	}
	
	


}
	
	
	


