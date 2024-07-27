<HTML>
<HEAD>
<TITLE>Sample FSS-PG Page</TITLE>
<BODY>
<%@ page language="java" session="true"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import ="java.util.HashMap" %>
<%@ page import="java.util.regex.Pattern" %> 
<%@ page import="javax.servlet.*,java.text.*" %>
<%@ page import ="java.io.BufferedInputStream" %>
<%@ page import ="java.io.BufferedReader" %>
<%@ page import ="java.io.DataOutputStream" %>
<%@ page import ="java.io.File" %>
<%@ page import ="java.io.FileInputStream" %>
<%@ page import ="java.io.FileOutputStream" %>
<%@ page import ="java.io.InputStream" %>
<%@ page import ="java.io.InputStreamReader" %>
<%@ page import ="java.io.PrintStream" %>
<%@ page import ="java.net.URL" %>
<%@ page import ="java.net.URLConnection" %>
<%@ page import ="javax.net.ssl.HttpsURLConnection" %>
<%@ page import ="java.net.HttpURLConnection" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.io.*,java.util.*" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="sun.misc.BASE64Encoder" %>
<%

/*
******************************************************************
			* COMPANY    - FSS Pvt. Ltd.
******************************************************************
Name of the Program : Hosted UMI Pages with Hashing
Page Description    : Allows Merchant to connect Payment Gateway and send request
Request parameters  : TranportalID,TranportalPassword,Action,Amount,Currency,Merchant 
                      Response/Error URL & TrackID,Language,UDF1-UDF
Hashing Parameters	: TranportalID,TrackID,Amount,Currency,Action
Response parameters : Payment Id, Pay Page URL, Error
Values from Session : No 
Values to Session   : No
Created by          : FSS Payment Gateway Team
Created On          : 12-03-2013
Version             : Version 4.1

The set of pages are developed and tested using below set of hardware and software only. 
In case of any issues noticed by merchant during integration, merchant can contact respective bank 
for technical assistance

NOTE - 
This pages are developed and tested on below platform
Java Version     - Sun JDK 1.6 and above
Application      - Tomcat 6.0
Operating System - Windows Server 2003
****************************************************************
*/

/*
IMPORTANT INFORMATION
This document is provided by Financial Software and System Pvt Ltd on the basis 
that you will treat it as private and confidential.
Data used in examples and sample data files are intended to be fictional and any 
resemblance to real persons or entities is entirely coincidental.
This example assumes that a form has been sent to this example with the required 
fields. The example then processes the command and displays the receipt or error 
to a HTML page in the users web browser.
*/

/*
Before merchant uses this page in his environment, merchant to ensure
below changes are made in the pages - 
1. Merchant sets Tranportal ID / Password provided by Bank in respective 
places
2. Merchant collects the transaction track Id and amount from his database
3. Merchant changes the Response URL / Error URL as per his website
*/

/*  
sign "&" is mandatory to mention with in the end of passed value, in below section 
this to make the string  Merchant can use their own logic of creating the string 
with required inputs, below is just a basic method on how to create a request 
string and pass the values to Payment Gateway 
*/	

/*
Getting Transaction Amount and Merchant TrackID from Initial HTML page
Since this page for demonstration, values from HTML page are directly
taken from browser and used for transaction processing. 
Merchants SHOULD NOT follow this practice in production environment.
*/
	/*
	The merchant developer should ensure the track id passed here is from merchant
	database
	*/
	//String TranTrackid=request.getParameter("MTrackid");
//String TranTrackid=request.getParameter("MTrackid");
		
	
	String TranTrackid=System.currentTimeMillis()+"";
	/*
	The merchant developer should ensure the transaction amount passed here is 
	from merchant database
	*/
	//String TranAmount=request.getParameter("MAmount");
	String TranAmount="1";

/* 
to pass Tranportal ID provided by the bank to merchant. Tranportal ID is sensitive 
information of merchant from the bank, merchant MUST ensure that Tranportal ID is 
never passed to customer browser by any means. Merchant MUST ensure that Tranportal
ID is stored in secure environment & securely at merchant end. Tranportal Id is 
referred as id. Tranportal ID for test and production will be different, please 
contact bank for test and production Tranportal ID
*/
	//String ReqTranportalId = "id="+"70009027"+"&";
String ReqTranportalId = "id="+"9000640"+"&";
	
/* 
to pass Tranportal password provided by the bank to merchant. Tranportal password 
is sensitive information of merchant from the bank, merchant MUST ensure that 
Tranportal password is never passed to customer browser by any means. Merchant 
MUST ensure that Tranportal password is stored in secure environment & securely 
at merchant end. Tranportal password is referred as password. Tranportal password 
for test and production will be different, please contact bank for test and 
production Tranportal password 
*/
	//String ReqTranportalPassword = "password="+"70009027"+"&";
String ReqTranportalPassword = "password="+"password1"+"&";
	
/*Getting Transaction Amount and Merchant TrackID from Initial HTML page
Since this sample page for demonstration, values from HTML page are directly
taken from browser and used for transaction processing. Merchants SHOULD NOT
follow this practice in production environment. */
	
	String ReqAmount = "amt="+TranAmount+"&";

/* Track Id passed here should be from merchant backend system like database and not from customer browser*/
	String ReqTrackId="trackid="+TranTrackid+"&";

/* 
Currency code of the transaction. By default INR i.e. 356 is configured. 
If merchant wishes to do multiple currency code transaction, merchant 
needs to check with bank team on the available currency code 
*/
	String ReqCurrency = "currencycode="+"356"+"&";
	
/* Transaction language, THIS MUST BE ALWAYS USA. */
	String ReqLangid = "langid="+"USA"+"&";
	
/* 
Action Code of the transaction, this refers to type of transaction. 
Action Code 1 stands of Purchase transaction and 
action code 4 stands for Authorization (pre-auth). 
Merchant should confirm from Bank action code enabled for the merchant by the 
bank
*/
	String ReqAction = "action="+"1"+"&";
	
/* 
Response URL where Payment gateway will send response once transaction 
processing is completed Merchant MUST ensure that below points in Response URL
1- Response URL must start with http://
2- the Response URL SHOULD NOT have any additional parameters or query string 
*/
	//String ReqResponseUrl = "responseURL="+"http://www.merchantdemo.com/GetHandleRESponse.jsp"+"&";
	String ReqResponseUrl = "responseURL="+"https://studentzone-ngasce.nmims.edu:8090/exam/getExamFeePaymentResponse"+"&";
	//String ReqResponseUrl = "responseURL="+"http://10.177.165.161:8080/exam/getExamFeePaymentResponse"+"&";
	

/* 
Error URL where Payment gateway will send response in case any issues while 
processing the transaction. 
Merchant MUST ensure that below points in ErrorURL 
1- error url must start with http://
2- the error url SHOULD NOT have any additional parameters or query string
*/ 
	//String ReqErrorUrl = "errorURL="+"http://www.merchantdemo.com/StatusTRAN.jsp"+"&";
String ReqErrorUrl = "errorURL="+"https://studentzone-ngasce.nmims.edu:8090/exam/examFeeFinalPaymentResponse"+"&";
//String ReqErrorUrl = "errorURL="+"http://10.177.165.161:8080/exam/examFeeFinalPaymentResponse"+"&";

/* 
User Defined Fields as per Merchant or bank requirement. Merchant MUST 
ensure merchant merchant is not passing junk values OR CRLF in any of the UDF. 
In below sample UDF values are not utilized 
*/

	String ReqUdf1 = "udf1="+"Test1"+"&";	// UDF1 values 
	String ReqUdf2 = "udf2="+"Test2"+"&";	// UDF2 values 
	String ReqUdf3 = "udf3="+"Test3"+"&";	// UDF3 values 
	String ReqUdf4 = "udf4="+"Test4"+"&";	// UDF4 values 

/*
NOTE -
ME should now do the validations on the amount value set like - 
a) Transaction Amount should not be blank and should be only numeric
b) Language should always be USA
c) Action Code should not be blank
d) UDF values should not have junk values and CRLF 
(line terminating parameters)Like--> [ !#$%^&*()+[]\\\';,{}|\":<>?~` ]
*/

//==============================HASHING LOGIC CODE START==============================================

/*Below are the fields/prametres which will use for hashing using (SHA256) hashing 
Algorithm,and need to pass same hashed valued in UDF5 filed only*/

	//String strhashTID="XXXXX";//USE Tranportal ID FIELD Value FOR HASHING 
	//String strhashTID="70009027";//USE Tranportal ID FIELD Value FOR HASHING
	String strhashTID="9000640";//USE Tranportal ID FIELD Value FOR HASHING	
	String strhashtrackid=TranTrackid;//USE Trackid FIELD Value FOR HASHING 
	String strhashamt=TranAmount;//USE Amount FIELD Value FOR HASHING 
	String strhashcurrency="356";//USE Currencycode FIELD Value FOR HASHING 
	String strhashaction="1";//USE Action code FIELD Value FOR HASHING 

	//Create a Hashing String to Hash
	String Strhashs=strhashTID.trim()+strhashtrackid.trim()+strhashamt.trim()+strhashcurrency.trim()+strhashaction.trim();

	/* 
	Use GetSHA256 Function which is defined below for Hashing ,
	It will return Hashed valued of above string
	*/
	String hashString=GetSHA256(Strhashs);

	String ReqUdf5 = "udf5="+hashString+"&";	// Passed Calculated Hashed Value in UDF5 Field 

//==============================HASHING LOGIC CODE END==============================================

/* Payment Id, Payment Page and ConnectionResponse variables are declared */
	String paymentId=null,paymentPage=null,TranResponse=null;  

/* Now merchant sets all the inputs in one string for passing to the Payment Gateway URL */		
try {
		String TranRequest=	ReqTranportalId+ReqTranportalPassword+ReqAction+ReqLangid+ReqCurrency+ReqAmount+ReqResponseUrl+ReqErrorUrl+ReqTrackId+ReqUdf1+ReqUdf2+ReqUdf3+ReqUdf4+ReqUdf5;
		
/* This is Payment Gateway Test URL where merchant sends request. This is test enviornment URL, 
production URL will be different and will be shared by Bank during production movement */
	//	URL url = new URL("https://securepgtest.fssnet.co.in/pgway/servlet/PaymentInitHTTPServlet");  
	URL url = new URL("https://securepgtest.fssnet.co.in/pgway/servlet/PaymentInitHTTPServlet");
/* 
Log the complete request in the log file for future reference
Now creating a connection and sending request
*/			
		Object obj;
		obj = (HttpsURLConnection)url.openConnection();	//create a SSL connection object server-to-server
		((URLConnection)obj).setDoInput(true);
		((URLConnection)obj).setDoOutput(true);
		((URLConnection)obj).setUseCaches(false);
		((URLConnection)obj).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if(TranRequest.length()>0)
		{
			// Here the HTTPS request URL is created
			DataOutputStream dataoutputstream = new DataOutputStream(((URLConnection)obj).getOutputStream());	
			dataoutputstream.writeBytes(TranRequest);	// here the request is sent to payment gateway
            dataoutputstream.flush(); 
            dataoutputstream.close(); //connection closed

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(((URLConnection)obj).getInputStream()));
			TranResponse = bufferedreader.readLine();	//Payment Gateway response is read
			String ErrorCheck;
			try
			{
			 ErrorCheck = GetTextBetweenTags(TranResponse, "!", "!-");//This line will find Error Keyword in TranResponse	
			}
			catch(Exception e)
			{
				ErrorCheck="";
			}

			if(!ErrorCheck.equals("ERROR"))//This block will check for Error in TranResponce
			{
			// If Payment Gateway response has Payment ID & Pay page URL		
			int index=TranResponse.indexOf(":");
			int size=TranResponse.length();
			// Merchant MUST map (update) the Payment ID received with the merchant Track Id in his database at this place.
			paymentId=TranResponse.substring(0, index); 
			paymentPage=TranResponse.substring(index+1, size);
			// here redirecting the customer browser from ME site to Payment Gateway Page with the Payment ID
			response.sendRedirect(response.encodeRedirectURL( paymentPage + "?PaymentID=" + paymentId ));	
			//out.println(hashString);	
			}
			else
			{
				// here redirecting the error page 
				response.sendRedirect("https://studentzone-ngasce.nmims.edu:8090/exam/examFeeFinalPaymentResponse?ResError="+TranResponse+"&ResTrackId="+TranTrackid+"&ResAmount="+TranAmount);
				//response.sendRedirect("http://10.177.165.161:8080/exam/examFeeFinalPaymentResponse?ResError="+TranResponse+"&ResTrackId="+TranTrackid+"&ResAmount="+TranAmount);
					
			}
		 } 
   
}
catch (Exception localException)
{
			// here redirecting the error page 
			response.sendRedirect("https://studentzone-ngasce.nmims.edu:8090/exam/examFeeFinalPaymentResponse?ResError="+localException.getMessage()+TranResponse+"&ResTrackId="+TranTrackid+"&ResAmount="+TranAmount);
			//response.sendRedirect("http://10.177.165.161:8080/exam/examFeeFinalPaymentResponse?ResError="+localException.getMessage()+TranResponse+"&ResTrackId="+TranTrackid+"&ResAmount="+TranAmount);
		
} 
	
/**<!--
 This is a sample demonstration page only ment for demonstration, this page should not be used in production
 Transaction data should only be accepted once from a browser at the point of input, and then kept in a way that does not allow others to modify it (example server session, database  etc.)
- Any transaction information displayed to a customer, such as amount, should be passed only as display information and the actual transactional data should be retrieved from the secure source last thing at the point of processing the transaction.
- Any information passed through the customer's browser can potentially be modified/edited/changed/deleted by the customer, or even by third parties to fraudulently alter the transaction data/information. Therefore, all transaction information should not be passed through the browser to Payment Gateway in a way that could potentially be modified (example hidden form fields). 
-->**/

%>


<%!
//This is GetTextBetweenTags function which return the value between two XML tags or two string 
   public String GetTextBetweenTags(String InputText,String Tag1,String Tag2)
   {
	String Result;	
	int index1 = InputText.indexOf(Tag1);
	int index2 = InputText.indexOf(Tag2);
	index1=index1+Tag1.length();	
	Result=InputText.substring(index1, index2);
	return Result;   
   }
//========HASHING FUNCTION GetSHA256 CODE STARTS HERE=========================
/* hashing function starts here.
Note - Standard SHA 256 algorithm has been used. This is generally driven by the JDK version 
installed. We request merchant to check the version at their end before testing.

In JDK 1.3 and lower the function getInstance is "SHA256"
*/
   public String GetSHA256(String str)
   {	
		StringBuffer strhash=new StringBuffer();
		try
		{
		//-------- Tampering code starts here -----
		String message = str;
		MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
		messagedigest.update(message.getBytes());
		byte digest[] = messagedigest.digest();
		strhash = new StringBuffer(digest.length*2);
		int length = digest.length;
		for (int n=0; n < length; n++)
		{
			int number = digest[n];
			if(number < 0)
			{			   
				number= number + 256;
			}
		   	//number = (number < 0) ? (number + 256) : number; // shift to positive range
			String str1="";
			if(Integer.toString(number,16).length()==1)
			{
					str1="0"+String.valueOf(Integer.toString(number,16));
			}
			else
			{
					str1=String.valueOf(Integer.toString(number,16));
			}
			strhash.append(str1);
		}
		}catch(Exception e)
		{
		} 
	  
	return strhash.toString(); 
   }
   //========HASHING FUNCTION GetSHA256 CODE ENDS HERE=========================
%>

</BODY>
</HTML>
