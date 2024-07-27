<!DOCTYPE html>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="User Authorization" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Set Up User Authorization</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<div class="panel-body" style="border-radius:8px">

				 <form:form  method="post"
					modelAttribute="user" id="createUser" role="form">
					<fieldset>						
						<div class="col-md-6  column">
							<div class="form-group" style="margin-top:15px">
								<input id="userId" path="userId" type="text" placeholder="User ID" class="form-control" 
								required="required">
					 	 	</div> 
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="searchUserAuthorization">Search User</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="searchUserAuthorizationFormNew" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>

						</div>
					</fieldset>
				</form:form> 

			</div>

			 <div class="panel-body" style ="border-radius:8px;">
					<form:form action="setAuthorization" method="post"
						modelAttribute="userAuthorizationBean" id="createUser" role="form">
						<fieldset>
							<div class="col-md-12 col-xl-8 column">
								<div class="form-group" style="margin-top:15px">
									<form:input id="userId" path="userId" type="text"
										readonly="true" placeholder="User ID" class="form-control"
										value="${userAuthorizationBean.userId}" />
								</div>
								<div class="form-group row" style="overflow: visible;">
									<div class="col-md-8 column">
										<form:label path="allRoles" for="allRoles">Available Roles</form:label>
										<form:select id="allRoles" path="allRoles" style="border-radius:6px ; cursor:pointer"
											class="form-control" multiple="true" size="6">
											<form:options items="${available}"/>
										</form:select>
									</div>
									<div class="col-md-2 column" style="text-align: center">
										<br />
										<br /> <a href="JavaScript:void(0);" id="btn-add1">Add
											&raquo;</a><br /> <a href="JavaScript:void(0);" id="btn-remove1">&laquo;
											Remove</a>
									</div>
									<div class="col-md-8 column">
										<form:label path="roles" for="roles">Allocated Roles</form:label>
										<form:select id="roles" path="roles" class="form-control"  style="border-radius:6px ; cursor:pointer ; "
											multiple="true" size="6">
											
											<form:options items="${allocated }"/>
										</form:select>
									</div>
								</div>

								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											formaction="" onClick="selectAllOptions();">Set
											Authorization</button>
										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="searchUserAuthorizationFormNew" formnovalidate="formnovalidate">Cancel</button>
									</div>
								</div>

							</div>
						</fieldset>
					</form:form>
				</div> 
	
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script src ="${pageContext.request.contextPath }/assets/js/searchUserAuthorizationFormNew.js"></script>
</body>
</html>
