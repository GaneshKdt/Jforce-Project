package com.nmims.controllers;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.WebCopycaseBean;
import com.nmims.daos.AssignmentsDAO;

@RestController
@RequestMapping("/m")
public class CopyLeaksController {
	
	String accessToken; 
	LocalDate date;
	
	@Value(value = "${COPYLEAKS_USER_EMAIL}")
	String COPYLEAKS_USER_EMAIL;
	
	@Value(value = "${COPYLEAKS_USER_KEY}")
	String COPYLEAKS_USER_KEY;
	
	@Value(value = "${COPYLEAKS_AUTHORIZATION_URL}")
	String COPYLEAKS_AUTHORIZATION_URL;
	
	@Value(value = "${COPYLEAKS_SCAN_URL}")
	String COPYLEAKS_SCAN_URL;
	
	@Value(value = "${COPYLEAKS_EXPORT_URL}")
	String COPYLEAKS_EXPORT_URL;
	
	@Value(value = "${ASSIGNMNET_FILE_UPLOAD_S3_URL}")
	String ASSIGNMNET_FILE_UPLOAD_S3_URL;
	
	@Value(value = "${SERVER_PATH}")
	String SERVER_PATH;
	
	@Value(value = "${ASSIGNMENT_PREVIEW_PATH}")
	String ASSIGNMENT_PREVIEW_PATH;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	private Map<String,String> userInputMap=new HashMap<String, String>();	
	private Map<String,AssignmentFileBean> studentInfoMap=new HashMap<String, AssignmentFileBean>();	
	private static final Logger copyLeaksControllerLogger = LoggerFactory.getLogger("assignmentWebCopyCase");
	
	@PostMapping(path = "/getWebCCReportFromCopyLeaks")
	public ResponseEntity<Map<String,Object>> getReport1( @RequestBody Map<String,AssignmentFileBean> req){
		
		AssignmentFileBean searchBean=req.get("searchBean");
		AssignmentFileBean assignmentFileBean=req.get("assignmentFileBean");
		userInputMap.put("month", searchBean.getMonth());
		userInputMap.put("year", searchBean.getYear());
		userInputMap.put("getMinMatchPercent", searchBean.getMinMatchPercent()+"");
		userInputMap.put("getThreshold2", searchBean.getThreshold2()+"");
		String subject=assignmentFileBean.getSubject();

		
		//authentication token generation
		String sapid=assignmentFileBean.getSapId();
		Map<String,String> authenticationmap=new HashMap<>();
		authenticationmap.put("email",COPYLEAKS_USER_EMAIL);
		authenticationmap.put("key",COPYLEAKS_USER_KEY);
		
		String  authenticationUrl=COPYLEAKS_AUTHORIZATION_URL;
		RestTemplate authenticationTemplate=new RestTemplate();
		
		HttpHeaders authenticationHeaders = new HttpHeaders();
		authenticationHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String,String>> authenticationResponseEntity = new HttpEntity<Map<String,String>>(authenticationmap,authenticationHeaders);
		
		Map<String,Object> authenticationResponseBody=new HashMap<String, Object>();
		try {
		ResponseEntity<Object> authenticationResponseResponse=authenticationTemplate.exchange(
				authenticationUrl,
			     HttpMethod.POST, authenticationResponseEntity,  Object.class);
				authenticationResponseBody = (HashMap<String,Object>)authenticationResponseResponse.getBody();
		}
		catch (Exception e) {
			copyLeaksControllerLogger.info("----- Error while generating access key ------"+e.getMessage());
		}
		
		//assign value to access token from response of authentication call
		accessToken=(String) authenticationResponseBody.get("access_token");
		
//		System.out.println("---- access key genrated succsfully "+accessToken+"------");
		copyLeaksControllerLogger.info("---- access key genrated succsfully "+accessToken+"------");
		
		
		
		//send assignment file to copyleks api to scan
		Map<String,Object> propertiesMap = new HashMap();
		Map<String,Object> webhooksMap = new HashMap();
		Map<String,Object> pdfMap = new HashMap();
		
			//unique scanid
			String ScanId = sapid+"_"+RandomStringUtils.randomAlphabetic(10).toLowerCase() ;
			studentInfoMap.put(ScanId, assignmentFileBean);
		
		
		
		System.out.println("ScanId :: "+ScanId);
		
		webhooksMap.put("status", SERVER_PATH+"exam/m/getWebhookResult/{STATUS}/"+ScanId);
		pdfMap.put("create", true);
		propertiesMap.put("webhooks", webhooksMap);
		propertiesMap.put("pdf", pdfMap);
		
		Map<String,Object> sendAssFilemap=new HashMap<>();
		sendAssFilemap.put("url",ASSIGNMENT_PREVIEW_PATH+assignmentFileBean.getPreviewPath());
		sendAssFilemap.put("properties",propertiesMap);
		
		String url=COPYLEAKS_SCAN_URL+ScanId;
		RestTemplate template=new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer "+accessToken);
		HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(sendAssFilemap,headers);
		ResponseEntity<Object> response=null;
		Map<String,Object> responseBody=null;
		try {
		response=template.exchange(
				url,
				HttpMethod.PUT, entity,  Object.class);
		 responseBody = (HashMap<String,Object>)response.getBody();
		}
		catch (Exception e) {
			copyLeaksControllerLogger.info("----- Error while sending file to copyleaks api for ScanId ------"+ScanId);
			copyLeaksControllerLogger.info("----- Error : while sending file to copyleaks api error is ------"+e.getMessage());
		}
//		System.out.println();
//		System.out.println("------------- file is sends succussefully ------"+ASSIGNMENT_PREVIEW_PATH+assignmentFileBean.getPreviewPath());
		copyLeaksControllerLogger.info("----- file is sends succussefully ------"+ASSIGNMENT_PREVIEW_PATH+assignmentFileBean.getPreviewPath());
//		System.out.println("response:"+responseBody);
		
		return new ResponseEntity<>(responseBody,HttpStatus.OK);
	}
	
	
	
	
//	@PostMapping(path = "/getScanResult")
//	public ResponseEntity<Map<String,Object>> getScanResult(){
//		System.out.println("CopyLeaksController.getScanResult()");
//		Map<String,Object> propertiesMap = new HashMap();
//		Map<String,Object> webhooksMap = new HashMap();
//		Map<String,Object> pdfMap = new HashMap();
//		webhooksMap.put("status", "https://8b0e-202-134-191-26.in.ngrok.io/exam/m/getWebhookResult/{STATUS}/771160008494");
//		pdfMap.put("create", true);
//		propertiesMap.put("webhooks", webhooksMap);
//		propertiesMap.put("pdf", pdfMap);
//		
//		Map<String,Object> map=new HashMap<>();
//		map.put("url","https://testdummyfiles.s3.ap-south-1.amazonaws.com/77116000836_Business_Statistics.pdf");
//		map.put("properties",propertiesMap);
//		
//		String url="https://api.copyleaks.com/v3/scans/submit/url/771160008494";
//		RestTemplate template=new RestTemplate();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		headers.set("Authorization", "Bearer DEC50C84DBC49F39A94B33DD361E9F79D418C45B701C1998D9A9142410C88FBF");
//		HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(map,headers);
//		ResponseEntity<Object> response=null;
//		Map<String,Object> responseBody=null;
//		try {
//		response=template.exchange(
//				url,
//				HttpMethod.PUT, entity,  Object.class);
//		 responseBody = (HashMap<String,Object>)response.getBody();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		System.out.println("response:"+responseBody);
//		
//		return new ResponseEntity<>(responseBody,HttpStatus.OK);
//	}
//	
	
	
	    public static String convertToString(Map<String, Object> map) {
	        JSONObject jsonObject = new JSONObject(map);
	        return jsonObject.toString();
	    }
	
		
	@PostMapping(path = "/getWebhookResult/{Status}/{ScanId}")
	public ResponseEntity<String> getWebhookResult(@PathVariable String Status,@PathVariable String ScanId,@RequestBody Map<String,Object> req){
//		System.out.println();
//		System.out.println("---------- scan completed ---------------");
//		System.out.println("Scan Staus :: "+Status);
//		System.out.println("ScanId :: "+ScanId);
		
		copyLeaksControllerLogger.info("--- scan completed ---");
		copyLeaksControllerLogger.info("ScanId :: "+ScanId);
		copyLeaksControllerLogger.info("Scan Staus :: "+Status);
		copyLeaksControllerLogger.info("Scan Staus Complete Json Responce :: "+req);
		
		
//		System.out.println(" :: "+req.get("results"));
		
		Map<String,Object> resultsMap=(Map<String, Object>) req.get("results");
		Map<String,Double> scoreMap=(Map<String, Double>) resultsMap.get("score");
		Map<String,String> scannedDocumentMap=(Map<String, String>) req.get("scannedDocument");
		
//		System.out.println("---------------");
//		System.out.println("resultsMap :: "+resultsMap);
//		System.out.println("---------------");
//		System.out.println("---------------");
//		System.out.println("scoreMap :: "+ scoreMap);
//		System.out.println("---------------");
//		System.out.println("---------------");
//		System.out.println("scannedDocumentMap :: "+scannedDocumentMap);
//		System.out.println("---------------");
		
		
		System.out.println("--user inputs :: "+userInputMap);
		
		
		
		System.out.println();
		
		AssignmentFileBean studentInfo=studentInfoMap.get(ScanId);
		String fileName=studentInfo.getSubject();
		fileName = fileName.replaceAll("'", "_");
		fileName = fileName.replaceAll(",", "_");
		fileName = fileName.replaceAll("&", "and");
		fileName = fileName.replaceAll(" ", "_");

		String exportFileName = studentInfo.getSapId()+"_"+fileName+"_" + RandomStringUtils.randomAlphanumeric(10);
		
		
		
		try {
			
			if((scoreMap.get("aggregatedScore")) >= (Double.parseDouble(userInputMap.get("getMinMatchPercent")))) {
			
			WebCopycaseBean bean=new WebCopycaseBean();
			bean.setSubject(studentInfo.getSubject());
			bean.setMonth(userInputMap.get("month"));
			bean.setYear(userInputMap.get("year"));
			bean.setSapid(studentInfo.getSapId());
			bean.setName(studentInfo.getFirstName()+" "+studentInfo.getLastName());
			bean.setAggregatedScore(String.valueOf(scoreMap.get("aggregatedScore")));
			bean.setTotalWords(String.valueOf(scannedDocumentMap.get("totalWords")));
			bean.setIdenticalWords(String.valueOf(scoreMap.get("identicalWords")));
			bean.setMinorChangedWords(String.valueOf(scoreMap.get("minorChangedWords")));
			bean.setRelatedMeaningWords(String.valueOf(scoreMap.get("relatedMeaningWords")));
			bean.setWebReportPdfPath(exportFileName+".pdf");
			bean.setThreshold(userInputMap.get("getMinMatchPercent"));
			bean.setResponseJson(convertToString(req));
			
			
			asignmentsDAO.saveInWebcopyCaseDetailedResult(bean);
			//export pdf into s3 bucket
			getExportPdfResult(ScanId,exportFileName);
			}
			
//			System.out.println("student name :: "+studentInfo.getFirstName()+" "+studentInfo.getLastName());
//			System.out.println("student Subject :: "+studentInfo.getSubject());
//			System.out.println("student Month :: "+userInputMap.get("month"));
//			System.out.println("student year :: "+userInputMap.get("year"));
//			System.out.println("student sapid :: "+studentInfo.getSapId());
//		System.out.println("aggregatedScore :: ");
//		System.out.println(String.valueOf(scoreMap.get("aggregatedScore")));
//		System.out.println("identicalWords :: ");
//		System.out.println(String.valueOf(scoreMap.get("identicalWords")));
//		System.out.println("minorChangedWords :: ");
//		System.out.println(String.valueOf(scoreMap.get("minorChangedWords")));
//		System.out.println("relatedMeaningWords :: ");
//		System.out.println(String.valueOf(scoreMap.get("relatedMeaningWords")));
//		System.out.println("totalWords :: ");
//		System.out.println(String.valueOf(scannedDocumentMap.get("totalWords")));
		
		
		
		}
		catch (Exception e) {
			copyLeaksControllerLogger.info("----- Error while inserting data into WebcopyCaseDetailedResult table for ScanId : ------"+ScanId+"  Error :: "+e.getMessage());
		}
		
		
		
		
		
		
		return new ResponseEntity<>("Scan Status is recived",HttpStatus.OK);
	}	
	
	
	@PostMapping(path = "/getWebhookExportResult/export/{exportId}/{Status}")
	public ResponseEntity<String> getWebhookExportResult(@PathVariable String exportId,@PathVariable String Status,@RequestBody Object obj){
		
//		System.out.println();
//		System.out.println("---------- Export completed ---------------");
//		System.out.println("export Status :: "+Status);
//		System.out.println("exportId :: "+exportId);
		
		copyLeaksControllerLogger.info("---------- Export completed ---------------");
		copyLeaksControllerLogger.info("export Status :: "+Status);
		copyLeaksControllerLogger.info("exportId :: "+exportId);
		
		System.out.println("Body Containt :: "+obj);
		
		
		
		return new ResponseEntity<>("Export Status is recived",HttpStatus.OK);
	}

	
	public void getExportPdfResult(String scanId,String exportFileName){
//		System.out.println("CopyLeaksController.getScanResult()");
		
		
		Map<String,Object> pdfReport = new HashMap();
		Map<String,Object> crawledVersion = new HashMap();
		
		pdfReport.put("verb", "PUT");
		pdfReport.put("endpoint", ASSIGNMNET_FILE_UPLOAD_S3_URL+exportFileName+".pdf");
		crawledVersion.put("verb", "PUT");
		crawledVersion.put("endpoint", "https://hfdgh7j9dl.execute-api.ap-south-1.amazonaws.com/demo/testdemopublic/export/77116000849528/crawled-version");
		Map<String,Object> finalMap=new HashMap<>();
		finalMap.put("results", new ArrayList<>());
		finalMap.put("pdfReport",pdfReport);
		finalMap.put("crawledVersion",crawledVersion);
		finalMap.put("completionWebhook",SERVER_PATH+"exam/m/getWebhookExportResult/export/"+scanId+"/completed");
		finalMap.put("maxRetries",3);
		
		
		
		String url=COPYLEAKS_EXPORT_URL+scanId+"/export/"+scanId;
		RestTemplate template=new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer "+accessToken);
		HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(finalMap,headers);
		ResponseEntity<Object> response=null;
		Map<String,Object> responseBody=null;
		try {
		response=template.exchange(
				url,
				HttpMethod.POST, entity,  Object.class);
		 responseBody = (HashMap<String,Object>)response.getBody();
		}
		catch (Exception e) {
			copyLeaksControllerLogger.info("----- Error while sending Export pdf Report to copyleaks api ------"+e.getMessage());
			// TODO: handle exception
		}
//		System.out.println();
//		System.out.println("---------- file export reuest has bean sent ---------------");
		copyLeaksControllerLogger.info("---------- file export reuest has bean sent ---------------");
		
		
	}
//	@PostMapping(path = "/getExportPdfResult")
//	public ResponseEntity<Map<String,Object>> getExportPdfResult(){
//		System.out.println("CopyLeaksController.getScanResult()");
//		
//		
//		Map<String,Object> pdfReport = new HashMap();
//		Map<String,Object> crawledVersion = new HashMap();
//		
//		pdfReport.put("verb", "PUT");
//		pdfReport.put("endpoint", "https://hfdgh7j9dl.execute-api.ap-south-1.amazonaws.com/demo/testdemopublic/77116000849128.pdf");
//		crawledVersion.put("verb", "PUT");
//		crawledVersion.put("endpoint", "https://hfdgh7j9dl.execute-api.ap-south-1.amazonaws.com/demo/testdemopublic/export/77116000849528/crawled-version");
//		Map<String,Object> finalMap=new HashMap<>();
//		finalMap.put("results", new ArrayList<>());
//		finalMap.put("pdfReport",pdfReport);
//		finalMap.put("crawledVersion",crawledVersion);
//		finalMap.put("completionWebhook","https://ff06-49-33-224-138.in.ngrok.io/exam/m/getWebhookExportResult/export/77116000849528/completed");
//		finalMap.put("maxRetries",3);
//		
//		
//		
//		String url="https://api.copyleaks.com/v3/downloads/7711600084916/export/77116000849528";
//		RestTemplate template=new RestTemplate();
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		headers.set("Authorization", "Bearer DEC50C84DBC49F39A94B33DD361E9F79D418C45B701C1998D9A9142410C88FBF");
//		HttpEntity<Map<String,Object>> entity = new HttpEntity<Map<String,Object>>(finalMap,headers);
//		ResponseEntity<Object> response=null;
//		Map<String,Object> responseBody=null;
//		try {
//			response=template.exchange(
//					url,
//					HttpMethod.POST, entity,  Object.class);
//			responseBody = (HashMap<String,Object>)response.getBody();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		System.out.println("response:"+responseBody);
//		
//		return new ResponseEntity<>(responseBody,HttpStatus.OK);
//	}
	
	
	
	

}
