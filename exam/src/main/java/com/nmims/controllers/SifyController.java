package com.nmims.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.SifyDAO;
import com.nmims.daos.StudentMarksDAO;

@Controller
public class SifyController extends BaseController{

	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	SifyDAO sifyDAO;

	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	@Value("#{'${STUDENT_TYPE_LIST}'.split(',')}")
	private List<String> STUDENT_TYPE_LIST;
	
	@Value("#{'${SIFY_URL}'}")
	private String SIFY_URL ;
	
	@Value("#{'${SIFY_TOKEN_URL}'}")
	private String SIFY_TOKEN_URL ;
	
	private ArrayList<String> subjectList = null;
	private ArrayList<Integer> subjectCodeList = new ArrayList<Integer>();
	
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		
		subjectList = null;
		getSubjectList();
		
		subjectCodeList = null;
		getSubjectCodeList();
		
		return null;
	}

	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	
	public ArrayList<Integer> getSubjectCodeList(){
		
		if( this.subjectCodeList == null ||this.subjectCodeList.isEmpty()){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectCodeList = dao.getSubjectsCodeList();
		}
		return subjectCodeList;
	}
	
	@RequestMapping(value="/readSifyDataForm")
	public ModelAndView readSifyDataForm(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute SifyMarksBean sifyMarksBean) {
		ModelAndView mav=new ModelAndView("sify/sifyDataForm");
		mav.addObject("yearList",ACAD_YEAR_LIST);
		mav.addObject("monthList",EXAM_MONTH_LIST);
		mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
		mav.addObject("sifyMarksBean",sifyMarksBean);
		mav.addObject("rowCount",0);
		HashMap<String,Integer> missingCodes = sifyDAO.getMissingSubjectCodes();
		if(missingCodes.size()>0){
			String s ="";
			for(Entry<String,Integer> e :missingCodes.entrySet()){
				s+="No Sify Code Specified for "+e.getKey()+"<br/>";
			}
			setError(request, s);
		}
		return mav;
	}
	
	@RequestMapping(value="/readSifyMarksFromAPI")
	public ModelAndView readSifyMarksFromAPI(HttpServletRequest request,
			HttpServletResponse response,@ModelAttribute SifyMarksBean sifyMarksBean) throws IOException, ParseException { 
	
		ModelAndView mav=new ModelAndView("sify/sifyDataForm");
	    mav.addObject("yearList",ACAD_YEAR_LIST);
	    mav.addObject("monthList",EXAM_MONTH_LIST);
	    mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
	    
	    //Commented below and hardcoded later as cycle is Apr2019 and driveName by sify may2019 by PS
	    //String drive=""+sifyMarksBean.getMonth().toLowerCase()+""+sifyMarksBean.getExamCode();
	    String drive = "sep2019";
	    
     	String year = sifyMarksBean.getExamCode();
	    String month = sifyMarksBean.getMonth();
		String studentType = sifyMarksBean.getStudentType();
	    HashMap<Integer,String> subjectCodesMap = sifyDAO.getUniqueSubjectCodes();
	    String SIFY_TOKEN = generateSifyToken(request);
	    if(SIFY_TOKEN != null){
	    for(int subject_code : subjectCodesMap.keySet()){
	    	ObjectMapper mapper=new ObjectMapper();
	    	String inline="";
	    	String sify_url = SIFY_URL+"?tkno="+SIFY_TOKEN+"&drive="+drive+"&subject_code="+subject_code;
	    	URL url = new URL(sify_url);
	    	HttpURLConnection conn=(HttpURLConnection) url.openConnection();
	    	conn.setRequestMethod("GET");
	    	conn.connect();
	    	int responseCode=conn.getResponseCode();
	    	if(responseCode!=200) {
	    		throw new RuntimeException("HTTPResponseCode "+responseCode);
		    }else {
	        }
	    	Scanner sc=new Scanner(url.openStream());
	    	while (sc.hasNext()) {
	    		inline+=sc.nextLine();
	    	}
	    	sc.close();
	    	JSONParser jsonParse=new JSONParser();
	    	jsonParse.parse(inline);
	    	JsonNode rootNode = mapper.readTree(inline);
	    	JsonNode drNode = rootNode.path("data");
	    	Iterator<JsonNode> itr = drNode.getElements();
	    	ArrayList<SifyMarksBean> details=new ArrayList<>();
	    	
	    	try{
	    	while (itr.hasNext()) {
	    		
	    		JsonNode temp = itr.next();
	    		String examDate = temp.get("exam_date").toString().replaceAll("^\"|\"$", "");
	    		String sapid = temp.get("membership_no").toString().replaceAll("^\"|\"$", ""); 
	    		int subjectCode = temp.get("subject_code").asInt();
	    		String subject = subjectCodesMap.get(subjectCode);  
	    		String name =  temp.get("name").toString().replaceAll("^\"|\"$", "");
	    		Double totalScore = temp.get("totalscore").asDouble();
	    		Double sectionOneMarks = temp.get("section1").asDouble();
	    		Double sectionTwoMarks = temp.get("section2").asDouble();
	    		Double sectionThreeMarks = temp.get("section3").asDouble();
	    		Double sectionFourMarks = temp.get("section4").asDouble();
		     
	    		SifyMarksBean sbm=new SifyMarksBean();
		     	sbm.setExamDate(examDate);
		     	sbm.setExamCode(year);
		     	sbm.setSapid(sapid);
		     	sbm.setSubjectCode(subjectCode);
		     	sbm.setSubject(subject);
		     	sbm.setName(name);
		     	sbm.setTotalScore(totalScore);
			    sbm.setSectionOneMarks(sectionOneMarks);
			    sbm.setSectionTwoMarks(sectionTwoMarks);
			    sbm.setSectionThreeMarks(sectionThreeMarks);
			    sbm.setSectionFourMarks(sectionFourMarks);
			    sbm.setYear(year);
			    sbm.setMonth(month);
			    sbm.setStudentType(studentType);
			    details.add(sbm);
       
        }
	    	}catch(Exception e){
	    		
	    	}
	    	sifyDAO.addData(details); 
	    	
	    	
	}
	    }else{
	    	return mav;
	    }
      
      List<SifyMarksBean> sifyMarksListSummary=sifyDAO.getTotalDataSummary(year, month);
      if(sifyMarksListSummary.isEmpty() || sifyMarksListSummary.size() == 0 ){
			setError(request,"No Data Found !");
      }else{
			setSuccess(request, "Data Added Successfully");
      }
      request.getSession().setAttribute("sifyMarksListSummary",sifyMarksListSummary);
      mav.addObject("rowCount",sifyMarksListSummary.size());
      return mav;
 }
	
	
	@RequestMapping(value="/viewSifyMarks",method={RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView viewSifyMarks(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute SifyMarksBean sifyMarksBean) {
		ModelAndView mav=new ModelAndView("sify/viewSifyMarks");
		try {
			mav.addObject("sifyMarksBean",sifyMarksBean);
			mav.addObject("subjectList", getSubjectList());
			mav.addObject("monthList", EXAM_MONTH_LIST);
			mav.addObject("yearList", ACAD_YEAR_LIST);
			mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
			mav.addObject("subjectCodeList", getSubjectCodeList());
			mav.addObject("rowCount", 0);
		}
		catch (Exception e) {
			
			setError(request,"Error in fetching results");
		}
		return mav;
	}

	@RequestMapping(value="/sifyData",method={RequestMethod.GET,RequestMethod.POST}) 
	public ModelAndView sifyData(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute SifyMarksBean sifyMarksBean) {
		ModelAndView mav=new ModelAndView("sify/viewSifyMarks");
		try {
			String year= sifyMarksBean.getExamCode();
			String month=sifyMarksBean.getMonth();
			String subject=sifyMarksBean.getSubject();
			String studentType=sifyMarksBean.getStudentType();
			int subjectCode = sifyMarksBean.getSubjectCode();
			List<SifyMarksBean> sifyMarksList=sifyDAO.getData(year,month,subject,subjectCode,studentType);
			List<SifyMarksBean> sifyMarksListSummary=sifyDAO.getDataSummary(year,month,subject,subjectCode,studentType);
			if(sifyMarksList.isEmpty() || sifyMarksList.size() == 0 ){
				setError(request,"No Data Found !");
			}
			mav.addObject("sifyMarksList",sifyMarksList);
			mav.addObject("subjectList", getSubjectList());
			mav.addObject("monthList", EXAM_MONTH_LIST);
			mav.addObject("yearList", ACAD_YEAR_LIST);
			mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
			mav.addObject("subjectCodeList", getSubjectCodeList());
			mav.addObject("rowCount", sifyMarksList.size() );
			request.getSession().setAttribute("sifyMarksListSummary", sifyMarksListSummary);

		}
		catch (Exception e) {
			
			setError(request,"Error in fetching results");
		}
		return mav;
	}
	
	@RequestMapping(value="/summaryReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView summaryReport(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<SifyMarksBean> sifyMarksListSummary =	(List<SifyMarksBean>)request.getSession().getAttribute("sifyMarksListSummary");
		return new ModelAndView("sifySummaryReportView","sifyMarksList",sifyMarksListSummary);
	}
	
	@RequestMapping(value="/transferSifyResultsToOnlineMarksForm")
	public ModelAndView transferSifyResultsToOnlineMarksForm(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SifyMarksBean sifyMarksBean)
		{
			ModelAndView mav=new ModelAndView("sify/sifyResultsTransferToMarksTable");
			mav.addObject("monthList", EXAM_MONTH_LIST);
			mav.addObject("yearList", ACAD_YEAR_LIST);
			mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
			return mav;
		}
	
	@RequestMapping(value="/transferSifyResultsToOnlineMarks")
	public ModelAndView transferSifyResultsToOnlineMarks(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SifyMarksBean sifyMarksBean)
	{
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mav=new ModelAndView("sify/sifyResultsTransferToMarksTable");
	
		mav.addObject("monthList", EXAM_MONTH_LIST);
		mav.addObject("yearList", ACAD_YEAR_LIST);
		mav.addObject("studentTypeList", STUDENT_TYPE_LIST);
		
		ArrayList<SifyMarksBean> listOfSifyData= sifyDAO.dataFromSify(sifyMarksBean.getExamCode(),sifyMarksBean.getMonth(),sifyMarksBean.getStudentType());
		//HashMap<Integer,ProgramSubjectMappingBean> programSubjectMap = sifyDAO.getUniqueSubjectCodeProgramMap();
		ArrayList<OnlineExamMarksBean> listOfExamMarks=new ArrayList<>();
		for(SifyMarksBean marks:listOfSifyData) {
			OnlineExamMarksBean onlineExamMarksBean=new OnlineExamMarksBean();
			
			//Check if student had registered for exam
			ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>)studentMarksDAO.getConfirmedBookingForGivenYearMonth(marks.getSapid(), marks.getSubject(), marks.getYear(), marks.getMonth());
		
			if(bookingList == null || bookingList.size() == 0 || bookingList.size() > 1){
				onlineExamMarksBean.setErrorMessage(onlineExamMarksBean.getErrorMessage()+" Exam Registration not found for record with SAPID:"+marks.getSapid()+ " & SUBJECT:"+marks.getSubject());
				onlineExamMarksBean.setErrorRecord(true);
			}else{
				onlineExamMarksBean.setSem(bookingList.get(0).getSem());
				onlineExamMarksBean.setProgram(bookingList.get(0).getProgram());
			}
			
			/*if(programSubjectMap.containsKey(marks.getSubjectCode())){
				onlineExamMarksBean.setSem(programSubjectMap.get(marks.getSubjectCode()).getSem());
				onlineExamMarksBean.setProgram(programSubjectMap.get(marks.getSubjectCode()).getProgram());
			}*/
			onlineExamMarksBean.setSapid(marks.getSapid());
			onlineExamMarksBean.setName(marks.getName());
			double total = marks.getTotalScore();
			onlineExamMarksBean.setTotal(total);
			if(total < 0){
				int roundedTotal = 0;
				onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
			}else{
				int roundedTotal = (int) Math.ceil(total);
				onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
			}
			onlineExamMarksBean.setPart1marks(marks.getSectionOneMarks());
			onlineExamMarksBean.setPart2marks(marks.getSectionTwoMarks());
			onlineExamMarksBean.setPart3marks(marks.getSectionThreeMarks());
			onlineExamMarksBean.setPart4marks(marks.getSectionFourMarks());
			onlineExamMarksBean.setSubject(marks.getSubject());
			onlineExamMarksBean.setYear(marks.getYear());
			onlineExamMarksBean.setMonth(marks.getMonth());
			onlineExamMarksBean.setStudentType(marks.getStudentType());
			onlineExamMarksBean.setCreatedBy(userId);
			onlineExamMarksBean.setLastModifiedBy(userId);
			listOfExamMarks.add(onlineExamMarksBean);
		}
		ArrayList<String> errorlist=studentMarksDAO.batchUpsertOnlineExamMarks(listOfExamMarks);
		if(errorlist.size()==0){
			setSuccess(request, "Data Transferred to Online Marks Table");
		}
		
		return mav;
		
	}
	
	@RequestMapping(value="/admin/examResultProcessingChecklist",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView examResultProcessingChecklist(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("examResultsProcessingChecklist");
		return mav;
	}
	
	public String generateSifyToken(HttpServletRequest request) throws IOException, ParseException{
		String token_url = ""+SIFY_TOKEN_URL;
		String inline="";
	    URL url = new URL(token_url);
	    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		int responseCode =0;
		try{
			responseCode=conn.getResponseCode();
		}catch(Exception e){
			setError(request,"Unable to fetch token to generate Sify url");
			
			return null;
		}
		if(responseCode!=200) {
			throw new RuntimeException("HTTPResponseCode "+responseCode);
	    }else {
	    }
		Scanner sc=new Scanner(url.openStream());
		while (sc.hasNext()) {
			inline+=sc.nextLine();
		}
		sc.close();
		JSONParser jsonParse=new JSONParser();
		JSONObject jobj = (JSONObject)jsonParse.parse(inline); 
		String SIFY_TOKEN =  jobj.get("token").toString();
		
		return SIFY_TOKEN;

	}
	
	@RequestMapping(value="/admin/executiveResultProcessingChecklist",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView executiveResultProcessingChecklist(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("executiveResultsProcessingChecklist");
		return mav;
	}
    
}
