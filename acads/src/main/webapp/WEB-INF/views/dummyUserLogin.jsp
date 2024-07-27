<%@page import="sun.misc.BASE64Decoder"%>
<%@page import="sun.misc.BASE64Encoder"%>
<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<link rel="shortcut icon" href="/idpsp/favicon.ico" type="image/x-icon">
<link rel="icon" href="/idpsp/favicon.ico" type="image/x-icon">
    <title>Login Dummy User</title>
</head>
 <body  onload="document.frm1.submit();">  

	
	<c:choose>
	<c:when test='${fn:contains(userId,"@") or fn:contains(userId,".")}'>
		<form method="post" name="frm1" id="frm1" action="/studentportal/loginForLeads">        
	    
	    <input  name="userId" type="hidden" value="${userId}" />		
	         
	    <input style="display: none" type="submit" value="Submit" />                
	    </form>
	</c:when>
	
	<c:otherwise>
	    <form method="post" name="frm1" id="frm1" action="/studentportal/loginAs">        
	    
	    <input  name="userId" type="hidden" value="${userId}" />
		<input  name="password" type="hidden" value="ngasce@admin20" />  
	         
	    <input style="display: none" type="submit" value="Submit" />                
	    </form>
    </c:otherwise>
    </c:choose>

</body>
</html>
