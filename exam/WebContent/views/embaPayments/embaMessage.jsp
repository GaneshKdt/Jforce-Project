<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="no-store, no-cache, must-revalidate">
<META HTTP-EQUIV="PRAGMA" CONTENT="no-store, no-cache, must-revalidate">
<body>
<% 	if(request.getParameter("message") != null){  %>
		<%= request.getParameter("message") %>
<%	} 	%>
<% 	if(request.getParameter("errorMessage") != null){  %>
		<%= request.getParameter("errorMessage") %>
<%	} 	%>
</body>
</html>
