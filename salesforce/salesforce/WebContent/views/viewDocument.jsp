<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String url=(String) request.getAttribute("url"); 
	
	
%>
<html style="height: 100%;">

<head><meta name="viewport" content="width=device-width, minimum-scale=0.1">
<title>Document</title>
<style>
#myFrame {
  margin:0 auto 500px;
  
 	
}
</style>
</head>
<body style="margin: 0px; background: #0e0e0e; height: 100%;">
<div id="myFrame">
<iframe   height="100%" width="100%" frameborder="0" allowTransparency="true" scrolling="auto" src='<%=url%>'>
</iframe>
</div>

</body>
</html>