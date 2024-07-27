<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Confirm before Payment" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">

			

			<div class="row">
				<div class="col-sm-6">
				<legend>Confirm before Payment</legend>
				
				
				<div class="table-responsive">
			<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Subject</th>
					</tr>
				</thead>
				<tbody>

					<c:forEach var="subject" items="${subjects}"
						varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td><c:out value="${subject}" /></td>
						</tr>
					</c:forEach>


				</tbody>
			</table>
			</div>
			
				</div>
				<div class="col-sm-12">
				<legend>Payment</legend>
				<form:form action="goToGateway" method="post" >
					<fieldset>
						Number of Subjects: ${noOfSubjects} <br/>
						Exam Fees per Subject: ${examFeesPerSubject}/- INR<br/>
						Total Exam Fees: ${totalFees}/- INR<br/>
						

							
						<div class="col-sm-6">
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="goToGateway">Proceed to Payment</button>
									<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
							
						</div>
					</fieldset>
				</form:form>
				</div>
			</div>

			
			
		</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>
