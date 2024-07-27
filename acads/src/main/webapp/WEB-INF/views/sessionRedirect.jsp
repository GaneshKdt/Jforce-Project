<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Redirecting..</title>
</head>
<body>

<%-- 
<form action="${WEB_EX_API_URL }" method="post" id="webExForm">

<input type="hidden" name="AT" value="JM"/>
<input type="hidden" name="MK" value="${session.meetingKey }"/>
<input type="hidden" name="PW" value="${session.meetingPwd }"/>
<input type="hidden" name="AN" value="${name }"/>
<input type="hidden" name="AE" value="${email }"/>
<input type="hidden" name="CO" value="${mobile }"/>

</form>

<script type="text/javascript">

document.getElementById("webExForm").submit();
</script> --%>
<%-- 	<form action="${session.studentJoinUrl}" method="post" id="zoomLogin"></form> --%>
	<c:if test="${ not empty tempurl}">
	<form action="${tempurl}" method="post" id="zoomLogin"></form>
	</c:if>
<%-- 
	<c:if test="${joinFor == 'HOST' }">
		<form action="${session.studentJoinUrl}" method="post" id="zoomLogin"></form>
	</c:if>
	<c:if test="${joinFor eq 'ALTFACULTYID' }">
		<form action="${session.studentJoinUrl}" method="post" id="zoomLogin"></form>
	</c:if>
	<c:if test="${joinFor eq 'ALTFACULTYID2' }">
		<form action="${session.studentJoinUrl}" method="post" id="zoomLogin"></form>
	</c:if>
	<c:if test="${joinFor eq 'ALTFACULTYID3' }">
		<form action="${session.studentJoinUrl}" method="post" id="zoomLogin"></form>
	</c:if>

 --%>

<script type="text/javascript">

document.getElementById("zoomLogin").submit();
</script>

</body>
</html>