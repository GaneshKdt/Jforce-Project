<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Copy Cases" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Copy Cases</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadExcelErrorMessages.jsp"%>
				<form:form modelAttribute="fileBean" method="post"
					enctype="multipart/form-data" action="uploadCopyCases">
					<div class="row">
					<div class="col-md-6 column">
					
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadCopyCases">Upload</button>
					</div>
					</div>
					
					
			</div>

			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
