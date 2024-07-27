<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>NGASCE payment processing</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	
	<center>
		<div>
			<h3>Loading...</h3>
			<h4>Don't closed or refresh browser while payment in process</h4>
		</div>
	</center>
	<form action="${ payment_response.portal_return_url }" method="POST" name="f1">
		<input type="hidden" name="track_id" value="${ payment_response.track_id }" />
		<input type="hidden" name="sapid" value="${ payment_response.sapid }" />
		<input type="hidden"  name="transaction_id" value="${ payment_response.transaction_id }" />
		<input type="hidden"  name="request_id" value="${ payment_response.request_id }" />
		<input type="hidden"  name="merchant_ref_no" value="${ payment_response.merchant_ref_no }" />
		<input type="hidden"  name="secure_hash" value="${ payment_response.secure_hash }" />
		<input type="hidden"  name="response_amount" value="${ payment_response.response_amount }" />
		<input type="hidden"  name="response_transaction_date_time" value="${ payment_response.response_transaction_date_time }" />
		<input type="hidden"  name="response_code" value="${ payment_response.response_code }" />
		<input type="hidden"  name="response_payment_method" value="${ payment_response.response_payment_method }" />
		<input type="hidden"  name="response_message" value="${ payment_response.response_message }" />
		<input type="hidden"  name="payment_id" value="${ payment_response.payment_id }" />
		<input type="hidden"  name="payment_option" value="${ payment_response.payment_option }" />
		<input type="hidden"  name="bank_name" value="${ payment_response.bank_name }" />
		<input type="hidden"  name="transaction_status" value="${ payment_response.transaction_status }" />
		<input type="hidden"  name="error" value="${ payment_response.error }" />
		<input type="hidden"  name="description" value="${ payment_response.description }" />
		<br/> <br/>
	</form>
	<script type="text/javascript">
            document.f1.submit();
        </script> 
</body>
</html>