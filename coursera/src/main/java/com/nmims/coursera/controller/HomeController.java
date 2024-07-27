package com.nmims.coursera.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nmims.coursera.beans.Person;
import com.nmims.coursera.beans.StudentCourseraBean;
import com.nmims.coursera.helpers.AESencrp;
import com.nmims.coursera.services.StudentService;


@Controller
public class HomeController {
	
	@Autowired
	StudentService studentService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String test(HttpServletRequest request) {
		return "Hello World";
	}
	
	@GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("message", "Spring Boot application that uses JSP With Embedded Tomcat");
        return "index";
    }
	
	@RequestMapping(value = "/loginforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String loginforSSO(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		try {
			String emailId = "";
			Boolean logout = false;
			request.getSession().setAttribute("logout", logout);
			
			request.getSession().setAttribute("validityExpired","No");
			request.getSession().setAttribute("earlyAccess", "No");
		
			String userIdEncrypted = request.getParameter("uid");
			String userId = AESencrp.decrypt(userIdEncrypted);
			
			if(userId.equals(request.getSession().getAttribute("userId_coursera")) ){
				//Session already created. Don't fire another query on DB
				return null;
			}
			
			if(isEmail(userId)) {
				emailId = userId;
				userId = "77999999999";
		    	request.getSession().setAttribute("isLoginAsLead", "true");
			}else
				request.getSession().setAttribute("isLoginAsLead", "fasle");
			
			request.getSession().setAttribute("emailId", emailId);
			request.getSession().setAttribute("userId_acads", userId);
			//Used to get a login over all the page
			
			StudentCourseraBean student = studentService.getSingleStudentsData(userId);
			request.getSession().setAttribute("student_coursera", student);
			
			Person person = new Person();
			person.setUserId(userId);
			request.getSession().setAttribute("user_coursera",person);
			System.out.println("COURSERA APP: User logged in "+person.getUserId());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isEmail(String email){ 
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        boolean match =  mat.matches();
        return match;
    } 
}
