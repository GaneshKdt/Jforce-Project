package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.Specialisation;
import com.nmims.beans.SpecialisationResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.SpecialisationService;

@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping("m")
public class SpecialisationRestControllers extends BaseController {
	

	@Autowired
	ApplicationContext act;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	SpecialisationService service;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST;
	
	public void setSuccess(HashMap<String, String> response, String successMessage){
		response.put("status", "Success");
		response.put("successMessage", successMessage);
	}
	
	public void setError(HashMap<String, String> response, String errorMessage){
		response.put("status", "Fail");
		response.put("error","true");
		response.put("errorMessage",errorMessage);
		
	}
	
	public void redirectToPortalApp(HttpServletResponse httpServletResponse) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"studentportal/");
		} catch (IOException e) {
			
		}
	}
	
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}

	}
	
	
	@PostMapping(path = "/getSpecialisationTypes")
	public ResponseEntity<SpecialisationResponseBean> mgetSpecialisationTypes(HttpServletRequest request) {
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		SpecialisationResponseBean response = new SpecialisationResponseBean();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ArrayList<Specialisation> specialisationList = sDao.getAllSpecialisation();
		
		if (specialisationList.isEmpty()) {
			response.setStatus("Fail");
			return new ResponseEntity<SpecialisationResponseBean>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}else {
			response.setStatus("Success");
			response.setSpecialisationList(specialisationList);
			return new ResponseEntity<SpecialisationResponseBean>(response, headers, HttpStatus.OK);
		}
	}
	
	@PostMapping(path = "/getSpecialisationSubjects")
	public ResponseEntity<SpecialisationResponseBean> mgetSpecialisationSubjects(HttpServletRequest request, 
			@RequestBody Specialisation specialisation) {
		
		//getting all specialization, core, pre-requsite subject and total subject count in specialization
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		SpecialisationResponseBean response = new SpecialisationResponseBean();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> subjectWithPrerequisite = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> coreSubject = new ArrayList<>();
		Integer maxTerm = sDao.getMaxSemForCuurentCycle(specialisation.getSapid());
		ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectCount = new ArrayList<>();

		ArrayList<ProgramSubjectMappingExamBean> commonSubjectList = new ArrayList<>();
		ArrayList<ProgramSubjectMappingExamBean> autoSelectSubject = new ArrayList<>();
		
		if (maxTerm == 0) {
			maxTerm = sDao.getMaxSem(specialisation.getSapid());
		}

		if( maxTerm == 2 || maxTerm == 3 ) {
			
			specialisationSubjectList = sDao.getAllSpecialisationSubjects();
			autoSelectSubject = sDao.getAutoSelectSubject();
			commonSubjectList = sDao.getCommonSubjects();
			
		}else if ( maxTerm > 3 ) 
			specialisationSubjectList = sDao.getAllSpecialisationSubjectsForSemFive();
		
		specialisationSubjectList.addAll(commonSubjectList);
		
		Collections.sort(specialisationSubjectList, new Comparator<ProgramSubjectMappingExamBean>() {
	        @Override 
	        public int compare(ProgramSubjectMappingExamBean a, ProgramSubjectMappingExamBean b) {
	            return Integer.parseInt( a.getSpecializationType() ) -  Integer.parseInt( b.getSpecializationType() ); // Ascending
	        }

	    }); 
		
		
		if (specialisationSubjectList.isEmpty()) {
			
			response.setStatus("Fail");
			return new ResponseEntity<SpecialisationResponseBean>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}else {
			
			try {
				subjectWithPrerequisite = sDao.getSpecialisationPrerequisite();
				coreSubject = sDao.getCoreSubject();
				specialisationSubjectCount = sDao.getSubjectCount();
			} catch (Exception e) {
				
			}

			response.setStatus( "Success" );
			response.setSubjectWithPrerequisite( subjectWithPrerequisite );
			response.setCoreSubject( coreSubject );
			response.setSpecialisationSubjectList( specialisationSubjectList );
			response.setSpecialisationSubjectCount( specialisationSubjectCount );
			response.setAutoSelectSubject( autoSelectSubject );
			response.setCommonSubject( commonSubjectList );
			
			return new ResponseEntity<SpecialisationResponseBean>(response, headers, HttpStatus.OK);
		}
	}
	
	@PostMapping(path = "/saveStudentSpecialisationSubjects")
	public ResponseEntity<HashMap<String, String>> msaveStudentSpecialisationSubjects(@RequestBody Specialisation specialisation) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new HashMap<>();
		MailSender mailer = (MailSender)act.getBean("mailer");
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		
		String errorMessage = "";
		int count = 0;
		int totalSubjectsForSpecialisation = 5;
		int totalSubjectsForSpecialisationSemFive = 2;
		Long srId = null;
		
		Integer maxTerm = sDao.getMaxSemForCuurentCycle(specialisation.getSapid());

		if (maxTerm == 0) {
			maxTerm = sDao.getMaxSem(specialisation.getSapid());
		}

		StudentExamBean student = sDao.getSingleStudentsData(specialisation.getSapid());
		LinkedList<ProgramSubjectMappingExamBean> SpecialisationSubjectList = specialisation.getSpecialisationSubjectList();
		Specialisation studentSpecilisation = new Specialisation();
		
		try {
			studentSpecilisation = sDao.getExistingSpecialisationDetails(specialisation.getSapid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if( maxTerm.equals(4) && SpecialisationSubjectList.size() != totalSubjectsForSpecialisationSemFive ) {

			response.put("status", "Fail");
			response.put("errorMessage", "Please select total "+totalSubjectsForSpecialisationSemFive+" Subjects to continue.");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			
		}else if ( SpecialisationSubjectList.size() != totalSubjectsForSpecialisation && 
				( maxTerm.equals(2) || maxTerm.equals(3) ) ) {

			response.put("status", "Fail");
			response.put("errorMessage", "Please select total "+totalSubjectsForSpecialisation+" Subjects to continue.");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
			
		}
		
		if (specialisation.isServiceRequest()) {
			// Update Specialization on SFDC
			if("PROD".equalsIgnoreCase(ENVIRONMENT)){
				errorMessage = salesforceHelper.updateSpecilisationDetails(specialisation);
			}
			
			if(errorMessage == null || "".equals(errorMessage)){
				// Insert Existing Specialization into History table
				boolean isInserted = sDao.insertExistingSpecialisationIntoHistory(studentSpecilisation);
				// Update Existing Specialization
				boolean isUpdated =  sDao.updateSpecilisationDetails(specialisation);
				
				// Insert Data in SR Request
				//adding check for payment applicable, if yes SR entry with details will already be made
				if( !sDao.isPaymentApplicableForSR( specialisation.getSapid() ) ) {
					
					srId = sDao.insertStudentSpecialisationDetailsinSR(specialisation);
				
					if (!isInserted || !isUpdated || srId.equals(null)) {
						setError(response, "Error While Updating Extising Specilisation Details.");
						return new ResponseEntity<>(response,headers, HttpStatus.OK);
					}
					
				}
				
			}else {
				setError(response, "Error While Updating Specilisation Details on SFDC.");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
		}
		
		//Checked student is already selected Specialization Subjects or Not for next semester
		Integer subjectSelectedForTerm = maxTerm +1;
		ArrayList<String> alreadyAppiledList = sDao.getSpecialisationTimeBoundIds(specialisation.getSapid());

		if (alreadyAppiledList.size() > 0) {
			//Insert Existing Timebound ids Entries into history table & delete from old table
			boolean isUpdated = sDao.insertExistingTimeBoundMappingIntoHistory(specialisation.getSapid(), alreadyAppiledList);
			if (isUpdated) {
			}
		}
		
		for (ProgramSubjectMappingExamBean programSubjectMappingBean : SpecialisationSubjectList) {
			specialisation.setSubject(programSubjectMappingBean.getSubject());
			specialisation.setProgram_sem_subject_id(String.valueOf(programSubjectMappingBean.getId()));
			specialisation.setTerm(programSubjectMappingBean.getSem());
			specialisation.setTimeBoundId(programSubjectMappingBean.getTimeBoundId());
			
			
			//Insert New TimeBound Student Mapping
			boolean isInserted = sDao.insertTimeBoundStudentMapping_V2( specialisation, programSubjectMappingBean );
			if (isInserted) {
				count++;
			}else{
				setError(response, "Error While Insert timebound id");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
		}
		
		if ( count != 0 && count == totalSubjectsForSpecialisation  && 
				( maxTerm.equals(2) || maxTerm.equals(3) ) ) {
			
			if (specialisation.isServiceRequest()) {
				ServiceRequestBean srBean = sDao.getSingleSR(srId);
				mailer.sendSREmail(student, srBean);
			}
			
			mailer.sendSpecialisationSubjectSummary(student ,SpecialisationSubjectList, subjectSelectedForTerm);
			setSuccess(response, "Specialisation Details Saved Successfully.");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
			
		}else if ( count != 0 && count == totalSubjectsForSpecialisationSemFive  && maxTerm.equals(4) ){

			if (specialisation.isServiceRequest()) {
				ServiceRequestBean srBean = sDao.getSingleSR(srId);
				mailer.sendSREmail(student, srBean);
			}
			
			mailer.sendSpecialisationSubjectSummary(student ,SpecialisationSubjectList, subjectSelectedForTerm);
			setSuccess(response, "Specialisation Details Saved Successfully.");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
			
		}else {
			
			setError(response, "Specialisation Details not Saved.");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
			
		}
		
	}
	
	@PostMapping(path = "/isSpecialisationDone")
	public ResponseEntity<SpecialisationResponseBean> isSpecialisationDone(@RequestBody Specialisation specialisation) {
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		SpecialisationResponseBean response = new SpecialisationResponseBean();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		
		Integer maxTerm = sDao.getMaxSemForCuurentCycle(specialisation.getSapid());
		
		if (maxTerm == 0) {
			maxTerm = sDao.getMaxSem(specialisation.getSapid());
		}
		
		ArrayList<Specialisation> appiledSpecialisation = service.isSpecialisationDone(specialisation.getSapid(), maxTerm);
		Specialisation studentSpecilisation = new Specialisation();
		
		try {
			studentSpecilisation = sDao.getExistingSpecialisationDetails(specialisation.getSapid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setMaxTerm(maxTerm);
		
		switch ( maxTerm ) {
			case 2:
				if (appiledSpecialisation.size() > 0) {
					response.setStatus("Applied");
					response.setMessage("Student Already Applied for Specialisation.");
					response.setSpecialisationList(appiledSpecialisation);
				}else {
					response.setStatus("Not Applied");
					response.setMessage("Not Yet Applied for Specialisation.");
				}
				response.setSpecialisation(studentSpecilisation);
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
				
			case 3:

				if (appiledSpecialisation.size() > 0) {
					response.setStatus("Applied");
					response.setMessage("Student Already Applied for Specialisation.");
					response.setSpecialisationList(appiledSpecialisation);
				}else {
					response.setStatus("Not Applied");
					response.setMessage("Not Yet Applied for Specialisation.");
				}
				response.setSpecialisation(studentSpecilisation);
				response.setTerm3SelectedSubjects( service.getSpecialisationDoneSubjects(specialisation.getSapid(), maxTerm) );
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
				
			case 4:

				if (appiledSpecialisation.size() > 0) {
					response.setStatus("Applied");
					response.setMessage("Student Already Applied for Specialisation.");
					response.setSpecialisationList(appiledSpecialisation);
				}else {
					response.setStatus("Not Applied");
					response.setMessage("Not Yet Applied for Specialisation.");
				}
				response.setSpecialisation(studentSpecilisation);
				response.setTerm4SelectedSubjects( service.getSpecialisationDoneSubjects(specialisation.getSapid(), maxTerm) );
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
	
			default:
				response.setStatus("Not Applicable");
				response.setMessage("Student Already Applied for Specialisation.");
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		
	}
	
	@PostMapping(path = "/checkExistingSpecialisation")
	public ResponseEntity<Specialisation> checkExistingSpecialisation(@RequestBody Specialisation specialisation) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Specialisation response = new Specialisation();
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		try {
			response = sDao.getExistingSpecialisationDetails(specialisation.getSapid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
		
	}
	
	@GetMapping(path = "/isServiceRequestRaised")
	public ResponseEntity<HashMap<String, String>> isServiceRequestRaised(@RequestParam String sapId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new HashMap<String, String>();	
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		String isServiceRequestRaised = sDao.isServiceRequestRaisedForSpecialisation(sapId);
		response.put("isServiceRequestRaised",isServiceRequestRaised);		
		
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/isSpecialisationApplicable")
	public ResponseEntity<HashMap<String, Boolean>> isSpecialisationApplicable(@RequestBody StudentExamBean student){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, Boolean> response = new HashMap<String, Boolean>();
		
		if(!student.getProgram().equalsIgnoreCase("MBA - WX")) {
			response.put("isSpecialisationSRApplicable", false);
			return new ResponseEntity<HashMap<String, Boolean>>(response, headers, HttpStatus.OK);
		}
		
		SpecialisationDAO sDao = (SpecialisationDAO) act.getBean("specialisationDao");
		Integer maxTerm = sDao.getMaxSemForCuurentCycle(student.getSapid());
		Integer masterKey = sDao.getStudentMasterKey(student.getSapid());
		
		//If Registered in Current Acad Year/Month
		if (maxTerm != 0) {
			boolean isApplicable;
			if (maxTerm == 4) {
				 isApplicable = sDao.isSpecialisationApplicableTerm5(student, true);
				
			}else if ((maxTerm == 2 && masterKey == 160) || (maxTerm == 3 && masterKey == 111) || (maxTerm == 2 && masterKey == 111)){
				 isApplicable = sDao.isSpecialisationApplicable(student, true);
				
			}else {
				isApplicable = false;
			}
			response.put("isSpecialisationApplicable", isApplicable);
			
			
			if (maxTerm == 2) {
				boolean isPaymentApplicable = sDao.isPaymentApplicableForSR ( student.getSapid() );
				response.put("isSpecialisationSRPaymentApplicable", isPaymentApplicable);
				response.put("isSpecialisationSRApplicable", true);
			} else {
				response.put("isSpecialisationSRApplicable", false);
			}
			
			return new ResponseEntity<HashMap<String, Boolean>>(response, headers, HttpStatus.OK);
		//If Not Registered in Current Acad Year/Month then 
		} else {
			
//			boolean isApplicable = sDao.isSpecialisationApplicable(student, false);
//			response.put("isSpecialisationApplicable", isApplicable);
			
			maxTerm = sDao.getMaxSem(student.getSapid());
			boolean isApplicable;
			if (maxTerm==4) {
				
				 isApplicable = sDao.isSpecialisationApplicableTerm5(student, false);
				
			}else {
				 isApplicable = sDao.isSpecialisationApplicable(student, false);
				
			}
			response.put("isSpecialisationApplicable", isApplicable);
			
			if (maxTerm == 2) {
				boolean isPaymentApplicable = sDao.isPaymentApplicableForSR ( student.getSapid() );
				response.put("isSpecialisationSRPaymentApplicable", isPaymentApplicable);
				response.put("isSpecialisationSRApplicable", true);
			} else {
				response.put("isSpecialisationSRApplicable", false);
			}
			return new ResponseEntity<HashMap<String, Boolean>>(response, headers, HttpStatus.OK);
		}
		
		
	}
	
	@PostMapping(value = "/getStudentElectiveDetailsForSR")
	public ResponseEntity< Specialisation > getStudentElectiveDetailsForSR( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		SpecialisationDAO dao = (SpecialisationDAO) act.getBean("specialisationDao");
		Specialisation response = new Specialisation();
		
		response = dao.getSpecializationDetailsForStudent( specialisation.getSapid() );
		
		return new ResponseEntity< Specialisation >(response, headers, HttpStatus.OK);
		
	}
	
	@PostMapping(path = "/getBatchsForYearMonth")
	public ResponseEntity<List<Specialisation>> getBatchsForYearMonth( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<Specialisation> response = new ArrayList<>();
		
		try {
			response = service.getBatchsForYearMonth( specialisation.getAcadYear(), specialisation.getAcadMonth() );
			return new ResponseEntity< List<Specialisation> >(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity< List<Specialisation> >(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/getSubjectForBatch")
	public ResponseEntity<List<ProgramSubjectMappingExamBean>> getSubjectToMapForSpecialization( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<ProgramSubjectMappingExamBean> response = new ArrayList<>();
		
		try {
			response = service.getSubjectForBatch(specialisation.getBatchId(), specialisation.getAcadYear(), 
					specialisation.getAcadMonth(), specialisation.getSem());
			return new ResponseEntity< List<ProgramSubjectMappingExamBean> >(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity< List<ProgramSubjectMappingExamBean> >(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/checkIfSpecializationDetailsExists")
	public ResponseEntity<Boolean> checkIfSpecializationDetailsExists( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		Boolean exists = Boolean.FALSE;
		
		try {
			exists = service.checkIfSpecializationDetailsExists(specialisation.getAcadYear(), specialisation.getAcadMonth(), specialisation.getSem());
			return new ResponseEntity<Boolean>(exists, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity< Boolean>(exists, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/getSpecializationDetails")
	public ResponseEntity<List<Specialisation>> getSpecializationDetails( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<Specialisation> response = new ArrayList<>();
		
		try {
			response = service.getSpecializationDetails(specialisation.getAcadYear(), specialisation.getAcadMonth(), specialisation.getSem());
			return new ResponseEntity<List<Specialisation>>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Specialisation>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/deleteSpecializationInstance")
	public ResponseEntity<Specialisation> deleteSpecializationInstance( @RequestBody Specialisation specialisation ){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			service.deleteSpecializationInstance(specialisation.getAcadYear(), specialisation.getAcadMonth(), 
					specialisation.getSem(), specialisation.getTimeBoundId(), specialisation.getSpecialization());
			specialisation.setStatus(true);
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			specialisation.setStatus(false);
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/saveSpecializationDetails")
	public ResponseEntity<Specialisation> saveSpecializationDetails(@RequestBody List<Specialisation> details){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		Specialisation response = new Specialisation();
		
		try {
			service.saveSpecializationDetails(details);
			return new ResponseEntity<Specialisation>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Specialisation>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/fetchBlockDetails")
	public ResponseEntity<HashMap<Integer, List<Specialisation>>> fetchBlockDetails(){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		HashMap<Integer, List<Specialisation>> response = new HashMap<>();
		
		try {
			response = service.fetchBlockDetails();
			return new ResponseEntity<HashMap<Integer, List<Specialisation>>>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<HashMap<Integer, List<Specialisation>>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/fetchBlockSequenceDetails")
	public ResponseEntity<List<Specialisation>> fetchBlockSequenceDetails( @RequestBody Specialisation specialisation ){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<Specialisation> response = new ArrayList<>();
		
		try {
			response = service.fetchBlockSequenceDetails(specialisation.getSem());
			return new ResponseEntity<List<Specialisation>>(response, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Specialisation>>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/fetchStudentDetailsForSpecialization")
	public ResponseEntity<Specialisation> fetchStudentDetailsForSpecialization( @RequestBody Specialisation specialisation ){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			specialisation = service.fetchStudentDetailsForSpecialization(specialisation.getSapid());
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/saveSpecializationForStudent")
	public ResponseEntity<Specialisation> saveSpecializationForStudent( @RequestBody List<Specialisation> specialisationList ){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		Specialisation specialisation = new Specialisation();
		
		try {
			service.saveSpecializationForStudent(specialisationList);
			specialisation.setStatus(true);
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			specialisation.setStatus(false);
			return new ResponseEntity<Specialisation>(specialisation, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@PostMapping(path = "/fetchSpecializationIfAlreadyOptedin")
	public ResponseEntity<List<Specialisation>> fetchSpecializationIfAlreadyOptedin( @RequestBody Specialisation userDetails ){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<Specialisation> optedinSpecialization = new ArrayList<>();
		
		try {
			optedinSpecialization = service.fetchOptedinSpecialization(userDetails.getUserId());
			return new ResponseEntity<List<Specialisation>>(optedinSpecialization, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<List<Specialisation>>(optedinSpecialization, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
