<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>LoginAsForm</title>
</head>
<body>
<form action="/" method="POST" id="form1">
	<center><h3>Loading...</h3></center>
	<input type="hidden" name="username" value="${ username }"/>
	<input type="hidden" name="password" value="${ password }"/>
</form>	
<script>
document.getElementById("form1").submit();
</script>
</body>
</html>