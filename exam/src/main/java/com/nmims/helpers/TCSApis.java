package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TCSExamBookingDataBean;
import com.nmims.beans.TCSMarksBean;
import com.nmims.daos.SifyDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.daos.TestDAO;


@Service("tcsHelper")
public class TCSApis {
//	public static final Log LOGGER = LogFactory.getLog(ClientCode.class);
	@Autowired(required=false)
	ApplicationContext act;

	
	@Autowired
	SifyDAO sifyDAO;
	
	@Autowired
	TCSApiDAO tcsDAO;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	@Autowired
	TestDAO testDAO;
	
	@Value("${TCS_AUTH_URL}")
	private String TCS_AUTH_URL;
	
	@Value("${TCS_SERVICE_KEY}")
	private String TCS_SERVICE_KEY;
	
	@Value("${TCS_URL}")
	public void getUrl(String TCS_URL) {
		TCSApis.tcsurl = TCS_URL;
	}
	
	@Value("${REQUEST_ACTION_ADD}")
	private String REQUEST_ACTION_ADD;
	
	@Value("${REQUEST_ACTION_READ}")
	private String REQUEST_ACTION_READ;
	
	@Value("${DATA_OFFSET}")
	private int DATA_OFFSET;
	
	@Value("${NUMBER_OF_RECORDS}")
	private int NUMBER_OF_RECORDS;
	
	@Value("${RESULT_FORM_ID}")
	private String RESULT_FORM_ID;
	
	@Value("${RESULT_ORG_ID}")
	private String RESULT_ORG_ID;
	
	
	StringWriter out;
	PreparedStatement ps = null, ps1 = null;
	int updateCnt = 0;
	ResultSet rs = null;
	ResultSetMetaData rsmdt = null;
	String queryMT = "";
	private final static String USER_AGENT = "Mozilla/5.0";
	private static String tcsurl = "";
	String CurrentDate = new SimpleDateFormat("dd/MMM/yyyy").format(new Date());
//	following 4 fields are to create request json
	JSONArray col_JA;
	JSONArray rec_JA = new JSONArray();
	JSONObject rec_list_JO;
	JSONObject addJO = null;
//	for insert request
//	private final int recordsPerRequest = 100;
	private final String FORM_ID = "64011"; //testing form id 
	private final String ORG_ID = "2221";
//	private final String REQUEST_ACTION = "ADD";  
	private final String RECORD_ACTION = "ADD";
	int i =0;
	int offset = 0;
	
//	for read request
//	private final int DATA_OFFSET = 0;
//	private final int NUMBER_OF_RECORDS = 100;
	


//	public void execute(List lstParameters, HashMap fieldValue, StudentMarksBean studentMarks)
	public ResponseBean execute(StudentMarksBean studentMarks, String isFullUpload)
			throws IOException, SQLException, Exception  {
		TCSApiDAO dao = (TCSApiDAO)act.getBean("tcsApiDAO");
		ResponseBean responseBean = new ResponseBean();
		String jsonTxt = "", tokenId = "", resp = "";
		
		
					
					
			String fieldName[] = {"Exam Year", "Exam Month", "User Id", "Password", "Subject Code", 
					"Subject Id", "First Name", "Last Name", "Exam Date", "Exam Time", "Exam Center"};
			
			String Value[]= {"",""};
			List<TCSExamBookingDataBean> examBookingList = new ArrayList<TCSExamBookingDataBean>();
			// JO>ADD_DATA_JO>RECORDS_JA>JO>COL_JA>every_column_asJO
//			get exam booking data
			if(isFullUpload.equals("YES")) {
				examBookingList = dao.getConfirmedBookingForGivenYearMonthForTCS(studentMarks,offset);
			}else {
				examBookingList = dao.syncUpdatedExamBookingData(studentMarks, offset);
			}
			
			/*
			 * 
			 * Insert data in col_JA as per logic
			after the insertion of data in col_JA
			put it in rec_JA
			
			*/
//			repeatedly send request for recordsPerRequest 
			
			if(examBookingList != null) {
				
				for(int j=0; j<examBookingList.size();j=j+NUMBER_OF_RECORDS) {
					JSONObject jobj = new JSONObject();

					offset = j+NUMBER_OF_RECORDS; 
					if(offset <= examBookingList.size() ) {
						if(examBookingList.size() - j == NUMBER_OF_RECORDS) {
							jobj = createJson(col_JA, rec_list_JO, examBookingList.subList(j, examBookingList.size()));
						}else {
							jobj = createJson(col_JA, rec_list_JO, examBookingList.subList(j, offset));
						}
						
					}else {
						jobj = createJson(col_JA, rec_list_JO, examBookingList.subList(j, examBookingList.size()));
					}
				
					
					
					jsonTxt = URLEncoder.encode((jobj.toString()).replace("\\/", "/"), "UTF-8");

					tokenId = getAuthenticate(TCS_AUTH_URL);

					if (tokenId != null && tokenId != "") {
						resp = getWebserviceApiData(jsonTxt, TCS_SERVICE_KEY, tokenId);
//									String jsonString = gson.toJson(resp);
						JSONParser jsonParser = new JSONParser();
						JSONObject json = (JSONObject)jsonParser.parse(resp);
						if(json.get("RESULT").equals("SUCCESS")) {
							responseBean.setStatus("success");
							responseBean.setCode(200);
							responseBean.setMessage(json.get("MESSAGE").toString());
						}
						else {
							responseBean.setStatus("fail");
							responseBean.setMessage(json.get("MESSAGE").toString());
							responseBean.setCode(422);
							break;
							
						}
					}
				}
				return responseBean;
			}else {
				responseBean.setStatus("fail");
				responseBean.setMessage("Records Not Found For Upload to TCS");
				responseBean.setCode(422);
				return responseBean;
			}
				
	}

	private static String getWebserviceApiData(String jsonText, String serviceKey, String TokenId) throws IOException {

		// https://preqa.tcsion.com/iONBizServices/iONWebService?servicekey=gg1X30kC40OxVZ7%2FHCF6YA&requestJsonData={REQUEST_ACTION":"GETFORMSAVAILABLE"}&tokenid=819651473251854589
		String urlParameters = "servicekey=" + serviceKey + "&requestJsonData=" + jsonText + "&tokenid=" + TokenId;
		
		String url = tcsurl;
		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private static String getAuthenticate(String AuthUrl) throws IOException {

		URL obj = new URL(AuthUrl);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String TokenId = response.toString();
		TokenId = TokenId.substring(TokenId.indexOf("TOKENID") + 11);
		TokenId = TokenId.substring(0, TokenId.indexOf("'"));

		return TokenId;
	}

	public HashMap validateFormForSubmit(List lstParameters, HttpServletRequest request) {
		boolean check = true;
		HashMap returnFormValues = new HashMap();
		try {
			String orgId = (String) lstParameters.get(0);
			String appId = (String) lstParameters.get(1);
			String formId = (String) lstParameters.get(2);
			HashMap hashMap = (HashMap) lstParameters.get(3);

			String subAction = (String) request.getAttribute("subAction");

			String appSeqNo = request.getParameter("app_seq_no");

			String NewAppStatus = "";

			hashMap.put("app_status", "Document Uploaded");
			NewAppStatus = hashMap.get("app_status").toString();

			returnFormValues.put("ErrorStatus", Boolean.valueOf(check));
			returnFormValues.put("FormValues", hashMap);
		} catch (Exception e) {
			check = false;
			
		}
		return returnFormValues;
	}
	
	public JSONObject createJson (JSONArray col_JA, JSONObject rec_list_JO, List<TCSExamBookingDataBean> examBookingList) throws ParseException {
		for(TCSExamBookingDataBean bean : examBookingList) {
			col_JA = new JSONArray();
			rec_list_JO = new JSONObject();
			JSONObject beanRow = new JSONObject();
			
			beanRow.put("NAME","Application Seq No");
			beanRow.put("VALUE", bean.getSapid() + bean.getSifySubjectCode());
			col_JA.add(beanRow);
			
			
			beanRow = new JSONObject();
			beanRow.put("NAME","App Status");
			beanRow.put("VALUE", "Pending");
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Remarks");
			beanRow.put("VALUE", "Testing");
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","App Date (dd/MMM/yyyy)");
			beanRow.put("VALUE", CurrentDate);
			col_JA.add(beanRow);
			
			
			Date date = new SimpleDateFormat("MMM").parse(bean.getMonth());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
			beanRow = new JSONObject();
			beanRow.put("NAME","Unique Request Id"); //combine sapid,subjectId,examYr,examMon
			beanRow.put("VALUE", bean.getSapid()+month+bean.getYear()+bean.getSifySubjectCode());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Exam Year");
			beanRow.put("VALUE", bean.getYear());
			col_JA.add(beanRow);
			
			
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Exam Month");
			beanRow.put("VALUE", bean.getMonth());
			col_JA.add(beanRow);
			
			
			beanRow = new JSONObject();
			beanRow.put("NAME","User Id");
			beanRow.put("VALUE", bean.getSapid());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Password");
			beanRow.put("VALUE", bean.getPassword());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Subject Code"); //max 11 chars
			beanRow.put("VALUE", "");
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Subject Id");
//			beanRow.put("VALUE", getSifySubCode(bean.getSapid(), bean.getSubject(), bean.getSem()));
			beanRow.put("VALUE", bean.getSifySubjectCode());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","First Name");
			beanRow.put("VALUE", bean.getFirstName());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Last Name");
			beanRow.put("VALUE", bean.getLastName());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Exam Date");
			beanRow.put("VALUE", bean.getExamDate());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Exam Time");
			beanRow.put("VALUE", bean.getExamTime());
			col_JA.add(beanRow);
			
			beanRow = new JSONObject();
			beanRow.put("NAME","Exam Center");
			beanRow.put("VALUE", bean.getCenterId());
			col_JA.add(beanRow);
			
			
			rec_list_JO.put("ID", i);
			rec_list_JO.put("COLUMNS", col_JA);
			rec_list_JO.put("RECORD_ACTION", RECORD_ACTION);
			rec_JA.add(rec_list_JO);


		
		

		
			addJO = new JSONObject();
			addJO.put("RECORDS", rec_JA);
			i++;
//						break;
		}

		JSONObject jobj = new JSONObject();
		jobj.put("FORM_ID", Integer.parseInt(RESULT_FORM_ID));
		jobj.put("ORG_ID", Integer.parseInt(RESULT_ORG_ID));
		jobj.put("REQUEST_ACTION", REQUEST_ACTION_ADD);
		jobj.put("ADD_DATA", addJO);
		
		rec_JA = new JSONArray();
		
		return jobj;
	}
	
	public String getSifySubCode(String subject, String program, String prgmStructApplicable, String sem) {
		TCSApiDAO dao = (TCSApiDAO)act.getBean("tcsApiDAO");
		return dao.getSifySubCode(subject,program,prgmStructApplicable,sem);
	}
	
	public JSONObject createReadDataJson(String appStatus, String fromDate, String toDate,int offset) {
		
		JSONObject filters = new JSONObject();
		filters.put("APPLICATION_STATUS", appStatus);
		filters.put("FROM_DATE", fromDate);
		filters.put("TO_DATE", toDate);
		
		JSONArray requiredFields = new JSONArray();
		
		JSONObject reqField = new JSONObject();
		
		reqField.put("NAME","Unique Request Id");
		reqField.put("ID","0");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Exam Year");
		reqField.put("ID","1");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Exam Month");
		reqField.put("ID","2");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","User Id");
		reqField.put("ID","3");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Password");
		reqField.put("ID","4");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Subject Code");
		reqField.put("ID","5");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Subject Id");
		reqField.put("ID","6");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","First Name");
		reqField.put("ID","7");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Last Name");
		reqField.put("ID","8");
		requiredFields.add(reqField);
		
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Exam Date");
		reqField.put("ID","9");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Exam Time");
		reqField.put("ID","10");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Exam Center");
		reqField.put("ID","11");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Section 1 Marks");
		reqField.put("ID","12");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Section 2 Marks");
		reqField.put("ID","13");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Section 3 Marks");
		reqField.put("ID","14");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Section 4 Marks");
		reqField.put("ID","15");
		requiredFields.add(reqField);
		
		
		/*
		 * reqField = new JSONObject(); reqField.put("NAME","Section 5 Marks");
		 * reqField.put("ID","16"); requiredFields.add(reqField);
		 */
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Total Marks");
		reqField.put("ID","16");
		requiredFields.add(reqField);
		
		
		reqField = new JSONObject();
		reqField.put("NAME","Attendance Status");
		reqField.put("ID","17");
		requiredFields.add(reqField);
		
		
		
		JSONObject jObjForRead = new JSONObject();
		jObjForRead.put("FILTERS", filters);
		jObjForRead.put("REQUIRED_FIELDS",requiredFields);
		jObjForRead.put("NUMBER_OF_RECORDS",NUMBER_OF_RECORDS);
		jObjForRead.put("DATA_OFFSET",offset);
		
		
		JSONObject readJobj = new JSONObject();
		readJobj.put("FORM_ID", Integer.parseInt(RESULT_FORM_ID));
		readJobj.put("ORG_ID", Integer.parseInt(RESULT_ORG_ID));
		readJobj.put("REQUEST_ACTION", REQUEST_ACTION_READ);
		readJobj.put("READ_DATA", jObjForRead);
		
		
		return readJobj;
	}
	
	public ResponseBean executePullRequest(String appStatus, String fromDate, String toDate,String month,String year)
			throws IOException, SQLException, Exception  {
			ResponseBean responseBean = new ResponseBean();
			String jsonTxt = "", tokenId = "", resp = "";
			String fieldName[] = {"Exam Year", "Exam Month", "User Id", "Password", "Subject Code", 
					"Subject Id", "First Name", "Last Name", "Exam Date", "Exam Time", "Exam Center"};
			
			String Value[]= {"",""};
	    	int NUMBER_OF_RECORDS2 = 0;

					TCSMarksBean studentMarks;
					ArrayList<TCSMarksBean> details=new ArrayList<>();
					//check if bookings exist for given year-month.
					int totalBookings = tcsDAO.getTotalCountofBookingsForGivenYearMonth(year,month) + 1000;
					if(totalBookings<=0) {
						responseBean.setStatus("fail");
						responseBean.setMessage("No bookings acquired!");
						return responseBean;	
					}
					
					//get unique subject codes list.
					HashMap<Integer,String> subjectCodesMap = sifyDAO.getUniqueSubjectCodes(); 
					int offset = 0;
					HashMap<String,String> checkMap = new HashMap<String,String>();	
					//get token id to pass in api.
					tokenId = getAuthenticate(TCS_AUTH_URL);
					while(totalBookings >0) {
						
						//create object with required fields to fetch data.
						JSONObject jsonObj = createReadDataJson(appStatus, fromDate, toDate,offset);
						jsonTxt = URLEncoder.encode((jsonObj.toString()).replace("\\/", "/"), "UTF-8");

						//fetching data from api start ----
						if (tokenId != null && tokenId != "") {
							resp = getWebserviceApiData(jsonTxt, TCS_SERVICE_KEY, tokenId);
//							String jsonString = gson.toJson(resp);
							JSONParser jsonParser = new JSONParser();
//							JSONObject json = (JSONObject)jsonParser.parse(resp);
							jsonParser.parse(resp);
							ObjectMapper mapper=new ObjectMapper();
							JsonNode rootNode = mapper.readTree(resp);
							
							String resultStatus = rootNode.get("RESULT").getTextValue();
							
							if(resultStatus.equalsIgnoreCase("SUCCESS")) {
							
					    	JsonNode drNode = rootNode.path("SEARCH_RESULTS").path("RECORDS");
					    	
//					    	Iterator<JsonNode> itr = drNode.getElements();
					    	NUMBER_OF_RECORDS2 = 0;
					    	try {
					    		if (drNode.isArray()) {
						            for (JsonNode arrayItem : drNode) {
						            	if(arrayItem.get("COLUMNS").isArray()) {
						            		String applSqlNum =  "";
								    		String uniqueReqId =  "";
								    		String examYear = "";
								    		String examMonth = "";
								    		String examDate = "";
								    		String examTime = "";
								    		int examCenter = 0;
								    		String sapid = ""; 
								    		String password = "";
								    		String subjectCode = "";
								    		int subjectId = 0; //subjectId is sifySubjectCode
								    		String subject = "";  
								    		String name =  "";
								    		Double totalScore = 0.0;
								    		Double sectionOneMarks = 0.0;
								    		Double sectionTwoMarks = 0.0;
								    		Double sectionThreeMarks = 0.0;
								    		Double sectionFourMarks = 0.0;
								    	//	Double sectionFiveMarks = 0.0; // removed section after confirmation from exam team.
								    		String attendanceStatus = "";
						            		for (JsonNode node : arrayItem.get("COLUMNS")) {
						            			 String temp = node.get("NAME").toString().replaceAll("^\"|\"$", "");
						            			 
									                if(temp.equals("Unique Request Id")) {
									                	uniqueReqId =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Exam Year")) {
									                	examYear =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Exam Month")) {
									                	examMonth =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Exam Date")) {
									                	examDate =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Exam Time")) {
									                	examTime =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Exam Center")) {
									                	examCenter =  node.get("VALUE").asInt(); //node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("User Id")) {
									                	sapid =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Password")) {
									                	password =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Subject Code")) {
									                	subjectCode =  node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
									                if(temp.equals("Subject Id")) {
									                	subjectId =  node.get("VALUE").asInt();
									                }
									                subject =  subjectCodesMap.get(subjectId);  
								                	if(temp.equals("First Name")) {
									                	name =  name + node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
								                	if(temp.equals("Last Name")) {
									                	name =  name + " " + node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }
								                	if(temp.equals("Total Marks")) {
									                	totalScore = node.get("VALUE").asDouble();
									                }
								                	if(temp.equals("Section 1 Marks")) {
									                	sectionOneMarks = node.get("VALUE").asDouble();
									                }
								                	if(temp.equals("Section 2 Marks")) {
									                	sectionTwoMarks = node.get("VALUE").asDouble();
									                }
								                	if(temp.equals("Section 3 Marks")) {
									                	sectionThreeMarks = node.get("VALUE").asDouble();
									                }
								                	if(temp.equals("Section 4 Marks")) {
									                	sectionFourMarks = node.get("VALUE").asDouble();
									                }
										/* removed section after confirmation from exam team.
										 * if(temp.equals("Section 5 Marks")) { sectionFiveMarks =
										 * node.get("VALUE").asDouble(); }
										 */
								                	if(temp.equals("Attendance Status")) {
								                		attendanceStatus = node.get("VALUE").toString().replaceAll("^\"|\"$", "");
									                }

										    		
						            		}
						            		TCSMarksBean tmb=new TCSMarksBean();
//								    		tmb.setApplicationSequenceNumber(applSqlNum);
								    		tmb.setUniqueRequestId(uniqueReqId);
								    		tmb.setSapid(sapid);
								    		tmb.setName(name);
								    		tmb.setExamDate(examDate);
								    		tmb.setExamTime(examTime);
								    		tmb.setYear(examYear);
								    		tmb.setMonth(examMonth);
								    		tmb.setSubjectId(subjectId);
								    		tmb.setSubject(subject);
								    		tmb.setSectionOneMarks(sectionOneMarks);
								    		tmb.setSectionTwoMarks(sectionTwoMarks);
										    tmb.setSectionThreeMarks(sectionThreeMarks);
										    tmb.setSectionFourMarks(sectionFourMarks);
										  //  tmb.setSectionFiveMarks(sectionFiveMarks); removed section after confirmation from exam team.
										    tmb.setTotalScore(totalScore);
										    tmb.setCenterCode(examCenter);
								    		tmb.setPassword(password); 
										    tmb.setAttendanceStatus(attendanceStatus);
										    
										    //added for testing.
										    String key = tmb.getUniqueRequestId();		
													
													if(!checkMap.containsKey(key)) {
														checkMap.put(key, tmb.getSapid());
													}
											
												details.add(tmb);
											
						            	}
						            	NUMBER_OF_RECORDS2 = NUMBER_OF_RECORDS2 +1;
						               
						            }
						        }
					    	}catch(Exception e) {
					    		
					    		responseBean.setStatus("fail");
								responseBean.setMessage("Error in fetching data");
								return responseBean;
					    	}
							}else {
								String resultMessage = rootNode.get("MESSAGE").getTextValue();
								responseBean.setStatus("fail");
								responseBean.setMessage(resultMessage);
							//	responseBean.setCode(422);
								return responseBean;	
							}
						}
						//fetching data from api end ----
						


						totalBookings=totalBookings-NUMBER_OF_RECORDS2;
						offset=offset+1000;
						if(NUMBER_OF_RECORDS2 == 0) {
							break;
						}
					}
					
			
			HashMap<String,String> studentTypeMap= new HashMap<String,String>();
			if(details.size()>0) {
				 List<TCSMarksBean> studentTypeList = tcsDAO.getStudentTypeMap();	
				 if(studentTypeList.size()>0) {
					 for(TCSMarksBean bean:studentTypeList) {
						 if(!studentTypeMap.containsKey(bean.getSapid())) {
							 studentTypeMap.put(bean.getSapid(), bean.getConsumerType());
							}
					 }
					 for(TCSMarksBean bean:details) {
						 String studentType = "";
						 try {
						 studentType = studentTypeMap.get(bean.getSapid());
						 }catch(Exception e) {
							 
						 }
						 bean.setStudentType(studentType);
					 }
				 }else {
						responseBean.setStatus("fail");
						responseBean.setMessage("No consumerType acquired!");
				 }
			  ArrayList<String> errorList = tcsDAO.addTcsData(details);
			  if(errorList.size() == 0) {
				  responseBean.setStatus("success");
			      responseBean.setCode(200); 
			      responseBean.setMessage(""); 
			      } else {
			      responseBean.setStatus("fail");
			      responseBean.setMessage("error unable to update data");
			      responseBean.setCode(422);
			  
			  }
			
			}else {
				responseBean.setStatus("fail");
				responseBean.setMessage("No results acquired!");
			}
			
			return responseBean;	
	}
	public List<TCSMarksBean> getTotalTCSDataSummary(String year,String month) throws ParseException{
		List<TCSMarksBean> tcsMarksList = tcsDAO.getTotalTCSDataSummary(year,month);
		return tcsMarksList;
	}
	
	public ArrayList<TCSMarksBean> getDataFromTcsTable(TCSMarksBean studentMarks) throws ParseException{
		ArrayList<TCSMarksBean> tcsMarksList = tcsDAO.getDataFromTcsTable(studentMarks);
		return tcsMarksList;
	}
	
	public List<TCSExamBookingDataBean> getConfirmedBookingForGivenYearMonthForTCS(StudentMarksBean studentMarks, int offset) {
		List<TCSExamBookingDataBean> examBookingList = tcsDAO.getConfirmedBookingForGivenYearMonthForTCS(studentMarks,offset);
		return examBookingList;
	}
	
	
	public List<TCSMarksBean> getTCSData(String year,String month,String subject,int subjectCode,String studentType) throws ParseException{
		List<TCSMarksBean> tcsMarksList= tcsDAO.getTCSData(year,month,subject,subjectCode,studentType);
		return tcsMarksList;
	}
	
	
	public List<TCSMarksBean> getTCSDataDetails(String year,String month,String subject,int subjectCode,String studentType) throws ParseException{
		List<TCSMarksBean> tcsMarksListDetails=tcsDAO.getTCSDataDetails(year,month,subject,subjectCode,studentType);
		return tcsMarksListDetails;
	}
	
	
	public void transferScoresToOnlineMarks(HttpServletRequest request,TCSMarksBean studentMarks,String userId) throws ParseException {
		ArrayList<TCSMarksBean> tcsDataList = null;
		String errorTcsUpsert="false";
		String successTcsUpsert="false";
		String errorTcsUpsertMessage ="";
		String successTcsUpsertMessage ="";
		tcsDataList = getDataFromTcsTable(studentMarks);
		
		ArrayList<OnlineExamMarksBean> listOfExamMarks=new ArrayList<>();
		ArrayList<OnlineExamMarksBean> listOfExamMarksError=new ArrayList<>();
		for(TCSMarksBean marks:tcsDataList) {
			OnlineExamMarksBean onlineExamMarksBean=new OnlineExamMarksBean();
			StudentMarksBean bean = new StudentMarksBean();
			bean.setYear(studentMarks.getYear());
			bean.setMonth(studentMarks.getMonth());
			//Check if student had registered for exam
//			List<TCSExamBookingDataBean> examBookingList = tcsHelper.getConfirmedBookingForGivenYearMonthForTCS(bean,0); ????
			ArrayList<ExamBookingTransactionBean> examBookingList = (ArrayList<ExamBookingTransactionBean>)studentMarksDAO.getConfirmedBookingForGivenYearMonth(marks.getSapid(), marks.getSubject(), marks.getYear(), marks.getMonth());
		
			if(examBookingList == null || examBookingList.size() == 0 || examBookingList.size() > 1){
				onlineExamMarksBean.setErrorMessage(" Exam Registration not found for record with SAPID:"+marks.getSapid()+ " & SUBJECT:"+marks.getSubject());
				onlineExamMarksBean.setErrorRecord(true);
				listOfExamMarksError.add(onlineExamMarksBean);
			}else{
				onlineExamMarksBean.setSem(examBookingList.get(0).getSem());
				onlineExamMarksBean.setProgram(examBookingList.get(0).getProgram());
				onlineExamMarksBean.setSapid(marks.getSapid());
				onlineExamMarksBean.setName(marks.getName());
				double total = marks.getTotalScore();
				onlineExamMarksBean.setTotal(total);
				if(total < 0){
					int roundedTotal = 0;
					onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
				}else{
					int roundedTotal = (int) Math.ceil(total);
					onlineExamMarksBean.setRoundedTotal(roundedTotal+"");
				}
				onlineExamMarksBean.setPart1marks(marks.getSectionOneMarks());
				onlineExamMarksBean.setPart2marks(marks.getSectionTwoMarks());
				onlineExamMarksBean.setPart3marks(marks.getSectionThreeMarks());
				onlineExamMarksBean.setPart4marks(marks.getSectionFourMarks());
				onlineExamMarksBean.setSubject(marks.getSubject());
				onlineExamMarksBean.setYear(marks.getYear());
				onlineExamMarksBean.setMonth(marks.getMonth());
				onlineExamMarksBean.setStudentType(marks.getStudentType());
				onlineExamMarksBean.setCreatedBy(userId);
				onlineExamMarksBean.setLastModifiedBy(userId);
				listOfExamMarks.add(onlineExamMarksBean);
				}
			}
//		temp commented
		if(listOfExamMarksError.size()>0) {
			errorTcsUpsert="true";
			errorTcsUpsertMessage=listOfExamMarksError.size()+" Records Not Transferred to Online Marks Table. \n ExamBooking Data not found for "+listOfExamMarksError.size()+" students.";
			request.getSession().setAttribute("errorTcsUpsert", errorTcsUpsert);
			request.getSession().setAttribute("errorTcsUpsertMessage", errorTcsUpsertMessage);
			request.getSession().setAttribute("successTcsUpsert", successTcsUpsert);
			for(OnlineExamMarksBean bean : listOfExamMarksError) {
			}
			
		}
		
		ArrayList<String> errorlist=studentMarksDAO.batchUpsertOnlineExamMarks(listOfExamMarks);
		if(errorlist.size()==0){
			successTcsUpsert="true";
			errorTcsUpsertMessage.concat("\n Records Not Transferred to Online Marks Table.");
			request.getSession().setAttribute("successTcsUpsert", successTcsUpsert);
			request.getSession().setAttribute("successTcsUpsertMessage", listOfExamMarks.size()+" Records Transferred to Online Marks Table.");
			request.getSession().setAttribute("errorTcsUpsert", errorTcsUpsert);
		}else {
			errorTcsUpsert="true";
			request.getSession().setAttribute("successTcsUpsert", successTcsUpsert);
			request.getSession().setAttribute("errorTcsUpsert", errorTcsUpsert);
			request.getSession().setAttribute("errorTcsUpsertMessage",errorTcsUpsertMessage );
		}
	}

	
	public ArrayList<String>  getConsumerTypesList() {
		ArrayList<ConsumerProgramStructureExam> consumerTypeList = testDAO.getConsumerTypeList();
		ArrayList<String> consumerType = new ArrayList<String>();
		for(ConsumerProgramStructureExam bean: consumerTypeList) {
			consumerType.add(bean.getName());
		}
		return consumerType;
	}
}


