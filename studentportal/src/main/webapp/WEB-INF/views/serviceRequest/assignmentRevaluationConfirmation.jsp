<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Assignment Revaluation Confirmation"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Assignment Revaluation Confirmation</legend></div>
        <form:form  action="saveAssignmentRevaluation" method="post" modelAttribute="sr" >
		<fieldset>
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Please confirm Subjects selected and Amount to be paid before proceeding for Payment. 
			</div>
			<br>
			
			<div class="col-md-18 column">
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType } for ${mostRecentResultPeriod } Exams
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges:</label>
					INR. ${sr.amount }/-
					<form:hidden path="amount"/>
					<form:hidden path="revaluationSubjects"/>
				</div>
				
								
				<div class="table-responsive">
				<table class="table table-striped" style="font-size: 12px">
					<thead>
						<tr>
							<th>Sr. No.</th>
							<th style="text-align:left;">Subject</th>
							<th>Sem</th>
							<th>Assignment Marks</th>
							<th>Low Score Reason</th>
						</tr>
					</thead>
					<tbody>

						<c:forEach var="studentMarks" items="${revaluationList}"
							varStatus="status">
							<tr>
								<td><c:out value="${status.count}" /></td>
								<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.assignmentscore}" /></td>
								<td><c:out value="${studentMarks.reason}" /></td>
							</tr>
						</c:forEach>

					</tbody>
				</table>
				</div>
					
						
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="saveAssignmentRevaluation" onClick="return confirm('Are you sure you want to proceed to Payment?');">Proceed to Payment</button>
						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
					
				</div>
				
				
							
				
		</div>
		</fieldset>
		</form:form>
	</div>
	
</section>
	
<jsp:include page="../footer.jsp" />

</body>
</html>
 --%>


<!DOCTYPE html>

<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>



<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Assignment Revaluation Confirmation" name="title" />
</jsp:include>

<style>
td {
	padding: 10px;
}

.selectCheckBox {
	width: 30px; /*Desired width*/
	height: 30px; /*Desired height*/
}

.red {
	color: red;
	font-size: 14px;
}

[type="checkbox"]:not (:checked ), [type="checkbox"]:checked {
	position: relative;
	left: 0px;
	opacity: 1;
}
</style>


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
						<h2 class="red text-capitalize">Assignment Revaluation
							Confirmation</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form:form action="saveAssignmentRevaluation" method="post"
								modelAttribute="sr">
								<fieldset>
									<div class="col-md-18 column">

										<div class="form-group">
											<form:label path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType }for ${mostRecentResultPeriod }
												Exams</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label>Charges:</label>
											<p>INR. ${sr.amount }/-</p>
											<form:hidden path="amount" />
											<form:hidden path="revaluationSubjects" />
										</div>


										<div class="table-responsive">
											<table class="table table-striped" style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th style="text-align: left;">Subject</th>
														<th>Sem</th>
														<th>Assignment Marks</th>
														<th>Low Score Reason</th>
													</tr>
												</thead>
												<tbody>

													<c:forEach var="studentMarks" items="${revaluationList}"
														varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td nowrap="nowrap" style="text-align: left;"><c:out
																	value="${studentMarks.subject}" /></td>
															<td><c:out value="${studentMarks.sem}" /></td>
															<td><c:out value="${studentMarks.assignmentscore}" /></td>
															<td><c:out value="${studentMarks.reason}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveAssignmentRevaluation"
													onClick="return confirm('Are you sure you want to proceed to Payment?');">Proceed
													to Payment</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="formnovalidate">Cancel</button>
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