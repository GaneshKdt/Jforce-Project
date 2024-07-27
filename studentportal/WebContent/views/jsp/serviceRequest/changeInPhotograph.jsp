
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
								Please provide notarized affidavit or bank stamped
								identification affidavit to verify the photograph provided.</p>
							<!-- 	<p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->

							<form:form action="saveChangeInPhotograph" method="post"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>

									<div class="row">
										<div class="col-md-8 column">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group">
												<label>Charges:</label>
												<p>No Charges</p>
											</div>

											<div class="form-group">
												<label for="changeInPhotographDoc">Please Upload Photo</label> <input type="file"
													name="changeInPhotographDoc" required="required" id="changeInPhotographDoc"
													class="form-control">
											</div>


											<div class="form-group">
												<label for="changeInPhotographProofDoc">Please Upload Photo ID for verification</label> <input
													type="file" name="changeInPhotographProofDoc" id="changeInPhotographProofDoc"
													required="required" class="form-control">
											</div>



											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="saveChangeInPhotograph"
														onClick="return confirm('Are you sure you want to save this information?');">Save
														Service Request</button>
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