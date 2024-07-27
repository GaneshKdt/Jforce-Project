 <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.itextpdf.text.log.SysoCounter"%>
<html class="no-js">
<!--<![endif]-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="jscss.jsp">
	<jsp:param value="Update Authorization" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

		<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Update Authorization </legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="row">
				<div class="col-xs-18">
					 
					<div class="panel-body content">
					
					<form:form action="viewAuthorization" method="post"
					modelAttribute="user" id="createUser" role="form">
					<fieldset>
						<div class="col-md-6 column">

							<div class="form-group">
								<form:input id="userId" path="userId" type="text"
									placeholder="User ID" class="form-control" required="required"
									value="${user.userId}" />
							</div>


							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="viewAuthorization">Search User</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
										<br />	<br />
										
											<p><b>&nbsp;Click here to download existing User Authorization records.</b><font size="2px"> &nbsp; <a href="/studentportal/admin/downloadAuthorizationReport">Download to Excel</a> </font></p>
										
								</div>
							</div>

						</div>
					</fieldset>
				</form:form>

					<c:if test="${userFound == 'true' }">
				<div class="panel-body">
					<form:form action="updateAuthorization" method="post"
						modelAttribute="userAuthorizationBean" id="createUser" role="form">
						<fieldset>
							<div class="col-md-12 column">
							
								<div class="form-group">
									<form:input id="userId" path="userId" type="text"
										readonly="true" placeholder="User ID" class="form-control"
										value="${userAuthorizationBean.userId}" />
								</div>

								<div class="form-group row" style="overflow: visible;">
									<div class="col-md-8 column">
										<form:label path="allRoles" for="allRoles">Available Roles</form:label>
										<form:select id="allRoles" path="allRoles"
											class="form-control" multiple="true" size="5">
											<form:options items="${availableRolesList}" />
										</form:select>
									</div>
									<div class="col-md-2 column" style="text-align: center">
										<br />
										<br /> <a href="JavaScript:void(0);" id="btn-add3">Add
											&raquo;</a><br /> <a href="JavaScript:void(0);" id="btn-remove3">&laquo;
											Remove</a>
									</div>
									<div class="col-md-8 column">
										<form:label path="roles" for="roles">Allocated Roles</form:label>
										<form:select id="roles" path="roles" class="form-control"
											multiple="true" size="5" required="required">
											<form:options items="${selectedRolesList}" />
										</form:select>
									</div>
								</div>

								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											formaction="updateAuthorization" onClick="selectAllOptions();">Update
											Authorization</button>
										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
									</div>
								</div>

							</div>
						</fieldset>
					</form:form>
				</div>
			</c:if>
					</div>
				</div>
			</div>
		</div>
	</section>
	<br>
	<jsp:include page="footer.jsp" />
	<jsp:include page="updateAuthorization_Js.jsp" />

	
</body>
</html>


 