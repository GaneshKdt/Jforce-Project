<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>CC Avenue Transaction Processing</title>
</head>
<body style="background-color:rgb(245,245,245)">
	<center><h1>Please do not refresh this page...</h1></center>
        <form action='${ transaction_url }' method='post' name="redirect">
			<input type="hidden" id="encRequest" name="encRequest" value="${ encRequest }" />
			<input type="hidden" name="access_code" id="access_code" value="${ access_code }" />

	<script language='javascript'>
            document.redirect.submit();
        </script>
        	</form>
</body>
</html>