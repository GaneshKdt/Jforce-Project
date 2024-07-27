package com.nmims.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.StudentMarksDAO;

@Controller
public class PGGraceController extends BaseController{
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> yearList;

	@Value("${PROGRAM_STRUCTURES}") 
	private List<String> PROGRAM_STRUCTURES;
	
	@Autowired
	private StudentMarksDAO studentMarksDao;
	
	@Autowired
	private ReportsDAO reportsDao;
	
	@Autowired
	private AssignmentsDAO assignmentsDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

	
	@RequestMapping(value = "/admin/graceToCompleteProgramReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String graceToCompleteProgramReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", yearList);
		m.addAttribute("programStructureList", PROGRAM_STRUCTURES);
		m.addAttribute("programList",getProgramList() );
		ArrayList<ConsumerProgramStructureExam> consumerType = assignmentsDao.getConsumerTypeList();
		m.addAttribute("consumerType",consumerType);
		request.getSession().setAttribute("consumerType", consumerType);
		return "graceToCompleteProgramReport";
	}


	/*
	 *////Added by Steff
	// migrated from reports controller to Grace Controller By Swarup
	@RequestMapping(value = "/admin/graceToCompleteProgramReport", method = RequestMethod.POST)
	public ModelAndView graceToCompleteProgramReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		logger.info(" graceToCompleteProgramReport by user : {} , studentMarks : {}", request.getSession().getAttribute("userId"), studentMarks);
		
		ModelAndView modelnView = new ModelAndView("graceToCompleteProgramReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		ArrayList<ConsumerProgramStructureExam> consumerType = (ArrayList<ConsumerProgramStructureExam>)request.getSession().getAttribute("consumerType");
		List<PassFailExamBean> studentMarksList = new ArrayList<PassFailExamBean>();
		List<PassFailExamBean> programCompleteList = new ArrayList<PassFailExamBean>();
		ArrayList<String> consumerProgramStructureIdList = assignmentsDao.getconsumerProgramStructureIds(studentMarks.getProgram(),studentMarks.getProgramStructApplicable(),studentMarks.getConsumerType());
		if(consumerProgramStructureIdList.size()>0) {
			for(String consumerProgramStructureId : consumerProgramStructureIdList) {
				try {
				logger.info(" graceToCompleteProgramReport consumerProgramStructureId : {} ", consumerProgramStructureId);	
				ProgramExamBean programDetails = reportsDao.getSingleProgramDetailsFromMasterKey(consumerProgramStructureId);
				logger.info(" graceToCompleteProgramReport programDetails : {} ", programDetails.toString());
				if(StringUtils.isBlank(programDetails.getProgram())||StringUtils.isBlank(programDetails.getNoOfSubjectsToClear())||StringUtils.isBlank(programDetails.getProgramStructure()) ){
				}else{
					studentMarksList.addAll(reportsDao.getGraceToCompleteProgramReport(studentMarks, programDetails.getProgram(),
							Integer.parseInt(programDetails.getNoOfSubjectsToClear()), 
							programDetails.getProgramStructure(), false,getAuthorizedCodes(request),consumerProgramStructureId  ));

					studentMarksList.addAll(reportsDao.getGraceToCompleteProgramReport(studentMarks, programDetails.getProgram(),
							Integer.parseInt(programDetails.getNoOfSubjectsToClearLateral()), 
							programDetails.getProgramStructure(), true,getAuthorizedCodes(request),consumerProgramStructureId ));
				}
				
			}catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.error(" Error in graceToCompleteProgramReport : {} ", errors.toString());
			}

			}
		}
		
		if(studentMarksList.size() > 0) {
			studentMarksList.removeIf(c -> ("Copy Case".equals(c.getRemarks()) ));
		}
		
		modelnView.addObject("studentMarksList", studentMarksList);
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("programStructureList", PROGRAM_STRUCTURES);
		modelnView.addObject("programList",getProgramList() );
		modelnView.addObject("examProcessingYear", studentMarks.getYear());
		modelnView.addObject("examProcessingMonth", studentMarks.getMonth());
		modelnView.addObject("consumerType", consumerType);

		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		for(PassFailExamBean e :studentMarksList){
			programCompleteList.addAll(reportsDao.getProgramCompleteGraceToBeAppliedForAStudent(e.getProgram(),e.getSapid())) ; 
		}

		request.getSession().setAttribute("programCompleteList",programCompleteList);
		modelnView.addObject("rowCount",studentMarksList.size());
		return modelnView;
	}



	@RequestMapping(value = "/admin/downloadGraceAppliedDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadGraceAppliedDetails(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		String program = request.getParameter("program");
		String sapid = request.getParameter("sapid");
		String totalGrace = request.getParameter("totalGrace");
		List<PassFailExamBean> programCompleteList = reportsDao.getProgramCompleteGraceAppliedForAStudent(program,sapid);

		return new ModelAndView("graceToCompleteProgramDetailExcelView","programCompleteList",programCompleteList);
	}
	
	@RequestMapping(value = "/admin/downloadGraceValidityEndRecords", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadGraceValidityEndRecords(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		List<PassFailExamBean> validityEndGraceRecords = (List<PassFailExamBean>) request.getSession().getAttribute("validityEndGraceRecords");
		
		return new ModelAndView("graceToCompleteProgramDetailExcelView","programCompleteList",validityEndGraceRecords);
	}

	@RequestMapping(value = "/admin/downloadGraceToCompleteProgramReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadGraceToCompleteProgramReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<PassFailExamBean> programCompleteList = (ArrayList<PassFailExamBean>)request.getSession().getAttribute("programCompleteList");

		return new ModelAndView("graceToCompleteProgramReportExcelView","programCompleteList",programCompleteList);
	}

	public List<String> getProgramList() {
		return studentMarksDao.getAllPrograms();
	}	
	
}
