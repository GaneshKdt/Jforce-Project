package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ModuleContentAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LearningResourcesDAO;

@Controller
public class LearningResourcesStudentController extends BaseController
{
	
	private ArrayList<String> subjectList = null;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	private static final int BUFFER_SIZE = 4096;
	
	

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;


	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}

		return subjectList;
	}
	
	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList;
		try {
			failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingAcadsBean>();
			  
		}
		return failSubjectList;
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

	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	public ArrayList<String> applicableSubjectsForStudent(HttpServletRequest request) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegistrationData = cdao.getStudentRegistrationData(sapId);


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
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
				request.getSession().setAttribute("currentSemSubjects", currentSemSubjects);
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
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){
			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}
		
		request.getSession().setAttribute("failSubjectsBeans", failSubjectsBeans);
		request.getSession().setAttribute("applicableSubjects", applicableSubjects);
		return applicableSubjects;
				
	}
	
	@RequestMapping(value = "/student/learningModule", method = RequestMethod.GET)
	public String learningModule(Model m, HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return "studentPortalRedirect";
		}
		ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
		String s1 = "";
		HashMap<String, List<ModuleContentAcadsBean>> listOfContent = new HashMap<String, List<ModuleContentAcadsBean>>();
		ModuleContentAcadsBean moduleContentBean = new ModuleContentAcadsBean();
		List<ModuleContentAcadsBean> getContentList = new ArrayList<ModuleContentAcadsBean>();
		String userId = (String) request.getSession().getAttribute("userId_acads");
		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<String> getSubjectList = dao.getSubjectList(userId);
		Integer percentageOverall=0;
		List<ModuleContentAcadsBean> moduleDocumnentList=null;
		List<String> listOfPercentage=new ArrayList<>();
		List<Integer> listOfModuleDocumentsCount=new ArrayList<>();
		List<Integer> listOfModuleVideosCount=new ArrayList<>();
		for (int i = 0; i < allsubjects.size(); i++) {
			s1 = allsubjects.get(i);
			ArrayList<String> tempSubject = new ArrayList<String>();
			tempSubject.add(s1);
			getContentList = dao.getContentList(tempSubject);
			//tempSubject = null;
			int contentCount = getContentList != null ? getContentList.size() : 0;
			if (!(contentCount == 0)) {
				listOfContent.put(s1, getContentList);
				for (ModuleContentAcadsBean bean : getContentList) {
					 moduleDocumnentList = dao.getModuleDocumentDataById( bean.getId());
					Integer percentageForDoc=dao.getModuleDocumentPercentage(userId, bean.getId());
					Integer percentageForVideo=dao.getModuleVideoPercentage(userId, bean.getId());
					Integer n=dao.getModuleProgressCount(userId, bean.getId());
					Integer noOfModuleDocuments=dao.getModuleDocumentsCount(bean.getId());
					Integer noOfModuleVideos=dao.getModuleVideosCount(bean.getId());
					
					if(n!=0) {
						percentageOverall=(percentageForDoc+percentageForVideo)/n;
					}
					else {
						 percentageOverall=(percentageForDoc+percentageForVideo);
					}
					listOfModuleDocumentsCount.add(noOfModuleDocuments);
					listOfModuleVideosCount.add(noOfModuleVideos);
					listOfPercentage.add((Double.toString(percentageOverall)));
					//uncomment after testing
					ArrayList<Integer> allDocPercentage = new ArrayList<Integer>();
					for (ModuleContentAcadsBean docBean : moduleDocumnentList) {
						int percentage;
						allDocPercentage.add(docBean.getPercentComplete());
					}
					if(noOfModuleDocuments!=0) {
						moduleContentBean.setNoOfModuleDocuments(noOfModuleDocuments); }
						else {
						moduleContentBean.setNoOfModuleDocuments(0);	}
					
					if(noOfModuleVideos!=0) {
						moduleContentBean.setNoOfModuleVideos(noOfModuleVideos); }
						else {
						moduleContentBean.setNoOfModuleVideos(0);	}
					
					int sum = 0;
					int total = allDocPercentage.size();
					for (Integer docBean : allDocPercentage) {

						sum = sum + docBean;
					}

					Double overallModulePercentage = (double) sum / total;
					float tempFloat = percentageOverall.floatValue();
					int tempInt = Math.round(tempFloat);

					if (total != 0) {
						bean.setPercentageCombined(percentageOverall);
						bean.setPercentComplete(tempInt);
					} else {
						bean.setPercentage(0);
						bean.setPercentageCombined(0);
					}
				
				}
			}
		}
		m.addAttribute("moduleDocumnentList",moduleDocumnentList);
		m.addAttribute("listOfContent", listOfContent);
		m.addAttribute("moduleContentBean", moduleContentBean);
		return "learningModule";
	}

	@RequestMapping(value="/student/moduleLibraryList",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView ModuleLibraryList(@RequestParam("moduleId") Integer moduleId, 
										  @RequestParam(required=false) String colorId,
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		String userId = (String)request.getSession().getAttribute("userId_acads");
		ModelAndView mv = new ModelAndView("moduleLibraryList");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentAcadsBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
		moduleContentBean.setVideoPercentage(videoPercentage);
		List<ModuleContentAcadsBean> moduleDocumnentList =  dao.getModuleDocumentDataById(moduleId);
		List<VideoContentAcadsBean> videoTopicsList=dao.getVideoSubTopicsListByModuleId(moduleId);
		VideoContentAcadsBean videoToBePlayed=null;
		List<VideoContentAcadsBean> relatedTopics =new ArrayList<VideoContentAcadsBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && videoTopicsList.size()!=0) {
		 videoToBePlayed = videoTopicsList.get(0);
		 relatedTopics.remove(videoToBePlayed);
		}
		mv.addObject("moduleContentBean",moduleContentBean);
		//mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		mv.addObject("SERVER_PATH",SERVER_PATH);
		
		//Added to session to be used in viewVideoModuleTopic it will override each time this method is called.
		request.getSession().setAttribute("moduleDocumnentList", moduleDocumnentList);
		request.getSession().setAttribute("videoTopicsList", videoTopicsList);
		return mv;
	}
	
	@RequestMapping(value="/student/viewVideoModuleTopic",method=RequestMethod.GET)
	public ModelAndView viewVideoModuleTopic(@RequestParam("moduleId") Integer moduleId, 
											 @RequestParam("videoSubtopicId") long videoSubtopicId, 
											 HttpServletRequest request,
											 HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		ModelAndView mv = new ModelAndView("viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentAcadsBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
		moduleContentBean.setVideoPercentage(videoPercentage);
		List<ModuleContentAcadsBean> moduleDocumnentList =  (List<ModuleContentAcadsBean>)request.getSession().getAttribute("moduleDocumnentList");
		List<VideoContentAcadsBean> videoTopicsList= (List<VideoContentAcadsBean>)request.getSession().getAttribute("videoTopicsList");
		VideoContentAcadsBean videoToBePlayed=null;
		List<VideoContentAcadsBean> relatedTopics =new ArrayList<VideoContentAcadsBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && videoTopicsList.size()!=0) {
			for(VideoContentAcadsBean  tempVideo : videoTopicsList) {
				if(tempVideo.getId().longValue() == videoSubtopicId) {
				
					videoToBePlayed =tempVideo;
				}
			}
			relatedTopics.remove(videoToBePlayed);
		}

		mv.addObject("moduleContentBean",moduleContentBean);
		mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		
		return mv;
	}
	
	@RequestMapping(value="/student/viewVideoModule",method=RequestMethod.GET)
	public ModelAndView viewVideoModule(@RequestParam("moduleId") Integer moduleId,
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("studentRedirect");
		}
		ModelAndView mv = new ModelAndView("viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentAcadsBean moduleContentBean =  dao.getModuleContentById(moduleId);
		mv.addObject("moduleContentBean",moduleContentBean);
		return mv;
	}
	
	@RequestMapping(value = "/student/downloadDocument", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadDocument(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView modelnView = new ModelAndView("downloadDocument");

		String fullPath = request.getParameter("filePath");
		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}
	
}
