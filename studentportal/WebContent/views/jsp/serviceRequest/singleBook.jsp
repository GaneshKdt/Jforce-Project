<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Enter Service Request Information"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Enter Service Request Information</legend></div>
        <form:form  action="saveSingleBook" method="post" modelAttribute="sr" >
			<fieldset>
			
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
			</div>
			<br>
			
			<div class="col-md-6 column">
			
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType }
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges:</label>
					INR. 500/-
					<form:hidden path="amount" value="500"/>
				</div>
				
				<c:if test="${size > 0 }">
				
					<div class="form-group">
					<select name="subject" required="required" class="form-control" >
						<option value="">Select Subject for Book</option>
						<c:forEach var="subject" items="${subjectList}" varStatus="status">
							<option value="${subject}">${subject}</option>
						</c:forEach>
					</select>
					</div>
				
				
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary" formaction="saveSingleBook" onClick="return confirm('Are you sure you want to proceed to Payment?');">Save & Proceed to Payment</button>
							<button id="cancel" name="cancel" class="btn btn-danger"
								formaction="home" formnovalidate="formnovalidate">Cancel</button>
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
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				</c:if>
					
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
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>

<%
  boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%>
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
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>



				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
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
											<form:label path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType }</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label>Charges:</label>
											<div id="showValue" style="color: red; size: 20px;"></div>
											<form:hidden path="amount" value="500" />
										</div>

										<c:if test="${size > 0 }">

											<div class="form-group">
												<select name="subject" required="required"
													class="form-control">
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
														class="btn btn-large btn-primary"
														formaction="saveSingleBook">Save & Proceed to Payment</button>
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-danger" formaction="selectSRForm"
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
	<jsp:include page="../common/footer.jsp" />
	<%-- <%@ include file="./paymentOption.jsp"%> --%>
</body>
<script type="text/javascript">
		var isCertificate = <%=isCertificate%>
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
</html>