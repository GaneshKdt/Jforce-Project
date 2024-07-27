package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.nmims.beans.SessionDayTimeAcadsBean;


public class WebExMeetingManager {

	private String site;       
	private String webExID;// = "NMIMSADMIN";                // Host username
	private String password; // = "Abcd@1234";               // Host password
	
	//private String siteName = "nmims";                  // WebEx site name
	private String xmlURL = "WBXService/XMLService";    // XML API URL
	private String siteID = "806157";                     // Site ID
	private String partnerID = "HOVY53Ux_qbgVlCJFaprRQ";                  // Partner ID
	private String meetingPassword = "NMIMS";
	
	public String getWebExID() {
		return webExID;
	}

	public void setWebExID(String webExID) {
		this.webExID = webExID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void scheduleTrainingSession(SessionDayTimeAcadsBean session) throws Exception{
		disableSslVerification();

//		session.setMeetingPwd(meetingPassword);

		String xmlServerURL = site + xmlURL;
		System.out.println("Host ID "+session.getAltHostId()+" host Password : "+ session.getHostPassword()+" meetingPwrd : "+session.getMeetingPwd());
		System.out.println("Site = "+site);
		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);

		// URLConnection supports HTTPS protocol only with JDK 1.4+
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + session.getHostId() + "</webExID>"
				+ "			<password>" + session.getHostPassword() + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				//+ "			<email>johnsmith@xyz.com</email>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.CreateTrainingSession\">"
				+ "			<accessControl>"
				+ "				<listing>UNLISTED</listing>"
				+ "				<sessionPassword>"+meetingPassword+"</sessionPassword>"
				+ "			</accessControl>"
				+ "			<schedule>"
				+ "				<startDate>"+ session.getDate() +" " + session.getStartTime()+"</startDate>"
				+ "				<timeZone>GMT+05:30, India (Bombay)</timeZone>"
				+ "				<duration>120</duration>"
				//+ "				<timeZoneID>46</timeZoneID>"
				+ "				<openTime>45</openTime>"
				//+ "				<joinTeleconfBeforeHost>true</joinTeleconfBeforeHost>" Not applicable for VOIP I guess
				+ "				<entryExitTone>NOTONE</entryExitTone>"
				+ "			</schedule>"
				+ "			<metaData>"
				+ "				<confName>"+ session.getSubject().replaceAll("&", "and") +"-"+ session.getSessionName().replaceAll("&", "and")+"</confName>"
				//+ "				<agenda>agenda 1</agenda>"
				//+ "				<description>description</description>"
				+ "				<greeting>Welcome Participants!</greeting>"
				//+ "				<location>location</location>"
				//+ "				<invitation>invitation</invitation>"
				+ "			</metaData>"
				+ "			<enableOptions>"
				+ "				<attendeeList>false</attendeeList>"
				+ "				<javaClient>false</javaClient>"
				+ "				<nativeClient>true</nativeClient>"
				+ "				<chat>true</chat>"
				//+ "				<chatHost>true</chatHost> "
				//+ "				<chatPresenter>true</chatPresenter> "
				//+ "				<chatAllAttendees>false</chatAllAttendees> "
				+ "				<poll>false</poll>"
				+ "				<audioVideo>true</audioVideo>"
				+ "				<fileShare>false</fileShare>"
				+ "				<presentation>false</presentation>"
				+ "				<applicationShare>false</applicationShare>"
				+ "				<desktopShare>false</desktopShare>"
				+ "				<webTour>false</webTour>"
				+ "				<trainingSessionRecord>false</trainingSessionRecord>"
				+ "				<annotation>false</annotation>"
				+ "				<importDocument>false</importDocument>"
				+ "				<saveDocument>true</saveDocument>"
				+ "				<printDocument>false</printDocument>"
				+ "				<pointer>false</pointer>"
				+ "				<switchPage>false</switchPage>"
				+ "				<fullScreen>false</fullScreen>"
				+ "				<thumbnail>false</thumbnail>"
				+ "				<zoom>false</zoom>"
				+ "				<copyPage>false</copyPage>"
				+ "				<rcAppShare>false</rcAppShare>"
				+ "				<rcDesktopShare>false</rcDesktopShare>"
				+ "				<rcWebTour>false</rcWebTour>"
				+ "				<attendeeRecordTrainingSession>false"
				+ "				</attendeeRecordTrainingSession>"
				+ "				<voip>true</voip>"
				+ "				<faxIntoTrainingSession>false</faxIntoTrainingSession>"
				+ "				<autoDeleteAfterMeetingEnd>false</autoDeleteAfterMeetingEnd>"
				+ "				<muteOnEntry>true</muteOnEntry>"
				+ "				<multiVideo>false</multiVideo>"
				+ "			</enableOptions>"
				+ "			<telephony>"
//Commented by Vikas+ "<telephonySupport>NONE</telephonySupport>"
				+ "				<numPhoneLines>4</numPhoneLines>"
				+ "				<extTelephonyURL>String</extTelephonyURL>"
				+ "				<extTelephonyDescription>String</extTelephonyDescription>"
				//Commented by Vikas+ "				<enableTSP>false</enableTSP>"
				//Commented by Vikas+ "				<tspAccountIndex>1</tspAccountIndex>"
				//+ "				<HQVideo>true</HQVideo>"
				+ "			</telephony>"

				+ "		</bodyContent>"
				+ "	</body>"			

		    
				+ "</serv:message>";

		System.out.println("reqXML = "+reqXML);

		// send request
		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		// read response
		BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		//System.out.println("\nXML Response\n");        
		System.out.println(respXML);
		
		parseCreateSessionResponse(session, respXML);
		
	}
	
	//Updated for batchUpload start
	public List<String> scheduleTrainingSessionForBatchUpload(String hostId, String hostPassword,String date, String startTime,String subject, String sessionName) throws Exception{
		disableSslVerification();
		
		List<String> returnValues = new ArrayList<String>();
		returnValues.add(meetingPassword);
		
		String xmlServerURL = site + xmlURL;

		System.out.println("Site = "+site);
		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);

		// URLConnection supports HTTPS protocol only with JDK 1.4+
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + hostId + "</webExID>"
				+ "			<password>" + hostPassword + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				//+ "			<email>johnsmith@xyz.com</email>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.CreateTrainingSession\">"
				+ "			<accessControl>"
				+ "				<listing>UNLISTED</listing>"
				+ "				<sessionPassword>"+meetingPassword+"</sessionPassword>"
				+ "			</accessControl>"
				+ "			<schedule>"
				+ "				<startDate>"+ date +" " + startTime+"</startDate>"
				+ "				<timeZone>GMT+05:30, India (Bombay)</timeZone>"
				+ "				<duration>120</duration>"
				//+ "				<timeZoneID>46</timeZoneID>"
				+ "				<openTime>45</openTime>"
				//+ "				<joinTeleconfBeforeHost>true</joinTeleconfBeforeHost>" Not applicable for VOIP I guess
				+ "				<entryExitTone>NOTONE</entryExitTone>"
				+ "			</schedule>"
				+ "			<metaData>"
				+ "				<confName>"+ subject.replaceAll("&", "and") +"-"+ sessionName.replaceAll("&", "and")+"</confName>"
				//+ "				<agenda>agenda 1</agenda>"
				//+ "				<description>description</description>"
				+ "				<greeting>Welcome Participants!</greeting>"
				//+ "				<location>location</location>"
				//+ "				<invitation>invitation</invitation>"
				+ "			</metaData>"
				+ "			<enableOptions>"
				+ "				<attendeeList>false</attendeeList>"
				+ "				<javaClient>false</javaClient>"
				+ "				<nativeClient>true</nativeClient>"
				+ "				<chat>true</chat>"
				//+ "				<chatHost>true</chatHost> "
				//+ "				<chatPresenter>true</chatPresenter> "
				//+ "				<chatAllAttendees>false</chatAllAttendees> "
				+ "				<poll>false</poll>"
				+ "				<audioVideo>true</audioVideo>"
				+ "				<fileShare>false</fileShare>"
				+ "				<presentation>false</presentation>"
				+ "				<applicationShare>false</applicationShare>"
				+ "				<desktopShare>false</desktopShare>"
				+ "				<webTour>false</webTour>"
				+ "				<trainingSessionRecord>false</trainingSessionRecord>"
				+ "				<annotation>false</annotation>"
				+ "				<importDocument>false</importDocument>"
				+ "				<saveDocument>true</saveDocument>"
				+ "				<printDocument>false</printDocument>"
				+ "				<pointer>false</pointer>"
				+ "				<switchPage>false</switchPage>"
				+ "				<fullScreen>false</fullScreen>"
				+ "				<thumbnail>false</thumbnail>"
				+ "				<zoom>false</zoom>"
				+ "				<copyPage>false</copyPage>"
				+ "				<rcAppShare>false</rcAppShare>"
				+ "				<rcDesktopShare>false</rcDesktopShare>"
				+ "				<rcWebTour>false</rcWebTour>"
				+ "				<attendeeRecordTrainingSession>false"
				+ "				</attendeeRecordTrainingSession>"
				+ "				<voip>true</voip>"
				+ "				<faxIntoTrainingSession>false</faxIntoTrainingSession>"
				+ "				<autoDeleteAfterMeetingEnd>false</autoDeleteAfterMeetingEnd>"
				+ "				<muteOnEntry>true</muteOnEntry>"
				+ "				<multiVideo>false</multiVideo>"
				+ "			</enableOptions>"
				+ "			<telephony>"
//Commented by Vikas+ "<telephonySupport>NONE</telephonySupport>"
				+ "				<numPhoneLines>4</numPhoneLines>"
				+ "				<extTelephonyURL>String</extTelephonyURL>"
				+ "				<extTelephonyDescription>String</extTelephonyDescription>"
				//Commented by Vikas+ "				<enableTSP>false</enableTSP>"
				//Commented by Vikas+ "				<tspAccountIndex>1</tspAccountIndex>"
				//+ "				<HQVideo>true</HQVideo>"
				+ "			</telephony>"

				+ "		</bodyContent>"
				+ "	</body>"			

		    
				+ "</serv:message>";

		System.out.println("reqXML = "+reqXML);

		// send request
		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		// read response
		BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		//System.out.println("\nXML Response\n");        
		System.out.println(respXML);
		
		return parseCreateSessionResponseForBatchUpload(returnValues, respXML);
		
	}
	public List<String> parseCreateSessionResponseForBatchUpload(List<String> returnValues, String respXML) throws Exception{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(respXML));
	    Document doc = db.parse(is);
	    NodeList nodes = doc.getElementsByTagName("serv:result");
	    Node node = nodes.item(0);
	    String result = node.getTextContent();
	    System.out.println("Result = "+result);
	    if("SUCCESS".equalsIgnoreCase(result)){
	    	nodes = doc.getElementsByTagName("train:sessionkey");
	    	String meetingKey = nodes.item(0).getTextContent();
	    	System.out.println("In parseCreateSessionResponseForBatchUpload generated meetingKey : "+meetingKey);
	    	returnValues.add(meetingKey);
	    	return returnValues;
	    }else if("FAILURE".equalsIgnoreCase(result)){
	    	nodes = doc.getElementsByTagName("serv:reason");
	    	String failureReason = nodes.item(0).getTextContent();
	    	System.out.println("failureReason = "+failureReason);
	    	returnValues.add("");
	    	returnValues.add(failureReason);
	    }
	    
	    return returnValues;
	}
	//updated for batchUpload end
	
	
	public void deleteTrainingSession(SessionDayTimeAcadsBean session) throws Exception{
		disableSslVerification();

		session.setMeetingPwd(meetingPassword);

		String xmlServerURL = site + xmlURL;

		System.out.println("Site = "+site);
		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);

		// URLConnection supports HTTPS protocol only with JDK 1.4+
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + session.getHostId() + "</webExID>"
				+ "			<password>" + session.getHostPassword() + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.DelTrainingSession\">"
				+ "				<sessionKey>"+session.getMeetingKey()+"</sessionKey>"
				+ "		</bodyContent>"
				+ "	</body>"			
				+ "</serv:message>";

		//System.out.println("reqXML = "+reqXML);

		// send request
		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		//System.out.println(reqXML+"\n");

		// read response
		BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		System.out.println("\nDelete Session : XML Response\n");        
		System.out.println(respXML);
		
		parseDeleteSessionResponse(session, respXML);
		
	}
	
	public void parseCreateSessionResponse(SessionDayTimeAcadsBean session, String respXML) throws Exception{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(respXML));
	    Document doc = db.parse(is);
	    NodeList nodes = doc.getElementsByTagName("serv:result");
	    String result = nodes.item(0).getTextContent();
	    System.out.println("Result = "+result);
	    if("SUCCESS".equalsIgnoreCase(result)){
	    	session.setErrorRecord(false);
	    	nodes = doc.getElementsByTagName("train:sessionkey");
	    	String meetingKey = nodes.item(0).getTextContent();
	    	session.setMeetingKey(meetingKey);
	    }else if("FAILURE".equalsIgnoreCase(result)){
	    	session.setErrorRecord(true);
	    	nodes = doc.getElementsByTagName("serv:reason");
	    	String failureReason = nodes.item(0).getTextContent();
	    	System.out.println("failureReason = "+failureReason);
	    	session.setErrorRecord(true);
	    	session.setErrorMessage(failureReason);
	    }
	    
	    
	}
	
	
	public void parseDeleteSessionResponse(SessionDayTimeAcadsBean session, String respXML) throws Exception{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(respXML));
	    Document doc = db.parse(is);
	    NodeList nodes = doc.getElementsByTagName("serv:result");
	    String result = nodes.item(0).getTextContent();
	    System.out.println("Result = "+result);
	    if("SUCCESS".equalsIgnoreCase(result)){
	    	session.setErrorRecord(false);
	    }else if("FAILURE".equalsIgnoreCase(result)){
	    	session.setErrorRecord(true);
	    	nodes = doc.getElementsByTagName("serv:reason");
	    	String failureReason = nodes.item(0).getTextContent();
	    	System.out.println("failureReason = "+failureReason);
	    	session.setErrorMessage(failureReason);
	    	session.setErrorRecord(true);
	    }
	    
	    
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		disableSslVerification();
		
		String siteName = "nmims";                  // WebEx site name
		String xmlURL = "WBXService/XMLService";    // XML API URL
		String siteID = "806157";                     // Site ID
		String partnerID = "HOVY53Ux_qbgVlCJFaprRQ";                  // Partner ID
		String webExID = "NMIMSADMIN";                // Host username
		String password = "Abcd@1234";               // Host password

	/*	System.setProperty("https.proxyHost", "172.25.9.120");
		System.setProperty("https.proxyPort",  "2006");
		System.setProperty("https.auth.preference", "NTLM");


		Authenticator.setDefault(new Authenticator() {
	        @Override
	        public PasswordAuthentication getPasswordAuthentication() {
	            System.out.println(getRequestingScheme() + " authentication");
	             // Remember to include the NT domain in the username
	            return new PasswordAuthentication("pwiodc" + "\\" +  "718962", "Feb2015&".toCharArray());
	        }
	    });*/
		
		//String xmlServerURL = "https://"+siteName+".webex.com/"+xmlURL;
		String xmlServerURL = "https://114.29.213.193/"+xmlURL;

		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);

		// URLConnection supports HTTPS protocol only with JDK 1.4+
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + webExID + "</webExID>"
				+ "			<password>" + password + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				//+ "			<email>johnsmith@xyz.com</email>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.CreateTrainingSession\">"
				+ "			<accessControl>"
				+ "				<listing>UNLISTED</listing>"
				+ "				<sessionPassword>NMIMS</sessionPassword>"
				+ "			</accessControl>"
				+ "			<schedule>"
				+ "				<startDate>4/05/2015 10:00:00</startDate>"
				+ "				<timeZone>GMT+05:30, India (Bombay)</timeZone>"
				+ "				<duration>60</duration>"
				+ "				<timeZoneID>46</timeZoneID>"
				+ "				<openTime>20</openTime>"
				//+ "				<joinTeleconfBeforeHost>true</joinTeleconfBeforeHost>" Not applicable for VOIP I guess
				+ "				<entryExitTone>NOTONE</entryExitTone>"
				+ "			</schedule>"
				+ "			<metaData>"
				+ "				<confName>jimz hol test</confName>"
				//+ "				<agenda>agenda 1</agenda>"
				+ "				<description>description</description>"
				+ "				<greeting>Welcome Participants!</greeting>"
				//+ "				<location>location</location>"
				//+ "				<invitation>invitation</invitation>"
				+ "			</metaData>"
				+ "			<enableOptions>"
				+ "				<attendeeList>false</attendeeList>"
				+ "				<javaClient>false</javaClient>"
				+ "				<nativeClient>true</nativeClient>"
				+ "				<chat>false</chat>"
				//+ "				<chatHost>true</chatHost> "
				//+ "				<chatPresenter>true</chatPresenter> "
				//+ "				<chatAllAttendees>false</chatAllAttendees> "
				+ "				<poll>false</poll>"
				+ "				<audioVideo>true</audioVideo>"
				+ "				<fileShare>false</fileShare>"
				+ "				<presentation>false</presentation>"
				+ "				<applicationShare>false</applicationShare>"
				+ "				<desktopShare>false</desktopShare>"
				+ "				<webTour>false</webTour>"
				+ "				<trainingSessionRecord>false</trainingSessionRecord>"
				+ "				<annotation>false</annotation>"
				+ "				<importDocument>false</importDocument>"
				+ "				<saveDocument>false</saveDocument>"
				+ "				<printDocument>false</printDocument>"
				+ "				<pointer>false</pointer>"
				+ "				<switchPage>false</switchPage>"
				+ "				<fullScreen>false</fullScreen>"
				+ "				<thumbnail>false</thumbnail>"
				+ "				<zoom>false</zoom>"
				+ "				<copyPage>false</copyPage>"
				+ "				<rcAppShare>false</rcAppShare>"
				+ "				<rcDesktopShare>false</rcDesktopShare>"
				+ "				<rcWebTour>false</rcWebTour>"
				+ "				<attendeeRecordTrainingSession>false"
				+ "				</attendeeRecordTrainingSession>"
				+ "				<voip>true</voip>"
				+ "				<faxIntoTrainingSession>false</faxIntoTrainingSession>"
				+ "				<autoDeleteAfterMeetingEnd>true</autoDeleteAfterMeetingEnd>"
				+ "				<muteOnEntry>true</muteOnEntry>"
				+ "				<multiVideo>false</multiVideo>"
				+ "			</enableOptions>"
				+ "			<telephony>"
				+ "				<telephonySupport>NONE</telephonySupport>"
				+ "				<numPhoneLines>4</numPhoneLines>"
				+ "				<extTelephonyURL>String</extTelephonyURL>"
				+ "				<extTelephonyDescription>String</extTelephonyDescription>"
				+ "				<enableTSP>false</enableTSP>"
				+ "				<tspAccountIndex>1</tspAccountIndex>"
				//+ "				<HQVideo>true</HQVideo>"
				+ "			</telephony>"

				+ "		</bodyContent>"
				+ "	</body>"			
				
				/*+ "	 <body>"
		        + "	<bodyContent  xsi:type=\"java:com.webex.service.binding.meeting.GethosturlMeeting\">"
		        + "	    <sessionKey>570095419</sessionKey>"
		        + "	</bodyContent>"
		        + "	 </body>"	 */   
		    
				+ "</serv:message>";

		System.out.println("reqXML = "+reqXML);

		System.out.println("XML Request POSTed to " + xmlServerURL + "\n");   
		// send request
		/* PrintWriter out = new PrintWriter(urlConnectionXMLServer.getOutputStream());

        out.println(reqXML);
        out.close();*/

		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		System.out.println(reqXML+"\n");

		// read response
		BufferedReader in = new BufferedReader(new
				InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		System.out.println("\nXML Response\n");        
		System.out.println(respXML);
	}

	private static void disableSslVerification() {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
	            }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
	            }
	        }
	        };

	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };

	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	          
	    } catch (KeyManagementException e) {
	          
	    }
	}
	
//		Added for checking valid meeting key
		public void checkValidSession (SessionDayTimeAcadsBean session, String hostId, String meetingKey, String hostPass) throws Exception {
		
		disableSslVerification();
		
		System.out.println("Site : "+site);
		String xmlServerURL = site + xmlURL;
		
		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);
				
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);
		
		String reqXML = null;
				
		 reqXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> "
				+ "		<header>"
				+ "			<securityContext>"
				+ "				<webExID> "+hostId+ "</webExID> "
				+ "				<password> "+hostPass+ "</password>"
				+ "				<siteID>"+siteID+"</siteID>"
				+ "				<partnerID>"+partnerID+"</partnerID>"
				+ "			</securityContext>"
				+ "		</header>"
				+ "<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.GetTrainingSession\"> "
				+ "			<sessionKey>"+meetingKey+"</sessionKey>"
				+ "		</bodyContent>"
				+ "</body>"
				+ "</serv:message>";
				
		
		System.out.println("reqXML for checking session : " +reqXML);
		System.out.println("XML Request checking session Posted to " + xmlServerURL + "\n");   
		
		// send request
				OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
				out.write(reqXML);
				out.close();
				System.out.println("OutputStream ended");
				
		// read response
				BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
				String line;
				String respXML = "";
				while ((line = in.readLine()) != null) {
					respXML += line;
				}
				in.close();
				
		// output response
				respXML = URLDecoder.decode(respXML,"UTF-8");  
				System.out.println("XML Response");        
				System.out.println(respXML);
				
				parseCheckSession (session, respXML);
	}

	
	public void parseCheckSession(SessionDayTimeAcadsBean sessionBean, String respXML) throws Exception {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource file = new InputSource();
		file.setCharacterStream(new StringReader(respXML));
		Document document = builder.parse(file);
		NodeList nodes = document.getElementsByTagName("serv:result");
		NodeList nodes2 = document.getElementsByTagName("sess:startDate");
		String result = nodes.item(0).getTextContent();
		String result2 = nodes2.item(0).getTextContent();
		System.out.println("result in check session :" +result);
		
		String DateTime = sessionBean.getDate()+" "+sessionBean.getStartTime();
		System.out.println(DateTime);
		if (DateTime.equals(result2)) {
			System.out.println("Date Time match");
		}
		else
			System.out.println("Date time not match");
		
		if ("SUCCESS".equalsIgnoreCase(result) && DateTime.equals(result2)) {
			sessionBean.setErrorRecord(false);
			nodes = document.getElementsByTagName("sess:schedule");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if(node.getNodeType()==node.ELEMENT_NODE)
				{
					System.out.println(node.getNodeName() + "-" + node.getTextContent());
				}
			}
			
		}else{
			sessionBean.setErrorRecord(true);
			nodes = document.getElementsByTagName("serv:reason");
			String failureReason = nodes.item(0).getTextContent();
			System.out.println("failureReason for checking session :"+failureReason);
			sessionBean.setErrorMessage(failureReason);
		}
	}
	
	public void deleteTrainingSessionKey(SessionDayTimeAcadsBean session, String meetingKey, String hostId, String hostPass) throws Exception{
		disableSslVerification();

		String xmlServerURL = site + xmlURL;

		System.out.println("Site = "+site);
		URL urlXMLServer = new URL(xmlServerURL);

		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		System.out.println("IN deleteTrainingSessionKey");
		System.out.println("sessionID : "+session.getId());
		System.out.println("Meetingkey : "+meetingKey);
		System.out.println("hostId : "+hostId);
		System.out.println("host password : " +hostPass);
		
		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + hostId + "</webExID>"
				+ "			<password>" + hostPass + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.DelTrainingSession\">"
				+ "				<sessionKey>"+meetingKey+"</sessionKey>"
				+ "		</bodyContent>"
				+ "	</body>"			
				+ "</serv:message>";


		// send request
		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		// read response
		BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		System.out.println("\nDelete Session : XML Response\n");        
		System.out.println(respXML);
		
		parseDeleteSessionResponse(session, respXML);
		
	}
	
	public void scheduleTrainingSessionKey(SessionDayTimeAcadsBean session, String hostId, String hostPass) throws Exception{
		disableSslVerification();

		session.setMeetingPwd(meetingPassword);

		String xmlServerURL = site + xmlURL;
		System.out.println("Host ID "+hostId+" host Password : "+ hostPass );
		System.out.println("Site = "+site);
		// connect to XML server
		URL urlXMLServer = new URL(xmlServerURL);

		// URLConnection supports HTTPS protocol only with JDK 1.4+
		URLConnection urlConnectionXMLServer = urlXMLServer.openConnection();
		urlConnectionXMLServer.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
		urlConnectionXMLServer.setDoOutput(true);

		String reqXML =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
				+ "<serv:message xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:serv=\"http://www.webex.com/schemas/2002/06/service\""
				+ "		xsi:schemaLocation=\"http://www.webex.com/schemas/2002/06/service\">"
				+ "	<header>"
				+ "		<securityContext>"
				+ "			<webExID>" + hostId + "</webExID>"
				+ "			<password>" + hostPass + "</password>"
				+ "			<siteID>" + siteID + "</siteID>"
				+ "			<partnerID>" + partnerID + "</partnerID>"
				//+ "			<email>johnsmith@xyz.com</email>"
				+ "		</securityContext>"
				+ "	</header>"
				+ "	<body>"
				+ "		<bodyContent xsi:type=\"java:com.webex.service.binding.training.CreateTrainingSession\">"
				+ "			<accessControl>"
				+ "				<listing>UNLISTED</listing>"
				+ "				<sessionPassword>"+meetingPassword+"</sessionPassword>"
				+ "			</accessControl>"
				+ "			<schedule>"
				+ "				<startDate>"+ session.getDate() +" " + session.getStartTime()+"</startDate>"
				+ "				<timeZone>GMT+05:30, India (Bombay)</timeZone>"
				+ "				<duration>120</duration>"
				//+ "				<timeZoneID>46</timeZoneID>"
				+ "				<openTime>45</openTime>"
				//+ "				<joinTeleconfBeforeHost>true</joinTeleconfBeforeHost>" Not applicable for VOIP I guess
				+ "				<entryExitTone>NOTONE</entryExitTone>"
				+ "			</schedule>"
				+ "			<metaData>"
				+ "				<confName>"+ session.getSubject().replaceAll("&", "and") +"-"+ session.getSessionName().replaceAll("&", "and")+"</confName>"
				//+ "				<agenda>agenda 1</agenda>"
				//+ "				<description>description</description>"
				+ "				<greeting>Welcome Participants!</greeting>"
				//+ "				<location>location</location>"
				//+ "				<invitation>invitation</invitation>"
				+ "			</metaData>"
				+ "			<enableOptions>"
				+ "				<attendeeList>false</attendeeList>"
				+ "				<javaClient>false</javaClient>"
				+ "				<nativeClient>true</nativeClient>"
				+ "				<chat>true</chat>"
				//+ "				<chatHost>true</chatHost> "
				//+ "				<chatPresenter>true</chatPresenter> "
				//+ "				<chatAllAttendees>false</chatAllAttendees> "
				+ "				<poll>false</poll>"
				+ "				<audioVideo>true</audioVideo>"
				+ "				<fileShare>false</fileShare>"
				+ "				<presentation>false</presentation>"
				+ "				<applicationShare>false</applicationShare>"
				+ "				<desktopShare>false</desktopShare>"
				+ "				<webTour>false</webTour>"
				+ "				<trainingSessionRecord>false</trainingSessionRecord>"
				+ "				<annotation>false</annotation>"
				+ "				<importDocument>false</importDocument>"
				+ "				<saveDocument>true</saveDocument>"
				+ "				<printDocument>false</printDocument>"
				+ "				<pointer>false</pointer>"
				+ "				<switchPage>false</switchPage>"
				+ "				<fullScreen>false</fullScreen>"
				+ "				<thumbnail>false</thumbnail>"
				+ "				<zoom>false</zoom>"
				+ "				<copyPage>false</copyPage>"
				+ "				<rcAppShare>false</rcAppShare>"
				+ "				<rcDesktopShare>false</rcDesktopShare>"
				+ "				<rcWebTour>false</rcWebTour>"
				+ "				<attendeeRecordTrainingSession>false"
				+ "				</attendeeRecordTrainingSession>"
				+ "				<voip>true</voip>"
				+ "				<faxIntoTrainingSession>false</faxIntoTrainingSession>"
				+ "				<autoDeleteAfterMeetingEnd>false</autoDeleteAfterMeetingEnd>"
				+ "				<muteOnEntry>true</muteOnEntry>"
				+ "				<multiVideo>false</multiVideo>"
				+ "			</enableOptions>"
				+ "			<telephony>"
//Commented by Vikas+ "<telephonySupport>NONE</telephonySupport>"
				+ "				<numPhoneLines>4</numPhoneLines>"
				+ "				<extTelephonyURL>String</extTelephonyURL>"
				+ "				<extTelephonyDescription>String</extTelephonyDescription>"
				//Commented by Vikas+ "				<enableTSP>false</enableTSP>"
				//Commented by Vikas+ "				<tspAccountIndex>1</tspAccountIndex>"
				//+ "				<HQVideo>true</HQVideo>"
				+ "			</telephony>"

				+ "		</bodyContent>"
				+ "	</body>"			

		    
				+ "</serv:message>";

		System.out.println("reqXML = "+reqXML);

		// send request
		OutputStreamWriter out = new OutputStreamWriter(urlConnectionXMLServer.getOutputStream());
		out.write(reqXML);
		out.close();

		// read response
		BufferedReader in = new BufferedReader(new	InputStreamReader(urlConnectionXMLServer.getInputStream()));
		String line;
		String respXML = "";
		while ((line = in.readLine()) != null) {
			respXML += line;
		}
		in.close();

		// output response
		respXML = URLDecoder.decode(respXML,"UTF-8");  
		//System.out.println("\nXML Response\n");        
		System.out.println(respXML);
		
		parseCreateSessionResponse(session, respXML);
		
	}
}
