<!DOCTYPE html>


<html lang="en">
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Attendance & Feedback" name="title" />
</jsp:include>



<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Report for Academic Feedback"
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

						<h2 class="red text-capitalize">Report for Academic Feedback</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="downloadAcadFeedbackReportForm" method="post"
								modelAttribute="feedback">
								<fieldset>
									<div class="col-md-6 column">

										<div class="form-group">
											<form:select id="acadYear" path="year" required="required"
												class="form-control" itemValue="${feedback.year}">
												<form:option value="">Select Academic Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="acadMonth" path="month" required="required"
												class="form-control" itemValue="${searchBean.month}">
												<form:option value="">Select Academic Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>


										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="generateAcadFeedbackReport">Generate</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" formnovalidate="formnovalidate">Cancel</button>

										</div>



									</div>


								</fieldset>
								<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1){%>

								<c:if test="${rowCount > 0}">
									<legend>
										&nbsp;Academic Feedback Report<font size="2px"><span
											style="color: red">&nbsp;(${rowCount} Records)&nbsp; </span><a
											href="downloadAcadFeedbackReport">Download to Excel</a></font>
									</legend>

								</c:if>
								<c:if test="${rowCount == 0}">
									<legend>
										&nbsp;Academic Feedback Report<font size="2px"> NO
											RECORDS FOUND </font>
									</legend>

								</c:if>

								<%} %>
							</form:form>





						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />



</body>
</html>