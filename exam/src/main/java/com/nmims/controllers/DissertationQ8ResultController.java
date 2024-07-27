package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.nmims.interfaces.DissertationQ8ResultService;
import com.nmims.interfaces.DissertationResultProcessingService;

@Controller
@RequestMapping("admin")
public class DissertationQ8ResultController {
	

	@Autowired
	DissertationQ8ResultService dissertationQ8Service;
	
	@Autowired
	DissertationResultProcessingService dissertationQ7Service;
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	
	private static final String Q8SubjectName = "Masters Dissertation Part - II";
	

	public List<BatchExamBean> getBatchList(TEEResultBean resultBean){
		//getting ConsumerProgramStructureId and PssId from subject and sem
		List<Integer> masterKeys = dissertationQ8Service.getConsumerAndSubjectId();
		return dissertationQ8Service.getBatchList(masterKeys);
	}
	
	public ModelAndView getModelView(ModelAndView model,HttpServletRequest request,@ModelAttribute TEEResultBean resultBean ) {
		try {
			List<BatchExamBean> batchList = getBatchList(resultBean);
			model.addObject("subjectName", Q8SubjectName);
			model.addObject("resultBean", resultBean);
			model.addObject("batch", batchList);
		}catch(Exception e) {
			dissertationLogs.info("DissertationQ8 : Error Found in getting BatchDetails "+ e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error Found In Result Processing"+" "+ e.getMessage());
		}
		return model;
	}
	
	@RequestMapping(value ="dissertationQ8ExamResultCheckList",method = RequestMethod.GET)
	public ModelAndView dissertationResultCheckList() {
		ModelAndView model = new ModelAndView("dissertationQ8ResultCheckList");
		return model;
	}
	
	@RequestMapping(value = "/dissertationQ8InsertMarksForm", method = RequestMethod.GET)
	public ModelAndView dissertationResultProcessingForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Marks");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value ="insertDissertationQ8Marks",method = RequestMethod.POST)
	public ModelAndView insertDissertationQ8Marks(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Marks");
		List<DissertationResultBean> errorList  =  new ArrayList<DissertationResultBean>();	
		
		try {
			String timeBoundId = resultBean.getTimebound_id();
				
			int insertedCount =0;
			String loggerInUser =  (String) request.getSession().getAttribute("userId");
			if (null != timeBoundId) {
				dissertationLogs.info("Dissertation Q8 : " +timeBoundId+" "+"Insert Marks Started");
				List<TEEResultBean> eligibleIAStudents = dissertationQ7Service.getEligibleIAStudents(timeBoundId,
						errorList);
				dissertationLogs.info("Dissertation Q8 : " +timeBoundId+" "+"Total Eligible student Found : "+ eligibleIAStudents.size());
				List<DissertationResultBean> upsertList = dissertationQ8Service.getDissertationQ8Scores(timeBoundId,
						eligibleIAStudents, errorList, loggerInUser);

				insertedCount = dissertationQ8Service.upsertIntoMarks(upsertList);
				dissertationLogs.info("Dissertation Q8 : " +timeBoundId+" "+"Inserted Count in Marks Table : "+ insertedCount);
				
				if (insertedCount > 0) {
					request.getSession().setAttribute("DissertationResultForQ8", upsertList);
					model.addObject("insertedCount", insertedCount);
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", insertedCount + " Record has been Inserted");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to Insert");
				}
			}
			
			
			
		}catch(Exception e){
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In InsertMarksForm"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+" "+ e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value ="dissertationQ8StagingForm", method = RequestMethod.GET)
	public ModelAndView dissertationQ8StagingForm(HttpServletRequest request ,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Staging");
		return getModelView(model, request, resultBean);
		
	}
	
	@RequestMapping(value ="searchDissertaionQ8Marks",method = RequestMethod.POST)
	public ModelAndView searchDissertaionQ8Marks(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Staging");
		try {
			if(null!=resultBean.getTimebound_id()) {
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" marks to staging Search started " );
				List<DissertationResultBean> searchList = dissertationQ8Service.getQ8MarksList(resultBean.getTimebound_id());
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+"Total student Found for marks to staging: "+ searchList.size());
				if(!searchList.isEmpty()) {
					request.getSession().setAttribute("marksforStaging", searchList);
					model.addObject("searchList",searchList);
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to Proceed");
				}
			}
		}catch(Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In searchDissertaionQ8Marks"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+" "+ e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value="transfermarksToStaging",method = RequestMethod.POST)
	public ModelAndView transfermarksToStaging(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Staging");
		try {
			dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" marks to staging transfer started " );
			List<DissertationResultBean> upsertListForStaging = (List<DissertationResultBean>) request.getSession().getAttribute("marksforStaging");
			dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" Total Record for transfer : "+ upsertListForStaging.size());
			if(!upsertListForStaging.isEmpty()) {
				String loggedInUser =  (String) request.getSession().getAttribute("userId");
				List<DissertationResultBean> finalUpsertListForStaging = dissertationQ8Service.processUpsertList(upsertListForStaging,loggedInUser);
				
				int upsertCount = dissertationQ8Service.upsertMarkstoStaging(finalUpsertListForStaging);
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total Record transferd is :  "+upsertCount );
				
				int updateMarks = dissertationQ8Service.updateMarksProccessed(finalUpsertListForStaging);
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total record updated in marks for processed = Y is :  "+updateMarks );
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", upsertCount+" "+"Record has been Transfered");
				
				model.addObject("upsertListForStaging",upsertCount);
				request.getSession().setAttribute("DissertationResultForQ8", finalUpsertListForStaging);
			}
		}catch(Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In transfermarksToStaging"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+" "+ e.getMessage());
		}
		
		return getModelView(model, request, resultBean);	
	}
	
	@RequestMapping(value = "disseratationGraceForm",method = RequestMethod.GET)
	public ModelAndView dissertationGraceForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Grace");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "searchApplicateGraceStudents",method = RequestMethod.POST)
	public ModelAndView searchApplicateGraceStudents(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Grace");
		try {
			if (null != resultBean.getTimebound_id()) {
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" search grace started");

				List<DissertationResultBean> graceStudent = dissertationQ8Service.getGraceApplicabeStudent(resultBean.getTimebound_id());
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total Record  for grace  is :  "+graceStudent.size() );

				if (!graceStudent.isEmpty()) {
					model.addObject("graceStudent", graceStudent);
					request.getSession().setAttribute("applicableGraceStudents", graceStudent);
				
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Grace Applicable Student Found");
				}
			}
		} catch (Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In searchApplicateGraceStudents"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+" "+ e.getMessage());
		}
	
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "dissertationApplyGrace",method = RequestMethod.POST)
	public ModelAndView dissertationApplyGrace(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("dissertationQ8Grace");
		TEEResultBean resultBean =  new TEEResultBean();
		try {
			
			List<DissertationResultBean> graceStudent = (List<DissertationResultBean>) request.getSession().getAttribute("applicableGraceStudents");
			dissertationLogs.info("Dissertation Q8 : " +graceStudent.size()+" "+" total Record for applying grace is  :  "+graceStudent.size() );

			List<DissertationResultBean> processedGrace = dissertationQ8Service.applyGrace(graceStudent);
			
			int updateGraceList = dissertationQ8Service.upsertGraceList(processedGrace);
			dissertationLogs.info("Dissertation Q8 : " +updateGraceList+" "+" total Record that have been applied grace and update  :  "+updateGraceList );
			
			if (updateGraceList > 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", updateGraceList + " " + "Record has been applied Grace");
				model.addObject("graceCount", updateGraceList);
				request.getSession().setAttribute("DissertationResultForQ8", processedGrace);
			}
			
		}catch(Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In dissertationApplyGrace"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+" "+ e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "dissertationQ8PassFailForm", method = RequestMethod.GET)
	public ModelAndView dissertationQ8PassFailForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8PassFail");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "dissertationQ8PassFailProcess" , method = RequestMethod.POST)
	public ModelAndView dissertationQ8PassFailProcess(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		
		ModelAndView model = new ModelAndView("dissertationQ8PassFail");
		try {
			if (null != resultBean.getTimebound_id()) {
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" Transfer to pass Fail Started ");
				List<DissertationResultBean> passFailList = dissertationQ8Service.getPassFailStaging(resultBean.getTimebound_id());
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total Record to tramsfer to pass fail found from staging is :  "+passFailList.size() );
				
				if (!passFailList.isEmpty()) {
					
					int processCount = dissertationQ8Service.transferToPassFail(resultBean.getTimebound_id(),passFailList);
					dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total Record transfered to passfail  :  "+processCount);
					
					if (processCount > 0) {
						request.setAttribute("success", "true");
						request.setAttribute("successMessage", processCount + " " + "Record has been Processed");
						model.addObject("passFailCount", processCount);
						request.getSession().setAttribute("DissertationResultForQ8", passFailList);
					} 
					
				}else {		
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to be Processed");
				}
			}
			

		} catch (Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In dissertationQ8PassFailProcess"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : "+ e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "dissertationQ8MakeLiveForm",method = RequestMethod.GET)
	public ModelAndView dissertationQ8MakeLiveForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8MakeLive");
		return getModelView(model, request, resultBean);
	}
	
	@RequestMapping(value = "dissertationQ8MakeLive", method = RequestMethod.POST)
	public ModelAndView dissertationQ8MakeLive(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		
		ModelAndView model = new ModelAndView("dissertationQ8MakeLive");
		try {
			if (null != resultBean.getTimebound_id()) {
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" make live started");
				int makeLiveCount = dissertationQ8Service.makeLive(resultBean.getTimebound_id());
				
				dissertationLogs.info("Dissertation Q8 : " +resultBean.getTimebound_id()+" "+" total Record that have been make live is  :  "+makeLiveCount );
					if(makeLiveCount > 0) {
						request.setAttribute("success", "true");
						request.setAttribute("successMessage", makeLiveCount+" "+"Record has been Live");
					}else {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "No Record Found to be Processed");
					}
			}
		} catch (Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In dissertationQ8MakeLive"+" "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Found : " + " " + e.getMessage());
		}
		return getModelView(model, request, resultBean);
	}
	
	
	@RequestMapping(value="downloadDissertationResultQ8",method = RequestMethod.GET)
	public ModelAndView downloadResultForDissertation(HttpServletRequest request) {
		List<DissertationResultBean> resultList = (List<DissertationResultBean>) request.getSession().getAttribute("DissertationResultForQ8");
		return new ModelAndView("downloadDissertationQ8Result","dissertationQ8Result",resultList);
	}
	
	@RequestMapping(value = "disseratationQ8GradeForm", method = RequestMethod.GET)
	public ModelAndView disseratationQ8GradeForm(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		
		ModelAndView model = new ModelAndView("dissertationQ8Grade");
		return getModelView(model, request, resultBean);
	}
	
	
	@RequestMapping(value = "applyQ8Grade",method = RequestMethod.POST)
	public ModelAndView applyQ8Grade(HttpServletRequest request,@ModelAttribute TEEResultBean resultBean) {
		ModelAndView model = new ModelAndView("dissertationQ8Grade");
		try {
			int countUpdate =0;
			if (null != resultBean.getTimebound_id()) {
				String loggedInUser = (String) request.getSession().getAttribute("userId");
				List<DissertationResultBean> passFailStaging = dissertationQ8Service
						.getPassFailStaging(resultBean.getTimebound_id());

				if (!passFailStaging.isEmpty()) {
					List<EmbaGradePointBean> gradeList = dissertationQ8Service.getAllGrades();
					List<DissertationResultBean> upsertList = dissertationQ8Service.applyGrade(passFailStaging,
							loggedInUser, gradeList);
					countUpdate = dissertationQ8Service.upsertGrade(upsertList);

					if (countUpdate > 0) {
						model.addObject("gradeCount",countUpdate);
						request.getSession().setAttribute("DissertationResultForQ8", upsertList);
						request.setAttribute("success", "true");
						request.setAttribute("successMessage", countUpdate + " " + "Record has been Live");
					}
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Record Found to be Processed");
				}
			}
		}catch(Exception e) {
			dissertationLogs.info("Dissertation Q8 : "+" "+"Error In applyQ8Grade"+" "+e);
		}

		return getModelView(model, request, resultBean);
	}
}
