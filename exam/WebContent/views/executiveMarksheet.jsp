<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.PassFailExamBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Generate Marsheets For Executive" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Generate Executive Marksheet</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<div class="panel-body clearfix">
				<form:form action="downloadExecutiveMarksheet" method="post"
					modelAttribute="bean">
					<fieldset>

						<div class="row">
							<div class="col-md-4 column">
								<div class="form-group">
									<form:select id="year" path="year" type="text"
										placeholder="Year on marksheet" class="form-control"
										required="required" itemValue="${bean.year}">
										<form:option value="">Exam Year on Marksheet</form:option>
										<form:options items="${yearList}" />
									</form:select>
								</div>
							</div>

							<div class="col-md-4 column">
								<div class="form-group">
									<form:select id="month" path="month" type="text"
										placeholder="Month on Marksheet" class="form-control"
										required="required" itemValue="${bean.month}">
										<form:option value="">Exam Month on Marksheet</form:option>
										<form:options items="${monthList}" />
									</form:select>
								</div>
							</div>

							<div class="col-md-4 column">
								<div class="form-group">
									<form:select id="program" path="program" type="text"
										placeholder="Select Program" class="form-control"
										required="required" itemValue="${bean.program}">
										<form:option value="">Select Program</form:option>
										<form:option value="EPBM">EPBM</form:option>
										<form:option value="MPDV">MPDV</form:option>
									</form:select>
								</div>
							</div>

						</div>

						<div class="row">
							<div class="col-md-4 column">
								<div class="form-group">
									<form:select id="batchYear" path="batchYear" type="text"
										placeholder="BatchYear" class="form-control"
										required="required" itemValue="${bean.batchYear}">
										<form:option value="">Batch Year</form:option>
										<form:options items="${yearList}" />
									</form:select>
								</div>
							</div>

							<div class="col-md-4 column">

								<div class="form-group">
									<form:select id="batchMonth" path="batchMonth" type="text"
										placeholder="Batch Month" class="form-control"
										required="required" itemValue="${bean.batchMonth}">
										<form:option value="">Batch Month</form:option>
										<form:options items="${monthList}" />
									</form:select>
								</div>

							</div>

							<div class="col-md-4 column">
								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="downloadExecutiveMarksheet" onclick = "return validate()">Generate</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>


						</div>

					</fieldset>
				</form:form>


				<%
					if ("true".equals((String) request.getAttribute("success"))) {
				%>
				<a href="${pageContext.request.contextPath}/admin/download">Download Marksheet</a>
				<%} %>


			</div>
		</div>



	</section>

	<jsp:include page="footer.jsp" />

<script>
function validate(){
	
	var program = document.getElementById('program').value;
	var month = document.getElementById('month').value;
	var year = document.getElementById('year').value;
	console.log(program);
	console.log(month);
	if("EPBM" == program && ("May" != month || "2019" != year) ){
		alert('Kindly select exam month as May and exam year as 2019');
		return false;
	}
	
	if("MPDV"== program && ("Aug"!= month || "2018" != year)){
		alert('Kindly select exam month as Aug and exam year as 2018');
		return false;
	}
	
	return true;
}

</script>
</body>
</html>
