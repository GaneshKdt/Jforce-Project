package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentHistoryResponseBean;
import com.nmims.beans.Page;
import com.nmims.beans.Person;
import com.nmims.daos.ProjectSubmissionDAO;

@RestController
@RequestMapping("m")
public class ProjectSubmissionRestController extends BaseController {
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@RequestMapping(value = "/viewPreviousProjects", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<AssignmentHistoryResponseBean> mViewPreviousAssignments(HttpServletRequest request,
			@RequestBody Person input){
		
		AssignmentHistoryResponseBean response = new AssignmentHistoryResponseBean();

		AssignmentFileBean searchBean = new AssignmentFileBean();
		searchBean.setSapId(input.getSapId());
		Page<AssignmentFileBean> page = projectSubmissionDAO.getProjectSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			response.setError("true");
			response.setErrorMessage("No Assignment Submissions found.");
		} else {
			response.setError("false");
			response.setData(assignmentFilesList);
		}
		return new ResponseEntity<AssignmentHistoryResponseBean>(response, HttpStatus.OK);
	}

}
