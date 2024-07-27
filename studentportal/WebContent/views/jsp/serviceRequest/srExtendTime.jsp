
<!DOCTYPE html>
<%@page import="java.util.*"%>

<html lang="en">


<%@page import="com.nmims.beans.ServiceRequestType"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Extended SR Time" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Extended SR Time"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Extend Service Request Time</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="addStudentExtendedSRTimeForm" method="post"
								modelAttribute="srBean">
								<fieldset>
									<div class="col-md-6">

										<div class="form-group">
											<form:select id="serviceRequestType"
												path="serviceRequestName" class="form-control">
												<form:option value="">Select Service Request Type</form:option>
												<form:options items="${allRequestTypes}" />
											</form:select>
										</div>


										<div class="form-group">
											<form:textarea id="sapId" path="sapIdList" type="text"
												cols="50" rows="7"
												placeholder="Enter different Student Ids in new lines"
												class="form-control" required="required" />
										</div>

										<div class="form-group">

											<div class="row">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="addStudentExtendedSRTime">Add Student</button>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="removeStudentExtendedSRTime">Remove
													Students</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>

									</div>




								</fieldset>
							</form:form>
						</div>


					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	<script>
	$('textarea').keypress(function(e) {
    var a = [];
    var k = e.which;
    
    for (i = 48; i < 58; i++)
        a.push(i);
    
    if ((k < 48 || k> 57) && k !== 13)
        e.preventDefault();
    
});
	</script>
</body>
</html>