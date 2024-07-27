package com.nmims.controllers;

import java.util.ArrayList;

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
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.RemovalOfFacultyFromAllStageOfRevaluationBean;
import com.nmims.services.RemovalOfFacultyFromRevaluationServiceInterface;

@Controller
public class RemovalOfFacultyFromRevaluationController extends BaseController {

	@Autowired
	private RemovalOfFacultyFromRevaluationServiceInterface removalOfFacultyInterface;

	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;

	private static final Logger logger = LoggerFactory.getLogger(RemovalOfFacultyFromRevaluationController.class);

	
	@GetMapping(value = "/admin/removalOfFacultyFromRevaluationForm")
	public String getRemovalOfFacultyFromRevaluationForm(HttpServletRequest request, HttpServletResponse response,
			Model model) {

		if (!checkSession(request, response)) {
			return "login";
		}

		AssignmentFileBean assignmentFileBean = new AssignmentFileBean();

		model.addAttribute("assignmentFileBean", assignmentFileBean);

		model.addAttribute("faculties", removalOfFacultyInterface.getAllFaculties());

		model.addAttribute("subjects", removalOfFacultyInterface.getActiveSubjects());

		model.addAttribute("yearlist", currentYearList);

		return "assignment/removalOfFacultyFromAllStages";
	}

	@PostMapping(value = "/admin/searchFacultyFromAllStage")
	public ModelAndView searchFacultyFromAllStage(@ModelAttribute AssignmentFileBean assignmentFileBean,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("assignment/removalOfFacultyFromAllStages");

		ArrayList<AssignmentFileBean> facultyListFromAllStagesOfRevaluation = new  ArrayList<AssignmentFileBean>();
		try {
			facultyListFromAllStagesOfRevaluation = removalOfFacultyInterface
					.searchFacultyFromAllStagesOfRevaluation(assignmentFileBean.getYear(), assignmentFileBean.getMonth(),
							assignmentFileBean.getSubject(), assignmentFileBean.getFacultyId());
			logger.info(" User Input Year {} Month {} Subject {} FacultyId {} ",assignmentFileBean.getYear(), assignmentFileBean.getMonth(),
					assignmentFileBean.getSubject(), assignmentFileBean.getFacultyId());
			if(facultyListFromAllStagesOfRevaluation.size()<=0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records Found ");
			}
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
			logger.error("Error Searching Faculty " + e);

		}

		modelAndView.addObject("facultyListFromAllStages", facultyListFromAllStagesOfRevaluation);

		modelAndView.addObject("assignmentFileBean", assignmentFileBean);

		modelAndView.addObject("faculties", removalOfFacultyInterface.getAllFaculties());

		modelAndView.addObject("subjects", removalOfFacultyInterface.getActiveSubjects());

		modelAndView.addObject("yearlist", currentYearList);

		return modelAndView;
	}

	@PostMapping(value = "/admin/removalOfFacultyFromRevaluation")
	public ModelAndView removalOfFacultyFromAllStagesOfRevaluation(
			@ModelAttribute AssignmentFileBean assignmentFileBean, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView("assignment/removalOfFacultyFromAllStages");

		try {
			String userId = (String)request.getSession().getAttribute("userId");

			RemovalOfFacultyFromAllStageOfRevaluationBean rowsAffected = removalOfFacultyInterface.removeFacultyFromAllStagesOfRevaluation(
					assignmentFileBean.getYear(), assignmentFileBean.getMonth(), assignmentFileBean.getSubject(),
					assignmentFileBean.getFacultyId(),userId);

			AssignmentFileBean assignmentsFileBean = new AssignmentFileBean();

			modelAndView.addObject("assignmentFileBean", assignmentsFileBean);

			modelAndView.addObject("faculties", removalOfFacultyInterface.getAllFaculties());

			modelAndView.addObject("subjects", removalOfFacultyInterface.getActiveSubjects());

			modelAndView.addObject("yearlist", currentYearList);
			if(!(rowsAffected.getRowsAffectedFromAssignmentSubmission()==0 && rowsAffected.getRowsAffectedFromQAssignmentSubmission()==0)) {
				
			request.setAttribute("success", "true");
			
			request.setAttribute("successMessage", "No Of Records Deleted : " + rowsAffected.getRowsAffectedFromAssignmentSubmission() +" - " + rowsAffected.getRowsAffectedFromQAssignmentSubmission());
			
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			
			request.setAttribute("errorMessage", e.getMessage());
			
			logger.error("Error Updating Faculty " + e);

		}
		return modelAndView;
	}

}
