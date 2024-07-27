<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Get Online Exam Passwords" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Get Siffy Online Exam Passwords</legend></div>
			
			<%@ include file="../messages.jsp"%>

			<div class="panel-body clearfix">
			<form:form  action="/exam/admin/onlineExamPasswordReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/onlineExamPasswordReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCount > 0}">
			<div class="panel-body">
				<h2>&nbsp;Siffy Online Exam Password Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadOnlineExamPasswordReport">Download to Excel</a></font></h2>
			</div>
		</c:if>
		</div>
			<!-- added for tcs report -->
		
				<div class="container-fluid customTheme">

			<div class="row"><legend>Get TCS Online Exam Password Reports</legend></div>
			
			<%if("true".equals( (String)request.getAttribute("tcsSuccess"))) { %>
				<div class="alert alert-success alert-dismissible">
					<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
					<%=((String)request.getAttribute("tcsSuccessMessage"))%>
				</div>
			<%} %>
			
			<%if("true".equals( (String)request.getAttribute("tcsError"))) { %>
				<div class="alert alert-danger alert-dismissible">
					<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
					<%=((String)request.getAttribute("tcsErrorMessage"))%>
				</div>
			<%} %>

			<div class="panel-body clearfix">
			<form:form  action="/exam/admin/tcsOnlineExamPasswordReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${monthList}" />

						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/tcsOnlineExamPasswordReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCountTcs > 0}">
			<div class="panel-body">
				<h2>&nbsp;TCS Online Exam Password Report<font size="2px"> (${rowCountTcs} Records Found) &nbsp; <a href="/exam/admin/downloadtcsOnlineExamPasswordReport">Download to Excel</a></font></h2>
			</div>
		</c:if>
		</div>
		
	
	<!-- added for executive students report -->
	<div class="container-fluid customTheme">

			<div class="row"><legend>Get Executive Exam Password Reports</legend></div>
	<div class="panel-body clearfix">
									
										<form:form  action="/exam/admin/onlineExamPasswordReport" method="post" 
										modelAttribute="studentMarks">
											<fieldset>
											<div class="col-md-6 column">

														<div class="form-group">
						<form:select id="year" path="year" type="text" required="required"	placeholder="Acad Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
    			
    			
    			<div class="form-group">
						<form:select id="month" path="month" type="text" required="required"	placeholder="Acad Month" class="form-control"   itemValue="${studentMarks.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
														
														<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
															formaction="/exam/admin/onlineExamPasswordReport">Generate</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
														</div>
													
											 </div>
										 </fieldset>
										</form:form>
										</div>
										<c:if test="${rowCountSAS > 0}">
											<legend>&nbsp;Executive Exam Bookings Report<font size="2px"> (${rowCountSAS} Records Found) &nbsp; <a href="/exam/admin/downloadExecutiveExamPassword">Download to Excel</a></font></legend>
										</c:if>
	
	</div>
									 
	</section>
	
	

	<jsp:include page="../footer.jsp" />


</body>
</html>
