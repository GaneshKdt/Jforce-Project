<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


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
        <form:form  action="saveDuplicateStudyKit" method="post" modelAttribute="sr" >
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
					INR. 3000/-
					<form:hidden path="amount" value="3000"/>
				</div>
				
				<div class="form-group">
				<select name="semester" required="required" class="form-control" >
					<option value="">Select Semester for Study Kit</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
				</select>
				</div>
						
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="saveDuplicateStudyKit" onClick="return confirm('Are you sure you want to proceed to Payment?');">Save & Proceed to Payment</button>
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
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>

<%
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%>


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

						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">

							<%@ include file="../common/messages.jsp"%>
							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>

							<form:form id="form1" action="saveDuplicateStudyKit"
								method="post" modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>

									<!-- payment gateway option  -->
									<input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" />

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
											<form:hidden path="amount" value="3000" />
										</div>

										<div class="form-group">
											<select name="semester" required="required"
												class="form-control">
												<option value="">Select Semester for Study Kit</option>
												<option value="1">1</option>
												<option value="2">2</option>
												<option value="3">3</option>
												<option value="4">4</option>
											</select>
										</div>

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<a id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveDuplicateStudyKit">Save & Proceed to
													Payment</a> <a id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-danger" formaction="selectSRForm"
													formnovalidate="formnovalidate">Back to New Service
													Request</a>
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
	<%@ include file="./paymentOption.jsp"%>


</body>
<script type="text/javascript">
    
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
    			
    			$('#submit').click(function(){
    				let sem = $("select[name='semester']").val();
    				if(!isEmptyData(sem)){
    					if (!confirm('Are you sure you want to submit this Service Request?')) return false;
       				 		$("#paymentGatewayOptions").modal();
    					return false;
    				}
    				alert("please select semester");
    				
    			});
    			
    			
    			$(document).on('click','.selectPaymentGateway',function(){
    				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
    				ChangeFormAction('saveDuplicateStudyKit');
    			});
    			
    			/* $(document).on('click','#selectPaytm',function(){
    				$("#paymentOption").attr("value","paytm");
    				ChangeFormAction('saveDuplicateStudyKit');
    			});
    			
    			$(document).on('click','#selectHdfc',function(){
    				$("#paymentOption").attr("value","hdfc");
    				ChangeFormAction('saveDuplicateStudyKit');
    			});
    			
    			$(document).on('click','#selectBilldesk',function(){
    				$("#paymentOption").attr("value","billdesk");
    				ChangeFormAction('saveDuplicateStudyKit');
    			});
    			
    			$(document).on('click','#selectPayu',function(){
    				$("#paymentOption").attr("value","payu");
    				ChangeFormAction('saveDuplicateStudyKit');
    			}); */
    	});
    	
    	
		var isCertificate = <%=isCertificate%>
		var totalCost = 3000;
		if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;
	</script>
</html>