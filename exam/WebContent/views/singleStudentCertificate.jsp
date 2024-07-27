<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Generate Certificate" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Generate Certificate</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="getMarksheet" method="post" modelAttribute="bean">
			<fieldset>
		

	<div class="col-md-6 column">

					
					<div class="form-group">
							<form:input id="serviceRequestId" path="serviceRequestId" type="text" placeholder="SERVICE REQUEST ID" class="form-control" value="${studentMarks.serviceRequestId}" required="required"/>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<!-- <div class="controls"> -->
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getSingleStudentCertificate">Generate</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
						<!-- </div> -->
					</div>
				</div>
			</fieldset>
		</form:form>
		
		<%if("true".equals((String)request.getAttribute("success"))){ %>
		<a href="${pageContext.request.contextPath}/admin/download">Download Certificate</a>
		<%} %>
		</div>
	</div>

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
