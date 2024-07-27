<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="jscss.jsp">
<jsp:param value="Edit Student Registration" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Student Registration</legend></div>
        
		<div class="row clearfix">
		<%@ include file="messages.jsp"%>
		<form:form  action="updateStudentRegistration" method="post" modelAttribute="student">
			<fieldset>
			<div class="col-md-6 column">
			<!--   -->
		

				<%if("true".equals((String)request.getAttribute("edit"))){ %>
						<form:hidden id="oldSem" path="oldSem" value="${student.sem}" />
				<%} %>
				<div class="form-group">
						<label class="control-label" for="year">Academic Year</label>
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${student.year}">
							<form:option value="">Select Acadmemic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<label class="control-label" for="month">Academic Month</label>
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${student.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>

					
					<div class="form-group">
							
							<label class="control-label" for="sapid">SAP ID</label>
							<%if("true".equals((String)request.getAttribute("edit")) && roles.indexOf("Exam Admin") == -1){ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"   readonly="true" />
							<%}else{ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}" />
							<%} %>
							
							
					</div>
					
					<div class="form-group">
						<label class="control-label" for="sem">Semester</label>
						<form:select id="sem" path="sem" placeholder="Semester" class="form-control" required="required" value="${student.sem}" >
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="program">Program</label>
						<form:select id="program" path="program"  class="form-control" required="required" itemValue="${student.program}" >
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>

					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<%if("true".equals((String)request.getAttribute("edit"))){ %>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateStudentRegistration">Update</button>
							<%}else	{%>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateStudentRegistration">Submit</button>
							<%} %>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
					
				</div>
				
				
				
			</fieldset>
		</form:form>

		</div>
		</div>
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
