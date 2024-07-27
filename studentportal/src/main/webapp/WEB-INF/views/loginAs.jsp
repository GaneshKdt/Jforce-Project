<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.PageStudentPortal"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />
<%

		//String SERVER_PATH = (String)request.getAttribute("SERVER_PATH");
		
		String examAppLogoutUrl = (String)request.getAttribute("SERVER_PATH") + "exam/logoutforSSO";
		String acadsAppLogoutUrl = (String)request.getAttribute("SERVER_PATH") + "acads/logoutforSSO";
		String ltiAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/logoutforSSO";
		String csAppLogoutUrl = (String)request.getAttribute("SERVER_PATH") + "careerservices/logoutforSSO";
	
	%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Log In as" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Log In As</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<div class="row clearfix">
				<form action="loginAs" method="post">
					<fieldset>
						<div class="col-md-6 column">


							<div class="form-group">
								<input id="userId" name="userId" type="text"
									placeholder="User ID" class="form-control" /> <input
									id="password" name="password" type="password"
									placeholder="Admin Password" class="form-control" />
							</div>


							<!-- Button (Double) -->
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="loginAs">Log
										In</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>



						</div>



					</fieldset>
				</form>

			</div>
		</div>

		<div id="examApp"></div>
		<div id="acadsApp"></div>
		<div id="ltiApp"></div>
		<div id="csApp"></div>

	</section>

	<jsp:include page="footer.jsp" />



	<script>
		    	//Logout users from all apps when they visit login page
				$( "#examApp" ).load( "<%=examAppLogoutUrl%>" );
				$( "#acadsApp" ).load( "<%=acadsAppLogoutUrl%>" );
				$( "#ltiApp" ).load( "<%=ltiAppLogoutUrl%>" );
				$( "#csApp" ).load( "<%=csAppLogoutUrl%>" );
			</script>

</body>

</html>
