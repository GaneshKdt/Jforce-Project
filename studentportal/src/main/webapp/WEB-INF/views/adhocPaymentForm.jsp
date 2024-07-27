<!DOCTYPE html>

<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="common/jscss.jsp">
	<jsp:param value="Adhoc Payment GateWay" name="title" />
</jsp:include>



<body>


	<%@ include file="common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Adhoc Online Payment"
				name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">




				<div class="sz-content-wrapper examsPage">

					<div class="sz-content">

						<h2 class="red text-capitalize">Adhoc Online Payment</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="messages.jsp"%>

							<div class="clearfix"></div>
							<form:form action="saveAdhocPaymentRequest" method="post"
								modelAttribute="adhocPaymentBean" enctype="multipart/form-data">
								<fieldset>
									<div class="col-md-6 column">

										<div class="form-group">
											<form:select id="paymentType" path="paymentType"
												required="required" class="form-control">
												<form:option value="">Select Payment Type</form:option>
												<form:options items="${paymentType}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:input path="sapId" id="sapId" required="required"
												class="form-control" Placeholder="Enter SAPID" />
										</div>

										<div class="form-group">
											<form:input path="emailId" id="emailId" required="required"
												class="form-control" Placeholder="Enter Email Id" />
										</div>

										<div class="form-group">
											<form:input path="mobile" id="mobile" required="required"
												class="form-control" Placeholder="Enter Mobile No" />
										</div>

										<div class="form-group">
											<form:input path="description" id="description"
												required="required" class="form-control"
												Placeholder="Description" />
										</div>

										<div class="form-group">
											<form:select id="year" path="year" class="form-control">
												<form:option value="">Select Exam Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="month" path="month" class="form-control">
												<form:option value="">Select Exam Month</form:option>
												<form:option value="April">April</form:option>
												<form:option value="Jun">Jun</form:option>
												<form:option value="Sept">Sept</form:option>
												<form:option value="Dec">Dec</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<form:input path="amount" id="amount" required="required"
												class="form-control" Placeholder="Enter Amount" />
										</div>


										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveAdhocPaymentRequest" class="form-control">Proceed</button>
												<button id="backToAdhocPayment" name="BackToAdhocPayment"
													class="btn btn-danger" formaction="home"
													formnovalidate="formnovalidate">Cancel</button>
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
	<jsp:include page="common/footer.jsp" />


</body>
</html>