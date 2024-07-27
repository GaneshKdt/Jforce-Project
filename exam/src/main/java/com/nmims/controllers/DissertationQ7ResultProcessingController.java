package com.nmims.controllers;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.interfaces.DissertationResultProcessingService;




@Controller
@RequestMapping("/admin")
public class DissertationQ7ResultProcessingController {
	
	@Autowired
	DissertationResultProcessingService dissertationQ7Service;
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	

	private static final String Q7SubjectName = "Masters Dissertation Part - I";
	

	public List<BatchExamBean> getBatchList(TEEResultBean resultBean){
		//getting ConsumerProgramStructureId and PssId from subject and sem
		List<Integer> responseMasterKeys = dissertationQ7Service.getConsumerAndSubjectId();
		return dissertationQ7Service.getBatchList(responseMasterKeys);
	}
	
	public ModelAndView getModelView(ModelAndView model,HttpServletRequest request,@ModelAttribute TEEResultBean resultBean ) {
		try {
			List<BatchExamBean> batchList = getBatchList(resultBean);
			
			model.addObject("resultBean", resultBean);
			model.addObject("batch", batchList);
			model.addObject("subjectName", Q7SubjectName);
		}catch(Exception e) {
			dissertationLogs.info("DissertationQ7 : Error Found in Getting Batch Details "+ e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error Found In Result Processing"+" "+ e.getMessage());
		}
		return model;
	}
	
	@RequestMapping(value ="/dissertationQ7ExamResultCheckList",method = RequestMethod.GET)
	public ModelAndView dissertationCheckList() {
		return new ModelAndView("dissertationQ7ExamResultCheckList");
	}
	
	
	
	@RequestMapping(value="/dissertationResultForm", method = {RequestMethod.GET})
	public ModelAndView insertMSCScoreIntoMarksForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean ) {
		ModelAndView model = new ModelAndView("dissertationQ7Marks");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value="/insertDissertationScoreIntoMarks" , method = {RequestMethod.POST})
	public ModelAndView inserDissertationScoreIntoMarks(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ7Marks");

		List<DissertationResultBean> errorList = new ArrayList<DissertationResultBean>();
		int insertedCount =0;
		try {
			String loggedInUser =(String)request.getSession().getAttribute("userId");	
			if (!StringUtils.isEmpty(resultBean.getTimebound_id())) {
				
				// Getting Eligible Student by timeBoundId
				List<TEEResultBean> eligibleIAStudents = dissertationQ7Service.getEligibleIAStudents(resultBean.getTimebound_id(),errorList);
				
				if (!eligibleIAStudents.isEmpty()) {
					// Processing the eligible Student list and returning the score to insert
					List<DissertationResultBean> upsertScoreList = dissertationQ7Service
							.getDissertationQ7Scores(resultBean.getTimebound_id(),eligibleIAStudents, errorList,loggedInUser);

					if (!upsertScoreList.isEmpty()) {
						// Insert into "exam.mscaiml_md_q7_marks"
						insertedCount	= dissertationQ7Service.upsertMarks(upsertScoreList);
						request.getSession().setAttribute("DissertationResultForQ7", upsertScoreList);
					}
				}

				request.setAttribute("success", "true");
				request.setAttribute("successMessage", insertedCount+" "+"Records has been Processed");
				model.addObject("errorList",errorList);
				model.addObject("insertedCount",insertedCount);
			}

		} catch (Exception e) {
			dissertationLogs.info("DissertationQ7 : Error Found in insertDissertationScoreIntoMarks "+ e);
			model.addObject("errorList",errorList);
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error Found In Result Processing"+" "+ e.getMessage());
		}
			
		return getModelView(model,request, resultBean);
	}
	
	@RequestMapping(value="/dissertationStagingForm", method = {RequestMethod.GET})
	public ModelAndView processPassFailStaging(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ7Staging");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "/searchMarksForDissertation",method = {RequestMethod.POST})
	public ModelAndView searchPassFail(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ7Staging");
		List<DissertationResultBean> upsertList = new ArrayList<DissertationResultBean>();
		try {
			request.getSession().removeAttribute("finalListForStaging");
			if (null != resultBean.getTimebound_id()) {

				//getting list which are in marks
				 upsertList = dissertationQ7Service.getNotProcessedList(resultBean.getTimebound_id());
	
				 request.getSession().setAttribute("finalListForStaging", upsertList);
				 
				if (upsertList.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to Proccessed");
				}
				model.addObject("searchList", upsertList);
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "TimeBound Not Found");
			}
			
		}catch(Exception e) {
			dissertationLogs.info("DissertationQ7 : while searchMarksForDissertation"+ e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found In Result Processing" + " " + e.getMessage());
		
		}
		return getModelView(model, request, resultBean);
	}
	
	
	@RequestMapping(value = "/transferDisserationToStaging",method = {RequestMethod.POST})
	public ModelAndView processPassFailForDissertation(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ7Staging");
		try {
			String loggedInUser =(String)request.getSession().getAttribute("userId");
				

				List<DissertationResultBean> marksListForStaging = (List<DissertationResultBean>) request.getSession().getAttribute("finalListForStaging");
				
				//processing staging list and applying passfail Logic
				List<DissertationResultBean> proccessedList = dissertationQ7Service.processUpsertList(marksListForStaging, loggedInUser);
			
				//inserting processed list in passfailStaging table q7
				int transferCount =	dissertationQ7Service.upsertPassFailStaging(proccessedList);
		
				//update marks table processed  = Y , which have been processed
				int updateCount = dissertationQ7Service.updateUpsertList(proccessedList);
				
				if(transferCount == updateCount) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", transferCount+" "+"Records has been Processed");
					model.addObject("transferedStagingCount", transferCount);
					request.getSession().setAttribute("DissertationResultForQ7", proccessedList);
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Data transfer and update count Mismatch");
				}
				
		}catch(Exception e) {
			dissertationLogs.info("DissertationQ7 :while transferDisserationToStaging"+ e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error Found In Result Processing"+" "+ e.getMessage());
		
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value ="/dissertationPassFailForm", method = {RequestMethod.GET})
	public ModelAndView transferStageToPassFailForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean)  {
		ModelAndView model = new ModelAndView("dissertationQ7PassFail");
		return getModelView(model, request, resultBean);
	}
	
	
	@RequestMapping(value ="/transferDissertationToPassFail", method = {RequestMethod.POST})
	public ModelAndView transferStageToPassFail(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean)  {
		ModelAndView model = new ModelAndView("dissertationQ7PassFail");
		
		try {
			// getting staging all data
			List<DissertationResultBean> transferList = dissertationQ7Service.getStagedDissertationList(resultBean.getTimebound_id());

			if (transferList.size() > 0) {
				// logic implementaion for staging to passfail data Transfer in service layer
				int upsertedList = dissertationQ7Service.transferToPassFail(transferList,resultBean.getTimebound_id());
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", upsertedList + " " + "Records has been Processed");
				request.getSession().setAttribute("DissertationResultForQ7", transferList);
				model.addObject("transferPassFailCount",upsertedList);
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Record Found To Processed");
			}

		} catch (Exception e) {
			dissertationLogs.info("DissertationQ7 :while transferDissertationToPassFail" + e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found In Result Processing" + " " + e.getMessage());

		}

		return getModelView(model, request, resultBean);
	}

	
	@RequestMapping(value="dissertationQ7MakeResultLiveForm",method = RequestMethod.GET)
	public ModelAndView makeResultLiveForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model= new ModelAndView("dissertationQ7ResultLive");
		return getModelView(model, request, resultBean);	
	}
	
	@RequestMapping(value ="dissertationMakeResultLive",method = RequestMethod.POST)
	public ModelAndView makeResultLive(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model =  new ModelAndView("dissertationQ7ResultLive");
		try {
			if (null != resultBean.getTimebound_id()) {
				int liveCount = dissertationQ7Service.makeResultLive(resultBean.getTimebound_id());
				if (liveCount > 0) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", liveCount + " " + "Records has been make live");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to Make Live");
				}
			}
		} catch (Exception e) {
			dissertationLogs.info("DissertationQ7 :while dissertationMakeResultLive" + e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found In Result Processing" + " " + e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value="downloadDissertationResultQ7",method = RequestMethod.GET)
	public ModelAndView downloadResultForDissertation(HttpServletRequest request) {
		List<DissertationResultBean> resultList = (List<DissertationResultBean>) request.getSession().getAttribute("DissertationResultForQ7");
		return new ModelAndView("downloadDissertationResult","dissertationResult",resultList);
	}
	
	@RequestMapping(value ="dissertationQ7GradePointForm",method = RequestMethod.GET)
	public ModelAndView dissertationGradePointForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model =  new ModelAndView("dissertationQ7GradePoints");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "applyQ7GradePoint",method = RequestMethod.POST)
	public ModelAndView applyQ7GradePoint(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model =  new ModelAndView("dissertationQ7GradePoints");
		try {
			if (null != resultBean.getTimebound_id()) {
				int updatedCountForGrade = 0;
				List<DissertationResultBean> passFailStaging = dissertationQ7Service
						.getStagedDissertationList(resultBean.getTimebound_id());
				
				if (!passFailStaging.isEmpty()) {
					
					String loggedInUser = (String) request.getSession().getAttribute("userId");
					List<EmbaGradePointBean> grades =  dissertationQ7Service.getAllGrades();
					List<DissertationResultBean> processListForGrade = dissertationQ7Service
							.applyGradeForQ7(passFailStaging, loggedInUser,grades);
					
					 updatedCountForGrade = dissertationQ7Service
							.upsertGradeInPassFailStaging(processListForGrade);
				
				
				if(updatedCountForGrade > 0) {
					model.addObject("gradeCount", updatedCountForGrade);
					request.getSession().setAttribute("DissertationResultForQ7", processListForGrade);
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", updatedCountForGrade + " " + "Records has been applied Grade");
				} 
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to Make Live");
				}
				
			}
		}
		catch(Exception e) {
			dissertationLogs.info("DissertationQ7 :while applyQ7GradePoint" + e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found While Applying Grade");
		}
		return getModelView(model, request, resultBean);
	}

}
