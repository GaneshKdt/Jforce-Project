<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AdhocPaymentStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search SR" name="title" />
</jsp:include>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
	<div class="container-fluid customTheme">
		<div class="row">
			<legend>Search SR</legend>
		</div>
		<%@ include file="messages.jsp"%>
		<div class="row clearfix">
			<form action="searchSRDetailsByTrackId" method="GET">
				<fieldset>
					<div class="col-md-6 column">
						<div class="form-group">
							<label class="control-label" for="trackId">Enter
								Track Id</label>
							<input id="trackId" type="text"
								placeholder="TrackId" name="trackId" class="form-control" value="${ trackId }"
								required="required" />
						</div>

						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit"
									class="btn btn-large btn-primary"
								>Submit</button>
							</div>
						</div>
					</div>

				</fieldset>
			</form>

		</div>
		
		<c:choose>
    		<c:when test="${serviceRequestList == null || serviceRequestList.size() <= 0 }">
    			<!-- nothing to do -->
    		</c:when>
    		<c:otherwise>
	    		<c:choose>
	    		<c:when test="${serviceRequestList.size() <= 0 }">
	    			<div class="alert alert-danger">
	    				No record found
	    			</div>
	    		</c:when>
	    		<c:otherwise>
	    			<div class="clearfix"></div>
						<div class="panel-content-wrapper">
	    			<div class="row">
						<div class="col-md-15 column">
			
							<div class="table-responsive">
								<table class="table table-striped" style="font-size: 12px">
									<thead>
										<tr>
											<th>SR Id</th>
											<th>SAPID</th>
											<th>Track ID</th>
											<th>Transaction Status</th>
											<th>Service Request Type</th>
											<th>Description</th>
											<th>Amount</th>
											<th>Payment Option</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="srBean" items="${ serviceRequestList }">
			
										<tr>
											<td><c:out value="${ srBean.id }" /></td>
											<td><c:out value="${ srBean.sapId }" /></td>
											<td><c:out value="${ srBean.trackId }" /></td>
											<td><c:out value="${ srBean.tranStatus }" /></td>
											<td><c:out value="${ srBean.serviceRequestType }" /></td>
											<td><c:out value="${ srBean.description }" /></td>
											<td><c:out value="${ srBean.amount }" /></td>
											<td><c:out value="${ srBean.paymentOption }" /></td>
										</tr>
										</c:forEach>
									
									</tbody>
								</table>
							</div>
						</div>
					</div>
					</div>
	    		</c:otherwise>
	    		</c:choose>
    		</c:otherwise>
    	</c:choose>

		

		

	</div>

	</section>

</body>
</html>