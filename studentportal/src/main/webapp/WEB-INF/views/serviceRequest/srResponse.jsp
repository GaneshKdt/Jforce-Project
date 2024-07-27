<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Service Request response</title>
</head>
<body><br/><br/>
	<center>
		<c:choose>
			<c:when test="${ responseType == 'success' } ">
				<h1 style="color:green">${ response }</h1>
			</c:when> 
			<c:otherwise>
				<h1 style="color:red">${ response }</h1>
			</c:otherwise>
		</c:choose>
	</center>
</body>
</html>