package com.nmims.listeners;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.stereotype.Component;


public class ExamBookingSessionListener implements HttpSessionListener{

	private static int noOfActiveUsers = 0;
	
	@Override
	public void sessionCreated(HttpSessionEvent hse) {
		HttpSession session = hse.getSession();
		noOfActiveUsers++;
		session.getServletContext().setAttribute("noOfActiveUsers", noOfActiveUsers);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent hse) {
		HttpSession session = hse.getSession();
		noOfActiveUsers--;
		session.getServletContext().setAttribute("noOfActiveUsers", noOfActiveUsers);
	}

}
