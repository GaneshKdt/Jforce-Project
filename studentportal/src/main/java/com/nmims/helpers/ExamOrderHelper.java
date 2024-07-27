package com.nmims.helpers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import com.nmims.daos.PortalDao;

public class ExamOrderHelper {
	
	@Autowired
	public  PortalDao portalDAO;
	
	public double  getExamOrderFromAcadMonthAndYear(String acadMonth,String acadYear)
	{
		
		double examOrder = portalDAO.getExamOrderFromAcadMonthAndYear(acadMonth, acadYear);
		return examOrder;
	}
	public double  getExamOrderFromExamMonthAndYear(String examMonth,String examYear)
	{
		double examOrder = portalDAO.getExamOrderFromExamMonthAndYear(examMonth, examYear);
		return examOrder;
	}
}
