<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Faculty" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid">
        <div class="row"><legend>Faculty</legend></div>
        <%@ include file="messages.jsp"%>
        
		<div class="panel-body">

				<form  action="editFaculty" method="post">

				<h4>Faculty ID: ${faculty.facultyId}</h4>
				<h4>Name: ${faculty.firstName} ${faculty.lastName}</h4>
				<h4>Email: ${faculty.email}</h4>
				<h4>Contact Number: ${faculty.mobile}</h4>
				<h4>Active: ${faculty.active}</h4>

				<c:url value="editFaculty" var="editurl">
				  <c:param name="id" value="${faculty.id}" />
				</c:url>
				<c:url value="deleteFaculty" var="deleteurl">
				  <c:param name="id" value="${faculty.id}" />
				</c:url>
				

				
				<!-- Button (Double) -->
				<div class="control-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="edit" name="edit" class="btn btn-success" formaction="${editurl}">Edit</button>
						<button id="delete" name="delete" class="btn btn-danger" formaction="${deleteurl}" onclick="return confirm('Are you sure you want to deactivate this Faculty?')">Deactivate</button>
						<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
						
						
					</div>
				</div>
				</form>
			</div>
		</div>
	</section>

<jsp:include page="footer.jsp" />



</body>
</html>
