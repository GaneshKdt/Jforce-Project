<!DOCTYPE html>
<%@page import="com.nmims.beans.ForumAcadsBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
    
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Answer Query" name="title"/>
    </jsp:include>
    
    <body>
    
        <script>

        	opener.location.href="/acads/admin/assignedCourseQueries?status=1";
        	window.close();
        
        
        </script>
  	
    </body>
</html>