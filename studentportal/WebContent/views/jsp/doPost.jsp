<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>	
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title></title>
</head>
<body>
	<center>
		<h1>Please do not refresh this page...</h1>
	</center>
	<form method="post" action="${ destination }" name="f1">
		<table border="1">
			<tbody>
				<c:forEach items="${allRequestParam}" var="object">
					<input type="hidden" name="${ object.key }" value="${ object.value }">
				</c:forEach>
				
			</tbody>
		</table>
		<script type="text/javascript">
            document.f1.submit();
        </script>
	</form>
</body>
</html>