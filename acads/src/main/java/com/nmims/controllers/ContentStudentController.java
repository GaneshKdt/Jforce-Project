package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.factory.ContentFactory;
import com.nmims.factory.ContentFactory.StudentType;
import com.nmims.interfaces.ContentInterface;

@Controller
public class ContentStudentController extends BaseController
{
	
	@Autowired
	ContentDAO contentDAO;
	
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST; 
	
	private ArrayList<String> subjectList = null; 
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	

	@Autowired
    private ContentFactory contentFactory;  
	
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		
		return subjectList;
	}
	
	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}
	

	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	@RequestMapping(value = "/student/previewContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContent(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContent");
	}
	
	@RequestMapping(value = "/student/previewContentAlt", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContentAlt(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContentAlt");
	}
	
	private ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}

	
	@RequestMapping(value = "/student/viewApplicableSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewApplicableSubjectsForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegistrationData = dao.getStudentRegistrationData(sapId);

		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());

		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
			}
			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}
		
		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = dao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);

		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you.");
			return new ModelAndView("viewApplicableSubjects");
		}

		m.addAttribute("subjects",allsubjects);
		int rowCount = (allsubjects == null ? 0 : allsubjects.size());
		m.addAttribute("rowCount", rowCount);


		return new ModelAndView("viewApplicableSubjects");
	}
	
	//View All Content using subjectcodeId
		@RequestMapping(value = "/student/viewContentForSubject", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView viewContentForSubject(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ContentAcadsBean content) {

			if(!checkSession(request, respnse)){
				return new ModelAndView("studentPortalRediret");
			}
			
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			PersonAcads user = (PersonAcads)request.getSession().getAttribute("user_acads");
			
			String roles = user.getRoles();
			
			String subject = request.getParameter("subject").trim();
			
			List<ContentAcadsBean> allContentListForSubject = contentDAO.getContentsForSubjects(subject);
			
			VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
			List<VideoContentAcadsBean> videoContentList = vDao.getVideoContentForSubject(subject, content);
			
			request.getSession().setAttribute("videoContentList", videoContentList);

			if(allContentListForSubject == null || allContentListForSubject.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Study Material found for this subject.");
				return new ModelAndView("viewContentForSubject");
			}
			
			List<ContentAcadsBean> contentList = new ArrayList<ContentAcadsBean>();
			
			 
			request.getSession().setAttribute("videoContentList", videoContentList);
		
			
			String userId = (String)request.getSession().getAttribute("userId_acads");
			if(student != null && (userId.startsWith("77") || userId.startsWith("79") )){
				
				
				if(student.getWaivedOffSubjects().contains(subject)){
					//If subject is waived off, dont go to assignment submission page.
					setError(request, subject + " subject is not applicable for you.");
					return viewApplicableSubjectsForm(request, respnse, m);
				}
				
				String programStructureForStudent = student.getPrgmStructApplicable();
				for (ContentAcadsBean contentBean : allContentListForSubject) {
					String programStructureForContent = contentBean.getProgramStructure();
					
					if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
						contentList.add(contentBean);
					}else if(programStructureForContent.equals(programStructureForStudent)){
						contentList.add(contentBean);
					}
				}
			}else{
				contentList = allContentListForSubject;
				contentList = addConsumerProgramProgramStructureNameToEachContentFile(contentList);
			}
			
			// Commented By Riya as no longer needed as content is being uploaded by subjectcodeId
			//Added check for UG faculty
			/*if(roles.indexOf("Faculty") != -1) {
				List<ContentBean> newContentList = new ArrayList<ContentBean>();
				
				if (userId.equalsIgnoreCase("NGASCE24072020") && subject.equalsIgnoreCase("Business Communication")) {
					for (ContentBean contentBean : contentList) {
						if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
							newContentList.add(contentBean);
						}
					}
					contentList = newContentList;
				}else if(subject.equalsIgnoreCase("Business Communication") && !userId.equalsIgnoreCase("NMSCEMU200303331")) {
					for (ContentBean contentBean : contentList) {
						if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
							newContentList.add(contentBean);
						}
					}
					contentList.removeAll(newContentList);
				}
			}*/
			 
			m.addAttribute("contentList",contentList);
			int rowCount = (contentList == null ? 0 : contentList.size());
			m.addAttribute("rowCount", rowCount);
			m.addAttribute("subject",subject);
			m.addAttribute("month",content.getMonth());
			m.addAttribute("year",content.getYear());
			return new ModelAndView("viewContentForSubject");
		}
	
		private List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile( List<ContentAcadsBean> contentList) {
			try {
				/*int size = 0, i = 1;
				String contentIds = "";
				
				if(contentList != null ) {
					size = contentList.size();
				}
				
				for(ContentBean c : contentList) {
					if(i == size) {
						contentIds += c.getId()+"";
					}else {
						contentIds += c.getId()+",";
					}
					i++;
				}
				
				Map<String,Integer> contentIdNCountOfProgramsApplicableToMap =  dao.getContentIdNCountOfProgramsApplicableToMap(contentIds);
				
				for(ContentBean t : contentList) {*/
					
						/*t.setConsumerType(getConsumerTypeIdNameMap().get(t.getConsumerTypeIdFormValue()));
						
						if(t.getProgramStructureIdFormValue().split(",").length>1) {
							t.setProgramStructure("All");;
						}else {
							t.setProgramStructure(getProgramStructureIdNameMap().get(t.getProgramStructureIdFormValue()));;
						}
						
						if(t.getProgramIdFormValue().split(",").length>1) {
							t.setProgram("All");;
						}else {
							t.setProgram(getProgramIdNameMap().get(t.getProgramIdFormValue()));;
						}*/
						
						//t.setCountOfProgramsApplicableTo(contentIdNCountOfProgramsApplicableToMap.get(t.getId()));
				ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
				contentList = contents.addConsumerProgramProgramStructureNameToEachContentFile(contentList);
					
					
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			
			return contentList;
		}

	
} 
