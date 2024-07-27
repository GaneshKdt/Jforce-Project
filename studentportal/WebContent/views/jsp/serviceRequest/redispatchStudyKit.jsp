
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

							<form:form id="form1" action="saveRedispatchStudyKit"
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
											<form:hidden path="amount" value="300" />
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
													formaction="saveRedispatchStudyKit">Save & Proceed to
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
    			
    			$("#submit").click(function(){
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
    				ChangeFormAction('saveRedispatchStudyKit');
    			});
    			
    			/* $(document).on('click','#selectHdfc',function(){
    				$("#paymentOption").attr("value","hdfc");
    				ChangeFormAction('saveRedispatchStudyKit');
    			});
    			
    			$('#selectBilldesk').click(function(){
    				$("#paymentOption").attr("value","billdesk");
    				ChangeFormAction('saveRedispatchStudyKit');
    			});
    			
    			$('#selectPayu').click(function(){
    				$("#paymentOption").attr("value","payu");
    				ChangeFormAction('saveRedispatchStudyKit');
    			}); */
    	});
		var isCertificate = <%=isCertificate%>
		var totalCost = 300;
		if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;
	</script>
</html>