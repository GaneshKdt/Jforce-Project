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
<jsp:param value="Generate Transcript" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Generate Transcript</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="getTranscript" method="post" modelAttribute="student">
			<fieldset>



		<div class="col-md-6 column">
					
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"/>
					</div>
					
					<div class="form-group" id="">
						<form:label path="logoRequired">
						<form:checkbox id="logoRequired" path="logoRequired" style="width:15px;height:15px"  class="form-control" value="Y"/>Is Logo Required
						</form:label>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getTranscriptMBAX">Generate</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</fieldset>
		</form:form>
		
		<%if("true".equals((String)request.getAttribute("success"))){ %>
			<a href="${pageContext.request.contextPath}/downloadTrascriptSheet">Download Transcript</a>
		<%} %>
		</div>
	</div>
	
	

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
