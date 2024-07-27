<!DOCTYPE html>
<html class="no-js"> <!--<![endif]-->


<%@page import="java.util.ArrayList"%> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Learning Resources" name="title" />
</jsp:include>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>    

<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css"> 
  
  
<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       
       	<div class="row"><legend><%=request.getParameter("name") %></legend></div>
		
		<div class="panel-body">
						
			<div class="col-md-18 column">
				<div class="sz-content">
					
						<div class="clearfix"></div>
			            <div class="panel-content-wrapper">
			              	<%@ include file="common/messages.jsp" %>
			              		<%
			              			if("PDF".equals(request.getParameter("type"))){ %>
										<div class="embed-responsive embed-responsive-16by9">	
											<iframe class="embed-responsive-item" src="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=request.getParameter("previewPath") %>?id=<%=Math.random() %>#toolbar=0"></iframe>
										</div>
	           						<% } %>
	           									
						</div>
              	</div>
			</div>
			
		</div>
		</div>
	
	</section>
	
	<script>
		function previewContent() {
			var search = window.location.search;
				window.location.href = "/acads/admin/previewContentAlt" + search;
			}
  	</script>
  	
    <jsp:include page="footer.jsp"/>

</body>
</html>