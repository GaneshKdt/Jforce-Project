package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import com.nmims.helpers.AmazonS3Helper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.nmims.beans.DelhiveryMergeBean;
import com.nmims.beans.DispatchedDocumentMergeResponseBean;
import com.nmims.beans.FedExMergerBean;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.html2pdf.HtmlConverter;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.DelhiveryManager;
import com.nmims.interfaces.CourierService;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.partner.Connector;


@Controller
public class DelhiveryController {
	
	@Value("${DelhiveryDocuments}")
	private String DelhiveryDocuments;
	
	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
	@Autowired
	@Qualifier("delhiveryservice")
	CourierService courierService;
	
	@Autowired
	DelhiveryManager deliveryObject;
	
	@Autowired
	AmazonS3Helper awsHelper;
	
	private static final Logger logger = LoggerFactory.getLogger(DelhiveryController.class);
	
	//checks whether service is provided by delhivery for specified pincode
	@RequestMapping(value="/checkPincodeFromDelhivery",method={RequestMethod.GET})
	public ResponseEntity<String> checkPincode() 
	{
		try
		{
			String pincode="400706";
			String deliveryType="S";
			ResponseEntity<String> res=courierService.checkPincode(pincode,deliveryType);
			//System.out.println(res.getBody());
			return res;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//gets unique trackingId 
	@RequestMapping(value="/getTrackingIdFromDelhivery",method={RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> getTrackingId(HttpServletRequest request) throws IOException
	{
		try
		{
			return courierService.getTrackingId(request);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//gets unique trackingId's in bulk
	@RequestMapping(value="/bulkTrackingIdFromDelhivery",method= {RequestMethod.GET})
	public ResponseEntity<String> getBulkTrackingId(HttpServletRequest request) throws IOException
	{
		try
		{
			return courierService.getBulkTrackingId(request);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//tracks order in delhivery system
	@RequestMapping(value="/trackOrderFromDelhivery",method= {RequestMethod.GET})
	public ResponseEntity<String> trackOrder() throws IOException
	{
		try
		{
			String pincode="400706";
			String deliveryType="S";
			return courierService.trackOrder(pincode,deliveryType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//edit order in delhivery system
	@RequestMapping(value="/editOrderFromDelhivery",method= {RequestMethod.POST})
	public ResponseEntity<String> editOrder() throws IOException
	{
		try
		{
			return courierService.editOrder();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//cancel order from delhivery system
	@RequestMapping(value="/cancelOrderFromDelhivery",method= {RequestMethod.POST})
	public ResponseEntity<String> cancelOrder() throws IOException
	{
		try
		{
			return courierService.cancelOrder();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//pickup creation for the order from warehouse
	@RequestMapping(value="/pickupCreationFromDelhivery",method= {RequestMethod.POST})
	public ResponseEntity<String> pickupRequestCreation() throws IOException
	{
		try
		{
			return courierService.pickupRequestCreation();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//order creation in delhivery system
	@RequestMapping(value="/orderCreationFromDelhivery",method= {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String orderCreation(HttpServletRequest request) 
	{
		try
		{
			int retryCount=Integer.parseInt(SFDC_API_MAX_RETRY_COUNT);
			String dispatchOrderId=request.getParameter("id");
			//System.out.println(dispatchOrderId);
			return courierService.orderCreationFromDelhivery(dispatchOrderId,retryCount);
		}
		catch(Exception e)
		{
			logger.info("Exception in orderCreation in DelhiveryController:"+e);
			e.printStackTrace();
			return ExceptionUtils.getStackTrace(e);
		}
	}
	
	//DocumentsMerge for Delhivery
	@RequestMapping(value="/delhiveryDocumentsMerge",method= {RequestMethod.GET,RequestMethod.POST}, consumes = "application/json",produces = "application/json")
	public ResponseEntity<HashMap<String, String>> delhiveryDocumentsMerge(@RequestBody DelhiveryMergeBean bean, HttpServletRequest request, HttpServletResponse response)
	{
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        HashMap<String, String> mergeList = new  HashMap<String, String>();
		try
		{
			int retryCount=Integer.parseInt(SFDC_API_MAX_RETRY_COUNT);
//			String commaSepratedIds=request.getParameter("ids");
//			String arr[]=commaSepratedIds.split(",");
			String orderIds="";
			List<String> ids=bean.getIds();
			for(int i=0;i<=ids.size()-1;i++)
			{
				if(i==ids.size()-1)
					orderIds=orderIds+"'"+ids.get(i)+"'";
				else
					orderIds=orderIds+"'"+ids.get(i)+"',";
			}
			
			String url=courierService.documentsMergeForDelhivery(response,orderIds,retryCount);
			mergeList.put("url", url);
	        return new ResponseEntity<>(mergeList, headers,  HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.info("Exception in delhiveryDocumentsMerge in DelhiveryController:"+e);
			e.printStackTrace();
		    mergeList.put("url", "Exception in delhiveryDocumentsMerge in DelhiveryController:"+e);
		    return new ResponseEntity<>(mergeList, headers,  HttpStatus.BAD_REQUEST);
		}
	}
	
	//packing slip creation in delhivery system
	@RequestMapping(value="/slipCreationFromDelhivery",method= {RequestMethod.GET,RequestMethod.POST})
	public ResponseEntity<String> slipCreation(String trackingId) throws IOException
	{
		try
		{
			return courierService.slipCreation(trackingId,"S");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//shipping charges calculation for delivery
	@RequestMapping(value="/shippingChargeFromDelhivery",method= {RequestMethod.GET,RequestMethod.POST})
	public ResponseEntity<String> shipCharge() throws IOException
	{
		String mode="S";
		String weight="10";
		String origin_pin="400706";
		String status="RTO";
		String destination_pin="400004";
		try
		{
			 return courierService.shippingChargesCalculation(mode, weight, origin_pin, destination_pin, status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity("error",HttpStatus.BAD_REQUEST);
			//return new ResponseEntity<>("Invalid Api key",HttpStatus.UNAUTHORIZED);
		}
	}
	
	//pdfCreationForCorruptFiles for delhivery
	@RequestMapping(value="/pdfCreationDelhivery", method= {RequestMethod.GET,RequestMethod.POST},consumes = "application/json")
	@ResponseBody
	public String pdfCreation(@RequestBody DelhiveryMergeBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		try
		{

			String orderIds="";
			List<String> ids=bean.getIds();
			for(int i=0;i<=ids.size()-1;i++)
			{
				if(i==ids.size()-1)
					orderIds=orderIds+"'"+ids.get(i)+"'";
				else
					orderIds=orderIds+"'"+ids.get(i)+"',";
			}
			
			return courierService.pdfCreationForDelhivery(orderIds);
		}
		catch(Exception e)
		{
			logger.info("exception in pdfCreation controller"+e);
			return "exception in pdfCreation controller"+e;
		}
	}
	
	@RequestMapping(value="/checkShippingCharge", method= {RequestMethod.GET,RequestMethod.POST})
	public ResponseEntity<String> checkShippingCharge(HttpServletRequest req)
	{
		try
		{
			String mode=req.getParameter("mode");
			String destination_pin=req.getParameter("destinationPin");
			String status=req.getParameter("status");
			String origin_pin=req.getParameter("originPin");
			ResponseEntity<String> res= deliveryObject.shippingChargesCalculation(mode, destination_pin, status,origin_pin);
			String body=res.getBody();
			System.out.println(body);
			return res;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/dispatchOrderDocumnetMerge", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
	public ResponseEntity<DispatchedDocumentMergeResponseBean> dispatchOrderDocumnetMerge(@RequestBody List<FedExMergerBean> bean) throws Exception {
		//System.out.println("Invoked ---- ");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		DispatchedDocumentMergeResponseBean mergedBean = new  DispatchedDocumentMergeResponseBean();
		try {
			mergedBean = courierService.dispatchOrderDocumnetMerge(bean);
		}catch (Exception e) {
			logger.info("Exception in dispatchOrderDocument merge is :"+e);
			e.printStackTrace();
			//System.out.println(e);
		}
		return  new ResponseEntity<>(mergedBean, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dispatchOrderDocumentMergeForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView dispatchOrderDocumentMergeForm(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("dispatchOrderDocumentMerge");
	}
	
	@RequestMapping(value = "/downloadDispatchOrderDocumentMerge", method = {RequestMethod.POST})
	public ModelAndView downloadDispatchOrderDocumentMerge(HttpServletRequest request, @RequestParam(value="file") MultipartFile file) {
		ModelAndView modelAndView = new ModelAndView("dispatchOrderDocumentMerge");
		try {
			DispatchedDocumentMergeResponseBean mergerResponse = courierService.createFileFromExcelRecords(file);
			if(StringUtils.isNotBlank(mergerResponse.getSuccessMessage())) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", mergerResponse.getSuccessMessage());
				modelAndView.addObject("mergedUrl", mergerResponse.getUrl());
			}
			if(StringUtils.isNotBlank(mergerResponse.getErrorMessage())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", mergerResponse.getErrorMessage());	
			}
		} catch (Exception e) {
			logger.info("Error in inserting rows for dispatchOrderDocumentMerge from Excel file due to : {}", e);
		}
		return modelAndView;
	}
	
}
