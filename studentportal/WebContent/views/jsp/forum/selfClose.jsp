<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Post Reply" name="title" />
</jsp:include>

<body>

	<script>

        	opener.location.href="/studentportal/student/viewForumThreadChain?id=${originalPostId}&reply=true";
        	window.close();
        
        
        </script>

</body>
</html>