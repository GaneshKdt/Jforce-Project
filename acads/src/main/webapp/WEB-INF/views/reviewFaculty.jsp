<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<jsp:include page="jscss.jsp">
	<jsp:param value="Review Faculty" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Review Faculty</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="panel-body">

				<form:form  action="saveReviewForFaculty" method="POST" modelAttribute="reviewBean">
					<input type="hidden" value="${reviewId}" name="reviewId"/>
					<input type="hidden" value="${action}" name="action"/>
					<form:input path="id" type="hidden"/>
					<form:input path="reviewerFacultyId" type="hidden"/>
					<div class="table-responsive">
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th width="5%">Sr. No.</th>
									<th width="35%">Review Point</th>
									<th width="20%">Response</th>
									<th width="40%">Review Remarks</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>1.</td>
									<td>Adhering to the Session plan</td>
									<td>
										<div class="form-group">
											<form:select  path="q1Response" class="makeEditable">
												<form:option value="">Select Option</form:option>
												<form:option value="Yes">Yes</form:option>
												<form:option value="No">No</form:option>
											</form:select>
										</div>
									</td>
									<td>
										<form:textarea   cols="50" rows="3" path="q1Remarks"
											class="form-control makeEditable"></form:textarea>
									</td>
								</tr>


								<tr>
									<td>2.</td>
									<td>Addressing Student queries</td>
									<td>
										<div class="form-group">
											<form:select name="q2Response" class="makeEditable" path="q2Response">
												<form:option value="">Select Option</form:option>
												<form:option value="Yes">Yes</form:option>
												<form:option value="No">No</form:option>
											</form:select>
										</div>
									</td>
									<td>
										<form:textarea path="q2Remarks" required="required" cols="50" rows="3"
											class="form-control makeEditable"></form:textarea>
									</td>
								</tr>

								<tr>
									<td>3.</td>
									<td>Aligning Case study with course content</td>
									<td>
										<div class="form-group">
											<form:select path="q3Response" class="makeEditable">
											    <form:option value="">Select Option</form:option>
												<form:option value="Yes">Yes</form:option>
												<form:option value="No">No</form:option>
											</form:select>
										</div>
									</td>
									<td>
										<form:textarea path="q3Remarks"  required="required" cols="50" rows="3"
											class="form-control makeEditable"></form:textarea>
									</td>
								</tr>


								<tr>
									<td>4.</td>
									<td>Lecture delivery (Poor/Needs
										Improvement/Good/Excellent)</td>
									<td>
										<div class="form-group">
											<form:select path="q4Response" class="makeEditable">
											    <form:option value="">Select Option</form:option>
												<form:option value="Poor">Poor</form:option>
												<form:option value="Needs Improvement">Needs
													Improvement</form:option>
												<form:option value="Good">Good</form:option>
												<form:option value="Excellent">Excellent</form:option>
											</form:select>
										</div>
								    </td>
								    
									<td>
										<form:textarea path="q4Remarks" required="required" cols="50" rows="3"
											class="form-control makeEditable"></form:textarea>
									</td>
								</tr>


								<tr>
									<td>5.</td>
									<td>Communication- Language (On a scale of 1-7)</td>
									<td>
										<div class="form-group">
											<form:select path="q5Response" class="makeEditable">
											    <form:option value="">Select Option</form:option>
												<c:forEach var="i" begin="1" end="7">
        												<form:option value="${i}">${i}</form:option>
												</c:forEach>
											</form:select>
										</div>
									</td>
									<td>
										<form:textarea path="q5Remarks" required="required" cols="50" rows="3"
											class="form-control makeEditable"></form:textarea>
									</td>
								</tr>


								<tr>
									<td>6.</td>
									<td>Communicaton- Clarity (On a scale of 1-7)</td>
									<td>
										<div class="form-group">
											<form:select path="q6Response" class="makeEditable">
											    <form:option value="">Select Option</form:option>
												<c:forEach var="i" begin="1" end="7">
        												<form:option value="${i}">${i}</form:option>
												</c:forEach>
											</form:select>
										</div>
									</td>
									<td>
										<form:textarea path="q6Remarks" required="required" cols="50" rows="3"
											class="form-control makeEditable"></form:textarea>
								   </td>
								</tr>



							</tbody>
						</table>
					</div>
					
					<c:if test="${action eq 'edit'}">
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="saveReviewForFaculty"
							>Submit Review</button>
						</div>
					</c:if>	
					<div class="form-group">
						<button id="cancel" name="cancel" class="btn btn-large btn-primary" formaction="viewReviewForFacultyForm" formnovalidate="formnovalidate"
						>Back To Session Review</button>
					</div>
				</form:form>
				<br>
			</div>

		</div>

	</section>

	<jsp:include page="footer.jsp" />

<script>
    $(document).ready(function(e){
    	var makeEditables = '${action}';
    	if(makeEditables =='view'){
    		$('.makeEditable').prop('required',false);
    		$('.makeEditable').prop('disabled',true);
    	}else{
    		$('.makeEditable').prop('required',true);
    		$('.makeEditable').prop('disabled',false);
    	}
    });
</script>
</body>
</html>
