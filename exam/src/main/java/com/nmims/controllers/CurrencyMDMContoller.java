package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.CurrencyMappingBean;
import com.nmims.services.CurrencyMDMServiceInterface;

 
@Controller
public class CurrencyMDMContoller  extends BaseController {

	@Autowired
	CurrencyMDMServiceInterface currencyInterface;
	
	HashMap<Integer,CurrencyMappingBean> masterkeyProgramMapping=null;
	
	HashMap<Integer,String> feeTypeList= null;
	
	Map<Integer,String> currencyMap=null;
	
	public HashMap<Integer,CurrencyMappingBean> masterkeyMapping(){
		if(this.masterkeyProgramMapping==null || this.masterkeyProgramMapping.size() == 0) {
			this.masterkeyProgramMapping=currencyInterface.getMapProgramsById();
		}
		return masterkeyProgramMapping;
	}
	
	public HashMap<Integer,String> feeTypeList(){
		if(this.feeTypeList==null || this.feeTypeList.size() == 0) {
			this.feeTypeList=currencyInterface.getFeeType();
		}
		return feeTypeList;
	}
	
	public Map<Integer,String> currencyMap(){
		if(this.currencyMap==null || this.currencyMap.size() == 0) {
			this.currencyMap=currencyInterface.getCurrency();
		}
		return currencyMap;
	}
	
	
	
	@RequestMapping(value="/admin/currencyDetailsForm",method = {RequestMethod.GET})
	public ModelAndView currencyDetailsForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("mdm/currencyForm");
		mav.addObject("currency",new CurrencyMappingBean());
			try{
				ArrayList<ConsumerProgramStructureExam> consumerType = currencyInterface.getConsumerTypeList();
				ArrayList<CurrencyMappingBean> currencyDetailsList=currencyInterface.getAllCurrencyValue(masterkeyMapping(),feeTypeList(),currencyMap());
			
				
				int rowCount=0;
				rowCount=currencyDetailsList.size();
				request.getSession().setAttribute("currencyDetailsList", currencyDetailsList);
				mav.addObject("currencyDetailsList",currencyDetailsList);
				mav.addObject("consumerType",consumerType);
				mav.addObject("currencyList",currencyMap());
				mav.addObject("feeTypeList",feeTypeList());
				if(rowCount>=0) {
					mav.addObject("rowCount",rowCount);
				}
			}catch(Exception e){
				e.printStackTrace();
				return mav;
			}
		return mav;
	}
	
	@RequestMapping(value="/admin/saveCurrencyDetails",method = {RequestMethod.POST})
	public ModelAndView programDetails(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CurrencyMappingBean currency) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		try {
			String program=currency.getProgram();
			String programStructureId= currency.getProgramStructure();
			ArrayList<String> programId= new ArrayList<>(Arrays.asList(program.split(",")));
			ArrayList<String> programStructureID=new ArrayList<>(Arrays.asList(programStructureId.split(",")));
	
	
			ArrayList<CurrencyMappingBean> currencyList=currencyInterface.getMasterKey(currency.getConsumerType(),programId,programStructureID,currency);
		
			int size=currencyList.size();
			ArrayList<CurrencyMappingBean> dublicateList=currencyInterface.saveCurrencyDetails(currencyList);
		
			String notSaved="";
			for(CurrencyMappingBean bean:dublicateList) {
				CurrencyMappingBean currencyBean=masterkeyProgramMapping.get(bean.getConsumerProgramStructureId());
				notSaved=notSaved+currencyBean.getConsumerType()+" "+""+"-"+""+" "+currencyBean.getProgram()+" "+""+"-"+""+" "+currencyBean.getProgramStructure()+" <br>";
			}
		
			int finalDetailsSaveSize=size-dublicateList.size();
			String feeType=feeTypeList.get(currency.getFeeId());
			
			if(!notSaved.isEmpty() && finalDetailsSaveSize==0) {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Error While Adding Mappings For "+" <b>"+feeType+"</b> "+"Entry Already Exists For Below Records"+"<br>"+notSaved);	
			}else if(!notSaved.isEmpty() && finalDetailsSaveSize>=0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage", finalDetailsSaveSize+" "+"Details Save Successfully");
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Error While Adding Mappings For "+" <b>"+feeType+"</b> "+"Entry Already Exists For Below Records"+"<br>"+notSaved);	
			}else {
					request.setAttribute("success","true");
					request.setAttribute("successMessage", finalDetailsSaveSize+" "+"Details Save Successfully");
				}
			} catch (Exception e) {
				return currencyDetailsForm(request, response);
		}
		return currencyDetailsForm(request, response);
	}
	
	
	@RequestMapping(value = "/admin/updateCurrencyDetails",  method = {RequestMethod.POST}, consumes="application/json", produces="application/json")
	public ResponseEntity<HashMap<String, String>> updateCurrencyDetails(@RequestBody CurrencyMappingBean currencyMappingBean,HttpServletRequest request){
		request.removeAttribute("success");
		request.removeAttribute("successMessage");
		HashMap<String, String> response = new  HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			HashMap<String, String> message = currencyInterface.updateCurrencyDetails(currencyMappingBean);
			if (message.containsKey("error")) {
				response.put("Status", "Fail");
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
			}
			response.put("Status", "Success");

		} catch (Exception e) {
			response.put("Status", "Fail");
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
		}
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);	
		
	}
	
	
	@RequestMapping(value = "/admin/downloadCurrencyReport", method = { RequestMethod.GET })
	public ModelAndView downloadCurrencyReport(HttpServletRequest request, HttpServletResponse response) {
	
		ArrayList<CurrencyMappingBean> currencyDetailsList=new ArrayList<CurrencyMappingBean>();
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		try {
			currencyDetailsList =  (ArrayList<CurrencyMappingBean>) request.getSession().getAttribute("currencyDetailsList");
			return new ModelAndView("currencyProgramExcelView", "currencyDetailsList", currencyDetailsList);
		}catch(Exception e) {	
			return new ModelAndView("currencyProgramExcelView", "currencyDetailsList", currencyDetailsList);
		}		
	}


//	@RequestMapping(value="/admin/addUniqueCurrency",method = {RequestMethod.GET})
//	public ModelAndView addUniqueCurrency(HttpServletRequest request,HttpServletResponse response) {
//		if(!checkSession(request, response)){
//			redirectToPortalApp(response);
//			return null;
//		}
//		ModelAndView mav=new ModelAndView("mdm/addUniqueCurrencyValue");
//		mav.addObject("currency",new CurrencyMappingBean());
//			try{
//				
//				Map<Integer,String> currencyMap=currencyInterface.getCurrency();
//				int rowCount=0;
//				rowCount=currencyMap.size();
////				
////				request.getSession().setAttribute("currencyDetailsList", currencyDetailsList);
////				//mav.addObject("programsList",programsList);
//				mav.addObject("unqiueCurrencyValue",currencyMap);
////				mav.addObject("consumerType",consumerType);
////				mav.addObject("currencyList",currencyMap);
////				mav.addObject("feeTypeList",feeTypeList);
//				if(rowCount>=0) {
//					mav.addObject("rowCount",rowCount);
//				}
//			}catch(Exception e){
//				
//				return mav;
//			}
//		return mav;
//	}
//////	
//	@RequestMapping(value="/admin/saveUniqueCurrencyValue",method = {RequestMethod.POST})
//	public ModelAndView saveUniqueCurrencyValue(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CurrencyMappingBean currency) {
//		if(!checkSession(request, response)){
//			redirectToPortalApp(response);
//			return null;
//		}
//		try {
//	
//			String currencyName=currency.getCurrencyName();
//			System.out.println("currency"+" "+currencyName);
//			if(currencyName!=null) {
//			int count = currencyInterface.saveUniqueCurrencyValue(currencyName);
//			}
//		
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			return addUniqueCurrency(request, response);
//		}
//		return addUniqueCurrency(request, response);
//	}
//	

}
