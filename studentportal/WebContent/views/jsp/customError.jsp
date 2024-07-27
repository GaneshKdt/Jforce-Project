<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<body>
<h1>Something went wrong! </h1>

<c:choose>
    <c:when test="${errorMsg ne null }">
        <h2>${errorMsg}</h2>
    </c:when>
    <c:when test="${errorStatusCode eq '404' }">
        <h2>Page Not Found.</h2>
    </c:when>
    <c:when test="${errorStatusCode eq '500' }">
        <h2>Internal Error.</h2>
    </c:when>
    <c:otherwise>
        <h2>Got internal error, Please retry.</h2>
    </c:otherwise>
</c:choose>

<a href="/">Go Home</a>
</body>

<script type="text/javascript">
	console.debug('Error stackTrace')
	<c:forEach items="${exception.stackTrace}" var="element">
		console.debug('${element}')
	</c:forEach>
</script>

</html>