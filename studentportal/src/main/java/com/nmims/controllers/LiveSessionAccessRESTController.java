package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.LiveSessionAccessBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.services.LiveSessionAccessService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;

@RestController
@RequestMapping("m")
public class LiveSessionAccessRESTController {
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	@Autowired
	LiveSessionAccessService liveSessionAccessService;

	@Autowired
	StudentService studentService;

	@Autowired
	ApplicationContext act;
	
	@Autowired
	RegistrationHelper registrationHelper;
	
	@Autowired
	StudentCourseMappingService studentCourseService;
	
	private static final Logger logger = LoggerFactory.getLogger(LiveSessionAccessRESTController.class);

	public static final String LIVE_SESSION_ACCESS_DATE = "01/Jul/2021";
	public static final String PGM_CODE_BBA = "BBA";
	public static final String PGM_CODE_BCOM = "B.Com";
	public static final String PGM_CODE_CP_WL = "CP-WL";
	public static final String PGM_CODE_CP_ME = "CP-ME";
	public static final String PGM_CODE_PD_WM = "PD - WM";
	public static final String PGM_CODE_PD_DM = "PD - DM";
	public static final String PGM_CODE_M_Sc_App_Fin = "M.Sc. (App. Fin.)";
	public static final String PGM_CODE_BBA_BA = "BBA-BA";
	
	public static final List<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList(PGM_CODE_BBA, PGM_CODE_BCOM, PGM_CODE_PD_WM, 
														PGM_CODE_PD_DM, PGM_CODE_M_Sc_App_Fin, PGM_CODE_CP_WL, PGM_CODE_CP_ME, PGM_CODE_BBA_BA));

	@PostMapping(value = "/getLiveSessionAccessList")
	public LiveSessionAccessBean getStudentPaidSessionSubjectList(@RequestBody StudentStudentPortalBean student) {
		LiveSessionAccessBean response = new LiveSessionAccessBean();
		ArrayList<String> currentSemPSSIdString = new ArrayList<String>();
		ArrayList<Integer> currentSemPSSId = new ArrayList<Integer>();
		ArrayList<Integer> subjectCodeId = new ArrayList<Integer>();
		List<Integer> liveSessionPssIdList = new ArrayList<Integer>();
		StudentStudentPortalBean studentRegistrationForAcademicSession = new StudentStudentPortalBean();
		
		/*boolean isNonPG_Program = Boolean.FALSE;*/
		boolean isFreeLiveSessionApplicable = Boolean.FALSE;
	
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());

		try {
			studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
			student.setSem(studentRegistrationForAcademicSession.getSem());
			student.setProgram(studentRegistrationForAcademicSession.getProgram());
		//	ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
		//	currentSemPSSIdString = pDao.getPSSIds(studentRegistrationForAcademicSession.getConsumerProgramStructureId(), studentRegistrationForAcademicSession.getSem() ,waivedOffSubjects);
			currentSemPSSIdString = studentCourseService.getPSSIds(student.getSapid(),studentRegistrationForAcademicSession.getYear(),studentRegistrationForAcademicSession.getMonth());
			
			currentSemPSSId = (ArrayList<Integer>) currentSemPSSIdString.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("getStudentPaidSessionSubjectList 1 : " + e.getClass() + " class " + e.getMessage());
		}

		try {
			/*isNonPG_Program = nonPG_ProgramList.contains(studentRegistrationForAcademicSession.getProgram());*/
			ArrayList<String> listOfMaterKeyHavingFreeLiveSession = liveSessionAccessService.getListOfFreeLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST);
			isFreeLiveSessionApplicable = listOfMaterKeyHavingFreeLiveSession.contains(studentRegistrationForAcademicSession.getConsumerProgramStructureId());
			logger.info("(Enrollment Year/Month, LiveSessionAccessDate, Program, NonPG_Program) : ("
												+ enrollDate + "," + LIVE_SESSION_ACCESS_DATE + ","
												+ studentRegistrationForAcademicSession.getProgram() + "," + isFreeLiveSessionApplicable);
			if (DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy, enrollDate, DateTimeHelper.FORMAT_ddMMMyyyy,
					LIVE_SESSION_ACCESS_DATE) || isFreeLiveSessionApplicable) {
				response.setIsLiveSessionAccessLogicApply(false);
				liveSessionPssIdList = currentSemPSSId;
			} else {
				response.setIsLiveSessionAccessLogicApply(true);
				liveSessionPssIdList = liveSessionAccessService.fetchPSSforLiveSessionAccess(student.getSapid(), studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
			}
			response.setCurrentSemPSSId(currentSemPSSId);
			response.setLiveSessionPssIdAccess(liveSessionPssIdList);
			if(currentSemPSSId.size() > 0) {
				response.setSubjectCodeId(studentCourseService.getSubjectCodeIdByPssId(currentSemPSSId.stream().map(String::valueOf).collect(Collectors.toSet())));
			}
			response.setStatus("success");
			return response;
		} catch (Exception e) {
			logger.error("getStudentPaidSessionSubjectList 2 : " + e.getClass() + " class " + e.getMessage());
			if(studentRegistrationForAcademicSession==null) {
				response.setIsLiveSessionAccessLogicApply(false);
				response.setStatus("success");
			}else {
				response.setStatus("error");
				response.setErrorMessage(e.getMessage());
			}
			response.setCurrentSemPSSId(currentSemPSSId);
			response.setLiveSessionPssIdAccess(liveSessionPssIdList);
			response.setSubjectCodeId(subjectCodeId);
			
		
			return response;
		}
	}

}
