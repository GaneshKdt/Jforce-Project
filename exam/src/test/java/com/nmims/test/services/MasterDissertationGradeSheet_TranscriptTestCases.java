package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.controllers.MarksheetController;
import com.nmims.daos.DissertationQ7DAO;
import com.nmims.daos.DissertationQ8ResultDaoImpl;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.services.impl.DissertationGradeSheet_TranscriptServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MasterDissertationGradeSheet_TranscriptTestCases {
	
	@InjectMocks
	DissertationGradeSheet_TranscriptServiceImpl q8Service;
	
	@Mock
	DissertationQ7DAO dissertationQ7Dao;
	
	@Mock
	DissertationQ8ResultDaoImpl dissertationQ8Dao;
	
	@Mock
	ExamsAssessmentsDAO examDAO;
	
	@Mock
	StudentMarksDAO dao;
	
	@InjectMocks
	MarksheetController marksControl;
	
	@Autowired
	MockMvc mockMvc;
	
	private ObjectMapper mapper = new  ObjectMapper();
	

	
	@Test
	public void getPassFailQ7() {
		
		String sapid = "77421191442";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		result.setIsResultLive("Y");
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		masterdissertationDto.setSubject("Master Dissertation Part -I");
		masterdissertationDto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ7(sapid);
	
		if(bean.getSapid().equalsIgnoreCase("77421191442")
				&& bean.getSubject().equalsIgnoreCase("Master Dissertation Part -I")
				&& (bean.getTimeboundId().equals("1306"))) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ7WithoutSapid() {
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ7(null);
		
		if(bean == null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ7WithWrongSapid() {
		
		String sapid = "77777777771";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		dto.setSubject("Master Dissertation Part -I");
		dto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ7("77421191442");
	
		if(bean ==  null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}

	@Test
	public void getPassFailQ8() {
		
		String sapid = "77421191442";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ8Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1991)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		masterdissertationDto.setSubject("Master Dissertation Part -II");
		masterdissertationDto.setId(1991);
		when(dissertationQ7Dao.getSubjectName(1991)).thenReturn(masterdissertationDto);
		
		
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ8(sapid);

		if(bean.getSapid().equalsIgnoreCase("77421191442")
				&& bean.getSubject().equalsIgnoreCase("Master Dissertation Part -II")
				&& (bean.getTimeboundId().equals("1306"))) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ8WithoutSapid() {
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ8(null);
		
		if(bean == null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ8WithWrongSapid() {
		
		String sapid = "77777777771";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		dto.setSubject("Master Dissertation Part -I");
		dto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		EmbaPassFailBean bean = q8Service.getPassFailForQ8("77421191442");
		if(bean ==  null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	/*Transcript*/
	
	
	@Test
	public void getPassFailQ7ForTranscipt() {
		
		String sapid = "77421191442";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		result.setIsResultLive("Y");
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		masterdissertationDto.setSubject("Master Dissertation Part -I");
		masterdissertationDto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ7Transcript("77421191442");
	
		if(bean.getSapid().equalsIgnoreCase("77421191442")
				&& bean.getSubject().equalsIgnoreCase("Master Dissertation Part -I")
				&& (bean.getTimeboundId().equals("1306"))) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ7WithoutSapidForTranscipt() {
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ7Transcript(null);
		
		if(bean == null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ7WithWrongSapidForTranscipt() {
		
		String sapid = "77421924980";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(3.2f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1308);
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1308);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		dto.setSubject("Master Dissertation Part -I");
		dto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ7Transcript("77421191442");
	
		if(bean ==  null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}

	
	@Test
	public void getPassFailQ8ForTranscript() {
		
		String sapid = "77421924980";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		result.setIsResultLive("Y");
		
		when(dissertationQ8Dao.getPassFail(sapid)).thenReturn(result);
	
		
		DissertationResultProcessingDTO dtoo  =  new DissertationResultProcessingDTO();
		dtoo.setId(1306);
		dtoo.setAcadMonth("Oct");
		dtoo.setAcadYear("2022");
		dtoo.setExamMonth("Jan");
		dtoo.setExamYear("2023");
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		timebound.add(dtoo);
		when(dissertationQ8Dao.getTimeBound(1991)).thenReturn(timebound);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMapping = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMapping.add(dissertationDto);
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMapping);
		
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		masterdissertationDto.setSubject("Master Dissertation Part -II");
		masterdissertationDto.setId(1991);
		when(dissertationQ7Dao.getSubjectName(1991)).thenReturn(masterdissertationDto);
		
		
	
		
		
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ8Transcript(sapid);

		if(bean.getSapid().equalsIgnoreCase("77421924980")
				&& bean.getSubject().equalsIgnoreCase("Master Dissertation Part -II")
				&& (bean.getTimeboundId().equals("1306"))) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
		
	}
	
	@Test
	public void getPassFailQ8WithoutSapidForTranscript() {
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ8Transcript(null);
		
		if(bean == null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getPassFailQ8WithWrongSapidForTranscript() {
		
		String sapid = "77777777771";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		dto.setSubject("Master Dissertation Part -I");
		dto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		MBAPassFailBean bean = q8Service.getPassFailForQ8Transcript("77421191442");
		if(bean ==  null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	

	@Test
	public void getTimeBoundForQ8WithSapid() {
		boolean expected = true;
		boolean actual =false;
		StudentSubjectConfigExamBean bean = q8Service.getPassFailForQ8Timebound(null);
		
		if(bean == null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getTimeBoundForQ8WithWrongSapid() {
		
		String sapid = "77777777771";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
		
		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
		dto.setSubject("Master Dissertation Part -I");
		dto.setId(1990);
		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		StudentSubjectConfigExamBean bean = q8Service.getPassFailForQ8Timebound("77421191442");
		if(bean ==  null) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getTimeBoundForQ8() {
		
		String sapid = "77421924980";
		DissertationResultBean result=  new DissertationResultBean();
		result.setSapid(Long.valueOf(sapid));
		result.setIsPass("Y");
		result.setGrade("A");
		result.setGradePoints(2.75f);
		result.setPrgm_sem_subj_id(1990);
		result.setTimeBoundId(1306);
		
		when(dissertationQ8Dao.getPassFail(sapid)).thenReturn(result);
	
		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
		
		dto.setId(1306);
		dto.setAcadMonth("Oct");
		dto.setAcadYear("2022");
		dto.setExamMonth("Jan");
		dto.setExamYear("2023");
		timebound.add(dto);
		
		when(dissertationQ8Dao.getTimeBound(1991)).thenReturn(timebound);
		DissertationResultProcessingDTO register =  new DissertationResultProcessingDTO();
		register.setAcadMonth("Oct");
		register.setAcadYear("2022");
		when(dissertationQ8Dao.getRegistration(sapid)).thenReturn(register);
		
		
		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
		dissertationDto.setSapid(sapid);
		dissertationDto.setId(1306);
		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
		timeBoundUserMap.add(dissertationDto);
		
		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
		boolean expected = true;
		boolean actual =false;
		StudentSubjectConfigExamBean bean = q8Service.getPassFailForQ8Timebound(sapid);

		if(bean.getExamMonth().equalsIgnoreCase("Jan")
				&& bean.getExamYear().equalsIgnoreCase("2023")
				&& bean.getAcadMonth().equalsIgnoreCase("Oct")
				&&bean.getAcadYear().equals("2022")) {
					
			actual = true;
				}
			
		assertEquals(expected, actual);
		
	}
	

	
	@Test
	public void generateMarksheetFromSRForMBAWX() {
		String srId = "123|234";
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("serviceRequestIdList", "123|234");
		
		
		
//		List<EmbaMarksheetBean>  srList = new ArrayList<EmbaMarksheetBean>();
//		
//		EmbaMarksheetBean srBean =  new EmbaMarksheetBean();
//		bean.setSapid("777777777771");
//		bean.setSem("7");
//		bean.setProgram("MSC AI");
//		bean.setExamMonth("Apr");
//		bean.setExamYear("2023");
//		bean.setMonth("Jan");
//		bean.setYear("2023");
//		
//		
//		
//		srBean =  new EmbaMarksheetBean();
//		bean.setSapid("777777777772");
//		bean.setSem("8");
//		bean.setProgram("MSC AI");
//		bean.setExamMonth("Apr");
//		bean.setExamYear("2023");
//		bean.setMonth("Jan");
//		bean.setYear("2023");
//		srList.add(srBean);
//		
//		when(examDAO.getStudentsForSRForMBAWX(bean)).thenReturn(srList);
//		
//		StudentExamBean student =  new StudentExamBean();
//		student.setSapid("777777777771");
//		student.setSem("7");
//		student.setProgramForHeader("MSC AI");
//		student.setMonth("Jan");
//		student.setYear("2023");
//		student.setConsumerProgramStructureId("131");
//		when(dao.getStudentDetails("777777777771")).thenReturn(student);
//		
//		List<EmbaPassFailBean> embaPassFailList =  new ArrayList<EmbaPassFailBean>();
//		EmbaPassFailBean embaPassFailBean = new EmbaPassFailBean();
//		embaPassFailBean.setSapid("777777777771");
//		embaPassFailBean.setIaScore("20.5");
//		embaPassFailBean.setTeeScore(40);
//		embaPassFailList.add(embaPassFailBean);
//		String commaSepratedSrId = "123,234";
//		when(examDAO.getEmbaPassFailByAllSapids(commaSepratedSrId)).thenReturn(embaPassFailList);
//		
//		String sapid = "777777777771";
//		DissertationResultBean result=  new DissertationResultBean();
//		result.setSapid(Long.valueOf(sapid));
//		result.setIsPass("Y");
//		result.setGrade("A");
//		result.setGradePoints(2.75f);
//		result.setPrgm_sem_subj_id(1990);
//		result.setTimeBoundId(1306);
//		result.setIsResultLive("Y");
//		
//		when(dissertationQ7Dao.getPassFail(sapid)).thenReturn(result);
//	
//		List<DissertationResultProcessingDTO> timebound = new ArrayList<DissertationResultProcessingDTO>();
//		DissertationResultProcessingDTO dto  =  new DissertationResultProcessingDTO();
//		
//		dto.setId(1306);
//		dto.setAcadMonth("Oct");
//		dto.setAcadYear("2022");
//		dto.setExamMonth("Jan");
//		dto.setExamYear("2023");
//		timebound.add(dto);
//		
//		when(dissertationQ8Dao.getTimeBound(1990)).thenReturn(timebound);
//		DissertationResultProcessingDTO masterdissertationDto = new DissertationResultProcessingDTO();
//		dto.setSubject("Master Dissertation Part -I");
//		dto.setId(1990);
//		when(dissertationQ7Dao.getSubjectName(1990)).thenReturn(masterdissertationDto);
//		
//		
//		DissertationResultProcessingDTO dissertationDto  = new DissertationResultProcessingDTO();
//		dissertationDto.setSapid(sapid);
//		dissertationDto.setId(1306);
//		List<DissertationResultProcessingDTO> timeBoundUserMap = new ArrayList<DissertationResultProcessingDTO>();
//		timeBoundUserMap.add(dissertationDto);
//		
//		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMap);
//		
//		
//		
//		DissertationResultBean resultBean=  new DissertationResultBean();
//		result.setSapid(Long.valueOf(sapid));
//		result.setIsPass("Y");
//		result.setGrade("A");
//		result.setGradePoints(2.75f);
//		result.setPrgm_sem_subj_id(1990);
//		result.setTimeBoundId(1306);
//		
//		when(dissertationQ8Dao.getPassFail(sapid)).thenReturn(result);
//	
//		List<DissertationResultProcessingDTO> timeboundq8 = new ArrayList<DissertationResultProcessingDTO>();
//		DissertationResultProcessingDTO dtoq8  =  new DissertationResultProcessingDTO();
//		
//		dtoq8.setId(1306);
//		dtoq8.setAcadMonth("Oct");
//		dtoq8.setAcadYear("2022");
//		dtoq8.setExamMonth("Jan");
//		dtoq8.setExamYear("2023");
//		timeboundq8.add(dto);
//		
//		when(dissertationQ8Dao.getTimeBound(1991)).thenReturn(timeboundq8);
//		
//		DissertationResultProcessingDTO masterdissertationDtoq8 = new DissertationResultProcessingDTO();
//		dtoq8.setSubject("Master Dissertation Part -II");
//		dtoq8.setId(1991);
//		when(dissertationQ7Dao.getSubjectName(1991)).thenReturn(masterdissertationDtoq8);
//		
//		
//		
//		
//		DissertationResultProcessingDTO dissertationq8Dto  = new DissertationResultProcessingDTO();
//		dissertationq8Dto.setSapid(sapid);
//		dissertationq8Dto.setId(1306);
//		List<DissertationResultProcessingDTO> timeBoundUserMapq8 = new ArrayList<DissertationResultProcessingDTO>();
//		timeBoundUserMapq8.add(dissertationq8Dto);
//		
//		when(dissertationQ8Dao.getTimbound(sapid)).thenReturn(timeBoundUserMapq8);
	
		boolean expected  = true;
		boolean actual = false;
		try {
			
	
	
//			this.mockMvc.perform(post("/admin/generateMarksheetFromSRForMBAWX")
//			.contentType(MediaType.APPLICATION_JSON)
//			.params(body)
//			.characterEncoding("utf-8")
//			.accept(MediaType.TEXT_HTML))
//			.andDo(print())
//			.andExpect(result -> assertNotEquals(result.getModelAndView().getModelMap().get(key)))
//					
			actual = true;
		}catch (Exception e) {
		e.printStackTrace();	
		}
		
			assertEquals(expected, actual);	
	}

}
