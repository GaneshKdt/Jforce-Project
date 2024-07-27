<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirecting..</title>
</head>
<body>


<form action="${WEB_EX_API_URL }" method="post" id="webExForm">

<input type="hidden" name="AT" value="HM"/>
<input type="hidden" name="MK" value="${session.meetingKey }"/>
<input type="hidden" name="CSRF" value="${CSRF}"/>

</form>

<script type="text/javascript">

document.getElementById("webExForm").submit();
</script>

</body>
</html>