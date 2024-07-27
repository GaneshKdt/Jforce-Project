package com.nmims.interfaces;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CacheRefreshStudentPortalBean;

public interface SystemPanelInterface {

	public String panelView(HttpServletRequest request,HttpServletResponse response);
	
	public @ResponseBody CacheRefreshStudentPortalBean pingServer(HttpServletRequest request) throws IOException;
	
	public String TestingPingRequest();
	
}
