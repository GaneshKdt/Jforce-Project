package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.beans.IAReportsBean;
import com.nmims.beans.Person;
import com.nmims.dto.IAVerifyParametersReportDto;
import com.nmims.services.IAReportsServiceImpl;

@Controller
public class IAReportsController extends BaseController {
	private IAReportsServiceImpl iaReportsServiceImpl;
	private static final Logger logger = LoggerFactory.getLogger(IAReportsController.class);
	
	@Autowired
	public IAReportsController(IAReportsServiceImpl iaReportsServiceImpl) {
		this.iaReportsServiceImpl = iaReportsServiceImpl;
	}
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<Integer> CURRENT_YEAR_LIST; 
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private ArrayList<Integer> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private ArrayList<String> ACAD_MONTH_LIST;
	
	
	@GetMapping(value = "/admin/iaVerifyParametersReportForm")
	public String iaVerifyParametersReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)) {
			return SERVER_PATH + "/logout";
		}
		
		try {
			IAVerifyParametersReportDto iaVerifyParametersReportDto = new IAVerifyParametersReportDto();
			m.addAttribute("iaReportDto", iaVerifyParametersReportDto);
			
			m.addAttribute("currentYearList", CURRENT_YEAR_LIST);
			m.addAttribute("acadYearList", ACAD_YEAR_LIST);
			m.addAttribute("acadMonthList", ACAD_MONTH_LIST);
			
			HashMap<String, String> facultyIdNameMap = iaReportsServiceImpl.testFacultyIdNameMap();
			m.addAttribute("facultyList", iaReportsServiceImpl.convertHashMapToHyphenSeparatedList(facultyIdNameMap));
			
			m.addAttribute("consumerTypeMap", iaReportsServiceImpl.testConsumerTypeMap());
			HashMap<Integer, String> programIdCodeMap = iaReportsServiceImpl.testProgramMap();
			Integer[] programIdsArray = (Integer[]) programIdCodeMap.keySet().toArray(new Integer[programIdCodeMap.size()]);
			logger.info("ProgramIds corresponding to Test records: " + programIdsArray);
			m.addAttribute("programMap", programIdCodeMap);
			
			HashMap<Integer, HashMap<Integer, String>> programStructureMapByProgramMap = iaReportsServiceImpl.programStructureByProgramIdMap(programIdsArray);
			m.addAttribute("programStructureByProgramMap", new ObjectMapper().writeValueAsString(programStructureMapByProgramMap));				//Converting HashMap to JSON Object for front-end usage
			
			m.addAttribute("testNameList", iaReportsServiceImpl.testNameList());
			m.addAttribute("subjectList", iaReportsServiceImpl.testSubjectList());
			logger.error("Required Test attributes gathered and stored as Model attributes successfully!");
		}
		catch(Exception ex) {
			setSessionMessage(request, false, "Error encountered while fetching the form attributes. Please refresh the page.");
			logger.error("Error occurred while storing test attributes as Model attributes, Exception thrown: " + ex.toString());
		}
		
		return "test/iaVerifyParametersReport";
	}
	
	@PostMapping(value = "/admin/iaVerifyParametersReport")
	public String iaVerifyParametersReport(HttpServletRequest request, HttpServletResponse response, Model m, 
											@ModelAttribute("iaReportDto") IAVerifyParametersReportDto iaVerifyParametersReportDto) {
		if(!checkSession(request, response)) {
			return SERVER_PATH + "/logout";
		}
		
		try {
			Person user = (Person)request.getSession().getAttribute("user");
			String roles = user != null ? user.getRoles() : "";
			logger.info("User Roles retrieved from Session: " + roles);
			
			m.addAttribute("iaReportDto", iaVerifyParametersReportDto);
			logger.info("Filters selected in IA Verify Parameters Report Form: " + iaVerifyParametersReportDto.toString());
			m.addAttribute("userRoles", roles);
			
			m.addAttribute("currentYearList", CURRENT_YEAR_LIST);
			m.addAttribute("acadYearList", ACAD_YEAR_LIST);
			m.addAttribute("acadMonthList", ACAD_MONTH_LIST);
			
			HashMap<String, String> facultyIdNameMap = iaReportsServiceImpl.testFacultyIdNameMap();
			m.addAttribute("facultyList", iaReportsServiceImpl.convertHashMapToHyphenSeparatedList(facultyIdNameMap));
			
			HashMap<Integer, String> consumerTypeIdNameMap = iaReportsServiceImpl.testConsumerTypeMap();
			m.addAttribute("consumerTypeMap", consumerTypeIdNameMap);
			
			HashMap<Integer, String> programIdCodeMap = iaReportsServiceImpl.testProgramMap();
			Integer[] programIdsArray = (Integer[]) programIdCodeMap.keySet().toArray(new Integer[programIdCodeMap.size()]);
			logger.info("ProgramIds corresponding to Test records: " + programIdsArray);
			m.addAttribute("programMap", programIdCodeMap);
			
			HashMap<Integer, HashMap<Integer, String>> programStructureMapByProgramMap = iaReportsServiceImpl.programStructureByProgramIdMap(programIdsArray);
			m.addAttribute("programStructureByProgramMap", new ObjectMapper().writeValueAsString(programStructureMapByProgramMap));				//Converting HashMap to JSON Object for front-end usage
			
			HashMap<Integer, String> programStructMapOfSelectedProgramId = programStructureMapByProgramMap.get(iaVerifyParametersReportDto.getProgramId());
			if(programStructMapOfSelectedProgramId != null) {
				String programStructureSelected = programStructMapOfSelectedProgramId.get(iaVerifyParametersReportDto.getProgramStructureId());
				m.addAttribute("selectedProgramStructureName", programStructureSelected);			//Storing ProgramStructure Name of selected ProgramStructure in Session for displaying in front-end 
			}
			
			m.addAttribute("testNameList", iaReportsServiceImpl.testNameList());
			m.addAttribute("subjectList", iaReportsServiceImpl.testSubjectList());
			
			ArrayList<IAReportsBean> iaVerifyParametersReportDataList = iaReportsServiceImpl.iaVerifyParametersReportData(iaVerifyParametersReportDto, facultyIdNameMap, 
																													consumerTypeIdNameMap, programIdCodeMap, programStructureMapByProgramMap);
			request.getSession().setAttribute("iaReportDataList", iaVerifyParametersReportDataList);		//List of Test data is stored in Session to use in downloadIaVerifyParametersReport method aswell
			
			int reportDataListSize = iaVerifyParametersReportDataList.size();
			if(reportDataListSize == 0) 												//display errorMessage if the size of the dataList is 0
				setSessionMessage(request, false, "No Records Found! Please try again with different Parameters.");
			m.addAttribute("dataCount", reportDataListSize);							//Size of dataList is stored to displayed the count of Test Records retrieved
			logger.error("Required Test attributes gathered and stored as Model attributes, also test records successfully retrieved and stored in Session!");
		}
		catch(Exception ex) {
			setSessionMessage(request, false, "Error encountered while retrieving the Internal Assessment data. Please try again!");
			logger.error("Error occurred while storing test attributes or retrieving Test records, Exception thrown: " + ex.toString());
		}

		return "test/iaVerifyParametersReport";
	}
	
	/**
	 * Creates a Excel from the IA VerifyParameterReport data stored in Session.
	 * @param request - HttpServletRequest contains the Session attribute for the required data.
	 * @param response - HttpServletResponse which sends the created Excel file as a response.
	 */
	@GetMapping(value = "/admin/downloadIaVerifyParametersReport")
	public void downloadIaVerifyParametersReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)) {
			new RedirectView(SERVER_PATH + "/logout");
		}
		
		try {
			@SuppressWarnings("unchecked")		//this is to suppress Type safety: Unchecked cast warning, Java cannot guarantee at Runtime, the object returned
												//from the HttpServletRequest is an instance of ArrayList<IAReportsBean>, throws ClassCast Exception
			ArrayList<IAReportsBean> iaVerifyParametersReportDataList = (ArrayList<IAReportsBean>) request.getSession().getAttribute("iaReportDataList");
			
			iaReportsServiceImpl.downloadIaExcelReportServletResponse(response, iaVerifyParametersReportDataList);		//Sends the output stream of the file created as a Response
			setSessionMessage(request, true, "Your request has been processed, the download will begin shortly.");
			logger.info("Excel Sheet created and stored as a Response object in HttpServletResponse successfully!");
		}
		catch(ClassCastException ex) {
			setSessionMessage(request, false, "Download Failed! Error encountered while reading the report data, please try again!");
			logger.error("Error while retrieving the iaReportDataList from the current HttpSession of the HttpServletRequest object, Exception thrown: " + ex.toString());
		}
		catch(IOException ex) {
			setSessionMessage(request, false, "Download Failed! Error encountered while creating the Excel file for download, please try again!");
			logger.error("Error while writing the XSSFWorkbook created as an OutputStream, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			setSessionMessage(request, false, "Download Failed! Error encountered while processing the download request, please try again!");
			logger.error("Error while creating the XSSFWorkbook with the IAReports data and storing as a Response object in HttpServletResponse, Exception thrown: " + ex.toString());
		}
	}
	
	/**
	 * Adds Success / Error Message in Session object
	 * @param request - HttpServletRequest to obtain the current session
	 * @param isSuccess	- flag which denotes if the type is Success or Error
	 * @param message - String message to be displayed
	 */
	private void setSessionMessage(HttpServletRequest request, boolean isSuccess, String message) {
		if(isSuccess) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", message);
		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", message);
		}
	}
}
