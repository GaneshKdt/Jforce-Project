<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.security.MessageDigest,
                 java.math.*,
				 java.util.*,
				 java.io.*"%>




<%
	
	HashMap testMap = new HashMap();
    Enumeration en = request.getParameterNames();
	String returnURL = (String)request.getAttribute("returnURL");
	System.out.println("returnURL "+returnURL);
	/* while(en.hasMoreElements()) {
        String fieldName = (String) en.nextElement();
        String fieldValue = request.getParameter(fieldName);
        if ((fieldValue != null) && (fieldValue.length() > 0)) {
        	System.out.println("Putting in map" + fieldName + " "+fieldValue);
            testMap.put(fieldName, fieldValue);
        }
    }

	//Sort the HashMap
    Map requestFields = new TreeMap(testMap); */
    
    
    
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<META HTTP-EQUIV="CACHE-CONTROL"
	CONTENT="no-store, no-cache, must-revalidate">
<META HTTP-EQUIV="PRAGMA" CONTENT="no-store, no-cache, must-revalidate">
<body onLoad="document.order.submit()">
	<form name="order" action="<%=returnURL%>" method="post">

		<input type="hidden" name="Amount"
			value="<%=(String)request.getAttribute("Amount")%>" /> <input
			type="hidden" name="Description"
			value="<%=(String)request.getAttribute("Description")%>" /> <input
			type="hidden" name="MerchantRefNo"
			value="<%=(String)request.getAttribute("MerchantRefNo")%>" /> <input
			type="hidden" name="PaymentID"
			value="<%=(String)request.getAttribute("PaymentID")%>" /> <input
			type="hidden" name="PaymentMethod"
			value="<%=(String)request.getAttribute("PaymentMethod")%>" /> <input
			type="hidden" name="RequestId"
			value="<%=(String)request.getAttribute("RequestId")%>" /> <input
			type="hidden" name="ResponseCode"
			value="<%=(String)request.getAttribute("ResponseCode")%>" /> <input
			type="hidden" name="TransactionID"
			value="<%=(String)request.getAttribute("TransactionID")%>" /> <input
			type="hidden" name="apiRequestId"
			value="<%=(String)request.getAttribute("apiRequestId")%>" />

		<%
	String error = (String)request.getAttribute("Error");
	if(error != null){
	%>
		<input type="hidden" name="Error"
			value="<%=(String)request.getAttribute("Error")%>" />
		<%} %>




		<h3>Please wait while your payment is being processed...</h3>
		<%-- <%	
	

        for (Iterator i = requestFields.keySet().iterator(); i.hasNext(); ) {
            
            String key = (String)i.next();
            String value = (String)requestFields.get(key);
			
			
			System.out.println("Key = "+key + " Value = " + value);
%>
        	<input type="hidden" name="<%=key%>" value="<%=value%>"/><br>
<%             
    	}
%> --%>

	</form>
</body>
</html>
