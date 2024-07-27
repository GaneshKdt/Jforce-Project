<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta charset="ISO-8859-1">
<title>Response</title>
</head>

<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/bootstrap.min.css" rel="stylesheet">
<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/font-awesome.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/fonts.css">
<link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/materialize.css">
    
<style>

body {
    position: relative;
    font-size: 16px;
    font-family: "Open Sans";
    font-weight: normal;
    background: #ece9e7;
    padding: 0 !important;
}

.backgroud{
	background-color: gray;
}

.messageContainer{
	margin: auto;
	width: 20%;
	height: 50%;
	background: white;
	box-shadow: 1px 1px 8px 2px lightgrey;
	border: 2px;
	border-radius: 4px;  
	padding: 1.2rem;
	margin-top:15%;
}

.success{
	color: #fff;
    background-color: #2169ff;
    border-color: #2169ff;
	box-shadow: 1px 1px 8px 2px lightgrey;
	margin-bottom: 10px;
}

.success:hover{
	color: #fff;
	cursor: pointer;
}

.error{
	color: #fff;
    background-color: #ff3b3b;
    border-color: #ff3b3b;
	box-shadow: 1px 1px 8px 2px lightgrey;
	margin-bottom: 10px;
}

.error:hover{
	color: #fff;
}

</style>

<body>
	<c:choose>
		<c:when test="${ success == true }">
			<div id="successDiv" class='messageContainer' style="text-align: center;">
				<div>
					<i style="color: #2169ff" class="fa-solid fa-circle-check fa-4x" aria-hidden="true"></i>
				</div>
				<h2 style="color: #2169ff">Success</h2>
				<br>
				<div>
					<p>${ successMessage }</p>
					<br>
					<a class="btn success" href="${ redirectURI }">Continue</a>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div id="errorDiv" class='messageContainer' style="text-align: center;">
				<div>
					<i style="color: #ff3b3b" class="fa-solid fa-circle-xmark fa-4x" aria-hidden="true"></i>
				</div>
				<h2 style="color: #ff3b3b">Error</h2>
				<div>
					<p>${ errorMessage }</p>
					<br>
					<a class="btn error" href="${ redirectURI }">Continue</a>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

</body>
</html>