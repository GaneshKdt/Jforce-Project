package com.nmims.controllers;



import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.AssignmentService;
import com.nmims.services.EmbaPassFailService;
import com.nmims.services.MarksheetMBAX;
import com.nmims.views.PassFailResultsExcelView;

/**
 * Handles requests for the application home page.
 */
@RestController 

public class EmbaPassFailController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 
	
	@Autowired
	EmbaPassFailService epfService;

	@Autowired
	MarksheetMBAX mxs;

	protected List<String> SAS_EXAM_MONTH_LIST = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
	
	private final int pageSize = 20;

	private static final Logger logger = LoggerFactory.getLogger(EmbaPassFailController.class);
	private ArrayList<String> programList = null;

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019")); 
	
	private ArrayList<String> validityYearList = new ArrayList<String>(Arrays.asList( 
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020")); 
	 
	private ArrayList<String> graceYearList = new ArrayList<String>(Arrays.asList( 
			"2013","2014","2015","2016","2017","2018","2019")); 

	private ArrayList<String> subjectList = null; 
	private HashMap<String, String> programCodeNameMap = null;
	private ArrayList<CenterExamBean> centers = null; 
	private HashMap<String, CenterExamBean> centersMap = null; 
	private HashMap<String, String> centerCodeNameMap = null; 
	private ArrayList<String> progStructListFromProgramMaster = null;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	
//	to be deleted, api shifted to rest controller
////	api to get cleared sems of a student
//	@RequestMapping(value = "/m/getClearedSemForStudent", method = RequestMethod.POST, produces="application/json")
//	public ResponseEntity<ArrayList<EmbaPassFailBean>> getClearedSemForStudent(@RequestBody EmbaPassFailBean bean
//			) throws Exception {
//		String sapid = bean.getSapid();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ArrayList<EmbaPassFailBean> response =  epfService.getClearedSemForStudent(sapid);
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//
//	}
	
	
//	@RequestMapping(value = "/m/getClearedSemForStudentMBAX", method = RequestMethod.POST, produces="application/json")
//	public ResponseEntity<ArrayList<EmbaPassFailBean>> getClearedSemForStudentMBAX(@RequestBody EmbaPassFailBean bean
//			) throws Exception {
//		String sapid = bean.getSapid();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ArrayList<EmbaPassFailBean> response =  mxs.getClearedSemForStudent(sapid);
//		return new ResponseEntity<>(response,headers, HttpStatus.OK);
//
//	}

}

