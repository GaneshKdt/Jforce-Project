<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Learning Resources" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Learning Resources</legend></div>
       	<%@ include file="messages.jsp"%>
		
		
		
	<c:choose>
		<c:when test="${rowCount > 0}">
		<!-- 	<form:form action="" method="post" modelAttribute="content">
			 	<form:input type="hidden" path="year" id="year" value="${currentYear}"/>
				<form:input type="hidden" path="month" id="month" value="${currentMonth}"/>-->
				
				<div class="table-responsive panel-body">
					<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Program</th>
							<%} %>
							<th>Subject</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Sem</th>
							<%} %>
							<th>Month</th>
							<th>Year</th>
							<th>Content</th>
						</tr>
						</thead>
						
						<tbody>
						
						<c:forEach var="bean" items="${subjects}" varStatus="status">
						
					        <tr>
					        <form:form action="" method="post" modelAttribute="content">
					        
					            <td><c:out value="${status.count}" /></td>
					            
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
					            <td><c:out value="${bean.program}" /></td>
					            <%} %>
					            
					            <td><c:out value="${bean.subjectName} (${bean.subjectcode})" /></td>
					            <td><c:out value="${bean.month}" /></td>
					            <td><c:out value="${bean.year}" /></td>
					            <form:input type="hidden" path="month" id="month" value="${bean.month}"/>
					            <form:input type="hidden" path="year" id="year" value="${bean.year}"/>
					            
							   
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
								<td><c:out value="${bean.sem}" /></td>
								<%} %>

								<td> 
						            <c:url value="viewContentForSubject" var="contentUrl">
									  <c:param name="subjectCodeId" value="${bean.subjectCodeId}" />
									</c:url>

									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
									
					            </td>
					          </form:form>  
					        </tr> 
					       
					    </c:forEach>
						
						</tbody>
					</table>
				</div>
				<br>
			<!-- </form:form>-->
		</c:when>
	</c:choose>
	
	</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html> --%>
<!--Commented as above code By Riya as month and year are static  -->
<!-- Commented to add dropdown of year and month -->
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Learning Resources" name="title" />
</jsp:include>
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">
<body class="inside">

<%@ include file="header.jsp"%>
	 <div class="sz-main-content-wrapper">
      <section class="content-container login">
       <div class="container-fluid customTheme">
        <div class="row"><legend>Learning Resources</legend></div>
        <div class="panel-body">
        <div class="sz-main-content menu-closed">
        	<div class="sz-main-content-inner">
        		
        		<div class="sz-content">
        		
        		
        		<div class="clearfix"></div>
				<div class="panel-content-wrapper" style="min-height:450px;">
       	
		<%@ include file="messages.jsp"%>

	
		<form:form action="" method="post" modelAttribute="content">
			<div class="form-group">
			
						<form:select id="year" path="year" type="text" 	placeholder="Year" style="width: 450px;" class="form-control filterBy"  required="required" 
								itemValue="${content.year}" > 
								<form:option value="">Select Acad Year</form:option>
								<form:option value="${currentYear }" selected="true" >${currentYear }</form:option>
								<form:options items="${yearList}" />
							</form:select>
				</div>
												
				<div class="form-group">
					
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control filterBy" style="width: 450px;" required="required" 
								itemValue="${content.month}" >
								<form:option value="${currentMonth}" selected="true">${ currentMonth}</form:option>
								<form:options items="${monthList}" />
						</form:select>
					
				</div>
				<h2 class="red text-capitalize">Applicable Subjects</h2>
				
			<div class="table-responsive">
					<table class="table table-striped" id="dataTable" style="font-size:12px;">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Program</th>
							<%} %>
							<th>Subject</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Sem</th>
							<%} %>
							<th>Content</th>
						
						</tr>
						</thead>
						
						<tbody>
						
						<c:forEach var="bean" items="${subjects}" varStatus="status">
						
					        <tr>
					        
					        
					            <td><c:out value="${status.count}" /></td>
					            
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
					            <td><c:out value="${bean.program}" /></td>
					            <%} %>
					            
					            <td><c:out value="${bean.subjectName} (${bean.subjectcode})" /></td>
					            
							   
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
								<td><c:out value="${bean.sem}" /></td>
								<%} %>

								<td> 
						            <c:url value="viewContentForSubject" var="contentUrl">
									  <c:param name="subjectCodeId" value="${bean.subjectCodeId}" />
									</c:url>

									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
									
					            </td>
					           
					        </tr> 
					       
					    </c:forEach>
						
						</tbody>
					</table>
				</div>
				<br>
			</form:form>
		
	</div>
	
	</div>
	</div>
	</div>
	</div>
</div>
</section>
	</div> 
	

	  <jsp:include page="footer.jsp" />


</body>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>
    <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
    <script type="text/javascript">
	    $(document).ready (function(){
	    	$('#dataTable').DataTable();
	    	
	
	    });
    </script>
    
     <script>
     $(document).ready(function(){
  	   $('.filterBy').on('change', function () {
  		   var year = $('#year').val(); 
  		   var month = $('#month').val(); 
  		 
  			window.location = '/acads/admin/viewApplicableSubjectsForFaculty?month='+month+'&year='+year;
  	      });
     }); 
    </script>
</html>


 
 