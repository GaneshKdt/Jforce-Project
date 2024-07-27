<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Generate LOU Report" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Generate LOU Report"
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
						<h2 class="red text-capitalize">Generate LOU Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="genratelouReport" method="POST" modelAttribute="louForm">
								<fieldset>
									  <div class="col-md-4">
										<div class="form-group">
										 <label for="programType">Select Program Type:</label>
											<form:select id="programType"
												path="programType" class="form-control">
												<form:option value="">-- Select Program Type --</form:option>
												<form:options items="${program_Type_List}" /> 
											</form:select>
										</div>
										</div> 
									   <div class="col-md-4">
										<div class="form-group">
											<label for="programName">Select Program Name:</label>
											 <select name="ProgramName" class="form-control" id="programName" path="programType">
													 <option disabled selected value="">-- Select Program Name --</option>
											</select>
										</div>
									</div> 
								     
									<div class="col-md-4">
										<div class="form-group">
											<label for="semTerm">Select Sem/Term:</label>
											  <select name="semTerm" class="form-control" id="semTerm">
													 <option disabled selected value="">-- Select Sem/Term --</option> 
											</select>
										</div>
									</div>
									
									<div class="col-md-4">
										<div class="form-group">
										 <label for="enrollmentMonth">Select Enrollment Month:</label>
											<form:select id="enrollmentmonth" path="enrollmentmonth" class="form-control">
												<form:option value="">-- Select Month --</form:option>
												<form:options items="${month_list}" />
											</form:select>
										</div>
									</div>
									
									<div class="col-md-4">
										<div class="form-group">
										<label for="enrollmentYear">Select Enrollment Year:</label>
											<form:select id="enrollmentyear" path="enrollmentyear" class="form-control">
												<form:option value="">-- Select Year --</form:option>
												<form:options items="${year_list}" />
											</form:select>
										</div>
									</div>  
									
									<div class="col-md-4">
										<div class="form-group">
											<label for="dataOfSubmission">Select Date of Submission:</label>
											 <input type="date" id="dataOfSubmission" class="form-control" name="dataOfSubmission"/>
										</div>
									</div>
									
								</fieldset>	
							</form:form>
							<button id="generate" class="btn btn-primary">Generate</button>
							<button class="btn btn-primary" type="button" disabled style="display:none" id="loading">
							 Loading...
							</button>
							<p id="generatedResult"></p>
							<button id="downloadlouReport" name="submit" class="btn btn-large btn-primary" style="display:none">
							<a href="/studentportal/admin/downloadlouReport" style="color:white">Download LOU Report</a>
							</button>
						</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources_2015/js/louReport.js"></script>
</body>
</html>