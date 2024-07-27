package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.StudentMarksDAO;


@Controller
public class DashboardController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017" , "2018" ,"2019", "2020")); 
	private HashMap<String, String> centerCodeNameMap = null; 
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	
	
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
		
		programList = null;
		getProgramList();
		
		centerCodeNameMap = null;
		getCenterCodeNameMap();
		
		
		
		return null;
	}
	
	public HashMap<String, String> getCenterCodeNameMap(){
		if(this.centerCodeNameMap == null || this.centerCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<CenterExamBean> centers = dao.getAllCenters();
			centerCodeNameMap = new HashMap<>();
			for (int i = 0; i < centers.size(); i++) {
				CenterExamBean cBean = centers.get(i);
				centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
			}
		}
		return centerCodeNameMap;
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null || this.subjectList.size()==0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		if(this.programList == null || this.programList.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	@RequestMapping(value = "/admin/dashboard", method = {RequestMethod.GET, RequestMethod.POST})
	public String goToHome(HttpServletRequest request, HttpServletResponse response, Model m,  @ModelAttribute StudentExamBean student) {
		
		
		if(student == null){
			student = new StudentExamBean();
		}
		
		if(!checkSession(request, response)){
			return "login";
			//Check is user is Admin.
		}
		
		
		DashboardDAO dao = (DashboardDAO)act.getBean("dashboardDAO");
		ArrayList<PassFailExamBean> list = dao.getPassFailByLCIC(student);
		String NoOfStudentsByMarksData = getStudentsDataByMarksBucket(student, list);
		String passFailPercentData = getPassFailPercentData(student, list);
		
		m.addAttribute("NoOfStudentsByMarksData", NoOfStudentsByMarksData);
		m.addAttribute("passFailPercentData", passFailPercentData);
		
		m.addAttribute("student", student);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("centerList", getCenterCodeNameMap());
		
		return "dashboard";
	}


	private String getPassFailPercentData(StudentExamBean student,ArrayList<PassFailExamBean> list) {
		int passSubjects = 0;
		int failSubjects = 0;
		
		
		for (PassFailExamBean passFailBean : list) {
			String isPass = passFailBean.getIsPass();
			if("Y".equals(isPass)){
				passSubjects++;
			}else if("N".equals(isPass)){
				failSubjects++;
			}
			
		}
		
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("label","Pass");
		obj.put("value",passSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","Fail");
		obj.put("value",failSubjects);
		arr.add(obj);
		
		return arr.toJSONString();
	}


	private String getStudentsDataByMarksBucket(StudentExamBean student, ArrayList<PassFailExamBean> list) {
		
		int zeroToTenMarksStudentSubjects = 0;
		int tenToTwentyMarksStudentSubjects = 0;
		int twentyToThirtyMarksStudentSubjects = 0;
		int thirtyToFortyMarksStudentSubjects = 0;
		int fortyToFiftyMarksStudentSubjects = 0;
		int FiftyToSixtyMarksStudentSubjects = 0;
		int sixtyToSeventyMarksStudentSubjects = 0;
		int seventyToEightyMarksStudentSubjects = 0;
		int eightyToNinetyMarksStudentSubjects = 0;
		int ninetyToHundredMarksStudentSubjects = 0;
		
		for (PassFailExamBean passFailBean : list) {
			
			int total = 0;
			String totalString = passFailBean.getTotal();
			if(totalString != null && !"".equals(totalString.trim())){
				total = Integer.parseInt(passFailBean.getTotal());
			}
			if(total >= 0 && total <= 10){
				zeroToTenMarksStudentSubjects++;
			}else if(total >= 11 && total <= 20){
				tenToTwentyMarksStudentSubjects++;
			}else if(total >= 21 && total <= 30){
				twentyToThirtyMarksStudentSubjects++;
			}else if(total >= 31 && total <= 40){
				thirtyToFortyMarksStudentSubjects++;
			}else if(total >= 41 && total <= 50){
				fortyToFiftyMarksStudentSubjects++;
			}else if(total >= 51 && total <= 60){
				FiftyToSixtyMarksStudentSubjects++;
			}else if(total >= 61 && total <= 70){
				sixtyToSeventyMarksStudentSubjects++;
			}else if(total >= 71 && total <= 80){
				seventyToEightyMarksStudentSubjects++;
			}else if(total >= 81 && total <= 90){
				eightyToNinetyMarksStudentSubjects++;
			}else if(total >= 91 && total <= 100){
				ninetyToHundredMarksStudentSubjects++;
			}
			
		}
		
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("label","0-10");
		obj.put("value",zeroToTenMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","11-20");
		obj.put("value",tenToTwentyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","21-30");
		obj.put("value",twentyToThirtyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","31-40");
		obj.put("value",thirtyToFortyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","41-50");
		obj.put("value",fortyToFiftyMarksStudentSubjects);
		arr.add(obj);
		
		
		obj = new JSONObject();
		obj.put("label","51-60");
		obj.put("value",FiftyToSixtyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","61-70");
		obj.put("value",sixtyToSeventyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","71-80");
		obj.put("value",seventyToEightyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","81-90");
		obj.put("value",eightyToNinetyMarksStudentSubjects);
		arr.add(obj);
		
		obj = new JSONObject();
		obj.put("label","91-100");
		obj.put("value",ninetyToHundredMarksStudentSubjects);
		arr.add(obj);
		
		return arr.toJSONString();
	}
	
}
