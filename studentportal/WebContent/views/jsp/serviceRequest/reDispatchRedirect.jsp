<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ReDispatch Of Study Kit</title>
</head>
<body>

<form name="myRedirectForm" action="/servicerequest/student/redispatchStudyKit" method="post">
    <input name="serviceRequestType" type="hidden" value="${serviceRequestType}" />
    <input name="error" type="hidden" value="${error}" />
    <input name="errorMessage" type="hidden" value="${errorMessage}" />
</form>
    <script type="text/javascript">

            document.myRedirectForm.submit();

    </script>
</body>
</html>