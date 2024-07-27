package com.nmims.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;

@RestController
@RequestMapping("m")
public class StudentMarksRestController extends BaseController{
	
	@Value( "${SERVER_PATH}" )
	String SERVER_PATH;
	
	private ArrayList<CenterExamBean> centers = null;
	
	public ArrayList<CenterExamBean> getCentersList(){
		if(this.centers == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.centers = dao.getAllCenters();
		}
		return centers;
	}
	
/*NOTE: As of 2022-01-22, commented and kept, latest in StudentMarksController/java - mgetMostRecentResults(). Vilpesh.
	//Mobile Api
		@PostMapping(path = "/getMostRecentResults" , consumes= "application/json", produces = "application/json")
			public ResponseEntity<Map<String,List>> mgetMostRecentResults (@RequestBody StudentExamBean student) throws UnsupportedEncodingException{
			
			if(StringUtils.isBlank(student.getFromAdmin()) ) {
				TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
				
				if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
					//Map<String,List> returnDataFromRedis = restApiCallToGetResultsFromCache(student);
					Map<String,List> returnDataFromRedis = new HashMap<>();
						
					return new ResponseEntity<Map<String,List>>(returnDataFromRedis, HttpStatus.OK);
					
				}

			}
			
			
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			StudentMarksBean bean = new StudentMarksBean();
			bean.setSapid(student.getSapid());
			Map<String,List> result_data = new HashMap<String, List>();
			List passFailStatus = mgetPassFailStatus(student);
			//Temp hide
			//passFailStatus = new ArrayList<>(); 
			result_data.put("passFailStatus",passFailStatus);
			List studentMarksHistory = mgetStudentMarksHistory(student);
			//Temp hide
			//studentMarksHistory = new ArrayList<>();
			result_data.put("studentMarksHistory",studentMarksHistory);
			String centerCode = dao.getStudentCenterDetails(student.getSapid());
*/
			
			/*if(centerCode == null || "".equals(centerCode.trim())){
				modelnView = new ModelAndView("selectCenter");
				ArrayList<CenterBean> centers = getCentersList();
				modelnView.addObject("center",new CenterBean());

				Map<String,String> centerCodes = new LinkedHashMap<String,String>();
				
				for (int i = 0; i < centers.size(); i++) {
					CenterBean cBean = centers.get(i);
					centerCodes.put(cBean.getCenterCode(), cBean.getCenterName());
				}
				modelnView.addObject("centerCodes",centerCodes);
				request.getSession().setAttribute("centerCodes",centerCodes);
				return modelnView;
			}else{
				ArrayList<CenterBean> centers = getCentersList();
				Map<String,CenterBean> centerMap = new LinkedHashMap<String,CenterBean>();
				
				for (int i = 0; i < centers.size(); i++) {
					CenterBean cBean = centers.get(i);
					centerMap.put(cBean.getCenterCode(), cBean);
				}
				modelnView.addObject("center", centerMap.get(centerCode));
				
			}*/
			
/*			if(centerCode != null && !"".equals(centerCode.trim())){
				ArrayList<CenterExamBean> centers = getCentersList();
				Map<String,CenterExamBean> centerMap = new LinkedHashMap<String,CenterExamBean>();
				
				for (int i = 0; i < centers.size(); i++) {
					CenterExamBean cBean = centers.get(i);
					centerMap.put(cBean.getCenterCode(), cBean);
				}
				//modelnView.addObject("center", centerMap.get(centerCode));
				List center = 	new ArrayList<>();
				center.add(centerMap.get(centerCode));
				result_data.put("center",center);
			}else{
				CenterExamBean cBean = new CenterExamBean();
				cBean.setCenterName("Center Information Not Available");
				cBean.setAddress("Please contact head office to get your center information updated.");
				//modelnView.addObject("center", cBean);
				List center = 	new ArrayList<>();
				center.add(center);
				result_data.put("center",center);
			}
			
			String mostRecentResultPeriod = "";
			String declareDate = "";
			List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
			
			if("Online".equals(student.getExamMode())){
				mostRecentResultPeriod = dao.getMostRecentResultPeriod();
				declareDate = dao.getRecentExamDeclarationDate();
				studentMarksList =  dao.getAStudentsMostRecentMarks(bean);
			}else{
				mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
				declareDate = dao.getRecentOfflineExamDeclarationDate();
				studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(bean);
			}
*/			
			/* //Temp Hide start to be rmoved later by PS
			//studentMarksList = new ArrayList<StudentMarksBean>();
			StudentBean studentBean = dao.getSingleStudentsData(student.getSapid());
			if("Diageo".equalsIgnoreCase(studentBean.getConsumerType())){
				studentMarksList = new ArrayList<StudentMarksBean>();
			}
			//end */
			
/*			result_data.put("studentMarksList",studentMarksList);

			List center = 	new ArrayList<>();
			center.add(mostRecentResultPeriod);
			result_data.put("mostRecentResultPeriod",center);
			List center2 = 	new ArrayList<>();
			center2.add(declareDate);
			result_data.put("declareDate",center2);
			List center4 = 	new ArrayList<>();
			center4.add( studentMarksList.size());
			result_data.put("size",center4);
*/			
//			
//			if(studentMarksList.size() == 0){
//				setError(request, "No Marks Entries found for "+mostRecentResultPeriod);
//			}
/*			
			//Added by Pranit on 22Apr20 start
			StudentExamBean studentDetailsFromDB = dao.getSingleStudentsData(student.getSapid());

			List studentDetailsArray = 	new ArrayList<>();
			studentDetailsArray.add(studentDetailsFromDB);
			
			result_data.put("studentDetails",studentDetailsArray);
			//Added by Pranit on 22Apr20 end
			
			return new ResponseEntity<Map<String,List>>(result_data, HttpStatus.OK);
		}
*///end-NOTE: As of 2022-01-22.
	
		public Map<String,List> restApiCallToGetResultsFromCache(StudentExamBean student) {

			RestTemplate restTemplate = new RestTemplate();
			Map<String,List> returnBean = new HashMap<>();
			try {
				  String url = SERVER_PATH+"timeline/api/results/getResultsDataFromRedisBySapid";
		    	  HttpHeaders headers = new HttpHeaders();
				  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				  HttpEntity<StudentExamBean> entity = new HttpEntity<StudentExamBean>(student,headers);
				  
				  StudentsDataInRedisBean bean = (StudentsDataInRedisBean)  restTemplate.exchange(
					 url,
				     HttpMethod.POST, entity, StudentsDataInRedisBean.class).getBody();
				  if(bean != null && bean.getResultsData() != null) {
					  return bean.getResultsData();
				  }
			} catch (Exception e) {
				
			}
			
			return returnBean;
		}
		
		
		@PostMapping(path = "/getMostRecentAssignmentResults", consumes= "application/json", produces = "application/json")
		public ResponseEntity<Map<String,List>> mgetMostRecentAssignmentResults (@RequestBody StudentExamBean student) throws UnsupportedEncodingException{
		
		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		
		if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			Map<String,List> returnDataFromRedis = new HashMap<>();
			return new ResponseEntity<Map<String,List>>(returnDataFromRedis, HttpStatus.OK);
		}
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentMarksBean bean = new StudentMarksBean();

		//String sapId = request.getParameter("sapId"); For testing
		bean.setSapid(student.getSapid());
		String mostRecentResultPeriod = dao.getMostRecentAssignmentResultPeriod();
		//String declareDate = dao.getRecentExamDeclarationDate();
		List<StudentMarksBean> studentMarksList =  dao.getAStudentsMostRecentAssignmentMarks(bean);
		
		Map<String,List> result_data = new HashMap<String, List>();

		/* //Temp Hide start to be rmoved later by PS
		StudentBean studentBean = dao.getSingleStudentsData(student.getSapid());
		if("Diageo".equalsIgnoreCase(studentBean.getConsumerType())){
			studentMarksList = new ArrayList<StudentMarksBean>();
		}
		//end */
		
		//modelnView.addObject("declareDate", declareDate);
		result_data.put("studentMarksList", studentMarksList);
			
		
		List center1 = 	new ArrayList<>();
		center1.add(mostRecentResultPeriod);
		result_data.put("mostRecentResultPeriod",center1);
		
		
		List center2 = 	new ArrayList<>();
		center2.add( studentMarksList.size());
		result_data.put("size",center2);
		
//		if(studentMarksList.size() == 0){
//			setError(request, "No Assignment Marks found for "+mostRecentResultPeriod);
//		}
		
		return new ResponseEntity<Map<String,List>>(result_data, HttpStatus.OK);

	}
		
		
		private List mgetPassFailStatus(StudentExamBean student) {
			List result_data = new ArrayList<>();

				StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
				StudentMarksBean bean = new StudentMarksBean();

				//String sapId = request.getParameter("sapId"); For testing
				bean.setSapid(student.getSapid());
				
				
				
			
				String mostRecentResultPeriod = "";
				String declareDate = "";
				List<PassFailExamBean> studentMarksListForPassFail = new ArrayList<PassFailExamBean>();
				//JUL2013 is online course//
				if("Online".equals(student.getExamMode())){
					mostRecentResultPeriod = dao.getMostRecentResultPeriod();
					//declareDate = dao.getRecentExamDeclarationDate();
					studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Online");
				}else{
					mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
					//declareDate = dao.getRecentOfflineExamDeclarationDate();
					studentMarksListForPassFail =  dao.getAStudentsMostRecentPassFailMarks(bean, "Offline");
				}
				

				result_data.add(declareDate);
				
			
				result_data.add(studentMarksListForPassFail);
				
			result_data.add(studentMarksListForPassFail.size());
				
			
				 return result_data;
				
			}
		private List<StudentMarksBean>  mgetStudentMarksHistory(StudentExamBean student){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			StudentMarksBean bean = new StudentMarksBean();
			bean.setSapid(student.getSapid());
			List<StudentMarksBean> studentMarksListForMarksHistory =  null;
			if("Online".equals(student.getExamMode())){
				studentMarksListForMarksHistory = dao.getAStudentsMarksForOnline(bean);
			}else{
				studentMarksListForMarksHistory = dao.getAStudentsMarksForOffline(bean);
			}
			return studentMarksListForMarksHistory;
		}
		
		
		

}
