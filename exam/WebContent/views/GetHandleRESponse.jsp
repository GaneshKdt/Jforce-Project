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
Name of the Program : Hosted UMI Integration Pages - Response Page
Page Description    : Receives response from Payment Gateway and handles the same
Response parameters : Result,Ref,Transaction id, Payment id,Auth Code, Track ID,
                      Amount,avr(optional), UDF1-UDF5,Error 
Hashing Parameters	: TranportalID,TrackID,Amount,Result, Payment id,Ref,Auth Code,Transaction id
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
***************************************************************** 

Disclaimer:- Important Note in Pages

- Transaction data should only be accepted once from a browser at the point of input, and then kept 
in a way that does not allow others to modify it (example server session, database  etc.)

- Any transaction information displayed to a customer, such as amount, should be passed only as 
display information and the actual transactional data should be retrieved from the secure source 
last thing at the point of processing the transaction.

- Any information passed through the customer's browser can potentially be modified/edited/changed
/deleted by the customer, or even by third parties to fraudulently alter the transaction data/
information. Therefore, all transaction information should not be passed through the browser to 
Payment Gateway in a way that could potentially be modified (example hidden form fields). 
*/

		
/* BELOW ARE LIST OF PARAMETERS THAT WILL BE RECEIVED BY MERCHANT FROM PAYMENT GATEWAY */
/*Variable Declaration*/	
String ResPaymentId,ResResult,ResErrorText,ResPosdate,ResTranId,ResAuth,ResAmount,ResErrorNo,ResTrackID,ResRef,ResAVR,Resudf1,Resudf2,Resudf3,Resudf4,Resudf5;

/* Capture the IP Address from where the response has been received */
String strResponseIPAdd = request.getRemoteAddr();
/*
to get the IP Address in case of proxy server used
String ipAddress  = request.getHeader("X-FORWARDED-FOR");
*/
/* Check whether the IP Address from where response is received is PG IP  */
if (!strResponseIPAdd.equals("221.134.101.174") && !strResponseIPAdd.equals("221.134.101.169") && !strResponseIPAdd.equals("198.64.129.10") && !strResponseIPAdd.equals("198.64.133.213")) 
{
		//IF ip address recevied is not Payment Gateway IP Address
		/*
		IMPORTANT NOTE - IF IP ADDRESS MISMATCH, ME LOGS DETAILS IN LOGS,
		UPDATES MERCHANT DATABASE WITH PAYMENT FAILURE, REDIRECTS CUSTOMER 
		ON FAILURE PAGE WITH RESPECTIVE MESSAGE
		*/
		
		out.println("REDIRECT=https://studentzone-ngasce.nmims.edu:8090/exam/views/StatusTRAN.jsp?ResError=--IP MISSMATCH-- Response IP Address is: "+strResponseIPAdd);
} 
else 
{
//=======================================================================================================================	
		ResErrorText= request.getParameter("ErrorText");	//Error Text/message
		ResPaymentId = request.getParameter("paymentid");	//Payment Id
		ResTrackID = request.getParameter("trackid");		//Merchant Track ID
		ResErrorNo= request.getParameter("Error");			//Error Number					
		ResResult = request.getParameter("result");			//Transaction Result
		ResPosdate = request.getParameter("postdate");		//Postdate					
		ResTranId = request.getParameter("tranid");			//Transaction ID
		ResAuth = request.getParameter("auth");				//Auth Code		
		ResAVR = request.getParameter("avr");				//TRANSACTION avr
		ResRef = request.getParameter("ref");				//Reference Number also called Seq Number					
		ResAmount= request.getParameter("amt");				//Transaction Amount
		Resudf1= request.getParameter("udf1");				//UDF1
		Resudf2= request.getParameter("udf2");				//UDF2
		Resudf3= request.getParameter("udf3");				//UDF3
		Resudf4= request.getParameter("udf4");				//UDF4
		Resudf5= request.getParameter("udf5");				//UDF5
		
//LIST OF PARAMETERS RECEIVED BY MERCHANT FROM PAYMENT GATEWAY ENDS HERE 

//=========================================================================================================================	
              
/*
LIST OF PARAMETERS RECEIVED BY MERCHANT FROM PAYMENT GATEWAY ENDS HERE 
*/              
/* 
First check, if error number is NOT present,then go for Hashing using required parameters 
*/
/* 
NOTE - MERCHANT MUST LOG THE RESPONSE RECEIVED IN LOGS AS PER BEST PRACTICE. Since the
logging mechanism is merchant driven, the sample code for same is not provided in this
pages
*/

	if (ResErrorNo == null) 
	{
				
		//******************HASHING CODE LOGIC START************************************
			
		/*
		IMP NOTE: For Hashing below listed parameters have been used. In case merchant develops 
		his/her own pages, merchant to 	make note of these parameters to ensure hashing 
		logic remains same.
		Tranportal ID, TrackID, Amount, Result, Payment ID, Reference Number, Auth Code, Transaction ID 

		If any Hashing parameters is null of blank then merchant need to exclude those parameters 
		from hashing
		*/
		
		/*
		USE Tranportal ID FIELD as one parameter for hashing.
		Tranportal ID is a sensitive parameter, Merchant can store the Tranportal ID field in 
		database as well in page as config value. We recommend merchant storing this parameter 
		in database and then calling from database.
		*/
		String strHashTraportalID="9000640"; 
		
		String strhashstring="";//Declaration of Hashing String 
		
		strhashstring = strHashTraportalID.trim();//Padding Tranportal ID Value

		/*
		Below code creates the Hashing String also it will check NULL and Blank parameters and 
		exclude from the hashing string
		*/
		
		if (ResTrackID != null && !ResTrackID.trim().equals(""))
		strhashstring= strhashstring + ResTrackID.trim();//Padding TrackID Value,Mercahnt need to take this filed value  from his Secure channel such as DATABASE.
		if (ResAmount != null && !ResAmount.trim().equals(""))
		strhashstring= strhashstring + ResAmount.trim();//Padding Amount Value,Mercahnt need to take this field value  from his Secure channel such as DATABASE,Value should be in same format which he pass in initial request for eg. if he passed amount as 1.00 then for hashing also mercahnt need to use 1.00
		if (ResResult != null && !ResResult.trim().equals(""))
		strhashstring= strhashstring + ResResult.trim();//Padding Result Value
		if (ResPaymentId != null && !ResPaymentId.trim().equals(""))
		strhashstring= strhashstring + ResPaymentId.trim();//Padding PaymentId Value
		if (ResRef != null && !ResRef.trim().equals(""))
		strhashstring= strhashstring + ResRef.trim();//Padding Ref Value
		if (ResAuth != null && !ResAuth.trim().equals(""))
		strhashstring= strhashstring + ResAuth.trim();//Padding Auth Value
		if (ResTranId != null && !ResTranId.trim().equals(""))
		strhashstring= strhashstring + ResTranId.trim();//Padding TranId Value

		//Use GetSHA256 Function which is defined below for Hashing ,It will return Hashed valued of above strin
		String hashvalue=GetSHA256(strhashstring);	

		//******************HASHING CODE LOGIC END************************************
		
		/*
		Now Match/Compare that calculated Hashed value with PG Response UDF5 field value received,
		from PG and if it is equal the HASHING is Successful, Merchant can redirect 
		User to Successful Result page.
		*/
		if(hashvalue.equals(Resudf5))
		{
			/*
			IMPORTANT NOTE - MERCHANT SHOULD UPDATE TRANACTION PAYMENT STATUS IN MERCHANT DATABASE 
			AT THIS POSITION AND THEN REDIRECT CUSTOMER ON RESULT PAGE
			*/

			/* 
			NOTE - MERCHANT MUST LOG THE RESPONSE RECEIVED IN LOGS AS PER BEST PRACTICE
			/*
			
			/*
			IMPORTANT NOTE - We highly recommend merchant to validate the
			Track ID and Amount with his database before updating the status. 
			Generally this can be done using single update query.
			*/

			/* !!IMPORTANT INFORMATION!!
			During redirection, Merchant can pass the values as per his/her requirement.
			In these pages we have passed, Track Id, Amount, Status to Finale Page. 
			Merchant can use his values.
			
			We do not recommend any processing on the final result page to customer 
			based on the values passed by merchant from this page to end result page.
			*/

			//Hashing Response Successful
			out.println("REDIRECT=https://studentzone-ngasce.nmims.edu:8090/exam/views/StatusTRAN.jsp?ResResult="+ResResult+"&ResTrackId="+ResTrackID+"&ResPaymentId="+ResPaymentId+"&ResRef="+ResRef+"&ResTranId="+ResTranId+"&ResAmount="+ResAmount+"&ResError="+ResErrorText+"Hashing Response Successful");	
		}
		else
		{
			/*
			IMPORTANT NOTE - MERCHANT SHOULD UPDATE TRANACTION PAYMENT STATUS IN MERCHANT DATABASE 
			AT THIS POSITION WE RECOMMEND UPDATE AS "HASHING MIS MATCH" AND TRASNACTION SHOULD 
			BE TREATED AS FAILED. MERCHANT SHOULD NOT PROVIDE GOODS / SERVICES TO CUSTOMER IN 
			THIS CASE
			*/
			
			/* 
			NOTE - MERCHANT MUST LOG THE RESPONSE RECEIVED IN LOGS AS PER BEST PRACTICE 
			*/
			
			/* 
			Udf5 field values not matched with calculated hashed valued then show appropriate message to
			Merchant for E.g. RESPONSE MISMATCH
			*/

			//Hashing Response NOT Successful
			out.println("REDIRECT=https://studentzone-ngasce.nmims.edu:8090/exam/views/StatusTRAN.jsp?ResError=Hashing Response Missmatch");
		}				
	} 
	else 
	{
	/*
	ERROR IN TRANSACTION PROCESSING
	IMPORTANT NOTE - MERCHANT SHOULD UPDATE 
	TRANACTION PAYMENT STATUS IN MERCHANT DATABASE AT THIS POSITION 
	AND THEN REDIRECT CUSTOMER ON RESULT / ERROR PAGE
	*/
	out.println("REDIRECT=https://studentzone-ngasce.nmims.edu:8090/exam/views/StatusTRAN.jsp?ResResult="+ResResult+"&ResTrackId="+ResTrackID+"&ResPaymentId="+ResPaymentId+"&ResRef="+ResRef+"&ResTranId="+ResTranId+"&ResAmount="+ResAmount+"&ResError="+ResErrorText);	
	}
}
%>
<%!
//=======This is GetTextBetweenTags function which return the value between two XML tags or two string =====
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
