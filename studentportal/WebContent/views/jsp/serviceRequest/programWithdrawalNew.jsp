 <!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<html lang="en">
   <!--  <style>
    #confirmation{
    position: unset;left: 999px;opacity: 100;
    }
    </style>
 -->
	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    
    
    
    <body>
    <c:set var="reason" value="${fn:split('Shifting Abroad,Not Interested,No time to study,Other', ',')}" scope="application" />
    <jsp:include page="../common/headerDemo.jsp"/>
    	<%-- <%@ include file="../common/headerDemo.jsp" %> --%>



	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
				</ul>
			</div>
		</div>
		<%-- <%@ include file="../common/breadcrum.jsp" %> --%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>



				<div class="sz-content-wrapper examsPage">
					<jsp:include page="../common/studentInfoBar.jsp" />
					<%--   						<%@ include file="../common/studentInfoBar.jsp" %> --%>


					<div class="sz-content">

						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
							<jsp:include page="../common/messageDemo.jsp" />
							<%-- 	<%@ include file="../common/messages.jsp" %> --%>

							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>
							<!-- <p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->

							<form:form method="post" modelAttribute="sr"
								enctype="multipart/form-data" class="">
								<fieldset>

									<div class="row">
										<div class="col-md-8">

											<div class="form-group">
												<form:label class="fw-bold" path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group mb-3">
												<form:label class="fw-bold" path="">Charges:</form:label>
												<p>No Charges</p>
											</div>

											<div class="row">
												<div class="col-md-6 mb-3">
													<div class="form-group">
														<form:label class="fw-bold" path="">Student No:</form:label>
														<p>${student.sapid }</p>
													</div>
												</div>
												<div class="col-md-6 mb-3">
													<div class="form-group">
														<label class="fw-bold">Student Name:</label>
														<p>${student.firstName }${student.lastName }</p>
													</div>
												</div>
											</div>
											<div class="row">
												<div class="col-md-6">
													<div class="form-group">
														<label class="fw-bold">Enrolment Year:</label>
														<p>
														<p>${student.enrollmentYear }</p>
														</p>
													</div>
												</div>
												<div class="col-md-6">
													<div class="form-group">
														<label class="fw-bold">Program Name:</label>
														<p>${student.program }</p>
													</div>
												</div>
											</div>
											<div class="row">
												<div class="col-md-6">
													<div class="form-group">
														<label class="fw-bold">Validity Month:</label>
														<p>
														<p>${student.validityEndMonth }</p>
														</p>
													</div>
												</div>
												<div class="col-md-6">
													<div class="form-group">
														<label class="fw-bold">Validity Year:</label>
														<p>
														<p>${student.validityEndYear }</p>
														</p>
													</div>
												</div>
											</div>

											<div class="form-group">
												<label class="fw-bold ">Reason for withdrawal:</label>
												<form:select id="serviceRequestType" path="description"
													class="form-select mb-2" required="required">
													<form:option value="">Select Service Request</form:option>
													<form:options items="${reason}" />
												</form:select>
											</div>
											<div class="row otherReasonDiv" style="display: none;">
												<div class="form-group col-md-6">
													<label class="fw-bold">Reason :</label>
													<form:input type="textarea" path="additionalInfo1"
														class="form-control otherReason" />

												</div>
												<div class="form-group col-md-6"></div>
											</div>
											<div class="form-group">
												<input type="checkbox" id="confirmation" /> I hereby agree
												and accept to completely withdraw from my program of study
												offered by SVKM's NMIMS - NMIMS Global Access - School for
												Continuing Education. I also agree that in case of any
												dispute or differences about this withdrawal, the decision
												of the SVKM's NMIMS - NMIMS Global Access - School for
												Continuing Education will be final and binding on me. I am
												aware that No fees pending or otherwise will be refunded.
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-danger submitform"
														formaction="saveProgramWithdrawal"
														onClick="return confirm('Are you sure you want to save this information?');">Save
														Service Request</button>
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-dark" formaction="selectSRForm"
														formnovalidate="formnovalidate">Back to New
														Service Request</button>
												</div>
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


	<jsp:include page="../common/footerDemo.jsp"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/serviceRequest/programwithdrawal.js">
	
	</script>
</body>
</html>