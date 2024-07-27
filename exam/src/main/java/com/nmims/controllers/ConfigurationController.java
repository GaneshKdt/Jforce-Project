package com.nmims.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ConfigurationExam;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ConsumerType;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.FailedSubjectCountCriteriaBean;
import com.nmims.beans.GroupExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramStructureBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProgramsBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.SubjectBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ApplozicGroupHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.ConfigurationServiceInterface;
import com.nmims.views.StudentTimeboundExcelView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ConfigurationController extends BaseController{
		
			@Autowired(required=false)
			ApplicationContext act;
			
			@Autowired
			DashboardDAO dashboardDao;
			
			@Autowired
			ServiceRequestDAO serviceRequestDao;
			
			@Autowired
			AssignmentsDAO asignmentsDAO;

			@Autowired
			ApplozicGroupHelper applozicGroupHelper;
			
			@Value("${SERVER_PATH}")
			private String SERVER_PATH;
			
			@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
			private List<String> ACAD_YEAR_LIST;
			
			@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
			private List<String> ACAD_MONTH_LIST;
			
			@Autowired
			ConfigurationServiceInterface configurationService;
			
			@Autowired
			StudentTimeboundExcelView studentTimeboundExcelView;
		
			/**
			 * Refresh Cache function to refresh cache
			 * @param 
			 * none
			 * @return 
			 * none
			 * */
			public String RefreshCache() {
				programListFromProgramMaster = null;
				getProgramListFromProgramMaster();
				
				progStructListFromProgramMaster = null;
				getProgStructListFromProgramMaster();
				
				return null;
			}
			@ModelAttribute("configurationList")
			public ArrayList<String> getConfigurationList(){
				return this.configurationList;
			}
			public ArrayList<String> configurationList = new ArrayList<String>(Arrays.asList( 
					"Exam Registration","Re-sit Exam Registation","Hall Ticket Download")); 
			public ArrayList<String> semesterList = new ArrayList<String>(Arrays.asList( 
					"1","2","3","4","5","6","7","8","9","10")); 
			
			private static String statusMessage = "message";
			private static String status = "Status";
			private static String success = "Success";
			private static String error = "Fail";
			private static String successDeleteMessage = "Entry deleted successfully.";
			private static String successUpdateMessage = "Entry updated successfully.";
			@ModelAttribute("programListFromProgramMaster")
			public ArrayList<String> getProgramListFromProgramMaster() {
				if (this.programListFromProgramMaster == null) {
					this.programListFromProgramMaster = dashboardDao.getProgramListFromProgramMaster();
				}
				return programListFromProgramMaster;
			}
			@ModelAttribute("progStructListFromProgramMaster")
			public ArrayList<String> getProgStructListFromProgramMaster() {
				if (this.progStructListFromProgramMaster == null) {
					this.progStructListFromProgramMaster = dashboardDao.getProgStructListFromProgramMaster();
				}
				return progStructListFromProgramMaster;
			}
			private ArrayList<String> programListFromProgramMaster = null;
			private ArrayList<String> progStructListFromProgramMaster=null;
			//private ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = null;
			private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
			/*private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList( 
			"2014","2015","2016", "2017" , "2018" , "2019"));*/
			

			@RequestMapping(value = "/admin/changeConfigurationForm", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {
				ModelAndView modelnView = new ModelAndView("changeConfiguration");
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				List<ConfigurationExam> currentConfList = dao.getCurrentConfigurationList(); 
				modelnView.addObject("currentConfList", currentConfList);
				
				ConfigurationExam configuration = new ConfigurationExam();
				m.addAttribute("configuration", configuration);
				return modelnView;
			}
			

			
			@RequestMapping(value = "/admin/changeConfiguration", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView changeConfiguration(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ConfigurationExam configuration) {
				logger.info("Make results live Page");
				ModelAndView modelnView = new ModelAndView("changeConfiguration");
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				try{
					configuration.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
					dao.updateConfiguration(configuration);
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Date/Time changed successfully for "+configuration.getConfigurationType());
				}catch(Exception e){
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in changing configuration.");
				}
				
				
				List<ConfigurationExam> currentConfList = dao.getCurrentConfigurationList(); 
				modelnView.addObject("currentConfList", currentConfList);
				modelnView.addObject("configuration", configuration);
				return modelnView;
			}
			
			// Newly Added to Make Exam Registration Time Based means Make Exam Registration Live For Temporary 
			@RequestMapping(value = "/admin/extendExamRegistrationDatesForm", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView extendExamRegistrationForm(HttpServletRequest request, HttpServletResponse response, Model m) {
				ModelAndView modelnView = new ModelAndView("extendExamRegistrationDatesForm");
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				List<ConfigurationExam> currentConfList = dao.getCurrentConfigurationListForExamRegistration(); 
				modelnView.addObject("currentConfList", currentConfList);
				
				ConfigurationExam configuration = new ConfigurationExam();
				m.addAttribute("configuration", configuration);
				m.addAttribute("server_path", SERVER_PATH);
				return modelnView;
			}
			@RequestMapping(value="/encryptSAidList",method={RequestMethod.GET})
			public @ResponseBody String commaSeperatedEncryptedList(@RequestParam(value="saidList") String myArray,HttpServletRequest request,HttpServletResponse response){
				String json ="";
				 String[] values = myArray.split(",");
				 for(String s : values){
					 try{
						 json = json + AESencrp.encrypt(s)+",";
					 }catch(Exception e){
						 
					 }
					 
				 }
				return json;
				
			}
			@RequestMapping(value = "/admin/extendExamRegistrationDates", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView extendExamRegistrationDates(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ConfigurationExam configuration) {
				logger.info("Make results live Page");
				ModelAndView modelnView = new ModelAndView("extendExamRegistrationDatesForm");
				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				try{
					configuration.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
					dao.updateExtendedExamRegistrtaionDates(configuration);
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Date/Time changed successfully for "+configuration.getConfigurationType());
				}catch(Exception e){
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in changing configuration.");
				}
				
				
				List<ConfigurationExam> currentConfList = dao.getCurrentConfigurationListForExamRegistration(); 
				modelnView.addObject("currentConfList", currentConfList);
				modelnView.addObject("configuration", configuration);
				return modelnView;
			}
			
			//MDM Pages START
			//Page 1 : Consumer Type Entries Add/Update Start
			@RequestMapping(value="/admin/consumerTypeForm",method= {RequestMethod.GET})
			public ModelAndView consumerTypeForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ConsumerType consumerTypeBean) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/consumerMDM");
				try{
					request.getSession().setAttribute("consumerTypeBean",consumerTypeBean);
					ArrayList<ConsumerType> consumerTypeListBean = dashboardDao.getConsumerTypeList();
					request.getSession().setAttribute("consumerTypeListBean",consumerTypeListBean);
					mav.addObject("consumerTypeListBean",consumerTypeListBean);
					mav.addObject("consumerTypeBean",new ConsumerType());
					String userId = (String)request.getSession().getAttribute("userId");
					mav.addObject("userId",userId);
				}catch(Exception e){
					
				}		
				return mav;
				}
			@RequestMapping(value="/admin/consumerTypeFormData",method= {RequestMethod.POST})
			public ModelAndView consumerTypeFormData(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ConsumerType consumerType)
				{
					ModelAndView mav=new ModelAndView("mdm/consumerMDM");
					try {
						consumerType.setCreatedBy((String)request.getSession().getAttribute("userId"));
						consumerType.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
						if(consumerType.getName()!=null || consumerType.getIsCorporate()!=null) {
							String responseData  = dashboardDao.insertIntoConsumerTypeTable(consumerType);
							if(responseData.equalsIgnoreCase("true")) {
								request.setAttribute("success","true");
								request.setAttribute("successMessage","Successfully data inserted");
							}
							else {
								request.setAttribute("error","true");
								request.setAttribute("errorMessage",responseData);
							}
						}
						request.getSession().setAttribute("consumerTypeBean",consumerType);
						ArrayList<ConsumerType> consumerTypeListBean = dashboardDao.getConsumerTypeList();
						request.getSession().setAttribute("consumerTypeListBean",consumerTypeListBean);
						mav.addObject("consumerTypeListBean",consumerTypeListBean);
						mav.addObject("consumerTypeBean",new ConsumerType());
						mav.addObject("userId",(String)request.getSession().getAttribute("userId"));
					}catch(Exception e) {
						
						request.setAttribute("error","true");
						request.setAttribute("errorMessage",e.getMessage());
					}
					return mav;
				}		
			@RequestMapping(value="/admin/updateConsumerType",method= {RequestMethod.POST})
			public ResponseEntity<ResponseBean> updateConsumerType(HttpServletRequest request,@RequestBody ConsumerType consumerTypeBean){
				ResponseBean response = new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					consumerTypeBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
					String responseData  = dashboardDao.updateConsumerTypeEntry(consumerTypeBean);
					if(responseData.equalsIgnoreCase("true")) {
						response.setStatus("Success");
						response.setMessage("Successfully data updated");
					}
					else {
						response.setStatus("Error");
						response.setMessage(responseData);
					}
				}catch (Exception e) {
					response.setStatus("Error");
					response.setMessage(e.getMessage());
				}
				return new ResponseEntity<ResponseBean>(response,headers,HttpStatus.OK);
			}
			//Page 1 : Consumer Type Entries Add/Update End
			
			//Page 2 : ProgramStructure Entries Add/Update Start
			@RequestMapping(value="/admin/programStructureForm",method= {RequestMethod.GET})
			public String programStructureForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramStructureBean programStructureBean) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView();
				try{
					request.getSession().setAttribute("programStructureBean",programStructureBean);
					
					ArrayList<ProgramStructureBean> programStructureListBean = dashboardDao.getProgramStructureList();
					request.getSession().setAttribute("programStructureListBean",programStructureListBean);
					mav.addObject("programStructureListBean",programStructureListBean);
					mav.addObject("programStructureBean",new ProgramStructureBean());
					String userId = (String)request.getSession().getAttribute("userId");
					mav.addObject("userId",userId);
				}catch(Exception e){
					
				}
				return "mdm/programStructureMDM";
			}
			@RequestMapping(value="/admin/programStructureFormData",method= {RequestMethod.POST})
			public ModelAndView programStructureFormData(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramStructureBean programStructureBean)
				{
					ModelAndView mav=new ModelAndView("mdm/programStructureMDM");
					try {
						programStructureBean.setCreatedBy((String)request.getSession().getAttribute("userId"));
						programStructureBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
						if(programStructureBean.getProgram_structure()!=null ) {  
							String responseData  = dashboardDao.insertIntoProgramStructureTable(programStructureBean);
							if(responseData.equalsIgnoreCase("true")) {
								request.setAttribute("success","true");
								request.setAttribute("successMessage","Successfully data inserted");
							}
							else {
								request.setAttribute("error","true");
								request.setAttribute("errorMessage",responseData);
							}
						}
					ArrayList<ProgramStructureBean> programStructureListBean=dashboardDao.getProgramStructureList();
					request.getSession().getAttribute("programStructureListBean");
					mav.addObject("programStructureListBean",programStructureListBean);
					mav.addObject("programStructureBean", new ProgramStructureBean());
					}
					catch(Exception e) {
						
						request.setAttribute("error","true");
						request.setAttribute("errorMessage",e.getMessage());
					}
					return mav;
				}
			@RequestMapping(value="/admin/updateProgramStructure",method= {RequestMethod.POST})
			public ResponseEntity<ResponseBean> updateProgramStructure(HttpServletRequest request,@RequestBody ProgramStructureBean programStructureBean){
				ResponseBean response = new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					programStructureBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
					String responseData  = dashboardDao.updateProgramStructureEntry(programStructureBean);
					if(responseData.equalsIgnoreCase("true")) {
						response.setStatus("Success");
						response.setMessage("Successfully data updated");
					}
					else {
						response.setStatus("Error");
						response.setMessage(responseData);
					}
				}
				catch (Exception e) {
					response.setStatus("Error");
					response.setMessage(e.getMessage());
				}
				return new ResponseEntity<ResponseBean>(response,headers,HttpStatus.OK);
			}
			//Page 2 : ProgramStructure Entries Add/Update End
			
			//Page 3 : Program Entries Add/Update Start
			@RequestMapping(value="/admin/programForm",method= {RequestMethod.GET})
			public ModelAndView programForm(HttpServletRequest request,HttpServletResponse response) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/programsFormPage");
				try{
					ProgramExamBean programsForm= new ProgramExamBean();
					ArrayList<ProgramExamBean> programsList=dashboardDao.getProgramListFromProgramCodeMaster();
					ArrayList<String> specializationTypeList=dashboardDao.getSpecializationtypeNameList();
					for(String type: specializationTypeList) {
						if("Marketing".equalsIgnoreCase(type)) {
							int index=specializationTypeList.indexOf(type);
							String Spectype=type.concat("(MG)");
							specializationTypeList.set(index, Spectype);
							break;
						}
					}
					logger.info("SpecialisationType List  {}  ",specializationTypeList);
					mav.addObject("specializationNameList",specializationTypeList);
					logger.info("Adding SpecializationList to The modelObject");
					mav.addObject("programsList",programsList);
					mav.addObject("programsForm",programsForm);
				 }catch(Exception e){
					
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Unable to load page");
				}
				return mav;
			
			}
			@RequestMapping(value="/admin/insertProgramFormMasterEntry",method= {RequestMethod.POST})
			public ModelAndView programFormDataInsert(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramExamBean programsForm)
				{
					if(!checkSession(request, response)){
						redirectToPortalApp(response);
						return null;
					}
					ModelAndView mav=new ModelAndView("mdm/programsFormPage");
					mav.addObject("programsForm",programsForm);
					try {
						String userId = (String)request.getSession().getAttribute("userId");
						programsForm.setCreatedBy(userId);
						programsForm.setLastModifiedBy(userId);
							ArrayList<ProgramExamBean> programsList=dashboardDao.getProgramListFromProgramCodeMaster();
							String programExistsInProgramTable= "false";
								for(ProgramExamBean bean: programsList){
									if(bean.getCode().equalsIgnoreCase(programsForm.getCode())){
										programExistsInProgramTable ="true";
									}
								}
								if(programExistsInProgramTable.equalsIgnoreCase("false")){
									if("Marketing(MG)".equalsIgnoreCase(programsForm.getSpecialization())) {
										programsForm.setSpecializationId("9");
										programsForm.setSpecializationType("Marketing");
									}else {
									String sId= dashboardDao.getSpecialisationIdFromName(programsForm.getSpecialization());
									programsForm.setSpecializationId(sId);
									}
									
									logger.info("Specialization Id   {}",programsForm.getSpecializationId());
									logger.info("Inserting program data in DB");
									HashMap<String,String> message = dashboardDao.insertIntoProgramTable(programsForm);
									
									if(message.containsKey("success")){
										request.setAttribute("success","true");
										request.setAttribute("successMessage",message.get("success"));
										//programsList=dashboardDao.getAllProgramsList();
									}else{
										request.setAttribute("error","true");
										request.setAttribute("errorMessage",message.get("error"));
									}
								}else{
									request.setAttribute("error","true");
									request.setAttribute("errorMessage","Program Already Exists");
								}
								ArrayList<String> specializationTypeList=dashboardDao.getSpecializationtypeNameList();
								for(String type: specializationTypeList) {
									if("Marketing".equalsIgnoreCase(type)) {
										int index=specializationTypeList.indexOf(type);
										String Spectype=type.concat("(MG)");
										specializationTypeList.set(index, Spectype);
										break;
									}
								}
								programsList=dashboardDao.getProgramListFromProgramCodeMaster();
								System.out.println("This is programsList "+ programsList);
								mav.addObject("specializationNameList",specializationTypeList);
								mav.addObject("programsList",programsList);		
						}

						catch(Exception e) {
							e.printStackTrace();
							request.setAttribute("error","true");
							request.setAttribute("errorMessage","Entries Not inserted Correctly");
						}
					return mav;
				}
			@RequestMapping(value = "/admin/updateProgramFormMasterEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateProgramMasterEntry(@RequestBody ProgramExamBean bean, HttpServletRequest request){
				
				String userId = (String)request.getSession().getAttribute("userId");
				bean.setLastModifiedBy(userId);
				HashMap<String, String> response = new  HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				HashMap<String,String> message = new HashMap<>();
				response.put(status, error);
			
				try {
					if("Marketing(MG)".equalsIgnoreCase(bean.getSpecializationType())) {
						bean.setSpecializationId("9");
						bean.setSpecializationType("Marketing");
						message = dashboardDao.updateProgramTableEntry(bean);
					}
					else {
						String sId= dashboardDao.getSpecialisationIdFromName(bean.getSpecializationType());
						bean.setSpecializationId(sId);
						message = dashboardDao.updateProgramTableEntry(bean);
						
					}
					if(message.containsKey("error")){
						return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
					}
					response.put(status, success);
					response.put(statusMessage, successUpdateMessage);
					
				} catch (Exception e) {
					response.put(statusMessage, "Error in updating the record. Error :- "+e.getMessage());
					
				}
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
			}
			@RequestMapping(value = "/admin/programNameBulkUploadForm", method = {RequestMethod.GET})
			public String programNameBulkUploadForm(HttpServletRequest request, HttpServletResponse response, Model m) {
				ProgramExamBean fileBean = new ProgramExamBean();
				m.addAttribute("fileBean",fileBean);
				return "mdm/programNameBulkUpload";
			}
			@RequestMapping(value = "/admin/programNameBulkUpload", method = {RequestMethod.POST})
			public ModelAndView programNameBulkUpload(ProgramExamBean fileBean, HttpServletRequest request, HttpServletResponse response, Model m){
				
				ModelAndView modelnView = new ModelAndView("mdm/programNameBulkUpload");
				
				try {
					String userId = (String)request.getSession().getAttribute("userId");
					ExcelHelper excelHelper = new ExcelHelper();
					ArrayList<List> resultList = excelHelper.readProgramNameExcel(fileBean, userId);
					
					List<ProgramExamBean> programNameList = (ArrayList<ProgramExamBean>)resultList.get(0);
					List<ProgramExamBean> errorBeanList = (ArrayList<ProgramExamBean>)resultList.get(1);
					List<ProgramExamBean> programNameListAlreadyExists= new ArrayList<ProgramExamBean>(); 
					fileBean = new ProgramExamBean();
					m.addAttribute("fileBean",fileBean);
				
					
					if(errorBeanList.size() > 0){
						request.setAttribute("errorBeanList", errorBeanList);
						return modelnView; 
					}
					ArrayList<String> programName = dashboardDao.getProgramNameListFromProgramCodeMaster();
					if(programNameList.size()>0){
						for(ProgramExamBean pb:programNameList){
							if(programName.contains(pb.getCode())){
								programNameListAlreadyExists.add(pb);
							}
						}
						programNameList.removeAll(programNameListAlreadyExists);
					}
					
					if(programNameList.size()>0){
						ArrayList<String> errorList = dashboardDao.batchInsertProgramNameEntries(programNameList);
						if(errorList.size() == 0){
							request.setAttribute("success","true");
							if(programNameListAlreadyExists.size()>0){
								request.setAttribute("successMessage",programNameList.size() +" rows out of "+ programNameList.size()+" inserted successfully."+programNameListAlreadyExists.size()+" duplicate program code found not inserted.");
							}else{
								request.setAttribute("successMessage",programNameList.size() +" rows out of "+ programNameList.size()+" inserted successfully.");
							}
							
						}else{
							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
						}
					}else if(programNameList.size()==0 && programNameListAlreadyExists.size()>0){
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", programNameListAlreadyExists.size() + " records were NOT inserted as the program codes already exists");
					}
					
					
				} catch(Exception e){
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in inserting rows.");
				}
		
				return modelnView;
			}
			//Page 3 : Program Entries Add/Update End
			
			//Page 4a : Consumer Program ProgramStructure Entries Add/Update Start
			@RequestMapping(value="/admin/consumerProgramStructureMappingForm",method= {RequestMethod.GET})
			public ModelAndView consumerProgramStructureMappingForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ConsumerProgramStructureExam ConsumerProgramStructure) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/consumerProgramStructureMappingForm");
				try{
					ArrayList<ConsumerProgramStructureExam> programData = dashboardDao.getProgramList();
					ArrayList<ProgramStructureBean> programStructureData = dashboardDao.getProgramStructureList();
					ArrayList<ConsumerType> consumerTypeListData = dashboardDao.getConsumerTypeList();
					request.getSession().setAttribute("programList",programData);
					request.getSession().setAttribute("programStructureList",programStructureData);
					request.getSession().setAttribute("consumerTypeList",consumerTypeListData);
					ArrayList<ConsumerProgramStructureExam> consumerProgramStructureList=dashboardDao.getAllConsumerProgramStructureMappingList();
					mav.addObject("consumerProgramStructureMappingList",consumerProgramStructureList);
					mav.addObject("ConsumerProgramStructure",new ConsumerProgramStructureExam());
					String userId = (String)request.getSession().getAttribute("userId");
					////////////////////////
					String consumerTypeList = "{";
					for (ConsumerType consumerTypeList1 : consumerTypeListData) {
						consumerTypeList = consumerTypeList + "\"" +consumerTypeList1.getId() + "\":\"" + consumerTypeList1.getName() + "\",";
					}
					consumerTypeList = consumerTypeList.substring(0,consumerTypeList.length() - 1);
					consumerTypeList = consumerTypeList + "}";
					///////////////////////
					String programStructureList = "{";
					for(ProgramStructureBean programStructure : programStructureData) {
						programStructureList = programStructureList + "\"" +programStructure.getId() + "\":\"" + programStructure.getProgram_structure() + "\",";
					}
					programStructureList = programStructureList.substring(0,programStructureList.length() - 1);
					programStructureList = programStructureList + "}";
					/////////////////////////
					String programDataList = "{";
					for(ConsumerProgramStructureExam program : programData) {
						programDataList = programDataList + "\"" +program.getId() + "\":\"" + program.getCode() + "\",";
					}
					programDataList = programDataList.substring(0,programDataList.length() - 1);
					programDataList = programDataList + "}";
					/////////////////////
					mav.addObject("programDataList_tmp",programDataList);
					mav.addObject("consumerTypeList_tmp",consumerTypeList);
					mav.addObject("programStructureList_tmp",programStructureList);
					mav.addObject("userId",userId);
				}catch(Exception e){
					
				}
				return mav;
			}
			@RequestMapping(value="/admin/consumerProgramStructureMappingData",method= {RequestMethod.POST})
			public ModelAndView consumerProgramStructureMappingFormData(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ConsumerProgramStructureExam consumerProgramStructure)
			{
				ModelAndView mav=new ModelAndView("mdm/consumerProgramStructureMappingForm");
				
				String userId = (String)request.getSession().getAttribute("userId");
				ArrayList<ConsumerProgramStructureExam> programData=new ArrayList<ConsumerProgramStructureExam>();
				ArrayList<ProgramStructureBean> programStructureData=new ArrayList<ProgramStructureBean>();
				ArrayList<ConsumerType> consumerTypeListData=new ArrayList<ConsumerType>();
				ArrayList<ConsumerProgramStructureExam> consumerProgramStructureList=new ArrayList<ConsumerProgramStructureExam>();
				
				String programDataList="";
				String programStructureList="";
				String consumerTypeList="";
				
				try {
					consumerProgramStructure.setCreatedBy((String)request.getSession().getAttribute("userId"));
					consumerProgramStructure.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
				if(consumerProgramStructure.getConsumerTypeId()!=null||consumerProgramStructure.getProgramId()!=null||consumerProgramStructure.getProgramStructureId()!=null ) {
					if(dashboardDao.getConsumerProgramStructure(consumerProgramStructure)==0) {	
						String result  = dashboardDao.insertIntoConsumerProgramStructureTable(consumerProgramStructure);
						if(result.equalsIgnoreCase("true")) {
							request.setAttribute("success","true");
							request.setAttribute("successMessage","Entries Inserted Successfully");			
						}	
						else {
							request.setAttribute("error","true");
							request.setAttribute("errorMessage","Entries Not inserted Correctly");
						}		
					}
					else {
						request.setAttribute("error","true");
						request.setAttribute("errorMessage","Already Exist.");
					}
				}else {
				}
			
				programData = dashboardDao.getProgramList();
				programStructureData = dashboardDao.getProgramStructureList();
				consumerTypeListData = dashboardDao.getConsumerTypeList();
				
				
				consumerProgramStructureList=dashboardDao.getAllConsumerProgramStructureMappingList();
				System.out.println("consumerProgramStructureListBean>>"+consumerProgramStructureList.get(1));
				////////////////////////
				consumerTypeList = "{";
				for (ConsumerType consumerTypeList1 : consumerTypeListData) {
					consumerTypeList = consumerTypeList + "\"" +consumerTypeList1.getId() + "\":\"" + consumerTypeList1.getName() + "\",";
				}
				consumerTypeList = consumerTypeList.substring(0,consumerTypeList.length() - 1);
				consumerTypeList = consumerTypeList + "}";
				///////////////////////
				programStructureList = "{";
				for(ProgramStructureBean programStructure : programStructureData) {
					programStructureList = programStructureList + "\"" +programStructure.getId() + "\":\"" + programStructure.getProgram_structure() + "\",";
				}
				programStructureList = programStructureList.substring(0,programStructureList.length() - 1);
				programStructureList = programStructureList + "}";
				/////////////////////////
				programDataList = "{";
				for(ConsumerProgramStructureExam program : programData) {
					programDataList = programDataList + "\"" +program.getId() + "\":\"" + program.getCode() + "\",";
				}
				programDataList = programDataList.substring(0,programDataList.length() - 1);
				programDataList = programDataList + "}";
		
				}
				catch(Exception e) {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Entries Not inserted Correctly");
				}
				
				request.getSession().setAttribute("programList",programData);
				request.getSession().setAttribute("programStructureList",programStructureData);
				request.getSession().setAttribute("consumerTypeList",consumerTypeListData);
				
				mav.addObject("consumerProgramStructureMappingList",consumerProgramStructureList);
				mav.addObject("ConsumerProgramStructure",new ConsumerProgramStructureExam());
				mav.addObject("programDataList_tmp",programDataList);
				mav.addObject("consumerTypeList_tmp",consumerTypeList);
				mav.addObject("programStructureList_tmp",programStructureList);
				mav.addObject("userId",userId);
				return mav;
			}
			@RequestMapping(value="/admin/updateConsumerProgramStructureMapping",method= {RequestMethod.POST})
			public ResponseEntity<ResponseBean> updateConsumerProgramStructureMapping(HttpServletRequest request,@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
				ResponseBean response = new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					//Commented by Saurabh as Logic moved to service layer
					/*ConsumerProgramStructure.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
					if(dashboardDao.getConsumerProgramStructure(ConsumerProgramStructure)==0) {	
						String result  = dashboardDao.updateConsumerProgramStructureMappingList(ConsumerProgramStructure);
						if(result.equalsIgnoreCase("true")) {
								response.setStatus("Success");
								response.setMessage("Successfully data updated");		
						}else {
								response.setStatus("Error");
								response.setMessage(result);
							 }		
						}
						else {
							dashboardDao.updateHasLiveSessionAccessFlag(ConsumerProgramStructure.getHasLiveSessionApplicable(), ConsumerProgramStructure.getId());
							response.setStatus("Error");
							response.setMessage("ConsumerProgramStructureId already Exist.");
						}*/
					ConsumerProgramStructureExam bean=configurationService.updateMasterKeyDetails(consumerProgramStructure);
					response.setStatus(bean.getStatus());
					response.setMessage(bean.getMessage());
					
				}catch (Exception e) {
					response.setStatus("Error");
					response.setMessage(e.getMessage());
				}
				return new ResponseEntity<ResponseBean>(response,headers,HttpStatus.OK);
			}
			//Page 4a : Consumer Program ProgramStructure Entries Add/Update End
			
			//Page 4b : Consumer Program ProgramStructure Entries Add/Update Start
			@RequestMapping(value = "/admin/insertIntoMasterKeyTable",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			/*@ get all master keys from students db
			 *@ get all master key from cps db
			 *@ for every student key if master key is missing the insert master key in cps
			 *@ update student db for missing master keys.*/		
			public void insertIntoMasterKeyTable(){
				ArrayList<String>  studentsKeyList = dashboardDao.getAllMasterKeyFromStudents();
				ArrayList<String>  masterKeyList = dashboardDao.getAllMasterKeyFromCPP();
				ArrayList<String>  masterKeyListInserted = new ArrayList<String>();
				ArrayList<String>  masterKeyListFailedToInsert = new ArrayList<String>();
				ArrayList<String>  masterKeyListAlreadyExists = new ArrayList<String>();
				for(String key : studentsKeyList){
					String[] values = key.split("\\|",0);
					String consumer = values[0];
					String program =  values[1];
					String programStructure = values[2];
					String c_id = dashboardDao.getConsumerTypeIdFromName(consumer);
					String p_id = dashboardDao.getProgramIdFromCode(program);
					String ps_id = dashboardDao.getProgramStructureIdFromName(programStructure);
					if(!masterKeyList.contains(c_id+"|"+p_id+"|"+ps_id)){
						if(!StringUtils.isBlank(c_id) && !StringUtils.isBlank(p_id) && !StringUtils.isBlank(ps_id)) {
							ConsumerProgramStructureExam bean= new ConsumerProgramStructureExam();
							bean.setConsumerTypeId(c_id);
							bean.setProgramId(p_id);
							bean.setProgramStructureId(ps_id);
							bean.setLastModifiedBy("System");
							bean.setCreatedBy("System");
								String result ="";
								try{
									result = dashboardDao.insertIntoConsumerProgramStructureTable(bean);
									
								}catch(Exception e){
									
								}
								if(result.equalsIgnoreCase("true")) {
									masterKeyListInserted.add(c_id+"|"+p_id+"|"+ps_id);
								}	
								else{
									masterKeyListFailedToInsert.add(c_id+"|"+p_id+"|"+ps_id);
								}		
							
						}else {
						}
					}else{
						masterKeyListAlreadyExists.add(c_id+"|"+p_id+"|"+ps_id);
					}
				}
				try{
				dashboardDao.updateStudentsConsumerProgramStructureId();
				}catch(Exception e){
					
				}
			}
			//Page 4b : Consumer Program ProgramStructure Entries Add/Update End
			
			//Page 5 : Program Details Entries Add/Update Start
			@RequestMapping(value="/admin/programDetailsForm",method= {RequestMethod.GET})
			public ModelAndView programDetailsForm(HttpServletRequest request,HttpServletResponse response) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/programsForm");
				mav.addObject("programsForm",new ProgramsBean());
					try{
						mav.addObject("progStructListFromProgramMaster",progStructListFromProgramMaster);	
						ArrayList<ProgramsBean> programsList=dashboardDao.getAllProgramsList();
						ArrayList<ConsumerProgramStructureExam> consumerType = asignmentsDAO.getConsumerTypeList();
						ArrayList<String> programTypeList=dashboardDao.getProgramTypeNameList();
						request.getSession().setAttribute("programTypeList", programTypeList);
						mav.addObject("programsList",programsList);
						mav.addObject("programTypeList",programTypeList);
						mav.addObject("consumerType",consumerType);
					}catch(Exception e){
					
					}
				return mav;
			}
			@RequestMapping(value="/admin/programDetails",method= {RequestMethod.POST})
			public ModelAndView programDetails(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramsBean programsForm) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/programsForm");
				mav.addObject("programsForm",programsForm);
					try{
						ArrayList<String> programTypeList = (ArrayList<String>)request.getSession().getAttribute("programTypeList");
						 	ArrayList<ProgramsBean> programsList=dashboardDao.getAllProgramsList();
							ArrayList<ConsumerProgramStructureExam> consumerType = asignmentsDAO.getConsumerTypeList();
							String programCode = dashboardDao.getProgramCodeFromId(Integer.valueOf(programsForm.getProgram()));
							String programStructure = dashboardDao.getProgramStructureFromId(Integer.valueOf(programsForm.getProgramStructure()));
							String consumerTypeName = dashboardDao.getConsumerTypeFromId(Integer.valueOf(programsForm.getConsumerType()));
							String programName = dashboardDao.getProgramNameFromId(Integer.valueOf(programsForm.getProgram()));
							programsForm.setConsumerProgramStructureId(dashboardDao.getConsumerProgramStructureId(consumerTypeName, programStructure, programCode));
							programsForm.setProgram(programCode);
							programsForm.setProgramStructure(programStructure);
							programsForm.setProgramname(programName);
							if(!StringUtils.isBlank(programsForm.getActive()) && !StringUtils.isBlank(programsForm.getProgram())) {
								for(ProgramsBean bean: programsList){
									if(bean.getConsumerProgramStructureId().equalsIgnoreCase(programsForm.getConsumerProgramStructureId())){
										request.setAttribute("error","true");
										request.setAttribute("errorMessage","Program already exists !");
										mav.addObject("programsList",programsList);
										mav.addObject("consumerType",consumerType);
										mav.addObject("programTypeList",programTypeList);
										return mav;
									}
								}
							}
							if("Y".equalsIgnoreCase(programsForm.getActive())){
								if(StringUtils.isBlank(programsForm.getActive()) || StringUtils.isBlank(programsForm.getProgram())
									||	StringUtils.isBlank(programsForm.getProgramname()) || StringUtils.isBlank(programsForm.getProgramcode())
									||	StringUtils.isBlank(programsForm.getProgramDuration()) || StringUtils.isBlank(programsForm.getProgramDurationUnit())
									||	StringUtils.isBlank(programsForm.getProgramType()) || StringUtils.isBlank(programsForm.getNoOfSubjectsToClear())
									||	StringUtils.isBlank(programsForm.getNoOfSubjectsToClearLateral()) || StringUtils.isBlank(programsForm.getProgramStructure())
									||	StringUtils.isBlank(programsForm.getExamDurationInMinutes()) || StringUtils.isBlank(programsForm.getNoOfSemesters())
									||	StringUtils.isBlank(programsForm.getNoOfSubjectsToClearSem())){
									request.setAttribute("error","true");
									request.setAttribute("errorMessage","Program not cannot be made active as fields are blank. \n Kindly check all fields before activating program");
									 mav.addObject("programsList",programsList);
									 mav.addObject("consumerType",consumerType);
									 mav.addObject("programTypeList",programTypeList);
									 return mav;
								}
							}
							String userId = (String)request.getSession().getAttribute("userId");
							programsForm.setCreatedBy(userId);
							programsForm.setLastModifiedBy(userId);
							HashMap<String,String> message = dashboardDao.insertPrograms(programsForm);
							if(message.containsKey("success")){
								programsList=dashboardDao.getAllProgramsList();
								request.setAttribute("success","true");
								request.setAttribute("successMessage",message.get("success"));
							}else{
								request.setAttribute("error","true");
								request.setAttribute("errorMessage",message.get("error"));
							}
							mav.addObject("progStructListFromProgramMaster",progStructListFromProgramMaster);	
							mav.addObject("programsList",programsList);
							mav.addObject("consumerType",consumerType);
							mav.addObject("programTypeList",programTypeList);
							
					}catch(Exception e){
					
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Program not Inserted");
					 
					}
				return mav;
			}	
			@RequestMapping(value = "/admin/updateProgramEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateProgramEntry(@RequestBody ProgramsBean programsForm,HttpServletRequest request){

				HashMap<String, String> response = new  HashMap<String, String>();
				response.put(status, error);
			
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
				
					if("Y".equalsIgnoreCase(programsForm.getActive())){
						
						if(StringUtils.isBlank(programsForm.getActive()) || StringUtils.isBlank(programsForm.getProgram())
							||	StringUtils.isBlank(programsForm.getProgramname()) || StringUtils.isBlank(programsForm.getProgramcode())
							||	StringUtils.isBlank(programsForm.getProgramDuration()) || StringUtils.isBlank(programsForm.getProgramDurationUnit())
							||	StringUtils.isBlank(programsForm.getProgramType()) || StringUtils.isBlank(programsForm.getNoOfSubjectsToClear())
							||	StringUtils.isBlank(programsForm.getNoOfSubjectsToClearLateral()) || StringUtils.isBlank(programsForm.getProgramStructure())
							||	StringUtils.isBlank(programsForm.getExamDurationInMinutes()) || StringUtils.isBlank(programsForm.getNoOfSemesters())
							||	StringUtils.isBlank(programsForm.getNoOfSubjectsToClearSem())){
							response.put(statusMessage, "Error in getting details");
							return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
						}
					}
					String userId = (String)request.getSession().getAttribute("userId");
					programsForm.setLastModifiedBy(userId);
					
					HashMap<String,String> message = dashboardDao.updateProgramsEntry(programsForm);
					if(message.containsKey("error")){
						//response.put("Status", "Fail");
						return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
					}
					response.put(status, success);
					response.put(statusMessage, successUpdateMessage);
					
				} catch (Exception e) {
					response.put(statusMessage, "Error in updating the record. Error :- "+e.getMessage());
				//	response.put("Status", "Fail");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
				
			}
			@RequestMapping(value = "/admin/programDetailsBulkUploadForm", method = {RequestMethod.GET})
			public String programDetailsBulkUploadForm(HttpServletRequest request, HttpServletResponse response, Model m) {
				ProgramExamBean fileBean = new ProgramExamBean();
				m.addAttribute("fileBean",fileBean);
				return "mdm/programDetailsBulkUpload";
			}
			//Page 5 : Program Details Entries Add/Update End
			
			//Page 6 : Subject Entries Add/Update Start
			@RequestMapping(value="/admin/subjectForm",method= {RequestMethod.GET,RequestMethod.POST})
			public ModelAndView subjectForm(HttpServletRequest request,HttpServletResponse response) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mv = new ModelAndView("mdm/subjectForm");
				mv.addObject("subject", new SubjectBean());
				mv.addObject("subjectsList", dashboardDao.getAllSubject());
				return mv;
			}
			@RequestMapping(value = "/admin/subjectFormData",  method = RequestMethod.POST )
			public ModelAndView subjectFormData(HttpServletRequest request,HttpServletResponse response,@ModelAttribute SubjectBean subject) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mv = new ModelAndView("mdm/subjectForm");
				 
				if(subject.getSubjectname()==null||subject.getSubjectbbcode()==null||subject.getCommonSubject()==null) {
					
				}else {
					if(dashboardDao.getCountSubjects(subject)==0) {
				
						String daoresponse = dashboardDao.insertSubject(subject);
						
						if(daoresponse.indexOf("Error") != -1) {
							mv.addObject("responseType", "error");
							mv.addObject("message", "Failed to create new subject,Try Again");
						}else {
							mv.addObject("responseType", "success");
							mv.addObject("message", "Successfully Subject Created");
						}
					}
					else {
						mv.addObject("responseType", "error");
						mv.addObject("message", "Subject Already Exists.");	
					}
				
				}
				
				mv.addObject("subject", new SubjectBean());
				mv.addObject("subjectsList", dashboardDao.getAllSubject());
				return mv;
			}
			@RequestMapping(value = "/admin/updateSubjectEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateSubjectEntry(@RequestBody SubjectBean bean){
				
				HashMap<String, String> response = new  HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					dashboardDao.updateSubject(bean);
					response.put("Status", "Success");
		
				} catch (Exception e) {
					
					response.put("Status", "Fail");
				}
				
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
				
			}
			//Page 6 : Subject Entries Add/Update End
			
			//Page 7 : ProgramSubject Entries Add/Update Start
			@RequestMapping(value="/admin/programSubjectForm",method= {RequestMethod.GET})
			public ModelAndView programSubjectForm(HttpServletRequest request,HttpServletResponse response
					,@ModelAttribute ProgramSubjectMappingExamBean programSubjectMappingBean) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/programSubjectForm"); 
				try{
					AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
					/*request.getSession().setAttribute("programListFromProgramMaster",programListFromProgramMaster);
					request.getSession().setAttribute("progStructListFromProgramMaster",progStructListFromProgramMaster);*/
					request.getSession().setAttribute("semesterList",semesterList);
					request.getSession().setAttribute("subjectList",dao.getSubjectsList());
					request.getSession().setAttribute("consumerType",dao.getConsumerTypeList());
					ArrayList<ProgramSubjectMappingExamBean> programSubjectList=dashboardDao.getAllProgramSubjectList();
					request.getSession().setAttribute("programSubjectList",programSubjectList);
					mav.addObject("programSubjectList",programSubjectList);
					mav.addObject("programSubjectMappingBean",new ProgramSubjectMappingExamBean());
					String userId = (String)request.getSession().getAttribute("userId");
					mav.addObject("userId",userId);
					ArrayList<String> specializationTypeList=dashboardDao.getSpecializationtypeNameList();
					request.getSession().setAttribute("specializationTypeList", specializationTypeList);
					mav.addObject("specializationTypeList",specializationTypeList);
				}catch(Exception e){
					
				}
				return mav;
			}
			@RequestMapping(value="/admin/programSubjectFormData",method= {RequestMethod.POST})
			public ModelAndView programSubjectFormData(HttpServletRequest request,HttpServletResponse response,
					@ModelAttribute ProgramSubjectMappingExamBean programSubjectMappingBean)
			{
				ModelAndView mav=new ModelAndView("mdm/programSubjectForm");
				ArrayList<String> specializationTypeList = (ArrayList<String>)request.getSession().getAttribute("specializationTypeList");
				try {
						programSubjectMappingBean.setCreatedBy((String)request.getSession().getAttribute("userId"));
						programSubjectMappingBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
						
						ArrayList<ProgramSubjectMappingExamBean> programSubjectList=dashboardDao.getAllProgramSubjectList();

						String doesNotexist ="true";
						if(programSubjectMappingBean!=null ) {
								String key = programSubjectMappingBean.getProgram()+"|"+programSubjectMappingBean.getSem()+"|"+programSubjectMappingBean.getSubject()
										+"|"+programSubjectMappingBean.getPrgmStructApplicable()+"|"+programSubjectMappingBean.getConsumerType();
							    for(ProgramSubjectMappingExamBean bean : programSubjectList){
							    	String str = bean.getProgram()+"|"+bean.getSem()+"|"+bean.getSubject()+"|"+bean.getPrgmStructApplicable()+"|"+bean.getConsumerType();
							    	if(str.equalsIgnoreCase(key)){
							    		doesNotexist="false";
							    		request.setAttribute("error","true");
										request.setAttribute("errorMessage","Entries Alreay Exists");
							    	}
							    }
							    
							    if(programSubjectMappingBean.getActive().equalsIgnoreCase("Y")){
							    	if(StringUtils.isBlank(programSubjectMappingBean.getConsumerType()) || StringUtils.isBlank(programSubjectMappingBean.getProgram()) ||
							    			StringUtils.isBlank(programSubjectMappingBean.getSubject()) || StringUtils.isBlank(programSubjectMappingBean.getSem()) ||
							    			StringUtils.isBlank(programSubjectMappingBean.getPrgmStructApplicable()) || StringUtils.isBlank(programSubjectMappingBean.getActive()) ||
							    			StringUtils.isBlank(String.valueOf(programSubjectMappingBean.getPassScore())) || StringUtils.isBlank(programSubjectMappingBean.getHasAssignment()) ||
							    			StringUtils.isBlank(programSubjectMappingBean.getAssignmentNeededBeforeWritten()) || StringUtils.isBlank(programSubjectMappingBean.getWrittenScoreModel()) ||
							    			StringUtils.isBlank(programSubjectMappingBean.getAssignmentScoreModel()) || StringUtils.isBlank(programSubjectMappingBean.getCreateCaseForQuery()) ||
							    			StringUtils.isBlank(programSubjectMappingBean.getAssignQueryToFaculty()) || StringUtils.isBlank(programSubjectMappingBean.getIsGraceApplicable()) ||
							    			StringUtils.isBlank(String.valueOf(programSubjectMappingBean.getMaxGraceMarks())) || StringUtils.isBlank(programSubjectMappingBean.getHasTest()) ||
							    			StringUtils.isBlank(String.valueOf(programSubjectMappingBean.getSifySubjectCode())) || StringUtils.isBlank(String.valueOf(programSubjectMappingBean.getSpecializationName()))
							    			|| StringUtils.isBlank(String.valueOf(programSubjectMappingBean.getSubjectCredits()))){
							    		
							    		request.setAttribute("error","true");
										request.setAttribute("errorMessage","Cannot be made active as fields are missing value ");
										doesNotexist="false";
							    	}
							    } 
							    
							    
							    
							    if(doesNotexist.equalsIgnoreCase("true")){
									String status =	dashboardDao.insertDataInProgramtable(programSubjectMappingBean);
									if(status.equalsIgnoreCase("Success")) {
										request.setAttribute("success","true");
										request.setAttribute("successMessage","Entries Inserted Successfully");
									}else {
										request.setAttribute("error","true");
										request.setAttribute("errorMessage","Entries Not inserted Correctly");
									}
								}
								
								
							}
						AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
						programSubjectList=dashboardDao.getAllProgramSubjectList();
						request.getSession().getAttribute("programSubjectList");
						mav.addObject("programSubjectList",programSubjectList);
						request.setAttribute("active", request.getParameter("active"));
						request.getSession().setAttribute("semesterList",semesterList);
						mav.addObject("programSubjectMappingBean",programSubjectMappingBean);
						request.getSession().setAttribute("subjectList",dao.getSubjectsList());
						request.getSession().setAttribute("consumerType",dao.getConsumerTypeList());
						request.setAttribute("hasAssignment", request.getParameter("hasAssignment"));
						request.setAttribute("assignmentNeededBeforeWritten", request.getParameter("assignmentNeededBeforeWritten"));
						request.setAttribute("writtenScoreModel", request.getParameter("writtenScoreModel"));
						request.setAttribute("assignmentScoreModel", request.getParameter("assignmentScoreModel"));
				}
				catch(Exception e) {
					
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Entries Not inserted Correctly");
				}
				mav.addObject("specializationTypeList",specializationTypeList);
				return mav;
			}
			@RequestMapping(value = "/admin/updateProgramSubjectEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateProgramSubjectEntry(@RequestBody ProgramSubjectMappingExamBean bean){
				
				HashMap<String, String> response = new  HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					
					if(bean!=null ) {
						 if(bean.getActive().equalsIgnoreCase("Y")){
						    	if(StringUtils.isBlank(bean.getConsumerType()) || StringUtils.isBlank(bean.getProgram()) ||
						    			StringUtils.isBlank(bean.getSubject()) || StringUtils.isBlank(bean.getSem()) ||
						    			StringUtils.isBlank(bean.getPrgmStructApplicable()) || StringUtils.isBlank(bean.getActive()) ||
						    			StringUtils.isBlank(String.valueOf(bean.getPassScore())) || StringUtils.isBlank(bean.getHasAssignment()) ||
						    			StringUtils.isBlank(bean.getAssignmentNeededBeforeWritten()) || StringUtils.isBlank(bean.getWrittenScoreModel()) ||
						    			StringUtils.isBlank(bean.getAssignmentScoreModel()) || StringUtils.isBlank(bean.getCreateCaseForQuery()) ||
						    			StringUtils.isBlank(bean.getAssignQueryToFaculty()) || StringUtils.isBlank(bean.getIsGraceApplicable()) ||
						    			StringUtils.isBlank(String.valueOf(bean.getMaxGraceMarks())) || StringUtils.isBlank(bean.getHasTest()) ||
						    			StringUtils.isBlank(String.valueOf(bean.getSifySubjectCode())) ||  StringUtils.isBlank(String.valueOf(bean.getStudentType()))
						    			|| StringUtils.isBlank(String.valueOf(bean.getSubjectCredits()))){
						    		
						    		response.put("Status", "Error");
						    		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
						    	}
						    }
						String status =	dashboardDao.updateDataInProgramtable(bean);
							
							if(status.equalsIgnoreCase("Success")) {
								response.put("Status", "Success");
							}else {
								response.put("Status", "Error");
							}
						}
					/*dashboardDao.updateDataInProgramtable(bean);
					response.put("Status", "Success");*/
		
				} catch (Exception e) {
					
					response.put("Status", "Fail");
				}
				
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
				
			}				
			@RequestMapping(value="/admin/downloadMDM",method={RequestMethod.GET,RequestMethod.POST})
			public ModelAndView downloadMDM(HttpServletRequest request, HttpServletResponse response){
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
		
				ArrayList<ProgramSubjectMappingExamBean> programSubjectList=dashboardDao.getAllProgramSubjectList();			
				return new ModelAndView("downloadMDMReport","programSubjectList",programSubjectList);
			}		
			@RequestMapping(value = "/admin/getValueByProgramStructure",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getDataByProgramStructure(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
				//Fill Programs , Subject DropDown Data Based On ConsumerType Selected	
				ResponseBean response = (ResponseBean) new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					ArrayList<ConsumerProgramStructureExam> programData = dashboardDao.getProgramByConsumerTypeAndPrgmStructure(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramStructureId());
					response.setStatus("success");
					response.setProgramsData(programData);
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
				return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);	
			}
			@RequestMapping(value = "/admin/getValueByConsumerType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getDataByConsumerType(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
				//fetching data based on consumer Type
				ResponseBean response = (ResponseBean) new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					ArrayList<ConsumerProgramStructureExam> programStructureData = dashboardDao.getProgramStructureByConsumerType(consumerProgramStructure.getId());
					ArrayList<ConsumerProgramStructureExam> programData = dashboardDao.getProgramByConsumerType(consumerProgramStructure.getId());
					response.setStatus("success");
					response.setProgramStructureData(programStructureData);
					response.setProgramsData(programData);
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
				return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);	
			}	
			//Page 7 : ProgramSubject Entries Add/Update End
			
			// Program Details Master Key Entries Add/Update Start
			@RequestMapping(value = "/admin/met1",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public void met1(){
				ArrayList<ProgramsBean> studentsProgramList = dashboardDao.getAllProgramConsumerProgramStructureDetails();
				HashMap<String,ArrayList<String>> studentsMap = new HashMap<String,ArrayList<String>>();
				HashMap<String,ProgramsBean> programsMap = new HashMap<String,ProgramsBean>();
				for(ProgramsBean pb:studentsProgramList){
					String key = pb.getProgram()+"|"+pb.getProgramStructure();
					if(studentsMap.containsKey(key)){
						studentsMap.get(key).add(pb.getConsumerType());
					}
					if(!studentsMap.containsKey(key)){
						ArrayList<String> consumerType = new ArrayList<String>();
						consumerType.add(pb.getConsumerType());
						studentsMap.put(key, consumerType);
					}
				}
				
				ArrayList<ProgramsBean> programsList = dashboardDao.getAllProgramsList();
				for(ProgramsBean pb:programsList){
					String key = pb.getProgram()+"|"+pb.getProgramStructure();
					if(!programsMap.containsKey(key)){
						programsMap.put(key, pb);
					}
				}
				
				int count = 0;
				for(Entry<String,ArrayList<String>> ent: studentsMap.entrySet()){
					String studentKey= ent.getKey();
					String[] values = studentKey.split("\\|",0);
					String Program = values[0];
					String ProgramStructure =  values[1];
					for(String arr : ent.getValue()){
						String consumer = arr;
						String cps_id = dashboardDao.getConsumerProgramStructureId(consumer, ProgramStructure, Program);
						if(programsMap.containsKey(studentKey)){
							ProgramsBean b = programsMap.get(studentKey);
							if(!b.getConsumerProgramStructureId().equalsIgnoreCase(cps_id)){
								if(b.getConsumerProgramStructureId().equalsIgnoreCase("0")){
									count++;
									b.setConsumerProgramStructureId(cps_id);
									dashboardDao.updateProgramsDetailsEntry(b);
								}else{
									b.setConsumerProgramStructureId(cps_id);
									dashboardDao.insertPrograms(b);
								}
							}
						}
					}
				}
			}
			// Program Details Master Key Entries Add/Update End
			
			//Page 8 : ProgramType Entries Add/Update Start
			@RequestMapping(value="/admin/programTypeForm",method= {RequestMethod.GET})
			public ModelAndView programTypeForm(HttpServletRequest request,HttpServletResponse response) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/programTypeMDM");
				try{
					ArrayList<ProgramExamBean> programTypeList=dashboardDao.getProgramTypeList();
					mav.addObject("programTypeList",programTypeList);
					mav.addObject("programBean",new ProgramExamBean());
				 }catch(Exception e){
					
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Unable to load page");
				}
				return mav;
			
			}
			@RequestMapping(value="/admin/insertProgramType",method= {RequestMethod.POST})
			public ModelAndView insertProgramType(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramExamBean programBean)
				{
					if(!checkSession(request, response)){
						redirectToPortalApp(response);
						return null;
					}
					ModelAndView mav=new ModelAndView("mdm/programTypeMDM");
					mav.addObject("programBean",programBean);
					try {
							ArrayList<ProgramExamBean> programTypeList=dashboardDao.getProgramTypeList();
							String programTypeExists= "false";
								for(ProgramExamBean bean: programTypeList){
									if(bean.getProgramType().equalsIgnoreCase(programBean.getProgramType())){
										programTypeExists ="true";
									}
								}
								if(programTypeExists.equalsIgnoreCase("false")){
									HashMap<String,String> message = dashboardDao.insertIntoProgramTypeTable(programBean);
									if(message.containsKey("success")){
										request.setAttribute("success","true");
										request.setAttribute("successMessage",message.get("success"));
									}else{
										request.setAttribute("error","true");
										request.setAttribute("errorMessage",message.get("error"));
									}
								}else{
									request.setAttribute("error","true");
									request.setAttribute("errorMessage","Program Already Exists");
								}
								programTypeList=dashboardDao.getProgramTypeList();
								mav.addObject("programTypeList",programTypeList);		
						}
						catch(Exception e) {
							
							request.setAttribute("error","true");
							request.setAttribute("errorMessage","Entries Not inserted Correctly");
						}
					return mav;
				}
			@RequestMapping(value = "/admin/updateProgramType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateProgramType(@RequestBody ProgramExamBean bean){
				HashMap<String, String> response = new  HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					HashMap<String,String> message = dashboardDao.updateProgramTypeEntry(bean);
					if(message.containsKey("error")){
						response.put("Status", "Fail");
						return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
					}
					response.put("Status", "Success");
				} catch (Exception e) {
					
					response.put("Status", "Fail");
				}
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
			}
			//Page 8 : ProgramType Entries Add/Update End
			
			//Page 9 : Specialization Entries Add/Update Start
			@RequestMapping(value="/admin/specializationTypeForm",method= {RequestMethod.GET})
			public ModelAndView specializationTypeForm(HttpServletRequest request,HttpServletResponse response) {
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				ModelAndView mav=new ModelAndView("mdm/specializationTypeMDM");
				try{
					ArrayList<ProgramExamBean> programTypeList=dashboardDao.getSpecializationtypeList();
					mav.addObject("programTypeList",programTypeList);
					mav.addObject("programBean",new ProgramExamBean());
				 }catch(Exception e){
					
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Unable to load page");
				}
				return mav;
			
			}
			@RequestMapping(value="/admin/insertSpecializationType",method= {RequestMethod.POST})
			public ModelAndView insertSpecializationType(HttpServletRequest request,HttpServletResponse response,@ModelAttribute ProgramExamBean programBean)
				{
					if(!checkSession(request, response)){
						redirectToPortalApp(response);
						return null;
					}
					ModelAndView mav=new ModelAndView("mdm/specializationTypeMDM");
					mav.addObject("programBean",programBean);
					try {
							ArrayList<ProgramExamBean> programTypeList=dashboardDao.getSpecializationtypeList();
							String programTypeExists= "false";
								for(ProgramExamBean bean: programTypeList){
									if(bean.getSpecializationType().equalsIgnoreCase(programBean.getSpecializationType())){
										programTypeExists ="true";
									}
								}
								if(programTypeExists.equalsIgnoreCase("false")){
									HashMap<String,String> message = dashboardDao.insertIntoSpecializationtypeTable(programBean);
									if(message.containsKey("success")){
										request.setAttribute("success","true");
										request.setAttribute("successMessage",message.get("success"));
									}else{
										request.setAttribute("error","true");
										request.setAttribute("errorMessage",message.get("error"));
									}
								}else{
									request.setAttribute("error","true");
									request.setAttribute("errorMessage","Program Already Exists");
								}
								programTypeList=dashboardDao.getSpecializationtypeList();
								mav.addObject("programTypeList",programTypeList);		
						}
						catch(Exception e) {
							
							request.setAttribute("error","true");
							request.setAttribute("errorMessage","Entries Not inserted Correctly");
						}
					return mav;
				}
			@RequestMapping(value = "/admin/updateSpecializationType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<HashMap<String, String>> updateSpecializationType(@RequestBody ProgramExamBean bean){
				HashMap<String, String> response = new  HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					HashMap<String,String> message = dashboardDao.updateSpecializationTypeEntry(bean);
					if(message.containsKey("error")){
						response.put("Status", "Fail");
						return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
					}
					response.put("Status", "Success");
				} catch (Exception e) {
					
					response.put("Status", "Fail");
				}
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
			}
			//Page 9 : ProgramType Entries Add/Update End
			
		
			//Fill Programs , Subject DropDown Data Based On ConsumerType Selected
            @RequestMapping(value = "/admin/addSubjectDateForm", method = {RequestMethod.GET, RequestMethod.POST})
            public String addSubjectDateForm(HttpServletRequest request, HttpServletResponse response, Model m) {

                AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
                ProgramSubjectMappingExamBean  studentConfig = new ProgramSubjectMappingExamBean();
                ArrayList<StudentSubjectConfigExamBean> currentLiveList = dashboardDao.getExecutiveCurrentSubject();             
                m.addAttribute("studentConfig",studentConfig);
                m.addAttribute("yearList", ACAD_YEAR_LIST);
                m.addAttribute("monthList", ACAD_MONTH_LIST);
                m.addAttribute("currentLiveList", currentLiveList);
                m.addAttribute("consumerType", dao.getConsumerTypeList());

                return "addSubjectDate";
            }
			
			@RequestMapping(value = "/admin/getDataByConsumerTypeForEMBA",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getDataByConsumerTypeForEMBA(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
			
				ResponseBean response = (ResponseBean) new ResponseBean();
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
					
					ArrayList<ConsumerProgramStructureExam> programStructureData = dao.getProgramStructureByConsumerType(consumerProgramStructure.getId());
					ArrayList<ConsumerProgramStructureExam> programData = dao.getProgramByConsumerType(consumerProgramStructure.getId());
					
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
					response.setSubjectsData(dashboardDao.getSubjectByConsumerType(consumerProgramStructure.getId(),programDataId,programStructureDataId));
		
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
		
				return new ResponseEntity(response, HttpStatus.OK);	
				
			}
			
			//Fill Programs , Subject DropDown Data Based On ConsumerType Selected
			@RequestMapping(value = "/admin/getDataByProgramStructureForEMBA",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getDataByProgramStructureForEMBA(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
				
				ResponseBean response = (ResponseBean) new ResponseBean();
				AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					ArrayList<ConsumerProgramStructureExam> programData = dao.getProgramByConsumerTypeAndPrgmStructure(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramStructureId());
					
					String programDataId = "";
					for(int i=0;i < programData.size();i++){
						programDataId = programDataId + ""+ programData.get(i).getId() +",";
					}
					programDataId = programDataId.substring(0,programDataId.length()-1);
					
					response.setStatus("success");
					
					response.setProgramsData(programData);
					response.setSubjectsData(dashboardDao.getSubjectByConsumerType(consumerProgramStructure.getConsumerTypeId(),programDataId,consumerProgramStructure.getProgramStructureId()));
				
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
				
				return new ResponseEntity(response, HttpStatus.OK);	
				
			}
			
			@RequestMapping(value = "/admin/getDataByProgramForEMBA",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getDataByProgramForEMBA (@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
			
				ResponseBean response = (ResponseBean) new ResponseBean();
				AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					
					response.setStatus("success");				
					response.setSubjectsData(dashboardDao.getSubjectByConsumerTypeAndSem(consumerProgramStructure));
		
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
				
				return new ResponseEntity(response, HttpStatus.OK);	
				
			}
			
			@RequestMapping(value = "/admin/addSubjectDate",  method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView addSubjectDate(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ProgramSubjectMappingExamBean studentConfig){
				
				
				AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
				String userId = (String)request.getSession().getAttribute("userId");
				ModelAndView modelnView = new ModelAndView("addSubjectDate");
				int count = 0;
				
				modelnView.addObject("currentLiveList", dashboardDao.getExecutiveCurrentSubject());
				modelnView.addObject("studentConfig",new ProgramSubjectMappingExamBean());
				modelnView.addObject("monthList", ACAD_MONTH_LIST);
				modelnView.addObject("yearList", ACAD_YEAR_LIST);
				modelnView.addObject("consumerType", dao.getConsumerTypeList());
				
				List<StudentSubjectConfigExamBean> studentSubConfig = studentConfig.getStudentConfigFile();
				
				String dummyUserId = "";
				String acadMonth = "";
				Date date = null;
				
				try {
					dummyUserId = "777777";
					date = new SimpleDateFormat("MMM").parse(studentConfig.getAcadMonth());
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					acadMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
					if(acadMonth.length() < 2) {
						acadMonth = "0" + acadMonth;
					}
					dummyUserId = dummyUserId + acadMonth + studentConfig.getAcadYear().toString().substring(2,4)+"1";
				} catch (ParseException e1) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error While Creating dummy userId.");
					return modelnView;
				}
				
				
				try {
					for (StudentSubjectConfigExamBean bean : studentSubConfig) {
						if (StringUtils.isBlank(bean.getStartDate()) || StringUtils.isBlank(bean.getEndDate()) || StringUtils.isBlank(bean.getId())) {	
							continue;
						}
						
//						String prgmSemSubId = dashboardDao.getPrgmSemSubId(studentConfig,bean.getSubject());
						
						bean.setAcadYear(studentConfig.getAcadYear());
						bean.setAcadMonth(studentConfig.getAcadMonth());
						bean.setExamYear(studentConfig.getExamYear());
						bean.setExamMonth(studentConfig.getExamMonth());
						bean.setPrgm_sem_subj_id(bean.getId());
						bean.setBatchId(studentConfig.getBatchId());
						bean.setCreatedBy(userId);
						bean.setLastModifiedBy(userId);
						
						int timeBoundId = dashboardDao.insertSubjectDate(bean);
						if (timeBoundId != 0) {
							count++;
							String subjectName = dashboardDao.getSubjectNameByPSSId(bean.getId());
							
							GroupExamBean group = new GroupExamBean();
							group.setTimeBoundId(timeBoundId);
							group.setGroupName(subjectName);
							group.setGroupDescription("Subject Generic Group");
							group.setCreatedBy(userId);
							group.setLastModifiedBy(userId);
							long groupId = dashboardDao.insertGenericGroups(group);
							if (groupId == 0) {
								request.setAttribute("error", "true");
								request.setAttribute("errorMessage", "Error While inserting into Group .");
								return modelnView;
							}
							
							boolean isDummyIdAdded = dashboardDao.insertDummyStudentMapping(timeBoundId, dummyUserId, userId);
							if (!isDummyIdAdded) {
								request.setAttribute("error", "true");
								request.setAttribute("errorMessage", "Error While inserting into Dummy Users .");
								return modelnView;
							}
							
						}else{
							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", "Error While inserting TimeBound table .");
							return modelnView;
						}	
					}
		
				} catch (Exception e) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in Add subject Date."+e.getMessage());
					return modelnView;
				}
				
				modelnView.addObject("currentLiveList", dashboardDao.getExecutiveCurrentSubject());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Added TimeBound for "+count+" Subjects successfully. ");
				return modelnView;
				
			}
			
			@RequestMapping(value = "/getStudentListBySubjectIdForEMBA",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
			public ResponseEntity<ResponseBean> getStudentListBySubjectIdForEMBA (@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
			
				ResponseBean response = (ResponseBean) new ResponseBean();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					response.setStatus("success");				
					response.setStudentsData(dashboardDao.getStudentBySubjectForEMBA(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramId(),consumerProgramStructure.getProgramStructureId(),consumerProgramStructure.getSubject(),consumerProgramStructure.getGroupid()));
		
				} catch (Exception e) {
					
					response.setStatus("fail");
				}
				
				return new ResponseEntity(response, HttpStatus.OK);
				
			}
			//Page 8 : ProgramType Entries Add/Update End
			@RequestMapping(value = "/admin/addTimeBoundStudentsForm", method = {RequestMethod.GET, RequestMethod.POST})
			public String addTimeBoundStudentsForm(@RequestParam("id") String TimeBoundSubjectConfigId, 
												   @RequestParam("subjectId") String prgm_sem_subj_id,
												   @RequestParam("batchId") String batchId,
												   HttpServletRequest request, HttpServletResponse response, Model m) {
				
				request.getSession().setAttribute("StudentSubjectConfigId", TimeBoundSubjectConfigId);
				request.getSession().setAttribute("prgm_sem_subj_id", prgm_sem_subj_id);
				
				request.getSession().setAttribute("batchId", batchId);
				
				ArrayList<TimeBoundUserMapping> existingStudentList = dashboardDao.getExistingStudentsByTimeboundId(TimeBoundSubjectConfigId);
				
				StudentSubjectConfigExamBean fileBean = new StudentSubjectConfigExamBean();
				m.addAttribute("fileBean",fileBean);
				m.addAttribute("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
				m.addAttribute("prgm_sem_subj_id", prgm_sem_subj_id);
				m.addAttribute("yearList", ACAD_YEAR_LIST);
				m.addAttribute("monthList", ACAD_MONTH_LIST);
				m.addAttribute("existingStudentList", existingStudentList);
				
				return "addTimeBoundStudents";
			}
			
			@RequestMapping(value = "/admin/addTimeBoundStudents", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView addTimeBoundStudents(StudentSubjectConfigExamBean fileBean, HttpServletRequest request, HttpServletResponse response, Model m){
				
				ModelAndView modelnView = new ModelAndView("addTimeBoundStudents");
				String TimeBoundSubjectConfigId = (String) request.getSession().getAttribute("TimeBoundSubjectConfigId");
				String prgm_sem_subj_id = (String) request.getSession().getAttribute("prgm_sem_subj_id");
				String subjectName=dashboardDao.getSubjectNameByPSSId(prgm_sem_subj_id);
				JsonObject responseJsonObject = new JsonObject();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				try {
					ExcelHelper excelHelper = new ExcelHelper();
					String userId = (String)request.getSession().getAttribute("userId");
					ArrayList<String> studentList = dashboardDao.getTimeBoundStudentsList(fileBean);
					
					//Start added by Abhay for MBAx Program Structure change Students waived in subject
					ArrayList<String> studentListProgramStructureChangeSubject = dashboardDao.getTimeBoundStudentsListforProgramStructureChangeSubject(fileBean);
					if(studentListProgramStructureChangeSubject != null) {
						studentList.addAll(studentListProgramStructureChangeSubject);
					}
					//End added by Abhay
					
					ArrayList<TimeBoundUserMapping> existingStudentList = dashboardDao.getExistingStudentsByTimeboundId(TimeBoundSubjectConfigId);
					
					ArrayList<List> resultList = excelHelper.readSapIdFromExcel(fileBean, studentList, userId,subjectName,serviceRequestDao);
					
					List<TimeBoundUserMapping> studentSapIdList = (ArrayList<TimeBoundUserMapping>)resultList.get(0);
					List<TimeBoundUserMapping> errorBeanList = (ArrayList<TimeBoundUserMapping>)resultList.get(1);

					StudentSubjectConfigExamBean studentSubjectConfig=fileBean;

					fileBean = new StudentSubjectConfigExamBean();
					m.addAttribute("fileBean",fileBean);
					m.addAttribute("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
					m.addAttribute("prgm_sem_subj_id", prgm_sem_subj_id);
					m.addAttribute("yearList", ACAD_YEAR_LIST);
					m.addAttribute("monthList", ACAD_MONTH_LIST);
					m.addAttribute("existingStudentList", existingStudentList);
					
					if(errorBeanList.size() > 0){
						request.setAttribute("errorBeanList", errorBeanList);
						return modelnView; 
					}
					
					ArrayList<String> errorList = dashboardDao.batchUpdateStudentEntries(studentSapIdList);
					if(errorList.size() == 0){
						request.setAttribute("success","true");
						request.setAttribute("successMessage",studentSapIdList.size() +" rows out of "+ studentSapIdList.size()+" inserted successfully.");
					}else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
					}

				} catch(Exception e){
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in inserting rows.");
				}
		
				return modelnView;
			}
			
			@RequestMapping(value = "/admin/addTimeBoundFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView addTimeBoundFacultyForm(@RequestParam("id") String TimeBoundSubjectConfigId, 
												  @RequestParam("subjectId") String prgm_sem_subj_id,
												  @RequestParam("role") String role,
												  HttpServletRequest request, HttpServletResponse response) {
				
				ModelAndView modelnView = new ModelAndView("addTimeBoundFaculty");
				request.getSession().setAttribute("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
				request.getSession().setAttribute("prgm_sem_subj_id", prgm_sem_subj_id);
				request.getSession().setAttribute("role", role);
				
				TimeBoundUserMapping fileBean = new TimeBoundUserMapping();
				modelnView.addObject("facultyList",dashboardDao.getAllFacultyList());
				modelnView.addObject("faculties",dashboardDao.getFacultiesByTimeboundId(TimeBoundSubjectConfigId,role));
				modelnView.addObject("fileBean",fileBean);
				modelnView.addObject("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
				modelnView.addObject("prgm_sem_subj_id", prgm_sem_subj_id);
				modelnView.addObject("yearList", ACAD_YEAR_LIST);
				
				return modelnView;
			}
			
			@RequestMapping(value = "/admin/addTimeBoundFaculty", method = {RequestMethod.GET, RequestMethod.POST})
			public ModelAndView addTimeBoundFaculty(HttpServletRequest request, HttpServletResponse response, 
													@ModelAttribute TimeBoundUserMapping mappingBean){
						
				String userId = (String)request.getSession().getAttribute("userId");
				String TimeBoundSubjectConfigId = (String) request.getSession().getAttribute("TimeBoundSubjectConfigId");
				String prgm_sem_subj_id = (String) request.getSession().getAttribute("prgm_sem_subj_id");
				String role = (String) request.getSession().getAttribute("role");
				
				ModelAndView modelAndView = new ModelAndView("addTimeBoundFaculty");
				try {
					
					List<FacultyExamBean> listOfFaculty = mappingBean.getFaculties();
					int successCount = 0;
					
					for (FacultyExamBean bean : listOfFaculty) {
						
						if (StringUtils.isBlank(bean.getFacultyId())) {
							//If no FacultyId, then do not store in Database
							continue;
						}
						mappingBean.setCreatedBy(userId);
						mappingBean.setUserId(bean.getFacultyId());
						if(role.equalsIgnoreCase("Faculty")) {
							mappingBean.setRole("Faculty");
						}else {
							mappingBean.setRole("Grader");
						}
						dashboardDao.insertFacultyMapping(mappingBean);
						successCount++;
					}
					
					request.setAttribute("success","true");
					request.setAttribute("successMessage", successCount+" Faculty Added for this subject successfully");
					
				} catch (Exception e) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error While Updating.");
				}
				
				TimeBoundUserMapping fileBean = new TimeBoundUserMapping();
				modelAndView.addObject("facultyList",dashboardDao.getAllFacultyList());
				modelAndView.addObject("fileBean",fileBean);
				modelAndView.addObject("yearList", ACAD_YEAR_LIST);
				modelAndView.addObject("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
				modelAndView.addObject("faculties",dashboardDao.getFacultiesByTimeboundId(TimeBoundSubjectConfigId,role));
				modelAndView.addObject("prgm_sem_subj_id", prgm_sem_subj_id);
				
				return modelAndView;
				
			}

	@RequestMapping(value = "/admin/addTimeBoundCoordinatorForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addTimeBoundCoordinatorForm(@RequestParam("id") String TimeBoundSubjectConfigId,
												@RequestParam("subjectId") String prgm_sem_subj_id,
												HttpServletRequest request, HttpServletResponse response) {

		ModelAndView modelnView = new ModelAndView("addTimeBoundCoordinator");
		String userId = (String)request.getSession().getAttribute("userId");
		request.getSession().setAttribute("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
		request.getSession().setAttribute("prgm_sem_subj_id", prgm_sem_subj_id);

		TimeBoundUserMapping fileBean = new TimeBoundUserMapping();
		modelnView.addObject("coordinatorList",dashboardDao.getAllCoordinatorList());
		modelnView.addObject("coordinators",dashboardDao.getCoordinatorsByTimeboundId(TimeBoundSubjectConfigId));
		modelnView.addObject("fileBean",fileBean);
		modelnView.addObject("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
		modelnView.addObject("prgm_sem_subj_id", prgm_sem_subj_id);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		return modelnView;
	}

	@RequestMapping(value = "/admin/addTimeBoundCoordinator", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addTimeBoundCoordinator(HttpServletRequest request, HttpServletResponse response,
											@ModelAttribute TimeBoundUserMapping mappingBean){

		String userId = (String)request.getSession().getAttribute("userId");
		String TimeBoundSubjectConfigId = (String) request.getSession().getAttribute("TimeBoundSubjectConfigId");
		String prgm_sem_subj_id = (String) request.getSession().getAttribute("prgm_sem_subj_id");

		ModelAndView modelAndView = new ModelAndView("addTimeBoundCoordinator");

		try {

			List<TimeBoundUserMapping> coordinators=dashboardDao.getCoordinatorsByTimeboundId(TimeBoundSubjectConfigId);
			if(coordinators.size()>1){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Course Coordinator is already assigned!");
			}
			else {
				List<UserAuthorizationExamBean> listOfCoordinator = mappingBean.getCoordinators();
				int successCount = 0;

				for (UserAuthorizationExamBean ua : listOfCoordinator) {

					if (StringUtils.isBlank(ua.getUserId())) {
						//If no CoordinatorId, then do not store in Database
						continue;
					}
					mappingBean.setCreatedBy(userId);
					mappingBean.setLastModifiedBy(userId);
					mappingBean.setUserId(ua.getUserId());
					mappingBean.setRole("Course Coordinator");
					String timebound_user_mapping_primaryKey=dashboardDao.addTimeBoundMapping(mappingBean);
					if(!timebound_user_mapping_primaryKey.equals("") && timebound_user_mapping_primaryKey!=null && !timebound_user_mapping_primaryKey.isEmpty()) {
						request.setAttribute("success", "true");
						request.setAttribute("successMessage", "Coordinator Added for this subject successfully");
					} else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error While Updating.");
					}
				}
			}

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error While Updating.");
		}

		TimeBoundUserMapping fileBean = new TimeBoundUserMapping();
		modelAndView.addObject("coordinatorList",dashboardDao.getAllCoordinatorList());
		modelAndView.addObject("fileBean",fileBean);
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("TimeBoundSubjectConfigId", TimeBoundSubjectConfigId);
		modelAndView.addObject("coordinators",dashboardDao.getCoordinatorsByTimeboundId(TimeBoundSubjectConfigId));
		modelAndView.addObject("prgm_sem_subj_id", prgm_sem_subj_id);

		return modelAndView;

	}
			
		@RequestMapping(value = "/addBatchDetailsForm", method = {RequestMethod.GET, RequestMethod.POST})
		public String addBatchDetailsForm(HttpServletRequest request, HttpServletResponse response, Model m) {
	
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			BatchExamBean bean = new BatchExamBean();
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			
			m.addAttribute("batchList", dashboardDao.getBacthDeatils());
			m.addAttribute("consumerType", dao.getConsumerTypeList());
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("monthList", ACAD_MONTH_LIST);
			m.addAttribute("semesterList", semesterList);
			m.addAttribute("bean", bean);
			
			return "mdm/addBatchDetails";
		}
			
		@RequestMapping(value = "/addBatchDetails",  method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView addBatchDetails(HttpServletRequest request, HttpServletResponse response, @ModelAttribute BatchExamBean bean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			String userId = (String)request.getSession().getAttribute("userId");
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			ModelAndView modelnView = new ModelAndView("mdm/addBatchDetails");
			
			modelnView.addObject("bean", new BatchExamBean());
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("monthList", ACAD_MONTH_LIST);
			modelnView.addObject("consumerType", dao.getConsumerTypeList());
			modelnView.addObject("semesterList", semesterList);
			
			try {
				String consumerProgramStructureId = dao.getAssignmentKey(bean.getProgramId(), bean.getProgramStructureId(), bean.getConsumerTypeId());
				bean.setConsumerProgramStructureId(consumerProgramStructureId);
				bean.setCreatedBy(userId);
				
				String successMsg = dashboardDao.insertBatchDeatils(bean);
				if (StringUtils.equalsIgnoreCase("Error", successMsg)) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error While updating DB .");
				}
				
			} catch (Exception e) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in Add Batch Deatils. "+e.getMessage());
				return modelnView;
			}
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Batch Deatils Added successfully. ");
			modelnView.addObject("batchList", dashboardDao.getBacthDeatils());
			return modelnView;
		
		}
		
		@RequestMapping(value = "/deleteBatch", method = RequestMethod.POST, consumes="application/json", produces="application/json")
		public ResponseEntity<HashMap<String, String>> deleteBatch(@RequestBody BatchExamBean bean){
			HashMap<String, String> response = new  HashMap<String, String>();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			try {
				HashMap<String,String> message = dashboardDao.deleteBatch(bean.getId());
				if(message.containsKey("error")){
					response.put("Status", "Fail");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("Status", "Success");
			} catch (Exception e) {
				
				response.put("Status", "Fail");
			}
			
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
		}
		
		@RequestMapping(value = "/updateBatchName",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity<HashMap<String, String>> updateBatchName(HttpServletRequest request, @RequestBody BatchExamBean bean){
			
			HashMap<String, String> response = new  HashMap<String, String>();
			String userId = (String)request.getSession().getAttribute("userId");
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			try {
				bean.setLastModifiedBy(userId);
				HashMap<String,String> message = dashboardDao.updateBatchName(bean);
				if(message.containsKey("error")){
					response.put("Status", "Fail");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("Status", "Success");
			} catch (Exception e) {
				
				response.put("Status", "Fail");
			}
			
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);				
		}
		
		@RequestMapping(value = "/admin/getBatchList",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity<ResponseBean> getBatchList (@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
		
			ResponseBean response = (ResponseBean) new ResponseBean();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			try {
				response.setBatchData(dashboardDao.getBatchList(consumerProgramStructure));
				response.setStatus("success");
			} catch (Exception e) {
				
				response.setStatus("fail");
			}
			return new ResponseEntity(response, HttpStatus.OK);	
		}
		
		@RequestMapping(value = "/addTimeBoundStudentMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
		public String addTimeBoundStudentMappingForm(HttpServletRequest request, HttpServletResponse response, Model m) {
	
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			TimeBoundUserMapping mappingBean = new TimeBoundUserMapping();
			
			m.addAttribute("mappingBean", mappingBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("monthList", ACAD_MONTH_LIST);
			m.addAttribute("semesterList", semesterList);
			m.addAttribute("batchList", dashboardDao.getBatchListByYearMonth());
			m.addAttribute("userList", dashboardDao.getExistingTimeboundStudents());
			return "mdm/addTimeBoundStudentMapping";
		}
		
		@RequestMapping(value = "/addTimeBoundStudentMapping", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView addTimeBoundStudentMapping(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TimeBoundUserMapping mappingBean) {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			String userId = (String)request.getSession().getAttribute("userId");
			ModelAndView modelnView = new ModelAndView("mdm/addTimeBoundStudentMapping");
			int success = 0;
			int error = 0;
			try {

				List<String> userIds = Arrays.asList(mappingBean.getUserId().split(","));

				mappingBean.setCreatedBy(userId);
				mappingBean.setLastModifiedBy(userId);
				mappingBean.setRole("Student");

				for (String sapid : userIds) {
					try {
						mappingBean.setUserId(sapid);
						ArrayList<Integer> list = dashboardDao.getTimeBoundId(mappingBean.getBatchId());
						if (list.size() == 0) {

							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", "No timebound id found for selected batch.");

							modelnView.addObject("mappingBean", new TimeBoundUserMapping());
							modelnView.addObject("semesterList", semesterList);
							modelnView.addObject("yearList", ACAD_YEAR_LIST);
							modelnView.addObject("monthList", ACAD_MONTH_LIST);
							modelnView.addObject("batchList", dashboardDao.getBatchListByYearMonth());
							modelnView.addObject("userList", dashboardDao.getExistingTimeboundStudents());

							return modelnView;

						}else {
							for (Integer timeBoundId : list) {
								mappingBean.setTimebound_subject_config_id(timeBoundId);
								dashboardDao.addTimeBoundMapping(mappingBean);
							}
							success++;
						}
						
					} catch (Exception e) {
						error++;
						
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error while Adding "+error+" Timebound Mapping.");
					}
				}
				
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Timebound Mapping Added Sucessfully for "+success+" Students.");
				
			} catch (Exception e) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in Adding Timebound Mapping.");
			}
			
			modelnView.addObject("mappingBean", new TimeBoundUserMapping());
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("semesterList", semesterList);
			modelnView.addObject("batchList", dashboardDao.getBatchListByYearMonth());
			modelnView.addObject("userList", dashboardDao.getExistingTimeboundStudents());
			
			return modelnView;
		}
		
		@RequestMapping(value = "/getBatchListByYearMonth",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity<ResponseBean> getBatchListByYearMonth (@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
		
			ResponseBean response = (ResponseBean) new ResponseBean();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			try {
				response.setBatchData(dashboardDao.getBatchListByYearMonth(consumerProgramStructure));
				response.setStatus("success");	
			} catch (Exception e) {
				
				response.setStatus("fail");
			}
			return new ResponseEntity(response, HttpStatus.OK);	
		}
		
		@RequestMapping(value = "/getMBAStudentsList", method = RequestMethod.POST)
		public ResponseEntity<ResponseBean> getMBAStudentsList (@RequestBody ConsumerProgramStructureExam consumerBean){
			
			ResponseBean response = (ResponseBean) new ResponseBean();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			try {
				response.setStudentList(dashboardDao.getStudentListCPSId(consumerBean));
				response.setStatus("success");
			} catch (Exception e) {
				
				response.setStatus("fail");
			}
			
			return new ResponseEntity<ResponseBean>(response,headers, HttpStatus.OK);
		}
		
		   
		@RequestMapping(value = "/admin/updateTimeBoundMapping",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity<HashMap<String, String>> updateTimeBoundMapping(@RequestBody StudentSubjectConfigExamBean configBean){
			
			HashMap<String, String> response = new  HashMap<String, String>();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			try {
				//Checking date comparison as Start Date should be less than End Date
					//Create SimpleDateFormat Object
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
					
					//Parsing String Date to Util Date
					Date startDate=sdf.parse(configBean.getStartDate());
					Date endDate=sdf.parse(configBean.getEndDate());
					
					//Compare startDate with endDate if it is not null
					if(startDate!=null && endDate!=null && !"".equals(startDate) && !"".equals(endDate))
						if(startDate.after(endDate) || startDate.equals(endDate)) { 
							response.put("Status", "Fail");
							response.put("message", "Start date should be less than end Date.");
							return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
						}//inner if
				
				HashMap<String,String> message = dashboardDao.updateTimeBoundMapping(configBean);
				if(message.containsKey("error")){
					response.put("Status", "Fail");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("Status", "Success");
			} catch (Exception e) {
				
				response.put("Status", "Fail");
				response.put("message", "Error while updating details. Please retry");
			}
			
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);				
		}
		
		@RequestMapping(value = "/admin/deleteTimeBoundMapping", method = RequestMethod.POST, consumes="application/json", produces="application/json")
		public ResponseEntity<HashMap<String, String>> deleteTimeBoundMapping(@RequestBody StudentSubjectConfigExamBean configBean){
			HashMap<String, String> response = new  HashMap<String, String>();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
			try {
				HashMap<String,String> message = dashboardDao.deleteTimeBoundMapping(configBean.getId());
				if(message.containsKey("error")){
					response.put("Status", "Fail");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("Status", "Success");
			} catch (Exception e) {
				
				response.put("Status", "Fail");
			}
			
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
		}
		
			
		@RequestMapping(value = "/updateMasterKeyInStudentsAndRegTables", method = {RequestMethod.POST})
		public ResponseEntity<ResponseBean> updateMasterKeyInStudentsAndRegTables(){
			//get list of students to update masterkey
			ArrayList<StudentExamBean> studentsWithIncorrectData = dashboardDao.studentsWithIncorrectMasterKeyJul2019();
			ResponseBean bean =new ResponseBean();
			//find correct masterkeys for studentList
			for(StudentExamBean student:studentsWithIncorrectData) {
						student.setConsumerProgramStructureId(dashboardDao.getConsumerProgramStructureId(student.getConsumerType(), student.getPrgmStructApplicable(), student.getProgram()));
			}
			
			//update student and registration tables
			String error1 = dashboardDao.batchUpdateStudentsForIncorrectMasterKeyJul2019(studentsWithIncorrectData);
			if(StringUtils.isNotEmpty(error1)) {
				bean.setMessage("Error in updating masterKey students TAble");
				return new ResponseEntity<ResponseBean>(bean, HttpStatus.OK);
			}
			
			String error2 = dashboardDao.batchUpdateRegistrationForIncorrectMasterKeyJul2019(studentsWithIncorrectData);
			if(StringUtils.isNotEmpty(error2)) {
				bean.setMessage("Error in updating masterKey reg table");
				return new ResponseEntity<ResponseBean>(bean, HttpStatus.OK);
			}else {
				bean.setMessage("Successful");
				return new ResponseEntity<ResponseBean>(bean, HttpStatus.OK);
			}
			
		}
		
		@RequestMapping(value = "/admin/deleteFacultyMapping", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView deleteFacultyMapping(HttpServletRequest request, HttpServletResponse response){
			String timeBoundId = request.getParameter("timeBoundId");
			String id = request.getParameter("id");
			String prgm_sem_subj_id = request.getParameter("prgm_sem_subj_id");
			String role = request.getParameter("role");
			
			try{
				
				dashboardDao.deleteCourseFacultyMapping(id);
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record deleted successfully from Database");

			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in deleting Record.");
			}
			
			return addTimeBoundFacultyForm(timeBoundId, prgm_sem_subj_id, role,request, response);
		}
		
		@RequestMapping(value = "/admin/editFacultyMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView editFacultyMappingForm(HttpServletRequest request, HttpServletResponse response,  Model m){
			
			String id = request.getParameter("id");
			String prgm_sem_subj_id = request.getParameter("prgm_sem_subj_id");
			ModelAndView modelnView = new ModelAndView("addTimeBoundFaculty");
			
			try{
				TimeBoundUserMapping fileBean = dashboardDao.findScheduledSessionById(id);
				fileBean.setPrgm_sem_subj_id(prgm_sem_subj_id);
				m.addAttribute("fileBean", fileBean);
				modelnView.addObject("facultyList",dashboardDao.getAllFacultyList());
				
			}catch(Exception e){
				
			}
			
			request.setAttribute("edit", "true");
			return modelnView;
		}
		
		@RequestMapping(value = "/admin/editFacultyMapping", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView editFacultyMapping(HttpServletRequest request, HttpServletResponse response, 
												@ModelAttribute TimeBoundUserMapping fileBean){
			
			String timeBoundId = Integer.toString(fileBean.getTimebound_subject_config_id());
			String role = request.getParameter("role");
			try{
				boolean updated = dashboardDao.updateTimeBoundFacultyMapping(fileBean);
				if (updated) {
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Record Updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in Updating Record.");
				}
				
			}catch(Exception e){
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in Updating Record.");
			}
			
			return addTimeBoundFacultyForm(timeBoundId, fileBean.getPrgm_sem_subj_id(),role, request, response);
		}

    @RequestMapping(value = "/admin/deleteCoordinatorMapping", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView deleteCoordinatorMapping(HttpServletRequest request, HttpServletResponse response){
        String timeBoundId = request.getParameter("timeBoundId");
        String id = request.getParameter("id");
        String prgm_sem_subj_id = request.getParameter("prgm_sem_subj_id");

        try{
            dashboardDao.deleteCourseCoordinatorMapping(id);           
            request.setAttribute("success","true");
            request.setAttribute("successMessage","Record deleted successfully from Database");

        }catch(Exception e){
            
            request.setAttribute("error", "true");
            request.setAttribute("errorMessage", "Error in deleting Record.");
        }

        return addTimeBoundCoordinatorForm(timeBoundId, prgm_sem_subj_id, request, response);
    }

	@RequestMapping(value = "/admin/editCoordinatorMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editCoordinatorMappingForm(HttpServletRequest request, HttpServletResponse response,  Model m){

		String id = request.getParameter("id");
		String prgm_sem_subj_id = request.getParameter("prgm_sem_subj_id");
		ModelAndView modelnView = new ModelAndView("addTimeBoundCoordinator");

		try{
			TimeBoundUserMapping fileBean = dashboardDao.findScheduledSessionForCoordinatorById(id);
			fileBean.setPrgm_sem_subj_id(prgm_sem_subj_id);
			m.addAttribute("fileBean", fileBean);
			modelnView.addObject("coordinatorList",dashboardDao.getAllCoordinatorList());

		}catch(Exception e){
			
		}

		request.setAttribute("edit", "true");
		return modelnView;
	}

	@RequestMapping(value = "/admin/editCoordinatorMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editCoordinatorMapping(HttpServletRequest request, HttpServletResponse response,
										   @ModelAttribute TimeBoundUserMapping fileBean){

		String timeBoundId = Integer.toString(fileBean.getTimebound_subject_config_id());
		try{
			boolean updated = dashboardDao.updateTimeBoundCoordinatorMapping(fileBean);			
			if (updated) {
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record Updated successfully");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in Updating Record.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Updating Record.");
		}

		return addTimeBoundCoordinatorForm(timeBoundId, fileBean.getPrgm_sem_subj_id(), request, response);
	}
		
		
		@RequestMapping(value = "/examRegularRegistrationChecklist", method = {RequestMethod.GET, RequestMethod.POST})
		public String examRegularRegistrationChecklist(HttpServletRequest request, HttpServletResponse respnse,Model m) {
		
			return "examRegularRegistrationChecklist";
		}

	@RequestMapping(value = "/admin/searchStudentBatchMappingForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchStudentBatchMappingForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		TimeBoundUserMapping mappingBean = new TimeBoundUserMapping();
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		m.addAttribute("mappingBean", mappingBean);
		m.addAttribute("consumerType", dao.getConsumerTypeList());
		m.addAttribute("programSubjectMappingBean",new ProgramSubjectMappingExamBean());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("semesterList", semesterList);
//		m.addAttribute("batchList", dashboardDao.getBatchListByYearMonth());
		return "mdm/searchStudentBatchMapping";
	}

	@RequestMapping(value = "/admin/searchStudentBatchMapping", method = RequestMethod.POST)
	public ModelAndView searchStudentBatchMapping(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute TimeBoundUserMapping mappingBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		
		String[] pssidanddate = mappingBean.getSubject().split("~");
		String pssId = pssidanddate[0];
		String startDate = pssidanddate[1];
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView modelnView = new ModelAndView("mdm/searchStudentBatchMapping");
		List<TimeBoundUserMapping> userList = new ArrayList<>();
		try {
			
			userList = dashboardDao.getStudentBatchMapping(pssId, mappingBean.getBatchId(), mappingBean.getIsResit(), startDate);
			if (userList.size() == 0) {

				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "None students found for selected batch/subject.");

				modelnView.addObject("mappingBean", new TimeBoundUserMapping());
				modelnView.addObject("consumerType", dao.getConsumerTypeList());
				modelnView.addObject("programSubjectMappingBean",new ProgramSubjectMappingExamBean());
				modelnView.addObject("semesterList", semesterList);
				modelnView.addObject("yearList", ACAD_YEAR_LIST);
				modelnView.addObject("monthList", ACAD_MONTH_LIST);
//				modelnView.addObject("batchList", dashboardDao.getBatchListByYearMonth());
				modelnView.addObject("rowCount", userList.size());
				return modelnView;

			} else {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Successfully fetched student batch mapping.");			
			}			
			

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error while fetching student batch mapping.");
		}					
		
		modelnView.addObject("mappingBean", new TimeBoundUserMapping());
		modelnView.addObject("consumerType", dao.getConsumerTypeList());
		modelnView.addObject("programSubjectMappingBean",new ProgramSubjectMappingExamBean());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("monthList", ACAD_MONTH_LIST);
		modelnView.addObject("semesterList", semesterList);
//		modelnView.addObject("batchList", dashboardDao.getBatchListByYearMonth());
		modelnView.addObject("userList", userList);
		modelnView.addObject("rowCount", userList.size());

		request.getSession().setAttribute("studentBatchMapList", userList);
		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadStudentBatchMapping", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadStudentBatchMapping(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");

		List<TimeBoundUserMapping> studentBatchMapList = (List<TimeBoundUserMapping>) request.getSession()
				.getAttribute("studentBatchMapList");

		return new ModelAndView("studentBatchMappingExcelView", "studentBatchMapList", studentBatchMapList);
	}
	
	@RequestMapping(value = "/admin/deleteStudentTimeBoundMapping", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public ResponseEntity<HashMap<String, String>> deleteStudentTimeBoundMapping(@RequestBody TimeBoundUserMapping userMapping){
		HashMap<String, String> response = new  HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			HashMap<String,String> message = dashboardDao.deleteStudentTimeBoundMapping(userMapping.getId());
			if(message.containsKey("error")){
				response.put("Status", "Fail");
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
			}
			response.put("Status", "Success");
		} catch (Exception e) {
			
			response.put("Status", "Fail");
		}
		
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/getBatchesByMasterKey",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getBatchesByMasterKey (@RequestBody ConsumerProgramStructureExam consumerProgramStructure){

		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response.setBatchData(dashboardDao.getBatchesByMasterKey(consumerProgramStructure));
			response.setStatus("success");
		} catch (Exception e) {
			
			response.setStatus("fail");
		}
		return new ResponseEntity(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/insertLiveProgramMapping",  method = RequestMethod.GET)
	public String insertLiveProgramMapping (){

		try {
			System.out.println("insertLiveProgramMapping");
			ArrayList<String> masterkeysList=dashboardDao.getMasterKeysList();
			System.out.println("masterkeysList"+masterkeysList.size());
			for(String consumerProgramStructureId: masterkeysList) {
				dashboardDao.insertmasterKeyInLiveSessionMappingTable(consumerProgramStructureId);
			}
			dashboardDao.updateLiveSessionFlag();
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Fail";
		}
	}
	
	@RequestMapping(value="/admin/failedSubjectCriteriaForm",method = RequestMethod.GET)
	public ModelAndView failedSubjectCriteriaForm(HttpServletRequest request)
	{
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("mdm/timeboundFailedSubjectCount");
		mv.addObject("userId", userId);
		List<FailedSubjectCountCriteriaBean>failedCriteriaDetails = dashboardDao.getFailedCriteriaDetails();
		mv.addObject("failedCriteriaDetails", failedCriteriaDetails);
		return mv;
	}
	
	@RequestMapping(value = "/admin/deleteProgramFormMasterEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<HashMap<String, String>> deleteProgramFormMasterEntry(@RequestBody ProgramExamBean bean, HttpServletRequest request){
		
		HashMap<String, String> response = new  HashMap<String, String>();
		response.put(status, error);
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			int i =  dashboardDao.deleteProgram(bean.getId());
			if(i > 0) {
				response.put(statusMessage, successDeleteMessage);
				response.put(status, success);
			}
		}catch(DataIntegrityViolationException e) {
			response.put(statusMessage, "Not Allowed to delete this id as it has references.");
		}
		catch (Exception e) {
			response.put(statusMessage, "Error in deleting the record. Error :- "+e.getMessage());
		}
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
	}
	
	@RequestMapping(value = "/admin/deleteProgramsEntry",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<HashMap<String, String>> deleteProgramsEntry(@RequestBody ProgramsBean programsForm){

		HashMap<String, String> response = new  HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		response.put(status, error);
	
		headers.add("Content-Type", "application/json");
		try {
				if(StringUtils.isBlank(programsForm.getId())){
					response.put(statusMessage, "Error in getting details");
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
				}
				int i =  dashboardDao.deleteProgramDetails(programsForm.getId());
				if(i > 0) {
						response.put(statusMessage, successDeleteMessage);
						response.put(status, success);
				}
			}catch(DataIntegrityViolationException e) {
				response.put(statusMessage, "Not Allowed to delete this id as it has references.");
			}
			catch (Exception e) {
				response.put(statusMessage, "Error in deleting the record. Error :- "+e.getMessage());
			}
		
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
		
	}
	
	


	@RequestMapping(value = "/admin/downloadStudentTimeBoundReport",method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentTimeBoundReport(HttpServletRequest request,HttpServletResponse response){		
		List<TimeBoundUserMapping> newStudentList=new ArrayList<TimeBoundUserMapping>();
		try {	
			String timeBoundSubjectConfigId = (String)request.getSession().getAttribute("StudentSubjectConfigId");
			String prgm_sem_subj_id = (String)request.getSession().getAttribute("prgm_sem_subj_id");		
			String batchId = (String)request.getSession().getAttribute("batchId");
			newStudentList = configurationService.downloadTimeBoundExcelService(timeBoundSubjectConfigId,prgm_sem_subj_id,batchId);	
			return new ModelAndView(studentTimeboundExcelView, "existingStudentList", newStudentList);
		} catch (Exception e) {
			logger.error(String.valueOf(e));
		} 
		return null;
		
	}
	
	
	
}	

