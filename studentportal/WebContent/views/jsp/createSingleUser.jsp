<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Create User" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Create User</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<div class="panel-body">


				<form:form action="createSingleUser" method="post"
					modelAttribute="person" id="createUser" role="form" onSubmit="return confirm('Are you sure to create user ? ')">
					<fieldset>
						<div class="col-md-6 column">

							<div class="form-group">
								<form:input id="userId" path="userId" type="text"
									placeholder="User ID" class="form-control" required="required"
									value="${person.userId}" />
							</div>

							<div class="form-group">
								<form:input id="password" path="password" type="text"
									placeholder="Password" class="form-control" required="required"
									value="${person.password}" />
							</div>

							<div class="form-group">
								<form:input id="firstName" path="firstName" type="text"
									placeholder="First Name" class="form-control"
									required="required" value="${person.firstName}" />
							</div>

							<div class="form-group">
								<form:input id="lastName" path="lastName" type="text"
									placeholder="Last Name" class="form-control"
									required="required" value="${person.lastName}" />
							</div>

							<div class="form-group">
								<form:input id="email" path="email" type="text"
									placeholder="Email" class="form-control" email="email"
									value="${person.email}" required="required" />
							</div>

							<div class="form-group">
								<form:input id="program" path="program" type="text"
									placeholder="Program" class="form-control"
									value="${person.program}" />
							</div>

							<div class="form-group">
								<form:input id="contactNo" path="contactNo" type="text"
									placeholder="Contact No" class="form-control"
									value="${person.contactNo}" required="required"  />
							</div>

							<div class="form-group">
								<form:input id="altContactNo" path="altContactNo" type="text"
									placeholder="Alternate Contact No" class="form-control"
									value="${person.altContactNo}" />
							</div>

							<div class="form-group">
								<form:input id="postalAddress" path="postalAddress" type="text"
									placeholder="Postal Address" class="form-control"
									value="${person.postalAddress}" />
							</div>
							
					</div>
		
					</fieldset>
					<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="createSingleUser">Create User</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
				</form:form>

			</div>
			<!-- /row -->

		</div>
		<!-- /container -->
	</section>

	<jsp:include page="footer.jsp" />       
</body>
</html>
