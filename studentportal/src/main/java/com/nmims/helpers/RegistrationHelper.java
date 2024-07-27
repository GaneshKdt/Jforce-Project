package com.nmims.helpers;

import java.time.Month;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PortalDao;

@Component("registrationHelper")
public class RegistrationHelper {
	
	@Autowired
	ApplicationContext act;
	
	@Value("${CURRENT_ACAD_MONTH}")
	 String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	 String CURRENT_ACAD_YEAR;
	
	
	 public StudentStudentPortalBean checkStudentRegistration(String sapId, StudentStudentPortalBean student) {
			
			StudentStudentPortalBean studentRegistrationData = new StudentStudentPortalBean();
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			
			studentRegistrationData = pDao.getStudentRegistrationDetails(sapId);

			if ((!"111".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"151".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"131".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"17".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"153".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"150".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"154".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"155".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"156".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"157".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
					&& (!"158".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))) {
				String month_curr = "";
				String month_reg = "";
				if(studentRegistrationData.getMonth().equals("Jan"))
					month_reg = "JANUARY";
				else
					month_reg = "JULY";
				
				if(CURRENT_ACAD_MONTH.equals("Jan")) {
					month_curr = "JANUARY";}
				else 
					month_curr = "JULY";
				
				Month reg_m = Month.valueOf(month_reg);	
				Month curr_m = Month.valueOf(month_curr);
				
				YearMonth reg_date = YearMonth.of(Integer.parseInt(studentRegistrationData.getYear()) ,reg_m);
				YearMonth curr_date = YearMonth.of(Integer.parseInt(CURRENT_ACAD_YEAR) ,curr_m);
				
				
				if(reg_date.compareTo(curr_date) < 0) {
					studentRegistrationData = null;
				}
			}
			return studentRegistrationData;
		}
	 
	 public StudentMarksBean CheckStudentRegistrationForCourses(HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,double acadContentLiveOrder,double current_order,double reg_order,List<ExamOrderStudentPortalBean> liveFlagList){
	 
		StudentMarksBean studentRegistrationForCourses=null;
		
			for(ExamOrderStudentPortalBean bean:liveFlagList){
				if(	Double.parseDouble(bean.getOrder())==reg_order){
					studentRegistrationForCourses= monthYearAndStudentRegistrationMap.get(bean.getAcadMonth()+"-"+bean.getYear());
					break;
				}
			}
		
		return studentRegistrationForCourses;
	}

	 
	 public boolean twoAcadCycleCourses(HttpServletRequest request){
		 
		 	boolean current = false;
			try {
				
				   
				double current_order=(double) request.getSession().getAttribute("current_order"); 
			    double reg_order = (double)request.getSession().getAttribute("reg_order");
			    double acadContentLiveOrder=(double) request.getSession().getAttribute("acadContentLiveOrder");
			    
			  
			    
			    if(current_order == reg_order || acadContentLiveOrder == reg_order)
			    {
			    	current = true;
			    }
			    
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return current;
		}
	 
	 
	 public boolean twoAcadCycleCourses(double current_order,double reg_order,double acadContentLiveOrder){
		 
		 	boolean current = false;
				
		    if(current_order == reg_order || acadContentLiveOrder == reg_order)
			    {
			    	current = true;
			    }
			    
			return current;
		}
}
