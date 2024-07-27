/**
 * 
 */
package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.MDMSubjectCodeBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.services.MDMSubjectCodeService;

/**
 * @author vil_m    
 *
 */
@Controller
@RequestMapping("/admin")
public class MDMSubjectCodeController extends BaseController {
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	private static final String N_ZERO = "0";
	private static final String N_ONE = "1";
	private static final String N_TWO = "2";
	private static final String N_THREE = "3";
	private static final String N_FOUR = "4";
	private static final String N_FIVE = "5";
	private static final String N_SIX = "6";
	private static final String N_SEVEN = "7";
	private static final String N_EIGHT = "8";
	private static final String N_NINE = "9";
	private static final String N_TEN = "10";
	
	private static final String KEY_Y = "Y";
	private static final String KEY_N = "N";
	private static final String KEY_NA = "NA";
	private static final String V_YES = "Yes";
	private static final String V_NO = "No";
	private static final String V_NA = "Not Applicable";
	private static final String V_BEST = "Best";
	private static final String V_LATEST = "Latest";
	private static final String V_REGULAR = "Regular";
	private static final String V_TIMEBOUND = "TimeBound";
	
	//private static final String S_OPENB = "{"; 
	//private static final String S_CLOSEB = "}"; 
	//private static final String S_COLON = ":"; 
	//private static final String S_COMMA = ","; 
	//private static final String MSG_STATUS = "Select Status"; 
	//private static final String S_ESC_QUOTES = "\"";

	private static final List<String> semesterList;
	//private static final String semesterJSON;//Semester cannot be edited.
	private static final Map<String, String> yesNoMap;
	private static final Map<String, String> yesNoNAMap;
	private static final Map<String, String> bestLatestMap;
	private static final Map<String, String> bestLatestNAMap;
	private static final Map<String, String> numberYesNoMap;
	private static final Map<String, String> regTimeMap;
	
	public static String ALPHANUMERIC_REGEX = "[\\d|[a-zA-Z]]+";
	
	public static final Logger logger = LoggerFactory.getLogger(MDMSubjectCodeController.class);
	
	private static final String edit_button = "edit";
	
	private static final String delete_button = "delete";
	
	private static final String action_button = "action";

	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	MDMSubjectCodeService mdmSubjectCodeService;
	
	@Autowired
	ConfigurationController configController;
	
	@Value("#{'${SUBJECT_CREDITS}'.split(',')}")
	private List<String> SUBJECT_CREDITS;

	static {
		semesterList = new ArrayList<String>(Arrays.asList(N_ONE, N_TWO, N_THREE, N_FOUR, N_FIVE, N_SIX, N_SEVEN, N_EIGHT, N_NINE, N_TEN));
		/*semesterJSON = "'" + S_OPENB + S_ESC_QUOTES + N_ONE + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_ONE + S_ESC_QUOTES + S_COMMA 
				 + S_ESC_QUOTES + N_TWO + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_TWO + S_ESC_QUOTES + S_COMMA 
				 + S_ESC_QUOTES + N_THREE + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_THREE + S_ESC_QUOTES + S_COMMA 
				 + S_ESC_QUOTES + N_FOUR  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_FOUR + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_FIVE  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_FIVE + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_SIX  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_SIX + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_SEVEN  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_SEVEN + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_EIGHT  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_EIGHT + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_NINE  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_NINE + S_ESC_QUOTES + S_COMMA
				 + S_ESC_QUOTES + N_TEN  + S_ESC_QUOTES + S_COLON + S_ESC_QUOTES + N_TEN + S_ESC_QUOTES 
				 + S_CLOSEB + "'";*/

		yesNoMap = new LinkedHashMap<String, String>();
		yesNoMap.put(KEY_Y, V_YES);
		yesNoMap.put(KEY_N, V_NO);

		yesNoNAMap = new LinkedHashMap<String, String>();
		yesNoNAMap.put(KEY_Y, V_YES);
		yesNoNAMap.put(KEY_N, V_NO);
		yesNoNAMap.put(KEY_NA, V_NA);

		bestLatestMap = new LinkedHashMap<String, String>();
		bestLatestMap.put(V_BEST, V_BEST);
		bestLatestMap.put(V_LATEST, V_LATEST);

		bestLatestNAMap = new LinkedHashMap<String, String>();
		bestLatestNAMap.put(V_BEST, V_BEST);
		bestLatestNAMap.put(V_LATEST, V_LATEST);
		bestLatestNAMap.put(KEY_NA, V_NA);
		
		numberYesNoMap = new LinkedHashMap<String, String>();
		numberYesNoMap.put(N_ONE, V_YES);
		numberYesNoMap.put(N_ZERO, V_NO);
		
		regTimeMap = new LinkedHashMap<String, String>();
		regTimeMap.put(V_REGULAR, V_REGULAR);
		regTimeMap.put(V_TIMEBOUND, V_TIMEBOUND);
	}

	public MDMSubjectCodeController() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/uploadMDMSubjectCode", method = { RequestMethod.POST })
	public ModelAndView uploadMDMSubjectCode(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeBean mdmSubjectCodeBean2) {
		logger.info("Entering uploadMDMSubjectCode");
		Boolean isUploaded = Boolean.FALSE;
		MDMSubjectCodeBean codeBean = null;
		String userId = null;
		ModelAndView mav = null;
		Map<String,Object> dataPrepared = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				mav = new ModelAndView("mdm/subjectCodeForm");
				
				if(isBlank(mdmSubjectCodeService.validateFileExtension(mdmSubjectCodeBean2.getFileData().getOriginalFilename()))) {
					request.setAttribute(KEY_ERROR, "Received file does not have a standard excel extension!");
				} else {
					userId = (String) request.getSession().getAttribute("userId");
					isUploaded = mdmSubjectCodeService.processFile(mdmSubjectCodeBean2, userId);
					if(isUploaded) {
						mdmSubjectCodeBean2 = new MDMSubjectCodeBean();
					} else {
						if(null != mdmSubjectCodeBean2.getStatus()) {
							request.setAttribute(mdmSubjectCodeBean2.getStatus(), mdmSubjectCodeBean2.getMessage());
						}
					}
				}
			}
		} catch(Exception ex) {
			request.setAttribute(KEY_ERROR, ex.getMessage());
		} finally {
			dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCode();
			request.setAttribute("specializationTypeNameList", dataPrepared.get("specializationTypeNameList"));
			request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
			request.setAttribute("regTimeMap", MDMSubjectCodeController.regTimeMap);
			request.setAttribute("numberYesNoMap", MDMSubjectCodeController.numberYesNoMap);
			request.setAttribute("subjectCodeList", dataPrepared.get("subjectCodeList"));
			codeBean = new MDMSubjectCodeBean();
			mav.addObject("mdmSubjectCodeBean", codeBean);
			mav.addObject("mdmSubjectCodeBean2", mdmSubjectCodeBean2);
		}
		if(isUploaded) {
			if(null == request.getAttribute(KEY_ERROR)) {
				request.setAttribute(KEY_SUCCESS, "File Uploaded Successfully.");
			}
		}
		return mav;
	}

	@RequestMapping(value = "/mdmSubjectCodeForm", method = { RequestMethod.GET })
	public ModelAndView displayMDMSubjectCodeForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeBean mdmSubjectCodeBean) {
		logger.info("Entering displayMDMSubjectCodeForm");
		MDMSubjectCodeBean codeBean = null;
		MDMSubjectCodeBean uploadBean = null;
		Map<String,Object> dataPrepared = null;
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				mav = new ModelAndView("mdm/subjectCodeForm");
				dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCode();
				request.setAttribute("specializationTypeNameList", dataPrepared.get("specializationTypeNameList"));
				request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
				request.setAttribute("regTimeMap", MDMSubjectCodeController.regTimeMap);
				request.setAttribute("numberYesNoMap", MDMSubjectCodeController.numberYesNoMap);
				request.setAttribute("subjectCodeList", dataPrepared.get("subjectCodeList"));
				codeBean = new MDMSubjectCodeBean();
				uploadBean = new MDMSubjectCodeBean();
				mav.addObject("mdmSubjectCodeBean", codeBean);
				mav.addObject("mdmSubjectCodeBean2", uploadBean);
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : displayMDMSubjectCodeForm : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}	
		return mav;
	}

	@RequestMapping(value = "/saveMDMSubjectCodeForm", method = { RequestMethod.POST } )
	public ModelAndView saveMDMSubjectCodeForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeBean mdmSubjectCodeBean) {
		logger.info("Entering saveMDMSubjectCodeForm");
		boolean isSuccess = Boolean.FALSE;
		String userId = null;
		Map<String,Object> dataPrepared = null;
		ModelAndView mav = null;
		boolean goSave = Boolean.TRUE;
		MDMSubjectCodeBean uploadBean = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				mav = new ModelAndView("mdm/subjectCodeForm");
				if (null != mdmSubjectCodeBean) {
					if(mdmSubjectCodeBean.getActive().equalsIgnoreCase(KEY_Y)) {
						if (isBlank(mdmSubjectCodeBean.getSubjectcode()) || isBlank(mdmSubjectCodeBean.getSubjectname())
								|| isBlank(mdmSubjectCodeBean.getIsProject())
								|| isBlank(mdmSubjectCodeBean.getSpecializationType())
								|| isBlank(mdmSubjectCodeBean.getStudentType())
								|| isIntegerBlank(mdmSubjectCodeBean.getSessionTime())) {
							request.setAttribute(KEY_ERROR, "Cannot be made active as fields are missing value.");
							goSave = Boolean.FALSE;
						}
					}
					if(goSave) {
						if(!applyRegExp(ALPHANUMERIC_REGEX, mdmSubjectCodeBean.getSubjectcode())) {
							request.setAttribute(KEY_ERROR, "Special Characters not allowed in SubjectCode.");
							goSave = Boolean.FALSE;
						}
					}
					if(goSave) {
						userId = (String) request.getSession().getAttribute("userId");
						isSuccess = mdmSubjectCodeService.saveMDMSubjectCode(mdmSubjectCodeBean, userId);
						setMessageInRequest(request, mdmSubjectCodeBean);
						if (isSuccess) {
							mdmSubjectCodeBean = new MDMSubjectCodeBean();
						}
					}
				}
				uploadBean = new MDMSubjectCodeBean();
				dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCode();
				request.setAttribute("specializationTypeNameList", dataPrepared.get("specializationTypeNameList"));
				request.setAttribute("regTimeMap", MDMSubjectCodeController.regTimeMap);
				request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
				request.setAttribute("numberYesNoMap", MDMSubjectCodeController.numberYesNoMap);
	
				request.setAttribute("subjectCodeList", dataPrepared.get("subjectCodeList"));
				mav.addObject("mdmSubjectCodeBean", mdmSubjectCodeBean);
				mav.addObject("mdmSubjectCodeBean2", uploadBean);
				mav.addObject("subjectCodeList", dataPrepared.get("subjectCodeList"));
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : saveMDMSubjectCodeForm : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	public static boolean applyRegExp(String regExp, String arg) {
		Boolean isMatched = Boolean.FALSE;
		isMatched = Pattern.matches(regExp, arg);
		return isMatched;
	}

	@RequestMapping(value = "/downloadMDMSubjectCode", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadMDMSubjectCode(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering downloadMDMSubjectCode");
		ModelAndView mav = null;
		List<MDMSubjectCodeBean> subjectCodeList = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				subjectCodeList = mdmSubjectCodeService.fetchMDMSubjectCodeList();
				if(null != subjectCodeList) {
					logger.info("MDMSubjectCodeController :  downloadMDMSubjectCode : Total Exported : "+ subjectCodeList.size());
				}
				mav = new ModelAndView("downloadMDMSubjectCodeReport", "mdmSubjectCodeList", subjectCodeList);
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : downloadMDMSubjectCode : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}

	@RequestMapping(value = "/checkIfDuplicateMDMSubjectCode", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseBean> checkIfDuplicateMDMSubjectCode(
			@RequestBody MDMSubjectCodeBean mdmSubjectCodeBean) {
		logger.info("Entering checkIfDuplicateMDMSubjectCode");
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");

			boolean isDuplicate = mdmSubjectCodeService.checkIfDuplicateMDMSubjectCode(mdmSubjectCodeBean.getSubjectcode());
			if (isDuplicate) {
				responseBean.setStatus(KEY_ERROR);
				responseBean.setMessage("Entered SubjectCode already exists!");
			} else {
				responseBean.setStatus(KEY_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("MDMSubjectCodeController : checkIfDuplicateMDMSubjectCode : " + e.getMessage());
			
			
			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Error checking for SubjectCode, if duplicate.");
		}
		return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
	}

	/*@RequestMapping(value = "/updateMDMSubjectCode", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<HashMap<String, String>> updateMDMSubjectCode(@ModelAttribute MDMSubjectCodeBean bean) {
		HashMap<String, String> responseMap = null;
		HttpHeaders headers = null;
		try {
			responseMap = new HashMap<String, String>();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
	
			if (null != bean) {
				if (bean.getActive().equalsIgnoreCase(KEY_Y)) {
					if (isBlank(bean.getSubjectcode()) || isBlank(bean.getSubjectname()) || isBlank(bean.getIsProject())
							|| isIntegerBlank(bean.getSifySubjectCode()) || isBlank(bean.getSpecializationType())
							|| isBlank(bean.getStudentType())) {
						responseMap.put(KEY_ERROR, "Cannot be made active as fields are missing value.");
						return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
					}
				}
				mdmSubjectCodeService.updateMDMSubjectCode(bean);
				responseMap.put(bean.getStatus(), bean.getMessage());
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : updateMDMSubjectCode : " + e.getMessage());
			
			responseMap.put(KEY_ERROR, e.getMessage());
		}
		return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
	}*/
	
	@RequestMapping(value = "/updateOrDeleteMDMSubjectCode", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<HashMap<String, String>> updateOrDeleteMDMSubjectCode(@ModelAttribute MDMSubjectCodeBean bean, HttpServletRequest request) {
		logger.info("Entering updateOrDeleteMDMSubjectCode");
		HashMap<String, String> responseMap = null;
		HttpHeaders headers = null;
		try {
			responseMap = new HashMap<String, String>();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
	
			if (null != bean) {
				if (null !=bean.getActive() && bean.getActive().equalsIgnoreCase(KEY_Y)) {
					if (isBlank(bean.getSubjectcode()) || isBlank(bean.getSubjectname()) || isBlank(bean.getIsProject())
							|| isBlank(bean.getSpecializationType())
							|| isBlank(bean.getStudentType())
							|| isIntegerBlank(bean.getSessionTime())) {
						responseMap.put(KEY_ERROR, "Cannot be made active as fields are missing value.");
						return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
					}
				}
				if(null != bean.getSubjectcode() && !applyRegExp(ALPHANUMERIC_REGEX, bean.getSubjectcode())) {
					responseMap.put(KEY_ERROR, "Special Characters not allowed in SubjectCode.");
					return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
				}
			}
			if(isBlank(bean.getSubjectcode()) && isBlank(bean.getSubjectname())) {
				mdmSubjectCodeService.deleteMDMSubjectCode(bean);
			} else {
				String userId = (String) request.getSession().getAttribute("userId");
				bean.setLastModifiedBy(userId);
				mdmSubjectCodeService.updateMDMSubjectCode(bean);
			}
			responseMap.put(bean.getStatus(), bean.getMessage());
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : updateOrDeleteMDMSubjectCode : " + e.getMessage());
			
			responseMap.put(KEY_ERROR, e.getMessage());
		}
		return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
	}

	//MDMSubjectCodeMapping
	@RequestMapping(value = "/uploadMDMSubjectCodeMapping", method = { RequestMethod.POST })
	public ModelAndView uploadMDMSubjectCodeMapping(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean2) {
		logger.info("Entering uploadMDMSubjectCodeMapping");
		MDMSubjectCodeMappingBean codeBean = null;
		String userId = null;
		Boolean isUploaded = Boolean.FALSE;
		Map<String,Object> dataPrepared = null;
		ModelAndView mav = null;
		
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				mav = new ModelAndView("mdm/subjectCodeMappingForm");
				
				if(isBlank(mdmSubjectCodeService.validateFileExtension(mdmSubjectCodeMappingBean2.getFileData().getOriginalFilename()))) {
					request.setAttribute(KEY_ERROR, "Received file does not have a standard excel extension!");
				} else {
					userId = (String) request.getSession().getAttribute("userId");
					isUploaded = mdmSubjectCodeService.processFile(mdmSubjectCodeMappingBean2, userId);
					if(isUploaded) {
						mdmSubjectCodeMappingBean2 = new MDMSubjectCodeMappingBean();
					} else {
						if(null != mdmSubjectCodeMappingBean2.getStatus()) {
							request.setAttribute(mdmSubjectCodeMappingBean2.getStatus(), mdmSubjectCodeMappingBean2.getMessage());
						}
					}
				}
			}
		} catch(Exception ex) {
			request.setAttribute(KEY_ERROR, ex.getMessage());
		} finally {
			dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCodeMapping();
			request.setAttribute("subjectCodeMappingList", dataPrepared.get("subjectCodeMappingList"));
			request.setAttribute("consumerTypeMap", dataPrepared.get("consumerTypeMap"));
			request.setAttribute("subjCodeMap", dataPrepared.get("subjCodeMap"));
			//request.setAttribute("subjCodeString", dataPrepared.get("subjCodeString"));
			request.setAttribute("semesterList", MDMSubjectCodeController.semesterList);
			//request.setAttribute("semesterJSON", MDMSubjectCodeController.semesterJSON);
			request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
			request.setAttribute("yesNoNAMap", MDMSubjectCodeController.yesNoNAMap);
			request.setAttribute("bestLatestMap", MDMSubjectCodeController.bestLatestMap);
			request.setAttribute("bestLatestNAMap", MDMSubjectCodeController.bestLatestNAMap);
			codeBean = new MDMSubjectCodeMappingBean();
			mav.addObject("mdmSubjectCodeMappingBean", codeBean);
			mav.addObject("mdmSubjectCodeMappingBean2", mdmSubjectCodeMappingBean2);
		}
		if(isUploaded) {
			if(null == request.getAttribute(KEY_ERROR)) {
				request.setAttribute(KEY_SUCCESS, "File Uploaded Successfully.");
			}
		}
		return mav;
	}
	
	@RequestMapping(value = "/mdmSubjectCodeMappingForm", method = { RequestMethod.GET })
	public ModelAndView displayMDMSubjectCodeMappingForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		logger.info("Entering displayMDMSubjectCodeMappingForm");
		MDMSubjectCodeMappingBean codeBean = null;
		MDMSubjectCodeMappingBean uploadBean = null;
		Map<String,Object> dataPrepared = null;
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
				return mav;
			} else {
				mav = new ModelAndView("mdm/subjectCodeMappingForm");
				codeBean = new MDMSubjectCodeMappingBean();
				uploadBean =  new MDMSubjectCodeMappingBean();
				dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCodeMapping();
	
				request.setAttribute("subjectCodeMappingList", dataPrepared.get("subjectCodeMappingList"));
				request.setAttribute("consumerTypeMap", dataPrepared.get("consumerTypeMap"));
				request.setAttribute("subjCodeMap", dataPrepared.get("subjCodeMap"));
				//request.setAttribute("subjCodeString", dataPrepared.get("subjCodeString"));
				request.setAttribute("semesterList", MDMSubjectCodeController.semesterList);
				//request.setAttribute("semesterJSON", MDMSubjectCodeController.semesterJSON);
				request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
				request.setAttribute("yesNoNAMap", MDMSubjectCodeController.yesNoNAMap);
				request.setAttribute("bestLatestMap", MDMSubjectCodeController.bestLatestMap);
				request.setAttribute("bestLatestNAMap", MDMSubjectCodeController.bestLatestNAMap);
				mav.addObject("mdmSubjectCodeMappingBean", codeBean);
				mav.addObject("mdmSubjectCodeMappingBean2", uploadBean);
				mav.addObject("subject_credits",SUBJECT_CREDITS);
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : displayMDMSubjectCodeMappingForm : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}	
	
	@RequestMapping(value = "/getPrgmStructByConsumerType", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseBean> getPrgmStructByConsumerType(@RequestBody ConsumerProgramStructureExam consumerProgramStructure) {
		logger.info("Entering getPrgmStructByConsumerType");
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		ArrayList<ConsumerProgramStructureExam> cpsList = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			cpsList = (ArrayList<ConsumerProgramStructureExam>) mdmSubjectCodeService.fetchPrgmStruc_By_ConsumerType(consumerProgramStructure.getConsumerTypeId());
			responseBean.setProgramStructureData(cpsList);
			if (null != cpsList && cpsList.isEmpty()) {
				responseBean.setStatus(KEY_ERROR);
				responseBean.setMessage("No ProgramStructure mapped to Consumer!");
			} else {
				responseBean.setStatus(KEY_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("MDMSubjectCodeController : getPrgmStructByConsumerType : " + e.getMessage());
			
			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Error finding ProgramStructure mapped to Consumer.");
		}
		return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
	}

	@RequestMapping(value = "/getPrgmByPrgmStructConsumerType", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseBean> getPrgmByPrgmStructConsumerType(@RequestBody ConsumerProgramStructureExam consumerProgramStructure) {
		logger.info("Entering getPrgmByPrgmStructConsumerType");
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		ArrayList<ConsumerProgramStructureExam> cpsList = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			cpsList = (ArrayList<ConsumerProgramStructureExam>) mdmSubjectCodeService.fetchProgram_By_PrgmStruc_ConsumerType(consumerProgramStructure.getProgramStructureId(),consumerProgramStructure.getConsumerTypeId());
			responseBean.setProgramsData(cpsList);
			if (null != cpsList && cpsList.isEmpty()) {
				responseBean.setStatus(KEY_ERROR);
				responseBean.setMessage("No Program mapped to ProgramStructure and Consumer!");
			} else {
				responseBean.setStatus(KEY_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("MDMSubjectCodeController : getPrgmByPrgmStructConsumerType : " + e.getMessage());
			
			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Error finding Program mapped to ProgramStructure and Consumer.");
		}
		return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/saveMDMSubjectCodeMappingForm", method = { RequestMethod.POST } )
	public ModelAndView saveMDMSubjectCodeMappingForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute MDMSubjectCodeMappingBean mdmSubjectCodeMappingBean) {
		logger.info("Entering saveMDMSubjectCodeMappingForm");
		ModelAndView mav = null;
		String userId = null;
		Map<String,Object> dataPrepared = null;
		boolean isSuccess = Boolean.FALSE;
		boolean goSave = Boolean.TRUE;
		MDMSubjectCodeMappingBean uploadBean = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
				return mav;
			} else {
				mav = new ModelAndView("mdm/subjectCodeMappingForm");
				if (null != mdmSubjectCodeMappingBean) {
					if(mdmSubjectCodeMappingBean.getActive().equalsIgnoreCase(KEY_Y)) {
						if (isBlank(mdmSubjectCodeMappingBean.getSubjectCodeId())
								|| isBlank(mdmSubjectCodeMappingBean.getSem())
								|| isBlank(mdmSubjectCodeMappingBean.getConsumerType())
								|| isBlank(mdmSubjectCodeMappingBean.getPrgmStructApplicable())
								|| isBlank(mdmSubjectCodeMappingBean.getProgram())
								|| isIntegerBlank(mdmSubjectCodeMappingBean.getSifySubjectCode())
								|| isIntegerBlank(mdmSubjectCodeMappingBean.getPassScore())
								|| isBlank(mdmSubjectCodeMappingBean.getHasIA())
								|| isBlank(mdmSubjectCodeMappingBean.getHasAssignment())
								|| isBlank(mdmSubjectCodeMappingBean.getHasTest())
								|| isBlank(mdmSubjectCodeMappingBean.getHasTEE())
								|| isBlank(mdmSubjectCodeMappingBean.getAssignmentNeededBeforeWritten())
								|| isBlank(mdmSubjectCodeMappingBean.getWrittenScoreModel())
								|| isBlank(mdmSubjectCodeMappingBean.getAssignmentScoreModel())
								|| isBlank(mdmSubjectCodeMappingBean.getCreateCaseForQuery())
								|| isBlank(mdmSubjectCodeMappingBean.getAssignQueryToFaculty())
								|| isBlank(mdmSubjectCodeMappingBean.getIsGraceApplicable())
								|| isIntegerBlank(mdmSubjectCodeMappingBean.getMaxGraceMarks())
								|| isDoubleBlank(mdmSubjectCodeMappingBean.getSubjectCredits())
								) {
							request.setAttribute(KEY_ERROR, "Cannot be made active as fields are missing value.");
							goSave = Boolean.FALSE;
						}
					}
					if(goSave) {
						userId = (String) request.getSession().getAttribute("userId");
						isSuccess = saveInPSS(request, response, mdmSubjectCodeMappingBean);
						if(isSuccess) {
							isSuccess = mdmSubjectCodeService.saveMDMSubjectCodeMapping(mdmSubjectCodeMappingBean, userId);
							setMessageInRequest(request, mdmSubjectCodeMappingBean);
							if (isSuccess) {
								mdmSubjectCodeMappingBean = new MDMSubjectCodeMappingBean();
							}
						}
					}
				}
				uploadBean =  new MDMSubjectCodeMappingBean();
				dataPrepared = mdmSubjectCodeService.prepareMDMSubjectCodeMapping();
				request.setAttribute("subjectCodeMappingList", dataPrepared.get("subjectCodeMappingList"));
				request.setAttribute("consumerTypeMap", dataPrepared.get("consumerTypeMap"));
				request.setAttribute("subjCodeMap", dataPrepared.get("subjCodeMap"));
				//request.setAttribute("subjCodeString", dataPrepared.get("subjCodeString"));
				request.setAttribute("semesterList", MDMSubjectCodeController.semesterList);
				//request.setAttribute("semesterJSON", MDMSubjectCodeController.semesterJSON);
				request.setAttribute("yesNoMap", MDMSubjectCodeController.yesNoMap);
				request.setAttribute("yesNoNAMap", MDMSubjectCodeController.yesNoNAMap);
				request.setAttribute("bestLatestMap", MDMSubjectCodeController.bestLatestMap);
				request.setAttribute("bestLatestNAMap", MDMSubjectCodeController.bestLatestNAMap);
				mav.addObject("mdmSubjectCodeMappingBean", mdmSubjectCodeMappingBean);
				mav.addObject("mdmSubjectCodeMappingBean2", uploadBean);
				mav.addObject("subject_credits",SUBJECT_CREDITS);
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : saveMDMSubjectCodeMappingForm : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	boolean saveInPSS(HttpServletRequest request, HttpServletResponse response,MDMSubjectCodeMappingBean srcBean) {
		logger.info("-------------MDMSubjectCodeController : saveInPSS : ENTER-------------");
		Boolean isSaved = Boolean.FALSE;
		ModelAndView mav = null;
		ProgramSubjectMappingExamBean programSubjectMappingBean = null;
		
		String consumerTypeName = mdmSubjectCodeService.fetchConsumerType(srcBean.getConsumerType());
		String programStructure = mdmSubjectCodeService.fetchProgramStructure(srcBean.getConsumerType(), srcBean.getPrgmStructApplicable());
		String program = mdmSubjectCodeService.fetchProgram(srcBean.getConsumerType(), srcBean.getPrgmStructApplicable(), srcBean.getProgram());
		MDMSubjectCodeBean dataBean = mdmSubjectCodeService.fetchMDMSubjectCode(srcBean.getSubjectCodeId());
		
		programSubjectMappingBean = new ProgramSubjectMappingExamBean();
		programSubjectMappingBean.setConsumerType(consumerTypeName);//use RETAIL not its Id
		programSubjectMappingBean.setPrgmStructApplicable(programStructure);//use Jul2014 not its Id
		programSubjectMappingBean.setProgram(program);//use ACBM not its Id

		programSubjectMappingBean.setSubject(dataBean.getSubjectname());
		programSubjectMappingBean.setActive(srcBean.getActive());
		
		if(srcBean.getActive().equalsIgnoreCase(KEY_N)) {
			if(null == srcBean.getSifySubjectCode()) {
				srcBean.setSifySubjectCode(0);
			}
		}
		programSubjectMappingBean.setSifySubjectCode(srcBean.getSifySubjectCode());
		programSubjectMappingBean.setSpecializationName(dataBean.getSpecializationType());
		programSubjectMappingBean.setStudentType(dataBean.getStudentType());
		programSubjectMappingBean.setDescription(dataBean.getDescription());
		programSubjectMappingBean.setSem(srcBean.getSem());
		programSubjectMappingBean.setPassScore(srcBean.getPassScore());
		programSubjectMappingBean.setHasAssignment(srcBean.getHasAssignment());
		programSubjectMappingBean.setAssignmentNeededBeforeWritten(srcBean.getAssignmentNeededBeforeWritten());
		programSubjectMappingBean.setWrittenScoreModel(srcBean.getWrittenScoreModel());
		programSubjectMappingBean.setAssignmentScoreModel(srcBean.getAssignmentScoreModel());
		programSubjectMappingBean.setCreateCaseForQuery(srcBean.getCreateCaseForQuery());
		programSubjectMappingBean.setAssignQueryToFaculty(srcBean.getAssignQueryToFaculty());
		programSubjectMappingBean.setIsGraceApplicable(srcBean.getIsGraceApplicable());
		programSubjectMappingBean.setMaxGraceMarks(srcBean.getMaxGraceMarks());
		programSubjectMappingBean.setHasTest(srcBean.getHasTest());
		programSubjectMappingBean.setHasIA(srcBean.getHasIA());
		programSubjectMappingBean.setSubjectCredits(srcBean.getSubjectCredits());
		
		programSubjectMappingBean.setCreatedBy(srcBean.getCreatedBy());
		programSubjectMappingBean.setLastModifiedBy(srcBean.getLastModifiedBy());

		logger.info(" (ConsumerType,ProgramStructure,Program,Subject) ("+ consumerTypeName+ ","+ programStructure+","+program+ ","+dataBean.getSubjectname()+")");
		mav = configController.programSubjectFormData(request, response, programSubjectMappingBean);
		
		if(null != request) {
			if(null != request.getAttribute("error")) {
				if("true".equalsIgnoreCase(request.getAttribute("error").toString())) {
					request.setAttribute(KEY_ERROR, request.getAttribute("errorMessage").toString());
				}
			} else if(null != request.getAttribute("success")) {
				if("true".equalsIgnoreCase(request.getAttribute("success").toString())) {
					request.setAttribute(KEY_SUCCESS, request.getAttribute("successMessage").toString());
					isSaved = Boolean.TRUE;
				}
			}
		}
		
		//To Prevent SideEffects, Clear information set from other controller.
		mav.clear();//to remove 'programSubjectList' and 'programSubjectMappingBean'.
		request.setAttribute("active", null);
		request.getSession().setAttribute("semesterList", null);
		request.getSession().setAttribute("subjectList", null);
		request.getSession().setAttribute("consumerType", null);
		request.setAttribute("hasAssignment", null);
		request.setAttribute("assignmentNeededBeforeWritten", null);
		request.setAttribute("writtenScoreModel", null);
		request.setAttribute("assignmentScoreModel", null);
		
		mav = null;
		program = null;
		programStructure = null;
		consumerTypeName = null;
		programSubjectMappingBean = null;
		logger.info("-------------MDMSubjectCodeController : saveInPSS : EXIT-------------");
		return isSaved;
	}
	
	boolean updateInPSS(MDMSubjectCodeMappingBean srcBean) {
		logger.info("-------------MDMSubjectCodeController : updateInPSS : ENTER-------------");
		Boolean isUpdated = Boolean.FALSE;
		ResponseEntity<HashMap<String, String>> responseMap = null;
		HashMap<String, String> bodyMap = null;
		ProgramSubjectMappingExamBean programSubjectMappingBean = null;
		
		MDMSubjectCodeMappingBean newDataBean = mdmSubjectCodeService.fetchMDMSubjectCodeMapping(srcBean.getId());
		String consumerTypeName = newDataBean.getConsumerType();
		String programStructure = newDataBean.getPrgmStructApplicable();
		String program = newDataBean.getProgram();
		String consumerProgramStructureId = newDataBean.getConsumerProgramStructureId();
		String semm = newDataBean.getSem();
		MDMSubjectCodeBean dataBean = mdmSubjectCodeService.fetchMDMSubjectCode(newDataBean.getSubjectCodeId());
		
		programSubjectMappingBean = new ProgramSubjectMappingExamBean();
		programSubjectMappingBean.setConsumerType(consumerTypeName);//use RETAIL not its Id
		programSubjectMappingBean.setPrgmStructApplicable(programStructure);//use Jul2014 not its Id
		programSubjectMappingBean.setProgram(program);//use ACBM not its Id
		programSubjectMappingBean.setConsumerProgramStructureId(consumerProgramStructureId);

		programSubjectMappingBean.setSubject(dataBean.getSubjectname());//
		programSubjectMappingBean.setActive(srcBean.getActive());
		
		if(srcBean.getActive().equalsIgnoreCase(KEY_N)) {
			if(null == srcBean.getSifySubjectCode()) {
				srcBean.setSifySubjectCode(0);
			}
		}
		programSubjectMappingBean.setSifySubjectCode(srcBean.getSifySubjectCode().intValue());
		programSubjectMappingBean.setSpecializationName(dataBean.getSpecializationType());//
		programSubjectMappingBean.setStudentType(dataBean.getStudentType());//
		programSubjectMappingBean.setDescription(dataBean.getDescription());//
		programSubjectMappingBean.setSem(semm);
		programSubjectMappingBean.setPassScore(srcBean.getPassScore());
		programSubjectMappingBean.setHasAssignment(srcBean.getHasAssignment());
		programSubjectMappingBean.setAssignmentNeededBeforeWritten(srcBean.getAssignmentNeededBeforeWritten());
		programSubjectMappingBean.setWrittenScoreModel(srcBean.getWrittenScoreModel());
		programSubjectMappingBean.setAssignmentScoreModel(srcBean.getAssignmentScoreModel());
		programSubjectMappingBean.setCreateCaseForQuery(srcBean.getCreateCaseForQuery());
		programSubjectMappingBean.setAssignQueryToFaculty(srcBean.getAssignQueryToFaculty());
		programSubjectMappingBean.setIsGraceApplicable(srcBean.getIsGraceApplicable());
		programSubjectMappingBean.setMaxGraceMarks(srcBean.getMaxGraceMarks());
		programSubjectMappingBean.setHasTest(srcBean.getHasTest());
		programSubjectMappingBean.setHasIA(srcBean.getHasIA());
		programSubjectMappingBean.setSubjectCredits(srcBean.getSubjectCredits());
		
		programSubjectMappingBean.setCreatedBy(srcBean.getCreatedBy());
		programSubjectMappingBean.setLastModifiedBy(srcBean.getLastModifiedBy());
		
		logger.info(" (ConsumerType,ProgramStructure,Program,Subject) ("+ consumerTypeName+ ","+ programStructure+","+program+ ","+dataBean.getSubjectname()+")");
		responseMap = configController.updateProgramSubjectEntry(programSubjectMappingBean);
		
		if(null != responseMap && responseMap.hasBody()) {
			bodyMap = responseMap.getBody();
			if(null != bodyMap.get("Status")) {
				if("Error".equalsIgnoreCase(bodyMap.get("Status")) || "Fail".equalsIgnoreCase(bodyMap.get("Status"))) {
					isUpdated = Boolean.FALSE;
				} else if("Success".equalsIgnoreCase(bodyMap.get("Status"))) {
					isUpdated = Boolean.TRUE;
				}
			}
		}
		
		//To Prevent SideEffects, Clear information set from other controller.
		responseMap = null;
		
		program = null;
		programStructure = null;
		consumerTypeName = null;
		programSubjectMappingBean = null;
		logger.info("-------------MDMSubjectCodeController : updateInPSS : EXIT-------------");
		return isUpdated;
	}
	
	@RequestMapping(value = "/updateOrDeleteMDMSubjectCodeMapping", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<HashMap<String, String>> updateOrDeleteMDMSubjectCodeMapping(@ModelAttribute MDMSubjectCodeMappingBean bean, HttpServletRequest request) {
		logger.info("Entering updateOrDeleteMDMSubjectCodeMapping "+ bean.getSubjectCodeId());
		HashMap<String, String> responseMap = null;
		HttpHeaders headers = null;
		
		try {
			responseMap = new HashMap<String, String>();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			if (null != bean) {
				//String[] uiArr =  bean.getProgramId().split("~");
				if (!isBlank(bean.getActive())) {
					if (bean.getActive().equalsIgnoreCase(KEY_Y)) {
						if (isIntegerBlank(bean.getSifySubjectCode()) || isIntegerBlank(bean.getPassScore()) || isBlank(bean.getHasIA())
								|| isBlank(bean.getHasAssignment()) || isBlank(bean.getHasTest()) || isBlank(bean.getHasTEE())
								|| isBlank(bean.getAssignmentNeededBeforeWritten())
								|| isBlank(bean.getWrittenScoreModel()) || isBlank(bean.getAssignmentScoreModel())
								|| isBlank(bean.getCreateCaseForQuery()) || isBlank(bean.getAssignQueryToFaculty())
								|| isBlank(bean.getIsGraceApplicable()) || isIntegerBlank(bean.getMaxGraceMarks())
								|| isDoubleBlank(bean.getSubjectCredits())) {
							responseMap.put(KEY_ERROR,"Cannot be made active as fields are missing value.");
							return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
						}
					}
				}
				
				if (isBlank(bean.getSem()) && isBlank(bean.getActive())) {
					/*bean.setSem(uiArr[0]);
					bean.setSubjectCodeId(uiArr[1]);
					bean.setConsumerProgramStructureId(uiArr[2]);*/
					/**
					 * commented by Riya new service is created
					 */
					//mdmSubjectCodeService.deleteMDMSubjectCodeMapping(bean); 
					responseMap.put(action_button, delete_button);
					mdmSubjectCodeService.deletePssId(bean);
				} else {
					responseMap.put(action_button, edit_button);
					String userId = (String) request.getSession().getAttribute("userId");
					bean.setLastModifiedBy(userId);
					bean.setCreatedBy(userId);
					if(updateInPSS(bean)) {
						mdmSubjectCodeService.updateMDMSubjectCodeMapping(bean); //, uiArr[0], uiArr[1], uiArr[2]);
					} else {
						bean.setStatus(KEY_ERROR);
						bean.setMessage("Update Fail.");
					}
				}
				responseMap.put(bean.getStatus(), bean.getMessage());
			}
		} catch (Exception e) {
			logger.error("MDMSubjectCodeController : updateOrDeleteMDMSubjectCodeMapping : " + e.getMessage());
			
			responseMap.put(KEY_ERROR,e.getMessage());
		}
		return new ResponseEntity<HashMap<String, String>>(responseMap, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/downloadMDMSubjectCodeMapping", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadMDMSubjectCodeMapping(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering downloadMDMSubjectCodeMapping");
		ModelAndView mav = null;
		List<MDMSubjectCodeMappingBean> subjectCodeMappingList = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
				return mav;
			} else {
				subjectCodeMappingList = mdmSubjectCodeService.fetchMDMSubjectCodeMappingList();
				if(null != subjectCodeMappingList) {
					logger.info("MDMSubjectCodeController :  downloadMDMSubjectCodeMapping : Total Exported : "+ subjectCodeMappingList.size());
				}
				mav = new ModelAndView("downloadMDMSubjectCodeMappingReport", "mdmSubjectCodeMappingList", subjectCodeMappingList);
			}
		} catch(Exception e) {
			logger.error("MDMSubjectCodeController : downloadMDMSubjectCodeMapping : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}

	//Others
	public void setMessageInRequest(HttpServletRequest request, Object bean) {
		if(bean instanceof MDMSubjectCodeBean) {
			request.setAttribute(((MDMSubjectCodeBean)bean).getStatus(), ((MDMSubjectCodeBean)bean).getMessage());
		} else if(bean instanceof MDMSubjectCodeMappingBean) {
			request.setAttribute(((MDMSubjectCodeMappingBean)bean).getStatus(), ((MDMSubjectCodeMappingBean)bean).getMessage());
		}
	}

	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}

	public static boolean isIntegerBlank(Integer arg) {
		return StringUtils.isBlank(String.valueOf(arg));
	}
	
	public static boolean isDoubleBlank(Double arg) {
		return StringUtils.isBlank(String.valueOf(arg));
	}
}