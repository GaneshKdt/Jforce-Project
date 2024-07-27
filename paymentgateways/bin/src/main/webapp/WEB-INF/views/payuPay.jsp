<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Payu Transaction Processing</title>
</head>
<body>
	<center><h1>Please do not refresh this page...</h1></center>
        <form action='${ trans_url }' method='post' name="f1">
			<input type="hidden" name="firstname" value="${ firstname }" />
			<input type="hidden" name="surl" value="${ surl }" />
			<input type="hidden" name="phone" value="${ phone }" />
			<input type="hidden" name="key" value="${ key }" />
			<input type="hidden" name="hash" value="${ hash }"/>
			<input type="hidden" name="furl" value="${ furl }" />
			<input type="hidden" name="txnid" value="${ txnid }" />
			<input type="hidden" name="productinfo" value="${ productinfo }" />
			<input type="hidden" name="amount" value="${ amount }" />
			<input type="hidden" name="email" value="${ email }" />
	</form>
	
	<script type="text/javascript">
            document.f1.submit();
        </script>
</body>
</html>