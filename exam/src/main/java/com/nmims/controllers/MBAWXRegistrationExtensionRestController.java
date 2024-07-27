package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.MBAWXExamRegistrationExtensionBean;
import com.nmims.daos.MBAWXLiveSettingsDAO;

@RestController
@RequestMapping("m")
public class MBAWXRegistrationExtensionRestController {

	@Autowired
	MBAWXLiveSettingsDAO liveSettingsDAO;

	@RequestMapping(value = "/getRegistrationExtendedStudents", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public List<MBAWXExamRegistrationExtensionBean> getRegistrationExtendedStudents() {

		List<MBAWXExamRegistrationExtensionBean> beans = liveSettingsDAO.getAllExtendedRegistrationStudents();

		return beans;
	}

	@RequestMapping(value = "/extendRegistrationDateTimeForSapIds", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public Map<String, List<String>> extendRegistrationDateTimeForSapIds(HttpServletRequest request, HttpServletResponse response,
			@RequestBody MBAWXExamRegistrationExtensionBean extendedStudentsData) {
		
		Map<String, List<String>> responseMap = new HashMap<String, List<String>>();
		List<String> errorSapIdList = new ArrayList<String>();
		List<String> successSapIdList = new ArrayList<String>();

		String individualSapIds[] = extendedStudentsData.getSapid().split(",");

		for (String sapId : individualSapIds) {

			MBAWXExamRegistrationExtensionBean bean = new MBAWXExamRegistrationExtensionBean();
			
			sapId =  sapId.trim();
			
			bean.setSapid(sapId);
			bean.setExamMonth(extendedStudentsData.getExamMonth());
			bean.setExamYear(extendedStudentsData.getExamYear());
			bean.setExtendStartDateTime(extendedStudentsData.getExtendStartDateTime().replaceFirst("T", " "));
			bean.setExtendEndDateTime(extendedStudentsData.getExtendEndDateTime().replaceFirst("T", " "));
			bean.setCreatedBy(extendedStudentsData.getCreatedBy());

			try {
				int rowsAffected = insertIntoExtensionTableForIndividualSapid(bean);
				
				if(rowsAffected > 0) 
					successSapIdList.add(bean.getSapid());
				else 
					errorSapIdList.add(bean.getSapid());

			} catch (Exception e) {
				errorSapIdList.add(bean.getSapid());
			}
			
			responseMap.put("error", errorSapIdList);
			responseMap.put("success", successSapIdList);
		}
		return responseMap;
	}

	private int insertIntoExtensionTableForIndividualSapid(MBAWXExamRegistrationExtensionBean bean) {

		String masterKey = liveSettingsDAO.returnMasterKeyifStudentIsTimebound(bean);
		
		bean.setConsumerProgramStructureId(masterKey);

		int rowsAffected = liveSettingsDAO.insertIntoRegistrationExtensionTable(bean);
		
		return rowsAffected;
	}

	@RequestMapping(value = "/deleteExtendedRegistration", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json")
	public Map<String, String> deleteExtendedRegistration(HttpServletRequest request, HttpServletResponse response,
			@RequestBody MBAWXExamRegistrationExtensionBean bean) {
		
		Map<String, String> statusMap = new HashMap<>(); 
		int rowsAffected = 0;
		String successStatus = null;
		
		try {
			rowsAffected = liveSettingsDAO.deleteRegistrationExtension(bean);

			if (rowsAffected < 1) {
				successStatus = "failed";
			} else 
				successStatus = "successful";
		} catch (Exception e) {
			successStatus = "failed";
		}
		
		statusMap.put("status", successStatus);
		
		return statusMap;
	}
}
