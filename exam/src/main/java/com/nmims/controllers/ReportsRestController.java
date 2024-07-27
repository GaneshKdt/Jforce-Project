package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentPendingSubjectBean;
import com.nmims.beans.StudentPendingSubjectsResponseBean;
import com.nmims.daos.ReportsDAO;
import com.nmims.factory.StudentPendingSubjectsEligibleFactory;
import com.nmims.stratergies.StudentPendingSubjectsEligibilityStrategyInterface;

@RestController
@RequestMapping("m")
public class ReportsRestController  extends BaseController{
	
	@Autowired(required=false)
	ApplicationContext act;

	
	@PostMapping(path = "/getPendingSubjectsForStudent" , produces="application/json", consumes="application/json")
	public ResponseEntity<StudentPendingSubjectsResponseBean> getPendingSubjectsForStudent(HttpServletRequest request, HttpServletResponse response, @RequestBody StudentExamBean input) {

		StudentPendingSubjectsResponseBean bean = new StudentPendingSubjectsResponseBean();
		String sapid = input.getSapid();
		
		if(StringUtils.isBlank(sapid)) {
			bean.setStatus("Fail");
			bean.setMessage("Please enter student number!");
			return ResponseEntity.ok(bean);
		}
		
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");	
		StudentExamBean student;
		try {
			student = dao.getStudentBySapid(sapid);
		}catch (Exception e) {
			bean.setStatus("Fail");
			bean.setMessage("Error fetching student info!");
			return ResponseEntity.ok(bean);
		}
		String studentProgramType;
		switch(student.getProgram()) {
		case "MBA - WX" : 
			studentProgramType = "MBAWX";
			break;
		case "MBA - X" : 
			studentProgramType = "MBAX";
			break;
		default : 
			studentProgramType = "PG";
			break;
		}
		StudentPendingSubjectsEligibleFactory.ProductType productType = StudentPendingSubjectsEligibleFactory.ProductType.valueOf(studentProgramType);
		StudentPendingSubjectsEligibleFactory factory = (StudentPendingSubjectsEligibleFactory) act.getBean("studentPendingSubjectsEligibleFactory");
		try {
			StudentPendingSubjectsEligibilityStrategyInterface strategy = factory.getProductType(productType);
			List<StudentPendingSubjectBean> pendingSubjects = strategy.getPendingSubjectsForStudent(student);
			bean.setStatus("Success");
			bean.setPendingSubjects(pendingSubjects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			bean.setStatus("Fail");
			bean.setMessage(e.getMessage());
		}

		return ResponseEntity.ok(bean);
	}
}
