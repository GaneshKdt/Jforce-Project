<%@page import="com.nmims.beans.MBAExamBookingRequest"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="no-store, no-cache, must-revalidate">
<META HTTP-EQUIV="PRAGMA" CONTENT="no-store, no-cache, must-revalidate">
<body onload="document.paymentform.submit()">

	<form id = "paymentform" name="paymentform" action="${bookingRequest.transactionUrl}" method="post">
	<p>Please wait while your payment is being processed...</p>
	
	<div style="display: none;">
		<%
			MBAExamBookingRequest br = (MBAExamBookingRequest) request.getAttribute("bookingRequest");
			
			//Sort the HashMap
				    Map requestFields = new TreeMap(br.getFormParameters());
				   	for (Iterator i = requestFields.keySet().iterator(); i.hasNext(); ) {
				           
				        String key = (String)i.next();
				        String value = (String)requestFields.get(key);
		%>
		    	<input name="<%=key%>" value="<%=value%>"/><hr/>
		<% 
			}
		%>
		<button type="submit"> Submit </button>
	</div>
	
 </form>
</body>
</html>
