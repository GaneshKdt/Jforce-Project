<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>

<style>

.backgroud{
	background-color: gray;
}

.messageContainer{
	margin: auto;
	width: 40%;
	height: 50%;
	background: white;
	box-shadow: 1px 1px 8px 2px lightgrey;
	border: 2px;
	border-radius: 20px;  
}

.success{
	background: green;
}

.success{
	background: red;
}
</style>

<body>
	<c:choose>
		<c:when test="${ success == true }">
			<div id="successDiv" class='messageContainer success'>
				<h2>Success</h2>
				<div>
					<p>${ successMessage }</p>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<div id="errorDiv" class='messageContainer'>
				<h2>Error</h2>
				<div>
					<p>${ errorMessage }</p>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

</body>
</html>