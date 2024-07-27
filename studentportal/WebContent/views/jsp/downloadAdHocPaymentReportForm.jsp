<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="./adminCommon/jscss.jsp">
	<jsp:param value="AdHoc Payment Report" name="title" />
</jsp:include>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.css" />
<body>
	<%@ include file="./adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="./adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Download Adhoc Payment Report"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="./adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="./adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Download Adhoc Payment Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="./adminCommon/messages.jsp"%>
								<fieldset>
									<div class="col-md-4">
										<form id="submittrackIdForm" action="downloadAdHocPaymentFilterReport?filterType=trackId" method="post">
											<div class="form-group">
												<label for="trackId">Track Id</label>
	    										<input type="text" class="form-control" id="trackId" name="trackId" placeholder="Enter track id">
											</div>
											
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submittrackId" name="submittrackId"
														class="btn btn-large btn-primary">Download Report</button>
												</div>
											</div>
										</form>
										
										<p>------------------ OR ------------------</p>
										<form id="submitDateRangeForm" action="downloadAdHocPaymentFilterReport?filterType=daterange" method="post">
											<div class="form-group">
												<label for="daterange">Date From - TO</label>
	    										<input type="text" class="form-control" id="daterange" name="daterange" placeholder="Select your date from - to you want repot">
											</div>
											
	
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submitDateRange" name="submitDateRange"
														class="btn btn-large btn-primary">Download Report</button>
												</div>
											</div>
										</form>

									</div>

								</fieldset>
							</form>
						</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="./adminCommon/footer.jsp" />
	<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/daterangepicker/daterangepicker.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>
	<script>
		$(function() {
		  $('#daterange').daterangepicker({
		    opens: 'left'
		  }, function(start, end, label) {
		    console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
		  });
		});
		
		$(document).ready(function(){
			$('#submittrackId').click(function(e){
				e.preventDefault();
				let trackId = $('#trackId').val();
				//let daterange = $('#daterange').val();
				console.log(trackId);
				//console.log(daterange)
				if((trackId == null || trackId == "" || trackId.trim() == "")){
					alert("Please enter you trackId");
					return false;
				}
				$('#submittrackIdForm').submit();
				
				
			});
			
			$('#submitDateRange').click(function(e){
				e.preventDefault();
				//let trackId = $('#trackId').val();
				let daterange = $('#daterange').val();
				//console.log(trackId);
				console.log(daterange)
				if((daterange == null || daterange == "" || daterange.trim() == "")){
					alert("Please enter you date range");
					return false;
				}
				$('#submitDateRangeForm').submit();
			});
		});
	</script>
</body>
</html>