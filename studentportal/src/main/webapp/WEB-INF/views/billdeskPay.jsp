<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Payment Initiating</title>
</head>
<body>
	<center>
		<h1>Please do not refresh this page...</h1>
	</center>
	<form method="post" action="${ trans_url }" name="f1">
		<table border="1">
			<tbody>
				<input type="hidden" name="msg" value="${ msg }">
			</tbody>
		</table>
		<script type="text/javascript">
            document.f1.submit();
        </script>
	</form>
</body>
</html>