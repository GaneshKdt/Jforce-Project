package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.PCPBookingDAO;
import com.nmims.helpers.PCPPDFCreator;

@Controller
public class PCPAdminController extends BaseController
{
	
	@Autowired
	PCPBookingDAO pcpBookingDAO;
	
	@Value( "${MARKSHEETS_PATH}" )
	private String MARKSHEETS_PATH;
	

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;
	
	@RequestMapping(value = "/admin/pcpRegistrationReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String pcpRegistrationReportForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		PCPBookingTransactionBean bean = new PCPBookingTransactionBean();
		m.addAttribute("bean",bean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "pcp/pcpBookingReport";
	}
	
	@RequestMapping(value = "/admin/pcpRegistrationReport", method = RequestMethod.POST)
	public ModelAndView pcpRegistrationReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PCPBookingTransactionBean bean){
		ModelAndView modelnView = new ModelAndView("pcp/pcpBookingReport");
		request.getSession().setAttribute("bean", bean);
		try{
			ArrayList<PCPBookingTransactionBean> pcpBookingList = pcpBookingDAO.getConfirmedBookingForGivenYearMonth(bean, getAuthorizedCodes(request));
			request.getSession().setAttribute("pcpBookingList",pcpBookingList);

			if(pcpBookingList != null && pcpBookingList.size() > 0){
				modelnView.addObject("rowCount",pcpBookingList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("bean", bean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadPCPBookingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadPCPBookingReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
			//redirectToPortalApp(response);
		
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<PCPBookingTransactionBean> pcpBookingList = (ArrayList<PCPBookingTransactionBean>)request.getSession().getAttribute("pcpBookingList");
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)request.getSession().getAttribute("userAuthorization");
       	String roles="";
		if(userAuthorization != null){
       	roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
       	}
		request.setAttribute("roles", roles);
		return new ModelAndView("pcpBookingReportExcelView","pcpBookingList",pcpBookingList);
	}
	
	
	@RequestMapping(value="/admin/massPCPBookingDownloadForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView massPCPBookingDownloadForm(HttpServletRequest request, HttpServletResponse respnse){
		ModelAndView modelAndView = new ModelAndView("massPCPBooking");
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("pcpBookingBean", new PCPBookingTransactionBean());
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/massPCPBookingDownload",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView massPCPBookingDownload(@ModelAttribute PCPBookingTransactionBean pcpBookingBean,HttpServletRequest request, HttpServletResponse respnse){
		
		PCPBookingDAO pcpDao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		PCPPDFCreator pcpPDFHelper = new PCPPDFCreator();
		
		ModelAndView modelAndView = new ModelAndView("massPCPBooking");
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("pcpBookingBean", new PCPBookingTransactionBean());
		
		boolean alreadyDownloadedForSelectedYearAndMonth = false;
		alreadyDownloadedForSelectedYearAndMonth = pcpDao.alreadyDownloadedDocument(pcpBookingBean, "PCP Fee Receipt");
		
		if(alreadyDownloadedForSelectedYearAndMonth)
		{
			setError(request,"PCP Fee Receipt Already Generated For Selected Year-Month "+pcpBookingBean.getYear()+"-"+pcpBookingBean.getMonth());
			return modelAndView;
		}
		
		try{
		ArrayList<String> listOfDistincSapidBookingsFromMonthAndYear = pcpDao.listOfDistincSapidBookingsFromMonthAndYear(pcpBookingBean);
		HashMap<String,ArrayList<PCPBookingTransactionBean>> mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag = pcpDao.mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag(pcpBookingBean);
		ArrayList<PCPBookingTransactionBean> pcpBookingListForBatchUpdate = new ArrayList<PCPBookingTransactionBean>();
		
		for(String sapid : listOfDistincSapidBookingsFromMonthAndYear){
			
			StudentAcadsBean student = new StudentAcadsBean();
			PCPBookingTransactionBean pcpTransactionBean = new PCPBookingTransactionBean();
			ArrayList<PCPBookingTransactionBean> pcpBookingList = mapOfSapidAndPCPBookingListFromMonthAndYearWithoutAcadsFlag.get(sapid);
			
			student.setSapid(sapid);
			student.setProgram(pcpBookingList.get(0).getProgram());
			student.setFirstName(pcpBookingList.get(0).getFirstName());
			student.setLastName(pcpBookingList.get(0).getLastName());
			
				String fileName = pcpPDFHelper.createReceiptAndReturnFileName(pcpBookingList, MARKSHEETS_PATH, student);	
				pcpTransactionBean.setSapid(sapid);
				pcpTransactionBean.setFilePath(fileName);
				pcpTransactionBean.setMonth(pcpBookingBean.getMonth());
				pcpTransactionBean.setYear(pcpBookingBean.getYear());
			
			pcpBookingListForBatchUpdate.add(pcpTransactionBean);
			
		}
		pcpDao.batchInsertOfDocumentRecords(pcpBookingListForBatchUpdate, "PCP Fee Receipt");
		setSuccess(request,"Successfully generated PCP Receipts");
		}catch(Exception e){
			setError(request,"Error in generating PCP Receipts");
			  
		}	
		
		return modelAndView;
	}

}
