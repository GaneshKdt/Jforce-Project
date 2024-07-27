<!DOCTYPE html>
<html lang="en">
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Mass Update SR Status" name="title" />
</jsp:include>
<style>
	.panel-default {
		margin-top: 1em;
	}
	.panel-heading {
		padding: 1em 1.6em 2em;
	}
	.panel-title {
		font-size: 1.1em;
	}
	.panel-body {
		padding-top: 1.2em;
		padding-left: 2em;
	}
	label {
		font-size: 0.9em;
	}
	#cancelReason {
		margin-bottom: 0.5em;
	}
	.reasonNote {
		display: block;
		color: #d2232a;
    	font-size: 14px;
    	font-weight: 600;
	}
</style>
<body>
	<jsp:include page="../adminCommon/header.jsp" />
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Student Portal</a></li> 
	        		<li><a href="massUpdateSRStatusForm">Mass Update SR Status</a></li>
		        </ul>
          	</div>
        </div><!-- breadcrumbs -->

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				
				<div class="sz-content-wrapper examsPage">
					<jsp:include page="../adminCommon/adminInfoBar.jsp" />
					<div class="sz-content">
					
						<div class="panel panel-default">
							<div class="panel-heading">
								<h2 class="panel-title red text-capitalize">Mass Update SR Status</h2>
							</div>
							
							<div class="panel-body">
								<c:if test="${not empty status}">
									<fmt:parseNumber var="updateRecordCount" integerOnly="true" value="${successCount}" />
									<c:if test="${updateRecordCount gt 0}">
									    <div class="alert alert-success alert-dismissible" role="alert">
											<button type="button" class="close" data-dismiss="alert" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
											<strong>${updateRecordCount} Service Request record(s) updated successfully!</strong>
									    </div>
									</c:if><!-- Displays the success message -->
									
									<c:if test="${status eq 'error'}">
									    <div class="alert alert-danger alert-dismissible" role="alert">
											<button type="button" class="close" data-dismiss="alert" aria-label="Close">
												<span aria-hidden="true">&times;</span>
											</button>
											<strong>${statusMessage}</strong>
									    </div>
									</c:if><!-- Displays the error message -->
								</c:if><!-- Check if the status attribute exists -->
								
								<form method="post"	action="massUpdateSRStatus" onsubmit="return validateSubmit()">
									<div class="row">
										<div class="col-md-4 form-group">
											<label for="srIdList">Service Request IDs</label>
											<textarea id="srIdList" name="serviceRequestIds" class="form-control" required 
												cols="50" rows="7" placeholder="Enter different Service Request IDs in new lines"></textarea>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-4 form-group">
											<label for="requestStatus">Select SR Status</label>
	 										<select name="requestStatus" id="requestStatus" required 
	 											class="form-control" onchange="requestStatusChangeCheck(this.value)">
	 											<option value="">Select an Option</option>
	 											<option value="Cancelled">Cancelled</option>
	 											<option value="Closed">Closed</option>
	 											<option value="In Progress">In Progress</option>
	 											<option value="Payment Failed">Payment Failed</option>
	 											<option value="Payment Pending">Payment Pending</option>
	 											<option value="Submitted">Submitted</option>
											</select>
										</div>
									</div>
									
									<div class="row hidden" id="cancelReasonBlock">
										<div class="col-md-3">
											<label for="cancelReason">Cancellation Reason</label>
											<textarea id="cancelReason" name="cancellationReason" class="form-control"
												cols="20" rows="4" placeholder="Enter the Cancellation Reason"></textarea>
										</div>
										<div class="col-xs-10 form-group">
											<span class="reasonNote">Note: The entered Cancellation Reason will be applied to all of the provided Service Request IDs.</span>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-6 form-group">
											<button id="submit" name="submit" 
												class="btn btn-large btn-primary">Submit</button>
											<button id="reset" type="reset"
												class="btn btn-danger">Reset</button>
											<a class="btn btn-large btn-danger"
												href="searchSRForm">search SR</a>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	
<script>
	function requestStatusChangeCheck(requestStatus) {
		if(requestStatus === "Cancelled") {
			document.getElementById("cancelReasonBlock").classList.remove("hidden");
		}
		else {
			document.getElementById("cancelReasonBlock").classList.add("hidden");
			document.getElementById("cancelReason").value = "";
		}
	}

	function validateSubmit() {
		if(document.getElementById("requestStatus").value === "Cancelled" 
			&& document.getElementById("cancelReason").value.trim() === "") {
			alert("Please enter the Cancellation Reason.");
			return false;
		}
		else {
			return true;
		}
	}
</script>
</body>
</html>