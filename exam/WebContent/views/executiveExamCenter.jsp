<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Exam Center" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <%@ include file="messages.jsp"%>
       <div class="row"><legend>Exam Center Details</legend></div>
		<div class="row clearfix">
			<div class="col-md-12 column">
				
				<form  action="editCompany" method="post">

				<h4>Exam Year: ${examCenter.year}</h4>
				<h4>Exam Month: ${examCenter.month}</h4>
				<h4>Exam Center Name: ${examCenter.examCenterName}</h4>
				<h4>Exam Locality: ${examCenter.locality}</h4>
				<h4 align="justify">Address: ${examCenter.address}</h4>
				<h4>City: ${examCenter.city}</h4>
				<h4 align="justify">State: ${examCenter.state}</h4>
				<h4 align="justify">Capacity: ${examCenter.capacity}</h4>
				<h4>Created By: ${examCenter.createdBy}</h4>
				<h4>Created Date: ${examCenter.createdDate}</h4>
				<h4>Last Modified By: ${examCenter.lastModifiedBy}</h4>
				<h4>Last Modified Date: ${examCenter.lastModifiedDate}</h4>
				
				
				<c:url value="editSASExamCenter" var="editurl">
				  <c:param name="centerId" value="${examCenter.centerId}" />
				</c:url>
				<c:url value="deleteSASExamCenter" var="deleteurl">
			
				     <c:param name="centerId" value="${examCenter.centerId}" />
				</c:url>
				<c:url value="viewExecutiveExamCenterSlots" var="SlotsdetailsUrl">
				  <c:param name="centerId" value="${examCenter.centerId}" />
				  <c:param name="year" value="${examCenter.year}" />
				  <c:param name="month" value="${examCenter.month}" />
				  
				</c:url>
				<!-- Button (Double) -->
				<div class="control-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
					
						<button id="edit" name="edit" class="btn btn-primary" formaction="${editurl}">Edit</button>
						<button id="edit" name="edit" class="btn btn-primary" formaction="${SlotsdetailsUrl}">View Generated Exam Center Slots</button>
						<button id="delete" name="delete" class="btn btn-danger" formaction="${deleteurl}" 
						onclick="return confirm('Are you sure you want to delete this Exam Center?')">Delete</button> 
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>

					
					</div>
				</div>
				</form>
			</div>
			
		</div><br/>
	</div>
</section>


<jsp:include page="footer.jsp" />

</body>
</html>
