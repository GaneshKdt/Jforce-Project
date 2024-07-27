package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.dto.MailDto;

@RestController
@RequestMapping("m")
public class EmailSMSRestController {
	@Autowired
	ApplicationContext act;
	
	@Autowired
	LeadDAO leadDAO;
	
	@PostMapping(path="/myEmailCommunicationsForm", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<MailDto>> mmyEmailCommunicationForm(@RequestBody  StudentStudentPortalBean student){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
//		if(!checkSession(request, response)){
//			return  new ModelAndView("jsp/login"); 
//		}
		
		//ModelAndView modelAndView = new ModelAndView("jsp/myEmailCommunications");
		
		//ArrayList<MailBean> listOfCommunicationMadeToStudent = (ArrayList<MailBean>)request.getSession().getAttribute("listOfCommunicationMadeToStudent_studentportal");
		
		//if(listOfCommunicationMadeToStudent == null){
			//Query from DB since it is not in session
		//	StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			
			List<MailDto> mailDtoList = new ArrayList<MailDto>();
			
			MailDto mailDto = null;
			
			List<MailStudentPortalBean> listOfCommunicationMadeToStudent  = (ArrayList<MailStudentPortalBean>)pDao.getEmailCommunicationMadeToStudent(student.getSapid());
		//}
			for(MailStudentPortalBean bean : listOfCommunicationMadeToStudent)
			{
				mailDto = new MailDto();
				mailDto.setBody(bean.getBody());
				mailDto.setSubject(bean.getSubject());
				mailDto.setCreatedDate(bean.getCreatedDate());
				mailDto.setId(bean.getId());
				mailDto.setFilterCriteria(bean.getFilterCriteria());
				mailDto.setFromEmailId(bean.getFromEmailId());
				
				mailDtoList.add(mailDto);
				
			}
		
		if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
			mailDtoList = new ArrayList<MailDto>();
			//request.setAttribute("error", "true");
			//request.setAttribute("errorMessage", "No Email Communications Made To You");
			//request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);//So that we don't query again for 0 email scenario as well
			//return modelAndView;
		}
		//request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);
		//modelAndView.addObject("rowCount",listOfCommunicationMadeToStudent.size());
		
		/*ArrayList<MailBean> getAllStudentCommunications = pDao.getAllStudentCommunications();*/
		//Get stream of sapid's sent in order to simplify//
		/*String getAllCommaSeperatedSapIdList = pDao.getCommaSeperatedSapIdListFromMailTable();
		if(!"".equals(getAllCommaSeperatedSapIdList) && getAllCommaSeperatedSapIdList != null){
			List<String> listOfSapid = Arrays.asList(getAllCommaSeperatedSapIdList.split("\\s*,\\s*"));
		
			String sapIdForMailSearch = "";
			for(String sapid : listOfSapid){
				if(sapid.equals(student.getSapid())){
					sapIdForMailSearch = student.getSapid();
				
					break;
				}
			}
			if(!"".equals(sapIdForMailSearch)){
				listOfCommunicationMadeToStudent = pDao.getEmailCommunicationMadeToStudent(sapIdForMailSearch);
			}
			
			
				
				if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Email Communications Made To You");
					return modelAndView;
				}
				
			
		}else{
			//This loop if there are no communications at all in mail table
			setError(request, "No Emailers");
			return modelAndView;
		}*/
		return new ResponseEntity<List<MailDto>>(mailDtoList, headers, HttpStatus.OK);

	
	}
	
	
	@PostMapping(path="/emailCommunicationsForLeads", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<MailStudentPortalBean>> emailCommunicationsForLeads(@RequestBody  StudentStudentPortalBean student){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");

		ArrayList<MailStudentPortalBean> listOfCommunicationMadeToStudent = leadDAO.getCommunicationForLeads();
		
		return new ResponseEntity<ArrayList<MailStudentPortalBean>>(listOfCommunicationMadeToStudent, headers, HttpStatus.OK);
	}

}
