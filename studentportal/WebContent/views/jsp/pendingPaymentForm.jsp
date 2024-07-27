<!DOCTYPE html>

<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%-- <%@page import="java.util.ArrayList"%> --%>

<%@page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="common/jscss.jsp">
	<jsp:param value="Adhoc Payment GateWay" name="title" />
</jsp:include>



<body>





	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Pending Payment" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">




				<div class="sz-content-wrapper examsPage">

					<div class="sz-content">

						<h2 class="red text-capitalize">Pending Payment</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%-- <%@ include file="messages.jsp" %> --%>
							<jsp:include page="common/messages.jsp" />

							<div class="clearfix"></div>
							<form:form action="saveAdhocPaymentRequest" method="post"
								modelAttribute="adhocPaymentBean" enctype="multipart/form-data">
								<fieldset>
									<form:hidden path="amount" id="amount"
										value="${adhocPaymentBean.amount}" />
									<div class="col-md-6 column">

										<div class="form-group">
											<form:select id="paymentType" path="paymentType"
												required="required" class="form-control" disabled="true">
												<form:option value="Exam Registration Fee"> Exam Registration Fee</form:option>
											</form:select>
										</div>

										<!-- <div class="form-group">
												<label for="emailId">EmailId</label>
													<form:input path="emailId" id="emailId" required="required" class="form-control" Placeholder="Enter Email Id"/>
												</div> -->

										<div class="form-group">
											<label for="description">Description</label>
											<form:input path="description" id="description"
												required="required" class="form-control"
												Placeholder="Description" />
										</div>

										<div class="form-group">
											<label for="amount">Amount</label>
											<form:input path="amount" value="${adhocPaymentBean.amount}"
												type="text" disabled="true" class="form-control" />
										</div>


										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="savePendingAmountRequest" class="form-control">Proceed</button>
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