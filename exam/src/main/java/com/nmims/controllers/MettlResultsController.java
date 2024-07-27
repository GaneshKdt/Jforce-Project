package com.nmims.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.MettlEvaluatorInfo;
import com.nmims.beans.MettlFetchTestResultBean;
import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlResultAPIResponseBean;
import com.nmims.beans.MettlResultCandidateBean;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.MettlSectionQuestionResponse;
import com.nmims.beans.MettlStudentSectionInfo;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.TCSMarksBean;
import com.nmims.daos.MettlPGResultProcessingDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.MettlTeeMarksService;
import com.nmims.services.PGApplyBODService;

@Controller
public class MettlResultsController extends BaseController {

	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 
	
	@Autowired
	private MettlTeeMarksService mettlTeeMarksService;
	
	@Autowired
	private PGApplyBODService pgApplyBODService;


	@Autowired
	private MettlPGResultProcessingDAO dao;

	@Value("${SERVER}")
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	public static final Logger logger = LoggerFactory.getLogger(MettlResultsController.class);
	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");
	
//	List<String> questionIds = Arrays.asList("flMk6nj7EHsqO5h6A0C3pQ3EYFzBiKRVYIiuGW73618%3D","Yj6N7GYmhUxkVwmKm33sG6zxcKiE7qIZ%2FQtwOAqBvg8%3D","YFLFKYmhxOVp5qUR1H85RaxszqP23xulILnB%2BqX1Wvo%3D","%2FM1CmGLg4Dzn99y19n0vOnPNxi%2BYv7Q51luJd4yTeFg%3D","uXihjwNtWrZ1Dki%2FEXrjHqgd6g1lMH4SbsmBYmwtVKM%3D","UJEgKW%2Fl19IgttI1uu2DtEBl6iyQ3EQLOrtjqTx4Urg%3D","JsjLin4k9kRMZ5NaLG90cdbnQZMOVPBoooFiD1Ddzc8%3D","jDvJwnFJGP2FTxskE2%2F2GuzGyrUo35yZxBJyKw2QQW8%3D","JkQjmV6q9uGzVFYcknNhJxhtYGf55FVX7K3WnZZjJYA%3D","dyFRWhAvnaeiJ2ZOyDlY94G%2F4Beth0r6hfBRgWTHxRM%3D","oLWMNUKURHtc4LI7%2FojADkLiR042K6PClfppTanj1zc%3D","jC9zXgw9btCYdhdbPv8pLv035RVuhsBnK%2BBGwkj3HcY%3D","oh5LBrJAjpCMz8kzsZO75D8XGHMNtwRLs9uYBaPofGA%3D","Pc8P9woAiestp%2FTrdCKQl2q9%2BHcj%2FDdkd6yq3EKzP1Q%3D","gRYIPeCmOHGTjfSPDiwcy%2BZrqpggOjKCepmQKDXrrX0%3D","TCklZHrc4H4W1%2FQTMA5UKhykKZrNafnz62hhJwZ%2BdgI%3D","RGrfWH5lgARU0RiSrvEz5oJx8mXvRyydUR6ZQ2Lz4g0%3D","4rz9zun%2FLiplqa9mC%2FPt2Z9RcEBxmaKJNpu7Oa7xpFM%3D","SbaMxr1CgowGWXubN1oXi%2FNUHKqqI5dbf95Zd6mQqe8%3D","Gw8woKeT%2FVxnhjhn4r170GQ1bG%2FJI46y6kY0UEEknP8%3D","Z1Vq9Y%2BQVnNzvPjeg5om0Z%2F02tjbY3xtKHkaoY0irJk%3D","dHR21enHg9ZjcrBmhLPbe1lvhUgQax7t2k8RvXs20kM%3D","ZQ7%2BwstqAT6Uh403QAD6tM%2BRJmq16XL3s%2BNrvzp0OLA%3D","5Ow1n6%2BTSUGg0edJTNgueAUFVcMubvGdgOS%2FWz4M5QM%3D","PSszrFnZQnUowTNpYSYwOTGkuhiCJ%2BX25hftm5aWNko%3D","lSTIAKQ3StG%2BYPVExUjW0gOHF4gW6Z6KELnMfmhOB3I%3D","1cjxfyPkYb5%2BXB5Xa44c%2BhBudocDAc4fMTEY7SBnApw%3D","yOUGuRH20z0qY8LvWBrogJmKIUljs3%2B3cx6iaG3kl7g%3D","E%2FUrN0Lmt%2BEeWCRYKrM6W88oYRl6dtegJ%2BONEp9nloo%3D","Zo51zrwzPJlVNEIyHMPqXFei1X0QnMsodtthXKfzFfs%3D","1DiafMIpidU35oxAKaFCNXSq0McETKGIdv6Bh8%2FHagw%3D","RUeOlNdlnNbMSqTeX7pMaA3EYFzBiKRVYIiuGW73618%3D","ZHy3pTwfLZ%2B889f1pPD9keMRU7HGB6MLWqlKYjGBRJk%3D","hF%2BLWbhQcA8l2zZ72RK%2B9STUOAm6Lmy8fldlkom0yGE%3D","slU7I%2FU6kHgPcPUEGpFU3D8L%2FIZBJTGcb5t6m4lbpzA%3D","aFBxZ%2BLXB3P8DTTWcrMhOcLeW8EJ6gtE0zKRHYxd354%3D","kvVSP1Mp70LcNkMWpsp4TcPS5C4sdA1yJkEKXfqS6nk%3D","DbIGknnmIlWlRcR6Dh5wTZxCJGhT8YHf8KGDhUcAFws%3D","fxy0eYlbTKuRFmGVagQYhmKZPhBNZoHxluIaANSmuHQ%3D","uInT%2F73uQJ2TpyQVi32tFVJPcvCuIR%2B1GXQ7RFNMzPg%3D","Thfn%2FKHS5DlJl7GWFmGyG5Q9Fa4IYFFOnctINuicq5I%3D","RmW9nMAl1ppLl%2BZ2PI3Q6CaF7EmoL54r0gauui8mHT0%3D","YssaizorySZf01uSvc84PlLF5hMFM0RZwMJ0tNYMgXY%3D","B9txwZzHI%2Fero97zlzgj%2BI8AWn8cghsLOE5h6PYy2mw%3D","cl5RuHE9Qc%2F9dr%2B6wdTBLsNc4gtbFdEh1kT%2BKzKU9hg%3D","9tz9D1cKHFP9U0vnlg1BK0YRJe3C96Pmf5TxkApk1bE%3D","%2B3DVmarz5bOtCKWVR05AMB1LiSccQ4JQ0JqaO4NgheQ%3D","YwQCd%2Fay02xr4p8ZNyEl7d6uTKo7CsqnjHw3X9Y8lSw%3D","5cLlyFn1s1a%2FG7jB8xXG9A3t4UVeNibO0gA0moWBptI%3D","1j7Scn97%2FGaCQJR7%2BoxVsrxOGU9COfcjvyT%2FfApL8OE%3D","BJzwAZbb1LleAvvYOsd170fQmQ%2B%2FoZ7tIGLk%2FHUfDeE%3D","4cLvCW4Quk6wBXF4EDiuSh1LiSccQ4JQ0JqaO4NgheQ%3D","LIT6gKc43kswnGw8qkJoWRBf8G9MyzhrDnthmWqVXSA%3D","QnSdFmoNHEOGMgWoEl%2FhIAV1dnFM30XgMmv7ISuK278%3D","f%2Bg7JPHyiJGGkxiOVPiZkCXE1ysZMx7sBge4mf4TOKk%3D","qGDvu69Oq4SNMN%2Fp%2FF8lTnweFs1WPxEsGF0U0JuO2ok%3D","HaJO86AX0mEKBT1dUpcvuqoMfX0tDArUWoj9LXC4cmI%3D","By2MifF27pghETYK9ivqUST5KnZgDs4BQ%2BwSvGKFBa0%3D","GhKFOX2PPeT%2BqQngnmhSBj%2BoY2grx80%2F2K63oEUPSLk%3D","Kdz9PSRV2RSMTBRjc1eV1Y6uxTuunjm%2FXYlssl8hgho%3D","VMLdMx7I1EpNu5r9GE63UF%2Fb5BD1GJtxVOIcBHiUwU4%3D");
	
//	
//	@RequestMapping(value = "/runMettlAbsentTeeListSchedular", method = RequestMethod.POST, produces="application/json")
//	public ResponseEntity<ResponseBean> runMettlAbsentTeeListSchedular(@RequestBody TcsOnlineExamBean tcsOnlineExamBean) {
//		ResponseBean responseBean = new ResponseBean();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		String result = mettlTeeMarksService.runMettlAbsentTeeListSchedular(tcsOnlineExamBean.getExamDate());
//		responseBean.setMessage(result);
//		
//		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
//	}


	@RequestMapping(value = "/admin/pullMettlMarksForTeeExamsForm",  method = RequestMethod.GET)
	public ModelAndView pullMettlMarksForTeeExamsForm(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("mettl/pullMettlMarksForm");
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("programStructureList", dao.getProgramStructureList());
		mv.addObject("programList", dao.getProgramList());
		mv.addObject("yearList",yearList);
		String userId = (String)request.getSession().getAttribute("userId");
		mv.addObject("userId", userId);
		MettlFetchTestResultBean resultbean = mettlTeeMarksService.getPullProcessStatus();
		mv.addObject("resultbean", resultbean);
		if(resultbean.getFailureResponse() != null && resultbean.getFailureResponse().size() > 0) {
			request.getSession().setAttribute("pgTeePullProcessFailureResponseDownload", resultbean.getFailureResponse());
		}
		return mv;
	}
	
	@RequestMapping(value="/admin/applyBodForm",method=RequestMethod.GET)
	public ModelAndView applyBodForm(HttpServletRequest request, HttpServletResponse response)
	{
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("mettl/applyBod");
		String userId = (String)request.getSession().getAttribute("userId");
		mv.addObject("userId", userId);
		mv.addObject("monthList", EXAM_MONTH_LIST);
		mv.addObject("yearList",yearList);
		return mv;
	}
	
	@RequestMapping(value = "/admin/downloadApplyBodErrorList", method = RequestMethod.GET)
	public ModelAndView downloadApplyBodErrorList(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 ArrayList<String> list = (ArrayList<String>)request.getSession().getAttribute("applyBodErrorListDownload");
		 return new ModelAndView("ApplyBodFailureResponseExcelView","applyBodErrorList",list);
	}
	
	@RequestMapping(value = "/admin/downloadPgTeePullProcessFailureResponse", method = RequestMethod.GET)
	public ModelAndView downloadExamBookingData(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		 List<MettlPGResponseBean> list = (List<MettlPGResponseBean>)request.getSession().getAttribute("pgTeePullProcessFailureResponseDownload");
		 return new ModelAndView("PGTeePullProcessFailureResponseExcelView","pgTeePullProcessFailureResponseList",list);
	}

	@RequestMapping(value = "/admin/getListOfSubjectsForMettl", produces="application/json")
	public ResponseEntity<List<ProgramSubjectMappingExamBean>> getListOfSubjectsForMettl(@RequestBody ConsumerProgramStructureExam cps) {

		List<ProgramSubjectMappingExamBean> listOfSubjects = dao.getSubjectsList(cps);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		return new ResponseEntity<List<ProgramSubjectMappingExamBean>>(listOfSubjects, headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/admin/pullMettlMarksForTeeExams", method = RequestMethod.POST)
	public ModelAndView pullMettlMarksForTeeExams(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MettlResultsSyncBean inputBean) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		inputBean.setCreatedBy(userId);
		inputBean.setLastModifiedBy(userId);
		
		ModelAndView mv = new ModelAndView("mettl/pullMettlMarksForm");
		mv.addObject("consumerTypeList", dao.getConsumerTypeList());
		mv.addObject("programStructureList", dao.getProgramStructureList());
		mv.addObject("programList", dao.getProgramList());

		ExcelHelper excelHelper = new ExcelHelper();
		List<MettlPGResponseBean> studentsToFetchResultsFor = excelHelper.readExcelMettlExamInput(inputBean);
		inputBean.setCustomFetchInput(studentsToFetchResultsFor);
		
		mettlTeeMarksService.pullMarksFromMettlAPI(inputBean);

		mv.addObject("inputBean", inputBean);
		return mv;
	}
	

	
	@RequestMapping(value="/admin/transferMettlResultsToOnlineMarksForm")
	public ModelAndView transferTCSResultsToOnlineMarksForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TCSMarksBean studentMarks) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("mettl/transferToMarksTable");
		mav.addObject("studentMarks", studentMarks);
		mav.addObject("yearList", yearList);
		mav.addObject("monthList", EXAM_MONTH_LIST);
		return mav;
	}
	

	@RequestMapping(value="/admin/transferMettlResultsToOnlineMarks")
	public ModelAndView transferTCSResultsToOnlineMarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MettlPGResponseBean studentMarks) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mav=new ModelAndView("mettl/transferToMarksTable");

		mav.addObject("studentMarks", studentMarks);
		mav.addObject("yearList", yearList);
		mav.addObject("monthList", EXAM_MONTH_LIST);
		try {
			mettlTeeMarksService.transferScoresToOnlineMarks(mav, studentMarks, userId);
		} catch (ParseException e) {
			logger.error("transferMettlResultsToOnlineMarks : "+e.getMessage());//by Vilpesh 2022-02-23 
			setError(request, e.getMessage());
		} catch(Exception ex) {
			logger.error("transferMettlResultsToOnlineMarks : "+ex.getMessage());//by Vilpesh 2022-02-23
			setError(request, ex.getMessage());
		}
		return mav;
	}
	
//	@RequestMapping(value="/getBOD",method={RequestMethod.GET,RequestMethod.POST}, produces="application/json") 
//	public ResponseEntity<String> getBOD(HttpServletRequest request,HttpServletResponse response) {	
//		for (String questionId : questionIds) {
//			List<MettlStudentTestInfo> testInfo = mettlTeeMarksService.grantBenefitOfDoubtToStudentsForQuestion(questionId);
//		}
//		return new ResponseEntity<String>(new Gson().toJson(testInfo) ,HttpStatus.OK);
//	}

//	@RequestMapping(value="/admin/applyBOD",method={RequestMethod.GET,RequestMethod.POST}, produces="application/json") 
//	public ResponseEntity<String> applyBOD(HttpServletRequest request,HttpServletResponse response) {
//
//		List<MettlStudentTestInfo> successList = new ArrayList<MettlStudentTestInfo>();
//		List<MettlStudentTestInfo> errorList = new ArrayList<MettlStudentTestInfo>();
				
//		for (String questionId : questionIds) {
//			List<MettlStudentTestInfo> testInfoList = mettlTeeMarksService.grantBenefitOfDoubtToStudentsForQuestion(questionId);
//
//			mettlTeeMarksService.updateMarksForBenefitOfDoubtQuestions(testInfoList, successList, errorList);
//		}
		
//		pgApplyBODService.applyBOD(successList, errorList);
//		
//		applybodPG.info(
//				"\n"+SERVER+":  applyBOD " 
//				+ " Finished Processing."
//				+ " Successful inserts : " + successList.size()
//				+ " Failed inserts : " + errorList.size()
//				+ " Failed inserts JSON : " + new Gson().toJson(errorList)
//			);
//		Map<String, List<MettlStudentTestInfo>> resp = new HashMap<String, List<MettlStudentTestInfo>>();
//		
//		resp.put("errorList", errorList);
//		return new ResponseEntity<String>(new Gson().toJson(resp) ,HttpStatus.OK);
//	}

	

	@RequestMapping(value="/admin/getDQMismatch",method={RequestMethod.GET,RequestMethod.POST}, produces="application/json") 
	public ResponseEntity<String> getDQMismatch(HttpServletRequest request,HttpServletResponse response) {
		mettlTeeMarksService.checkAllDQQuestionScores();

		return new ResponseEntity<String>(new Gson().toJson("") ,HttpStatus.OK);
	}
	


	@RequestMapping(value = "/admin/getMettlStudentEvaluationReportForm",  method = RequestMethod.GET)
	public ModelAndView getMettlStudentEvaluationReportForm() {

		ModelAndView mv = new ModelAndView("mettl/evaluationReport");
		mv.addObject("inputBean", new MettlResultsSyncBean());
		
		return mv;
	}


	@RequestMapping(value = "/admin/getMettlStudentEvaluationReport", method = RequestMethod.POST)
	public ModelAndView getMettlStudentEvaluationReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MettlResultsSyncBean inputBean) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ExcelHelper excelHelper = new ExcelHelper();
		List<MettlPGResponseBean> studentsToFetchResultsFor = excelHelper.readExcelMettlExamInput(inputBean);
		inputBean.setCustomFetchInput(studentsToFetchResultsFor);

		ModelAndView mv;
		try {
			
			List<MettlStudentTestInfo> testInfoList = mettlTeeMarksService.getStudentTestInfo(inputBean);
			List<MettlStudentSectionInfo> studentTestSectionInfo = mettlTeeMarksService.getStudentTestSectionInfo(inputBean);
			List<MettlSectionQuestionResponse> questionInfo = mettlTeeMarksService.getStudentTestSectionQuestionInfo(inputBean);
			List<MettlEvaluatorInfo> evaluatorInfo = mettlTeeMarksService.getStudentTestSectionEvaluationInfo(inputBean);
			Map<String, String> questions = mettlTeeMarksService.getQuestionMap();
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("testInfoList", testInfoList);
			data.put("studentTestSectionInfo", studentTestSectionInfo);
			data.put("questionInfo", questionInfo);
			data.put("evaluatorInfo", evaluatorInfo);
			data.put("questions", questions);
			mv = new ModelAndView("mettlEvaluationReportExcelView", data);
		} catch (Exception e) {
			
			mv = new ModelAndView("mettl/evaluationReport");
			mv.addObject("inputBean", inputBean);
			setError(request, "Error getting report : " + e.getMessage());
		}
		return mv;
	}
	
}
