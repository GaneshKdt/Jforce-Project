<!DOCTYPE html>
<%@page import="java.util.*"%>
<%--@page import="java.text.DateFormat, com.nmims.beans.UserAuthorizationBean"--%>

<%@page import="com.nmims.beans.Page"%>
<html lang="en">
<%-- 
String roles = "";
UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization");
if(userAuthorization != null){
	roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
}
 --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../adminCommon/jscss.jsp">
<jsp:param value="Remarks Grade Results Processing Checklist" name="title"/>
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Results Processing ; Remarks Grade Checklist"
				name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Results Processing : 
							Remarks Grade Checklist</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<div class="col-md-16 column">
								<legend>
									&nbsp;Remarks Grade Checklist<font size="2px"> </font>
								</legend>
								<div class="table-responsive">
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
												<th>Link</th>
												<th>Description</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>1. <a href="/exam/admin/stepRG1"
													target="_blank">Upload Marks</a></td>
												<td>Click Link To Upload Marks (Assignment).</td>
											</tr>
											<tr>
												<td>2. <a href="/exam/admin/stepRG2"
													target="_blank">View Marks</a></td>
												<td>Click Link To View Uploaded Marks.</td>
											</tr>
											<tr>
												<td>3. <a href="/exam/admin/stepRG25"
													target="_blank">Add Copycases</a></td>
												<td>Click Link To Add Copycases.</td>
											</tr>
											<tr>
												<td>4. <a href="/exam/admin/stepRG3"
													target="_blank">Upload Absentees</a></td>
												<td>Click Link To Upload Absentees.</td>
											</tr>
											<tr>

												<td>4. <a href="/exam/admin/stepRG4"
													target="_blank">RIA/NV Cases</a></td>
												<td>RIA/NV Cases.</td>
											</tr>
											<tr>

												<td>5. <a href="/exam/admin/stepRG5"
													target="_blank">Pass/Fail Trigger</a></td>
												<td>Start the Pass/Fail Trigger Process.</td>
											</tr>
											<!-- tr>
												<td>6. <a href="/exam/checklist?typePageNumber=ugpage6"
													target="_blank">Grace</a></td>
												<td>Grace.</td>
											</tr> -->
											<tr>

												<td>6. <a href="/exam/admin/stepRG66"
													target="_blank">Generate Absolute Grading</a></td>
												<td>Generate Absolute Grading.</td>
											</tr>
											<tr>

												<td>7. <a href="/exam/admin/stepRG7"
													target="_blank">Transfer</a></td>
												<td>Transfer out from Staging.</td>
											</tr>
											<tr>

												<td>8. <a href="/exam/admin/stepRG8"
													target="_blank">Make Live</a></td>
												<td>Turn on Result Display.</td>
											</tr>
											<tr>

												<td>9. <a href="/exam/admin/stepRG9"
													target="_blank">PassFail Report</a></td>
												<td>PassFail details.</td>
											</tr>
											<tr>
												<td>10. <a href="/exam/admin/stepRG15"
													target="_blank">Eligible Student Report</a></td>
												<td>Eligible Students Report (assignments for the given exam cycle).</td>
											</tr>
											<!-- tr>
												<td>8. <a href="/exam/checklist?typePageNumber=ugpage1"
													target="_blank">Login as Student</a></td>
												<td>Click Link to Login As Student to Verify Results.</td>
											</tr> -->
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
</body>
</html> 