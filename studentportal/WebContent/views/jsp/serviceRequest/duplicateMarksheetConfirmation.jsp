<!DOCTYPE html>

<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<%@ include file="../common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>



							<form:form action="saveSingleBook" method="post"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>
									<div class="col-md-6 column">


										<div class="form-group">
											<form:label path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType }</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<form:select id="year" path="year" required="required"
												class="form-control" itemValue="${sr.year}">
												<c:forEach var="year" items="${sr.getYear}"
													varStatus="status">
													<form:option value="">Select Exam Year</form:option>
													<form:options items="${year}" />
												</c:forEach>
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="month" path="month" required="required"
												class="form-control" itemValue="${sr.month}">
												<c:forEach var="month" items="${sr.getMonth}"
													varStatus="status">
													<form:option value="">Select Exam Month</form:option>
													<form:option value="${month}">
														<c:out value="${month}" />
													</form:option>
												</c:forEach>
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="sem" path="sem" required="required"
												class="form-control" itemValue="${sr.sem}">
												<c:forEach var="semester" items="${sr.getSem}"
													varStatus="status">
													<form:option value="">Select Semester</form:option>
													<form:option value="${month}">
														<c:out value="${semester}" />
													</form:option>
												</c:forEach>
											</form:select>
										</div>


										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="checkMarksheetHistory" class="form-control">Proceed</button>
												<button id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-danger" formaction="selectSRForm"
													formnovalidate="formnovalidate">Back to New
													Service Request</button>
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


	<jsp:include page="../common/footer.jsp" />


</body>
</html>