package com.nmims.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.map.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ResponseStudentPortalBean;
import com.nmims.beans.SalesforceStudentAccountBean;
import com.nmims.beans.StudentStudentPortalBean; 
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.helpers.SalesforceHelper;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.SchedulerApisBean; 
@Controller
public class SalesForceSyncController {
	@Autowired
	StudentInfoCheckDAO sDao; 
	@Autowired
	ApplicationContext act;
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	@Autowired
	private SalesforceHelper sfdc;
	@Value("${SERVER}")
	private String SERVER;
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	@Value("${SALESFORCE_SERVER_PATH}")
	private String SALESFORCE_SERVER_PATH;
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	private static final Logger loggerPFSyncPG = LoggerFactory.getLogger("salesforcePFSyncPG");
	private static final Logger loggerPFSyncMBAWX = LoggerFactory.getLogger("salesforcePFSyncMBAWX");
	private static final Logger loggerPFSyncMBAX = LoggerFactory.getLogger("salesforcePFSyncMBAX");
	private static final Logger loggerPFSyncPDDM = LoggerFactory.getLogger("salesforcePFSyncPDDM");

	private final String programPDDMType = "PDDM";

	@RequestMapping(value = "/admin/syncPassedSemList", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView salesforceSync( HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList");
		SchedulerApisBean bean = new SchedulerApisBean();
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv;   

	}
	

	@RequestMapping(value = "/admin/getPassedSemInfoForPg", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPassedSemInfoForPg( HttpServletRequest request,@ModelAttribute SchedulerApisBean search) {
		loggerPFSyncPG.info("PG-PF START");  
		boolean response = fetchPassFailListAndUpdateInSFDC(search.getAcadYear(),search.getAcadMonth(),"PG",search.getSyncType(), loggerPFSyncPG);
		if(response) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully Synced.");
		}
		SchedulerApisBean bean = new SchedulerApisBean();
		bean.setSyncType("Passed Sem List For PG"); 
		sDao.updateLastSyncedTime(bean);
		loggerPFSyncPG.info("PG-PF END");
		
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList"); 
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv;   
	}
	

	@RequestMapping(value = "/admin/getPassedSemInfoForMbawx", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPassedSemInfoForMbawx( HttpServletRequest request,@ModelAttribute SchedulerApisBean search) throws ConnectionException {
		//fetch failed students from salesforce
		loggerPFSyncMBAWX.info("MBAWX-PF START"); 
		boolean response = fetchPassFailListAndUpdateInSFDC(search.getAcadYear(),search.getAcadMonth(),"MBA (WX)",search.getSyncType(), loggerPFSyncMBAWX);
		if(response) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully Synced.");
		}
		SchedulerApisBean bean = new SchedulerApisBean();
		bean.setSyncType("Passed Sem List For MbaWx"); 
		sDao.updateLastSyncedTime(bean);
		loggerPFSyncMBAWX.info("MBAWX-PF END"); 
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList"); 
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv;     
	}
	
	@RequestMapping(value = "/admin/getPassedSemInfoForMbax", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPassedSemInfoForMbax( HttpServletRequest request,@ModelAttribute SchedulerApisBean search) {

		loggerPFSyncMBAX.info("MBAX-PF START"); 
		loggerPFSyncMBAX.info("Running Pass-Fail for MBA - X");
		boolean response = fetchPassFailListAndUpdateInSFDC(search.getAcadYear(),search.getAcadMonth(),"MBA (X)",search.getSyncType(),loggerPFSyncMBAX);
		if(response) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully Synced.");
		}
		SchedulerApisBean bean = new SchedulerApisBean();
		bean.setSyncType("Passed Sem List For Mbax"); 
		loggerPFSyncMBAX.info("Updating last sync time");
		sDao.updateLastSyncedTime(bean);
		loggerPFSyncMBAX.info("MBAX-PF END"); 
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList"); 
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv;      
	}
	@RequestMapping(value = "/admin/getPassedSemInfoForMsc", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPassedSemInfoForMsc( HttpServletRequest request,@ModelAttribute SchedulerApisBean search) throws ConnectionException {
		//fetch failed students from salesforce 
		loggerPFSyncMBAWX.info("MSC-PF START");
		boolean response=false;
		try {
			response = fetchPassFailListAndUpdateInSFDC(search.getAcadYear(),search.getAcadMonth(),"MSC",search.getSyncType(),loggerPFSyncMBAWX);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(response) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully Synced.");
		}
		SchedulerApisBean bean = new SchedulerApisBean();
		bean.setSyncType("Passed Sem List For Msc"); 
		sDao.updateLastSyncedTime(bean);
		loggerPFSyncMBAWX.info("MSC-PF END"); 
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList"); 
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv; 
	}
	public boolean fetchPassFailListAndUpdateInSFDC(String year,String month,String programType,String syncType, Logger logger) {
		
		
		
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
		  System.out.println("Not Running passfail sync since it is not PROD ");
		  logger.warn("Not Running passfail sync since it is not PROD ");
		  
		  return false; }
		 
		if((year==null || month==null || year.equalsIgnoreCase("") || month.equalsIgnoreCase("") ) && !(programPDDMType.equalsIgnoreCase(programType))) {
			System.out.println("Invalid year month");
			return false;
		}
			
		int UPDATE_BATCH_SIZE = 10;

		ArrayList<SObject> passFailListToUpdateBackInSalesforce = new ArrayList<SObject>();
		ArrayList<SalesforceStudentAccountBean> opportunitiesToUpdateInSalesforce = new ArrayList<SalesforceStudentAccountBean>();
		//fetch failed students from salesforce
		ArrayList<SalesforceStudentAccountBean> sfdcStudents = sfdc.getFailedStudentsFromsfdc(programType); 
		logger.info("SFDC Failed Students Size" + sfdcStudents.size());
		//logger.info("Students List"+sfdcStudents.toString());

		/*if(sfdcStudents.size()>0) {
			for (SalesforceStudentAccountBean sfdcInput : sfdcStudents) { 
				if(sfdcInput.getSapid().equalsIgnoreCase("77117885985")) {
					//System.out.println("got sapid 77117885985");
				}
			}
		}*/
		if(sfdcStudents.size()>0) {
			for (SalesforceStudentAccountBean sfdcInput : sfdcStudents) { 
				SalesforceStudentAccountBean sfdcOutput = new SalesforceStudentAccountBean();
				if(sfdcInput.getSapid().startsWith("77")  &&
						sfdcInput.getSapid().length()==11
						){
					////System.out.println("got student-"+sfdcInput.getSapid());
					String sapid=sfdcInput.getSapid();
					sfdcOutput.setSapid(sapid);
					sfdcOutput.setId(sfdcInput.getId());
					sfdcOutput.setProgramType(programType);
					try {
						StudentStudentPortalBean student = sDao.getstudentData(sapid);  
							 
						//check student's program 
						//and create a map of sem & subjectscount per sem
						ArrayList<ProgramSubjectMappingStudentPortalBean> SemSubjectCountMap = serviceRequestDao.getSemSubjectCountMapping(student);
						
						//check passfail table to find if all subjects cleared 
						//for each sem
						ArrayList<PassFailBean> passedSems = new ArrayList<PassFailBean>();
						for (ProgramSubjectMappingStudentPortalBean programBean : SemSubjectCountMap) {
							PassFailBean passedSubjectsBean=null;	
							////System.out.println("programType"+programType);
							if("PG".equalsIgnoreCase(programType) || programPDDMType.equalsIgnoreCase(programType) ) {
								if(programPDDMType.equalsIgnoreCase(programType) )
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbaWx(programBean.getSem(),sapid);
								else
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCount(programBean.getSem(),sapid);
								if(passedSubjectsBean.getCount() == Integer.parseInt(programBean.getSubjectsCount()) ) {
								
									passedSems.add(passedSubjectsBean);
									switch (programBean.getSem()) {
									case "1":
										sfdcOutput.setSem1(true);
										break;
									case "2":
										sfdcOutput.setSem2(true);
										break;
									case "3":
										sfdcOutput.setSem3(true);
										break;
									case "4":
										sfdcOutput.setSem4(true);
										break;
									}
								}
								
							}else {
								int total_subject_to_clear = Integer.parseInt(programBean.getSubjectsCount());
								boolean semFailed=false;
								if("MBA (X)".equalsIgnoreCase(programType)) {
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbax(programBean.getSem(),sapid);
									semFailed = serviceRequestDao.checkIfFailedForThisSemMbax(programBean.getSem(),sapid);
									
								}
								
								//subjects to clear in sem 3 and 4 kept static as there's electives in these sem
								if("MBA (WX)".equalsIgnoreCase(programType) ) {
	 								 
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbaWx(programBean.getSem(),sapid);
									semFailed = serviceRequestDao.checkIfFailedForThisSemMbaWx(programBean.getSem(),sapid);
									
									if (student.getConsumerProgramStructureId().equalsIgnoreCase("111")) {
										if( "3".equalsIgnoreCase(programBean.getSem()) ||  "4".equalsIgnoreCase(programBean.getSem()) ) {
											total_subject_to_clear = 5;
										}
									}else if (student.getConsumerProgramStructureId().equalsIgnoreCase("160")) {
										if( "3".equalsIgnoreCase(programBean.getSem())) {
											total_subject_to_clear = 4;
										}else if ("4".equalsIgnoreCase(programBean.getSem()) || "5".equalsIgnoreCase(programBean.getSem())) {
											total_subject_to_clear = 5;
										}
									}
								} 
								//subjects to clear in sem 4 and 5 kept static as there's electives in these sem
								if("MSC".equalsIgnoreCase(programType) ) {
	 								 
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbaWx(programBean.getSem(),sapid);
									semFailed = serviceRequestDao.checkIfFailedForThisSemMbaWx(programBean.getSem(),sapid);
									
									if( "4".equalsIgnoreCase(programBean.getSem())) {
										total_subject_to_clear = 3;
									}
									if( "5".equalsIgnoreCase(programBean.getSem()) ) {
										total_subject_to_clear = 4;
									}
								}
								
								//System.out.println("passed subjects:"+passedSubjectsBean.getCount()+"--total"+ total_subject_to_clear);
								
								StudentStudentPortalBean sbean = sDao.getSingleSemRegistrationData(sapid,programBean.getSem());
								 
								if(sbean.getAcadYear()!=null && sbean.getAcadMonth()!=null && sbean.getAcadYear().equalsIgnoreCase(year) &&
									sbean.getAcadMonth().equalsIgnoreCase(month)	) {
									
									SalesforceStudentAccountBean sfdcOpp = new SalesforceStudentAccountBean();
									
									sfdcOpp.setAcadYear(sbean.getAcadYear());
									sfdcOpp.setAcadMonth(sbean.getAcadMonth());
									sfdcOpp.setSem(sbean.getSem());
									sfdcOpp.setSapid(sapid);
	  
									logger.info("Student:" + sapid+" Passed "+passedSubjectsBean.getCount()+"/"+ total_subject_to_clear+" Subjects"+" For Sem:"+programBean.getSem());
	
									if(passedSubjectsBean.getCount() == total_subject_to_clear ) {	
										passedSems.add(passedSubjectsBean);
										//set term cleared='Yes' in opportunities 
										sfdcOpp.setTermCleared("Yes");
										logger.info("Sem Cleared");
									}else if(semFailed && syncType.equalsIgnoreCase("passfail") ){//mark fail only if attempted for that sem
										sfdcOpp.setTermCleared("No");
										logger.info("Sem Failed");
									}
									
									if(sfdcOpp.getTermCleared().equalsIgnoreCase("Yes") || sfdcOpp.getTermCleared().equalsIgnoreCase("No")) {
										opportunitiesToUpdateInSalesforce.add(sfdcOpp);
										logger.info("adding in opportunitiesToUpdateInSalesforce");
									}
							    }
							}
						}
						/*//System.out.println("sfdcInput:"+sfdcInput);
						//System.out.println("sfdcOutput:"+sfdcOutput);
						
						//System.out.println("passedSems"+passedSems);*/
						//as per students program if number of sem=passedsems 
						//this student is all cleared
						////System.out.println(passedSems.size()+"--"+SemSubjectCountMap.size());
						 if((passedSems.size()>0) && (passedSems.size()==SemSubjectCountMap.size())) {
								////System.out.println("is passout");
								sfdcOutput.setPassout(true); 
								//for lateral and program changed students check passout from students table	
							}else if((student.getProgramChanged() !=null && student.getProgramChanged().equalsIgnoreCase("Y"))
									 || student.getIsLateral().equalsIgnoreCase("Y")) {
								if(student.getProgramCleared().equalsIgnoreCase("Y")) {
									logger.info("Student Passedout");
									sfdcOutput.setPassout(true);
								}
							}
							
							if("PG".equalsIgnoreCase(programType) || programPDDMType.equalsIgnoreCase(programType)) {
								if (sfdcInput.isSem1() != sfdcOutput.isSem1() || sfdcInput.isSem2() != sfdcOutput.isSem2()
										|| sfdcInput.isSem3() != sfdcOutput.isSem3() || sfdcInput.isSem4() != sfdcOutput.isSem4() 
										|| sfdcInput.isPassout() != sfdcOutput.isPassout()) {
									
									SObject sObject =populateSfdcObjectAccount(sfdcOutput);
									passFailListToUpdateBackInSalesforce.add(sObject);
								}
							}else { 
								if(sfdcOutput.isPassout() ){
									SObject sObject =populateSfdcObjectAccount(sfdcOutput);
									passFailListToUpdateBackInSalesforce.add(sObject);
								} 
							} 
					} catch (Exception e) {
						logger.info("couldnot update sfdc for student"+sfdcInput.getSapid()); 
					} 
				}
			}
			if("MBA (WX)".equalsIgnoreCase(programType)|| "MBA (X)".equalsIgnoreCase(programType) || "MSC".equalsIgnoreCase(programType)) {
				ArrayList<SalesforceStudentAccountBean> opportunities = sfdc.fetchSfdcOpportunitiesToUpdate(opportunitiesToUpdateInSalesforce);
				logger.info("opportunities to update in sfdc size:"+opportunities.size());
                sfdc.updateRecordsBackInSalesforce(populateSfdcObjectOpportunity(opportunities),"Opportunity",UPDATE_BATCH_SIZE);
			}
			logger.info("passFailListToUpdateBackInSalesforce size:"+passFailListToUpdateBackInSalesforce.size());
			sfdc.updateRecordsBackInSalesforce(passFailListToUpdateBackInSalesforce,"Account",UPDATE_BATCH_SIZE);
			return true;
		} 
		return false;
	}
	
	@RequestMapping(value = "/admin/checkPassfailMismatch", method = {RequestMethod.GET},produces = "application/json")
	public ResponseEntity<ResponseStudentPortalBean> checkPassfailMismatch( HttpServletRequest request) { 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseStudentPortalBean response = new ResponseStudentPortalBean(); 
		String programType = request.getParameter("programType");
		ArrayList<SObject> passFailListToUpdateBackInSalesforce = new ArrayList<SObject>();
		ArrayList<SalesforceStudentAccountBean> opportunitiesToUpdateInSalesforce = new ArrayList<SalesforceStudentAccountBean>();
		//fetch failed students from salesforce
		ArrayList<SalesforceStudentAccountBean> sfdcStudents = sfdc.getFailedStudentsFromsfdc(programType); 
		List<ResponseStudentPortalBean> accountMismatchStudents= new ArrayList<ResponseStudentPortalBean>();
		if(sfdcStudents.size()>0) {
			for (SalesforceStudentAccountBean sfdcInput : sfdcStudents) {
				ResponseStudentPortalBean mismatchStudent = new ResponseStudentPortalBean();
				mismatchStudent.setSapid(sfdcInput.getSapid());
				SalesforceStudentAccountBean sfdcOutput = new SalesforceStudentAccountBean();
				if(sfdcInput.getSapid().startsWith("77") && 
						sfdcInput.getSapid().length()==11
						){ 
					String sapid=sfdcInput.getSapid();
					sfdcOutput.setSapid(sapid);
					sfdcOutput.setId(sfdcInput.getId());
					sfdcOutput.setProgramType(programType);
					try {
						StudentStudentPortalBean student = sDao.getstudentData(sapid);  
							 
						//check student's program 
						//and create a map of sem & subjectscount per sem
						ArrayList<ProgramSubjectMappingStudentPortalBean> SemSubjectCountMap = serviceRequestDao.getSemSubjectCountMapping(student);
						
						//check passfail table to find if all subjects cleared 
						//for each sem
						ArrayList<PassFailBean> passedSems = new ArrayList<PassFailBean>();
						for (ProgramSubjectMappingStudentPortalBean programBean : SemSubjectCountMap) {
							PassFailBean passedSubjectsBean=null;	
							////System.out.println("programType"+programType);
							if("PG".equalsIgnoreCase(programType) || programPDDMType.equalsIgnoreCase(programType) ) {
								if(programPDDMType.equalsIgnoreCase(programType) )
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbaWx(programBean.getSem(),sapid);
								else
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCount(programBean.getSem(),sapid);
								if(passedSubjectsBean.getCount() == Integer.parseInt(programBean.getSubjectsCount()) ) {
									passedSems.add(passedSubjectsBean);
									switch (programBean.getSem()) {
									case "1":
										sfdcOutput.setSem1(true);
										break;
									case "2":
										sfdcOutput.setSem2(true);
										break;
									case "3":
										sfdcOutput.setSem3(true);
										break;
									case "4":
										sfdcOutput.setSem4(true);
										break;
									}
								}
								
							}else {
								int total_subject_to_clear = Integer.parseInt(programBean.getSubjectsCount());
								
								if("MBA (X)".equalsIgnoreCase(programType)) {
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbax(programBean.getSem(),sapid);
									
								}
								if("MBA (WX)".equalsIgnoreCase(programType)) {
									passedSubjectsBean = serviceRequestDao.getPassedSubjectCountForMbaWx(programBean.getSem(),sapid);
									
									if( "3".equalsIgnoreCase(programBean.getSem()) ||  "4".equalsIgnoreCase(programBean.getSem()) ) {
										total_subject_to_clear = 5;
									}
								}
								 if(passedSubjectsBean.getCount() == total_subject_to_clear ) {
									passedSems.add(passedSubjectsBean);
									//set term cleared='Yes' in opportunities
									StudentStudentPortalBean sbean = sDao.getSingleSemRegistrationData(sapid,programBean.getSem());
									SalesforceStudentAccountBean sfdcOpp = new SalesforceStudentAccountBean();
									sfdcOpp.setAcadYear(sbean.getAcadYear());
									sfdcOpp.setAcadMonth(sbean.getAcadMonth());
									sfdcOpp.setSem(sbean.getSem());
									sfdcOpp.setSapid(sapid);
									opportunitiesToUpdateInSalesforce.add(sfdcOpp);
									ResponseStudentPortalBean oppmismatchStudent = new ResponseStudentPortalBean();
									oppmismatchStudent.setStatus("Opportunity");
									oppmismatchStudent.setSapid(sapid); 
									oppmismatchStudent.setSem(sbean.getSem());
									oppmismatchStudent.setYear(sbean.getAcadYear());
									oppmismatchStudent.setMonth(sbean.getAcadMonth());
									accountMismatchStudents.add(oppmismatchStudent); 
								}
							}
						}
						if(passedSems.size()>0) {
							if(passedSems.size()==SemSubjectCountMap.size()) {
								////System.out.println("is passout");
								sfdcOutput.setPassout(true); 
								//for lateral and program changed students check passout from students table	
							}else if((student.getProgramChanged() !=null && student.getProgramChanged().equalsIgnoreCase("Y"))
									 || student.getIsLateral().equalsIgnoreCase("Y")) {
								if(student.getProgramCleared().equalsIgnoreCase("Y")) {
									////System.out.println("is passout");
									sfdcOutput.setPassout(true);
								}
							}
							
							if("PG".equalsIgnoreCase(programType) || "PDDM".equalsIgnoreCase(programType)) {
								if (sfdcInput.isSem1() != sfdcOutput.isSem1() || sfdcInput.isSem2() != sfdcOutput.isSem2()
										|| sfdcInput.isSem3() != sfdcOutput.isSem3() || sfdcInput.isSem4() != sfdcOutput.isSem4() 
										|| sfdcInput.isPassout() != sfdcOutput.isPassout()) {
									SObject sObject =populateSfdcObjectAccount(sfdcOutput);
									passFailListToUpdateBackInSalesforce.add(sObject); 
									mismatchStudent.setStatus("Account");
									accountMismatchStudents.add(mismatchStudent);
								}
							}else {  
								SObject sObject =populateSfdcObjectAccount(sfdcOutput);
								passFailListToUpdateBackInSalesforce.add(sObject); 
								mismatchStudent.setStatus("Account"); 
								accountMismatchStudents.add(mismatchStudent);
							}
						}
					} catch (NumberFormatException e) {
						//System.out.println("couldnot update sfdc for student"+sfdcInput.getSapid()); 
					} 
				}
			} 
		} 
		
		
	    response.setMessage("Mismatch: "+passFailListToUpdateBackInSalesforce.size()); 
	    List<String> details= new ArrayList<String>();
	    details.add("Account;accounts not synced:"+passFailListToUpdateBackInSalesforce.size());
	    details.add("Opportunity;opportunities not synced:"+opportunitiesToUpdateInSalesforce.size()); 
	    response.setStudents(accountMismatchStudents);
	    response.setDetailedMessage(details);
	    System.out.println("accountMismatchStudents"+accountMismatchStudents.size()); 
	    return new ResponseEntity<ResponseStudentPortalBean>(response,headers, HttpStatus.OK);
	}
	public SObject populateSfdcObjectAccount(SalesforceStudentAccountBean sfdcbean) {
		SObject sObject = new SObject();
		sObject.setType("Account");
		sObject.setId(sfdcbean.getId());
		if(sfdcbean.getProgramType().equalsIgnoreCase("PG") || sfdcbean.getProgramType().equalsIgnoreCase(programPDDMType)) {
	
			sObject.setField("Sem_1__c", sfdcbean.isSem1());
			sObject.setField("Sem_2__c", sfdcbean.isSem2());
			sObject.setField("Sem_3__c", sfdcbean.isSem3());
			sObject.setField("Sem_4__c", sfdcbean.isSem4());
		}
		sObject.setField("Pass_Out__c", sfdcbean.isPassout());
		return sObject;
	}
	public ArrayList<SObject> populateSfdcObjectOpportunity(ArrayList<SalesforceStudentAccountBean> list) {
		ArrayList<SObject> sobj = new ArrayList<SObject>();
		for(SalesforceStudentAccountBean bean :list) {
			if(bean.getOpportunityId() !=null) {
			SObject sObject = new SObject();
			sObject.setType("Opportunity");
			sObject.setId(bean.getOpportunityId());
			sObject.setField("Term_Cleared__c",bean.getTermCleared() );
			sobj.add(sObject);
			}
		}
		return sobj;
	}
	@RequestMapping(value = "/admin/schedulerApisDashboard", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView schedulerApisDashboard( HttpServletRequest request) {
	ModelAndView m = new ModelAndView("jsp/salesforceSyncDashboard");	
	ArrayList<SchedulerApisBean> apilist = sDao.getSalesforceSyncApiList();
	for(SchedulerApisBean bean:apilist) {   
		if(bean.getSource().equalsIgnoreCase("salesforce")) { 
			bean.setUrl(SALESFORCE_SERVER_PATH+bean.getUrl());
			bean.setStatusUrl(SALESFORCE_SERVER_PATH+bean.getStatusUrl());
		}else { 
			bean.setUrl(SERVER_PATH+bean.getUrl());
			bean.setStatusUrl(SERVER_PATH+bean.getStatusUrl());
		} 
	}
	String logsUrl=SALESFORCE_SERVER_PATH+"salesforce/getLogFiles";
	m.addObject("apilist",apilist);
	m.addObject("logsUrl",logsUrl);
 	return m;   
	}
	
	@RequestMapping(value = "/admin/runSchedularApiManually", method = {RequestMethod.GET, RequestMethod.POST},produces = "application/json",consumes="application/json")
	public ResponseEntity<ResponseStudentPortalBean> RunSyncApiManually( HttpServletRequest request,@RequestBody SchedulerApisBean bean) {
	RestTemplate restTemplate = new RestTemplate(); 
	ResponseStudentPortalBean response_jackson = null;
	ResponseEntity<String> responseBean = null;
	ResponseEntity<ResponseStudentPortalBean> responseEntity=null;
	try {
		String url =bean.getUrl();  
		responseBean = restTemplate.getForEntity(url, String.class);
	} catch (RestClientException e) {
		//e.printStackTrace(); 
	} 
	try {
		JsonObject jsonResponse =  new JsonParser().parse(responseBean.getBody()).getAsJsonObject();
		System.out.println(jsonResponse);
		ObjectMapper objectMapper = new ObjectMapper();
		response_jackson = objectMapper.readValue(jsonResponse.toString(), ResponseStudentPortalBean.class);	
		responseEntity =  new ResponseEntity<ResponseStudentPortalBean>(response_jackson, HttpStatus.OK);
			
	} catch (Exception e) {//e.printStackTrace();
	}
	
	return responseEntity;
  }
	
	@RequestMapping(value = "/admin/getPassedSemInfoForPDDM", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getPassedSemInfoForPDDM( HttpServletRequest request,@ModelAttribute SchedulerApisBean search) throws ConnectionException {
		//fetch failed students from salesforce
		loggerPFSyncPDDM.info("PDDM START"); 
		boolean response = fetchPassFailListAndUpdateInSFDC(search.getAcadYear(),search.getAcadMonth(),programPDDMType,search.getSyncType(), loggerPFSyncPDDM);
		if(response) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully Synced.");
		}
		SchedulerApisBean bean = new SchedulerApisBean();
		bean.setSyncType("Passed Sem List For PDDM"); 
		sDao.updateLastSyncedTime(bean);
		loggerPFSyncPDDM.info("PDDM END"); 
		ModelAndView mv = new ModelAndView("jsp/syncPassedSemList"); 
		mv.addObject("yearList", currentYearList);
		mv.addObject("bean",bean);
		return mv;     
	}

}
