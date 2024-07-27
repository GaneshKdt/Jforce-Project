<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>payment processing...</title>
</head>
<body>
	<center><h1>Please do not refresh this page...</h1></center>
        <form method="post" action="/paymentgateways/student/selectGatewayStageTransaction" name="f1">
            <table border="1">
                <tbody>
                    <input type="hidden" name="track_id" value="${ track_id }">
                    <input type="hidden" name="sapid" value="${ sapid }">
                    <input type="hidden" name="type" value="${ type }">
                    <input type="hidden" name="amount" value="${ amount }">
                    <input type="hidden" name="description" value="${ description }">
                    <input type="hidden" name="source" value="${ source }">
                    <input type="hidden" name="portal_return_url" value="${ portal_return_url }">
                    <input type="hidden" name="created_by" value="${ created_by }">
                    <input type="hidden" name="updated_by" value="${ updated_by }">
                    <input type="hidden" name="mobile" value="${ mobile }">
                    <input type="hidden" name="email_id" value="${ email_id }">
                    <input type="hidden" name="first_name" value="${ first_name }">
                    <input type="hidden" name="response_method" value="${ response_method }">
                </tbody>
            </table>
        <script type="text/javascript">
            document.f1.submit();
        </script>
        </form>
</body>
</html>