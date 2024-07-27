<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>


<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<%@ include file="../common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>



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
							<p>You won't be able to submit service Request for next 48hrs
								. For details refer to "My Communications" Tab</p>

							<form:form id="srForm" action="saveDeRegisteredRequest"
								method="post" modelAttribute="sr">
								<fieldset>

									<div class="row">
										<div class="col-md-8">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>


											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="saveDeRegisteredRequest">De-Registered</button>

													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-danger" formaction="selectSRForm"
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


	<jsp:include page="../common/footer.jsp" />


</body>

</html>