package com.nmims.helpers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ShipmentRequest;
import com.nmims.listeners.DelhiveryScheduler;

@Component
public class DelhiveryManager {

	@Value("${Authorization-key-test}")
	private String authorizationKeyTest;
	
	@Value("${Authorization-key-prod-surface}")
	private String authorizationKeyProdForSurafce;
	
	@Value("${Authorization-key-prod-express}")
	private String authorizationKeyProdForExpress;
	
	@Value("${DelhiveryBaseUrl}")
	private String DelhiveryBaseUrl;
	
	private static final Logger logger = LoggerFactory.getLogger(DelhiveryManager.class);

	
	public HttpHeaders createTestHeader()
	{
		HttpHeaders header=new HttpHeaders();
		header.add("Authorization", authorizationKeyTest);
		header.add("Accept", "application/json");
		header.add("Content-Type", "application/json");
		return header;
	}
	
	public HttpHeaders createProdHeaderForSurface()
	{
		HttpHeaders headers=new HttpHeaders();
		headers.add("Authorization", authorizationKeyProdForSurafce);
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	public HttpHeaders createProdHeaderForExpress()
	{
		HttpHeaders headers=new HttpHeaders();
		headers.add("Authorization", authorizationKeyProdForExpress);
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
	}
	
	public ResponseEntity<String> checkPinCode(String pincode,String shippingMode)
	{
		
		//System.out.println(pincode);
		String url=DelhiveryBaseUrl+"/c/api/pin-codes/json/?filter_codes="+pincode;
		JsonObject jsonObj=new JsonObject();
		try
		{
			HttpHeaders headers=new HttpHeaders();
			if(shippingMode.equalsIgnoreCase("S"))
				headers=createProdHeaderForSurface();
			else if(shippingMode.equalsIgnoreCase("E"))
				headers=createProdHeaderForExpress();
			RestTemplate restTemplate=new RestTemplate();
			HttpEntity<String> requestEntity=new HttpEntity<String>(headers);
			long t1=System.currentTimeMillis();
			ResponseEntity<String>response= restTemplate.exchange(url, HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from check pincode api Delhivery in milli seconds:"+t3);
			jsonObj= new JsonParser().parse(response.getBody()).getAsJsonObject();
			logger.info("Response from check pincode delhivery: "+response.getBody());
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				JsonArray jsonarr= jsonObj.get("delivery_codes").getAsJsonArray();
				if(jsonarr.size()>0)
				{
					String pickup=jsonarr.get(0).getAsJsonObject().get("postal_code").getAsJsonObject().get("pre_paid").getAsString();
					if(pickup.equalsIgnoreCase("Y"))
					{
						//System.out.println("Yes pickup");
						logger.info("Service available for receiver pincode : "+pincode);
						return new ResponseEntity<>("Y",HttpStatus.OK);
						
					}
					else
					{
						//System.out.println("nopickup");
						return new ResponseEntity<>("No service available for the specified pincode.",HttpStatus.BAD_REQUEST);
						
					}
					}
				else if(jsonarr.size()==0)
					return new ResponseEntity<>("No service available for the specified pincode.",HttpStatus.BAD_REQUEST);
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			logger.info("HttpClientErrorException from check pincode :"+ex);
			logger.info("HttpClientErrorException from check pincode response body :"+s);
			logger.info("HttpClientErrorException from check pincode response status :"+status);
			return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception from check pincode :"+e);
			return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
	}
	
	
	public ResponseEntity<String> getTrackingId(HttpServletRequest request) 
	{
		String clientName=request.getParameter("cl");
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/waybill/api/fetch/json/?cl="+clientName;
		try
		{
//			HttpHeaders headers=new HttpHeaders();
//			headers.add("Authorization", authorizationKey);
//			headers.add("Accept", "application/json");
//			headers.add("Content-Type", "application/json");
			HttpEntity<String> requestEntity=new HttpEntity<String>(createTestHeader());
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from check pincode api Delhivery in milli seconds:"+t3);
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(response.getBody());
				String body=response.getBody();
				String trackid=body.substring(1, body.length()-1);
				return new ResponseEntity<>(trackid,rh,HttpStatus.OK);
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			return new ResponseEntity<>("Error",rh,status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Incorrect API key",HttpStatus.BAD_REQUEST);
		}
//		finally
//		{
//			httpClient.close();
//		}
		return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
	}
	
	
	public ResponseEntity<String> getBulkTrackingId(HttpServletRequest request) throws IOException
	{
		String clientName=request.getParameter("cl");
		String count=request.getParameter("count");
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/waybill/api/bulk/json/?cl="+clientName+"&count="+count;
		try
		{
//			HttpHeaders headers=new HttpHeaders();
//			headers.add("Authorization", authorizationKey);
//			headers.add("Accept", "application/json");
//			headers.add("Content-Type", "application/json");
			HttpEntity<String> requestEntity=new HttpEntity<String>(createTestHeader());
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from check pincode api Delhivery in milli seconds:"+t3);
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(response.getBody());
				return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			return new ResponseEntity<>("Error",rh,status);
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",rh,HttpStatus.BAD_REQUEST);
		}
		finally
		{
			httpClient.close();
		}
		return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
	}
	
	
	public ResponseEntity<String> trackOrder(String trackingId,String shippingMode) 
	{
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		String url=DelhiveryBaseUrl+"/api/v1/packages/json/?waybill="+trackingId;
		//String testUrl=baseUrlTest+"/api/v1/packages/json/?waybill="+trackingId;
		try
		{
			HttpHeaders headers=new HttpHeaders();
			if(shippingMode.equalsIgnoreCase("Surface"))
				headers=createProdHeaderForSurface();
			else if(shippingMode.equalsIgnoreCase("Express"))
				headers=createProdHeaderForExpress();
			HttpEntity<String> requestEntity=new HttpEntity<String>(headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from track order api Delhivery in milli seconds:"+t3);
			logger.info("Response Body from track order :"+response.getBody());
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(response.getBody());
				if(!(response.getBody().contains("Error")))
				{
					logger.info("Success response from delhivery track api for Tracking Number : "+trackingId);
					return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
				}
				else
				{
					String error=jsonObj.get("Error").getAsString();
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			logger.info("HttpClientErrorException from track order :"+ex);
			logger.info("HttpClientErrorException from track order response body :"+s);
			logger.info("HttpClientErrorException from track order response status :"+status);
			return new ResponseEntity<>(ex.getMessage(),rh,HttpStatus.BAD_REQUEST);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception from track Order :"+e);
			return new ResponseEntity<>(e.getMessage(),rh,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
	}
	
//	{
//		  "tax_value": "tax value",
//		  "shipment_width": 10,
//		  "product_category": "category",
//		  "waybill": "waybill no.",
//		  "consignee_tin": "tin",
//		  "phone": "phone no.",
//		  "stax_ack_number": "stax_ack_number",
//		  "gm": 10,
//		  "shipment_length": 10,
//		  "shipment_height": 10,
//		  "commodity_value": "commodity_value",
//		  "name": "jai",
//		  "product_details": "product details",
//		  "add": "abc road,house no 2",
//		  "pt":"COD",	
//		}
	
	
	public ResponseEntity<String> editOrder() throws IOException
	{
		String waybill="5420410001234";
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/api/p/edit";
		try
		{
			
			JsonObject request = new JsonObject();
			request.addProperty("waybill",waybill);
//			request.addProperty("tax_value": "tax value");
//			request.addProperty( "shipment_width": 10);
//			request.addProperty("product_category": "category");
//			request.addProperty("consignee_tin": "tin");
//			request.addProperty( "phone": "phone no.");
//			request.addProperty("stax_ack_number": "stax_ack_number");
			request.addProperty("gm",10);
//			request.addProperty("shipment_length": 10);
//			request.addProperty("shipment_height": 10);
//			request.addProperty("commodity_value": "commodity_value");
//			request.addProperty("name": "jai");
//			request.addProperty("product_details": "product details");
//			request.addProperty("add": "abc road,house no 2");
//			HttpHeaders headers=new HttpHeaders();
//			headers.add("Authorization", authorizationKey);
//			headers.add("Accept", "application/json");
//			headers.add("Content-Type", "application/json");
			HttpEntity<String> requestEntity=new HttpEntity<String>(request.toString(),createTestHeader());
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from edit order api Delhivery in milli seconds:"+t3);
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println(response.getBody());
				String status=jsonObj.get("status").getAsString();
				if(response.getBody().contains("error") && status.equalsIgnoreCase("Failure"))
				{
					String error=jsonObj.get("error").getAsString();
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
				else if(status.equalsIgnoreCase("false"))
				{
					String error="error";
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
				else
				{
					return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
				}
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			return new ResponseEntity<>("Error",rh,status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api",rh,HttpStatus.BAD_REQUEST);
		}
		finally
		{
			httpClient.close();
		}
		return new ResponseEntity<>("error",rh,HttpStatus.BAD_REQUEST);
	}
	
//	{
//	    "waybill": "waybill no.",
//	    "cancellation": "true"
//	}
	

	public ResponseEntity<String> cancelOrder() throws IOException
	{
		String waybill="542041000077";
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/api/p/edit";
		try
		{
//			HttpHeaders headers=new HttpHeaders();
//			headers.add("Authorization", authorizationKey);
//			headers.add("Accept", "application/json");
//			headers.add("Content-Type", "application/json");
			JsonObject body = new JsonObject();
			body.addProperty("waybill", waybill);
			body.addProperty("cancellation", "true");
			HttpEntity<String> requestEntity=new HttpEntity<String>(body.toString(),createTestHeader());
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from check cancel order api Delhivery in milli seconds:"+t3);
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(jsonObj);
				String status=jsonObj.get("status").getAsString();
				if(response.getBody().contains("error") && status.equalsIgnoreCase(("Failure")))
				{
					String error=jsonObj.get("error").getAsString();
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
				else if(status.equalsIgnoreCase("false"))
				{
					String error="error";
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
				else
				{
					return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
				}
			}	
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			return new ResponseEntity<>("Error",rh,status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",rh,HttpStatus.BAD_REQUEST);
		}
		finally
		{
			httpClient.close();
		}
		return new ResponseEntity<>("error",rh,HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<String> slipCreation(String trackingId,String shippingMode) throws IOException
	{
		String url=DelhiveryBaseUrl+"/api/p/packing_slip?wbns="+trackingId;
		//String testUrl=baseUrlTest+"/api/p/packing_slip?wbns="+trackingId;
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		try
		{
			HttpHeaders headers=new HttpHeaders();
			if(shippingMode.equalsIgnoreCase("S"))
				headers=createProdHeaderForSurface();
			else if(shippingMode.equalsIgnoreCase("E"))
				headers=createProdHeaderForExpress();
			HttpEntity<String> requestEntity=new HttpEntity<String>(headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from slip creation api Delhivery in milli seconds:"+t3);
			logger.info("Response body from slip creation :"+response.getBody());
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(response.getBody());
				int size=jsonObj.get("packages").getAsJsonArray().size();
				if(size>0)
				{
					logger.info("Successfull reponse from delhivery slip creation api for Tracking Id: "+trackingId);
					return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK); 
				}
				else
				{
					return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
				}
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			logger.info("HttpClientErrorException from slip creation :"+ex);
			logger.info("HttpClientErrorException from slip creation response body :"+s);
			logger.info("HttpClientErrorException from slip creation response status :"+status);
			return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception from slip creation :"+e);
			return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
	}
	
//	{
//	    "pickup_time": "18:30:00",
//	    "pickup_date": "2017-08-29",
//	    "pickup_location": "xxxxxxxxxxxxxxxxx",
//	    "expected_package_count": 1
//	}
	
	public ResponseEntity<String> pickupRequestCreation() throws IOException
	{
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/fm/request/new/";
		ResponseEntity<String>response=null;
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		try
		{
//			HttpHeaders headers=new HttpHeaders();
//			headers.add("Authorization", authorizationKey);
//			headers.add("Accept", "application/json");
//			headers.add("Content-Type", "application/json");
			JsonObject body = new JsonObject();
			body.addProperty("pickup_time", "20:30:00");
			body.addProperty("pickup_date", "2021-12-29");
			body.addProperty("pickup_location","NMIMS SURFACE");
			body.addProperty("expected_package_count",2);
			HttpEntity<String> requestEntity=new HttpEntity<String>(body.toString(),createTestHeader());
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from pickup creation api Delhivery in milli seconds:"+t3);
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("201".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(jsonObj);
				if(response.getBody().contains("error"))
				{
					String error=jsonObj.get("error").getAsJsonObject().get("message").getAsString();
					return new ResponseEntity<>(error,rh,HttpStatus.BAD_REQUEST);
				}
				return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			return new ResponseEntity<>("Error",rh,status);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("Invalid Api key",rh,HttpStatus.BAD_REQUEST);
		}
		finally
		{
			httpClient.close();
		}
		return new ResponseEntity<>("error",rh,HttpStatus.BAD_REQUEST);
	}
	
//	{
//		  "phone": "phone no.",
//		  "city": "Kota",
//		  "name": "warehouse name",
//		  "pin": "324005",
//		  "address": "address",
//		  "country": "India",
//		  "email": "abc@gmail.com",
//		  "registered_name": "registered username",
//		   "return_address": "return_address",
//		   "return_pin":"return_pin",
//		   "return_city":"return_city",
//		   "return_state":"return_state",
//		   "return_country": "return_country"
//		}
	public void clientWarehouseCreation() throws IOException
	{
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/api/backend/clientwarehouse/create/";
		try
		{
			HttpHeaders headers=new HttpHeaders();
			headers.add("Authorization", "Token XXXXXXXXXXXXXXXXXX");
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
			JsonObject body = new JsonObject();
			body.addProperty("phone","phone no.");
			body.addProperty(  "city","Kota");
			body.addProperty("name","warehouse name");
			body.addProperty(  "pin",324005);
			body.addProperty(  "address", "address" );
			body.addProperty( "country", "India");
			body.addProperty( "email", "abc@gmail.com");
			body.addProperty( "registered_name", "registered username");
			body.addProperty( "return_address", "return_address");
			body.addProperty("return_pin","return_pin" );
			body.addProperty(  "return_city","return_city");
			body.addProperty( "return_state","return_state");
			body.addProperty("return_country", "return_country");
			HttpEntity<String> requestEntity=new HttpEntity<String>(body.toString(),headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from client warehouse creation api Delhivery in milli seconds:"+t3);
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("201".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(jsonObj);
			}
			else if("401".equalsIgnoreCase(response.getStatusCode().toString()) || "400".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("Api key required");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			httpClient.close();
		}
	}
	
//	{
//	      "name":"clwh-name",
//	      "registered_name":"name",
//	      "address":"123, sector 40 Gurgaon",
//	      "pin":"456010",
//	      "phone":"8888888888"
//	}
	
	public void editClientWarehouseDetails() throws IOException
	{
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/api/backend/clientwarehouse/create/";
		try
		{
			HttpHeaders headers=new HttpHeaders();
			headers.add("Authorization", "Token XXXXXXXXXXXXXXXXXX");
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
			JsonObject body = new JsonObject();
			body.addProperty("name","clwh-name");
			body.addProperty("registered_name","name");
			body.addProperty("name","warehouse name");
			body.addProperty("address","123, sector 40 Gurgaon");
			body.addProperty("pin",456010);
			body.addProperty("phone","8888888888");
			HttpEntity<String> requestEntity=new HttpEntity<String>(body.toString(),headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from editClientWarehouseDetails api Delhivery in milli seconds:"+t3);
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("201".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(jsonObj);
			}
			else if("415".equalsIgnoreCase(response.getStatusCode().toString()) || "400".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("Api key required");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			httpClient.close();
		}
	}
	
//	format=json&data={
//			  "pickup_location": {
//			    "pin": "110096",
//			    "add": "address",
//			    "phone": "1111111111",
//			    "state": "Delhi",
//			    "city": "Delhi",
//			    "country": "India",
//			    "name": "name of pickup/warehouse location registered with delhivery"
//			  },
//			  "shipments": [{
//			    "return_name": "client-name-registered-with-delhivery"
//			    "return_pin": "110096",
//			    "return_city": "Delhi",
//			    "return_phone": "1111111111",
//			    "return_add": "address",
//			    "return_state": "Delhi",
//			    "return_country": "India",
//			    "order": "123467800",
//			    "phone": "1111111111",
//			    "products_desc": "", // Description of product which is used in shipping label
//			    "cod_amount": "1.0",
//			    "name": "Customer_name",
//			    "country": "India",
//			    "waybill": "waybillno.(trackingid)", (optional)
//			    "seller_inv_date": "",
//			    "order_date": "2018-05-18 06:22:43",
//			    "total_amount": "1.0",
//			    "seller_add": "",
//			    "seller_cst": "",
//			    "add": "jaipur, ",
//			    "seller_name": "",
//			    "seller_inv": "",
//			    "seller_tin": "",
//			    "pin": "110096",
//			    "quantity": "1",
//			    "payment_mode": "COD",
//			    "state": "Delhi",
//			    "city": "Delhi",
//			    "client": "client-name-registered with-delhvery"
//			  }]
//			}

	public ResponseEntity<String> orderCreation(ShipmentRequest request) throws IOException
	{
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		String url=DelhiveryBaseUrl+"/api/cmu/create.json";
		//String testUrl=baseUrlTest+"/api/cmu/create.json";
		try
		{
			JsonObject data = new JsonObject();
			JsonObject pickup=new JsonObject();
			JsonArray shiplocationArray=new JsonArray();
			JsonObject shiplocationObj=new JsonObject();
			pickup.addProperty("pin", request.getShipper().getPin());
			pickup.addProperty("add", request.getShipper().getAdd());
			pickup.addProperty("state", request.getShipper().getState());
			pickup.addProperty("city", request.getShipper().getCity());
			pickup.addProperty("phone", request.getShipper().getPhone());
			pickup.addProperty("country", request.getShipper().getCountry());
			pickup.addProperty("name", request.getShipper().getPickupName());
			shiplocationObj.addProperty("seller_name", request.getShipper().getPickupName());
			shiplocationObj.addProperty("seller_add", request.getShipper().getAdd());
			shiplocationObj.addProperty("return_name", request.getShipper().getPickupName());
			shiplocationObj.addProperty("return_pin", request.getShipper().getPin());
			shiplocationObj.addProperty("return_city", request.getShipper().getCity());
			shiplocationObj.addProperty("return_phone", request.getShipper().getPhone());
			shiplocationObj.addProperty("return_add", request.getShipper().getAdd());
			shiplocationObj.addProperty("return_state", request.getShipper().getState());
			shiplocationObj.addProperty("return_country", request.getShipper().getCountry());
			shiplocationObj.addProperty("order", request.getReceiver().getCustomerOrderId());
			String personPhone=request.getReceiver().getCustomerPhone();
			personPhone=personPhone.replaceAll("[%&;#-]", "");  
		    personPhone=personPhone.replace("\\","");
			shiplocationObj.addProperty("phone", personPhone);
//			System.out.println(request.getReceiver().getCustomerPhone());
			shiplocationObj.addProperty("products_desc", request.getProductDetails().getProductDescription());
			String personName=request.getReceiver().getCustomerPersonName();
			personName=personName.replaceAll("[%&;#]", "");  
		    personName=personName.replace("\\","");
			shiplocationObj.addProperty("name", personName);
			shiplocationObj.addProperty("country", request.getReceiver().getCustomerCountry());
			shiplocationObj.addProperty("order_date",request.getProductDetails().getOrderDate());
			shiplocationObj.addProperty("total_amount", request.getProductDetails().getTotalAmount());
			String personAddress=request.getReceiver().getCustomerAddress();
			personAddress=personAddress.replaceAll("[%&;#]", "");  
		    personAddress=personAddress.replace("\\","");
			shiplocationObj.addProperty("add", personAddress);
			shiplocationObj.addProperty("pin", request.getReceiver().getCustomerPin());
//			shiplocationObj.addProperty("quantity", request.getProductDetails().getQuantity());
			shiplocationObj.addProperty("payment_mode", request.getProductDetails().getPaymentMode());
			String personState=request.getReceiver().getCustomerState();
			personState=personState.replaceAll("[%&;#]", "");  
		    personState=personState.replace("\\","");
			shiplocationObj.addProperty("state", personState);
			String personCity=request.getReceiver().getCustomerCity();
			personCity=personCity.replaceAll("[%&;#]", "");  
		    personCity=personCity.replace("\\","");
			shiplocationObj.addProperty("city", personCity);
			shiplocationObj.addProperty("client", request.getShipper().getClientName());
//			shiplocationObj.addProperty("shipment_height",request.getProductDetails().getShipmentHeight());
//			shiplocationObj.addProperty("shipment_length", request.getProductDetails().getShipmentLength());
//			shiplocationObj.addProperty("shipment_width", request.getProductDetails().getShipmentWidth());
			shiplocationObj.addProperty("shipping_mode", request.getProductDetails().getShippingMode());
			data.add("pickup_location", pickup);
			shiplocationArray.add(shiplocationObj);
			data.add("shipments", shiplocationArray);
			String body="format=json&data="+data;
			logger.info("Request Body for "+request.getReceiver().getCustomerOrderId()+" order creation api is :"+body);
			HttpHeaders headers=new HttpHeaders();
			if(request.getProductDetails().getShippingMode().equalsIgnoreCase("Surface"))
				headers=createProdHeaderForSurface();
			else if(request.getProductDetails().getShippingMode().equalsIgnoreCase("Express"))
				headers=createProdHeaderForExpress();
			HttpEntity<String> requestEntity=new HttpEntity<String>(body.toString(),headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from order ceation api Delhivery in milli seconds:"+t3);
			logger.info("Response body from order ceation: "+response.getBody());
			JsonObject jsonObj=new JsonParser().parse(response.getBody()).getAsJsonObject();
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				//System.out.println("success");
				//System.out.println(jsonObj);
				String success=jsonObj.get("success").getAsString();
				String remark="error";
				String status="";
				int length=jsonObj.get("packages").getAsJsonArray().size();
				if(length>0)
				{
					status=jsonObj.get("packages").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsString();
					remark=jsonObj.get("packages").getAsJsonArray().get(0).getAsJsonObject().get("remarks").getAsJsonArray().get(0).getAsString();
				}
				else
				{
					remark=jsonObj.get("rmk").getAsString();
				}
				//System.out.println(success+":"+status+":"+remark);
				if(success.equalsIgnoreCase("false"))
					return new ResponseEntity<>(remark,rh,HttpStatus.BAD_REQUEST);
				else
				{
					logger.info("Successfull response from delhivery order creation api for OrderId : "+request.getReceiver().getCustomerOrderId());
					return new ResponseEntity<>(response.getBody(),rh,HttpStatus.OK);
				}
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus status=ex.getStatusCode();
			logger.info("HttpClientErrorException from order creation :"+ex);
			logger.info("HttpClientErrorException from order creation response body :"+s);
			logger.info("HttpClientErrorException from order creation response status :"+status);
			return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception from order creation :"+e);
			return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Error",rh,HttpStatus.BAD_REQUEST);
	}
	
//	Url = https://track.delhivery.com/api/kinko/v1/invoice/charges/.json/
//		querystring = {"md":"S","cgm":"10","o_pin":"250004","ss":"Delivered","d_pin":"250001"}
//
//		headers = {
//		    'authorization': "Token <Token_id>",
//		    'accept': "application/json",
//		    'content-type': "application/json"
//		    }
	public ResponseEntity<String> shippingChargesCalculation(String shippingMode,String destination_pin,String status, String origin_pin) throws IOException
	{
		//CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		String url=DelhiveryBaseUrl+"/api/kinko/v1/invoice/charges/.json";
		String weight="";
		String mode="";
//		String origin_pin="421302";
		HttpHeaders rh=new HttpHeaders();
		rh.add("Content-Type", "application/json");
		try
		{
			HttpHeaders headers=new HttpHeaders();
			if(shippingMode.equalsIgnoreCase("Surface"))
			{
				mode="S";
				weight="5000";
				headers=createProdHeaderForSurface();
			}
			else if(shippingMode.equalsIgnoreCase("Express"))
			{
				mode="E";
				weight="1000";
				headers=createProdHeaderForExpress();
			}
			
			 UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
		     .queryParam("md",mode)
		     .queryParam("cgm",weight)
		     .queryParam("o_pin",origin_pin)
		     .queryParam("ss",status)
			 .queryParam("d_pin",destination_pin)
			 .queryParam("pt", "Pre-paid");
			logger.info("shipping charges api paramteres are mode:"+mode+",weight:"+weight+",status:"+status+",destiantion_pin:"+destination_pin+",origin_pin:"+origin_pin); 
			 
			HttpEntity<String> requestEntity=new HttpEntity<String>(headers);
			RestTemplate restTemplate=new RestTemplate();
			long t1=System.currentTimeMillis();
			ResponseEntity<String> response=restTemplate.exchange(builder.toUriString(), HttpMethod.GET,requestEntity,String.class);
			long t2=System.currentTimeMillis();
			long t3=t2-t1;
			logger.info("Response time from shippingChargesCalculation api Delhivery in milli seconds:"+t3);
			logger.info("Response Body from shipping charges api"+response.getBody());
			if("200".equalsIgnoreCase(response.getStatusCode().toString()))
			{
				if(!(response.getBody().contains("error")))
				{
					JsonArray jsonArr= new JsonParser().parse(response.getBody()).getAsJsonArray();
					String totalAmount=jsonArr.get(0).getAsJsonObject().get("total_amount").getAsString();
					return new ResponseEntity<>(totalAmount,HttpStatus.OK);
				}
				else
				{
					return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
				}
			}
		}
		catch(HttpClientErrorException ex)
		{
			ex.printStackTrace();
			String s=ex.getResponseBodyAsString();
			HttpStatus statusCode=ex.getStatusCode();
			logger.info("HttpClientErrorException from shipping charges :"+ex);
			logger.info("HttpClientErrorException from shipping charges response body :"+s);
			logger.info("HttpClientErrorException from shipping charges response status :"+statusCode);
			return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception from shipping charges :"+e);
			return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
	}
	
}
