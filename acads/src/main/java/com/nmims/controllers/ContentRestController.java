package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ResponseAcadsBean;
import com.nmims.factory.ContentFactory;
import com.nmims.factory.ContentFactory.StudentType;
import com.nmims.interfaces.ContentInterface;

@RestController
public class ContentRestController 
{
	@Autowired
    private ContentFactory contentFactory;  
	
	//get the data from consumer type

	@RequestMapping(value = "/getDataByConsumerType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByConsumerType(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
	
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		
		try {
			
			
			ArrayList<ConsumerProgramStructureAcads> programStructureData = content.getProgramStructureByConsumerType(consumerProgramStructure.getId());
			ArrayList<ConsumerProgramStructureAcads> programData = content.getProgramByConsumerType(consumerProgramStructure.getId());
			
			String programDataId = "";
			for(int i=0;i < programData.size();i++){
				programDataId = programDataId + ""+ programData.get(i).getId() +",";
			}
			programDataId = programDataId.substring(0,programDataId.length()-1);
			
			String programStructureDataId = "";
			for(int i=0;i < programStructureData.size();i++){
				programStructureDataId = programStructureDataId + ""+ programStructureData.get(i).getId() +",";
			}
			programStructureDataId = programStructureDataId.substring(0,programStructureDataId.length()-1);
			
			response.setStatus("success");
			response.setProgramStructureData(programStructureData);
			response.setProgramsData(programData);
			response.setSubjectsData(content.getSubjectByConsumerType(consumerProgramStructure.getId(),programDataId,programStructureDataId));
		

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	
	@RequestMapping(value = "/getDataByProgramStructure",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByProgramStructure(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
	
		
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			
		
			ArrayList<ConsumerProgramStructureAcads> programData = content.getProgramByConsumerTypeAndPrgmStructure(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramStructureId());
			
			String programDataId = "";
			for(int i=0;i < programData.size();i++){
				programDataId = programDataId + ""+ programData.get(i).getId() +",";
			}
			
			programDataId = programDataId.substring(0,programDataId.length()-1);
			
			response.setStatus("success");
			
			
			response.setProgramsData(programData);
			response.setSubjectsData(content.getSubjectByConsumerType(consumerProgramStructure.getConsumerTypeId(),programDataId,consumerProgramStructure.getProgramStructureId()));
		

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	@RequestMapping(value = "/getDataByProgram",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseAcadsBean> getDataByProgram(@RequestBody ConsumerProgramStructureAcads consumerProgramStructure){
	
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		
		ResponseAcadsBean response = (ResponseAcadsBean) new ResponseAcadsBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response.setStatus("success");
			response.setSubjectsData(content.getSubjectByConsumerType(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramId(),consumerProgramStructure.getProgramStructureId()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	@RequestMapping(value = "/getProgramsListForCommonContent", method = {RequestMethod.POST})
	public ResponseEntity<List<ContentAcadsBean>> getProgramsListForCommonContent(@RequestBody ContentAcadsBean bean) {
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		
		return new ResponseEntity<List<ContentAcadsBean>>(content.getProgramsListForCommonContent(bean.getId()),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getCommonContentProgramsList", method = {RequestMethod.POST})
	public ResponseEntity<ArrayList<ContentAcadsBean>> getCommonContentProgramsList(@RequestBody ContentAcadsBean bean) {
		//ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		
		return new ResponseEntity<ArrayList<ContentAcadsBean>>(contents.getCommonGroupProgramList(bean),HttpStatus.OK);
	}

}
