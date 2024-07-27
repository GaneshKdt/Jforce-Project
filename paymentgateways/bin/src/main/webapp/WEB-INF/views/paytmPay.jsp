<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Paytm payment processing...</title>
</head>
<body>
	<center><h1>Please do not refresh this page...</h1></center>
        <form method="post" action="${ TRANS_URL }" name="f1">
            <table border="1">
                <tbody>
                    <input type="hidden" name="MID" value="${ MERCHANTMID }">
                    <input type="hidden" name="WEBSITE" value="${ WEBSITE }">
                    <input type="hidden" name="ORDER_ID" value="${ ORDER_ID }">
                    <input type="hidden" name="CUST_ID" value="${ CUST_ID }">
                    <input type="hidden" name="MOBILE_NO" value="${ MOBILE_NO }">
                    <input type="hidden" name="EMAIL" value="${ EMAIL }">
                    <input type="hidden" name="INDUSTRY_TYPE_ID" value="${ INDUSTRY_TYPE_ID }">
                    <input type="hidden" name="CHANNEL_ID" value="${ CHANNEL_ID }">
                    <input type="hidden" name="TXN_AMOUNT" value="${ TXN_AMOUNT }">
                    <input type="hidden" name="CALLBACK_URL" value="${ CALLBACK_URL }">
                    <input type="hidden" name="CHECKSUMHASH" value="${ CHECKSUMHASH }">
                </tbody>
            </table>
        <script type="text/javascript">
            document.f1.submit();
        </script>
        </form>
</body>
</html>