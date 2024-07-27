
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<style>
/* Fade in tabs */
@
-webkit-keyframes fadeEffect {
	from {opacity: 0;
}

to {
	opacity: 1;
}

}
@
keyframes fadeEffect {
	from {opacity: 0;
}

to {
	opacity: 1;
}

}
#my_id {
	display: block;
}
</style>

<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Registration Links" name="title" />
</jsp:include>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Registration;Registration Links"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Registration Links</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>

							<form:form action="" method="get" modelAttribute="person"
								id="registrationAutomatedForm">
								<fieldset>


									<div class="form-group">
										<form:input id="userId" path="userId" type="text"
											placeholder="SAP ID" class="form-control"
											value="${person.userId}" disabled="disabled" />
									</div>



									<div class="form-group">
										<form:select id="registrationType" path="registrationType"
											type="text" placeholder="Year" class="form-control"
											itemValue="${person.registrationType}" required="required">
											<form:option value="">Select Registration type</form:option>
											<form:option value="PCP Registration">PCP Registration</form:option>
											<form:option value="Exam Registration">Exam Registration</form:option>
										</form:select>
									</div>

									<button id="submit" name="submit"
										class="btn btn-large btn-primary">Submit</button>

									<div id="exTab1" class="">
										<ul class="nav nav-pills">
											<li class="active"><a href="#1a" data-toggle="tab">PCP
													Registration Link</a></li>
											<li><a href="#2a" data-toggle="tab">EXAM
													Registration Link</a></li>

										</ul>

										<div class="tab-content clearfix">
											<div class="tab-pane active" id="1a">
												<h3 style="float: left; margin: 1em 1em 0 0;">
													<a
														href="${SERVER_PATH}/acads/student/selectPCPSubjectsForm?eid=${passEnc}"
														target="_blank"> Click Here For PCP Registration Link
													</a>
												</h3>
											</div>
											<div class="tab-pane" id="2a">
												<h3 style="float: left; margin: 1em 1em 0 0;">
													<a
														href="${SERVER_PATH}/exam/selectSubjectsForm?eid=${passEnc}"
														target="_blank"> Click Here For Exam Registration Link
													</a>
												</h3>
											</div>
										</div>
									</div>


								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>

			<jsp:include page="adminCommon/footer.jsp" />
</body>


</html>

