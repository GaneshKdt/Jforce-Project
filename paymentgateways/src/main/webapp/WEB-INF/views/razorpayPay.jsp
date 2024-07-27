<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Razorpay payment processing...</title>
</head>
<body>
	<center>
		<h1>Please do not refresh this page...</h1>
	</center>
	<form method="POST" action="${TRAN_URL}" name="f1">
  <input type="hidden" name="key_id" value="${key}">
  <input type="hidden" name="amount" value="${amount}">
  <input type="hidden" name="order_id" value="${order_id}">
  <input type="hidden" name="name" value="${name}">
  <input type="hidden" name="description" value="${description}">
  <input type="hidden" name="image" value="https://d3udzp2n88cf0o.cloudfront.net/css/logo.png">
  <input type="hidden" name="prefill[name]" value="${name}">
  <input type="hidden" name="prefill[contact]" value="${contact}">
  <input type="hidden" name="prefill[email]" value="${email}">
  <%-- <input type="hidden" name="notes[shipping address]" value="${prefill.address} "> --%>
  <input type="hidden" name="callback_url" value="${callback_url}">
  <input type="hidden" name="cancel_url" value="${cancel_url}">
</form>
<script type="text/javascript">
            document.f1.submit();
        </script>
</body>
</html>.