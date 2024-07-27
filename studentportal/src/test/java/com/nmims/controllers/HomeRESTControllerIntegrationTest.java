package com.nmims.controllers;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.nmims.beans.ReRegistrationStudentPortalBean;
import com.nmims.services.IStudentReRegistrationService;


//@AutoConfigureMockMvc
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@WebAppConfiguration
public class HomeRESTControllerIntegrationTest {
	
	private static final String WRONG_SAPID = "77114000757";
	private static final String DOB_FOR_WRONG_SAPID = "2016-01-31";
	
	private static final String CORRECT_SAPID = "77220653027";
	private static final String DOB_FOR_CORRECT_SAPID = "1990-06-01";
	
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
	@Autowired
	IStudentReRegistrationService studentReRegistrationService;

	/**
	 * Correct SapId
	 * */	
//	@Test
	public void testReRegForMobile() throws Exception{
		
		ReRegistrationStudentPortalBean newStudent = new ReRegistrationStudentPortalBean(false, true, true, true, "2022", "Jul", "00:00:00", "23:59:00", CORRECT_SAPID , DOB_FOR_CORRECT_SAPID, "158", "");

		Gson gson = new Gson();
		String newStudentJSON = gson.toJson(newStudent);
		
		String rawData = mockMvc.perform(post("/m/reRegForMobile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(newStudentJSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		
		ReRegistrationStudentPortalBean newStudent1 = gson.fromJson(rawData, ReRegistrationStudentPortalBean.class);
		System.err.println(newStudent1.getUrl());
		assertEquals("https://ngasce.secure.force.com/nmLogin_new?studentNo=77220653027&dob=01/06/1990&type=reregistration", newStudent1.getUrl());
	}
	
	/**
	 * Wrong SapId
	 * */
//	@Test
	public void getReRegForMobileWrongSapId() throws Exception{
		ReRegistrationStudentPortalBean newStudent = new ReRegistrationStudentPortalBean(false, true, true, true, "2022", "Jul", "00:00:00", "23:59:00", WRONG_SAPID , DOB_FOR_WRONG_SAPID, "158", "");

		Gson gson = new Gson();
		String newStudentJSON = gson.toJson(newStudent);
		
		String rawData = mockMvc.perform(post("/m/reRegForMobile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(newStudentJSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		
		ReRegistrationStudentPortalBean newStudent1 = gson.fromJson(rawData, ReRegistrationStudentPortalBean.class);
		System.err.println(newStudent1.getUrl());
		assertEquals("", newStudent1.getUrl());
	}
	
	/**
	 * SapId with other CPSID
	 * */
//	@Test
	public void getReRegForMobileSapIdWithOtherCPSID() throws Exception{
		ReRegistrationStudentPortalBean newStudent = new ReRegistrationStudentPortalBean(false, true, true, true, "2022", "Jul", "00:00:00", "23:59:00", WRONG_SAPID , DOB_FOR_WRONG_SAPID, "151", "");

		Gson gson = new Gson();
		String newStudentJSON = gson.toJson(newStudent);
		
		String rawData = mockMvc.perform(post("/m/reRegForMobile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(newStudentJSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		
		ReRegistrationStudentPortalBean newStudent1 = gson.fromJson(rawData, ReRegistrationStudentPortalBean.class);
		System.err.println(newStudent1.getUrl());
		assertEquals("https://ngasce.secure.force.com/nmLogin_new?studentNo=77114000757&dob=31/01/2016&type=reregistration", newStudent1.getUrl());
	}
}
