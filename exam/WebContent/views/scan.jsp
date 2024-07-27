<%@page import="com.nmims.helpers.JavaAPICheck"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Scan File</title>
</head>
<body>


<%

String[] args = new String[4];
args[0] = "-file:C:\\Users\\Sanket\\Downloads\\Symantec\\Symantec\\symantec\\src\\form29.pdf";
args[1] = "-server:localhost:8004";
args[2] = "-policy:scan";
args[3] = "-api:2";
JavaAPICheck scanner = new JavaAPICheck();
scanner.scanFiles(args);


%>

</body>
</html>