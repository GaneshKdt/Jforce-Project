<!DOCTYPE html>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%-- <%@page import="java.util.ArrayList"%> --%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>

<%-- <%
  boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%> --%>
<!-- <style>
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
</style> -->

<body>
<jsp:include page="../common/header.jsp"/>
	<%-- <%@ include file="../common/header.jsp"%> --%>



	<div class="sz-main-content-wrapper">
	<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li>Single Book</li>
				</ul>
			</div>
		</div>
		<%-- <%@ include file="../common/breadcrum.jsp"%> --%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>



				<div class="sz-content-wrapper examsPage">
				<jsp:include page="../common/studentInfoBar.jsp"/>
				
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>


					<div class="sz-content">

						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
						<jsp:include page="../common/messageDemo.jsp"/>
						<%-- 	<%@ include file="../common/messages.jsp"%> --%>
							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>

							<form:form id="form1" action="saveSingleBook" method="post"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>
									<!-- payment gateway option  -->
									<!-- <input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" /> -->

									<div class="col-md-6 column">
										<div class="form-group">
											<form:label class="fw-bold" path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType }</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label class="fw-bold">Charges:</label>
											<div class="text-danger mb-3" id="showValue"></div>
											<form:hidden path="amount" value="500" />
										</div>

										<c:if test="${size > 0 }">

											<div class="form-group">
												<select name="subject" required="required"
													class="form-select">
													<option value="">Select Subject for Book</option>
													<c:forEach var="subject" items="${subjectList}"
														varStatus="status">
														<option value="${subject}">${subject}</option>
													</c:forEach>
												</select>
											</div>


											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button name="submit"
													class="btn btn-danger"
														formaction="saveSingleBook">Save & Proceed to Payment</button>
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-dark" formaction="selectSRForm"
														formnovalidate="formnovalidate">Back to New Service
														Request</button>
												</div>
											</div>
										</c:if>

										<c:if test="${size == 0 }">

											<div class="form-group">
												<label>No Failed/Current Session subjects for you.</label>
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-danger" formaction="selectSRForm"
														formnovalidate="formnovalidate">Back to New Service
														Request</button>

												</div>
											</div>
										</c:if>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../common/footerDemo.jsp" />
	<%-- <%@ include file="./paymentOption.jsp"%> --%>
</body>
<script type="text/javascript">
		var isCertificate = '${isCertificate}';
		var totalCost = 500;
		if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;
		
		$(document).ready(function(){
			function isEmptyData(data){
				if(data.trim() == "" || data == null){
					return true;
				}
				return false;
			}
			function ChangeFormAction(action){
				$('#form1').attr('action',action);
				$('#form1').submit();
			}
			$("#backToSR").click(function(){
				ChangeFormAction('selectSRForm');
			});
			
			$("#submit").click(function(){
				
				let sem = $("select[name='subject']").val();
				if(!isEmptyData(sem)){
					if (!confirm('Are you sure you want to submit this Service Request?')) return false;
					ChangeFormAction('saveSingleBook');
					return false;
				}
				alert("please select subject");
				
			});
			
			
/* 			$(document).on('click','.selectPaymentGateway',function(){
				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
				ChangeFormAction('saveSingleBook');
			}); */
			
			/* $('#selectPaytm').click(function(){
				$("#paymentOption").attr("value","paytm");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectHdfc').click(function(){
				$("#paymentOption").attr("value","hdfc");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectBilldesk').click(function(){
				$("#paymentOption").attr("value","billdesk");
				ChangeFormAction('saveSingleBook');
			});
			
			$('#selectPayu').click(function(){
				$("#paymentOption").attr("value","payu");
				ChangeFormAction('saveSingleBook');
			}); */
		});
</script>
<%-- <script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/singleBook.js"></script> --%>
</html>