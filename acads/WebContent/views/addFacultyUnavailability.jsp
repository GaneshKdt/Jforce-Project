<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Faculty Unavailability Date" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Faculty Unavailability Date</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body">

			<div class="col-md-6 column">
			
			<form:form  action="addFacultyUnavailability" method="post" enctype="multipart/form-data" modelAttribute="faculty">
			<fieldset>

				<%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="id" value="${faculty.id}"/>
				<%} %>
				
				<div class="form-group">
					<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control" required="required" value="${faculty.facultyId}" readonly="true"/>
				</div>
				
				<div class="form-group">
					<form:input id="unavailabilityDate" path="unavailabilityDate" type="date" placeholder="Date" class="form-control" required="required" value="${faculty.unavailabilityDate}"/>
				</div>

				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<%if("true".equals((String)request.getAttribute("edit"))){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateFacultyUnavailability">Update</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addFacultyUnavailability">Submit</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>

			</fieldset>
		</form:form>

		</div>
		</div>
	</div>
	
</section>
	
 <jsp:include page="footer.jsp" />

</body>
</html>
