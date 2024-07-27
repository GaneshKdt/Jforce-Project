<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirecting..</title>
</head>
<body>


<form action="${WEB_EX_LOGIN_API_URL }" method="post" id="webExForm">

<input type="hidden" name="AT" value="LI"/>
<input type="hidden" name="WID" value="${session.hostId }"/>
<input type="hidden" name="PW" value="${session.hostPassword }"/>
<input type="hidden" name="MU" value="${SERVER_PATH }acads/admin/startSession?id=${session.id }&type=${type}"/>
<input type="hidden" name="BU" value="${SERVER_PATH }acads/admin/startSession?id=${session.id }&type=${type}"/>



</form>

<script type="text/javascript">

document.getElementById("webExForm").submit();
</script>

</body>
</html>