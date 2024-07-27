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
<jsp:param value="All Active Subjects" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
   	<div class="container-fluid customTheme">
    
   <div class="row"> <legend>Subjects</legend></div>  
		
	<div class="panel-body">
	<div class="col-md-12 column">	
		 
        <%@ include file="messages.jsp"%>
		<c:choose>
		<c:when test="${rowCount > 0}">
	
		<div class="table-responsive">
			<table class="table table-striped" style="font-size:12px">
				<thead>
				<tr>
					<th>Sr. No.</th>
					<th>Subject</th>
					<th>Content</th>
					
				</tr>
			</thead>
				<tbody>
				<form:form  action="" method="post" >
				<c:forEach var="subject" items="${subjects}" varStatus="status">
			        <tr>
			            <td><c:out value="${status.count}" /></td>
			            <td><c:out value="${subject}" /></td>
						<td> 
				            <c:url value="viewContentForSubject" var="contentUrl">
							  <c:param name="subject" value="${subject}" />
							</c:url>
	
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
							
			            </td>
			            
			            
			        </tr>   
			    </c:forEach>
				</form:form>
					
				</tbody>
			</table>
		</div>
	
		<br>
	
		</c:when>
		</c:choose>
	</div>
	</div>
	
	</div>
	

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html> --%>


 <!DOCTYPE html>

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<html lang="en">
	
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
   <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="All Active Subjects" name="title"/>
    </jsp:include>
    
    <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
				<jsp:param value="Academics;All Subjects" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
          		<div class="sz-main-content-inner">
              		<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>
              				
              				
              		<div class="sz-content-wrapper examsPage">
              			<%@ include file="adminCommon/adminInfoBar.jsp" %>
              			
              			<div class="sz-content">
								
							<h2 class="red text-capitalize">Subjects</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<c:choose>
									<c:when test="${rowCount > 0}">
										<form:form action="" method="post" modelAttribute="content">

											<div class="form-group">
												<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" 
													itemValue="${content.year}" > 
													<form:option value="">Select Acad Year</form:option>
													<form:option value="${currentYear }" selected="true" >${currentYear }</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
												
											<div class="form-group">
												<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" 
													itemValue="${content.month}" >
													<form:option value="${currentMonth } " selected="true">${currentMonth }</form:option>
													<form:options items="${monthList}" />
													<%-- <form:option value="Jul">Jul</form:option> --%>
												</form:select>
											</div>
											<input type = "hidden" name="StudentType" id="StudentType" value="PG" />
											<div class="table-responsive">
												<table class="table table-striped" id="dataTable" style="font-size:12px">
													<thead>
													<tr>
														<th>Subject</th>
														<th>Content</th>
														<th>Action</th>
													</tr>
													</thead>
													<tbody>
													
													<c:forEach var="subject" items="${subjects}" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${subject.subjectName} (${subject.subjectcode})" /></td>
															<td> 
																<c:url value="viewContentForSubject" var="contentUrl">
																  <c:param name="subjectCodeId" value="${subject.subjectCodeId}" />
																</c:url>
										
																<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
															</td>															
														</tr>   
													</c:forEach>
														
													</tbody>
												</table>
											</div>
										</form:form>
										<br>
											
									</c:when>
								</c:choose>

							</div>
              			</div>
              		</div>
    			</div>
			</div>
		</div>
		
        <jsp:include page="adminCommon/footer.jsp"/>
        
    </body>
    
    <script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>
    <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
    <script type="text/javascript">
	    $(document).ready (function(){
	    	$('#dataTable').DataTable();
	
	    });
    </script>
</html>