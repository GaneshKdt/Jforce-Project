package com.nmims.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CacheRefreshStudentPortalBean;
import com.nmims.interfaces.SystemPanelInterface;

@Controller
public class SystemPanelController extends BaseController implements SystemPanelInterface {

	@Value("${SERVER_HOST}")
	private String host;
	
	
	@RequestMapping(value="/systemPanel",method= {RequestMethod.GET})
	@Override
	public String panelView(HttpServletRequest request,HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return "redirect:/login";
		}
		
		return "jsp/SystemPanel";
	}

	
	@RequestMapping(value="/ServerPing",method= {RequestMethod.GET})
	@Override
	public @ResponseBody CacheRefreshStudentPortalBean pingServer(HttpServletRequest request) throws IOException {
		
		String port = request.getParameter("port");
		String project = request.getParameter("project");
		boolean result = this.PingServerPort(port,project);
		CacheRefreshStudentPortalBean response = new CacheRefreshStudentPortalBean();
		if(result) {
			response.setMessage("Successfully ping");
			response.setStatus("200");
			return response;
		}else {
			response.setMessage("failed ping");
			response.setStatus("500");
			return response;
		}
	}
	
	
	
	private boolean PingServerPort(String port,String project) throws IOException {
		String path = "/"+ project +"/TestingPingRequest";
		try {
			
				URL obj = new URL("http://" + this.host + ":" + port + path);
				HttpURLConnection  con = (HttpURLConnection ) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				System.out.println("GET Response Code :: " + responseCode);
				if (responseCode == HttpURLConnection.HTTP_OK) {
					System.out.println("request completed");
					return true;
				} else {
					System.out.println("GET request not worked,System with port no : "+ port +" failed to ping");
					return false;
				}
		}
		catch (Exception e) {
			System.out.println("Error while Connecting with port : " + port + " project : " + project);
			return false;
		}
	
	}

	@RequestMapping(value="/TestingPingRequest",method= {RequestMethod.GET})
	@Override
	public String TestingPingRequest() {
		return "jsp/TestingPingRequest";
	}
	
}
