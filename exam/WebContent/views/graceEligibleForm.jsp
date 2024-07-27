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
	<jsp:param value="Search Student Marks" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row"><legend>Search Student Eligible for Grace</legend></div>
			<%@ include file="messages.jsp"%>
			<div class="row clearfix">
				<form:form action="searchGrace" method="post"
					modelAttribute="studentMarks">
					<fieldset>
						<div class="col-md-6 column">
							<!--   -->


							<!-- Form Name -->


							<!-- Text input-->

							<div class="form-group">
								<form:select id="year" path="writtenYear" type="text" required="required" 
									placeholder="Written Exam Year" class="form-control"
									itemValue="${studentMarks.writtenYear}">
									<form:option value="">Written Exam Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="month" path="writtenMonth" type="text" required="required"
									placeholder="Written Exam Month" class="form-control"
									itemValue="${studentMarks.writtenMonth}">
									<form:option value="">Written Exam Month</form:option>
									<form:option value="Jan">Jan</form:option>
									<form:option value="Apr">Apr</form:option>
									<form:option value="May">May</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
								</form:select>
							</div>



							<!-- Button (Double) -->
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="searchGrace">Search</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>



						</div>



					</fieldset>
				</form:form>

			</div>
		</div>

	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
